//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//
//
//Created on: 28-December-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;



import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegSearchResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;


/**
 * The Class MyRegistriesDisplayDroplet.
 */
public class MyRegistriesDisplayDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager registryManager;
	/** Constants for string literal profile. */
	private static final String PROFILE = "profile";
	/** Constants for string literal site id. */
	private static final String SITE_ID = "siteId";

	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;


	/** The Constant REGISTRY_LIST_SIZE. */
	public final static String REGISTRY_LIST_SIZE = "registryCount";

	/** The Constant ERR_REGINFO_FATAL_ERROR. */
	private static final String ERR_REGINFO_FATAL_ERROR = "err_gift_reg_fatal_error";

	/** The Constant ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR. */
	private static final String ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR = "err_gift_reg_siteflag_usertoken_error";

	/** The Constant ERR_REGINFO_INVALID_INPUT_FORMAT. */
	private static final String ERR_REGINFO_INVALID_INPUT_FORMAT = "err_gift_reg_invalid_input_format";

	/** The Registry info service name. */
	private String mRegistryInfoServiceName;

	/**
	 * The method gets the user's registries and corresponding details from the
	 * web service.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException,
			IOException {

		logDebug("Entering Service Method of MyRegistries Droplet.");
		 BBBPerformanceMonitor.start("MYRegistryDisplayDroplet", "GetRegistryInfoByProfileId");
		final Profile profile = (Profile)request.getObjectParameter(PROFILE);
		
		final String pSiteId = request.getParameter(SITE_ID);//getSiteContext().getSite().getId();
		final RegistrySearchVO registrySearchVO = new RegistrySearchVO();
			try {
				registrySearchVO.setProfileId(profile);
				if (null != getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId)) {
					registrySearchVO
							.setSiteId(getCatalogTools()
									.getAllValuesForKey(
											BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
											pSiteId).get(0));
				}
				registrySearchVO.setUserToken(getCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
						.get(0));
				registrySearchVO.setServiceName(getRegistryInfoServiceName());
				RegSearchResVO regSearchResVO = getRegistryManager().
						searchRegistries(registrySearchVO,pSiteId);
				
				List<RegistrySummaryVO> pRegistryVO = null;
				if (regSearchResVO != null){
					if( regSearchResVO.getServiceErrorVO().isErrorExists()) {
					if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
							.getErrorDisplayMessage())
							&& regSearchResVO.getServiceErrorVO().getErrorDisplayMessage().contains(".SQLException"))// Technical
																																				// Error
						{
							logError(LogMessageFormatter.formatMessage(request, "SQLException from service of MyRegistryDisplayDroplet "+regSearchResVO.getServiceErrorVO()
								.getErrorId(),BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1078));
							throw new BBBSystemException("err_code_db_conn",
								"err_code_db_conn");
						}
						if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
								.getErrorDisplayMessage())
								&& regSearchResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS)// Technical
							// Error
						{
							logError(LogMessageFormatter.formatMessage(request, "Gift Registry Input fields Exception from service of MyRegistryDisplayDroplet "+regSearchResVO.getServiceErrorVO()
									.getErrorId(),BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1078));
							throw new BBBSystemException("err_gift_reg_input_field_error",
									"err_gift_reg_input_field_error");
						}
						if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
								.getErrorDisplayMessage())
								&& regSearchResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR)// Technical
							// Error
						{

							logError(LogMessageFormatter.formatMessage(request, "Fatal error from service of MyRegistriesDisplayDroplet : Error Id is:"	+ regSearchResVO.getServiceErrorVO().getErrorId(), BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1011));
							throw new BBBSystemException(ERR_REGINFO_FATAL_ERROR,ERR_REGINFO_FATAL_ERROR);
						}
						if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
								.getErrorDisplayMessage())
								&& regSearchResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN)// Technical
							// Error
						{
							logError(LogMessageFormatter.formatMessage(request, "Either user token or site flag invalid from service of MyRegistriesDisplayDroplet : Error Id is:"	+ regSearchResVO.getServiceErrorVO().getErrorId(), BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1002));
							throw new BBBSystemException(
									ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR,ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR);
						}
						if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
								.getErrorDisplayMessage())
								&& regSearchResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT)// Technical
							// Error
						{
							logError(LogMessageFormatter.formatMessage(request, 
									"GiftRegistry input fields format error from service() of " +
											"MyRegistriesDisplayDroplet | webservice error code=" + regSearchResVO.getServiceErrorVO().getErrorId(), 
											BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

							throw new BBBSystemException(
									ERR_REGINFO_INVALID_INPUT_FORMAT,ERR_REGINFO_INVALID_INPUT_FORMAT);
						}

					}

					pRegistryVO = regSearchResVO
							.getListRegistrySummaryVO();

					List<RegistrySummaryVO> summaryVO = new ArrayList<RegistrySummaryVO>();
					if (null!=pRegistryVO)
					{
						// If Registrant/Co-Registrant is updated by the CSR, that
						// registry is not updated in session bean until user re-login so adding missing registries to session.
						//final BBBSessionBean sessionBean = (BBBSessionBean) request.resolveName(BBBCoreConstants.SESSION_BEAN);
						final BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(request);
						final HashMap sessionMap = sessionBean.getValues();
						List<String> pListUserRegIds = (List<String>) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
						if (pListUserRegIds == null) {
							pListUserRegIds = new ArrayList<String>();
						}
						for (RegistrySummaryVO regVO : pRegistryVO){
							if(regVO.getAddrSubType().equals("CO")){
								regVO.setCoRegistrantFullName(regVO.getPrimaryRegistrantFullName());						
							}
							if (regVO.getRegistryId() != null && !pListUserRegIds.contains(regVO.getRegistryId())) {
								pListUserRegIds.add(regVO.getRegistryId());
							}
							summaryVO.add(regVO);
						}
						pRegistryVO = summaryVO;
					}


					if(registrySearchVO.getSiteId().equalsIgnoreCase("3") && pRegistryVO!=null && pRegistryVO.size()>0)
					{
						for (int i = 0; i < pRegistryVO.size(); i++) {
							RegistrySummaryVO registrySummaryVO=pRegistryVO.get(i);
							registrySummaryVO.setEventDateCanada(BBBUtility.convertUSDateIntoWSFormatCanada(registrySummaryVO.getEventDate()));

						}

					}
				}
					request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO,
							pRegistryVO);
					if (null==pRegistryVO||pRegistryVO.size()<1)
					{
					request.setParameter(REGISTRY_LIST_SIZE, 0);
					}
					else
					{
						request.setParameter(REGISTRY_LIST_SIZE, pRegistryVO.size());
					}

				
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(request, "BBBBusinessException from service of MyRegistryDisplayDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),e);
				request.setParameter(OUTPUT_ERROR_MSG,
						BBBGiftRegistryConstants.BUSINESS_EXCEPTION);
				request.serviceLocalParameter(OPARAM_ERROR, request, response);
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(request, "BBBSystemException from SERVICE of MyRegistryDisplayDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),e);
				request.setParameter(OUTPUT_ERROR_MSG,
						BBBGiftRegistryConstants.SYSTEM_EXCEPTION);
				request.serviceLocalParameter(OPARAM_ERROR, request, response);

			}

	    logDebug("Exit Service Method of MyRegistries Droplet.");
	    BBBPerformanceMonitor.end("MYRegistryDisplayDroplet", "GetRegistryInfoByProfileId");
		request.serviceLocalParameter(OPARAM_OUTPUT, request, response);
	}

	
	/**
	 * Gets the catalog tools.
	 * 
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 * 
	 * @param pCatalogTools
	 *            the new catalog tools
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	

	/**
	 * Gets the registry info service name.
	 * 
	 * @return the registryInfoServiceName
	 */
	public String getRegistryInfoServiceName() {
		return mRegistryInfoServiceName;
	}

	/**
	 * Sets the registry info service name.
	 * 
	 * @param registryInfoServiceName
	 *            the registryInfoServiceName to set
	 */
	public void setRegistryInfoServiceName(String registryInfoServiceName) {
		mRegistryInfoServiceName = registryInfoServiceName;
	}


	public GiftRegistryManager getRegistryManager() {
		return registryManager;
	}


	public void setRegistryManager(GiftRegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
