/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.util.List;

import com.bbb.framework.integration.ServiceRequestBase;



// TODO: Auto-generated Javadoc
/**
 * This class holding Registry VO information properties.
 *
 * @author sku134
 */
public class RegistryVO extends ServiceRequestBase{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The event. */
	private EventVO event;
	
	@Override
	public String toString() {
		return "RegistryVO [event=" + event + ", prefStoreNum=" + prefStoreNum
				+ ", primaryRegistrant=" + primaryRegistrant
				+ ", coRegistrant=" + coRegistrant + ", shipping=" + shipping
				+ ", registryId=" + registryId + ", registryIdWS="
				+ registryIdWS + ", registryType=" + registryType
				+ ", giftPurchased=" + giftPurchased + ", giftRegistered="
				+ giftRegistered + ", displayName=" + displayName
				+ ", userAddressList=" + userAddressList + ", siteId=" + siteId
				+ ", registryToken=" + registryToken + ", serviceName="
				+ serviceName + ", numRegAnnouncementCards="
				+ numRegAnnouncementCards + ", userToken=" + userToken
				+ ", password=" + password + ", networkAffiliation="
				+ networkAffiliation + ", prefRegContMeth=" + prefRegContMeth
				+ ", prefRegContTime=" + prefRegContTime
				+ ", prefCoregContMeth=" + prefCoregContMeth
				+ ", refStoreContactMethod=" + refStoreContactMethod
				+ ", prefCoregContTime=" + prefCoregContTime + ", signup="
				+ signup + ", affiliateOptIn=" + affiliateOptIn
				+ ", optInWeddingOrBump=" + optInWeddingOrBump
				+ ", optInStoreContact=" + optInStoreContact + ", hint=" + hint
				+ ", word=" + word + ", registrantVO=" + registrantVO + ", coRegOwner=" + coRegOwner + "]";
	}
	
	private boolean coRegOwner;
	
	

	public boolean isCoRegOwner() {
		return coRegOwner;
	}

	public void setCoRegOwner(boolean coRegOwner) {
		this.coRegOwner = coRegOwner;
	}

	/** The pref store num. */
	private String prefStoreNum;
	
	/**The affiliate tag */
	private String affiliateTag;
	
	/** The primary registrant. */
	private RegistrantVO primaryRegistrant;
	
	/** The co registrant. */
	private RegistrantVO coRegistrant;
	
	/** The shipping. */
	private ShippingVO shipping;
	
	/** The registry id. */
	private String registryId;
	
	/** The registry id ws. */
	private long registryIdWS;
	
	/** The registry type. */
	private RegistryTypes registryType;
	
	/** The gift purchased. */
	private int giftPurchased;
	
	/** The gift registered. */
	private int giftRegistered;
	
	/** The display name. */
	private String displayName;
	
	/** The user address list. */
	private List<AddressVO> userAddressList;
	
	/** The site id. */
	private String siteId;
	
	/** The registry token. */
	private String registryToken;
	
	/** The service name. */
	private String serviceName;

	/** The num reg announcement cards. */
	private int numRegAnnouncementCards;
	
	/** The user token. */
	private String userToken;
	
	/** The password. */
	private String password;
	
	/** The network affiliation. */
	private String networkAffiliation;
	
	/** The pref reg cont meth. */
	private int prefRegContMeth;
	
	/** The pref reg cont time. */
	private String prefRegContTime;
	
	/** The pref coreg cont meth. */
	private int prefCoregContMeth;
	
	/** The ref store contact method. */
	private String refStoreContactMethod;
	
	/** The pref coreg cont time. */
	private String prefCoregContTime;
	
	/** The signup. */
	private String signup;
	
	/** The affiliate opt in. */
	private String affiliateOptIn;
	
	/** The opt in wedding or bump. */
	private String optInWeddingOrBump;
	
	/** The opt in store contact. */
	private boolean optInStoreContact;
	
	/** The hint. */
	private String hint;
	
	/** The word. */
	private String word;
	
	/** This can refer to primary or coReg. */
	private RegistrantVO registrantVO;
	
	private String status;
	
	private String cookieType;
	
	private String isPublic;
	
	private String regBG;
	
	private String coRegBG;
	
	private boolean create;
	
	public String getRegBG() {
		return regBG;
	}

