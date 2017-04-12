/**
 * 
 */
package com.bbb.commerce.catalog;

import com.bbb.common.BBBGenericService;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * @author iteggi
 *
 */
public class SiteProperty extends BBBGenericService {
	
	private MutableRepository catalogRepository;
	private MutableRepository inventoryRepository;
	private MutableRepository seoRepository;
	private String skuIdForTest;
	private String prdIdForTest;
	private String inventoryIdForTest;
	private String seoIdForTest;
	
	/**
	 * @return the seoRepository
	 */
	public MutableRepository getSeoRepository() {
		return seoRepository;
	}

	/**
	 * @param pSeoRepository the seoRepository to set
	 */
	public void setSeoRepository(MutableRepository pSeoRepository) {
		seoRepository = pSeoRepository;
	}

	/**
	 * @return the seoIdForTest
	 */
	public String getSeoIdForTest() {
		return seoIdForTest;
	}

	/**
	 * @param pSeoIdForTest the seoIdForTest to set
	 */
	public void setSeoIdForTest(String pSeoIdForTest) {
		seoIdForTest = pSeoIdForTest;
	}

	/**
	 * @return the prdIdForTest
	 */
	public String getPrdIdForTest() {
		return prdIdForTest;
	}

	/**
	 * @param pPrdIdForTest the prdIdForTest to set
	 */
	public void setPrdIdForTest(String pPrdIdForTest) {
		prdIdForTest = pPrdIdForTest;
	}

	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param pCatalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository pCatalogRepository) {
		this.catalogRepository = pCatalogRepository;
	}
	
	/**
	 * @return the skuIdForTest
	 */
	public String getSkuIdForTest() {
		return skuIdForTest;
	}

	/**
	 * @param pSkuIdForTest the skuIdForTest to set
	 */
	public void setSkuIdForTest(String pSkuIdForTest) {
		skuIdForTest = pSkuIdForTest;
	}
	
	

	public String getInventoryIdForTest() {
		return inventoryIdForTest;
	}

	public void setInventoryIdForTest(String inventoryIdForTest) {
		this.inventoryIdForTest = inventoryIdForTest;
	}

	public MutableRepository getInventoryRepository() {
		return inventoryRepository;
	}

	public void setInventoryRepository(MutableRepository inventoryRepository) {
		this.inventoryRepository = inventoryRepository;
	}

	public void seoPrint(){
		
		String seoId = getSeoIdForTest();
		logDebug("SEO ID : "+seoId);
		try {
			RepositoryItem seoRepositoryItem = seoRepository.getItem(seoId, "SEOTags");
			if(seoRepositoryItem != null){
				
				String titleDefault = (String) seoRepositoryItem.getPropertyValue("titleDefault");
				
				logDebug("Default title : "+titleDefault);
				
				String title = (String) seoRepositoryItem.getPropertyValue("title");
				
				logDebug("derived title : "+title);
				logDebug("---------------------------------------");
				
				
				String descriptionDefault = (String) seoRepositoryItem.getPropertyValue("descriptionDefault");
				
				logDebug("Default description : "+descriptionDefault);
				
				String description = (String) seoRepositoryItem.getPropertyValue("description");
				
				logDebug("derived description : "+description);
				logDebug("---------------------------------------");
				
				
				String keywordsDefault = (String) seoRepositoryItem.getPropertyValue("keywordsDefault");
				
				logDebug("Default keywords : "+keywordsDefault);
				
				String keywords = (String) seoRepositoryItem.getPropertyValue("keywords");
				
				logDebug("derived keywords : "+keywords);
				logDebug("---------------------------------------");
				
				
				String displayNameDefault = (String) seoRepositoryItem.getPropertyValue("displayNameDefault");
				
				logDebug("Default displayName : "+displayNameDefault);
				
				String displayName = (String) seoRepositoryItem.getPropertyValue("displayName");
				
				logDebug("derived displayName : "+displayName);
				logDebug("---------------------------------------");
				
				
			}else {				
				logDebug("SEO Repository Item is null ");
				}
			
		}catch (RepositoryException e) {				
					logError(e);				
				}
			}
	
	public void sitePrint(){
		
		String skuId = getSkuIdForTest();
		String prdId= getPrdIdForTest();
		logDebug("Product ID : "+prdId);
		try {
			RepositoryItem prodRepositoryItem = catalogRepository.getItem(prdId, "product");
			if(prodRepositoryItem != null){
				String displayNameDef = (String) prodRepositoryItem.getPropertyValue("priceRangeDescripDefault");
				
				logDebug("Default priceRangeDescrip : "+displayNameDef);
				
				String displayName = (String) prodRepositoryItem.getPropertyValue("priceRangeDescrip");
				
				logDebug("derived priceRangeDescrip : "+displayName);
				logDebug("---------------------------------------");
				
				Boolean webOnlyDef = (Boolean) prodRepositoryItem.getPropertyValue("webOnlyDefault");
				
				logDebug("Default webOnly : "+webOnlyDef);
				
				Boolean webOnly = (Boolean) prodRepositoryItem.getPropertyValue("webOnly");
				
				logDebug("derived webOnly : "+webOnly);
				
				logDebug("---------------------------------------");
				
				Boolean webOfferedDefault = (Boolean) prodRepositoryItem.getPropertyValue("webOfferedDefault");
				
				logDebug("Default webOffered : "+webOfferedDefault);
				
				Boolean webOffered = (Boolean) prodRepositoryItem.getPropertyValue("webOffered");
				
				logDebug("derived webOffered : "+webOffered);
				
				logDebug("---------------------------------------");
				
				Boolean prodDisableDefault = (Boolean) prodRepositoryItem.getPropertyValue("prodDisableDefault");
				
				logDebug("Default prodDisable : "+prodDisableDefault);
				
				Boolean prodDisable = (Boolean) prodRepositoryItem.getPropertyValue("prodDisable");
				
				logDebug("derived prodDisable : "+prodDisable);
				
				logDebug("---------------------------------------");
				
				String skuLowPriceDefault = (String) prodRepositoryItem.getPropertyValue("skuLowPriceDefault");
				
				logDebug("Default skuLowPrice : "+skuLowPriceDefault);
				
				String skuLowPrice = (String) prodRepositoryItem.getPropertyValue("skuLowPrice");
				
				logDebug("derived skuLowPrice : "+skuLowPrice);
				
				logDebug("---------------------------------------");
				
				String skuHighPriceDefault = (String) prodRepositoryItem.getPropertyValue("skuHighPriceDefault");
				
				logDebug("Default skuHighPrice : "+skuHighPriceDefault);
				
				String skuHighPrice = (String) prodRepositoryItem.getPropertyValue("skuHighPrice");
				
				logDebug("derived skuHighPrice : "+skuHighPrice);
				
				logDebug("---------------------------------------");
				
				
				
				String scene7URL = (String) prodRepositoryItem.getPropertyValue("scene7URL");
				
				logDebug("Default scene7URL : "+scene7URL);
				
				
				String thumbnailImage = (String) prodRepositoryItem.getPropertyValue("thumbnailImage");
				
				logDebug("Derived thumbnailImage : "+thumbnailImage);
				
				
				String smallImage = (String) prodRepositoryItem.getPropertyValue("smallImage");
				
				logDebug("Derived smallImage : "+smallImage);
				
				
				String largeImage = (String) prodRepositoryItem.getPropertyValue("largeImage");
				
				logDebug("Derived largeImage : "+largeImage);
				
				
				String mediumImage = (String) prodRepositoryItem.getPropertyValue("mediumImage");
				
				logDebug("Derived mediumImage : "+mediumImage);
				
				
				String collectionThumbnail = (String) prodRepositoryItem.getPropertyValue("collectionThumbnail");
				
				logDebug("Derived collectionThumbnail : "+collectionThumbnail);
				
								
			} else {
				
				logDebug("Product Repository Item is null ");
				}
			
			
			logDebug("###############################################");
			logDebug("SKU ID : "+skuId);
			
			RepositoryItem skuRepositoryItem = catalogRepository.getItem(skuId, "sku");
			if(skuRepositoryItem != null){
				Double shippingSurchargeDefault = (Double) skuRepositoryItem.getPropertyValue("shippingSurchargeDefault");
				
				logDebug("Default shippingSurcharge : "+shippingSurchargeDefault);
				
				
				
				Double shippingSurcharge = (Double) skuRepositoryItem.getPropertyValue("shippingSurcharge");
				
				logDebug("derived shippingSurcharge : "+shippingSurcharge); 
				
				
				String scene7URL = (String) skuRepositoryItem.getPropertyValue("scene7URL");
				
				logDebug("Default scene7URL : "+scene7URL);
				logDebug("*****************************************************");
				
				
				String thumbnailImage = (String) skuRepositoryItem.getPropertyValue("thumbnailImage");
				
				logDebug("Derived thumbnailImage : "+thumbnailImage);
				
				
				String smallImage = (String) skuRepositoryItem.getPropertyValue("smallImage");
				
				logDebug("Derived smallImage : "+smallImage);
				
				
				String largeImage = (String) skuRepositoryItem.getPropertyValue("largeImage");
				
				logDebug("Derived largeImage : "+largeImage);
				
				
				String mediumImage = (String) skuRepositoryItem.getPropertyValue("mediumImage");
				
				logDebug("Derived mediumImage : "+mediumImage);
				
							
				
			} else {
				
				logDebug("SKU Repository Item is null");
				}
			
			RepositoryItem inventoryRepositoryItem = inventoryRepository.getItem(inventoryIdForTest, "inventory");
			if(inventoryRepositoryItem != null){
				long afs = (Long)inventoryRepositoryItem.getPropertyValue("stockLevel");
				long alt_afs = (Long)inventoryRepositoryItem.getPropertyValue("siteStockLevelDefault");
				long igr = (Long)inventoryRepositoryItem.getPropertyValue("registryStockLevelDefault");
				
				logDebug("AFS : "+afs+" ALT_AFS(Default) : "+alt_afs+" igr(default) : "+igr);
				
				afs = (Long)inventoryRepositoryItem.getPropertyValue("siteStockLevel");
				alt_afs = (Long)inventoryRepositoryItem.getPropertyValue("stockLevel");
				igr = (Long)inventoryRepositoryItem.getPropertyValue("registryStockLevel");
				
				logDebug("AFS : "+afs+" ALT_AFS : "+alt_afs+" igr : "+igr);
				}
			
			
		} catch (RepositoryException e) {
			
			logError(e);
		
		}
	}
}
