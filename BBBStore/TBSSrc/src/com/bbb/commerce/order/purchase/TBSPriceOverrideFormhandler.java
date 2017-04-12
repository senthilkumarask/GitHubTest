package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.commerce.pricing.PricingException;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.order.TBSOrder;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.commerce.pricing.TBSPricingTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBShippingPriceInfo;


public class TBSPriceOverrideFormhandler extends PurchaseProcessFormHandler {
	
	private BBBCatalogTools bbbCatalogTools = null;

	private String reasonCode;
	private String competitor;
	private String overridePrice;
	private int overrideQuantity;
	private String shippingGroupID;
	private String commerceItemId;
	private String successURL="";
	private String errorURL="";
	private String taxExeptId;
	
	private Site site;
	
	private static final String NEW_PRICE = "newPrice";
	private static final String OVERRIDE_NON_ZERO = "Override Price is not zero";
	public static final String MSG_ERROR_UPDATE_ORDER = "errorUpdatingOrder";
	
	private TBSPricingTools pricingTools;
	private String fromPage;// Page Name that will be set from JSP
	
	private Map<String,String> successUrlMap;
    private Map<String,String> errorUrlMap;
	
	/*
	 * Validate the price override form and return the status vo for creating the json object
	 */
	
	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}

	public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleShippingPriceOverride(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		//Client DOM XSRF | Part -2
				if (StringUtils.isNotEmpty(getFromPage())) {
					setSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()));
					setErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
				}
		
		
    	final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
    	final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    	if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
    		BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
    		Transaction tr = null;
    		try {
    			tr = this.ensureTransaction();
    			if (this.getUserLocale() == null) {
    				this.setUserLocale(this.getUserLocale(pRequest, pResponse));
    			}
		
    			TBSOrder order = (TBSOrder)this.getOrder();    		    	
    			synchronized (order) {
    				
    		    	BBBHardGoodShippingGroup shipGroup = (BBBHardGoodShippingGroup)order.getShippingGroup(getShippingGroupID());

    		    	if(Double.compare(shipGroup.getPriceInfo().getAmount(), 0.0) == BBBCoreConstants.ZERO){
    		    		TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("false");
    	    			TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO("","Shipping amount is already Zero");
    	    			List<TBSPriceOverrideErrorMessageVO> errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
    	    			errorMessageVOList.add(errorMessageVO);
    	    			priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
    	    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
		    			return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
    		    	}
    		    	TBSShippingInfo shipInfo = shipGroup.getTbsShipInfo();
    		    	if( shipInfo == null ) {
    		    		if( isLoggingDebug() ) logDebug("Creating new shipInfo");
    		    		shipInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSShippingInfo();
    		    		shipGroup.setTbsShipInfo(shipInfo);
    		    	}
    		    	double overridePriceDbl = Double.parseDouble(this.getOverridePrice());
    		    	// Only allowing overrides to zero.
		    		if(Double.compare(overridePriceDbl, 0.0)  != BBBCoreConstants.ZERO  ) {
		    			if (isLoggingDebug()){
		    				logDebug("Non zero : " + getOverridePrice());
		    			}
		    			TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("false");
		    			priceOverrideStatusVO.setShippingGroupId(getShippingGroupID());
		    			TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO(NEW_PRICE+"_"+getShippingGroupID(),OVERRIDE_NON_ZERO);
		    			List<TBSPriceOverrideErrorMessageVO> errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
		    			errorMessageVOList.add(errorMessageVO);
		    			priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
		    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
		    			if (isLoggingDebug()){
		    				logDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    			}
		    			return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
		    		}
		    		
		    		double thresholdPrice = getBbbCatalogTools().getOverrideThreshold(getSite().getId(),BBBCatalogConstants.TBS_SHIPPING_OVERRIDE_THRESHOLD);
		    		double origPrice = shipGroup.getPriceInfo().getRawShipping();
    		    	Map<String, Boolean> pricemap = order.getOverridePriceMap();
		    		if (isLoggingDebug()){
		    			logDebug("thresholdPrice="+thresholdPrice);
		    		} 
		    		if( origPrice >= thresholdPrice ) {
		    			// Set the order to require approval 
		    			if (isLoggingDebug()){
		    				logDebug("needs approval");
		    			}
		    			order.setTBSApprovalRequired(true);
		    			pricemap.put(shipGroup.getId(), Boolean.TRUE);
		    			order.setOverridePriceMap(pricemap);
		    		}
   		    		else if( pricemap.containsKey(shipGroup.getId()) ) {
		    				pricemap.remove(shipGroup.getId());
   		    			if (pricemap.keySet().isEmpty()) {
   		    				order.setTBSApprovalRequired(false);
   		    			}
		    		}

		    			    	    		    		
    		    	// Set is price override flag
    		    	shipInfo.setShipPriceOverride(true);
    		    	// set override price
    		    	shipInfo.setShipPriceValue(overridePriceDbl);
    		    	// Set override reason 
    		    	shipInfo.setShipPriceReason(this.getReasonCode());

		    		TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("true");
		    		priceOverrideStatusVO.setShippingGroupId(shippingGroupID);
		    		priceOverrideStatusVO.setOverridePrice(overridePriceDbl);
		    		pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
	    			getOrderManager().updateOrder(order);
		    		if (isLoggingDebug()){
		    			logDebug("4. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    		}
    			}
    		}
    		catch( Exception ex ) {
    			ex.printStackTrace();    				
    			TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("false");
    			TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO("",ex.getMessage());
    			List<TBSPriceOverrideErrorMessageVO> errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
    			errorMessageVOList.add(errorMessageVO);
    			priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
    			if (isLoggingDebug()){
    				logDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    			}  
    		}
    	}    	
    	return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);    				
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleTaxPriceOverride(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		//Client DOM XSRF | Part -2
		if (StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
		}
		
    	final RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
    	final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    	if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
    		BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
    		Transaction tr = null;
    		try {
    			tr = this.ensureTransaction();
    			if (this.getUserLocale() == null) {
    				this.setUserLocale(this.getUserLocale(pRequest, pResponse));
    			}
		
    			synchronized (this.getOrder()) {
    				BBBHardGoodShippingGroup hardShip = null;
    				BBBStoreShippingGroup storeShip = null;
    				TBSShippingInfo shipInfo = null;
    				
    		    	TBSOrder order = (TBSOrder) getOrder();    		    	
    		    	ShippingGroup shipGroup = (ShippingGroup)order.getShippingGroup(shippingGroupID);
    		    	double overridePriceDbl = Double.parseDouble(this.getOverridePrice());
    		    	double taxBeforeOverride = order.getPriceInfo().getTax();
    		    	
    		    	if(shipGroup instanceof BBBHardGoodShippingGroup){
    		    		hardShip = (BBBHardGoodShippingGroup)shipGroup;
    		    	} else if(shipGroup instanceof BBBStoreShippingGroup){
    		    		storeShip = (BBBStoreShippingGroup)shipGroup;
    		    	}
    		    	if(hardShip != null){
    		    		shipInfo = hardShip.getTbsShipInfo();
    		    		if( shipInfo == null ) {
    		    			vlogDebug("Creating new shipInfo");
    		    			shipInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSShippingInfo();
    		    			hardShip.setTbsShipInfo(shipInfo);
    		    			
    		    		}
    		    	}
    		    	
    		    	if(storeShip != null){
    		    		shipInfo = storeShip.getTbsShipInfo();
    		    		if( shipInfo == null ) {
    		    			vlogDebug("Creating new shipInfo");
    		    			shipInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSShippingInfo();
    		    			storeShip.setTbsShipInfo(shipInfo);
    		    			
    		    		}
    		    	}
    		    	
    		    	// Only allowing overrides to zero.
		    		if(Double.compare(overridePriceDbl, 0.0)  != BBBCoreConstants.ZERO) {
		    			vlogDebug("Non zero : " + getOverridePrice());
		    			TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("false");
		    			priceOverrideStatusVO.setShippingGroupId(getShippingGroupID());
		    			TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO(NEW_PRICE+"_"+getShippingGroupID(),OVERRIDE_NON_ZERO);
		    			List<TBSPriceOverrideErrorMessageVO> errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
		    			errorMessageVOList.add(errorMessageVO);
		    			priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
		    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
		    			vlogDebug("4. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
		    			return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
		    		}
		    		
		    		double thresholdPrice = getBbbCatalogTools().getOverrideThreshold(getSite().getId(),BBBCatalogConstants.TBS_TAX_OVERRIDE_THRESHOLD);
    		    	Map<String, Boolean> pricemap = order.getOverridePriceMap();
		    		if (isLoggingDebug()){
		    			logDebug("thresholdPrice="+thresholdPrice);
		    		} 
		    		if( taxBeforeOverride >= thresholdPrice ) {
		    			// Set the order to require approval 
		    			vlogDebug("needs approval");
		    			order.setTBSApprovalRequired(true);
		    			pricemap.put(shippingGroupID, Boolean.TRUE);
		    			order.setOverridePriceMap(pricemap);
		    		} else if( pricemap.containsKey(shippingGroupID)) {
		    				pricemap.remove(shippingGroupID);
   		    			if (pricemap.keySet().isEmpty()) {
   		    				order.setTBSApprovalRequired(false);
   		    			}
		    		}
		    		if(shipInfo != null){
		    			// Set is price override flag
		    			shipInfo.setTaxOverride(true);
		    			// set override type
		    			shipInfo.setTaxOverrideType(0);
		    			// set override price
		    			shipInfo.setTaxValue(overridePriceDbl);
		    			// Set override reason 
		    			shipInfo.setTaxReason(this.getReasonCode());
		    			//set Tax exemption Id
		    			shipInfo.setTaxExemptId(getTaxExeptId());
		    		}
    		    	
    		    	TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("true");
		    		priceOverrideStatusVO.setShippingGroupId(shippingGroupID);
		    		priceOverrideStatusVO.setOverridePrice(overridePriceDbl);
		    		pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
		    		vlogDebug("4. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    			}
    		}
    		catch( Exception ex ) {
    			vlogError("Exception occurred while overriding tax :: "+ex);
    			TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("false");
    			TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO("",ex.getMessage());
    			List<TBSPriceOverrideErrorMessageVO> errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
    			errorMessageVOList.add(errorMessageVO);
    			priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
    			vlogDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		}
    	}
		return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);    				
    }
	
	/*
	 * Validate the price override form and return the status vo for creating the json object
	 */
	
	
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public boolean handleShipSurchargePriceOverride(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		//Client DOM XSRF | Part -2
		if (StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
		}

    	RepeatingRequestMonitor rrm = this.getRepeatingRequestMonitor();
    	String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
    	if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
    		BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_ORDER, myHandleMethod);
    		Transaction tr = null;
    		try {
    			tr = this.ensureTransaction();
    			if (this.getUserLocale() == null) {
    				this.setUserLocale(this.getUserLocale(pRequest, pResponse));
    			}
		
    			TBSOrder order = (TBSOrder)this.getOrder(); 
    			double surCharges = 0.0;
    			TBSShippingInfo shipInfo = null;
    			
    			double overridePriceDbl = Double.parseDouble(this.getOverridePrice());
		    	// Only allowing overrides to zero.
	    		if(Double.compare(overridePriceDbl, 0.0) != BBBCoreConstants.ZERO ) {
	    			vlogDebug("Non zero : " + getOverridePrice());
	    			TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("false");
	    			priceOverrideStatusVO.setShippingGroupId(getShippingGroupID());
	    			TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO(NEW_PRICE+"_"+getShippingGroupID(),OVERRIDE_NON_ZERO);
	    			List<TBSPriceOverrideErrorMessageVO> errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
	    			errorMessageVOList.add(errorMessageVO);
	    			priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
	    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
	    			vlogDebug("3. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
	    			return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
	    		}
	    		
    			synchronized (order) {
    				String shipId = getShippingGroupID();
    				if(!StringUtils.isEmpty(shipId)){
    					BBBHardGoodShippingGroup hardShipGroup = (BBBHardGoodShippingGroup)order.getShippingGroup(shipId);
    					shipInfo = surchargeInfo(order, shipInfo, hardShipGroup);
    				} else {
    					
    					List<ShippingGroup> shipGroups = order.getShippingGroups();
    					for (ShippingGroup shipGroup : shipGroups) {
    						BBBHardGoodShippingGroup hardShipGroup = null;
    						if(shipGroup instanceof BBBHardGoodShippingGroup){
    							hardShipGroup = (BBBHardGoodShippingGroup)shipGroup;
    							shipInfo = surchargeInfo(order, shipInfo, hardShipGroup);
    						}
    					}
    				}
		    		if(shipInfo != null){
		    			// Set is price override flag
	    		    	shipInfo.setSurchargeOverride(true);
	    		    	// set override price
	    		    	shipInfo.setSurchargeValue(overridePriceDbl);
	    		    	// Set override reason 
	    		    	shipInfo.setSurchargeReason(this.getReasonCode());		    			
		    		}

		    		TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("true");
		    		priceOverrideStatusVO.setShippingGroupId(shippingGroupID);
		    		priceOverrideStatusVO.setOverridePrice(overridePriceDbl);
		    		pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
	    			getOrderManager().updateOrder(order);
		    		vlogDebug("4. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    			}
    		}
    		catch( Exception ex ) {
    			ex.printStackTrace();    				
    			TBSPriceOverrideStatusVO priceOverrideStatusVO = new TBSPriceOverrideStatusVO("false");
    			TBSPriceOverrideErrorMessageVO errorMessageVO = new TBSPriceOverrideErrorMessageVO("",ex.getMessage());
    			List<TBSPriceOverrideErrorMessageVO> errorMessageVOList = new ArrayList<TBSPriceOverrideErrorMessageVO>();
    			errorMessageVOList.add(errorMessageVO);
    			priceOverrideStatusVO.setErrorMessages(errorMessageVOList);
    			pRequest.getSession().setAttribute("priceOverrideStatusVO", priceOverrideStatusVO);
    			vlogDebug("6. TBSPriceOverrideMessageVO success:"+priceOverrideStatusVO.getSuccess());
    		}
    	}    	
    	return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);    				
	}

	private TBSShippingInfo surchargeInfo(TBSOrder order,
			TBSShippingInfo shipInfo, BBBHardGoodShippingGroup hardShipGroup) throws PricingException,
			CommerceException, BBBBusinessException, BBBSystemException {
		double surCharges;
		List<CommerceItemRelationship> shipCItems =  hardShipGroup.getCommerceItemRelationships();
		for (CommerceItemRelationship cItemRelationship : shipCItems) {
			if(cItemRelationship.getCommerceItem() instanceof BBBCommerceItem){
				surCharges = getPricingTools().calculateSurcharge(order.getSiteId(), hardShipGroup);
				if(surCharges > 0){
					shipInfo = hardShipGroup.getTbsShipInfo();
					if( shipInfo == null ) {
						vlogDebug("Creating new shipInfo");
						shipInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSShippingInfo();
						hardShipGroup.setTbsShipInfo(shipInfo);
					}
					double thresholdPrice = getBbbCatalogTools().getOverrideThreshold(getSite().getId(),BBBCatalogConstants.TBS_SURCHARGE_OVERRIDE_THRESHOLD);
					double origPrice = 0;
					if(hardShipGroup.getPriceInfo() instanceof BBBShippingPriceInfo){
						origPrice = ((BBBShippingPriceInfo)hardShipGroup.getPriceInfo()).getSurcharge();
					}
					Map<String, Boolean> pricemap = order.getOverridePriceMap();
					if (isLoggingDebug()){
						logDebug("thresholdPrice="+thresholdPrice);
					} 
					if( origPrice >= thresholdPrice ) {
						// Set the order to require approval 
						vlogDebug("needs approval");
						order.setTBSApprovalRequired(true);
						pricemap.put(hardShipGroup.getId(), Boolean.TRUE);
						order.setOverridePriceMap(pricemap);
					} else if( pricemap.containsKey(hardShipGroup.getId()) ) {
						pricemap.remove(hardShipGroup.getId());
						if (pricemap.keySet().isEmpty()) {
							order.setTBSApprovalRequired(false);
						}
					}
				} 
			}
		}
		return shipInfo;
	}

	public String getReasonCode() {
		return reasonCode;
	}


	public void setReasonCode(String reason_code) {
		this.reasonCode = reason_code;
	}


	public String getCompetitor() {
		return competitor;
	}

	public void setCompetitor(String competitor) {
		this.competitor = competitor;
	}

	public String getOverridePrice() {		
			return overridePrice;
	}

	public void setOverridePrice(String override_price) {
			this.overridePrice = override_price;
	}

    public final String getCommerceItemId() {
        return this.commerceItemId;
    }

    public final void setCommerceItemId(final String pCommerceItemId) {
        this.commerceItemId = pCommerceItemId;
    }

	public String getShippingGroupID() {
		return shippingGroupID;
	}

	public void setShippingGroupID(String shippingGroupID) {
		this.shippingGroupID = shippingGroupID;
	}

	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	public int getOverrideQuantity() {
		return overrideQuantity;
	}

	public void setOverrideQuantity(int overrideQuantity) {
		this.overrideQuantity = overrideQuantity;
	}	

	public String getSuccessURL() {
		return successURL;
	}
	
	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}
	
	public String getErrorURL() {
		return errorURL;
	}
	
	public void setErrorURL(String errorURL) {
		this.errorURL = errorURL;
	}

	/**
	 * @return the site
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * @param pSite the site to set
	 */
	public void setSite(Site pSite) {
		site = pSite;
	}

	/**
	 * @return the taxExeptId
	 */
	public String getTaxExeptId() {
		return taxExeptId;
	}

	/**
	 * @param pTaxExeptId the taxExeptId to set
	 */
	public void setTaxExeptId(String pTaxExeptId) {
		taxExeptId = pTaxExeptId;
	}

	/**
	 * @return the pricingTools
	 */
	public TBSPricingTools getPricingTools() {
		return pricingTools;
	}

	/**
	 * @param pricingTools the pricingTools to set
	 */
	public void setPricingTools(TBSPricingTools pricingTools) {
		this.pricingTools = pricingTools;
	}

	
}
