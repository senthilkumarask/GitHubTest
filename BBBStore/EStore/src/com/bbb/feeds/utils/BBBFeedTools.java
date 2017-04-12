package com.bbb.feeds.utils;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

import atg.adapter.gsa.LoadingStrategyContext;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RemovedItemException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.email.SMTPEmailSender;
import atg.service.webappregistry.WebApp;
import atg.servlet.ServletUtil;

import com.bbb.cms.tools.CmsTools;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.checkout.util.BBBOrderUtilty;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.config.manager.ConfigTemplateManager;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.feeds.marketing.utils.BBBMarketingFeedTools;
import com.bbb.repositorywrapper.CustomRepositoryException;
import com.bbb.repositorywrapper.ICreateRepositoryItemCallback;
import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryItemWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.DeliveryChargeComparator;

public class BBBFeedTools extends BBBGenericService {

	private Repository catalogRepository;
	private Repository siteRepository;
	private BBBCatalogTools catalogTools;
	private String defaultSiteId;
	private final Map<String,String> freeShipAttrShipGrpMap = new HashMap<String,String> ();
	private IndirectUrlTemplate productTemplate = null;
	private IndirectUrlTemplate categoryTemplate = null;
	private Map<String, String> modeMap = new HashMap<String, String>();
	private ConfigTemplateManager configTemplateManager = null;
	private SiteManager siteManager = null;
	private BBBInventoryManager inventoryManager;
	private PriceListManager priceListManager;
	private boolean sampleFeed = false;
	private int sampleDataSize = 1000;
	private ProductManager productManager = null;
	private SMTPEmailSender emailSender;
	private SiteContextManager siteContextManager = null;
	private	Map<String, RepositoryItem[]> cachedCatalogItemsMap = null;
	private StringBuffer deactiveProducts = null;
	private StringBuffer productsWithOutSkus = null;
	private StringBuffer productsWithDisabledSkus = null;
	private String disabledData;
	private CmsTools mCmsTools;

	public CmsTools getCmsTools() {
		return mCmsTools;
	}

	public void setCmsTools(CmsTools mCmsTools) {
		this.mCmsTools = mCmsTools;
	}

	protected static final ApplicationLogging MLOGGING =
		    ClassLoggingFactory.getFactory().getLoggerForClass(BBBMarketingFeedTools.class);

	public SMTPEmailSender getEmailSender() {
		return this.emailSender;
	}

	public void setEmailSender(final SMTPEmailSender emailSender) {
		this.emailSender = emailSender;
	}

