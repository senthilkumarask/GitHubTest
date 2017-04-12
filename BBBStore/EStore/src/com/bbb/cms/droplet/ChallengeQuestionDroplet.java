/**
 * 
 */
package com.bbb.cms.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import atg.core.util.Base64;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * @author asha60
 * This Droplet will fetch the challengeQuestion setup in BCC ,to display
 * to user on personal info page.Also will fetch the user challengeQuestion preselected in 
 * reset password flow.
 * 
 *
 */
public class ChallengeQuestionDroplet extends BBBDynamoServlet {
	
	private BBBCatalogTools catalogTools;
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}





	private BBBProfileTools profileTools;

	/* ===================================================== *
	GETTERS and SETTERS
* ===================================================== */
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
	  * This service method  will fetch the challengeQuestion setup in BCC ,to display
	  * to user on personal info page.Also will fetch the user challengeQuestion preselected in 
	  * reset password flow
	 * @param req
	 * @param res
	 * @return void
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		
	  try{
			
			if (isLoggingDebug()) {
				logDebug("ChallengeQuestionDroplet Service method : Start");
			}
			
			Map<String,String> challengeQuestionMapSelected= new HashMap<String, String>();
			MutableRepositoryItem challengQuestionItem=null;
			if(null !=req.getParameter(BBBCoreConstants.FORGET_EMAIL)){
				req.setParameter(BBBCoreConstants.FORGET_EMAIL, BBBUtility.toLowerCase(req.getParameter(BBBCoreConstants.FORGET_EMAIL)));				
			}
			
			if(null !=req.getParameter(BBBCoreConstants.FORGET_EMAIL) || ((null !=req.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL) && StringUtils.isEmpty(req.getParameter(BBBCoreConstants.FROM_LOGIN_PAGE))))){
				if(null !=req.getParameter(BBBCoreConstants.FORGET_EMAIL) )
				{
					req.getSession().setAttribute(BBBCoreConstants.FORGET_EMAIL, req.getParameter(BBBCoreConstants.FORGET_EMAIL));
				}
				
				//Profile profile = (Profile) req.resolveName(BBBCoreConstants.ATG_PROFILE);
				RepositoryItem userItemFromEmail =getProfileTools().getItemFromEmail((String)req.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL));
				
				challengQuestionItem=	getProfileTools().fetchProfileIdFromChallengeQuestionRepository((String)userItemFromEmail.getPropertyValue(BBBCoreConstants.ID));
				if(null !=challengQuestionItem){
				if(null !=challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1)) {
					 req.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1,  getProfileTools().fetchChallengeQuestionsFromRepository().get(BBBCoreConstants.STRING_ONE).get(challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1)));
					}
				if(null !=challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2)) {
					  req.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2, getProfileTools().fetchChallengeQuestionsFromRepository().get(BBBCoreConstants.STRING_TWO).get(challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2)));
					}				
					req.serviceParameter(BBBCoreConstants.OUTPUT, req, res);
				
			}}
			else
			{	
			
			Profile profile = (Profile) req.resolveName(BBBCoreConstants.ATG_PROFILE);
			 challengQuestionItem=	getProfileTools().fetchProfileIdFromChallengeQuestionRepository((String)profile.getPropertyValue(BBBCoreConstants.ID));
			if(null !=challengQuestionItem){			
				req.setParameter("challengQuestionExist", true);
			if(null !=challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1)) {
				challengeQuestionMapSelected.put(BBBCoreConstants.PREFERRED_QUESTION_1, (String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1));
				  req.setParameter(BBBCoreConstants.PREFERRED_QUESTION_1, (String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_1));
				}
			if(null !=challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2)) {
				challengeQuestionMapSelected.put(BBBCoreConstants.PREFERRED_QUESTION_2, (String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2));
				  req.setParameter(BBBCoreConstants.PREFERRED_QUESTION_2, (String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_QUESTION_2));
				}
			if(null !=challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_1)) {
				challengeQuestionMapSelected.put(BBBCoreConstants.PREFERRED_ANSWER_1, (String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_1));
				  req.setParameter(BBBCoreConstants.PREFERRED_ANSWER_1, Base64.decodeToString((String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_1)));
				}
			if(null !=challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_2)) {
				challengeQuestionMapSelected.put(BBBCoreConstants.PREFERRED_ANSWER_2, (String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_2));
				  req.setParameter(BBBCoreConstants.PREFERRED_ANSWER_2, Base64.decodeToString((String)challengQuestionItem.getPropertyValue(BBBCoreConstants.CHALLENGE_ANSWER_2)));
				}
					}
			else
			{
				req.setParameter("challengQuestionExist", false);
			}
			  req.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP, challengeQuestionMapSelected);
		
			
			  Map<String,Map<String,String>> challengeQuestionsMap=  getProfileTools().fetchChallengeQuestionsFromRepository();  
			 req.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP1, challengeQuestionsMap.get(BBBCoreConstants.STRING_ONE));
			 req.setParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP2, challengeQuestionsMap.get(BBBCoreConstants.STRING_TWO));
			 req.serviceParameter(BBBCoreConstants.OUTPUT, req, res);
			
				 if (isLoggingDebug()) {
					logDebug("ChallengeQuestionDroplet Service method : End");
				}
			}
	}catch(Exception e){
		  logError("Challenge question service failed", e);
		  req.serviceParameter(BBBCoreConstants.ERROR, req, res);
	}
}

	public Map<String,Object> fetchchallengeQuestion(String emailId) throws BBBSystemException{
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
				
		Map<String,Object> challengeQuestions= new HashMap<String, Object>();
			//calls the "service" method to fetch the details of the legacy order
		//boolean challengQuestionExist = Boolean.parseBoolean(pRequest.getParameter("challengQuestionExist"));
		
			try {
				if(!BBBUtility.isEmpty(emailId)) pRequest.setParameter(BBBCoreConstants.FORGET_EMAIL, BBBUtility.toLowerCase(emailId));
				service(pRequest, pResponse);
				if(null !=pRequest.getParameter(BBBCoreConstants.FORGET_EMAIL) || ((null !=pRequest.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL) && StringUtils.isEmpty(pRequest.getParameter(BBBCoreConstants.FROM_LOGIN_PAGE)))))
				{
					challengeQuestions.put("PreferredQuestion1", pRequest.getParameter(BBBCoreConstants.PREFERRED_QUESTION_1));
					challengeQuestions.put("PreferredQuestion2", pRequest.getParameter(BBBCoreConstants.PREFERRED_QUESTION_2));
				}
				else
				{
					
				challengeQuestions.put("challengQuestionExist", pRequest.getParameter("challengQuestionExist"));
				challengeQuestions.put("PreferredQuestion1", pRequest.getParameter(BBBCoreConstants.PREFERRED_QUESTION_1));
				challengeQuestions.put("PreferredQuestion2", pRequest.getParameter(BBBCoreConstants.PREFERRED_QUESTION_2));
				challengeQuestions.put("PreferredAnswer1", pRequest.getParameter(BBBCoreConstants.PREFERRED_ANSWER_1));
				challengeQuestions.put("PreferredAnswer2", pRequest.getParameter(BBBCoreConstants.PREFERRED_ANSWER_2));
				challengeQuestions.put("challengeQuestionsMap1", pRequest.getObjectParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP1));
				challengeQuestions.put("challengeQuestionsMap2", pRequest.getObjectParameter(BBBCoreConstants.CHALLENGE_QUESTION_MAP2));
				}
				if((null !=pRequest.getSession().getAttribute(BBBCoreConstants.FORGET_EMAIL)))
						{
					pRequest.getSession().removeAttribute(BBBCoreConstants.FORGET_EMAIL);
						}
			} catch (ServletException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_Legacy_Details, "Exception occurred while fetching the legacy order details");
		}catch (IOException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_Legacy_Details,"Exception occurred while fetching the legacy order details");
		}	
			return challengeQuestions;
}
}
