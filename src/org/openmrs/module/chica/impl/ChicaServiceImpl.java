package org.openmrs.module.chica.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chica.db.ChicaDAO;
import org.openmrs.module.chica.hibernateBeans.Bmiage;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Chica1Patient;
import org.openmrs.module.chica.hibernateBeans.Chica1PatientObsv;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportMap;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportStatus;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Family;
import org.openmrs.module.chica.hibernateBeans.Hcageinf;
import org.openmrs.module.chica.hibernateBeans.Lenageinf;
import org.openmrs.module.chica.hibernateBeans.OldRule;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Statistics;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.BadScansFileFilter;
import org.openmrs.module.chica.xmlBeans.Field;
import org.openmrs.module.chica.xmlBeans.LanguageAnswers;
import org.openmrs.module.chica.xmlBeans.PWSPromptAnswerErrs;
import org.openmrs.module.chica.xmlBeans.PWSPromptAnswers;
import org.openmrs.module.chica.xmlBeans.StatsConfig;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.dss.DssElement;
import org.openmrs.module.dss.DssManager;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;

public class ChicaServiceImpl implements ChicaService
{

	private Log log = LogFactory.getLog(this.getClass());

	private ChicaDAO dao;

	/**
	 * 
	 */
	public ChicaServiceImpl()
	{
	}

	/**
	 * @return
	 */
	public ChicaDAO getChicaDAO()
	{
		return this.dao;
	}

	/**
	 * @param dao
	 */
	public void setChicaDAO(ChicaDAO dao)
	{
		this.dao = dao;
	}

	/**
	 * @should testPSFConsume
	 * @should testPWSConsume
	 */
	public void consume(InputStream input, Patient patient,
			Integer encounterId,FormInstance formInstance,
			Integer sessionId,
			List<FormField> fieldsToConsume,
			Integer locationTagId)
	{
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		AdministrationService adminService = Context.getAdministrationService();

		try
		{
			DssService dssService = Context
					.getService(DssService.class);
			ATDService atdService = Context
					.getService(ATDService.class);
			ParameterHandler parameterHandler = new ChicaParameterHandler();

			// check that the medical record number in the xml file and the medical
			// record number of the patient match
			String patientMedRecNumber = patient.getPatientIdentifier()
					.getIdentifier();
			String xmlMedRecNumber = null;
			String xmlMedRecNumber2 = null;
			LogicService logicService = Context.getLogicService();

			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService
					.getLogicDataSource("xml");
			HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource
					.getParsedFile(formInstance);

			Integer formInstanceId = formInstance.getFormInstanceId();

			if (fieldMap == null)
			{
				try
				{
					formInstance = xmlDatasource.parse(input,
							formInstance,locationTagId);
					fieldMap = xmlDatasource.getParsedFile(formInstance);

				} catch (Exception e1)
				{
					ATDError atdError = new ATDError("Error","XML Parsing", 
							" Error parsing XML file to be consumed. Form Instance Id = " + formInstanceId
							, Util.getStackTrace(e1), new Date(),sessionId);
					atdService.saveError(atdError);
					return;
				}
			}

			startTime = System.currentTimeMillis();


			Integer formId = formInstance.getFormId();
			Integer locationId = formInstance.getLocationId();
			String medRecNumberTag = org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formId, "medRecNumberTag",locationTagId,locationId);

			String medRecNumberTag2 = org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formId, "medRecNumberTag2",locationTagId,locationId);
			
			//MRN
			if (medRecNumberTag!=null&&fieldMap.get(medRecNumberTag) != null)
			{
				xmlMedRecNumber = fieldMap.get(medRecNumberTag).getValue();
			}
			
			if (medRecNumberTag2 != null &&fieldMap.get(medRecNumberTag2)!=null ){
				xmlMedRecNumber2 = fieldMap.get(medRecNumberTag2).getValue();
			}
			
			//Compare form MRNs to patient medical record number
			if (!Util.extractIntFromString(patientMedRecNumber).equalsIgnoreCase(
					Util.extractIntFromString(xmlMedRecNumber)))
			{
				//Compare patient MRN to MRN bar code from back of form.
				if (xmlMedRecNumber2 == null || !Util.extractIntFromString(patientMedRecNumber)
							.equalsIgnoreCase( Util.extractIntFromString(xmlMedRecNumber2))){
					ATDError noMatch = new ATDError("Fatal", "MRN Validity", "Patient MRN" 
							+ " does not match any form MRN bar codes (front or back) " 
							,"\r\n Form instance id: "  + formInstanceId 
							+ "\r\n Patient MRN: " + patientMedRecNumber
							+ " \r\n MRN barcode front: " + xmlMedRecNumber + "\r\n MRN barcode back: "
							+ xmlMedRecNumber2, new Date(), sessionId);
					atdService.saveError(noMatch);
				    return;
					
				} 
				//Patient MRN does not match MRN bar code on front of form, but does match 
				// MRN bar code on back of form.
				ATDError warning = new ATDError("Warning", "MRN Validity", "Patient MRN matches" 
							+ " MRN bar code from back of form only. " 
							,"Form instance id: "  + formInstanceId 
							+ "\r\n Patient MRN: " + patientMedRecNumber
							+ " \r\n MRN barcode front: " + xmlMedRecNumber + "\r\n MRN barcode back: " 
							+ xmlMedRecNumber2, new Date(), sessionId);
				atdService.saveError(warning);
				
				
			}else{
			
				//Check for conflicting front and back MRN bar codes.
				if (!Util.extractIntFromString(xmlMedRecNumber).equalsIgnoreCase(
						Util.extractIntFromString(xmlMedRecNumber2)))
				{
					ATDError atdError = new ATDError("Warning", "MRN Validity", "Patient MRN matches " 
							+ " MRN bar code on front of form, but the front and back of the form do not match."
							+ " Possible scan error. "
							, "\r\n Form instance id: "  + formInstanceId + " \r\n MRN bar code front: " + xmlMedRecNumber + "\r\n MRN bar code back: "
							+ xmlMedRecNumber2, new Date(), sessionId);
					atdService.saveError(atdError);
				}
			}

