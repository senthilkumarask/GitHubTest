package com.bbb.cms;

import java.io.Serializable;
import java.util.Map;

 

public class GuidesTemplateVO implements Serializable {  

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private String contentType;
  private String guidesCategory;
  private String title;
  private String imageUrl;
  private String imageAltText;
  private String shortDescription;
  private String longDescription;
  private String guideTemplateId;
  private Map<String, String> omnitureData;
  
  public Map<String, String> getOmnitureData() {
	return omnitureData;
}
public void setOmnitureData(Map<String, String> omnitureData) {
	this.omnitureData = omnitureData;
}
/**
   * @return the guideTemplateId
   */
  public String getGuideTemplateId() {
    return guideTemplateId;
  }
  /**
   * @param pGuideTemplateId the guideTemplateId to set
   */
  public void setGuideTemplateId(String pGuideTemplateId) {
    guideTemplateId = pGuideTemplateId;
  }
  
  
  /**
   * @return the contentType
   */
  public String getContentType() {
    return contentType;
  }
  /**
   * @param pContentType the contentType to set
   */
  public void setContentType(String pContentType) {
    contentType = pContentType;
  }
  /**
   * @return the guidesCategory
   */
  public String getGuidesCategory() {
    return guidesCategory;
  }
  /**
   * @param pGuidesCategory the guidesCategory to set
   */
  public void setGuidesCategory(String pGuidesCategory) {
    guidesCategory = pGuidesCategory;
  }
  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }
  /**
   * @param pTitle the title to set
   */
  public void setTitle(String pTitle) {
    title = pTitle;
  }
  /**
   * @return the imageUrl
   */
  public String getImageUrl() {
    return imageUrl;
  }
  /**
   * @param pImageUrl the imageUrl to set
   */
  public void setImageUrl(String pImageUrl) {
    imageUrl = pImageUrl;
  }
  /**
   * @return the imageAltText
   */
  public String getImageAltText() {
    return imageAltText;
  }
  /**
   * @param pImageAltText the imageAltText to set
   */
  public void setImageAltText(String pImageAltText) {
    imageAltText = pImageAltText;
  }
  /**
   * @return the shortDescription
   */
  public String getShortDescription() {
    return shortDescription;
  }
  /**
   * @param pShortDescription the shortDescription to set
   */
  public void setShortDescription(String pShortDescription) {
    shortDescription = pShortDescription;
  }
  /**
   * @return the longDescription
   */
  public String getLongDescription() {
    return longDescription;
  }
  /**
   * @param pLongDescription the longDescription to set
   */
  public void setLongDescription(String pLongDescription) {
    longDescription = pLongDescription;
  }
   
  
  
  /**
   * @param pContentType
   * @param pGuidesCategory
   * @param pTitle
   * @param pImageUrl
   * @param pImageAltText
   * @param pShortDescription
   * @param pLongDescription
   * @param pGuideTemplateId
   */
  public GuidesTemplateVO(String pContentType, String pGuidesCategory, String pTitle, String pImageUrl,
      String pImageAltText, String pShortDescription, String pLongDescription, String pGuideTemplateId) {
    super();
    contentType = pContentType;
    guidesCategory = pGuidesCategory;
    title = pTitle;
    imageUrl = pImageUrl;
    imageAltText = pImageAltText;
    shortDescription = pShortDescription;
    longDescription = pLongDescription;
    guideTemplateId = pGuideTemplateId;
  }
  /**
   * 
   */
  public GuidesTemplateVO() {
    super();
  }




}
