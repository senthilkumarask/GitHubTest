package com.bbb.commerce.order.feeds;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.TransactionManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.states.StateDefinitions;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.email.EmailException;
import atg.service.email.SMTPEmailSender;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.LockManagerException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;


import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.commerce.porch.service.vo.PorchSchedlueJobResponseVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.orderfeed.ItemTrackingType;
import com.bbb.framework.jaxb.orderfeed.OrderAttributesType;
import com.bbb.framework.jaxb.orderfeed.OrderLineItemType;
import com.bbb.framework.jaxb.orderfeed.OrderStatusType;
import com.bbb.framework.jaxb.orderfeed.ShipmentTrackingType;
import com.bbb.framework.jaxb.orderfeed.ShippingGroupType;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BaseCommerceItemImpl;
import com.bbb.order.bean.TrackingInfo;
import com.bbb.utils.BBBUtility;
/**
 * This class updates the order repository as per the order feed.
 * It is called by OrderFeedMessageListener when there is a new order feed
 * message on the queue
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * @author njai13
 *
 */

public class OrderStatusUpdateManager  extends BBBGenericService{

	private static final int DEFAULT_XML_DATE_VALUE = -2147483648;
	private static final int OTHERS = 5017;
	private MutableRepository orderRepository;
	private String exceptionMessage = "Fatal Exception occured while processing the order status feed";
	private  SMTPEmailSender emailSender;
	private String sender;
	private String subject;
	private String[] recipients;
	private BBBOrderTools orderTools;
	private final JAXBContext context;
	private final Unmarshaller unMarshaller;
	private BBBCommerceItemManager mCommerceItemManager;
	private PorchServiceManager porchServiceManager;
	private int poolSize;
	private String dcPrefix;
	private boolean processOrderOfAllDC;
	private boolean fromOrderFeedListener;
	private ClientLockManager localLockManager;
	
	public ClientLockManager getLocalLockManager() {
		return localLockManager;
	}

	public void setLocalLockManager(ClientLockManager localLockManager) {
		this.localLockManager = localLockManager;
	}

	

	/**
	 * @return the processOrderOfAllDC
	 */
	public boolean isProcessOrderOfAllDC() {
		return processOrderOfAllDC;
	}

	/**
	 * @param processOrderOfAllDC the processOrderOfAllDC to set
	 */
	public void setProcessOrderOfAllDC(boolean processOrderOfAllDC) {
		this.processOrderOfAllDC = processOrderOfAllDC;
	}

	/**
	 * @return the poolSize
	 */
	public int getPoolSize() {
		return poolSize;
	}

