/**
 * 
 */
package com.bbb.commerce.order.paypal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.ServletUtil;

import com.bbb.commerce.vo.PayPalInputVO;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.paypal.BBBAddressPPVO;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;
import com.bbb.paypal.vo.PayPalAddressVerifyVO;

public class TBSPayPalAddressVerification extends PayPalAddressVerification{
	

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
	@Override
	public boolean validatePayPalShippingAddress(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrderImpl order, PayPalAddressVerifyVO addressVerifyVO, PayPalInputVO paypalInput) throws ServletException, IOException{
		
		logDebug("TBSPayPalAddressVerification.verifyPayPalShippingAddress() :: Start");
		BBBAddressPPVO shippingAddress = voResp.getShippingAddress();
		List<String> addressErrorList = new ArrayList<String>();
		boolean success = true;
		try {
			logDebug("Address does not exist in Order. Validating Paypal Address");
			if(shippingAddress != null){
				shippingAddress.setFromPayPal(true);
				addressVerifyVO.setAddress(shippingAddress);
				
				//Verify Shipping Restrictions for PayPal address i.e Verify State and Zip code restriction
				addressErrorList = verifyShippingRestriction(order, shippingAddress, addressErrorList);
				if(addressErrorList != null && addressErrorList.size() > 0){
					logError("Shipping Restriction Present in Paypal Address");
					success = false;
					Object lObject = ServletUtil.getCurrentRequest().resolveName("/com/bbb/commerce/order/paypal/PayPalSessionBean");	
					if(lObject!=null && lObject instanceof BBBPayPalSessionBean)
					{
						BBBPayPalSessionBean lPalSessionBean = (BBBPayPalSessionBean) lObject;
						lPalSessionBean.setErrorList(addressErrorList);
					}
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
		
		logDebug("TBSPayPalAddressVerification.verifyPayPalShippingAddress() :: End");
		return success;
	}

}
