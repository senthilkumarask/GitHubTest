package com.bbb.search.vendor.customizer;

import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.SearchResults;

public interface VendorResponseCustomizer {
	
	 public abstract void customizeResponse(SearchResults browseSearchVO, SearchQuery pSearchQuery);
	 
}
