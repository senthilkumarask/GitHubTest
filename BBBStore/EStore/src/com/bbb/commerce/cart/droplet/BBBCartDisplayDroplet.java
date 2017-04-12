package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBStoreInventoryManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.formhandler.BBBShippingGroupFormhandler;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.order.ShippingGroupRelationship;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

/**
 * @author hkapo1
 * @version $Revision: #1 $
 */
public class BBBCartDisplayDroplet extends BBBDynamoServlet {
	
	/** Constant for String ShippipngFormHandler. */
	private static final String SHIPPING_FORM_HANDLER = "/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler";
	/** Constant for String Error While invoking getCartItemVOList method. */
	private static final String ERROR_WHILE_INVOKING_GET_CART_ITEM_VO_LIST_METHOD = "Error While invoking getCartItemVOList method";
	/** Instance for BBBCheckoutManager. */
	private BBBCheckoutManager mCheckoutManager;
	/** Instance for BBBOrderManager. */
	private BBBOrderManager mOrderManager;
	/** Instance for GiftRegistryTools. */
	private GiftRegistryTools mGiftRegistryTools;
	/** Instance for BBBCatalogTools. */
	private BBBCatalogTools catalogTools;
	/** Instance for StatusChangeMessageManager. */
	private StatusChangeMessageManager mStatusManager;
	/** Instance for TransactionManager. */
	private TransactionManager mTransactionManager;
	/** Instance for BBBStoreInventoryManager. */
	private BBBStoreInventoryManager storeInventoryManager;
	/** Instance for SearchStoreManager. */
	private SearchStoreManager searchStoreManager;
	/** Constant for String storeDetails. */
	public final static String STORE_DETAILS ="storeDetails";
	/** Constant for String favStoreStockStatus. */
	public final static String FAV_STORE_STOCK_STATUS = "favStoreStockStatus";


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
	 * 
	 * @return catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	/**
	 * 
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	/**
	 * 
	 * @return mGiftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return mGiftRegistryTools;
	}
	/**
	 * 
	 * @param pGiftRegistryTools the pGiftRegistryTools to set
	 */
	public void setGiftRegistryTools(GiftRegistryTools pGiftRegistryTools) {
		this.mGiftRegistryTools = pGiftRegistryTools;
	}

	/**
	 * Getter for OrderManager.
	 * 
	 * @return the OrderManager
	 */
	public BBBOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * Setter for OrderManager.
	 * 
	 * @param pOrderManager
	 */
	public void setOrderManager(BBBOrderManager pOrderManager) {
		this.mOrderManager = pOrderManager;
	}

	/**
	 * @return BBBCheckoutManager
	 */
	public BBBCheckoutManager getCheckoutManager() {
		return mCheckoutManager;
	}

	/**
	 * sets BBBCheckoutManager.
	 * 
	 * @param mCheckoutManager
	 */
	public void setCheckoutManager(final BBBCheckoutManager mCheckoutManager) {
		this.mCheckoutManager = mCheckoutManager;
	}

