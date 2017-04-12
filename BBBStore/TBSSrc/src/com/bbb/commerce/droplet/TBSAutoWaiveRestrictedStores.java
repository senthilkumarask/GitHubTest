/**
 * 
 */
package com.bbb.commerce.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.catalog.TBSCatalogToolsImpl;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * This class is used to get the restricted AutoWaive stores and remove the AutoWaive details from
 * order in case of current store is there in restricted stores list
 * 
 * @author Mdevireddy
 *
 */
public class TBSAutoWaiveRestrictedStores extends DynamoServlet {
	
	private TBSOrderTools orderTools;
	private TBSCatalogToolsImpl bbbCatalogTools;

	/**
	 * @return the orderTools
	 */
	public TBSOrderTools getOrderTools() {
		return orderTools;
	}

	/**
	 * @param orderTools the orderTools to set
	 */
	public void setOrderTools(TBSOrderTools orderTools) {
		this.orderTools = orderTools;
	}

	/**
	 * @return the bbbCatalogTools
	 */
	public TBSCatalogToolsImpl getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	/**
	 * @param bbbCatalogTools the bbbCatalogTools to set
	 */
	public void setBbbCatalogTools(TBSCatalogToolsImpl bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException ,IOException {
		Object orderObj = pRequest.getObjectParameter("order");
		if(orderObj != null){
			BBBOrder order = (BBBOrder) orderObj;
			String storeNum = getOrderTools().storeNumFromCookie(pRequest);
			vlogDebug("Current store ::"+storeNum);
			List<String> storeIds =  getOrderTools().getBlockedAutoWaiveStores();
			vlogDebug("Restricted Stores ::"+storeIds);
			if(!StringUtils.isBlank(storeNum) && storeIds != null && !storeIds.isEmpty() && storeIds.contains(storeNum)){
				getOrderTools().removeAutoWaiveDetails(order);
			}
		}
	}
	
	
}
