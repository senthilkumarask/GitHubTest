/*
 *
 * File  : CreateRegistryServiceUnMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.SetAnnouncementCardResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.SetAnnouncementCardCountResponseDocument;
import com.bedbathandbeyond.www.SetAnnouncementCardCountResponseDocument.SetAnnouncementCardCountResponse;


/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author ikhan2
 * 
 */
public class AnnounceCardCountServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
		
		logDebug("AnnounceCardCountServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("AnnounceCardCountServiceUnMarshaller-processResponse");

		final SetAnnouncementCardResVO cardResVO = new SetAnnouncementCardResVO();
		if (responseDocument != null) {
			try {
				
				final SetAnnouncementCardCountResponse setCardCountRes 
					= ((SetAnnouncementCardCountResponseDocument) responseDocument)
					.getSetAnnouncementCardCountResponse();

				if (setCardCountRes != null) {
					if (!setCardCountRes.getSetAnnouncementCardCountResult().
							getStatus().getErrorExists()) {
						
						//operation success
						cardResVO.setOperationStatus(true);
						
					} else {

						cardResVO.getServiceErrorVO()
								.setErrorExists(true);
						cardResVO.getServiceErrorVO().setErrorId(
								setCardCountRes.getSetAnnouncementCardCountResult().getStatus().getID());
						cardResVO.getServiceErrorVO()
								.setErrorMessage(setCardCountRes
												.getSetAnnouncementCardCountResult().getStatus().getErrorMessage());
						cardResVO.setWebServiceError(true);
					}

				}
			} catch (Exception e) {
				BBBPerformanceMonitor
				.end("AnnounceCardCountServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1360,e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor
				.end("AnnounceCardCountServiceUnMarshaller-processResponse");
			}
		}

		logDebug("AnnounceCardCountServiceUnMarshaller.processResponse() method ends");
		
		return cardResVO;
	}
}
