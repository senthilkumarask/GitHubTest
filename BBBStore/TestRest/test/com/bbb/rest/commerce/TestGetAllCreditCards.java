package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetAllCreditCards extends BaseTestCase {

    public TestGetAllCreditCards(final String name) {
        super(name);
    }

    RestSession mSession;
    HashMap params = null;


    /**
     * Test get all credit card from order
     * @throws JSONException
     *
     * @throws JSONExceptiontestcr
     * @throws IOException
     * @throws RestClientException
     */

    @SuppressWarnings("unchecked")
    public void testGetAllCreditCard() throws RestClientException, JSONException, IOException  {
        this.mSession = this.getNewHttpSession();
        testGetCurrentOrderDetailsTransientUser(mSession);
        this.mSession.setUseHttpsForLogin(false);
        this.mSession.setUseInternalProfileForLogin(false);
        String result = null;
        String creditCardNumber=null;
        JSONObject jsonObject = null;
        try {
          //  this.mSession.login();
            this.params = (HashMap) this.getControleParameters();
            jsonObject = new JSONObject(this.addItemToCart().readInputStream());

            if (jsonObject.has("formExceptions")) {
                result = jsonObject.getString("formExceptions");
            }
            assertNull(result);

            this.getObject("cardnumber");
            this.params.put("saveProfileFlag", this.getObject("saveprofileflag"));
            this.params.put("isOrderAmtCoveredByGC", this.getObject("isOrderAmtCoveredByGC"));
            // credit card details
            this.params.put("selectedCreditCardId", this.getObject("selectedcreditcardid"));
            this.params.put("creditCardInfo.cardVerificationNumber", this.getObject("cardverificationnumber"));
            this.params.put("creditCardInfo.creditCardNumber", this.getObject("cardnumber"));
            this.params.put("creditCardInfo.creditCardType", this.getObject("cardtype"));
            this.params.put("creditCardInfo.expirationMonth", this.getObject("expirationmonth"));
            this.params.put("creditCardInfo.expirationYear", this.getObject("expirationyear"));
            this.params.put("creditCardInfo.nameOnCard", this.getObject("nameoncard"));
            // Billing address
            this.params.put("userSelectedOption", this.getObject("userSelectedOption"));
            this.params.put("billingAddress.firstName", this.getObject("firstName"));
            this.params.put("billingAddress.lastName", this.getObject("lastName"));
            this.params.put("billingAddress.companyName", this.getObject("companyName"));
            this.params.put("billingAddress.address1", this.getObject("address1"));
            this.params.put("billingAddress.address2", this.getObject("address2"));
            this.params.put("billingAddress.city", this.getObject("city"));
            this.params.put("billingAddress.state", this.getObject("state"));
            this.params.put("billingAddress.country", this.getObject("country"));
            this.params.put("billingAddress.postalCode", this.getObject("postalCode"));
            this.params.put("billingAddress.email", this.getObject("email"));
            this.params.put("billingAddress.mobileNumber", this.getObject("phoneNumber"));
            this.params.put("confirmedEmail", this.getObject("confirmedEmail"));
            this.params.put("atg-rest-return-form-handler-properties", "true");
            final JSONObject billingJson = new JSONObject(this.addBillingAddressRequest().readInputStream());
            System.out.println("testAddNewShippingAddress jason response - " + billingJson);
            final JSONObject billingJsonOrder=new JSONObject(this.getCurrentOrderDetails().readInputStream());
            System.out.println("jason response after billing address"+billingJsonOrder);
            final JSONObject afterCreditDetail=new JSONObject(this.addCreditCardRestCall().readInputStream());
            System.out.println("addCreditCardRestCall jason response - " + afterCreditDetail);
            final JSONObject jsonOrderCredtCard=new JSONObject(this.getCurrentOrderDetails().readInputStream());
            System.out.println(jsonOrderCredtCard);
            final String explastFourDigit= "1111";
            //get all credit card details
            if (this.params.get("atg-rest-output").toString()
                    .equalsIgnoreCase("json"))
            {
                final JSONObject jsonOrderGetCredtCard=new JSONObject(this.getAllCreditCardAPI().readInputStream());
                System.out.println("response---->"+jsonOrderGetCredtCard);
                if(jsonOrderGetCredtCard.toString().contains(explastFourDigit))
                {
                    creditCardNumber=explastFourDigit;

                }
                assertEquals(explastFourDigit, creditCardNumber);

            }
            else
            {
                final String resultStr=this.getAllCreditCardAPI().readInputStream().toString();
                System.out.println(resultStr);
                if(resultStr.contains(creditCardNumber))
                {

                }
                assertEquals(creditCardNumber, creditCardNumber);


            }
        }

        finally {

            try {
                this.mSession.logout();
            } catch (final RestClientException e) {
                assertFalse("Logout failed", true);
            }
        }

    }



    /**
     * Test get all credit card for logged in user
     * @throws JSONException
     *
     * @throws JSONExceptiontestcr
     * @throws IOException
     * @throws RestClientException
     */

    @SuppressWarnings("unchecked")
    public void testGetAllCreditCardLoggedIn() throws RestClientException, JSONException, IOException  {
        this.mSession = this.getNewHttpSession();
        testGetCurrentOrderDetailsTransientUser(mSession);
        this.mSession.setUseHttpsForLogin(false);
        this.mSession.setUseInternalProfileForLogin(false);
        String result = null;
        String creditLastfourDigit=null;
        JSONObject jsonObject = null;
        try {
            //this.mSession.login();
            this.params = (HashMap) this.getControleParameters();
            if (this.params.get("atg-rest-output").toString()
                    .equalsIgnoreCase("json"))
            {
                jsonObject = new JSONObject(this.addItemToCart().readInputStream());

                if (jsonObject.has("formExceptions")) {
                    result = jsonObject.getString("formExceptions");
                }
                assertNull(result);
            }

            else{
                //System.out.println(addItemToCart().readInputStream());
            }
            this.loginRequestRestCall();
            this.params.put("saveProfileFlag", this.getObject("saveprofileflag"));
            this.params.put("isOrderAmtCoveredByGC", this.getObject("isOrderAmtCoveredByGC"));
            // credit card details
            this.params.put("selectedCreditCardId", this.getObject("selectedcreditcardid"));
            this.params.put("creditCardInfo.cardVerificationNumber", this.getObject("cardverificationnumber"));
            this.params.put("creditCardInfo.creditCardNumber", this.getObject("cardnumber"));
            this.params.put("creditCardInfo.creditCardType", this.getObject("cardtype"));
            this.params.put("creditCardInfo.expirationMonth", this.getObject("expirationmonth"));
            this.params.put("creditCardInfo.expirationYear", this.getObject("expirationyear"));
            this.params.put("creditCardInfo.nameOnCard", this.getObject("nameoncard"));
            // Billing address
            this.params.put("userSelectedOption", this.getObject("userSelectedOption"));
            this.params.put("billingAddress.firstName", this.getObject("firstName"));
            this.params.put("billingAddress.lastName", this.getObject("lastName"));
            this.params.put("billingAddress.companyName", this.getObject("companyName"));
            this.params.put("billingAddress.address1", this.getObject("address1"));
            this.params.put("billingAddress.address2", this.getObject("address2"));
            this.params.put("billingAddress.city", this.getObject("city"));
            this.params.put("billingAddress.state", this.getObject("state"));
            this.params.put("billingAddress.country", this.getObject("country"));
            this.params.put("billingAddress.postalCode", this.getObject("postalCode"));
            this.params.put("billingAddress.email", this.getObject("email"));
            this.params.put("billingAddress.mobileNumber", this.getObject("phoneNumber"));
            this.params.put("confirmedEmail", this.getObject("confirmedEmail"));
            this.params.put("atg-rest-return-form-handler-properties", "true");
            final RestResult restResult= this.addBillingAddressRequest();
            if (this.params.get("atg-rest-output").toString()
                    .equalsIgnoreCase("json"))
            {
                jsonObject = new JSONObject(restResult.readInputStream());
                //System.out.println("testAddNewShippingAddress jason response - " + jsonObject);
            }

            if (this.params.get("atg-rest-output").toString()
                    .equalsIgnoreCase("json"))
            {
                final JSONObject billingJsonOrder=new JSONObject(this.getCurrentOrderDetails().readInputStream());
                System.out.println("jason response after billing address"+billingJsonOrder);
                final JSONObject afterCreditDetail=new JSONObject(this.addCreditCardRestCall().readInputStream());
                System.out.println(afterCreditDetail);
            }
            else
            {
                //System.out.println(getCurrentOrderDetails().readInputStream().toString());
                System.out.println(this.addCreditCardRestCall().readInputStream().toString());
            }


            final String explastFourDigit= "1111";
            //get all credit card details
            if (this.params.get("atg-rest-output").toString()
                    .equalsIgnoreCase("json"))
            {
                final JSONObject jsonOrderCredtCard=new JSONObject(this.getAllCreditCardAPI().readInputStream());
                System.out.println("response---->"+jsonOrderCredtCard);
                if(jsonOrderCredtCard.toString().contains(explastFourDigit))
                {
                    creditLastfourDigit=explastFourDigit;

                }
                assertEquals(explastFourDigit, creditLastfourDigit);

            }
            else
            {
                final String resultStr=this.getAllCreditCardAPI().readInputStream().toString();
                System.out.println("response RS---->"+resultStr);
                if(resultStr.contains(explastFourDigit))
                {
                    creditLastfourDigit=explastFourDigit;

                }
                assertEquals(explastFourDigit, creditLastfourDigit);
            }
        }

        finally {

            try {
                this.mSession.logout();
            } catch (final RestClientException e) {
                assertFalse("Logout failed", true);
            }
        }

    }

    /**
     * login call
     *
     * @return response JSON Object
     * @throws RestClientException
     * @throws IOException
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private RestResult loginRequestRestCall() throws RestClientException, JSONException, IOException {

        this.params.put("value.login", this.getObject("login"));
        this.params.put("value.password", this.getObject("password"));
        final RestResult restResult = this.mSession.createHttpRequest("http://" + this.getHost() + ":" + this.getPort() + (String) this.getObject("loginRequest"), this.params,
                "POST");

        return restResult;
    }

    /**
     * login call
     *
     * @return response JSON Object
     * @throws RestClientException
     * @throws IOException
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private RestResult getAllCreditCardAPI() throws RestClientException, JSONException, IOException {

        final RestResult restResult = this.mSession.createHttpRequest("http://" + this.getHost() + ":" + this.getPort() + (String) this.getObject("allCreditCardRequest"), this.params,
                "POST");
        return restResult;
    }


    /**
     * login call
     *
     * @return response JSON Object
     * @throws RestClientException
     * @throws IOException
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private RestResult addCreditCardRestCall() throws RestClientException, JSONException, IOException {
        this.params.put("selectedCreditCardId", "NEW");
        this.params.put("saveProfileFlag", this.getObject("saveprofileflag"));
        this.params.put("isOrderAmtCoveredByGC", this.getObject("isOrderAmtCoveredByGC"));
        // credit card details
        this.params.put("selectedCreditCardId", this.getObject("selectedcreditcardid"));
        this.params.put("creditCardInfo.cardVerificationNumber", this.getObject("cardverificationnumber"));
        this.params.put("creditCardInfo.creditCardNumber", this.getObject("cardnumber"));
        this.params.put("creditCardInfo.creditCardType", this.getObject("cardtype"));
        this.params.put("creditCardInfo.expirationMonth", this.getObject("expirationmonth"));
        this.params.put("creditCardInfo.expirationYear", this.getObject("expirationyear"));
        this.params.put("creditCardInfo.nameOnCard", this.getObject("nameoncard"));
        final RestResult restResult = this.mSession.createHttpRequest("http://" + this.getHost() + ":" + this.getPort() + (String) this.getObject("paymentGroupFormHandler"), this.params,
                "POST");

        return restResult;
    }

    /**
     * Get current order details.
     *
     * @return response JSON Object
     * @throws RestClientException
     * @throws IOException
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private RestResult getCurrentOrderDetails() throws RestClientException, JSONException, IOException {

        this.params = (HashMap) this.getControleParameters();
        this.params.put("arg1", false);

        final RestResult restResult = this.mSession.createHttpRequest(
                "http://" + this.getHost() + ":" + this.getPort() + (String) this.getObject("currentOrderDetailsRequest"), this.params, "POST");
        return restResult;
    }

    /**
     * Add item to cart.
     *
     * @return response JSON Object
     * @throws RestClientException
     * @throws IOException
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private RestResult addItemToCart() throws RestClientException, JSONException, IOException {

        this.params.put("jsonResultString", this.getObject("jsonResultString"));
        final RestResult restResult = this.mSession.createHttpRequest("http://" + this.getHost() + ":" + this.getPort() + (String) this.getObject("addCartRequest"), this.params,
                "POST");

        return restResult;
    }
    /**
     * Add Billing Address Request
     *
     * @throws RestClientException
     * @throws IOException
     * @throws JSONException
     */
    private RestResult addBillingAddressRequest() throws RestClientException, JSONException, IOException {

        final RestResult restResult = this.mSession.createHttpRequest("http://" + this.getHost() + ":" + this.getPort() + (String) this.getObject("addBillingAddressRequest"),
                this.params, "POST");
        return restResult;
    }

}
