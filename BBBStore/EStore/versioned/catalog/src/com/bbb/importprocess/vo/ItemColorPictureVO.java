package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class ItemColorPictureVO {
  
  private String mColorCD;
  private boolean mDisableFlag;
  private String mMedLoc;
  private String mSwatchLoc;
  private String mZoomLoc;


  /**
   * @return the colorCD
   */
  public String getColorCD() {
    
    if (!StringUtils.isEmpty(mColorCD)) {

      return mColorCD.trim();
    }
    return mColorCD;
  }
  /**
   * @param pColorCD the colorCD to set
   */
  public void setColorCD(String pColorCD) {
    mColorCD = pColorCD;
  }
  /**
   * @return the disableFlag
   */
  public boolean isDisableFlag() {
    return mDisableFlag;
  }
  /**
   * @param pDisableFlag the disableFlag to set
   */
  public void setDisableFlag(boolean pDisableFlag) {
    mDisableFlag = pDisableFlag;
  }
  /**
   * @return the medLoc
   */
  public String getMedLoc() {
    if (!StringUtils.isEmpty(mColorCD)) {

      return mColorCD.trim();
    }
    return mMedLoc;
  }
  /**
   * @param pMedLoc the medLoc to set
   */
  public void setMedLoc(String pMedLoc) {
    
    mMedLoc = pMedLoc;
  }
  /**
   * @return the swatchLoc
   */
  public String getSwatchLoc() {
    if (!StringUtils.isEmpty(mSwatchLoc)) {

      return mSwatchLoc.trim();
    }
    return mSwatchLoc;
  }
  /**
   * @param pSwatchLoc the swatchLoc to set
   */
  public void setSwatchLoc(String pSwatchLoc) {
    
    mSwatchLoc = pSwatchLoc;
  }
  /**
   * @return the zoomLoc
   */
  public String getZoomLoc() {
    if (!StringUtils.isEmpty(mZoomLoc)) {

      return mZoomLoc.trim();
    }
    return mZoomLoc;
  }
  /**
   * @param pZoomLoc the zoomLoc to set
   */
  public void setZoomLoc(String pZoomLoc) {
    mZoomLoc = pZoomLoc;
  }

}



