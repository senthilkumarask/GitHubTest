/**
 * 
 */
package com.bbb.commerce.order.processor;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.constants.BBBCoreConstants;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceIdentifier;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.processor.OrderRepositoryUtils;
import atg.commerce.order.processor.ProcSavePriceInfoObjects;
import atg.commerce.pricing.AmountInfo;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;

/**
 * @author Pradeep Reddy
 *
 */
public class BBBProcSavePriceInfoObjects extends ProcSavePriceInfoObjects {
	
	private String shippingItemsTaxPriceInfosPropertyName;

	protected MutableRepositoryItem savePriceInfo(Order order, CommerceIdentifier ci, MutableRepositoryItem piRepItem, String repItemPropName, AmountInfo pPriceInfo, MutableRepository mutRep, OrderManager orderManager)
	        throws RepositoryException, IntrospectionException, PropertyNotFoundException, CommerceException
	    {
		MutableRepositoryItem tpiRepItem = null;
		tpiRepItem =superSavePriceInfo(order, ci, piRepItem, repItemPropName, pPriceInfo, mutRep, orderManager);
		
		if (pPriceInfo instanceof TaxPriceInfo && tpiRepItem !=null) {
			tpiRepItem = saveItemTaxPriceMap(order.getTaxPriceInfo(), tpiRepItem, mutRep);
		}
		
		return tpiRepItem;
	    }
	
	protected MutableRepositoryItem superSavePriceInfo(Order order, CommerceIdentifier ci, MutableRepositoryItem piRepItem, String repItemPropName, AmountInfo pPriceInfo, MutableRepository mutRep, OrderManager orderManager)
	        throws RepositoryException, IntrospectionException, PropertyNotFoundException, CommerceException
	    {
		return super.savePriceInfo(order, ci, piRepItem, repItemPropName, pPriceInfo, mutRep, orderManager);
		
	    }
	protected MutableRepositoryItem saveItemTaxPriceMap(TaxPriceInfo pPriceInfo, MutableRepositoryItem tpiRepItem, MutableRepository mutRep) throws RepositoryException
	    {
		TaxPriceInfo tpi = null;
		TaxPriceInfo itemTaxPriceInfo = null;
		Map<String, TaxPriceInfo> itemShipTaxMap = null;
		Set<String> commerceItemIds = null;
		MutableRepositoryItem shipTaxPriceInfoRepItem = null;
		MutableRepositoryItem itemTaxPriceInfoRepItem = null;
		
		Map shipTaxMapRepItem = (Map) tpiRepItem.getPropertyValue(getShippingItemsTaxPriceInfosPropertyName());
		Map<String, TaxPriceInfo> shipTaxMap = pPriceInfo.getShippingItemsTaxPriceInfos();
		Set<String> shippingGroupIds = shipTaxMap.keySet();
			for (String shippingId : shippingGroupIds) {
				tpi = shipTaxMap.get(shippingId);
				itemShipTaxMap = tpi.getShippingItemsTaxPriceInfos();
				if (shipTaxMapRepItem != null) {
					shipTaxPriceInfoRepItem = (MutableRepositoryItem) shipTaxMapRepItem.get(shippingId);
					if (shipTaxPriceInfoRepItem != null) {
						Map itemShipTaxMapRepItem = new HashMap();
						commerceItemIds = itemShipTaxMap.keySet();						
						for (String commerceItemId : commerceItemIds) {
							itemTaxPriceInfoRepItem = mutRep.createItem(getTaxPriceInfoDescName());
							itemTaxPriceInfo = itemShipTaxMap.get(commerceItemId);
							itemTaxPriceInfoRepItem = writeTaxPriceInfoProperties(itemTaxPriceInfo, itemTaxPriceInfoRepItem);
							mutRep.addItem(itemTaxPriceInfoRepItem);
							itemShipTaxMapRepItem.put(commerceItemId, itemTaxPriceInfoRepItem);
						}
						 shipTaxPriceInfoRepItem.setPropertyValue(getShippingItemsTaxPriceInfosPropertyName(), itemShipTaxMapRepItem);
					}
				}
			}			
			return tpiRepItem;
	    }

