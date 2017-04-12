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

//Java Util Imports
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;

//
import org.apache.xmlbeans.XmlObject;




//BBB Imports
import com.bbb.account.vo.CouponListVo;
import com.bbb.account.vo.CouponResponseVo;
import com.bbb.account.vo.RedemptionCodeVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;

//Stub Imports
import com.bedbathandbeyond.www.ArrayOfCoupon3;
import com.bedbathandbeyond.www.ArrayOfRedemptionCode;
//import com.bedbathandbeyond.www.ArrayOfRedemptionCodes;
import com.bedbathandbeyond.www.Coupon3;
import com.bedbathandbeyond.www.GetCoupons3ResponseDocument;
import com.bedbathandbeyond.www.GetCoupons3ResponseDocument.GetCoupons3Response;
import com.bedbathandbeyond.www.GetCouponsReturn3;
import com.bedbathandbeyond.www.RedemptionCode;
//import com.bedbathandbeyond.www.RedemptionCodes;
import com.bedbathandbeyond.www.Status;

public class GetCouponsServiceUnMarshaller extends ResponseUnMarshaller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ServiceResponseIF processResponse(XmlObject pResponseDocument)throws BBBSystemException {
			
			CouponResponseVo couponsResponseVo = null;
		try
		{
			if(pResponseDocument != null){
				
				logDebug("Entry processResponse with XmlObject:"+pResponseDocument.getClass());
		        BBBPerformanceMonitor.start("GetCouponsServiceUnmarshaller-processResponse");
				final GetCoupons3Response cpnRes = ((GetCoupons3ResponseDocument) pResponseDocument).getGetCoupons3Response();
				if (cpnRes != null) {
					
					final GetCouponsReturn3 couponsReturn = cpnRes.getGetCoupons3Result();
				
					
			
					if (!couponsReturn.getStatus().getErrorExists()) {
						couponsResponseVo = (CouponResponseVo) getDozerMappedResponse(couponsReturn);
					} 
				else {
					couponsResponseVo = new CouponResponseVo();
					final Status status = couponsReturn.getStatus();
				
					final ErrorStatus errorStatus = getDozerMappedError(status);
			
					couponsResponseVo.setErrorStatus(errorStatus);
					couponsResponseVo.setWebServiceError(true);
					}
				}
			}
		} 
		finally
		{
			BBBPerformanceMonitor
				.end("GetCouponsServiceUnmarshaller-processResponse");
		}
		return couponsResponseVo;
	}
	
	
	
	private ServiceResponseIF getDozerMappedResponse(final GetCouponsReturn3 couponsReturn ) throws BBBSystemException {

		
		BBBPerformanceMonitor
				.start("GetCouponsServiceUnMarshaller-getDozerMappedResponse");

		final CouponResponseVo couponResponseVo = new CouponResponseVo();
		List<CouponListVo> couponList =new ArrayList<CouponListVo>();
		if(couponsReturn.getCoupons3()!=null){
			
		ArrayOfCoupon3 getCouponsList = couponsReturn.getCoupons3();
		Coupon3[] coupons3List= getCouponsList.getCoupon3Array();
		
		for(Coupon3 coupon : coupons3List){
			
			CouponListVo couponVO =new CouponListVo();
			List<RedemptionCodeVO> redemptionCodesVO=new ArrayList<RedemptionCodeVO>();
			couponVO.setEntryCd(coupon.getEntryCd());
			couponVO.setRedemptionChannel(coupon.getRedemptionChannel());
			couponVO.setExpiryDate(coupon.getExpirationDate());
			couponVO.setLastRedemptionDate(coupon.getLastRedemptionDate());
			couponVO.setIssueDate(coupon.getIssueDate());
			couponVO.setDescription(coupon.getDescription());
			couponVO.setUniqueCouponCd(coupon.getUniqueCouponCd());
			couponVO.setCouponsExclusions(coupon.getCouponExclusionsMessage());
			couponVO.setCouponExclusionsMessage(coupon.getCouponExclusionsMessage());
			couponVO.setRedemptionCount(coupon.getRedemptionCount());
			couponVO.setRedemptionLimit(coupon.getRedemptionLimit());
			ArrayOfRedemptionCode redemptionCodesArray =  coupon.getRedemptionCodes();
			if(redemptionCodesArray!=null && !redemptionCodesArray.isNil()){
			RedemptionCode[] redemptionCodes = redemptionCodesArray.getRedemptionCodeArray();
			for(RedemptionCode redemptionCode: redemptionCodes){
			RedemptionCodeVO redemptionCodeVO =new RedemptionCodeVO();
			redemptionCodeVO.setCouponImageURL(redemptionCode.getCouponImageURL());
			redemptionCodeVO.setDisplayFlag(redemptionCode.getDisplayFlag());
			redemptionCodeVO.setEnforcedExpirationFlag(redemptionCode.getEnforcedExpirationFlag());
			redemptionCodeVO.setOnlineOfferCode(redemptionCode.getOnlineOfferCode());
			redemptionCodeVO.setpOSCouponID(redemptionCode.getPOSCouponID());
			redemptionCodeVO.setPrintableFlag(redemptionCode.getPrintableFlag());
			redemptionCodeVO.setRedemptionCodeChannel(redemptionCode.getRedemptionCodeChannel());
			redemptionCodeVO.setUniqueCouponCd(redemptionCode.getUniqueCouponCd());
			redemptionCodesVO.add(redemptionCodeVO);
			}
			couponVO.setRedemptionCodesVO(redemptionCodesVO);
			}
			couponList.add(couponVO);
		}
		couponResponseVo.setCouponList(couponList);
		}
		final Status status = couponsReturn.getStatus();
		
		final ErrorStatus errorStatus = getDozerMappedError(status);
		
		couponResponseVo.setErrorStatus(errorStatus);
		couponResponseVo.setWebServiceError(false);
		
		return couponResponseVo;
	}
	
	@SuppressWarnings("unchecked")
	private ErrorStatus getDozerMappedError(final Status status) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("GetCouponsServiceUnMarshaller-getDozerMappedError");

		final ErrorStatus errorStatus= new ErrorStatus();
		
		//final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			errorStatus.setDisplayMessage(status.getDisplayMessage());
			errorStatus.setErrorExists(status.getErrorExists());
			errorStatus.setErrorId(status.getID());
			errorStatus.setErrorMessage(status.getErrorMessage());
			errorStatus.setValidationErrors((List<ValidationError>) status.getValidationErrors());
		}
		
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1341,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1341,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1341,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1341,e.getMessage(), e);
		}
		finally
		{
			BBBPerformanceMonitor
				.end("GetCouponsServiceUnMarshaller-getDozerMappedError");
		}

		return errorStatus;
	}

	
	
}