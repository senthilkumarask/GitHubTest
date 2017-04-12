package com.bbb.autowaiveshippingservice;



import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.webservices.BBBWebservicesConfig;
import com.bbb.order.bean.TBSCommerceItem;

public class AutoWaiveShippingInfoService extends BBBGenericService {

	private BBBWebservicesConfig webservicesConfig;
	
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

	/**
	 * @return the webservicesConfig
	 */
	public BBBWebservicesConfig getWebservicesConfig() {
		return webservicesConfig;
	}

	/**
	 * @param webservicesConfig the webservicesConfig to set
	 */
	public void setWebservicesConfig(BBBWebservicesConfig webservicesConfig) {
		this.webservicesConfig = webservicesConfig;
	}

	/**
	 * @param onHoldDetails 
	 
	 */
    public ServiceResponseIF getAutoWaiveShippingInfo(Order order, Map<String, BigInteger> onHoldDetails,String currentId) {
		
    	vlogInfo("getAutoWaiveShippingInfo called with params "+ "Order with orderID-" + order.getId());
		ServiceRequestIF sreqVo;
		sreqVo = prepareObjectForAutoWaiveShippingInfoRequest(order, onHoldDetails,currentId);
		
		return invokeService(sreqVo);
	}
	
	private ServiceRequestIF prepareObjectForAutoWaiveShippingInfoRequest(Order order, Map<String, BigInteger> onHoldDetails,String currentId) {
		
			vlogInfo("prepareObjectForAutoWaiveShippingInfoRequest called with params " + "Order with orderID-" + order.getId());
			
			AutoWaiveShippingInfoRequestOrderVO request = new AutoWaiveShippingInfoRequestOrderVO();
			AutoWaiveShippingInfoRequestOrderHeaderVO orderHeader= prepareOrderHeader(order);
			List<AutoWaiveShippingInfoRequestOrderLineItemVO> orderLineItems = prepareOrderLineItems(order, onHoldDetails,currentId);
			
			BigInteger orderId = new BigInteger(order.getId());
			request.setOrderId(orderId);
			
			request.setHeader(orderHeader);
			request.setOrderLineItems(orderLineItems);
			request.setServiceName(TBSConstants.AUTO_WAIVE_SHIPPING_SERVICE_NAME);
			
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			String storeId = storeNumFromCookie(pRequest);
			
			request.setStoreNum(storeId);
			return request;
			
			
		}
	
	private AutoWaiveShippingInfoRequestOrderHeaderVO prepareOrderHeader(Order order){
		vlogInfo("prepareOrderHeader for order:"+order.getId());
		AutoWaiveShippingInfoRequestOrderHeaderVO orderHeader = new AutoWaiveShippingInfoRequestOrderHeaderVO();
		String servicewsdl = (String) (getWebservicesConfig().getServiceToWsdlMap()).get(TBSConstants.AUTO_WAIVE_SHIPPING_SERVICE_NAME);
		//Put Header Items
		//orderHeader.setApplicationName("TestApplication");
		//orderHeader.setHostName("BBBY");
		try {
			if(getCatalogTools().getAllValuesForKey(servicewsdl, TBSConstants.AUTO_WAIVE_SERVICE_USER_TOKEN) != null){
				orderHeader.setUserToken(getCatalogTools().getAllValuesForKey(servicewsdl, TBSConstants.AUTO_WAIVE_SERVICE_USER_TOKEN).get(BBBWebServiceConstants.ZERO));
			}
		} catch (BBBSystemException e) {
			vlogError("BBBSystemException occurred while getting configkey "+e);
		} catch (BBBBusinessException e) {
			vlogError("BBBBusinessException occurred while getting configkey "+e);
		}
		return orderHeader;
	}
	
	@SuppressWarnings("unchecked")
	private List<AutoWaiveShippingInfoRequestOrderLineItemVO> prepareOrderLineItems(Order order, Map<String, BigInteger> onHoldDetails,String currentId) {
		vlogInfo("prepareOrderLineItems for order:"+order.getId());
		
		List<AutoWaiveShippingInfoRequestOrderLineItemVO> orderLineItems=new ArrayList<AutoWaiveShippingInfoRequestOrderLineItemVO>();
		List<CommerceItem> commerceItems = order.getCommerceItems();
		AutoWaiveShippingInfoRequestOrderLineItemVO lOrderOrderLineItems = null;
		String zero = "0";
		for (CommerceItem comItem : commerceItems) {
		     if(comItem instanceof TBSCommerceItem) {
		    	 TBSCommerceItem commrceItem = (TBSCommerceItem)comItem;
			      lOrderOrderLineItems = new AutoWaiveShippingInfoRequestOrderLineItemVO();
			      lOrderOrderLineItems.setOrderLineId(commrceItem.getId());
			      lOrderOrderLineItems.setOrderQty(BigInteger.valueOf(commrceItem.getQuantity()));
			      lOrderOrderLineItems.setSku(new BigInteger(commrceItem.getCatalogRefId()));

			      if(!StringUtils.isBlank(commrceItem.getRegistryId())){
			    	  long regId = Long.parseLong(commrceItem.getRegistryId());
			    	  lOrderOrderLineItems.setRegistryNum(BigInteger.valueOf(regId));
			      }
			      if(currentId == null){
			    	  lOrderOrderLineItems.setSkuClassification(commrceItem.getAutoWaiveClassification());
			      }else if(!currentId.equalsIgnoreCase(commrceItem.getId())){
			    	  lOrderOrderLineItems.setSkuClassification(commrceItem.getAutoWaiveClassification());
			      }
			      
			      if(onHoldDetails != null && !onHoldDetails.isEmpty()){
			    	  lOrderOrderLineItems.setStoreOnHandQty(onHoldDetails.get(commrceItem.getCatalogRefId()));
			      } else {
			    	  lOrderOrderLineItems.setStoreOnHandQty(new BigInteger(zero));
			      }
			      orderLineItems.add(lOrderOrderLineItems);
		     }
		}
		return orderLineItems;
	}
	
	protected ServiceResponseIF invokeService(ServiceRequestIF sreqVo) {

		AutoWaiveShippingInfoResponseOrderVO sResVo = null;
		if(sreqVo != null){
			vlogInfo("invokeService method Start with request "+sreqVo.toString());
			try {
				sResVo = (AutoWaiveShippingInfoResponseOrderVO) ServiceHandlerUtil.invoke(sreqVo);
			} catch (BBBBusinessException e) {
				vlogError("BBBBusinessException occurred :: "+e);
			} catch (BBBSystemException e) {
				vlogError("BBBSystemException occurred :: "+e);
			}
		}
		return sResVo;
	}
	
	/**
	 * This is used to get the store number from cookie
	 * @param pRequest
	 * @return
	 */
	private String storeNumFromCookie(HttpServletRequest pRequest) {
		Cookie cookies[] = pRequest.getCookies();
		String cookieValue = null;
		if (cookies != null)
			for (int i = 0; i < cookies.length; i++) {
				String name = cookies[i].getName();
				if (TBSConstants.STORE_NUMBER_COOKIE.equals(name)) {
					cookieValue = cookies[i].getValue();
					break;
				}
			}
		return cookieValue;
	}
}
