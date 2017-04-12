package com.bbb.borderfree.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import atg.adapter.gsa.GSARepository;
import atg.nucleus.ServiceException;
import atg.repository.Repository;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * This class is used to create IP cache
 * on jvm startup or via BBBUSIPCacheScheduler.
 * 
 * @author apan25
 *
 */
public class BBBUSIPStartUpCache extends BBBGenericService {

	private Repository internationalRepository;
	
	private Map<Long, BBBIPAddressVO> usIPAddressMap;
	
	private BBBCatalogTools catalogTools;
	
	private static final String SELECT_US_COUNTRY_FROM_E4X_IP_LIST = 
			"Select * from E4X_IP_LIST where COUNTRY = 'US'";
	
	/**
	 * Check if ip cache is ready.
	 */
	private boolean ipCacheReady;
	
	/**
	 * cache enable property
	 */
    private boolean cacheEnabled;
    

    /**
     * This method executes sql query and populates BBBIPAddressVO.
     * 
     * @return Map<Long, BBBIPAddressVO>
     * @throws BBBBusinessException
     * @throws SQLException
     */
	public Map<Long, BBBIPAddressVO> buildUSIPAddressCache() throws BBBBusinessException, SQLException {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.BUILD_US_IP_CACHE, 
				BBBCoreConstants.BUILD_US_IP_ADDRESS_CAHE);
		logDebug("BBBUSIPStartUpCache.buildUSIPAddressCache : Start");
		
		Map<Long, BBBIPAddressVO> usIpAddressMap = new ConcurrentSkipListMap<Long, BBBIPAddressVO>();
		BBBIPAddressVO ipContext = null;

		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet resultSet = null;
		try {
			con = ((GSARepository)getInternationalRepository()).getDataSource().getConnection();
			
			if (con != null) {
				final String sqlQuery = SELECT_US_COUNTRY_FROM_E4X_IP_LIST;
				logDebug("SQL query to be fired for IP retrieving US IP addresses : " + sqlQuery);

				preparedStmt = con.prepareStatement(sqlQuery);
				if (preparedStmt != null) {
					resultSet = preparedStmt.executeQuery(sqlQuery);
					if (resultSet != null) {
						while (resultSet.next()) {
							ipContext = new BBBIPAddressVO();
							ipContext.setCountry(resultSet.getString(BBBCoreConstants.COUNTRY));
							ipContext.setFromIP(resultSet.getLong(BBBCoreConstants.FROM_IP));
							ipContext.setToIP(resultSet.getLong(BBBCoreConstants.TO_IP));
							usIpAddressMap.put(resultSet.getLong(BBBCoreConstants.FROM_IP), ipContext);
						}
					}
				}
			}
		}
		catch (SQLException excep) {
			logError("SQL exception while fetching country code for the given IP address "
					+ "in BBBUSIPStartUpCache", excep);
		} finally{
			closeResources(con, preparedStmt, resultSet);
		}
		
		if (!usIpAddressMap.isEmpty())
			setIpCacheReady(true);
		
