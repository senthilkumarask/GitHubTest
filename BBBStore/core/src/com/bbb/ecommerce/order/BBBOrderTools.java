package com.bbb.ecommerce.order;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.QueryOptions;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;
import atg.repository.rql.RqlStatement;
//import atg.servlet.DynamoHttpServletRequest;
//import atg.servlet.ServletUtil;

import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.BBBOrderPropertyManager;
import com.bbb.constants.BBBCoreConstants;
//import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
//import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBDetailedItemPriceInfo;
import com.bbb.utils.BBBUtility;
//import com.bbb.utils.BBBUtility;

/**
 * 
 * @author manohar
 * @story UC_checkout_billing
 * @created 12/2/2011
 */
public class BBBOrderTools extends OrderTools{
	
	private BBBOrderPropertyManager mPropertyManager = null;
	/**
	 * Gift wrap commerce item type.
	 */
	private String mGiftWrapCommerceItemType;
	
	private String dcPrefix;
	
	/**
	 * Eco fee commerce item type.
	 */
	private String mEcoFeeCommerceItemType;
    private OrderManager mOrderManager;
    
    /**
     * LTL assembly fee commerce item type
     */
    private String ltlAssemblyFeeCommerceItemType;
    
    /**
     * LTL delivery charge commerce item type
     */
    private String ltlDeliveryChargeCommerceItemType;
	
   /**
    * RQL to fetch tax info from reository
    */
    private String rqlQueryTaxInfo;
    
	/**
	 * Archive Order Repository
	 */
	private Repository archiveOrderRepository;
    
	/**
	 * @return the propertyManager
	 */
	public BBBOrderPropertyManager getPropertyManager() {
		return this.mPropertyManager;
	}

	/**
	 * @param pPropertyManager the propertyManager to set
	 */
	public void setPropertyManager(BBBOrderPropertyManager pPropertyManager) {
		this.mPropertyManager = pPropertyManager;
	}	
	
	private List<String> sites;

	public List<String> getSites() {
		return sites;
	}

	public void setSites(List<String> sites) {
		this.sites = sites;
	}

	public String getRqlQueryTaxInfo() {
		return rqlQueryTaxInfo;
	}

	public void setRqlQueryTaxInfo(String rqlQueryTaxInfo) {
		this.rqlQueryTaxInfo = rqlQueryTaxInfo;
	}

	/**
	 * Only pull those orders which are submitted, but haven't been sent to TIBCO
	 * 
	 * @param pSubstatus
	 * @param mSubmittedDate
	 * @param startIndex
	 * @param endIndex
	 * @return
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getUnsubmittedOrders(String pSubstatus, Timestamp mSubmittedDate, int pStartIndex, int pEndIndex) throws BBBSystemException{
		RepositoryItem[] orders = null;
		
		Repository repository = getOrderRepository();
		if (isLoggingDebug()) {
			logDebug("START : Retriving Unsubmitted orders");
		}
		try {
			/* Query BBB Order */
			RepositoryItemDescriptor orderItemDescriptor = repository.getItemDescriptor(getPropertyManager().getOrderName());
			RepositoryView orderView = orderItemDescriptor.getRepositoryView();
			QueryBuilder orderQueryBuilder = orderView.getQueryBuilder();
			
			/*Create query for Substatus attribute*/
			QueryExpression substatusProperty = orderQueryBuilder.createPropertyQueryExpression(getPropertyManager().getSubstatusName());
			QueryExpression substatusValue = orderQueryBuilder.createConstantQueryExpression(pSubstatus);
			Query substatusQuery = orderQueryBuilder.createComparisonQuery(substatusProperty, substatusValue, QueryBuilder.EQUALS);

			/*Create query for Submitted date attribute*/
			QueryExpression submittedDateProperty = orderQueryBuilder.createPropertyQueryExpression(getPropertyManager().getSubmittedDateName());
			QueryExpression submittedDateValue = orderQueryBuilder.createConstantQueryExpression(mSubmittedDate);
			Query submittedDateQuery = orderQueryBuilder.createComparisonQuery(submittedDateProperty, submittedDateValue, QueryBuilder.LESS_THAN_OR_EQUALS);
			
