/**
 * 
 */
package com.bbb.valuelink;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.valuelink.constants.BBBValueLinkConstants;

/** @author vagra4 */
public class ValueLinkGiftCardUtil extends BBBGenericService {

    /** This method checks for blank values
     * 
     * @param pString
     * @param pVLBlankValue
     * @return */
    public final boolean isEmpty(final String pString, final String pVLBlankValue) {

        this.logDebug("Starting method ValueLinkGiftCardUtil.isEmpty");

        boolean flag = false;

        if ((pString == null) || pString.equals(pVLBlankValue)) {
            flag = true;
        }

        this.logDebug("Exiting method ValueLinkGiftCardUtil.isEmpty");

        return flag;
    }

    /** This method returns TimeReversalPayload
     * 
     * @param originalPayload
     * @param pTimeRevCallCode
     * @param originalTransCodeSeparator
     * @return */
    public final String getTimeReversalPayload(final String originalPayload, final String pOrigTransCdSep,
            final String pTimeRevCallCode) {

        this.logDebug("Starting method ValueLinkGiftCardUtil.getTimeReversalPayload");

        String response = null;
        StringBuffer strOne = null;
        if (originalPayload != null) {
            strOne = new StringBuffer(originalPayload);

            final String fieldSep = originalPayload.substring(14, 15);

            final String originalTransCode = originalPayload.substring(17, 21);

            strOne.append(fieldSep).append(pOrigTransCdSep).append(originalTransCode);

            strOne.replace(17, 21, pTimeRevCallCode);
        }

        this.logDebug("Exiting method ValueLinkGiftCardUtil.getTimeReversalPayload");

        if (strOne != null) {
            response = strOne.toString();
        }
        return response;
    }

    /** This method returns unique ID.
     * 
     * @return String */
    public final String generateUniqueClientRef(final String pClientRefVersion) {

        this.logDebug("Starting method ValueLinkGiftCardUtil.generateUniqueClientRef, InputParam::: "
                + pClientRefVersion);

        final Calendar calender = Calendar.getInstance();
        final int index = calender.get(Calendar.HOUR_OF_DAY) + 65;
        int indexJ = calender.get(Calendar.MINUTE) + 65;

        indexJ = ((indexJ < 91) || ((indexJ > 96) && (indexJ < 123))) ? indexJ : indexJ - 6;

        final StringBuilder uniqueStr = new StringBuilder();
        uniqueStr.append((char) index).append((char) indexJ).append(calender.get(Calendar.SECOND))
        .append(calender.get(Calendar.MILLISECOND)).append(pClientRefVersion);

        this.logDebug("Exiting method ValueLinkGiftCardUtil.generateUniqueClientRef, Output::: " + uniqueStr.toString());

        return uniqueStr.toString();
    }

    /** This method takes amount in String format and returns amount string with a format in which last 2 digits are cent always.
     * 
     * @param pAmount
     * @return */
    public final String getDollorCentAmount(final String pAmount) {

        this.logDebug("Starting method ValueLinkGiftCardUtil.getDollorCentAmount, amount: " + pAmount);

        String dollorCentAmount = null;
        final NumberFormat formatter = new DecimalFormat(BBBCheckoutConstants.DOL_CENT_FMT);
        try {
            double amount = new Double(pAmount);
            amount = amount / 100.0;
            dollorCentAmount = formatter.format(amount);
        } catch (final NumberFormatException nfe) {
            this.logError("Error while parsing pAmount. input is value:" + pAmount, nfe);
            dollorCentAmount = pAmount;
        }

        this.logDebug("Exiting method ValueLinkGiftCardUtil.getDollorCentAmount");

        return dollorCentAmount;
    }

    /** This method returns amount in a format which is required by Value link.
     * 
     * @param pAmount
     * @return */
    public final String getAmountInVLFormat(final double pAmount) {
        this.logDebug("Starting method ValueLinkGiftCardUtil.getAmountInVLFormat, amount: " + pAmount);
        BigDecimal bigDec = new BigDecimal(pAmount);
        bigDec = bigDec.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        bigDec.doubleValue();

        final StringBuilder returnedAmount = new StringBuilder();
        final String strAmount = String.valueOf(pAmount);
        returnedAmount.append(strAmount.substring(0, strAmount.indexOf(BBBCheckoutConstants.DOT)));
        returnedAmount.append(strAmount.substring(strAmount.indexOf(BBBCheckoutConstants.DOT) + 1,
                strAmount.length()));

        // If after decimal point there is only one digit then appending one
        // more 0
        if (strAmount.contains(BBBCheckoutConstants.DOT)) {
            final String strOne = strAmount.substring(strAmount.indexOf(BBBCheckoutConstants.DOT) + 1,
                    strAmount.length());
            if ((strOne != null) && (strOne.length() == 1)) {
                returnedAmount.append(BBBCheckoutConstants.STRING_ZERO);
            }
        }

        this.logDebug("Exiting method ValueLinkGiftCardUtil.getAmountInVLFormat, returnedAmount"
                + returnedAmount.toString());

        return returnedAmount.toString();
    }

    /** This method converts a String to Hexa decimal format.
     * 
     * @param str
     * @return */
    public final String stringToHex(final String str) {

        this.logDebug("Starting method ValueLinkGiftCardUtil.stringToHex, str: " + str);

        final StringBuilder strBuilder = new StringBuilder();

        if (str != null) {
            strBuilder.append(BBBValueLinkConstants.HEX_PREFIX);
            final char[] chars = str.toCharArray();
            for (final char c : chars) {
                strBuilder.append(Integer.toHexString(c));
            }
        }

        this.logDebug("Exiting method ValueLinkGiftCardUtil.stringToHex, hexString" + strBuilder.toString());
        return strBuilder.toString();
    }

    /** This method returns an array of String containing first 2 digit in the first element of array.
     * 
     * @param pString
     * @return */
    public final String[] split(final String pString) {

        this.logDebug("[Start] ValueLinkGiftCardUtil.split method, pString: " + pString);

        String[] array = null;

        if ((pString != null) && (pString.length() > 1)) {
            array = new String[2];
            array[0] = pString.substring(0, 2);
            array[1] = pString.substring(2, pString.length());
        }

        if (array != null) {
            this.logDebug("array.length " + array.length);
            if (array.length == 1) {
                this.logDebug("array[0]: " + array[0]);
            }
            if (array.length == 2) {
                this.logDebug("array[0]: " + array[0]);
                this.logDebug("array[1]: " + array[1]);
            }
        }
        this.logDebug("[End] ValueLinkGiftCardUtil.split method");

        return array;
    }

}
