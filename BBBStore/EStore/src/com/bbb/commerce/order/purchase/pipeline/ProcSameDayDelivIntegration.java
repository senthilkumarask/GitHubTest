package com.bbb.commerce.order.purchase.pipeline;

import java.io.IOException;
import java.util.HashMap;

import javax.transaction.SystemException;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.processor.LoadProperties;
import atg.core.util.ResourceUtils;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.browse.vo.SddZipcodeVO;
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.sdd.vo.SddRequestVO;
import com.bbb.sdd.vo.response.SddVOResponse;
import com.bbb.utils.BBBConfigRepoUtils;


public class ProcSameDayDelivIntegration extends LoadProperties implements PipelineProcessor {

	/** The Constant MY_RESOURCE_NAME. */
	static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";
	

	/**  Resource Bundle *. */
	private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME,
			atg.service.dynamo.LangLicense.getLicensedDefault());
	
	static final String SAME_DAY_DELIV_ERROR_MESSAGE = "";

	/** The success. */
	private final int SUCCESS = 1;
	private boolean enabled;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private BBBSameDayDeliveryManager sameDayDeliveryManager;

	public BBBSameDayDeliveryManager getSameDayDeliveryManager() {
		return sameDayDeliveryManager;
	}

	public void setSameDayDeliveryManager(BBBSameDayDeliveryManager sameDayDeliveryManager) {
		this.sameDayDeliveryManager = sameDayDeliveryManager;
	}
	

	/**
	 * Returns the valid return codes 1 - The processor completed.
	 * 
	 * @return an integer array of the valid return codes.
	 */
	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	/* (non-Javadoc)
	 * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object, atg.service.pipeline.PipelineResult)
	 */
	@Override
	public int runProcess(Object pParamObject,
			PipelineResult pParamPipelineResult) throws Exception {
		logDebug("ProcSameDayDelivIntegration starts");
		HashMap map = (HashMap) pParamObject;
		BBBOrderImpl order = (BBBOrderImpl) map.get(PipelineConstants.ORDER);
		BBBSameDayDeliveryManager sdm = getSameDayDeliveryManager();
		boolean sameDayDeliveryFlag = false;
		if (order == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));

		if (sdm == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));

		logInfo("current order Order is : " + order.getId());
		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(
				BBBCoreConstants.SAME_DAY_DELIVERY_KEYS,
				BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
		if (null != sddEligibleOn) {
			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
		}
		// check if the same day delivery flag is true
		//If Checkout is being done with Same Day Delivery shipping method
		if (sameDayDeliveryFlag && null != order.getShippingGroups()
				&& order.getShippingGroups().size() == 1
				&& (ShippingGroup) order.getShippingGroups().get(0) instanceof BBBHardGoodShippingGroup
				&& isEnabled()
				&& ((BBBHardGoodShippingGroup) order.getShippingGroups().get(0))
				.getShippingMethod().equals(BBBCoreConstants.SDD)
				&& !StringUtils.isEmpty(((BBBHardGoodShippingGroup) order.getShippingGroups().get(0))
						.getSddStoreId())){
			try {
				logDebug("Calling Deliv API Call for order : "
						+ order.getId());
				// Create the Request to make Deliv API Call
				SddRequestVO sddvo = sdm.populateDelivRequestVO(order);
				SddVOResponse sddVOResponse = sdm.invokeDelivAPI(sddvo,
						order);
				if (null != sddVOResponse) {
					logDebug("Saving tracking Info for order : "
							+ order.getId());
					sdm.updateTrackingInfo(order, sddVOResponse
							.getTracking_code(), sddvo.getPackages().size());
				}
			} catch (BBBSystemException e) {
				String errorKey = BBBCoreErrorConstants.SAME_DAY_DELIV_ERROR;
				String pStatusMessage =SAME_DAY_DELIV_ERROR_MESSAGE;
				pParamPipelineResult.addError(errorKey, pStatusMessage);
				logError("Exception Occurred while processing the Same Day Deliv Integration." + e.getMessage());
				return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
			}
		}
		logDebug("ProcSameDayDelivIntegration Ends");
		return SUCCESS;

	}

	


	
}
