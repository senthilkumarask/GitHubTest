package com.bbb.commerce.order.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.Address;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.api.BBBAddressVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.TBSConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;

public class TBSGetStoreAddressDroplet extends BBBDynamoServlet{
	
	/**
	 * Property to hold TBSSearchStoreManager reference.
	 */
	private TBSSearchStoreManager mSearchStoreManager;
	
	@Override
	public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) throws ServletException, IOException {
		
		String storeId = (String) pReq.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		StoreDetails storeDetails = null;
		try {
			RepositoryItem storeItem = getSearchStoreManager().getStoreRepository().getItem(storeId, TBSConstants.STORE);
			
			if (storeItem != null) {
				storeDetails = getSearchStoreManager().convertStoreItemToStore(storeItem, null, null);

				pReq.setParameter("storeDetails", storeDetails);
				pReq.serviceParameter("output", pReq, pRes);
			} else {
				pReq.serviceParameter("empty", pReq, pRes);
			}
		} catch (RepositoryException e) {
			pReq.serviceParameter("empty", pReq, pRes);
			logError(e);
		}
		
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

}
