package com.bbb.commerce.checkout;

import java.util.HashMap;
import java.util.Map;

import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

public class BBBCreditCardContainer extends BBBGenericService {
	
	private int mMaxCreditCardRetryCount = 0;
	
	private int mCreditCardRetryCount = 0;

	/**
	 * @return the maxCreditCardRetryCount
	 */
	public final int getMaxCreditCardRetryCount() {
		int maxInvalidCCAttempts = 0;
		final String maxInvalidCCAttemptsConfig = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.CART_AND_CHECKOUT_KEYS, BBBCoreConstants.MAX_CARD_FAIL_ATTEMPTS);
		if (BBBUtility.isNotEmpty(maxInvalidCCAttemptsConfig)) {
			try {
				maxInvalidCCAttempts = Integer.parseInt(maxInvalidCCAttemptsConfig);
			} catch (final NumberFormatException nfe) {
				this.logError("Invalid Number format:" + maxInvalidCCAttemptsConfig, nfe);
			}
		} else {
			maxInvalidCCAttempts = mMaxCreditCardRetryCount;
		}
		return maxInvalidCCAttempts;
	}

	/**
	 * @param pMaxCreditCardRetryCount the maxCreditCardRetryCount to set
	 */
	public final void setMaxCreditCardRetryCount(int pMaxCreditCardRetryCount) {
		mMaxCreditCardRetryCount = pMaxCreditCardRetryCount;
	}

	/**
	 * @return the creditCardRetryCount
	 */
	public final int getCreditCardRetryCount() {
		return mCreditCardRetryCount;
	}

	/**
	 * @param pCreditCardRetryCount the creditCardRetryCount to set
	 */
	public final void setCreditCardRetryCount(int pCreditCardRetryCount) {
		mCreditCardRetryCount = pCreditCardRetryCount;
	}
	
	/**
	 * @param pCreditCardErrorCount the creditCardErrorCount to set
	 */
	public final void increaseCreditCardRetryCount() {
		mCreditCardRetryCount++;
	}	

	/**
	 * Constant for Map<String, BBBCreditCardInfo>
	 */
	private Map<String, BasicBBBCreditCardInfo> creditCardMap = null;
	

	/**
	 * This initialize the creditCardMap & sourceMap to null
	 */
	public void initialize() {
		creditCardMap = null;
	}

	/**
	 * @return the creditCardMap
	 */
	public Map<String, BasicBBBCreditCardInfo> getCreditCardMap() {
		
		if (creditCardMap != null) {
			return creditCardMap;
		} else {
			creditCardMap = new HashMap<String, BasicBBBCreditCardInfo>();			
			return creditCardMap;
		}
		
	}

	/**
	 * @param ccKey
	 * @param bbbCreditCardInfo
	 */
	public void addCreditCardToContainer (String ccKey, BasicBBBCreditCardInfo bbbCreditCardInfo) {
		getCreditCardMap().put(ccKey, bbbCreditCardInfo);
	}
	
	/**
	 * @param ccKey
	 * @return BasicBBBCreditCardInfo
	 */
	public BasicBBBCreditCardInfo getCreditCardFromContainer (String ccKey) {
		return new BasicBBBCreditCardInfo();
	}
	
	/**
	 * @param ccKey
	 * @param creditCardId
	 */
	public void removeCreditCardFromContainer (String ccKey, String creditCardId) {
		getCreditCardMap().remove(ccKey);
	}
}