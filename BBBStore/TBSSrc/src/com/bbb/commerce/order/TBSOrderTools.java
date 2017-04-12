package com.bbb.commerce.order;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.bbb.autowaiveshippingservice.AutoWaiveShippingInfoResponseOrderLineItemVO;
import com.bbb.autowaiveshippingservice.AutoWaiveShippingInfoResponseOrderVO;
import com.bbb.autowaiveshippingservice.AutoWaiveShippingInfoService;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.pricing.BBBPricingManager;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.commerce.vo.OrderVO;
import com.bbb.commerce.vo.ShipVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSARepository;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.ObjectCreationException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.states.StateDefinitions;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import oracle.jdbc.OracleTypes;

public class TBSOrderTools extends BBBOrderTools {
	
	private String mTbsItemInfoClass;
	
	private BBBPricingManager pricingManager;
	
	private AutoWaiveShippingInfoService autowaiveService;
	
	private TBSSearchStoreManager searchStoreManager;
	
	private BBBCatalogTools catalogToolsImpl;
	
	private boolean mTestAutowaiveMockService;
	
	private int mDefaultResultSetSize;
	
	public int getDefaultResultSetSize() {
		return mDefaultResultSetSize;
	}

	public void setDefaultResultSetSize(int pDefaultResultSetSize) {
		mDefaultResultSetSize = pDefaultResultSetSize;
	}
	
	public BBBCatalogTools getCatalogToolsImpl() {
		return catalogToolsImpl;
	}

	public void setCatalogToolsImpl(BBBCatalogTools catalogToolsImpl) {
		this.catalogToolsImpl = catalogToolsImpl;
	}

	public boolean isTestAutowaiveMockService(){
		return mTestAutowaiveMockService;
	}
	
	public void setTestAutowaiveMockService(boolean pTestAutowaiveMockService){
		this.mTestAutowaiveMockService=pTestAutowaiveMockService;
	}
	
	
	/**
	 * @return the tbsItemInfoClass
	 */
	public String getTbsItemInfoClass() {
		return mTbsItemInfoClass;
	}

	/**
	 * @param pTbsItemInfoClass the tbsItemInfoClass to set
	 */
	public void setTbsItemInfoClass(String pTbsItemInfoClass) {
		mTbsItemInfoClass = pTbsItemInfoClass;
	}
	

	private String mTbsShippingInfoClass;

	/**
	 * @return the tbsShippingInfoClass
	 */
	public String getTbsShippingInfoClass() {
		return mTbsShippingInfoClass;
	}

	/**
	 * @param pTbsShippingInfoClass the tbsShippingInfoClass to set
	 */
	public void setTbsShippingInfoClass(String pTbsShippingInfoClass) {
		mTbsShippingInfoClass = pTbsShippingInfoClass;
	}
	
	/**
	 * @return the pricingManager
	 */
	public BBBPricingManager getPricingManager() {
		return pricingManager;
	}

	/**
	 * @param pricingManager the pricingManager to set
	 */
	public void setPricingManager(BBBPricingManager pricingManager) {
		this.pricingManager = pricingManager;
	}

	/**
	 * @return the autowaiveService
	 */
	public AutoWaiveShippingInfoService getAutowaiveService() {
		return autowaiveService;
	}

	/**
	 * @param autowaiveService the autowaiveService to set
	 */
	public void setAutowaiveService(AutoWaiveShippingInfoService autowaiveService) {
		this.autowaiveService = autowaiveService;
	}

	
	/**
	 * @return the searchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	/**
	 * @param searchStoreManager the searchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}

	/**
	 * This method is used to create the TBSItemInfo based on the Kirsch & CMO details.
	 * @return
	 * @throws CommerceException
	 */
	public TBSItemInfo createTBSItemInfo() throws CommerceException {

		vlogDebug("TBSOrderTools :: createTBSItemInfo() :: START " );
		String itemClassName = getTbsItemInfoClass();
		if (itemClassName == null) {
			vlogError("InvalidTBSInfoType ::");
		}
		TBSItemInfo item = null;
		try {
			item = (TBSItemInfo) Class.forName(itemClassName).newInstance();
			if (item instanceof ChangedProperties)
				((ChangedProperties) item).setSaveAllProperties(true);
		} catch (ClassNotFoundException e) {
			vlogError("UnableToCreateTBSItemInfoObject ::",e);
		} catch (InstantiationException e) {
			vlogError("UnableToCreateTBSItemInfoObject ::",e);
		} catch (IllegalAccessException e) {
			vlogError("UnableToCreateTBSItemInfoObject ::",e);
		}

		try {
			MutableRepository mutRep = (MutableRepository) getOrderRepository();
			String descName = getMappedItemDescriptorName(itemClassName);
			MutableRepositoryItem mutItem = mutRep.createItem(descName);
			DynamicBeans
					.setPropertyValue(item, "id", mutItem.getRepositoryId());
			if (item instanceof ChangedProperties)
				((ChangedProperties) item).setRepositoryItem(mutItem);
		} catch (RepositoryException e) {
			throw new ObjectCreationException(e);
		} catch (PropertyNotFoundException e) {
			throw new ObjectCreationException(e);
		}
		vlogDebug("TBSOrderTools :: createTBSItemInfo() :: END " );
		return item;
	}

	/**
	 * This method is used to create the TBSItemInfo based on the Kirsch & CMO details.
	 * @return
	 * @throws CommerceException
	 */
	public TBSShippingInfo createTBSShippingInfo() throws CommerceException {

		vlogDebug("TBSOrderTools :: createTBSShippingInfo() :: START " );
		String itemClassName = getTbsShippingInfoClass();
		if (itemClassName == null) {
			vlogError("InvalidTBSShippingInfoType ::");
		}

		TBSShippingInfo item = null;
		try {
			item = (TBSShippingInfo) Class.forName(itemClassName).newInstance();
			if (item instanceof ChangedProperties)
				((ChangedProperties) item).setSaveAllProperties(true);
		} catch (ClassNotFoundException e) {
			vlogError("UnableToCreateTBSShippingInfoObject ::",e);
		} catch (InstantiationException e) {
			vlogError("UnableToCreateTBSShippingInfoObject ::",e);
		} catch (IllegalAccessException e) {
			vlogError("UnableToCreateTBSShippingInfoObject ::",e);
		}

		try {
			MutableRepository mutRep = (MutableRepository) getOrderRepository();
			String descName = getMappedItemDescriptorName(itemClassName);
			MutableRepositoryItem mutItem = mutRep.createItem(descName);
			DynamicBeans
					.setPropertyValue(item, "id", mutItem.getRepositoryId());
			if (item instanceof ChangedProperties)
				((ChangedProperties) item).setRepositoryItem(mutItem);
		} catch (RepositoryException e) {
			throw new ObjectCreationException(e);
		} catch (PropertyNotFoundException e) {
			throw new ObjectCreationException(e);
		}
		vlogDebug("TBSOrderTools :: createTBSShippingInfo() :: END " );
		return item;
	}

	/**
	 * This method is used to get the order id using the onlineOrder property of the order.
	 * @param pSearchTerm
	 * @return
	 * @throws RepositoryException
	 */
	public String orderSearch(String pSearchTerm) throws RepositoryException {
		vlogDebug("TBSOrderTools :: orderSearch() :: START " );
		
		String orderId = null;
		RepositoryItem[] orders = null;
		String siteId =  SiteContextManager.getCurrentSiteId();
		String ecomSiteId = null;
		if(siteId.contains(TBSConstants.BED_BATH_US)){
			ecomSiteId = TBSConstants.BED_BATH_US;
        } else if(siteId.contains(TBSConstants.BED_BATH_CA)){
        	ecomSiteId = TBSConstants.BED_BATH_CA;
        } else if(siteId.contains(TBSConstants.BUY_BUY_BABY)){
        	ecomSiteId = TBSConstants.BUY_BUY_BABY;
        }
		
		TBSOrderPropertyManager propetyManager = (TBSOrderPropertyManager)getPropertyManager();
		if (BBBUtility.isNotEmpty(pSearchTerm))
			pSearchTerm = pSearchTerm.toUpperCase();
		if(!StringUtils.isBlank(pSearchTerm) && pSearchTerm.startsWith("OLP")) {
			
			orders = this.getBopusOrders(pSearchTerm,siteId,ecomSiteId, getOrderRepository());

		    // searching orders in archive repository
		    if(BBBUtility.isArrayEmpty(orders)) {
		    	vlogDebug("Searching order in archive repository");
		    	orders = this.getBopusOrders(pSearchTerm,siteId,ecomSiteId,getArchiveOrderRepository());
		    }
			
		} else if(!StringUtils.isBlank(pSearchTerm)){
			orders = getOnlineOrders(pSearchTerm, getOrderRepository());
			
			// searching orders in archive repository
		    if(BBBUtility.isArrayEmpty(orders)) {
		    	vlogDebug("Searching order in archive repository");
		    	orders = getOnlineOrders(pSearchTerm, getArchiveOrderRepository());
		    }
		}
		if(orders != null && orders.length > TBSConstants.ZERO ){
			orderId = orders[TBSConstants.ZERO].getRepositoryId();
		}
		
		
		vlogDebug("TBSOrderTools :: orderSearch() :: END " );
		return orderId;
	}

