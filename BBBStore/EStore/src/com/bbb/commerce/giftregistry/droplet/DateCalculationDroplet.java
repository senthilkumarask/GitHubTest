/**
 * 
 */
package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

// TODO: Auto-generated Javadoc
/**
 * The Class DateCalculationDroplet.
 * 
 * @author vagar4
 */
public class DateCalculationDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;
	
	private static final String FLAG_KEY = "dateFlag";
	private static final String DIFF_DAYS = "numberOfDays";	
	private static final String DAYS_TO_GO = "daysToGo";

	/**
	 * Gets the gift registry manager.
	 * 
	 * @return the giftRegistryManager
	 */
	public final GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public final void setGiftRegistryManager(
			GiftRegistryManager pGiftRegistryManager) {
		mGiftRegistryManager = pGiftRegistryManager;
	}

	/**
	 * Calculate the difference between today's date and the input date.
	 * 
	 * @param request
	 *            - HTTP Request
	 * @param response
	 *            - HTTP Response
	 * @throws IOException
	 *             if an error occurs
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException,
			IOException {
 		logDebug(" DateCalculationDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		String eventDate = request.getParameter("eventDate");
		String convertDateFlag = request.getParameter("convertDateFlag");

		String currentSiteId = getCurrentSiteId();
		if ((BBBCoreConstants.TRUE).equals(convertDateFlag)) {
			if (currentSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || currentSiteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)) {
				eventDate = BBBUtility.convertDateToAppFormat(eventDate);
			}
		}
		try {
				Date date = getGiftRegistryManager().compareDate(eventDate);
				if(date ==null){
					request.serviceLocalParameter(OPARAM_ERROR, request, response);
					return;
				}
				logDebug("Date from Gift Registry" + date);
				logDebug("Date from Gift Registry" + date.toString());
				final Calendar calDate = Calendar.getInstance();
				calDate.setTime(date);
				final Calendar current = Calendar.getInstance();  
				int currentYear, currentMonth, currentDay;
				currentYear = current.get(Calendar.YEAR);  
				currentMonth = current.get(Calendar.MONTH);  
				currentDay = current.get(Calendar.DATE);  
				current.set(currentYear, currentMonth, currentDay);  

				Map<String, Object> resultMap = calculateDaysParameters(calDate, current);
				if(resultMap!=null){
					Integer dateFlag = (Integer)resultMap.get(DateCalculationDroplet.FLAG_KEY);
					Long days = (Long) resultMap.get(DateCalculationDroplet.DIFF_DAYS);
					
					if (dateFlag==1) {
						request.setParameter("daysToGo", days);
						request.setParameter("check", "true");
						request.setParameter("daysCheck", "true");
						logDebug("Inside If Block - remDays : " + days);
					} else if(dateFlag==-1){
						Long daysToGo = (Long)resultMap.get(DateCalculationDroplet.DAYS_TO_GO);
						request.setParameter("daysToNextCeleb", daysToGo);
						request.setParameter("daysToGo", days);
						request.setParameter("check", "false");
						request.setParameter("daysCheck", "false");
						logDebug("Inside else Block - remDays : " + days);
					}else{
						Long daysToGo = (Long)resultMap.get(DateCalculationDroplet.DAYS_TO_GO);
						request.setParameter("daysToGo", days);
						request.setParameter("daysCheck", "true");
						request.setParameter("daysToNextCeleb", daysToGo);
						request.setParameter("check", "true");
					}
				}
				request.serviceLocalParameter(OPARAM_OUTPUT, request, response);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(request, "Date Calculation BBBBusinessException from service of DateCalculationDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1065),e);
			request.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			request.serviceLocalParameter(OPARAM_ERROR, request, response);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(request, "Date Calculation BBBSystemException from service of DateCalculationDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1066),e);
			request.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			request.serviceLocalParameter(OPARAM_ERROR, request, response);
		} catch (ParseException e) {
			logError(LogMessageFormatter.formatMessage(request, "Date Calculation ParseException from service of DateCalculationDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1067),e);
			request.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			request.serviceLocalParameter(OPARAM_ERROR, request, response);
		}
		logDebug(" DateCalculationDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}

	/**
	 * @return
	 */
	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
	
	/**
	 * 
	 * Determines whether the event has occur or not and the number of days in which the event occurs if the event is yet to happen.
	 * Two/Three Flags are returned on whether the event is yet to occur(2 Values - dateFlag and remainingDays) or the event has already happened
	 * (3 Values - dateFlag, remainingDays and daysToGo).  
	 * 
	 * @param event
	 * @param current
	 * @return dateFlag - 0 - sameDate Event, 1- Event that occurs later, -1 - Event that already occurred.<br>
	 * remainingDays - days difference in between the eventDates and currentDates.<br>
	 * daysToGo - The number of days in which the event will happen in this year or the next year for yearly events like Birthdays. 
	 * @throws BBBBusinessException
	 */
	protected Map<String, Object> calculateDaysParameters(Calendar event, Calendar current) throws BBBBusinessException{
		if(event!=null && current!=null){
			logDebug("The Values obtained are eventDate :-  " + event.toString() + " currentDate :- "+ current.toString() );
			int currentYear, currentMonth, currentDay;
			currentYear = current.get(Calendar.YEAR);  
			currentMonth = current.get(Calendar.MONTH);  
			currentDay = current.get(Calendar.DATE);  
			current.set(currentYear, currentMonth, currentDay);  
			Map<String, Object> resultMap = null; 
			resultMap = new HashMap<String, Object>();
			int dateFlag = 0;
			int compareDates = compareDates(event, current, false);
        	if(compareDates>0){
        		dateFlag = 1;
        		resultMap.put(DateCalculationDroplet.FLAG_KEY, dateFlag);
        		logDebug("The EventDate is in the future.");
        	}else if(compareDates==0){
        		dateFlag = 0;
        		resultMap.put(DateCalculationDroplet.FLAG_KEY, dateFlag);
        		logDebug("The EventDate is today.");
        	}else if(compareDates<0){
        		dateFlag = -1;
        		resultMap.put(DateCalculationDroplet.FLAG_KEY, dateFlag);
        		logDebug("The EventDate has already passed.");
        	}
        	dateFlag = (Integer)resultMap.get(DateCalculationDroplet.FLAG_KEY);
			long remDays = 0;
			if(dateFlag==1){
				remDays = daysDifference(event, current);
			}else if(dateFlag==-1){
				remDays = daysDifference(current, event);
				long daysToGo = daysToGo(event, current);
				resultMap.put(DateCalculationDroplet.DAYS_TO_GO, daysToGo);
			}else{
				remDays = 0;
				long daysToGo = daysToGo(event, current);
				resultMap.put(DateCalculationDroplet.DAYS_TO_GO, daysToGo);
			}
			resultMap.put(DateCalculationDroplet.DIFF_DAYS, remDays);
			return resultMap;
		}else{
			if(event == null){
				throw new BBBBusinessException("Unable to Calculate difference in Days. The values are eventDate :- null  and currentDate:- " + current.toString());
			}
			if(current == null){
				throw new BBBBusinessException("Unable to Calculate difference in Days. The values are eventDate :- " + event.toString() + "  and currentDate:- null");
			} else {
				throw new BBBBusinessException("Unable to Calculate difference in Days. The values are eventDate :- " + event.toString() + "  and currentDate:- " + current.toString());
			}
		}
	}
	
	/**
	 * Generates the difference in days between two calendar objects.
	 * The firstDate should be larger than the secondDate.
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return differenceInDays
	 */
	protected long daysDifference(Calendar firstDate, Calendar secondDate){
		int eventYear = secondDate.get(Calendar.YEAR);
		int tempEventYear = eventYear;
		int currentYear = firstDate.get(Calendar.YEAR);
		long differenceInDays = 0;
		if(tempEventYear<currentYear){
			if(tempEventYear%4==0){
				differenceInDays = 366-secondDate.get(Calendar.DAY_OF_YEAR);
			}else{
				differenceInDays = 365-secondDate.get(Calendar.DAY_OF_YEAR);
			}
			tempEventYear++;
		}
		while(tempEventYear<currentYear){
			if(tempEventYear%4==0){
				differenceInDays = differenceInDays + 366;
			}else{
				differenceInDays = differenceInDays + 365;
			}
			tempEventYear++;
		}
		if(eventYear==currentYear){
			differenceInDays = firstDate.get(Calendar.DAY_OF_YEAR) - secondDate.get(Calendar.DAY_OF_YEAR);
		}else{
			differenceInDays = differenceInDays + firstDate.get(Calendar.DAY_OF_YEAR);
		}
		return differenceInDays - 1;
	}
	
	/**
	 * 
	 * Generates the number of days till the event occurs in the same year or the 
	 * number of days for the event to occur in the next year. 
	 * 
	 * @param event
	 * @param current
	 * @return daysToGo
	 * 
	 */
	protected long daysToGo(Calendar event, Calendar current){
		long daysToGo = 0;
		int flag = compareDates(event, current, true);
		if(flag==-1){
			if(current.get(Calendar.YEAR)%4==0){
				daysToGo = 366 - current.get(Calendar.DAY_OF_YEAR);
			}else{
				daysToGo = 365 - current.get(Calendar.DAY_OF_YEAR);
			}
			daysToGo = daysToGo + event.get(Calendar.DAY_OF_YEAR);
		}else if(flag==1){
			daysToGo = event.get(Calendar.DAY_OF_YEAR) - current.get(Calendar.DAY_OF_YEAR);
		}else{
			daysToGo = 0;
		}
		return daysToGo;
	}
	
	/**
	 * 
	 * Compares the two dates and returns whether the eventDate is before, after or the same currentDate.
	 * A boolean variable yearMatch is used to see if the date in the year is before, after or same
	 * day in the current year. 
	 * 
	 * @param eventDate
	 * @param currentDate
	 * @param yearMatch - whether the day in the eventDate object has passed the same date in the currentDate.
	 * @return -1 if eventDate is before currentDate.<br>
	 *  0 if both the dates are same.<br>
	 *  1 if eventDate is after currentDate
	 */
	private int compareDates(Calendar eventDate, Calendar currentDate, boolean yearMatch){
		int eventYear, eventMonth, eventDay, currentYear, currentMonth, currentDay;
		currentYear = currentDate.get(Calendar.YEAR);
		currentMonth = currentDate.get(Calendar.MONTH);
		currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
		if(yearMatch){
			eventYear = currentYear;
		}else{
			eventYear = eventDate.get(Calendar.YEAR);
		}
		eventMonth = eventDate.get(Calendar.MONTH);
		eventDay = eventDate.get(Calendar.DAY_OF_MONTH);
		if(eventYear<currentYear){
			return -1;
		}else if(currentYear==eventYear){
			if(eventMonth>currentMonth){
				return 1;
			}else if(eventMonth<currentMonth){
				return -1;
			}else{
				if(eventDay<currentDay){
					return -1;
				}else if(eventDay>currentDay){
					return 1;
				}else{
					return 0;
				}
			}
		}else{
			return 1;
		}
	}
	
}
