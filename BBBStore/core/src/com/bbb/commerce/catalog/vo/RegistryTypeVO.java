/**
 * 
 */
package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

import com.bbb.commerce.catalog.BBBCatalogConstants;

import atg.repository.RepositoryItem;

/**
 * 
 * @author njai13
 *
 */
public class RegistryTypeVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String registryTypeId;
	private String registryName;
	private String registryCode;
	private String registryDescription;
	private int registryIndex;
	private RepositoryItem repositoryItem;

	public RegistryTypeVO() {
		// TODO Auto-generated constructor stub
	}

	public RegistryTypeVO(RepositoryItem repositoryItem) {
		this.repositoryItem=repositoryItem;
	}

	/**
	 * @return the registryTypeId
	 */
	public String getRegistryTypeId() {
		if(repositoryItem!=null){
			return (String)repositoryItem.getRepositoryId();
		}
		else
		{
			return this.registryTypeId;
		}
	}

	/**
	 * @param pRegistryTypeId the registryTypeId to set
	 */
	public void setRegistryTypeId(String pRegistryTypeId) {
		registryTypeId = pRegistryTypeId;
	}

	/**
	 * @return the registryName
	 */
	public String getRegistryName() {
		if(repositoryItem!=null && repositoryItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_NAME)!=null){
			return (String)repositoryItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_NAME);
		}
		else
		{
			return this.registryName;
		}
	}

	/**
	 * @param pRegistryName the registryName to set
	 */
	public void setRegistryName(String pRegistryName) {

		registryName = pRegistryName;
	}

	/**
	 * @return the registryName
	 */
	public String getRegistryCode() {
		if(repositoryItem!=null && repositoryItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_CODE)!=null){
			return (String)repositoryItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_CODE);
		}
		else
		{
			return this.registryCode;
		}
	}

	/**
	 * @param pRegistryName the registryName to set
	 */
	public void setRegistryCode(String pRegistryCode) {

		registryCode = pRegistryCode;
	}

	/**
	 * @return the registryDescription
	 */
	public String getRegistryDescription() {
		if(repositoryItem!=null && repositoryItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_DESC_REGISTRY_PROPERTY_NAME)!=null){
			return  (String)repositoryItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_DESC_REGISTRY_PROPERTY_NAME);
		}
		else{
			return this.registryDescription;
		}
	}

	/**
	 * @param pRegistryDescription the registryDescription to set
	 */
	public void setRegistryDescription(String pRegistryDescription) {
		registryDescription = pRegistryDescription;
	}

	/**
	 * @return the registryIndex
	 */
	public int getRegistryIndex() {
		if(repositoryItem!=null && repositoryItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_INDEX_REGISTRY_PROPERTY_NAME)!=null){
		return Integer.parseInt((String) repositoryItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_INDEX_REGISTRY_PROPERTY_NAME));
		}
		else{
			return this.registryIndex;
		}
	}

	/**
	 * @param pRegistryIndex the registryIndex to set
	 */
	public void setRegistryIndex(int pRegistryIndex) {
		registryIndex = pRegistryIndex;
	}

	public String toString(){
		StringBuffer toString=new StringBuffer(" Registry Type VO Details \n ");
		if(repositoryItem!=null){
			toString.append("Registry Type Id  ").append(registryTypeId).append("\n")
			.append(" Registry Name  ").append(registryName).append("\n")
			.append(" Registry Code ").append(registryCode).append("\n")
			.append(" Registry Description  ").append(registryDescription).append("\n")
			.append(" Registry Index  ").append(registryIndex).append("\n");
		}
		else{
			toString.append(" Registry Type Details not set/available ");
		}
		return toString.toString();
	}

}
