package com.bbb.feeds.marketing.marshaller;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.feeds.marshaller.IFeedsMarshaller;

public class OmnitureMarketingFeedMarshaller extends BBBMarketingFeedsMarshaller implements IFeedsMarshaller  {
	
	public static final String COLLECTION_FEED_CONFIG_KEY	= "collection";
	public static final String PRODUCT_FEED_CONFIG_KEY 		= "product";
	public static final String SKU_FEED_CONFIG_KEY 			= "sku";
	
	private String 				typeOfFeedPrefix  			= null;
	private Map<String, String> feedTypeConfigMap 			= null;
	private Map<String, String> collectionFeedFileName   	= null;
	private Map<String, String> prodFeedFileName   			= null;
	private Map<String, String> skuFeedFileName   			= null;
	private String collectionMethodName						= null;
	private String productMethodName						= null;
	private String skuMethodName							= null;
	private String collectionFeedFilePath 					= null;
	private String prodFeedFilePath 						= null;
	private String skuFeedFilePath 							= null;
	
	private List<String> collectionFeedHeaders   			= null;
	private List<String> prodFeedHeaders   					= null;
	private List<String> skuFeedHeaders   					= null;
	private String collectionFeedStaticText					= null;
	private String productFeedStaticText					= null;
	private String skuFeedStaticText						= null;

	@Override
	public void marshall(boolean isFullDataFeed, Timestamp schStartDate) {
	    logDebug("OmnitureMarketingFeedMarshaller.marshall Method start");
		Set<String> feedCodes = feedTypeConfigMap.keySet();
				
		Iterator<String> it = feedCodes.iterator();
		while(it.hasNext()) {
			String feedConfigurationCode = it.next();
			setTypeOfFeed(getTypeOfFeedPrefix()+":"+feedConfigurationCode);
			if(!Boolean.parseBoolean(feedTypeConfigMap.get(feedConfigurationCode))) {
				continue;
			}
			
			if(COLLECTION_FEED_CONFIG_KEY.equals(feedConfigurationCode)) {
				preOmniCategoryFeedMarshall();

			}else if(PRODUCT_FEED_CONFIG_KEY.equals(feedConfigurationCode)) {
				preOmniProductFeedMarshall();
				
			}else if(SKU_FEED_CONFIG_KEY.equals(feedConfigurationCode)) {
				preOmniSkuFeedMarshall();
			}
			setFeedGenerationDate();
			super.marshall(isFullDataFeed, schStartDate);
		}
	    logDebug("OmnitureMarketingFeedMarshaller.marshall Method end");		
	}
	
	public void preOmniCategoryFeedMarshall() {
		
		setFeedStaticText(collectionFeedStaticText);
		setMethodName(getCollectionMethodName());
		setFeedFileName(getCollectionFeedFileName());
		setFeedHeaders(getCollectionFeedHeaders());
		setFeedFilePath(getCollectionFeedFilePath());
	}

	public void preOmniProductFeedMarshall() {
		
		setFeedStaticText(productFeedStaticText);
		setMethodName(getProductMethodName());		
		setFeedFileName(getProdFeedFileName());
		setFeedHeaders(getProdFeedHeaders());
		setFeedFilePath(getProdFeedFilePath());		
	}
	
	public void preOmniSkuFeedMarshall() {
		
		setFeedStaticText(skuFeedStaticText);
		setMethodName(getSkuMethodName());		
		setFeedFileName(getSkuFeedFileName());
		setFeedHeaders(getSkuFeedHeaders());
		setFeedFilePath(getSkuFeedFilePath());		
	}
	
