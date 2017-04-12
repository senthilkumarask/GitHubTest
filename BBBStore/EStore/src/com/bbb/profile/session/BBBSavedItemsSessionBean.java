package com.bbb.profile.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.cart.droplet.StatusChangeMessageManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.checkout.droplet.IsProductSKUShippingDropletHelper;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;
import com.bbb.wishlist.GiftListVOWrapper;
import com.bbb.wishlist.manager.BBBGiftlistManager;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;

public class BBBSavedItemsSessionBean extends BBBGenericService {
	

	protected static final String WISH_LIST = "wishlist";
	
	private List<GiftListVO> mGiftListVO;
	private String mSiteContextPath;
	RepositoryItem mUserProfile;
	private BBBGiftlistManager mGiftlistManager;
	private String mWishListItemId;
	private RepositoryItem mMovedSavedItem;
	private int movedItemIndex;
	private BBBCommerceItem commerceItemToRemove;
	private int undoQuantity;
	private GiftListVO movedGiftlistItem;
	private long undoItemQuantity;
	private StatusChangeMessageManager mStatusManager;
	private BBBCatalogTools catalogTool;	
	private IsProductSKUShippingDropletHelper mHelper;
	
	private GiftRegistryManager giftRegistryManager;
	private BBBEximManager eximManager;
	
	/**
	 * @return the helper
	 */
	public IsProductSKUShippingDropletHelper getHelper() {
		return mHelper;
	}

	/**
	 * @param pHelper the helper to set
	 */
	public void setHelper(IsProductSKUShippingDropletHelper pHelper) {
		mHelper = pHelper;
	}

	/**
	 * @return the catalogTool
	 */
	public BBBCatalogTools getCatalogTool() {
		return catalogTool;
	}

	/**
	 * @param catalogTool the catalogTool to set
	 */
	public void setCatalogTool(BBBCatalogTools catalogTool) {
		this.catalogTool = catalogTool;
	}

	/**
	 * @return the statusManager
	 */
	public StatusChangeMessageManager getStatusManager() {
		return mStatusManager;
	}

	/**
	 * @param pStatusManager the statusManager to set
	 */
	public void setStatusManager(StatusChangeMessageManager pStatusManager) {
		mStatusManager = pStatusManager;
	}

	/**
	 * @return the undoQuantity
	 */
	public int getUndoQuantity() {
		return undoQuantity;
	}

	/**
	 * @param undoQuantity the undoQuantity to set
	 */
	public void setUndoQuantity(int undoQuantity) {
		this.undoQuantity = undoQuantity;
	}

	/**
	 * @return the commerceItemToRemove
	 */
	public BBBCommerceItem getCommerceItemToRemove() {
		return commerceItemToRemove;
	}

	/**
	 * @param commerceItemToRemove the commerceItemToRemove to set
	 */
	public void setCommerceItemToRemove(BBBCommerceItem commerceItemToRemove) {
		this.commerceItemToRemove = commerceItemToRemove;
	}

	/**
	 * @return the movedItemIndex
	 */
	public int getMovedItemIndex() {
		return movedItemIndex;
	}

	/**
	 * @param movedItemIndex the movedItemIndex to set
	 */
	public void setMovedItemIndex(int movedItemIndex) {
		this.movedItemIndex = movedItemIndex;
	}

	/**
	 * @return the mMovedSavedItem
	 */
	public RepositoryItem getMovedSavedItem() {
		return mMovedSavedItem;
	}

	/**
	 * @param mMovedSavedItem the mMovedSavedItem to set
	 */
	public void setMovedSavedItem(RepositoryItem mMovedSavedItem) {
		this.mMovedSavedItem = mMovedSavedItem;
	}
	
	/**
	 * @return the mWishListItemId
	 */
	public String getWishListItemId() {
		return mWishListItemId;
	}

	/**
	 * @param mWishListItemId the mWishListItemId to set
	 */
	public void setWishListItemId(String mWishListItemId) {
		this.mWishListItemId = mWishListItemId;
	}

	/**
	 * @return the movedGiftlistItem
	 */
	public GiftListVO getMovedGiftlistItem() {
		return movedGiftlistItem;
	}

	/**
	 * @param movedGiftlistItem the movedGiftlistItem to set
	 */
	public void setMovedGiftlistItem(GiftListVO movedGiftlistItem) {
		this.movedGiftlistItem = movedGiftlistItem;
	}