	public void setRegBG(String regBG) {
		this.regBG = regBG;
	}

	public String getCoRegBG() {
		return coRegBG;
	}

	public void setCoRegBG(String coRegBG) {
		this.coRegBG = coRegBG;
	}	
	
	
	/** This refers to appointment requested **/ 
	//private String  appointmentRequested;
	
	
	/**
	 * Gets the ref store contact method.
	 *
	 * @return the ref store contact method
	 */
	public String getRefStoreContactMethod() {
		return refStoreContactMethod;
	}
	
	/**
	 * Gets the Appointment Requested method.
	 *
	 * @return the appointmentRequested
	 */
	/*public String getAppointmentRequested() {
		return appointmentRequested;
	}
	*/
	/**
	 * Sets the Appointment Requested .
	 *
	 * @param appointmentRequested 
	 */
	/*public void setAppointmentRequested(String appointmentRequested) {
		this.appointmentRequested = appointmentRequested;
	}*/

	/**
	 * Sets the ref store contact method.
	 *
	 * @param refStoreContactMethod the new ref store contact method
	 */
	public void setRefStoreContactMethod(String refStoreContactMethod) {
		this.refStoreContactMethod = refStoreContactMethod;
	}

	/**
	 * Gets the pref store num.
	 *
	 * @return the pref store num
	 */
	public String getPrefStoreNum() {
		return prefStoreNum;
	}

	/**
	 * Sets the pref store num.
	 *
	 * @param prefStoreNum the new pref store num
	 */
	public void setPrefStoreNum(final String prefStoreNum) {
		this.prefStoreNum = prefStoreNum;
	}
	
	/**Gets the affiliate Tag for XO Users
	 * 
	 * @return affiliateTag
	 */
	public String getAffiliateTag() {
		return affiliateTag;
	}
	

	/**Sets the affiliate Tag for XO Users
	 * 
	 * @param affiliateTag
	 */
	public void setAffiliateTag(String affiliateTag) {
		this.affiliateTag = affiliateTag;
	}


	/**
	 * Gets the user token.
	 *
	 * @return the userToken
	 */	
	public String getUserToken() {
		return userToken;
	}

	/**
	 * Sets the user token.
	 *
	 * @param userToken the userToken to set
	 */	
	public void setUserToken(final String userToken) {
		this.userToken = userToken;
	}


	/**
	 * Gets the event.
	 *
	 * @return the event
	 */
	public EventVO getEvent() {
		if(event != null){
			return event;
		}else{
			event= new EventVO();
		return event;
		}		
	}


	/**
	 * Sets the event.
	 *
	 * @param event the event to set
	 */
	public void setEvent(final EventVO event) {
		this.event = event;
	}


	/**
	 * Gets the primary registrant.
	 *
	 * @return the primaryRegistrant
	 */
	public RegistrantVO getPrimaryRegistrant() {
		if (primaryRegistrant != null){
			return primaryRegistrant;
		} else {
			primaryRegistrant = new RegistrantVO();
			return primaryRegistrant;
		}
		
	}


	/**
	 * Sets the primary registrant.
	 *
	 * @param primaryRegistrant the primaryRegistrant to set
	 */
	public void setPrimaryRegistrant(final RegistrantVO primaryRegistrant) {
		this.primaryRegistrant = primaryRegistrant;
	}


	/**
	 * Gets the co registrant.
	 *
	 * @return the coRegistrant
	 */
	public RegistrantVO getCoRegistrant() {
		
		if (coRegistrant != null){
			return coRegistrant;
		} else {
			coRegistrant = new RegistrantVO();
			return coRegistrant;
		}
	}


	/**
	 * Sets the co registrant.
	 *
	 * @param coRegistrant the coRegistrant to set
	 */
	public void setCoRegistrant(final RegistrantVO coRegistrant) {
		this.coRegistrant = coRegistrant;
	}


	/**
	 * Gets the shipping.
	 *
	 * @return the shipping
	 */
	public ShippingVO getShipping() {
		if (shipping == null){
			shipping = new ShippingVO();
			return shipping;
			
		} else {
			return shipping;
		}

	}


