package com.bbb.rest.commerce.promotion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.adapter.gsa.GSAItem;
import atg.commerce.CommerceException;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.PricingTools;
import atg.commerce.promotion.ClosenessQualifierDroplet;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.commerce.checkout.util.BBBOrderUtilty;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class BBBClosenessQualifierService extends BBBGenericService{
	private static final String ATG_COMMERCE_SHOPPING_CART = "/atg/commerce/ShoppingCart";
	private ClosenessQualifierDroplet closenessQualifierDroplet;
	private BBBOrder order;
	private OrderDetailsManager orderDetailsManager;
	private BBBPricingTools pricingTools;
	
	
	/**
	 * @return the closenessQualifierDroplet
	 */
	public ClosenessQualifierDroplet getClosenessQualifierDroplet() {
		return closenessQualifierDroplet;
	}
	/**
	 * @param closenessQualifierDroplet the closenessQualifierDroplet to set
	 */
	public void setClosenessQualifierDroplet(ClosenessQualifierDroplet closenessQualifierDroplet) {
		this.closenessQualifierDroplet = closenessQualifierDroplet;
	}
	/**
	 * @return the order
	 */
	public BBBOrder getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(BBBOrder order) {
		this.order = order;
	}
	
	
	public OrderDetailsManager getOrderDetailsManager() {
		return orderDetailsManager;
	}
	
	
	public void setOrderDetailsManager(OrderDetailsManager orderDetailsManager) {
		this.orderDetailsManager = orderDetailsManager;
	}
	
	public BBBPricingTools getPricingTools() {
		return pricingTools;
	}
	public void setPricingTools(BBBPricingTools pricingTools) {
		this.pricingTools = pricingTools;
	}
	/**
	 * this method calls the OOTB ClosenessQualifierDroplet and fetches the valid closenessQualifiers
	 * 
	 * @param qualifierType: type of the closeness qualifier to be  fetched
	 * @return BBBClosenessQualifierDropletResultVO - contains the name,type and url value contained in the 1st closeness qualifier
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("rawtypes")
	public BBBClosenessQualifierDropletResultVO getClosenessQualifiers(String qualifierType, String fromMobilePDP) throws BBBSystemException, BBBBusinessException{
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		if((qualifierType == null || qualifierType.trim().equals("")) || (null == getOrder())){
			throw new BBBBusinessException(BBBCoreErrorConstants.REQUIRED_PARA_NULL, "required parameter can't be empty");
		}
		pRequest.setParameter("type", qualifierType);
		pRequest.setParameter("order", getOrder());
		try {
			getClosenessQualifierDroplet().service(pRequest, pResponse);			
		} catch (ServletException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR__SERVLET_CLOSENESSQUALIFIER, "Exception occured in closenessQualifier droplet ");
		} catch (IOException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR__IO_CLOSENESSQUALIFIER, "Exception occured in closenessQualifier droplet ");
		}
		if(pRequest.getObjectParameter("errorMsg") !=null){
			throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_CLOSENESSQUALIFIER, "Some error occurred in fetching the closeness qualifier for the order");
		}
		List closenessQualifier = new ArrayList();
		BBBClosenessQualifierDropletResultVO closenessQualifierResultVo = new BBBClosenessQualifierDropletResultVO();
		closenessQualifier = (List) pRequest.getObjectParameter("closenessQualifiers");
		if(closenessQualifier !=null && !closenessQualifier.isEmpty()){
			GSAItem qualifier = (GSAItem) closenessQualifier.get(0);
			String name = (String) qualifier.getPropertyValue("name");
			closenessQualifierResultVo.setName(name);
			GSAItem upsellMedia = (GSAItem) qualifier.getPropertyValue("upsellMedia");
			if(null != upsellMedia){
				String url = (String) upsellMedia.getPropertyValue("url");
				closenessQualifierResultVo.setUrl(url);
				int type = (Integer) upsellMedia.getPropertyValue("type");
				closenessQualifierResultVo.setType(type);
				String desc = (String) upsellMedia.getPropertyValue("description");
				closenessQualifierResultVo.setPromoText(desc);
			}
		}
		if (BBBCoreConstants.TRUE.equalsIgnoreCase(fromMobilePDP)) {
			final OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_COMMERCE_SHOPPING_CART);
			final BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
			final BBBOrderVO bbbOrderVO = new BBBOrderVO();
			if (order != null) {
				final PriceInfoVO orderPriceInfo = getPricingTools().getOrderPriceInfo(order);
				bbbOrderVO.setOrderPriceInfoDisplayVO(BBBOrderUtilty.populatePriceInfo(orderPriceInfo, SiteContextManager.getCurrentSiteId(), order, null));
				
				closenessQualifierResultVo.setOnlinePurchaseTotal(bbbOrderVO.getOrderPriceInfoDisplayVO().getOnlinePurchaseTotal());
			}
		}
		return closenessQualifierResultVo;

	}
}
