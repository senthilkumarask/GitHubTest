package com.bbb.commerce.order.processor;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.order.bean.TBSCommerceItem;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.PipelineConstants;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class ProcUpdateGSOrder extends ApplicationLoggingImpl implements PipelineProcessor{
	
	
	/**
	 * Final Static variable for success.
	 */
	private static final int SUCCESS = 1;
	private static final int FAILURE = 2;

	static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

	/**
	 * Resource bundle
	 */
	private static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle("atg.commerce.order.OrderResources");

	private String mLoggingIdentifier = "ProcSendSubmitOrderMessage";
	
	
	/**
	 * Overriden method of PipelineProcessor which indicated the return code for
	 * run process method.
	 * 
	 * @return Int: Array of int .
	 */
	public int[] getRetCodes() {
		int[] retn = { SUCCESS, FAILURE };
		return retn;
	}

	/**
	 * Sets property LoggingIdentifier
	 */
	public void setLoggingIdentifier(String pLoggingIdentifier) {
		mLoggingIdentifier = pLoggingIdentifier;
	}

	/**
	 * Returns property LoggingIdentifier
	 */
	public String getLoggingIdentifier() {
		return mLoggingIdentifier;
	}

	/* (non-Javadoc)
	 * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object, atg.service.pipeline.PipelineResult)
	 */
	@Override
	public int runProcess(Object pParam,
			PipelineResult pResult) throws Exception {
		
		HashMap map = (HashMap) pParam;
		BBBOrder bbbOrder = (BBBOrder) map.get(PipelineConstants.ORDER);
		
		
		if (bbbOrder == null) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));
		}

		if (isLoggingDebug()) {
			logDebug("START: Updating Guided Selling Order for Order Id [" + bbbOrder.getId() + "]");
		}
		
		try {
			updateGSOrder(bbbOrder);
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while updating the Guided Selling Repository", e);
			}
		}
		return SUCCESS;
	}
	
	
	/**
	 * This method updates the guided selling order repository item
	 * with corresponding ATG order id.
	 *
	 * @param pBbbOrder the bbb order
	 * @throws RepositoryException the repository exception
	 */
	public void updateGSOrder(BBBOrder pBbbOrder) throws RepositoryException {
		List<CommerceItem> commerceItems = pBbbOrder.getCommerceItems();

		for (CommerceItem commerceItem : commerceItems) {
			if (commerceItem instanceof TBSCommerceItem) {
				TBSCommerceItem tbsCommerceItem = (TBSCommerceItem) commerceItem;
				String gsOrderId = tbsCommerceItem.getGsOrderId();

				if (!StringUtils.isBlank(gsOrderId)) {
					MutableRepositoryItem gsItem = getGuidedSellingRepository()
							.getItemForUpdate(gsOrderId, "gsOrderInfo");
					if (gsItem != null) {
						gsItem.setPropertyValue("atgOrderId", pBbbOrder.getId());
						getGuidedSellingRepository().updateItem(gsItem);
						break;
					}

				}

			}
		}

	}
	
	private MutableRepository mGuidedSellingRepository;

	public MutableRepository getGuidedSellingRepository() {
		return mGuidedSellingRepository;
	}

	public void setGuidedSellingRepository(
			MutableRepository pGuidedSellingRepository) {
		mGuidedSellingRepository = pGuidedSellingRepository;
	}


}
