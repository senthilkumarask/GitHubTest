/*
 *
 * File  : RegCopyServiceUnMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegCopyResVO;
import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.RegCopyResponseDocument;
import com.bedbathandbeyond.www.RegCopyResponseDocument.RegCopyResponse;
import com.bedbathandbeyond.www.RegCopyReturn;
import com.bedbathandbeyond.www.Status;



/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author ssha53
 * 
 */
public class RegCopyServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		BBBPerformanceMonitor
				.start("RegCopyServiceMarshaller-processResponse");
		
		
		final RegCopyResVO regCopyResponseVO = new RegCopyResVO();
		
		try
		{
		
			RegCopyResponseDocument regDocRes = (RegCopyResponseDocument)responseDocument;
			RegCopyResponse regCopRes = regDocRes.getRegCopyResponse();
			
			if(!regCopRes.getRegCopyResult().getStatus().getErrorExists())
			{
				regCopyResponseVO.getServiceErrorVO().setErrorExists(false);
				
				RegCopyReturn  repReturn = regCopRes.getRegCopyResult();
				Status status = repReturn.getStatus();
				regCopyResponseVO.setSetRegCopyResult(!status.getErrorExists());				
				regCopyResponseVO.settotalNumOfItemsCopied((status.getNumberOfItemsAdded()));		
			} 
			else 
			{
				regCopyResponseVO.getServiceErrorVO().setErrorExists(true);
				regCopyResponseVO.getServiceErrorVO().setErrorId(regCopRes.getRegCopyResult().getStatus().getID());
				regCopyResponseVO.getServiceErrorVO().setErrorMessage(regCopRes.getRegCopyResult().getStatus().getErrorMessage());
				regCopyResponseVO.setWebServiceError(true);			
			}
		}
		catch (Exception e) {
			BBBPerformanceMonitor
			.end("RegCopyServiceMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1380,e.getMessage(), e);
		}finally{
				BBBPerformanceMonitor
				.end("RegCopyServiceMarshaller-processResponse");
		}
		return regCopyResponseVO;
	}
}
