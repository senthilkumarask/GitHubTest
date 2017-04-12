package com.bbb.commerce.pricing.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.order.purchase.TBSReasonVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;

public class TBSOverrideReasonsDroplet extends BBBDynamoServlet {

	private Map<String,String> itemReasonCodesMap;
	private List<String> competitors;
	private Map<String,String> surchargeReasonCodesMap;
	private Map<String,String> shipReasonCodesMap;
	private Map<String,String> taxReasonCodesMap;
	private Map<String,String> giftWrapReasonCodesMap;
	private static final String OVERRIDE_TYPE="OverrideType";
	private static final String ITEM="item";
	private static final String COMPETITORS_CONSTANT="competitors";
	private static final String SHIPPING="shipping";
	private static final String SURCHARGE="surcharge";
	private static final String TAX="tax";
	private static final String GIFT_WRAP="giftwrap";
	private static final String REASONS="reasons";

	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String listType = pRequest.getParameter(OVERRIDE_TYPE);

        List<TBSReasonVO> reasons = null;
        
        Map<String, String> reasonMap=new HashMap<String,String>();
        
        if(listType ==null){
        	return;
        }
				
		if( listType.equalsIgnoreCase(ITEM)) {
			reasonMap=getItemReasonCodesMap();
		}
		else if( listType.equalsIgnoreCase(COMPETITORS_CONSTANT)) {
			reasonMap = getCompetitorsCodeMap();
		}
		else if( listType.equalsIgnoreCase(SHIPPING)) {
			reasonMap = getShipReasonCodesMap();
		}
		else if( listType.equalsIgnoreCase(SURCHARGE)) {
			reasonMap = getSurchargeReasonCodesMap();
		}
		else if( listType.equalsIgnoreCase(TAX)) {
			reasonMap = getTaxReasonCodesMap();
		}
		else if( listType.equalsIgnoreCase(GIFT_WRAP)) {
			reasonMap=getGiftWrapReasonCodesMap();
		}

		pRequest.setParameter(REASONS, reasonMap);
		if( isLoggingDebug() ) {
			logDebug("reasons = " + reasons);
		}
		
		pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
	}
	
	
	public Map<String, String> getCompetitorsCodeMap() {

		if (isLoggingDebug()) {
			logDebug("Building reason list for Competitors");
		}

		Map<String, String> reasonMap = new HashMap<String, String>();

		for (String competitor : getCompetitors()) {
			reasonMap.put(competitor, competitor);
		}
		return reasonMap;
	}
	
		public Map<String, String> getItemReasonCodesMap() {
			return itemReasonCodesMap;
		}


		public void setItemReasonCodesMap(Map<String, String> itemReasonCodesMap) {
			this.itemReasonCodesMap = itemReasonCodesMap;
		}


		public List<String> getCompetitors() {
			return competitors;
		}


		public void setCompetitors(List<String> competitors) {
			this.competitors = competitors;
		}


		public Map<String, String> getSurchargeReasonCodesMap() {
			return surchargeReasonCodesMap;
		}


		public void setSurchargeReasonCodesMap(
				Map<String, String> surchargeReasonCodesMap) {
			this.surchargeReasonCodesMap = surchargeReasonCodesMap;
		}


		public Map<String, String> getShipReasonCodesMap() {
			return shipReasonCodesMap;
		}


		public void setShipReasonCodesMap(Map<String, String> shipReasonCodesMap) {
			this.shipReasonCodesMap = shipReasonCodesMap;
		}


		public Map<String, String> getTaxReasonCodesMap() {
			return taxReasonCodesMap;
		}


		public void setTaxReasonCodesMap(Map<String, String> taxReasonCodesMap) {
			this.taxReasonCodesMap = taxReasonCodesMap;
		}


		public Map<String, String> getGiftWrapReasonCodesMap() {
			return giftWrapReasonCodesMap;
		}


		public void setGiftWrapReasonCodesMap(Map<String, String> giftWrapReasonCodesMap) {
			this.giftWrapReasonCodesMap = giftWrapReasonCodesMap;
		}

}