package com.bbb.search.bean.result;

import java.io.Serializable;

/**
 * @author agupt8
 *
 */
public class CategoryRefinementVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String query;
	private String size;
	private String isPortraitEligible;
	private String totalSize;
	private String subCatURL;
	
	/**
	 * @return the totalSize
	 */
	public String getTotalSize() {
		return totalSize;
	}
	/**
	 * @param totalSize the totalSize to set
	 */
	public void setTotalSize(String totalSize) {
		this.totalSize = totalSize;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(final String query) {
		this.query = query;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(final String size) {
		this.size = size;
	}

	public String getIsPortraitEligible() {
		return isPortraitEligible;
	}
	
	public void setIsPortraitEligible(String isPortraitEligible) {
		this.isPortraitEligible = isPortraitEligible;
	}
	
	public String getSubCatURL() {
		return subCatURL;
	}
	public void setSubCatURL(String subCatURL) {
		this.subCatURL = subCatURL;
	}
	
	@Override
	public String toString() {
		return "CategoryRefinementVO [name=" + name + ", query=" + query
				+ ", size=" + size + ", isPortraitEligible="
				+ isPortraitEligible + ", totalSize=" + totalSize + "subCatURL" + subCatURL + "]";
	}
}
