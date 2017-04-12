package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.wishlist.GiftListVO;

/**
 * @author snaya2
 * @version $Revision: #1 $
 */
public class BBBSaveForLaterDisplayDroplet extends BBBDynamoServlet {
	protected static final String WISH_LIST = "wishlist";
	private BBBCheckoutManager mCheckoutManager;
	private BBBOrderManager mOrderManager;
	private BBBCatalogTools catalogTools;
	private BBBSavedItemsSessionBean savedItemsSessionBean;
	private static final String ADDMSG = "addMSG";
	RepositoryItem mUserProfile;
	
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
	
	
	/**
	 * @return the savedItemsSessionBean
	 */
	public BBBSavedItemsSessionBean getSavedItemsSessionBean() {
		return savedItemsSessionBean;
	}

	/**
	 * @param savedItemsSessionBean the savedItemsSessionBean to set
	 */
	public void setSavedItemsSessionBean(
			BBBSavedItemsSessionBean savedItemsSessionBean) {
		this.savedItemsSessionBean = savedItemsSessionBean;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
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
	 * sets BBBCheckoutManager
	 * 
	 * @param mCheckoutManager
	 */
	public void setCheckoutManager(final BBBCheckoutManager mCheckoutManager) {
		this.mCheckoutManager = mCheckoutManager;
	}

	/**
	 * this methods adds list of CommerceItemVO in request for order param being
	 * passes in request
	 * 
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
    public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	
		String msg = pRequest.getParameter(ADDMSG);
		String checkUserSavedItem=pRequest.getParameter("checkUserSavedItem");
		boolean check = true;
		if(!StringUtils.isEmpty(msg)){
			check = false;
		}
		

			// List<GiftListVO> giftList =
			// this.getSavedItemsSessionBean().getSaveItems(check);
			if (null != checkUserSavedItem
					&& checkUserSavedItem.equalsIgnoreCase("true")) {
				
				Integer countSavedItems = 0;
				boolean userWithWishList = false;
				if (getUserProfile().isTransient()) {
					List<GiftListVO> giftList = this.getSavedItemsSessionBean().getGiftListVO();
					if( giftList!=null && giftList.size()>0 ){
					userWithWishList = true;
					countSavedItems =  Integer.valueOf(giftList.size());
					
					}

				} else {
					RepositoryItem wishList = ((RepositoryItem) getUserProfile()
							.getPropertyValue(WISH_LIST));
					if (wishList != null) {
						@SuppressWarnings("unchecked")
						List<RepositoryItem> wishtListItems = (List<RepositoryItem>) (wishList
								.getPropertyValue(BBBCoreConstants.GIFT_LIST_ITEMS));
						if (wishtListItems != null) {
							userWithWishList = true;
							countSavedItems = Integer.valueOf(wishtListItems.size());
						}
					}


				}
				pRequest.setParameter("countSavedItems", countSavedItems);
				pRequest.setParameter("userWithWishList", userWithWishList);
				pRequest.serviceParameter(BBBCoreConstants.OUTPUT,
						pRequest, pResponse);
			} else {

				List<GiftListVO> giftList = this.getSavedItemsSessionBean()
						.getSaveItems(check);

				if ((null != giftList) && (!giftList.isEmpty())) {
					for (GiftListVO giftListVO : giftList) {
						try{
							String productId = giftListVO.getProdID();
							giftListVO.setProductVO(getCatalogTools()
									.getProductDetails(
											SiteContextManager.getCurrentSiteId(),
											productId, true, true, false));
						}catch(BBBBusinessException e){
							logError(LogMessageFormatter.formatMessage(pRequest,
									"BBBBusinessException"), e);
						}catch (BBBSystemException e) {
							logError(LogMessageFormatter.formatMessage(pRequest,
									"BBBSystemException"), e);
						} 
					}
					pRequest.setParameter(BBBCoreConstants.GIFTLIST, giftList);
					pRequest.serviceParameter(BBBCoreConstants.OUTPUT,
							pRequest, pResponse);
				} else {
					pRequest.serviceParameter(BBBCoreConstants.EMPTY, pRequest,
							pResponse);
				}
			}
		

	}
}
