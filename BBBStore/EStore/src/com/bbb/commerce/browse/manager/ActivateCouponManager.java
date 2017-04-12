package com.bbb.commerce.browse.manager;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.account.validatecoupon.ActivateCouponRequestVO;
import com.bbb.account.validatecoupon.ActivateCouponResponseVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;




/**
 * @author nkum42
 * 
 * This manager is contains  all method which are related to product details . This manager get the data from catalog 
 *
 */
public class ActivateCouponManager extends BBBGenericService {
	
	public ActivateCouponResponseVO activateBigBlue(ActivateCouponRequestVO reqVO)
			throws ServletException, IOException, BBBBusinessException,
			BBBSystemException {
		ActivateCouponResponseVO resVO = null;
		String bigBlueToken = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCmsConstants.BIG_BLUE_TOKEN);
			if(!BBBUtility.isEmpty(bigBlueToken))
			{
				reqVO.setUserToken(bigBlueToken);
				logDebug("Big Blue token value :: " + bigBlueToken );
			}
			else
			{
				logError("Big Blue Token is null");
			}
			if (reqVO != null) {	
				BBBPerformanceMonitor.start(
						BBBPerformanceConstants.WEB_SERVICE_CALL, "activateCoupon");
					resVO = invokeServiceHandlerUtil(reqVO);
					BBBPerformanceMonitor.end(
							BBBPerformanceConstants.WEB_SERVICE_CALL,
							"activateCoupon");

				logDebug("WebService Response recieved Coupon Status ="
						+ resVO.getCouponStatus());
				//String resStatus = resVO.getCouponStatus();
				//ErrorStatus errorExist = resVO.getStatus();
				
			}

		return resVO;
	}

	/**
	 * @param reqVO
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected ActivateCouponResponseVO invokeServiceHandlerUtil(ActivateCouponRequestVO reqVO)
			throws BBBBusinessException, BBBSystemException {
		return (ActivateCouponResponseVO) ServiceHandlerUtil
				.invoke(reqVO);
	}
	
}