package com.bbb.mobile.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;


import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ui.ModelMap;

import com.bbb.commerce.giftregistry.vo.RecommendationRegistryProductVO;
import com.bbb.mobile.util.BBBConstant;
import com.bbb.mobile.util.ForwardKeys;

public class TestRegistryRecommController extends TestCase {

	@Spy RegistryRecommController registryRecommController = new RegistryRecommController();
	@Spy  ModelMap modelMap = new ModelMap();
	@Mock  HttpServletRequest request;
	@Spy List<RecommendationRegistryProductVO> recommRegProdList = new ArrayList<RecommendationRegistryProductVO>();
	@Spy RecommendationRegistryProductVO regProd1 = new RecommendationRegistryProductVO();
	@Spy RecommendationRegistryProductVO regProd2 = new RecommendationRegistryProductVO();
	@Spy RecommendationRegistryProductVO regProd3 = new RecommendationRegistryProductVO();
	@Spy RecommendationRegistryProductVO regProd4 = new RecommendationRegistryProductVO();

	String registryId = "registryId";
	String sortOption = "sortOption";
	String pageNum = "pageNum";
	String eventType = "eventType";
	String prevRecommender = "prevRecommender";

	public TestRegistryRecommController(String name) {
		super(name);
	}
	@Override
	public void setUp() throws Exception{
		super.setUp();
		MockitoAnnotations.initMocks(this);
	}

	public void testDisplayRecommendations_setup() {
		regProd1.setProductId("product1");
		regProd1.setSkuDisplayName("skuname1");
		regProd2.setProductId("product2");
		regProd2.setSkuDisplayName("skuname2");
		regProd3.setProductId("product3");
		regProd3.setSkuDisplayName("skuname3");
		regProd4.setProductId("product4");
		regProd4.setSkuDisplayName("skuname4");

		recommRegProdList.add(regProd1);
		recommRegProdList.add(regProd2);
		recommRegProdList.add(regProd3);
		recommRegProdList.add(regProd4);
	}

	@Test
	public void testDisplayPendingRecommendations() {
		testDisplayRecommendations_setup();
		String tabId = BBBConstant.PENDING_TAB;
		doReturn(recommRegProdList).when(registryRecommController).getRecommendationsService(registryId, tabId, sortOption, pageNum, eventType);
		doReturn(true).when(registryRecommController).dataValidation(registryId, sortOption,pageNum,prevRecommender);
		registryRecommController.displayPendingRecommendations(modelMap, request, registryId, sortOption, pageNum, eventType, prevRecommender);
		verify(registryRecommController, times(1)).getRecommendationsService(registryId, tabId, sortOption, pageNum, eventType);
	}

	@Test
	public void testSetSeoUrlForEachRecom() {
		testDisplayRecommendations_setup();
		assertNull(regProd1.getProductSeoUrl());
		assertNull(regProd2.getProductSeoUrl());
		registryRecommController.setSeoUrlForEachRecom(recommRegProdList);
		verify(regProd3, times(1)).setProductSeoUrl(anyString());
		verify(regProd4, times(1)).setProductSeoUrl(anyString());
	}


	/**
	 * This method is used to unit test displayRecommendersTab method.
	 *
	 */
	@Test
	public void testDisplayRecommendersTab() {
		testDisplayRecommenders_setup();

		doReturn(recommRegProdList).when(registryRecommController).getRecommendersDetailService(registryId);
		doReturn(true).when(registryRecommController).dataValidation(registryId, sortOption,pageNum,prevRecommender);
		String returnValue = registryRecommController.displayRecommendersTab(modelMap, request, registryId, BBBConstant.BLANK_SORT_OPTION_RECOM_TAB,
				pageNum, eventType, prevRecommender);

		assertEquals("Return value is not same as expected", returnValue, ForwardKeys.RECOMMENDER_TAB);
		assertEquals("List is not same as expected", recommRegProdList, modelMap.get(BBBConstant.RECOMMENDERS_DETAILS));
	}

	/**
	 * This method is used to setup values to unit test
	 * displayRecommendersTab method.
	 *
	 */
	public void testDisplayRecommenders_setup() {
		regProd1.setFirstName("FirstName1");
		regProd1.setLastName("LastName1");
		regProd2.setFirstName("FirstName2");
		regProd2.setLastName("LastName2");

		recommRegProdList.add(regProd1);
		recommRegProdList.add(regProd2);
	}

	@Test
	public void testDisplayDeclinedRecommendations() {
		testDisplayRecommendations_setup();
		String tabId = BBBConstant.DECLINED_TAB;
		doReturn(recommRegProdList).when(registryRecommController).getRecommendationsService(registryId, tabId, sortOption, pageNum, eventType);
		doReturn(true).when(registryRecommController).dataValidation(registryId, sortOption,pageNum,prevRecommender);
		registryRecommController.displayDeclinedRecommendations(modelMap, request, registryId, sortOption, pageNum, eventType, prevRecommender);
		verify(registryRecommController, times(1)).getRecommendationsService(registryId, tabId, sortOption, pageNum, eventType);
	}

	@Test
	public void testDisplayAcceptedRecommendations() {
		testDisplayRecommendations_setup();
		String tabId = BBBConstant.ACCEPTED_TAB;
		doReturn(recommRegProdList).when(registryRecommController).getRecommendationsService(registryId, tabId, sortOption, pageNum, eventType);
		doReturn(true).when(registryRecommController).dataValidation(registryId, sortOption,pageNum,prevRecommender);
		registryRecommController.displayAcceptedRecommendations(modelMap, request, registryId, sortOption, pageNum, eventType, prevRecommender);
		verify(registryRecommController, times(1)).getRecommendationsService(registryId, tabId, sortOption, pageNum, eventType);
	}
}