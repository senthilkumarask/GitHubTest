package com.bbb.tools;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBDynamicPriceCacheContainer;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO;
import com.bbb.utils.BBBUtility;

/**
 * @author Kumar Magudeeswaran
 * This class is created to refactor the code from BBBCatalogToolsImpl
 * Methods accessing PriceList and dynamicPrice repository are placed in this class from BBBCatalogToolsImpl
 *  
 */
public class BBBPriceListRepositoryTools extends BBBConfigToolsImpl{
	
	/**	Instance for dynamic price Repository. */
	private Repository dynamicPriceRepository;
	/**	Instance for price list Repository. */
	private MutableRepository priceListRepository;
	/**	Instance for price list Manager. */
	private PriceListManager priceListManager;
	/**	Instance for BBBDynamicPriceCacheContainer. */
	private BBBDynamicPriceCacheContainer skuCacheContainer;
	/**	Instance for GlobalRepositoryTools. */
	private GlobalRepositoryTools globalRepositoryTools;
	
	
	/**
	 * This method gets Incart price 
	 * Story - BBBH-2889.
	 *
	 * @param productId the product id
	 * @param skuId the sku id
	 * @return the incart price
	 */
	public Double getIncartPrice(String productId, String skuId) {
		logDebug("BBBCatalogToolsImpl.getIncartPrice - productID: " + productId + " ,sku Id: " + skuId);
        Double inCartPrice = new Double("0.00");
        String inCartPriceListId = null;
        RepositoryItem priceListItem = null;
        RepositoryItem price = null;
        String country = BBBInternationalShippingConstants.DEFAULT_COUNTRY;
		if (getGlobalRepositoryTools().returnCountryFromSession() != null) {
			country = getGlobalRepositoryTools().returnCountryFromSession();
		}
		String siteId=getCurrentSiteId();
        // use CanadaInCartPriceList if the site is BedBathCanada.
        try {
		if (siteId.equals(BBBCoreConstants.SITE_BAB_CA)
				|| siteId.equals(BBBCoreConstants.SITEBAB_CA_TBS)) {
			
				inCartPriceListId = getConfigValueByconfigType(
								BBBCoreConstants.CONTENT_CATALOG_KEYS)
						.get(BBBCoreConstants.SITE_BAB_CA
								+ BBBCatalogConstants.IN_CART_PRICELIST);
			
		}// use MexicoInCartPriceList if the country selected is Mexico.
		else if (country
				.equalsIgnoreCase(BBBCatalogConstants.MEXICO_COUNTRY)) {
			inCartPriceListId =getConfigValueByconfigType(
							BBBCoreConstants.CONTENT_CATALOG_KEYS)
					.get(BBBCatalogConstants.MEXICO_CODE
							+ BBBCatalogConstants.IN_CART_PRICELIST);
		}// use USInCartPriceList if the country selected is other than
		// Mexico.
		else {
			inCartPriceListId = getConfigValueByconfigType(
							BBBCoreConstants.CONTENT_CATALOG_KEYS)
					.get(BBBCoreConstants.SITE_BAB_US
							+ BBBCatalogConstants.IN_CART_PRICELIST);
		}
        } catch (BBBBusinessException e) {
			logError("BBBBusinessException while fetching in cart price",e);
		} catch (BBBSystemException e) {
			logError("BBBSystemException while fetching in InCart Price List from Configure Key",e);
		}
		if (!BBBUtility.isEmpty(inCartPriceListId)) {
			logDebug("BBBCatalogToolsImpl.getIncartPrice - Price List id is: " + inCartPriceListId);
			try {
				priceListItem = getPriceListRepository()
						.getItem(
								inCartPriceListId,
								BBBInternationalShippingConstants.PROPERTY_PRICELIST);
			
			if (null != priceListItem) {
				price = this.getPriceListManager().getPrice(
						(RepositoryItem) priceListItem,
						productId,
						skuId);
				if (price != null) {
					inCartPrice = (Double) price
							.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME);
					}
				}
			} catch (RepositoryException e) {
				logError(
						"Repository Exception while fetching in cart price",
						e);
			} catch (PriceListException e) {
				logError(
						"Repository Exception while fetching in cart price",
						e);
			}
			}
		logDebug("BBBCatalogToolsImpl.getIncartPrice - In Cart Price of SKU : " + skuId + " is : " + inCartPrice);
        return inCartPrice;
    }


	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
	
	
	/**
	 * returns dynamic price product item.
	 *
	 * @param productId the product id
	 * @return product item, if is dynamic price product
	 */
