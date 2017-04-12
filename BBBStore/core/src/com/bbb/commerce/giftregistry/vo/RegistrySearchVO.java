/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import com.bbb.framework.integration.ServiceRequestBase;

import atg.userprofiling.Profile;



// TODO: Auto-generated Javadoc
/**
 * This class provides the Registry search VO information properties.
 *
 * @author sku134
 */
public class RegistrySearchVO  extends ServiceRequestBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The first name. */
	private String firstName;
	
	/** The last name. */
	private String lastName;
	
	/** The excluded reg nums. */
	private String excludedRegNums;
	
	/** The registry id. */
	private String registryId;
	
	/** The email. */
	private String email;
	
	/** The per page size. */
	private int perPageSize;
	
	/** The page num. */
	private int pageNum;
	
	/** The blk size. */
	private int blkSize;
	
	/** The gift giver. */
	private boolean giftGiver;
	
	/** The profile id. */
	private Profile profileId;
	
	/** The sort seq. */
	private String sortSeq ;
	
	/** The sort seq order. */
	private String sortSeqOrder;
	
	/** The site id. */
	private String siteId;
	
	/** The start idx. */
	private int startIdx;
	
	/** The filter registries in profile. */
	private boolean filterRegistriesInProfile;
	
	/** The return leagacy registries. */
	private boolean returnLeagacyRegistries;
	
	/** The service name. */
	private String serviceName;
	
	
	/** The user token. */
	private String userToken;
	
	/** The view. */
	private int view;
	
	/** The avail for web purchase flag. */
	private boolean availForWebPurchaseFlag;
	
	private String state;
	
	private String event;
	
	private String sort;
	
	/**
	 * Gets the user token.
	 *
	 * @return the user token
	 */
	public final String getUserToken() {
		return userToken;
	}

	/**
	 * Sets the user token.
	 *
	 * @param pUserToken the new user token
	 */
	public final void setUserToken(String pUserToken) {
		userToken = pUserToken;
	}

	
	/**
	 * Gets the blk size.
	 *
	 * @return the blk size
	 */
	public int getBlkSize() {
		return blkSize;
	}

	/**
	 * Sets the blk size.
	 *
	 * @param blkSize the new blk size
	 */
	public void setBlkSize(int blkSize) {
		this.blkSize = blkSize;
	}

	/**
	 * Gets the start idx.
	 *
	 * @return the start idx
	 */
	public int getStartIdx() {
		return startIdx;
	}

	/**
	 * Sets the start idx.
	 *
	 * @param startIdx the new start idx
	 */
	public void setStartIdx(int startIdx) {
		this.startIdx = startIdx;
	}

	/**
	 * Very important to implement this method and let the framework know the
	 * whether the web service response needs to be cached.
	 *
	 * @return the boolean
	 */
	@Override
	public Boolean isCacheEnabled() {
		return false;
	}
	
	/**
	 * Checks if is filter registries in profile.
	 *
	 * @return true, if is filter registries in profile
	 */
	public final boolean isFilterRegistriesInProfile() {
		return filterRegistriesInProfile;
	}


	/**
	 * Sets the filter registries in profile.
	 *
	 * @param pFilterRegistriesInProfile the new filter registries in profile
	 */
	public final void setFilterRegistriesInProfile(
			boolean pFilterRegistriesInProfile) {
		filterRegistriesInProfile = pFilterRegistriesInProfile;
	}


	/**
	 * Checks if is return leagacy registries.
	 *
	 * @return true, if is return leagacy registries
	 */
	public boolean isReturnLeagacyRegistries() {
		return returnLeagacyRegistries;
	}


	/**
	 * Sets the return leagacy registries.
	 *
	 * @param pReturnLeagacyRegistries the new return leagacy registries
	 */
	public final void setReturnLeagacyRegistries(boolean pReturnLeagacyRegistries) {
		returnLeagacyRegistries = pReturnLeagacyRegistries;
	}


/**
 * Instantiates a new registry search vo.
 */
public RegistrySearchVO() {
	// TODO Auto-generated constructor stub
}


/**
 * Gets the first name.
 *
 * @return the firstName
 */
public String getFirstName() {
	return firstName;
}


/**
 * Sets the first name.
 *
 * @param firstName the firstName to set
 */
public void setFirstName(String firstName) {
	this.firstName = firstName;
}


/**
 * Gets the last name.
 *
 * @return the lastName
 */
public String getLastName() {
	return lastName;
}


/**
 * Sets the last name.
 *
 * @param lastName the lastName to set
 */
