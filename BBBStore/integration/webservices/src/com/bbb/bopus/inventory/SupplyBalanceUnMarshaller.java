/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit
 * written consent is prohibited.
 *
 * Created by: skoner
 *
 * Created on: 19-JAN-2012
 * --------------------------------------------------------------------------------
 */
package com.bbb.bopus.inventory;

//BBB Imports
import org.apache.xmlbeans.XmlObject;

import com.bbb.bopus.inventory.vo.SupplyBalanceResponseVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.manh.doms.app.common.webservices.SupplyBalanceResponseDocument;
import com.manh.doms.app.common.webservices.SupplyBalanceResponseDocument.SupplyBalanceResponse;

public class SupplyBalanceUnMarshaller extends
		com.bbb.framework.webservices.ResponseUnMarshaller {
	/**
     *
     */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.framework.webservices.ResponseUnMarshaller#processResponse(org
	 * .apache.xmlbeans.XmlObject)
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
		java.lang.String output;

		
		logDebug("Inside OrderHistoryUnMarshaller:processResponse. Response Document is ");
		

		BBBPerformanceMonitor
				.start("OrderHistoryUnMarshaller-processResponse");

		final SupplyBalanceResponseVO supplyBalanceResVO = new SupplyBalanceResponseVO();

		if (responseDocument != null) {
			logDebug("Response Document is " + responseDocument.toString());
			final SupplyBalanceResponseDocument supplyBalanceResDoc = (SupplyBalanceResponseDocument) responseDocument;

			final SupplyBalanceResponse supplyBalanceRes = supplyBalanceResDoc
					.getSupplyBalanceResponse();
			
			if(supplyBalanceRes != null){
			output = supplyBalanceRes.getSupplyBalanceReturn()
					.getStringValue();

			supplyBalanceResVO.setResponse(output);
			}
			else{
				supplyBalanceResVO.setWebServiceError(true);
			}
		}

		BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-processResponse");

		return supplyBalanceResVO;
	}
}
