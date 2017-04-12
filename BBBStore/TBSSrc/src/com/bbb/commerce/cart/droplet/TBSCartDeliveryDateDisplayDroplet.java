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

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.TBSCatalogToolsImpl;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.utils.BBBUtility;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class TBSCartDeliveryDateDisplayDroplet extends BBBDynamoServlet {

	private static final String VDC_SKU_SHIPPING_MESSAGE = "vdcSKUShippingMessage";
	private static final String ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD = "Error While invoking getExpectedDeliveryDate method";
	private static final String BUSINESS_EXCEPTION_WHILE_INVOKING_GET_ATTRIBUTES_INFO_METHOD = "Business Exception While invoking getAttributeInfoRepositoryItems";
	private static final String SYSTEM_EXCEPTION_WHILE_INVOKING_GET_ATTRIBUTES_INFO_METHOD = "System Exception While invoking getExpectedDeliveryDate method";
	private static final String EXPECTED_DELIVERY_DATE_OUTPUT = "expectedDeliveryDateOutput";
	private static final String SHIPPINGGROUP = "shippingGroup";
	private static final String LTL_SKU_ID = "ltlSkuId";
	private static final String BLANK = "";
	private BBBCatalogTools catalogTools;
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
    /** this method adds expected delivery date or vdcSKUShippingMessage in request for shippinggroup, ltlskuid, 
     *  isFromOrderDetail, order, orderdate passed as parameters.
     *  
     *  @param DynamoHttpServletRequest
	 *  @param DynamoHttpServletResponse
	 *  @return void
	 *  @throws ServletException, IOException
     */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		if (null != pRequest.getObjectParameter(SHIPPINGGROUP) && null == pRequest.getObjectParameter(LTL_SKU_ID)) {
			Object shipObj = pRequest.getObjectParameter(SHIPPINGGROUP);
			HardgoodShippingGroup shipGrp = null;
			BBBStoreShippingGroup storeGrp = null;
			
			String fromCart = pRequest.getParameter(BBBCoreConstants.FROM_CART);
			if(shipObj instanceof HardgoodShippingGroup){
				shipGrp = (HardgoodShippingGroup)shipObj;
			}
			if(shipObj instanceof BBBStoreShippingGroup){
				storeGrp = (BBBStoreShippingGroup)shipObj;
			}
			
			String shippingMethod = null; 
			if(shipGrp != null){
				shippingMethod = shipGrp.getShippingMethod();
			}
			if(storeGrp != null){
				shippingMethod = storeGrp.getShippingMethod();
			}
			String vdcShippingMessage=null;
			String attributeId=null;
			String skuAttrRelnId = null;
			String currentSiteId = SiteContextManager.getCurrentSiteId();
			boolean showVdcMessage = false;
			boolean isSkuLtl = false;
			boolean isVdcSku = false;
			String vdcSkuId = null;
			
			if (shipGrp != null || storeGrp != null) {
				List relations = null;
				if(shipGrp != null){
					relations = shipGrp.getCommerceItemRelationships();
				}
				if(storeGrp != null){
					relations = storeGrp.getCommerceItemRelationships();
				}
				List<BBBShippingGroupCommerceItemRelationship> bbbSGCIRelList = (List<BBBShippingGroupCommerceItemRelationship>) relations;
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
								if (vdcAttributesList != null && vdcAttributesList.size() != 0 && vdcAttributesList.contains(attributeId)) {
									vdcFlag = true;
									vdcSkuId = catalogRefItem.getRepositoryId();
									isVdcSku = true;
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
								if (showVdcMessage) {
									if (null != catalogRefItem.getPropertyValue(BBBCatalogConstants.VDC_SKU_MESSAGE_SKU_PROPERTY_NAME)) {
										vdcShippingMessage = (String) catalogRefItem.getPropertyValue(BBBCatalogConstants.VDC_SKU_MESSAGE_SKU_PROPERTY_NAME);
										logDebug("VDC SKu Message :: " + vdcShippingMessage);
									}
								}
							}

						}
					}
				}
			}

			String state = null;
			Date shipOnDate = null;
			if(shipGrp != null){
				state = shipGrp.getShippingAddress().getState();
				shipOnDate = shipGrp.getShipOnDate();
			}
			if(storeGrp != null){
				state = storeGrp.getStateAsString();
				shipOnDate = storeGrp.getShipOnDate();
			}
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
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BBBCoreConstants.DATE_FORMAT);
				if(timestamp!=null){
					String orderDate = simpleDateFormat.format(timestamp);
					try {
						orderSubmittedDate = simpleDateFormat.parse(orderDate);
					} catch (ParseException e) {
						throw new BBBBusinessException("ParseException");
					}
				}

				if(isLoggingDebug()){
					vlogDebug("shippingMethod: " + shippingMethod);
					vlogDebug("state: " + state);
					vlogDebug("isFromOrderDetail: " + isFromOrderDetail);
					vlogDebug("shipOnDate: " + shipOnDate );

				}
				if(shipGrp != null){
					Order order = (Order) pRequest.getObjectParameter(BBBCoreConstants.ORDER);
					int cartQty = order.getCommerceItemCount();
					
					if (cartQty>1 && ((TBSCatalogToolsImpl)getCatalogTools()).isCartContainSpecialOrder(order, siteId)){
						
						pRequest.setParameter(BBBCoreConstants.EXPECTED_DELIVERY_DATE,"");	
						
						} else {				
							
						if(!BBBUtility.isEmpty(isFromOrderDetail) && isFromOrderDetail.equalsIgnoreCase(BBBCoreConstants.TRUE) && orderSubmittedDate!=null){
							if(shipOnDate != null){
								if(isVdcSku){
									expDeliveryDate = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethod, vdcSkuId , true, shipOnDate, false);
								}else{
									expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state , siteId, shipOnDate,false);
								}
							}else{
								if(isVdcSku){
									expDeliveryDate = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethod, vdcSkuId ,true, orderSubmittedDate, false);
								}else{
									expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state , siteId, orderSubmittedDate,false );
								}
							}
						}else{
							//if orderSubmittedDate is null then order is not placed yet hence use current date
							if(shipOnDate != null){
								if(isVdcSku && orderSubmittedDate != null){
									expDeliveryDate = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethod, vdcSkuId ,true, shipOnDate, false);
								}else{
									expDeliveryDate = getCatalogTools().getExpectedDeliveryDate(shippingMethod, state , siteId, shipOnDate,false);
								}
							}else{
								if(isVdcSku){
									expDeliveryDate = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethod, vdcSkuId ,true, new Date(), false);
								}else{
									expDeliveryDate = ((TBSCatalogToolsImpl)getCatalogTools()).getTBSExpectedDeliveryDate(shippingMethod, state , siteId, new Date(), order,false );
								}
							}
						}
						if(isLoggingDebug()){
							vlogDebug("expDeliveryDate: " + expDeliveryDate);
						}
						pRequest.setParameter(BBBCoreConstants.EXPECTED_DELIVERY_DATE,expDeliveryDate);
					}
					if(vdcShippingMessage!=null){
						pRequest.setParameter(VDC_SKU_SHIPPING_MESSAGE,vdcShippingMessage);
					}
					pRequest.serviceParameter(EXPECTED_DELIVERY_DATE_OUTPUT, pRequest, pResponse);
				}

			} catch (BBBSystemException e) {
				vlogError(LogMessageFormatter.formatMessage(pRequest, ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD), e);
			} catch (BBBBusinessException e) {
				vlogError(LogMessageFormatter.formatMessage(pRequest, ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD), e);
			}
		}
	}
	if (null != pRequest.getObjectParameter(LTL_SKU_ID) && null != pRequest.getObjectParameter(SHIPPINGGROUP)) {
		
		Object shipObj = pRequest.getObjectParameter(SHIPPINGGROUP);
		HardgoodShippingGroup shipGrp = null;
		BBBStoreShippingGroup storeGrp = null;
		
		if(shipObj instanceof HardgoodShippingGroup){
			shipGrp = (HardgoodShippingGroup)shipObj;
		}
		if(shipObj instanceof BBBStoreShippingGroup){
			storeGrp = (BBBStoreShippingGroup)shipObj;
		}
		String skuId = (String)pRequest.getObjectParameter(LTL_SKU_ID);
		Boolean isSkuLtl = false;
		String shippingMethod = null;
		String state = null;
		Date shipOnDate = null;
		
		if(shipGrp != null){
			shippingMethod = shipGrp.getShippingMethod();
			state = shipGrp.getShippingAddress().getState();
			shipOnDate = shipGrp.getShipOnDate();
		}
		if(storeGrp != null){
			shippingMethod = storeGrp.getShippingMethod();
			state = storeGrp.getStateAsString();
			shipOnDate = storeGrp.getShipOnDate();
		}
		
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
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BBBCoreConstants.DATE_FORMAT);
					if(timestamp!=null){
						String orderDate = simpleDateFormat.format(timestamp);
						try {
							orderSubmittedDate = simpleDateFormat.parse(orderDate);
						} catch (ParseException e) {
							throw new BBBBusinessException("ParseException");
						}
					}
					
					if(isLoggingDebug()){
						vlogDebug("shippingMethod: " + shippingMethod);
						vlogDebug("state: " + state);
						vlogDebug("isFromOrderDetail: " + isFromOrderDetail);
						vlogDebug("shipOnDate: " + shipOnDate );
					}
					if(shipGrp != null){
						if(!BBBUtility.isEmpty(isFromOrderDetail) && isFromOrderDetail.equalsIgnoreCase(BBBCoreConstants.TRUE) && orderSubmittedDate!=null){
							if(shipOnDate != null){
								expDeliveryDate = getCatalogTools().getExpectedDeliveryDateForLTLItem(shippingMethod, siteId, skuId, shipOnDate, false);
							}else{
								expDeliveryDate = getCatalogTools().getExpectedDeliveryDateForLTLItem(shippingMethod, siteId, skuId, orderSubmittedDate, false);
							}
						}else{
							//if orderSubmittedDate is null then order is not placed yet hence use current date
							if(shipOnDate != null){
								expDeliveryDate = getCatalogTools().getExpectedDeliveryDateForLTLItem(shippingMethod, siteId, skuId, shipOnDate, false);
							}else{
								expDeliveryDate = getCatalogTools().getExpectedDeliveryDateForLTLItem(shippingMethod, siteId, skuId, new Date(), false);
							}
						}
						if(isLoggingDebug()){
							logDebug("expDeliveryDate: " + expDeliveryDate);
						}
					}
				}
				pRequest.setParameter(BBBCoreConstants.EXPECTED_DELIVERY_DATE,expDeliveryDate);
				pRequest.serviceParameter(EXPECTED_DELIVERY_DATE_OUTPUT,pRequest, pResponse);
				
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD), e);
			}
		}


	}
}
