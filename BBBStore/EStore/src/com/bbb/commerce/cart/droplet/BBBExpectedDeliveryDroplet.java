package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.order.HardgoodShippingGroup;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.utils.BBBUtility;

/**
 * @author kmagud 
 *
 * Refactored the code from BBBCartDisplayDroplet to get Expected Delivery Date
 */
public class BBBExpectedDeliveryDroplet extends BBBDynamoServlet{
	
	/**	Constant for string shippingGroup. */
	private static final String SHIPPINGGROUP = "shippingGroup";
	/**	Constant for string ltlSkuId. */
	private static final String LTL_SKU_ID = "ltlSkuId";
	/**	Constant for string vdcSkuId. */
	private static final String VDC_SKU_ID = "vdcSkuId";
	/**	Constant for string isVdcSku. */
	private static final String IS_VDC_SKU = "isVdcSku";
	/**	Constant for string Business Exception in getAttributeInfoRepositoryItems. */
	private static final String BUSINESS_EXCEPTION_WHILE_INVOKING_GET_ATTRIBUTES_INFO_METHOD = "Business Exception While invoking getAttributeInfoRepositoryItems";
	/**	Constant for string system Exception in getExpectedDeliveryDate. */
	private static final String SYSTEM_EXCEPTION_WHILE_INVOKING_GET_ATTRIBUTES_INFO_METHOD = "System Exception While invoking getExpectedDeliveryDate method";
	/**	Constant for string blank. */
	private static final String BLANK = "";
	/**	Constant for string vdcSKUShippingMessage. */
	private static final String VDC_SKU_SHIPPING_MESSAGE = "vdcSKUShippingMessage";
	/**	Constant for string expectedDeliveryDateOutput. */
	private static final String EXPECTED_DELIVERY_DATE_OUTPUT = "expectedDeliveryDateOutput";
	/**	Constant for string error in getExpectedDeliveryDate. */
	private static final String ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD = "Error While invoking getExpectedDeliveryDate method";
	/**	Instance for BBBCatalogTools. */
	private BBBCatalogTools catalogTools;
	
