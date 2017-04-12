/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBReferralControler.java
 *
 *  DESCRIPTION: A pipeline servlet handling the wedding channel &
 * 				commission junction integration logic.
 *  HISTORY:
 *  02/06/12 Initial version
 *
 */
package com.bbb.pipeline;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.vo.ReferralVO;
import com.bbb.utils.BBBUtility;

/**
 * DESCRIPTION: A pipeline servlet handling the wedding channel & commission
 * junction integration logic.
 *
 * @author skalr2
 */
public class BBBReferralController extends InsertableServletImpl {

	private static final String REFERRAL_CONTROLS = "ReferralControls";
	private static final String WC_REFERRER_COOKIE_TIMEOUT = "wc_referrer_cookie_timeout";
	private static final String BP_REFERRER_COOKIE_TIMEOUT = "bp_referrer_cookie_timeout";

	private static final String REF_ID = "refId";
	private static final String QUERY_STRING = "queryString";
	private static final String CUR_URL = "curUrl";
	private static final String REFERRER_DOMAIN = "referrerDomain";
	private static final String REGISTRY_TYPE_EVENT = "RegistryTypesEvent";

	private transient Boolean mEnabled;
	private String weddingBumpFlag;
	private BBBCatalogTools catalogTools;

	public Boolean getEnabled() {
		return this.mEnabled;
	}

	public void setEnabled(final Boolean pEnabled) {
		this.mEnabled = pEnabled;
	}

	public String getWeddingBumpFlag() {
		return this.weddingBumpFlag;
	}

