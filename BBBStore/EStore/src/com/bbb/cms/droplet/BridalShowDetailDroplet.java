package com.bbb.cms.droplet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.cms.manager.BridalShowManager;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 *  This Class is used to populate list of states based on the
 *         Site id and Current Date
 */

public class BridalShowDetailDroplet extends BBBDynamoServlet {

	/**
	 * Refers to the repository BridalShowTemplate
	 */
	private Repository mBridalShowTemplateRepository ;
	
	/**
	 * Refers to the manager BridalShowManager
	 */
	private BridalShowManager mBridalShowManager ;

	private BBBCatalogTools mCatalogTools;

	private String bridalShowViewDirection;



	public String getBridalShowViewDirection() {
		return bridalShowViewDirection;
	}

	public void setBridalShowViewDirection(String bridalShowViewDirection) {
		this.bridalShowViewDirection = bridalShowViewDirection;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}
	/**
	 * @return the BridalShowManager
	 */
	public BridalShowManager getBridalShowManager() {
		return mBridalShowManager;
	}

	/**
	 * @param pBridalShowManager
	 *            the BridalShowManager to set
	 */
	public void setBridalShowManager(BridalShowManager pBridalShowManager) {
		mBridalShowManager = pBridalShowManager;
	}

	/**
	 * Gets the BridalShowTemplate repository
	 */
	public Repository getBridalShowTemplateRepository() {
		return mBridalShowTemplateRepository;
	}

	/**
	 * Sets the BridalShowTemplate repository
	 * 
	 * @param pBridalShowTemplateRepository
	 */
	public void setBridalShowTemplateRepository(Repository pBridalShowTemplateRepository) {

		mBridalShowTemplateRepository = pBridalShowTemplateRepository;
	}

	/**
	 * This method gets the value of state and site from the page and fetches the
	 * bridal show template based on site id, state id and greater than the
	 * current date.
	 */
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		/**
		 * try block to check for repository exception.
		 * 
		 */
		String errMsg=null;
		try {
			/**
			 * siteId from the JSP page.
			 */
			String siteId = request.getParameter(BBBCmsConstants.SITE_ID);
			if (siteId == null) {

				siteId = getCurrentSiteId();
			}

			/**
			 * stateId from the JSP page.
			 */
			final String stateId = request
					.getParameter(BBBCmsConstants.STATE_ID);

			if (StringUtils.isEmpty(siteId) || StringUtils.isEmpty(stateId)) {
				request.serviceParameter(BBBCmsConstants.EMPTY, request,
						response);
				logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId);
				return;
			}

			/**
			 * Repository view of ShowTemplate Item.
			 */
			final RepositoryView repoView = getBridalShowTemplateRepository()
					.getView(BBBCmsConstants.SHOW_TEMPLATE);
			/**
			 * QueryBuilder based on the repository item view show template.
			 */
			final QueryBuilder pQueryBuilder = repoView.getQueryBuilder();
			/**
			 * Property QueryExpression based on the date.
			 */
			final QueryExpression pProperty = pQueryBuilder
					.createPropertyQueryExpression(BBBCmsConstants.TO_DATE);


			final java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

			/**
			 * Constant QueryExpression based on the current date.
			 */
			final QueryExpression pValue = pQueryBuilder
					.createConstantQueryExpression(currentDate);
			/**
			 * Queries to check for site, state and greater than current date.
			 */
			Query[] queries=null;
			logDebug("BridalShowDetailDroplet:selected state id "+stateId);
			//if data is required for all states don't query based on states
			if(stateId!=null && !stateId.equals("ALL")){
				queries= new Query[3];
				queries[0] = getBridalShowManager().multiValueQuery(pQueryBuilder,
						BBBCmsConstants.SITE_ID, siteId);
				queries[1] = pQueryBuilder.createComparisonQuery(pProperty, pValue,
						QueryBuilder.GREATER_THAN_OR_EQUALS);

				queries[2] = getBridalShowManager().multiValueQuery(pQueryBuilder,
						BBBCmsConstants.STATE_ID, stateId);
			}
			else{
				queries= new Query[2];
				queries[0] = getBridalShowManager().multiValueQuery(pQueryBuilder,
						BBBCmsConstants.SITE_ID, siteId);
				queries[1] = pQueryBuilder.createComparisonQuery(pProperty, pValue,
						QueryBuilder.GREATER_THAN_OR_EQUALS);
			}
			final Query querySiteandTime = pQueryBuilder
					.createAndQuery(queries);
			final SortDirectives sortDirectives = new SortDirectives();
            sortDirectives.addDirective(new SortDirective(BBBCmsConstants.FROM_DATE,SortDirective.DIR_ASCENDING));
			logDebug("The query to be executed to get  show data "+querySiteandTime);
			final RepositoryItem[] bridalShowTemplate = repoView
					.executeQuery(querySiteandTime,sortDirectives);
			if (bridalShowTemplate != null && bridalShowTemplate.length > 0) {
				logDebug("no of shows for state "+stateId +" is :"+bridalShowTemplate.length);
				/**
				 * Set parameter of Bridal parameter.
				 */
				LinkedHashSet<Map<String, Object>> bridalDetails = new LinkedHashSet<Map<String, Object>>();

				for (RepositoryItem bridalItem : bridalShowTemplate) { 
					Map<String, Object> bridalDetail = new HashMap<String, Object>();

					if (null != bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE)) {
						bridalDetail.put(BBBCmsConstants.FROM_DATE, 
								new SimpleDateFormat("MMM dd, yyyy").format(bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE)));
					}

