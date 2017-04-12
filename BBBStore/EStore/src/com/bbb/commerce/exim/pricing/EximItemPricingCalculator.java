package com.bbb.commerce.exim.pricing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.ItemPriceCalculator;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.exim.bean.ErrorVO;
import com.bbb.commerce.exim.bean.EximCustomizedAttributesVO;
import com.bbb.commerce.exim.bean.EximImagePreviewVO;
import com.bbb.commerce.exim.bean.EximImagesVO;
import com.bbb.commerce.exim.bean.EximSummaryResponseVO;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBItemPriceInfo;
import com.bbb.utils.BBBUtility;

/**
 * @author BBB
 *
 */
public class EximItemPricingCalculator extends ItemPriceCalculator {
	

	private static final String EXIM_PRICE_ADJUSTMENT_DESCRIPTION = "eximPriceAdjustmentDescription";
	private static final String DEFAULT_LOCALE = "EN";
	private BBBEximManager eximPricingManager;
	private Map<String, String> customizationOfferedSiteMap;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBCatalogTools bbbCatalogTools;
		
	
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	/**
	 * Gets the lbl txt template manager.
	 *
	 * @return the lbl txt template manager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	/**
	 * Sets the lbl txt template manager.
	 *
	 * @param lblTxtTemplateManager the new lbl txt template manager
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/**
	 * @return the customizationOfferedSiteMap
	 */
	public Map<String, String> getCustomizationOfferedSiteMap() {
		return this.customizationOfferedSiteMap;
	}

	/**
	 * @param customizationOfferedSiteMap the customizationOfferedSiteMap to set
	 */
	public void setCustomizationOfferedSiteMap(
			Map<String, String> customizationOfferedSiteMap) {
		this.customizationOfferedSiteMap = customizationOfferedSiteMap;
	}

	/**
	 * @return the eximPricingManager
	 */
	public BBBEximManager getEximPricingManager() {
		return this.eximPricingManager;
	}

	/**
	 * @param eximPricingManager the eximPricingManager to set
	 */
	public void setEximPricingManager(BBBEximManager eximPricingManager) {
		this.eximPricingManager = eximPricingManager;
	}
	
