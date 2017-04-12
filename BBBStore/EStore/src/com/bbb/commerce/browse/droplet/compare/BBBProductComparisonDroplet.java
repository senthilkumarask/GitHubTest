//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Created by: Madhur Aggarwal
//
//Created on: 14-February-2014
//--------------------------------------------------------------------------------

package com.bbb.commerce.browse.droplet.compare;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.comparison.BBBProductComparisonList;
import com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * This class is used to iterate over the list of products
 * added in compare products drawer from session and populate
 * each item's ComparisonProductVO and update its attributes
 * and to display them on product comparison page.
 * 
 */
public class BBBProductComparisonDroplet extends BBBDynamoServlet {

	private BBBProductComparisonList comparisonList;
	private BBBCatalogTools catalogTools;
	
	private static final String OPARAM_OUTPUT="output";
	private static final String OPARAM_ERROR="error";
	private static final String COMPARE_PRODUCTS_SUCCESS = "compareProductsSuccess";
   
	
	/**
	 * @return comparisonList
	 */
	public final BBBProductComparisonList getComparisonList() {
		return this.comparisonList;
	}

	/**
	 * @param comparisonList
	 */
	public final void setComparisonList(final BBBProductComparisonList comparisonList) {
		this.comparisonList = comparisonList;
	}

	/**
	 * This method gets the ProductComparisonList from session
	 * and iterate over each item to update each item's VO with
	 * required attributes
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public final void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		this.logDebug("BBBProductComparisonDroplet.service() method starts");

		// get comparison products list from session.
		List<CompareProductEntryVO> compareProductsList = getComparisonList().getItems();
          	
			getComparisonList().setColorFlagClear(0);
			getComparisonList().setAttributesListFlagClear(0);
			if(!BBBUtility.isListEmpty(compareProductsList)){
					int noAttributeProductCount=1,noColorProductCount=1,noPersonalizedAvailableProd=1;		
             	for(CompareProductEntryVO productToCompare : compareProductsList){
				try {
							this.logDebug("Updating the CompareProductEntryVO of the product in comparison list for product id: "+ productToCompare.getProductId());
					// call to update the VO of each product in comparison list with the attributes.
							getCatalogTools().getCompareProductDetail(productToCompare);
						if(BBBUtility.isMapNullOrEmpty(productToCompare.getAttributesList()))
							{
								getComparisonList().setAttributesListFlagClear(noAttributeProductCount);;
								noAttributeProductCount++;
							}
						if(BBBUtility.isMapNullOrEmpty(productToCompare.getColor()) && !productToCompare.isCollection())
							{
								getComparisonList().setColorFlagClear(noColorProductCount);
								noColorProductCount++;
							}
						if(StringUtils.isBlank(productToCompare.getCustomizationCode())) {
							getComparisonList().setCustomizationCodeFlagClear(noPersonalizedAvailableProd);
							noPersonalizedAvailableProd++;
						}
					
				} catch (BBBSystemException excep) {
					this.logError("SystemException from service method of BBBProductComparisonDroplet while iterating the product compare VO of product id : " + productToCompare.getProductId(), excep);
				}
				catch (BBBBusinessException excep) {
					this.logError("BusinessException from service method of BBBProductComparisonDroplet while iterating the product compare VO of product id : " + productToCompare.getProductId(), excep);
				}
			}
			this.logDebug("The comparison list products VOs are successfully updated.");
			pRequest.setParameter(COMPARE_PRODUCTS_SUCCESS, Boolean.TRUE);
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
		}
		else {
			this.logError("Products Comparison List is null in the session");
			pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
		}
		this.logDebug("BBBProductComparisonDroplet.service() method ends");
	}

	/**
	 * @return catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
}
