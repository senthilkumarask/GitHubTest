package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.pricing.PricingTools;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * To get shipping methods for single ship and multi ship.
 *
 * @author msiddi
 * @version 1.0
 * @story UC_MDZ_Shipping_Methods
 */

public class GetApplicableShippingMethodsDroplet extends BBBDynamoServlet {

	private static final String CHECK_FOR_INVENTORY = "checkForInventory";

	/** The Constant SDD_ELIGIBLITY_STATUS. */
	private static final String SDD_ELIGIBLITY_STATUS = "sddEligiblityStatus";

	/** The Constant SDD_OPTION_ENABLED. */
	private static final String SDD_OPTION_ENABLED = "sddOptionEnabled";

	/** The Constant REGISTRY_ZIP. */
	private static final String REGISTRY_ZIP = "registryZip";

	/** The Constant CURRENT_ZIP. */
	private static final String CURRENT_ZIP = "currentZip";

	/**
	 * Instance of Shipping manager.
	 */
	private BBBShippingGroupManager mShippingGroupManager;

	/**
	 * Instance of PricingTools.
	 */
	private PricingTools mPricingTools;
	
	/** The sdd ship method id. */
	private String sddShipMethodId;
	
	
	/**
	 * Gets the sdd ship method id.
	 *
	 * @return the sdd ship method id
	 */
	public String getSddShipMethodId() {
		return sddShipMethodId;
	}

	/**
	 * Sets the sdd ship method id.
	 *
	 * @param sddShipMethodId the new sdd ship method id
	 */
	public void setSddShipMethodId(String sddShipMethodId) {
		this.sddShipMethodId = sddShipMethodId;
	}

	/** The same day delivery manager. */
	private BBBSameDayDeliveryManager sameDayDeliveryManager;

	/**
	 * Gets the same day delivery manager.
	 *
	 * @return the same day delivery manager
	 */
	public BBBSameDayDeliveryManager getSameDayDeliveryManager() {
		return sameDayDeliveryManager;
	}

	/**
	 * Sets the same day delivery manager.
	 *
	 * @param sameDayDeliveryManager the new same day delivery manager
	 */
	public void setSameDayDeliveryManager(
			BBBSameDayDeliveryManager sameDayDeliveryManager) {
		this.sameDayDeliveryManager = sameDayDeliveryManager;
	}
	
