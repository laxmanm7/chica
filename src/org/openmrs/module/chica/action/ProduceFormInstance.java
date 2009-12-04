/**
 * 
 */
package org.openmrs.module.chica.action;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.ChicaStateActionHandler;
import org.openmrs.module.chica.datasource.ObsChicaDatasource;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chica.hibernateBeans.Statistics;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.IOUtil;

/**
 * @author tmdugan
 * 
 */
public class ProduceFormInstance implements ProcessStateAction
{
	private static Log log = LogFactory.getLog(ChicaStateActionHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction,
	 *      org.openmrs.Patient,
	 *      org.openmrs.module.atd.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
			throws Exception
	{
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		ChicaService chicaService = Context
				.getService(ChicaService.class);
		ATDService atdService = Context
				.getService(ATDService.class);
		ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);

		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		
		Session session = atdService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		LocationTagAttributeValue locTagAttrValue = 
			chirdlUtilService.getLocationTagAttributeValue(locationTagId, currState.getFormName(), locationId);
		
		Integer formId = null;
		
		if(locTagAttrValue != null){
			String value = locTagAttrValue.getValue();
			if(value != null){
				try
				{
					formId = Integer.parseInt(value);
				} catch (Exception e)
				{
				}
			}
		}
		
		if(formId == null){
			//open an error state
			currState = atdService.getStateByName("ErrorState");
			atdService.addPatientState(patient,
					currState, sessionId,locationTagId,locationId);
			log.error(currState.getFormName()+
					" locationTagAttribute does not exist for locationTagId: "+
					locationTagId+" locationId: "+locationId);
			return;
		}
		
		// write the form
		FormInstance formInstance = atdService.addFormInstance(formId,
				locationId);
		patientState.setFormInstance(formInstance);
		Integer formInstanceId = formInstance.getFormInstanceId();
		String mergeDirectory = IOUtil
				.formatDirectoryName(org.openmrs.module.atd.util.Util
						.getFormAttributeValue(formId, "pendingMergeDirectory",
								locationTagId, locationId));

		String mergeFilename = mergeDirectory + formInstance.toString() + ".xml";
		int maxDssElements = org.openmrs.module.chica.util.Util
				.getMaxDssElements(formId, locationTagId, locationId);
		String dsstype = org.openmrs.module.chica.util.Util
				.getDssType(currState.getName());
		FileOutputStream output = new FileOutputStream(mergeFilename);
		chicaService.produce(output, patientState, patient, encounterId,
				dsstype, maxDssElements, sessionId);
		output.flush();
		output.close();
		LogicService logicService = Context.getLogicService();

		ObsChicaDatasource xmlDatasource = (ObsChicaDatasource) logicService
				.getLogicDataSource("RMRS");

		// clear the in-memory obs from the MRF dump at the end of each
		// produce state
		xmlDatasource.deleteRegenObsByPatientId(patientId);

		StateManager.endState(patientState);
		ChicaStateActionHandler.changeState(patient, sessionId, currState, stateAction, parameters,
				locationTagId, locationId);

		// update statistics
		List<Statistics> statistics = chicaService.getStatByFormInstance(
				formInstanceId, dsstype, locationId);

		for (Statistics currStat : statistics)
		{
			currStat.setPrintedTimestamp(patientState.getEndTime());
			chicaService.updateStatistics(currStat);
		}
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}