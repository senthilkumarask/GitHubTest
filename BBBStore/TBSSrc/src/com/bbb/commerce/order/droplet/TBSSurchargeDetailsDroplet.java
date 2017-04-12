package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.pricing.TBSPricingTools;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;

/**
 * This class is used to get the surcharge amount 
 * @author Administrator
 *
 */
public class TBSSurchargeDetailsDroplet extends DynamoServlet {
	
	private TBSPricingTools pricingTools;
	
	/**
	 * @return the pricingTools
	 */
	public TBSPricingTools getPricingTools() {
		return pricingTools;
	}

	/**
	 * @param pricingTools the pricingTools to set
	 */
	public void setPricingTools(TBSPricingTools pricingTools) {
		this.pricingTools = pricingTools;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		String fromCart = pRequest.getParameter("fromCart");
		Object obj = pRequest.getObjectParameter("order");
		Order order = null;
		double totalSurCharges = 0.0;
		if(obj != null){
			order = (Order)obj;
		}
		try {
			if(!StringUtils.isBlank(fromCart)){
				List<ShippingGroup> shipGroups = order.getShippingGroups();
				for (ShippingGroup shipGroup : shipGroups) {
					BBBHardGoodShippingGroup hardShipGroup = null;
					if(shipGroup instanceof BBBHardGoodShippingGroup){
						hardShipGroup = (BBBHardGoodShippingGroup)shipGroup;
						List<CommerceItemRelationship> shipCItems =  hardShipGroup.getCommerceItemRelationships();
						double surCharges = 0.0;
						for (CommerceItemRelationship cItemRelationship : shipCItems) {
							if(cItemRelationship.getCommerceItem() instanceof BBBCommerceItem){
								surCharges = getPricingTools().getCatalogUtil().getSKUSurcharge(order.getSiteId(), cItemRelationship.getCommerceItem().getCatalogRefId(), hardShipGroup.getShippingMethod());
								surCharges = surCharges * cItemRelationship.getQuantity();
								if(surCharges > 0){
									totalSurCharges += surCharges;
									surCharges = 0.0;
								} 
							}
						}
					}
				}
			} 
			if(totalSurCharges > 0){
				pRequest.setParameter("surcharge", totalSurCharges);
				pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
			} else {
				pRequest.serviceLocalParameter(TBSConstants.EMPTY, pRequest, pResponse);
			}
			
		} catch (BBBSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BBBBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
