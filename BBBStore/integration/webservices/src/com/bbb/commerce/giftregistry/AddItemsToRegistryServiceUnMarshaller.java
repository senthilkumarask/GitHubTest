/*
 *
 * File  : AddItemToRegistryServiceUnMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.AddItemsToRegistryResponseDocument;
import com.bedbathandbeyond.www.AddItemsToRegistryResponseDocument.AddItemsToRegistryResponse;



/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author ssha53
 * 
 */
public class AddItemsToRegistryServiceUnMarshaller extends ResponseUnMarshaller {

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
				.start("AddItemsToRegistryServiceMarshaller-processResponse");

		
		final ValidateAddItemsResVO addItemsResVO = new ValidateAddItemsResVO();
		if (responseDocument != null) {


			final AddItemsToRegistryResponse  addItemsToRegRes = ((AddItemsToRegistryResponseDocument) responseDocument).getAddItemsToRegistryResponse();
					

			if (addItemsToRegRes != null) {
				if (!addItemsToRegRes.getAddItemsToRegistryResult().getStatus()
						.getErrorExists()) {
					addItemsResVO.getServiceErrorVO()
					.setErrorExists(false);
									
				} else {

					addItemsResVO.getServiceErrorVO()
							.setErrorExists(true);
					addItemsResVO.getServiceErrorVO().setErrorId(
							addItemsToRegRes.getAddItemsToRegistryResult().getStatus().getID()
							);
					addItemsResVO.getServiceErrorVO()
							.setErrorMessage(addItemsToRegRes
											.getAddItemsToRegistryResult().getStatus().getErrorMessage());
					addItemsResVO.setWebServiceError(true);
				}

			}

		}

		BBBPerformanceMonitor
				.end("AddItemsToRegistryServiceMarshaller-processResponse");
		return addItemsResVO;
	}
}
