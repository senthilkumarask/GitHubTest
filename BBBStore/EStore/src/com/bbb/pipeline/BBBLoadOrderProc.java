package com.bbb.pipeline;

import java.util.HashMap;

import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.processor.ProcLoadOrderObject;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;

/**
 * This class copies the emailAddress property from address item to order item
 * 
 * Order, OrderManager must be parameters in the map
 * As of the time of this writing the super method ProcLoadOrderObject ensures both are in map
 *
 */
public class BBBLoadOrderProc extends ProcLoadOrderObject {
	
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		super.runProcess(pParam, pResult);
		
		HashMap map = (HashMap) pParam;
		Order order = (Order) map.get(BBBCoreConstants.ORDER_FROM_MAP);
		String orderId = order.getId();
		MutableRepositoryItem orderItem = (MutableRepositoryItem) map.get(BBBCoreConstants.ORDER_REPO_ITEM);
		OrderManager orderManager = (OrderManager) map.get(BBBCoreConstants.ORDER_MANAGER);
		MutableRepository orderRepo = (MutableRepository) map.get(BBBCoreConstants.ORDER_REPOSITORY);
		
		if (orderItem == null) {
			orderItem = orderRepo.getItemForUpdate(orderId,
					orderManager.getOrderItemDescriptorName());
			if (orderItem == null) {
				throw new BBBSystemException(BBBCoreConstants.ORDER_NOT_AVAILABLE);
			}
		}
		
		((BBBOrderManager)orderManager).addEmailAddressToBBBOrder(orderItem);
		
		return 1;
	}
}