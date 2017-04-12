package com.bbb.search.endeca.index;

import java.util.HashSet;
import java.util.Set;

import atg.endeca.index.RecordStoreBulkLoaderImpl;
import atg.repository.MembershipContext;
import atg.search.multisite.SiteIndexInfo;

public class ProductCatalogBulkLoaderImpl extends RecordStoreBulkLoaderImpl {
	
	/**
	 * Returns empty hashset so that indexing would include all products
	 * and not rely on contextMemberships property
	 * @param pSiteIndexInfo
	 * 
	 * @return empty hashset
	 * 
	 */
	@Override
	public Set<MembershipContext> createMembershipContextSet(SiteIndexInfo pSiteIndexInfo) {
		return new HashSet<MembershipContext>();
	}



}
