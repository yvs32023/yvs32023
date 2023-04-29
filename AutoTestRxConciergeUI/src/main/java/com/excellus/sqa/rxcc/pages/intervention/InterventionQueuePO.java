/**
 * 
 * @copyright 2022 Excellus BCBS All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.pages.intervention;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.ContactInformation;
import com.excellus.sqa.rxcc.dto.MainMenu;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.intervention.DisplayColumns;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.selenium.DriverBase;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.spring.BeanLoader;


/**
 * 
 * 
 * @author Vijaya Sekhar Yeleswarapu (vyeleswa)
 * @since 12/28/2022
 */

public class InterventionQueuePO extends AbstractCommonPage {
  private static final Logger logger = LoggerFactory.getLogger(InterventionQueuePO.class);

  @FindBy(
      xpath = "//input[@placeholder='Search Intervention Queue' ][@type='text']")
  private WebElement textboxSearchIntervention;


  // webelement Slect a Tanent dropdown webelement

  @FindBy(
      xpath = "//span[contains(@class,'p-dropdown-label')]")
  private WebElement dropdownSelectATenant;

  // webelement Columns Selected text box
  @FindBy(xpath = "//div[contains(@class,'p-multiselect-label')]")
  private WebElement textboxMultiSelectColums;

  // WebEelement button veiw RPH Queue
  @FindBy(xpath = "//button[.='View RPH Queue']")
  private WebElement buttonViewRPHQueue;

  // WebEelement button veiw OC Queue
  @FindBy(xpath = "//button[.='View OC Queue']")
  private WebElement buttonViewOCQueue;

  // text filed select columns
  @FindBy(
      xpath = "//input[@role='textbox' and contains(@class,'p-multiselect-filter p-inputtext p-component')]")
  private WebElement textboxSelectColums;

  @FindBy(xpath = "//label[contains(normalize-space(),'Interventions')]")
  private WebElement labelSearchRecordsFound;

  @FindBy(xpath = "//table[contains(@class,'p-datatable-table')]")
  private WebElement tableInterventionsSearch;

  @FindBy(xpath = "//table[contains(@class,'p-datatable-table')]//tr")
  private WebElement tableInterventionsSearchHeaderRow;

  @FindBy(xpath = "//table[contains(@class,'p-datatable-table')]//tbody/tr")
  private List<WebElement> rowInterventions;
  
  @FindBy(xpath = "//*[@type='button'][@label='Reset All']")
  private WebElement buttonResetAll;

  // Date & Time column
  @FindBy(xpath = "//div/p-columnfilterformelement/div/p-calendar[1]/span/input")
  private WebElement textboxFilterStartDate;



  @FindBy(xpath = "//div/p-columnfilterformelement/div/p-calendar[2]/span/input")
  private WebElement textboxFilterEndDate;

  @FindBy(xpath = "//div[@class='flex justify-between h-full']")
  private WebElement textboxFilterDate;

  @FindBy(xpath = "//p-columnfilterformelement//button[@label='Clear']")
  private WebElement buttonFilterClear;



  // filter column Min Cost value - for Plan cost , member cost
  @FindBy(xpath = "//label[normalize-space()='Min Cost']/parent::div//input[contains(@class,'p-inputtext')][@inputmode='decimal']")

  private WebElement textboxFilterMinCost;


  // filter column Max Cost value - for Plan cost , member cost
  @FindBy(xpath = "//label[normalize-space()='Max Cost']/parent::div//input[contains(@class,'p-inputtext')][@inputmode='decimal']")
  private WebElement textboxFilterMaxCost;
  
  @FindBy(xpath = "/html/body/div[2]/div[1]/div/p-columnfilterformelement/div/p-multiselect/div/div[2]/div")
  private WebElement listStatusFilter;
  
  
  @FindBy(xpath = "//div[@class='p-checkbox-box']")
  private WebElement checkboxStatus;
  
  

  
  
  @FindBy(xpath = "//input[@role='textbox']")
  private WebElement textStatusFilter;



  // By
  private By checkboxAllXpath = By.xpath("//div[@role='checkbox']");

  private By columnHeadersXpath = By.xpath("//*[@id='pr_id_2-table']/thead/tr/rxc-filter-header");

  // Dynamic Xpath
  private String checkboxDisplayColumn =
      "//li[@aria-label='%s']//div[contains(@class,'p-checkbox-box')]";

  private String dropdownItemsXpath = "//li/span[contains(.,'%s')]";

  private String columnNamesXpath =
      "./td[count(//tr/th[contains(.,'%s')]/preceding-sibling::th)+1]";

