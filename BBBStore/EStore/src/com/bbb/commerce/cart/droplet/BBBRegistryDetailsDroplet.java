package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
/**
 * @author kmagud
 *
 * Refactored the code from BBBCartDisplayDroplet to get the registry details
 */
public class BBBRegistryDetailsDroplet extends BBBDynamoServlet {
	
	/** Constant for string regOutput. */
	private static final String REG_OUTPUT = "regOutput";
	
	@SuppressWarnings("unchecked")
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		if (pRequest.getObjectParameter(BBBCoreConstants.REGISTRY_ID) != null
				&& pRequest.getObjectParameter(BBBCoreConstants.ORDER) != null) {
			String registryType;
			String registrantEmail;
			BBBOrder order = (BBBOrder) pRequest
					.getObjectParameter(BBBCoreConstants.ORDER);

			Map<String, RegistrySummaryVO> registryMap = order.getRegistryMap();

			RegistrySummaryVO regSummVO = registryMap.get((String) pRequest
					.getObjectParameter(BBBCoreConstants.REGISTRY_ID));
			if (regSummVO != null && regSummVO.getRegistryType() != null) {

				registryType = regSummVO.getRegistryType()
						.getRegistryTypeDesc();
				registrantEmail = regSummVO.getRegistrantEmail();
				
				logDebug("registryType: " + registryType);
				pRequest.setParameter(BBBCoreConstants.REGISTRY_TYPE,
						registryType);
				pRequest.setParameter(BBBCoreConstants.REGISTRANT_EMAIL,
						registrantEmail);
				pRequest.serviceParameter(REG_OUTPUT, pRequest, pResponse);

			}
		}
	
	}

}
