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
import com.bedbathandbeyond.www.UpdateRegistryItems2ResponseDocument;
import com.bedbathandbeyond.www.UpdateRegistryItems2ResponseDocument.UpdateRegistryItems2Response;



/**
 *  * This class contain methods used for umarshalling the webservice response
 * for Update Registry Items and Remove Registry Items webcalls.
 * 
 * @author ikhan2
 * 
 */
public class UpdateRemoveRegItems2ServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		logDebug("UpdateRemoveRegItems2ServiceUnMarshaller.processResponse() method start");
		
		BBBPerformanceMonitor
				.start("UpdateRemoveRegItems2ServiceUnMarshaller-processResponse");

		//Response VO for Update & Remove registry items
		final ManageRegItemsResVO manageRegItemsResVO = new ManageRegItemsResVO();
		
		if (responseDocument != null) {

				
			final UpdateRegistryItems2Response updateRegItemsRes2 =
					((UpdateRegistryItems2ResponseDocument) responseDocument).
					getUpdateRegistryItems2Response();

			if (updateRegItemsRes2 != null) {
				if (!updateRegItemsRes2.getUpdateRegistryItems2Result().getStatus()
						.getErrorExists()) {
					
					manageRegItemsResVO.setOperationStatus(true);
					manageRegItemsResVO.getServiceErrorVO()
						.setErrorExists(false);
									
				} else {

					manageRegItemsResVO.getServiceErrorVO()
							.setErrorExists(true);
					manageRegItemsResVO.getServiceErrorVO().setErrorId(
							updateRegItemsRes2.getUpdateRegistryItems2Result().getStatus().getID()
							);
					manageRegItemsResVO.getServiceErrorVO()
							.setErrorMessage(updateRegItemsRes2
											.getUpdateRegistryItems2Result().getStatus().getErrorMessage());
					manageRegItemsResVO.setWebServiceError(true);
				}
			}
				

		}

		BBBPerformanceMonitor
				.end("UpdateRemoveRegItems2ServiceUnMarshaller-processResponse");
		
		logDebug("UpdateRemoveRegItems2ServiceUnMarshaller.processResponse() method end");
		return manageRegItemsResVO;
	}
}
