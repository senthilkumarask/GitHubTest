package com.bbb.commerce.common;

import java.util.ArrayList;
import java.util.List;

import atg.commerce.order.PropertyNameConstants;
import atg.commerce.order.RepositoryContactInfo;
import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBPropertyNameConstants;
import com.bbb.utils.BBBUtility;

/**
 * @author manohar
 */
public class BBBRepositoryContactInfo extends RepositoryContactInfo implements BBBAddress {

    private static final String BBB_REPOSITORY_CONTACT_INFO = "BBBRepositoryContactInfo";

    private static final long serialVersionUID = 5976654444038692683L;
    private String source;
    private String sourceIdentifier;
    //private String alternatePhoneNumber;
    private boolean beddingKitOrderAddr;
    private boolean webLinkOrderAddr;
    private boolean nonPOBoxAddress;
    private boolean poBoxAddress;
    private boolean qasValidated;

    /**
     * @param repoItem
     */
    public BBBRepositoryContactInfo(final MutableRepositoryItem repoItem) {
        super(repoItem);
    }

    /**
     *
     */
    public BBBRepositoryContactInfo() {
        super();
    }

    @Override
    public final String getSource() {
        return this.source;
    }

    @Override
    public final void setSource(final String pSource) {
        this.source = pSource;
    }

    @Override
    public final String getRegistryInfo() {
        return (String) this.getPropertyValue(BBBPropertyNameConstants.REGISTRY_INFO);
    }

    @Override
    public final void setRegistryInfo(final String pRegistryInfo) {
        this.setPropertyValue(BBBPropertyNameConstants.REGISTRY_INFO, pRegistryInfo);
    }

    @Override
    public final String getIdentifier() {
        if (null == this.getSource()) {
            return this.getId();
        }
        return this.getSource() + this.getId();
    }

    @Override
    public final boolean isDefault() {
        return false;
    }

    @Override
    public final String getId() {
        return this.mRepositoryItem.getRepositoryId();
    }

    @Override
    public void setId(final String Id){
        // FIXME
    }

    @Override
    public final String getRegistryId() {
        return (String) this.getPropertyValue(BBBPropertyNameConstants.REGISTRY_ID);
    }

    @Override
    public final void setRegistryId(final String pRegistryId) {
        this.setPropertyValue(BBBPropertyNameConstants.REGISTRY_ID, pRegistryId);
    }

    private Object getPropertyValue(final String pPropertyName) {
        try {
            if (this.mRepositoryItem.getItemDescriptor().hasProperty(pPropertyName)) {
                return this.mRepositoryItem.getPropertyValue(pPropertyName);
            }
        } catch (final IllegalArgumentException e) {
            Nucleus.getGlobalNucleus().logError(e.getMessage() + BBB_REPOSITORY_CONTACT_INFO);
        } catch (final RepositoryException e) {
            Nucleus.getGlobalNucleus().logError(e.getMessage() + BBB_REPOSITORY_CONTACT_INFO);
        }
        return null;
    }

    private void setPropertyValue(final String pPropertyName, final Object pPropertyValue) {
        try {
            if (this.mRepositoryItem.getItemDescriptor().hasProperty(pPropertyName)) {
                this.mRepositoryItem.setPropertyValue(pPropertyName, pPropertyValue);
            }
        } catch (final IllegalArgumentException e) {
            Nucleus.getGlobalNucleus().logError(e.getMessage() + BBB_REPOSITORY_CONTACT_INFO);
        } catch (final RepositoryException e) {
            Nucleus.getGlobalNucleus().logError(e.getMessage() + BBB_REPOSITORY_CONTACT_INFO);
        }
    }

    @Override
    public String getState() {
        return (String) this.mRepositoryItem.getPropertyValue(PropertyNameConstants.STATE);
    }

    @Override
    public final void setState(final String pState) {
        this.mRepositoryItem.setPropertyValue(PropertyNameConstants.STATE, pState);
    }

