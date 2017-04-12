package com.bbb.commerce.giftregistry.manager;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Spy;

import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.tool.RecommendationRegistryProductVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BaseTestCase;

public class TestGiftRegistryRecommendationManager extends BaseTestCase{
	
	@Spy GiftRegistryRecommendationManager giftRegistryRecommendationManager = new GiftRegistryRecommendationManager();
	@Mock GiftRegistryTools mGiftRegistryTools;
	@Spy RecommendationRegistryProductVO mRecommendationRegistryProductVO = new RecommendationRegistryProductVO();
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Override
	public void setUp(){
		super.setUp();
		giftRegistryRecommendationManager.setGiftRegistryTools(mGiftRegistryTools);
	}
	
	@Test
	public void testCreateRegistryRecommendationProduct() throws BBBSystemException {
		String registryId = "registryId";
		String skuId = "skuId";
		String comment = "comment";
		Long quantity = 1L;
		String recommenderProfileId = "recommenderProfileId";
		giftRegistryRecommendationManager.createRegistryRecommendationProduct(registryId, skuId, comment, quantity, recommenderProfileId);
		verify(mGiftRegistryTools,times(1)).createRegistryRecommendationsItem((RecommendationRegistryProductVO)anyObject());
		
		registryId = "registryId";
		quantity = 0L;
		giftRegistryRecommendationManager.createRegistryRecommendationProduct(registryId, skuId, comment, quantity, recommenderProfileId);
		verify(mGiftRegistryTools,times(0)).createRegistryRecommendationsItem(mRecommendationRegistryProductVO);
		
		registryId = "";
		quantity = 1L;
		giftRegistryRecommendationManager.createRegistryRecommendationProduct(registryId, skuId, comment, quantity, recommenderProfileId);
		verify(mGiftRegistryTools,times(0)).createRegistryRecommendationsItem(mRecommendationRegistryProductVO);
	}

}