  private String columnNamesSortIconXpath = "//span[contains(text(),'%s')]//p-sorticon/i";

  private final String SORT_ASCENDING = "pi-sort-amount-up-alt";

  private final String SORT_DESCENDING = "pi-sort-amount-down";

  private final String SORT_DEFAULT = "pi-sort-alt";

  private final String columnNamesFilterIconXpath =
      "//span[contains(text(),'%s')]/following-sibling::p-columnfilter/div/button";

  private final String columnNamesFilterInputXpath =
      "//p-columnfilterformelement//input[@type='text'][contains(@placeholder,'%s')]";

  private final String columnNamesFilterInputValueXpath =
      "//ul[@role='listbox']//li/span[contains(text(),'%s')]";
  
  
  
  
  



  /**
   * Constructor
   *
   * @param driver WebDriver for PageObject
   * @param page PageConfiguration for the UI page
   */
  public InterventionQueuePO(WebDriver driver, PageConfiguration page) {
    super(driver, page);
    waitForPageObjectToLoad();
  }



  @Override
  public void waitForPageObjectToLoad() {
    SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(driver,
        dropdownSelectATenant, getElementTimeout());
  }


  public List<String> getColumnHeaders() throws ElementNotFoundException {

    List<WebElement> webelementColumns = driver
        .findElements(columnHeadersXpath);

    List<String> columns = SeleniumPageHelperAndWaiter.retrieveWebElementsText(this,
        webelementColumns);


    return columns;
  }


  /**
   * Retrieve the MemberIntervention base on the row Interventions WebElements
   * 
   * @return list of MemberIntervention from the UI represented by {@link MemberIntervention}
   * @throws ElementNotFoundException if there is an issue
   */
  public List<MemberIntervention> retrieveMemberInterventionRows() throws ElementNotFoundException {
    List<MemberIntervention> dtos = new LinkedList<MemberIntervention>();

    List<String> columnNames = getColumnHeaders();


    for (WebElement row : rowInterventions) {
      MemberIntervention dto = new MemberIntervention();

      for (String columnName : columnNames) {

        String xpath = String.format(columnNamesXpath, columnName);
        By td = By.xpath(xpath);
        String value = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, td);

        switch (columnName) {
          case "Date & Time":
            dto.setQueueStatusChangeDateTime(value);
            break;
          case "Status":
            dto.setQueueStatusCode(value);
            break;
          case "Tenant Name":
            dto.setTargetProductName(value);
            break;
          case "RxCC Group Name":
            dto.setRxccGroupName(value);
            break;
          case "Plan Cost":
            dto.setPlanCost(Double.parseDouble(value));
            break;
          case "Member Cost":
            dto.setMemberCost(Double.parseDouble(value));
            break;
          case "Member Id":
            dto.setMemberId(value);
            break;
          case "Member Name":
            dto.setMemberName(value);
            break;
          case "Member DOB":
            dto.setMemberDateBirth(value);
            break;
          case "Target Drug Name":
            dto.setTargetProductName(value);
            break;
          case "Provider Name":
            dto.setOriginalProviderName(value);
            break;
          case "Provider Phone Number":
            ContactInformation contact = new ContactInformation();
            contact.setPhoneNumber(value);
            dto.setOriginalProviderContact(contact);
            break;
          case "Last Updated By":
            dto.setLastUpdatedBy(value);
            break;
          case "Username":
            break;

          default:
            break;
        }

      }

      dtos.add(dto);
    }

