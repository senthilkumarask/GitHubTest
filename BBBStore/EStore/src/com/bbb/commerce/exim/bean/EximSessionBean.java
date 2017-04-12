package com.bbb.commerce.exim.bean;

public class EximSessionBean {
	
	private EximCustomizedAttributesVO eximResponse;
	private long quantity;
	private boolean altImages;
	private String skuId;
	private String refnum;
	private String productId;
	private String siteId;
	private boolean personalizationComplete;
	private String personalizedSingleCode;
	private String personalizedSingleCodeDescription;
	
	public String getPersonalizedSingleCode() {
		return personalizedSingleCode;
	}
	public void setPersonalizedSingleCode(String personalizedSingleCode) {
		this.personalizedSingleCode = personalizedSingleCode;
	}
	public boolean isPersonalizationComplete() {
		return personalizationComplete;
	}
	public void setPersonalizationComplete(boolean personalizationComplete) {
		this.personalizationComplete = personalizationComplete;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public EximCustomizedAttributesVO getEximResponse() {
		return eximResponse;
	}
	public void setEximResponse(EximCustomizedAttributesVO eximResponse) {
		this.eximResponse = eximResponse;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public boolean isAltImages() {
		return altImages;
	}
	public void setAltImages(boolean altImages) {
		this.altImages = altImages;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getRefnum() {
		return refnum;
	}
	public void setRefnum(String refnum) {
		this.refnum = refnum;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((refnum == null) ? 0 : refnum.hashCode());
		result = prime * result + ((skuId == null) ? 0 : skuId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EximSessionBean other = (EximSessionBean) obj;
		if (refnum == null) {
			if (other.refnum != null)
				return false;
		} else if (!refnum.equals(other.refnum))
			return false;
		if (skuId == null) {
			if (other.skuId != null)
				return false;
		} else if (!skuId.equals(other.skuId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "EximSessionBean [eximResponse=" + eximResponse + ", quantity="
				+ quantity + ", altImages=" + altImages + ", skuId=" + skuId
				+ ", refnum=" + refnum + ", productId=" + productId
				+ ", siteId=" + siteId + ", personalizationComplete="
				+ personalizationComplete + "]";
	}
	public String getPersonalizedSingleCodeDescription() {
		return personalizedSingleCodeDescription;
	}
	public void setPersonalizedSingleCodeDescription(
			String personalizedSingleCodeDescription) {
		this.personalizedSingleCodeDescription = personalizedSingleCodeDescription;
	}
	
}
