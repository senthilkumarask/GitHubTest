package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.MappingException;

import com.bbb.commerce.giftregistry.vo.RegistryReqVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.LinkCoRegProfileToRegistryDocument;
import com.bedbathandbeyond.www.LinkCoRegProfileToRegistryDocument.LinkCoRegProfileToRegistry;

/**
 * 
 * This class contain methods used for marshalling the Link CoReg Profile to Reg webservice request.
 * 
 * @author ssha53
 * 
 */
public class LinkCoRegProfileToRegServiceMarshaller extends RequestMarshaller {

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

		logDebug("LinkCoRegProfileToRegServiceMarshaller.buildRequest() method start");
		
		BBBPerformanceMonitor
				.start("LinkCoRegProfileToRegServiceMarshaller-buildRequest");

		LinkCoRegProfileToRegistryDocument linkCoRegProfileToRegDocument = null;
		try {
			linkCoRegProfileToRegDocument = LinkCoRegProfileToRegistryDocument.Factory.newInstance();

			linkCoRegProfileToRegDocument.setLinkCoRegProfileToRegistry(mapLinkCoRegToRegRequest(pRreqVO));
		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("LinkCoRegProfileToRegServiceMarshaller-buildRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1377,e.getMessage(), e);
		}finally{
		BBBPerformanceMonitor
				.end("LinkCoRegProfileToRegServiceMarshaller-buildRequest");
		}
		
		logDebug("LinkCoRegProfileToRegServiceMarshaller.buildRequest() method ends");

		return linkCoRegProfileToRegDocument;
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
	private LinkCoRegProfileToRegistry mapLinkCoRegToRegRequest(
			ServiceRequestIF pRreqVO) throws BBBSystemException {

		logDebug("LinkCoRegProfileToRegServiceMarshaller.mapLinkCoRegToRegRequest() method start");
		
		BBBPerformanceMonitor
				.start("LinkCoRegProfileToRegServiceMarshaller-mapLinkCoRegToRegRequest");

		
		LinkCoRegProfileToRegistry linkCoRegProfileToReg = LinkCoRegProfileToRegistry.Factory.newInstance();
		try {
			linkCoRegProfileToReg.setUserToken(((RegistryReqVO)pRreqVO).getUserToken());
			linkCoRegProfileToReg.setSiteFlag(((RegistryReqVO)pRreqVO).getSiteId());
			linkCoRegProfileToReg.setEmail(((RegistryReqVO)pRreqVO).getEmailId());	
			linkCoRegProfileToReg.setProfileID(((RegistryReqVO)pRreqVO).getProfileId());
			
		}catch(MappingException me) {
			BBBPerformanceMonitor
			.end("LinkCoRegProfileToRegServiceMarshaller-mapLinkCoRegToRegRequest");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10153 +" MappingException from mapLinkCoRegToRegRequest from LinkCoRegProfileToRegServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1377,me.getMessage(), me);
			
		}finally{
			BBBPerformanceMonitor
					.end("LinkCoRegProfileToRegServiceMarshaller-mapLinkCoRegToRegRequest");
		}
		
		logDebug("LinkCoRegProfileToRegServiceMarshaller.mapGLinkCoRegToRegRequest() method ends");

		return linkCoRegProfileToReg;
	}

}