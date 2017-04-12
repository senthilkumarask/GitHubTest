package com.bbb.commerce.order.droplet;



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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.vo.CouponListVo;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.checkout.util.BBBCouponUtil;
import com.bbb.commerce.order.manager.PromotionLookupManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.rest.checkout.vo.AppliedCouponListVO;
import com.bbb.utils.BBBUtility;

/**
 * Servlet class gets all coupons for user and helps rendering coupons on 
 * the front end.
 * @author nagarg
 *
 */
public class UserCouponWalletDroplet extends BBBDynamoServlet {
    private static final String PROFILE_NULL = "Profile Null";
	private static final String ERROR_MSG = "errorMsg";
	private PromotionLookupManager mPromoManger;
    private BBBCouponUtil mCouponUtil;
    private BBBCatalogTools catalogTools;
    private BBBPromotionTools promTools;
    //private String mMediaItmKey;
    private LblTxtTemplateManager lblTxtTemplateManager; 
	private String exclusionRuleQuery;
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
	
    public LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.lblTxtTemplateManager;
	}
	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

    
    public final PromotionLookupManager getPromoManger() {
        return this.mPromoManger;
    }
    public final void setPromoManger(final PromotionLookupManager promoManger) {
        this.mPromoManger = promoManger;
    }
	public BBBCouponUtil getCouponUtil() {
		return this.mCouponUtil;
	}
	public void setCouponUtil(final BBBCouponUtil pCouponUtil) {
		this.mCouponUtil = pCouponUtil;
	}
	
    @SuppressWarnings("unchecked")
	@Override
    public void service(final DynamoHttpServletRequest req,
           final DynamoHttpServletResponse res) throws ServletException, IOException {
         Map<String, RepositoryItem> promotions = null;
         boolean isFromBillingpage = false;
         List<CouponListVo> couponList =new ArrayList<CouponListVo>();
        final Profile profile = (Profile) req.getObjectParameter(BBBCoreConstants.PROFILE);
        final BBBOrderImpl order = (BBBOrderImpl) req.getObjectParameter(BBBCoreConstants.ORDER);
        final String site = (String) req.getObjectParameter(BBBCoreConstants.SITE); 
        final String cartCheck = (String) req.getObjectParameter("cartCheck");  
        if(BBBUtility.isEmpty(cartCheck)){
        	isFromBillingpage = true;
        }
        if(profile == null 
                || order == null) {
        	req.setParameter(ERROR_MSG,PROFILE_NULL);
            return;
        }
        TransactionManager tm = getPromTools().getPricingTools().getTransactionManager();
    	TransactionDemarcation td = new TransactionDemarcation();
        boolean pRollback = false;
		try {
			td.begin(tm, TransactionDemarcation.REQUIRED);
            synchronized (order) {
            DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
            String couponMailId =  (String)pRequest.getSession().getAttribute("couponMailId");
            if(order.getCouponMap().size() > 0 && profile.isTransient() && !BBBUtility.isEmpty(couponMailId)) {
	                logDebug(LogMessageFormatter.formatMessage(req, "getting coupons from order skipping webservice call service"));
	                promotions = order.getCouponMap();
	                couponList = order.getCouponListVo();
            } else if(!profile.isTransient() || (profile.isTransient() && BBBUtility.isEmpty(couponMailId))) {
					/**
					 * This block gets executed only for authenticated user, For
					 * anonymous user this logic gets executed in
					 * BBBBillingAddressFormHandler postSaveBillingAddress() method.
					 */
	
	                logDebug(LogMessageFormatter.formatMessage(req, "getting coupons from web service"));
	                couponList=getPromoManger().getCouponList(profile, order, site, isFromBillingpage);
	                order.setCouponListVo(couponList);
	                promotions = getPromoManger().fillPromoList(site,couponList);
	            }
	            
	            promotions = getCouponUtil().applySchoolPromotion(promotions, profile, order);
	            order.setCouponMap(promotions);//setting the coupons map in order for later                
	            
	
				if (promotions != null) {
					logDebug(LogMessageFormatter.formatMessage(req,
								"promotions found " + promotions.size()));		
					promotions=sortedPromotions(promotions);
					//Added code for My offers Automation.. To get all the applied coupons form the order
					final List<RepositoryItem> profileSelCoupons = this.getPromTools().getCouponListFromOrder(order); 
					boolean isUpdateOrderRequired = processPromotions(req, res, promotions, profileSelCoupons,
							profile,couponList, order);
					if(isUpdateOrderRequired){
						getPromTools().getPricingTools().getOrderManager().updateOrder(order);
					}else{
						if (promotions.isEmpty()){
							req.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, req,
									res);							
						}
						return;	
					}
				}
            }
        } catch (BBBBusinessException e) {
            logError(LogMessageFormatter.formatMessage(req, "Error getting coupon promotions"), e);
            req.setParameter(ERROR_MSG,BBBCatalogErrorCodes.PROMOTIONID_DOES_NOT_EXIST_FOR_GIVEN_COUPONID);
            pRollback = true;
            return;            
        }catch (BBBSystemException e) {
            logError(LogMessageFormatter.formatMessage(req, "Error getting coupon promotions"), e);
            req.setParameter(ERROR_MSG,BBBCatalogErrorCodes.PROMOTIONID_DOES_NOT_EXIST_FOR_GIVEN_COUPONID);
            pRollback = true;
            return;            
        } catch (CommerceException e) {
        	logError(LogMessageFormatter.formatMessage(req, "Error updating order"), e);
            req.setParameter(ERROR_MSG,BBBCatalogErrorCodes.COMMERCE_EXCEPTION);
            pRollback = true;
            return; 
		} catch (TransactionDemarcationException e) {
			logError(LogMessageFormatter.formatMessage(req, "Error creating tramsaction"), e);
            req.setParameter(ERROR_MSG,BBBCatalogErrorCodes.TRANSACTION_DEMARCATION_EXCEPTION);
            pRollback = true;
            return; 
		}finally{
			try {
				td.end(pRollback);
			} catch (TransactionDemarcationException e) {
				logError(LogMessageFormatter.formatMessage(req, "Error commiting transaction"), e);
	            req.setParameter(ERROR_MSG,BBBCatalogErrorCodes.TRANSACTION_DEMARCATION_EXCEPTION);
	            return; 
			}
		}
    }



	public Map<String, RepositoryItem> sortedPromotions(Map<String, RepositoryItem> promotions){
	Map<String, RepositoryItem> sortedMap = new HashMap<String, RepositoryItem>();
	sortedMap.putAll(promotions);
  	return promotions;
    }
    
    
	/**
	 * This method is used for getting selected coupons from profile and update
	 * it to email and school coupons property to show in cart page.
	 * 
	 * @param req
	 * @param res
	 * @param promotions
	 * @param profileSelCoupons
	 * @param profile
	 * @param mCouponList
	 * @param order
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean processPromotions(final DynamoHttpServletRequest req,
            final DynamoHttpServletResponse res,
            final Map<String, RepositoryItem> promotions,
            final List<RepositoryItem> profileSelCoupons, final Profile profile, final List<CouponListVo> mCouponList, BBBOrderImpl order) throws ServletException, IOException, BBBSystemException {
    	boolean isUpdateOrderRequired = false;
    	final List selPromotions = (List) profile.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST);
        selPromotions.clear();
        DateFormat displayFormat ;
        String siteId = getSiteId();
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
			displayFormat = new SimpleDateFormat("dd/MM/yyyy");
		}else{
			displayFormat = new SimpleDateFormat("MM/dd/yyyy");
		}
		
		DateFormat wsFormatter = new SimpleDateFormat(BBBCoreConstants.WS_FORMAT_COUPONS);
		Timestamp wsExpDate =null;
		Timestamp promoTimestamp=null;
		Timestamp couponTimestamp=null;
		
        if(promotions.isEmpty()) {
         logDebug(LogMessageFormatter.formatMessage(req, "No promotions found"));
            return false;               
        }
        
        req.serviceParameter(BBBCoreConstants.OUTPUT_START, req, res);
        List<AppliedCouponListVO> onlineCoupons  =new ArrayList<AppliedCouponListVO>();
		List<AppliedCouponListVO> useAnywhereCoupons =new ArrayList<AppliedCouponListVO>();
		List<AppliedCouponListVO> inStoreCoupons =new ArrayList<AppliedCouponListVO>();
		List<AppliedCouponListVO> expRedeemedCoupons =new ArrayList<AppliedCouponListVO>();
		boolean schoolCouponApplied = false;
        String schoolCouponCode = BBBCoreConstants.BLANK;
		for (Entry<String, RepositoryItem> entry : promotions.entrySet()) {
        	AppliedCouponListVO appliedCpnListVO = new AppliedCouponListVO();
        	final RepositoryItem promoItem = entry.getValue();
        	final String displayName=(String)promoItem.getPropertyValue("displayName");
        	final String description=(String)promoItem.getPropertyValue("description");       				
        	int redemptionCountInt=0;
    		int redemptionLimitInt=1;
        	String couponCode = entry.getKey();
			if(mCouponList!=null && !mCouponList.isEmpty()){
            	for(CouponListVo item:mCouponList){
            		if((item.getEntryCd()).equalsIgnoreCase(couponCode)){
            			appliedCpnListVO.setRedemptionChannel(item.getRedemptionChannel());
            			appliedCpnListVO.setUniqueCouponCd(item.getUniqueCouponCd());
            			appliedCpnListVO.setExpiryDate(item.getExpiryDate());
            			if((item.getRedemptionCount()!=null && !(item.getRedemptionCount()).isEmpty() )&& (item.getRedemptionLimit()!=null && !(item.getRedemptionLimit()).isEmpty())){
            			appliedCpnListVO.setLastRedemptionDate(item.getLastRedemptionDate());
            			redemptionCountInt = Integer.parseInt(item.getRedemptionCount());
            			redemptionLimitInt = Integer.parseInt(item.getRedemptionLimit());
            			}
            		}
            	}
            	}
        	
        	String wsExpirationDate = appliedCpnListVO.getExpiryDate();
			if (wsExpirationDate != null) {
				try {
					wsExpDate = new Timestamp((wsFormatter.parse(wsExpirationDate)).getTime());
				} catch (ParseException e) {
					vlogError(
							"UserCouponWalletDroplet.processPromotions: Exception occured while parsing expiration date {0} and exception message is {1}",
							wsExpirationDate, e.getMessage());
				}
			}
    		
    		Object endUsableDate = promoItem.getPropertyValue(BBBCoreConstants.END_USABLE);
            if(endUsableDate != null && endUsableDate != BBBCoreConstants.BLANK){
    		promoTimestamp = ((Timestamp)endUsableDate);
            }
            if(wsExpDate!=null && promoTimestamp!=null && (wsExpDate.before(promoTimestamp)) )
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
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			java.util.Calendar sysDate = java.util.Calendar.getInstance();
			if(couponTimestamp != null){
				calendar.setTimeInMillis(couponTimestamp.getTime());
				appliedCpnListVO.setExpiryDate(wsFormatter.format(calendar.getTime()));
				calendar.add(Calendar.HOUR, -3);
				calendar.add(Calendar.MINUTE, -1);
				appliedCpnListVO.setDisplayExpiryDate(wsFormatter.format(calendar.getTime()));
			}
			
			int daysDiff = (int)(Math.ceil((calendar.getTimeInMillis()-sysDate.getTimeInMillis())/(float)(24*3600*1000)));
			appliedCpnListVO.setExpiryCount(daysDiff);

			//Added code for My offers Automation .. Iterate over the applied coupons in the order to display them as applied on front end
			boolean selected = false;    			
			for (RepositoryItem couponItem : profileSelCoupons) {
				if (null == couponItem) {
					vlogDebug("UserCouponWalletDroplet.processPromotions: Coupons is null");
					continue;
				}
				String schoolPromoId = (String) profile.getPropertyValue(BBBCoreConstants.SCHOOL_PROMOTIONS);
				if (couponCode.equalsIgnoreCase(couponItem.getRepositoryId())) {
					vlogDebug("UserCouponWalletDroplet.processPromotions: Coupon code {0} is already selected",
							couponCode);
					selPromotions.add(couponCode);
					selected = true;
				} else if (null != schoolPromoId && BBBCheckoutConstants.SCHOOLPROMO.equals(couponCode)) {
					Set<RepositoryItem> promotionList = (Set<RepositoryItem>) couponItem
							.getPropertyValue(getPromoManger().getCouponUtil().getClaimableManager()
									.getClaimableTools().getPromotionsPropertyName());
					if (null == promotionList || promotionList.isEmpty()) {
						vlogDebug(
								"UserCouponWalletDroplet.processPromotions: There is no coupons associated to the coupon {0}",
								couponItem);
						continue;
					} else {
						Iterator<RepositoryItem> promoIterator = promotionList.iterator();
						RepositoryItem selectedPromotion = promoIterator.next();
						if (schoolPromoId.equalsIgnoreCase(selectedPromotion.getRepositoryId())) {
							vlogDebug(
									"UserCouponWalletDroplet.processPromotions: Coupon code {0} is already selected, Promotion {1} is school promotion for school id {2}",
									couponCode, schoolPromoId, profile.getPropertyValue(BBBCoreConstants.SCHOOL_IDS));
							selPromotions.add(couponCode);
							selected = true;
							schoolCouponApplied = true;
							schoolCouponCode = couponItem.getRepositoryId();
						}
					}

				}
			}
    		appliedCpnListVO.setSelected(selected);  
            appliedCpnListVO.setCouponId(couponCode);
            appliedCpnListVO.setDisplayName(displayName);
            logDebug("UserCouponWallet droplet fetching exclusion text for : couponCode " + couponCode + " siteId " + siteId);	
            appliedCpnListVO.setCouponsExclusions(this.getPromTools().fetchExclusionText(couponCode, siteId));
            appliedCpnListVO.setDescription(description);
            appliedCpnListVO.setPromoId((String) promoItem.getPropertyValue(BBBCoreConstants.ID));
            Map mediaMap = (Map) promoItem.getPropertyValue(BBBCoreConstants.MEDIA);
    		if(mediaMap != null){
    			RepositoryItem mediaIteam = (RepositoryItem) mediaMap.get("mainImage");
    			if(mediaIteam != null){
    				appliedCpnListVO.setCouponsImageUrl((String) (mediaIteam.getPropertyValue(BBBCoreConstants.URL) != null ? mediaIteam.getPropertyValue(BBBCoreConstants.URL):"") );
    			}
    		}
    		
    		Date todaysDate = new Date();
			String expiry =  appliedCpnListVO.getExpiryDate();
			Date expiryDate =null;
			try {
				if (!BBBUtility.isEmpty(expiry)) {
					expiryDate = wsFormatter.parse(expiry);
				}
				if ((redemptionCountInt >= redemptionLimitInt) || (expiryDate != null && todaysDate.after(expiryDate))) {
					appliedCpnListVO = changeDateFormat(appliedCpnListVO, displayFormat, wsFormatter);
					expRedeemedCoupons.add(appliedCpnListVO);
				} else if (appliedCpnListVO.getRedemptionChannel() != null
						&& appliedCpnListVO.getRedemptionChannel().equalsIgnoreCase("2")) {
					appliedCpnListVO = changeDateFormat(appliedCpnListVO, displayFormat, wsFormatter);
					onlineCoupons.add(appliedCpnListVO);
				} else if (appliedCpnListVO.getRedemptionChannel() != null
						&& appliedCpnListVO.getRedemptionChannel().equalsIgnoreCase("1")) {
					appliedCpnListVO = changeDateFormat(appliedCpnListVO, displayFormat, wsFormatter);
					inStoreCoupons.add(appliedCpnListVO);
				} else if (appliedCpnListVO.getRedemptionChannel() != null
						&& appliedCpnListVO.getRedemptionChannel().equalsIgnoreCase("3")) {
					appliedCpnListVO = changeDateFormat(appliedCpnListVO, displayFormat, wsFormatter);
					useAnywhereCoupons.add(appliedCpnListVO);
				} else if (BBBCheckoutConstants.SCHOOLPROMO.equals(appliedCpnListVO.getCouponId())) {
					appliedCpnListVO = changeDateFormat(appliedCpnListVO, displayFormat, wsFormatter);
					onlineCoupons.add(appliedCpnListVO);
				}
			} catch (ParseException e) {
				vlogError(
						"UserCouponWalletDroplet.processPromotions: Exception occured while parsing expiration date {0} and exception message is {1}",
						expiry, e.getMessage());
			}

        }
		/* This block checks whether a school promotion is applied or not. */
		if (schoolCouponApplied) {
			order.setSchoolCoupon(schoolCouponCode);
			vlogDebug("UserCouponWalletDroplet.processPromotions: School promoiton is applied, coupon id is {0}.",
					schoolCouponCode);
			isUpdateOrderRequired = true;
		} else if(!BBBUtility.isEmpty(order.getSchoolCoupon())){
			order.setSchoolCoupon(null);
			vlogDebug("UserCouponWalletDroplet.processPromotions: School promoiton is not applied.");
			isUpdateOrderRequired = true;
		}
        Collections.sort(onlineCoupons, new UserCouponsComparator());
		Collections.sort(useAnywhereCoupons, new UserCouponsComparator());
		
		req.setParameter("onlineCouponList", onlineCoupons);
		req.setParameter("useAnywhereCouponList", useAnywhereCoupons);
        req.serviceParameter(BBBCoreConstants.OUTPUT, req, res);
        req.serviceParameter(BBBCoreConstants.OUTPUT_END, req, res);
        return isUpdateOrderRequired;
      }


	protected String getSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
	
	
	private AppliedCouponListVO changeDateFormat(AppliedCouponListVO couponListVo,DateFormat displayFormat, DateFormat formatter){

		String expirationDate = couponListVo.getExpiryDate();
		String displayExpirationDate = couponListVo.getDisplayExpiryDate();
		String lastRedeemptionDate = couponListVo.getLastRedemptionDate();
		try {
			if(expirationDate!=null && !StringUtils.isEmpty(expirationDate)){
			couponListVo.setExpiryDate(displayFormat.format(formatter.parse(expirationDate)));
			}
			if(displayExpirationDate!=null && !StringUtils.isEmpty(displayExpirationDate)){
				couponListVo.setDisplayExpiryDate(displayFormat.format(formatter.parse(displayExpirationDate)));
				}
			if(lastRedeemptionDate != null && !StringUtils.isEmpty(lastRedeemptionDate)){
			couponListVo.setLastRedemptionDate(displayFormat.format(formatter.parse(lastRedeemptionDate)));
			}
		} catch (ParseException e) {
			vlogError(
					"UserCouponWalletDroplet.changeDateFormat: Exception occured while parsing expiration date {0} and exception message is {1}",
					expirationDate, e.getMessage());
		}
		return couponListVo;
	}


	public BBBPromotionTools getPromTools() {
		return promTools;
	}


	public void setPromTools(BBBPromotionTools promTools) {
		this.promTools = promTools;
	}
        
    }

