package org.openmrs.module.chica.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.StudyAttribute;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * @author Tammy Dugan
 * 
 */
public class TestChicaService extends BaseModuleContextSensitiveTest
{

	/**
	 * Set up the database with the initial dataset before every test method in
	 * this class.
	 * 
	 * Require authorization before every test method in this class
	 * 
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		// create the basic user and give it full rights
		initializeInMemoryDatabase();
//		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		// authenticate to the temp database
		authenticate();
	}

	/**
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testTeleformXMLToDatabaseForm() throws Exception
	{
		final int PWS_FORM_ID = 38;
		final int PSF_FORM_ID = 37;
		ATDService atdService = Context
				.getService(ATDService.class);

		String templateXMLFilename = TestUtil.WORK_DOC_SHEET_FILE;
		Form pwsForm = atdService.teleformXMLToDatabaseForm("testForm",
				templateXMLFilename);
		compareFormFields(pwsForm, PWS_FORM_ID);
		
		templateXMLFilename = TestUtil.PRESCREENER_FILE;
		Form psfForm = atdService.teleformXMLToDatabaseForm("testForm",
				templateXMLFilename);
		compareFormFields(psfForm, PSF_FORM_ID);	
	}

	private void compareFormFields(Form newForm, int databaseFormId)
	{
		FormService formService = Context.getFormService();
		Iterator<FormField> newFields = newForm.getFormFields().iterator();
		FormField currFormField = null;
		// store fields for new and old form in TreeMap
		TreeMap<Integer, String> newFormMap = new TreeMap<Integer, String>();
		TreeMap<Integer, String> oldFormMap = new TreeMap<Integer, String>();

		while (newFields.hasNext())
		{
			currFormField = newFields.next();
			newFormMap.put(currFormField.getFieldNumber(), currFormField
					.getField().getName());
		}

		Form oldPSFForm = formService.getForm(databaseFormId);
		Iterator<FormField> oldFields = oldPSFForm.getFormFields().iterator();
		
		while (oldFields.hasNext())
		{
			currFormField = oldFields.next();
			oldFormMap.put(currFormField.getFieldNumber(), currFormField
					.getField().getName());
		}
		
		Iterator<String> oldFormFields = oldFormMap.values().iterator();
		Iterator<String> newFormFields = newFormMap.values().iterator();
		
		while(oldFormFields.hasNext())
		{
			if(newFormFields.hasNext())
			{
				String oldFormFieldName = oldFormFields.next();
				String newFormFieldName = newFormFields.next();
				assertEquals(oldFormFieldName,newFormFieldName);
			}
		}
		
	}
	
	/**
	 * @see org.openmrs.module.chica.service.ChicaService#getStudyAttributeByName(String)
	 */
	@Test
	@Verifies(value = "Retreives a StudyAttribute by name", method = "getStudyAttributeByName(String)")
	public void getStudyAttributeByName_shouldRetreiveStudyAttribute() throws Exception {
		executeDataSet("dbunitFiles/studyAttributeTest.xml");
		StudyAttribute studyAttribute = Context.getService(ChicaService.class).getStudyAttributeByName("Custom Randomizer");
		System.out.println("ID::"+studyAttribute.getId());
		System.out.println("DESCRIPTION::"+studyAttribute.getDescription());
		System.out.println("CREATOR::"+studyAttribute.getCreator());
		System.out.println("UUID::"+studyAttribute.getUuid());
	}

	/**
	 * @see org.openmrs.module.chica.service.ChicaService#saveStudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute)
	 */
	@Test
	@Verifies(value = "should create new StudyAttribute", method = "saveStudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute)")
	public void saveStudyAttribute_shouldCreateNewStudyAttribute() throws Exception {
		
		executeDataSet("dbunitFiles/studyAttributeTest.xml");
		StudyAttribute studyAttribute = new StudyAttribute("NEW TEST", "New TEST DESC");
		Context.getService(ChicaService.class).saveStudyAttribute(studyAttribute);//"NEW TEST", "New TEST DESC");
		System.out.println("ID::"+studyAttribute.getStudyAttributeId());
		System.out.println("UUID::"+studyAttribute.getUuid());
		System.out.println("CREATOR::"+studyAttribute.getCreator());
		System.out.println("DATE CREATED::"+studyAttribute.getDateCreated());
	}
	
	/**
	 * @see org.openmrs.module.chica.service.ChicaService#voidStudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute, String)
	 */
	@Test
	@Verifies(value = "should void strudyAttribute with the given reason", method = "voidStudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute, String)")
	public void voidStudyAttribute_shouldVoidStudyAttributeWithTheGivenReason() throws Exception {
		executeDataSet("dbunitFiles/studyAttributeTest.xml");
		StudyAttribute studyAttribute = Context.getService(ChicaService.class).getStudyAttributeByName("Custom Randomizer");
		Context.getService(ChicaService.class).voidStudyAttribute(studyAttribute, "Test Voiding StudyAttribute");
		System.out.println("VOIDED::"+studyAttribute.getVoided());
		System.out.println("VOIDED BY::"+studyAttribute.getVoidedBy());
		System.out.println("DATE VOIDED::"+studyAttribute.getDateVoided());
		System.out.println("VOIDED REASON::"+studyAttribute.getVoidReason());
	}
	
	/**
	 * @see org.openmrs.module.chica.service.ChicaService#unvoidStudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute)
	 */
	@Test
	@Verifies(value = "should unvoid voided studyAttribute", method = "unvoidStudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute)")
	public void unvoidStudyAttribute_shouldUnvoidVoidedStudyAttribute() throws Exception {
		executeDataSet("dbunitFiles/studyAttributeTest.xml");
		StudyAttribute studyAttribute = Context.getService(ChicaService.class).getStudyAttributeByName("Custom Randomizer");
		Context.getService(ChicaService.class).unvoidStudyAttribute(studyAttribute);
		System.out.println("UNVOIDED::"+studyAttribute.getVoided());
		System.out.println("UNVOIDED BY::"+studyAttribute.getVoidedBy());
		System.out.println("DATE UNVOIDED::"+studyAttribute.getDateVoided());
		System.out.println("UNVOIDED REASON::"+studyAttribute.getVoidReason());
	}
	
	
	private StudyAttribute createTestStudyAttribute() {
		StudyAttribute studyAttribute = new StudyAttribute("TEST STUDY ATT", "TEST STUDY ATT DESC");
		studyAttribute.setUuid(UUID.randomUUID().toString());
		studyAttribute.setDateCreated(new Date());
		studyAttribute.setCreator(Context.getAuthenticatedUser());
		return studyAttribute;
	}
	
	@Test
	public void testStudyAttribute() {
		StudyAttribute studyAttribute = createTestStudyAttribute();
		System.out.println(studyAttribute.getStudyAttributeId());
		System.out.println(studyAttribute.getName());
		System.out.println(studyAttribute.getDescription());
		System.out.println(studyAttribute.getCreator());
		System.out.println(studyAttribute.getUuid());
		System.out.println(studyAttribute.getDateCreated());
		assertEquals(1, studyAttribute.getCreator().getId().intValue());
		assertNotNull(studyAttribute.getDateCreated());
	}
}
