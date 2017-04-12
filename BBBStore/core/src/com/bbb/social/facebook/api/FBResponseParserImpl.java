package com.bbb.social.facebook.api;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.bbb.common.BBBGenericService;

import com.bbb.exception.BBBBusinessException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.social.facebook.FBConstants;
import com.bbb.social.facebook.api.vo.FBEducationVO;
import com.bbb.social.facebook.api.vo.FBErrorVO;
import com.bbb.social.facebook.api.vo.FBFriendResponseVO;
import com.bbb.social.facebook.api.vo.FBSchoolVO;
import com.bbb.social.facebook.api.vo.FBUserVO;
import com.bbb.social.facebook.vo.SchoolVO;
import com.bbb.social.facebook.vo.UserVO;
import com.google.gson.Gson;

/**
 * Implements FBResponser parser
 * 
 * @author hbandl
 * @version 1.0
 */
public class FBResponseParserImpl extends BBBGenericService implements FBResponseParser {
	
	
	/* (non-Javadoc)
	 * @see com.bbb.social.facebook.api.FBResponseParser#parseFacebookFriends(java.lang.String)
	 */
	public FBFriendResponseVO parseFacebookFriends(String pFbFriendJson)
			throws BBBBusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	
	/* (non-Javadoc)
	 * @see com.bbb.social.facebook.api.FBResponseParser#parseFacebookBasicInfo(java.lang.String)
	 */
	public UserVO parseFacebookBasicInfo(String pFbBasicInfoJson) throws BBBBusinessException {
		
		String opName = "FBResponseParserImpl.parseFacebookBasicInfo()";
        BBBPerformanceMonitor.start(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
	
		logDebug(opName + ": Start") ;
		
		
		UserVO userVO = null;
		if(pFbBasicInfoJson != null && !pFbBasicInfoJson.equalsIgnoreCase(FBConstants.EMPTY_STRING)){
			
			logDebug("Parsing json response start :\n" + pFbBasicInfoJson) ;
			
			
			Gson gson = new Gson();
			if(isFacebookError(pFbBasicInfoJson)) {
				FBErrorVO fbError = gson.fromJson(pFbBasicInfoJson, FBErrorVO.class);
				String fbErrorMessage = FBConstants.FB_ERROR_PARSE_BASIC_INFO;
				if(fbError != null) {
					fbErrorMessage = fbError.getMsg();
				}
				BBBPerformanceMonitor.cancel(opName);
				throw new BBBBusinessException(FBConstants.FB_ERROR_PARSE_BASIC_INFO,fbErrorMessage);
			} else {
				/*Unmarshall the Facebook Graph API JSON response*/
				FBUserVO fbUserVO = gson.fromJson(pFbBasicInfoJson, FBUserVO.class);
				if(fbUserVO != null){
					userVO = new UserVO();
					convertFBUserToBBBUser(userVO, fbUserVO);
				}
			}
		}
		
		logDebug(opName + ": End") ;
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
		return userVO;
	}
	
	/*
	 * Transforms Facebook Graph API response objects to appropriate value objects. It also
	 * retrieves Facebook friends list for the given user.
	 */
	private void convertFBUserToBBBUser(UserVO pUser, FBUserVO pFBUser) {
		
		String opName = "FBResponseParserImpl.convertFBUserToBBBUser()";
        BBBPerformanceMonitor.start(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
        
		pUser.setUserName(pFBUser.getId());
		pUser.setName(pFBUser.getName());
		pUser.setFirstName(pFBUser.getFirstName());
		pUser.setMiddleName(pFBUser.getMiddleName());
		pUser.setLastName(pFBUser.getLastName());
		pUser.setGender(pFBUser.getGender());
		pUser.setTokenValid(true);
		pUser.setVerified(pFBUser.isVerified());
		pUser.setEmail(pFBUser.getEmail());
		
		/*Retrieve Facebook profile's education updates*/
		List<FBEducationVO> fbEducations = pFBUser.getEducation();
		if (fbEducations != null) {
			SchoolVO school = null;
			FBSchoolVO fbSchool = null;
			FBEducationVO fbEducation = null;
			Set<SchoolVO> newSchools = new HashSet<SchoolVO>();
			Set<SchoolVO> schools = pUser.getSchools();
			if(schools == null){
				schools = new HashSet<SchoolVO>();
			}
			for (int index = 0; index < fbEducations.size(); index++) {
				fbEducation = fbEducations.get(index);
				fbSchool = fbEducation.getSchool();
				boolean exists = false;
				for(Iterator<SchoolVO> schoolIter = schools.iterator(); schoolIter.hasNext();){
					school = schoolIter.next();
					
					if(StringUtils.equals(school.getSchoolID(), fbSchool.getId())){
						exists = true;
					}
				}
				if(!exists){
					school = new SchoolVO();
					school.setSchoolID(fbSchool.getId());
				}
				
				school.setName(fbSchool.getName());

				newSchools.add(school);
			}

			pUser.setSchools(newSchools);
		}
		
        BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
	}
	
	/*
	 * Checks if the Facebook graph API returned a JSON error response
	 */
	private boolean isFacebookError(String response){
		boolean error = true;
		final String errorString = "\"error\":";
		if(!StringUtils.isBlank(response) && atg.core.util.StringUtils.numOccurrences(response, errorString) == 0) {
			error = false;
		}
		return error;
	}
	
}