/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  EndicaQueryVO.java
 *
 *  DESCRIPTION: Endeca Search Query VO 	
 *  HISTORY:
 *  16/03/12 Initial version
 *  	
 */
package com.bbb.search.endeca.vo;

//import com.endeca.content.ene.ENEContentQuery;
import com.endeca.navigation.ENEQuery;

/**
 * @author hbandl
 *
 */
public class EndecaQueryVO {

	private String queryString;
	//private ENEContentQuery endicaContentQuery;
	private ENEQuery queryObject;
	
	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}
	
	/**
	 * @return the queryObject
	 */
	public ENEQuery getQueryObject() {
		return queryObject;
	}

	/**
	 * @param queryObject the queryObject to set
	 */
	public void setQueryObject(ENEQuery queryObject) {
		this.queryObject = queryObject;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	
}