	/**
	 * This method is called to set exim price info to commerce item by making web service call if it is not present already.
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
	throws PricingException {
		
		BBBPerformanceMonitor.start("EximItemPricingCalculator", "priceItem");
		logDebug("Start - EximItemPricingCalculator.priceItem");
		
		// if the request is coming from Pricing web service then skip the calculation and web service call
		if (null == pExtraParameters.get(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER)) {
			
	        double personalizedPrice = 0.0;
	        double rawTotalPrice = 0.0;
	        double unitPrice= 0.0;
	        double totalPrice= 0.0;
	        long quantity = pItem.getQuantity();
	        List detailsList;
	        List newDetails;
	        List adjustments;
	        double adjustAmount = 0.0;
	        PricingAdjustment adjustment;
	        String siteId = extractSiteId();
	        boolean isCustomizationOffered = this.getBbbCatalogTools().isCustomizationOfferedForSKU((RepositoryItem)pItem.getAuxiliaryData().getCatalogRef(), siteId);
	        boolean isEximErrorExist = false;
	        boolean isEximPricingReqd = true;
	        String personalizationType = "";
	        String enableKatoriFlag = getEximPricingManager().getKatoriAvailability();
	       
	        // exim pricing will happen only when site specific customization is true for the sku and katori is enabled across sites
	        if("true".equalsIgnoreCase(enableKatoriFlag) && isCustomizationOffered && 
	        		pItem instanceof BBBCommerceItem && BBBUtility.isNotEmpty(((BBBCommerceItem) pItem).getReferenceNumber())) {
	        	
	        	personalizationType = (String) ((RepositoryItem) pItem.getAuxiliaryData().getCatalogRef()).getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE);
	        	personalizedPrice = ((BBBCommerceItem) pItem).getPersonalizePrice();
	        	isEximErrorExist = ((BBBCommerceItem) pItem).isEximErrorExists();
	        	isEximPricingReqd = ((BBBCommerceItem) pItem).isEximPricingReq();
		        
		        logDebug("personalizationType [" + personalizationType + "] isEximPricingReqd ["
		        			+ isEximPricingReqd + "] personalizedPrice [" + personalizedPrice);
		        
	        	// If exim pricingrequired flag is true, then only call exim webservice, else check exim details map is not null. 
	        	//If not null, set the exim details from map. Map is used in anonymous user flow while creating CI from cookie
	        	if(isEximPricingReqd){
					try {
						//Throwing exception if Exim Webservice Fails|BPSI-3240
						personalizedPrice = getEximPersonalizeDetails(((BBBCommerceItem) pItem),
								personalizedPrice, siteId);
					} catch (BBBBusinessException businessException) {
						((BBBCommerceItem) pItem).setEximErrorExists(true);
						throw new PricingException(getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.ERROR_ADD_TO_CART_EXIM, DEFAULT_LOCALE, null, null));
					
					} catch (BBBSystemException e) {
						((BBBCommerceItem) pItem).setEximErrorExists(true);
						throw new PricingException(getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.ERROR_ADD_TO_CART_EXIM, DEFAULT_LOCALE, null, null));
					}
					((BBBCommerceItem) pItem).setEximPricingReq(false);
	        	
	        	} else if(null != pExtraParameters.get(BBBCoreConstants.EXIM_DETAILS_MAP)){
	        		
	        		Map<String,EximCustomizedAttributesVO> eximDetailMap = (Map<String,EximCustomizedAttributesVO>)pExtraParameters.get(BBBCoreConstants.EXIM_DETAILS_MAP);
	        		String refNum = ((BBBCommerceItem) pItem).getReferenceNumber();
	        		//Set the exim details from map only if reference number exists in map
	        		if(eximDetailMap.containsKey(refNum)){
	        			personalizedPrice = getEximPricingManager().setEximPersonalizeDetailsInCI(((BBBCommerceItem) pItem),
	        				eximDetailMap.get(refNum));
	        		}
	        	}
	        	if(((BBBCommerceItem) pItem).getReferenceNumber()!=null && !BBBUtility.isEmpty(((BBBCommerceItem) pItem).getRegistryId())){
	        			try {		        			
							getEximPricingManager().setModerateImageURL(((BBBCommerceItem) pItem));
						} catch (BBBSystemException e) {
							logError("Unable to set the moderated image in the order",e);
						} catch (BBBBusinessException e) {
							logError("Unable to set the moderated image in the order",e);
						}
	        	}
	        	
	        	// In case of an Exim error, set the List/Sale price of CI from Prev Price to avoid Price Change message
	        	// On frontend the price will be displayed as TBD
	        	if(isEximErrorExist){
	        		unitPrice = ((BBBCommerceItem) pItem).getPrevPrice();
	        		if (pPriceQuote.getSalePrice() > 0.0) {
		        		pPriceQuote.setSalePrice(unitPrice);
	        		}
		        		pPriceQuote.setListPrice(unitPrice);
	        	}else{
			        //personlized price will vary depending upon the code associated with SKU
			        // if code is CR , then exim price is the final price
			        if (BBBCoreConstants.PERSONALIZATION_CODE_CR.equalsIgnoreCase(personalizationType)) {
			        	
			        	unitPrice = personalizedPrice;
			        	pPriceQuote.setListPrice(unitPrice);
			        	if (pPriceQuote.getSalePrice() > 0.0) {
			        		pPriceQuote.setSalePrice(unitPrice);
			        	}
			        	
			        	
			        } else if (BBBCoreConstants.PERSONALIZATION_CODE_PY.equalsIgnoreCase(personalizationType)) {
			        	// if code is PY , then add the price coming from exim response
			        	unitPrice = pPriceQuote.getListPrice() + personalizedPrice;
			        	pPriceQuote.setListPrice(unitPrice);
			        	if (pPriceQuote.getSalePrice() > 0.0) {
			        		unitPrice = pPriceQuote.getSalePrice() + personalizedPrice;
			        		pPriceQuote.setSalePrice(unitPrice);
			        	} 			        	
			        	
			        } else {
			        	// if personalization type is PB , then dont use exim personalized price
			        	unitPrice = pPriceQuote.getListPrice();
		        		pPriceQuote.setListPrice(unitPrice);
			        	if (pPriceQuote.getSalePrice() > 0.0) {
			        		unitPrice = pPriceQuote.getSalePrice();
			        	} 
				    }
	        	}
	        	
		        rawTotalPrice=  quantity * pPriceQuote.getListPrice();
				rawTotalPrice = getPricingTools().round(rawTotalPrice);
				
				totalPrice=  quantity * unitPrice;
				totalPrice = getPricingTools().round(totalPrice);
				
				if (pPriceQuote instanceof BBBItemPriceInfo) {
					((BBBItemPriceInfo) pPriceQuote).setPersonalizedPrice(personalizedPrice);
				}
				
				pPriceQuote.setRawTotalPrice(rawTotalPrice);
				pPriceQuote.setAmount(totalPrice);
		
				detailsList = pPriceQuote.getCurrentPriceDetails();
				if(detailsList.size() > 0){
					detailsList.clear();
				}
				
				logDebug("price quote: " + pPriceQuote);
				
				newDetails = getPricingTools().getDetailedItemPriceTools().createInitialDetailedItemPriceInfos(totalPrice, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, EXIM_PRICE_ADJUSTMENT_DESCRIPTION);
				detailsList.addAll(newDetails);
					
				adjustments = pPriceQuote.getAdjustments();
				/*if(adjustments.size() > 0){
					adjustments.clear();
				}*/
      
