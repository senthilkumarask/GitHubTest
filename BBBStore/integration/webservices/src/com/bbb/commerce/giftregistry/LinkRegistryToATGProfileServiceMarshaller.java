package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.MappingException;

import com.bbb.commerce.giftregistry.vo.LinkRegistryToProfileVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.LinkRegistryToATGProfileDocument;
import com.bedbathandbeyond.www.LinkRegistryToATGProfileDocument.LinkRegistryToATGProfile;

/**
 * 
 * This class contain methods used for marshalling the Link CoReg Profile to Reg webservice request.
 * 
 * @author ssha53
 * 
 */
public class LinkRegistryToATGProfileServiceMarshaller extends RequestMarshaller {

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

		logDebug("LinkRegistryToATGProfileServiceMarshaller.buildRequest() method start");
		
		BBBPerformanceMonitor
				.start("LinkRegistryToATGProfileServiceMarshaller-buildRequest");

		LinkRegistryToATGProfileDocument linkRegistryToATGProfileDocument = null;
		try {
			linkRegistryToATGProfileDocument = LinkRegistryToATGProfileDocument.Factory.newInstance();

			linkRegistryToATGProfileDocument.setLinkRegistryToATGProfile(mapLinkRegistryToATGProfileRequest(pRreqVO));
		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("LinkRegistryToATGProfileServiceMarshaller-buildRequest");
			throw new BBBSystemException(e.getMessage(), e);
		}finally{
		BBBPerformanceMonitor
				.end("LinkRegistryToATGProfileServiceMarshaller-buildRequest");
		}
		
		logDebug("LinkRegistryToATGProfileServiceMarshaller.buildRequest() method ends");

		return linkRegistryToATGProfileDocument;
	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param ServiceRequestIF
	 *            the registry pReqVOs vo
	 * 
	 * @return the get registry item list.
	 * @throws BBBSystemException 
	 * 
	 */
	private LinkRegistryToATGProfile mapLinkRegistryToATGProfileRequest(
			ServiceRequestIF pRreqVO) throws BBBSystemException {

		logDebug("LinkCoRegProfileToRegServiceMarshaller.mapLinkRegistryToATGProfileRequest() method start");
		
		BBBPerformanceMonitor
				.start("LinkCoRegProfileToRegServiceMarshaller-mapLinkRegistryToATGProfileRequest");

		
		LinkRegistryToATGProfile linkRegistryToATGProfile = LinkRegistryToATGProfile.Factory.newInstance();
		try {
			linkRegistryToATGProfile.setUserToken(((LinkRegistryToProfileVO)pRreqVO).getUserToken());
			linkRegistryToATGProfile.setEmail(((LinkRegistryToProfileVO)pRreqVO).getEmailId());	
			linkRegistryToATGProfile.setProfileID(((LinkRegistryToProfileVO)pRreqVO).getProfileId());
			linkRegistryToATGProfile.setRegistrantType(((LinkRegistryToProfileVO)pRreqVO).getRegCoreg());
			linkRegistryToATGProfile.setRegistryNum(((LinkRegistryToProfileVO)pRreqVO).getRegistryId());
			
		}catch(MappingException me) {
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10153 +" MappingException from mapLinkRegistryToATGProfileRequest from LinkCoRegProfileToRegServiceMarshaller",me);
			BBBPerformanceMonitor
			.end("LinkCoRegProfileToRegServiceMarshaller-mapLinkRegistryToATGProfileRequest");
			throw new BBBSystemException(me.getMessage(), me);
			
		}finally{
			BBBPerformanceMonitor
					.end("LinkCoRegProfileToRegServiceMarshaller-mapLinkRegistryToATGProfileRequest");
		}
		
		logDebug("LinkCoRegProfileToRegServiceMarshaller.mapGLinkRegistryToATGProfileRequest() method ends");

		return linkRegistryToATGProfile;
	}

}