	protected void loadCatalogData(boolean isFullDataFeed, Timestamp lastModDate, String siteName) throws BBBSystemException {
		
		try {

			getFeedTools().getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
					BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			getFeedTools().getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
					"collection", "collection=true");
			getFeedTools().getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
					BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
		} catch (BBBSystemException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}

	}

	public String getTypeOfFeedPrefix() {
		return typeOfFeedPrefix;
	}

	public void setTypeOfFeedPrefix(String typeOfFeedPrefix) {
		this.typeOfFeedPrefix = typeOfFeedPrefix;
	}
	
	public Map<String, String> getFeedTypeConfigMap() {
		return feedTypeConfigMap;
	}
	
	public void setFeedTypeConfigMap(Map<String, String> feedTypeConfigMap) {
		this.feedTypeConfigMap = feedTypeConfigMap;
	}

	public Map<String, String> getCollectionFeedFileName() {
		return collectionFeedFileName;
	}

	public void setCollectionFeedFileName(Map<String, String> collectionFeedFileName) {
		this.collectionFeedFileName = collectionFeedFileName;
	}

	public Map<String, String> getProdFeedFileName() {
		return prodFeedFileName;
	}

	public void setProdFeedFileName(Map<String, String> prodFeedFileName) {
		this.prodFeedFileName = prodFeedFileName;
	}

	public Map<String, String> getSkuFeedFileName() {
		return skuFeedFileName;
	}

	public void setSkuFeedFileName(Map<String, String> skuFeedFileName) {
		this.skuFeedFileName = skuFeedFileName;
	}

	public String getCollectionMethodName() {
		return collectionMethodName;
	}

	public void setCollectionMethodName(String collectionMethodName) {
		this.collectionMethodName = collectionMethodName;
	}

	public String getProductMethodName() {
		return productMethodName;
	}

	public void setProductMethodName(String productMethodName) {
		this.productMethodName = productMethodName;
	}

	public String getSkuMethodName() {
		return skuMethodName;
	}

	public void setSkuMethodName(String skuMethodName) {
		this.skuMethodName = skuMethodName;
	}

	public String getCollectionFeedFilePath() {
		return collectionFeedFilePath;
	}

	public void setCollectionFeedFilePath(String collectionFeedFilePath) {
		this.collectionFeedFilePath = collectionFeedFilePath;
	}

	public String getProdFeedFilePath() {
		return prodFeedFilePath;
	}

	public void setProdFeedFilePath(String prodFeedFilePath) {
		this.prodFeedFilePath = prodFeedFilePath;
	}

	public String getSkuFeedFilePath() {
		return skuFeedFilePath;
	}

	public void setSkuFeedFilePath(String skuFeedFilePath) {
		this.skuFeedFilePath = skuFeedFilePath;
	}

	public List<String> getCollectionFeedHeaders() {
		return collectionFeedHeaders;
	}

	public void setCollectionFeedHeaders(List<String> collectionFeedHeaders) {
		this.collectionFeedHeaders = collectionFeedHeaders;
	}

	public List<String> getProdFeedHeaders() {
		return prodFeedHeaders;
	}

	public void setProdFeedHeaders(List<String> prodFeedHeaders) {
		this.prodFeedHeaders = prodFeedHeaders;
	}

	public List<String> getSkuFeedHeaders() {
		return skuFeedHeaders;
	}

	public void setSkuFeedHeaders(List<String> skuFeedHeaders) {
		this.skuFeedHeaders = skuFeedHeaders;
	}

	public String getCollectionFeedStaticText() {
		return collectionFeedStaticText;
	}

	public void setCollectionFeedStaticText(String collectionFeedStaticText) {
		this.collectionFeedStaticText = collectionFeedStaticText;
	}

	public String getProductFeedStaticText() {
		return productFeedStaticText;
	}

	public void setProductFeedStaticText(String productFeedStaticText) {
		this.productFeedStaticText = productFeedStaticText;
	}

	public String getSkuFeedStaticText() {
		return skuFeedStaticText;
	}

	public void setSkuFeedStaticText(String skuFeedStaticText) {
		this.skuFeedStaticText = skuFeedStaticText;
	}
}
