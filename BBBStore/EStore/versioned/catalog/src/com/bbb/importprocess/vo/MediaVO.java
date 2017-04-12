package com.bbb.importprocess.vo;

import java.sql.Timestamp;

import atg.core.util.StringUtils;

public class MediaVO {
  
  private String mMediaType;
  private String mProvider;
  private String mMediaSource;
  private String mMediaDescription;
  private String mComments;
  private String mMediaTranscript;
  private Timestamp startDate;
  private String mSiteIds;

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	private Timestamp endDate;
	private String widgetId;
  /**
   * @return the mediaType
   */
  public String getMediaType() {
    if (!StringUtils.isEmpty(mMediaType)) {

      return mMediaType.trim();
    }
    return mMediaType;
  }
  /**
   * @param pMediaType the mediaType to set
   */
  public void setMediaType(String pMediaType) {
    mMediaType = pMediaType;
  }
  /**
   * @return the provider
   */
  public String getProvider() {
    if (!StringUtils.isEmpty(mProvider)) {

      return mProvider.trim();
    }
    return mProvider;
  }
  /**
   * @param pProvider the provider to set
   */
  public void setProvider(String pProvider) {
    mProvider = pProvider;
  }
  /**
   * @return the mediaSource
   */
  public String getMediaSource() {
    if (!StringUtils.isEmpty(mMediaSource)) {

      return mMediaSource.trim();
    }
    return mMediaSource;
  }
  /**
   * @param pMediaSource the mediaSource to set
   */
  public void setMediaSource(String pMediaSource) {
    mMediaSource = pMediaSource;
  }
  /**
   * @return the mediaDescription
   */
  public String getMediaDescription() {
    
    if (!StringUtils.isEmpty(mMediaDescription)) {

      return mMediaDescription.trim();
    }
    return mMediaDescription;
  }
  /**
   * @param pMediaDescription the mediaDescription to set
   */
  public void setMediaDescription(String pMediaDescription) {
    mMediaDescription = pMediaDescription;
  }
  /**
   * @return the comments
   */
  public String getComments() {
    if (!StringUtils.isEmpty(mComments)) {

      return mComments.trim();
    }
    return mComments;
  }
  /**
   * @param pComments the comments to set
   */
  public void setComments(String pComments) {
    mComments = pComments;
  }
  /**
   * @return the mediaTranscript
   */
  public String getMediaTranscript() {
    if (!StringUtils.isEmpty(mMediaTranscript)) {

      return mMediaTranscript.trim();
    }
    return mMediaTranscript;
  }
  /**
   * @param pMediaTranscript the mediaTranscript to set
   */
  public void setMediaTranscript(String pMediaTranscript) {
    mMediaTranscript = pMediaTranscript;
  }
  
  
  /**
   * @return the siteIds
   */
  public String getSiteIds() {
    
    if (!StringUtils.isEmpty(mSiteIds)) {

      return mSiteIds.trim();
    }
    return mSiteIds;
  }

  /**
   * @param pSiteIds the siteIds to set
   */
  public void setSiteIds(String pSiteIds) {
    mSiteIds = pSiteIds;
  }

}
