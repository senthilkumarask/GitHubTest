package com.bbb.commerce.giftregistry.manager;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.tool.RecommendationRegistryProductVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BaseTestCase;

public class TestGiftRegistryManagerUnit extends BaseTestCase {

	@Spy
	List<RecommendationRegistryProductVO> recommendationRegistryList = new ArrayList<RecommendationRegistryProductVO>();
	@Spy
	GiftRegistryRecommendationManager giftRegistryRecommendationManager = new GiftRegistryRecommendationManager();
	@Spy
	GiftRegistryManager giftRegistryManager = new GiftRegistryManager();
	@Mock
	GiftRegistryTools mockGiftRegistryTools;// = new GiftRegistryTools();
	@Mock
	BBBCatalogTools catalogTools;
	@Spy
	Map<String, List<SKUDetailVO>> jdaCategoryMap = new HashMap<String, List<SKUDetailVO>>();
	@Spy
	RecommendationRegistryProductVO recommendationRegistryProductVO1 = new RecommendationRegistryProductVO(
			"r1", "sku1", 5l, "comm1", "p1");
	@Spy
	RecommendationRegistryProductVO recommendationRegistryProductVO2 = new RecommendationRegistryProductVO(
			"r2", "sku2", 5l, "comm2", "p1");
	@Spy
	RecommendationRegistryProductVO recommendationRegistryProductVO3 = new RecommendationRegistryProductVO(
			"r3", "sku3", 5l, "comm3", "p2");
	@Spy
	RecommendationRegistryProductVO recommendationRegistryProductVO4 = new RecommendationRegistryProductVO(
			"r4", "sku4", 5l, "comm4", "p2");
	@Spy
	SKUDetailVO skuDetailVo1 = new SKUDetailVO();
	@Spy
	SKUDetailVO skuDetailVo2 = new SKUDetailVO();
	@Spy
	SKUDetailVO skuDetailVo3 = new SKUDetailVO();
	@Spy
	SKUDetailVO skuDetailVo4 = new SKUDetailVO();
	@Spy
	List<SKUDetailVO> skuDetailVoList1 = new ArrayList<SKUDetailVO>();
	@Spy
	List<SKUDetailVO> skuDetailVoList2 = new ArrayList<SKUDetailVO>();
	@Spy
	List<SKUDetailVO> skuDetailVoList3 = new ArrayList<SKUDetailVO>();
	@Mock BazaarVoiceProductVO mBazaarVoiceProductVO;

	@Override
	public void setUp() {
		super.setUp();
		recommendationRegistryProductVO1.setFirstName("James");
		recommendationRegistryProductVO1.setSkuSalePrice(10);
		recommendationRegistryProductVO1.setRecenltyModifiedDate(new Date(2015,
				0, 5));
		recommendationRegistryProductVO1.setRecommendedDate(new Date(2015,
				0, 5));
		// recommendationRegistryProductVO1.setJdaCategory("Kitchen");

		recommendationRegistryProductVO2.setFirstName("Pratul");
		recommendationRegistryProductVO2.setSkuSalePrice(11);
		recommendationRegistryProductVO2.setRecenltyModifiedDate(new Date(2015,
				1, 5));
		recommendationRegistryProductVO2.setRecommendedDate(new Date(2015,
				1, 5));
		// recommendationRegistryProductVO2.setJdaCategory("Fine Ware");

		recommendationRegistryProductVO3.setFirstName("James");
		recommendationRegistryProductVO3.setSkuSalePrice(10);
		recommendationRegistryProductVO3.setRecenltyModifiedDate(new Date(2015,
				2, 5));
		recommendationRegistryProductVO3.setRecommendedDate(new Date(2015,
				2, 5));
		// recommendationRegistryProductVO3.setJdaCategory("Dining");

		recommendationRegistryProductVO4.setFirstName("Amit");
		recommendationRegistryProductVO4.setSkuSalePrice(5);
		recommendationRegistryProductVO4.setRecenltyModifiedDate(new Date(2015,
				0, 6));
		recommendationRegistryProductVO4.setRecommendedDate(new Date(2015,
				0, 6));
		// recommendationRegistryProductVO4.setJdaCategory("Kitchen");

		recommendationRegistryList.add(recommendationRegistryProductVO1);
		recommendationRegistryList.add(recommendationRegistryProductVO2);
		recommendationRegistryList.add(recommendationRegistryProductVO3);
		recommendationRegistryList.add(recommendationRegistryProductVO4);

		skuDetailVo1.setSkuId("sku1");
		skuDetailVo2.setSkuId("sku2");
		skuDetailVo3.setSkuId("sku3");
		skuDetailVo4.setSkuId("sku4");
		skuDetailVoList1.add(skuDetailVo1);
		skuDetailVoList1.add(skuDetailVo4);
		skuDetailVoList2.add(skuDetailVo2);
		skuDetailVoList3.add(skuDetailVo3);
		jdaCategoryMap.put("kitchen", skuDetailVoList1);
		jdaCategoryMap.put("Fine Ware", skuDetailVoList2);
		jdaCategoryMap.put("Dining", skuDetailVoList3);
		setUpRegistrySearchVO();
	}

