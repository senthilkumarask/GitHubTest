/*
 *  Copyright 2014, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestBuildContext.java
 *
 *  DESCRIPTION: Test BBBInternationalShippingBuilderImpl
 *
 *  HISTORY:
 *  Oct 14, 2011 Initial version
 */
package com.bbb.internationalshipping.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.vo.BBBInternationalContextVO;
import com.sapient.common.tests.BaseTestCase;


public class TestBuildContext extends BaseTestCase {

	
	/*************  TestBuildContextFromIPSuccess  *************/
		
	// Getting country Code while passing Ip Address to buildContextFromIP() method
	
	public void testBuildContextFromIPSuccess() throws IOException, BBBSystemException, BBBBusinessException {
		
			BBBInternationalShippingBuilderImpl internationalShippingBuilder = (BBBInternationalShippingBuilderImpl) getObject("internationalShippingBuilder");
			String clientIPAddress = (String) getObject("clientIPAddress");
			BBBInternationalContextVO bbbInternationalContextVO  = internationalShippingBuilder.buildContextFromIP(clientIPAddress);
				System.out.println("Country Code :"+bbbInternationalContextVO.getShippingLocation().getCountryCode());
			assertNotNull(bbbInternationalContextVO.getShippingLocation().getCountryCode()); 
				
	}
	
	/*************  TestBuildContextFromIPError  *************/
	
	// Getting Exception while passing null Ip Address to buildContextFromIP() method
	public void testBuildContextFromIPError() throws  IOException, BBBSystemException, BBBBusinessException {

		BBBInternationalShippingBuilderImpl internationalShippingBuilder = null;	
		try
		{
			internationalShippingBuilder = (BBBInternationalShippingBuilderImpl) getObject("internationalShippingBuilder");
			String clientIPAddress = (String) getObject("clientIPAddress");
			internationalShippingBuilder.buildContextFromIP(clientIPAddress);
			
		}
		catch (BBBBusinessException e) 
		{
			String errorMessage = e.getMessage();
			if (errorMessage.contains(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1004)) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
		
	}
	
	
	/*************  TestBuildContextOnCountryCodeSuccess  *************/
	
	// Getting country name while passing Country Code to buildContextBasedOnCountryCode() method
	public void testBuildContextOnCountryCodeSuccess() throws IOException, BBBSystemException, BBBBusinessException {
			
				BBBInternationalShippingBuilder internationalShippingBuilder  = (BBBInternationalShippingBuilder) getObject("internationalShippingBuilder");	
				String countryCode = (String) getObject("countryCode");
				BBBInternationalContextVO bbbInternationalContextVO = internationalShippingBuilder.buildContextBasedOnCountryCode(countryCode);
					System.out.println("Country Name : "+bbbInternationalContextVO.getShippingLocation().getCountryName());
				assertNotNull(bbbInternationalContextVO.getShippingLocation().getCountryCode()); 
					
		}
	
	/*************  TestBuildContextOnCountryCodeError  *************/
	// Getting Exception while passing null Country Code to buildContextBasedOnCountryCode() method
	public void testBuildContextOnCountryCodeError() throws  IOException, BBBSystemException, BBBBusinessException {

		BBBInternationalShippingBuilder internationalShippingBuilder = null;
		try
		{
			internationalShippingBuilder = (BBBInternationalShippingBuilder) getObject("internationalShippingBuilder");	
			String countryCode = (String) getObject("countryCode");
			internationalShippingBuilder.buildContextBasedOnCountryCode(countryCode);
		}
		catch (BBBBusinessException e) {
				String errorMessage = e.getMessage();
				if (errorMessage.contains(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1005)) {
					assertTrue(true);
				} else {
					assertFalse(true);
				}
		}
		
	}
	
	/*************  TestBuildContextAll  *************/
	
	// Getting values from buildContextAll() method in List of BBBInternationalContextVO type
	public void testBuildContextAll() throws IOException, BBBSystemException, BBBBusinessException {
			
				BBBInternationalShippingBuilder internationalShippingBuilder = (BBBInternationalShippingBuilder) getObject("internationalShippingBuilder");	
				List<BBBInternationalContextVO> contextVO = internationalShippingBuilder.buildContextAll();
					System.out.println("contextVO :" +contextVO); 
				assertNotNull(contextVO); 
								
		}
	
	/*************  TestBuildCurrencyMap  *************/
	
	// Getting values from testBuildCurrencyMap() method where passing buildContextAll() as a parameter
	public void testBuildCurrencyMap() throws IOException, BBBSystemException, BBBBusinessException {
		
		BBBInternationalShippingBuilder internationalShippingBuilder = (BBBInternationalShippingBuilder) getObject("internationalShippingBuilder");	
		List<BBBInternationalContextVO> contextVO = internationalShippingBuilder.buildContextAll();
			Map<String, String> map=internationalShippingBuilder.buildCurrencyMap(contextVO);
				System.out.print("Map Values :"+ map);
			assertNotNull(map); 
		
	}
}

