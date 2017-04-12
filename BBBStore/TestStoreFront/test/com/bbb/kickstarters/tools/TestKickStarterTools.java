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
package com.bbb.kickstarters.tools;


import java.util.List;
import atg.repository.RepositoryItem;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Sample droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestKickStarterTools extends BaseTestCase {
    
    	
    	    	
	public void testGetKickStartersByType() throws Exception{
		KickStarterTools kickStarterTools = (KickStarterTools) getObject("KickStarterTools");
		
		String registryType= (String) getObject("registryType");
		String siteId= (String) getObject("siteId");
		boolean isTransient= false;
		String kickStarterType= (String) getObject("kickStarterType");
		List<RepositoryItem> kickStarters=kickStarterTools.getKickStartersByType(registryType,siteId,isTransient,kickStarterType);
		assertNotNull(kickStarters);

	}
	
	public void testGetKickStartersForRest() throws Exception{
		KickStarterTools kickStarterTools = (KickStarterTools) getObject("KickStarterTools");
		
		String registryType= (String) getObject("registryType");
		String siteId= (String) getObject("siteId");
		boolean isTransient= false;
		List<RepositoryItem> kickStarters=kickStarterTools.getKickStartersForRest(registryType,siteId,isTransient);
		assertNotNull(kickStarters);

	}
	
	public void testGetTopConsultantDetails() throws Exception{
		KickStarterTools kickStarterTools = (KickStarterTools) getObject("KickStarterTools");
		
		String consultantId= (String) getObject("consultantId");
		RepositoryItem kickStarter=kickStarterTools.getTopConsultantDetails(consultantId);
		assertNotNull(kickStarter);

	}
	
	
    	 } 
    	  	
    	
    	    

