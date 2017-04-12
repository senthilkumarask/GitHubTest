package com.bbb.selfservice.vo;


import java.io.Serializable;
import java.util.Date;

import com.bbb.commerce.catalog.vo.StateVO;

 


import atg.repository.RepositoryItem;

public class SchoolVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String schoolId;
	
	private String schoolName;
	private String smallLogoURL;
	private String largeLogoURL;
	private String smallWelcomeMsg;
	private String largeWelcomeMsg;
	private String addrLine1;
	private String addrLine2;
	private String city;
	private StateVO state;
	private String zip;
	private String imageURL;
	private String prefStoreId;
	private String pdfURL;
	private String collegeTag;
	private String collegeLogo;
	private Date creationDate;
	private Date lastModifiedDate;
	private String schoolSeoName;
	private RepositoryItem promotionRepositoryItem;
	private String schoolContent;
	private String cssFilePath;
	private String jsFilePath;
	
	public SchoolVO()
	{
		super();
	}

  /**
   * @return the schoolId
   */
  public String getSchoolId() {
    return schoolId;
  }

  /**
   * @param pSchoolId the schoolId to set
   */
  public void setSchoolId(String pSchoolId) {
    schoolId = pSchoolId;
  }

  /**
   * @return the schoolName
   */
  public String getSchoolName() {
    return schoolName;
  }

  /**
   * @param pSchoolName the schoolName to set
   */
  public void setSchoolName(String pSchoolName) {
    schoolName = pSchoolName;
  }

  /**
   * @return the smallLogoURL
   */
  public String getSmallLogoURL() {
    return smallLogoURL;
  }

  /**
   * @param pSmallLogoURL the smallLogoURL to set
   */
  public void setSmallLogoURL(String pSmallLogoURL) {
    smallLogoURL = pSmallLogoURL;
  }

  /**
   * @return the largeLogoURL
   */
  public String getLargeLogoURL() {
    return largeLogoURL;
  }

  /**
   * @param pLargeLogoURL the largeLogoURL to set
   */
  public void setLargeLogoURL(String pLargeLogoURL) {
    largeLogoURL = pLargeLogoURL;
  }

  /**
   * @return the smallWelcomeMsg
   */
  public String getSmallWelcomeMsg() {
    return smallWelcomeMsg;
  }

  /**
   * @param pSmallWelcomeMsg the smallWelcomeMsg to set
   */
  public void setSmallWelcomeMsg(String pSmallWelcomeMsg) {
    smallWelcomeMsg = pSmallWelcomeMsg;
  }

  /**
   * @return the state
   */
  public StateVO getState() {
    return state;
  }

  /**
   * @param pState the state to set
   */
  public void setState(StateVO pState) {
    state = pState;
  }
  
  
  /**
   * @return the largeWelcomeMsg
   */
  public String getLargeWelcomeMsg() {
    return largeWelcomeMsg;
  }

  /**
   * @param pLargeWelcomeMsg the largeWelcomeMsg to set
   */
  public void setLargeWelcomeMsg(String pLargeWelcomeMsg) {
    largeWelcomeMsg = pLargeWelcomeMsg;
  }

  

  /**
   * @return the addrLine1
   */
  public String getAddrLine1() {
    return addrLine1;
  }

  /**
   * @param pAddrLine1 the addrLine1 to set
   */
  public void setAddrLine1(String pAddrLine1) {
    addrLine1 = pAddrLine1;
  }

  /**
   * @return the addrLine2
   */
  public String getAddrLine2() {
    return addrLine2;
  }

  /**
   * @param pAddrLine2 the addrLine2 to set
   */
  public void setAddrLine2(String pAddrLine2) {
    addrLine2 = pAddrLine2;
  }

  /**
   * @return the city
   */
  public String getCity() {
    return city;
  }

  /**
   * @param pCity the city to set
   */
  public void setCity(String pCity) {
    city = pCity;
  }

   

  /**
   * @return the zip
   */
  public String getZip() {
    return zip;
  }

  /**
   * @param pZip the zip to set
   */
  public void setZip(String pZip) {
    zip = pZip;
  }

  /**
   * @return the imageURL
   */
  public String getImageURL() {
    return imageURL;
  }

  /**
   * @param pImageURL the imageURL to set
   */
  public void setImageURL(String pImageURL) {
    imageURL = pImageURL;
  }

  /**
   * @return the prefStoreId
   */
  public String getPrefStoreId() {
    return prefStoreId;
  }

  /**
   * @param pPrefStoreId the prefStoreId to set
   */
  public void setPrefStoreId(String pPrefStoreId) {
    prefStoreId = pPrefStoreId;
  }

  /**
   * @return the pdfURL
   */
  public String getPdfURL() {
    return pdfURL;
  }

  /**
   * @param pPdfURL the pdfURL to set
   */
  public void setPdfURL(String pPdfURL) {
    pdfURL = pPdfURL;
  }

  /**
   * @return the collegeTag
   */
  public String getCollegeTag() {
    return collegeTag;
  }

  /**
   * @param pCollegeTag the collegeTag to set
   */
  public void setCollegeTag(String pCollegeTag) {
    collegeTag = pCollegeTag;
  }

  /**
   * @return the collegeLogo
   */
  public String getCollegeLogo() {
    return collegeLogo;
  }

  /**
   * @param pCollegeLogo the collegeLogo to set
   */
  public void setCollegeLogo(String pCollegeLogo) {
    collegeLogo = pCollegeLogo;
  }

  /**
   * @return the creationDate
   */
  public Date getCreationDate() {
    return new Date(creationDate.getTime());
  }

  /**
   * @param pCreationDate the creationDate to set
   */
  public void setCreationDate(Date pCreationDate) {
    creationDate = new Date(pCreationDate.getTime());
  }

  /**
   * @return the lastModifiedDate
   */
  public Date getLastModifiedDate() {
    return new Date(lastModifiedDate.getTime());
  }

  /**
   * @param pLastModifiedDate the lastModifiedDate to set
   */
  public void setLastModifiedDate(Date pLastModifiedDate) {
    lastModifiedDate = new Date(pLastModifiedDate.getTime());
  }

  /**
   * @return the schoolRepositoryItem
   */
  public RepositoryItem getPromotionRepositoryItem() {
    return promotionRepositoryItem;
  }

  /**
   * @param pSchoolRepositoryItem the schoolRepositoryItem to set
   */
  public void setPromotionRepositoryItem(RepositoryItem pPromotionRepositoryItem) {
    promotionRepositoryItem = pPromotionRepositoryItem;
  }

  public String getSchoolSeoName() {
	return schoolSeoName;
  }
	
  public void setSchoolSeoName(String schoolSeoName) {
	this.schoolSeoName = schoolSeoName;
  }

public String getSchoolContent() {
	return schoolContent;
}

public void setSchoolContent(String schoolContent) {
	this.schoolContent = schoolContent;
}

public String getCssFilePath() {
	return cssFilePath;
}

public void setCssFilePath(String cssFilePath) {
	this.cssFilePath = cssFilePath;
}

public String getJsFilePath() {
	return jsFilePath;
}

public void setJsFilePath(String jsFilePath) {
	this.jsFilePath = jsFilePath;
}
	 
}
