package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PaginationVO implements Serializable
{
  
	private static final long serialVersionUID = 1L;
private long currentPage;
  private long pageCount;
  private String firstPage;
  private String previousPage;
  private String nextPage;
  private String lastPage;
  private String secondPage;
  private String thirdPage;
  private long pageSize;
  private String secondLast;
  private String thirdLast;
  private String currentPageUrl;
  private String pageFilter;
  private String canonicalPageFilter;
  private List<String> pageNumbers = new ArrayList<String>();
	/**
	 * @return the pageFilter
	 */
	public String getPageFilter() {
		return pageFilter;
	}
	/**
	 * @param pageFilter the pageFilter to set
	 */
	public void setPageFilter(String pageFilter) {
		this.pageFilter = pageFilter;
	}
	
/**
 * @return the currentPage
 */
public long getCurrentPage() {
	return currentPage;
}
/**
 * @param currentPage the currentPage to set
 */
public void setCurrentPage(final long currentPage) {
	this.currentPage = currentPage;
}
/**
 * @return the pageCount
 */
public long getPageCount() {
	return pageCount;
}
/**
 * @param pageCount the pageCount to set
 */
public void setPageCount(final long pageCount) {
	this.pageCount = pageCount;
}
/**
 * @return the firstPage
 */
public String getFirstPage() {
	return firstPage;
}
/**
 * @param firstPage the firstPage to set
 */
public void setFirstPage(final String firstPage) {
	this.firstPage = firstPage;
}
/**
 * @return the previousPage
 */
public String getPreviousPage() {
	return previousPage;
}
/**
 * @param previousPage the previousPage to set
 */
public void setPreviousPage(final String previousPage) {
	this.previousPage = previousPage;
}
/**
 * @return the nextPage
 */
public String getNextPage() {
	return nextPage;
}
/**
 * @param nextPage the nextPage to set
 */
public void setNextPage(final String nextPage) {
	this.nextPage = nextPage;
}
/**
 * @return the lastPage
 */
public String getLastPage() {
	return lastPage;
}
/**
 * @param lastPage the lastPage to set
 */
public void setLastPage(final String lastPage) {
	this.lastPage = lastPage;
}
/**
 * @return the secondPage
 */
public String getSecondPage() {
	return secondPage;
}
/**
 * @param secondPage the secondPage to set
 */
public void setSecondPage(final String secondPage) {
	this.secondPage = secondPage;
}
/**
 * @return the thirdPage
 */
public String getThirdPage() {
	return thirdPage;
}
/**
 * @param thirdPage the thirdPage to set
 */
public void setThirdPage(final String thirdPage) {
	this.thirdPage = thirdPage;
}
/**
 * @return the pageSize
 */
public long getPageSize() {
	return pageSize;
}
/**
 * @param pPageSize the pageSize to set
 */
public void setPageSize(final long pPageSize) {
	pageSize = pPageSize;
}
/**
 * @return the secondLast
 */
public String getSecondLast() {
	return secondLast;
}
/**
 * @param pSecondLast the secondLast to set
 */
public void setSecondLast(final String pSecondLast) {
	secondLast = pSecondLast;
}
/**
 * @return the thirdLast
 */
public String getThirdLast() {
	return thirdLast;
}
/**
 * @param pThirdLast the thirdLast to set
 */
public void setThirdLast(final String pThirdLast) {
	thirdLast = pThirdLast;
}
/**
 * @return the currentPageUrl
 */
public String getCurrentPageUrl() {
	return currentPageUrl;
}
/**
 * @param pCurrentPageUrl the currentPageUrl to set
 */
public void setCurrentPageUrl(final String pCurrentPageUrl) {
	currentPageUrl = pCurrentPageUrl;
}
/**
 * @return the pageNumbers
 */
public List<String> getPageNumbers() {
	return pageNumbers;
}
/**
 * @param pPageNumbers the pageNumbers to set
 */
public void setPageNumbers(List<String> pPageNumbers) {
	pageNumbers = pPageNumbers;
} 

/**
 * @return the canonicalPageFilter
 */
public String getCanonicalPageFilter() {
	return canonicalPageFilter;
}
/**
 * @param canonicalPageFilter the canonicalPageFilter to set
 */
public void setCanonicalPageFilter(String canonicalPageFilter) {
	this.canonicalPageFilter = canonicalPageFilter;
}

}
