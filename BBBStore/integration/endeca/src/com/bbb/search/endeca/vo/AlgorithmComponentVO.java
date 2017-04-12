/**
 * 
 */
package com.bbb.search.endeca.vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author pku104
 *
 */
public class AlgorithmComponentVO implements Serializable {

	private static final long serialVersionUID = -5651755401143092236L;
	private String algorithmId;
	private String algorithmName;
	private Integer percentage;
	private String endecaProperty;
	private Integer algorithmType;
	private String algorithmDescription;
	private Timestamp lastModifiedDate;

	/**
	 * @return the algorithmId
	 */
	public String getAlgorithmId() {
		return algorithmId;
	}

	/**
	 * @param algorithmId the algorithmId to set
	 */
	public void setAlgorithmId(String algorithmId) {
		this.algorithmId = algorithmId;
	}

	/**
	 * @return the algorithmName
	 */
	public String getAlgorithmName() {
		return algorithmName;
	}

	/**
	 * @param algorithmName the algorithmName to set
	 */
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	/**
	 * @return the percentage
	 */
	public Integer getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage the percentage to set
	 */
	public void setPercentage(Integer percentage) {
		this.percentage = percentage;
	}

	/**
	 * @return the endecaProperty
	 */
	public String getEndecaProperty() {
		return endecaProperty;
	}

	/**
	 * @param endecaProperty the endecaProperty to set
	 */
	public void setEndecaProperty(String endecaProperty) {
		this.endecaProperty = endecaProperty;
	}

	/**
	 * @return the algorithmDescription
	 */
	public String getAlgorithmDescription() {
		return algorithmDescription;
	}

	/**
	 * @param algorithmDescription the algorithmDescription to set
	 */
	public void setAlgorithmDescription(String algorithmDescription) {
		this.algorithmDescription = algorithmDescription;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BoostingAlgorithmVO [algorithmId=" + algorithmId
				+ ", algorithmName=" + algorithmName + ", percentage="
				+ percentage + ", endecaProperty=" + endecaProperty
				+ ", algorithmDescription=" + algorithmDescription
				+ ", lastModifiedDate=" + lastModifiedDate + "]";
	}

	/**
	 * @return the algorithmType
	 */
	public Integer getAlgorithmType() {
		return algorithmType;
	}

	/**
	 * @param algorithmType the algorithmType to set
	 */
	public void setAlgorithmType(Integer algorithmType) {
		this.algorithmType = algorithmType;
	}

}
