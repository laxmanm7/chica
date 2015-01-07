package org.openmrs.module.chica.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class ViewEncounterController extends SimpleFormController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject
	 * (javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return "testing";
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		String optionsString = request.getParameter("options");
		String patientIdString = request.getParameter("patientId");

		Integer patientId = null;

		try {
			if (patientIdString != null) {
				patientId = Integer.parseInt(patientIdString);
			}
		} catch (Exception e) {
		}

		String encounterIdString = request.getParameter("encounterId");
		Integer encounterId = null;
		try {
			if (encounterIdString != null && !encounterIdString.equals("")) {
				encounterId = Integer.parseInt(encounterIdString);
			}
		} catch (Exception e) {
		}

		if (optionsString != null) {

			if (patientId != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("encounterId", encounterId);
				map.put("patientId", patientIdString);
				StringTokenizer tokenizer = new StringTokenizer(optionsString,"_");
				Integer locationId = null;
				Integer formId = null;
				Integer formInstanceId = null;
				if(tokenizer.hasMoreTokens()){
					try
					{
						locationId = Integer.parseInt(tokenizer.nextToken());
					} catch (NumberFormatException e)
					{
					}
				}
				
				if(tokenizer.hasMoreTokens()){
					try
					{
						formId = Integer.parseInt(tokenizer.nextToken());
					} catch (NumberFormatException e)
					{
					}
				}
				
				if(tokenizer.hasMoreTokens()){
					try
					{
						formInstanceId = Integer.parseInt(tokenizer.nextToken());
					} catch (NumberFormatException e)
					{
					}
				}
				
				FormService formService = Context.getFormService();
				Form form = formService.getForm(formId);
				String formName = form.getName();
				ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
				Integer leftImageLocationId = null;
				Integer leftImageFormId = null;
				Integer leftImageFormInstanceId = null;
				String leftImageStylesheet = null;
				String rightImageStylesheet = null;
				boolean displayMergeForms = false;
				boolean displayScanForms = false;
				org.openmrs.api.EncounterService encounterService = Context.getEncounterService();
				org.openmrs.Encounter encounter = encounterService.getEncounter(encounterId);
				ChicaService chicaService = Context.getService(ChicaService.class);
				
				if (formName.equals("PSF") || formName.equals("ADHD P") || formName.equals("ADHD PS") ||
						formName.equals("MCHAT")|| formName.equals("ADHD PFU")|| formName.equals("ADHD PSFU") || 
						formName.equals("ParentSummaryReport") || formName.equals("ImmunizationSchedule7yrOrOlder") ||
						formName.equals("ImmunizationSchedule") || formName.equals("PHQ9_JIT_MOBILE")) {
					leftImageLocationId = locationId;
					leftImageFormId = formId;
					leftImageFormInstanceId = formInstanceId;
					if (formName.equals("ParentSummaryReport")) {
						leftImageStylesheet = "parentSummaryReport.xsl";
						displayMergeForms = true;
					} else if (formName.equals("PSF")) {
						leftImageStylesheet = "psf.xsl";
					} else if (formName.equals("PHQ9_JIT_MOBILE")) {
						leftImageStylesheet = "PHQ9_JIT_MOBILE.xsl";
						displayScanForms = true;
					} else if (formName.equals("MCHAT")) {
						leftImageStylesheet = "mchat.xsl";
					}
				} else {
					ArrayList<String> leftNames = new ArrayList<String>();
					
					if(formName.equals("ADHD T")){
						leftNames.add("ADHD P");
						leftNames.add("ADHD PS");
					}
					
					if(formName.equals("ADHD TFU")){
						leftNames.add("ADHD PFU");
						leftNames.add("ADHD PSFU");
					}
					
					if(formName.equals("PWS")){
						leftNames.add("PSF");
						leftImageStylesheet = "psf.xsl";
					}
					
					if (formName.equals("TeacherSummaryReport")) {
						leftNames.add("ParentSummaryReport");
						leftImageStylesheet = "parentSummaryReport.xsl";
						displayMergeForms = true;
					}
					
					if (formName.equals("ImmunizationSchedule")) {
						leftNames.add("ImmunizationSchedule");
						leftImageStylesheet = "ImmunizationSchedule.xsl";
						displayMergeForms = true;
					}
					
					if (formName.equals("ImmunizationSchedule7yrOrOlder")) {
						leftNames.add("ImmunizationSchedule7yrOrOlder");
						leftImageStylesheet = "ImmunizationSchedule7yrOrOlder.xsl";
						displayMergeForms = true;
					}
					
					for (String leftName : leftNames) {
						List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(leftName,
						    encounterId);
						if (patientStates != null && !patientStates.isEmpty()) {
							
							for (PatientState currState : patientStates) {
								if (currState.getEndTime() != null) {
									leftImageLocationId = currState.getLocationId();
									leftImageFormId = currState.getFormId();
									leftImageFormInstanceId = currState.getFormInstanceId();
									break;
								}
							}
						} else {
							if (leftName!=null&&leftName.equals("PSF")) {
								Chica1Appointment chica1Appt = chicaService.getChica1AppointmentByEncounterId(encounterId);
								
								if (chica1Appt != null) {
									leftImageFormInstanceId = chica1Appt.getApptPsfId();
									leftImageLocationId = encounter.getLocation().getLocationId();
									
									if (leftImageFormInstanceId != null) {
										form = formService.getForm("PSF");
										if (form != null) {
											leftImageFormId = form.getFormId();
										}
									}
								}
							}
						}
					}
				}
				
				Integer rightImageLocationId = null;
				Integer rightImageFormId = null;
				Integer rightImageFormInstanceId = null;
				
				//don't set a right image for MCHAT
				if (!formName.equals("MCHAT") && !formName.equals("ImmunizationSchedule7yrOrOlder")
						&& !formName.equals("ImmunizationSchedule") && !formName.endsWith("PHQ9_JIT_MOBILE")) {
					if (formName.equals("PWS") || formName.equals("ADHD T")|| formName.equals("ADHD TFU") || 
							formName.equals("TeacherSummaryReport")) {
						rightImageLocationId = locationId;
						rightImageFormId = formId;
						rightImageFormInstanceId = formInstanceId;
						if (formName.equals("TeacherSummaryReport")) {
							rightImageStylesheet = "teacherSummaryReport.xsl";
							displayMergeForms = true;
						} else if (formName.equals("PWS")) {
							rightImageStylesheet = "pws.xsl";
						}
					} else {
						
						String rightName = null;
						
						if (formName.equals("ADHD P")) {
							rightName = "ADHD T";
						}
						
						if (formName.equals("ADHD PFU")) {
							rightName = "ADHD TFU";
						}
						
						if (formName.equals("ADHD PS")) {
							rightName = "ADHD T";
						}
						
						if (formName.equals("ADHD PSFU")) {
							rightName = "ADHD TFU";
						}
						
						if (formName.equals("PSF")) {
							rightName = "PWS";
							rightImageStylesheet = "pws.xsl";
						}
						
						if (formName.equals("ParentSummaryReport")) {
							rightName = "TeacherSummaryReport";
							rightImageStylesheet = "teacherSummaryReport.xsl";
							displayMergeForms = true;
						}
						
						List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(rightName,
						    encounterId);
						if (patientStates != null && !patientStates.isEmpty()) {
							
							for (PatientState currState : patientStates) {
								if (currState.getEndTime() != null) {
									rightImageLocationId = currState.getLocationId();
									rightImageFormId = currState.getFormId();
									rightImageFormInstanceId = currState.getFormInstanceId();
									break;
								}
							}
						} else {
							if (rightName != null && rightName.equals("PWS")) {
								Chica1Appointment chica1Appt = chicaService.getChica1AppointmentByEncounterId(encounterId);
								
								if (chica1Appt != null) {
									rightImageFormInstanceId = chica1Appt.getApptPwsId();
									rightImageLocationId = encounter.getLocation().getLocationId();
									
									if (rightImageFormInstanceId != null) {
										form = formService.getForm("PWS");
										if (form != null) {
											rightImageFormId = form.getFormId();
										}
									}
								}
							}
						}
					}
				}
							
					map.put("rightImageLocationId", rightImageLocationId);
					map.put("rightImageFormId", rightImageFormId);
					map.put("rightImageFormInstanceId", rightImageFormInstanceId);
					map.put("rightImageStylesheet", rightImageStylesheet);
					map.put("leftImageLocationId", leftImageLocationId);
					map.put("leftImageFormId", leftImageFormId);
					map.put("leftImageFormInstanceId", leftImageFormInstanceId);
					map.put("leftImageStylesheet", leftImageStylesheet);
					
					if (displayMergeForms) {
						return new ModelAndView(new RedirectView("displayMergeForm.form"), map);
					} else if (displayScanForms) {
						return new ModelAndView(new RedirectView("displayScanForm.form"), map);
					}

					return new ModelAndView(new RedirectView("displayTiff.form"), map);
			}

		}
		return new ModelAndView(new RedirectView("viewEncounter.form"));

	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);		
		FormService formService = Context.getFormService();
		ChicaService chicaService = Context.getService(ChicaService.class);
		PatientService patientService = Context
				.getService(PatientService.class);
		Map<String, Object> map = new HashMap<String, Object>();
		HashMap<Integer,String> formNameMap = new HashMap<Integer,String>();

		try {

			String pidparam = request.getParameter("patientId");
			Patient patient = null;

			if (pidparam == null || pidparam.trim().length()==0) {
				String mrn = request.getParameter("mrn");
				if (mrn != null && mrn.trim().length() > 0) {
					mrn = Util.removeLeadingZeros(mrn);
					PatientIdentifierType identifierType = patientService
							.getPatientIdentifierTypeByName("MRN_OTHER");
					List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
					identifierTypes.add(identifierType);
					List<Patient> patients = patientService.getPatients(null, mrn,
							identifierTypes,true);
					if (patients.size() == 0){
						patients = patientService.getPatients(null, "0" + mrn,
								identifierTypes,true);
					}

					if (patients.size() > 0)
					{
						patient = patients.get(0);
					}
				}
			} else {
				patient = patientService.getPatient(Integer.valueOf(pidparam));
			}
			
			if (patient == null) {
				return map;
			}
			
			// title name, mrn, and dob
			String dobString = "";
			Date dob = patient.getBirthdate();
			if (dob != null) {
				dobString = new SimpleDateFormat("yyyy-MM-dd").format(dob);
			}
			map.put("titleMRN", patient.getPatientIdentifier());
			map.put("titleLastName", patient.getFamilyName());
			map.put("titleFirstName", patient.getGivenName());
			map.put("titleDOB", dobString);

			// encounter rows
			EncounterService encounterService = Context
					.getService(EncounterService.class);
			List<org.openmrs.Encounter> list = encounterService
					.getEncountersByPatientId(patient.getPatientId());
			List<PatientRow> rows = new ArrayList<PatientRow>();

			ArrayList<String> formsToProcess = new ArrayList<String>();
			formsToProcess.add("PSF");
			formsToProcess.add("PWS");
			formsToProcess.add("ADHD P");
			formsToProcess.add("ADHD PS");
			formsToProcess.add("ADHD T");
			formsToProcess.add("MCHAT");
			formsToProcess.add("ADHD PFU");
			formsToProcess.add("ADHD TFU");
			formsToProcess.add("ADHD PSFU");
			formsToProcess.add("ParentSummaryReport");
			formsToProcess.add("TeacherSummaryReport");
			formsToProcess.add("ImmunizationSchedule");
			formsToProcess.add("ImmunizationSchedule7yrOrOlder");
			formsToProcess.add("PHQ9_JIT_MOBILE");
			
			String firstName = null;
			String lastName = null;
			String mrn = null;
			if (patient != null) {
				firstName = patient.getGivenName();
				lastName = patient.getFamilyName();
				PatientIdentifier pi = patient.getPatientIdentifier();
				if (pi != null) {
					mrn = pi.getIdentifier();	
				}
			}
			for (org.openmrs.Encounter enc : list) {
				Integer encounterId = enc.getEncounterId();
				PatientRow row = new PatientRow();
				row.setLastName(lastName);
				row.setMrn(mrn);
				Encounter enct = (Encounter) encounterService
						.getEncounter(encounterId);
				if (enct == null)
					continue;

				String apptDateString = "";
				Date appt = enct.getScheduledTime();
				if (appt != null) {
					apptDateString = new SimpleDateFormat("MMM dd, yyyy")
							.format(appt);
				}

				// Set checkin date text
				Date checkin = enct.getEncounterDatetime();

				String checkinDateString = "";

				if (checkin != null) {
					if (Util.isToday(checkin)) {
						checkinDateString = "Today";
					} else {
						checkinDateString = new SimpleDateFormat("MMM dd, yyyy")
								.format(checkin);
					}
				}

				// Calculate age at visit
				UserService userService = Context.getUserService();
				List<User> providers = userService.getUsersByPerson(enct.getProvider(), true);
				User provider = null;
				if(providers != null&& providers.size()>0){
					provider = providers.get(0);
				}
				String providerName = getProviderName(provider);
				String station = enct.getPrinterLocation();
				

				row.setFirstName(firstName);
				row.setMdName(providerName);
				row.setAppointment(apptDateString);
				row.setCheckin(checkinDateString);
				row.setEncounter(enct);
				row.setStation(station);
				row.setAgeAtVisit(org.openmrs.module.chirdlutil.util.Util
						.adjustAgeUnits(dob, checkin));

				// psf, pws form ids
							
					List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(null,encounterId);
					if (patientStates != null && !patientStates.isEmpty()) {
						for (PatientState currState : patientStates) {
							if (currState.getEndTime() != null) {
								Integer formId = currState.getFormId();
								String formName = formNameMap.get(formId);
								if(formName == null){
									Form form = formService.getForm(formId);
									formName = form.getName();
									formNameMap.put(formId, formName);
								}
								
							//make sure you only get the most recent psf/pws pair
							if (row.getPsfId() == null) {
								if (formName.equals("PSF")) {
									row.setPsfId(currState.getFormInstance());
								}
							}
							if (row.getPwsId() == null) {
								if (formName.equals("PWS")) {
									row.setPwsId(currState.getFormInstance());
								}
							}
						
							if (formsToProcess.contains(formName)) {							
									row.addFormInstance(currState.getFormInstance());
								}
							}
						}
					}
			
				
				//get CHICA 1 PSF and PWS ids
				Chica1Appointment chica1Appt = chicaService
					.getChica1AppointmentByEncounterId(encounterId);
				
				if (chica1Appt != null)
				{
					Integer psfId = chica1Appt.getApptPsfId();
					Integer pwsId = chica1Appt.getApptPwsId();
					Integer locationId = enct.getLocation().getLocationId();
					
					
					if(psfId != null){
						Form form = formService.getForm("PSF");
						Integer formId = null;
						if (form != null)
						{
							formId = form.getFormId();
						}
						FormInstance psfFormInstance = new FormInstance();
						psfFormInstance.setFormInstanceId(psfId);
						psfFormInstance.setFormId(formId);
						psfFormInstance.setLocationId(locationId);
						row.setPsfId(psfFormInstance);
						formNameMap.put(formId, "PSF");
						row.addFormInstance(psfFormInstance);
					}
					
					if(pwsId != null){
						Form form = formService.getForm("PWS");
						Integer formId = null;
						if (form != null)
						{
							formId = form.getFormId();
						}
						FormInstance pwsFormInstance = new FormInstance();
						pwsFormInstance.setFormInstanceId(pwsId);
						pwsFormInstance.setFormId(formId);
						pwsFormInstance.setLocationId(locationId);
						row.setPwsId(pwsFormInstance);
						formNameMap.put(formId, "PWS");
						row.addFormInstance(pwsFormInstance);
					}
				}

				// From the encounter, get the obs for weight percentile
				//String result = searchEncounterForObs(enct, "WTCENTILE");
				//row.setWeightPercentile(result);
				row.setWeightPercentile("");
				
				//result = searchEncounterForObs(enct, "HTCENTILE");
				//row.setHeightPercentile(result);
				row.setHeightPercentile("");

				FormInstance pwsId = row.getPwsId();
				if (pwsId != null) {
					State currState = chirdlutilbackportsService.getStateByName("PWS_wait_to_scan");
					List<PatientState> pwsScanStates = chirdlutilbackportsService.getPatientStateByFormInstanceState(pwsId,
					    currState,true);
					if (pwsScanStates != null && pwsScanStates.size() > 0) {
						PatientState pwsScanState = pwsScanStates.get(0);
						if (pwsScanState.getEndTime() != null) {
							row.setPwsScanned(true);
						}
					}
				}
				rows.add(row);
			}

			

			map.put("patientRows", rows);
			map.put("formNameMap",formNameMap);

		} catch (UnexpectedRollbackException ex) {
			// ignore this exception since it happens with an
			// APIAuthenticationException
		} catch (APIAuthenticationException ex2) {
			// ignore this exception. It happens during the redirect to the
			// login page
		}

		return map;
	}

	private String getProviderName(User prov) {
		String mdName = "";
		if (prov != null) {
			String firstInit = Util.toProperCase(prov.getGivenName());
			if (firstInit != null && firstInit.length() > 0) {
				firstInit = firstInit.substring(0, 1);
			} else {
				firstInit = "";
			}

			String middleInit = Util.toProperCase(prov.getPerson().getMiddleName());
			if (middleInit != null && middleInit.length() > 0) {
				middleInit = middleInit.substring(0, 1);
			} else {
				middleInit = "";
			}
			if (firstInit != null && firstInit.length() > 0) {
				mdName += firstInit + ".";
				if (middleInit != null && middleInit.length() > 0) {
					mdName += " " + middleInit + ".";
				}
			}
			if (mdName.length() > 0) {
				mdName += " ";
			}
			String familyName = Util.toProperCase(prov.getFamilyName());
			if (familyName == null) {
				familyName = "";
			}
			mdName += familyName;

		}

		return mdName;

	}

	private String searchEncounterForObs(Encounter enc, String conceptName) {
		String name = "N/A";

		Set<Obs> allObs = enc.getObs();
		ConceptService conceptService = Context.getConceptService();
		if (allObs != null) {
			for (Obs obs : allObs) {
				Concept obsConcept = obs.getConcept();
				Concept wtcentileConcept = conceptService
						.getConceptByName(conceptName);
				if (obsConcept != null
						&& wtcentileConcept != null
						&& wtcentileConcept.getConceptId().equals(obsConcept
								.getConceptId())) {
					Double wtcentile = obs.getValueNumeric();
					if (wtcentile != null) {
						name = wtcentile.toString();
					}
				}
			}
		}
		return name;
	}

}
