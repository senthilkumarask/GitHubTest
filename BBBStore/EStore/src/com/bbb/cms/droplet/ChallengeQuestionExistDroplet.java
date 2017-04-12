/**
 * 
 */
package com.bbb.cms.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.OrderHolder;
import atg.repository.MutableRepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * @author asha60
 * This droplet will check is user has challenge Question setup in his profile or not
 * accordingly show the message on personal info page.
 *
 */
public class ChallengeQuestionExistDroplet extends BBBDynamoServlet {
	private BBBProfileTools profileTools;
	public static final String PROFILE_ID = "profile";
	/**
	 * @return the profileTools
	 */
	public BBBProfileTools getProfileTools() {
		return profileTools;
	}

	/**
	 * @param profileTools the profileTools to set
	 */
	public void setProfileTools(BBBProfileTools profileTools) {
		this.profileTools = profileTools;
	}

	/**
	  * This service method  will check is user has challenge Question setup in his profile or not
	  * accordingly show the message on personal info page.
	 * @param req
	 * @param res
	 * @return void
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("ChallengeQuestionExistDroplet Service method : Start");
		}
		String profileId=req.getParameter(BBBCoreConstants.CHALLENGE_QUESTION_PROFILE_ID);
		
		MutableRepositoryItem challengQuestionItem=null;
		
		challengQuestionItem=	getProfileTools().fetchProfileIdFromChallengeQuestionRepository(profileId);
		if(null == challengQuestionItem ){
			req.setParameter("ChallengeQuestionNotExist","Challenge Question didnot setup, please setup the question in below form");
			if (isLoggingDebug()) {
				logDebug("ChallengeQuestionExistDroplet Service method : End");
			}
			req.serviceParameter(BBBCoreConstants.OUTPUT, req, res);
			
		}
	}
	
	public Map<String,Object> getChallengeQuestion() throws BBBSystemException{
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		Map<String,Object> challengeQuestions= new HashMap<String,Object>();
		Profile profile= (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);			
		pRequest.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_PROFILE_ID, profile.getRepositoryId());			
			//calls the "service" method to fetch the details of the legacy order
			try {
				service(pRequest, pResponse);			
			} catch (ServletException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_Legacy_Details, "Exception occurred while fetching the legacy order details");
		}catch (IOException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_Legacy_Details,"Exception occurred while fetching the legacy order details");
		}	
			String challengeQuestionNotExist = pRequest.getParameter("ChallengeQuestionNotExist");
			if(BBBUtility.isEmpty(challengeQuestionNotExist)){
				challengeQuestions.put("challengeQuestionNotExist",false);
			}
			else{
				challengeQuestions.put("challengeQuestionNotExist",true);
			}
			return challengeQuestions;
}
}
