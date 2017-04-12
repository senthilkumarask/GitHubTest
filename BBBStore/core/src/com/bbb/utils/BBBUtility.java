package com.bbb.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.codehaus.jackson.map.ObjectMapper;

import com.bbb.account.vo.order.OrderDetailInfoReturn;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.validation.BBBValidationRules;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO;
import com.bbb.search.bean.result.BBBDynamicPriceVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import atg.core.util.StringUtils;
import atg.droplet.TagConversionException;
import atg.droplet.TagConverterManager;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;


/**
 * The Class BBBUtility.
 *
 * @author ugoel
 */
public final class BBBUtility extends StringUtils {

	//private static final String RIGHT_PARENTHESIS = ")";
	//private static final String LEFT_PARENTHESIS = "(";
	/** The Constant PERIOD. */
	private static final String PERIOD = ".";
	//private static final String COMMA = ",";
	/** The Constant HYPHEN. */
	private static final String HYPHEN = "-";
	
	/** The Constant TWENTY_FIVE. */
	private static final int TWENTY_FIVE = 25;
    
    /** The Constant THIRTY. */
    private static final int THIRTY = 30;
	
	/** The Constant SPACE. */
	private static final String SPACE = " ";
	
	/** The Constant ONE. */
	private static final int ONE = 1;
	
	/** The Constant ZERO. */
	private static final int ZERO = 0;
	
	/** The Constant BLANK_STRING. */
	private static final String BLANK_STRING = "";
	
	/** The Constant MD5_CHARS. */
	private static final String MD5_CHARS = "[a-fA-F0-9]{32}";
	
	/** The Constant ROUND. */
	private static final int ROUND = 100;
    
    /** The Constant MAX_DATE_LENGTH. */
    private static final int MAX_DATE_LENGTH = 7;
    
    /** The Constant MAX_REGISTRY_LENGTH. */
    private static final int MAX_REGISTRY_LENGTH = 20;
    
    /** The Constant CARD_DIGITS. */
    private static final int CARD_DIGITS = 4;

    /** The Constant SLASH_APOSTROPHE. */
    private static final String SLASH_APOSTROPHE = "\'";
    
    /** The Constant SLASH_QUOTE. */
    private static final String SLASH_QUOTE = "\"";
    
    /** The Constant DOUBLE_AMPERSAND. */
    private static final String DOUBLE_AMPERSAND = "&&";
    
    /** The Constant CROSS. */
    private static final String CROSS = "X";
    
    /** The Constant NUMBER_RANGE. */
    private static final String NUMBER_RANGE = "[0-9]";
    
    /** The Constant GREATER_THAN_SYMBOL. */
    private static final String GREATER_THAN_SYMBOL = ">";
    
    /** The Constant LESS_THAN_SYMBOL. */
    private static final String LESS_THAN_SYMBOL = "<";
    
    /** The Constant GMT. */
    private static final String GMT = "GMT";
    
    /** The Constant SYSTEM_ERROR_INFO. */
    private static final String SYSTEM_ERROR_INFO = "/com/bbb/utils/SystemErrorInfo";
    
    /** The Constant BACKSLASH. */
    private static final String BACKSLASH = "&#092;";
    
    /** The Constant QUOTE. */
    private static final String QUOTE = "&quot;";
    
    /** The Constant APOSTROPHE. */
    private static final String APOSTROPHE = "&apos;";
    
    /** The Constant AMPERSAND. */
    private static final String AMPERSAND = "&amp;";
    
    /** The Constant GREATER_THAN. */
    private static final String GREATER_THAN = "&gt;";
    
    /** The Constant LESS_THAN. */
    private static final String LESS_THAN = "&lt;";
    
    /** The Constant EXPIRY_DATE_FORMAT. */
    private static final String EXPIRY_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";

    /** The Constant MAX_GIFT_MESSAGE_LENGTH. */
    private static final int MAX_GIFT_MESSAGE_LENGTH = 200;

    /** The Constant COUPON_CODE_LENGTH_8. */
    private static final int COUPON_CODE_LENGTH_8 = 8;
    
    /** The Constant COUPON_CODE_LENGTH_12. */
    private static final int COUPON_CODE_LENGTH_12 = 12;
    
    /** The Constant COUPON_CODE_LENGTH_20. */
    private static final int COUPON_CODE_LENGTH_20 = 20;
    
    /** The Constant COUPON_CODE_LENGTH_30. */
    private static final int COUPON_CODE_LENGTH_30 = 30;
   
    /** The Constant LOG. */
    private static final ApplicationLogging LOG = ClassLoggingFactory.getFactory().getLoggerForClass(BBBUtility.class);
    
    /** The Constant RULES. */
    private static  BBBValidationRules rules;
    public static BBBValidationRules getRules() {
    	if(rules == null) {
    		 rules = (BBBValidationRules) Nucleus.getGlobalNucleus().resolveName(
            BBBCoreConstants.BBB_VALIDATION_RULES_PROPERTY);
    	}
		return rules;
	}
    public static void setRules(BBBValidationRules pRules) {
    	rules = pRules;
	}
    
    /** The Constant LESS_THAN_PATTERN. */
    private static final Pattern LESS_THAN_PATTERN = Pattern.compile(LESS_THAN, Pattern.CASE_INSENSITIVE);
    
    /** The Constant GREATER_THAN_PATTERN. */
    private static final Pattern GREATER_THAN_PATTERN = Pattern.compile(GREATER_THAN, Pattern.CASE_INSENSITIVE);
    
    /** The Constant AMPERSAND_PATTERN. */
    private static final Pattern AMPERSAND_PATTERN = Pattern.compile(AMPERSAND, Pattern.CASE_INSENSITIVE);
    
    /** The Constant APOSTROPHE_PATTERN. */
    private static final Pattern APOSTROPHE_PATTERN = Pattern.compile(APOSTROPHE, Pattern.CASE_INSENSITIVE);
    
    /** The Constant QUOTE_PATTERN. */
    private static final Pattern QUOTE_PATTERN = Pattern.compile(QUOTE, Pattern.CASE_INSENSITIVE);
    
    /** The Constant BACKSLASH_PATTERN. */
    private static final Pattern BACKSLASH_PATTERN = Pattern.compile(BACKSLASH, Pattern.CASE_INSENSITIVE);
    
    /** The Constant SMTP_HOST. */
    private static final String SMTP_HOST = "mail.smtp.host";
    
    /** The Constant CATALOG_TOOLS. */
    private static BBBCatalogTools catalogTools;

    /**
     *  The pattern.
     *
     * @param value the value
     * @return the double
     */



	/**
	 * Sets the pattern.
	 * 
	 * @param pattern
	 *            the pattern to set
	 */
	

    /**
     * @param value Double Value
     * @return Value rounded down to two decimal places
     */
    public static double round(final double value) {
        return (double) Math.round(value * ROUND) / ROUND;
    }

    /**
     * Round.
     *
     * @param value Double Value
     * @return Value rounded down to two decimal places
     */
    public static double round(final Double value) {
        return (double) Math.round(value.doubleValue() * ROUND) / ROUND;
    }

    /**
     * Description: Added for the fix of BBBSL-1876 :Special characters in Gift
     * message.
     *
     * @param pGiftMessage
     *            - Gift message which is to be checked for validation.
     * @return boolean flag true/ false based on validation passed or fails.
     */

    public static boolean isGiftMessageValid(final String pGiftMessage) {

        boolean validateStatus = false;
        String giftString = pGiftMessage;

        if (giftString != null) {
            giftString = giftString.replaceAll("\\r", BLANK_STRING);
          //Changes for BSL-1658 Start
            int length=0;
			
            try {
				length = giftString.getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				LOG.logError("BBBUtility.isGiftMessageValid: Error occurred for Gift Message Text");
			}
			if ((length >= BBBCoreConstants.ZERO) && (length <= MAX_GIFT_MESSAGE_LENGTH)) {
                if (BBBUtility.isGiftStringPatternValid(getRules().getGiftMessagePattern(), giftString)) {
                    validateStatus = true;
                }
            }
			//Changes for BSL-1658 End
        }
        return validateStatus;
    }

    /**
     * Checks if is gift string pattern valid.
     *
     * @param pPattern the pattern
     * @param pValue the value
     * @return true, if is gift string pattern valid
     */
    private static boolean isGiftStringPatternValid(final String pPattern, final String pValue) {
        if (isEmpty(pPattern)) {
            return false;
        }

        final Pattern pattern = Pattern.compile(pPattern);
        final Matcher match = pattern.matcher(pValue);
        return match.matches();
    }

    /**
     * Checks if is alpha numeric.
     *
     * @param pValue            Value
     * @return true if string pattern is alpha numeric. false otherwise.
     */
    public static boolean isAlphaNumeric(String pValue) {
        if (isEmpty(pValue)) {
            return false;
        }
        return isStringPatternValid(getRules().getAlphaNumericPattern(), pValue);
    }
	
    /**
     * Checks if is string pattern valid.
     *
     * @param pPattern            Pattern
     * @param pValue            Value
     * @return true if string pattern is valid. false otherwise.
     */
    public static boolean isStringPatternValid(final String pPattern, final String pValue) {

        if (isEmpty(pPattern) || isEmpty(pValue)) {
            return false;
        }

        final Pattern pattern = Pattern.compile(pPattern);
        final Matcher match = pattern.matcher(pValue);
        return match.matches();
    }

    /**
     * Mask credi card number.
     *
     * @param creditCardNumber            Credit Card Number
     * @param expirationYear
     * @return
     */
    public static String stripExpirationYear(final String expirationYear) {
        String strippedExpYear = expirationYear;
        if (expirationYear != null && (expirationYear.length() == 4)) {
        	strippedExpYear = expirationYear.substring(2);
        }
        return strippedExpYear;
    }

    /**
     * @param expirationMonth
     * @return
     */
    public static String stripExpirationMonth(final String expirationMonth) {
        String strippedExpMonth = expirationMonth;
        if (expirationMonth != null && (expirationMonth.length() == 2)) {
        	strippedExpMonth = expirationMonth.replaceFirst("^0+(?!$)", "");
        }
        return strippedExpMonth;
    }

    /**
     * @param creditCardNumber
     *            Credit Card Number
     * @return last four digits of Credit Card
     */
    public static String maskCrediCardNumber(final String creditCardNumber) {
        String finalDigit = BLANK_STRING;
        if ((creditCardNumber != null) && (creditCardNumber.length() > CARD_DIGITS)) {
            final String lastDigit = creditCardNumber.substring(creditCardNumber.length() - CARD_DIGITS);
            String firstDigit = creditCardNumber.substring(0, creditCardNumber.length() - lastDigit.length());
            firstDigit = firstDigit.replaceAll(NUMBER_RANGE, CROSS);
            finalDigit = firstDigit + lastDigit;
        }
        return finalDigit;
    }

