package com.bbb.search.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.catalog.TBSCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.constants.TBSConstants;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.TBSProduct;

public class TBSQuickViewDroplet extends DynamoServlet {
	private Repository productCatalog;
	private TBSCatalogToolsImpl bbbCatalogTools;
	
	/**
	 * @return the productCatalog
	 */
	public Repository getProductCatalog() {
		return productCatalog;
	}
	/**
	 * @param pProductCatalog the productCatalog to set
	 */
	public void setProductCatalog(Repository pProductCatalog) {
		productCatalog = pProductCatalog;
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("TBSQuickViewDroplet :: service() method :: START");
		Object  productList = pRequest.getObjectParameter("array");
		String siteId = SiteContextManager.getCurrentSiteId();
		if(productList == null){
			vlogDebug("productList is :: "+productList);
			pRequest.serviceLocalParameter("empty", pRequest, pResponse);
			return;
		}
		if(productList instanceof ArrayList && ((ArrayList) productList).size() > 0){
			List<BBBProduct> products = (ArrayList) productList;
			TBSProduct productVO = null;
			RepositoryItem productItem = null;
			List<RepositoryItem> skuList = null;
			RepositoryItem lSkuRepositoryItem = null;
			pRequest.setParameter("size", products.size());
			pRequest.serviceLocalParameter("outputStart", pRequest, pResponse);
			boolean isActive = true;
			for (int i = 0; i < products.size(); i++) {
				productVO = new TBSProduct();
				try {
					productItem = getProductCatalog().getItem(products.get(i).getProductID(), "product");
					
					if(products.get(i).getSkuSet() != null){
						Set<String> skuIds = products.get(i).getSkuSet().keySet();
						for (String skuid : skuIds) {
								lSkuRepositoryItem = getProductCatalog().getItem(skuid, "sku");
								break;
						}
						if(productItem!=null && productItem.getPropertyValue("displayName")!=null){
							productVO.setProductName((String)productItem.getPropertyValue("displayName"));
							if(productItem.getPropertyValue("description")!=null)
							productVO.setShortDescription((String)productItem.getPropertyValue("description"));
						}
					}
					if( productItem != null && lSkuRepositoryItem == null) {
						skuList = (List<RepositoryItem>) productItem.getPropertyValue("childSKUs");
						vlogDebug("child skus :: "+skuList);
					}
					if(null!=skuList){
						for (Iterator lSkuIterator = skuList.iterator(); lSkuIterator.hasNext();) {
							lSkuRepositoryItem = (RepositoryItem) lSkuIterator.next();
						}
					}
				} catch (RepositoryException e) {
					vlogError("RepositoryException occurred while getting product item :: "+e);
					pRequest.serviceLocalParameter("error", pRequest, pResponse);
				}
				if(i+1 < products.size()){
					productVO.setNextProductId(products.get(i+1).getProductID());
				}
				if(i > 0){
					productVO.setPreviousProductId(products.get(i-1).getProductID());
				}
				productVO.setProductID(products.get(i).getProductID());
				if(StringUtils.isBlank(productVO.getProductName())){
					productVO.setProductName(products.get(i).getProductName());
				}
				productVO.setSwatchFlag(products.get(i).getSwatchFlag());
				productVO.setRollupFlag(products.get(i).isRollupFlag());
				productVO.setCollectionFlag(products.get(i).getCollectionFlag());
				productVO.setImageURL(products.get(i).getImageURL());
				productVO.setVerticalImageURL(products.get(i).getVerticalImageURL());
				productVO.setColorSet(products.get(i).getColorSet());
				productVO.setDescription(products.get(i).getDescription());
				productVO.setHighPrice(products.get(i).getHighPrice());
				productVO.setLowPrice(products.get(i).getLowPrice());
				productVO.setHyperlink(products.get(i).getHyperlink());
				productVO.setRatings(products.get(i).getRatings());
				productVO.setReviews(products.get(i).getReviews());
				productVO.setAttribute(products.get(i).getAttribute());
				productVO.setHideAttributeList(products.get(i).getHideAttributeList());
				productVO.setPriceRange(products.get(i).getPriceRange());
				productVO.setWasPriceRange(products.get(i).getWasPriceRange());
				productVO.setCategory(products.get(i).getCategory());
				productVO.setVideoId(products.get(i).getVideoId());
				productVO.setGuideId(products.get(i).getGuideId());
				productVO.setGuideTitle(products.get(i).getGuideTitle());
				productVO.setGuideImage(products.get(i).getGuideImage());
				productVO.setGuideAltText(products.get(i).getGuideAltText());
				productVO.setGuideShortDesc(products.get(i).getGuideShortDesc());
				productVO.setOthResCategory(products.get(i).getOthResCategory());
				productVO.setOthResTitle(products.get(i).getOthResTitle());
				productVO.setOthResLink(products.get(i).getOthResLink());
				productVO.setSeoUrl(products.get(i).getSeoUrl());
				productVO.setOnSale(products.get(i).getOnSale());
				productVO.setOtherPageType(products.get(i).getOtherPageType());
				productVO.setInCompareDrawer(products.get(i).isInCompareDrawer());
				//TBSN-262 fix starts here
				BazaarVoiceProductVO bvReviews = getBbbCatalogTools().getBazaarVoiceDetails(products.get(i).getProductID(), siteId);
				productVO.setBvReviews(bvReviews);
				//TBSN-262 fix ends here
				if(null!=productItem && productItem.getPropertyValue("longDescription") != null){
						productVO.setLongDescription((String)productItem.getPropertyValue("longDescription") );
				}
				if(null!=lSkuRepositoryItem && null!=lSkuRepositoryItem.getPropertyValue("displayName")){
					
					productVO.setSkuId(lSkuRepositoryItem.getRepositoryId());
					Set<RepositoryItem> skuAttrRelation = null;
					if(lSkuRepositoryItem != null){
						skuAttrRelation = (Set<RepositoryItem>) lSkuRepositoryItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
					}
					if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
						vlogDebug("skuAttrRelation :: "+skuAttrRelation );
						RepositoryItem skuAttribute = null;
						String skuAttrId = null;
						for (RepositoryItem skuAttrReln : skuAttrRelation) {
							skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
							if(skuAttribute != null){
								skuAttrId = skuAttribute.getRepositoryId();
							}
							if(!StringUtils.isBlank(skuAttrId) && (skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE) || skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE))){
								vlogDebug("Sku attribute :: "+skuAttrId);
								productVO.setCmokirschFlag(true);
							}
						}
					}
				}
				if(null!=lSkuRepositoryItem && lSkuRepositoryItem.getPropertyValue("ltlFlag") != null){
					productVO.setLtlProduct((Boolean)lSkuRepositoryItem.getPropertyValue("ltlFlag"));
				}
				lSkuRepositoryItem = null;
				productItem = null;
				pRequest.setParameter("productVO", productVO);
				pRequest.setParameter("count", i+1);
				pRequest.serviceLocalParameter("output", pRequest, pResponse);
			}
			pRequest.serviceLocalParameter("outputEnd", pRequest, pResponse);
			vlogDebug("TBSQuickViewDroplet :: service() method :: END");
		}
	}

}
