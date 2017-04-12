package com.bbb.internationalshipping.integration.checkoutrequest;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;

import atg.core.util.StringUtils;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.InternationalServiceException;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.EnvoyInitialParamsV2;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.Message;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.MultiLayerErrorMessage;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.MultilayerErrorResponse;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.MultilayerErrorResponse.Errors;
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.Payload;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBInternationalCheckoutResponseVO;
import com.bbb.internationalshipping.vo.checkoutrequest.IntlShippingErrorMessage;
import com.bbb.logging.LogMessageFormatter;


/**
 * The class is the unmarshaller class which takes the International Checkout response xml as input and
 * populate  a BBBInternationalCheckoutResponseVO * 
 * 
 */
public class BBBInternationalCheckoutUnMarshaller extends BBBGenericService {
	
	// JAXBContext instance
	private static JAXBContext context;
	
	/**
	 * Handle response.
	 *
	 * @param pResponseXml the response xml
	 * @param checkoutResponseVO the checkout response vo
	 * @throws BBBBusinessException 
	 * @throws InternationalServiceException the international service exception
	 */
	public void handleResponse(String pResponseXml,BBBInternationalCheckoutResponseVO checkoutResponseVO) throws BBBBusinessException  {
		
		logDebug("Entering class: BBBInternationalCheckoutUnMarshaller,  "
				+ "method : handleResponse");
		// Unmarshall the response string
		    Message messageObj;
		    try
		    {
				messageObj = (Message) getResponseAsObject(pResponseXml);
				Payload bodyObj = messageObj.getPayload();
				MultilayerErrorResponse errorResponse =bodyObj.getErrorResponse();
				if(errorResponse!=null)
				{
					Errors errors = errorResponse.getErrors();
					List<MultiLayerErrorMessage>  errorList=errors.getError();

					ArrayList<IntlShippingErrorMessage> list = new ArrayList<IntlShippingErrorMessage>();

					for(MultiLayerErrorMessage error:errorList)
					{
						IntlShippingErrorMessage  errorMessage=new IntlShippingErrorMessage();
						errorMessage.setId(error.getId());
						errorMessage.setDetails(error.getDetails());
						errorMessage.setField(error.getField());
						errorMessage.setMessage(error.getMessage());
						errorMessage.setName(error.getName());
						list.add(errorMessage);
					}
					checkoutResponseVO.setErrorMessage(list);
				}
				else
				{
					EnvoyInitialParamsV2 envoyInitialParamsV2 =bodyObj.getSetCheckoutSessionResponse().getEnvoyInitialParams();
		
					String chkSessionId =envoyInitialParamsV2.getFiftyOneCheckoutSessionId();
					checkoutResponseVO.setFullEnvoyUrl(envoyInitialParamsV2.getFullEnvoyUrl());
					if (!StringUtils.isBlank(chkSessionId) && (chkSessionId.indexOf('-') != -1)) {
						checkoutResponseVO.setInternationalOrderId(chkSessionId.substring(0, chkSessionId.indexOf('-')));
					} else {
						logError("checkoutSessionID received is null");
					}
				}
		    }
			catch (JAXBException e) {
				logError(LogMessageFormatter.formatMessage(null,
						"BBBInternationalCheckoutUnMarshaller.handleResponse() | BBBBusinessException "), e);
			throw new BBBBusinessException(BBBCatalogErrorCodes.JAXB_EXCEPTION,BBBCatalogErrorCodes.JAXB_EXCEPTION, e);
		}
		    logDebug("Exiting class: BBBInternationalCheckoutUnMarshaller,  "
					+ "method : handleResponse " );

	}


	/**
	 * Returns the response object from xml string.
	 *
	 * @param responseXml the response xml
	 * @return the response as object
	 * @throws JAXBException the jAXB exception
	 */
	private Object getResponseAsObject(String responseXml)
			throws JAXBException {
		logDebug("Entering class: BBBInternationalCheckoutUnMarshaller,  "
				+ "method : getResponseAsObject");
		
		if(null == context){ 
            context = JAXBContext.newInstance( 
                            "com.bbb.framework.jaxb.internationalshipping.checkoutrequest", 
                            Thread.currentThread().getContextClassLoader()); 
		}

		Unmarshaller unmarshaller = context.createUnmarshaller();

		SAXSource source = null;
		// Prepare the input, in this case a java.io.File (output)
		InputSource is = new InputSource(new StringReader(responseXml));
		source = new SAXSource(is);
		return ((javax.xml.bind.JAXBElement) unmarshaller.unmarshal(source)).getValue();	
	}
}
