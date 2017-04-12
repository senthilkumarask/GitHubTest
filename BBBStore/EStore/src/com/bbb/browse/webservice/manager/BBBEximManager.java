/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: ssh108
 *
 * Created on: 18-Feb-2014
 * --------------------------------------------------------------------------------
 */

package com.bbb.browse.webservice.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.bbb.account.BBBProfileManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.VendorInfoVO;
import com.bbb.commerce.exim.bean.EximCustomizedAttributesVO;
import com.bbb.commerce.exim.bean.EximImagePreviewVO;
import com.bbb.commerce.exim.bean.EximImagesVO;
import com.bbb.commerce.exim.bean.EximSessionBean;
import com.bbb.commerce.exim.bean.EximSummaryResponseVO;
import com.bbb.commerce.giftregistry.bean.LockAPIBean;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBEximConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;
import com.bbb.rest.catalog.vo.KatoriPriceRestVO;
import com.bbb.rest.output.BBBCustomTagComponent;
import com.bbb.utils.BBBUtility;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.core.util.StringUtils;
import atg.droplet.TagConversionException;
import atg.droplet.TagConverterManager;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;


/**
 * The Class BBBEximManager.
 *
 * @author BBB
 */

public class BBBEximManager extends BBBGenericService {
	
	private BBBCustomTagComponent customTagComponent;
	
	private LblTxtTemplateManager lblTxtTemplateManager;
	
	/** The http call invoker. */
	private HTTPCallInvoker httpCallInvoker;
	
	/** The catalog tools. */
	private BBBCatalogTools catalogTools;
	
	/** The exim repository. */
	
	private MutableRepository eximRepository;	
	
	/** Product Catalog Repository */
	private MutableRepository catalogRepository;
	
	private MutableRepository vendorRepository;
	
	public MutableRepository getVendorRepository() {
		return vendorRepository;
	}

	public void setVendorRepository(MutableRepository vendorRepository) {
		this.vendorRepository = vendorRepository;
	}

	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * Gets the exim repository.
	 *
	 * @return the exim repository
	 */
	public MutableRepository getEximRepository() {
		return eximRepository;
	}
	
	/**
	 * Sets the exim repository.
	 *
	 * @param eximRepository the new exim repository
	 */
	public void setEximRepository(MutableRepository eximRepository) {
		this.eximRepository = eximRepository;
	}
	
	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalog tools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/**
	 * Gets the http call invoker.
	 *
	 * @return httpCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return this.httpCallInvoker;
	}

	/**
	 * Sets the http call invoker.
	 *
	 * @param httpCallInvoker the new http call invoker
	 */
	public void setHttpCallInvoker(final HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}
	
	public BBBCustomTagComponent getCustomTagComponent() {
		return customTagComponent;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	public void setCustomTagComponent(BBBCustomTagComponent customTagComponent) {
		this.customTagComponent = customTagComponent;
	}
	
	/**
	 * This method is used to invoke the LOCK API after adding item
	 * to the registry.
	 *
	 * @param refNum the ref num
	 * @return the string
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	
	public String invokeLockAPI(String productId, String refNum) throws  BBBSystemException, BBBBusinessException {
		
		logDebug("GIFT REGISTRY MANAGER : Calling Lock API method");
		
		LockAPIBean lockAPIBean = new LockAPIBean();
		
		VendorInfoVO vendorInfoVO = getCatalogTools().getVendorInfo(
				getCatalogTools().getProductVOMetaDetails(SiteContextManager.getCurrentSite().getId(), productId).getVendorId());	
		
		
		HashMap<String, String> headerParam = new HashMap<String, String>();
		
		headerParam.put(BBBCoreConstants.X_ClientId, vendorInfoVO.getClientId());
		headerParam.put(BBBCoreConstants.X_ApiKey, vendorInfoVO.getApiKey());
		
		HashMap<String, String> serviceParameters = new HashMap<String, String>();
		serviceParameters.put(BBBCoreConstants.STATUS, BBBCoreConstants.LOCKED);
		
		String httpType = BBBCoreConstants.PUT;
		String view =BBBCoreConstants.LOCK_VIEW;
		
		lockAPIBean = getHttpCallInvoker().invokeToGetJson(LockAPIBean.class, headerParam, constructURL(view,vendorInfoVO.getApiURL(), refNum), serviceParameters, httpType);
		
		logDebug("The status is " + lockAPIBean.getStatus());
		
		return lockAPIBean.getStatus();
		
	}

	
	/**
	 * This methods invokes the summary API for personalized items
	 * for all the vendors
	 *
	 * @param refNum the ref num
	 * @return the exim summary response vo
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	
	public EximSummaryResponseVO invokeSummaryAPI(String productId,String vendor, String... refNum) throws BBBSystemException, BBBBusinessException {
		
		BBBPerformanceMonitor.start("BBBEximManager", "invokeSummaryAPI");
		
		logDebug("BBBEximManager : Calling summary API method");		
		
		logDebug("Product Id" + productId + "vendorName" + vendor );
		
		if(!BBBUtility.isEmpty(productId) && !BBBUtility.isEmpty(vendor)){
			logError("Both product Id and vendor Name are null. Only one of them should be null");
			return null;
		}
		
		VendorInfoVO vendorDetails = getVendorInfo(productId, vendor);
		
		EximSummaryResponseVO summaryResponse = new EximSummaryResponseVO();
		
		HashMap<String, String> headerParam = new HashMap<String, String>();
		
        if(BBBUtility.isEmpty(vendorDetails.getClientId()) || BBBUtility.isEmpty(vendorDetails.getApiKey())) {
			logError("Either Client Id or API key is null");
		}

		headerParam.put(BBBCoreConstants.X_ClientId, vendorDetails.getClientId());
		headerParam.put(BBBCoreConstants.X_ApiKey, vendorDetails.getApiKey());
		
		
		HashMap<String, String> serviceParameters = new HashMap<String, String>();
		
		String view = BBBCoreConstants.SUMMARY_VIEW;
		String httpType = BBBCoreConstants.GET;
		
			
		summaryResponse = getHttpCallInvoker().invokeToGetJson(EximSummaryResponseVO.class, headerParam, constructURL(view,vendorDetails.getApiURL(), refNum), serviceParameters, httpType);
		
		logDebug("Summary API Response:"+summaryResponse.toString());
		
		BBBPerformanceMonitor.end("BBBEximManager", "invokeSummaryAPI");
		
		return summaryResponse;
		
	}

	/**
	 * This method gives the vendor Name based on product Id
	 * @param productId
	 * @param vendorId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private VendorInfoVO getVendorInfo(String productId, String vendorName)
			throws BBBSystemException, BBBBusinessException {
		String siteId = SiteContextManager.getCurrentSite().getId();
		
		VendorInfoVO vendorInfoVO = new VendorInfoVO();
		if(productId !=null){		
			vendorInfoVO = getCatalogTools().getVendorInfo(
				getCatalogTools().getProductVOMetaDetails(siteId, productId).getVendorId());			
		}else{			
			try {
				
				QueryBuilder builder = getVendorRepository().getView(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR).getQueryBuilder();
				QueryExpression name = builder.createPropertyQueryExpression(BBBCatalogConstants.VENDORS_NAME_PROPERTY);
				QueryExpression value = builder.createConstantQueryExpression(vendorName);
				
				Query query = builder.createComparisonQuery(name, value, QueryBuilder.EQUALS);
				
				RepositoryItem[] item = getVendorRepository().getView(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR).executeQuery(query);
				
				if(item!=null){
					RepositoryItem repositoryItem = item[0];
					vendorInfoVO.setApiKey((String) repositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_API_KEY));
	                vendorInfoVO.setApiURL((String) repositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_API_URL));
	                if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
                    	vendorInfoVO.setClientId((String)repositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_BBB_CLIENT_ID));
                    } else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
                    	vendorInfoVO.setClientId((String)repositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_BAB_CLIENT_ID));
                    } else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
                    	vendorInfoVO.setClientId((String)repositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_CAN_CLIENT_ID));
                    } else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_US))  {
                    	vendorInfoVO.setClientId((String)repositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_BBB_CLIENT_ID));
                    } else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) {
                    	vendorInfoVO.setClientId((String)repositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_CAN_CLIENT_ID));
                    } else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BBB)) {
                    	vendorInfoVO.setClientId((String)repositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_BAB_CLIENT_ID));
                    }
	                vendorInfoVO.setApiVersion((String) repositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_API_VERSION));
				}else{
					logError("There is no items related to the vendor" + vendorName);
				}
			} catch (RepositoryException e) {
				logError("error fetching data for vendor by its name");
			}		
			
		}
		
		return vendorInfoVO;
	}

	/**
	 * Construct request url for the Katori summary API to get the response.
	 *
	 * @param view the view  :- Lock or summary
	 * @param referenceNumber the reference number
	 * @return the string
	 * @throws BBBBusinessException the bBB business exception
	 * @throws BBBSystemException the bBB system exception
	 */
	private String constructURL(String view,String vendorAPIUrl, String... referenceNumber) throws BBBBusinessException, BBBSystemException {
		logDebug("BBBEximPricingManager.constructURL starts");
		String completeURL = null;
		String referenceNum = null; 
		String refNumParam = "refnum[]=";
		int count = 0;
		try {
			if(StringUtils.isEmpty(vendorAPIUrl))
			{
				throw new BBBBusinessException(BBBCoreConstants.EXIM_API_URL,"Vendor API URL cannot be null");
			}
	    if(view.equalsIgnoreCase(BBBCoreConstants.LOCK_VIEW)) {
		   vendorAPIUrl+= BBBCoreConstants.SLASH;
		   completeURL = vendorAPIUrl + referenceNumber[0] + view;
	     } else if (view.equalsIgnoreCase(BBBCoreConstants.SUMMARY_VIEW)) {
		   vendorAPIUrl+= BBBCoreConstants.QUESTION_MARK;
		   String encodedRefnum = refNumParam.replace(BBBCoreConstants.SQUARE_BRACKET, URLEncoder.encode(BBBCoreConstants.SQUARE_BRACKET, BBBCoreConstants.UTF_ENCODING));
		
		   for(String refNum : referenceNumber) {
			  if(count == 0) {
			      referenceNum = encodedRefnum + refNum; 
			  } else {
				  referenceNum += BBBCoreConstants.AMPERSAND + encodedRefnum +refNum;
			  }
			  count++;
		   	  }
		completeURL = vendorAPIUrl + referenceNum;
		logDebug("hostTargetURL = " + vendorAPIUrl + " and completeURL = " + completeURL);
	    } 
		}catch (UnsupportedEncodingException e) {
		    logError(LogMessageFormatter.formatMessage(null, "UnsupportedEncodingException while encoding square bracket:"), e);
		    throw new BBBBusinessException("UnsupportedEncodingException while encoding square bracket", e);
	
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException occurs in BBBEximPricingManager.constructURL "
					+ ":: hostTargetURL = " + vendorAPIUrl + " and completeURL = " + completeURL), e);
			throw e;
		
	    } catch (Exception e) {
	    	String errorMsg=LogMessageFormatter.formatMessage(null, "BBBSystemException occurs in BBBEximPricingManager.constructURL "
					+ ":: hostTargetURL = " + vendorAPIUrl + " and completeURL = " + completeURL);
	    	logError(errorMsg, e);
		  throw new BBBSystemException(errorMsg);
	    }
	    logDebug("BBBEximPricingManager.constructURL ends");
	    
