package com.bbb.ecommerce.pricing;

import java.util.List;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.core.util.Address;
import atg.payment.tax.ShippingDestinationImpl;
import atg.payment.tax.TaxRequestInfo;
import atg.payment.tax.TaxRequestInfoImpl;
import atg.payment.tax.TaxStatus;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;

public class TBSCyberSourceTax extends BBBCyberSourceTax {

	private static final String CLASS_NAME = TBSCyberSourceTax.class.getName();
	/**
	 * Property to hold TBSSearchStoreManager reference.
	 */
	private TBSSearchStoreManager mSearchStoreManager;
	
	/**
	 * Property to hold shippingAddressClassName.
	 */
	private String shippingAddressClassName;

	/**
	 * @return the mSearchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}

	/**
	 * @param pSearchStoreManagerp the mSearchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManagerp) {
		mSearchStoreManager = pSearchStoreManagerp;
	}

	public String getShippingAddressClassName() {
		return shippingAddressClassName;
	}

	public void setShippingAddressClassName(String shippingAddressClassName) {
		this.shippingAddressClassName = shippingAddressClassName;
	}

	/**
	 * This method is overridden to calculate the tax for CMO orders.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TaxStatus[] calculateTaxByShipping(TaxRequestInfo ccinfo) {
		final String methodName = CLASS_NAME + ".calculateTaxByShipping()";
		boolean cmoflag = false;
		/* Reset the CYBERSOURCE_TAX_FAILURE Flag */
		Map specialInstructions = ccinfo.getOrder().getSpecialInstructions();
		if (specialInstructions != null) {
			specialInstructions.remove(BBBCheckoutConstants.CYBERSOURCE_TAX_FAILURE);
		}
		Order order = ccinfo.getOrder();
		List<CommerceItem> citems = null;
		if(order != null){
			citems = order.getCommerceItems();
		}
		if(citems != null){
			for (CommerceItem item : citems) {
				if(item instanceof TBSCommerceItem && ((TBSCommerceItem)item).isCMO()){
					cmoflag = true;
					break;
				}
			}
		}
		if(cmoflag && ccinfo.getShippingDestinations() != null){
			RepositoryItem currentStoreItem = null;
			ShippingDestinationImpl shipDesct = (ShippingDestinationImpl) ccinfo.getShippingDestination(0);
			//shipDesct.setShippingAmount(5.99);
			TaxStatus[] taxStatuses = null;
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CYBERSOURCE_TAX_CALL, methodName);
			try {
				BBBRepositoryContactInfo billingInfo = ((BBBOrderImpl)order).getBillingAddress();
				Address billingAddr = null;
				if(billingInfo != null){
					((TaxRequestInfoImpl)ccinfo).setBillingAddress(billingInfo);
				}
				
				taxStatuses = calculateTaxByShippingInternal(ccinfo);
			} finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CYBERSOURCE_TAX_CALL, methodName);
			}
			/*Check & handle tax errors (if any)*/
			taxStatuses = handleTaxError(taxStatuses, ccinfo);
			
			return taxStatuses;
		} else {
			return super.calculateTaxByShipping(ccinfo);
		}
	}
}
