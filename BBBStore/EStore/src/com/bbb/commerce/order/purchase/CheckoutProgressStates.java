
package com.bbb.commerce.order.purchase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.multisite.SiteContextManager;
import atg.nucleus.GenericService;
import atg.userprofiling.Profile;

/**
 * 
 */
public class CheckoutProgressStates extends GenericService {
  /**
   * Class version
   */
  public final static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/order/purchase/CheckoutProgressStates.java#2 $$Change: 633752 $";

  private String mCurrentLevel = DEFAULT_STATES.CART.toString();
  private Boolean isTBSOrderSubmitted;
  
  private Map<String, Integer> mCheckoutProgressLevels = new HashMap<String, Integer>();
  private Map<String, String> mCheckoutSuccessURLs = new HashMap<String, String>();
  private Map<String, String> mCheckoutFailureURLs = new HashMap<String, String>();

  private Map<String, String> mCheckoutUnAuthSuccessURLs = new HashMap<String, String>();
  private com.bbb.commerce.checkout.manager.BBBCheckoutManager checkoutManager;
	private BBBCatalogTools catalogTools;

  
  public String getCurrentLevel()
  {
    return mCurrentLevel;
  }

  public void setCurrentLevel(String pCurrentLevel)
  {
    mCurrentLevel = pCurrentLevel;
  }
  
  /**
   * Success UnAuth URL of currentLevel property 
   */
  private String getCurrentUnAuthSuccessURL()
  {
    String successURL = mCheckoutUnAuthSuccessURLs.get(mCurrentLevel);
    return successURL;
  }
  
  /**
   * Success URL of currentLevel property 
   */
  private String getCurrentSuccessURL()
  {
    String successURL = mCheckoutSuccessURLs.get(mCurrentLevel);
    return successURL;
  }
  
  /**
   * Failure URL of currentLevel property 
   */
  public String getCurrentFailureURL()
  {
    String successURL = mCheckoutFailureURLs.get(mCurrentLevel);
    return successURL;
  }
  
  /**
   * Numeric representation of currentLevel property 
   */
  public int getCurrentLevelAsInt()
  {
    Integer currentLevel = mCheckoutProgressLevels.get(mCurrentLevel);
    return currentLevel == null ? 0 : currentLevel;
  }
  
  /**
   * checoutProgressLevels property contains checkout milestones available for user. These milestones are stored in form of map,
   * linking milestone name with it's level int value.
   * Milestones with lower level are available for displaying to user.
   */
  public Map<String, Integer> getCheckoutProgressLevels()
  {
    return mCheckoutProgressLevels;
  }
  
