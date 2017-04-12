package com.bbb.commerce.order.purchase.pipeline;

import java.util.List;
import java.util.Map;

import com.bbb.account.BBBAddressTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBPopulateStatesDroplet;
import com.bbb.common.BBBGenericService;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

import atg.commerce.order.PipelineConstants;
import atg.multisite.SiteContextManager;
import atg.nucleus.ServiceException;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

/**
 * 
 * This processor checks if order is a Paypal order.
 * Checks for all mandatory fields of billing address. 
 * Checks if Country Code is valid. If country is US/Canada, then check if the state is valid or not.
 * If the validation fails at any stage, replace billing address with shipping address. 
 * 
 *
 */
public class ProcOverrideInvalidPaypalBillingAddress extends BBBGenericService implements PipelineProcessor {
	
	private static final int SUCCESS = 1;
	
	public static final String PAYPAL_OVERRIDE = "Paypal Override";
	
	private BBBCatalogToolsImpl bbbCatalogTools;
	
	private BBBCheckoutManager bbbCheckoutManager;
	
	public BBBCheckoutManager getBbbCheckoutManager() {
		return bbbCheckoutManager;
	}

	public void setBbbCheckoutManager(BBBCheckoutManager bbbCheckoutManager) {
		this.bbbCheckoutManager = bbbCheckoutManager;
	}

