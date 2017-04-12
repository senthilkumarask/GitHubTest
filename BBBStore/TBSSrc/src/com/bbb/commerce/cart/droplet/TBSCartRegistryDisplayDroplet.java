package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class TBSCartRegistryDisplayDroplet extends DynamoServlet{

	private static final String REG_OUTPUT = "regOutput";
	
	/**
	 * this method adds registryType, registryMail in request for registryId and Order passed as Parameter.
	 *
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException, IOException
	 */
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		if (pRequest.getObjectParameter(BBBCoreConstants.REGISTRY_ID) != null
				&& pRequest.getObjectParameter(BBBCoreConstants.ORDER) != null) {
			String registryType;
			String registrantEmail;
			BBBOrder order = (BBBOrder) pRequest.getObjectParameter(BBBCoreConstants.ORDER);

			Map<String, RegistrySummaryVO> registryMap = order.getRegistryMap();

			RegistrySummaryVO regSummVO = registryMap.get((String) pRequest.getObjectParameter(BBBCoreConstants.REGISTRY_ID));
			if (regSummVO != null && regSummVO.getRegistryType() != null) {

				registryType = regSummVO.getRegistryType().getRegistryTypeDesc();
				registrantEmail = regSummVO.getRegistrantEmail();

				pRequest.setParameter(BBBCoreConstants.REGISTRY_TYPE, registryType);
				pRequest.setParameter(BBBCoreConstants.REGISTRANT_EMAIL, registrantEmail);
				pRequest.serviceParameter(REG_OUTPUT, pRequest, pResponse);

			}
		}
		
	}
}
