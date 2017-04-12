/**
 * 
 */
package com.bbb.cms.tools;

import java.util.Set;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

import org.apache.commons.lang.StringUtils;

/**
 * @author vagra4
 * 
 */
public class CmsTools extends BBBGenericService {

	private Repository mStaticTemplateRepository;

	private Repository mShippingRepository;
	
	private Repository mSiteRepository;

	private String deliverySurchargeRqlQuery;
	
	private String deliverySurchargeMaxRangeRqlQuery;

	public String getDeliverySurchargeMaxRangeRqlQuery() {
		return deliverySurchargeMaxRangeRqlQuery;
	}

	public void setDeliverySurchargeMaxRangeRqlQuery(
			String deliverySurchargeMaxRangeRqlQuery) {
		this.deliverySurchargeMaxRangeRqlQuery = deliverySurchargeMaxRangeRqlQuery;
	}

	public String getDeliverySurchargeRqlQuery() {
		return deliverySurchargeRqlQuery;
	}

	public void setDeliverySurchargeRqlQuery(String deliverySurchargeRqlQuery) {
		this.deliverySurchargeRqlQuery = deliverySurchargeRqlQuery;
	}

	/**
	 * @return the mSiteRepository
	 */
	public Repository getSiteRepository() {
		return mSiteRepository;
	}

	/**
	 * @param mSiteRepository the mSiteRepository to set
	 */
	public void setSiteRepository(Repository mSiteRepository) {
		this.mSiteRepository = mSiteRepository;
	}

	/**
	 * @return instance of StaticTemplateRepository
	 */
	public Repository getStaticTemplateRepository() {
		return mStaticTemplateRepository;
	}

	/**
	 * @param pStaticTemplateRepository
	 *            the mStaticTemplateRepository to set
	 */
	public void setStaticTemplateRepository(Repository pStaticTemplateRepository) {
		mStaticTemplateRepository = pStaticTemplateRepository;
	}

	
	/**
	 * This method returns static page data based on siteId and pageName.
	 * 
	 * @param pSiteId
	 * @param pPageName
	 * @return RepositoryItem
	 * @throws RepositoryException
	 */
	public RepositoryItem getStaticTemplateData(String pSiteId, String pPageName,String pbabyCAMode)
			throws RepositoryException {

		
		logDebug("starting method CmsTools.getStaticTemplateData, Passed parameters: "
					+ "pSiteId=" + pSiteId + ", pPageName=" + pPageName);

		
		RepositoryItem staticPageDetail = null;
		RepositoryView view = getStaticTemplateRepository().getView(
				BBBCmsConstants.SITE_STATIC_PAGE);

		RqlStatement statement = RqlStatement
				.parseRqlStatement(BBBCmsConstants.STATIC_TEMPLATE_QUERY);

		Object params[] = new Object[2];
		params[0] = pSiteId;
		params[1] = pPageName;

		RepositoryItem[] siteStaticPageItems = null;

		try {
			siteStaticPageItems = statement.executeQuery(view, params);
		} catch (IllegalArgumentException iLLArgExp) {

			logError(LogMessageFormatter.formatMessage(null, "getStaticTemplateData:","catalog_1065" ),iLLArgExp);
			
	
			siteStaticPageItems = null;
		}

		if (siteStaticPageItems != null) {
			staticPageDetail = siteStaticPageItems[0];
		} else {
			
			if(StringUtils.equalsIgnoreCase(pbabyCAMode,BBBCoreConstants.TRUE) && pPageName.endsWith(BBBCmsConstants.BABY_CA)){
				pPageName = pPageName.substring(0, pPageName.lastIndexOf(BBBCmsConstants.BABY_CA));
								
				params[1] = pPageName;
				try{
					siteStaticPageItems = statement.executeQuery(view, params);
					if (siteStaticPageItems == null)				{
						
						logDebug("CmsTools.getStaticTemplateDataThere is no page with the name" + pPageName);					
					}
					else
					{	
						staticPageDetail = siteStaticPageItems[0];
					}
				}catch (IllegalArgumentException iLLArgExp) {
					logError(LogMessageFormatter.formatMessage(null, "getStaticTemplateData:","catalog_1065" ),iLLArgExp);	
					siteStaticPageItems = null;
				}
				
				
			}
		}
		if(siteStaticPageItems == null){
			logDebug("CMSTools :: New page is requested");

			RqlStatement newStatement = RqlStatement.parseRqlStatement(BBBCmsConstants.STATIC_NEW_TEMPLATE_QUERY);
			Object newparams[] = new Object[3];
			newparams[0] = pSiteId;
			newparams[1] = "Others";
			newparams[2] = pPageName;
			
			
			try {
				siteStaticPageItems = newStatement.executeQuery(view, newparams);
				if (siteStaticPageItems == null)				{
				
					logDebug("CmsTools.getStaticTemplateDataThere is no page with the name" + pPageName);					
				}
				else
				{	
					staticPageDetail = siteStaticPageItems[0];
				}
			} catch (IllegalArgumentException iLLArgExp) {
				
				
				logError(LogMessageFormatter.formatMessage(null, "getStaticTemplateData:","catalog_1066" ),iLLArgExp);
					
				siteStaticPageItems = null;
			}
		}
		
		logDebug("Exiting method CmsTools.getStaticTemplateData");
		
		return staticPageDetail;
	}

