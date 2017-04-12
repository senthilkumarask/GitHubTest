package com.bbb.certona.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;




import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.certona.vo.CertonaRequestVO;
import com.bbb.certona.vo.CertonaResonanceItemVO;
import com.bbb.certona.vo.CertonaResponseVO;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPQueryManager;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;


/**
 * The class fetches the certona response from the web
 * service. - siteId,isRobot,ResponseVO,schemenames
 * @author rjai39
 * 
 */
public class CertonaResponseUtils extends BBBGenericService {

	private HTTPQueryManager mHttpQueryManager;
	private BBBCatalogTools mCatalogTools;
	private Map<String, String> mSiteIdAppIdMap;
	private BBBInventoryManager mInventoryManager;
	private static final String CLS_NAME = "CLS=[CertonaResponseUtils]/MSG::";
	
	/**
	 * Method to fetch the certona response from the web
	 * service. - siteId,isRobot,ResponseVO,schemenames
	 *  
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @return Certona Response VO in <code>CertonaResponseVO</code> format
	 * @throws BBBBusinessException, BBBSystemException
	 * @throws UnsupportedEncodingException 
	 */
	public CertonaResponseVO getCertonaResponseVO(final DynamoHttpServletRequest pRequest, 
			final DynamoHttpServletResponse pResponse) throws BBBBusinessException, BBBSystemException, UnsupportedEncodingException {

		BBBPerformanceMonitor.start(CertonaResponseUtils.class.getName() + " : " + "getCertonaResponseVO");
		this.logDebug(CLS_NAME + "entering getCertonaResponseVO method");
		
		final String shippingThreshold = pRequest.getParameter(BBBCertonaConstants.SHIPPINGTHRESHOLD);
		
		String recommendedProd = pRequest.getParameter(BBBCertonaConstants.RECOMM_PRODUCTS);
		
		Boolean babyCAMode = false;
		boolean isRobot = false;
		boolean registryProdParam = false;
		if(recommendedProd!=null && recommendedProd.equalsIgnoreCase("true")){
			registryProdParam = true;
		}
		if (pRequest.getObjectParameter(BBBCertonaConstants.BABY_CA_MODE) != null) {
			babyCAMode = Boolean.parseBoolean((String) pRequest.getObjectParameter(BBBCertonaConstants.BABY_CA_MODE));
		}

		String siteId = pRequest.getParameter(BBBCertonaConstants.SITE_ID);
		String isInternationalCustomer = pRequest.getParameter(BBBCertonaConstants.IS_INTERNATIONAL_CUSTOMER);
		if(StringUtils.isBlank(siteId)) {
			siteId = SiteContextManager.getCurrentSiteId();
		}
		String schemeNames = pRequest.getParameter(BBBCertonaConstants.SCHEME);
		if (schemeNames == null) {
			schemeNames = "";
		}

		if (StringUtils.isNotBlank(schemeNames)
				&& BBBCertonaConstants.SEARCH_RR.equalsIgnoreCase(schemeNames)) {
			pRequest.setParameter(BBBCertonaConstants.SEARCH_SCHEME_OUTPUT, BBBCertonaConstants.OUTPUT_XML);
		}
		CertonaResponseVO certonaResponseVO = null;
		boolean isRspnseAvble = false;

		final String content = this.getCertonaTag(siteId);
		/* Invoke Certona in case CertaonTag is enable */
		isRobot = BBBUtility.isRobot(pRequest);
		if (BBBCoreConstants.TRUE.equalsIgnoreCase(content) && !isRobot) {
			final CertonaRequestVO certonaRequestVO = this.populateRequestVO(siteId, schemeNames, pRequest, shippingThreshold, babyCAMode);
			if (certonaRequestVO != null) {
				if(this.isLoggingDebug()){
					this.logDebug("certona request VO before making certona call : "+certonaRequestVO);
				}
				certonaResponseVO = (CertonaResponseVO) this.getHttpQueryManager().invoke(certonaRequestVO);

				if ((certonaResponseVO != null) && (certonaResponseVO.getError() != null)) {
					this.logError("getCertonaResponseVO: RequestVO passed in webservice call" + certonaRequestVO + 
							"getCertonaResponseVO: ResponseVO passed returned from webservice call" + certonaResponseVO);
				}
			}
		}

		if (this.isVOEmpty(certonaResponseVO)) {
			pRequest.setParameter(BBBCoreConstants.WEBSERVICE_ERROR ,BBBCoreErrorConstants.ERROR_CERTONA_1001);
			
			// backup behaviour for lmi
			if (schemeNames.equalsIgnoreCase(BBBCertonaConstants.FULL_CART_LAST_MINUTE_ITEMS)) {
				// for full cart lastminute items add backup skus from
				// config
				certonaResponseVO = this.addBackupLMISKUDetailsVOList(siteId, certonaResponseVO);

				if (!certonaResponseVO.getResonanceMap().get(BBBCertonaConstants.FULL_CART_LAST_MINUTE_ITEMS).getSkuDetailVOList().isEmpty()) {
					isRspnseAvble = true;
				}
			}

		} else {
			// lmi & top reg items need SKU details VO list
			if ((schemeNames.equalsIgnoreCase(BBBCertonaConstants.FULL_CART_LAST_MINUTE_ITEMS) || schemeNames.equalsIgnoreCase(BBBCertonaConstants.GIFT_REGISTRY_TOP_REG_ITEMS)) && !registryProdParam) {

				this.addSKUDetailsVOList(siteId, schemeNames, certonaResponseVO,isInternationalCustomer);
				isRspnseAvble = true;
			} else {
				this.addProductsVOList(siteId, schemeNames, certonaResponseVO,registryProdParam);
				isRspnseAvble = true;
			}
		}

		if (isRspnseAvble) {

			this.logDebug(CLS_NAME + " Response is available ");

			this.filteOutBCCDisabledSchemes(certonaResponseVO, schemeNames);
			String linkString = "";
			if (certonaResponseVO != null) {
				linkString = this.createLinkString(certonaResponseVO.getResonanceMap());

				// pRequest.setParameter(LINK_STRING, "");
				certonaResponseVO.setResxLinks(linkString);
				
				//start (BPS-2413) OOS recommendation: New certona slot & tagging - MSWP
				String groupForResLink = pRequest.getParameter(BBBCertonaConstants.GROUP_FOR_RESLINK);
				
				this.logDebug(CLS_NAME + " groupForResLink ::=================> " + groupForResLink);
				
				if (StringUtils.isNotBlank(groupForResLink)) {

					Map<String, String> resxLinksMap = this.createLinkMap(groupForResLink, certonaResponseVO.getResonanceMap());
					certonaResponseVO.setResxLinksMap(resxLinksMap);
				}
				//end (BPS-2413) OOS recommendation: New certona slot & tagging - MSWP
				
				this.addCertonaCookies(pRequest, pResponse, certonaResponseVO.getTrackingId());
			}
			this.logDebug(CLS_NAME + "exiting getCertonaResponseVO method returning CertonaResponseVO : " + certonaResponseVO);
			BBBPerformanceMonitor.end(CertonaResponseUtils.class.getName() + " : " + "getCertonaResponseVO");
			return certonaResponseVO;
		} else {
			if (isRobot) {
				logDebug("ROBOT Found");
				pRequest.setParameter("isRobot", isRobot);
			}
			this.logDebug(CLS_NAME + "exiting getCertonaResponseVO method returning CertonaResponseVO as null");
			BBBPerformanceMonitor.end(CertonaResponseUtils.class.getName() + " : " + "getCertonaResponseVO");
			return null;
		}
	}