	/**
	 * 
	 * @return catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	/**
	 * 
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	
	@SuppressWarnings("unchecked")
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	
		String currentSiteId = SiteContextManager.getCurrentSiteId();
		
		if (null != pRequest.getObjectParameter(SHIPPINGGROUP) && null == pRequest.getObjectParameter(LTL_SKU_ID)) {
			
			HardgoodShippingGroup shipGrp = (HardgoodShippingGroup)pRequest.getObjectParameter(SHIPPINGGROUP);
			
			String shippingMethod = shipGrp.getShippingMethod();
			String vdcShippingMessage=null;
			String attributeId=null;
			String skuAttrRelnId = null;
			Boolean isVdcSku=false;
			String vdcSkuId="";
			if(pRequest.getObjectParameter(IS_VDC_SKU) != null && pRequest.getObjectParameter(IS_VDC_SKU) != ""){
				isVdcSku = (Boolean) pRequest.getObjectParameter(IS_VDC_SKU);
			}
			if(pRequest.getObjectParameter(VDC_SKU_ID) != null){
				vdcSkuId = (String) pRequest.getObjectParameter(VDC_SKU_ID);
			}
			boolean showVdcMessage = false;
			boolean isSkuLtl = false;
			if (shipGrp != null) {
				List<BBBShippingGroupCommerceItemRelationship> bbbSGCIRelList = (List<BBBShippingGroupCommerceItemRelationship>) shipGrp.getCommerceItemRelationships();
				for (BBBShippingGroupCommerceItemRelationship bbbSGCIRelItem : bbbSGCIRelList) {
					RepositoryItem catalogRefItem = (RepositoryItem) bbbSGCIRelItem.getCommerceItem().getAuxiliaryData().getCatalogRef();
					if(bbbSGCIRelItem.getCommerceItem() instanceof BBBCommerceItem && ((BBBCommerceItem)bbbSGCIRelItem.getCommerceItem()).isLtlItem()){
						break;
					}
					//If shipping methos is ltl then return Blank delivery date
					
					Set<RepositoryItem> skuAttrRelnIdsSet = (Set<RepositoryItem>) catalogRefItem.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME);
					if (skuAttrRelnIdsSet != null && !skuAttrRelnIdsSet.isEmpty()) {
						for (RepositoryItem skuAttrRelnIds : skuAttrRelnIdsSet) {
							boolean vdcFlag = false;
							skuAttrRelnId = (String) skuAttrRelnIds.getRepositoryId();
							if (skuAttrRelnId != null && !skuAttrRelnId.isEmpty()) {
								int index = skuAttrRelnId.indexOf(BBBCoreConstants.UNDERSCORE);
								attributeId = skuAttrRelnId.substring(index + 1);
								List<String> vdcAttributesList = new ArrayList<String>();
								try {
									vdcAttributesList = this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,	BBBCatalogConstants.VDC_ATTRIBUTES_LIST);
								} catch (Exception e) {
									logError(LogMessageFormatter.formatMessage(	pRequest, "Exception - vdcAttributesList Key not found", BBBCoreErrorConstants.CART_ERROR_1025), e);
								}
								if (vdcAttributesList != null && vdcAttributesList.contains(attributeId)) {
									vdcFlag = true;
								}
							}

							if (vdcFlag) {
								RepositoryItem[] attributeInfoItems = null;
								String siteId = null;
								try {
									attributeInfoItems = getCatalogTools().getAttributeInfoRepositoryItems(attributeId);
									for (RepositoryItem attributeInfoItem : attributeInfoItems) {
										Set<RepositoryItem> sitesSet = (Set<RepositoryItem>) attributeInfoItem.getPropertyValue(BBBCoreConstants.SITES);
										if (sitesSet != null && !sitesSet.isEmpty()) {
											for (RepositoryItem siteIdItem : sitesSet) {
												siteId = (String) siteIdItem.getRepositoryId();
												if (currentSiteId.equalsIgnoreCase(siteId)) {
													showVdcMessage = true;
													break;
												}
											}
										}

									}

								} catch (BBBBusinessException businessException) {
									logError(LogMessageFormatter.formatMessage(pRequest, BUSINESS_EXCEPTION_WHILE_INVOKING_GET_ATTRIBUTES_INFO_METHOD), businessException);
								} catch (BBBSystemException systemException) {
									logError(LogMessageFormatter.formatMessage(pRequest, SYSTEM_EXCEPTION_WHILE_INVOKING_GET_ATTRIBUTES_INFO_METHOD),systemException);
								}

								// Show site specific VDC Message 
								if (showVdcMessage && null != catalogRefItem.getPropertyValue(BBBCatalogConstants.VDC_SKU_MESSAGE_SKU_PROPERTY_NAME)) {
										vdcShippingMessage = (String) catalogRefItem.getPropertyValue(BBBCatalogConstants.VDC_SKU_MESSAGE_SKU_PROPERTY_NAME);
										logDebug("VDC SKu Message :: " + vdcShippingMessage);
								}
							}

						}
					}
				}
			}

			String state = shipGrp.getShippingAddress().getState();
			Date shipOnDate = shipGrp.getShipOnDate();
			if(!isSkuLtl) {
			try {

				String expDeliveryDate = BLANK;
				String siteId = SiteContextManager.getCurrentSiteId();
				String isFromOrderDetail = null;
				Timestamp timestamp = null;
				Date orderSubmittedDate  = null;
				if(pRequest.getObjectParameter("isFromOrderDetail")!=null){
					isFromOrderDetail = (String) pRequest.getObjectParameter("isFromOrderDetail");
				}
				if(pRequest.getObjectParameter("orderDate")!=null){
					timestamp = (Timestamp) pRequest.getObjectParameter("orderDate");
				}
				orderSubmittedDate = this.getOrderSubmittedDate(timestamp);
				
					logDebug("shippingMethod: " + shippingMethod);
					logDebug("state: " + state);
					logDebug("isFromOrderDetail: " + isFromOrderDetail);
					logDebug("shipOnDate: " + shipOnDate );

				
				if(!BBBUtility.isEmpty(isFromOrderDetail) && isFromOrderDetail.equalsIgnoreCase(BBBCoreConstants.TRUE) && orderSubmittedDate!=null){
					if(shipOnDate != null){
						if(isVdcSku != null && isVdcSku){
							expDeliveryDate = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethod, vdcSkuId , true, shipOnDate, false);
					}else{
							expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state , siteId, shipOnDate,false);
					}
				}else{
						if(isVdcSku!=null && isVdcSku){
							expDeliveryDate = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethod, vdcSkuId ,true, orderSubmittedDate, false);
						}else{
							expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state , siteId, orderSubmittedDate,false );
						}
					}
				}else{
					//if orderSubmittedDate is null then order is not placed yet hence use current date
					if(shipOnDate != null){
						if(isVdcSku!=null && isVdcSku && orderSubmittedDate != null){
							expDeliveryDate = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethod, vdcSkuId ,true, shipOnDate, false);
					}else{
							expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state , siteId, shipOnDate,false);
					}
					}else{
						if(isVdcSku!=null && isVdcSku){
							expDeliveryDate = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethod, vdcSkuId ,true, new Date(), false);
						}else{
							expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state , siteId, new Date(),false );
				}
					}
				}
				
					logDebug("expDeliveryDate: " + expDeliveryDate);
				

				pRequest.setParameter(BBBCoreConstants.EXPECTED_DELIVERY_DATE,expDeliveryDate);
				if(vdcShippingMessage!=null){
					pRequest.setParameter(VDC_SKU_SHIPPING_MESSAGE,vdcShippingMessage);
				}
				pRequest.serviceParameter(EXPECTED_DELIVERY_DATE_OUTPUT,
						pRequest, pResponse);

			} catch (BBBSystemException e) {
				logError(
						LogMessageFormatter
						.formatMessage(pRequest,
								ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD),
								e);

			} catch (BBBBusinessException e) {
				logError(
						LogMessageFormatter
						.formatMessage(pRequest,
								ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD),
								e);

			}
		}

	}
	if (null != pRequest.getObjectParameter(LTL_SKU_ID) && null != pRequest.getObjectParameter(SHIPPINGGROUP)) {
			
			String skuId = (String)pRequest.getObjectParameter(LTL_SKU_ID);
			HardgoodShippingGroup shipGrp = (HardgoodShippingGroup)pRequest.getObjectParameter(SHIPPINGGROUP);
			Boolean isSkuLtl = false;
			String shippingMethod = shipGrp.getShippingMethod();
			
			String state = shipGrp.getShippingAddress().getState();
			Date shipOnDate = shipGrp.getShipOnDate();
			
			try {
				String expDeliveryDate = BLANK;
				String siteId = SiteContextManager.getCurrentSiteId();
				String isFromOrderDetail = null;
				Timestamp timestamp = null;
				Date orderSubmittedDate  = null;
				
				isSkuLtl = (Boolean) pRequest.getObjectParameter("isltlSku");
				logDebug("Is sku LTL:"+isSkuLtl);
				if(isSkuLtl) {
					if(pRequest.getObjectParameter("isFromOrderDetail")!=null){
						isFromOrderDetail = (String) pRequest.getObjectParameter("isFromOrderDetail");
					}
					if(pRequest.getObjectParameter("orderDate")!=null){
						timestamp = (Timestamp) pRequest.getObjectParameter("orderDate");
					}
					orderSubmittedDate = this.getOrderSubmittedDate(timestamp);
					
						logDebug("shippingMethod: " + shippingMethod);
						logDebug("state: " + state);
						logDebug("isFromOrderDetail: " + isFromOrderDetail);
						logDebug("shipOnDate: " + shipOnDate );
	
					
					if(!BBBUtility.isEmpty(isFromOrderDetail) && isFromOrderDetail.equalsIgnoreCase(BBBCoreConstants.TRUE) && orderSubmittedDate!=null){
						if(shipOnDate != null){
							expDeliveryDate = getCatalogTools().getExpectedDeliveryDateForLTLItem(shippingMethod, siteId, skuId, shipOnDate,false);
						}else{
							expDeliveryDate = getCatalogTools().getExpectedDeliveryDateForLTLItem(shippingMethod, siteId, skuId, orderSubmittedDate,false);
						}
					}else{
						//if orderSubmittedDate is null then order is not placed yet hence use current date
						if(shipOnDate != null){
							expDeliveryDate = getCatalogTools().getExpectedDeliveryDateForLTLItem(shippingMethod, siteId, skuId, shipOnDate,false);
						}else{
							expDeliveryDate = getCatalogTools().getExpectedDeliveryDateForLTLItem(shippingMethod, siteId, skuId, new Date(),false);
						}
					}
					
					logDebug("expDeliveryDate: " + expDeliveryDate);
					
				}
					pRequest.setParameter(BBBCoreConstants.EXPECTED_DELIVERY_DATE,expDeliveryDate);
					pRequest.serviceParameter(EXPECTED_DELIVERY_DATE_OUTPUT,pRequest, pResponse);
				
			} catch (BBBSystemException e) {
				logError(
						LogMessageFormatter
								.formatMessage(pRequest,
										ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD),
						e);

			} catch (BBBBusinessException e) {
				logError(
						LogMessageFormatter
								.formatMessage(pRequest,
										ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD),
						e);

			}
			
		}

	}
	/**
	 * @param timestamp
	 * @param orderSubmittedDate
	 * @return
	 * @throws BBBBusinessException
	 */
	private Date getOrderSubmittedDate(Timestamp timestamp) throws BBBBusinessException {
		Date orderSubmittedDate = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BBBCoreConstants.DATE_FORMAT);
		if(timestamp!=null){
			String orderDate = simpleDateFormat.format(timestamp);
			try {
				orderSubmittedDate = simpleDateFormat.parse(orderDate);
			} catch (ParseException e) {
				throw new BBBBusinessException("ParseException");
			}
		}
		return orderSubmittedDate;
	}
		
}

