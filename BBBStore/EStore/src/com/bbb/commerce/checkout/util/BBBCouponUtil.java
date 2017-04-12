/**
 * 
 */
package com.bbb.commerce.checkout.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.selfservice.vo.SchoolVO;

/**
 * @author vagra4
 * 
 */
public class BBBCouponUtil extends BBBGenericService {

	private OrderManager mOrderManager;
	
	private TransactionManager mTransactionManager;

	private BBBCatalogTools bbbCatalogTools;

	private ClaimableManager mClaimableManager;

	/**
	 * @return the bbbCatalogTools
	 */
	public BBBCatalogTools getBBBCatalogTools() {
		return bbbCatalogTools;
	}

	/**
	 * @param pBbbCatalogTools
	 *            the bbbCatalogTools to set
	 */
	public void setBBBCatalogTools(BBBCatalogTools pBbbCatalogTools) {
		bbbCatalogTools = pBbbCatalogTools;
	}

	/**
	 * This method fetches the school promotion from the user profile and adds
	 * to the Map.
	 * 
	 * @param pProfile
	 * @param pPromotions
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public void addSchoolPromotion(Profile pProfile,
			Map<String, RepositoryItem> pPromotions) throws BBBSystemException,
			BBBBusinessException {

		String logMsg = null;
		if (pProfile instanceof Profile) {
			logMsg = "valid Profile object(Not displaying on logs due to securioty reasons)";
		}

		logDebug("Start: method addSchoolPromotion, input parameters--> pProfile:"
				+ logMsg + " pPromotions:" + pPromotions);

		RepositoryItem schoolPromoItem = null;

		Object schoolPromoID = pProfile.getPropertyValue(BBBCheckoutConstants.SCHOOL_PROMOTIONS);
		Object schoolID = pProfile
				.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS);

		SchoolVO schoolVO = null;
		if (schoolID instanceof String && schoolPromoID instanceof String) {
			// validating schoolID
			schoolVO = (SchoolVO) getBBBCatalogTools().getSchoolDetailsById(
					(String) schoolID);

			if (schoolVO.getPromotionRepositoryItem() instanceof RepositoryItem) {
				schoolPromoItem = schoolVO.getPromotionRepositoryItem();
				if (schoolPromoItem.getRepositoryId().equals(
						(String) schoolPromoID)) {
					logDebug("schoolPromotion added to Promotion Map");
					pPromotions.put(BBBCheckoutConstants.SCHOOLPROMO,
							schoolPromoItem);
				}
			}
		}
		logDebug("End: method addSchoolPromotion");
	}

	/**
	 * 
	 * @param couponClaimCode
	 * @param string
	 * @throws RepositoryException
	 */
	public Boolean compareClaimCode(String couponClaimCode, Object pSchoolPromo)
			throws RepositoryException {

		String logMsg = null;

		if (couponClaimCode != null) {
			logMsg = "couponClaimCode entered by user(not displaying on logs due to securioty reasons)";
		}

		logDebug("Start: method compareClaimCode, input parameters--> couponClaimCode:"
				+ logMsg + " pSchoolPromo:" + pSchoolPromo);

		//This has been kept null to test sapeunit
		Boolean promoFound = null;

		String schoolPromotion = null;

		if (pSchoolPromo instanceof String) {
			schoolPromotion = (String) pSchoolPromo;
		}

		RepositoryItem coupon = getClaimableManager().getClaimableTools()
				.getClaimableItem(couponClaimCode);
		if (coupon != null) {
			String promoPropertyName = getClaimableManager()
					.getClaimableTools().getPromotionsPropertyName();

			@SuppressWarnings("unchecked")
			Set<RepositoryItem> multiplePromotions = (Set<RepositoryItem>) coupon
					.getPropertyValue(promoPropertyName);
			if (multiplePromotions != null) {
				//This has been kept Boolean.FALSE to test sapeunit
				promoFound = Boolean.FALSE;
				for (RepositoryItem repositoryItem : multiplePromotions) {
					if (repositoryItem.getRepositoryId()
							.equals(schoolPromotion)) {
						promoFound = Boolean.TRUE;
						break;
					}
				}

			}

		}
		logDebug("End: method compareClaimCode, User entered promo code comparison status with profile's school promotion coupon code:"
				+ promoFound);
		
		//BBBSL-2574 Fixed the null pointer issue as the promoFound was returned null if the coupon code is invalid //
		if(promoFound == null){
			promoFound = Boolean.FALSE;
			return promoFound;
		}
		return promoFound;
	}