  public void setCheckoutProgressLevels(@SuppressWarnings("rawtypes") Map pCheckoutProgressLevels)
  {
    mCheckoutProgressLevels.clear();
    if (pCheckoutProgressLevels == null)
    {
      return;
    }
    for (Object entryObj: pCheckoutProgressLevels.entrySet())
    {
      @SuppressWarnings("rawtypes")
      Map.Entry entry = (Map.Entry)entryObj;
      mCheckoutProgressLevels.put(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString()));
    }
  }
  
    public Map<String, String> getCheckoutSuccessURLs() {
       return mCheckoutSuccessURLs;
    }
    
    public void setCheckoutSuccessURLs(Map<String, String> pCheckoutSuccessURLs) {
        this.mCheckoutSuccessURLs = pCheckoutSuccessURLs;
    }
    
    public Map<String, String> getCheckoutUnAuthSuccessURLs() {
        return mCheckoutUnAuthSuccessURLs;
     }
     
     public void setCheckoutUnAuthSuccessURLs(Map<String, String> pCheckoutUnAuthSuccessURLs) {
         this.mCheckoutUnAuthSuccessURLs = pCheckoutUnAuthSuccessURLs;
     }
    
    public Map<String, String> getCheckoutFailureURLs() {
        return mCheckoutFailureURLs;
    }
    
    public void setCheckoutFailureURLs(Map<String, String> pCheckoutFailureURLs) {
        this.mCheckoutFailureURLs = pCheckoutFailureURLs;
    }
    
    public String getSuccessURL(Profile profile) {
        if(profile.isTransient() && getCurrentUnAuthSuccessURL() != null) {
            return getCurrentUnAuthSuccessURL();
        } else {
            return getCurrentSuccessURL();
        }
    }
    
    public String getFailureURL() {
        return getCurrentFailureURL();
    }
    
    /**
	 * @return the isTBSOrderSubmitted
	 */
	public Boolean getIsTBSOrderSubmitted() {
		return isTBSOrderSubmitted;
	}

	/**
	 * @param isTBSOrderSubmitted the isTBSOrderSubmitted to set
	 */
	public void setIsTBSOrderSubmitted(Boolean isTBSOrderSubmitted) {
		this.isTBSOrderSubmitted = isTBSOrderSubmitted;
	}

	public static enum DEFAULT_STATES
    {
        CART, GUEST, SHIPPING_SINGLE, SHIPPING_MULTIPLE, BILLING, COUPONS, PAYMENT, REVIEW, GIFT, ERROR , VALIDATION, INTERMEDIATE_PAYPAL, SP_GUEST, SP_SHIPPING_SINGLE, SP_SHIPPING_MULTIPLE, SP_BILLING, SP_COUPONS, SP_PAYMENT, SP_REVIEW, SP_GIFT, SP_ERROR, SP_CHECKOUT_SINGLE, SP_PAYPAL;
    }
	
	 public final BBBCatalogTools getCatalogUtil() {
	        return this.catalogTools;
	    }

	    /** @param catalogUtil */
	    public final void setCatalogUtil(final BBBCatalogTools catalogUtil) {
	        this.catalogTools = catalogUtil;
	    }
	    public final BBBCheckoutManager getCheckoutManager() {
	        return this.checkoutManager;
	    }

	    /** @param pCheckoutManager */
	    public final void setCheckoutManager(final BBBCheckoutManager pCheckoutManager) {
	        this.checkoutManager = pCheckoutManager;
	    }
	
	//Seeing if customer needs to go to single page checkout 
   public boolean spcEligible(BBBOrderImpl order , boolean isSinglePageCheckout){
	   
		String siteId = SiteContextManager.getCurrentSiteId();
		if (siteId != null && siteId.startsWith(TBSConstants.TBS_PREFIX)) {
			return false;
		}
    	boolean spcEligible=false;
    	boolean spcBCCFlag = false;
    	boolean spcSiteSpectFlag = false;
		List<String> spcBCCFlagList;
		List<String> spcSiteSpectFlagList;
		try {
			spcBCCFlagList = this.catalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn");
			spcSiteSpectFlagList = this.catalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect");
			if (spcBCCFlagList != null && !spcBCCFlagList.isEmpty()) {
				spcBCCFlag = Boolean.parseBoolean(spcBCCFlagList.get(0));
			}
			if (spcSiteSpectFlagList != null && !spcSiteSpectFlagList.isEmpty()) {
				spcSiteSpectFlag = Boolean.parseBoolean(spcSiteSpectFlagList.get(0));
			}
		} catch (BBBSystemException e1) {
			logError("Error occured in BBBSystemException block  : " + e1);
		} catch (BBBBusinessException e1) {
			
			logError("Error occured in BBBBusinessException block : " + e1);
		}
		
		boolean ordercContainsLTLItem=false;
    	boolean result ;
    	
    	int shippingGrpCnt = order.getShippingGroupCount();
    	// second argument is false - as no need to check for single page functionality for mobile
    	result =getCheckoutManager().displaySingleShipping(order, false);
    	
    	try {	
			ordercContainsLTLItem=getCheckoutManager().orderContainsLTLItem(order);
    	}catch (BBBSystemException e) {
    		logError("Error occured in BBBSystemException block : " + e);
			
		} catch (BBBBusinessException e) {
			logError("Error occured in BBBBusinessException block : " + e);
		}
    	if(spcBCCFlag && shippingGrpCnt==1 && result && !ordercContainsLTLItem){
    		if((spcSiteSpectFlag  && isSinglePageCheckout ) || !spcSiteSpectFlag ){
    			spcEligible=true;
    		}
    	}
		
		
		return spcEligible;
    }
}
