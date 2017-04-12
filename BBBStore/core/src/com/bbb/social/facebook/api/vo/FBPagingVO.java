package com.bbb.social.facebook.api.vo;

import java.io.Serializable;

/**
 * 
 * @author manohar
 * @version 1.0
 */
public class FBPagingVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String next;

	/**
	 * 
	 * @return
	 */
	public String getNext() {
		return next;
	}

	/**
	 * 
	 * @param next
	 */
	public void setNext(String next) {
		this.next = next;
	}

}
