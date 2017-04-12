//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit
//written consent is prohibited.
//
//Created by: ssha53
//
//Created on: 03-November-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

// TODO: Auto-generated Javadoc
/**
 * This class get the registryVO for a given registryId.
 *
 *
 * @author ssha53
 */
public class GetRegistryVODroplet extends BBBPresentationDroplet {

	/**
	 * The output parameter that contains the registryVO get from web service
	 * call.
	 */

	public static final String WEB_SERVICE_DATE_FORMAT = "MM/dd/yyyy";
	/* US Date Format */
	/** The Constant US_DATE_FORMAT. */
	public static final String US_DATE_FORMAT = "MM/dd/yyyy";
	/* Canada Date Format */
	/** The Constant CA_DATE_FORMAT. */
	public static final String CA_DATE_FORMAT = "dd/MM/yyyy";
	/* Web service date format for ShowerDate */
	// public static final String SHOWER_SRC_DATE_FORMAT =
	// "MM/dd/yyyy HH:mm:ss a";
	private BBBCatalogTools mCatalogTools;

	/** The Constant CANADA_SITE. */
	public static final String CANADA_SITE = "BedBathCanada";
	public static final String OUTPUT_REGISTRY_TYPE_NAME = "registryTypeName";

	/** The Constant EVENT_DATE_FORMAT. */
	public static final String EVENT_DATE_FORMAT = "yyyyMMdd";

	/** The Constant SHOWER_SRC_DATE_FORMAT. */
	public static final String SHOWER_SRC_DATE_FORMAT = "yyyyMMdd";

	/** The Constant OUTPUT_REGISTRYVO. */
	public static final String OUTPUT_REGISTRYVO = "registryVO";

	/** The Constant NO_LITERAL. */
	public static final String NO_LITERAL = "N";

	/** The Constant YES_LITERAL. */
	public static final String YES_LITERAL = "Y";

	public static final String SITE_ID = "siteId";
	
	/** The Constant IS_REGISTRY_TYPE_NAME_REQUIRED. */
	public static final String IS_REGISTRY_TYPE_NAME_REQUIRED = "isRegTypeNameReq";

	public static final String SESSION_BEAN_NUCLEUS_PATH = "/com/bbb/profile/session/SessionBean";

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	/**
	 * Generates Boolean object for profile exist for email or not..
	 *
	 * @param pRequest
	 *            - http request
	 * @param pResponse
	 *            - http response
	 * @throws IOException
	 *             if an error occurs
	 * @throws ServletException
	 *             if an error occurs
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug(" GetRegistryVODroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		final String registryId = pRequest.getParameter("registryId");
		final String siteId = pRequest.getParameter(SITE_ID);
		boolean isRegistryTypeNameRequired = false;
		if(null != pRequest.getParameter(IS_REGISTRY_TYPE_NAME_REQUIRED)){
			isRegistryTypeNameRequired = Boolean.parseBoolean(pRequest.getParameter(IS_REGISTRY_TYPE_NAME_REQUIRED));
		}
		try {
			final BBBSessionBean sessionBean = (BBBSessionBean)pRequest.resolveName(SESSION_BEAN_NUCLEUS_PATH);
			RegistryVO registryVO = null;
			if ((sessionBean!= null) && (null != registryId) && (null != sessionBean.getRegistryVOs()) && (null != sessionBean.getRegistryVOs().get(registryId)) 
					&& !BBBUtility.isEmpty(sessionBean.getRegistryVOs().get(registryId).getRegistryId())) {
				registryVO = sessionBean.getRegistryVOs().get(registryId);
			}
			else{
				registryVO = this.mGiftRegistryManager.getRegistryDetailInfo(registryId, siteId);
				this.logDebug("Getting registryVO form Web Service call");
				
				registryVO.setRegistryId(registryId);
	
				if ((registryVO.getAffiliateOptIn() != null)
						&& YES_LITERAL.equalsIgnoreCase(registryVO
								.getAffiliateOptIn())) {
	
					registryVO.setOptInWeddingOrBump("true");
				} else {
					registryVO.setOptInWeddingOrBump("false");
				}
	
				/* Event Date */
				final String eventDate = registryVO.getEvent().getEventDate();
				String formattedDate = this.formatDate(siteId, eventDate, null,pRequest);
				registryVO.getEvent().setEventDate(formattedDate);
	
				/* Show Date */
				final String showerDate = registryVO.getEvent().getShowerDate();
				formattedDate = this.formatDate(siteId, showerDate,
						SHOWER_SRC_DATE_FORMAT,pRequest);
				registryVO.getEvent().setShowerDate(formattedDate);
	
