/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestSearchDroplet.java
 *
 *  DESCRIPTION: Test Search droplet
 *
 *  HISTORY:
 *  Nov 7, 2011  Initial version
*/
package com.bbb.search.droplet;

import java.util.ArrayList;
import java.util.List;

import atg.servlet.ServletUtil;

import com.sapient.common.tests.BaseTestCase;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.droplet.SearchDroplet;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestSearchDroplet extends BaseTestCase {
    public void testService() throws Exception {
        SearchDroplet sDroplet = (SearchDroplet) getObject("searchDroplet");
        ServletUtil.setCurrentRequest(getRequest());
        getRequest().setParameter("CatalogId", "0");
        getRequest().setParameter("Keyword", "Sheets");
        getRequest().setParameter("siteId",(String) getObject("siteId"));
        sDroplet.service(getRequest(), getResponse());
        assertNotNull("Search Results are not obtained.",getRequest().getObjectParameter("browseSearchVO"));
       
        
        // Added this case for Department tree structure
        getRequest().removeParameter("CatalogId");
        getRequest().setParameter("origSearchTerm", "sheets");
        getRequest().setParameter("enteredSearchTerm", "sheets");
        getRequest().setParameter("savedUrl", "/store/s/sheets");
        getRequest().setParameter("Keyword", "Sheets");
        getRequest().setParameter("siteId",(String) getObject("siteId"));
        sDroplet.service(getRequest(), getResponse());
        SearchResults browseSearchVO =(SearchResults)getRequest().getObjectParameter("browseSearchVO");
        for(FacetParentVO fpVO:browseSearchVO.getFacets()) {
			if(fpVO.getName().equalsIgnoreCase("DEPARTMENT")){
				for(FacetRefinementVO frVOL1:fpVO.getFacetRefinement()) {
					if(frVOL1.getFacetsRefinementVOs()!=null && frVOL1.getFacetsRefinementVOs().size()>0) {
						 assertEquals("L2 catatory is enpty", true, frVOL1.getFacetsRefinementVOs().size()==1);
						 List<FacetRefinementVO> frVOl3=frVOL1.getFacetsRefinementVOs().get(0).getFacetsRefinementVOs();
						 if(frVOl3!=null) {
							 assertEquals("L3 catatory is empty", true, frVOl3.size()>0);
						 }
						
					}
				}
			}
			
		}
   //SWS Sape Unit for NullSearch page
        String swsKeyword="sadsadfasfasf";
        getRequest().removeParameter("CatalogId");
        getRequest().setParameter("origSearchTerm", "sheets");
        getRequest().setParameter("enteredSearchTerm", "sheets");
        getRequest().setParameter("savedUrl", "/store/s/sheets");
        getRequest().setParameter("Keyword", "Sheets");
        getRequest().setParameter("swsterms", "white::%24white::"+swsKeyword);
        getRequest().setParameter("siteId",(String) getObject("siteId"));
        sDroplet.service(getRequest(), getResponse());
        String lastEnteredSWSKeyword =(String)getRequest().getObjectParameter("lastEnteredSWSKeyword"); 
		 assertEquals("Results not found for the SWSkeyword"+swsKeyword, swsKeyword, lastEnteredSWSKeyword);

    }
}