    return dtos;
  }



  /**
   * Set the field FilterStartDate Date WebElement
   * 
   * @param param value to set the textbox
   * @throws ElementNotFoundException if issue occurs
   * @throws InterruptedException
   */
  public void setFilterStartDate(String input) throws ElementNotFoundException {

    // SeleniumPageHelperAndWaiter.clickWebElement(driver, textboxFilterStartDate);
    SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxFilterStartDate, input);
    SeleniumPageHelperAndWaiter.pause(2000);

    SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxFilterStartDate,
        Keys.ENTER);

  }



  /**
   * Set the field FilterEndDate Date WebElement
   * 
   * @param param value to set the textbox
   * @throws ElementNotFoundException if issue occurs
   * @throws InterruptedException
   */
  public void setFilterEndDate(String input) throws ElementNotFoundException, InterruptedException {
    // SeleniumPageHelperAndWaiter.clickWebElement(driver, textboxFilterEndDate);
    SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxFilterEndDate, input);
    SeleniumPageHelperAndWaiter.pause(2000);

    driver.findElement(By.xpath("//div/p-columnfilterformelement/div/p-calendar[2]/span/input"))
        .sendKeys(Keys.ENTER);

    // SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxFilterEndDate,
    // Keys.ENTER);

    SeleniumPageHelperAndWaiter.pause(2000);
  }



  /**
   * Set the column Filter Name
   * 
   * @param param value to set the textbox
   * @throws ElementNotFoundException if issue occurs
   */
  public void setFilterColumnName(String inputPlaceHolder, String input) throws ElementNotFoundException {

    By locator = By.xpath(String.format(columnNamesFilterInputXpath, inputPlaceHolder));

    if (SeleniumPageHelperAndWaiter.isLocatorPresence(this,
        locator, true)) {

      SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, driver.findElement(locator),
          input,
          getElementTimeout());

    }

  }
  
  
  /**
   * Set the column Status multiselect 
   * 
   * @param param value to set the textbox
   * @throws ElementNotFoundException if issue occurs
   */
  public void setFilterStatus(String input) throws ElementNotFoundException {

    
    
    SeleniumPageHelperAndWaiter.clickWebElement(driver, listStatusFilter);

    
      SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textStatusFilter,
          input,
          getElementTimeout());
      
      
    

    

  }


  /**
   * Set the column Filter Min Cost
   * 
   * @param param value to set the textbox
   * @throws ElementNotFoundException if issue occurs
   */
  public void setFilterColumnMinCost(String inputMinCost) throws ElementNotFoundException {

    SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxFilterMinCost,
        inputMinCost,
        getElementTimeout());

  }



  /**
   * Set the column Filter Min Max Cost
   * 
   * @param param value to set the textbox
   * @throws ElementNotFoundException if issue occurs
   */
  public void setFilterColumnMinMaxCost(String inputMinCost, String inputMaxCost)
      throws ElementNotFoundException {

    SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxFilterMinCost,
        inputMinCost,
        getElementTimeout());

    SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxFilterMaxCost,
        inputMaxCost,
        getElementTimeout());

  }


  /**
   * Set the column Filter Max Cost
   * 
   * @param param value to set the textbox
   * @throws ElementNotFoundException if issue occurs
   */
  public void setFilterColumnMaxCost(String inputMaxCost) throws ElementNotFoundException {

    SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxFilterMaxCost,
        inputMaxCost,
        getElementTimeout());

  }


  /**
   * Retrieve the button WebElement Clear run time value
   * 
   * @throws ElementNotFoundException if issue occurs
   */
  public boolean isFilterClearDisabled() throws ElementNotFoundException {

    boolean flag = false;

    if (SeleniumPageHelperAndWaiter.retrieveAttribute(this, textboxFilterStartDate, "outerHTML")
        .contains("disabled")) {
      flag = true;
    }
    return flag;


  }



  /**
   * Clicks the button WebElement FilterClear
   * 
   * @throws ElementNotFoundException if issue occurs
   */
  public void clickFilterClear() throws ElementNotFoundException {

    if (!isFilterClearDisabled())
      SeleniumPageHelperAndWaiter.clickWebElement(this, buttonFilterClear);

  }


  /**
   * Retrieve the current value of the field FilterStartDate textbox
   * 
   * @author {@user}
   * @return String representation of the current value of the field FilterStartDate textbox
   * @throws ElementNotFoundException if issue occurs
   */
  public String retrieveFilterStartDate() throws ElementNotFoundException {
    return SeleniumPageHelperAndWaiter.retrieveAttribute(this, textboxFilterStartDate, "value");
  }


  public boolean isColumnSortAsc(String column) throws ElementNotFoundException {
    boolean flag = false;

    By locator = By.xpath(String.format(columnNamesSortIconXpath, column));

    if (SeleniumPageHelperAndWaiter.isLocatorPresence(this,
        By.xpath(String.format(columnNamesSortIconXpath, column)), true)) {

      String attrValue = SeleniumPageHelperAndWaiter.retrieveAttribute(this, driver.findElement(
          locator), "class");

      if ((attrValue.contains(SORT_ASCENDING))) {
        flag = true;

      }
    }
    return flag;

  }

  public boolean isColumnSortDesc(String column) throws ElementNotFoundException {
    boolean flag = false;

    By locator = By.xpath(String.format(columnNamesSortIconXpath, column));

    if (SeleniumPageHelperAndWaiter.isLocatorPresence(this,
        By.xpath(String.format(columnNamesSortIconXpath, column)), true)) {

      String attrValue = SeleniumPageHelperAndWaiter.retrieveAttribute(this, driver.findElement(
          locator), "class");

      if ((attrValue.contains(SORT_DESCENDING))) {
        flag = true;

      }
    }
    return flag;

  }

  public boolean isColumnSortDefault(String column) throws ElementNotFoundException {
    boolean flag = false;

    By locator = By.xpath(String.format(columnNamesSortIconXpath, column));

    if (SeleniumPageHelperAndWaiter.isLocatorPresence(this,
        By.xpath(String.format(columnNamesSortIconXpath, column)), true)) {

      String attrValue = SeleniumPageHelperAndWaiter.retrieveAttribute(this, driver.findElement(
          locator), "class");

      if ((attrValue.contains(SORT_DEFAULT))) {
        flag = true;

      }
    }
    return flag;

  }

  public void selectColumnSortAscending(String column) throws ElementNotFoundException {

    By locator = By.xpath(String.format(columnNamesSortIconXpath, column));


    if (!isColumnSortAsc(column) || isColumnSortDefault(column)) {

      SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, driver.findElement(locator),
          getElementTimeout());

    }

  }

  public void selectColumnSortDescending(String column) throws ElementNotFoundException {

    By locator = By.xpath(String.format(columnNamesSortIconXpath, column));


    if (!isColumnSortDesc(column) || isColumnSortDefault(column)) {

      SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, driver.findElement(locator),
          getElementTimeout());

    }

  }

  public boolean isTenantSelectionDiplayed(String tenant) {

    return SeleniumPageHelperAndWaiter.isLocatorPresence(this,
        By.xpath(String.format(dropdownItemsXpath, tenant)), true);
  }


  public void selectATenant(String tenant) throws ElementNotFoundException {

    By locator = By.xpath(String.format(dropdownItemsXpath, tenant));

    SeleniumPageHelperAndWaiter.clickWebElement(this,
        dropdownSelectATenant);

    if (isTenantSelectionDiplayed(tenant)) {

      SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, driver.findElement(locator),
          getElementTimeout());

    }

  }

  public void setDisplayColumns(List<String> columns) throws ElementNotFoundException {
    for (String column : columns) {

      setDisplayColumn(column);

    }
  }

  public void setDisplayColumn(DisplayColumns column) throws ElementNotFoundException {
    setDisplayColumn(column.toString());
  }

  public void setDisplayColumn(String column)
      throws ElementNotFoundException {
    By locator = By.xpath(String.format(checkboxDisplayColumn, column));
    SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, textboxMultiSelectColums,
        getElementRetry());

    SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxSelectColums, column,
        getElementTimeout());

    String attrValue = SeleniumPageHelperAndWaiter.retrieveAttribute(this, driver.findElement(
        locator), "class");

    if ((attrValue.contains("p-highlight"))) {

    } else {
      SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, driver.findElement(locator),
          getElementTimeout());

    }
    
    SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, textboxSearchIntervention,
        getElementTimeout());