	/**
	 * This method checks if a school promotion is already applied and user
	 * revisits a new school URL resulting new schoolID and promotionID get set
	 * in profile then removing previous school promotion from Order and
	 * promotion Map and adding new school promotion in promotion Map now this
	 * latest school promotion will be displayed in select state on coupon page.
	 * 
	 * @param pPromotions
	 * @param pProfile
	 * @param pOrder
	 * @param req 
	 * @return 
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Map<String, RepositoryItem> applySchoolPromotion(Map<String, RepositoryItem> pPromotions,
			Profile pProfile, Order pOrder) throws BBBSystemException,
			BBBBusinessException {

		String logMsg = null;
		if (pProfile instanceof Profile) {
			logMsg = "valid Profile object(Not displaying on logs due to securioty reasons)";
		}

		if(pPromotions == null){
			pPromotions = new HashMap<String, RepositoryItem>();
		}
		
		logDebug("Start: method applySchoolPromotion, input parameters--> couponClaimCode:"
				+ logMsg + " pPromotions:" + pPromotions + " pOrder:" + pOrder);

		if (pProfile.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS) != null) {
			synchronized (pOrder) {
				boolean rollback = false;
				TransactionDemarcation td = new TransactionDemarcation();
				try {
					/* Start the transaction */
					td.begin(getTransactionManager());
					String profileSchoolID = (String) pProfile
							.getPropertyValue(BBBCheckoutConstants.SCHOOLIDS);
					String orderSchoolID = ((BBBOrderImpl) pOrder)
							.getSchoolId();

					if (!StringUtils.isEmpty(orderSchoolID)
							&& !profileSchoolID.equals(orderSchoolID)) {
						logDebug("*** User has visited another school URL, Removing previous applied school promotion from Order and promotion Map...");
						if (pPromotions
								.containsKey(BBBCheckoutConstants.SCHOOLPROMO)) {
							getClaimableManager()
									.getPromotionTools()
									.removePromotion(
											(MutableRepositoryItem) pProfile,
											pPromotions
													.get(BBBCheckoutConstants.SCHOOLPROMO),
											false);
							logDebug("Removed schoolPromotion from Order");
							pPromotions
									.remove(BBBCheckoutConstants.SCHOOLPROMO);
							logDebug("Removed schoolPromotion from Promotion Map");
							((BBBOrderImpl) pOrder).setSchoolCoupon(null);
						}
					}
					/**
					 * Setting the schoolId initially if user has a schoolID in
					 * profile.
					 */
					((BBBOrderImpl) pOrder).setSchoolId(profileSchoolID);
					getOrderManager().updateOrder(pOrder);
				} catch (TransactionDemarcationException tdexp) {
					if (isLoggingError()) {
						logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1020 + 
								": Transaction demarcation failure while updating Order for school promotions",
								tdexp);
					}
					throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1066,tdexp.getMessage(), tdexp);
				} catch (CommerceException comexp) {
					if (isLoggingError()) {
						logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1019 +
								"Commerce Exception while updating Order for school promotions",
								comexp);
					}
					throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1066,comexp.getMessage(), comexp);
				} finally {
					try {
						/* Complete the transaction */
						td.end(rollback);
					} catch (TransactionDemarcationException tdexp) {
						if (isLoggingError()) {
							logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1020 +
									"Transaction demarcation failure while updating Order for school promotions",
									tdexp);
						}
					}
				}
			}

			if (!pPromotions.containsKey(BBBCheckoutConstants.SCHOOLPROMO)) {
				addSchoolPromotion(pProfile, pPromotions);
			}

			logDebug("End: method applySchoolPromotion");
		}
		
		return pPromotions;
	}

	/**
	 * @return the mClaimableManager
	 */
	public ClaimableManager getClaimableManager() {
		return mClaimableManager;
	}

	/**
	 * @param pClaimableManager
	 *            the mClaimableManager to set
	 */
	public void setClaimableManager(ClaimableManager pClaimableManager) {
		mClaimableManager = pClaimableManager;
	}

	/**
	 * @return the mTransactionManager
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	/**
	 * @param pTransactionManager
	 *            the mTransactionManager to set
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	/**
	 * @return the mOrderManager
	 */
	public OrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @param pOrderManager the mOrderManager to set
	 */
	public void setOrderManager(OrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}
}
