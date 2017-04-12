package com.bbb.common.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.ServletException;

import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.cms.tools.CmsTools;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.checkout.droplet.GetApplicableShippingMethodsDroplet;
import com.bbb.commerce.checkout.droplet.GetSddShippingMethodsDroplet;
import com.bbb.commerce.checkout.vo.ShipMethodsResponseVORest;
import com.bbb.commerce.checkout.vo.ShipMethodsVORest;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.common.BBBGenericService;
import com.bbb.common.vo.AppShipMethodPriceVO;
import com.bbb.common.vo.ShippingInfoKey;
import com.bbb.common.vo.StatesShippingMethodPriceVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * @author vagrawal5
 * 
 *         This Class perform all the business logic for the shipping method
 *         details.
 */
public class ShippingMethodManager extends BBBGenericService {

	/**
	 * instance of CmsTools
	 */
	private CmsTools mCmsTools;
	
	/**
	 * instance of BBBCatalogTools
	 */
	private BBBCatalogTools mCatalogTools;
	
	/**
	 * Instance of GetApplicableShippingMethodsDroplet.
	 */
	private GetApplicableShippingMethodsDroplet shippingMethodsDroplet;
	
	private GetSddShippingMethodsDroplet sddShippingMethodsDroplet;
	
	private String mAlaskaStateCode ;
	private String mHawaiiStateCode;
	
	/**
	 * Setter for Shipping manager.
	 */
	public void setShippingMethodsDroplet(GetApplicableShippingMethodsDroplet shippingMethodsDroplet) {
		this.shippingMethodsDroplet = shippingMethodsDroplet;
	}

	/**
	 * Getter for Shipping manager.
	 */
	public GetApplicableShippingMethodsDroplet getShippingMethodsDroplet() {
		return shippingMethodsDroplet;
	}
	
	public GetSddShippingMethodsDroplet getSddShippingMethodsDroplet() {
		return sddShippingMethodsDroplet;
	}

	public void setSddShippingMethodsDroplet(
			GetSddShippingMethodsDroplet sddShippingMethodsDroplet) {
		this.sddShippingMethodsDroplet = sddShippingMethodsDroplet;
	}

	/**
	 * Instance of Shipping manager.
	 */
	private BBBShippingGroupManager mShippingGroupManager;

	/**
	 * Instance of PricingTools.
	 */
	private PricingTools mPricingTools;
	
	/**
	 * Setter for Pricing Tool.
	 * 
	 * @param pPricingTools
	 */
	public void setPricingTools(PricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}

	/**
	 * Getter for Pricing Tool.
	 * 
	 * @return mPricingTools
	 */
	public PricingTools getPricingTools() {
		return mPricingTools;
	}

	/**
	 * Getter for BBBShippingGroupManager.
	 * 
	 * @return mShippingGroupManager.
	 */
	public BBBShippingGroupManager getShippingGroupManager() {
		return mShippingGroupManager;
	}

	/**
	 * Setter for BBBShippingGroupManager.
	 * 
	 * @param pShipingGrpMgr
	 *            BBB Shipping Group Manager
	 */
	public void setShippingGroupManager(
			final BBBShippingGroupManager pShipingGrpMgr) {
		this.mShippingGroupManager = pShipingGrpMgr;
	}
	
	
	 /*
	 * ===================================================================
	 * ----------------------- CONSTANTS --------------------------------
	 * ===================================================================
	 */
	
	private static final String SINGLE_SHIPPING_MODE = "singleShipping";
	private static final String MULTI_SHIPPING_MODE = "multiShipping";
	private static final String CHOOSE_SDD_OPTION = "chooseSddOption";
	private static final String CURRENT_ZIP = "currentZip";
	