	/**
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param catalogId
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getCatalogItemsForFeedGeneration(
			final boolean isFullDataFeed, final Timestamp lastModDate, final String catalogItemDes)
			throws BBBSystemException {

		return this.getCatalogItemsForFeedGeneration(isFullDataFeed, lastModDate, catalogItemDes, null);
	}

	/**
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param catalogId
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getCatalogItemsForFeedGeneration(
			final boolean isFullDataFeed, final Timestamp lastModDate, final String catalogItemDes, String rql)
			throws BBBSystemException {

		MLOGGING.logDebug("BBBFeedTools [getCatalogItemsForFeedGeneration] start");
		RepositoryItem[] catalogItems = null;

		if(this.getCachedCatalogItemsMap().get(catalogItemDes) != null) {
			catalogItems = this.getCachedCatalogItemsMap().get(catalogItemDes);
			if(this.isSampleFeed()) {
				catalogItems = this.getSampleData(catalogItems);
			}
			return catalogItems;
		}


		final IRepositoryWrapper repWrapper = new RepositoryWrapperImpl(this.getCatalogRepository());
		Object[] params = null;
		if (isFullDataFeed || (lastModDate == null)) {

			MLOGGING.logDebug("Fetch data for full feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"+ lastModDate);
			rql = rql==null?"all":rql;
			params = new Object[]{};
		} else {

			MLOGGING.logDebug("Fetch data for incremental feed lastModifiedDate:" + lastModDate);
			rql = rql == null ? "lastModifiedDate>?0" : rql
					+ " and lastModifiedDate>?0";
			params = new Object[]{lastModDate};
		}
		try {
			if(catalogItemDes.equals("collection")) {
				catalogItems = repWrapper.queryRepositoryItems(BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR, rql, params, true);
			}
			else {
				catalogItems = repWrapper.queryRepositoryItems(catalogItemDes, rql, params, true);
			}
			if(catalogItems!=null) {
				MLOGGING.logDebug(catalogItemDes+" count before filtering " + catalogItems.length);
			}
			this.getCachedCatalogItemsMap().put(catalogItemDes, catalogItems);
			if(this.isSampleFeed()) {
				catalogItems = this.getSampleData(catalogItems);
			}
		}
		catch (final CustomRepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}

		MLOGGING.logDebug("BBBFeedTools [getCatalogItemsForFeedGeneration] end");
		return catalogItems;
	}

	public RepositoryItem[] getBrandItemsForFeedGeneration(final boolean isFullDataFeed, final Timestamp lastModDate, final String siteId) throws BBBSystemException {

		this.logDebug("BBBFeedTools [getBrandItemsForFeedGeneration] start");
		RepositoryItem[] brandItems = null;
		final IRepositoryWrapper repWrapper = new RepositoryWrapperImpl(this.getCatalogRepository());
		Object[] params = null;
		String rql = "sites=?0";
		if (isFullDataFeed || (lastModDate == null)) {
			params = new Object[]{siteId};
			this.logDebug("Fetch data for full feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"+ lastModDate);
		} else {

			this.logDebug("Fetch data for incremental feed lastModifiedDate:" + lastModDate);
			rql = rql+" and lastModifiedDate>?1";
			params = new Object[]{siteId, lastModDate};
		}
		try {
			brandItems = repWrapper.queryRepositoryItems(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR, rql, params, true);
			if(brandItems!=null) {
				this.logDebug(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR+" count before filtering " + brandItems.length);
			}
			if(this.isSampleFeed()) {
				brandItems = this.getSampleData(brandItems);
			}
		} catch (final CustomRepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		this.logDebug("BBBFeedTools [getBrandItemsForFeedGeneration] End");
		return brandItems;
	}

	protected RepositoryItem[] getUserPreferencesForFeedGeneration(final Repository userProfileRepository, final String rqlQuery, final boolean isFullDataFeed, final Timestamp lastModDate) throws BBBSystemException {

		this.logDebug("BBBFeedTools [getUserPreferencesForFeedGeneration] start");
		final IRepositoryWrapper repWrapper = new RepositoryWrapperImpl(userProfileRepository);
		RepositoryItem[] userProfileItems = null;
		Object[] params = null;
		if (isFullDataFeed || (lastModDate == null)) {
			this.logDebug("Fetch data for full feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"+ lastModDate);
		} else {
			this.logDebug("Fetch data for incremental feed lastModifiedDate:" + lastModDate);
			params = new Object[]{lastModDate};
		}
		try {
			userProfileItems = repWrapper.queryRepositoryItems(BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME, rqlQuery, params);
			if(userProfileItems!=null) {
				this.logDebug(BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME+" count before filtering " + userProfileItems.length);
			}
			if(this.isSampleFeed()) {
				userProfileItems = this.getSampleData(userProfileItems);
			}
		} catch (final CustomRepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		this.logDebug("BBBFeedTools [getUserPreferencesForFeedGeneration] end");
		return userProfileItems;
	}


	public RepositoryItem getCatalogBySite(final String siteName) throws BBBSystemException  {
		RepositoryItem[] catalogItems = null;
		MLOGGING.logDebug("BBBFeedTools [getCatalogBySite] site Name: " + siteName);
		try {
			final RepositoryView catalogView = this.getCatalogRepository()
					.getView(BBBCatalogConstants.CATALOG_ITEM_DESCRIPTOR);
			final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
			final Query getAllItemsQuery = queryBuilder
					.createUnconstrainedQuery();
			catalogItems = catalogView.executeQuery(getAllItemsQuery);
			if (catalogItems != null) {
				for (final RepositoryItem catalogItem : catalogItems) {
					final Object obj = catalogItem
							.getPropertyValue(BBBCatalogConstants.SITE_IDS_CATEGORY_PROPERTY_NAME);
					if (obj == null) {
						continue;
					}
					MLOGGING.logDebug("Catalogs found: " + obj);
					if (((Set<String>) obj).contains(siteName)) {
						return catalogItem;
					}
				}
			}

		} catch (final RepositoryException e) {
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}
		MLOGGING.logDebug("BBBFeedTools [getCatalogBySite] End");
		return null;
	}


	/**
	 *
	 * @param viewValue
	 * @return
	 * @throws BBBSystemException
	 */
	  public Timestamp getLastModifiedDate(final String feedType){

		 MLOGGING.logDebug("BBBFeedTools Method : getLastModifiedDate Start");
	    Timestamp modifiedDate = null;
	    final RepositoryItem[] repItems = BBBCoreConstants.getBbbScheduledFeed()
										.queryRepositoryItems(BBBCertonaConstants.FEED,
													"typeOfFeed = ?0 and status=?1 ORDER BY lastModifiedDate SORT DESC", new Object[] {
													feedType, true });
	    if ((repItems != null) && (repItems.length > 0)) {
	    	modifiedDate = (Timestamp) repItems[0].getPropertyValue("lastModifiedDate");
	    }
	    MLOGGING.logDebug("Last Modified Date from repository: " + modifiedDate);
	    MLOGGING.logDebug("BBBFeedTools Method : getLastModifiedDate End");
	    return modifiedDate;
	  }


	public String getCategoryPageURL(final String categoryName, final String categoryId) {
		//categoryName = categoryName.replaceAll(" ", "-");
		return this.formatCatalogItemUrl(categoryId, categoryName, this.getCategoryTemplate());
	}

	public String getProductPageURL(final String productName, final String productId) {
		return this.formatCatalogItemUrl(productId, productName, this.getProductTemplate());
	}

