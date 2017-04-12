package com.bbb.internationalshipping.fulfillment.poservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import atg.commerce.CommerceException;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.internationalshipping.pofile.OrderFeed;
import com.bbb.internationalshipping.manager.InternationalCheckoutManager;
import com.bbb.internationalshipping.vo.pofileprocessing.BBBInternationalOrderPOFileVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

/**
 * 
 *
 */

public class IntlPOFileUnMarshaller extends BBBGenericService {
	
	
	private static final String PO_FILE_ENCODING_UTF_8 = "UTF-8";
	private static final String INTL_PO_PROCESS = "INTL_PO_PROCESS";
	/** The Constant SHIPPING_TYPE. */
	public static final String SHIPPING_TYPE="Shipping";

	/** The Constant BILLING_TYPE. */
	public static final String BILLING_TYPE="Billing";
	
	/** The Constant ORDER_DESC. */
	public static final String ORDER_DESC="order";
	
	public static final String INTL_ORDER_DESC="internationalOrder";
		
	/** The Constant ORDER_DESC. */
	public static final String ONLINE_ORDER_ID="onlineOrderNumber";
	
	private static JAXBContext context;
	
	/** The orderId. */
	private Repository orderRepository;
	private String dcPrefix;
	public String getDcPrefix() {
		return dcPrefix;
	}
	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}

	/** The catalog tools. */
	private BBBCatalogTools catalogTools;


	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools the catalogTools to set
	 */
	public final void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	private Map<String, String> internationalPOUnprocessedFolder = new HashMap<String, String>();


	public Map<String, String> getInternationalPOUnprocessedFolder() {
		return internationalPOUnprocessedFolder;
	}

	public void setInternationalPOUnprocessedFolder(
			Map<String, String> internationalPOUnprocessedFolder) {
		this.internationalPOUnprocessedFolder = internationalPOUnprocessedFolder;
	}

	
	/**
	 * @return the orderRepository
	 */
	public final Repository getOrderRepository() {
		return orderRepository;
	}
	/**
	 * @param orderRepository the orderRepository to set
	 */
	public final void setOrderRepository(final Repository orderRepository) {
		this.orderRepository = orderRepository;
	}
	private InternationalCheckoutManager intlCheckoutManager;
	
	public InternationalCheckoutManager getIntlCheckoutManager() {
		return intlCheckoutManager;
	}
	public void setIntlCheckoutManager(
			InternationalCheckoutManager intlCheckoutManager) {
		this.intlCheckoutManager = intlCheckoutManager;
	}
	private MutableRepository  internationalOrderRepository;
	
	public MutableRepository getInternationalOrderRepository() {
		return internationalOrderRepository;
	}
	public void setInternationalOrderRepository(
			MutableRepository internationalOrderRepository) {
		this.internationalOrderRepository = internationalOrderRepository;
	}
	/**
	 * Unmarshalling International Order PO file
	 * 
	 * @return OrderFeed
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceException 
	 * @throws JAXBException
	 */

	public final OrderFeed unmarshalPOFile(final String decryptedPoFile,File unprocessed,String siteId) throws  BBBBusinessException, BBBSystemException, CommerceException {
		    
		logDebug("Entering - International Order PO File Unmarshaller Method Name [intlUnmarshalPOFile] return type: OrderFeed");
		
		BBBInternationalOrderPOFileVO orderPOFileVO= null;
		  OrderFeed orderFeed=null;
		try
		{
			if(null == context){
				context = JAXBContext.newInstance(OrderFeed.class);
			}
		    final Unmarshaller unMarshaller = context.createUnmarshaller();
		   
		    InputStream xml;
			try {
				xml = new ByteArrayInputStream(decryptedPoFile.getBytes(PO_FILE_ENCODING_UTF_8));
			} catch (UnsupportedEncodingException encodingEx) {
				logError("Error while unmarshalling PO File: " + unprocessed + " - " + encodingEx.getMessage() + ". Proceeding with default encoding");
				xml = new ByteArrayInputStream(decryptedPoFile.getBytes());
			}	    
		    final OrderFeed itemJaxb = (OrderFeed) unMarshaller.unmarshal(xml);
		    orderFeed = itemJaxb;
		    //Added code for Data center
		    if(orderFeed!=null)
		    {
		    	//Check if Single Datacenter exists
		    	boolean singleDataCenter=false;
		    	final List<String> configValue =  getCatalogTools()
						.getAllValuesForKey(
								BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
								BBBInternationalShippingConstants.INTL_SINGLE_DATACENTER);
		    	if(!BBBUtility.isListEmpty(configValue)) {
		    		singleDataCenter = Boolean.valueOf(configValue.get(0));
				}
		    	if(!singleDataCenter)
		    	{
		    		String dataCenter = this.getDcPrefix();
		    		String customData =orderFeed.getOrder().get(0).getDomesticBasket().getOrderDetails().getCustom();
		    		String arg[]=customData.split(",");
		    		for(String val:arg)
		    		{
		    			String cdata[]=val.split(":");
		    			if(!BBBUtility.isEmpty(cdata))
		    			{
		    				//Check for Datacenter property  present in custom Data
		    				if(cdata[0].equalsIgnoreCase(BBBInternationalShippingConstants.DATACENTER))
		    				{
		    					//Check if Datacenter is same as in the order 
	    					if(!BBBUtility.isEmpty(cdata[1]) && cdata[1].length() >= 3 && !(cdata[1].substring(0, 3)).equalsIgnoreCase(dataCenter))
		    					{
	    						//Datacenter not same as of order ,so no processing
	    						orderPOFileVO =  new BBBInternationalOrderPOFileVO();
	    						orderPOFileVO.setRetry(true);
	    						//Added code for Not processed orders due to datacenter unmatch
	    						String unprocessedPath=null;
	    						
	    						unprocessedPath= BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, getInternationalPOUnprocessedFolder().get(siteId));
	    						if(unprocessed!=null && unprocessedPath!=null)
	    						{
	    							if (BBBFileUtils.moveFile(unprocessedPath, unprocessed)){
	    								vlogInfo("File --->"+unprocessed+" moved successfully "+unprocessedPath);
	    							}
	    							else{
	    								logError("Error while moving File --->" + unprocessed + " to folder =" + unprocessedPath);
	    							}
	    						}
	    						return null;
	    						
		    					}
		    				}
		    			}
		    		}
		    		//orderPOFileVO=getIntlOrderPOFile(orderFeed);
		    	}
		    	/*else
		    	{
		    		//orderPOFileVO=getIntlOrderPOFile(orderFeed);
		    	}*/
		    }
	          
		
		} catch (JAXBException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"International Order PO File Unmarshaller Method Name [getIntlOrderPOFile]"), e);
			throw new BBBBusinessException(BBBInternationalShippingConstants.JAXB_EXCEPTION_ERROR_CODE,
					BBBInternationalShippingConstants.JAXB_EXCEPTION_MESSAGE);
		}
	
		 logDebug("Exiting - International Order PO File Unmarshaller Method Name [intlUnmarshalPOFile] ");

		 return orderFeed;
    }
	
	
	
	/**
	 * Method to set credit card details , 
	 * billing and shipping address to BBBInternationalOrderPOFileVO
	 * 
	 * @param orderFeed
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 * @throws CommerceException 
	 */
