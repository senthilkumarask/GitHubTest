package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.cart.PricingMessageVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;
import com.bbb.wishlist.manager.BBBGiftlistManager;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.Order;
import atg.commerce.pricing.ItemPriceInfo;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

/**
 * This class helps to check inventory and price change based on that gives a
 * Price Message vo which holds the response
 *
 * @author akhaju
 *
 */
public class StatusChangeMessageManager extends BBBGenericService {

	private BBBCatalogTools mBbbCatalogTools;
	private BBBInventoryManager mInventoryManager;
	private MutableRepository mCatalogRepository;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBGiftlistManager mGiftListManager;

	public static final String MESSAGE = "message";
	public static final String PARENTCATEGORY = "parentcategory";
	public static final String PRICEMESSAGEVO = "priceMessageVO";
	private CatalogTools mCatalogTools;
	private BBBOrderManager mOrderManager;
	private ProductManager mProductManager;
	public static final String PRICECHANGEVO = "priceChangeVO";

	/**
	 * @param pProductManager
	 *            the productManager to set
	 */
	public void setProductManager(ProductManager pProductManager) {
		mProductManager = pProductManager;
	}

	/**
	 * @return the catalogTools
	 */
	public CatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(CatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the bbbCatalogTools
	 */
	public BBBCatalogTools getBbbCatalogTools() {
		return mBbbCatalogTools;
	}

	/**
	 * @param pBbbCatalogTools
	 *            the bbbCatalogTools to set
	 */
	public void setBbbCatalogTools(BBBCatalogTools pBbbCatalogTools) {
		mBbbCatalogTools = pBbbCatalogTools;
	}

	/**
	 * @return the giftListManager
	 */
	public BBBGiftlistManager getGiftListManager() {
		return mGiftListManager;
	}

	/**
	 * @param giftListManager
	 *            the giftListManager to set
	 */
	public void setGiftListManager(BBBGiftlistManager giftListManager) {
		this.mGiftListManager = giftListManager;
	}

	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return mCatalogRepository;
	}

	/**
	 * @param catalogRepository
	 *            the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.mCatalogRepository = catalogRepository;
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
	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.mInventoryManager = inventoryManager;
	}

	/**
	 * This method call checkCartItemMessage/checkSavedItemMessages based on
	 * request which furthur checks inventory and price change
	 *
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 */
	/*@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		String methodName = "StatusChangeMessageDroplet.service";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.STATUS_CHANGE_MESSAGE, methodName);

		if (isLoggingDebug()) {
			logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[service ends]");
		}
		try {
			String reqType = pRequest.getParameter(BBBCoreConstants.REQTYPE);
			if (reqType != null && reqType.equalsIgnoreCase(BBBCoreConstants.CART)) {
				// check messages for cart Items
				checkCartItemMessage(pRequest, pResponse);
			} else if (reqType != null && reqType.equalsIgnoreCase(BBBCoreConstants.WISHLIST)) {
				// check messages for saved Items

				List<GiftListVO> giftListVO = null;
				BBBSavedItemsSessionBean bean = (BBBSavedItemsSessionBean) pRequest.getObjectParameter(BBBCoreConstants.SESSIONBEAN);
				if (bean == null) {
					giftListVO = getSavedItemsSessionBean().getItems();
				} else {
					giftListVO = getSavedItemsSessionBean().getItems();
					if (giftListVO == null) {
						bean = (BBBSavedItemsSessionBean) pRequest.resolveName(BBBCoreConstants.SAVEDCOMP);
						giftListVO = bean.getItems();
					}
				}
				checkSavedItemMessages(giftListVO, pRequest, pResponse);
			}

		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException"), e);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException"), e);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException"), e);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		} catch (CommerceException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException"), e);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		}

		if (isLoggingDebug()) {
			logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[service ends]");
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.STATUS_CHANGE_MESSAGE, methodName);

	}*/

	private BBBSavedItemsSessionBean getSavedItemsSessionBean() {
		BBBSavedItemsSessionBean bean = (BBBSavedItemsSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SAVEDCOMP);
		return bean;
	}



