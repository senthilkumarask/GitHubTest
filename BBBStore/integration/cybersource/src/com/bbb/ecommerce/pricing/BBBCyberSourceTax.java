/**
 * 
 */
package com.bbb.ecommerce.pricing;



import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;

import atg.commerce.order.RepositoryContactInfo;
import atg.commerce.payment.DummyTaxProcessor;
import atg.commerce.pricing.PricingTools;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.integrations.cybersourcesoap.CyberSourceException;
import atg.integrations.cybersourcesoap.CyberSourceStatus;
import atg.integrations.cybersourcesoap.CyberSourceTax;
import atg.integrations.cybersourcesoap.MessageConstant;
import atg.multisite.SiteContextManager;
import atg.payment.tax.ShippingDestination;
import atg.payment.tax.TaxRequestInfo;
import atg.payment.tax.TaxRequestInfoImpl;
import atg.payment.tax.TaxStatus;
import atg.payment.tax.TaxableItem;
import atg.payment.tax.TaxableItemImpl;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.event.PersistedInfoLogEvent;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBTagConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;
import com.cybersource.stub.Item;
import com.cybersource.stub.ReplyMessage;
import com.cybersource.stub.RequestMessage;
import com.cybersource.stub.TaxReply;
import com.cybersource.stub.TaxReplyItem;

