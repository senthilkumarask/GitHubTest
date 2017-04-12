package com.bbb.commerce.browse.droplet;

import java.util.ArrayList;
import java.util.List;

import com.bbb.commerce.catalog.vo.ProductVO;
import com.sapient.common.tests.BaseTestCase;

public class TestExitemIdDroplet extends BaseTestCase {

	public void testService() throws Exception {
		ExitemIdDroplet exitemIdDroplet = (ExitemIdDroplet) getObject("exitemIdDroplet");
		List<ProductVO> lastviewedProductsList = new ArrayList<ProductVO>();
		ProductVO productVO = new ProductVO();
		productVO.setProductId("prod10033");
		lastviewedProductsList.add(productVO);

		productVO.setProductId("prod10013");
		lastviewedProductsList.add(productVO);

		productVO.setProductId("prod10031");
		lastviewedProductsList.add(productVO);

		productVO.setProductId("prod10033");
		lastviewedProductsList.add(productVO);

		productVO.setProductId("prod10053");
		lastviewedProductsList.add(productVO);

		getRequest().setParameter("lastviewedProductsList",	lastviewedProductsList);
		exitemIdDroplet.service(getRequest(), getResponse());

		assertNotNull(getRequest().getParameter("productList"));

	}

}
