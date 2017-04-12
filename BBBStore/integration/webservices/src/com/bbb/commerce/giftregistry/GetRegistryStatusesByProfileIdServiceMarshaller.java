package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.commerce.giftregistry.vo.RegistryStatusVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.GetRegistryStatusesByProfileIdDocument;
import com.bedbathandbeyond.www.GetRegistryStatusesByProfileIdDocument.GetRegistryStatusesByProfileId;

/**
 * Marshalling the web service
 * 
 * @author prbhoomu
 * 
 */
public class GetRegistryStatusesByProfileIdServiceMarshaller extends RequestMarshaller {

	/**
	 * default serial verson Id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request.
	 * 
	 * @param ServiceRequestIF
	 * 
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBSystemException {

		logDebug("GetRegistryStatusesByProfileIdServiceMarshaller.buildRequest() method start");

		BBBPerformanceMonitor
				.start("GetRegistryStatusesByProfileIdServiceMarshaller-buildRequest");

		
		GetRegistryStatusesByProfileIdDocument getRegistryStatusesByProfileIdDocument = null;
		try {
			getRegistryStatusesByProfileIdDocument = GetRegistryStatusesByProfileIdDocument.Factory.newInstance();
			getRegistryStatusesByProfileIdDocument.setGetRegistryStatusesByProfileId(mapGetRegistryInfoByProfileIdRequest(pRreqVO));
				
		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("GetRegistryStatusesByProfileIdServiceMarshaller-buildRequest");
			throw new BBBSystemException(e.getMessage(), e);
		}finally{
			BBBPerformanceMonitor
			.end("GetRegistryStatusesByProfileIdServiceMarshaller-buildRequest");
		}

		logDebug("GetRegistryStatusesByProfileIdServiceMarshaller.buildRequest() method ends");

		return getRegistryStatusesByProfileIdDocument;
	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param pReqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private GetRegistryStatusesByProfileId mapGetRegistryInfoByProfileIdRequest(ServiceRequestIF pReqVO)
			throws BBBSystemException {

		logDebug("GetRegistryStatusesByProfileIdServiceMarshaller.mapCreateRegistryRequest() method start");
		BBBPerformanceMonitor
				.start("GetRegistryStatusesByProfileIdServiceMarshaller-mapCreateRegistryRequest");

		GetRegistryStatusesByProfileId getRegistryInfoByProfileId = GetRegistryStatusesByProfileId.Factory.newInstance();
		//DozerBeanMapper mapper = getDozerBean().getDozerMapper();		
		try {
			//mapper.map(pReqVO, getRegistryInfoByProfileId);
			
			getRegistryInfoByProfileId.setProfileID(((RegistryStatusVO)pReqVO).getProfileId());
			getRegistryInfoByProfileId.setUserToken(((RegistryStatusVO)pReqVO).getUserToken());
			getRegistryInfoByProfileId.setSiteFlag(((RegistryStatusVO)pReqVO).getSiteId());
		} 
		catch (MappingException me) {
			BBBPerformanceMonitor
			.end("GetRegistryStatusesByProfileIdServiceMarshaller-mapCreateRegistryRequest");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10147 +" MappingException from mapGetRegistryInfoByProfileIdRequest from GetRegistryStatusesByProfileIdServiceMarshaller",me);
			throw new BBBSystemException(me.getMessage(), me);
		}
		catch (Exception e) {
			BBBPerformanceMonitor
			.end("GetRegistryStatusesByProfileIdServiceMarshaller-mapCreateRegistryRequest");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10148 +" Exception from mapGetRegistryInfoByProfileIdRequest from GetRegistryStatusesByProfileIdServiceMarshaller",e);
			throw new BBBSystemException(e.getMessage(), e);
		}
		finally{
			BBBPerformanceMonitor
					.end("GetRegistryStatusesByProfileIdServiceMarshaller-mapCreateRegistryRequest");
		}
		logDebug("GetRegistryStatusesByProfileIdServiceMarshaller.mapCreateRegistryRequest() method ends");
		return getRegistryInfoByProfileId;
	}

}