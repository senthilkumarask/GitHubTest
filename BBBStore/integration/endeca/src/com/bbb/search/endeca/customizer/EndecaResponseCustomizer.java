package com.bbb.search.endeca.customizer;

import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.SearchResults;

/**
 * @author rjai39
 *
 */
public interface EndecaResponseCustomizer
{
    public abstract void customizeResponse(SearchResults browseSearchVO, SearchQuery pSearchQuery);
        
}
