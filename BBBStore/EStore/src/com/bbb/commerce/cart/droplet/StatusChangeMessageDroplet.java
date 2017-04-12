package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import org.apache.commons.lang.StringUtils;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.ItemPriceInfo;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.cart.PricingMessageVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;
import com.bbb.wishlist.manager.BBBGiftlistManager;

/**
 * This class helps to check inventory and price change based on that gives a
 * Price Message vo which holds the response
 * 
 * @author akhaju
 * 
 */
public class StatusChangeMessageDroplet extends BBBDynamoServlet {

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
	private TransactionManager mTransactionManager;

	private SiteContext mSiteContext;

	/**
	 * @return the mTransactionManager
	 */
	public TransactionManager getTransactionManager() {
		return this.mTransactionManager;
	}

	/**
	 * @param pTransactionManager
	 *            the mTransactionManager to set
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		this.mTransactionManager = pTransactionManager;
	}

	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param mSiteContext
	 *            the mSiteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		this.mSiteContext = pSiteContext;
	}
	
	/**
	 * @return the productManager
	 */
	public ProductManager getProductManager() {
		return mProductManager;
	}

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
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		String methodName = "StatusChangeMessageDroplet.service";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.STATUS_CHANGE_MESSAGE, methodName);

		
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[service ends]");
		
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

		
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[service ends]");
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.STATUS_CHANGE_MESSAGE, methodName);

	}

	private BBBSavedItemsSessionBean getSavedItemsSessionBean() {
		BBBSavedItemsSessionBean bean = (BBBSavedItemsSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SAVEDCOMP);
		return bean;
	}

	/**
	 * Check inventory and price change based on that return PricingMessageVO
	 * for cart section
	 * 
	 * @param commerceItemList
	 * @param pRequest
	 * @param pResponse
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void checkCartItemMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws BBBSystemException, BBBBusinessException, RepositoryException, ServletException, IOException {

		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage starts]");
		
		boolean inStock = true;
		int inStockStatus;
		Double currentPrice = 0.0;
		String siteId = SiteContextManager.getCurrentSiteId();

		String id = pRequest.getParameter(BBBCoreConstants.ID);
		logDebug("Id Recieved " + id);
	
		boolean checkItemInValid = true;
		if (id != null) {
			OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
			Order order = cart.getCurrent();
			List<CommerceItem> commerceItemLists = (List<CommerceItem>) cart.getCurrent().getCommerceItems();
			for (CommerceItem commerceItem : commerceItemLists) {
				if (commerceItem instanceof BBBCommerceItem) {
					BBBCommerceItem bbbCommerceItem = (BBBCommerceItem) commerceItem;
					if (id.equalsIgnoreCase(bbbCommerceItem.getId())) {
						checkItemInValid = false;
						PricingMessageVO vo = new PricingMessageVO();

						String productId = bbbCommerceItem.getAuxiliaryData().getProductId();

						String skuId = bbbCommerceItem.getCatalogRefId();

						String registryId = bbbCommerceItem.getRegistryId();
						vo.setQuantity(bbbCommerceItem.getQuantity());

						vo.setSkuId(skuId);
						vo.setProdId(productId);
						vo.setCommerceItemId(bbbCommerceItem.getId());
						vo.setParentCat(getBbbCatalogTools().getShopSimilarItemCategory(productId, siteId));
						SKUDetailVO skuVO = getBbbCatalogTools().getSKUDetails(SiteContextManager.getCurrentSiteId(), skuId);
						vo.setBopus(skuVO.isBopusAllowed());
						ItemPriceInfo itemPriceInfo = bbbCommerceItem.getPriceInfo();
						Double listPrice = itemPriceInfo.getListPrice();
						Double salePrice = itemPriceInfo.getSalePrice();

						boolean onSale = itemPriceInfo.isOnSale();
						currentPrice = checkCurrentPrice(onSale, listPrice, salePrice);
						vo.setCurrentPrice(currentPrice);
						try {

							if (getBbbCatalogTools().isSkuActive(skuId)) {

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
									pRequest.setParameter(PRICEMESSAGEVO, vo);
									logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage ends]");
									
								} else if (vo.getMessage() != null) {
									pRequest.setParameter(PRICEMESSAGEVO, vo);
									pRequest.serviceParameter(BBBCoreConstants.OUTPUTPRICE, pRequest, pResponse);
									logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage ends]");
									

								}
							} else {
								vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
								vo.setFlagOff(true);
								pRequest.setParameter(PRICEMESSAGEVO, vo);								
								logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage ends]");
								
								return;
							}

							if (bbbCommerceItem.getPrevPrice() != currentPrice && Double.compare(bbbCommerceItem.getPrevPrice(),0.0) != BBBCoreConstants.ZERO) {
								PricingMessageVO psvo = (PricingMessageVO) vo.clone();
								psvo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_ITEM_CHANGE, pRequest.getLocale().getLanguage(), null));
								psvo.setPrevPrice(bbbCommerceItem.getPrevPrice());
								psvo.setInStock(true);
								psvo.setPriceChange(true);
								psvo.setCurrentPrice(currentPrice);
								String rev = pRequest.getParameter(BBBCoreConstants.REV);
								if(!StringUtils.isEmpty(rev)){
									bbbCommerceItem.setPrevPrice(currentPrice);	
									updateOrder(order, pRequest);
								}
								pRequest.setParameter(PRICECHANGEVO, psvo);
							}
						} finally {
							pRequest.serviceParameter(BBBCoreConstants.OUTPUTPRICE, pRequest, pResponse);
							updateOrder(order, pRequest);
						}
					}
				}
			}
		} else if (checkItemInValid) {
			throw new BBBBusinessException(BBBCoreErrorConstants.CART_ERROR_1000, BBBCoreErrorConstants.ERROR_ID_NULL);
		}
	}

	/**
	 * Update Order
	 * 
	 * @param order
	 * @param pRequest
	 */
	private void updateOrder(Order order, DynamoHttpServletRequest pRequest) {
		// BBBSL-2735. Added synchronization & transaction for updating the order
		TransactionDemarcation td = new TransactionDemarcation();
		boolean rollback = false;
		try {
			/* Start the transaction */
			td.begin(getTransactionManager());
			synchronized (order) {
				getOrderManager().updateOrder(order);
			}
		} catch (CommerceException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GET_ATG_ORDER_DETAILS_1001, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
			
		} catch (TransactionDemarcationException e) {
			this.logError("TransactionDemarcationException from updateOrder() method in StatusChangeMessageDroplet class", e);
			rollback = true;
		} finally{
			try {
				td.end(rollback);
			} catch (TransactionDemarcationException e) {
				this.logError("TransactionDemarcationException in ending the transaction from updateOrder() method in StatusChangeMessageDroplet class", e);
			}

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
	private void checkSavedItemMessages(List<GiftListVO> giftListVO, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws BBBSystemException, BBBBusinessException, RepositoryException, ServletException, IOException, CommerceException {
		
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages starts]");
		
		double currentPrice = 0.0;
		boolean inStock = true;
		int inStockStatus;
		String siteId = SiteContextManager.getCurrentSiteId();

		String id = pRequest.getParameter(BBBCoreConstants.ID);
		
		logDebug("Id Recieved " + id);
		
		boolean checkItemInValid = true;
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		if (id != null && giftListVO != null && !giftListVO.isEmpty()) {
			for (GiftListVO savedItem : giftListVO) {

				if (id.equalsIgnoreCase(savedItem.getWishListItemId())) {
					checkItemInValid = false;
					PricingMessageVO vo = new PricingMessageVO();

					vo.setWishListId(savedItem.getWishListItemId());
					String productId = savedItem.getProdID();

					String skuId = savedItem.getSkuID();

					String registryId = savedItem.getRegistryID();
					vo.setSkuId(skuId);
					vo.setProdId(productId);
					vo.setQuantity(savedItem.getQuantity());
					vo.setParentCat(getBbbCatalogTools().getShopSimilarItemCategory(productId, siteId));
					SKUDetailVO skuVO = getBbbCatalogTools().getSKUDetails(SiteContextManager.getCurrentSiteId(), skuId);
					vo.setBopus(skuVO.isBopusAllowed());
					if (getBbbCatalogTools().isSkuActive(skuId)) {

						if (!BBBUtility.isEmpty(registryId)) {
							vo.setRegistryId(registryId);
							inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.ADD_ITEM_FROM_REG, savedItem.getQuantity());
						} else {
							inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.PRODUCT_DISPLAY, savedItem.getQuantity());
						}

						if (inStockStatus == BBBInventoryManager.NOT_AVAILABLE) {
							inStock = false;
						}
						Double listPrice = ((BBBCatalogTools) getBbbCatalogTools()).getListPrice(productId, skuId);
						Double salePrice = ((BBBCatalogTools) getBbbCatalogTools()).getSalePrice(productId, skuId);

						boolean onSale = ((BBBCatalogTools) getBbbCatalogTools()).isSkuOnSale(productId, skuId);
						currentPrice = checkCurrentPrice(onSale, listPrice, salePrice);
						vo.setCurrentPrice(currentPrice);

						if (!inStock) {
							vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
							vo.setInStock(false);
							pRequest.setParameter(PRICEMESSAGEVO, vo);
							
							logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
							

						} else if (vo.getMessage() != null) {
							pRequest.setParameter(PRICEMESSAGEVO, vo);
							pRequest.serviceParameter(BBBCoreConstants.OUTPUTPRICE, pRequest, pResponse);
							logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
							

						}
					} else {
						Double listPrice = ((BBBCatalogTools) getBbbCatalogTools()).getListPrice(productId, skuId);
						Double salePrice = ((BBBCatalogTools) getBbbCatalogTools()).getSalePrice(productId, skuId);

						boolean onSale = ((BBBCatalogTools) getBbbCatalogTools()).isSkuOnSale(productId, skuId);
						currentPrice = checkCurrentPrice(onSale, listPrice, salePrice);
						vo.setCurrentPrice(currentPrice);
						vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
						vo.setFlagOff(true);
						pRequest.setParameter(PRICEMESSAGEVO, vo);
						pRequest.serviceParameter(BBBCoreConstants.OUTPUTPRICE, pRequest, pResponse);
						return;
					}

					// Need to be removed
					if (Double.compare(savedItem.getPrevPrice(),currentPrice) != BBBCoreConstants.ZERO && Double.compare(savedItem.getPrevPrice() ,0.0) != BBBCoreConstants.ZERO) {
						PricingMessageVO psvo = (PricingMessageVO) vo.clone();
						psvo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_ITEM_CHANGE, pRequest.getLocale().getLanguage(), null));
						
						logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
						
						psvo.setPrevPrice(savedItem.getPrevPrice());
						psvo.setCurrentPrice(currentPrice);
						psvo.setPriceChange(true);
						psvo.setInStock(true);
						String rev = pRequest.getParameter(BBBCoreConstants.REV);
						if(!StringUtils.isEmpty(rev)){
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
												if ((itemGift.getPropertyValue(getGiftListManager().getGiftlistTools().getCatalogRefIdProperty()).equals(skuId)) 
														&& (itemGift.getPropertyValue(getGiftListManager().getGiftlistTools().getSiteProperty()).equals(SiteContextManager.getCurrentSiteId())) 
														&& vo.getRegistryId().equalsIgnoreCase(regId))
												{
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
												if ((itemGift.getPropertyValue(getGiftListManager().getGiftlistTools().getCatalogRefIdProperty()).equals(skuId)) 
														&& (itemGift.getPropertyValue(getGiftListManager().getGiftlistTools().getSiteProperty()).equals(SiteContextManager.getCurrentSiteId())) 
														&& itemGift.getPropertyValue(BBBCoreConstants.REGISTRY_ID) == null)
												{
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
									throw e;
								}
							} else {
								savedItem.setPrevPrice(currentPrice);
							}
						}
						pRequest.setParameter(PRICECHANGEVO, psvo);

					}
					if (pRequest.getParameter(PRICECHANGEVO) == null && pRequest.getParameter(PRICEMESSAGEVO) == null) {
						pRequest.serviceParameter(BBBCoreConstants.EMPTY, pRequest, pResponse);
						logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
						
						return;
					} else {
						pRequest.serviceParameter(BBBCoreConstants.OUTPUTPRICE, pRequest, pResponse);
					}
					
					logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
					
				}

			}

		} else if (checkItemInValid) {
			throw new BBBBusinessException(BBBCoreErrorConstants.GIFT_ERROR_1000, BBBCoreErrorConstants.ERROR_GIFT_NULL);
		}
	}

	/**
	 * Helper for junit, calls the checkCartItemMessage method to get the
	 * response
	 * 
	 * @return
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	public List<PricingMessageVO> checkCartItemMessage(List<String> pIds) throws BBBBusinessException {
		
		if (null != pIds && !pIds.isEmpty()) {
			logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage starts]");
			logDebug("Id Recieved " + pIds);
			
			List<PricingMessageVO> vo = new ArrayList<PricingMessageVO>();
			Iterator<String> pvo = pIds.iterator();
			while (pvo.hasNext()) {
				DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
				DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
				try {
					pRequest.setParameter(BBBCoreConstants.ID, pvo.next());
					checkCartItemMessage(pRequest, pResponse);
					PricingMessageVO pricemessagevo = (PricingMessageVO) pRequest.getObjectParameter(PRICEMESSAGEVO);
					if (pricemessagevo != null) {
						vo.add(pricemessagevo);
					}
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.CART_ERROR_1000, BBBCoreErrorConstants.ERROR_CART_ERROR_1000);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.CART_ERROR_1001, BBBCoreErrorConstants.ERROR_CART_ERROR_1001);
				} catch (RepositoryException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.CART_ERROR_1002, BBBCoreErrorConstants.ERROR_CART_ERROR_1002);
				} catch (ServletException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "ServletException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.CART_ERROR_1003, BBBCoreErrorConstants.ERROR_CART_ERROR_1003);
				} catch (IOException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "IOException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.CART_ERROR_1004, BBBCoreErrorConstants.ERROR_CART_ERROR_1004);
				}
			}
			return vo;
		} else {
			throw new BBBBusinessException(BBBCoreErrorConstants.CART_ERROR_1000, BBBCoreErrorConstants.ERROR_ID_NULL);
		}
	}

	/**
	 * Helper for junit, calls the checkSavedItemMessages method to get the
	 * response
	 * 
	 * @param pIds
	 * @return
	 * @throws BBBBusinessException
	 */
	public List<PricingMessageVO> checkSavedItemMessages(List<String> pIds) throws BBBBusinessException {
		
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages starts]");
		logDebug("Id Recieved " + pIds);
		
		if (pIds != null && !pIds.isEmpty()) {
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
			List<PricingMessageVO> vo = new ArrayList<PricingMessageVO>();
			Iterator<String> pvo = pIds.iterator();
			while (pvo.hasNext()) {
				List<GiftListVO> giftListVO = getSavedItemsSessionBean().getItems();
				pRequest.setParameter(BBBCoreConstants.ID, pvo.next());
				try {
					checkSavedItemMessages(giftListVO, pRequest, pResponse);
					PricingMessageVO pricemessagevo = (PricingMessageVO) pRequest.getObjectParameter(PRICEMESSAGEVO);
					if (pricemessagevo != null) {
						vo.add(pricemessagevo);
					}
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.GIFT_ERROR_1000, BBBCoreErrorConstants.ERROR_GIFT_ERROR_1000);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.GIFT_ERROR_1001, BBBCoreErrorConstants.ERROR_GIFT_ERROR_1001);
				} catch (RepositoryException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.GIFT_ERROR_1002, BBBCoreErrorConstants.ERROR_GIFT_ERROR_1002);
				} catch (ServletException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "ServletException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.GIFT_ERROR_1003, BBBCoreErrorConstants.ERROR_GIFT_ERROR_1003);
				} catch (IOException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "IOException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.GIFT_ERROR_1004, BBBCoreErrorConstants.ERROR_GIFT_ERROR_1004);
				} catch (CommerceException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "CommerceException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.GIFT_ERROR_1005, BBBCoreErrorConstants.ERROR_GIFT_ERROR_1005);
				}			
			}
			logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
			
			return vo;
		} else {
			throw new BBBBusinessException(BBBCoreErrorConstants.GIFT_ERROR_1003, BBBCoreErrorConstants.INVALID_GIFT_ID);
		}
	}

	/**
	 * Checks the current price of the Item
	 * 
	 * @param onSale
	 * @param listPrice
	 * @param salePrice
	 * @return
	 */
	private Double checkCurrentPrice(boolean onSale, Double listPrice, Double salePrice) {
		Double currentPrice;
		if (onSale) {
			if ((null != salePrice)) {
				currentPrice = salePrice;
			} else {
				currentPrice = listPrice;
			}
		} else {
			currentPrice = listPrice;
		}
		return currentPrice;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}
}
