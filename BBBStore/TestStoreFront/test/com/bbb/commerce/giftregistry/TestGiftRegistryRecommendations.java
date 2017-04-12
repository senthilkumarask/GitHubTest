package com.bbb.commerce.giftregistry;
import java.util.Date;
import java.util.List;

import atg.multisite.SiteContextManager;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.tool.RecommendationRegistryProductVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * This class provides test case for Registry Recommendations for a given registry
 * 1) Test case for recommendation Creation
 * 2) Test case for Recommondations Sort Order and pegination
 * 
 * @author vchan5
 * 
 */
public class TestGiftRegistryRecommendations extends BaseTestCase {

	private static final String SKU_DISPLAY_NAME = "skuDisplayName";
	private static final String PRODUCT = "product";
	private static final String RECOMMENDER = "recommender";
	private static final String FIRST_NAME = "firstName";
	private static final String REGISTRY_ID1 = "registryId1";
	private static final String PAGE_SIZE = "pageSize";
	private static final String LIST_PRICE = "listPrice";
	private static final String TAB_ID = "0";
	private static final String PROFILE_ID = "profileId";
	private static final String COMMENT = "comment";
	private static final String SKU_ID = "skuId";
	private static final String REGISTRY_ID = "registryId";
	private static final String TEST_GIFT_REGISTRY_RECOMMENDATIONS = "testGiftRegistryRecommendations";
	private static final String GIFT_REGISTRY_MANAGER = "giftRegistryManager";
	private static final String GIFT_REGISTRY_TOOLS = "giftRegistryTools";
	private static final String SITE_CONTEXT_MANAGER = "siteContextManager";
	private static final String SITE_ID = "siteId";
	private static final String TOKEN = "token";

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	/**
	 * Testing RegistryTypes
	 * 
	 * @throws Exception
	 */
	public void testGiftRegistryRecommendations() throws Exception {
		String registryId = (String) getObject(REGISTRY_ID);
		String registryId1 = (String) getObject(REGISTRY_ID1);
		String skuId = (String) getObject(SKU_ID);
		String comment = (String) getObject(COMMENT);
		String profileId = (String) getObject(PROFILE_ID);
		String pageSize = (String) getObject(PAGE_SIZE);
		String fisrtName=(String) getObject(FIRST_NAME);
		String listPrice = (String) getObject(LIST_PRICE);
		String skuDisplayName=(String) getObject(SKU_DISPLAY_NAME);

		// List<RecommendationRegistryProductVO> localRegistryRecommendedProduct
		// = new ArrayList<RecommendationRegistryProductVO>();
		String pSiteId = (String) getObject(SITE_ID);
		SiteContextManager siteContextManager = (SiteContextManager) getObject(SITE_CONTEXT_MANAGER);
		siteContextManager.pushSiteContext(BBBSiteContext
				.getBBBSiteContext(pSiteId));
		GiftRegistryRecommendationManager testRegistryRecommendationManager = (GiftRegistryRecommendationManager) getObject(TEST_GIFT_REGISTRY_RECOMMENDATIONS);
		testRegistryRecommendationManager.setLoggingDebug(true);

		try {
			// test case for recommendaton creation
			Date currentDateBeforeRecommendation = new Date();
			List<RecommendationRegistryProductVO> registryRecommendedProductBeforeCreation = testRegistryRecommendationManager
					.getRegistryRecommendationItemsForTab(registryId, TAB_ID,
							null,"",getRequest());
			testRegistryRecommendationManager
					.createRegistryRecommendationProduct(registryId, skuId,
							comment, 100L, profileId);
			List<RecommendationRegistryProductVO> registryRecommendedProduct = testRegistryRecommendationManager
					.getRegistryRecommendationItemsForTab(registryId, TAB_ID,
							null,"",getRequest());
			
			// Below assert is test to the create Recommendation
			
			if (null != registryRecommendedProduct
					&& registryRecommendedProduct.size() > 0) {
				assertNotSame("New Recommendation is created for registry : "
						+ registryId + "and size of the recommendations is : "
						+ registryRecommendedProduct.size(),
						registryRecommendedProductBeforeCreation.size(),
						registryRecommendedProduct.size());
				assertTrue("Test Case for Sort Option for Recommendation Date",
						currentDateBeforeRecommendation
								.compareTo(registryRecommendedProduct.get(0)
										.getRecommendedDate()) < 0);
			}
			registryRecommendedProduct = testRegistryRecommendationManager
					.getRegistryRecommendationItemsForTab(registryId1, TAB_ID,
							RECOMMENDER,"",getRequest());
			
			if (null != registryRecommendedProduct
					&& registryRecommendedProduct.size() > 0) {
				assertTrue("Test Case for Sort Option for Recommender First Name",
						registryRecommendedProduct.get(0).getFirstName().equalsIgnoreCase(fisrtName));
			}
			
			registryRecommendedProduct = testRegistryRecommendationManager
					.getRegistryRecommendationItemsForTab(registryId1, TAB_ID,
							PRODUCT,"",getRequest());
			
			if (null != registryRecommendedProduct
					&& registryRecommendedProduct.size() > 0) {
				assertTrue("Test Case for Sort Option for Product",
						registryRecommendedProduct.get(0).getSkuDisplayName().equalsIgnoreCase(skuDisplayName));
			}
			
			registryRecommendedProduct = testRegistryRecommendationManager
					.getRegistryRecommendationItemsForTab(registryId1, TAB_ID,
							LIST_PRICE,"",getRequest());
			
			if (null != registryRecommendedProduct
					&& registryRecommendedProduct.size() > 0) {
				assertTrue("Test Case for Sort Option for List Price",
						String.valueOf(registryRecommendedProduct.get(0).getSkuListPrice()).equalsIgnoreCase(listPrice));
			}
			registryRecommendedProduct = testRegistryRecommendationManager
					.getRegistryRecommendationItemsForTab(registryId, TAB_ID,
							null,"",getRequest());
			
			if (null != registryRecommendedProduct
					&& registryRecommendedProduct.size() > 0) {
				assertTrue("Test Case for pagination",pageSize.equals(registryRecommendedProduct.size()));
			}
			
		} catch (BBBSystemException se) {
			assertNotSame(se.getErrorCode(), "3011");
		}
	}
	