	/**
	 * Gets the details of shipping prices to various states
	 * 
	 * @param pSiteId
	 * @return
	 * @throws RepositoryException
	 */
	public Map<String, Map<ShippingInfoKey, StatesShippingMethodPriceVO>> getShippingPriceTableDetail(String pSiteId) throws RepositoryException {
		logDebug("Starting method ShippingMethodManager.getShippingPriceTableDetail");

		Map<String, Map<ShippingInfoKey, StatesShippingMethodPriceVO>> shipPriceTableMap = null;
		Map<ShippingInfoKey, StatesShippingMethodPriceVO> shippingMapUS; // pricing details for US (where state id is blank)
		Map<ShippingInfoKey, StatesShippingMethodPriceVO> shippingMapAlHi; // pricing details for Alaska and Hawaii
		Map<ShippingInfoKey, StatesShippingMethodPriceVO> shippingMapOthers; // pricing details for other states
		
		final RepositoryItem[] shippingMethodDetails = getCmsTools().getAllShippingPriceDetailsAsc(pSiteId); 
		if (shippingMethodDetails != null) {			
			shippingMapUS = new HashMap<ShippingInfoKey, StatesShippingMethodPriceVO>();
			shippingMapAlHi = new HashMap<ShippingInfoKey, StatesShippingMethodPriceVO>();
			shippingMapOthers = new HashMap<ShippingInfoKey, StatesShippingMethodPriceVO>();
			
			for(RepositoryItem current : shippingMethodDetails){
				
				RepositoryItem stateItem = null;
				String state = null;
				boolean isTerritory = false;

				if (current.getPropertyValue(BBBCmsConstants.STATE_LOWER_CASE) instanceof RepositoryItem) {
					stateItem = (RepositoryItem) current.getPropertyValue(BBBCmsConstants.STATE_LOWER_CASE);
					state = (String) stateItem.getPropertyValue(BBBCmsConstants.ID);
					if(stateItem.getPropertyValue(BBBCmsConstants.TERRITORY)!=null ){
						isTerritory=(Boolean)stateItem.getPropertyValue(BBBCmsConstants.TERRITORY) ;
					}
				}
				
				if(StringUtils.isBlank(state)){ // For US
					
					addPricingMethod(current, shippingMapUS);		
					
				}else if(isTerritory){ // for Alaska and Hawaii
					
					addPricingMethod(current, shippingMapAlHi);		
					
				}else{ // For other states 
					
					addPricingMethod(current, shippingMapOthers);			
				}
			}
			
			shipPriceTableMap = new HashMap<String, Map<ShippingInfoKey,StatesShippingMethodPriceVO>>();
			// treemap is used to keep the prices in sorted order for easy display
			shipPriceTableMap.put(BBBCmsConstants.DEFAULT_STATE_US, new TreeMap<ShippingInfoKey, StatesShippingMethodPriceVO>(shippingMapUS));
			shipPriceTableMap.put(BBBCmsConstants.DEFAULT_STATE_ALHI, new TreeMap<ShippingInfoKey, StatesShippingMethodPriceVO>(shippingMapAlHi));
			shipPriceTableMap.put(BBBCmsConstants.DEFAULT_STATE_OTHERS, new TreeMap<ShippingInfoKey, StatesShippingMethodPriceVO>(shippingMapOthers));
				
		}
		
		logDebug("Starting method ShippingMethodManager.getShippingPriceTableDetail");

		return shipPriceTableMap;
		
	}
	
