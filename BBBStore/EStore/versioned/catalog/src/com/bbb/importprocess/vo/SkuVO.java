package com.bbb.importprocess.vo;

public class SkuVO {
  
  //Frequent 
  private String mShippingSurcharge;
  private String mCAShippingSurcharge;
  private String mBABShippingSurcharge;
  
  ///////////////////  Rare //////////////////////
  private String mColor; 
  private String mColorGroup; 
  private String mCost;
  private String mCaCost;
  private String mSize;
 
  private String mJdaDeptId; 
  private String mJdaSubDeptid;
  private String mJdaClass;
  private String mTaxCd; 
  
  private String mGiftCertfFag; 
  private String mShopGuideId; 
  private String mSkuCollegeId; 
  private String mSkuTypeCd; 
 
  private String mProp65Lighting; 
  private String mProp65Crystal; 
  private String mProp65DinnerWare; 
  private String mProp65Other; 
  private String mGiftWrapEligible; 
  private String mUpc; 
  private String mBrandId; 
  private String mEligibleShipMethods; 
  private String mFreeShippingMethods; 
  private String mCaFreeShippingMethods; 
  private String mBabFreeShippingMethods; 
  private String mNonShippableStates; 
  private String mVDCSkuType; 
  private String mVDCSkuMessage; 
  private String mCAEcomFulfilmentFlag; 
  private String mOverWeightFlag; 
  private String mOverSizeFlag; 
  private String mBbbForceBelowLine; 
  private String mBabForceBelowLine; 
  private String mCaForceBelowLine;
  private String mBopus;
  private String mClearance;
  private String mVendorId;
  //Start : LTL-33(PIM feed processing changes for SKUs)
  private String mAssemblyTime; 
  private String mAssemblyOffered; 
  private String mLtlFlag; 
  private String mOrderToShipSla; 
  private String mCaseWeight;  
  private String mIntlRestricted;
  
  //EXIM or Katori BPSI-420
  private String mCustomizationOfferedFlag;
  private String mBabCustomizationOfferedFlag;
  private String mCaCustomizationOfferedFlag;
  private String mGsCustomizationOfferedFlag;
  private String mPersonalizationType;
  private String mEligibleCustomizationCodes;
  private String mMinShippingDays;
  private String mMaxShippingDays;
  private String mShippingCutoffOffset;
  
  //  PIM Feed Import BBBP-6233 Changes start
  private Character chainSkuStatusCd, webSkuStatusCd, caChainSkuStatusCd, caWebSkuStatusCd;
  
