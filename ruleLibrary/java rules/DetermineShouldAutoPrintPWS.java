package org.openmrs.module.chica.rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

/**
 * Determines if the PWS PDF should be auto-printed.
 * Will be auto-printed if the PSF and vitals have been submitted OR this is a force-print from the GreaseBoard
 */
public class DetermineShouldAutoPrintPWS implements Rule
{
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException 
	{
		Object psfSubmittedObj = parameters.get(ChirdlUtilConstants.PARAMETER_1);
		Object vitalsProcessedObj = parameters.get(ChirdlUtilConstants.PARAMETER_2);
		Object printedFromGreaseBoardObj = parameters.get(ChirdlUtilConstants.PARAMETER_3);
		
		String psfSubmitted = "";
		String vitalsProcessed = "";
		String printedFromGreaseBoard = "";
		
		if (psfSubmittedObj != null && psfSubmittedObj instanceof String)
		{
			psfSubmitted = (String) psfSubmittedObj;
		}
		
		if (vitalsProcessedObj != null && vitalsProcessedObj instanceof String)
		{
			vitalsProcessed = (String) vitalsProcessedObj;
		}
		
		if (printedFromGreaseBoardObj != null && printedFromGreaseBoardObj instanceof String)
		{
			printedFromGreaseBoard = (String) printedFromGreaseBoardObj;
		}
	
		// Auto print if the PSF AND vitals have been submitted OR this is a force print from the GreaseBoard
		if((psfSubmitted.equalsIgnoreCase(ChirdlUtilConstants.GENERAL_INFO_TRUE) && vitalsProcessed.equalsIgnoreCase(ChirdlUtilConstants.GENERAL_INFO_TRUE)) || (printedFromGreaseBoard.equalsIgnoreCase(ChirdlUtilConstants.GENERAL_INFO_TRUE)))
		{
			return new Result(ChirdlUtilConstants.GENERAL_INFO_TRUE);
		}
		else
		{
			return new Result(ChirdlUtilConstants.GENERAL_INFO_FALSE);
		}
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[]{};
	}

	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return new HashSet<RuleParameterInfo>();
	}

	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0;
	}
}