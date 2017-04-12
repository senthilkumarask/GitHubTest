package com.bbb.internationalshipping.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.internationalshipping.utils.InternationalOrderXmlRepoTools;
import com.bbb.internationalshipping.vo.orderconfirmation.BBBInternationalShippingOrderConfReq;
import com.bbb.internationalshipping.vo.orderconfirmation.BBBInternationalShippingOrderConfResponse;
import com.bbb.utils.BBBUtility;

/**
 * This Manager class is used for making 
 * webservice call of Order Confirmation API, 
 * using the orderConfirmation request and response pair.
 * When the orderConfirmation response is successful, 
 * Borderfree sends an "Order Confirmation" email to the international customer.
 * 
 * @version 1.0
 */
public class InternationalOrderConfirmationManager extends BBBGenericService {
	
	private BBBCatalogTools catalogTools;
	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	/**
	 * @param catalogTools the catalogTools to set
	 */
	public final void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	/**
	 * Parameter for Service Name
	 */
	private String serviceName;
	/**
	 * @return the serviceName
	 */
	public final String getServiceName() {
		return serviceName;
	}
	/**
	 * @param serviceName the serviceName to set
	 */
	public final void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}
	
	private InternationalOrderXmlRepoTools intlRepoTools;
	
	public InternationalOrderXmlRepoTools getIntlRepoTools() {
		return intlRepoTools;
	}
	public void setIntlRepoTools(final InternationalOrderXmlRepoTools intlRepoTools) {
		this.intlRepoTools = intlRepoTools;
	}
	
	private int maxOrderRetry;
	
	/**
	 * @return the maxOrderRetry
	 */
	public final int getMaxOrderRetry() {
		return maxOrderRetry;
	}
	/**
	 * @param maxOrderRetry the maxOrderRetry to set
	 */
	public final void setMaxOrderRetry(int maxOrderRetry) {
		this.maxOrderRetry = maxOrderRetry;
	}
	
	/**
	 * This method will create the BBBInternationalShippingOrderConfReq with the 
	 * merchantOrderId and orderId received as input parameters.
	 * Will make the web service call and 
	 * return the BBBInternationalShippingOrderConfResponse
	 * @param merchantOrderId
	 * @param orderId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public final BBBInternationalShippingOrderConfResponse orderConfirmation(final String merchantOrderId, final String e4xOrderId)   {

		logDebug("Entering class: InternationalOrderConfirmationManager,  "
				+ "method : orderConfirmation : merchantOrderId :" + merchantOrderId + " : orderId : " + e4xOrderId);

		if(BBBUtility.isEmpty(merchantOrderId) || BBBUtility.isEmpty(e4xOrderId)) {
			logDebug("Inside class: InternationalOrderConfirmationManager,  "
					+ "method : invalid parameters");
			return null;
		}

		final BBBInternationalShippingOrderConfReq orderConfReq = getInternationalShippingOrderConfReq(merchantOrderId , e4xOrderId );
		BBBInternationalShippingOrderConfResponse orderConfResponse = new BBBInternationalShippingOrderConfResponse();
		try {
			orderConfResponse = (BBBInternationalShippingOrderConfResponse) ServiceHandlerUtil.invoke(orderConfReq);
			this.intlRepoTools.setOrderConfirmationSucessFlag(e4xOrderId);
		} catch (BBBBusinessException e) {
			logError("Exception while calling Order Confirmationa API", e);
			orderConfResponse.setWebServiceError(true);
			this.intlRepoTools.updateRetriesOrderConfirmation(e4xOrderId, e.getMessage(), merchantOrderId);
		} catch (BBBSystemException e) {
			logError("Exception while calling Order Confirmationa API", e);
			orderConfResponse.setWebServiceError(true);
			this.intlRepoTools.updateRetriesOrderConfirmation(e4xOrderId, e.getMessage(), merchantOrderId);
		}catch (Exception e) {
			logError("Exception while calling Order Confirmationa API", e);
			orderConfResponse.setWebServiceError(true);
			this.intlRepoTools.updateRetriesOrderConfirmation(e4xOrderId, e.getMessage(), merchantOrderId);
		}
		logDebug("Exiting class: InternationalOrderConfirmationManager,  "
				+ "method : orderConfirmation");
		return orderConfResponse;

	}


	/**
	 * Method created to retry the failed Order Confirmation.
	 * This method will be called from IntlRetryScheduler
	 */
	public final void retryOrderConfirmation(){
		
		logDebug("Entering class: InternationalOrderConfirmationManager,  "
				+ "method : retryOrderConfirmation ");
		Map<String, String> e4xOrderMap = null;
		int maxRetryOrderConf = this.getMaxOrderRetry();
		try {
			final List<String> maxRetry = this.catalogTools
					.getAllValuesForKey(
							BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
							BBBInternationalShippingConstants.CONFIG_KEY_ORDER_CONF_RETRY_COUNT);
			if(maxRetry != null && !maxRetry.isEmpty()){
				maxRetryOrderConf = Integer.valueOf(maxRetry.get(0));
			}
			e4xOrderMap = this.getIntlRepoTools().getOrdersForConfirmationRetry(maxRetryOrderConf);
		} catch (BBBSystemException e) {
			logError("Exception while getting the Failed E4XOrder Id's", e);
		} catch (BBBBusinessException e) {
			logError("Exception while getting the Config Key", e);
		}
		if(e4xOrderMap!=null && !e4xOrderMap.isEmpty()){
			final Set<String> e4xOrderIdSet=e4xOrderMap.keySet();
			for(final String e4xOrderId: e4xOrderIdSet){
				this.orderConfirmation(e4xOrderMap.get(e4xOrderId), e4xOrderId);
			}
		}
		logDebug("Exiting class: InternationalOrderConfirmationManager,  "
				+ "method : retryOrderConfirmation ");

	}
	
	private BBBInternationalShippingOrderConfReq getInternationalShippingOrderConfReq(
			final String merchantOrderId, final String e4xOrderId) {

		final BBBInternationalShippingOrderConfReq orderConfReq = new BBBInternationalShippingOrderConfReq();
		orderConfReq.setMerchantOrderId(merchantOrderId);
		orderConfReq.setOrderId(e4xOrderId);
		orderConfReq.setServiceName(this.getServiceName());
		return orderConfReq;
	}
}
