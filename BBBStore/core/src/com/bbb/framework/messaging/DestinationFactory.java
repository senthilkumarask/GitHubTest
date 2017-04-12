/*
 *
 * File  : DestinationFactory.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.messaging;

import com.bbb.common.BBBGenericService;
import atg.nucleus.ServiceMap;

/**
 * Holds the destination name to Destination MessageDestination map
 * 
 * @author manohar
 * @version 1.0
 */
public class DestinationFactory extends BBBGenericService{
	
	/**
	 * 
	 */
	private ServiceMap destinationMap;

	/**
	 * 
	 * @return destinationMap
	 */
	public ServiceMap getDestinationMap() {
		return destinationMap;
	}

	/**
	 * 
	 * @param destinationMap
	 */
	public void setDestinationMap(ServiceMap destinationMap) {
		this.destinationMap = destinationMap;
	}

	/**
	 * 
	 * @param service
	 * @return
	 */
	public MessageDestination getDestination(String service){
		return (MessageDestination)this.destinationMap.get(service);
	}
}
