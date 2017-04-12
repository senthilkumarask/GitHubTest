/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestSampleDroplte.java
 *
 *  DESCRIPTION: Test sample droplet
 *
 *  HISTORY:
 *  Oct 14, 2011  Initial version
*/
package com.bbb.selfservice.survey.formhandler;


import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.selfservice.formhandler.SurveyFormHandler;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Sample droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestSurveyFormHandler extends BaseTestCase {
    
    	
    	    	
    	 public void testService() throws Exception{
    		 
    		    DynamoHttpServletRequest pRequest = getRequest();
    	    	DynamoHttpServletResponse pResponse = getResponse();
    	    	
    	    	SurveyFormHandler surveyFormHandler = (SurveyFormHandler) getObject("SurveyFormHandler");
    	    	surveyFormHandler.setEmailRequired("yes");
    	    	surveyFormHandler.setEmail("test@example.com");
    	    	surveyFormHandler.setUserName("UserName");
    	    	surveyFormHandler.setFeaturesMesssage("Features Message");
    	    	surveyFormHandler.setWebSiteMessage("WebSite Message");
    	    	surveyFormHandler.setOtherMessage("Other Message");
    	    	surveyFormHandler.setSelectedAge("30");
    	    	surveyFormHandler.setSelectedGender("M");
    	    	surveyFormHandler.setLocation("CA");
    	    	
    	    	surveyFormHandler.handleRequestInfo(pRequest, pResponse);
    	    	assertTrue(surveyFormHandler.isSuccessMessage());
    		   
    			}

    	 
    	 } 
    	  	
    	
    	    

