/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.pages.member.MemberDetailsNotesPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Retrieves the member note from the UI
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/28/2022
 */
public class RetrieveMemberNoteStep extends AbstractUIStep {

	private static final Logger logger = LoggerFactory.getLogger(RetrieveMemberNoteStep.class);
	
	private final static String STEP_NAME = "RetrieveMemberNote";
	private final static String STEP_DESC = "Retrieve member note";
	
	/*
	 * If set then the step will look for this in the note. 
	 * Otherwise it will retrieve the first note in the table
	 */
	private String uniqueNoteIdentifier;
	
	private List<MemberNote> memberNotes;
	private boolean discard = false;	// denotes if retrieving discarded note or not 

	/**
	 * Constructor - use this if retrieving all notes from the member notes table
	 * 
	 * @param driver {@link WebElement}
	 */
	public RetrieveMemberNoteStep(WebDriver driver) 
	{
		super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
	}

	/**
	 * Constructor - use this if retrieving a specific note
	 * 
	 * @param driver {@link WebElement}
	 * @param uniqueNoteIdentifier of the note
	 */
	public RetrieveMemberNoteStep(WebDriver driver, String uniqueNoteIdentifier) 
	{
		super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
		this.uniqueNoteIdentifier = uniqueNoteIdentifier;
	}
	
	/**
	 * Constructor - use this if retrieving a specific note from discard notes
	 * 
	 * @param driver {@link WebElement}
	 * @param uniqueNoteIdentifier of the note
	 * @param discard if true then retrieve discard notes otherwise retrieve non-discard notes
	 */
	public RetrieveMemberNoteStep(WebDriver driver, String uniqueNoteIdentifier, boolean discard) 
	{
		super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
		this.uniqueNoteIdentifier = uniqueNoteIdentifier;
		this.discard = discard;
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());
		
		try
		{
			MemberDetailsNotesPO memberNotesPO = new MemberDetailsNotesPO(driver, pageConfiguration);
			
			memberNotesPO.clickNotesTab();
			memberNotesPO.setDiscarded(discard);
			
			memberNotes = memberNotesPO.retrieveMemberNotes();
			
			super.stepStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}
		
		logger.info(print());

	}
	
	/**
	 * Retrieve the note
	 * @return
	 */
	public MemberNote getMemberNote() 
	{
		
		if ( StringUtils.isNotBlank(uniqueNoteIdentifier) )
		{
			return memberNotes.stream()
					.filter(note -> StringUtils.contains(note.getNote(), uniqueNoteIdentifier))
					.findFirst()
					.orElse(null);
		}
		else
		{
			return memberNotes.get(0);	// get the first row
		}
		
	}
	
	/**
	 * Retrieve all the notes
	 * @return list of notes
	 */
	public List<MemberNote> getMemberNotes() 
	{
		return memberNotes;
	}

}

