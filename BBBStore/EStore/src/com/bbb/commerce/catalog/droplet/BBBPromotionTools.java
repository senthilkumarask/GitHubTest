package com.bbb.commerce.catalog.droplet;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.vo.CouponListVo;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.checkout.util.BBBCouponUtil;
import com.bbb.commerce.order.droplet.UserCouponsComparator;
import com.bbb.commerce.order.manager.PromotionLookupManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.rest.checkout.vo.AppliedCouponListVO;
import com.bbb.rest.checkout.vo.AppliedCouponsVO;
import com.bbb.utils.BBBUtility;

public class BBBPromotionTools extends PromotionTools{
	private static final String TRUE = "true";
	private String mQuery;
	private LblTxtTemplateManager mLblTxtTemplate;
	private String mMediaItmKey;
	private PromotionLookupManager promoManager;
	private BBBCouponUtil couponUtil;
	private String exclusionRuleQuery;
	private BBBCatalogToolsImpl mCatalogTools;
	
	private String couponByPromotionQuery;
	private BBBCatalogTools catalogTools;
	
	/** @return */
    public final BBBCatalogTools getCatalogUtil() {
        return this.catalogTools;
    }

    /** @param catalogUtil */
    public final void setCatalogUtil(final BBBCatalogTools catalogUtil) {
        this.catalogTools = catalogUtil;
    }

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
	 * @return the lblTxtTemplate
	 */
	public LblTxtTemplateManager getLblTxtTemplate() {
		return mLblTxtTemplate;
	}

	/**
	 * @param pLblTxtTemplate the lblTxtTemplate to set
	 */
	public void setLblTxtTemplate(LblTxtTemplateManager pLblTxtTemplate) {
		mLblTxtTemplate = pLblTxtTemplate;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return mQuery;
	}

	/**
	 * @param pQuery the query to set
	 */
	public void setQuery(String pQuery) {
		mQuery = pQuery;
	}
	
	/**
	 * @return the promoManager
	 */
	public PromotionLookupManager getPromoManager() {
		return promoManager;
	}

	/**
	 * @param promoManager the promoManager to set
	 */
	public void setPromoManager(PromotionLookupManager promoManager) {
		this.promoManager = promoManager;
	}
	
	/**
	 * @return the couponUtil
	 */
	public BBBCouponUtil getCouponUtil() {
		return couponUtil;
	}

	/**
	 * @param couponUtil the couponUtil to set
	 */
	public void setCouponUtil(BBBCouponUtil couponUtil) {
		this.couponUtil = couponUtil;
	}

