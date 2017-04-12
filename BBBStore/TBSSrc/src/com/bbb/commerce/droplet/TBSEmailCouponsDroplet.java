package com.bbb.commerce.droplet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.bbb.account.BBBGetCouponsManager;
import com.bbb.account.TBSProfileTools;
import com.bbb.account.vo.CouponListVo;
import com.bbb.account.vo.CouponResponseVo;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.claimable.TBSClaimableManager;
import com.bbb.commerce.order.manager.PromotionLookupManager;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.rest.checkout.vo.AppliedCouponListVO;
import com.bbb.utils.BBBUtility;

public class TBSEmailCouponsDroplet extends DynamoServlet {
	
	private static final String APPLIED_COUPON_VO = "appliedCpnVO";
	/**
	 * mCouponsManager to hold BBBGetCouponsManager
	 */
	private  BBBGetCouponsManager mCouponsManager;
	/**
	 * mCatalogTools to hold BBBCatalogTools
	 */
	private BBBCatalogTools mCatalogTools;
	/**
	 * mClaimableManager to hold TBSClaimableManager
	 */
	private TBSClaimableManager mClaimableManager;
	private TBSProfileTools profileTools;
	/**
	 * mPromoManger
	 */
	private PromotionLookupManager mPromoManger;
	private BBBPromotionTools promotionTools;
	
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
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the claimableManager
	 */
	public TBSClaimableManager getClaimableManager() {
		return mClaimableManager;
	}

	/**
	 * @param pClaimableManager the claimableManager to set
	 */
	public void setClaimableManager(TBSClaimableManager pClaimableManager) {
		mClaimableManager = pClaimableManager;
	}

	/**
	 * @return the promoManger
	 */
	public PromotionLookupManager getPromoManger() {
		return mPromoManger;
	}

	/**
	 * @param pPromoManger the promoManger to set
	 */
	public void setPromoManger(PromotionLookupManager pPromoManger) {
		mPromoManger = pPromoManger;
	}

	/**
	 * 
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("TBSEmailCouponsDroplet :: service() method to get the email and phone based coupons :: START");
		List<CouponListVo> mCouponList = null;
		CouponResponseVo couponResponseVo = null;
		
		String emailAddress = pRequest.getParameter("emailId");
		String mobileNumber = pRequest.getParameter("mobileNumber");
		BBBOrderImpl order = (BBBOrderImpl) pRequest.getObjectParameter(BBBCoreConstants.ORDER);
		String site = (String) pRequest.getObjectParameter(BBBCoreConstants.SITE);
		String walletId = pRequest.getParameter("walletId");
		Profile profile = (Profile) pRequest
				.resolveName("/atg/userprofiling/Profile");
		
		if (StringUtils.isBlank(walletId) && null != profile && null != profile.getPropertyValue("email") && ((String)profile.getPropertyValue("email")).equalsIgnoreCase(emailAddress)) {
			walletId = (String) profile.getPropertyValue("walletId");
		}	
		else if(StringUtils.isBlank(walletId) && null != emailAddress){
			RepositoryItem bbbUserProfile = getProfileTools()
					.getItemFromEmail(emailAddress.toLowerCase());
			if (bbbUserProfile != null) {
				walletId = (String) bbbUserProfile
						.getPropertyValue("walletId");
			}
		}
		if(StringUtils.isBlank(emailAddress) && StringUtils.isBlank(mobileNumber)){
			pRequest.serviceParameter(BBBCoreConstants.EMPTY, pRequest, pResponse);
			return;
		}
		DateFormat displayFormat;
		if(site.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
			displayFormat = new SimpleDateFormat(BBBCoreConstants.CA_DATE_FORMAT);
		}else{
			displayFormat = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
		}	
		DateFormat formatter = new SimpleDateFormat(BBBCoreConstants.WS_FORMAT_COUPONS);
		
		HashMap<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>();
		try {
			vlogDebug("email Id :: "+emailAddress + " mobileNumber :: "+mobileNumber);
			couponResponseVo = getCouponsManager().getCouponsList(emailAddress, mobileNumber, walletId, true, false);
			if(couponResponseVo != null && couponResponseVo.getErrorStatus().isErrorExists()){
				ErrorStatus errStatus = couponResponseVo.getErrorStatus();
				//Technical Error
				if(!BBBUtility.isEmpty(errStatus.getErrorMessage())){
					vlogError(LogMessageFormatter.formatMessage(null, "Technical Error received while fetching My Coupons System \n Error returned from service : " + errStatus.getErrorMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
				}
				//Business error from webservice
				if(!BBBUtility.isEmpty(errStatus.getDisplayMessage())){
					vlogError(LogMessageFormatter.formatMessage(null, "Technical Error received while fetching My Coupons System \n Error returned from service : " + errStatus.getDisplayMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));
				}
				pRequest.serviceLocalParameter(TBSConstants.ERROR, pRequest, pResponse);
			}else if(couponResponseVo != null){
				mCouponList =  couponResponseVo.getCouponList();
				
				if(mCouponList != null && !mCouponList.isEmpty()){
					getPromoManger().fillPromotionList(site, promotions, mCouponList);
					
					List<RepositoryItem> profileSelPromos = this.getPromotionTools().getCouponListFromOrder(order);/* new ArrayList<RepositoryItem>();
					getClaimableManager().getPromotionTools().getOrderPromotions(order, profileSelPromos, profileSelPromos,
							profileSelPromos, profileSelPromos, false);*/
					int index = 0;

