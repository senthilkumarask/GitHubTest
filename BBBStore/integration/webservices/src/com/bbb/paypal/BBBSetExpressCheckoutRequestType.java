/**
 * BBBSetExpressCheckoutRequestType.java
 *
 * Project:     BBB
 */

package com.bbb.paypal;

/**
 * @author ssh108
 *
 */

public class BBBSetExpressCheckoutRequestType {
    private BBBSetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails;

    public BBBSetExpressCheckoutRequestType() {
    	//default constructor
    }

        /**
     * Gets the setExpressCheckoutRequestDetails value for this SetExpressCheckoutRequestType.
     * 
     * @return setExpressCheckoutRequestDetails
     */
    public BBBSetExpressCheckoutRequestDetailsType getSetExpressCheckoutRequestDetails() {
        return setExpressCheckoutRequestDetails;
    }


    /**
     * Sets the setExpressCheckoutRequestDetails value for this SetExpressCheckoutRequestType.
     * 
     * @param setExpressCheckoutRequestDetails
     */
    public void setSetExpressCheckoutRequestDetails(BBBSetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails) {
        this.setExpressCheckoutRequestDetails = setExpressCheckoutRequestDetails;
    }

}
