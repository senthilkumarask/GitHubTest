package com.bbb.commerce.order.manager;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.pricing.PricingException;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.StringUtils;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBGetCouponsManager;
import com.bbb.account.vo.CouponListVo;
import com.bbb.account.vo.CouponResponseVo;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.checkout.util.BBBCouponUtil;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * The Promotion lookup manager will get promotions for list of coupons 
 * returned from coupon manager
 * This class also validated promotions for site and dates
 * @author nagarg
 *
 */

public class PromotionLookupManager extends BBBGenericService {
    private BBBCatalogToolsImpl mCatalogTools;
    private BBBGetCouponsManager mCouponManager;
    private PromotionTools mPromotionTools;
    private BBBCouponUtil couponUtil;
    
    /**
	 * @return the couponUtil
	 */
	public BBBCouponUtil getCouponUtil() {
		return this.couponUtil;
	}

	/**
	 * @param couponUtil the couponUtil to set
	 */
	public void setCouponUtil(BBBCouponUtil couponUtil) {
		this.couponUtil = couponUtil;
	}
	
	
	public List<CouponListVo> getCouponList(Profile profile, BBBOrderImpl order,
            String site, boolean isFromBillingPaymentPage) throws BBBSystemException, BBBBusinessException {
       // HashMap<String, RepositoryItem> results = new HashMap<String, RepositoryItem>();
        String email  = null;         
        String  walletId= null;
        BBBRepositoryContactInfo billingAddress = order.getBillingAddress();
        if(billingAddress != null && profile.isTransient() ) {
            email = billingAddress.getEmail();            
        }
        if(!profile.isTransient()) {
            email = (String) profile.getPropertyValue(BBBCoreConstants.EMAIL);
            if(profile.getPropertyValue("walletId")!=null)
            walletId =(String) profile.getPropertyValue("walletId");
        }
        if(StringUtils.isEmpty(email)) {
          logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: Email not found, No promotions returned"));
           return null;
        }
        String pMobilePhone = setPhoneNumber(profile,billingAddress);
        boolean showOnlyActive=true;

        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: Making call for getCouponsList"));
        CouponResponseVo couponResponse = getCouponManager().getCouponsList(email, pMobilePhone, walletId, showOnlyActive, isFromBillingPaymentPage);
        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: returned  from getCouponsList"));
        List<CouponListVo> mCouponList=new ArrayList<CouponListVo>();
		if (null != couponResponse) {
			if (couponResponse.getErrorStatus() != null
					&& couponResponse.getErrorStatus().isErrorExists()) {
				logDebug(LogMessageFormatter
						.formatMessage(null,
								"populateValidPromotions: couponResponse got ErrorStatus"));
				ErrorStatus errStatus = couponResponse.getErrorStatus();
				throw new BBBSystemException(
						BBBCoreErrorConstants.CHECKOUT_ERROR_1069,
						"Error getting coupons from the services, "
								+ errStatus.getDisplayMessage());
			}

			mCouponList = couponResponse.getCouponList();
			if (mCouponList != null) {
				logDebug(LogMessageFormatter.formatMessage(null,
						"populateValidPromotions: couponResponse got coupons count"
								+ mCouponList.size()));
			}
		}
        return mCouponList;
    }
	
