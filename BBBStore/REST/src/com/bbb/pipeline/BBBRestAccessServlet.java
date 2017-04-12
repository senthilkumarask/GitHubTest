/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBRestAccessServlet.java
 *
 *  DESCRIPTION: A pipeline component handles the Rest related service
 *  HISTORY:
 *  31/11/12 Initial version
 *
 */
package com.bbb.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.rest.RestException;
import atg.rest.processor.RestSecurityProcessor;
import atg.rest.servlet.RestPipelineServlet;
import atg.security.BBBRestClientPersona;
import atg.security.Persona;
import atg.security.ThreadSecurityManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userdirectory.UserDirectoryUserAuthority;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.RestConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 * DESCRIPTION: A pipeline component handles the Rest related service
 * 
 * @author akhaju
 */
public class BBBRestAccessServlet extends RestPipelineServlet {
	private UserDirectoryUserAuthority mUserAuthority;
	private boolean mRestCustomSecurityEnable;
	private Repository mRepository;
	private boolean mHttpsEnabled;
	private RestSecurityProcessor mRestSecurityProcessor;

	/**
	 * @return the userAuthority
	 */
	public UserDirectoryUserAuthority getUserAuthority() {
		return mUserAuthority;
	}

	/**
	 * @return the restSecurityProcessor
	 */
	public RestSecurityProcessor getRestSecurityProcessor() {
		return mRestSecurityProcessor;
	}

	/**
	 * @param pRestSecurityProcessor
	 *            the restSecurityProcessor to set
	 */
	public void setRestSecurityProcessor(RestSecurityProcessor pRestSecurityProcessor) {
		mRestSecurityProcessor = pRestSecurityProcessor;
	}

	/**
	 * @param pUserAuthority
	 *            the userAuthority to set
	 */
	public void setUserAuthority(UserDirectoryUserAuthority pUserAuthority) {
		mUserAuthority = pUserAuthority;
	}

	public boolean isRestCustomSecurityEnable() {
		return mRestCustomSecurityEnable;
	}

