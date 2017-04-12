package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.vo.RecommendedCategoryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * @author ddutta
 *
 */
public class GetRelatedCategoriesDroplet extends BBBDynamoServlet{
	
	/** The Constant productManager. */
	private ProductManager productManager;
	/** The Constant RECOMM_CAT_VO. */
	private static final String RECOMM_CAT_VO="recommCatVO";
	/** The Constant SHOW_RECOMM. */
	private static final String SHOW_RECOMM = "showRecommendations";
	
	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,	IOException {
		
		String categoryId = pRequest.getParameter(BBBCoreConstants.CATEGORY_ID_PARAM);
		String productId = pRequest.getParameter(BBBCoreConstants.PRODUCT_ID_PARAM);
		List<RecommendedCategoryVO> recommendedCategoryList=getProductManager().getCategoryRecommendation(categoryId,productId);
		if(recommendedCategoryList!=null&&!recommendedCategoryList.isEmpty()){
			pRequest.setParameter(RECOMM_CAT_VO,recommendedCategoryList);
			pRequest.setParameter(SHOW_RECOMM,true);
		}
		pRequest.serviceParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
	 }

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
	


	

}
