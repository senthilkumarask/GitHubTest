package atg.commerce.util;

import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;


public class BBBContextValueRetriever extends ContextValueRetriever {
	
	private MutableRepository priceListRepository;
	
	private BBBCatalogTools catalogTools;

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


	/**
	 * @return the priceListRepository
	 */
	public MutableRepository getPriceListRepository() {
		return priceListRepository;
	}


	/**
	 * @param priceListRepository the priceListRepository to set
	 */
	public void setPriceListRepository(MutableRepository priceListRepository) {
		this.priceListRepository = priceListRepository;
	}


	@Override
	public RepositoryItem retrieveValue(ContextValueRetrieverArguments pArguments) throws RepositoryException {
        RepositoryItem value = null;
        if (isLoggingDebug()) {
		      logDebug("Arguments: site=" + pArguments.getSite() + ", profile=" + pArguments.getProfile() + ", profilePropertyName=" + pArguments.getProfilePropertyName());
		}
        RepositoryItem profile = pArguments.getProfile();
        if (profile != null) {
		    String profilePropertyName = pArguments.getProfilePropertyName();
		    String selectedCountry= (String) profile.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE);
		    try {
		    	if (BBBInternationalShippingConstants.PROPERTY_PRICELIST.equals(profilePropertyName)) {
		    		String listPriceListId = this.getCatalogTools().getConfigValueByconfigType( 
		    				BBBCoreConstants.CONTENT_CATALOG_KEYS).get( selectedCountry + BBBCoreConstants.UNDERSCORE + 
		    						BBBInternationalShippingConstants.PROPERTY_LIST_PRICELIST);
		    		if (!BBBUtility.isEmpty(listPriceListId)) {
		    			if (isLoggingDebug()) {
		    				logDebug("List Price List id for Country : " + selectedCountry +  " is " + listPriceListId);	
						}
		    			value = getPriceListRepository().getItem(listPriceListId, BBBInternationalShippingConstants.PROPERTY_PRICELIST);
					}
		    		
				} else if (BBBInternationalShippingConstants.PROFILE_SALE_PRICELIST.equals(profilePropertyName)) {
				    String salePriceListId =  this.getCatalogTools().getConfigValueByconfigType(
				    		BBBCoreConstants.CONTENT_CATALOG_KEYS).get( selectedCountry + BBBCoreConstants.UNDERSCORE +
				    				BBBInternationalShippingConstants.PROPERTY_SALE_PRICELIST);
				    if (!BBBUtility.isEmpty(salePriceListId)) {
				    	if (isLoggingDebug()) {
				    		logDebug("Sale Price List id for Country : " + selectedCountry + " is " + salePriceListId);	
						}
				    	value = getPriceListRepository().getItem(salePriceListId, BBBInternationalShippingConstants.PROPERTY_PRICELIST);	
					}
				    
				}
		    } catch (BBBSystemException e) {
		        logError(LogMessageFormatter.formatMessage(null, "BBBSystemException from retrieveValue of BBBContextValueRetriever :"), e);
		        	
			} catch (BBBBusinessException e) {
			    logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException from retrieveValue of BBBContextValueRetriever :"), e);
			    	
			}
	    }
        if (value == null) {
        	if (isLoggingDebug()) {
        		logDebug("Retrieving Price List for default configuration");	
			}
        	return super.retrieveValue(pArguments);
		}
        return value;
        
    }
}
