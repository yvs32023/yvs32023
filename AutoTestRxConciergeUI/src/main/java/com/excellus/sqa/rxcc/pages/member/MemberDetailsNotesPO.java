/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.member;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SortOrder;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;


/**
 * GC (03/28/22) Refactor to fit best practices
 * 
 * @author Husnain Zia (hzia)
 * @since 02/11/2022
 */
public class MemberDetailsNotesPO extends AbstractCommonPage implements IPage 
{

	private static final Logger logger = LoggerFactory.getLogger(MemberDetailsNotesPO.class);

	//Declared Variables
	
	@FindBy(xpath = "//span[1][contains(text(),'Notes')]")
	private WebElement linkNotesTab;
	
	@FindBy(xpath = "//rxc-member-notes-table//div[@role='toolbar']")
	private WebElement toolbarNotes;
	
	private final String ROW_NOTE = "//rxc-member-notes-table//table[@role='table']/tbody/tr[./*[contains(.,'%s')]]";
	private final String EDIT_NOTE = ROW_NOTE + "//button[contains(., 'Edit')]";
	private final String DISCARD_NOTE = ROW_NOTE + "//button[contains(., 'Discard')]";
	private final String RESTORE_NOTE = ROW_NOTE + "//button[contains(., 'Restore')]";
	private final String DELETE_NOTE = ROW_NOTE + "//button[contains(., 'Delete')]";
	
	@FindBy(xpath = "//rxc-member-notes-table//button[@label='New' and .='New']")
	private WebElement buttonNew;
	
	private By rowNotes = By.xpath("//rxc-member-notes-table//table[@role='table']/tbody/tr");
	
	@FindBy(xpath = "//div[@role='dialog']/div[contains(., 'Add a New Note')]")
	private WebElement headerAddNewNote;
	
	@FindBy(xpath = "//div[@role='dialog']/div[contains(., 'Edit Note')]")
	private WebElement headerEditNote;
	
	@FindBy(xpath = "//div[@role='dialog']//textarea[following-sibling::label[.='Type Note: ']]")
	private WebElement textareaNote;
		
	@FindBy(xpath = "//button[@type='submit' and contains(text(),'Update')]")
	private WebElement buttonUpdate;
	
	@FindBy(xpath = "//div[@role='dialog']//button[@type='submit' and contains(., 'Add')]")
	private WebElement buttonAdd;
	
	@FindBy(xpath = "//rxc-member-notes-table//button[contains(., 'Edit')]")	// edits the first note
	private WebElement buttonEdit;
	
	@FindBy(xpath = "//p-dialog//button[@type='button' and contains(., 'Cancel')]")
	private WebElement buttonCancel;

	@FindBy(xpath = "//rxc-member-notes-table//button[contains(., 'Discard')]")
	private WebElement buttonDiscard;
	
	@FindBy(xpath = "//rxc-member-notes-table//span[contains(., 'Show Discarded')]/p-checkbox")
	private WebElement checkboxDiscarded;

	@FindBy(xpath = "//p-dialog[@header='Note']//button[span='Confirm']")
	private WebElement buttonConfirm;
	
	@FindBy(xpath = "//rxc-member-notes-table//button[contains(., 'Restore')]")	// restores the first note
	private WebElement buttonRestore;
	
	@FindBy(xpath = "//rxc-member-notes-table//button[contains(., 'Delete')]")	// delete the first note
	private WebElement buttonDelete;
	
	@FindBy(xpath = "//div[@role='dialog']//span[contains(@class, 'p-dialog-title') and .='Note']")
	private WebElement headerDialogConfirmCancel;
	
	@FindBy(xpath = "//tr[1]//td[2]//div//span[1]")
	private WebElement labelDiscardedText;
	
	@FindBy(xpath = "//p-dialog//label[contains(., 'Alert:')]/following-sibling::p-checkbox")
	private WebElement checkboxAlert;

	@FindBy(xpath = "//p-toastitem//div[contains(@class, 'p-toast-detail')]")
	private WebElement labelNoteAlert;
	
	private By buttonNoteAlertExit = By.xpath("//p-toastitem//button[contains(@class, 'p-toast-icon-close')]");
	