	public String formatCatalogItemUrl(final String repItem, final String displayName, final IndirectUrlTemplate template) {

		String formattedURL = null;
		this.logDebug("BBBFeedTools Method :formatCatalogItemUrl() start:");
		this.logDebug("input parameters :  repositoryId "+repItem+", displayName "+displayName);
		try{
			final WebApp pDefaultWebApp = null;
			final UrlParameter[] pUrlParams = template.cloneUrlParameters();
			pUrlParams[0].setValue(URLEncoder.encode(displayName, "UTF-8").replaceAll("\\+", "%20"));
			pUrlParams[1].setValue(URLEncoder.encode(repItem, "UTF-8").replaceAll("\\+", "%20"));
			formattedURL = template.formatUrl(pUrlParams, pDefaultWebApp);
			this.logDebug("formattedURL generated from IndirectUrlTemplate : "+formattedURL );
		}catch (final ItemLinkException e) {
			this.logError("Exception occourred while creating SEO URL for the product : "+displayName, e);
		}catch (final Exception e) {
			this.logError("Exception occourred while creating SEO URL for the product : "+displayName, e);
		}
		this.logDebug("BBBFeedTools Method :formatCatalogItemUrl() start:");
		return formattedURL;

	}

	public void pushSiteContext(final String siteId) {

		try {

			this.getSiteContextManager().pushSiteContext(this.getSiteContextManager().getSiteContext(siteId));
		} catch (final SiteContextException e) {
			this.logDebug("Exception occured while pushing the siteContest "+ e);
		}
	}

