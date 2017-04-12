package com.bbb.commerce.order.paypal;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.ShippingGroup;
import atg.core.util.Address;
import atg.multisite.SiteContextManager;
import atg.servlet.ServletUtil;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.commerce.vo.PayPalInputVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.paypal.BBBAddressPPVO;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;
import com.bbb.paypal.vo.PayPalAddressVerifyVO;
import com.bbb.utils.BBBUtility;
import com.bbb.commerce.common.BBBAddress;

/**
 * This Class verifies shipping address returned from PayPal if shipping group is not present in order.
 * Otherwise validate shipping address stored in order
 * 
 * @author aban13
 *
 */
public class PayPalAddressVerification extends BBBGenericService {

	private BBBCatalogTools catalogTools;
	private BBBPurchaseProcessHelper shippingHelper;
	private BBBCheckoutManager checkoutManager;
	private Properties shippingMethodMap;
	@SuppressWarnings("rawtypes")
	private List states;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private Map<String,String> addressVerifyRedirectUrl;
	private BBBPayPalServiceManager paypalServiceManager;

    public BBBPayPalServiceManager getPaypalServiceManager() {
		return this.paypalServiceManager;
	}

	public void setPaypalServiceManager(BBBPayPalServiceManager paypalServiceManager) {
		this.paypalServiceManager = paypalServiceManager;
	}
	
	/**
	 * @return addressVerifyRedirectUrl
	 */
	public Map<String, String> getAddressVerifyRedirectUrl() {
		return this.addressVerifyRedirectUrl;
	}

	/**
	 * @param addressVerifyRedirectUrl to set addressVerifyRedirectUrl
	 */
	public void setAddressVerifyRedirectUrl(
			Map<String, String> addressVerifyRedirectUrl) {
		this.addressVerifyRedirectUrl = addressVerifyRedirectUrl;
	}


	/**
	 * @return lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.lblTxtTemplateManager;
	}


	/**
	 * @param lblTxtTemplateManager to set lblTxtTemplateManager
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}


	/**
	 * @return states
	 */
	@SuppressWarnings("rawtypes")
	public List getStates() {
		return this.states;
	}


	/**
	 * @param states to set states
	 */
	@SuppressWarnings("rawtypes")
	public void setStates(List states) {
		this.states = states;
	}


	/**
	 * @return shippingMethodMap
	 */
	public Properties getShippingMethodMap() {
		return this.shippingMethodMap;
	}


	/**
	 * @param shippingMethodMap to shippingMethodMap - contains shipping methods and its values
	 */
	public void setShippingMethodMap(Properties shippingMethodMap) {
		this.shippingMethodMap = shippingMethodMap;
	}


	/**
	 * @return checkoutManager
	 */
	public BBBCheckoutManager getCheckoutManager() {
		return this.checkoutManager;
	}


	/**
	 * @param checkoutManager to set checkoutManager
	 */
	public void setCheckoutManager(BBBCheckoutManager checkoutManager) {
		this.checkoutManager = checkoutManager;
	}


	/**
	 * @return shippingHelper
	 */
	public BBBPurchaseProcessHelper getShippingHelper() {
		return this.shippingHelper;
	}


