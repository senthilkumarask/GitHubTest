package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.purchase.RepriceOrder;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.utils.CommonConfiguration;

public class BBBRepriceOrder extends RepriceOrder {
	 private CommonConfiguration commonConfiguration;

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		 
			logOrderDetails("preRepriceOrder");
		    callSuperService(pRequest, pResponse);
		    logOrderDetails("postRepriceOrder");
		 

	}

	protected void callSuperService(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		super.service(pRequest, pResponse);
	}

	private void logOrderDetails(String logMsg) {
		try { 
			if(getCommonConfiguration().isLogDebugEnableOnRepriceOrderForOrderDetail()){    
				OrderImpl order = (BBBOrderImpl) getOrder();
				 
				 RepositoryItem orderItem=  getOrderManager().getOrderTools().getOrderRepository().getItem(order.getId(), "order") ;
		         int repVersion= (Integer) orderItem.getPropertyValue("version"); 
		         logDebug(logMsg+":[" + order.toString() + " ,orderVersion:" + order.getVersion()+ ",repOrderVersion:"+repVersion+"]" + " , CommerceItemsCount:["+ order.getCommerceItemCount() + "], OrderHexString:["+order.getClass().getName() + "@" + Integer.toHexString(hashCode())+"]");
	            List<CommerceItem> commerceItems =	order.getCommerceItems();	
			     if(order.getCommerceItemCount() > 0){
				    StringBuffer commerceItemslogs = new StringBuffer("CommerceItems:{");
				    for(CommerceItem commerceItem :  commerceItems){
					   commerceItemslogs.append("commerceItemId [" + commerceItem.getId()+",catalogRefId:"+commerceItem.getCatalogRefId()+ ",Qty:"+commerceItem.getQuantity()+"] ");
					   }
			          commerceItemslogs.append("}");
			          logDebug(logMsg +": "+commerceItemslogs.toString());
			       } else{
			    	   logDebug(logMsg +":No CommerceItems:{}"); 
			       }
			}
	     } catch (Exception e) {
	    	 logError("BBBRepriceOrder:logOrderDetails", e);
			}
	}
	
	@Override
    public final boolean isLoggingDebug() {
        return this.getCommonConfiguration().isLogDebugEnableOnRepriceOrder();
    }

	public CommonConfiguration getCommonConfiguration() {
		return commonConfiguration;
	}

	public void setCommonConfiguration(CommonConfiguration commonConfiguration) {
		this.commonConfiguration = commonConfiguration;
	}

}
