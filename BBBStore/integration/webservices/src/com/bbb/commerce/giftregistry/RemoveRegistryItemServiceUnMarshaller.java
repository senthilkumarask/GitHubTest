/*
 *
 * File  : CreateRegistryServiceUnMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.AddItemsToRegistryResponseDocument;
import com.bedbathandbeyond.www.AddItemsToRegistryResponseDocument.AddItemsToRegistryResponse;


/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author ikhan2
 * 
 */
public class RemoveRegistryItemServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		BBBPerformanceMonitor
				.start("RemoveRegistryItemServiceUnMarshaller-processResponse");

		
		final ValidateAddItemsResVO addItemsResVO = new ValidateAddItemsResVO();
		if (responseDocument != null) {
			try {

				final AddItemsToRegistryResponse  addItemsRes = ((AddItemsToRegistryResponseDocument) responseDocument).getAddItemsToRegistryResponse();
						

				if (addItemsRes != null) {
					if (!addItemsRes.getAddItemsToRegistryResult().getStatus()
							.getErrorExists()) {
						addItemsResVO.getServiceErrorVO()
						.setErrorExists(false);
										
					} else {

						addItemsResVO.getServiceErrorVO()
								.setErrorExists(true);
						addItemsResVO.getServiceErrorVO().setErrorId(
								addItemsRes.getAddItemsToRegistryResult().getStatus().getID()
								);
						addItemsResVO.getServiceErrorVO()
								.setErrorMessage(addItemsRes
												.getAddItemsToRegistryResult().getStatus().getErrorMessage());
						addItemsResVO.setWebServiceError(true);
					}

				}
			} catch (Exception e) {
				BBBPerformanceMonitor
				.end("RemoveRegistryItemServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1380,e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor
				.end("RemoveRegistryItemServiceUnMarshaller-processResponse");
			}
		}

		return addItemsResVO;
	}
}
