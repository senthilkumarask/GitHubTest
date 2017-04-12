package com.bbb.commerce.cart.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;

public class ValidateClosenessQualifier extends BBBDynamoServlet {

	public static final ParameterName ORDER_PARAMETER = ParameterName
			.getParameterName("order");

	private BBBOrder order;

	private BBBPricingTools mPricingTools;

	/**
	 * @return the pricingTools
	 */
	public final BBBPricingTools getPricingTools() {
		return this.mPricingTools;
	}

	/**
	 * @param pPricingTools
	 *            the pricingTools to set
	 */
	public final void setPricingTools(final BBBPricingTools pPricingTools) {
		this.mPricingTools = pPricingTools;
	}

	public BBBOrder getOrder() {
		return order;
	}

	public void setOrder(BBBOrder order) {
		this.order = order;
	}

	private static final String RESULT = "result";

	/**
	 * The method check whether all the items in the cart are Bopus items or
	 * they have free shipping
	 * 
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		BBBOrder pDestOrder = getOrder();
		this.logDebug("ValidateClosenessQualifier.service : Starts");
		boolean displayBanner = false;
		final PriceInfoVO orderPriceInfo = getPricingTools().getOrderPriceInfo(
				(BBBOrderImpl) pDestOrder);
		double finalShippingCharge = orderPriceInfo.getFinalShippingCharge();
		if (finalShippingCharge > 0) {
			this.logDebug("The banner should be displayed " + displayBanner);
			displayBanner = true;

		}

		this.logDebug("ValidateClosenessQualifier.service : ENDS");
		req.setParameter(RESULT, displayBanner);
		req.serviceParameter(BBBCoreConstants.OPARAM, req, res);

	}
}