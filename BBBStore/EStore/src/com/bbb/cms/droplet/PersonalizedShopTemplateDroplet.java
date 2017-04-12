package com.bbb.cms.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.cms.manager.ContentTemplateManager;
import com.bbb.cms.vo.CMSResponseVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * This Class is used to populate static template related data for Personalized Shop.
 */

public class PersonalizedShopTemplateDroplet extends BBBDynamoServlet {

	private ContentTemplateManager mContentTemplateMgr;
	private static final String ARRAY_SEPARETOR = "}";
	private static final String ARRAY_SEPARETORSTART = "{";
	/**
	 * @return the mStaticTemplateManager
	 */
	public ContentTemplateManager getContentTemplateMgr() {
		return mContentTemplateMgr;
	}

	/**
	 * @param pStaticTemplateManager
	 *            the mStaticTemplateManager to set
	 */
	public void setContentTemplateMgr(
			ContentTemplateManager pStaticTemplateManager) {
		mContentTemplateMgr = pStaticTemplateManager;
	}

	/**
	 *  This Class is used to populate static template related data for Personalized Shop.
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		
		logDebug("starting method PersonalizedShopTemplateDroplet");

		final String categoryId = pRequest.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID);
		final String templateName = BBBCoreConstants.PERSONALIZEDSHOPTEMPLATE;
		try {
			// Get data from Custom landing template repository for a given category id
			if(!BBBUtility.isEmpty(categoryId) ){
			
				String jsonString = ARRAY_SEPARETORSTART+BBBCoreConstants.PARAM_CATEGORY_ID+BBBCoreConstants.COLON+ categoryId + ARRAY_SEPARETOR;
				if(BBBUtility.isEmpty(pRequest.getParameter(BBBCoreConstants.CHANNEL))){
					ServletUtil.getCurrentRequest().setParameter(BBBCoreConstants.CHANNEL,BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
				}
				final CMSResponseVO responseVO = getContentTemplateMgr().getContent(templateName, jsonString);
			
			
			if (responseVO != null) {
				pRequest.setParameter(BBBCmsConstants.STATIC_TEMPLATE_DATA,
						responseVO);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
						pResponse);
			} else {
				
				pRequest.serviceParameter(BBBCmsConstants.EMPTY, pRequest,
						pResponse);
			}
			}
		} catch (RepositoryException repException) {
			
			logError("catalog_1052: Repository Exception PersonalizedShopTemplateDroplet", repException);
		
		} catch (BBBSystemException e) {
			logError("catalog_1052: Repository Exception PersonalizedShopTemplateDroplet", e);
		} catch (BBBBusinessException e) {
			logError("catalog_1052: Repository Exception PersonalizedShopTemplateDroplet", e);
		}

		
		logDebug("Existing method PersonalizedShopTemplateDroplet");

		
	}
}
