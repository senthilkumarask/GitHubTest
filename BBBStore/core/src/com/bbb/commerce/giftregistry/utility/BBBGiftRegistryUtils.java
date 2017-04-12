package com.bbb.commerce.giftregistry.utility;


import java.net.InetAddress;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import oracle.jdbc.OracleTypes;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import atg.adapter.gsa.GSARepository;
import atg.apache.xerces.impl.xpath.regex.RegularExpression;
import atg.repository.MutableRepository;
import atg.servlet.DynamoHttpServletRequest;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.RegNamesVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryBabyVO;
import com.bbb.commerce.giftregistry.vo.RegistryHeaderVO;
import com.bbb.commerce.giftregistry.vo.RegistryPrefStoreVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.utils.BBBUtility;

public class BBBGiftRegistryUtils extends BBBGenericService {

	private String invalidCharactersPattern;
	private String nonAlphaNumericPattern;
	private String numericPattern;
	private String emailPattern;
	private String alphaNumericPattren;
	private String invalidCharactersForHackPattern;
	private BBBCatalogTools mCatalogTools;
	private MutableRepository mRegistryInfoRepository;
	
	private String validZipPattern;
	private String validZipPatternForCA;
	private HashMap<String,String> regDescLookUpMap;
	private HashMap<String,String> prefixLookUpMap;
	private final String CLASS_NAME="BBBGiftRegistryUtils";

	public String getInvalidCharactersPattern() {
		return invalidCharactersPattern;
	}
	public void setInvalidCharactersPattern(String invalidCharactersPattern) {
		this.invalidCharactersPattern = invalidCharactersPattern;
	}
	public String getNonAlphaNumericPattern() {
		return nonAlphaNumericPattern;
	}
	public void setNonAlphaNumericPattern(String nonAlphaNumericPattern) {
		this.nonAlphaNumericPattern = nonAlphaNumericPattern;
	}
	public String getNumericPattern() {
		return numericPattern;
	}
	public void setNumericPattern(String numericPattern) {
		this.numericPattern = numericPattern;
	}
	public String getEmailPattern() {
		return emailPattern;
	}
	public void setEmailPattern(String emailPattern) {
		this.emailPattern = emailPattern;
	}

	public String getAlphaNumericPattren() {
		return alphaNumericPattren;
	}
	public void setAlphaNumericPattren(String alphaNumericPattren) {
		this.alphaNumericPattren = alphaNumericPattren;
	}
	public String getInvalidCharactersForHackPattern() {
		return invalidCharactersForHackPattern;
	}
	public void setInvalidCharactersForHackPattern(
			String invalidCharactersForHackPattern) {
		this.invalidCharactersForHackPattern = invalidCharactersForHackPattern;
	}
	public String getValidZipPattern() {
		return validZipPattern;
	}
	public void setValidZipPattern(String validZipPattern) {
		this.validZipPattern = validZipPattern;
	}
	public String getValidZipPatternForCA() {
		return validZipPatternForCA;
	}
	public void setValidZipPatternForCA(String validZipPatternForCA) {
		this.validZipPatternForCA = validZipPatternForCA;
	}
	public HashMap<String, String> getRegDescLookUpMap() {
		return regDescLookUpMap;
	}
	public void setRegDescLookUpMap(HashMap<String, String> regDescLookUpMap) {
		this.regDescLookUpMap = regDescLookUpMap;
	}
	public HashMap<String, String> getPrefixLookUpMap() {
		return prefixLookUpMap;
	}
	public void setPrefixLookUpMap(HashMap<String, String> prefixLookUpMap) {
		this.prefixLookUpMap = prefixLookUpMap;
	}
	
	/**
	 * Gets GetRegistryInfo repo.
	 *
	 * @return mRegistryInfoRepository
	 */
	public MutableRepository getRegistryInfoRepository() {
		return mRegistryInfoRepository;
	}
	/**
	 * Sets the mRegistryInfoRepository.
	 *
	 * @param mRegistryInfoRepository
	 */
	public void setRegistryInfoRepository(MutableRepository pRegistryInfoRepository) {
		this.mRegistryInfoRepository = pRegistryInfoRepository;
	}
	/**
	 * Gets the catalog tools.
	 *
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}
	/**
	 * Sets the catalog tools.
	 *
	 * @param pCatalogTools
	 *            the new catalog tools
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	/**
	 * This method validates the input passed does not contain any invalid character  
	 * @param siteCode
	 * @return boolean
	 */

	public boolean hasInvalidChars(String value)
	{
		boolean hasInvalidChars = false;

		if(!BBBUtility.isEmpty(value)) {
			RegularExpression objRegEx = new RegularExpression(getInvalidCharactersPattern());
			if (objRegEx.matches(value)) {
				hasInvalidChars = true;
				logError("Input value passed as : " + value + " contains some invalid characters ");
			}
		}

		return hasInvalidChars;
	}
	
	/**
	 * This method validates the zipCode passed as input for the site
	 * @param zipCode
	 * @param siteCode
	 * @return boolean
	 */
	
	public boolean isValidZip(String zipCode, String siteCode) {
		
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. isValidZip methos starts with zip code :: " + zipCode + " sand site code :: " + siteCode);
		}
		boolean validZip = false;
		RegularExpression objRegEx = null;
		if (!BBBUtility.isEmpty(zipCode) && !BBBUtility.isEmpty(siteCode)) {
			if (siteCode.equals("1") || siteCode.equals("2")) {
				objRegEx = new RegularExpression(getValidZipPattern());
			} else if (siteCode.equals("3")) {
				objRegEx = new RegularExpression(getValidZipPatternForCA());
			}
			
			if (objRegEx != null && objRegEx.matches(zipCode)) {
				validZip = true;
			} else {
				logError("Zipcode passed : " + zipCode + " is not a valid zip for Site Id : " + siteCode);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. isValidZip methos ends with returning validZip :: " + validZip );
		}
		return validZip;
	}
	
	/**
	 * This method validates the state passed as input for the site
	 * @param stateCode
	 * @param siteCode
	 * @return boolean
	 */
	
	public boolean isValidState(String stateCode, String siteCode) {
		
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. isValidState methos starts with state code :: " + stateCode + " sand site code :: " + siteCode);
		}
		
		boolean validState = false;
		String allowedStates = null;
		List<String> allowedStatesList = null;
		