    /**
     * Mask all digits.
     *
     * @param number the number
     * @return masked digits number.
     */
    public static String maskAllDigits(final String number) {

        String maskedNumber = number;
        if (!StringUtils.isEmpty(number)) {
            maskedNumber = number.replaceAll(NUMBER_RANGE, CROSS);
        }
        return maskedNumber;
    }

    /**
     * Checks if is string length valid.
     *
     * @param pStringValue the string value
     * @param pMinLength the min length
     * @param pMaxLength the max length
     * @return boolean flag true/ false based on valid string
     */
    public static boolean isStringLengthValid(final String pStringValue, final int pMinLength, final int pMaxLength) {

        if (isEmpty(pStringValue)) {
            return false;
        }

        return (pStringValue.length() >= pMinLength) && (pStringValue.length() <= pMaxLength);
    }

    /**
     * Check password contains first name last name.
     *
     * @param pFirstName the first name
     * @param pLastName the last name
     * @param pPassword the password
     * @return boolean flag true/ false if string matches.
     */
    public static boolean checkPasswordContainsFirstNameLastName(final String pFirstName, final String pLastName,
            final String pPassword) {
        boolean isStringMatch = false;

        if ((pPassword.toLowerCase(Locale.US).indexOf(pFirstName.toLowerCase(Locale.US)) != -1)
                || (pPassword.toLowerCase(Locale.US).indexOf(pLastName.toLowerCase(Locale.US)) != -1)) {
            isStringMatch = true;
        }
        return isStringMatch;
    }

    /**
     * Checks if is same email.
     *
     * @param pEmail1 the email1
     * @param pEmail2 the email2
     * @return boolean flag true/ false if email is same
     */
    public static boolean isSameEmail(final String pEmail1, final String pEmail2) {

        if (isEmpty(pEmail1) && isEmpty(pEmail2)) {
            return false;
        }

        return pEmail1.equals(pEmail2);
    }

    /**
     * This method is validate the Email Address.
     *
     * @param pEmailString            return true if phone is valid according business rules
     *            otherwise return false
     * @return boolean flag true/ false if email string is valid.
     */

    public static boolean isValidEmail(final String pEmailString) {

    	//R2.2.1 change - Changed total length to 130 and 125 before @
        if (!isEmpty(pEmailString) && isStringLengthValid(pEmailString, BBBCoreConstants.SIX, BBBCoreConstants.ONE_THIRTY)) {

            final String[] emailParts = StringUtils.splitStringAtCharacter(pEmailString, BBBCoreConstants.AT_THE_RATE);

            // Local part can have max 64 chars
            if (isCrossSiteScripting(pEmailString)) {
                return false;
            }

            if (isStringLengthValid(emailParts[BBBCoreConstants.ZERO], BBBCoreConstants.ONE, BBBCoreConstants.ONE_TWO_FIVE)) {

                return isStringPatternValid(getRules().getEmailPattern(), pEmailString);
            }
        }
        return false;
    }

    /**
     * This method is used to validate phone number for desired phone pattern.
     *
     * @param pPhoneNumber the phone number
     * @return true if phone number is match with according defined phone
     *         pattern false - otherwise
     */

    public static boolean isValidPhoneNumber(final String pPhoneNumber) {
        if (isCrossSiteScripting(pPhoneNumber)) {
            return false;
        }

        if (!isEmpty(pPhoneNumber) && isStringLengthValid(pPhoneNumber, BBBCoreConstants.TEN, BBBCoreConstants.FIFTEEN)) {

            return isStringPatternValid(getRules().getPhonePattern(), pPhoneNumber);
        }
        return false;
    }

    /**
     * Checks if is valid international phone number.
     *
     * @param pPhoneNumber the phone number
     * @return true if phone number is valid International phone number. False
     *         otherwise.
     */
    public static boolean isValidInternationalPhoneNumber(final String pPhoneNumber) {
        if (isCrossSiteScripting(pPhoneNumber)) {
            return false;
        }
        if (!isEmpty(pPhoneNumber) && isStringLengthValid(pPhoneNumber, BBBCoreConstants.ONE, BBBCoreConstants.TWENTY)) {

            return isStringPatternValid(getRules().getNumericOnlyPattern(), pPhoneNumber);
        }
        return false;
    }

    /**
     * This method is used to validate phone Ext for desired phone pattern.
     *
     * @param pPhoneExt the phone ext
     * @return true if phone Ext is match with according defined phone Ext
     *         pattern false - otherwise
     */

    public static boolean isValidPhoneExt(final String pPhoneExt) {
        if (isCrossSiteScripting(pPhoneExt)) {
            return false;
        }
        if (!isEmpty(pPhoneExt) && isStringLengthValid(pPhoneExt, BBBCoreConstants.ONE, BBBCoreConstants.SEVEN)) {

            return isStringPatternValid(getRules().getPhoneExtPattern(), pPhoneExt);
        }
        return false;
    }

    /**
     * This method is used to validate Order number for desired order number
     * pattern.
     *
     * @param OrderNumber the order number
     * @return true if order number is match with according defined Order Number
     *         pattern false - otherwise
     */

    public static boolean isValidOrderNumber(final String OrderNumber) {
        if (isCrossSiteScripting(OrderNumber)) {
            return false;
        }
        if (!isEmpty(OrderNumber) && isStringLengthValid(OrderNumber, BBBCoreConstants.ONE, BBBCoreConstants.NINETEEN)) {

            return isStringPatternValid(getRules().getOrderPattern(), OrderNumber);
        }
        return false;
    }

    /**
     * This method is used to validate Address line1 for desigred pattern.
     *
     * @param pAddressLine1 the address line1
     * @param pAddressLine2 the address line2
     * @return true if address is non PO box address. false otherwise.
     */
    public static boolean isNonPOBoxAddress(final String pAddressLine1, final String pAddressLine2) {
        boolean nonPOBoxAddress = true;

        if (isEmpty(pAddressLine1)) {
            return false;
        }

        if (isStringLengthValid(pAddressLine1, BBBCoreConstants.ONE, BBBCoreConstants.THIRTY)) {
            nonPOBoxAddress = isStringPatternValid(getRules().getNonPOBoxAddressPattern(), pAddressLine1);
        }
        if (!nonPOBoxAddress && !isEmpty(pAddressLine2)
                && isStringLengthValid(pAddressLine2, BBBCoreConstants.ONE, BBBCoreConstants.THIRTY)) {
            nonPOBoxAddress = isStringPatternValid(getRules().getNonPOBoxAddressPattern(), pAddressLine2);
        }

        return !nonPOBoxAddress;
    }

    /**
     * This method is used to validate Address line1 for desigred pattern.
     *
     * @param pAddressLine1 the address line1
     * @return true if address line1 is valid. false otherwise.
     */
    public static boolean isValidAddressLine1(final String pAddressLine1) {
        if (isCrossSiteScripting(pAddressLine1)) {
        	LOG.logError("BBBUtility.isValidAddressLine1: isCrossSiteScripting[true]");
            return false;
        }
        if (!isEmpty(pAddressLine1)
                && isStringLengthValid(pAddressLine1, BBBCoreConstants.ONE, BBBCoreConstants.THIRTY)) {

            if (!pAddressLine1.startsWith(SPACE)) {
                return (isStringPatternValid(getRules().getAddressLine1Pattern(), pAddressLine1) && !pAddressLine1
                        .contains(DOUBLE_AMPERSAND));
            }
        }

        return false;

    }

    /**
     * This method is used to validate Address line2.
     *
     * @param pAddressLine2 the address line2
     * @return true if address line2 is valid. false otherwise.
     */
    public static boolean isValidAddressLine2(final String pAddressLine2) {
        if (isCrossSiteScripting(pAddressLine2)) {
            return false;
        }
        if (!isEmpty(pAddressLine2)
                && isStringLengthValid(pAddressLine2, BBBCoreConstants.ONE, BBBCoreConstants.THIRTY)) {

            if (!pAddressLine2.startsWith(SPACE)) {
                return (isStringPatternValid(getRules().getAddressLine2Pattern(), pAddressLine2) && !pAddressLine2
                        .contains(DOUBLE_AMPERSAND));
            }
        }

        return false;
    }

    /**
     * This method is used to validate Address line2.
     *
     * @param pAddressLine3
     * @return true if address line3 is valid. false otherwise.
     */
    public static boolean isValidAddressLine3(final String pAddressLine3) {
        if (isCrossSiteScripting(pAddressLine3)) {
            return false;
        }
        if (!isEmpty(pAddressLine3)
                && isStringLengthValid(pAddressLine3, BBBCoreConstants.ONE, BBBCoreConstants.THIRTY)) {

            if (!pAddressLine3.startsWith(SPACE)) {
                return (isStringPatternValid(getRules().getAddressLine2Pattern(), pAddressLine3) && !pAddressLine3
                        .contains(DOUBLE_AMPERSAND));
            }
        }

        return false;
    }
    
    /**
     * This method is used to validate first name.
     *
     * @param firstName the first name
     * @return true if first name is valid. false otherwise.
     */

    public static boolean isValidFirstName(final String firstName) {
        boolean validateStatus = true;
        if (isCrossSiteScripting(firstName)) {
            return false;
        }
        if (!isEmpty(firstName)) {

            if (!BBBUtility.isStringPatternValid(getRules().getNamePattern(), firstName)) {
                validateStatus = false;
            } else {
                if (!BBBUtility.isStringLengthValid(firstName, BBBCoreConstants.ONE, BBBCoreConstants.THIRTY)) {
                    validateStatus = false;
                }
            }

        } else {
            validateStatus = false;
        }

        return validateStatus;
    }

    /**
     * This method is used to validate city.
     *
     * @param pCity the city
     * @return true if city is valid. false otherwise.
     */
    public static boolean isValidCity(final String pCity) {
        if (isCrossSiteScripting(pCity)) {
            return false;
        }
        if (!isEmpty(pCity)) {
            if (!BBBUtility.isStringLengthValid(pCity, BBBCoreConstants.ONE, BBBCoreConstants.TWENTYFIVE)) {
                return false;
            }
            return isStringPatternValid(getRules().getCityNamePattern(), pCity);
        }

        return false;
    }

    /**
     * This method is used to validate state.
     *
     * @param pState the state
     * @return true if state is valid. false otherwise.
     */
    public static boolean isValidState(final String pState) {
        if (isCrossSiteScripting(pState)) {
            return false;
        }
        if (!isEmpty(pState)) {
            if (pState.length() != 2 ) {
                return false;
            }
            return isStringPatternValid(getRules().getStatePattern(), pState);
        }

        return false;
    }
    
    /**
     * This method is used to validate company Name.
     *
     * @param pCompanyName the company name
     * @return true if company name is valid. false otherwise.
     */
    public static boolean isValidCompanyName(final String pCompanyName) {
        if (isCrossSiteScripting(pCompanyName)) {
            return false;
        }
        if (!isEmpty(pCompanyName)) {
        	
            if (!BBBUtility.isStringLengthValid(pCompanyName, BBBCoreConstants.ONE, Integer.parseInt(getRules().getCollegeNameMaxLength()))) {
                return false;
            }

            return isStringPatternValid(getRules().getCompanyNamePattern(), pCompanyName);
        }

        return false;
    }

