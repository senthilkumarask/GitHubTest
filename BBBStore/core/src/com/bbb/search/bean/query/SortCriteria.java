/**
 * 
 */
package com.bbb.search.bean.query;

import java.io.Serializable;

/** This VO holds sort criteria parameters.
 * @author agoe21
 *
 */
public class SortCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sortFieldName;
	private boolean sortAscending;	

	/**
	 * @return the sortFieldName
	 */
	public String getSortFieldName() {
		return sortFieldName;
	}

	/**
	 * @param sortFieldName the sortFieldName to set
	 */
	public void setSortFieldName(String sortFieldName) {
		this.sortFieldName = sortFieldName;
	}

	/**
	 * @param pSortAscending the sortAscending to set
	 */
	public void setSortAscending(boolean pSortAscending) {
		sortAscending = pSortAscending;
	}

	/**
	 * @return the sortAscending
	 */
	public boolean isSortAscending() {
		return sortAscending;
	}
}
