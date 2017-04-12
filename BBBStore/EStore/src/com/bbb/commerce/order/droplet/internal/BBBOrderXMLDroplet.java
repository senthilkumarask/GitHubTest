package com.bbb.commerce.order.droplet.internal;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.checkout.tibco.SubmitOrderMarshaller;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBException;
import com.bbb.utils.BBBUtility;


public class BBBOrderXMLDroplet extends BBBDynamoServlet {
	
	public static final ParameterName ORDER = ParameterName.getParameterName("order");
	public static final ParameterName ISOLDORDER = ParameterName.getParameterName("isOldOrder");
	public static final ParameterName VIEW_SAVED_XML = ParameterName.getParameterName("viewSavedXML");
	public final static ParameterName OUTPUT = ParameterName.getParameterName("output");
	
	private SubmitOrderMarshaller marshaller;

	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		final BBBOrderImpl order = (BBBOrderImpl) pRequest.getObjectParameter(ORDER);
		Boolean isOldOrder = false;
		Boolean viewSavedXML = false;
		//Check for older orders
		if(pRequest.getObjectParameter(ISOLDORDER)!=null){
			String oldeorder=(String)pRequest.getObjectParameter(ISOLDORDER);
			isOldOrder=Boolean.valueOf(oldeorder);
			order.setOldOrder(isOldOrder);
		}
		if(pRequest.getObjectParameter(VIEW_SAVED_XML)!=null){
			String savedXML = (String)pRequest.getObjectParameter(VIEW_SAVED_XML);
			viewSavedXML = Boolean.valueOf(savedXML);
		}
		try {
			 if (order != null) {
				String orderXML = viewSavedXML ? order.getOrderXML() : getMarshaller().getOrderAsXML(order);
				pRequest.setParameter("orderXML", orderXML);
				pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
				logDebug("orderXML: " + orderXML);
				
			 }		
		} catch (BBBException e) {			
				logError(e);			               
		}
		
	}	

	/**
	 * @return the marshaller
	 */
	public SubmitOrderMarshaller getMarshaller() {
		return marshaller;
	}

	/**
	 * @param marshaller the marshaller to set
	 */
	public void setMarshaller(SubmitOrderMarshaller marshaller) {
		this.marshaller = marshaller;
	}

	
}
