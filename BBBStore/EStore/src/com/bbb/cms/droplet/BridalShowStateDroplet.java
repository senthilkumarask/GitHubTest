package com.bbb.cms.droplet;

import java.util.Arrays;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.manager.BridalShowManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;

/**
 *  This Class is used to populate list of states based on the
 *         Site id and Current Date
 */

public class BridalShowStateDroplet extends BBBDynamoServlet {
	/**
	 * Refers to the manager BridalShowManager
	 */
	private BridalShowManager mBridalShowManager;
	/**
	 * Refers to the repository BridalShowTemplate
	 */
	private Repository mBridalShowTemplateRepository = null;

	/**
	 * Gets the BridalShowTemplate repository
	 */
	public Repository getBridalShowTemplateRepository() {
		return this.mBridalShowTemplateRepository;
	}

	/**
	 * Sets the BridalShowTemplate repository
	 *
	 * @param pBridalShowTemplateRepository
	 */
	public void setBridalShowTemplateRepository(final Repository pBridalShowTemplateRepository) {
		this.mBridalShowTemplateRepository = pBridalShowTemplateRepository;
	}

	/**
	 * Private variable to get and set the value of states
	 */
	private String state;

	/**
	 * Returns state
	 */
	public String getState() {
		return this.state;
	}

	/**
	 *
	 * Sets State
	 */
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * This method gets the value of site id from the page and fetches the states
	 * greater than the current date and site id.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		/**
		 * try block to check for repository exception.
		 */
		try {
			/**
			 * siteId from the JSP page.
			 */
			String siteId = request.getParameter(BBBCmsConstants.SITE_ID);
			if (siteId == null) {

				siteId = getCurrentSiteId();
			}
			if (!StringUtils.isEmpty(siteId)) {

				this.logDebug("BridalShowStateDroplet : siteId= " + siteId);

				/**
				 * Repository view of ShowTemplate Item.
				 */
				final RepositoryView repoView = this.getBridalShowTemplateRepository()
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
				 * Queries to check for site and greater than current date.
				 */
				final Query[] queries = new Query[2];
				queries[0] = this.getBridalShowManager().multiValueQuery(
						pQueryBuilder, BBBCmsConstants.SITE_ID, siteId);

				queries[1] = pQueryBuilder.createComparisonQuery(pProperty,
						pValue, QueryBuilder.GREATER_THAN_OR_EQUALS);

				final Query querySiteandTime = pQueryBuilder
						.createAndQuery(queries);
				final RepositoryItem[] bridalArray = repoView
						.executeQuery(querySiteandTime);
				this.logDebug("Result set of applicable states " + Arrays.toString(bridalArray));
				final SortedMap<String, String> stateMap = new TreeMap<String, String>();

				if ((bridalArray != null) && (bridalArray.length > 0)) {
					this.logDebug(("no of states in which show is present :  "+bridalArray.length));
					for (final RepositoryItem bridalItem : bridalArray) {

						/**
						 * Sorted set of state item.
						 */
						final Set<RepositoryItem> states = (Set<RepositoryItem>) bridalItem
								.getPropertyValue(BBBCmsConstants.STATE_ID);

						for (final RepositoryItem stateItem : states) {
							final String stateId = stateItem.getRepositoryId();
							final String stateName = (String) stateItem
									.getPropertyValue(BBBCmsConstants.DESCRIP);
							stateMap.put(stateId, stateName);
						}
					}
				}

				/**
				 * Set parameter of type state map.
				 */
				if (!stateMap.isEmpty()) {
					request.setParameter(BBBCmsConstants.STATE_MAP, stateMap);
					request.serviceParameter(BBBCmsConstants.OUTPUT, request,
							response);
					this.logDebug("No of results in stateMap= " + stateMap.size());
				} else {
					/**
					 * Set empty state parameter when no records are found.
					 */
					request.serviceParameter(BBBCmsConstants.EMPTY, request,
							response);
					this.logDebug("No results in states Map");
				}
			}
		} catch (final RepositoryException e) {
				this.logDebug("State Repository not found");
		}

	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	/**
	 * @return the BridalShowManager
	 */
	public BridalShowManager getBridalShowManager() {
		return this.mBridalShowManager;
	}

	/**
	 * @param pBridalShowManager
	 *            the BridalShowManager to set
	 */
	public void setBridalShowManager(final BridalShowManager pBridalShowManager) {
		this.mBridalShowManager = pBridalShowManager;
	}

}
