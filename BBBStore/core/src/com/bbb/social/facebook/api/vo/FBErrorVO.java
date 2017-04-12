package com.bbb.social.facebook.api.vo;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author manohar
 * @version 1.0
 */
public class FBErrorVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@SerializedName("message")
	private String msg;

	/**
	 * 
	 */
	private String type;

	/**
	 * 
	 * @return
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * 
	 * @param msg
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
}