	/**
	 * Populate CertonaRequestVo with required parameters
	 * 
	 * @param pRequest
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public CertonaRequestVO populateRequestVO(final String pSiteId, final String pSchemeNames, final DynamoHttpServletRequest pRequest, 
			final String shippingThreshold, final boolean isbabyCAMode) throws UnsupportedEncodingException {

		// create parameters values map using all request parameters
		final Map<String, String> paramsValuesMap = new HashMap<String, String>();

		@SuppressWarnings("unchecked")
		final Enumeration<String> paramsEnum = pRequest.getParameterNames();
		while (paramsEnum.hasMoreElements()) {
			final String paramName = paramsEnum.nextElement();
			final String paramValue = pRequest.getParameter(paramName);
			paramsValuesMap.put(paramName, paramValue);
		}
		if(!BBBUtility.isEmpty(paramsValuesMap.get(BBBCertonaConstants.PARAM_SEARCH_TERMS))){
			String searchTerm = (String)paramsValuesMap.get(BBBCertonaConstants.PARAM_SEARCH_TERMS);
			if(searchTerm.contains(BBBCertonaConstants.QUOTES))
			{
				searchTerm = searchTerm.replaceAll(BBBCertonaConstants.QUOTES, BBBCoreConstants.BLANK);
				searchTerm = searchTerm.trim();
			}
			searchTerm = URLEncoder.encode(searchTerm, BBBCoreConstants.UTF_ENCODING);
			paramsValuesMap.put(BBBCertonaConstants.PARAM_SEARCH_TERMS, searchTerm);
		}
		// Adding shippingThreshold to the certona call.
		if (BBBUtility.isEmpty(shippingThreshold)) {
			paramsValuesMap.put(BBBCertonaConstants.FREESHIPPINGTHRESHOLD, BBBCertonaConstants.SHIPPINGTHRESHOLDVALUE);
		} else {
			paramsValuesMap.put(BBBCertonaConstants.FREESHIPPINGTHRESHOLD, shippingThreshold);
		}
		if (!paramsValuesMap.containsKey(BBBCertonaConstants.TRACKINGID)) {
			final String trackingId = pRequest.getCookieParameter(BBBCertonaConstants.CERTONA_RES_TRACKING_COOKIE_NAME);
			if (BBBUtility.isEmpty(trackingId)) {
				paramsValuesMap.put("trackingid", "");
			} else {
				paramsValuesMap.put("trackingid", trackingId);
			}
		}
		final String fromRest = pRequest.getParameter(BBBCertonaConstants.FROM_REST);
		if (StringUtils.equalsIgnoreCase(fromRest, "true")) {
			try {
				Map<String, String> siteAppIdRestMap = new HashMap<String, String>();
				siteAppIdRestMap = this.getCatalogTools().getConfigValueByconfigType("MobileWebConfig");
				// set appId, trackingID, jsessionId
				paramsValuesMap.put("appid", siteAppIdRestMap.get(pSiteId));

			} catch (final BBBSystemException e) {
				this.logDebug("CertonaDroplet.populateRequestVO() :: System Exception thrown while fetching config key values of config type: CertonaKeys", e);
			} catch (final BBBBusinessException e) {
				this.logDebug("CertonaDroplet.populateRequestVO() :: Business Exception thrown while fetching config key values of config type: CertonaKeys", e);
			}
		} else {
			// set appId, trackingID, jsessionId
			paramsValuesMap.put("appid", this.mSiteIdAppIdMap.get(pSiteId));
		}
		if (!paramsValuesMap.containsKey(BBBCertonaConstants.SESSIONID)) {
			final String sessionId = pRequest.getCookieParameter(BBBCertonaConstants.CERTONA_RES_SESSIONID_COOKIE_NAME);
			if (!BBBUtility.isEmpty(sessionId)) {
				paramsValuesMap.put("sessionid", sessionId);
			}
		}

		// BBBSL-2232-Start. Added IP in CertonaRequest
		Map<String, String> configMap;
		try {
			String ipAddress = pRequest.getRemoteAddr();
			configMap = this.getCatalogTools().getConfigValueByconfigType(BBBCertonaConstants.CARTANDCHECKOUTKEYS);
			final String ipHeaderName = configMap.get(BBBCertonaConstants.TRUE_IP_HEADER);

			if (BBBUtility.isNotEmpty(ipHeaderName)) {
				final String trueIP = pRequest.getHeader(ipHeaderName);
				if (BBBUtility.isNotEmpty(trueIP)) {
					ipAddress = trueIP;
				}
			}
			paramsValuesMap.put(BBBCertonaConstants.IPADDRESS, ipAddress);

		} catch (final BBBSystemException e) {
			this.logError("Error in fetching TRUE_IP_HEADER", e);
		} catch (final BBBBusinessException e) {
			this.logError("Error in fetching TRUE_IP_HEADER", e);
		}

		// BBBSL-2232-End. Added IP in CertonaRequest
		StringBuffer schemeBuff = null;
		if (pSchemeNames != null) {

			final StringTokenizer sTokenizer = new StringTokenizer(pSchemeNames, BBBCertonaConstants.SEMICOL_DELIMITER);

			schemeBuff = new StringBuffer("");

			// for each scheme, create productVOs list
			while (sTokenizer.hasMoreTokens()) {
				final String schemeName = sTokenizer.nextToken();
				if(!BBBUtility.isEmpty(schemeName)) schemeBuff.append(BBBCertonaConstants.CERTONA).append('_').append(schemeName).append(';');
			}

			final int lstIndxSemiColon = schemeBuff.lastIndexOf(";");
			if (lstIndxSemiColon > 0) {
				schemeBuff.replace(lstIndxSemiColon, lstIndxSemiColon, "");
			}
		}

		if (isbabyCAMode) {
			paramsValuesMap.put(BBBCertonaConstants.CO_BRAND, BBBCertonaConstants.BABY_CANADA);
			paramsValuesMap.put(BBBCertonaConstants.BABYCA_PARAMETER, BBBCertonaConstants.BABYCA_PARAMETER_TRUE);
		}
		CertonaRequestVO certonaRequestVO = null;
		if (schemeBuff != null) {
			// final String serviceName = CERTONA +"_" +pSchemeNames;

			certonaRequestVO = new CertonaRequestVO();
			certonaRequestVO.setServiceName(schemeBuff.toString());
			certonaRequestVO.setParamsValuesMap(paramsValuesMap);
			certonaRequestVO.setSiteId(pSiteId);
			certonaRequestVO.setServiceType(BBBCertonaConstants.CERTONA);

			this.logDebug(CLS_NAME + " MTHD=[populateRequestVO]/ serviceName=" + schemeBuff + " paramsValuesMap=" + paramsValuesMap);
		}
		return certonaRequestVO;
	}

	/**
	 * This method check if Certona is on for this site
	 * 
	 * @param currentSiteId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String getCertonaTag(final String currentSiteId) throws BBBBusinessException, BBBSystemException {

		String certonaOn = null;

		if (currentSiteId == null) {
			return null;
		}
		final String bedBathUSSite = this.getCatalogTools().getContentCatalogConfigration("BedBathUSSiteCode").get(0);
		final String buyBuyBabySite = this.getCatalogTools().getContentCatalogConfigration("BuyBuyBabySiteCode").get(0);
		final String bedBathCanadaSite = this.getCatalogTools().getContentCatalogConfigration("BedBathCanadaSiteCode").get(0);
		
		final String tbsBedBathUSSite = this.getCatalogTools().getContentCatalogConfigration("TBS_BedBathUSSiteCode").get(0);
		final String tbsBuyBuyBabySite = this.getCatalogTools().getContentCatalogConfigration("TBS_BuyBuyBabySiteCode").get(0);
		final String tbsBedBathCanadaSite = this.getCatalogTools().getContentCatalogConfigration("TBS_BedBathCanadaSiteCode").get(0);

		if (currentSiteId.equalsIgnoreCase(bedBathUSSite) || currentSiteId.equalsIgnoreCase(tbsBedBathUSSite)) {
			certonaOn = this.getCatalogTools().getContentCatalogConfigration("CertonaTag_us").get(0);

		} else if (currentSiteId.equalsIgnoreCase(buyBuyBabySite) || currentSiteId.equalsIgnoreCase(tbsBuyBuyBabySite)) {
			certonaOn = this.getCatalogTools().getContentCatalogConfigration("CertonaTag_baby").get(0);

		} else if (currentSiteId.equalsIgnoreCase(bedBathCanadaSite) || currentSiteId.equalsIgnoreCase(tbsBedBathCanadaSite)) {
			certonaOn = this.getCatalogTools().getContentCatalogConfigration("CertonaTag_ca").get(0);
		}

		return certonaOn;
	}

	/**
	 * Fiter out schemes whiche are bcc disabled
	 * 
	 * @param schemeNames
	 * @return
	 */
	private void filteOutBCCDisabledSchemes(final CertonaResponseVO pResponseVO, final String schemeNames) {

		this.logDebug(CLS_NAME + " Filtering out bcc disabled schems");
		if (schemeNames != null) {
			final StringTokenizer sTokenizer = new StringTokenizer(schemeNames, BBBCertonaConstants.SEMICOL_DELIMITER);

			this.logDebug(CLS_NAME + " ShemeNames:==" + schemeNames);

			while (sTokenizer.hasMoreTokens()) {
				final String scheme = sTokenizer.nextToken();

				if (!BBBUtility.isEmpty(scheme)) {

					final boolean bccEnabled = this.checkBCCActivation(scheme);

					// if bcc disabled, filter remove the result
					if (!bccEnabled) {
						pResponseVO.getResonanceMap().put(scheme, null);
						this.logDebug(CLS_NAME + " sheme is disabled hence removing it from VO, schemeName==" + scheme);
					}
				}
			}
		}

	}

