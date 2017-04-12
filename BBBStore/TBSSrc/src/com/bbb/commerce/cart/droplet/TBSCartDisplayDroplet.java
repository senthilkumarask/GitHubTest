package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.catalog.TBSCatalogToolsImpl;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

import atg.commerce.order.Order;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.commerce.order.HardgoodShippingGroup;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class TBSCartDisplayDroplet extends BBBCartDisplayDroplet {

	private static final String VDC_SKU_SHIPPING_MESSAGE = "vdcSKUShippingMessage";
	private static final String ERROR_WHILE_INVOKING_GET_CART_ITEM_VO_LIST_METHOD = "Error While invoking getCartItemVOList method";
	private static final String ERROR_WHILE_INVOKING_GET_EXPECTED_DELIVERY_DATE_METHOD = "Error While invoking getExpectedDeliveryDate method";
	private static final String BUSINESS_EXCEPTION_WHILE_INVOKING_GET_ATTRIBUTES_INFO_METHOD = "Business Exception While invoking getAttributeInfoRepositoryItems";
	private static final String SYSTEM_EXCEPTION_WHILE_INVOKING_GET_ATTRIBUTES_INFO_METHOD = "System Exception While invoking getExpectedDeliveryDate method";
	private static final String REG_OUTPUT = "regOutput";
	private static final String EXPECTED_DELIVERY_DATE_OUTPUT = "expectedDeliveryDateOutput";
	private static final String SHIPPINGGROUP = "shippingGroup";
	private static final String LTL_SKU_ID = "ltlSkuId";
	private static final String BLANK = "";
	private static final String FROM_ORDER_DETAIL = "isFromOrderDetail";
	private TBSCatalogToolsImpl tbsCatalogToolsImpl;
	
	public TBSCatalogToolsImpl getTbsCatalogToolsImpl() {
		return tbsCatalogToolsImpl;
	}

	public void setTbsCatalogToolsImpl(TBSCatalogToolsImpl tbsCatalogToolsImpl) {
		this.tbsCatalogToolsImpl = tbsCatalogToolsImpl;
	}
	
	/**
	 * this methods adds list of CommerceItemVO in request for order param being
	 * passes in request
	 * 
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		if (pRequest.getObjectParameter(BBBCoreConstants.ORDER) != null) {
			List<CommerceItemVO> commerceItemVOs = null;
			Order order = (Order) pRequest.getObjectParameter(BBBCoreConstants.ORDER);
			try {

				String fromCart = pRequest.getParameter(BBBCoreConstants.FROM_CART);
				//BBBSL-9914 | Checking for order detail flag to avoid order update 
				String fromOrderDetail = (String) pRequest.getAttribute(FROM_ORDER_DETAIL);
				
					if(!BBBUtility.isEmpty(fromCart) && ( BBBUtility.isEmpty(fromOrderDetail) || BBBCoreConstants.FALSE.equalsIgnoreCase(fromOrderDetail))) {
						commerceItemVOs = setSaveForLaterDetails(pRequest, order);
					} else {
						//getOrderManager().updateAvailabilityMapInOrder(pRequest, order);
						commerceItemVOs = getCheckoutManager().getCartItemVOList(order);
					}
				
				
				BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
				List<String> restrictedSkuList= (List<String>) sessionBean.getValues().get("internationalShipRestrictedSku");
				List<String> bopusSkuList=	(List<String>) sessionBean.getValues().get("bopusSkuNoIntShip");
				boolean hasIntShipRestrictedSku=false;
				boolean hasIntShipBopusSku=false;
				if(restrictedSkuList!=null && !restrictedSkuList.isEmpty()){
					hasIntShipRestrictedSku=true;
				}
				if(bopusSkuList!=null && !bopusSkuList.isEmpty()){
					hasIntShipBopusSku=true;
				}
				if(hasIntShipBopusSku || hasIntShipRestrictedSku){
					
					for(CommerceItemVO commerceItemVO:commerceItemVOs){
						final	String commerceItemId=commerceItemVO.getBBBCommerceItem().getId();
						if(hasIntShipRestrictedSku && restrictedSkuList.contains(commerceItemId)){
							commerceItemVO.getSkuDetailVO().setRestrictedForIntShip(true);
						}
						if(hasIntShipBopusSku && bopusSkuList.contains(commerceItemId)){
							commerceItemVO.getSkuDetailVO().setRestrictedForBopusAllowed(true);
						}
					}
				}
				if(hasIntShipBopusSku){
					sessionBean.getValues().remove("bopusSkuNoIntShip");
				}
				 if(hasIntShipRestrictedSku){
					sessionBean.getValues().remove("internationalShipRestrictedSku");
				}
				pRequest.setParameter(BBBCoreConstants.COMMERCE_ITEM_LIST, commerceItemVOs);
				pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
			} catch (BBBSystemException e) {
				vlogError(LogMessageFormatter.formatMessage(pRequest, "Error getting item details"), e);
				pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
			} catch (BBBBusinessException e) {
				vlogError(LogMessageFormatter.formatMessage(pRequest, ERROR_WHILE_INVOKING_GET_CART_ITEM_VO_LIST_METHOD), e);
				pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
			} 

		}
	}
}

