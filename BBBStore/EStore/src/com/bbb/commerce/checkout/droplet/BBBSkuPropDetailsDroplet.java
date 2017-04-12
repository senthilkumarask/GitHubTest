package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.repository.Repository;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * To populate get sku prop65 flag details
 * 
 * @author ajosh8
 * @version 1.0
 */
public class BBBSkuPropDetailsDroplet extends BBBDynamoServlet {

	private static final String EMPTY = "empty";
	private static final String ORDER_OBJECT = "order";
	private static final String CA = "CA";
	private static final String OUTPUT = "output";
	private static final String SKU_PROD_STATUS = "skuProdStatus";
	private static final String SKU_DETAILS = "skuDetails";
	

	private BBBCatalogTools catalogTools;
	private  Repository orderRepository;

	public Repository getOrderRepository() {
		return orderRepository;
	}


	public void setOrderRepository(Repository orderRepository) {
		this.orderRepository = orderRepository;
	}


	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * This will interact with catalog to get sku prop65 flag details. 
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		logDebug("Entry BBBSkuPropDetailsDroplet.service");
		Order order=null;
		Map<String, String> skuProdMap = new HashMap<String, String>();
		
		List<String> listSku=new ArrayList<String>();
		Map<String, String> skuDetails = new HashMap<String, String>();
		String skuId = null ;

		if(pRequest.getObjectParameter(ORDER_OBJECT) !=null){
			order = (Order)pRequest.getObjectParameter(ORDER_OBJECT);
            logDebug("Order Details: "+order);
		 	for(Object shippingGroupObj: order.getShippingGroups()){
		 		
				ShippingGroup sg = (ShippingGroup) shippingGroupObj;
				if (sg instanceof BBBHardGoodShippingGroup) {
					if (((BBBHardGoodShippingGroup)sg).getShippingAddress().getState().equalsIgnoreCase(CA)) {
						for (Object sgcirObj : sg.getCommerceItemRelationships()) {
							ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) sgcirObj;
							listSku.add(sgcir.getCommerceItem().getCatalogRefId());

							 skuId = sgcir.getCommerceItem().getCatalogRefId();
							try {
								skuDetails.put(skuId, ((SKUDetailVO)getCatalogTools().getSKUDetails(order.getSiteId(), skuId, false)).getDisplayName());
							} catch (BBBSystemException e) {
				              logError("Sku not present in catalog" +e.getMessage(),e);
							} catch (BBBBusinessException e) {
								logError("Repository Exception"+e.getMessage(),e);
							}
							if(skuId != null){
								try {
									skuProdMap= getCatalogTools().getSkuPropFlagStatus(skuId);
									Map<String, Map> skuDetailsMap = new HashMap<String, Map>();
									skuDetailsMap.put(skuId, skuProdMap);
									if(skuProdMap.size()> 0 && !skuProdMap.isEmpty()){
										pRequest.setParameter(SKU_PROD_STATUS, skuDetailsMap);
										pRequest.setParameter(SKU_DETAILS, skuDetails);
										pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
									}else{
										pRequest.serviceParameter(EMPTY, pRequest, pResponse);
								 	}
								} catch (BBBBusinessException e) {
									logError("Sku not present in catalog"+e.getMessage(),e);
								} catch (BBBSystemException e) {
									logError("Repository Exception" + e.getMessage(),e);
								}
							}
						}
					}
				}else{
					logDebug(" Shiiping Group is not HardGoodShippingGroup");
				}
				

				
			}
		}else{
			 logDebug("Order is null");
		}
		logDebug("Exit BBBSkuPropDetailsDroplet.service");
	}
}