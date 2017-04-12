package com.bbb.commerce.browse;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ParameterName;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
/*
 * Written for fetching VDC specific delivery dates/days to show on PDP/CART pages
 */
public class VDCShippingMessagingDroplet extends BBBDynamoServlet {
	
	private static final String VDC_SHIP_MSG = "vdcShipMsg";
	private static final String OPARAM_OUTPUT = "output";
	private BBBCatalogTools catalogTools;
	private static final String ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD = "err_invoking_get_expected_del_date";
	private static final String ERROR = "error";
	private String defaultShippingMethod;
	public final static String OPARAM_VDC_MSG ="vdcMsg";
	public final static String FROM_SHIPPING_PAGE = "fromShippingPage";
	public final static String IS_VDC_SKU = "isVdcSku";
	public final static String SHIPPING_STATE = "shippingState";
	private static final String LTL_SKU = "ltlSku";
	private static final String PERSONALIZED_SKU = "personalizedSku";
	private MutableRepository catalogRepository;
	
	public final MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	public void setCatalogRepository(final MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
	

	/**
	 * Getter for catalogTools.
	 * 
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	/**
	 * Setter for catalogTools.
	 * 
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	/**
	 * Getter for defaultShippingMethod.
	 * 
	 * @return the defaultShippingMethod
	 */
	public String getDefaultShippingMethod() {
		return defaultShippingMethod;
	}
	/**
	 * Setter for defaultShippingMethod.
	 * 
	 * @param defaultShippingMethod the defaultShippingMethod to set
	 */
	public void setDefaultShippingMethod(String defaultShippingMethod) {
		this.defaultShippingMethod = defaultShippingMethod;
	}
	
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		String shippingMethodCode = pRequest.getParameter(BBBCoreConstants.SHIPPING_METHOD_CODE);
		if(BBBUtility.isEmpty(shippingMethodCode)){
			shippingMethodCode = getDefaultShippingMethod();
		}
		String shippingState = "";
		
		String skuId = pRequest.getParameter(BBBCoreConstants.SKUID);
		boolean requireMsgInDates = false;
		boolean fromShippingPage = false;
		boolean isVDC = true;
		boolean isSKUPersonalizedItem=false;
		boolean isFromOrderDetail=false;
		BBBOrder order=null;
		Date orderSubmittedDate=null;
		String siteId = BBBUtility.getCurrentSiteId(pRequest);
		
		if(!StringUtils.isEmpty((String)pRequest.getParameter(BBBCoreConstants.IS_FROM_ORDER_DETAIL))){
			isFromOrderDetail = Boolean.parseBoolean((String)pRequest.getParameter(BBBCoreConstants.IS_FROM_ORDER_DETAIL));
			if(pRequest.getObjectParameter(BBBCoreConstants.ORDER) != null){
				order = (BBBOrder) pRequest.getObjectParameter(BBBCoreConstants.ORDER);
				orderSubmittedDate= order.getSubmittedDate();
			}
		}
		logDebug("isFromOrderDetail is " + isFromOrderDetail);
		
		if(!StringUtils.isEmpty((String)pRequest.getParameter(BBBCoreConstants.REQUIRE_MSG_IN_DATES))){
			requireMsgInDates = Boolean.parseBoolean((String)pRequest.getParameter(BBBCoreConstants.REQUIRE_MSG_IN_DATES));
		}
		if(!StringUtils.isEmpty((String)pRequest.getParameter(FROM_SHIPPING_PAGE))){
			fromShippingPage = Boolean.parseBoolean((String)pRequest.getParameter(FROM_SHIPPING_PAGE));
		}
		logDebug("fromShippingPage is " + fromShippingPage);
		
		if(!StringUtils.isEmpty((String)pRequest.getParameter(IS_VDC_SKU))){
			isVDC = Boolean.parseBoolean((String)pRequest.getParameter(IS_VDC_SKU));
		}
		
		if(!StringUtils.isEmpty((String)pRequest.getParameter(SHIPPING_STATE))){
			shippingState = (String)pRequest.getParameter(SHIPPING_STATE);
		}
		
		try {
			String vdcShipMsg = null;
			// Calling CatalogTools API to fetch VDC delivery Message
			if(isVDC){
				if(order!=null && isFromOrderDetail && orderSubmittedDate!=null)
				{
					vdcShipMsg = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethodCode, skuId, requireMsgInDates, orderSubmittedDate, false, fromShippingPage);
				}
				else{
					vdcShipMsg = getCatalogTools().getExpectedDeliveryTimeVDC(shippingMethodCode, skuId, requireMsgInDates, new Date(), false, fromShippingPage);
				}
			}else{
				if(order!=null && isFromOrderDetail && orderSubmittedDate!=null)
				{
					vdcShipMsg = getCatalogTools().getExpectedDeliveryDate(shippingMethodCode, shippingState , siteId ,orderSubmittedDate, false);
				}
				else{
					vdcShipMsg = getCatalogTools().getExpectedDeliveryDate(shippingMethodCode, shippingState , siteId ,new Date(), false);
				}
			}
			pRequest.setParameter(VDC_SHIP_MSG, vdcShipMsg); 
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,pResponse);
		} catch (BBBSystemException e) {
			logError(
					LogMessageFormatter
					.formatMessage(pRequest,
							ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD), e);
			pRequest.serviceParameter(ERROR, pRequest, pResponse);
		}catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD), e);
			pRequest.serviceParameter(ERROR, pRequest, pResponse);
		}
		
		try {// BPSI - 2446 DSK | VDC messaging - combine cart and PDP | offset message
			
			boolean ltlSku = getCatalogTools().isSkuLtl(siteId, skuId);
			pRequest.setParameter(VDCShippingMessagingDroplet.LTL_SKU, ltlSku);
			String vdcOffsetFlag = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS).get(BBBCoreConstants.VDC_OFFSET_FLAG);			
			
			RepositoryItem skuRepositoryItem;
			try {
				if(BBBUtility.isNotEmpty(skuId))
				{
					skuRepositoryItem = this.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					isSKUPersonalizedItem = getCatalogTools().isCustomizationOfferedForSKU(skuRepositoryItem, siteId);
				}
			} catch (RepositoryException e) {
				this.logError("Catalog API Method Name [getSKUDetails]: RepositoryException "
                        + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
		        BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetailsForStore");
		        throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
		                        BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			}
			
			//ordercContainsPersonalizedItem=getManager().ordercContainsPersonalizedItem(order);
			pRequest.setParameter(VDCShippingMessagingDroplet.PERSONALIZED_SKU, isSKUPersonalizedItem);
			if(!StringUtils.isEmpty(vdcOffsetFlag) && vdcOffsetFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE)
				&& (siteId!= null)){
				String offsetDateVDC = getCatalogTools().getActualOffsetDate(siteId, skuId);
				if (!StringUtils.isEmpty(offsetDateVDC)){					
					pRequest.setParameter(BBBCoreConstants.OFFSET_DATE_VDC,offsetDateVDC);
					pRequest.serviceLocalParameter(OPARAM_VDC_MSG,pRequest,pResponse);
				}
			}

		} catch (final BBBSystemException e) {
			this.logError("System Exception Occourred while getting getActualOffsetDate ", e);
		}catch (final BBBBusinessException e) {
			this.logError("Business Exception Occourred while getting getActualOffsetDate ", e);
		}
		
		
	}

}
