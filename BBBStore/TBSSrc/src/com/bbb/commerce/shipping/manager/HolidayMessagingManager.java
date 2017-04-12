package com.bbb.commerce.shipping.manager;



import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.shipping.vo.HolidayMessagingVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.nucleus.GenericService;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

public class HolidayMessagingManager extends GenericService {
Repository mHolidayMessagingRepository;
	
	private BBBCatalogTools catalogTools;
	private Map<String, String> canadaStandardMessageKeysMap;
	private Map<String, String> canadaExpeditedMessageKeysMap;
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param availabilityOn
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws RepositoryException 
	 */
	public HolidayMessagingVO getMessagingItems(String availabilityOn) throws ServletException, IOException, RepositoryException{
	
	vlogDebug("HolidayMessagingManager :: getMessagingItems method start");
		
	RepositoryItem[] holidayItems = null;
	RepositoryItemDescriptor holidayDescriptor = null;
	Timestamp startTime = null;
	Timestamp endTime = null;
	Timestamp currentTime;
	boolean validTime = Boolean.FALSE;
	RepositoryItem standardLabel = null, expeditedLabel=null; 
	String name=null;
	HolidayMessagingVO lHolidayMessagingVO = new HolidayMessagingVO();
	
	
	holidayDescriptor = getHolidayMessagingRepository().getItemDescriptor("holidayMessaging");
	RepositoryView holidayView = holidayDescriptor.getRepositoryView();
	QueryBuilder QueryBuilder = holidayView.getQueryBuilder();
	QueryExpression availabileProperty = QueryBuilder.createPropertyQueryExpression("itemAvailability");
	QueryExpression availabileValue = QueryBuilder.createConstantQueryExpression(availabilityOn);
	Query skuQuery = QueryBuilder.createComparisonQuery(availabileProperty, availabileValue, QueryBuilder.EQUALS);

	holidayItems = holidayView.executeQuery(skuQuery);
	
	if (null != holidayItems && holidayItems.length > TBSConstants.ZERO) {
		
		Date date = new Date();
		currentTime = new Timestamp(date.getTime());
		for (RepositoryItem holidayItem : holidayItems) {
			if(holidayItem.getPropertyValue("startTime") != null){
				startTime = (Timestamp) holidayItem.getPropertyValue("startTime");
			}
			if(holidayItem.getPropertyValue("endTime") != null){
				endTime = (Timestamp) holidayItem.getPropertyValue("endTime");
			}
			// checking for the startdate and no end date
			if(startTime != null && endTime == null){
				validTime = validateTime(currentTime, startTime, null);
			} else {
				// checking with both start date and end date
				validTime = validateTime(currentTime, startTime, endTime);
			}
			if(validTime){
				if(null!= holidayItem.getPropertyValue("standardLabel")){
					standardLabel= (RepositoryItem) holidayItem.getPropertyValue("standardLabel");
					lHolidayMessagingVO.setStandardLabel((String) standardLabel.getPropertyValue("key"));
				}
				if(null!=holidayItem.getPropertyValue("expeditedLabel")){
					expeditedLabel = (RepositoryItem) holidayItem.getPropertyValue("expeditedLabel");
					lHolidayMessagingVO.setExpeditedLabel((String) expeditedLabel.getPropertyValue("key"));
				}
				if(null!=holidayItem.getPropertyValue("name")){
					name = holidayItem.getPropertyValue("name").toString();
				}
				lHolidayMessagingVO.setName(name);
				break;
			}
			startTime = null;
			endTime = null;
		}
		vlogDebug("Repository Items :: startTime ::" + startTime + " endTime ::"+ endTime 
					+ " standardLabel :: " + standardLabel+ " expeditedLabel :: " +expeditedLabel+ " name :: "+name);
		
	}
	vlogDebug("HolidayMessagingManager :: getMessagingItems method end");
	return lHolidayMessagingVO;
	}
	
	
	/**
	 * Get holiday messaging from config keys
	 * @param availabilityOn
	 * @return Holiday messaging based on config keys
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public HolidayMessagingVO getCanadaMessagingItems (String availabilityOn) throws BBBSystemException, BBBBusinessException {
		vlogDebug("getCanadaMessagingItems : availabilityOn=" + availabilityOn);
		HolidayMessagingVO messagingVO = new HolidayMessagingVO();
		Timestamp startTime = null, endTime = null, currentTime = new Timestamp(new Date().getTime());
		startTime = getHolidayDateTime(availabilityOn, BBBCoreConstants.HOLIDAY_MESSAGING_START_DATE);
		endTime = getHolidayDateTime(availabilityOn, BBBCoreConstants.HOLIDAY_MESSAGING_END_DATE);
		if (validateTime(currentTime, startTime, endTime)) {
			messagingVO.setStandardLabel(this.getCanadaStandardMessageKeysMap().get(availabilityOn));
			messagingVO.setExpeditedLabel(this.getCanadaExpeditedMessageKeysMap().get(availabilityOn));
		}
		return messagingVO;
	}
		
	/**
	 * Check if the current dateTime is within start and end dateTime
	 * @param currentTime
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private boolean validateTime(Timestamp currentTime, Timestamp startTime, Timestamp endTime) {

		vlogDebug("HolidayMessagingManager validateTime():: START");

		if(startTime != null && endTime != null){
			if (currentTime.getTime()>=startTime.getTime() && currentTime.getTime()<=endTime.getTime()) {
					return true;
				}
		} else if(startTime != null && currentTime.getTime()>=startTime.getTime()){
				return true;
			}
		vlogDebug("HolidayMessagingManager validateDate():: END");

		return false;
	}
	
	/**
	 * 
	 * @param availabilityOn
	 * @param dateConfigKey
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws ParseException
	 */
	private Timestamp getHolidayDateTime(String availabilityOn, String dateConfigKey)
			throws BBBSystemException, BBBBusinessException {
		vlogDebug("getHolidayDateTime params: availabilityOn = " + availabilityOn + ", dateConfigKey =" + dateConfigKey  );
		Timestamp dateTime = null;
		String timeRangeStr = getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS , dateConfigKey).get(0);
		String[] timeRangeSplit = timeRangeStr.split(BBBCoreConstants.COMMA);
		for (int i = 0; i < timeRangeSplit.length; i++) {
			String[] tempStr = timeRangeSplit[i].split(BBBCoreConstants.EQUAL);
			if (tempStr[0].trim().equalsIgnoreCase(availabilityOn)) {
				SimpleDateFormat formatter = new SimpleDateFormat(BBBCoreConstants.FORMAT); //yyyy-MMM-dd HH:mm:ss
				try {
					dateTime = new Timestamp(formatter.parse(tempStr[1].trim()).getTime());
				} catch (ParseException e) {
					logError("Error occurred while parsing holiday message date from config key:" + dateConfigKey);
					throw new BBBSystemException("Error occurred while parsing holiday message date from config key:" + dateConfigKey, e);
				}
				break;
			}
		}
		vlogDebug("getHolidayDateTime return value : dateTime = " + dateTime  );
		return dateTime;
	}


	/**
	 * @return the holidayMessagingRepository
	 */
	public Repository getHolidayMessagingRepository() {
		return mHolidayMessagingRepository;
	}

	/**
	 * @param pHolidayMessagingRepository the holidayMessagingRepository to set
	 */
	public void setHolidayMessagingRepository(Repository pHolidayMessagingRepository) {
		mHolidayMessagingRepository = pHolidayMessagingRepository;
	}


	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
}


	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


	/**
	 * @return the canadaStandardMessageKeysMap
	 */
	public Map<String, String> getCanadaStandardMessageKeysMap() {
		return canadaStandardMessageKeysMap;
	}


	/**
	 * @param canadaStandardMessageKeysMap the canadaStandardMessageKeysMap to set
	 */
	public void setCanadaStandardMessageKeysMap(Map<String, String> canadaStandardMessageKeysMap) {
		this.canadaStandardMessageKeysMap = canadaStandardMessageKeysMap;
	}


	/**
	 * @return the canadaExpeditedMessageKeysMap
	 */
	public Map<String, String> getCanadaExpeditedMessageKeysMap() {
		return canadaExpeditedMessageKeysMap;
	}


	/**
	 * @param canadaExpeditedMessageKeysMap the canadaExpeditedMessageKeysMap to set
	 */
	public void setCanadaExpeditedMessageKeysMap(Map<String, String> canadaExpeditedMessageKeysMap) {
		this.canadaExpeditedMessageKeysMap = canadaExpeditedMessageKeysMap;
	}
}
