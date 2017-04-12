/**
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;

/**
 * @author pku104
 *
 */
public class RegistryPrefStoreVO implements Serializable {
	
	private static final long serialVersionUID = 5358413241786373912L;
	private String storeNum;
	private String contactFlag;

	/**
	 * @return the storeNum
	 */
	public String getStoreNum() {
		return storeNum;
	}

	/**
	 * @param storeNum the storeNum to set
	 */
	public void setStoreNum(String storeNum) {
		this.storeNum = storeNum;
	}

	/**
	 * @return the contactFlag
	 */
	public String getContactFlag() {
		return contactFlag;
	}

	/**
	 * @param contactFlag the contactFlag to set
	 */
	public void setContactFlag(String contactFlag) {
		this.contactFlag = contactFlag;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegistryPrefStoreVO [storeNum=" + storeNum + ", contactFlag="
				+ contactFlag + "]";
	}

}