		logDebug("BBBUSIPStartUpCache.buildUSIPAddressCache : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.BUILD_US_IP_CACHE, 
				BBBCoreConstants.BUILD_US_IP_ADDRESS_CAHE);
		return usIpAddressMap;
	}

	/**
	 * @param con
	 * @param preparedStmt
	 * @param resultSet
	 * @throws BBBBusinessException
	 */
	protected void closeResources(Connection con,
			PreparedStatement preparedStmt, ResultSet resultSet)
			throws BBBBusinessException {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (preparedStmt != null) {
				preparedStmt.close();
			}
			if (con != null) {
				con.close();
			}
		}
		catch (SQLException e) {
			throw new BBBBusinessException("An exception occurred while executing the sql statement.", e);
		}
	}

	/**
	 * This method is called on component startup.
	 */
	@Override
	public void doStartService() {
		logDebug("BBBUSIPStartUpCache.doStartService : Start");
		try {
			String enable_us_ip_startup_cache = this.getCatalogTools().
					getConfigValueByconfigType(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS).
					get(BBBCoreConstants.ENABLE_US_IP_STARTUP_CACHE);
			setCacheEnabled(Boolean.parseBoolean(enable_us_ip_startup_cache));
		} catch (BBBSystemException e) {
			logError(e);
		} catch (BBBBusinessException e) {
			logError(e);
		}
		 doCacheUSIpAddresses();
		 logDebug("BBBUSIPStartUpCache.doStartService : End");
	}
	
	/**
	 * This method caches US ip addresses.
	 */
	private void doCacheUSIpAddresses() {
		logInfo("BBBUSIPStartUpCache.doCacheUSIpAddress : Start");
		try {
			if (isCacheEnabled()) {
				logInfo("Is US Ip Start up cache enabled: " + isCacheEnabled());
				setIpCacheReady(false);
				setUsIPAddressMap(buildUSIPAddressCache());
			}
		} catch (Exception e) {
			logError(e);
		}
		logInfo("BBBUSIPStartUpCache.doCacheUSIpAddress : End");
	}
	
	/**
	 * This method clears ip cache.
	 */
	@Override
	public void doStopService() throws ServiceException {
		logDebug("BBBUSIPStartUpCache.doStopService : Start");
		if (isCacheEnabled()) {
			super.doStopService();
			this.clearUSIPAddressCache();
		}
		logDebug("BBBUSIPStartUpCache.doStopService : End");
	}
	
	/**
	 * This method rebuilds ip cache.
	 */
	public void doRebuildIPAddressCache() {
		logDebug("BBBUSIPStartUpCache.doRebuildIPAddressCache : Start");
		if (isCacheEnabled()) {
			this.clearUSIPAddressCache();
			try {
				setUsIPAddressMap(buildUSIPAddressCache());
			} catch (Exception e) {
				logError("Error resetting cache : ", e);
			}
		}
		logDebug("BBBUSIPStartUpCache.doRebuildIPAddressCache : End");
	}
	
	/**
	 * This method clears ip cache.
	 */
	private void clearUSIPAddressCache() {
		logDebug("BBBUSIPStartUpCache.clearUSIPAddressCache : Start");
		setIpCacheReady(false);
		usIPAddressMap.clear();
		logDebug("BBBUSIPStartUpCache.clearUSIPAddressCache : End");
	}
	
	/**
	 * @param ipaddress
	 * @return the country code from cached ip map
	 */
	public String lookUpInUSIPCache(long ipaddress) {
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.LOOKUP_IN_US_IP_CACHE, 
				BBBCoreConstants.LOOKUP_US_IP_CAHE);
		logDebug("BBBUSIPStartUpCache.lookUpInUSIPCache : Start");
		String countryCode = null;
		try {
			ConcurrentSkipListMap<Long, BBBIPAddressVO> ipMap = 
					(ConcurrentSkipListMap<Long, BBBIPAddressVO>) this.getUsIPAddressMap();

			if (ipMap != null) {

				Long key = ipMap.floorKey(ipaddress);
				if (key != null) {
					BBBIPAddressVO vo = ipMap.get(key);

					if (vo.contains(ipaddress)) {
						countryCode = vo.getCountry();
					} 
				}
			}
		} catch (Exception e) {
			logError("Error fecthing country from UsIPAddressMap: ", e);
		}
		
		logDebug("BBBUSIPStartUpCache.lookUpInUSIPCache : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.LOOKUP_IN_US_IP_CACHE, 
				BBBCoreConstants.LOOKUP_US_IP_CAHE);
		return countryCode;
	}
	
	/**
	 * @return the Repository
	 */
	public Repository getInternationalRepository() {
		return internationalRepository;
	}

	/**
	 * @param internationalRepository the internationalRepository to set
	 */
	public void setInternationalRepository(final Repository internationalRepository) {
		this.internationalRepository = internationalRepository;
	}
	
	/**
	 * @return the usIPAddressMap
	 */
	public Map<Long, BBBIPAddressVO> getUsIPAddressMap() {
		return usIPAddressMap;
	}

	/**
	 * @param usIPAddressMap the usIPAddressMap to set
	 */
	public void setUsIPAddressMap(Map<Long, BBBIPAddressVO> usIPAddressMap) {
		this.usIPAddressMap = usIPAddressMap;
	}

	/**
	 * 
	 * @return the ipCacheReady
	 */
	public boolean isIpCacheReady() {
		return ipCacheReady;
	}

	/**
	 * 
	 * @param ipCacheReady the ipCacheReady to set
	 */
	public void setIpCacheReady(boolean ipCacheReady) {
		this.ipCacheReady = ipCacheReady;
	}

	/**
	 * @return the cacheEnabled
	 */
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	/**
	 * @param cacheEnabled the cacheEnabled to set
	 */
	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}


	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
}
