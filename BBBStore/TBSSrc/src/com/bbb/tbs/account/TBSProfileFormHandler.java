package com.bbb.tbs.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.ItemRemovedFromOrder;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.order.TBSOrderManager;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.utils.BBBUtility;

public class TBSProfileFormHandler extends BBBProfileFormHandler {

	/** The Create krisch order success url. */
	private String mCreateKrischOrderSuccessURL;
	
	/** The Create krisch order error url. */
	private String mCreateKrischOrderErrorURL;
	
	/** The Create cmo order success url. */
	private String mCreateCMOOrderSuccessURL;
	
	/** The Create cmo order error url. */
	private String mCreateCMOOrderErrorURL;
	
	
	private Map<String, String> mTbsBBBSiteMap;
	
	private String mClearCartSuccessURL;
	
	private String mClearCartErrorURL;
	
	private String orderId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the clearCartSuccessURL
	 */
	public String getClearCartSuccessURL() {
		return mClearCartSuccessURL;
	}

	/**
	 * @return the clearCartErrorURL
	 */
	public String getClearCartErrorURL() {
		return mClearCartErrorURL;
	}

	/**
	 * @param pClearCartSuccessURL the clearCartSuccessURL to set
	 */
	public void setClearCartSuccessURL(String pClearCartSuccessURL) {
		mClearCartSuccessURL = pClearCartSuccessURL;
	}

	/**
	 * @param pClearCartErrorURL the clearCartErrorURL to set
	 */
	public void setClearCartErrorURL(String pClearCartErrorURL) {
		mClearCartErrorURL = pClearCartErrorURL;
	}


