package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.List;

public class SortOptionVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SortOptionsVO defaultSortingOption;
	private List<SortOptionsVO> sortingOptions;
	/**
	 * @return the defaultSortingOption
	 */
	public SortOptionsVO getDefaultSortingOption() {
		return defaultSortingOption;
	}
	/**
	 * @param defaultSortingOption the defaultSortingOption to set
	 */
	public void setDefaultSortingOption(SortOptionsVO defaultSortingOption) {
		this.defaultSortingOption = defaultSortingOption;
	}
	/**
	 * @return the sortingOptions
	 */
	public List<SortOptionsVO> getSortingOptions() {
		return sortingOptions;
	}
	/**
	 * @param sortingOptions the sortingOptions to set
	 */
	public void setSortingOptions(List<SortOptionsVO> sortingOptions) {
		this.sortingOptions = sortingOptions;
	}
	
	
	
}
