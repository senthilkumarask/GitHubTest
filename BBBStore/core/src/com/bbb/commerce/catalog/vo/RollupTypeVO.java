package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author njai13
 *
 */
public class RollupTypeVO implements Comparable<RollupTypeVO>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rollupTypeCode;
	private String rollupAttribute;
	private String swatchImagePath;

	// START -- Added following properties for R2.1 Item - PDP Color Swatch Story
	private String largeImagePath;
	private String thumbnailImagePath;
	private String smallImagePath;
	private String firstRollUp;
	// END   -- Added following properties for R2.1 Item - PDP Color Swatch Story
	//GS-130 Added skuImageMap
	private Map<String, String> skuImageMap;

	public String getFirstRollUp() {
		return this.firstRollUp;
	}


	public void setFirstRollUp(final String firstRollUp) {
		this.firstRollUp = firstRollUp;
	}


	private volatile int hashCode = 0;

	public RollupTypeVO() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the rollupTypeCode
	 */
	public String getRollupTypeCode() {
		return this.rollupTypeCode;

	}

	/**
	 * @param rollupTypeCode the rollupTypeCode to set
	 */
	public void setRollupTypeCode(final String rollupTypeCode) {
		this.rollupTypeCode = rollupTypeCode;
	}

	/**
	 * @return the rollupAttribute
	 */
	public String getRollupAttribute() {

		return this.rollupAttribute;

	}

	/**
	 * @param rollupAttribute the rollupAttribute to set
	 */
	public void setRollupAttribute(final String rollupAttribute) {
		this.rollupAttribute = rollupAttribute;
	}
	/**
	 * @return the swatchImagePath
	 */
	public String getSwatchImagePath() {
		return this.swatchImagePath;
	}
	/**
	 * @param swatchImagePath the swatchImagePath to set
	 */
	public void setSwatchImagePath(final String swatchImagePath) {
		this.swatchImagePath = swatchImagePath;
	}

	@Override
	public String toString(){
		final StringBuffer toString=new StringBuffer(" RollUp Type VO Details \n ");
		toString.append("Roll Up Type Code  ").append(this.rollupTypeCode).append("\n")
		.append(" Roll Up Type Attribute  ").append(this.rollupAttribute).append("\n")
		.append(" Swatch Image Path ").append(this.swatchImagePath).append("\n");

		return toString.toString();
	}
	@Override
	public int hashCode() {
		final int multiplier = 23;
		if (this.hashCode == 0) {
			int code = 133;
			code = (multiplier * code) + this.rollupTypeCode.hashCode();

			this.hashCode = code;
		}
		return this.hashCode;

	}


	@Override
	public boolean equals(final Object pObj) {

		if (pObj instanceof RollupTypeVO ) {
			final RollupTypeVO rollUpVO = (RollupTypeVO) pObj;
			if (this.rollupAttribute.equals(rollUpVO.rollupAttribute)
					&& this.rollupTypeCode.equals(rollUpVO.rollupTypeCode)
					&& this.swatchImagePath.equals(rollUpVO.swatchImagePath)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public int compareTo(final RollupTypeVO o) {
		final int compareResult = this.rollupAttribute.compareTo( o.rollupAttribute);
		return compareResult;
	}

	// START -- Added following getters / setters for R2.1 Item - PDP Color Swatch Story
	public String getLargeImagePath() {
		return this.largeImagePath;
	}


	public void setLargeImagePath(final String largeImagePath) {
		this.largeImagePath = largeImagePath;
	}


	public String getThumbnailImagePath() {
		return this.thumbnailImagePath;
	}


	public void setThumbnailImagePath(final String thumbnailImagePath) {
		this.thumbnailImagePath = thumbnailImagePath;
	}


	public String getSmallImagePath() {
		return this.smallImagePath;
	}


	public void setSmallImagePath(final String smallImagePath) {
		this.smallImagePath = smallImagePath;
	}
	// END   -- Added following getters / setters for R2.1 Item - PDP Color Swatch Story


	public Map<String, String> getSkuImageMap() {
		return skuImageMap;
	}


	public void setSkuImageMap(Map<String, String> skuImageMap) {
		this.skuImageMap = skuImageMap;
	}
}