public RepositoryItem getDynamicPriceProductItem(String productId) {

	logDebug("Returning Dynamic Price Repository Item for product id:" + productId);
	RepositoryItem prodItem = null;
	try {
		prodItem = getDynamicPriceRepository().getItem(productId,
				BBBCatalogConstants.PRODUCT_PRICE_ITEM);
		logDebug(productId + "product is in Dynamic Price Repository ");
	} catch (RepositoryException e) {
		logError(" Error while fetching product from Dynamic Price repository: " + e);
	}
	return prodItem;
}

/**
 * This method gets Incart flag for the sku 
 * Story - BBBH-2890.
 *
 * @param skuId the sku id
 * @return the sku incart flag
 */

public boolean getSkuIncartFlag(String skuId, boolean fetchFromDB) {
	logDebug("BBBCatalogToolsImpl.getSkuIncartFlag - sku Id: " + skuId);
	RepositoryItem dynamicSkuItem=null;
	boolean skuInCartFlag = false;
	
	boolean enableDynamicPricing = false;
	String enableDynamicPricingStr = getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false");
    		
    if(enableDynamicPricingStr!=null){
    	enableDynamicPricing= Boolean.parseBoolean(enableDynamicPricingStr);
    }
    
    if(!enableDynamicPricing){
    	logDebug("BBBCatalogToolsImpl.getSkuIncartFlag - dynamic pricing featue is disabled");	
    	return skuInCartFlag;
    }
	
	BBBDynamicPriceSkuVO skuDynamicPriceVo = null;
	try {
		if(null != getSkuCacheContainer()){
			logDebug("Fetching dynamic sku data from cache in getSkuIncartFlag(): " + skuId);
			skuDynamicPriceVo = (BBBDynamicPriceSkuVO) getSkuCacheContainer().get("sku_" + skuId);
			logDebug("Feched skuDynamicPriceVo ="+skuDynamicPriceVo);
		}
		
		if( null == skuDynamicPriceVo && fetchFromDB){
			
			logDebug("Fetching dynamic sku data from db/repository in getSkuIncartFlag(): " + skuId);
			 dynamicSkuItem = getDynamicPriceRepository().getItem(skuId, BBBCatalogConstants.SKU_PRICE_ITEM);	
			 if(null != dynamicSkuItem){
				
			logDebug("Fetched dynamic sku data from cache in getSkuIncartFlag(): " + skuId);
				skuDynamicPriceVo = populateSkuDynamicPriceVO(dynamicSkuItem);
			 }
		}
	}
	catch (RepositoryException e) {
		logError("Error while fetching SKU item from Dynamic Repository for SKU id: "+skuId,e);
	}
	
	if(skuDynamicPriceVo!=null){
		
		String siteId = getCurrentSiteId();
		String country = BBBInternationalShippingConstants.DEFAULT_COUNTRY;
	
		if (getGlobalRepositoryTools().returnCountryFromSession() != null) {
			country = getGlobalRepositoryTools().returnCountryFromSession();
		}
			
		if (siteId.contains(BBBCoreConstants.SITE_BAB_CA)) {
			skuInCartFlag =skuDynamicPriceVo.isCaIncartFlag();
		}else if(BBBInternationalShippingConstants.MEXICO_COUNTRY.equalsIgnoreCase(country)){
			skuInCartFlag =skuDynamicPriceVo.isMxIncartFlag();
		}else if (siteId.contains(BBBCoreConstants.SITE_BBB)) {				
			skuInCartFlag = skuDynamicPriceVo.isBabyIncartFlag();
		}
		else if(siteId.contains(BBBCoreConstants.SITE_BAB_US)){
			skuInCartFlag = skuDynamicPriceVo.isBbbIncartFlag();				
		}
		
		logDebug("BBBCatalogToolsImpl.getSkuIncartFlag - In Cart Flag : "+	skuInCartFlag +"for Site Id :" + siteId);
	}
	return skuInCartFlag;
}