			startTime = System.currentTimeMillis();
			// make sure storeObs gets loaded before running consume
			// rules
			dssService.loadRule("CREATE_JIT",false);
			dssService.loadRule("ChicaAgeRule",false);
			dssService.loadRule("storeObs",false);
			dssService.loadRule("getObs",false);
			dssService.loadRule("DDST", false);
			dssService.loadRule("LookupBPcentile", false);
			dssService.loadRule("ScoreJit", false);
			dssService.loadRule("CheckIncompleteScoringJit", false);
			dssService.loadRule("VanderbiltParentADHD", false);
			dssService.loadRule("LocationAttributeLookup", false);
			dssService.loadRule("CHOOSE_ASQ_JIT", false);
			dssService.loadRule("CHOOSE_ASQ_ACTIVITY_JIT", false);
			dssService.loadRule("ASQWriteDoneObs", false);
			dssService.loadRule("getLastObs",false);
			dssService.loadRule("CHOOSE_ASQ_JIT_PWS",false);
			String defaultPackagePrefix = Util.formatPackagePrefix(
				adminService.getGlobalProperty("atd.defaultPackagePrefix"));
			
			dssService.loadRule("roundOnePlace", defaultPackagePrefix, null, false);
			dssService.loadRule("integerResult", defaultPackagePrefix, null,false);
			
			startTime = System.currentTimeMillis();
			//only consume the question fields for one side of the PSF
			HashMap<String,Field> languageFieldsToConsume = 
				saveAnswers(fieldMap, formInstance,encounterId,patient);
			FormService formService = Context.getFormService();
			Form databaseForm = formService.getForm(formId);
			TeleformTranslator translator = new TeleformTranslator();

			if (fieldsToConsume == null)
			{
				fieldsToConsume = new ArrayList<FormField>();

				for (FormField currField : databaseForm.getOrderedFormFields())
				{
					FormField parentField = currField.getParent();
					String fieldName = currField.getField().getName();
					FieldType currFieldType = currField.getField()
							.getFieldType();
					// only process export fields
					if (currFieldType != null
							&& currFieldType.equals(translator
									.getFieldType("Export Field")))
					{
						if (parentField != null)
						{
							PatientATD patientATD = atdService.getPatientATD(
									formInstance, parentField.getField()
											.getFieldId());

							if (patientATD != null)
							{
								if (databaseForm.getName().equals("PSF"))
								{
									// consume only one side of questions for
									// PSF
									if (languageFieldsToConsume
											.get(fieldName)!=null)
									{
										fieldsToConsume.add(currField);
									}
								} else
								{
									fieldsToConsume.add(currField);
								}
							} else
							{
								// consume all other fields with parents
								fieldsToConsume.add(currField);
							}
						} else
						{
							// consume all other fields
							fieldsToConsume.add(currField);
						}
					}
				}
			}
			System.out.println("chicaService.consume: Fields to consume: "+
				(System.currentTimeMillis()-startTime));
			
