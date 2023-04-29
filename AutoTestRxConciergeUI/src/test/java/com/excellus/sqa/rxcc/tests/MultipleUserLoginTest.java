/**
 *
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 *
 */
package com.excellus.sqa.rxcc.tests;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.remote.Browser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.IUserConfiguration;
import com.excellus.sqa.configuration.TestConfiguration;
import com.excellus.sqa.rxcc.configuration.RxConciergeBeanConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUIBrowser;
import com.excellus.sqa.rxcc.configuration.UserConfiguration;
import com.excellus.sqa.rxcc.pageobject.LoginPO;
import com.excellus.sqa.rxcc.pageobject.LogoutPO;
import com.excellus.sqa.selenium.BrowserSetup;
import com.excellus.sqa.selenium.DriverBase;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.EdgeConfiguration;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.LogInUIStep;
import com.excellus.sqa.selenium.step.LogOffUIStep;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.spring.SpringConfig;
import com.excellus.sqa.step.IStep.Status;
import com.excellus.sqa.test.TestBase;

/**
 * If you are getting the following error when running/debugging this test, please make sure that
 * you are NOT including -DTestUserRoleFilter argument in the VM arguments section of the Arguments
 * tab of the Run/Debug Configurations window:
 * org.opentest4j.TestAbortedException: User role provided [<SOME_ROLE>] is either invalid or no test is setup to run with the role. Please update jvm -DTestUserRoleFilter option to match supported user role annotations!

 * @author Garrett Cosmiano(gcosmian)
 * @since 08/04/2022
 */
@ExtendWith(RxConciergeUIBrowser.class)
@SpringConfig(classes = {RxConciergeBeanConfig.class, RxConciergeCosmoConfig.class})
@Tag("PERFORMANCE_ACCTS")
public class MultipleUserLoginTest extends TestBase
{

    private static final Logger logger = LoggerFactory.getLogger(MultipleUserLoginTest.class);

    PageConfiguration loginPage = BeanLoader.loadBean("loginPage", PageConfiguration.class);
    PageConfiguration logoutPage = BeanLoader.loadBean("homePage", PageConfiguration.class);
    PageConfiguration mainMenuPage = BeanLoader.loadBean("homePage", PageConfiguration.class);

    protected static DriverBase driverBase;

    @TestFactory
    public List<DynamicNode> multiplUserLogin() throws IOException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        if ( driverBase == null )
            driverBase = BrowserSetup.getDriverBase();

        Map<String, String> userToken = new HashMap<String, String>();

        List<UserConfiguration> users = UserConfiguration.getPerformanceTestAccts();

        for ( UserConfiguration user : users )
        {
            logger.info("Login as " + user.getName());

            List<DynamicNode> testUser = new ArrayList<DynamicNode>();

            TestConfiguration testConfiguration = new TestConfiguration(user,
                    RxConciergeBeanConfig.envConfig().getProperty(user.getName() + ".pwd"));

            try
            {
                /*
                 * Login
                 */

                LoginPO loginPO = new LoginPO(driverBase.getWebDriver(), loginPage, driverBase.getDevTools());
                LogInUIStep logInStep = new LogInUIStep(loginPO, testConfiguration);
                logInStep.run();
                testUser.addAll(logInStep.getTestResults());

                if ( logInStep.stepStatus() != Status.COMPLETED )
                {
                    tests.add(dynamicContainer(user.getName(), testUser));
                    throw logInStep.getStepException();
                }

                /*
                 * Grab the token
                 */
                String tokenAccess = loginPO.getTokenAccess();
                userToken.put(user.getName(), tokenAccess);

                /*
                 * Logout
                 */

                SeleniumPageHelperAndWaiter.pause(1000);
                LogoutPO logoutPO = new LogoutPO(driverBase.getWebDriver(), logoutPage, user.getUsername());

                LogOffUIStep logOffStep = new LogOffUIStep(logoutPO, false);
                logOffStep.run();
                testUser.addAll(logOffStep.getTestResults());

                if ( logOffStep.stepStatus() != Status.COMPLETED )
                {
                    tests.add(dynamicContainer(user.getName(), testUser));
                    throw logOffStep.getStepException();
                }
            }
            catch (Exception e)
            {
                logger.error("Something went wrong", e);

                SeleniumPageHelperAndWaiter.pause(1000);
                try
                {
                    driverBase.destroyWebDriver();
                }
                catch (Exception e1)
                {
                    logger.error("Unable to destroy the driver", e1);
                    driverBase.getWebDriver().quit();
                }

                SeleniumPageHelperAndWaiter.pause(1000);
                driverBase = new DriverBase(Browser.EDGE);
                EdgeConfiguration edgeConfig = BeanLoader.loadBean(EdgeConfiguration.class);
                driverBase.initWebDriver(edgeConfig);
                SeleniumPageHelperAndWaiter.pause(1000);
            }


            tests.add(dynamicContainer(user.getName(), testUser));

            SeleniumPageHelperAndWaiter.pause(1000);
        }

        logger.debug("Tokens:\n" + userToken);

        /*
         * Write the tokens in a file
         */
        StringBuilder sb = new StringBuilder();
        for(Entry<String, String> e : userToken.entrySet())
        {
            String key = e.getKey();
            String value = e.getValue();

            sb.append(key);
            sb.append(',');
            sb.append(value);
            sb.append(System.getProperty("line.separator"));
        }

        FileUtils.writeStringToFile(new File("../target/ui/token.csv"), sb.toString(), Charset.defaultCharset());
        return tests;
    }

    @Override
    public List<IUserConfiguration> convertUserToUserConfiguration() {
        return null;
    }


}

