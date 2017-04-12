package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

import com.bbb.commerce.catalog.BBBCatalogConstants;

import atg.repository.RepositoryItem;

public class TabVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tabName;
	private String tabContent;

	private RepositoryItem tabRepositoryItem;
	public TabVO() {
		// TODO Auto-generated constructor stub
	}
	
	public TabVO(RepositoryItem tabRepositoryItem) {
		this.tabRepositoryItem=tabRepositoryItem;
	}


	/**
	 * @return the tabName
	 */
	public String getTabName() {
	if(tabRepositoryItem!=null && tabRepositoryItem.getPropertyValue(BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME)!=null){
		return (String) tabRepositoryItem.getPropertyValue(BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME);
	}
	else{
		return this.tabName;
	}
	}

	/**
	 * @param tabName the tabName to set
	 */
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	/**
	 * @return the tabContent
	 */
	public String getTabContent() {
		if(tabRepositoryItem!=null && tabRepositoryItem.getPropertyValue(BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME)!=null){
			return (String) tabRepositoryItem.getPropertyValue(BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME);
		}
		else{
			return this.tabContent;
		}
	}

	/**
	 * @param tabContent the tabContent to set
	 */
	public void setTabContent(String tabContent) {
		this.tabContent = tabContent;
	}
	public String toString(){
		StringBuffer toString=new StringBuffer(" Tab VO Details \n ");
		if(tabRepositoryItem!=null){
			toString.append("Tab Id  ").append(tabName).append("\n")
			.append(" Tab Content  ").append(tabContent).append("\n");
		}
		else{
			toString.append(" Tab Details not set/available ");
		}
		return toString.toString();
	}

}