    /**
     * This method is used to validate zip code.
     *
     * @param pZip the zip
     * @return true if zip code is valid. false otherwise.
     */
    public static boolean isValidZip(final String pZip) {
        if (isCrossSiteScripting(pZip)) {
            return false;
        }
        if (!isEmpty(pZip)) {
            return BBBUtility.isStringPatternValid(getRules().getZipCodePattern(), pZip.toUpperCase());
        }
        return false;
    }
    
    /**
     * Checks if is canada zip valid.
     *
     * @param pZip the zip
     * @return true, if is canada zip valid
     */
    public static boolean isCanadaZipValid(final String pZip)
    {
    	if (isCrossSiteScripting(pZip)) {
            return false;
        }
        if (!isEmpty(pZip)) {
            return BBBUtility.isStringPatternValid(getRules().getZipCanadaCodePattern(), pZip.toUpperCase());
        }
        return false;
    }

    /**
     * This method is used to validate International zip code.
     *
     * @param pZip the zip
     * @return true if zip code is valid International zip code. false
     *         otherwise.
     */
    public static boolean isValidInternationalZip(final String pZip) {
        if (isCrossSiteScripting(pZip)) {
            return false;
        }
        if((!isEmpty(pZip) && pZip.contains(HYPHEN)) && BBBUtility.isStringLengthValid(pZip, BBBCoreConstants.ONE, BBBCoreConstants.TEN) ){
            return true;
        }
        else if (!isEmpty(pZip) && BBBUtility.isStringLengthValid(pZip, BBBCoreConstants.ONE, BBBCoreConstants.EIGHT)) {
            return true;
        }
        return false;
    }

    /**
     * This method is used to validate International State.
     *
     * @param pState the state
     * @return true if state is valid International state. false otherwise.
     */
    public static boolean isValidInternationalState(final String pState) {
        if (isCrossSiteScripting(pState)) {
            return false;
        }
        if (!isEmpty(pState) && BBBUtility.isStringLengthValid(pState, BBBCoreConstants.ONE, BBBCoreConstants.FOURTY)) {
            return true;
        }
        return false;
    }

    /**
     * This method is used to validate last name.
     *
     * @param pLastName the last name
     * @return true if last name is valid. false otherwise.
     */
    public static boolean isValidLastName(final String pLastName) {

        boolean validateStatus = true;
        if (isCrossSiteScripting(pLastName)) {
            return false;
        }
        if (!isEmpty(pLastName)
                && !BBBUtility.isStringLengthValid(pLastName, BBBCoreConstants.TWO, BBBCoreConstants.THIRTY)) {
            validateStatus = false;

        } else {
            if (!BBBUtility.isStringPatternValid(getRules().getNamePattern(), pLastName)) {
                validateStatus = false;
            }
        }
        return validateStatus;
    }
    
    /**
     * This method is used to validate Maxico last name.
     *
     * @param pLastName the last name
     * @return true if last name is valid. false otherwise.
     */
    public static boolean isValidMxLastName(final String pLastName) {

        boolean validateStatus = true;
        if (isCrossSiteScripting(pLastName)) {
            return false;
        }
        if (!isEmpty(pLastName)
                && !BBBUtility.isStringLengthValid(pLastName, BBBCoreConstants.TWO, BBBCoreConstants.THIRTY)) {
            validateStatus = false;

        } else {
            if (!BBBUtility.isStringPatternValid(getRules().getMxNamePattern(), pLastName)) {
                validateStatus = false;
            }
        }
        return validateStatus;
    }

    /**
     * This method is used to validate last name.
     *
     * @param pName the name
     * @return true if name is valid. false otherwise.
     */
    public static boolean isValidName(final String pName) {

        boolean validateStatus = true;
        if (isCrossSiteScripting(pName)) {
            return false;
        }
        if (!isEmpty(pName) && !BBBUtility.isStringLengthValid(pName, BBBCoreConstants.ONE, BBBCoreConstants.SIXTY)) {
            validateStatus = false;

        }
        return validateStatus;
    }

    /**
     * Checks if is validate registry id.
     *
     * @param pRegistryId the registry id
     * @return true if registry id is valid. false otherwise.
     */
    public static boolean isValidateRegistryId(final String pRegistryId) {

        boolean validateStatus = true;
        if (isCrossSiteScripting(pRegistryId)) {
            return false;
        }
        if (!isEmpty(pRegistryId)) {

            if (!BBBUtility.isStringPatternValid(getRules().getAlphaNumericPattern(), pRegistryId)) {
                validateStatus = false;
            } else {
                if (!BBBUtility.isStringLengthValid(pRegistryId, 1, MAX_REGISTRY_LENGTH)) {
                    validateStatus = false;
                }
            }

        } else {
            validateStatus = false;
        }

        return validateStatus;
    }

    /**
     * Checks if is valid number.
     *
     * @param requestedQuantity the requested quantity
     * @return true if requested quantity is a valid number. false otherwise.
     */
    public static boolean isValidNumber(final String requestedQuantity) {
        if (isCrossSiteScripting(requestedQuantity)) {
            return false;
        }
        if (!isEmpty(requestedQuantity)) {

            return isStringPatternValid(getRules().getNumericOnlyPattern(), requestedQuantity);
        }
        return false;

    }

    /**
     * This method is used to check if the given map is null or empty.
     *
     * @param map
     *            The Map which is to be checked.
     *
     * @return true if map is null or empty else false
     */
    public static boolean isMapNullOrEmpty(final Map <?, ?> map) {
        if (map == null) {
            return true;
        }
        return map.isEmpty();
    }

    /**
     * This method checks if a String is null or empty.
     *
     * @param string the string
     * @return true if string is null or empty else false.
     */
    public static boolean isEmpty(final String string) {
        return (string == null) || (string.trim().length() == 0);
    }

    /**
     * This method checks if a String[] is null or empty.
     *
     * @param string the string
     * @return true if string[] is null or empty else false.
     */
    public static boolean isEmpty(final String[] string) {
        return (string == null) || (string.length == 0);
    }
    
    /**
     * This method checks if a List is null or empty.
     *
     * @param string the string
     * @return true if string[] is null or empty else false.
     */
    public static boolean isEmpty(final List<?> list) {
        return (list == null) || (list.size() == 0);
    }


    /**
     * This method checks if String is not null or empty.
     *
     * @param str the str
     * @return boolean
     */
    public static boolean isNotEmpty(final String str) {
        return (!(isEmpty(str)));
    }

    /**
     * This method check input string for numeric char.
     *
     * @param requestedQuantity
     *            input sting to be validated
     * @return true if requested quantity is a numeric only. false otherwise.
     */
    public static boolean isNumericOnly(final String requestedQuantity) {
        if (!isEmpty(requestedQuantity)) {

            return isStringPatternValid(getRules().getNumericOnlyPattern(), requestedQuantity);
        }
        return false;

    }

    /**
     * This method check input string for numeric char.
     *
     * @param string
     *
     *            input sting to be validated
     * @return true if passed string follows cross site scripting pattern else
     *         false.
     */
    public static boolean isCrossSiteScripting(final String string) {
        if (!isEmpty(string)) {
            return isStringPatternValid(getRules().getCrossSiteScriptingPattern(), string);
        }
        return false;
    }

    /**
     * This method convert given Date to XMLGregorianCalendar .
     *
     * @param pDate            input Date
     * @return XMLGregorianCalendar
     * @throws BBBSystemException the BBB system exception
     */
    public static XMLGregorianCalendar getXMLCalendar(final Date pDate) throws BBBSystemException {
        XMLGregorianCalendar xmlGregorianCalendar = null;
        final GregorianCalendar gCalendar = new GregorianCalendar();
        if (pDate != null) {
            gCalendar.setTimeInMillis(pDate.getTime());
            try {
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
            } catch (final DatatypeConfigurationException e) {
                throw new BBBSystemException(BBBCoreErrorConstants.UTIL_ERROR_1000,
                        "Error while creating XML calendar instance using [" + gCalendar + "]", e);
            }
        }
        return xmlGregorianCalendar;
    }

    /**
     * Write file with the given data at given path.
     *
     * @param pFilePath the file path
     * @param pFileName the file name
     * @param pData the data
     * @return true file is successfully written. false otherwise.
     * @throws BBBSystemException the BBB system exception
     */
    public static boolean writeFile(final String pFilePath, final String pFileName, final String pData)
            throws BBBSystemException {

        boolean isFileWriteSuccess = false;
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;
        File file = null;
        try {

            final File dirStruc = new File(pFilePath);
            if (!dirStruc.exists()) {
                // Create the directory tree, if does
                // not exist
                dirStruc.mkdirs();
            }

            if (dirStruc.canWrite()) {

                file = new File(dirStruc, pFileName);

                if (file.exists()) { // If file exists, delete it
                    file.delete();
                }

                // Construct the BufferedWriter object
                fileWriter = new FileWriter(file);
                bufferedWriter = new BufferedWriter(fileWriter);

                // Start writing to the output stream
                bufferedWriter.write(pData);
                isFileWriteSuccess = true;

            } else {
                throw new BBBSystemException(BBBCoreErrorConstants.UTIL_ERROR_1001,
                        "Write permissions unavailable for directory : " + pFilePath);
            }

        } catch (final FileNotFoundException ex) {
            throw new BBBSystemException(BBBCoreErrorConstants.UTIL_ERROR_1002, "Error while creating file"
                    + (pFilePath + pFileName), ex);
        } catch (final IOException ex) {
            throw new BBBSystemException(BBBCoreErrorConstants.UTIL_ERROR_1003, "Error while creating file"
                    + (pFilePath + pFileName), ex);
        } finally {
            // Close the BufferedWriter and FileWriter.
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (final IOException ex) {
                throw new BBBSystemException(BBBCoreErrorConstants.UTIL_ERROR_1003, "Error while creating file"
                        + (pFilePath + pFileName), ex);
            }
        }

        return isFileWriteSuccess;
    }

    /**
     * Convert date into ws format.
     *
     * @param getDate the get date
     * @return date converted into WS format.
     */
    public static String convertDateIntoWSFormat(final String getDate) {
        if (StringUtils.isEmpty(getDate)) {
            return null;
        }
        final String[] temp = getDate.split(BBBCoreConstants.SLASH);
        return temp[2] + temp[0] + temp[1];
    }

    /**
     * This method converts US date form to Webservice form
     * 
     * US Date is in form MM/DD/YYYY WebService form is YYYYMMDD.
     *
     * @param getDate the get date
     * @return US date converted into WS format.
     */
    public static String convertUSDateIntoWSFormat(final String getDate) {
        if (StringUtils.isEmpty(getDate)) {
            return null;
        }
        final String[] temp = getDate.split(BBBCoreConstants.SLASH);
        return temp[2] + temp[0] + temp[1];
    }

