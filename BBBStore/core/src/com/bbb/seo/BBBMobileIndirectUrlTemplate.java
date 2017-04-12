package com.bbb.seo;

import java.util.Iterator;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;
import atg.servlet.DynamoHttpServletRequest;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBConfigRepoUtils;

public class BBBMobileIndirectUrlTemplate extends IndirectUrlTemplate {
	private BBBCatalogTools mCatalogTools;
	private String mMobileForwrdtemplateFormat;
	private String skuParamName;
	
	public String getSkuParamName() {
		return skuParamName;
	}

	public void setSkuParamName(String skuParamName) {
		this.skuParamName = skuParamName;
	}

	@Override
	public String getForwardUrl(DynamoHttpServletRequest arg0, String arg1,
			WebApp arg2, Repository arg3) throws ItemLinkException {
		
		String finalUrl = null;
		
		try {
			
			String productId = super.getForwardUrl(arg0, arg1, arg2, arg3);
			
			if(productId == null){
				return null;
			}
        
			
			String siteId = SiteContextManager.getCurrentSite().getId();
			
			if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
				
				finalUrl = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.MOBILE_LEGACYURL_BUYBUYBABY);
				
			}else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
				
				finalUrl = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.MOBILE_LEGACYURL_BED_BATH_US);
				
			}else if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
				finalUrl = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.MOBILE_LEGACYURL_BED_BATH_CA);			
			}	
			
		if(arg0.getObjectParameter(getSkuParamName())!= null){
			
			finalUrl = finalUrl + (String)arg0.getObjectParameter(getSkuParamName());
			return finalUrl;
		}
			

			if (finalUrl == null) {
				return null;
			}

			ProductVO prodVO = mCatalogTools.getProductDetails(SiteContextManager.getCurrentSiteId(), productId);
			CollectionProductVO collectionProductVO = null;
			if (prodVO != null) {
				if(prodVO instanceof CollectionProductVO){
					collectionProductVO = (CollectionProductVO) prodVO;
				}
					
				if (collectionProductVO != null && collectionProductVO.getLeadSKU()) {
					finalUrl = finalUrl + prodVO.getChildSKUs().get(0);
				}
				else if(collectionProductVO != null  && prodVO.isCollection()){
					
					
					Iterator<ProductVO> it = collectionProductVO.getChildProducts().iterator();
					ProductVO childProdVO = null;
					while (it.hasNext()){
					
					childProdVO = it.next();
					if (childProdVO != null && childProdVO.getChildSKUs() != null && childProdVO.getChildSKUs().size() > 0) {
						finalUrl = finalUrl + childProdVO.getChildSKUs().get(0);
						return finalUrl;
					}
					}
				}
				else {
					if (prodVO.getChildSKUs() != null
							&& prodVO.getChildSKUs().size() > 0) {
						finalUrl = finalUrl + prodVO.getChildSKUs().get(0);
					} else {
						finalUrl = finalUrl + prodVO.getProductId();
					}
				}
			}else{
				if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
					
					finalUrl = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.MOBILE_LEGACY_ERR_URL_BUYBUYBABY);
					
				}else if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
					
					finalUrl = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.MOBILE_LEGACY_ERR_URL_BED_BATH_US);
					
				}else if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
					finalUrl = BBBConfigRepoUtils.getStringValue(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.MOBILE_LEGACY_ERR_URL_BED_BATH_CA);
				}
			}
		
		}
		catch(Exception e){
			
			logError("Error while redirecting to mobile legacy Site"); 
			return null;
		}

		return finalUrl;
	}
	
	@Override
	public String formatUrl(UrlParameter[] pUrlParams, WebApp pDefaultWebApp)
			throws ItemLinkException {
		return super.formatUrl(pUrlParams, pDefaultWebApp);
	}
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	public String getMobileForwrdtemplateFormat() {
		return mMobileForwrdtemplateFormat;
	}

	public void setMobileForwrdtemplateFormat(String pMobileForwrdtemplateFormat) {
		this.mMobileForwrdtemplateFormat = pMobileForwrdtemplateFormat;
	}
	
}