	/**
	 * This method will fetch the ACTIVATIONLABELID or ACTIVATEDLABELID property
	 * value of the media item from promotion whose key matches with the
	 * mMediaItmKey.Also it checks whether the promotion belongs the current
	 * site.If isExpiryDateRequired is true then this method will return endDate
	 * of promotion. If isExpiryDateRequired is true then the expired date and
	 * time is returned in cmsKey
	 * 
	 * @param pPromId
	 * @param pProperty
	 * @param pCurrSite
	 * @param isExpiryDateRequired
	 * @param pMediaItmKey
	 * @return cmsValue
	 */
	public String getPromotionCouponKey(String pPromId, String pProperty,
			String pCurrSite, String pLocale, boolean isExpiryDateRequired) {
		logDebug("BBBPromotionTools.getPromotionCouponKey() method Starts");
		logDebug("pPromId =" + pPromId + ":pProperty =" + pProperty
				+ ":pCurrSite =" + pCurrSite + ":pLocale =" + pLocale
				+ ":isExpiryDateRequired =" + isExpiryDateRequired);
		String cmsValue = null;
		if (pPromId != null
				&& pCurrSite != null
				&& pProperty != null
				&& (pProperty.equalsIgnoreCase(BBBCoreConstants.ACTIVATION_LABEL_ID) || pProperty.equalsIgnoreCase(BBBCoreConstants.ACTIVATED_LABEL_ID))) {
			Repository repos = getPromos();
			try {
				RepositoryView promotionView = repos
						.getView(BBBCoreConstants.PROMOTION);
				RqlStatement statement = RqlStatement.parseRqlStatement(mQuery);
				Object[] param = new Object[BBBCoreConstants.ONE];
				param[BBBCoreConstants.ZERO] = pPromId;
				RepositoryItem[] promotionItem = executeDBQuery(promotionView, statement, param);
				if (promotionItem != null
						&& promotionItem.length == BBBCoreConstants.ONE) {
					Object siteItemObj = promotionItem[BBBCoreConstants.ZERO]
							.getPropertyValue(BBBCoreConstants.SITES);
					boolean isValidSite = false;
					if (siteItemObj != null) {
						Set<RepositoryItem> siteItems = (Set<RepositoryItem>) siteItemObj;
						Iterator<RepositoryItem> itr = siteItems.iterator();

						while (itr.hasNext()) {
							if (((String) itr.next().getPropertyValue(
									BBBCoreConstants.ID))
									.equalsIgnoreCase(pCurrSite)) {
								isValidSite = true;
							}
						}

					}
					if (!isValidSite) {
						logDebug("Promotion dosn't belong to this site");
						logDebug("BBBPromotionTools.getPromotionCouponKey() method ends");
						return null;
					}
					if (isExpiryDateRequired) {
						Date endDate = (Date) promotionItem[BBBCoreConstants.ZERO]
								.getPropertyValue(BBBCoreConstants.END_USABLE);
						if (endDate!=null) {
							String time = new SimpleDateFormat(
									BBBCoreConstants.TIME_FORMAT_COUPON)
									.format(endDate);
							String date = new SimpleDateFormat(
									BBBCoreConstants.DATE_FORMAT_COUPON)
									.format(endDate);
	
							cmsValue = time + BBBCoreConstants.ON_STRING + date;
						} else {
							cmsValue = BBBCoreConstants.EXPIRED_DATE;
						}
					} else {
						cmsValue = (String) promotionItem[BBBCoreConstants.ZERO]
								.getPropertyValue(pProperty);
							
							if (cmsValue == null) {
								cmsValue = BBBCoreConstants.BLANK;
								logDebug("Value not Found for  " + pProperty);
							}
					}
				} else {
					logDebug("Coupon Exist in more than two promotion");
				}
			} catch (RepositoryException e) {
				logError("catalog_1067 : Error while retriving data from repository", e);
			}

		}
		logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = "
				+ cmsValue);
		return cmsValue;
	}

	protected RepositoryItem[] executeDBQuery(RepositoryView promotionView,
			RqlStatement statement, Object[] param) throws RepositoryException {
		return statement.executeQuery(
				promotionView, param);
	}

	protected Repository getPromos() {
		return (Repository)getPromotions();
	}
	
	
	public RepositoryItem getPromotionById(String pPromId) {
		logDebug("BBBPromotionTools.getPromotionById() method Starts");
		RepositoryItem[] promotionItem =null;
		if (pPromId != null) {
			Repository repos = getPromos();
			try {
				RepositoryView promotionView = repos
						.getView(BBBCoreConstants.PROMOTION);
				RqlStatement statement = RqlStatement.parseRqlStatement(mQuery);
				Object[] param = new Object[BBBCoreConstants.ONE];
				param[BBBCoreConstants.ZERO] = pPromId;
				promotionItem = executeDBQuery(promotionView, statement, param);
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logError(e.getMessage(),e);
			}
			}
		if(promotionItem!=null)
			return promotionItem[0];
		else
			return null;
	}
	
	/**
	 * Get all the applied coupons for a profile
	 * externalized from UserCouponWalletDroplet as it is.
	 * @param order
	 * @param profile
	 * @param couponFlag 
	 * @param siteId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AppliedCouponsVO getAppliedCoupons(BBBOrderImpl order, Profile profile, boolean couponFlag){

		final AppliedCouponsVO appliedCouponsVO = new AppliedCouponsVO();
		String siteId = extractCurrentSiteId();
		Map<String, RepositoryItem> promotions = null;
		List<CouponListVo> couponList =new ArrayList<CouponListVo>();
		promotions = order.getCouponMap();
		couponList = order.getCouponListVo();
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		String channel = BBBUtility.getChannel();
		boolean isFirstCouponCallMade = false;
		if(!BBBUtility.isEmpty(channel) && (BBBCoreConstants.MOBILEWEB.equals(channel) || BBBCoreConstants.MOBILEAPP.equals(channel))){
			isFirstCouponCallMade = isFirstCouponWebServiceCallMade(pRequest,profile);
		}
		if(null!= profile){
			try {
				if(order.getCouponMap() != null && order.getCouponMap().size() > 0 && !couponFlag) {
					if(isLoggingDebug()) {
						logDebug("getting coupons from order, skipping webservice call service");
					}
					promotions = order.getCouponMap();
					couponList = order.getCouponListVo();
				} else if(!profile.isTransient() && !isFirstCouponCallMade) {
					/**
					 * This block gets executed only for authenticated user, For
					 * anonymous user this logic gets executed in
					 * BBBBillingAddressFormHandler postSaveBillingAddress() method.
					 */
					if(isLoggingDebug()) {
						logDebug("getting coupons from web service");
					}
					couponList = getPromoManager().getCouponList(profile, order, siteId, false);
					order.setCouponListVo(couponList);
	                promotions = getPromoManager().fillPromoList(siteId,couponList);
			        if(null!= profile.getPropertyValue(BBBCoreConstants.EMAIL)){
			         	String email = (String)profile.getPropertyValue(BBBCoreConstants.EMAIL);
			           	pRequest.getSession().setAttribute("couponMailId", email);
			        }
				}
	
				promotions = getCouponUtil().applySchoolPromotion(promotions, profile, order);
				order.setCouponMap(promotions);//setting the coupons map in order for later                
				populateAppliedCouponVO(order, profile, siteId, appliedCouponsVO, promotions, couponList);
			}
			catch (BBBBusinessException e) {
				logError("Error getting coupon promotions", e);           
			}catch (BBBSystemException e) {
				logError( "Error getting coupon promotions", e);            
			}
		}
		return appliedCouponsVO;
	}

	protected String extractCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	/**
	 * To check if first coupon web service call for profile 
	 * @param pRequest
	 * @param profile
	 * @return
	 */
	private boolean isFirstCouponWebServiceCallMade(DynamoHttpServletRequest pRequest, Profile profile){
		
		boolean isFirstCouponCallMade = false;
		String emailFromSession = BBBCoreConstants.BLANK;
		String emailFromProfile = BBBCoreConstants.BLANK;
		if(null != pRequest.getSession() & null != pRequest.getSession().getAttribute("couponMailId")){
			emailFromSession = (String)pRequest.getSession().getAttribute("couponMailId");
		}
		if(null != profile && null!= profile.getPropertyValue(BBBCoreConstants.EMAIL)){
			emailFromProfile = (String)profile.getPropertyValue(BBBCoreConstants.EMAIL);
		}
		if(!BBBUtility.isEmpty(emailFromSession) && !BBBUtility.isEmpty(emailFromProfile) && emailFromProfile.equals(emailFromSession)){
			isFirstCouponCallMade = true;
		}
		return isFirstCouponCallMade;
	}
	/**
	 * Populate applied coupons in the appliedCouponsVO
	 *
	 * @param order the order
	 * @param profile the profile
	 * @param siteId the site id
	 * @param appliedCouponsVO the applied coupons vo
	 * @param promotions the promotions
	 * @param couponList the coupon list
	 * @throws BBBSystemException 
	 */
	public void populateAppliedCouponVO(BBBOrderImpl order, Profile profile, String siteId,
			final AppliedCouponsVO appliedCouponsVO,
			Map<String, RepositoryItem> promotions,
			List<CouponListVo> couponList) throws BBBSystemException {
		int index = 0;
		int redemptionCountInt=0;
		int redemptionLimitInt=1;
		Timestamp wsExpDate =null;
		DateFormat formatter;
		//Timestamp promoTimestamp=null;
		Timestamp couponTimestamp=null;
		if(isLoggingDebug()) {
			logDebug("populateAppliedCouponVO start");
		}
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
			formatter = new SimpleDateFormat("dd/MM/yyyy");
		}else{
			formatter = new SimpleDateFormat("MM/dd/yyyy");
		}
		
		DateFormat wsFormatter = new SimpleDateFormat(BBBCoreConstants.WS_FORMAT_COUPONS);
		if (promotions != null) {
			if (isLoggingDebug()) {
				logDebug("promotions found::" + promotions.size());
			}
			if (promotions.isEmpty()) {
				if (isLoggingDebug()) {
					logDebug("no promotions return");
				}
				return;
			}
			//Added code for My offers Automation .. Get all the applied coupons in the order
			final List<RepositoryItem> profileSelCoupons = this.getCouponListFromOrder(order); 
			/* new ArrayList<RepositoryItem>();
			getPromoManager().getPromotionTools().getOrderPromotions(order,
					profileSelPromos, profileSelPromos,
					profileSelPromos, profileSelPromos, false);*/

			final List selPromotions = (List) profile.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST);
			selPromotions.clear();

			if(promotions.isEmpty()) {
				if(isLoggingDebug()) {
					logDebug( "No promotions found");
				}
				return;              
			}

			final List<AppliedCouponListVO> appCpnListVO= new ArrayList<AppliedCouponListVO>();
			for (Entry<String, RepositoryItem> entry : promotions.entrySet()) {
				final AppliedCouponListVO appliedCpnListVO = new AppliedCouponListVO();
				final RepositoryItem repositoryItem=entry.getValue();
				final String displayName=(String)repositoryItem.getPropertyValue("displayName");
				final String description=(String)repositoryItem.getPropertyValue("description");
				//final String exclusion=(String)repositoryItem.getPropertyValue(BBBCoreConstants.TERMS_N_CONDITIONS_PROMOTIONS);
				//(String)repositoryItem.getPropertyValue(BBBCheckoutConstants.BBBCOUPONS);  
				boolean selected = false;  			
				//Added code for My offers Automation .. Iterate over the applied coupons in the order to display them as applied on front end
				for(RepositoryItem couponItem : profileSelCoupons){
					if(couponItem != null && couponItem.getRepositoryId().equalsIgnoreCase(entry.getKey())){
						if(isLoggingDebug()) {
							logDebug(entry.getKey() + ":: coupon is already selected");
						} 
		                selPromotions.add(entry.getKey());
		                selected = true;
					}
				}
	    		appliedCpnListVO.setSelected(selected);  
				/*if(profileSelPromos.contains(entry.getValue())) {
					if(isLoggingDebug()) {
						logDebug(entry.getKey() + ":: coupon is already selected");
					}
					appliedCpnListVO.setSelected(true);
					selPromotions.add(entry.getKey());
				} else {
					if(isLoggingDebug()) {
						logDebug(entry.getKey() + ":: coupon is not selected");
					}
					appliedCpnListVO.setSelected(false);
				}*/
				String couponCode =entry.getKey();// (String)repositoryItem.getPropertyValue(BBBCheckoutConstants.BBBCOUPONS);
			
				appliedCpnListVO.setCouponId(entry.getKey());
				appliedCpnListVO.setIndex(Integer.toString(index++));
				appliedCpnListVO.setDisplayName(displayName);
				appliedCpnListVO.setCouponsExclusions(this.fetchExclusionText(couponCode,siteId));
				appliedCpnListVO.setCouponExclusionsMessage(this.fetchExclusionText(couponCode,siteId));
				appliedCpnListVO.setDescription(description);
				appliedCpnListVO.setPromoId((String) repositoryItem.getPropertyValue(BBBCoreConstants.ID));
				Map mediaMap = (Map) repositoryItem.getPropertyValue(BBBCoreConstants.MEDIA);
				if(mediaMap != null){

					RepositoryItem mediaIteam = (RepositoryItem) mediaMap.get("mainImage");

					if(mediaIteam != null){
						appliedCpnListVO.setCouponsImageUrl((String) (mediaIteam.getPropertyValue(BBBCoreConstants.URL) != null ? mediaIteam.getPropertyValue(BBBCoreConstants.URL):"") );
					}
				}
				if(!BBBUtility.isListEmpty(couponList)){
					for(CouponListVo item:couponList){
						if((item.getEntryCd()).equalsIgnoreCase(entry.getKey())){
							appliedCpnListVO.setRedemptionChannel(item.getRedemptionChannel());
							appliedCpnListVO.setUniqueCouponCd(item.getUniqueCouponCd());
							appliedCpnListVO.setExpiryDate(item.getExpiryDate());
							appliedCpnListVO.setLastRedemptionDate(item.getLastRedemptionDate());
							if(!BBBUtility.isEmpty(item.getRedemptionCount()) && !BBBUtility.isEmpty(item.getRedemptionLimit())){			            			
								redemptionCountInt = Integer.parseInt(item.getRedemptionCount());
								redemptionLimitInt = Integer.parseInt(item.getRedemptionLimit());
								appliedCpnListVO.setRedemptionCount(String.valueOf(redemptionCountInt));
								appliedCpnListVO.setRedemptionLimit(String.valueOf(redemptionLimitInt));
							}else{
								appliedCpnListVO.setRedemptionCount(String.valueOf(redemptionCountInt));
								appliedCpnListVO.setRedemptionLimit(String.valueOf(redemptionLimitInt));	
							}
						}
					}
				}
				String wsExpirationDate = appliedCpnListVO.getExpiryDate();

				if(wsExpirationDate !=null){
					try {
						wsExpDate = new Timestamp((wsFormatter.parse(wsExpirationDate)).getTime());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logError(e.getMessage(),e);
					}
				}
				Timestamp promoTimestamp=null;
				Object endUsableDate = repositoryItem.getPropertyValue(BBBCoreConstants.END_USABLE);
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
				calendar.setTimeInMillis((couponTimestamp != null) ? couponTimestamp.getTime() : null);
				appliedCpnListVO.setExpiryDate(formatter.format(calendar.getTime()));
				calendar.add(Calendar.HOUR, -3);
				calendar.add(Calendar.MINUTE, -1);
				appliedCpnListVO.setDisplayExpiryDate(formatter.format(calendar.getTime()));

				int daysDiff = (int)(Math.ceil((calendar.getTimeInMillis()-sysDate.getTimeInMillis())/(float)(24*3600*1000)));
				appliedCpnListVO.setExpiryCount(daysDiff);

				appCpnListVO.add(appliedCpnListVO);
			}

			if(appCpnListVO!=null && !appCpnListVO.isEmpty()){
				sortAppCpnListVO(appCpnListVO);
			}
			appliedCouponsVO.setAppliedCouponListVOs(appCpnListVO);
		} else {
			return;
		}
		if(isLoggingDebug()) {
			logDebug("populateAppliedCouponVO start");
		}
	}

	protected void sortAppCpnListVO(final List<AppliedCouponListVO> appCpnListVO) {
		Collections.sort(appCpnListVO, new UserCouponsComparator());
	}

    /**
     * Method to fetch all the applied coupons in an order for My offers Automation
     * @param order
     * @return List<RepositoryItem>
     */
	public List<RepositoryItem> getCouponListFromOrder(Order order) {
		List<RepositoryItem> coupons = new ArrayList<RepositoryItem>();
		vlogDebug("getCouponListFromOrder start, Order id: {0}", order.getId());
		// Add Order adjustments coupons
		getOrderAdjustmentsCouponsFromOrder(order, coupons);
		// Add item adjustments
		getItemAdjustmentsCouponsFromOrder(order, coupons);
		// Add shipping Adjustments
		List<ShippingGroup> shippingList = order.getShippingGroups();
		// iterate over each shipping group
		getShippingAdjustmentsCouponsFromOrder(coupons, shippingList);
		vlogDebug("getCouponListFromOrder end, with coupons: {0}", coupons);
		return coupons;
	}

	/**
	 * Get all coupons from order adjustments
	 * @param coupons
	 * @param shippingList
	 */
	private void getShippingAdjustmentsCouponsFromOrder(
			List<RepositoryItem> coupons, List<ShippingGroup> shippingList) {
		RepositoryItem coupon;
		for(ShippingGroup shippingGroup : shippingList){
			if(shippingGroup.getPriceInfo() != null){
				List<PricingAdjustment> shipAdjustments = shippingGroup.getPriceInfo().getAdjustments();
				// iterate over each adjustment in the shipping group
				for (PricingAdjustment pricingAdj : shipAdjustments) {
						coupon = pricingAdj.getCoupon();
		    			if(coupon != null){
		    				coupons.add(coupon);
		    				if(isLoggingDebug()) {
		    					logDebug("Added Coupon in the applied List" +coupon.getRepositoryId());
		    				}
		    			}
			}
			}
		}
	}

	/**
	 * Get all coupons from item adjustments
	 * @param order
	 * @param coupons
	 */
	private void getItemAdjustmentsCouponsFromOrder(Order order,
			List<RepositoryItem> coupons) {
		RepositoryItem coupon;
		List<CommerceItem> commItems = order.getCommerceItems();
		//iterate over the commerce items
		for(CommerceItem commerceItem : commItems){
			if( commerceItem.getPriceInfo() != null){
				List<PricingAdjustment> itemAdjustments = commerceItem.getPriceInfo().getAdjustments();
				// iterate over each adjustment in IPI of the commerce item 
				for (PricingAdjustment pricingAdj : itemAdjustments) {
							coupon = pricingAdj.getCoupon();
			    			if(coupon != null){
			    				coupons.add(coupon);
			    				logDebug("Added Coupon in the applied List" +coupon.getRepositoryId());
			    			}
				}
			}
		}
	}

	/**
	 * Get all coupons from shipping adjustments
	 * @param order
	 * @param coupons
	 */
	private void getOrderAdjustmentsCouponsFromOrder(Order order,
			List<RepositoryItem> coupons) {
		RepositoryItem coupon;
		//Add order adjustments
		if(order.getPriceInfo() != null){
	    	List<PricingAdjustment> orderAdjustment = order.getPriceInfo().getAdjustments();
	    	// iterate over each adjustment in order  
	    	for(PricingAdjustment pricingAdjustment : orderAdjustment){
	    			coupon=pricingAdjustment.getCoupon();
	    			if(coupon != null){
	    				coupons.add(coupon);
	    				logDebug("Added Coupon in the applied List" +coupon.getRepositoryId());
	    			}
	    	}
		}
	}

	
	/**
	 * Fetch exclusion text based on couponCode and siteId
	 *
	 * @param couponCode the couponCode
	 * @param site the site id
	 * @throws BBBSystemException 
	 */
	 public String fetchExclusionText(String couponCode,String site) throws BBBSystemException{
		 //Changing the logic to fetch exclusion text from coupon rule repository rather than 'tandc' field of pricingModels.xml for BBBP-5940
		 if(isLoggingDebug()) {
				logDebug(" Inside fetchExclusionText() for entryCode " + couponCode + " and siteId " + site);
			}
		 String exclusionText = null;
		 if(!BBBUtility.isEmpty(couponCode) && !BBBUtility.isEmpty(site)){
	    	final Object[] params = new Object[2];
			params[0] = couponCode;
			params[1] = site;
			RepositoryItem[] exclusionTextItem;
		
			exclusionTextItem = getCatalogTools().executeRQLQuery(this.getExclusionRuleQuery(), params,
					BBBCatalogConstants.EXCLUSION_RULES_ITEM_DESCRIPTOR, this.getCouponRepository());
				if ((exclusionTextItem != null) && (exclusionTextItem.length >0 )) {
					exclusionText = (String)exclusionTextItem[0].getPropertyValue(BBBCoreConstants.EXCLUSION_TEXT);
				}					
		 }
		 if(isLoggingDebug()) {
				logDebug(" Exclusion text is " + exclusionText);
			}
		 return exclusionText;
	    }
	 
	 
	/**
	 * This method does query to claimable repository to get list of coupons
	 * associated with a promotion.
	 * 
	 * @param promotion
	 * @return
	 * @throws RepositoryException
	 */
	public RepositoryItem[] getCoupons(RepositoryItem promotion) throws RepositoryException {
		vlogDebug("BBBPromotionTools.getCoupons: Starts, Promotion: {0}", promotion);
		if (null == promotion) {
			vlogDebug("BBBPromotionTools.getCoupons: Ends, Promotion is null");
			return null;
		}
		String promotionId = promotion.getRepositoryId();
		String param[] = { promotionId };
		RepositoryView claimableView = getCatalogTools().getClaimableTools().getClaimableRepository()
				.getItemDescriptor(getCatalogTools().getClaimableTools().getClaimableItemDescriptorName())
				.getRepositoryView();
		RqlStatement statement = RqlStatement.parseRqlStatement(getCouponByPromotionQuery());
		RepositoryItem claimableItem[] = statement.executeQuery(claimableView, param);
		if (null != claimableItem && claimableItem.length > 0) {
			vlogDebug("BBBCouponDetailsInitializer.getCoupons: Coupon id: {0} is associated with this promotion: {1}",
					claimableItem[0].getRepositoryId(), promotion);
			vlogDebug("BBBPromotionTools.getCoupons: Ends");
			return claimableItem;
		}
		vlogDebug("BBBPromotionTools.getCoupons: Ends");
		return null;
	}
	 
	public BBBCatalogToolsImpl getCatalogTools() {
		return mCatalogTools;
	}


	public void setCatalogTools(BBBCatalogToolsImpl catalogTools) {
		mCatalogTools = catalogTools;
	}


	/**
	 * @return the couponByPromotionQuery
	 */
	public String getCouponByPromotionQuery() {
		return couponByPromotionQuery;
	}


	/**
	 * @param couponByPromotionQuery the couponByPromotionQuery to set
	 */
	public void setCouponByPromotionQuery(String couponByPromotionQuery) {
		this.couponByPromotionQuery = couponByPromotionQuery;
	}
	
	/*
	 * removeAlreadyAppliedCoupons method is used from post login method.
	 * It removes the coupons which are already applied by 
	 * user because user will apply coupon again after login.
	 * @param alreadyAppliedCoupons
	 * @param profile
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 * 
	*/
	public void removeAlreadyAppliedCoupons(List<RepositoryItem> alreadyAppliedCoupons, Profile profile, String orderSiteId) throws ServletException, IOException{
		if(isLoggingDebug())
			this.logDebug("BBBPromotionTools : removeAlreadyAppliedCoupons starts...");
		BBBPerformanceMonitor.start("BBBPromotionTools", "removeAlreadyAppliedCoupons");
		
		try{
			String couponOn = null;
			final Map<String, String> configMap = this.getCatalogUtil().getConfigValueByconfigType(
	                "ContentCatalogKeys");
			
			 if (orderSiteId.equalsIgnoreCase(configMap.get("BedBathUSSiteCode"))) {
	             couponOn = this.getCatalogUtil().getContentCatalogConfigration("CouponTag_us").get(0);
	         } else if (orderSiteId.equalsIgnoreCase(configMap.get("BuyBuyBabySiteCode"))) {
	             couponOn = this.getCatalogUtil().getContentCatalogConfigration("CouponTag_baby").get(0);
	         } else if (orderSiteId.equalsIgnoreCase(configMap.get("BedBathCanadaSiteCode"))) {
	             couponOn = this.getCatalogUtil().getContentCatalogConfigration("CouponTag_ca").get(0);
	         }

	         if (couponOn == null) {
	             couponOn = TRUE;
	         }
			
			final List<RepositoryItem> availablePromotions = (List<RepositoryItem>) profile.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);
			final List<RepositoryItem> activePromotions = (List<RepositoryItem>) profile.getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS);
			final List<RepositoryItem> removeAvailablePromotions = new ArrayList<RepositoryItem>();
			
			if (couponOn.equalsIgnoreCase(TRUE)) {
				boolean isStoreOnlyCoupons = false;
				
				if(null != alreadyAppliedCoupons && !alreadyAppliedCoupons.isEmpty()){
					for (final Object element : alreadyAppliedCoupons) {
						final RepositoryItem couponToRemove = (RepositoryItem) element;
						
						RepositoryItem[] currentPromos = null;
						try {
							currentPromos = getCatalogTools().getPromotions(couponToRemove.getRepositoryId());
							if (null == currentPromos
									|| currentPromos.length <= 0
									|| !(currentPromos[0].getItemDescriptor().getItemDescriptorName()
											.equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT) || currentPromos[0]
											.getItemDescriptor().getItemDescriptorName()
											.equalsIgnoreCase(BBBCoreConstants.ORDER_DISCOUNT))) {
								vlogDebug("BBBPromotionTools: There is no promotions associated to the coupon {0}",
										couponToRemove);
								continue;
							}
						} catch (BBBSystemException e) {
							vlogError(
								BBBCoreErrorConstants.POST_LOGIN_COUPON_GENERIC_ERROR + " : BBBPromotionTools: BBBSystemException occurred while trying to fetch promotion from the coupon {0}",
									couponToRemove);
							BBBPerformanceMonitor.cancel("BBBPromotionTools", "removeAlreadyAppliedCoupons");
						} catch (BBBBusinessException e) {
							vlogError(
									BBBCoreErrorConstants.POST_LOGIN_COUPON_NULL + " :BBBPromotionTools: BBBBusinessException occurred while trying to fetch promotion from the coupon {0}",
									couponToRemove);
							BBBPerformanceMonitor.cancel("BBBPromotionTools", "removeAlreadyAppliedCoupons");
						} catch (RepositoryException e) {
							vlogError(
									BBBCoreErrorConstants.POST_LOGIN_REPO_EXCEPTION + " :BBBPromotionTools: RepositoryException occurred while trying to fetch promotion type from promotion {0}",
									currentPromos[0]);
							BBBPerformanceMonitor.cancel("BBBPromotionTools", "removeAlreadyAppliedCoupons");
						}
						
						vlogDebug("BBBPromotionTools: Promotions {0} found in associated coupon {1}", currentPromos, couponToRemove);
					
						if(null != currentPromos && null != currentPromos[0]){
							removeAvailablePromotions.add(currentPromos[0]);
						}
						
					}
				}
				
				if (activePromotions != null && !activePromotions.isEmpty()) {
					for (RepositoryItem promotionStatus : activePromotions) {
						RepositoryItem activePromotion = (RepositoryItem) promotionStatus.getPropertyValue(BBBCoreConstants.PROMOTION);
						List<RepositoryItem> coupons = (List<RepositoryItem>) promotionStatus.getPropertyValue("coupons");
						isStoreOnlyCoupons = Boolean.parseBoolean(String.valueOf(coupons.get(0).getPropertyValue(TBSConstants.STORE_ONLY)));
						if (isStoreOnlyCoupons) {
							continue;
						} else {
							removeAvailablePromotions.add(activePromotion);
							
						}
					}
				}
				
				for (RepositoryItem removePromotion : removeAvailablePromotions) {
					removePromotion((MutableRepositoryItem) profile, removePromotion, false);
					if (availablePromotions.contains(removePromotion)) {
						availablePromotions.remove(removePromotion);
					}
				}
				
				vlogDebug(
						"BBBPromotionTools: Removed AlreadyAppliedCoupons list: {0} from available promotion list: {1}",
						removeAvailablePromotions, availablePromotions);
				((MutableRepositoryItem) profile).setPropertyValue(
						BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, availablePromotions);
				/*
				 * Re-initializing all current active promotion to
				 * pricingModelHolder object.
				 */
				
				initializePricingModels(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse());
				if(isLoggingDebug())
					this.logDebug("BBBPromotionTools : removeAlreadyAppliedCoupons end.");
				BBBPerformanceMonitor.end("BBBPromotionTools", "removeAlreadyAppliedCoupons");
			}
		} 
		catch (final BBBException e) {        	
			vlogError(
					BBBCoreErrorConstants.POST_LOGIN_FETCH_CONFIG_ERROR + " :BBBPromotionTools: Error occured while fetching config map.");
            BBBPerformanceMonitor.cancel("BBBPromotionTools", "removeAlreadyAppliedCoupons");
        }
	}
}
