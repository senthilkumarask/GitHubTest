package com.bbb.commerce.order.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;

public class TBSCommerceItemLookupDroplet extends DynamoServlet{

	@Override
	public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) throws ServletException, IOException {

		String cId = pReq.getParameter("id");
		Object orderObj = pReq.getObjectParameter("order");
		String elementName = pReq.getParameter("elementName");
		CommerceItem cItem = null;
		double listPrice = 0.0;
		if(orderObj != null && !StringUtils.isBlank(cId)){
			BBBOrder order = (BBBOrder) orderObj;
			try {
				cItem = order.getCommerceItem(cId);
				RepositoryItem skuItem = (RepositoryItem) cItem.getAuxiliaryData().getCatalogRef();
				String skuName = (String) skuItem.getPropertyValue("displayName");
				listPrice = cItem.getPriceInfo().getListPrice();
				pReq.setParameter("displayName", skuName);
				pReq.setParameter("listPrice", listPrice);
				if (elementName == null){
					elementName = "element";
				}
				pReq.setParameter(elementName, cItem);
				pReq.serviceLocalParameter(TBSConstants.OUTPUT, pReq, pRes);
			} catch (CommerceItemNotFoundException e) {
				vlogError("CommerceItemNotFoundException occurred  :: "+e);
			} catch (InvalidParameterException e) {
				vlogError("InvalidParameterException occurred  :: "+e);
			}
	}
	}
}
