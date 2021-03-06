package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

/**
 * Rule to append only an apostrophe if the patient first name ends in an s
 * Appends 's if not
 *
 */
public class getPossessiveName implements Rule {
	
	private static final String CHAR_APOSTROPHE = "'";
	private static final String CHAR_S = "s";
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Patient patient = Context.getPatientService().getPatient(patientId);
		String firstName = patient.getGivenName();
		
		if(StringUtils.isBlank(firstName))
		{
			return Result.emptyResult();
		}
		
		if(firstName.endsWith(CHAR_S))
		{
			firstName += CHAR_APOSTROPHE;
		}
		else
		{
			firstName += CHAR_APOSTROPHE + CHAR_S;
		}
		
		return new Result(firstName);
	}
}
