package com.bbb.commerce.order.purchase;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderManager;
import atg.core.util.Address;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.PropertyManager;
import atg.userprofiling.address.AddressTools;

import com.bbb.account.api.BBBAddressVO;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;

public class TBSShippingAddressDroplet extends BBBShippingAddressDroplet {
	
	
	/**
	 * Property to hold TBSSearchStoreManager reference.
	 */
	private TBSSearchStoreManager mSearchStoreManager;
	
	/** Property to hold OrderManager reference. */
	private OrderManager mOrderManager;
	
	/** Property to hold TransactionManager reference. */
	private TransactionManager mTransactionManager;
	
	
	/* (non-Javadoc)
	 * @see com.bbb.commerce.order.purchase.BBBShippingAddressDroplet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) throws ServletException, IOException {
		boolean cmo = false;
		boolean kirsch = false;

		BBBOrder order = (BBBOrder) pReq.getObjectParameter(BBBCoreConstants.ORDER);
		 final BBBAddressContainer addressContainer = (BBBAddressContainer) pReq.getObjectParameter(ADDRESS_CONTAINER);
		if (order == null) {
			order = getOrder();
		}
		List<CommerceItem> commerceItems = order.getCommerceItems();
		for (CommerceItem item : commerceItems) {
			if (item instanceof TBSCommerceItem) {
				TBSCommerceItem tbsCommerceItem = (TBSCommerceItem) item;
				if (tbsCommerceItem.isCMO()) {
					cmo = true;
				} else if(tbsCommerceItem.isKirsch()){
					kirsch = true;
				}
			}
		}
		
		if(kirsch){
			pReq.setParameter("kirsch", true);
		}

		if (cmo) {
			pReq.setParameter("cmo", true);
			String storeId = (String) pReq.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
			StoreDetails storeDetails = null;
			String firstName = "";
			String lastName = "";

			BBBAddressVO shippingGroupAddress = null;
			String addressSelected = null;
			List<BBBAddress> shippinggroupAddresses = new ArrayList<BBBAddress>();

			Profile profile = (Profile) pReq.getObjectParameter(BBBCoreConstants.PROFILE);
			if (profile != null) {
				PropertyManager propertyManager = profile.getProfileTools().getPropertyManager();
				firstName = (String) profile.getPropertyValue(propertyManager.getFirstNamePropertyName());
				lastName = (String) profile.getPropertyValue(propertyManager.getLastNamePropertyName());
			}
			boolean isRollBack = false;

			final TransactionManager tManager = getTransactionManager();
			final TransactionDemarcation tDemarcation = new TransactionDemarcation();
			try {
				RepositoryItem storeItem = getSearchStoreManager().getStoreRepository().getItem(storeId, TBSConstants.STORE);
				if (storeItem != null) {
					storeDetails = getSearchStoreManager().convertStoreItemToStore(storeItem, null, null);

					shippingGroupAddress = new BBBAddressVO();
					shippingGroupAddress.setFirstName(firstName);
					shippingGroupAddress.setLastName(lastName);
					shippingGroupAddress.setAddress1(storeDetails.getAddress());
					shippingGroupAddress.setCity(storeDetails.getCity());
					shippingGroupAddress.setState(storeDetails.getState());
					shippingGroupAddress.setCountry(storeDetails.getCountry());
					shippingGroupAddress.setPostalCode(storeDetails.getPostalCode());
					shippingGroupAddress.setSource(storeDetails.getStoreId());

					HardgoodShippingGroup shippingGroup = (HardgoodShippingGroup) pReq.getObjectParameter(SHIPPING_GROUP);
					
					

					if (tManager != null) {
						tDemarcation.begin(tManager, TransactionDemarcation.REQUIRES_NEW);
					}
					synchronized (order) {
						if(shippingGroup != null){
							shippingGroup = orderShippingGroup(order, shippingGroup);
							shippingGroup.setShippingAddress(shippingGroupAddress);

							getOrderManager().updateOrder(order);
						}
					}
					if(shippingGroup != null){
						addressSelected = shippingGroup.getId();
					}
					shippingGroupAddress.setIdentifier(addressSelected);
					shippinggroupAddresses.add(shippingGroupAddress);
					
					addAddressToContainer(shippinggroupAddresses, addressContainer);
					
					pReq.setParameter(GROUP_ADDRESSES, shippinggroupAddresses);
					pReq.setParameter(ADDRESS_CONTAINER, addressContainer);
					pReq.setParameter(SELECTED_ADDRESS, addressSelected);
					pReq.serviceLocalParameter(BBBCoreConstants.OUTPUT, pReq, pRes);
					pReq.serviceLocalParameter(END, pReq, pRes);
				}

			} catch (RepositoryException e) {
				isRollBack = true;
				vlogError("RepositoryException occurred while fetching store details");
				pReq.setParameter("errorMessage", e.getMessage());
				pReq.serviceLocalParameter("error", pReq, pRes);
			} catch (TransactionDemarcationException e) {
				isRollBack = true;
				pReq.setParameter("errorMessage", e.getMessage());
				pReq.serviceLocalParameter("error", pReq, pRes);
			} catch (CommerceException e) {
				isRollBack = true;
				pReq.setParameter("errorMessage", e.getMessage());
				pReq.serviceLocalParameter("error", pReq, pRes);
			} finally {
				try {
					if (tManager != null) {
						tDemarcation.end(isRollBack);
					}
				} catch (TransactionDemarcationException e) {
					logError(e);
				}
			}

		} else {
			super.service(pReq, pRes);
		}
	}


	/**
	 * Gets the search store manager.
	 *
	 * @return the searchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}


	/**
	 * Sets the search store manager.
	 *
	 * @param pSearchStoreManager the searchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}


	/**
	 * Gets the order manager.
	 *
	 * @return the orderManager
	 */
	public OrderManager getOrderManager() {
		return mOrderManager;
	}


	/**
	 * Sets the order manager.
	 *
	 * @param pOrderManager the orderManager to set
	 */
	public void setOrderManager(OrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}


	/**
	 * Gets the transaction manager.
	 *
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}


	/**
	 * Sets the transaction manager.
	 *
	 * @param pTransactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

}