		/**
	 * This method is used search the order based on either emailId or order number
	 * @param pOrderId
	 * @param pEmailId
	 * @return
	 * @throws RepositoryException
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	
	public List<OrderVO> minimalOrderSearch(String pOrderId, String pEmailId, int numResultSetSize){
		vlogDebug("TBSOrderTools :: minimalOrderSearch() :: START " );
		vlogDebug(" Searching for orders using orderId: "+pOrderId +" Email Id : "+pEmailId);

		Connection con = null;
		CallableStatement cs = null;
		ResultSet resultSet = null;
		List<OrderVO> resultedOrders = new ArrayList<OrderVO>();
		DateFormat dateFormat = null;
		String siteId = SiteContextManager.getCurrentSiteId();
		String ecomSiteId = null;
		
		int fetchHistoryFlag = getFetchHistoryFlag();
		vlogDebug("fetchHistoryFlag: "+ fetchHistoryFlag);
		
		if(siteId.contains(TBSConstants.BED_BATH_US)){
			ecomSiteId = TBSConstants.BED_BATH_US;
        } else if(siteId.contains(TBSConstants.BED_BATH_CA)){
        	ecomSiteId = TBSConstants.BED_BATH_CA;
        } else if(siteId.contains(TBSConstants.BUY_BUY_BABY)){
        	ecomSiteId = TBSConstants.BUY_BUY_BABY;
        }
		
		if (TBSConstants.SITE_TBS_BAB_US.equalsIgnoreCase(siteId) || 
				TBSConstants.SITE_TBS_BBB.equalsIgnoreCase(siteId)) {
			dateFormat = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
		} else if (TBSConstants.SITE_TBS_BAB_CA.equalsIgnoreCase(siteId)) {
			dateFormat = new SimpleDateFormat(BBBCoreConstants.CA_DATE_FORMAT);
		}
		try {
			con = ((GSARepository) getOrderRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				// prepare the callable statement
				cs = con.prepareCall(TBSConstants.FETCH_ORDER_TRACKING);
				cs.setFetchSize(100);
				// set input parameters ...
			
			if (BBBUtility.isNotEmpty(pOrderId)){
				pOrderId = pOrderId.toUpperCase();
			}
			//we will populate the relevant callable statements arguments in code below, rest to remain null.
			cs.setObject(1, null);
			cs.setObject(2, null);
			cs.setObject(3, null);
			cs.setObject(4, null);
			cs.setObject(5, null);
			cs.setObject(6, null);
			cs.setObject(7, null);
			cs.setObject(8, null);
			cs.setObject(9, null);
			cs.setString(10, siteId);
			cs.setString(11, ecomSiteId);
			cs.setObject(12, null); 
			cs.setObject(13, null);
			
			//Set BOPUS_ORDER_NUMBER if Order number provided is of BOPUS type.
			//Else populate arguments for Email or Order Number if not null.
			if(!StringUtils.isBlank(pOrderId) && pOrderId.startsWith("OLP")){
				cs.setString(2, pOrderId);
			}else if(!StringUtils.isBlank(pOrderId) && StringUtils.isBlank(pEmailId)){
				cs.setString(1, pOrderId);
			} else if(StringUtils.isBlank(pOrderId) && !StringUtils.isBlank(pEmailId)){
				cs.setString(3, pEmailId);
			} else if(!StringUtils.isBlank(pOrderId) && !StringUtils.isBlank(pEmailId)){
				cs.setString(1, pOrderId);
				cs.setString(3, pEmailId);
			}
			cs.setInt(12, numResultSetSize);
			cs.setInt(13, fetchHistoryFlag);
			cs.registerOutParameter(14, OracleTypes.CURSOR);
			// execute stored proc
			cs.execute();
			resultSet = (ResultSet) cs.getObject(14);
			
			while(resultSet.next())
			{
				OrderVO order = new OrderVO();
				order.setOnlineOrderNumber(resultSet.getString(1));
				order.setOrderNumber(resultSet.getString(2));
				
				if(resultSet.getDate(3) != null) {
					order.setSubmittedDate(dateFormat.format(resultSet.getDate(3)));
				}
				order.setFirstName(resultSet.getString(4));
				order.setLastName(resultSet.getString(5));
				order.setEmailId(resultSet.getString(6));
				order.setOrderAmount(resultSet.getDouble(7));
				order.setOrderStatus(resultSet.getString(8));
				order.setStore(resultSet.getString(9));
				order.setAssociate(resultSet.getString(10));
					
				resultedOrders.add(order);
			}
			vlogDebug("Total number of orders found: ["+resultedOrders.size()+"]");
		}
		} catch (Exception ex) {
			vlogError("Error occurred while fetching order in Order Enquiry:", ex);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (cs != null) {
					cs.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				vlogError("Error occurred while closing connection", e);
			}
		}
		vlogDebug("TBSOrderTools :: minimalOrderSearch() :: END " );
		return resultedOrders;
	}

	/**
	 * This method is used search the order based on either emailId or order number
	 * @param pOrderId
	 * @param pEmailId
	 * @return
	 * @throws RepositoryException
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public List<RepositoryItem> orderSearch(String pOrderId, String pEmailId) throws RepositoryException, BBBSystemException, BBBBusinessException {
		
		vlogDebug("TBSOrderTools :: orderSearch() :: START " );
		TBSOrderPropertyManager propetyManager = (TBSOrderPropertyManager)getPropertyManager();
		RepositoryItemDescriptor orderItemDescriptor = getOrderRepository().getItemDescriptor(propetyManager.getOrderName());
		RepositoryView orderView = orderItemDescriptor.getRepositoryView();
		RqlStatement statement = null;
		RepositoryItem [] orders = null;
		String siteId =  SiteContextManager.getCurrentSiteId();
		String ecomSiteId = null;
		if(siteId.contains(TBSConstants.BED_BATH_US)){
			ecomSiteId = TBSConstants.BED_BATH_US;
        } else if(siteId.contains(TBSConstants.BED_BATH_CA)){
        	ecomSiteId = TBSConstants.BED_BATH_CA;
        } else if(siteId.contains(TBSConstants.BUY_BUY_BABY)){
        	ecomSiteId = TBSConstants.BUY_BUY_BABY;
        }
		
		int numResultSetSize = getDefaultResultSetSize();
		 
		 List<String> resultSetSizeValueFromConfigKey = null;
		 
		 resultSetSizeValueFromConfigKey = getCatalogToolsImpl().getAllValuesForKey("AdvancedOrderInquiryKeys", "ResultSetSize");
		 				 
		 if (null != resultSetSizeValueFromConfigKey
					&& null != resultSetSizeValueFromConfigKey.get(TBSConstants.ZERO)){
			 numResultSetSize = Integer.parseInt(resultSetSizeValueFromConfigKey.get(TBSConstants.ZERO));
		 }
		
		vlogDebug(" Searching for orders using orderId: "+pOrderId +" Email Id : "+pEmailId);
		if (BBBUtility.isNotEmpty(pOrderId))
			pOrderId = pOrderId.toUpperCase();
		if(!StringUtils.isBlank(pOrderId) && pOrderId.startsWith("OLP")) {
			
			orders = this.getBopusOrders(pOrderId,siteId, ecomSiteId, getOrderRepository());
			

		} else if(!StringUtils.isBlank(pOrderId) && StringUtils.isBlank(pEmailId)){
			statement = RqlStatement.parseRqlStatement(" onlineOrderNumber=?0 AND state !=?1 AND (siteId = ?2 or siteId = ?3) order by submittedDate DESC RANGE +?4");
			Object params[] = new Object[5];
			params[0] = pOrderId;
			params[1] = TBSConstants.ORDER_INCOMPLETE;
			params[2] = siteId;
			params[3] = ecomSiteId;
			params[4] = numResultSetSize;
			orders =statement.executeQuery (orderView, params);
		} else if(StringUtils.isBlank(pOrderId) && !StringUtils.isBlank(pEmailId)){
			statement = RqlStatement.parseRqlStatement("emailAddress  EQUALS ?0 AND state !=?1 AND (siteId = ?2 or siteId = ?3) ORDER BY submittedDate DESC RANGE +?4" );
			Object params[] = new Object[5];
			params[0] = pEmailId.toLowerCase();
			params[1] = TBSConstants.ORDER_INCOMPLETE;
			params[2] = siteId;
			params[3] = ecomSiteId;
			params[4] = numResultSetSize;
			orders =statement.executeQuery (orderView, params);
		} else if(!StringUtils.isBlank(pOrderId) && !StringUtils.isBlank(pEmailId)){
			//Modifying the RQL query to fix memory leak issue in TBS Production
			//OLD RQL QUERY
			//statement = RqlStatement.parseRqlStatement("(onlineOrderNumber =?0 OR billingAddress.email  EQUALS IGNORECASE ?1) AND state !=?2 AND siteId CONTAINS ?3" );
			//NEW RQL QUERY
			statement = RqlStatement.parseRqlStatement("(state !=?2 AND (siteId = ?3 or siteId = ?4) AND emailAddress =?1) OR (onlineOrderNumber =?0 AND state !=?2 AND (siteId = ?3 or siteId = ?4)) ORDER BY submittedDate DESC RANGE +?5");
			Object params[] = new Object[6];
			params[0] = pOrderId;
			params[1] = pEmailId.toLowerCase();
			params[2] = TBSConstants.ORDER_INCOMPLETE;
			params[3] = siteId;
			params[4] = ecomSiteId;
			params[5] = numResultSetSize;
			orders =statement.executeQuery (orderView, params);
		}
		if(orders != null){
			List<RepositoryItem> orderIdorderLists = new ArrayList<RepositoryItem>(Arrays.asList(orders));
			vlogDebug("Total Resulted Orders " + orderIdorderLists);
			vlogDebug("TBSOrderTools :: orderSearch() :: END " );
			return orderIdorderLists;
		} 
		vlogDebug("TBSOrderTools :: orderSearch() :: END " );
		return null;
	}

    public RepositoryItem[] getBopusOrders(String bopusOrderNumber,String siteId, String ecomSiteId, Repository orderRepository) throws RepositoryException 
    {
       vlogDebug("Fetching Order using Bopus Order Number :{0} site Id :{1} ",bopusOrderNumber,siteId);
       RqlStatement statement = RqlStatement.parseRqlStatement("bopusOrderNumber=?0 AND state !=?1 AND (siteId = ?2 or siteId = ?3)");
       vlogDebug("Execuate Rql query for Bopus Orders {0}",statement.toString());
       final RepositoryView view = orderRepository.getView("order");
       Object[] params = new Object[4];
       params[0] = bopusOrderNumber;
       params[1] = TBSConstants.ORDER_INCOMPLETE;;
       params[2] = siteId;
       params[3] = ecomSiteId;
       RepositoryItem[] items = statement.executeQuery(view, params);
       return items;
    }	
	/**
	 * This is used to search the order based on the below conditions.
	 * �	Any of the following combinations are required for the search : 
	 *	o	Date range with at least one of the other attributes (name or registry id)
	 *	o	Registry number (order containing items associated to this registry Id shall be displayed)
	 *	o	Store Id with at least one of the other attribute (name or registry id)
	 *
	 * @param pFirstName
	 * @param pLastName
	 * @param pStartDate
	 * @param pEndDate
	 * @param pRegistryId
	 * @param pStoreId
	 * @return
	 * @throws RepositoryException 
	 * @throws ParseException 
	 */
	public List<RepositoryItem> orderSearch(String pFirstName, String pLastName,
			String pStartDate, String pEndDate, String pRegistryId, String pStoreId,boolean archivedOrder) throws RepositoryException, ParseException {
		
		vlogDebug("TBSOrderTools :: orderSearch() :: START archived order {0} ",archivedOrder );
		String siteId =  SiteContextManager.getCurrentSiteId();
		if(siteId.contains(TBSConstants.BED_BATH_US)){
    	   siteId = TBSConstants.BED_BATH_US;
        } else if(siteId.contains(TBSConstants.BED_BATH_CA)){
    	   siteId = TBSConstants.BED_BATH_CA;
        } else if(siteId.contains(TBSConstants.BUY_BUY_BABY)){
    	   siteId = TBSConstants.BUY_BUY_BABY;
        }
		vlogDebug(" Searching for orders using FirstName: "+pFirstName +" LastName : "+pLastName +" StartDate : "+pStartDate +
				" EndDate : "+pEndDate +" RegistryId : "+pRegistryId +" StoreId : "+pStoreId +"Site Id "+siteId);
		
		TBSOrderPropertyManager propetyManager = (TBSOrderPropertyManager)getPropertyManager();
		RepositoryItemDescriptor orderItemDescriptor = getOrderRepository().getItemDescriptor(propetyManager.getOrderName());
		

		if(archivedOrder) {
			orderItemDescriptor = getArchiveOrderRepository().getItemDescriptor(propetyManager.getOrderName());
		}
		
		RepositoryView orderView = orderItemDescriptor.getRepositoryView();
		RqlStatement statement = null;
		RepositoryItem [] orders = null;
		Timestamp startDate = null;
		Timestamp endDate = null;
		if(!StringUtils.isBlank(pStartDate)){
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			Date sDate = dateFormat.parse(pStartDate);
			startDate = new Timestamp(sDate.getTime());
			//startDate = new java.sql.Date(sDate.getTime());
			Date eDate = dateFormat.parse(pEndDate);
			//endDate = new java.sql.Date(eDate.getTime());

			// Increment endDate by 1 to account for the entire day
			Calendar c = Calendar.getInstance(); 
			c.setTime(eDate); 
			c.add(Calendar.DATE, 1);
			eDate = c.getTime();
			endDate = new Timestamp(eDate.getTime());
		}
		
		//submitted date with all combination
		if(startDate != null && endDate!=null && !StringUtils.isBlank(pFirstName) && !StringUtils.isBlank(pLastName) && !StringUtils.isBlank(pRegistryId) && !StringUtils.isBlank(pStoreId)){
			vlogDebug("getting orders based on date and all combination");
			statement = RqlStatement.parseRqlStatement(
					"billingAddress.firstname STARTS WITH IGNORECASE ?0 AND billingAddress.lastname STARTS WITH IGNORECASE ?1 AND submittedDate >=?2 AND submittedDate <= ?3 AND commerceItems INCLUDES ITEM (registryId=?4) AND tbsStoreNo=?5" );
			Object params[] = new Object[6];
			params[0] = pFirstName;
			params[1] = pLastName;
			params[2] = startDate;
			params[3] = endDate;
			params[4] = pRegistryId;
			params[5] = pStoreId;
			orders =statement.executeQuery (orderView, params);
		} else //submitted date with except lastname
		if(startDate != null && endDate!=null && !StringUtils.isBlank(pFirstName) && !StringUtils.isBlank(pRegistryId) && !StringUtils.isBlank(pStoreId)){
			vlogDebug("getting orders based on date except lastname");
			statement = RqlStatement.parseRqlStatement(
					"billingAddress.firstname STARTS WITH IGNORECASE ?0 AND submittedDate >=?1 AND submittedDate <= ?2 AND commerceItems INCLUDES ITEM (registryId=?3) AND tbsStoreNo=?4" );
			Object params[] = new Object[5];
			params[0] = pFirstName;
			params[1] = startDate;
			params[2] = endDate;
			params[3] = pRegistryId;
			params[4] = pStoreId;
			orders =statement.executeQuery (orderView, params);
		} else //submitted date with except first name
			if(startDate != null && endDate!=null && !StringUtils.isBlank(pLastName) && !StringUtils.isBlank(pRegistryId) && !StringUtils.isBlank(pStoreId)){
				vlogDebug("getting orders based on date except lastname");
				statement = RqlStatement.parseRqlStatement(
						"billingAddress.lastname STARTS WITH IGNORECASE ?0 AND submittedDate >=?1 AND submittedDate <= ?2 AND commerceItems INCLUDES ITEM (registryId=?3) AND tbsStoreNo=?4" );
				Object params[] = new Object[5];
				params[0] = pLastName;
				params[1] = startDate;
				params[2] = endDate;
				params[3] = pRegistryId;
				params[4] = pStoreId;
				orders =statement.executeQuery (orderView, params);
			}
		if(orders != null){
			List<RepositoryItem> orderIdorderLists = new ArrayList<RepositoryItem>(Arrays.asList(orders));
			vlogDebug("Total Resulted Orders " + orderIdorderLists);
			vlogDebug("TBSOrderTools :: orderSearch() :: END " );
			return orderIdorderLists;
		}
		//submitted date with name combination
		if(!StringUtils.isBlank(pFirstName) && !StringUtils.isBlank(pLastName) && startDate != null && endDate!=null && StringUtils.isBlank(pRegistryId) && StringUtils.isBlank(pStoreId)){
			vlogDebug("getting orders based on date and customer first name and last name combination");
			statement = RqlStatement.parseRqlStatement("billingAddress.firstname STARTS WITH IGNORECASE ?0 AND billingAddress.lastname STARTS WITH IGNORECASE ?1 AND submittedDate >=?2 AND submittedDate <= ?3 AND siteId CONTAINS ?4" );
			Object params[] = new Object[5];
			params[0] = pFirstName;
			params[1] = pLastName;
			params[2] = startDate;
			params[3] = endDate;
			params[4] = siteId;
			orders =statement.executeQuery (orderView, params);
		} else //submitted date with First name combination
			if(!StringUtils.isBlank(pFirstName) && startDate != null && endDate!=null && StringUtils.isBlank(pRegistryId) && StringUtils.isBlank(pStoreId)){
				vlogDebug("getting orders based on date and customer first name combination");
				statement = RqlStatement.parseRqlStatement("billingAddress.firstname STARTS WITH IGNORECASE ?0 AND submittedDate >=?1 AND submittedDate <= ?2 AND siteId CONTAINS ?3" );
				Object params[] = new Object[4];
				params[0] = pFirstName;
				params[1] = startDate;
				params[2] = endDate;
				params[3] = siteId;
				orders =statement.executeQuery (orderView, params);
			} else //submitted date with Last name combination
		if(!StringUtils.isBlank(pLastName) && startDate != null && endDate!=null && StringUtils.isBlank(pRegistryId) && StringUtils.isBlank(pStoreId)){
			vlogDebug("getting orders based on date and customer last name combination");
			statement = RqlStatement.parseRqlStatement("billingAddress.lastname STARTS WITH IGNORECASE ?0 AND submittedDate >=?1 AND submittedDate <= ?2 AND siteId CONTAINS ?3" );
			Object params[] = new Object[4];
			params[0] = pLastName;
			params[1] = startDate;
			params[2] = endDate;
			params[3] = siteId;
			orders =statement.executeQuery (orderView, params);
		}
		if(orders != null){
			List<RepositoryItem> orderIdorderLists = new ArrayList<RepositoryItem>(Arrays.asList(orders));
			vlogDebug("Total Resulted Orders " + orderIdorderLists);
			vlogDebug("TBSOrderTools :: orderSearch() :: END " );
			return orderIdorderLists;
		} 
		
		//StoreId with name combination
		if(!StringUtils.isBlank(pStoreId) && !StringUtils.isBlank(pFirstName) && !StringUtils.isBlank(pLastName) && StringUtils.isBlank(pRegistryId) && startDate == null){
			vlogDebug("getting orders based on StoreId and pFirstName combination");
			statement = RqlStatement.parseRqlStatement("tbsStoreNo=?0 AND billingAddress.firstname STARTS WITH IGNORECASE ?1 AND billingAddress.lastname STARTS WITH IGNORECASE ?2 AND NOT submittedDate IS NULL" );
			Object params[] = new Object[3];
			params[0] = pStoreId;
			params[1] = pFirstName;
			params[2] = pLastName;
			orders =statement.executeQuery (orderView, params);
		} else //StoreId with fistname combination
		if(!StringUtils.isBlank(pStoreId) && !StringUtils.isBlank(pFirstName) && StringUtils.isBlank(pRegistryId) && startDate == null){
			vlogDebug("getting orders based on StoreId and pFirstName combination");
			statement = RqlStatement.parseRqlStatement("tbsStoreNo=?0 AND billingAddress.firstname STARTS WITH IGNORECASE ?1 AND NOT submittedDate IS NULL" );
			Object params[] = new Object[2];
			params[0] = pStoreId;
			params[1] = pFirstName;
			orders =statement.executeQuery (orderView, params);
		} else //StoreId with fistname combination
		if(!StringUtils.isBlank(pStoreId) && !StringUtils.isBlank(pLastName) && StringUtils.isBlank(pRegistryId) && startDate == null){
			vlogDebug("getting orders based on StoreId and pLastName combination");
			statement = RqlStatement.parseRqlStatement("tbsStoreNo=?0 AND billingAddress.lastname STARTS WITH IGNORECASE ?1 AND NOT submittedDate IS NULL" );
			Object params[] = new Object[2];
			params[0] = pStoreId;
			params[1] = pLastName;
			orders =statement.executeQuery (orderView, params);
		} 
		if(orders != null){
			List<RepositoryItem> orderIdorderLists = new ArrayList<RepositoryItem>(Arrays.asList(orders));
			vlogDebug("Total Resulted Orders " + orderIdorderLists);
			vlogDebug("TBSOrderTools :: orderSearch() :: END " );
			return orderIdorderLists;
		} 
		
		//submitted date with registryId combination
		if(!StringUtils.isBlank(pRegistryId) && startDate != null && endDate!=null && StringUtils.isBlank(pStoreId)){
			vlogDebug("getting orders based on date and registryid combination");
			statement = RqlStatement.parseRqlStatement("submittedDate >=?0 AND submittedDate <= ?1 AND commerceItems INCLUDES ITEM (registryId=?2)" );
			Object params[] = new Object[3];
			params[0] = startDate;
			params[1] = endDate;
			params[2] = pRegistryId;
			orders =statement.executeQuery (orderView, params);
		} 
		if(orders != null){
			List<RepositoryItem> orderIdorderLists = new ArrayList<RepositoryItem>(Arrays.asList(orders));
			vlogDebug("Total Resulted Orders " + orderIdorderLists);
			vlogDebug("TBSOrderTools :: orderSearch() :: END " );
			return orderIdorderLists;
		} 
		
		//only registryId combination
		if(!StringUtils.isBlank(pRegistryId) && StringUtils.isBlank(pStoreId)){
			vlogDebug("getting orders based on registryId combination");
			statement = RqlStatement.parseRqlStatement("commerceItems INCLUDES ITEM (registryId=?0) AND NOT submittedDate IS NULL" );
			Object params[] = new Object[1];
			params[0] = pRegistryId;
			orders =statement.executeQuery (orderView, params);
		} else //StoreId with registryId combination
		if(!StringUtils.isBlank(pStoreId) && !StringUtils.isBlank(pRegistryId)){
			vlogDebug("getting orders based on StoreId and pRegistryId combination");
			statement = RqlStatement.parseRqlStatement("tbsStoreNo=?0 AND commerceItems INCLUDES ITEM (registryId=?1) AND NOT submittedDate IS NULL" );
			Object params[] = new Object[2];
			params[0] = pStoreId;
			params[1] = pRegistryId;
			orders =statement.executeQuery (orderView, params);
		} 
		if(orders != null){
			List<RepositoryItem> orderIdorderLists = new ArrayList<RepositoryItem>(Arrays.asList(orders));
			vlogDebug("Total Resulted Orders " + orderIdorderLists);
			vlogDebug("TBSOrderTools :: orderSearch() :: END " );
			return orderIdorderLists;
		} 
		
		//submitted date with pRegistryId and pStoreId
		if(startDate != null && endDate!=null && !StringUtils.isBlank(pRegistryId) && !StringUtils.isBlank(pStoreId)){
			vlogDebug("getting orders based on date pRegistryId and pStoreId");
			statement = RqlStatement.parseRqlStatement(
					"submittedDate >=?0 AND submittedDate <= ?1 AND commerceItems INCLUDES ITEM (registryId=?2) AND tbsStoreNo=?3" );
			Object params[] = new Object[4];
			params[0] = startDate;
			params[1] = endDate;
			params[2] = pRegistryId;
			params[3] = pStoreId;
			orders =statement.executeQuery (orderView, params);
		} else //submitted date with pStoreId
		if(startDate != null && endDate!=null && !StringUtils.isBlank(pStoreId) && StringUtils.isBlank(pFirstName) && StringUtils.isBlank(pLastName)){
			vlogDebug("getting orders based on date and store id");
			statement = RqlStatement.parseRqlStatement(
					"submittedDate >=?0 AND submittedDate <= ?1 AND tbsStoreNo=?2" );
			Object params[] = new Object[3];
			params[0] = startDate;
			params[1] = endDate;
			params[2] = pStoreId;
			orders =statement.executeQuery (orderView, params);
		} else //date, storeId, firstName and lastName 
			if(startDate != null && endDate!=null && !StringUtils.isBlank(pStoreId) && !StringUtils.isBlank(pFirstName) && !StringUtils.isBlank(pLastName)){
			vlogDebug("getting orders based on date and store id");
			statement = RqlStatement.parseRqlStatement(
					"submittedDate >=?0 AND submittedDate <= ?1 AND tbsStoreNo=?2 AND billingAddress.firstname STARTS WITH IGNORECASE ?3 AND billingAddress.lastname STARTS WITH IGNORECASE ?4" );
			Object params[] = new Object[5];
			params[0] = startDate;
			params[1] = endDate;
			params[2] = pStoreId;
			params[3] = pFirstName;
			params[4] = pLastName;
			orders =statement.executeQuery (orderView, params);
		} else //date, storeId and firstName
			if(startDate != null && endDate!=null && !StringUtils.isBlank(pStoreId) && !StringUtils.isBlank(pFirstName)){
			vlogDebug("getting orders based on date and store id");
			statement = RqlStatement.parseRqlStatement(
					"submittedDate >=?0 AND submittedDate <= ?1 AND tbsStoreNo=?2 AND billingAddress.firstname STARTS WITH IGNORECASE ?3" );
			Object params[] = new Object[5];
			params[0] = startDate;
			params[1] = endDate;
			params[2] = pStoreId;
			params[3] = pFirstName;
			orders =statement.executeQuery (orderView, params);
		} else //date, storeId and lastName
			if(startDate != null && endDate!=null && !StringUtils.isBlank(pStoreId) && !StringUtils.isBlank(pLastName)){
			vlogDebug("getting orders based on date and store id");
			statement = RqlStatement.parseRqlStatement(
					"submittedDate >=?0 AND submittedDate <= ?1 AND tbsStoreNo=?2 AND billingAddress.lastname STARTS WITH IGNORECASE ?3" );
			Object params[] = new Object[5];
			params[0] = startDate;
			params[1] = endDate;
			params[2] = pStoreId;
			params[3] = pLastName;
			orders =statement.executeQuery (orderView, params);
		} 
		if(orders != null){
			List<RepositoryItem> orderIdorderLists = new ArrayList<RepositoryItem>(Arrays.asList(orders));
			vlogDebug("Total Resulted Orders " + orderIdorderLists);
			vlogDebug("TBSOrderTools :: orderSearch() :: END " );
			return orderIdorderLists;
		} 
		return null;
	}
	/**
	 * This method determines the range of OrderVO's to be generated from total number of orders returned by the query, 
	 * which will be returned and stored in cache for purpose of displaying the same to the user.
	 * 
	 * @param pOrders
	 * @param orderVOList
	 * @param numberOfOrdersToProcess
	 * @return
	 * @throws CommerceException
	 */
	public List<OrderVO> partialProcessOrders(List<RepositoryItem> pOrders, List<OrderVO> orderVOList, int numberOfOrdersToProcess) throws CommerceException {
		List<RepositoryItem> ordersToProcess = new ArrayList();
			for(int orderToProcess=orderVOList.size();orderToProcess<numberOfOrdersToProcess && pOrders.size()>orderToProcess;orderToProcess++){
				ordersToProcess.add(pOrders.get(orderToProcess));
			}
		return processOrderVO(ordersToProcess,false);
	}
	