					AppliedCouponListVO appliedCpnListVO = null;
					for (Entry<String, RepositoryItem> entry : promotions.entrySet()) {
						appliedCpnListVO = new AppliedCouponListVO();
			        	RepositoryItem repositoryItem=entry.getValue();
			        	String displayName=(String)repositoryItem.getPropertyValue(BBBCoreConstants.COUPON_DISPLAY_NAME);
			        	String description=(String)repositoryItem.getPropertyValue(BBBCoreConstants.COUPON_DESCRIPTION);
			        	//String exclusion=(String)repositoryItem.getPropertyValue(BBBCoreConstants.TERMS_N_CONDITIONS_PROMOTIONS);
			        	String exclusion = this.getPromotionTools().fetchExclusionText(entry.getKey(),site);
			        	boolean selected=false;//(String)repositoryItem.getPropertyValue(BBBCheckoutConstants.BBBCOUPONS);    			
						for(RepositoryItem couponItem : profileSelPromos){
							if(couponItem != null && couponItem.getRepositoryId().equalsIgnoreCase(entry.getKey())){
								vlogDebug(LogMessageFormatter.formatMessage(pRequest, entry.getKey() + " coupon is already selected"));
				                selected=true;
							}
						}
						 pRequest.setParameter(BBBCoreConstants.SELECT, selected); 
			        	/*if(profileSelPromos.contains(entry.getValue())) {
			                vlogDebug(LogMessageFormatter.formatMessage(pRequest, entry.getKey() + " coupon is already selected"));
			                pRequest.setParameter(BBBCoreConstants.SELECT, Boolean.TRUE);  
			            } else {
			                vlogDebug(LogMessageFormatter.formatMessage(pRequest, entry.getKey() + " coupon is not selected"));
			                pRequest.setParameter(BBBCoreConstants.SELECT, Boolean.FALSE);
			            }*/
			          	pRequest.setParameter(BBBCoreConstants.INDEX, index++);
			          	pRequest.setParameter(BBBCoreConstants.KEY, entry.getKey());
			          	pRequest.setParameter(BBBCoreConstants.ITEM, entry.getValue());
			            //repositoryItem.getItemDisplayName()
			            appliedCpnListVO.setCouponId(pRequest.getParameter(BBBCoreConstants.KEY).toString());
			            appliedCpnListVO.setSelected(Boolean.parseBoolean(pRequest.getParameter(BBBCoreConstants.SELECT)));
			            appliedCpnListVO.setIndex(pRequest.getParameter(BBBCoreConstants.INDEX));
			            appliedCpnListVO.setDisplayName(displayName);
			            appliedCpnListVO.setCouponsExclusions(exclusion);
			            appliedCpnListVO.setDescription(description);
			            if(mCouponList!=null && !mCouponList.isEmpty()){
							for(CouponListVo item:mCouponList){
								if((item.getEntryCd()).equalsIgnoreCase(entry.getKey())){
									try {
										appliedCpnListVO.setExpiryDate(displayFormat.format(formatter.parse(item.getExpiryDate())));
									} catch (ParseException e) {
										logError("Error occured while parsing date", e);
									}
								}
							}
					   	}
			            //appCpnListVO.add(appliedCpnListVO);
			            pRequest.setParameter(APPLIED_COUPON_VO, appliedCpnListVO);
			            pRequest.setParameter(BBBCoreConstants.EXCLUSION_TEXT, exclusion);
			            pRequest.serviceParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
					}
				}
			}
		} catch (BBBSystemException e) {
			vlogError("BBBSystemException occurred while getting coupons based on email or mobile :: "+e);
		} catch (BBBBusinessException e) {
			vlogError("BBBBusinessException occurred while getting coupons based on email or mobile :: "+e);
		}
		vlogDebug("TBSEmailCouponsDroplet :: service() method to get the email and phone based coupons :: END");
	}

	public BBBPromotionTools getPromotionTools() {
		return promotionTools;
	}

	public void setPromotionTools(BBBPromotionTools promotionTools) {
		this.promotionTools = promotionTools;
	}

	public TBSProfileTools getProfileTools() {
		return profileTools;
	}

	public void setProfileTools(TBSProfileTools profileTools) {
		this.profileTools = profileTools;
	}
}
