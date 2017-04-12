package com.bbb.tbs;

import java.util.List;

public class TBSPriceOverrideReason {
	
	@SuppressWarnings("rawtypes")
	private List itemReasonCodes;
	private List shipReasonCodes;
	private List surchargeReasonCodes;
	
	private List itemReasonDesc;
	private List shipReasonDesc;
	private List surchargeReasonDesc;
	
	private List competitors;
	
	private List<String> taxReasonCodes;
	private List<String> taxReasonDesc;
	
	private List<String> giftwrapReasonCodes;
	private List<String> giftwrapReasonDesc;
	
	public List getItemReasonCodes() {
		return itemReasonCodes;
	}
	public void setItemReasonCodes(List itemReasonCodes) {
		this.itemReasonCodes = itemReasonCodes;
	}
	public List getShipReasonCodes() {
		return shipReasonCodes;
	}
	public void setShipReasonCodes(List shipReasonCodes) {
		this.shipReasonCodes = shipReasonCodes;
	}
	public List getSurchargeReasonCodes() {
		return surchargeReasonCodes;
	}
	public void setSurchargeReasonCodes(List surchargeReasonCodes) {
		this.surchargeReasonCodes = surchargeReasonCodes;
	}
	public List getItemReasonDesc() {
		return itemReasonDesc;
	}
	public void setItemReasonDesc(List itemReasonDesc) {
		this.itemReasonDesc = itemReasonDesc;
	}
	public List getShipReasonDesc() {
		return shipReasonDesc;
	}
	public void setShipReasonDesc(List shipReasonDesc) {
		this.shipReasonDesc = shipReasonDesc;
	}
	public List getSurchargeReasonDesc() {
		return surchargeReasonDesc;
	}
	public void setSurchargeReasonDesc(List surchargeReasonDesc) {
		this.surchargeReasonDesc = surchargeReasonDesc;
	}
	public List getCompetitors() {
		return competitors;
	}
	public void setCompetitors(List competitors) {
		this.competitors = competitors;
	}
	/**
	 * @return the taxReasonCodes
	 */
	public List<String> getTaxReasonCodes() {
		return taxReasonCodes;
	}
	/**
	 * @return the taxReasonDesc
	 */
	public List<String> getTaxReasonDesc() {
		return taxReasonDesc;
	}
	/**
	 * @param pTaxReasonCodes the taxReasonCodes to set
	 */
	public void setTaxReasonCodes(List<String> pTaxReasonCodes) {
		taxReasonCodes = pTaxReasonCodes;
	}
	/**
	 * @param pTaxReasonDesc the taxReasonDesc to set
	 */
	public void setTaxReasonDesc(List<String> pTaxReasonDesc) {
		taxReasonDesc = pTaxReasonDesc;
	}
	
	/**
	 * @return the giftwrapReasonCodes
	 */
	public List<String> getGiftWrapReasonCodes() {
		return giftwrapReasonCodes;
	}
	/**
	 * @return the giftwrapReasonDesc
	 */
	public List<String> getGiftWrapReasonDesc() {
		return giftwrapReasonDesc;
	}
	/**
	 * @param pGiftWrapReasonCodes the giftwrapReasonCodes to set
	 */
	public void setGiftWrapReasonCodes(List<String> pGiftWrapReasonCodes) {
		giftwrapReasonCodes = pGiftWrapReasonCodes;
	}
	/**
	 * @param pGiftWrapReasonDesc the giftwrapReasonDesc to set
	 */
	public void setGiftWrapReasonDesc(List<String> pGiftWrapReasonDesc) {
		giftwrapReasonDesc = pGiftWrapReasonDesc;
	}
}
