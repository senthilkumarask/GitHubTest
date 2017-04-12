package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;

public class BBBSuperScriptDroplet extends BBBDynamoServlet{
	
	private LblTxtTemplateManager messageHandler;
	Map<String,String> placeHolderMap= new HashMap<String,String>() ;
	 private BBBPricingTools mPricingTools = null;
	 
	 
	public void  service(final DynamoHttpServletRequest request,final DynamoHttpServletResponse response) throws ServletException, IOException
	{
//      PriceInfoVO priceInfoVO  = 	(PriceInfoVO) request.getObjectParameter(BBBCoreConstants.PRICE_INFO);
        Order order = (Order) request.getObjectParameter(BBBCoreConstants.ORDER);
        PriceInfoVO priceInfoVO =    getPricingTools().getOrderPriceInfo((OrderImpl) order);
       int count=0;
       String pageLabel= BBBCoreConstants.BLANK;
       if(priceInfoVO !=null){
       if(priceInfoVO.getOnlineEcoFeeTotal()>0)
       {
    	     
	    	 pageLabel =  getMessageHandler().getPageLabel("lbl_footnote_ecofee",request.getLocale().getLanguage() ,null);
	    	 if(pageLabel!=null)
	    	 {  
	    		 count++;
	    	     String strCount=Integer.toString(count); 
	    		 placeHolderMap.put("ecofeeFootNoteCount", strCount);
	    	 }
       }
	      if(priceInfoVO.getGiftWrapTotal()>0)
	      {
	    	  
	    	 pageLabel =  getMessageHandler().getPageLabel("lbl_footnote_giftWrap",request.getLocale().getLanguage() ,null);
	    	 if(pageLabel!=null)
	    	 {
	    		 count++;
		    	  String strCount=Integer.toString(count);
	    		 placeHolderMap.put("giftWrapFootNoteCount", strCount);
	    	 }
	      }
	      if(priceInfoVO.getFreeShipping())
	      {
	    	  
	    	 pageLabel =  getMessageHandler().getPageLabel("lbl_footnote_shipping",request.getLocale().getLanguage() ,null);
	    	 if(pageLabel!=null)
	    	 {
	    		 count++;
		    	  String strCount=Integer.toString(count); 
	    		 placeHolderMap.put("shippingFootNoteCount", strCount);
	    	 }
	      }
	      if(SiteContextManager.getCurrentSiteId()!=null && SiteContextManager.getCurrentSiteId().equals("BedBathCanada"))
	      {
	    	  pageLabel=	  getMessageHandler().getPageTextArea("txt_billing_cc_disclaimer",request.getLocale().getLanguage(), null);
	    	  if(pageLabel!=null)
		    	 {
		    		  count++;
			    	  String strCount=Integer.toString(count);
		    		  placeHolderMap.put("creditCardDisclaimer", strCount);
		    	 }
	    	 
	      }
	      request.setParameter("SuperScriptMap", placeHolderMap);
	     
	      request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
       }else{
    	   request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
       }
	}

	public LblTxtTemplateManager getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(LblTxtTemplateManager messageHandler) {
		this.messageHandler = messageHandler;
	}

	public BBBPricingTools getPricingTools() {
		return mPricingTools;
	}

	public void setPricingTools(BBBPricingTools mPricingTools) {
		this.mPricingTools = mPricingTools;
	}
}