					if (null != bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE)) {
						bridalDetail.put(BBBCmsConstants.TO_DATE, 
								new SimpleDateFormat("MMM dd, yyyy").format(bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE)));
					}
					if (null != bridalItem.getPropertyValue(BBBCmsConstants.NAME)) {
						bridalDetail.put(BBBCmsConstants.NAME, bridalItem
								.getPropertyValue(BBBCmsConstants.NAME));
					}

					if (null != bridalItem.getPropertyValue(BBBCmsConstants.TIME)) {
						bridalDetail.put(BBBCmsConstants.TIME, bridalItem.
								getPropertyValue(BBBCmsConstants.TIME));
					}

					if (null != bridalItem.getPropertyValue(BBBCmsConstants.ADDRESS)) {
						bridalDetail.put(BBBCmsConstants.ADDRESS, bridalItem
								.getPropertyValue(BBBCmsConstants.ADDRESS));
					}
					if (null != bridalItem.getPropertyValue(BBBCmsConstants.PHONE)) {
						bridalDetail.put(BBBCmsConstants.PHONE, bridalItem
								.getPropertyValue(BBBCmsConstants.PHONE));
					}


					String bridalShowViewDirection = null;
					try {
						final String viewDirKey=this.getBridalShowViewDirection();
						if(viewDirKey!=null){
							bridalShowViewDirection = getCatalogTools().getContentCatalogConfigration(getBridalShowViewDirection()).get(0);			}		
					} catch (BBBSystemException e) {	
						errMsg="BBBSystemException:catalog_1027";
						logError(LogMessageFormatter.formatMessage(request, "BridalShowDetailDroplet|service|BBBSystemException","catalog_1027"),e);
					} catch (BBBBusinessException e) {	
						errMsg="BBBBusinessException:catalog_1028";
						logError(LogMessageFormatter.formatMessage(request, "BridalShowDetailDroplet|service|BBBBusinessException","catalog_1028"),e);
					}
					if (bridalShowViewDirection != null) {
						bridalDetail.put("bridalShowViewDirection", bridalShowViewDirection);						
					}


					bridalDetails.add(bridalDetail);
				}

				request.setParameter(BBBCmsConstants.STATE_ITEM, bridalDetails);
				request.serviceParameter(BBBCmsConstants.OUTPUT, request,response);
			} else {
				request.serviceParameter(BBBCmsConstants.EMPTY, request,response);
				logDebug("No results applicable for the shows");
			}
		} catch (RepositoryException e) {
			
			logError(LogMessageFormatter.formatMessage(request, "BridalShowDetailDroplet|service|RepositoryException","catalog_1029"),e);
			
				errMsg="RepositoryException:catalog_1029";
			

		}
		request.setParameter("errMsg", errMsg);

	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
	
	/**
	 * get bridal show for the rest API
	 * @param request
	 * @param response
	 * @return bridal show details
	 * @throws BBBSystemException
	 */

	public Set<Map<String, Object>> getBridalShowsAPI(String stateId) throws BBBSystemException
	{
		logDebug("Starting method getBridalShowsAPI for rest module");
		Set<Map<String,Object>> bridalDetail= new HashSet<Map<String,Object>> ();
		try{
			DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		    request.setParameter("stateId", stateId);
		    //
		    service(request, response);

		    bridalDetail = (HashSet<Map<String,Object>>) request.getObjectParameter(BBBCmsConstants.STATE_ITEM);
		    String error=request.getParameter("errMsg");
		    if (null!=error)
		    {
		    	String errorArray=error.substring(0,error.indexOf(":"));
		    	String errorCode=error.substring(error.indexOf(":")+1);
		    	throw new BBBSystemException(errorArray,errorCode);
		    	
		  }
		    logDebug("End method getBridalShowsAPI for rest module");
		}
		catch (IOException e) {
			 throw new BBBSystemException(BBBCatalogErrorCodes.GET_BRIDAL_SHOWS_IO_EXCEPTION, "IO Exception While fetching bridal show detail");

		} catch (ServletException e) {
			 throw new BBBSystemException(BBBCatalogErrorCodes.GET_BRIDAL_SHOWS_SERVLET_EXCEPTION, "Servlet Exception While fetching bridal show detail");
		}
			
		return bridalDetail;
	}

	

}