				/* Birth Date */
				final String birthDate = registryVO.getEvent().getBirthDate();
				formattedDate = this.formatDate(siteId, birthDate, null,pRequest);
				registryVO.getEvent().setBirthDate(formattedDate);
	
				/* Future Shipping Date */
				if (registryVO.getShipping() != null) {
					final String futureShippingDate = registryVO.getShipping()
							.getFutureShippingDate();
					formattedDate = this.formatDate(siteId, futureShippingDate, null,pRequest);
					registryVO.getShipping().setFutureShippingDate(formattedDate);
				}
				if ((sessionBean!= null && (null != registryId))) {
					HashMap<String, RegistryVO> registryVOEntry = sessionBean.getRegistryVOs();
					if(null == registryVOEntry){
						registryVOEntry = new HashMap<String, RegistryVO>();
					}
					registryVOEntry.put(registryId, registryVO);
					sessionBean.setRegistryVOs(registryVOEntry);
				}
			}
			//R2.1 changes
			if(registryVO == null){
				this.logError("GetRegistryVODroplet.service got registryId = " + registryId + " . But recieved null response from service/session");
				return;
			}
			if ((sessionBean!= null) && (null != sessionBean.getRegistryTypesEvent())) {
				sessionBean.setRegistryTypesEvent(null);
			}
			if(sessionBean!=null){
				sessionBean.setRegistryTypesEvent(registryVO.getRegistryType().getRegistryTypeName());	
			}
			if(isRegistryTypeNameRequired){
				String registryTypeName = null;
				String registryTypeCode = registryVO.getRegistryType().getRegistryTypeName();
				try {
					if (null != registryTypeCode) {
						registryTypeName = getCatalogTools().getRegistryTypeName(registryTypeCode,siteId);
					}
					pRequest.setParameter(OUTPUT_REGISTRY_TYPE_NAME, registryTypeName);
				} catch (BBBBusinessException e) {
					pRequest.setParameter(OUTPUT_REGISTRY_TYPE_NAME, "");
					logError(LogMessageFormatter.formatMessage(pRequest, "Get Registry name by registy code BBBBusinessException from service of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1070),e);
				} catch (BBBSystemException e) {
					pRequest.setParameter(OUTPUT_REGISTRY_TYPE_NAME, "");
					logError(LogMessageFormatter.formatMessage(pRequest, "Get Registry name by registy code BBBSystemException from service of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1071),e);
				}
			}
			pRequest.setParameter(OUTPUT_REGISTRYVO, registryVO);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);

		} catch (final BBBBusinessException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			this.logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1073),e);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (final BBBSystemException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
			this.logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1074),e);
		}
		this.logDebug(" GetRegistryVODroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}

	/**
	 * Format date.
	 *
	 * @param siteId
	 *            the site id
	 * @param srcDate
	 *            the src date
	 * @param srcFormat
	 *            the src format
	 * @return the string
	 */
	public String formatDate(final String siteId, final String srcDate, final String srcFormat, final DynamoHttpServletRequest pRequest) {

		if ((srcDate == null) || srcDate.trim().equalsIgnoreCase("") ||  BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(srcDate)) {
			return null;
		}

		String srcDateFormat = EVENT_DATE_FORMAT;
		if (srcFormat != null) {
			srcDateFormat = srcFormat;
		}

		final SimpleDateFormat sdfSource = new SimpleDateFormat(srcDateFormat);
		SimpleDateFormat sdfDestination = null;
		String strDate = null;
		Date date = null;
		try {
			date = sdfSource.parse(srcDate);
			if (siteId.equalsIgnoreCase(CANADA_SITE)) {
				sdfDestination = new SimpleDateFormat(CA_DATE_FORMAT);
			} else {
				sdfDestination = new SimpleDateFormat(US_DATE_FORMAT);
			}

			strDate = sdfDestination.format(date);

		} catch (final java.text.ParseException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest, "Date parsing Exception from formatDate of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1072),e);
			this.logError("GetRegistryVODroplet service method event date parse exception"
					+ e);
		}

		return strDate;

	}

	/**
	 * Gets the gift registry manager.
	 *
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return this.mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 *
	 * @param pGiftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(
			final GiftRegistryManager pGiftRegistryManager) {
		this.mGiftRegistryManager = pGiftRegistryManager;
	}

	/**
	 * Gets the catalog tools.
	 * 
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 * 
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		mCatalogTools = catalogTools;
	}
}