	/**
	 * This method test if bcc has enabled certona request for any of the
	 * following slots
	 * 
	 * FULL_CART_LAST_MINUTE_ITEMS PRODDETAIL_FREQUENT_BOUGHT
	 * PRODDETAIL_CUSTOMER_VIEWED PRODDETAIL_COLL_FREQUENT_BOUGHT
	 * PRODDETAIL_COLL_CUSTOMER_VIEWED PRODDETAIL_ACC_FREQUENT_BOUGHT
	 * PRODDETAIL_ACC_CUSTOMER_VIEWED
	 * 
	 * If the bcc disables the slot (tab) then no request is sent to certona
	 * 
	 * @param schemeName
	 * @param pRequest
	 * @return
	 */
	private boolean checkBCCActivation(final String schemeName) {

		boolean bccCntnrActve = true;
		List<String> enableContainer = null;

		final String key = BBBCertonaConstants.CERTONA + "_" + schemeName;

		if (BBBCertonaConstants.PRODDETAIL_CUSTOMER_VIEWED.equalsIgnoreCase(schemeName) || BBBCertonaConstants.PRODDETAIL_FREQUENT_BOUGHT.equalsIgnoreCase(schemeName)
				|| BBBCertonaConstants.FULL_CART_LAST_MINUTE_ITEMS.equalsIgnoreCase(schemeName) || BBBCertonaConstants.PRODDETAIL_COLL_FREQUENT_BOUGHT.equalsIgnoreCase(schemeName) 
					|| BBBCertonaConstants.PRODDETAIL_COLL_CUSTOMER_VIEWED.equalsIgnoreCase(schemeName) || BBBCertonaConstants.PRODDETAIL_ACC_FREQUENT_BOUGHT.equalsIgnoreCase(schemeName) 
						|| BBBCertonaConstants.PRODDETAIL_ACC_CUSTOMER_VIEWED.equalsIgnoreCase(schemeName)) {

			try {
				/* check if container is active in bcc */
				enableContainer = this.getCatalogTools().getAllValuesForKey(BBBCertonaConstants.FLAG_DRIVEN_CONFIG_KEY, key);

				if ((enableContainer != null) && !enableContainer.isEmpty()) {
					bccCntnrActve = Boolean.parseBoolean(enableContainer.get(0));
				}
			} catch (final BBBSystemException e) {
				bccCntnrActve = false;
			} catch (final BBBBusinessException e) {
				bccCntnrActve = false;
			}
		}

		this.logDebug(CLS_NAME + " MTHD=[checkBCCActivation]" + " bccContainerActive " + bccCntnrActve + " for " + key);

		return bccCntnrActve;
	}