	/**
	   * The method updates the repository with the details of the scheduler
	   * run
	   *
	   * @param schedulerStartDate
	   * @param isFullDataFeed
	   * @param typeOfFeed
	   * @param status
	   */
	  public void updateRepository(final String typeOfFeed, final Timestamp schedulerStartDate, final boolean isFullDataFeed, final boolean status) {

		this.logDebug("BBBFeedTools Method : updateRepository start");
		this.logDebug("scheduler Start Date :" + schedulerStartDate + " full feed?: " + isFullDataFeed + " typeOfFeed: "
		      + typeOfFeed + " status:" + status);
	    final Date schedulerEndDate = new Date();
	    final String mode = this.modeMap.get(String.valueOf(isFullDataFeed));
		final RepositoryItemWrapper feedRepositoryItemWrapper = BBBCoreConstants.getBbbScheduledFeed().createItem(BBBCertonaConstants.FEED,
									new ICreateRepositoryItemCallback() {
									@Override
									public void setCreatedItemProperties(final MutableRepositoryItem createdItem) {
										createdItem.setPropertyValue(BBBCertonaConstants.LAST_MODIFIED_DATE, schedulerStartDate);
										createdItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_COMPLETION_DATE, schedulerEndDate);
										createdItem.setPropertyValue(BBBCertonaConstants.SCHEDULER_START_DATE, schedulerStartDate);
										createdItem.setPropertyValue(BBBCertonaConstants.STATUS, status);
										createdItem.setPropertyValue(BBBCertonaConstants.TYPE_OF_FEED, typeOfFeed);
										createdItem.setPropertyValue(BBBCertonaConstants.MODE, mode);
									}});
		this.sendNotification(typeOfFeed, schedulerStartDate, new Timestamp(schedulerEndDate.getTime()), isFullDataFeed, status);
		this.logDebug("BBBFeedTools Method : updateRepository end");
	  }

	public void sendNotification(final String typeOfFeed, final Timestamp schedulerStartDate, final Timestamp schedulerEndDate, final boolean isFullDataFeed, final boolean status) {

		List<String> feedNotifications = null;
		String[] recipentEmail = null;
		try {
			feedNotifications = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.SCHEDULED_FEEDS_CONFIG_TYPE, typeOfFeed+"Notifications");
		} catch (final BBBSystemException e) {
			this.logError("Error occured while attempting to get notification configuration for "+typeOfFeed+".");
		} catch (final BBBBusinessException e) {

			this.logError("Error occured while attempting to get notification configuration for "+typeOfFeed+".");
		}
		if((feedNotifications == null)
				 || feedNotifications.isEmpty()) {
			return;
		}
		final String emailIds = feedNotifications.get(0);
		if(!StringUtils.isEmpty(emailIds)) {
			try {
				final String subject = typeOfFeed+" feed status on server "+this.getEmailSender().getSourceHostName();
				recipentEmail = emailIds.split(",");
				this.logDebug("sendFailedRecordsReport :Sender :bbbfeeds@bedbath.com recipients: " + Arrays.toString(recipentEmail));
				String message = typeOfFeed+" feed for "+this.getSiteContextManager().getCurrentSiteId()+" site generation completed with following attributes \n";
				message = message+" FullDataFeed="+isFullDataFeed+"\nStart Time = "+schedulerStartDate+"\nEnd Time = "+schedulerEndDate+"\nisFeedSuccessful = "+status;
				this.getEmailSender().sendEmailMessage("bbbfeeds@bedbath.com", recipentEmail, subject, message);
			} catch (final Exception e) {
				this.logError(typeOfFeed+": Error occured while attempting to send email.");
			}
		}
		this.logDebug("BBBFeedTools Method : sendNotification end");

	}

	public RepositoryItem getPriceItem(final RepositoryItem pSite, final String priceListType, final String productId, final String skuId) throws BBBSystemException{

				this.logDebug("BBBFeedTools Method : getPriceItem start");
				final Double listPrice = new Double("0.00");
				final RepositoryItem profile = ServletUtil.getCurrentUserProfile();
				RepositoryItem priceItem = null;
				try {
					RepositoryItem priceList = null;
					try {
						priceList = this.getPriceListManager().getPriceListForSite(pSite, priceListType);
					} catch (final RepositoryException e) {
						this.logError("Price List not configured for site"+ pSite.getRepositoryId());
					}
					if(priceList != null){
						priceItem = this.getPriceListManager().getPrice(priceList, productId, skuId);
					}
				} catch (final PriceListException e) {

					throw new BBBSystemException(BBBCoreErrorConstants.FEED_PRICE_LIST_EXCEPTION,"PriceListException while retrieving List Price "+e);
				}
		        catch (final RemovedItemException removedItemException) {
		            //throw new BBBSystemException("Attempt to use an item which has been removed - productId : " + productId + " skuId : " + skuId );
					this.logError("Attempt to use an item which has been removed - productId : " + productId + " skuId : " + skuId +" Ignore and move on");
		        }

				this.logDebug("BBBFeedTools Method : getPriceItem end");
				return priceItem;
		}


		/**
		 * The method gets the List price of the sku based on site price List
		 */
		public Double getSalePriceBySite(final RepositoryItem pSite, final String productId, final String skuId) throws BBBSystemException{

			this.logDebug("BBBFeedTools Method : getSalePriceBySite called");
			final RepositoryItem priceItem = this.getPriceItem(pSite, "defaultSalePriceList", productId, skuId);
			if(priceItem == null){
				return this.getListPriceBySite(pSite, productId, skuId);
			}
			final double salePrice = (Double)priceItem.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME);
			return Double.compare(salePrice, 0.0)==0?this.getListPriceBySite(pSite, productId, skuId):salePrice;
		}


	  /**
	 * The method gets the List price of the sku based on site price List
	 */
	public Double getListPriceBySite(final RepositoryItem pSite, final String productId, final String skuId) throws BBBSystemException{

		this.logDebug("BBBFeedTools Method : getListPriceBySite called");
		final RepositoryItem priceItem = this.getPriceItem(pSite, "defaultListPriceList", productId, skuId);
		if(priceItem == null){
			return 0.0;
		}
		return (Double)priceItem.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME);
	}

	public RepositoryItemWrapper getProduct(final RepositoryItemWrapper skuWrapper, final String siteName) {

		this.logDebug("BBBFeedTools Method : getProduct start");
		final Set<RepositoryItem> prodSet = skuWrapper.getSet(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME);
		RepositoryItemWrapper productWrapper = null;
		Set<Object> assocSites = null;
		if(prodSet != null) {
			for(final RepositoryItem product: prodSet) {
				productWrapper = new RepositoryItemWrapper(product);
				assocSites = productWrapper.getSet("siteIds");
				if((assocSites == null)
					|| !assocSites.contains(siteName)
					|| !this.getCatalogTools().isProductActive(product)) {
					continue;
				}
				break;
			}
		}
		this.logDebug("BBBFeedTools Method : getProduct end");
		return productWrapper;
	}

	public String getParentCategoryName(final RepositoryItemWrapper productWrapper, final String siteName) {
		this.logDebug("BBBFeedTools Method : getParentCategoryName start");
		final Set<RepositoryItem> catList = productWrapper.getSet(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
		RepositoryItemWrapper categoryWrapper = null;
		Set<Object> assocSites = null;
		String categoryName = "";
		if(catList != null) {
			for(final RepositoryItem category: catList) {
				categoryWrapper = new RepositoryItemWrapper(category);
				assocSites = categoryWrapper.getSet("siteIds");
				if((assocSites == null)
					|| !assocSites.contains(siteName)
					|| !this.getCatalogTools().isCategoryActive(category)) {
					continue;
				}
				categoryName = this.getPlainText(categoryWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
				break;
			}
		}
		this.logDebug("BBBFeedTools Method : getParentCategoryName end");
		return categoryName;
	}
	
	@SuppressWarnings("unchecked")
	public RepositoryItem getParentCateogry (final RepositoryItem catalogItem, final String catalogId) {

		this.logDebug("BBBFeedTools Method : getParentCateogry start");
		RepositoryItem categoryItem = null;
		Object obj= null;
		if(catalogItem == null) {
			return categoryItem;
		}
		try {
			if(catalogItem.getItemDescriptor().getItemDescriptorName().equals(BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR)) {
				//BBBH-1596 Updating omniture feed start
				
				obj= catalogItem.getPropertyValue(BBBCatalogConstants.PRIMARY_PARENT_CATEGORY);
				
				if(obj!=null) {
					categoryItem = getCatalogRepository().getItem((String) obj, BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
					this.logDebug("BBBFeedTools Method : getParentCateogry :: returning primary Category " + obj.toString() +" for product: " + catalogItem.getRepositoryId());
					return categoryItem;
					
				} else {
					this.logDebug("BBBFeedTools Method : getParentCateogry :: returning parent Categories for product: " + catalogItem.getRepositoryId());
					obj= catalogItem.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
				}
				//BBBH-1596 Updating omniture feed end
			}
			else {
				obj= catalogItem.getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME);
			}
		} catch (final RepositoryException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}
		if(obj==null) {
			return categoryItem;
		}
		final Set<RepositoryItem> catSet = (Set<RepositoryItem>) obj;
		Set<RepositoryItem> catalogSet = null;
		for(final RepositoryItem category: catSet) {
			obj = category.getPropertyValue("computedCatalogs");
			if(obj!=null) {
				catalogSet = (Set<RepositoryItem>)obj;
				for(final RepositoryItem catalog: catalogSet) {
					if(catalog.getRepositoryId().equals(catalogId)) {
						categoryItem = category;
						break;
					}
				}
				if(categoryItem != null) {
					break;
				}
			}
		}
		this.logDebug("BBBFeedTools Method : getParentCateogry end");
		return categoryItem;

	}

	public String getProductNavigationPath(final RepositoryItem catalogItem, final String catalogId) {

		this.logDebug("BBBFeedTools Method : getProductNavigationPath start");
		String categoryName = null;
		final RepositoryItem categoryItem = this.getParentCateogry(catalogItem, catalogId);
		if(categoryItem != null) {
			final RepositoryItemWrapper categoryWrapper = new RepositoryItemWrapper(categoryItem);
			categoryName = categoryWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
			final String parentCatName = this.getProductNavigationPath(categoryItem, catalogId);
			if((parentCatName!=null)
					&& !parentCatName.equals("Commerce Root")) {
				categoryName = parentCatName +" > "+ categoryName;
			}
		}
		this.logDebug("BBBFeedTools Method : getProductNavigationPath end");
		return categoryName;
	}

	public String getBreadCrumb(final RepositoryItem productItem, final String siteId) {

		this.logDebug("BBBFeedTools Method : getProductNavigationPath Start");
		String productNavigationPath = "";
		Boolean bts=false;
		try {
			final Map<String, CategoryVO> breadCrumb = this.productManager.getParentCategoryForProduct(productItem.getRepositoryId(),siteId);
			Collection<CategoryVO> category = breadCrumb.values();
			if(category!=null && !category.isEmpty())
			{
				for(final CategoryVO categoryVO:category){
					if((null != categoryVO.getIsCollege()) && categoryVO.getIsCollege()){
						bts=true;
						break;
					}
	
				}
			}
			
			// check if breadcrumb is phantom category then hide it.
			if ((null != breadCrumb) && !breadCrumb.isEmpty()) {
				final Integer breadcrumbKey = breadCrumb.size() - 1;
				final CategoryVO categoryVO = breadCrumb.get(breadcrumbKey.toString());
				if ((null != categoryVO) && (null != categoryVO.getPhantomCategory()) && categoryVO.getPhantomCategory() && !bts ) {
					breadCrumb.remove(breadcrumbKey.toString());
				}
			}
			if ((null != breadCrumb) && !breadCrumb.isEmpty()) {
				category = breadCrumb.values();
				for(final CategoryVO categoryVO:category){
					productNavigationPath = categoryVO.getCategoryName()+" > ";
				}
			}

		} catch (final BBBBusinessException bbbbEx) {
			this.logError("Business Exception from service of BreadcrumbDroplet for productId=" +productItem.getRepositoryId() +" |SiteId="+siteId);
		} catch (final BBBSystemException bbbsEx) {
			this.logError("System Exception from service of BreadcrumbDroplet for productId=" +productItem.getRepositoryId() +" |SiteId="+siteId);
		}
		this.logDebug("BBBFeedTools Method : getProductNavigationPath end");
		return productNavigationPath;
	}

	public String getProductBrandName(final RepositoryItemWrapper productWrapper) {

		this.logDebug("BBBFeedTools Method : getProductBrandName start");
		final RepositoryItem brand = productWrapper.getRepositoryItem(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR);
		String brandName = "";
		if(brand != null) {
			brandName = this.getPlainText((String)brand.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME));
		}
		this.logDebug("BBBFeedTools Method : getProductBrandName end");
		return brandName;
	}

	public Set<RepositoryItem> getParentCategories(final RepositoryItem prodRepoItem) {

		LoadingStrategyContext.pushLoadStrategy("lazy");
		final Object parentCatObj = prodRepoItem.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
		LoadingStrategyContext.popLoadStrategy();
		if(parentCatObj == null) {
			return null;
		}
		return (Set<RepositoryItem>)parentCatObj;
	}

	public List<RepositoryItem> getChildSkus(final RepositoryItem prodRepoItem) {

		LoadingStrategyContext.pushLoadStrategy("lazy");
		final Object skus = prodRepoItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
		LoadingStrategyContext.popLoadStrategy();
		if(skus == null) {
			return null;
		}
		return (List<RepositoryItem>)skus;
	}

	public boolean isSkuWebOnly(final RepositoryItem sku, final String siteId) {


		final Object obj = sku.getPropertyValue("webOnly");
		return obj == null ? false: (Boolean) obj;
	}

	
	

	public boolean isSkuWebOffered(final RepositoryItem sku, final String siteId) {

		final Object translationsObj = sku.getPropertyValue("skuTranslations");
		Date previewDate = new Date();
		Object obj = sku.getPropertyValue("webOfferedDefault");
		boolean webOffered= obj == null ? false: (Boolean) obj;
		obj = sku.getPropertyValue("disableDefault");
		boolean disable=obj == null ? false: (Boolean) obj;

		if(translationsObj != null) {

			final Set<RepositoryItem> translations = (Set<RepositoryItem>) translationsObj;
			for (final RepositoryItem transRepo : translations) {

				final RepositoryItem siteItem = (RepositoryItem) transRepo
						.getPropertyValue(BBBCatalogConstants.SITE_PRODUCT_PROPERTY_NAME);
				if ((siteItem == null)
						|| !siteItem.getRepositoryId().equals(siteId)) {
					continue;
				}
				final Object attrName = transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME_SITE_TRANS_PROPERTY_NAME);
				final Object translationBooleanValue= transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME);

				if((attrName != null) && (translationBooleanValue != null) &&((String)attrName).equalsIgnoreCase(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)) {
					webOffered =(Boolean)translationBooleanValue;
				}
				if((attrName != null) && (translationBooleanValue != null) &&((String) attrName).equalsIgnoreCase(BBBCatalogConstants.DISABLE_PROPERTY_NAME)){
					disable = (Boolean)translationBooleanValue;
				}
			}
		}
		if( this.getCatalogTools().isPreviewEnabled()){
			previewDate =  this.getCatalogTools().getPreviewDate();
		}
		final Date startDate = (Date) sku.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME);
		final Date endDate = (Date) sku.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME);
		this.disabledData = previewDate+"|"+startDate+"|"+endDate+"|"+disable+"|"+webOffered+"|"+siteId+"|"+sku.getRepositoryId();
		if((((endDate!=null) && previewDate.after(endDate))|| ((startDate!=null)&& previewDate.before(startDate))) ||(disable) ||(!webOffered)){
			return false;
		}
		return true;
	}
	/** The method gives minimum delivery charge for the LTL Sku corresponding to site.
	 * @param skuId 
	 * @param siteId
	 * @return deliverySurcharge
	 * @throws BBBSystemException
	 * 
	 *  */
	
	public Double getDeliveryChargeForLTLSku(final String skuId,
			final String siteId) throws BBBSystemException,
			BBBBusinessException {
		List<ShipMethodVO> shipMethodVOList = null;
		double deliverySurcharge = 0.0;
		shipMethodVOList=this.getCatalogTools().getLTLEligibleShippingMethods(skuId, siteId, "en_US");
		ShipMethodVO ltlShipMethodVo=shipMethodVOList.get(0);
		deliverySurcharge=ltlShipMethodVo.getDeliverySurcharge();
		
		
		/*@SuppressWarnings("unused")
		double whiteGloveDeliverySurcharge = 0.00;

		this.logDebug("[START] BBBFeedTools.getLTLEligibleShippingMethods");
		// get all ltl eligible shipping methods for a sku item & site id
		List<ShipMethodVO> ltlShipMehtodsVO = this.getCatalogTools()
				.getShippingMethodsForSku(siteId, skuId);
		
		final Double caseWeight = this.getCatalogTools().getCaseWeightForSku(skuId);
		if (caseWeight != null && ltlShipMehtodsVO != null && !ltlShipMehtodsVO.isEmpty()) {
			// get all delivery surcharge prices for ltl shipping methods for a
			// sku
			final RepositoryItem[] deliverySurchargeItem = this.getCmsTools().getAllSurchargePrice(caseWeight, siteId);
			final boolean isAssemblyFeeOffered = this.getCatalogTools().isAssemblyFeeOffered(skuId);
			boolean isWhiteGlovePresent = false;
			if (deliverySurchargeItem != null
					&& deliverySurchargeItem.length > 0) {
				for (int i = 0; i < deliverySurchargeItem.length; i++) {
					if (deliverySurchargeItem[i] != null) {
						double deliverySurcharges = ((Double) deliverySurchargeItem[i]
								.getPropertyValue(BBBCatalogConstants.DELIVERY_SURCHARGE_PROPERTY_NAME));
						if (i == 0) {
							deliverySurcharge = deliverySurcharges;
						} else {
							if ((deliverySurcharges < deliverySurcharge))

								deliverySurcharge = deliverySurcharges;
						}

					}

				}
			}
			for (final ShipMethodVO shipMethodVO : ltlShipMehtodsVO) {
				
				if (BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD
						.equalsIgnoreCase(shipMethodVO.getShipMethodId())) {
					whiteGloveDeliverySurcharge = shipMethodVO.getDeliverySurcharge();
					isWhiteGlovePresent = true;
					break;
				}

			}
			// calculate assembly fees if white glove is present
			if (isWhiteGlovePresent) {
				if (isAssemblyFeeOffered) {
					double assemblyFees = 0.00;
					assemblyFees = this.getCatalogTools().getAssemblyCharge(siteId, skuId);
					deliverySurcharge += assemblyFees;
				}
			}
		}
		this.logDebug("Minimum delivery Surcharge For Sku Id : "+skuId +" is  :"+deliverySurcharge);
		this.logDebug("[Exit] BBBFeedTools.getLTLEligibleShippingMethods");
*/
		return deliverySurcharge;
	}
	
	
	
	
	
	



	public boolean isProductWebOffered(final RepositoryItem productRepoItem, final String siteId) {

		final Object prdTranslations = productRepoItem.getPropertyValue(BBBCatalogConstants.PROD_TRANS_SITE_TRANS_PROPERTY_NAME);
		Date previewDate = new Date();
		Object obj = productRepoItem.getPropertyValue("webOfferedDefault");
		boolean webOffered= obj == null ? false: (Boolean) obj;
		obj = productRepoItem.getPropertyValue("prodDisableDefault");
		boolean disable=obj == null ? false: (Boolean) obj;

		if(prdTranslations != null) {

			final Set<RepositoryItem> translations = (Set<RepositoryItem>) prdTranslations;
			for (final RepositoryItem transRepo : translations) {

				final RepositoryItem siteItem = (RepositoryItem) transRepo
						.getPropertyValue(BBBCatalogConstants.SITE_PRODUCT_PROPERTY_NAME);
				if ((siteItem == null)
						|| !siteItem.getRepositoryId().equals(siteId)) {
					continue;
				}
				final Object attrName = transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME_SITE_TRANS_PROPERTY_NAME);
				final Object translationBooleanValue= transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_BOOLEAN_SITE_TRANS_PROPERTY_NAME);

				if((attrName != null) && (translationBooleanValue != null) &&((String)attrName).equalsIgnoreCase(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)) {
					webOffered =(Boolean)translationBooleanValue;
				}
				if((attrName != null) && (translationBooleanValue != null) &&((String) attrName).equalsIgnoreCase(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)){
					disable = (Boolean)translationBooleanValue;
				}
			}
		}
		if( this.getCatalogTools().isPreviewEnabled()){
			previewDate =  this.getCatalogTools().getPreviewDate();
		}
		final Date startDate = (Date) productRepoItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
		final Date endDate = (Date) productRepoItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
		this.disabledData = previewDate+"|"+startDate+"|"+endDate+"|"+disable+"|"+webOffered+"|"+siteId+"|"+productRepoItem.getRepositoryId();
		if((((endDate!=null) && previewDate.after(endDate))|| ((startDate!=null)&& previewDate.before(startDate))) ||(disable) ||(!webOffered)){
			return false;
		}
		return true;
	}

	public String getPlainText(final String htmlText) {
		if(StringUtils.isEmpty(htmlText)) {
			return htmlText;
		}
		return Jsoup.parse(htmlText).text();
	}

	public RepositoryItem[] getSampleData(final RepositoryItem[] catalogItems) {

		if(catalogItems == null) {
			return catalogItems;
		}
		List<RepositoryItem> sampleDataList = new ArrayList<RepositoryItem>(Arrays.asList(catalogItems));
		sampleDataList = sampleDataList.subList(0, this.getSampleDataSize()>sampleDataList.size()?sampleDataList.size()-1:this.getSampleDataSize());
		return sampleDataList.toArray(new RepositoryItem[sampleDataList.size()]);
	}

	public void generateTriggerFile(final String filePath, final String fileName, final String fileContent) {

		try {
			BBBUtility.writeFile(filePath+System.getProperty("file.separator")+"go", fileName, fileContent);
		} catch (final BBBSystemException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}
		finally {
			this.clearExceptionData();
		}
	}

	public void generateExceptionData(final String filePath, final String fileName) {
		try {
			BBBUtility.writeFile(filePath, fileName, this.getDeactiveProducts().toString() +"\n"+ this.getProductsWithOutSkus().toString() +"\n"+ this.getProductsWithDisabledSkus().toString());
		} catch (final BBBSystemException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}
	}

	/**
	 * @return the catalogRepository
	 */
	public Repository getCatalogRepository() {
		return this.catalogRepository;
	}

	/**
	 * @param catalogRepository
	 *          the catalogRepository to set
	 */
	public void setCatalogRepository(final Repository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 *          the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the defaultSiteId
	 */
	public String getDefaultSiteId() {
		return this.defaultSiteId;
	}

	/**
	 * @param defaultSiteId
	 *          the defaultSiteId to set
	 */
	public void setDefaultSiteId(final String defaultSiteId) {
		this.defaultSiteId = defaultSiteId;
	}


	public Repository getSiteRepository() {
		return this.siteRepository;
	}

	public void setSiteRepository(final Repository siteRepository) {
		this.siteRepository = siteRepository;
	}


	public IndirectUrlTemplate getProductTemplate() {
		return this.productTemplate;
	}

	public void setProductTemplate(final IndirectUrlTemplate productTemplate) {
		this.productTemplate = productTemplate;
	}

	public IndirectUrlTemplate getCategoryTemplate() {
		return this.categoryTemplate;
	}

	public void setCategoryTemplate(final IndirectUrlTemplate categoryTemplate) {
		this.categoryTemplate = categoryTemplate;
	}
	public Map<String, String> getModeMap() {
		return this.modeMap;
	}

	public void setModeMap(final Map<String, String> modeMap) {
		this.modeMap = modeMap;
	}

	public ConfigTemplateManager getConfigTemplateManager() {
		return this.configTemplateManager;
	}

	public void setConfigTemplateManager(final ConfigTemplateManager configTemplateManager) {
		this.configTemplateManager = configTemplateManager;
	}

	public SiteManager getSiteManager() {
		return this.siteManager;
	}

	public void setSiteManager(final SiteManager siteManager) {
		this.siteManager = siteManager;
	}

	public BBBInventoryManager getInventoryManager() {
		return this.inventoryManager;
	}

	public void setInventoryManager(final BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public PriceListManager getPriceListManager() {
		return this.priceListManager;
	}

	public void setPriceListManager(final PriceListManager priceListManager) {
		this.priceListManager = priceListManager;
	}

	/**
	 * @return the productManager
	 */
	public ProductManager getProductManager() {
		return this.productManager ;
	}

	/**
	 * @param productManager the productManager to set
	 */
	public void setProductManager(final ProductManager productManager) {
		this.productManager = productManager;
	}

	public boolean isSampleFeed() {
		return this.sampleFeed;
	}

	public void setSampleFeed(final boolean sampleFeed) {
		this.sampleFeed = sampleFeed;
	}

	public int getSampleDataSize() {
		return this.sampleDataSize;
	}

	public void setSampleDataSize(final int sampleDataSize) {
		this.sampleDataSize = sampleDataSize;
	}


	public SiteContextManager getSiteContextManager() {
		return this.siteContextManager;
	}

	public void setSiteContextManager(final SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}

	public Map<String, RepositoryItem[]> getCachedCatalogItemsMap() {
		if(this.cachedCatalogItemsMap == null) {
			this.cachedCatalogItemsMap = new HashMap<String, RepositoryItem[]>();
		}
		return this.cachedCatalogItemsMap;
	}

	public void setCachedCatalogItemsMap(
			final Map<String, RepositoryItem[]> cachedCatalogItemsMap) {
		this.cachedCatalogItemsMap = cachedCatalogItemsMap;
	}

	public void clearCachedCatalogItems() {
		this.cachedCatalogItemsMap.clear();
	}

	public StringBuffer getDeactiveProducts() {
		if(this.deactiveProducts==null) {
			this.deactiveProducts = new StringBuffer();
			this.deactiveProducts.append("The following products are disabled for "+this.getSiteContextManager().getCurrentSiteId()+" site\n");
			this.deactiveProducts.append("productid|previewDate|startDate|EndDate|disable|webOffered|siteId|productid\n");
		}
		return this.deactiveProducts;
	}


	public void addDeactiveProducts(final String productId) {
		this.getProductsWithDisabledSkus().append(productId+"|"+this.disabledData+"\n");

	}

	public StringBuffer getProductsWithOutSkus() {
		if(this.productsWithOutSkus==null) {
			this.productsWithOutSkus = new StringBuffer();
			this.productsWithOutSkus.append("The following products are doesn't have any skus\n");

		}
		return this.productsWithOutSkus;
	}

	public StringBuffer getProductsWithDisabledSkus() {
		if(this.productsWithDisabledSkus==null) {
			this.productsWithDisabledSkus = new StringBuffer();
			this.productsWithDisabledSkus.append("The following products have disabled skus for "+this.getSiteContextManager().getCurrentSiteId()+" site\n");
			this.productsWithDisabledSkus.append("productid|skuId|previewDate|startDate|EndDate|disable|webOffered|siteId|skuId\n");
		}
		return this.productsWithDisabledSkus;
	}

	public void addProductsWithDisabledSkus(final String productId, final String skuId) {
		this.getProductsWithDisabledSkus().append(productId+"|"+skuId+"|"+this.disabledData+"\n");
	}

	public void clearExceptionData() {
		this.deactiveProducts = null;
		this.productsWithOutSkus = null;
		this.productsWithDisabledSkus = null;
	}
	
   /**
    * 
    * @param brand
    * @param title
    * @param sku
    * @param size
    * @return
    */
	public String getTitleWithBrandColorSize(String brand,String title,String color,String size){
		
		final StringBuilder titleWithBrandColorSize = new StringBuilder();
		
		 //Create a  new title as combination of brand, title, color and size Example : titleWithBrandColorSize = brand + title + color + size  in order || BBBSL-6884
		
		if (StringUtils.isNotEmpty(brand)) {
			titleWithBrandColorSize.append(brand).append(BBBCoreConstants.SPACE);
		}

		titleWithBrandColorSize.append(title);

		if (StringUtils.isNotEmpty(color)) {
			titleWithBrandColorSize.append(BBBCoreConstants.COMMA)
					.append(BBBCoreConstants.SPACE).append(color);
		}
		
		if (StringUtils.isNotEmpty(size)) {
			titleWithBrandColorSize.append(BBBCoreConstants.SPACE)
					.append(BBBCoreConstants.HYPHEN)
					.append(BBBCoreConstants.SPACE).append(size);
		}
		    
		return titleWithBrandColorSize.toString();
		
	}
}