			/*Create query for querying the DC prefix*/
			QueryExpression dataCenterProperty = orderQueryBuilder.createPropertyQueryExpression(getPropertyManager().getCreatedByOrderIdName());
			QueryExpression dataCenterValue = orderQueryBuilder.createConstantQueryExpression(getDcPrefix());
			Query dataCenterQuery = orderQueryBuilder.createPatternMatchQuery(dataCenterProperty, dataCenterValue, QueryBuilder.STARTS_WITH);
			
			/* Creating a query for matching based on site. TBS or Online Applications. */
			Query[] siteQueries = null;
			siteQueries = new Query[getSites().size()];
			int index = 0;
			for(String site : getSites()){
				QueryExpression siteQueryExpression = orderQueryBuilder.createPropertyQueryExpression(BBBCoreConstants.SITE_ID);
				QueryExpression siteValueQuery = orderQueryBuilder.createConstantQueryExpression(site);
				siteQueries[index] = orderQueryBuilder.createComparisonQuery(siteQueryExpression, siteValueQuery, QueryBuilder.EQUALS);
				index++;
			}
			 
			Query siteQuery = orderQueryBuilder.createOrQuery(siteQueries);
			
			/*Create AND query using Substatus, Submitted date & DC prefix attributes & Site Checks.*/
			Query[] pieces = {substatusQuery, submittedDateQuery,dataCenterQuery, siteQuery};
			Query unsubmittedOrdersQuery = orderQueryBuilder.createAndQuery(pieces);
			
			if(isLoggingDebug()){
				logDebug(" unsubmittedOrdersQuery: " + unsubmittedOrdersQuery);
			}
			SortDirectives sortDirectives = new SortDirectives();
			sortDirectives.addDirective(new SortDirective(BBBCoreConstants.SUBMITTED_DATE_ORDER_PROPERTY_NAME, SortDirective.DIR_ASCENDING));
			/* Using query, only pull those orders which are submitted, but haven't been sent to TIBCO*/
			orders = orderView.executeQuery(unsubmittedOrdersQuery, new QueryOptions(pStartIndex, pEndIndex, sortDirectives,null));
			
