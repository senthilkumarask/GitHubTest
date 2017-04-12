package com.bbb.account.profile;

import java.util.LinkedHashMap;
import java.util.Map;

import com.bbb.account.profile.vo.ProfileVO;
import com.bbb.account.profile.vo.RegistryVO;
import com.sapient.common.tests.BaseTestCase;

public class TestProfileFeedTools extends BaseTestCase {
	
	public void testSaveProfilesForProfileUser(){
		
		ProfileFeedTools profileFeedTools = (ProfileFeedTools)getObject("profileFeedTools");
		ProfileVO profileVo= new ProfileVO();
		String email = (int)(Math.random()*9999) + (String) getObject("email");
		String fName = (String) getObject("firstName");
		String lName = (String) getObject("lastName");
		String memberId = (String) getObject("memberId");
		String siteId = (String) getObject("siteId");
		String lastAccessedDate = (String) getObject("lastAccessedDate");
		
		profileVo.setEmail(email);
		profileVo.setFirstName(fName);
		profileVo.setLastName(lName);
		profileVo.setMemberId(memberId);
		profileVo.setSiteId(siteId);
		profileVo.setSiteId(siteId);
		profileVo.setId("1");
		profileVo.setLastModifiedDateAsString(lastAccessedDate);
		profileVo.setOptInFlag("TRUE");
		
		Map<String,ProfileVO> profileMap = new  LinkedHashMap<String,ProfileVO>(); 
		profileMap.put("1", profileVo);
		Map<String,ProfileVO> responseMap = profileFeedTools.saveProfiles(profileMap);
		assertNotNull(responseMap);
		assertTrue(responseMap.size()==1);
		assertFalse(profileVo.isError());
		assertNotNull(profileVo.getResponseMessage());
		assertNotNull(profileVo.getProfileId());
	}
	
	public void testSaveProfilesForRegistryUser(){
		
		ProfileFeedTools profileFeedTools = (ProfileFeedTools)getObject("profileFeedTools");
		ProfileVO vo= new ProfileVO();
		String email = (String) getObject("email");
		String fName = (String) getObject("firstName");
		String lName = (String) getObject("lastName");
		String siteId = (String) getObject("siteId");
		String lastAccessedDate = (String) getObject("lastAccessedDate");
		
		vo.setEmail((int)(Math.random()*9999)+email);
		vo.setFirstName(fName);
		vo.setLastName(lName);
		vo.setSiteId(siteId);
		vo.setId("1");
		vo.setLastModifiedDateAsString(lastAccessedDate);
		vo.setOptInFlag("TRUE");
		
		Map<String,ProfileVO> profileMap = new  LinkedHashMap<String,ProfileVO>(); 
		profileMap.put("1", vo);
		Map<String,ProfileVO> responseMap = profileFeedTools.saveProfiles(profileMap);
		assertNotNull(responseMap);
		assertTrue(responseMap.size()==1);
		assertFalse(vo.isError());
		assertNotNull(vo.getResponseMessage());
		assertNotNull(vo.getProfileId());
	}
	
	public void testSaveProfilesInvalidUser(){
		
		ProfileFeedTools profileFeedTools = (ProfileFeedTools)getObject("profileFeedTools");
		ProfileVO vo= new ProfileVO();
		String email = (String) getObject("email");
		String fName = (String) getObject("firstName");
		String lName = (String) getObject("lastName");
		String siteId = (String) getObject("siteId");
		String memberId = (String) getObject("memberId");
		String lastAccessedDate = (String) getObject("lastAccessedDate");
		
		vo.setEmail(email);
		vo.setFirstName(fName);
		vo.setLastName(lName);
		vo.setSiteId(siteId);
		vo.setMemberId(memberId);
		vo.setId("1");
		vo.setLastModifiedDateAsString(lastAccessedDate);
		vo.setOptInFlag("TRUE");
		
		Map<String,ProfileVO> profileMap = new  LinkedHashMap<String,ProfileVO>(); 
		profileMap.put("1", vo);
		Map<String,ProfileVO> responseMap = profileFeedTools.saveProfiles(profileMap);
		assertTrue(responseMap.isEmpty());
		assertTrue(vo.isError());
		assertNotNull(vo.getResponseMessage());
		assertNotNull(vo.getErrorCode());
		assertTrue("".equals(vo.getProfileId()));
	}
	
