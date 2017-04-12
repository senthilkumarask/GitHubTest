/*
 *
 * File  : GetRegistryInfoServiceMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import atg.core.util.StringUtils;

import com.bbb.commerce.giftregistry.vo.RegistryReqVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.LinkCoRegToRegDocument;
import com.bedbathandbeyond.www.LinkCoRegToRegDocument.LinkCoRegToReg;

/**
 * 
 * This class contain methods used for marshalling the Link CoReg to Reg webservice request.
 * 
 * @author skalr2
 * 
 */
public class DoCoRegistryLinkingServiceMarshaller extends RequestMarshaller {

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

		logDebug("DoCoRegistryLinkingServiceMarshaller.buildRequest() method start");
		
		BBBPerformanceMonitor
				.start("DoCoRegistryLinkingServiceMarshaller-buildRequest");

		
		LinkCoRegToRegDocument linkCoRegToRegDocument = null;
		try {
			linkCoRegToRegDocument = LinkCoRegToRegDocument.Factory.newInstance();

			linkCoRegToRegDocument.setLinkCoRegToReg(mapLinkCoRegToRegRequest(pRreqVO));
		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("DoCoRegistryLinkingServiceMarshaller-buildRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1363,e.getMessage(), e);
		}

		BBBPerformanceMonitor
				.end("DoCoRegistryLinkingServiceMarshaller-buildRequest");
		
		logDebug("DoCoRegistryLinkingServiceMarshaller.buildRequest() method ends");

		return linkCoRegToRegDocument;
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
	private LinkCoRegToReg mapLinkCoRegToRegRequest(
			ServiceRequestIF pRreqVO) throws BBBSystemException {

		logDebug("DoCoRegistryLinkingServiceMarshaller.mapLinkCoRegToRegRequest() method start");
		
		BBBPerformanceMonitor
				.start("DoCoRegistryLinkingServiceMarshaller-mapLinkCoRegToRegRequest");

		LinkCoRegToReg linkCoRegToReg = LinkCoRegToReg.Factory.newInstance();
		//DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			//mapper.map(pRreqVO, linkCoRegToReg);
			linkCoRegToReg.setUserToken(((RegistryReqVO)pRreqVO).getUserToken());
			linkCoRegToReg.setSiteFlag(((RegistryReqVO)pRreqVO).getSiteId());
			String regiNum=((RegistryReqVO)pRreqVO).getRegistryId();
			if(!StringUtils.isEmpty(regiNum)){
				linkCoRegToReg.setRegistryNum(Long.parseLong(((RegistryReqVO)pRreqVO).getRegistryId()));	
			}
			else
			{
				linkCoRegToReg.setRegistryNum(-1);
			}
			
			linkCoRegToReg.setProfileID(((RegistryReqVO)pRreqVO).getProfileId());
		}catch(MappingException me) {
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10144 +" MappingException from mapLinkCoRegToRegRequest from DoCoRegistryLinkingServiceMarshaller",me);
			BBBPerformanceMonitor
			.end("DoCoRegistryLinkingServiceMarshaller-mapLinkCoRegToRegRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1363,me.getMessage(), me);
			
		}finally{
			BBBPerformanceMonitor
					.end("DoCoRegistryLinkingServiceMarshaller-mapLinkCoRegToRegRequest");
		}
		
		
		
		
		logDebug("DoCoRegistryLinkingServiceMarshaller.mapGLinkCoRegToRegRequest() method ends");

		return linkCoRegToReg;
	}

}