	public Map<String, RepositoryItem> fillPromoList(String site, List<CouponListVo> mCouponList)
			throws BBBSystemException, BBBBusinessException {
		HashMap<String, RepositoryItem> results = new HashMap<String, RepositoryItem>();
		if (!BBBUtility.isListEmpty(mCouponList)) {
			fillPromotionList(site,results,mCouponList);
					}
		return results;
	}
	
	
	private Boolean isExpRedeemedAndInstore(CouponListVo couponListVo ){
		String redemptionCount =couponListVo.getRedemptionCount();
		String redemptionLimit =couponListVo.getRedemptionLimit();
		DateFormat formatter = new SimpleDateFormat(BBBCoreConstants.WS_FORMAT_COUPONS);
		Date sysDate = new Date();
		String sysdate = formatter.format(sysDate);
		String expiry =  couponListVo.getExpiryDate();
		String redemptionChannel = couponListVo.getRedemptionChannel();
		
		Date expiryDate =null;
		try {
		if(!BBBUtility.isEmpty(expiry)){
			expiryDate = formatter.parse(expiry);
			}
		}catch (ParseException e) {
			logError(e.getMessage(),e);
		}
		boolean isExpRedeemedAndInstore=false;
		int redemptionCountInt=0;
		int redemptionLimitInt=1;
		if(!BBBUtility.isEmpty(redemptionCount)){
			redemptionCountInt = Integer.parseInt(redemptionCount);			
		}
		if(!BBBUtility.isEmpty(redemptionLimit)){
			redemptionLimitInt = Integer.parseInt(redemptionLimit);			
		}
		if((redemptionCountInt >= redemptionLimitInt) || (expiryDate!=null && sysDate.after(expiryDate)) ||(redemptionChannel!=null && redemptionChannel.equalsIgnoreCase("1"))){
			isExpRedeemedAndInstore=true;
		}
		return isExpRedeemedAndInstore;
	}

	/**
     * 
     * @param profile
     * @param order
     * @param site
     * @return
     * @throws BBBSystemException
     * @throws BBBBusinessException
     */
    
    public Map<String, RepositoryItem> populateValidPromotions(final Profile profile, final BBBOrderImpl order,
            String site, boolean isFromBillingPaymentPage) throws BBBSystemException, BBBBusinessException {
        HashMap<String, RepositoryItem> results = new HashMap<String, RepositoryItem>();
        String email  = null;
        String  walletId= null;
        BBBRepositoryContactInfo billingAddress = order.getBillingAddress();
        if(billingAddress != null && profile.isTransient() ) {
            email = billingAddress.getEmail();            
        }
        if(!profile.isTransient()) {
            email = (String) profile.getPropertyValue(BBBCoreConstants.EMAIL);
            if(profile.getPropertyValue("walletId")!=null)
            walletId =(String) profile.getPropertyValue("walletId");
        }
        if(StringUtils.isEmpty(email)) {
         logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: Email not found, No promotions returned"));
          return results;
        }
        String pMobilePhone = setPhoneNumber(profile,billingAddress);
        populateCouponsFromService(order, site, isFromBillingPaymentPage, results,
				email, walletId, pMobilePhone);
        return results;
    }

