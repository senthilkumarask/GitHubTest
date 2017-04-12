/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: ssh108
 *
 * Created on: 18-Feb-2014
 * --------------------------------------------------------------------------------
 */
package com.bbb.commerce.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.checkout.tools.BBBCheckoutTools;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager;
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.commerce.vo.PayPalInputVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BaseCommerceItemImpl;
import com.bbb.paypal.BBBAddressPPVO;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;
import com.bbb.paypal.vo.PayPalAddressVerifyVO;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * This droplet is used fetch user info from PayPal based on token ID
 * And Validating Shipping Address which is recieved from PayPal
 * 
 * @author sku134
 */

public class PayPalDroplet extends BBBDynamoServlet {
	public static final String ORDERMANAGER ="OrderManager";
	public static final String CATALOGTOOLS ="CatalogTools";
	public static final String ORDERID ="OrderId";
	public static final String ORDERREPOSITORY ="OrderRepository";
	public static final String LOADORDERPRICEINFO ="LoadOrderPriceInfo";
	public static final String LOADTAXPRICEINFO ="LoadTaxPriceInfo";
	public static final String LOADITEMPRICEINFO ="LoadItemPriceInfo";
	public static final String LOADSHIPPINGPRICEINFO ="LoadShippingPriceInfo";
	public static final String LOADORDER ="loadOrder";
	
	public static final String VERSION ="version";
	
	private BBBPayPalServiceManager paypalServiceManager;
	private OrderDetailsManager orderDetailsManager;
	
	private BBBPaymentGroupManager paymentGroupManager;
	private CheckoutProgressStates checkoutState;
    private BBBPayPalSessionBean payPalSessionBean;
    private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBOrder order;
	private BBBCheckoutManager checkoutManager;
	private BBBCheckoutTools checkoutTools;
    private Profile userProfile;
    private BBBAddressContainer shippingContainer;
    private BBBAddressContainer addressContainer;
    private BBBAddressContainer multishippingContainer;
    private BBBSessionBean sessionBean;
    private PorchServiceManager porchServiceManager;
    
   	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	/**
	 * @return the multishippingContainer
	 */
	public BBBAddressContainer getMultishippingContainer() {
		return multishippingContainer;
	}

	/**
	 * @param multishippingContainer the multishippingContainer to set
	 */
	public void setMultishippingContainer(BBBAddressContainer multishippingContainer) {
		this.multishippingContainer = multishippingContainer;
	}

   	public BBBAddressContainer getAddressContainer() {
   		return this.addressContainer;
   	}

   	public void setAddressContainer(BBBAddressContainer addressContainer) {
   		this.addressContainer = addressContainer;
   	}


	public BBBAddressContainer getShippingContainer() {
		return this.shippingContainer;
	}

	public void setShippingContainer(BBBAddressContainer shippingContainer) {
		this.shippingContainer = shippingContainer;
	}

	public Profile getUserProfile() {
		return this.userProfile;
	}

	public void setUserProfile(Profile userProfile) {
		this.userProfile = userProfile;
	}

	public BBBCheckoutTools getCheckoutTools() {
		return this.checkoutTools;
	}

	public void setCheckoutTools(BBBCheckoutTools checkoutTools) {
		this.checkoutTools = checkoutTools;
	}

	public BBBCheckoutManager getCheckoutManager() {
		return this.checkoutManager;
	}

