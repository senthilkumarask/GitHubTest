package com.bbb.selfservice.tools;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

public class TBSContactUsTools extends ContactUsTools {

	/**
	  * This method is used to fetch subject category from contactus repository   
	  * @return Array of RepositoryItem 
	  * @throws BBBBusinessException,BBBSystemException
	 */
	 
	 public RepositoryItem[] getContactUsItem() throws BBBSystemException {
	    	
			logDebug("ContactUsTools.getContactUsItem() method started");
		    RepositoryItem[] items = null;
		    String siteId = getSiteContext().getSite().getId();
		    if(siteId.equals("TBS_BedBathUS") ) {
		    	siteId = "BedBathUS";
			}
			else if(siteId.equals("TBS_BuyBuyBaby") ) {
				siteId = "BuyBuyBaby";			
			}
			else if(siteId.equals("TBS_BedBathCanada") ) {
				siteId = "BedBathCanada";			
			}
		 	try{
			    RepositoryView view = getContactUsRepository().getView("contactus");
		    	QueryBuilder queryBuilder = view.getQueryBuilder();
		    	QueryExpression pProperty = queryBuilder.createPropertyQueryExpression("siteName");
		    	QueryExpression pValue = queryBuilder.createConstantQueryExpression(siteId);
		    	Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
		    	items = view.executeQuery(query);
		 	}catch (RepositoryException eRepositoryException) {
		 		logError(LogMessageFormatter.formatMessage(null, "Method ContactUsTools.getContactUsItem() throws error in fetch subject category from contactus repository", BBBCoreErrorConstants.ACCOUNT_ERROR_1231 ), eRepositoryException);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1332,eRepositoryException.getMessage(),eRepositoryException);
			}
	    	
	    	if(items!=null && items.length>0){
	    	    	return items;
	    	}
	    	
			logDebug("ContactUsTools.getContactUsItem() method ends");
	    	
	    	return items;
	    }
}
