package com.bbb.commerce.checkout.manager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.browse.vo.SddZipcodeVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBInventoryManagerImpl;
import com.bbb.commerce.inventory.BopusInventoryService;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.sdd.vo.CustomerSddVOReq;
import com.bbb.sdd.vo.PackageSddVOReq;
import com.bbb.sdd.vo.SddRequestVO;
import com.bbb.sdd.vo.response.SddVOResponse;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;


// TODO: Auto-generated Javadoc
/**
 * The Class BBBSameDayDeliveryManager.
 *
 * @author sghosh
 * 
 * This manager is contains  all method which are related to same day delivery feature 
 */


public class BBBSameDayDeliveryManager extends BBBGenericService {
	
	/** The bbb catalog tools. */
	private BBBCatalogToolsImpl bbbCatalogTools;
	
	/** The home page url. */
	private String homePageURL;
	
	/** The order repository. */
	private MutableRepository orderRepository;
	
	/** The http call invoker. */
	private HTTPCallInvoker httpCallInvoker;
	
	/** The bopus inventory service. */
	private BopusInventoryService bopusInventoryService;
	
	/** The search store manager. */
	private SearchStoreManager searchStoreManager;
	
	/** The inventory manager impl. */
	private BBBInventoryManagerImpl inventoryManagerImpl;
	
	/** The dummy inventory flag. */
	private String dummyInventoryFlag;
	
	/** The inventory flag. */
	private String inventoryCheckFlagDefault;

	/**
	 * The defaultStoreOpeningTime.
	 */
	private String defaultStoreOpeningTime;
	

	private String sddAttributeCookieAge;
	
	private String sddModalCookieMaxAge;
	/**
	 * This method fetches the regions on the basis of zipcode and populates the current and landing zipcode VO.
	 *
	 * @param sessionBean the session bean
	 * @param pRequest the request
	 * @param customerZip the customer zip
	 * @param populateLandingVO the populate landing vo
	 * @param populateCurrentVO the populate current vo
	 * @param populateShippingVO the populate shipping vo
	 * @return the region vo
	 */
	public RegionVO populateDataInVO(BBBSessionBean sessionBean, DynamoHttpServletRequest pRequest, 
			String customerZip, boolean populateLandingVO , boolean populateCurrentVO, boolean populateShippingVO) {
		
		RegionVO regionVO = null;
		
		try {
			logDebug("BBBSameDayDeliveryManager.populateDataInVO.start with zip: " + customerZip);
			 regionVO = getBbbCatalogTools().getRegionDataFromZip(customerZip);
			// checking if the zipcode populated in session is not same with this customerZip then  populate the session
			if(sessionBean.getCurrentZipcodeVO() == null || !StringUtils.isBlank(sessionBean.getCurrentZipcodeVO().getZipcode()) && 
					!sessionBean.getCurrentZipcodeVO().getZipcode().equals(customerZip)){
				
				if(populateLandingVO){
					populateLandingZipcodeVO(sessionBean, pRequest, regionVO, customerZip);
				}
				if(populateCurrentVO) {
					populateCurrentZipcodeVO(sessionBean, regionVO, customerZip);
				}
			
			}
			if(populateShippingVO) {
				populateShippingZipcodeVO(sessionBean, regionVO, customerZip);
			}
			logDebug("BBBSameDayDeliveryManager.populateDataInVO.end");
			
			
		} catch (BBBBusinessException | BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Exception while checking for SDD eligibility" ), e);
		}
		
		return regionVO;
	}
	
	/**
	 * This method populates the landing zipcode VO in the session.
	 *
	 * @param sessionBean the session bean
	 * @param pRequest the request
	 * @param regionVO the region vo
	 * @param customerZip the customer zip
	 */
	public void populateLandingZipcodeVO(BBBSessionBean sessionBean,  DynamoHttpServletRequest pRequest, RegionVO regionVO, String customerZip) {
		logDebug("BBBSameDayDeliveryManager.populateLandingZipcodeVO.start with zip: " + customerZip);
		SddZipcodeVO landingZipcodeVO = new SddZipcodeVO();
		landingZipcodeVO.setZipcode(customerZip);
		if(regionVO != null){
			landingZipcodeVO.setSddEligibility(true);
		} else{
			landingZipcodeVO.setSddEligibility(false);
		}
		sessionBean.setLandingZipcodeVO(landingZipcodeVO);
		logDebug("BBBSameDayDeliveryManager.populateLandingZipcodeVO.end param: landingZipcodeVO" + landingZipcodeVO.toString());
	}
         