	/**
	 * this methods adds list of CommerceItemVO in request for order param being.
	 * passes in request
	 * 
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		String currentSiteId = SiteContextManager.getCurrentSiteId();
		
		if ( pRequest.getObjectParameter(BBBCoreConstants.ORDER)!= null) {
				List<CommerceItemVO> commerceItemVOs = null;
				Order order = (Order) pRequest.getObjectParameter(BBBCoreConstants.ORDER);
				BBBOrderImpl currentOrder =(BBBOrderImpl) order;
				List<String> bopusList=new ArrayList<String>();
				bopusList=checkAndUpdateInternationalOrder(currentOrder,bopusList,pRequest,pResponse);
         
			 order = (Order) currentOrder;
			try {
				String fromCart = pRequest.getParameter(BBBCoreConstants.FROM_CART);
				if(!BBBUtility.isEmpty(fromCart)) {
					commerceItemVOs = setSaveForLaterDetails(pRequest, order);
					mCheckoutManager.processSddItemsOnCart(commerceItemVOs, currentSiteId);
				}else{
					commerceItemVOs = mCheckoutManager.getCartItemVOList(order);
				}
					BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
					List<String> restrictedSkuList= (List<String>) sessionBean.getValues().get(BBBInternationalShippingConstants.INTERNATIONAL_RESTRICTED_SKU);
					List<String> bopusSkuList=	bopusList;
					boolean hasIntShipRestrictedSku=false;
					boolean hasIntShipBopusSku=false;
					if(restrictedSkuList!=null && !restrictedSkuList.isEmpty()){
						hasIntShipRestrictedSku=true;
					}
					if(bopusSkuList!=null && !bopusSkuList.isEmpty()){
						hasIntShipBopusSku=true;
					}
					if(hasIntShipBopusSku || hasIntShipRestrictedSku){
						
						for(CommerceItemVO commerceItemVO:commerceItemVOs){
							final	String commerceItemId=commerceItemVO.getBBBCommerceItem().getId();
							if(hasIntShipRestrictedSku && restrictedSkuList.contains(commerceItemId)){
								commerceItemVO.getSkuDetailVO().setRestrictedForIntShip(true);
							}
							if(hasIntShipBopusSku && bopusSkuList.contains(commerceItemId)){
									commerceItemVO.getSkuDetailVO().setRestrictedForBopusAllowed(true);
							}
						}
					}
					if(hasIntShipBopusSku){
						sessionBean.getValues().remove(BBBInternationalShippingConstants.BOPUSSKU_NOT_INTSHIP);
					}
					 if(hasIntShipRestrictedSku){
						sessionBean.getValues().remove(BBBInternationalShippingConstants.INTERNATIONAL_RESTRICTED_SKU);
					}
					try{
						fetchFavStoreInvMultiSkus(pRequest, profile, (BBBOrder) order, sessionBean);
					} catch (Exception exc) {
						logError("Error fetching favorite store inventory status from cart page", exc);
					} 
					
					
					//change for same day delivery changes for cart
					
					
				pRequest.setParameter(BBBCoreConstants.COMMERCE_ITEM_LIST,
						commerceItemVOs);
				pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest,
						pResponse);
			} catch (BBBSystemException e) {
				logError(
						LogMessageFormatter.formatMessage(pRequest,
								"Error getting item details"), e);

				pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM,
						pRequest, pResponse);
			} catch (BBBBusinessException e) {

				logError(LogMessageFormatter.formatMessage(pRequest,
						ERROR_WHILE_INVOKING_GET_CART_ITEM_VO_LIST_METHOD), e);
				pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM,
						pRequest, pResponse);
			}

		}

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
					this.logDebug("Transaction is getting rollback in BBBCartDisplayDroplet");
					
					if (tm != null){
						tm.rollback();
					}else{
						pTransaction.rollback();  // PR65109: rollback() before invalidateOrder() prevent deadlocks due to thread synchronization in the invalidateOrder()
					}
					if (order!=null && order instanceof OrderImpl){
						this.logDebug("Invalidating order because of transaction is getting rollback");
						((OrderImpl)order).invalidateOrder();
					}
				}
				else {
					this.logDebug("Transaction is getting commited in BBBCartDisplayDroplet");
					if (tm != null){
						tm.commit();
					}else{
						pTransaction.commit();
					}
				}
			}else{
				this.logDebug("Transaction is null in BBBCartDisplayDroplet class || no commit:no rollback");
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
			if (exception && order!=null && order instanceof OrderImpl) {
					this.logDebug("Invalidating order because of transaction is getting rollback for Transactional Exception in BBBCartDisplayDroplet");
					((OrderImpl)order).invalidateOrder();
			}
		}
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
	 * Sets variable for undo operation in cart.
	 * @param pReq
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	//R2.1 changes for junit
	public List<CommerceItemVO> setSaveForLaterDetails(DynamoHttpServletRequest pRequest, Order order) 
			throws BBBSystemException, BBBBusinessException {
		List<CommerceItemVO> commerceItemVOs = null;
		logDebug("Entering : setSaveForLaterDetails()");
		// Filter Item which have been moved
		synchronized (order) {
			boolean rollback = false;
			boolean isTransact = false;
			List<String> pos = (List<String>) pRequest.getSession().getAttribute(BBBCoreConstants.ITEM);
			if (pos != null && !pos.isEmpty()) {
				isTransact = true;
			}
			try {
				/* Start the transaction */
				int i = 1;
				if (isTransact) {
					TransactionDemarcation td = new TransactionDemarcation();
					td.begin(getTransactionManager(),TransactionDemarcation.REQUIRED);
					Iterator<String> comitem = pos.iterator();
					while (comitem.hasNext()) {
						String item = comitem.next();
						try {
							CommerceItem tempComitem = (CommerceItem) order.getCommerceItem(item);
							if(tempComitem != null && tempComitem instanceof BBBCommerceItem) {
								((BBBCommerceItem) tempComitem).setSeqNumber(i);
							}
						} catch (CommerceItemNotFoundException e) {
							comitem.remove();
							continue;
						} catch (InvalidParameterException e) {
							
							logError(BBBCoreErrorConstants.CART_ERROR_1019 + ": Invalid parameter", e);
						
							continue;
						}
						i++;
					}
					getOrderManager().updateOrder(order);
				}
			}catch(CommerceException e){
				
					logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.ERROR_GIFT_CHECK_COM, BBBCoreErrorConstants.GIFT_ERROR_1000), e);
				
				
				rollback = true;
			}catch (TransactionDemarcationException e){
				logError(LogMessageFormatter.formatMessage(pRequest, "TransactionDemarcation Exception found") + e);
				rollback = true;
			}finally {
				if (isTransact) {
					this.commitTransaction(rollback, order);
				}
				rollback = false;
			}
			try {
				/* Start the transaction */
				TransactionDemarcation tdNew = new TransactionDemarcation();
				tdNew.begin(getTransactionManager() , TransactionDemarcation.REQUIRED);
				getOrderManager().updateAvailabilityMapInOrder(pRequest, order);
				commerceItemVOs = mCheckoutManager.getCartItemVOList(order);
				if (commerceItemVOs != null) {
					boolean isDoUpdateOrder = false;
					for (CommerceItemVO item : commerceItemVOs) {
						item.setPriceMessageVO(getStatusManager().checkCartItemMessage(item.getBBBCommerceItem(), pRequest, order));
						if (item != null && StringUtils.isEmpty(item.getBBBCommerceItem().getRegistryId())) {
							item.getBBBCommerceItem().setRegistryId(null);
							isDoUpdateOrder = true;
						}
					}
					if(isDoUpdateOrder){
						getOrderManager().updateOrder(order);
					}
				}
			}catch (CommerceException e){
				logError(LogMessageFormatter.formatMessage(pRequest, "CommerceException found") + e);
				rollback = true;
			}catch (TransactionDemarcationException e){
				logError(LogMessageFormatter.formatMessage(pRequest, "TransactionDemarcation Exception found") + e);
				rollback = true;
			}finally {
				this.commitTransaction(rollback, order);
			}
			Collections.sort(commerceItemVOs, new CommerceItemVO());
			return commerceItemVOs;
		}
	}

	 /** check for International Order.
    *
     * @param order Order details
    * @param bopusSkuList list of bopus commerce items in order
    */
   private  List<String>  checkAndUpdateInternationalOrder(BBBOrderImpl order,List<String> bopusSkuList, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
   	//get all commerce items from cart
   	try {
			List<CommerceItemVO> commerceItemVOs = this.getCheckoutManager().getCartItemVOList(order);
			if(pRequest!=null){
			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			if(sessionBean.isInternationalShippingContext())
			{
				logDebug("Check for Bopus item for Intl Order in checkAndUpdateInternationalOrder()");
				for(CommerceItemVO commerceItemVO:commerceItemVOs){
					if(BBBUtility.isNotEmpty(commerceItemVO.getBBBCommerceItem().getStoreId())){
						bopusSkuList.add(commerceItemVO.getBBBCommerceItem().getId());
						final List<ShippingGroupRelationship> shipGrpRelnList=commerceItemVO.getBBBCommerceItem().getShippingGroupRelationships();
						for(final ShippingGroupRelationship shipGrpReln:shipGrpRelnList){
							final ShippingGroupImpl shipGrp=	(ShippingGroupImpl) shipGrpReln.getShippingGroup();
							if(!(shipGrp instanceof HardgoodShippingGroup)){
								BBBShippingGroupFormhandler	shipGroupFormHandler = (BBBShippingGroupFormhandler) pRequest.resolveName(SHIPPING_FORM_HANDLER);
								shipGroupFormHandler.setOrder(order);
								shipGroupFormHandler.setOldShippingId(shipGrp.getId());
								shipGroupFormHandler.setCommerceItemId(commerceItemVO.getBBBCommerceItem().getId());
								shipGroupFormHandler.setNewQuantity(Long.toString(commerceItemVO.getBBBCommerceItem().getQuantity()));
								try {
									shipGroupFormHandler.handleChangeToShipOnline(pRequest, pResponse);
									logDebug(" Item  changed to ship to online");
								} catch (ServletException e) {
									logError(" servlet exception for commerce id"+commerceItemVO.getBBBCommerceItem().getId()+e);
								} catch (IOException e) {
									logError(" IOException exception for commerce id"+commerceItemVO.getBBBCommerceItem().getId()+e);
								}
							}
						}
					}
				}
			}
		}
		} catch (BBBSystemException e) {
			this.logError("Commerce Exception while getting current order details", e);
		} catch (BBBBusinessException e) {
			this.logError("Commerce Exception while getting current order details", e);
		}
   	return  bopusSkuList;
	}


   		/**
 		 * This method sets favorite store inventory 
 		 * status for commerce items on cart page.
 		 * @param req
 		 * @param profile
 		 * @param order
   		 * @throws Exception 
 		 */
 		private void fetchFavStoreInvMultiSkus(final DynamoHttpServletRequest req, 
 				final Profile profile, final BBBOrder order, final BBBSessionBean sessionBean) 
 						throws Exception {
 			logDebug("BBBCartDisplayDroplet.fetchFavStoreInvMultiSkus() - start with params request: "
 					+ req + "profile: " + profile + "order: " + order);
 			if (!profile.isTransient() && !sessionBean.isInternationalShippingContext()) {
 				String siteId = SiteContextManager.getCurrentSiteId();
 				String favoriteStoreId = getSearchStoreManager().
 						fetchFavoriteStoreId(siteId, profile);
 				 getStoreInventoryManager().fetchFavStoreInvMultiSkus(req, order, siteId, favoriteStoreId); 				
 			} 			
 			logDebug("BBBCartDisplayDroplet.fetchFavStoreInvMultiSkus() - end");		
 		}
	
	/**
	 * 
	 * @return the BBBStoreInventoryManager
	 */
	public BBBStoreInventoryManager getStoreInventoryManager() {
		return storeInventoryManager;
	}

	/**
	 * 
	 * @param the storeInventoryManager to set
	 */
	public void setStoreInventoryManager(
			BBBStoreInventoryManager storeInventoryManager) {
		this.storeInventoryManager = storeInventoryManager;
	}

	/**
	 * 
	 * @return the SearchStoreManager
	 */
	public SearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	/**
	 * 
	 * @param the searchStoreManager to set
	 */
	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}


}

