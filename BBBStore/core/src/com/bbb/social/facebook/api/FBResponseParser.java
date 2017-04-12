package com.bbb.social.facebook.api;


import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.social.facebook.api.vo.FBFriendResponseVO;
import com.bbb.social.facebook.vo.UserVO;

/**
 * Interface that parse the different Facebook response into 
 * corresponding object.
 * 
 * @author hbandl
 * @version 1.0
 */
public interface FBResponseParser {
	
	/**
	 * method that parse Facebook friends list response into 
	 * corresponding object 
	 * 
	 * @param pFbFriendJson friends json string 
	 * @return FBFriendResponseVO as response
	 * @throws BBBBusinessException throws exception in case of any parsing error.
	 */
	public FBFriendResponseVO parseFacebookFriends(String pFbFriendJson) throws BBBBusinessException;

	/**
	 * method that parse Facebook basic information response into 
	 * corresponding object 
	 * 
	 * @param pFbBasicInfoJson basic information json string
	 * @return UserVO as response
	 * @throws BBBSystemException throws exception in case of any parsing error.
	 */
	public UserVO parseFacebookBasicInfo(String pFbBasicInfoJson) throws BBBBusinessException;
	
	
}
