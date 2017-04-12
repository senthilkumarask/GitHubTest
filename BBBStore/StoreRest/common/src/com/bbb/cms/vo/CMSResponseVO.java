package com.bbb.cms.vo;

import atg.repository.RepositoryItem;

/**
 * Response VO common to all CMS templates
 * @author ikhan2
 *
 */
public class CMSResponseVO {
	
	private RepositoryItem[] responseItems;

	public RepositoryItem[] getResponseItems() {
		return responseItems;
	}

	public void setResponseItems(RepositoryItem[] responseItems) {
		this.responseItems = responseItems;
	}
			
}
