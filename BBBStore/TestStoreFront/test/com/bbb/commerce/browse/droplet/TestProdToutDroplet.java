package com.bbb.commerce.browse.droplet;

import java.util.ArrayList;
import java.util.List;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;

public class TestProdToutDroplet extends BaseTestCase{

	@SuppressWarnings("unchecked")
	public void testGetClearanceItems() throws Exception
	{
		ProdToutDroplet prodToutDroplet = (ProdToutDroplet) getObject("prodToutDroplet");
		String siteId= (String) getObject("siteId");
		String categoryId= (String) getObject("categoryId");
		 getRequest().setParameter("id", categoryId);
		 getRequest().setParameter("siteId", siteId);
	
		prodToutDroplet.setLoggingDebug(true);
		
		BBBSessionBean bbbSessionBean = (BBBSessionBean) getRequest().resolveName("/com/bbb/profile/session/SessionBean");
		getRequest().setParameter("tabList", "clearance,lastviewed");
		prodToutDroplet.service(getRequest(), getResponse());
	
		List <ProductVO> clearanceProductsList=((List<ProductVO>)getRequest().getObjectParameter("clearanceProductsList"));
		List <ProductVO> lastviewedProductsList=((List<ProductVO>)getRequest().getObjectParameter("lastviewedProductsList"));
		assertTrue(clearanceProductsList!=null);
		
		List<String> productIdList= (List<String>)bbbSessionBean.getValues().get("lastviewedProductsList");

		if(productIdList==null)
			assertTrue(lastviewedProductsList.isEmpty());


		final List <String>lastViewedItemsList=new ArrayList<String>();
		lastViewedItemsList.add("prod10033");
		lastViewedItemsList.add("prod10013");
		lastViewedItemsList.add("prod10031");
		lastViewedItemsList.add("prod10053");
		bbbSessionBean.getValues().put(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST, lastViewedItemsList);
		prodToutDroplet.service(getRequest(), getResponse());
		lastviewedProductsList=((List<ProductVO>)getRequest().getObjectParameter("lastviewedProductsList"));

		assertNotNull(lastviewedProductsList);
		assertTrue(lastviewedProductsList.get(0) instanceof ProductVO );


	}


}
