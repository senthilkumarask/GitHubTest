package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.List;

/**
 * @author agupt8
 *
 */
public class CategoryParentVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String query;
	private String isPortraitEligible;
	private String categorySEOUrl;
	private List<CategoryRefinementVO> categoryRefinement;
	private List<String> categoryTree;
	private Boolean phantomCategory;
	private Boolean isMoreLinkRequired;
	
	
	public String getCategorySEOUrl() {
		return categorySEOUrl;
	}
	public void setCategorySEOUrl(String categorySEOURL) {
		this.categorySEOUrl = categorySEOURL;
	}
	public Boolean getIsMoreLinkRequired() {
		return isMoreLinkRequired;
	}
	public void setIsMoreLinkRequired(Boolean isMoreLinkRequired) {
		this.isMoreLinkRequired = isMoreLinkRequired;
	}
	/**
	 * @return the phantomCategory
	 */
	public Boolean getPhantomCategory() {
		return phantomCategory;
	}
	/**
	 * @param phantomCategory the phantomCategory to set
	 */
	public void setPhantomCategory(Boolean phantomCategory) {
		this.phantomCategory = phantomCategory;
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
	 * @return the categoryRefinement
	 */
	public List<CategoryRefinementVO> getCategoryRefinement() {
		return categoryRefinement;
	}
	/**
	 * @param categoryRefinement the categoryRefinement to set
	 */
	public void setCategoryRefinement(final List<CategoryRefinementVO> categoryRefinement) {
		this.categoryRefinement = categoryRefinement;
	}
	/**
	 * @return the categoryTree
	 */
	public List<String> getCategoryTree() {
		return categoryTree;
	}
	/**
	 * @param categoryTree the categoryTree to set
	 */
	public void setCategoryTree(List<String> categoryTree) {
		this.categoryTree = categoryTree;
	}
	
	public String getIsPortraitEligible() {
		return isPortraitEligible;
	}
	
	public void setIsPortraitEligible(String isPortraitEligible) {
		this.isPortraitEligible = isPortraitEligible;
	}
	@Override
	public String toString() {
		return "CategoryParentVO [name=" + name + ", query=" + query
				+ ", isPortraitEligible=" + isPortraitEligible
				+ ", categoryRefinement=" + categoryRefinement
				+ ", categoryTree=" + categoryTree + ", phantomCategory="
				+ phantomCategory + ", isMoreLinkRequired="
				+ isMoreLinkRequired + "]";
	}
}
