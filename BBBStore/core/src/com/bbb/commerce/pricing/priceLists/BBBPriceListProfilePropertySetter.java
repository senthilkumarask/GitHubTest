package com.bbb.commerce.pricing.priceLists;

import atg.commerce.pricing.priceLists.PriceListProfilePropertySetter;
import atg.multisite.Site;
import atg.repository.RepositoryException;
import atg.userprofiling.Profile;

/**
 * @author magga3
 *
 * This class overrides the existing PriceListProfilePropertySetter method
 * setPriceListProfileProperties()
 */
public class BBBPriceListProfilePropertySetter extends PriceListProfilePropertySetter{

	/** 
	 * This overridden method checks if site is null then return false
	 * else calls its super method.
	 */
	@Override
	protected boolean setPriceListProfileProperties(Profile pProfile, Site pSite)
			throws RepositoryException {
		if(isLoggingDebug()){
			logDebug("BBBPriceListProfilePropertySetter.setPriceListProfileProperties() method started");
		}
		if(pSite == null){
			if(isLoggingDebug()){
				logDebug("Site is null, hence not changing profile properties in setPriceListProfileProperties method. BBBPriceListProfilePropertySetter ends");
			}
			return false;
		}
		if(isLoggingDebug()){
			logDebug("BBBPriceListProfilePropertySetter.setPriceListProfileProperties() method ends");
		}
		return super.setPriceListProfileProperties(pProfile, pSite);
	}
}
