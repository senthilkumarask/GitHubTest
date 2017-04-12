package com.bbb.search.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.result.SortOptionVO;
import com.bbb.tools.BBBSiteRepositoryTools;

public class BBBDefaultSortOptionsDroplet extends BBBDynamoServlet {
	
	private BBBSiteRepositoryTools siteRepositoryTools;
	
	/**
	 * @return the siteRepositoryTools
	 */
	public BBBSiteRepositoryTools getSiteRepositoryTools() {
		return siteRepositoryTools;
	}

	/**
	 * @param siteRepositoryTools the siteRepositoryTools to set
	 */
	public void setSiteRepositoryTools(BBBSiteRepositoryTools siteRepositoryTools) {
		this.siteRepositoryTools = siteRepositoryTools;
	}

	/**
	 * This method sets the default sort options for the site
	 * BBBJ-1220
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		try {
			SortOptionVO sortOptions = this.getSiteRepositoryTools().getSortOptionsForSite();
			pRequest.setParameter(BBBCoreConstants.SORT_OPTIONS, sortOptions);
			pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("BBBDefaultSortOptionsDroplet.service method. Exception : "+ e.getMessage());
			if(isLoggingDebug()) {
				logError("BBBDefaultSortOptionsDroplet.service method. Exception Full Trace: ", e);
			}
		}
	}
}