	public List<OrderVO> processOrderVO(List<RepositoryItem> pOrders,boolean archivedOrder) throws CommerceException {
		vlogDebug("Enter TBSOrderTools.processOrderVO(List<RepositoryItem>, boolean) archivedOrder is {0}", archivedOrder);
		OrderVO ordervo = null;
		BBBOrderImpl order = null;
		String orderId;
		String submittedDate = null;
		DateFormat dateFormat = null;
		List<OrderVO> resultedOrders = new ArrayList<OrderVO>();
		String onlineOrder = null;
		String siteId = SiteContextManager.getCurrentSiteId();
		if (TBSConstants.SITE_TBS_BAB_US.equalsIgnoreCase(siteId) || 
				TBSConstants.SITE_TBS_BBB.equalsIgnoreCase(siteId)) {
			dateFormat = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
		} else if (TBSConstants.SITE_TBS_BAB_CA.equalsIgnoreCase(siteId)) {
			dateFormat = new SimpleDateFormat(BBBCoreConstants.CA_DATE_FORMAT);
		}
		for (RepositoryItem repositoryItem : pOrders) {
			double orderAmount = 0;
			Map<String, ShipVO> shipMap = new HashMap<String, ShipVO>();
			orderId = repositoryItem.getRepositoryId();

			// load order from archive repository
			if (!archivedOrder) {
				vlogDebug("Loading order from order repository order Id {0}", orderId);
				order = (BBBOrderImpl) getOrderManager().loadOrder(orderId);
			} else {
				vlogDebug("Loading order from archive repository order Id {0}", orderId);
				if(((BBBOrderManager) getOrderManager()).orderExistsInArchive(orderId)) {
				    order = (BBBOrderImpl) ((BBBOrderManager) getOrderManager()).loadArchiveOrder(orderId);
				} else {
					continue;
				}
			}
			
			onlineOrder = order.getOnlineOrderNumber();
			List<ShippingGroup> shippingGroups = order.getShippingGroups();
			for (ShippingGroup shippingGroup : shippingGroups) {
				PriceInfoVO priceInfoVO = getPricingManager().getShippingPriceInfo(shippingGroup, order);
				orderAmount += priceInfoVO.getTotalAmount();
			}
			ordervo = new OrderVO();
			ordervo.setOrderNumber(orderId);
			ordervo.setOrderAmount(orderAmount);

			if(order.getBillingAddress()!=null){
				ordervo.setEmailId(order.getBillingAddress().getEmail());
				ordervo.setFirstName(order.getBillingAddress().getFirstName());
				ordervo.setLastName(order.getBillingAddress().getLastName());
			}
			ordervo.setStore(order.getTbsStoreNo());
			ordervo.setAssociate(order.getTBSAssociateID());
			if(StringUtils.isBlank(onlineOrder)){
				onlineOrder = order.getBopusOrderNumber();
			}
			ordervo.setOnlineOrderNumber(onlineOrder);
			
			if(order.getSubmittedDate() != null) {
				submittedDate = dateFormat.format(order.getSubmittedDate());
				ordervo.setSubmittedDate(submittedDate);
			}
			String orderStatus = order.getStateDetail();
			if(StringUtils.isBlank(orderStatus)){
				orderStatus = StateDefinitions.ORDERSTATES.getStateString(order.getState());
			}
			ordervo.setOrderStatus(orderStatus);
		
			/*List<CommerceItem> citems =  order.getCommerceItems();
			List<Relationship> relations =  order.getRelationships();
			ShippingGroup sg = null;
			List<String> skuIds = null;
			Map<String, String> skuQty= null;
			Map<String, CommerceItem> commItem= null;
			Map<String, String> autoWaiveClassification = null;
			ShipVO shipVo  = null;
			Map<String, Double> shipPrice = new HashMap<String, Double>();
			ShippingGroupCommerceItemRelationship shipRe = null;
			String shipId = null;
			Double itemPrice = null;
			Double existPrice = null;
			for (Relationship relationship : relations) {
				if(relationship instanceof ShippingGroupCommerceItemRelationship){
					shipRe = (ShippingGroupCommerceItemRelationship) relationship;
					if(shipRe!=null){
						shipId = shipRe.getShippingGroup().getId();
						if(shipRe.getCommerceItem()!=null && shipRe.getCommerceItem().getPriceInfo()!=null){
							itemPrice = Double.valueOf(shipRe.getCommerceItem().getPriceInfo().getAmount());
						}
						if(!shipPrice.containsKey(shipId)){
							shipPrice.put(shipId, itemPrice);
						} else {
							existPrice = shipPrice.put(shipId, itemPrice);
							existPrice = Double.valueOf(existPrice.doubleValue() + itemPrice.doubleValue());
							shipPrice.put(shipId, existPrice);
						}
				  }
				}
			}
			for (CommerceItem commerceItem : citems) {
				if(BBBUtility.isListEmpty(commerceItem.getShippingGroupRelationships())) {
					continue;
				}
				sg = ((ShippingGroupRelationship) commerceItem.getShippingGroupRelationships().get(0)).getShippingGroup();
				if(sg.getId() == null) {
					continue;
				}
				if(shipMap.containsKey(sg.getId())){
					shipVo = shipMap.get(sg.getId());
					shipVo.getSkuIds().add(commerceItem.getCatalogRefId());
					shipVo.getSkuQty().put(commerceItem.getCatalogRefId(), Long.toString(commerceItem.getQuantity()));
					shipVo.getCommItem().put(commerceItem.getCatalogRefId(), commerceItem);
					if (commerceItem instanceof TBSCommerceItem) {
						shipVo.getAutoWaiveClassificationList().put(commerceItem.getCatalogRefId(), ((TBSCommerceItem)commerceItem).getAutoWaiveClassification());
					}
				} else {
					shipVo = new ShipVO();
					//shipVo.setShipAmount(sg.getPriceInfo().getAmount());
					shipVo.setShipAmount(shipPrice.get(sg.getId()));
					shipVo.setShipState(sg.getStateDetail());
					skuIds = new ArrayList<String>();
					skuIds.add(commerceItem.getCatalogRefId());
					skuQty = new HashMap<String, String>();
					skuQty.put(commerceItem.getCatalogRefId(), Long.toString(commerceItem.getQuantity()));
					shipVo.setSkuQty(skuQty);
					if (commerceItem instanceof TBSCommerceItem) {
						autoWaiveClassification = new HashMap<String, String>();
						autoWaiveClassification.put(commerceItem.getCatalogRefId(), ((TBSCommerceItem)commerceItem).getAutoWaiveClassification());
						shipVo.setAutoWaiveClassificationList(autoWaiveClassification);
					}
					shipVo.setSkuIds(skuIds);
					commItem = new HashMap<String, CommerceItem>();
					commItem.put(commerceItem.getCatalogRefId(), commerceItem);
					shipVo.setCommItem(commItem);
					shipMap.put(sg.getId(), shipVo);
				}
			}
			ordervo.setShipMap(shipMap);*/
			resultedOrders.add(ordervo);
		}
		return resultedOrders;
	}
	