	public void setUpRegistrySearchVO() {
		siteId="BedBathUS";
		emailId = "a@b.com";
		mRegistrySearchVO.setAvailForWebPurchaseFlag(false);
		mRegistrySearchVO.setBlkSize(1);
		mRegistrySearchVO.setEmail(emailId);
		mRegistrySearchVO.setEvent("event");
		mRegistrySearchVO.setExcludedRegNums("excludedRegNums");
		mRegistrySearchVO.setFilterRegistriesInProfile(true);
		mRegistrySearchVO.setFirstName("firstName");
		mRegistrySearchVO.setGiftGiver(false);
		mRegistrySearchVO.setLastName("lastName");
		mRegistrySearchVO.setPageNum(1);
		mRegistrySearchVO.setPerPageSize(10);
		mRegistrySearchVO.setProfileId(mProfile);
		mRegistrySearchVO.setRegistryId("pRegistryId");
		mRegistrySearchVO.setReturnLeagacyRegistries(true);
		mRegistrySearchVO.setServiceName("pServiceName");
		mRegistrySearchVO.setSiteId("BedBathUS");
		mRegistrySearchVO.setSort("sort");
		mRegistrySearchVO.setSortSeq("sortSeq");
		mRegistrySearchVO.setSortSeqOrder("sortSeqOrder");
		mRegistrySearchVO.setStartIdx(0);
		mRegistrySearchVO.setState("state");
		mRegistrySearchVO.setUserToken("pUserToken");
		mRegistrySearchVO.setView(0);
		
	}

	@Test
	public void sortPendingItems_ByDate() throws BBBBusinessException,
			BBBSystemException {

		giftRegistryRecommendationManager
				.setGiftRegistryTools(mockGiftRegistryTools);
		giftRegistryRecommendationManager.setCatalogTools(catalogTools);
		doReturn(recommendationRegistryList).when(mockGiftRegistryTools)
				.getRegistryRecommendationItemsForTab(anyString(), anyString());
		doReturn(mBazaarVoiceProductVO).when(catalogTools).getBazaarVoiceDetails("productId");

		giftRegistryRecommendationManager.getRegistryRecommendationItemsForTab(
				"registryId", "0", "date", "0", "0", "BRD");
		assertEquals(recommendationRegistryList.get(0).hashCode(),
				recommendationRegistryProductVO3.hashCode());
		assertEquals(recommendationRegistryList.get(1).hashCode(),
				recommendationRegistryProductVO2.hashCode());
		assertEquals(recommendationRegistryList.get(2).hashCode(),
				recommendationRegistryProductVO4.hashCode());
		assertEquals(recommendationRegistryList.get(3).hashCode(),
				recommendationRegistryProductVO1.hashCode());

		List<RecommendationRegistryProductVO> recommendationsVO = giftRegistryRecommendationManager
				.getRegistryRecommendationItemsForTab("registryId", "0",
						"date", "2", "1", "BRD");
		assertEquals(2, recommendationsVO.size());

		recommendationsVO = giftRegistryRecommendationManager
				.getRegistryRecommendationItemsForTab("registryId", "0",
						"date", "3", "2", "BRD");
		assertEquals(0, recommendationsVO.size());
	}

