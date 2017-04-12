/**
 * 
 */
package com.bbb.commerce.checkout.tibco;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.account.BBBProfileTools;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.inventory.InventoryDecrementVO;
import com.bbb.commerce.inventory.OnlineInventoryManagerImpl;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.email.BBBEmailHelper;
import com.bbb.email.BBBTemplateEmailSender;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;


/**
 * @author alakra
 * 
 */
public class BBBSubmitOrderHandler extends BBBGenericService {
	
	/** Class name for performance logging*/
	private static final String CLASS_NAME = BBBSubmitOrderHandler.class.getName();
	
	/**
	 * Profile Tools
	 */
	private BBBProfileTools mProfileTools;
	
	/**
	 * Order manager
	 */
	private BBBOrderManager mOrderManager;
	
	/**
	 * Transaction Manager instance for scheduler
	 */
	private TransactionManager mTransactionManager;
	

	/**
	 * Exim Manager
	 */
	private BBBEximManager bbbEximPricingManager;
	private String dcPrefix;
	private HashMap<String, String> dataCenterMap;
	private BBBCatalogTools mBBBCatalogTools;
	// variable to get context path from properties file - REST Specific
	private String mStoreContextPath;
	private MutableRepository orderRepository;
	/**
	 * @return the orderRepository
	 */
	public MutableRepository getOrderRepository() {
		return orderRepository;
	}