    @Override
    public final List<String> getInvalidProperties() {
        final List<String> inValidProperties = new ArrayList<String>();

        if (StringUtils.isBlank(this.getFirstName()) || !BBBUtility.isValidFirstName(this.getFirstName())) {
            inValidProperties.add(BBBCoreConstants.INVALID_FIRSTNAME);
        }
        if (StringUtils.isBlank(this.getLastName()) || !BBBUtility.isValidLastName(this.getLastName())) {
            inValidProperties.add(BBBCoreConstants.INVALID_LASTNAME);
        }
        if (StringUtils.isBlank(this.getAddress1()) || !BBBUtility.isValidAddressLine1(this.getAddress1())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ADDRESS1);
        }
        if (!StringUtils.isBlank(this.getAddress2()) && !BBBUtility.isValidAddressLine2(this.getAddress2())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ADDRESS2);
        }
        if (!StringUtils.isBlank(this.getAddress3()) && !BBBUtility.isValidAddressLine3(this.getAddress3())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ADDRESS3);
        }
        if (StringUtils.isBlank(this.getCity()) || !BBBUtility.isValidCity(this.getCity())) {
            inValidProperties.add(BBBCoreConstants.INVALID_CITY);
        }
        if (StringUtils.isBlank(this.getPostalCode())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ZIPCODE);
        }
        if (StringUtils.isBlank(this.getEmail()) || !BBBUtility.isValidEmail(this.getEmail())) {
            inValidProperties.add(BBBCoreConstants.INVALID_EMAIL);
        }
        if (StringUtils.isBlank(this.getMobileNumber()) || !BBBUtility.isValidPhoneNumber(this.getMobileNumber())) {
            inValidProperties.add(BBBCoreConstants.INVALID_PHONE);
        }
        return inValidProperties;
    }
    
    public final List<String> getSPInvalidProperties() {
        final List<String> inValidProperties = new ArrayList<String>();

        if (StringUtils.isBlank(this.getFirstName()) || !BBBUtility.isValidFirstName(this.getFirstName())) {
            inValidProperties.add(BBBCoreConstants.INVALID_FIRSTNAME);
        }
        if (StringUtils.isBlank(this.getLastName()) || !BBBUtility.isValidLastName(this.getLastName())) {
            inValidProperties.add(BBBCoreConstants.INVALID_LASTNAME);
        }
        if (StringUtils.isBlank(this.getAddress1()) || !BBBUtility.isValidAddressLine1(this.getAddress1())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ADDRESS1);
        }
        if (!StringUtils.isBlank(this.getAddress2()) && !BBBUtility.isValidAddressLine2(this.getAddress2())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ADDRESS2);
        }
        if (StringUtils.isBlank(this.getCity()) || !BBBUtility.isValidCity(this.getCity())) {
            inValidProperties.add(BBBCoreConstants.INVALID_CITY);
        }
        if (StringUtils.isBlank(this.getPostalCode())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ZIPCODE);
        }
        if (StringUtils.isBlank(this.getMobileNumber()) || !BBBUtility.isValidPhoneNumber(this.getMobileNumber())) {
            inValidProperties.add(BBBCoreConstants.INVALID_PHONE);
        }
        return inValidProperties;
    }

    /**
     * @return the mIsNonPOBoxAddress
     */
    @Override
    public final boolean getIsNonPOBoxAddress() {
        return this.nonPOBoxAddress;
    }

    /**
     * @param mIsNonPOBoxAddress
     *            the mIsNonPOBoxAddress to set
     */
    @Override
    public final void setIsNonPOBoxAddress(final boolean mIsNonPOBoxAddress) {
        this.nonPOBoxAddress = mIsNonPOBoxAddress;
    }

    @Override
    public final String getSourceIdentifier() {

        return this.sourceIdentifier;
    }

    @Override
    public final void setSourceIdentifier(final String pSourceIdentifier) {

        this.sourceIdentifier = pSourceIdentifier;
    }

    @Override
    public String getMobileNumber() {
        return (String) this.getPropertyValue(BBBPropertyNameConstants.MOBILE_NUMBER);
    }

    @Override
    public  void setMobileNumber(final String pMobileNumber) {
        this.setPropertyValue(BBBPropertyNameConstants.MOBILE_NUMBER, pMobileNumber);
    }

    @Override
    public final String getCountryName() {
        return (String) this.getPropertyValue(BBBPropertyNameConstants.COUNTRY_NAME);
    }

    @Override
    public final void setCountryName(final String pCountryName) {
        this.setPropertyValue(BBBPropertyNameConstants.COUNTRY_NAME, pCountryName);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBRepositoryContactInfo [source=").append(this.source).append(", sourceIdentifier=")
                        .append(this.sourceIdentifier).append(", nonPOBoxAddress=").append(this.nonPOBoxAddress)
                        .append("]");
        return builder.toString();
    }
    
   
    /**
     * return if the address is of paypal
     * @return
     */
    public  boolean getIsFromPaypal() {
    	if (this.getPropertyValue("isFromPaypal") != null) {
        return ((Boolean) this.getPropertyValue("isFromPaypal")).booleanValue();
    	}
    	return false;
    }

 
	/** 
	 * set flag if the address is of paypal
	 * @param isFromPaypal
	 */
	public final void setFromPaypal(final boolean isFromPaypal) {
        this.setPropertyValue("isFromPaypal", isFromPaypal);
    }
	
	@Override
	public boolean getIsWebLinkOrderAddr() {
		return this.webLinkOrderAddr;
	}

	@Override
	public void setIsWebLinkOrderAddr(boolean mIsWeblinkOrderAddr) {
		this.webLinkOrderAddr = mIsWeblinkOrderAddr;
	}

	@Override
	public boolean getIsBeddingKitOrderAddr() {
		return this.beddingKitOrderAddr;
	}

	@Override
	public void setIsBeddingKitOrderAddr(boolean mIsBeddingKitOrderAddr) {
		this.beddingKitOrderAddr = mIsBeddingKitOrderAddr;
	}
	
	@Override
    public String getAlternatePhoneNumber() {
        return (String) this.getPropertyValue(BBBPropertyNameConstants.ALTERNATE_PHONE_NUMBER);
}

    @Override
    public final void setAlternatePhoneNumber(final String pAlternatePhoneNumber) {
        this.setPropertyValue(BBBPropertyNameConstants.ALTERNATE_PHONE_NUMBER, pAlternatePhoneNumber);
    }
    
    //getter setters for QAS
    
    public boolean isPoBoxAddress() {
    	if (this.getPropertyValue("poBoxAddress") != null) {
        return ((Boolean) this.getPropertyValue("poBoxAddress")).booleanValue();
    	}
    	return false;
    }

	
	public final void setPoBoxAddress(final boolean poBoxAddress) {
        this.setPropertyValue("poBoxAddress", poBoxAddress);
    }
	
	public boolean isQasValidated() {
    	if (this.getPropertyValue("qasValidated") != null) {
        return ((Boolean) this.getPropertyValue("qasValidated")).booleanValue();
    	}
    	return false;
    }

	
	public final void setQasValidated(final boolean qasValidated) {
        this.setPropertyValue("qasValidated", qasValidated);
    }
	
	 /**
     * return if the address is of paypal
     * @return
     */
    public boolean isFromPaypal() {
    	if (this.getPropertyValue("isFromPaypal") != null) {
        return ((Boolean) this.getPropertyValue("isFromPaypal")).booleanValue();
    	}
    	return false;
    }

 
	
}