	@FindBy(xpath = "//rxc-member-notes-table//input[@placeholder='Search Notes']")
	private WebElement textboxSearch;
	
	@FindBy(xpath = "//rxc-member-notes-table//thead/tr/th[@psortablecolumn='note']")
	private WebElement columnContent;

	/**
	 * Constructor
	 *
	 * @param driver WebDriver for PageObject
	 * @param page   PageConfiguration for the UI page
	 */
	public MemberDetailsNotesPO(WebDriver driver, PageConfiguration page) 
	{
		super(driver, page);
		waitForPageObjectToLoad();
	}
	
	@Override
	public void waitForPageObjectToLoad() 
	{
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, linkNotesTab);
	}
	
	/**
	 * Retrieve the member note
	 * 
	 * @param uniqueIdentifier that identifies the note
	 * @return member note from the UI represented by {@link MemberNote}
	 * @throws ElementNotFoundException if there is an issue
	 */
	public MemberNote retrieveMemberNote(String uniqueIdentifier) throws ElementNotFoundException
	{
		By rowNoteLocator = By.xpath( String.format(ROW_NOTE, uniqueIdentifier) );
		
		if ( SeleniumPageHelperAndWaiter.isLocatorPresence(this, rowNoteLocator, 3) )
		{
			WebElement rowNoteElement = driver.findElement(rowNoteLocator);
			
			return retrieveMemberNote(rowNoteElement);
		}
		
		return null;
	}
	
	/**
	 * Retrieve the first note
	 * @return member note from the UI represented by {@link MemberNote}
	 * @throws ElementNotFoundException if there is an issue
	 */
	public MemberNote retrieveMemberNote() throws ElementNotFoundException
	{
		return retrieveMemberNote(driver.findElement(rowNotes));
	}
	
	/**
	 * Retrieve all member notes
	 * 
	 * @return list notes
	 * @throws ElementNotFoundException if there is an issue
	 */
	public List<MemberNote> retrieveMemberNotes() throws ElementNotFoundException
	{
		List<MemberNote> notes = new LinkedList<MemberNote>();
		List<WebElement> rows = driver.findElements(rowNotes);
		
		for ( WebElement row : rows )
		{
			MemberNote note = retrieveMemberNote(row);
			notes.add(note);
		}
		
		return notes;
	}
	
	/**
	 * Retrieve the member note base on the row WebElement of the note
	 * 
	 * @param rowNoteElement that correspond to the note
	 * @return member note from the UI represented by {@link MemberNote}
	 * @throws ElementNotFoundException if there is an issue
	 */
	private MemberNote retrieveMemberNote(WebElement rowNoteElement) throws ElementNotFoundException
	{
		MemberNote note = new MemberNote();
		
		try {
			if ( SeleniumPageHelperAndWaiter.isWebElementVisible(driver, rowNoteElement.findElement(By.xpath("./td[1]/i")), 1) )
				note.setAlert(true);
		}
		catch (Exception e) {
			// do nothing
		}
		
		String temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, rowNoteElement, By.xpath("./td[2]"));
		note.setNote(temp);
		
		if ( StringUtils.startsWith(temp, "Discarded") && StringUtils.endsWith(temp, "Discarded") )
		{
			note.setHidden(true);
		}
		
		temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, rowNoteElement, By.xpath("./td[3]"));
		if ( StringUtils.isNoneBlank(temp) )
		{
			String[] temp2 = temp.split("\n");
			note.setLastUpdatedBy(temp2[0]);
			note.setLastUpdated(temp2[1]);
		}
		
		temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, rowNoteElement, By.xpath("./td[4]"));
		if ( StringUtils.isNoneBlank(temp) )
		{
			String[] temp2 = temp.split("\n");
			note.setCreateUser(temp2[0]);
			note.setCreateDateTime(temp2[1]);
		}
		
		return note;
	}
	
	/**
	 * Add a new note
	 * 
	 * @param memberNote to be added
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void addNewNote(MemberNote memberNote) throws ElementNotFoundException
	{
		int numNotes = driver.findElements(rowNotes).size();	// get the current number of notes
		
		clickNew();
		
		setNote(memberNote.getNote());
		
		if ( memberNote.isAlert() )
			this.setAlert(true);
		
		clickAdd();
		
		SeleniumPageHelperAndWaiter.waitForNumberOfElementsToBeMoreThan(this, rowNotes, numNotes, 2);	// wait for the number of notes to increase
	}
	
	/**
	 * Retrieve the number of notes for the member
	 * 
	 * @return number of notes
	 */
	public int getNumberOfNotes()
	{
		return driver.findElements(rowNotes).size();
	}
	
	/**
	 * Update the first member's note
	 * 
	 * @param updateNote to be update
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void editNote(String updateNote) throws ElementNotFoundException
	{
		clickEdit();
		setNote(updateNote);
		clickUpdate();
		
		SeleniumPageHelperAndWaiter.pause(500);	// wait for note to be updated to avoid stale element exception
	}
	
	/**
	 * Update the member's note that contains note identifier
	 * 
	 * @param uniqueNoteIdentifier of a note
	 * @param updateNote to be update
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void editNote(String uniqueNoteIdentifier, String updateNote) throws ElementNotFoundException
	{
		clickEdit(uniqueNoteIdentifier);
		setNote(updateNote);
		clickUpdate();
		
		SeleniumPageHelperAndWaiter.pause(500);	// wait for note to be updated to avoid stale element exception
	}
	
	/**
	 * Discard the member's note that contains note identifier
	 * 
	 * @param uniqueNoteIdentifier to be discard
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void discardNote(String uniqueNoteIdentifier) throws ElementNotFoundException
	{
		clickDiscard(uniqueNoteIdentifier);
		clickConfirm();
		
		SeleniumPageHelperAndWaiter.pause(500);	// wait for note to be updated to avoid stale element exception
	}
	
	/**
	 * Discard the first member's note
	 * 
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void discardNote() throws ElementNotFoundException
	{
		clickDiscard();
		clickConfirm();
		
		SeleniumPageHelperAndWaiter.pause(500);	// wait for note to be updated to avoid stale element exception
	}
	
	/**
	 * Restore the member's note that contains note identifier
	 * 
	 * @param uniqueNoteIdentifier to be discard
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void restoreNote(String uniqueNoteIdentifier) throws ElementNotFoundException
	{
		setDiscarded(true);
		clickRestore(uniqueNoteIdentifier);
		clickConfirm();
		
		SeleniumPageHelperAndWaiter.pause(500);	// wait for note to be updated to avoid stale element exception
	}
	
	/**
	 * Restore the first member's note
	 * 
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void restoreNote() throws ElementNotFoundException
	{
		setDiscarded(true);
		clickRestore();
		clickConfirm();
		
		SeleniumPageHelperAndWaiter.pause(500);	// wait for note to be updated to avoid stale element exception
	}
	
	/**
	 * Delete the member's note that contains note identifier
	 * 
	 * @param uniqueNoteIdentifier to be discard
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void deleteNote(String uniqueNoteIdentifier) throws ElementNotFoundException
	{
		setDiscarded(true);
		clickDelete(uniqueNoteIdentifier);
		clickConfirm();
		
		SeleniumPageHelperAndWaiter.pause(500);	// wait for note to be updated to avoid stale element exception
	}
	
	/**
	 * Delete the first member's note
	 * 
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void deleteNote() throws ElementNotFoundException
	{
		setDiscarded(true);
		clickDelete();
		clickConfirm();		

		SeleniumPageHelperAndWaiter.pause(500);	// wait for note to be updated to avoid stale element exception
	}
			
	/**
	 * Clicks the link WebElement NotesTab
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickNotesTab() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, linkNotesTab);
	}

	/**
	 * Get href attribute from link WebElement NotesTab
	 * @throws ElementNotFoundException if issue occurs
	 */
	public String getLinkHrefNotesTab() throws ElementNotFoundException {
		return SeleniumPageHelperAndWaiter.retrieveAttribute(this, linkNotesTab, "href");
	}

	/**
	 * Determine if link WebElement NotesTab is present and displayed
	 * @throws ElementNotFoundException if issue occurs
	 */
	public boolean isLinkNotesTabPresent() throws ElementNotFoundException {
		try {
			return SeleniumPageHelperAndWaiter.isWebElementVisible(driver, linkNotesTab, 1);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Clicks the button WebElement New
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickNew() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, buttonNew);
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonNew);
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerAddNewNote);
	}
	
	/**
	 * Set the field Note textarea WebElement 
	 * @param note value to set the textarea
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void setNote(String note) throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.pause(500);	// GC (03/31/22) avoid intermittent ElementClickInterceptedException
		SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textareaNote, note);
	}

	/**
	 * Retrieve the current value of the field Note textarea
	 * @return String representation of the current value of the field Note textarea
	 * @throws ElementNotFoundException if issue occurs
	 */
	public String retrieveNote() throws ElementNotFoundException {
		return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, textareaNote);
	}
	
	public void updateNoteText(String editNoteText) throws ElementNotFoundException 
	{
		SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textareaNote, editNoteText);
	}
	
	/**
	 * Clicks the button WebElement Add
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickAdd() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonAdd);
	}
	
	/**
	 * Clicks the button WebElement Update
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickUpdate() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonUpdate);
	}
	
	/**
	 * Clicks the button WebElement Edit
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickEdit() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonEdit);
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerEditNote);
	}
	
	/**
	 * Clicks the button WebElement Edit given a unique identifier of a note
	 * 
	 * @param uniqueNoteIdentifier of a note
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickEdit(String uniqueNoteIdentifier) throws ElementNotFoundException {
		By locator = By.xpath( String.format(EDIT_NOTE, uniqueNoteIdentifier) );
		SeleniumPageHelperAndWaiter.clickBy(this, locator);
	}
	
	/**
	 * Clicks the button WebElement Cancel
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickCancel() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonCancel);
	}
	
	/**
	 * Clicks the button WebElement Discard
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickDiscard() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonDiscard);
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerDialogConfirmCancel);
	}
	
	/**
	 * Clicks the button WebElement Discard given a unique identifier of a note
	 * 
	 * @param uniqueNoteIdentifier of a note
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickDiscard(String uniqueNoteIdentifier) throws ElementNotFoundException {
		By locator = By.xpath( String.format(DISCARD_NOTE, uniqueNoteIdentifier) );
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, locator);
		SeleniumPageHelperAndWaiter.clickBy(this, locator);
		
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerDialogConfirmCancel);
	}
	
	/**
	 * Clicks the button WebElement Confirm
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickConfirm() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonConfirm);
	}
	
	/**
	 * Set the field Discarded checkbox WebElement
	 * @param status true to enable the checkbox otherwise false to disable checkbox
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void setDiscarded(boolean status) throws ElementNotFoundException {
		
		boolean checkboxState = SeleniumPageHelperAndWaiter.isCheckboxEnable(driver, checkboxDiscarded.findElement(By.xpath(".//input")));
		
		if ( status != checkboxState )
		{
			SeleniumPageHelperAndWaiter.clickWebElement(this, checkboxDiscarded);
			SeleniumPageHelperAndWaiter.pause(500); // to avoid stale element exception
		}
	}

	/**
	 * Retrieve the current setting for the field Discarded checkbox WebElement
	 * @return true if the checkbox is enabled otherwise return false
	 */
	public boolean isDiscarded() {
		return SeleniumPageHelperAndWaiter.isCheckboxEnable(driver, checkboxDiscarded.findElement(By.xpath(".//input")));
	}
	
	/**
	 * Clicks the button WebElement Restore
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickRestore() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonRestore);
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerDialogConfirmCancel);
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerDialogConfirmCancel);
	}
	
	/**
	 * Clicks the button WebElement Restore given a unique identifier of a note
	 * 
	 * @param uniqueNoteIdentifier of a note
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickRestore(String uniqueNoteIdentifier) throws ElementNotFoundException {
		By locator = By.xpath( String.format(RESTORE_NOTE, uniqueNoteIdentifier) );
		SeleniumPageHelperAndWaiter.clickBy(this, locator);
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerDialogConfirmCancel);
	}

	/**
	 * Clicks the button WebElement Delete
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickDelete() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonDelete);
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerDialogConfirmCancel);
	}
	
	/**
	 * Clicks the button WebElement Delete given a unique identifier of a note
	 * 
	 * @param uniqueNoteIdentifier of a note
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void clickDelete(String uniqueNoteIdentifier) throws ElementNotFoundException {
		By locator = By.xpath( String.format(DELETE_NOTE, uniqueNoteIdentifier) );
		SeleniumPageHelperAndWaiter.clickBy(this, locator);
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerDialogConfirmCancel);
	}
	
	/**
	 * Set the field Alert checkbox WebElement
	 * @param status true to enable the checkbox otherwise false to disable checkbox
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void setAlert(boolean status) throws ElementNotFoundException {
		
		boolean checkboxStatus = SeleniumPageHelperAndWaiter.isCheckboxEnable(driver, checkboxAlert.findElement(By.xpath(".//input")));
		
		if ( status != checkboxStatus )
			SeleniumPageHelperAndWaiter.clickWebElement(this, checkboxAlert);
	}

	/**
	 * Retrieve the current setting for the field Alert checkbox WebElement
	 * @return true if the checkbox is enabled otherwise return false
	 */
	public boolean isAlert() {
		return SeleniumPageHelperAndWaiter.isCheckboxEnable(driver, checkboxAlert.findElement(By.xpath(".//input")));
	}

	/**
	 * Retrieve the text of the field NoteAlert label WebElement
	 * @return String that represents the text of the label WebElement
	 * @throws ElementNotFoundException if issue occurs
	 */
	public String retrieveNoteAlert() throws ElementNotFoundException {
        clickNotesTab();
        SeleniumPageHelperAndWaiter.pause(2000);
		return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelNoteAlert);
		
	}

	/**
	 * Clicks the button WebElement NoteAlertExit
	 */
	public void clickNoteAlertExit() 
	{
		int alertCount = driver.findElements(buttonNoteAlertExit).size();
		
		try {
			// GC (03/30/22) handle multiple alerts
			for ( int i=0; i<alertCount; i++ )
			{
				SeleniumPageHelperAndWaiter.clickBy(this, buttonNoteAlertExit);
				SeleniumPageHelperAndWaiter.pause(100);
			}
		}
		catch (Exception e) {
			// do nothing - it is possible the alert no longer exist or it never existed
		}
	}
	
	/**
	 * Set the field Search textbox WebElement 
	 * @param param value to set the textbox
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void setSearch(String param) throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxSearch, param);
	}

	/**
	 * Retrieve the current value of the field Search textbox
	 * @author {@user}
	 * @return String representation of the current value of the field Search textbox
	 * @throws ElementNotFoundException if issue occurs
	 */
	public String retrieveSearch() throws ElementNotFoundException {
		return SeleniumPageHelperAndWaiter.retrieveAttribute(this, textboxSearch, "value");
	}
	
	/**
	 * Sort the column content
	 * 
	 * @param order for sorting
	 * @throws ElementNotFoundException if there is an issue
	 */
	public void sortColumnContent(SortOrder order) throws ElementNotFoundException 
	{
		String currentOrder = SeleniumPageHelperAndWaiter.retrieveAttribute(this, columnContent, "aria-sort");
		
		if ( order == SortOrder.ASCENDING )
		{
			if ( !StringUtils.equalsIgnoreCase(SortOrder.DESCENDING.name(), currentOrder) )
			{
				SeleniumPageHelperAndWaiter.clickWebElement(this, columnContent);
			}
		}
		else if ( order == SortOrder.DESCENDING )
		{
			if ( StringUtils.equalsIgnoreCase("none", currentOrder) )
			{
				SeleniumPageHelperAndWaiter.clickWebElement(this, columnContent);
				SeleniumPageHelperAndWaiter.pause(500);
				SeleniumPageHelperAndWaiter.clickWebElement(this, columnContent);
			}
			else if ( StringUtils.equalsIgnoreCase(SortOrder.ASCENDING.name(), currentOrder) )
			{
				SeleniumPageHelperAndWaiter.clickWebElement(this, columnContent);
			}	
		}

		SeleniumPageHelperAndWaiter.pause(500);
	}
}

