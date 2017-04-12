package com.bbb.cms.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestNextCollegeCollectionDroplet extends BaseTestCase{



	@SuppressWarnings("unchecked")
	public void testServiceNextCollegeCollection() throws BBBBusinessException,
	BBBSystemException, ServletException, IOException
	{
		NextCollegeCollectionDroplet nextCollegeCollectionDroplet = (NextCollegeCollectionDroplet)getObject("nextCollegeCollectionDroplet");
		nextCollegeCollectionDroplet.setLoggingDebug(true);
		
		nextCollegeCollectionDroplet.setSiteId((String) getObject("siteId"));

		String productId=(String) getObject("prdId");
		getRequest().setParameter("productId", productId);
		nextCollegeCollectionDroplet.service(getRequest(), getResponse());
		List<CollectionProductVO> listCollectionVo = (List<CollectionProductVO>) getRequest().getObjectParameter("listCollectionVo"); 
		assertNotNull(listCollectionVo);
		 
		 String collectionVOProductId = listCollectionVo.get(0).getProductId();
		 
		 getRequest().setParameter("productId", collectionVOProductId);
		 /*getRequest().setParameter("id", categoryId);*/
		 
		 nextCollegeCollectionDroplet.service(getRequest(), getResponse());
		 CollectionProductVO nextCollectionProductVO = (CollectionProductVO)getRequest().getObjectParameter("nextCollectionVo");
		 
		 assertEquals(listCollectionVo.get(1).getProductId(), nextCollectionProductVO.getProductId());


	}




}
