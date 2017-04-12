package com.bbb.certona.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.adapter.gsa.ChangeAwareSet;
import atg.nucleus.Nucleus;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.seo.ItemLink;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.HeadPipelineServlet;

import com.bbb.certona.vo.CertonaGuideVO;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * Guide feed helper class. Fetches data from Guide repository based on
 * date.For full feed all data is fetched.For incremental feed data is fetched
 * based on last modified date
 */
public class CertonaGuideFeedTools extends BBBGenericService {
	private static final String GUIDES = "guides";
	private static final String GUIDE_TEMPLATE = "/com/bbb/cms/repository/GuidesTemplate";
	private Repository guideRepository;
	private Repository catalogRepository;
	private BBBCatalogTools catalogTools;
	// default site as in guide
	private ItemLink guideItemLink;
	public ItemLink getGuideItemLink() {
		return guideItemLink;
	}
	/**
	 * @param guideItemLink
	 *          the guideItemLink to set
	 */
	public void setGuideItemLink(ItemLink guideItemLink) {
		this.guideItemLink = guideItemLink;
	}
	/**
	 * @return the guideRepository
	 */
	public Repository getGuideRepository() {
		return guideRepository;
	}

	/**
	 * @param guideRepository
	 *          the guideRepository to set
	 */
	public void setGuideRepository(final Repository guideRepository) {
		this.guideRepository = guideRepository;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *          the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the catalogRepository
	 */
	public Repository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param catalogRepository
	 *          the catalogRepository to set
	 */
	public void setCatalogRepository(Repository siteRepository) {
		this.catalogRepository = siteRepository;
	}

	/**
	 * This method fetches properties for guides to be sent in guide feed if
	 * full feed is required or if last modified date is null ,full data for
	 * guide is fetched.For incremental feed data that has been modified since
	 * last feed is only fetched
	 * 
	 * @param isFullDataFeed
	 *          :if full feed is required=true else false
	 * @param lastModifiedDate
	 *          :date since last feed was run
	 * @return List of CertonaGuideVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<CertonaGuideVO> getGuideDetails(final boolean isFullDataFeed,final Timestamp lastModifiedDate,final String pRqlQueryRange)
			throws BBBSystemException, BBBBusinessException {
		logDebug("CertonaGuideFeedTools [getGuideDetails]");
		RepositoryItem[] guideItems;
		final List<CertonaGuideVO> guideVOList = new ArrayList<CertonaGuideVO>();
		if (isFullDataFeed || lastModifiedDate == null) {
			guideItems = getRepoForFullFeed(GUIDES);
			logDebug("Fetch data for full guide feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"
					+ lastModifiedDate);
		} else {
			guideItems = getRepoForIncrementalFeed(lastModifiedDate,GUIDES);
			logDebug("Fetch data for incremental guide feed lastModifiedDate:" + lastModifiedDate);
		}

		if (guideItems != null && guideItems.length > 0) {
			for (int i = 0; i < guideItems.length; i++) {
				if (guideItems[i].getPropertyValue(BBBCoreConstants.SITE) != null) {
					ChangeAwareSet siteIds=(ChangeAwareSet)guideItems[i].getPropertyValue(BBBCoreConstants.SITE);
					if(siteIds.size()>0){
						for(int count=0;count<siteIds.size();count++){
							RepositoryItem siteItem =  (RepositoryItem) siteIds.toArray()[count];
							final CertonaGuideVO guideVO = this.populateGuideVO(guideItems[i],siteItem);
							if (guideVO != null) {
								guideVOList.add(guideVO);
							}
						}
					}
				}
				
			}
		} else {
			logDebug("No data available in repository for guide feed");
			throw new BBBBusinessException(BBBCatalogErrorCodes.NO_DATA_FOR_GUIDE_FEED,BBBCatalogErrorCodes.NO_DATA_FOR_GUIDE_FEED);
		}
		logDebug("Exiting getCategoryDetails of CertonaGuideFeedTools");
		return guideVOList;
	}

	/**
	 * the method sets the properties in CertonaGuideVo corresponding to a
	 * particular repository item
	 * 
	 * @param guideRepositoryItem
	 * @return
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	@SuppressWarnings("unchecked")
	private CertonaGuideVO populateGuideVO(final RepositoryItem guideRepoItem,RepositoryItem siteItem) throws BBBSystemException, BBBBusinessException {
		if(guideRepoItem != null) {
			logDebug("CertonaGudieFeedTools [populateGuideVO] Fetch data for guide Id:"
					+ guideRepoItem.getRepositoryId());	
		}
		CertonaGuideVO guideVO = null;
		if (guideRepoItem != null && guideRepoItem.getPropertyValue(BBBCoreConstants.SHOPGUIDEID)!=null) {
			guideVO = setCatBasicproperties(guideRepoItem,siteItem);
			if(guideVO==null){
				return null;
			}
			RepositoryItem[] catalogItems = null;

			try {
				final RepositoryView catalogView = this.getCatalogRepository().getView(BBBCoreConstants.DEF_TYPE_DATA);
				final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
				final QueryExpression pProperty = queryBuilder
						.createPropertyQueryExpression(BBBCoreConstants.SHOPGUIDE);
				final QueryExpression pValue = queryBuilder.createConstantQueryExpression(guideRepoItem.getPropertyValue(BBBCoreConstants.SHOPGUIDEID));
				final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
				logDebug("Query to retrieve data : " + query);
				catalogItems = catalogView.executeQuery(query);
			} catch (RepositoryException e) {
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			}
			if(catalogItems==null || catalogItems.length<=0){
				return null;
			}
			List<String> productids=new ArrayList<String>();
			for(RepositoryItem item:catalogItems){
				productids.add(item.getRepositoryId());
			}
			guideVO.setProductIds(productids);
			DynamoHttpServletRequest request = (DynamoHttpServletRequest) (((HeadPipelineServlet) Nucleus
					.getGlobalNucleus().resolveName(BBBCoreConstants.DYNAMO_HANDLER)).getRequest(null));
			// In schedular there is no request object avaliable that why create a request
			DynamoHttpServletResponse response = request.getResponse();
			ServletUtil.setCurrentRequest(request);
			ServletUtil.setCurrentResponse(response);
			DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
			// adding Site id for the scheduler
			req.setParameter(BBBCoreConstants.PROPERTY_ITEM_DESCRIPTOR,GUIDES);
			req.setParameter(BBBCoreConstants.ID, guideVO.getGuideId());
			req.setParameter(BBBCoreConstants.REPOSITORY_NAME ,GUIDE_TEMPLATE);
			try {
				this.getGuideItemLink().service(request, response);
				List<String> val=this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.GUIDEFEED_SITE_MAP);
				String serverName=request.getServerName();
				if(val.get(0).contains(guideVO.getSiteId())){
					int index=val.get(0).indexOf(guideVO.getSiteId());
					if(val.get(0).indexOf(BBBCoreConstants.COMMA,index)>0){
						serverName=val.get(0).substring(val.get(0).indexOf(BBBCoreConstants.EQUAL,index)+1, val.get(0).indexOf(BBBCoreConstants.COMMA,index));
					}
					else
						serverName=val.get(0).substring(val.get(0).indexOf(BBBCoreConstants.EQUAL,index)+1);
				}
				if(guideVO.getSiteId().contains(BBBCoreConstants.TBS)){
					guideVO.setGuideUrl(request.getScheme() + BBBCoreConstants.CONSTANT_SLASH +serverName+BBBCoreConstants.CONTEXT_TBS+request.getParameter(BBBCoreConstants.URL.toLowerCase()));
				}else{
					guideVO.setGuideUrl(request.getScheme() + BBBCoreConstants.CONSTANT_SLASH +serverName+BBBCoreConstants.CONTEXT_STORE+request.getParameter(BBBCoreConstants.URL.toLowerCase()));
				}
			} catch (ServletException e) {
				logError("CertonaGuideFeedTools [getRepoForFullFeed] view Name: " + e);
			} catch (IOException e) {
				logError("CertonaGuideFeedTools [getRepoForFullFeed] view Name: " + e);
			}
		}

		return guideVO;
	}
	/***
	 * The method sets basic properties of guide for guide feed
	 * @param guideRepoItem
	 * @return CertonaGuideVO
	 */
	private CertonaGuideVO setCatBasicproperties(RepositoryItem guideRepoItem,RepositoryItem siteItem){
		final CertonaGuideVO categoryVO = new CertonaGuideVO();
		categoryVO.setGuideId((String) guideRepoItem.getRepositoryId());
		categoryVO.setSiteId((String)siteItem.getRepositoryId());
		return categoryVO;
	}
	
	/**
	 * The method gets all data from repository for full feed
	 * 
	 * @param viewName
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getRepoForFullFeed(final String viewName) throws BBBSystemException {
		
			logDebug("CertonaGuideFeedTools [getRepoForFullFeed] view Name: " + viewName);
		
		RepositoryItem[] guideItems = null;

		try {
			final RepositoryView guideView = this.getGuideRepository().getView(viewName);
			final QueryBuilder queryBuilder = guideView.getQueryBuilder();
			final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();
			guideItems = guideView.executeQuery(getAllItemsQuery);
		} catch (RepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		return guideItems;
	}

	

	/**
	 * The method gets data from repository for incremental feed based on last
	 * modified date provided
	 * 
	 * @param lastModifiedDate
	 * @param viewName
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getRepoForIncrementalFeed(final Timestamp lastModifiedDate, final String viewName)
			throws BBBSystemException {
		
			logDebug("CertonaGuideFeedTools [getRepoForIncrementalFeed] view Name:" + viewName);
		
		RepositoryItem[] guideItems = null;

		try {

			final RepositoryView guideView = this.getGuideRepository().getView(viewName);
			final QueryBuilder queryBuilder = guideView.getQueryBuilder();
			final QueryExpression pProperty = queryBuilder
					.createPropertyQueryExpression(BBBCertonaConstants.LAST_MODIFIED_DATE_CERTONA_PROPERTY_NAME);
			final QueryExpression pValue = queryBuilder.createConstantQueryExpression(lastModifiedDate);
			final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.GREATER_THAN_OR_EQUALS);
			logDebug("Query to retrieve data : " + query);
			guideItems = guideView.executeQuery(query);
		} catch (RepositoryException e) {
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		return guideItems;
	}

}
