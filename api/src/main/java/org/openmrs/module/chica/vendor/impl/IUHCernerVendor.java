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

import org.openmrs.module.chica.vendor.Vendor;


/**
 * IU Health Cerner vendor implementation for receiving data from external sources.
 * 
 * @author Steve McKee
 */
public class IUHCernerVendor extends VendorImpl implements Vendor {
	
	private static final String PARAM_PERSON = "person";
	private static final String PARAM_USER = "user";
	
	/**
	 * Constructor method
	 * 
	 * @param request HttpServletRequest object for accessing URL parameters.
	 */
	public IUHCernerVendor(HttpServletRequest request) {
		super(request);
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
}

