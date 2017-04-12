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

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.OrderHolder;
import atg.core.util.StringUtils;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.account.vo.CouponListVo;
import com.bbb.account.vo.CouponResponseVo;
import com.bbb.account.vo.RedemptionCodeVO;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.CouponsComparator;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.order.manager.PromotionLookupManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;
import atg.repository.MutableRepository;

public class GetCouponsDroplet extends BBBDynamoServlet {
	
	
	private  BBBGetCouponsManager  mCouponsManager;
	private BBBCatalogToolsImpl mCatalogTools;
	private String mMediaItmKey;
	private String mTermConditionItmKey;
	private SiteContext mSiteContext;
	private BBBSessionBean sessionBean;
	private String exclusionRuleQuery;
	private BBBPromotionTools promTools;
	public String getExclusionRuleQuery() {
		return exclusionRuleQuery;
	}


	public void setExclusionRuleQuery(String exclusionRuleQuery) {
		this.exclusionRuleQuery = exclusionRuleQuery;
	}


	public MutableRepository getCouponRepository() {
		return couponRepository;
	}


	public void setCouponRepository(MutableRepository couponRepository) {
		this.couponRepository = couponRepository;
	}

	private MutableRepository couponRepository;
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}


	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}


	/**
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return mSiteContext;
	}


	/**
	 * @param pSiteContext the siteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		mSiteContext = pSiteContext;
	}


	/**
	 * @return the mediaItmKey
	 */
	public String getMediaItmKey() {
		return mMediaItmKey;
	}



	/**
	 * @param pMediaItmKey the mediaItmKey to set
	 */
	public void setMediaItmKey(String pMediaItmKey) {
		mMediaItmKey = pMediaItmKey;
	}


	/**
	 * @return the catalogTools
	 */
	public BBBCatalogToolsImpl getCatalogTools() {
		return mCatalogTools;
	}


	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogToolsImpl pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}


	/**
	 * @return the couponsManager
	 */
	public BBBGetCouponsManager getCouponsManager() {
		return mCouponsManager;
	}


	/**
	 * @param pCouponsManager the couponsManager to set
	 */
	public void setCouponsManager(BBBGetCouponsManager pCouponsManager) {
		mCouponsManager = pCouponsManager;
	}


	/**
	 * @return the termConditionItmKey
	 */
	public String getTermConditionItmKey() {
		return mTermConditionItmKey;
	}


	/**
	 * @param pTermConditionItmKey the termConditionItmKey to set
	 */
	public void setTermConditionItmKey(String pTermConditionItmKey) {
		mTermConditionItmKey = pTermConditionItmKey;
	}



	/**
	 * This method is used to fetch data of my coupons to show on UI.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("GetCouponsDroplet.service() method Started");
	     
		
		String emailAddr = pRequest
				.getParameter(BBBCoreConstants.EMAIL_ADDR_PARAM_NAME);
		String mobilePhone = pRequest
				.getParameter(BBBCoreConstants.MOBILE_NUMBER_PARAM_NAME);
		String walletId = pRequest.getParameter(BBBCoreConstants.WALLETID);
		String offerId = pRequest.getParameter(BBBCoreConstants.OFFER_ID);
		String channel = BBBUtility.getChannel();
		logDebug(" channel::" + channel);
		if (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel)) {
			((BBBProfileTools) getCouponsManager().getProfileTools()).createShallowForNonExistingUser(pRequest, emailAddr, walletId, true);
			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName("/com/bbb/profile/session/SessionBean");
			emailAddr = sessionBean.getCouponEmail();
		}
		Profile profile = (Profile) pRequest
				.resolveName("/atg/userprofiling/Profile");
		
		if (StringUtils.isBlank(walletId)) {
			walletId = (String) profile.getPropertyValue("walletId");
		}
		OrderHolder cart = (OrderHolder) pRequest
				.resolveName("/atg/commerce/ShoppingCart");
		BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
		boolean showOnlyActive = false;
		List<CouponListVo> mCouponList = null;

		List<CouponListVo> onlineCoupons  =new ArrayList<CouponListVo>();
		List<CouponListVo> useAnywhereCoupons =new ArrayList<CouponListVo>();
		List<CouponListVo> inStoreCoupons =new ArrayList<CouponListVo>();
		List<CouponListVo> expRedeemedCoupons =new ArrayList<CouponListVo>();
		List<CouponListVo> preOnlineCoupons  =new ArrayList<CouponListVo>();
		List<CouponListVo> preUseAnywhereCoupons =new ArrayList<CouponListVo>();
		List<CouponListVo> preInStoreCoupons =new ArrayList<CouponListVo>();
		List<CouponListVo> preExpRedeemedCoupons =new ArrayList<CouponListVo>();
		final Map<String, List<CouponListVo>> couponsMap = new LinkedHashMap<String, List<CouponListVo>>();
		RedemptionCodeVO redemptionCodeVO =new RedemptionCodeVO();
		CouponListVo justAddedCouponVo=null;
		
		
		DateFormat format1 ;
		DateFormat displayFormat ;
		String siteId = SiteContextManager.getCurrentSiteId();
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
			//format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			displayFormat = new SimpleDateFormat("dd/MM/yyyy");
		}else{
			//format1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			displayFormat = new SimpleDateFormat("MM/dd/yyyy");
		}	
		DateFormat formatter = new SimpleDateFormat(BBBCoreConstants.WS_FORMAT_COUPONS);
		
		String justAddedType=null;
		
		try{
		
			 CouponResponseVo couponResponseVo = mCouponsManager.getCouponsList(emailAddr, mobilePhone, walletId, showOnlyActive,false);
				
			 if(couponResponseVo != null && couponResponseVo.getErrorStatus().isErrorExists()){
					ErrorStatus errStatus = couponResponseVo.getErrorStatus();
					
					if(errStatus.getErrorId()>0){
						String errorMessage =mCouponsManager.getWSMappedErrorMessage(errStatus.getErrorId());
						logError(LogMessageFormatter.formatMessage(null, "GetCouponsDroplet::service : Technical Error received while fetching My Coupons System \n Error returned from service : " + errStatus.getDisplayMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
						pRequest.setParameter("systemerror", errorMessage);
						pRequest.serviceLocalParameter("error", pRequest, pResponse);
					}
//					if(!BBBUtility.isEmpty(errStatus.getDisplayMessage()))//Business error from webservice
//					{
//						logError(LogMessageFormatter.formatMessage(null, "GetCouponsDroplet::service : Technical Error received while fetching My Coupons System \n Error returned from service : " + errStatus.getDisplayMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
//						pRequest.setParameter("systemerror", errStatus.getDisplayMessage());
//						pRequest.serviceLocalParameter("error", pRequest, pResponse);
//					}else if(BBBUtility.isEmpty(errStatus.getDisplayMessage()) && !BBBUtility.isEmpty(errStatus.getErrorMessage()))//Technical Error
//					{
//						logError(LogMessageFormatter.formatMessage(null, "GetCouponsDroplet::service : Technical Error received while fetching My Coupons System \n Error returned from service : " + errStatus.getErrorMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
//						pRequest.setParameter("systemerror", errStatus.getErrorMessage());
//						pRequest.serviceLocalParameter("error", pRequest, pResponse);
//					}
					else if(errStatus.getErrorId() == -1){
						// setting this elseIf block for passing error parameter for mobile service
						logError(LogMessageFormatter.formatMessage(null, "GetCouponsDroplet::service : Technical Error received while fetching My Coupons System \n Error returned from service : " + errStatus.getErrorMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
						pRequest.setParameter("systemerror", "err_mycoupons_system_error");
						pRequest.serviceLocalParameter("error", pRequest, pResponse);
						pRequest.setParameter("errorMapForService", couponsMap);
					}					
					else{
						logError(LogMessageFormatter.formatMessage(null, "GetCouponsDroplet::service : Technical Error received while fetching My Coupons System \n Error returned from service : " + errStatus.getErrorMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
						pRequest.setParameter("systemerror", "err_mycoupons_system_error");
						pRequest.serviceLocalParameter("error", pRequest, pResponse);						
					}
			}else{
				if(couponResponseVo != null){
					mCouponList =  couponResponseVo.getCouponList();
				}
				
				int couponCount=0;
				int signupCount=0;
				boolean expSecAvailable=false;
				HashMap<String, RepositoryItem> results = new HashMap<String, RepositoryItem>();
				Date sysDate = new Date();
				String sysdate = formatter.format(sysDate);
				try {
					sysDate =formatter.parse(sysdate);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					logError(e1.getMessage(),e1);
				}
				
				if(offerId !=null){
					offerId = offerId.replaceAll("[^\\w\\s-]", "");
				}


				if(mCouponList != null){
				for (Iterator<CouponListVo> itr = mCouponList.iterator();itr.hasNext();) {
					CouponListVo couponListVo= new CouponListVo();
					couponListVo = itr.next();

					//adding bcc promotion details to the coupon
						String code = couponListVo.getEntryCd();
						RepositoryItem[] promId = mCatalogTools.getPromotions(code);


						getPromotionVO(couponListVo, promId);
						
					
					
					String lastRedeemption = couponListVo.getLastRedemptionDate();
					String expiry =  couponListVo.getExpiryDate();
					String issuedate  =couponListVo.getIssueDate();
					Date lastRedeemptionDate=null;
					Date expiryDate =null;
					Date issueDate = null;
					Date updatedIssueDate = null;
					try {
						if(lastRedeemption!=null && !lastRedeemption.isEmpty()){
						lastRedeemptionDate = formatter.parse(lastRedeemption);
				 }
						if(expiry!=null && !expiry.isEmpty()){
						expiryDate = formatter.parse(expiry);


						}
						if(issuedate!=null && !issuedate.isEmpty()){
							issueDate = formatter.parse(issuedate);
							Calendar cal = Calendar.getInstance();
					        cal.setTime(issueDate);
					        cal.add(Calendar.DATE, 2);
					        updatedIssueDate = cal.getTime();
					  
						}
						//code for just added coupons 
						if(couponListVo.getRedemptionCodesVO()!=null){
						redemptionCodeVO = (couponListVo.getRedemptionCodesVO()).get(0);								
						}
						if(offerId!=null && redemptionCodeVO!=null && (offerId.equalsIgnoreCase(redemptionCodeVO.getOnlineOfferCode())||offerId.equalsIgnoreCase(redemptionCodeVO.getpOSCouponID())|| offerId.equalsIgnoreCase(redemptionCodeVO.getUniqueCouponCd()))){
															
								if(isExpRedeemed(couponListVo.getRedemptionCount(), couponListVo.getRedemptionLimit(), sysDate,expiryDate)){
									justAddedType="expired";
									
								}else if(isInStoreCoupon(couponListVo.getRedemptionChannel())){
									justAddedType= "inStore";
									
								}else if(isOnlineCoupon(couponListVo.getRedemptionChannel())){
									justAddedType = "online";
								
								}else{
									justAddedType = "useranywhere";
								
								}
								couponListVo = changeDateFormat(couponListVo, displayFormat,formatter);								
								if(!couponListVo.getDisqualify()){
									justAddedCouponVo= new CouponListVo();
									justAddedCouponVo= couponListVo;
									couponCount++;
							
								}
						}else if(isPreloadedCoupon(updatedIssueDate, sysDate)){
							//Adding code for preloaded coupons
							logDebug("Entered in Preloaded coupons loop");
							if(isExpRedeemed(couponListVo.getRedemptionCount(), couponListVo.getRedemptionLimit(), sysDate,expiryDate)){
								logDebug("Entered in ExpRedeemed");
								couponListVo = changeDateFormat(couponListVo, displayFormat,formatter);
								if (!couponListVo.getDisqualify()) {
								couponListVo.setPreloaded(true);
								preExpRedeemedCoupons.add(couponListVo);
								}
							}else if(isInStoreCoupon(couponListVo.getRedemptionChannel())){
								logDebug("Entered in InStoreCoupon");
								couponListVo = changeDateFormat(couponListVo, displayFormat,formatter);
								couponListVo.setPreloaded(true);								
								if(!couponListVo.getDisqualify()){
									preInStoreCoupons.add(couponListVo);
									couponCount++;
								}
							}else if(isOnlineCoupon(couponListVo.getRedemptionChannel())){
								logDebug("Entered in OnlineCoupon");
								couponListVo = changeDateFormat(couponListVo, displayFormat,formatter);
								couponListVo.setPreloaded(true);							
								if(!couponListVo.getDisqualify()){
									preOnlineCoupons.add(couponListVo);
									couponCount++;
								}
							}else{
								couponListVo = changeDateFormat(couponListVo, displayFormat,formatter);
								couponListVo.setPreloaded(true);								
								if(!couponListVo.getDisqualify()){
									preUseAnywhereCoupons.add(couponListVo);
									couponCount++;
								}
								
							}
							
						}else if(isExpRedeemed(couponListVo.getRedemptionCount(), couponListVo.getRedemptionLimit(), sysDate,expiryDate)){
							couponListVo = changeDateFormat(couponListVo, displayFormat,formatter);
							if (!couponListVo.getDisqualify()) {
							expRedeemedCoupons.add(couponListVo);							
							expSecAvailable = true;
						
								}						
						}else if(isInStoreCoupon(couponListVo.getRedemptionChannel())){
							couponListVo = changeDateFormat(couponListVo, displayFormat,formatter);							
							if(!couponListVo.getDisqualify()) {
								inStoreCoupons.add(couponListVo);
								couponCount++;
							
							}
						}else if(isOnlineCoupon(couponListVo.getRedemptionChannel())){
							couponListVo = changeDateFormat(couponListVo, displayFormat,formatter);							
							if(!couponListVo.getDisqualify()){
						onlineCoupons.add(couponListVo);
								couponCount++;
						
							}
					}else{
							// if(couponListVo.getRedemptionChannel()!=null && couponListVo.getRedemptionChannel().equalsIgnoreCase("3"))
							couponListVo = changeDateFormat(couponListVo, displayFormat,formatter);							
							if(!couponListVo.getDisqualify()){	
						useAnywhereCoupons.add(couponListVo);
								couponCount++;
							
					}
					
				}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logError(e.getMessage(),e);
					}
				}
				
				Collections.sort(useAnywhereCoupons, new CouponsComparator());
				Collections.sort(onlineCoupons, new CouponsComparator());
				Collections.sort(inStoreCoupons, new CouponsComparator());
				Collections.sort(expRedeemedCoupons, new CouponsComparator());	
				
				//sorting pre loaded coupons
				Collections.sort(preUseAnywhereCoupons, new CouponsComparator());
				Collections.sort(preOnlineCoupons, new CouponsComparator());
				Collections.sort(preInStoreCoupons, new CouponsComparator());
				Collections.sort(preExpRedeemedCoupons, new CouponsComparator());	
				
				//adding preloaded coupons on the top of the list.
				useAnywhereCoupons.addAll(0, preUseAnywhereCoupons);
				onlineCoupons.addAll(0, preOnlineCoupons);
				inStoreCoupons.addAll(0, preInStoreCoupons);
				expRedeemedCoupons.addAll(0, preExpRedeemedCoupons);
				
				//Adding click through coupon all the way on top if available
				if(justAddedCouponVo!=null){
					if(justAddedType.equalsIgnoreCase("expired")){
						expRedeemedCoupons.add(0, justAddedCouponVo);
					}else if(justAddedType.equalsIgnoreCase("inStore")){
						inStoreCoupons.add(0, justAddedCouponVo);
					}else if(justAddedType.equalsIgnoreCase("online")){
						onlineCoupons.add(0, justAddedCouponVo);
					}else{
						useAnywhereCoupons.add(0, justAddedCouponVo);
					}					
				}
			
				if(useAnywhereCoupons.isEmpty()&& onlineCoupons.isEmpty() && inStoreCoupons.isEmpty()){
					signupCount=0;
				}else if(!useAnywhereCoupons.isEmpty()&& !onlineCoupons.isEmpty() && !inStoreCoupons.isEmpty()){
					signupCount=3;
				}else if((!useAnywhereCoupons.isEmpty()&& onlineCoupons.isEmpty() && inStoreCoupons.isEmpty()) ||(useAnywhereCoupons.isEmpty()&& !onlineCoupons.isEmpty() && inStoreCoupons.isEmpty())||(useAnywhereCoupons.isEmpty()&& onlineCoupons.isEmpty() && !inStoreCoupons.isEmpty())){
					signupCount=1;
				}else if((!useAnywhereCoupons.isEmpty()&& !onlineCoupons.isEmpty() && inStoreCoupons.isEmpty()) ||(!useAnywhereCoupons.isEmpty()&& onlineCoupons.isEmpty() && !inStoreCoupons.isEmpty())||(useAnywhereCoupons.isEmpty()&& !onlineCoupons.isEmpty() && !inStoreCoupons.isEmpty())){
					signupCount=2;
				}
				
				pRequest.setParameter("signupCount", signupCount);	
				pRequest.setParameter("expSecAvailable", expSecAvailable);
				
				
				if(useAnywhereCoupons!=null && !useAnywhereCoupons.isEmpty()){
				couponsMap.put("useAnywhereCoupons", useAnywhereCoupons);
				}
				if(onlineCoupons!=null && !onlineCoupons.isEmpty()){
				couponsMap.put("onlineCoupons", onlineCoupons);
				}
				if(inStoreCoupons!=null && !inStoreCoupons.isEmpty()){
				couponsMap.put("inStoreCoupons", inStoreCoupons);
				}
				if(expRedeemedCoupons!=null && !expRedeemedCoupons.isEmpty()){
				couponsMap.put("expRedeemedCoupons", expRedeemedCoupons);
				}
				pRequest.setParameter("couponCount", couponCount);
				getSessionBean().setCouponCount(couponCount);
				}else{
					pRequest.setParameter("couponCount", couponCount);
					getSessionBean().setCouponCount(couponCount);
				}
				
				pRequest.setParameter("couponsMap", couponsMap);
				
				pRequest.serviceLocalParameter("output", pRequest, pResponse);
			}
			
		} catch (BBBBusinessException bbbBusinessException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "err_mycoupons_system_error" , BBBCoreErrorConstants.ACCOUNT_ERROR_1118),bbbBusinessException);
			pRequest.setParameter("systemerror", "err_mycoupons_system_error");
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
		} catch (BBBSystemException bbbSystemException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "err_mycoupons_system_error" , BBBCoreErrorConstants.ACCOUNT_ERROR_1118),bbbSystemException);
			pRequest.setParameter("systemerror", "err_mycoupons_system_error");
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
		}

			logDebug("GetCouponsDroplet.service() method end");
		
		
	}
	
	
	private Boolean isExpRedeemed(String redemptionCount, String redemptionLimit, Date sysDate, Date expiryDate){
		boolean isExpRedeemed=false;
		int redemptionCountInt=0;
		int redemptionLimitInt=1;
		if((redemptionCount!=null && !redemptionCount.isEmpty() )&& (redemptionLimit!=null && !redemptionLimit.isEmpty())){
		redemptionCountInt = Integer.parseInt(redemptionCount);
		redemptionLimitInt = Integer.parseInt(redemptionLimit);
	}
		if((redemptionCountInt >= redemptionLimitInt) || (expiryDate!=null && sysDate.after(expiryDate))){
			isExpRedeemed=true;
		}
		
		return isExpRedeemed;
	}

	private Boolean isInStoreCoupon(String redemptionChannel){
		boolean isInStoreCoupon=false;
		if(redemptionChannel!=null && redemptionChannel.equalsIgnoreCase("1")){
			isInStoreCoupon=true;
		}
		
		return isInStoreCoupon;
	}
	
	private Boolean isOnlineCoupon(String redemptionChannel){
		boolean isOnlineCoupon=false;
		if(redemptionChannel!=null && redemptionChannel.equalsIgnoreCase("2")){
			isOnlineCoupon=true;
		}
	
		return isOnlineCoupon;
	}
	
	private Boolean isPreloadedCoupon(Date updatedIssueDate, Date sysDate){
		boolean isPreloadedCoupon=false;
		if(sysDate!=null && updatedIssueDate!=null && (sysDate.before(updatedIssueDate) || sysDate.equals(updatedIssueDate)) ){
			isPreloadedCoupon=true;
		}
		return isPreloadedCoupon;
	}
	
	
	private CouponListVo changeDateFormat(CouponListVo couponListVo,DateFormat displayFormat, DateFormat formatter){

		String expirationDate = couponListVo.getExpiryDate();
		String displayExpirationDate = couponListVo.getDisplayExpiryDate();
		String lastRedeemptionDate = couponListVo.getLastRedemptionDate();
		logDebug("changeDateFormat method Started");
	
		try {
			if(expirationDate!=null && !expirationDate.isEmpty()){
			couponListVo.setExpiryDate(displayFormat.format(formatter.parse(expirationDate)));
			}
			if(displayExpirationDate!=null && !displayExpirationDate.isEmpty()){
				couponListVo.setDisplayExpiryDate(displayFormat.format(formatter.parse(displayExpirationDate)));
				}
			if(lastRedeemptionDate != null && !lastRedeemptionDate.isEmpty()){
			couponListVo.setLastRedemptionDate(displayFormat.format(formatter.parse(lastRedeemptionDate)));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logError (e.getMessage(),e);
		}
		return couponListVo;
	}
	
	/**
	 * This method is used to get promotion information and fill in couponListVo for return entry code by web service.
	 * @param couponListVo
	 * @param promId repository item
	 * @throws BBBSystemException 
	 */
	private void getPromotionVO(CouponListVo couponListVo,
			RepositoryItem[] promId) throws BBBSystemException {		
	
			logDebug("GetCouponsDroplet.getPromotionVO() method Started");
	     
		if(promId != null && promId.length != 0 && promId[0] != null && PromotionLookupManager.validPromo(promId[0], mSiteContext.getSite().getId())) {
			fillVOObject(couponListVo, promId[0]);
		} else {
			couponListVo.setDisqualify(true);
				logDebug("Either Promotion validation fail or Promotion doen't exist for this entry code");
		}
		
			logDebug("GetCouponsDroplet.getPromotionVO() method Started");
	     
	}

	/**
	 * This method is used to fill promotion information in couponListVo.
	 * @param couponListVo
	 * @param promId repository item
	 * @throws BBBSystemException 
	 */
	
	@SuppressWarnings({ "rawtypes", "static-access" })
    private void fillVOObject(CouponListVo couponListVo, RepositoryItem promo) throws BBBSystemException {
		
			logDebug("GetCouponsDroplet.fillVOObject() method Started");
			//String siteId = SiteContextManager.getCurrentSiteId();
			
			DateFormat	format1 = new SimpleDateFormat(BBBCoreConstants.WS_FORMAT_COUPONS);


			DateFormat wsFormatter = new SimpleDateFormat(BBBCoreConstants.WS_FORMAT_COUPONS);
			Timestamp wsExpDate =null;
			Timestamp promoTimestamp=null;
			Timestamp couponTimestamp=null;
			
		Map mediaMap = (Map) promo.getPropertyValue(BBBCoreConstants.MEDIA);

		String promoDesription = (String)promo.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
		if(mediaMap != null){

			RepositoryItem mediaIteam = (RepositoryItem) mediaMap.get(mMediaItmKey);
		
			if(mediaIteam != null){
				couponListVo.setCouponsImageUrl((String) (mediaIteam.getPropertyValue(BBBCoreConstants.URL) != null ? mediaIteam.getPropertyValue(BBBCoreConstants.URL):"") );
			}
		}
		
		String siteId = mSiteContext.getSite().getId();
		String couponCode = couponListVo.getEntryCd();
		couponListVo.setCouponsExclusions (this.getPromTools().fetchExclusionText(couponCode, siteId));
		couponListVo.setPromoId((String) promo.getPropertyValue(BBBCoreConstants.ID));
		couponListVo.setDescription((String) promoDesription);
		couponListVo.setCouponsDescription((String) (promo.getPropertyValue(BBBCoreConstants.DESCRIPTION) != null ? promo.getPropertyValue(BBBCoreConstants.DESCRIPTION) :promoDesription));
		
		String wsExpirationDate = couponListVo.getExpiryDate();

		if(wsExpirationDate !=null){
		try {
			wsExpDate = new Timestamp((wsFormatter.parse(wsExpirationDate)).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logError (e.getMessage(),e);
		}
		}
		
		Object endUsableDate = promo.getPropertyValue(BBBCoreConstants.END_USABLE);
		logDebug("endUsableDate"+ endUsableDate);
        if(endUsableDate != null && endUsableDate != BBBCoreConstants.BLANK){
		promoTimestamp = ((Timestamp)endUsableDate);
        }
        if(wsExpDate!=null && promoTimestamp!=null && ((wsExpDate.before(promoTimestamp)) || (wsExpDate.equals(promoTimestamp))) )
        {
        	couponTimestamp=wsExpDate;
        }else if(wsExpDate!=null && promoTimestamp!=null && (promoTimestamp.before(wsExpDate)) )
        {
        	couponTimestamp=promoTimestamp;
        }
        else if(wsExpDate!=null && promoTimestamp==null){
        	couponTimestamp=wsExpDate;
        }
        else if(wsExpDate==null && promoTimestamp!=null){
        	couponTimestamp=promoTimestamp;
        }
        if(couponTimestamp != null){
        	logDebug("GetCouponsDroplet couponTimestamp is :" + couponTimestamp);
        	java.util.Calendar calendar = java.util.Calendar.getInstance();
			java.util.Calendar sysDate = java.util.Calendar.getInstance();
			calendar.setTimeInMillis(couponTimestamp.getTime());
        	couponListVo.setExpiryDate(format1.format(calendar.getTime()));
			calendar.add(Calendar.HOUR, -3);
			calendar.add(Calendar.MINUTE, -1);
			couponListVo.setDisplayExpiryDate(format1.format(calendar.getTime()));
			int daysDiff = (int)(Math.ceil((calendar.getTimeInMillis()-sysDate.getTimeInMillis())/(float)(24*3600*1000)));
			couponListVo.setExpiryCount(daysDiff);
        }
		logDebug("GetCouponsDroplet couponListVo.getEntryCd()" + couponListVo.getEntryCd());
		logDebug("GetCouponsDroplet couponListVo.getCouponsDescription()" + couponListVo.getCouponsDescription());
		logDebug("GetCouponsDroplet couponListVo.getDescription()" + couponListVo.getDescription());
			logDebug("GetCouponsDroplet.fillVOObject() method Ended");
	}


	public BBBPromotionTools getPromTools() {
		return promTools;
	}


	public void setPromTools(BBBPromotionTools promTools) {
		this.promTools = promTools;
	}
	
}