	public void setRestCustomSecurityEnable(boolean pRestCustomSecurityEnable) {
		mRestCustomSecurityEnable = pRestCustomSecurityEnable;
	}

	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return mRepository;
	}

	/**
	 * @param pRepository
	 *            the repository to set
	 */
	public void setRepository(Repository pRepository) {
		mRepository = pRepository;
	}

	/**
	 * @return the httpsEnabled
	 */
	public boolean isHttpsEnabled() {
		return mHttpsEnabled;
	}

	/**
	 * @param pHttpsEnabled
	 *            the httpsEnabled to set
	 */
	public void setHttpsEnabled(boolean pHttpsEnabled) {
		mHttpsEnabled = pHttpsEnabled;
	}

	/**
	 * @return the secureURL
	 */

	/**
	 * OverRideded method from BBAccessControlerServlet and does Authentication
	 * task for Rest Services according to the business rules
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with servlet io
	 * @return void
	 */
	public void serviceRESTRequest(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException, RestException {
		String methodName = RestConstants.SERVICE;
		BBBPerformanceMonitor.start(RestConstants.BBB_REST_SERVICE, methodName);
		if (isLoggingDebug()) {
			logDebug("BBBRestAccessServlet.service()  method started");
		}

		String uri = pRequest.getRequestURI();
		String request_type = null;

		try {
			// restSecurityEnable will be enabled from
			// /atg/rest/processor/RestSecurityProcessor.enabled ATG OOTB
			// Security frame work
			if (getRestSecurityProcessor().isEnabled()) {
				if (isLoggingDebug()) {
					logDebug("rest security :  "+getRestSecurityProcessor().isEnabled());
				}
				String clientID = pRequest.getHeader(RestConstants.CLIENTID);
				String token = pRequest.getHeader(RestConstants.TOKEN);
				String siteId = SiteContextManager.getCurrentSiteId();
				Map<String,BBBRestClientPersona> personaMap = new HashMap<String,BBBRestClientPersona>();
				
				if (isLoggingDebug()) {
					logDebug("Client ID ====  "+clientID);
					logDebug("token ID ====  "+token);
					logDebug("siteId ID ====  "+siteId);
				}
				
				Persona[] perlist = ThreadSecurityManager.currentUser().getPersonae();
				if(perlist !=null && perlist.length>0){
					// All the associated personae which are instance of BBBRestClientPersona gets removed from User for each request
					for(Persona persona : perlist ){
						if (isLoggingDebug()) {
							logDebug("User already has persona :  "+persona.getName());
						}
						if(persona instanceof BBBRestClientPersona){
							if (isLoggingDebug()) {
								logDebug("User already has BBBRestClientPersona :  "+persona.getName());
							}
							personaMap.put(((BBBRestClientPersona) persona).getRoleName(), (BBBRestClientPersona)persona);
							//ThreadSecurityManager.currentUser().removePersona(persona);
						}
					}
					if (isLoggingDebug()) {
						logDebug("Current User :  "+ThreadSecurityManager.currentUser());
					}
				}
				
				// Below code get the repository item from the database
				// Based on the Client Id
				RepositoryItem[] items = checkClient(clientID);
				if (items == null) {
					logError("BBBRestAccessServlet.service()- Error Code = " + 403 + " ERROR MESSAGE = " + RestConstants.ERROR_INVALID_CLIENT_ID);
					throw new RestException(RestConstants.ERROR_INVALID_CLIENT_ID, pResponse, 403);
				}
				//checks if "token" is supplied in the request or not.. If yes, then it must match the token value associated with the client in the repository
				if(token !=null && !token.equals((String)items[0].getPropertyValue(RestConstants.CLIENT_TOKEN))){
					logError("BBBRestAccessServlet.service()- Error Code = " + 403 + " ERROR MESSAGE = " + RestConstants.ERROR_INVALID_TOKEN);
					throw new RestException(RestConstants.ERROR_INVALID_TOKEN, pResponse, 403);
				}
				if (isLoggingDebug()) {
					if(BBBUtility.isNotEmpty(token)){
					logDebug("Valid token supplied for the current client");
					}
				}
				
				
				// Iterate the list of roles assigned
				List<RepositoryItem> role = (List<RepositoryItem>) items[0].getPropertyValue(RestConstants.ROLE);
			
				
				if (role != null && !role.isEmpty()) {
					if (isLoggingDebug()) {
						logDebug("Found : Associated roles with User");
						logDebug("Calling : addPersonaToUserProfile ");
					}
					addPersonaToUserProfile(role, clientID,token,siteId,personaMap);
					
				} else {
					if (isLoggingDebug()) {
						logError("No Role Assigned to client");
					}
				}
			} 
		} catch (RepositoryException e) {
			logError("BBBRestAccessServlet.service()- Error Code = " + 500 + " ERROR MESSAGE = " + RestConstants.INTERNAL_ERROR);
			logError("Exception in BBBRestAccessServlet.service()" + e.getMessage());
			throw new RestException(RestConstants.INTERNAL_ERROR, pResponse, 500);
		}
		finally
		{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ACCESS_CONTROLLER, methodName);
		}
		if (isLoggingDebug()) {
			logDebug("BBBRestAccessServlet.service() method ends");
		}
	}

	/**
	 * The method will get the repository item from database corresponding to
	 * token and clienID
	 * 
	 * @param pClientID
	 * @param pToken
	 * @return RepositoryItem[]
	 * @throws RepositoryException
	 */
	private RepositoryItem[] checkClient(String pClientID) throws RepositoryException {
		if (isLoggingDebug()) {
			logDebug("BBBRestAccessServlet.checkClient() method starts");			
		}
		RepositoryItem[] item = null;
		if (pClientID != null) {
			RepositoryItemDescriptor repoItemDES = mRepository.getItemDescriptor(RestConstants.CLIENT_INFO);
			RepositoryView view = mRepository.getView(repoItemDES);
			RqlStatement stat = null;
			Object[] obj = new Object[BBBCoreConstants.ONE];
			obj[0] = pClientID;
				stat = RqlStatement.parseRqlStatement(RestConstants.CLIENT_ID_QUERY);
			item = stat.executeQuery(view, obj);
		}
		if (isLoggingDebug()) {
			logDebug("BBBRestAccessServlet.checkClient() retuns = " + item);
			logDebug("BBBRestAccessServlet.checkClient() method ends");
		}
		return item;
	}

	/**
	 * Below Code iterates over Client role and add role to current user persona
	 * 
	 * @param pRole
	 * @param pClientID
	 * @param pToken
	 * @param pSiteId
	 * @throws RestException
	 */
	private void addPersonaToUserProfile(List<RepositoryItem> pRole, String pClientID, String pToken,String pSiteId, Map<String,BBBRestClientPersona> personaMap) {
		if (isLoggingDebug()) {
			logDebug("BBBRestAccessServlet.addPresonaToUserProfile() starts = " + pRole);
		}
		for (RepositoryItem repositoryItem : pRole) {
			
			String applicableSite = (String) repositoryItem.getPropertyValue(RestConstants.APPLICABLE_SITE_ID);
			String isSecurePersona = (String) repositoryItem.getPropertyValue(RestConstants.IS_SECURE_PERSONA);
			String rolveValue = (String) repositoryItem.getPropertyValue(RestConstants.PERSONA);
			if (isLoggingDebug()) {
				logDebug("Values fetched from role Item ");
				logDebug("applicableSite ID ====  "+applicableSite);
				logDebug("isSecurePersona ====  "+isSecurePersona);
				logDebug("rolveValue ====  "+rolveValue);
			}
			BBBRestClientPersona persona = null;
			
			// checks that only those roles gets added to user persona where associated roles's applicable siteID is either null or matches the currents request's site ID
			if(applicableSite ==null || applicableSite.equalsIgnoreCase(pSiteId)){
				if (BBBUtility.isEmpty(pToken) && (isSecurePersona.equalsIgnoreCase(RestConstants.INSECURE_PERSONA))){
					persona = new BBBRestClientPersona(getUserAuthority());
					persona.setRoleName(rolveValue);
					persona.setClientID(pClientID);

					if (rolveValue!=null && personaMap!=null && !personaMap.containsKey(rolveValue))
					{
						if (isLoggingDebug()) {
						
							logDebug("Adding persona for role:" + rolveValue );
						}
						ThreadSecurityManager.currentUser().addPersona(persona);
					}
					
					
					/*if (isLoggingDebug()) {
						logDebug("Added ROLE : " +persona.getRoleName());
					}*/
					
				}
				else if(BBBUtility.isNotEmpty(pToken)){
					persona = new BBBRestClientPersona(getUserAuthority());
					persona.setRoleName(rolveValue);
					persona.setClientID(pClientID);
					if (rolveValue!=null && personaMap!=null && !personaMap.containsKey(rolveValue))
					{	
						if (isLoggingDebug()) {
						
							logDebug("Adding persona for role:" + rolveValue );
						}
						
						ThreadSecurityManager.currentUser().addPersona(persona);
					}

					
					/*if (isLoggingDebug()) {
						logDebug("Added ROLE : " +persona.getRoleName());
					}*/
				}
			}
		}

		if (isLoggingDebug()) {
			if (ThreadSecurityManager.currentUser().getPersonae()!=null)
			{
				logDebug("Count of total persona objects:" + ThreadSecurityManager.currentUser().getPersonae().length);
			}
			else
			{
				logDebug("Count of total persona objects: 0");
			}
			
		}


		
		if (isLoggingDebug()) {
			logDebug("BBBRestAccessServlet.addPresonaToUserProfile() ends");
		}
	}
}
