package org.openmrs.module.chica.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * @author Tammy Dugan
 *
 */
public class AdminList extends AdministrationSectionExt {

	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	@Override
	public String getTitle() {
		return "chica.title";
	}
	
	@Override
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("module/chica/testCheckin.form", "Test checkin through AOP");
		map.put("module/chica/parseDictionary.form", "Parse dictionary file");
		map.put("module/chica/loadObs.form", "Load old obs");
		map.put("module/chica/fillOutPSF.form", "Scan PSF");
		map.put("module/chica/fillOutPWS.form", "Scan PWS");
		map.put("module/chica/greaseBoard.form", "Grease Board");
		map.put("module/chica/viewPatient.form", "View Encounters");
		map.put("module/chica/ruleTester.form", "Rule Tester");
		return map;
	}
	
}