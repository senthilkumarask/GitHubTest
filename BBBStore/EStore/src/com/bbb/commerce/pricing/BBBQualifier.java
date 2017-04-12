package com.bbb.commerce.pricing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.Qualifier;
import atg.commerce.pricing.definition.MatchingObject;
import atg.repository.RepositoryException;

import com.bbb.constants.BBBCheckoutConstants;

public class BBBQualifier extends Qualifier {
	
		
	private static final String CLOSENESS_QUALIFIER = "closenessQualifier";

	/**
	 * This method calls the filters to filter all the items which are excluded from finding out the 
	 * threshold for the applying promotion and sets the amount in the pPricingContext
	 * 
	 * @param pPricingContext
	 * @param pExtraParametersMap
	 * @throws PricingException
	 * 
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public MatchingObject findQualifyingShipping(
			PricingContext pPricingContext, Map pExtraParametersMap)
			throws PricingException {
		
		//Order's threshold amount for applying promotion
		double thresholdAmount = 0;
		//check if threshold amount is set in pExtraParametersMap
		/*Defect BBBH-4623 - Once Shipping global promotion is enabled, explicit shipping promotion is not getting applied*/
		try { 
			if (null != pPricingContext.getPricingModel()
					&& CLOSENESS_QUALIFIER.equalsIgnoreCase(pPricingContext.getPricingModel().getItemDescriptor()
							.getItemDescriptorName())
					&& pExtraParametersMap.get(BBBCheckoutConstants.THRESHOLD_AMOUNT) != null){
				thresholdAmount = (Double) pExtraParametersMap.get(BBBCheckoutConstants.THRESHOLD_AMOUNT);			
			} else {
				Map detailsTaggedToReceiveDiscount = new HashMap();
			    List<FilteredCommerceItem> filteredQualifierItems = new ArrayList<FilteredCommerceItem>();
			    doFilters(1, pPricingContext, pExtraParametersMap, null, detailsTaggedToReceiveDiscount, filteredQualifierItems);
			    
			    
			    if(isLoggingDebug()){
			    	List itemsQualified = pPricingContext.getItems();
			    	logDebug("findQualifyingShipping() method : Items quantity before applying filters - "+itemsQualified.size());
			    	logDebug("findQualifyingShipping() method : thresholdAmount in Order's priceInfo before applying filters   - "+pPricingContext.getOrderPriceQuote().getAmount());
				/*
				 * Commenting out as it also removes the item from the pricing
				 * context. Defect BBBH-4623 - Once Shipping global promotion is
				 * enabled, explicit shipping promotion is not getting applied
				 
			    	itemsQualified.removeAll(filteredQualifierItems);
			    	logDebug("findQualifyingShipping() method : Filtered Items disqualified for applying SG promotion  - "+itemsQualified.size());*/
			    }
			           
			    
			    for (FilteredCommerceItem pFilteredCommerceItem : filteredQualifierItems) {
			    	CommerceItem tempItem = pFilteredCommerceItem.getWrappedItem();
			    	if(tempItem != null && tempItem.getPriceInfo() != null){
			    		thresholdAmount += tempItem.getPriceInfo().getAmount();
			    	}
				}
			}
		} catch (RepositoryException e) {
			vlogError(
					"BBBQualifier.findQualifyingShipping: Repository exception occured while trying to get itemdescriptor name from promotion {0}",
					pPricingContext.getPricingModel());
			throw new PricingException(
					"BBBQualifier.findQualifyingShipping: Repository exception occured while trying to get itemdescriptor name from promotion",
					e);
		}
		/*Defect BBBH-4623 - Once Shipping global promotion is enabled, explicit shipping promotion is not getting applied
		 * }*/
		
		if(isLoggingDebug()){
			logDebug("findQualifyingShipping() method : thresholdAmount to be set in Order's priceInfo  - "+thresholdAmount);			
		}
		
		
		double oldAmount = pPricingContext.getOrderPriceQuote().getAmount();
		pPricingContext.getOrderPriceQuote().setAmount(thresholdAmount);		
		pExtraParametersMap.put(BBBCheckoutConstants.THRESHOLD_AMOUNT, thresholdAmount);
		
		MatchingObject matchObj = extractSuperFindQualifyingShipping(pPricingContext, pExtraParametersMap);
		if(isLoggingInfo()){
			logInfo("findQualifyingShipping() method : MatchingObject object " + (matchObj == null ?matchObj:matchObj.getMatchingObject()));
		}
		pPricingContext.getOrderPriceQuote().setAmount(oldAmount);
		
		return matchObj;
	}

	protected MatchingObject extractSuperFindQualifyingShipping(PricingContext pPricingContext, Map pExtraParametersMap)
			throws PricingException {
		return super.findQualifyingShipping(pPricingContext, pExtraParametersMap);
	}
}
