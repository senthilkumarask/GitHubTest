package com.bbb.simplifyRegistry.droplet;


import java.util.ArrayList;
import java.util.List;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kickstarters.KickStarterVO;
import com.bbb.kickstarters.droplet.KickStarterDetailsDroplet;
import com.bbb.simplifyRegistry.RegistryInputVO;
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO;
import com.bbb.simplifyRegistry.manager.SimplifyRegistryManager;

/**
 * This class retrives registryInputs by registry type
 * 
 * @author dwaghmare
 * 
 */
public class RegistryInputsDroplet extends BBBDynamoServlet {
	
	public SimplifyRegistryManager simplifyRegistryManager;
	
	
	public SimplifyRegistryManager getSimplifyRegistryManager() {
		return simplifyRegistryManager;
	}


	public void setSimplifyRegistryManager(
			SimplifyRegistryManager simplifyRegistryManager) {
		this.simplifyRegistryManager = simplifyRegistryManager;
	}


	/**
	 * to hold error variable
	 */
	public static final String OPARAM_ERROR = "error";
	

	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws javax.servlet.ServletException, java.io.IOException {
		String eventType = ((String) pRequest.getLocalParameter("eventType"));
		RegistryInputsByTypeVO registryInputsByTypeVO = new RegistryInputsByTypeVO();
		List<RegistryInputVO> registryInputList =(List) new ArrayList<RegistryInputVO>();
		try {
			registryInputsByTypeVO= getSimplifyRegistryManager().getRegInputsByRegType(eventType);
			if(registryInputsByTypeVO !=null){
				registryInputList =registryInputsByTypeVO.getRegistryInputList();				
				pRequest.setParameter("registryInputsByTypeVO",registryInputsByTypeVO);
				pRequest.setParameter("registryInputList",registryInputList);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
						pResponse);
			}else{
				pRequest.serviceParameter(BBBCmsConstants.EMPTY, pRequest,
						pResponse);
			}
		} catch (RepositoryException e) {
			
			logError(e.getSourceException());
			
		}
		BBBPerformanceMonitor.end(KickStarterDetailsDroplet.class.getName() + " : " + "service");
	}
}