  public Character getChainSkuStatusCd() {
	  return chainSkuStatusCd;
  }
  public void setChainSkuStatusCd(Character chainSkuStatusCd) {
	  this.chainSkuStatusCd = chainSkuStatusCd;
  }
  public Character getWebSkuStatusCd() {
	  return webSkuStatusCd;
  }
  public void setWebSkuStatusCd(Character webSkuStatusCd) {
	  this.webSkuStatusCd = webSkuStatusCd;
  }
  public Character getCaChainSkuStatusCd() {
	  return caChainSkuStatusCd;
  }
  public void setCaChainSkuStatusCd(Character caChainSkuStatusCd) {
	  this.caChainSkuStatusCd = caChainSkuStatusCd;
  }
  public Character getCaWebSkuStatusCd() {
	  return caWebSkuStatusCd;
  }
  public void setCaWebSkuStatusCd(Character caWebSkuStatusCd) {
	  this.caWebSkuStatusCd = caWebSkuStatusCd;
  }
//PIM Feed Import BBBP-6233 changes end
/**
   * @return the shippingSurcharge
   */
  public String getShippingSurcharge() {
    return mShippingSurcharge;
  }
  /**
   * @param pShippingSurcharge the shippingSurcharge to set
   */
  public void setShippingSurcharge(String pShippingSurcharge) {
    mShippingSurcharge = pShippingSurcharge;
  }
  /**
   * @return the cAShippingSurcharge
   */
  public String getCAShippingSurcharge() {
    return mCAShippingSurcharge;
  }
  /**
   * @param pCAShippingSurcharge the cAShippingSurcharge to set
   */
  public void setCAShippingSurcharge(String pCAShippingSurcharge) {
    mCAShippingSurcharge = pCAShippingSurcharge;
  }
  /**
   * @return the bABShippingSurcharge
   */
  public String getBABShippingSurcharge() {
    return mBABShippingSurcharge;
  }
  /**
   * @param pBABShippingSurcharge the bABShippingSurcharge to set
   */
  public void setBABShippingSurcharge(String pBABShippingSurcharge) {
    mBABShippingSurcharge = pBABShippingSurcharge;
  }
  /**
   * @return the color
   */
  public String getColor() {
    return mColor;
  }
  /**
   * @param pColor the color to set
   */
  public void setColor(String pColor) {
    mColor = pColor;
  }
  /**
   * @return the colorgroup
   */
  public String getColorGroup() {
    return mColorGroup;
  }
  /**
   * @param pColorGroup the colorgroup to set
   */
  public void setColorGroup(String pColorGroup) {
    mColorGroup = pColorGroup;
  }
  /**
   * @return the cost
   */
  public String getCost() {
    return mCost;
  }
  /**
   * @param pCost the cost to set
   */
  public void setCost(String pCost) {
    mCost = pCost;
  }
  /**
   * @return the caCost
   */
  public String getCaCost() {
    return mCaCost;
  }
  /**
   * @param pCaCost the caCost to set
   */
  public void setCaCost(String pCaCost) {
    mCaCost = pCaCost;
  }
  /**
   * @return the size
   */
  public String getSize() {
    return mSize;
  }
  /**
   * @param pSize the size to set
   */
  public void setSize(String pSize) {
    mSize = pSize;
  }
  /**
   * @return the jdaDeptId
   */
  public String getJdaDeptId() {
    return mJdaDeptId;
  }
  /**
   * @param pJdaDeptId the jdaDeptId to set
   */
  public void setJdaDeptId(String pJdaDeptId) {
    mJdaDeptId = pJdaDeptId;
  }
  /**
   * @return the jdaSubDeptid
   */
  public String getJdaSubDeptid() {
    return mJdaSubDeptid;
  }
  /**
   * @param pJdaSubDeptid the jdaSubDeptid to set
   */
  public void setJdaSubDeptid(String pJdaSubDeptid) {
    mJdaSubDeptid = pJdaSubDeptid;
  }
  /**
   * @return the giftCertfFag
   */
  public String getGiftCertfFag() {
    return mGiftCertfFag;
  }
  /**
   * @param pGiftCertfFag the giftCertfFag to set
   */
  public void setGiftCertfFag(String pGiftCertfFag) {
    mGiftCertfFag = pGiftCertfFag;
  }
  /**
   * @return the shopGuideId
   */
  public String getShopGuideId() {
    return mShopGuideId;
  }
  /**
   * @param pShopGuideId the shopGuideId to set
   */
  public void setShopGuideId(String pShopGuideId) {
    mShopGuideId = pShopGuideId;
  }
  /**
   * @return the skuCollegeId
   */
  public String getSkuCollegeId() {
    return mSkuCollegeId;
  }
  /**
   * @param pSkuCollegeId the skuCollegeId to set
   */
  public void setSkuCollegeId(String pSkuCollegeId) {
    mSkuCollegeId = pSkuCollegeId;
  }
  /**
   * @return the skuTypeCd
   */
  public String getSkuTypeCd() {
    return mSkuTypeCd;
  }
  /**
   * @param pSkuTypeCd the skuTypeCd to set
   */
  public void setSkuTypeCd(String pSkuTypeCd) {
    mSkuTypeCd = pSkuTypeCd;
  }
  /**
   * @return the prop65Lighting
   */
  public String getProp65Lighting() {
    return mProp65Lighting;
  }
  /**
   * @param pProp65Lighting the prop65Lighting to set
   */
  public void setProp65Lighting(String pProp65Lighting) {
    mProp65Lighting = pProp65Lighting;
  }
  /**
   * @return the prop65Crystal
   */
  public String getProp65Crystal() {
    return mProp65Crystal;
  }
  /**
   * @param pProp65Crystal the prop65Crystal to set
   */
  public void setProp65Crystal(String pProp65Crystal) {
    mProp65Crystal = pProp65Crystal;
  }
  /**
   * @return the prop65DinnerWare
   */
  public String getProp65DinnerWare() {
    return mProp65DinnerWare;
  }
  /**
   * @param pProp65DinnerWare the prop65DinnerWare to set
   */
  public void setProp65DinnerWare(String pProp65DinnerWare) {
    mProp65DinnerWare = pProp65DinnerWare;
  }
  /**
   * @return the prop65Other
   */
  public String getProp65Other() {
    return mProp65Other;
  }
  /**
   * @param pProp65Other the prop65Other to set
   */
  public void setProp65Other(String pProp65Other) {
    mProp65Other = pProp65Other;
  }
  /**
   * @return the giftWrapEligible
   */
  public String getGiftWrapEligible() {
    return mGiftWrapEligible;
  }
  /**
   * @param pGiftWrapEligible the giftWrapEligible to set
   */
  public void setGiftWrapEligible(String pGiftWrapEligible) {
    mGiftWrapEligible = pGiftWrapEligible;
  }
  /**
   * @return the upc
   */
  public String getUpc() {
    return mUpc;
  }
  /**
   * @param pUpc the upc to set
   */
  public void setUpc(String pUpc) {
    mUpc = pUpc;
  }
  /**
   * @return the brandId
   */
  public String getBrandId() {
    return mBrandId;
  }
  /**
   * @param pBrandId the brandId to set
   */
  public void setBrandId(String pBrandId) {
    mBrandId = pBrandId;
  }
  /**
   * @return the eligibleShipMethods
   */
  public String getEligibleShipMethods() {
    return mEligibleShipMethods;
  }
  /**
   * @param pEligibleShipMethods the eligibleShipMethods to set
   */
  public void setEligibleShipMethods(String pEligibleShipMethods) {
    mEligibleShipMethods = pEligibleShipMethods;
  }
  /**
   * @return the freeShippingMethods
   */
  public String getFreeShippingMethods() {
    return mFreeShippingMethods;
  }
  /**
   * @param pFreeShippingMethods the freeShippingMethods to set
   */
  public void setFreeShippingMethods(String pFreeShippingMethods) {
    mFreeShippingMethods = pFreeShippingMethods;
  }
  /**
   * @return the caFreeShippingMethods
   */
  public String getCaFreeShippingMethods() {
    return mCaFreeShippingMethods;
  }
  /**
   * @param pCaFreeShippingMethods the caFreeShippingMethods to set
   */
  public void setCaFreeShippingMethods(String pCaFreeShippingMethods) {
    mCaFreeShippingMethods = pCaFreeShippingMethods;
  }
  /**
   * @return the babFreeShippingMethods
   */
  public String getBabFreeShippingMethods() {
    return mBabFreeShippingMethods;
  }
  /**
   * @param pBabFreeShippingMethods the babFreeShippingMethods to set
   */
  public void setBabFreeShippingMethods(String pBabFreeShippingMethods) {
    mBabFreeShippingMethods = pBabFreeShippingMethods;
  }
  /**
   * @return the nonShippableStates
   */
  public String getNonShippableStates() {
    return mNonShippableStates;
  }
  /**
   * @param pNonShippableStates the nonShippableStates to set
   */
  public void setNonShippableStates(String pNonShippableStates) {
    mNonShippableStates = pNonShippableStates;
  }
  /**
   * @return the vDCSkuType
   */
  public String getVDCSkuType() {
    return mVDCSkuType;
  }
  /**
   * @param pVDCSkuType the vDCSkuType to set
   */
  public void setVDCSkuType(String pVDCSkuType) {
    mVDCSkuType = pVDCSkuType;
  }
  /**
   * @return the vDCSkuMessage
   */
  public String getVDCSkuMessage() {
    return mVDCSkuMessage;
  }
  /**
   * @param pVDCSkuMessage the vDCSkuMessage to set
   */
  public void setVDCSkuMessage(String pVDCSkuMessage) {
    mVDCSkuMessage = pVDCSkuMessage;
  }
  /**
   * @return the cAEcomFulfilmentFlag
   */
  public String getCAEcomFulfilmentFlag() {
    return mCAEcomFulfilmentFlag;
  }
  /**
   * @param pCAEcomFulfilmentFlag the cAEcomFulfilmentFlag to set
   */
  public void setCAEcomFulfilmentFlag(String pCAEcomFulfilmentFlag) {
    mCAEcomFulfilmentFlag = pCAEcomFulfilmentFlag;
  }
  /**
   * @return the overWeightFlag
   */
  public String getOverWeightFlag() {
    return mOverWeightFlag;
  }
  /**
   * @param pOverWeightFlag the overWeightFlag to set
   */
  public void setOverWeightFlag(String pOverWeightFlag) {
    mOverWeightFlag = pOverWeightFlag;
  }
  /**
   * @return the overSizeFlag
   */
  public String getOverSizeFlag() {
    return mOverSizeFlag;
  }
  /**
   * @param pOverSizeFlag the overSizeFlag to set
   */
  public void setOverSizeFlag(String pOverSizeFlag) {
    mOverSizeFlag = pOverSizeFlag;
  }
  /**
   * @return the bbbForceBelowLine
   */
  public String getForceBelowLine() {
    return mBbbForceBelowLine;
  }
  /**
   * @param pBbbForceBelowLine the bbbForceBelowLine to set
   */
  public void setForceBelowLine(String pBbbForceBelowLine) {
    mBbbForceBelowLine = pBbbForceBelowLine;
  }
  /**
   * @return the babForceBelowLine
   */
  public String getBabForceBelowLine() {
    return mBabForceBelowLine;
  }
  /**
   * @param pBabForceBelowLine the babForceBelowLine to set
   */
  public void setBabForceBelowLine(String pBabForceBelowLine) {
    mBabForceBelowLine = pBabForceBelowLine;
  }
  /**
   * @return the caForceBelowLine
   */
  public String getCaForceBelowLine() {
    return mCaForceBelowLine;
  }
  /**
   * @param pCaForceBelowLine the caForceBelowLine to set
   */
  public void setCaForceBelowLine(String pCaForceBelowLine) {
    mCaForceBelowLine = pCaForceBelowLine;
  }
  /**
   * @return the taxCd
   */
  public String getTaxCd() {
    return mTaxCd;
  }
  /**
   * @param pTaxCd the taxCd to set
   */
  public void setTaxCd(String pTaxCd) {
    mTaxCd = pTaxCd;
  }
  /**
   * @return the bopus
   */
  public String getBopus() {
    return mBopus;
  }
  /**
   * @param pBopus the bopus to set
   */
  public void setBopus(String pBopus) {
    mBopus = pBopus;
  }
  /**
   * @return the clearance
   */
  public String getClearance() {
    return mClearance;
  }
  /**
   * @param pClearance the clearance to set
   */
  public void setClearance(String pClearance) {
    mClearance = pClearance;
  }
  /**
   * @return the jdaClass
   */
  public String getJdaClass() {
    return mJdaClass;
  }
  /**
   * @param pJdaClass the jdaClass to set
   */
  public void setJdaClass(String pJdaClass) {
    mJdaClass = pJdaClass;
  }
  public String getVendorId() {
    return mVendorId;
  }
  public void setVendorId(String pVendorId) {
    this.mVendorId = pVendorId;
  } 
 
