package com.bbb.commerce.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;
import atg.commerce.order.OrderHolder;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.inventory.vo.BBBStoreInventoryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

/**
 * This class is used to get inventory status of BOPUS products
 * 
 * @author jpadhi
 * @version $Revision: #1 $
 */
public class BBBStoreInventoryManagerImpl extends BBBGenericService implements
		BBBStoreInventoryManager {
	/*
	 * ==================================================== * MEMBER VARIABLE
	 * ====================================================
	 */
	private BBBCatalogTools mCatalogTools;
	private SiteContextManager siteContextManager = null;
	private BopusInventoryService mBopusService;
	private SearchStoreManager searchStoreManager;
	private BBBCheckoutManager checkoutManager;
	private BBBStoreInventoryManager storeInventoryManager;
	public final static String STORE_DETAILS = "storeDetails";
	public final static String FAV_STORE_STOCK_STATUS = "favStoreStockStatus";
	/*
	 * ===================================================== * SETTERS & GETTERS
	 * =====================================================
	 */

	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}

	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}
	
	public BopusInventoryService getBopusService() {
		return mBopusService;
	}

	public void setBopusService(
			BopusInventoryService bopusService) {
		this.mBopusService = bopusService;
	}

	/**
	 * This method return the BBBStoreInventoryVO for store pickup item
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @param storeId
	 * @return BBBStoreInventoryVO
	 * @exception InventoryException
	 * @throws BBBBusinessException 
	 * 
	 */
	@Override
	public BBBStoreInventoryVO getInventory(final String pSkuId, final String storeId , boolean isFromLocalInventory) throws InventoryException, BBBBusinessException {
		final String pSiteId = extractCurrentSiteId();
		List<BBBStoreInventoryVO> storeInventories = null;
		List<String> storeIds = new ArrayList<String>();
		if((null != storeId) && (!storeId.equals(""))){
			storeIds.add(storeId); 
			storeInventories = getInventory(pSiteId, pSkuId, storeIds , isFromLocalInventory);
		} 
				
		if((null == storeInventories) || (storeInventories.size()==0)) {
			throw new BBBBusinessException("empty_storeId", "Store ID entered is empty");
		}
		return storeInventories.get(0);
	}

	/**
	 * @return
	 */
	protected String extractCurrentSiteId() {
		return getSiteContextManager().getCurrentSiteId();
	}	
	
	
	/*
	 * ===================================================== * METHODS
	 * =====================================================
	 */

	/**
	 * This method return the BBBStoreInventoryVO for store pickup item
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @param storeId
	 * @return BBBStoreInventoryVO
	 * @exception InventoryException
	 * 
	 */
	@Override
	public List<BBBStoreInventoryVO> getInventory(final String pSiteId,
			final String pSkuId, final List<String> storeIds , boolean isFromLocalRepository)
			throws InventoryException {
		// TODO Auto-generated method stub

		final List<BBBStoreInventoryVO> storeInventories = new ArrayList<BBBStoreInventoryVO>();

		/*final BopusInventoryService bopusService = (BopusInventoryService) Nucleus
				.getGlobalNucleus().resolveName(
						"/com/bbb/commerce/inventory/BopusInventoryService");*/

		try {

			final ThresholdVO skuThresholdVO = getCatalogTools()
					.getSkuThreshold(pSiteId, pSkuId);
			
			final Map<String, Integer> inventoryByStore = getBopusService()
					.getInventoryForBopusItem(pSkuId, storeIds , isFromLocalRepository);
			//StoreIds will never be null : if (storeIds != null) {
				for (int i = 0; i < storeIds.size(); i++) {
					final BBBStoreInventoryVO storeInventoryVO = new BBBStoreInventoryVO();

					storeInventories.add(storeInventoryVO);
					if( null != inventoryByStore && inventoryByStore.size() > 0 && null != inventoryByStore.get(storeIds.get(i))){
						storeInventoryVO.setStoreInventoryStock(inventoryByStore
								.get(storeIds.get(i)));
						}
						else{
							storeInventoryVO.setStoreInventoryStock(0);
						}
					if (skuThresholdVO != null) {
					storeInventoryVO.setThresholdAvailable(skuThresholdVO
							.getThresholdAvailable());
					storeInventoryVO.setThresholdLimited(skuThresholdVO
							.getThresholdLimited());
					}
					storeInventoryVO.setStoreId(storeIds.get(i));
				}
			//}
			return storeInventories;
		} catch (BBBSystemException e) {
			
			throw new InventoryException(e);
		} catch (BBBBusinessException e) {
			throw new InventoryException(e);
		}

	}
	
	/**
	 * This method fetches favorite store inventory from pdp.
	 * @param skuId
	 * @param siteId
	 * @param storeId
	 * @return list of skus with inventory status
	 * @throws InventoryException
	 * @throws BBBBusinessException
	 */
	@Override
	public Map<String, Integer> getFavStoreInventory(final String skuId, final String siteId,
			final String storeId , boolean isFromLocalRepository) throws InventoryException, BBBBusinessException {
		logDebug("BBBStoreInventoryManagerImpl.getFavStoreInventory() - start "
				+ "with params skuId: " + skuId + "siteId" + siteId + "storeId" + storeId);
		BBBStoreInventoryVO favStoreInventoryVO = getInventory(skuId, storeId , isFromLocalRepository);
		logDebug("favStoreInventoryVO from BBBStoreInventoryManagerImpl.getFavStoreInventory(): " 
				+ favStoreInventoryVO);
		Map<String, Integer> stockAvailMap = new HashMap<String, Integer>();
		stockAvailMap.put(skuId, setInventoryStatus(favStoreInventoryVO, 1));
		logDebug("BBBStoreInventoryManagerImpl.getFavStoreInventory() - end "
				+ "with return params stockAvailMap: " + stockAvailMap);
		return stockAvailMap;
	}

	/**
	 * This method sets inventory status for bopus item.
	 * @param favStoreInventoryVO
	 * @return inventory status item in favorite store
	 * 			Available: 0, Limited: 1, Not Available: 2
	 */
	public int setInventoryStatus(final BBBStoreInventoryVO favStoreInventoryVO, 
			final long reqQty) {
		logDebug("BBBStoreInventoryManagerImp.setInventoryStatus() - start "
				+ "with params favStoreInventoryVO: " + favStoreInventoryVO);
		int inventoryStatus =  BBBInventoryManager.NOT_AVAILABLE;
		if (Double.compare(favStoreInventoryVO.getThresholdAvailable(),0.0) != BBBCoreConstants.ZERO) {
			
			
			if (favStoreInventoryVO.getStoreInventoryStock() - reqQty >= 
					favStoreInventoryVO.getThresholdAvailable()) {
				inventoryStatus = BBBInventoryManager.AVAILABLE;
				if(favStoreInventoryVO.getStoreInventoryStock() - reqQty< 
						favStoreInventoryVO.getThresholdAvailable()) {
					inventoryStatus = BBBInventoryManager.LIMITED_STOCK;
				}
			} else if (favStoreInventoryVO.getStoreInventoryStock() - reqQty >= favStoreInventoryVO.getThresholdLimited()) {
				inventoryStatus = BBBInventoryManager.LIMITED_STOCK;
			} else {
				inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
			}
		} else {
			logDebug("ThresholdAvailable is 0");
			if (favStoreInventoryVO.getStoreInventoryStock() - reqQty > 0) {
				inventoryStatus = BBBInventoryManager.AVAILABLE;
			} else {
				inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
			}
		}
		logDebug("BBBStoreInventoryManagerImp.setInventoryStatus() - end "
				+ "with return param inventory status: " + inventoryStatus);
		return inventoryStatus;
	}
	
	/**
	 * This method sets threshold for list of skus.
	 * @param siteId
	 * @param skuIds
	 * @param storeId
	 * @param inventoryBySku
	 * @return store inventory map
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Map<String, BBBStoreInventoryVO> setThresholdBySku(final String siteId, 
			final List<String> skuIds, final String storeId,
			final Map<String, Integer> inventoryBySku)
			throws BBBSystemException, BBBBusinessException {
		logDebug("BBBStoreInventoryManagerImp.setThresholdBySku() - start with params siteId: "
				+ siteId + "skuIds: " + storeId + "storeId: " + skuIds + "inventoryBySku: " + inventoryBySku);
		Map<String, BBBStoreInventoryVO> storeInventories = 
				new HashMap<String, BBBStoreInventoryVO>();
		
		if (skuIds != null) {
			for (String skuId : skuIds) {
				
				final ThresholdVO skuThresholdVO = getCatalogTools()
						.getSkuThreshold(siteId, skuId);
				
				final BBBStoreInventoryVO storeInventoryVO = new BBBStoreInventoryVO();

				
				if( null != inventoryBySku && inventoryBySku.size() > 0 && null != inventoryBySku.get(skuId)){
					storeInventoryVO.setStoreInventoryStock(
							inventoryBySku.get(skuId));
				} else{
						storeInventoryVO.setStoreInventoryStock(0);
				}
				if (skuThresholdVO != null) {
				storeInventoryVO.setThresholdAvailable(skuThresholdVO
						.getThresholdAvailable());
				storeInventoryVO.setThresholdLimited(skuThresholdVO
						.getThresholdLimited());
				}
				storeInventoryVO.setSkuId(skuId);
				storeInventories.put(skuId, storeInventoryVO);
			}
		}
		logDebug("BBBStoreInventoryManagerImp.setThresholdBySku() - end "
				+ "with return param storeInventories: " + storeInventories);
		return storeInventories;
	}

	/**
	 * @param req
	 * @param order
	 * @param siteId
	 * @param favoriteStoreId
	 * @return HashMap<String, Object>
	 * @throws Exception 
	 */
	public HashMap<String, Object> fetchFavStoreInvMultiSkus(final DynamoHttpServletRequest req,
			final BBBOrder order, String siteId, String favoriteStoreId)
			throws Exception {
		logDebug("BBBStoreInventoryManagerImpl.fetchFavStoreInvMultiSkus() - start");
		//If favorite store id is empty do not make inventory call.
		if (!BBBUtility.isEmpty(favoriteStoreId)) {
			List<CommerceItemVO> commerceItemVOList = (List<CommerceItemVO>) this.getCheckoutManager().getCartItemVOList(order);
		
			List<String> skuIdList = new ArrayList<String>();
			List<String> bopusEligibleCommItemsList = new ArrayList<String>();
			//Iterate through each of commerce item and to list which is bopus eligible.
			for (CommerceItemVO commerceItemVO : commerceItemVOList) {
				String skuId = commerceItemVO.getSkuDetailVO().getSkuId();
				if (checkCommItemBopusEligible(commerceItemVO)) {
					if (!skuIdList.contains(skuId)) {//not required
						skuIdList.add(skuId);
					}
					bopusEligibleCommItemsList.add(commerceItemVO.getBBBCommerceItem().getId());
				} 
			}
		
			//If none of sku is bopus eligible do not fetch store details and inventory status.
			if (!skuIdList.isEmpty()) {
				StoreDetails storeDetails = getSearchStoreManager().fetchFavStoreDetails(
						favoriteStoreId, siteId);
				//If store is ineligible for bopus do not make inventory call.
				if (storeDetails != null) {
					
					Map<String, Integer> favStoreInventory = 
							getBopusService().getMultiSkusStoreInventory(skuIdList, favoriteStoreId);
					
					final Map<String, BBBStoreInventoryVO> storeInventories =
						setThresholdBySku(siteId, skuIdList, favoriteStoreId, favStoreInventory);
					
					Map<String, Integer> favStoreInventoryStatus = 
							new HashMap<String, Integer>();
					
					for (CommerceItemVO commerceItemVO : commerceItemVOList) {
						String commItemId = commerceItemVO.getBBBCommerceItem().getId();
						if (bopusEligibleCommItemsList.contains(commItemId)) {
							String skuId = commerceItemVO.getSkuDetailVO().getSkuId();
							favStoreInventoryStatus.put(commItemId, 
									setInventoryStatus(storeInventories.get(skuId), 
											commerceItemVO.getBBBCommerceItem().getQuantity()));
						}
					}
					
					String channelApp = null;
						if (null != ServletUtil.getCurrentRequest()) {
							channelApp = ServletUtil.getCurrentRequest()
								.getHeader(BBBCoreConstants.CHANNEL);
						}
						if(!StringUtils.isBlank(channelApp) && (channelApp.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) 
								|| channelApp.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
							HashMap<String,Object> favStoreDetailMap = new HashMap<String,Object>();
							favStoreDetailMap.put(STORE_DETAILS, storeDetails);
							favStoreDetailMap.put(FAV_STORE_STOCK_STATUS, favStoreInventoryStatus);
							return favStoreDetailMap;
						}else{
							req.setParameter(STORE_DETAILS, storeDetails);
							req.setParameter(FAV_STORE_STOCK_STATUS, favStoreInventoryStatus);
						}						
				}
			}
		}
		logDebug("BBBStoreInventoryManagerImpl.fetchFavStoreInvMultiSkus() - end");
		return null;
	}


	/**
	 * This method checks given commerce item is bopus eligible or not.
	 * @param commerceItemVO
	 * @return
	 */
	public boolean checkCommItemBopusEligible(final CommerceItemVO commerceItemVO) {
		logDebug("BBBStoreInventoryManagerImpl.checkCommItemBopusEligible() - start "
				+ "with params commerceItemVO: " + commerceItemVO);
		return commerceItemVO !=null && !commerceItemVO.getSkuDetailVO().isLtlItem() && StringUtils.isEmpty(commerceItemVO.getBBBCommerceItem().getReferenceNumber()) && !commerceItemVO.getSkuDetailVO().isBopusAllowed();
	}

	/**
	 * This method sets favorite store inventory 
	 * status for commerce items on cart page for mobile.
	 * @param req
	 * @param profile
	 * @param order
	 * @throws Exception 
	 */
	public HashMap<String, Object> getRestFavStoreInvMultiSkus()
			throws Exception {
		logDebug("BBBStoreInventoryManagerImpl.getRestFavStoreInvMultiSkus() - start");
		final DynamoHttpServletRequest request = ServletUtil
				.getCurrentRequest();
		final Profile profile = (Profile) request
				.resolveName("/atg/userprofiling/Profile");
		final BBBSessionBean sessionBean = (BBBSessionBean) request
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final OrderHolder cart = (OrderHolder) request
				.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
		BBBOrder order = (BBBOrder) cart.getCurrent();
		if (!profile.isTransient()
				&& !sessionBean.isInternationalShippingContext()) {
			String siteId = extractCurrentSiteId();
			String favoriteStoreId = getSearchStoreManager()
					.fetchFavoriteStoreId(siteId, profile);
			logDebug("BBBStoreInventoryManagerImpl.favoriteStoreId" + favoriteStoreId);
			HashMap<String, Object> favStoreMap = fetchFavStoreInvMultiSkus(request,
					order, siteId, favoriteStoreId);
			return favStoreMap;
		}

		logDebug("BBBStoreInventoryManagerImpl.getRestFavStoreInvMultiSkus() - end");
		return null;
	}
	



	public SearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}

	public BBBCheckoutManager getCheckoutManager() {
		return checkoutManager;
	}

	public void setCheckoutManager(BBBCheckoutManager checkoutManager) {
		this.checkoutManager = checkoutManager;
	}

	public BBBStoreInventoryManager getStoreInventoryManager() {
		return storeInventoryManager;
	}

	public void setStoreInventoryManager(
			BBBStoreInventoryManager storeInventoryManager) {
		this.storeInventoryManager = storeInventoryManager;
	}
}
