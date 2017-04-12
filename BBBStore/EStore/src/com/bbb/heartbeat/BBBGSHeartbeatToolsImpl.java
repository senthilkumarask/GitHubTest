package com.bbb.heartbeat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import atg.adapter.gsa.GSARepository;
import atg.repository.MutableRepository;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.rest.stofu.vo.HeartbeatMonitoringVO;
import com.bbb.utils.BBBUtility;

public class BBBGSHeartbeatToolsImpl extends BBBGenericService implements
		BBBGSHeartbeatTools {

	private static final int DEFAULT_PERMISSIBLE_LIMIT = 60;
	private static final int DEFAULT_DESIRED_LIMIT = 30;
	private final String APP_ID = "app_Id";
	private final String STORE_ID = "store_Id";
	private final String TERMINAL_ID = "terminal_Id";
	private final String CHANNEL = "channel_Id";
	private final String CHANNELTHEME = "channel_theme_id";
	private final String FRIENDLY_NAME = "friendly_name";
	private final String APP_STATE = "app_state";
	private final String LOG_TIME = "log_time";
	private final long MILLI_SECOND_TO_MIN = 60000;
	private final String HEALTH_WHITE = "white";
	private final String HEALTH_GREEN = "green";
	private final String HEALTH_YELLOW = "yellow";
	private final String HEALTH_RED = "red";
	private final String APP_STATE_STARTING = "Active";
	private final String APP_STATE_HEARTBEAT = "Heartbeat";
	private final String APP_STATE_CLOSING = "In-Active";

	private final String SQL_STORE_ID = " and store_id=";
	private final String SQL_ORDERBY_STORE_ID = " order by store_id";
	private String orderByRownum;

	public String getOrderByRownum() {
		return orderByRownum;
	}

	public void setOrderByRownum(String orderByRownum) {
		this.orderByRownum = orderByRownum;
	}

	private String preFilterAppIdSqlQuery;

	public String getPreFilterAppIdSqlQuery() {
		return preFilterAppIdSqlQuery;
	}

	public void setPreFilterAppIdSqlQuery(String preFilterAppIdSqlQuery) {
		this.preFilterAppIdSqlQuery = preFilterAppIdSqlQuery;
	}

	private String postFilterAppIdSqlQuery;

	public String getPostFilterAppIdSqlQuery() {
		return postFilterAppIdSqlQuery;
	}

	public void setPostFilterAppIdSqlQuery(String postFilterAppIdSqlQuery) {
		this.postFilterAppIdSqlQuery = postFilterAppIdSqlQuery;
	}

	private String filterAppIdRows;

	public String getFilterAppIdRows() {
		return filterAppIdRows;
	}

	public void setFilterAppIdRows(String filterAppIdRows) {
		this.filterAppIdRows = filterAppIdRows;
	}

	private BBBCatalogTools catalogTools;

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	private MutableRepository configureRepository;

	public MutableRepository getConfigureRepository() {
		return configureRepository;
	}

	public void setConfigureRepository(MutableRepository configureRepository) {
		this.configureRepository = configureRepository;
	}

	private MutableRepository heartbeatRepository;

	public MutableRepository getHeartbeatRepository() {
		return heartbeatRepository;
	}

	public void setHeartbeatRepository(MutableRepository heartbeatRepository) {
		this.heartbeatRepository = heartbeatRepository;
	}

	private String sqlQuery;
	private String orderSqlByStoreId;
	private String sqlAppId;
	private String sqlStoreId;

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getOrderSqlByStoreId() {
		return orderSqlByStoreId;
	}

	public void setOrderSqlByStoreId(String orderSqlByStoreId) {
		this.orderSqlByStoreId = orderSqlByStoreId;
	}

	public String getSqlAppId() {
		return sqlAppId;
	}

	public void setSqlAppId(String sqlAppId) {
		this.sqlAppId = sqlAppId;
	}

	public String getSqlStoreId() {
		return sqlStoreId;
	}

	public void setSqlStoreId(String sqlStoreId) {
		this.sqlStoreId = sqlStoreId;
	}

	@Override
	public List<HeartbeatMonitoringVO> fetchHeartbeatMonitoringData(
			String filterStoreId, String filterAppId, String filterHealth)
			throws BBBSystemException, BBBBusinessException {
		logDebug("BBBGSHeartbeatToolsImpl Class fetchHeartbeatMonitoringData method started");
		String health = null;
		List<HeartbeatMonitoringVO> listHeartbeatMonitoringVO = null;
		float desiredLimit = DEFAULT_DESIRED_LIMIT;
		float permissibleLimit = DEFAULT_PERMISSIBLE_LIMIT;
		if (this.getCatalogTools() != null) {
			desiredLimit = Float.parseFloat(this
					.getCatalogTools()
					.getConfigValueByconfigType(
							BBBCatalogConstants.HEARTBEAT_CONFIG_TYPE)
					.get(BBBCatalogConstants.HEARTBEAT_DESIRED_LIMIT));
			permissibleLimit = Float.parseFloat(this
					.getCatalogTools()
					.getConfigValueByconfigType(
							BBBCatalogConstants.HEARTBEAT_CONFIG_TYPE)
					.get(BBBCatalogConstants.HEARTBEAT_PERMISSIBLE_LIMIT));
		} else {
			logError("catalog tools is not available");
		}
		if (isLoggingDebug()) {
			logDebug("filterStoreId [" + filterStoreId + "] , filterAppId ["
					+ filterAppId + "] , filterHealth [" + filterHealth + "]");
		}
		if (BBBUtility.isNotEmpty(filterAppId)) {
			listHeartbeatMonitoringVO = filterAppIdResults(filterAppId,
					filterHealth, filterStoreId, health, desiredLimit,
					permissibleLimit);
		} else {
			listHeartbeatMonitoringVO = filterNotAppIdResults(filterHealth,
					filterStoreId, desiredLimit, permissibleLimit);
		}
		logDebug("BBBGSHeartbeatToolsImpl Class fetchHeartbeatMonitoringData method exited with listHeartbeatMonitoringVO returned");
		return listHeartbeatMonitoringVO;
	}

	private List<HeartbeatMonitoringVO> filterNotAppIdResults(
			String filterHealth, String filterStoreId, float desiredLimit,
			float permissibleLimit) throws BBBBusinessException {
		Statement statement = null;
		ResultSet resultSet = null;
		Date currentTime = new Date();
		Connection connection = null;
		List<HeartbeatMonitoringVO> listHeartbeatMonitoringVO = null;
		String sql = this.getSqlQuery();
		if (BBBUtility.isNotEmpty(filterStoreId)) {
			sql = sql + SQL_STORE_ID + "'" + filterStoreId + "'";

		}
		sql = sql + SQL_ORDERBY_STORE_ID;
		if (isLoggingDebug()) {
			logDebug("SQL : " + sql);
		}
		try {
			connection = ((GSARepository) this.getHeartbeatRepository())
					.getDataSource().getConnection();

			if (connection != null) {

				String health = "";
				String appStateValue = "";
				HeartbeatMonitoringVO heartbeatMonitoringVO = null;
				listHeartbeatMonitoringVO = new ArrayList<HeartbeatMonitoringVO>();
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				while (resultSet.next()) {
					String appId = resultSet.getString(APP_ID);
					String storeId = resultSet.getString(STORE_ID);
					String terminalId = resultSet.getString(TERMINAL_ID);
					String channel = resultSet.getString(CHANNEL);
					String channelTheme = resultSet.getString(CHANNELTHEME);
					String friendlyName = resultSet.getString(FRIENDLY_NAME);
					int appState = resultSet.getInt(APP_STATE);
					Date logTime = resultSet.getTimestamp(LOG_TIME);
					health = this.getApplicationHealth(logTime, currentTime,
							desiredLimit, permissibleLimit);
					appStateValue = getApplicationState(appState);
					if (!BBBUtility.isEmpty(filterHealth)) {
						if (health.equalsIgnoreCase(filterHealth)) {
							heartbeatMonitoringVO = new HeartbeatMonitoringVO(
									appId, appStateValue, terminalId,
									friendlyName, channel, channelTheme,
									logTime, storeId, health);
							listHeartbeatMonitoringVO
									.add(heartbeatMonitoringVO);
						}
					} else {
						heartbeatMonitoringVO = new HeartbeatMonitoringVO(
								appId, appStateValue, terminalId, friendlyName,
								channel, channelTheme, logTime, storeId, health);
						listHeartbeatMonitoringVO.add(heartbeatMonitoringVO);
					}
				}
			}
		} catch (SQLException sqlException) {
			logError("SQL Exception Occurred while fetching the data from the Heartbeat repository");
			throw new BBBBusinessException(
					"SQL Exception Occurred while fetching the data from the Heartbeat repository",
					sqlException);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logError("SQL Exception Occurred closing the connection");
			}
		}
		return listHeartbeatMonitoringVO;
	}

	private List<HeartbeatMonitoringVO> filterAppIdResults(String filterAppId,
			String filterHealth, String filterStoreId, String health,
			float desiredLimit, float permissibleLimit)
			throws BBBBusinessException {
		Statement statement = null;
		ResultSet resultSet = null;
		Date currentTime = new Date();
		Connection connection = null;
		List<HeartbeatMonitoringVO> listHeartbeatMonitoringVO = null;
		List<HeartbeatMonitoringVO> tempListHeartbeatMonitoringVO = null;
		try {
			connection = ((GSARepository) this.getHeartbeatRepository())
					.getDataSource().getConnection();
			if (connection != null) {

				int filterAppIdRows = Integer.parseInt(this
						.getFilterAppIdRows()) + 1;
				String sql = this.getPreFilterAppIdSqlQuery() + "'"
						+ filterAppId + "' "
						+ this.getPostFilterAppIdSqlQuery() + filterAppIdRows
						+ " " + this.getOrderByRownum();
				if (isLoggingDebug()) {
					logDebug("SQL : " + sql);
				}

				listHeartbeatMonitoringVO = new ArrayList<HeartbeatMonitoringVO>();
				tempListHeartbeatMonitoringVO = new ArrayList<HeartbeatMonitoringVO>();
				String appStateValue = "";
				HeartbeatMonitoringVO heartbeatMonitoringVO = null;
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				logDebug("Filters : application ID[" + filterAppId + "]");
				while (resultSet.next()) {
					String appId = resultSet.getString(APP_ID);
					String storeId = resultSet.getString(STORE_ID);
					if (BBBUtility.isEmpty(filterStoreId)
							|| (BBBUtility.isNotEmpty(filterStoreId) && filterStoreId
									.equalsIgnoreCase(storeId))) {
						String terminalId = resultSet.getString(TERMINAL_ID);
						String channel = resultSet.getString(CHANNEL);
						String channelTheme = resultSet.getString(CHANNELTHEME);
						String friendlyName = resultSet
								.getString(FRIENDLY_NAME);
						int appState = resultSet.getInt(APP_STATE);
						Date logTime = resultSet.getTimestamp(LOG_TIME);
						appStateValue = getApplicationState(appState);
						heartbeatMonitoringVO = new HeartbeatMonitoringVO(
								appId, appStateValue, terminalId, friendlyName,
								channel, channelTheme, logTime, storeId,
								HEALTH_WHITE);
						tempListHeartbeatMonitoringVO
								.add(heartbeatMonitoringVO);
					} else {
						continue;
					}
				}
				int j = 0;
				if (tempListHeartbeatMonitoringVO.size() > 0) {
					health = this.getApplicationHealth(
							tempListHeartbeatMonitoringVO.get(0).getLogTime(),
							currentTime, desiredLimit, permissibleLimit);
					if (BBBUtility.isNotEmpty(filterHealth)) {
						if (health.equalsIgnoreCase(filterHealth)) {
							listHeartbeatMonitoringVO
									.add(tempListHeartbeatMonitoringVO.get(j));
							listHeartbeatMonitoringVO.get(j).setHealth(health);
							j = j + 1;
						}
					} else {
						listHeartbeatMonitoringVO
								.add(tempListHeartbeatMonitoringVO.get(j));
						listHeartbeatMonitoringVO.get(j).setHealth(health);
						j = j + 1;
					}
				}
				int i = 1;
				int limit = 0;
				if (tempListHeartbeatMonitoringVO.size() <= Integer
						.parseInt(this.getFilterAppIdRows())) {
					limit = tempListHeartbeatMonitoringVO.size();
				} else {
					limit = tempListHeartbeatMonitoringVO.size() - 1;
				}
				for (i = 1; i < limit; i++) {
					if (tempListHeartbeatMonitoringVO.size() <= Integer
							.parseInt(this.getFilterAppIdRows())) {
						if (i == limit - 1 || i == limit) {
							health = HEALTH_WHITE;
						} else {
							health = this.getApplicationHealth(
									tempListHeartbeatMonitoringVO.get(i + 1)
											.getLogTime(),
									tempListHeartbeatMonitoringVO.get(i)
											.getLogTime(), desiredLimit,
									permissibleLimit);
						}
					} else {
						health = this.getApplicationHealth(
								tempListHeartbeatMonitoringVO.get(i + 1)
										.getLogTime(),
								tempListHeartbeatMonitoringVO.get(i)
										.getLogTime(), desiredLimit,
								permissibleLimit);
					}

					if (BBBUtility.isNotEmpty(filterHealth)) {
						if (health.equalsIgnoreCase(filterHealth)) {
							listHeartbeatMonitoringVO
									.add(tempListHeartbeatMonitoringVO.get(j));
							listHeartbeatMonitoringVO.get(j).setHealth(health);
							j = j + 1;
						}
					} else {
						listHeartbeatMonitoringVO
								.add(tempListHeartbeatMonitoringVO.get(j));
						listHeartbeatMonitoringVO.get(j).setHealth(health);
						j = j + 1;
					}
				}
			}
		} catch (SQLException sqlException) {
			logError("SQL Exception Occurred while fetching the data from the Heartbeat repository");
			throw new BBBBusinessException(
					"SQL Exception Occurred while fetching the data from the Heartbeat repository",
					sqlException);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logError("SQL Exception Occurred closing the connection");
			}
		}

		return listHeartbeatMonitoringVO;
	}

	private String getApplicationState(int appState) {
		String applicationState = "";
		switch (appState) {
		case 0:
			applicationState = APP_STATE_STARTING;
			break;
		case 1:
			applicationState = APP_STATE_HEARTBEAT;
			break;
		case 2:
			applicationState = APP_STATE_CLOSING;
			break;
		default: //do nothing
			break;
		}

		return applicationState;
	}

	private String getApplicationHealth(Date initialDate, Date finalDate,
			float desiredLimit, float permissibleLimit) {
		long difference = finalDate.getTime() - initialDate.getTime();
		float diffInMinutes = difference / MILLI_SECOND_TO_MIN;
		if (diffInMinutes < desiredLimit) {
			return HEALTH_GREEN;
		} else if (diffInMinutes < permissibleLimit) {
			return HEALTH_YELLOW;
		} else {
			return HEALTH_RED;
		}
	}
}