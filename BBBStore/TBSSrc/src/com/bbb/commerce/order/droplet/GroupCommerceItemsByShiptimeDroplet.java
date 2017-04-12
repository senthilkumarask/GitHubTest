package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.TBSOrder;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.utils.BBBUtility;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This class is used to combine the commerce items based on the shiptime.
 *
 */
public class GroupCommerceItemsByShiptimeDroplet extends BBBDynamoServlet {
	
	private List<String> mAvailableShiptimecodes = new ArrayList<String>();
	private String mVdcSkuGenericHeader;

	
	public String getVdcSkuGenericHeader() {
		return mVdcSkuGenericHeader;
	}


	public void setVdcSkuGenericHeader(String pVdcSkuGenericHeader) {
		mVdcSkuGenericHeader = pVdcSkuGenericHeader;
	}


	public List<String> getAvailableShiptimecodes() {
		return mAvailableShiptimecodes;
	}


	public void setAvailableShiptimecodes(List<String> pAvailableShiptimecodes) {
		mAvailableShiptimecodes = pAvailableShiptimecodes;
	}



	@SuppressWarnings("unchecked")
	@Override
	public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) throws ServletException, IOException {
		vlogDebug("GroupCommerceItemsByShiptimeDroplet :: service() method :: START");
		Map<String, List<CommerceItemVO>> cItemsByShipTime = new HashMap<String, List<CommerceItemVO>>(); 
		List <String>lShipTimeList = getAvailableShiptimecodes();
		final String VDC_SKU_GENERIC_HEADER = getVdcSkuGenericHeader();
		List<CommerceItemVO> lCommerceItemVOs = null;
		Object orderObj = pReq.getObjectParameter("order");
		ShippingGroup shipObj = (ShippingGroup) pReq.getObjectParameter("shipGroup");
		Object citemVOs = pReq.getObjectParameter(BBBCoreConstants.COMMERCE_ITEM_LIST);
		String itemMovedFromCartShipTime = pReq.getParameter("itemMovedFromCartShipTime");
		if(orderObj != null && shipObj != null && citemVOs != null){
			String lShipTime = null;
			
			TBSOrder order = (TBSOrder) orderObj;
			lCommerceItemVOs = (List<CommerceItemVO>) citemVOs;
			
			
			List relations = order.getRelationships();
			BBBShippingGroupCommerceItemRelationship shippingGroupRelationship = null;
			CommerceItem citem = null;
			List<String> cIds = new ArrayList<String>();
			TBSCommerceItem tbsItem = null;
			for (Object object : relations) {
				if(object instanceof BBBShippingGroupCommerceItemRelationship){
					shippingGroupRelationship = (BBBShippingGroupCommerceItemRelationship) object;
					citem  = shippingGroupRelationship.getCommerceItem();
					if(citem instanceof BBBCommerceItem && shippingGroupRelationship.getShippingGroup().getId().equals(shipObj.getId())){
						Object lValue = null;
						if(citem instanceof TBSCommerceItem){
							tbsItem = (TBSCommerceItem) citem;
							lValue = tbsItem.getPropertyValue("shipTime");
						}
						for (CommerceItemVO lCommerceItemVO : lCommerceItemVOs) {
							if(lCommerceItemVO.getBBBCommerceItem().getId().equals(citem.getId())){
								if(lValue != null){
									if(!lShipTimeList.contains(lValue)){
										lShipTime = VDC_SKU_GENERIC_HEADER;
									} else {
										lShipTime = (String) lValue;
										}
									if(!StringUtils.isBlank(lShipTime)){
										List<CommerceItemVO> lItemsList = cItemsByShipTime.get(lShipTime);
										if(lItemsList == null) {
											lItemsList = new ArrayList<CommerceItemVO>();
											cItemsByShipTime.put(lShipTime, lItemsList);
										}
										if(!cIds.contains(lCommerceItemVO.getBBBCommerceItem().getId())){
											cIds.add(lCommerceItemVO.getBBBCommerceItem().getId());
											lItemsList.add(lCommerceItemVO);
										}
									}
								}
								if(lValue == null || StringUtils.isBlank(lShipTime)){
									List<CommerceItemVO> lList = cItemsByShipTime.get(" ");
									if(lList == null) {
										lList = new ArrayList<CommerceItemVO>();
										cItemsByShipTime.put(" ", lList);
									}
									if(!cIds.contains(lCommerceItemVO.getBBBCommerceItem().getId())){
										cIds.add(lCommerceItemVO.getBBBCommerceItem().getId());
										lList.add(lCommerceItemVO);
									}
								}
							}
						}
					}
				}
			}
			
		} else if(citemVOs != null) {
			lCommerceItemVOs = (List<CommerceItemVO>) citemVOs;
			//Object shipObj = pReq.getObjectParameter("shipGroup");
			vlogDebug("No of commerce Items :: "+lCommerceItemVOs.size());
			String lShipTime = null;
			
			
			for (CommerceItemVO lCommerceItemVO : lCommerceItemVOs) {
				
				Object lValue = lCommerceItemVO.getBBBCommerceItem().getPropertyValue("shipTime");
				
				if(lValue != null){
					if(!lShipTimeList.contains(lValue)){
						lShipTime = VDC_SKU_GENERIC_HEADER;
					} else {
						lShipTime = (String) lValue;
						}
				} 
				vlogDebug("Ship time of  :: "+lCommerceItemVO.getBBBCommerceItem().getId() + " item is :: "+lShipTime);
				if(!StringUtils.isBlank(lShipTime)){
					List<CommerceItemVO> lItemsList = cItemsByShipTime.get(lShipTime);
					
					if(lItemsList == null) {
						lItemsList = new ArrayList<CommerceItemVO>();
						cItemsByShipTime.put(lShipTime, lItemsList);
					}
					lItemsList.add(lCommerceItemVO);
				} else if(lValue == null || StringUtils.isBlank(lShipTime)){
					List<CommerceItemVO> lList = cItemsByShipTime.get(" ");
					if(lList == null) {
						lList = new ArrayList<CommerceItemVO>();
						cItemsByShipTime.put(" ", lList);
					}
					lList.add(lCommerceItemVO);
				}
			}
		}
		
		if(!cItemsByShipTime.isEmpty()){
			pReq.setParameter("itemsByShipTime", cItemsByShipTime);
			if(!BBBUtility.isEmpty(itemMovedFromCartShipTime) && cItemsByShipTime.containsKey(itemMovedFromCartShipTime)) {
				pReq.setParameter("movedSFLItemShipTimePresent", BBBCoreConstants.TRUE);
			} else  {
				pReq.setParameter("movedSFLItemShipTimePresent", BBBCoreConstants.FALSE);
			}
			pReq.serviceParameter("output", pReq, pRes);
		} else {
			vlogDebug("No commerce items found to get the ship time :: ");
			pReq.serviceParameter("empty", pReq, pRes);
		}
		vlogDebug("GroupCommerceItemsByShiptimeDroplet :: service() method :: END");
	}
}