//	/*private final BBBInternationalOrderPOFileVO getIntlOrderPOFile(final OrderFeed orderFeed ) throws BBBBusinessException, BBBSystemException, CommerceException
//	{
//		logDebug("Entering - International Order PO File Unmarshaller " +
//				"Method Name [getIntlOrderPOFile]"); 
//			
//		final BBBInternationalOrderPOFileVO orderPOFileVO =  new BBBInternationalOrderPOFileVO();
//				if(orderFeed != null)
//					{
//					
//					
//						 final OrderId order=(OrderId) orderFeed.getOrder().get(0).getOrderId(); 
//						 if(order.getMerchantOrderId()!=null)
//						 {
//						      /*
//							  * BBBSL-4857 | Removed the check for order.getMerchantOrderRef() being null since it can never null as we are sending the DS order id now in order XML.
//							  * Hence removed the check since it failed to handle the scenario where checkoutSuccess was not called for an order and when PO file was received for processing, it failed to submit the order.
//							  */
//								String orderId = getBBBOrderId(orderFeed, order.getMerchantOrderId());
//							 
//							 if(orderId != null && !orderId.isEmpty()) {
//									 orderPOFileVO.setOrderId(orderId);
//									 orderPOFileVO.setE4xOrderId(order.getE4XOrderId());
//									 orderPOFileVO.setMerchantOrderId(order.getMerchantOrderId());
//									 orderPOFileVO.setFraudState( orderFeed.getOrder().get(0).getFraudState() == null ? null :  orderFeed.getOrder().get(0).getFraudState());
//							}
//						 }
//						 else
//						 {
//							 throw new BBBBusinessException(BBBInternationalShippingConstants.INTL_MERCHANT_ID_NULL_ERROR_CODE, BBBInternationalShippingConstants.INTL_MERCHANT_ID_NULL_MESSAGE);
//						 }
//						
//						 
//						if(getIntlBillingAddress(orderFeed)!=null)
//						{
//							logDebug("Inside- International Order PO File Unmarshaller " +
//									"Method Name [getIntlOrderPOFile] : " +
//									"setting billing address details to BBBInternationalOrderPOFileVO");
//							
//							orderPOFileVO.setBillingIntlAddress(getIntlBillingAddress(orderFeed));
//						}
//						if(getIntlShippingAddress(orderFeed)!=null)
//						{
//							logDebug("Inside- International Order PO File Unmarshaller " +
//									"Method Name [getIntlOrderPOFile] : " +
//									"setting shipping address details to BBBInternationalOrderPOFileVO");
//							
//							orderPOFileVO.setShippingIntlAddress(getIntlShippingAddress(orderFeed));
//						}
//						
//						if(getIntlCreditCardDetails(orderFeed)!=null)
//						{
//							logDebug("Inside- International Order PO File Unmarshaller " +
//									"Method Name [getIntlOrderPOFile] : " +
//									"setting credit card details to BBBInternationalOrderPOFileVO");
//											
//							orderPOFileVO.setBbbIntlCreditCardVO(getIntlCreditCardDetails(orderFeed));
//						}
//						
//					}
//									
//			logDebug("Exiting - International Order PO File Unmarshaller" +
//					" Method Name [intlUnmarshalPOFile]");
//		return orderPOFileVO;
//		
//	}*/
//	
	
		/**
		 * Method to get orderId on the basis of merchant order id
		 * 
		 * @param orderFeed
		 * @param mrechantId
		 * @return
		 * @throws BBBBusinessException 
		 * @throws BBBSystemException 
		 * @throws CommerceException 
		 */
		public final String getBBBOrderId(final OrderFeed orderFeed,final String merchantId) throws BBBBusinessException, BBBSystemException, CommerceException
		{
			logDebug("Entering - International Order PO File Unmarshaller" +
					" Method Name [getBBBOrderId]");
			
				RepositoryView view = null;
				QueryBuilder queryBuilder = null;
				QueryExpression queryMerchantOrderId;
				QueryExpression queryMerchatnId;
				RepositoryItem item = null;
				String orderId = null;
				try{
					
					view = getOrderRepository().getView(ORDER_DESC);
					queryBuilder = view.getQueryBuilder();
					
					queryMerchantOrderId = queryBuilder.createPropertyQueryExpression(ONLINE_ORDER_ID);
					queryMerchatnId = queryBuilder.createConstantQueryExpression(merchantId);
					
					Query[] queries = new Query[1];
			        queries[0] = queryBuilder.createComparisonQuery(queryMerchantOrderId, queryMerchatnId, QueryBuilder.EQUALS);
			        
			        Query query = queryBuilder.createAndQuery(queries);
			        
			        logDebug("Exiting - International Order PO File Unmarshaller" +
							" Method Name [getBBBOrderId] : Executing Query to retrieve data : "+query);
								        

				        if(view.executeQuery(query) != null){
					        	
					        	item = view.executeQuery(query)[0];	
					        	
						        	if(item != null){
						        		orderId =  item.getRepositoryId();
						        	}
					        	}
				        else
				        {
				        	String countrycode=null;
				        	String currencyCode=null;
				        	String fraudState =orderFeed.getOrder().get(0).getFraudState() == null ? null :  orderFeed.getOrder().get(0).getFraudState();
				        	String intlOrder =orderFeed.getOrder().get(0).getOrderId().getE4XOrderId()== null ? null :  orderFeed.getOrder().get(0).getOrderId().getE4XOrderId();
				        	view = getInternationalOrderRepository().getView(INTL_ORDER_DESC);
							queryBuilder = view.getQueryBuilder();
							
							queryMerchantOrderId = queryBuilder.createPropertyQueryExpression(ONLINE_ORDER_ID);
							queryMerchatnId = queryBuilder.createConstantQueryExpression(merchantId);
							
							queries = new Query[1];
							queries[0]  = queryBuilder.createComparisonQuery(queryMerchantOrderId, queryMerchatnId, QueryBuilder.EQUALS);
					        
					        query = queryBuilder.createAndQuery(queries);
					        RepositoryItem[]  repoResult=view.executeQuery(query);
					        if(repoResult!=null && repoResult.length >=1){
					        	item = repoResult[0];
					        	orderId = (String)item.getPropertyValue(BBBInternationalShippingConstants.ORDER_ID);
				            	countrycode = (String)item.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE);
				            	currencyCode = (String)item.getPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE);
				            	boolean createOrderOOB = false;
					        	RepositoryItem orderItem=  getOrderRepository().getItem(orderId, "order") ;
					        	if(orderItem!=null) {
					        		createOrderOOB = true;
					        	}
				            	this.getIntlCheckoutManager().updateInternationalOrder(orderId, merchantId, fraudState, countrycode, currencyCode, intlOrder, createOrderOOB);
				    			this.logPersistedInfo(INTL_PO_PROCESS, orderId, null, null, null, null,null, null, new Timestamp(new Date().getTime()).toString(), INTL_PO_PROCESS, "Online Order " + merchantId + " not found in BBB_ORDER, recreating from BBB_INTL_ORDER");
				            }
					        else
					        {
					        	throw new BBBBusinessException(BBBInternationalShippingConstants.ATG_ORDER_ID_NOT_IN_REPOSITORY_ERROR_CODE, BBBInternationalShippingConstants.ATG_ORDER_ID_NOT_IN_REPOSITORY_MESSAGE);
					        }
				        }
					}
								
					catch(RepositoryException e)
					{
						logError(LogMessageFormatter.formatMessage(null, "RepositoryException : Order id NULL"), e);
						throw new BBBBusinessException(BBBInternationalShippingConstants.REPOSITORY_EXCEPTION_ERROR_CODE, 
								BBBInternationalShippingConstants.REPOSITORY_EXCEPTION_MESSAGE);
					}
				logDebug("Exiting - International Order PO File Unmarshaller" +
						" Method Name [getBBBOrderId]");
				return orderId;
		}
		/**
		 * Method to get International billing address details
		 * 
		 * @param orderFeed
		 * @return
		 */
		/*private BBBIntlAddressVO getIntlBillingAddress(final OrderFeed orderFeed)
		{
			logDebug("Entering - International Order PO File " +
					"Unmarshaller Method Name [getIntlBillingAddress]");
			
			BBBIntlAddressVO billAddress = null;
			 
			if (null != orderFeed) {
	        	
				billAddress = new  BBBIntlAddressVO();
	            final DomesticProfile domesticProfile = orderFeed.getOrder().get(0).getDomesticProfile();
	           final List<Address> add=domesticProfile.getAddress();
	           
	            
	            for(final Address address:add){
	            
		            if (BILLING_TYPE.equals(((Address) address).getType()))
		            {             	
		            	billAddress.setType(address.getType());
		            	billAddress.setAddress1(address.getAddressLine1());
		            	billAddress.setAddress2(address.getAddressLine2());
		            	billAddress.setAddress3(address.getAddressLine3());
		            	billAddress.setCity(address.getCity());
		            	billAddress.setCountry(address.getCountry());
		            	billAddress.setEmail(address.getEmail());
		            	billAddress.setFirstName(address.getFirstName());
		            	billAddress.setLastName(address.getLastName());
		            	billAddress.setMiddleName(address.getMiddleInitials());
		            	billAddress.setPhoneNumber(address.getPrimaryPhone());
		            	billAddress.setPostalCode(address.getPostalCode());
		            	billAddress.setState(address.getRegion());
		            	
		            	logDebug("Inside - International Order PO File Unmarshaller " +
		            			"Method Name [getIntlBillingAddress] : BillingAddress : "+billAddress);
		            	break;
		            }
		            
	            }
	           
		}
	        logDebug("Exiting - International Order PO File Unmarshaller Method Name [getIntlBillingAddress]");
	       return billAddress;
	}*/
		
		
		/**
		 * Method to get International shipping address details
		 * 
		 * @param orderFeed
		 * @return
		 */
		/*private BBBIntlAddressVO getIntlShippingAddress(final OrderFeed orderFeed)
		{
			logDebug("Entering - International Order PO File Unmarshaller" +
					" Method Name [getIntlShippingAddress]");
			
				
			 BBBIntlAddressVO shipAddress=null;
	        if (null != orderFeed) {
	        	
	        	shipAddress = new  BBBIntlAddressVO();
	            final DomesticProfile domesticProfile = orderFeed.getOrder().get(0).getDomesticProfile();
	            final  List<Address> add=domesticProfile.getAddress();
	           
	            for(final Address address:add){
		            	            
		            if (SHIPPING_TYPE.equals(address.getType()))
		            {
		            	shipAddress.setType(address.getType());
		            	shipAddress.setAddress1(address.getAddressLine1());
		            	shipAddress.setAddress2(address.getAddressLine2());
		            	shipAddress.setAddress3(address.getAddressLine3());
		            	shipAddress.setCity(address.getCity());
		            	shipAddress.setCountry(address.getCountry());
		            	shipAddress.setEmail(address.getEmail());
		            	shipAddress.setFirstName(address.getFirstName());
		            	shipAddress.setLastName(address.getLastName());
		            	shipAddress.setMiddleName(address.getMiddleInitials());
		            	shipAddress.setPhoneNumber(address.getPrimaryPhone());
		            	shipAddress.setPostalCode(address.getPostalCode());
		            	shipAddress.setState(address.getRegion());
		            	logDebug("Inside - International Order PO File Unmarshaller " +
		            			"Method Name [getIntlShippingAddress] : ShippingAddress : "+shipAddress);
		            	break;
		            }
	            }
	            
	            
	         }
	        logDebug("Exiting - International Order PO File Unmarshaller " +
	        		"Method Name [getIntlShippingAddress]");
	       return shipAddress;
	}*/
		
		
		/**
		 * Method to get International credit card details
		 * 
		 * @param orderFeed
		 * @return
		 */
		/*private BBBIntlCreditCardVO getIntlCreditCardDetails(final OrderFeed orderFeed)
		{
			logDebug("Entering - International Order PO File Unmarshaller " +
					"Method Name [getIntlCreditCardDetails]");
			
			
			final BBBIntlCreditCardVO creditCardDetails = new  BBBIntlCreditCardVO();
	         if (null != orderFeed) {
	        	
	            final CreditCard creditCard=(CreditCard) orderFeed.getOrder().get(0).getCreditCard();
	            if(null != creditCard.getType())
	            {           	
	            	creditCardDetails.setCreditCardNumber(creditCard.getNumber());
	            	creditCardDetails.setCreditCardType(creditCard.getType());
	            	creditCardDetails.setExpirationMonth(creditCard.getExpiry().getMonth());
	            	creditCardDetails.setExpirationYear(creditCard.getExpiry().getYear());
	            	creditCardDetails.setNameOnCard(creditCard.getNameOnCard());
	            	creditCardDetails.setCvn(creditCard.getCVN());
	            	
	            	logDebug("Entering - International Order PO File Unmarshaller " +
	            			"Method Name [getIntlCreditCardDetails] : CreditCardDetails : "+creditCardDetails);
	             }
	          
		}
	         logDebug("Exiting - International Order PO File Unmarshaller " +
	         		"Method Name [getIntlCreditCardDetails]"); 
	       return creditCardDetails;
	}*/

}
	                

	                