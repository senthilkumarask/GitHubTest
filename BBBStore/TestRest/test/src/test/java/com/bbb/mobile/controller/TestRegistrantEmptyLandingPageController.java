package com.bbb.mobile.controller;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ui.ModelMap;

import com.bbb.cms.RecommendationLandingPageTemplateVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.mobile.exception.BBBSystemException;
import com.bbb.mobile.service.ServiceManager;
import com.bbb.mobile.util.ForwardKeys;
import com.bbb.mobile.vo.RequestVO;

/**
 * This class is used to unit test RecommenderLandingPageController
 * using mockito.
 *
 * @author apan25
 *
 */
public class TestRegistrantEmptyLandingPageController extends TestCase{

	@Spy GiftRegistryController giftRegistryController = new GiftRegistryController();
	@Spy ModelMap modelMap = new ModelMap();
	@Mock  HttpServletRequest mockRequest;
	@Rule public ExpectedException exception = ExpectedException.none();
	@Mock RecommendationLandingPageTemplateVO mockRecommendationLandingPageTemplateVO;
	@Mock ServiceManager serviceManager;

	String registryId = "registryId";
	String registryName = "registryName";

	public TestRegistrantEmptyLandingPageController(String name) {
		super(name);
	}
	@Override
	public void setUp() throws Exception{
		super.setUp();
		MockitoAnnotations.initMocks(this);
		giftRegistryController.setServiceManager(serviceManager);
	}

	/**
	 * This method is used to unit test recomLandingPageController when
	 * user is logged in and token is invalid.
	 * @throws BBBSystemException
	 */
	@Test
	public void testRegistrantEmptyPage() throws BBBSystemException {
		doReturn("/m").when(mockRequest).getContextPath();
		doReturn(mockRecommendationLandingPageTemplateVO).when(serviceManager).invokeWebService((RequestVO)anyObject());

		String returnValue = giftRegistryController.fetchRegistrantLandingData(modelMap, mockRequest, registryId, registryName);

		assertEquals("Return value is not same as expected", returnValue, ForwardKeys.REGISTRANT_LAUNCH_PAGE);
	}
}