  	/**
  	 * @return mAssemblyTime
  	 */
  	public String getAssemblyTime() {
		return mAssemblyTime;
}
	/**
	 * @param mAssemblyTime
	 */
	public void setAssemblyTime(String mAssemblyTime) {
		this.mAssemblyTime = mAssemblyTime;
	}
	/**
	 * @return mAssemblyOffered
	 */
	public String getAssemblyOffered() {
		return mAssemblyOffered;
	}
	/**
	 * @param mAssemblyOffered
	 */
	public void setAssemblyOffered(String mAssemblyOffered) {
		this.mAssemblyOffered = mAssemblyOffered;
	}
	/**
	 * @return mLtlFlag
	 */
	public String getLtlFlag() {
		return mLtlFlag;
	}
	/**
	 * @param mLtlFlag
	 */
	public void setLtlFlag(String mLtlFlag) {
		this.mLtlFlag = mLtlFlag;
	}
	/**
	 * @return mOrderToShipSla
	 */
	public String getOrderToShipSla() {
		return mOrderToShipSla;
	}
	/**
	 * @param mOrderToShipSla
	 */
	public void setOrderToShipSla(String mOrderToShipSla) {
		this.mOrderToShipSla = mOrderToShipSla;
	}
	/**
	 * @return mCaseWeight
	 */
	public String getCaseWeight() {
		return mCaseWeight;
	}
	/**
	 * @param mCaseWeight
	 */
	public void setCaseWeight(String mCaseWeight) {
		this.mCaseWeight = mCaseWeight;
	}
	
