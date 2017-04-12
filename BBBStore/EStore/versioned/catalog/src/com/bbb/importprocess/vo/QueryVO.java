package com.bbb.importprocess.vo;

import java.util.Arrays;

/**
 * Represents Query Object 
 * 
 * @author skuma7
 *
 */
public class QueryVO {
  
	/**
	 * Query String or prepared statement
	 */
	private String query;
	
	/**
	 * Query parameters
	 */
	private String[] queryParams;
	
	public QueryVO(){
		// default constructor
	}
	
	public QueryVO(String query, String[] queryParams){	
		this.query = query;
		this.queryParams = queryParams;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String[] getQueryParams() {
		return queryParams;
	}
	public void setQueryParams(String[] queryParams) {
		this.queryParams = queryParams;
	}
	
	@Override
	public String toString() {
		return "QueryVO [query=" + query + ", queryParams="
				+ Arrays.toString(queryParams) + "]";
	}
  
}
