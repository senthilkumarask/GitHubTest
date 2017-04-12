package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.cart.PricingMessageVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.InventoryTools;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;
import com.bbb.wishlist.manager.BBBGiftlistManager;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderImpl;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingConstants;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

/**
 * This class helps to check inventory and price change based on that gives a
 * Price Message vo which holds the response
 *
 * @author akhaju
 *
 */
public class TopStatusChangeMessageDroplet extends BBBDynamoServlet {

	private BBBCatalogTools mBbbCatalogTools;
	private BBBInventoryManager mInventoryManager;
	private MutableRepository mCatalogRepository;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBGiftlistManager mGiftListManager;

	public static final String MESSAGE = "message";
	public static final String PARENTCATEGORY = "parentcategory";
	public static final String PRICEMESSAGEVO = "priceMessageVO";
	public static final String FLAGOFFVOLIST = "flagOffVOList";
	public static final String OOSVOLIST = "oosVOList";
	public static final String PRICECHANGEVOLIST = "pricechangeVOList";
	public static final String INT_SHIP_RESTRICTED_SKU = "intShipRestrictedSku";
	public static final String INT_SHIP_BOPUS_SKU = "intShipBopusSku";
	public static final String INT_SHIP_SKU = "intShipSku";
	public static final String ENVOY_2_CART = "envoy2cart";
	public static final String REFERER = "Referer";
	public static final String SKIP_OOS_MSG = "skipOOSMessage";
	public static final String ENVOY_REFERER = "/store/checkout/envoy_checkout.jsp";
	
	private InventoryTools inventoryTools;
	private CatalogTools mCatalogTools;
	private BBBOrderManager mOrderManager;
	private ProductManager mProductManager;
	public static final String PRICECHANGEVO = "priceChangeVO";
	public static final String REQTYPE = "reqType";
	public static final String CART = "cart";
	public static final String MERGE = "merge";
	public static final String JMERGE = "jmerge";
	private TransactionManager transactionManager;
	private ClientLockManager localLockManager;
	
	public static final String GETPRICECHANGEMSGONLY = "getPriceChangeMsgOnly";
	private static final String OOOSITEMS = "oosItems";
	private static final String PRICEMESSAGEVOLIST = "priceMessageVoList";

	
	/**
	 * @return the inventoryTools
	 */
	public InventoryTools getInventoryTools() {
		return inventoryTools;
	}
	

	/**
	 * @param inventoryTools the inventoryTools to set
	 */
	public void setInventoryTools(InventoryTools inventoryTools) {
		this.inventoryTools = inventoryTools;
	}
	

	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
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
		
		ClientLockManager lockManager = getLocalLockManager();
		Profile profile = (Profile)ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
		boolean acquireLock = false;
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation();
		boolean isException = false;
		OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
		Order order = cart.getCurrent();
		