	   return completeURL;
		
	}
	
	/**
	 * 
	 * Used when saving personalized item in session.
	 * 
	 * @param refNumParam
	 * @param skuId
	 * @param siteId
	 * @param inCartFlag - introduced to handle incart pricing on tbs
	 * @return
	 */
	public KatoriPriceRestVO getPriceByRef(String refNumParam, String skuId, String siteId, boolean inCartFlag, String productId) {
		EximSummaryResponseVO eximResponse = null;
		KatoriPriceRestVO katoriPriceRestVO = new KatoriPriceRestVO();
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
				
		try {
			eximResponse = invokeSummaryAPI(productId, null, refNumParam);
		} catch(BBBBusinessException bbe) {
			katoriPriceRestVO.setErrorExist(true);
			katoriPriceRestVO.setErrorMsg(getLblTxtTemplateManager().getErrMsg("err_exim_invoke_business", request.getLocale().getLanguage(), null, siteId));
			logError("Error retreiving response from exim service call", bbe);
		} catch (BBBSystemException bse) {
			katoriPriceRestVO.setErrorExist(true);
			katoriPriceRestVO.setErrorMsg(getLblTxtTemplateManager().getErrMsg("err_exim_invoke_business", request.getLocale().getLanguage(), null, siteId));
			logError("Error retreiving response from exim service call", bse);
		}
		if(eximResponse!=null && !eximResponse.getCustomizations().isEmpty()){
			katoriPriceRestVO = this.getPriceByRefKatori(refNumParam, skuId, siteId, eximResponse.getCustomizations().get(0), null, true, inCartFlag, productId);
		}
		return katoriPriceRestVO;
	}
	
	/**
	 * 
	 * Used when saving personalized item in session.
	 * 
	 * @param refNumParam
	 * @param skuId
	 * @param siteId
	 * @param save
	 * @return
	 */
	public KatoriPriceRestVO getPriceByRef(String productId, String refNumParam, String skuId, String siteId,boolean save) {
		EximSummaryResponseVO eximResponse = null;
		KatoriPriceRestVO katoriPriceRestVO = new KatoriPriceRestVO();
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		try {
			eximResponse = invokeSummaryAPI(productId,null, refNumParam);
		} catch(BBBBusinessException bbe) {
			katoriPriceRestVO.setErrorExist(true);
			katoriPriceRestVO.setErrorMsg(getLblTxtTemplateManager().getErrMsg("err_exim_invoke_business", request.getLocale().getLanguage(), null, siteId));
			logError("Error retreiving response from exim service call", bbe);
		} catch (BBBSystemException bse) {
			katoriPriceRestVO.setErrorExist(true);
			katoriPriceRestVO.setErrorMsg(getLblTxtTemplateManager().getErrMsg("err_exim_invoke_business", request.getLocale().getLanguage(), null, siteId));
			logError("Error retreiving response from exim service call", bse);
		}
		if(eximResponse!=null && !eximResponse.getCustomizations().isEmpty()){
			katoriPriceRestVO = this.getPriceByRefKatori(refNumParam, skuId, siteId, eximResponse.getCustomizations().get(0), null, save, false, null);
		}
		return katoriPriceRestVO;
	}
	
	/**
	 * 
	 * Used when saving personalized item in session from rest call.
	 * 
	 * @param refNumParam
	 * @param skuId
	 * @param siteId
	 * @param productId
	 * @param altImage
	 * @param quantity
	 * @return
	 */
	public KatoriPriceRestVO getPriceByRefForRestCall(String refNumParam, String skuId, String siteId, String productId, String altImage, String quantity) {
		EximSummaryResponseVO eximResponse = null;
		KatoriPriceRestVO katoriPriceRestVO = new KatoriPriceRestVO();
		try {
			eximResponse = invokeSummaryAPI(productId,null, refNumParam);
		} catch(BBBBusinessException bbe) {
			katoriPriceRestVO.setErrorExist(true);
			logError("Error occured "+ bbe.getMessage());
			logDebug("Error Stack Trace:"+bbe);
			katoriPriceRestVO.setErrorMsg("Something went wrong. Please refresh page");
		} catch (BBBSystemException bse) {
			katoriPriceRestVO.setErrorExist(true);
			logError("Error occured "+ bse.getMessage());
			logDebug("Error Stack Trace:"+bse);
			katoriPriceRestVO.setErrorMsg("Something went wrong. Please refresh page");
		}
		if(eximResponse!=null && !eximResponse.getCustomizations().isEmpty()){
			Map<String, String> restParameters = new HashMap<String, String>();
			restParameters.put(BBBCoreConstants.SKUID, skuId);
			restParameters.put(BBBCoreConstants.SITE_ID, siteId);
			restParameters.put(BBBCoreConstants.PRODUCTID, productId);
			restParameters.put("altImage", altImage);
			restParameters.put(BBBCoreConstants.QUANTITY, quantity);
			restParameters.put(BBBCoreConstants.REFERENCE_NUMBER_PARAM, refNumParam);
			katoriPriceRestVO = this.getPriceByRefKatori(refNumParam, skuId, siteId, eximResponse.getCustomizations().get(0), restParameters, true, false, null);
		}
		return katoriPriceRestVO;
	}
	
	/**
	 * 
	 * Used for calculating price in double format.
	 * 
	 * @param skuId
	 * @param siteId
	 * @param retailAdder
	 * @param inCartFlag - introduced to handle incart pricing on tbs
	 * @param productId
	 * @return
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public Map<String, Object> calculatePrice(String skuId, String siteId, double retailAdder, boolean inCartFlag, String productId) throws BBBBusinessException, BBBSystemException{
		double finalPrice = 0.00;
		double eximPersonlizedPrice = 0.00;
		String personalizationType = getPersonalizationType(siteId, skuId);
		if (BBBCoreConstants.PERSONALIZATION_CODE_CR.equalsIgnoreCase(personalizationType)) {
			eximPersonlizedPrice = retailAdder;
			finalPrice = eximPersonlizedPrice;
		} else if (BBBCoreConstants.PERSONALIZATION_CODE_PY.equalsIgnoreCase(personalizationType)) {
			// if code is PY , then add the price coming from exim response
			eximPersonlizedPrice = retailAdder;
			Double basePrice;
			basePrice = getBasePriceBySku(skuId, inCartFlag, siteId, productId);
			finalPrice = eximPersonlizedPrice + basePrice;
		} else {
			// if personalization type is PB , then don't use exim personalized price
			finalPrice = getBasePriceBySku(skuId, inCartFlag, siteId, productId);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("itemPrice", finalPrice);
		result.put("personalizedPrice", eximPersonlizedPrice);
		Properties prop = new Properties();
		prop.setProperty("formatPrice", "false");
		prop.setProperty("isFormattedWithoutCurrency", "false");
		String finPrice = "0";
		String eximPersPrice = "0";
		try {
			finPrice = TagConverterManager.getTagConverterByName("currency").convertObjectToString(ServletUtil.getCurrentRequest(),BBBUtility.round(finalPrice), prop).toString();
			eximPersPrice = TagConverterManager.getTagConverterByName("currency").convertObjectToString(ServletUtil.getCurrentRequest(),BBBUtility.round(eximPersonlizedPrice), prop).toString();
			if(!BBBUtility.isEmpty(finPrice) && !BBBUtility.isEmpty(eximPersPrice)){
				finalPrice = (Double.valueOf(finPrice)).doubleValue();
				eximPersonlizedPrice = (Double.valueOf(eximPersPrice)).doubleValue();
			}
		} catch (TagConversionException e) {
			logError("Unable to convert prices for countries other than default.",e);
		}
		result.put(BBBEximConstants.EXIM_ITEM_PRICE, finalPrice);
		result.put(BBBEximConstants.EXIM_PERSONALISED_PRICE, eximPersonlizedPrice);
		return result;
	}
	
	
	/**
	 * 
	 * Used for calculating the personalized prices in double value.
	 * 
	 * @param skuId
	 * @param siteId
	 * @param retailAdder
	 * @param inCartFlag - introduced to handle incart pricing on tbs
	 * @param productId
	 * @return
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public Map<String, Object> calculateFormattedPrice(String skuId, String siteId, double retailAdder, boolean inCartFlag, String productId) throws BBBBusinessException, BBBSystemException{
		Map<String, Object> doublePrice = this.calculatePrice(skuId, siteId, retailAdder, inCartFlag, productId);
		if(doublePrice!=null && !doublePrice.isEmpty()){
			/*NumberFormat format = getCustomTagComponent().getCurrencyFormatter(2, (Double) doublePrice.get(BBBEximConstants.EXIM_ITEM_PRICE));			
			String itemPrice = format.format((Double) doublePrice.get(BBBEximConstants.EXIM_ITEM_PRICE));
			doublePrice.put(BBBEximConstants.FORMATTED_EXIM_ITEM_PRICE, itemPrice);
			format = getCustomTagComponent().getCurrencyFormatter(2, (Double) doublePrice.get(BBBEximConstants.EXIM_PERSONALISED_PRICE));
			String eximPersonalizedPrice = format.format((Double) doublePrice.get(BBBEximConstants.EXIM_PERSONALISED_PRICE));
			doublePrice.put(BBBEximConstants.FORMATTED_EXIM_PERSONALIZED_PRICE, eximPersonalizedPrice);*/
			Properties prop = new Properties();
			prop.setProperty("isFormattedWithoutCurrency", "true");
			try {
				String finPrice = TagConverterManager.getTagConverterByName("currency").convertObjectToString(ServletUtil.getCurrentRequest(),(Double) doublePrice.get("itemPrice"), prop).toString();
				String eximPersPrice = TagConverterManager.getTagConverterByName("currency").convertObjectToString(ServletUtil.getCurrentRequest(),(Double) doublePrice.get("personalizedPrice"), prop).toString();
				if(!BBBUtility.isEmpty(finPrice) && !BBBUtility.isEmpty(eximPersPrice)){
					doublePrice.put(BBBEximConstants.FORMATTED_EXIM_ITEM_PRICE, finPrice);
					doublePrice.put(BBBEximConstants.FORMATTED_EXIM_PERSONALIZED_PRICE, eximPersPrice);
				}
			} catch (TagConversionException e) {
				logError("Unable to convert prices for countries other than default.",e);
			}
		    return doublePrice;
		}
		return null;
	}
	
	/**
	 * 
	 *  Extracts the price information from 
	 * 
	 * @param refNumParam
	 * @param skuId
	 * @param siteId
	 * @param eximSummary
	 * @param restParameters
	 * @param save
	 * @param inCartFlag - introduced to handle incart pricing on tbs
	 * @param productId
	 * @return
	 */
	public KatoriPriceRestVO getPriceByRefKatori(String refNumParam, String skuId, String siteId, EximCustomizedAttributesVO eximSummary, Map<String, String> restParameters, boolean save, boolean inCartFlag, String productId) {
		logDebug("ref num :: " + refNumParam + " skuId:"+skuId + " siteId:"+siteId);
		if(BBBUtility.isEmpty(siteId)) {
			siteId = SiteContextManager.getCurrentSiteId();
		}
		double finalPrice = 0.00;
		double eximPersonlizedPrice = 0.00;
		String formattedEximItemPrice = "0";
		String formattedEximPersonalizedPrice = "0";
		KatoriPriceRestVO katoriPriceRestVO = new KatoriPriceRestVO();
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		BBBSessionBean sessionBean = getSessionBean(request);
		Map<String, Object> formattedPrice = null;
		try{
			if(save){
				EximSessionBean eximSessionBean = new EximSessionBean();
				formEximSessionBean(request, eximSessionBean , eximSummary, restParameters);
				saveEximCustomizedVOInSession(eximSessionBean, sessionBean);
			}
			formattedPrice = calculateFormattedPrice(skuId, siteId, eximSummary.getRetailPriceAdder(), inCartFlag, productId);
		} catch(BBBBusinessException bbe) {
			katoriPriceRestVO.setErrorExist(true);
			katoriPriceRestVO.setErrorMsg(getLblTxtTemplateManager().getErrMsg("err_exim_invoke_business", request.getLocale().getLanguage(), null, siteId));
			logError("Error retreiving response from exim service call", bbe);
		} catch (BBBSystemException bse) {
			katoriPriceRestVO.setErrorExist(true);
			katoriPriceRestVO.setErrorMsg(getLblTxtTemplateManager().getErrMsg("err_exim_invoke_business", request.getLocale().getLanguage(), null, siteId));
			logError("Error retreiving response from exim service call", bse);
		}
		
		if(!katoriPriceRestVO.isErrorExist() && formattedPrice!=null && !formattedPrice.isEmpty()){
			finalPrice = ((Double)formattedPrice.get(BBBEximConstants.EXIM_ITEM_PRICE)).doubleValue();
			eximPersonlizedPrice = ((Double)formattedPrice.get(BBBEximConstants.EXIM_PERSONALISED_PRICE)).doubleValue();
			formattedEximItemPrice = ((String)formattedPrice.get(BBBEximConstants.FORMATTED_EXIM_ITEM_PRICE));
			formattedEximPersonalizedPrice = ((String)formattedPrice.get(BBBEximConstants.FORMATTED_EXIM_PERSONALIZED_PRICE));
		}
		katoriPriceRestVO.setKatoriItemPrice(formattedEximItemPrice);
		katoriPriceRestVO.setKatoriPersonlizedPrice(formattedEximPersonalizedPrice);
		katoriPriceRestVO.setDoubleKatoriItemPrice(BBBUtility.round(finalPrice));
		katoriPriceRestVO.setDoubleKatoriPersonalizedPrice(eximPersonlizedPrice);
		String country = (String) sessionBean.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
		if(BBBUtility.isEmpty(country) || country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY) || siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
			katoriPriceRestVO.setCurrencySymbol(BBBCoreConstants.DOLLAR);
		}else{
			katoriPriceRestVO.setCurrencySymbol((String) sessionBean.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY));
		}
		setShipMessageFlag(katoriPriceRestVO, skuId, siteId, formattedEximItemPrice);
		return katoriPriceRestVO;
	}
	
	/**
	 * Sets the ship message flag and display custom ship message.
	 *
	 * @param pRequest the request
	 * @param skuId the sku id
	 * @param siteId the site id
	 * @param personlizedPrice the personlized price
	 */
	private void setShipMessageFlag(KatoriPriceRestVO katoriPriceRestVO, String skuId, String siteId, String itemPrice) {
		String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
		String higherShippingThreshhold =null;
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		try {
			shipMsgDisplayFlag = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
			if(Boolean.parseBoolean(shipMsgDisplayFlag) && !getCatalogTools().isSkuLtl(siteId, skuId) && 
					!((BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBGiftRegistryConstants.SESSION_BEAN)).isInternationalShippingContext()){
				double higherShipThreshhold = 0.00;
				higherShippingThreshhold =  getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, request.getLocale().getLanguage(), 
						null,SiteContextManager.getCurrentSiteId());
				if(!StringUtils.isBlank(higherShippingThreshhold)){
					String trimedHigherShippingThreshold = higherShippingThreshhold.replaceAll("[^0-9^.]", BBBCoreConstants.BLANK).trim();
					if(!trimedHigherShippingThreshold.equalsIgnoreCase(BBBCoreConstants.BLANK)){		
						higherShipThreshhold = Double.parseDouble(higherShippingThreshhold);
					}else{
						return;
					} 
				} else{
					return;
				}
				Map<String, String> placeholderMap = new HashMap<String, String>();
				if(!StringUtils.isBlank(itemPrice) && Double.valueOf(itemPrice) > higherShipThreshhold){
					placeholderMap.put(BBBCoreConstants.CURRENCY, BBBCoreConstants.DOLLAR);
					placeholderMap.put(BBBCoreConstants.HIGHER_SHIP_THRESHHOLD, higherShippingThreshhold);
					katoriPriceRestVO.setShipMsgFlag(true);
					katoriPriceRestVO.setDisplayShipMsg(((BBBConfigToolsImpl) getCatalogTools()).getLblTxtTemplateManager()
							.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, placeholderMap));
				}
			}
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error while getting config key ShipMsgDisplayFlag value", e);
		}
	}
	
	private void formEximSessionBean(DynamoHttpServletRequest request, EximSessionBean eximSessionBean, EximCustomizedAttributesVO eximResponse, Map<String, String> restParameters){
		if(restParameters==null || restParameters.isEmpty()){
			String refNum = request.getParameter(BBBCoreConstants.REFERENCE_NUMBER_PARAM);
			String skuId = request.getParameter(BBBCoreConstants.SKUID);
			String siteId =SiteContextManager.getCurrentSiteId();
			String productId = request.getParameter("productId");
			String altImage = request.getParameter("altImage");
			String quantity = request.getParameter("quantity");
			logDebug("ref num :: " + refNum + " skuId:"+skuId + " siteId:"+siteId);
			eximSessionBean.setSiteId(siteId);
			eximSessionBean.setSkuId(skuId);
			eximSessionBean.setEximResponse(eximResponse);
			if(eximResponse!=null && (eximResponse.getCustomizationStatus().equalsIgnoreCase("saved-complete") || 
					eximResponse.getCustomizationStatus().equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_STATUS_COMPLETE)
					))
				eximSessionBean.setPersonalizationComplete(true);
			eximSessionBean.setProductId(productId);
			eximSessionBean.setAltImages(altImage.equalsIgnoreCase(BBBCoreConstants.TRUE));
			long qty = 0;
			try{
				qty = Long.valueOf(quantity).longValue();
			}catch(NumberFormatException e){
				logError("",e);
			}
			eximSessionBean.setQuantity(qty);
			eximSessionBean.setRefnum(refNum);
		}else{
			eximSessionBean.setSiteId(restParameters.get(BBBCoreConstants.SITE_ID));
			eximSessionBean.setSkuId(restParameters.get(BBBCoreConstants.SKUID));
			eximSessionBean.setEximResponse(eximResponse);
			if(eximResponse!=null && (eximResponse.getCustomizationStatus().equalsIgnoreCase("saved-complete") || 
					eximResponse.getCustomizationStatus().equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_STATUS_COMPLETE)))
				eximSessionBean.setPersonalizationComplete(true);
			eximSessionBean.setProductId(restParameters.get(BBBCoreConstants.PRODUCTID));
			String altImage = restParameters.get("altImage");
			eximSessionBean.setAltImages(altImage.equalsIgnoreCase(BBBCoreConstants.TRUE));
			long qty = 0;
			try{
				qty = Long.valueOf(restParameters.get(BBBCoreConstants.QUANTITY)).longValue();
			}catch(NumberFormatException e){
				logError("",e);
			}
			eximSessionBean.setQuantity(qty);
			eximSessionBean.setRefnum(restParameters.get(BBBCoreConstants.REFERENCE_NUMBER_PARAM));
		}
		JSONObject katoriCodes = this.getCustomizationCodesJSON();
		String customizationCode = getCatalogTools().getCustomizationCodeFromSKU(eximSessionBean.getSkuId());
		if(katoriCodes!=null && customizationCode!=null && katoriCodes.has(eximSessionBean.getEximResponse().getCustomizationService())){
			JSONObject obj = (JSONObject) katoriCodes.get(customizationCode);
			eximSessionBean.setPersonalizedSingleCode(obj.getString(BBBEximConstants.SINGLE_CODE));
			eximSessionBean.setPersonalizedSingleCodeDescription(obj.getString(BBBEximConstants.DESCRIPTION));
		}
	}
	
	public BBBSessionBean getSessionBean(DynamoHttpServletRequest request){
		return  BBBProfileManager.resolveSessionBean(request);
		//return (BBBSessionBean)request.resolveName(BBBCoreConstants.SESSION_BEAN);
	}
	
	public void saveEximCustomizedVOInSession(EximSessionBean eximSessionBean, BBBSessionBean sessionBean) throws BBBBusinessException{
		if(eximSessionBean!=null && !BBBUtility.isEmpty(eximSessionBean.getRefnum()) && !BBBUtility.isEmpty(eximSessionBean.getSkuId())){
			Map<String, EximSessionBean> personalizedSkus = sessionBean.getPersonalizedSkus();
			if(eximSessionBean.getEximResponse()!=null){
				personalizedSkus.put(eximSessionBean.getSkuId(), eximSessionBean);
			}else{
				throw new BBBBusinessException("Error saving item in Session. Items :-" + eximSessionBean);				
			}
		}
	}
	

	/**
	 * Gets the personalization type.
	 *
	 * @param siteId the site id
	 * @param skuId the sku id
	 * @return the personalization type
	 * @throws BBBBusinessException the bBB business exception
	 * @throws BBBSystemException the bBB system exception
	 */
	private String getPersonalizationType(String siteId, String skuId)
			throws BBBBusinessException, BBBSystemException {
		logDebug("SkuId:"+skuId + " siteId:"+siteId);
		SKUDetailVO pSKUDetailVO;
		pSKUDetailVO = getCatalogTools().getSKUDetails(siteId, skuId, false, false);
		String personalizationType = pSKUDetailVO.getPersonalizationType();
		logDebug("PersonalizationType:"+personalizationType);
		return personalizationType;
	}

	/**
	 * Gets the base price of sku.
	 *
	 * @param skuId the sku id
	 * @param inCartFlag - introduced to handle incart pricing on tbs
	 * @param siteId 
	 * @param productId 
	 * @return the base price by sku
	 * @throws BBBSystemException the bBB system exception
	 */
	private Double getBasePriceBySku(String skuId, boolean inCartFlag, String siteId, String productId) throws BBBSystemException {
		logDebug("Sku ID: "+skuId);
		Double basePrice = 0.00;
		
		//BBBH-2889 - if flag true than base price is set to inCartPrice 
		if(inCartFlag && (siteId.contains("TBS"))){
			basePrice=getCatalogTools().getIncartPrice(productId, skuId).doubleValue();
		}
		else {
		double listPrice = getCatalogTools().getListPrice(null,skuId).doubleValue();
		double salePrice = getCatalogTools().getSalePrice(null,skuId).doubleValue();
		logDebug("SalePrice: "+salePrice + " ListPrice"+listPrice);
		if(salePrice > 0) {
			basePrice = salePrice;
		} else {
			basePrice = listPrice;
		}
		}
		return basePrice;
	}
	
	/**
	 * This method return the customization Codes fetching from schema.
	 *
	 * @return the customization codes
	 */
	public String getCustomizationCodes(){
		
		return this.getCustomizationCodesJSON().toString();
		
	}
	
	public JSONObject getCustomizationCodesJSON(){
		
		logDebug("EximCustomizationManager.getCustomizationCodes method starts");
		
		Object[] params = null;
		String rql= BBBEximConstants.ALL;
		params = new Object[]{};
		
		IRepositoryWrapper iRepositoryWrapper = new RepositoryWrapperImpl(getEximRepository());
		
		RepositoryItem[] items;
		
		items = iRepositoryWrapper.queryRepositoryItems(BBBEximConstants.EXIM_ITEM_DESCRIPTOR, rql, params,true);
		
		JSONObject jsonObject = new JSONObject();
		
		if(items!=null && items.length > 0){
			for(RepositoryItem item : items){
				String customizationCode = (String) item.getPropertyValue(BBBEximConstants.CUSTOMIZATION_CODES);
				String singleCode = (String) item.getPropertyValue(BBBEximConstants.SINGLE_CODE);
				String description = (String) item.getPropertyValue(BBBEximConstants.DESCRIPTION);
				JSONObject object = new JSONObject();
				
				object.put(BBBEximConstants.SINGLE_CODE, singleCode);
				object.put(BBBEximConstants.DESCRIPTION, description);			
				
				try {
					jsonObject.put(customizationCode, object);
				} catch (JSONException e) {
					logError("Json exception",e);
				}
			}
		}
		
		
		
		logDebug("JSON object is = " + jsonObject.toString() );
		
		return jsonObject;
		
		
	}
	
	/**
	 * Gets the exim map.
	 *
	 * @return the exim map
	 */
	public HashMap<String,String> getEximValueMap(){
		Object[] params = null;
		String rql= BBBEximConstants.ALL;
		params = new Object[]{};
		
		IRepositoryWrapper iRepositoryWrapper = new RepositoryWrapperImpl(getEximRepository());
		
		RepositoryItem[] items;
		
		items = iRepositoryWrapper.queryRepositoryItems(BBBEximConstants.EXIM_ITEM_DESCRIPTOR, rql, params,true);
		
		HashMap<String,String> eximMap = new HashMap<String,String>();
		
		if(items!=null && items.length > 0){
			for(RepositoryItem item : items){
				String customizationCode = (String) item.getPropertyValue(BBBEximConstants.CUSTOMIZATION_CODES);
				String description = (String) item.getPropertyValue(BBBEximConstants.DESCRIPTION);
				eximMap.put(customizationCode, description);
			}
			return eximMap;
		}
		return null;
		}
	
	// the below method is to retrieve singleCode for Customization codes
	
	public String getPersonalizedOptionsDisplayCode(String personalizationOptions){
		String personalizedOptionsDisplay = null;
		String personalizedOptionsDisplay_rql = "customizationCode = ?0";
		
		try {
			RepositoryItem[] items;
			RepositoryView view =  getEximRepository().getView(BBBEximConstants.EXIM_ITEM_DESCRIPTOR);

			RqlStatement statement = RqlStatement.parseRqlStatement(personalizedOptionsDisplay_rql);

			Object params[] = new Object[2];
			params[0] = personalizationOptions;

			items = statement.executeQuery(view, params);
			if(null!=items && items.length>0){
				if(null!=items[0].getPropertyValue(BBBEximConstants.SINGLE_CODE)){
					personalizedOptionsDisplay=(String)items[0].getPropertyValue(BBBEximConstants.SINGLE_CODE);
				}
			}
			
		} catch (RepositoryException e) {
			this.logDebug("BBBEximManager getPersonalizedOptionDisplayCode(): RepositoryException ");
		} 
	return personalizedOptionsDisplay;
	}
	/**
	 * This method is used to get response for multiple reference numbers in one call using Exim Multi-Ref Num Service. 
	 * Sets the response values in Commerce ITem. In case of an error, set the error flag for
	 * commerce item as true.
	 *
	 * @param commerceItems 
	 * @return eximMap
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean setEximDetailsbyMultiRefNumAPI(final List<CommerceItem> commerceItems, final BBBOrder pOrder) {
		
		logDebug("BBBEximManager.setEximDetailsbyMultiRefNumAPI -START - for commerce Item");
		Map<String, List<String>> vendorToRefNum = new HashMap<String, List<String>>();
		List<EximCustomizedAttributesVO> eximAtrributesList = null;
		boolean isItemExistInResponse = false;
		boolean isWebServiceFailure = false;
		boolean isAllCutomizationsEmpty = false;
		EximSummaryResponseVO eximResponse = null;		
		Map<String, List<CommerceItem>> customizedCommerceItemInfos = new HashMap<String, List<CommerceItem>>();
		
		/**
		 * creating Map of vendor name and list of all the reference numbers associated with the respective vendor.
		 */
		String siteId = SiteContextManager.getCurrentSite().getId();
		List<CommerceItem> customizedCommerceItem = new ArrayList<CommerceItem>();
		for (final CommerceItem commItem : commerceItems) {			
			if(commItem instanceof BBBCommerceItem){
				String refNum = ((BBBCommerceItem)commItem).getReferenceNumber();
				String productId = commItem.getAuxiliaryData().getProductId();				
				String vendorName = getVendorName(productId, siteId);				
				createVendorNameMap(vendorToRefNum,
						customizedCommerceItemInfos, commItem, refNum,
						vendorName);
				//BBBH-6482: commerceItem added to customizedCommerceItem in case refNum exists to populate personalizationOptionsDisplay
				if(!BBBUtility.isEmpty(refNum)){
					customizedCommerceItem.add(commItem);
				}
			}
		}	
		
		if(vendorToRefNum.entrySet() == null){
			pOrder.setEximWebserviceFailure(false);
		}else{
			

			/**
			 * Iterating Map by vendor name and fetching the response by multi reference number API.
			 */
			
			for(Map.Entry<String, List<String>> mp : vendorToRefNum.entrySet() ){
			
				try {
					// Call the EXIm web service
					eximResponse = invokeSummaryAPI(null,mp.getKey(), mp.getValue().toArray(new String[mp.getValue().size()]));
				} catch (BBBBusinessException e) {
					logError("Business Exception occured while calling EXIM webservice" + e);
					isWebServiceFailure = true;
				} catch (BBBSystemException e) {
					logError("System Exception occured while calling EXIM webservice" + e);
					isWebServiceFailure = true;
				} 
				
				if(null != eximResponse) {
					eximAtrributesList = eximResponse.getCustomizations();
					logDebug("Response is not null or empty from Exim Multi Ref Service");
					
					if(null != eximAtrributesList && !eximAtrributesList.isEmpty()){
						for (final CommerceItem commItem : customizedCommerceItem) {
							for( final EximCustomizedAttributesVO eximAtrributes: eximAtrributesList) {
								String refNum = ((BBBCommerceItem)commItem).getReferenceNumber();
								
								//If the reference number exist in response, then do the processing else set the error as true
								if(null != eximAtrributes.getRefnum() && eximAtrributes.getRefnum().equalsIgnoreCase(refNum)){
									if (null!= eximAtrributes.getErrors() && !eximAtrributes.getErrors().isEmpty()) {
										logDebug("EError exists for ref Num : " + refNum);
										((BBBCommerceItem)commItem).setEximErrorExists(true);
									} else{
										logDebug("Set the refNum Details in commerce item " + refNum);
										setEximPersonalizeDetailsInCI((BBBCommerceItem)commItem, eximAtrributes);
										((BBBCommerceItem)commItem).setEximErrorExists(false);
									}
									isItemExistInResponse = true;
									break;
								}
							}
							if(!isItemExistInResponse){
								logDebug("BBBEximMamanger.getEximDetailsbyMultiRefNum Error exists for ref Num : " + ((BBBCommerceItem)commItem).getReferenceNumber());
								((BBBCommerceItem)commItem).setEximErrorExists(true);
							}
							((BBBCommerceItem)commItem).setEximPricingReq(false);
						}
					}else{
						isAllCutomizationsEmpty = true;
					}
					//Set the order level EXIM web service failure flag as false
					pOrder.setEximWebserviceFailure(false);
				}else{
					isWebServiceFailure = true;
				}
				
				//In case of web service failure or when all customizations are empty,
				//set the error flag as true and EXImM pricing required as false for all commerce item
				if(isWebServiceFailure || isAllCutomizationsEmpty){
					for (final CommerceItem commItem : customizedCommerceItem) {
						logDebug("BBBEximMamanger.getEximDetailsbyMultiRefNum Error exists for ref Num : " + ((BBBCommerceItem)commItem).getReferenceNumber());
						((BBBCommerceItem)commItem).setEximErrorExists(true);
						((BBBCommerceItem)commItem).setEximPricingReq(false);
					}
				}
				if(isWebServiceFailure){
					//Set the order level EXIM web service failure flag as true
					pOrder.setEximWebserviceFailure(true);
				}
			
			}
		}
		logDebug("BBBEximMamanger.setEximDetailsbyMultiRefNumAPI ENDS");
		return isWebServiceFailure;
	}
	
	/**
	 * This method is used to get response for multiple reference numbers in one call using Exim Multi-Ref Num Service. 
	 * Sets the response values in Commerce ITem. In case of an error, set the error flag for
	 * commerce item as true.
	 *
	 * @param commerceItems 
	 * @return eximMap
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean setEximDetailsLoadShoppingCart(final List<CommerceItem> commerceItems, final BBBOrder pOrder) {
		
		logDebug("BBBEximManager.setEximDetailsLoadShoppingCart -START - for commerce Item");
		Map<String, List<String>> vendorToRefNum = new HashMap<String, List<String>>();
		List<EximCustomizedAttributesVO> eximAtrributesList = null;
		boolean isItemExistInResponse = false;
		boolean repricingRequired = false;
		boolean isAllCutomizationsEmpty = false;
		boolean webSericefailure = false;
		EximSummaryResponseVO eximResponse = null;		
		Map<String, List<CommerceItem>> customizedCommerceItemInfos = new HashMap<String, List<CommerceItem>>();
		String siteId = SiteContextManager.getCurrentSite().getId();
		/**
		 * creating Map of vendor name and list of all the reference numbers associated with the respective vendor.
		 */
		List<CommerceItem> customizableCommerceItem = new ArrayList<CommerceItem>();
		for (final CommerceItem commItem : commerceItems) {			
			if(commItem instanceof BBBCommerceItem){
				String refNum = ((BBBCommerceItem)commItem).getReferenceNumber();
				String productId = commItem.getAuxiliaryData().getProductId();
				String skuId = commItem.getCatalogRefId();
				RepositoryItem skuRepositoryItem = null;
				try {
					skuRepositoryItem = this.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				}catch (final RepositoryException e) {
		            this.logError("Catalog API Method Name [getSKUDetails]: RepositoryException for sku Id " + skuId);
		            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
		        } finally {
		        	this.logDebug("Catalog API Method Name [getSKUDetails] siteId[" + siteId + "] pSkuId[" + skuId + "] Exit");
		            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetails");
		        }
				boolean customizationRequired = this.getCatalogTools().isCustomizationRequiredForSKU(skuRepositoryItem, siteId);
				if(customizationRequired && !BBBUtility.isEmpty(refNum)){
					String vendorName = getVendorName(productId, siteId);				
					createVendorNameMap(vendorToRefNum,customizedCommerceItemInfos, commItem, refNum,vendorName);
					//BBBH-6482: commerceItem added to customizedCommerceItem in case refNum exists to populate personalizationOptionsDisplay
					customizableCommerceItem.add(commItem);
				}else if(customizationRequired && BBBUtility.isEmpty(refNum)){
					((BBBCommerceItem)commItem).setEximErrorExists(true);
					repricingRequired = true;
				}
			}
		}	
		
		if(vendorToRefNum.entrySet() == null){
			pOrder.setEximWebserviceFailure(false);
		}else{
			
			if(!BBBUtility.isListEmpty(customizableCommerceItem)){
			
			/**
			 * Iterating Map by vendor name and fetching the response by multi reference number API.
			 */
			
				for(Map.Entry<String, List<String>> mp : vendorToRefNum.entrySet() ){
				
					try {
						// Call the EXIm web service
						eximResponse = invokeSummaryAPI(null,mp.getKey(), mp.getValue().toArray(new String[mp.getValue().size()]));
					} catch (BBBBusinessException e) {
						logError("Business Exception occured while calling EXIM webservice" + e);
						webSericefailure=true;
					} catch (BBBSystemException e) {
						logError("System Exception occured while calling EXIM webservice" + e);
						webSericefailure=true;
					} 
					
					if(null != eximResponse) {
						eximAtrributesList = eximResponse.getCustomizations();
						logDebug("Response is not null or empty from Exim Multi Ref Service");
						
						if(null != eximAtrributesList && !eximAtrributesList.isEmpty()){
							for (final CommerceItem commItem : customizableCommerceItem) {
								for( final EximCustomizedAttributesVO eximAtrributes: eximAtrributesList) {
									String refNum = ((BBBCommerceItem)commItem).getReferenceNumber();
									
									//If the reference number exist in response, then do the processing else set the error as true
									if(null != eximAtrributes.getRefnum() && eximAtrributes.getRefnum().equalsIgnoreCase(refNum)){
										if (null!= eximAtrributes.getErrors() && !eximAtrributes.getErrors().isEmpty()) {
											logDebug("EError exists for ref Num : " + refNum);
											((BBBCommerceItem)commItem).setEximErrorExists(true);
										} else{
											logDebug("Set the refNum Details in commerce item " + refNum);
											setEximPersonalizeDetailsInCI((BBBCommerceItem)commItem, eximAtrributes);
											((BBBCommerceItem)commItem).setEximErrorExists(false);
										}
										isItemExistInResponse = true;
										break;
									}
								}
								if(!isItemExistInResponse){
									logDebug("BBBEximMamanger.getEximDetailsbyMultiRefNum Error exists for ref Num : " + ((BBBCommerceItem)commItem).getReferenceNumber());
									((BBBCommerceItem)commItem).setEximErrorExists(true);
								}
								((BBBCommerceItem)commItem).setEximPricingReq(false);
							}
						}else{
							isAllCutomizationsEmpty = true;
						}
						//Set the order level EXIM web service failure flag as false
						pOrder.setEximWebserviceFailure(false);
						repricingRequired=true;
					}
					
					//In case of web service failure or when all customizations are empty,
					//set the error flag as true and EXImM pricing required as false for all commerce item
					if(webSericefailure || isAllCutomizationsEmpty){
						for (final CommerceItem commItem : customizableCommerceItem) {
							logDebug("BBBEximMamanger.getEximDetailsbyMultiRefNum Error exists for ref Num : " + ((BBBCommerceItem)commItem).getReferenceNumber());
							((BBBCommerceItem)commItem).setEximErrorExists(true);
							((BBBCommerceItem)commItem).setEximPricingReq(false);
						}
						pOrder.setEximWebserviceFailure(true);
						repricingRequired=true;
					}
				
				}
			}
		}
		logDebug("BBBEximMamanger.setEximDetailsLoadShoppingCart ENDS");
		return repricingRequired;
	}

	/**
	 * This methods creates 2 Maps
	 * 1. Create map for vendor name and reference numbers related to the vendor
	 * 2. Create map for vendor name and commerce items related to the vendor
	 * @param vendorToRefNum
	 * @param customizedCommerceItemInfos
	 * @param commItem
	 * @param refNum
	 * @param vendorName
	 */
	
	private void createVendorNameMap(Map<String, List<String>> vendorToRefNum,
			Map<String, List<CommerceItem>> customizedCommerceItemInfos,
			final CommerceItem commItem, String refNum, String vendorName) {
		if(!BBBUtility.isEmpty(vendorName) && !BBBUtility.isEmpty(refNum)){							
			if(vendorToRefNum.get(vendorName) == null){				
				List<String> refNumList = new ArrayList<String>();	
				List<CommerceItem> customizedCommerceItemInfo = new ArrayList<CommerceItem>();
				refNumList.add(refNum);
				vendorToRefNum.put(vendorName, refNumList);
				customizedCommerceItemInfo.add(commItem);
				customizedCommerceItemInfos.put(vendorName, customizedCommerceItemInfo);
			}else{					
				List<String> refNumList=vendorToRefNum.get(vendorName);						
				refNumList.add(refNum);
				List<CommerceItem> commerceItemList = customizedCommerceItemInfos.get(vendorName);
				commerceItemList.add(commItem);					
			}
		}
	}
	
	/**
	 * This method is used to get response for multiple reference numbers in one call using Exim Multi-Ref Num Service. 
	 * Creates a map of refNum and corresponding ResponseVo. In case of an error, set the error flag for
	 * commerce item info as true.
	 *
	 * @param itemInfos the item infos
	 * @return eximMap
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<String, EximCustomizedAttributesVO> getEximDetailsMapByMultiRefNum(final AddCommerceItemInfo[] itemInfos, final BBBOrder pOrder) {
		
		logDebug("BBBEximManager.getEximDetailsbyMultiRefNum START");
		Map<String, EximCustomizedAttributesVO> eximResponseMap = new HashMap<String, EximCustomizedAttributesVO>();		
		Map<String, List<String>> vendorToRefNum = new HashMap<String, List<String>>();
		EximSummaryResponseVO eximResponse = null;
		List<EximCustomizedAttributesVO> eximAtrributesList = null;		
		boolean isItemExistInResponse = false;
		boolean isWebServiceFailure = false;
		boolean isAllCutomizationsEmpty = false;
		String siteId = SiteContextManager.getCurrentSite().getId();

		Map<String, List<AddCommerceItemInfo>> customizedCommerceItemInfos = new HashMap<String, List<AddCommerceItemInfo>>();		

		/**
		 * creating Map of vendor name and list of all the reference numbers associated with the respective vendor.
		 */
		
		for (final AddCommerceItemInfo itemInfo : itemInfos) {
			String refNum = (String)itemInfo.getValue().get(BBBCoreConstants.REFERENCE_NUMBER);
			String productId= itemInfo.getProductId();			
			String vendorName = getVendorName(productId, siteId);			
			createVendorNameMap(vendorToRefNum, customizedCommerceItemInfos,
					itemInfo, refNum, vendorName);			
		}		

		/**
		 * Iterating Map by vendor name and fetching the response by multi reference number API.
		 */
		
		for(Map.Entry<String, List<String>> mp : vendorToRefNum.entrySet()){
			
				try {					
					eximResponse = invokeSummaryAPI(null,mp.getKey(), mp.getValue().toArray(new String[mp.getValue().size()]));		// invoking EXIM web service
				} catch (BBBBusinessException be) {
					logError("Business Exception occured while calling EXIM webservice" + be);
					isWebServiceFailure = true;
				} catch (BBBSystemException se) {
					logError("System Exception occured while calling EXIM webservice" + se);
					isWebServiceFailure = true;
				} 
				
				if(null != eximResponse) {
					eximAtrributesList = eximResponse.getCustomizations();															
					logDebug("Response is not null or empty from Exim Multi Ref Service");
					
					if(null != eximAtrributesList && !eximAtrributesList.isEmpty()){
						isItemExistInResponse = processingEximResponse(
								eximResponseMap, eximAtrributesList,
								isItemExistInResponse,
								customizedCommerceItemInfos, mp);
					}else{
						isAllCutomizationsEmpty = true;
					}					
					pOrder.setEximWebserviceFailure(false);		//Set the order level EXIM web service failure flag as false
				}else{
					isWebServiceFailure = true;
				}
				if(isWebServiceFailure || isAllCutomizationsEmpty){   	//In case of web service failure set the error flag as true for all commerce item
					for (final AddCommerceItemInfo customizedItemInfo : customizedCommerceItemInfos.get(mp.getKey())) {
						logDebug("BBBEximMamanger.getEximDetailsbyMultiRefNum Error exists for ref Num : " + customizedItemInfo.getValue().get(BBBCoreConstants.REFERENCE_NUMBER));
						customizedItemInfo.getValue().put(BBBCoreConstants.EXIM_ERROR_EXISTS, BBBCoreConstants.TRUE);
						customizedItemInfo.getValue().put(BBBCoreConstants.EXIM_PRICING_REQ, BBBCoreConstants.FALSE);
					}
				}
				if(isWebServiceFailure){					
					pOrder.setEximWebserviceFailure(true);		//Set the order level exim web service failure flag as true
				}
			
		}
		logDebug("BBBEximMamanger.getEximDetailsbyMultiRefNum ENDS");
		return eximResponseMap;
	}

	/**
	 * This methods processes the response from EXIM and puts the data in eximResponseMap
	 * 
	 * @param eximResponseMap
	 * @param eximAtrributesList
	 * @param isItemExistInResponse
	 * @param customizedCommerceItemInfos
	 * @param mp
	 * @return
	 */
	private boolean processingEximResponse(
			Map<String, EximCustomizedAttributesVO> eximResponseMap,
			List<EximCustomizedAttributesVO> eximAtrributesList,
			boolean isItemExistInResponse,
			Map<String, List<AddCommerceItemInfo>> customizedCommerceItemInfos,
			Map.Entry<String, List<String>> mp) {
		
		logDebug("BBBEximManager.processingEximResponse: Method Starts");
		
		for (final AddCommerceItemInfo customizedItemInfo : customizedCommerceItemInfos.get(mp.getKey())) {
			for( final EximCustomizedAttributesVO eximAtrributeVO: eximAtrributesList) {
				String refNum = (String)customizedItemInfo.getValue().get(BBBCoreConstants.REFERENCE_NUMBER);
				if(null != eximAtrributeVO.getRefnum() && eximAtrributeVO.getRefnum().equalsIgnoreCase(refNum)){
					
					//If the reference number exist in response, then do the processing else set the error as true
					if (null!= eximAtrributeVO.getErrors() && !eximAtrributeVO.getErrors().isEmpty()) {
						logDebug("Error exists for ref Num : " + refNum);
						customizedItemInfo.getValue().put(BBBCoreConstants.EXIM_ERROR_EXISTS, BBBCoreConstants.TRUE);
					} else{
						logDebug("Set the refNum Details in map : " + refNum);
						eximResponseMap.put(eximAtrributeVO.getRefnum(), eximAtrributeVO);
					}
					isItemExistInResponse = true;
					break;
				}
			}
			if(!isItemExistInResponse){
				logDebug("Error exists for ref Num : " + customizedItemInfo.getValue().get(BBBCoreConstants.REFERENCE_NUMBER));
				customizedItemInfo.getValue().put(BBBCoreConstants.EXIM_ERROR_EXISTS, BBBCoreConstants.TRUE);
			}
			customizedItemInfo.getValue().put(BBBCoreConstants.EXIM_PRICING_REQ, BBBCoreConstants.FALSE);
		}
		logDebug("Items Exists in Response is " + isItemExistInResponse);
		logDebug("BBBEximManager.processingEximResponse: Method Ends");
		return isItemExistInResponse;
	}
	
	/**
	 * This methods creates 2 Maps
	 * 1. Map for vendor name and list of reference numbers
	 * 2. Map for vendor name and commerce items specific to vendor. 
	 * @param vendorToRefNum
	 * @param customizedCommerceItemInfos
	 * @param itemInfo
	 * @param refNum
	 * @param vendorName
	 */

	private void createVendorNameMap(Map<String, List<String>> vendorToRefNum,
			Map<String, List<AddCommerceItemInfo>> customizedCommerceItemInfos,
			final AddCommerceItemInfo itemInfo, String refNum, String vendorName) {
		
		if(!BBBUtility.isEmpty(vendorName) && !BBBUtility.isEmpty(refNum)){							
			if(vendorToRefNum.get(vendorName) == null){				
				List<String> refNumList = new ArrayList<String>();	
				List<AddCommerceItemInfo> customizedCommerceItemInfo = new ArrayList<AddCommerceItemInfo>();
				refNumList.add(refNum);
				vendorToRefNum.put(vendorName, refNumList);
				customizedCommerceItemInfo.add(itemInfo);
				customizedCommerceItemInfos.put(vendorName, customizedCommerceItemInfo);
			}else{					
				List<String> refNumList=vendorToRefNum.get(vendorName);						
				refNumList.add(refNum);
				List<AddCommerceItemInfo> commerceItemList = customizedCommerceItemInfos.get(vendorName);
				commerceItemList.add(itemInfo);					
			}
		}
	}

	
	/**
	 * This methods fetches vendor Name based on product id
	 * @param productId
	 * @return
	 */
	private String getVendorName(String productId, String siteId) {
		
		String vendorName = null;
		try {
			vendorName = getCatalogTools().getVendorInfo( 																
					getCatalogTools().getProductVOMetaDetails(siteId, productId).getVendorId()).getVendorName();
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Exception while fetching vendor Name for the product" + e );
		}		
	
		return vendorName;
	}
	
	/**
	 * Sets the exim personalized details in Commerce Item.
	 *
	 * @param pItem the item
	 * @param eximAtrributes the exim atrributes
	 * @return the double
	 */
	public double setEximPersonalizeDetailsInCI(BBBCommerceItem pItem, EximCustomizedAttributesVO eximAtrributes) {
		
		logDebug("BBBEximMamanger.setEximPersonalizeDetailsInCI starts");
		double eximPersonlizedPrice = 0.0;
		String customizationCode = null;
		if (null != eximAtrributes) {
			logDebug("eximAtrributes error :: " + eximAtrributes.getErrors() + " and eximPersonlizedPrice :: " + eximAtrributes.getRetailPriceAdder());
			eximPersonlizedPrice = eximAtrributes.getRetailPriceAdder();
			pItem.setPersonalizePrice(eximAtrributes.getRetailPriceAdder());
			pItem.setPersonalizeCost(eximAtrributes.getCostPriceAdder());
			pItem.setPersonalizationDetails(eximAtrributes.getNamedrop());
			customizationCode = getCatalogTools().getCustomizationCodeFromSKU((RepositoryItem)pItem.getAuxiliaryData().getCatalogRef());
			if(customizationCode!=null) {
				pItem.setPersonalizationOptions(customizationCode);
			}
			//BPSI-5386 Personalization option single code display		
			pItem.setPersonalizationOptionsDisplay(getPersonalizedOptionsDisplayCode(pItem.getPersonalizationOptions()));
			pItem.setMetaDataFlag(eximAtrributes.getMetadataStatus());
			pItem.setMetaDataUrl(eximAtrributes.getMetadataUrl());
			pItem.setModerationFlag(eximAtrributes.getModerationStatus());
			pItem.setModerationUrl(eximAtrributes.getModerationUrl());
			List<EximImagesVO> images = eximAtrributes.getImages();
			for(EximImagesVO imageVO : images) {
				if(imageVO.getId().equalsIgnoreCase(BBBCoreConstants.IMAGE_ID) || imageVO.getId().equalsIgnoreCase("")) {
				List<EximImagePreviewVO> previews = imageVO.getPreviews();
				  for (EximImagePreviewVO preview : previews) { 
					   if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_LARGE)) 
					      pItem.setFullImagePath(preview.getUrl());
					   if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_X_SMALL)) {
						pItem.setThumbnailImagePath(preview.getUrl());
					    pItem.setMobileThumbnailImagePath(preview.getUrl());
					   }
					   if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_SMALL))
						pItem.setMobileFullImagePath(preview.getUrl());
				  }
				  break;
				}
			}
        }
		logDebug("BBBEximMamanger.setEximPersonalizeDetailsInCI ends");
        return eximPersonlizedPrice;
	}
	
	public String getKatoriAvailability() {
		String katoriFlag = null;
		try {
			katoriFlag = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.EXIM_KEYS).get(BBBCoreConstants.ENABLE_KATORI);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException occurs in BBBEximPricingManager.getKatoriAvailability "
					+ ":: katoriFlag = " + katoriFlag), e);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException occurs in BBBEximPricingManager.getKatoriAvailability "
					+ ":: katoriFlag = " + katoriFlag), e);
		}
		return katoriFlag;
	}
	
	/**
	 * This method sets the moderated image url in the commerce item
	 * @param bbbCommerceItem
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public void setModerateImageURL(BBBCommerceItem bbbCommerceItem) throws BBBSystemException, BBBBusinessException {
		
		logDebug("Method: setting full and thumbnail image for mobile and desktop in the commerce item");
		
		List<String> moderateKeyValueList = new ArrayList<String>();
		moderateKeyValueList = getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBCoreConstants.MODERATED);
		String moderateKeyValue = "";
		if(null != moderateKeyValueList && !(moderateKeyValueList.isEmpty())){
			moderateKeyValue = moderateKeyValueList.get(0);
		}
		logDebug("Moderate Key value = " + moderateKeyValue);
		
		String personalizeMobileImgURL = bbbCommerceItem.getMobileFullImagePath();
		String personalizeMobileThumbnailImage = bbbCommerceItem.getMobileThumbnailImagePath();
		
		if(personalizeMobileImgURL!=null && !personalizeMobileImgURL.contains(moderateKeyValue)){
			if(!(BBBUtility.isEmpty(personalizeMobileImgURL))){
		    	StringBuffer moderatedUrl = appendModerateUrl(moderateKeyValue,
						personalizeMobileImgURL);
	            bbbCommerceItem.setMobileFullImagePath(moderatedUrl.toString());              
			}
		}
		if(personalizeMobileThumbnailImage!=null && !personalizeMobileThumbnailImage.contains(moderateKeyValue)){
			if(!(BBBUtility.isEmpty(personalizeMobileThumbnailImage))){
		    	StringBuffer moderatedUrl = appendModerateUrl(moderateKeyValue,
						personalizeMobileThumbnailImage);
	            bbbCommerceItem.setMobileThumbnailImagePath(moderatedUrl.toString());              
			}
		}
		
		String personalizeImgUrl = bbbCommerceItem.getFullImagePath();
		String personalizeImgThumbUrl = bbbCommerceItem.getThumbnailImagePath();
		
		if(personalizeImgUrl!=null && (!personalizeImgUrl.contains(moderateKeyValue))){
			if(!(BBBUtility.isEmpty(personalizeImgUrl))){
		    	StringBuffer moderatedUrl = appendModerateUrl(moderateKeyValue,
						personalizeImgUrl);
	            bbbCommerceItem.setFullImagePath(moderatedUrl.toString());
	              
			}
		}
		if(personalizeImgThumbUrl!=null && !personalizeImgThumbUrl.contains(moderateKeyValue)){	
			if(!(BBBUtility.isEmpty(personalizeImgThumbUrl))){
			   StringBuffer moderatedUrlThumb = appendModerateUrl(
						moderateKeyValue, personalizeImgThumbUrl);
		           bbbCommerceItem.setThumbnailImagePath(moderatedUrlThumb.toString());	            
		   }			
		}
		
		logDebug("End Method: - setModeratedImagePath");
	}

	private StringBuffer appendModerateUrl(String moderateKeyValue,
			String personalizeMobileImgURL) {
		
		logDebug("Appending moderateKeyValue in the image URL");
		
		StringBuffer moderatedUrl = new StringBuffer();
		if(personalizeMobileImgURL.contains(BBBCoreConstants.DOT_EXTENTION)){
		     int indexOfDotExtension = personalizeMobileImgURL.lastIndexOf(BBBCoreConstants.DOT_EXTENTION);
		     String imageName = personalizeMobileImgURL.substring(0,indexOfDotExtension);
		     String extension = personalizeMobileImgURL.substring(indexOfDotExtension);
		     moderatedUrl = moderatedUrl.append(imageName).append(moderateKeyValue).append(extension);   
		}
		else{
		         moderatedUrl.append(personalizeMobileImgURL).append(moderateKeyValue);    
		}
		logDebug("moderate url is = " + moderatedUrl);
		return moderatedUrl;
	}
	
	/**
	 * This method returns the json modal for all the vendors in the BBB_CUSTOMIZATION_VENDOR table
	 * @return
	 */
	public String getVendorJSON() {
		
		logDebug("BBBEximManager.getVendorJSON method starts");
		
		Object[] params = null;
		String rql= BBBEximConstants.ALL;
		params = new Object[]{};
		Set<String> vendorSet = new HashSet<String>();
		IRepositoryWrapper iRepositoryWrapper = new RepositoryWrapperImpl(getVendorRepository());
		
		RepositoryItem[] items;
		String siteId = SiteContextManager.getCurrentSite().getId();
		items = iRepositoryWrapper.queryRepositoryItems(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR, rql, params,true);
		
		JSONObject jsonObject = new JSONObject();
		
		if(items!=null && items.length > 0){
			for(RepositoryItem item : items){
				
				if(!vendorSet.contains((String)item.getPropertyValue(BBBCatalogConstants.VENDORS_NAME_PROPERTY))){
					
					String vendorName = (String) item.getPropertyValue(BBBCatalogConstants.VENDORS_NAME_PROPERTY);
					String vendorJS = (String) item.getPropertyValue(BBBCatalogConstants.VENDOR_JS);
					String vendorJSMobile = (String) item.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE);
					String vendorCSS = (String) item.getPropertyValue(BBBCatalogConstants.VENDOR_CSS);
					String vendorApiURL = (String) item.getPropertyValue(BBBCatalogConstants.VENDOR_API_URL);
					String vendorApiKey = (String) item.getPropertyValue(BBBCatalogConstants.VENDOR_API_KEY);
					String vendorCssMobile = (String) item.getPropertyValue(BBBCatalogConstants.VENDOR_CSS_MOBILE);
					String apiVersion = (String) item.getPropertyValue(BBBCatalogConstants.VENDOR_API_VERSION);
					String clientId = null;
					if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
                    	clientId = ((String)item.getPropertyValue(BBBCatalogConstants.VENDOR_BBB_CLIENT_ID));
                    }else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
                    	clientId = ((String)item.getPropertyValue(BBBCatalogConstants.VENDOR_BAB_CLIENT_ID));
                    }else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
                    	clientId = ((String)item.getPropertyValue(BBBCatalogConstants.VENDOR_CAN_CLIENT_ID));
                    }else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_US))  {
                    	clientId = (String)item.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_BBB_CLIENT_ID);
                    } else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) {
                    	clientId = (String)item.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_CAN_CLIENT_ID);
                    } else if(siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BBB)) {
                    	clientId = (String)item.getPropertyValue(BBBCatalogConstants.VENDOR_TBS_BAB_CLIENT_ID);
                    }
					
					
					JSONObject object = new JSONObject();
					
					object.put(BBBCatalogConstants.VENDOR_JS, vendorJS);
					object.put(BBBCatalogConstants.VENDOR_JS_MOBILE, vendorJSMobile);
					object.put("vendorCSS", vendorCSS);
					object.put("vendorCssMobile", vendorCssMobile);
					object.put(BBBCatalogConstants.VENDOR_API_URL, vendorApiURL);
					object.put(BBBCatalogConstants.VENDOR_API_KEY, vendorApiKey);
					object.put(BBBCatalogConstants.VENDOR_METHOD_NAME, vendorName);
					object.put(BBBCatalogConstants.VENDOR_API_VERSION, apiVersion);
					
					if(clientId!=null){
						object.put("clientId",clientId);
					}					
					
					try {
						jsonObject.put(vendorName, object.toString());
					} catch (JSONException e) {
						logError("Json exception",e);
					}					
					vendorSet.add(vendorName);					
				}				
			}
		}
		
		logDebug("Vendor Json string is "  + jsonObject.toString());
		return jsonObject.toString();
	}
	

	
}
