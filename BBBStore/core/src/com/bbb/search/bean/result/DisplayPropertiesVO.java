package com.bbb.search.bean.result;

import java.io.Serializable;


public class DisplayPropertiesVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String facetListStyle;
	private String fieldsetClass;
	private String offScreen;
	private String divClass;
	private String title;
	private String displayName;
	/**
	 * @return the facetListStyle
	 */
	public String getFacetListStyle() {
		return facetListStyle;
	}
	/**
	 * @param facetListStyle the facetListStyle to set
	 */
	public void setFacetListStyle(String facetListStyle) {
		this.facetListStyle = facetListStyle;
	}
	/**
	 * @return the fieldsetClass
	 */
	public String getFieldsetClass() {
		return fieldsetClass;
	}
	/**
	 * @param fieldsetClass the fieldsetClass to set
	 */
	public void setFieldsetClass(String fieldsetClass) {
		this.fieldsetClass = fieldsetClass;
	}
	/**
	 * @return the offScreen
	 */
	public String getOffScreen() {
		return offScreen;
	}
	/**
	 * @param offScreen the offScreen to set
	 */
	public void setOffScreen(String offScreen) {
		this.offScreen = offScreen;
	}
	/**
	 * @return the divClass
	 */
	public String getDivClass() {
		return divClass;
	}
	/**
	 * @param divClass the divClass to set
	 */
	public void setDivClass(String divClass) {
		this.divClass = divClass;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	
}