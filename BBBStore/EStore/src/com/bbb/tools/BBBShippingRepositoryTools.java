package com.bbb.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * @author Kumar Magudeeswaran
 * This class is created to refactor the code from BBBCatalogToolsImpl
 * Methods accessing Shipping repository are placed in this class from BBBCatalogToolsImpl
 *  
 */
public class BBBShippingRepositoryTools extends BBBConfigToolsImpl {
	
	/** Constant for string hyphen.	 */
	private static final String HYPHEN = " - ";
	/** Constant for string days. */
	private static final String DAYS = "days";
	/** Constant for string weeks. */
	private static final String WEEKS = "weeks";
	/** Constant for string vdc unit type. */
	private static final String VDC_UNIT_TYPE = "vdcUnitType";
	/** Constant for string vdc ship time message. */
	private static final String VDC_SHIP_TIME_MSG = "vdcShipTimeMsg";
	
	/** Instance for shipping repository. */
	private MutableRepository shippingRepository;
	/** Instance for GloblaRepositoryTools. */
	private GlobalRepositoryTools globalRepositoryTools;
	/** Variable for ShippingFeeRqlQuery. */
	private String shippingFeeRqlQuery;
	/** Variable for shippingFeeRqlQueryForNullState. */
	private String shippingFeeRqlQueryForNullState;
	/** Variable for sddShipMethodId. */
	private String sddShipMethodId;
	/** Variable for shippingCostRqlQuery. */
	private String shippingCostRqlQuery;
	/** Variable for shippingDurationRqlQuery. */
	private String shippingDurationRqlQuery;
	/** Variable for shippingMethodRqlQuery. */
	private String shippingMethodRqlQuery;
	/** Variable for regionsRqlQuery. */
	private String regionsRqlQuery;
	
        
	/**
     *  If the ShippingMethodId does not exist in the system the method will throw a BBBBusinessException with an error
     * code indicating that the Shipping Method does not exist If the shipping method exists but not applicable for the
     * given state and site combination, method will throw a BBBBusinessException with an error code indicating that the
     * Shipping Method is not applicable for the site The shipping fee is returned through a combination of RQL query
     * with Iterating the Repository Items returned as part of RQL query. Here both ATG repository query-cache and
     * item-cache will be used.
     *
     * @param siteId the site id
     * @param shippingMethodId the shipping method id
     * @param stateId the state id
     * @param subTotalAmount the sub total amount
     * @param regionIdFromOrder the region id from order
     * @return the shipping fee
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    public final double getShippingFee(final String siteId, final String shippingMethodId, final String stateId,
                    final double subTotalAmount, String regionIdFromOrder) throws BBBSystemException, BBBBusinessException {
      
            final StringBuilder debug = new StringBuilder();
            debug.append("Catalog API Method Name [getShippingFee] siteId[").append(siteId)
                            .append("] shippingMethodId[").append(shippingMethodId).append("] stateId[")
                            .append(stateId).append(']');
            this.logDebug(debug.toString());


        double shippingFee = 0.0;
        if ((shippingMethodId != null) && !shippingMethodId.isEmpty() && (siteId != null) && !siteId.isEmpty()) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getShippingFee");
                RepositoryItem[] shippingRepositoryItem = null;
                final Object[] params = new Object[3];
                params[0] = shippingMethodId;
                params[1] = siteId;
                params[2] = stateId;
                if ((stateId != null) && !stateId.isEmpty()) {
                    this.logTrace("Query to be executed for shipping repo is " + this.getShippingFeeRqlQuery());
                    shippingRepositoryItem = this.executeRQLQuery(this.getShippingFeeRqlQuery(), params,
                                    BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,
                                    this.getShippingRepository());
                }

                if (StringUtils.isEmpty(stateId) || (shippingRepositoryItem == null)) {
                    this.logTrace("Query to be executed for shipping repo is "
                                    + this.getShippingFeeRqlQueryForNullState());
                    shippingRepositoryItem = this.executeRQLQuery(this.getShippingFeeRqlQueryForNullState(), params,
                                    BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,
                                    this.getShippingRepository());
                }
                if ((shippingRepositoryItem != null) && (shippingRepositoryItem.length > 0)) {
                    double lowerLimit = 0.0;
                    double upperLimit = 0.0;
                    for (final RepositoryItem element : shippingRepositoryItem) {
                        if (null != element.getPropertyValue(BBBCatalogConstants.LOWER_LIMIT_SHIPPING_PROPERTY_NAME)) {
                            lowerLimit = ((Double) element
                                            .getPropertyValue(BBBCatalogConstants.LOWER_LIMIT_SHIPPING_PROPERTY_NAME))
                                            .doubleValue();
                        }

                        if (null != element.getPropertyValue(BBBCatalogConstants.UPPER_LIMIT_SHIPPING_PROPERTY_NAME)) {
                            upperLimit = ((Double) element
                                            .getPropertyValue(BBBCatalogConstants.UPPER_LIMIT_SHIPPING_PROPERTY_NAME))
                                            .doubleValue();
                        }
                        final StringBuilder trace = new StringBuilder();
						trace.append("low").append("erLimit[").append(lowerLimit).append("] upperLimit[")
								.append(upperLimit).append("] subTotalAmount[").append(subTotalAmount).append("]");
                        this.logTrace(trace.toString());

                        if ((subTotalAmount <= upperLimit)
                                        && (subTotalAmount >= lowerLimit)
                                        && (element.getPropertyValue(BBBCatalogConstants.PRICE_SHIPPING_PROPERTY_NAME) != null)) {
                            
                        	if(shippingMethodId.equals(getSddShipMethodId()) &&  !StringUtils.isBlank(regionIdFromOrder)){
								vlogDebug(
										"BBBShippingRepositoryTools.getShippingFee: Calculating price for SDD shipping method and region : {0}",
										regionIdFromOrder);
                        			if(element.getPropertyValue(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) != null){
                        				@SuppressWarnings("unchecked")
										Set<RepositoryItem> regionsSet = (Set<RepositoryItem>) element.getPropertyValue(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR);
                        				for(RepositoryItem region : regionsSet){
                        					String regionId = region.getRepositoryId();
                        					if(regionId.equals(regionIdFromOrder)){
                        						shippingFee = ((Double) element
                            							.getPropertyValue(BBBCatalogConstants.PRICE_SHIPPING_PROPERTY_NAME))
                            							.doubleValue();
												vlogDebug(
														"BBBShippingRepositoryTools.getShippingFee: Found SDD shipping method price: {0} for region : {1}",
														shippingFee, regionIdFromOrder);
                        						break;
                        					}
                        				}
                        			}
                        		
                        	} else{
								vlogDebug(
										"BBBShippingRepositoryTools.getShippingFee: Calculating price for non-SDD shipping method: {0}",
										shippingMethodId);
                            	shippingFee = ((Double) element
                                        .getPropertyValue(BBBCatalogConstants.PRICE_SHIPPING_PROPERTY_NAME))
                                        .doubleValue();
                            	break;
                            }
                        }
                    }
                } else {
                	   BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getShippingFee");
                    this.logError("catalog_1000: No Valid shipping method ID [" + shippingMethodId + "]");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } finally {
            	debug.append(" Exit");
            	this.logDebug(debug.toString());
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getShippingFee");
            }
        }
        this.logTrace("shippingFee [ " + shippingFee + "]");
        return shippingFee;
    }
    
	
	/**
     *  If the ShippingMethodId is invalid the method will throw a BBBBusinessException with an error code indicating
     * that Shipping Method is not valid. The shipping cost for gift card is retrieved using an RQL query and uses the
     * Repository Query Cache
     *
     * @param siteId the site id
     * @param shippingMethodId the shipping method id
     * @return the double
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final double shippingCostForGiftCard(final String siteId, final String shippingMethodId)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [shippingCostForGiftCard] Parameter siteId[" + siteId
                        + "] Parameter ShippingMethodId[" + shippingMethodId + "]");
        if ((siteId != null) && !StringUtils.isEmpty(siteId) && (shippingMethodId != null)
                        && !StringUtils.isEmpty(shippingMethodId)) {
            RepositoryItem[] shippingRepositoryItem = null;
            final Object[] params = new Object[2];
            params[0] = shippingMethodId;
            params[1] = siteId;
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " shippingCostForGiftCard");
                shippingRepositoryItem = this.executeRQLQuery(this.getShippingCostRqlQuery(), params,
                                BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,
                                this.getShippingRepository());
                if ((shippingRepositoryItem != null)
                                && (shippingRepositoryItem.length > 0)
                                && (null != shippingRepositoryItem[0]
                                                .getPropertyValue(BBBCatalogConstants.PRICE_SHIPPING_PROPERTY_NAME))) {
                    return ((Double) shippingRepositoryItem[0]
                                    .getPropertyValue(BBBCatalogConstants.PRICE_SHIPPING_PROPERTY_NAME)).doubleValue();
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " shippingCostForGiftCard");
                this.logError("catalog_1009: No Valid gift card price for shipping ID [" + shippingMethodId + "]");
                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_GIFT_CARD_FOR_SHIPPING_ID_IN_REPOSITORY,
                                BBBCatalogErrorCodes.NO_GIFT_CARD_FOR_SHIPPING_ID_IN_REPOSITORY);
            } finally {
            	this.logDebug("Catalog API Method Name [shippingCostForGiftCard] Parameter siteId[" + siteId
                        + "] Parameter ShippingMethodId[" + shippingMethodId + "] Exit");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " shippingCostForGiftCard");
            }
        }

        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }
    
    /**
     * Gets the shipping method.
     *
     * @param shippingMethod the shipping method
     * @return RepositoryItem
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final RepositoryItem getShippingMethod(String shippingMethod)
                    throws BBBBusinessException, BBBSystemException {
        RepositoryItem shipItem = null;
        try {
			if (shippingMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)) {
				shipItem = this.getShippingRepository().getItem(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD,
						BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR);
			} else {
				shipItem = this.getShippingRepository().getItem(shippingMethod,
						BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR);
			}
            if (shipItem == null) {

                throw new BBBBusinessException(BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY,
                                "not a valid shipping method");

            }
        } catch (final RepositoryException e) {
            if (this.isLoggingError()) {
                this.logError("catalog_1016: Error while retrieving shipping method", e);
            }

            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            "unable to retrive data for a shipping method ");

        }
        return shipItem;
    }
    
    
    /**
     * Gets the shipping duration.
     *
     * @param pShippingMethod the shipping method
     * @param pSiteId the site id
     * @return RepositoryItem
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public RepositoryItem getShippingDuration(final String pShippingMethod, final String pSiteId)
                    throws BBBBusinessException, BBBSystemException {
        RepositoryItem shippingDuration = null;
        try {
            final RepositoryView view = this.getShippingRepository().getView(
                            BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR);
            final QueryBuilder queryBuilder = view.getQueryBuilder();
            final Query[] queries = new Query[2];
            final QueryExpression shipMethodCode = queryBuilder
                            .createPropertyQueryExpression(BBBCatalogConstants.SHIP_METHOD_CD);
            final QueryExpression shipcode = queryBuilder.createConstantQueryExpression(pShippingMethod);
            queries[0] = queryBuilder.createComparisonQuery(shipMethodCode, shipcode, QueryBuilder.EQUALS);

            final QueryExpression siteId = queryBuilder.createPropertyQueryExpression(BBBCoreConstants.SITE);
            final QueryExpression siteValue = queryBuilder.createConstantQueryExpression(pSiteId);
            queries[1] = queryBuilder.createComparisonQuery(siteId, siteValue, QueryBuilder.EQUALS);
            final Query query = queryBuilder.createAndQuery(queries);

            final RepositoryItem[] repositoryItems = view.executeQuery(query);
            if ((repositoryItems != null) && (repositoryItems.length > 0)) {
                shippingDuration = repositoryItems[0];
            }
        } catch (final RepositoryException re) {
            this.logError(LogMessageFormatter.formatMessage(null,
                            "BBBCatalogToolsImpl.getShippingMethod() | RepositoryException"), re);
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_FETCH_DATA_REPOSITORY_EXCEPTION, re);
        }
        return shippingDuration;
    }
    
    
    /**
	 * Method for fetching Delivery dates based on VDC specific SKUs.BPSI 1928.
	 * requireMsgInDate - true : retruns vdc delivery time in Dates
	 * requireMsgInDate - false : returns vdc delivery time in days
	 *
	 * @param shippingMethod the shipping method
	 * @param skuID the sku id
	 * @param requireMsgInDate the require msg in date
	 * @param inputDate the input date
	 * @param includeYearFlag the include year flag
	 * @param fromShippingPage the from shipping page
	 * @return the expected delivery time vdc
	 * @throws BBBSystemException the BBB system exception
	 */
	public String getExpectedDeliveryTimeVDC(String shippingMethod, String skuID, boolean requireMsgInDate, Date inputDate, boolean includeYearFlag, boolean fromShippingPage) throws BBBSystemException{
		this.logDebug("Catalog API Method Name [getExpectedDeliveryTimeVDC] skuID ["+skuID+"] Entry");
		String vdcMsg = "", vdcShipTimeMsg = "", vdcUnitType="";
		int minDaysToShipVDC = 0, maxDaysToShipVDC=0,minShippingDays=0,maxShippingDays=0,minDays=0,maxDays=0;
		Date cutOffTime = null;
		String siteId = getCurrentSiteId();
		try {
			final RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuID, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			if(skuRepositoryItem != null){
				//Fix for BBBSL-8155.
				boolean personalizationOffered = getGlobalRepositoryTools().isCustomizationOfferedForSKU(skuRepositoryItem, siteId);
				boolean ltlFlag = false;
				if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) != null) {
					ltlFlag = ((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU)).booleanValue();
		        }
				//Fix for BBBSL-8999
				/*if( !fromShippingPage && (personalizationOffered || ltlFlag) && !requireMsgInDate){
					return vdcMsg;
				}else{*/
					if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) != null
							&& skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) != null){
						minShippingDays = (Integer) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS);
						maxShippingDays = (Integer) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS);
						 this.logDebug("SKUVDC minShippingDays: " + minShippingDays+"maxShippingDays:"+maxShippingDays);
					}
				/*}*/
				if(fromShippingPage || requireMsgInDate){
	             if (BBBUtility.isNotEmpty(shippingMethod)) {
	                 this.logDebug("Query to be executed for shipping repo is " + this.getShippingDurationRqlQuery());
	                 RepositoryItem[] shippingRepositoryItem = null;
	                 Object[] params = new Object[2];
	                 params[0] = shippingMethod;
	                 params[1] = siteId;
	                 shippingRepositoryItem = this.executeRQLQuery(this.getShippingDurationRqlQuery(), params,
	                                 BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,
	                                 this.getShippingRepository());
	                 if(shippingRepositoryItem != null && shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_VDC_PROPERTY_NAME) != null
	                		 && shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_VDC_PROPERTY_NAME) != null){
	                	minDaysToShipVDC =  (Integer)(shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_VDC_PROPERTY_NAME));
	                	maxDaysToShipVDC =  (Integer)(shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_VDC_PROPERTY_NAME));
	                	cutOffTime = (Date)(shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME));
	                	this.logDebug("ShippingDuration minDaysToShipVDC: " + minDaysToShipVDC + "maxDaysToShipVDC:"+maxDaysToShipVDC);
	                 }else{
	                	 params = new Object[1];
	                	 params[0] = shippingMethod;
	                	 this.logDebug("Query to be executed for shipping repo is " + this.getShippingMethodRqlQuery());
	                	 shippingRepositoryItem = this.executeRQLQuery(this.getShippingMethodRqlQuery(), params,
	                             BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR,
	                             this.getShippingRepository());
	                	 if(shippingRepositoryItem != null && shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_VDC_PROPERTY_NAME) != null
	                			 && shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_VDC_PROPERTY_NAME) != null){
	                		 minDaysToShipVDC = (Integer)(shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_VDC_PROPERTY_NAME));
	                		 maxDaysToShipVDC = (Integer)(shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_VDC_PROPERTY_NAME));
	                		 cutOffTime = (Date)(shippingRepositoryItem[0].getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME));
	                	 }
	                 }
	             }
				}
	             // summing days at sku and vdc level to get final min max days
	             minDays = minShippingDays + minDaysToShipVDC;
	             maxDays = maxShippingDays + maxDaysToShipVDC;
	             if(minDays >0 && maxDays > 0){
					if (requireMsgInDate) {
						Calendar offSetMinDateCal = Calendar.getInstance();
						Calendar offSetMaxDateCal = Calendar.getInstance();
						if (inputDate != null) {
							offSetMinDateCal.setTime(inputDate);
							offSetMaxDateCal.setTime(inputDate);
						}

						Set<Integer> weekEndDays = this.getWeekEndDays(siteId);
						this.logDebug("weekEndDays are  " + weekEndDays);
						// Calculate Holiday List based on BCC Configured list
						Set<Date> holidayList = this.getHolidayList(siteId);
						if (weekEndDays != null && !weekEndDays.isEmpty()
								&& holidayList != null && !holidayList.isEmpty()) {
							minDays = getOffsetDays(minDays, offSetMinDateCal, weekEndDays, holidayList, cutOffTime);
							maxDays = getOffsetDays(maxDays, offSetMaxDateCal, weekEndDays, holidayList, cutOffTime);
						}else{
							offSetMinDateCal.add(Calendar.DATE, minDays);
							offSetMaxDateCal.add(Calendar.DATE, maxDays);
						}

						String offSetMinDateString = getSiteBasedFormattedDate(siteId, offSetMinDateCal, includeYearFlag);
						String offSetMaxDateString = getSiteBasedFormattedDate(siteId, offSetMaxDateCal, includeYearFlag);

						this.logDebug("[getExpectedDeliveryTimeVDC] Delivery Dates :  "
								+ offSetMinDateString
								+ HYPHEN
								+ offSetMaxDateString);
						return offSetMinDateString + HYPHEN + offSetMaxDateString;
					}else{
		            		 if(minDays >= 14){
		            			 vdcShipTimeMsg = String.valueOf(Math.round(((float) minDays)/7)) + "-" + String.valueOf((int)Math.ceil(((float) maxDays)/5));
		            			 vdcUnitType=WEEKS;
		            		 }else{
		            			 vdcShipTimeMsg = minDays + "-" + maxDays;
		            			 vdcUnitType=DAYS;
		            		 }
		            		 Map<String,String> vdcPlaceHolder = new HashMap<String, String>();
		            		 vdcPlaceHolder.put(VDC_SHIP_TIME_MSG, vdcShipTimeMsg);
		            		 vdcPlaceHolder.put(VDC_UNIT_TYPE, vdcUnitType);
		            		 return getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_VDC_DEL_MSG, null, vdcPlaceHolder, siteId);
		            	 }
	             	}
				}
		} catch (RepositoryException e) {
			this.logDebug("Catalog API Method Name [getExpectedDeliveryTimeVDC]: RepositoryException "+BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION);
			throw new BBBSystemException (BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		} catch (BBBSystemException e) {
			this.logDebug("Catalog API Method Name [getExpectedDeliveryTimeVDC]: BBBSystemException " + e);
			throw new BBBSystemException (BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}
		this.logDebug("Catalog API Method Name [getExpectedDeliveryTimeVDC] skuID ["+skuID+"] Exit");
		return vdcMsg ;
	}
   
	 /**
		 * This method will return a RegionVO object with Region and Store
		 * information, when provided with the zip code.
		 *
		 * @param zipCode the zip code
		 * @return the region data from zip
		 * @throws BBBBusinessException the BBB business exception
		 * @throws BBBSystemException the BBB system exception
		 */
		public RegionVO getRegionDataFromZip(String zipCode) throws BBBBusinessException, BBBSystemException {

			this.logDebug("Catalog API Method Name [getRegionNStoreIds] zipCode[" + zipCode + "]");
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getRegionNStoreIds");

			if (zipCode == null || StringUtils.isEmpty(zipCode)) {
				throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
						BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
			} else if (zipCode.contains(BBBCoreConstants.HYPHEN)) {
				vlogDebug(
						"BBBShippingRepositoryTools.getRegionDataFromZip: zipcode: {0} contains '-', so we are considering only the part before hyphen",
						zipCode);
				zipCode = zipCode.split(BBBCoreConstants.HYPHEN)[0];
			}

			RegionVO regionVo = null;

			try {
				
				final String siteId = getCurrentSiteId();
				RepositoryItem[] regionsRepositoryItem = null;
				RepositoryView regionsView = getShippingRepository().getView(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR);
				RqlStatement regionsRqlStatement = getRQLStatement();
				Object params[] = new Object[2];
				params[0] = zipCode;
				params[1] = siteId;
				vlogDebug(
						"BBBShippingRepositoryTools.getRegionDataFromZip: Querying ShippingRepository with query: {0} and param: {1}",
						getRegionsRqlQuery(), params);
				regionsRepositoryItem = regionsRqlStatement.executeQuery(regionsView, params);

				if (null != regionsRepositoryItem)
					regionVo = this.populateRegionVO(regionsRepositoryItem[0], BBBCatalogConstants.REGIONS_REG_STORES);

			} catch (RepositoryException e) {
				logError("Catalog API Method Name [getRegionNStoreIds]: RepositoryException "
						+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRegionNStoreIds");
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION, e);
			} finally {
				logDebug("Catalog API Method Name [getRegionNStoreIds] for zipCode   [" + zipCode + "] Exit");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRegionNStoreIds");
			}

			return regionVo;

		}


	protected RqlStatement getRQLStatement() throws RepositoryException {
		return RqlStatement.parseRqlStatement(getRegionsRqlQuery());
	}


	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
	
	
		/**
		 * This method will return RegionVO, with zip codes populated, when provided
		 * with a regionId.
		 *
		 * @param regionId the region id
		 * @return the zip codes from region
		 * @throws BBBSystemException the BBB system exception
		 * @throws BBBBusinessException the BBB business exception
		 */
		public RegionVO getZipCodesFromRegion(String regionId) throws BBBSystemException, BBBBusinessException {

			this.logDebug("Catalog API Method Name [getZipCodes] regionId[" + regionId + "]");
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getZipCodes");

			if (regionId == null || StringUtils.isEmpty(regionId)) {
				throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
						BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
			}

			RegionVO regionVo = null;
			try {

				final RepositoryItem regionRepositoryItem = this.getShippingRepository().getItem(regionId,
						BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR);
				regionVo = this.populateRegionVO(regionRepositoryItem, BBBCatalogConstants.REGIONS_ZIPCODES);

			} catch (RepositoryException e) {
				logError("Catalog API Method Name [getZipCodes]: RepositoryException "
						+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getZipCodes");
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION, e);

			} finally {
				logDebug("Catalog API Method Name [getZipCodes] for regionId   [" + regionId + "] Exit");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getZipCodes");
			}

			return regionVo;
		}
	
		
		/**
		 * This method will return a RegionVO object with StoreIds populated, when
		 * provided with a regionId.
		 *
		 * @param regionId the region id
		 * @return the store ids from region
		 * @throws BBBBusinessException the BBB business exception
		 * @throws BBBSystemException the BBB system exception
		 */
		public RegionVO getStoreIdsFromRegion(String regionId) throws BBBBusinessException, BBBSystemException {

			this.logDebug("Catalog API Method Name [getStoreIds] regionId[" + regionId + "]");
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getStoreIds");

			if (regionId == null || StringUtils.isEmpty(regionId)) {
				throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
						BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
			}

			RegionVO regionVo = null;

			try {

				final RepositoryItem regionRepositoryItem = this.getShippingRepository().getItem(regionId,
						BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR);
				regionVo = this.populateRegionVO(regionRepositoryItem, BBBCatalogConstants.REGIONS_STORES);

			} catch (RepositoryException e) {
				logError("Catalog API Method Name [getZipCodes]: RepositoryException "
						+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getStoreIds");
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION, e);

			} finally {
				logDebug("Catalog API Method Name [getStoreIds] for regionId   [" + regionId + "] Exit");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getStoreIds");
			}

			return regionVo;

		}
	
	
		/**
		 * This method will return a specific regions all values, when provided with
		 * a regionId.
		 *
		 * @param regionId the region id
		 * @return the all region details
		 * @throws BBBBusinessException the BBB business exception
		 * @throws BBBSystemException the BBB system exception
		 */
		public RegionVO getAllRegionDetails(String regionId) throws BBBBusinessException, BBBSystemException {

			this.logDebug("Catalog API Method Name [getRegionValues] regionId[" + regionId + "]");
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getRegionValues");

			if (regionId == null || StringUtils.isEmpty(regionId)) {
				throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
						BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
			}

			RegionVO regionVo = null;
			try {

				final RepositoryItem regionRepositoryItem = this.getShippingRepository().getItem(regionId,
						BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR);
				regionVo = this.populateRegionVO(regionRepositoryItem, BBBCatalogConstants.REGIONS_ALL);

			} catch (RepositoryException e) {
				logError("Catalog API Method Name [getRegionValues]: RepositoryException "
						+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRegionValues");
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION, e);

			} finally {
				logDebug("Catalog API Method Name [getRegionValues] for regionId   [" + regionId + "] Exit");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRegionValues");
			}

			return regionVo;

		}
	
	
		/**
		 * This is a helper method used for populating RegionVO object, type value
		 * governs which all values will be populated in the VO.
		 *
		 * @param regionRepoItem the region repo item
		 * @param type the type
		 * @return the region vo
		 * @throws BBBBusinessException the BBB business exception
		 */
		@SuppressWarnings("unchecked")
		public RegionVO populateRegionVO(RepositoryItem regionRepoItem, String type) throws BBBBusinessException {

			this.logDebug("Catalog API Method Name [populateRegionVO] regionRepoItem[" + regionRepoItem + "]" + " type ["
					+ type + "]");

			if (null == regionRepoItem || null == type) {
				return null;

			}

			RegionVO regionVo = new RegionVO();
			String regionPromoAtt = BBBCoreConstants.BLANK;
			// check for SddFlag
			boolean sddFlag = (boolean) regionRepoItem.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG);
			String regionId = (String) regionRepoItem.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_ID);
			String regionName = (String) regionRepoItem.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_NAME);
			Date regionCutOff = (Date) regionRepoItem.getPropertyValue(BBBCatalogConstants.REGIONS_CUT_OFF);
			String displayCutOff = (String) regionRepoItem.getPropertyValue(BBBCatalogConstants.REGIONS_DISP_CUT_OFF);
			Set<String> regionStores = (Set<String>) regionRepoItem.getPropertyValue(BBBCatalogConstants.REGIONS_STORES);
			Set<String> regionZipCodes = (Set<String>) regionRepoItem
					.getPropertyValue(BBBCatalogConstants.REGIONS_ZIPCODES);
			RepositoryItem regionPromoAttRepoItem = (RepositoryItem) regionRepoItem.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT);
			if(null != regionPromoAttRepoItem)
				regionPromoAtt = regionPromoAttRepoItem.getRepositoryId();
			Set<RepositoryItem> sites = (Set<RepositoryItem>) regionRepoItem.getPropertyValue(BBBCatalogConstants.SITES);
			double minShipFee = (double) regionRepoItem.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE);
			String getByTime = (String) regionRepoItem.getPropertyValue(BBBCatalogConstants.GET_BY_TIME);
			String timeZone = (String) regionRepoItem.getPropertyValue(BBBCatalogConstants.TIME_ZONE);
			
			

			// logDebug
			this.logDebug("Catalog API Method Name [populateRegionVO] values in the Region sddFlag: " + sddFlag
					+ " regionId: " + regionId + " regionName: " + regionName + " regionCutOff: " + regionCutOff
					+ " displayCutOff: " + displayCutOff + " regionStores: " + regionStores + " regionZipCodes : "
					+ regionZipCodes + " regionPromoAtt : " + regionPromoAtt + "getByTime : " + getByTime + "minShipFee: " 
					+ minShipFee + "sites : " + sites);

			if (type.equals(BBBCatalogConstants.REGIONS_ZIPCODES) && sddFlag) {

				if (null != regionZipCodes && !regionZipCodes.isEmpty()) {

					regionVo.setZipCodes(regionZipCodes);
					return regionVo;
				} else {

					return null;
				}

			} else if (type.equals(BBBCatalogConstants.REGIONS_STORES) && sddFlag) {

				if (null != regionStores && !regionStores.isEmpty()) {

					regionVo.setStoreIds(regionStores);
					return regionVo;
				} else {
					return null;
				}
				// here we are not checking sdd flag here since we have already
				// fired the query with sddFlag=true
			} else if (type.equals(BBBCatalogConstants.REGIONS_ALL)) {
				

				regionVo.setRegionId(regionId);
				regionVo.setRegionName(regionName);
				regionVo.setCutOffTimeRegion(regionCutOff);
				regionVo.setDisplayCutoffTime(displayCutOff);
				regionVo.setStoreIds(regionStores);
				regionVo.setZipCodes(regionZipCodes);
				regionVo.setPromoAttId(regionPromoAtt);
				regionVo.setSddFlag(sddFlag);
				regionVo.setMinShipFee(minShipFee);
				regionVo.setDisplayGetByTime(getByTime);
				regionVo.setTimeZone(timeZone);
				Set<String> sitesSet = new HashSet<>();
				for(RepositoryItem site : sites){
					sitesSet.add(site.getRepositoryId());
				}
				regionVo.setSites(sitesSet);
				return regionVo;

			} else if (type.equals(BBBCatalogConstants.REGIONS_REG_STORES)) {

				if (!StringUtils.isBlank(regionId)){
					regionVo.setRegionId(regionId);
					regionVo.setDisplayCutoffTime(displayCutOff);
					regionVo.setDisplayGetByTime(getByTime);
					regionVo.setMinShipFee(minShipFee);
					regionVo.setPromoAttId(regionPromoAtt);
					regionVo.setTimeZone(timeZone);
				}
					

				if (null != regionStores && !regionStores.isEmpty())
					regionVo.setStoreIds(regionStores);
				
				if (!StringUtils.isBlank(displayCutOff)){
					regionVo.setDisplayCutoffTime(displayCutOff);
				}
				regionVo.setMinShipFee(minShipFee);
				
				
				return regionVo;

			} else {

				logDebug("The type value that you have entered is invalid, please enter the correct value type = " + type);
			}

			return null;

		}
	
   
	    /**
	     *  Returns a list of state Codes that are bopus disabled. If input parameter is null then BBBBusinessException is
	     * thrown
	     *
	     * @return the bopus disabled states
	     * @throws BBBBusinessException the BBB business exception
	     * @throws BBBSystemException the BBB system exception
	     */
	    public final List<String> getBopusDisabledStates() throws BBBBusinessException, BBBSystemException {
	        this.logDebug("Catalog API Method Name [getBopusDisabledStates] siteid ");

	        try {
	            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getBopusDisabledStates");
	            final RepositoryItem[] stateRepoItem = this.executeRQLQuery("bopus=0 or bopus is null",
	                            BBBCatalogConstants.STATE_ITEM_DESCRIPTOR, this.getShippingRepository());
	            this.logDebug("stateRepoItem value " + Arrays.toString(stateRepoItem));
	            if (stateRepoItem != null) {
	                final List<String> bopusStatesList = new ArrayList<String>();
	                for (final RepositoryItem element : stateRepoItem) {
	                    if (element == null) {
	                        continue;
	                    }

	                    this.logDebug("State code of bopus state " + element.getRepositoryId());
	                    final String stateDesc = (String) (element
	                                    .getPropertyValue(BBBCatalogConstants.STATE_DESC_PROPERTY_NAME));
	                    bopusStatesList.add(element.getRepositoryId() + ":" + stateDesc);
	                }
	                return bopusStatesList;
	            }
	            throw new BBBBusinessException(BBBCatalogErrorCodes.NO_BOPUS_DISABLED_STATE_AVAILABLE_IN_REPOSITORY,
	                            BBBCatalogErrorCodes.NO_BOPUS_DISABLED_STATE_AVAILABLE_IN_REPOSITORY);
	        } catch (final RepositoryException e) {
	            this.logError("Catalog API Method Name [getBopusDisabledStates]: RepositoryException ");
	            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
	                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
	        } finally {
	            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBopusDisabledStates");
	        }

	    }
    
   
	    /**
	   	 *  
	   	 * The method gets the state tax details corresponding to a state code.
	   	 *
	   	 * @param stateCode the state code
	   	 * @return the state tax
	   	 */
		   public double getStateTax(final String stateCode) {
		       double stateTax = 0.00;
		       try{
			       if(stateCode != null && !StringUtils.isEmpty(stateCode)){
				       final RepositoryItem statesRepositoryItem = this.getShippingRepository().getItem(stateCode,
				                       BBBCatalogConstants.STATE_ITEM_DESCRIPTOR);
				       if ((statesRepositoryItem != null)
				                       && (statesRepositoryItem.getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_STATE_TAX) != null)) {
				    		   stateTax = Double.valueOf(statesRepositoryItem
				                       .getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_STATE_TAX).toString());
				       }
			       }
		       }catch(RepositoryException repositoryException){
		    	   logError("Error while Fetching State tax from repository : " + repositoryException);
		       }
		       return stateTax;
		   }
   
 
		   /**
			 * Finding the offset dates based on input offset considering holidays and
			 * weekends.
			 *
			 * @param offSetDays the off set days
			 * @param offSetDateCal the off set date cal
			 * @param weekEndDays the week end days
			 * @param holidayList the holiday list
			 * @param cutOffTime the cut off time
			 * @return offset days
			 */
			private int getOffsetDays(int offSetDays, Calendar offSetDateCal, Set<Integer> weekEndDays,
					Set<Date> holidayList, Date cutOffTime) {
				boolean isCutOff = false;
				int resultDays = offSetDays;
				final Calendar calCutOffTime = Calendar.getInstance();
				final Calendar calCurrentDate = Calendar.getInstance();
				if (cutOffTime != null) {
					calCutOffTime.setTime(cutOffTime);
		            if (calCurrentDate.get(Calendar.HOUR_OF_DAY) == calCutOffTime.get(Calendar.HOUR_OF_DAY)) {
		                if (calCurrentDate.get(Calendar.MINUTE) >= calCutOffTime.get(Calendar.MINUTE)) {
		                    isCutOff = true;
		                }
		            } else if (calCurrentDate.get(Calendar.HOUR_OF_DAY) > calCutOffTime.get(Calendar.HOUR_OF_DAY)) {
		                   isCutOff = true;
		            }
		            if (isCutOff && !weekEndDays.contains(Integer.valueOf(calCurrentDate.get(Calendar.DAY_OF_WEEK)))
		                    && !isHoliday(holidayList, calCurrentDate)) {
		            	resultDays = resultDays + 1;
		    		}
		        }

				int tempDays = resultDays;
				while (tempDays != 0) {
		            if (!weekEndDays.contains(Integer.valueOf(offSetDateCal.get(Calendar.DAY_OF_WEEK)))
		                            && !isHoliday(holidayList, offSetDateCal)) {
		            	tempDays--;
		            }
		            if (tempDays != 0) {
		            	offSetDateCal.add(Calendar.DATE, 1);
		            }
				}
				return resultDays;
			}
			

			/**
			 * @return the regionsRqlQuery
			 */
			public String getRegionsRqlQuery() {
				return regionsRqlQuery;
			}

			/**
			 * @param regionsRqlQuery the regionsRqlQuery to set
			 */
			public void setRegionsRqlQuery(String regionsRqlQuery) {
				this.regionsRqlQuery = regionsRqlQuery;
			}

			
			/**
			 *
			 * @return shippingMethodRqlQuery
			 */
			public String getShippingMethodRqlQuery() {
				return shippingMethodRqlQuery;
			}
			/**
			 *
			 * @param shippingMethodRqlQuery
			 */
			public void setShippingMethodRqlQuery(String shippingMethodRqlQuery) {
				this.shippingMethodRqlQuery = shippingMethodRqlQuery;
			}
			
			/**
			 *
			 * @return shippingDurationRqlQuery
			 */
			public String getShippingDurationRqlQuery() {
				return shippingDurationRqlQuery;
			}
			/**
			 *
			 * @param shippingDurationRqlQuery
			 */
			public void setShippingDurationRqlQuery(String shippingDurationRqlQuery) {
				this.shippingDurationRqlQuery = shippingDurationRqlQuery;
			}
			
		    /** @return the shippingCostRqlQuery */
		    public final String getShippingCostRqlQuery() {
		        return this.shippingCostRqlQuery;
		    }

		    /** @param shippingCostRqlQuery the shippingCostRqlQuery to set */
		    public final void setShippingCostRqlQuery(final String shippingCostRqlQuery) {
		        this.shippingCostRqlQuery = shippingCostRqlQuery;
		    }
			
			/** @return the shippingFeeRqlQuery */
		    public final String getShippingFeeRqlQuery() {
		        return this.shippingFeeRqlQuery;
		    }

		    /** @param shippingFeeRqlQuery the shippingFeeRqlQuery to set */
		    public final void setShippingFeeRqlQuery(final String shippingFeeRqlQuery) {
		        this.shippingFeeRqlQuery = shippingFeeRqlQuery;
		    }

			/** @return the shippingRepository */
		    public final MutableRepository getShippingRepository() {
		        return this.shippingRepository;
		    }

		    /** @param shippingRepository the shippingRepository to set */
		    public final void setShippingRepository(final MutableRepository shippingRepository) {
		        this.shippingRepository = shippingRepository;
		        
		    }
		    
		    /** @return the shippingFeeRqlQueryForNullState */
		    public final String getShippingFeeRqlQueryForNullState() {
		        return this.shippingFeeRqlQueryForNullState;
		    }
		    
		    /** @param shippingFeeRqlQueryForNullState the shippingFeeRqlQueryForNullState to set */
		    public final void setShippingFeeRqlQueryForNullState(final String shippingFeeRqlQueryForNullState) {
		        this.shippingFeeRqlQueryForNullState = shippingFeeRqlQueryForNullState;
		    }
		    
		    /**
			 * @return the sddShipMethodId
			 */
			public String getSddShipMethodId() {
				return sddShipMethodId;
			}

			/**
			 * @param sddShipMethodId the sddShipMethodId to set
			 */
			public void setSddShipMethodId(String sddShipMethodId) {
				this.sddShipMethodId = sddShipMethodId;
			}


			/**
			 * @return the globalRepositoryTools
			 */
			public GlobalRepositoryTools getGlobalRepositoryTools() {
				return globalRepositoryTools;
			}


			/**
			 * @param globalRepositoryTools the globalRepositoryTools to set
			 */
			public void setGlobalRepositoryTools(GlobalRepositoryTools globalRepositoryTools) {
				this.globalRepositoryTools = globalRepositoryTools;
			}
}