	/**
	 * Setter for Pricing Tool.
	 *
	 * @param pPricingTools the new pricing tools
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

	/**
	 * This methods adds list of shipping methods in request for order param
	 * being passed in the request.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @return void
	 * @throws ServletException             , IOException
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start("GetApplicableShippingMethodsDroplet", "service");
		logDebug("starting - service method GetApplicableShippingMethodsDroplet");
		HashMap<String, List<ShipMethodVO>> skuMethodsMap = null;
		List<ShipMethodVO> shipMethodVOList = null;

		final BBBOrder order = (BBBOrder) pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);
		String shippingZip = BBBCoreConstants.BLANK;
		String eligibilityStatus = BBBCoreConstants.BLANK;
		final String operationParam = (String) pRequest
				.getObjectParameter(BBBCoreConstants.OPERATION);
		String checkForInventory = (String) pRequest
				.getObjectParameter(CHECK_FOR_INVENTORY);
		BBBSessionBean sessionBean = null;
		boolean sddOptionEnabled = false;
		try {
			if (null == order
					|| StringUtils.isBlank(operationParam)
					|| !(StringUtils.equalsIgnoreCase(BBBCoreConstants.PER_SKU,
							operationParam) || StringUtils.equalsIgnoreCase(
									BBBCoreConstants.PER_ORDER, operationParam))) {
				// throw error
				pRequest.setParameter(BBBCoreConstants.SKU_MEHOD_MAP,
						skuMethodsMap);
				pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,
						shipMethodVOList);
				pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
						pResponse);
				logDebug("GetApplicableShippingMethodsDroplet: error scenario");
			} else if (StringUtils.equalsIgnoreCase(operationParam,
					BBBCoreConstants.PER_SKU)) {
				logDebug("GetApplicableShippingMethodsDroplet: multishipping page flow");
				skuMethodsMap = new HashMap<String, List<ShipMethodVO>>();

				List<CommerceItem> items = (List<CommerceItem>) order
						.getCommerceItems();

				for (CommerceItem commerceItem : items) {
					if (commerceItem instanceof BBBCommerceItem
							&& ((null == ((BBBCommerceItem) commerceItem)
							.getStoreId() || 
							StringUtils.isEmpty((((BBBCommerceItem) commerceItem)
									.getStoreId()))) && !skuMethodsMap
									.containsKey(commerceItem.getCatalogRefId()))) {

						List<ShipMethodVO> shipMethodVOs = getShippingGroupManager()
								.getShippingMethodsForSku(
										commerceItem.getCatalogRefId(),
										order.getSiteId());
						
						List<HardgoodShippingGroup> hardgoodShippingGroupList = getShippingGroupManager()
								.getHardgoodShippingGroups(order);
						
						
						//Adding shipping cost price here so that it can be displayed on jsp as per sorted on shippingCharges.
						getShippingGroupManager().calculateShippingCost(shipMethodVOs, order,
								hardgoodShippingGroupList, shippingZip);
						
						// BBBH-2379 - Shipping page changes (MPC)
						for (ShipMethodVO vo : shipMethodVOs) {
							// we set the shipping charge used for sorting on jsp to high dummy value to display SDD option in the end
							if(vo.getShipMethodId().equals(getSddShipMethodId())){
								String sddShipCharge = getSameDayDeliveryManager().getBbbCatalogTools().getSddShipMethodCharge();
								logDebug("Setting value of SDD shipping method charge as: " + sddShipCharge);
								if(!StringUtils.isBlank(sddShipCharge)){
									vo.setSortShippingCharge(Double.valueOf(sddShipCharge));
								}
							}
						}
						skuMethodsMap.put(commerceItem.getCatalogRefId(),
								shipMethodVOs);

					}
				}

				if (skuMethodsMap.isEmpty()) {

					pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM,
							pRequest, pResponse);

				} else {
					// BBBH-2379 - Shipping page changes (MPC)
					boolean sddEligibility = false;
					boolean sameDayDeliveryFlag = false;
					String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
					
					if(null != sddEligibleOn){
						sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
					}
					if(sameDayDeliveryFlag){
						sessionBean = ((BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN));
						if(sessionBean.getCurrentZipcodeVO() != null){
							sddEligibility = sessionBean.getCurrentZipcodeVO().isSddEligibility();
						}
					}
					
					logDebug("GetApplicableShippingMethodsDroplet : sddEligibility flag" + sddEligibility);
					if(sddEligibility){
						pRequest.setParameter(SDD_ELIGIBLITY_STATUS, BBBCoreConstants.MARKET_ELIGIBLE);
					} else{
						pRequest.setParameter(SDD_ELIGIBLITY_STATUS, BBBCoreConstants.MARKET_INELIGIBLE);
					}
					pRequest.setParameter(SDD_OPTION_ENABLED, false);
				
					
					pRequest.setParameter(BBBCoreConstants.SKU_MEHOD_MAP,
							skuMethodsMap);
					pRequest.serviceParameter(BBBCoreConstants.OPARAM,
							pRequest, pResponse);
				}

			} else if (StringUtils.equalsIgnoreCase(operationParam,
					BBBCoreConstants.PER_ORDER)) {
				logDebug("GetApplicableShippingMethodsDroplet: single shipping page flow");
				// Get shipping methods perOrder
				shipMethodVOList = getShippingGroupManager()
						.getShippingMethodsForOrder(order);

				String preSelectedShipMethod = null;
				String sddStoreId = BBBCoreConstants.BLANK;
				List<HardgoodShippingGroup> hardgoodShippingGroupList = getShippingGroupManager()
						.getHardgoodShippingGroups(order);
				for (HardgoodShippingGroup hardgoodShippingGroupItem : hardgoodShippingGroupList) {
					preSelectedShipMethod = hardgoodShippingGroupItem.getShippingMethod();
					
					if(!StringUtils.isBlank(preSelectedShipMethod) && preSelectedShipMethod.equals(getSddShipMethodId()) 
							&& hardgoodShippingGroupItem instanceof BBBHardGoodShippingGroup){
						sddStoreId = ((BBBHardGoodShippingGroup) hardgoodShippingGroupItem).getSddStoreId();
						logDebug("GetApplicableShippingMethodsDroplet : sddStoreId in order" + sddStoreId);
					}
					
				}
				
				logDebug("GetApplicableShippingMethodsDroplet : preSelectedShipMethod from order is " + preSelectedShipMethod);
				// BBBH-2379 - Shipping page changes (MPC)
				//If zipcode is empty , fetch it from session to check for SDD eligibility
				boolean sameDayDeliveryFlag = false;
				String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
				final String DISPLAY_SDD_ALWAYS="displaySDDAlways";
				if(null != sddEligibleOn){
					sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
				}
				if(sameDayDeliveryFlag){
					
					sessionBean = ((BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN));
					
					shippingZip = BBBUtility.hyphenExcludedZip((String) pRequest.getParameter(CURRENT_ZIP));
					logDebug("GetApplicableShippingMethodsDroplet : sdd flow begins with global flag true and zip obtained from request is" + shippingZip);
					if (StringUtils.isBlank(shippingZip) ){
						if(sessionBean.getCurrentZipcodeVO() != null){
							shippingZip = sessionBean.getCurrentZipcodeVO().getZipcode();
							logDebug("Obtaining zip from session: " + shippingZip);
							
							RegionVO regionVO = getSameDayDeliveryManager().populateDataInVO(sessionBean, pRequest,shippingZip,BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_FALSE,BBBCoreConstants.RETURN_TRUE);
							eligibilityStatus = getSameDayDeliveryManager().checkForSDDEligibility(pRequest, order, regionVO, shipMethodVOList, shippingZip);
							if(regionVO != null){
								pRequest.setParameter("regionVO", regionVO);
							}
							if (isDisplaySSD(sessionBean, regionVO , true)) {
								logDebug("GetApplicableShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" );
								pRequest.getSession().setAttribute(DISPLAY_SDD_ALWAYS, BBBCoreConstants.TRUE);
							} 
						}
						
					} else if(shippingZip.equals(REGISTRY_ZIP)){
						logDebug("Registry Zip flow");
						eligibilityStatus = BBBCoreConstants.ADDRESS_INELIGIBLE;
						if (isDisplaySSD(sessionBean, null , false)) {
							logDebug("GetApplicableShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" );
							pRequest.getSession().setAttribute(DISPLAY_SDD_ALWAYS, BBBCoreConstants.TRUE);
						}
					} else{
						RegionVO regionVO = getSameDayDeliveryManager().populateDataInVO(sessionBean, pRequest, shippingZip,
								BBBCoreConstants.RETURN_FALSE,
								BBBCoreConstants.RETURN_FALSE,
								BBBCoreConstants.RETURN_TRUE);
						eligibilityStatus = getSameDayDeliveryManager().checkForSDDEligibility(pRequest, order, regionVO, shipMethodVOList, shippingZip);
						if(regionVO != null){
							pRequest.setParameter("regionVO", regionVO);
						}
						if (isDisplaySSD(sessionBean, regionVO , true)) {
							logDebug("GetApplicableShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" );
							pRequest.getSession().setAttribute(DISPLAY_SDD_ALWAYS, BBBCoreConstants.TRUE);
						} 
					}
					
					logDebug("GetApplicableShippingMethodsDroplet sdd eligibilityStatus is : " + eligibilityStatus + " checkForInventory flag is " + checkForInventory);
					
					if(!StringUtils.isBlank(eligibilityStatus) && eligibilityStatus.equals(BBBCoreConstants.ITEM_ELIGIBLE)){
						// On page load, if SDD shipping method is already present in shipping group then we need to check for inventory also
						if(!StringUtils.isBlank(checkForInventory) && Boolean.valueOf(checkForInventory) && !StringUtils.isBlank(sddStoreId)){
							/*
							 * Below portion of code is for checking inventory
							 * from session to prevent SBC calls in checkout
							 * page load.
							 */
							sddOptionEnabled = checkSDDInventoryFromSession(pRequest, order, sddStoreId);
							if (sddOptionEnabled) {
								pRequest.setParameter(SDD_OPTION_ENABLED, true);
								eligibilityStatus = BBBCoreConstants.SDD_ELIGIBLE;
							} 
						} else {
							logDebug(
									"GetApplicableShippingMethodsDroplet option as enabled since sdd eligibility conditions are met");
							pRequest.setParameter(SDD_OPTION_ENABLED, true);
							sddOptionEnabled = true;
						}
						
					}
					pRequest.setParameter(SDD_ELIGIBLITY_STATUS, eligibilityStatus);
					logDebug("GetApplicableShippingMethodsDroplet returns sdd eligibilityStatus as" + eligibilityStatus + " SDD option enabled is " + sddOptionEnabled);
				}
				
				

				getShippingGroupManager().calculateShippingCost(shipMethodVOList, order,
						hardgoodShippingGroupList, shippingZip);

				boolean matchFound = false;
				boolean isStandardFound = false;
				for (ShipMethodVO vo : shipMethodVOList) {
					// if market is not found then we set the shipping charge to high dummy value to display SDD in the end after sorting
					if(vo.getShipMethodId().equals(getSddShipMethodId())){
						String sddShipCharge = getSameDayDeliveryManager().getBbbCatalogTools().getSddShipMethodCharge();
						logDebug("Setting value of SDD shipping method charge as: " + sddShipCharge);
						if(!StringUtils.isBlank(sddShipCharge)){
							vo.setSortShippingCharge(Double.valueOf(sddShipCharge));
						}
					}

					if (preSelectedShipMethod != null && preSelectedShipMethod.equalsIgnoreCase(vo.getShipMethodId())){
						matchFound = true;
					}

					if(vo.getShipMethodId().equalsIgnoreCase(BBBCoreConstants.SHIP_METHOD_STANDARD_ID)){
						isStandardFound = true;
					}
				}
				
				if (preSelectedShipMethod != null && preSelectedShipMethod.equalsIgnoreCase(getSddShipMethodId()) && !sddOptionEnabled){
					if(isStandardFound){
						preSelectedShipMethod = BBBCoreConstants.SHIP_METHOD_STANDARD_ID;
					} else{
						preSelectedShipMethod = (shipMethodVOList.get(0)).getShipMethodId();
					}
					matchFound = true;
				}
				
				if (!matchFound && !shipMethodVOList.isEmpty() ) {
					preSelectedShipMethod = (shipMethodVOList.get(0)).getShipMethodId();
				}				
				logDebug("preSelectedShipMethod is set as: " + preSelectedShipMethod);
				pRequest.setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,
						preSelectedShipMethod);
				pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,
						shipMethodVOList);
				pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest,
						pResponse);

			}
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"SystemException"), e);
			setErrorParam(pRequest, pResponse);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BusinessException"), e);
			setErrorParam(pRequest, pResponse);

		} catch (RepositoryException re) {
			logError(LogMessageFormatter.formatMessage(null,
					"RepositoryException"), re);
			setErrorParam(pRequest, pResponse);
		}
		logDebug("Exiting - service method GetApplicableShippingMethodsDroplet");

		BBBPerformanceMonitor.end("GetApplicableShippingMethodsDroplet", "service");
	}

	/**
	 * This method is for checking inventory from session to prevent SBC calls
	 * in checkout page on load.
	 * 
	 * @param pRequest
	 * @param order
	 * @param sddStoreId
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	private boolean checkSDDInventoryFromSession(final DynamoHttpServletRequest pRequest, final BBBOrder order,
			String sddStoreId) throws BBBSystemException, BBBBusinessException {
		vlogDebug("GetApplicableShippingMethodsDroplet.checkSDDInventoryFromSession: Starts");
		BBBStoreInventoryContainer bbbStoreInventoryContainer = (BBBStoreInventoryContainer) pRequest
				.resolveName(BBBCoreConstants.BBB_STORE_INVENTORY_CONTAINER);
		Map<String, Integer> inventoryFromSession = bbbStoreInventoryContainer.getSddStoreInventoryMap();
		boolean sddOptionEnabled = false;
		if (!BBBUtility.isMapNullOrEmpty(inventoryFromSession)) {
			/*
			 * Create sku and quantity map by adding quantity of same sku's in
			 * different commerce items
			 */
			Map<String, Long> skuIdQuantityMap = getSameDayDeliveryManager().getSkuIdQuantityMapFromOrder(order);
			for (Map.Entry<String, Long> entry : skuIdQuantityMap.entrySet()) {
				String skuId = entry.getKey();
				Long requiredQty = entry.getValue();
				if (!inventoryFromSession.containsKey(sddStoreId + BBBCoreConstants.PIPE_SYMBOL + skuId)) {
					vlogDebug(
							"GetApplicableShippingMethodsDroplet.checkSDDInventoryFromSession: Inventory is not found whileloading SPC page, switching back to standard shipping method.");
					return false;
				}
				ThresholdVO skuThresholdVO = getSameDayDeliveryManager().getBbbCatalogTools()
						.getSkuThreshold(order.getSiteId(), skuId);
				int availableInventory = inventoryFromSession.get(sddStoreId + BBBCoreConstants.PIPE_SYMBOL + skuId);
				int inventoryStatus = getSameDayDeliveryManager().getInventoryManagerImpl()
						.getInventoryStatus(availableInventory, requiredQty, skuThresholdVO, sddStoreId);
				if (inventoryStatus == BBBInventoryManager.AVAILABLE
						|| inventoryStatus == BBBInventoryManager.LIMITED_STOCK) {
					sddOptionEnabled = true;
					vlogDebug(
							"GetApplicableShippingMethodsDroplet.checkSDDInventoryFromSession: Inventory available for sku {0} in store {1}.",
							skuId, sddStoreId);
				} else {
					vlogDebug(
							"GetApplicableShippingMethodsDroplet.checkSDDInventoryFromSession: Inventory is not found whileloading SPC page, switching back to standard shipping method.");
					sddOptionEnabled = false;
					break;
				}

			}
		}
		vlogDebug("GetApplicableShippingMethodsDroplet.checkSDDInventoryFromSession: Ends");
		return sddOptionEnabled;
	}


	/**
	 *  this method is use to check DISPLAY_SDD_ALWAYS flag
	 * @param sessionBean - session bean
	 * @param regionVO - region VO
	 * @param includeRegionVO - flag to include region VO  
	 * @return -  boolesn 
	 */
	protected boolean isDisplaySSD(BBBSessionBean sessionBean, RegionVO regionVO , boolean includeRegionVO) {
		boolean result = false ;
		if(includeRegionVO){
			result = regionVO != null
				|| (null != sessionBean
						&& null != sessionBean
								.getCurrentZipcodeVO() && sessionBean
						.getCurrentZipcodeVO()
						.isSddEligibility())
				|| (null != sessionBean
						&& null != sessionBean
								.getLandingZipcodeVO() && sessionBean
						.getLandingZipcodeVO()
						.isSddEligibility());
		}else{
		result = (null != sessionBean
					&& null != sessionBean.getCurrentZipcodeVO() && sessionBean
					.getCurrentZipcodeVO().isSddEligibility())
					|| (null != sessionBean
							&& null != sessionBean
									.getLandingZipcodeVO() && sessionBean
							.getLandingZipcodeVO()
							.isSddEligibility());
		}
		return result ;
	}


	

	
	
	/**
	 * Sets the error param.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void setErrorParam(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse) throws ServletException, IOException{
		pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
				pResponse);
	}

}
