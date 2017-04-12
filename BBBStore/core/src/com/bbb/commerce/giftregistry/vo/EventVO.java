/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import atg.core.util.StringUtils;


// TODO: Auto-generated Javadoc
/**
 * This class provides the event related properties.
 *
 * @author sku134
 */
public class EventVO implements Serializable{
	
	@Override
	public String toString() {
		return "EventVO [eventDate=" + eventDate + ", eventDateWS="
				+ eventDateWS + ", birthDate=" + birthDate + ", birthDateWS="
				+ birthDateWS + ", guestCount=" + guestCount
				+ ", showerDateWS=" + showerDateWS + ", showerDate="
				+ showerDate + ", college=" + college + ", babyGender="
				+ babyGender + ", babyName=" + babyName + ", babyNurseryTheme="
				+ babyNurseryTheme + ", showerDateObject=" + showerDateObject
				+ "]";
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The event date. */
	private String eventDate;
	
	/** The event date ws. */
	private String eventDateWS;
	
	/** The birth date. */
	private String birthDate;
	
	/** The birth date ws. */
	private String birthDateWS;
	
	/** The guest count. */
	private String guestCount;
	
	/** The shower date ws. */
	private String showerDateWS;
	
	/** The shower date. */
	private String showerDate;
	
	/** The college. */
	private String college;
	
	/** The baby gender. */
	private String babyGender;
	
	/** The baby name. */
	private String babyName;
	
	/** The baby nursery theme. */
	private String babyNurseryTheme;
	
	/** The showerDate date object */
	private Date showerDateObject;
	
	/**
	 * constructor stub.
	 */
	public EventVO() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * Gets the event date.
	 *
	 * @return the eventDate
	 */
	public String getEventDate() {
		return eventDate;
	}


	/**
	 * Sets the event date.
	 *
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}


	


	/**
	 * Gets the guest count.
	 *
	 * @return the guest count
	 */
	public String getGuestCount() {
		return guestCount;
	}


	/**
	 * Sets the guest count.
	 *
	 * @param guestCount the new guest count
	 */
	public void setGuestCount(String guestCount) {
		this.guestCount = guestCount;
	}


	/**
	 * Gets the shower date.
	 *
	 * @return the showerDate
	 */
	public String getShowerDate() {
		return showerDate;
	}


	/**
	 * Sets the shower date.
	 *
	 * @param showerDate the showerDate to set
	 */
	public void setShowerDate(String showerDate) {
		this.showerDate = showerDate;
	}


	/**
	 * Gets the college.
	 *
	 * @return the college
	 */
	public String getCollege() {
		return college;
	}


	/**
	 * Sets the college.
	 *
	 * @param college the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
	}


	/**
	 * Gets the baby gender.
	 *
	 * @return the babyGender
	 */
	public String getBabyGender() {
		return babyGender;
	}


	/**
	 * Sets the baby gender.
	 *
	 * @param babyGender the babyGender to set
	 */
	public void setBabyGender(String babyGender) {
		this.babyGender = babyGender;
	}


	/**
	 * Gets the baby name.
	 *
	 * @return the babyName
	 */
	public String getBabyName() {
		return babyName;
	}


	/**
	 * Sets the baby name.
	 *
	 * @param babyName the babyName to set
	 */
	public void setBabyName(String babyName) {
		this.babyName = babyName;
	}


	/**
	 * Gets the baby nursery theme.
	 *
	 * @return the babyNurseryTheme
	 */
	public String getBabyNurseryTheme() {
		return babyNurseryTheme;
	}


	/**
	 * Sets the baby nursery theme.
	 *
	 * @param babyNurseryTheme the babyNurseryTheme to set
	 */
	public void setBabyNurseryTheme(String babyNurseryTheme) {
		this.babyNurseryTheme = babyNurseryTheme;
	}


	/**
	 * Gets the event date ws.
	 *
	 * @return the event date ws
	 */
	public String getEventDateWS() {
		return eventDateWS;
	}


	/**
	 * Sets the event date ws.
	 *
	 * @param eventDateWS the new event date ws
	 */
	public void setEventDateWS(String eventDateWS) {
		this.eventDateWS = eventDateWS;
	}


	/**
	 * Gets the shower date ws.
	 *
	 * @return the shower date ws
	 */
	public String getShowerDateWS() {
		return showerDateWS;
	}


	/**
	 * Sets the shower date ws.
	 *
	 * @param showerDateWS the new shower date ws
	 */
	public void setShowerDateWS(String showerDateWS) {
		this.showerDateWS = showerDateWS;
	}


	/**
	 * Gets the birth date.
	 *
	 * @return the birthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}


	/**
	 * Sets the birth date.
	 *
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}


	/**
	 * Gets the birth date ws.
	 *
	 * @return the birthDateWS
	 */
	public String getBirthDateWS() {
		return birthDateWS;
	}


	/**
	 * Sets the birth date ws.
	 *
	 * @param birthDateWS the birthDateWS to set
	 */
	public void setBirthDateWS(String birthDateWS) {
		this.birthDateWS = birthDateWS;
	}
	
	/**
	 * Gets the showerDateObject.
	 *
	 * @return the showerDateObject
	 */
	public Date getShowerDateObject() {
	
		if(!StringUtils.isEmpty(showerDate)){
			 DateFormat formatter ; 
			 try{ 
				 formatter = new SimpleDateFormat("yyyyMMdd");
				 showerDateObject = (Date)formatter.parse(showerDate);  
				 
			  } catch (ParseException e){
				  showerDateObject = null;
			  }  
			 
		}

		return showerDateObject;
	}

	/**
	 * Sets the showerDateObject.
	 *
	 * @param showerDateObject the showerDateObject to set
	 */
	public void setShowerDateObject(Date showerDateObject) {
		this.showerDateObject = showerDateObject;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result 
				+ ((babyGender == null) ? 0 : babyGender.hashCode());
		result = prime * result
				+ ((babyName == null) ? 0 : babyName.hashCode());
		result = prime
				* result
				+ ((babyNurseryTheme == null) ? 0 : babyNurseryTheme.hashCode());
		result = prime * result
				+ ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result
				+ ((birthDateWS == null) ? 0 : birthDateWS.hashCode());
		result = prime * result + ((college == null) ? 0 : college.hashCode());
		result = prime * result
				+ ((eventDate == null) ? 0 : eventDate.hashCode());
		result = prime * result
				+ ((eventDateWS == null) ? 0 : eventDateWS.hashCode());
		result = prime * result
				+ ((guestCount == null) ? 0 : guestCount.hashCode());
		result = prime * result
				+ ((showerDate == null) ? 0 : showerDate.hashCode());
		result = prime
				* result
				+ ((showerDateObject == null) ? 0 : showerDateObject.hashCode());
		result = prime * result
				+ ((showerDateWS == null) ? 0 : showerDateWS.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventVO other = (EventVO) obj;
		if (babyGender != other.babyGender)
			return false;
		if (babyName == null) {
			if (other.babyName != null)
				return false;
		} else if (!babyName.equals(other.babyName))
			return false;
		if (babyNurseryTheme == null) {
			if (other.babyNurseryTheme != null)
				return false;
		} else if (!babyNurseryTheme.equals(other.babyNurseryTheme))
			return false;
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (birthDateWS == null) {
			if (other.birthDateWS != null)
				return false;
		} else if (!birthDateWS.equals(other.birthDateWS))
			return false;
		if (college == null) {
			if (other.college != null)
				return false;
		} else if (!college.equals(other.college))
			return false;
		if (eventDate == null) {
			if (other.eventDate != null)
				return false;
		} else if (!eventDate.equals(other.eventDate))
			return false;
		if (eventDateWS == null) {
			if (other.eventDateWS != null)
				return false;
		} else if (!eventDateWS.equals(other.eventDateWS))
			return false;
		if (guestCount == null) {
			if (other.guestCount != null)
				return false;
		} else if (!guestCount.equals(other.guestCount))
			return false;
		if (showerDate == null) {
			if (other.showerDate != null)
				return false;
		} else if (!showerDate.equals(other.showerDate))
			return false;
		if (showerDateObject == null) {
			if (other.showerDateObject != null)
				return false;
		} else if (!showerDateObject.equals(other.showerDateObject))
			return false;
		if (showerDateWS == null) {
			if (other.showerDateWS != null)
				return false;
		} else if (!showerDateWS.equals(other.showerDateWS))
			return false;
		return true;
	}
	
	
}
