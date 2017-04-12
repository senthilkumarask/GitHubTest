/*
 *
 * File  : CreateRegistryServiceUnMarshaller.java
 * Project:     BBB
 *
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.ArrayOfValError;
import com.bedbathandbeyond.www.CreateRegistry2ResponseDocument;
import com.bedbathandbeyond.www.CreateRegistry2ResponseDocument.CreateRegistry2Response;


/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author ssha53
 * 
 */
public class CreateRegistry2ServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final ApplicationLogging MLOGGING =
		    ClassLoggingFactory.getFactory().getLoggerForClass(CreateRegistry2ServiceUnMarshaller.class);

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
		
		
		logDebug("CreateRegistryServiceUnMarshaller.processResponse() method start");
		

		BBBPerformanceMonitor
				.start("CreateRegistryServiceUnMarshaller-processResponse");

		final RegistryResVO createRegVO = new RegistryResVO();
		if (responseDocument != null) {
			try {

				final CreateRegistry2Response createRegRes = ((CreateRegistry2ResponseDocument) responseDocument)
						.getCreateRegistry2Response();

				if (createRegRes != null) {
					if (!createRegRes.getCreateRegistry2Result().getStatus().getErrorExists()) {
						createRegVO
								.setRegistryId(createRegRes
										.getCreateRegistry2Result()
										.getRegistryNum());
					} else {

						createRegVO.getServiceErrorVO()
								.setErrorExists(true);
						createRegVO.getServiceErrorVO().setErrorId(
								createRegRes.getCreateRegistry2Result().getStatus().getID());
						createRegVO.getServiceErrorVO()
								.setErrorMessage(createRegRes
												.getCreateRegistry2Result().getStatus().getErrorMessage());
						createRegVO.setWebServiceError(true);
						
						ArrayOfValError arrayOfValError = createRegRes.getCreateRegistry2Result().getStatus().getValidationErrors();
						logError("CreateRegistryServiceUnMarshaller Error in Response : Error Id "
									+ createRegVO.getServiceErrorVO().getErrorId());
						logError("CreateRegistryServiceUnMarshaller Error in Response : Error Message "
								+ createRegVO.getServiceErrorVO().getErrorMessage());
						
						for (int count = 0; count < arrayOfValError
								.sizeOfValErrorArray(); count++) {
							String errorKey = arrayOfValError.getValErrorArray(count).getKey();
							String errorValue = arrayOfValError.getValErrorArray(count).getValue();
							
							logError("CreateRegistryServiceUnMarshaller Validation Error Key  "	+ errorKey);
							logError("CreateRegistryServiceUnMarshaller Validation Error Value : "	+ errorValue);
							
						}
					}

				}
			} catch (Exception e) {
				BBBPerformanceMonitor
				.end("CreateRegistryServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1362,e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor
				.end("CreateRegistryServiceUnMarshaller-processResponse");				
			}
		}

		
		logDebug("CreateRegistryServiceUnMarshaller.processResponse() method ends");
		
		
		return createRegVO;
	}
}
