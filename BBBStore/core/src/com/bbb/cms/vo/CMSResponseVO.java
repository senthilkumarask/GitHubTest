package com.bbb.cms.vo;

import java.io.Serializable;

import atg.repository.RepositoryItem;

/**
 * Response VO common to all CMS templates
 * @author ikhan2
 *
 */
public class CMSResponseVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RepositoryItem[] responseItems;

	public RepositoryItem[] getResponseItems() {
		return responseItems;
	}

	public void setResponseItems(RepositoryItem[] responseItems) {
		this.responseItems = responseItems;
	}

}
