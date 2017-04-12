package com.bbb.common.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBSystemException;

// TODO: Auto-generated Javadoc
/**
 * This droplet Fetches List Price and Sale Price based on productId and SKUId.
 * 
 * @author skalr2
 * 
 */
public class ListPriceSalePriceDroplet extends BBBDynamoServlet {
	
	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;
	
	/**
		CONSTANTS
	 **/	
	public static final String PRODUCTID = "productId";
	public static final String SKUID = "skuId";
	public static final String SALEPRICE = "salePrice";
	public static final String LISTPRICE = "listPrice";
	public static final String INCARTPRICE = "inCartPrice";
	public static final String OUTPUT = "output";

	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		logDebug(" ListPriceSalePriceDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		String productId = null;
		String skuId = null;

		try {
			productId = pRequest.getParameter(PRODUCTID);
			skuId = pRequest.getParameter(SKUID);
			
			Double salePrice = 0.0;
			Double listPrice = 0.0;
			Double inCartPrice = 0.0;
			
			salePrice = getCatalogTools().getSalePrice(productId,skuId);
			listPrice = getCatalogTools().getListPrice(productId,skuId);
			
			//BBBH-2890 - fetching incart price
			inCartPrice = getCatalogTools().getIncartPrice(productId, skuId).doubleValue();

			pRequest.setParameter(SALEPRICE,salePrice);
			pRequest.setParameter(LISTPRICE,listPrice);
			pRequest.setParameter(INCARTPRICE,inCartPrice);
			
			pRequest.serviceParameter(OUTPUT, pRequest,pResponse);
		} catch (BBBSystemException bbbsEx) {
			logError(bbbsEx);
		}
		logDebug(" ListPriceSalePriceDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends");
	}

	/**
	 * Gets the catalog tools.
	 * 
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 * 
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}
}