	public void setCheckoutManager(BBBCheckoutManager checkoutManager) {
		this.checkoutManager = checkoutManager;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/**
     * @return checkoutState
     */
    public CheckoutProgressStates getCheckoutState() {
		return this.checkoutState;
	}

	/**
	 * @param checkoutState to set checkoutState
	 */
	public void setCheckoutState(CheckoutProgressStates checkoutState) {
		this.checkoutState = checkoutState;
	}
	/**
     * @return paymentGroupManager
     */
    public BBBPaymentGroupManager getPaymentGroupManager() {
		return this.paymentGroupManager;
	}

	/**
	 * @param paymentGroupManager
	 */
	public void setPaymentGroupManager(BBBPaymentGroupManager paymentGroupManager) {
		this.paymentGroupManager = paymentGroupManager;
	}

	/**
	 * @return the paypalServiceManager
	 */
	public BBBPayPalServiceManager getPaypalServiceManager() {
		return this.paypalServiceManager;
	}

	/**
	 * @param paypalServiceManager
	 *            the paypalServiceManager to set
	 */
	public void setPaypalServiceManager(BBBPayPalServiceManager paypalServiceManager) {
		this.paypalServiceManager = paypalServiceManager;
	}
	/**
	 * @return the payPalSessionBean
	 */
	public BBBPayPalSessionBean getPayPalSessionBean() {
		return this.payPalSessionBean;
	}

	/**
	 * @param payPalSessionBean the payPalSessionBean to set
	 */
	public void setPayPalSessionBean(BBBPayPalSessionBean payPalSessionBean) {
		this.payPalSessionBean = payPalSessionBean;
	}

	public OrderDetailsManager getOrderDetailsManager() {
		return this.orderDetailsManager;
	}

	public void setOrderDetailsManager(OrderDetailsManager orderDetailsManager) {
		this.orderDetailsManager = orderDetailsManager;
	}
	/**
	 * This service method fetch user info from PayPal based on token ID
	 * Invoke AddressVerification to validate shipping Address in following sequence
	 * International
	 * PO Box
	 * If qas is verfied perform following checks in sequence
	 * Shipping Restriction - State and Zip Code
	 * Coupon Check 
	 * 
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException{
		logDebug("PayPalDroplet.service():: Start");
		//checkOrderVersionMisMatchAndRestore(true, (BBBOrderImpl) getOrder());
		BBBOrderImpl order = (BBBOrderImpl) getOrder();
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO();
		getPayPalSessionBean().setPorchServiceErrorMsg(null);
		logDebug("Check if GetExpCheckout Response exists in session");
		BBBGetExpressCheckoutDetailsResVO voResp = getPayPalSessionBean().getGetExpCheckoutResponse();
		//Below code takes response from session VO and will get empty once the session is expired or User calls setExpress service again
		if(voResp == null){
			
			logDebug("GetExpCheckout Response does not exists in session, calling GetExpCheckout webservice");
			logDebug("Validating Token to call GetExpCheckout webservice");
			
			String redirectUrl = this.getCheckoutTools().getPayPalRedirectUrl(this.getCheckoutState());
			String token = pRequest.getParameter(BBBCoreConstants.TOKEN);
			if(StringUtils.isEmpty(token) || BBBCoreConstants.NULL_VALUE.equalsIgnoreCase(token)){
				token = order.getToken();
			}
			if(!getCheckoutManager().validateToken(token, order, this.getUserProfile())){
				pRequest.setParameter(BBBPayPalConstants.PAYPAL_ERROR_URL, redirectUrl);
				pRequest.setParameter(BBBCoreConstants.FROM_CART, Boolean.valueOf(this.getCheckoutTools().isPayPalCallFromCart(this.getCheckoutState().getCurrentLevel())));
				pRequest.serviceParameter(BBBPayPalConstants.OPARAM_SETEXPRESS, pRequest, pResponse);
				logDebug("PayPalDroplet.service():: paypalErrorUrl: " + redirectUrl + "isFromCart" + Boolean.valueOf(this.getCheckoutTools().isPayPalCallFromCart(this.getCheckoutState().getCurrentLevel())));
			}
			try 
			{
    		  voResp = getPaypalServiceManager().getExpCheckoutDetails(token,order,this.getUserProfile(), this.getAddressContainer());
			} catch (CommerceException e) {
				logError("PayPalDroplet.service():: Commerce Exception while calling webservice GetExpressCheckoutDetails: " + e);
				logDebug("PayPalDroplet.service():: Redirect user to " + redirectUrl);
				handlePaypalError(addressVerifyVO,  this.getLblTxtTemplateManager().getErrMsg(e.getMessage(), BBBCoreConstants.DEFAULT_LOCALE, null), redirectUrl, pRequest);
				pRequest.serviceParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
			} catch (BBBSystemException e) {
				logError("PayPalDroplet.service():: System Exception while calling webservice GetExpressCheckoutDetails: " + e);
				logDebug("PayPalDroplet.service():: Redirect user to " + redirectUrl);
				handlePaypalError(addressVerifyVO, this.getLblTxtTemplateManager().getErrMsg(e.getErrorCode(), BBBCoreConstants.DEFAULT_LOCALE, null), redirectUrl, pRequest);
				pRequest.serviceParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
			} catch (BBBBusinessException e) {
				logError("PayPalDroplet.service():: Business Exception while calling webservice GetExpressCheckoutDetails: " + e);
				logDebug("PayPalDroplet.service():: Redirect user to " + redirectUrl);
				handlePaypalError(addressVerifyVO,  this.getLblTxtTemplateManager().getErrMsg(e.getErrorCode(), BBBCoreConstants.DEFAULT_LOCALE, null), redirectUrl, pRequest);
				pRequest.serviceParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
			}
		}
		else{
			logDebug("GetExpCheckout Response exists in session :: " + voResp.toString());
		}
		if (voResp != null && !voResp.getErrorStatus().isErrorExists()) {
			
			String validateAddress = (String)pRequest.getObjectParameter(BBBPayPalConstants.VALIDATE_ADDRESS);
			String validateShippingMethod = pRequest.getParameter(BBBPayPalConstants.VALIDATE_SHIPPING_METHOD);
			PayPalInputVO paypalInput = new PayPalInputVO();
			paypalInput.setAddressCoontainer(this.getShippingContainer());
			paypalInput.setCheckoutState(this.getCheckoutState());
			paypalInput.setPaypalSessionBean(this.getPayPalSessionBean());
			paypalInput.setProfile(this.getUserProfile());
			paypalInput.setMultiShipAddContainer(this.getMultishippingContainer());
			if(this.getSessionBean().isSinglePageCheckout()){
				paypalInput.setSpcSession(true);
			}
			String isfromReviewPorchPage=pRequest.getParameter("isfromReviewPorchPage");
			if(!StringUtils.isBlank(isfromReviewPorchPage) && isfromReviewPorchPage.equalsIgnoreCase("true")){
			boolean hasPorchService=false;
			List<CommerceItem> commerceItemList=(List<CommerceItem>) getOrder().getCommerceItems();
			 for(CommerceItem commerceItem:commerceItemList){
				 
					BaseCommerceItemImpl cItemimpl = (BaseCommerceItemImpl) commerceItem;
		    		if(cItemimpl.isPorchService() ){
		    			
		    			BBBAddressPPVO porchServiceAddress= voResp.getShippingAddress();
						 String commItemZipCode= porchServiceAddress.getPostalCode();
				 		 String[] commItemShippingCode=commItemZipCode.split("-");
			    			RepositoryItem productItem = (RepositoryItem) commerceItem.getAuxiliaryData().getProductRef();
			    			List<String> porchServiceFamilycodes=getPorchServiceManager().getPorchServiceFamilyCodes(productItem.getRepositoryId(),null);
			    			Object responseVO = null;
			    			
			    			if(!StringUtils.isBlank(commItemShippingCode[0]) && !StringUtils.isBlank(porchServiceFamilycodes.get(0))){
	 							try {
	 								responseVO = getPorchServiceManager().invokeValidateZipCodeAPI(commItemShippingCode[0],porchServiceFamilycodes.get(0));
	 							} catch (BBBSystemException e) {
	 								if(isLoggingError()){
	 									logError("Error while invoking validateZipCode porch api "+e,e);
	 								}
	 							} catch (BBBBusinessException e) {
	 								if(isLoggingError()){
	 									logError("Error while invoking validateZipCode porch api "+e,e);
	 								}
	 							} 
	 		    			}
	 		    			 if(null==responseVO){
	 		    				hasPorchService=true;
	 		    				try {
	 		    					cItemimpl.setPorchServiceRef(null);
	 		    					cItemimpl.setPorchService(false);
	 		    				
	 		    				} catch (Exception e) {
	 		    					if(isLoggingError()){ 
	 		    					logDebug(" error while removing proch service ref from commerce item "+e,e);
	 		    					}
	 		    				
	 		    				}
	 		    				String errorMsg = this.getLblTxtTemplateManager().getErrMsg("err_porchServiceNotAvailable", BBBCoreConstants.DEFAULT_LOCALE, null);	 		    				
	 		    				getPayPalSessionBean().setPorchServiceErrorMsg(errorMsg);
	 		    			 }
		    		}
			 }
			 if(hasPorchService){
				 try {
					getPaymentGroupManager().getOrderManager().updateOrder(getOrder());
				} catch (CommerceException e) {
					if(isLoggingError()){
						logError("Error while updating porch service ref in commerceItem "+e,e);
					}
				}
			 }
		}
			 
			if(validateAddress != null && BBBCoreConstants.TRUE.equalsIgnoreCase(validateAddress)){
				logDebug("Validate PayPal Shipping Group");
				addressVerifyVO = getCheckoutTools().validateShipping(voResp, order, addressVerifyVO, paypalInput);
				pRequest.setParameter(BBBPayPalConstants.SUCCESS, Boolean.valueOf(addressVerifyVO.isSuccess()));
				pRequest.setParameter(BBBPayPalConstants.REDIRECT_URL, addressVerifyVO.getRedirectUrl());
				pRequest.setParameter(BBBPayPalConstants.REDIRECT_STATE, addressVerifyVO.getRedirectState());
				pRequest.serviceParameter(BBBPayPalConstants.ADDRESS_OUTPUT, pRequest, pResponse);
			}
			else{
				this.getCheckoutManager().processPayPal(voResp, order, validateShippingMethod, paypalInput);
				if (validateShippingMethod != null) {
					if(!sessionBean.isSinglePageCheckout())
						getCheckoutState().setCurrentLevel(BBBPayPalConstants.REVIEW);
					else{
						getCheckoutState().setCurrentLevel(BBBPayPalConstants.SP_REVIEW);
					}
					}				
			}
			pRequest.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, voResp.getShippingAddress());
			boolean addressExistInOrder = getPaypalServiceManager().addressInOrder(order);
			if (!addressExistInOrder) {
				pRequest.setParameter("addExist", false);
			} else {
				pRequest.setParameter("addExist", true);
			}
		}else if(voResp != null && voResp.getErrorStatus().isErrorExists()){
			String redirectUrl = this.getCheckoutTools().getPayPalRedirectUrl(this.getCheckoutState());
			boolean isFromCart = this.getCheckoutTools().isPayPalCallFromCart(this.getCheckoutState().getCurrentLevel());
			try {
				getPaypalServiceManager().removePayPalPaymentGroup(order, this.getUserProfile());
			} catch (BBBSystemException e) {
				logError("PayPalDroplet.service():: System Exception while removing PayPal Details from Order: " + e);
			}
			pRequest.setParameter(BBBPayPalConstants.PAYPAL_ERROR_URL, redirectUrl);
			pRequest.setParameter(BBBCoreConstants.FROM_CART, Boolean.valueOf(isFromCart));
			pRequest.serviceParameter(BBBPayPalConstants.OPARAM_SETEXPRESS, pRequest, pResponse);
		}
		pRequest.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS_VO,addressVerifyVO);
		pRequest.serviceParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
		logDebug("PayPalDroplet.service():: End");
	}
	

	/**
	 * This Method is called through rest from mobile to verify shipping address validations
	 * 1. International shipping address
	 * 2. PO Box Validation
	 * 3. Shipping Restriction
	 * 4. Shipping Method Validation
	 * 5. Coupon check
	 * 
	 * @param paypalParamMap - Map of request parameters
	 * @return PayPalAddressVerifyVO
	 * @throws BBBSystemException 
	 */
	public PayPalAddressVerifyVO getVerifiedShippingAddress(Map<String,String> paypalParamMap) throws BBBSystemException,BBBBusinessException {
		logDebug("PayPalDroplet.verifyShippingAddress() method starts");
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();
		PayPalAddressVerifyVO addressVerifyVO =null;
		try {
			pRequest.setParameter(BBBCoreConstants.TOKEN, paypalParamMap.get(BBBCoreConstants.TOKEN));
			pRequest.setParameter(BBBPayPalConstants.PAYPAL_SESSION_BEAN, getPayPalSessionBean());
			//Gets Current Order
	        final BBBOrderImpl order = (BBBOrderImpl) getOrder();
	        pRequest.setParameter(BBBCoreConstants.ORDER, order);
	        String error_exist = "";
	      //If Request is coming from Payment Page. Create Payment & Shipping group and Redirect to Preview Page
	        if(null != paypalParamMap.get(BBBPayPalConstants.FROM_PAYMENT_PAGE) && Boolean.valueOf(paypalParamMap.get(BBBPayPalConstants.FROM_PAYMENT_PAGE))){
	        
					pRequest.setParameter(BBBPayPalConstants.VALIDATE_ADDRESS, String.valueOf(false));
					pRequest.setParameter(BBBCoreConstants.CREATE_GROUP,String.valueOf(true));
					service(pRequest, pResponse);
					addressVerifyVO  = (PayPalAddressVerifyVO) pRequest.getObjectParameter(BBBPayPalConstants.PAYPAL_ADDRESS_VO);
					error_exist  = (String) pRequest.getObjectParameter(BBBPayPalConstants.ERROR_EXIST);
					if(BBBUtility.isNotEmpty(error_exist) && error_exist.equalsIgnoreCase(BBBCoreConstants.TRUE)){
						addressVerifyVO.setRedirectState(BBBPayPalConstants.PAYMENT);
					}
				
	        }else{
	        	String validateOrderAdd = paypalParamMap.get(BBBPayPalConstants.VALIDATE_ORDER_ADD);
	        	boolean addressExistInOrder = getPaypalServiceManager().addressInOrder(order);
	        	//If Address exists in order or coming from cart page when token is already present.
		        	if(addressExistInOrder || (validateOrderAdd != null && validateOrderAdd.equalsIgnoreCase(BBBCoreConstants.TRUE))){
		        		
		        		payPalSessionBean.setValidateOrderAddress(true);
		        		pRequest.setParameter(BBBPayPalConstants.VALIDATE_ADDRESS, String.valueOf(true));
						pRequest.setParameter(BBBPayPalConstants.QAS_VERIFIED,String.valueOf(true));
						service(pRequest, pResponse);
						addressVerifyVO  = (PayPalAddressVerifyVO) pRequest.getObjectParameter(BBBPayPalConstants.PAYPAL_ADDRESS_VO);
						error_exist  = (String) pRequest.getObjectParameter(BBBPayPalConstants.ERROR_EXIST);
						if(BBBUtility.isNotEmpty(error_exist) && error_exist.equalsIgnoreCase(BBBCoreConstants.TRUE)){
							addressVerifyVO.setRedirectState(BBBCoreConstants.CART);
						}
						addressVerifyVO.setAddress((BBBAddressPPVO)pRequest.getObjectParameter(BBBPayPalConstants.PAYPAL_ADDRESS));
						//R2.2 - Start Story AK
			        	//This checks if we are redirecting to preview page and Shipping Method been changed
			        	if( BBBPayPalConstants.COUPONS.equals(addressVerifyVO.getRedirectState()) || BBBPayPalConstants.REVIEW.equals(addressVerifyVO.getRedirectState())){
			        		//shipping method check
							String ShippingMethodType = getPaypalServiceManager().validateShippingMethod(order, this.getUserProfile());
							if(!StringUtils.isEmpty(ShippingMethodType) && ShippingMethodType.equals(BBBCoreConstants.SHIP_METHOD_EXPEDIATED_ID)){
								addressVerifyVO.setShipingGroupChanged(true);
								addressVerifyVO.getAddressErrorList().add(this.getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPEDIT, BBBCoreConstants.DEFAULT_LOCALE, null));
							}
							else if(!StringUtils.isEmpty(ShippingMethodType) && ShippingMethodType.equals(BBBCoreConstants.SHIP_METHOD_EXPRESS_ID)){
								addressVerifyVO.getAddressErrorList().add(this.getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPRESS, BBBCoreConstants.DEFAULT_LOCALE, null));
								addressVerifyVO.setShipingGroupChanged(true);
							}
			        	}
			        	//R2.2 - END
		        	}else{
				        	//If address does not in order, we call PO, international as per request.
							pRequest.setParameter(BBBPayPalConstants.VALIDATE_ADDRESS, paypalParamMap.get(BBBPayPalConstants.VALIDATE_ADDRESS));
							pRequest.setParameter(BBBPayPalConstants.QAS_VERIFIED,paypalParamMap.get(BBBPayPalConstants.QAS_VERIFIED));
							service(pRequest, pResponse);
							addressVerifyVO  = (PayPalAddressVerifyVO) pRequest.getObjectParameter(BBBPayPalConstants.PAYPAL_ADDRESS_VO);
							error_exist  = (String) pRequest.getObjectParameter(BBBPayPalConstants.ERROR_EXIST);
							if(BBBUtility.isNotEmpty(error_exist) && error_exist.equalsIgnoreCase(BBBCoreConstants.TRUE)){
								addressVerifyVO.setRedirectState(BBBCoreConstants.SHOPPING_CART);
							}
		        	}
		        	// We are using "PREVIEW" as redirect state in Mobile
		        	if(BBBPayPalConstants.REVIEW.equals(addressVerifyVO.getRedirectState())){
		        		addressVerifyVO.setRedirectState(BBBPayPalConstants.PREVIEW);
		        	}
	        }
		} catch (ServletException e) {
			this.logError("ServletException occured in Rest service PayPalDroplet.getVerifiedShippingAddress :", e);
		} catch (IOException e) {
			this.logError("IOException occured in Rest service PayPalDroplet.getVerifiedShippingAddress :", e);
		}  finally {
				logDebug(" PayPalDroplet.verifyShippingAddress() method ends");
		}
		return addressVerifyVO;
	}
	
	/**
	 * This method is to handle error scenarios in Desktop
	 * to show validation errors on cart or payment page
	 * 
	 * @param addressVerifyVO
	 * @param redirectState
	 */
	private PayPalAddressVerifyVO handlePaypalError(PayPalAddressVerifyVO addressVerifyVO, String errorMsg, String redirectUrl, DynamoHttpServletRequest pRequest){
		
		addressVerifyVO.setSuccess(false);
		addressVerifyVO.setRedirectUrl(redirectUrl);
		ArrayList<String> errorList = new ArrayList<String>();
		errorList.add(errorMsg);
		addressVerifyVO.setAddressErrorList(errorList);
		this.getPayPalSessionBean().setErrorList(errorList);
		pRequest.setParameter(BBBPayPalConstants.ERROR_EXIST, BBBCoreConstants.TRUE);
		pRequest.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS_VO,addressVerifyVO);
		return addressVerifyVO;
	}

	/**
	 * @return the order
	 */
	public BBBOrder getOrder() {
		return this.order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(BBBOrder order) {
		this.order = order;
	}

	/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}

	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}
	
}
