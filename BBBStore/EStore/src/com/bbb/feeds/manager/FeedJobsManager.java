package com.bbb.feeds.manager;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;

import com.bbb.common.BBBGenericService;
import com.bbb.feeds.marshaller.IFeedsMarshaller;

/**
 * 
 * @author Prashanth K Bhoomula
 *
 */
public class FeedJobsManager extends BBBGenericService {
	private Map<String,String> feedTypeMarshallerMap=new HashMap<String,String>();


	/**
	 * @return the feedTypeMarshallerMap
	 */
	public Map<String, String> getFeedTypeMarshallerMap() {
		return feedTypeMarshallerMap;
	}


	/**
	 * @param feedTypeMarshallerMap the feedTypeMarshallerMap to set
	 */
	public void setFeedTypeMarshallerMap(Map<String, String> feedTypeMarshallerMap) {
		this.feedTypeMarshallerMap = feedTypeMarshallerMap;
	}

	/**
	 * 
	 * @param typeOfFeed
	 * @param isFullDataFeed
	 * @param schedulerStartDate
	 */
	public void getFeedDetails (String typeOfFeed ,boolean isFullDataFeed, Timestamp schedulerStartDate){
		
		logDebug("Entering getFeedDetails of FeedJobsManager with typeOfFeed=["+typeOfFeed+"]");
		if(typeOfFeed!=null && !typeOfFeed.isEmpty()){
			final String resolveNameKey=this.feedTypeMarshallerMap.get(typeOfFeed);
			if(StringUtils.isBlank(resolveNameKey)){
				logError("Marshaller component not defined for feed type : "+typeOfFeed);
			}else{	
				final IFeedsMarshaller marshaller=(IFeedsMarshaller)Nucleus.getGlobalNucleus().resolveName(resolveNameKey);
				if(marshaller!=null){
					marshaller.marshall(isFullDataFeed, schedulerStartDate);
				}
			}	
		}
		else{
			logDebug("Type of feed parameter is null");
		}
	}
}