		if (!BBBUtility.isEmpty(stateCode) && !BBBUtility.isEmpty(siteCode)) {
			try {
				if (siteCode.equals("1") || siteCode.equals("2")) {
					
					allowedStatesList = getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "STATE_ABBREVATION");
				} else if (siteCode.equals("3")) {
					allowedStatesList = getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "STATE_ABBREVATION_CA");
				}
			} catch (BBBSystemException | BBBBusinessException e) {
					logError("Exception occured while fetching config key", e);
			}
			if (!BBBUtility.isListEmpty(allowedStatesList)) {
				allowedStates = allowedStatesList.get(0);
			}
			if (allowedStates != null && allowedStates.indexOf(":" + stateCode + ":") > -1) {
				validState = true;
			} else {
				logError("State passed : " + stateCode + " is not a valid state for Site  Id : " + siteCode);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. isValidState methos ends with returning validState :: " + validState );
		}
		return validState;
	}
	
	/**
	 * This method validates the phone number passed as input is a valid one
	 * @param phoneNumToCheck
	 * @return boolean
	 */
	
	public boolean isValidPhone(String phoneNumToCheck) {
		boolean isValid = false;
		String strippedNum = BBBCoreConstants.BLANK;
		strippedNum = stripNonAlphaNumerics(phoneNumToCheck	+ BBBCoreConstants.BLANK);
		if (isNumeric(strippedNum) && strippedNum.length() >= 10 && strippedNum.length() <= 15) {
			isValid = true;
		} else {
			logError("Phone number passed in input :: " + phoneNumToCheck + " is not a valid phone number");
		}

		return isValid;
	}

	public String stripNonAlphaNumerics(String sValue) {
		String sResult = BBBCoreConstants.BLANK;
		String sFilterPattern = getNonAlphaNumericPattern();

		if (sValue != null) {
			sResult = BBBCoreConstants.BLANK;
			sResult = sValue.replaceAll(sFilterPattern, BBBCoreConstants.BLANK);
		}

		return sResult;
	}

	public boolean isNumeric(String sValue) {
		RegularExpression objRegEx = new RegularExpression(getNumericPattern());
		return objRegEx.matches(sValue);
	}


	/**
	 * This method formats the phone number
	 * @param phone
	 * @return String
	 */
	public String getPhone(final String phone) {
		
		String phoneNumberToReturn = BBBCoreConstants.BLANK;
		if (!BBBUtility.isEmpty(phone)) {
			if (phone.length() > 10) {
				phoneNumberToReturn = phone.substring(0, 10);
			} else {
				phoneNumberToReturn = phone;
			}
		}
		return phoneNumberToReturn;
	}

	/**
	 * This method formats the ext phone number, if provided
	 * 
	 * @param phone
	 * @return
	 */
	public String getPhoneExt(final String phone) {
		if (!BBBUtility.isEmpty(phone) && phone.length() > 10) {
			return phone.substring(10);
		} else {
			return "";
		}
	}

	/**
	 * This method strips out all the white spaces and returns in UPPER case
	 * 
	 * @param sValue
	 * @return
	 */
	public String stripAllWhiteSpaceAndMakeUppercase(final String sValue) {
		if (sValue == null) {
			return null;
		}
		String sResult = sValue.replaceAll(" +", "");
		return sResult.toUpperCase();
	}

	public boolean isValidEmail(String sVal)
	{
		if (sVal == null)
			return false;

		boolean bValid = false;
		String sNewVal = sVal.trim();
		//string[] arrBadChars = new string[7];
		String[] arrBadChars = new String[6];
		String[] arrLastSegment = new String[2];

		arrBadChars[0] = ";";
		arrBadChars[1] = ",";
		arrBadChars[2] = "!";
		arrBadChars[3] = " ";
		arrBadChars[4] = "/";
		arrBadChars[5] = ":";

		for (int i = 0; i < arrBadChars.length; i++)
		{
			if (sNewVal.indexOf(arrBadChars[i]) > -1)
				return false;
		}

		int nAtLoc = sNewVal.indexOf(BBBCoreConstants.AT_THE_RATE, 0);
		if (nAtLoc < 0)
			return false;

		int nAtDotLoc = sNewVal.indexOf(BBBCoreConstants.DOT_EXTENTION, nAtLoc);
		if (nAtDotLoc < 0)
			return false;

		arrLastSegment = sNewVal.split(BBBGiftRegistryConstants.DOT_EXTENTION_WITH_SLASH);
		
		String sAfterAt = sNewVal.substring(nAtLoc + 1, nAtLoc + 2);
		String sLastSeg = arrLastSegment[arrLastSegment.length - 1];

		//THIS DESCRIBES A STRING FROM 2 to 6 CHARACTERS IN LENGTH
		RegularExpression objAlpha2To6RegExp = new RegularExpression(getEmailPattern());

		//THIS DESCRIBES AN ALPHA NUMERIC STRING
		RegularExpression objAlphaNumRegExp = new RegularExpression(getAlphaNumericPattren());

		if (nAtLoc > 0 && sNewVal.indexOf(BBBCoreConstants.DOT_EXTENTION, nAtLoc + 2) > 0 &&
				(nAtDotLoc + 1) < sNewVal.length() &&
				(sNewVal.indexOf(BBBCoreConstants.DOT_EXTENTION, sNewVal.length() - 1) != sNewVal.length() - 1) &&
				sNewVal.length() > 5 && sNewVal.indexOf(BBBCoreConstants.AT_THE_RATE, nAtLoc + 1) < 0 &&
				sNewVal.indexOf(BBBCoreConstants.DOT_EXTENTION+BBBCoreConstants.AT_THE_RATE, 0) < 0 &&
				objAlpha2To6RegExp.matches(BBBCoreConstants.DOT_EXTENTION + sLastSeg) &&
				objAlphaNumRegExp.matches(sAfterAt))
			bValid = true;

		if (sNewVal.indexOf(BBBCoreConstants.AT_THE_RATE+BBBCoreConstants.DOT_EXTENTION, 0) >= 0)
			bValid = false;
		
		return bValid;
	}

	/** This method checks if the passed date as input in long format is a valid JDA date or not
	 * @param jdaDate
	 * @return boolean
	 */
	public boolean isJDADate(long jdaDate) {
		
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. isJDADate method starts with input date :: " + jdaDate );
		}
		boolean isDateUsable = true;
		String sDate = BBBCoreConstants.BLANK;
		long pastDateLimit = Long.valueOf(formatDate(addYears(new Date(), -2), BBBCoreConstants.WS_DATE_FORMAT));
		long futureDateLimit = Long.valueOf(formatDate(addYears(new Date(), 5), BBBCoreConstants.WS_DATE_FORMAT));
		try {
			sDate = String.valueOf(jdaDate);
			if (!isDate(sDate.substring(4, 6) + BBBCoreConstants.SLASH + sDate.substring(6, 8) + BBBCoreConstants.SLASH + sDate.substring(0, 4))) {
				throw new Exception(BBBGiftRegistryConstants.ERROR_INVALID_DATE1);
			}
			if (jdaDate < pastDateLimit) {
				throw new Exception(BBBGiftRegistryConstants.ERROR_INVALID_DATE2);
			}
			if (jdaDate > futureDateLimit) {
				throw new Exception(BBBGiftRegistryConstants.ERROR_INVALID_DATE3);
			}
			if (sDate.length() != 8) {
				throw new Exception(BBBGiftRegistryConstants.ERROR_INVALID_DATE4);
			}
		} catch (Exception ex) {
			isDateUsable = false;
			logError("Invalid JDA Date :: " + jdaDate, ex);
		}
		return isDateUsable;
	}


	/**
	 * This method validates preferred store with registry
	 * 
	 * @param regPrefStore
	 * @return
	 */
	public List<ValidationError> validateRegPrefStore(final RegistryPrefStoreVO regPrefStore) {
		
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. validateRegPrefStore method starts ");
		}
		List<ValidationError> valErrorsList = new ArrayList<ValidationError>();
		ValidationError valError = new ValidationError();
		if (!BBBUtility.isEmpty(regPrefStore.getStoreNum())) {
			try {
				Integer.parseInt(regPrefStore.getStoreNum());
			} catch (NumberFormatException e) {
				valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_PREFERRED_STORE_NUM);
				valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_PREFERRED_STORE_NUM);
				valErrorsList.add(valError);
				logError(BBBGiftRegistryConstants.ERROR_INVALID_PREFERRED_STORE_NUM + " :: " + regPrefStore.getStoreNum(), e);
			}
		}
		
		if (!BBBUtility.isEmpty(regPrefStore.getContactFlag()) && !"N".equalsIgnoreCase(regPrefStore.getContactFlag())
				&& !"P".equalsIgnoreCase(regPrefStore.getContactFlag())
				&& !"E".equalsIgnoreCase(regPrefStore.getContactFlag())) {
			valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_STORE_CONTACT_METHOD);
			valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_STORE_CONTACT_METHOD);
			valErrorsList.add(valError);
			logError(BBBGiftRegistryConstants.ERROR_INVALID_STORE_CONTACT_METHOD + " :: " + regPrefStore.getStoreNum());
		}
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. validateRegPrefStore method ends " + valErrorsList);
		}
		return valErrorsList;
	}

	/**
	 * This method validates header inputs
	 * 
	 * @param siteFlag
	 * @param regHeader
	 * @param isCreate
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public List<ValidationError> validateHeaderInput(String siteFlag, RegistryHeaderVO regHeader, boolean isCreate) throws BBBSystemException, BBBBusinessException	{
		
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. validateHeaderInput method starts for site id :: " + siteFlag);
		}
		
		String isPublic = regHeader.getIsPublic();
		String registryType = regHeader.getEventType();
		String eDate = regHeader.getEventDate();
		String signUp = regHeader.getPromoEmailFlag();
		List<ValidationError> valErrorsList = new ArrayList<ValidationError>();
		ValidationError valError = new ValidationError();

		if (isCreate) {
			valErrorsList.addAll(isValidRegistryType(siteFlag, registryType));
		}

		if (BBBUtility.isEmpty(isPublic)
				|| (!BBBCoreConstants.STRING_ONE.equals(isPublic) && !BBBCoreConstants.STRING_ZERO.equals(isPublic))) {
			valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_IS_PUBLIC);
			valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_IS_PUBLIC);
			valErrorsList.add(valError);
			logError(BBBGiftRegistryConstants.ERROR_INVALID_IS_PUBLIC + " :: " + isPublic);
		} else {
			isPublic = BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(isPublic) ? BBBCoreConstants.NO_CHAR : BBBCoreConstants.YES_CHAR;
			regHeader.setIsPublic(isPublic);
		}

		if (!BBBUtility.isEmpty(eDate) && !BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(eDate)) {
			if (!isNumeric(eDate)) {
				valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_EVENT_DATE);
				valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_EVENT_DATE);
				valErrorsList.add(valError);
				logError("Event Date :: " + eDate + " is not in numeric format");
			} else if (!isJDADate(Long.valueOf(eDate))) {
				valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_EVENT_DATE);
				valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_EVENT_DATE);
				valErrorsList.add(valError);
				logError("Event Date :: " + eDate + " is not a valid JDA Date");
			}
		}else {
				logDebug("Event date is :" + eDate);
		}

		if (!BBBUtility.isEmpty(signUp)) {
			if (!BBBCoreConstants.NO_CHAR.equalsIgnoreCase(signUp) && !BBBCoreConstants.YES_CHAR.equalsIgnoreCase(signUp)) {
				valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_SIGN_UP);
				valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_SIGN_UP);
				valErrorsList.add(valError);
				logError(BBBGiftRegistryConstants.ERROR_INVALID_SIGN_UP + " :: " + signUp);
			}
		}

		if (!BBBUtility.isEmpty(regHeader.getShowerDate())) {
			long showerDate = 0;
			try {
				showerDate = Long.parseLong(regHeader.getShowerDate());
			} catch (NumberFormatException e) {
				valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_SHOWER_DATE);
				valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_SHOWER_DATE);
				valErrorsList.add(valError);
				logError("Shower Date :: " + regHeader.getShowerDate() + " is not in numeric format ", e );
			}

			if (!isJDADate(showerDate)) {
				valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_SHOWER_DATE);
				valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_SHOWER_DATE);
				valErrorsList.add(valError);
				logError("Shower Date :: " + showerDate + " is not a valid JDA Date");
			}
		}

		if (!BBBUtility.isEmpty(regHeader.getOtherDate())) {
			long engDate = 0;
			try {
				engDate = Long.parseLong(regHeader.getOtherDate());
			} catch (NumberFormatException e) {
				valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_OTHER_DATE);
				valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_OTHER_DATE);
				valErrorsList.add(valError);
				logError("Other Date :: " + regHeader.getOtherDate() + " is not in numeric format ", e );
			}

			if (BBBGiftRegistryConstants.EVENT_TYPE_BIRTHDAY.equalsIgnoreCase(regHeader.getEventType())) {
				if (!isDate(regHeader.getOtherDate().substring(4, 6) + BBBCoreConstants.SLASH
						+ regHeader.getOtherDate().substring(6, 8) + BBBCoreConstants.SLASH
						+ regHeader.getOtherDate().substring(0, 4))) {
					valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_BIRTH_DATE);
					valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_BIRTH_DATE);
					valErrorsList.add(valError);
					logError(BBBGiftRegistryConstants.ERROR_INVALID_BIRTH_DATE + " :: " + regHeader.getOtherDate());
				}
			} else {
				if (!isJDADate(engDate)) {
					valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_ENG_OTHER_DATE);
					valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_OTHER_DATE);
					valErrorsList.add(valError);
					logError("Other Date :: " + engDate + " is not a valid JDA Date");
				}
			}
		}
		String networkAffiliation = regHeader.getNetworkAffiliation();
		if (!BBBUtility.isEmpty(networkAffiliation) && (!BBBCoreConstants.YES_CHAR.equalsIgnoreCase(networkAffiliation)
				&& !BBBCoreConstants.NO_CHAR.equalsIgnoreCase(networkAffiliation))) {
			valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_NETWORK_AFFILIATION);
			valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_NETWORK_AFFILIATION);
			valErrorsList.add(valError);
			logError(BBBGiftRegistryConstants.ERROR_INVALID_NETWORK_AFFILIATION + " :: " + networkAffiliation);
		}

		if (BBBGiftRegistryConstants.EVENT_TYPE_WEDDING.equalsIgnoreCase(regHeader.getEventType())
				|| BBBGiftRegistryConstants.EVENT_TYPE_CERMONY.equalsIgnoreCase(regHeader.getEventType())) {
			if (!BBBUtility.isEmpty(regHeader.getEstimateNumGuests())) {
				try {
					long estGuests = Long.parseLong(regHeader
							.getEstimateNumGuests());
					if (estGuests < 0 || estGuests > 9999) {
						valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_EST_GUESTS);
						valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_EST_GUESTS);
						valErrorsList.add(valError);
						logError(BBBGiftRegistryConstants.ERROR_INVALID_EST_GUESTS + " :: " + regHeader.getEstimateNumGuests());
					}
				} catch (NumberFormatException e) {
					valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_EST_GUESTS);
					valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_EST_GUESTS);
					valErrorsList.add(valError);
					logError("Estimated Guest Count is not a Number :: " + regHeader.getEstimateNumGuests(), e );
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. validateHeaderInput method ends " + valErrorsList );
		}
		return valErrorsList;
	}


	/**
	 * This method is used to validate registry type on the basis of site flag
	 * 
	 * @param siteFlag
	 * @param registryType
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public List<ValidationError> isValidRegistryType(final String siteFlag, final String registryType) throws BBBSystemException, BBBBusinessException {
		
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. isValidRegistryType method starts for site id :: " + siteFlag + " and registryType :: " + registryType);
		}
		List<ValidationError> valErrorsList = new ArrayList<ValidationError>();
		ValidationError valError = new ValidationError();
		List<String>validRegTypeList = null;
		String validRegTypes = null;
		if ("1".equalsIgnoreCase(siteFlag)) {
			validRegTypeList = getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "BbbyValidRegTypes");
		} else if ("2".equalsIgnoreCase(siteFlag)) {
			validRegTypeList = getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "BabyValidRegTypes");
		} else if ("3".equalsIgnoreCase(siteFlag)) {
			validRegTypeList = getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "CAValidRegTypes");
		}
		if (!BBBUtility.isListEmpty(validRegTypeList)) {
			validRegTypes = validRegTypeList.get(BBBCoreConstants.ZERO);
		}

		boolean isValidRegistryType = false;
		if (!BBBUtility.isEmpty(registryType) && !BBBUtility.isEmpty(validRegTypes) && validRegTypes.indexOf(registryType) > -1) {
			isValidRegistryType = true;
		}

		if (!isValidRegistryType) {
			valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_REGISTRY_TYPE);
			valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_REGISTRY_TYPE);
			valErrorsList.add(valError);
			logError(BBBGiftRegistryConstants.ERROR_INVALID_REGISTRY_TYPE + " :: " + registryType + " for Site Flag :: " + siteFlag);
		}
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. isValidRegistryType method ends " + valErrorsList);
		}
		return valErrorsList;
	}



	public static boolean validateSiteFlag(final String siteFlag)
	{
		boolean isValid = true;
		if (siteFlag != "1" && siteFlag != "2" && siteFlag != "3" && siteFlag != "5")
		{
			isValid = false;
		}

		//		if (!isValid)
		//			status = new Status { ErrorExists = true, ErrorMessage = "SiteFlag is Invalid.", DisplayMessage = String.Empty, ID = Constants.Error.UserTokenSiteFlagHttpsInvalid, ValidationErrors = null };

		return isValid;
	}

	/**
	 * This method validates input parameters
	 * 
	 * @param list
	 * @return ErrorStatus
	 */
	public ErrorStatus validateInput(final String[] list) {
		
		boolean isValid = true;
		ErrorStatus errorStatus = new ErrorStatus();
		for (String input : list) {
			if (hasInvalidChars(input))
				isValid = false;
			break;
		}
		if(!isValid) {
               errorStatus.setErrorExists(true);
			   errorStatus.setErrorMessage(BBBGiftRegistryConstants.INVALID_CHARS_IN_INPUT);
			   errorStatus.setDisplayMessage(null);
			   errorStatus.setErrorId(Integer.parseInt(BBBGiftRegistryConstants.InputFieldsUnexpectedFormat));
			   errorStatus.setValidationErrors(null);
		} 
		return errorStatus;
	}

	/**
	 * @param sValue
	 * @return
	 */
	public boolean hasInvalidCharsForHackOnly(final String sValue) {
		boolean hasInvalidChars = false;

		if(!BBBUtility.isEmpty(sValue)) {
			RegularExpression objRegEx = new RegularExpression("\\\\|<|>");
			if (objRegEx.matches(sValue)) {
				hasInvalidChars = true;
				logError("Input value " + sValue + " has invalid charecters for hack");
			}
		}
		return hasInvalidChars;
	}

	/**
	 * @param list
	 * @return
	 */
	public ErrorStatus validateInputForHackOnly(final String[] list) {
		boolean isValid = true;
		ErrorStatus errorStatus = new ErrorStatus();
		for (String input : list) {
			if (hasInvalidCharsForHackOnly(input)) {
				isValid = false;
			}
			break;
		}
		if (!isValid) {
			errorStatus.setErrorExists(true);
			errorStatus.setErrorMessage(BBBGiftRegistryConstants.INVALID_CHARS_IN_INPUT);
			errorStatus.setDisplayMessage(null);
			errorStatus.setErrorId(Integer.parseInt(BBBGiftRegistryConstants.InputFieldsUnexpectedFormat));
			errorStatus.setValidationErrors(null);
		}
		return errorStatus;
	}

	/**
	 * This method validates various fields passed as input to create registryand return the list of errors/invalid input
	 * 
	 * @param siteFlag
	 * @param regBaby
	 * @param regHeader
	 * @param regNames
	 * @param shippingNames
	 * @param isCreate
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public List<ValidationError> validateRegistryFields(String siteFlag, RegistryBabyVO regBaby, RegistryHeaderVO regHeader,
			List<RegNamesVO> regNames, List<RegNamesVO> shippingNames, boolean isCreate) throws BBBBusinessException, BBBSystemException {
		
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. validateRegistryFields method starts ");
		}
		List<ValidationError> valErrors = new ArrayList<ValidationError>();
		valErrors.addAll(validateHeaderInput(siteFlag, regHeader, isCreate));
		valErrors.addAll(validateNameEntry(siteFlag, regHeader.getEventType(), BBBGiftRegistryConstants.REG_SUB_TYPE, regNames.get(BBBCoreConstants.ZERO), isCreate));

		if (BBBGiftRegistryConstants.EVENT_TYPE_WEDDING.equalsIgnoreCase(regHeader.getEventType())
				|| BBBGiftRegistryConstants.EVENT_TYPE_CERMONY.equalsIgnoreCase(regHeader.getEventType())) {
			if (regNames.size() > BBBCoreConstants.ONE) {
				valErrors.addAll(validateNameEntry(siteFlag, regHeader.getEventType(), BBBGiftRegistryConstants.COREG_SUB_TYPE, regNames.get(BBBCoreConstants.ONE), isCreate));
			}
		}

		if (shippingNames.size() > BBBCoreConstants.ZERO) {
			valErrors.addAll(validateNameEntry(siteFlag, regHeader.getEventType(), BBBGiftRegistryConstants.SH, shippingNames.get(BBBCoreConstants.ZERO), isCreate));
		}

		if (shippingNames.size() == BBBCoreConstants.TWO) {
			valErrors.addAll(validateNameEntry(siteFlag, regHeader.getEventType(), BBBGiftRegistryConstants.FU, shippingNames.get(BBBCoreConstants.ONE), isCreate));
		}

		if (BBBGiftRegistryConstants.EVENT_TYPE_BABY.equalsIgnoreCase(regHeader.getEventType())) {
			valErrors.addAll(validateBabyInput(regBaby));
		}
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. validateRegistryFields methos ends " + valErrors);
		}
		return valErrors;
	}

	/**
	 * This method maps to validates the values for baby registry
	 * @param regBaby
	 * @return
	 */
	public List<ValidationError> validateBabyInput(RegistryBabyVO regBaby) {
		
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. validateBabyInput methos starts ");
		}
		String gender = regBaby.getGender();
		String decor = regBaby.getDecor();
		String firstname = regBaby.getFirstName();
		List<ValidationError> valErrorsList = new ArrayList<ValidationError>();
		ValidationError valError = new ValidationError();

		if (!BBBUtility.isEmpty(gender.trim())) {
			if (!"B".equalsIgnoreCase(gender) && !"G".equalsIgnoreCase(gender) && !"S".equalsIgnoreCase(gender)
					&& !"T".equalsIgnoreCase(gender)) {
				valError.setKey(BBBGiftRegistryConstants.BABY_GENDER);
				valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_BABY_GENDER);
				valErrorsList.add(valError);
				logError(BBBGiftRegistryConstants.ERROR_INVALID_BABY_GENDER + " :: " + gender);
			}
		}

		if (!BBBUtility.isEmpty(decor) && decor.length() > 100) {
			valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_BABY_DECOR_THEME);
			valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_BABY_DECOR_THEME);
			valErrorsList.add(valError);
			logError(BBBGiftRegistryConstants.ERROR_INVALID_BABY_DECOR_THEME + " :: " + decor);
		}

		if (hasInvalidChars(firstname)) {
			valError.setKey(BBBGiftRegistryConstants.BABY_NAME);
			valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_CHARS_IN_BABY_NAME);
			valErrorsList.add(valError);
			logError("Invalid Baby first Name");
		} else if (!BBBUtility.isEmpty(firstname) && firstname.length() > 30) {
			valError.setKey(BBBGiftRegistryConstants.BABY_NAME);
			valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_BABY_NAME);
			valErrorsList.add(valError);
			logError("Baby first Name too long");
		}
		
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. validateBabyInput methos ends " + valErrorsList);
		}
		return valErrorsList;
	}

	/**
	 * This method checks if date is valid.
	 * 
	 * @param sDate
	 * @return boolean
	 */
	public boolean isDate(final String sDate) {
		boolean bIsDate = false;
		SimpleDateFormat formatter = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
		try {
			Date date = formatter.parse(sDate);
			formatter.format(date);
			bIsDate = true;
		} catch (ParseException objError) {
			logError("Date : " + sDate + " is not parsable in required MM/dd/yyyy format", objError);
		}
		return bIsDate;
	}
	
	/**
	 * This method returns date in JDA(yyyyMMddHHmmss) format.
	 * 
	 * @param sDate
	 * @return boolean
	 */
	
	public String getJDADateTime() {
		
		DateTime jdaDate = new DateTime();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");

		return formatter.print(jdaDate);
	}

	/**
	 * This method is used to add/subtract years in date
	 * 
	 * @param currentDate
	 * @param years
	 * @return Date
	 */
	public Date addYears(Date currentDate, int years) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(currentDate);
		cal.add(Calendar.YEAR, years);
		return cal.getTime();
	}

	/**
	 * This method is used to format date in specified format
	 * 
	 * @param sDate
	 * @param format
	 * @return
	 */
	public String formatDate(Date sDate, final String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String formattedDate = null;
		try {
			formattedDate = formatter.format(sDate);
		} catch (Exception objError) {
			logError("Date : " + sDate + " is not parsable in " + format + " format" + objError);
		}
		return formattedDate;
	}
	
	
	/**
	 * This method is used to return Date object corresponding to the input string
	 * @param sDate
	 * @param format
	 * @return Date
	 */
	public Date formatStringToDate(String sDate, final String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date formattedDate = null;
		try {
			formattedDate = formatter.parse(sDate);
		} catch (Exception objError) {
			logError("Date : " + sDate + " is not parsable in " + format + " format" + objError);
		}
		return formattedDate;
	}

	/**This method is used to log exception in database using stored procedure
	 *  which occurred while validation of the inputs or while invoking stored procedure
	 * @param serviceName
     * @param connection
     * @param objectType
     * @param exception
     * @param args
     * @return Object
     * @throws BBBSystemException
     * @throws BBBBusinessException
     */
	public Object logAndFormatError(String serviceName, Connection connection,
			String objectType, Exception exception, Object... args)
			throws BBBSystemException, BBBBusinessException {
		List<String> logErrorsFromConfig = null;
		if (isLoggingDebug()) {
			logDebug("BBBGiftRegistryUtils. logAndFormatError methos starts for Service :: "
					+ serviceName);
		}

		long logId = -1;
		logErrorsFromConfig = getCatalogTools().getAllValuesForKey(
				BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,
				BBBGiftRegistryConstants.CONFIG_KEY_LOG_ERRORS);

		if (!BBBUtility.isListEmpty(logErrorsFromConfig)) {
			if (BBBCoreConstants.YES_CHAR.equals(logErrorsFromConfig.get(0))) {
				logId = logException(serviceName, connection, exception, args);
			} else {
				logDebug("Logging Exception to Db is false");
			}
		} else {
			logDebug("Config key is emptyfor  "+BBBGiftRegistryConstants.CONFIG_KEY_LOG_ERRORS);
		}

		if (isLoggingDebug()) {
			logDebug("Recieved LogId after logging the exeption :: " + logId);
		}

		String errorMessage = BBBCoreConstants.BLANK;
		List<String> showDetailedMsg = getCatalogTools()
				.getAllValuesForKey(
						BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,
						BBBGiftRegistryConstants.CONFIG_KEY_SHOW_DETAILED_ERRORS);
		
		if (!BBBUtility.isListEmpty(showDetailedMsg)) {
			if (BBBCoreConstants.YES_CHAR.equals(showDetailedMsg.get(0))) {
				errorMessage = formatMessage(exception, args);
			} else {
				errorMessage = "Fatal error. Process support Id = " + logId
						+ " (-1 means logger failed)";
			}
		}
		else
		{
			logDebug("Config Key is null for show Detailes message");
		}
		// removed toString() call from exception.getMessage property because it may be null and calling toString cause null
		return setErrorInResponse(objectType, errorMessage, exception
				.getMessage(), true, 200, null);

	}
     
     /**This method is used to log exception in database using stored procedure
 	 * which occurred while validation of the inputs or while invoking stored procedure
 	 * @param serviceName
     * @param connection
     * @param exception
     * @param args
     * @return long
     */

     public long logException(String serviceName, Connection connection, Exception originalException, Object... args) {
    	 
    	 	
            long processID = -1;
            List <String> processCDList = null;
            String process_cd = BBBCoreConstants.BLANK;
            CallableStatement cs = null;
            try {
            	processCDList = getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, "PROCESS_CD");
            	if (!BBBUtility.isListEmpty(processCDList)) {
					process_cd = processCDList.get(BBBCoreConstants.ZERO);
				}
                   if(connection == null) {
                         connection = ((GSARepository) getRegistryInfoRepository()).getDataSource().getConnection();
                   }
                   if (connection != null) {
                	   
                          cs = connection.prepareCall(BBBGiftRegistryConstants.CREATE_SUPPORT_REC);
							
	                      cs.setString(1, process_cd);
	                      cs.setString(2, serviceName);
	                      cs.setString(3, formatMessage(originalException, args));
	                      cs.registerOutParameter(4 , OracleTypes.NUMBER);
	
	                      cs.executeQuery();
	
	                      processID = cs.getInt(4);
                   }
            }
            catch (SQLException e) {
                   this.logError("Error occurred while executing stored proc", e);
            } catch (BBBSystemException e) {
                this.logError("BBBSystemException occurred while getting the config key:: PROCESS_CD from Config Ket Type :: " + BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, e);
            } catch (BBBBusinessException e) {
            	this.logError("BBBBusinessException occurred while getting the config key:: PROCESS_CD from Config Ket Type :: " + BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, e);
            }
            finally {
            	try {
                   	if (cs != null) {
						cs.close();
                   	}
                    if(connection != null) {
                         connection.close();
                    }
            	} catch (SQLException e) {
            		this.logError("Error occurred while closing connection", e);
                }
            }

            return processID;

     }
     
     /** This method is used to format the message which is logged in case of any exception
  	 * which occurred while validation of the inputs or while invoking stored procedure
     * @param exception
     * @param args
     * @return String
     */

     private String formatMessage(Exception exception, Object... args) {
            String host = getHostName();
            String argString = getArgString(args);
            String message = host + "\n" +
                         argString + "\n" +
                         exception.toString();

            message = message.length() >= 500 ? message.substring(0, 500) : message;
            logDebug("Exception message "+exception.toString());
            return message;
     }

     private String getHostName() {
    	 
            String host = BBBCoreConstants.BLANK;
            try {
                   host = InetAddress.getLocalHost().getHostName();
            } catch(Exception ex) {
            	logError("Exception occurred while getting the host name ", ex);
                host = "Unkown";
            }
            return host;
     }

     private String getArgString(Object... args) {
    	 
    	 String val = null;
    	 if(args != null) {
    		 val = BBBCoreConstants.BLANK;
    		 for (Object s : args) {
    			 if (s != null) {
    				 val += s.toString() + BBBCoreConstants.COMMA;
				} else {
					val += BBBCoreConstants.BLANK + BBBCoreConstants.COMMA;
				}
    			 
    		 }
    	 }

    	 return val;
     }
     
     /**
  	 * This method validates the Values for any registrant or shipping address and populate the error list if any value is invalid
  	 * @param siteFlag
  	 * @param eventType
  	 * @param addrType
  	 * @param regName
  	 * @param isCreate
  	 * @return 
  	 * 
  	 */
     
     public List<ValidationError> validateNameEntry(String siteFlag,
 			String eventType, String addrType, RegNamesVO regName, boolean isCreate) {
 		
    	if (isLoggingDebug()) {
 			logDebug("BBBGiftRegistryUtils. validateNameEntry method starts for Registrant type :: " + addrType);
 		}
 		List<ValidationError> valErrorsList = new ArrayList<ValidationError>();
 		boolean isPrimaryRegistrant = BBBGiftRegistryConstants.REG_SUB_TYPE.equals(addrType);
 		String addrDescription = this.getRegDescLookUpMap().get(addrType);
 		String prefix = this.getPrefixLookUpMap().get(addrType);
 		
 			
 			
 		if (!BBBUtility.isEmpty(regName.getLastName()) || isPrimaryRegistrant) {
 			validateLastName(regName.getLastName(), valErrorsList, prefix, addrDescription);
 		}
 			
 		if (!BBBUtility.isEmpty(regName.getFirstName()) || isPrimaryRegistrant) {
 			validateFirstName(regName.getFirstName(), valErrorsList, prefix, addrDescription);
 		}
 			
 		if (!BBBUtility.isEmpty(regName.getDayPhone())) {
 			validatePhoneNumber(regName.getDayPhone(), valErrorsList, prefix, addrDescription);
 		}
 			
 		if (!BBBUtility.isEmpty(regName.getEvePhone())) {
 			validateMobileNumber(regName.getEvePhone(), valErrorsList, prefix, addrDescription);
 		}
 			
 		if (!BBBUtility.isEmpty(regName.getCompany())) {
 			validateCompany(regName.getCompany(), valErrorsList, prefix, addrDescription);
 		}
 			
 		if (!BBBUtility.isEmpty(regName.getAddress1())) {
 			validateAddressOne(regName.getAddress1(), valErrorsList, prefix, addrDescription);
 		}
 			
 		if (!BBBUtility.isEmpty(regName.getAddress2())) {
 			validateAddressTwo(regName.getAddress2(), valErrorsList, prefix, addrDescription);
 		}
 			
 		if (!BBBUtility.isEmpty(regName.getCity())) {
 			validateCity(regName.getCity(), valErrorsList, prefix, addrDescription);
 		}
 			
 		if (!BBBUtility.isEmpty(regName.getState())) {
 			validateState(regName.getState(), valErrorsList, prefix, addrDescription, siteFlag);
 		}
 			
 		if (!BBBUtility.isEmpty(regName.getZipCode())) {
 			validateZipCode(regName.getZipCode(), valErrorsList, prefix, addrDescription, siteFlag);
 		}
 			
 		if (BBBGiftRegistryConstants.COREG_SUB_TYPE.equals(addrType) || BBBGiftRegistryConstants.REG_SUB_TYPE.equals(addrType)) {
 			
 			if (!BBBUtility.isEmpty(regName.getEmailId()) || isPrimaryRegistrant) {
 				validateEmail(regName.getEmailId(), valErrorsList, isPrimaryRegistrant, prefix, addrDescription);
 			}
 			
 			if (BBBGiftRegistryConstants.CO.equals(addrType) && !BBBUtility.isEmpty(regName.getEmailFlag())) {
 				validateEmailOptinFlag(regName.getEmailFlag(), valErrorsList, prefix, addrDescription);
 			}
 			
 			if (isPrimaryRegistrant && !BBBUtility.isEmpty(regName.getPrefContMeth())) {
 				validatePrefContactMeth(regName.getPrefContMeth(), valErrorsList, prefix, addrDescription);
 			}
 			
 			if (isPrimaryRegistrant && !BBBUtility.isEmpty(regName.getPrefContTime())) {
 				validatePrefContactTime(regName.getPrefContTime(), valErrorsList, prefix, addrDescription);
 			}
 			
 			if (isPrimaryRegistrant &&  (BBBGiftRegistryConstants.EVENT_TYPE_WEDDING.equals(eventType) || BBBGiftRegistryConstants.EVENT_TYPE_BABY.equals(eventType)) && !BBBUtility.isEmpty(regName.getAffiliateOptIn())) {
 				validateAffiliateOptIn(regName.getAffiliateOptIn(), valErrorsList, prefix, addrDescription);
 			}
 			
 			if (isPrimaryRegistrant && BBBUtility.isEmpty(regName.getAtgProfileId())) {
 				ValidationError valError = new ValidationError();
 				valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_REG_PROFILE_ID);
 				valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_REG_PROFILE_ID_EMPTY);
 				valErrorsList.add(valError);
 				logError("Profile Id is empty for Primary Registrant");
 			}
 			
 		}
 		
 		if (BBBGiftRegistryConstants.FU.equals(addrType) && !BBBUtility.isEmpty(regName.getAsOfDateFtrShipping())) {
 			validateFutureShipDate(regName.getAsOfDateFtrShipping(), isCreate, valErrorsList, addrDescription, prefix);
 		}
 		
 		if (isLoggingDebug()) {
 			logDebug("BBBGiftRegistryUtils. validateNameEntry method ends for Registrant type :: " + addrType + " [ " + valErrorsList + " ] ");
 		}
 		return valErrorsList;
 	}
     
     /**
 	 * This method validates the Future Shipping Date selected by any registrant and populate the error list if Future Shipping Date is invalid
 	 * @param asOfDateFtrShipping
 	 * @param isCreate
 	 * @param valErrorsList
 	 * @param prefix
 	 * @param addrDescription
 	 * 
 	 */
     
 	public void validateFutureShipDate(String asOfDateFtrShipping, boolean isCreate,
 			List<ValidationError> valErrorsList, String addrDescription, String prefix) {
 		
 		Long ftrShippingDate = null;
 		try {
 			ftrShippingDate = Long.parseLong(asOfDateFtrShipping);
 		} catch (NumberFormatException e) {
 			ValidationError valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_AS_OF_DATE);
 			valError.setValue(BBBGiftRegistryConstants.ERROR_INVALID_DATE4);
 			valErrorsList.add(valError);
 			logError(BBBGiftRegistryConstants.ERROR_INVALID_DATE4 + "::" + asOfDateFtrShipping, e);
 		}
 		if (!isNumeric(asOfDateFtrShipping)) {
 			ValidationError valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_AS_OF_DATE);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_INVALID_AS_OF_DATE);
 			valErrorsList.add(valError);
 			logError(BBBGiftRegistryConstants.ERROR_INVALID_DATE4 + "::" + asOfDateFtrShipping );
 		} else if (isCreate && !isJDADate(ftrShippingDate)) {
 			ValidationError valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_AS_OF_DATE);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_INVALID_AS_OF_DATE);
 			valErrorsList.add(valError);
 			logError("Future Shipping Date :: " + asOfDateFtrShipping + " is not a valid JDA date");
 		} else if (!isCreate && !isDate(asOfDateFtrShipping.substring(4, 6) + BBBCoreConstants.SLASH
 				+ asOfDateFtrShipping.substring(6, 8) + BBBCoreConstants.SLASH
 				+ asOfDateFtrShipping.substring(0, 4))) {
 			ValidationError valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_AS_OF_DATE);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_INVALID_AS_OF_DATE);
 			valErrorsList.add(valError);
 			logError("Future Shipping Date :: " + asOfDateFtrShipping + " is not a valid date");
 		}
 	}
 	
 	/**
	 * This method validates the prefContMeth of any registrant and populate the error list if prefContMeth is invalid
	 * @param prefContMeth
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validatePrefContactMeth(String prefContMeth, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		if (Integer.parseInt(prefContMeth) < 0 || Integer.parseInt(prefContMeth) > 4){
 			ValidationError valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_REG_PREF_CONT_METH);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_INVALID_REG_PREF_CONT_METH);
 			valErrorsList.add(valError);
 			logError("Preferred Contact Method " + prefContMeth + " is invalid");
 		}
 	}
 	
 	/**
	 * This method validates the prefContTime of any registrant and populate the error list if prefContTime is invalid
	 * @param prefContTime
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validatePrefContactTime(String prefContTime, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		if (!(prefContTime.equals("M") || prefContTime.equals("A") || prefContTime.equals("E"))){
 			ValidationError valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_REG_PREF_CONT_TIME);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_INVALID_REG_PREF_CONT_TIME);
 			valErrorsList.add(valError);
 			logError("Preferred Contact Time " + prefContTime + " is invalid");
 		}
 	}
 	
 	/**
	 * This method validates the affiliateOptIn of any registrant and populate the error list if phone number is invalid
	 * @param affiliateOptIn
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateAffiliateOptIn(String affiliateOptIn, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		if (!(affiliateOptIn.equals(BBBCoreConstants.YES_CHAR) || affiliateOptIn.equals(BBBCoreConstants.NO_CHAR))){
 			ValidationError valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_REG_AFFLT_OPT_IN);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_INVALID_REG_AFFLT_OPT_IN);
 			valErrorsList.add(valError);
 			logError("Affiliate Opt In " + affiliateOptIn + " is invalid");
 		}
 	}
 	
 	/**
	 * This method validates the emailId of any registrant and populate the error list if phone number is invalid
	 * @param emailId
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateEmail(String emailId, List<ValidationError> valErrorsList, boolean isPrimaryRegistrant, String prefix, String addrDescription) {
 		
 		ValidationError valError = null;
 		if (this.hasInvalidChars(emailId)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_EMAIL);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_CHARS_MSSG);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_CHARS_MSSG);
 		} else if (isPrimaryRegistrant && BBBUtility.isEmpty(emailId)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_EMAIL);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_EMAIL_EMPTY_MSSG);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_EMAIL_EMPTY_MSSG);
 		} else if (!this.isValidEmail(emailId)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_EMAIL);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_MSSG);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_MSSG);
 		}
 	}
 	
 	/**
	 * This method validates the emailOptinFlag of any registrant and populate the error list if phone number is invalid
	 * @param emailOptinFlag
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateEmailOptinFlag(String emailOptinFlag, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		ValidationError valError = null;
 		if (!(BBBCoreConstants.YES_CHAR.equals(emailOptinFlag) || BBBCoreConstants.NO_CHAR.equals(emailOptinFlag))) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_COREG_EMAIL);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_INVALID_COREG_EMAIL);
 			valErrorsList.add(valError);
 			logError("Email Optin Flag :: " + emailOptinFlag + "is invalid");
 		}
 	}
 	
 	/**
	 * This method validates the zipCode of any registrant and populate the error list if phone number is invalid
	 * @param zipCode
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateZipCode(String zipCode, List<ValidationError> valErrorsList, String prefix, String addrDescription, String siteFlag) {
 		
 		ValidationError valError = null;
 		
 		if (this.hasInvalidChars(zipCode)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_ZIP);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ZIP_INVALID_CHARS);
 			valErrorsList.add(valError);
 			logError("Zip Code :: " + zipCode + " has invalid characteres");
 		} else if (!this.isValidZip(zipCode, siteFlag)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_ZIP);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ZIP_INVALID);
 			valErrorsList.add(valError);
 			logError("Zip Code :: " + zipCode + " is invalid zip code");
 		}
 	}
 	
 	/**
	 * This method validates the state of any registrant and populate the error list if phone number is invalid
	 * @param state
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateState(String state, List<ValidationError> valErrorsList, String prefix, String addrDescription, String siteFlag) {
 		
 		ValidationError valError = null;
 		
 		if (this.hasInvalidChars(state)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_STATE);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_STATE_INVALID_CHARS);
 			valErrorsList.add(valError);
 			logError("State :: " + state + " has invalid characteres");
 		} else if (!isValidState(state, siteFlag)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_STATE);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_STATE_INVALID);
 			valErrorsList.add(valError);
 			logError("State :: " + state + " is invalid state");
 		}
 	}
 	
 	/**
	 * This method validates the city of any registrant and populate the error list if phone number is invalid
	 * @param city
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateCity(String city, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		ValidationError valError = null;
 		
 		if (this.hasInvalidChars(city)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_CITY);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_CITY_INVALID_CHARS);
 			valErrorsList.add(valError);
 			logError("City :: " + city + " has invalid characteres");
 		} else if (city.length() > 35) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_CITY);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_CITY_TOO_LONG);
 			valErrorsList.add(valError);
 			logError("City :: " + city + " is too long");
 		}
 	}
 	
 	/**
	 * This method validates the address2 of any registrant and populate the error list if phone number is invalid
	 * @param address
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateAddressTwo(String address, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		ValidationError valError = null;
 		
 		if (this.hasInvalidChars(address)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_ADDR2);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ADDR2_INVALID_CHARS);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ADDR2_INVALID_CHARS);
 		} else if (address.length() > 50) {
 			valError = new ValidationError();
 			valError.setKey(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ADDR2_TOO_LONG);
 			valError.setValue(prefix + BBBGiftRegistryConstants.ERROR_KEY_ADDR2);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ADDR2_TOO_LONG);
 		}
 	}
 	
 	/**
	 * This method validates the address1 of any registrant and populate the error list if phone number is invalid
	 * @param address
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateAddressOne(String address, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		ValidationError valError = null;
 		
 		if (this.hasInvalidChars(address)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_ADDR1);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ADDR1_INVALID_CHARS);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ADDR1_INVALID_CHARS);
 		} else if (address.length() > 50) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_ADDR1);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ADDR1_TOO_LONG);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_ADDR1_TOO_LONG);
 		}
 	}
 	
 	/**
	 * This method validates the Company of any registrant and populate the error list if phone number is invalid
	 * @param companyName
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateCompany(String companyName, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		ValidationError valError = null;
 		
 		if (this.hasInvalidChars(companyName)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_COMPANY);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_COMPANY_INVALID_CHARS);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_COMPANY_INVALID_CHARS);
 		} else if (companyName.length() > 30) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_COMPANY);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_COMPANY_TOO_LONG);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_COMPANY_TOO_LONG);
 		}
 	}
 	
 	/**
	 * This method validates the Mobile number of any registrant and populate the error list if phone number is invalid
	 * @param mobileNumber
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateMobileNumber(String mobileNumber, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		ValidationError valError = null;
 		
 		if (this.hasInvalidChars(mobileNumber)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_PHONE2);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_PHONE2_INVALID_CHARS);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_PHONE2_INVALID_CHARS);
 		} else if (!this.isValidPhone(mobileNumber)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_PHONE2);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_PHONE2_INVALID);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_PHONE2_INVALID);
 		}
 	}
 	
 	/**
	 * This method validates the phone number of any registrant and populate the error list if phone number is invalid
	 * @param phoneNumber
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validatePhoneNumber(String phoneNumber, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		ValidationError valError = null;
 		
 		if (this.hasInvalidChars(phoneNumber)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_PHONE1);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_PHONE1_INVALID_CHARS);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_PHONE1_INVALID_CHARS);
 		} else if (!this.isValidPhone(phoneNumber)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_PHONE1);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_PHONE1_INVALID);
 			valErrorsList.add(valError);
 			logError(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_PHONE1_INVALID);
 		}
 	}
 	
 	/**
	 * This method validates the last name of any registrant and populate the error list if name is invalid
	 * @param lastName
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateLastName(String lastName, List<ValidationError> valErrorsList, String prefix, String addrDescription ) {
 		
 		ValidationError valError = null;
 		if (this.hasInvalidChars(lastName)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_LAST_NAME);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_LST_NM_INVALID_CHARS);
 			valErrorsList.add(valError);
 		} else if (BBBUtility.isEmpty(lastName)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_LAST_NAME);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_LST_NM_EMPTY_MSSG);
 			valErrorsList.add(valError);
 			logError("Last Name is Empty");
 		} else if (lastName.length() > 50) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_LAST_NAME);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_LST_NM_LONG_MSSG);
 			valErrorsList.add(valError);
 			logError("Last Name is too long");
 		}
 	}
 	
 	/**
	 * This method validates the first name of any registrant and populate the error list if name is invalid
	 * @param firstName
	 * @param valErrorsList
	 * @param prefix
	 * @param addrDescription
	 * 
	 */
 	
 	public void validateFirstName(String firstName, List<ValidationError> valErrorsList, String prefix, String addrDescription) {
 		
 		ValidationError valError = null;
 		
 		if (this.hasInvalidChars(firstName)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_FIRST_NAME);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_FST_NM_INVALID_CHARS);
 			valErrorsList.add(valError);
 		} else if (BBBUtility.isEmpty(firstName)) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_FIRST_NAME);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_FST_NM_EMPTY_MSSG);
 			valErrorsList.add(valError);
 			logError("First Name is Empty");
 		} else if (firstName.length() > 50) {
 			valError = new ValidationError();
 			valError.setKey(prefix + BBBGiftRegistryConstants.ERROR_KEY_FIRST_NAME);
 			valError.setValue(addrDescription + BBBCoreConstants.SPACE + BBBGiftRegistryConstants.ERROR_FST_NM_LONG_MSSG);
 			valErrorsList.add(valError);
 			logError("First Name is too long");
 		}
 	}
 	
 	/**
	 * This method returns the state code corresponding to site code passed into input
	 * @param siteFlag
	 * @return String
	 */
 	
	public String getCountry(String siteFlag) {
		
		String countryCode = BBBCoreConstants.BLANK;
		switch (siteFlag) {
		case "1":
			countryCode = BBBGiftRegistryConstants.US;
			break;
		case "2":
			countryCode = BBBGiftRegistryConstants.US;
			break;
		case "3":
			countryCode = BBBGiftRegistryConstants.CA;
			break;

		default:
			break;
		}
		return countryCode;
	}
	
	/**
	 * This method populates checks whether co-registrant is selected or not while  creating a registry
	 * On basis of return type of this method RegNamesVO for co-registrant will be populated 
	 * @param registryVO
	 * @return boolean
	 */
	
	public boolean isCoRegistrantEmpty(RegistryVO registryVO) {
		
		if (registryVO.getCoRegistrant() == null) {
			return true;
		} else if (registryVO.getCoRegistrant().getContactAddress() == null) {
			return true;
		}
		RegistrantVO coRegistrantVO = registryVO.getCoRegistrant();
		AddressVO coRegAddressVO = coRegistrantVO.getContactAddress();
			return (BBBUtility.isEmpty(coRegistrantVO.getFirstName()) && BBBUtility.isEmpty(coRegistrantVO.getLastName()) && BBBUtility.isEmpty(coRegistrantVO.getPrimaryPhone()) 
					&& BBBUtility.isEmpty(coRegistrantVO.getCellPhone()) && BBBUtility.isEmpty(coRegAddressVO.getCompany()) && BBBUtility.isEmpty(coRegAddressVO.getAddressLine1()) 
					&& BBBUtility.isEmpty(coRegAddressVO.getAddressLine2()) && BBBUtility.isEmpty(coRegAddressVO.getCity()) && BBBUtility.isEmpty(coRegAddressVO.getState())
					&& BBBUtility.isEmpty(coRegAddressVO.getZip()) && BBBUtility.isEmpty(coRegistrantVO.getEmail()) && BBBUtility.isEmpty(coRegistrantVO.getBabyMaidenName()) 
					&& BBBUtility.isEmpty(coRegistrantVO.getProfileId()));
	}
	
	/**
	 * This method populates checks whether future shipping is selected or not while  creating a registry
	 * On basis of return type of this method RegNamesVO for future shipping will be populated 
	 * @param ShippingVO
	 * @return boolean
	 */
	
	public boolean isFutureShippingEmpty(ShippingVO shippingVO) {
		AddressVO addressVO = shippingVO.getFutureshippingAddress();
		return (BBBUtility.isEmpty(addressVO.getFirstName()) && BBBUtility.isEmpty(addressVO.getLastName())
			&& BBBUtility.isEmpty(addressVO.getPrimaryPhone()) && BBBUtility.isEmpty(addressVO.getCompany())
				&& BBBUtility.isEmpty(addressVO.getAddressLine1()) && BBBUtility.isEmpty(addressVO.getAddressLine2())
					&& BBBUtility.isEmpty(addressVO.getCity()) && BBBUtility.isEmpty(addressVO.getState())
						&& BBBUtility.isEmpty(addressVO.getZip()) && BBBUtility.isEmpty(shippingVO.getFutureShippingDateWS()));
	}
	
	/**
	 * This method populates all the relevant parameters from registryVO into String [] 
	 * @param RegistryVO
	 * @return Object[]
	 */

	public Object[] populateInputToLogErrorOrValidate(RegistryVO registryVO) {
			
		if (isLoggingDebug()) {
			this.logDebug("BBBGiftRegistryUtils.populateInputToLogErrorOrValidate() method starts");
		}
		
		EventVO eventVO = registryVO.getEvent();
		RegistrantVO primaryRegistrant = registryVO.getPrimaryRegistrant();
		AddressVO regAddress = primaryRegistrant.getContactAddress();
		RegistrantVO coRegistrant = registryVO.getCoRegistrant();
		AddressVO coRegAddress = coRegistrant.getContactAddress();
		AddressVO shippingAddress = registryVO.getShipping().getShippingAddress();
		AddressVO futureShippingAddress = registryVO.getShipping().getFutureshippingAddress();
		String [] args = {
				registryVO.getSiteId(), eventVO.getEventDateWS(), registryVO.getSignup(), registryVO.getWord(), registryVO.getHint(),	primaryRegistrant.getLastName(),
				primaryRegistrant.getFirstName(), regAddress.getCompany(),	regAddress.getAddressLine1(), regAddress.getAddressLine2(),	regAddress.getCity(), regAddress.getState(),
				regAddress.getZip(), primaryRegistrant.getPrimaryPhone(), primaryRegistrant.getCellPhone(), primaryRegistrant.getEmail(), coRegistrant.getLastName(), coRegistrant.getFirstName(),
				coRegAddress.getCompany(), coRegAddress.getAddressLine1(), coRegAddress.getAddressLine2(), coRegAddress.getCity(), regAddress.getState(), coRegAddress.getZip(),
				coRegistrant.getPrimaryPhone(), coRegistrant.getCellPhone(), coRegistrant.getEmail(), shippingAddress.getLastName(), shippingAddress.getFirstName(), shippingAddress.getCompany(),
				shippingAddress.getAddressLine1(), shippingAddress.getAddressLine2(), shippingAddress.getCity(), shippingAddress.getState(), shippingAddress.getZip(), shippingAddress.getPrimaryPhone(),
				futureShippingAddress.getLastName(), futureShippingAddress.getFirstName(), futureShippingAddress.getCompany(), futureShippingAddress.getAddressLine1(), futureShippingAddress.getAddressLine2(),
				futureShippingAddress.getCity(), futureShippingAddress.getState(), futureShippingAddress.getZip(), futureShippingAddress.getPrimaryPhone(), registryVO.getShipping().getFutureShippingDateWS(), eventVO.getBabyGender(),
				eventVO.getShowerDateWS(), eventVO.getBirthDate(), registryVO.getNetworkAffiliation(), registryVO.getAffiliateOptIn(), Integer.toString(registryVO.getPrefRegContMeth()),
				registryVO.getPrefRegContTime(), Integer.toString(registryVO.getPrefCoregContMeth()), registryVO.getPrefCoregContTime(), eventVO.getGuestCount(), coRegistrant.getCoRegEmailFlag(),
				registryVO.getRegistryType().getRegistryTypeName(), eventVO.getBabyName(), primaryRegistrant.getBabyMaidenName(), coRegistrant.getBabyMaidenName(),
				registryVO.getPrefStoreNum(), registryVO.getRefStoreContactMethod(), primaryRegistrant.getProfileId(), coRegistrant.getProfileId(), registryVO.getIsPublic()
				};
		
		if (isLoggingDebug()) {
			this.logDebug("BBBGiftRegistryUtils.populateInputToLogErrorOrValidate() method ends");
		}
		return args;
	}
	
 	/**This method removes duplicates from list
 	 * @param lRegistrySummaryVOs
 	 * @return
 	 */
 	public List<RegistrySummaryVO> removeDuplicateRows(List<RegistrySummaryVO> registrySummaryVOs) {		

 		List<RegistrySummaryVO> filteredRegSummryVOs = new ArrayList<RegistrySummaryVO>();
 		Set<String> uniqueRegistryIds = new HashSet<String>();
 		if (!BBBUtility.isListEmpty(registrySummaryVOs)) {
	 		for( RegistrySummaryVO summaryVO : registrySummaryVOs ) {
	 			if(uniqueRegistryIds.add( summaryVO.getRegistryId())) {
	 				filteredRegSummryVOs.add(summaryVO);
	 			}
	 		}
 		}
 		return filteredRegSummryVOs;

 	}
 	
 	
 	/**This method sets error response in the type of object passed as input param
 	 * @param objectType
 	 * @param errorMessage
 	 * @param displayMessage
 	 * @param errorExists
 	 * @param errorId
 	 * @param validationErrors
 	 * @return ErrorStatus/ServiceErrorVO
 	 */
 	public Object setErrorInResponse(String objectType ,String errorMessage , String displayMessage , boolean errorExists , int errorId , List validationErrors)		
 	{		
 		Object error = null;		
 		if(BBBGiftRegistryConstants.ERROR_STATUS.equals(objectType))		
 		{		
 			ErrorStatus errorStatus = new ErrorStatus();		
 			errorStatus.setErrorExists(true);		
 			errorStatus.setErrorMessage(errorMessage);		
 			errorStatus.setDisplayMessage(displayMessage);		
 			errorStatus.setErrorId(errorId);		
 			errorStatus.setValidationErrors(validationErrors);		
 			error = errorStatus;		
 		}		
 		else if(BBBGiftRegistryConstants.SERVICE_ERROR_VO.equals(objectType))		
 		{		
 			ServiceErrorVO serviceErrorVO = new ServiceErrorVO();		
 			serviceErrorVO.setErrorExists(errorExists);		
 			serviceErrorVO.setErrorId(errorId);		
 			serviceErrorVO.setErrorDisplayMessage(displayMessage);		
 			serviceErrorVO.setErrorMessage(errorMessage);		
 			error =  serviceErrorVO;		
 		}		
 		return error;		
 	}
 	
 	/**This method encrypts the value passed.
 	 * @param nValue
 	 * @return
 	 */
 	public  String encryptRegNumForPersonalWebsite(long nValue)
	{
 		return String.valueOf(encryptVal(nValue) * -10);
	}
 	
 	public  long encryptVal(long nValue)
	{
		return (((nValue & 31) << 26) + ((nValue >> 5) & 0x3FFFFFF)) ^ 0xBEB0BA;
	}
 	
	/**This method returns requested number of records
	 * @param lRegistrySummaryVOs
	 * @param from
	 * @param blockSize
	 * @return List<RegistrySummaryVO>
	 */
	public List<RegistrySummaryVO> getPagedData(
			List<RegistrySummaryVO> lRegistrySummaryVOs, int from, int blockSize) {
		List<RegistrySummaryVO> lPagedRegistrySummaryVOs = null;
		if (from < 0)
			from = 0;

		if (blockSize < 0)
			blockSize = 0;

		if (from == 0 && blockSize == 0) {
			lPagedRegistrySummaryVOs = lRegistrySummaryVOs;
		} else {
			lPagedRegistrySummaryVOs = new ArrayList<RegistrySummaryVO>();
			int count = 0;
			int upperLimit = 0;

			if ((from + blockSize) > lRegistrySummaryVOs.size()) {
				upperLimit = lRegistrySummaryVOs.size();
			} else {
				upperLimit = from + blockSize;
			}

			Iterator<RegistrySummaryVO> itRegistrySummVO = lRegistrySummaryVOs
					.iterator();
			while (itRegistrySummVO.hasNext()) {
				RegistrySummaryVO registrySummaryVO = itRegistrySummVO.next();
				if (count >= from && count < upperLimit) {
					lPagedRegistrySummaryVOs.add(registrySummaryVO);
				}
				count++;
				if (count >= upperLimit) {
					break;
				}
			}
		}
		return lPagedRegistrySummaryVOs;
	}
	
	/**
	 * This method returns the seo url for c1/c2/c3 category for checklist functionality
	 * BBBJ-1220
	 * 
	 * @param pRequest
	 * @param pSearchQuery
	 * @return seoUrl
	 */
	public String populateChecklistSEOUrl(DynamoHttpServletRequest pRequest,SearchQuery pSearchQuery){
	BBBPerformanceMonitor.start(CLASS_NAME + "_populateChecklistSEOUrl");
	String cat1Name=null;
	String cat2Name=null;
	String cat3Name=null;
	String categoryId=null;
	String checklistId=null;
	String checklistDisplayName=null;
		if(pRequest!=null && pRequest.getHeader(BBBCoreConstants.CHANNEL)!=null){
			String channel=pRequest.getHeader(BBBCoreConstants.CHANNEL);
			if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel) || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel)){
				cat1Name=pSearchQuery.getC1name();
				cat2Name=pSearchQuery.getC2name();
				cat3Name=pSearchQuery.getC3name();
				categoryId=pSearchQuery.getChecklistCategoryId();
				checklistId=pSearchQuery.getChecklistId();
				checklistDisplayName=pSearchQuery.getChecklistName();
			}
		}
		else{
		cat1Name=pRequest.getParameter(BBBCoreConstants.CAT1_NAME);
		cat2Name=pRequest.getParameter(BBBCoreConstants.CAT2_NAME);
		cat3Name=pRequest.getParameter(BBBCoreConstants.CAT3_NAME);
		categoryId=pRequest.getParameter(BBBCoreConstants.CHECKLIST_CATEGORY_ID);
		checklistId=pRequest.getParameter(BBBCoreConstants.CHECKLIST_ID);
		checklistDisplayName=pRequest.getParameter(BBBCoreConstants.CHECKLIST_DISPLAY_NAME);
		}
		
		logDebug("GiftRegistryTools populateChecklistSEOUrl cat1Name:[" + cat1Name + ",cat2Name:[" + cat2Name
				+ "],cat3Name:[" + cat3Name + "],categoryId:[" + categoryId + "],checklistId:[" + checklistId
				+ ",checklistDisplayName:[" + checklistDisplayName + "]");
		String seoURL =BBBCoreConstants.BLANK;
		if(BBBUtility.isNotEmpty(checklistDisplayName) && BBBUtility.isNotEmpty(cat1Name) 
				&& BBBUtility.isNotEmpty(checklistId) && BBBUtility.isNotEmpty(categoryId)){
			seoURL = seoURL.concat(BBBCoreConstants.SLASH + BBBCoreConstants.CHECKLIST.toLowerCase() + BBBCoreConstants.SLASH + checklistDisplayName + BBBCoreConstants.SLASH + cat1Name + BBBCoreConstants.SLASH);
			if(BBBUtility.isNotEmpty(cat2Name)){
				seoURL = seoURL.concat(cat2Name+BBBCoreConstants.SLASH);
			}
			if(BBBUtility.isNotEmpty(cat3Name)){
				seoURL = seoURL.concat(cat3Name+BBBCoreConstants.SLASH);
			}
			seoURL = seoURL.concat(categoryId+BBBCoreConstants.SLASH+checklistId+BBBCoreConstants.SLASH);
		}
		
		logDebug("SEO url formed :" + seoURL);
		BBBPerformanceMonitor.end(CLASS_NAME + "_populateChecklistSEOUrl");
		return seoURL;		
	}

}