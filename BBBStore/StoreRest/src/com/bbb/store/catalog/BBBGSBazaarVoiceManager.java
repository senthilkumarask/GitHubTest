package com.bbb.store.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBStoreRestConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.store.catalog.bvreviews.Reviews;
import com.bbb.utils.BBBUtility;

/**
 * Class created to get the Bazaar voice results from the Bazaar voice api call.
 * 
 *
 */
public class BBBGSBazaarVoiceManager extends BBBGenericService {

	/**
	 * Config key to get the passKey
	 */
	private String apiPassKey;
	/**
	 * Config key to get the apiVersion
	 */
	private String apiVersionKey;
	private String filterQuery;
	private String filterQueryValue;
	private BBBCatalogTools catalogTools;
	/**
	 * Config key for the Bazaar voice url
	 */
	private String apiKey;
	/**
	 * Include Key parameter
	 */
	private String includeQuery;
	private String includeQueryValue;
	/**
	 * Stats query parameter
	 */
	private String statsQuery;
	private String statsQueryValue;
	/**
	 * Sort query parameter
	 */
	private String sortQuery;
	private String sortQueryValue;
	/**
	 * Limit query parameter
	 */
	private String limitQuery;
	/**
	 * Config key for the Limit
	 */
	private String limitQueryKey;
	private HTTPCallInvoker httpCallInvoker;
	/**
	 * Pass key parameter
	 */
	private String apiPassQuery;
	/**
	 * Api version parameter
	 */
	private String apiVersionQuery;
	
	/**
	 * @return the limitQueryKey
	 */
	public String getLimitQueryKey() {
		return limitQueryKey;
	}

	/**
	 * @param limitQueryKey the limitQueryKey to set
	 */
	public void setLimitQueryKey(String limitQueryKey) {
		this.limitQueryKey = limitQueryKey;
	}

	

	/**
	 * @return the apiPassQuery
	 */
	public String getApiPassQuery() {
		return apiPassQuery;
	}

	/**
	 * @param apiPassQuery
	 *            the apiPassQuery to set
	 */
	public void setApiPassQuery(String apiPassQuery) {
		this.apiPassQuery = apiPassQuery;
	}

	/**
	 * @return the apiVersionQuery
	 */
	public String getApiVersionQuery() {
		return apiVersionQuery;
	}

	/**
	 * @param apiVersionQuery
	 *            the apiVersionQuery to set
	 */
	public void setApiVersionQuery(String apiVersionQuery) {
		this.apiVersionQuery = apiVersionQuery;
	}

	/**
	 * @return the httpCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}

	/**
	 * @param httpCallInvoker
	 *            the httpCallInvoker to set
	 */
	public void setHttpCallInvoker(HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}

	/**
	 * @return the limitQuery
	 */
	public String getLimitQuery() {
		return limitQuery;
	}

	/**
	 * @param limitQuery
	 *            the limitQuery to set
	 */
	public void setLimitQuery(String limitQuery) {
		this.limitQuery = limitQuery;
	}

	/**
	 * @return the includeQuery
	 */
	public String getIncludeQuery() {
		return includeQuery;
	}

	/**
	 * @param includeQuery
	 *            the includeQuery to set
	 */
	public void setIncludeQuery(String includeQuery) {
		this.includeQuery = includeQuery;
	}

	/**
	 * @return the statsQuery
	 */
	public String getStatsQuery() {
		return statsQuery;
	}

	/**
	 * @param statsQuery
	 *            the statsQuery to set
	 */
	public void setStatsQuery(String statsQuery) {
		this.statsQuery = statsQuery;
	}

	/**
	 * @return the sortQuery
	 */
	public String getSortQuery() {
		return sortQuery;
	}

	/**
	 * @param sortQuery
	 *            the sortQuery to set
	 */
	public void setSortQuery(String sortQuery) {
		this.sortQuery = sortQuery;
	}

	/**
	 * @return the apiVersionKey
	 */
	public String getApiVersionKey() {
		return apiVersionKey;
	}

	/**
	 * @param apiVersionKey
	 *            the apiVersionKey to set
	 */
	public void setApiVersionKey(String apiVersionKey) {
		this.apiVersionKey = apiVersionKey;
	}

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @return the filterQuery
	 */
	public String getFilterQuery() {
		return filterQuery;
	}

