package com.bbb.commerce.cart.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/*
 * Written for checking if a particular sku is personalized or not.
 */
/**
 * @author dgoel7
 *
 */
public class IsPersonalizedSkuDroplet extends BBBDynamoServlet {

	private static final String PERSONALIZED_SKU = "personalizedSku";

	public final static String OPARAM_OUTPUT = "output";
	private ProductManager productManager;

	/**
	 * @return the productManager
	 */
	public ProductManager getProductManager() {
		return productManager;
	}

	/**
	 * @param productManager
	 *            the productManager to set
	 */
	public void setProductManager(final ProductManager productManager) {
		this.productManager = productManager;
	}

	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		boolean isSKUPersonalizedItem = false;

		String skuId = (String) pRequest.getParameter(BBBCoreConstants.SKUID);
		try {
			isSKUPersonalizedItem = productManager.isPersonalizedSku(skuId);
		} catch (BBBSystemException e) {
			this.logError("System Exception Occourred while getting isPersonalizedSku ", e);

		}
		pRequest.setParameter(IsPersonalizedSkuDroplet.PERSONALIZED_SKU, isSKUPersonalizedItem);
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
	}

}
