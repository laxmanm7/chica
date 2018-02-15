package org.openmrs.module.chica.hibernateBeans;

import org.openmrs.BaseOpenmrsData;

/**
 * Holds information to store in the chica_study_attribute table
 * 
 * @author Tammy Dugan
 */
public class StudyAttribute extends BaseOpenmrsData implements java.io.Serializable {

	// Fields
	private Integer studyAttributeId = null;
	private String name = null;
	private String description = null;


	// Constructors

	/** default constructor */
	public StudyAttribute() {
		
	}
	
	public StudyAttribute(String name, String description) {
		setName(name);
		setDescription(description);
	}


	/**
	 * @return the studyAttributeId
	 */
	public Integer getStudyAttributeId()
	{
		return this.studyAttributeId;
	}


	/**
	 * @param studyAttributeId the studyAttributeId to set
	 */
	public void setStudyAttributeId(Integer studyAttributeId)
	{
		this.studyAttributeId = studyAttributeId;
	}


	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}


	@Override
	public Integer getId() {
		return getStudyAttributeId();
	}


	@Override
	public void setId(Integer id) {
		setStudyAttributeId(id);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((studyAttributeId == null) ? 0 : studyAttributeId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudyAttribute other = (StudyAttribute) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (studyAttributeId == null) {
			if (other.studyAttributeId != null)
				return false;
		} else if (!studyAttributeId.equals(other.studyAttributeId))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "StudyAttribute [studyAttributeId=" + studyAttributeId + ", name=" + name + ", description="
				+ description + "]";
	}
	
	
}