/**
 * @param dynamicSkuItem
 * @return
 */
private BBBDynamicPriceSkuVO populateSkuDynamicPriceVO(RepositoryItem dynamicSkuItem) {
	
		BBBDynamicPriceSkuVO skuDynamicPriceVo = null;
		skuDynamicPriceVo = new BBBDynamicPriceSkuVO();
		skuDynamicPriceVo.setBabyIncartFlag((boolean)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU));
		skuDynamicPriceVo.setBabyPricingLabelCode((String)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU));
		skuDynamicPriceVo.setBbbIncartFlag((boolean)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU));
		skuDynamicPriceVo.setBbbPricingLabelCode((String)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU));
		skuDynamicPriceVo.setCaIncartFlag((boolean)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU));
		skuDynamicPriceVo.setCaPricingLabelCode((String)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU));
		skuDynamicPriceVo.setMxIncartFlag((boolean)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU));
		skuDynamicPriceVo.setMxPricingLabelCode((String)dynamicSkuItem.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU));
		return skuDynamicPriceVo;
}
	
	
/**
 * Populate dynamic sku deatil in vo.
 *
 * @param sKUDetailVO the s ku detail vo
 * @param country the country
 * @param siteId the site id
 */
public void populateDynamicSKUDeatilInVO(SKUDetailVO sKUDetailVO,
		String country, String siteId, boolean fromCart) {
	
	logDebug("Inside populateDynamicSKUDeatilInVO ");

	RepositoryItem dynamicSkuItem = null;
	BBBDynamicPriceSkuVO  skuDynamicPriceVo = null;
	try {
		if(null != getSkuCacheContainer() && null != getSkuCacheContainer().get("sku_" + sKUDetailVO.getSkuId())){
			logDebug("Fetching sku data from cache in populateDynamicSKUDeatilInVO(): " + sKUDetailVO.getSkuId());
			skuDynamicPriceVo = (BBBDynamicPriceSkuVO) getSkuCacheContainer().get("sku_" + sKUDetailVO.getSkuId());
		}
		else{
			//adding fromCart check to make DB call only while adding to cart.
			if(enableRepoCallforDynPrice() && fromCart){
				this.vlogDebug("Making Dynamic repository call to check if incart eligible sku fromCart:{0} & skuId:{1}",fromCart,sKUDetailVO.getSkuId());
				dynamicSkuItem = getDynamicPriceRepository().getItem(sKUDetailVO.getSkuId(), BBBCatalogConstants.SKU_PRICE_ITEM);	
				if(null != dynamicSkuItem){
					skuDynamicPriceVo = populateSkuDynamicPriceVO(dynamicSkuItem);
				 }
			}
		}

		if (skuDynamicPriceVo != null&& (country.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO) || country.equals(BBBInternationalShippingConstants.DEFAULT_COUNTRY))) {
			updateSkuPriceFlags(sKUDetailVO, skuDynamicPriceVo, country,siteId);
		}

		// If condition for international user in cart flag for Baby and US
		// site
		else {
			if (skuDynamicPriceVo != null) {
				if (siteId.equals(BBBCoreConstants.SITE_BBB)
						|| siteId.equals(BBBCoreConstants.SITEBAB_BABY_TBS)) {
					sKUDetailVO.setInCartFlag(skuDynamicPriceVo.isBabyIncartFlag());
				} else if (siteId.equals(BBBCoreConstants.SITE_BAB_US)
						|| siteId.equals(BBBCoreConstants.TBS_BEDBATH_US)) {
					sKUDetailVO.setInCartFlag(skuDynamicPriceVo.isBbbIncartFlag());

				}

				logDebug("In Cart Flag : " + sKUDetailVO.isInCartFlag()
						+ "for Site Id :" + siteId);
			}

		}

	} catch (RepositoryException e) {
		logError(
				"Error while fetching SKU item from Dynamic Repository for SKU id: "
						+ sKUDetailVO.getSkuId(), e);

	}
}


