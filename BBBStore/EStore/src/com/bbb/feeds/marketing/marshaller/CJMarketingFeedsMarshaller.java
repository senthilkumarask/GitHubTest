package com.bbb.feeds.marketing.marshaller;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.feeds.marketing.vo.MarketingFeedVO;
import com.bbb.feeds.marshaller.IFeedsMarshaller;
import com.bbb.utils.BBBJobContextManager;
import com.bbb.utils.BBBUtility;

public class CJMarketingFeedsMarshaller extends BBBMarketingFeedsMarshaller implements IFeedsMarshaller  {
	
	public static final String COLLECTION_FEED_CONFIG_KEY	= "collection";
	public static final String PRODUCT_FEED_CONFIG_KEY 		= "product";
	public static final String SKU_FEED_CONFIG_KEY 			= "sku";
	
	private Map<String, String> cjFeedStaticTextMap			= null;

	  /**
	   * This method calls the respective marshal methods for category and product.
	   */
		@Override
	   public void marshall(boolean isFullDataFeed, Timestamp schedulerStartDate) {
		   
	    logDebug("BBBMarketingFeedsMarshaller.marshall Method start");
		if (!isFullDataFeed) {
			logDebug("Get last modified date from repository");
			this.lastModDate = getFeedTools().getLastModifiedDate(getTypeOfFeed());
		}
		logDebug("last modified date: " + this.getLastModDate());
		Set<String> siteCodes = siteFeedConfiguration.keySet();
		logDebug("Generating "+ getTypeOfFeed()+" for Site"+ siteCodes);
		Iterator<String> it = siteCodes.iterator();
		List<MarketingFeedVO>  feedVOList = null;
		while(it.hasNext()) {
			boolean status = true;
			String siteConfigurationCode = it.next();
			logDebug(getTypeOfFeed()+" Configuration for "+siteConfigurationCode+" is set to"+ siteFeedConfiguration.get(siteConfigurationCode));		
			if(!Boolean.parseBoolean(siteFeedConfiguration.get(siteConfigurationCode))) {
				continue;
			}
			try {
				//Gets the site name for given site code
				List<String> config = getCatalogTools().getContentCatalogConfigration(siteConfigurationCode);	
				if (config == null 
						|| config.isEmpty()) {
					logError(siteConfigurationCode +" is not configured, so skipping it");
					continue;
				}
				this.siteName = config.get(0);
				Long threadId = Thread.currentThread().getId();
				String jobId = BBBJobContextManager.getInstance().getJobInfo().get(threadId.toString());
				//Setting site name in job context manager 
				if(BBBUtility.isNotEmpty(jobId) && null != BBBJobContextManager.getInstance().getJobContext().get(jobId) ){
				BBBJobContextManager.getInstance().getJobContext().get(jobId).put(BBBCoreConstants.SITE_ID, siteName);
				}
				//Get the catalog by site name
				RepositoryItem catalog = getFeedTools().getCatalogBySite(siteName);
				if(catalog == null) {
					logError("The "+ siteName +" doesn't have any catalogs");
					continue;
				}
				this.catalogId = catalog.getRepositoryId();
				String staticText = getCjFeedStaticTextMap().get(siteConfigurationCode);
				setFeedStaticText(staticText==null?"":staticText);
				logDebug("Generating "+getTypeOfFeed()+" for Site "+ siteName +" and from catalog "+catalog.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
				feedVOList = getMarketingFeedVOList(isFullDataFeed);		
			} catch (BBBBusinessException e) {
				status = false;
				//e.printStackTrace();
				logError(e.getMessage(),e);
			} catch (BBBSystemException e) {
				status = false;			
				//e.printStackTrace();
				logError(e.getMessage(),e);
			}
			finally {
				if(status) {
					status = generateFeed(feedVOList, siteConfigurationCode);
				}
			    getFeedTools().updateRepository(getTypeOfFeed(), schedulerStartDate, isFullDataFeed, status);
			    getFeedTools().getSiteContextManager().clearSiteContextStack();
			}
		}
		getFeedTools().clearCachedCatalogItems();
	   }

	public Map<String, String> getCjFeedStaticTextMap() {
		return cjFeedStaticTextMap;
	}

	public void setCjFeedStaticTextMap(Map<String, String> cjFeedStaticTextMap) {
		this.cjFeedStaticTextMap = cjFeedStaticTextMap;
	}
	

}
