package com.bbb.integration.BazaarVoice;

import java.util.ArrayList;
import java.util.List;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.BazaarVoiceManager;
import com.bbb.commerce.catalog.BazaarVoiceSchedulerJob;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceVO;
import com.sapient.common.tests.BaseTestCase;

public class TestBazaarVoiceSchedulerJob extends BaseTestCase {

	public void testBazaarVoiceSchedulerJob() throws Exception {
		BazaarVoiceSchedulerJob bazaarVoiceSchedulerJob = (BazaarVoiceSchedulerJob) getObject("bazaarVoiceSchedulerJob");
		bazaarVoiceSchedulerJob.setLoggingDebug(true);
		bazaarVoiceSchedulerJob.getTypeOfFeed();
		bazaarVoiceSchedulerJob.getJobDescription();
		bazaarVoiceSchedulerJob.getJobName();
		bazaarVoiceSchedulerJob.getBazaarVoiceManager();
		bazaarVoiceSchedulerJob.getBazaarVoiceUnMarshaller();
		bazaarVoiceSchedulerJob.getScheduler();
		bazaarVoiceSchedulerJob.getSchedule();
		bazaarVoiceSchedulerJob.isSchedulerEnabled();
		
		
		BazaarVoiceManager manager = (BazaarVoiceManager) getObject("manager");

	    String id = (String) getObject("id");
	    Float averageOverallRating = (Float) getObject("averageOverallRating");

	    Integer totalReviewCount = (Integer) getObject("totalReviewCount");
	    String externalId = (String) getObject("externalId");
	    String siteId = (String) getObject("siteId");

	    BazaarVoiceProductVO bazaarVoiceProductVO=new BazaarVoiceProductVO();
	    BazaarVoiceVO bazaarVoiceVO =new BazaarVoiceVO();
	    List<BazaarVoiceProductVO> lstBazaarVoiceProduct=new ArrayList<BazaarVoiceProductVO>();

	    bazaarVoiceProductVO.setAverageOverallRating(averageOverallRating);
	    bazaarVoiceProductVO.setId(id);
	    bazaarVoiceProductVO.setExternalId(externalId);
	    bazaarVoiceProductVO.setTotalReviewCount(totalReviewCount);
	    bazaarVoiceProductVO.setSiteId(siteId);

	    lstBazaarVoiceProduct.add(bazaarVoiceProductVO);
	    bazaarVoiceVO.setBazaarVoiceProduct(lstBazaarVoiceProduct);

	    if (manager != null) {
	    	manager.setLoggingDebug(true);

	      manager.createUpdateProductBV(bazaarVoiceVO);
	    }

	    BBBCatalogToolsImpl bbbCatalogToolsImpl = (BBBCatalogToolsImpl)getObject("catalogTools");
	    BazaarVoiceProductVO bazaarVoiceProductTestVO=(BazaarVoiceProductVO) bbbCatalogToolsImpl.getBazaarVoiceDetails(id,siteId); 

	    Float averageOverallRatingTest = (Float) bazaarVoiceProductTestVO.getAverageOverallRating(); 

	    Integer totalReviewCountTest = (Integer) bazaarVoiceProductTestVO.getTotalReviewCount(); 
	    String externalIdTest = (String)bazaarVoiceProductTestVO.getExternalId();
	    String siteIdTest = bazaarVoiceProductTestVO.getSiteId();



	    addObjectToAssert("averageOverallRatingTest",averageOverallRatingTest);
	    addObjectToAssert("totalReviewCountTest",totalReviewCountTest);

	    addObjectToAssert("externalIdTest", externalIdTest);
	    addObjectToAssert("siteIdTest", siteIdTest);
		
			}
}