	/**
	 * Sets the shipping.
	 *
	 * @param shipping the shipping to set
	 */
	public void setShipping(final ShippingVO shipping) {
		this.shipping = shipping;
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
	public void setRegistryId(final String pRegistryId) {
		this.registryId = pRegistryId;
	}


	/**
	 * Gets the registry type.
	 *
	 * @return the registryType
	 */
	public RegistryTypes getRegistryType() {
		if (registryType == null){
			registryType = new RegistryTypes();
			return registryType;
			
		} else {
			return registryType;
		}

	}


	/**
	 * Sets the registry type.
	 *
	 * @param registryType the registryType to set
	 */
	public void setRegistryType(final RegistryTypes registryType) {
		this.registryType = registryType;
	}


	/**
	 * Gets the gift purchased.
	 *
	 * @return the giftPurchased
	 */
	public int getGiftPurchased() {
		return giftPurchased;
	}


	/**
	 * Sets the gift purchased.
	 *
	 * @param giftPurchased the giftPurchased to set
	 */
	public void setGiftPurchased(final int giftPurchased) {
		this.giftPurchased = giftPurchased;
	}


	/**
	 * Gets the gift registered.
	 *
	 * @return the giftRegistered
	 */
	public int getGiftRegistered() {
		return giftRegistered;
	}


	/**
	 * Sets the gift registered.
	 *
	 * @param giftRegistered the giftRegistered to set
	 */
	public void setGiftRegistered(final int giftRegistered) {
		this.giftRegistered = giftRegistered;
	}


	/**
	 * Gets the display name.
	 *
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}


	/**
	 * Sets the display name.
	 *
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}


	
	/**
	 * Gets the user address list.
	 *
	 * @return the user address list
	 */
	public List<AddressVO> getUserAddressList() {
		return userAddressList;
	}

	/**
	 * Sets the user address list.
	 *
	 * @param userAddressList the new user address list
	 */
	public void setUserAddressList(List<AddressVO> userAddressList) {
		this.userAddressList = userAddressList;
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
	public void setSiteId(final String siteId) {
		this.siteId = siteId;
	}


	/**
	 * Gets the registry token.
	 *
	 * @return the registryToken
	 */
	public String getRegistryToken() {
		return registryToken;
	}


	/**
	 * Sets the registry token.
	 *
	 * @param registryToken the registryToken to set
	 */
	public void setRegistryToken(final String registryToken) {
		this.registryToken = registryToken;
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
public void setServiceName(final String pServiceName) {
	serviceName = pServiceName;
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
	 * Gets the num reg announcement cards.
	 *
	 * @return the numRegAnnouncementCards
	 */
	public int getNumRegAnnouncementCards() {
		return numRegAnnouncementCards;
	}
	
	/**
	 * Sets the num reg announcement cards.
	 *
	 * @param numRegAnnouncementCards the numRegAnnouncementCards to set
	 */
	public void setNumRegAnnouncementCards(final int numRegAnnouncementCards) {
		this.numRegAnnouncementCards = numRegAnnouncementCards;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param pPassword the password to set
	 */
	public void setPassword(final String pPassword) {
		password = pPassword;
	}

	/**
	 * Gets the network affiliation.
	 *
	 * @return the network affiliation
	 */
	public String getNetworkAffiliation() {
		return networkAffiliation;
	}

	/**
	 * Sets the network affiliation.
	 *
	 * @param networkAffiliation the new network affiliation
	 */
	public void setNetworkAffiliation(String networkAffiliation) {
		this.networkAffiliation = networkAffiliation;
	}

	/**
	 * Gets the pref reg cont meth.
	 *
	 * @return the pref reg cont meth
	 */
	public int getPrefRegContMeth() {
		return prefRegContMeth;
	}

	/**
	 * Sets the pref reg cont meth.
	 *
	 * @param prefRegContMeth the new pref reg cont meth
	 */
	public void setPrefRegContMeth(int prefRegContMeth) {
		this.prefRegContMeth = prefRegContMeth;
	}

	/**
	 * Gets the pref reg cont time.
	 *
	 * @return the pref reg cont time
	 */
	public String getPrefRegContTime() {
		return prefRegContTime;
	}

	/**
	 * Sets the pref reg cont time.
	 *
	 * @param prefRegContTime the new pref reg cont time
	 */
	public void setPrefRegContTime(String prefRegContTime) {
		this.prefRegContTime = prefRegContTime;
	}

	/**
	 * Gets the pref coreg cont meth.
	 *
	 * @return the pref coreg cont meth
	 */
	public int getPrefCoregContMeth() {
		return prefCoregContMeth;
	}

	/**
	 * Sets the pref coreg cont meth.
	 *
	 * @param prefCoregContMeth the new pref coreg cont meth
	 */
	public void setPrefCoregContMeth(int prefCoregContMeth) {
		this.prefCoregContMeth = prefCoregContMeth;
	}

	/**
	 * Gets the pref coreg cont time.
	 *
	 * @return the pref coreg cont time
	 */
	public String getPrefCoregContTime() {
		return prefCoregContTime;
	}

	/**
	 * Sets the pref coreg cont time.
	 *
	 * @param prefCoregContTime the new pref coreg cont time
	 */
	public void setPrefCoregContTime(String prefCoregContTime) {
		this.prefCoregContTime = prefCoregContTime;
	}

	/**
	 * Gets the signup.
	 *
	 * @return the signup
	 */
	public String getSignup() {
		return signup;
	}

	/**
	 * Sets the signup.
	 *
	 * @param signup the new signup
	 */
	public void setSignup(String signup) {
		this.signup = signup;
	}

	/**
	 * Gets the hint.
	 *
	 * @return the hint
	 */
	public String getHint() {
		return hint;
	}

	/**
	 * Sets the hint.
	 *
	 * @param hint the new hint
	 */
	public void setHint(String hint) {
		this.hint = hint;
	}

	/**
	 * Gets the word.
	 *
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Sets the word.
	 *
	 * @param word the new word
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * Gets the registry id ws.
	 *
	 * @return the registry id ws
	 */
	public long getRegistryIdWS() {
		return registryIdWS;
	}

	/**
	 * Sets the registry id ws.
	 *
	 * @param registryIdWS the new registry id ws
	 */
	public void setRegistryIdWS(long registryIdWS) {
		this.registryIdWS = registryIdWS;
	}

	/**
	 * Getter for affiliateOptIn.
	 *
	 * @return the affiliate opt in
	 */	
	public String getAffiliateOptIn() {
		return affiliateOptIn;
	}

	/**
	 * Sets the affiliate opt in.
	 *
	 * @param affiliateOptIn the affiliateOptIn to set
	 */	
	public void setAffiliateOptIn(String affiliateOptIn) {
		this.affiliateOptIn = affiliateOptIn;
	}

	/**
	 * Gets the opt in wedding or bump.
	 *
	 * @return the opt in wedding or bump
	 */
	public String getOptInWeddingOrBump() {
		return optInWeddingOrBump;
	}

	/**
	 * Sets the opt in wedding or bump.
	 *
	 * @param optInWeddingOrBump the new opt in wedding or bump
	 */
	public void setOptInWeddingOrBump(String optInWeddingOrBump) {
		this.optInWeddingOrBump = optInWeddingOrBump;
	}

	/**
	 * Checks if is opt in store contact.
	 *
	 * @return the optInStoreContact
	 */
	public boolean isOptInStoreContact() {
		return optInStoreContact;
	}

	/**
	 * Sets the opt in store contact.
	 *
	 * @param optInStoreContact the optInStoreContact to set
	 */
	public void setOptInStoreContact(boolean optInStoreContact) {
		this.optInStoreContact = optInStoreContact;
	}

	/**
	 * Gets the registrant vo.
	 *
	 * @return the registrantVO
	 */	
	public RegistrantVO getRegistrantVO() {
		
		if (this.registrantVO != null){
			return registrantVO;
		} else {
			registrantVO = new RegistrantVO();
			return registrantVO;
		}
	}
	
	/**
	 * Sets the registrant vo.
	 *
	 * @param registrantVO the registrantVO to set
	 */
	public void setRegistrantVO(RegistrantVO registrantVO) {
		this.registrantVO = registrantVO;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the cookieType
	 */
	public String getCookieType() {
		return cookieType;
	}

	/**
	 * @param cookieType the cookieType to set
	 */
	public void setCookieType(String cookieType) {
		this.cookieType = cookieType;
	}

	 
	/**
	 * @return the isPublic
	 */
	public String getIsPublic() {
		return isPublic;
	}

	/**
	 * @param isPublic the isPublic to set
	 */
	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}
	

}
