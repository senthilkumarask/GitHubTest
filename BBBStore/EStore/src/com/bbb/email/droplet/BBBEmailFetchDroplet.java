package com.bbb.email.droplet;

import java.io.IOException;
import javax.servlet.ServletException;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

import static com.bbb.constants.BBBAccountConstants.*;


public class BBBEmailFetchDroplet extends BBBDynamoServlet{
    
    private Repository emailPersistRepository= null;

    public Repository getEmailPersistRepository() {
        return emailPersistRepository;
    }

    public void setEmailPersistRepository(Repository emailPersistRepository) {
        this.emailPersistRepository = emailPersistRepository;
    }
    

    public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
    			throws ServletException, IOException{
	
	/**
	 * Unique Id from URL passed as Parameter
	 */
	
	logDebug("Entering Email Fetch Droplet");
		
	String idReceived = request.getParameter(BBBCmsConstants.TOKEN);
	
	if (idReceived != null) {
	    /**
	     * try block to check for repository exception.
	     * 
	     */
	    String errMsg = null;
	    try {
		/**
		 * Repository view of Email Persisted Item.
		 */		
		final RepositoryView repoView = getEmailPersistRepository()
			.getView(BBBCmsConstants.EMAIL_DATA);

		/**
		 * QueryBuilder based on the repository item view email
		 * persisted item.
		 */		
		final QueryBuilder pQueryBuilder = repoView.getQueryBuilder();

		/**
		 * Property QueryExpression based on the Email Unique
		 * Identifier.
		 */
		final QueryExpression pProperty = pQueryBuilder.createPropertyQueryExpression(BBBCmsConstants.EMAIL_ID);

		/**
		 * Constant QueryExpression based on the unique id received from url.
		 */
		final QueryExpression pValue = pQueryBuilder.createConstantQueryExpression(idReceived);
		/**
		 * Query is build for Retrieving Persisted Email		 * 
		 */
		Query uniqueEmailId =
			pQueryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
		
		/**
		 * Query is executed for Retrieving Persisted Email
		 */
		RepositoryItem[] emailItem = repoView.executeQuery(uniqueEmailId);
		
		if (emailItem != null && emailItem.length > 0 && emailItem[0] != null) {		    		   

		    if (null != emailItem[0].getPropertyValue(BBBCmsConstants.EMAIL_MESSAGE)) {
			request.setParameter(BBBCmsConstants.EMAIL_ITEM, emailItem[0].getPropertyValue(BBBCmsConstants.EMAIL_MESSAGE));
		    }

		    logDebug("Email Found");
		    request.serviceParameter(BBBCmsConstants.OUTPUT, request, response);
		}else{
		    	errMsg = "Email Item not found";
			request.setParameter("errMsg", errMsg);
			request.serviceParameter(OPARAM_ERROR, request,response);
		}
		    
		
	    }catch (RepositoryException e) {

			logError(LogMessageFormatter.formatMessage(request,
				"EmailFetchDroplet|service|RepositoryException", ""), e);

			errMsg = "RepositoryException for Fetching Email";
			request.setParameter("errMsg", errMsg);

	    }	   
	} else {
		    
	    request.serviceParameter(OPARAM_ERROR, request,response);
	    logDebug("No Email Token Found In Request");
		    
	} 
	
    }
	
    
    
    /**
	 * get view in browser details for the rest API
	 * @param request
	 * @param response
	 * @return email details
	 * @throws BBBSystemException
	 * @throws BBBBusinessException 
	 */

	public String fetchEmailAPI(String tokenId) throws BBBSystemException, BBBBusinessException
	{
		logDebug("Starting method getFetchEmailAPI for Email Fetching");
		String emailItemValue = null;		
			
		    
		    try{					
			
			DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
			
			request.setParameter(BBBCmsConstants.TOKEN ,tokenId);
			if(tokenId != null){
			    request.setParameter(BBBCmsConstants.TOKEN ,tokenId);
			}
			
			service(request, response);

			emailItemValue = (String) request.getObjectParameter(BBBCmsConstants.EMAIL_ITEM);
			logDebug("End method getFetchEmailAPI for rest module");
			
		    }catch (IOException e) {
        		throw new BBBSystemException(BBBCoreErrorConstants.GET_EMAIL_FETCH_IO_EXCEPTION, "IO Exception While fetching email detail");
        
		    } catch (ServletException e) {
        		throw new BBBSystemException(BBBCoreErrorConstants.GET_EMAIL_FETCH_SERVLET_EXCEPTION, "Servlet Exception While fetching email detail");
		    }
		    		
		return emailItemValue;			
		
	}
    
}