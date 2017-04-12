package com.bbb.certona.manager;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import atg.core.util.StringUtils;
import com.bbb.common.BBBGenericService;
import atg.nucleus.Nucleus;

import com.bbb.certona.marshaller.ICertonaMarshaller;

/**
 * 
 * @author njai13
 *
 */
public class CertonaFeedManager extends BBBGenericService {
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


	public void getFeedDetails (String typeOfFeed ,boolean isFullDataFeed, Timestamp schedulerStartDate){
		
			logDebug("Entering getFeedDetails of CertonaFeedManager with typeOfFeed=["+typeOfFeed+"]");
		

		if(typeOfFeed!=null && !typeOfFeed.isEmpty()){
			final String resolveNameKey=this.feedTypeMarshallerMap.get(typeOfFeed);
			if(StringUtils.isBlank(resolveNameKey)){
				
					logInfo("Marshaller component not defined for feed type : "+typeOfFeed);
				
			}else{	
				final ICertonaMarshaller marshaller=(ICertonaMarshaller)Nucleus.getGlobalNucleus().resolveName(resolveNameKey);
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