    /**
     * Convert us date into ws format canada.
     *
     * @param getDate the get date
     * @return US date converted into Canada WS format.
     */
    public static String convertUSDateIntoWSFormatCanada(final String getDate) {
        if (StringUtils.isEmpty(getDate) || getDate.equalsIgnoreCase("0")) {

            return null;
        }
        final String[] temp = getDate.split(BBBCoreConstants.SLASH);
        return temp[1] + BBBCoreConstants.SLASH + temp[0] + BBBCoreConstants.SLASH + temp[2];
    }

    /**
     * Convert date ws to app format.
     *
     * @param eventDate the event date
     * @return WS format date converted into App format date.
     */
    public static String convertDateWSToAppFormat(final String eventDate) {
        String returnDate = eventDate;
        if (StringUtils.isEmpty(eventDate)) {

            return returnDate;
        }
        if ((eventDate.length() > MAX_DATE_LENGTH) && (eventDate.indexOf('/') < 0)) {
            final String year = eventDate.substring(0, 4);
            final String month = eventDate.substring(4, 6);
            final String day = eventDate.substring(6, 8);
            returnDate = month + BBBCoreConstants.SLASH + day + BBBCoreConstants.SLASH + year;
        }
        return returnDate;
    }

    /**
     * Convert date to app format.
     *
     * @param eventDate the event date
     * @return date converted into App format date.
     */
    public static String convertDateToAppFormat(final String eventDate) {
        String returnDate = eventDate;
        if (StringUtils.isEmpty(eventDate)) {
            return returnDate;
        }
        if ((eventDate.length() > MAX_DATE_LENGTH) && (eventDate.indexOf(BBBCoreConstants.SLASH) > 0)) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(eventDate.substring(3, 5));
            buffer.append(BBBCoreConstants.SLASH);
            buffer.append(eventDate.substring(0, 2));
            buffer.append(BBBCoreConstants.SLASH);
            buffer.append(eventDate.substring(6, 10));
            returnDate = buffer.toString();
        }
        return returnDate;
    }

    /**
     * This method convert Web Service Date format(yyyyMMdd) to US Date
     * format(MM/dd/yyyy).
     *
     * @param date the date
     * @return convertedDate
     */
    public static String convertWSDateToUSFormat(final String date) {
        String convertedDate = date;
        if (StringUtils.isEmpty(date)) {
            return convertedDate;
        }
        if ((date.length() > MAX_DATE_LENGTH) && (date.indexOf(BBBCoreConstants.SLASH) < 0)) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(date.substring(4, 6));
            buffer.append(BBBCoreConstants.SLASH);
            buffer.append(date.substring(6, 8));
            buffer.append(BBBCoreConstants.SLASH);
            buffer.append(date.substring(0, 4));
            convertedDate = buffer.toString();
        }
        return convertedDate;
    }

    /**
     * This method convert App format Date format(MMddyyyy) to CA Date
     * format(dd/MM/yyyy).
     *
     * @param date the date
     * @return convertedDate
     */
    public static String convertAppFormatDateToCAFormat(final String date) {
        String convertedDate = date;
        if (StringUtils.isEmpty(date)) {
            return convertedDate;
        }
        if ((date.length() > MAX_DATE_LENGTH) && (date.indexOf(BBBCoreConstants.SLASH) > 0)) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(date.substring(3, 5));
            buffer.append(BBBCoreConstants.SLASH);
            buffer.append(date.substring(0, 2));
            buffer.append(BBBCoreConstants.SLASH);
            buffer.append(date.substring(6, 10));
            convertedDate = buffer.toString();
        }
        return convertedDate;
    }

    /**
     * This method converts CA date format(dd/MM/yyyy) to Web Service
     * formmat(yyyyMMdd).
     *
     * @param date the date
     * @return CA date converted into WS date format.
     */
    public static String convertCADateIntoWSFormat(final String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        final String[] convertedDate = date.split(BBBCoreConstants.SLASH);
        return convertedDate[2] + convertedDate[1] + convertedDate[0];
    }

    /**
     * This method convert Web Service Date format(yyyyMMdd) to CA Date
     * format(dd/MM/yyyy).
     *
     * @param date the date
     * @return convertedDate
     */
    public static String convertWSDateToCAFormat(final String date) {
        String convertedDate = date;
        if (StringUtils.isEmpty(date)) {
            return convertedDate;
        }
        if ((date.length() > MAX_DATE_LENGTH) && (date.indexOf(BBBCoreConstants.SLASH) < 0)) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(date.substring(6, 8));
            buffer.append(BBBCoreConstants.SLASH);
            buffer.append(date.substring(4, 6));
            buffer.append(BBBCoreConstants.SLASH);
            buffer.append(date.substring(0, 4));
            convertedDate = buffer.toString();
        }
        return convertedDate;
    }

    /**
     * Net work flag value.
     *
     * @param registryCode the registry code
     * @return Y/ N based on registry code.
     */
    public static String netWorkFlagValue(final String registryCode) {
        if (!StringUtils.isEmpty(registryCode) || registryCode.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_WEDDING)
                || registryCode.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_CERMONY) || registryCode.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_BABY)) {

            return "Y";
        }
        return "N";

    }

    /**
     * Refine facet string.
     *
     * @param passRefineString the pass refine string
     * @return refined facet string
     */
    public static String refineFacetString(final String passRefineString) {
        String refineFacetString = passRefineString;
        if (passRefineString != null) {
            if (passRefineString.startsWith(LESS_THAN_SYMBOL)) {
                final int rightIndex = passRefineString.indexOf(GREATER_THAN_SYMBOL);
                if(rightIndex > 0) {
                refineFacetString = passRefineString.replace(passRefineString.substring(0, rightIndex + 1), BLANK_STRING);
                final int rightSecondIndex = refineFacetString.indexOf(LESS_THAN_SYMBOL);
                    if(rightSecondIndex > 0) {
                refineFacetString = refineFacetString.replace(
                        refineFacetString.substring(rightSecondIndex, refineFacetString.length()), BLANK_STRING);
                    }
                } 
            }
            // to be updated with a corrective fix
            refineFacetString = refineFacetString.replaceAll(SLASH_APOSTROPHE, BLANK_STRING);
            refineFacetString = refineFacetString.replaceAll("&#39;", BLANK_STRING);
        }
        return refineFacetString;

    }

    /**
     * method check for the valid password.
     *
     * @param pValue            string to check for valid password
     * @return true if password is valid. false otherwise.
     */
    public static boolean isValidPassword(final String pValue) {

        return isStringPatternValid(getRules().getPasswordPattern(), pValue);

    }

    /**
     * Encode nursery decor theme.
     *
     * @param encodeNurseryDecorTheme the encode nursery decor theme
     * @return encoded nursery decor theme.
     */
    public static String EncodeNurseryDecorTheme(final String encodeNurseryDecorTheme) {
        if (encodeNurseryDecorTheme == null) {
            return BLANK_STRING;
        }
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(encodeNurseryDecorTheme);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append(LESS_THAN);
            } else if (character == '>') {
                result.append(GREATER_THAN);
            }

            else if (character == '&') {
                result.append(AMPERSAND);
            }

            else if (character == '\\') {
                result.append(BACKSLASH);
            } else if (character == '\'') {
                result.append(APOSTROPHE);
            } else if (character == '\"') {
                result.append(QUOTE);
            } else {
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    /**
     * Decode nursery decor theme.
     *
     * @param nurseryDecorTheme the nursery decor theme
     * @return decoded nursery decor theme.
     */
    public static String DecodeNurseryDecorTheme(final String nurseryDecorTheme) {
        String result = null;
        if (nurseryDecorTheme == null) {
            return BLANK_STRING;
        }
        Matcher matcher = LESS_THAN_PATTERN.matcher(nurseryDecorTheme);
        result = matcher.replaceAll(LESS_THAN_SYMBOL);
        matcher = GREATER_THAN_PATTERN.matcher(result);
        result = matcher.replaceAll(GREATER_THAN_SYMBOL);
        matcher = AMPERSAND_PATTERN.matcher(result);
        result = matcher.replaceAll(BBBCoreConstants.AMPERSAND);
        matcher = BACKSLASH_PATTERN.matcher(result);
        result = matcher.replaceAll("\\\\");
        matcher = QUOTE_PATTERN.matcher(result);
        result = matcher.replaceAll(SLASH_QUOTE);
        matcher = APOSTROPHE_PATTERN.matcher(result);
        result = matcher.replaceAll(SLASH_APOSTROPHE);
        return result;
    }

    /**
     * This method format currency 1000.00 to $1,000.00 format
     *
     * @param currency the currency
     * @return formatedCurrency
     */
    public static String FormatCurrency(final String currency) {
        String formatedCurrency = null;
        final NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        formatedCurrency = numberFormat.format(new BigDecimal(currency));
        return formatedCurrency;
    }

    /**
     * Adds the cookie.
     *
     * @param pResponse the response
     * @param cookie the cookie
     * @param isHttpOnlyRequired the is http only required
     */
    public static void addCookie(final DynamoHttpServletResponse pResponse, final Cookie cookie,
            final boolean isHttpOnlyRequired) {
        if (isHttpOnlyRequired) {

            final StringBuffer header = new StringBuffer();
            if ((cookie.getName() != null) && (!cookie.getName().equals(BLANK_STRING))) {
                header.append(cookie.getName());
            }
            if (cookie.getValue() != null) {
                header.append("=" + cookie.getValue());
            }

            if (cookie.getVersion() == 1) {
                header.append(";version=1");
                if (cookie.getComment() != null) {
                    header.append(";comment=\"" + cookie.getComment() + SLASH_QUOTE);
                }
                if (cookie.getMaxAge() > -1) {
                    header.append(";max-age=" + cookie.getMaxAge());
                }
            } else {
                if (cookie.getMaxAge() > -1) {
                    final Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, cookie.getMaxAge());
                    // Date currentDate = new Date();

                    final SimpleDateFormat cookieFormat = new SimpleDateFormat(EXPIRY_DATE_FORMAT);
                    cookieFormat.setTimeZone(TimeZone.getTimeZone(GMT));
                    header.append(";expires=" + cookieFormat.format(cal.getTime()));
                }
            }

            if (cookie.getDomain() != null) {
                header.append("; domain=" + cookie.getDomain());
            }
            if (cookie.getPath() != null) {
                header.append("; path=" + cookie.getPath());
            }
            if (cookie.getSecure()) {
                header.append("; secure");
            }
            header.append("; HttpOnly");
            // pResponse.addHeader("Set-Cookie", header.toString());
            pResponse.getResponse().addHeader("Set-Cookie", header.toString());
            if (LOG.isLoggingDebug()) {
                LOG.logDebug("Added Cookies " + header.toString());
            }
        } else {
            pResponse.addCookie(cookie);
            if (LOG.isLoggingDebug()) {
                LOG.logDebug("Added Cookies : " + cookie.getName() + " = " + cookie.getValue());
            }
        }
    }
    
    /**
     * Gets the cookie.
     *
     * @param pRequest the request
     * @param cookieName the cookie name
     * @return the cookie
     */
    public static String getCookie(final DynamoHttpServletRequest pRequest, String cookieName)
    {
    	String cookieValue=null;
    	
    	Cookie[] cookies = pRequest.getCookies();

		if (cookies != null) {
		 for (Cookie cookie : cookies) {
		   if (cookie.getName().equals(cookieName)) {
			   cookieValue = cookie.getValue();
		    }
		  }
		}
    	return cookieValue;
    }

    /**
     * Pass err to page.
     *
     * @param errCode the err code
     * @param errValue the err value
     */
    public static void passErrToPage(final String errCode, final String errValue) {
        try {
            final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
            final SystemErrorInfo errorInfo = (SystemErrorInfo) pRequest.resolveName(SYSTEM_ERROR_INFO);
            List <ErrorInfoVO> errorList = new ArrayList <ErrorInfoVO>();
            final ErrorInfoVO errorInfoVO = new ErrorInfoVO();

            if ((errorInfo.getErrorList() != null) && !errorInfo.getErrorList().isEmpty()) {
                errorList = errorInfo.getErrorList();
            }
            errorInfoVO.setErrorCode(errCode);
            errorInfoVO.setErrorDescription(errValue);
            errorList.add(errorInfoVO);
            errorInfo.setErrorList(errorList);
        } catch (final Exception e) {
            if (LOG.isLoggingDebug()) {
                LOG.logError("Error in passErrToPage " + e.getMessage());
            }
        }
    }

    /**
     * This method is used to get the given date difference in days from the
     * current date.
     *
     * @param pEventDateStr            date in <String> format.
     * @param siteId the site id
     * @return difference in the number of days from the current date
     * @throws ParseException the parse exception
     */
    public static long getDateDiff(final String pEventDateStr, final String siteId) throws ParseException {
        // check if user registry event is past date and more than 90 days.
    	SimpleDateFormat dateFormat = null;
    	Date eventDate = null;
        long diffDays = 0;
        
        try {
            if (!isEmpty(pEventDateStr)) {
                if (pEventDateStr.contains(BBBCoreConstants.SLASH)) {
        			if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
                        dateFormat = new SimpleDateFormat(BBBCoreConstants.CA_DATE_FORMAT);
                    } else {
                        dateFormat = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
                    }
                } else {
                    dateFormat = new SimpleDateFormat(BBBCoreConstants.WS_DATE_FORMAT);
                }
                eventDate = dateFormat.parse(pEventDateStr);
            }
        } catch (final ParseException e) {
			diffDays = 1l;
            if (LOG.isLoggingDebug()) {
                LOG.logError("Error in pasrseDate " + e.getMessage());
            }
        }
        if (eventDate != null) {
            final long eventDateLong = eventDate.getTime();
            final long currentDate = new Date().getTime();
            diffDays = eventDateLong - currentDate;
            diffDays = diffDays / (1000 * 60 * 60 * 24);

            return Long.valueOf(diffDays);
        }
        return Long.valueOf(diffDays);
    }

    /**
     * Checks if is integer.
     *
     * @param input the input
     * @return true if input string is integer. false otherwise.
     */
    public static boolean isInteger(final String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            LOG.logError("Error in isInteger method " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if is float.
     *
     * @param input the input
     * @return true if input string is float. false otherwise.
     */
    public static boolean isFloat(final String input) {
        try {
            Float.parseFloat(input);
            return true;
        } catch (final NumberFormatException e) {
            LOG.logError("Error in isFloat method " + e.getMessage());
            return false;
        }
    }

    /**
     * This method validate the Date.
     *
     * @param date            as String
     * @param dateFormat            as String
     * @return boolean
     */
    public static boolean isValidDate(final String date, final String dateFormat) {

        boolean result = false;
        if (StringUtils.isEmpty(date)) {
            result = false;
        } else {
            SimpleDateFormat sdf = null;
            if (StringUtils.isEmpty(dateFormat)) {
                sdf = new SimpleDateFormat(BBBCoreConstants.DATE_FORMAT);
            } else {
                sdf = new SimpleDateFormat(dateFormat);
            }
            sdf.setLenient(false);
            Date validDate = null;
            try {
                validDate = sdf.parse(date);
            } catch (final ParseException e) {
                result = false;
            }
            if (validDate != null) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Gets the channel.
     *
     * @return channel Id.
     */
    public static String getChannel() {
        String channelId = null;
        final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        if (request != null) { 
            channelId = request.getHeader(BBBCoreConstants.CHANNEL);
            if (channelId == null) {
                channelId = BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
            }
        } else {
            channelId = BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
        }
        return channelId;
    }
    
    /**
     * returns channelThemeId (X-bbb-channel-theme) from header.
     *
     * @return channelThemeId
     */
    public static String getChannelTheme() {
        String channelThemeId = null;
        final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        if (request != null) {
        	channelThemeId = request.getHeader(BBBCoreConstants.CHANNEL_THEME);
        }
         return channelThemeId;
    }
    
    
    /**
     * Gets the origin of traffic.
     *
     * @return origin of traffic.
     */
    public static String getOriginOfTraffic() {
        String originOfTraffic = null;
        final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        if (null != request) {
        	originOfTraffic = request.getHeader(BBBCoreConstants.ORIGIN_OF_TRAFFIC);
            if (null == originOfTraffic) {
            	originOfTraffic = BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
            }
        } else {
        	originOfTraffic = BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
        }
        if (LOG.isLoggingDebug()) {
            LOG.logDebug("Origin Of Traffic : " + originOfTraffic);
        }
        return originOfTraffic;
    }
    
    /**
     * Gets the traffic os.
     *
     * @return traffic OS
     */
    public static String getTrafficOS() {
        String trafficOS = BBBCoreConstants.ANDRIOD_OS;
        final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        if (null != request) {
        	trafficOS = request.getHeader(BBBCoreConstants.TRAFFIC_OS); 
        } 
        if (LOG.isLoggingDebug()) {
            LOG.logDebug("Traffic OS: " + trafficOS);
        }
        return trafficOS;
    }

    /**
     * This method checks if a Boolean is null.
     *
     * @param pDate the date
     * @return true if string is null or empty else false.
     */
    public static boolean isNull(final Date pDate) {
        return (pDate == null);
    }

    /**
     * Checks if is attribute applicable.
     *
     * @param previewDate the preview date
     * @param startDate the start date
     * @param endDate the end date
     * @return true if date attribute is applicable. false otherwise.
     */
    public static boolean isAttributeApplicable(final Date previewDate, final Date startDate, final Date endDate) {

        if (isNull(startDate) || isNull(endDate)) {
            if (isNull(endDate)
                    && (!isNull(startDate) && (previewDate.after(startDate) || previewDate.equals(startDate)))) {
                return true;
            } else if (isNull(startDate)
                    && (!isNull(endDate) && (previewDate.before(endDate) || previewDate.equals(endDate)))) {
                return true;
            } else if (isNull(startDate) && isNull(endDate)) {
                return true;
            }
        } else if ((previewDate.after(startDate) || previewDate.equals(startDate))
                && (previewDate.before(endDate) || previewDate.equals(endDate))) {
            return true;
        }
        return false;
    }

    /**
     * This method is used to validate Credit Card Name.
     *
     * @param nameOnCard the name on card
     * @return true if credit card is name is valid. false otherwise.
     */

    public static boolean isValidCreditCardName(final String nameOnCard) {
        boolean validateStatus = true;
        if (isCrossSiteScripting(nameOnCard)) {
            return false;
        }
        if (!isEmpty(nameOnCard)) {
            if (!BBBUtility.isStringPatternValid(getRules().getNameOnCardPattern(), nameOnCard)) {
                validateStatus = false;
            } else {
                if (!BBBUtility.isStringLengthValid(nameOnCard, BBBCoreConstants.FOUR, BBBCoreConstants.SIXTYONE)) {
                    validateStatus = false;
                }
            }
        } else {
            validateStatus = false;
        }
        return validateStatus;
    }
    
    
    /**
     * method to check if list is null or empty.
     *
     * @param inputList collection object to check for empty
     * @return boolean if list is null or empty return true otherwise false
     */
    public static boolean isListEmpty(List inputList){
    	if(inputList == null || inputList.isEmpty()){
    		return true;
    	}
    	return false;
    }
    
    public static boolean isCollectionEmpty(Collection inputSet){
    	return (inputSet == null || inputSet.isEmpty());
    }
    
    /**
     * Checks if is valid m d5.
     *
     * @param md5Hash the md5 hash
     * @return true, if is valid m d5
     */
    public static boolean isValidMD5(String md5Hash) {
        return md5Hash!=null && md5Hash.matches(MD5_CHARS);
    }
	
	/**
     * This method check input string for Registry name.
     *
     * @param inputString
     *            input sting to be validated
     * @return true if requested quantity is a alphabet only. false otherwise.
     */
    public static boolean isRegistryNameValid(final String inputString) {
        if (!isEmpty(inputString)) {

            return isStringPatternValid(getRules().getRegistryNamePattern(), inputString);
        }
        return false;

    }
    
    
    /**
     * This method check input string for alphabet char.
     *
     * @param alphaString
     *            input sting to be validated
     * @return true if requested quantity is a alphabet only. false otherwise.
     */
    public static boolean isAlphaOnly(final String alphaString) {
        if (!isEmpty(alphaString)) {

            return isStringPatternValid(getRules().getAlphabetOnlyPattern(), alphaString);
        }
        return false;

    }

	/**
	 * modifying firstName coming from Paypal as per BBB validation rules.
	 *
	 * @param firstName the first name
	 * @return modifiedFirstName
	 */
    public static String modifyFirstName(String firstName) {
		String modifiedFirstName = BLANK_STRING;
		List<String> specialCharsToExclude=new ArrayList<String>();
		specialCharsToExclude.add(HYPHEN);
		specialCharsToExclude.add(SLASH_APOSTROPHE);
		specialCharsToExclude.add(PERIOD);
		specialCharsToExclude.add(SPACE);
		if (!StringUtils.isEmpty(firstName)) {
			modifiedFirstName = handleFirstSpecialCharacter(firstName, specialCharsToExclude);
			modifiedFirstName = modifiedFirstName.trim().replaceAll(
					getRules().getNamePatternComlement(), BLANK_STRING);
			if (modifiedFirstName.length() > THIRTY) {
				modifiedFirstName = modifiedFirstName.substring(ZERO,THIRTY);
			}
		}
		return modifiedFirstName;
	}
    
   

	/**
	 * modifying lastName coming from Paypal as per BBB validation rules.
	 *
	 * @param lastName the last name
	 * @return modifiedLastName
	 */
	public static String modifyLastName(String lastName) {
		String modifiedLastName=BLANK_STRING;
		List<String> specialCharsToExclude=new ArrayList<String>();
		specialCharsToExclude.add(HYPHEN);
		specialCharsToExclude.add(SLASH_APOSTROPHE);
		specialCharsToExclude.add(PERIOD);
		specialCharsToExclude.add(SPACE);
		if (!StringUtils.isEmpty(lastName)) {
			modifiedLastName = handleFirstSpecialCharacter(lastName,specialCharsToExclude);
			modifiedLastName = modifiedLastName.trim().replaceAll(
					getRules().getNamePatternComlement(), BLANK_STRING);
			if (modifiedLastName.length() == ONE) {
				modifiedLastName = lastName.concat(modifiedLastName);
			}
			if (modifiedLastName.length() > THIRTY) {
				modifiedLastName = modifiedLastName.substring(ZERO,THIRTY);
			}
		}
		return modifiedLastName;
	}
    
	/**
	 * modifying address1 coming from Paypal as per BBB validation rules.
	 *
	 * @param address1 the address1
	 * @return modifiedAddress1
	 */
	public static String modifyAddress1(String address1) {
		String modifiedAddress1 = BLANK_STRING;
		if (!StringUtils.isEmpty(address1)) {
			modifiedAddress1 = address1.trim().replaceAll(
					getRules().getAddressLine1PatternComplement(),BLANK_STRING);
			if (modifiedAddress1.length() > THIRTY) {
				modifiedAddress1 = modifiedAddress1.substring(ZERO,THIRTY);
			}
		}
		return modifiedAddress1;
	}
    
	/**
	 * modifying address2 coming from Paypal as per BBB validation rules.
	 *
	 * @param address2 the address2
	 * @return modifiedAddress2
	 */
	public static String modifyAddress2(String address2) {
		String modifiedAddress2 = BLANK_STRING;
		if (!StringUtils.isEmpty(address2)) {
			modifiedAddress2 = address2.trim().replaceAll(
					getRules().getAddressLine2PatternComplement(), BLANK_STRING);
			if (modifiedAddress2.length() > THIRTY) {
				modifiedAddress2 = modifiedAddress2.substring(ZERO,THIRTY);
			}
		}
		return modifiedAddress2;
	}

	/**
	 * modifying cityName coming from Paypal as per BBB validation rules.
	 *
	 * @param city the city
	 * @return city
	 */
	public static String modifyCity(String city) {
		String modifiedCity=BLANK_STRING;
		List<String> specialCharsToExclude=new ArrayList<String>();
		specialCharsToExclude.add(PERIOD);
		specialCharsToExclude.add(SLASH_APOSTROPHE);
		specialCharsToExclude.add(HYPHEN);
		specialCharsToExclude.add(SPACE);
		if (!StringUtils.isEmpty(city)) {
			modifiedCity = handleFirstSpecialCharacter(city,specialCharsToExclude);
			modifiedCity = modifiedCity.trim().replaceAll(
					getRules().getCityPatternComplement(), BLANK_STRING);
			if (modifiedCity.length() > TWENTY_FIVE) {
				modifiedCity = modifiedCity.substring(ZERO,TWENTY_FIVE);
			}
		}
		return modifiedCity;
	}
	 
 	/**
 	 * This method removes any special character supplied in List from first place of the supplied String .
 	 *
 	 * @param stringToBeModified the string to be modified
 	 * @param specialCharsToExclude the special chars to exclude
 	 * @return modifiedString
 	 */
	    private static  String handleFirstSpecialCharacter(String stringToBeModified, List<String> specialCharsToExclude) {
	    	String modifiedString = BLANK_STRING;
	    	if (!StringUtils.isEmpty(stringToBeModified)) {
	    		if (specialCharsToExclude != null &&  !specialCharsToExclude.isEmpty()) {
	    			modifiedString = stringToBeModified;
	    			while(specialCharsToExclude.contains(modifiedString.substring(ZERO, ONE)))
	    			{
	    				modifiedString = modifiedString.substring(ONE); 
	    			}
	    		}
	    	}
	    	return modifiedString;
		}


	    /**
    	 * Get Tracking Order Status.
    	 *
    	 * @param orderStatus the order status
    	 * @param objOrderDetailRes the obj order detail res
    	 * @param dateFormat the date format
    	 * @return orderStatus
    	 * @throws ParseException the parse exception
    	 */
		public static String getTrackingOrderStatus(String orderStatus,OrderDetailInfoReturn objOrderDetailRes,String dateFormat) throws ParseException {
			if(BBBCoreConstants.TRACKING_ORDER_STATUS_F.equalsIgnoreCase(orderStatus) || 
					BBBCoreConstants.TRACKING_ORDER_STATUS_P.equalsIgnoreCase(orderStatus)){
				return BBBCoreConstants.STATUS_ORDER_BEING_PROCESSED;
				
			}else if(BBBCoreConstants.TRACKING_ORDER_STATUS_T.equalsIgnoreCase(orderStatus)){
				String shipDate=objOrderDetailRes.getShipping().getShipDt();
				return BBBCoreConstants.STATUS_ORDER_T + formatDate(isNotEmpty(shipDate)?shipDate:objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getStatusDt(),dateFormat);
				
			}else if(BBBCoreConstants.TRACKING_ORDER_STATUS_D.equalsIgnoreCase(orderStatus)){
				String deliveryDate=objOrderDetailRes.getShipping().getDeliveryDt();
				return BBBCoreConstants.STATUS_ORDER_D + formatDate(isNotEmpty(deliveryDate)?deliveryDate:objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getStatusDt(),dateFormat);
				
			}else if(BBBCoreConstants.TRACKING_ORDER_STATUS_C.equalsIgnoreCase(orderStatus)){
				return BBBCoreConstants.STATUS_ORDER_C + formatDate(objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getStatusDt(),dateFormat);
				
			}else {
				String status = orderStatus;
				if(!isEmpty(orderStatus) && orderStatus.length() > 1){
					status = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
				}
				return status;
			}	
		}
		
		/**
		 * method to convert String into Date format .
		 *
		 * @param dateAsString the date as string
		 * @param dateFormat the date format
		 * @return the string
		 * @throws ParseException the parse exception
		 */
		public static String formatDate(String dateAsString,String dateFormat) throws ParseException {

			DateFormat formatter = new SimpleDateFormat(dateFormat);
			String formattedDate ="";
			if(!isEmpty(dateAsString)){
				Date date= formatter.parse(dateAsString);
				formattedDate=formatter.format(date);
			}
			return formattedDate;
			
		}
		
		/**
		 * This method is used to get alternate channel foe a given request.
		 *
		 * @return channel Id.
		 */
	    public static String getAlternateChannel() {
	        String channelId = null;
	        final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
	        if (request != null) {
	            channelId = request.getHeader(BBBCoreConstants.CHANNEL);
	            if (channelId == null) {
	                channelId = BBBCoreConstants.MOBILEWEB;
	            }else{
	            	 channelId = BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
	            }
	        } 
	        return channelId;
	    }
	    
	    /**
    	 * Checks if is valid coupon code.
    	 *
    	 * @param pCouponCode the coupon code
    	 * @return true if coupon code is valid. false otherwise.
    	 */
	    public static boolean isValidCouponCode(final String pCouponCode) {

	        boolean validateStatus = true;	       
	        if (!isEmpty(pCouponCode)) {
	        	
	            if (!BBBUtility.isStringPatternValid(getRules().getAlphaNumericPattern(), pCouponCode)) {
	                validateStatus = false;
	            } else {
	                if ((!BBBUtility.isStringLengthValid(pCouponCode, 1, COUPON_CODE_LENGTH_8))&&(!BBBUtility.isStringLengthValid(pCouponCode, 1, COUPON_CODE_LENGTH_12))&&(!BBBUtility.isStringLengthValid(pCouponCode, 1, COUPON_CODE_LENGTH_20))&&(!BBBUtility.isStringLengthValid(pCouponCode, 1, COUPON_CODE_LENGTH_30))) {
	                    validateStatus = false;
}
	            }

	        } 
	        return validateStatus;
	    }  
	    
	    
	    
	    /**
		 * This method is used to convert US dollar prices to International Prices.
		 * 
		 * @param oldPrice in <code>String</code> format
		 * @param lowPrice in <code>String</code> format
		 * @param highPrice in <code>String</code> format
		 * @param prop in <code>Properties</code> format
		 * @return oldPrice in <code>String</code> format
		 */
		public static String convertToInternationalPrice(String oldPrice,String lowPrice,String highPrice,Properties prop)
		{
			String lowVal=lowPrice,highVal=highPrice;
			lowPrice=BBBCoreConstants.DOLLAR+lowPrice;
			highPrice=BBBCoreConstants.DOLLAR+highPrice;

			if(!(BBBUtility.isEmpty(lowVal)) && lowVal.contains(BBBCoreConstants.DOLLAR)){
				lowVal=lowVal.substring(1);
				}
			if(!(BBBUtility.isEmpty(highVal)) && highVal.contains(BBBCoreConstants.DOLLAR)){
				highVal=highVal.substring(1);
				}
			if(lowVal.contains(BBBInternationalShippingConstants.LPTOKEN))
				lowVal=BBBInternationalShippingConstants.LPTOKEN;
			if(highVal.contains(BBBInternationalShippingConstants.HPTOKEN))
				highVal=BBBInternationalShippingConstants.HPTOKEN;
			String newLowPrice = BBBCoreConstants.BLANK, newHighPrice =BBBCoreConstants.BLANK;
			try {
				if (!BBBUtility.isEmpty(lowPrice) && !BBBUtility.isEmpty(lowVal)) {
					newLowPrice = TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).convertObjectToString(ServletUtil.getCurrentRequest(), Double.valueOf(lowVal),prop).toString();
					oldPrice = oldPrice.replace(lowPrice,newLowPrice);
				}
				if (!BBBUtility.isEmpty(highPrice) && !BBBUtility.isEmpty(highVal)) {
					newHighPrice = TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).convertObjectToString(ServletUtil.getCurrentRequest(), Double.valueOf(highVal),prop).toString();
					oldPrice = oldPrice.replace(highPrice,newHighPrice);
				}
			} catch (TagConversionException e) {
				 LOG.logError("Error in convertToInternationalPrice method " + e.getMessage());
			}
		return oldPrice;
		}
		
	    /**
	     * This method is used to convert US dollar prices to International Prices.
	     * 
	     * @param oldPrice in <code>String</code> format
	     * @param lowPrice in <code>String</code> format
		 * @param highPrice in <code>String</code> format
		 * @param prop in <code>Properties</code> format
		 * @return oldPrice in <code>String</code> format
		 */
		public static String convertToInternationalPriceByToken(String oldPrice,String lowPrice,String highPrice,Properties prop)
		{
					String lowVal=lowPrice,highVal=highPrice;
					lowPrice=BBBInternationalShippingConstants.LPTOKEN;
					highPrice=BBBInternationalShippingConstants.HPTOKEN;
					String newLowPrice = BBBCoreConstants.BLANK, newHighPrice =BBBCoreConstants.BLANK;
					try {
						if (!BBBUtility.isEmpty(lowPrice) && !BBBUtility.isEmpty(lowVal)) {
							newLowPrice = TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).convertObjectToString(ServletUtil.getCurrentRequest(), Double.valueOf(lowVal),prop).toString();
							oldPrice = oldPrice.replace(lowPrice,newLowPrice);
						}
						if (!BBBUtility.isEmpty(highPrice) && !BBBUtility.isEmpty(highVal)) {
							newHighPrice = TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).convertObjectToString(ServletUtil.getCurrentRequest(), Double.valueOf(highVal),prop).toString();
							oldPrice = oldPrice.replace(highPrice,newHighPrice);
						}
						
					} catch (TagConversionException e) {
						 LOG.logError("Error in convertToInternationalPrice method " + e.getMessage());
					}
			return oldPrice;
		}
				
		
		/**
		 * method to check if Array is null or empty.
		 *
		 * @param inputArray Array object to check for empty
		 * @return boolean if inputArray is null or empty return true otherwise false
		 */
		
		public static boolean isArrayEmpty(Object[] inputArray) {
			if(inputArray == null || inputArray.length == 0){
	    		return true;
	    	}
	    	return false;
		}

