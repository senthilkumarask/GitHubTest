package com.bbb.cms.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestCollegeCollectionsDroplet extends BaseTestCase{



	@SuppressWarnings("unchecked")
	public void testServiceCollegeCollections() throws BBBBusinessException,
	BBBSystemException, ServletException, IOException
	{
		CollegeCollectionsDroplet collegeCollectionsDroplet = (CollegeCollectionsDroplet)getObject("collegeCollectionsDroplet");
		collegeCollectionsDroplet.setLoggingDebug(true);
		
		collegeCollectionsDroplet.setSiteId((String) getObject("siteId"));
		
		collegeCollectionsDroplet.service(getRequest(), getResponse());
		List<CollectionProductVO> listCollectionVo = (List<CollectionProductVO>) getRequest().getObjectParameter("listCollectionVo"); 
		assertNotNull(listCollectionVo);


	}




}
