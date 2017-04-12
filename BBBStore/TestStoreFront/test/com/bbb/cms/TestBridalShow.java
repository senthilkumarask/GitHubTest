package com.bbb.cms;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.droplet.BridalShowDetailDroplet;
import com.bbb.cms.droplet.BridalShowStateDroplet;
import com.sapient.common.tests.BaseTestCase;

public class TestBridalShow extends BaseTestCase {
  DynamoHttpServletRequest request = getRequest();
  DynamoHttpServletResponse response = getResponse();
  @SuppressWarnings("unchecked")
public void testBridalShowState() throws Exception {
    BridalShowStateDroplet bridalState = (BridalShowStateDroplet) getObject("bridalState");
    String pSiteId = (String)getObject("siteId");
    request.setParameter("siteId", pSiteId);
    bridalState.service(request, response);
    SortedMap<String, String> mapSMap = (SortedMap<String, String>)request.getObjectParameter("stateMap");
    String pStateId = (String)getObject("stateId");
    assertNotNull("state does not exist", mapSMap.get(pStateId));
  }
  
  @SuppressWarnings("unchecked")
public void testBridalTemplate() throws Exception {
    BridalShowDetailDroplet bridalState = (BridalShowDetailDroplet) getObject("bridalTemplate");
    DynamoHttpServletRequest request = getRequest();
    DynamoHttpServletResponse response = getResponse();
    String pSiteId = (String)getObject("siteId");
    String pStateId = (String)getObject("stateId");
    request.setParameter("siteId", pSiteId);
    request.setParameter("stateId", pStateId);
    bridalState.service(request, response);
    Set<Map<String,Object>> templates = (HashSet<Map<String,Object>>) request.getObjectParameter("stateItem");
    assertNotNull("Shows do not Exist for StateID:"+pStateId, templates);
    if(templates != null){
    	Iterator itr = templates.iterator();
    	while(itr.hasNext()){
    		Map<String, Object> mp = (Map<String, Object>)itr.next();
    		assertNotNull("Bridal Show Name is NULL", mp.get("name"));
    	    addObjectToAssert("name", mp.get("name")); 	   
    	}
    }
     
  }
  
}