public class BBBCyberSourceTax extends CyberSourceTax {
	private static final String CLASS_NAME = BBBCyberSourceTax.class.getName();
	private BBBCatalogTools catalogTools;
	private boolean testCyberSourceRetryCount;
	private PricingTools pricingTools;
	private String address1;
	private String city;
	private String postalCode;
	private String state;
	private String country;
	
	
	/**
	 * Implement the TaxProcessor interface to return tax status objects at a
	 * shipping destination level
	 * 
	 * @param ccinfo
	 *            Object that contains info about items being purchased
	 * 
	 * @see TaxRequestInfo
	 * 
	 * @return Array of TaxStatus objects with the result of the calculation
	 * 
	 * @see TaxStatus
	 */
	@SuppressWarnings("rawtypes")
	public TaxStatus[] calculateTaxByShipping(TaxRequestInfo ccinfo) {
		final String methodName = CLASS_NAME + ".calculateTaxByShipping()";
		/* Reset the CYBERSOURCE_TAX_FAILURE Flag */
		Map specialInstructions = ccinfo.getOrder().getSpecialInstructions();
		if (specialInstructions != null) {
			specialInstructions.remove(BBBCheckoutConstants.CYBERSOURCE_TAX_FAILURE);
		}
		TaxStatus[] taxStatuses = null;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CYBERSOURCE_TAX_CALL, methodName);
		try {
			taxStatuses = calculateTaxByShippingInternal(ccinfo);
		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CYBERSOURCE_TAX_CALL, methodName);
		}
		/*Check & handle tax errors (if any)*/
		taxStatuses = handleTaxError(taxStatuses, ccinfo);
	
		return taxStatuses;
	}
	
	/*
	 * This method gets Tax from Shipping repository.
	 * 
	 * @Param ccinfo
	 *            Object that contains info about items being purchased
	 * 
	 * @return Array of TaxStatus objects with the result of the calculation
	 * 
	 */
	private CyberSourceStatus getStateTax(TaxRequestInfo ccinfo,int index){
		if (isLoggingDebug()) {
			logDebug("BBBCyberSourceTax :: getStateTax - Start");
		}
		
			double stateTaxPercent;
			stateTaxPercent = this.getCatalogTools().getStateTax(ccinfo.getShippingDestination(index).getShippingAddress().getState());
	
			//Calculate Tax based on total taxable amount for Particular destination
			//	double statetax =((ccinfo.getShippingDestination(index).getTaxableItemAmount()+ccinfo.getShippingDestination(index).getShippingAmount())*stateTaxPercent)/100;
			
			//Create Dummy Object and Assign tax Value to it.
			TaxReplyItem taxReplyItem = null;		
			int itemsCount = ccinfo.getShippingDestination(index).getTaxableItems().length;
			TaxReplyItem[] taxReplyItems = new TaxReplyItem[itemsCount+1];
			ReplyMessage replyMessage = new ReplyMessage();
			replyMessage.setReasonCode(BigInteger.valueOf(100)); //Reason Code 100 is to denote successful transaction
			TaxableItem taxableitem = null;
			double total = 0.0;
			double taxitem = 0.0;
			//Calculate Tax based on  taxable amount for Particular destination
			for (int i = 0; i < itemsCount; i++) {
				taxableitem = ccinfo.getShippingDestination(index).getTaxableItems()[i];
			    taxitem = this.getPricingTools().round((taxableitem.getAmount()*taxableitem.getQuantity()*stateTaxPercent)/100);
				total = this.getPricingTools().round(total + taxitem);
				taxReplyItem = new TaxReplyItem();
				taxReplyItem.setCityTaxAmount("0");
				taxReplyItem.setCountyTaxAmount("0");
				taxReplyItem.setDistrictTaxAmount("0");
				taxReplyItem.setId(BigInteger.ZERO);
				taxReplyItem.setStateTaxAmount(String.valueOf(taxitem));
				taxReplyItem.setTotalTaxAmount(String.valueOf(taxitem));
				taxReplyItems[i] = taxReplyItem;
				if (isLoggingDebug()) {
					logDebug("Item Information Start");
					StringBuffer logmessage = new StringBuffer();
					logmessage.append("ProductId ::: " + ccinfo.getShippingDestination(index).getTaxableItems()[i].getProductId() + "\n " );
					logmessage.append("Qty ::: " +  ccinfo.getShippingDestination(index).getTaxableItems()[i].getQuantity() + "\n ");
					logmessage.append("Tax Status ::: " +  ccinfo.getShippingDestination(index).getTaxableItems()[i].getTaxStatus() + "\n ");
					logmessage.append("Product Amount ::: " +  ccinfo.getShippingDestination(index).getTaxableItems()[i].getAmount()+ "\n ");
					logDebug(logmessage.toString());
				}
				
			}
			double shippingtax =(ccinfo.getShippingDestination(index).getShippingAmount()*stateTaxPercent)/100;
			//Calculate Tax for shipping amount
			taxReplyItem = new TaxReplyItem();
			taxReplyItem.setCityTaxAmount("0");
			taxReplyItem.setCountyTaxAmount("0");
			taxReplyItem.setDistrictTaxAmount("0");
			taxReplyItem.setId(BigInteger.ZERO);
			taxReplyItem.setStateTaxAmount(String.valueOf(this.getPricingTools().round(shippingtax)));
			taxReplyItem.setTotalTaxAmount(String.valueOf(this.getPricingTools().round(shippingtax)));
			taxReplyItems[itemsCount] = taxReplyItem;
			total = this.getPricingTools().round(total + this.getPricingTools().round(shippingtax));
			TaxReply taxReply = new TaxReply();
			taxReply.setTotalTaxAmount(String.valueOf(total));
			taxReply.setTotalStateTaxAmount(String.valueOf(total));
			taxReply.setTotalCityTaxAmount("0");
			taxReply.setTotalCountyTaxAmount("0");
			taxReply.setTotalDistrictTaxAmount("0");
			taxReply.setItem(taxReplyItems);
			replyMessage.setTaxReply(taxReply);

			CyberSourceStatus cyberSourceStatus = new CyberSourceStatus(
					replyMessage);
			cyberSourceStatus.setTransactionSuccess(true);
		
			if (isLoggingDebug()) {
				logDebug("Shipping Destination "+ ccinfo.getShippingDestination(index).getShippingAddress().getState() +" Taxable Amount ::: " + ccinfo.getShippingDestination(index).getTaxableItemAmount());
				logDebug("Tax Calculated for "+ ccinfo.getShippingDestination(index).getShippingAddress().getState() +" at " + stateTaxPercent + " % is " + cyberSourceStatus.getAmount() );
			}
	
		if (isLoggingDebug()) {
			logDebug("BBBCyberSourceTax :: getStateTax - END");
		}
		return cyberSourceStatus;
	}
	
	  public TaxStatus[] calculateTaxByShippingInternal(TaxRequestInfo paramTaxRequestInfo)
	  {
	    TaxStatus[] arrayOfTaxStatus = null;
	    try
	    {
	      if (getCsCon().getSiteConfiguration().isCalculateTaxForSite(paramTaxRequestInfo.getOrder().getSiteId()))
	      {
	        CyberSourceStatus localCyberSourceStatus = getPreCalculatedTaxUtils().getPrecalculatedTaxes(paramTaxRequestInfo);
	        if (localCyberSourceStatus != null)
	          return new TaxStatus[] { localCyberSourceStatus };
	        arrayOfTaxStatus = performTax(paramTaxRequestInfo);
	      }
	      else
	      {
	        return new DummyTaxProcessor().calculateTaxByShipping(paramTaxRequestInfo);
	      }
	    }
	    catch (CyberSourceException localCyberSourceException)
	    {
	      logError(localCyberSourceException.getMessage());
	      return new TaxStatus[] { getCsCon().processError(localCyberSourceException.getMessage()) };
	    }
	    catch (AxisFault localAxisFault)
	    {
	      logError(localAxisFault.getMessage());
	      return new TaxStatus[] { CyberSourceStatus.getStatusForError(MessageConstant.CYBER_SOURCE_ERROR_OCCURRED) };
	    }
	    catch (Exception localException)
	    {
	      logError(localException);
	      return new TaxStatus[] { getCsCon().processError(MessageConstant.CYBER_SOURCE_TAX_FAILED, null) };
	    }
	    //TaxStatus localTaxStatus = calculateAllTax(arrayOfTaxStatus);
	    //getPreCalculatedTaxUtils().persistPrecalculatedTaxes(paramTaxRequestInfo, localTaxStatus);
	    return arrayOfTaxStatus; //new TaxStatus[] { localTaxStatus };
	  }
	  
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TaxStatus calculate(TaxRequestInfo taxrequestinfo, int paramInt, List list) throws Exception {

		//changes for cybersource zipcode fix to be uncommented//
		//String postalCode = null;
		long startTime = System.currentTimeMillis();
		if (isLoggingDebug()) {
			logDebug("Entered Calculate Method in BBBCyberSourceTax");
		}
		//changes for cybersource zipcode fix to be uncommented//
		/*if(taxrequestinfo.getShippingDestination(i).getShippingAddress() !=null){
			postalCode = taxrequestinfo.getShippingDestination(i).getShippingAddress().getPostalCode();
			taxrequestinfo.getShippingDestination(i).getShippingAddress().setPostalCode(modifiedPostalCode(postalCode));
		}*/

		String cybersourceTagOn = getCatalogTools().getThirdPartyTagStatus(taxrequestinfo.getOrder().getSiteId(), getCatalogTools(), BBBTagConstants.CYBERSOURCE_TAG);
		if (cybersourceTagOn != null && cybersourceTagOn.equalsIgnoreCase(BBBCoreConstants.FALSE)) {
			if (isLoggingDebug()) {
				logDebug("Skipping Tax Calculation as CyberSource Integration is turned off");
			}

			CyberSourceStatus taxStatus = createCyberSourceStatusForZeroTax(taxrequestinfo, paramInt);
			/*Set the error message so that Tax request may be marked as failure*/
			taxStatus.setErrorMessage("Skipping Tax Calculation as CyberSource Integration is turned off");
			return taxStatus;
		}

		if (taxrequestinfo.getShippingDestination(paramInt).getShippingAddress() == null) {
			if (isLoggingDebug()) {
				logDebug("Skipping Tax Calculation as this is a BOPUS Item");
			}

			return createCyberSourceStatusForZeroTax(taxrequestinfo, paramInt);
		}
		String stateId = taxrequestinfo.getShippingDestination(paramInt).getShippingAddress().getState();
		String shippingGroupId = ((RepositoryContactInfo)taxrequestinfo.getShippingDestination(paramInt).getShippingAddress()).getRepositoryItem().getRepositoryId();
		String siteId = SiteContextManager.getCurrentSiteId();

		if (getCatalogTools().isNexusState(siteId, stateId)) {

			if (isLoggingDebug()) {
				logDebug("Skipping Tax Calculation as the Shipping address belongs to Nexus states");
			}

			return createCyberSourceStatusForZeroTax(taxrequestinfo, paramInt);
		}
		
		if(taxrequestinfo.getOrder() instanceof BBBOrder){
			Map<String, Boolean> taxOverrideMap = ((BBBOrder)(taxrequestinfo.getOrder())).getTaxOverrideMap();
			if(taxOverrideMap.containsKey(paramInt) && taxOverrideMap.get(paramInt)){
				if (isLoggingDebug()) {
					logDebug("Skipping Tax Calculation as Tax is overriden");
				}
				return createCyberSourceStatusForZeroTax(taxrequestinfo, paramInt);
			}
		}
		
		Map specialInstructions = taxrequestinfo.getOrder().getSpecialInstructions();
		if(!BBBUtility.isMapNullOrEmpty(specialInstructions)){
			specialInstructions.clear();
		}
		
		//disable GC from being taxed on canada site
		if(BBBCoreConstants.SITE_BAB_CA.equals(siteId) || TBSConstants.SITE_TBS_BAB_CA.equals(siteId)){
			TaxableItem[] taxableItems = taxrequestinfo.getShippingDestination(paramInt).getTaxableItems();
			if(taxableItems!=null){
				for(TaxableItem item :taxableItems){
					final TaxableItemImpl taxableItem = (TaxableItemImpl) item;
					boolean isGCItem= getCatalogTools().isGiftCardItem(siteId, item.getCatalogRefId());
					if (isGCItem){
						taxableItem.setAmount(0.0);
					}
				}
			}
		}
		if (taxrequestinfo.getOrder() != null) {
			((TaxRequestInfoImpl) taxrequestinfo).setBillingAddress(((BBBOrderImpl) taxrequestinfo.getOrder()).getBillingAddress());
		}
		setSellerRegistrationNumber(null);
		TaxStatus taxStatus = null;
		BBBPerformanceMonitor.start("BBBCyberSourceTax.calculate()");
		
		/* Start BBBSL-3522 */
		int maxCyberSourceCallCount = Integer.parseInt(this.getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBTagConstants.MAX_CYBERSOURCE_TAX_CALL, "1"));
		int retryCount = 1;
		//clear the map so that previous tax records goes away
		
		while (retryCount <= maxCyberSourceCallCount){
			if (isLoggingDebug()) {
				logDebug("CyberSource Retry Count:" + retryCount);
			}
			if(!BBBUtility.isMapNullOrEmpty(specialInstructions)){
				specialInstructions.clear();
			}
			boolean errorFlag = false;
			StringBuffer errorMsg=new StringBuffer();
			if(this.isTestCyberSourceRetryCount()){
				logError("Testing Tax Transaction call Failure in Progress");
				errorFlag = true;
				persistLogErrorMsg(taxStatus, taxrequestinfo, paramInt,errorMsg, "Tax calculated for testing");
	    	}else {
	    		try{
	    			long startTimeBeforCalculate = System.currentTimeMillis();
	    			taxStatus = super.calculate(taxrequestinfo, paramInt, list);
	    			logInfo("Total time taken to Calculate TAX by Cybersource : " + (System.currentTimeMillis()-startTimeBeforCalculate));
	    			//The below If condition checks for any error message from cybersource and not captured by
	    			if(taxStatus != null && (taxStatus.getTransactionSuccess() == false || !StringUtils.isBlank(taxStatus.getErrorMessage()))){
	    					logError("Tax Transaction call Status: " + taxStatus.getTransactionSuccess());
	    					logError("Tax Transaction call Failure : " + taxStatus.getErrorMessage() + "\n");
	    					persistLogErrorMsg(taxStatus, taxrequestinfo, paramInt,errorMsg, BBBCheckoutConstants.TAX_CALCULATED_BY_USR_ADDR);
	    					errorFlag = true;
	    			}
	    		}catch(Exception ex){	
	    			logError("Tax Transaction call Failure Stacktrace: ", ex);
	    			if(retryCount == 1){
	    				errorMsg.append("Error while Calling for Tax by " + BBBCheckoutConstants.TAX_CALCULATED_BY_USR_ADDR);
	    				persistLogErrorMsg(taxStatus, taxrequestinfo, paramInt,errorMsg, BBBCheckoutConstants.TAX_CALCULATED_BY_USR_ADDR);
	    			} else {
	    				errorMsg.append("Error while Calling for Tax by " + BBBCheckoutConstants.TAX_CALCULATED_BY_BBB_ADDR);
	    				persistLogErrorMsg(taxStatus, taxrequestinfo, paramInt,errorMsg, BBBCheckoutConstants.TAX_CALCULATED_BY_BBB_ADDR);
	    			}
	    			errorFlag = true;
	    		}
	    	}
			if(!errorFlag){
				if (isLoggingDebug()) {
					logDebug("CyberSource Retry Count:" + retryCount + " SuccessFull and add Special Instruction to Map");
				}
				if(retryCount == 1)
				specialInstructions.put(shippingGroupId, BBBCheckoutConstants.TAX_CALCULATED_BY_USR_ADDR);
				else
				specialInstructions.put(shippingGroupId, BBBCheckoutConstants.TAX_CALCULATED_BY_BBB_ADDR);	
				break;
			}
			else
			{
				//BBBSL-4993 Change billing address | CS tax call on Paypal address. START
				//Print Old and New Billing address in log file (Log Level Info)
				logInfo("Setting new billing address as error flag is true.");
				Address originalAddress = ((TaxRequestInfoImpl) taxrequestinfo).getBillingAddress();
				if(originalAddress !=null){
					logInfo("Old Billing Address: ["+originalAddress.getAddress1()+","+originalAddress.getAddress2()+","+
							originalAddress.getCity()+","+originalAddress.getState()+","+originalAddress.getCountry()+"]");
				}
				final Address changeBillingAddress = new Address();
				changeBillingAddress.setAddress1(getAddress1());
				changeBillingAddress.setCity(getCity());
				changeBillingAddress.setState(getState());
				changeBillingAddress.setPostalCode(getPostalCode());
				changeBillingAddress.setCountry(getCountry());
				((TaxRequestInfoImpl) taxrequestinfo).setBillingAddress(changeBillingAddress);
				logInfo("New Billing Address: "+((TaxRequestInfoImpl) taxrequestinfo).getBillingAddress().toString());
				//BBBSL-4993 Change billing address | CS tax call on Paypal address. START
			}
			if (isLoggingDebug()) {
				logDebug("CyberSource Retry Count:" + retryCount + " UnSuccessFull.");
			}
			// BBBSL-3318 Cybersouce Failure- Start
			//Below Condition makes sure its last call.
			if(retryCount == maxCyberSourceCallCount){
				/*Once a structure is created, assign it back*/
				taxStatus = this.getStateTax(taxrequestinfo,paramInt);	
				specialInstructions.put(shippingGroupId, BBBCheckoutConstants.TAX_CALCUATED_BY_BCC);
				logInfo("Tax calcualted by BCC");
				break;
			}
			retryCount++;
		}
	
		//changes for cybersource zipcode fix to be uncommented//
		//taxrequestinfo.getShippingDestination(i).getShippingAddress().setPostalCode(postalCode);
		BBBPerformanceMonitor.end("BBBCyberSourceTax.calculate()");
		logInfo("Total time taken by method BBBCyberSourceTax.calculate() " + (System.currentTimeMillis()-startTime));
		return taxStatus;
	}
	
	/*
	 * Method logs Error Message and make an entry of error message in SKU_SCAN_AUDIT_LOGGING table
	 * 
	 */
	private void persistLogErrorMsg(TaxStatus taxStatus, TaxRequestInfo taxrequestinfo,int paramInt, StringBuffer errorMsg, String taxCalculationSource){
		errorMsg.append("Tax Transaction call Failure : \r\n" );
		if(taxStatus != null){
			errorMsg.append("Transaction Id : " + taxStatus.getTransactionId() + "\r\n ");
			errorMsg.append("Transaction Status : " + taxStatus.getTransactionSuccess() + "\r\n ");
			errorMsg.append("Transaction ErrorMessage : " + taxStatus.getErrorMessage() + "\r\n ");
			errorMsg.append("Order Id: " + taxrequestinfo.getOrderId() + "\r\n ");
		}	
		TaxableItem[] taxableItems = taxrequestinfo.getShippingDestination(paramInt).getTaxableItems();
		if(taxableItems!=null){
			for(TaxableItem item :taxableItems){
				final TaxableItemImpl taxableItem = (TaxableItemImpl) item;
				errorMsg.append(" Item Id : " + taxableItem.getProductId() + "\r\n ");
				errorMsg.append("ProductId : " + taxableItem.getProductId() + "\r\n ");
				errorMsg.append("Qty : " + taxableItem.getQuantity() + "\r\n ");
				errorMsg.append("Tax Status : " + taxableItem.getTaxStatus() + "\r\n ");
				errorMsg.append("Product Amount : " + taxableItem.getAmount()+ "\r\n ");
			}
		}
		//SAP-255 Changed code to get the source of traffic from Header field "origin_of_traffic" set in Mobile code for both Mobile Web and Mobile App.
		String originOfTraffic = BBBUtility.getOriginOfTraffic();
	    Date today = new Date();
		Timestamp dateTimeStamp=new Timestamp(today.getTime());
		String time = dateTimeStamp.toString();
		this.sendLogEvent(new PersistedInfoLogEvent(taxCalculationSource, taxrequestinfo.getOrderId(), null, null, null, SiteContextManager.getCurrentSiteId(), originOfTraffic, null, time, taxCalculationSource, errorMsg.toString()));
	}
	
	protected void setShipToInfo(RequestMessage paramRequestMessage,
			TaxRequestInfo taxrequestinfo, int i) throws Exception {
		String stateId = taxrequestinfo.getShippingDestination(i).getShippingAddress().getState();
		if(BBBCoreConstants.SITE_BAB_CA.equals(SiteContextManager.getCurrentSiteId()) ||
 		   TBSConstants.SITE_TBS_BAB_CA.equals(SiteContextManager.getCurrentSiteId())) {
			paramRequestMessage.getTaxService().setSellerRegistration(getSellerRegistrationNumberByState(stateId));
		}
		super.setShipToInfo(paramRequestMessage, taxrequestinfo, i);
	}
	
	private String getSellerRegistrationNumberByState(String stateId) {
		String result = null;
		try {
			if (stateId != null) {
				List<String> configValues = null;
				configValues = getCatalogTools().getAllValuesForKey(BBBCheckoutConstants.SELLER_REGISTRATION_NUMBER, stateId);
				if (configValues != null && configValues.size() > 0) {
					result = configValues.get(0);
					if (isLoggingDebug()) {
						logDebug("Seller Registration Number for state["
								+ stateId + "]" + configValues.get(0));
					}
				}
			}
		} catch (BBBSystemException e) {
			logError("BBBSystemException in getSellerRegistrationNumber method" + e);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException in getSellerRegistrationNumber method" + e);
		}
		return result;

	}

	/**
	 * Handle System exceptions from Credit Card Authorization and mark the transaction successful
	 *  
	 * @param errorFlag
	 * @param pParams
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected TaxStatus[] handleTaxError(TaxStatus[] taxStatuses, TaxRequestInfo taxRequestInfo){
		int reasonCode = 100;
		Map specialInstructions = taxRequestInfo.getOrder().getSpecialInstructions();
		ShippingDestination[] shippingDestinations = taxRequestInfo.getShippingDestinations();
		boolean errorFlag = false;
		if(taxStatuses != null && taxStatuses.length > 0){
			CyberSourceStatus taxStatus = (CyberSourceStatus)taxStatuses[0];
			/*Check if there's an exception*/
			if(taxStatus.getTransactionSuccess() == false || !StringUtils.isBlank(taxStatus.getErrorMessage())){
				/*Flag there's an error*/
				errorFlag = true;
				/*If there's system failure (and as a result there are fewer tax statues than shipping destinations), create zero tax structure)*/
				if(taxStatuses.length != shippingDestinations.length || taxStatus.getReply() == null || taxStatus.getReply().getTaxReply() == null){
					CyberSourceStatus[] cyberSourceStatuses = new CyberSourceStatus[shippingDestinations.length];
					for(int index = 0; index < shippingDestinations.length; index++){
						cyberSourceStatuses[index] = createCyberSourceStatusForZeroTax(taxRequestInfo, index);
					}
					
					/*Once a structure is created, assign it back*/
					taxStatuses = cyberSourceStatuses;
				}
				
				/*Correct the reason code / transaction success flag*/
				ReplyMessage reply = null;
				for(int index = 0; index < taxStatuses.length; index++){
					reply = ((CyberSourceStatus)taxStatuses[index]).getReply();
					reasonCode = reply.getReasonCode().intValue();
					if(errorFlag || reasonCode == 150 || reasonCode == 151 || reasonCode == 152) {
						/*Mark the Tax status as successful to make the transaction success*/
						reply.setReasonCode(BigInteger.valueOf(100L));
						
						/* Set the CYBERSOURCE_TAX_FAILURE Flag */
						specialInstructions.put(BBBCheckoutConstants.CYBERSOURCE_TAX_FAILURE, Boolean.TRUE.toString());
					}
				}
			}
		}
		
		return taxStatuses;
	}

	private CyberSourceStatus createCyberSourceStatusForZeroTax(TaxRequestInfo taxrequestinfo, int i) {

		if (isLoggingDebug()) {
			logDebug("START: Creating CyberSourceStatus with zero tax as the CyberSource call is skipped");
		}
		
		TaxReplyItem taxReplyItem = null;		
		int itemsCount = taxrequestinfo.getShippingDestination(i).getTaxableItems().length;
		TaxReplyItem[] taxReplyItems = new TaxReplyItem[itemsCount];
		ReplyMessage replyMessage = new ReplyMessage();
		replyMessage.setReasonCode(BigInteger.valueOf(100));

		TaxReply taxReply = new TaxReply();
		taxReply.setTotalTaxAmount("0");
		taxReply.setTotalStateTaxAmount("0");
		taxReply.setTotalCityTaxAmount("0");
		taxReply.setTotalCountyTaxAmount("0");
		taxReply.setTotalDistrictTaxAmount("0");
		
		for (i = 0; i < itemsCount; i++) {
			taxReplyItem = new TaxReplyItem();
			taxReplyItem.setCityTaxAmount("0");
			taxReplyItem.setCountyTaxAmount("0");
			taxReplyItem.setDistrictTaxAmount("0");
			taxReplyItem.setId(BigInteger.ZERO);
			taxReplyItem.setStateTaxAmount("0");
			taxReplyItem.setTotalTaxAmount("0");
			taxReplyItems[i] = taxReplyItem;
		}
		taxReply.setItem(taxReplyItems);
		replyMessage.setTaxReply(taxReply);

		CyberSourceStatus cyberSourceStatus = new CyberSourceStatus(
				replyMessage);
		
		if (isLoggingDebug()) {
			logDebug("END: Creating CyberSourceStatus with zero tax as the CyberSource call is skipped");
		}
		return cyberSourceStatus;
	}	
	/**
	 * ovveride method assignTaxProductCode , to not to default quantity of each line item to 1
	 */
	@Override
	public void assignTaxProductCode(TaxableItem paramTaxableItem,
			Item paramItem) throws CyberSourceException {
		paramItem.setProductCode(paramTaxableItem.getTaxStatus());
		paramItem.setQuantity(new BigInteger(BBBCoreConstants.BLANK+paramTaxableItem.getQuantity()));
		if (paramTaxableItem.getProductId() == null)
			throw new CyberSourceException(
					MessageConstant.CYBER_SOURCE_FIELD_NULL,
					true,
					new Object[] { MessageConstant.CYBER_SOURCE_PRODUCT_ID_FIELD });
		paramItem.setProductName(paramTaxableItem.getProductId());
		if (paramTaxableItem.getCatalogRefId() == null)
			throw new CyberSourceException(
					MessageConstant.CYBER_SOURCE_FIELD_NULL,
					true,
					new Object[] { MessageConstant.CYBER_SOURCE_CATALOG_REF_ID_FIELD });
		paramItem.setProductSKU(paramTaxableItem.getCatalogRefId());
	}
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	//changes for cybersource zipcode fix to be uncommented//
	/*private String modifiedPostalCode(String postalCode) {
		String modifiedPostalCode = postalCode;
		if (BBBUtility.isNotEmpty(modifiedPostalCode)) {
			if (modifiedPostalCode.indexOf(BBBCoreConstants.HYPHEN) > 0) {
				modifiedPostalCode = modifiedPostalCode.substring(0,modifiedPostalCode.indexOf(BBBCoreConstants.HYPHEN));
			}
		} 
		return modifiedPostalCode;
	}*/

	/**
	 * @return the testCyberSourceRetryCount
	 */
	public boolean isTestCyberSourceRetryCount() {
		return testCyberSourceRetryCount;
	}

	/**
	 * @param testCyberSourceRetryCount the testCyberSourceRetryCount to set
	 */
	public void setTestCyberSourceRetryCount(final boolean testCyberSourceRetryCount) {
		this.testCyberSourceRetryCount = testCyberSourceRetryCount;
	}

	/**
	 * @return the pricingTools
	 */
	public PricingTools getPricingTools() {
		return pricingTools;
	}

	/**
	 * @param pricingTools the pricingTools to set
	 */
	public void setPricingTools(PricingTools pricingTools) {
		this.pricingTools = pricingTools;
	}

	/**
	 * 
	 * @return address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * 
	 * @param address1
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * 
	 * @return city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * 
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * 
	 * @return postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * 
	 * @param postalCode
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * 
	 * @return state
	 */
	public String getState() {
		return state;
	}

	/**
	 * 
	 * @param state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * 
	 * @return country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * 
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
}