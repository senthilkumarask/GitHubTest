package com.bbb.commerce.browse.droplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBrandsDroplet extends BaseTestCase{

	public void testProcessBrandResults() throws Exception
	{
		BrandsDroplet brandsDroplet = (BrandsDroplet) getObject("brandsDroplet");	
		String pSiteId = (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		brandsDroplet.service(getRequest(), getResponse());
		@SuppressWarnings("unchecked")
		Map <String,ArrayList<BrandVO>> alphabetBrandListMap= (Map<String, ArrayList<BrandVO>>) getRequest().getObjectParameter("alphabetBrandListMap");
		//List<BrandVO> featuredBrands=(List<BrandVO>)getRequest().getObjectParameter("featuredBrands");
		assertTrue(alphabetBrandListMap!=null);

		assertFalse(alphabetBrandListMap.isEmpty());
		//assertTrue(featuredBrands!=null);

		//assertFalse(featuredBrands.isEmpty());


	}




}