	/**
	 * @param filterQuery
	 *            the filterQuery to set
	 */
	public void setFilterQuery(String filterQuery) {
		this.filterQuery = filterQuery;
	}

	/**
	 * @return the filterQueryValue
	 */
	public String getFilterQueryValue() {
		return filterQueryValue;
	}

	/**
	 * @param filterQueryValue
	 *            the filterQueryValue to set
	 */
	public void setFilterQueryValue(String filterQueryValue) {
		this.filterQueryValue = filterQueryValue;
	}

	/**
	 * @return the includeQueryValue
	 */
	public String getIncludeQueryValue() {
		return includeQueryValue;
	}

	/**
	 * @param includeQueryValue
	 *            the includeQueryValue to set
	 */
	public void setIncludeQueryValue(String includeQueryValue) {
		this.includeQueryValue = includeQueryValue;
	}

	/**
	 * @return the statsQueryValue
	 */
	public String getStatsQueryValue() {
		return statsQueryValue;
	}

	/**
	 * @param statsQueryValue
	 *            the statsQueryValue to set
	 */
	public void setStatsQueryValue(String statsQueryValue) {
		this.statsQueryValue = statsQueryValue;
	}

	/**
	 * @return the sortQueryValue
	 */
	public String getSortQueryValue() {
		return sortQueryValue;
	}

	/**
	 * @param sortQueryValue
	 *            the sortQueryValue to set
	 */
	public void setSortQueryValue(String sortQueryValue) {
		this.sortQueryValue = sortQueryValue;
	}

	/**
	 * @param apiKey
	 *            the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/**
	 * @return the apiPassKey
	 */
	public String getApiPassKey() {
		return apiPassKey;
	}

	/**
	 * @param apiPassKey
	 *            the apiPassKey to set
	 */
	public void setApiPassKey(String apiPassKey) {
		this.apiPassKey = apiPassKey;
	}

