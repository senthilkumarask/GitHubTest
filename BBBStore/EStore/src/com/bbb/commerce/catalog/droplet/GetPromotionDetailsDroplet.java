package com.bbb.commerce.catalog.droplet;

import java.util.Map;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.droplet.ProcessCouponDroplet;
import com.bbb.utils.BBBUtility;

/**
 * This class will take promotionId and get the promotion details. 
 * @author ajosh8
 *
 */

public class GetPromotionDetailsDroplet extends BBBDynamoServlet{

  private ProcessCouponDroplet processCouponDroplet;


  private String language;
  private BBBPromotionTools mPromTools;


  /**
 * @return the promTools
 */
public BBBPromotionTools getPromTools() {
	return mPromTools;
}


/**
 * @param pPromTools the promTools to set
 */
public void setPromTools(BBBPromotionTools pPromTools) {
	mPromTools = pPromTools;
}


  /**
   * @return the language
   */
  public String getLanguage() {
    return language;
  }


  /**
   * @param pLanguage the language to set
   */
  public void setLanguage(String pLanguage) {
    language = pLanguage;
  }


  /**
   * @return the processCouponDroplet
   */
  public ProcessCouponDroplet getProcessCouponDroplet() {
    return processCouponDroplet;
  }


  /**
   * @param pProcessCouponDroplet the processCouponDroplet to set
   */
  public void setProcessCouponDroplet(ProcessCouponDroplet pProcessCouponDroplet) {
    processCouponDroplet = pProcessCouponDroplet;
  }

/**
 *This method will call the ProcessCouponDroplet to get the promotion details. 
 */
  public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
      throws javax.servlet.ServletException, java.io.IOException {

    logDebug("starting method PromotionLookupDroplet.Service");
    String promotionId=null;
    String couponId=null;
    if(request.getLocalParameter(BBBCoreConstants.PROMOTION_ID) !=null){
      promotionId = (String)request.getLocalParameter(BBBCoreConstants.PROMOTION_ID);
      logDebug("Input Paramter SchoolId :"+promotionId);
      
    }else{

      logDebug("promotionId Id is Null");
      
      request.serviceParameter(BBBCoreConstants.EMPTY, request, response);

    }
    if(request.getLocalParameter(BBBCoreConstants.COUPON_ID) !=null){
    	couponId = (String)request.getLocalParameter(BBBCoreConstants.COUPON_ID);
        logDebug("Input Paramter COUPON_ID :"+couponId);
        
      }else{

        logDebug("couponId Id is Null");
        
        request.serviceParameter(BBBCoreConstants.EMPTY, request, response);

      }

    RepositoryItem promotion = getPromTools().getPromotionById(promotionId);
    if (promotion==null) {
        logDebug("Promotion dosn't belong to this site");
      request.serviceParameter(BBBCoreConstants.EMPTY, request, response);
    }
    else{
    	Map mediaMap = (Map) promotion.getPropertyValue(BBBCoreConstants.MEDIA);
    	if(mediaMap != null){

			RepositoryItem mainImgItem = (RepositoryItem) mediaMap.get("mainImage");
			if(mainImgItem!=null){
			String mainImgUrl = (String) mainImgItem.getPropertyValue(BBBCoreConstants.URL);
			request.setParameter("mainImgUrl", mainImgUrl);
			}
			RepositoryItem lrgImgItem = (RepositoryItem) mediaMap.get("large");
			if(lrgImgItem!=null){
			String lrgImgUrl = (String) lrgImgItem.getPropertyValue(BBBCoreConstants.URL);			
			request.setParameter("lrgImgUrl", lrgImgUrl);
			}
			
    	}
		String promoDesription = (String)promotion.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
		String siteId = null;
		
		if(null != ServletUtil.getCurrentRequest()) {
            siteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
		}

		if (BBBUtility.isEmpty(siteId)){
            siteId = SiteContextManager.getCurrentSiteId();
		}
		
		String tAndc = null;
		try {
			tAndc = this.getPromTools().fetchExclusionText((String) couponId, siteId);
			/*tAndc = this.getPromTools().fetchExclusionText((String) promotion.getPropertyValue(BBBCheckoutConstants.BBBCOUPONS), siteId);*/
		} catch (BBBSystemException e) {			
				logError(LogMessageFormatter.formatMessage(request, "err_promotion_details_error" , BBBCoreErrorConstants.ACCOUNT_ERROR_1118),e);
				request.setParameter("systemerror", "err_mycoupons_system_error");
				request.serviceLocalParameter("error", request, response);
		}
		
		
		request.setParameter("promoDesription", promoDesription);
		request.setParameter("tAndc", tAndc);
		
      request.serviceParameter(BBBCoreConstants.OUTPUT, request, response);
    }

  
     logDebug("Ending method PromotionLookupDroplet.Service");
  
  }
}















