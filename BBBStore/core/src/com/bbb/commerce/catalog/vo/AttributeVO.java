package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

import com.bbb.commerce.catalog.BBBCatalogConstants;

import atg.repository.RepositoryItem;
/**
 * 
 * @author njai13
 *
 */
public class AttributeVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String attributeName; 
	private String attributeDescrip;
	private String imageURL;
	private String actionURL;

	private String placeHolder;
	private int priority;
	private RepositoryItem attributeRepositoryItems;
	private String intlProdAttr;
	private String skuAttributeId;
	private boolean hideAttribute;
	
	public boolean isHideAttribute() {
		return hideAttribute;
	}
	public void setHideAttribute(boolean hideAttribute) {
		this.hideAttribute = hideAttribute;
	}
	
	public String getSkuAttributeId() {
		return skuAttributeId;
	}
	public void setSkuAttributeId(String skuAttributeId) {
		this.skuAttributeId = skuAttributeId;
	}
	public AttributeVO() {
		// TODO Auto-generated constructor stub
	}
	public String getIntlProdAttr() {
		return intlProdAttr;
	}
	public void setIntlProdAttr(String intlProdAttr) {
		this.intlProdAttr = intlProdAttr;
	}
	public AttributeVO(	RepositoryItem attributeRepositoryItems) {
		if(attributeRepositoryItems!=null){
			this.attributeRepositoryItems=attributeRepositoryItems;

		}
	}
	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		if(attributeRepositoryItems!=null){
			return (String) attributeRepositoryItems.getRepositoryId();
		}
		else{
			return this.attributeName;
		}
	}

	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return the attributeDescrip
	 */
	public String getAttributeDescrip() {
		if(attributeRepositoryItems!=null &&  this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)!=null)
		{
			return (String) this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME);
		}
		else
		{
			return this.attributeDescrip;
		}
	}

	/**
	 * @param attributeDescrip the attributeDescrip to set
	 */
	public void setAttributeDescrip(String attributeDescrip) {
		this.attributeDescrip = attributeDescrip;
	}

	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		if(attributeRepositoryItems!=null && this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME)!=null)
		{
			return (String) this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME);
		}
		else
		{
			return this.imageURL;
		}
	}

	/**
	 * @param imageURL the imageURL to set
	 */
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	/**
	 * @return the actionURL
	 */
	public String getActionURL() {
		if(attributeRepositoryItems!=null &&  
				this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME)!=null)
		{
			return (String) this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME);
		}
		else
		{
			return this.actionURL;
		}
	}

	/**
	 * @param actionURL the actionURL to set
	 */
	public void setActionURL(String actionURL) {
		this.actionURL = actionURL;
	}


	/**
	 * @return the placeHolder
	 */
	public String getPlaceHolder() {
		if(attributeRepositoryItems!=null &&
				this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME)!=null)
		{
			return (String) this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.PLACE_HOLDER_ATTRIBUTE_PROPERTY_NAME);
		}
		else
		{
			return this.placeHolder;
		}
	}
	/**
	 * @param placeHolder the placeHolder to set
	 */
	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}
	public int getPriority() {
		if(attributeRepositoryItems!=null &&  this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.PRIORITY_ATTRIBUTE_PROPERTY_NAME)!=null){
			return ((Integer) this.attributeRepositoryItems.getPropertyValue(BBBCatalogConstants.PRIORITY_ATTRIBUTE_PROPERTY_NAME)).intValue();
		} else {
			return this.priority;
		}
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public String toString(){
		StringBuffer toString=new StringBuffer(" Attribute VO Details \n ");
		if(attributeRepositoryItems!=null){
			toString.append("Attribute Name ").append(attributeName).append("\n")
			.append("Attribute Description ").append(attributeDescrip).append("\n")
			.append("Attribute Image URL ").append(imageURL).append("\n")
			.append("Attribute Place Holder ").append(placeHolder).append("\n")
			.append("Attribute priority ").append(priority).append("\n");
		}
		else{
			toString.append("Attribute Details are NULL Not Set");
		}
		return toString.toString();
	}
	
	
    public boolean equals(Object obj){

        if (obj instanceof AttributeVO) {
        	AttributeVO pAttributeVO = (AttributeVO) obj;
        	
        	this.attributeRepositoryItems.getRepositoryId();
            return (pAttributeVO.getAttributeName().equals(this.getAttributeName()) 
            		&& pAttributeVO.getPlaceHolder().equals(this.getPlaceHolder()));
        } else {
            return false;
        }
    }
    
    public int hashCode(){
    	
    	if( this.attributeRepositoryItems!=null ){
    		return this.attributeRepositoryItems.getRepositoryId().hashCode();
    	}else{
    		return super.hashCode();
    	}
    }
}
