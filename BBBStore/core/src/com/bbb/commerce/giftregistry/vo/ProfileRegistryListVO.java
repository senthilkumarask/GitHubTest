/**
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author simra2
 *
 */
public class ProfileRegistryListVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8433763933482299719L;
	private List<RegistrySkinnyVO>  profileRegistryList;
    private List<RegistrySummaryVO> recommendedRegistryList;
	public List<RegistrySkinnyVO> getProfileRegistryList() {
		return profileRegistryList;
	}
	public void setProfileRegistryList(List<RegistrySkinnyVO> profileRegistryList) {
		this.profileRegistryList = profileRegistryList;
	}
	public List<RegistrySummaryVO> getRecommendedRegistryList() {
		return recommendedRegistryList;
	}
	public void setRecommendedRegistryList(
			List<RegistrySummaryVO> recommendedRegistryList) {
		this.recommendedRegistryList = recommendedRegistryList;
	}
	@Override
	public String toString() {
		return "ProfileRegistryListVO [profileRegistryList="
				+ profileRegistryList + ", recommendedRegistryList="
				+ recommendedRegistryList + "]";
	}

}
