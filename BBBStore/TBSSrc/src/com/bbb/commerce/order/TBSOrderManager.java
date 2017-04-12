package com.bbb.commerce.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.cmo.vo.CMOLineItemsReqVO;
import com.bbb.cmo.vo.CMOLineItemsRespVO;
import com.bbb.cmo.vo.LineItemVO;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.TBSBopusInventoryService;
import com.bbb.commerce.inventory.TBSInventoryManagerImpl;
import com.bbb.commerce.order.purchase.TBSPurchaseProcessHelper;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.exception.TBSCustomOrderException;
import com.bbb.framework.httpquery.HTTPQueryManager;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kirsch.vo.ItemsFromWorkBookReqVO;
import com.bbb.kirsch.vo.ItemsFromWorkBookRespVO;
import com.bbb.kirsch.vo.LineItem;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;

import atg.commerce.CommerceException;
import atg.commerce.gifts.InvalidGiftQuantityException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import noNamespace.AccessDocument.Access;
import noNamespace.GetItemsFromWorkBookRequestDocument;
import noNamespace.GetItemsFromWorkBookRequestDocument.GetItemsFromWorkBookRequest;

/**
 * This class extends the BBBOrderManager to add the TBS Specific functionalities.
 */
public class TBSOrderManager extends BBBOrderManager {

	/** mApiKey property to hold the Api key. */
	private double mApiKey;

	/**  mTransKey property to hold the Trans key. */
	private String mTransKey;

	/** The HTTP query manager. */
	private HTTPQueryManager mHTTPQueryManager;

	private TBSBopusInventoryService mBopusService;


	private TBSInventoryManagerImpl mTbsInventoryManager;

	private TBSSearchStoreManager mSearchStoreManager;

	private LblTxtTemplateManager messageHandler;
	
	/**
	 * @return the tbsInventoryManager
	 */
	public TBSInventoryManagerImpl getTbsInventoryManager() {
		return mTbsInventoryManager;
	}
	/**
	 * @param pTbsInventoryManager the tbsInventoryManager to set
	 */
	public void setTbsInventoryManager(TBSInventoryManagerImpl pTbsInventoryManager) {
		mTbsInventoryManager = pTbsInventoryManager;
	}

	/**
	 * @return the bopusService
	 */
	public TBSBopusInventoryService getBopusService() {
		return mBopusService;
	}
	/**
	 * @param pBopusService the bopusService to set
	 */
	public void setBopusService(TBSBopusInventoryService pBopusService) {
		mBopusService = pBopusService;
	}

	/**
	 * @return the searchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}
	/**
	 * @param pSearchStoreManager the searchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}

	/**
	 * Moves gift items from order.
	 * 
	 * @param pRemovableItems
	 *            the removable items
	 * @param pRemovableItemQuantityMap
	 *            the removable item quantity map
	 * @param pOrder
	 *            the order
	 * @throws InvalidGiftQuantityException
	 *             the invalid gift quantity exception
	 * @throws CommerceException
	 *             the commerce exception
	 */
	public void moveGiftItemsFromOrder(List<CommerceItem> pRemovableItems, Map<String, Long> pRemovableItemQuantityMap, Order pOrder)
			throws InvalidGiftQuantityException, CommerceException {

		for (Iterator<CommerceItem> lIterator = pRemovableItems.iterator(); lIterator.hasNext();) {
			CommerceItem lCommerceItem = (CommerceItem) lIterator.next();

			Long removableQty = pRemovableItemQuantityMap.get(lCommerceItem.getId());
			if (removableQty <= 0)
				throw new InvalidGiftQuantityException();

			long itemQty = lCommerceItem.getQuantity();
			long quantity = itemQty - removableQty;
			if (quantity > 0) {
				lCommerceItem.setQuantity(quantity);
				vlogDebug("updating the item quantity after moving the " + removableQty + " quantity to the gift list");
			} else {
				getCommerceItemManager().removeItemFromOrder(pOrder, lCommerceItem.getId());
			}
		}
		updateOrder(pOrder);
	}



