package com.bbb.search.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.catalog.CatalogTools;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.SkuVO;

public class TBSSearchReportDroplet extends DynamoServlet {
	
	private CatalogTools catalogtools;

	/**
	 * @return the catalogtools
	 */
	public CatalogTools getCatalogtools() {
		return catalogtools;
	}
	/**
	 * @param pCatalogtools the catalogtools to set
	 */
	public void setCatalogtools(CatalogTools pCatalogtools) {
		catalogtools = pCatalogtools;
	}

	@SuppressWarnings("unchecked")
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		String searchTeam = pRequest.getParameter("keyword");
		Object object = pRequest.getObjectParameter("resultProducts");
		StringBuilder resultsList = null;
		StringBuilder noResultsList = null;
		List<BBBProduct> bbbProducts = null;
		String[] skuIds = null;
		List<String> searchIdLst = new ArrayList<String>();
		List<String> resultskuIds = new ArrayList<String>();
		List<String> foundskuIds = new ArrayList<String>();
		
		if(!StringUtils.isBlank(searchTeam)){
			skuIds = searchTeam.split(",");
			for (String skuId : skuIds) {
				searchIdLst.add(skuId.trim());
			}
		}
		if(object != null){
			bbbProducts = (List<BBBProduct>) object; 
			for (BBBProduct bbbProduct : bbbProducts) {
				Map<String, SkuVO> skuSet = bbbProduct.getSkuSet();
				for (String skuId : skuSet.keySet()) {
					resultskuIds.add(skuSet.get(skuId).getSkuID());
				}
			}
			for (String resSkuId : resultskuIds) {
				try {
					
					if(null != searchIdLst && searchIdLst .contains(resSkuId)){
						foundskuIds.add(resSkuId);
					} else {
						RepositoryItem skuItem = getCatalogtools().findSKU(resSkuId);
						if(null != skuItem.getPropertyValue("upc") && null !=searchIdLst && searchIdLst.contains(((String)skuItem.getPropertyValue("upc")))){
							foundskuIds.add((String)skuItem.getPropertyValue("upc"));
						}	
					}
					
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			if (null != foundskuIds) {
				for (String foundskuId : foundskuIds) {
					if(resultsList == null){
						resultsList = new StringBuilder(foundskuId);
					} else {
						resultsList.append(",").append(foundskuId);
					}
					searchIdLst.remove(foundskuId);
				}
				if (!searchIdLst.isEmpty()){
					for (String skuid : searchIdLst) {
						if(noResultsList == null){
							noResultsList = new StringBuilder(skuid);
						} else {
							noResultsList.append(",").append(skuid);
						}
					}
				}
				
			}
			if(resultsList != null){
				pRequest.setParameter("resultsList", resultsList.toString());
			}
			if(noResultsList != null){
				pRequest.setParameter("noResultsList", noResultsList.toString());
			}
			pRequest.serviceLocalParameter("output", pRequest, pResponse);
		} else {
			pRequest.serviceLocalParameter("empty", pRequest, pResponse);
		}
	}
}
