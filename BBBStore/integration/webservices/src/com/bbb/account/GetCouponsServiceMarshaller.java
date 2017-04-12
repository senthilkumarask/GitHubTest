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
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;

//BBB Stubs imports
import com.bedbathandbeyond.www.GetCoupons3Document;
import com.bedbathandbeyond.www.GetCoupons3Document.Factory;
import com.bedbathandbeyond.www.GetCoupons3Document.GetCoupons3;

/**
 * The class is the marshaller class which takes the coupon requestVo and creates a XML request for the Webservice to call
 * The class will require a request VO object which will contain the request parameters for the XML request 
 * @author rravid
 *
 */
public class GetCouponsServiceMarshaller extends RequestMarshaller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The method is extension of the RequestMarshaller service which will take the request Vo object and create a XML Object for the webservice
	 */
	public XmlObject buildRequest(ServiceRequestIF pReqVo)
	throws BBBBusinessException, BBBSystemException {
			logDebug("Entry buildRequest of GetCouponsServiceMarshaller with ServiceRequestIF object:"+pReqVo.getServiceName());
		
		BBBPerformanceMonitor.start("GetCouponsServiceMarshaller-buildRequest");
		
		GetCoupons3Document getCoupons3Document = null;	

		try{
			getCoupons3Document = GetCoupons3Document.Factory.newInstance();
			getCoupons3Document.setGetCoupons3(getDozerMappedRequest(pReqVo));			
		}finally{
			BBBPerformanceMonitor.end("GetCouponsServiceMarshaller-buildRequest");
		}
		
		
			logDebug("Exit buildRequest of GetCouponsServiceMarshaller with XmlObject object:"+getCoupons3Document.getClass());
		return getCoupons3Document;
	}
	
	
	/**
	 * This methos is used to map a page specific VO to web service VO.
	 * @throws BBBSystemException 
	 * 
	 */
	
	private GetCoupons3 getDozerMappedRequest(ServiceRequestIF reqVO) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("GetCouponsServiceMarshaller-buildValidateAddressType");

		// GetCoupons3 = GetCoupons3.
		GetCoupons3 getCoupons3 =GetCoupons3.Factory.newInstance();
		//DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			
			getCoupons3.setEmailAddr(((CouponRequestVo)reqVO).getEmailAddr());
			getCoupons3.setMobilePhone(((CouponRequestVo)reqVO).getMobilePhone());
			getCoupons3.setWalletId(((CouponRequestVo)reqVO).getWalletId());
			getCoupons3.setShowOnlyActive(((CouponRequestVo)reqVO).getShowActiveOnly());
			getCoupons3.setSiteFlag(((CouponRequestVo)reqVO).getSiteFlag());
			getCoupons3.setUserToken(((CouponRequestVo)reqVO).getUserToken());
			
			//mapper.map(pReqVO, GetCoupons3);
		}
		catch(MappingException me)
		{
			logError(me.getMessage(), me);
			BBBPerformanceMonitor
			.end("GetCouponsServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetCouponsServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetCouponsServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetCouponsServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetCouponsServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1340,e.getMessage(), e);
		}
		finally
		{
			BBBPerformanceMonitor
				.end("GetCouponsServiceMarshaller-buildValidateAddressType");
		}
		return getCoupons3;
	}
	
}