	/**
	 * Check inventory and price change based on that return PricingMessageVO
	 * for cart section
	 *
	 * @param bbbCommerceItem
	 * @param pRequest
	 * @param pOrder
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 */
	public PricingMessageVO checkCartItemMessage(BBBCommerceItem bbbCommerceItem, DynamoHttpServletRequest pRequest, Order pOrder) throws BBBSystemException, BBBBusinessException, CommerceException{

		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage starts]");
		int inStockStatus = 0 ;
		Double currentPrice = 0.0;
		SiteContext siteContext = (SiteContext)ServletUtil.getCurrentRequest().resolveName("/atg/multisite/SiteContext");
		String siteId = siteContext.getSite().getId();

		boolean inStock = true;
		
		logDebug("bbbCommerceItem Recieved " + bbbCommerceItem);
		
		boolean doUpdate = false;
		if (bbbCommerceItem != null && bbbCommerceItem instanceof BBBCommerceItem) {

			PricingMessageVO vo = new PricingMessageVO();

			String productId = bbbCommerceItem.getAuxiliaryData().getProductId();

			String skuId = bbbCommerceItem.getCatalogRefId();

			String registryId = bbbCommerceItem.getRegistryId();
			vo.setQuantity(bbbCommerceItem.getQuantity());

			vo.setSkuId(skuId);
			vo.setProdId(productId);
			vo.setCommerceItemId(bbbCommerceItem.getId());
			String parentCategory = getBbbCatalogTools().getShopSimilarItemCategory(productId, siteId);
			vo.setParentCat(parentCategory);
			try{
				if(!BBBUtility.isEmpty(parentCategory)){
					vo.setParentCatName(getBbbCatalogTools().getCategoryDetail(SiteContextManager.getCurrentSiteId(), parentCategory,false).getCategoryName());
					vo.setParentSeoUrl(getBbbCatalogTools().getCategoryDetail(SiteContextManager.getCurrentSiteId(), parentCategory,false).getSeoURL());
				}
			}catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException - Issue in getting Category"), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException - Issue in getting Category"), e);
			}
			SKUDetailVO skuVO = getBbbCatalogTools().getSKUDetails(SiteContextManager.getCurrentSiteId(), skuId);
			vo.setBopus(skuVO.isBopusAllowed());
			ItemPriceInfo itemPriceInfo = bbbCommerceItem.getPriceInfo();
			Double listPrice = itemPriceInfo.getListPrice();
			Double salePrice = itemPriceInfo.getSalePrice();

			boolean onSale = itemPriceInfo.isOnSale();
			currentPrice = BBBUtility.checkCurrentPrice(onSale, listPrice, salePrice);
			vo.setCurrentPrice(currentPrice);
			try {
				boolean isSkuActive = false;

				try {
					isSkuActive = getBbbCatalogTools().isSkuActive(skuId);
				} catch (RepositoryException e) {
					logError("Repository Exception in checkCartItemMessage SKU Active:", e);
				}
				if (isSkuActive) {

					if (!BBBUtility.isEmpty(registryId)) {
						vo.setRegistryId(registryId);
						inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.ADD_ITEM_FROM_REG, bbbCommerceItem.getQuantity());
					} else {
						inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.PRODUCT_DISPLAY, bbbCommerceItem.getQuantity());
					}
					if (inStockStatus == BBBInventoryManager.NOT_AVAILABLE) {
						inStock = false;
					}
					if (!inStock && BBBUtility.isEmpty(bbbCommerceItem.getStoreId())) {
						vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
						vo.setInStock(false);
						logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage ends]");
						
					} else {
						vo.setInStock(true);
					}
				} else {
					vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
					vo.setFlagOff(true);
					pRequest.setParameter(PRICEMESSAGEVO, vo);					
					logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage ends]");
					
					return vo;
				}
					
				if (bbbCommerceItem.getPrevPrice() != currentPrice && Double.compare(bbbCommerceItem.getPrevPrice(), 0.0) != BBBCoreConstants.ZERO) {
					String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
					vo.setPrevPrice(bbbCommerceItem.getPrevPrice());
					vo.setPriceChange(true);
					vo.setCurrentPrice(currentPrice);
					if (!(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel) || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel))) {
						bbbCommerceItem.setPrevPrice(currentPrice);
					}
					doUpdate = true;
				}

			} finally {
				if(doUpdate) {
					updateOrder(pOrder, pRequest);
				}
			}
			return vo;
		} else {
			return null;
		}

	}

	/**
	 * Update Order
	 *
	 * @param order
	 * @param pRequest
	 */
	private void updateOrder(Order order, DynamoHttpServletRequest pRequest) throws CommerceException {
		try {
			synchronized (order) {
				getOrderManager().updateOrder(order);
			}
		} catch (CommerceException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GET_ATG_ORDER_DETAILS_1001, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
			
			throw e;
		}
	}

	/**
	 * @return the orderManager
	 */
	public BBBOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @param pOrderManager
	 *            the orderManager to set
	 */
	public void setOrderManager(BBBOrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	/**
	 * Check inventory and price change based on that return PricingMessageVO
	 * for saved item section
	 *
	 * @param giftListVO
	 * @param pRequest
	 * @param pResponse
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException
	 */
	public PricingMessageVO checkSavedItemMessages(GiftListVO giftListVO) throws BBBSystemException, BBBBusinessException {
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages starts]");
		
		double currentPrice = 0.0;
		boolean inStock = true;
		int inStockStatus;
		SiteContext siteContext = (SiteContext)ServletUtil.getCurrentRequest().resolveName("/atg/multisite/SiteContext");
		String siteId = siteContext.getSite().getId();


		if (giftListVO != null) {
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			PricingMessageVO vo = new PricingMessageVO();

			vo.setWishListId(giftListVO.getWishListItemId());
			String productId = giftListVO.getProdID();

			String skuId = giftListVO.getSkuID();

			String registryId = giftListVO.getRegistryID();
			vo.setSkuId(skuId);
			vo.setProdId(productId);
			vo.setQuantity(giftListVO.getQuantity());
			String parentCategory = getBbbCatalogTools().getShopSimilarItemCategory(productId, siteId);
			vo.setParentCat(parentCategory);
			try{
				if(!BBBUtility.isEmpty(parentCategory)){
					vo.setParentCatName(getBbbCatalogTools().getCategoryDetail(SiteContextManager.getCurrentSiteId(), parentCategory,false).getCategoryName());
					vo.setParentSeoUrl(getBbbCatalogTools().getCategoryDetail(SiteContextManager.getCurrentSiteId(), parentCategory,false).getSeoURL());
				}
			}catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException - Issue in getting Category"), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException - Issue in getting Category"), e);
			}
			SKUDetailVO skuVO = getBbbCatalogTools().getSKUDetails(SiteContextManager.getCurrentSiteId(), skuId);
			vo.setBopus(skuVO.isBopusAllowed());
			boolean isSkuActive = false;

			try {
				isSkuActive = getBbbCatalogTools().isSkuActive(skuId);
			} catch (RepositoryException e) {
				logError("Repository Exception in checkSavedItemMessages SKU Active:", e);
			}
			if (isSkuActive) {

				if (!BBBUtility.isEmpty(registryId)) {
					vo.setRegistryId(registryId);
					inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.ADD_ITEM_FROM_REG, giftListVO.getQuantity());
				} else {
					inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.PRODUCT_DISPLAY, giftListVO.getQuantity());
				}

				if (inStockStatus == BBBInventoryManager.NOT_AVAILABLE) {
					inStock = false;
				}
				Double listPrice = ((BBBCatalogTools) getBbbCatalogTools()).getListPrice(productId, skuId);
				Double salePrice = ((BBBCatalogTools) getBbbCatalogTools()).getSalePrice(productId, skuId);

				boolean onSale = ((BBBCatalogTools) getBbbCatalogTools()).isSkuOnSale(productId, skuId);
				/*	BPSI-3285 DSK | Handle Pricing message for Personalized Item*/
				currentPrice = BBBUtility.checkCurrentPriceForSavedItem(onSale, listPrice, salePrice,skuVO.getPersonalizationType(), giftListVO.getPersonalizePrice(), giftListVO.getReferenceNumber());
				vo.setCurrentPrice(currentPrice);
				vo.setInStock(inStock);
				if (!inStock) {
					vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
					vo.setInStock(false);					
					logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
					

				}
			} else {
				Double listPrice = ((BBBCatalogTools) getBbbCatalogTools()).getListPrice(productId, skuId);
				Double salePrice = ((BBBCatalogTools) getBbbCatalogTools()).getSalePrice(productId, skuId);
				boolean onSale = ((BBBCatalogTools) getBbbCatalogTools()).isSkuOnSale(productId, skuId);
				currentPrice = BBBUtility.checkCurrentPriceForSavedItem(onSale, listPrice, salePrice,skuVO.getPersonalizationType(), giftListVO.getPersonalizePrice(), giftListVO.getReferenceNumber());
				vo.setCurrentPrice(currentPrice);
				vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
				vo.setFlagOff(true);
				return vo;
			}

			// Need to be removed
			if (Double.compare(giftListVO.getPrevPrice() ,currentPrice) != BBBCoreConstants.ZERO && Double.compare(giftListVO.getPrevPrice(), 0.0) != BBBCoreConstants.ZERO) {

				vo.setPrevPrice(giftListVO.getPrevPrice());
				vo.setCurrentPrice(currentPrice);
				vo.setPriceChange(true);
				Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
				if (!profile.isTransient()) {
					String itemId = null;
					try {
						if (!StringUtils.isEmpty(vo.getRegistryId())) {
							List itemsList = getGiftListManager().getGiftlistItems(getGiftListManager().getWishlistId(profile.getRepositoryId()));
							if (itemsList != null) {
								Iterator iterator = itemsList.iterator();

								while (iterator.hasNext()) {
									RepositoryItem itemGift = (RepositoryItem) iterator.next();
									String regId = (String) itemGift.getPropertyValue(BBBCoreConstants.REGISTRY_ID);
									if ((null != regId && BBBCoreConstants.BLANK.equalsIgnoreCase(regId)) || regId == null) {
										continue;
									}
									if ((itemGift.getPropertyValue(getGiftListManager().getGiftlistTools().getCatalogRefIdProperty()).equals(skuId)) && (itemGift.getPropertyValue(getGiftListManager().getGiftlistTools().getSiteProperty()).equals(SiteContextManager.getCurrentSiteId())) && vo.getRegistryId().equalsIgnoreCase(regId)) {
										itemId = ((String) itemGift.getPropertyValue(BBBCoreConstants.ID));
										MutableRepositoryItem item = (MutableRepositoryItem) getGiftListManager().getGiftitem(itemId);
										item.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, currentPrice);
										((MutableRepository) getGiftListManager().getGiftlistTools().getGiftlistRepository()).updateItem(item);
										break;
									}
								}
							}

						} else {
							List itemsList = getGiftListManager().getGiftlistItems(getGiftListManager().getWishlistId(profile.getRepositoryId()));
							if (itemsList != null) {
								Iterator iterator = itemsList.iterator();

								while (iterator.hasNext()) {
									RepositoryItem itemGift = (RepositoryItem) iterator.next();
									if ((itemGift.getPropertyValue(getGiftListManager().getGiftlistTools().getCatalogRefIdProperty()).equals(skuId)) && (itemGift.getPropertyValue(getGiftListManager().getGiftlistTools().getSiteProperty()).equals(SiteContextManager.getCurrentSiteId())) && itemGift.getPropertyValue(BBBCoreConstants.REGISTRY_ID) == null) {
										itemId = ((String) itemGift.getPropertyValue(BBBCoreConstants.ID));
										MutableRepositoryItem item = (MutableRepositoryItem) getGiftListManager().getGiftitem(itemId);
										item.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, currentPrice);
										((MutableRepository) getGiftListManager().getGiftlistTools().getGiftlistRepository()).updateItem(item);
										break;
									}
								}
							}

						}

					} catch (RepositoryException e) {						
						logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP, BBBCoreErrorConstants.GIFT_ERROR_1000), e);
						
					} catch (CommerceException e) {						
						logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
						
					}
				} else {
					giftListVO.setPrevPrice(currentPrice);
				}
			}

			return vo;
		}

			logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
			return null;
	}


	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}
}
