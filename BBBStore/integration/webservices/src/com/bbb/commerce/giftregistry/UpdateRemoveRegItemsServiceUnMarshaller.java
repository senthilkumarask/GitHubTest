/*
 *
 * File  : CreateRegistryServiceUnMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.ManageRegItemsResVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.UpdateRegistryItemsResponseDocument;
import com.bedbathandbeyond.www.UpdateRegistryItemsResponseDocument.UpdateRegistryItemsResponse;



/**
 *  * This class contain methods used for umarshalling the webservice response
 * for Update Registry Items and Remove Registry Items webcalls.
 * 
 * @author ikhan2
 * 
 */
public class UpdateRemoveRegItemsServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		logDebug("UpdateRemoveRegItemsServiceUnMarshaller.processResponse() method start");
		
		BBBPerformanceMonitor
				.start("UpdateRemoveRegItemsServiceUnMarshaller-processResponse");

		//Response VO for Update & Remove registry items
		final ManageRegItemsResVO manageRegItemsResVO = new ManageRegItemsResVO();
		
		if (responseDocument != null) {

				
			final UpdateRegistryItemsResponse  updateRegItemsRes =
					((UpdateRegistryItemsResponseDocument) responseDocument).
					getUpdateRegistryItemsResponse();

			if (updateRegItemsRes != null) {
				if (!updateRegItemsRes.getUpdateRegistryItemsResult().getStatus()
						.getErrorExists()) {
					
					manageRegItemsResVO.setOperationStatus(true);
					manageRegItemsResVO.getServiceErrorVO()
						.setErrorExists(false);
									
				} else {

					manageRegItemsResVO.getServiceErrorVO()
							.setErrorExists(true);
					manageRegItemsResVO.getServiceErrorVO().setErrorId(
							updateRegItemsRes.getUpdateRegistryItemsResult().getStatus().getID()
							);
					manageRegItemsResVO.getServiceErrorVO()
							.setErrorMessage(updateRegItemsRes
											.getUpdateRegistryItemsResult().getStatus().getErrorMessage());
					manageRegItemsResVO.setWebServiceError(true);
				}
			}
				

		}

		BBBPerformanceMonitor
				.end("UpdateRemoveRegItemsServiceUnMarshaller-processResponse");
		
		logDebug("UpdateRemoveRegItemsServiceUnMarshaller.processResponse() method end");
		return manageRegItemsResVO;
	}
}