	/**
	 * @return mIntlRestricted
	 */
	public String getIntlRestricted() {
		return mIntlRestricted;
	}

	/**
	 * @param mIntlRestricted
	 */
	public void setIntlRestricted(String pIntlRestricted) {
		this.mIntlRestricted = pIntlRestricted;
	}
	
	/**
	 * @return mCustomizationOfferedFlag
	 */
	public String getCustomizationOfferedFlag() {
		return mCustomizationOfferedFlag;
	}

	/**
	 * @param mCustomizationOfferedFlag
	 */
	public void setCustomizationOfferedFlag(final String pCustomizationOfferedFlag) {
		this.mCustomizationOfferedFlag = pCustomizationOfferedFlag;
	}

	/**
	 * @return the mBabCustomizationOfferedFlag
	 */
	public String getBabCustomizationOfferedFlag() {
		return mBabCustomizationOfferedFlag;
	}

	/**
	 * @param mBabCustomizationOfferedFlag
	 */
	public void setBabCustomizationOfferedFlag(
			final String pBabCustomizationOfferedFlag) {
		this.mBabCustomizationOfferedFlag = pBabCustomizationOfferedFlag;
	}

	/**
	 * @return the mCaCustomizationOfferedFlag
	 */
	public String getCaCustomizationOfferedFlag() {
		return mCaCustomizationOfferedFlag;
	}