	public void setWeddingBumpFlag(final String weddingBumpFlag) {
		this.weddingBumpFlag = weddingBumpFlag;
	}

	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools bbbCatalogTools) {
		this.catalogTools = bbbCatalogTools;
	}

	/**
	 * OverRided method from ATG OOTB InsertableServletImpl and does the session
	 * tracking according to the business rules i.e when the user comes from a
	 * referral site and try to access store pages such as create registry &
	 * registry guest view page.
	 *
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with servlet io
	 * @return void
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {

		if (this.isLoggingDebug()) {
			this.logDebug("BBBReferralContoller.service() method started");
		}

		// check that schools promotions are enabled or not
		if (!this.getEnabled()) {
			this.passRequest(pRequest, pResponse);
		} else {

			try {

				this.handleReferralURLPattern(pRequest, pResponse);
				this.passRequest(pRequest, pResponse);

			} catch (final BBBSystemException e) {
				this.logDebug("BBBReferralContoller : BBBSystemException" + e.getMessage());
				this.passRequest(pRequest, pResponse);
			} catch (final BBBBusinessException e) {
				this.logDebug("BBBReferralContoller : BBBBusinessException" + e.getMessage());
				this.passRequest(pRequest, pResponse);
			}
		}
		if (this.isLoggingDebug()) {
			this.logDebug("BBBReferralContoller.service() method ends");
		}

	}

	/**
	 * This method validate url form where request is coming.If it is valid it
	 * would check about the passed referralId such as wc(wedding channel) or cj
	 * (commission junction). If referrallId is valid.It will set the referral
	 * details such as referralId and the query parameters in session.
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return boolean
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws ServletException
	 */
	private void handleReferralURLPattern(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			BBBSystemException, BBBBusinessException, ServletException {

		final String methodName = BBBCoreConstants.SERVICE;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.REFERRAL_WEBLINK,
				methodName);

		if (this.isLoggingDebug()) {
			this.logDebug("BBBReferralContoller.handleReferralURLPattern() method starts");
		}

		String refId = null;
		String[] currentUrl = null;
		String curUrl = null;
		String queryString = null;
		String referrer = null;
		boolean isCJMatch = false;
		ReferralVO referralVO = null;
		final Map<String, String> paramValueMap = new HashMap<String, String>();

		try {
			final String regex = this.getCatalogTools().getConfigValueByconfigType(
					BBBCoreConstants.REFERRAl_CONTROLS).get(
					BBBCoreConstants.REFERRAL_STRINGS);
			if (BBBUtility.isEmpty(regex)) {
				return;
			}

			// Create a Pattern object
			final Pattern pattern = Pattern.compile(regex);

			// extracting the URI -context path, query string
			final String contextPath = pRequest.getContextPath();
			currentUrl = pRequest.getRequestURL().toString().split(contextPath);
			queryString = pRequest.getQueryString();


			if (currentUrl.length > 1){
				curUrl = currentUrl[1];
			}
			referrer = pRequest.getHeader(BBBCoreConstants.REFERRER);

			if (this.isLoggingDebug()) {
				this.logDebug("BBBReferralContoller.handleReferralURLPattern() Parameters are ");
				this.logDebug("contextPath = " + contextPath);
				this.logDebug("currentUrl = " + Arrays.toString(currentUrl));
				this.logDebug("queryString = " + queryString);
				this.logDebug("referrer = " + referrer);
			}

			List<String> cookieTimeOut = null;

			if (BBBUtility.isNotEmpty(queryString)) {
				final Matcher matcher = pattern.matcher(queryString);
				// If queryString has patterns for WeddingChannel and TheBumps
				if (matcher.find()) {
					refId = matcher.group();
					// Check if this request is from WeddingChannel or TheBumps wcref=yes pattern
					if (!BBBUtility.isEmpty(refId) && refId.equalsIgnoreCase(this.getWeddingBumpFlag())) {
						this.logDebug("Current Request is for WeddingChannel and  TheBumps");
						final String currentSiteId = SiteContextManager.getCurrentSiteId();
						final String bedBathUSSiteCode = this.getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.BED_BATH_US_SITE_CODE).get(0);
						final String buyBuyBabySiteCode = this.getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.BUY_BUY_BABY_SITE_CODE).get(0);
						if (bedBathUSSiteCode.equalsIgnoreCase(currentSiteId)) {
							refId = BBBCoreConstants.WED_CHANNEL_REF;
							cookieTimeOut = this.getCatalogTools().getAllValuesForKey(REFERRAL_CONTROLS, WC_REFERRER_COOKIE_TIMEOUT);
						} else if (buyBuyBabySiteCode.equalsIgnoreCase(currentSiteId)) {
							refId = BBBCoreConstants.THEBUMP_REF;
							cookieTimeOut = this.getCatalogTools().getAllValuesForKey(REFERRAL_CONTROLS,	BP_REFERRER_COOKIE_TIMEOUT);
						}
					}
				} else {
					isCJMatch = this.checkCommisionJunction(pRequest, paramValueMap);
					if (isCJMatch) {
						refId = BBBCoreConstants.COM_JUN_REF;
					}
				}
			}

			if (this.isLoggingDebug()) {
				this.logDebug("Ref Id is : " + refId);
			}
			if (!BBBUtility.isEmpty(refId)) {

					referralVO = new ReferralVO();
					referralVO.setReferralId(refId);
					referralVO.setReferralUrl(queryString);
					referralVO.setCurrentUrl(curUrl);
					referralVO.setReferrerDomain(this.findReferrerDomain(referrer));

					final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
							.resolveName("/com/bbb/profile/session/SessionBean");

					//store each request parameter in ParamValuesMap
					if (!BBBUtility.isEmpty(queryString)) {
						final String[]  parameterValues= queryString.split(BBBCoreConstants.AMPERSAND);
						if(parameterValues !=null ){
							for(final String parameterValue: parameterValues){
								if(parameterValue.indexOf(BBBCoreConstants.EQUAL)>0){
									final String[] tokens = parameterValue.split(BBBCoreConstants.EQUAL);
									paramValueMap.put( tokens[0] , ((tokens.length == 2) && (tokens[1] != null))?tokens[1]:"");
								}
							}
						}
						if(paramValueMap.size()>0){
							sessionBean.getValues().put(BBBCoreConstants.REFFERAL_PARAMS_MAP, paramValueMap);
						}
					}
					int maxTimeout = 100000;
					if ((cookieTimeOut != null) && !cookieTimeOut.isEmpty()) {
						try {
							maxTimeout=Integer.parseInt(cookieTimeOut.get(0));
						} catch (final NumberFormatException nfe) {
							throw new BBBSystemException("Cannot Cast To Number"+nfe);
						}
					}

					Cookie registryTypesEvent=null;
				if (refId.equalsIgnoreCase(BBBCoreConstants.WED_CHANNEL_REF) || refId.equalsIgnoreCase(BBBCoreConstants.THEBUMP_REF)) {
					final String eventType = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS_REG_EVENTS, refId).get(0);

						// setting the VO in session
						sessionBean.setRegistryTypesEvent(eventType);
						registryTypesEvent = new Cookie(REGISTRY_TYPE_EVENT, eventType);

						if(registryTypesEvent!=null){
							if(maxTimeout>0){
							registryTypesEvent.setMaxAge(maxTimeout);
						}
						registryTypesEvent.setPath(BBBCoreConstants.SLASH);
						pResponse.addCookie(registryTypesEvent);
					}
				}

				sessionBean.getValues().put(BBBCoreConstants.REF_URL_VO, referralVO);

				final Cookie refIdCookie = new Cookie(REF_ID, referralVO.getReferralId());
				final Cookie queryStringCookie = new Cookie(QUERY_STRING, referralVO.getReferralUrl());
				final Cookie curUrlCookie = new Cookie(CUR_URL, referralVO.getCurrentUrl());
				final Cookie referrerDomainCookie = new Cookie(REFERRER_DOMAIN, referralVO.getReferrerDomain());

				if (maxTimeout > 0) {
					refIdCookie.setMaxAge(maxTimeout);
					queryStringCookie.setMaxAge(maxTimeout);
					curUrlCookie.setMaxAge(maxTimeout);
					referrerDomainCookie.setMaxAge(maxTimeout);
				}
				refIdCookie.setPath(BBBCoreConstants.SLASH);
				curUrlCookie.setPath(BBBCoreConstants.SLASH);
				queryStringCookie.setPath(BBBCoreConstants.SLASH);
				referrerDomainCookie.setPath(BBBCoreConstants.SLASH);
				pResponse.addCookie(refIdCookie);
				pResponse.addCookie(queryStringCookie);
				pResponse.addCookie(curUrlCookie);
				pResponse.addCookie(referrerDomainCookie);
				if (this.isLoggingDebug()) {
					this.logDebug("Cookie is set for refId=" + refId
							+ " setReferralUrl=" + queryString
							+ " setCurrentUrl=" + curUrl + " paramValueMap="
							+ paramValueMap);
				}
			} else {
				if (this.isLoggingDebug()) {
					this.logDebug("BBBReferralContoller : Referral Id is null or Invalid");
				}
			}

		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.REFERRAL_WEBLINK,
					methodName);
		}

	}

	/**
	 * This method finds out the referrer host name
	 *
	 * Example
	 * <ul>
	 * <li>Referrer</li>
	 * <li>http://registry.weddingchannel.com/cs/search.action?auid=950&ainfo=
	 * search_box&cr=registry_search&sec=navbar</li>
	 * </ul>
	 *
	 * This method will return referrer domain
	 * http://registry.weddingchannel.com
	 *
	 * @param referrer
	 * @return
	 * @throws MalformedURLException
	 */
	private String findReferrerDomain(final String referrer)
			throws MalformedURLException {

		String referrerDomain = null;
		if (!BBBUtility.isEmpty(referrer)) {
			final URL url = new URL(referrer);
			referrerDomain = url.getProtocol() + BBBCoreConstants.COLON
					+ BBBCoreConstants.SLASH + BBBCoreConstants.SLASH
					+ url.getHost() + BBBCoreConstants.SLASH;
		}
		return referrerDomain;

	}

	/**
	 * This method check if referrer is valid as per referrerPatterns
	 *
	 * @param referrer
	 * @return boolean
	 */
