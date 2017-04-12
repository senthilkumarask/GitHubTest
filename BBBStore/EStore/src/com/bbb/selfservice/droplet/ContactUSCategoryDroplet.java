/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 04-November-2011
 * --------------------------------------------------------------------------------
 */

package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.manager.ContactUsManager;
import com.bbb.utils.BBBUtility;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;

/**
 * The class is the extension of the ATG DynamoServlet. The class is responsible
 * for rendering the content for subject drop down in contact_us.jsp.
 * 
 */

public class ContactUSCategoryDroplet extends BBBDynamoServlet {
	private static final String SUBJECTCATEGORYTYPES = "subjectCategoryTypes";

	private ContactUsManager mContactUsManager;
	private LblTxtTemplateManager lblTxtTemplateManager;

	/**
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	/**
	 * @param lblTxtTemplateManager the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/**
	 * @return the contactUsManager
	 */
	public ContactUsManager getContactUsManager() {
		return mContactUsManager;
	}

	/**
	 * @param pContactUsManager
	 *            the contactUsManager to set
	 */
	public void setContactUsManager(ContactUsManager pContactUsManager) {
		mContactUsManager = pContactUsManager;
	}

	/**
	 * Fetch Category Types for the dropdown to select a Category type.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("ContactUSCategoryDroplet.service() method started");

		try {

			List<String> subjectCategoryTypes = new ArrayList<String>();
			RepositoryItem[] items = getContactUsManager().getContactUsItem();

			if (items != null && items.length > 0) {

				for (RepositoryItem item : items) {

					subjectCategoryTypes.add((String) item
							.getPropertyValue("subject"));
				}

			}

			pRequest.setParameter("subjectCategoryTypes", subjectCategoryTypes);
			logDebug("set output to the display page");

			pRequest.serviceLocalParameter("output", pRequest, pResponse);

		} catch (BBBSystemException systemException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException in ContactUSCategoryDroplet.service()", BBBCoreErrorConstants.ACCOUNT_ERROR_1186 ), systemException);
		} catch (BBBBusinessException businessException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException in ContactUSCategoryDroplet.service()", BBBCoreErrorConstants.ACCOUNT_ERROR_1187 ), businessException);
		} 

		logDebug("ContactUSCategoryDroplet.service() method ends");
	}
	
	
	/**
	 * Fetch Category Types for the dropdown to select a Category type by calling service method
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Object[] getAllSubjects(Map <String,String> inputParam) throws BBBSystemException, BBBBusinessException{
		
		logDebug("ContactUSCategoryDroplet.getAllSubjects() method starts");
		
		List<String> subjectCategoryTypes = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();
		try {
			service(pRequest, pResponse);
			subjectCategoryTypes = (List<String>)pRequest.getObjectParameter(SUBJECTCATEGORYTYPES);
			if(BBBUtility.isListEmpty(subjectCategoryTypes)){
				logError("ContactUSCategoryDroplet.getAllSubjects() recived null subjectCategoryTypes object in request");
				throw new ServletException(); 
			} else {
				
				logDebug("ContactUSCategoryDroplet.getAllSubjects() method ends");
				
				return subjectCategoryTypes.toArray();
			}
		} catch (ServletException e) {
			 throw new BBBSystemException("err_contactus_category_droplet", getLblTxtTemplateManager().getErrMsg(
						"err_contactus_category_droplet",
						pRequest.getLocale().getLanguage(),
						null, null));
		} catch (IOException e) {
			 throw new BBBSystemException("err_io_exception_category_droplet", "BBBSystemException in ContactUSCategoryDroplet.service()");
		}
	}
	

}
