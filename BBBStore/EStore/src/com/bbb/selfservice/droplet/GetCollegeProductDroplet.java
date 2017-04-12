package com.bbb.selfservice.droplet;

/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  GetCollegeProductDroplet.java
 *
 *  DESCRIPTION: GetCollegeProductDroplet extends ATG OOTB DynamoServlet
 *  			 and helps to get the list of productvo from 
 *  			 BBBCatalogToolsImpl.getCollegeProduct() by passing the collegeid 
 *  			 and siteId	
 *  HISTORY:
 *  1/4/2012 Initial version
 *
 */
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 * @author akhaju
 * 
 */
public class GetCollegeProductDroplet extends BBBDynamoServlet {
	private BBBCatalogToolsImpl mCatalogTools;

	/**
	 * @return mCatalogTools
	 */
	public BBBCatalogToolsImpl getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 */
	public void setCatalogTools(BBBCatalogToolsImpl pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @param pReq
	 * @param pRes
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {
		logDebug("GetCollegeProductDroplet.service() method Starts");
		String collegeId = pReq.getParameter(BBBCoreConstants.COLLEGE_ID);
		String siteId = pReq.getParameter(BBBCoreConstants.SITE_ID);
		try {
			List<ProductVO> productList = getCatalogTools().getCollegeProduct(
					collegeId, siteId);
			
			logInfo("mCatalogTools.getCollegeProduct() returns " + productList); 
			
			pReq.setParameter("productList", productList);
			pReq.serviceLocalParameter(BBBCoreConstants.OUTPUT, pReq, pRes);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pReq, "BBBBussiness Exception from service method of GetCollegeProductDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1190 ), e);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pReq, "BBBBussiness Exception from service method of GetCollegeProductDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1191 ), e);
		}
		logDebug("GetCollegeProductDroplet.service() method ends");
	}

}