	public BBBGiftlistManager getGiftlistManager() {
		return mGiftlistManager;
	}

	public void setGiftlistManager(BBBGiftlistManager giftlistManager) {
		mGiftlistManager = giftlistManager;
	}
	
	public String getSiteContextPath() {
		return mSiteContextPath;
	}

	public void setSiteContextPath(String siteContextPath) {
		mSiteContextPath = siteContextPath;
	}

	/**
	 * @return the userProfile
	 */
	public RepositoryItem getUserProfile() {
		return mUserProfile;
	}

	/**
	 * @param pRepositoryItem
	 *            the userProfile to set
	 */
	public void setUserProfile(RepositoryItem userprofile) {
		mUserProfile = userprofile;
	}
	
		public List<GiftListVO> getGiftListVO() {
		return mGiftListVO;
	}

	public void setGiftListVO(List<GiftListVO> giftListVO) {
		this.mGiftListVO = giftListVO;
	}

	public List<GiftListVO> getItems()
	{
		return getSaveItems(false);
	}
	
	public BBBEximManager getEximManager() {
		return eximManager;
	}

	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}

	public List<?> fetchBatchForSavedItems(GiftListVOWrapper giftListVOWrapper,List<?> vo, int fromSflIndex){
		
		int size = vo.size();
		List<?> sublist;
		giftListVOWrapper.setSize(size);
		giftListVOWrapper.setFromSflIndex(-1);
		List<String> batchSizeKeys = null;
		String batchSizekey;
		int batchSize = 0;
		try {
			batchSizeKeys = this.getCatalogTool().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,
					BBBCmsConstants.GIFTLIST_BATCH_SIZE);
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Exception occured while fetching config key", e);
		}
		if (null != batchSizeKeys) {
			batchSizekey = batchSizeKeys.get(0);
			try {
				batchSize = Integer.parseInt(batchSizekey);
			} catch (NumberFormatException nfe) {
				logError("Invalid Number format:" + batchSizekey, nfe);
			}

		}
		int toIndex = fromSflIndex + batchSize;
		boolean hasMoreItems = false;
		int remaining = size - toIndex ;

		if (remaining > 0) {
			hasMoreItems = true;
			giftListVOWrapper.setFromSflIndex(toIndex);
		}
		giftListVOWrapper.setHasMoreItems(hasMoreItems);
		
		int reverseToIndex = size-fromSflIndex;
		int reverseFromIndex = reverseToIndex-batchSize;
		
		if(reverseFromIndex <0){
			reverseFromIndex = 0;
		}
		
		if(reverseToIndex<0){
			reverseToIndex = 0;
		}
		
		sublist = vo.subList(reverseFromIndex, reverseToIndex);

		return sublist;

	}
	
	
	@SuppressWarnings("unchecked")
	public  GiftListVOWrapper getSaveItems(boolean showItemMSG, int fromSflIndex)
	{
		List<GiftListVO> newList = new ArrayList<GiftListVO>();
		GiftListVOWrapper giftListVOWrapper = new GiftListVOWrapper();
		if(getUserProfile().isTransient())
		{
			List<GiftListVO> originalVo = getGiftListVO();
			if(originalVo!=null) {
				List<GiftListVO> vo = (List<GiftListVO>) fetchBatchForSavedItems(giftListVOWrapper,originalVo,fromSflIndex); 
				giftListVOWrapper.setGiftListVOlist(vo);
			}
			Map<String, RegistryVO> registryMap = new HashMap<String, RegistryVO>();
			if (giftListVOWrapper.getGiftListVOlist() !=null) {
				for(GiftListVO tempvo:giftListVOWrapper.getGiftListVOlist()){
					try {
						tempvo.setSkuVO(getCatalogTool().getSKUDetails(SiteContextManager.getCurrentSiteId(), tempvo.getSkuID()));
						if(showItemMSG){
							tempvo.setPriceMessageVO(getStatusManager().checkSavedItemMessages(tempvo));
						}
						
					}catch (BBBSystemException e) {
						logError("Error getting item details in BBBSavedItemsSessionBean", e);
					} catch (BBBBusinessException e) {
						logError("Error getting item details in BBBSavedItemsSessionBean", e);					
					}
					try {
						tempvo.setProductVO(getCatalogTool().getProductDetails(SiteContextManager.getCurrentSiteId(), tempvo.getProdID(),false,true,false));
					} catch (BBBSystemException e) {
						logError("Error getting product details in BBBSavedItemsSessionBean", e);
					} catch (BBBBusinessException e) {
						logError("Error getting product details in BBBSavedItemsSessionBean", e);
					}
					//Set Price Info
					try {
						setPriceInfo(tempvo);
					} catch (BBBSystemException e) {
						logError("Error getting product details in BBBSavedItemsSessionBean", e);
					} catch (BBBBusinessException e) {
						logError("Error getting product details in BBBSavedItemsSessionBean", e);
					}
					//set Active Deals
					try {
						Map<String,AttributeVO> restrictionZipCodeAttributes = getHelper().getAttribute(SiteContextManager.getCurrentSiteId(), tempvo.getSkuID(), tempvo.getProdID());
						if(restrictionZipCodeAttributes !=null && !restrictionZipCodeAttributes.isEmpty()){
							tempvo.setAttribute(restrictionZipCodeAttributes);
						}
					} catch (ServletException e) {
						logError("Error getting Active Deals in BBBSavedItemsSessionBean", e);
					} catch (IOException e) {
						logError("Error getting Active Deals in BBBSavedItemsSessionBean", e);
					}
					
					if (!StringUtils.isEmpty(tempvo.getRegistryID())) {
						RegistryVO registryVO;
						if(registryMap.containsKey(tempvo.getRegistryID())){
							tempvo.setRegistryVO(registryMap.get(tempvo.getRegistryID()));
						} else {
							try {
								registryVO = giftRegistryManager.getRegistryDetailInfo(tempvo.getRegistryID(), SiteContextManager.getCurrentSiteId());
								tempvo.setRegistryVO(registryVO);
								registryMap.put(tempvo.getRegistryID(), registryVO);
							} catch (BBBBusinessException e) {
								logError("Error getting product details in BBBSavedItemsSessionBean", e);
							} catch (BBBSystemException e) {
								logError("Error getting product details in BBBSavedItemsSessionBean", e);
							}
						}
						
				
					}						
					if (((SKUDetailVO)tempvo.getSkuVO()).isLtlItem() && BBBUtility.isNotEmpty(tempvo.getLtlShipMethod())){
					
							try {
								tempvo.setDeliverySurcharge(getCatalogTool().getDeliveryCharge(SiteContextManager.getCurrentSiteId(), tempvo.getSkuID(), tempvo.getLtlShipMethod())
										* tempvo.getQuantity());
								if(tempvo.getLtlShipMethod().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
								tempvo.setAssemblyFees(getCatalogTool().getAssemblyCharge(SiteContextManager.getCurrentSiteId(), tempvo.getSkuID())
										* tempvo.getQuantity());
				}
								RepositoryItem shippingMethod = getCatalogTool().getShippingMethod(tempvo.getLtlShipMethod());
								String shippingMethodDesc = (String) shippingMethod.getPropertyValue("shipMethodDescription");
								tempvo.setLtlShipMethodDesc(shippingMethodDesc);
							} catch (BBBBusinessException e) {
								logError("Error getting product details in BBBSavedItemsSessionBean", e);
							} catch (BBBSystemException e) {
								logError("Error getting product details in BBBSavedItemsSessionBean", e);
			}
						
						
				
					}
					
				}
			}
			return giftListVOWrapper;
		} else {
			//if logged in user
			RepositoryItem wishList = ((RepositoryItem) getUserProfile().getPropertyValue(WISH_LIST));
			if (wishList != null) {
				List<RepositoryItem> wishtListItemsAll = (List<RepositoryItem>) (wishList
						.getPropertyValue(BBBCoreConstants.GIFT_LIST_ITEMS));
				List<RepositoryItem> wishtListItems = (List<RepositoryItem>) fetchBatchForSavedItems(giftListVOWrapper,
						wishtListItemsAll, fromSflIndex);
				if (null != wishtListItems) {
					newList = convertItemsToGiftListVO(wishtListItems, wishList, showItemMSG);

				}
				giftListVOWrapper.setGiftListVOlist(newList);
			}
		}
		return giftListVOWrapper;
	}	
	
	
	@SuppressWarnings("unchecked")
	public List<GiftListVO> getSaveItems(boolean showItemMSG)
	{
		List<GiftListVO> newList = new ArrayList<GiftListVO>();
		if(getUserProfile().isTransient())
		{
			
			List<GiftListVO> vo = getGiftListVO();
			Map<String, RegistryVO> registryMap = new HashMap<String, RegistryVO>();
			if (vo !=null) {
				for(GiftListVO tempvo:vo){
					try {
						tempvo.setSkuVO(getCatalogTool().getSKUDetails(SiteContextManager.getCurrentSiteId(), tempvo.getSkuID()));
						if(showItemMSG){
							tempvo.setPriceMessageVO(getStatusManager().checkSavedItemMessages(tempvo));
						}
						
					}catch (BBBSystemException e) {
						logError("Error getting item details in BBBSavedItemsSessionBean", e);
					} catch (BBBBusinessException e) {
						logError("Error getting item details in BBBSavedItemsSessionBean", e);					
					}
					try {
						tempvo.setProductVO(getCatalogTool().getProductDetails(SiteContextManager.getCurrentSiteId(), tempvo.getProdID(),false,true,false));
					} catch (BBBSystemException e) {
						logError("Error getting product details in BBBSavedItemsSessionBean", e);
					} catch (BBBBusinessException e) {
						logError("Error getting product details in BBBSavedItemsSessionBean", e);
					}
					//Set Price Info
					try {
						setPriceInfo(tempvo);
					} catch (BBBSystemException e) {
						logError("Error getting product details in BBBSavedItemsSessionBean", e);
					} catch (BBBBusinessException e) {
						logError("Error getting product details in BBBSavedItemsSessionBean", e);
					}
					//set Active Deals
					try {
						Map<String,AttributeVO> restrictionZipCodeAttributes = getHelper().getAttribute(SiteContextManager.getCurrentSiteId(), tempvo.getSkuID(), tempvo.getProdID());
						if(restrictionZipCodeAttributes !=null && !restrictionZipCodeAttributes.isEmpty()){
							tempvo.setAttribute(restrictionZipCodeAttributes);
						}
					} catch (ServletException e) {
						logError("Error getting Active Deals in BBBSavedItemsSessionBean", e);
					} catch (IOException e) {
						logError("Error getting Active Deals in BBBSavedItemsSessionBean", e);
					}
					
					if (!StringUtils.isEmpty(tempvo.getRegistryID())) {
						RegistryVO registryVO;
						if(registryMap.containsKey(tempvo.getRegistryID())){
							tempvo.setRegistryVO(registryMap.get(tempvo.getRegistryID()));
						} else {
							try {
								registryVO = giftRegistryManager.getRegistryDetailInfo(tempvo.getRegistryID(), SiteContextManager.getCurrentSiteId());
								tempvo.setRegistryVO(registryVO);
								registryMap.put(tempvo.getRegistryID(), registryVO);
							} catch (BBBBusinessException e) {
								logError("Error getting product details in BBBSavedItemsSessionBean", e);
							} catch (BBBSystemException e) {
								logError("Error getting product details in BBBSavedItemsSessionBean", e);
							}
						}
						
				
					}						
					if (((SKUDetailVO)tempvo.getSkuVO()).isLtlItem() && BBBUtility.isNotEmpty(tempvo.getLtlShipMethod())){
					
							try {
								tempvo.setDeliverySurcharge(getCatalogTool().getDeliveryCharge(SiteContextManager.getCurrentSiteId(), tempvo.getSkuID(), tempvo.getLtlShipMethod())
										* tempvo.getQuantity());
								if(tempvo.getLtlShipMethod().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
								tempvo.setAssemblyFees(getCatalogTool().getAssemblyCharge(SiteContextManager.getCurrentSiteId(), tempvo.getSkuID())
										* tempvo.getQuantity());
				}
								RepositoryItem shippingMethod = getCatalogTool().getShippingMethod(tempvo.getLtlShipMethod());
								String shippingMethodDesc = (String) shippingMethod.getPropertyValue("shipMethodDescription");
								tempvo.setLtlShipMethodDesc(shippingMethodDesc);
							} catch (BBBBusinessException e) {
								logError("Error getting product details in BBBSavedItemsSessionBean", e);
							} catch (BBBSystemException e) {
								logError("Error getting product details in BBBSavedItemsSessionBean", e);
			}
						
						
				
					}
					
				}
			}
			return vo;
		} else {
			//if logged in user
			RepositoryItem wishList = ((RepositoryItem) getUserProfile().getPropertyValue(WISH_LIST));
			if( wishList !=null){
				List<RepositoryItem> wishtListItems = (List<RepositoryItem>)(wishList.getPropertyValue(BBBCoreConstants.GIFT_LIST_ITEMS));
				newList  = convertItemsToGiftListVO(wishtListItems,wishList,showItemMSG);
				}
			}
		return newList;
	}
	
	private List<GiftListVO> convertItemsToGiftListVO(List<RepositoryItem> wishtListItems, RepositoryItem wishList,boolean showItemMSG){
		
		List<GiftListVO> listGiftListVO = new ArrayList<GiftListVO>();
		Map<String, RegistryVO> registryMap = new HashMap<String, RegistryVO>();

		for (RepositoryItem item : wishtListItems)
		{
			// Check for sister site too to share between TBS & WEB
			String sisterSiteId = SiteContextManager.getCurrentSiteId();
			if( sisterSiteId.startsWith("TBS") ) {
				sisterSiteId = sisterSiteId.substring(4);
			}
			else {
				sisterSiteId = "TBS_" + sisterSiteId;
			}
			String itemSiteId = (String)item.getPropertyValue("siteId");
			
			if(null != itemSiteId && (itemSiteId.equals(SiteContextManager.getCurrentSiteId()) || itemSiteId.equals(sisterSiteId)) )  {
				GiftListVO newItem = new GiftListVO();
				newItem.setSiteId(itemSiteId);
				newItem.setGiftRepositoryItem(item);
				newItem.setProdID((String) item.getPropertyValue(BBBCoreConstants.PRODUCTID));
				newItem.setSkuID((String) item.getPropertyValue(BBBCoreConstants.CATALOG_REF_ID));
				newItem.setRegistryID((String) item.getPropertyValue(BBBCoreConstants.REGISTRY_ID));
				newItem.setLtlShipMethod((String) item.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD));
				newItem.setQuantity(Long.parseLong(String.valueOf(item.getPropertyValue(BBBCoreConstants.QUANTITY_DESIRED))));
//				newItem.setPrice((String) item.getPropertyValue("registryId"));
				newItem.setQtyPurchased(String.valueOf(item.getPropertyValue(BBBCoreConstants.QUANTITY_PURCHASED)));
				newItem.setQtyRequested(String.valueOf(item.getPropertyValue(BBBCoreConstants.QUANTITY_DESIRED)));
				newItem.setBts((Boolean)item.getPropertyValue(BBBCoreConstants.IS_BTS));
				Boolean flagoff = (Boolean)item.getPropertyValue(BBBCoreConstants.MSGSHOWNFLAGOFF);
				Boolean shipMethodUnsupported = (Boolean)item.getPropertyValue(BBBCoreConstants.MSGSHIPMETHODUNSUPPORTED);
				newItem.setShipMethodUnsupported(shipMethodUnsupported);
			    String siteId = SiteContextManager.getCurrentSiteId();
			    
			    //Set LTL Shipping Method Description and DeliverSurcharge and Assembly Fees
			    try {
				boolean isSkuLtl = getCatalogTool().isSkuLtl(siteId, newItem.getSkuID());
				if(isSkuLtl && BBBUtility.isNotEmpty(newItem.getLtlShipMethod())) {
				  if(newItem.getLtlShipMethod().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
					 newItem.setAssemblyFees(getCatalogTool().getAssemblyCharge(siteId, newItem.getSkuID()) * newItem.getQuantity());
				  }
				  RepositoryItem shippingMethod = getCatalogTool().getShippingMethod(newItem.getLtlShipMethod());
				  String shippingMethodDesc = (String) shippingMethod.getPropertyValue("shipMethodDescription");
				  newItem.setLtlShipMethodDesc(shippingMethodDesc);
				  newItem.setDeliverySurcharge(getCatalogTool().getDeliveryCharge(siteId, newItem.getSkuID(), newItem.getLtlShipMethod())* newItem.getQuantity());
				}
			    } catch (BBBSystemException e) {
					logError("Error getting product details in BBBSavedItemsSessionBean", e);
				} catch (BBBBusinessException e) {
					logError("Error getting product details in BBBSavedItemsSessionBean", e);
				}
			    
				if(flagoff == null){
					flagoff= false;
				}
				newItem.setMsgShownFlagOff(flagoff);
				Boolean msgShownOOS = (Boolean)item.getPropertyValue(BBBCoreConstants.MSGSHOWNOOS);
				if(msgShownOOS == null){
					msgShownOOS= false;
				}
				newItem.setMsgShownOOS(msgShownOOS);
				Double price = (Double)item.getPropertyValue(BBBCoreConstants.PREVIOUSPRICE);
				if(price != null) {
					newItem.setPrevPrice(price);
				}
				newItem.setWishListItemId(item.getRepositoryId());
				newItem.setGiftListId(wishList.getRepositoryId());
				boolean isSkuPersonalized = false;
				double personalizePrice = 0.0;
				/*	BPSI-3285 DSK | Handle Pricing message for Personalized Item*/
				//Start: BPSL-1963 | EXIM- Populate personalized item details
				if(null != item.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER)){
					isSkuPersonalized = true;
					personalizePrice = (Double)item.getPropertyValue(BBBCoreConstants.PERSONALIZE_PRICE);
					newItem.setReferenceNumber((String)item.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER));
					newItem.setPersonalizePrice((Double)item.getPropertyValue(BBBCoreConstants.PERSONALIZE_PRICE));
					newItem.setPersonalizationDetails((String)item.getPropertyValue(BBBCoreConstants.PERSONALIZATION_DETAILS));
					newItem.setPersonalizationOptions((String)item.getPropertyValue(BBBCoreConstants.PERSONALIZATION_OPTIONS));
					newItem.setFullImagePath((String)item.getPropertyValue(BBBCoreConstants.FULL_IMAGE_PATH));
					newItem.setThumbnailImagePath((String)item.getPropertyValue(BBBCoreConstants.THUMBNAIL_IMAGE_PATH));
					newItem.setMobileFullImagePath((String)item.getPropertyValue(BBBCoreConstants.MOBILE_FULL_IMAGE_PATH));
					newItem.setMobileThumbnailImagePath((String)item.getPropertyValue(BBBCoreConstants.MOBILE_THUMBNAIL_IMAGE_PATH));
					newItem.setPersonalizationStatus((String)item.getPropertyValue(BBBCoreConstants.PERSONALIZATION_STATUS));
					//BPSI-5386 Personalization option single code display
					newItem.setPersonalizationOptionsDisplay(getEximManager().getPersonalizedOptionsDisplayCode(newItem.getPersonalizationOptions()));
				}
				
				//Set Messaging Info
				try {
					SKUDetailVO skuDetailVO = getCatalogTool().getSKUDetails(SiteContextManager.getCurrentSiteId(), newItem.getSkuID());
					newItem.setSkuVO(skuDetailVO);
					if(isSkuPersonalized){
						getCatalogTool().updateShippingMessageFlag(skuDetailVO, isSkuPersonalized, personalizePrice);
					}
					if(showItemMSG){
						newItem.setPriceMessageVO(getStatusManager().checkSavedItemMessages(newItem));
					} 
					//newItem.setSkuVO(getCatalogTool().getSKUDetails(SiteContextManager.getCurrentSiteId(), newItem.getSkuID()));
				} catch (BBBSystemException e) {
					logError("Error getting item details in BBBSavedItemsSessionBean", e);
				} catch (BBBBusinessException e) {
					logError("Error getting item details in BBBSavedItemsSessionBean", e);
				}
				//Set Product Info
				try {
					newItem.setProductVO(getCatalogTool().getProductDetails(SiteContextManager.getCurrentSiteId(), newItem.getProdID(),false,true,false));
				} catch (BBBSystemException e) {
					logError("Error getting product details in BBBSavedItemsSessionBean", e);
				} catch (BBBBusinessException e) {
					logError("Error getting product details in BBBSavedItemsSessionBean", e);
				}
				//End: BPSL-1963 | EXIM- Populate personalized item details
				//Set Price Info

				try {
					setPriceInfo(newItem);
				} catch (BBBSystemException e) {
					logError("Error getting product details in BBBSavedItemsSessionBean", e);
				} catch (BBBBusinessException e) {
					logError("Error getting product details in BBBSavedItemsSessionBean", e);
				}
				//set Active Deals
				try {
					Map<String,AttributeVO> restrictionZipCodeAttributes = getHelper().getAttribute(SiteContextManager.getCurrentSiteId(), newItem.getSkuID(), newItem.getProdID());
					if(restrictionZipCodeAttributes !=null && !restrictionZipCodeAttributes.isEmpty()){
						newItem.setAttribute(restrictionZipCodeAttributes);
					}
				} catch (ServletException e) {
					logError("Error getting Active Deals in BBBSavedItemsSessionBean", e);
				} catch (IOException e) {
					logError("Error getting Active Deals in BBBSavedItemsSessionBean", e);
				}
				
				// Added below NULL string check as some SFL item have registry id set null string
				if (!StringUtils.isEmpty(newItem.getRegistryID()) && !newItem.getRegistryID().equalsIgnoreCase(BBBCoreConstants.NULL_VALUE)) {
					RegistryVO registryVO;
					if(registryMap.containsKey(newItem.getRegistryID())){
						newItem.setRegistryVO(registryMap.get(newItem.getRegistryID()));
					} else {
						try {
							registryVO = giftRegistryManager.getRegistryDetailInfo(newItem.getRegistryID(), SiteContextManager.getCurrentSiteId());
							newItem.setRegistryVO(registryVO);
							registryMap.put(newItem.getRegistryID(), registryVO);
						} catch (BBBBusinessException e) {
							logError("Error getting item details in BBBSavedItemsSessionBean", e);
						} catch (BBBSystemException e) {
							logError("Error getting item details in BBBSavedItemsSessionBean", e);
						}
					}
				}	
					
				//End: BPSL-1963 | EXIM- Populate personalized item details
				listGiftListVO.add(newItem);
			}			
		}
		return listGiftListVO;
	}

	/**
	 * @return the undoItemQuantity
	 */
	public long getUndoItemQuantity() {
		return undoItemQuantity;
	}

	/**
	 * @param undoItemQuantity the undoItemQuantity to set
	 */
	public void setUndoItemQuantity(long undoItemQuantity) {
		this.undoItemQuantity = undoItemQuantity;
	}
	
	/**
	 * Sets Price Info
	 * @param newItem
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void setPriceInfo(GiftListVO newItem) throws BBBSystemException, BBBBusinessException{
		Double listPrice = ((BBBCatalogTools) getStatusManager().getBbbCatalogTools()).getListPrice(newItem.getProdID(), newItem.getSkuID());
		Double salePrice = ((BBBCatalogTools)  getStatusManager().getBbbCatalogTools()).getSalePrice(newItem.getProdID(), newItem.getSkuID());

		boolean onSale = ((BBBCatalogTools)  getStatusManager().getBbbCatalogTools()).isSkuOnSale(newItem.getProdID(), newItem.getSkuID());
		//BBBH-3982 - For displaying message for incart eligible skus on SFL email
		final boolean skuInCartFlag = getStatusManager().getBbbCatalogTools().getSkuIncartFlag(newItem.getSkuID());
		newItem.setSkuIncartFlag(skuInCartFlag);
		
		// BBBH-3978 - for displaying incart price for eligible sku on saved item email only for TBS
		if(SiteContextManager.getCurrentSiteId().contains(BBBCoreConstants.TBS)){
			logDebug("Inside condition for repalcing incart price for : listPrice" + listPrice + "salePrice" + salePrice + "onSale" + onSale + "skuID" + newItem.getSkuID() + "productId" + newItem.getProdID());
			if(skuInCartFlag){
				Double inCartPrice = ((BBBCatalogTools)  getStatusManager().getBbbCatalogTools()).getIncartPrice
						(newItem.getProdID(), newItem.getSkuID());
				
				if(onSale){
					salePrice = inCartPrice;
				} else {
					listPrice = inCartPrice;
				}
			}
			logDebug("skuInCartFlag"+ skuInCartFlag + "updated salePrice" + salePrice + "listPrice"+ listPrice);
		
		}
		
		Double currentPrice;
		Double personalizePrice = newItem.getPersonalizePrice();		
		//BPSI 3291- SFL integration on PDP(Mobile) & Pricing changes as per new EXIM pricing logic
		
		currentPrice = BBBUtility.checkCurrentPriceForSavedItem(onSale, listPrice, salePrice,((SKUDetailVO)newItem.getSkuVO()).getPersonalizationType(), personalizePrice, newItem.getReferenceNumber());
		//Need to confirm fom nishant 
		newItem.setPrice(currentPrice.toString());
		newItem.setTotalPrice(currentPrice*newItem.getQuantity());
	}
	
	
	public GiftRegistryManager getGiftRegistryManager() {
		return giftRegistryManager;
	}

	public void setGiftRegistryManager(GiftRegistryManager giftRegistryManager) {
		this.giftRegistryManager = giftRegistryManager;
	}
}
