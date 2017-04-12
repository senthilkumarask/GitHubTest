//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit
//written consent is prohibited.
//
//Created by: dhanashree waghmare
//
//Created on: 17-Feb-2014
//--------------------------------------------------------------------------------

package com.bbb.commerce.browse.droplet;
import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager;
//import com.bbb.commerce.catalog.vo.ProductVO;
//import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;


/**
 * This class is to populate the all info of product which is the part of
 * product VO.
 *
 */
public class EverLivingPDPDroplet extends BBBDynamoServlet {

/* ===================================================== *
	MEMBER VARIABLES
 * ===================================================== */
	private ProductManager productManager;
	//private BBBInventoryManager inventoryManager;

	public final static String OPARAM_OUTPUT="output";
	public final static String OPARAM_ERROR="error";
	public final static String EVER_LIVING_PRODUCT = "everLivingProduct";
	public final static String COLLECTION = "collection";

	public ProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(final ProductManager productManager) {
		this.productManager = productManager;
	}

	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String productId = pRequest.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER);
		String siteId = pRequest.getParameter(BBBCmsConstants.SITE_ID);

		boolean everLivingProduct= false;

		if(null != productId && !productId.isEmpty()){
			try {
				everLivingProduct = getProductManager().getProductStatus(siteId, productId);
			} catch (BBBSystemException e) {
				// TODO Auto-generated catch block
				logError(e.getMessage(),e);
			} catch (BBBBusinessException e) {
				// TODO Auto-generated catch block
				logError(e.getMessage(),e);
			}
				pRequest.setParameter(EVER_LIVING_PRODUCT, everLivingProduct);
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
						pResponse);

			}else{
					pRequest.serviceParameter(OPARAM_ERROR, pRequest,
							pResponse);
				}
			}


}