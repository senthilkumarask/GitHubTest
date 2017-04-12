/**
 * 
 */
package com.bbb.commerce.porch.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.TransactionManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.porch.service.vo.PorchBookAJobResponseVO;
import com.bbb.commerce.porch.service.vo.PorchJobVO;
import com.bbb.commerce.porch.service.vo.PorchSchedlueJobResponseVO;
import com.bbb.commerce.porch.service.vo.PorchServiceHeader;
import com.bbb.commerce.porch.service.vo.PorchServiceOrderVO;
import com.bbb.commerce.porch.service.vo.PorchValidateZipCodeResponseVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BaseCommerceItemImpl;
import com.bbb.order.bean.TrackingInfo;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.PropertyNameConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.states.StateDefinitions;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
 

/**
 * @author sm0191
 *
 */
 
public class PorchServiceManager extends GenericService {

	private BBBOrderManager orderManager;
	private BBBCommerceItemManager commerceItemManager;
	private HTTPCallInvoker httpCallInvoker;
	private MutableRepository scheduledRepository;
	private TransactionManager transactionManager;
	private CheckListTools checkListTools;
	private BBBCatalogTools catalogTools;
	
	private static final String FEED = "feed";	
	private static final String SCHEDULER_COMPLETION_DATE = "schedulerCompletionDate";
	private static final String SCHEDULER_START_DATE = "schedulerStartDate";
	private static final String STATUS = "status";
	private static final String TYPE_OF_FEED = "typeOfFeed";
	private static final String MODE = "mode";
	private static final String SERVICE_REFERAL_ITEM="serviceReferal";
	private static final String PORCH_JSON_DATA_REQUEST="jsonDataRequest";
	private static final String ADVERTISED_PRICE_HIGH="advertisedPriceHigh";
	private static final String ADVERTISED_PRICE_LOW="advertisedPriceLow";
	private static final String JOB_PRICE_ESTIMATION="jobPriceEstimation";
	private static final String PORCH_CREATION_DATE="creationDate";
	private static final String PORCH_JOB_ID="jobId";
	private static final String PORCH_JOB_STATUS="jobStatus";
	private static final String PORCH_JOB_STATUS_IN_PROGRESS="IN PROGRESS";
	private static final String PORCH_JOB_STATUS_FAILED="FAILED";
	private static final String PORCH_JOB_STATUS_PENDING="PENDING";
	private static final String PORCH_BOOK_A_JOB_REQ="bookAJobJsonReq"; 
	private static final String PORCH_BOOK_A_JOB_RES="bookAJobJsonResp"; 
	private static final String PORCH_PROJECT_ID="porchProjectId" ;	
	private static final String PORCH_CHANGE_DATE="changeDate";
	private static final String PORCH_RETRY_COUNT="retryCount";
	private static final String PORCH_SERVICE_FAMILY_CODE="porchServiceFamilyTypeCode";
	private static final String PORCH_SERVICE_FAMILY_TYPE="porchServiceFamilyType";
	private static final String PORCH_CONSTNAT="porch";
	private static final String PORCH_JSON_CONSTNAT="json";
	private static final String PORCH_SCHEDULED_DATE="scheduledDate";
	private static final String PORCH_TRACKING_URL="trackingUrl";
	private static final String PORCH_PHONE_NUMBER="phoneNumber";
	private static final String PORCH_PARTNER_CUSTMERID="partnerCustomerId";
	private static final String PORCH_ADDRESS_STREET1="street1";
	private static final String PORCH_ADDRESS_STREET2="street2";
	private static final String PORCH_ADDRESS_CITY="city";
	private static final String PORCH_ADDRESS_STATE="state";
	private static final String PORCH_ADDRESS_POSTALCODE="postalCode";
	private static final String PORCH_ADDRESS1="address1";
	private static final String PORCH_ADDRESS2="address2";	 
	private static final String PORCH_DELIVERY_ADDRESS="deliveryAddress";
	private static final String PORCH_CUSTOMER="customer";
	private static final String PORCH_JOBS="jobs";	
	private static final String PORCH_CATEGORYLIST="categoriesList";
	private static final String PORCH_SUBTYPE_CODE="PRH";
	private static final String PORCH_SERVICETYPECODE="serviceTypeCode";
	private static final String SYMBOL_$="$";
	private static final String PORCH_POSTAL_CODE="postalCode";	
	private static final String PORCH_PARTNER_ORDER_ID="partnerOrderId";
	private static final String PORCH_DISCOUNT_CODE="discountCode";
	private static final String PHONE_NUMBER="phoneNumber";
	private static final String CATEGORY_NAME="categoryName";
	private static final String PORCH_REPORT_SERVICE_INFO="orderServiceReferralInfo";
	private static final String PORCH_REPORT_APPLICATION_NAME="PorchOrdersATGtoEDW";
	private static final String PORCH_ORDER_TIBCO_MSG="porchOrderTibcoMessage";
	private static final String ORDER_NUMBER="OrderNumber";
	private static final String DATE_FORMAT="MM-dd-yyyy";
	private static final String ORDER_SUBMIT_DATE="OrderSubmitDate";
	private static final String ORDER_STATE="OrderState";
	private static final String SHIPPING_GROUPID="ShippingGroupID";
	private static final String COMMERCE_ITEM_ID="CommerceItemID";
	private static final String SKU_ID="SkuID";
	private static final String SKU_DISPLAY_NAME="SkuDisplayName";
	private static final String UNIT_PRICE="UnitPrice";
	private static final String QTY_ORDERED="QtyOrdered";
	private static final String LINE_ITEM_AMOUNT="LineItemAmount";
	private static final String ORDER_LINE_STATE="OrderLineState";
	private static final String ORDER_AMOUNT="OrderAmount";
	private static final String PORCH_SERVICE_TYPE="ServiceType";
	private static final String PROJECT_ID="ProjectID";
	private static final String SERVICE_FAMILY_TYPE_CODE="ServiceFamilyTypeCode";	
	private static final String SERVICE_CATEGORY="serviceCategory";
	private static final String JOB_ID="JobID";
	private static final String PRICE_RANGE="PriceRange";
	
