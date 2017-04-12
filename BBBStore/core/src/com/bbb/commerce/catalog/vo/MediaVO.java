package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

public class MediaVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mediaType;
	private String providerId;
	private String mediaSource;
	private String mediaDescription;
	private String comments;
	private String mediaTranscript;
	
	public MediaVO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the mediaType
	 */
	public String getMediaType() {
		return mediaType;
	}

	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	/**
	 * @return the mediaSource
	 */
	public String getMediaSource() {
		return mediaSource;
	}

	/**
	 * @param mediaSource the mediaSource to set
	 */
	public void setMediaSource(String mediaSource) {
		this.mediaSource = mediaSource;
	}

	/**
	 * @return the mediaDescription
	 */
	public String getMediaDescription() {
		return mediaDescription;
	}

	/**
	 * @param mediaDescription the mediaDescription to set
	 */
	public void setMediaDescription(String mediaDescription) {
		this.mediaDescription = mediaDescription;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the mediaTranscript
	 */
	public String getMediaTranscript() {
		return mediaTranscript;
	}

	/**
	 * @param mediaTranscript the mediaTranscript to set
	 */
	public void setMediaTranscript(String mediaTranscript) {
		this.mediaTranscript = mediaTranscript;
	}

}
