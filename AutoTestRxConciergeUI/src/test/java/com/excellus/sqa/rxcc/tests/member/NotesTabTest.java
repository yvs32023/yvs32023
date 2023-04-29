/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.SortOrder;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeBeanConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.dto.Tenant.Type;
import com.excellus.sqa.rxcc.pages.member.MemberDetailsNotesPO;
import com.excellus.sqa.rxcc.steps.member.AlertMemberNoteStep;
import com.excellus.sqa.rxcc.steps.member.GetMemberNoteStep;
import com.excellus.sqa.rxcc.steps.member.InsertMemberNoteStep;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.steps.member.RetrieveMemberNoteStep;
import com.excellus.sqa.rxcc.steps.member.SearchMemberNoteStep;
import com.excellus.sqa.rxcc.workflows.member.AddNewMemberNoteWorkflow;
import com.excellus.sqa.rxcc.workflows.member.DeleteMemberNoteWorkflow;
import com.excellus.sqa.rxcc.workflows.member.DiscardMemberNoteWorkflow;
import com.excellus.sqa.rxcc.workflows.member.EditMemberNoteWorkflow;
import com.excellus.sqa.rxcc.workflows.member.RestoreMemberNoteWorkflow;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.excellus.sqa.workflow.IWorkflow.Mode;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 02/11/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI","RXCC_FULL_LOA", "RXCC_FULL_MED"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_NOTE")

public class NotesTabTest extends RxConciergeUITestBase{

    private static final Logger logger = LoggerFactory.getLogger(NotesTabTest.class);

    private List<MemberNote> toBeDeleted = new ArrayList<MemberNote>();

    private static Member[] members;

    //nt 02/15/2023
    static String memberlist;
    static String memberlistExe;
    static String memberlistEhp;

    //Declared variables
    String noteText = "Test Automation";
    /**
     * Setup the members to use
     */

