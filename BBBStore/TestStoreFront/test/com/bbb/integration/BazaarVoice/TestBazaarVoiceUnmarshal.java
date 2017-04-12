package com.bbb.integration.BazaarVoice;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import atg.core.io.FileUtils;

import com.bbb.commerce.catalog.BazaarVoiceUnMarshaller;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceVO;
import com.sapient.common.tests.BaseTestCase;

public class TestBazaarVoiceUnmarshal extends BaseTestCase {

	public void testUnMarshalarBV() throws Exception {
		BazaarVoiceUnMarshaller manager = (BazaarVoiceUnMarshaller) getObject("manager");
		manager.setLoggingDebug(true);
		Float averageOverallRating=null;
		Integer overallRatingRange=null;
		Integer totalReviewCount=null;

		BazaarVoiceVO bazaarVoiceVO=null;
		boolean status = false;
		if (manager != null) {
			copyFiles();
			File dir=manager.getFilePathFrmConfigRepo(manager.getArchiveFilePathKey());
			System.out.println("dir  "+dir.getAbsolutePath());
			File bvFile=manager.moveFileToUploadLoc(dir);
			//System.out.println("bvFile path  "+bvFile.getAbsolutePath());

			status= manager.unmarshal();
			/*
			List<BazaarVoiceProductVO> lstBazaarVoiceProduct=(List<BazaarVoiceProductVO>)bazaarVoiceVO.getBazaarVoiceProduct();

			Iterator<BazaarVoiceProductVO> it=lstBazaarVoiceProduct.iterator();

			while(it.hasNext()){
				BazaarVoiceProductVO bazaarVoiceProductVO=(BazaarVoiceProductVO) it.next();

				averageOverallRating= bazaarVoiceProductVO.getAverageOverallRating();
				overallRatingRange=bazaarVoiceProductVO.getOverallRatingRange();
				totalReviewCount=bazaarVoiceProductVO.getTotalReviewCount();
			}

			addObjectToAssert("averageOverallRating",averageOverallRating);
			addObjectToAssert("overallRatingRange",overallRatingRange);

			addObjectToAssert("totalReviewCount",totalReviewCount);
			System.out.println("-------------averageOverallRating----------------"+averageOverallRating);*/
		}
		//boolean isArchive=  manager.archiveFile();
		System.out.println("status  "+status);
		assertTrue(status);
	}
	
	private void copyFiles() {
		try {
			String permFile = "/export/bbbylogs/BBBY/feeds/bbbysite/bazaarVoiceRatings/bv_bedbathbeyond_ratings_perm.xml";
			String newFilePath = "/export/bbbylogs/BBBY/feeds/bbbysite/bazaarVoiceRatings/bv_bedbathbeyond_ratings.xml";
			File newFile = new File(newFilePath);
			if(!newFile.exists()) {
					newFile.createNewFile();
			}
			FileUtils.copyFile(permFile, newFilePath);
			
			permFile = "/export/bbbylogs/BBBY/feeds/babysite/bazaarVoiceRatings/bv_buybuybaby_ratings_perm.xml";
			newFilePath = "/export/bbbylogs/BBBY/feeds/babysite/bazaarVoiceRatings/bv_buybuybaby_ratings.xml";
			newFile = new File(newFilePath);
			if(!newFile.exists()) {
					newFile.createNewFile();
			}
			FileUtils.copyFile(permFile, newFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
