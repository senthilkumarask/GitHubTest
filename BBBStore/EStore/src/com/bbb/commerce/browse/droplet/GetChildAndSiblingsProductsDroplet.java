package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 *
 *
 */
public class GetChildAndSiblingsProductsDroplet extends DynamoServlet{
	private static final String IS_COLLECTION = "isCollection";
	private ProductManager productManager;
	private  static final String SIBLING_PRODUCT_DETAILS="siblingProductDetails";
	private static final String IS_ACCESSORY_LEAD = "isAccessoryLead";
	private static final String SIBLING_PRODUCT_SIZE = "siblingProductSize";

	/**
	 * @return productManager
	 */
	public ProductManager getProductManager() {
		return productManager;
	}
	

	/**
	 * @param productManager
	 */
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}
	
	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) throws ServletException,	IOException {
		String productId = pRequest.getParameter(BBBCoreConstants.PRODUCT_ID_PARAM);
		try {
			List<ProductVO> productDetailsWithSiblings=getProductManager().getProductDetailsWithSiblings(productId);
			
			if(productDetailsWithSiblings!=null && !productDetailsWithSiblings.isEmpty()){
				pRequest.setParameter(SIBLING_PRODUCT_DETAILS,productDetailsWithSiblings);
				pRequest.setParameter(SIBLING_PRODUCT_SIZE,productDetailsWithSiblings.size());
				if(productDetailsWithSiblings.get(0).isParentcollection()){
					pRequest.setParameter(IS_COLLECTION,true);	
				}
				if(productDetailsWithSiblings.get(0).isAccessoryLead()){
					pRequest.setParameter(IS_ACCESSORY_LEAD,true);
				}
				
				pRequest.serviceParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
			}
			
		} catch (BBBBusinessException | BBBSystemException e) {
			logError("Error while fetching siblings for product: "+productId+":: "+e);
		}
		
	}
	

}
