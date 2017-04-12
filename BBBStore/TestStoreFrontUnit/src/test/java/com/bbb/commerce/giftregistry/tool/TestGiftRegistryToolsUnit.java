package com.bbb.commerce.giftregistry.tool;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BaseTestCase;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class TestGiftRegistryToolsUnit extends BaseTestCase{

	@Mock MutableRepository mockGiftRegistryRepository;
	@Mock MutableRepositoryItem mockRegistryRecommenderItem;
	@Mock MutableRepositoryItem mockRegistryItem;
	@Mock RepositoryItem mockProfileItem;
	@Mock MutableRepositoryItem mockRegistryRecommendationProdctItem;
	@Mock Set mockRecommendationSet;
	@Spy Set<RepositoryItem> mockRepositoryItemsSet=new HashSet<RepositoryItem>();
	@Mock List<RecommendationRegistryProductVO> recommendationRegistryProductVO;

	// ----------- fetchAllPendingItems --------------
	@Mock MutableRepository mockGiftRepository;
	@Mock RepositoryItem mockRegistryRecommendationItem;
	@Mock MutableRepositoryItem castRepositoryItem;
	@Mock MutableRepositoryItem registryItem;
	@Spy Set<RepositoryItem> mockRecommnederProfileSet=new HashSet<RepositoryItem>();
	@Spy Set<RepositoryItem> mockRecommendationProduct1Set=new HashSet<RepositoryItem>();
	@Spy Set<RepositoryItem> mockRecommendationProduct2Set=new HashSet<RepositoryItem>();
	@Mock RepositoryItem mockRecommendationProfile1;
	@Mock RepositoryItem mockProfile1;
	@Mock RepositoryItem mockRecommendationProfile2;
	@Mock RepositoryItem mockProfile2;
	@Mock RepositoryItem mockRecommendationProduct11;
	@Mock RepositoryItem mockRecommendationProduct12;
	@Mock RepositoryItem mockRecommendationProduct21;
	@Mock RepositoryItem mockRecommendationProduct22;
	@Mock RecommendationRegistryProductVO mockRegistryProductVO;
	@Mock BBBPerformanceMonitor mockBBBPerformanceMonitor;
	@Mock MutableRepositoryItem mMutableRepositoryItem;
	@Mock RepositoryItem mockRepositoryItem;

	@Spy GiftRegistryTools giftRegistryTools =new GiftRegistryTools();
	String registryId="registryId";
	String tabId="0";

	@Override
	public void setUp(){
		super.setUp();
	}

	public void fetchRecommendationItemsSetUp() throws RepositoryException {
		giftRegistryTools.setGiftRepository(mockGiftRepository);
		when(mockGiftRepository.getItem(registryId,BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS)).thenReturn(castRepositoryItem);
		when(mockGiftRepository.getItemForUpdate(registryId,BBBGiftRegistryConstants.GIFT_REGISTRY)).thenReturn(registryItem);
		when(castRepositoryItem.getPropertyValue("recommenderItems")).thenReturn(mockRecommnederProfileSet);
		mockRecommnederProfileSet.add(mockRecommendationProfile1);
		mockRecommnederProfileSet.add(mockRecommendationProfile2);
		mockRecommendationProduct1Set.add(mockRecommendationProduct11);
		mockRecommendationProduct1Set.add(mockRecommendationProduct12);
		mockRecommendationProduct2Set.add(mockRecommendationProduct21);
		mockRecommendationProduct2Set.add(mockRecommendationProduct22);

		when(mockRecommendationProfile1.getPropertyValue("recommendations")).thenReturn(mockRecommendationProduct1Set);
		when(mockRecommendationProfile2.getPropertyValue("recommendations")).thenReturn(mockRecommendationProduct2Set);

		when(mockRecommendationProfile1.getPropertyValue("inviteeProfileId")).thenReturn(mockProfile1);
		when(mockRecommendationProfile2.getPropertyValue("inviteeProfileId")).thenReturn(mockProfile2);

		when(mockRecommendationProduct11.getPropertyValue("acceptedQuantity")).thenReturn(0l);
		when(mockRecommendationProduct12.getPropertyValue("acceptedQuantity")).thenReturn(0l);
		when(mockRecommendationProduct21.getPropertyValue("acceptedQuantity")).thenReturn(0L);
		when(mockRecommendationProduct22.getPropertyValue("acceptedQuantity")).thenReturn(1l);

		when(mockRecommendationProduct11.getPropertyValue("declined")).thenReturn(1);
		when(mockRecommendationProduct12.getPropertyValue("declined")).thenReturn(0);
		when(mockRecommendationProduct21.getPropertyValue("declined")).thenReturn(0);
		when(mockRecommendationProduct22.getPropertyValue("declined")).thenReturn(0);

	}


	@Test
	public void fetchAllPendingItems() throws BBBSystemException, BBBBusinessException,RepositoryException{
		// Fetch data for pending items

		fetchRecommendationItemsSetUp();
		ArgumentCaptor<RepositoryItem> argRepitemForProfile=ArgumentCaptor.forClass(RepositoryItem.class);
		ArgumentCaptor<RepositoryItem> argRepitemForProduct=ArgumentCaptor.forClass(RepositoryItem.class);
		doReturn(mockRegistryProductVO).when(giftRegistryTools).populateRecommendedProductList((RepositoryItem)anyObject(), (RepositoryItem)anyObject(), anyString() , anyString());

		giftRegistryTools.getRegistryRecommendationItemsForTab(registryId, tabId);
		verify(giftRegistryTools,times(2)).populateRecommendedProductList(argRepitemForProduct.capture(), argRepitemForProfile.capture(), anyString(),anyString());
		verify(giftRegistryTools,times(0)).populateRecommendedProductList(eq(mockRecommendationProduct11),eq(mockProfile1) , anyString(),anyString());
		verify(giftRegistryTools,times(1)).populateRecommendedProductList(eq(mockRecommendationProduct12),eq(mockProfile1) , anyString(),anyString());
		verify(giftRegistryTools,times(1)).populateRecommendedProductList(eq(mockRecommendationProduct21),eq(mockProfile2) , anyString(),anyString());
		verify(giftRegistryTools,times(0)).populateRecommendedProductList(eq(mockRecommendationProduct22),eq(mockProfile2) , anyString(),anyString());

		// Fetch data for pending items
		tabId="1";
		giftRegistryTools.getRegistryRecommendationItemsForTab(registryId, tabId);
		verify(giftRegistryTools,times(1)).fetchAcceptedRecommendations(eq(mockRecommendationProduct1Set),eq(mockProfile1) , anyString(),anyString());
		verify(giftRegistryTools,times(1)).fetchAcceptedRecommendations(eq(mockRecommendationProduct2Set),eq(mockProfile2) , anyString(),anyString());
	}

	@Rule
	  public ExpectedException nullPointerException = ExpectedException.none();

	@Test
	public void fetchAllPendingItems_when_registryid_not_found() throws BBBSystemException, BBBBusinessException,RepositoryException{
		// Fetch data for pending items

		fetchRecommendationItemsSetUp();
		when(mockGiftRepository.getItemForUpdate("registryId2",BBBGiftRegistryConstants.GIFT_REGISTRY)).thenReturn(registryItem);
		doReturn(mockRegistryProductVO).when(giftRegistryTools).populateRecommendedProductList((RepositoryItem)anyObject(), (RepositoryItem)anyObject(), anyString(),anyString());
		List tempList=giftRegistryTools.getRegistryRecommendationItemsForTab("registryId2", tabId);
		assertEquals(0, tempList.size());
	}

	@Test
	public void fetchAllAcceptedItems() throws BBBSystemException, BBBBusinessException,RepositoryException{
		// Fetch data for pending items
		fetchRecommendationItemsSetUp();
		doReturn(mockRegistryProductVO).when(giftRegistryTools).populateRecommendedProductList((RepositoryItem)anyObject(), (RepositoryItem)anyObject(), anyString(), anyString());
		when(giftRegistryTools.fetchAcceptedRecommendations(mockRecommendationProduct1Set,mockProfile1,"BedBathUs","true")).thenReturn(recommendationRegistryProductVO);
		when(giftRegistryTools.fetchAcceptedRecommendations(mockRecommendationProduct2Set,mockProfile2,"BedBathUs","true")).thenReturn(recommendationRegistryProductVO);
		verify(giftRegistryTools,times(0)).populateRecommendedProductList(eq(mockRecommendationProduct11),eq(mockProfile1) , anyString(),anyString());
		verify(giftRegistryTools,times(0)).populateRecommendedProductList(eq(mockRecommendationProduct12),eq(mockProfile1) , anyString(), anyString());
		verify(giftRegistryTools,times(0)).populateRecommendedProductList(eq(mockRecommendationProduct21),eq(mockProfile2) , anyString(), anyString());
		verify(giftRegistryTools,times(1)).populateRecommendedProductList(eq(mockRecommendationProduct22),eq(mockProfile2) , anyString(), anyString());
	}

	@Test
	public void fetchAllDeclinedItems() throws BBBSystemException, BBBBusinessException,RepositoryException{
		// Fetch data for pending items
		fetchRecommendationItemsSetUp();
		doReturn(mockRegistryProductVO).when(giftRegistryTools).populateRecommendedProductList((RepositoryItem)anyObject(), (RepositoryItem)anyObject(), anyString(), anyString());
		when(giftRegistryTools.populateDeclinedTabProductList(mockRecommendationProduct1Set,mockProfile1,"BedBathUs",recommendationRegistryProductVO,"true")).
				thenReturn(recommendationRegistryProductVO);
		when(giftRegistryTools.populateDeclinedTabProductList(mockRecommendationProduct2Set,mockProfile2,"BedBathUs",recommendationRegistryProductVO,"true")).
				thenReturn(recommendationRegistryProductVO);
		verify(giftRegistryTools,times(1)).populateRecommendedProductList(eq(mockRecommendationProduct11),eq(mockProfile1) , anyString(), anyString());
		verify(giftRegistryTools,times(0)).populateRecommendedProductList(eq(mockRecommendationProduct12),eq(mockProfile1) , anyString(), anyString());
		verify(giftRegistryTools,times(0)).populateRecommendedProductList(eq(mockRecommendationProduct21),eq(mockProfile2) , anyString(), anyString());
		verify(giftRegistryTools,times(0)).populateRecommendedProductList(eq(mockRecommendationProduct22),eq(mockProfile2) , anyString(), anyString());
	}

	@Test
	public void fetchAllDeclinedItems_when_registryid_not_found() throws BBBSystemException, BBBBusinessException,RepositoryException{
		// Fetch data for pending items

		fetchRecommendationItemsSetUp();
		when(mockGiftRepository.getItemForUpdate("registryId2",BBBGiftRegistryConstants.GIFT_REGISTRY)).thenReturn(registryItem);
		doReturn(mockRegistryProductVO).when(giftRegistryTools).populateRecommendedProductList((RepositoryItem)anyObject(), (RepositoryItem)anyObject(), anyString(), anyString());
		List tempList=giftRegistryTools.getRegistryRecommendationItemsForTab("registryId2", "2");
		assertEquals(0, tempList.size());
	}

	@Rule
	  public ExpectedException exception = ExpectedException.none();

	@Test
	public void createReccommendation_ForRegistryNotPresent() throws RepositoryException, BBBSystemException {
		GiftRegistryTools giftRegistryTools=new GiftRegistryTools();
		giftRegistryTools.setGiftRepository(mockGiftRegistryRepository);
		when(mockGiftRegistryRepository.getItemForUpdate(anyString(),anyString())).thenReturn(null);
		RecommendationRegistryProductVO recommendationItemVO=new RecommendationRegistryProductVO();

		giftRegistryTools.createRegistryRecommendationsItem(recommendationItemVO);
	}

	@Test
	public void createReccommendation_ForProfileNotPresent() throws
	RepositoryException,BBBSystemException{
		GiftRegistryTools giftRegistryTools=new GiftRegistryTools();
		giftRegistryTools.setGiftRepository(mockGiftRegistryRepository);
		when(mockGiftRegistryRepository.getItemForUpdate(anyString(),anyString())).thenReturn(mockRegistryRecommenderItem);
		when(mockRegistryRecommenderItem.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS)).thenReturn(null);
		RecommendationRegistryProductVO recommendationItemVO=new RecommendationRegistryProductVO();

		giftRegistryTools.createRegistryRecommendationsItem(recommendationItemVO);

	}

	@Test
	public void createReccommendation_WhenProfileDoesNotMatch() throws
	RepositoryException,BBBSystemException{
		GiftRegistryTools giftRegistryTools=new GiftRegistryTools();
		giftRegistryTools.setGiftRepository(mockGiftRegistryRepository);
		when(mockGiftRegistryRepository.getItemForUpdate(anyString(),anyString())).thenReturn(mockRegistryRecommenderItem);
		Set<MutableRepositoryItem> repositoryItemsSet=new HashSet<MutableRepositoryItem>();		
		repositoryItemsSet.add(mockRegistryItem);
		when(mockRegistryRecommenderItem.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS)).thenReturn(repositoryItemsSet);
		when(mockRegistryItem.getPropertyValue(BBBGiftRegistryConstants.INVITEE_PROFILE_ID)).thenReturn(mockProfileItem);
		when(mockProfileItem.getRepositoryId()).thenReturn("profile2");
		when(mockRegistryItem.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS)).thenReturn(mockRecommendationSet);
		when(mockGiftRegistryRepository.createItem(BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS)).thenReturn(mockRegistryRecommendationProdctItem);

		RecommendationRegistryProductVO recommendationItemVO=new RecommendationRegistryProductVO();
		recommendationItemVO.setRecommenderProfileId("profile2");

		giftRegistryTools.createRegistryRecommendationsItem(recommendationItemVO);

	}


	@Test
	public void createReccommendation_WhenProfileMatch() throws
	RepositoryException,BBBSystemException{
		GiftRegistryTools giftRegistryTools=new GiftRegistryTools();
		giftRegistryTools.setGiftRepository(mockGiftRegistryRepository);
		when(mockGiftRegistryRepository.getItemForUpdate("registryId",BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS)).thenReturn(mockRegistryRecommenderItem);
		
		Set<RepositoryItem> mRepositoryItemsSet = new HashSet<RepositoryItem>();
		mRepositoryItemsSet.add(mockRegistryItem);
		when(mockRegistryRecommenderItem.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS)).thenReturn(mRepositoryItemsSet);
		when(mockRegistryItem.getPropertyValue(BBBGiftRegistryConstants.INVITEE_PROFILE_ID)).thenReturn(mockProfileItem);
		when(mockProfileItem.getRepositoryId()).thenReturn("profile2");
		
		Set<RepositoryItem> repositoryItemsSet=new HashSet<RepositoryItem>();
		repositoryItemsSet.add(mockRegistryItem);
		when(mMutableRepositoryItem.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS)).thenReturn(repositoryItemsSet);
		
		when(mockGiftRegistryRepository.createItem(BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS)).thenReturn(mockRegistryRecommendationProdctItem);
		when(mockRegistryItem.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS)).thenReturn(mockRecommendationSet);

		RecommendationRegistryProductVO recommendationItemVO=new RecommendationRegistryProductVO();
		recommendationItemVO.setRecommenderProfileId("profile2");
		recommendationItemVO.setSkuId("sku1");
		recommendationItemVO.setRecommendedQuantity(10);
		recommendationItemVO.setComment("comment");
		recommendationItemVO.setRegistryId("registryId");

		ArgumentCaptor<String> skuForRecommendation=ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Object> qtyForRecommendation=ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<String> comment=ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Date> date=ArgumentCaptor.forClass(Date.class);
		ArgumentCaptor<Object> declined=ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> acceptedQty=ArgumentCaptor.forClass(Object.class);

		giftRegistryTools.createRegistryRecommendationsItem(recommendationItemVO);
		verify(mockRegistryRecommendationProdctItem).setPropertyValue(eq(BBBGiftRegistryConstants.RECOMMENDED_SKU), skuForRecommendation.capture());
		verify(mockRegistryRecommendationProdctItem).setPropertyValue(eq(BBBGiftRegistryConstants.RECOMMENDED_QUANTITY), qtyForRecommendation.capture());
		verify(mockRegistryRecommendationProdctItem).setPropertyValue(eq(BBBGiftRegistryConstants.RECOMMENDATION_COMMENT), comment.capture());
		verify(mockRegistryRecommendationProdctItem).setPropertyValue(eq(BBBGiftRegistryConstants.RECOMMENDED_DATE), date.capture());
		verify(mockRegistryRecommendationProdctItem).setPropertyValue(eq(BBBGiftRegistryConstants.DECLINED2), declined.capture());
		verify(mockRegistryRecommendationProdctItem).setPropertyValue(eq(BBBGiftRegistryConstants.ACCEPTED_QUANTITY), acceptedQty.capture());
		assertEquals("sku1", skuForRecommendation.getValue());
		assertEquals("10", qtyForRecommendation.getValue().toString());
		assertEquals("comment", comment.getValue());
		assertEquals(new Date().getDate(),date.getValue().getDate());
		assertEquals("0", declined.getValue().toString());
		assertEquals("0", acceptedQty.getValue().toString());


		InOrder inorder=Mockito.inOrder(mockRecommendationSet,mockRegistryItem,mockRepositoryItemsSet,mockRegistryRecommenderItem);
//		inorder.verify(mockRecommendationSet,times(1)).add(mockRegistryRecommendationProdctItem);
		inorder.verify(mockRegistryItem,times(1)).setPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS,mockRecommendationSet);
//		inorder.verify(mockRepositoryItemsSet,times(1)).add(mockRegistryItem);
//		inorder.verify(mockRegistryRecommenderItem,times(1)).setPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS,
//				mockRepositoryItemsSet);
	}



}
