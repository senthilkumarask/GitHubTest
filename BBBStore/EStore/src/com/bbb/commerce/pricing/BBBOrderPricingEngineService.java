/**
 * 
 */
package com.bbb.commerce.pricing;

import java.util.Locale;
import java.util.Map;

import atg.commerce.pricing.OrderPricingEngineImpl;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.constants.BBBCheckoutConstants;

/**
 * @author Pradeep Reddy
 *
 */
public class BBBOrderPricingEngineService extends OrderPricingEngineImpl {
	


	private Repository siteRepository;
		
	@SuppressWarnings("rawtypes")
	protected String getPricingCurrencyCode(RepositoryItem pProfile, String pPriceListPropertyName, boolean pUseDefaultPriceList, Map pExtraParameters, Locale pLocale)
    {		
		String currency = null;
		
		try {
			
			String siteId = extractSiteId();
			if(!StringUtils.isBlank(siteId)) {
				RepositoryItem site = getSiteRepository().getItem(siteId, BBBCheckoutConstants.SITE_CONFIGURATION_ITEM);
				if (site != null) {
					currency = (String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY);
				}
			}
			
			if (StringUtils.isBlank(currency)) {
				currency = BBBCheckoutConstants.SITE_CURRENCY_USD;
			}
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError(e);
			}
			return BBBCheckoutConstants.SITE_CURRENCY_USD;
		}
		return currency;
    }

	protected String extractSiteId() {
		String siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}
	
	/**
	 * @return the siteRepository
	 */
	public Repository getSiteRepository() {
		return siteRepository;
	}

	/**
	 * @param siteRepository the siteRepository to set
	 */
	public void setSiteRepository(Repository siteRepository) {
		this.siteRepository = siteRepository;
	}
	
}
