/*
 *
 * File  : CreateRegistryServiceMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.SetAnnouncementCardCountDocument;
import com.bedbathandbeyond.www.SetAnnouncementCardCountDocument.SetAnnouncementCardCount;


/**
 * 
 * This class contain methods used for marshalling the webservice request.
 * 
 * @author ikhan2
 * 
 */
public class AnnounceCardCountServiceMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request.
	 * 
	 *  * @param ServiceRequestIF
	 *            the validate address pRreqVO vo
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBSystemException {

		logDebug("AnnounceCardCountServiceMarshaller.buildRequest() method start");
	
		BBBPerformanceMonitor
				.start("AnnounceCardCountServiceMarshaller-buildRequest");

		SetAnnouncementCardCountDocument setAnnouncementCardCountDocument = null;
		try {
			setAnnouncementCardCountDocument = SetAnnouncementCardCountDocument.Factory.newInstance();
			setAnnouncementCardCountDocument.setSetAnnouncementCardCount(
					mapSetAnnouncementCardCountRequest(pRreqVO));

		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("AnnounceCardCountServiceMarshaller-buildRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1359,e.getMessage(), e);
		}finally{
			BBBPerformanceMonitor
			.end("AnnounceCardCountServiceMarshaller-buildRequest");		
		}

		logDebug("AnnounceCardCountServiceMarshaller.buildRequest() method ends");

		return setAnnouncementCardCountDocument;
	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param ServiceRequestIF
	 *            the registry pReqVOs vo
	 * 
	 * @return the create registry.
	 * @throws BBBSystemException 
	 * 
	 */
	private SetAnnouncementCardCount mapSetAnnouncementCardCountRequest(ServiceRequestIF pReqVO)
			throws BBBSystemException {

		logDebug("AnnounceCardCountServiceMarshaller.mapCreateRegistryRequest() method start");
		
		BBBPerformanceMonitor
				.start("AnnounceCardCountServiceMarshaller-buildValidateAddressType");

		SetAnnouncementCardCount setAnnouncementCardCount = SetAnnouncementCardCount.Factory.newInstance();
		
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pReqVO, setAnnouncementCardCount);
		}
		catch(MappingException me)
		{
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10141 +" MappingException from mapSetAnnouncementCardCountRequest from AnnounceCardCountServiceMarshaller",me);
			BBBPerformanceMonitor
			.end("AnnounceCardCountServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1359,me.getMessage(), me);
		}finally{
			BBBPerformanceMonitor
			.end("AnnounceCardCountServiceMarshaller-buildValidateAddressType");
		}
		
		logDebug("AnnounceCardCountServiceMarshaller.mapCreateRegistryRequest() method ends");

		return setAnnouncementCardCount;
	}

}