	/**
	 * The CreateKirschOrder method invoke the Krisch service by passing
	 * TBSItemVO object and get the line items response and creates an order.
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @return true, if successful
	 * @throws BBBSystemException 
	 * @throws ServletException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 */
	public void createKirschOrder(String pUserId, Order pOrder) throws BBBSystemException {


		ItemsFromWorkBookReqVO lBookRequestVO = new ItemsFromWorkBookReqVO();
		ItemsFromWorkBookRespVO responseVO = new ItemsFromWorkBookRespVO();
		Map<String, String> paramsMap = new HashMap<String, String>();

		vlogDebug("Creating the Kirsch Serice Request");

		GetItemsFromWorkBookRequestDocument lDoc = GetItemsFromWorkBookRequestDocument.Factory.newInstance();
		GetItemsFromWorkBookRequest lRequest = lDoc.addNewGetItemsFromWorkBookRequest();
		Access lAccess = lRequest.addNewAccess();

		lAccess.setApi(BigDecimal.valueOf(getApiKey()));
		lAccess.setTranskey(getTransKey());
		lRequest.setUserID(pUserId);

		//setting the input parameters for the kirsch request
		paramsMap.put("getItemsFromWorkbook", lDoc.toString());
		paramsMap.put("userId", pUserId);
		paramsMap.put("isPostXMLRequest", "true");
		lBookRequestVO.setParamsValuesMap(paramsMap);
		lBookRequestVO.setServiceType("getItemsFromWorkbook");
		lBookRequestVO.setServiceName("getItemsFromWorkbook");
		String lSiteId = SiteContextManager.getCurrentSiteId();
		lBookRequestVO.setSiteId(lSiteId);

		try {
			vlogDebug("Invoking the Kirsch Serice");
			responseVO = (ItemsFromWorkBookRespVO) getHTTPQueryManager().invoke(lBookRequestVO);
			if (responseVO != null) {
				vlogDebug("Got the response from Kirsch Serice " + responseVO);
				List<LineItem> lineItems = responseVO.getLineItems();
				if (lineItems != null && !lineItems.isEmpty()) {
					((TBSPurchaseProcessHelper) getPurchaseProcessHelper()).createKirschCommerceItems(lineItems, pOrder);
				} else {
					throw new TBSCustomOrderException(
							"Kirsch items configuration is not complete");
				}
			} else {
				throw new TBSCustomOrderException("There is a problem while trying access the Kirsch Service. Please try again later");
			}
		} catch(TBSCustomOrderException te){
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_KIRSCH_ORDER_10001 + ":" + te);
			}
			throw te;
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_KIRSCH_ORDER_10002 + ":" + e);
			}
			throw new TBSCustomOrderException("There is a problem while trying access the Kirsch Service. Please try again later");
		} catch (BBBSystemException e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_KIRSCH_ORDER_10003 + ":" + e);
			}
			throw new TBSCustomOrderException("There is a problem while trying access the Kirsch Service. Please try again later");
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_KIRSCH_ORDER_10004 + ":" + e);
			}
			throw new TBSCustomOrderException("There is a problem while trying access the Kirsch Service. Please try again later");
		} catch (CommerceException e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_KIRSCH_ORDER_10005 + ":" + e);
			}
			throw new TBSCustomOrderException("There is a problem while trying access the Kirsch Service. Please try again later");
		}
	}

	/**
	 * The createCMOOrder method invoke the CMO service by passing TBSItemVO
	 * object and get the line items response and creates an order.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws BBBSystemException 
	 */
	public void createCMOOrder(String pUserId, Order pOrder) throws ServletException, IOException, BBBSystemException {
		CMOLineItemsReqVO lBookRequestVO = new CMOLineItemsReqVO();
		CMOLineItemsRespVO responseVO = new CMOLineItemsRespVO();
		Map<String, String> paramsMap = new HashMap<String, String>();

		vlogDebug("Creating the CMO Serice Request");

		lBookRequestVO.setParamsValuesMap(paramsMap);
		paramsMap.put("userId", pUserId);
		paramsMap.put("paramsPartOfURL", "true");

		lBookRequestVO.setServiceType("getLineItems");
		lBookRequestVO.setServiceName("getLineItems");

		String lSiteId = SiteContextManager.getCurrentSiteId();
		lBookRequestVO.setSiteId(lSiteId);

		try {
			vlogDebug("Invoking the CMO Serice");
			responseVO = (CMOLineItemsRespVO) getHTTPQueryManager().invoke(lBookRequestVO);

			if (responseVO != null) {
				vlogDebug("Got the response from CMO Serice " + responseVO);
				List<LineItemVO> lLineItems = responseVO.getLineItems();
				vlogDebug("LineItems got in response from CMO Serice " + lLineItems);
				if (lLineItems != null && !lLineItems.isEmpty()) {
					((TBSPurchaseProcessHelper) getPurchaseProcessHelper()).createCMOCommerceItems(lLineItems, pOrder);
				} else {
					throw new TBSCustomOrderException(getMessageHandler().getErrMsg(BBBCoreErrorConstants.ERR_MSG_CMO_SERVICE_EMPTY_LINE_ITEM, null, null));
				}
			} else {
				throw new TBSCustomOrderException(getMessageHandler().getErrMsg(BBBCoreErrorConstants.ERR_MSG_CMO_SERVICE_NULL_RESPONSE, null, null));
			}
		} catch(TBSCustomOrderException te){
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_CMO_ORDER_10001 + ":" + te);
			}
			throw te;
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_CMO_ORDER_10002 + ":" + e);
			}
			throw new TBSCustomOrderException(getMessageHandler().getErrMsg(BBBCoreErrorConstants.ERR_MSG_CMO_SERVICE_BUSINESS_EXCEP, null, null));
		} catch (BBBSystemException e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_CMO_ORDER_10003 + ":" + e);
			}
			throw new TBSCustomOrderException(getMessageHandler().getErrMsg(BBBCoreErrorConstants.ERR_MSG_CMO_SERVICE_SYSTEM_EXCEP, null, null));
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_CMO_ORDER_10004 + ":" + e);
			}
			throw new TBSCustomOrderException(getMessageHandler().getErrMsg(BBBCoreErrorConstants.ERR_MSG_CMO_SERVICE_REPOSITORY_EXCEP, null, null));
		} catch (CommerceException e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.TBS_CMO_ORDER_10005 + ":" + e);
			}
			throw new TBSCustomOrderException(getMessageHandler().getErrMsg(BBBCoreErrorConstants.ERR_MSG_CMO_SERVICE_COMMERCE_EXCEP, null, null));
		}

	}

	/* (non-Javadoc)
	 * @see com.bbb.commerce.order.BBBOrderManager#compareHardgoodShippingGroups(atg.commerce.order.ShippingGroup, atg.commerce.order.ShippingGroup)
	 */
	@Override
	public boolean compareHardgoodShippingGroups(ShippingGroup pSrcShippingGroup, ShippingGroup pDestShippingGroup) {
		Address srcAddress = ((HardgoodShippingGroup)pSrcShippingGroup).getShippingAddress();
		Address destAddress = ((HardgoodShippingGroup)pDestShippingGroup).getShippingAddress();
		if (srcAddress!= null &&  "Initial".equalsIgnoreCase(srcAddress.getState())) {
			srcAddress.setState(null);
		}

		if (destAddress != null && "Initial".equalsIgnoreCase(destAddress.getState())) {
			destAddress.setState(null);
		}
		return super.compareHardgoodShippingGroups(pSrcShippingGroup, pDestShippingGroup);
	}

	/**
	 * Re populate the availabilityMap of Order with Inventory availability
	 * It loops through all the CommerceItems in the Order and finds out the 
	 * Inventory availability then creates an entry in the mavailabilityMap
	 * 
	 * 
	 * @param pOrder
	 * @throws InventoryException
	 */
	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public void updateAvailabilityMapInOrder(DynamoHttpServletRequest pRequest, Order pOrder) {
		if (isLoggingDebug()) {
			logDebug("updateAvailabilityMapInOrder() : Starts");
		}
		final long startTime = System.currentTimeMillis();
		String methodName = "updateAvailabilityMapInOrder";
		boolean isMonitorCanceled = false;
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
		try {
			if (pOrder instanceof BBBOrder) {
				BBBOrder bbbOrder = (BBBOrderImpl) pOrder;
				Map<String, Integer> tempAvailabilityMap = new HashMap<String, Integer>();
				List<CommerceItem> itemList = bbbOrder.getCommerceItems();
				BBBCommerceItem bbbItem = null;
				BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer)pRequest.
						resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");

				int availabilityStatus = BBBInventoryManager.AVAILABLE;
				vlogDebug("updateAvailabilityMapInOrder() : SiteID  "+ pOrder.getSiteId());

				SKUDetailVO skuvo = null;
				long rolledUpQty = 0;
				String lShiptime=null;
				for (CommerceItem item : itemList) {
					if (item instanceof BBBCommerceItem) {
						bbbItem = (BBBCommerceItem) item;
						try {
							rolledUpQty = getPurchaseProcessHelper().getRollupQtyForUpdate(bbbItem.getStoreId(),
									bbbItem.getRegistryId(), bbbItem.getCatalogRefId(), (BBBOrderImpl) bbbOrder, bbbItem.getQuantity());
								
							availabilityStatus = getPurchaseProcessHelper().checkCachedInventory(pOrder.getSiteId(), bbbItem.getCatalogRefId(), 
									bbbItem.getStoreId(), bbbOrder, rolledUpQty, BBBInventoryManager.RETRIEVE_CART, 
									storeInventoryContainer, BBBInventoryManager.AVAILABLE);
							if( availabilityStatus == BBBInventoryManager.NOT_AVAILABLE  && StringUtils.isBlank(bbbItem.getStoreId())){
							skuvo = getCatalogUtil().getSKUDetails(pOrder.getSiteId(), bbbItem.getCatalogRefId());
							}
						} catch (CommerceException e) {
							vlogError(e.getMessage(), e);
							availabilityStatus = BBBInventoryManager.AVAILABLE;
							isMonitorCanceled = true;
							BBBPerformanceMonitor.cancel(BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
						}catch (BBBSystemException e) {
							vlogError("SKUDetails VO not found :: ",e);
						} catch (BBBBusinessException e) {
							vlogError("SKUDetails VO not found :: ",e);
						}
						//Enabling and disabling Add to Cart based on inventory availability on BOPUS service
						if(skuvo != null && !skuvo.isVdcSku() && !skuvo.isLtlItem() && availabilityStatus == BBBInventoryManager.NOT_AVAILABLE 
								&& StringUtils.isBlank(bbbItem.getStoreId())){

							lShiptime = getSearchStoreManager().getShipTime(bbbItem.getCatalogRefId(), rolledUpQty, pOrder.getSiteId(), (String)ServletUtil.getCurrentRequest().getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER));
						
							availabilityStatus = BBBInventoryManager.AVAILABLE;
							if(!StringUtils.isBlank(lShiptime) && lShiptime == "0004"){
								availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
							}
						}
						tempAvailabilityMap.put(bbbItem.getId(),
								Integer.valueOf(availabilityStatus));
					}


				}

				// set the tempAvailabilityMap to order
				bbbOrder.setAvailabilityMap(tempAvailabilityMap);
				if (isLoggingDebug()) {
					logDebug("updateAvailabilityMapInOrder() : Ends");
				}

			}
		} finally {
			if (isLoggingDebug()) {
				long totalTime = System.currentTimeMillis() - startTime;
				logInfo("***Total time taken by BBBOrderManager.updateAvailabilityMapInOrder() is "
						+ totalTime);
				if(!isMonitorCanceled){
					BBBPerformanceMonitor.end(
							BBBPerformanceConstants.BBB_ORDER_MGR, methodName);
				}
			}
		}
	}


	/**
	 * Gets the api key.
	 * 
	 * @return the api key
	 */
	public double getApiKey() {
		return mApiKey;
	}

	/**
	 * Sets the api key.
	 * 
	 * @param pApiKey
	 *            the new api key
	 */
	public void setApiKey(double pApiKey) {
		mApiKey = pApiKey;
	}

	/**
	 * Gets the trans key.
	 * 
	 * @return the trans key
	 */
	public String getTransKey() {
		return mTransKey;
	}

	/**
	 * Sets the trans key.
	 * 
	 * @param pTransKey
	 *            the new trans key
	 */
	public void setTransKey(String pTransKey) {
		mTransKey = pTransKey;
	}

	/**
	 * @return the hTTPQueryManager
	 */
	public HTTPQueryManager getHTTPQueryManager() {
		return mHTTPQueryManager;
	}

	/**
	 * @param pHTTPQueryManager
	 *            the hTTPQueryManager to set
	 */
	public void setHTTPQueryManager(HTTPQueryManager pHTTPQueryManager) {
		mHTTPQueryManager = pHTTPQueryManager;
	}

	/**
	 * This method is used to remove the overridden details from the commerce items.
	 * @param pOrder
	 */
	@SuppressWarnings("unchecked")
	public void removeOverrideItems(Order pOrder) {
		if(pOrder == null){
			return;
		}
		List<CommerceItem> citems = pOrder.getCommerceItems();
		List<ShippingGroup> shipGroups = pOrder.getShippingGroups();

		//removing item override
		if(citems != null && !citems.isEmpty()){
			TBSCommerceItem tbsItem = null;
			LTLDeliveryChargeCommerceItem ltlDeliItem = null;
			LTLAssemblyFeeCommerceItem ltlAssItem = null;
			TBSItemInfo tbsInfo = null;
			for (CommerceItem commerceItem : citems) {
				if(commerceItem instanceof TBSCommerceItem){
					tbsItem = (TBSCommerceItem) commerceItem;
					tbsInfo = tbsItem.getTBSItemInfo();
					removeOverrideDetails(tbsInfo);
				}
				if(commerceItem instanceof LTLDeliveryChargeCommerceItem){
					ltlDeliItem = (LTLDeliveryChargeCommerceItem) commerceItem;
					tbsInfo = ltlDeliItem.getTBSItemInfo();
					removeOverrideDetails(tbsInfo);
				}
				if(commerceItem instanceof LTLAssemblyFeeCommerceItem){
					ltlAssItem = (LTLAssemblyFeeCommerceItem) commerceItem;
					tbsInfo = ltlAssItem.getTBSItemInfo();
					removeOverrideDetails(tbsInfo);
				}
			}
		}

		// removing ship & tax override
		BBBHardGoodShippingGroup hardGoodShip = null;
		BBBStoreShippingGroup bbbStoreShip = null;
		TBSShippingInfo tbsShipInfo = null;
		for (ShippingGroup shippingGroup : shipGroups) {
			if(shippingGroup instanceof BBBHardGoodShippingGroup){
				hardGoodShip = (BBBHardGoodShippingGroup) shippingGroup;
				tbsShipInfo = hardGoodShip.getTbsShipInfo();
			} else if(shippingGroup instanceof BBBStoreShippingGroup){
				bbbStoreShip = (BBBStoreShippingGroup) shippingGroup;
				tbsShipInfo = bbbStoreShip.getTbsShipInfo();
			}
			if(tbsShipInfo != null){
				tbsShipInfo.setShipPriceOverride(false);
				tbsShipInfo.setShipPriceValue(0);
				tbsShipInfo.setShipPriceReason("");
				tbsShipInfo.setTaxOverride(false);
				tbsShipInfo.setTaxValue(0);
				tbsShipInfo.setTaxReason("");
				tbsShipInfo.setTaxExemptId("");
			}
		}
	}

	/**
	 * remove the override details
	 * @param tbsInfo
	 */
	private void removeOverrideDetails(TBSItemInfo tbsInfo) {
		if(tbsInfo != null){
			tbsInfo.setCompetitor("");
			tbsInfo.setOverideReason("");
			tbsInfo.setOverridePrice(0);
			tbsInfo.setOverrideQuantity(0);
			tbsInfo.setPriceOveride(false);
		}
	}

	public void getAutoWaiveShipDetails(Order pOrder, String currentId) {
		String autowaiveFlag = getAutoWaiveShippingFlag(pOrder.getSiteId());
		if(!StringUtils.isBlank(autowaiveFlag) && autowaiveFlag.equalsIgnoreCase(BBBCoreConstants.TRUE)){
			vlogDebug("Executing autowaive service");
			try {
			((TBSOrderTools)getOrderTools()).getAutoWaiveShipDetails(pOrder,currentId);
			updateOrder(pOrder);
			} catch (CommerceException e) {
				vlogError("Exception occurred while updating the order in case of AutoWaiveShipping :: "+e);
			}
		} else {
			vlogDebug("Autowaive service is not executing");
		}
	}


	/**
	 * Gets the Auto waive shipping Flag
	 * @param pSiteId
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getAutoWaiveShippingFlag(String pSiteId) {
		List<String> siteIds = null;
		String autoWaiveFlag = null;
		if (pSiteId != null) {
			try {
				siteIds = getSearchStoreManager().getCatalogTools().getAllValuesForKey(TBSConstants.AUTO_WAIVE_SHIPPING_FLAG, pSiteId);
				if (siteIds != null && !siteIds.isEmpty() ){
					autoWaiveFlag = siteIds.get(BBBCoreConstants.ZERO);
				}
			} catch (BBBSystemException e) {
				vlogError("No Value found for AutoWaiveShippingFlag");
			} catch (BBBBusinessException e) {
				vlogError("No Value found for AutoWaiveShippingFlag");
			}
		}
		return autoWaiveFlag;
	}
	
	public void clearOrderForGS(Order pOrder) {
		List<CommerceItem> cItems = pOrder.getCommerceItems();
		List <String> removalCommerceIds = new ArrayList<String>();
		try {
			for (CommerceItem commerceItem : cItems) {
				removalCommerceIds.add(commerceItem.getId());
			}
			for (String commerceId : removalCommerceIds) {
				getCommerceItemManager().removeAllRelationshipsFromCommerceItem(pOrder, commerceId);
				getCommerceItemManager().removeItemFromOrder(pOrder, commerceId);
			}
		} catch (CommerceException e) {
			vlogError("error while clearing cart for GS order" + e);
		}
	}
	public LblTxtTemplateManager getMessageHandler() {
		return messageHandler;
	}
	public void setMessageHandler(LblTxtTemplateManager messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	
}


