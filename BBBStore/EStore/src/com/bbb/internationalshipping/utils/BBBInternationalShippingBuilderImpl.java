package com.bbb.internationalshipping.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import atg.adapter.gsa.GSARepository;
import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.borderfree.cache.BBBUSIPStartUpCache;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.vo.BBBInternationalContextVO;
import com.bbb.internationalshipping.vo.BBBInternationalCurrencyVO;
import com.bbb.internationalshipping.vo.BBBInternationalLocationVO;
import com.bbb.internationalshipping.vo.BBBInternationalPropertyManagerVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * This interface models around building of Implementation of API to be used
 * in International Shipping.
 */
public class BBBInternationalShippingBuilderImpl extends BBBGenericService
implements BBBInternationalShippingBuilder {

	private static final String COUNTRY = "COUNTRY";
	/**
	 * This variable is used to point to International Repository.
	 */
	private Repository internationalRepository;
	/**
	 * This variable is used to point to ipListQuery.
	 */
	/**
	 * This variable is used to point to countryCurrencyQuery.
	 */
	private String countryCurrencyQuery;
	/**
	 * This variable is used to point to internationalPropertyManager.
	 */
	private BBBInternationalPropertyManagerVO internationalPropertyManager;
	/**
	 * This variable is used to point to internationalPropertyManager.
	 */
	private BBBCatalogTools catalogTools;
	/**
	 * This variable is used to point to ipAddressCache.
	 */
	private BBBUSIPStartUpCache ipAddressCache;
	/**
	 * This variable is used to fetch to countryByIpAddressQuery.
	 */
	private String countryByIpAddressQuery;
	/**
	 * @return the internationalRepository
	 */
	public final Repository getInternationalRepository() {
		return internationalRepository;
	}

	/**
	 * @param internationalRepository the internationalRepository to set
	 */
	public final void setInternationalRepository(final Repository internationalRepository) {
		this.internationalRepository = internationalRepository;

	/**
	 * @return the ipListQuery
	 */

	/**
	 * @param ipListQuery the ipListQuery to set
	 */
	}

	/**
	 * @return the internationalPropertyManager
	 */
	public BBBInternationalPropertyManagerVO getInternationalPropertyManager() {
		return internationalPropertyManager;
	}

	/**
	 * @param internationalPropertyManager the internationalPropertyManager to set
	 */
	public void setInternationalPropertyManager(final 
			BBBInternationalPropertyManagerVO internationalPropertyManager) {
		this.internationalPropertyManager = internationalPropertyManager;
	}

	/**
	 * @return the countryCurrencyQuery
	 */
	public final String getCountryCurrencyQuery() {
		return countryCurrencyQuery;
	}

	/**
	 * @param countryCurrencyQuery the countryCurrencyQuery to set
	 */
	public final void setCountryCurrencyQuery(final String countryCurrencyQuery) {
		this.countryCurrencyQuery = countryCurrencyQuery;
	}

	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public final void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/* 
	 * This Method will built the International context from the IP received
	 * with the currency and country details
	 * Will check the IP range falling in between FROM_IP and TO_IP column of E4X_IP_LIST table.
	 */
	@Override
	public final BBBInternationalContextVO buildContextFromIP(final String clientIPAddress) throws BBBSystemException, BBBBusinessException {
		logDebug("Entering class: InternationalShippingBuilderImpl,  "
				+ "method : buildContextFromIP : clientIPAddress : "
				+ clientIPAddress);

		BBBInternationalContextVO iContext = new BBBInternationalContextVO();
		if(BBBUtility.isNotEmpty(clientIPAddress)) {
			final long ipAddressValue = calculateIPRange(clientIPAddress);
			String countryCode = BBBInternationalShippingConstants.DEFAULT_COUNTRY;
			Connection con = null;
			PreparedStatement preparedStmt = null;
			ResultSet resultSet = null;
				if (ipAddressValue == -1) {
					iContext = buildContextBasedOnCountryCode(countryCode);
				} else if (getIpAddressCache().isCacheEnabled() && getIpAddressCache().isIpCacheReady()) {
					
					countryCode = getIpAddressCache().lookUpInUSIPCache(ipAddressValue);
					if (StringUtils.isEmpty(countryCode)) {
						countryCode = this.fecthCountryCode(con, preparedStmt, resultSet, 
								ipAddressValue, countryCode, iContext);
					}
					logDebug("Country code found from repository for the given IP address is [" + countryCode + "]");
		            iContext = buildContextBasedOnCountryCode(countryCode);
				} else {
					countryCode = this.fecthCountryCode(con, preparedStmt, resultSet, 
							ipAddressValue, countryCode, iContext);
						iContext = buildContextBasedOnCountryCode(countryCode);
				}
				logDebug("Inside class: InternationalShippingBuilderImpl,  "
						+ "method : buildContextFromIP : countryCode" + countryCode);
			
		} else {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1004, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1004);
		}

		logDebug("Exiting class: InternationalShippingBuilderImpl,  "
				+ "method : buildContextFromIP");

		return iContext;
	}

	private String fecthCountryCode (Connection con, PreparedStatement preparedStmt, 
			ResultSet resultSet, long ipAddressValue, String countryCode, 
			BBBInternationalContextVO iContext) throws BBBBusinessException, BBBSystemException {
		try {
			con = ((GSARepository)getInternationalRepository()).getDataSource().getConnection();
			if(con != null) {
				logDebug("SQL query to be fired for IP address ["+ ipAddressValue +"] is : " + getCountryByIpAddressQuery());
				preparedStmt = con.prepareStatement(getCountryByIpAddressQuery());
				if(preparedStmt != null){
					preparedStmt.setLong(1, ipAddressValue);
					resultSet = preparedStmt.executeQuery();
					if(resultSet != null ){
						while(resultSet.next()){
							countryCode = resultSet.getString(COUNTRY);
							logDebug("Country code found from repository for the given IP address is [" + countryCode + "]");
							break;
						}
					}
				}
			}
		} catch (SQLException excep) {
			logError("SQL exception while fetching country code for the given IP address in BBBInternationalShippingBuilder", excep);
			throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1001, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1001);
		}finally{
			if(resultSet!=null){
				try {
					resultSet.close();
				} catch (SQLException e) {
					logError("BBBInternationalShippingBuilder:SQL exception while closing the prepared statement" +e);
			throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1001, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1001);
				}
				}
			if (preparedStmt != null){
				try {
					preparedStmt.close();
				} catch (SQLException e) {
					logError("BBBInternationalShippingBuilder:SQL exception while closing the prepared statement" +e);
			throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1001, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1001);
				}
			}
			if (con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logError("BBBInternationalShippingBuilder:SQL exception while closing the JDBC connection" +e);
					throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1001, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1001);
				}
			}
		}
		return countryCode;
	}
	/**
	 * This method converts the passed ClientIPAddress into Long value to
	 * compare this value in DB to identify Client's Country
	 * 
	 * @param ipAddress
	 * @return
	 */
	private long calculateIPRange(final String ipAddress) {

		logDebug("Entering class: InternationalShippingBuilderImpl,  "
				+ "method : calculateIPRange : IP Address : "
				+ ipAddress);

		long finalIP = 0;

		try {
			final String[] parts = ipAddress.split("\\.");
			for (int i = 0; i < parts.length; i++) {
				final int power = 3 - i;
				finalIP += ((Integer.parseInt(parts[i]) % 256 * Math // NOPMD by njai13 on 5/22/14 4:30 PM
						.pow(256, power)));
			}
		} catch (NumberFormatException e) {
			logError("Unable to parse IP Address. Got NumberFormatException. Build default context. ", e);
			finalIP = -1;
		} catch (Exception e) {
			logError("Unable to parse IP Address. Build default context. ", e);
			finalIP = -1;
		}
		logDebug("Exiting class: InternationalShippingBuilderImpl,  "
				+ "method : calculateIPRange : final IP : "
				+ finalIP);

		return finalIP;
	}

	/**
	 * This method will get the Country and Currency drop down
	 * with currency and location details.
	 * @param merchantId
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public final List<BBBInternationalContextVO> buildContextAll() throws BBBSystemException, BBBBusinessException {
		logDebug("Entering class: InternationalShippingBuilderImpl,  "
				+ "method : buildContextAll");
		List<BBBInternationalContextVO> countryCurrencyList = null;
		final String merchantId = this.getMerchantId();
		if(BBBUtility.isNotEmpty(merchantId)) {
			try {
				final RepositoryView countryListView = getInternationalRepository()
						.getView(BBBInternationalShippingConstants.COUNTRY_LIST_ITEMDESCRIPTOR);
				final RqlStatement statement = RqlStatement.parseRqlStatement(getCountryCurrencyQuery(), true);
				final Object[] params = {merchantId, BBBCoreConstants.YES_CHAR };
				final RepositoryItem[] countryListItems = statement.executeQuery(
						countryListView, params);
				if (countryListItems != null && countryListItems.length > 0) {

					BBBInternationalContextVO iContext = null;
					// Get country/currency details from Repository
					countryCurrencyList = new ArrayList<BBBInternationalContextVO>();
					for (final RepositoryItem item : countryListItems) {
						iContext = populateContextObject(item);
						countryCurrencyList.add(iContext);
					}
				}

			}
			catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException"), e);
				throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1002, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1002);
			}
		} else {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1006, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1006);
		}
		logDebug("Exiting class: InternationalShippingBuilderImpl,  "
				+ "method : buildContextAll");
		return countryCurrencyList;
	}

	/**
	 * This method will do the following things
	 * <ol>
	 * <li>Read the Country repository item.</li>
	 * <li>Create a new context object.</li>
	 * <li>Update the Context object with Shipping location & Shopping Currency
	 * details.</li>
	 * </ol>
	 * @param pCountryRepoItem
	 * @return
	 */
	private BBBInternationalContextVO populateContextObject(
			final RepositoryItem pCountryRepoItem) {
		BBBInternationalContextVO iContext = null;
		if (pCountryRepoItem != null) {
			iContext = new BBBInternationalContextVO();
			final BBBInternationalLocationVO locationObject = new BBBInternationalLocationVO();
			final BBBInternationalCurrencyVO currencyObject = new BBBInternationalCurrencyVO();
			locationObject.setCountryCode((String) pCountryRepoItem
					.getPropertyValue(getInternationalPropertyManager()
							.getCountryCodePropertyName()));
			locationObject.setCountryName((String) pCountryRepoItem
					.getPropertyValue(getInternationalPropertyManager()
							.getCountryNamePropertyName()));
			if (null != pCountryRepoItem
					.getPropertyValue(getInternationalPropertyManager()
							.getShippingEnabledPropertyName())) {
				locationObject.setShippingEnabled((boolean) pCountryRepoItem
						.getPropertyValue(getInternationalPropertyManager()
								.getShippingEnabledPropertyName()).equals(BBBCoreConstants.YES_CHAR));
			}
			currencyObject.setCurrencyCode((String) pCountryRepoItem
					.getPropertyValue(getInternationalPropertyManager()
							.getCurrencyCodePropertyName()));
			currencyObject.setCurrencyName((String) pCountryRepoItem
					.getPropertyValue(getInternationalPropertyManager()
							.getCurrencyNamePropertyName()));
			currencyObject.setCurrencySymbol((String) pCountryRepoItem
					.getPropertyValue(getInternationalPropertyManager()
							.getCurrencySymbolPropertyName()));
			if (null != pCountryRepoItem
					.getPropertyValue(getInternationalPropertyManager()
							.getBillingEnabledPropertyName())) {
				currencyObject.setBillingEnabled((boolean) pCountryRepoItem
						.getPropertyValue(getInternationalPropertyManager()
								.getBillingEnabledPropertyName()).equals(BBBCoreConstants.YES_CHAR));

				if( !currencyObject.isBillingEnabled())
				{
					currencyObject.setCurrencyCode(BBBInternationalShippingConstants.USD);
					currencyObject.setCurrencyName(BBBInternationalShippingConstants.US_Dollar);
					currencyObject.setCurrencySymbol(BBBInternationalShippingConstants.USD);
				}
			}
			iContext.setShippingLocation(locationObject);
			iContext.setShoppingCurrency(currencyObject);
		}
		return iContext;

	}

	/**
	 * Gets the country currency details based on the Country and currency Code
	 * 
	 * @param countryCode
	 *            the country code
	 * @param currencyCode
	 *            the currency code
	 * @return the country currency details
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public BBBInternationalContextVO buildContextBasedOnCountryCode(
			final String countryCode) throws BBBSystemException, BBBBusinessException {
		logDebug("ENTERING class : InternationalShippingBuilderImpl.buildContextBasedOnCountryCode ");
		logDebug("InternationalShippingBuilderImpl.buildContextBasedOnCountryCode: Country Code value : "
				+ countryCode);

		RepositoryItem countryRepoItem;
		BBBInternationalContextVO iContext = null;
		if(BBBUtility.isNotEmpty(countryCode) ) {
			try {
				final String merchantId = this.getMerchantId();
				if(BBBUtility.isNotEmpty(merchantId)) {
					countryRepoItem = getInternationalRepository().getItem(
							countryCode + BBBCoreConstants.COLON
							+ merchantId,
							BBBInternationalShippingConstants.COUNTRY_LIST_ITEMDESCRIPTOR);
					if(countryRepoItem==null || (countryRepoItem!=null && BBBCoreConstants.NO_CHAR.equals((String)countryRepoItem.getPropertyValue(getInternationalPropertyManager().getShippingEnabledPropertyName()))))
					{
						countryRepoItem = getInternationalRepository().getItem(
								BBBInternationalShippingConstants.DEFAULT_COUNTRY + BBBCoreConstants.COLON
								+ merchantId,
								BBBInternationalShippingConstants.COUNTRY_LIST_ITEMDESCRIPTOR);
					}

					if (null != countryRepoItem) {
						iContext = populateContextObject(countryRepoItem);
					}

				} else {
					throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1006, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1006);
				}
			} catch (RepositoryException e) {			
				logError(LogMessageFormatter.formatMessage(null, "RepositoryException"), e);
				throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1003, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1003);
			}
		} else {
			throw new BBBBusinessException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1005, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1005);
		}
		logDebug("EXITING class : InternationalShippingBuilderImpl.buildContextBasedOnCountryCode");
		
		return iContext;
	}

	/**
	 * Method returns the Merchant Id.
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getMerchantId() throws BBBSystemException, BBBBusinessException{

		final List<String> merchantId = this.getCatalogTools().getAllValuesForKey(
				BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.MERCHANT_ID);
		if(null != merchantId && 
				!merchantId.isEmpty()) {
			return merchantId.get(0);
		}
		return "";
	}

	/**
	 * Method returns the currency Map
	 * with Key as Currency Name and value as Currency Code
	 * @param internationalContextVOs
	 * @return
	 */
	public Map<String,String> buildCurrencyMap(final List<BBBInternationalContextVO> internationalContextVOs) {
		logDebug("ENTERING class : InternationalShippingBuilderImpl.buildCurrencyMap ");
		final Map<String, String> currencyMap = new TreeMap<String, String>();
		if(internationalContextVOs!=null && !internationalContextVOs.isEmpty()){
			for (final BBBInternationalContextVO contextVO : internationalContextVOs) {
				if (null != contextVO.getShoppingCurrency() 
						&& contextVO.getShoppingCurrency().isBillingEnabled() 
						&& !currencyMap.containsKey(contextVO.getShoppingCurrency().getCurrencyName())) {
					currencyMap.put(contextVO.getShoppingCurrency().getCurrencyName(),
							contextVO.getShoppingCurrency().getCurrencyCode());
				}
			}
		}
		logDebug("currency map :[" + currencyMap + "]");
		logDebug("EXITING class : InternationalShippingBuilderImpl.buildCurrencyMap ");
		return currencyMap;

	}
	
	/**
	 * @return the ipAddressCache
	 */
	public BBBUSIPStartUpCache getIpAddressCache() {
		return ipAddressCache;
	}
	
	/**
	 * @param ipAddressCache the ipAddressCache to set
	 */
	public void setIpAddressCache(BBBUSIPStartUpCache ipAddressCache) {
		this.ipAddressCache = ipAddressCache;
	}

	/**
	 * @return the countryByIpAddressQuery
	 */
	public String getCountryByIpAddressQuery() {
		return countryByIpAddressQuery;
	}

	/**
	 * @param countryByIpAddressQuery the countryByIpAddressQuery to set
	 */
	public void setCountryByIpAddressQuery(String countryByIpAddressQuery) {
		this.countryByIpAddressQuery = countryByIpAddressQuery;
	}
	
}