	/**
	 * @param poolSize the poolSize to set
	 */
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public String getDcPrefix() {
		return dcPrefix;
	}

	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}
	/**
	 * @return the threadPool
	 */
	public ExecutorService getThreadPool() {
		return threadPool;
	}

	/**
	 * @param threadPool the threadPool to set
	 */
	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}

	private ExecutorService threadPool;
	
	public OrderStatusUpdateManager() throws JAXBException {
	    this.context = JAXBContext.newInstance (OrderStatusType.class);

	    this.unMarshaller = this.context.createUnmarshaller ();
    }

	/**
	 * @return the recipients
	 */
	public String[] getRecipients() {
		return this.recipients;
	}
	/**
	 * @param recipients the recipients to set
	 */
	public void setRecipients(final String[] recipients) {
		this.recipients = recipients;
	}

	/**
	 * @return the emailSender
	 */
	public SMTPEmailSender getEmailSender() {
		return this.emailSender;
	}
	/**
	 * @param emailSender the emailSender to set
	 */
	public void setEmailSender(final SMTPEmailSender emailSender) {
		this.emailSender = emailSender;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return this.sender;
	}
	/**
	 * @param sender the sender to set
	 */
	public void setSender(final String sender) {
		this.sender = sender;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return this.subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * @return the orderRepository
	 */
	public MutableRepository getOrderRepository() {
		return this.orderRepository;
	}
	/**
	 * @param orderRepository the orderRepository to set
	 */
	public void setOrderRepository(final MutableRepository orderRepository) {
		this.orderRepository = orderRepository;
	}



	/**
	 * @return the orderTools
	 */
	public BBBOrderTools getOrderTools() {
		return this.orderTools;
	}
	/**
	 * @param orderTools the orderTools to set
	 */
	public void setOrderTools(final BBBOrderTools orderTools) {
		this.orderTools = orderTools;
	}
	/**
	 * @return the commerceItemManager
	 */
	public BBBCommerceItemManager getCommerceItemManager() {
		return mCommerceItemManager;
	}

	/**
	 * @param pCommerceItemManager the commerceItemManager to set
	 */
	public void setCommerceItemManager(BBBCommerceItemManager pCommerceItemManager) {
		mCommerceItemManager = pCommerceItemManager;
	}
	
	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		threadPool = Executors.newFixedThreadPool(poolSize);
	}
	
	/**
	 * The updateOrderStatus is called on the client request to perform a update of the order status from TIBCO.
	 * The updateOrderStatus method will call the WebserviceHandler to get the Vo object with the order update parameters.
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws JAXBException
	 */
	public void updateOrderStatus(String xml)
	{
		logDebug(" The Current request is from OrderFeedListener therfore will not reprice the order for eco-fee items ");
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		if(pRequest!=null){
			logDebug("pRequest is not null.");
			pRequest.setParameter(BBBCoreConstants.FROM_ORDER_FEED_LISTENER,isFromOrderFeedListener());
		} 

		this.logDebug("[START] updateOrderStatus");
		if(!StringUtils.isBlank(xml)){
			final Map<String,String> failedInfoList = new HashMap<String,String>();

			try{

				final Source source = new StreamSource(new StringReader(xml.trim()));

				final JAXBElement<OrderStatusType> itemJaxb = this.unMarshaller.unmarshal (source,OrderStatusType.class);

				final OrderStatusType orderStatusTypeElement=itemJaxb.getValue();
				final OrderStatusUpdateVO orderStatusUpdateVO=new OrderStatusUpdateVO();
				if(orderStatusTypeElement !=null){
					
					Runnable newQueryThread = new Runnable(){
						public void run(){
					final List<OrderAttributesType> orderAttributesElement= orderStatusTypeElement.getOrderAttributes();
					if((orderAttributesElement!=null) && !orderAttributesElement.isEmpty())
					{
						/*Code Cleanup  - We are printing orderAttributesElement.size and there is a possibility that orderAttributesElement object is null*/
						logDebug(" Total No Of orders is ["+orderAttributesElement.size()+"]");
						/*Code Cleanup - Instantiating in the for loop is expensive from GC standpoint and also its one of the performance best practice.*/
						OrderAttributesType orderAttributesType = null;
						
						for(int indexOfOrder=0;indexOfOrder<orderAttributesElement.size();indexOfOrder++){
							orderAttributesType = orderAttributesElement.get(indexOfOrder);
								logDebug(" Inserting "+indexOfOrder+" the Order having order no ["+orderAttributesType.getOrderNumber()+"]");

							if(orderAttributesType.getOrderNumber()!=null) {
								logInfo("Staus update message received for onlineOrderNumber ["+orderAttributesType.getOrderNumber()+"] with order status ["+orderAttributesType.getOrderState()+"]");
								if (!isProcessOrderOfAllDC() && !isOrderFromCurrentDC(orderAttributesType)) {
									return;
								}
								orderStatusUpdateVO.setOrderNumber(orderAttributesType.getOrderNumber());
							}
							logDebug(" Order state ["+orderAttributesType.getOrderState()+"]");
							logDebug(" Order state Description ["+orderAttributesType.getStateDescription()+"]");
							logDebug(" Order Status Change Date ["+orderAttributesType.getStatusChangeDate()+"]");

							if(orderAttributesType.getOrderState()!=null){
								orderStatusUpdateVO.setOrderState(orderAttributesType.getOrderState());
							}
							if(orderAttributesType.getStateDescription()!=null)
							{
								orderStatusUpdateVO.setStateDescription(orderAttributesType.getStateDescription());
							}
							if(orderAttributesType.getStatusChangeDate()!=null)
							{
								orderStatusUpdateVO.setModificationDate(orderAttributesType.getStatusChangeDate());
							}
							if((orderAttributesType.getShippingGroup()!=null) && !orderAttributesType.getShippingGroup().isEmpty())
							{
								final List<ShippingGroupTypeVO> ShippingGroupTypeVOList=new ArrayList<ShippingGroupTypeVO>();
								final	List<ShippingGroupType> shipGrpTypeElementList=orderAttributesType.getShippingGroup();
								final int noOfSG=shipGrpTypeElementList.size();
									logDebug(" Updating shipping Group Info.Total no of records to be updated is" +" ["+noOfSG+"]");
								/*Code Cleanup - Instantiating in the for loop is expensive from GC standpoint and also its one of the performance best practice.*/
								ShippingGroupType shipGrpTypeElement = null;
								ShippingGroupTypeVO shippingGroupTypeVO = null;
								for(int i=0;i<noOfSG;i++)
								{
									shipGrpTypeElement=shipGrpTypeElementList.get(i);
									logInfo("Status update message received for onlineOrderNumber ["+orderAttributesType.getOrderNumber()+"] for shippinGroupId ["+shipGrpTypeElement.getShipmentGroupId()+"] with Shipment Status ["+shipGrpTypeElement.getShipmentState()+"]");
									shippingGroupTypeVO=updateShippingGroupTypeVO(shipGrpTypeElement);
										logDebug(" Updating ["+i+"] th shipping record shipGrpTypeElement" +" ["+shipGrpTypeElement+"]");
									ShippingGroupTypeVOList.add(shippingGroupTypeVO);
								}
								orderStatusUpdateVO.setShippingGroupTypeVOList(ShippingGroupTypeVOList);
							}
							updateOrderRepository(orderStatusUpdateVO, failedInfoList);
							if(!failedInfoList.isEmpty()){
								sendFailedRecordsReport(getMessage(failedInfoList));
							}
						}
					}
						}
					};
					threadPool.execute(newQueryThread);
				}
			} catch(final Exception e){
					this.logError(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.updateOrderStatus() | Exception "), e);
				this.sendFailedRecordsReport(this.getExceptionMessage());
			}
		}

			this.logDebug("[END] updateOrderStatus");
	}

	/**
	 * This method sends the report for records that failed to update
	 * @param failedInfoList
	 */
	public void sendFailedRecordsReport(final String pMessage){
		try {
				this.logDebug("Sender :"+this.getSender()+" recipients: " + Arrays.toString(this.getRecipients())+" subject "+this.getSubject());
			this.getEmailSender().setLoggingDebug(true);
			this.getEmailSender().setLoggingError(true);
			this.getEmailSender().sendEmailMessage(this.getSender(),this.getRecipients(), this.getSubject(), pMessage);
		}
		catch (final EmailException e) {
				this.logError(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.sendFailedRecordsReport() | EmailException Failed to send email message:" +
						"Remember to set /atg/dynamo/service/SMTPEmail.emailHandlerHostName and " +
						"/atg/dynamo/service/SMTPEmail.emailHandlerPort" ),e);
			}
		}
	/**
	 * The method returns the message with list of order and shipping ids that failed to update alond with the reason
	 * of failure
	 * @param failedInfoList
	 * @return
	 */
	private String getMessage(final Map<String, String> failedInfoList){
		final StringBuffer messageString=new StringBuffer();
		if((failedInfoList!=null) && !failedInfoList.isEmpty()){
			final Set<String> orderIdKey=failedInfoList.keySet();
			for(final String orderId:orderIdKey){
				messageString.append("Updation of order Id"+orderId+" failed because "+failedInfoList.get(orderId) +"\\n");
			}
		}
			this.logDebug("message body of the mail  "+messageString.toString());
		return messageString.toString();
	}

	/**
	 * This method updates the information for the shipping group in the ShippingGroupTypeVO
	 * @param shipGrpTypeElement
	 * @return
	 */
	private ShippingGroupTypeVO updateShippingGroupTypeVO(final ShippingGroupType shipGrpTypeElement)
	{
		final ShippingGroupTypeVO shippingGroupTypeVO=new ShippingGroupTypeVO();
		if((shipGrpTypeElement.getShipmentTracking()!=null) && !shipGrpTypeElement.getShipmentTracking().isEmpty() )
		{
			final List<ShipmentTrackingVO> shipmentTrackingVOList=new ArrayList<ShipmentTrackingVO>();
			final List<ShipmentTrackingType> ShipmentTrackingElementList=shipGrpTypeElement.getShipmentTracking();
				this.logDebug(" Updating shipment Tracking for shipping id "+shipGrpTypeElement.getShipmentGroupId()+" .Total no of records to be updated is" +
						" ["+ShipmentTrackingElementList.size()+"]");
			/*Code Cleanup - Instantiating in the for loop is expensive from GC standpoint and also its one of the performance best practice.*/
			ShipmentTrackingType shipmentTrackingTypeElement = null;
			ShipmentTrackingVO shipmentTrackingVO = null;
			for(int index=0;index<ShipmentTrackingElementList.size();index++)
			{
				shipmentTrackingTypeElement=ShipmentTrackingElementList.get(index);
				shipmentTrackingVO=this.updateShipmentTrackingVO(shipmentTrackingTypeElement);
				shipmentTrackingVOList.add(shipmentTrackingVO);
			}
			if(shipGrpTypeElement.getOrderLineItem() != null && !shipGrpTypeElement.getOrderLineItem().isEmpty()){
				List<OrderLineItemType> ShipmentLineItemsList = shipGrpTypeElement.getOrderLineItem();
				List<OrderLineItemVO> orderLineItemVOs = updateShipmentLineItemVO(ShipmentLineItemsList);
				shippingGroupTypeVO.setOrderLineItemVO(orderLineItemVOs);
			}
			
			shippingGroupTypeVO.setShipmentTrackingVO(shipmentTrackingVOList);
		}
			this.logDebug("Tracking details updation complete ");
			this.logDebug("Shipping Group Id  ["+shipGrpTypeElement.getShipmentGroupId()+"]");
			this.logDebug("Shipping Group State Description  ["+shipGrpTypeElement.getStateDescription()+"]");
			this.logDebug("Shipping Group State   ["+shipGrpTypeElement.getShipmentState()+"]");
		if(shipGrpTypeElement.getShipmentGroupId()!=null){
			shippingGroupTypeVO.setShipmentGroupId(shipGrpTypeElement.getShipmentGroupId());
		}
		if(shipGrpTypeElement.getStateDescription()!=null){
			shippingGroupTypeVO.setShipmentDescription(shipGrpTypeElement.getStateDescription());
		}
		if(shipGrpTypeElement.getShipmentState()!=null){
			shippingGroupTypeVO.setShipmentState(shipGrpTypeElement.getShipmentState());
		}
		return shippingGroupTypeVO;
	}

	/**
	 * This method is used to populate the all line items shipment details to VOs 
	 * @param pShipmentLineItemsList
	 * @return
	 */
	private List<OrderLineItemVO> updateShipmentLineItemVO(List<OrderLineItemType> pShipmentLineItemsList) {
		vlogDebug("OrderStatusUpdateManager :: updateShipmentLineItemVO() method :: START ");
		
		List<OrderLineItemVO> orderLineItemVOs = new ArrayList<OrderLineItemVO>();
		OrderLineItemVO orderLineItemVO = null;
		
		for (OrderLineItemType orderLineItemType : pShipmentLineItemsList) {
			this.logInfo("Status update message received for line item ["+orderLineItemType.getItemId()+"] with status ["+orderLineItemType.getItemStatus()+"]");
			orderLineItemVO = new OrderLineItemVO();
			if(orderLineItemType.getItemId() != null){
				orderLineItemVO.setItemId(orderLineItemType.getItemId());
			}
			if(orderLineItemType.getItemStatus() != null){
				orderLineItemVO.setItemStatus(orderLineItemType.getItemStatus());
			}
			if(orderLineItemType.getItemStatusDescription() != null){
				orderLineItemVO.setItemStatusDesc(orderLineItemType.getItemStatusDescription());
			}
			if(orderLineItemType.getTotalItemQuantity() != null){
				orderLineItemVO.setTotalItemQty(orderLineItemType.getTotalItemQuantity().longValue());
			}
			vlogDebug("ItemId  :: "+orderLineItemType.getItemId());
			vlogDebug("ItemStatus  :: "+orderLineItemType.getItemStatus());
			vlogDebug("ItemStatusDescription  :: "+orderLineItemType.getItemStatusDescription());
			vlogDebug("TotalItemQuantity  :: "+orderLineItemType.getTotalItemQuantity());
			
			if(orderLineItemType.getTracking() != null && !orderLineItemType.getTracking().isEmpty()){
				List<ItemTrackingType> itemTrackingList = orderLineItemType.getTracking();
				List<ShipmentTrackingVO> itemTrackingVOs = updateItemTrackingVO(itemTrackingList);
				orderLineItemVO.setItemTrackingVO(itemTrackingVOs);
			}
			orderLineItemVOs.add(orderLineItemVO);
		}
		vlogDebug("OrderStatusUpdateManager :: updateShipmentLineItemVO() method :: START ");
		return orderLineItemVOs;
	}

	/**
	 * This method is used to populate the line item shipment tracking details to VOs
	 * @param pItemTrackingList
	 * @return
	 */
	private List<ShipmentTrackingVO> updateItemTrackingVO(List<ItemTrackingType> pItemTrackingList) {
		vlogDebug("OrderStatusUpdateManager :: updateItemTrackingVO() method :: START ");
		
		List<ShipmentTrackingVO> itemTrackingVOs = new ArrayList<ShipmentTrackingVO>();
		ShipmentTrackingVO itemTrackingVO = null;
		
		for (ItemTrackingType itemTrackingType : pItemTrackingList) {
			itemTrackingVO = new ShipmentTrackingVO();
			if(itemTrackingType.getTrackingNumber() != null){
				itemTrackingVO.setTrackingId(itemTrackingType.getTrackingNumber());
			}
			if(itemTrackingType.getCarrierCode() != null){
				itemTrackingVO.setCarrierCode(itemTrackingType.getCarrierCode().toUpperCase());
			}
			if(itemTrackingType.getShipmentDate() != null){
				itemTrackingVO.setShipModificationDate(itemTrackingType.getShipmentDate());
			}
			if(itemTrackingType.getShipmentQuantity() != null){
				itemTrackingVO.setShipmentQty(itemTrackingType.getShipmentQuantity().intValue());
			}
			itemTrackingVOs.add(itemTrackingVO);
			
			vlogDebug("Item TrackingNumber  :: "+itemTrackingType.getTrackingNumber());
			vlogDebug("Item CarrierCode  :: "+itemTrackingType.getCarrierCode());
			vlogDebug("Item ShipmentDate  :: "+itemTrackingType.getShipmentDate());
			vlogDebug("Item Shipment Quantity  :: "+itemTrackingType.getShipmentQuantity());
		}
		vlogDebug("OrderStatusUpdateManager :: updateItemTrackingVO() method :: START ");
		return itemTrackingVOs;
	}

	/**
	 * This method updates the tracking information in the ShipmentTrackingVO
	 * @param shipmentTrackingTypeElement
	 * @return
	 */
	private ShipmentTrackingVO updateShipmentTrackingVO(final ShipmentTrackingType shipmentTrackingTypeElement){
		final ShipmentTrackingVO shipmentTrackingVO=new ShipmentTrackingVO();
			this.logDebug("Actual Ship date of shipment ["+shipmentTrackingTypeElement.getActualShipDate()+"]");
			this.logDebug("Carrier Code of shipment ["+shipmentTrackingTypeElement.getCarrierCode()+"]");
			this.logDebug("Tracking No of shipment ["+shipmentTrackingTypeElement.getTrackingNumber()+"]");
			vlogDebug("URL to get of shipment :: "+shipmentTrackingTypeElement.getURL());
		if(shipmentTrackingTypeElement.getActualShipDate()!=null)
		{//Actual_ship_date
			shipmentTrackingVO.setShipModificationDate(shipmentTrackingTypeElement.getActualShipDate());
		}
		if(shipmentTrackingTypeElement.getCarrierCode()!=null)
		{
			shipmentTrackingVO.setCarrierCode(shipmentTrackingTypeElement.getCarrierCode().toUpperCase());
		}
		if(shipmentTrackingTypeElement.getTrackingNumber()!=null){
			shipmentTrackingVO.setTrackingId(shipmentTrackingTypeElement.getTrackingNumber());
		}
		if(!StringUtils.isBlank(shipmentTrackingTypeElement.getURL())){
			shipmentTrackingVO.setUrl(shipmentTrackingTypeElement.getURL());
		}
		return shipmentTrackingVO;
	}

	/**
	 * The method updates the order repository with information in the orderUpdateVO object.
	 *  The method also performs validations before the status is updated into the OrderRepository
	 * @param orderStatusUpdateVO
	 * @param failedInfoList
	 * @throws BBBSystemException
	 */
	public void updateOrderRepository(final OrderStatusUpdateVO  orderStatusUpdateVO, final Map<String, String> failedInfoList ) {
		this.logDebug("[START] updateOrderRepository");
		this.logDebug("Updating Order Repository from OrderStatusUpdateVO  "+orderStatusUpdateVO);
		boolean rollback = true; 
		ClientLockManager lockManager = getLocalLockManager();
		boolean acquireLock = false;
		final TransactionManager tm = this.getOrderTools().getTransactionManager();
		final TransactionDemarcation td = new TransactionDemarcation();
		Order order = null;
		try{
			order = this.getOrderTools().getOrderFromOnlineOrBopusOrderNumber(orderStatusUpdateVO.getOrderNumber());
			if(order != null){
				acquireLock = !lockManager.hasWriteLock(order.getId(), Thread.currentThread());
				if (acquireLock) {
					lockManager.acquireWriteLock(order.getId(), Thread.currentThread());
				}
				if (tm != null) {
					td.begin(tm, TransactionDemarcation.REQUIRED);
				}

				synchronized (order) {
		                this.logDebug("orderItem Object for order No "+orderStatusUpdateVO.getOrderNumber()+" is "+order.getId());
					final String orderState=StateDefinitions.ORDERSTATES.getStateString(order.getState());
					final String orderSubState=((BBBOrder) order).getSubStatus();
						this.logDebug("orderState is "+orderState);
						
						//Add indicator as transient property to indicate that it is doing order update from OrderStatusUpdate Listener flow
						if (order instanceof BBBOrder){
							((BBBOrder)order).setOSUpdateListener(isFromOrderFeedListener());
							logDebug("pRequest may be null. So setting the OSUpdateListener as true in transient order property.");
						}
						
						if((orderState!=null) && (orderSubState!=null) && (orderSubState.equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_SUBMITTED)) && !(orderState.equalsIgnoreCase(BBBCoreConstants.INCOMPLETE_ORDER_STATUS) || orderState.equalsIgnoreCase(BBBCoreConstants.REMOVED_ORDER_STATUS))){
						this.updateOrderItem(orderStatusUpdateVO, order, failedInfoList);
						this.getOrderTools().getOrderManager().updateOrder(order);
					}
					else{
					    failedInfoList.put(orderStatusUpdateVO.getOrderNumber(), "Order state is null or incomplete or removed::"+orderState);
					}
				}
				rollback = false;
			}
			else{
				this.logInfo(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.updateOrderRepository() | Order id not present in database "+orderStatusUpdateVO.getOrderNumber()));
				failedInfoList.put(orderStatusUpdateVO.getOrderNumber(), "Order Id not in Repsoitory");
			}
		}catch(final RepositoryException e){
			this.logError(LogMessageFormatter.formatMessage(null, "RepositoryException occured while updating order ["+orderStatusUpdateVO.getOrderNumber()+"]"), e);
			failedInfoList.put(orderStatusUpdateVO.getOrderNumber(), "Exception while updating order id "+orderStatusUpdateVO.getOrderNumber() +" "+e.getMessage());
		}catch(final CommerceException e){
			this.logError(LogMessageFormatter.formatMessage(null, "CommerceException occured while updating order ["+orderStatusUpdateVO.getOrderNumber()+"]"), e);
			failedInfoList.put(orderStatusUpdateVO.getOrderNumber(), "Exception while updating order id "+orderStatusUpdateVO.getOrderNumber() +" "+e.getMessage());
		}catch(final TransactionDemarcationException e){
			this.logError(LogMessageFormatter.formatMessage(null, "TransactionDemarcationException occured while updating order ["+orderStatusUpdateVO.getOrderNumber()+"]"), e);
			failedInfoList.put(orderStatusUpdateVO.getOrderNumber(), "Exception while updating order id "+orderStatusUpdateVO.getOrderNumber() +" "+e.getMessage());
		}catch(final Exception e){
			this.logError(LogMessageFormatter.formatMessage(null, "Exception occured while updating order ["+orderStatusUpdateVO.getOrderNumber()+"]"), e);
			failedInfoList.put(orderStatusUpdateVO.getOrderNumber(), "Exception while updating order id "+orderStatusUpdateVO.getOrderNumber() +" "+e.getMessage());
		}finally {
			try {
				if (tm != null) {
					td.end(rollback);
				}
			} catch (final TransactionDemarcationException tranDemException) {
				this.logError("TransactionDemarcation Excpetion during updateOrderRepository method", tranDemException);
			} finally {

				if (order != null && acquireLock)
					try {
						lockManager.releaseWriteLock(order.getId(), Thread.currentThread(), true);
					} catch (LockManagerException e) {
						this.logError("TransactionDemarcationException releasing lock on order", e);

					}
		}
		}
		this.logDebug("[END] updateOrderRepository");
	}

	/**
     * This method updates the order with order related data from order feed
     * @param orderStatusUpdateVO
     * @param order
	 * @param failedInfoList
     * @return
     */
	private void updateOrderItem(final OrderStatusUpdateVO orderStatusUpdateVO,
			final Order order, final Map<String, String> failedInfoList) {
		if(order!=null){

			this.logDebug("[START] updateOrderItem");
			final int stateFromString = StateDefinitions.ORDERSTATES.getStateFromString(orderStatusUpdateVO.getOrderState().toUpperCase());
			if(stateFromString == 0) {//true if unknown state in the input
				order.setState(OTHERS);
			} else {
				order.setState(stateFromString);
			}

			order.setStateDetail(orderStatusUpdateVO.getStateDescription());

			final Object modificationDateObj = orderStatusUpdateVO
					.getModificationDate();
			this.logDebug("Calling getCalendarFromXMLDate() for ORDER ModificationDate....");
			final Calendar calendar = this.getCalendarFromXMLDate(modificationDateObj);

			if (calendar != null) {
				this.logDebug("OrderModification date, calendar: " + calendar
						+ ", calendar.getTime(): " + calendar.getTime());
				order.setLastModifiedTime(calendar.getTimeInMillis());
			}

			this.logDebug("order till now: " + order);

			if(orderStatusUpdateVO.getShippingGroupTypeVOList()!=null){

				this.logDebug("orderStatusUpdateVO.getShippingGroupTypeVOList().size(): "
						+ orderStatusUpdateVO.getShippingGroupTypeVOList()
						.size());
				for(int i=0;i<orderStatusUpdateVO.getShippingGroupTypeVOList().size();i++)
				{
					final ShippingGroupTypeVO shippingGroupTypeVO = orderStatusUpdateVO.getShippingGroupTypeVOList().get(i);
					this.logDebug("shippingGroupTypeVO: " + shippingGroupTypeVO);
					this.updateShippingGroup(order, shippingGroupTypeVO, failedInfoList,orderStatusUpdateVO);
				}

			}
			else{
				this.logInfo(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.updateOrderItem() | No shipping group info available "));
				failedInfoList.put(orderStatusUpdateVO.getOrderNumber(), "No shipping group info available to update ");
			}
		}
		this.logDebug("[START] updateOrderItem");
	}

	/**
     * This method is used to update shipping group related data from the feed.
     * for updation of tracking information first all previous the tracking info ids is added in a list
     * Then the set of shipmentTracking info is removed from the shipping group.
     * Then all the previous tracking info is also removed from tracking item descriptor.
     * The shipping group is then refreshed with new data.
     * @param shippingGroupTypeVO
	 * @param failedInfoList
	 * @param orderStatusUpdateVO 
     * @return
     * @throws BBBSystemException
     */
    private void updateShippingGroup(final Order order,
            final ShippingGroupTypeVO shippingGroupTypeVO, final Map<String, String> failedInfoList, OrderStatusUpdateVO orderStatusUpdateVO) {
            this.logDebug("[START] updateShippingGroup");
        ShippingGroup shippingGroupitem=null;
        try{
            shippingGroupitem=order.getShippingGroup(shippingGroupTypeVO.getShipmentGroupId());
            
            if(shippingGroupitem != null){
            	/*Code Cleanup - We are checking shippingGroupitem.getId() but its possible that shippingGroupitem is null*/ 
            	this.logDebug("shipping group item from repository for shipId "+shippingGroupTypeVO.getShipmentGroupId()+" is :"+shippingGroupitem.getId());

    			final int stateFromString = StateDefinitions.SHIPPINGGROUPSTATES.getStateFromString(shippingGroupTypeVO.getShipmentState().toUpperCase());
    			this.logDebug("shipping stateFromString: " + stateFromString);

    			// Commenting the shipping state update from Order Status Feed
    			// because this is creating issue and changing Shipping address
    			// state also.
    			/*if(stateFromString == 0) {
                   // shippingGroupitem.setState(1);
                } else {
                   // shippingGroupitem.setState(stateFromString);
                }*/

    			shippingGroupitem.setStateDetail(shippingGroupTypeVO.getShipmentDescription());
    			this.logDebug("shippingGroupitem.getStateDetail : " + shippingGroupitem.getStateDetail());
    			if(shippingGroupitem instanceof BBBHardGoodShippingGroup){
    				Map<String, TrackingInfo>shipmentTrackerSet=null;

    				final List<ShipmentTrackingVO> shipmentTrackingVOs = shippingGroupTypeVO.getShipmentTrackingVO();

    				if((shipmentTrackingVOs!=null) && !shipmentTrackingVOs.isEmpty()){
    					this.logDebug("shipmentTrackingVOs.size(): " + shipmentTrackingVOs.size());

    					shipmentTrackerSet =((BBBHardGoodShippingGroup) shippingGroupitem).getTrackingInfosMap();
    					if(shipmentTrackerSet == null) {
    						shipmentTrackerSet = new HashMap<String, TrackingInfo>();
    					}
    					for (final Object element : shipmentTrackingVOs) {
    						final ShipmentTrackingVO shipmentTrackingVO2 = (ShipmentTrackingVO) element;
    						this.logDebug("shipmentTrackinginfo: " + shipmentTrackingVO2);

    						TrackingInfo trackingInfo = shipmentTrackerSet.get(shipmentTrackingVO2.getTrackingId());

    						final Object modificationDateObj = shipmentTrackingVO2
    								.getShipModificationDate();
    						this.logDebug("Calling getCalendarFromXMLDate() for SHIP ModificationDate....");

    						final Calendar calendar = this.getCalendarFromXMLDate(modificationDateObj);

    						if (calendar != null) {
    							this.logDebug("ShipModification date, calendar: "+ calendar+ ", calendar.getTime(): "+ calendar.getTime());
    						}

    						if(trackingInfo != null) {
    							this.logDebug("Tracking info exist in ShippingGroup "+ shippingGroupTypeVO.getShipmentGroupId()+ " fith trackingInfoID "
    									+ trackingInfo.getRepositoryItem().getRepositoryId());
    							if (shipmentTrackingVO2.getCarrierCode() != null) {
    								trackingInfo.setCarrierCode(shipmentTrackingVO2.getCarrierCode());
    							}
    							if (!StringUtils.isBlank(shipmentTrackingVO2.getUrl())) {
    								trackingInfo.setTrackingUrl(shipmentTrackingVO2.getUrl());
    							}
    							if (calendar != null) {
    								trackingInfo.setActualShipDate(calendar.getTime());
    							}

    						} else {
    							final MutableRepositoryItem trackingInfoItem = this.getOrderRepository().createItem(BBBCoreConstants.TRACKING_INFO_ITEM_DESCRIPTOR);

    							trackingInfoItem.setPropertyValue(BBBCoreConstants.CARRIER_CODE_TRACKING_INFO_PROPERTY_NAME, shipmentTrackingVO2.getCarrierCode());
    							trackingInfoItem.setPropertyValue(BBBCoreConstants.TRACKING_NUMBER_TRACKING_INFO_PROPERTY_NAME, shipmentTrackingVO2.getTrackingId());
    							trackingInfoItem.setPropertyValue(BBBCoreConstants.TRACKING_URL_TRACKING_INFO_PROPERTY_NAME, shipmentTrackingVO2.getUrl());
    							if (calendar != null) {
    								trackingInfoItem
    								.setPropertyValue(
    										BBBCoreConstants.ACTUAL_SHIPDATE_TRACKING_INFO_PROPERTY_NAME,
    										calendar.getTime());
    							}
    							//getOrderRepository().addItem(trackingInfoItem);
    							trackingInfo = new TrackingInfo(trackingInfoItem);
    							shipmentTrackerSet.put(trackingInfo.getTrackingNumber(), trackingInfo);
    							this.getOrderRepository().addItem(trackingInfoItem);
    						}

    					}

    					//updating commerceitems with status details
    					updateLineItemStatus(order, shippingGroupTypeVO,orderStatusUpdateVO);

    					((BBBHardGoodShippingGroup) shippingGroupitem).setTrackingInfos(shipmentTrackerSet);

    				}
    			} else {
    				this.logInfo(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.updateShippingGroup() | No Track ids to update"));

    			}
    		}

    		else{
    			this.logError(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.updateShippingGroup() | shipGroup id not present in database "));
    			failedInfoList.put(shippingGroupTypeVO.getShipmentGroupId(), "shipGroup id: "+shippingGroupTypeVO.getShipmentGroupId()+ "not present in database");
    		}

    	}catch(final RepositoryException e){
    		this.logError(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.updateShippingGroup() | RepositoryException while updating tracking Info for order ["+((BBBOrder)order).getOnlineOrderNumber()+"] for ShippingGroup "+shippingGroupTypeVO.getShipmentGroupId()), e);
    		failedInfoList.put(shippingGroupTypeVO.getShipmentGroupId(), "shipGroup id: "+shippingGroupTypeVO.getShipmentGroupId()+ "Repository Exception while updating shipping id "+shippingGroupTypeVO.getShipmentGroupId() +" "+e.getMessage());
    	} catch (final ShippingGroupNotFoundException e) {
    		this.logError(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.updateShippingGroup() | ShippingGroupNotFoundException while updating tracking Info for order ["+((BBBOrder)order).getOnlineOrderNumber()+"] for ShippingGroup "+shippingGroupTypeVO.getShipmentGroupId()), e);
    		failedInfoList.put(shippingGroupTypeVO.getShipmentGroupId(), "shipGroup id: "+shippingGroupTypeVO.getShipmentGroupId()+ "Repository Exception while updating shipping id "+shippingGroupTypeVO.getShipmentGroupId() +" "+e.getMessage());
    	} catch (final InvalidParameterException e) {
    		this.logError(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.updateShippingGroup() | InvalidParameterException while updating tracking Info for order ["+((BBBOrder)order).getOnlineOrderNumber()+"] for ShippingGroup "+shippingGroupTypeVO.getShipmentGroupId()), e);
    		failedInfoList.put(shippingGroupTypeVO.getShipmentGroupId(), "shipGroup id: "+shippingGroupTypeVO.getShipmentGroupId()+ "Repository Exception while updating shipping id "+shippingGroupTypeVO.getShipmentGroupId() +" "+e.getMessage());
    	}catch(final Exception e){
    		this.logError(LogMessageFormatter.formatMessage(null, "OrderStatusUpdateManager.updateShippingGroup() | Exception while updating tracking Info for order ["+((BBBOrder)order).getOnlineOrderNumber()+"] for ShippingGroup"+shippingGroupTypeVO.getShipmentGroupId()), e);
    		failedInfoList.put(shippingGroupTypeVO.getShipmentGroupId(), "shipGroup id: "+shippingGroupTypeVO.getShipmentGroupId()+ "Repository Exception while updating shipping id "+shippingGroupTypeVO.getShipmentGroupId() +" "+e.getMessage());
    	}
    	this.logDebug("[END] updateShippingGroup");
    }

	@SuppressWarnings("unchecked")
	private void updateLineItemStatus(Order order, ShippingGroupTypeVO shippingGroupTypeVO, OrderStatusUpdateVO orderStatusUpdateVO) {
		//updating line items information 
		List<OrderLineItemVO> orderLineItemVOs = shippingGroupTypeVO.getOrderLineItemVO();
		try {
			
			if((orderLineItemVOs!=null) && !orderLineItemVOs.isEmpty()){
				ShippingGroup shipGroup = null;
				List<ShippingGroupCommerceItemRelationship> relationShip = null;
				//String commerceId = null;
				String skuId = null;
				for (OrderLineItemVO orderLineItemVO : orderLineItemVOs) {
					CommerceItem lineItem = null;
					//commented below code to get commerce item from order based on sku Id received in status update message
					//commerceId = orderLineItemVO.getItemId();
					//get the line itemm based on the commerceId
					//CommerceItem lineItem = getCommerceItemManager().getCommerceItemFromOrder(order, commerceId);
					//if(lineItem == null){
					// if lineItem not found based on the commerceId then get it based on the skuId. 
					skuId = orderLineItemVO.getItemId();
					shipGroup = order.getShippingGroup(shippingGroupTypeVO.getShipmentGroupId());
					if(shipGroup != null){
						relationShip = shipGroup.getCommerceItemRelationships();
					}
					if(relationShip != null && !relationShip.isEmpty()){
						for (ShippingGroupCommerceItemRelationship shipCommerceRelation : relationShip) {
							// get the commerceItem, if catalogRefId and skuId are same
							if(shipCommerceRelation.getCommerceItem().getCatalogRefId().equals(skuId)){
								lineItem = (BBBCommerceItem) shipCommerceRelation.getCommerceItem();
								break;
							}
						}
					}
					//}
					if(lineItem != null && lineItem instanceof BBBCommerceItem){
						BBBCommerceItem bbblineItem = (BBBCommerceItem) lineItem;
						if(!StringUtils.isBlank(orderLineItemVO.getItemStatus())){
							bbblineItem.setStateAsString(orderLineItemVO.getItemStatus().toUpperCase());
						}
						if(!StringUtils.isBlank(orderLineItemVO.getItemStatusDesc())){
							lineItem.setStateDetail(orderLineItemVO.getItemStatusDesc());
						}
						
						if(bbblineItem.isPorchService() && !orderStatusUpdateVO.getOrderState().equalsIgnoreCase("CANCELLED")){
							invokeScheduleJob(bbblineItem,shippingGroupTypeVO,orderLineItemVO,(BBBOrder)order,orderStatusUpdateVO.getModificationDate());
						}
						//Refactored code to save tacking info on commerce item level.
						List<ShipmentTrackingVO> itemTrackingVOs = orderLineItemVO.getItemTrackingVO();
						if(itemTrackingVOs != null && !itemTrackingVOs.isEmpty()){
							Map<String, TrackingInfo> lineItemTrackerMap =bbblineItem.getTrackingInfosMap();
							if(lineItemTrackerMap == null) {
								lineItemTrackerMap = new HashMap<String, TrackingInfo>();
							}
							//List<RepositoryItem> trackingItems = null;
							for (ShipmentTrackingVO itemTrackingVO : itemTrackingVOs) {
								TrackingInfo trackingInfo = lineItemTrackerMap.get(itemTrackingVO.getTrackingId());
								if (trackingInfo != null) {
									this.logDebug("Tracking info exist in ShippingGroup ["+ shippingGroupTypeVO.getShipmentGroupId()+ "] with trackingInfoID ["
											+ trackingInfo.getRepositoryItem().getRepositoryId()+"]");
									if (itemTrackingVO.getCarrierCode() != null) {
										trackingInfo.setCarrierCode(itemTrackingVO.getCarrierCode());
									}
									if (!StringUtils.isBlank(itemTrackingVO.getTrackingId())) {
										trackingInfo.setTrackingUrl(itemTrackingVO.getTrackingId());
									}

									trackingInfo.setShipmentQty(itemTrackingVO.getShipmentQty());

									Object shipmentDate = itemTrackingVO.getShipModificationDate();
									Calendar calendar = getCalendarFromXMLDate(shipmentDate);
									if (calendar != null) {
										trackingInfo.setActualShipDate(calendar.getTime());
									}

								} else {
									final MutableRepositoryItem trackingInfoItem = this.getOrderRepository().createItem(BBBCoreConstants.TRACKING_INFO_ITEM_DESCRIPTOR);

									trackingInfoItem.setPropertyValue(BBBCoreConstants.CARRIER_CODE_TRACKING_INFO_PROPERTY_NAME, itemTrackingVO.getCarrierCode());
									trackingInfoItem.setPropertyValue(BBBCoreConstants.TRACKING_NUMBER_TRACKING_INFO_PROPERTY_NAME, itemTrackingVO.getTrackingId());
									trackingInfoItem.setPropertyValue(BBBCoreConstants.SHIPMENT_QUANTITY, itemTrackingVO.getShipmentQty());

									Object shipmentDate = itemTrackingVO.getShipModificationDate();
									Calendar calendar = getCalendarFromXMLDate(shipmentDate);
									if (calendar != null) {
										trackingInfoItem.setPropertyValue(BBBCoreConstants.ACTUAL_SHIPDATE_TRACKING_INFO_PROPERTY_NAME, calendar.getTime());
									}

									trackingInfo = new TrackingInfo(trackingInfoItem);
									lineItemTrackerMap.put(trackingInfo.getTrackingNumber(), trackingInfo);
									this.getOrderRepository().addItem(trackingInfoItem);
								}

								//commented code where tracking_info_id is generated based on tracking number
								/*try {
								if(bbblineItem.getPropertyValue("tracking") != null){
									trackingItems = (List<RepositoryItem>) bbblineItem.getPropertyValue("tracking");
									//updating the existing tracking item
									if(!trackingItems.isEmpty() && trackingItems.contains(itemTrackingVO.getTrackingId())){
										MutableRepositoryItem trackingInfoItem = (MutableRepositoryItem) getOrderRepository().getItem(BBBCoreConstants.TRACKING_INFO_ITEM_DESCRIPTOR, itemTrackingVO.getTrackingId());
										if(trackingInfoItem != null){
											trackingInfoItem.setPropertyValue(BBBCoreConstants.CARRIER_CODE_TRACKING_INFO_PROPERTY_NAME, itemTrackingVO.getCarrierCode());
											trackingInfoItem.setPropertyValue(BBBCoreConstants.TRACKING_NUMBER_TRACKING_INFO_PROPERTY_NAME, itemTrackingVO.getTrackingId());
											trackingInfoItem.setPropertyValue("shipmentQty", itemTrackingVO.getShipmentQty());
											Object shipmentDate = itemTrackingVO.getShipModificationDate();

											Calendar calendar = getCalendarFromXMLDate(shipmentDate);
											if (calendar != null) {
												trackingInfoItem.setPropertyValue(BBBCoreConstants.ACTUAL_SHIPDATE_TRACKING_INFO_PROPERTY_NAME, calendar.getTime());
											}
											
											getOrderRepository().updateItem(trackingInfoItem);
										}
									} else {
										//creating new tracking item
										createLineItemTrackInfo(itemTrackingVO, trackingItems);
									}
								} else {
									createLineItemTrackInfo(itemTrackingVO, trackingItems);
								}
								bbblineItem.setPropertyValue("tracking", trackingItems);
								
							} catch (RepositoryException e) {
								vlogError("RepositoryException occurred for order ["+((BBBOrder)order).getOnlineOrderNumber()+"] while updating tracking id ["+itemTrackingVO.getTrackingId()+"] of sku ["+skuId+"]",e);
							}*/
							}

							bbblineItem.setTrackingInfos(lineItemTrackerMap);
						}
						
					} else {
						this.logError("Line item with  :: "+ orderLineItemVO.getItemId() + " Id is not available in the Order :: "+((BBBOrder)order).getOnlineOrderNumber());
					}
				}
			}
		} catch (ShippingGroupNotFoundException e) {
			this.logError("ShippingGroupNotFoundException occurred for order ["+((BBBOrder)order).getOnlineOrderNumber()+"] while getting the ShippingGroup for id ["+shippingGroupTypeVO.getShipmentGroupId()+"]",e);
		} catch (InvalidParameterException e) {
			this.logError("InvalidParameterException occurred for order ["+((BBBOrder)order).getOnlineOrderNumber()+"] while getting the ShippingGroup for id ["+shippingGroupTypeVO.getShipmentGroupId()+"]",e);
		} catch (Exception e) {
			this.logError("Exception occurred for order ["+((BBBOrder)order).getOnlineOrderNumber()+"] while getting the ShippingGroup for id ["+shippingGroupTypeVO.getShipmentGroupId()+"]",e);
		}
	}

	/*private void createLineItemTrackInfo(ShipmentTrackingVO itemTrackingVO, List<RepositoryItem> trackingItems) throws RepositoryException {
		if(trackingItems == null){
			trackingItems = new ArrayList<RepositoryItem>();
		}
		MutableRepositoryItem trackingInfoItem = getOrderRepository().createItem(itemTrackingVO.getTrackingId(), BBBCoreConstants.TRACKING_INFO_ITEM_DESCRIPTOR);
		trackingInfoItem.setPropertyValue(BBBCoreConstants.CARRIER_CODE_TRACKING_INFO_PROPERTY_NAME, itemTrackingVO.getCarrierCode());
		trackingInfoItem.setPropertyValue(BBBCoreConstants.TRACKING_NUMBER_TRACKING_INFO_PROPERTY_NAME, itemTrackingVO.getTrackingId());
		trackingInfoItem.setPropertyValue("shipmentQty", itemTrackingVO.getShipmentQty());
		Object shipmentDate = itemTrackingVO.getShipModificationDate();

		Calendar calendar = getCalendarFromXMLDate(shipmentDate);
		if (calendar != null) {
			trackingInfoItem.setPropertyValue(BBBCoreConstants.ACTUAL_SHIPDATE_TRACKING_INFO_PROPERTY_NAME, calendar.getTime());
		}
		getOrderRepository().addItem(trackingInfoItem);
		trackingItems.add(trackingInfoItem);
	}*/

	/**
	 * @param shipmentTrackingVO2
	 * @param modificationDateObj
	 * @param calender
	 * @return
	 */
	private Calendar getCalendarFromXMLDate(final Object modificationDateObj) {
			this.logDebug("[START] getCalendarFromXMLDate , modificationDateObj: "
					+ modificationDateObj);
		Calendar calender = null;

		if (modificationDateObj instanceof XMLGregorianCalendar) {

			calender = Calendar.getInstance();

			final XMLGregorianCalendar modificationDate = (XMLGregorianCalendar) modificationDateObj;

			if (modificationDate.getDay() != DEFAULT_XML_DATE_VALUE) {
				calender.set(Calendar.DATE, modificationDate.getDay());
			}

			if (modificationDate.getMonth() != DEFAULT_XML_DATE_VALUE) {
				calender.set(Calendar.MONTH, modificationDate.getMonth());
			}

			if (modificationDate.getYear() != DEFAULT_XML_DATE_VALUE) {
				calender.set(Calendar.YEAR, modificationDate.getYear());
			}

			if (modificationDate.getHour() != DEFAULT_XML_DATE_VALUE) {
				calender.set(Calendar.HOUR, modificationDate.getHour());
			}

			if (modificationDate.getMinute() != DEFAULT_XML_DATE_VALUE) {
				calender.set(Calendar.MINUTE, modificationDate.getMinute());
			}

			if (modificationDate.getSecond() != DEFAULT_XML_DATE_VALUE) {
				calender.set(Calendar.SECOND, modificationDate.getSecond());
			}

			if (modificationDate.getMillisecond() != DEFAULT_XML_DATE_VALUE) {
				calender.set(Calendar.MILLISECOND,
						modificationDate.getMillisecond());
			}

			if (modificationDate.getTimezone() != DEFAULT_XML_DATE_VALUE) {
				calender.set(Calendar.ZONE_OFFSET,
						modificationDate.getTimezone());
			}

				this.logDebug("------------ShipModificationDate/XMLGregorianCalendar-------------------");

				if (modificationDate.getDay() != DEFAULT_XML_DATE_VALUE) {
					this.logDebug("Day: " + modificationDate.getDay());
				}
				if (modificationDate.getMonth() != DEFAULT_XML_DATE_VALUE) {
					this.logDebug("Month: " + modificationDate.getMonth());
				}
				if (modificationDate.getYear() != DEFAULT_XML_DATE_VALUE) {
					this.logDebug("Year: " + modificationDate.getYear());
				}
				if (modificationDate.getHour() != DEFAULT_XML_DATE_VALUE) {
					this.logDebug("Hour: " + modificationDate.getHour());
				}
				if (modificationDate.getMinute() != DEFAULT_XML_DATE_VALUE) {
					this.logDebug("Minute: " + modificationDate.getMinute());
				}
				if (modificationDate.getSecond() != DEFAULT_XML_DATE_VALUE) {
					this.logDebug("Second: " + modificationDate.getSecond());
				}
				if (modificationDate.getMillisecond() != DEFAULT_XML_DATE_VALUE) {
					this.logDebug("Millisecond" + modificationDate.getMillisecond());
				}
				if (modificationDate.getTimezone() != DEFAULT_XML_DATE_VALUE) {
					this.logDebug("Timezone" + modificationDate.getTimezone());
				}

				this.logDebug("calender.getTimeInMillis(): "
						+ calender.getTimeInMillis());
		} else {
				this.logDebug("modificationDateObj is not instanceof XMLGregorianCalendar");
				if (modificationDateObj != null) {
					this.logDebug("modificationDateObj Class: "
							+ modificationDateObj.getClass());
				}
			}

			this.logDebug("[END] getCalendarFromXMLDate,  ");

		return calender;
	}
	
	
	
	/**
	 * @param bbblineItem
	 * @param shippingGroupTypeVO
	 * @param orderLineItemVO
	 * @param order 
	 * @param object 
	 */
	private void invokeScheduleJob(BaseCommerceItemImpl bbblineItem, ShippingGroupTypeVO shippingGroupTypeVO,
			OrderLineItemVO orderLineItemVO, BBBOrder order, Object modificationDateObj) {
		ShipmentTrackingVO trackingInfo=getShippingLineItem(orderLineItemVO,shippingGroupTypeVO);
		if(!order.isBopusOrder() &&   null==trackingInfo){
			return;
		}
        RepositoryItem porchItem=bbblineItem.getPorchServiceRef();
       	 try {
       		   String jobId=(String) porchItem.getPropertyValue("jobId");
       		   if(!StringUtils.isBlank(jobId)){
       			PorchSchedlueJobResponseVO responseVO=null;
       			   if(order.isBopusOrder()){
       				final Calendar calendar = this.getCalendarFromXMLDate(modificationDateObj);
       				responseVO =getPorchServiceManager().invokeScheduleAJobAPI(jobId,null,calendar.getTime());
       			   }
       			   else{
       				responseVO =getPorchServiceManager().invokeScheduleAJobAPI(jobId,trackingInfo.getUrl(),trackingInfo.getShipModificationDate());
       			   }
	    	   if(null!=responseVO && !StringUtils.isBlank(responseVO.getJobId())){
	    		   getPorchServiceManager().updateScheduledJobResponse("jobStatus", "SCHEDULED", porchItem);
	    	   }
	    	   else{
	    		   getPorchServiceManager().updateScheduledJobResponse("jobStatus", "SCHEDULED_FAILED", porchItem);
	    	   }
	    	   
       		   }
		} catch (BBBSystemException | BBBBusinessException e) {
			if(isLoggingError()){
				logError("Error while updating porch sheduler date "+e,e);
			}
		}		
		
	}
	
	 

	/**
	 * @param orderLineItemVO
	 * @param shippingGroupTypeVO
	 * @return
	 */
	private ShipmentTrackingVO getShippingLineItem(OrderLineItemVO orderLineItemVO, ShippingGroupTypeVO shippingGroupTypeVO) {
	    final List<ShipmentTrackingVO> shipmentTrackingVOs = shippingGroupTypeVO.getShipmentTrackingVO();
	     
	    ShipmentTrackingVO lineItemtrackingVO = orderLineItemVO.getItemTrackingVO().get(0);
	    for(ShipmentTrackingVO shippingTrackingVO:shipmentTrackingVOs){
	    	if(shippingTrackingVO.getTrackingId().equalsIgnoreCase(lineItemtrackingVO.getTrackingId())){	    		 
	    		return shippingTrackingVO;
	    	}
	    }
		return null;
		
	}
	/**
	 * @return the exceptionMessage
	 */
	public String getExceptionMessage() {
		return this.exceptionMessage;
	}

	/**
	 * @param exceptionMessage the exceptionMessage to set
	 */
	public void setExceptionMessage(final String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
		/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}

	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}
	
	/**
	 * @param orderAttributesType
	 * return true if order received is submitted from same DC, otherwise return false
	 */
	private boolean isOrderFromCurrentDC(OrderAttributesType orderAttributesType) {
		try {
			RqlStatement statement = null;
			if (orderAttributesType.getOrderNumber().startsWith("OLP")) {
				statement = RqlStatement.parseRqlStatement("bopusOrderNumber=?0");
			} else {
				statement = RqlStatement.parseRqlStatement("onlineOrderNumber=?0");
			}
			final RepositoryView view = getOrderRepository().getView(BBBCoreConstants.ORDER);
			Object[] params = new Object[1];
			params[0] = orderAttributesType.getOrderNumber();

			final RepositoryItem[] items = statement.executeQuery(view, params);
			if (items != null && items.length > 0) {
				final RepositoryItem item = items[0];
				String orderCreatedBy = (String) item.getPropertyValue(BBBCoreConstants.CREATED_BY_ORDER_ID);
				if (BBBUtility.isNotEmpty(orderCreatedBy) && orderCreatedBy.startsWith(getDcPrefix())){
					return true;
				}
			}
		} catch (RepositoryException e){
			logError("RepositoryException occured while fetching order ["+orderAttributesType.getOrderNumber()+"]", e);
		}
		logDebug("OrderStatusUpdateManager.updateOrderRepository() | Order ["+orderAttributesType.getOrderNumber()+ "] is not submitted from " + getDcPrefix() +" data center ");
		return false;
	}

	public boolean isFromOrderFeedListener() {
		return fromOrderFeedListener;
	}

	public void setFromOrderFeedListener(boolean fromOrderFeedListener) {
		this.fromOrderFeedListener = fromOrderFeedListener;
	}
}