	@SuppressWarnings("unchecked")
	public void getAutoWaiveShipDetails(Order pOrder,String currentId) {
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		String storeId = storeNumFromCookie(pRequest);
		List<String> blockedStores = getBlockedAutoWaiveStores();
		if(!StringUtils.isBlank(storeId) && blockedStores != null && !blockedStores.isEmpty() && blockedStores.contains(storeId)){
			vlogDebug("Current store "+storeId +" is in blocked Stores "+blockedStores);
			return;
		}
		
		boolean rollback = true;
		TransactionDemarcation td = new TransactionDemarcation();
		BBBOrder bbbOrder = (BBBOrder)pOrder;
		Map<String, Integer> storeInventoryMap = null;
		
		try {
			List<StoreDetails> storeDetails = new ArrayList<StoreDetails>();
			StoreDetails store = new StoreDetails(storeId, null, null, null, null, null, null, null,
				      null, null, null, null, null, null, null, null,null, null, null, null, null, null);
			storeDetails.add(store);
			
			Map<String, BigInteger> onHoldDetails = new HashMap<String, BigInteger>();
			
			BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer) pRequest.resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");
			List<CommerceItem> citems = bbbOrder.getCommerceItems();
			for (CommerceItem commerceItem : citems) {
				if(commerceItem instanceof TBSCommerceItem){
					try {
						storeInventoryMap = getSearchStoreManager().checkProductAvailability(storeDetails, bbbOrder.getSiteId(), commerceItem.getCatalogRefId(),
								null, false, commerceItem.getQuantity(), BBBInventoryManager.STORE_STORE, storeInventoryContainer, pRequest);
						if(storeInventoryMap != null && !storeInventoryMap.isEmpty()){
							onHoldDetails.put(commerceItem.getCatalogRefId(), new BigInteger(storeInventoryMap.get(storeId).toString()));
						}
					} catch (InventoryException e) {
						vlogDebug("InventoryException while getting StoreOnHoldQuantity :: "+e);
					} catch (BBBSystemException e) {
						vlogError("BBBSystemException while getting StoreOnHoldQuantity :: "+e);
					} catch (BBBBusinessException e) {
						vlogError("BBBBusinessException while getting StoreOnHoldQuantity :: "+e);
					}
				}
			}
			ServiceResponseIF response = getAutowaiveService().getAutoWaiveShippingInfo(pOrder, onHoldDetails,currentId);
			td.begin(getTransactionManager());
			
			if(response != null){
				//remove the previous values from order
				removeAutoWaiveDetails(pOrder);
				AutoWaiveShippingInfoResponseOrderVO responseVO = (AutoWaiveShippingInfoResponseOrderVO)response;
				
				if(isTestAutowaiveMockService()){
					//Reset shipping group classification vaules
					Iterator sgIter = bbbOrder.getShippingGroups().iterator();
					while( sgIter.hasNext() ) {
						Object obj = sgIter.next();
						if( obj instanceof BBBHardGoodShippingGroup ) {
							((BBBHardGoodShippingGroup)obj).setAutoWaiveFlag(false);
							((BBBHardGoodShippingGroup)obj).setAutoWaiveClasses("");
						}
					}
					List<AutoWaiveShippingInfoResponseOrderLineItemVO> lineItemVOs = responseVO.getOrderLineItems();
					String orderAutoWaiveFlag=responseVO.getOrderWaiveShipFlag();
					String orderClassification=responseVO.getOrderClassification();
					if(!StringUtils.isEmpty(orderAutoWaiveFlag)){
						logDebug("Order AutowaiveFlag is: "+orderAutoWaiveFlag);
					}
					else {
						logDebug("Order AutowaiveFlag is: empty");
					}
					
					if(!StringUtils.isEmpty(orderClassification)){
						logDebug("Order AutowaiveClassification is: "+orderClassification);
					}
					else {
						logDebug("Order AutowaiveClassification is: empty");
					}
						
					if(!StringUtils.isEmpty(responseVO.getOrderWaiveShipFlag())){
						bbbOrder.setAutoWaiveFlag(responseVO.getOrderWaiveShipFlag().equals("Y"));
					}
					if(!StringUtils.isEmpty(responseVO.getOrderClassification())){
						bbbOrder.setAutoWaiveClassification((responseVO.getOrderClassification()));
					}
					copyLineItemDetails(citems, lineItemVOs);
				}
				
				else {
				if(!StringUtils.isBlank(responseVO.getOrderId()) && responseVO.getOrderId().equals(pOrder.getId())){
					//Reset shipping group classification vaules
					Iterator sgIter = bbbOrder.getShippingGroups().iterator();
					while( sgIter.hasNext() ) {
						Object obj = sgIter.next();
						if( obj instanceof BBBHardGoodShippingGroup ) {
							((BBBHardGoodShippingGroup)obj).setAutoWaiveFlag(false);
							((BBBHardGoodShippingGroup)obj).setAutoWaiveClasses("");
						}
					}
					List<AutoWaiveShippingInfoResponseOrderLineItemVO> lineItemVOs = responseVO.getOrderLineItems();
					if(!StringUtils.isEmpty(responseVO.getOrderWaiveShipFlag())){
						bbbOrder.setAutoWaiveFlag(responseVO.getOrderWaiveShipFlag().equals("Y"));
					}
					if(!StringUtils.isEmpty(responseVO.getOrderClassification())){
						bbbOrder.setAutoWaiveClassification((responseVO.getOrderClassification()));
					}
					copyLineItemDetails(citems, lineItemVOs);
				}
			}
			}
			rollback = false;
		}  catch (TransactionDemarcationException e) {
			vlogError("TransactionDemarcationException occurred :: "+e);
		}
		finally {
    		try {
				td.end(rollback);
			} catch (TransactionDemarcationException tde) {
				vlogError("TransactionDemarcationException "+tde);
			}
    	}
	}
	
	
	public void reInitializeAndCopyLineItemDetails(Order pOrder) {
		BBBOrder bbbOrder = (BBBOrder)pOrder;
		BBBHardGoodShippingGroup hsg = null;
		List<ShippingGroup> shippingGroups = bbbOrder.getShippingGroups();
		TBSCommerceItem tbsItem = null;
		CommerceItem cItem = null;
		
		vlogDebug("---------------reinitializing shipping groups for autowaive" );
		
		vlogDebug("---------------set Shipping groups for autowaive" );
		for (ShippingGroup shippingGroup : shippingGroups) {
			if(shippingGroup instanceof BBBHardGoodShippingGroup){
				hsg = (BBBHardGoodShippingGroup) shippingGroup;
				hsg.setAutoWaiveFlag(false);
				hsg.setAutoWaiveClasses(null);
				vlogDebug("shipping group: " + hsg );
				
				List <CommerceItemRelationship> cirs = hsg.getCommerceItemRelationships();
				
				for (CommerceItemRelationship cir : cirs) {
					cItem = cir.getCommerceItem();
					if(cItem instanceof TBSCommerceItem){
						tbsItem = (TBSCommerceItem) cItem;
					}
					vlogDebug("tbsItem: " +tbsItem);
					
					if( tbsItem != null && tbsItem.isAutoWaiveFlag() ) {
						
						vlogDebug("item auto waive flag is set to true, set SG autowaive to true and set classes");
						hsg.setAutoWaiveFlag(true);
						
						String classes = hsg.getAutoWaiveClasses();
						if (StringUtils.isEmpty(classes)) {
							vlogDebug("classes is empty, setting autoWaive classes to "  + tbsItem.getAutoWaiveClassification());
							hsg.setAutoWaiveClasses(tbsItem.getAutoWaiveClassification());
						} else {
							vlogDebug("setting autoWaive classes to" + hsg.getAutoWaiveClasses() + " " + tbsItem.getAutoWaiveClassification() );
							hsg.setAutoWaiveClasses(hsg.getAutoWaiveClasses() + "," + tbsItem.getAutoWaiveClassification());
						}
					}
					tbsItem = null;
				}
			}
		}
	}

	private void copyLineItemDetails(List<CommerceItem> citems, List<AutoWaiveShippingInfoResponseOrderLineItemVO> lineItemVOs) {
		
		if(citems != null && lineItemVOs != null && !lineItemVOs.isEmpty()){
			for (AutoWaiveShippingInfoResponseOrderLineItemVO autoWaiveLineItemVO : lineItemVOs) {
				TBSCommerceItem tbsItem = null;
				for (CommerceItem cItem : citems) {
					if (cItem.getCatalogRefId().equals(String.valueOf(autoWaiveLineItemVO.getSku()))) {
						if(isTestAutowaiveMockService()){
							String itemAutoWaiveClassification=autoWaiveLineItemVO.getSkuClassification();
							String itemAutowaiveFlag=autoWaiveLineItemVO.getWaiveShipFlag();
							if(!StringUtils.isBlank(itemAutoWaiveClassification)){
								logDebug("Autowaive Sku Classification for item "+cItem.getId()+":"+itemAutoWaiveClassification);
							}
							else {
								logDebug("Autowaive Sku Classification for item "+cItem.getId()+":Empty");
							}
							if(!StringUtils.isBlank(itemAutowaiveFlag)){
								logDebug("Autowaive flag for item "+cItem.getId()+":"+itemAutowaiveFlag);
							}
							else {
								logDebug("Autowaive flag for item "+cItem.getId()+":Empty");
							}
							tbsItem = (TBSCommerceItem) cItem;
							tbsItem.setAutoWaiveClassification(autoWaiveLineItemVO.getSkuClassification());
							if(!StringUtils.isBlank(autoWaiveLineItemVO.getWaiveShipFlag())){
								tbsItem.setAutoWaiveFlag(autoWaiveLineItemVO.getWaiveShipFlag().equals("Y"));
							}
							if(tbsItem.isAutoWaiveFlag() && !BBBUtility.isListEmpty(tbsItem.getShippingGroupRelationships())) {
								// Set same flag on the shipping group
								ShippingGroupRelationship sgr = (ShippingGroupRelationship) tbsItem.getShippingGroupRelationships().get(0);
								if (sgr.getShippingGroup() instanceof BBBHardGoodShippingGroup) {
									BBBHardGoodShippingGroup hsg = (BBBHardGoodShippingGroup) sgr.getShippingGroup();
									hsg.setAutoWaiveFlag(true);
									String classes = hsg.getAutoWaiveClasses();
									if (StringUtils.isEmpty(classes)) {
										hsg.setAutoWaiveClasses(tbsItem.getAutoWaiveClassification());
									} else {
										hsg.setAutoWaiveClasses(hsg.getAutoWaiveClasses() + "," + tbsItem.getAutoWaiveClassification());
									}
								}

							}
						}	
						else {
							if(cItem instanceof TBSCommerceItem && autoWaiveLineItemVO.getOrderLineId().equals(cItem.getId())){
								tbsItem = (TBSCommerceItem) cItem;
								tbsItem.setAutoWaiveClassification(autoWaiveLineItemVO.getSkuClassification());
								if(!StringUtils.isBlank(autoWaiveLineItemVO.getWaiveShipFlag())){
									tbsItem.setAutoWaiveFlag(autoWaiveLineItemVO.getWaiveShipFlag().equals("Y"));
								}
								if( tbsItem.isAutoWaiveFlag() && !BBBUtility.isListEmpty(tbsItem.getShippingGroupRelationships())) {
									// Set same flag on the shipping group
									ShippingGroupRelationship sgr = (ShippingGroupRelationship) tbsItem.getShippingGroupRelationships().get(0);
									if (sgr.getShippingGroup() instanceof BBBHardGoodShippingGroup) {
										BBBHardGoodShippingGroup hsg = (BBBHardGoodShippingGroup) sgr.getShippingGroup();
										hsg.setAutoWaiveFlag(true);
										String classes = hsg.getAutoWaiveClasses();
										if (StringUtils.isEmpty(classes)) {
											hsg.setAutoWaiveClasses(tbsItem.getAutoWaiveClassification());
										} else {
											hsg.setAutoWaiveClasses(hsg.getAutoWaiveClasses() + "," + tbsItem.getAutoWaiveClassification());
										}
									}

								}
							}
						}}
				}
			}
		}
		
	}

	/**
	 * This is used to get the store number from cookie
	 * @param pRequest
	 * @return
	 */
	public String storeNumFromCookie(HttpServletRequest pRequest) {
		Cookie cookies[] = pRequest.getCookies();
		String cookieValue = null;
		if (cookies != null)
			for (int i = 0; i < cookies.length; i++) {
				String name = cookies[i].getName();
				if (TBSConstants.STORE_NUMBER_COOKIE.equals(name)) {
					cookieValue = cookies[i].getValue();
					break;
				}
			}
		return cookieValue;
	}
	
	/**
	 * This method is used to remove the AutoWaive details from the order
	 * @param pOrder
	 */
	@SuppressWarnings("unchecked")
	public void removeAutoWaiveDetails(Order pOrder) {
		BBBOrder order = (BBBOrder) pOrder;
		List<CommerceItem> cItems = null;
		List<ShippingGroup> shipGroups = null;
		
		boolean shouldRollback = true;
		TransactionDemarcation td = new TransactionDemarcation();
	        try {
	        	td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
	        	synchronized (order) {
	        		
	        		order.setAutoWaiveFlag(false);
	        		order.setAutoWaiveClassification(null);
	        		
	        		cItems = order.getCommerceItems();
	        		TBSCommerceItem tbsItem = null;
	        		for (CommerceItem commerceItem : cItems) {
	        			if(commerceItem instanceof TBSCommerceItem){
	        				tbsItem = (TBSCommerceItem) commerceItem;
	        				tbsItem.setAutoWaiveFlag(false);
	        				tbsItem.setAutoWaiveClassification(null);
	        			}
	        		}
	        		shipGroups = order.getShippingGroups();
	        		BBBHardGoodShippingGroup hardShip = null;
	        		for (ShippingGroup shippingGroup : shipGroups) {
	        			if(shippingGroup instanceof BBBHardGoodShippingGroup){
	        				hardShip = (BBBHardGoodShippingGroup) shippingGroup;
	        				hardShip.setAutoWaiveFlag(false);
	        				hardShip.setAutoWaiveClasses(null);
	        			}
	        		}
	        		getOrderManager().updateOrder(pOrder);
	        		shouldRollback = false;
	        	}
	        } catch (CommerceException e) {
	        	logError("Error occurred while removing auto waive details", e);
	        } catch (TransactionDemarcationException e) {
	        	logError("TransactionDemarcationException while removing auto waive details", e);
			} finally {
	        	try {
					td.end(shouldRollback);
				} catch (TransactionDemarcationException e) {
					this.logError("TransactionDemarcationException exception occurred", e);
				}
	        }
		
	}
	@SuppressWarnings("unchecked")
	public void removeAutoWaiveDetailsFromCommerceItem(Order pOrder) {
		BBBOrder order = (BBBOrder) pOrder;
		List<CommerceItem> cItems = null;
		TBSCommerceItem tbsItem = null;
		cItems = order.getCommerceItems();
		for (CommerceItem commerceItem : cItems) {
			if(commerceItem instanceof TBSCommerceItem){
				tbsItem = (TBSCommerceItem) commerceItem;
				tbsItem.setAutoWaiveFlag(false);
				tbsItem.setAutoWaiveClassification(null);
			}
		}
	}
	
	public List<String> getBlockedAutoWaiveStores() {
		List<String> storeIds = null;
		try {
//			searchStoreManager = (TBSSearchStoreManager) Nucleus.getGlobalNucleus().resolveName("/com/bbb/selfservice/SearchStoreManager");
			storeIds = getCatalogToolsImpl().getAllValuesForKey("WSDL_AutoWaiveShipping", "autoWaive_blocked_stores");
		} catch (BBBSystemException e) {
			vlogError("No Value found for autoWaive_blocked_stores");
		} catch (BBBBusinessException e) {
			vlogError("No Value found for autoWaive_blocked_stores");
		}
		return storeIds;
	}
	
	/**
	 * This is used to search the order based on the below conditions with some of them containing the minimum combinations.
	 * •	Any of the following minimum combinations are required for the search : 
	 *	o	Name Date
	 *	o	Name Store
	 *	o	Date Store
	 *  o	Registry
	 *
	 * @param pFirstName
	 * @param pLastName
	 * @param pStartDate
	 * @param pEndDate
	 * @param pRegistryId
	 * @param pStoreId
	 * @param orders 
	 * @return
	 * @throws RepositoryException 
	 * @throws ParseException 
	 */
	public List<RepositoryItem> advancedOrderSearch(String pFirstName, String pLastName,
			String pStartDate, String pEndDate, String pRegistryId, String pStoreId, int pThreshhold) throws RepositoryException, ParseException {
		List<RepositoryItem> orderIdorderLists = null;
		
		vlogDebug("TBSOrderTools :: orderSearch() :: START " );
		String siteId =  SiteContextManager.getCurrentSiteId();
		String ecomSiteId = null;
		if(siteId.contains(TBSConstants.BED_BATH_US)){
			ecomSiteId = TBSConstants.BED_BATH_US;
        } else if(siteId.contains(TBSConstants.BED_BATH_CA)){
        	ecomSiteId = TBSConstants.BED_BATH_CA;
        } else if(siteId.contains(TBSConstants.BUY_BUY_BABY)){
        	ecomSiteId = TBSConstants.BUY_BUY_BABY;
        }
		vlogDebug(" Searching for orders using FirstName: "+pFirstName +" LastName : "+pLastName +" StartDate : "+pStartDate +
				" EndDate : "+pEndDate +" RegistryId : "+pRegistryId +" StoreId : "+pStoreId +"Site Id "+siteId+"Online Site Id"+ecomSiteId);
		
		TBSOrderPropertyManager propetyManager = (TBSOrderPropertyManager)getPropertyManager();
		RepositoryItemDescriptor orderItemDescriptor = getOrderRepository().getItemDescriptor(propetyManager.getOrderName());
		RepositoryView orderView = orderItemDescriptor.getRepositoryView();
		RqlStatement statement = null;
		RepositoryItem [] orders = null;
		Timestamp startDate = null;
		Timestamp endDate = null;
		int threshold = pThreshhold + 1;
		String rangeQuery = " RANGE +"+threshold;
		
		if(!StringUtils.isBlank(pStartDate)){
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			Date sDate = dateFormat.parse(pStartDate);
			startDate = new Timestamp(sDate.getTime());
			//startDate = new java.sql.Date(sDate.getTime());
			Date eDate = dateFormat.parse(pEndDate);
			//endDate = new java.sql.Date(eDate.getTime());
			// Increment endDate by 1 to account for the entire day
			Calendar c = Calendar.getInstance(); 
			c.setTime(eDate); 
			c.add(Calendar.DATE, 1);
			eDate = c.getTime();
			endDate = new Timestamp(eDate.getTime());
		}
		
		StringBuilder query = new StringBuilder();
		Object params[] = new Object[6];
		int pos = 0;
		
		if(!StringUtils.isBlank(pFirstName)) {
			query.append("billingAddress.firstName STARTS WITH IGNORECASE ?" + pos);
			query.append(" AND ");
			params[pos] = pFirstName;
			pos++;
		}
		
		if(!StringUtils.isBlank(pLastName)) {
			query.append("billingAddress.lastName STARTS WITH IGNORECASE ?" + pos);
			query.append(" AND ");
			params[pos] = pLastName;
			pos++;
		}
		
		if(startDate != null  && endDate != null) {
			query.append("submittedDate >=?" + pos);
			query.append(" AND ");
			params[pos] = startDate;
			pos++;
			
			query.append("submittedDate <= ?" + pos);
			query.append(" AND ");
			params[pos] = endDate;
			pos++;
		} else{
			query.append("state != ?" + pos);
			query.append(" AND ");
			params[pos] = "INCOMPLETE";
			pos++;
		}
		
		if(!StringUtils.isBlank(pRegistryId)) {
			query.append("commerceItems INCLUDES ITEM (registryId=?" + pos + ")");
			query.append(" AND ");
			params[pos] = pRegistryId;
			pos++;
		}
		
		if(!StringUtils.isBlank(pStoreId)) {
			query.append("tbsStoreNo=?" + pos);
			params[pos] = pStoreId;
			pos++;
		}
		
		if(query.toString().endsWith(" AND ")) {
			query.replace(query.lastIndexOf(" AND"), query.length(), "");
		}
		
		query.append(rangeQuery);
		
		statement = RqlStatement.parseRqlStatement(query.toString());
		orders = statement.executeQuery (orderView, params);
		
		

		if(orders != null){
			orderIdorderLists = new ArrayList<RepositoryItem>(Arrays.asList(orders));
			vlogDebug("Total Resulted Orders " + orderIdorderLists);
			vlogDebug("TBSOrderTools :: orderSearch() :: END " );
		}
	  
		String fetchArchiveOrders = this.getCatalogToolsImpl().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
				BBBCoreConstants.FETCH_ARCHIVED_ORDERS, BBBCoreConstants.FALSE);
		
		vlogDebug("Archived flag {0} ", fetchArchiveOrders);
		
		if(Boolean.valueOf(fetchArchiveOrders)) {
			orderItemDescriptor = getArchiveOrderRepository().getItemDescriptor(propetyManager.getOrderName());
			orderView = orderItemDescriptor.getRepositoryView();
			RepositoryItem[] archivedOrders = statement.executeQuery (orderView, params);
			
			
			
			if(!BBBUtility.isArrayEmpty(archivedOrders)) {
				vlogDebug("Archived orders {0}"+archivedOrders);
				ArrayList<RepositoryItem> archivedOrderList = new ArrayList<RepositoryItem>(Arrays.asList(archivedOrders));
				
				if(!BBBUtility.isListEmpty(orderIdorderLists)) {
					orderIdorderLists.addAll(archivedOrderList);
				} else {
					orderIdorderLists = archivedOrderList;
				}
				
			}
			
		}
		return orderIdorderLists;
	}
	/**
	 * This method is used search the order based on either emailId or order number in archive order repository
	 * @param pOrderId
	 * @param pEmailId
	 * @return
	 * @throws RepositoryException
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public List<RepositoryItem> archiveOrderSearch(String pOrderId, String pEmailId) throws RepositoryException, BBBSystemException, BBBBusinessException {

		
		vlogDebug("TBSOrderTools :: orderSearch() :: START " );
		TBSOrderPropertyManager propetyManager = (TBSOrderPropertyManager)getPropertyManager();
		RepositoryItemDescriptor orderItemDescriptor = getArchiveOrderRepository().getItemDescriptor(propetyManager.getOrderName());
		RepositoryView orderView = orderItemDescriptor.getRepositoryView();
		RqlStatement statement = null;
		RepositoryItem [] orders = null;
		String siteId =  SiteContextManager.getCurrentSiteId();
		String ecomSiteId = null;
		if(siteId.contains(TBSConstants.BED_BATH_US)){
			ecomSiteId = TBSConstants.BED_BATH_US;
        } else if(siteId.contains(TBSConstants.BED_BATH_CA)){
        	ecomSiteId = TBSConstants.BED_BATH_CA;
        } else if(siteId.contains(TBSConstants.BUY_BUY_BABY)){
        	ecomSiteId = TBSConstants.BUY_BUY_BABY;
        }
		
		int numResultSetSize = getDefaultResultSetSize();
		 
		 List<String> resultSetSizeValueFromConfigKey = null;
		 
		 resultSetSizeValueFromConfigKey = getCatalogToolsImpl().getAllValuesForKey("AdvancedOrderInquiryKeys", "ResultSetSize");
		 				 
		 if (null != resultSetSizeValueFromConfigKey
					&& null != resultSetSizeValueFromConfigKey.get(TBSConstants.ZERO)){
			 numResultSetSize = Integer.parseInt(resultSetSizeValueFromConfigKey.get(TBSConstants.ZERO));
		 }
		
		vlogDebug(" Searching for orders using orderId: "+pOrderId +" Email Id : "+pEmailId);
		if (BBBUtility.isNotEmpty(pOrderId))
			pOrderId = pOrderId.toUpperCase();
		if(!StringUtils.isBlank(pOrderId) && pOrderId.startsWith("OLP")) {
			
			orders = this.getBopusOrders(pOrderId,siteId, ecomSiteId, getArchiveOrderRepository());
			

		} else if(!StringUtils.isBlank(pOrderId) && StringUtils.isBlank(pEmailId)){
			statement = RqlStatement.parseRqlStatement(" onlineOrderNumber=?0 AND state !=?1 AND (siteId = ?2 or siteId = ?3) order by submittedDate DESC RANGE +?4");
			Object params[] = new Object[5];
			params[0] = pOrderId;
			params[1] = TBSConstants.ORDER_INCOMPLETE;
			params[2] = siteId;
			params[3] = ecomSiteId;
			params[4] = numResultSetSize;
			orders =statement.executeQuery (orderView, params);
		} else if(StringUtils.isBlank(pOrderId) && !StringUtils.isBlank(pEmailId)){
			statement = RqlStatement.parseRqlStatement("billingAddress.email  EQUALS IGNORECASE ?0 AND state !=?1 AND (siteId = ?2 or siteId = ?3) ORDER BY submittedDate DESC RANGE +?4" );
			Object params[] = new Object[5];
			params[0] = pEmailId.toLowerCase();
			params[1] = TBSConstants.ORDER_INCOMPLETE;
			params[2] = siteId;
			params[3] = ecomSiteId;
			params[4] = numResultSetSize;
			orders =statement.executeQuery (orderView, params);
		} else if(!StringUtils.isBlank(pOrderId) && !StringUtils.isBlank(pEmailId)){
			//Modifying the RQL query to fix memory leak issue in TBS Production
			//OLD RQL QUERY
			//statement = RqlStatement.parseRqlStatement("(onlineOrderNumber =?0 OR billingAddress.email  EQUALS IGNORECASE ?1) AND state !=?2 AND siteId CONTAINS ?3" );
			//NEW RQL QUERY
			statement = RqlStatement.parseRqlStatement("(state !=?2 AND (siteId = ?3 or siteId = ?4) AND billingAddress.email =?1) OR (onlineOrderNumber =?0 AND state !=?2 AND (siteId = ?3 or siteId = ?4)) ORDER BY submittedDate DESC RANGE +?5");
			Object params[] = new Object[6];
			params[0] = pOrderId;
			params[1] = pEmailId.toLowerCase();
			params[2] = TBSConstants.ORDER_INCOMPLETE;
			params[3] = siteId;
			params[4] = ecomSiteId;
			params[5] = numResultSetSize;
			orders =statement.executeQuery (orderView, params);
		}
		if(orders != null){
			List<RepositoryItem> orderIdorderLists = new ArrayList<RepositoryItem>(Arrays.asList(orders));
			vlogDebug("Total Resulted Orders " + orderIdorderLists);
			vlogDebug("TBSOrderTools :: orderSearch() :: END " );
			return orderIdorderLists;
		} 
		vlogDebug("TBSOrderTools :: orderSearch() :: END " );
		return null;
	
	}
	
	/**
	 * Fetch order using search term in given repository
	 * @param pSearchTerm
	 * @param orderRepository
	 * @return
	 * @throws RepositoryException
	 */
	public RepositoryItem[] getOnlineOrders(String pSearchTerm, Repository orderRepository) throws RepositoryException {
		
		vlogDebug("Enter TBSOrderTools.getOnlineOrders() search term {0} order repository {1}", pSearchTerm,orderRepository);
		RepositoryItem[] orders = null;
		TBSOrderPropertyManager propetyManager = (TBSOrderPropertyManager)getPropertyManager();
		RepositoryItemDescriptor orderItemDescriptor = orderRepository.getItemDescriptor(propetyManager.getOrderName());
		RepositoryView orderView = orderItemDescriptor.getRepositoryView();
		QueryBuilder orderQueryBuilder = orderView.getQueryBuilder();
		
		QueryExpression onlineOrderProperty = orderQueryBuilder.createPropertyQueryExpression(propetyManager.getOnlineOrderNumberProperty());
		QueryExpression searchTermValue = orderQueryBuilder.createConstantQueryExpression(pSearchTerm);
		Query orderQuery = orderQueryBuilder.createComparisonQuery(onlineOrderProperty, searchTermValue, QueryBuilder.EQUALS);
		
		QueryExpression orderStateProperty = orderQueryBuilder.createPropertyQueryExpression(propetyManager.getOrderStateProperty());
		QueryExpression stateValue = orderQueryBuilder.createConstantQueryExpression(TBSConstants.ORDER_INCOMPLETE);
		Query stateQuery = orderQueryBuilder.createComparisonQuery(orderStateProperty, stateValue, QueryBuilder.NOT_EQUALS);
		
		/*Create AND query using orderQuery, stateQuery attributes*/
		Query[] pieces = {orderQuery, stateQuery};
		Query submittedOrdersQuery = orderQueryBuilder.createAndQuery(pieces);
		
		vlogDebug(" submittedOrdersQuery: " + submittedOrdersQuery);
		
		orders = orderView.executeQuery(submittedOrdersQuery);	
		vlogDebug("Exit TBSOrderTools.getOnlineOrders() orders "+orders);
		return orders;
	}	
	
	/**
	 * This is used to search the order based on the below conditions with some of them containing the minimum combinations.
	 * •	Any of the following minimum combinations are required for the search : 
	 *	o	Name Date
	 *	o	Name Store
	 *	o	Date Store
	 *  o	Registry
	 *
	 * @param pFirstName
	 * @param pLastName
	 * @param pStartDate
	 * @param pEndDate
	 * @param pRegistryId
	 * @param pStoreId
	 * @param orders 
	 * @return
	 * @throws RepositoryException 
	 * @throws ParseException 
	 */
	public List<OrderVO> minimalAdvancedOrderSearch(String pFirstName, String pLastName,
			String pStartDate, String pEndDate, String pRegistryId, String pStoreId, int numResultSetSize) throws RepositoryException, ParseException {
		Connection con = null;
		CallableStatement cs = null;
		ResultSet resultSet = null;
		List<OrderVO> resultedOrders = new ArrayList<OrderVO>();
		
		DateFormat dateFormat = null;
		String siteId = SiteContextManager.getCurrentSiteId();
		if (TBSConstants.SITE_TBS_BAB_US.equalsIgnoreCase(siteId) || 
				TBSConstants.SITE_TBS_BBB.equalsIgnoreCase(siteId)) {
			dateFormat = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
		} else if (TBSConstants.SITE_TBS_BAB_CA.equalsIgnoreCase(siteId)) {
			dateFormat = new SimpleDateFormat(BBBCoreConstants.CA_DATE_FORMAT);
		}
		
		vlogDebug("TBSOrderTools :: minimalAdvancedOrderSearch() :: START " );
		
		vlogDebug(" Searching for orders using FirstName: "+pFirstName +" LastName : "+pLastName +" StartDate : "+pStartDate +
				" EndDate : "+pEndDate +" RegistryId : "+pRegistryId +" StoreId : "+pStoreId);
		
		int fetchHistoryFlag = getFetchHistoryFlag();
		vlogDebug("fetchHistoryFlag: "+ fetchHistoryFlag);
		
		try{
			con = ((GSARepository) getOrderRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				// prepare the callable statement
				cs = con.prepareCall(TBSConstants.FETCH_ORDER_TRACKING);
				cs.setFetchSize(100);
				// set input parameters ...
				
				cs.setObject(1, null);
				cs.setObject(2, null);
				cs.setObject(3, null);
				cs.setObject(4, null);
				cs.setObject(5, null);
				cs.setObject(6, null);
				cs.setObject(7, null);
				cs.setObject(8, null);
				cs.setObject(9, null);
				cs.setObject(10, null);
				cs.setObject(11, null);
				cs.setObject(12, null);
				cs.setObject(13, null);
				
				if(!StringUtils.isBlank(pFirstName)) {
					cs.setString(4, pFirstName);
				}
				
				if(!StringUtils.isBlank(pLastName)) {
					cs.setString(5, pLastName);
				}
				
				if(pStartDate != null  && pEndDate != null) {
					cs.setString(6, pStartDate);
					cs.setString(7, pEndDate);
				}
				
				if(!StringUtils.isBlank(pRegistryId)) {
					cs.setString(8, pRegistryId);
				}
				
				if(!StringUtils.isBlank(pStoreId)) {
					cs.setString(9, pStoreId);
				}
				
				cs.setInt(12,numResultSetSize);
				cs.setInt(13, fetchHistoryFlag);
				
				cs.registerOutParameter(14, OracleTypes.CURSOR);
				vlogDebug("Advance Order search via Stored Procs| fname:"+pFirstName+"| lname:"+pLastName+"| stdate:"+pStartDate+"| endate:"+pEndDate+"| regId:"+pRegistryId+"| storeId:"+pStoreId+"| numResultSetSize:"+numResultSetSize+"| fetchHistoryFlag:"+fetchHistoryFlag);
				// execute stored proc
				cs.execute();
				resultSet = (ResultSet) cs.getObject(14);

				while(resultSet.next())
				{
					OrderVO order = new OrderVO();
					order.setOnlineOrderNumber(resultSet.getString(1));
					order.setOrderNumber(resultSet.getString(2));
					
					if(resultSet.getDate(3) != null) {
						order.setSubmittedDate(dateFormat.format(resultSet.getDate(3)));
					}
					
					order.setFirstName(resultSet.getString(4));
					order.setLastName(resultSet.getString(5));
					order.setEmailId(resultSet.getString(6));
					order.setOrderAmount(resultSet.getDouble(7));
					order.setOrderStatus(resultSet.getString(8));
					order.setStore(resultSet.getString(9));
					order.setAssociate(resultSet.getString(10));
					resultedOrders.add(order);
				}
			}			
		}catch(Exception e){
			vlogError("Error occurred while fetching data for Order Enquiry" + e);
		}finally{
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (cs != null) {
					cs.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				vlogError("Error occurred while closing connection", e);
			}
		}
		return resultedOrders;
	}
	
	int getFetchHistoryFlag(){
		
		String fetchHistory=TBSConstants.FALSE;
		int fetchHistoryFlag = 1;
		List<String> fetchHistoryKey = null;
		try {
			fetchHistoryKey = getCatalogToolsImpl().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.FETCH_ARCHIVED_ORDERS);
		} catch (BBBSystemException e) {
			vlogError("No Value found for AdvanceSearchFetchHistory FlagDrivenFunction | Setting it to 0 | " , e);
		} catch (BBBBusinessException e) {
			vlogError("No Value found for AdvanceSearchFetchHistory FlagDrivenFunction | Setting it to 0 | " , e);
		}
		if(!BBBUtility.isListEmpty(fetchHistoryKey)){
			fetchHistory = fetchHistoryKey.get(0);
		}
		if(!(TBSConstants.TRUE).equalsIgnoreCase(fetchHistory)){ 
			fetchHistoryFlag = 0;
		}
		return fetchHistoryFlag;
	}

}
