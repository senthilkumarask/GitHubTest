/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: dwaghmare
 *
 * Created on: 25-Feb-2015
 * --------------------------------------------------------------------------------
 */

package com.bbb.account;

//XML exports
import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

//BBB Exports
import com.bbb.account.vo.UpdateWalletProfileReqVo;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;

//BBB Stubs imports
import com.bedbathandbeyond.www.UpdateWalletProfileDocument;
import com.bedbathandbeyond.www.UpdateWalletProfileDocument.Factory;
import com.bedbathandbeyond.www.UpdateWalletProfileDocument.UpdateWalletProfile;

/**
 * The class is the marshaller class which takes the coupon requestVo and creates a XML request for the Webservice to call
 * The class will require a request VO object which will contain the request parameters for the XML request 
 * @author rravid
 *
 */
public class UpdateWalletProfileMarshaller extends RequestMarshaller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The method is extension of the RequestMarshaller service which will take the request Vo object and create a XML Object for the webservice
	 */
	public XmlObject buildRequest(ServiceRequestIF pReqVo)
	throws BBBBusinessException, BBBSystemException {
			logDebug("Entry buildRequest of UpdateWalletProfileMarshaller with ServiceRequestIF object:"+pReqVo.getServiceName());
		
		BBBPerformanceMonitor.start("UpdateWalletProfileMarshaller-buildRequest");
		
		UpdateWalletProfileDocument updateWalletProfileDocument = null;	

		try{
			updateWalletProfileDocument = UpdateWalletProfileDocument.Factory.newInstance();
			updateWalletProfileDocument.setUpdateWalletProfile(getDozerMappedRequest(pReqVo));			
		}finally{
			BBBPerformanceMonitor.end("UpdateWalletProfileMarshaller-buildRequest");
		}
		
		logDebug("Exit buildRequest of UpdateWalletProfileMarshaller with XmlObject object:"+updateWalletProfileDocument.getClass());
		return updateWalletProfileDocument;
	}
	
	
	/**
	 * This methos is used to map a page specific VO to web service VO.
	 * @throws BBBSystemException 
	 * 
	 */
	
	private UpdateWalletProfile getDozerMappedRequest(ServiceRequestIF reqVO) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("UpdateWalletProfileMarshaller-buildValidateAddressType");

		// GetCoupons3 = GetCoupons3.
		UpdateWalletProfile updateWalletProfile =UpdateWalletProfile.Factory.newInstance();
		//DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			updateWalletProfile.setSiteFlag(((UpdateWalletProfileReqVo)reqVO).getSiteFlag());
			updateWalletProfile.setUserToken(((UpdateWalletProfileReqVo)reqVO).getUserToken());
			updateWalletProfile.setEmailAddr(((UpdateWalletProfileReqVo)reqVO).getEmailAddr());
			updateWalletProfile.setMobilePhone(((UpdateWalletProfileReqVo)reqVO).getMobilePhone());
			updateWalletProfile.setWalletId(((UpdateWalletProfileReqVo)reqVO).getWalletId());
			updateWalletProfile.setFirstName(((UpdateWalletProfileReqVo)reqVO).getFirstName());
			updateWalletProfile.setLastName(((UpdateWalletProfileReqVo)reqVO).getLastName());
			updateWalletProfile.setAddress(((UpdateWalletProfileReqVo)reqVO).getAddress());
			updateWalletProfile.setCity(((UpdateWalletProfileReqVo)reqVO).getCity());
			updateWalletProfile.setState(((UpdateWalletProfileReqVo)reqVO).getState());
			updateWalletProfile.setPostalCode(((UpdateWalletProfileReqVo)reqVO).getPostalCode());
			
			//mapper.map(pReqVO, GetCoupons3);
		}
		catch(MappingException me)
		{
			logError(me.getMessage(), me);
			BBBPerformanceMonitor
			.end("UpdateWalletProfileMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("UpdateWalletProfileMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("UpdateWalletProfileMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("UpdateWalletProfileMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("UpdateWalletProfileMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		}
		finally
		{
			BBBPerformanceMonitor
				.end("UpdateWalletProfileMarshaller-buildValidateAddressType");
		}
		return updateWalletProfile;
	}
	
}
