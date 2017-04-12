/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: Lokesh Duseja
 *
 * Created on: 23-November-2011
 * --------------------------------------------------------------------------------
 */

package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;

import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SiteChatAttributesVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.SelfServiceUtil;

/**
 * The class is the extension of the ATG DynamoServlet. The class is responsible
 * for rendering the content for subject drop down in contact_us.jsp.
 * 
 */

public class ClickToChatDroplet extends BBBDynamoServlet {

	private SiteContext mSiteContext;
	private BBBCatalogTools mCatalogTools;

	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param mSiteContext
	 *            the mSiteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		this.mSiteContext = pSiteContext;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * 
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("ClickToChatDroplet.service() method started");

		boolean chatWindowOpenFlag = true;

		SiteChatAttributesVO siteChatAttributesVO = new SiteChatAttributesVO();
		String siteId = getSiteContext().getSite().getId();

		try {
			siteChatAttributesVO = getCatalogTools().getSiteChatAttributes(siteId);
			/*} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null, "err_chat_system_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1117),e);
			pRequest.setParameter("systemerror", "err_chat_system_error");
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
		}*/

			if (siteChatAttributesVO.isOnOffFlag()) {
				chatWindowOpenFlag = SelfServiceUtil.isChatWindowOpen(siteChatAttributesVO);
			}

			pRequest.setParameter("chatURL", siteChatAttributesVO.getChatURL());
			pRequest.setParameter("onOffFlag", siteChatAttributesVO.isOnOffFlag());
			pRequest.setParameter("weekDayOpenTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekDayOpenTime()));
			pRequest.setParameter("weekDayCloseTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekDayCloseTime()));
			pRequest.setParameter("weekEndOpenTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekEndOpenTime()));
			pRequest.setParameter("weekEndCloseTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekEndCloseTime()));

			pRequest.setParameter("chatOpenFlag", chatWindowOpenFlag);

			pRequest.serviceLocalParameter("output", pRequest, pResponse);

		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null, "err_chat_system_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1117),e);
			pRequest.setParameter("systemerror", "err_chat_system_error");
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
		}

		logDebug("ClickToChatDroplet.service() method ended");

	}

}
