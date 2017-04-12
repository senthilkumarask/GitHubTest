/*
 *
 * File  : GetRegistryInfoServiceUnMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.utils.BBBUtility;
import com.bedbathandbeyond.www.GetRegistryInfoResponseDocument;
import com.bedbathandbeyond.www.GetRegistryInfoResponseDocument.GetRegistryInfoResponse;


/**
 * This class contain methods used for unmarshalling the get registry info webservice response.
 * 
 * @author ssha53
 * 
 */
public class GetRegistryInfoServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		logDebug("GetRegistryInfoServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("GetRegistryInfoServiceUnMarshaller-processResponse");

		final RegistryResVO registryResVO = new RegistryResVO();
		
		if (responseDocument != null) {

			try {
				
				final GetRegistryInfoResponse regInfoRes = ((GetRegistryInfoResponseDocument) responseDocument)
						.getGetRegistryInfoResponse();

				if (regInfoRes != null) {
					if (!regInfoRes.getGetRegistryInfoResult().
							getStatus().getErrorExists()) {
						
						final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
						try {
							mapper.map(regInfoRes, registryResVO);
							int remainingGift=0;

							remainingGift=(registryResVO.getRegistrySummaryVO().getGiftRegistered())
							- (registryResVO.getRegistrySummaryVO().getGiftPurchased());
							if (remainingGift<0)
							{
								remainingGift=0;
							}
							registryResVO.getRegistrySummaryVO().setGiftRemaining(remainingGift);
							registryResVO.getRegistrySummaryVO().setFutureShippingDate(BBBUtility.convertDateWSToAppFormat(registryResVO.getRegistrySummaryVO().getFutureShippingDate()));
							final String reNusDecoTheme=registryResVO.getRegistryVO().getEvent().getBabyNurseryTheme();
							if (null!=reNusDecoTheme&&reNusDecoTheme.length()>1)
							{
							registryResVO.getRegistryVO().getEvent().setBabyNurseryTheme(BBBUtility.DecodeNurseryDecorTheme(reNusDecoTheme));
							}

						} catch (MappingException me) {
							logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10149 +" Exception from processResponse from GetRegistryInfoServiceUnMarshaller",me);
							BBBPerformanceMonitor
							.end("GetRegistryInfoServiceUnMarshaller-processResponse");
							throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1372,me.getMessage(), me);
						}
					} else {

						registryResVO.getServiceErrorVO()
								.setErrorExists(true);
						registryResVO.getServiceErrorVO().setErrorId(
								regInfoRes.getGetRegistryInfoResult().getStatus().getID());
						registryResVO.getServiceErrorVO()
								.setErrorMessage(regInfoRes
												.getGetRegistryInfoResult().getStatus().getErrorMessage());
						registryResVO.setWebServiceError(true);
					}

				}
			} catch (Exception e) {
				BBBPerformanceMonitor
				.end("GetRegistryInfoServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1372,e.getMessage(), e);
			}			
			
			BBBPerformanceMonitor
					.end("GetRegistryInfoServiceUnMarshaller-processResponse");

			logDebug("GetRegistryInfoServiceUnMarshaller.processResponse() method ends");

			return registryResVO;
		}
		return registryResVO;
	}

}
