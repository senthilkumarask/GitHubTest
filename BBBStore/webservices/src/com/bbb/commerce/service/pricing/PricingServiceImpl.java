package com.bbb.commerce.service.pricing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

import atg.nucleus.Nucleus;

import com.bbb.exception.BBBBusinessException;
import com.bedbathandbeyond.atg.MessageError;
import com.bedbathandbeyond.atg.MessageHeader;
import com.bedbathandbeyond.atg.PricingError;
import com.bedbathandbeyond.atg.PricingErrorDocument;
import com.bedbathandbeyond.atg.PricingRequest;
import com.bedbathandbeyond.atg.PricingResponse;
import com.bedbathandbeyond.atg.PricingResponseDocument;

public class PricingServiceImpl implements PricingService {

	/**
	 * The operation invoked by the webservice engine. 
	 * 
	 * @param pRequestDocument
	 * @return pricingResponse
	 * @throws SoapFault
	 */
	public PricingResponseDocument performPricing(com.bedbathandbeyond.atg.PricingRequestDocument pRequestDocument) throws SoapFault {
		PricingResponseDocument responseDocument = PricingResponseDocument.Factory.newInstance();
		BBBPricingWebService ws = extractNucleus();		
		try {
			validateRequest(pRequestDocument);
			PricingResponse pricingResponse = ws.priceOrder(pRequestDocument.getPricingRequest());
			responseDocument.setPricingResponse(pricingResponse);
		} catch (Throwable e) {//NOPMD: We dont want to throw 500 error anytime
			if(ws.isLoggingError()){
				ws.logError("Webservice error while performing Pricing", e);
			}
			throw getSoapFault(pRequestDocument.getPricingRequest(), e);
		}
				
		return responseDocument;
	}

	protected BBBPricingWebService extractNucleus() {
		return (BBBPricingWebService) Nucleus.getGlobalNucleus().resolveName("/com/bbb/commerce/service/pricing/BBBPricingWebService");
	}
	
	private void validateRequest(com.bedbathandbeyond.atg.PricingRequestDocument pRequestDocument) throws BBBBusinessException{
		List<XmlValidationError> validationErrors = createValidatioErrors(); 
	    XmlOptions xmlOptions = new XmlOptions();
	    xmlOptions.setErrorListener(validationErrors); 
	    if(!pRequestDocument.validate(xmlOptions)){
	    	for(XmlValidationError validationError : validationErrors){
	    		throw new BBBBusinessException(validationError.getErrorCode(),String.valueOf(validationError));
	    	}
	    }
	}

	/**
	 * @return
	 */
	protected ArrayList<XmlValidationError> createValidatioErrors() {
		return new ArrayList<XmlValidationError>();
	}	
	
	/**
	 * Create a Soap fault instance using exception
	 * 
	 * @param pPricingRequest
	 * @param pThrowable
	 * @return
	 */
	private SoapFault getSoapFault(PricingRequest pPricingRequest, Throwable pThrowable){
		SoapFault fault = new SoapFault(pThrowable);
		
		PricingErrorDocument errorDocument = PricingErrorDocument.Factory.newInstance();
		PricingError error = PricingError.Factory.newInstance();
		MessageError errorMessage = MessageError.Factory.newInstance();
		
		if(pThrowable instanceof BBBBusinessException) {
			errorMessage.setCode("Server.Biz");
		} else {
			errorMessage.setCode("Server.Sys");
		}
		errorMessage.setDescription(pThrowable.getMessage());
		error.setHeader(populateResponseHeader(pPricingRequest));
		error.setError(errorMessage);
		errorDocument.setPricingError(error);
		fault.setFaultMessage(errorDocument);
		
		return fault;
	}
	
	/**
	 * Create header for response
	 * 
	 * @param pWSRequest
	 * @return
	 */
	private MessageHeader populateResponseHeader(final PricingRequest pWSRequest) {
		MessageHeader pricingHeader = MessageHeader.Factory.newInstance();
		MessageHeader requestHeader = pWSRequest.getHeader();

		/* Set header information */
		pricingHeader.setOrderId(requestHeader.getOrderId());
		pricingHeader.setSiteId(requestHeader.getSiteId());
		pricingHeader.setOrderDate(requestHeader.getOrderDate());
		if(requestHeader.getCurrencyCode() != null) {
			pricingHeader.setCurrencyCode(requestHeader.getCurrencyCode());
		}
		if(requestHeader.getCallingAppCode() != null){
			pricingHeader.setCallingAppCode(requestHeader.getCallingAppCode());
		}
		pricingHeader.setTimestamp(Calendar.getInstance());

		return pricingHeader;
	}
}