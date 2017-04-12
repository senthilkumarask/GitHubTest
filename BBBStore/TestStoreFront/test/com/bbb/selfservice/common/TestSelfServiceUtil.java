/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  SelfServiceUtil.java
 *
 *  DESCRIPTION: SelfServiceUtil is a general utility class for Self Service Module 
 *   	
 *  HISTORY:
 *  10/14/11 Initial version
 *
 */
package com.bbb.selfservice.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.bbb.commerce.catalog.vo.SiteChatAttributesVO;
import com.bbb.constants.BBBCoreConstants;
import com.sapient.common.tests.BaseTestCase;

public class TestSelfServiceUtil extends BaseTestCase{

	
	public void testIsChatWindowOpen() throws Exception{
		SiteChatAttributesVO pChatAttributes = new SiteChatAttributesVO();
		SiteChatAttributesVO inValidAttributes = new SiteChatAttributesVO();
		
		String weekDayStartTime = (String) getObject("weekDayStartTime");
		String weekDayEndTime = (String) getObject("weekDayEndTime");
		String weekEndStartTime = (String) getObject("weekEndStartTime");
		String weekEndEndTime = (String) getObject("weekEndEndTime");

		String inValidweekDayStartTime = (String) getObject("inValidweekDayStartTime");
		String inValidweekDayEndTime = (String) getObject("inValidweekDayEndTime");
		String inValidweekEndStartTime = (String) getObject("inValidweekEndStartTime");
		String inValidweekEndEndTime = (String) getObject("inValidweekEndEndTime");

		pChatAttributes.setWeekDayOpenTime(new SimpleDateFormat("HH:mm").parse(weekDayStartTime));
		pChatAttributes.setWeekDayCloseTime(new SimpleDateFormat("HH:mm").parse(weekDayEndTime));
		pChatAttributes.setWeekEndOpenTime(new SimpleDateFormat("HH:mm").parse(weekEndStartTime));
		pChatAttributes.setWeekEndCloseTime(new SimpleDateFormat("HH:mm").parse(weekEndEndTime));
		
		inValidAttributes.setWeekDayOpenTime(new SimpleDateFormat("HH:mm").parse(inValidweekDayStartTime));
		inValidAttributes.setWeekDayCloseTime(new SimpleDateFormat("HH:mm").parse(inValidweekDayEndTime));
		inValidAttributes.setWeekEndOpenTime(new SimpleDateFormat("HH:mm").parse(inValidweekEndStartTime));
		inValidAttributes.setWeekEndCloseTime(new SimpleDateFormat("HH:mm").parse(inValidweekEndEndTime));
		
		boolean bWindowOpen = true;
		
		bWindowOpen = SelfServiceUtil.isChatWindowOpen(pChatAttributes);
		assertTrue("Chat closed now",bWindowOpen);

		bWindowOpen = SelfServiceUtil.isChatWindowOpen(inValidAttributes);
		assertFalse("Chat window open test failed",bWindowOpen);
	}

	
}
