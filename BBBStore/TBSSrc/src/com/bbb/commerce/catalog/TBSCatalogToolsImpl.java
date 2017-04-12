package com.bbb.commerce.catalog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.SkuVO;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;
import com.bbb.utils.BBBUtility;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.ServletUtil;

public class TBSCatalogToolsImpl extends BBBCatalogToolsImpl {
	
	private static final String SHIPPING_DURATION_IS = "ShippingDuration is ";
	private static final String HYPHEN = " - ";
	private MutableRepository siteRepository;
	
	public  MutableRepository getSiteRepository() {
		return this.siteRepository;
	}

	public final void setSiteRepository(final MutableRepository siteRepository) {
		this.siteRepository = siteRepository;
	}
	
	private TBSSearchStoreManager tbsSearchStoreManager;
	//public BBBOrderImpl order;
	//private AddCommerceItemInfo pItemInfo;
	
	public TBSSearchStoreManager getTbsSearchStoreManager() {
		return tbsSearchStoreManager;
	}

	public void setTbsSearchStoreManager(TBSSearchStoreManager tbsSearchStoreManager) {
		this.tbsSearchStoreManager = tbsSearchStoreManager;
	}
	/**
	 * Overridden to return always TRUE for StoreOnly/ CMO/ Kirsch skus
	 */
	@SuppressWarnings("unchecked")
	public boolean isSkuActive(RepositoryItem pSkuRepositoryItem) {
		
		Set<RepositoryItem> skuAttrRelation = null;
		RepositoryItem skuAttribute = null;
		String skuAttrId = null;
		
		Date previewDate = new Date();
        if (isPreviewEnabled()) {
            previewDate = getPreviewDate();
            vlogDebug("Preview is enabled Value of Preview Date " + previewDate.toString());
        }
		
        if (pSkuRepositoryItem != null) {
            skuAttrRelation = (Set<RepositoryItem>) pSkuRepositoryItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
            if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
            	vlogDebug("skuAttrRelation :: "+skuAttrRelation );
            	for (RepositoryItem skuAttrReln : skuAttrRelation) {
            		skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
            		if(skuAttribute != null){
            			skuAttrId = skuAttribute.getRepositoryId();
            		}
            		if(!StringUtils.isBlank(skuAttrId) && (skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE) || skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE))){
            			return true;
            		}

            	}
            }
            
            Date startDate = (Date) pSkuRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME);
            Date endDate = (Date) pSkuRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME);
            vlogDebug("SKU startDate[" + startDate + "]");
            vlogDebug("SKU endDate[" + endDate + "]");
		    if ((((endDate != null) && previewDate.after(endDate)) || ((startDate != null) && previewDate.before(startDate)))) {
		        return false;
		    }
		    return true;
        }
        return false;
	}
	
	
	 /** This method return Tag On/OFF status
    *
    * @param pRequest - DynamoHttpServletRequest
    * @param catalogTools
    * @param key - requested Tag
    * @return keyStatus */
   @Override
   public final String getThirdPartyTagStatus(final String currentSiteId, final BBBCatalogTools catalogTools,
		   final String name) throws BBBBusinessException, BBBSystemException {
	   String tagStatus = BBBCatalogConstants.TRUE;

	   final StringBuilder tagName = new StringBuilder();
	   tagName.append(name);
	   tagName.append(BBBCatalogConstants.UNDERSCORE);

	   String bedBathUSSite = "";
	   String buyBuyBabySite = "";
	   String tbsBedBathUSSite = "";
	   String tbsBuyBuyBabySite = "";
	   List<String> config = catalogTools.getContentCatalogConfigration(BBBCatalogConstants.BED_BATH_US_SITE_CODE);
	   if ((config != null) && !config.isEmpty()) {
		   bedBathUSSite = config.get(0);
	   }
	   config = catalogTools.getContentCatalogConfigration(BBBCatalogConstants.BUY_BUY_BABY_SITE_CODE);
	   if ((config != null) && !config.isEmpty()) {
		   buyBuyBabySite = config.get(0);
	   }
	   config = catalogTools.getContentCatalogConfigration(BBBCatalogConstants.TBS_BED_BATH_US_SITE_CODE);
	   if ((config != null) && !config.isEmpty()) {
		   tbsBedBathUSSite = config.get(0);
	   }
	   config = catalogTools.getContentCatalogConfigration(BBBCatalogConstants.TBS_BUY_BUY_BABY_SITE_CODE);
	   if ((config != null) && !config.isEmpty()) {
		   tbsBuyBuyBabySite = config.get(0);
	   }

	   if (currentSiteId.equalsIgnoreCase(bedBathUSSite) || currentSiteId.equalsIgnoreCase(tbsBedBathUSSite)) {
		   tagName.append(BBBCatalogConstants.US);
	   } else if (currentSiteId.equalsIgnoreCase(buyBuyBabySite) || currentSiteId.equalsIgnoreCase(tbsBuyBuyBabySite)) {
		   tagName.append(BBBCatalogConstants.BABY);
	   } else {
		   tagName.append(BBBCatalogConstants.CA);
	   }

	   config = catalogTools.getContentCatalogConfigration(tagName.toString());
	   if ((config != null) && !config.isEmpty()) {
		   tagStatus = config.get(0);
	   }
	   return tagStatus;
   }

   
   public BrandVO getBrandDetails(String brandId, String siteId) throws BBBBusinessException, BBBSystemException {
	   if(siteId.startsWith("TBS") ) {
		   return super.getBrandDetails(brandId, siteId.substring(4));
	   }
	   else {
		   return super.getBrandDetails(brandId, siteId);		   	
	   }
   }
   
   protected ProductVO updateProductTabs(final RepositoryItem productRepositoryItem, final String siteId, ProductVO pProductVO) {
	   if(siteId.startsWith("TBS") ) {
		   return super.updateProductTabs(productRepositoryItem, siteId.substring(4), pProductVO);
	   }
	   else {
		   return super.updateProductTabs(productRepositoryItem, siteId, pProductVO);
	   }			
   }


   /**
    * This method is used to get the special departments from Sku
    * @param sku
    * @return
 * @throws BBBSystemException 
 * @throws BBBBusinessException 
    */
	public List<String> getSkuDepartMents(String sku) throws BBBBusinessException, BBBSystemException {
		
		if(StringUtils.isBlank(sku)){
			return null;
		}
		RepositoryItem skuItem = getSkuRepositoryItem(sku);
		List<String> deptIds = new ArrayList<String>();
		if (null != skuItem && null != skuItem.getPropertyValue("jdaSubDept")) {
			String subDeptStoreId = ((RepositoryItem)skuItem.getPropertyValue("jdaSubDept")).getRepositoryId();
			if (subDeptStoreId.contains("_")) {
				String [] splitArray = subDeptStoreId.split("_");
				deptIds.addAll(Arrays.asList(splitArray));
			} else {
				deptIds.add(subDeptStoreId);
			}
		} else if (null != skuItem && null != skuItem.getPropertyValue("jdaDept")) {
			deptIds.add(((RepositoryItem)skuItem.getPropertyValue("jdaDept")).getRepositoryId());
		}
		if (null != skuItem && null != skuItem.getPropertyValue("jdaClass")) {
			deptIds.add((String) skuItem.getPropertyValue("jdaClass"));
		}
		return deptIds;
	}


	public ThresholdVO getSkuThresholdForSplDept(String deptId, long pQuantity) throws RepositoryException {
		 vlogDebug("Catalog API Method Name [getSkuThresholdForSplDept] deptId [  " + deptId + "]");
        if (!StringUtils.isEmpty(deptId)) {
            ThresholdVO thresholdVO = null;
            RepositoryItem[] thresholdItms = null;
            Query thresholdQuery = null;
            QueryExpression deptValue = null;
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSkuThreshold");
            RepositoryItemDescriptor skuThresholdDesc;
			skuThresholdDesc = getCatalogRepository().getItemDescriptor(BBBCatalogConstants.SKU_THRESHOLD_ITEM_DESCRIPTOR);
			RepositoryView skuThresholdView = skuThresholdDesc.getRepositoryView();
			QueryBuilder thresholdQB = skuThresholdView.getQueryBuilder();
			
			QueryExpression jdaDeptProperty = thresholdQB.createPropertyQueryExpression(TBSConstants.JDA_DEPT_ID);
			deptValue = thresholdQB.createConstantQueryExpression(deptId);
			thresholdQuery = thresholdQB.createComparisonQuery(jdaDeptProperty, deptValue, QueryBuilder.EQUALS);
			thresholdItms = skuThresholdView.executeQuery(thresholdQuery);
			if (thresholdItms == null || thresholdItms.length == 0) {
				QueryExpression jdaSubDeptProperty = thresholdQB.createPropertyQueryExpression(TBSConstants.JDA_SUBDEPT_ID);
				deptValue = thresholdQB.createConstantQueryExpression(deptId);
				thresholdQuery = thresholdQB.createComparisonQuery(jdaSubDeptProperty, deptValue, QueryBuilder.EQUALS);
				thresholdItms = skuThresholdView.executeQuery(thresholdQuery);
			}
			if (thresholdItms == null || thresholdItms.length == 0) {
				QueryExpression jdaSubClassProperty = thresholdQB.createPropertyQueryExpression(TBSConstants.JDA_CLASS);
				deptValue = thresholdQB.createConstantQueryExpression(deptId);
				thresholdQuery = thresholdQB.createComparisonQuery(jdaSubClassProperty, deptValue, QueryBuilder.EQUALS);
				thresholdItms = skuThresholdView.executeQuery(thresholdQuery);
			} 
			if (null != thresholdItms && thresholdItms.length > 0 ) {
				for (int i = 0; i < thresholdItms.length; i++) {
					thresholdVO = getThresholdVO(thresholdItms[i]);
					if (thresholdVO.getThresholdAvailable() >= pQuantity) {
						return thresholdVO;
					}
				}
			} else {
				return null;
			}
        }
		return null;
		
	}
	
	/**
	 * Overridden to check only date and decide the sku active
	 */
	 @SuppressWarnings("unchecked")
	public boolean isSkuActive(final RepositoryItem skuRepositoryItem,String ... value) {
	     
		Set<RepositoryItem> skuAttrRelation = null;
		RepositoryItem skuAttribute = null;
		String skuAttrId = null;
		
		Date previewDate = new Date();
        if (isPreviewEnabled()) {
            previewDate = getPreviewDate();
            vlogDebug("Preview is enabled Value of Preview Date " + previewDate.toString());
        }
        
		if(skuRepositoryItem != null){
			
			skuAttrRelation = (Set<RepositoryItem>) skuRepositoryItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
			if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
				vlogDebug("skuAttrRelation :: "+skuAttrRelation );
				for (RepositoryItem skuAttrReln : skuAttrRelation) {
					skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
					if(skuAttribute != null){
						skuAttrId = skuAttribute.getRepositoryId();
					}
					if(!StringUtils.isBlank(skuAttrId) && (skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE) || skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE))){
						return true;
					}
					
				}
			}
			
			Date startDate = (Date) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME);
	        Date endDate = (Date) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME);
	        vlogDebug("SKU startDate[" + startDate + "]");
	        vlogDebug("SKU endDate[" + endDate + "]");
	        if ((((endDate != null) && previewDate.after(endDate)) || ((startDate != null) && previewDate.before(startDate)))) {
	            return false;
	        }
	        return true;
		}
		return false;
    }
	 
	 public List<ShipMethodVO> getLTLEligibleShippingMethods(String skuId,
				String siteId, String locale) throws BBBSystemException,
				BBBBusinessException {
		/*if(BBBUtility.isNotEmpty(siteId) && siteId.contains(TBSConstants.TBS_SITE_PREFIX)){
			siteId = siteId.replace(TBSConstants.TBS_SITE_PREFIX, "");
		}*/
		logDebug("[START] TBSCatalogToolsImpl.getLTLEligibleShippingMethods");
		return super.getLTLEligibleShippingMethods(skuId, siteId, locale);
	 }

	 /**
	 * This method is used specifically for upc search from repository and will return a skuItem.
	 * It will check if the entered search term corresponds to a sku/upc id and will accordingly return a skuItem.
	 * @param skuId
	 */
	public RepositoryItem getSKUForUPCSearch(String skuId) {
		logDebug("Starting TBSCatalogToolsImpl : getSKUForUPCSearch()");
        RepositoryView mView = null;
        RepositoryItem[] skuRepositoryItems = null;
        RepositoryItem skuItem = null;
        
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetailVOForUPCSearch");

            mView = this.getCatalogRepository().getView(BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            RqlStatement statement = RqlStatement.parseRqlStatement(BBBCatalogConstants.SKU_UPC_RQL);
            Object[] params = new Object[1];
            params[0] = skuId;
            skuRepositoryItems = statement.executeQuery(mView, params);
            if (skuRepositoryItems != null) {
            	skuItem = skuRepositoryItems[0];
            }
            logDebug("Following query : "+BBBCatalogConstants.SKU_UPC_RQL+" returns sku repository item:"+skuRepositoryItems+" for skuID : "+skuId);
        } catch(Exception e) {
        	this.logError("Catalog API Method Name [getSKUForUPCSearch]: RepositoryException for sku Id " + skuId);
        } finally {
        	logDebug("Ending TBSCatalogToolsImpl : getSKUForUPCSearch()");
        	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetailVOForUPCSearch");
        }
		return skuItem;
	}
	
	/**
	 * This method is used specifically for upc search from repository and will return a BBBProduct VO.
	 * This method will create BBBProduct by using values from productRepositoryItem and SkuVO which itself is created 
	 * using the skuRepositoryItem.
	 * @param siteId
	 * @param productId
	 * @param skuRepositoryItem
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public BBBProduct getProductDetailsForUPCSearch(RepositoryItem productItem,RepositoryItem skuRepositoryItem) throws BBBBusinessException {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetailsForUPCSearch");
		logDebug("Starting TBSCatalogToolsImpl : getProductDetailsForUPCSearch()");
		RepositoryItem productRepositoryItem = null;
		BBBProduct productVO = null;
		SkuVO skuVO = new SkuVO();
		String skuId = null;
		
		Map<String, SkuVO> skuSet = new HashMap<String, SkuVO>();
		
		try {
			logDebug("Starting TBSCatalogToolsImpl : getProductDetailsForUPCSearch()");
			productRepositoryItem = productItem;
		
			if (productRepositoryItem == null) {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetailsForUPCSearch");
				throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
                    BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
			}
			if(skuRepositoryItem!=null){
				skuId = skuRepositoryItem.getRepositoryId();
				skuVO.setSkuSwatchImageURL((String) skuRepositoryItem.getPropertyValue("swatchImage"));
				skuVO.setSkuMedImageURL((String) skuRepositoryItem.getPropertyValue("mediumImage"));
				skuVO.setSkuVerticalImageURL((String) productRepositoryItem.getPropertyValue("smallImage"));
			}
			
			logDebug("TBSCatalogToolsImpl : getProductDetailsForUPCSearch() :productId : "+productRepositoryItem.getRepositoryId()+" skuId  : "+skuId);
			
			productVO = new BBBProduct();
			skuVO.setSkuID(skuId);
			skuSet.put(skuVO.getSkuID(),skuVO);
			productVO.setSkuSet(skuSet);
			productVO.setProductID(productRepositoryItem.getRepositoryId());
			productVO.setProductName((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
			productVO.setSwatchFlag(((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_PRODUCT_PROPERTY_NAME)).toString());
			productVO.setCollectionFlag(((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION)).toString());
			productVO.setSeoUrl((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME)+"?skuId="+skuId);
			productVO.setVerticalImageURL((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.VERTICAL_IMAGE_PRODUCT_PROPERTY));
			productVO.setImageURL((String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME));
			if(null!=productRepositoryItem.getPropertyValue(BBBCatalogConstants.PROD_ROLLUP_TYPE)) {
				RepositoryItem prodRollUpTypeItem = (RepositoryItem) productRepositoryItem.getPropertyValue(BBBCatalogConstants.PROD_ROLLUP_TYPE);
				String rollUpCode = (String) prodRollUpTypeItem.getPropertyValue(BBBCatalogConstants.ID);
				if(rollUpCode.equals("04") || rollUpCode.equals("05")){
					productVO.setRollupFlag(true);
				}
			}
		} finally {
			logDebug("Ending TBSCatalogToolsImpl : getProductDetailsForUPCSearch()");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getProductDetailsForUPCSearch");
		}
		
		return productVO;
	}
	 
	public boolean isTBSProductActiveMIESearch(final RepositoryItem productRepositoryItem) {
        // Edited as part of Instant preview story
        Date previewDate = new Date();
        if (isPreviewEnabled()) {
            previewDate = getPreviewDate();
        }
		
        final Date startDate = (Date) productRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
        final Date endDate = (Date) productRepositoryItem
                        .getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
     
        boolean disable = true;

        if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) != null) {
            disable = ((Boolean) productRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME)).booleanValue();
        }
        logDebug(productRepositoryItem.getRepositoryId() + " Product id details ::Product startDate[" + startDate
                        + "]Product endDate[" + endDate + "]Product disable[" + disable + "]");
        if ((((endDate != null) && previewDate.after(endDate)) || ((startDate != null) && previewDate.before(startDate)))
                        || disable) {
            return false;
        }
        return true;

    }
	
	public final String getTBSExpectedDeliveryDate(final String shippingMethod,
			final String state, final String siteId, final Date orderDate,
			Order order, boolean includeYearFlag) throws BBBBusinessException,
			BBBSystemException {

		this.logDebug("Catalog API Method Name [getExpectedDeliveryDate]shippingMethod["
				+ shippingMethod + "]" + "includeYearFlag- " + includeYearFlag);

		if ((shippingMethod != null) && !StringUtils.isEmpty(shippingMethod)) {
			try {
				BBBPerformanceMonitor
						.start(BBBPerformanceConstants.CATALOG_API_CALL
								+ " getExpectedDeliveryDate");

				final RepositoryItem shippingDurationItem = this
						.getShippingDuration(shippingMethod, siteId);
				final Calendar calCurrentDate = Calendar.getInstance();
				final Calendar minDate = Calendar.getInstance();
				final Calendar maxDate = Calendar.getInstance();
				final Date cutOffTime = (Date) shippingDurationItem
						.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME);
				final Calendar calCutOffTime = Calendar.getInstance();
				calCutOffTime.setTime(cutOffTime);

				calCurrentDate.setTime(orderDate);
				minDate.setTime(orderDate);
				maxDate.setTime(orderDate);

				this.logDebug("Value of cutOffTime [ " + cutOffTime + "]");
				int maxDaysToShip = 0;
				int minDaysToShip = 0;
				// TO DO
				int cartQty = order.getCommerceItemCount();
				if ((siteId.equalsIgnoreCase(BBBCatalogConstants.TBS_US_SITE)
						|| siteId
								.equalsIgnoreCase(BBBCatalogConstants.TBS_BABY_SITE) || siteId
							.equalsIgnoreCase(BBBCatalogConstants.TBS_CA_SITE))
						&& isCartContainSpecialOrder(order, siteId)
						&& !(cartQty > 1)) {
					maxDaysToShip = BBBCatalogConstants.FIFTY_SIX;
					minDaysToShip = BBBCatalogConstants.FOURTY_TWO;
				} else {
					maxDaysToShip = ((Integer) shippingDurationItem
							.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
							.intValue();
					minDaysToShip = ((Integer) shippingDurationItem
							.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME))
							.intValue();
				}
				this.logDebug("maxDaysToShip [ " + maxDaysToShip
						+ " ] minDaysToShip [" + minDaysToShip + "]");
				this.logDebug("Hour value  in cutOffTime ["
						+ calCutOffTime.get(Calendar.HOUR_OF_DAY)
						+ "] Minute value in cutOffTime ["
						+ calCutOffTime.get(Calendar.MINUTE) + "]");
				this.logDebug("Hour value  in current date ["
						+ calCurrentDate.get(Calendar.HOUR_OF_DAY)
						+ "] Minute value in  current date ["
						+ calCurrentDate.get(Calendar.MINUTE) + "]");
				boolean isCutOff = false;

				Set<Integer> weekEndDays = new HashSet<Integer>();
				weekEndDays = this.getWeekEndDays(siteId);
				this.logDebug("weekEndDays are  " + weekEndDays);

				// Calculate Holiday Dates
				Set<Date> holidayList = new HashSet<Date>();
				holidayList = this.getHolidayList(siteId);
				final int holidayCount = holidayList.size();
				this.logDebug("holidayCount is  " + holidayCount);

				if (cutOffTime != null) {
					if (calCurrentDate.get(Calendar.HOUR_OF_DAY) == calCutOffTime
							.get(Calendar.HOUR_OF_DAY)) {
						if (calCurrentDate.get(Calendar.MINUTE) >= calCutOffTime
								.get(Calendar.MINUTE)) {
							this.logDebug("Value of hours for cutoff ad current is same.So checking for minutes value ");
							this.logDebug("Is minutes value withing cutoff "
									+ (calCurrentDate.get(Calendar.MINUTE) <= calCutOffTime
											.get(Calendar.MINUTE)));
							isCutOff = true;
						}
					} else if (calCurrentDate.get(Calendar.HOUR_OF_DAY) > calCutOffTime
							.get(Calendar.HOUR_OF_DAY)) {
						this.logDebug("hours in current date has exceeded cutoff time");
						isCutOff = true;
					}
					// if today is not a weekday or holiday then add +1 day to
					// min and max
					if (isCutOff
							&& !weekEndDays.contains(Integer
									.valueOf(calCurrentDate
											.get(Calendar.DAY_OF_WEEK)))
							&& !isHoliday(holidayList, calCurrentDate)) {
						minDaysToShip = minDaysToShip + 1;
						maxDaysToShip = maxDaysToShip + 1;
					}
				}
				this.logDebug("maxDaysToShip after cutofftime check [ "
						+ maxDaysToShip
						+ " ] minDaysToShip after cutofftime check ["
						+ minDaysToShip + "]");
				if (this.getExceptionalDeliveryDateStatesList().contains(state)
						&& (this.getStateNoOfDaysThanNormalMap().get(state) != null)) {
					final int extraNoOfDays = Integer.parseInt(this
							.getStateNoOfDaysThanNormalMap().get(state));
					minDaysToShip = minDaysToShip + extraNoOfDays;
					maxDaysToShip = maxDaysToShip + extraNoOfDays;

					final StringBuffer debug = new StringBuffer(50);
					debug.append(
							"maxDaysToShip after cutofftime check for exceptional delivery dates state [ ")
							.append(maxDaysToShip)
							.append(" ] minDaysToShip for exceptional delivery dates state  [")
							.append(minDaysToShip).append("for state ")
							.append(state).append("]");
					this.logDebug(debug.toString());
				}

				final int shippingDuration = maxDaysToShip - minDaysToShip;
				this.logDebug(SHIPPING_DURATION_IS + shippingDuration);
				int tmpMinDays = minDaysToShip;
				int tmpMaxDays = maxDaysToShip;

				while (tmpMinDays != 0) {
					if (!weekEndDays.contains(Integer.valueOf(minDate
							.get(Calendar.DAY_OF_WEEK)))
							&& !isHoliday(holidayList, minDate)) {
						tmpMinDays--;
					}
					if (tmpMinDays != 0) {
						minDate.add(Calendar.DATE, 1);
					}
				}

				while (tmpMaxDays != 0) {
					if (!weekEndDays.contains(Integer.valueOf(maxDate
							.get(Calendar.DAY_OF_WEEK)))
							&& !isHoliday(holidayList, maxDate)) {
						tmpMaxDays--;
					}
					if (tmpMaxDays != 0) {
						maxDate.add(Calendar.DATE, 1);
					}
				}
				String minDateString = BBBCoreConstants.BLANK;
				String maxDateString = BBBCoreConstants.BLANK;

				if (includeYearFlag) {
					final DateFormat dateFormat = new SimpleDateFormat(
							"MM/dd/yyyy");
					minDateString = dateFormat.format(minDate.getTime());
					maxDateString = dateFormat.format(maxDate.getTime());
				} else {
					if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)
							|| siteId
									.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) {
						minDateString = minDate.get(Calendar.DAY_OF_MONTH)
								+ "/" + (minDate.get(Calendar.MONTH) + 1);
						maxDateString = maxDate.get(Calendar.DAY_OF_MONTH)
								+ "/" + (maxDate.get(Calendar.MONTH) + 1);
					} else {
						minDateString = (minDate.get(Calendar.MONTH) + 1) + "/"
								+ minDate.get(Calendar.DAY_OF_MONTH);
						maxDateString = (maxDate.get(Calendar.MONTH) + 1) + "/"
								+ maxDate.get(Calendar.DAY_OF_MONTH);
					}
				}

				this.logDebug("minDateString after format  " + minDateString
						+ "  maxDateString after format " + maxDateString);
				return getFormattedDate(minDateString, includeYearFlag, siteId)
						+ HYPHEN
						+ getFormattedDate(maxDateString, includeYearFlag,
								siteId);
			} finally {
				BBBPerformanceMonitor
						.end(BBBPerformanceConstants.CATALOG_API_CALL
								+ " getExpectedDeliveryDate");
			}
		}
		throw new BBBBusinessException(
				BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
	}

	@SuppressWarnings("unchecked")
	public boolean isCartContainSpecialOrder(Order pOrder, String siteId) {
		if (pOrder != null) {
			List<CommerceItem> commerceItems = pOrder.getCommerceItems();
			for (CommerceItem commerceItem : commerceItems) {
				if (commerceItem instanceof TBSCommerceItem) {
					if(!BBBUtility.isEmpty(((TBSCommerceItem) commerceItem).getShipTime())
							&& ((TBSCommerceItem) commerceItem).getShipTime().equalsIgnoreCase(BBBCatalogConstants.SIX_TO_EIGHT_WEEKS)) {
						return true;
					} else if(BBBUtility.isEmpty(((TBSCommerceItem) commerceItem).getShipTime())) {
						String specialOrderShipTime = this.getTbsSearchStoreManager().getShipTime(
								commerceItem.getCatalogRefId(),
								commerceItem.getQuantity(),
								siteId,
												(String) ServletUtil.getCurrentRequest().getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER));

						if (specialOrderShipTime.equalsIgnoreCase(BBBCatalogConstants.SIX_TO_EIGHT_WEEKS)) {
					return true;
				}
			}
		}
		   }
		}
		return false;
	}
	
	/**
	 * This method returns the override threshold given in the site repository
	 *
	 * @param siteId The site ID for which the threshold needs to be fetched eg TBS_BedBathUS, TBS_BuyBuyBaby, TBS_BedBathCanada
	 * @param type The fee type whose threshold needs to be fetched eg Item price, Ship fee, Tax, Gift Wrap price, Ship surcharge, Delivery fee, Assembly fee
	 * @return the override threshold value 
	 */
	@Override
	public double getOverrideThreshold(String siteId, String type) throws BBBBusinessException, BBBSystemException{
		
		this.logDebug("Catalog API Method Name [getOverrideThreshold] siteId "
				+ siteId);
		RepositoryItem siteConfiguration = null;
		Double threshold = new Double(0);
		try {
			BBBPerformanceMonitor
					.start(BBBPerformanceConstants.CATALOG_API_CALL
							+ " getOverrideThreshold");
			siteConfiguration = this.siteRepository.getItem(siteId,
					BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
			if (siteConfiguration == null) {
				return threshold.doubleValue();
}
			if (siteConfiguration
					.getPropertyValue(type) != null) {
				threshold = (Double) siteConfiguration
						.getPropertyValue(type);
				this.logDebug("getOverrideThreshold : "+ type +" [" + threshold + "]");
			}
		} catch (final RepositoryException e) {
			this.logError("Catalog API Method Name [getOverrideThreshold]: RepositoryException ");
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ " getOverrideThreshold");
		}
		return threshold.doubleValue();
	}
}
