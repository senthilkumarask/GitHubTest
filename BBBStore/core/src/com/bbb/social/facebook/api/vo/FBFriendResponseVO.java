package com.bbb.social.facebook.api.vo;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author manohar
 * @version 1.0
 */
public class FBFriendResponseVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@SerializedName("data")
	private List<FBFriendVO> friends;

	/**
	 * 
	 */
	private FBPagingVO paging;

	/**
	 * 
	 */
	private FBErrorVO error;

	/**
	 * 
	 * @return
	 */
	public List<FBFriendVO> getFriends() {
		return friends;
	}

	/**
	 * 
	 * @param friends
	 */
	public void setFriends(List<FBFriendVO> friends) {
		this.friends = friends;
	}

	/**
	 * 
	 * @return
	 */
	public FBPagingVO getPaging() {
		return paging;
	}

	/**
	 * 
	 * @param paging
	 */
	public void setPaging(FBPagingVO paging) {
		this.paging = paging;
	}

	/**
	 * 
	 * @return
	 */
	public FBErrorVO getError() {
		return error;
	}

	/**
	 * 
	 * @param error
	 */
	public void setError(FBErrorVO error) {
		this.error = error;
	}

}
