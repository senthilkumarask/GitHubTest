package com.bbb.commerce.promotion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableException;
import atg.commerce.order.CommerceItem;
import atg.commerce.promotion.CouponFormHandler;
import atg.commerce.promotion.PromotionException;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.claimable.TBSClaimableManager;
import com.bbb.commerce.common.couponFilterVO;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.TBSOrder;
import com.bbb.commerce.order.TBSOrderImpl;
import com.bbb.common.TBSSessionComponent;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.utils.BBBUtility;

public class TBSCouponFormHandler extends CouponFormHandler {

    private static final String COUPON_GENERIC_ERROR = "err_coupon_grant_error";
    private static final String COUPON_BOPUS_ERROR = "err_coupon_bopus_error";
    private static final String COUPON_GC_ERROR = "err_coupon_gc_error";
    private static final String COUPON_NMC_ERROR = "err_coupon_nmc_error";
    private static final String COUPON_SKU_EX_ERROR = "err_coupon_sku_ex_error";
    private String fromPage;// Page Name that will be set from JSP
	
	private Map<String,String> successUrlMap;
    private Map<String,String> errorUrlMap;
    
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}

	public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	/**
	 * mCouponCodes to store the selected store coupon codes
	 */
	private String[] mCouponCodes = new String[10];
	/**
	 * mSessionComponent to hold TBSSessionComponent
	 */
	private TBSSessionComponent mSessionComponent;
	/**
	 * mOrderManager to hold BBBOrderManager
	 */
	private BBBOrderManager mOrderManager;
	/**
	 * mOrder to hold BBBOrderImpl
	 */
	private BBBOrderImpl mOrder;
	/**
	 * mEmailCoupons to hold email coupons
	 */
	private String[] mEmailCoupons;
	/**
	 * mCatalogTools to hold BBBCatalogTools
	 */
	private BBBCatalogTools mCatalogTools;
	
	private LblTxtTemplateManager lblTxtTemplateManager;
	
	/**
	 * @return the couponCodes
	 */
	public String[] getCouponCodes() {
		return mCouponCodes;
	}

	/**
	 * @return the sessionComponent
	 */
	public TBSSessionComponent getSessionComponent() {
		return mSessionComponent;
	}

	/**
	 * @param pSessionComponent the sessionComponent to set
	 */
	public void setSessionComponent(TBSSessionComponent pSessionComponent) {
		mSessionComponent = pSessionComponent;
	}

	/**
	 * @param pCouponCodes the couponCodes to set
	 */
	public void setCouponCodes(String[] pCouponCodes) {
		mCouponCodes = pCouponCodes;
	}
	
	/**
	 * @return the orderManager
	 */
	public BBBOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @return the order
	 */
	public BBBOrderImpl getOrder() {
		return mOrder;
	}

	/**
	 * @param pOrderManager the orderManager to set
	 */
	public void setOrderManager(BBBOrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	/**
	 * @param pOrder the order to set
	 */
	public void setOrder(BBBOrderImpl pOrder) {
		mOrder = pOrder;
	}

	/**
	 * @return the emailCoupons
	 */
	public String[] getEmailCoupons() {
		return mEmailCoupons;
	}

	/**
	 * @param pEmailCoupons the emailCoupons to set
	 */
	public void setEmailCoupons(String[] pEmailCoupons) {
		mEmailCoupons = pEmailCoupons;
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
	 * This method is used to claim the store coupons. It will use the OOTB Claimable Manager to claim the store coupons
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public boolean handleClaimStoreCoupons(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
			throws ServletException, IOException {
		vlogDebug("TBSCouponFormHandler :: handleClaimStoreCoupons() method :: START");
		
		//Client DOM XSRF | Part -2
		if (StringUtils.isNotEmpty(getFromPage())) {
			setClaimCouponSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()));
			setClaimCouponErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
		}
		
		preClaimStoreCoupons(pRequest, pResponse);
		
		TransactionDemarcation td = new TransactionDemarcation();
		TBSClaimableManager claimableManager = (TBSClaimableManager)getClaimableManager();
		Map<String, List<RepositoryItem>> promotions  = new HashMap<String, List<RepositoryItem>>();
		boolean rollback = true;
		TBSOrder order = (TBSOrder) getOrder();
		List<RepositoryItem> availablePromotions = (List<RepositoryItem>) getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);
		List<RepositoryItem> orderAvailablePromotions = order.getAvailablePromotionsList();
		
		if(!getFormError()){
			try {
				td.begin(getTransactionManager());
				vlogDebug("Existing promotions at profile :: "+availablePromotions);
				synchronized(getProfile()){
				for (Object element : orderAvailablePromotions) {
					// remove already granted promotion
					RepositoryItem promotion = (RepositoryItem) element;
					getPromotionTools().removePromotion((MutableRepositoryItem) getProfile(), promotion, false);
					vlogDebug("Removing the promotion :: "+promotion.getRepositoryId());
				}
				
				// Setting order object promo code null on removal of promotions.
				synchronized(order){
				((BBBOrderImpl) getOrder()).setSchoolCoupon(null);
				availablePromotions.clear();
				orderAvailablePromotions.clear();
				getOrder().updateVersion();
				Map<String, RepositoryItem> couponMap = ((TBSOrderImpl)getOrder()).getTbsCouponsMap();
				couponMap.clear();
				if(mEmailCoupons != null && mEmailCoupons.length > TBSConstants.ZERO){
					RepositoryItem[] emailPromoItems = null;
					for (String emailCouponId : mEmailCoupons) {
						emailPromoItems = getCatalogTools().getPromotions(emailCouponId);
						vlogDebug("email based promotions :: "+emailPromoItems);
						List<RepositoryItem> promoItems = null;
						for (RepositoryItem emailPromoItem : emailPromoItems) {
							Set<RepositoryItem> sites = (Set<RepositoryItem>)emailPromoItem.getPropertyValue("sites");
							if(sites != null && !sites.isEmpty()) {
								for (RepositoryItem siteItem : sites) {
									if(siteItem.getRepositoryId().equals(SiteContextManager.getCurrentSiteId())){
										if(promotions.get(emailCouponId) == null){
											promoItems = new ArrayList<RepositoryItem>();
											promoItems.add(emailPromoItem);
											promotions.put(emailCouponId, promoItems);
										} else {
											promoItems = promotions.get(emailCouponId);
											promoItems.add(emailPromoItem);
										}
									}
								}
							} else {
								if(promotions.get(emailCouponId) == null){
									promoItems = new ArrayList<RepositoryItem>();
									promoItems.add(emailPromoItem);
									promotions.put(emailCouponId, promoItems);
								} else {
									promoItems = promotions.get(emailCouponId);
									promoItems.add(emailPromoItem);
								}
							}
						}
					}
				}
				promotions = claimableManager.fetchCouponPromotions(promotions, mCouponCodes);
				vlogDebug("Total promotions :: "+promotions);

				List<CommerceItem> commerceItemLists = getOrder().getCommerceItems();
				couponFilterVO couponFiltervo = new couponFilterVO();
                String couponErrorText = COUPON_GENERIC_ERROR;
                
				if(!promotions.isEmpty()){
					boolean checkProm = false;
					for (Map.Entry<String, List<RepositoryItem>> promotionMap : promotions.entrySet()) {
						try {
                            for (final CommerceItem commerceItem : commerceItemLists) {
                                couponFiltervo = checkCommerceItemQualifierFlags(commerceItem, couponFiltervo, promotionMap.getKey());
                                if (couponFiltervo.isContainsNormal()) {
                                    break;
                                }
                            }
                        } catch (final BBBSystemException bse) {
                            this.logError("BBBSystem Exception occured while identifying a couponFilter ", bse);
                        } catch (final BBBBusinessException bbe) {
                            this.logError("BBBBusiness Exception occured while identifying a couponFilter ", bbe);
                        }
						couponErrorText = getCouponErrorMessage(couponFiltervo);
						String couponCode= promotionMap.getKey();
						RepositoryItem couponRepositoryItem=this.getClaimableTools().getClaimableItem(couponCode);
						if (couponErrorText.equalsIgnoreCase(COUPON_GENERIC_ERROR)) {
							for (RepositoryItem promoItem : promotionMap.getValue()) {
								checkProm = getPromotionTools().checkPromotionGrant(getProfile(), promoItem);
								if (checkProm) {
									getPromotionTools().grantPromotion(getProfile(), promoItem, null, null, couponRepositoryItem);
									availablePromotions.add(promoItem);
									orderAvailablePromotions.add(promoItem);
									couponMap.put(promotionMap.getKey(), promoItem);
								}
							}
						} else {
							vlogDebug("PromoCode " + promotionMap.getKey() + " is not applicable");
							if(mEmailCoupons != null && mEmailCoupons.length > 0){
								List<String> emailCoupons = Arrays.asList(mEmailCoupons);
								if(emailCoupons.contains(promotionMap.getKey())){
									if((promotionMap.getValue().get(0)).getPropertyValue("description") != null){
										String couponName = ((String)(promotionMap.getValue().get(0)).getPropertyValue("displayName")) + 
												((String)(promotionMap.getValue().get(0)).getPropertyValue("description"));
										addFormException(new DropletException(couponName.replaceAll("_", "-")));
									} else {
										addFormException(new DropletException(((String) (promotionMap.getValue().get(0)).getPropertyValue("displayName")).replaceAll("_", "-")));
									}
									addFormException(new DropletException(couponErrorText));
								} 
							}
							if(mCouponCodes != null && mCouponCodes.length > 0){
								List<String> couponCodes = Arrays.asList(mCouponCodes);
								if(couponCodes.contains(promotionMap.getKey())){
									RepositoryItem coupon = claimableManager.claimItem(promotionMap.getKey());
									addFormException(new DropletException(((String) coupon.getPropertyValue("displayName")).replaceAll("_", "-")));
									addFormException(new DropletException(couponErrorText));
								}
							}
						}
					}
				}
				getProfile().setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, availablePromotions);
				order.setAvailablePromotionsList(orderAvailablePromotions);
                getPromotionTools().initializePricingModels(pRequest, pResponse);

                getOrderManager().updateOrder(getOrder());
				}
				}
                if(mEmailCoupons != null && mEmailCoupons.length > 0){
                	mEmailCoupons = new String[10]; 
        		}
        		if(mCouponCodes != null && mCouponCodes.length > 0){
        			mCouponCodes = new String[10]; 
        		}
				rollback = false;
			} catch (TransactionDemarcationException e) {
				vlogError("TransactionDemarcationException occurred :: "+e);
			} catch (PromotionException e) {
				vlogError("PromotionException occurred :: "+e);
				addFormException(new DropletException(e.getMessage()));
			} catch (ClaimableException e) {
				vlogError("ClaimableException occurred :: "+e);
				addFormException(new DropletException(e.getMessage()));
			}catch (BBBSystemException e) {
				vlogError("BBBSystemException occurred :: "+e);
				addFormException(new DropletException(e.getMessage()));
			} catch (BBBBusinessException e) {
				vlogError("BBBBusinessException occurred :: "+e);
				addFormException(new DropletException(e.getMessage()));
			} catch (CommerceException e) {
				vlogError("CommerceException occurred :: "+e);
				addFormException(new DropletException(e.getMessage()));
			} catch (RepositoryException e) {
				vlogError("RepositoryException occurred :: "+e);
				addFormException(new DropletException(e.getMessage()));
			} finally {
				try {
					td.end(rollback);
				} catch (TransactionDemarcationException tde) {
					vlogError("TransactionDemarcationException "+tde);
				}
			}
		}
		vlogDebug("TBSCouponFormHandler :: handleClaimStoreCoupons() method :: END");
		return checkFormRedirect(getClaimCouponSuccessURL(), getClaimCouponErrorURL(), pRequest, pResponse);
	}

	/**
	 * This method is used for validating the storeCoupons.
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private void preClaimStoreCoupons(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		
		vlogDebug("TBSCouponFormHandler :: preClaimStoreCoupons() method :: START");
		if((mCouponCodes == null || mCouponCodes.length <= TBSConstants.ZERO) &&
				(mEmailCoupons == null || mEmailCoupons.length <= TBSConstants.ZERO) ){
			addFormException(new DropletException("Please select the coupon to claim."));
		} else if (Arrays.asList(mCouponCodes).contains(BBBCoreConstants.MINUS_ONE)) {
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERR_APPLY_ORDER_COUPONS,
					pRequest.getLocale().getLanguage(), null, null)));
		}
		vlogDebug("TBSCouponFormHandler :: preClaimStoreCoupons() method :: END");
	}
	
	/**
	 * This method is used for clearing all the store specific promotions from the order.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	 @SuppressWarnings("unchecked")
	public boolean handleClearStoreCoupons(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
				 throws ServletException, IOException {
		vlogDebug("TBSCouponFormHandler :: handleClearStorePromotions() :: START");
		//Client DOM XSRF | Part -2
		if (StringUtils.isNotEmpty(getFromPage())) {
			setClaimCouponSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()));
			setClaimCouponErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
		}
		TransactionDemarcation td = new TransactionDemarcation();
		PromotionTools promotools = getClaimableManager().getPromotionTools();
		boolean rollback = true;
		TBSOrder order = (TBSOrder) getOrder();
		try {
			td.begin(getTransactionManager());
			RepositoryItem promotion = null;
            
			List<RepositoryItem> availablePromotions = (List<RepositoryItem>) getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);
			List<RepositoryItem> orderAvailablePromotions = order.getAvailablePromotionsList();
			vlogDebug("Existing promotions at profile :: "+availablePromotions);
			synchronized(getProfile()){
			for (Object element : orderAvailablePromotions) {
				// remove already granted promotion
			    promotion = (RepositoryItem) element;
			    getPromotionTools().removePromotion((MutableRepositoryItem) getProfile(), promotion, false);
			}
			promotools.initializePricingModels();
			availablePromotions.clear();
			orderAvailablePromotions.clear();
			if(mEmailCoupons != null && mEmailCoupons.length > 0){
            	mEmailCoupons = new String[10]; 
    		}
    		if(mCouponCodes != null && mCouponCodes.length > 0){
    			mCouponCodes = new String[10]; 
    		}
    		
			getProfile().setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, availablePromotions);
    		}
			synchronized(order){
			order.setAvailablePromotionsList(orderAvailablePromotions);
			getOrderManager().updateOrder(getOrder());
			}
			rollback = false;
		} catch (TransactionDemarcationException e) {
			vlogError("TransactionDemarcationException occurred :: "+e);
		} catch (CommerceException e) {
			vlogError("CommerceException occurred :: "+e);
			addFormException(new DropletException(e.getMessage()));
		} finally {
			try {
				td.end(rollback);
			} catch (TransactionDemarcationException tde) {
				vlogError("TransactionDemarcationException "+tde);
			}
		}
		vlogDebug("TBSCouponFormHandler :: handleClearStorePromotions() :: END");
		return checkFormRedirect(getClaimCouponSuccessURL(), getClaimCouponErrorURL(), pRequest, pResponse);
	 }

	 /** This method finds if a particular Commerce Item fall under any one of the Qualifier Service Filters. If it does,
	     * it sets the pCouponFiltervo variable true.
	     *
	     * @param CommerceItem pCommerceItem
	     * @param couponFilterVO pCouponFiltervo
	     * @param String pCouponCode
	     * @return couponFilterVO pCouponFiltervo
	     * @throws BBBSystemException
	     * @throws BBBBusinessException */
	    private couponFilterVO checkCommerceItemQualifierFlags(CommerceItem pCommerceItem, couponFilterVO pCouponFiltervo, String pCouponCode)
	                    throws BBBSystemException, BBBBusinessException {
	        // Check if Commerce item is instance of BBBCommerce Item for BOPUS check
	        BBBCommerceItem bbbCommerceItem = null;
	        if (pCommerceItem instanceof BBBCommerceItem) {
	            bbbCommerceItem = (BBBCommerceItem) pCommerceItem;
	        }

	        // Check if Commerce item is BOPUS
	        if ((bbbCommerceItem != null) && !(BBBUtility.isEmpty(bbbCommerceItem.getStoreId()))) {
	            pCouponFiltervo.setContainsBOPUS(true);
	        }
	        // Check if Commerce item is Gift Card
	        else if (getCatalogTools().isGiftCardItem(getOrder().getSiteId(), pCommerceItem.getCatalogRefId())) {
	            pCouponFiltervo.setContainsGiftCard(true);
	        }
	        // Check if Commerce item is Non Merchandise CommerceItem
	        else if (pCommerceItem instanceof NonMerchandiseCommerceItem) {
	            pCouponFiltervo.setContainsNMC(true);
	        }
	        // Check if Commerce item is SKU Excluded
	        else if (getCatalogTools().isSkuExcluded(pCommerceItem.getCatalogRefId(), pCouponCode, false)) {
	            pCouponFiltervo.setContainsSKUExclusion(true);
	        }
	        // Commerce item is Normal
	        else {
	            pCouponFiltervo.setContainsNormal(true);
	        }

	        return pCouponFiltervo;
	    }
	    
	    /** This method return the corresponding Coupon Error Code based on Priority.
	     *
	     * @param couponFilterVO pCouponFiltervo
	     * @return couponFilterVO pCouponFiltervo */
	    private static String getCouponErrorMessage(couponFilterVO pCouponFiltervo) {
	        if (pCouponFiltervo.isContainsNormal()) {
	            return COUPON_GENERIC_ERROR;
	        } else if (pCouponFiltervo.isContainsBOPUS()) {
	            return COUPON_BOPUS_ERROR;
	        } else if (pCouponFiltervo.isContainsGiftCard()) {
	            return COUPON_GC_ERROR;
	        } else if (pCouponFiltervo.isContainsNMC()) {
	            return COUPON_NMC_ERROR;
	        } else if (pCouponFiltervo.isContainsSKUExclusion()) {
	            return COUPON_SKU_EX_ERROR;
	        }
	        return COUPON_GENERIC_ERROR;
	    }

		public LblTxtTemplateManager getLblTxtTemplateManager() {
			return lblTxtTemplateManager;
		}

		public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
			this.lblTxtTemplateManager = lblTxtTemplateManager;
		}
	    
}