	public void testSaveRegistry(){
		
		ProfileFeedTools profileFeedTools = (ProfileFeedTools)getObject("profileFeedTools");
		ProfileVO profileVo= new ProfileVO();
		String email = (int)(Math.random()*9999) + (String) getObject("email");
		String fName = (String) getObject("firstName");
		String lName = (String) getObject("lastName");
		String siteId = (String) getObject("siteId");
		String lastAccessedDate = (String) getObject("lastAccessedDate");
		
		profileVo.setEmail(email);
		profileVo.setFirstName(fName);
		profileVo.setLastName(lName);
		profileVo.setSiteId(siteId);
		profileVo.setId("1");
		profileVo.setLastModifiedDateAsString(lastAccessedDate);
		profileVo.setOptInFlag("TRUE");
		
		RegistryVO regVo= new RegistryVO();
		regVo.setEmail(email);
		regVo.setEventDate((String) getObject("eventDate"));
		regVo.setRegistryType((String) getObject("eventType"));
		regVo.setRegistrantAsString("TRUE");
		regVo.setRegistryId((String) getObject("registryId"));
		regVo.setId("1");
		regVo.setSiteId(siteId);
		
		Map<String,ProfileVO> profileMap = new  LinkedHashMap<String,ProfileVO>(); 
		Map<String,RegistryVO> registryMap = new  LinkedHashMap<String,RegistryVO>();
		registryMap.put("1", regVo);
		profileMap.put("1", profileVo);
		Map<String,ProfileVO> responseMap = profileFeedTools.saveProfiles(profileMap);
		
		profileFeedTools.saveProfileRegistries(registryMap);
		assertFalse(regVo.isError());
	}
	public void testSaveRegistryInvalid(){
		
		ProfileFeedTools profileFeedTools = (ProfileFeedTools)getObject("profileFeedTools");
		ProfileVO profileVo= new ProfileVO();
		String email = (int)(Math.random()*9999) + (String) getObject("email");
		String fName = (String) getObject("firstName");
		String lName = (String) getObject("lastName");
		String siteId = (String) getObject("siteId");
		String lastAccessedDate = (String) getObject("lastAccessedDate");
		
		profileVo.setEmail(email);
		profileVo.setFirstName(fName);
		profileVo.setLastName(lName);
		profileVo.setSiteId(siteId);
		profileVo.setId("1");
		profileVo.setLastModifiedDateAsString(lastAccessedDate);
		profileVo.setOptInFlag("TRUE");
		
		RegistryVO regVo= new RegistryVO();
		regVo.setEmail(email);
		regVo.setEventDate((String) getObject("eventDate"));
		regVo.setRegistryType((String) getObject("eventType"));
		regVo.setRegistrantAsString("TRUE");
		regVo.setRegistryId((String) getObject("registryId"));
		regVo.setId("1");
		regVo.setSiteId(siteId);
		
		Map<String,ProfileVO> profileMap = new  LinkedHashMap<String,ProfileVO>(); 
		Map<String,RegistryVO> registryMap = new  LinkedHashMap<String,RegistryVO>();
		registryMap.put("1", regVo);
		profileMap.put("1", profileVo);
		Map<String,ProfileVO> responseMap = profileFeedTools.saveProfiles(profileMap);
		
		profileFeedTools.saveProfileRegistries(registryMap);
		assertTrue(regVo.isError());
	}
	
	public void testCoRegistrantLinking(){
		
		ProfileFeedTools profileFeedTools = (ProfileFeedTools)getObject("profileFeedTools");
		ProfileVO profileVo= new ProfileVO();
		ProfileVO profileVo1= new ProfileVO();
		String email = (int)(Math.random()*9999) + (String) getObject("email");
		String email2 = (int)(Math.random()*9999) + (String) getObject("email");
		String fName = (String) getObject("firstName");
		String lName = (String) getObject("lastName");
		String siteId = (String) getObject("siteId");
		String lastAccessedDate = (String) getObject("lastAccessedDate");
		
		profileVo.setEmail(email);
		profileVo.setFirstName(fName);
		profileVo.setLastName(lName);
		profileVo.setSiteId(siteId);
		profileVo.setId("1");
		profileVo.setLastModifiedDateAsString(lastAccessedDate);
		profileVo.setOptInFlag("TRUE");
		
		profileVo1.setEmail(email2);
		profileVo1.setFirstName(fName);
		profileVo1.setLastName(lName);
		profileVo1.setSiteId(siteId);
		profileVo1.setId("2");
		profileVo1.setLastModifiedDateAsString(lastAccessedDate);
		profileVo1.setOptInFlag("TRUE");
		
		RegistryVO regVo= new RegistryVO();
		regVo.setEmail(email);
		regVo.setEventDate((String) getObject("eventDate"));
		regVo.setRegistryType((String) getObject("eventType"));
		regVo.setRegistrantAsString("TRUE");
		regVo.setRegistryId((String) getObject("registryId"));
		regVo.setId("1");
		regVo.setSiteId(siteId);
		
		RegistryVO regVo1= new RegistryVO();
		regVo1.setEmail(email2);
		regVo1.setEventDate((String) getObject("eventDate"));
		regVo1.setRegistryType((String) getObject("eventType"));
		regVo1.setRegistrantAsString("FALSE");
		regVo1.setRegistryId((String) getObject("registryId"));
		regVo1.setId("2");
		regVo1.setSiteId(siteId);
		
		Map<String,ProfileVO> profileMap = new  LinkedHashMap<String,ProfileVO>(); 
		Map<String,RegistryVO> registryMap = new  LinkedHashMap<String,RegistryVO>();
		registryMap.put(regVo.getId(), regVo);
		profileMap.put(profileVo.getId(), profileVo);
		profileMap.put(profileVo1.getId(), profileVo1);
		Map<String,ProfileVO> responseMap = profileFeedTools.saveProfiles(profileMap);
		profileFeedTools.saveProfileRegistries(registryMap);
		registryMap.clear();
		registryMap.put(regVo1.getId(), regVo1);		
		profileFeedTools.linkCoRegistrant(registryMap);
		assertFalse(regVo1.isError());
	}
}

