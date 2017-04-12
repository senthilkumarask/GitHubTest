package com.bbb.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.PreviewAttributes;
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO;
import com.bbb.commerce.catalog.vo.StoreSpecialityVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;
/**
 * @author Kumar Magudeeswaran
 * This class is created to refactor the code from BBBCatalogToolsImpl
 * Methods which cannot be accessed from core to Estore module are moved here.
 *  
 */
public class GlobalRepositoryTools extends BBBConfigToolsImpl {
	
	/**	Constant for string key not found for preview enabled. */
	private static final String KEY_NOT_FOUND_FOR_PREVIEW_ENABLED = "key not found for preview enabled ";
	/**	Boolean for preview enabled. */
	private Boolean previewEnabled;
	
	/** Map with details about sites offering customization. */
	private Map<String, String> customizationOfferedSiteMap;


	public String returnCountryFromSession() {
		String country = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		
		if(pRequest ==null){
			return null;
		}
		BBBSessionBean sessionBeanFromReq = (BBBSessionBean) pRequest
				.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME);
		if (sessionBeanFromReq == null) {
			sessionBeanFromReq = (BBBSessionBean) ServletUtil
					.getCurrentRequest().resolveName(
							BBBCoreConstants.SESSION_BEAN);
			pRequest.setAttribute(BBBCoreConstants.SESSION_BEAN_NAME,
					sessionBeanFromReq);
		}