	public BBBCatalogToolsImpl getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBCatalogToolsImpl bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
	}

	@Override
	public int[] getRetCodes() {
		return new int[]{SUCCESS};
	}

	@Override @SuppressWarnings("unchecked")
	public int runProcess(Object obj, PipelineResult pipelineresult) throws Exception {
		logDebug("ProcOverrideInvalidPaypalBillingAddress.runProcess method starts.");
		try{
			Map<String, Object> requestMap = (Map<String, Object>) obj;
			BBBOrderImpl order = (BBBOrderImpl) requestMap.get(PipelineConstants.ORDER);
			if(order.isPayPalOrder()){
				logDebug("Order is Paypal Order.");
				DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
				DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
				boolean isValidBillingAddress = this.isValidBillingAddress(order, request, response);//isValidBillingAddress==false implies address is invalid & address needs to be replaced.
				if(!isValidBillingAddress){
					logInfo("Invalid billing address detected for paypal order. So, overriding billing address with shipping address.");
					overrideInvalidPaypalBillingAddress(order);
				}
			}
		}
		catch(Exception e){
			logError("Exception occurred in pipeline component ProcOverrideInvalidPaypalBillingAddress",e);
		}
		logDebug("ProcOverrideInvalidPaypalBillingAddress.runProcess method ends.");
		return SUCCESS;
	}
	
	/**
	 * 
	 * Uses validateBillingAddress function to check for basic validation of Fields such as address1, address2, firstName,
	 * lastName, postalCode, state.
	 * 
	 * @param order
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isValidBillingAddress(BBBOrder order, DynamoHttpServletRequest request, DynamoHttpServletResponse response){
		logDebug("ProcOverrideInvalidPaypalBillingAddress.isValidBillingAddress method starts.");
		boolean result = true;//true implies valid address. false implies invalid billing address.
		BBBAddress billingAddress = order.getBillingAddress();
		if (billingAddress!=null) {
			result = this.validateBillingAddressFields(billingAddress);//result should be true to proceed.
			//As generic field validation has passed, check for valid countries & state.
			if(result){
				try {
					Map<String, String> countries = this.getBbbCatalogTools().getCountriesInfo(null);
					if(!countries.containsKey(billingAddress.getCountry())){
						logError("Billing Address Country:-" + billingAddress.getCountry() + " is not a valid BCC Managed Country.");
						result = false;
					} else if(billingAddress.getCountry().equalsIgnoreCase(BBBPopulateStatesDroplet.US_COUNTRY_CODE) 
							|| billingAddress.getCountry().equalsIgnoreCase(BBBPopulateStatesDroplet.CA_COUNTRY_CODE)){
						List<StateVO> states = getBbbCheckoutManager().getStatesBasedOnCountry(billingAddress.getCountry(), SiteContextManager.getCurrentSiteId(), true, null);
						if(states!=null && !states.isEmpty()){
							boolean isValidState=false;
							for(StateVO state : states){
								if(state.getStateCode().equalsIgnoreCase(billingAddress.getState())){
									logDebug("State has been matched :- " + state.getStateCode() +" for the country:-" + billingAddress.getCountry());
									isValidState = true;
									break;
								}
							}
							if(!isValidState)
								logError("Billing Address State " + billingAddress.getState() + " is not a valid BCC Managed State.");
							billingAddress.setCountryName(countries.get(billingAddress.getCountry()));
							result = isValidState;
						}
					}
				} catch (BBBSystemException| BBBBusinessException e ) {
					logError("Error Occurred while fetching countries.", e);
				} catch (Exception e) {
					logError("Error Occurred while validating states and country for paypal order:-" + billingAddress.getCountry(), e);
				} 
			}
			else
				logDebug("Generic Form Validation of Billing Address failed.");
		}
		logDebug("ProcOverrideInvalidPaypalBillingAddress.isValidBillingAddress method ends with result:-" + result);
		return result;
	}
	
	
	/**
	 * 
	 * Copies Shipping Address to Billing Address.
	 * 
	 * @param order
	 */
	private void overrideInvalidPaypalBillingAddress(BBBOrder order) {
		logDebug("ProcOverrideInvalidPaypalBillingAddress.overrideInvalidPaypalBillingAddress method starts.");
		BBBAddressTools.copyBBBAddress(order.getShippingAddress(), order.getBillingAddress());
		order.getBillingAddress().setAddress2(PAYPAL_OVERRIDE);
		logDebug("ProcOverrideInvalidPaypalBillingAddress.overrideInvalidPaypalBillingAddress method ends.");
	}
	
	public boolean validateBillingAddressFields(BBBAddress billingAddress) {
        logDebug("ProcOverrideInvalidPaypalBillingAddress.validateBillingAddressFields() method starts");
        boolean missingFields = false;
        boolean incorrectVal = false;
        StringBuffer errors = new StringBuffer();
        if (!BBBUtility.isValidFirstName(billingAddress.getFirstName())) {
            missingFields = true;
            incorrectVal = true;
            errors.append("Incorrect First Name - " + billingAddress.getFirstName() + ".");
        }
        if (!BBBUtility.isValidLastName(billingAddress.getLastName())) {
            missingFields = true;
            incorrectVal = true;
            errors.append("Incorrect Last Name - " + billingAddress.getLastName() + ".");
        }
        if (!BBBUtility.isValidAddressLine1(billingAddress.getAddress1())){
            missingFields = true;
            incorrectVal = true;
            errors.append("Incorrect Address Line 1 - " + billingAddress.getAddress1() + ".");
        }
        if (!BBBUtility.isEmpty(billingAddress.getAddress2()) && !BBBUtility.isValidAddressLine2(billingAddress.getAddress2())) {
        	incorrectVal = true;
            errors.append("Incorrect Address Line 2 - " + billingAddress.getAddress2() + ".");
        }
        if (!BBBUtility.isValidState(billingAddress.getState())){
            missingFields = true;
            incorrectVal = true;
            errors.append("Incorrect State - " + billingAddress.getState() + ".");
        }
        if (!BBBUtility.isValidCity(billingAddress.getCity())) {
            missingFields = true;
            incorrectVal = true;
            errors.append("Incorrect City - " + billingAddress.getCity() + ".");
        }
        if (!BBBUtility.isValidZip(billingAddress.getPostalCode())) {
            missingFields = true;
            incorrectVal = true;
            errors.append("Incorrect Postal Code - " + billingAddress.getPostalCode() + ".");
        }
        if (!BBBUtility.isEmpty(billingAddress.getCompanyName()) && !BBBUtility.isValidCompanyName(billingAddress.getCompanyName().toString())) {
        	incorrectVal = true;
            errors.append("Incorrect Company name - " + billingAddress.getCompanyName() + ".");
        }
        if (BBBUtility.isEmpty(billingAddress.getCountry())) {
        	missingFields=true;
        	incorrectVal = true;
            errors.append("Incorrect Country - " + billingAddress.getCountry() + ".");
        }
        if(missingFields || incorrectVal)
        	logError("Field Validation Failed for Paypal Billing Address. Failures are :-" + errors);
        this.logDebug("ProcOverrideInvalidPaypalBillingAddress.validateBillingAddressFields() method ends");
        return !(missingFields || incorrectVal);
    }

}
