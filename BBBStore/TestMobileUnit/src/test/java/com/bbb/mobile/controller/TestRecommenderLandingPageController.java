package com.bbb.mobile.controller;

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

import com.bbb.cms.RecommenderLandingPageTemplateVO;
import com.bbb.mobile.exception.BBBSystemException;
import com.bbb.mobile.util.ForwardKeys;

/**
 * This class is used to unit test RecommenderLandingPageController
 * using mockito.
 *
 * @author apan25
 *
 */
public class TestRecommenderLandingPageController extends TestCase{

	@Spy RecommenderLandingPageController recommenderLandingPageController = new RecommenderLandingPageController();
	@Spy ModelMap modelMap = new ModelMap();
	@Mock  HttpServletRequest mockRequest;
	@Rule public ExpectedException exception = ExpectedException.none();
	@Mock RecommenderLandingPageTemplateVO mockRecommenderLandingPageTemplateVO;

	String registryId = "registryId";
	String eventType = "Other";
	String token = "token";
	String recommenderIsFromFB = "false";
	private static final String ERROR_INVALID_TOKEN_RECOM = "err_invalid_token_recom";

	private static final String  ERROR_MESSAGE = "errorMessgae";

	public TestRecommenderLandingPageController(String name) {
		super(name);
	}
	@Override
	public void setUp() throws Exception{
		super.setUp();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * This method is used to unit test recomLandingPageController when
	 * user is logged in and token is invalid.
	 * @throws BBBSystemException
	 */
	@Test
	public void testInvalidTokenUserLoggedIn() throws BBBSystemException {

		doReturn("/m").when(mockRequest).getContextPath();
		doReturn(-1).when(recommenderLandingPageController).validateToken(anyString(), anyString());
		doReturn(true).when(recommenderLandingPageController).isUserLoggedIn();
		doReturn(mockRecommenderLandingPageTemplateVO).when(recommenderLandingPageController).populateRLPData(anyString());

		String returnValue = recommenderLandingPageController.recomLandingPageController(modelMap, mockRequest, registryId, eventType, token, recommenderIsFromFB);

		assertEquals("Return value is not same as expected", returnValue, ForwardKeys.RECOMMENDER_LANDING_PAGE);
		assertEquals("recommenderLandingPageController.mVisibility is not same as expected",
				recommenderLandingPageController.mVisibility, "none");
		assertEquals("errorMessgae is not same as expected", ERROR_INVALID_TOKEN_RECOM, modelMap.get(ERROR_MESSAGE));

	}

	/**
	 * This method is used to unit test recomLandingPageController when
	 * user is  not logged in and token is valid.
	 * @throws BBBSystemException
	 */
	@Test
	public void testValidTokenUserNotLoggedIn() throws BBBSystemException {

		when(mockRequest.getContextPath()).thenReturn("/m");
		doReturn(1).when(recommenderLandingPageController).validateToken(anyString(), anyString());
		doReturn(false).when(recommenderLandingPageController).isUserLoggedIn();
		doReturn(mockRecommenderLandingPageTemplateVO).when(recommenderLandingPageController).populateRLPData(anyString());

		String returnValue = recommenderLandingPageController.recomLandingPageController(
				modelMap, mockRequest, registryId, eventType, token, recommenderIsFromFB);

		assertEquals("Return value is not same as expected", returnValue, ForwardKeys.RECOMMENDER_LANDING_PAGE);
		assertEquals("recommenderLandingPageController.mVisibility is not same as expected",
				recommenderLandingPageController.mVisibility, "block");
		assertEquals("errorMessgae is not same as expected", null, modelMap.get(ERROR_MESSAGE));

	}

}