public void setLastName(String lastName) {
	this.lastName = lastName;
}


/**
 * Gets the registry id.
 *
 * @return the registryId
 */
public String getRegistryId() {
	return registryId;
}


/**
 * Sets the registry id.
 *
 * @param pRegistryId the registryId to set
 */
public void setRegistryId(String pRegistryId) {
	registryId = pRegistryId;
}


/**
 * Gets the email.
 *
 * @return the email
 */
public String getEmail() {
	return email;
}


/**
 * Sets the email.
 *
 * @param email the email to set
 */
public void setEmail(String email) {
	this.email = email;
}


/**
 * Gets the per page size.
 *
 * @return the perPageSize
 */
public int getPerPageSize() {
	return perPageSize;
}


/**
 * Sets the per page size.
 *
 * @param perPageSize the perPageSize to set
 */
public void setPerPageSize(int perPageSize) {
	this.perPageSize = perPageSize;
}


/**
 * Gets the page num.
 *
 * @return the pageNum
 */
public int getPageNum() {
	return pageNum;
}


/**
 * Sets the page num.
 *
 * @param pageNum the pageNum to set
 */
public void setPageNum(int pageNum) {
	this.pageNum = pageNum;
}


/**
 * Gets the gift giver.
 *
 * @return the isGiftGiver
 */
public boolean getGiftGiver() {
	return giftGiver;
}


/**
 * Sets the gift giver.
 *
 * @param isGiftGiver the isGiftGiver to set
 */
public void setGiftGiver(boolean isGiftGiver) {
	this.giftGiver = isGiftGiver;
}

/**
 * Gets the profile id.
 *
 * @return the profile id
 */
public Profile getProfileId() {
	return profileId;
}


/**
 * Sets the profile id.
 *
 * @param pProfileId the new profile id
 */
public final void setProfileId(Profile pProfileId) {
	profileId = pProfileId;
}


/**
 * Gets the site id.
 *
 * @return the siteId
 */
public String getSiteId() {
	return siteId;
}


/**
 * Sets the site id.
 *
 * @param siteId the siteId to set
 */
public void setSiteId(String siteId) {
	this.siteId = siteId;
}

/**
 * Gets the service name.
 *
 * @return the serviceName
 */
@Override
public String getServiceName() {
	return serviceName;
}


/**
 * Sets the service name.
 *
 * @param pServiceName the serviceName to set
 */
public void setServiceName(String pServiceName) {
	serviceName = pServiceName;
}

/**
 * Gets the view.
 *
 * @return the view
 */
public int getView() {
	return view;
}

/**
 * Sets the view.
 *
 * @param view the view to set
 */
public void setView(int view) {
	this.view = view;
}

/**
 * Gets the avail for web purchase flag.
 *
 * @return the isAvailForWebPurchaseFlag
 */
public boolean getAvailForWebPurchaseFlag() {
	return availForWebPurchaseFlag;
}

/**
 * Sets the avail for web purchase flag.
 *
 * @param availForWebPurchaseFlag the new avail for web purchase flag
 */
public void setAvailForWebPurchaseFlag(boolean availForWebPurchaseFlag) {
	this.availForWebPurchaseFlag = availForWebPurchaseFlag;
}

/**
 * Gets the excluded reg nums.
 *
 * @return the excluded reg nums
 */
public String getExcludedRegNums() {
	return excludedRegNums;
}

/**
 * Sets the excluded reg nums.
 *
 * @param excludedRegNums the new excluded reg nums
 */
public void setExcludedRegNums(String excludedRegNums) {
	this.excludedRegNums = excludedRegNums;
}

/**
 * Gets the sort seq.
 *
 * @return the sort seq
 */
public String getSortSeq() {
	return sortSeq;
}

/**
 * Sets the sort seq.
 *
 * @param sortSeq the new sort seq
 */
public void setSortSeq(String sortSeq) {
	this.sortSeq = sortSeq;
}

/**
 * Gets the sort seq order.
 *
 * @return the sort seq order
 */
public String getSortSeqOrder() {
	return sortSeqOrder;
}

/**
 * Sets the sort seq order.
 *
 * @param sortSeqOrder the new sort seq order
 */
public void setSortSeqOrder(String sortSeqOrder) {
	this.sortSeqOrder = sortSeqOrder;
}

public String getState() {
	return state;
}

public void setState(String state) {
	this.state = state;
}

public String getEvent() {
	return event;
}

public void setEvent(String event) {
	this.event = event;
}

public String getSort() {
	return sort;
}

public void setSort(String sort) {
	this.sort = sort;
}



}
