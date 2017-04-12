/**
 * 
 */
package com.bbb.integration.dashboard;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSARepository;
import atg.commerce.order.OrderLookup;
import atg.repository.MutableRepository;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * @author akhat1
 * 
 * This droplet returns the ATG orders and legacy orders list for
 * a given profile or for a given orderId and overrides the default enable security 
 * flag of OrderLookUp class
 */
public class DashboardDataDroplet extends OrderLookup {
	
	private MutableRepository orderRepository;
	List<String> siteId ;
	String submittedOrderCountQuery;
	String unSubmittedOrderCountQuery;
	String orderCountQueryAll;
	String orderCountQueryConcept;


	final String[] channelArrayDSK = new String[]{"0","3"};
	final String[] channelArrayMOB = new String[]{"1", "2", "4", "5"};
	final String[] siteIdArrayECOM = new String[]{"BedBathUS", "BuyBuyBaby", "BedBathCanada"};
	final String[] siteIdArrayTBS = new String[]{"TBS_BedBathUS", "TBS_BuyBuyBaby", "TBS_BedBathCanada"};


	public MutableRepository getOrderRepository() {
		return orderRepository;
	}

	public void setOrderRepository(MutableRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
	
	public String getSubmittedOrderCountQuery() {
		return submittedOrderCountQuery;
	}

	public void setSubmittedOrderCountQuery(String submittedOrderCountQuery) {
		this.submittedOrderCountQuery = submittedOrderCountQuery;
	}

	public String getUnSubmittedOrderCountQuery() {
		return unSubmittedOrderCountQuery;
	}

	public void setUnSubmittedOrderCountQuery(String unSubmittedOrderCountQuery) {
		this.unSubmittedOrderCountQuery = unSubmittedOrderCountQuery;
	}


	public String getOrderCountQueryAll() {
		return orderCountQueryAll;
	}

	public void setOrderCountQueryAll(String orderCountQueryAll) {
		this.orderCountQueryAll = orderCountQueryAll;
	}
	
	public String getOrderCountQueryConcept() {
		return orderCountQueryConcept;
	}

	public void setOrderCountQueryConcept(String orderCountQueryConcept) {
		this.orderCountQueryConcept = orderCountQueryConcept;
	}

	/**
	 * Method will initiate the GetOrderDetails 
	 * 
	 */
	
	@Override
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {
		int submittedOrderCountStore = 0;
		int submittedOrderCountMobile = 0;
		int submittedOrderCountTBS = 0;
		int unSubmittedOrderCountStore = 0;
		int unSubmittedOrderCountMobile = 0;
		int unSubmittedOrderCountTBS = 0;
		Map<String, String> orderCountCurrent;
		Map<String, String> orderCountYesterday;
		Map<String, String> orderCountLastWeek;
		String operationRequired = pReq.getParameter("operationRequired");
		String channel = pReq.getParameter("channel");
		if(operationRequired.equalsIgnoreCase("SubmittedOrderCount")){
			try {
				submittedOrderCountStore = fetchOrderData(channelArrayDSK , siteIdArrayECOM , getSubmittedOrderCountQuery());
				submittedOrderCountMobile = fetchOrderData(channelArrayMOB , siteIdArrayECOM , getSubmittedOrderCountQuery());
				submittedOrderCountTBS = fetchOrderData(channelArrayDSK , siteIdArrayTBS , getSubmittedOrderCountQuery());
				unSubmittedOrderCountStore = fetchOrderData(channelArrayDSK , siteIdArrayECOM , getUnSubmittedOrderCountQuery());
				unSubmittedOrderCountMobile = fetchOrderData(channelArrayMOB , siteIdArrayECOM , getUnSubmittedOrderCountQuery());
				unSubmittedOrderCountTBS = fetchOrderData(channelArrayDSK , siteIdArrayTBS , getUnSubmittedOrderCountQuery());
				pReq.setParameter("submittedOrderCountStore", submittedOrderCountStore);
				pReq.setParameter("submittedOrderCountMobile", submittedOrderCountMobile);
				pReq.setParameter("submittedOrderCountTBS", submittedOrderCountTBS);
				pReq.setParameter("unSubmittedOrderCountStore", unSubmittedOrderCountStore);
				pReq.setParameter("unSubmittedOrderCountMobile", unSubmittedOrderCountMobile);
				pReq.setParameter("unSubmittedOrderCountTBS", unSubmittedOrderCountTBS);
			} catch (BBBBusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(operationRequired.equalsIgnoreCase("OrderComparision")){
			try {
				orderCountCurrent = fetchOrderComparison(0,-1, getOrderCountQueryAll());
				orderCountYesterday = fetchOrderComparison(1,0, getOrderCountQueryAll());
				orderCountLastWeek = fetchOrderComparison(7,6, getOrderCountQueryAll());
				pReq.setParameter("orderCountCurrent", orderCountCurrent);
				pReq.setParameter("orderCountYesterday", orderCountYesterday);
				pReq.setParameter("orderCountLastWeek", orderCountLastWeek);
			}catch (BBBBusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(operationRequired.equalsIgnoreCase("SpecificOrderComparision") && channel.equalsIgnoreCase("Desktop")){
			try {
				orderCountCurrent = fetchSpecificOrderComparison(0,-1, channelArrayDSK , siteIdArrayECOM, getOrderCountQueryConcept());
				orderCountYesterday = fetchSpecificOrderComparison(1,0, channelArrayDSK , siteIdArrayECOM,getOrderCountQueryConcept());
				orderCountLastWeek = fetchSpecificOrderComparison(7,6, channelArrayDSK , siteIdArrayECOM,getOrderCountQueryConcept());
				pReq.setParameter("orderCountCurrent", orderCountCurrent);
				pReq.setParameter("orderCountYesterday", orderCountYesterday);
				pReq.setParameter("orderCountLastWeek", orderCountLastWeek);
			}catch (BBBBusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(operationRequired.equalsIgnoreCase("SpecificOrderComparision") && channel.equalsIgnoreCase("Mobile")){
				try {
					orderCountCurrent = fetchSpecificOrderComparison(0,-1, channelArrayMOB , siteIdArrayECOM, getOrderCountQueryConcept());
					orderCountYesterday = fetchSpecificOrderComparison(1,0, channelArrayMOB , siteIdArrayECOM,getOrderCountQueryConcept());
					orderCountLastWeek = fetchSpecificOrderComparison(7,6, channelArrayMOB , siteIdArrayECOM,getOrderCountQueryConcept());
					pReq.setParameter("orderCountCurrent", orderCountCurrent);
					pReq.setParameter("orderCountYesterday", orderCountYesterday);
					pReq.setParameter("orderCountLastWeek", orderCountLastWeek);
				}catch (BBBBusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}else if(operationRequired.equalsIgnoreCase("SpecificOrderComparision") && channel.equalsIgnoreCase("TBS")){
					try {
						orderCountCurrent = fetchSpecificOrderComparison(0,-1, channelArrayDSK , siteIdArrayTBS, getOrderCountQueryConcept());
						orderCountYesterday = fetchSpecificOrderComparison(1,0, channelArrayDSK , siteIdArrayTBS,getOrderCountQueryConcept());
						orderCountLastWeek = fetchSpecificOrderComparison(7,6, channelArrayDSK , siteIdArrayTBS,getOrderCountQueryConcept());
						pReq.setParameter("orderCountCurrent", orderCountCurrent);
						pReq.setParameter("orderCountYesterday", orderCountYesterday);
						pReq.setParameter("orderCountLastWeek", orderCountLastWeek);
					}catch (BBBBusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}
		pReq.serviceLocalParameter("output", pReq, pRes);
		
	  }
	
	
	public int fetchOrderData(String[] channel, String[] siteId, String query) throws BBBBusinessException {
		logDebug("DashboardDataDroplet.fetchOrderData start :: siteId " + siteId + " query :: " + query);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		int orderCountTotal = 0;
		
		try {
			connection = ((GSARepository) this.getOrderRepository())
					.getDataSource().getConnection();
			if (connection != null) {
				preparedStatement = connection.prepareStatement(query);
				for(int i=0; i<channel.length ; i++) {
					for(int j=0; j<siteId.length ; j++) {
					preparedStatement.setString(1,  channel[i]);
					preparedStatement.setString(2, siteId[j]);
		            resultSet = preparedStatement.executeQuery();
			            if (resultSet != null) {
							while (resultSet.next()) {
								int orderCount = resultSet.getInt("rowcount");
								orderCountTotal = orderCountTotal + orderCount;
							}
			            }
					}
				}
				
			}
		} catch (SQLException sqlException) {
			logError("SQL exception occurred while fetching the data from the Order Repo");
			throw new BBBBusinessException("SQL exception occurred while fetching the data from the Order Repo",sqlException);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logError("SQL exception occurred while closing the connection");
			}
		}
		logDebug("DashboardDataDroplet end :: orderCountTotal " + orderCountTotal);
		return orderCountTotal;
	}
	
	public Map<String, String> fetchOrderComparison(int startTime, int endTime, String query) throws BBBBusinessException {
		logDebug("DashboardDataDroplet.fetchOrderComparison start :: Start " + startTime + " End : " + endTime + " query :: " + query);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		Map<String, String> orderCount = new TreeMap<>();
		
		try {
			connection = ((GSARepository) this.getOrderRepository())
					.getDataSource().getConnection();
			if (connection != null) {
				preparedStatement = connection.prepareStatement(query);
					preparedStatement.setInt(1, startTime);
					preparedStatement.setInt(2, endTime);
		            resultSet = preparedStatement.executeQuery();
		            if (resultSet != null) {
		            	logDebug("Result set from Order Repo ::" + resultSet.toString());
						while (resultSet.next()) {
							String hour = resultSet.getString("thehour");
							String count = resultSet.getString("rowcount");
							orderCount.put(hour, count);
						}
		            }
				
			}
		} catch (SQLException sqlException) {
			logError("SQL exception occurred while fetching the data from the Order Repo");
			throw new BBBBusinessException("SQL exception occurred while fetching the data from the Order Repo",sqlException);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logError("SQL exception occurred while closing the connection");
			}
		}
		logDebug("DashboardDataDroplet end :: orderCountTotal " + orderCount);
		return orderCount;
	}
	
	public Map<String, String> fetchSpecificOrderComparison(int startTime, int endTime, String[] channel, String[] siteId, String query) throws BBBBusinessException {
		logDebug("DashboardDataDroplet.fetchOrderComparison start :: Start " + startTime + " End : " + endTime + " query :: " + query);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		Map<String, String> orderCount = new TreeMap<>();
		int orderCountTotal = 0;
		String hour = "";
		
		try {
			connection = ((GSARepository) this.getOrderRepository())
					.getDataSource().getConnection();
			if (connection != null) {
				preparedStatement = connection.prepareStatement("select trunc(submitted_date,'HH') thehour, count(1) rowcount from bbb_core.dcspp_order where state!='INCOMPLETE' and submitted_date>=trunc(sysdate-?) and submitted_date<=trunc(sysdate -?) and SALES_CHANNEL IN ? and SITE_ID IN ? group by trunc(submitted_date,'HH') order by thehour asc");
					preparedStatement.setInt(1, startTime);
					preparedStatement.setInt(2, endTime);
					for(int i=0; i<channel.length ; i++) {
						for(int j=0; j<siteId.length ; j++) {
						preparedStatement.setString(3,  channel[i]);
						preparedStatement.setString(4, siteId[j]);
			            resultSet = preparedStatement.executeQuery();
			            logDebug("Result set from Order Repo ::" + resultSet.toString());
				            if (resultSet != null) {
								while (resultSet.next()) {
									int orderCountSpecific = resultSet.getInt("rowcount");
									hour = resultSet.getString("thehour");
									orderCountTotal = orderCountTotal + orderCountSpecific;
								}
				            }
						}
					}
					orderCount.put(hour, Integer.toString(orderCountTotal));
		            logDebug("Result set from Order Repo ::" + resultSet.toString());
				
			}
		} catch (SQLException sqlException) {
			logError("SQL exception occurred while fetching the data from the Order Repo");
			throw new BBBBusinessException("SQL exception occurred while fetching the data from the Order Repo",sqlException);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logError("SQL exception occurred while closing the connection");
			}
		}
		logDebug("DashboardDataDroplet end :: orderCountTotal " + orderCount);
		return orderCount;
	}
	
}