	/**
	 * This method returns all the shipping methods.
	 * 
	 * @return RepositoryItem[], Array of RepositoryItems.
	 * @throws RepositoryException
	 */
	@SuppressWarnings("unchecked")
	public RepositoryItem[] getShippingMethods(String siteId) throws RepositoryException {
		
		logDebug("starting method CmsTools.getShippingMethods");

		
		RepositoryItem[] shippingMethods = null;

		RepositoryItem siteItem = getSiteRepository().getItem(siteId, BBBCmsConstants.SITE_ITEM_DESCRIPTOR);
		
		if(null!=siteItem){
			Set<RepositoryItem> applicableShipMethodsSet = (Set<RepositoryItem>) siteItem.getPropertyValue(BBBCmsConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME);
			shippingMethods = new RepositoryItem[applicableShipMethodsSet.size()];
			int i = 0;
			for (RepositoryItem repositoryItem : applicableShipMethodsSet) {
				shippingMethods[i] = repositoryItem;
				i++;
			}			
		}

		
		logDebug("starting method CmsTools.getShippingMethods");

	

		return shippingMethods;
	}

	/**
	 * This method returns all the shipping methods prices for a specific site
	 * which is passed as the parameter.
	 * 
	 * @return RepositoryItem[], Array of RepositoryItems.
	 * @throws RepositoryException
	 */
	public RepositoryItem[] getAllShippingPriceDetails(String pSiteId)
			throws RepositoryException {

		
		logDebug("starting method CmsTools.getAllShippingPriceDetails");

		RepositoryItem[] shippingMethodPrices = null;

		RepositoryView view = getShippingRepository().getView(
				BBBCmsConstants.SHIPPING_METHOD_PRICES);

		RqlStatement statement;

		statement = RqlStatement
				.parseRqlStatement(BBBCmsConstants.SHIPPING_PRICE_QUERY);
		Object params[] = new Object[1];
		params[0] = pSiteId;

		shippingMethodPrices = statement.executeQuery(view, params);

		
		logDebug("starting method CmsTools.getAllShippingPriceDetails");

		

		return shippingMethodPrices;
	}
	
	public RepositoryItem[] getAllShippingPriceDetailsAsc(String pSiteId) throws RepositoryException {
		
		logDebug("starting method CmsTools.getAllShippingPriceDetails");

		RepositoryItem[] shippingMethodPrices = null;

		RepositoryView view = getShippingRepository().getView(BBBCmsConstants.SHIPPING_METHOD_PRICES);
		
		String queryStatement = "site = ?0 AND (giftCard IS NULL OR  giftCard = 0) AND (shipMethodCode = ?1 OR shipMethodCode= ?2 OR shipMethodCode = ?3)  ORDER BY lowerLimit SORT ASC";

		RqlStatement statement = RqlStatement.parseRqlStatement(queryStatement);
		Object params[] = new Object[4];
		params[0] = pSiteId;
		params[1] = BBBCmsConstants.SHIP_METHOD_CODE_1A;
		params[2] = BBBCmsConstants.SHIP_METHOD_CODE_2A;
		params[3] = BBBCmsConstants.SHIP_METHOD_CODE_3G;
		shippingMethodPrices = statement.executeQuery(view, params);

		
		logDebug("The Shipping Method Prices are " + shippingMethodPrices + ".");
		logDebug("ending method CmsTools.getAllShippingPriceDetails");


		return shippingMethodPrices;
	}

