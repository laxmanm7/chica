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
package org.openmrs.module.chica.hibernateBeans;

import java.util.UUID;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for StudyAttribute.
 * 
 * @author Seema Sarala
 */
public class TestStudyAttribute extends TestCase {
	
	private static final Integer studyAttributeId = 3;
	private String name = "TestName";
	private String description = "Test Study Attribute";
	public static final String STUDY_ATTRBUTE_FILE = "dbunitFiles/studyAttributeTest.xml";
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestStudyAttribute(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(TestStudyAttribute.class);
    }

    /**
     * Test setting/getting variables
     */
    public void testSettersGetters() {
    	StudyAttribute studyAttribute = createStudyAttribute();
    	assertEquals(studyAttributeId, studyAttribute.getStudyAttributeId());
    	assertEquals(studyAttributeId, studyAttribute.getId());
    	assertEquals(name, studyAttribute.getName());
    	assertEquals(description, studyAttribute.getDescription());
    }
    
    /**
     * Test equality
     */
    public void testEquals() {
    	// The OpenMRS equals requires the UUID to match.
    	UUID uuid = UUID.randomUUID();
    	StudyAttribute studyAttribute = createStudyAttribute();
    	studyAttribute.setUuid(uuid.toString());
    	
    	StudyAttribute studyAttribute1 = createStudyAttribute();
    	studyAttribute1.setUuid(uuid.toString());
    	
    	assertEquals(studyAttribute, studyAttribute1);
    }
    
    /**
     * Test hashCode
     */
    public void testHashCode() {
    	// The OpenMRS equals requires the UUID to match.
    	UUID uuid = UUID.randomUUID();
    	StudyAttribute studyAttribute = createStudyAttribute();
    	studyAttribute.setUuid(uuid.toString());
    	
    	StudyAttribute studyAttribute1 = createStudyAttribute();
    	studyAttribute1.setUuid(uuid.toString());
    	
    	
    	assertEquals(studyAttribute.hashCode(), studyAttribute1.hashCode());
    }
    
    /**
     * Create a StudyAttribute object.
     * 
     * @return StudyAttribute object.
     */
    private StudyAttribute createStudyAttribute() {
    	StudyAttribute studyAttribute = new StudyAttribute(name, description);
    	studyAttribute.setStudyAttributeId(studyAttributeId);
    	
    	return studyAttribute;
    }
}