	/**
	 * Checks if certonaResponseVO is empty
	 * 
	 * @param certonaResponseVO
	 * @return boolean
	 */
	private boolean isVOEmpty(final CertonaResponseVO certonaResponseVO) {

		this.logDebug(CLS_NAME + " Check isVOEmpty");
		boolean isEmpty = true;

		if ((null == certonaResponseVO) || (null != certonaResponseVO.getError())) {
			isEmpty = true;
		} else {

			final Map<String, CertonaResonanceItemVO> resonanceMap = certonaResponseVO.getResonanceMap();
			if (null == resonanceMap) {
				isEmpty = true;
			} else {

				final Set<String> keySet = resonanceMap.keySet();
				final Iterator<String> keyIter = keySet.iterator();
				while (keyIter.hasNext()) {

					final String key = keyIter.next();
					final CertonaResonanceItemVO resonanceItemVO = resonanceMap.get(key);

					if ((null == resonanceItemVO.getProductIDsList()) || (resonanceItemVO.getProductIDsList().size() == 0)) {
						isEmpty = true;
						this.logDebug(CLS_NAME + " Vo is having no data for scheme =" + key);

					} else {
						isEmpty = false;
						break;
					}
				}
			}
		}
		this.logDebug(CLS_NAME + "isVOEmpty=" + isEmpty);

		return isEmpty;
	}

