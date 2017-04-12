package com.bbb.commerce.common;

import java.util.ArrayList;
import java.util.List;

import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

/** @author manohar
 * @story UC_checkout_billing
 * @created 12/2/2011 */
public class BBBAddressImpl extends ContactInfo implements BBBAddress {

    /**
     *
     */
    private static final long serialVersionUID = -2275495136148002381L;

    private String registryInfo;
    private String source;
    private String sourceIdentifier;
    private String id;
    private String identifier;
    private String registryId;
    private String mobileNumber;
    private String countryName;
    private String siteId;
    private String alternatePhoneNumber;
    private boolean beddingKitOrderAddr;
    private boolean webLinkOrderAddr;
    private boolean nonPOBoxAddress;
    private boolean poBoxAddress;
    private boolean qasValidated;

	public boolean isQasValidated() {
		return qasValidated;
	}

	public void setQasValidated(boolean qasValidated) {
		this.qasValidated = qasValidated;
	}

	public boolean isPoBoxAddress() {
		return poBoxAddress;
	}

	public void setPoBoxAddress(boolean isPOBoxAddress) {
		this.poBoxAddress = isPOBoxAddress;
	}


    @Override
    public final String getCountryName() {
        return this.countryName;
    }

    @Override
    public final void setCountryName(final String pCountryName) {
        this.countryName = pCountryName;
    }

    /** @return Site ID */
    public final String getSiteId() {
        if (StringUtils.isEmpty(this.siteId)) {
            this.siteId = SiteContextManager.getCurrentSiteId();
        }
        return this.siteId;
    }

    @Override
    public final String getMobileNumber() {
        return this.mobileNumber;
    }

    @Override
    public final void setMobileNumber(final String pMobileNumber) {
        this.mobileNumber = pMobileNumber;
    }

    /** @return the mIsNonPOBoxAddress */
    @Override
    public final boolean getIsNonPOBoxAddress() {
        return this.nonPOBoxAddress;
    }

    /** @param mIsNonPOBoxAddress the mIsNonPOBoxAddress to set */
    @Override
    public final void setIsNonPOBoxAddress(final boolean mIsNonPOBoxAddress) {
        this.nonPOBoxAddress = mIsNonPOBoxAddress;
    }

    private final boolean mDefault = false;

    @Override
    public final String getRegistryInfo() {
        return this.registryInfo;
    }

    @Override
    public final void setRegistryInfo(final String pRegistryInfo) {
        this.registryInfo = pRegistryInfo;
    }

    @Override
    public final String getIdentifier() {
        return this.identifier;
    }

    /** @param identifier */
    public final void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    @Override
    public final boolean isDefault() {
        return this.mDefault;
    }

    @Override
    public final String getSource() {
        return this.source;
    }

    @Override
    public final void setSource(final String source) {
        this.source = source;
    }

    @Override
    public final String getId() {
        return this.id;
    }

    @Override
    public final void setId(final String id) {
        this.id = id;
    }

    @Override
    public final String getRegistryId() {
        return this.registryId;
    }

    @Override
    public final void setRegistryId(final String registryId) {
        this.registryId = registryId;
    }