	/**
	 * Populate coupons from service.
	 *
	 * @param order the order
	 * @param site the site
	 * @param isFromBillingPaymentPage the is from billing payment page
	 * @param promotions the promotions
	 * @param email the email
	 * @param walletId the wallet id
	 * @param pMobilePhone the mobile phone
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public void populateCouponsFromService(final BBBOrderImpl order, String site,
			boolean isFromBillingPaymentPage,
			Map<String, RepositoryItem> promotions, String email,
			String walletId, String pMobilePhone) throws BBBSystemException,
			BBBBusinessException {
		boolean showOnlyActive=true;
        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: Making call for getCouponsList"));
        CouponResponseVo couponResponse = getCouponManager().getCouponsList(email, pMobilePhone, walletId, showOnlyActive, isFromBillingPaymentPage);
        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: returned  from getCouponsList"));
        if(couponResponse.getErrorStatus().isErrorExists()) {
        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: couponResponse got ErrorStatus"));
         ErrorStatus errStatus = couponResponse.getErrorStatus();
         throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1069,"Error getting coupons from the services, " + errStatus.getDisplayMessage());
        }
        List<CouponListVo> mCouponList = couponResponse.getCouponList();
        if(mCouponList != null){
        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: couponResponse got coupons count" + mCouponList.size()));
        order.setCouponListVo(mCouponList);
        fillPromotionList(site, promotions, mCouponList);
        }
	}
    
    public Map<String, RepositoryItem> populateSPCValidPromotions(Profile profile, BBBOrderImpl order,
            String site) throws BBBSystemException, BBBBusinessException {
        HashMap<String, RepositoryItem> results = new HashMap<String, RepositoryItem>();
        String email  = null;
        String  walletId= null;
        BBBRepositoryContactInfo shippingAddress = order.getShippingAddress();
        if(shippingAddress != null && profile.isTransient() ) {
            email = shippingAddress.getEmail();            
        }
        if(!profile.isTransient()) {
            email = (String) profile.getPropertyValue(BBBCoreConstants.EMAIL);
            if(profile.getPropertyValue("walletId")!=null)
            walletId =(String) profile.getPropertyValue("walletId");
        }
        if(StringUtils.isEmpty(email)) {
         logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: Email not found, No promotions returned"));
          return results;
        }
        String pMobilePhone = setPhoneNumber(profile,shippingAddress);

        boolean showOnlyActive=true;
        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: Making call for getCouponsList"));
        CouponResponseVo couponResponse = getCouponManager().getCouponsList(email, pMobilePhone, walletId, showOnlyActive, false);
        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: returned  from getCouponsList"));
        if(couponResponse.getErrorStatus()!=null && couponResponse.getErrorStatus().isErrorExists()) {
        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: couponResponse got ErrorStatus"));
         ErrorStatus errStatus = couponResponse.getErrorStatus();
         this.logError("Error in coupon ws call::::"+errStatus.getDisplayMessage());
         }else{
        List<CouponListVo> mCouponList = couponResponse.getCouponList();
        DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
    	pRequest.getSession().setAttribute("couponMailId",email);
        if(mCouponList != null){
        logDebug(LogMessageFormatter.formatMessage(null, "populateValidPromotions: couponResponse got coupons count" + mCouponList.size()));
        order.setCouponListVo(mCouponList);
        fillPromotionList(site, results, mCouponList);
        }
        }
        return results;
    }
    public void fillPromotionList(String site,
            Map<String, RepositoryItem> results,
            List<CouponListVo> mCouponList) throws BBBSystemException,
            BBBBusinessException {
        for (CouponListVo couponListVo : mCouponList) {
           String code = couponListVo.getEntryCd();
           if(!isExpRedeemedAndInstore(couponListVo)){
           RepositoryItem[] promId = getCatalogTools().getPromotions(code);
           if(promId != null && promId.length > 0 && promId[0] != null && validPromo(promId[0], site)) {
               results.put(code, promId[0]);
               this.logDebug("promotion code adding to user profile is : "+promId[0]);
           } 
           }
        }
    }
    
    private String setPhoneNumber(final Profile profile,BBBRepositoryContactInfo billingAddress) {
        String results = ""; 
        if(billingAddress != null && profile.isTransient()) {
        	results = billingAddress.getPhoneNumber();
        	if (BBBUtility.isEmpty(results)) {
        		results = billingAddress.getMobileNumber();
        	}
        }
        if(BBBUtility.isEmpty(results)){
        	results = (String) profile.getPropertyValue(BBBCoreConstants.MOBILE_NUM);
        }
        return results;
    }
    
    /**
     * 
     * @param promoItem
     * @param siteID
     * @return
     */
 public static boolean validPromo(RepositoryItem promoItem, String siteID) {
        
        /*no validations for now, only valid coupons will be returned by the service*/
    	Timestamp promoTimestamp=null;
    	Date todaysDate = new Date();
    	Object endUsableDate = promoItem.getPropertyValue(BBBCoreConstants.END_USABLE);
        if(endUsableDate != null && endUsableDate != BBBCoreConstants.BLANK){
		promoTimestamp = ((Timestamp)endUsableDate);
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTimeInMillis(promoTimestamp.getTime());
		//Commented as part of MO-236 - Coupon expiration date interpreted incorrectly 
		/*calendar.add(Calendar.HOUR, -3);
		calendar.add(Calendar.MINUTE, -1);*/
		Date promotionDate =calendar.getTime();
		if(todaysDate.after(promotionDate)){
			return false;
		}
        }
        
		
        return true;
    }
    
    public BBBGetCouponsManager getCouponManager() {
        return this.mCouponManager;
    }
    public void setCouponManager(BBBGetCouponsManager pCouponManager) {
        this.mCouponManager = pCouponManager;
    }
    public BBBCatalogToolsImpl getCatalogTools() {
        return this.mCatalogTools;
    }
    public void setCatalogTools(BBBCatalogToolsImpl catalogTools) {
        this.mCatalogTools = catalogTools;
    }

