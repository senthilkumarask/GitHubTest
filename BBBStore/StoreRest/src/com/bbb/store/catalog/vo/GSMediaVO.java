package com.bbb.store.catalog.vo;

import java.io.Serializable;

public class GSMediaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mediaType;
	private String providerId;
	private String widget;
	
	public GSMediaVO() {
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
	public String getWidget() {
		return widget;
	}

	/**
	 * @param mediaSource the mediaSource to set
	 */
	public void setWidget(String widget) {
		this.widget = widget;
	}

}
