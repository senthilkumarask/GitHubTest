package com.bbb.commerce.common;

import java.io.Serializable;

/**
 * This is a custom class for Coupon Filter flags for the Order.
 * 
 * @author agupta
 * @version $Revision: #1 $
 */

public class couponFilterVO implements Serializable{// NOPMD: TODO: This PMD needs to be fixed with proper regression impact 

	/* MEMBER VARIABLES */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean containsBOPUS;
	private boolean containsGiftCard;
	private boolean containsNMC;
	private boolean containsSKUExclusion;
	private boolean containsNormal;

	/**
	 * @return the containsBOPUS
	 */
	public boolean isContainsBOPUS() {
		return containsBOPUS;
	}
	/**
	 * @param containsBOPUS the containsBOPUS to set
	 */
	public void setContainsBOPUS(boolean pContainsBOPUS) {
		this.containsBOPUS = pContainsBOPUS;
	}
	/**
	 * @return the containsGiftCard
	 */
	public boolean isContainsGiftCard() {
		return containsGiftCard;
	}
	/**
	 * @param containsGiftCard the containsGiftCard to set
	 */
	public void setContainsGiftCard(boolean pContainsGiftCard) {
		this.containsGiftCard = pContainsGiftCard;
	}
	/**
	 * @return the containsNMC
	 */
	public boolean isContainsNMC() {
		return containsNMC;
	}
	/**
	 * @param containsNMC the containsNMC to set
	 */
	public void setContainsNMC(boolean pContainsNMC) {
		this.containsNMC = pContainsNMC;
	}
	/**
	 * @return the containsSKUExclusion
	 */
	public boolean isContainsSKUExclusion() {
		return containsSKUExclusion;
	}
	/**
	 * @param containsSKUExclusion the containsSKUExclusion to set
	 */
	public void setContainsSKUExclusion(boolean pContainsSKUExclusion) {
		this.containsSKUExclusion = pContainsSKUExclusion;
	}
	/**
	 * @return the containsNormal
	 */
	public boolean isContainsNormal() {
		return containsNormal;
	}
	/**
	 * @param containsNormal the containsNormal to set
	 */
	public void setContainsNormal(boolean pContainsNormal) {
		this.containsNormal = pContainsNormal;
	}
}