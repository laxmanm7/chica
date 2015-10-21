/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.vendor.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.vendor.Vendor;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;


/**
 * IU Health Cerner vendor implementation for receiving data from external sources.
 * 
 * @author Steve McKee
 */
public class IUHCernerVendor extends VendorImpl implements Vendor {
	
	private static Log log = LogFactory.getLog(IUHCernerVendor.class);
	
	private static final String PARAM_PERSON = "person";
	private static final String PARAM_USER = "user";
	private static final String PARAM_FORM_NAME_PWS = "PWS";
	private static final String PARAM_FORM_PAGE_IUH = "pwsIUHCerner.form";
	private static final String PARAM_START_STATE_PWS = "PWS_create";
	private static final String PARAM_END_STATE_PWS = "PWS_process";
	
	/**
	 * Constructor method
	 * 
	 * @param request HttpServletRequest object for accessing URL parameters.
	 */
	public IUHCernerVendor(HttpServletRequest request) {
		super(request);
	}
	
	@Override
    public String getEncryptionKey() {
		String key = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_IU_HEALTH_CERNER_ENCRYPTION_KEY);
		if (key == null || key.trim().length() == 0) {
			log.warn("Cannot find value for global property " + ChirdlUtilConstants.GLOBAL_PROP_IU_HEALTH_CERNER_ENCRYPTION_KEY + ".  Clear text "
					+ "value will be used.");
			return null;
		}
		
		return key;
    }
	
	/**
	 * @see org.openmrs.module.chica.vendor.impl.VendorImpl#getMrn()
	 */
	public String getMrn() {
		return request.getParameter(PARAM_PERSON);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.impl.VendorImpl#getProviderId()
	 */
	public String getProviderId() {
		return request.getParameter(PARAM_USER);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.impl.VendorImpl#getFormName()
	 */
	public String getFormName() {
		return PARAM_FORM_NAME_PWS;
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.impl.VendorImpl#getFormPage()
	 */
	public String getFormPage() {
		return PARAM_FORM_PAGE_IUH;
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.impl.VendorImpl#getStartState()
	 */
	public String getStartState() {
		return PARAM_START_STATE_PWS;
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.impl.VendorImpl#getEndState()
	 */
	public String getEndState() {
		return PARAM_END_STATE_PWS;
	}
}