	/**
	 * Method for returning the BV reviews related to the product Id passed as an
	 * input parameter
	 * @param productId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Reviews getProductReviews(final String productId, final String sortingOrder)
			throws BBBSystemException, BBBBusinessException {
		
		logDebug("BBBGSBazaarVoiceReview.getProductReviews() method starts");

		BBBPerformanceMonitor.start(BBBPerformanceConstants.BAZAAR_VOICE_API_CALL
				+ " getProductReviews");
		
		Reviews reviewsVO = null;
		List<String> requiredParams = new ArrayList<String>();
		List<String> optionalParams = new ArrayList<String>();
		Map<String,String> paramsValuesMap = new HashMap<String, String>();
		
		String bazaarVoiceUrl = null;
		String passKey = null;
		String apiVersion = null;
		String limit = null;
		String sortingOrderValue = null;
		logDebug("BBBGSBazaarVoiceReview.getProductReviews(): Product Id: "
				+ productId);
		
		try {

			
			bazaarVoiceUrl = getCatalogTools().getAllValuesForKey(
					BBBStoreRestConstants.THIRD_PARTY_URLS, getApiKey()).get(0);
			passKey = getCatalogTools().getAllValuesForKey(
							BBBStoreRestConstants.THIRD_PARTY_URLS,
							getApiPassKey()).get(0);
			apiVersion = getCatalogTools().getAllValuesForKey(
					BBBStoreRestConstants.THIRD_PARTY_URLS,
					getApiVersionKey()).get(0);
			limit = getCatalogTools().getAllValuesForKey(
					BBBStoreRestConstants.THIRD_PARTY_URLS,
					getLimitQueryKey()).get(0);
			
			logDebug("BBBGSBazaarVoiceReview.getProductReviews(): Bazaar voice url: "
					+ bazaarVoiceUrl);
			logDebug("BBBGSBazaarVoiceReview.getProductReviews(): Pass Key: "
					+ passKey);
			logDebug("BBBGSBazaarVoiceReview.getProductReviews(): Api Version: "
					+ apiVersion);
			logDebug("BBBGSBazaarVoiceReview.getProductReviews(): Limit: "
					+ limit);
			
			if(BBBUtility.isNotEmpty(sortingOrder)){
				sortingOrderValue = getSortQueryValue() + sortingOrder;
			} else {
				sortingOrderValue = getSortQueryValue() + BBBStoreRestConstants.SORT_DESC_ORDER;
			}
			
			// Set the required params, paramValuesMap and optionalParams
			if(BBBUtility.isNotEmpty(passKey) && BBBUtility.isNotEmpty(apiVersion) 
					&& BBBUtility.isNotEmpty(getApiPassQuery()) && BBBUtility.isNotEmpty(getApiVersionQuery())) {
				
				requiredParams.add(getApiPassQuery());
				requiredParams.add(getApiVersionQuery());
				paramsValuesMap.put(getApiPassQuery(), passKey);
				paramsValuesMap.put(getApiVersionQuery(), apiVersion);
				
				if(BBBUtility.isNotEmpty(getFilterQuery()) && BBBUtility.isNotEmpty(getFilterQueryValue()) && BBBUtility.isNotEmpty(productId)){
					requiredParams.add(getFilterQuery());
					paramsValuesMap.put(getFilterQuery(), getFilterQueryValue() + productId);
				}
				if(BBBUtility.isNotEmpty(getIncludeQuery()) && BBBUtility.isNotEmpty(getIncludeQueryValue())){
					optionalParams.add(getIncludeQuery());
					paramsValuesMap.put(getIncludeQuery(), getIncludeQueryValue());
				}
				if(BBBUtility.isNotEmpty(getStatsQuery()) && BBBUtility.isNotEmpty(getStatsQueryValue())){
					optionalParams.add(getStatsQuery());
					paramsValuesMap.put(getStatsQuery(), getStatsQueryValue());
				}
				if(BBBUtility.isNotEmpty(getSortQuery()) && BBBUtility.isNotEmpty(sortingOrderValue)){
					optionalParams.add(getSortQuery());
					paramsValuesMap.put(getSortQuery(), sortingOrderValue);
				}
				if(BBBUtility.isNotEmpty(getLimitQuery()) && BBBUtility.isNotEmpty(limit)){
					optionalParams.add(getLimitQuery());
					paramsValuesMap.put(getLimitQuery(), limit);
				}
				
				
			} else {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.BAZAAR_VOICE_API_CALL
						+ " getProductReviews");
				throw new BBBBusinessException(BBBStoreRestConstants.ERROR_BAZAAR_VOICE_1003,BBBStoreRestConstants.ERROR_BAZAAR_VOICE_PARAM_ERROR);
			}
			
			String response = getHttpCallInvoker().invoke(bazaarVoiceUrl, requiredParams, optionalParams, paramsValuesMap);
			logDebug("BBBGSBazaarVoiceReview.getProductReviews(): Response Json:" + response);

			ObjectMapper mapper = new ObjectMapper();

			if (BBBUtility.isNotEmpty(response)) {
				reviewsVO = mapper.readValue(response, Reviews.class);
			}
			
			//Check for error during Bazaar voice call
			if(reviewsVO.getHasErrors()){
				Iterator<Object> errorIt = reviewsVO.getErrors().iterator();
				while(errorIt.hasNext()){
					logError(errorIt.next().toString());
				}
			}

			logDebug("BBBGSBazaarVoiceReview.getProductReviews(): TotalResults:"
					+ reviewsVO.getTotalResults());
		} 
		catch (BBBSystemException e) {
			logError(BBBStoreRestConstants.ERROR_BAZAAR_VOICE_MSG);
			BBBUtility.passErrToPage(BBBStoreRestConstants.ERROR_BAZAAR_VOICE_1001,BBBStoreRestConstants.ERROR_BAZAAR_VOICE_MSG);
		}
		
		catch (IOException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.BAZAAR_VOICE_API_CALL
					+ " getProductReviews");
			logError(BBBStoreRestConstants.ERROR_BAZAAR_VOICE_ERROR);
			throw new BBBBusinessException(BBBStoreRestConstants.ERROR_BAZAAR_VOICE_1002,BBBStoreRestConstants.ERROR_BAZAAR_VOICE_ERROR);
			
		}
		finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.BAZAAR_VOICE_API_CALL
					+ " getProductReviews");
		}
		logDebug("BBBGSBazaarVoiceReview.getProductReviews() method ends");
		
		return reviewsVO;
	}

}
