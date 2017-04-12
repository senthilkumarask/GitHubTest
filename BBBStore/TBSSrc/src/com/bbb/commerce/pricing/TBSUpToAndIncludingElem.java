package com.bbb.commerce.pricing;

import atg.commerce.pricing.definition.UpToAndIncludingElem;

public class TBSUpToAndIncludingElem extends UpToAndIncludingElem {
	
	// Sorts commerce items by their current price rather than the list price
    public void setSortBy(String pSortBy)
    {
    	if( pSortBy.equals("priceInfo.listPrice") ) {
    		super.setSortBy("priceInfo.amount");
    	}
    	else {
    		super.setSortBy(pSortBy);
    	}
    }    
}