	/**
	 * Adds the pricing details to the state specific map
	 * 
	 * @param pShippingMethodDetail
	 * @param shippingMap
	 */
	private void addPricingMethod(final RepositoryItem pShippingMethodDetail,
									final Map<ShippingInfoKey, StatesShippingMethodPriceVO> shippingMap) {
		logDebug("Entering ShippingMethodManager.addPricingMethod");
	
		BigDecimal minOrderPrice=null;
		BigDecimal maxOrderPrice=null;
		final ShippingInfoKey shippingInfoKey = new ShippingInfoKey();
		
		if (pShippingMethodDetail.getPropertyValue(BBBCmsConstants.LOWERLIMIT) instanceof Number) {
		 	minOrderPrice=new BigDecimal((Double) pShippingMethodDetail.getPropertyValue(BBBCmsConstants.LOWERLIMIT)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
		 	if (minOrderPrice == null) {
		 		minOrderPrice = BigDecimal.valueOf(0.0d);
		 	}
            shippingInfoKey.setMinOrderPrice(minOrderPrice);
		} 
		
		if (pShippingMethodDetail.getPropertyValue(BBBCmsConstants.UPPERLIMIT) instanceof Number) {
			maxOrderPrice=new BigDecimal((Double) pShippingMethodDetail.getPropertyValue(BBBCmsConstants.UPPERLIMIT)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
			if (maxOrderPrice == null) {
				maxOrderPrice = BigDecimal.valueOf(0.0d);
		 	}
			shippingInfoKey.setMaxOrderPrice(maxOrderPrice);
		}
		logDebug("Key is : "+shippingInfoKey.toString());
		
		String stateName = null;
		if (pShippingMethodDetail.getPropertyValue(BBBCmsConstants.STATE_LOWER_CASE) instanceof RepositoryItem) {
			final RepositoryItem stateItem = (RepositoryItem) pShippingMethodDetail.getPropertyValue(BBBCmsConstants.STATE_LOWER_CASE);
			stateName = (String) stateItem.getPropertyValue("ID");
			if(StringUtils.isBlank(stateName)){
				stateName = BBBCmsConstants.DEFAULT_STATE_US;
			}else{
				stateName = (String) stateItem.getPropertyValue("descrip");
			}
		}
		
		
		String shippingMethodName = null;
		if (((RepositoryItem) pShippingMethodDetail.getPropertyValue(BBBCmsConstants.SHIP_METHOD_CODE))
				.getPropertyValue(BBBCmsConstants.SHIP_METHOD_NAME) instanceof String) {

			shippingMethodName = (String) (((RepositoryItem) pShippingMethodDetail
												.getPropertyValue(BBBCmsConstants.SHIP_METHOD_CODE))
												.getPropertyValue(BBBCmsConstants.SHIP_METHOD_NAME));
		}
		
		String shippingPrice = null;
		if (pShippingMethodDetail.getPropertyValue(BBBCmsConstants.PRICE) instanceof Number) {
			
			final Double shipPrice = (Double) pShippingMethodDetail.getPropertyValue(BBBCmsConstants.PRICE);
			if(shipPrice != null) {
				shippingPrice = shipPrice.toString();
			}
		}
	
		
		StatesShippingMethodPriceVO statesShippingMethodPriceVO = shippingMap.get(shippingInfoKey);
		if(statesShippingMethodPriceVO == null  ){			// if this detail is not already there
			statesShippingMethodPriceVO = new StatesShippingMethodPriceVO();
			statesShippingMethodPriceVO.setStateName(stateName);
			
			final List<AppShipMethodPriceVO> pAppShipMethodPriceVO = new ArrayList<AppShipMethodPriceVO>();
			final AppShipMethodPriceVO appShipMethodPriceVO = new AppShipMethodPriceVO(shippingMethodName, shippingPrice);
			appShipMethodPriceVO.setmAppShipMethodName(shippingMethodName);
			
			pAppShipMethodPriceVO.add(appShipMethodPriceVO);
			statesShippingMethodPriceVO.setAppShipMethodPriceVO(pAppShipMethodPriceVO);
			
			shippingMap.put(shippingInfoKey, statesShippingMethodPriceVO);
			
			statesShippingMethodPriceVO.setMinOrderPrice(minOrderPrice);
			statesShippingMethodPriceVO.setMaxOrderPrice(maxOrderPrice);
			
		}else{ //fetch the list and add to the existing list			
			
			final List<AppShipMethodPriceVO> appShipMethodPriceVOList = statesShippingMethodPriceVO.getAppShipMethodPriceVO();
			boolean exists = false;
			if(null!=shippingMethodName){
				for(AppShipMethodPriceVO curr : appShipMethodPriceVOList){
					if(shippingMethodName.equals(curr.getmAppShipMethodName())){
						exists = true;
					}
				}
			}
			if(!exists){
				final AppShipMethodPriceVO appShipMethodPriceVOEx = new AppShipMethodPriceVO(shippingMethodName, shippingPrice);
				appShipMethodPriceVOList.add(appShipMethodPriceVOEx);
				Collections.sort(appShipMethodPriceVOList);
			}
			
		}
		
		logDebug("Exiting ShippingMethodManager.addPricingMethod");
		
	}

	/**
	 * Returns all the available shipping methods.
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public Set<String> getShippingMethods(String siteId) throws RepositoryException {
		logDebug("Starting method ShippingMethodManager.getShippingMethods");
		
		final RepositoryItem[] shippingMethods = getCmsTools().getShippingMethods(siteId);
		Set<String> shipMethods = null;
		if (shippingMethods != null) {
			shipMethods = new TreeSet<String>();
			for(RepositoryItem current : shippingMethods){
				if (current.getPropertyValue(BBBCmsConstants.SHIP_METHOD_NAME) != null) {
					shipMethods.add((String) current.getPropertyValue(BBBCmsConstants.SHIP_METHOD_NAME));
				}
			}
		}
		
		return shipMethods;		
	}

	/**
	 * This method returns shippping method details
	 * 
	 * @return Map<String, List<String>>
	 * @throws RepositoryException
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public Map<String, List<String>> getShippingMethodDetails(String siteId)
			throws RepositoryException, BBBSystemException, BBBBusinessException {
		logDebug("Starting method ShippingMethodManager.getShippingMethodDetails");

		int mMinDaysAdditionToAlHUU;
		int mMaxDaysAdditionToAlHUU;
		final Map<String, List<String>> unSortedshippingMethodDetails = new LinkedHashMap<String, List<String>>();
		TreeMap<String,List<String>> shippingMethodDetails = null;
		
		final RepositoryItem[] shippingMethods = getCmsTools().getShippingMethods(siteId);

		if (shippingMethods != null && shippingMethods.length > 0) {
			for (RepositoryItem shippingMethod : shippingMethods) {

				final List<String> businessDays = new ArrayList<String>();

				if (shippingMethod != null) {

					RepositoryItem shippingDuration  = this.getCatalogTools().getShippingDuration(shippingMethod.getRepositoryId(), siteId);
					String shippingMethodName = null;
					Integer minDaysToShip = null;
					Integer maxDaysToShip = null;
					Integer minDaysToShipVDC = null;
					Integer maxDaysToShipVDC = null;

					if (shippingMethod
							.getPropertyValue(BBBCmsConstants.SHIP_METHOD_NAME) != null) {
						shippingMethodName = (String) shippingMethod
								.getPropertyValue(BBBCmsConstants.SHIP_METHOD_NAME);

					}
					if(shippingDuration != null){
						if (shippingDuration.getPropertyValue(BBBCmsConstants.MIN_DAYS_TO_SHIP) != null) {
							minDaysToShip = (Integer) shippingDuration.getPropertyValue(BBBCmsConstants.MIN_DAYS_TO_SHIP);
						}
						if (shippingDuration.getPropertyValue(BBBCmsConstants.MAX_DAYS_TO_SHIP) != null) {
							maxDaysToShip = (Integer) shippingDuration.getPropertyValue(BBBCmsConstants.MAX_DAYS_TO_SHIP);
						}
						if (shippingDuration.getPropertyValue(BBBCmsConstants.MIN_DAYS_TO_SHIP_VDC) != null) {
							minDaysToShipVDC = (Integer) shippingDuration.getPropertyValue(BBBCmsConstants.MIN_DAYS_TO_SHIP_VDC);
						}
						if (shippingDuration.getPropertyValue(BBBCmsConstants.MAX_DAYS_TO_SHIP_VDC) != null) {
							maxDaysToShipVDC = (Integer) shippingDuration.getPropertyValue(BBBCmsConstants.MAX_DAYS_TO_SHIP_VDC);
						}
					}
					
					if (minDaysToShip != null
							&& maxDaysToShip != null
							&& !unSortedshippingMethodDetails
									.containsKey(shippingMethodName)) {
						businessDays.add(minDaysToShip.toString());
						businessDays.add(maxDaysToShip.toString());
						// Adding 2 extra days for Alaska & Hawaii
						if(!siteId.equals("BedBathCanada")){
						//calling the BBBcatalogtools to get config type
						mMinDaysAdditionToAlHUU= Integer.parseInt(getCatalogTools().getAllValuesForKey(
								BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCmsConstants.MIN_DAYS_ADDITION_TO_ALHUU).get(0));
						mMaxDaysAdditionToAlHUU= Integer.parseInt(getCatalogTools().getAllValuesForKey(
								BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCmsConstants.MAX_DAYS_ADDITION_TO_ALHUU).get(0));
						businessDays.add(minDaysToShip
								+ mMinDaysAdditionToAlHUU
								+ BBBCmsConstants.BLANK);
						businessDays.add(maxDaysToShip
								+ mMaxDaysAdditionToAlHUU
								+ BBBCmsConstants.BLANK);
						}
						unSortedshippingMethodDetails.put(shippingMethodName,
								businessDays);
						
						
						
					}
					
					
				}

			}
		} else {
				logDebug("No shippingMethods found in shippingMethods item of shippingRepository");
		}
		//For sorting of shipping methds based on 
		if(unSortedshippingMethodDetails != null){
			ValueComparator valueComparator =  new ValueComparator(unSortedshippingMethodDetails);

		    shippingMethodDetails = new TreeMap<String,List<String>>(valueComparator);
		    shippingMethodDetails.putAll(unSortedshippingMethodDetails);
		     
		}
		

			logDebug("Existing method ShippingMethodManager.getShippingMethodDetails");
			logDebug("Returned ShippingMethodDetails :::: "
					+ unSortedshippingMethodDetails);


		return shippingMethodDetails;
	}

	/**
	 * @return instance of CmsTools
	 */
	public CmsTools getCmsTools() {
		return mCmsTools;
	}

	/**
	 * @param pCmsTools
	 *            the pCmsTools to set
	 */
	public void setCmsTools(final CmsTools pCmsTools) {
		mCmsTools = pCmsTools;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	
	/**
	 * @return the mAlaskaStateCode
	 */
	public String getAlaskaStateCode() {
		return mAlaskaStateCode;
	}

	/**
	 * @param pAlaskaStateCode the mAlaskaStateCode to set
	 */
	public void setAlaskaStateCode(final String pAlaskaStateCode) {
		this.mAlaskaStateCode = pAlaskaStateCode;
	}

	/**
	 * @return the mHawaiiStateCode
	 */
	public String getHawaiiStateCode() {
		return mHawaiiStateCode;
	}

	/**
	 * @param pHawaiiStateCode the mHawaiiStateCode to set
	 */
	public void setHawaiiStateCode(final String pHawaiiStateCode) {
		this.mHawaiiStateCode = pHawaiiStateCode;
	}

	/**
	 * This class is used for sorting based on max shipping days.
	 * @author ajosh8
	 *
	 */
	class ValueComparator implements Comparator<Object>{
	    Map<String, List<String>> map;

	    public ValueComparator(Map<String, List<String>> map) {
	           super();
	           this.map = map;
	    }
	    
	    
	    public int compare(Object a, Object b) {
	        
	        //Getting size because in ca list would have less size
	        int sizeLst1 =   ((List<String>)map.get(a)).size();
	        int sizeLst2 = ((List<String>)map.get(b)).size();
	        
	        int maxShippingDays1 = Integer.parseInt(((String)((List<String>)map.get(a)).get(sizeLst1-1)));
	        int maxShippingDays2 = Integer.parseInt((String)((List<String>)map.get(b)).get(sizeLst2-1));
	        boolean k=  maxShippingDays1 <  maxShippingDays2;
	        if (k) {
	          return 1;
	        } else {
	          return -1;
	        }
	           
	           
	    }
	  
	}
	
	/**
	 * 
	 * To get shipping methods from currentOrder.
	 * 
	 * @param shippingMode
	 * @return ShipMethodsResponseVORest
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public ShipMethodsResponseVORest getShippingMethodsFromCurrentOrder(String shippingMode, String sddShipZip) throws BBBSystemException, BBBBusinessException {

		logDebug("Starting method ShippingMethodManager.getShippingMethodsFromCurrentOrder, Shipping mode:" + shippingMode);
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();

		OrderHolder cart = (OrderHolder) pRequest.resolveName("/atg/commerce/ShoppingCart");
		BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
		String operationParam = null;
		ShipMethodsResponseVORest shipMethodsResponse = null;
		
		if (null == order
				|| StringUtils.isBlank(shippingMode)
				|| !(org.apache.commons.lang.StringUtils.equalsIgnoreCase(SINGLE_SHIPPING_MODE, shippingMode) || org.apache.commons.lang.StringUtils
						.equalsIgnoreCase(MULTI_SHIPPING_MODE, shippingMode))) {

			
			logDebug("Shipping Mode is incorrect throw an error, shippingMode:" + shippingMode);
			
			throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1001,
					"Invalid input- must be [singleShipping | multiShipping]");

		} else if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(shippingMode, SINGLE_SHIPPING_MODE)) {
			operationParam = BBBCoreConstants.PER_ORDER;
		} else if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(shippingMode, MULTI_SHIPPING_MODE)) {
			operationParam = BBBCoreConstants.PER_SKU;
		}

		try {

			pRequest.setParameter(BBBCoreConstants.ORDER, order);
			pRequest.setParameter(BBBCoreConstants.OPERATION, operationParam);
			pRequest.setParameter(CURRENT_ZIP, sddShipZip);
			pRequest.setParameter("checkForInventory", "true");
			logDebug("ShippingMethodManager.getShippingMethodsFromCurrentOrder; ShippingMethodsDroplet Call start for Shipping mode:" + shippingMode);
			
			getShippingMethodsDroplet().service(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse());
			logDebug("ShippingMethodManager.getShippingMethodsFromCurrentOrder; ShippingMethodsDroplet Call end for Shipping mode:" + shippingMode);
			
			pRequest.getParameter(BBBCoreConstants.SKU_MEHOD_MAP);
			
			shipMethodsResponse = populateShipMethodsResponse(pRequest, shippingMode);

		} catch (ServletException e) {
			
			logError("ShippingMethodManager.getShippingMethodsFromCurrentOrder, Error occurred while fetching data: " + BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1002);
			
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1002, "Error occurred while fetching data");
		} catch (IOException e) {
			
			logError("ShippingMethodManager.getShippingMethodsFromCurrentOrder,Error occurred while fetching data: " + BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1003);
			
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1003, "Error occurred while fetching data");
		}
		
		
		logDebug("Exiting method ShippingMethodManager.getShippingMethodsFromCurrentOrder");
		

		return shipMethodsResponse;

	}
	
	
	/**
	 * 
	 * To get shipping methods from currentOrder.
	 * 
	 * @param shippingMode
	 * @return ShipMethodsResponseVORest
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public ShipMethodsResponseVORest getSddShippingMethodFromCurrentOrder(String sddShipZip, String chooseSddOption) throws BBBSystemException, BBBBusinessException {

		logDebug("Starting method ShippingMethodManager.getSddShippingMethodFromCurrentOrder, zipcode:" + sddShipZip + CHOOSE_SDD_OPTION + chooseSddOption);
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();

		OrderHolder cart = (OrderHolder) pRequest.resolveName("/atg/commerce/ShoppingCart");
		BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
		ShipMethodsResponseVORest shipMethodsResponse = null;
		try {
			// setting the required parameters in the request before invoking the droplet
			pRequest.setParameter(BBBCoreConstants.ORDER, order);
			pRequest.setParameter(CURRENT_ZIP, sddShipZip);
			pRequest.setParameter(CHOOSE_SDD_OPTION, chooseSddOption);
			
			logDebug("ShippingMethodManager.getSddShippingMethodFromCurrentOrder; GetSddShippingMethodsDroplet Call starts");
			getSddShippingMethodsDroplet().service(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse());
			logDebug("ShippingMethodManager.getShippingMethodsFromCurrentOrder; GetSddShippingMethodsDroplet Call end");
			shipMethodsResponse = populateShipMethodsResponse(pRequest, SINGLE_SHIPPING_MODE);

		} catch (ServletException e) {
			logError("ShippingMethodManager.getSddShippingMethodFromCurrentOrder, Error occurred while fetching data: " + BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1002);
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1002, "Error occurred while fetching data");
		} catch (IOException e) {
			logError("ShippingMethodManager.getSddShippingMethodFromCurrentOrder,Error occurred while fetching data: " + BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1003);
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1003, "Error occurred while fetching data");
		}
		logDebug("Exiting method ShippingMethodManager.getShippingMethodsFromCurrentOrder");
		return shipMethodsResponse;

	}

	/**
	 * populate ShipMethodsResponse from the request
	 * 
	 * @param shipMethodVOList
	 * @param order
	 * @param hardgoodShippingGroupList
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 * @throws PricingException
	 */
	@SuppressWarnings("unchecked")
	public ShipMethodsResponseVORest populateShipMethodsResponse(DynamoHttpServletRequest pRequest, String operationMode) throws BBBBusinessException, BBBSystemException {

		ShipMethodsResponseVORest shipMethodsResponse = new ShipMethodsResponseVORest();
		ShipMethodsVORest shipMethodsVORest = new ShipMethodsVORest();
		List<ShipMethodsVORest> shipMethodRestList = new ArrayList<ShipMethodsVORest>();

		shipMethodsResponse.setShippingOperation(operationMode);

		if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(operationMode, SINGLE_SHIPPING_MODE)) {
			List<ShipMethodVO> shipMethodVOList = (List<ShipMethodVO>) (pRequest.getObjectParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST));
			for(ShipMethodVO shipMethod: shipMethodVOList )
			{
				try {
					RepositoryItem shippingDuration = getCatalogTools().getShippingDuration(shipMethod.getShipMethodId(), SiteContextManager.getCurrentSiteId());
					if(shippingDuration!=null ){
						Integer maxDaysToShip=(Integer)shippingDuration.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME);
						Integer minDaysToShip=(Integer)shippingDuration.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME);
						
						shipMethod.setDaysToShip(minDaysToShip.toString()+"-"+maxDaysToShip.toString()+" business days");
						}
				} catch (BBBBusinessException e) {
					logError("ShippingMethodManager.populateShipMethodsResponse, Error occurred while fetching data: " + BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1002);
					throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1002, "Error occurred while fetching data");
				} catch (BBBSystemException e) {
					logError("ShippingMethodManager.populateShipMethodsResponse, Error occurred while fetching data: " + BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1002);
					throw new BBBSystemException(BBBCoreErrorConstants.ERROR_SHIPPING_METHODS_1002, "Error occurred while fetching data");
				}
			}
			shipMethodsVORest.setShipMethodList(shipMethodVOList);
			shipMethodsVORest.setSddEligibilityStatus(pRequest.getParameter(BBBCoreConstants.SDD_ELIGIBLITY_STATUS));
			shipMethodsVORest.setSddOptionState(pRequest.getParameter(BBBCoreConstants.SDD_OPTION_ENABLED));
			if(pRequest.getParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD) != null){
				shipMethodsVORest.setPreSelectedShipMethod(pRequest.getParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD));
			}
			shipMethodRestList.add(shipMethodsVORest);

		} else if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(operationMode, MULTI_SHIPPING_MODE)) {

			HashMap<String, List<ShipMethodVO>> skuMethodsMap = (HashMap<String, List<ShipMethodVO>>) pRequest
					.getObjectParameter(BBBCoreConstants.SKU_MEHOD_MAP);

			if (null !=skuMethodsMap && !skuMethodsMap.isEmpty()) {
				for (Map.Entry<String, List<ShipMethodVO>> shipMethodsSkuMap : skuMethodsMap.entrySet()) {
					shipMethodsVORest = new ShipMethodsVORest();
					shipMethodsVORest.setSkuId(shipMethodsSkuMap.getKey());
					shipMethodsVORest.setShipMethodList(shipMethodsSkuMap.getValue());
					shipMethodRestList.add(shipMethodsVORest);
				}
			}

		}

		shipMethodsResponse.setShipMethodsVORest(shipMethodRestList);
		if(pRequest.getObjectParameter("regionVO") != null){
			shipMethodsResponse.setRegionVO((RegionVO) pRequest.getObjectParameter("regionVO"));
		}
		return shipMethodsResponse;
	}
		
}

