package com.bbb.commerce.cart.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.wishlist.BBBWishlistManager;

/**
 * @author dgoel6
 * @version $Revision: #1 $
 */
public class FetchSavedItemsProductIdsDroplet extends BBBDynamoServlet {

	private BBBWishlistManager wishListManager;

	
	public BBBWishlistManager getWishListManager() {
		return wishListManager;
	}
	

	public void setWishListManager(BBBWishlistManager wishListManager) {
		this.wishListManager = wishListManager;
	}
	


	/**
	 * this method fetches the product ids of saved items
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


		String prodIdsList = getWishListManager().getProdIDsSavedItems();

		if ((null != prodIdsList) && (!prodIdsList.isEmpty())) {
		
			pRequest.setParameter(BBBCoreConstants.PRODUCT_ID_LIST, prodIdsList);
			pRequest.serviceParameter(BBBCoreConstants.OUTPUT, pRequest,
					pResponse);
		} else {
			pRequest.serviceParameter(BBBCoreConstants.EMPTY, pRequest,
					pResponse);
		}

	}



	
}
