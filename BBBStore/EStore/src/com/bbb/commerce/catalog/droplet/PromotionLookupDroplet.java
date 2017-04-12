package com.bbb.commerce.catalog.droplet;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.selfservice.droplet.ProcessCouponDroplet;

/**
 * This class will take promotionId and get the promotion details. 
 * @author ajosh8
 *
 */

public class PromotionLookupDroplet extends BBBDynamoServlet{

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

    String siteId = extractCurrentSiteId();
    
    if(StringUtils.isBlank(siteId)){
    	siteId = request.getParameter("siteId");
    }

    if(request.getLocalParameter(BBBCoreConstants.PROMOTION_ID) !=null){
      promotionId = (String)request.getLocalParameter(BBBCoreConstants.PROMOTION_ID);
      logDebug("Input Paramter SchoolId :"+promotionId);
      
    }else{
      logDebug("promotionId Id is Null");
      request.serviceParameter(BBBCoreConstants.EMPTY, request, response);

    }

    String promotionDetails = getPromTools().getPromotionCouponKey(promotionId, BBBCoreConstants.ACTIVATION_LABEL_ID, siteId, getLanguage(), false);
    if (StringUtils.isEmpty(promotionDetails)) {
      logDebug("Promotion dosn't belong to this site");
      request.serviceParameter(BBBCoreConstants.EMPTY, request, response);
    }
    else{
      request.setParameter(BBBCoreConstants.PROMTION_DETAILS, promotionDetails);
      request.serviceParameter(BBBCoreConstants.OUTPUT, request, response);
    }

    logDebug("Ending method PromotionLookupDroplet.Service");
  }


protected String extractCurrentSiteId() {
	return SiteContextManager.getCurrentSiteId();
}
}















