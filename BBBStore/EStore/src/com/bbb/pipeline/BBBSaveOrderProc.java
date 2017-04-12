package com.bbb.pipeline;

import java.util.HashMap;

import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.processor.ProcSaveOrderObject;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;

/**
 * This class save the emailAddress property from address item to order item
 *
 */
public class BBBSaveOrderProc extends ProcSaveOrderObject {

	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		HashMap map = (HashMap) pParam;
		String orderId = (String) map.get(BBBCoreConstants.ORDER_ID_FROM_MAP);
		Order order = (Order) map.get(BBBCoreConstants.ORDER_FROM_MAP);
		MutableRepositoryItem orderItem = (MutableRepositoryItem) map.get(BBBCoreConstants.ORDER_REPO_ITEM);
		OrderManager orderManager = (OrderManager) map.get(BBBCoreConstants.ORDER_MANAGER);
		MutableRepository orderRepo = (MutableRepository) map.get(BBBCoreConstants.ORDER_REPOSITORY);
		
		if(null == orderId && null == order) {
			throw new BBBSystemException(BBBCoreConstants.INAVLID_ORDER);
		}
		if (order != null) {
			orderId = order.getId();
		}
		if(null == orderManager) {
			throw new BBBSystemException(BBBCoreConstants.ORDERMANAGER_NOT_AVAILABLE);
		}
		if (orderItem == null) {
			orderItem = orderRepo.getItemForUpdate(orderId,
					orderManager.getOrderItemDescriptorName());
			if (orderItem == null) {
				throw new BBBSystemException(BBBCoreConstants.ORDER_NOT_AVAILABLE);
			}
		}
		
		((BBBOrderManager)orderManager).addEmailAddressToBBBOrder(orderItem);
		
		//Super will call updateOrder
		return super.runProcess(pParam, pResult);
	}
}
