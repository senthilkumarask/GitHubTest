package com.bbb.commerce.browse.category;

import java.util.ArrayList;
import java.util.List;

import com.bbb.common.BBBGenericService;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;

/**
 * Helper class to retrieve latest last viewed productVO from
 *  productId list in session bean
 * the max no of productVO to be retrieved can be configured 
 * through property maxLastItemsViewed 
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * Created by: njai13
 * Created on: November-2011
 * @author njai13
 *
 */
public class LastViewedToutHelper  extends BBBGenericService  implements IProdToutHelper {
	protected static final String LAST_VIEWED_PRODUCT_ID_LIST = "productIdList";

	private BBBCatalogTools catalogTools;

	private Integer maxLastItemsViewed;
private BBBSessionBean sessionBean;

	/**
 * @return the sessionBean
 */
public BBBSessionBean getSessionBean() {
	return sessionBean;
}

/**
 * @param sessionBean the sessionBean to set
 */
public void setSessionBean(BBBSessionBean sessionBean) {
	this.sessionBean = sessionBean;
}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	

	/**
	 * @return the maxLastItemsViewed
	 */
	public Integer getMaxLastItemsViewed() {
		return maxLastItemsViewed;
	}

	/**
	 * @param maxLastItemsViewed the maxLastItemsViewed to set
	 */
	public void setMaxLastItemsViewed(Integer maxLastItemsViewed) {
		this.maxLastItemsViewed = maxLastItemsViewed;
	}



	/**
	 * The method retrieves latest last viewed product Ids from session Bean
	 * and adds ProductVO corresponding to it in lastViewedProductVOList 
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public List <ProductVO> getProducts(String siteId, String categoryId) throws BBBSystemException, BBBBusinessException {
		String methodName="LastViewedToutHelper:getProduct";
		
		logDebug("Start of "+methodName);
			
		List <ProductVO> lastViewedProductVOList=new ArrayList<ProductVO>();

		List<String> productIdList= (List<String>)getSessionBean().getValues().get(LAST_VIEWED_PRODUCT_ID_LIST);
		
		if(productIdList!=null){
			int productIdListSize=productIdList.size();
			//variable to keep track of no of productVO added to the list
			//Should not be more than maxLastItemsViewed
			int counter=0;
			int index;
			for(int i=productIdListSize;;i--){

				counter++;
				index=i-1;
				if(counter>getMaxLastItemsViewed()){
					break;
				}
				else{
					if(index>=0){
						String productId=productIdList.get((index));
						try {
							if(!this.getCatalogTools().isEverlivingProduct(productId, siteId)){
								lastViewedProductVOList.add(this.getCatalogTools().getProductDetails(siteId, productId, true));
							}
						} catch (BBBBusinessException bbe) {
							logError("LastViewedToutHelper.getProducts :: Received Business Exception. Ignore product "
									+ productId + " and continue loop :");
						} catch (BBBSystemException bse) {
							logError("LastViewedToutHelper.getProducts :: Received System Exception. Ignore product "
									+ productId + " and continue loop :");
						}
					  }
					}
				}

		}
		
		logDebug("Exit "+methodName);
		
		return lastViewedProductVOList;
	}

}