				adjustAmount = pPriceQuote.getAmount();
				adjustment = new PricingAdjustment(EXIM_PRICE_ADJUSTMENT_DESCRIPTION, null, getPricingTools().round(adjustAmount), pItem.getQuantity());
				adjustments.add(adjustment);
				logDebug("adjustment: " + adjustment + " adjustAmount: " + adjustAmount);
			
	        }
        
		}
			
		logDebug("End- EximItemPricingCalculator.priceItem");
		
		BBBPerformanceMonitor.end("EximItemPricingCalculator", "priceItem");
		
	}

	protected String extractSiteId() {
		String siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}

	
	/**
	 * The method invokes the summary API on the basis of CI's reference number and set the reponse in the respective
	 * commerceItem properties
	 * @param pItem
	 * @param unitPersonalizedFee
	 * @param siteId
	 * @return personalizedPrice
	 * @throws Exception 
	 * @throws PricingException 
	 */
	public double getEximPersonalizeDetails(BBBCommerceItem pItem,
			double unitPersonalizedFee, String siteId) throws BBBSystemException, BBBBusinessException {
		
		logDebug("EximItemPricingCalculator.getEximPersonalizeDetails starts");		
		String refNumParam = pItem.getReferenceNumber();
		EximSummaryResponseVO eximResponse = null;
		EximCustomizedAttributesVO eximAtrributes = null;
		double eximPersonlizedPrice = unitPersonalizedFee;
		Date date = new Date();
		logDebug("ref num :: " + refNumParam + " is associated with commerce item :: " + pItem.getId());
		
		eximResponse = getEximPricingManager().invokeSummaryAPI((String) ((RepositoryItem) pItem.getAuxiliaryData().getProductRef()).getPropertyValue("Id"),null, refNumParam);
        if (null != eximResponse && null != eximResponse.getCustomizations() && !eximResponse.getCustomizations().isEmpty()) {
		eximAtrributes = eximResponse.getCustomizations().get(0);
		if (null != eximAtrributes && eximAtrributes.getErrors().isEmpty()) {
			logDebug("eximAtrributes error :: " + eximAtrributes.getErrors() + " and eximPersonlizedPrice :: " + eximAtrributes.getRetailPriceAdder());
			eximPersonlizedPrice = eximAtrributes.getRetailPriceAdder();
			pItem.setPersonalizePrice(eximAtrributes.getRetailPriceAdder());
			pItem.setPersonalizeCost(eximAtrributes.getCostPriceAdder());
			pItem.setPersonalizationDetails(eximAtrributes.getNamedrop());
			pItem.setPersonalizationOptions(eximAtrributes.getCustomizationService());
			//BPSI-5386 Personalization option single code display			
			pItem.setPersonalizationOptionsDisplay(getEximPricingManager().getPersonalizedOptionsDisplayCode(pItem.getPersonalizationOptions()));
			pItem.setMetaDataFlag(eximAtrributes.getMetadataStatus());
			pItem.setMetaDataUrl(eximAtrributes.getMetadataUrl());
			pItem.setModerationFlag(eximAtrributes.getModerationStatus());
			pItem.setModerationUrl(eximAtrributes.getModerationUrl());
			List<EximImagesVO> images = eximAtrributes.getImages();
			for(EximImagesVO imageVO : images) {
				if(imageVO.getId().equalsIgnoreCase(BBBCoreConstants.IMAGE_ID) || imageVO.getId().equalsIgnoreCase("")) {
				List<EximImagePreviewVO> previews = imageVO.getPreviews();
				  for (EximImagePreviewVO preview : previews) { 
					   if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_LARGE)) 
					      pItem.setFullImagePath(preview.getUrl()+"?v="+date.getTime());
					   if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_X_SMALL)) {
						pItem.setThumbnailImagePath(preview.getUrl()+"?v="+date.getTime());
					    pItem.setMobileThumbnailImagePath(preview.getUrl()+"?v="+date.getTime());
					   }
					   if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_SMALL))
						pItem.setMobileFullImagePath(preview.getUrl()+"?v="+date.getTime());
					}
				  break;
				}
			}
			
		}
        else if(null != eximAtrributes && null != eximAtrributes.getErrors()) {
			ErrorVO eximResponseError = eximAtrributes.getErrors().get(0);
			this.logError(BBBCoreConstants.EXIM_ADDTOCART_ERROR + eximResponseError.getMessage());
			throw new BBBBusinessException(eximResponseError.getCode(), eximResponseError.getMessage());
		} 
        }else if(null == eximResponse || null == eximResponse.getCustomizations() || eximResponse.getCustomizations().isEmpty()) {
			throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1001,
					"Response from web service is NULL");
			
		}
		
		logDebug("EximItemPricingCalculator.getEximPersonalizeDetails ends");
		
		return eximPersonlizedPrice;
	}
	
	/** The method executes the business logic to determine if Customization is Offered For sku on this site or not 
	 * @param pItem 
	 * @param siteId 
	 * @return isCustomizationOffered
	 *//*
    public final boolean isCustomizationOfferedForSKU(final CommerceItem pItem , final String siteId) {
    	
    	boolean isCustomizationOffered = false;
    	String customizationOfferedFlag = this.getCustomizationOfferedSiteMap().get(siteId);
    	if(null != ((RepositoryItem) pItem.getAuxiliaryData().getCatalogRef()).getPropertyValue(customizationOfferedFlag)){
        	isCustomizationOffered = ((Boolean) ((RepositoryItem) pItem.getAuxiliaryData().getCatalogRef()).getPropertyValue(customizationOfferedFlag)).booleanValue();
        }
    	this.logDebug("EximItemPricingCalculator.isCustomizationOfferedForSKU:: pItem [ " + 
    			pItem.getCatalogId() + "] siteId " + siteId + "] isCustomizationOffered [ " + isCustomizationOffered);
        return isCustomizationOffered;
    }
*/
	@Override
	public void logDebug(String msg){
		if (isLoggingDebug()) {
			super.logDebug(msg);
		}
	}
	
}