	protected MutableRepositoryItem writeTaxPriceInfoProperties(TaxPriceInfo tpi,
			MutableRepositoryItem tpiRepItem) {
		
		tpiRepItem.setPropertyValue("amount", tpi.getAmount());
		tpiRepItem.setPropertyValue("currencyCode", tpi.getCurrencyCode());
		tpiRepItem.setPropertyValue("discounted", tpi.isDiscounted());
		tpiRepItem.setPropertyValue("amountIsFinal", tpi.isAmountIsFinal());
		tpiRepItem.setPropertyValue("finalReasonCode", tpi.getFinalReasonCode());
		tpiRepItem.setPropertyValue("adjustments", tpi.getAdjustments());
		tpiRepItem.setPropertyValue("cityTax", tpi.getCityTax());
		tpiRepItem.setPropertyValue("districtTax", tpi.getDistrictTax());
		tpiRepItem.setPropertyValue("countyTax", tpi.getCountyTax());
		tpiRepItem.setPropertyValue("stateTax", tpi.getStateTax());
		tpiRepItem.setPropertyValue("countryTax", tpi.getCountryTax());
		tpiRepItem.setPropertyValue("shippingItemsTaxPriceInfos", tpi.getShippingItemsTaxPriceInfos());
		
		return tpiRepItem;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	protected void saveDetailedItemPriceInfos(Order order, AmountInfo pPriceInfo, MutableRepositoryItem mutItem,
			MutableRepository mutRep, OrderManager orderManager) throws RepositoryException, IntrospectionException,
			PropertyNotFoundException, CommerceException {
		List detList = getPropertyValue(pPriceInfo, getCurrentPriceDetailsProperty());
		for (Iterator iter = detList.iterator(); iter.hasNext();) {
			DetailedItemPriceInfo det = (DetailedItemPriceInfo) iter.next();
			List<MutableRepositoryItem> taxItems = getPropertyValue(
					order, det, BBBCoreConstants.DPI_DSLINEITEM_PROPERTY);
			for (MutableRepositoryItem taxItem : taxItems) {
				writeItemToRepository(order, mutRep, taxItem);
			}
		}
		superSaveDetailedItemPriceInfos(order, pPriceInfo, mutItem, mutRep, orderManager);
	}
	@SuppressWarnings({ "rawtypes" })
	public List getPropertyValue(AmountInfo pPriceInfo, String CurrentPriceDetailsProperty) throws PropertyNotFoundException{
		List detList = (List) DynamicBeans.getPropertyValue(pPriceInfo, getCurrentPriceDetailsProperty());
		return detList;
	}
	@SuppressWarnings("unchecked")
	public List<MutableRepositoryItem> getPropertyValue(Order order,DetailedItemPriceInfo det, String CurrentPriceDetailsProperty) throws PropertyNotFoundException{
		List<MutableRepositoryItem> taxItems = (List<MutableRepositoryItem>) OrderRepositoryUtils.getPropertyValue(
				order, det, BBBCoreConstants.DPI_DSLINEITEM_PROPERTY);
		return taxItems;
	}
	protected void superSaveDetailedItemPriceInfos(Order order, AmountInfo pPriceInfo, MutableRepositoryItem mutItem,
			MutableRepository mutRep, OrderManager orderManager)throws RepositoryException, IntrospectionException,
			PropertyNotFoundException, CommerceException {
		super.saveDetailedItemPriceInfos(order, pPriceInfo, mutItem, mutRep, orderManager);
		
	}

	/**
	 * @return the shippingItemsTaxPriceInfosPropertyName
	 */
	public String getShippingItemsTaxPriceInfosPropertyName() {
		return shippingItemsTaxPriceInfosPropertyName;
	}

	/**
	 * @param shippingItemsTaxPriceInfosPropertyName the shippingItemsTaxPriceInfosPropertyName to set
	 */
	public void setShippingItemsTaxPriceInfosPropertyName(
			String shippingItemsTaxPriceInfosPropertyName) {
		this.shippingItemsTaxPriceInfosPropertyName = shippingItemsTaxPriceInfosPropertyName;
	}	
}
