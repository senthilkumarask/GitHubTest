package com.bbb.account.profile;

import java.util.HashMap;
import java.util.Map;

import com.bbb.account.profile.vo.ProfileVO;
import com.bbb.account.profile.vo.RegistryVO;
import com.sapient.common.tests.BaseTestCase;

public class TestFeedFileParser extends BaseTestCase {
	
	public void testWriteProfileFeedResponse() throws Exception {
		
		FeedFileParserImpl feedFileParser = (FeedFileParserImpl)getObject("feedFileParser");
		
		String profileFeedSuccessResponseLocation = (String) getObject("profileFeedSuccessResponseLocation");
		String profileFeedFailureResponseLocation = (String) getObject("profileFeedFailureResponseLocation");
		feedFileParser.setProfileFeedSuccessResponseLocation(profileFeedSuccessResponseLocation);
		feedFileParser.setProfileFeedFailureResponseLocation(profileFeedFailureResponseLocation);
		
		ProfileVO profile = new ProfileVO();
		profile.setId((String) getObject("id"));
		profile.setMemberId((String) getObject("memberId"));
		profile.setEmail((String) getObject("email"));
		profile.setSiteId((String) getObject("siteId"));
		profile.setFirstName((String) getObject("firstName"));
		profile.setLastName((String) getObject("lastName"));
		profile.setPhoneNumber((String) getObject("phoneNumber"));
		profile.setMobileNumber((String) getObject("mobileNumber"));
		profile.setOptInFlag((String) getObject("optInFlag"));
		profile.setLastModifiedDateAsString((String) getObject("lastModifiedDateAsString"));
		profile.setFavStoreId((String) getObject("favStoreId"));
		profile.setProfileId((String) getObject("profileId"));
		profile.setError(false);
		profile.setErrorCode((String) getObject("errorCode"));
		profile.setResponseMessage((String) getObject("responseMessage"));
		
		Map<String, ProfileVO> profileResponseVOs = new HashMap<String, ProfileVO>();
		profileResponseVOs.put(profile.getId(), profile);
		boolean status = feedFileParser.writeProfileFeedResponse(profileResponseVOs);
		
		assertTrue(status);
		
	}
	
	public void testReadProfileFeed() throws Exception {
		
		FeedFileParserImpl feedFileParser = (FeedFileParserImpl)getObject("feedFileParser");
		
		String profileFeedLocation = (String) getObject("profileFeedLocation");
		feedFileParser.setProfileFeedLocation(profileFeedLocation);
		String id = (String) getObject("id");
		
		Map<String, ProfileVO> profileResponseVOs = feedFileParser.readProfileFeed();
		assertNotNull(profileResponseVOs);
		assertTrue(profileResponseVOs.containsKey(id));
		ProfileVO profile = profileResponseVOs.get(id);
		assertNotNull(profile);
		
	}
	
	public void testWriteRegistryAndCoRegistrantResponseFeed() throws Exception {
		
		FeedFileParserImpl feedFileParser = (FeedFileParserImpl)getObject("feedFileParser");
		
		String registryFeedSuccessResponseLocation = (String) getObject("registryFeedSuccessResponseLocation");
		String coregistrantFeedSuccessResponseLocation = (String) getObject("coregistrantFeedSuccessResponseLocation");
		
		String registryFeedFailureResponseLocation = (String) getObject("registryFeedFailureResponseLocation");
		String coregistrantFeedFailureResponseLocation = (String) getObject("coregistrantFeedFailureResponseLocation");
		
		feedFileParser.setRegistryFeedSuccessResponseLocation(registryFeedSuccessResponseLocation);
		feedFileParser.setCoregistrantFeedSuccessResponseLocation(coregistrantFeedSuccessResponseLocation);
		
		feedFileParser.setRegistryFeedFailureResponseLocation(registryFeedFailureResponseLocation);
		feedFileParser.setCoregistrantFeedFailureResponseLocation(coregistrantFeedFailureResponseLocation);
		
		RegistryVO registry = new RegistryVO();
		
		registry.setId((String) getObject("id"));
		registry.setRegistryId((String) getObject("registryId"));
		registry.setEmail((String) getObject("email"));
		registry.setSiteId((String) getObject("siteId"));
		registry.setRegistryType((String) getObject("registryType"));
		registry.setEventDate((String) getObject("eventDate"));
		registry.setRegistrantAsString((String) getObject("isRegistrant"));
		registry.setProfileId((String) getObject("profileId"));
		registry.setError(false);
		registry.setErrorCode((String) getObject("errorCode"));
		registry.setResponseMessage((String) getObject("responseMessage"));
		
		Map<String, RegistryVO> registryResponseVOs = new HashMap<String, RegistryVO>();
		registryResponseVOs.put(registry.getId(), registry);
		boolean registryResponse = feedFileParser.writeRegistryFeedResponse(registryResponseVOs, true);
		boolean coRegistrantResponse = feedFileParser.writeRegistryFeedResponse(registryResponseVOs, false);
		
		assertTrue(registryResponse);
		assertTrue(coRegistrantResponse);
		
	}

	public void testReadRegistryFeed() throws Exception {
		
		FeedFileParserImpl feedFileParser = (FeedFileParserImpl)getObject("feedFileParser");
		String registryFeedLocation = (String) getObject("registryFeedResponseLocation");
		feedFileParser.setRegistryFeedLocation(registryFeedLocation);
		String id = (String) getObject("id");
		
		Map<String, RegistryVO> registryResponseVOs = feedFileParser.readRegistryFeed();
		assertNotNull(registryResponseVOs);
		assertTrue(registryResponseVOs.containsKey(id));
		RegistryVO registry = registryResponseVOs.get(id);
		assertNotNull(registry);
		
	}
	
	
}	

