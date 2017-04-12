package com.bbb.account.api;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import atg.beans.PropertyNotFoundException;
import atg.commerce.profile.CommercePropertyManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class BBBCreditCardAPIImpl extends BBBGenericService implements BBBCreditCardAPI {

	public BBBProfileTools mProfileTools;

	/**
	 * @return the mProfileTools
	 */
	public BBBProfileTools getProfileTools() {
		return this.mProfileTools;
	}

	/**
	 * @param mProfileTools the mProfileTools to set
	 */
	public void setProfileTools(final BBBProfileTools mProfileTools) {
		this.mProfileTools = mProfileTools;
	}

	@Override
	public BasicBBBCreditCardInfo addNewCreditCard(final Profile profile, final BasicBBBCreditCardInfo cardInfo, final String siteId)
			throws BBBSystemException {

		final BasicBBBCreditCardInfo creditCardVO = new BasicBBBCreditCardInfo();
		final HashMap<String,String> card = new HashMap<String,String>();
		card.put(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_NUMBER,cardInfo.getCreditCardNumber());
		card.put(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_TYPE,cardInfo.getCreditCardType());
		card.put(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_NOC, cardInfo.getNameOnCard());
		card.put(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_EXP_YEAR, cardInfo.getExpirationYear());
		card.put(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_EXP_MONTH, cardInfo.getExpirationMonth());
		String nickName=null;
		try {
			final MutableRepositoryItem creditCardRepositoryItem = ((MutableRepository)profile.getRepository()).createItem(((CommercePropertyManager)this.getProfileTools().getPropertyManager()).getCreditCardItemDescriptorName());

			creditCardRepositoryItem.setPropertyValue(((CommercePropertyManager)this.getProfileTools().getPropertyManager()).getCreditCardTypePropertyName(), cardInfo.getCreditCardType());
			creditCardRepositoryItem.setPropertyValue(((CommercePropertyManager)this.getProfileTools().getPropertyManager()).getCreditCardNumberPropertyName(), cardInfo.getCreditCardNumber());

			nickName = this.getProfileTools().getUniqueCreditCardNickname(creditCardRepositoryItem,profile,nickName);

			this.getProfileTools().createProfileCreditCard(profile,card,nickName,null);
		} catch(final IntrospectionException e) {
			this.logError(e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1278,e.getMessage(), e);
		} catch(final PropertyNotFoundException e) {
			this.logError(e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1280,e.getMessage(), e);
		} catch(final RepositoryException e) {
			this.logError(e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1281,e.getMessage(), e);
		}
		this.getProfileTools().setDefaultCreditCard(profile,nickName);
		creditCardVO.setCreditCardId(this.getProfileTools().getCreditCardByNickname(nickName,profile).toString());
		creditCardVO.setSource(BBBCreditCardAPIConstants.SOURCE);
		return creditCardVO;

	}

	@Override
	public BasicBBBCreditCardInfo getDefaultCreditCard(final Profile profile, final String siteId)
			throws BBBSystemException, BBBBusinessException {

			this.logDebug("getDefaultCreditCard() method starts");
		BasicBBBCreditCardInfo creditCardVO = new BasicBBBCreditCardInfo();

		try{

			// below throws null if no default card
			creditCardVO.setCreditCardNumber(this.getProfileTools().getDefaultCreditCard(profile).getPropertyValue(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_NUMBER).toString());
			creditCardVO.setCreditCardType(this.getProfileTools().getDefaultCreditCard(profile).getPropertyValue(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_TYPE).toString());
			creditCardVO.setExpirationMonth(this.getProfileTools().getDefaultCreditCard(profile).getPropertyValue(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_EXP_YEAR).toString());
			creditCardVO.setExpirationYear(this.getProfileTools().getDefaultCreditCard(profile).getPropertyValue(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_EXP_MONTH).toString());
			creditCardVO.setNameOnCard(this.getProfileTools().getDefaultCreditCard(profile).getPropertyValue(BBBCreditCardAPIConstants.PPTY_CREDIT_CARD_NOC).toString());
			creditCardVO.setDefault(true);
			creditCardVO.setSource(BBBCreditCardAPIConstants.SOURCE);

			// below throws null if no default card ends
		} catch(final Exception e){
			creditCardVO = null;
			logError("Exception occured" + e.getMessage(), e);
		}
			this.logDebug("getDefaultCreditCard() method ends");
		return creditCardVO ;
	}

	@Override
	public List<BasicBBBCreditCardInfo> getUserCreditCardWallet(final Profile profile, final String siteId)
			throws BBBSystemException, BBBBusinessException {

			this.logDebug("getUserCreditCardWallet() method starts");
		final List<BasicBBBCreditCardInfo> creditCardList = new ArrayList<BasicBBBCreditCardInfo>();
		BasicBBBCreditCardInfo creditCardVO = null;
		/*Iterator it = getProfileTools().getUsersCreditCardMap(profile).keySet().iterator() ;*/
		//For Showing up the oreder in the billing page
		final Object[] keys = this.getProfileTools()
				.getUsersCreditCardMap(profile).keySet().toArray();
		for (int i=keys.length-1;i>=0;i--) {

			creditCardVO = new BasicBBBCreditCardInfo();
			final String key =(String)keys[i] ;

            	this.logDebug("BBBCreditCardAPIImpl | getUserCreditCardWallet key:"+key);
            	this.logDebug("BBBCreditCardAPIImpl | getUserCreditCardWallet value:"+this.getProfileTools().getUsersCreditCardMap(profile).get(key).toString().replace(BBBCreditCardAPIConstants.RPLC_CC_TXT_TO_EMPTY, ""));
			final String creditCardNumber = this.getProfileTools().getCreditCardById(this.getProfileTools().getUsersCreditCardMap(profile).get(key).toString().replace(BBBCreditCardAPIConstants.RPLC_CC_TXT_TO_EMPTY, "")).getPropertyValue("creditCardNumber").toString();
			final String year = this.getProfileTools().getCreditCardById(this.getProfileTools().getUsersCreditCardMap(profile).get(key).toString().replace(BBBCreditCardAPIConstants.RPLC_CC_TXT_TO_EMPTY, "")).getPropertyValue("expirationYear").toString();
			final String month = this.getProfileTools().getCreditCardById(this.getProfileTools().getUsersCreditCardMap(profile).get(key).toString().replace(BBBCreditCardAPIConstants.RPLC_CC_TXT_TO_EMPTY, "")).getPropertyValue("expirationMonth").toString();

			final java.util.Calendar calendar = java.util.Calendar.getInstance();
			if((Integer.parseInt(year)<calendar.get(java.util.Calendar.YEAR)) || ((Integer.parseInt(year)==calendar.get(java.util.Calendar.YEAR)) && (Integer.parseInt(month)< (calendar.get(java.util.Calendar.MONTH)+1)))){
				creditCardVO.setExpired(BBBCoreConstants.RETURN_TRUE);
			} else {
				creditCardVO.setExpired(BBBCoreConstants.RETURN_FALSE);
			}
			creditCardVO.setCreditCardNumber(creditCardNumber);
            creditCardVO.setCreditCardType(this.getProfileTools().getCreditCardById(this.getProfileTools().getUsersCreditCardMap(profile).get(key).toString().replace(BBBCreditCardAPIConstants.RPLC_CC_TXT_TO_EMPTY, "")).getPropertyValue("creditCardType").toString());
            creditCardVO.setNameOnCard(this.getProfileTools().getCreditCardById(this.getProfileTools().getUsersCreditCardMap(profile).get(key).toString().replace(BBBCreditCardAPIConstants.RPLC_CC_TXT_TO_EMPTY, "")).getPropertyValue("nameOnCard").toString());
            creditCardVO.setExpirationYear(year);
            creditCardVO.setExpirationMonth(month);
            creditCardVO.setLastFourDigits(creditCardNumber.substring(creditCardNumber.length()-4, creditCardNumber.length()));
            creditCardVO.setPaymentId(this.getProfileTools().getCreditCardById(this.getProfileTools().getUsersCreditCardMap(profile).get(key).toString().replace(BBBCreditCardAPIConstants.RPLC_CC_TXT_TO_EMPTY, "")).getPropertyValue("id").toString());

            final String str1=this.getProfileTools().getUsersCreditCardMap(profile).get(key).toString().replace(BBBCreditCardAPIConstants.RPLC_CC_TXT_TO_EMPTY, "");
            final String str2=this.getProfileTools().getDefaultCreditCard(profile).toString().replace("credit-card:", "");
            	this.logDebug("BBBCreditCardAPIImpl | getUserCreditCardWallet VAL 1:"+str1);
            	this.logDebug("BBBCreditCardAPIImpl | getUserCreditCardWallet VAL 2:"+str2);

            if (str1.equals(str2)) {
            		this.logDebug("BBBCreditCardAPIImpl | getUserCreditCardWallet True");
            	creditCardVO.setDefault(true);
            } else {
            		this.logDebug("BBBCreditCardAPIImpl | getUserCr editCardWallet False");
            	creditCardVO.setDefault(false);
            }
            creditCardVO.setSource(BBBCreditCardAPIConstants.SOURCE);
            creditCardList.add(creditCardVO);
		}
			this.logDebug("getUserCreditCardWallet() creditCardList*********"+creditCardList);

			this.logDebug("getUserCreditCardWallet() method ends");
		return creditCardList;
	}

}
