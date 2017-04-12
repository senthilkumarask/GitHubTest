package com.bbb.mobile.controller;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.servlet.ModelAndView;

import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.mobile.component.ServiceSession;
import com.bbb.mobile.exception.BBBSystemException;
import com.bbb.mobile.util.ForwardKeys;

/**
 * This class is used to unit test GiftRegistryController
 * using mockito.
 *
 * @author apan25
 *
 */
public class TestGiftRegistryController extends TestCase {

	@Spy GiftRegistryController giftRegistryController = new GiftRegistryController();
	@Spy  ModelAndView modelAndView = new ModelAndView();
	@Mock  HttpServletRequest request;
	@Rule public ExpectedException exception = ExpectedException.none();
	@Mock RegistrySummaryVO registrySummaryVO;

	@Spy ServiceSession mockServiceSession = new ServiceSession();
	@Spy RegistrySummaryVO registrySummaryVO1 = new RegistrySummaryVO();
	@Spy RegistrySummaryVO registrySummaryVO2 = new RegistrySummaryVO();
	@Spy List<RegistrySummaryVO> registrySummaryVOList = new ArrayList<RegistrySummaryVO>();

	String registryId = "registryId";
	String eventType = "Other";
	String token = "token";
	String recommenderIsFromFB = "false";

	public TestGiftRegistryController(String name) {
		super(name);
	}

	@Override
	public void setUp() throws Exception{
		super.setUp();
		MockitoAnnotations.initMocks(this);
		giftRegistryController.setServiceSession(mockServiceSession);
	}

	/**
	 * This method is used to unit test giftGiverRegistryRecommenderView
	 * when user is logged in.
	 * @throws BBBSystemException
	 */
	@Test
	public void testUserLoggedIn() throws BBBSystemException {

		doReturn(true).when(mockServiceSession).isLogin();

		doReturn(registrySummaryVO1).when(giftRegistryController).
			persistRecommendersData(anyString(), anyString(), anyString());

		doReturn(registrySummaryVO).when(giftRegistryController).
			persistRecommendersData(anyString(), anyString(), anyString());

		doReturn(modelAndView).when(giftRegistryController).
			giftGiverRegistryView(registryId, request, modelAndView);

		giftRegistryController.giftGiverRegistryRecommenderView(
				registryId, eventType, token, recommenderIsFromFB, request, modelAndView);

		verify(giftRegistryController, times(1)).addRegistrySummaryVOToList((RegistrySummaryVO)anyObject(),
				(List<RegistrySummaryVO>)anyObject());
	}

	/**
	 * This method is used to unit test giftGiverRegistryRecommenderView
	 * when user is not logged in.
	 * @throws BBBSystemException
	 */
	@Test
	public void testUserNotLoggedIn() throws BBBSystemException {

		doReturn(false).when(mockServiceSession).isLogin();

		giftRegistryController.giftGiverRegistryRecommenderView(
				registryId, eventType, token, recommenderIsFromFB, request, modelAndView);

		assertEquals("Forwrd keys is not browse/login", ForwardKeys.LOGIN_URL, modelAndView.getViewName());

	}

	/**
	 * This method is used to test addRegistrySummaryVOToList when registrySummaryVO
	 * already exists in registrySummaryVO list session.
	 *
	 * @throws BBBSystemException
	 */
	@Test
	public void testAddRegistrySummaryVORegistryIdExists() throws BBBSystemException {
		registrySummaryVO1.setRegistryId("203524492");
		registrySummaryVO2.setRegistryId("203524493");
		registrySummaryVOList.add(registrySummaryVO1);
		registrySummaryVOList.add(registrySummaryVO2);

		mockServiceSession.setRegistrySummaryVOList(registrySummaryVOList);

		giftRegistryController.addRegistrySummaryVOToList(registrySummaryVO2, registrySummaryVOList);

		verify(mockServiceSession, times(1)).getRegistrySummaryVOList();
	}

	/**
	 * This method is used to test addRegistrySummaryVOToList when registrySummaryVO
	 * already does not exist in registrySummaryVO list session.
	 *
	 * @throws BBBSystemException
	 */
	@Test
	public void testAddRegistrySummaryVORegistryIdDontExists() throws BBBSystemException {

		registrySummaryVO1.setRegistryId("203524492");
		registrySummaryVO2.setRegistryId("203524493");
		registrySummaryVOList.add(registrySummaryVO1);

		mockServiceSession.setRegistrySummaryVOList(registrySummaryVOList);

		giftRegistryController.addRegistrySummaryVOToList(registrySummaryVO2, registrySummaryVOList);

		assertEquals("registrySummaryVO not added to list", registrySummaryVOList.get(1).getRegistryId(),
				registrySummaryVO2.getRegistryId());

	}

	/**
	 * This method is used to test addRegistrySummaryVOToList when
	 * registrySummaryVO list does not exist in session.
	 *
	 * @throws BBBSystemException
	 */
	@Test
	public void testAddRegSummaryVOListEmpty() throws BBBSystemException {

		giftRegistryController.addRegistrySummaryVOToList(registrySummaryVO1, registrySummaryVOList);

		assertNotNull("mockServiceSession.getRegistrySummaryVO() is null",
				mockServiceSession.getRegistrySummaryVOList());

	}
}