	/**
	 * @return the instance of ShippingRepository
	 */
	public Repository getShippingRepository() {
		return mShippingRepository;
	}

	/**
	 * @param pShippingRepository
	 *            the mShippingRepository to set
	 */
	public void setShippingRepository(Repository pShippingRepository) {
		mShippingRepository = pShippingRepository;
	}

	/**
	 * This method returns all surcharge prices based on sku weight and site id for different shipping methods.
	 * 
	 * @param skuWeight
	 * @param pSiteId
	 * @return RepositoryItem
	 * @throws RepositoryException
	 */
	public RepositoryItem[] getAllSurchargePrice(Double skuWeight, String pSiteId) throws BBBBusinessException, BBBSystemException
	{
		logDebug("CmsTools.getAllSurchargePrice : Start");
		logDebug("Sku Weight="+skuWeight+" Site="+pSiteId);
		
		if(null == skuWeight || null == pSiteId)
		{
			logError("Either sku or site id is null");
			throw new BBBBusinessException(BBBCatalogErrorCodes.NO_SKU_ID_NO_SITE_ID, "Either sku or site id is null");
			
}
		RepositoryItem[] deliverySurchargePrices = null;

		try{
			RepositoryView view = getShippingRepository().getView(BBBCmsConstants.DELIVERY_SURCHARGE);

			RqlStatement statement = RqlStatement.parseRqlStatement(deliverySurchargeRqlQuery);
			Object params[] = new Object[2];
			params[0] = pSiteId;
			params[1] = skuWeight;
			deliverySurchargePrices = statement.executeQuery(view, params);

			if(null == deliverySurchargePrices){
				statement = RqlStatement.parseRqlStatement(deliverySurchargeMaxRangeRqlQuery);
				deliverySurchargePrices = statement.executeQuery(view, params);
			}
			
		}catch (RepositoryException e){
			throw new BBBSystemException(BBBCatalogErrorCodes.NO_DATA_FOUND_FOR_DELIVERY_SURCHARGE, "Error occured while fetching surcharge details against a weight");
		}
				
		logDebug("CmsTools.getAllSurchargePrice : End");

		return deliverySurchargePrices;
	}
    
	
	  public double getDeliveryCharge(final String siteId, final Double skuWeight, String shippingMethodCode) throws BBBBusinessException, BBBSystemException{
			this.logDebug("Entering CmsTools.getDeliveryCharge() method");		
			double deliveryCharge = 0.0;
			if(null == skuWeight || null == siteId)
			{
				logError("Either sku or site id is null");
				throw new BBBBusinessException(BBBCatalogErrorCodes.NO_SKU_ID_NO_SITE_ID, "Either sku or site id is null");
				
	        }  
			if(shippingMethodCode.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)) {
				shippingMethodCode = BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD;
        	}
			RepositoryItem[] deliverySurchargePrices = null;

			try{
				RepositoryView view = getShippingRepository().getView(BBBCmsConstants.DELIVERY_SURCHARGE);

				RqlStatement statement = RqlStatement.parseRqlStatement(deliverySurchargeRqlQuery + "AND shipMethodCode = ?2");
				Object params[] = new Object[3];
				params[0] = siteId;
				params[1] = skuWeight;
				params[2] = shippingMethodCode;
				deliverySurchargePrices = statement.executeQuery(view, params);

				if(null == deliverySurchargePrices){
					statement = RqlStatement.parseRqlStatement(deliverySurchargeMaxRangeRqlQuery + "AND shipMethodCode = ?2");
					deliverySurchargePrices = statement.executeQuery(view, params);
				}
				if(deliverySurchargePrices != null){
					deliveryCharge = (Double) deliverySurchargePrices[0].getPropertyValue(BBBCatalogConstants.DELIVERY_SURCHARGE_PROPERTY_NAME);
				}
			}catch (RepositoryException e){
				throw new BBBSystemException(BBBCatalogErrorCodes.NO_DATA_FOUND_FOR_DELIVERY_SURCHARGE, "Error occured while fetching surcharge details against a weight");
			}
			
			return deliveryCharge;
		
		}
}