			if(isLoggingDebug()){
				if(orders !=null){
				logDebug("No of UNSUBMITTED Orders:: " + orders.length);
				}else{
				logDebug("No of UNSUBMITTED Orders are ZERO " );
				}
			}
			
		} catch (RepositoryException e) {
			String msg = "Error while retriving unsubmitted order";
			throw new BBBSystemException(BBBCoreErrorConstants.CART_ERROR_1026,msg, e);
		}

		if (isLoggingDebug()) {
			logDebug("END : Retriving Unsubmitted orders");
		}
		
		return orders;
	}
	
	/**
	 * Update the order substatus with the provided value
	 * 
	 * @param pOrder
	 * @param pSubstatus
	 * @return
	 * @throws BBBSystemException
	 */
	public boolean updateOrderSubstatus(Order pOrder, String pSubstatus) throws BBBSystemException {
		
		// Re-factored the code for JIRA ticket BBBSL-2751 & BBBSL-2636.
		// Added update order call instead of updating order repository item.
		BBBOrder bbbOrder = (BBBOrder) pOrder;
		boolean success = false;
		if (isLoggingDebug()) {
			logDebug("START : Updating Order [" + bbbOrder.getId() + "] to substatus [" + pSubstatus + "]");
		}
		try {
			bbbOrder.setSubStatus(pSubstatus);
			this.getOrderManager().updateOrder(bbbOrder);
			success = true;
		} catch (Exception e) {
			String msg = "Exception occured while updating the Order substatus for ["+bbbOrder.getId()+"]";
			throw new BBBSystemException(BBBCoreErrorConstants.CART_ERROR_1026,msg, e);
		}

		if (isLoggingDebug()) {
			logDebug("END : Updating Order [" + bbbOrder.getId() + "] to substatus [" + pSubstatus + "]");
		}

		return success;
	}
		
	
	/**
	 * Update the order state with the provided value
	 * 
	 * @param pOrder
	 * @param pSubstatus
	 * @return
	 * @throws BBBSystemException
	 */
	public boolean updateOrderState(Order pOrder, int state) throws BBBSystemException {
		
		BBBOrder bbbOrder = (BBBOrder) pOrder;
		boolean success = false;
			if(isLoggingDebug()) logDebug("START : Updating Order [" + bbbOrder.getId() + "] to state [" + state + "]");
		try {
			bbbOrder.setState(state);
			this.getOrderManager().updateOrder(bbbOrder);
			success = true;
		} catch (CommerceException e) {
			String msg = "Commerce Exception while updating the Order substatus for ["+bbbOrder.getId()+"]";
			throw new BBBSystemException(BBBCoreErrorConstants.CART_ERROR_1026,msg, e);
		}

		if(isLoggingDebug()) logDebug("END : Updating Order [" + bbbOrder.getId() + "] to state [" + state + "]");
		return success;
	}
		
	public BBBRepositoryContactInfo createBillingAddress()
			throws RepositoryException {

		MutableRepository orderRepo = (MutableRepository) getOrderRepository();
		MutableRepositoryItem addrItem = orderRepo.createItem("bbbAddress");
		BBBRepositoryContactInfo repoConInfo = new BBBRepositoryContactInfo(addrItem);
		return repoConInfo;
	}

  
    public Order getOrderFromOnlineOrBopusOrderNumber(String onlineOrBopusOrderNumber) throws RepositoryException, CommerceException {
    	if(StringUtils.isEmpty(onlineOrBopusOrderNumber)) {
    		if (isLoggingDebug()) {
    			logDebug("onlineOrBopusOrderNumber is empty");
    		}
    	    return null;
    	}

    	if (isLoggingDebug()) {
    		logDebug("Fetching Order using Online or Bopus Order Number : "+onlineOrBopusOrderNumber);
    	}
        // Start : R 2.2 Change to get Order Id from Repository, since ATG OrderId and DSOrderID are not exact matches now    	
        Order order = null;
        String atgOrderId = "";
        
        // Get Order Number From RQL Only
	   if (isLoggingDebug()) {
		   logDebug("Start Fetch Order Details From DS OrderID");
	   }
      
         atgOrderId = getOrderIdFromOnlineOrBopusOrderNumber(onlineOrBopusOrderNumber, getOrderRepository());
         
         if(!BBBUtility.isEmpty(atgOrderId)){
        	 vlogDebug("Found order in order repository");
        	 order = getOrderManager().loadOrder(atgOrderId);
         } else {
        	 vlogDebug("Searching order in archive repository");
        	 atgOrderId = getOrderIdFromOnlineOrBopusOrderNumber(onlineOrBopusOrderNumber, getArchiveOrderRepository());
        	 
        	 if(!BBBUtility.isEmpty(atgOrderId)) {
        		    // Here we can call loadArchive to load order from archive repository
        		 	order = getOrderManager().loadOrder(atgOrderId);
        	 }
         }

         if (isLoggingDebug()) {
			if (order == null) {
				logDebug("No Order Return from using RQL");
			} else {
				logDebug("Found order using RQL: " + order);
			}
		}
       
        return order;
        // End : R 2.2 Change to get Order Id from Repository, since ATG OrderId and DSOrderID are not exact matches now
}

    /**
     * Fetch order Id from order repository , Fetch from archive repository if not found in order repository
     * @param onlineOrBopusOrderNumber
     * @return atgOrderId
     * @throws RepositoryException
     */
    public String getOrderIdFromOnlineOrBopusOrderNumber(String onlineOrBopusOrderNumber, Repository OrderRepository) throws RepositoryException {
    	vlogDebug("Enter BBBOrderTools.getOrderId(String)  onlineOrBopusOrderNumber {0} ", onlineOrBopusOrderNumber);
    	String atgOrderId = "";
    	
       RqlStatement statement = null;
    	 
       if (onlineOrBopusOrderNumber.startsWith("OLP")) {
             statement = RqlStatement
                           .parseRqlStatement("bopusOrderNumber=?0");
       } else {
             statement = RqlStatement
                           .parseRqlStatement("onlineOrderNumber=?0");
       }
         
         RepositoryView view = OrderRepository.getView("order");
       Object[] params = new Object[1];
       params[0] = onlineOrBopusOrderNumber;

       RepositoryItem[] items = fecthOrder(statement, view, params);
       if (items != null && items.length > 0) {
             final RepositoryItem item = items[0];
             atgOrderId = item.getRepositoryId();

    			}
        vlogDebug("Exit BBBOrderTools.getOrderId(String)  atgOrderId {0} ", atgOrderId);
    	return atgOrderId;
    		}

	protected RepositoryItem[] fecthOrder(RqlStatement statement, final RepositoryView view, Object[] params)
			throws RepositoryException {
		return statement.executeQuery(view, params);
	}

    public final OrderManager getOrderManager() {
        return this.mOrderManager;
    }

    public final void setOrderManager(final OrderManager pOrderManager) {
        this.mOrderManager = pOrderManager;
    }

	
	  /**
	   * @return gift wrap commerce item type.
	   */
	  public String getGiftWrapCommerceItemType() {
	    return this.mGiftWrapCommerceItemType;
	  }

	  /**
	   * @param pGiftWrapCommerceItemType - gift wrap
	   * commerce item type.
	   */
	  public void setGiftWrapCommerceItemType(final String pGiftWrapCommerceItemType) {
	    this.mGiftWrapCommerceItemType = pGiftWrapCommerceItemType;
	  }
	  
	  /**
	   * @return eco fee commerce item type.
	   */
	  public String getEcoFeeCommerceItemType() {
	    return this.mEcoFeeCommerceItemType;
	  }

	  /**
	   * @param pEcoFeeCommerceItemType - eco fee commerce item type.
	   * 
	   */
	  public void setEcoFeeCommerceItemType(final String pEcoFeeCommerceItemType) {
		  this.mEcoFeeCommerceItemType = pEcoFeeCommerceItemType;
	  }
	  
	  
	 /**
	  * Invalidate order cache
	  */
	  public void invalidateOrderCache(Order order){
		  
		  if(order instanceof OrderImpl ){
			  
			  String orderId = order.getId();
			  try {
				  final MutableRepositoryItem mutItem =((OrderImpl)order).getRepositoryItem();
				  final ItemDescriptorImpl itemDescriptor = (ItemDescriptorImpl) getOrderRepository().getItemDescriptor(
		    			getOrderItemDescriptorName());
		    		
				  if(mutItem !=null && itemDescriptor !=null){
					    if(isLoggingDebug()) logDebug("MSG=[Invalidating item cache for order =" +mutItem);
		    			invalidateCache(itemDescriptor, mutItem);
		    			if(isLoggingDebug()) logDebug("MSG=[Cache invalidation done for order =" +mutItem);
		    			
		    			@SuppressWarnings("unchecked")
						final List<RepositoryItem> commerceItems = (List<RepositoryItem>)mutItem.getPropertyValue("commerceItems");
		    			
		    			if(commerceItems !=null ){
			    			for (final RepositoryItem commerceItem : commerceItems) {
			    				 final MutableRepositoryItem mcommItem = (MutableRepositoryItem)commerceItem;
			   				  	 final ItemDescriptorImpl commItemDescriptor = (ItemDescriptorImpl) getOrderRepository().getItemDescriptor("commerceItem");
			    			
			   				  	 if( mcommItem!=null && commItemDescriptor !=null ){
			   				  	     if(isLoggingDebug()) logDebug("MSG=[Invalidating item cache for commcerItem =" +mcommItem.getRepositoryId());
				   				  	 invalidateCache(commItemDescriptor, mcommItem);
				   				     if(isLoggingDebug()) logDebug("MSG=[Cache invalidation done for commcerItem =" +mcommItem.getRepositoryId());
			   				  	 }
							}
		    			} else{
		    				logInfo("MSG=[No commerceItems for order =]" +orderId);	
		    			}
				  }
				  
				  
			  }
			  catch (RepositoryException e) {
				  //if (isLoggingWarning()){
					  logError("Unable to invalidate item descriptor " + getOrderItemDescriptorName() + ":" + orderId);
				 // }
			  }
		  }
	  }
	  
	 /**
	 * This method invalidates the item from the cache
	 */
	  public void invalidateCache(final ItemDescriptorImpl desc, final MutableRepositoryItem mutItem) {
		  try {
			  desc.removeItemFromCache(mutItem.getRepositoryId());
		  }
		  catch (RepositoryException e) {
			  //if (isLoggingWarning()){
				  logError("Unable to invalidate item descriptor " + desc.getItemDescriptorName() + ":" + mutItem.getRepositoryId());
			  //}
		  }
	  }

	  /**
		 * @return the dcPrefix
		 */
		public String getDcPrefix() {
			return this.dcPrefix;
		}

		/**
		 * @param dcPrefix the dcPrefix to set
		 */
		public void setDcPrefix(final String dcPrefix) {
			this.dcPrefix = dcPrefix;
		}
		
		/**
		 * @return the ltlAssemblyFeeCommerceItemType
		 */
		public String getLtlAssemblyFeeCommerceItemType() {
			return ltlAssemblyFeeCommerceItemType;
		}
		
		/**
		 * @param ltlAssemblyFeeCommerceItemType the ltlAssemblyFeeCommerceItemType to set
		 */
		public void setLtlAssemblyFeeCommerceItemType(
				String ltlAssemblyFeeCommerceItemType) {
			this.ltlAssemblyFeeCommerceItemType = ltlAssemblyFeeCommerceItemType;
		}

		/**
		 * @return the ltlDeliveryChargeCommerceItemType
		 */
		public String getLtlDeliveryChargeCommerceItemType() {
			return ltlDeliveryChargeCommerceItemType;
		}

		/**
		 * @param ltlDeliveryChargeCommerceItemType the ltlDeliveryChargeCommerceItemType to set
		 */
		public void setLtlDeliveryChargeCommerceItemType(
				String ltlDeliveryChargeCommerceItemType) {
			this.ltlDeliveryChargeCommerceItemType = ltlDeliveryChargeCommerceItemType;
		}
		
		/**
		 * Adding a new International Order
		 * @param orderId
		 * 		Order Id
		 * @param internationalOrderId
		 * 		International Order Id
		 * @param internationalState
		 * 		International State
		 * @param currencyCode
		 * 		Currency code
		 * @param countryCode
		 * 		Country Code
		 * @throws BBBSystemException
		 * 		the bBB system exception
		 * @throws RepositoryException 
		*/
		
		public boolean addInternationalOrder(Order order, String internationalOrderId,String internationalState,String currencyCode,String countryCode) throws BBBSystemException, RepositoryException{
			if (isLoggingDebug()) {
			logDebug("Entering class: BBBOrderTools,  "
					+"method : addInternationalOrder : orderId : "+ order.getId()
					+ "International Order Id : "+internationalOrderId);
			}
            
			BBBOrder bbbOrder = (BBBOrder) order;
			Date orderSubmittedDate = new Date();
			boolean success = false;
			try {
				bbbOrder.setInternationalOrderId(internationalOrderId);
				bbbOrder.setInternationalState(internationalState);
				bbbOrder.setInternationalCountryCode(countryCode);
				bbbOrder.setInternationalCurrencyCode(currencyCode);
				bbbOrder.setInternationalOrderDate(orderSubmittedDate);
				this.getOrderManager().updateOrder(bbbOrder);
				success = true;
			} catch (CommerceException e) {
				String msg = "Commerce Exception while updating the Order substatus for ["+bbbOrder.getId()+"]";
				throw new BBBSystemException(BBBCoreErrorConstants.CART_ERROR_1026,msg, e);
			}
			if (isLoggingDebug()) {
            logDebug("Exit from  class: BBBOrderTools, method : addInternationalOrder ");
			}
            return success;
     }
		/**
		 * Updating International Order state
		 * @param orderId
		 * 		Order Id
		 * @param internationalOrderId
		 * 		International Order Id
		 * @param internationalState
		 * 		International State
		 * @param currencyCode
		 * 		Currency code
		 * @param countryCode
		 * 		Country Code
		 * @throws BBBSystemException
		 * 		the bBB system exception
		 * @throws RepositoryException 
		*/
		public boolean updateInternationalOrderState(Order order, String internationalState) throws BBBSystemException, RepositoryException{
			if (isLoggingDebug()) {
			logDebug("Entering class: BBBOrderTools,  "
					+"method : updateInternationalOrderState : orderId : "+ order.getId()
					+ "internationalState : " +internationalState);
			}
            
			BBBOrder bbbOrder = (BBBOrder) order;
			boolean success = false;
			try {
				bbbOrder.setInternationalState(internationalState);
				this.getOrderManager().updateOrder(bbbOrder);
				success = true;
			} catch (CommerceException e) {
				String msg = "Commerce Exception while updating the Order substatus for ["+bbbOrder.getId()+"]";
				throw new BBBSystemException(BBBCoreErrorConstants.CART_ERROR_1026,msg, e);
			}
			if (isLoggingDebug()) {
            logDebug("Exit from  class: BBBOrderTools, method : addInternationalOrder ");
			}
            return success;
		}
	  
		/**
		 * This method is used to fetch tax info from db.
		 * 
		 * @param unitPriceBean
		 * @param itemType
		 * @return RepositoryItem
		 */
		public RepositoryItem fetchTaxInfoFromDPI(final BBBDetailedItemPriceInfo unitPriceBean, final String itemType) {
			RepositoryItem dsLineItemTaxInfo = null;
			List<RepositoryItem> dsLineItemTaxcInfos = unitPriceBean.getDsLineItemTaxInfos();
			for (RepositoryItem dpiTaxInfo : dsLineItemTaxcInfos) {
				if (itemType.equalsIgnoreCase((String) dpiTaxInfo.getPropertyValue(BBBCoreConstants.DPI_DSLINEITEM_ITEM_TYPE_PROPERTY))) {
					dsLineItemTaxInfo = dpiTaxInfo;
					break;
				}
			}
			return dsLineItemTaxInfo;
		}
		
		/**
		 * This method is used to check if the address provided to
		 * this method is empty i.e. First Name, Last Name, Address related properties are empty.
		 * @param pAddress
		 * @return boolean
		 */
		
		/**
		 * Overriding this method to ignore the value present in state
		 * As it gives the value 'INITIAL' which is not actual state address but Shipping Group's state
		 * Changed for RMT-40546
		 */
		
		@ Override
		public boolean isNullAddress(Address pAddress) {
			
			if (isLoggingDebug()) {
				logDebug("Entering class: [BBBOrderTools] method : [isNullAddress]");
			}
			
			if (pAddress == null) {
				if (isLoggingDebug()) {
					logDebug("pAddress object is null, returning true");
				}
				return true;
			}
			if (pAddress.getPrefix() != null) {
				if (isLoggingDebug()) {
					logDebug("prefix is not null, returning false");
				}
				return false;
			}
			if (pAddress.getFirstName() != null) {
				if (isLoggingDebug()) {
					logDebug("First Name is not null, returning false");
				}
				return false;
			}
			if (pAddress.getMiddleName() != null) {
				if (isLoggingDebug()) {
					logDebug("Middle Name is not null, returning false");
				}
				return false;
			}
			if (pAddress.getLastName() != null) {
				if (isLoggingDebug()) {
					logDebug("Last Name is not null, returning false");
				}
				return false;
			}
			if (pAddress.getSuffix() != null) {
				if (isLoggingDebug()) {
					logDebug("Suffix is not null, returning false");
				}
				return false;
			}
			if (pAddress.getAddress1() != null) {
				if (isLoggingDebug()) {
					logDebug("Address1 is not null, returning false");
				}
				return false;
			}
			if (pAddress.getAddress2() != null) {
				if (isLoggingDebug()) {
					logDebug("Address2 is not null, returning false");
				}
				return false;
			}
			if (pAddress.getAddress3() != null) {
				if (isLoggingDebug()) {
					logDebug("Address3 is not null, returning false");
				}
				return false;
			}
			if (pAddress.getCity() != null) {
				if (isLoggingDebug()) {
					logDebug("City is not null, returning false");
				}
				return false;
			}
				
			if (pAddress.getPostalCode() != null) {
				if (isLoggingDebug()) {
					logDebug("Postal Code is not null, returning false");
				}
				return false;
			}
			if (pAddress.getCountry() != null) {
				if (isLoggingDebug()) {
					logDebug("Country is not null, returning false");
				}
				return false;
			}
			if (isLoggingDebug()) {
				logDebug("Owner Id :: " + pAddress.getOwnerId());
			}
			return (pAddress.getOwnerId() == null);
		}
		
		/**
		 * Returns reference to archiveOrderRepository
		 * @return archiveOrderRepository
		 */
		public Repository getArchiveOrderRepository() {
			return archiveOrderRepository;
}

		/**
		 * @param archiveOrderRepository
		 */
		public void setArchiveOrderRepository(Repository archiveOrderRepository) {
			this.archiveOrderRepository = archiveOrderRepository;
		}
}
