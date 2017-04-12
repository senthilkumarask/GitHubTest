package com.bbb.commerce.giftregistry.droplet;

import java.util.Locale;

import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;

import com.sapient.common.tests.BaseTestCase;

public class TestPrintAtHomeDroplet extends BaseTestCase {

	@SuppressWarnings("static-access")
	public void testService() throws Exception {
		PrintAtHomeDroplet dateCalculationDroplet = (PrintAtHomeDroplet) getObject("TestPrintAtHomeDroplet");
		getRequest().setParameter("siteId", "BedBathUS");
		getRequest().getLocale().setDefault(Locale.US);
		ServletUtil.setCurrentRequest(getRequest());
		dateCalculationDroplet.service(getRequest(), getResponse());
		RepositoryItem [] repoItems  =  (RepositoryItem [])getRequest().getObjectParameter("repoItems");
		boolean isvalid = false;
		if(repoItems!=null && repoItems.length >1){
			isvalid = true;
		}
		addObjectToAssert("testValue1", isvalid);
	}
}