	/**
	 * Adding certona cookies to response
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param trackingId
	 */
	private void addCertonaCookies(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse, final String trackingId) {

		this.logDebug(CLS_NAME + " entering MTHD=[addCertonaCookies]");

		// check if there is an existing cookie for trackingId
		final String cookie = pRequest.getCookieParameter(BBBCertonaConstants.CERTONA_RES_TRACKING_COOKIE_NAME);

		if (((cookie == null) || (cookie.length() == 0)) && (trackingId != null)) {

			// creating new cookie
			final Cookie trackingCookie = new Cookie(BBBCertonaConstants.CERTONA_RES_TRACKING_COOKIE_NAME, trackingId);
			// set its max age in seconds so that it is a permanent cookie
			trackingCookie.setMaxAge(BBBCertonaConstants.CERTONA_RES_COOKIE_AGE);
			// setting path of the cookie to store
			// trackingCookie.setPath(CERTONA_RES_COOKIE_PATH);
			// trackingCookie.setDomain(pRequest.getRemoteHost());
			String path = pRequest.getParameter(BBBCoreConstants.PATH_REQ_PARAM);		 
			String domain = pRequest.getParameter(BBBCoreConstants.DOMAIN_REQ_PARAM);
			if(BBBUtility.isEmpty(domain)){
				if(pRequest.getServerName()!=null) {
					domain = pRequest.getServerName();
				} else {
					domain = BBBCoreConstants.BLANK;
				}
			}
			if(BBBUtility.isEmpty(path)){
				path = BBBCoreConstants.SLASH;
			}
			trackingCookie.setDomain(domain);
			trackingCookie.setPath(path);
		 
			// add the cookie to the pResponse object
			// pResponse.addCookie(trackingCookie);
			BBBUtility.addCookie(pResponse, trackingCookie, false);
			this.logDebug(CLS_NAME + " MTDH=[addCertonaCookies]/Cookie set: " + trackingCookie.getName());

		}

		this.logDebug(CLS_NAME + " exiting MTHD=[addCertonaCookies]");

	}

	/**
	 * This method provides backup behaviour for Last Minute Items in case
	 * Certona does not return any response
	 * 
	 * It fetches SKUs list from Config Repository and for each SKU id get
	 * SKUDetailsVO and add into the pResponseVO
	 * 
	 * @param siteId
	 * @param responseVO
	 */
	private CertonaResponseVO addBackupLMISKUDetailsVOList(final String pSiteId, final CertonaResponseVO pResponseVO) throws BBBBusinessException, BBBSystemException {

		this.logDebug(CLS_NAME + "entering addBackupLMISKUDetailsVOList method");

		CertonaResponseVO responseVO = pResponseVO;
		// if response is null then create new responseVO
		if (responseVO == null) {
			responseVO = new CertonaResponseVO();
		}

		final String key = pSiteId + "_" + BBBCertonaConstants.LAST_MINUTE_ITEMS;
		// create Mapping of skuID & its parent productId
		final Map<String, String> skuPrntPrdMap = new HashMap<String, String>();
		List<String> lstLstMinItms = null;
		final List<SKUDetailVO> lstSKUDtlsVO = new ArrayList<SKUDetailVO>();

		try {
			lstLstMinItms = this.getCatalogTools().getAllValuesForKey(BBBCertonaConstants.LMI_CONFIG_KEY, key);

		} catch (final BBBSystemException e) {
			this.logError(BBBCertonaConstants.BACKUP_LMI_CONFIG_RETREIVAL_ERR_MSG);
			throw e;

		} catch (final BBBBusinessException e) {
			this.logError(BBBCertonaConstants.BACKUP_LMI_CONFIG_RETREIVAL_ERR_MSG);
			throw e;
		}

		this.logDebug(CLS_NAME + " MTHD= [addBackupLMISKUDetailsVOList]/" + "List of last minute items in config=" + lstLstMinItms);

		if (null != lstLstMinItms) {
			for (final String skuId : lstLstMinItms) {

				try {
					lstSKUDtlsVO.add(this.getCatalogTools().getSKUDetails(pSiteId, skuId, true));

					// get SKU's parent productID
					final String productId = this.getCatalogTools().getParentProductForSku(skuId);
					skuPrntPrdMap.put(skuId, productId);

				} catch (final BBBSystemException e) {
					this.logError(BBBCertonaConstants.BACKUP_LMI_SKUVO_RETREIVAL_ERR_MSG + skuId);
					// continue with next sku
				} catch (final BBBBusinessException e) {
					this.logError(BBBCertonaConstants.BACKUP_LMI_SKUVO_RETREIVAL_ERR_MSG + skuId);
					// continue with next sku
				}

			}
		}

		for (final SKUDetailVO skuDetailVO : lstSKUDtlsVO) {
			this.logDebug("Sku Id in VO: " + skuDetailVO.getSkuId());
		}
		this.logDebug(CLS_NAME + "MTHD=[addBackupLMISKUDetailsVOList] " + " skuParentProductMap =" + skuPrntPrdMap);

		final Map<String, CertonaResonanceItemVO> resonanceMap = new HashMap<String, CertonaResonanceItemVO>();
		final CertonaResonanceItemVO resonanceItem = new CertonaResonanceItemVO();

		resonanceItem.setSkuDetailVOList(lstSKUDtlsVO);
		resonanceItem.setSkuParentProductMap(skuPrntPrdMap);

		resonanceMap.put(BBBCertonaConstants.FULL_CART_LAST_MINUTE_ITEMS, resonanceItem);

		responseVO.setResonanceMap(resonanceMap);

		if (lstSKUDtlsVO.isEmpty()) {
			this.logError(CLS_NAME + "Empty list of SKUDetailsVO ");
			throw new BBBBusinessException("ERR_BIZ_EMPTY_BACKUP_SKUVOLIST", "ERR_BIZ_EMPTY_BACKUP_SKUVOLIST");
		}

		this.logDebug(CLS_NAME + "exitting addBackupLMISKUDetailsVOList method");

		return responseVO;
	}