			startTime = System.currentTimeMillis();
			atdService.consume(input, formInstance, patient, encounterId,
					 null, null, parameterHandler,
					 fieldsToConsume,locationTagId,sessionId);
			System.out.println("chicaService.consume: Time of atdService.consume: "+
				(System.currentTimeMillis()-startTime));
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}
	}

	private void populateFieldNameArrays(
			HashMap<String, HashMap<String, Field>> languages,
			ArrayList<String> pwsAnswerChoices,
			ArrayList<String> pwsAnswerChoiceErr)
	{
		AdministrationService adminService = Context.getAdministrationService();
		StatsConfig statsConfig = null;
		String statsConfigFile = adminService
				.getGlobalProperty("chica.statsConfigFile");

		if(statsConfigFile == null){
			log.error("Could not find statsConfigFile. Please set global property chica.statsConfigFile.");
			return;
		}
		try
		{
			InputStream input = new FileInputStream(statsConfigFile);
			statsConfig = (StatsConfig) XMLUtil.deserializeXML(
					StatsConfig.class, input);
		} catch (IOException e1)
		{
			log.error(e1.getMessage());
			log.error(Util.getStackTrace(e1));
			return;
		}

		// process prompt answers
		PWSPromptAnswers pwsPromptAnswers = statsConfig.getPwsPromptAnswers();

		if (pwsPromptAnswers != null)
		{
			ArrayList<org.openmrs.module.chica.xmlBeans.Field> fields = pwsPromptAnswers
					.getFields();

			if (fields != null)
			{
				for (org.openmrs.module.chica.xmlBeans.Field currField : fields)
				{
					if (currField.getId() != null)
					{
						pwsAnswerChoices.add(currField.getId());
					}
				}
			}
		}

		// process prompt answer errs
		PWSPromptAnswerErrs pwsPromptAnswerErrs = statsConfig
				.getPwsPromptAnswerErrs();

		if (pwsPromptAnswerErrs != null)
		{
			ArrayList<org.openmrs.module.chica.xmlBeans.Field> fields = pwsPromptAnswerErrs
					.getFields();

			if (fields != null)
			{
				for (org.openmrs.module.chica.xmlBeans.Field currField : fields)
				{
					if (currField.getId() != null)
					{
						pwsAnswerChoiceErr.add(currField.getId());
					}
				}
			}
		}

		LanguageAnswers languageAnswers = statsConfig.getLanguageAnswers();
		org.openmrs.module.chica.util.Util.populateFieldNameArrays(languages, languageAnswers);
	}

	private HashMap<String, Field> saveAnswers(
			HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap,
			FormInstance formInstance, int encounterId, Patient patient)
	{
		ATDService atdService = Context
				.getService(ATDService.class);
		TeleformTranslator translator = new TeleformTranslator();
		FormService formService = Context.getFormService();
		Integer formId = formInstance.getFormId();
		Form databaseForm = formService.getForm(formId);
		if (databaseForm == null)
		{
			log.error("Could not consume teleform export xml because form "
					+ formId + " does not exist in the database");
			return null;
		}

		ArrayList<String> pwsAnswerChoices = new ArrayList<String>();
		ArrayList<String> pwsAnswerChoiceErr = new ArrayList<String>();

		HashMap<String, HashMap<String, Field>> languageToFieldnames = new HashMap<String, HashMap<String, Field>>();

		this.populateFieldNameArrays(languageToFieldnames, pwsAnswerChoices,
				pwsAnswerChoiceErr);

		HashMap<String, Integer> languageToNumAnswers = new HashMap<String, Integer>();
		HashMap<String, HashMap<Integer, String>> languageToAnswers = new HashMap<String, HashMap<Integer, String>>();

		Rule providerNameRule = new Rule();
		providerNameRule.setTokenName("providerName");
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("encounterId", encounterId);
		providerNameRule.setParameters(parameters);

		for (FormField currField : databaseForm.getFormFields())
		{
			FieldType currFieldType = currField.getField().getFieldType();
			// only process export fields
			if (currFieldType != null
					&& currFieldType.equals(translator
							.getFieldType("Export Field")))
			{
				String fieldName = currField.getField().getName();
				String answer = null;
				if (fieldMap.get(fieldName) != null)
				{
					answer = fieldMap.get(fieldName).getValue();
				}
				FormField parentField = currField.getParent();

				// if parent field is not null look at parent
				// field for rule to execute
				if (parentField != null)
				{
					PatientATD patientATD = atdService.getPatientATD(
							formInstance,
							parentField.getField().getFieldId());

					if (patientATD != null)
					{
						Rule rule = patientATD.getRule();

						if (answer == null || answer.length() == 0)
						{
							answer = "NoAnswer";
						}
						Integer ruleId = rule.getRuleId();

						String dsstype = databaseForm.getName();

						if (dsstype.equalsIgnoreCase("PSF"))
						{
							for (String currLanguage : languageToFieldnames
									.keySet())
							{
								HashMap<String, Field> currLanguageArray = languageToFieldnames
										.get(currLanguage);
								HashMap<Integer, String> answers = languageToAnswers
										.get(currLanguage);

								if (answers == null)
								{
									answers = new HashMap<Integer, String>();
									languageToAnswers
											.put(currLanguage, answers);
								}
						
								if (currLanguageArray.get(currField
										.getField().getName())!= null)
								{
									answers.put(ruleId, answer);
									if (!answer.equalsIgnoreCase("NoAnswer"))
									{
										if (languageToNumAnswers
												.get(currLanguage) == null)
										{
											languageToNumAnswers.put(
													currLanguage, 0);
										}

										languageToNumAnswers.put(currLanguage,
												languageToNumAnswers
														.get(currLanguage) + 1);
									}
								}
							}
						}

						if (dsstype.equalsIgnoreCase("PWS"))
						{
							Integer formInstanceId = formInstance.getFormInstanceId();
							Integer locationId = formInstance.getLocationId();
							List<Statistics> statistics = this.getChicaDAO()
									.getStatByIdAndRule(formInstanceId,
											ruleId,dsstype,locationId);

							if (statistics != null)
							{
								if (!answer.equalsIgnoreCase("NoAnswer"))
								{
									answer = formatAnswer(answer);
								}
								for (Statistics stat : statistics)
								{
									if (pwsAnswerChoices.contains(currField
											.getField().getName()))
									{
										stat.setAnswer(answer);
									}
									if (pwsAnswerChoiceErr.contains(currField
											.getField().getName()))
									{
										stat.setAnswerErr(answer);
									}

									this.updateStatistics(stat);
								}
							}
						}
					}

				}
			}
		}

		int maxNumAnswers = -1;
		String maxLanguage = null;
		HashMap<Integer, String> maxAnswers = null;

		for (String language : languageToNumAnswers.keySet())
		{
			Integer compareNum = languageToNumAnswers.get(language);

			if (compareNum != null && compareNum > maxNumAnswers)
			{
				maxNumAnswers = compareNum;
				maxLanguage = language;
				maxAnswers = languageToAnswers.get(language);
			}
		}

		String languageResponse = "English";
		if (maxNumAnswers > 0)
		{
			languageResponse = maxLanguage;
			HashMap<Integer, String> answers = maxAnswers;

			for (Integer currRuleId : answers.keySet())
			{
				String answer = answers.get(currRuleId);
				Integer formInstanceId = formInstance.getFormInstanceId();
				Integer locationId = formInstance.getLocationId();
				List<Statistics> statistics = this.getChicaDAO()
						.getStatByIdAndRule(formInstanceId, currRuleId, "PSF",locationId);
				if (statistics != null)
				{
					for (Statistics stat : statistics)
					{
						stat.setAnswer(answer);
						stat.setLanguageResponse(languageResponse);

						this.updateStatistics(stat);
					}
				}
			}
		}
		
		String formName = formService.getForm(formId).getName();
		//save language response to preferred language
		//language is determined by maximum number of answers
		//selected for a language on the PSF
		if (languageResponse != null&&formName.equals("PSF")) {
			ObsService obsService = Context.getObsService();
			Obs obs = new Obs();
			String conceptName = "preferred_language";
			ConceptService conceptService = Context.getConceptService();
			Concept currConcept = conceptService.getConceptByName(conceptName);
			Concept languageConcept = conceptService.getConceptByName(languageResponse);
			if (currConcept == null || languageConcept == null) {
				log
					.error("Could not save preferred language for concept: " + conceptName + " and language: "
				        + languageResponse);
			} else {
				obs.setValueCoded(languageConcept);
				
				EncounterService encounterService = Context.getService(EncounterService.class);
				Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
				
				Location location = encounter.getLocation();
				
				obs.setPerson(patient);
				obs.setConcept(currConcept);
				obs.setLocation(location);
				obs.setEncounter(encounter);
				obs.setObsDatetime(new Date());
				obsService.saveObs(obs, null);
			}
		}
		if (maxNumAnswers > 0){
			return languageToFieldnames.get(maxLanguage);
		}else{
			return languageToFieldnames.get("English");
		}
	}

	private String formatAnswer(String answer)
	{
		final int NUM_ANSWERS = 6;
		String[] answers = new String[NUM_ANSWERS];

		// initialize all answers to N
		for (int i = 0; i < answers.length; i++)
		{
			answers[i] = "X";
		}

		char[] characters = answer.toCharArray();

		// convert the answer string from positional digits (1,2,3,4,5,6)
		// to Y
		for (int i = 0; i < characters.length; i++)
		{
			try
			{
				int intAnswer = Character.getNumericValue(characters[i]);
				int answerPos = intAnswer - 1;

				if (answerPos >= 0 && answerPos < answers.length)
				{
					answers[answerPos] = "Y";
				}
			} catch (Exception e)
			{
				log.error(e.getMessage());
				log.error(Util.getStackTrace(e));
			}
		}

		String newAnswer = "";

		for (String currAnswer : answers)
		{
			newAnswer += currAnswer;
		}

		return newAnswer;
	}

	/**
	 * @should testPSFProduce
	 * @should testPWSProduce
	 */
	public void produce(OutputStream output, PatientState state,
			Patient patient, Integer encounterId, String dssType,
			int maxDssElements,Integer sessionId)
	{
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		AdministrationService adminService = Context.getAdministrationService();

		DssService dssService = Context
				.getService(DssService.class);
		ATDService atdService = Context
				.getService(ATDService.class);

		DssManager dssManager = new DssManager(patient);
		dssManager.setMaxDssElementsByType(dssType, maxDssElements);
		HashMap<String, Object> baseParameters = new HashMap<String, Object>();
		startTime = System.currentTimeMillis();
		try {
	        dssService.loadRule("CREATE_JIT",false);
	        dssService.loadRule("ChicaAgeRule",false);
	        dssService.loadRule("storeObs",false);
			dssService.loadRule("getObs",false);
	        dssService.loadRule("DDST", false);
	        dssService.loadRule("LookupBPcentile", false);
			dssService.loadRule("ScoreJit", false);
			dssService.loadRule("CheckIncompleteScoringJit", false);
			dssService.loadRule("VanderbiltParentADHD", false);
			dssService.loadRule("LocationAttributeLookup", false);
			dssService.loadRule("CHOOSE_ASQ_JIT", false);
			dssService.loadRule("CHOOSE_ASQ_ACTIVITY_JIT", false);
			dssService.loadRule("ASQWriteDoneObs", false);
			dssService.loadRule("getLastObs",false);
			dssService.loadRule("CHOOSE_ASQ_JIT_PWS",false);
			String defaultPackagePrefix = Util.formatPackagePrefix(
				adminService.getGlobalProperty("atd.defaultPackagePrefix"));
			
			dssService.loadRule("roundOnePlace", defaultPackagePrefix, null, false);
			dssService.loadRule("integerResult", defaultPackagePrefix, null,false);
		}
        catch (Exception e) {
	        log.error("load rule failed", e);
        }
		startTime = System.currentTimeMillis();

		FormInstance formInstance = state.getFormInstance();
		atdService.produce(patient, formInstance, output, dssManager,
				encounterId, baseParameters, null,state.getLocationTagId(),sessionId);
		startTime = System.currentTimeMillis();
		Integer formInstanceId = formInstance.getFormInstanceId();
		Integer locationId = formInstance.getLocationId();
		Integer formId = formInstance.getFormId();
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		String formName = form.getName();
		this.saveStats(patient, formInstanceId, dssManager, encounterId,state.getLocationTagId(),
			locationId,formName);
	}

	private void saveStats(Patient patient, Integer formInstanceId,
			DssManager dssManager, Integer encounterId, 
			Integer locationTagId,Integer locationId,String formName)
	{
		HashMap<String, ArrayList<DssElement>> dssElementsByType = dssManager
				.getDssElementsByType();
		EncounterService encounterService = Context
				.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService
				.getEncounter(encounterId);
		String type = null;

		if (dssElementsByType == null)
		{
			return;
		}
		Iterator<String> iter = dssElementsByType.keySet().iterator();
		ArrayList<DssElement> dssElements = null;

		while (iter.hasNext())
		{
			type = iter.next();
			dssElements = dssElementsByType.get(type);
			for (int i = 0; i < dssElements.size(); i++)
			{
				DssElement currDssElement = dssElements.get(i);

					this.addStatistics(patient, currDssElement, formInstanceId, i,
							encounter, formName,locationTagId,locationId);
				}
			}
		}

	public void updateStatistics(Statistics statistics)
	{
		getChicaDAO().updateStatistics(statistics);
	}
	
	public void createStatistics(Statistics statistics)
	{
		getChicaDAO().addStatistics(statistics);
	}

	private void addStatistics(Patient patient, DssElement currDssElement,
			Integer formInstanceId, int questionPosition, Encounter encounter,
			String formName,Integer locationTagId,Integer locationId)
	{
			DssService dssService = Context
					.getService(DssService.class);
			Integer ruleId = currDssElement.getRuleId();
			Rule rule = dssService.getRule(ruleId);

			Statistics statistics = new Statistics();
			statistics.setAgeAtVisit(org.openmrs.module.chica.util.Util
					.adjustAgeUnits(patient.getBirthdate(), null));
			statistics.setPriority(rule.getPriority());
			statistics.setFormInstanceId(formInstanceId);
			statistics.setLocationTagId(locationTagId);
			statistics.setPosition(questionPosition + 1);

			statistics.setRuleId(ruleId);
			statistics.setPatientId(patient.getPatientId());
			statistics.setFormName(formName);
			statistics.setEncounterId(encounter.getEncounterId());
			statistics.setLocationId(locationId);

			getChicaDAO().addStatistics(statistics);
	}

	public Percentile getWtageinf(double ageMos, int sex)
	{
		return getChicaDAO().getWtageinf(ageMos, sex);
	}

	public Bmiage getBmiage(double ageMos, int sex)
	{
		return getChicaDAO().getBmiage(ageMos, sex);
	}

	public Hcageinf getHcageinf(double ageMos, int sex)
	{
		return getChicaDAO().getHcageinf(ageMos, sex);
	}

	public Lenageinf getLenageinf(double ageMos, int sex)
	{
		return getChicaDAO().getLenageinf(ageMos, sex);
	}

	public List<Study> getActiveStudies()
	{
		return getChicaDAO().getActiveStudies();
	}

	public List<Statistics> getStatByFormInstance(int formInstanceId,String formName, 
			Integer locationId)
	{
		return getChicaDAO().getStatByFormInstance(formInstanceId,formName, locationId);
	}

	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName)
	{
		return getChicaDAO().getStudyAttributeValue(study, studyAttributeName);
	}

	public List<Statistics> getStatByIdAndRule(int formInstanceId,int ruleId,String formName, 
			Integer locationId)	{
		return getChicaDAO().getStatByIdAndRule(formInstanceId,ruleId,formName,locationId);
	}

	public List<OldRule> getAllOldRules()
	{
		return getChicaDAO().getAllOldRules();
	}

	public List<Chica1PatientObsv> getChicaPatientObsByPSF(Integer psfId,
			Integer patientId)
	{
		return getChicaDAO().getChicaPatientObsByPSF(psfId, patientId);
	}

	public List<Chica1PatientObsv> getChicaPatientObsByPWS(Integer pwsId,
			Integer patientId)
	{
		return getChicaDAO().getChicaPatientObsByPWS(pwsId, patientId);
	}

	public List<Chica1Appointment> getChica1AppointmentsByPatient(
			Integer patientId)
	{
		return getChicaDAO().getChica1AppointmentsByPatient(patientId);
	}

	public List<Chica1Patient> getChica1Patients()
	{
		return getChicaDAO().getChica1Patients();
	}

	public PatientFamily getPatientFamily(Integer patientId)
	{
		return getChicaDAO().getPatientFamily(patientId);
	}

	public Family getFamilyByAddress(String address)
	{
		return getChicaDAO().getFamilyByAddress(address);

	}
	
	public Family getFamilyByPhone(String phone)
	{
		return getChicaDAO().getFamilyByPhone(phone);

	}

	public void savePatientFamily(PatientFamily patientFamily)
	{
		getChicaDAO().savePatientFamily(patientFamily);

	}

	public void saveFamily(Family family)
	{
		getChicaDAO().saveFamily(family);
	}

	public void updateFamily(Family family)
	{
		getChicaDAO().updateFamily(family);
	}
	
	public Obs getStudyArmObs(Integer familyId,Concept studyConcept){
		return getChicaDAO().getStudyArmObs(familyId, studyConcept);
	}
	
	public List<String> getInsCategories(){
		return getChicaDAO().getInsCategories();
	}
	
	public void updateChica1Patient(Chica1Patient patient){
		getChicaDAO().updateChica1Patient(patient);
	}
	
	public void updateChica1Appointment(Chica1Appointment appointment){
		getChicaDAO().updateChica1Appointment(appointment);
	}
	
	public void setChica1PatientObsvObsId(Chica1PatientObsv chica1PatientObsv){
		getChicaDAO().setChica1PatientObsvObsId(chica1PatientObsv);
	}
	
	public List<Chica1PatientObsv> getUnloadedChicaPatientObs(Integer patientId,String date){
		return getChicaDAO().getUnloadedChicaPatientObs(patientId, date);
	}
	
	public List<Chica1Appointment> getChica1AppointmentsByDate(Integer patientId, String date){
		return getChicaDAO().getChica1AppointmentsByDate(patientId, date);
	}
	public String getObsvNameByObsvId(String obsvId){
		return getChicaDAO().getObsvNameByObsvId(obsvId);
	}
	
	public String getInsCategoryByCarrier(String carrierCode){
		return getChicaDAO().getInsCategoryByCarrier(carrierCode);
	}
	
	public String getInsCategoryBySMS(String smsCode){
		return getChicaDAO().getInsCategoryBySMS(smsCode);
	}
	
	public String getInsCategoryByInsCode(String insCode){
		return getChicaDAO().getInsCategoryByInsCode(insCode);
	}
	
	
	public PatientState getPrevProducePatientState(Integer sessionId, Integer patientStateId ){
		ATDService atdService = Context.getService(ATDService.class);
		return atdService.getPrevPatientStateByAction(sessionId, patientStateId,"PRODUCE FORM INSTANCE");
	}
	
	public Double getHighBP(Patient patient, Integer bpPercentile,
			String bpType, org.openmrs.Encounter encounter)
	{
		List<Person> persons = new ArrayList<Person>();
		persons.add(patient);
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName("HtCentile");
		List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
		encounters.add(encounter);
		Double heightPercentile = null;
		ObsService obsService = Context.getObsService();
		List<Concept> concepts = new ArrayList<Concept>();

		concepts.add(concept);

		List<Obs> obs = obsService
				.getObservations(persons, encounters, concepts, null, null,
						null, null, null, null, null, null, false);
		if (obs != null && obs.size() > 0)
		{
			heightPercentile = obs.get(0).getValueNumeric();
		}

		if (heightPercentile == null)
		{
			return null;
		}
		
		return getHighBP(patient,bpPercentile,bpType,heightPercentile, new Date());
	}
	
	/**
	 * @should checkHighBP
	 */
	public Double getHighBP(Patient patient, Integer bpPercentile, String bpType, 
			Double heightPercentile, Date onDate)
	{
		Integer lowerAge = patient.getAge(onDate);
		
		if(lowerAge > 17){
			return null;
		}
		
		Integer upperAge = lowerAge + 1;
		String sex = patient.getGender();

		Integer lowerPercentile = null;
		Integer upperPercentile = null;

		if (heightPercentile < 5)
		{
			lowerPercentile = 5;
			upperPercentile = 5;
		}

		if (heightPercentile >= 5)
		{
			lowerPercentile = 5;
			upperPercentile = 10;
		}

		if (heightPercentile >= 10)
		{
			lowerPercentile = 10;
			upperPercentile = 25;
		}

		if (heightPercentile >= 25)
		{
			lowerPercentile = 25;
			upperPercentile = 50;
		}

		if (heightPercentile >= 50)
		{
			lowerPercentile = 50;
			upperPercentile = 75;
		}

		if (heightPercentile >= 75)
		{
			lowerPercentile = 75;
			upperPercentile = 90;
		}

		if (heightPercentile >= 90)
		{
			lowerPercentile = 90;
			upperPercentile = 95;
		}

		if (heightPercentile >= 95)
		{
			lowerPercentile = 95;
			upperPercentile = 95;
		}
		
		Integer lowerAgeLowerPercentile = null;
		Integer lowerAgeUpperPercentile = null;
		Integer upperAgeLowerPercentile = null;
		Integer upperAgeUpperPercentile = null;

		if (lowerAge >=1)
		{
			lowerAgeLowerPercentile = getChicaDAO().getHighBP(lowerAge,
					sex, bpPercentile, bpType, lowerPercentile);
		}
		if (lowerAge >=1)
		{
			lowerAgeUpperPercentile = getChicaDAO().getHighBP(lowerAge,
					sex, bpPercentile, bpType, upperPercentile);
		}
		if (upperAge <=17)
		{
			upperAgeLowerPercentile = getChicaDAO().getHighBP(upperAge,
					sex, bpPercentile, bpType, lowerPercentile);
		}
		if (upperAge <=17)
		{
			upperAgeUpperPercentile = getChicaDAO().getHighBP(upperAge,
					sex, bpPercentile, bpType, upperPercentile);
		}
		
		Double lowerAgeInter = null;
		
		// prevents division by zero
		Integer denominator = (upperPercentile - lowerPercentile);
		Double firstOperand = (heightPercentile - lowerPercentile);

		if (denominator == 0)
		{
			firstOperand = 0.0;
		} else
		{
			firstOperand = firstOperand / denominator;
		}
		
		//interpolate lowerAgeLowerPercentile and lowerAgeUpperPercentile
		if(lowerAgeLowerPercentile != null && lowerAgeUpperPercentile != null){
					
			lowerAgeInter = (firstOperand * (lowerAgeUpperPercentile - lowerAgeLowerPercentile))
					+ lowerAgeLowerPercentile;
		}
		
		Double upperAgeInter = null;
		
		//interpolate upperAgeLowerPercentile and upperAgeUpperPercentile
		if (upperAgeLowerPercentile != null && upperAgeUpperPercentile != null)
		{
			upperAgeInter = (firstOperand * (upperAgeUpperPercentile - upperAgeLowerPercentile))
					+ upperAgeLowerPercentile;
		}
		
		double fractionalAge = Util.getFractionalAgeInUnits(patient.getBirthdate(), onDate,Util.YEAR_ABBR);
		
		double fraction = fractionalAge - lowerAge;
		
		if(lowerAgeInter == null && upperAgeInter == null){
			if(lowerAgeUpperPercentile != null&&upperAgeUpperPercentile != null){
				upperAgeInter = upperAgeUpperPercentile.doubleValue();
				lowerAgeInter = lowerAgeUpperPercentile.doubleValue();
			}
			if(lowerAgeLowerPercentile != null&&upperAgeLowerPercentile != null){
				upperAgeInter = upperAgeLowerPercentile.doubleValue();
				lowerAgeInter = lowerAgeLowerPercentile.doubleValue();
			}
		}
		
		if(lowerAgeInter == null){
			return Util.round(upperAgeInter,2);
		}
		
		if(upperAgeInter == null){
			return Util.round(lowerAgeInter,2);
		}
		
		return Util.round(lowerAgeInter+((upperAgeInter-lowerAgeInter)*fraction),2);
		}

		public String getDDSTLeaf(String category, Integer ageInDays){
			return getChicaDAO().getDDSTLeaf(category, ageInDays);
		}
		
		public List<Statistics> getStatsByEncounterForm(Integer encounterId,String formName){
			return getChicaDAO().getStatsByEncounterForm(encounterId, formName);
		}

		public List<Statistics> getStatsByEncounterFormNotPrioritized(Integer encounterId,String formName){
			return getChicaDAO().getStatsByEncounterFormNotPrioritized(encounterId, formName);
		}
	
		
		public ChicaHL7Export insertEncounterToHL7ExportQueue(ChicaHL7Export export) {
			getChicaDAO().insertEncounterToHL7ExportQueue(export);
			return export;
		}

		public List<ChicaHL7Export> getPendingHL7Exports() {
			return getChicaDAO().getPendingHL7Exports();
			
		}

		public void saveChicaHL7Export(ChicaHL7Export export) {

			getChicaDAO().saveChicaHL7Export(export);
			
		}
	
		public List<ChicaHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId) {
			return getChicaDAO().getPendingHL7ExportsByEncounterId(encounterId);
		}
	
		public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, 
				Integer locationTagId,Integer locationId){
			return getChicaDAO().getReprintRescanStatesByEncounter(encounterId, optionalDateRestriction, locationTagId,locationId);
		}
		
		public List<String> getPrinterStations(Location location){
			String locationTagAttributeName = "ActivePrinterLocation";
			ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);

			Set<LocationTag> tags = location.getTags();
			List<String>  stationNames = new ArrayList<String>();
			for (LocationTag tag : tags){
				LocationTagAttributeValue locationTagAttributeValue = 
					chirdlUtilService.getLocationTagAttributeValue(tag
						.getLocationTagId(), locationTagAttributeName,location.getLocationId());
				if (locationTagAttributeValue != null)
				{
					String activePrinterLocationString = locationTagAttributeValue
							.getValue();
					//only display active printer locations
					if (activePrinterLocationString.equalsIgnoreCase("true"))
					{
						stationNames.add(tag.getTag());
					}
				}
				
			}
			Collections.sort(stationNames);
			
			return stationNames;
		}
		
		public Chica1Appointment getChica1AppointmentByEncounterId(Integer encId){
			Chica1Appointment appt= getChicaDAO().getChica1AppointmentByEncounterId(encId);
			return appt;
			
		}
		
		public void  saveHL7ExportMap (ChicaHL7ExportMap map){
			
			getChicaDAO().saveHL7ExportMap(map);
			
		}
		
		public ChicaHL7ExportMap getChicaExportMapByQueueId(Integer queue_id){
			return getChicaDAO().getChicaExportMapByQueueId(queue_id);
		}
		
		public ChicaHL7ExportStatus getChicaExportStatusByName (String name){
			return getChicaDAO().getChicaExportStatusByName(name);
		}
		
		public ChicaHL7ExportStatus getChicaExportStatusById (Integer id){
			return getChicaDAO().getChicaExportStatusById( id);
		}
		
		public List<Object[]> getFormsPrintedByWeek(String formName, String locationName) {
			return getChicaDAO().getFormsPrintedByWeek(formName, locationName);
		}
		
		public List<Object[]> getFormsScannedByWeek(String formName, String locationName) {
			return getChicaDAO().getFormsScannedByWeek(formName, locationName);
		}
		
		public List<Object[]> getFormsScannedAnsweredByWeek(String formName, String locationName) {
			return getChicaDAO().getFormsScannedAnsweredByWeek(formName, locationName);
		}
		public List<Object[]> getFormsScannedAnythingMarkedByWeek(String formName, String locationName){
			return getChicaDAO().getFormsScannedAnythingMarkedByWeek(formName,locationName);	
		}
		public List<Object[]> getQuestionsScanned(String formName, String locationName) {
			return getChicaDAO().getQuestionsScanned(formName, locationName);
		}

		public List<Object[]> getQuestionsScannedAnswered(String formName, String locationName) {
			return getChicaDAO().getQuestionsScannedAnswered(formName, locationName);
		}

        public List<URL> getBadScans(String locationName) {
	        AdministrationService adminService = Context.getAdministrationService();
	        String imageDirStr = adminService.getGlobalProperty("chica.defaultTifImageDirectory");
	        if (imageDirStr == null || imageDirStr.length() == 0) {
	        	log.equals("Please specify a value for the global property 'chica.defaultTifImageDirectory'");
	        	return new ArrayList<URL>();
	        }
	        
	        File imageDirectory = new File(imageDirStr, locationName);
	        if (!imageDirectory.exists()) {
	        	log.error("Cannot find directory: " + imageDirStr + File.separator + locationName);
	        	return new ArrayList<URL>();
	        }
	        
	        List<URL> badScans = new ArrayList<URL>();
	        String ignoreExtensions = adminService.getGlobalProperty("chica.badScansExcludedExtensions");
	        List<String> extensionList = new ArrayList<String>();
	        if (ignoreExtensions != null) {
	        	StringTokenizer tokenizer = new StringTokenizer(ignoreExtensions, ",");
	        	while (tokenizer.hasMoreTokens()) {
	        		extensionList.add(tokenizer.nextToken());
	        	}
	        }
	        
	        FilenameFilter fileFilter = new BadScansFileFilter(new Date(), extensionList);
	        return getBadScans(imageDirectory, badScans, fileFilter);
        }
        
        public void moveBadScan(String url, boolean formRescanned) throws Exception {
	        try {
	        	URL urlLoc = new URL(url);
	            File fileLoc = new File(urlLoc.getFile());
	            if (!fileLoc.exists()) {
	            	log.warn("Bad scan does not exist: " + fileLoc.getAbsolutePath());
	            	return;
	            }
	            
	            File parentFile = fileLoc.getParentFile();
	            File rescannedScansDir = null;
	            if (formRescanned) {
	            	rescannedScansDir = new File(parentFile, "rescanned bad scans");
	            } else {
	            	rescannedScansDir = new File(parentFile, "ignored bad scans");
	            }
	            
	            if (!rescannedScansDir.exists()) {
	            	rescannedScansDir.mkdirs();
	            }
	            
	            String filename = fileLoc.getName();
	            File newLoc = new File(rescannedScansDir, filename);
	            if (newLoc.exists()) {
	            	int i = 1;
	            	int index = filename.indexOf("."); 
	            	String name = null;
	            	String extension = "";
	            	if (index >= 0) {
	            		name = filename.substring(0, index);
	            		extension = filename.substring(index, filename.length());
	            	} else {
	            		name = filename;
	            	}
	            	newLoc = new File(rescannedScansDir, name + "_" + i++ + extension);
	            	while (newLoc.exists() && i < 1000) {
	            		newLoc = new File(rescannedScansDir, name + "_" + i++ + extension);
	            	}
	            }
	            
	            IOUtil.copyFile(fileLoc.getAbsolutePath(), newLoc.getAbsolutePath());
	            fileLoc.delete();
	            
	            // log the event
	            String description = null;
	            if (formRescanned) {
	            	description = "User attempted to rescan a bad scan: " + newLoc.getAbsolutePath();
	            } else {
	            	description = "User ignored a bad scan: " + newLoc.getAbsolutePath();
	            }
	            
	            ATDError event = new ATDError("Info", "Bad Scans", description, null, new Date(), null);
	            ATDService atdService = Context.getService(ATDService.class);
	            atdService.saveError(event);
            }
            catch (Exception e) {
	            log.error("Error moving bad scan", e);
	            throw e;
            }
        }
        
        private List<URL> getBadScans(File imageDirectory, List<URL> badScans, FilenameFilter fileFilter) {
    		File[] files = imageDirectory.listFiles(fileFilter);
    		if (files != null) {
    			for (File foundFile : files) {
    				if (foundFile.isDirectory()) {
    					getBadScans(foundFile, badScans, fileFilter);
    				} else {
	    				try {
	    					URI foundUri = foundFile.toURI();
	                        URL foundUrl = foundUri.toURL();
	                        badScans.add(foundUrl);
	                    }
	                    catch (MalformedURLException e) {
	                        log.error("Error converting file to URL", e);
	                    }
    				}
    			}
    		}
        	
        	return badScans;
        }
        
        /**
         * Query the mrf dump to find the list of immunizations for the patient
         * @see org.openmrs.module.chica.service.ChicaService#immunizationQuery(java.io.OutputStream, java.lang.Integer, java.lang.Integer, org.openmrs.Encounter, java.lang.Integer, java.lang.Integer)
         */
        public void immunizationQuery(OutputStream outputFile, Integer locationId,
    	                              Integer formId, org.openmrs.Encounter encounter,
    	                              Integer locationTagId, Integer sessionId) {
    		
    		Patient patient = encounter.getPatient();
    		
    		ATDService atdService = Context.getService(ATDService.class);
    		FormInstance formInstance = atdService.addFormInstance(formId, locationId);
    				    		
    		HashMap<String, Object> baseParameters = new HashMap<String, Object>();
    				
    		Integer encounterId = encounter.getEncounterId();
    		
    		//write the output to a string first so we can do some post-processing
    		ByteArrayOutputStream output = new ByteArrayOutputStream();
    		
    		//create an xml file from a form definition with all the immunization terms to look for
    		atdService.produce(patient, formInstance, output, encounterId, baseParameters, null, locationTagId,
    			sessionId);
    		
    		try {
    	        output.close();
            }
            catch (IOException e) {
    	        log.error("Error generated", e);
            }
            
            //transform the form xml into the immunization forecasting service input format
            AdministrationService adminService = Context.getAdministrationService();
            String xsltFilename = adminService.getGlobalProperty("chica.immunXSLT");
            ByteArrayOutputStream transformedOutput = new ByteArrayOutputStream();
            ByteArrayInputStream xmlInput = new ByteArrayInputStream(output.toByteArray());
            try {
    	        FileInputStream xslt = new FileInputStream(xsltFilename);
    	        
    	        XMLUtil.transformXML(xmlInput, transformedOutput, xslt, null);
    	        transformedOutput.close();
            }
            catch (FileNotFoundException e) {
    	        log.error("Error generated", e);
            }
            catch (IOException e) {
    	        log.error("Error generated", e);
            }
            String resultString = transformedOutput.toString();
            resultString = resultString.replaceAll("&lt;","<");
            resultString = resultString.replaceAll("&gt;",">");
            ByteArrayInputStream finalInput = new ByteArrayInputStream(resultString.getBytes());
            try {
    	        IOUtil.bufferedReadWrite(finalInput, outputFile);
    	        outputFile.close();
            }
            catch (IOException e) {
    	        log.error("Error generated", e);
            }
    	}
}