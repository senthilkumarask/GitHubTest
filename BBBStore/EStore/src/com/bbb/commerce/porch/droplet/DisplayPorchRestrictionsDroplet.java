package com.bbb.commerce.porch.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.framework.performance.BBBPerformanceMonitor;

import atg.commerce.order.Order;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * @author sm0191

 */
public class DisplayPorchRestrictionsDroplet extends BBBDynamoServlet {

	private static final String EMPTY = "empty";
	private static final String OUTPUT = "output";
	private static final String MAP_PORCH_RESTRICTED_ADDRESS = "mapPorchRestrictedAddress";
	private static final String ORDER2 = "order";
	private PorchServiceManager porchServiceManager;
	
	 



	/**
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */


	@SuppressWarnings("unused")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start("DisplayPorchRestrictionsDroplet", "service");		 
		Order order=null;
		Map<String,String> porchRestrictedAddress = new HashMap<String,String>();
		order = (Order)pRequest.getObjectParameter(ORDER2);
		if(pRequest.getObjectParameter(ORDER2) !=null){
			porchRestrictedAddress=getPorchServiceManager().getRestrictedPorchServiceAddress(order);
		}
		
		if(!porchRestrictedAddress.isEmpty()){
			pRequest.setParameter(MAP_PORCH_RESTRICTED_ADDRESS, porchRestrictedAddress); 
			pRequest.serviceParameter(OUTPUT, pRequest, pResponse);	
		}else{
			pRequest.serviceParameter(EMPTY, pRequest, pResponse);
		}
	 
			logDebug("CLS=[DisplayPorchRestrictionsDroplet] MTHD=[Service ends]");
			BBBPerformanceMonitor.end("DisplayPorchRestrictionsDroplet", "service");
	}
	
	 

	/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}

	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}

}
