package com.bbb.internationalshipping.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.TransactionManager;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBSystemException;

/**
 * The Class InternationalOrderXmlRepoTools.
 */
public class InternationalOrderXmlRepoTools extends BBBGenericService {

	/** The international order repository. */
	private MutableRepository  internationalOrderRepository;
	private TransactionManager transactionManager;
	private String e4xOrderQuey;
	/**
	 * @return the e4xOrderQuey
	 */
	public final String getE4xOrderQuey() {
		return e4xOrderQuey;
	}

	/**
	 * @param e4xOrderQuey the e4xOrderQuey to set
	 */
	public final void setE4xOrderQuey(final String e4xOrderQuey) {
		this.e4xOrderQuey = e4xOrderQuey;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(final TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}


	/**
	 * Gets the international order repository.
	 *
	 * @return the international order repository
	 */
	public MutableRepository getInternationalOrderRepository() {
		return internationalOrderRepository;
	}



	/**
	 * Sets the international order repository.
	 *
	 * @param internationalOrderRepository the new international order repository
	 */
	public void setInternationalOrderRepository(final 
			MutableRepository internationalOrderRepository) {
		this.internationalOrderRepository = internationalOrderRepository;
	}


	/**
	 * Adding a new International Order .
	 *
	 * @param orderId 			Order Id
	 * @param e4XOrderId 			Exchange Order Id
	 * @param orderXML 			Order XML
	 * @throws BBBSystemException 			the bBB system exception
	 */
	public void addOrderXml(final String orderId, final String e4XOrderId, final String orderXML,String bbborder,final String countrycode ,final String currencyCode) throws BBBSystemException{

		logDebug("Entering class: InternationalOrderXmlRepoTools,  "
				+"method : addOrderXml : orderId : "+ orderId
				+ "Exchange Order Id : "+e4XOrderId);
		try {
			if(getInternationalOrderRepository()!=null){

				MutableRepositoryItem orderXmlItem =getInternationalOrderRepository().getItemForUpdate(orderId,  BBBInternationalShippingConstants.INTERNATIONAL_ORDER);

				if(orderXmlItem!=null)
				{  
					orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.INTL_EXCHANGE_OORDER_ID, e4XOrderId);
					orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.INTL_ORDER_XML, orderXML);
					orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE, countrycode);
					orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE, currencyCode);
					orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.ONLINE_ORDER_NUMBER, bbborder);
					getInternationalOrderRepository().updateItem(orderXmlItem);
				}
				else
				{
					orderXmlItem = getInternationalOrderRepository().createItem(orderId, BBBInternationalShippingConstants.INTERNATIONAL_ORDER);
					if(orderXmlItem!=null){
						orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.INTL_EXCHANGE_OORDER_ID, e4XOrderId);
						orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.INTL_ORDER_XML, orderXML);
						orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE, countrycode);
						orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE, currencyCode);
						orderXmlItem.setPropertyValue(BBBInternationalShippingConstants.ONLINE_ORDER_NUMBER, bbborder);

						getInternationalOrderRepository().addItem(orderXmlItem);
					}

				}

				logDebug("Inside the class: InternationalOrderXmlRepoTools,  "
						+"method : addOrderXml : MutableRepositoryItem : "+ orderXmlItem);
			}
			else{
				logError(BBBInternationalShippingConstants.INTERNATIONAL_ORDER_REPOSITORY_NULL);
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBInternationalShippingConstants.INTERNATIONAL_ORDER_REPOSITORY_NULL);
			}
		} catch (RepositoryException e) {
			this.logError("InternationalOrderXmlRepoTools Method Name [addOrderXml]: RepositoryException "
					+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}

		logDebug("Exit from  class: InternationalOrderXmlRepoTools,  "
				+"method : addOrderXml ");

	}


	/**
	 * Removing an existing international order .
	 *
	 * @param orderId 			Order Id
	 * @throws BBBSystemException 			the bBB system exception
	 */
	public void removeOrderXml(final String orderId) throws BBBSystemException{

		logDebug("Entering class: InternationalOrderXmlRepoTools,  "
				+"method : removeOrderXml : orderId : "+ orderId);
		try {
			if(getInternationalOrderRepository()!=null){
				getInternationalOrderRepository().removeItem(orderId, BBBInternationalShippingConstants.INTERNATIONAL_ORDER);
			}
			else{
				logError(BBBInternationalShippingConstants.INTERNATIONAL_ORDER_REPOSITORY_NULL);
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBInternationalShippingConstants.INTERNATIONAL_ORDER_REPOSITORY_NULL);
			}

		} catch (RepositoryException e) {
			this.logError("InternationalOrderXmlRepoTools Method Name [removeOrderXml]: RepositoryException "
					+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}

		logDebug("Exit from  class: InternationalOrderXmlRepoTools,  "
				+"method : removeOrderXml ");
	}

	/**
	 * Getting the Order XML of an Existing international Order.
	 *
	 * @param orderId 			Order Id
	 * @return 			Order XML in the String format
	 * @throws BBBSystemException 			the bBB system exception
	 */
	public String getOrderXml(final String orderId) throws BBBSystemException{

		logDebug("Entering class: InternationalOrderXmlRepoTools,  "
				+"method : getOrderXml : orderId : "+ orderId);

		String orderXml="";
		try {
			if(null != getInternationalOrderRepository()){
				final RepositoryItem orderXmlItem = this.getInternationalOrderRepository().getItem(orderId,
						BBBInternationalShippingConstants.INTERNATIONAL_ORDER);
				if(orderXmlItem!=null){
					orderXml=	(String) orderXmlItem.getPropertyValue(BBBInternationalShippingConstants.INTL_ORDER_XML);
				}

				logDebug("Inside the class: InternationalOrderXmlRepoTools,  "
						+"method : getOrderXml : RepositoryItem : "+ orderXmlItem);
			}
			else{
				logError(BBBInternationalShippingConstants.INTERNATIONAL_ORDER_REPOSITORY_NULL);
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBInternationalShippingConstants.INTERNATIONAL_ORDER_REPOSITORY_NULL);
			}

		} catch (RepositoryException e) {
			this.logError("InternationalOrderXmlRepoTools Method Name [addOrderXml]: RepositoryException "
					+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		logDebug("Exit from  class: InternationalOrderXmlRepoTools,  "
				+"method : getOrderXml ");

		return orderXml;
	}

	/**
	 * This method monitors the file which is scheduled for retry.
	 * 
	 * @param fileName
	 *            the file name
	 * @param pExceptionMessage
	 *            the exception message
	 * @param retryCount
	 *            the retry count
	 * @return boolean
	 */
	public boolean retryProcessing(final String fileName, final String pExceptionMessage,final int retryCount,String siteId) {
		boolean success = false;
		logDebug("InternationalRepositoryManager -> retryProcessing() method starts");

		final MutableRepository mrepo = (MutableRepository) getInternationalOrderRepository();

		try {
			final RepositoryView view = mrepo.getView(BBBInternationalShippingConstants.RETRY_FILE_PROCESS_ITEM_DESCRIPTOR);
			final RqlStatement statement = RqlStatement.parseRqlStatement("fileName =?0", true);
			Object params[] = new Object[1];
			params[0] = fileName;
			final RepositoryItem[] items = statement.executeQuery(view, params);

			if (items == null || items.length == 0) {
				final MutableRepositoryItem mItem = (MutableRepositoryItem) mrepo.createItem(BBBInternationalShippingConstants.RETRY_FILE_PROCESS_ITEM_DESCRIPTOR);
				mItem.setPropertyValue("fileName", fileName);
				mItem.setPropertyValue(BBBInternationalShippingConstants.RETRY_COUNT, 0);
				mItem.setPropertyValue("exceptionCause",pExceptionMessage);
				mItem.setPropertyValue("retrySuccessful", false);
				mItem.setPropertyValue("siteId", siteId);
				mrepo.addItem(mItem);
				logDebug("InternationalRepositoryManager -> retryProcessing() method ends");
				success = true;
			} else {
				final MutableRepositoryItem mItem = (MutableRepositoryItem) items[0];
				int count = (Integer) mItem.getPropertyValue(BBBInternationalShippingConstants.RETRY_COUNT);
				if (count < retryCount) {
					mItem.setPropertyValue(BBBInternationalShippingConstants.RETRY_COUNT, ++count);
					mItem.setPropertyValue("exceptionCause",pExceptionMessage);
					mrepo.updateItem(mItem);
					logDebug("InternationalRepositoryManager -> retryProcessing() method ends");
					success = true;
				}
			}
		} catch (RepositoryException e) {
				logError(
						"Error occur while performing CRUD operation on repositry",
						e);
		}

		logDebug("InternationalRepositoryManager -> retryProcessing() method ends");
		return success;
	}



	public List<String> getPoFilesForRetry(int maxRetryCount,String siteId){

		final List<String> poFiles=new ArrayList<String>();
		try{

			final RepositoryView view = getInternationalOrderRepository().getView(BBBInternationalShippingConstants.RETRY_FILE_PROCESS_ITEM_DESCRIPTOR);
			RepositoryItem[] items = null;

			if(view != null){        
				final QueryBuilder queryBuilder = view.getQueryBuilder();

				final QueryExpression property = queryBuilder.createPropertyQueryExpression(BBBInternationalShippingConstants.RETRY_COUNT);
				final QueryExpression value = queryBuilder.createConstantQueryExpression(maxRetryCount);
				final Query getOrdersQuery = queryBuilder.createComparisonQuery(property, value, QueryBuilder.LESS_THAN); 
				final QueryExpression property1 = queryBuilder.createPropertyQueryExpression(BBBInternationalShippingConstants.RETRY_SUCCESSFUL);
				final QueryExpression value1 = queryBuilder.createConstantQueryExpression(BBBCoreConstants.ZERO);
				final Query retryQuery = queryBuilder.createComparisonQuery(property1, value1, QueryBuilder.EQUALS); 
				final QueryExpression property2 = queryBuilder.createPropertyQueryExpression("siteId");
				final QueryExpression value2 = queryBuilder.createConstantQueryExpression(siteId);
				final Query siteIdQuery = queryBuilder.createComparisonQuery(property2, value2, QueryBuilder.EQUALS); 
				final Query allQuery[]={getOrdersQuery,retryQuery,siteIdQuery};
				final Query andQuery = queryBuilder.createAndQuery(allQuery);
				items = view.executeQuery(andQuery);
			
			}

			if(items != null && items.length>0){
				logDebug(" No of retry Items from retry table"+items.length);
				for(final RepositoryItem item : items){

					final MutableRepositoryItem mItem = (MutableRepositoryItem) item;
					logDebug(" file added for retry  "+mItem.getPropertyValue("fileName"));
					poFiles.add((String) mItem.getPropertyValue("fileName"));

				}
			}
		}catch(RepositoryException re){
			logError(re);
		}
		return poFiles;

	}


	/**
	 * Method created to return the Map with Key as E4XOrderId and value as Merchant Order Id
	 * for the orders whose retry count is less than maxRetryCount variable
	 * and retry successful flag as false.
	 * @param maxRetryCount
	 * @return
	 * @throws BBBSystemException
	 */
	public Map<String,String> getOrdersForConfirmationRetry(final int maxRetryCount) throws BBBSystemException{
		
		logDebug("Entering class: InternationalOrderXmlRepoTools,  "
				+ "method : getOrdersForConfirmationRetry ");

		final Map<String,String> intlOrderIdMerchantIdMap=new  HashMap<String,String>();
		try{

			final RepositoryView view = getInternationalOrderRepository().getView(BBBInternationalShippingConstants.RETRY_ORDER_CONFIRMATION_ITEM_DESCRIPTOR);
			RepositoryItem[] items = null;

			if(view != null){        
				final QueryBuilder queryBuilder = view.getQueryBuilder();

				final QueryExpression property = queryBuilder.createPropertyQueryExpression(BBBInternationalShippingConstants.RETRY_COUNT);
				final QueryExpression value = queryBuilder.createConstantQueryExpression(maxRetryCount);
				final Query getOrdersQuery = queryBuilder.createComparisonQuery(property, value, QueryBuilder.LESS_THAN);
				final QueryExpression property1 = queryBuilder.createPropertyQueryExpression(BBBInternationalShippingConstants.RETRY_SUCCESSFUL);
				final QueryExpression value1 = queryBuilder.createConstantQueryExpression(BBBCoreConstants.ZERO);
				final Query retryQuery = queryBuilder.createComparisonQuery(property1, value1, QueryBuilder.EQUALS); 
				final Query allQuery[]={getOrdersQuery,retryQuery};
				final Query andQuery = queryBuilder.createAndQuery(allQuery);
				items = view.executeQuery(andQuery);
			}
			if(items != null && items.length>0){
				for(final RepositoryItem item : items){

					final MutableRepositoryItem mItem = (MutableRepositoryItem) item;
					intlOrderIdMerchantIdMap.put((String) mItem.getPropertyValue(BBBInternationalShippingConstants.INTL_EXCHANGE_OORDER_ID),
							(String) mItem.getPropertyValue(BBBInternationalShippingConstants.MERCHANT_ORDER_ID));

				}
			}
		} catch(RepositoryException re){
			logError(re);
			throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1015, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1015);
		}
		logDebug("Exiting class: InternationalOrderXmlRepoTools,  "
				+ "method : getOrdersForConfirmationRetry : intlOrderIdMerchantIdMap : " +intlOrderIdMerchantIdMap);
 
		return intlOrderIdMerchantIdMap;

	}

	/**
	 * Method used to set the Success Flag for the Order Confirmation.
	 * @param orderId
	 * @throws BBBSystemException 
	 */
	public void setOrderConfirmationSucessFlag(final String orderId) throws BBBSystemException{
		
		logDebug("Entering class: InternationalOrderXmlRepoTools,  "
				+ "method : setOrderConfirmationSucessFlag : E4XOrderId : " +orderId);
		
		final MutableRepository mrepo = (MutableRepository) getInternationalOrderRepository();
		try{
			final RepositoryView view = mrepo.getView(BBBInternationalShippingConstants.RETRY_ORDER_CONFIRMATION_ITEM_DESCRIPTOR);
			final RqlStatement statement = RqlStatement
					.parseRqlStatement(getE4xOrderQuey(), true);

			Object params[] = new Object[1];
			params[0] = orderId;
			final RepositoryItem[] items = statement.executeQuery(view, params);


			if (items == null || items.length == 0) {
				logDebug("no item in repository to update");
			}else{
				final MutableRepositoryItem mItem = (MutableRepositoryItem) items[0];
				mItem.setPropertyValue(BBBInternationalShippingConstants.RETRY_SUCCESSFUL, true);
			}
		} catch(RepositoryException e){
			logError("Error updating repository with succesful flag true ",e);
			throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1015, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1015);
		}
		
		logDebug("Exiting class: InternationalOrderXmlRepoTools,  "
				+ "method : setOrderConfirmationSucessFlag");

	}
	
	public void setPOSucessFlag(final String fileName){
		final MutableRepository mrepo = (MutableRepository) getInternationalOrderRepository();
		try{
			final RepositoryView view = mrepo.getView(BBBInternationalShippingConstants.RETRY_FILE_PROCESS_ITEM_DESCRIPTOR);
			final RqlStatement statement = RqlStatement
					.parseRqlStatement("fileName =?0", true);

			Object params[] = new Object[1];
			params[0] = fileName;
			final RepositoryItem[] items = statement.executeQuery(view, params);


			if (items == null || items.length == 0) {
				logDebug(" no item in repository to update");
			}else{
				final MutableRepositoryItem mItem = (MutableRepositoryItem) items[0];
				mItem.setPropertyValue(BBBInternationalShippingConstants.RETRY_SUCCESSFUL, true);
			}
		}catch(RepositoryException e){
			logError(" error updating repository with succesful flag true ",e);
		}

	}

	/**
	 * adds order intl_retry_order_confirmation
	 * tables for further retries.
	 * 
	 * @param orderId
	 *            -> for retry pre advice, this is orderID and for
	 *            retryOrderConfirmation it is intl Order id
	 * @param _logTrace
	 * @param _merchantOrderId
	 *            -> merchantId that comes in PO file

	 * 
	 */
	public boolean updateRetriesOrderConfirmation(final String orderId,
			final String logTrace, final String merchantOrderId) {
		
		logDebug("Entering class: InternationalOrderXmlRepoTools,  "
				+ "method : setOrderConfirmationSucessFlag : E4XOrderId : " +orderId + " : LogTrace : " + logTrace + " : merchantOrderId : " + merchantOrderId);
 
		boolean result = Boolean.FALSE.booleanValue();
		final boolean rollback = Boolean.TRUE.booleanValue();
		final TransactionDemarcation td = new TransactionDemarcation();
		final TransactionManager tm = getTransactionManager();

		try {
			td.begin(tm);
			final MutableRepository mrepo = (MutableRepository) getInternationalOrderRepository();
			final RepositoryView view = mrepo.getView(BBBInternationalShippingConstants.RETRY_ORDER_CONFIRMATION_ITEM_DESCRIPTOR);
			final RqlStatement statement = RqlStatement
					.parseRqlStatement(getE4xOrderQuey(), true);

			Object params[] = new Object[1];
			params[0] = orderId;
			final RepositoryItem[] items = statement.executeQuery(view, params);


			if (items == null || items.length == 0) {
				final MutableRepositoryItem mItem = (MutableRepositoryItem) mrepo
						.createItem(BBBInternationalShippingConstants.RETRY_ORDER_CONFIRMATION_ITEM_DESCRIPTOR);
				mItem.setPropertyValue(BBBInternationalShippingConstants.INTL_EXCHANGE_OORDER_ID,
						orderId);
				mItem.setPropertyValue(BBBInternationalShippingConstants.RETRY_COUNT, 0);
				mItem.setPropertyValue(BBBInternationalShippingConstants.LOG_TRACE,logTrace);

				mItem.setPropertyValue(BBBInternationalShippingConstants.MERCHANT_ORDER_ID, merchantOrderId);

				mrepo.addItem(mItem);
			} else {
				final MutableRepositoryItem mItem = (MutableRepositoryItem) items[0];
				int count = (Integer) mItem
						.getPropertyValue(BBBInternationalShippingConstants.RETRY_COUNT);
				mItem.setPropertyValue(BBBInternationalShippingConstants.RETRY_COUNT,
						++count);
				mItem.setPropertyValue(BBBInternationalShippingConstants.LOG_TRACE,
						logTrace);
				mrepo.updateItem(mItem);
			}
			result = true;



		} catch (RepositoryException re) {

			logError(" error updating repository for order confirmation logging ",re);
			try {
				td.end(rollback); 
			} catch (TransactionDemarcationException tde) {
				logError(" TransactionDemarcationException error while rollbacking ",tde);
			}
		} catch (TransactionDemarcationException tde) {
			logError(" TransactionDemarcationException error updating repository for order confirmation logging ",tde);
		} finally {
			try {
				td.end(!rollback); // commit
			} catch (TransactionDemarcationException tde) {
				logError(" TransactionDemarcationException error while rollbacking ",tde);
			}
		}
		
		logDebug("Exiting class: InternationalOrderXmlRepoTools,  "
				+ "method : setOrderConfirmationSucessFlag : resultFlag : " +result);
		
		return result;
	}


	/**
	 * Update retry file repository.
	 * 
	 * @param purchaseOrderFileName
	 *            the purchase order file name
	 */
	public void deleteSuccessfullyRetriedFile(final String purchaseOrderFileName) {
		try {
			final MutableRepository mrepo = (MutableRepository) getInternationalOrderRepository();
			final RepositoryView view = mrepo.getView(BBBInternationalShippingConstants.RETRY_FILE_PROCESS_ITEM_DESCRIPTOR);
			final RqlStatement statement = RqlStatement
					.parseRqlStatement("fileName =?0", true);
			Object params[] = new Object[1];
			params[0] = purchaseOrderFileName;
			final RepositoryItem[] items = statement.executeQuery(view, params);
			if (items != null) {
				final MutableRepositoryItem mItem = (MutableRepositoryItem) items[0];
				mrepo.removeItem(mItem.getRepositoryId(),BBBInternationalShippingConstants.RETRY_FILE_PROCESS_ITEM_DESCRIPTOR);
			}
		} catch (RepositoryException e) {
				logError(
						"Error occur while perfroming CRUD operation on repositry",
						e);
		}
	}
}
