package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.pricing.PricingException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.TBSPricingTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class TBSGetApplicableShippingMethodsDroplet extends GetApplicableShippingMethodsDroplet {
	private static final String IS_MULTI_SHIP = "isMulti";
	private static final String COMMERCE_ITEM = "commItem";
	private static final String LINE_AMOUNT = "lineAmt";
	
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		final BBBOrder order = (BBBOrder) pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);
		final String operationParam = (String) pRequest
				.getObjectParameter(BBBCoreConstants.OPERATION);
		
		Boolean isMulti = (Boolean) pRequest.getObjectParameter(IS_MULTI_SHIP);
		HashMap<String, List<ShipMethodVO>> skuMethodsMap = null;
		List<ShipMethodVO> shipMethodVOList = null;
		CommerceItem cItem = (CommerceItem) pRequest.getObjectParameter(COMMERCE_ITEM);
		boolean isGiftItem = false;
		
		if (null == order
				|| StringUtils.isBlank(operationParam)
				|| !(StringUtils.equalsIgnoreCase(BBBCoreConstants.PER_SKU,
						operationParam) || StringUtils.equalsIgnoreCase(
						BBBCoreConstants.PER_ORDER, operationParam))) {
			// throw error
			pRequest.setParameter(BBBCoreConstants.SKU_MEHOD_MAP,
					skuMethodsMap);
			pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,
					shipMethodVOList);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,
					pResponse);
			return;
		} 
		
		if((isMulti!= null  && isMulti && StringUtils.equalsIgnoreCase(operationParam, BBBCoreConstants.PER_SKU))){
			Double lineAmt = (Double) pRequest.getObjectParameter(LINE_AMOUNT);
			skuMethodsMap = new HashMap<String, List<ShipMethodVO>>();
			try {
				HardgoodShippingGroup hardGoodShippingGroup = (HardgoodShippingGroup)((ShippingGroupRelationship) 
						cItem.getShippingGroupRelationships().get(0)).getShippingGroup();
				
				shipMethodVOList = getShippingGroupManager()
						.getShippingMethodsForSku(cItem.getCatalogRefId(), order.getSiteId());
				
				isGiftItem = getShippingGroupManager().getCatalogUtil().isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId());
				calculateShippingCost(shipMethodVOList, lineAmt, hardGoodShippingGroup, order, isGiftItem, cItem.getCatalogRefId());
				if(isGiftItem){
					for (ShipMethodVO shipMethodVO : shipMethodVOList) {
						if(shipMethodVO.getShipMethodId().equalsIgnoreCase(BBBCoreConstants.SHIP_METHOD_STANDARD_ID)){
							shipMethodVO.setShippingCharge(0);
						}
					}
				}
				skuMethodsMap.put(cItem.getCatalogRefId(), shipMethodVOList);
				pRequest.setParameter(BBBCoreConstants.SKU_MEHOD_MAP, skuMethodsMap);
				pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
			} catch (BBBSystemException e) {
				logError("System exception occured while fetching shipping method vo", e);
				setErrorParam(pRequest, pResponse);

			} catch (BBBBusinessException e) {
				logError("Business exception occured while fetching shipping method vo", e);
				setErrorParam(pRequest, pResponse);
			}
		} else{
			super.service(pRequest, pResponse);
		}
	}
	
	
	public void calculateShippingCost(List<ShipMethodVO> shipMethodVOList, Double pLineAmt, HardgoodShippingGroup pHgShipGrp,
			BBBOrder pOrder, boolean isGiftItem, String catalogRefId) {
		String state = pHgShipGrp.getShippingAddress().getState();
		// Set state as null, as it is being initialised to INITIAL value, which
		if ((null != pHgShipGrp)
				&& ((null == pHgShipGrp.getShippingAddress()) || BBBUtility
						.isBlank(pHgShipGrp.getShippingAddress()
								.getAddress1()))) {
			state = null;
		}
		// calculate cost for each shipping group in order for each method
		for (ShipMethodVO shipMethodVOItem : shipMethodVOList) {
			double shippingCharges = 0.0;
			try {
				shippingCharges += ((TBSPricingTools) getPricingTools()).calculateShippingCost(pOrder.getSiteId(),
						shipMethodVOItem.getShipMethodId(), pLineAmt, isGiftItem, state, catalogRefId);
			} catch (PricingException exp) {
				if (isLoggingError()) {
					logError("Exception occured while fetching shipping methods " + "for per sku flow - ", exp);
				}
			}
			
			if(isLoggingDebug())
				vlogDebug("GetApplicableShippingMethodsDroplet::calculateShippingCost() Shipping Method :: {0} Shipping Charge:: {1}",shipMethodVOItem.getShipMethodId(), shippingCharges);
			shipMethodVOItem.setShippingCharge(shippingCharges);
		}

	}


	/**
	 * This method calculates shipping cost.
	 * 
	 * @param shipMethodVOList
	 * @param order
	 * @param hardgoodShippingGroupList
	 */
	
	public void calculateShippingCost(List<ShipMethodVO> shipMethodVOList,
			final BBBOrder order,
			List<HardgoodShippingGroup> hardgoodShippingGroupList) {
		
		// calculate cost for each shipping group in order for each method
		for (ShipMethodVO shipMethodVOItem : shipMethodVOList) {
			double shippingCharges = 0.0;
			for (HardgoodShippingGroup hardgoodShippingGroupItem : hardgoodShippingGroupList) {
				shippingCharges = 0.0;
				try {
					if(!(hardgoodShippingGroupItem.getCommerceItemRelationships() == null ||
							hardgoodShippingGroupItem.getCommerceItemRelationships().isEmpty())){
						
						if(shipMethodVOItem.getShipMethodId().equals(BBBCoreConstants.SHIP_METHOD_STANDARD_ID)){
							//iterates through the PricingAdjustments and populate the discounted shipping cost for the Free Standard Shipping case.	
							shippingCharges += ((BBBPricingTools)getPricingTools()).fillAdjustmentsForShipMethod(hardgoodShippingGroupItem,order,shipMethodVOItem.getShipMethodId());
						}else {
							shippingCharges += ((BBBPricingTools) getPricingTools()).calculateShippingCost(order.getSiteId(),shipMethodVOItem.getShipMethodId(),(HardgoodShippingGroup) hardgoodShippingGroupItem, null);
						}
				}
					
				} catch (PricingException exp) {
					if (isLoggingError()) {
						logError("Exception occured while fetching shipping methods " +
								"for perOrder flow - ", exp);
					}
				}
			}
			if(isLoggingDebug())
				vlogDebug("GetApplicableShippingMethodsDroplet::calculateShippingCost() Shipping Method :: {0} Shipping Charge:: {1}",shipMethodVOItem.getShipMethodId(), shippingCharges);
			shipMethodVOItem.setShippingCharge(shippingCharges);

		}
	}

}
