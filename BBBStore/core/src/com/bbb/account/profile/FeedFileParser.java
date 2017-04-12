/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  FeedFileParser.java
 *
 *  DESCRIPTION: Interface that provides feature to read write feed file for profile/
 *  registry migration 
 *   	
 *  HISTORY:
 *  29/03/12 Initial version
 *  	
 */
package com.bbb.account.profile;

import java.util.Map;

import com.bbb.account.profile.vo.ProfileVO;
import com.bbb.account.profile.vo.RegistryVO;
import com.bbb.exception.BBBSystemException;

/**
 * @author hbandl
 *
 */
public interface FeedFileParser {
	
	/**
	 * method to read profile feed for profile migration
	 * @return map of profile objects corresponding to feed file data 
	 */
	public Map<String, ProfileVO> readProfileFeed() throws BBBSystemException;
	
	/**
	 * method to read registry feed for registry migration
	 * 
	 * @return collection of profile that include their registry details
	 */
	public Map<String, RegistryVO> readRegistryFeed() throws BBBSystemException;

	/**
	 * method to write response of profile migration.
	 * 
	 * @param profileResponseVOs list of responses object that need to write into the feed file
	 * @return status of response file writing
	 */
	public boolean writeProfileFeedResponse(Map<String, ProfileVO> profileResponseVOs) throws BBBSystemException;
	
	/**
	 * method to write response of registry migration.
	 * 
	 * @param registryResponseVOs map of responses object that need to write into the feed file
	 * @param isRegistrantResponse flag if the response file is for registrant/co-registrant feed
	 * @return status of response file writing
	 */
	public boolean writeRegistryFeedResponse(Map<String, RegistryVO> registryResponseVOs, boolean isRegistrantResponse) throws BBBSystemException;
	
	
}
