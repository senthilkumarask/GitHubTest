package com.bbb.browse;



import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.droplet.RollUpListDroplet;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.sapient.common.tests.BaseTestCase;

public class TestRollUpListDroplet extends BaseTestCase{

	public void testRollUpList() throws  ServletException, IOException
	{
		RollUpListDroplet rollUpListDroplet = (RollUpListDroplet) getObject("rollUpListDroplet");	
	
		getRequest().setParameter("id","prod60043");
		getRequest().setParameter("siteId","BuyBuyBaby");
		getRequest().setParameter("firstRollUpValue","King");
		getRequest().setParameter("firstRollUpType","prodSize");
		rollUpListDroplet.service(getRequest(), getResponse());
	
		List<RollupTypeVO> rollUpList=(List<RollupTypeVO>) getRequest().getObjectParameter("rollupList");
		
		assertNotNull(rollUpList);
		/*assertEquals("Black", rollUpList.get(0).getRollupAttribute());
		assertEquals("White", rollUpList.get(1).getRollupAttribute());
		assertEquals("Green", rollUpList.get(2).getRollupAttribute());*/
		//assertNotNull(productVO);
		
	

	}




}