/*	private boolean isReferrerValid(String referrer) throws BBBSystemException,
			BBBBusinessException {
		List<String> referrerPatterns = null;
		boolean isValid = false;

		referrerPatterns = (List<String>) getCatalogTools().getAllValuesForKey(
				BBBCoreConstants.REFERRAl_CONTROLS,
				BBBCoreConstants.REFERRER_PATTERNS);

		if (isLoggingDebug()) {
			logDebug("BBBReferralController.isReferrerValid - referrerPatterns "
					+ referrerPatterns);
		}

		if (referrerPatterns != null && !referrerPatterns.isEmpty()) {
			for (String referrerPattern : referrerPatterns) {
				if (referrer.indexOf(referrerPattern) >= 0) {
					isValid = true;
					break;
				}
			}
		}
		return isValid;

	}
*/
	/**
	 * Check if request is for commission junction sale
	 *
	 * @param pRequest
	 * @param paramValueMap
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private boolean checkCommisionJunction(
			final DynamoHttpServletRequest pRequest,
			final Map<String, String> paramValueMap) throws BBBSystemException,
			BBBBusinessException {

		boolean isCJMatch = false;

		final String inputCJParam = this.getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.REFERRAl_CONTROLS).get(BBBCoreConstants.CJ_REF_URL_PARAM);
		final String inputCJParamValue = this.getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.REFERRAl_CONTROLS).get(BBBCoreConstants.CJ_REF_URL_PARAM_PREFIX);

		if (BBBUtility.isEmpty(inputCJParam)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_CJ_REF_URL_PARAM,
					"cj_ref_url_param missing in config keys");
		}

		final String commissionJuncReqParam = pRequest.getParameter(inputCJParam);

		if (!BBBUtility.isEmpty(commissionJuncReqParam)) {
			if (this.isLoggingDebug()) {
				this.logDebug("BBBReferralContoller.checkCommisionJunction : commissionJuncReqParam=" + commissionJuncReqParam);
			}
			isCJMatch = commissionJuncReqParam.indexOf(inputCJParamValue) >= 0 ? true : false;
			if (isCJMatch) {
				paramValueMap.put(BBBCoreConstants.CJ_CID_PARAM, commissionJuncReqParam);
			}
		}

		return isCJMatch;

	}

}