	@Test
	public void sortPendingItems_ByRecommender() throws BBBBusinessException,
			BBBSystemException {

		giftRegistryRecommendationManager
				.setGiftRegistryTools(mockGiftRegistryTools);
		giftRegistryRecommendationManager.setCatalogTools(catalogTools);
		doReturn(recommendationRegistryList).when(mockGiftRegistryTools)
				.getRegistryRecommendationItemsForTab(anyString(), anyString());
		doReturn(mBazaarVoiceProductVO).when(catalogTools).getBazaarVoiceDetails("productId");

		giftRegistryRecommendationManager.getRegistryRecommendationItemsForTab(
				"registryId", "0", "recommender", "0", "0", "BRD");
		assertEquals(recommendationRegistryList.get(0).hashCode(),
				recommendationRegistryProductVO4.hashCode());
		assertEquals(recommendationRegistryList.get(1).hashCode(),
				recommendationRegistryProductVO1.hashCode());
		assertEquals(recommendationRegistryList.get(2).hashCode(),
				recommendationRegistryProductVO3.hashCode());
		assertEquals(recommendationRegistryList.get(3).hashCode(),
				recommendationRegistryProductVO2.hashCode());
	}

	@Test
	public void sortPendingItems_ByPrice() throws BBBBusinessException,
			BBBSystemException {

		giftRegistryRecommendationManager
				.setGiftRegistryTools(mockGiftRegistryTools);
		giftRegistryRecommendationManager.setCatalogTools(catalogTools);
		doReturn(mBazaarVoiceProductVO).when(catalogTools).getBazaarVoiceDetails("productId");
		doReturn(recommendationRegistryList).when(mockGiftRegistryTools)
				.getRegistryRecommendationItemsForTab(anyString(), anyString());

		giftRegistryRecommendationManager.getRegistryRecommendationItemsForTab(
				"registryId", "0", "salePrice", "0", "0", "BRD");
		assertEquals(recommendationRegistryList.get(0).hashCode(),
				recommendationRegistryProductVO2.hashCode());
		assertEquals(recommendationRegistryList.get(1).hashCode(),
				recommendationRegistryProductVO1.hashCode());
		assertEquals(recommendationRegistryList.get(2).hashCode(),
				recommendationRegistryProductVO3.hashCode());
		assertEquals(recommendationRegistryList.get(3).hashCode(),
				recommendationRegistryProductVO4.hashCode());
	}

	@Test
	public void sortPendingItems_ByJDACategory() throws BBBBusinessException,
			BBBSystemException {

		giftRegistryRecommendationManager
				.setGiftRegistryTools(mockGiftRegistryTools);
		giftRegistryRecommendationManager.setCatalogTools(catalogTools);
		doReturn(mBazaarVoiceProductVO).when(catalogTools).getBazaarVoiceDetails("productId");

		doReturn(recommendationRegistryList).when(mockGiftRegistryTools)
				.getRegistryRecommendationItemsForTab(anyString(), anyString());
		doReturn("300002").when(giftRegistryRecommendationManager)
				.checkRegistryType(anyString(), anyString());
		doReturn(jdaCategoryMap).when(catalogTools).sortSkubyRegistry(
				anyList(), anyMap(), anyString(), anyString(), anyString(),
				anyString());

		giftRegistryRecommendationManager.getRegistryRecommendationItemsForTab(
				"registryId", "0", "category", "0", "0", "BRD");
		assertEquals(recommendationRegistryList.get(0).hashCode(),
				recommendationRegistryProductVO3.hashCode());
		assertEquals(recommendationRegistryList.get(1).hashCode(),
				recommendationRegistryProductVO2.hashCode());
		assertEquals(recommendationRegistryList.get(3).hashCode(),
				recommendationRegistryProductVO1.hashCode());
		assertEquals(recommendationRegistryList.get(2).hashCode(),
				recommendationRegistryProductVO4.hashCode());
	}

	@Mock
	Profile mProfile;
	@Spy
	RegistrySearchVO mRegistrySearchVO = new RegistrySearchVO();
	String siteId;
	String emailId;
}