	/**
	 * This method fetches SKU with max inventory for each productID returned
	 * from Certona WS and create list of SKUDetailsVO with those SKUs
	 * 
	 * It fetches SKUs list from Config Repository and for each SKU id get
	 * SKUDetailsVO and add into the pResponseVO
	 * 
	 * @param siteId
	 * @param responseVO
	 */
	private void addSKUDetailsVOList(final String pSiteId, final String schemeName, final CertonaResponseVO pResponseVO,String isInternationalCustomer) throws BBBBusinessException {
		int inStockStatus;

		// for this method we expect schemeNames contain only one scheme & no ;
		this.logDebug(CLS_NAME + " Start addSKUDetailsVOList");

		final CertonaResonanceItemVO certonaResVO = pResponseVO.getResonanceMap().get(schemeName);

		final List<String> productIDsList = certonaResVO.getProductIDsList();
		final List<String> skuMaxInvntryLst = new ArrayList<String>();
		final List<SKUDetailVO> lstSKUDtlsVO = new ArrayList<SKUDetailVO>();
		final Map<String, String> skuPrntProdMap = new HashMap<String, String>();
		//BSL-4323 | Creating list to store productIds of Active products
		final List<String> activeProductIDsList = new ArrayList<String>();
		
		final StringBuilder sbuilder = new StringBuilder();
		// for each productId fetch skuId with max Inventory
		for (final String productId : productIDsList) {
			try {
				ProductVO productVO  = this.getCatalogTools().getProductDetails(pSiteId, productId, false,false,true);
				activeProductIDsList.add(productVO.getProductId());
				final String skuID = this.getCatalogTools().getMaxInventorySKUForProduct(productVO, pSiteId);
				if (!BBBUtility.isEmpty(skuID)) {
					skuMaxInvntryLst.add(skuID);
					skuPrntProdMap.put(skuID, productId);
				}
			} catch (final BBBBusinessException ex) {

				sbuilder.append(BBBCertonaConstants.SKU_MAX_INVENTORY_RETRIEVAL_ERR_MSG);
				sbuilder.append(productId);
				sbuilder.append(' ');
				sbuilder.append(ex.getMessage());
				this.logError(sbuilder.toString());
				sbuilder.delete(0, sbuilder.length());
				// continue with next product
			} catch (final BBBSystemException ex) {

				sbuilder.append(BBBCertonaConstants.SKU_MAX_INVENTORY_RETRIEVAL_ERR_MSG);
				sbuilder.append(productId);
				sbuilder.append(' ');
				sbuilder.append(ex.getMessage());
				this.logError(sbuilder.toString());
				sbuilder.delete(0, sbuilder.length());
				// continue with next product
			}
		}

		this.logDebug(CLS_NAME + " MTHD::addSKUDetailsVOList :: Sku Id lis= " + skuMaxInvntryLst + "  ] skuParentProductMap =" + skuPrntProdMap);
		

		for (final String skuId : skuMaxInvntryLst) {

			try {
				inStockStatus = this.getInventoryManager().getProductAvailability(pSiteId, skuId, BBBInventoryManager.PRODUCT_DISPLAY, 0);
				if (schemeName.equalsIgnoreCase(BBBCertonaConstants.FULL_CART_LAST_MINUTE_ITEMS)) {
					if (inStockStatus == BBBInventoryManager.AVAILABLE || inStockStatus == BBBInventoryManager.LIMITED_STOCK) {
						lstSKUDtlsVO.add(this.getCatalogTools().getSKUDetails(pSiteId, skuId, true,isInternationalCustomer));
					}
				} else {
					lstSKUDtlsVO.add(this.getCatalogTools().getSKUDetails(pSiteId, skuId, true));
				}
				if(lstSKUDtlsVO.size()==3 && Boolean.valueOf(isInternationalCustomer) && schemeName.equalsIgnoreCase(BBBCertonaConstants.FULL_CART_LAST_MINUTE_ITEMS))
				{
					break;
				}

			} catch (final BBBSystemException ex) {
				sbuilder.append(BBBCertonaConstants.SKU_DETAIL_RETRIEVAL_ERR_MSG);
				sbuilder.append(skuId);
				sbuilder.append(' ');
				sbuilder.append(ex.getMessage());
				this.logError(sbuilder.toString());
				sbuilder.delete(0, sbuilder.length());
				// continue with next sku
			} catch (final BBBBusinessException ex) {
				sbuilder.append(BBBCertonaConstants.SKU_DETAIL_RETRIEVAL_ERR_MSG);
				sbuilder.append(skuId);
				sbuilder.append(' ');
				sbuilder.append(ex.getMessage());
				this.logError(sbuilder.toString());
				sbuilder.delete(0, sbuilder.length());
				// continue with next sku
			}
		}

		for (final SKUDetailVO skuDetailVO : lstSKUDtlsVO) {
			this.logDebug(CLS_NAME + " MTHD::addSKUDetailsVOList :: VO Sku Id= " + skuDetailVO.getSkuId());
		}
		
		certonaResVO.setActiveProductIDsList(activeProductIDsList);
		certonaResVO.setSkuDetailVOList(lstSKUDtlsVO);
		certonaResVO.setSkuParentProductMap(skuPrntProdMap);

		if (lstSKUDtlsVO.isEmpty()) {
			this.logError(CLS_NAME + "Empty list of SKUDetailsVO ");
			throw new BBBBusinessException("ERR_BIZ_EMPTY_SKUVOLIST", "ERR_BIZ_EMPTY_SKUVOLIST");
		}

	}