    public PromotionTools getPromotionTools() {
        return this.mPromotionTools;
    }

    public void setPromotionTools(PromotionTools mPromotionTools) {
        this.mPromotionTools = mPromotionTools;
    }
    
    /**
	 * This Method will return promotions Map for paypal user(Transient).
	 * 
	 * @param order
	 * @return Map<String, RepositoryItem>
     * @throws IOException 
     * @throws ServletException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, RepositoryItem> getCouponMap(BBBOrderImpl order, Profile profile) throws ServletException, IOException{
		
		this.logDebug("Start: method BBBGetCouponsManager().getCouponMapForTransientUser");
        String couponOn = null;
        Map<String, RepositoryItem> promotions = null;
        try {
            final Map<String, String> configMap = this.getCatalogTools().getConfigValueByconfigType(BBBCatalogConstants.CONTENT_CATALOG_KEYS);
            final String orderSiteId = order.getSiteId();

            if (orderSiteId.equalsIgnoreCase(configMap.get(BBBCatalogConstants.BED_BATH_US_SITE_CODE))) {
                couponOn = this.getCatalogTools().getContentCatalogConfigration(BBBPayPalConstants.COUPONS_US).get(0);
            } else if (orderSiteId.equalsIgnoreCase(configMap.get(BBBCatalogConstants.BUY_BUY_BABY_SITE_CODE))) {
                couponOn = this.getCatalogTools().getContentCatalogConfigration(BBBPayPalConstants.COUPONS_BABY).get(0);
            } else if (orderSiteId.equalsIgnoreCase(configMap.get(BBBCatalogConstants.BED_BATH_CANADA_SITE_CODE))) {
                couponOn = this.getCatalogTools().getContentCatalogConfigration(BBBPayPalConstants.COUPONS_CANADA).get(0);
            }
            if (couponOn == null) {
                couponOn = BBBCoreConstants.TRUE;
            }
          
            
            if (couponOn.equalsIgnoreCase(BBBCoreConstants.TRUE)) {
            	final List<RepositoryItem> availablePromotions = (List<RepositoryItem>) profile
                        .getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);
            		int availablePromotionSize = availablePromotions.size();
                // remove already granted promotion
                for (final Object element : availablePromotions) {
                    final RepositoryItem promotion = (RepositoryItem) element;
                    this.getPromotionTools().removePromotion(profile, promotion,
                                    false);
                }

                availablePromotions.clear();
                ((MutableRepositoryItem) profile).setPropertyValue(
                                BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, availablePromotions);
				/*
				 * When ever we remove active promotion from profile we should
				 * re initialize pricing models to update PrcingModelHolder
				 * object.
				 */
                if(availablePromotionSize > 0) {
                	getPromotionTools().initializePricingModels();
                    getPromotionTools().getPricingTools().priceOrderSubtotalShipping(order, null, ServletUtil.getCurrentRequest().getLocale(), profile, null);
                }
                promotions = populateValidPromotions(profile, order, order.getSiteId(),false);
                promotions = this.getCouponUtil().applySchoolPromotion(promotions,
                		profile, order);
                
                this.logDebug("BBBGetCouponsManager().getCouponMapForTransientUser :: Promotions: " + promotions);
                order.setCouponMap(promotions);
            }
        } catch (final BBBException e) {
            this.logError("Error finding the coupons", e);
        } catch (PricingException e) {
        	this.logError("PromotionLookupManager.getCouponMap: Pricing Exception while repricing order OrderSubtotalShipping", e);
		}
        this.logDebug("End: method BBBGetCouponsManager().getCouponMapForTransientUser");
        return promotions;
        
	}
	
	 /** This method checks if school Promotion is available in Order coupon Map or not.
    *
    * @return boolean, true if school promotion available else false */
   public boolean isSchoolPromotion(BBBOrderImpl order) {
	   
       this.logDebug("Start: method isSchoolPromotion");
       boolean flag = false;
       if (!BBBUtility.isMapNullOrEmpty(order.getCouponMap())) {
           flag = order.getCouponMap().containsKey(BBBCheckoutConstants.SCHOOLPROMO);
       }
       this.logDebug("End: method isSchoolPromotion");

       return flag;
   }
}
