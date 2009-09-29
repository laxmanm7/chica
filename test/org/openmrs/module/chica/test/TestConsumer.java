package org.openmrs.module.chica.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Calendar;

import org.junit.Before;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.dss.util.XMLUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class TestConsumer extends BaseModuleContextSensitiveTest
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
		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		// authenticate to the temp database
		authenticate();
	}
	
	@Test
	public void testConsume() throws Exception
	{		
		LocationService locationService = Context.getLocationService();

		int patientId = 30520;
		int providerId = 9350;
		EncounterService encounterService = Context
			.getService(EncounterService.class);
		PatientService patientService = Context.getPatientService();
		UserService userService = Context.getUserService();
		
		org.openmrs.module.chica.hibernateBeans.Encounter encounter = 
			new org.openmrs.module.chica.hibernateBeans.Encounter();
		encounter.setEncounterDatetime(new java.util.Date());
		Location location = locationService.getLocation("Unknown Location");
		Patient patient = patientService.getPatient(patientId);
		User provider = userService.getUser(providerId);
	
		encounter.setLocation(location);
		encounter.setProvider(provider);
		encounter.setPatient(patient);
		Calendar scheduledTime = Calendar.getInstance();
		scheduledTime.set(2007, Calendar.NOVEMBER, 20, 8, 12);
		encounter.setScheduledTime(scheduledTime.getTime());
		encounterService.saveEncounter(encounter);
		Integer encounterId = encounter.getEncounterId();
		ATDService atdService = Context
				.getService(ATDService.class);
		ChicaService chicaService = Context.getService(ChicaService.class);
		String filename = "test/testFiles/export_PSF.xml";
		String state = null;
		OutputStream generatedXML = new ByteArrayOutputStream();
		String PWSFilename = "test/testFiles/PWS_based_on_PSF_export.xml";
		String removeCurrentTimeXSLT = "test/testFiles/removeCurrentTime.xslt";
		AdministrationService adminService = Context.getAdministrationService();
		String booleanString = adminService.getGlobalProperty("atd.mergeTestCaseXML");
		boolean merge = Boolean.parseBoolean(booleanString);
		Integer formInstanceId = 1;
		Integer formId = null;
		FormService formService = Context.getFormService();
		Integer pwsFormId = formService.getForms("PWS",null,null,false,null,null,null).get(0).getFormId();
		Integer psfFormId = formService.getForms("PSF",null,null,false,null,null,null).get(0).getFormId();

		String PWSMergeDirectory = null;
		FormAttributeValue formAttributeValue = 
			atdService.getFormAttributeValue(pwsFormId, "pendingMergeDirectory");
		
		if (formAttributeValue != null)
		{
			PWSMergeDirectory = formAttributeValue.getValue();
		}
		
		PatientState patientState = new PatientState();
		patientState.setPatient(patient);
		
		//create the PSF form
		state = "PSF_create";
		formId = atdService.getStateByName(state).getFormId();
		patientState.setFormInstanceId(formInstanceId);

		patientState.setFormId(formId);
		
		int maxPWSDssElements = 0;
		
		formAttributeValue = 
			atdService.getFormAttributeValue(pwsFormId, "numPrompts");
		
		if (formAttributeValue != null)
		{
			maxPWSDssElements = Integer.parseInt(
					formAttributeValue.getValue());
		}
		
		int maxPSFDssElements = 0;
		state = "PWS_create";
		formId = atdService.getStateByName(state).getFormId();
		formAttributeValue = 
			atdService.getFormAttributeValue(psfFormId, "numQuestions");
		
		if (formAttributeValue != null)
		{
			maxPSFDssElements = Integer.parseInt(
					formAttributeValue.getValue());
		}
		
		chicaService.produce(new ByteArrayOutputStream(),
				patientState,patient,encounterId,"PSF", maxPSFDssElements);
		
		//read in the export xml and store observations
		chicaService.consume(new FileInputStream(filename), patient, encounterId,
				formId, formInstanceId, 1,null);

		//create the PWS form
		state = "PWS_create";
		formId = atdService.getStateByName(state).getFormId();
		patientState.setFormInstanceId(formInstanceId);

		patientState.setFormId(formId);
		generatedXML = new ByteArrayOutputStream();
		chicaService.produce(generatedXML,patientState,patient,encounterId,"PWS",maxPWSDssElements);
		ByteArrayOutputStream targetXML = new ByteArrayOutputStream();
		IOUtil.bufferedReadWrite(new FileInputStream(PWSFilename), targetXML);
		String generatedOutput = generatedXML.toString();
		if(merge&&PWSMergeDirectory != null)
		{
			FileWriter writer = new FileWriter(PWSMergeDirectory+"file5.xml");
			writer.write(generatedOutput);
			writer.flush();
			writer.close();
		}
		generatedXML = new ByteArrayOutputStream();
		XMLUtil.transformXML(new ByteArrayInputStream(generatedOutput
				.getBytes()), generatedXML, new FileInputStream(
				removeCurrentTimeXSLT),null);
		assertEquals(targetXML.toString(), generatedXML.toString());
	}
}