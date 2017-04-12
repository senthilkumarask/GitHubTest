package com.bbb.cms;

import java.util.ArrayList;
import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.droplet.PaginationDroplet;
 
import com.sapient.common.tests.BaseTestCase;

public class TestPaginationDroplet extends BaseTestCase {

  DynamoHttpServletRequest request = getRequest();
  DynamoHttpServletResponse response = getResponse();
  
 
  public void testPaginationDroplet() throws Exception {
  PaginationDroplet paginationDroplet = (PaginationDroplet) getObject("paginationDroplet");
		
		List<String> guideList=new ArrayList<String>();
guideList.add("a");
guideList.add("b");
guideList.add("c");
guideList.add("d");
guideList.add("a");
guideList.add("b");

		String pageNo = (String) getObject("pageNo");
String perPage = (String) getObject("perPage");

request.setParameter("pageNo", pageNo);
request.setParameter("perPage", perPage);
request.setParameter("guideList", guideList);

paginationDroplet.service(request, response);


Integer forStart=Integer.parseInt((String)request.getParameter("forStart"));
Integer forEnd=Integer.parseInt((String)request.getParameter("forEnd"));

addObjectToAssert("forStart", forStart);
addObjectToAssert("forEnd", forEnd);
 

	}

}
