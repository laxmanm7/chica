package org.openmrs.module.chica.hibernateBeans;

import org.openmrs.BaseOpenmrsData;

/**
 * Holds information to store in the chica_study_attribute_value table
 * 
 * @author Tammy Dugan
 */
public class StudyAttributeValue extends BaseOpenmrsData implements java.io.Serializable {

	// Fields
	private Integer studyAttributeValueId = null;
	private Integer studyId = null;
	private Integer studyAttributeId = null;
	private String value = null;

	// Constructors

	/** default constructor */
	public StudyAttributeValue() {
	}
	
	public StudyAttributeValue(Integer studyId, String value, Integer studyAttributeId) {
		setStudyId(studyId);//9
		setValue(value);
		setStudyAttributeId(studyAttributeId);//1
	}

	/**
	 * @return the studyAttributeValueId
	 */
	public Integer getStudyAttributeValueId()
	{
		return this.studyAttributeValueId;
	}

	/**
	 * @param studyAttributeValueId the studyAttributeValueId to set
	 */
	public void setStudyAttributeValueId(Integer studyAttributeValueId)
	{
		this.studyAttributeValueId = studyAttributeValueId;
	}

	/**
	 * @return the studyId
	 */
	public Integer getStudyId()
	{
		return this.studyId;
	}

	/**
	 * @param studyId the studyId to set
	 */
	public void setStudyId(Integer studyId)
	{
		this.studyId = studyId;
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
	 * @return the value
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public Integer getId() {
		return getStudyAttributeValueId();
	}

	@Override
	public void setId(Integer id) {
		setStudyAttributeValueId(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((studyAttributeId == null) ? 0 : studyAttributeId.hashCode());
		result = prime * result + ((studyAttributeValueId == null) ? 0 : studyAttributeValueId.hashCode());
		result = prime * result + ((studyId == null) ? 0 : studyId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StudyAttributeValue other = (StudyAttributeValue) obj;
		if (studyAttributeId == null) {
			if (other.studyAttributeId != null)
				return false;
		} else if (!studyAttributeId.equals(other.studyAttributeId))
			return false;
		if (studyAttributeValueId == null) {
			if (other.studyAttributeValueId != null)
				return false;
		} else if (!studyAttributeValueId.equals(other.studyAttributeValueId))
			return false;
		if (studyId == null) {
			if (other.studyId != null)
				return false;
		} else if (!studyId.equals(other.studyId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StudyAttributeValue [studyAttributeValueId=" + studyAttributeValueId + ", studyId=" + studyId
				+ ", studyAttributeId=" + studyAttributeId + ", value=" + value + "]";
	}
	
	
}