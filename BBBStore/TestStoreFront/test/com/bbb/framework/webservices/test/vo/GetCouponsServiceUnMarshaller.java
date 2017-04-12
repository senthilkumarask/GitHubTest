package com.bbb.framework.webservices.test.vo;

//Java Util Imports
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import com.bbb.account.vo.CouponListVo;
import com.bbb.account.vo.CouponResponseVo;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.ArrayOfCoupon;
import com.bedbathandbeyond.www.Coupon;
import com.bedbathandbeyond.www.GetCouponsResponseDocument;
import com.bedbathandbeyond.www.GetCouponsResponseDocument.GetCouponsResponse;
import com.bedbathandbeyond.www.GetCouponsReturn;
import com.bedbathandbeyond.www.Status;

public class GetCouponsServiceUnMarshaller extends ResponseUnMarshaller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)throws BBBSystemException {
		if(isLoggingDebug()){
			logDebug("Entry processResponse with XmlObject:"+pResponseDocument.getClass());
		}
		BBBPerformanceMonitor.start("GetCouponsServiceUnmarshaller-processResponse");
		CouponResponseVo couponsResponseVo = new CouponResponseVo();
		if (pResponseDocument != null) {
			if(isLoggingDebug()){
				logDebug("Response document is not null");
			}
			try {
				GetCouponsResponseDocument getCouponResponseDocument = (GetCouponsResponseDocument)pResponseDocument;
				GetCouponsResponse getCouponResponse = getCouponResponseDocument.getGetCouponsResponse();
				GetCouponsReturn couponReturn = getCouponResponse.getGetCouponsResult();				
				Status status = couponReturn.getStatus();
				if (null != status && status.getErrorExists()) {
					if(isLoggingDebug()){
						logDebug("Status is not null and creating array of coupons:");
					}
					ArrayOfCoupon arrayOfCoupons = couponReturn.getCoupons();
					if(null != arrayOfCoupons){
						Coupon[] coupons = arrayOfCoupons.getCouponArray();
						if( null != coupons && coupons.length > 0){
							List<CouponListVo> couponVo = new ArrayList<CouponListVo>(coupons.length);	
							for(Coupon coupon : coupons){
								if(isLoggingDebug()){
									logDebug("adding coupon array into coupon list with entryCd:"+coupon.getEntryCd());
								}
								CouponListVo couponList = new CouponListVo();
								couponList.setEntryCd(coupon.getEntryCd());
								couponList.setDescription(coupon.getDescription());
								couponVo.add(couponList);
							}
							couponsResponseVo.setCouponList(couponVo);
						}
					}					
				}
			} catch (Exception e) {
				throw new BBBSystemException(e.getMessage(), e.getCause());
			}
		}
		if(isLoggingDebug()){
			logDebug("Exit processResponse with ServiceResponseIF return:"+couponsResponseVo.getClass());
		}
		BBBPerformanceMonitor.end("GetCouponsServiceUnmarshaller-processResponse");
		return couponsResponseVo;
	}
}