	/**
	 * @param mCaCustomizationOfferedFlag
	 */
	public void setCaCustomizationOfferedFlag(final String pCaCustomizationOfferedFlag) {
		this.mCaCustomizationOfferedFlag = pCaCustomizationOfferedFlag;
	}

	/**
	 * @return the mGsCustomizationOfferedFlag
	 */
	public String getGsCustomizationOfferedFlag() {
		return mGsCustomizationOfferedFlag;
	}

	/**
	 * @param mGsCustomizationOfferedFlag
	 */
	public void setGsCustomizationOfferedFlag(final String pGsCustomizationOfferedFlag) {
		this.mGsCustomizationOfferedFlag = pGsCustomizationOfferedFlag;
	}

	/**
	 * @return the mPersonalizationType
	 */
	public String getPersonalizationType() {
		return mPersonalizationType;
	}

	/**
	 * @param pPersonalizationType
	 */
	public void setPersonalizationType(final String pPersonalizationType) {
		this.mPersonalizationType = pPersonalizationType;
	}

	/**
	 * @return the mEligibleCustomizationCodes
	 */
	public String getEligibleCustomizationCodes() {
		return mEligibleCustomizationCodes;
	}

	/**
	 * @param mEligibleCustomizationCodes
	 */
	public void setEligibleCustomizationCodes(final String pEligibleCustomizationCodes) {
		this.mEligibleCustomizationCodes = pEligibleCustomizationCodes;
	}

	/**
	 * @return the mMinShippingDays
	 */
	public String getMinShippingDays() {
		return mMinShippingDays;
	}

	/**
	 * @param mMinShippingDays
	 */
	public void setMinShippingDays(final String pMinShippingDays) {
		this.mMinShippingDays = pMinShippingDays;
	}

	/**
	 * @return the mMaxShippingDays
	 */
	public String getMaxShippingDays() {
		return mMaxShippingDays;
	}

	/**
	 * @param mMaxShippingDays
	 */
	public void setMaxShippingDays(final String pMaxShippingDays) {
		this.mMaxShippingDays = pMaxShippingDays;
	}

	/**
	 * @return the mShippingCutoffOffset
	 */
	public String getShippingCutoffOffset() {
		return mShippingCutoffOffset;
	}

	/**
	 * @param mShippingCutoffOffset
	 */
	public void setShippingCutoffOffset(final String pShippingCutoffOffset) {
		this.mShippingCutoffOffset = pShippingCutoffOffset;
	}
 
}
