/**
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;

/**
 * @author pku104
 *
 */
public class RegistryBabyVO implements Serializable {
	
	
	private static final long serialVersionUID = -8857736016758109917L;
	private String gender;
	private String decor;
	private String firstName;
	private String babyRegMaidenName;
	private String babyCoMaidenName;
	
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the decor
	 */
	public String getDecor() {
		return decor;
	}

	/**
	 * @param decor the decor to set
	 */
	public void setDecor(String decor) {
		this.decor = decor;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getBabyRegMaidenName() {
		return babyRegMaidenName;
	}

	public void setBabyRegMaidenName(String babyRegMaidenName) {
		this.babyRegMaidenName = babyRegMaidenName;
	}

	public String getBabyCoMaidenName() {
		return babyCoMaidenName;
	}

	public void setBabyCoMaidenName(String babyCoMaidenName) {
		this.babyCoMaidenName = babyCoMaidenName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegBabyVO [gender=" + gender + ", decor=" + decor
				+ ", firstName=" + firstName + "]";
	}

}
