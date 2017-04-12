package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.TBSOrderImpl;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

public class TBSAutowaiveMessageDisplayDroplet extends BBBDynamoServlet {

	private static final String AUTOWAIVE_FREESHIP_MESSAGE = "autoWaiveMessage";
	private static final String ERROR_OUTPUT = "Error while executing the service";
	private static final String OUTPUT_PARAM_IS_ORDER_AUTOWAIVED="isOrderAutowaived";
	private static final String OUTPUT_PARAM_IS_COMMERCE_ITEM_AUTOWAIVED="isCommerceItemAutowaived";
	private static final String OUTPUT_PARAM="output";
	private static final String EMPTY_PARAM="empty";
	private static final String MESSAGE_FREE_SHIP="Free Shipping";
	private static final String OUTPUT_PARAM_COMMERCEITEM_AUTOWAIVE_MAP="commerceItemAutowaiveMap";
	
	
	
	private static final String BLANK = "";
	
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
		try {
				if (pRequest.getObjectParameter(BBBCoreConstants.ORDER) != null) {
					List<CommerceItemVO> commerceItemVOs = null;
					boolean isAutowaiveApplied=false;
					Order order = (Order) pRequest.getObjectParameter(BBBCoreConstants.ORDER);
					if(order instanceof TBSOrderImpl){
						if(((TBSOrderImpl)order).isAutoWaiveFlag() && !((TBSOrderImpl)order).getAutoWaiveClassification().isEmpty()){
							pRequest.setParameter(OUTPUT_PARAM_IS_ORDER_AUTOWAIVED, true);
							pRequest.setParameter(AUTOWAIVE_FREESHIP_MESSAGE,MESSAGE_FREE_SHIP+" "+((TBSOrderImpl)order).getAutoWaiveClassification());
							pRequest.serviceLocalParameter(OUTPUT_PARAM, pRequest,pResponse);
							isAutowaiveApplied=true;
							return;
							
						}
						
					}
				
					List<CommerceItem> commerceItems=order.getCommerceItems();
					HashMap<String,String> commerceItemAutowaiveMap=new HashMap<String,String>();
					for(CommerceItem item:commerceItems){
						if(item instanceof TBSCommerceItem){
							if(((TBSCommerceItem)item).isAutoWaiveFlag() && !((TBSCommerceItem)item).getAutoWaiveClassification().isEmpty()){
								commerceItemAutowaiveMap.put(item.getId(), ((TBSCommerceItem)item).getAutoWaiveClassification());
								pRequest.setParameter(OUTPUT_PARAM_IS_COMMERCE_ITEM_AUTOWAIVED, true);
								pRequest.setParameter(OUTPUT_PARAM_COMMERCEITEM_AUTOWAIVE_MAP,commerceItemAutowaiveMap);
								pRequest.setParameter(AUTOWAIVE_FREESHIP_MESSAGE,MESSAGE_FREE_SHIP+" "+((TBSCommerceItem)item).getAutoWaiveClassification());
								pRequest.serviceLocalParameter(OUTPUT_PARAM, pRequest,pResponse);
								isAutowaiveApplied=true;
								
							}
						}
					}
					if(isAutowaiveApplied){
						return;
					}
					else {
						pRequest.serviceLocalParameter(EMPTY_PARAM, pRequest, pResponse);
					}
				} 
			} catch (Exception e) {
				vlogError(LogMessageFormatter.formatMessage(pRequest, ERROR_OUTPUT), e);
				pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
			} 


	}
}
