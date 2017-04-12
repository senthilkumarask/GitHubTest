package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.GetRegistryInfoByProfileIdDocument;
import com.bedbathandbeyond.www.GetRegistryInfoByProfileIdDocument.GetRegistryInfoByProfileId;

/**
 * Marshalling the web service
 * 
 * @author sku134
 * 
 */
public class GetRegistryByProfileIdServiceMarshaller extends RequestMarshaller {

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

		logDebug("GetRegistryByProfileIdServiceMarshaller.buildRequest() method start");

		BBBPerformanceMonitor
				.start("GetRegistryByProfileIdServiceMarshaller-buildRequest");

		
		GetRegistryInfoByProfileIdDocument getRegistryInfoByProfileIdDocument = null;
		try {
			getRegistryInfoByProfileIdDocument = GetRegistryInfoByProfileIdDocument.Factory
					.newInstance();
			getRegistryInfoByProfileIdDocument.setGetRegistryInfoByProfileId(mapGetRegistryInfoByProfileIdRequest(pRreqVO));
				
		} catch (Exception e) {

			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1369,e.getMessage(), e);
		}finally{
			BBBPerformanceMonitor
			.end("GetRegistryByProfileIdServiceMarshaller-buildRequest");
		}

		logDebug("GetRegistryByProfileIdServiceMarshaller.buildRequest() method ends");

		return getRegistryInfoByProfileIdDocument;
	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param pReqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private GetRegistryInfoByProfileId mapGetRegistryInfoByProfileIdRequest(ServiceRequestIF pReqVO)
			throws BBBSystemException {

		logDebug("GetRegistryByProfileIdServiceMarshaller.mapCreateRegistryRequest() method start");

		BBBPerformanceMonitor
				.start("GetRegistryByProfileIdServiceMarshaller-mapCreateRegistryRequest");

		GetRegistryInfoByProfileId getRegistryInfoByProfileId = GetRegistryInfoByProfileId.Factory.newInstance();
		//DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			getRegistryInfoByProfileId.setProfileID(((RegistrySearchVO)pReqVO).getProfileId().getRepositoryId().toString());
			
			getRegistryInfoByProfileId.setUserToken(((RegistrySearchVO)pReqVO).getUserToken());
			getRegistryInfoByProfileId.setSiteFlag(((RegistrySearchVO)pReqVO).getSiteId());
		} 
		catch (MappingException me) {
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10147 +" MappingException from mapGetRegistryInfoByProfileIdRequest from GetRegistryByProfileIdServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1369,me.getMessage(), me);
		}
		catch (Exception e) {
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10148 +" Exception from mapGetRegistryInfoByProfileIdRequest from GetRegistryByProfileIdServiceMarshaller",e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1369,e.getMessage(), e);
		}
		finally{
			BBBPerformanceMonitor
					.end("GetRegistryByProfileIdServiceMarshaller-mapCreateRegistryRequest");
		}

		logDebug("GetRegistryByProfileIdServiceMarshaller.mapCreateRegistryRequest() method ends");

		return getRegistryInfoByProfileId;
	}

}