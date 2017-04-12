package com.bbb.commerce.common;

import java.io.Serializable;
import java.util.List;

/** @author manohar */
public interface BBBAddress extends Serializable {

    /** @return Address Prefix */
    public String getPrefix();

    /** @param pPrefix */
    public void setPrefix(String pPrefix);
    
  //updating code for QAS PO box validation
    public boolean isPoBoxAddress();

	public void setPoBoxAddress(boolean poBoxAddress);
	
	public boolean isQasValidated();

	public void setQasValidated(boolean qasValidated);
	

    /** @return First Name */
    public String getFirstName();

    /** @param pFirstName */
    public void setFirstName(String pFirstName);

    /** @return */
    public String getMiddleName();

    /** @param pMiddleName */
    public void setMiddleName(String pMiddleName);

    /** @return */
    public String getLastName();

    /** @param pLastName */
    public void setLastName(String pLastName);

    /** @return */
    public String getSuffix();

    /** @param pSuffix */
    public void setSuffix(String pSuffix);

    /** @return */
    public String getJobTitle();;

    /** @param pJobTitle */
    public void setJobTitle(String pJobTitle);

    /** @return */
    public String getCompanyName();

    /** @param pCompanyName */
    public void setCompanyName(String pCompanyName);

    /** @return */
    public String getAddress1();

    /** @param pAddress1 */
    public void setAddress1(String pAddress1);

    /** @return */
    public String getAddress2();

    /** @param pAddress2 */
    public void setAddress2(String pAddress2);

    /** @return */
    public String getAddress3();

    /** @param pAddress3 */
    public void setAddress3(String pAddress3);

    /** @return */
    public String getCity();

    /** @param pCity */
    public void setCity(String pCity);

    /** @return */
    public String getState();

    /** @param pState */
    public void setState(String pState);

    /** @return */
    public String getCounty();

    /** @param pCounty */
    public void setCounty(String pCounty);

    /** @return */
    public String getPostalCode();

    /** @param pPostalCode */
    public void setPostalCode(String pPostalCode);

    /** @return */
    public String getCountry();

    /** @param pCountry */
    public void setCountry(String pCountry);

    /** @return */
    public String getCountryName();

    /** @param pCountryName */
    public void setCountryName(String pCountryName);

    /** @return */
    public String getPhoneNumber();

    /** @param pPhoneNumber */
    public void setPhoneNumber(String pPhoneNumber);

    /** @return */
    public String getFaxNumber();

    /** @param pFaxNumber */
    public void setFaxNumber(String pFaxNumber);

    /** @return */
    public String getEmail();

    /** @param pEmail */
    public void setEmail(String pEmail);

    /** @return */
    public String getOwnerId();

    /** @param pOwnerId */
    public void setOwnerId(String pOwnerId);

    /** @return */
    public String getRegistryInfo();

    /** @param pRegistryInfo */
    public void setRegistryInfo(String pRegistryInfo);

    /** @return */
    public String getIdentifier();

    /** @return */
    public boolean isDefault();

    /** @return */
    public String getSource();

    /** @param source */
    public void setSource(String source);

    /** @return */
    public String getSourceIdentifier();

    /** @param source */
    public void setSourceIdentifier(String sourceIdentifier);

    /** @return */
    public String getId();

    /** @param Id */
    public void setId(String Id);

    /** @return */
    public String getRegistryId();

    /** @param registryId */
    public void setRegistryId(String registryId);

    /** @return */
    public List <String> getInvalidProperties();
    
   // public List <String> getSPInvalidProperties();
    
    /** @return */
    public boolean getIsNonPOBoxAddress();

    /** @param mIsNonPOBoxAddress */
    public void setIsNonPOBoxAddress(boolean mIsNonPOBoxAddress);

    public String getMobileNumber();

    public void setMobileNumber(String pMobileNumber);
    
    /** @return */
    public boolean getIsWebLinkOrderAddr();

    /** @param mIsWeblinkOrderAddr */
    public void setIsWebLinkOrderAddr(boolean mIsWeblinkOrderAddr);
    
    /** @return */
    public boolean getIsBeddingKitOrderAddr();

    /** @param mIsBeddingKitOrderAddr */
    public void setIsBeddingKitOrderAddr(boolean mIsBeddingKitOrderAddr);
    
    /**
     * @param Alternate Phone Number
     */
    public void setAlternatePhoneNumber(String mAlternatePhoneNumber);
    
    /**
     * @return Alternate Phone Number
     */
    public String getAlternatePhoneNumber();
}
