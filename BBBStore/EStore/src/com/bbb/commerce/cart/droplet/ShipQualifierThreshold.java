package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import atg.adapter.gsa.GSAItem;
import atg.commerce.promotion.PromotionConstants;
import atg.commerce.promotion.PromotionUpsellTools;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;


/**
 * This droplet returns the shipping threshold for an order.
 * Updated and Improved by Rashid M
 */
public class ShipQualifierThreshold extends BBBDynamoServlet {

	private PromotionUpsellTools promotionUpsellTools;
	
	//parameters
	public static final ParameterName TYPE = ParameterName.getParameterName("type");
	public static final ParameterName ORDER = ParameterName.getParameterName("order");
	public static final ParameterName CLOSENESS_QUALIFIER=ParameterName.getParameterName("closenessQualifier");
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    public static final ParameterName ERROR_OPARAM = ParameterName.getParameterName("error");
	
	//constants
	private static final String SHIPPINGTHRESHOLD= "shippingThreshold";
	
	/**
	 * @return the promotionUpsellTools
	 */
	public PromotionUpsellTools getPromotionUpsellTools() {
		return promotionUpsellTools;
	}
	/**
	 * @param promotionUpsellTools the promotionUpsellTools to set
	 */
	public void setPromotionUpsellTools(PromotionUpsellTools promotionUpsellTools) {
		this.promotionUpsellTools = promotionUpsellTools;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		logDebug("inside service::");
		String qualifierType= pRequest.getParameter(TYPE);
		BBBOrder order = (BBBOrder) pRequest.getObjectParameter(ORDER);
		
		if(qualifierType != null && order != null){
			logDebug("qualifierType::"+qualifierType);
			logDebug("order id::"+ order.getId());
			
			Double orderTotal= order.getPriceInfo().getAmount();
			List closenessQualifier = new ArrayList();
				closenessQualifier=(List)pRequest.getObjectParameter(CLOSENESS_QUALIFIER);
				if(closenessQualifier==null && (qualifierType.equals("shipping")) )
				{
					closenessQualifier=promotionUpsellTools
							.compileShippingClosenessQualifiers(order);
				}
			if (closenessQualifier != null && !closenessQualifier.isEmpty()) {
				GSAItem qualifier = (GSAItem) closenessQualifier.get(0);
				String pmdlRule = (String) qualifier.getPropertyValue("pmdlRule");
				if (pmdlRule != null) {
					Document pmdlDoc = Jsoup.parse(pmdlRule);
					if (pmdlDoc.select("less-than") != null) {
						for (Element e : pmdlDoc.select("less-than")) {
							if (e.select("string-value") != null) {
								String stringValue = e.select("string-value")
										.first().text();
								if (stringValue != null) {
									double shippingLimit = Double
											.parseDouble(stringValue);
									if (shippingLimit > orderTotal) {
										double shipThreshold = shippingLimit
												- orderTotal;
										double shippingThreshold = Math
												.round(shipThreshold * 100) / 100.00;
										logDebug("shippingThreshold::"+shippingThreshold);
										pRequest.setParameter(SHIPPINGTHRESHOLD,
												shippingThreshold);
										pRequest.serviceParameter(
												BBBCoreConstants.OPARAM, pRequest,
												pResponse);
									}
								}
							}
						}
					}
				}
			} else {
				logDebug("not able to find any close qualifiers");
				pRequest.serviceParameter(BBBCoreConstants.EMPTY, pRequest,
						pResponse);
			}
		}else {
			logDebug("invalid input parameters");
			//input params are null log error and return
			String msg = PromotionConstants.getStringResource("invalidTypeParameter");
		    logError(msg);
		    pRequest.setParameter("errorMsg", msg);
		    pRequest.serviceLocalParameter(ERROR_OPARAM, pRequest, pResponse);
		    return;		
		}
	}
}
