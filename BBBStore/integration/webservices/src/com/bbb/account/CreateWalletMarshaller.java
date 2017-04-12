/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 26-December-2011
 * --------------------------------------------------------------------------------
 */

package com.bbb.account;

//XML exports
import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

//BBB Exports
import com.bbb.account.vo.CouponRequestVo;
import com.bbb.account.vo.CreateWalletReqVo;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.CreateWalletDocument;
import com.bedbathandbeyond.www.CreateWalletDocument.CreateWallet;



/**
 * The class is the marshaller class which takes the coupon requestVo and creates a XML request for the Webservice to call
 * The class will require a request VO object which will contain the request parameters for the XML request 
 * @author rravid
 *
 */
public class CreateWalletMarshaller extends RequestMarshaller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The method is extension of the RequestMarshaller service which will take the request Vo object and create a XML Object for the webservice
	 */
	public XmlObject buildRequest(ServiceRequestIF pReqVo)
	throws BBBBusinessException, BBBSystemException {
			logDebug("Entry buildRequest of CreatWalletMarshaller with ServiceRequestIF object:"+pReqVo.getServiceName());
		
		BBBPerformanceMonitor.start("CreatWalletMarshaller-buildRequest");
		
		CreateWalletDocument createWalletDocument = null;	

		try{
			createWalletDocument = CreateWalletDocument.Factory.newInstance();
			createWalletDocument.setCreateWallet(getDozerMappedRequest(pReqVo));			
		}finally{
			BBBPerformanceMonitor.end("CreateWalletMarshaller-buildRequest");
		}
		
		
			logDebug("Exit buildRequest of createWalletMarshaller with XmlObject object:"+createWalletDocument.getClass());
		return createWalletDocument;
	}
	
	
	/**
	 * This methos is used to map a page specific VO to web service VO.
	 * @throws BBBSystemException 
	 * 
	 */
	
	private CreateWallet getDozerMappedRequest(ServiceRequestIF reqVO) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("CreateWalletServiceMarshaller-buildValidateAddressType");

		CreateWallet createWallet =CreateWallet.Factory.newInstance();
		try
		{
			
			createWallet.setEmailAddr(((CreateWalletReqVo)reqVO).getEmailAddr());
			createWallet.setMobilePhone(((CreateWalletReqVo)reqVO).getMobilePhone());
			createWallet.setFirstName(((CreateWalletReqVo)reqVO).getFirstName());
			createWallet.setLastName(((CreateWalletReqVo)reqVO).getLastName());
			createWallet.setAddress(((CreateWalletReqVo)reqVO).getAddress());
			createWallet.setCity(((CreateWalletReqVo)reqVO).getCity());
			createWallet.setState(((CreateWalletReqVo)reqVO).getState());
			createWallet.setPostalCode(((CreateWalletReqVo)reqVO).getPostalCode());
			createWallet.setSiteFlag(((CreateWalletReqVo)reqVO).getSiteFlag());
			createWallet.setUserToken(((CreateWalletReqVo)reqVO).getUserToken());

		}
		catch(MappingException me)
		{
			logError(me.getMessage(), me);
			BBBPerformanceMonitor
			.end("createWalletMarshaller-buildValidateCreateWallet");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("createWalletMarshaller-buildValidateCreateWallet");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("createWalletMarshaller-buildValidateCreateWallet");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("createWalletMarshaller-buildValidateCreateWallet");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("createWalletMarshaller-buildValidateCreateWallet");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		}
		finally
		{
			BBBPerformanceMonitor
				.end("createWalletMarshaller-buildValidateCreateWallet");
		}
		return createWallet;
	}
	
}