	/**
	 * @param shippingHelper to set shippingHelper
	 */
	public void setShippingHelper(BBBPurchaseProcessHelper shippingHelper) {
		this.shippingHelper = shippingHelper;
	}


	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}


	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * Validating Shipping address from paypal if shipping groups are empty in order.
	 * In Following Sequence:
	 * 1. International shipping address
	 * 2. PO Box Validation
	 * 3. Shipping Restriction
	 * 4. Shipping Method Validation
	 * 5. Coupon check
	 * 
	 * @param voResp
	 * @param order
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public boolean validatePayPalShippingAddress(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrderImpl order, PayPalAddressVerifyVO addressVerifyVO, PayPalInputVO paypalInput) throws ServletException, IOException{
		
		logDebug("PayPalAddressVerification.verifyPayPalShippingAddress() :: Start");
		BBBAddressPPVO shippingAddress = voResp.getShippingAddress();
		List<String> addressErrorList = new ArrayList<String>();
		boolean success = true;
		try {
			logDebug("Address does not exist in Order. Validating Paypal Address");
			if(shippingAddress != null){
				shippingAddress.setFromPayPal(true);
				addressVerifyVO.setAddress(shippingAddress);
				
				if(!BBBUtility.isNonPOBoxAddress(shippingAddress.getAddress1(),shippingAddress.getAddress2()))
					shippingAddress.setPoBoxAddress(true);
				
				//Verify Shipping Restrictions for PayPal address i.e Verify State and Zip code restriction
				addressErrorList = verifyShippingRestriction(order, shippingAddress, addressErrorList);
				if(!BBBUtility.isListEmpty(addressErrorList)){
					logError("Shipping Restriction Present in Paypal Address");
					success = false;
					addressVerifyVO.setRedirectUrl(this.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.SHIPPING));
					addressVerifyVO.setRedirectState(BBBPayPalConstants.SHIPPING_SINGLE);
				}
				else if(!this.getPaypalServiceManager().addressInOrder(order)){
					//Create Shipping Group after all the restriction check
					logDebug("No restrictions present in PayPal Shipping Address, so populating paypal shipping address in shipping group");
					this.getPaypalServiceManager().createShippingGroupPP(voResp, order, paypalInput);
				}
			}
			else{
				logError("No paypal shipping address present");
				success = false;
				addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null));
				addressVerifyVO.setRedirectUrl(this.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.SHIPPING));
				addressVerifyVO.setRedirectState(BBBPayPalConstants.SHIPPING_SINGLE);
			}
			
			//Populate VO to return to droplet
			addressVerifyVO.setInternationalOrPOError(false);
			addressVerifyVO.setSuccess(success);
			addressVerifyVO.setAddressErrorList(addressErrorList);
			
		} catch (BBBBusinessException e) {
			logError("Business Exception while validating shipping address " + e);
		} catch (BBBSystemException e) {
			logError("System Exception while validating shipping address " + e);
		}
		
		logDebug("PayPalAddressVerification.verifyPayPalShippingAddress() :: End");
		return success;
	}
	
	
	/**
	 * This Method is added as Part of Story 83-N. Verifies Address returned from Paypal.Checks
	 * 1. International Address
	 * 2. Missing or Invalid Property
	 * 3. PO Box Address
	 * 
	 * 
	 * @param voResp
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	public boolean validateInternationalAndPOAddress(BBBGetExpressCheckoutDetailsResVO voResp, PayPalAddressVerifyVO addressVerifyVO){
		
		logDebug("BBBPayPalServiceManager.validateInternationalAndPOAddress() :: Start");
		
		boolean success = true;
		boolean isInternationalOrPOError = false;
		boolean isInternationalAddress = false;
		
		//Setting "Shipping" page as default for redirection
		String redirectURL = BBBPayPalConstants.SHIPPING;	
		
		List<String> addressErrorList = new ArrayList<String>();
		BBBAddressPPVO shippingAddress = voResp.getShippingAddress();
		final String siteId = getSiteId();
		if(shippingAddress != null){
			
			shippingAddress.setFromPayPal(true);
			String countryCode = shippingAddress.getCountry();
			logDebug("Checking International shipping Address Validation with country code : " + countryCode);
			
			//Check If PayPal address is International address. Add Error if Yes. 
			if(BBBUtility.isNotEmpty(countryCode) && BBBUtility.isNotEmpty(siteId) && 
					!(((siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BBB) || siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US) ||  siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_US)) 
					&& countryCode.equalsIgnoreCase(BBBPayPalConstants.US_COUNTRY_CODE)) || ((siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)  || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA))
							&& countryCode.equalsIgnoreCase(BBBPayPalConstants.CA_COUNTRY_CODE)))){
				
				logError("International shipping Address present in paypal: " + countryCode);
				success = false;
				isInternationalOrPOError = true;
				
				//Added as part of Paypal Redirect to Cart Page in case of international address error (not for CANADA concept) start
				if( !(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA))){
					isInternationalAddress = true;
					redirectURL = BBBPayPalConstants.CART;
					addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_INTERNATIONAL_PP_ADDRESS_CART, "EN", null));
				}
				else{
					addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_INTERNATIONAL_PP_ADDRESS, "EN", null));
				}
				//Added as part of Paypal Redirect to Cart Page in case of international address error end
			}
			if(success){
				
				//Added as part of R2.2.1 Story - Canada Site - Redirects to shipping page when User selects Quebec as shipping address on paypal - Start
				logDebug("If Country code is Canada then check if Province is Quebec or not");
				if(BBBUtility.isNotEmpty(countryCode) && BBBUtility.isNotEmpty(siteId) && (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)||siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA))  
						&& countryCode.equalsIgnoreCase(BBBPayPalConstants.CA_COUNTRY_CODE) && BBBUtility.isNotEmpty(shippingAddress.getState())){
					if(shippingAddress.getState().equalsIgnoreCase(BBBPayPalConstants.QUEBEC)){
						logError("Quebec province is present in canada address, Country Code: " + countryCode + " Province: " + shippingAddress.getState());
						success = false;
						isInternationalOrPOError = true; //This determines if we have to display only first and last name or not. If true display only first and Last Name
						addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_QUEBEC_PP_SHIPPING_ADDRESS, "EN", null));
					}
				}
				//Added as part of R2.2.1 Story - Canada Site - Redirects to shipping page when User selects Quebec as shipping address on paypal - End
				
				if(success){
					logDebug("Checking If any required property is not missing and not valid");
					
					//Check if all the required fields are not missed and are valid
					final List<List<String>> errorList = this.getShippingHelper().checkForRequiredAddressProperties(shippingAddress, ServletUtil.getCurrentRequest());
					if(errorList != null && ((!BBBUtility.isListEmpty(errorList.get(0))) || (!BBBUtility.isListEmpty(errorList.get(1))))){
						success = false;
						isInternationalOrPOError = false;
						this.addAddressValidationError(errorList, addressErrorList);
					}
					else{
						logDebug("Checking If address is PO Box address or not");
						//ship to po box changes
						List<String> shiptoPOBoxOn;
						boolean shiptoPOFlag =false;
						try {
							shiptoPOBoxOn = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
							shiptoPOFlag = Boolean.parseBoolean(shiptoPOBoxOn.get(0));
							//Check If PayPal address is PO Box address. Add Error if Yes
							if(!shiptoPOFlag && !BBBUtility.isNonPOBoxAddress(shippingAddress.getAddress1(),shippingAddress.getAddress2())){
								logError("PO Box shipping Address present in paypal: " + shippingAddress.getAddress1());
								success = false;
								isInternationalOrPOError = true;
								addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PO_BOX_PP_ADDRESS, "EN", null));
							}					
						} catch (BBBSystemException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							logError(e.getMessage(),e);
						} catch (BBBBusinessException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							logError(e.getMessage(),e);
						}
					}
				}
			}
		}
		else {
			success = false;
			isInternationalOrPOError = false;
			addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null));
			logError("Shipping Address is null in Response recieved from PayPal. Redirect to Shipping Page");
		}
		
		//Set Redirect Url and redirect state to shipping page if there is any error while validating shipping address
		//or if user selects Edit on qas modal.
		//Populate VO to return to droplet
		addressVerifyVO.setInternationalOrPOError(isInternationalOrPOError);
		addressVerifyVO.setInternationalError(isInternationalAddress);
		addressVerifyVO.setSuccess(success);
		addressVerifyVO.setAddressErrorList(addressErrorList);
		addressVerifyVO.setRedirectUrl(this.getAddressVerifyRedirectUrl().get(redirectURL));
		addressVerifyVO.setRedirectState(BBBPayPalConstants.SHIPPING_SINGLE);
		addressVerifyVO.setAddress(shippingAddress);
		logDebug("BBBPayPalServiceManager.validateInternationalAndPOAddress() :: End | Returns VO: " + addressVerifyVO.toString());
		
		return success;
	}
	
	protected String getSiteId(){
		String siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}
	/** 
	 *Method to add Error Properties to Error Map
	 *
	 * @param pErrorProperties - missing and invalid properties list
	 *  */
	@SuppressWarnings("rawtypes")
	private final void addAddressValidationError(final List pErrorProperties, List<String> addressErrorList) {
		
		logDebug("BBBPayPalServiceManager.addAddressValidationError() :: Start");
		final List pMissingProperties = (List) pErrorProperties.get(0);
		if (!BBBUtility.isListEmpty(pMissingProperties)) {
			final Map addressPropertyNameMap = this.getShippingHelper().getAddressPropertyNameMap();
			final Iterator properator = pMissingProperties.iterator();
			while (properator.hasNext()) {
				final String property = (String) properator.next();
				logError("Address validation error with: " + addressPropertyNameMap.get(property) + " property.");
				addressErrorList.add((String)addressPropertyNameMap.get(property) + " is Missing");
			}
		}
		final List pInvalidProperties = (List) pErrorProperties.get(1);
		if (!BBBUtility.isListEmpty(pInvalidProperties)) {
			final Map addressPropertyNameMap = this.getShippingHelper().getAddressPropertyNameMap();
			final Iterator properator = pInvalidProperties.iterator();
			while (properator.hasNext()) {
				final String property = (String) properator.next();
				logError("Address validation error with: " + addressPropertyNameMap.get(property) + " property.");
				addressErrorList.add((String)addressPropertyNameMap.get(property) + " is Invalid");
			}
		}
		logDebug("BBBPayPalServiceManager.addAddressValidationError() :: End");
	}
	
	/**
	 * Verifies Shipping Restrictions on PayPal address. Checks Following 
	 * 1. State Restriction
	 * 2. Zip Code Restriction
	 * 
	 * @param order
	 * @param address
	 * @param addressErrorList
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public List<String> verifyShippingRestriction(BBBOrderImpl order, BBBAddressPPVO address, List<String> addressErrorList) throws BBBBusinessException, BBBSystemException{
		
		this.logDebug("PayPalAddressVerification.verifyShippingRestriction() :: Start");
		if (!this.getCheckoutManager().canItemShipToAddress(order.getSiteId(),
				order.getCommerceItems(), address)) {
			if (!address.getIsNonPOBoxAddress()) {
			addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_STATE_INCORRECT_PP_ADDRESS, "EN", null));
			this.logError("PayPalAddressVerification.verifyShippingRestriction() :: State Restriction Exists, Error List" + addressErrorList);
			}else{
				addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_POBOX_INCORRECT_PP_ADDRESS, "EN", null));
				this.logError("PayPalAddressVerification.verifyShippingRestriction() :: POBox Restriction Exists, Error List" + addressErrorList);
		}
		}
		else{
			List<CommerceItem> commerceItemLists = order.getCommerceItems();
			for (CommerceItem commerceItem : commerceItemLists) {
				if (commerceItem instanceof BBBCommerceItem) {
					BBBCommerceItem bbbItem = (BBBCommerceItem) commerceItem;

					if(this.getCatalogTools().isShippingZipCodeRestrictedForSku(bbbItem.getCatalogRefId(),
							order.getSiteId(), address.getPostalCode())){
						addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS, "EN", null));
						this.logError("PayPalAddressVerification.verifyShippingRestriction() :: Zip Code Restriction Exists, Error List" + addressErrorList + "for commerce Item: " + bbbItem.getCatalogRefId());
						break;
					}
				}
			}
		}
		
		this.logDebug("PayPalAddressVerification.verifyShippingRestriction() :: End");
		return addressErrorList;
	}
	
	/**
	 * This method to populate PayPal Address VO from OOB ATG component
	 * 
	 * @param address 
	 * @return BBBAddressPPVO
	 */
	private static BBBAddressPPVO populatePayPalAddressVO(Address address){
		BBBAddressPPVO payPalAddressVO = new BBBAddressPPVO();
		payPalAddressVO.setAddress1(address.getAddress1());
		payPalAddressVO.setAddress2(address.getAddress2());
		payPalAddressVO.setCity(address.getCity());
		payPalAddressVO.setFirstName(address.getFirstName());
		payPalAddressVO.setLastName(address.getLastName());
		payPalAddressVO.setPostalCode(address.getPostalCode());
		payPalAddressVO.setState(address.getState());
		return payPalAddressVO;
	}
	
	
	/**
	 * This method verifies address in order. Because cart items may be modified before click on paypal
	 * Check-
	 * Shipping Restrictions - Zip code and State 
	 * Shipping method
	 * 
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean validateShippingAddressInOrder(BBBOrderImpl order, PayPalAddressVerifyVO addressVerifyVO){
		
		this.logDebug("PayPalAddressVerification.verifyAddressInOrder() :: Start");
		
		List<String> addressErrorList = new ArrayList<String>();
		boolean success = true;
		try{
			List<ShippingGroup> shpGrpList = order.getShippingGroups();
			for(ShippingGroup grp : shpGrpList){
				if(grp instanceof BBBHardGoodShippingGroup){
					BBBHardGoodShippingGroup hrd = (BBBHardGoodShippingGroup)grp;
					Address addrHrd = hrd.getShippingAddress();
					BBBAddressPPVO address = populatePayPalAddressVO(addrHrd);
					//address.setPoBoxAddress(((BBBAddress)addrHrd).isPoBoxAddress());
				
					if(!BBBUtility.isNonPOBoxAddress(addrHrd.getAddress1(),addrHrd.getAddress2()))
						address.setPoBoxAddress(true);
					
					if(addrHrd.getCity() == null && addrHrd.getAddress1()==null ){
						continue;
					}
					logDebug("Address exists in Order, so verifying Shipping Restrictions and Shipping Method: Shipping Group ID: " + hrd.getId());
					List<CommerceItemRelationship> commerceItemRelList = hrd.getCommerceItemRelationships();
					for(CommerceItemRelationship item : commerceItemRelList){
						if(this.getCheckoutManager().checkItemShipToAddress(order.getSiteId(), item.getCommerceItem().getCatalogRefId(), address)){
							if (!address.isPoBoxAddress()) {
								this.logError("PayPalAddressVerification.verifyAddressInOrder() :: State Restrictions exist in order, redirecting user to shipping page for commerce Item: " + item.getCommerceItem().getCatalogRefId());
								addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_STATE_INCORRECT_PP_ADDRESS, "EN", null));
							}else{
								this.logError("PayPalAddressVerification.verifyAddressInOrder() :: PO BOX Restrictions exist in order, redirecting user to shipping page for commerce Item: " + item.getCommerceItem().getCatalogRefId());
								addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_POBOX_INCORRECT_PP_ADDRESS, "EN", null));
							}
								
							success = false;
							addressVerifyVO.setRedirectUrl(getAddressVerifyRedirectUrl().get(BBBPayPalConstants.SHIPPING));
							addressVerifyVO.setRedirectState(BBBPayPalConstants.SHIPPING_SINGLE);
							break;
						} else if(this.getCatalogTools().isShippingZipCodeRestrictedForSku(item.getCommerceItem().getCatalogRefId(),
								order.getSiteId(), address.getPostalCode())){
							success = false;
							addressVerifyVO.setRedirectUrl(getAddressVerifyRedirectUrl().get(BBBPayPalConstants.SHIPPING) + BBBPayPalConstants.SHIPPING_RESTRICTION);
							addressVerifyVO.setRedirectState(BBBPayPalConstants.SHIPPING_SINGLE);
							this.logError("PayPalAddressVerification.verifyAddressInOrder() :: Zip code Restrictions exist in order, redirecting user to shipping page for commerce Item: " + item.getCommerceItem().getCatalogRefId());
							addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS, "EN", null));
							break;
						}
					}
					if(!success){
						addressVerifyVO.setAddress(address);
					}
				}
			}
			//Populate VO to return to droplet
			addressVerifyVO.setInternationalOrPOError(false);
			addressVerifyVO.setSuccess(success);
			addressVerifyVO.setAddressErrorList(addressErrorList);
		}
		catch (BBBBusinessException e) {
			logError("Business Exception while validating shipping address " + e);
		} catch (BBBSystemException e) {
			logError("System Exception while validating shipping address " + e);
		}
		
		this.logDebug("PayPalAddressVerification.verifyAddressInOrder() :: End");
		return success;
	}

}
