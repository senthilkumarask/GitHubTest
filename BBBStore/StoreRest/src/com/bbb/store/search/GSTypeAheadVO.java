package com.bbb.store.search;

import java.io.Serializable;
import java.util.List;

import com.bbb.search.bean.result.FacetQueryResults;


 
public class GSTypeAheadVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	FacetQueryResults facetQueryResults;
	private List<GSProductSearchVO> productTitleResult;
	public FacetQueryResults getFacetQueryResults() {
		return facetQueryResults;
	}
	public void setFacetQueryResults(FacetQueryResults facetQueryResults) {
		this.facetQueryResults = facetQueryResults;
	}
	public List<GSProductSearchVO> getProductTitleResult() {
		return productTitleResult;
	}
	public void setProductTitleResult(List<GSProductSearchVO> productTitleResult) {
		this.productTitleResult = productTitleResult;
	}
	 


}