	private static final String SERVICE_REFERRAL_INFO="ServiceReferralInfo";
	private static final String COMMERCE_ITEM_INFO="commerceItemInfo";
	private static final String PORCH_COMMERCE_ITEMS="CommerceItems";
	private static final String SHIPPING_GROUP_INFO="ShippingGroupInfo";
	private static final String SHIPPING_GROUPS="ShippingGroups";
	
	 
	/**
	 * @param pPropertyName
	 * @param pPropertyValue
	 * @param serviceRefItem
	 */
	public void updateScheduledJobResponse(String pPropertyName, String pPropertyValue, RepositoryItem serviceRefItem) {
		
		if(isLoggingDebug()){
			logDebug("Method start update scheduled job response "+getClass().getName() +".updateScheduledJobResponse()");
		}
		
		try {
			MutableRepository orderRepository = (MutableRepository) getOrderManager().getOrderTools()
					.getOrderRepository();
			MutableRepositoryItem mutServiceRefItem =orderRepository.getItemForUpdate(serviceRefItem.getRepositoryId(), SERVICE_REFERAL_ITEM);
			mutServiceRefItem.setPropertyValue(pPropertyName, pPropertyValue);
			orderRepository.updateItem(mutServiceRefItem);
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError("Error while updating porch serviceref details "+e,e);
			}
		}
		if(isLoggingDebug()){
			logDebug("Method ends update scheduled job response "+getClass().getName() +".updateScheduledJobResponse()");
		}

	}

 

	/**
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isPorchServiceOrder(Order order) {
		
		if(isLoggingDebug()){
			logDebug("Checking order " + order.getId() +" has porch service or not ");
		}
		boolean isPorchOrder = false;
		try{
		final List<CommerceItem> comItemObj = order.getCommerceItems();
		if(null==comItemObj){
			return false;
		}
		for (CommerceItem comerceItem : comItemObj) {

			BaseCommerceItemImpl citem = (BaseCommerceItemImpl) comerceItem;
			isPorchOrder = citem.isPorchService();
			if (isPorchOrder) {
				return isPorchOrder;
			}

		}
		}
		catch (Exception e){
			isPorchOrder=false;
		}
		return isPorchOrder;
	}


	/**
	 * @param jsonObject
	 * @param existingServiceRefItem
	 * @param productVO
	 * @return
	 */
	public RepositoryItem createUpdateServiceRefPorch(JSONObject jsonObject, RepositoryItem existingServiceRefItem, ProductVO productVO) {
		
		if(isLoggingDebug()){
			logDebug("Method start Creating or updating porch service ref "+getClass().getName() +".createUpdateServiceRefPorch()");
		}
		MutableRepositoryItem serviceReferalItem = null;
		try {
			MutableRepository orderRepository = (MutableRepository) getOrderManager().getOrderTools()
					.getOrderRepository();
			if(existingServiceRefItem==null){
			serviceReferalItem = (MutableRepositoryItem) orderRepository.createItem(SERVICE_REFERAL_ITEM);		 
			}
			else{
				serviceReferalItem = orderRepository.getItemForUpdate(existingServiceRefItem.getRepositoryId(),
					SERVICE_REFERAL_ITEM);				 
			}
			serviceReferalItem.setPropertyValue(PORCH_JSON_DATA_REQUEST, jsonObject.toString());			
			String estimatedPrice=null;
			try {
				String advertisedPriceHigh = jsonObject.getString(ADVERTISED_PRICE_HIGH);
				String advertisedPriceLow = jsonObject.getString(ADVERTISED_PRICE_LOW);
				try{
					Integer.parseInt(advertisedPriceHigh);					
				}
				catch(NumberFormatException e){
					advertisedPriceHigh=null;
				}
				try{
					Integer.parseInt(advertisedPriceLow);					
				}
				catch(NumberFormatException e){
					advertisedPriceLow=null;
				}
				
				if(!StringUtils.isBlank(advertisedPriceLow) && !StringUtils.isBlank(advertisedPriceHigh)){
					estimatedPrice=SYMBOL_$+advertisedPriceLow +" - "+SYMBOL_$+advertisedPriceHigh;
				}
			} catch (JSONException e) {
				if(isLoggingError()){
					logError("Error while create porch service ref"+e,e);
				}
			}			
			serviceReferalItem.setPropertyValue(JOB_PRICE_ESTIMATION, estimatedPrice);
			
			serviceReferalItem.setPropertyValue(PORCH_JOB_STATUS, PORCH_JOB_STATUS_PENDING);
			serviceReferalItem.setPropertyValue(PORCH_RETRY_COUNT, 0);
			if(null!=productVO){
			serviceReferalItem.setPropertyValue(PORCH_SERVICE_FAMILY_CODE, productVO.getPorchServiceFamilyCodes().get(0));
			serviceReferalItem.setPropertyValue(PORCH_SERVICE_FAMILY_TYPE, productVO.getPorchServiceFamilyType());
			}
			if(existingServiceRefItem==null){
				serviceReferalItem.setPropertyValue(PORCH_CREATION_DATE, new Date());	
				orderRepository.addItem(serviceReferalItem);
			}else{
				serviceReferalItem.setPropertyValue(PORCH_CHANGE_DATE, new Date());	
				orderRepository.updateItem(serviceReferalItem);
			}
			
			return serviceReferalItem;
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError("Error while adding porch service details "+e,e);
			}
		}
		if(isLoggingDebug()){
			logDebug("Method end Creating or updating porch service ref "+getClass().getName() +".createUpdateServiceRefPorch()");
		}
		return null;

	}

	/**
	 * @param existingServiceRefItem
	 * @param jobResponse
	 * @param jsonResponse 
	 * @param ignoreRetryCount 
	 * @param jsonReqStr 
	 * @return 
	 */
	private int updatePorchResponseinServiceRef(RepositoryItem existingServiceRefItem, PorchJobVO jobResponse, String bookAJobRequest, String jsonResponse, boolean ignoreRetryCount) {
		
		if(isLoggingDebug()){
			logDebug("Method start updating porch service response "+getClass().getName() +".updatePorchResponseinServiceRef()");
		}
		
		MutableRepositoryItem serviceReferalItem = (MutableRepositoryItem)existingServiceRefItem;
		int retryCount=0;
		MutableRepository orderRepository = (MutableRepository) getOrderManager().getOrderTools().getOrderRepository();
		try {
			
			if(null!=jobResponse && jobResponse.isSuccess()){				
				serviceReferalItem.setPropertyValue(PORCH_JOB_ID, String.valueOf(jobResponse.getJobId()));
				serviceReferalItem.setPropertyValue(PORCH_PROJECT_ID, String.valueOf(jobResponse.getProjectLifecycleId()));
				serviceReferalItem.setPropertyValue(PORCH_JOB_STATUS, PORCH_JOB_STATUS_IN_PROGRESS);
			}
					
			
			if(null!=existingServiceRefItem.getPropertyValue(PORCH_RETRY_COUNT)){
				retryCount=	(int) existingServiceRefItem.getPropertyValue(PORCH_RETRY_COUNT);
			}
			
			if(null==jobResponse || (null!=jobResponse &&  !StringUtils.isBlank(jobResponse.getErrorCode()))){
				serviceReferalItem.setPropertyValue(PORCH_JOB_STATUS, PORCH_JOB_STATUS_FAILED);
				if(!ignoreRetryCount)
				serviceReferalItem.setPropertyValue(PORCH_RETRY_COUNT, retryCount++);
			}
			 
			serviceReferalItem.setPropertyValue(PORCH_BOOK_A_JOB_REQ, bookAJobRequest);
			serviceReferalItem.setPropertyValue(PORCH_BOOK_A_JOB_RES, jsonResponse);
			serviceReferalItem.setPropertyValue(PORCH_CHANGE_DATE, new Date());	
			orderRepository.updateItem(serviceReferalItem);
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError("Error while updating porch service details "+e,e);
			}
		}
		if(isLoggingDebug()){
			logDebug("Method end updating porch service response "+getClass().getName() +".updatePorchResponseinServiceRef()");
		}
		 
		return retryCount;
	}
	
	
 
	/**
	 * @param existingServiceRefItem
	 * @return
	 */
	private int updateRetryCount(RepositoryItem existingServiceRefItem) {
		
		if(isLoggingDebug()){
			logDebug("Method start updating retry count porch service api call "+getClass().getName() +".updateRetryCount()");
		}
		
		MutableRepositoryItem serviceReferalItem = null;
		int retryCount=0;
		MutableRepository orderRepository = (MutableRepository) getOrderManager().getOrderTools().getOrderRepository();
		try {
			serviceReferalItem = orderRepository.getItemForUpdate(existingServiceRefItem.getRepositoryId(),
					SERVICE_REFERAL_ITEM);

			
			if(null!=serviceReferalItem.getPropertyValue(PORCH_RETRY_COUNT)){
				retryCount=(int) serviceReferalItem.getPropertyValue(PORCH_RETRY_COUNT);
			}
			
			retryCount++;
			serviceReferalItem.setPropertyValue(PORCH_RETRY_COUNT, retryCount);
			serviceReferalItem.setPropertyValue(PORCH_CHANGE_DATE, new Date());	
			orderRepository.updateItem(serviceReferalItem);
			
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError("Error while updating porch service details "+e,e);
			}
		}
		
		
		if(isLoggingDebug()){
			logDebug("Method end updating retry count porch service api call "+getClass().getName() +".updateRetryCount()");
		}
		return retryCount;
	}

	/**
	 * @param schedulerStartDate
	 * @param dcPrefix 
	 * @return
	 */
	public RepositoryItem[] getPorchOrders(Timestamp schedulerStartDate, String dcPrefix) {	
		
		if(isLoggingDebug()){
			logDebug("Method start get porch orders  "+getClass().getName() +".getPorchOrders()");
		}
		
		try {
			RepositoryView rv = getOrderManager().getOrderTools().getOrderRepository().getView(BBBCoreConstants.ORDER);
			QueryBuilder qb = rv.getQueryBuilder();
			Query dateQuery=null;
			QueryExpression property = qb.createPropertyQueryExpression(BBBCoreConstants.PROPERTY_LAST_MODIFIED_DATE);
			QueryExpression dateToday=qb.createConstantQueryExpression(schedulerStartDate);
			dateQuery = qb.createComparisonQuery(property,dateToday,QueryBuilder.GREATER_THAN_OR_EQUALS);			
			QueryExpression stateproperty = qb.createPropertyQueryExpression(BBBCoreConstants.STATE_ORDER_PROPERTY_NAME);
			QueryExpression orderStatus=qb.createConstantQueryExpression(BBBCoreConstants.ORDER_SUBSTATUS_SUBMITTED);
			Query stateQuery = qb.createComparisonQuery(stateproperty,orderStatus,QueryBuilder.EQUALS);
			
			QueryExpression dcPrefixProperty = qb.createPropertyQueryExpression(PropertyNameConstants.CREATEDBYORDERID);
			QueryExpression dcPrefixQueryExpression =qb.createConstantQueryExpression(dcPrefix);
			Query dcPrefixQuery = qb.createPatternMatchQuery(dcPrefixProperty,dcPrefixQueryExpression,QueryBuilder.STARTS_WITH);
			
			Query[] andQuery = { dateQuery, stateQuery, dcPrefixQuery};
			Query query = qb.createAndQuery(andQuery);
			
			if(isLoggingDebug()){
				logDebug("porch order Query  "+query.toString());
			}
			
			RepositoryItem[] orders = rv.executeQuery(query);

			return orders;
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError("Error while loading porch orders "+e,e);
			}
		}
		
		if(isLoggingDebug()){
			logDebug("Method end get porch orders  "+getClass().getName() +".getPorchOrders()");
		}
		return null;
	}
	
	
	  
 
	/**
	 * @param porchJob
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public PorchBookAJobResponseVO invokeBookAJobAPI(JSONObject porchJob) throws  BBBSystemException, BBBBusinessException {
		
		
		if(isLoggingDebug()){
			logDebug("Method start invoke book a job api call of porch  "+getClass().getName() +".invokeBookAJobAPI()");
		}
		
		
		logDebug("BBBPorchManager.invokeBookAJobAPI method called");
		
		
		HashMap<String, String> headerParam = new HashMap<String, String>();
		String authtoken = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.PORCH_CONFIG_KEY).get(BBBCoreConstants.PORCH_X_Auth_Token);
		String contentType = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.PORCH_CONFIG_KEY).get(BBBCoreConstants.PORCH_CONTENT_TYPE);
		String bookAJobURL=getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.PORCH_CONFIG_KEY).get(BBBCoreConstants.PORCH_BOOK_A_JOB_API);
		
		
		headerParam.put(BBBCoreConstants.PORCH_X_Auth_Token, authtoken);
		headerParam.put(BBBCoreConstants.PORCH_CONTENT_TYPE, contentType);	
		HashMap<String, String> serviceParameters = new HashMap<String, String>();
		serviceParameters.put(BBBCoreConstants.STATUS, BBBCoreConstants.LOCKED);		
		serviceParameters.put(PORCH_JSON_CONSTNAT, porchJob.toString());
	       
	    PorchBookAJobResponseVO responseVO = getHttpCallInvoker().invokeToGetJson(PorchBookAJobResponseVO.class, headerParam,bookAJobURL, serviceParameters, PORCH_CONSTNAT);
	        
	   
	    
	    if(isLoggingDebug()){
	    	 logDebug("The book a job proch order status is " + responseVO.toString());
			logDebug("Method end invoke book a job api call of porch  "+getClass().getName() +".invokeBookAJobAPI()");
		}
	    return responseVO;
		
	}
	 
	

	/**
	 * @param object 
	 * @param string 
	 * @param porchJob
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("static-access")
	public PorchSchedlueJobResponseVO invokeScheduleAJobAPI(String jobId, String shippmentURL, Object shippmentDate) throws  BBBSystemException, BBBBusinessException {
		
		 
		if(isLoggingDebug()){
			logDebug("Method start invoke schedule a job api call of porch  "+getClass().getName() +".invokeScheduleAJobAPI()");
		}
		
		
		HashMap<String, String> headerParam = new HashMap<String, String>();
		String authtoken = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.PORCH_CONFIG_KEY).get(BBBCoreConstants.PORCH_X_Auth_Token);
		String contentType = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.PORCH_CONFIG_KEY).get(BBBCoreConstants.PORCH_CONTENT_TYPE);
		String bookAJobURL=getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.PORCH_CONFIG_KEY).get(BBBCoreConstants.PORCH_BOOK_A_JOB_API);
		
		
		headerParam.put(BBBCoreConstants.PORCH_X_Auth_Token, authtoken);
		headerParam.put(BBBCoreConstants.PORCH_CONTENT_TYPE, contentType);
		
		HashMap<String, String> serviceParameters = new HashMap<String, String>();
		serviceParameters.put(BBBCoreConstants.STATUS, BBBCoreConstants.LOCKED);
		String URL = bookAJobURL+"/"+jobId+"/schedule";
		
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
 		
 		 
		if(null==shippmentDate){
			 gregorianCalendar.setTime(new Date(System.currentTimeMillis()));
			 GregorianCalendar gcDate = new GregorianCalendar();
      		 gcDate.setTime(new Date());
      		 XMLGregorianCalendar date2 =null;
      		 try {
      			  date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcDate);
      			 shippmentDate=date2;
      		} catch (DatatypeConfigurationException e1) {
      			if(isLoggingError()){
      				logError("Error while te to XMLGregorianCalendar "+ e1,e1);
      			}
      		}
		}
		 
		
		XMLGregorianCalendar sendTime = (XMLGregorianCalendar) shippmentDate;	 
		
		final Long time = sendTime.toGregorianCalendar().getInstance().getTimeInMillis();
		final Timestamp schedulerStartDate = new Timestamp(time);
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String sendTimeInUTC = formatter.format(schedulerStartDate);
	    serviceParameters.put(PORCH_SCHEDULED_DATE, sendTimeInUTC);
		serviceParameters.put(PORCH_TRACKING_URL, shippmentURL);
	       
		PorchSchedlueJobResponseVO responseVO = getHttpCallInvoker().invokeToGetJson(PorchSchedlueJobResponseVO.class, headerParam,URL, serviceParameters, "put");
	        
	   
	    if(isLoggingDebug()){
	    	 logDebug("The schedule a job proch order status is " + responseVO.toString());
			logDebug("Method end invoke schedule a job api call of porch  "+getClass().getName() +".invokeScheduleAJobAPI()");
		}
	    return responseVO;
		
	}
	
	
	public Object invokeValidateZipCodeAPI(String commItemShippingCode, String porchServiceFamilycode) throws BBBSystemException, BBBBusinessException{
		
		if(isLoggingDebug()){
			logDebug("Method start Creating or updating porch service ref "+getClass().getName() +".invokeValidateZipCodeAPI()");
		}
		HashMap<String, String> headerParam = new HashMap<String, String>();
		String authtoken = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.PORCH_CONFIG_KEY).get(BBBCoreConstants.PORCH_X_Auth_Token);
		String contentType = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.PORCH_CONFIG_KEY).get(BBBCoreConstants.PORCH_CONTENT_TYPE);
		String porchServiceFamilyAPIURL=getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.PORCH_CONFIG_KEY).get(BBBCoreConstants.PORCH_VALIDATE_ZIPCODE_API);
		
		headerParam.put(BBBCoreConstants.PORCH_X_Auth_Token, authtoken);
		headerParam.put(BBBCoreConstants.PORCH_CONTENT_TYPE, contentType);
		
		HashMap<String, String> serviceParameters = new HashMap<String, String>();
		serviceParameters.put(BBBCoreConstants.STATUS, BBBCoreConstants.LOCKED);
	
		String URL = porchServiceFamilyAPIURL+"/"+porchServiceFamilycode+"?"+PORCH_POSTAL_CODE+"="+commItemShippingCode;
		Object  responseVO=null;
		
		try {
			  responseVO = getHttpCallInvoker().invokeToGetJson(PorchValidateZipCodeResponseVO.class, headerParam,URL, serviceParameters, "get");
			 
		} catch (BBBSystemException | BBBBusinessException e) {
			if(isLoggingError()){
				logError("Error invoking validateZipCode porch api "+e,e);
			}
		}
		
		if(isLoggingDebug()){
			logDebug("Method end   "+getClass().getName() +".invokeValidateZipCodeAPI()");
		}
		return responseVO;
	}
	
	
	/**
	 * @param jsonArr
	 * @param string 
	 * @param shippingAddress2
	 * @param contactInfo 
	 * @param billingPhoneNumber 
	 * @param jsonPostalCode 
	 * @param string 
	 * @return
	 */
	public JSONObject buildBookJobJson(JSONArray jsonArr,  RepositoryItem shippingAddress2, BBBOrder order, String jsonPostalCode) {
		
		 
		if(isLoggingDebug()){
			logDebug("Method start marshall book a job json  "+getClass().getName() +".buildBookJobJson()");
		}
		   String billingPhoneNumber=order.getBillingAddress().getMobileNumber();
	       if(StringUtils.isBlank(billingPhoneNumber)){
	    	   billingPhoneNumber=order.getBillingAddress().getPhoneNumber();
	       }
	       BBBRepositoryContactInfo contactInfo = order.getShippingAddress();
	       String orderNumber = order.getOnlineOrderNumber();
	       if(StringUtils.isBlank(orderNumber)){
	    	   orderNumber=order.getBopusOrderNumber();
	       }
	       JSONObject bookJobJson= new JSONObject();
		
		try {
			bookJobJson.put(PORCH_PARTNER_ORDER_ID, orderNumber);
			bookJobJson.put(PORCH_DISCOUNT_CODE, new String());
			JSONObject customerInfo=new JSONObject();			
			RepositoryItem profile = null;
			String firstName=null;
			String lastName=null;
			String email=null;
			try {
				if(!StringUtils.isBlank(order.getBopusOrderNumber())){					 
					  firstName= order.getBillingAddress().getFirstName();					  
				       lastName= order.getBillingAddress().getLastName();				     
				       email=order.getBillingAddress().getEmail();
				      
				}
				else {
				profile = getOrderManager().getOrderTools().getProfileTools().getProfileItem(order.getProfileId());
				  firstName= profile==null? null: (String) profile.getPropertyValue(BBBCoreConstants.FIRST_NAME);
				 if(StringUtils.isBlank(firstName) && null!=contactInfo){
					 firstName=contactInfo.getFirstName();
				 }
			       lastName= profile==null? null:(String) profile.getPropertyValue(BBBCoreConstants.LAST_NAME);
			     if(StringUtils.isBlank(lastName) && null!=contactInfo){
			    	 lastName=contactInfo.getLastName();
			     }
			       email=profile==null? null:(String) profile.getPropertyValue(BBBCoreConstants.EMAIL);
			     if(StringUtils.isBlank(email) ){
			    	 email=order.getBillingAddress().getEmail();
			     }
				}
			      
			     
			     if(StringUtils.isBlank(billingPhoneNumber) && !StringUtils.isBlank((String) profile.getPropertyValue(PHONE_NUMBER))){
			    	   billingPhoneNumber= (String) profile.getPropertyValue(PORCH_PHONE_NUMBER);
			     }
			     else if(StringUtils.isBlank(billingPhoneNumber) &&  null!=contactInfo && !StringUtils.isBlank((String) contactInfo.getMobileNumber())){
			    	   billingPhoneNumber= (String) contactInfo.getMobileNumber();
			     }
			     else if(StringUtils.isBlank(billingPhoneNumber) &&  null!=contactInfo && !StringUtils.isBlank((String) contactInfo.getPhoneNumber())){
			    	   billingPhoneNumber= (String) contactInfo.getPhoneNumber();
			     }
				customerInfo.put(BBBCoreConstants.FIRST_NAME, firstName);
				customerInfo.put(BBBCoreConstants.LAST_NAME, lastName);
				customerInfo.put(BBBCoreConstants.EMAIL, email);
				customerInfo.put(PORCH_PHONE_NUMBER, billingPhoneNumber);
				customerInfo.put(PORCH_PARTNER_CUSTMERID, order.getProfileId());
			} catch (RepositoryException e) {
				if(isLoggingError()){
					logError("erro while getting profile from order for porch orders "+e,e);
				}
			}
			 JSONObject deliveryAddress = new JSONObject(); 
			 if(null!=shippingAddress2){
				 deliveryAddress.put(PORCH_ADDRESS_STREET1, shippingAddress2.getPropertyValue(PORCH_ADDRESS1));
				 deliveryAddress.put(PORCH_ADDRESS_STREET2, shippingAddress2.getPropertyValue(PORCH_ADDRESS2));
				 deliveryAddress.put(PORCH_ADDRESS_CITY, shippingAddress2.getPropertyValue(PORCH_ADDRESS_CITY));
				 deliveryAddress.put(PORCH_ADDRESS_STATE, shippingAddress2.getPropertyValue(PORCH_ADDRESS_STATE));
				 String zipCode = (String) shippingAddress2.getPropertyValue(PORCH_ADDRESS_POSTALCODE);
				 String[] splitZipCode= zipCode.split("-");
				 deliveryAddress.put(PORCH_ADDRESS_POSTALCODE, splitZipCode[0]);
			 }
			 else if(null!=contactInfo){
			 deliveryAddress.put(PORCH_ADDRESS_STREET1, contactInfo.getAddress1());
			 deliveryAddress.put(PORCH_ADDRESS_STREET2, contactInfo.getAddress2());
			 deliveryAddress.put(PORCH_ADDRESS_CITY, contactInfo.getCity());
			 deliveryAddress.put(PORCH_ADDRESS_STATE, contactInfo.getState());
			 String zipCode = contactInfo.getPostalCode();
			 String[] splitZipCode= zipCode.split("-");
			 deliveryAddress.put(PORCH_ADDRESS_POSTALCODE, splitZipCode[0]);
			 
			
			 }  
			 
			 if(!StringUtils.isBlank(jsonPostalCode)){
				 deliveryAddress.put(PORCH_ADDRESS_POSTALCODE, jsonPostalCode);
			 }
			 customerInfo.put(PORCH_DELIVERY_ADDRESS, deliveryAddress);
			 bookJobJson.put(PORCH_CUSTOMER,customerInfo);
			 
			 bookJobJson.put(PORCH_JOBS,jsonArr);
			
		} catch (JSONException e) {
			if(isLoggingError()){
				logError("Error while creating json request for porch order "+e,e);
			}
		}
		if(isLoggingDebug()){
			logDebug("Method end marshall book a job json  "+getClass().getName() +".buildBookJobJson()");
		}
		return bookJobJson;
	}


	 
	
	/**
	 * @param schedulerStartDate
	 * @param fullDataFeed
	 * @param typeOfFeed
	 * @param status
	 */
	/**
	 * @param schedulerStartDate
	 * @param fullDataFeed
	 * @param typeOfFeed
	 * @param status
	 */
	public final void updateScheduledRepository(final Timestamp schedulerStartDate, final String typeOfFeed, final boolean status) {
		logDebug("PorchServiceManager Method : updateScheduledRepository");
		logDebug("scheduler Start Date :" + schedulerStartDate + " typeOfFeed: "
					+ typeOfFeed + " status:" + status);
		final Date schedulerEndDate = new Date();
		try {
			final MutableRepositoryItem repItem = this.getScheduledRepository().createItem(
			        FEED);

			repItem.setPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE, schedulerStartDate);
			repItem.setPropertyValue(SCHEDULER_COMPLETION_DATE, schedulerEndDate);
			repItem.setPropertyValue(SCHEDULER_START_DATE, schedulerStartDate);
			repItem.setPropertyValue(STATUS, Boolean.valueOf(status));
			repItem.setPropertyValue(TYPE_OF_FEED, typeOfFeed);
			repItem.setPropertyValue(MODE, "full");
			this.getScheduledRepository().addItem(repItem);

		} catch (RepositoryException e) {

			logError(LogMessageFormatter.formatMessage(null,
						"PorchServiceManager.updateScheduledRepository() | RepositoryException ", "catalog_1072"), e);

		}
	}
	 
	

	/**
	 * @param order
	 * @param responseVO
	 * @param bookAJobjsonReq
	 */
	@SuppressWarnings("unchecked")
	public void updatePorchOrderStatus(BBBOrder order, PorchBookAJobResponseVO responseVO, String bookAJobjsonReq,boolean ignoreRetryCount) {
		
		if(isLoggingDebug()){
			logDebug("Method start update porch order status  "+getClass().getName() +".updatePorchOrderStatus()");
		}
		
		
		 int retryCount=0;
		 final List<CommerceItem> comItemObj = order.getCommerceItems();
		if(null==responseVO){		   
			updatePorchOrderFailureStatus(order, bookAJobjsonReq,ignoreRetryCount);
			return;	  
		}
		int bookAjobFailed=0;
		List<PorchJobVO> jobResponses = responseVO.getJobs();
		   try { 
			   ObjectMapper ob = new ObjectMapper();
			   String bookAJobjsonRes = ob.writeValueAsString(responseVO);
				if(null!=jobResponses){
					   for(PorchJobVO jobResponse:jobResponses){
						   String skuId = jobResponse.getPartnerSku();
						   	if(!StringUtils.isBlank(skuId) ){							  							   					   
							   for(CommerceItem porchCommItem:comItemObj){	
								   BaseCommerceItemImpl commerceItemImpl =(BaseCommerceItemImpl) porchCommItem;
								   String porchCommSkuId=(String) porchCommItem.getCatalogRefId() ;
								   if(skuId.equalsIgnoreCase(porchCommSkuId)){
									   RepositoryItem serviceRefItem = commerceItemImpl.getPorchServiceRef();
									     retryCount = updatePorchResponseinServiceRef(serviceRefItem,jobResponse,bookAJobjsonReq,bookAJobjsonRes,ignoreRetryCount);
									    
									    if(!ignoreRetryCount && retryCount!=0 && retryCount<=3){
											   updatePorchOrderLastModifiedDate(order);
										   }
									    else if(StringUtils.isBlank(jobResponse.getErrorCode()) && jobResponse.isSuccess()){
									    	ShippingGroup shGroup = ((BBBShippingGroupCommerceItemRelationship)porchCommItem.getShippingGroupRelationships().get(0)).getShippingGroup();
									    	if(shGroup instanceof BBBStoreShippingGroup && order.isBopusOrder()){
									    		callScheduleJob(jobResponse,serviceRefItem);
									    	}
									    	else {
									    		BBBHardGoodShippingGroup shipinggroup = (BBBHardGoodShippingGroup) shGroup;
											     String shippingMethod = shipinggroup.getShippingMethod();
											     if(shippingMethod.equalsIgnoreCase(BBBCoreConstants.SDD)){			 
													 callScheduleJob(jobResponse,serviceRefItem);
												  }
									    	}
									    }
								   }
								  
							   }
					   }
					   else if(!jobResponse.isSuccess()){
						   bookAjobFailed++;
						   	}
					   }
				}
				 
		   } catch (Exception e) {
				 if(isLoggingError()){
					 logError("Error updating porch order info "+e,e);
				 }
				}  
		  
		   if(bookAjobFailed!=0 && jobResponses.size()==1){
			    
				for(CommerceItem porchCommItem:comItemObj){
					   BaseCommerceItemImpl commerceItemImpl =(BaseCommerceItemImpl) porchCommItem;
					   if(commerceItemImpl.isPorchService()){
						   RepositoryItem serviceRefItem = commerceItemImpl.getPorchServiceRef();
						   String bookAJobjsonRes;
						try {
							bookAJobjsonRes = new ObjectMapper().writeValueAsString(responseVO);
							updatePorchResponseinServiceRef(serviceRefItem,jobResponses.get(0),bookAJobjsonReq,bookAJobjsonRes,ignoreRetryCount);
						} catch (JsonGenerationException e) {
							if(isLoggingError()){
								logError("Error while generating json for Porch error response "+e,e);
							}
						} catch (JsonMappingException e) {
							if(isLoggingError()){
							logError("Error while generating json for Porch error response "+e,e);
							}
						} catch (IOException e) {
							if(isLoggingError()){
							logError("Error while generating json for Porch error response "+e,e);
							}
						}
						    
						   
					   }
				}
		   }
	}



	/**
	 * @param order
	 * @param bookAJobjsonReq
	 * @param ignoreRetryCount 
	 */
	@SuppressWarnings("unchecked")
	private void updatePorchOrderFailureStatus(BBBOrder order, String bookAJobjsonReq, boolean ignoreRetryCount) {
		
		if(isLoggingDebug()){
			logDebug("Method start update porch order failure status  "+getClass().getName() +".updatePorchOrderFailureStatus()");
		}
		
		int retryCount=0;
		final List<CommerceItem> comItemObj = order.getCommerceItems();
		for(CommerceItem porchCommItem:comItemObj){
			   BaseCommerceItemImpl commerceItemImpl =(BaseCommerceItemImpl) porchCommItem;
			   if(commerceItemImpl.isPorchService()){
			   RepositoryItem serviceRefItem = commerceItemImpl.getPorchServiceRef();
			   if(null!=serviceRefItem){
				  if(!ignoreRetryCount) 
			     retryCount=updateRetryCount(serviceRefItem);
			     updatePorchResponseinServiceRef(serviceRefItem,null,bookAJobjsonReq,null,ignoreRetryCount);
			   }
			   }
		   }
		 
		   if(retryCount<3 && !ignoreRetryCount){
		   updatePorchOrderLastModifiedDate(order);
		   }
		   return;
	}



	/**
	 * @param order
	 */
	private void updatePorchOrderLastModifiedDate(BBBOrder order) {
		try {
				
				synchronized (order) {
					
					final Calendar currentDate = Calendar.getInstance();						
					order.setLastModifiedTime(currentDate.getTimeInMillis());
					getOrderManager().updateOrder(order);
				}
		 }
		 catch(Exception e1){
			 logError("error while updating order date "+e1,e1);
		 }
	}

	/**
	 * @param sddPorchResponse
	 * @param serviceRefItem 
	 */
	private void callScheduleJob(PorchJobVO sddPorchResponse, RepositoryItem serviceRefItem) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		 gregorianCalendar.setTime(new Date(System.currentTimeMillis()));
	 	 
		 GregorianCalendar gcDate = new GregorianCalendar();
		 gcDate.setTime(new Date());
		 XMLGregorianCalendar date2 =null;
		 try {
			  date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcDate);
		} catch (DatatypeConfigurationException e1) {
			if(isLoggingError()){
				logError("Error while parseing date to XMLGregorianCalendar "+ e1,e1);
			}
		}
		 
		 
		 String jobId = String.valueOf(sddPorchResponse.getJobId());
		  try {
			PorchSchedlueJobResponseVO scheduleJobRes=invokeScheduleAJobAPI(jobId,"",date2);
			if(null!=scheduleJobRes && !StringUtils.isBlank(scheduleJobRes.getJobId())){
	    		   updateScheduledJobResponse("jobStatus", "SCHEDULED", serviceRefItem);
	    	   }
			else {
				updateScheduledJobResponse("jobStatus", "SCHEDULED_FAILED", serviceRefItem);
			}
		} catch (BBBSystemException e) {
			if(isLoggingError()){
				logError("Eror while invoking schedule a job api of porch "+e,e);
			}
		} catch (BBBBusinessException e) {
			if(isLoggingError()){
				logError("Eror while invoking schedule a job api of porch "+e,e);
			}
		}
	}
	
	
 
	
	/**
	 * @param productVO
	 * @param pProductId
	 * @param productVO 
	 * @return 
	 */
	public  List<String> getPorchServiceFamilyCodes(String pProductId, ProductVO productVO) {
		
		
		if(isLoggingDebug()){
			logDebug("Method start to get porch service family list for product  "+getClass().getName() +".getPorchServiceFamilyCodes()");
		}
		
		List<String> porchServiceTypeCodes = new ArrayList<String>();
		if(StringUtils.isBlank(pProductId)){
			return porchServiceTypeCodes;
		}
		
		
		List<String> porchServiceFamilyType=new ArrayList<String>();
		BBBCatalogToolsImpl ctools =(BBBCatalogToolsImpl) getCatalogTools();
		RepositoryItem productItem=null;
		try {
			productItem = ctools.getCatalogRepository().getItem(pProductId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		} catch (RepositoryException e1) {
			if(isLoggingError()){
				logError("Error hile fetching productItem "+e1,e1);
			}
		}
		if(null!=productItem){			
		String productCategoryList = (String) productItem.getPropertyValue(PORCH_CATEGORYLIST);
		List<RepositoryItem> serviceFamilyList = getCheckListTools().getServiceFamilyListBySubType(PORCH_SUBTYPE_CODE);
		
		if(null!=serviceFamilyList && !serviceFamilyList.isEmpty()){
		for(RepositoryItem serviceFamily:serviceFamilyList){
			
			String serviceFamilyId = serviceFamily.getRepositoryId();
			
			if(null!=productCategoryList && productCategoryList.contains(serviceFamilyId)){
				String categoryName =(String) serviceFamily.getPropertyValue(CATEGORY_NAME);
				String serviceTypeCode =(String) serviceFamily.getPropertyValue(PORCH_SERVICETYPECODE);
				porchServiceTypeCodes.add(serviceTypeCode);
				porchServiceFamilyType.add(categoryName);
			}
			
		}
		}
		}
		
		if(null!=productVO && !porchServiceTypeCodes.isEmpty()){			 
				productVO.setPorchServiceFamilyCodes(porchServiceTypeCodes);
				productVO.setPorchServiceFamilyType(porchServiceFamilyType.get(0));			 
		}
		
		if(isLoggingDebug()){
			logDebug("Porch Service Family codes for the product "+pProductId + " "+porchServiceTypeCodes.toString());
		}
		return porchServiceTypeCodes;
	}

	
	
	/**
	 * @param porchOrders
	 * @param string 
	 */
	public void sendPorchOrdersReportTibcco(RepositoryItem[] porchOrders, String dcPrefix) {
		
		if(isLoggingDebug()){
			logDebug("Method start to send porch orders details to EDW  "+getClass().getName() +".sendPorchOrdersReportTibcco() "+porchOrders.toString());
		}
		
		if(null!=porchOrders){
			 
			 
			for(RepositoryItem porchOrder:porchOrders){			
				try {
					BBBOrder order = (BBBOrder) getOrderManager().loadOrder(porchOrder.getRepositoryId());
					boolean isPorchOrder=false;
					
					isPorchOrder = isPorchServiceOrder(order);
					
					if(isPorchOrder){
						 
						String orderNumber = order.getOnlineOrderNumber();
						if(StringUtils.isBlank(orderNumber)){
							orderNumber = order.getBopusOrderNumber();
						}
						JSONObject porchOrdersJson = new JSONObject();
						
						porchOrdersJson.put(PORCH_REPORT_SERVICE_INFO, createPorchOrderJson(order));
						PorchServiceOrderVO porchServiceOrderVO = new PorchServiceOrderVO();
						PorchServiceHeader  serviceHeader = new PorchServiceHeader();
						serviceHeader.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
						serviceHeader.setApplicationName(PORCH_REPORT_APPLICATION_NAME);
						serviceHeader.setHostName(InetAddress.getLocalHost().getHostName());
						serviceHeader.setDataCenterName(dcPrefix);
						porchServiceOrderVO.setHeader(serviceHeader);
						porchServiceOrderVO.setCreationDate(new Date());
						porchServiceOrderVO.setServiceName(PORCH_ORDER_TIBCO_MSG);
						porchServiceOrderVO.setJsonData(porchOrdersJson.toString());
						
						try {
							sendPorchOrderFeedXMLStringToEDW(porchServiceOrderVO);
						} catch (BBBBusinessException e) {
							if(isLoggingError()){
								logError("Error while sending order :"+orderNumber +"to  "+e,e);
							}
						} catch (BBBSystemException e) {
							logError("Error while sending order :"+orderNumber +"to Tibcco "+e,e);
						}
						
					}
				} catch (CommerceException e) {
					if(isLoggingError()){
						logError("Porch Service Order Report to EDW:   while invoking book a job for proch order "+e,e);
					}
				} catch (UnknownHostException e) {
					if(isLoggingError()){
						logError("Porch Service Order Report to EDW:   while invoking book a job for proch order "+e,e);
					}
				} catch (BBBSystemException e) {
					if(isLoggingError()){
						logError("Porch Service Order Report to EDW:   while invoking book a job for proch order "+e,e);
					}
				} catch (BBBBusinessException e) {
					if(isLoggingError()){
						logError("Porch Service Order Report to EDW:   while invoking book a job for proch order "+e,e);
					}
				}
			}
			
		}
		 
	}
	
	
	
	/**
	 * @param porchServiceOrder
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	public void sendPorchOrderFeedXMLStringToEDW(PorchServiceOrderVO porchServiceOrder) throws BBBBusinessException, BBBSystemException {
		String methodName = BBBCoreConstants.PORCH_ORDER_REPORT_TIBCO_CALL;
		logDebug(methodName + " method started");
		if(isLoggingDebug()){
			logDebug("Method start to send porch order details to EDW  "+getClass().getName() +".sendPorchOrderFeedXMLStringToEDW()  "+porchServiceOrder.toString());
		}
		 		

		BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL,
				methodName);

		try {
			ServiceHandlerUtil.send(porchServiceOrder);

		} catch (BBBSystemException ex) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL,
					methodName);	
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in  Service  manager while sendPorchOrderFeedXMLString", BBBCoreErrorConstants.ACCOUNT_ERROR_1218 ), ex);
			throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1389,ex.getMessage(), ex);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL,
					methodName);			
		}
		
		if(isLoggingDebug()){
		logDebug(methodName + " method ends");
		}
	}
	
	
	
	/**
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject createPorchOrderJson(BBBOrder order) {
		
		JSONObject bbbOrder = new JSONObject();
		String orderNumber = order.getOnlineOrderNumber();
		if(StringUtils.isBlank(orderNumber)){
			orderNumber = order.getBopusOrderNumber();
		}
		if(isLoggingDebug()){
			logDebug("Method start to create json for porch order  "+getClass().getName() +".createPorchOrderJson() for order id "+orderNumber);
		}
		bbbOrder.put(ORDER_NUMBER,orderNumber);
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        String formattedDate = formatter.format(order.getSubmittedDate());
		bbbOrder.put(ORDER_SUBMIT_DATE, formattedDate);
		bbbOrder.put(ORDER_STATE, StateDefinitions.ORDERSTATES.getStateString(order.getState()));
		if(null!=order.getPriceInfo()){
			bbbOrder.put(ORDER_AMOUNT, order.getPriceInfo().getTotal());
		}
		else{
			bbbOrder.put(ORDER_AMOUNT, "");
		}
		
		
		
		List<ShippingGroup> shippingGroups = order.getShippingGroups();
		JSONArray jsonCommerceItems= new JSONArray();
		
		JSONArray shippingGroupInfo= new JSONArray();
		JSONObject jsonShippingGroups= new JSONObject();
		 
		for(ShippingGroup shippingGroup:shippingGroups){
			JSONObject shippingJsonObject= new JSONObject();
			JSONObject commerceItemInfo = new JSONObject();
			shippingJsonObject.put(SHIPPING_GROUPID, shippingGroup.getId());
			
			List<ShippingGroupCommerceItemRelationship> commItemRelationships = shippingGroup.getCommerceItemRelationships();
			jsonCommerceItems=new JSONArray();
			for(ShippingGroupCommerceItemRelationship shippingGroupRelationship:commItemRelationships){
				JSONObject jsonCommerceItemInfo= new JSONObject();
				CommerceItem bbbCommerceItem = shippingGroupRelationship.getCommerceItem();
				jsonCommerceItemInfo.put(COMMERCE_ITEM_ID, bbbCommerceItem.getId());
				jsonCommerceItemInfo.put(SKU_ID,  bbbCommerceItem.getCatalogRefId());
				String skuDisplayName = (String) ((RepositoryItem)bbbCommerceItem.getAuxiliaryData().getProductRef()).getItemDisplayName();
				skuDisplayName=StringEscapeUtils.unescapeHtml4(skuDisplayName);
				jsonCommerceItemInfo.put(SKU_DISPLAY_NAME, skuDisplayName.replaceAll("[^\\p{ASCII}]", ""));
				jsonCommerceItemInfo.put(UNIT_PRICE, bbbCommerceItem.getPriceInfo().getListPrice());
				jsonCommerceItemInfo.put(QTY_ORDERED, bbbCommerceItem.getQuantity());
				jsonCommerceItemInfo.put(LINE_ITEM_AMOUNT, bbbCommerceItem.getPriceInfo().getAmount());
				jsonCommerceItemInfo.put(ORDER_LINE_STATE, StateDefinitions.SHIPITEMRELATIONSHIPSTATES.getStateString(shippingGroupRelationship.getState()));
				 
				
			 	RepositoryItem productItem=(RepositoryItem) bbbCommerceItem.getAuxiliaryData().getCatalogRef();
			
				String displayName =null;
				String categoryName=null;
				if(null!=productItem){			
				String productCategoryList = (String) productItem.getPropertyValue(PORCH_CATEGORYLIST);
				List<RepositoryItem> serviceFamilyList = getCheckListTools().getServiceFamilyListBySubType(PORCH_SUBTYPE_CODE);
				
				if(null!=serviceFamilyList && !serviceFamilyList.isEmpty()){
				for(RepositoryItem serviceFamily:serviceFamilyList){					
					String serviceFamilyId = serviceFamily.getRepositoryId();					
					if(null!=productCategoryList && productCategoryList.contains(serviceFamilyId)){
						displayName =(String) serviceFamily.getPropertyValue("displayName");
						categoryName =(String) serviceFamily.getPropertyValue("categoryName");
					}
					
				}
				}
				}
				

				
				BaseCommerceItemImpl citem = (BaseCommerceItemImpl) bbbCommerceItem;
				boolean isPorchOrder = citem.isPorchService();
				//ServiceReferralInfo
				if(isPorchOrder){
				String projectId = (String) citem.getPorchServiceRef().getPropertyValue(PORCH_PROJECT_ID);
				String jobId =(String) (citem.getPorchServiceRef().getPropertyValue(PORCH_JOB_ID));
				String serviceFamilyCode =(String) (citem.getPorchServiceRef().getPropertyValue(PORCH_SERVICE_FAMILY_CODE));
				String jobPriceEstimation=(String) (citem.getPorchServiceRef().getPropertyValue(JOB_PRICE_ESTIMATION));				
				JSONObject porchServiceInfo = new JSONObject();
				
				porchServiceInfo.put(PROJECT_ID, projectId);
				porchServiceInfo.put(JOB_ID, jobId);
				porchServiceInfo.put(PORCH_SERVICE_TYPE, categoryName);
				porchServiceInfo.put(SERVICE_CATEGORY, displayName);
				porchServiceInfo.put(SERVICE_FAMILY_TYPE_CODE, serviceFamilyCode);				
				//porchServiceInfo.put(SERVICE_FAMILY_TYPE, ((BBBCommerceItem)citem).getPorchServiceType());
				porchServiceInfo.put(PRICE_RANGE, jobPriceEstimation);
				
				
				jsonCommerceItemInfo.put(SERVICE_REFERRAL_INFO, porchServiceInfo);
				commerceItemInfo.put(COMMERCE_ITEM_INFO, jsonCommerceItemInfo);
				jsonCommerceItems.add(commerceItemInfo);
				
			}
				
			
			}
			shippingJsonObject.put(PORCH_COMMERCE_ITEMS, jsonCommerceItems);
			shippingGroupInfo.add(shippingJsonObject);
			
			
			
		}
		jsonShippingGroups.put(SHIPPING_GROUP_INFO, shippingGroupInfo);
		bbbOrder.put(SHIPPING_GROUPS, jsonShippingGroups);
		
		if(isLoggingDebug()){
			logDebug("Method ends to create json for porch order  "+getClass().getName() +".createPorchOrderJson()  "+bbbOrder.toString());
		}
		return bbbOrder;
		
	}
	
	
	/**
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getRestrictedPorchServiceAddress(Order order) {
		
		
		if(isLoggingDebug()){
			logDebug("Method start to get restricted address for porch order items   "+getClass().getName() +".getRestrictedPorchServiceAddress() ");
		}
		Map<String, String> porchRestrictedAddress = new HashMap<String, String>();

		int shippingGroups = order.getShippingGroupCount();
		final List<CommerceItem> comItemObj = order.getCommerceItems();
		if (shippingGroups > 1) {
			List<ShippingGroup> shippingGroupsList = order.getShippingGroups();
			for (ShippingGroup porchShipingGroup : shippingGroupsList) {
				if (!(porchShipingGroup instanceof BBBStoreShippingGroup)) {
					BBBHardGoodShippingGroup shipinggroup = (BBBHardGoodShippingGroup) porchShipingGroup;

					String address1 = shipinggroup.getShippingAddress().getAddress1();
					String address2 = shipinggroup.getShippingAddress().getAddress2();
					String city = shipinggroup.getShippingAddress().getCity();
					String state = shipinggroup.getShippingAddress().getState();
					String zipCode = shipinggroup.getShippingAddress().getPostalCode();
					String restrictedShippingAddress = "";

					List<BBBShippingGroupCommerceItemRelationship> porchCommerceItems = porchShipingGroup
							.getCommerceItemRelationships();
					for (BBBShippingGroupCommerceItemRelationship porchCommerceItem : porchCommerceItems) {
						BaseCommerceItemImpl bbbItem = (BaseCommerceItemImpl) porchCommerceItem.getCommerceItem();
						if(bbbItem.isPorchService()){
						
						String[] commItemShippingCode = zipCode.split("-");
						RepositoryItem productItem = (RepositoryItem) bbbItem.getAuxiliaryData().getProductRef();
						List<String> porchServiceFamilycodes = getPorchServiceFamilyCodes(productItem.getRepositoryId(),
								null);
						Object responseVO = null;
						if (!StringUtils.isBlank(commItemShippingCode[0])
								&& !StringUtils.isBlank(porchServiceFamilycodes.get(0))) {

							try {
								responseVO = invokeValidateZipCodeAPI(commItemShippingCode[0],
										porchServiceFamilycodes.get(0));
								if (null == responseVO) {
									if (!StringUtils.isBlank(address1)) {
										restrictedShippingAddress = address1;
									}
									if (!StringUtils.isBlank(address2)) {
										restrictedShippingAddress = restrictedShippingAddress + "," + address2;
									}
									if (!StringUtils.isBlank(city)) {
										restrictedShippingAddress = restrictedShippingAddress + "," + city;
									}
									if (!StringUtils.isBlank(state)) {
										restrictedShippingAddress = restrictedShippingAddress + "," + state;
									}
									if (!StringUtils.isBlank(zipCode)) {
										restrictedShippingAddress = restrictedShippingAddress + "," + zipCode;
									}

								}
							} catch (BBBSystemException e) {
								if (isLoggingError()) {
									logError("invoking validateZipCode porch api " + e, e);
								}
							} catch (BBBBusinessException e) {
								if (isLoggingError()) {
									logError("invoking validateZipCode porch api " + e, e);
								}
							}
						}
						
						if(!StringUtils.isBlank(restrictedShippingAddress)){
							porchRestrictedAddress.put(shipinggroup.getId(), restrictedShippingAddress);
						}
					}
					}

				}
			}

		} else {
			BBBRepositoryContactInfo contactInfo = null;
			for (CommerceItem comerceItem : comItemObj) {
				boolean isPorchCommerceItem = ((BaseCommerceItemImpl) comerceItem).isPorchService();
				  if (isPorchCommerceItem ) {
					  
					   contactInfo = ((BBBOrder) order).getShippingAddress();
					   
						
						String[] commItemShippingCode = contactInfo.getPostalCode().split("-");
						RepositoryItem productItem = (RepositoryItem) comerceItem.getAuxiliaryData().getProductRef();
						List<String> porchServiceFamilycodes = getPorchServiceFamilyCodes(productItem.getRepositoryId(),
								null);
						Object responseVO = null;
						if (!StringUtils.isBlank(commItemShippingCode[0])
								&& !StringUtils.isBlank(porchServiceFamilycodes.get(0))) {
							try {
								responseVO = invokeValidateZipCodeAPI(commItemShippingCode[0],
										porchServiceFamilycodes.get(0));
								
								if (null == responseVO) {
									
									String restrictedShippingAddress = "";
									if (null != contactInfo) {
										if (!StringUtils.isBlank(contactInfo.getAddress1())) {
											restrictedShippingAddress = contactInfo.getAddress1();
										}
										if (!StringUtils.isBlank(contactInfo.getAddress2())) {
											restrictedShippingAddress = restrictedShippingAddress + "," + contactInfo.getAddress2();
										}
										if (!StringUtils.isBlank(contactInfo.getCity())) {
											restrictedShippingAddress = restrictedShippingAddress + "," + contactInfo.getCity();
										}
										if (!StringUtils.isBlank(contactInfo.getState()) && !contactInfo.getState().equalsIgnoreCase("initial")) {
											restrictedShippingAddress = restrictedShippingAddress + "," + contactInfo.getState();
										}
										if (!StringUtils.isBlank(contactInfo.getPostalCode())) {
											restrictedShippingAddress = restrictedShippingAddress + "," + contactInfo.getPostalCode();
										}
	
									}
									if(!StringUtils.isBlank(restrictedShippingAddress)){
										porchRestrictedAddress.put("singleshippingaddress", restrictedShippingAddress);
									}
									
									
									
									
								}
							}
							catch (BBBSystemException e) {
								if (isLoggingError()) {
									logError("invoking validateZipCode porch api " + e, e);
								}
							} catch (BBBBusinessException e) {
								if (isLoggingError()) {
									logError("invoking validateZipCode porch api " + e, e);
								}
							}
						}
					  }
				}
		}
		if(isLoggingDebug()){
			logDebug("Method ends to get restricted address for porch order items   "+getClass().getName() +".getRestrictedPorchServiceAddress() ");
		}
		return porchRestrictedAddress;

	}

	
	
	

	
	/**
	 * @param order
	 * @param orderVO 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void getRestrictedPorchServiceAddressMobile(Order order, BBBOrderVO orderVO) {
		
		if(isLoggingDebug()){
			logDebug("Method starts to get restricted address for porch order items for mobile   "+getClass().getName() +".getRestrictedPorchServiceAddressMobile() ");
		}
		Map<String, String> porchRestrictedAddress = new HashMap<String, String>();
			List<ShippingGroup> shippingGroupsList = order.getShippingGroups();
			for (ShippingGroup porchShipingGroup : shippingGroupsList) {
				if (!(porchShipingGroup instanceof BBBStoreShippingGroup)) {
					BBBHardGoodShippingGroup shipinggroup = (BBBHardGoodShippingGroup) porchShipingGroup;
					String source = ((BBBRepositoryContactInfo)shipinggroup.getShippingAddress()).getSource();
				
					String restrictedShippingAddress = "";

					List<BBBShippingGroupCommerceItemRelationship> porchCommerceItems = porchShipingGroup
							.getCommerceItemRelationships();
					for (BBBShippingGroupCommerceItemRelationship porchCommerceItem : porchCommerceItems) {
						BaseCommerceItemImpl bbbItem = (BaseCommerceItemImpl) porchCommerceItem.getCommerceItem();
						if(bbbItem.isPorchService()){
							if(!StringUtils.isBlank(source) && source.equalsIgnoreCase("registry")){
								BaseCommerceItemImpl cItemimpl=(BaseCommerceItemImpl) bbbItem;							
								cItemimpl.setPorchServiceRef(null);
								cItemimpl.setPorchService(false);
								orderVO.setPorchRegistrantAddressRemoved(true);
								 
							} else {
							String zipCode = shipinggroup.getShippingAddress().getPostalCode();
							
							if(!StringUtils.isBlank(zipCode)){
								String address1 = shipinggroup.getShippingAddress().getAddress1();
								String address2 = shipinggroup.getShippingAddress().getAddress2();
								String city = shipinggroup.getShippingAddress().getCity();
								String state = shipinggroup.getShippingAddress().getState();
								String[] commItemShippingCode = zipCode.split("-");
								RepositoryItem productItem = (RepositoryItem) bbbItem.getAuxiliaryData().getProductRef();
								List<String> porchServiceFamilycodes = getPorchServiceFamilyCodes(productItem.getRepositoryId(),
										null);
								Object responseVO = null;
								if (!StringUtils.isBlank(commItemShippingCode[0])
										&& !StringUtils.isBlank(porchServiceFamilycodes.get(0))) {
		
									try {
										responseVO = invokeValidateZipCodeAPI(commItemShippingCode[0],
												porchServiceFamilycodes.get(0));
										if (null == responseVO) {
											if (!StringUtils.isBlank(address1)) {
												restrictedShippingAddress = address1;
											}
											if (!StringUtils.isBlank(address2)) {
												restrictedShippingAddress = restrictedShippingAddress + "," + address2;
											}
											if (!StringUtils.isBlank(city)) {
												restrictedShippingAddress = restrictedShippingAddress + "," + city;
											}
											if (!StringUtils.isBlank(state)) {
												restrictedShippingAddress = restrictedShippingAddress + "," + state;
											}
											if (!StringUtils.isBlank(zipCode)) {
												restrictedShippingAddress = restrictedShippingAddress + "," + zipCode;
											}
		
										}
									} catch (BBBSystemException e) {
										if (isLoggingError()) {
											logError("invoking validateZipCode porch api " + e, e);
										}
									} catch (BBBBusinessException e) {
										if (isLoggingError()) {
											logError("invoking validateZipCode porch api " + e, e);
										}
									}
								}
							}
						}
						
						if(!StringUtils.isBlank(restrictedShippingAddress)){
							porchRestrictedAddress.put(shipinggroup.getId(), restrictedShippingAddress);
						}
					}
					}

				}
			}

		 
			if(!porchRestrictedAddress.isEmpty()){
				orderVO.setPorchRestrictedServiceAddress(porchRestrictedAddress);
			}
			
			if(isLoggingDebug()){
				logDebug("Method ends to get restricted address for porch order items for mobile   "+getClass().getName() +".getRestrictedPorchServiceAddressMobile() ");
			}
	}
	

	/**
	 * @param orderNumbers
	 */
	@SuppressWarnings("unchecked")
	public Map<String,List<CommerceItem>> processQASPorchOrders(List<String> orderNumbers){
		if(isLoggingDebug()){
			logDebug("Method starts to call book a job and schedule job apis for porch orders  "+getClass().getName() +".processQASPorchOrders()   "+orderNumbers.toString());
		}
		Map<String,List<CommerceItem>> ordersWithProjectId= new HashMap<String,List<CommerceItem>>();
		for(String orderNumber:orderNumbers){
			Order order=null;			
			BBBOrderTools orderTools = (BBBOrderTools) getOrderManager().getOrderTools();
			try {
					order = orderTools.getOrderFromOnlineOrBopusOrderNumber(orderNumber.trim());
				} catch (RepositoryException | CommerceException e) {
					if(isLoggingError()){
					logError("Error while geting porch orders for qas "+e,e);
					}
				}
			BBBOrder bbbOrder = (BBBOrder) order;
			if(null==order){
				ordersWithProjectId.put(orderNumber.trim(), null);
			}
			else {
				boolean isPorchOrder =isPorchServiceOrder(bbbOrder);
			if(isPorchOrder){
				final List<CommerceItem> comItemObj = order.getCommerceItems();
				int shippingGroups = order.getShippingGroupCount();
			    if(shippingGroups>1){				
				   multipleBookAJobServices(bbbOrder, comItemObj,true);
				   ordersWithProjectId.put(bbbOrder.getOnlineOrderNumber(), comItemObj);
			   }
			   else {		   
				   singleBookAJobService(bbbOrder, comItemObj,true);
				   ordersWithProjectId.put(bbbOrder.getOnlineOrderNumber(), comItemObj);
			   }
			}
			else{
				ordersWithProjectId.put(bbbOrder.getOnlineOrderNumber(), null);
			}
		}
		}
		if(isLoggingDebug()){
			logDebug("Method ends to call book a job and schedule job apis for porch orders  "+getClass().getName() +".processQASPorchOrders()   "+orderNumbers.toString());
		}
		return ordersWithProjectId;
	}
	
	
	
	/**
	 * @param order
	 * @param comItemObj
	 * @param responseVO
	 */
	public void singleBookAJobService(BBBOrder order, final List<CommerceItem> comItemObj,boolean ignoreRetryCount) {
		
		if(isLoggingDebug()){
			logDebug("Method starts to call book a job  for porch order for single shipping address  "+getClass().getName() +".singleBookAJobService()   "+order.toString());
		}
		 PorchBookAJobResponseVO responseVO=null;
		boolean isPorchCommerceItem;
		RepositoryItem porchItem = null;
		JSONArray porchJobs= new JSONArray();
		String jsonPostalCode=null;
	       for(CommerceItem comerceItem: comItemObj){
	    	   isPorchCommerceItem = ((BaseCommerceItemImpl) comerceItem).isPorchService();
			   porchItem=((BaseCommerceItemImpl) comerceItem).getPorchServiceRef();	 
			   
		       	if(isPorchCommerceItem){
		       		String projectId = (String) porchItem.getPropertyValue(PORCH_PROJECT_ID);
		       		if(null!=porchItem && StringUtils.isBlank(projectId)){
		       			createPorchJob(porchItem, porchJobs,ignoreRetryCount);       		
		       		}
		       		else {
		       			reInvokePorchScheduleJob(porchItem, comerceItem);		       			
		       		}
		       	}   	
	       
	       }
	    if(!porchJobs.isEmpty()){
	       JSONObject bookAJobJson = new JSONObject();
	       String billingPhoneNumber=order.getBillingAddress().getMobileNumber();
	       if(StringUtils.isBlank(billingPhoneNumber)){
	    	   billingPhoneNumber=order.getBillingAddress().getPhoneNumber();
	       }
	       
	       if(!StringUtils.isBlank(order.getBopusOrderNumber())){
	    			String jsonReqStr =(String) porchItem.getPropertyValue("jsonDataRequest");
				   JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonReqStr);
				   final DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);	
				   jsonPostalCode=(String) JSONResultbean.get("postalCode");
	       }
	       
	       bookAJobJson= buildBookJobJson(porchJobs,null,order,jsonPostalCode);
		   try {
		       responseVO= invokeBookAJobAPI(bookAJobJson);
		    	  
			} catch (BBBSystemException | BBBBusinessException e) {
				if(isLoggingError()){
					logError("Error while invoking book a job a job   porch order "+order.getInternationalOrderId() +" "+e.getMessage());
				}
				
				
			} 
		      
		       updatePorchOrderStatus(order,responseVO,bookAJobJson.toString(),ignoreRetryCount);
		     	    	 
	       }
	    if(isLoggingDebug()){
			logDebug("Method ends to call book a job  for porch order for single shipping address  "+getClass().getName() +".singleBookAJobService()   "+order.toString());
		}
	}


	/**
	 * @param order
	 * @param comItemObj
	 * @param responseVO
	 * @return
	 */
	public void multipleBookAJobServices(BBBOrder order, final List<CommerceItem> comItemObj,boolean ignoreRetryCount) {
		 if(isLoggingDebug()){
				logDebug("Method starts to call book a job  for porch order for multi shipping address  "+getClass().getName() +".multipleBookAJobServices()   "+order.toString());
			}
		PorchBookAJobResponseVO responseVO=null;
		boolean isPorchCommerceItem;
		RepositoryItem porchItem;	 
		String jsonPostalCode=null;
		for(CommerceItem comerceItem: comItemObj){
			   isPorchCommerceItem = ((BaseCommerceItemImpl) comerceItem).isPorchService();
			   
			   	if(isPorchCommerceItem){
			   		porchItem=((BaseCommerceItemImpl) comerceItem).getPorchServiceRef();
			   		JSONArray porchJobs= new JSONArray();
			   		String projectId = (String) porchItem.getPropertyValue(PORCH_PROJECT_ID);
			   		if(null!=porchItem && StringUtils.isBlank(projectId)){
			   			
			   			createPorchJob(porchItem, porchJobs,ignoreRetryCount);  
			   		  
				        RepositoryItem shippingAddress = null;
				        ShippingGroup shpnggroup = ((BBBShippingGroupCommerceItemRelationship)comerceItem.getShippingGroupRelationships().get(0)).getShippingGroup();
				        if(shpnggroup instanceof BBBStoreShippingGroup){
				        	
				        }
				        else {
				        	shpnggroup = (BBBHardGoodShippingGroup) ((BBBShippingGroupCommerceItemRelationship)comerceItem.getShippingGroupRelationships().get(0)).getShippingGroup();
				        	BBBHardGoodShippingGroup shipinggroup = (BBBHardGoodShippingGroup)shpnggroup;
				        	   shippingAddress=shipinggroup.getRepositoryItem();
				        }
				          
			       	    JSONObject bookAJobJson = new JSONObject();
			       	    
			       	    if(!StringUtils.isBlank(order.getBopusOrderNumber())){
			       	    	String jsonReqStr =(String) porchItem.getPropertyValue("jsonDataRequest");
							JSONObject jsonPorchObject = (JSONObject) JSONSerializer.toJSON(jsonReqStr);			   
							final DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonPorchObject);
			       	    	jsonPostalCode=(String) JSONResultbean.get("postalCode");
			       	    }
			  	        if(!porchJobs.isEmpty()){
			  		       bookAJobJson= buildBookJobJson(porchJobs,shippingAddress,order,jsonPostalCode);
			  		       try {
			  		    	   responseVO=null;
			  		    	   responseVO=invokeBookAJobAPI(bookAJobJson);		  		    	  
			  		       } catch (BBBSystemException | BBBBusinessException e) {
			  				if(isLoggingError()){
			  					logError("Erro while  job a  for porch order "+order.getInternationalOrderId() +" "+e.getMessage());
			  				}
			  		       } 			      
			  		       updatePorchOrderStatus(order,responseVO,bookAJobJson.toString(),ignoreRetryCount);
				       }
			   		}
			   		else{

		       			reInvokePorchScheduleJob(porchItem, comerceItem);
		       		
		       		}
		   	  }
	 }
		if(isLoggingDebug()){
			logDebug("Method ends to call book a job  for porch order for multi shipping address  "+getClass().getName() +".multipleBookAJobServices()   "+order.toString());
		}
   }



	/**
	 * @param responseVO
	 * @param porchItem
	 * @param comerceItem
	 * @param jobStatus
	 */
	private void reInvokePorchScheduleJob(RepositoryItem porchItem,
			CommerceItem comerceItem) {
		
		if(isLoggingDebug()){
			logDebug("Method starts  re invooke schedule job api for porch commerce item "+getClass().getName() +".reInvokePorchScheduleJob()   "+comerceItem.toString());
		}
		
		String jobStatus=(String) porchItem.getPropertyValue("jobStatus");
		ShippingGroup shgroup = ((BBBShippingGroupCommerceItemRelationship)comerceItem.getShippingGroupRelationships().get(0)).getShippingGroup();
		if(!(shgroup instanceof BBBStoreShippingGroup)){
			BBBHardGoodShippingGroup hardGoodShipping = (BBBHardGoodShippingGroup) shgroup;
			Set<TrackingInfo> trackingInfos=null;
			trackingInfos = hardGoodShipping.getTrackingInfos();
			if(null !=trackingInfos && jobStatus.equalsIgnoreCase("SCHEDULED_FAILED")){
				TrackingInfo shippingTrackInfo = trackingInfos.iterator().next();
				try {
					String jobId=(String) porchItem.getPropertyValue("jobId");
					PorchSchedlueJobResponseVO scheduleResponseVO=invokeScheduleAJobAPI(jobId,shippingTrackInfo.getTrackingUrl(),shippingTrackInfo.getActualShipDate());
					 if(null!=scheduleResponseVO && !StringUtils.isBlank(scheduleResponseVO.getJobId())){
			    		   updateScheduledJobResponse("jobStatus", "SCHEDULED", porchItem);
			    	   }
			    	   else{
			    		   updateScheduledJobResponse("jobStatus", "SCHEDULED_FAILED", porchItem);
			    	   }
				} catch (BBBSystemException | BBBBusinessException e) {
					if(isLoggingError()){
						logError("Error while updating porch sheduler date "+e,e);
					}
				}	
			}
		
		}
		else if(shgroup instanceof BBBStoreShippingGroup && jobStatus.equalsIgnoreCase("SCHEDULED_FAILED")){
			try {
				String jobId=(String) porchItem.getPropertyValue("jobId");
				PorchSchedlueJobResponseVO scheduleResponseVO=invokeScheduleAJobAPI(jobId,"",null);
				 if(null!=scheduleResponseVO && !StringUtils.isBlank(scheduleResponseVO.getJobId())){
		    		   updateScheduledJobResponse("jobStatus", "SCHEDULED", porchItem);
		    	   }
		    	   else{
		    		   updateScheduledJobResponse("jobStatus", "SCHEDULED_FAILED", porchItem);
		    	   }
			} catch (BBBSystemException | BBBBusinessException e) {
				if(isLoggingError()){
					logError("Error while updating porch sheduler date "+e,e);
				}
			}
		}
		if(isLoggingDebug()){
			logDebug("Method ends  re invooke schedule job api for porch commerce item "+getClass().getName() +".reInvokePorchScheduleJob()   "+comerceItem.toString());
		}
	}
	
	
	/**
	 * @param porchItem
	 * @param porchJobs
	 */
	private void createPorchJob(RepositoryItem porchItem, JSONArray porchJobs,boolean ignoreRetryCount) {
		String jsonReqStr;
		jsonReqStr =(String) porchItem.getPropertyValue("jsonDataRequest");
		String porchStatus="";
		if(null!=porchItem.getPropertyValue("jobStatus")){
			porchStatus=(String) porchItem.getPropertyValue("jobStatus");
		}
		int retryCount=0;
		if(null!=porchItem.getPropertyValue("retryCount"))
		{
			retryCount=(int) porchItem.getPropertyValue("retryCount");
		}
		if(ignoreRetryCount){
			retryCount=2;
		}
		if(!StringUtils.isBlank(jsonReqStr) && ((!porchStatus.equalsIgnoreCase("IN PROGRESS") && retryCount<3))){
				JSONObject porchJob = (JSONObject) JSONSerializer.toJSON(jsonReqStr);							
				porchJobs.add(porchJob);
		}
	}
	
	
	/**
	 * @param req
	 * @param zipCode
	 * @return
	 */
	public String zipCodeFromAkamaiHeader(DynamoHttpServletRequest req) {
		String zipCode = null;
		String headerValue = (String) req.getHeader("X-Akamai-Edgescape");
		if (headerValue != null) {

			HashMap<String, String> map = null;
			
			if (!StringUtils.isBlank(headerValue)) {

				map = new HashMap<String, String>();
				int index = 0;
				while (index < headerValue.length()) {
					String key = headerValue.substring(index, headerValue.indexOf(BBBCoreConstants.EQUAL, index));
					index = headerValue.indexOf(BBBCoreConstants.EQUAL, index);
					String value = headerValue.substring(index + 1, (-1 != headerValue.indexOf(BBBCoreConstants.COMMA,
							index)) ? headerValue.indexOf(BBBCoreConstants.COMMA, index) : headerValue.length());
					index = (-1 != headerValue.indexOf(BBBCoreConstants.COMMA, index)) ? headerValue.indexOf(
							BBBCoreConstants.COMMA, index) + 1 : headerValue.length() + 1;
					map.put(key, value);
				}
				zipCode = map.get("zip");
			}
			
			
		
		}
		return zipCode;
	}
	/**
	 * @return the commerceItemManager
	 */
	public BBBCommerceItemManager getCommerceItemManager() {
		return commerceItemManager;
	}

	/**
	 * @param commerceItemManager
	 *            the commerceItemManager to set
	 */
	public void setCommerceItemManager(BBBCommerceItemManager commerceItemManager) {
		this.commerceItemManager = commerceItemManager;
	}

	/**
	 * @return the orderManager
	 */
	public BBBOrderManager getOrderManager() {
		return orderManager;
	}

	/**
	 * @param orderManager
	 *            the orderManager to set
	 */
	public void setOrderManager(BBBOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	/**
	 * @return the httpCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}

	/**
	 * @param httpCallInvoker the httpCallInvoker to set
	 */
	public void setHttpCallInvoker(HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}

	 

	/**
	 * @return the scheduledRepository
	 */
	public MutableRepository getScheduledRepository() {
		return scheduledRepository;
	}

	/**
	 * @param scheduledRepository the scheduledRepository to set
	 */
	public void setScheduledRepository(MutableRepository scheduledRepository) {
		this.scheduledRepository = scheduledRepository;
	}

	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}



	/**
	 * @return the checkListTools
	 */
	public CheckListTools getCheckListTools() {
		return checkListTools;
	}



	/**
	 * @param checkListTools the checkListTools to set
	 */
	public void setCheckListTools(CheckListTools checkListTools) {
		this.checkListTools = checkListTools;
	}


	/**
	 * @return the catalogTools
	 */
	/**
	 * @return catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	 

	
	
}
