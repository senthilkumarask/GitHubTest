package com.bbb.search.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.search.bean.result.BBBProduct;

/**
 * This class is to populate the next and previous product ids to be 
 * used for quick view on TBS
 */

public class TBSMinimalQuickViewDroplet extends DynamoServlet {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		logDebug("TBSMinimalQuickViewDroplet :: service() method :: START");
		Object  productList = pRequest.getObjectParameter("array");
		if(productList == null){
			logDebug("productList is :: "+productList);
			pRequest.serviceLocalParameter("empty", pRequest, pResponse);
			return;
		}
		if(productList instanceof ArrayList && ((ArrayList) productList).size() > 0){
			List<BBBProduct> products = (List<BBBProduct>) productList;
			pRequest.setParameter("size", products.size());
			pRequest.serviceLocalParameter("outputStart", pRequest, pResponse);
			for (int i = 0; i < products.size(); i++) {
				if (i + 1 < products.size()) {
					pRequest.setParameter("nextProductId", products.get(i + 1).getProductID());
				}
				if (i > 0) {
					pRequest.setParameter("previousProductId", products.get(i - 1).getProductID());
				}
				pRequest.setParameter("productVO", products.get(i));
				pRequest.setParameter("count", i+1);
				pRequest.serviceLocalParameter("output", pRequest, pResponse);
			}
			pRequest.serviceLocalParameter("outputEnd", pRequest, pResponse);
			logDebug("TBSMinimalQuickViewDroplet :: service() method :: END");
		}
	}

}
