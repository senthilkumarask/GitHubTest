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

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.manh.doms.app.common.webservices.SupplyBalanceDocument;
import com.manh.doms.app.common.webservices.SupplyBalanceDocument.SupplyBalance;
import com.bbb.bopus.inventory.vo.SupplyBalanceRequestVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bedbathandbeyond.www.GetCouponsDocument;
import com.bedbathandbeyond.www.GetCouponsDocument.GetCoupons;
import com.bedbathandbeyond.www.ProcessCouponDocument.ProcessCoupon;
import org.xmlsoap.schemas.soap.encoding.String;

public class SupplyBalanceMarshaller extends
		com.bbb.framework.webservices.RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.framework.webservices.RequestMarshaller#buildRequest(com.bbb.
	 * framework.integration.ServiceRequestIF)
	 */
	public XmlObject buildRequest(final ServiceRequestIF pReqVo)
			throws BBBBusinessException, BBBSystemException {
		
		logDebug("Entry buildRequest of SupplyBalanceMarshaller with ServiceRequestIF object:"
					+ pReqVo.getServiceName());
		

		BBBPerformanceMonitor.start("SupplyBalanceMarshaller-buildRequest");

		try {

			final SupplyBalanceDocument supplyBalanceDocument = SupplyBalanceDocument.Factory
					.newInstance();

			final SupplyBalance supplyBalance = SupplyBalance.Factory
					.newInstance();

			supplyBalance.setATPXml(((SupplyBalanceRequestVO) pReqVo)
					.getATPXml());
			supplyBalanceDocument.setSupplyBalance(supplyBalance);

			
			logDebug("Exit buildRequest of SupplyBalanceMarshaller ");
			
			return supplyBalanceDocument;
		} finally {
			BBBPerformanceMonitor.end("SupplyBalanceMarshaller-buildRequest");
		}

	}
}