	/* (non-Javadoc)
	 * @see com.bbb.account.BBBProfileFormHandler#postLoginUser(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void postLoginUser(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		super.postLoginUser(pRequest, pResponse);
		
		String contextPath = pRequest.getContextPath();
		
		if(!contextPath.equals("/store")){
			return;
		}
		
		GiftlistManager mgr = getGiftListManager();
		TBSOrderManager orderManager = (TBSOrderManager) getOrderManager();
		String displayName = null;
		String description = null;
		String siteId = null;
		Map<String, Long> removableItemQuantityMap = new HashMap<String, Long>();
		List<CommerceItem> removableItems = new ArrayList<CommerceItem>();
		
		vlogDebug("Updating the gift lists in post login");

		try {
			setGiftlistId((String) ((RepositoryItem) getProfile()
					.getPropertyValue("wishlist")).getPropertyValue("id"));
			synchronized (this.getOrder()) {
				List<CommerceItem> comItemObjList = this.getOrder()
						.getCommerceItems();
				
				for (CommerceItem comItemObj : comItemObjList) {

					if (comItemObj instanceof TBSCommerceItem) {
						TBSCommerceItem item = (TBSCommerceItem) comItemObj;

						String skuId = item.getCatalogRefId();
						if (skuId == null)
							return;
						RepositoryItem sku = (RepositoryItem) item
								.getAuxiliaryData().getCatalogRef();
						if (sku != null) {
							boolean webOffered = false;
					            if (sku.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
					                webOffered = ((Boolean) sku
					                                .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)).booleanValue();
					            }

							if (!webOffered) {
								long quantity = item.getQuantity();
								String productId = item.getAuxiliaryData()
										.getProductId();
								if (productId == null)
									return;

								siteId = item.getAuxiliaryData().getSiteId();
								RepositoryItem product = (RepositoryItem) item
										.getAuxiliaryData().getProductRef();
								if (product != null) {
									productId = product.getRepositoryId();
									displayName = (String) product
											.getPropertyValue(mgr
													.getGiftlistTools()
													.getDisplayNameProperty());
									description = (String) product
											.getPropertyValue(mgr
													.getGiftlistTools()
													.getDescriptionProperty());
								}
								// if item is in giftlist, increment quantity otherwise add
								String giftId = mgr.getGiftlistItemId(
										getGiftlistId(), skuId, productId,
										siteId);
								if (quantity == -9999999) {
									return;
								}

								try {
									if (giftId != null){
										vlogDebug("Updating gift item with sku id :: " + skuId);
										mgr.increaseGiftlistItemQuantityDesired(
												getGiftlistId(), giftId,
												quantity);
									}
									else {
										vlogDebug("Adding the sku with id" + skuId +  " to giftlist");
										String itemId = null;
										if (siteId != null) {
											itemId = mgr.createGiftlistItem(
													skuId, sku, productId,
													product, quantity,
													displayName, description,
													siteId);
										} else {
											itemId = mgr.createGiftlistItem(
													skuId, sku, productId,
													product, quantity,
													displayName, description);
										}
										mgr.addItemToGiftlist(getGiftlistId(),
												itemId);
									}
									// update order quantity
									removableItems.add(item);
									removableItemQuantityMap.put(item.getId(), quantity);
									
								} catch (CommerceException e) {
									if(isLoggingError()){
										logError(e);
									}
								}
							}
						}
					}
				}
				
				orderManager.moveGiftItemsFromOrder(removableItems, removableItemQuantityMap, getOrder());
			}
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError(e);
			}
		} catch (CommerceException e) {
			if(isLoggingError()){
				logError(e);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.bbb.account.BBBProfileFormHandler#preLoginUser(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void preLoginUser(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String lLoginEmail = getStringValueProperty(BBBCoreConstants.LOGIN);
		if(null != pRequest.getHeader("Referer") &&pRequest.getHeader("Referer").contains("shippingGr=multi")){
			vlogDebug("setting success url to multishipping page");
			setSuccessURL(pRequest, "checkoutPageLoginMulti");
		}
		if(!StringUtils.isBlank(lLoginEmail)){
			RepositoryItem lProfileItem = getProfileTools().getItemFromEmail(lLoginEmail);
			
			if(lProfileItem != null){
				Map lUserSitesMap = (Map) lProfileItem.getPropertyValue(getPropertyManager().getUserSiteItemsPropertyName());
				Set lUserSites = lUserSitesMap.keySet();
				
				String lCurrentSiteId = SiteContextManager.getCurrentSiteId();
				String lSiteId = getTbsBBBSiteMap().get(lCurrentSiteId);
				if(!StringUtils.isBlank(lSiteId)){
					if(lUserSites.contains(lSiteId) && !lUserSites.contains(lCurrentSiteId)){
						vlogDebug("setting TBS site association when user logs in with the existing account created in store");
						setAssoSite(lCurrentSiteId);
						
					}
				}
			}
		}
		super.preLoginUser(pRequest, pResponse);
	}

	
	
	/**
	 *  This method checks whether user logged in if not logged then invokes handle login,
	 *  then invokes Kirsch or CMO service based on the value of the customOrderKey parameter.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @return success/failure
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final boolean handleLoginForCustomOrder(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("BBBTBSProfileFormHandler.handleLoginForCustomOrder() method started");
		String key = (String) getValue().get("customOrderKey");
		indirectRequest = true;
		boolean isRollBack = false;
		setSuccessURL(pRequest, key);
		setErrorURL(pRequest, key);

		final TransactionManager tManager = getTransactionManager();
		final TransactionDemarcation tDemarcation = getTransactionDemarcation();
		try {
			if (tManager != null) {
				tDemarcation.begin(tManager, TransactionDemarcation.REQUIRES_NEW);
			}
			synchronized (getOrder()) {
				
				int lProfileSecurityStatusLogin = (Integer) getProfile().getPropertyValue("securityStatus");
				int lSecurityStatusLogin = getPropertyManager().getSecurityStatusLogin();
				
				if(lProfileSecurityStatusLogin < lSecurityStatusLogin){
					handleLoginUser(pRequest, pResponse);
				}
				if (!getFormError()) {
					if ("kirsch".equalsIgnoreCase(key)) {
						((TBSOrderManager) getOrderManager()).createKirschOrder(
								getProfile().getRepositoryId(), getOrder());
					} else if ("cmo".equalsIgnoreCase(key)) {
						((TBSOrderManager) getOrderManager()).createCMOOrder(getProfile().getRepositoryId(), getOrder());
					}
				}
				if(!isRollBack) {
					getOrderManager().updateOrder(getOrder());
				}
			}
		} catch (TransactionDemarcationException e) {
			isRollBack = true;
			throw new ServletException(e);
		} catch (CommerceException e) {
			isRollBack = true;
			addFormException(new DropletException("There is a problem while adding "+ key + " Order to Cart. Please try again later"));
		} catch (BBBSystemException e) {
			isRollBack = true;
			addFormException(new DropletException(e.getMessage()));
		}finally {
			try {
				if (tManager != null) {
					tDemarcation.end(isRollBack);
				}
			} catch (TransactionDemarcationException e) {
				logError(e);
			}
		}
		return checkFormRedirect(getLoginSuccessURL(), getLoginErrorURL(), pRequest,
				pResponse);
	}
	
	
	/**
	 * This method invokes the Kirsch Service and this method is invoked only
	 * when user is already logged in.
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
	 */
	public boolean handleCreateKirschOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		this.logDebug("BBBTBSProfileFormHandler.handleCreateKirschOrder() method started");
		boolean isRollBack = false;
		 // String siteId = SiteContextManager.getCurrentSiteId();
		if (getFromPage() != null) {
    		setCreateKrischOrderSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
    		setCreateKrischOrderErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));
		}

		final TransactionManager tManager = getTransactionManager();
		final TransactionDemarcation tDemarcation = getTransactionDemarcation();

		try {
			if (tManager != null) {
				tDemarcation.begin(tManager, TransactionDemarcation.REQUIRES_NEW);
			}

			synchronized (getOrder()) {

				if (!getFormError()) {
					((TBSOrderManager) getOrderManager()).createKirschOrder(getProfile().getRepositoryId(), getOrder());
				}
				if (!isRollBack) {
					getOrderManager().updateOrder(getOrder());
				}
			}

		} catch (TransactionDemarcationException e) {
			isRollBack = true;
			throw new ServletException(e);
		} catch (CommerceException e) {
			isRollBack = true;
			addFormException(new DropletException("There is a problem while adding Kirsch Order to Cart. Please try again later"));
		} catch (BBBSystemException e) {
			isRollBack = true;
			addFormException(new DropletException(e.getMessage()));
		} finally {
			try {
				if (tManager != null) {
					tDemarcation.end(isRollBack);
				}
			} catch (TransactionDemarcationException e) {
				logError(e);
			}
		}
		return checkFormRedirect(getCreateKrischOrderSuccessURL(), getCreateKrischOrderErrorURL(), pRequest, pResponse);
	}
	
	
	/**
	 * This method invokes the CMO Service and this method is invoked only when
	 * user is already logged in.
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
	 */
	public boolean handleCreateCMOOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		this.logDebug("BBBTBSProfileFormHandler.handleCreateCMOOrder() method started");
		boolean isRollBack = false;
		if (getFromPage() != null) {
    		setCreateCMOOrderSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
    		setCreateCMOOrderErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));
		}
		final TransactionManager tManager = getTransactionManager();
		final TransactionDemarcation tDemarcation = getTransactionDemarcation();

		try {
			if (tManager != null) {
				tDemarcation.begin(tManager, TransactionDemarcation.REQUIRES_NEW);
			}

			synchronized (getOrder()) {

				if (!getFormError()) {
					((TBSOrderManager) getOrderManager()).createCMOOrder(getProfile().getRepositoryId(), getOrder());
				}
				if (!isRollBack) {
					getOrderManager().updateOrder(getOrder());
				}
			}

		} catch (TransactionDemarcationException e) {
			isRollBack = true;
			throw new ServletException(e);
		} catch (CommerceException e) {
			isRollBack = true;
			addFormException(new DropletException("There is a problem while adding CMO Order to Cart. Please try again later"));
		} catch (BBBSystemException e) {
			isRollBack = true;
			addFormException(new DropletException(e.getMessage()));
		} finally {
			try {
				if (tManager != null) {
					tDemarcation.end(isRollBack);
				}
			} catch (TransactionDemarcationException e) {
				logError(e);
			}
		}
		return checkFormRedirect(getCreateCMOOrderSuccessURL(), getCreateCMOOrderErrorURL(), pRequest, pResponse);
	}
	
	
	/** This method is overridden from BBBProfileFormHandler to create the BBB siteAssociation when a profile created in TBS site. 
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */
    @Override
	protected void postCreateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException ,IOException {
		
		final String emailOptInFlag = this.isEmailOptIn() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
        final String emailOptInBabyCAFlag = this.isEmailOptIn_BabyCA() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
        
        String siteId = SiteContextManager.getCurrentSiteId();
        final String sEmail = (String) this.getValueProperty(this.getPropertyManager().getEmailAddressPropertyName());
        String memberId = null;
        if (!BBBUtility.isEmpty(this.getSessionBean().getLegacyMemberId())) {
            memberId = this.getSessionBean().getLegacyMemberId();
        }
        
        try {
        	RepositoryItem lProfileItem = getProfileTools().getItemFromEmail(sEmail);
        	Map userSiteItemsMap = null;
        	if(lProfileItem  != null ){
        		userSiteItemsMap = (Map) lProfileItem.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);

        		if(userSiteItemsMap == null){
        			userSiteItemsMap = new HashMap();
        		}
        		((BBBProfileTools)getProfileTools()).createSiteItemRedirect(sEmail, siteId, memberId, null, emailOptInFlag,emailOptInBabyCAFlag, lProfileItem);
        		String lBBBSite = getTbsBBBSiteMap().get(siteId);
        		if(!StringUtils.isBlank(lBBBSite)){
        			if(!userSiteItemsMap.containsKey(lBBBSite)){
        				((BBBProfileTools)getProfileTools()).createSiteItemRedirect(sEmail, lBBBSite, memberId, null, emailOptInFlag,emailOptInBabyCAFlag, lProfileItem);
        			}
        		}
        	}
		} catch (BBBSystemException e) {
			 this.logError(LogMessageFormatter.formatMessage(pRequest,
                     BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE,
                     BBBCoreErrorConstants.ACCOUNT_ERROR_1128), e);
             this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE,
                     BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE));
             getErrorMap().put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR,
                     BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE);
		}
        
        super.postCreateUser(pRequest, pResponse);
		
	}

	String mGiftlistId = "";

	/**
	 * Sets property giftlistId.
	 * 
	 * @param pGiftlistId
	 *            The property to store the giftlistId property of the current
	 *            giftlist.
	 * @beaninfo description: The property to store the giftlistId property of
	 *           the current giftlist.
	 **/
	public void setGiftlistId(String pGiftlistId) {
		mGiftlistId = pGiftlistId;
	}

	/**
	 * Returns property giftlistId.
	 * 
	 * @return The value of the property giftlistId.
	 **/
	public String getGiftlistId() {
		return mGiftlistId;
	}

	boolean mSendItemRemovedMessages = true;

	/**
	 * Specify whether to send ItemRemovedFromOrder messages when moving items
	 * from the shopping cart to a gift list or wish list. The default value is
	 * true; applications that require Dynamo 5.1 behavior, where a message was
	 * not sent, can set this property to false instead.
	 **/

	public void setSendItemRemovedMessages(boolean pSendItemRemovedMessages) {
		mSendItemRemovedMessages = pSendItemRemovedMessages;
	}

	/**
	 * Query whether to send ItemRemovedFromOrder messages when moving items
	 * from the shopping cart to a gift list or wish list.
	 **/

	public boolean isSendItemRemovedMessages() {
		return mSendItemRemovedMessages;
	}


	String mItemRemovedFromOrderEventType = ItemRemovedFromOrder.TYPE;

	/**
	 * Set the JMS message name for the item removed from order message
	 **/

	public void setItemRemovedFromOrderEventType(String pItemRemovedFromOrderEventType) {
		mItemRemovedFromOrderEventType = pItemRemovedFromOrderEventType;
	}

	/**
	 * Returns the JMS message name for the item removed from order message.
	 **/

	public String getItemRemovedFromOrderEventType() {
		return mItemRemovedFromOrderEventType;
	}

	/**
	 * Gets the creates the krisch order success url.
	 *
	 * @return the creates the krisch order success url
	 */
	public String getCreateKrischOrderSuccessURL() {
		return mCreateKrischOrderSuccessURL;
	}

	/**
	 * Sets the creates the krisch order success url.
	 *
	 * @param pCreateKrischOrderSuccessURL the new creates the krisch order success url
	 */
	public void setCreateKrischOrderSuccessURL(String pCreateKrischOrderSuccessURL) {
		mCreateKrischOrderSuccessURL = pCreateKrischOrderSuccessURL;
	}

	/**
	 * Gets the creates the krisch order error url.
	 *
	 * @return the creates the krisch order error url
	 */
	public String getCreateKrischOrderErrorURL() {
		return mCreateKrischOrderErrorURL;
	}

	/**
	 * Sets the creates the krisch order error url.
	 *
	 * @param pCreateKrischOrderErrorURL the new creates the krisch order error url
	 */
	public void setCreateKrischOrderErrorURL(String pCreateKrischOrderErrorURL) {
		mCreateKrischOrderErrorURL = pCreateKrischOrderErrorURL;
	}

	/**
	 * Gets the creates the cmo order success url.
	 *
	 * @return the creates the cmo order success url
	 */
	public String getCreateCMOOrderSuccessURL() {
		return mCreateCMOOrderSuccessURL;
	}

	/**
	 * Sets the creates the cmo order success url.
	 *
	 * @param pCreateCMOOrderSuccessURL the new creates the cmo order success url
	 */
	public void setCreateCMOOrderSuccessURL(String pCreateCMOOrderSuccessURL) {
		mCreateCMOOrderSuccessURL = pCreateCMOOrderSuccessURL;
	}

	/**
	 * Gets the creates the cmo order error url.
	 *
	 * @return the creates the cmo order error url
	 */
	public String getCreateCMOOrderErrorURL() {
		return mCreateCMOOrderErrorURL;
	}

	/**
	 * Sets the creates the cmo order error url.
	 *
	 * @param pCreateCMOOrderErrorURL the new creates the cmo order error url
	 */
	public void setCreateCMOOrderErrorURL(String pCreateCMOOrderErrorURL) {
		mCreateCMOOrderErrorURL = pCreateCMOOrderErrorURL;
	}
	
	

	public Map<String, String> getTbsBBBSiteMap() {
		return mTbsBBBSiteMap;
	}


	public void setTbsBBBSiteMap(Map<String, String> pTbsBBBSiteMap) {
		mTbsBBBSiteMap = pTbsBBBSiteMap;
	}
	
	/**
	 * This method is user to clear the current order and If the user is logged-in then logout the user.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleClearCart(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		vlogDebug("TBSProfileFormHandler :: handleClearCart() method :: START");
		
		TransactionDemarcation td = new TransactionDemarcation();
		BBBOrderImpl order = (BBBOrderImpl) getOrder();
		boolean rollback = true;
		String logoutSuccessURL = getClearCartSuccessURL();
		try {
			td.begin(getTransactionManager());
			String orderId = order.getId();
			synchronized(order){
				//removing the order only for anonymous user
				if(getProfile().isTransient()){
					getOrderManager().removeOrder(orderId);
				} else {
					//removing overridden details for the logged-in user
					((TBSOrderManager)getOrderManager()).removeOverrideItems(order);
					order.updateVersion();
					getOrderManager().updateOrder(order);
				}
			}
			vlogDebug("Order :: "+ orderId +" has been removed ");
			postLogoutUser(pRequest, pResponse);
			
		    if (StringUtils.isBlank(logoutSuccessURL)){
		    	logoutSuccessURL = pRequest.getContextPath();
		    }
		    //removing the cart cookie
		    removeOrderCookie(pRequest, pResponse);
		    if (!StringUtils.isBlank(logoutSuccessURL) && !getProfile().isTransient()){
		    	pRequest.getSession().removeAttribute(BBBCoreConstants.USER_TOKEN_BVRR);
		    	pRequest.setParameterDelimiter("&");
		        pRequest.addQueryParameter("DPSLogout", "true");
		        logoutSuccessURL = pRequest.encodeURL(logoutSuccessURL);
		    }
			rollback = false;
		} catch (CommerceException e) {
			vlogError("CommerceException occurred while clearing cart :: "+e);
			addFormException(new DropletException("There are some errors while clearing the cart, please try again."));
		} catch (TransactionDemarcationException e) {
			vlogError("TransactionDemarcationException occurred while clearing cart :: "+e);
		} finally {
    		try {
				td.end(rollback);
			} catch (TransactionDemarcationException tde) {
				vlogError("TransactionDemarcationException "+tde);
			}
    	}
		vlogDebug("TBSProfileFormHandler :: handleClearCart() method :: END");
		return checkFormRedirect(logoutSuccessURL, getClearCartErrorURL(), pRequest, pResponse);
	}
	
	/**
	 * this method is overridden to remove the override items.
	 */
	public boolean handleLogout(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        
		((TBSOrderManager)getOrderManager()).removeOverrideItems(getOrder());
        return super.handleLogout(pRequest, pResponse);
    }
	
	public boolean handleRegistration(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {		
		if(getOrderId() != null){
			BBBOrder order;
			try {
				order = (BBBOrder) getOrderManager().loadOrder(getOrderId());
				setBBBOrder(order);
			} catch (CommerceException e) {
				vlogError("Getting error while loading order :: "+e);
			}
		}
		return super.handleRegistration(pRequest, pResponse);
	}
	

}