    @Override
    public final List<String> getInvalidProperties() {
        final List<String> inValidProperties = new ArrayList <String>();

        boolean isInternationalCard = false;

        if (StringUtils.isBlank(this.getFirstName()) || !BBBUtility.isValidFirstName(this.getFirstName().trim())) {
            inValidProperties.add(BBBCoreConstants.INVALID_FIRSTNAME);
        }
        if (StringUtils.isBlank(this.getLastName()) || !BBBUtility.isValidLastName(this.getLastName().trim())) {
            inValidProperties.add(BBBCoreConstants.INVALID_LASTNAME);
        }
        if (StringUtils.isBlank(this.getAddress1()) || !BBBUtility.isValidAddressLine1(this.getAddress1().trim())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ADDRESS1);
        }
        if (!StringUtils.isBlank(this.getAddress2()) && !BBBUtility.isValidAddressLine2(this.getAddress2().trim())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ADDRESS2);
        }
        if (!StringUtils.isBlank(this.getAddress3()) && !BBBUtility.isValidAddressLine3(this.getAddress3().trim())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ADDRESS3);
        }
        if (StringUtils.isBlank(this.getCity()) || !BBBUtility.isValidCity(this.getCity().trim())) {
            inValidProperties.add(BBBCoreConstants.INVALID_CITY);
        }
        if (StringUtils.isBlank(this.getPostalCode())) {
            inValidProperties.add(BBBCoreConstants.INVALID_ZIPCODE);
        }
        if (StringUtils.isBlank(this.getEmail()) || !BBBUtility.isValidEmail(this.getEmail().trim())) {
            inValidProperties.add(BBBCoreConstants.INVALID_EMAIL);
        }

        if (!StringUtils.isBlank(this.getCountry())
                && !(((this.getCountry().equals("US")) && (this.getSiteId().equals("BedBathUS") || this.getSiteId()
                        .equals("BuyBuyBaby") || this.getSiteId().equals("TBS_BedBathUS")  || this.getSiteId().equals("TBS_BuyBuyBaby") )) || (this.getCountry().equals("CA") && (this.getSiteId().equals(
                        "BedBathCanada") || this.getSiteId().equals("TBS_BedBathCanada"))))) {
            isInternationalCard = true;
        }
        if(!isInternationalCard){
        	if (StringUtils.isBlank(this.getState()) || !BBBUtility.isValidState(this.getState().trim())) {
                inValidProperties.add(BBBCoreConstants.INVALID_STATE);
            }
        }
        if (isInternationalCard) {
            if (StringUtils.isBlank(this.getMobileNumber())
                    || !BBBUtility.isValidInternationalPhoneNumber(this.getMobileNumber().trim())) {
                inValidProperties.add(BBBCoreConstants.INVALID_PHONE);
            }
            if (StringUtils.isBlank(this.getCountryName()) || !BBBUtility.isValidName(this.getCountryName().trim())) {
                inValidProperties.add(BBBCoreConstants.INVALID_COUNTRY_NAME);
            }
            if (StringUtils.isBlank(this.getPostalCode()) || !BBBUtility.isValidInternationalZip(this.getPostalCode().trim())) {
                inValidProperties.add(BBBCoreConstants.INVALID_ZIPCODE);
            }
            if (StringUtils.isBlank(this.getState()) || !BBBUtility.isValidInternationalState(this.getState().trim())) {
                inValidProperties.add(BBBCoreConstants.INVALID_STATE);
            }
        }

        if (!StringUtils.isEmpty(this.getCompanyName()) && !BBBUtility.isValidCompanyName(this.getCompanyName().trim())) {
            inValidProperties.add("Invalid company");

        }
        if("undefined".equalsIgnoreCase(this.getCompanyName())){
        	this.setCompanyName(null);
        }

        return inValidProperties;
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
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBAddressImpl [registryInfo=").append(this.registryInfo).append(", source=")
                        .append(this.source).append(", sourceIdentifier=").append(this.sourceIdentifier)
                        .append(", id=").append(this.id).append(", identifier=").append(this.identifier)
                        .append(", registryId=").append(this.registryId).append(", mobileNumber=")
                        .append(this.mobileNumber).append(", countryName=").append(this.countryName)
                        .append(", siteId=").append(this.siteId).append(", nonPOBoxAddress=")
                        .append(this.nonPOBoxAddress).append(", mDefault=").append(this.mDefault).append(super.toString()).append("]");
        return builder.toString();
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
	public final void setAlternatePhoneNumber(String mAlternatePhoneNumber){
		this.alternatePhoneNumber = mAlternatePhoneNumber;
}
	
	 @Override
    public final String getAlternatePhoneNumber() {
        return this.alternatePhoneNumber;
    }

}
