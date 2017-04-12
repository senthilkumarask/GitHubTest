package com.bbb.framework.webservices.test.vo;

//XML exports
import org.apache.xmlbeans.XmlObject;

//BBB Exports
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;

//BBB Stubs imports
import com.bedbathandbeyond.www.GetCouponsDocument;
import com.bedbathandbeyond.www.GetCouponsDocument.GetCoupons;

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
		if(isLoggingDebug()){
			logDebug("Entry buildRequest of GetCouponsServiceMarshaller with ServiceRequestIF object:"+pReqVo.getServiceName());
		}
		BBBPerformanceMonitor.start("GetCouponsServiceMarshaller-buildRequest");
		GetCouponsDocument getCouponsDocument = null;		
		try{
			getCouponsDocument = GetCouponsDocument.Factory.newInstance();
			getCouponsDocument.setGetCoupons(buildGetCoupons((CouponRequestVo) pReqVo));			
		}catch (Exception e) {
			throw new BBBSystemException(e.getMessage(),e.getCause());
		}
		BBBPerformanceMonitor.end("GetCouponsServiceMarshaller-buildRequest");
		if(isLoggingDebug()){
			logDebug("Exit buildRequest of GetCouponsServiceMarshaller with XmlObject object:"+getCouponsDocument.getClass());
		}
		return getCouponsDocument;
	}
	
	/**
	 * The method generates the coupons request for the request. The method will set the request attributes to the getCoupons service
	 * @param reqVO
	 * @return
	 */
	private GetCoupons buildGetCoupons(CouponRequestVo pReqVO){
		
		if(isLoggingDebug()){
			logDebug("Entry buildGetCoupons of GetCouponsServiceMarshaller with CouponRequestVo object:"+pReqVO.getServiceName());
		}
		BBBPerformanceMonitor
		.start("GetCouponsServiceMarshaller-buildGetCoupons");
		
		if(isLoggingDebug()){
			logDebug("Building request object for getCoupons with userToken:"+pReqVO.getUserToken());
		}
		
		GetCoupons getCoupons = GetCoupons.Factory.newInstance();
		getCoupons.setUserToken(pReqVO.getUserToken());
		getCoupons.setEmailAddr(pReqVO.getEmailAddr());
		getCoupons.setSiteFlag(pReqVO.getSiteFlag());
		getCoupons.setUserToken(pReqVO.getUserToken());	
		
		
		BBBPerformanceMonitor
		.end("GetCouponsServiceMarshaller-buildGetCoupons");
		
		if(isLoggingDebug()){
			logDebug("Exit buildGetCoupons of GetCouponsServiceMarshaller with GetCoupons object:"+getCoupons.getClass());
		}
		
		return getCoupons;		
	}
	
	
}