	/**
	 * Test case for token validation
	 * @throws Exception
	 */
	public void testValidateToken() throws Exception {
		
		String validTokenRegistry = (String) getObject("validTokenRegistry");
		String expiredTokenRegistry = (String) getObject("validTokenRegistry");
		String invalidTokenRegistry = (String) getObject("invalidTokenRegistry");
		
		String validToken =(String) getObject("validToken");
		String expiredToken =(String) getObject("expiredToken");
		String invalidToken =(String) getObject("invalidToken");

		int TOKEN_EXPIRED = 0;
		int INVALID_TOKEN = -1;
		int VALID_TOKEN = 1;
		
		String pSiteId = (String) getObject(SITE_ID);
		SiteContextManager siteContextManager = (SiteContextManager) getObject(SITE_CONTEXT_MANAGER);
		siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		GiftRegistryManager giftRegistryManager = (GiftRegistryManager) getObject(GIFT_REGISTRY_MANAGER);
		GiftRegistryTools giftRegistryTools = (GiftRegistryTools) getObject(GIFT_REGISTRY_TOOLS);
		
		giftRegistryTools.setLoggingDebug(true);
		
		int status = giftRegistryTools.validateToken(expiredTokenRegistry, expiredToken);
		assertEquals(status, TOKEN_EXPIRED);
		
		status = giftRegistryTools.validateToken(invalidTokenRegistry, invalidToken);
		assertEquals(status, INVALID_TOKEN);
		
		status = giftRegistryTools.validateToken(validTokenRegistry, validToken);
		assertEquals(status, VALID_TOKEN);

	}
	
	/**
	 * Test case for token validation
	 * @throws Exception
	 */
	public void testPersistToken() throws Exception {
		
		String validTokenRegistry = (String) getObject("validTokenRegistry");
		String validToken =(String) getObject("validToken");
		String pSiteId = (String) getObject(SITE_ID);
		SiteContextManager siteContextManager = (SiteContextManager) getObject(SITE_CONTEXT_MANAGER);
		siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		if (!bbbProfileFormHandler.getProfile().isTransient()) {
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}
		bbbProfileFormHandler.getValue().put("login", (String)getObject("email"));
		bbbProfileFormHandler.getValue().put("password", (String)getObject("password"));
		atg.servlet.ServletUtil.setCurrentRequest(getRequest());
		boolean isLogin = bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
		Profile profile = bbbProfileFormHandler.getProfile();

		GiftRegistryManager giftRegistryManager = (GiftRegistryManager) getObject(GIFT_REGISTRY_MANAGER);
		GiftRegistryTools giftRegistryTools = (GiftRegistryTools) getObject(GIFT_REGISTRY_TOOLS);
		
		giftRegistryTools.setLoggingDebug(true);
		
		giftRegistryTools.persistRecommenderReln(validTokenRegistry, validToken, profile,"false");
		
	}
	
	public void recommendRegistryList() throws Exception {
		GiftRegistryManager giftRegistryManager = (GiftRegistryManager) getObject(GIFT_REGISTRY_MANAGER);
		String profileId = (String) getObject(PROFILE_ID);
		List<RegistrySummaryVO> recommendRegistry = giftRegistryManager.recommendRegistryList(profileId);
		if(null == recommendRegistry || recommendRegistry.size() == 0){
			assertFalse(true);
		}
	}
	
}
