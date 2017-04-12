package com.bbb.certona.vo;

import java.io.Serializable;
import java.sql.Timestamp;

public class SkuAttributesType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String attributeId;
	private String attributeDisplayName;
	private Timestamp attributeStartDate;
	private Timestamp attributeEndDate;
	private String placeHolder;
	private int priority;
	private String attrImageURL;
	private String attrActionURL;
	
	public String toString(){
		StringBuffer toString=new StringBuffer(" Attribute VO Details \n ");
	if(attributeId!=null){
			toString.append("Attribute Name ").append(attributeDisplayName).append("\n")
			.append("Attribute ID ").append(attributeId).append("\n")
			.append("Attribute Image URL ").append(attrImageURL).append("\n")
			.append("Attribute Place Holder ").append(placeHolder).append("\n")
			.append("Attribute priority ").append(priority).append("\n");
	}
	else{
		toString.append("Attribute Details are NULL Not Set");
	}
		
		return toString.toString();
	}
	/**
	 * @return the attributeId
	 */
	public String getAttributeId() {
		return attributeId;
	}
	/**
	 * @param pAttributeId the attributeId to set
	 */
	public void setAttributeId(String pAttributeId) {
		attributeId = pAttributeId;
	}
	/**
	 * @return the attributeDisplayName
	 */
	public String getAttributeDisplayName() {
		return attributeDisplayName;
	}
	/**
	 * @param pAttributeDisplayName the attributeDisplayName to set
	 */
	public void setAttributeDisplayName(String pAttributeDisplayName) {
		attributeDisplayName = pAttributeDisplayName;
	}
	/**
	 * @return the attributeStartDate
	 */
	public Timestamp getAttributeStartDate() {
		return attributeStartDate;
	}
	/**
	 * @param pAttributeStartDate the attributeStartDate to set
	 */
	public void setAttributeStartDate(Timestamp pAttributeStartDate) {
		attributeStartDate = pAttributeStartDate;
	}
	/**
	 * @return the attributeEndDate
	 */
	public Timestamp getAttributeEndDate() {
		return attributeEndDate;
	}
	/**
	 * @param pAttributeEndDate the attributeEndDate to set
	 */
	public void setAttributeEndDate(Timestamp pAttributeEndDate) {
		attributeEndDate = pAttributeEndDate;
	}
	/**
	 * @return the placeHolder
	 */
	public String getPlaceHolder() {
		return placeHolder;
	}
	/**
	 * @param pPlaceHolder the placeHolder to set
	 */
	public void setPlaceHolder(String pPlaceHolder) {
		placeHolder = pPlaceHolder;
	}
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param pPriority the priority to set
	 */
	public void setPriority(int pPriority) {
		priority = pPriority;
	}
	/**
	 * @return the attrImageURL
	 */
	public String getAttrImageURL() {
		return attrImageURL;
	}
	/**
	 * @param pAttrImageURL the attrImageURL to set
	 */
	public void setAttrImageURL(String pAttrImageURL) {
		attrImageURL = pAttrImageURL;
	}
	/**
	 * @return the attrActionURL
	 */
	public String getAttrActionURL() {
		return attrActionURL;
	}
	/**
	 * @param pAttrActionURL the attrActionURL to set
	 */
	public void setAttrActionURL(String pAttrActionURL) {
		attrActionURL = pAttrActionURL;
	}
	

}
