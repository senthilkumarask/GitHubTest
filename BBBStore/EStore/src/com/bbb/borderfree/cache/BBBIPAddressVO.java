package com.bbb.borderfree.cache;

import java.io.Serializable;

/**
 * This class holds fromIp, toIp and country code
 * retrieved from International Repository.
 * 
 * @author apan25
 *
 */
public class BBBIPAddressVO implements Comparable<BBBIPAddressVO>, Serializable {

	private static final long	serialVersionUID	= -7086304863052267372L;
	
	private Long fromIP;
	private Long toIP;
	private String country;
	
	/**
	 * 
	 * @return the fromIP
	 */
	public Long getFromIP () {
		return fromIP;
	}

	/**
	 * 
	 * @param fromIP the fromIP to set
	 */
	public void setFromIP (final Long fromIP) {
		this.fromIP = fromIP;
	}

	/**
	 * 
	 * @return the toIP
	 */
	public Long getToIP() {
		return toIP;
	}

	/**
	 * 
	 * @param toIP the toIP to set
	 */
	public void setToIP(final Long toIP) {
		this.toIP = toIP;
	}

	/**
	 * 
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * 
	 * @param country the country to set
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	/**
	 * This method is used to get a string object
	 * ofBBBIPAddressVO.
	 */
	@Override
	public String toString() {
		return "BBBIPAddressVO [fromIP=" + fromIP + ", toIP=" + toIP + ", country=" + country + "]";
	}
	
	/**
	 * This method compares hash codes of 
	 * fromIp, toIp and country.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((fromIP == null) ? 0 : fromIP.hashCode());
		result = prime * result + ((toIP == null) ? 0 : toIP.hashCode());
		return result;
	}

	/**
	 * This method equates hash codes of 
	 * fromIp, toIp and country.
	 */
	@Override
	public boolean equals (final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		BBBIPAddressVO other = (BBBIPAddressVO) obj;
		if (country == null && other.country != null)
				return false;
		else if (null!=country && !country.equals(other.country))
			return false;
		
		if (fromIP == null && other.fromIP != null)
				return false;
		else if (null!=fromIP && !fromIP.equals(other.fromIP))
			return false;
		
		if (toIP == null && other.toIP != null)
				return false;
		else if (null!=toIP && !toIP.equals(other.toIP))
			return false;
		
		return true;
	}

	/**
	 * This method compares fromIP.
	 */
	@Override
	public int compareTo(final  BBBIPAddressVO addressVO) {
		return this.getFromIP().compareTo(addressVO.getFromIP());
	}

	/**
	 * 
	 * @param ipAddress the ipAddress to set
	 * @return the true if ipAddress is range
	 */
	public boolean contains(final long ipAddress) {
		if (null != getFromIP() && (getFromIP().longValue() <= ipAddress && getToIP().longValue() >= ipAddress)) {
				return true;
		}
		return false;
	}
	
}