/**
 * This method is used to checkPennyPrice.
 *
 * @param pricerange the pricerange
 * @param lowPrice            in <code>String</code> format
 * @param highPrice            in <code>String</code> format
 * @param prop            in <code>Properties</code> format
 * @return oldPrice in <code>String</code> format
 */
	public static boolean checkPennyPrice(String pricerange, String lowPrice,
			String highPrice, Properties prop) {
		boolean pennyPrice = false;
		String lowVal = lowPrice;

		if (pricerange.contains(BBBInternationalShippingConstants.LPTOKEN))
			lowPrice = BBBInternationalShippingConstants.LPTOKEN;
		String newLowPrice = "";
		try {
			if (!BBBUtility.isEmpty(lowPrice) && !BBBUtility.isEmpty(lowVal)) {
				newLowPrice = lowVal;

			}

			if (newLowPrice != null
					&& ((Double.parseDouble(newLowPrice) <= BBBCoreConstants.POINT_ZERO_ONE))) {
				pennyPrice = true;

			}

		} catch (NumberFormatException exception) {
			LOG.logError(
					"EndecaSearch.getProductList || NumberFormatException occured in ",
					exception);
		}

		return pennyPrice;
	}

		/**
		 * Checks if is valid ip address.
		 *
		 * @param iPAddress the i p address
		 * @return true, if is valid ip address
		 */
		public static boolean isValidIpAddress(String iPAddress){
			String pPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
			final Pattern pattern = Pattern.compile(pPattern);
	        final Matcher match = pattern.matcher(iPAddress);
	        return match.matches();			
		}
		
		/**
		 * This method checks two Strings if they are equal or not.
		 *
		 * @param str1 the str1
		 * @param str2 the str2
		 * @return true if strings are equal or both are null
		 * false if strings are not equal
		 */
		public static boolean compareStringsIgnoreCase(final String str1,
				final String str2){
			
			if(!isEmpty(str1)){
				return str1.equalsIgnoreCase(str2);			
			}else if(!isEmpty(str2)){
				return false;
			}else{
				return true;
			}
			
		}
		
		/**
		 * |BBBP-3568|Preventing to add a LTL product to registry for PO box Address.
		 *
		 * @param addressLine1 the address line1
		 * @param addressLine2 the address line2
		 * @return true, if is PO box address
		 */
	public static boolean isPOBoxAddress(final String addressLine1,
			final String addressLine2) {
		StringBuffer buffer = new StringBuffer();
		boolean isValid = false;

		if (isNotEmpty(addressLine1)) {
			buffer.append(addressLine1);
		}

		if (isNotEmpty(addressLine2)) {
			buffer.append(" " + addressLine2);
		}

		if (isNotEmpty(buffer.toString())) {
			Pattern regex = Pattern.compile(BBBCoreConstants.POBoxPattern,
					Pattern.DOTALL);
			Matcher regexMatcher = regex.matcher(buffer);
			isValid = regexMatcher.find();
		}
		return isValid;
	}
		
		
		/**
		 *  BPSI-3285 DSK | Handle Pricing message for Personalized Item.
		 *
		 * @param onSale the on sale
		 * @param listPrice the list price
		 * @param salePrice the sale price
		 * @param personalizationType the personalization type
		 * @param personalizedPrice the personalized price
		 * @param referenceNumber the reference number
		 * @return the double
		 */
		public static Double  checkCurrentPriceForSavedItem(boolean onSale, Double listPrice, Double salePrice, String personalizationType, double personalizedPrice, String  referenceNumber) {
			Double currentPrice  = 0.0;
			if (LOG.isLoggingDebug()) {
			LOG.logDebug("TopStatusChangeMessageDroplet :: checkCurrentPriceForSavedItem () start");
			LOG.logDebug("TopStatusChangeMessageDroplet :: checkCurrentPriceForSavedItem () onSale::" + onSale + ",listPrice :: " + listPrice + ",salePrice :: "+ salePrice + 
					",personalizationType :: " + personalizationType + ", :: personalizedPrice " + personalizedPrice +", referenceNumbner :: " + referenceNumber );
			}
			if(BBBUtility.isNotEmpty(referenceNumber)){
				//personalized price will vary depending upon the code associated with SKU
				// if code is CR , then exim price is the final price
				if (BBBUtility.isNotEmpty(personalizationType) && BBBCoreConstants.PERSONALIZATION_CODE_CR.equalsIgnoreCase(personalizationType)) {

					currentPrice = personalizedPrice;
				} else if (BBBUtility.isNotEmpty(personalizationType) && BBBCoreConstants.PERSONALIZATION_CODE_PY.equalsIgnoreCase(personalizationType)) {
						// if code is PY , then add the price coming from exim response
						if (salePrice > 0.0 && onSale) {
							currentPrice = salePrice + personalizedPrice;
						} else {
							currentPrice = listPrice + personalizedPrice;
						}

				} else {
						// if personalization type is PB , then dont use exim personalized price
						if (salePrice > 0.0 && onSale ) {
							currentPrice = salePrice;        		
						} else {
							currentPrice = listPrice;        		
						}        	
					}	
			}else{
				currentPrice = checkCurrentPrice(onSale, listPrice, salePrice);
			}
			if (LOG.isLoggingDebug()) {
			LOG.logDebug("TopStatusChangeMessageDroplet :: checkCurrentPriceForSavedItem () end  currentPrice ::" + currentPrice);
			}
			return round(currentPrice);
		}
		
		
		/**
		 * Checks the current price of the Item.
		 *
		 * @param onSale the on sale
		 * @param listPrice the list price
		 * @param salePrice the sale price
		 * @return the double
		 */
		public static Double checkCurrentPrice(boolean onSale, Double listPrice, Double salePrice) {
			Double currentPrice;
			if (onSale) {
				if ((null != salePrice)) {
					currentPrice = salePrice;
				} else {
					currentPrice = listPrice;
				}
			} else {
				currentPrice = listPrice;
			}
			return currentPrice;
		}
		
		/**
		 * This method return Current Site Id.
		 *
		 * @param pRequest            - DynamoHttpServletRequest
		 * @return siteId
		 */
		public static String getCurrentSiteId(final DynamoHttpServletRequest pRequest) {
		
			String siteId = SiteContextManager.getCurrentSiteId();
			if (siteId == null) {
				siteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);
			}
			return siteId;
			
		}
		
		
		/**
		 * Gets the rkg formatted date.
		 *
		 * @param pDate the date
		 * @param pLocale the locale
		 * @return the rkg formatted date
		 */
		public static Object getRkgFormattedDate(Date pDate, Locale pLocale) {
		
			final SimpleDateFormat format = new SimpleDateFormat( BBBCoreConstants.RKG_DATE_PATTERN, pLocale);
			String rkgFormatedDate = null;
			if (pDate != null) {
				rkgFormatedDate = format.format(pDate);
			} else {
				rkgFormatedDate = format.format(new Date());
			}
			return rkgFormatedDate;
			
		}
		
		
		/**
		 * Gets the rkg formatted price.
		 *
		 * @param itemPrice the item price
		 * @return the rkg formatted price
		 */
		public static Object getRkgFormattedPrice(String itemPrice) {
		
			double cents;
			String priceInCents = null;
			String itemPriceRKG = itemPrice;
			itemPriceRKG = itemPriceRKG.replaceAll(BBBCoreConstants.COMMA, BBBCoreConstants.BLANK);
			final double price = Double.parseDouble(itemPriceRKG);
			if (price > 0) {
				cents = price * 100;
		 		priceInCents = String.valueOf((int) Math.round(cents));
			} else {
				priceInCents = String.valueOf((int) price);
			}
			return priceInCents;
			
		}
		
		
		/**
		 * Gets the encoded string.
		 *
		 * @param str the str
		 * @return the encoded string
		 */
		public static Object getEncodedString(String str) {
		
			String encodedString = null;
			try {
				encodedString = java.net.URLEncoder.encode(str, BBBCoreConstants.UTF_8).replace(BBBCoreConstants.PLUS, BBBCoreConstants.PERCENT_TWENTY);
			} catch (UnsupportedEncodingException e) {
				encodedString = str;
			}
			return encodedString;
			
		}
		
		
		/**
		 *  Used for sorting of countries on billing page.
		 *
		 * @param countryMap the country map
		 * @return Sorted map on base of value
		 */
		
		public static Map<String, String> sortByComparator(Map<String, String> countryMap) {
			 
			// Convert Map to List
			List<Map.Entry<String, String>> list = 
				new LinkedList<Map.Entry<String, String>>(countryMap.entrySet());
	 
			// Sort list with comparator, to compare the Map values
			Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
				public int compare(Map.Entry<String, String> o1,
	                                           Map.Entry<String, String> o2) {
					return (o1.getValue()).compareTo(o2.getValue());
				}
			});
	 
			// Convert sorted map back to a Map
			Map<String, String> sortedMap = new LinkedHashMap<String, String>();
			for (Iterator<Map.Entry<String, String>> it = list.iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = it.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}
			return sortedMap;
		}
		
		/**
		 * Formatting price to 2 decimal place.
		 *
		 * @param price the price
		 * @return price
		 */
		public static double formatPriceToTwoDecimal(double price) {
			return new BigDecimal(price).setScale(2,
			        BigDecimal.ROUND_HALF_DOWN).doubleValue();
		}
		
		/**
		 * Send email.
		 *
		 * @param map the map
		 */
		public static void sendEmail(Map<String, String> map) {

			String recipientFrom = map.get("RecipientFrom");
			String recipientTo = map.get("RecipientTo");
			String emailContent = map.get("emailContent");
			String subject = map.get("subject");
			StringBuffer msg = new StringBuffer();
			String host = map.get("smtpHostName");
			Properties properties = System.getProperties();
			properties.setProperty(SMTP_HOST, host);
			Session session = Session.getDefaultInstance(properties);

			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(recipientFrom));
				//message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientTo));
				// Sending email to multiple users
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientTo));
				message.setSubject(subject);
				msg.append(emailContent);
				message.setText(msg.toString());
				message.setContent(msg.toString(), "text/html; charset=utf-8");
				Transport.send(message);

			} catch (MessagingException mex) {
				LOG.logError("BBBUtility.sendEmail: Error occurred While sending the Email", mex);
			}

		}
		
		/**
		 * This method will return true if current site is TBS.
		 *
		 * @param siteId the site id
		 * @return true, if successful
		 */
		public static boolean siteIsTbs(String siteId){
			
			if(null!=siteId && (siteId.equals(TBSConstants.SITE_TBS_BAB_US)|| siteId.equals(TBSConstants.SITE_TBS_BBB) || siteId.equals(TBSConstants.SITE_TBS_BAB_CA))){
				
				return true;
			}
						
			return false;
			
		}
		
		 /**
 		 * This method is used to validate Credit Card Name.
 		 *
 		 * @param inputZip the input zip
 		 * @return true if credit card is name is valid. false otherwise.
 		 */

	    public static String hyphenExcludedZip(final String inputZip) {
	    	if(null != inputZip && inputZip.contains(BBBCoreConstants.HYPHEN)){
		    	String[] customerZipArr = inputZip
						.split(BBBCoreConstants.HYPHEN);
		        return customerZipArr[0];
	    	}
	    	return inputZip;
	    }
	    
	/**
	 * Populate dynamic pricing string.
	 *
	 * @param dynamicPriceVO the dynamic price vo
	 * @param productId the product id
	 */
	public static void populateDynamicPricingVO(
			BBBDynamicPriceVO dynamicPriceVO, final String productId){
		if(dynamicPricingEnabled()){
			if(LOG.isLoggingDebug()){
				LOG.logDebug("Calling dynamicePricing for populateDynamicPricingVO productId"+productId);
			}
			getCatalogTools().getDynamicPriceDetails(dynamicPriceVO, productId);
		}
	}

	/**
	 * Fetch dynamic pricing string details for product
	 * 
	 * @param productId
	 * @return
	 */
	public static BBBDynamicPriceVO populateDynamicProductPricingVO(
			final String productId){
	
		BBBDynamicPriceVO dynamicProductPricingVO = null;

		if(LOG.isLoggingDebug()){
			LOG.logDebug("Calling dynamicePricing for populateDynamicProductPricingVO productId"+productId);
		}
		dynamicProductPricingVO = getCatalogTools().getDynamicProdPriceDescription(productId);
		
		if (dynamicProductPricingVO == null){
			dynamicProductPricingVO = new BBBDynamicPriceVO();
		}
		return dynamicProductPricingVO;
	}

	/**
	 * Evaluate dynamic price eligiblity at run time 
	 * @param productPriceVo
	 */
	public static void evaluateDynamicPriceEligiblity(
			BBBDynamicPriceVO productPriceVo){
		
		getCatalogTools().evaluateDynamicPriceEligiblity( productPriceVo);

	}
	/**
	 * Populate dynamic pricing sku.
	 *
	 * @param dynamicPriceVO the dynamic price vo
	 * @param productId the product id
	 */
	public static BBBDynamicPriceSkuVO populateDynamicSKUPricingVO(final String skuId, final boolean fromCart){
		BBBDynamicPriceSkuVO populateDynamicSKUPricingVO = null;
		if(dynamicPricingEnabled()){
			if(LOG.isLoggingDebug()){
				LOG.logDebug("Calling dynamicePricing for populateDynamicSKUPricingVO skuId"+skuId);
			}
			populateDynamicSKUPricingVO = getCatalogTools().getDynamicPriceSKUVO(skuId, fromCart);
			
		}
		if (populateDynamicSKUPricingVO == null){
			populateDynamicSKUPricingVO = new BBBDynamicPriceSkuVO();
		}
		return populateDynamicSKUPricingVO;
	}

	
	
		
		/**
		 * This method will return true if EDW data is stale.
		 * @param siteId
		 * @return
		 */
	public static boolean isAfterExpDate(Date lastModifiedDate, String ttl) {

		boolean isAfterExpDate = false;

		long diffDays = 0L;
		long timetoLive=0L;
		diffDays = new Date().getTime() - lastModifiedDate.getTime();
		diffDays = diffDays / (1000 * 60 * 60 * 24);
		if(!isEmpty(ttl))
		{
		   timetoLive = Long.parseLong(ttl.trim());
		}
		if (diffDays > timetoLive) {
			isAfterExpDate = true;
		} else {
			isAfterExpDate = false;
		}

		return isAfterExpDate;
	}
	
	public static String getOnlyDigits(String image) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(image);
        String number = matcher.replaceAll("");
        return number;
    }
	
	/**
	 * This method will return a map for values in Akamai header.
	 * @param headerValue
	 * @return
	 */
	public static Map<String, String> getAkamaiHeaderValueMap(String headerValue) {

		HashMap<String, String> map = null;
		if (!isEmpty(headerValue)) {
			
			map = new HashMap<String, String>();
			int index = 0;
			while (index < headerValue.length()) {
				String key = headerValue.substring(index, headerValue.indexOf(BBBCoreConstants.EQUAL, index));
				index = headerValue.indexOf(BBBCoreConstants.EQUAL, index);
				String value = headerValue.substring(index + 1, (-1 != headerValue.indexOf(BBBCoreConstants.COMMA,
						index)) ? headerValue.indexOf(BBBCoreConstants.COMMA, index) : headerValue.length());
				index = (-1 != headerValue.indexOf(BBBCoreConstants.COMMA, index)) ? headerValue.indexOf(
						BBBCoreConstants.COMMA, index) + 1 : headerValue.length() + 1;
				map.put(key, value);
			}

		}

		return map;
	}
	
	/**
	 * This method is used to get GZIP compression enabled key for mobile REST services.
	 * @return
	 */
	public static boolean isGzipCompressionEnabled() {
		boolean compressionEnabled = false;
		List<String> gzipCompression;
		try {
			gzipCompression = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "GZIP_COMPRESSION_ENABLED");
			if (gzipCompression != null && !gzipCompression.isEmpty()) {
				compressionEnabled = Boolean.parseBoolean(gzipCompression.get(0));
			}
		} catch (BBBSystemException | BBBBusinessException e) {
			LOG.logError("BBBUtility.isGzipCompressionEnabled: Error occurred to get the GZIP compression enabled key from BCC.");
		}
		return compressionEnabled;
	}

	/**
	 * This method used to partition a list into multiple lists of specified
	 * batchSize
	 * 
	 * @param list
	 * @param batchSize
	 * @return
	 */
	public static List<List<String>> getBatchList(List<String> list, int batchSize) {
		List<List<String>> parts = new ArrayList<List<String>>();
		final int sizeOfList = list.size();
		for (int counter = 0; counter < sizeOfList; counter += batchSize) {
			parts.add(list.subList(counter, Math.min(sizeOfList, counter + batchSize)));
		}
		return parts;
	}
	
	/** 
	 * This method is used to retrive stack trace from exception
	 * @param exception
	 * @return
	 * @throws IOException
	 */
	public static String getExceptionTrace(Exception exception) {
		final Writer result = new StringWriter();
		PrintWriter printWriter = null;
		String errorMessage = BBBCoreConstants.BLANK;
		try {
			printWriter = new PrintWriter(result);
			exception.printStackTrace(printWriter);
			errorMessage = result.toString();
		} catch (Exception ex) {
			LOG.logError("BBBUtility.Some exception occurred.");
			 
		}finally {
			try {
				printWriter.close();
				result.close();
			} catch (IOException ex) {
				LOG.logError("BBBUtility.IO Exception occurred.");
			}
		}
		return errorMessage;
	}
	/**
	 * return This method is used to get akamai header for robot
	 * @param request
	 * @return
	 */
	public static boolean isRobot(DynamoHttpServletRequest request) {
		String headerValue = (String) request.getHeader(BBBCoreConstants.AKAMAI_BOT_HEADER);
		if(headerValue!=null && headerValue.equalsIgnoreCase("true"))
		{
			if(LOG.isLoggingDebug())
			LOG.logDebug(" In isRoboticUser, Robot is accessing the system");
		return true;
		}
		else{
			if(LOG.isLoggingDebug())
			LOG.logDebug(" In isRoboticUser, User is accessing the system");
			return false;
		}
	}

	/**
	 * Check if dynamic pricing feature is enabled
	 * @return
	 */
	public static boolean dynamicPricingEnabled(){
		/*BBBH-6242 adding ON/OFF Flag for Incart and Dynamic pricing*/
		boolean dynamicPricingEnabled=false;
		 List<String> dynamicPricingEnabledList = null;
		try {
			dynamicPricingEnabledList = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
					 BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY);
		} catch (BBBSystemException | BBBBusinessException e) {
			LOG.logError("Error whil fetching config key for Dynamic Pricing"+e);
		} 
			if(dynamicPricingEnabledList!=null && !dynamicPricingEnabledList.isEmpty()){
				dynamicPricingEnabled = Boolean.parseBoolean(dynamicPricingEnabledList.get(0));
			}
			return dynamicPricingEnabled;

	}
	public static void setCatalogTools(BBBCatalogTools pCatalogTools) {
		catalogTools = pCatalogTools;
	}
	private static BBBCatalogTools getCatalogTools() {
		if(catalogTools == null) {
			catalogTools = (BBBCatalogTools) Nucleus.getGlobalNucleus().resolveName("/com/bbb/commerce/catalog/BBBCatalogTools");
		}
		return catalogTools;
	}
	 public static String trimToNotNull(String pString)
	 {
	      String strResult = pString;
	     if (strResult != null) {
	        strResult = strResult.trim();
	     }else{
	    	 strResult =BBBCoreConstants.BLANK;
	     }
	     return strResult;
	    }
		
	public static String convertToJSON(Object object) throws BBBSystemException, IOException {
	        Gson gson = new GsonBuilder().create();
	        String jsonString = BBBCoreConstants.BLANK;
	        try{
	              jsonString = gson.toJson(object);
	        } catch(JsonSyntaxException e){
	        	throw new BBBSystemException(e.getMessage());
	        } catch(Exception e){
	        	throw new BBBSystemException(e.getMessage());
	        }
	        return jsonString;
	    }
	 
	
	public static String getEnvTypeName(){
		return ((CommonConfiguration) Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.BBB_COMMON_CONFIGURATION)).getEnvTypeName();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> convertObjectToMap(final Object object) {
		Map<String, Object> map = null;
		
		if(object != null) {
			final ObjectMapper oMapper = new ObjectMapper();
	        map = oMapper.convertValue(object, Map.class);
		}
		
        return map;
	}
}