//    clickResetAll(); // Using this for now instead of the textboxMultiSelectColums due to
                     // intermittent issue
  }

  /**
   * Set the field setAllColumnsSelected checkbox WebElement
   * 
   * @param status true to enable the checkbox otherwise false to disable checkbox
   * @throws ElementNotFoundException if issue occurs
   * @throws InterruptedException
   */
  public void setAllColumnsSelected(boolean flag)
      throws ElementNotFoundException, InterruptedException {

    SeleniumPageHelperAndWaiter.clickWebElement(driver,
        textboxMultiSelectColums);


    WebElement element = driver.findElement(checkboxAllXpath);
    if (flag) {
      if (!Boolean.parseBoolean(
          SeleniumPageHelperAndWaiter.retrieveAttribute(this,
              driver.findElement(checkboxAllXpath), "aria-checked")))
        // isPresentAndClick(checkboxAll);

        SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, element,
            getElementTimeout());


    } else {
      if (Boolean.parseBoolean(
          SeleniumPageHelperAndWaiter.retrieveAttribute(this,
              driver.findElement(checkboxAllXpath), "aria-checked")))
        // isPresentAndClick(checkboxAll);

        SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, element,
            getElementTimeout());
    }

  }

  /**
   * Clicks the button WebElement ViewRPHQueue
   * 
   * @throws ElementNotFoundException if issue occurs
   */
  public void clickViewRPHQueue() throws ElementNotFoundException {
    SeleniumPageHelperAndWaiter.clickWebElement(this, buttonViewRPHQueue);
  }

  /**
   * Clicks the button WebElement ViewRPHQueue
   * 
   * @throws ElementNotFoundException if issue occurs
   */
  public void clickViewOCQueue() throws ElementNotFoundException {
    SeleniumPageHelperAndWaiter.clickWebElement(this, buttonViewRPHQueue);
  }


  /**
   * Clicks the button WebElement column filter
   * 
   * @throws ElementNotFoundException if issue occurs
   */
  public void selectColumnFilter(String column) throws ElementNotFoundException {

    By locator = By.xpath(String.format(columnNamesFilterIconXpath, column));


    if (SeleniumPageHelperAndWaiter.isLocatorPresence(this,
        By.xpath(String.format(columnNamesFilterIconXpath, column)), true)) {

      SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, driver.findElement(locator),
          getElementTimeout());

    }

  }

  /**
   * Clicks the button WebElement column filter
   * 
   * @throws ElementNotFoundException if issue occurs
   */
  public void selectColumnFilterValue(String filter) throws ElementNotFoundException {

    By locator = By.xpath(String.format(columnNamesFilterInputValueXpath, filter));


    if (SeleniumPageHelperAndWaiter.isLocatorPresence(this,
        By.xpath(String.format(columnNamesFilterInputValueXpath, filter)), true)) {

      SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, driver.findElement(locator),
          getElementTimeout());

    }

  }

  /**
   * textboxSearchIntervention to set
   * 
   * @throws ElementNotFoundException if issue occurs
   */
  public void setSearchIntervention(String searchTerm) throws ElementNotFoundException {

    SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, textboxSearchIntervention,
        getElementTimeout());
    SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxSearchIntervention,
        searchTerm, getElementTimeout());

  }

  /**
   * Clicks the button WebElement ResetAll
   * 
   * @throws ElementNotFoundException if issue occurs
   */
  public void clickResetAll() throws ElementNotFoundException {
    SeleniumPageHelperAndWaiter.clickWebElement(this, buttonResetAll);
  }



  /**
   * Retrieve the row values based on column name provided to the method
   * 
   * @return list of MemberIntervention from the UI represented by {@link MemberIntervention}
   * @throws ElementNotFoundException if there is an issue
   */
  public List<String> retrieveMemberInterventionColValues(String columnName)
      throws ElementNotFoundException {
    List<String> rows = new ArrayList<String>();


    for (WebElement row : rowInterventions) {

      String xpath = String.format(columnNamesXpath, columnName);
      By td = By.xpath(xpath);
      String value = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, td);
      rows.add(value);
    }

    return rows;
  }
  
  /**
   * Select the row value based on column name provided to the method
   * 
   * @throws ElementNotFoundException if there is an issue
   */
  public void selectRow(int row)
      throws ElementNotFoundException {
    
    SeleniumPageHelperAndWaiter.pause(2000);
    
    
     
    int count = 0;
    for(WebElement element : rowInterventions) {
      
      if (row==count) {
        element.click();
        break;
      }
      
      count++;
    }
   
    SeleniumPageHelperAndWaiter.pause(10000);

//      Actions builder = new Actions(driver);
//      builder.keyDown(Keys.ALT).sendKeys(Keys.ARROW_LEFT).perform();
      
      PageConfiguration pageConfigurationHome = BeanLoader.loadBean(BeanNames.HOME_PAGE, PageConfiguration.class);
      

      MainMenuPO mainMenuPO = new MainMenuPO(driver,
          pageConfigurationHome);
      mainMenuPO.selectMenu(MainMenu.INTERVENTION_QUEUE);
      
         
  }


  public String getDisplayColumnNames() throws ElementNotFoundException {
    String columns = "";
    String rowText = SeleniumPageHelperAndWaiter.retrieveWebElementText(this,
        tableInterventionsSearchHeaderRow);
    if (!rowText.equals("")) {
      columns = rowText;
    }

    return columns;
  }



  /**
   * Retrieve the text of the field field label WebElement
   * 
   * @return String that represents the text of the label WebElement
   * @throws ElementNotFoundException if issue occurs
   */
  public boolean searchRecordsFound(String expectedNoRecords)
      throws ElementNotFoundException {


    boolean found = false;
    if (SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelSearchRecordsFound).contains(
        expectedNoRecords)) {
      found = true;
    }

    return found;

  }

  /**
   * Retrieve the text of the field field label WebElement
   * 
   * @return String that represents the text of the label WebElement
   * @throws ElementNotFoundException if issue occurs
   */
  public int getInterventionsRecordsCount()
      throws ElementNotFoundException {
    String expectedNoRecords = SeleniumPageHelperAndWaiter.retrieveWebElementText(this,
        labelSearchRecordsFound);
    StringTokenizer token = new StringTokenizer(expectedNoRecords, " Interventions");

    int noOfRecordsDisplayed = Integer.parseInt(token.nextToken());

    return noOfRecordsDisplayed;

  }



}
