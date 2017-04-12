/**
 * 
 */
package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import atg.multisite.SiteContextManager;
import atg.nucleus.GenericService;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * Helper to provide Shipping restriction message on the Product detail page,
 * Cart page and gift giver registry page
 * 
 * 
 * 
 * 
 */
public class IsProductSKUShippingDropletHelper extends GenericService {
	private BBBCatalogTools mCatalogTools;
	private boolean mAttributeSKUEnabled;
	private boolean mAttributeProductEnabled;
	private static final String LIST_OF_RESTRICTED_ATTRIBUTES = "listOfRestrictedAttributes";
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	/**
	 * @param pSiteId
	 * @param pSkuId
	 * @param pProductId
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public Map<String, AttributeVO> getAttribute(String pSiteId, String pSkuId,String pProductId) throws ServletException, IOException {
		if(isLoggingDebug()){
			logDebug("IsProductSKUShippingDropletHelper.getAttribute starts");
		}
		Map<String,AttributeVO> restrictionZipCodeAttributes= new HashMap<String,AttributeVO>();
		ProductVO prodVO=null; 
		SKUDetailVO skuVO=null; 
		if (pSiteId == null) {
			pSiteId = SiteContextManager.getCurrentSiteId();
		}
		try {		 	
			if (pSkuId != null && mCatalogTools != null) {
				if(isAttributeSKUEnabled()){
					skuVO=getCatalogTools().getSKUDetails(pSiteId, pSkuId,true);
					getRestrictionZipCodeAttributes(null,skuVO,restrictionZipCodeAttributes);
				}
				if(isAttributeProductEnabled() && pProductId!=null){
					prodVO=getCatalogTools().getProductDetails(pSiteId,pProductId);
					getRestrictionZipCodeAttributes(prodVO,null,restrictionZipCodeAttributes);
				}
			}  
			if(isLoggingDebug()){
				logDebug("***********************restrictionZipCodeAttributes********** = " + restrictionZipCodeAttributes);
				logDebug("IsProductSKUShippingDropletHelper.getAttribute ends");
			}
			return restrictionZipCodeAttributes;
			 
		} catch (BBBBusinessException pException) {
			logError(pException.getStackTrace().toString());
		} catch (BBBSystemException pException) {
			logError(pException.getStackTrace().toString());
		}
		return null;
	}
	
	/**
	 * Get attribute details
	 * @param pProductVO
	 * @param pSkudetailsVO
	 * @param pRestrictionZipCodeAttributes
	 * @return
	 */
	private Map<String,AttributeVO> getRestrictionZipCodeAttributes(ProductVO pProductVO,SKUDetailVO pSkudetailsVO,Map<String,AttributeVO> pRestrictionZipCodeAttributes){
		Map<String,List<AttributeVO> >pAttributesMap=null;
		List<AttributeVO> listAttributeVO=null;
		List<String> listOfRestrictedAttributes=null;
		String[] restrictedAttributesArray = null;
		try {
			
			listOfRestrictedAttributes = (List<String>) getCatalogTools()
					.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, LIST_OF_RESTRICTED_ATTRIBUTES);
			if (listOfRestrictedAttributes != null && !listOfRestrictedAttributes.isEmpty()) {
				restrictedAttributesArray = listOfRestrictedAttributes.get(0).split(",");
			}
		} catch (BBBSystemException e) {
			logError("listOfRestrictedAttributes key not found in the repository");
		} catch (BBBBusinessException e) {
			logError("listOfRestrictedAttributes key not found in the repository");
		}
		if(pProductVO!=null){
			pAttributesMap=pProductVO.getAttributesList();
		}else if(pSkudetailsVO!=null){
			pAttributesMap=pSkudetailsVO.getSkuAttributes();
		}
		if(pAttributesMap!=null && !pAttributesMap.isEmpty()){
		
			Set<String> listOfAttriPH = pAttributesMap.keySet();
			for(String key: listOfAttriPH){
				listAttributeVO=(List<AttributeVO>)pAttributesMap.get(key);
				for(AttributeVO attributeVO: listAttributeVO){
					if(attributeVO !=null && attributeVO.getAttributeName()!=null){
						if (restrictedAttributesArray != null && restrictedAttributesArray.length > 0) {
							for (String restrictedAttribute : restrictedAttributesArray) {
								if (restrictedAttribute != null && restrictedAttribute.equalsIgnoreCase(attributeVO.getAttributeName())) {
									pRestrictionZipCodeAttributes.put(attributeVO.getAttributeName(),attributeVO);
								}
							}
						}						
					}
				}
			}
		}
		return pRestrictionZipCodeAttributes;
	}

	/**
	 * @return the mAttributeSKUEnabled
	 */
	public boolean isAttributeSKUEnabled() {
		return mAttributeSKUEnabled;
	}

	/**
	 * @param mAttributeSKUEnabled the mAttributeSKUEnabled to set
	 */
	public void setAttributeSKUEnabled(boolean mAttributeSKUEnabled) {
		this.mAttributeSKUEnabled = mAttributeSKUEnabled;
	}

	/**
	 * @return the mAttributeProductEnabled
	 */
	public boolean isAttributeProductEnabled() {
		return mAttributeProductEnabled;
	}

	/**
	 * @param mAttributeProductEnabled the mAttributeProductEnabled to set
	 */
	public void setAttributeProductEnabled(boolean mAttributeProductEnabled) {
		this.mAttributeProductEnabled = mAttributeProductEnabled;
	}

}