	/**
	 * @param orderRepository the orderRepository to set
	 */
	public void setOrderRepository(MutableRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	private String payload;
	private String consumer;
	private String producer;
	private String messageFormat;
	
	
	
	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	/**
	 * @return the mStoreContextPath
	 */
	public String getStoreContextPath() {
		return mStoreContextPath;
	}

	/**
	 * @param mStoreContextPath the mStoreContextPath to set
	 */
	public void setStoreContextPath(String mStoreContextPath) {
		this.mStoreContextPath = mStoreContextPath;
	}

	/**
	   * 
	   * @return
	   */
	  public BBBCatalogTools getCatalogTools() {

	    return mBBBCatalogTools;
	  }

	 /**
	   * 
	   * @param pBBBCatalogLoadTools
	   */
	  public void setCatalogTools(final BBBCatalogTools pBBBCatalogTools) {

	    mBBBCatalogTools = pBBBCatalogTools;
	  }

	/**
	 * @return the profileTools
	 */
	public final BBBProfileTools getProfileTools() {
		return mProfileTools;
	}

	/**
	 * @param pProfileTools the profileTools to set
	 */
	public final void setProfileTools(BBBProfileTools pProfileTools) {
		mProfileTools = pProfileTools;
	}

	/**
	 * @return the orderManager
	 */
	public final BBBOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @param pOrderManager the orderManager to set
	 */
	public final void setOrderManager(BBBOrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	/**
	 * @return the transactionManager
	 */
	public final TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	/**
	 * @param pTransactionManager the transactionManager to set
	 */
	public final void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}
	
	/**
	 * @return the bbbEximPricingManager
	 */
	public BBBEximManager getBbbEximPricingManager() {
		return bbbEximPricingManager;
	}

	/**
	 * @param bbbEximPricingManager the bbbEximPricingManager to set
	 */
	public void setBbbEximPricingManager(BBBEximManager bbbEximPricingManager) {
		this.bbbEximPricingManager = bbbEximPricingManager;
	}

	public void submitOrder(SubmitOrderVO pSubmitOrder) throws BBBSystemException {
		final String methodName = CLASS_NAME + ".submitOrder()";
		
		if(pSubmitOrder != null && pSubmitOrder.getOrder() != null) {
			String orderID = pSubmitOrder.getOrder().getId();
			logInfo("START: Submitting order [" + orderID + "] over TIBCO message bus");
	
			BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL, methodName);
	
			try {
				pSubmitOrder.setServiceName(BBBCoreConstants.TIBCO_MSG_ORDER_SUBMIT);
				extractedSendTextMessage(pSubmitOrder);
	
			} catch (BBBSystemException se) {
				String msg = BBBCoreErrorConstants.CHECKOUT_ERROR_1015 + ": Error while submitting order [" + orderID + "] over TIBCO message bus";
				logError(msg, se);
				throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1057, msg, se);
			} catch (BBBBusinessException be) {
				String msg = BBBCoreErrorConstants.CHECKOUT_ERROR_1015 + ": Error while submitting order [" + orderID + "] over TIBCO message bus";
				logError(msg, be);
				throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1057,msg, be);
			} catch (Exception be) {
                String msg = BBBCoreErrorConstants.CHECKOUT_ERROR_1015 + ": Error while submitting order [" + orderID + "] over TIBCO message bus";
                logError(msg, be);  
                throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1057,msg, be);
            } finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
			}
			logInfo("END: Submitting order [" + orderID + "] over TIBCO message bus");
		}
	}

	protected void extractedSendTextMessage(ServiceRequestIF serviceRequest)
			throws BBBBusinessException, BBBSystemException {
		ServiceHandlerUtil.sendTextMessage(serviceRequest);
	}
	
	/**
	 * This method is used to make exim lock api call in order to 
	 * disable personalisable item from further modification.
	 * 
	 * @param bbbOrder
	 *            
	 */
	private void invokeLockAPI(final BBBOrder bbbOrder) {
		final String methodName = CLASS_NAME + ".invokeLockAPI()";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.EXIM_LOCK_API, methodName);
		logDebug("START: invokeLockAPI method with orderId: [" + bbbOrder.getId() + "]");
		
		@SuppressWarnings("unchecked")
		final List<CommerceItem> commerceItems = bbbOrder.getCommerceItems();

		for (final CommerceItem commerceItem : commerceItems) {
			if (commerceItem instanceof BBBCommerceItem) {
				BBBCommerceItem bbbItem= (BBBCommerceItem) commerceItem;
				if (!StringUtils.isEmpty(bbbItem.getReferenceNumber()) && ! BBBCoreConstants.MINUS_ONE.equals(bbbItem.getReferenceNumber())  && "true".equalsIgnoreCase(getBbbEximPricingManager().getKatoriAvailability())) {
					logDebug("Refrence Number: " + bbbItem.getReferenceNumber());
					String response = null;
					try {
						response = getBbbEximPricingManager().invokeLockAPI(commerceItem.getAuxiliaryData().getProductId(), bbbItem.getReferenceNumber());
					} catch (BBBSystemException e) {
						logError("BBBSystemException Error in invoking Lock API refNum:["+bbbItem.getReferenceNumber() +"]" ,e );
					} catch (BBBBusinessException e) {
						logError("BBBBusinessException Error in invoking Lock API refNum:["+bbbItem.getReferenceNumber() +"]" ,e );
					}
					logDebug("Response from Lock API call: " + response);
					
					if(response ==null || !"success".equalsIgnoreCase(response)){
						logError("Error in calling Lock API from submit order page, refNum:["+bbbItem.getReferenceNumber() +"]");
					}	
				}
			}
		}
		logDebug("END: invokeLockAPI method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.EXIM_LOCK_API, methodName);
	}
	
	/**
	 * 
	 * @param bbbOrder
	 * @param pRequest - null in case of TibcoOrderMessage scheduler. We are not sending while processing queued orders in Tibco
	 */
	public void processSubmitOrder(BBBOrder bbbOrder, HttpServletRequest pRequest){
		logInfo("START: Processing order for submission");
		
		boolean rollback = false;
		boolean updateSubstatusFail = false;
		TransactionDemarcation td = null;

		if(bbbOrder != null){
			td = new TransactionDemarcation();
			logInfo("START: Processing order [" + bbbOrder.getId() + "] for submission");
			
			try {
				/* Start the transaction */
				// BBBSL-2636. Added synchronization while updating the order.
				td.begin(getTransactionManager());
				synchronized (bbbOrder) {
					if(!(bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY)|| bbbOrder.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY))){
					//commenting below line as this is not required because we are picking orders with UNSUBMITTED status
					//getOrderManager().updateOrderSubstatus(bbbOrder, BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED);
					submitOrderMessage(bbbOrder);
					updateOrder(bbbOrder);
					invokeLockAPI(bbbOrder);
					logInfo("Successfully Submitted order : " + bbbOrder.getId());
					}
				}
			} catch (TransactionDemarcationException e) {
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + ": Transaction demarcation failure while processing Submit Order request", e);
			
				rollback = true;
				updateSubstatusFail = true;
			} catch (BBBSystemException e) {
				/* Rollback the transaction */
				rollback = true;	
				updateSubstatusFail = true;
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 + ": System exception while processing Submit Order request. updating updateSubstatusFail flag to true ", e);
			
			} catch (Exception e) {
				/* Rollback the transaction */
				rollback = true;	
				updateSubstatusFail = true;
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 + ": Exception while processing Submit Order request. updating updateSubstatusFail flag to true ", e);
			
			} finally {
				try {
					if (rollback) {
						logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + ": Transaction failure while processing Submit Order request. Rolling back the transaction. Will retry again");
					}
					/* Complete the transaction */
					td.end(rollback);
				} catch (TransactionDemarcationException tde) {
					logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + ": Transaction failure while processing Submit Order request", tde);
				}
			}
			
			if (updateSubstatusFail) {
				rollback = false;
				td = new TransactionDemarcation();
				try {
					td.begin(getTransactionManager());
					synchronized (bbbOrder) {
						updateOrderStatusFail(bbbOrder);
						logInfo("Submission Failure for order [" + bbbOrder.getId() + "]");
					}
				} catch (TransactionDemarcationException e) {
					rollback = true;
					logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016+ ": Transaction demarcation failure while updating order substatus to FAILED",e);
				} finally {
					try {
						if (rollback) {
							logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016
									+ ": Transaction failure while updating order substatus to FAILED. Rolling back the transaction.");
						}
						/* Complete the transaction */
						td.end(rollback);
					} catch (TransactionDemarcationException tde) {
						logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016	+ ": Transaction failure while updating order substatus to FAILED",tde);
					}
				}
			}
			
			logInfo("END: Processing order [" + bbbOrder.getId() + "] for submission");
		}
		
		logInfo("END: Processing order for submission");
	}

	/**
	 * @param bbbOrder
	 * @throws BBBSystemException
	 */
	private void updateOrder(BBBOrder bbbOrder) throws BBBSystemException {
		logDebug("Updating Order [" + bbbOrder.getId() + "] substatus to [" + BBBCoreConstants.ORDER_SUBSTATUS_SUBMITTED + "]");
		getOrderManager().updateOrderSubstatus(bbbOrder, BBBCoreConstants.ORDER_SUBSTATUS_SUBMITTED);
	}

	private void updateOrderStatusFail(BBBOrder bbbOrder) {
		logDebug("Updating Order [" + bbbOrder.getId() + "] substatus to [" + BBBCoreConstants.ORDER_SUBSTATUS_FAILED + "]");
		try {
		getOrderManager().updateOrderSubstatus(bbbOrder, BBBCoreConstants.ORDER_SUBSTATUS_FAILED);
		}
		catch (BBBSystemException e) {
			logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003+ ": System exception while updating order substatus through getOrderManager().updateOrderSubstatus",e);
			try {
				MutableRepositoryItem orderItem =  this.getOrderRepository().getItemForUpdate(bbbOrder.getId(), BBBCoreConstants.ORDER);
				orderItem.setPropertyValue(BBBCoreConstants.ORDER_SUB_STATUS, BBBCoreConstants.ORDER_SUBSTATUS_FAILED);
				this.getOrderRepository().updateItem(orderItem);
			} catch (RepositoryException e1) {
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003+ ": RepositoryException occured while updating order substatus to FAILED",e1);
			}
		}
	}
	
	/**
	 * @param pMessage
	 * @throws BBBSystemException
	 */
	private void submitOrderMessage(BBBOrder pOrder) throws JMSException, BBBSystemException {
		logDebug("START: Submitting Order [" + pOrder.getId() + "] Message to TIBCO");
		
		SubmitOrderVO submitOrderVO = new SubmitOrderVO();
		submitOrderVO.setOrder(pOrder);
		logDebug("Submitting Order [" + pOrder.getId() + "] Message to TIBCO");		
		submitOrder(submitOrderVO);

		logDebug("END: Submitting Order [" + pOrder.getId() + "] Message to TIBCO");
	}

	public void submitInventoryMesssage(BBBOrder pOrder) throws BBBBusinessException, BBBSystemException {
		long startTime =System.currentTimeMillis();
		logDebug("START: Submitting Inventory Decrement Message");
		InventoryDecrementVO inventoryRequest = createInventoryRequest(pOrder);
		if(inventoryRequest.getListOfInventoryVos().size() > 0) {
			InventoryVO[] inventoryVOs = new InventoryVO[inventoryRequest.getListOfInventoryVos().size()];
			inventoryRequest.getListOfInventoryVos().toArray(inventoryVOs);
			getInventoryManager().decrementInventoryStock(inventoryVOs);
			inventoryRequest.setServiceName(BBBCoreConstants.INVENTORY_DECREMENT_SERVICE);
			extractedSendTextMessage(inventoryRequest);
			
		}
		long endTime =System.currentTimeMillis();
		logDebug("END: Submitting Inventory Decrement Message. It took time to decrement, prepare and send tibco message="+ (endTime-startTime));

	}
	
	public void decrementInventoryRepository(BBBOrder pOrder) throws BBBBusinessException, BBBSystemException {
		logDebug("START: decrementInventoryRepository");
		InventoryDecrementVO inventoryRequest = createInventoryRequest(pOrder);
		if(inventoryRequest.getListOfInventoryVos().size() > 0){
			InventoryVO[] inventoryVOs = new InventoryVO[inventoryRequest.getListOfInventoryVos().size()];
			inventoryRequest.getListOfInventoryVos().toArray(inventoryVOs);
			getInventoryManager().decrementInventoryStock(inventoryVOs);
		}
		logDebug("END: decrementInventoryRepository");
	}

	@SuppressWarnings("rawtypes")
    public void sendMail(Order pOrder, HttpServletRequest pRequest) {
		logDebug("START: Sending Order Summary Mail to user");
		
		if(pRequest != null && pOrder instanceof BBBOrder && ((BBBOrder)pOrder).getBillingAddress() != null){
			BBBAddress bbbAddress = (BBBAddress)((BBBOrder)pOrder).getBillingAddress();
			if(bbbAddress.getEmail() != null){
				Map emailParams = collectParams(bbbAddress, pOrder, pRequest);
				try{
					DynamoHttpServletRequest request = getDynamoHttpServletRequest(pRequest);
					TemplateEmailInfoImpl template = (TemplateEmailInfoImpl) request.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo");
					if(template==null){
						throw new TemplateEmailException("Unable to resolve template:/atg/userprofiling/email/EmailOrderConfirmEmailInfo");
					}
					extractedSendEmail(emailParams, template);
				}catch (TemplateEmailException e) {
					logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + ": TemplateEmailException occurred while sending Order Confirmation Email",e);
				}
			}
		}
		logDebug("END: Sending Order Summary Mail to user");
	}
	
	protected DynamoHttpServletRequest getDynamoHttpServletRequest(HttpServletRequest pRequest){
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		if(request == null && pRequest != null){
			request = (DynamoHttpServletRequest)pRequest;
		}
		return request;
	}

	protected void extractedSendEmail(Map emailParams, TemplateEmailInfoImpl template) throws TemplateEmailException {
		BBBEmailHelper.sendEmail(emailParams, getEmailSender(), template);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map collectParams(BBBAddress pAddress, Order pOrder, HttpServletRequest pRequest){
		Map emailParams = new HashMap();
		String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
		emailParams.put(getTemplateUrlName(), getTemplateUrl());
		emailParams.put(getSiteIdParamName(), pOrder.getSiteId());
		
		emailParams.put("recipientName", pAddress
				.getFirstName());
		emailParams.put("recipientEmail", pAddress
				.getEmail());
		emailParams.put("message", "Order Confirmation");
		emailParams.put("siteId", pOrder.getSiteId());
		emailParams.put("order", pOrder);
		
		HashMap placeHolderValues = new HashMap();
		final Calendar currentDate = Calendar.getInstance();				
		long uniqueKeyDate = currentDate.getTimeInMillis();
		final String profileId = pOrder.getProfileId();
		String emailPersistId = profileId + uniqueKeyDate;
		
		DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
		final Profile profile = (Profile) dynamoRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		if(profile.isTransient()){
			emailParams.put(BBBCoreConstants.USER_ID,((BBBOrder)pOrder).getBillingAddress().getEmail());
		}else{
			emailParams.put(BBBCoreConstants.USER_ID, profileId);
		}
	
		placeHolderValues.put("emailType", this.getEmailType());
		placeHolderValues.put("frmData_siteId", pOrder.getSiteId());	
		placeHolderValues.put("frmData_orderId", pOrder.getId());
		placeHolderValues.put("frmData_onlineOrderNumber", ((BBBOrder)pOrder).getOnlineOrderNumber());
		placeHolderValues.put("frmData_bopusOrderNumber", ((BBBOrder)pOrder).getBopusOrderNumber());
		placeHolderValues.put("emailPersistId", emailPersistId);		
		Date submittedDate = pOrder.getSubmittedDate();
		SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy", pRequest.getLocale());
		String stringDate = "";
		if(submittedDate != null){
			stringDate = s.format(submittedDate);
		}
		placeHolderValues.put("frmData_orderDate", stringDate);
		placeHolderValues.put("frmData_firstName", pAddress.getFirstName());
		placeHolderValues.put("frmData_lastName", pAddress.getLastName());
		 
		emailParams.put("placeHolderValues", placeHolderValues);
		if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
			try {
				//set context path from properties file in case of Mobile Web and Mobile App
				emailParams.put("contextPath", getStoreContextPath()); 
				List<String> configValue = getCatalogTools().getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);
				if(configValue != null && configValue.size() > 0){
					//set serverName from config key
					emailParams.put("serverName", configValue.get(0)); 
				}
			} catch (BBBSystemException e) {
				logError("BBBSubmitOrderHandler.collectParams :: System Exception occured while fetching config value for config key " + BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, e);
			} catch (BBBBusinessException e) {
				logError("BBBSubmitOrderHandler.collectParams :: Business Exception occured while fetching config value for config key " + BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, e);
			}
		}
		else{
			emailParams.put("serverName", pRequest.getServerName());
			emailParams.put("contextPath", pRequest.getContextPath());
		}
		
		return emailParams;
	}
	
	private String mEmailType;
	private BBBTemplateEmailSender mEmailSender;
	private String mTemplateUrlName;
	private String mTemplateUrl;
	private String mSiteIdParamName;
	
	public String getTemplateUrlName() {
		return mTemplateUrlName;
	}
	public void setTemplateUrlName(final String pTemplateUrlName) {
		mTemplateUrlName = pTemplateUrlName;
	}	
	
	public void setTemplateUrl(final String pTemplateUrl) {
		mTemplateUrl = pTemplateUrl;
	}	
	public String getTemplateUrl() {
		return mTemplateUrl;
	}

	public String getSiteIdParamName() {
		return mSiteIdParamName;
	}

	public void setSiteIdParamName(final String pSiteIdParamName) {
		mSiteIdParamName = pSiteIdParamName;
	}
	
	public String getEmailType() {
		return mEmailType;
	}

	public void setEmailType(String pEmailType) {
		this.mEmailType = pEmailType;
	}	

	/**
	 * @return the emailSender
	 */
	public BBBTemplateEmailSender getEmailSender() {
		return mEmailSender;
	}


	/**
	 * @param pEmailSender the emailSender to set
	 */
	public void setEmailSender(BBBTemplateEmailSender pEmailSender) {
		mEmailSender = pEmailSender;
	}

	@SuppressWarnings("unchecked")
    private InventoryDecrementVO createInventoryRequest(BBBOrder pOrder) {
		InventoryDecrementVO inventoryRequest = new InventoryDecrementVO();
		List<InventoryVO> inventories = new ArrayList<InventoryVO>();
		List<CommerceItem> items = (List<CommerceItem>)pOrder.getCommerceItems();
		String updateAllInventory = BBBCoreConstants.FALSE;
		List<String> config = null;
		try {
			config = getCatalogTools().getContentCatalogConfigration(BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG);
		} catch (Exception e) {
			logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1014 + ": Error while retrieving configure keys value for [" + BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG + "]", e);
		}
		if(config != null && config.size() > 0) {
			updateAllInventory = config.get(0);
		}
		
		InventoryVO inventory = null;
		BBBCommerceItem bbbItem = null;
		for (CommerceItem item : items) {
			if(item instanceof BBBCommerceItem) {
				bbbItem = (BBBCommerceItem) item;				
				/* Check if the commerce item is NON-BOPUS */
				if (StringUtils.isBlank(bbbItem.getStoreId())) {
					
					/*If this is non-vdc item, and the flag is turned off - indicating not to update inventory for non-vdc item, skip the inventory update*/
					if(BBBCoreConstants.FALSE.equalsIgnoreCase(updateAllInventory) && !bbbItem.isVdcInd()){
						continue;
					}
					
					inventory = new InventoryVO();
					inventory.setSkuID(item.getCatalogRefId());
					inventory.setSiteID(pOrder.getSiteId());
					inventory.setOrderedQuantity(item.getQuantity());
					inventory.setDeliveryType(BBBCoreConstants.SHIPPING_DT);
					inventories.add(inventory);
				}
			}
		}
		
		final String uniqueId = UUID.randomUUID().toString();		
		inventoryRequest.setConsumer(getConsumer());
		inventoryRequest.setProducer(getProducer());
		inventoryRequest.setPayLoad(getPayload());
		inventoryRequest.setMessageFormat(getMessageFormat());
		inventoryRequest.setMessagePriority(BigInteger.valueOf(new Long(1)));
		inventoryRequest.setListOfInventoryVos(inventories);
		inventoryRequest.setMessageCreateTS(getXMLGregorianCalendar());
		inventoryRequest.setMessageId(uniqueId);		
		inventoryRequest.setCoRelationId(pOrder.getOnlineOrderNumber());
		inventoryRequest.setConversationId(getPayload()+"|"+uniqueId);
		inventoryRequest.setDataCenter(this.getDataCenterMap().get(this.getDcPrefix()));
		inventoryRequest.setOrderSubmissionDate(pOrder.getSubmittedDate());
		inventoryRequest.setSite(pOrder.getSiteId());
		return inventoryRequest;
	}

	/**
	 * This method return XMLGregorian Calendar time format
	 * @return
	 */
	private XMLGregorianCalendar getXMLGregorianCalendar() {
		GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
		
		XMLGregorianCalendar xmlGregorianCalendar = null;
		try {
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			logError("Error in fetch date in xmlGregorianCalender format",e);
		}
		return xmlGregorianCalendar;
	}
	
	
	
	
	
	private OnlineInventoryManagerImpl mInventoryManager;

	public OnlineInventoryManagerImpl getInventoryManager() {
		return mInventoryManager;
	}

	public void setInventoryManager(OnlineInventoryManagerImpl mInventoryManager) {
		this.mInventoryManager = mInventoryManager;
	}

	public String getDcPrefix() {
		return dcPrefix;
	}

	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}

	public HashMap<String, String> getDataCenterMap() {
		return dataCenterMap;
	}

	public void setDataCenterMap(HashMap<String, String> dataCenterMap) {
		this.dataCenterMap = dataCenterMap;
	}
	
	
	
}