package com.bbb.commerce.browse.droplet;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;

public class TestProductViewedDroplet extends BaseTestCase {

	public void testService() throws Exception {
		ProductViewedDroplet productViewedDroplet = (ProductViewedDroplet) getObject("productViewedDroplet");
		getRequest().setParameter("id", "prod10033");

		productViewedDroplet.service(getRequest(), getResponse());

		final BBBSessionBean sessionBean = (BBBSessionBean) getRequest().resolveName("/com/bbb/profile/session/SessionBean");

		assertNotNull(sessionBean.getValues().get(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST));

		sessionBean.getValues().put(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST,sessionBean.getValues().get(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST));

		productViewedDroplet.service(getRequest(), getResponse());

		assertNotNull(sessionBean.getValues().get(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST));

	}
}
