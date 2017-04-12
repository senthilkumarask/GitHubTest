package com.bbb.framework.security;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;



import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;


/**
 * Droplet to validate request parameters based on configurable regular expressions
 * @author ikhan2
 *
 */
public class ValidateParametersDroplet extends BBBDynamoServlet {

	private static String COMM_DELIM = ";";
	private static String OPARAM_VALID = "valid";
	
	private BBBCatalogTools mCatalogTools;
	
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}

	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		
		this.logDebug("ValidateParametersDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		
		BBBPerformanceMonitor.start("ValidateParametersDroplet", "Service");
			
		String paramName  = pRequest.getParameter("paramName");
		String paramValue  = pRequest.getParameter("value");
		String paramStringArray  = pRequest.getParameter("paramArray");
		String paramValuesStrArray  = pRequest.getParameter("paramsValuesArray");
		
		String[] paramsArr = null;
		String[] paramsValuesArr = null;
		
		logDebug("ValidateParametersDroplet MSG=paramName :" +paramName + " paramValue:"+paramValue+ " paramStringArray:"+paramStringArray+ " paramValuesStrArray:"+paramValuesStrArray);
		if(paramStringArray ==null && paramName==null){
					
			logDebug("ValidateParametersDroplet both paramName and paramNameArray are blank. Invalid inputs");
			pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest,pResponse);
			return;
			
		} else if(paramName !=null){
			
			if(BBBUtility.isEmpty(paramValue)){
				logDebug("Invalid inputs for paramName, paramValue can not be blank");
				pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest,pResponse);
				return;
			}
				
			paramsArr= new String[]{paramName};
			paramsValuesArr= new String[]{paramValue};
		} else{
			
			if(BBBUtility.isEmpty(paramValuesStrArray)){
				logDebug("Invalid inputs for paramStringArray, paramValuesStrArray can not be blank");
				pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest,pResponse);
				return;
			}
			
			//TO DO -refractor check using requestMap
			paramsArr = paramStringArray.split(COMM_DELIM);
			paramsValuesArr =paramValuesStrArray.split(COMM_DELIM);
		}
		
		boolean isValidPattern = false;
		
		try {
			
			if(paramsArr !=null && paramsValuesArr.length == paramsArr.length){
				
				for(int index=0;index<paramsArr.length;index++){
					String validateParamName = paramsArr[index];
					String validateParamVal = paramsValuesArr[index];
					
					isValidPattern= isPatternValid(validateParamName, validateParamVal);

					logDebug("isPatternValid() for validateParamName ="+isValidPattern);
					if(!isValidPattern){
						break;
					}
				}
			}
					
		} catch (BBBSystemException e) {
			logError("Error in validating parameters: MSG=" +e.getMessage());
		} catch (BBBBusinessException e) {
			logError("Error in validating parameters: MSG=" +e.getMessage());
		}
		
		
		if(isValidPattern){
			logDebug("Valid parameters format");
			pRequest.serviceParameter(OPARAM_VALID, pRequest,pResponse);	
		} else{
			logDebug("Invalid parameters format");
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,pResponse);
		}
	
		this.logDebug(" ValidateParametersDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");
		BBBPerformanceMonitor.end("ValidateParametersDroplet", "Service");
		

	}

	/**
	 * Check if pValue match to the pattern in config repository
	 * 
	 * @param paramName
	 * @param pValue
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean isPatternValid(final String paramName, final String pValue) 
			throws BBBSystemException, BBBBusinessException{
		
		logDebug("isPatternValid() begins");
		
		String pPattern = null;
		
		 if(this.getCatalogTools().getContentCatalogConfigration("regExp_"+paramName)!=null){
			 pPattern=  this.getCatalogTools().getContentCatalogConfigration("regExp_"+paramName).get(0);
		 }
		
		logDebug("isPatternValid() paramName="+paramName + " pPattern="+pPattern);
		
		
		if(BBBUtility.isEmpty(pValue)){
			return false;
		}
		
		//Do not validate parameter
		if (BBBUtility.isEmpty(pPattern)) {
			return true;
        }

        final Pattern pattern = Pattern.compile(pPattern);
        final Matcher match = pattern.matcher(pValue);
        
        logDebug("isPatternValid() ends");
        return match.matches();
		
	}
	

	
}
