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

public class SelfServiceUtil {

	
	/**
	 * DESCRIPTION: This method compares current time with chat open hours.
	 * If current time is outside chat open hours then returns true else returns false
	 * 
	 * @return the mChatWindowOpen
	 */
	public static boolean isChatWindowOpen(SiteChatAttributesVO pChatAttributes) {
		boolean bWindowOpen = true;
		
		Calendar currentDT = Calendar.getInstance();
		Calendar chatStartDT = Calendar.getInstance();
		Calendar chatEndDT = Calendar.getInstance();
		
		//If today is saturday then chat start and end date to be compared is for weekend hours 
		if(currentDT.get(Calendar.DAY_OF_WEEK)== Calendar.SATURDAY || currentDT.get(Calendar.DAY_OF_WEEK)== Calendar.SUNDAY)
		{
			chatStartDT.set(Calendar.HOUR_OF_DAY, Integer.valueOf(new SimpleDateFormat("HH:mm").format(pChatAttributes.getWeekEndOpenTime()).substring(0, 2)));
			chatStartDT.set(Calendar.MINUTE, Integer.valueOf(new SimpleDateFormat("HH:mm").format(pChatAttributes.getWeekEndOpenTime()).substring(3, 5)));
			chatStartDT.set(Calendar.SECOND, BBBCoreConstants.ZERO);
			
			chatEndDT.set(Calendar.HOUR_OF_DAY, Integer.valueOf(new SimpleDateFormat("HH:mm").format(pChatAttributes.getWeekEndCloseTime()).substring(0, 2)));
			chatEndDT.set(Calendar.MINUTE, Integer.valueOf(new SimpleDateFormat("HH:mm").format(pChatAttributes.getWeekEndCloseTime()).substring(3, 5)));
			chatEndDT.set(Calendar.SECOND, BBBCoreConstants.ZERO);

		}
		else
		{
			chatStartDT.set(Calendar.HOUR_OF_DAY, Integer.valueOf(new SimpleDateFormat("HH:mm").format(pChatAttributes.getWeekDayOpenTime()).substring(0, 2)));
			chatStartDT.set(Calendar.MINUTE, Integer.valueOf(new SimpleDateFormat("HH:mm").format(pChatAttributes.getWeekDayOpenTime()).substring(3, 5)));
			chatStartDT.set(Calendar.SECOND, BBBCoreConstants.ZERO);
			
			chatEndDT.set(Calendar.HOUR_OF_DAY, Integer.valueOf(new SimpleDateFormat("HH:mm").format(pChatAttributes.getWeekDayCloseTime()).substring(0, 2)));
			chatEndDT.set(Calendar.MINUTE, Integer.valueOf(new SimpleDateFormat("HH:mm").format(pChatAttributes.getWeekDayCloseTime()).substring(3, 5)));
			chatEndDT.set(Calendar.SECOND, BBBCoreConstants.ZERO);
		}
		
		if(currentDT.getTime().compareTo(chatStartDT.getTime()) < 0 || currentDT.getTime().compareTo(chatEndDT.getTime())>0)
		{
			bWindowOpen = false;
		}
		return bWindowOpen;
	}

	
}