     /**
      * This method checks SDD eligibility for changed zipcode and populates 
      * the current/landing zipcode VO in the session. This is called from rest.
      *
      * @param customerZip the customer zip
      * @param populateLandingVO the populate landing vo
      * @return the region vo
      */
	public RegionVO checkAndpopulateRegionDataInVO(String customerZip, boolean populateLandingVO) {
		logDebug("BBBSameDayDeliveryManager.populateLandingZipcodeVO.start with zip: " + customerZip);
		
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		BBBSessionBean sessionBean = ((BBBSessionBean)(request.resolveName(BBBCoreConstants.SESSION_BEAN)));
		return populateDataInVO(sessionBean, request, customerZip, populateLandingVO, true, false);
	}
	
	
	/**
	 * This method populates the current zipcode VO in the session.
	 *
	 * @param sessionBean the session bean
	 * @param regionVO the region vo
	 * @param customerZip the customer zip
	 */
	public void populateCurrentZipcodeVO(BBBSessionBean sessionBean, RegionVO regionVO, String customerZip) {
		logDebug("BBBSameDayDeliveryManager.populateCurrentZipcodeVO.start with zip: " + customerZip);
		SddZipcodeVO currentZipcodeVO;
		boolean isOldAndNewMarketSame = false;
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		String sddAttrCookieValue = BBBUtility.getCookie(request, BBBCoreConstants.SDD_ATTRIBUTE);
		if(regionVO != null){
			if(sessionBean.getCurrentZipcodeVO() != null){
				currentZipcodeVO = sessionBean.getCurrentZipcodeVO();
			} else{
				currentZipcodeVO = new SddZipcodeVO();
			}
			// checking if the new market is not same as the market in session then only repopulate the VO
			if(StringUtils.isBlank(currentZipcodeVO.getRegionId()) || !currentZipcodeVO.getRegionId().equals(regionVO.getRegionId())){
				currentZipcodeVO.setRegionId(regionVO.getRegionId());
				currentZipcodeVO.setPromoAttId(regionVO.getPromoAttId());
				currentZipcodeVO.setMinShipFee(regionVO.getMinShipFee());
				currentZipcodeVO.setDisplayCutoffTime(regionVO.getDisplayCutoffTime());
				currentZipcodeVO.setDisplayGetByTime(regionVO.getDisplayGetByTime());
			}else{
				isOldAndNewMarketSame = true;
			}
			currentZipcodeVO.setSddEligibility(true);
			updateSddAttributeCookie(sddAttrCookieValue,response,regionVO.getPromoAttId(),request);
			

		} else if(StringUtils.isNotEmpty(sddAttrCookieValue)){
			currentZipcodeVO = new SddZipcodeVO();
			currentZipcodeVO.setSddEligibility(false);
			deleteSddAttrCookie(response);
		}
		else{
			currentZipcodeVO = new SddZipcodeVO();
			currentZipcodeVO.setSddEligibility(false);
		}
		// if markets are different then we will reset sdd store id
		if(!isOldAndNewMarketSame){
			sessionBean.setSddStoreId(BBBCoreConstants.BLANK);
		}
		currentZipcodeVO.setZipcode(customerZip);
		sessionBean.setCurrentZipcodeVO(currentZipcodeVO);
		logDebug("BBBSameDayDeliveryManager.populateCurrentZipcodeVO.end param : currentZipcodeVO" + currentZipcodeVO.toString());
	}
	/**
	 * This method updates the sddAttribute cookie.
	 *
	 * @param sddAttrCookieValue the sddAttrCookieValue
	 * @param isAkamiOn the risAkamiOn
	 * @param response DynamoHttpServletResponse response
	 *  @param promoAttId the region promoAttId
	 */
	private void updateSddAttributeCookie(String sddAttrCookieValue, DynamoHttpServletResponse response,String promoAttId, DynamoHttpServletRequest request) {
		logDebug("Entering BBBSameDayDeliveryManager.updateSddAttributeCookie with params:: sddAttrCookieValue: "+ sddAttrCookieValue + " promoAttId: " + promoAttId);
		boolean isAkamiOn = false;
		String akamiOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.HOME_PAGE_CACHING_KEY);
		if (!BBBUtility.isEmpty(akamiOn)) {
			isAkamiOn = Boolean.valueOf(akamiOn);
			logDebug("Akamai Caching config key value:" + isAkamiOn);
		}
		if (StringUtils.isEmpty(sddAttrCookieValue) && isAkamiOn) {
			final Cookie newSddAttrCookie = new Cookie(BBBCoreConstants.SDD_ATTRIBUTE, promoAttId);
			newSddAttrCookie.setMaxAge(Integer.parseInt(getSddAttributeCookieAge()));
			newSddAttrCookie.setPath(BBBCoreConstants.SLASH);
			BBBUtility.addCookie(response, newSddAttrCookie, false);
			logDebug("Sdd attribute cookie created with sddAttribute: " + promoAttId);
		} else if (isAkamiOn) {
			deleteSddAttrCookie(response);
			final Cookie newSddAttrCookie = new Cookie(BBBCoreConstants.SDD_ATTRIBUTE, promoAttId);
			newSddAttrCookie.setMaxAge(Integer.parseInt(getSddAttributeCookieAge()));
			newSddAttrCookie.setPath(BBBCoreConstants.SLASH);
			BBBUtility.addCookie(response, newSddAttrCookie, false);
			logDebug("Sdd attribute cookie updated with sddAttribute: " + promoAttId);
		} else {
			deleteSddAttrCookie(response);
			logDebug("Sdd attribute cookie removed");
		}
		logDebug("Exiting BBBSameDayDeliveryManager.updateSddAttributeCookie with params:: sddAttrCookieValue: "+ sddAttrCookieValue + " promoAttId: " + promoAttId);

	}
	
	
	/**
	 * This method deletes the existing sddAttribute cookie.
	 *
	 * @param DynamoHttpServletRequest request
	 * @param DynamoHttpServletResponse pResponse
	 */
	public void deleteSddAttrCookie(DynamoHttpServletResponse pResponse) {
		Cookie sddCookie = new Cookie(BBBCoreConstants.SDD_ATTRIBUTE, BBBCoreConstants.BLANK);
		sddCookie.setPath(BBBCoreConstants.SLASH);
		sddCookie.setMaxAge(0);
		BBBUtility.addCookie(pResponse, sddCookie, false);
		logDebug("Sdd attribute cookie removed");
		
	}

	/**
	 * This method populates the current zipcode VO in the session.
	 *
	 * @param sessionBean the session bean
	 * @param regionVO the region vo
	 * @param customerZip the customer zip
	 */
	public void populateShippingZipcodeVO(BBBSessionBean sessionBean,
			RegionVO regionVO, String customerZip) {
		logDebug("BBBSameDayDeliveryManager.populateShippingZipcodeVO.start with zip: "
				+ customerZip);
		SddZipcodeVO shippingZipcodeVO = new SddZipcodeVO();
		
		if (regionVO != null) {
			
			shippingZipcodeVO.setDisplayCutoffTime(regionVO.getDisplayCutoffTime());
			shippingZipcodeVO.setDisplayGetByTime(regionVO.getDisplayGetByTime());
			shippingZipcodeVO.setRegionId(regionVO.getRegionId());
			shippingZipcodeVO.setPromoAttId(regionVO.getPromoAttId());
			shippingZipcodeVO.setSddEligibility(true);
			
			//populate current zipcode VO only when shipping zipcode is of pilot region
			populateCurrentZipcodeVO(sessionBean, regionVO, customerZip);
		}else{
			shippingZipcodeVO.setSddEligibility(false);
		}

		shippingZipcodeVO.setZipcode(customerZip);
		sessionBean.setShippingZipcodeVO(shippingZipcodeVO);
		logDebug("BBBSameDayDeliveryManager.populateShippingZipcodeVO.end param : shippingZipcodeVO"
				+ shippingZipcodeVO.toString());
	}
	
	
	
	/**
	 * This method is called from the shipping page whenever zipcode is changed in the shipping address. It checks for market and item eligibility for SDD
	 * and returns the corresponding messages.
	 *
	 * @param pRequest the request
	 * @param order the order
	 * @param regionVO 
	 * @param shipMethodVOList the ship method vo list
	 * @param shippingZip the shipping zip
	 * @return the string
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 * @throws RepositoryException the repository exception
	 */
	public String checkForSDDEligibility(DynamoHttpServletRequest pRequest, BBBOrder order, RegionVO regionVO, List<ShipMethodVO> shipMethodVOList, String shippingZip) 
			throws BBBBusinessException, BBBSystemException, RepositoryException {
		logDebug("BBBSameDayDeliveryManager.checkForSDDEligibility starts with zip: " + shippingZip);
		final String DISPLAY_SDD_DISABLED="displaySDDAlways";
		//check for hyphen shippingZip
		if(regionVO != null){
			//set the session to true
			
			pRequest.getSession().setAttribute(DISPLAY_SDD_DISABLED, BBBCoreConstants.TRUE);
			// Next we will check for item eligibility
			@SuppressWarnings("unchecked")
			List<CommerceItem> items = (List<CommerceItem>) order.getCommerceItems();
	        List<String> skuIdList = new ArrayList<String>();
			for (CommerceItem commerceItem : items) {
				if (commerceItem instanceof BBBCommerceItem){
					String skuId = commerceItem.getCatalogRefId();
					skuIdList.add(skuId);
					RepositoryItem skuItem = (RepositoryItem) (commerceItem.getAuxiliaryData().getCatalogRef());
					// fetching the sku detail vo to check for item eligibility
					boolean hasSddAttribute = getBbbCatalogTools().hasSDDAttribute(SiteContextManager.getCurrentSiteId(), skuItem, regionVO.getPromoAttId(), true);
					logDebug("BBBSameDayDeliveryManager.checkForSDDEligibility.end - Eligibility for sku : " + skuId + " is: " + hasSddAttribute);
					if(!hasSddAttribute){
						logDebug("BBBSameDayDeliveryManager.checkForSDDEligibility.end - Market found but items in cart is not sdd eligible");
						return BBBCoreConstants.ITEM_INELIGIBLE;
					}
				}
			}
			logDebug("BBBSameDayDeliveryManager.checkForSDDEligibility.end - Market found and all items in cart are sdd eligible");
			return BBBCoreConstants.ITEM_ELIGIBLE;
		} else{
			logDebug("BBBSameDayDeliveryManager.checkForSDDEligibility.end - No market found for zip");
			return BBBCoreConstants.MARKET_INELIGIBLE;
		}
	}

	
	
	/**
	 * This method is called from the shipping page whenever Same Day Delivery shipping method is selected. It checks for items inventory in the market and returns 
	 * the corresponding messages to be displayed on shipping page
	 *
	 * @param regionVO the region vo
	 * @param order the order
	 * @return the string
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public String checkSBCInventoryForSdd(Set<String> storeIds,
			BBBOrder order, boolean isSDDCommitOrderFlow) throws BBBBusinessException, BBBSystemException {
		logDebug("BBBSameDayDeliveryManager.chooseSddOption starts");
		String eligibilityStatus = BBBCoreConstants.ITEM_UNAVAILABLE;
		// Fetching the store id list from markets
		List<String> storeIdsList = new ArrayList<String>();
		if(storeIds != null){
			storeIdsList.addAll(storeIds);
			logDebug("storeIdsList obtained from region" + storeIdsList);
		}
		
		//Create sku and quantity map by adding quantity of same sku's in different commerce items
		Map<String, Long> skuIdQuantityMap = getSkuIdQuantityMapFromOrder(order);

		List<String> skuIdList = new ArrayList<String>();
		skuIdList.addAll(skuIdQuantityMap.keySet());
		String storeType = getSearchStoreManager().getStoreType(order.getSiteId());
		List<String> bopusEligiblestoreIdsList = new ArrayList<String>();
		// Here we will fetch the bopus ineligible store id's and will create new store list excluding these store id's before checking for inventory by SBC call
		List<String> bopusInEligibleStore = getBbbCatalogTools().getBopusInEligibleStores(storeType, order.getSiteId());
		if(!BBBUtility.isListEmpty(bopusInEligibleStore)){
			for(String storeId : storeIdsList){
				if(!bopusInEligibleStore.contains(storeId)){
					bopusEligiblestoreIdsList.add(storeId);
				}
			}
		} else{
			bopusEligiblestoreIdsList.addAll(storeIdsList);
		}
		
		//Now we will make SBC call to check for inventory
		Map<String, Map<String, Integer>> skuInventoryInStore = null;
		try {
			// This flag will be removed after SBC real service starts working
			boolean dummyInventoryFlag = true;
			if(!StringUtils.isBlank(getDummyInventoryFlag())){
				dummyInventoryFlag = Boolean.valueOf(getDummyInventoryFlag());
			}
			if(dummyInventoryFlag){
				skuInventoryInStore = new HashMap<String, Map<String,Integer>>();
				updateDummyInventoryInMap(skuIdList, bopusEligiblestoreIdsList, skuInventoryInStore);
			} else{
				skuInventoryInStore = getBopusInventoryService().getInventoryForMultiStoreAndSku(skuIdList, bopusEligiblestoreIdsList, false, isSDDCommitOrderFlow);
			}
			if(skuInventoryInStore != null && !skuInventoryInStore.isEmpty()){
				eligibilityStatus = checkInSBCInventory(skuInventoryInStore, skuIdQuantityMap, order.getSiteId());
			}
		} catch (BBBSystemException e) {
			logError("BBBSystemException - SBC call failed" + e.getMessage(), e);
			// we were not able to check for inventory by supply balance call so we will check in local store inventory
			logDebug("Checking inventory in local store repository");
			eligibilityStatus = checkInLocalInventory(skuIdQuantityMap, skuIdList, storeIdsList, order.getSiteId());
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException - Unauthorised/in complete SBC request" + e.getMessage(), e);
		}
		logDebug("BBBSameDayDeliveryManager.chooseSddOption ends with sdd eligibility status message" + eligibilityStatus);
		return eligibilityStatus;
	}

	/**
	 * Gets the sku id quantity map from order.
	 *
	 * @param order the order
	 * @return the sku id quantity map from order
	 */
	public Map<String, Long> getSkuIdQuantityMapFromOrder(
			BBBOrder order) {
		@SuppressWarnings("unchecked")
		List<CommerceItem> items = (List<CommerceItem>) order.getCommerceItems();
		Map<String, Long> skuIdQuantityMap = new HashMap<String, Long>();
		for (CommerceItem commerceItem : items) {
			if (commerceItem instanceof BBBCommerceItem){
				String skuId = commerceItem.getCatalogRefId();
				Long quantity = commerceItem.getQuantity();
				logDebug("skuId" + skuId + " quantity" + quantity);
				if(skuIdQuantityMap.keySet().contains(skuId)){
					skuIdQuantityMap.put(skuId, skuIdQuantityMap.get(skuId) + quantity);
				} else{
					skuIdQuantityMap.put(skuId, quantity);
				}
			}
		}
		return skuIdQuantityMap;
	}

	/**
	 * This method is created to create a map with sku list and store list passed to return dummy inventory values.
	 *
	 * @param skuIdList the sku id list
	 * @param storeIdsList the store ids list
	 * @param skuInventoryInStore the sku inventory in store
	 */
	private void updateDummyInventoryInMap(List<String> skuIdList,
			List<String> storeIdsList, Map<String, Map<String, Integer>> skuInventoryInStore) {
		logDebug("BBBSameDayDeliveryManager.updateDummyInventoryInMap starts with skuIdList" + skuIdList + " and storeIdsList" + storeIdsList);
		Integer inventoryVal = 0;
		if(storeIdsList.size() == 1){
			inventoryVal = 1000;
		}
		for(String storeId : storeIdsList){
			if(!storeIdsList.get(0).equals(storeId)){
				inventoryVal = 1000;
			} 
			Map<String, Integer> skuIdInventoryMap = new HashMap<String, Integer>();
			for(String skuId : skuIdList){
				skuIdInventoryMap.put(skuId, inventoryVal);
			}
			skuInventoryInStore.put(storeId, skuIdInventoryMap);
		}
		logDebug("BBBSameDayDeliveryManager.updateDummyInventoryInMap ends with Map" + skuInventoryInStore);
	}

	/**
	 * This method returns the sdd eligibility status on the basis of values
	 * obtained from the inventory map.
	 *
	 * @param skuInventoryInStore
	 *            the sku inventory in store
	 * @param skuIdQuantityMap
	 *            the sku id quantity map
	 * @param siteId
	 *            the site id
	 * @return the string
	 * @throws BBBSystemException
	 *             the BBB system exception
	 * @throws BBBBusinessException
	 *             the BBB business exception
	 */
	private String checkInSBCInventory(Map<String, Map<String, Integer>> skuInventoryInStore,
			Map<String, Long> skuIdQuantityMap, String siteId) throws BBBSystemException, BBBBusinessException {
		vlogDebug(
				"BBBSameDayDeliveryManager.checkInSBCInventory starts with inventory Map {0}  and skuIdQuantityMap {1}",
				skuInventoryInStore, skuIdQuantityMap);
		for (Map.Entry<String, Map<String, Integer>> skuInventoryInStoreEntry : skuInventoryInStore.entrySet()) {
			String storeId = skuInventoryInStoreEntry.getKey();
			boolean inventoryCheckFlag = Boolean.parseBoolean(getInventoryCheckFlagDefault());
			// we will iterate through all the stores and check if any store has
			// inventory for all the items
			Map<String, Integer> skuInventoryMap = skuInventoryInStore.get(storeId);
			if (skuInventoryMap.isEmpty() || !skuInventoryMap.keySet().containsAll(skuIdQuantityMap.keySet())) {
				vlogDebug(
						"BBBSameDayDeliveryManager.checkInSBCInventory: We were not able to find any store which has inventory for all the items");
				return BBBCoreConstants.ITEM_UNAVAILABLE;
			}

			for (Map.Entry<String, Integer> skuInventoryMapEntry : skuInventoryMap.entrySet()) {
				String skuId = skuInventoryMapEntry.getKey();
				Integer inventoryValue = skuInventoryMap.get(skuId);
				ThresholdVO skuThresholdVO = getBbbCatalogTools().getSkuThreshold(siteId, skuId);
				int inventoryStatus = getInventoryManagerImpl().getInventoryStatus(inventoryValue,
						skuIdQuantityMap.get(skuId), skuThresholdVO, storeId);
				if (inventoryStatus == BBBInventoryManager.AVAILABLE
							|| inventoryStatus == BBBInventoryManager.LIMITED_STOCK) {
						logDebug("inventory Found");
					} else{
						inventoryCheckFlag = false;
						break;
					}
				}

				if(inventoryCheckFlag){
					DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
				BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
				sessionBean.setSddStoreId(storeId);
				/*
				 * Below portion of code is for saving SBC respone into session
				 * to prevent SBC calls in checkout page load.
				 */
				BBBStoreInventoryContainer bbbStoreInventoryContainer = (BBBStoreInventoryContainer) pRequest
						.resolveName(BBBCoreConstants.BBB_STORE_INVENTORY_CONTAINER);
				Map<String, Integer> skuInventorySessionMap = new HashMap<>();
				for (Map.Entry<String, Integer> skuInventoryInSddStoreEntry : skuInventoryMap.entrySet()) {
					String skuId = skuInventoryInSddStoreEntry.getKey();
					skuInventorySessionMap.put(storeId + BBBCoreConstants.PIPE_SYMBOL + skuId,
							skuInventoryMap.get(skuId));
				}
				vlogDebug(
						"BBBSameDayDeliveryManager.checkInSBCInventory: Saved SBC response for current cart into session to prevent SBC calls on page load. - {0}",
						skuInventorySessionMap);
				bbbStoreInventoryContainer.setSddStoreInventoryMap(skuInventorySessionMap);
				vlogDebug(
						"BBBSameDayDeliveryManager.checkInSBCInventory: Ends, We found a store {0} in which all items have inventory. We have also updated the sdd store id in session with value",
						storeId);
				return BBBCoreConstants.SDD_ELIGIBLE;
				}
			}
		/*
		 * By now we know that we have not found a store which has inventory for
		 * all items
		 */
		vlogDebug(
				"BBBSameDayDeliveryManager.checkInSBCInventory: Ends, We were not able to find any store which has inventory for all the items");
		return BBBCoreConstants.ITEM_UNAVAILABLE;
	}
	


	/**
	 * This method returns the sdd eligibility status on the basis of values obtained from the inventory map we got from local store repository
	 *
	 * @param skuIdQuantityMap the sku id quantity map
	 * @param skuIdList the sku id list
	 * @param storeIdsList the store ids list
	 * @param siteId the site id
	 * @return the string
	 */
	private String checkInLocalInventory(Map<String, Long> skuIdQuantityMap,
			List<String> skuIdList, List<String> storeIdsList, String siteId) {
		logDebug("BBBSameDayDeliveryManager.checkInLocalInventory starts with sku id quantity Map" + skuIdQuantityMap);
		try {
			for(String storeId : storeIdsList){
				boolean inventoryCheckFlag = true;
				for(String skuId : skuIdList){
					boolean storeHasInventoryForSku = getSearchStoreManager().checkStoreHasInventoryForSkuId(storeId,
							skuId, skuIdQuantityMap.get(skuId), siteId);
					logDebug("skuId" + skuId + "inventory found is" + storeHasInventoryForSku);
					if (!storeHasInventoryForSku) {
						inventoryCheckFlag = false;
						break;
					}
				}
				if(inventoryCheckFlag){
					DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
					BBBSessionBean sessionBean = ((BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN));
					logDebug("We found a store in which all items have inventory. We have also updated the sdd store id in session with value" +storeId);
					sessionBean.setSddStoreId(storeId);
					return BBBCoreConstants.SDD_ELIGIBLE;
				}
			}
		} catch (BBBBusinessException | BBBSystemException
				| RepositoryException | ServletException | IOException e) {
			logError("Local Inventory call failed", e);
		}
		logDebug("We were not able to find any store in local store repository which has inventory for all the items");
		return BBBCoreConstants.ITEM_UNAVAILABLE;
	}
	
	
	/**
	 * This method populates the request Vo to call deliv API.
	 *
	 * @param pOrder the order
	 * @param packageSddVOList the package sdd vo list
	 * @return the sdd request vo as SddRequestVO
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	private List<PackageSddVOReq> populateDelivPackages(final BBBOrder pOrder, List<PackageSddVOReq> packageSddVOList) throws BBBBusinessException, BBBSystemException{
		
		logDebug("BBBSameDayDeliveryManager.populateDelivPackages: Starts for order : "+pOrder.getId());
		//create new package list
		final List<ShippingGroup> shippingList = pOrder.getShippingGroups();
		CommerceItem commerceItem = null;
		for(int index = 0; index < shippingList.size(); index++){
				final ShippingGroup shippingGroup = shippingList.get(index);
				if(shippingGroup instanceof BBBHardGoodShippingGroup){
					final List<ShippingGroupCommerceItemRelationship> commerceItemRelationships = shippingGroup.getCommerceItemRelationships();
					for(final ShippingGroupCommerceItemRelationship commerceItemRelation : commerceItemRelationships){
						commerceItem = commerceItemRelation.getCommerceItem();
						logDebug("Creating packages for Commerce item "+commerceItem.getId());
						if(commerceItem instanceof BBBCommerceItem) {
							long quantity = 0;
							double itemPrice = 0.0;
							String skuId= commerceItem.getCatalogRefId();
							SKUDetailVO skuVo = getBbbCatalogTools().getMinimalSku(skuId);
							String displayName = skuVo.getDisplayName();
							logDebug("Display name of sku is:"+ skuId + " Display name as : " + displayName);
							final List<DetailedItemPriceInfo> priceBeans= commerceItem.getPriceInfo().getCurrentPriceDetailsForRange(commerceItemRelation.getRange());
							for(final DetailedItemPriceInfo unitPriceBean : priceBeans) {
								quantity = unitPriceBean.getQuantity();
								
								itemPrice = unitPriceBean.getDetailedUnitPrice();
								
								for(int i=1; i<= quantity; i++){
									PackageSddVOReq packageSddVO = new PackageSddVOReq();
									packageSddVO.setName(displayName);
									packageSddVO.setSKU(skuId);
									packageSddVO.setPrice(String.valueOf(itemPrice));
									logDebug("Set Package VO in the list " + packageSddVO.toString());
									packageSddVOList.add(packageSddVO);
								}
							}
						}
					}
				}
		}
		
		logDebug("BBBSameDayDeliveryManager.populateDelivPackages: Ends with package List as  " + packageSddVOList);
		return packageSddVOList;
	}
	
	
	
	/**
	 * Populate deliv request vo.
	 *
	 * @param order the order
	 * @return the sdd request vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public SddRequestVO populateDelivRequestVO(BBBOrderImpl order) throws BBBSystemException, BBBBusinessException{
		
		logDebug("BBBSameDayDeliveryManager.populateSddRequestVO: Starts fetching order: "+order.getId());
		SddRequestVO sddvo = new SddRequestVO();
		CustomerSddVOReq customerSddVO= new CustomerSddVOReq();
		List<PackageSddVOReq> packageSddVOList = new ArrayList<PackageSddVOReq>();
		
        populateDelivCustomerReqVO(order, customerSddVO);
        
        populateDelivPackages(order, packageSddVOList);
       
		String readyByTime = getReadyByTime();
		sddvo.setReady_by(readyByTime);
		sddvo.setOrder_reference(order.getOnlineOrderNumber());
		sddvo.setStore_id_alias(((BBBHardGoodShippingGroup) order.getShippingGroups().get(0))
				.getSddStoreId());
		
		sddvo.setCustomer(customerSddVO);
		sddvo.setPackages(packageSddVOList);
		logDebug("BBBSameDayDeliveryManager.populateSddRequestVO: Ends with request VO as - " + sddvo.toString());
		
		return (sddvo);
    }

	/**
	 * Populate deliv customer req vo.
	 *
	 * @param order the order
	 * @param customerSddVO the customer sdd vo
	 */
	private void populateDelivCustomerReqVO(BBBOrderImpl order,
			CustomerSddVOReq customerSddVO) {
		logDebug("BBBSameDayDeliveryManager.populateDelivCustomerReqVO: Starts ");
		if( null != order.getShippingGroups()){
	        ShippingGroup shippingGroup = (ShippingGroup) order.getShippingGroups().get(0);
			if(null != shippingGroup && shippingGroup instanceof HardgoodShippingGroup)
			{       logDebug("fetching Shipping group: "+shippingGroup.getId());
					final HardgoodShippingGroup hgsg = (HardgoodShippingGroup) shippingGroup;
					final Address shippingAddress = hgsg.getShippingAddress();
					if ((null != shippingAddress && !BBBUtility.isEmpty(shippingAddress.getAddress1())))
					{
						BBBAddress address = (BBBAddress) shippingAddress;
						logDebug("Fetching shipping address details: "+address);
						customerSddVO.setFirst_name(address.getFirstName());
						customerSddVO.setLast_name(address.getLastName());
						customerSddVO.setEmail(address.getEmail());
						customerSddVO.setPhone(address.getMobileNumber());
						customerSddVO.setAddress_zipcode(BBBUtility.hyphenExcludedZip(address.getPostalCode()));
						customerSddVO.setAddress_line_1(address.getAddress1());
						customerSddVO.setAddress_city(address.getCity());
						customerSddVO.setAddress_state(address.getState());
						customerSddVO.setBusiness_name(address.getCompanyName());
					}
			} 
        }
		if( null != order.getBillingAddress()){
			final BBBAddress billingAddress = (BBBAddress)order.getBillingAddress();
			logDebug("Fetching billing address details to set email and phone number: "+billingAddress);
			if(!BBBUtility.isEmpty(order.getBillingAddress().getEmail())){
					customerSddVO.setEmail(billingAddress.getEmail());
			}
			
			if(!BBBUtility.isEmpty(order.getBillingAddress().getMobileNumber())){
				customerSddVO.setPhone(billingAddress.getMobileNumber());
			}else if(!BBBUtility.isEmpty(order.getBillingAddress().getPhoneNumber())){
				customerSddVO.setPhone(billingAddress.getPhoneNumber());
			}
		}
		logDebug("BBBSameDayDeliveryManager.populateDelivCustomerReqVO: Ends with customer Req VO as " + customerSddVO.toString());
	}
       
	
	
	/**
	 * This method invokes the  convertVOToJson method in HttpCallInvoker class
	 *  
	 *
	 * @param sddRequestVO the VO to be parsed into json
	 * @param order the order
	 * @return the sdd vo response
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws RepositoryException the repository exception
	 */
	
	public SddVOResponse invokeDelivAPI(SddRequestVO sddRequestVO,BBBOrderImpl order) throws BBBSystemException, BBBBusinessException, IOException, RepositoryException
	{  
		logDebug("BBBSameDayDeliveryManager.invokeDelivAPI: Starts with Request as : " + sddRequestVO.toString());
		SddVOResponse sddVOResponse = null;
		String postUrl = BBBCoreConstants.BLANK;
		HashMap<String, String> headerParam = new HashMap<String, String>();
		List<String> postUrlList = getBbbCatalogTools().getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.DELIV_URL_KEY);
		List<String> apiKeyList = getBbbCatalogTools().getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.DELIV_API_KEY);
		List<String> delivContentTypeList = getBbbCatalogTools().getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.DELIV_CONTENT_TYPE_KEY);
		 
		if(!BBBUtility.isListEmpty(postUrlList)){
			postUrl = postUrlList.get(0);
		}
        
       
		if(null != apiKeyList && !(apiKeyList.isEmpty())){
			headerParam.put(BBBCoreConstants.API_KEY,apiKeyList.get(0)); //Api-Key
		}
		if(null != delivContentTypeList && !(delivContentTypeList.isEmpty())){
			headerParam.put(BBBCoreConstants.HTTP_INVKR_CNT_TYPE,delivContentTypeList.get(0));
		}
			
		sddVOResponse =  getHttpCallInvoker().invokeWithJSONInput(SddVOResponse.class, sddRequestVO, postUrl, headerParam);
		
		if(null != sddVOResponse){
			logDebug("BBBSameDayDeliveryManager.invokeDelivAPI: Ends with Response paramerets as: "+sddVOResponse.toString());
		}else{
			logDebug("BBBSameDayDeliveryManager.invokeDelivAPI: Ends with Response paramerets as null");
		}
		
		return sddVOResponse;
	}
	
	
	

	/**
	 * @return defaultStoreOpeningTime.
	 */
	public String getDefaultStoreOpeningTime() {
		return defaultStoreOpeningTime;
	}

	/**
	 * @param defaultStoreOpeningTime
	 */
	public void setDefaultStoreOpeningTime(String defaultStoreOpeningTime) {
		this.defaultStoreOpeningTime = defaultStoreOpeningTime;
	}

	/**
	 * Gets the ready by time.
	 *
	 * @return the ready by time
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public String getReadyByTime() throws BBBBusinessException, BBBSystemException{
	  
		logDebug("BBBSameDayDeliveryManager.getReadyByTime :: Ready by Time in Deliv Starts");
		//Time format to send ready by time to Deliv
		final SimpleDateFormat simpleDateFormatDeliv = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		int storeBufferTime=2;
		TimeZone delivTimeZone = TimeZone.getTimeZone("GMT");	
		simpleDateFormatDeliv.setTimeZone(delivTimeZone);
		
		String readyByTime = BBBCoreConstants.BLANK;
		String regionId = null ;
		BBBSessionBean sessionBean = null;
		
		//get Region VO and get cutoff time and region time zone from region VO
		if(null !=ServletUtil.getCurrentRequest()){
	    	 sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
		}
		
		if(null != sessionBean && null != sessionBean.getShippingZipcodeVO()){
		    regionId = sessionBean.getShippingZipcodeVO().getRegionId();
		    RegionVO rvo = null;
			rvo = getBbbCatalogTools().getAllRegionDetails(regionId);
			
		    String timeZone = rvo.getTimeZone();
		    Date cutOffTime = rvo.getCutOffTimeRegion();	
		    
		    //setting format for local/region timezone
		    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");	
			TimeZone regionTimeZone = TimeZone.getTimeZone(timeZone);	
			simpleDateFormat.setTimeZone(regionTimeZone);	
			
			//Getting store Open time and set this as region time zone
			String storeOpenTimeString = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS,"storeOpensAt");	
			
			Date storeOpenTime = new Date();	
			try {	
				storeOpenTime = simpleDateFormat.parse(storeOpenTimeString);
			} catch (ParseException e) {	
				
				logError("Error parsing the Store opening Time. So setting Store opening Time as its default value ");
				
				try {
					storeOpenTimeString = getDefaultStoreOpeningTime();
					storeOpenTime = simpleDateFormat.parse(storeOpenTimeString);
				} catch (ParseException e1) {
					logError("Error parsing the default Store opening Time.");
					return BBBCoreConstants.BLANK;
				}
				
			}	
			
			//Get current local time
			final Calendar calendar = Calendar.getInstance();	
			String currentLocalTimeString = simpleDateFormat.format(calendar.getTime());	
			
			//adding 2 hours in current time for calculations further
			List<String> storeBufferTimeList = getBbbCatalogTools().getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, "storeBufferTime");
				if(null != storeBufferTimeList && !(storeBufferTimeList.isEmpty())){
					storeBufferTime = Integer.parseInt(storeBufferTimeList.get(0));
					
				}
			logDebug("Buffer time for store is: "+storeBufferTime);		
			//calendar.add(Calendar.HOUR, 2);	
			calendar.add(Calendar.HOUR, storeBufferTime);
			Calendar localTimeCal = Calendar.getInstance();
			localTimeCal.setTimeZone(regionTimeZone);
			localTimeCal.setTime(calendar.getTime());
			
			//Get current local/region time after ading 2 hours and also get cut off local/region time
			String currentNewLocalTimeString = simpleDateFormat.format(calendar.getTime());	
			String cutOffLocalTimeString = simpleDateFormat.format(cutOffTime);	
				
			logDebug("current Local Time" + currentLocalTimeString);	
			logDebug("current Local Time +storeBufferTime hours is " + currentNewLocalTimeString);	
			logDebug("Cut off Local Time" + cutOffLocalTimeString);	
			logDebug("Store Open Local Time" + storeOpenTimeString);	
			
			/* If current local Time +2 is less than store opening time, then ready by time should be the store opening time */
			if(currentNewLocalTimeString.compareTo(storeOpenTimeString) < 0){	
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(regionTimeZone);
				cal.setTime(storeOpenTime);
				cal.set(Calendar.YEAR,localTimeCal.get(Calendar.YEAR));
				cal.set(Calendar.MONTH,localTimeCal.get(Calendar.MONTH));
				cal.set(Calendar.DAY_OF_MONTH,localTimeCal.get(Calendar.DAY_OF_MONTH));
				
				logDebug("Current local Time +2 is less than store opening time, "
						+ "then ready by time should be the store opening time : " + 
						cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " " + 
							cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + " " + cal.getTimeZone());
				readyByTime = simpleDateFormatDeliv.format(cal.getTime());
				
			}
			/* If current local Time +2 is greater than store opening time and 
			 * current local Time < cut off local time, then ready by time should be current local Time +2 */
			else if(currentLocalTimeString.compareTo(cutOffLocalTimeString) <= 0){	
				logDebug("Current local Time +2 is greater than store opening time and"
						+ "current local Time < cut off local time, then ready by time should be current local Time + 2" + 
						calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) + " " + 
							calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " " + calendar.getTimeZone());
				readyByTime = simpleDateFormatDeliv.format(calendar.getTime());
				
			}
			/* If current local Time +2 is greater than store opening time and 
			 * current local Time is greater than cut off local time, then ready by time should be store opening time
			 * of next day */
			else{	
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(regionTimeZone);
				cal.setTime(storeOpenTime);
				cal.set(Calendar.YEAR,localTimeCal.get(Calendar.YEAR));
				cal.set(Calendar.MONTH,localTimeCal.get(Calendar.MONTH));
				cal.set(Calendar.DAY_OF_MONTH,localTimeCal.get(Calendar.DAY_OF_MONTH));
				
				//adding a day more to set time for next day
				cal.add(Calendar.DAY_OF_MONTH, 1);
				logDebug("current local Time +2 is greater than store opening time and "
						+ "current local Time is greater than cut off local time, then ready by time should be store opening time of next day" + 
						cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " " + 
							cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + " " + cal.getTimeZone());
				readyByTime = simpleDateFormatDeliv.format(cal.getTime());
			}	
			
		}else{
			logDebug("Session is blank or region is not set. so returning ready by Time as BLANK");
		}
		logDebug("BBBSameDayDeliveryManager.getReadyByTime :: Ready by Time in Deliv TimeZone is ::" + readyByTime);	
		return readyByTime;
	}
		    	
		


	/**
	 * Update tracking info.
	 *
	 * @param order the order
	 * @param trackingNumber the tracking number
	 * @param qty the qty
	 */
	public void updateTrackingInfo(BBBOrderImpl order, String trackingNumber, int qty){
		vlogDebug("BBBSameDayDeliveryManager.updateTrackingInfo starts with tracking numnber : {0} and order as : {1}", trackingNumber, order.getId());
		Calendar cal = Calendar.getInstance();
		java.util.Date shipDate2  = cal.getTime();
		MutableRepositoryItem trackingInfoItem = null;
		Set<RepositoryItem> trackingList = new HashSet<RepositoryItem>();
		try {
			trackingInfoItem = this.getOrderRepository().createItem(BBBCoreConstants.TRACKING_INFO_ITEM_DESCRIPTOR);
			if(null != trackingInfoItem){
				trackingInfoItem.setPropertyValue(BBBCoreConstants.CARRIER_CODE_TRACKING_INFO_PROPERTY_NAME, "Deliv");
				trackingInfoItem.setPropertyValue(BBBCoreConstants.TRACKING_NUMBER_TRACKING_INFO_PROPERTY_NAME, trackingNumber);
				trackingInfoItem.setPropertyValue(BBBCoreConstants.TRACKING_URL_TRACKING_INFO_PROPERTY_NAME, null); 
				trackingInfoItem.setPropertyValue("shipmentQty", qty);
				trackingInfoItem.setPropertyValue(BBBCoreConstants.ACTUAL_SHIPDATE_TRACKING_INFO_PROPERTY_NAME, shipDate2);
				logDebug("adding trackingInfoItem to OrderRepository : " + trackingInfoItem.getRepositoryId());
				this.getOrderRepository().addItem(trackingInfoItem);
				
				trackingList.add(trackingInfoItem);
				final List<ShippingGroup> shippingList = order.getShippingGroups();
				CommerceItem commerceItem = null;
				for(int index = 0; index < shippingList.size(); index++){
					final ShippingGroup shippingGroup = shippingList.get(index);
					if(shippingGroup instanceof BBBHardGoodShippingGroup){
						
						((BBBHardGoodShippingGroup) shippingGroup).setPropertyValue("shipmentTracking", trackingList);
						vlogDebug("adding trackingInfoItem {0} to Shipping group : {1}" , trackingInfoItem.getRepositoryId(), shippingGroup.getId());
						
						final List<ShippingGroupCommerceItemRelationship> commerceItemRelationships = shippingGroup.getCommerceItemRelationships();
						for(final ShippingGroupCommerceItemRelationship commerceItemRelation : commerceItemRelationships){
							commerceItem = commerceItemRelation.getCommerceItem();
							List<RepositoryItem> trackingItems = new ArrayList<RepositoryItem>();
							trackingItems.add(trackingInfoItem);
							if(commerceItem instanceof BBBCommerceItem) {
								((BBBCommerceItem) commerceItem).setPropertyValue("tracking", trackingItems);
								vlogDebug("adding trackingInfoItem {0} to Commerce item : {1}" , trackingInfoItem.getRepositoryId(), commerceItem.getId());
							}
						}
					}
				}
			}
			
		}catch (RepositoryException e) {
			e.printStackTrace();
		}
		logDebug("BBBSameDayDeliveryManager.updateTrackingInfo ends");	
	}
	
	/**
	 * Gets the http call invoker.
	 *
	 * @return the http call invoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}
	
	
	/**
	 * Sets the http call invoker.
	 *
	 * @param httpCallInvoker the new http call invoker
	 */
	public void setHttpCallInvoker(HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}
	
	
	/**
	 * Gets the order repository.
	 *
	 * @return the order repository
	 */
	public MutableRepository getOrderRepository() {
		return orderRepository;
	}
	
	/**
	 * Sets the order repository.
	 *
	 * @param orderRepository the new order repository
	 */
	public void setOrderRepository(MutableRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
	
	/**
	 * Gets the home page url.
	 *
	 * @return the home page url
	 */
	public String getHomePageURL() {
		return homePageURL;
	}
	
	/**
	 * Sets the home page url.
	 *
	 * @param homePageURL the new home page url
	 */
	public void setHomePageURL(String homePageURL) {
		this.homePageURL = homePageURL;
	}
	
	/**
	 * Gets the bbb catalog tools.
	 *
	 * @return the bbb catalog tools
	 */
	/**
	 * @return
	 */
	public BBBCatalogToolsImpl getBbbCatalogTools() {
		return bbbCatalogTools;
	}
	
	/**
	 * Sets the bbb catalog tools.
	 *
	 * @param bbbCatalogTools the new bbb catalog tools
	 */
	public void setBbbCatalogTools(BBBCatalogToolsImpl bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	/**
	 * Gets the search store manager.
	 *
	 * @return the search store manager
	 */
	public SearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	/**
	 * Sets the search store manager.
	 *
	 * @param searchStoreManager the new search store manager
	 */
	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}

	/**
	 * Gets the bopus inventory service.
	 *
	 * @return the bopus inventory service
	 */
	public BopusInventoryService getBopusInventoryService() {
		return bopusInventoryService;
	}

	/**
	 * Sets the bopus inventory service.
	 *
	 * @param bopusInventoryService the new bopus inventory service
	 */
	public void setBopusInventoryService(BopusInventoryService bopusInventoryService) {
		this.bopusInventoryService = bopusInventoryService;
	}
	
	
	/**
	 * Gets the inventory manager impl.
	 *
	 * @return the inventory manager impl
	 */
	public BBBInventoryManagerImpl getInventoryManagerImpl() {
		return inventoryManagerImpl;
	}

	/**
	 * Sets the inventory manager impl.
	 *
	 * @param inventoryManagerImpl the new inventory manager impl
	 */
	public void setInventoryManagerImpl(BBBInventoryManagerImpl inventoryManagerImpl) {
		this.inventoryManagerImpl = inventoryManagerImpl;
	}
	
	

	/**
	 * Gets the dummy inventory flag.
	 *
	 * @return the dummy inventory flag
	 */
	public String getDummyInventoryFlag() {
		return dummyInventoryFlag;
	}

	/**
	 * Sets the dummy inventory flag.
	 *
	 * @param dummyInventoryFlag the new dummy inventory flag
	 */
	public void setDummyInventoryFlag(String dummyInventoryFlag) {
		this.dummyInventoryFlag = dummyInventoryFlag;
	}
	
	
	/**
	 * Gets the inventory flag.
	 *
	 * @return the inventory flag
	 */
	public String getInventoryCheckFlagDefault() {
		return inventoryCheckFlagDefault;
	}

	/**
	 * Sets the inventory flag.
	 *
	 * @param InventoryFlag the new inventory flag
	 */
	public void setInventoryCheckFlagDefault(String inventoryCheckFlagDefault) {
		this.inventoryCheckFlagDefault = inventoryCheckFlagDefault;
	}

	public String getSddAttributeCookieAge() {
		String sddAttCookieAge=BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS,BBBCoreConstants.SDD_ATTRIBUTE_COOKIE_AGE);
		if(!BBBUtility.isEmpty(sddAttCookieAge)){
			return sddAttCookieAge;
		}else{
		return sddAttributeCookieAge;
		}
	}
	
	/**
	 * creates sdd Modal Cookie
	 *
	 * @param String domain
	 * @param  DynamoHttpServletResponse response 
	 * @param BBBSessionBean sessionBean 
	 * @param String sddCookieName 
	 * 
	 */
	public void createSDDModalCookie(String domain, DynamoHttpServletResponse response, BBBSessionBean sessionBean, String sddCookieName) {
		logDebug("BBBCreateSDDCookieDroplet Creating new SDD Cookie value");
		final Cookie sddLandingModalCookie = new Cookie(sddCookieName, BBBCoreConstants.TRUE);
		String sddCookieMaxAge = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS,BBBCoreConstants.SDD_COOKIE_MAX_AGE);
		if (StringUtils.isNotEmpty(sddCookieMaxAge)	&& BBBUtility.isNumericOnly(sddCookieMaxAge)) {
			sddLandingModalCookie.setMaxAge(Integer.parseInt(sddCookieMaxAge));
		} else {
			sddLandingModalCookie.setMaxAge(Integer.parseInt(getSddModalCookieMaxAge()));
		}
		sddLandingModalCookie.setPath(BBBCoreConstants.SLASH);
		sddLandingModalCookie.setDomain(domain);
		BBBUtility.addCookie(response, sddLandingModalCookie, false);
		sessionBean.setShowSDD(true);
		
	}
	
	public String getSddModalCookieMaxAge() {
		return sddModalCookieMaxAge;
	}
	

	public void setSddModalCookieMaxAge(String sddModalCookieMaxAge) {
		this.sddModalCookieMaxAge = sddModalCookieMaxAge;
	}
	

	}
	

	
	