    @BeforeAll
    public static void setupMemberIds()
    {
        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            memberlist =  MemberNoteQueries.getMemberWithNoteSizeLimit(Type.MED.getSubscriptionName());
            members = new Member[] { MemberQueries.getRandomNonDeceasedMemberUI(memberlist,Type.MED.getSubscriptionName())};
        }
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_LOA")) {
            memberlist =  MemberNoteQueries.getMemberWithNoteSizeLimit(Type.LOA.getSubscriptionName());       
            members = new Member[] { MemberQueries.getRandomNonDeceasedMemberUI(memberlist,Type.LOA.getSubscriptionName())};
        }
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) {
            memberlistEhp =  MemberNoteQueries.getMemberWithNoteSizeLimit(Type.EHP.getSubscriptionName());
            memberlistExe =  MemberNoteQueries.getMemberWithNoteSizeLimit(Type.EXE.getSubscriptionName());

            members = new Member[] { MemberQueries.getRandomNonDeceasedMemberUI(memberlistEhp,Type.EHP.getSubscriptionName()),
                    MemberQueries.getRandomNonDeceasedMemberUI(memberlistExe,Type.EXE.getSubscriptionName())};
        }
    }            

    /**
     * After each test the Home page will be loaded and perform data cleanup.
     * @throws ElementNotFoundException
     */
    @AfterEach
    public void dataCleanup() throws ElementNotFoundException 
    {
        logger.info("Starting the data cleanup");
        Set<String> memberIds = toBeDeleted.stream().map(MemberNote::getMemberId).collect(Collectors.toSet());

        for ( String memberId : memberIds )
        {
            List<MemberNote> testDatasToBeDeleted = toBeDeleted.stream()
                    .filter(memberNote -> memberId.equals(memberNote.getMemberId()))
                    .collect(Collectors.toList());

            List<MemberNote> cosmosNotes = MemberNoteQueries.getMemberNote(memberId);
            for ( MemberNote cosmosNote : cosmosNotes )
            {
                for ( MemberNote testDataToBeDeleted : testDatasToBeDeleted )
                {
                    if ( cosmosNote.getNote().contains(testDataToBeDeleted.getId()) )
                    {
                        MemberNoteQueries.deleteMemberNote(cosmosNote.getId(), testDataToBeDeleted.getMemberId());
                        break;
                    }

                }
            }
        }

        toBeDeleted.clear();

        // Reset browser to home page
        logger.info("Set to home page");
        driverBase.getWebDriver().get(RxConciergeBeanConfig.envConfig().getProperty("url.home"));
    }

    //This Test will add a new note to the user
    @TestFactory
    @Order(1)
    @DisplayName ("3317 RxCC Member Details Notes Tab - Add Note")
    public List<DynamicNode> addNote() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        for (Member member : members )
        {
            List<DynamicNode> theTest = new ArrayList<DynamicNode>(); // collection of test results of a member

            /*
             * Setup and create expected test data
             */

            // Create member note
            String noteTimeStamp = null;
            
     		try {
     			noteTimeStamp = DateTimeUtils.generateTimeStamp("MM/dd/yyyy");
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}
     		
            MemberNote expected = createNote("Add Note", member.getMemberId(), noteTimeStamp);

            /*
             * Add member note
             */

            AddNewMemberNoteWorkflow workflow = new AddNewMemberNoteWorkflow(expected, expected.getId(), Mode.TEST, driverBase.getWebDriver());
            workflow.run();

            theTest.addAll(workflow.workflowStepResults());
            theTest.addAll(workflow.workflowTestResults());

            tests.add(dynamicContainer(String.format("Member id %s", member.getMemberId()), theTest));

            // Setup data cleanup
            toBeDeleted.add(expected);
        }

        return tests;
    }

    //This Test will create a note and then search through the notes for that note
    @TestFactory
    @Order(2)
    @DisplayName ("3308 RxCC Member Details Notes Tab - Search Notes")
    public List<DynamicNode> searchNote() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        /*
         * Setup and create expected test data
         */

        Member member = members[0];

        // Insert 3 member note manually into Cosmos
        for (int i=0; i<3; i++)
        {
            String noteTimeStamp = null;
            
     		try {
     			noteTimeStamp = DateTimeUtils.generateTimeStamp("MM/dd/yyyy");
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}
     		
            MemberNote expected = createNote("Search Note", member.getMemberId(), noteTimeStamp);

            InsertMemberNoteStep insertMemberNoteStep = new InsertMemberNoteStep(expected);
            insertMemberNoteStep.run();
            tests.addAll(insertMemberNoteStep.getTestResults());

            if ( insertMemberNoteStep.stepStatus() != Status.COMPLETED )
                return tests;

            // Setup data cleanup
            toBeDeleted.add(expected);
        }

        /*
         * Open member web page
         */

        OpenMemberStep openMemberStep = new OpenMemberStep(driverBase.getWebDriver(), member.getMemberId());
        openMemberStep.run();
        tests.addAll(openMemberStep.getTestResults());

        // Collect the result if step didn't complete then stop and return the test results
        if ( openMemberStep.stepStatus() != Status.COMPLETED )
            return tests;

        /*
         * search note
         */

        MemberNote expected = toBeDeleted.get(0);
        SearchMemberNoteStep searchMemberNoteStep = new SearchMemberNoteStep(expected.getId(), driverBase.getWebDriver());
        searchMemberNoteStep.run();
        tests.addAll(searchMemberNoteStep.getTestResults());

        // Collect the result if step didn't complete then stop and return the test results
        if ( searchMemberNoteStep.stepStatus() != Status.COMPLETED )
            return tests;

        /*
         * retrieve note after search
         */

        RetrieveMemberNoteStep retrieveMemberNoteStep = new RetrieveMemberNoteStep(driverBase.getWebDriver(), expected.getId());
        retrieveMemberNoteStep.run();

        // Collect the result if step didn't complete then stop and return the test results
        if ( retrieveMemberNoteStep.stepStatus() != Status.COMPLETED )
            return tests;

        MemberNote actual = retrieveMemberNoteStep.getMemberNote();

        tests.add(dynamicContainer("Note Validation - memberId " + member.getMemberId() , expected.compareUI(actual)));

        return tests;
    }

    //This Test will edit an existing note
    @TestFactory
    @Order(3)
    @DisplayName ("3313 RxCC Member Details Notes Tab - Edit Note")
    public List<DynamicNode> editNote() throws ElementNotFoundException 
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();	// collection of all test results

        for (Member member : members )
        {
            List<DynamicNode> theTest = new ArrayList<DynamicNode>(); // collection of test results of a member

            /*
             * Setup and create expected test data
             */

            // Create member note
            String noteTimeStamp = null;
            
     		try {
     			noteTimeStamp = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT);
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}
     		
            MemberNote expected = createNote("Edit Note", member.getMemberId(), noteTimeStamp);

            // Insert member note manually into Cosmos
            InsertMemberNoteStep insertMemberNoteStep = new InsertMemberNoteStep(expected);
            insertMemberNoteStep.run();
            theTest.addAll(insertMemberNoteStep.getTestResults());

            // Collect the result if step didn't complete then move to the next member
            if ( insertMemberNoteStep.stepStatus() != Status.COMPLETED )
            {
                tests.add(dynamicContainer(String.format("Member Id %s", member.getMemberId()), theTest));
                continue;
            }

            // Setup data cleanup
            toBeDeleted.add(expected);

            // Update note for validation
     		try {
                expected.setLastUpdated(DateTimeUtils.generateTimeStamp("MM/dd/yyyy"));
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}

            expected.setLastUpdatedBy(RxConciergeUITestBase.acctName);
            
     		try {
                expected.setCreateDateTime(DateTimeUtils.generateTimeStamp("MM/dd/yyyy"));
                expected.setNote(expected.getNote() + " updated on " + DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT));
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}

            /*
             * Edit member note
             */

            EditMemberNoteWorkflow workflow = new EditMemberNoteWorkflow(expected, expected.getId(), Mode.TEST, driverBase.getWebDriver());
            workflow.run();

            theTest.addAll(workflow.workflowStepResults());
            theTest.addAll(workflow.workflowTestResults());

            tests.add(dynamicContainer(String.format("MemberId %s", member.getMemberId()), theTest));
        }

        return tests;
    }

    //This Test will add a new note but cancel the process
    @TestFactory 
    @Order(4)
    @DisplayName ("3310 RxCC Member Details Notes Tab - Cancel")
    public List<DynamicNode> cancelAddNote() throws ElementNotFoundException 
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();	// collection of all test results

        /*
         * Setup and create expected test data
         */

        Member member = members[0];

        // Create member note
        String noteTimeStamp = null;
        
 		try {
 			noteTimeStamp = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT);
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
 		
        MemberNote expected = createNote("Cancel creation of note", member.getMemberId(), noteTimeStamp);

        // Open member demographics
        OpenMemberStep openMemberStep = new OpenMemberStep(driverBase.getWebDriver(), member.getMemberId());
        openMemberStep.run();
        tests.addAll(openMemberStep.getTestResults());

        // Collect the result if step didn't complete then stop and return the test results
        if ( openMemberStep.stepStatus() != Status.COMPLETED )
            return tests;

        /*
         * Enter note but cancel it
         */

        MemberDetailsNotesPO notesPO = new MemberDetailsNotesPO(driverBase.getWebDriver(), BeanLoader.loadBean("memberPage", PageConfiguration.class));

        int countBefore = notesPO.getNumberOfNotes();	// get the number of notes

        notesPO.clickNotesTab();
        notesPO.clickNew();
        notesPO.setNote(expected.getNote());
        notesPO.clickCancel();

        int countAfter = notesPO.getNumberOfNotes();	// get the number of notes

        // Validation
        tests.add(dynamicTest("Number of note count after cancelling note [" + countBefore + "]", () -> assertEquals(countBefore, countAfter)));

        return tests;
    }

    //This Test will add a new note and then discard that note
    @TestFactory
    @Order(5)
    @DisplayName ("3314 RxCC Member Details Notes Tab - Delete Note")
    public List<DynamicNode> deleteNote() throws ElementNotFoundException 
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        for (Member member : members )
        {
            List<DynamicNode> theTest = new ArrayList<DynamicNode>(); // collection of test results of a member

            /*
             * Setup and create expected test data
             */

            // Create member note
            String noteTimeStamp = null;
            
     		try {
     			noteTimeStamp = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT);
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}
     		
            MemberNote expected = createNote("Delete Note", member.getMemberId(), noteTimeStamp);
            expected.setHidden(true);

            // Insert member note manually into Cosmos
            InsertMemberNoteStep insertMemberNoteStep = new InsertMemberNoteStep(expected);
            insertMemberNoteStep.run();
            theTest.addAll(insertMemberNoteStep.getTestResults());

            // Collect the result if step didn't complete then stop and return the test results
            if ( insertMemberNoteStep.stepStatus() != Status.COMPLETED )
            {
                tests.add(dynamicContainer("Member Id " + member.getMemberId(), theTest));
                continue;
            }

            // Setup data cleanup
            toBeDeleted.add(expected);

            /*
             * Delete member note
             */

            DeleteMemberNoteWorkflow workflow = new DeleteMemberNoteWorkflow(expected, expected.getId(), Mode.TEST, driverBase.getWebDriver());
            workflow.run();

            theTest.addAll(workflow.workflowStepResults());
            theTest.addAll(workflow.workflowTestResults());

            tests.add(dynamicContainer(String.format("Member Id %s",member.getMemberId()), theTest));
        }

        return tests;
    }

    //This Test will sort notes by content 
    @TestFactory
    @Order(6)
    @DisplayName ("3307 RxCC Member Details Notes Tab - Sort By")
    public List<DynamicNode> sortNotesByContent() throws ElementNotFoundException  
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        /*
         * Setup and create expected test data
         */

        Member member = members[0];

        // STEP 1 - Insert 3 member note manually into Cosmos
        for (int i=0; i<3; i++)
        {
            String noteTimeStamp = null;
            
     		try {
     			noteTimeStamp = DateTimeUtils.generateTimeStamp("MM/dd/yyyy");
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}
     		
            MemberNote expected = createNote("Sort Content", member.getMemberId(), noteTimeStamp);
            expected.setNote( RandomStringUtils.randomAlphanumeric(10) + " - " + expected.getNote() );

            InsertMemberNoteStep insertMemberNoteStep = new InsertMemberNoteStep(expected);
            insertMemberNoteStep.run();
            tests.addAll(insertMemberNoteStep.getTestResults());

            if ( insertMemberNoteStep.stepStatus() != Status.COMPLETED )
                return tests;

            // Setup data cleanup
            toBeDeleted.add(expected);
        }

        // STEP 2 - Open member demographics
        OpenMemberStep openMemberStep = new OpenMemberStep(driverBase.getWebDriver(), member.getMemberId());
        openMemberStep.run();
        tests.addAll(openMemberStep.getTestResults());

        // Collect the result if step didn't complete then stop and return the test results
        if ( openMemberStep.stepStatus() != Status.COMPLETED )
            return tests;

        // STEP 3 - Pull notes from cosmos db
        GetMemberNoteStep getMemberNoteStep = new GetMemberNoteStep(member.getMemberId());
        getMemberNoteStep.run();
        tests.addAll(openMemberStep.getTestResults());

        // Collect the result if step didn't complete then stop and return the test results
        if ( getMemberNoteStep.stepStatus() != Status.COMPLETED )
            return tests;

        List<MemberNote> expectedNotes = getMemberNoteStep.getMemberNotes().stream()
                .filter(note -> note.isAlert() == false)
                .collect(Collectors.toList());

        // Setup page object
        MemberDetailsNotesPO memberDetailsNotesPO = new MemberDetailsNotesPO(driverBase.getWebDriver(), BeanLoader.loadBean("memberPage", PageConfiguration.class));
        memberDetailsNotesPO.clickNotesTab();


        /*
         * Validate ascending order
         */

        List<String> ascExpected = expectedNotes.stream()
                .filter(note -> !note.isHidden())	// GC (03/31/22) do not include discard note
                .map(note -> note.getNote())
                .collect(Collectors.toList());

        ascExpected.sort(String.CASE_INSENSITIVE_ORDER);

        memberDetailsNotesPO.sortColumnContent(SortOrder.ASCENDING);
        List<String> ascActual = memberDetailsNotesPO.retrieveMemberNotes().stream()
                .map(note -> note.getNote())
                .collect(Collectors.toList());

        tests.add(dynamicTest("Ascending order", () -> assertThat(ascActual, contains(ascExpected.toArray(new String[ascExpected.size()]))) ));


        /*
         * Validate descending order
         */

        List<String> descExpected = expectedNotes.stream()
                .filter(note -> !note.isHidden())	// GC (03/31/22) do not include discard note
                .map(note -> note.getNote())
                .collect(Collectors.toList());

        descExpected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        memberDetailsNotesPO.sortColumnContent(SortOrder.DESCENDING);
        List<String> descActual = memberDetailsNotesPO.retrieveMemberNotes().stream()
                .map(note -> note.getNote())
                .collect(Collectors.toList());

        tests.add(dynamicTest("Descending order", () -> assertThat(descActual, contains(descExpected.toArray(new String[descExpected.size()]))) ));

        return Arrays.asList(dynamicContainer("Member id " + member.getMemberId(), tests));
    }

    //This Test will show previously discarded notes
    @TestFactory 
    @Order(7)
    @DisplayName ("3315 RxCC Member Details Notes Tab - Show Discarded")
    public List<DynamicNode> discardedNote() throws ElementNotFoundException  
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        for (Member member : members )
        {
            List<DynamicNode> theTest = new ArrayList<DynamicNode>(); // collection of test results of a member

            /*
             * Setup and create expected test data
             */

            // Create member note
            String noteTimeStamp = null;
            
     		try {
     			noteTimeStamp = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT);
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}
     		
            MemberNote expected = createNote("Discard Note", member.getMemberId(), noteTimeStamp);

            // Insert member note manually into Cosmos
            InsertMemberNoteStep insertMemberNoteStep = new InsertMemberNoteStep(expected);
            insertMemberNoteStep.run();
            theTest.addAll(insertMemberNoteStep.getTestResults());

            // Collect the result if step didn't complete then stop and return the test results
            if ( insertMemberNoteStep.stepStatus() != Status.COMPLETED )
            {
                tests.add(dynamicContainer("Member Id " + member.getMemberId(), theTest));
                continue;
            }

            // Setup data cleanup
            toBeDeleted.add(expected);

            // Update note after it is inserted into cosmos. This will be for validation.
     		try {
                expected.setLastUpdated(DateTimeUtils.generateTimeStamp("MM/dd/yyyy"));
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}

            expected.setLastUpdatedBy(RxConciergeUITestBase.acctName);
            expected.setHidden(true);
            
     		try {
                expected.setCreateDateTime(DateTimeUtils.generateTimeStamp("MM/dd/yyyy"));
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}
     		
            expected.setNote("Discarded\n" + expected.getNote() + "\nDiscarded");

            /*
             * Discard member note
             */

            DiscardMemberNoteWorkflow workflow = new DiscardMemberNoteWorkflow(expected, expected.getId(), Mode.TEST, driverBase.getWebDriver());
            workflow.run();

            theTest.addAll(workflow.workflowStepResults());
            theTest.addAll(workflow.workflowTestResults());

            tests.add(dynamicContainer(String.format("Member Id %s", member.getMemberId()), theTest));
        }

        return tests;
    }

    //This Test will restore a previously discarded note
    @TestFactory
    @Order(8)
    @DisplayName ("3312 RxCC Member Details Notes Tab - Restore")
    public List<DynamicNode> restoreNote() throws ElementNotFoundException  
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        for (Member member : members )
        {
            List<DynamicNode> theTest = new ArrayList<DynamicNode>(); // collection of test results of a member

            /*
             * Setup and create expected test data
             */

            // Create member note
            String noteTimeStamp = null;
            
     		try {
     			noteTimeStamp = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT);
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}
     		
            MemberNote expected = createNote("Restore Note", member.getMemberId(), noteTimeStamp);
            expected.setHidden(true);

            // Insert member note manually into Cosmos
            InsertMemberNoteStep insertMemberNoteStep = new InsertMemberNoteStep(expected);
            insertMemberNoteStep.run();
            theTest.addAll(insertMemberNoteStep.getTestResults());

            // Collect the result if step didn't complete then stop and return the test results
            if ( insertMemberNoteStep.stepStatus() != Status.COMPLETED )
            {
                tests.add(dynamicContainer("Member id " + member.getMemberId(), theTest));
                continue;
            }

            // Setup data cleanup
            toBeDeleted.add(expected);

            // Update note after it is inserted into cosmos. This will be for validation.
            expected.setHidden(false);
            
     		try {
                expected.setCreateDateTime(DateTimeUtils.generateTimeStamp("MM/dd/yyyy"));
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}

            // ntagore updates---------------------------------------------
     		try {
                expected.setLastUpdated(DateTimeUtils.generateTimeStamp("MM/dd/yyyy"));
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}

            expected.setLastUpdatedBy(RxConciergeUITestBase.acctName);

            /*
             * Restore discard member note
             */

            RestoreMemberNoteWorkflow workflow = new RestoreMemberNoteWorkflow(expected, expected.getId(), Mode.TEST, driverBase.getWebDriver());
            workflow.run();

            tests.addAll(workflow.workflowStepResults());
            tests.addAll(workflow.workflowTestResults());

            tests.add(dynamicContainer(String.format("Member Id %s", member.getMemberId()), theTest));
        }

        return tests;
    }

    //This Test will set a note as an alert and then check to see if the alert exists once user is searched again
    @TestFactory 
    @Order(9)
    @DisplayName ("3309 RxCC Member Details Notes Tab - Set Note As Alert")
    public List<DynamicNode> setNoteAlert() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        for (Member member : members )
        {
            List<DynamicNode> theTest = new ArrayList<DynamicNode>(); // collection of test results of a member

            /*
             * Setup and create expected test data
             */

            // Create member note
            String noteTimeStamp = null;
            
     		try {
     			noteTimeStamp = DateTimeUtils.generateTimeStamp("MM/dd/yyyy");
    		}
    		catch(Exception e) {
                logger.error("An unexpected error is caught while generating timestamp", e);
    		}
     		
            MemberNote expected = createNote("Alert Note", member.getMemberId(), noteTimeStamp);
            expected.setAlert(true);

            // Setup data cleanup
            toBeDeleted.add(expected);

            /*
             * Add member note
             */

            AddNewMemberNoteWorkflow workflow = new AddNewMemberNoteWorkflow(expected, expected.getId(), Mode.TEST, driverBase.getWebDriver());
            workflow.run();

            theTest.addAll(workflow.workflowStepResults());
            theTest.addAll(workflow.workflowTestResults());

            if ( workflow.workflowStatus() != Status.COMPLETED )
            {
                tests.add(dynamicContainer(String.format("Member Id %s", member.getMemberId()), theTest));
                continue;
            }


            /*
             * Validate alert display
             */

            AlertMemberNoteStep alertMemberNoteStep = new AlertMemberNoteStep(driverBase.getWebDriver(), expected.getMemberId());
            alertMemberNoteStep.run();

            if ( alertMemberNoteStep.stepStatus() != Status.COMPLETED )
            {
                tests.add(dynamicContainer("Member id " + member.getMemberId(), theTest));
                continue;
            }

            theTest.add(dynamicTest("Alert note display [" + expected.getNote() + "]", 
                    () -> assertEquals(expected.getNote(), alertMemberNoteStep.getAlertNote()) ));

            tests.add(dynamicContainer(String.format("Member Id %s", member.getMemberId()), theTest));

            // Setup data cleanup
            toBeDeleted.add(expected);
        }

        return tests;
    }

    /*
     * Helper method
     */

    /**
     * Create member note
     * 
     * @param testname to be added into note to indicate the test that added the note
     * @param memberId attached to note
     * @param uuid unique identifier
     * @return MemberNote
     */
    private MemberNote createNote(String testname, String memberId, String createdDate)
    {
        String uuid = UUID.randomUUID().toString();	// Create unique identifier for the note

        MemberNote expected = new MemberNote();
        expected.setMemberId(memberId);
        expected.setType("note");
        expected.setId(uuid);
        expected.setNote(noteText + " - " + testname + " - " + uuid);
        expected.setCreateUser(RxConciergeUITestBase.acctName);
        expected.setCreateDateTime(createdDate);

        return expected;
    }

}