/**
 * method to update dynamic price string for sku.
 *
 * @param skuDetailVO the sku detail vo
 * @param skuDynamicPriceVo the sku dynamic price vo
 * @param country the country
 * @param siteId the site id
 */
private void updateSkuPriceFlags(SKUDetailVO skuDetailVO, BBBDynamicPriceSkuVO skuDynamicPriceVo, String country, String siteId) {
	
		logDebug("Entering updatePriceStringSKU() method for sku id: "+skuDetailVO.getSkuId() +"to populate price label code and in cart flag");
		logDebug("updatePriceStringSKU() method for country: "+country +"siteId :"+siteId);
		
			String pricingLabelCode = null;
			boolean inCartFlag = false;
						 
			if (siteId.equals(BBBCoreConstants.SITE_BAB_CA)
					|| siteId.equals(BBBCoreConstants.SITEBAB_CA_TBS)) {
				if(skuDynamicPriceVo
						.getCaPricingLabelCode()!=null){
				pricingLabelCode = skuDynamicPriceVo.getCaPricingLabelCode();
				}
				inCartFlag=skuDynamicPriceVo.isCaIncartFlag();
			} else if(country.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO)){
				if(skuDynamicPriceVo.getMxPricingLabelCode()!=null){
					pricingLabelCode=skuDynamicPriceVo.getMxPricingLabelCode();
					}
					inCartFlag=skuDynamicPriceVo.isMxIncartFlag();
			}
			else if (siteId.equals(BBBCoreConstants.SITE_BBB)||siteId.equals(BBBCoreConstants.SITEBAB_BABY_TBS)) {
				if(skuDynamicPriceVo.getBabyPricingLabelCode()!=null){
					pricingLabelCode = skuDynamicPriceVo.getBabyPricingLabelCode();
					}
					inCartFlag=skuDynamicPriceVo.isBabyIncartFlag();
			}else {
				if(skuDynamicPriceVo.getBbbPricingLabelCode()!=null){
					pricingLabelCode = skuDynamicPriceVo.getBbbPricingLabelCode();
					}
					inCartFlag=skuDynamicPriceVo.isBbbIncartFlag();
			}

			skuDetailVO.setPricingLabelCode(pricingLabelCode);
			skuDetailVO.setInCartFlag(inCartFlag);
			skuDetailVO.setDynamicPriceSKU(true);
			logDebug("Pricing Label Code(SKU): "+pricingLabelCode + "In Cart Flag : "+inCartFlag);
			logDebug("Exitting updatePriceStringSKU() method");
	

}

/**
 * @return the skuCacheContainer
 */
public BBBDynamicPriceCacheContainer getSkuCacheContainer() {
	return skuCacheContainer;
}
/**
 * @param skuCacheContainer the skuCacheContainer to set
 */
public void setSkuCacheContainer(BBBDynamicPriceCacheContainer skuCacheContainer) {
	this.skuCacheContainer = skuCacheContainer;
}
/**
 * @return the priceListManager
 */
public PriceListManager getPriceListManager() {
	return priceListManager;
}
/**
 * @param priceListManager the priceListManager to set
 */
public void setPriceListManager(PriceListManager priceListManager) {
	this.priceListManager = priceListManager;
}
/**
 * @return the dynamicPriceRepository
 */
public Repository getDynamicPriceRepository() {
	return dynamicPriceRepository;
}
/**
 * @param dynamicPriceRepository the dynamicPriceRepository to set
 */
public void setDynamicPriceRepository(Repository dynamicPriceRepository) {
	this.dynamicPriceRepository = dynamicPriceRepository;
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

/**
 * @return the globalRepositoryTools
 */
public GlobalRepositoryTools getGlobalRepositoryTools() {
	return globalRepositoryTools;
}

/**
 * @param globalRepositoryTools the globalRepositoryTools to set
 */
public void setGlobalRepositoryTools(GlobalRepositoryTools globalRepositoryTools) {
	this.globalRepositoryTools = globalRepositoryTools;
}


	
}