		try {
			acquireLock = !lockManager.hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
			if (acquireLock) {
				lockManager.acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
			}
			td.begin(tm);
			/*OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
			Order order = cart.getCurrent();*/
			// check messages for cart Items
			String reqType = pRequest.getParameter(REQTYPE);
			boolean getPriceChangeMsgOnly =false;
			if(BBBUtility.isNotEmpty(pRequest.getParameter(GETPRICECHANGEMSGONLY))){
				getPriceChangeMsgOnly = Boolean.valueOf(pRequest.getParameter(GETPRICECHANGEMSGONLY)) ;
			}
			List<PricingMessageVO> flagOffList = new ArrayList<PricingMessageVO>();
			List<PricingMessageVO> oosList = new ArrayList<PricingMessageVO>();
			List<PricingMessageVO> pricechangeVOList = new ArrayList<PricingMessageVO>();
			synchronized (order) {
				boolean updatecartItemMsg =false;
				if(reqType != null && reqType.equalsIgnoreCase(MERGE)) {
					if(getPriceChangeMsgOnly){
						updatecartItemMsg = cartPriceChangeMessage(pRequest, pResponse);
						flagOffList = null;
						oosList = null;
					}else{
						updatecartItemMsg =	checkCartItemMessage(pRequest, pResponse);
					}
					mergeMsg(pRequest, flagOffList, oosList, pricechangeVOList);
				}
				boolean checkSavedItemMsg =false;
					
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
					
					if(getPriceChangeMsgOnly){
						checkSavedItemMsg = checkSavedItemPriceChangeMessages(giftListVO, pRequest, pResponse);
					}else{
						checkSavedItemMsg = checkSavedItemMessages(giftListVO, pRequest, pResponse);
						
					}
				
				if(updatecartItemMsg || checkSavedItemMsg){
					updateOrder(order, pRequest);
				}
			}
			List<PricingMessageVO> SIflagOffList = new ArrayList<PricingMessageVO>();
			List<PricingMessageVO> SIoosList = new ArrayList<PricingMessageVO>();
			List<PricingMessageVO> SIpricechangeVOList = new ArrayList<PricingMessageVO>();
			mergeMsg(pRequest, SIflagOffList, SIoosList, SIpricechangeVOList);
			List<PricingMessageVO> finalMerge = finalMerge(flagOffList, oosList, pricechangeVOList, SIflagOffList, SIoosList, SIpricechangeVOList);
			if(!finalMerge.isEmpty()){
				String jmer = pRequest.getParameter(JMERGE);
				if(!BBBUtility.isEmpty(jmer)){
					pRequest.setParameter(PRICEMESSAGEVO, finalMerge);
					pRequest.setParameter(PRICEMESSAGEVOLIST, finalMerge);
				} else {
					int i=0;
					
					pRequest.setParameter(PRICEMESSAGEVOLIST, finalMerge);
					
					while(i < finalMerge.size()) {
						pRequest.setParameter(PRICEMESSAGEVO, finalMerge.get(i));
						pRequest.serviceParameter(BBBCoreConstants.OUTPUTPRICE, pRequest, pResponse);
						i++;
					}
				}
			} else {
				pRequest.serviceParameter(BBBCoreConstants.EMPTY, pRequest, pResponse);
			}
			
			boolean intShipRestrictedSku = pRequest.getObjectParameter(INT_SHIP_RESTRICTED_SKU)== null ? false :(Boolean) pRequest.getObjectParameter(INT_SHIP_RESTRICTED_SKU);
			boolean intShipBopusSku = pRequest.getObjectParameter(INT_SHIP_BOPUS_SKU)== null ? false :(Boolean) pRequest.getObjectParameter(INT_SHIP_BOPUS_SKU);
			boolean envoy2cart = pRequest.getObjectParameter(ENVOY_2_CART)== null ? false :pRequest.getObjectParameter(ENVOY_2_CART).toString().equalsIgnoreCase(BBBCoreConstants.TRUE);
			String referer = pRequest.getHeader(REFERER);
			logDebug("CLS=[StatusChangeMessageDroplet] referer=["+referer+"] , Redirect from Envoy Page ["+envoy2cart+"]");
			if((BBBUtility.isNotEmpty(referer) && referer.contains(ENVOY_REFERER)) || envoy2cart){
				pRequest.serviceParameter(BBBCoreConstants.OUTPUTENVOY, pRequest, pResponse);
			}
			if(intShipRestrictedSku || intShipBopusSku){
				pRequest.setParameter(INT_SHIP_SKU, true);
				pRequest.serviceParameter(BBBCoreConstants.OUTPUTINTLSHIP, pRequest, pResponse);
			}
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException"), e);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
			isException = true;
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException"), e);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
			isException = true;
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException"), e);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
			isException = true;
		} catch (CommerceException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException"), e);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
			isException = true;
		} catch (TransactionDemarcationException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "TransactionDemarcationException"), e);
			logDebug("Error transaction " , e.getCause());
			
			isException = true;
		} catch (DeadlockException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException"), e);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
			isException = true;
		}
		finally {
			try {
				this.commitTransaction(isException, order);
				//td.end(isException);
			} finally {
				if (acquireLock)
					try {
						lockManager.releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
					} catch (LockManagerException e) {
						this.logError("TransactionDemarcationException releasing lock on profile", e);

					}
			}
		}
	
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[service ends]");
		

		BBBPerformanceMonitor.end(BBBPerformanceConstants.STATUS_CHANGE_MESSAGE, methodName);

	}

	/**
	 * @param flagOffList
	 * @param oosList
	 * @param pricechangeVOList
	 * @param SIflagOffList
	 * @param SIoosList
	 * @param SIpricechangeVOList
	 */
	private List<PricingMessageVO> finalMerge(List<PricingMessageVO> flagOffList, List<PricingMessageVO> oosList,
			List<PricingMessageVO> pricechangeVOList, List<PricingMessageVO> SIflagOffList, List<PricingMessageVO> SIoosList, List<PricingMessageVO> SIpricechangeVOList) {
		List<PricingMessageVO> finalList = new ArrayList<PricingMessageVO>();
		//Merge FlagOFF
		if (flagOffList != null && flagOffList.size() > 0) {
			List<PricingMessageVO> tempfinalFlagOffList = new ArrayList<PricingMessageVO>();
			for (PricingMessageVO item : flagOffList) {
				boolean check = true;
				for (PricingMessageVO siflagofflist : SIflagOffList) {
					if (siflagofflist.getSkuId().equalsIgnoreCase(item.getSkuId()) && siflagofflist.getProdId().equalsIgnoreCase(item.getProdId())) {
						check = false;
						break;
					}
				}
				if (check) {
					tempfinalFlagOffList.add(item);
				}
			}
			finalList.addAll(tempfinalFlagOffList);
			finalList.addAll(SIflagOffList);
		} else if(SIflagOffList != null && !SIflagOffList.isEmpty()){
			finalList.addAll(SIflagOffList);
		}

		//Merge OOS
		if (oosList != null && oosList.size() > 0) {
			List<PricingMessageVO> tempfinaloosList = new ArrayList<PricingMessageVO>();
			for (PricingMessageVO item : oosList) {
				boolean check = true;
				for (PricingMessageVO sioosList : SIoosList) {
					if (sioosList.getSkuId().equalsIgnoreCase(item.getSkuId()) && sioosList.getProdId().equalsIgnoreCase(item.getProdId())) {
						check = false;
						break;
					}
				}
				if (check) {
					tempfinaloosList.add(item);
				}
			}
			finalList.addAll(tempfinaloosList);
			finalList.addAll(SIoosList);
		} else if(SIoosList!=null && !SIoosList.isEmpty()){
			finalList.addAll(SIoosList);
		}

		//Merge PriceChange
		if (pricechangeVOList != null && pricechangeVOList.size() > 0) {
			List<PricingMessageVO> tempfinalpcList = new ArrayList<PricingMessageVO>();
			for (PricingMessageVO item : pricechangeVOList) {
				boolean check = true;
				for (PricingMessageVO sioosList : SIpricechangeVOList) {
					if (sioosList.getSkuId().equalsIgnoreCase(item.getSkuId()) && sioosList.getProdId().equalsIgnoreCase(item.getProdId())) {
						check = false;
						break;
					}
				}
				if (check) {
					tempfinalpcList.add(item);
				}
			}
			finalList.addAll(tempfinalpcList);
			finalList.addAll(SIpricechangeVOList);
		} else if(SIpricechangeVOList!=null && !SIpricechangeVOList.isEmpty()){
			finalList.addAll(SIpricechangeVOList);
		}
		return finalList;
	}

	/**
	 * @param pRequest
	 */
	private void mergeMsg(DynamoHttpServletRequest pRequest, List<PricingMessageVO> flagOffList, List<PricingMessageVO> oosList, List<PricingMessageVO> pricechangeVOList) {

		List<PricingMessageVO> flagOffVO = (List<PricingMessageVO>) pRequest.getObjectParameter(FLAGOFFVOLIST);
		List<PricingMessageVO> oosVO = (List<PricingMessageVO>) pRequest.getObjectParameter(OOSVOLIST);
		List<PricingMessageVO> priceChangeVO = (List<PricingMessageVO>) pRequest.getObjectParameter(PRICECHANGEVOLIST);
		if (flagOffVO != null && flagOffVO.size() > 0) {

			List<PricingMessageVO> itemAdded = new ArrayList<PricingMessageVO>();
			itemAdded.add(flagOffVO.get(0));

			for (PricingMessageVO item : flagOffVO) {
				boolean check = true;
				for (PricingMessageVO itemAddedone : itemAdded) {
					if (itemAddedone.getSkuId().equalsIgnoreCase(item.getSkuId())) {
						check = false;
						break;
					}
				}
				if (check) {
					itemAdded.add(item);
				}
			}
			flagOffList.addAll(itemAdded);
		}

		if (oosVO != null && oosVO.size() > 0) {

			List<PricingMessageVO> itemAdded = new ArrayList<PricingMessageVO>();
			itemAdded.add(oosVO.get(0));

			for (PricingMessageVO item : oosVO) {
				boolean check = true;
				for (PricingMessageVO itemAddedone : itemAdded) {
					if (itemAddedone.getSkuId().equalsIgnoreCase(item.getSkuId())) {
						check = false;
						break;
					}
				}
				if (check) {
					itemAdded.add(item);
				}
			}
			oosList.addAll(itemAdded);
		}

		if (priceChangeVO != null && priceChangeVO.size() > 0) {

			List<PricingMessageVO> itemAdded = new ArrayList<PricingMessageVO>();
			itemAdded.add(priceChangeVO.get(0));

			for (PricingMessageVO item : priceChangeVO) {
				boolean check = true;
				for (PricingMessageVO itemAddedone : itemAdded) {
					if (itemAddedone.getSkuId().equalsIgnoreCase(item.getSkuId())) {
						check = false;
						break;
					}
				}
				if (check) {
					itemAdded.add(item);
				}
			}
			pricechangeVOList.addAll(itemAdded);
		}
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
	private boolean checkCartItemMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws BBBSystemException, BBBBusinessException, RepositoryException, ServletException, IOException, CommerceException {
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage starts]");
		
		int inStockStatus = 0;
		Double currentPrice = 0.0;
		String siteId = SiteContextManager.getCurrentSiteId();

		OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
		Order order = cart.getCurrent();
		
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		List<String> restrictedSkuList= (List<String>) sessionBean.getValues().get("internationalShipRestrictedSku");
		List<String> bopusSkuList=	(List<String>) sessionBean.getValues().get("bopusSkuNoIntShip");
		
		boolean hasIntShipRestrictedSku=false;
		boolean hasIntShipBopusSku=false;

		if(restrictedSkuList!=null && !restrictedSkuList.isEmpty()){
			hasIntShipRestrictedSku=true;
		}
		if(bopusSkuList!=null && !bopusSkuList.isEmpty()){
			hasIntShipBopusSku=true;
		}
		
		pRequest.setParameter(INT_SHIP_RESTRICTED_SKU, hasIntShipRestrictedSku);
		pRequest.setParameter(INT_SHIP_BOPUS_SKU, hasIntShipBopusSku);

		
		List<PricingMessageVO> flagOffVO = new ArrayList<PricingMessageVO>();
		List<PricingMessageVO> oosVO = new ArrayList<PricingMessageVO>();
		StringBuilder oosItems=new StringBuilder();
		List<PricingMessageVO> priceChangeVO = new ArrayList<PricingMessageVO>();
		boolean doUpdate = false;		
	/*	boolean rollback = false;
		boolean isTransact = false;*/
		
		/*synchronized (order) {*/
			List<CommerceItem> commerceItemLists = (List<CommerceItem>) order.getCommerceItems();
			/*if(null != commerceItemLists && commerceItemLists.size() > 0){
				isTransact = true;
			}
			if(isTransact){
				TransactionDemarcation td = new TransactionDemarcation();
				td.begin(getTransactionManager());
			}*/
       for (CommerceItem commerceItem : commerceItemLists) {
		if (commerceItem instanceof BBBCommerceItem) {
			boolean inStock = true;
			BBBCommerceItem bbbCommerceItem = (BBBCommerceItem) commerceItem;

			boolean toplink = true;

			PricingMessageVO vo = new PricingMessageVO();

			String productId = bbbCommerceItem.getAuxiliaryData().getProductId();

			String skuId = bbbCommerceItem.getCatalogRefId();

			String registryId = bbbCommerceItem.getRegistryId();
			vo.setQuantity(bbbCommerceItem.getQuantity());
			vo.setCommerceItemId(bbbCommerceItem.getId());
			vo.setSkuId(skuId);
			vo.setProdId(productId);
			String parentCategory = getBbbCatalogTools().getShopSimilarItemCategory(productId, siteId);
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
			vo.setParentCat(parentCategory);
			SKUDetailVO skuVO = getBbbCatalogTools().getSKUDetails(SiteContextManager.getCurrentSiteId(),false,skuId);
			if(skuVO != null){
				vo.setBopus(skuVO.isBopusAllowed());
				vo.setDisplayName(skuVO.getDisplayName());
			}
			ItemPriceInfo itemPriceInfo = bbbCommerceItem.getPriceInfo();
			if(itemPriceInfo == null){
				Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
				repriceCartMoveItemsFromCart(order, profile);
				itemPriceInfo = bbbCommerceItem.getPriceInfo();
			}
			Double listPrice = itemPriceInfo.getListPrice();
			Double salePrice = itemPriceInfo.getSalePrice();

			boolean onSale = itemPriceInfo.isOnSale();
			currentPrice = BBBUtility.checkCurrentPrice(onSale, listPrice, salePrice);
			vo.setCurrentPrice(currentPrice);
			if (!BBBUtility.isEmpty(registryId)) {
				vo.setRegistryId(registryId);
			}				
				if (getBbbCatalogTools().isSkuActive(skuId)) {

				if (!Boolean.parseBoolean(pRequest.getParameter(SKIP_OOS_MSG))) {
										if (!BBBUtility.isEmpty(registryId)) {
											inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.ADD_ITEM_FROM_REG, bbbCommerceItem.getQuantity());
										} else {
											inStockStatus = getInventoryManager().getProductAvailability(SiteContextManager.getCurrentSiteId(), skuId, BBBInventoryManager.PRODUCT_DISPLAY, bbbCommerceItem.getQuantity());
										}
				}						

					if (inStockStatus == BBBInventoryManager.NOT_AVAILABLE) {
						inStock = false;
					}
					if (toplink && bbbCommerceItem.isMsgShownFlagOff()) {
						bbbCommerceItem.setMsgShownFlagOff(false);
						doUpdate = true;
					}
					// Commented for Mobile change.... Please Validate!!
					if (!inStock && BBBUtility.isEmpty(bbbCommerceItem.getStoreId()) && (toplink)) {
						vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
						vo.setInStock(false);
						oosVO.add(vo);
						logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage ends]");
						oosItems.append(bbbCommerceItem.getId()+BBBCoreConstants.EQUAL+BBBCoreConstants.ZERO+BBBCoreConstants.SEMICOLON);
						
					}
				} else if ((toplink)) {
					vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
					vo.setFlagOff(true);
					vo.setInStock(false);
					flagOffVO.add(vo);
					oosItems.append(bbbCommerceItem.getId()+BBBCoreConstants.EQUAL+BBBCoreConstants.ZERO+BBBCoreConstants.SEMICOLON);
				}
				
				if (!vo.isFlagOff() && !bbbCommerceItem.isMsgShownFlagOff() && bbbCommerceItem.getPrevPrice() != currentPrice && Double.compare(bbbCommerceItem.getPrevPrice(), 0.0) != BBBCoreConstants.ZERO) {
					PricingMessageVO psvo = (PricingMessageVO) vo.clone();
					psvo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_ITEM_CHANGE, pRequest.getLocale().getLanguage(), null));
					psvo.setPrevPrice(bbbCommerceItem.getPrevPrice());
					psvo.setInStock(true);
					psvo.setPriceChange(true);
					psvo.setCurrentPrice(currentPrice);
					priceChangeVO.add(psvo);
				}
			}
       }
       String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
		if (flagOffVO.size() > 0) {
			pRequest.setParameter(FLAGOFFVOLIST, flagOffVO);
		}
		if (oosVO.size() > 0) {
			pRequest.setParameter(OOSVOLIST, oosVO);
		}
		if (priceChangeVO.size() > 0) {
			pRequest.setParameter(PRICECHANGEVOLIST, priceChangeVO);
		}
		if (oosItems.length() > 0) {
			oosItems.deleteCharAt((oosItems.length()-1));
			pRequest.setParameter(OOOSITEMS, oosItems);
		}
		return doUpdate;
	}

	/**
	 * Update Order
	 *
	 * @param order
	 * @param pRequest
	 */
	private void updateOrder(Order order, DynamoHttpServletRequest pRequest) throws CommerceException{
		try {
				getOrderManager().updateOrder(order);
		} catch (CommerceException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GET_ATG_ORDER_DETAILS_1001, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
				logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM);
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
	private boolean checkSavedItemMessages(List<GiftListVO> giftListVO, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws BBBSystemException, BBBBusinessException, RepositoryException, ServletException, IOException, CommerceException {

		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages starts]");
		double currentPrice = 0.0;
		int inStockStatus;
		String siteId = SiteContextManager.getCurrentSiteId();

		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		boolean updateCheckSavedItemMsgs = false; 
		OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
		Order order = cart.getCurrent();
		if (giftListVO != null && !giftListVO.isEmpty()) {

			List<PricingMessageVO> flagOffVO = new ArrayList<PricingMessageVO>();
			List<PricingMessageVO> oosVO = new ArrayList<PricingMessageVO>();
			List<PricingMessageVO> priceChangeVO = new ArrayList<PricingMessageVO>();
			for (GiftListVO savedItem : giftListVO) {
				boolean inStock = true;
				PricingMessageVO vo = new PricingMessageVO();
				vo.setWishListId(savedItem.getWishListItemId());
				String productId = savedItem.getProdID();

				String skuId = savedItem.getSkuID();

				String registryId = savedItem.getRegistryID();
				vo.setSkuId(skuId);
				vo.setProdId(productId);
				vo.setQuantity(savedItem.getQuantity());
				String parentCategory = getBbbCatalogTools().getShopSimilarItemCategory(productId, siteId);
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
				vo.setParentCat(parentCategory);
				SKUDetailVO skuVO = getBbbCatalogTools().getSKUDetails(SiteContextManager.getCurrentSiteId(),false,skuId);
				if(skuVO != null){
					vo.setBopus(skuVO.isBopusAllowed());
					vo.setDisplayName(skuVO.getDisplayName());
				}
				
				/*TransactionManager tm = getTransactionManager();*/
				/*TransactionDemarcation td = new TransactionDemarcation();
				boolean rollback = false;*/
				
				try {
					
				/*td.begin(getTransactionManager());
				synchronized (order) {*/
					if (!BBBUtility.isEmpty(registryId)) {
						vo.setRegistryId(registryId);
					}
				if (getBbbCatalogTools().isSkuActive(skuId)) {

					if (!BBBUtility.isEmpty(registryId)) {
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
					/* BPSI-3285 DSK | Handle Pricing message for Personalized Item */
					currentPrice = BBBUtility.checkCurrentPriceForSavedItem(onSale, listPrice, salePrice,skuVO.getPersonalizationType(),savedItem.getPersonalizePrice(), savedItem.getReferenceNumber());
					vo.setCurrentPrice(currentPrice);

					

					if (savedItem.isMsgShownFlagOff()) {
						if (!profile.isTransient()) {
							try {
								String giftId= getGiftListManager().getGiftlistItemId(getGiftListManager().getWishlistId(profile.getRepositoryId()), skuId, productId, savedItem.getSiteId());
								if(giftId!=null)
								{
									MutableRepositoryItem item = (MutableRepositoryItem) getGiftListManager().getGiftitem(giftId);

									item.setPropertyValue(BBBCoreConstants.MSGSHOWNFLAGOFF, false);
									((MutableRepository) getGiftListManager().getGiftlistTools().getGiftlistRepository()).updateItem(item);
								}
							} catch (RepositoryException e) {
								logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP, BBBCoreErrorConstants.GIFT_ERROR_1000), e);
								//logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP);
							} catch (CommerceException e) {
								logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
								//logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM);
								throw e;
							}
						} else {
							savedItem.setMsgShownFlagOff(false);
						}

					}

					if (!inStock && !(savedItem.isMsgShownOOS()) ) {
						if (!profile.isTransient()) {
							try {
								String giftId= getGiftListManager().getGiftlistItemId(getGiftListManager().getWishlistId(profile.getRepositoryId()), skuId, productId, savedItem.getSiteId());
								if(giftId!=null)
								{
									MutableRepositoryItem item = (MutableRepositoryItem) getGiftListManager().getGiftitem(giftId);
									item.setPropertyValue(BBBCoreConstants.MSGSHOWNOOS, true);
									((MutableRepository) getGiftListManager().getGiftlistTools().getGiftlistRepository()).updateItem(item);
								}
							} catch (RepositoryException e) {
									logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP, BBBCoreErrorConstants.GIFT_ERROR_1000), e);
//									logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP);
							} catch (CommerceException e) {
									logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
//   								logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM);
								
								throw e;
							}
						} else {
							savedItem.setMsgShownOOS(true);
						}
						vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
						vo.setInStock(false);
						pRequest.setParameter(PRICEMESSAGEVO, vo);
						
						logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
						
						oosVO.add(vo);
					}
					//OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
					//Order order = cart.getCurrent();
					List<CommerceItem> commerceItemLists =  order.getCommerceItems();
					
					for (CommerceItem commerceItem : commerceItemLists) {
						if (commerceItem instanceof BBBCommerceItem && commerceItem.getCatalogRefId().equalsIgnoreCase(savedItem.getSkuID())) {
							BBBCommerceItem bbbcommerceItem = (BBBCommerceItem)commerceItem;
							if(!bbbcommerceItem.isMsgShownOOS()){
								bbbcommerceItem.setMsgShownOOS(true);
								updateCheckSavedItemMsgs = true;
							}
						}
					}
					
				} else if (!(savedItem.isMsgShownFlagOff())) {
					Double listPrice = ((BBBCatalogTools) getBbbCatalogTools()).getListPrice(productId, skuId);
					Double salePrice = ((BBBCatalogTools) getBbbCatalogTools()).getSalePrice(productId, skuId);

					boolean onSale = ((BBBCatalogTools) getBbbCatalogTools()).isSkuOnSale(productId, skuId);
					currentPrice = BBBUtility.checkCurrentPriceForSavedItem(onSale, listPrice, salePrice, skuVO.getPersonalizationType(),savedItem.getPersonalizePrice(), savedItem.getReferenceNumber());
					vo.setCurrentPrice(currentPrice);
					vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
					vo.setFlagOff(true);
					vo.setInStock(false);
					
					if (!profile.isTransient()) {
						try {
							String giftId= getGiftListManager().getGiftlistItemId(getGiftListManager().getWishlistId(profile.getRepositoryId()), skuId, productId, savedItem.getSiteId());
							if(giftId!=null)
							{
								MutableRepositoryItem item = (MutableRepositoryItem) getGiftListManager().getGiftitem(getGiftListManager().getGiftlistItemId(getGiftListManager().getWishlistId(profile.getRepositoryId()), skuId, productId, savedItem.getSiteId(),savedItem.getRegistryID(), savedItem.getLtlShipMethod(),savedItem.getReferenceNumber()));
								item.setPropertyValue(BBBCoreConstants.MSGSHOWNFLAGOFF, true);
								((MutableRepository) getGiftListManager().getGiftlistTools().getGiftlistRepository()).updateItem(item);
							}
						} catch (RepositoryException e) {
							logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP, BBBCoreErrorConstants.GIFT_ERROR_1000), e);
							//logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP);
							
						} catch (CommerceException e) {
								logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
							//	logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM);
							
							throw e;
						}
					} else {
						savedItem.setMsgShownFlagOff(true);
					}
					flagOffVO.add(vo);
					//OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
					//Order order = cart.getCurrent();
					List<CommerceItem> commerceItemLists =  order.getCommerceItems();

					for (CommerceItem commerceItem : commerceItemLists) {
						if (commerceItem instanceof BBBCommerceItem && commerceItem.getCatalogRefId().equalsIgnoreCase(savedItem.getSkuID())) {
							BBBCommerceItem bbbcommerceItem = (BBBCommerceItem)commerceItem;
							if(savedItem.isMsgShownFlagOff() && !bbbcommerceItem.isMsgShownFlagOff()){
								bbbcommerceItem.setMsgShownFlagOff(true);
							}
						}
					}
					updateCheckSavedItemMsgs = true;
				}
				
				String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
				/*if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)) && updateCheckSavedItemMsgs){
					getOrderManager().updateOrder(order);
				}
				}*/
				} catch (CommerceException e){
					this.logError(LogMessageFormatter.formatMessage(pRequest, "CommerceException"), e);
					/*rollback = true;*/
				}
				/*catch (TransactionDemarcationException e) {
					this.logError("TransactionDemarcationException from checkSavedItemMessages() method in TopStatusChangeMessageDroplet class", e);
					rollback = true;
				} finally{
					this.commitTransaction(rollback, order);
				}*/

				// Need to be removed
				// currentPrice = 1.1;
				
				if (!vo.isFlagOff() && !savedItem.isMsgShownFlagOff() && (Double.compare(savedItem.getPrevPrice(), currentPrice) != BBBCoreConstants.ZERO && Double.compare(savedItem.getPrevPrice(), 0.0) != BBBCoreConstants.ZERO)) {
					PricingMessageVO psvo = (PricingMessageVO) vo.clone();
					psvo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_ITEM_CHANGE, pRequest.getLocale().getLanguage(), null));

					logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
					
					psvo.setPrevPrice(savedItem.getPrevPrice());
					psvo.setCurrentPrice(currentPrice);
					psvo.setPriceChange(true);
					psvo.setInStock(true);
					priceChangeVO.add(psvo);

				}

			
					logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
				
			}
			if (flagOffVO.size() > 0) {
				pRequest.setParameter(FLAGOFFVOLIST, flagOffVO);
			}
			if (oosVO.size() > 0) {
				pRequest.setParameter(OOSVOLIST, oosVO);
			}
			if (priceChangeVO.size() > 0) {
				pRequest.setParameter(PRICECHANGEVOLIST, priceChangeVO);
			}
			
		}
		return updateCheckSavedItemMsgs;
	}
	
	/**
	 * Commits the supplied transaction. Also invalidate the order if there's
	 * a version mis-match in the order in case of transaction rollback
	 * @param pTransaction a <code>Transaction</code> value
	 */
	protected void commitTransaction( boolean isRollback, Order order) {

		boolean exception = false;
		Transaction pTransaction = null;

		try {
			pTransaction = getTransactionManager().getTransaction();
			if(pTransaction != null){
				TransactionManager tm = getTransactionManager();
				if (isRollback || this.isTransactionMarkedAsRollBack())  {
					this.logInfo("Transaction is getting rollback from TopStatusChangeMessageDroplet");
					if (tm != null){
						tm.rollback();
					}else{
						pTransaction.rollback();  // PR65109: rollback() before invalidateOrder() prevent deadlocks due to thread synchronization in the invalidateOrder()
					}
					if (order!=null && order instanceof OrderImpl){
						this.logInfo("Invalidating order because of transaction is getting rollback from TopStatusChangeMessageDroplet");
						((OrderImpl)order).invalidateOrder();
					}
				}
				else {
					if (tm != null){
						
						this.logDebug("Commiting transaction in TopStatusChangeMessageDroplet");
						
						tm.commit();
					}else{
						
						this.logDebug("Commiting transaction in TopStatusChangeMessageDroplet");
						
						pTransaction.commit();
					}
				}
			}
		}
		catch (RollbackException exc) {
			exception = true;			
			logError(exc);
		}
		catch (HeuristicMixedException exc) {
			exception = true;			
			logError(exc);
		}
		catch (HeuristicRollbackException exc) {
			exception = true;			
			logError(exc);
		}
		catch (SystemException exc) {
			exception = true;
		    logError(exc);
		}
		finally {
			if (exception) {
				this.logInfo("Invalidating order due to exception in TopStatusChangeMessageDroplet");
				if (order!=null && order instanceof OrderImpl)
					((OrderImpl)order).invalidateOrder();

			} // if
		} // finally
		// if
	}
	
	/**
	 * Returns true if the transaction associated with the current thread
	 * is marked for rollback. This is useful if you do not want to perform
	 * some action (e.g. updating the order) if some other subservice already
	 * needs the transaction rolledback.
	 * @return a <code>boolean</code> value
	 */
	protected boolean isTransactionMarkedAsRollBack() {
	  try {
	    TransactionManager tm = getTransactionManager();
	    if (tm != null) {
	      int transactionStatus = tm.getStatus();
	      if (transactionStatus == javax.transaction.Status.STATUS_MARKED_ROLLBACK)
	        return true;
	    }
	  }
	  catch (SystemException exc) {
	    
	     logError(exc);
	  }
	  return false;
	}
	/**
	 * Helper for junit, calls the checkCartItemMessage method to get the
	 * response
	 *
	 * @return
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 * @throws ServletException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public List<PricingMessageVO> checkCartItemMessage() throws BBBBusinessException, IOException, RepositoryException, ServletException, BBBSystemException, CommerceException {
		
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage starts]");
		
		List<PricingMessageVO> vo = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		try {
			List<PricingMessageVO> flagOffList = new ArrayList<PricingMessageVO>();
			List<PricingMessageVO> oosList = new ArrayList<PricingMessageVO>();
			List<PricingMessageVO> pricechangeVOList = new ArrayList<PricingMessageVO>();
			checkCartItemMessage(pRequest, pResponse);
			mergeMsg(pRequest, flagOffList, oosList, pricechangeVOList);
			flagOffList.addAll(oosList);
			flagOffList.addAll(pricechangeVOList);
			if(!flagOffList.isEmpty()){
				vo = flagOffList;
			}
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException"), e);
			throw new BBBSystemException(BBBCoreErrorConstants.CART_ERROR_1000, BBBCoreErrorConstants.ERROR_CART_ERROR_1000);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException"), e);
			throw new BBBBusinessException(BBBCoreErrorConstants.CART_ERROR_1001, BBBCoreErrorConstants.ERROR_CART_ERROR_1001);
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException"), e);
			throw new RepositoryException(BBBCoreErrorConstants.CART_ERROR_1002, BBBCoreErrorConstants.ERROR_CART_ERROR_1002);
		} catch (ServletException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "ServletException"), e);
			throw new ServletException(BBBCoreErrorConstants.ERROR_CART_ERROR_1003);
		} catch (IOException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "IOException"), e);
			throw new IOException(BBBCoreErrorConstants.ERROR_CART_ERROR_1004);
		}catch (CommerceException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "CommerceException"), e);
			throw new CommerceException(BBBCoreErrorConstants.ERROR_CART_ERROR_1005);
		}
		     // code to get the sku name for rest call
				if (null!=vo&&vo.size()>0){
					List<PricingMessageVO> voList = new ArrayList<PricingMessageVO>();

					for (PricingMessageVO pricingMessageVO : vo) {

						SKUDetailVO detailVO =new SKUDetailVO();
						detailVO=getBbbCatalogTools().getSKUDetails(SiteContextManager.getCurrentSiteId(),pricingMessageVO.getSkuId(), false, true , true);
						pricingMessageVO.setDisplayName(detailVO.getDisplayName());
						voList.add(pricingMessageVO);
					}
					return voList;

				}
		return vo;

	}

	/**
	 * @return
	 * @throws BBBBusinessException
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws ServletException
	 * @throws BBBSystemException
	 */
	public List<PricingMessageVO> mergeSaveCartItemMessage(String getPriceChangeMsgOnly) throws BBBBusinessException, IOException, RepositoryException, ServletException, BBBSystemException {
		
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage starts]");
		
		List<PricingMessageVO> vo = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		CheckoutProgressStates prog = (CheckoutProgressStates) pRequest.resolveName("/com/bbb/commerce/order/purchase/CheckoutProgressStates");
		prog.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
		try {
			pRequest.setParameter(REQTYPE, MERGE);
			pRequest.setParameter(JMERGE, JMERGE);
			pRequest.setParameter(GETPRICECHANGEMSGONLY, getPriceChangeMsgOnly);
			service(pRequest, pResponse);
			Object priceMsgVO = pRequest.getObjectParameter(PRICEMESSAGEVO);
			if (priceMsgVO != null && priceMsgVO instanceof PricingMessageVO) {
				vo = new ArrayList<PricingMessageVO>();
				vo.add((PricingMessageVO) priceMsgVO);
			} else if (null != priceMsgVO){
				vo = (List<PricingMessageVO>) priceMsgVO;
			}		
			
		} catch (ServletException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "ServletException"), e);
			throw new ServletException(BBBCoreErrorConstants.ERROR_CART_ERROR_1003);
		} catch (IOException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "IOException"), e);
			throw new IOException(BBBCoreErrorConstants.ERROR_CART_ERROR_1004);
		}
		return vo;
	}

	/**
	 * Helper for junit, calls the checkSavedItemMessages method to get the
	 * response
	 *
	 * @param pIds
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws ServletException
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws CommerceException
	 */
	public List<PricingMessageVO> checkSavedItemMessages() throws BBBBusinessException, BBBSystemException, ServletException, RepositoryException, IOException, CommerceException {
		
		    logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages starts]");

			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
			List<PricingMessageVO> vo = null;
				List<GiftListVO> giftListVO = getSavedItemsSessionBean().getItems();
				try {

					List<PricingMessageVO> flagOffList = new ArrayList<PricingMessageVO>();
					List<PricingMessageVO> oosList = new ArrayList<PricingMessageVO>();
					List<PricingMessageVO> pricechangeVOList = new ArrayList<PricingMessageVO>();
					checkSavedItemMessages(giftListVO, pRequest, pResponse);
					mergeMsg(pRequest, flagOffList, oosList, pricechangeVOList);
					flagOffList.addAll(oosList);
					flagOffList.addAll(pricechangeVOList);
					if(!flagOffList.isEmpty()){
						vo = flagOffList;
					}
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException"), e);
					throw new BBBSystemException(BBBCoreErrorConstants.GIFT_ERROR_1000, BBBCoreErrorConstants.ERROR_GIFT_ERROR_1000);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException"), e);
					throw new BBBBusinessException(BBBCoreErrorConstants.GIFT_ERROR_1001, BBBCoreErrorConstants.ERROR_GIFT_ERROR_1001);
				} catch (RepositoryException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException"), e);
					throw new RepositoryException(BBBCoreErrorConstants.GIFT_ERROR_1002, BBBCoreErrorConstants.ERROR_GIFT_ERROR_1002);
				} catch (ServletException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "ServletException"), e);
					throw new ServletException(BBBCoreErrorConstants.ERROR_GIFT_ERROR_1003);
				} catch (IOException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "IOException"), e);
					throw new IOException(BBBCoreErrorConstants.ERROR_GIFT_ERROR_1004);
				} catch (CommerceException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "CommerceException"), e);
					throw new CommerceException(BBBCoreErrorConstants.ERROR_GIFT_ERROR_1005);
				} finally {

					logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages ends]");
					
				}

			return vo;

	}
	
	/**
	 * Reprice the order
	 * @param pOrder
	 * @param pProfile
	 */
	private void repriceCartMoveItemsFromCart(Order pOrder, Profile pProfile) {
		Map map = new HashMap();
		map.put("Order", pOrder);
		map.put("Profile", pProfile);
		map.put(PricingConstants.PRICING_OPERATION_PARAM,  PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING);
		try {
			getOrderManager().getPipelineManager().runProcess("repriceOrder", map);
		} catch (RunProcessException e) {
			logError(LogMessageFormatter.formatMessage(null, "RunProcessException in StoreGiftlistFormHandler while repriceCartMoveItemsFromCart", BBBCoreErrorConstants.ACCOUNT_ERROR_1256 ), e);
			
		}
	}

	
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/**
	 * @return the localLockManager
	 */
	public ClientLockManager getLocalLockManager() {
		return localLockManager;
	}

	/**
	 * @param localLockManager the localLockManager to set
	 */
	public void setLocalLockManager(ClientLockManager localLockManager) {
		this.localLockManager = localLockManager;
	}
	
	@SuppressWarnings("unchecked")
	private boolean cartPriceChangeMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws BBBSystemException, BBBBusinessException, RepositoryException, ServletException, IOException, CommerceException {
		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage starts]");
		
		int inStockStatus = 0;
		Double currentPrice = 0.0;
		boolean getPriceChangeMsgOnly =false;
		if(BBBUtility.isNotEmpty(pRequest.getParameter(GETPRICECHANGEMSGONLY))){
			getPriceChangeMsgOnly = Boolean.valueOf(pRequest.getParameter(GETPRICECHANGEMSGONLY)) ;
		}
		//String getPriceChangeMsgOnly = pRequest.getParameter(GETPRICECHANGEMSGONLY);
		String siteId = SiteContextManager.getCurrentSiteId();

		OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
		Order order = cart.getCurrent();
		
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		List<String> restrictedSkuList= (List<String>) sessionBean.getValues().get("internationalShipRestrictedSku");
		List<String> bopusSkuList=	(List<String>) sessionBean.getValues().get("bopusSkuNoIntShip");
		
		boolean hasIntShipRestrictedSku=false;
		boolean hasIntShipBopusSku=false;

		if(restrictedSkuList!=null && !restrictedSkuList.isEmpty()){
			hasIntShipRestrictedSku=true;
		}
		if(bopusSkuList!=null && !bopusSkuList.isEmpty()){
			hasIntShipBopusSku=true;
		}
		
		pRequest.setParameter(INT_SHIP_RESTRICTED_SKU, hasIntShipRestrictedSku);
		pRequest.setParameter(INT_SHIP_BOPUS_SKU, hasIntShipBopusSku);

		List<PricingMessageVO> priceChangeVO = new ArrayList<PricingMessageVO>();
		List<PricingMessageVO> oosVO = new ArrayList<PricingMessageVO>();
		boolean doUpdate = false;		
		/*boolean rollback = false;
		boolean isTransact = false;*/
		
		/*synchronized (order) {*/
			List<CommerceItem> commerceItemLists = (List<CommerceItem>) order.getCommerceItems();
			/*if(null != commerceItemLists && commerceItemLists.size() > 0){
				isTransact = true;
			}
			if(isTransact){
				TransactionDemarcation td = new TransactionDemarcation();
				td.begin(getTransactionManager());
			}*/
			for (CommerceItem commerceItem : commerceItemLists) {
				if (commerceItem instanceof BBBCommerceItem) {
					boolean inStock = true;
					boolean toplink = true;
					BBBCommerceItem bbbCommerceItem = (BBBCommerceItem) commerceItem;

					PricingMessageVO vo = new PricingMessageVO();

					String productId = bbbCommerceItem.getAuxiliaryData().getProductId();

					String skuId = bbbCommerceItem.getCatalogRefId();
			
					String registryId = bbbCommerceItem.getRegistryId();
					vo.setQuantity(bbbCommerceItem.getQuantity());
					vo.setCommerceItemId(bbbCommerceItem.getId());
					vo.setSkuId(skuId);
					vo.setProdId(productId);
			
					String parentCategory = getBbbCatalogTools().getShopSimilarItemCategory(productId, siteId);
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
					vo.setParentCat(parentCategory);
					SKUDetailVO skuVO = getBbbCatalogTools().getSKUDetails(SiteContextManager.getCurrentSiteId(),false,skuId);
					if(skuVO != null){
						vo.setBopus(skuVO.isBopusAllowed());
						vo.setDisplayName(skuVO.getDisplayName());
					}
					ItemPriceInfo itemPriceInfo = bbbCommerceItem.getPriceInfo();
					if(itemPriceInfo == null){
						Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
						repriceCartMoveItemsFromCart(order, profile);
						itemPriceInfo = bbbCommerceItem.getPriceInfo();
					}
					Double listPrice = itemPriceInfo.getListPrice();
					Double salePrice = itemPriceInfo.getSalePrice();

					boolean onSale = itemPriceInfo.isOnSale();
					currentPrice = BBBUtility.checkCurrentPrice(onSale, listPrice, salePrice);
					vo.setCurrentPrice(currentPrice);
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
						if (toplink && bbbCommerceItem.isMsgShownOOS() && inStock) {
							bbbCommerceItem.setMsgShownOOS(false);
							doUpdate = true;
						}
						if (toplink && bbbCommerceItem.isMsgShownFlagOff()) {
							bbbCommerceItem.setMsgShownFlagOff(false);
							doUpdate = true;
						}
						// Commented for Mobile change.... Please Validate!!
						// updateOrder(order, pRequest);
						if (!inStock && BBBUtility.isEmpty(bbbCommerceItem.getStoreId()) && !(toplink && bbbCommerceItem.isMsgShownOOS())) {
							vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
							vo.setInStock(false);
							bbbCommerceItem.setMsgShownOOS(true);
							doUpdate = true;
							oosVO.add(vo);
							logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkCartItemMessage ends]");
					
						}
					} else if (!(toplink && bbbCommerceItem.isMsgShownFlagOff())) {
						vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
						vo.setFlagOff(true);
						vo.setInStock(false);
						bbbCommerceItem.setMsgShownFlagOff(true);
						doUpdate = true;
					}
					if (!vo.isFlagOff() && !bbbCommerceItem.isMsgShownFlagOff() && bbbCommerceItem.getPrevPrice() != currentPrice && Double.compare(bbbCommerceItem.getPrevPrice(), 0.0) != BBBCoreConstants.ZERO) {
						PricingMessageVO psvo = (PricingMessageVO) vo.clone();
						psvo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_ITEM_CHANGE, pRequest.getLocale().getLanguage(), null));
						psvo.setPrevPrice(bbbCommerceItem.getPrevPrice());
						psvo.setInStock(true);
						psvo.setPriceChange(true);
						psvo.setCurrentPrice(currentPrice);
						priceChangeVO.add(psvo);
						bbbCommerceItem.setPrevPrice(currentPrice);
					}
				}
			}
			String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
			if(channel != null && doUpdate && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
				//updateOrder(order, pRequest);
				doUpdate = true;
			}
			/*}*/
			if (priceChangeVO.size() > 0) {
				pRequest.setParameter(PRICECHANGEVOLIST, priceChangeVO);
			}
			return doUpdate;
		}
	
	
	/**
	 * 
	 * @param giftListVO
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException
	 */
	private boolean checkSavedItemPriceChangeMessages(List<GiftListVO> giftListVO, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws BBBSystemException, BBBBusinessException, RepositoryException, ServletException, IOException, CommerceException {

		logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemMessages starts]");
		double currentPrice = 0.0;
		int inStockStatus;
		String siteId = SiteContextManager.getCurrentSiteId();

		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		boolean updateCheckSavedItemMsgs = false; 
		OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
		Order order = cart.getCurrent();
		if (giftListVO != null && !giftListVO.isEmpty()) {

			List<PricingMessageVO> priceChangeVO = new ArrayList<PricingMessageVO>();
			for (GiftListVO savedItem : giftListVO) {
				boolean inStock = true;
				PricingMessageVO vo = new PricingMessageVO();
				vo.setWishListId(savedItem.getWishListItemId());
				String productId = savedItem.getProdID();

				String skuId = savedItem.getSkuID();

				String registryId = savedItem.getRegistryID();
				vo.setSkuId(skuId);
				vo.setProdId(productId);
				vo.setQuantity(savedItem.getQuantity());
				SKUDetailVO skuVO = getBbbCatalogTools().getSKUDetails(SiteContextManager.getCurrentSiteId(),false,skuId);
				if(skuVO != null){
					vo.setBopus(skuVO.isBopusAllowed());
					vo.setDisplayName(skuVO.getDisplayName());
				}
				
				/*TransactionManager tm = getTransactionManager();*/
			/*	TransactionDemarcation td = new TransactionDemarcation();*/
				/*boolean rollback = false;*/
				
				try {
					
				/*td.begin(getTransactionManager());
				synchronized (order) {*/

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
						/* BPSI-3285 DSK | Handle Pricing message for Personalized Item */
						currentPrice = BBBUtility.checkCurrentPriceForSavedItem(onSale, listPrice, salePrice,skuVO.getPersonalizationType(),savedItem.getPersonalizePrice(), savedItem.getReferenceNumber());
						vo.setCurrentPrice(currentPrice);

						if (savedItem.isMsgShownOOS() && inStock) {

							if (!profile.isTransient()) {
								try {
									MutableRepositoryItem item = (MutableRepositoryItem) getGiftListManager().getGiftitem(getGiftListManager().getGiftlistItemId(getGiftListManager().getWishlistId(profile.getRepositoryId()), skuId, productId, savedItem.getSiteId()));
									item.setPropertyValue(BBBCoreConstants.MSGSHOWNOOS, false);
									((MutableRepository) getGiftListManager().getGiftlistTools().getGiftlistRepository()).updateItem(item);
								} catch (RepositoryException e) {
									logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP, BBBCoreErrorConstants.GIFT_ERROR_1000), e);
									
									logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP);
								} catch (CommerceException e) {
										logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
										logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM);
									throw e;
								}
							} else {
								savedItem.setMsgShownOOS(false);
							}
						}

						if (savedItem.isMsgShownFlagOff()) {
							if (!profile.isTransient()) {
								try {
									MutableRepositoryItem item = (MutableRepositoryItem) getGiftListManager().getGiftitem(getGiftListManager().getGiftlistItemId(getGiftListManager().getWishlistId(profile.getRepositoryId()), skuId, productId, savedItem.getSiteId()));
									item.setPropertyValue(BBBCoreConstants.MSGSHOWNFLAGOFF, false);
									((MutableRepository) getGiftListManager().getGiftlistTools().getGiftlistRepository()).updateItem(item);
								} catch (RepositoryException e) {
									logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP, BBBCoreErrorConstants.GIFT_ERROR_1000), e);
									//logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP);
								} catch (CommerceException e) {
									logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
									//logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM);
									throw e;
								}
							} else {
								savedItem.setMsgShownFlagOff(false);
							}

						}

						if (!inStock && !(savedItem.isMsgShownOOS()) ) {
							if (!profile.isTransient()) {
								try {
									MutableRepositoryItem item = (MutableRepositoryItem) getGiftListManager().getGiftitem(getGiftListManager().getGiftlistItemId(getGiftListManager().getWishlistId(profile.getRepositoryId()), skuId, productId, savedItem.getSiteId()));
									item.setPropertyValue(BBBCoreConstants.MSGSHOWNOOS, true);
									((MutableRepository) getGiftListManager().getGiftlistTools().getGiftlistRepository()).updateItem(item);
								} catch (RepositoryException e) {
										logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP, BBBCoreErrorConstants.GIFT_ERROR_1000), e);
//										logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP);
								} catch (CommerceException e) {
										logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
//	   								logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM);
									
									throw e;
								}
							} else {
								savedItem.setMsgShownOOS(true);
							}
							vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
							vo.setInStock(false);
							pRequest.setParameter(PRICEMESSAGEVO, vo);
							
							logDebug("CLS=[TopStatusChangeMessageDroplet] MTHD=[checkSavedItemPriceChangeMessages ends]");
							
						}
						//OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
						//Order order = cart.getCurrent();
						List<CommerceItem> commerceItemLists =  order.getCommerceItems();
						
						for (CommerceItem commerceItem : commerceItemLists) {
							if (commerceItem instanceof BBBCommerceItem && commerceItem.getCatalogRefId().equalsIgnoreCase(savedItem.getSkuID())) {
								BBBCommerceItem bbbcommerceItem = (BBBCommerceItem)commerceItem;
								if(!bbbcommerceItem.isMsgShownOOS()){
									bbbcommerceItem.setMsgShownOOS(true);
									updateCheckSavedItemMsgs = true;
								}
							}
						}
						
					} else if (!(savedItem.isMsgShownFlagOff())) {
						Double listPrice = ((BBBCatalogTools) getBbbCatalogTools()).getListPrice(productId, skuId);
						Double salePrice = ((BBBCatalogTools) getBbbCatalogTools()).getSalePrice(productId, skuId);

						boolean onSale = ((BBBCatalogTools) getBbbCatalogTools()).isSkuOnSale(productId, skuId);
						currentPrice = BBBUtility.checkCurrentPriceForSavedItem(onSale, listPrice, salePrice, skuVO.getPersonalizationType(),savedItem.getPersonalizePrice(), savedItem.getReferenceNumber());
						vo.setCurrentPrice(currentPrice);
						vo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_NO_LONGER, pRequest.getLocale().getLanguage(), null));
						vo.setFlagOff(true);
						vo.setInStock(false);

						if (!profile.isTransient()) {
							try {
							    MutableRepositoryItem item = (MutableRepositoryItem) getGiftListManager().getGiftitem(getGiftListManager().getGiftlistItemId(getGiftListManager().getWishlistId(profile.getRepositoryId()), skuId, productId, savedItem.getSiteId(),savedItem.getRegistryID(), savedItem.getLtlShipMethod(),savedItem.getReferenceNumber()));
								item.setPropertyValue(BBBCoreConstants.MSGSHOWNFLAGOFF, true);
								((MutableRepository) getGiftListManager().getGiftlistTools().getGiftlistRepository()).updateItem(item);
							} catch (RepositoryException e) {
								logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP, BBBCoreErrorConstants.GIFT_ERROR_1000), e);
								//logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP);
								
							} catch (CommerceException e) {
									logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM, BBBCoreErrorConstants.GIFT_ERROR_1001), e);
								//	logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM);
								
								throw e;
							}
						} else {
							savedItem.setMsgShownFlagOff(true);
						}
						//OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
						//Order order = cart.getCurrent();
						List<CommerceItem> commerceItemLists =  order.getCommerceItems();

						for (CommerceItem commerceItem : commerceItemLists) {
							if (commerceItem instanceof BBBCommerceItem && commerceItem.getCatalogRefId().equalsIgnoreCase(savedItem.getSkuID())) {
								BBBCommerceItem bbbcommerceItem = (BBBCommerceItem)commerceItem;
								if(savedItem.isMsgShownFlagOff() && !bbbcommerceItem.isMsgShownFlagOff()){
									bbbcommerceItem.setMsgShownFlagOff(true);
								}
							}
						}
						updateCheckSavedItemMsgs = true;
					}
					
					String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
					/*if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)) && updateCheckSavedItemMsgs){
						getOrderManager().updateOrder(order);
					}*/
					/*}*/
				} catch (CommerceException e){
					this.logError(LogMessageFormatter.formatMessage(pRequest, "CommerceException"), e);
					/*rollback = true;*/
				}
				/*catch (TransactionDemarcationException e) {
					this.logError("TransactionDemarcationException from checkSavedItemPriceChangeMessages() method in TopStatusChangeMessageDroplet class", e);
					rollback = true;
				} finally{
					this.commitTransaction(rollback, order);
				}*/

				// Need to be removed
				// currentPrice = 1.1;
				if (!vo.isFlagOff() && !savedItem.isMsgShownFlagOff() && (Double.compare(savedItem.getPrevPrice(), currentPrice) != BBBCoreConstants.ZERO && Double.compare(savedItem.getPrevPrice(), 0.0) != BBBCoreConstants.ZERO)) {
					PricingMessageVO psvo = (PricingMessageVO) vo.clone();
					psvo.setMessage(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_ITEM_CHANGE, pRequest.getLocale().getLanguage(), null));

					logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemPriceChangeMessages ends]");
					psvo.setPrevPrice(savedItem.getPrevPrice());
					psvo.setCurrentPrice(currentPrice);
					psvo.setPriceChange(true);
					psvo.setInStock(true);
					priceChangeVO.add(psvo);

				}
			
					logDebug("CLS=[StatusChangeMessageDroplet] MTHD=[checkSavedItemPriceChangeMessages ends]");
				
			}
			if (priceChangeVO.size() > 0) {
				pRequest.setParameter(PRICECHANGEVOLIST, priceChangeVO);
			}

		}
		return updateCheckSavedItemMsgs;
	}
}