	/**
	 * This private method will iterate over list of product ids stored in
	 * responseVO & fetch the vo object of each product using catalog tool
	 * 
	 * @param siteId
	 * @param responseVO
	 */
	private void addProductsVOList(final String pSiteId, final String schemeNames, final CertonaResponseVO pResponseVO, final boolean registryProdParam) throws BBBBusinessException {

		// schemeNames can be multple schems with ; separted
		final long startTime = System.currentTimeMillis();
		this.logDebug(CLS_NAME + " Start addProductsVOList");
		boolean isResponseVOEmpty = true;

		if (schemeNames != null) {

			final StringTokenizer sTokenizer = new StringTokenizer(schemeNames, BBBCertonaConstants.SEMICOL_DELIMITER);
			final StringBuilder sbuilder = new StringBuilder();

			// for each scheme, create productVOs list
			while (sTokenizer.hasMoreTokens()) {

				final String schemeName = sTokenizer.nextToken();

				if (!BBBUtility.isEmpty(schemeName)) {

					final CertonaResonanceItemVO certonaResVO = pResponseVO.getResonanceMap().get(schemeName);

					if (certonaResVO != null) {

						final List<String> productIDsList = certonaResVO.getProductIDsList();
						final List<ProductVO> productsVOList = new ArrayList<ProductVO>();
						//BSL-4323 | Creating list to store productIds of Active products
						final List<String> activeProductIDsList = new ArrayList<String>();

						final Iterator<String> productIDsIter = productIDsList.iterator();

						while (productIDsIter.hasNext()) {
							final String productId = productIDsIter.next();
							try {
								final long startTimeProd = System.currentTimeMillis();
								final ProductVO productVO = this.getCatalogTools().getProductDetails(pSiteId, productId, true);
								boolean isSKUPersonalizedItem = false;
								if (productVO.getChildSKUs() != null && productVO.getChildSKUs().size() == 1) {
										String skuId = productVO.getChildSKUs().get(0);
										SKUDetailVO defaultSkuVO= new SKUDetailVO();
										defaultSkuVO.setSkuId(skuId);
										try {
											
											if (registryProdParam) {
												isSKUPersonalizedItem = productManager.isPersonalizedSku(skuId);
												productVO.setPersonalizedSku(isSKUPersonalizedItem);
											}
											productVO.setDefaultSkuDetailVO(defaultSkuVO);
										} catch (BBBSystemException e) {
											this.logError("System Exception Occourred while getting isPersonalizedSku ",
													e);
										}
									}
								
								logDebug("Time taken for addProductsVOList:prod:" + productId + " is:" + (System.currentTimeMillis() - startTimeProd));
								productsVOList.add(productVO);
								activeProductIDsList.add(productVO.getProductId());
								

							} catch (final BBBBusinessException bbbExcep) {
								sbuilder.append(CLS_NAME);
								sbuilder.append("product detail not available for productId=").append(productId);
								sbuilder.append(' ');
								sbuilder.append(bbbExcep.getMessage());
								this.logError(sbuilder.toString());
								sbuilder.delete(0, sbuilder.length());

							} catch (final BBBSystemException bbbExcep) {
								sbuilder.append(CLS_NAME);
								sbuilder.append("product detail not available for productId=").append(productId);
								sbuilder.append(' ');
								sbuilder.append(bbbExcep.getMessage());
								this.logError(sbuilder.toString());
								sbuilder.delete(0, sbuilder.length());
							}
						}

						if (productsVOList.isEmpty()) {
							this.logError(CLS_NAME + "Empty list of productVOs for scheme= " + schemeName);

						} else {
							isResponseVOEmpty = false;
						}
						certonaResVO.setActiveProductIDsList(activeProductIDsList);
						certonaResVO.setProductsVOsList(productsVOList);

					}
				}
			}

		}

		this.logDebug(CLS_NAME + " responseVO after addition of productVOs =" + pResponseVO);

		logDebug("Time taken for addProductsVOList:overall:" + (System.currentTimeMillis() - startTime));
		if (isResponseVOEmpty) {
			throw new BBBBusinessException(BBBCoreErrorConstants.ERR_BIZ_EMPTY_PRODLIST_IN_CERTONA_RESPONSE_VO_OR_PRODUCT, BBBCoreErrorConstants.ERR_BIZ_EMPTY_PRODLIST_IN_CERTONA_RESPONSE_VO_OR_PRODUCT);
		}
	}

