package com.bbb.account.wallet;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.GetProfileDocument;
import com.bedbathandbeyond.www.GetProfileDocument.GetProfile;

/**
 * Marshaller for Get Profile Service
 * 
 * @author jvishn
 *
 */
public class GetProfileServiceMarshaller extends RequestMarshaller{

	private static final long serialVersionUID = 1L;
	
	/**
	 * The method is extension of the RequestMarshaller service which will take the request Vo object and create a XML Object for the webservice
	 */
	public XmlObject buildRequest(ServiceRequestIF pReqVo)
	throws BBBBusinessException, BBBSystemException {
	
	logDebug("GetProfileServiceMarshaller.buildRequest() method start");
	BBBPerformanceMonitor
			.start("GetProfileServiceMarshaller-buildRequest");

	GetProfileDocument getProfileDocument = null;
	
	try {
		getProfileDocument = GetProfileDocument.Factory.newInstance();
		getProfileDocument.setGetProfile(mapGetProfileRequest(pReqVo));
	} catch (Exception e) {
		logError("Exception in marshalling getProfile request", e);
		throw new BBBSystemException("Exception in marshalling getProfile request", e);
	}finally{
		BBBPerformanceMonitor
				.end("GetProfileServiceMarshaller-buildRequest");
	}
	
	logDebug("GetRegistryInfoServiceMarshaller.buildRequest() method ends");
	return getProfileDocument;
	}
	
	
	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param ServiceRequestIF
	 *            the registry pReqVOs vo
	 * 
	 * @return the update registry.
	 * @throws BBBSystemException 
	 * 
	 */
	private GetProfile mapGetProfileRequest(ServiceRequestIF reqVO)
			throws BBBSystemException {

		logDebug("GetProfileServiceMarshaller.mapGetRegistryInfoRequest() method start");
		
		BBBPerformanceMonitor
				.start("GetProfileServiceMarshaller-mapGetRegistryInfoRequest");

		GetProfile getProfile = GetProfile.Factory.newInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(reqVO, getProfile);
		}catch(MappingException me) {
			logError("MappingException from mapGetRegistryInfoByProfileIdRequest from GetProfileServiceMarshaller",me);
			BBBPerformanceMonitor
			.end("GetProfileServiceMarshaller-mapGetRegistryInfoRequest");
			throw new BBBSystemException(me.getMessage(), me);			
		}finally{
			BBBPerformanceMonitor
					.end("GetProfileServiceMarshaller-mapGetRegistryInfoRequest");
		}
		logDebug("GetProfileServiceMarshaller.mapGetRegistryInfoRequest() method ends");
		return getProfile;
	}
	
	
}
