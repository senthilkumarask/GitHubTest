package com.bbb.commerce.checklist.vo;

import java.io.Serializable;

/**
 * The Class CheckListPrevNextCategoriesVO.
 *
 * @author ssi191
 */
public class CheckListPrevNextCategoriesVO  implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The cat1 id. */
	private String cat1Id;
	
	/** The cat2 id. */
	private String cat2Id;
	
	/** The cat3 id. */
	private String cat3Id;
	
	/** The prev cat2 id. */
	private String prevCatId;
	
	/** The next cat2 id. */
	private String nextCatId;
	
	/** The prev cat url. */
	private String prevCatUrl;
	
	/** The prev cat name. */
	private String prevCatName;
	
	/** The current cat url. */
	private String currentCatUrl;
	
	/** The current cat name. */
	private String currentCatName;
	
	private String currentCatId;
	
	/** The next cat url. */
	private String nextCatUrl;
	
	/** The next cat name. */
	private String nextCatName;
	
	/** The added quantity. */
	private int addedQuantity;
	
	/** The suggested quantity. */
	private int suggestedQuantity;
	
	/**
	 * Gets the cat1 id.
	 *
	 * @return the cat1 id
	 */
	public String getCat1Id() {
		return cat1Id;
	}
	
	/**
	 * Sets the cat1 id.
	 *
	 * @param cat1Id the new cat1 id
	 */
	public void setCat1Id(String cat1Id) {
		this.cat1Id = cat1Id;
	}
	
	/**
	 * Gets the cat2 id.
	 *
	 * @return the cat2 id
	 */
	public String getCat2Id() {
		return cat2Id;
	}

	/**
	 * Gets the cat3 id.
	 *
	 * @return the cat3 id
	 */
	public String getCat3Id() {
		return cat3Id;
	}
	
	/**
	 * Sets the cat3 id.
	 *
	 * @param cat3Id the new cat3 id
	 */
	public void setCat3Id(String cat3Id) {
		this.cat3Id = cat3Id;
	}
	
	/**
	 * Sets the cat2 id.
	 *
	 * @param cat2Id the new cat2 id
	 */
	public void setCat2Id(String cat2Id) {
		this.cat2Id = cat2Id;
	}
	
	/**
	 * Gets the added quantity.
	 *
	 * @return the added quantity
	 */
	public int getAddedQuantity() {
		return addedQuantity;
	}
	
	/**
	 * Sets the added quantity.
	 *
	 * @param addedQuantity the new added quantity
	 */
	public void setAddedQuantity(int addedQuantity) {
		this.addedQuantity = addedQuantity;
	}
	
	/**
	 * Gets the suggested quantity.
	 *
	 * @return the suggested quantity
	 */
	public int getSuggestedQuantity() {
		return suggestedQuantity;
	}
	
	/**
	 * Sets the suggested quantity.
	 *
	 * @param suggestedQuantity the new suggested quantity
	 */
	public void setSuggestedQuantity(int suggestedQuantity) {
		this.suggestedQuantity = suggestedQuantity;
	}
	
	/**
	 * Gets the prev cat url.
	 *
	 * @return the prevCatUrl
	 */
	public String getPrevCatUrl() {
		return prevCatUrl;
	}
	
	/**
	 * Sets the prev cat url.
	 *
	 * @param prevCatUrl the prevCatUrl to set
	 */
	public void setPrevCatUrl(String prevCatUrl) {
		this.prevCatUrl = prevCatUrl;
	}
	
	/**
	 * Gets the prev cat name.
	 *
	 * @return the prevCatName
	 */
	public String getPrevCatName() {
		return prevCatName;
	}
	
	/**
	 * Sets the prev cat name.
	 *
	 * @param prevCatName the prevCatName to set
	 */
	public void setPrevCatName(String prevCatName) {
		this.prevCatName = prevCatName;
	}
	
	/**
	 * Gets the current cat url.
	 *
	 * @return the currentCatUrl
	 */
	public String getCurrentCatUrl() {
		return currentCatUrl;
	}
	
	/**
	 * Sets the current cat url.
	 *
	 * @param currentCatUrl the currentCatUrl to set
	 */
	public void setCurrentCatUrl(String currentCatUrl) {
		this.currentCatUrl = currentCatUrl;
	}
	
	/**
	 * Gets the current cat name.
	 *
	 * @return the currentCatName
	 */
	public String getCurrentCatName() {
		return currentCatName;
	}
	
	/**
	 * Sets the current cat name.
	 *
	 * @param currentCatName the currentCatName to set
	 */
	public void setCurrentCatName(String currentCatName) {
		this.currentCatName = currentCatName;
	}
	
	/**
	 * Gets the next cat url.
	 *
	 * @return the nextCatUrl
	 */
	public String getNextCatUrl() {
		return nextCatUrl;
	}
	
	/**
	 * Sets the next cat url.
	 *
	 * @param nextCatUrl the nextCatUrl to set
	 */
	public void setNextCatUrl(String nextCatUrl) {
		this.nextCatUrl = nextCatUrl;
	}
	
	/**
	 * Gets the next cat name.
	 *
	 * @return the nextCatName
	 */
	public String getNextCatName() {
		return nextCatName;
	}
	
	/**
	 * Sets the next cat name.
	 *
	 * @param nextCatName the nextCatName to set
	 */
	public void setNextCatName(String nextCatName) {
		this.nextCatName = nextCatName;
	}
	
	public String getPrevCatId() {
		return prevCatId;
	}

	public void setPrevCatId(String prevCatId) {
		this.prevCatId = prevCatId;
	}

	public String getNextCatId() {
		return nextCatId;
	}

	public void setNextCatId(String nextCatId) {
		this.nextCatId = nextCatId;
	}

	public String getCurrentCatId() {
		return currentCatId;
	}

	public void setCurrentCatId(String currentCatId) {
		this.currentCatId = currentCatId;
	}

}