	private String createLinkString(final Map<String, CertonaResonanceItemVO> resonanceMap) {
		final StringBuilder finalString = new StringBuilder("");
		this.logDebug(CLS_NAME + " creating link string");

		if (resonanceMap != null) {
			final Set<String> keySet = resonanceMap.keySet();
			if (keySet != null) {
				final Iterator<String> iter = keySet.iterator();
				if (iter != null) {
					while (iter.hasNext()) {
						final String key = iter.next();
						final CertonaResonanceItemVO resonanceItemVO = resonanceMap.get(key);
						if (resonanceItemVO != null) {
							final List<String> productIDsList = resonanceItemVO.getActiveProductIDsList();
							if (productIDsList != null) {
								for (final String productId : productIDsList) {
									finalString.append(productId).append('|').append(key).append(';');
								}
							}
						}
					}
				}
			}
		}
		this.logDebug(CLS_NAME + "  link string = " + finalString.toString());
		return finalString.toString();
	}

	
	/**(BPS-2413) OOS recommendation: New certona slot & tagging - MSWP
	 * This method is used to create link on the basis of groups ( passed from jsp) and put the response in a map.
	 * 
	 * @param groupForResLink
	 * @param resonanceMap
	 * @return
	 */
	private Map<String, String> createLinkMap(final String groupForResLink,  final Map<String, CertonaResonanceItemVO> resonanceMap) {
	
		Map <String, String>  resxLinksMap = new HashMap<String, String>();
		
		String [] groupForResLinkArray = StringUtils.split(groupForResLink, '|');
		
		if (resonanceMap != null) {
		  for(String groupString : groupForResLinkArray){
			final StringBuilder finalString = new StringBuilder("");
			final Set<String> schemekeySet = resonanceMap.keySet();
			if (schemekeySet != null) {
				final Iterator<String> iter = schemekeySet.iterator();
				if (iter != null) {
					while (iter.hasNext()) {
						final String schemeName = iter.next();
						final CertonaResonanceItemVO resonanceItemVO = resonanceMap.get(schemeName);
						if (resonanceItemVO != null) {
							final List<String> productIDsList = resonanceItemVO.getActiveProductIDsList();
							if (productIDsList != null) {
								for (final String productId : productIDsList) {
									if(groupString.contains(schemeName))
									finalString.append(productId).append('|').append(schemeName).append(';');								
								}
							}
						}
					}
				}
			}
			this.logDebug(CLS_NAME + " Group ======> " + groupString + "value =======> " + finalString.toString());
			resxLinksMap.put(groupString, finalString.toString());
			 
		  }
		 }	
		
		return resxLinksMap;
	}
	


	/**
	 * @return the mHttpQueryManager
	 */
	public HTTPQueryManager getHttpQueryManager() {
		return mHttpQueryManager;
	}

	/**
	 * @param mHttpQueryManager the mHttpQueryManager to set
	 */
	public void setHttpQueryManager(final HTTPQueryManager mHttpQueryManager) {
		this.mHttpQueryManager = mHttpQueryManager;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}

	/**
	 * @return the siteIdAppIdMap
	 */
	public Map<String, String> getSiteIdAppIdMap() {
		return mSiteIdAppIdMap;
	}

	/**
	 * @param siteIdAppIdMap the siteIdAppIdMap to set
	 */
	public void setSiteIdAppIdMap(final Map<String, String> siteIdAppIdMap) {
		this.mSiteIdAppIdMap = siteIdAppIdMap;
	}

	/**
	 * @return the inventoryManager
	 */
	public BBBInventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	/**
	 * @param inventoryManager
	 *            the inventoryManager to set
	 */
	public void setInventoryManager(final BBBInventoryManager inventoryManager) {
		this.mInventoryManager = inventoryManager;
	}

	//product manager
	private ProductManager productManager;

	/**
	 * @return the productManager
	 */
	public ProductManager getProductManager() {
		return productManager;
	}

	/**
	 * @param productManager
	 *            the productManager to set
	 */
	public void setProductManager(final ProductManager productManager) {
		this.productManager = productManager;
	}
	
}
