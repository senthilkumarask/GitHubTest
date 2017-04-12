/**
 * 
 */
package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.List;

/**
 * @author agoe21
 *
 */
public class FacetQuery implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private String mKeyword;
	private String mSiteId;
	private String mCatalogId;
	private List<String> mFacets;
	
	//pageFilterSize for default follow on query
	private String mPageFilterSize;
	
	/**
	 * @return the keyword
	 */
	public String getPageFilterSize() {
		return this.mPageFilterSize;
	}

	/**
	 * @param pKeyword the keyword to set
	 */
	public void setPageFilterSize(final String mPageFilterSize) {
		this.mPageFilterSize = mPageFilterSize;
	}

	/* START R2.1 TypeAhead for Most Popular Keywords */
	private boolean mShowPopularTerms;

	public boolean isShowPopularTerms() {
		return mShowPopularTerms;
	}

	public void setShowPopularTerms(boolean showPopularTerms) {
		this.mShowPopularTerms = showPopularTerms;
	}
	/* END   R2.1 TypeAhead for Most Popular Keywords */

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return mKeyword;
	}

	/**
	 * @param pKeyword the keyword to set
	 */
	public void setKeyword(final String pKeyword) {
		this.mKeyword = pKeyword;
	}

	/**
	 * @return the facets
	 */
	public List<String> getFacets() {
		return mFacets;
	}

	/**
	 * @param pFacets the facets to set
	 */
	public void setFacets(final List<String> pFacets) {
		this.mFacets = pFacets;
	}

	/**
	 * @return mSiteId
	 */
	public String getSiteId() {
		return mSiteId;
	}

	/**
	 * @param mSiteId
	 */
	public void setSiteId(String mSiteId) {
		this.mSiteId = mSiteId;
	}

	/**
	 * @return the mCatalogId
	 */
	public String getCatalogId() {
		return mCatalogId;
	}

	/**
	 * @param mCatalogId the mCatalogId to set
	 */
	public void setCatalogId(String mCatalogId) {
		this.mCatalogId = mCatalogId;
	}
}
