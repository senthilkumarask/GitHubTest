package com.bbb.common.event;


//import com.sprint.common.ContextObject;


import atg.nucleus.logging.LogEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class PersistedInfoLogEvent.
 */
public class PersistedInfoLogEvent extends LogEvent {
	

	private String time;
	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	private String storeID;
	private String channel;
	private String siteID;
	private String upcCode;
	
	public String getStoreID() {
		return storeID;
	}


	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}


	public String getChannel() {
		return channel;
	}


	public void setChannel(String channel) {
		this.channel = channel;
	}


	public String getSiteID() {
		return siteID;
	}


	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}


	public String getUpcCode() {
		return upcCode;
	}


	public void setUpcCode(String upcCode) {
		this.upcCode = upcCode;
	}


	public String getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	public String getLatitude() {
		return latitude;
	}


	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	private String longitude;
	private String latitude;
	private String identifier;
	private String type;
	private String messageDescription;

	public String getIdentifier() {
		return identifier;
	}


	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public final void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the messageDescription
	 */
	public final String getMessageDescription() {
		return messageDescription;
	}


	/**
	 * @param messageDescription the messageDescription to set
	 */
	public final void setMessageDescription(String messageDescription) {
		this.messageDescription = messageDescription;
	}


	public PersistedInfoLogEvent(String pMessage,String upcCode, String longitude,String latitude,String siteID,String channel,String storeID,String time) {
		super(pMessage);
		this.upcCode=upcCode;
		this.channel=channel;
		this.siteID=siteID;
		this.latitude=latitude;
		this.longitude=longitude;
		this.storeID=storeID;
		this.time=time;
			
		}

	public PersistedInfoLogEvent(String pMessage,String identifier, String upcCode, String longitude,String latitude,String siteID,String channel,String storeID,String time) {
		super(pMessage);
		this.identifier=identifier;
		this.upcCode=upcCode;
		this.channel=channel;
		this.siteID=siteID;
		this.latitude=latitude;
		this.longitude=longitude;
		this.storeID=storeID;
		this.time=time;
		}

	public PersistedInfoLogEvent(String pMessage, String identifier, String upcCode, String longitude, String latitude, String siteID, String channel, String storeID, String time, String type, String messageDescription) {
		super(pMessage);
		this.identifier=identifier;
		this.upcCode=upcCode;
		this.channel=channel;
		this.siteID=siteID;
		this.latitude=latitude;
		this.longitude=longitude;
		this.storeID=storeID;
		this.time=time;
		this.type=type;
		this.messageDescription=messageDescription;
		}		
}

