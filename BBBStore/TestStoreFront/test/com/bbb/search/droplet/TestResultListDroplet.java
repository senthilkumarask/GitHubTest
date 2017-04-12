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

import java.io.IOException;
import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.query.SortCriteria;
import com.bbb.search.bean.result.BBBProductList;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestResultListDroplet extends BaseTestCase {
    public void testService() throws ServletException,IOException {
        ResultListDroplet rsDroplet = (ResultListDroplet) getObject("resultListDroplet");
        SearchQuery req = new SearchQuery();
        req.setSortCriteria(new SortCriteria());
        Map<String,String> pCatalogRef = new HashMap<String, String>();
        req.setCatalogRef(pCatalogRef);
        req.setSiteId((String) getObject("siteId"));
        try{
        	getRequest().setParameter("browseSearchVO", rsDroplet.getSearchManager().performSearch(req));
	        rsDroplet.service(getRequest(), getResponse());
	        //System.out.println("vuuuuu ::::"+((BBBProductList)(getRequest().getObjectParameter("BBBProductListVO"))).getBBBProductCount());
	        //addObjectToAssert("count",((BBBProductList)(getRequest().getObjectParameter("BBBProductListVO"))).getBBBProductCount());
	        
	        assertTrue( ((BBBProductList)(getRequest().getObjectParameter("BBBProductListVO"))).getBBBProductCount() !=0 );
	        
	        
	        req.setKeyWord("abcdef");
	        //req.setSearchField("P_Product_Title");
	        getRequest().setParameter("browseSearchVO", rsDroplet.getSearchManager().performSearch(req));
	        rsDroplet.service(getRequest(), getResponse());
	        //System.out.println("NTT ::::"+((BBBProductList)(getRequest().getObjectParameter("BBBProductListVO"))).getBBBProductCount());
	        
	        //addObjectToAssert("countZero",((BBBProductList)(getRequest().getObjectParameter("BBBProductListVO"))).getBBBProductCount());
	        assertTrue( ((BBBProductList)(getRequest().getObjectParameter("BBBProductListVO"))).getBBBProductCount() ==0 );
	        
	        /*
	        req.setKeyWord("Aquis");
	       //req.setSearchField("P_Product_Title");
	        getRequest().setParameter("browseSearchVO", rsDroplet.getSearchManager().performSearch(req));
	        rsDroplet.service(getRequest(), getResponse());
	        //System.out.println("NTT ::::"+((BBBProductList)(getRequest().getObjectParameter("BBBProductListVO"))).getBBBProductCount());
	        addObjectToAssert("countNonZero",((BBBProductList)(getRequest().getObjectParameter("BBBProductListVO"))).getBBBProductCount());
	        */
        }
        catch(BBBSystemException e){
        	
        }
        catch(BBBBusinessException e){
        	
        }
    }
}