		if (sessionBeanFromReq.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT) != null) {
			country = (String) sessionBeanFromReq.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
		}
		return country;

	}
	
	
	/**
     *  The method gets the details for EcoFee.
     *
     * @param pStateId the state id
     * @param pSkuId the sku id
     * @return Eco Free SKU Details
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final EcoFeeSKUVO getEcoFeeSKUDetailForState(final String pStateId, final String pSkuId)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getEcoFeeSKUDetailForState] pStateId ["+pStateId+"] pSkuId ["+pSkuId+"] Entry");
        final EcoFeeSKUVO pEcoFeeSKUVO = new EcoFeeSKUVO();
        boolean matchFound = false;
        int counter = 0;

        try {
            if ((null != pStateId) && !StringUtils.isEmpty(pStateId) && (null != pSkuId)
                            && !StringUtils.isEmpty(pSkuId)) {
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (null != skuRepositoryItem) {
                    final boolean isActive = this.isSkuActive(skuRepositoryItem);
                    this.logDebug("Sku is disabled no longer available");
                    if (isActive) {
                        @SuppressWarnings ("unchecked")
                        final Set<RepositoryItem> ecoFeeSKURelationList = (Set<RepositoryItem>) skuRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME);
                        if ((ecoFeeSKURelationList != null) && !ecoFeeSKURelationList.isEmpty()) {
                            for (final RepositoryItem ecoFeeSKURelationItem : ecoFeeSKURelationList) {
                                if (null != ecoFeeSKURelationItem
                                                .getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME)) {
                                    final String pRepoStateId = ((RepositoryItem) ecoFeeSKURelationItem
                                                    .getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME))
                                                    .getRepositoryId();
                                    if (pStateId.equals(pRepoStateId)
                                                    && (null != ecoFeeSKURelationItem
                                                                    .getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME))
                                                    && (null != ((RepositoryItem) ecoFeeSKURelationItem
                                                                    .getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME))
                                                                    .getRepositoryId())) {
                                        matchFound = true;
                                        counter++;
                                        if (counter > 1) {
                                            this.logDebug("More Than one eco-fee SKU for given sku id:" + pSkuId);
                                            throw new BBBBusinessException(
                                                            BBBCatalogErrorCodes.MORE_ECO_FEE_SKUS_AVAILABLE_FOR_SKU,
                                                            BBBCatalogErrorCodes.MORE_ECO_FEE_SKUS_AVAILABLE_FOR_SKU);
                                        }
                                        this.logDebug("Found EcoFee SKU for the skuID :" + pSkuId + "for state Id :"
                                                        + pStateId);
                                        pEcoFeeSKUVO.setEcoFeeSKUId(((RepositoryItem) ecoFeeSKURelationItem
                                                        .getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME))
                                                        .getRepositoryId());
                                        pEcoFeeSKUVO.setEcoFeeProductId(this
                                                        .getFirstActiveParentProductForInactiveSKU(pEcoFeeSKUVO
                                                                        .getFeeEcoSKUId()));

                                    }
                                }

                            }
                            if (!matchFound) {
                                this.logDebug("No Eco-fee SKU available for given sku id:" + pSkuId);
                                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_ECO_FEE_SKU_AVAILABLE_FOR_SKU,
                                                BBBCatalogErrorCodes.NO_ECO_FEE_SKU_AVAILABLE_FOR_SKU);
                            }
                        } else {
                            this.logDebug("No Eco-fee SKU available for given sku id:" + pSkuId);
                            throw new BBBBusinessException(BBBCatalogErrorCodes.NO_ECO_FEE_SKU_AVAILABLE_FOR_SKU,
                                            BBBCatalogErrorCodes.NO_ECO_FEE_SKU_AVAILABLE_FOR_SKU);
                        }
                    } else {
                        this.logDebug("In Exception Sku is disabled no longer available");
                        throw new BBBBusinessException(
                                        BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                                        BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
                    }

                } else {
                    this.logDebug("Repository Item is null");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } else {
                this.logDebug("input parameters State ID and Sku ID is null");
                throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                                BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getEcoFeeSKUDetailForState]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
        this.logDebug(pEcoFeeSKUVO.toString());
        this.logDebug("Catalog API Method Name [getEcoFeeSKUDetailForState] pStateId ["+pStateId+"] pSkuId ["+pSkuId+"] Exit");
        return pEcoFeeSKUVO;
    }
    
    
    /**
     *  This method is used to validate if sku is active in the catalog or not For the sku to be active weboffered should
     * be true and disable should be false Also if start and end date are not null then current date should be after
     * start date and before end date. As part of instant preview on staging a new preview date is introduced.This will
     * work only in staging The user will enter the preview date against which the start and end date will be tested. in
     * all other environments the current date will be the preview date
     *
     * @param skuRepositoryItem the sku repository item
     * @param value the value
     * @return SKU State
     */
    public boolean isSkuActive(final RepositoryItem skuRepositoryItem,String ... value) {
        // Edited as part of Instant preview story
		String isInternationalUser="false";
    	if(value.length==1)
    	{
    		isInternationalUser=value[0];
    	}
        Date previewDate = new Date();
        if (this.isPreviewEnabled()) {
            previewDate = this.getPreviewDate();
            this.logDebug("Preview is enabled Value of Preview Date " + previewDate.toString());
        }
        DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        String channelApp = "";
        if(null!=request)
    	{
    	  channelApp = request.getHeader(BBBCoreConstants.CHANNEL);
    	}
		
        if (skuRepositoryItem != null) {
			if(Boolean.valueOf(isInternationalUser))
        	{
        	String isIntlRestricted = BBBCoreConstants.NO_CHAR;
            if (!BBBUtility.isEmpty((String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED))) {
            	isIntlRestricted  = (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
            	if(BBBCoreConstants.YES_CHAR.equalsIgnoreCase(isIntlRestricted))
            	{
            		return false;
            	}
			}
        	}
            final Date startDate = (Date) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME);
            final Date endDate = (Date) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME);
            boolean webOffered = false;
            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
                webOffered = ((Boolean) skuRepositoryItem
                                .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)).booleanValue();
                if (!webOffered && !StringUtils.isBlank(channelApp)
				&& channelApp
						.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)) {
                	return true;
            }
            }
            this.logTrace("SKU startDate[" + startDate + "]");
            this.logTrace("SKU endDate[" + endDate + "]");
            if(((endDate != null) && previewDate.after(endDate)) || ((startDate != null) && previewDate
					.before(startDate))){
            	return false;
            }
            boolean disable = true;
            if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) != null) {
                disable = ((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME))
                                .booleanValue();
            }
                this.logTrace("SKU disable[" + disable + "]");
                this.logTrace("Product webOffered[" + webOffered + "]");
           if (disable || !webOffered) {
                return false;
            }
            return true;
        }
        return false;
    }

    
    /**
     *  the method returns the product id of the first parent of the sku that is active.
     *
     * @param pSkuId the sku id
     * @return First Active Parent
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final String getFirstActiveParentProductForInactiveSKU(final String pSkuId)
                    throws BBBBusinessException, BBBSystemException {
        final String methodName = "getFirstActiveParentProductForInactiveSKU(String pSkuId)";
        boolean parentActiveProductFound = false;
        try {
            if ((null != pSkuId) && !StringUtils.isEmpty(pSkuId)) {
                    this.logDebug("Entering  " + methodName + " with SkuId: " + pSkuId);
                final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,
                                BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
                if (null != skuRepositoryItem) {
                        final StringBuilder logDebug = new StringBuilder();
                        logDebug.append("Sku ").append(pSkuId).append(" is Active");
                        this.logDebug(logDebug.toString());
                    @SuppressWarnings ("unchecked")
                    final Set<RepositoryItem> parentProductList = (Set<RepositoryItem>) skuRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME);
                    if ((parentProductList != null) && !parentProductList.isEmpty()) {
                        for (final RepositoryItem parentProductItem : parentProductList) {
                            if (null != (parentProductItem.getRepositoryId())) {
                                parentActiveProductFound = true;
                                return parentProductItem.getRepositoryId();
                            }
                        }
                        if (!parentActiveProductFound) {
                                this.logDebug("SKU Doesnt belong to any ACTIVE Product, sku id:" + pSkuId);
                            throw new BBBBusinessException(BBBCatalogErrorCodes.NO_ACTIVE_PRODUCT_FOR_SKU);
                        }
                    } else {
                            this.logDebug("SKU Doesnt belong to any Product, sku id:" + pSkuId);
                        throw new BBBBusinessException(BBBCatalogErrorCodes.NO_PRODUCT_FOR_SKU);
                    }

                } else {
                    this.logDebug("Repository Item is null for sku : " + pSkuId);
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } else {
                this.logDebug("input parameter SKUId id is null");
                throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getFirstActiveParentProductForSKU]: RepositoryException ",e);
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }

        return null;
    }
    
    
    /**
     *  This method is to identify whether it is a preview site.
     *
     * @return boolean
     */
    public final boolean isPreviewEnabled() {
    	if(null != this.previewEnabled) {
    		return this.previewEnabled.booleanValue();
    	}
    	else {
    		boolean tempValue = false;
	        try {
	            final List<String> values = this.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,
	                            BBBCatalogConstants.PREVIEW_ENABLED);
	            if ((values != null) && !values.isEmpty()) {
	            	tempValue = BBBCatalogConstants.TRUE.equalsIgnoreCase(values.get(0)) ? true : false;
	            }
	        } catch (final BBBSystemException e) {
	            this.logError("catalog_1023 :" + KEY_NOT_FOUND_FOR_PREVIEW_ENABLED + e);
	        } catch (final BBBBusinessException e) {
	            this.logError("catalog_1024:" + KEY_NOT_FOUND_FOR_PREVIEW_ENABLED + e);
	        }
	        this.setPreviewEnabled(tempValue);
	        return tempValue;
    	}
    }
    
    
    public void setPreviewEnabled(boolean mPreviewEnabled) {
		this.previewEnabled = Boolean.valueOf(mPreviewEnabled);
	}
    
    
    /**
     *  The method gets the Preview date against which thek of start and end date check will be made for Preview by
     * business users As PreviewAttributes is a request scope so its resolved and not injected.
     *
     * @return Preview Date
     */

    public final Date getPreviewDate() {
        final PreviewAttributes previewAttributes = (PreviewAttributes) ServletUtil.getCurrentRequest().resolveName(
                        "/com/bbb/commerce/catalog/PreviewAttributes");
        Date previewDate = new Date();
        if ((null != previewAttributes) && (previewAttributes.getPreviewDate() != null)) {
            previewDate = previewAttributes.getPreviewDate();
        }
        return previewDate;
    }
    
    
    /**
	 *  The method executes the business logic to determine if Customization is Offered For sku on this site or not.
	 *
	 * @param skuRepositoryItem the sku repository item
	 * @param siteId the site id
	 * @return true, if is customization offered for sku
	 */
	 public boolean isCustomizationOfferedForSKU(final RepositoryItem skuRepositoryItem , final String siteId) {
	    boolean isCustomizationOffered = false;
	    String customizationOfferedFlag = this.getCustomizationOfferedSiteMap().get(siteId);
	    if(null != skuRepositoryItem.getPropertyValue(customizationOfferedFlag)){
	    	isCustomizationOffered = ((Boolean) skuRepositoryItem.getPropertyValue(customizationOfferedFlag)).booleanValue();
	    }
	    this.logDebug("Catalog API Method Name [isCustomizationOfferedForSKU] siteId [ " + siteId
	    		+ "] skuId [" + skuRepositoryItem.getRepositoryId() + "] isCustomizationOffered [ " + isCustomizationOffered);
	    return isCustomizationOffered;
	 }
	 
	 /* (non-Javadoc)
	     * @see com.bbb.commerce.catalog.BBBCatalogTools#getStoreSpecialityList(java.util.Set)
	     */
	    public List<StoreSpecialityVO> getStoreSpecialityList(final Set<RepositoryItem> specialityItemSet) {
	        final List<StoreSpecialityVO> storeSpecialityVOList = new ArrayList<StoreSpecialityVO>();
	        for (final RepositoryItem specialityItem : specialityItemSet) {
	            final StoreSpecialityVO storeSpecialityVO = new StoreSpecialityVO();
	            storeSpecialityVO.setSpecialityCodeId(specialityItem.getRepositoryId());

	            if (specialityItem.getPropertyValue(BBBCatalogConstants.SPECIALITY_CODE_NAME_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setSpecialityCode((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.SPECIALITY_CODE_NAME_STORE_PROPERTY_NAME));
	            }

	            if (specialityItem.getPropertyValue(BBBCatalogConstants.CODE_IMAGE_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setCodeImage((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.CODE_IMAGE_STORE_PROPERTY_NAME));
	            }
	            if (specialityItem.getPropertyValue(BBBCatalogConstants.PRIORITY_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setPriority(((Integer) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.PRIORITY_STORE_PROPERTY_NAME)).intValue());
	            }
	            if (specialityItem.getPropertyValue(BBBCatalogConstants.STORE_LIST_ALT_TEXT_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setStoreListAltTxt((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.STORE_LIST_ALT_TEXT_STORE_PROPERTY_NAME));
	            }
	            if (specialityItem.getPropertyValue(BBBCatalogConstants.STORE_LIST_TITLE_TEXT_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setStoreListTitleTxt((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.STORE_LIST_TITLE_TEXT_STORE_PROPERTY_NAME));
	            }
	            if (specialityItem.getPropertyValue(BBBCatalogConstants.IMG_LOC_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setImg2ImgLocation((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.IMG_LOC_STORE_PROPERTY_NAME));
	            }
	            if (specialityItem.getPropertyValue(BBBCatalogConstants.IMG_ALT_TEXT_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setImg2AltText((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.IMG_ALT_TEXT_STORE_PROPERTY_NAME));
	            }
	            if (specialityItem.getPropertyValue(BBBCatalogConstants.IMG_TITLE_TEXT_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setImg2TitleText((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.IMG_TITLE_TEXT_STORE_PROPERTY_NAME));
	            }
	            if (specialityItem.getPropertyValue(BBBCatalogConstants.LEGEND_FILE_LOC_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setLegendFileLocation((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.LEGEND_FILE_LOC_STORE_PROPERTY_NAME));
	            }
	            if (specialityItem.getPropertyValue(BBBCatalogConstants.LEGEND_ALT_TEXT_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setLegendAltText((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.LEGEND_ALT_TEXT_STORE_PROPERTY_NAME));
	            }
	            if (specialityItem.getPropertyValue(BBBCatalogConstants.LEGEND_TITLE_TEXT_STORE_PROPERTY_NAME) != null) {
	                storeSpecialityVO.setLegendTitleText((String) specialityItem
	                                .getPropertyValue(BBBCatalogConstants.LEGEND_TITLE_TEXT_STORE_PROPERTY_NAME));
	            }

	            storeSpecialityVOList.add(storeSpecialityVO);
	        }
	        return storeSpecialityVOList;
	    }
	 
	 
	 /**
		 * @return the customizationOfferedSiteMap
		 */
		public Map<String, String> getCustomizationOfferedSiteMap() {
			return customizationOfferedSiteMap;
		}

		/**
		 * @param customizationOfferedSiteMap the customizationOfferedSiteMap to set
		 */
		public void setCustomizationOfferedSiteMap(
				Map<String, String> customizationOfferedSiteMap) {
			this.customizationOfferedSiteMap = customizationOfferedSiteMap;
		}
	 
	 
    
    
    
    
    
    
    
    
    
   
	
	
	
	

}
