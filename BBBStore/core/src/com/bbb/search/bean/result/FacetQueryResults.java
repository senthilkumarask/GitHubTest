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
public class FacetQueryResults implements Serializable{

	private static final long serialVersionUID = 1L;

	private FacetQuery mFacetQuery;
	
	private List<FacetQueryResult> mResults;
	
	private String topPopularTerm;

	/**
	 * @return the facetQuery
	 */
	public FacetQuery getFacetQuery() {
		return mFacetQuery;
	}

	/**
	 * @param pFacetQuery the facetQuery to set
	 */
	public void setFacetQuery(final FacetQuery pFacetQuery) {
		this.mFacetQuery = pFacetQuery;
	}

	/**
	 * @return the results
	 */
	public List<FacetQueryResult> getResults() {
		return mResults;
	}

	/**
	 * @param pResults the results to set
	 */
	public void setResults(final List<FacetQueryResult> pResults) {
		this.mResults = pResults;
	}

	/**
	 * @return the topPopularTerm
	 */
	public String getTopPopularTerm() {
		return topPopularTerm;
	}

	/**
	 * @param topPopularTerm the topPopularTerm to set
	 */
	public void setTopPopularTerm(String topPopularTerm) {
		this.topPopularTerm = topPopularTerm;
	}
}
