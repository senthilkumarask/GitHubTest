package com.bbb.search.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.order.TBSOrderTools;

public class TBSPrintWorkOrderDroplet extends DynamoServlet {
	
	private TBSOrderTools mOrderTools;

	/**
	 * @return the orderTools
	 */
	public TBSOrderTools getOrderTools() {
		return mOrderTools;
	}

	/**
	 * @param pOrderTools the orderTools to set
	 */
	public void setOrderTools(TBSOrderTools pOrderTools) {
		mOrderTools = pOrderTools;
	}

	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		String onlineOrder = pRequest.getParameter("onlineOrderNumber");
		String atgorderId = null;
		if(!StringUtils.isBlank(onlineOrder)){
			try {
				atgorderId = getOrderTools().orderSearch(onlineOrder);
				 if(!StringUtils.isBlank(atgorderId)){
					 pRequest.setParameter("atgorderId", atgorderId);
					 pRequest.serviceLocalParameter("output", pRequest, pResponse);
				 } else {
					 pRequest.serviceLocalParameter("empty", pRequest, pResponse);
				 }
			} catch (RepositoryException e) {
				pRequest.setParameter("error", e.getMessage());
				vlogError("Exception occurred "+e);
			}
		}
	}
}
