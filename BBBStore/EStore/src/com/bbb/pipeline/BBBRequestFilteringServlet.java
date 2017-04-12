/**
 * Filter Request 
 * 
 */
package com.bbb.pipeline;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.droplet.DropletConstants;
import atg.droplet.DropletException;
import atg.naming.NameContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.SecurityServlet;
import atg.servlet.security.param.ParameterValidator;

/**
 * @author Rajesh Saini
 * 
 */
public class BBBRequestFilteringServlet extends SecurityServlet {

	

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws IOException
	 * @throws ServletException
	 * 
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	        throws IOException, ServletException
	    {
		      if(!allowRequest(pRequest, pResponse))
	        {
	        	addFormException(pRequest,	new DropletException("error_in_input","error_in_input"));
				String destination  ="/WEB-INF/global/serverError.jsp";        
				//pResponse.sendRedirect(pResponse.encodeRedirectURL(destination));
				//throw new ServletException();.
				RequestDispatcher rd = pRequest.getRequestDispatcher(destination);
				rd.forward(pRequest,pResponse);
	        } else
	        {
	            passRequest(pRequest, pResponse);
	            return;
	        }
	       
	     
	        
	    }
	/**
	 * Adds a new exception to the list of FormExceptions.
	 */
	private void addFormException(DynamoHttpServletRequest pRequest,
			DropletException exc) {
		Vector<DropletException> mExceptions = new Vector<DropletException>();
		mExceptions.addElement(exc);

		if (pRequest != null) {
			NameContext ctx = pRequest.getRequestScope();
			if (ctx != null) {
				Vector<DropletException> v = (Vector<DropletException>) ctx
						.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE);
				if (v == null) {
					v = new Vector<DropletException>();
					/* This is for compatibility */
					pRequest.setAttribute(
							DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, v);
					/*
					 * This is so that exceptions are preserved across request
					 * scope
					 */
					ctx.putElement(
							DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, v);
				}
				if (!v.contains(exc)) {
					v.addElement(exc);
				}
			}
		}
	}
	
    protected boolean allowQueryParameters(DynamoHttpServletRequest pRequest)
    {
    	if (getParameterValidator() == null) {
			return true;
	}
		 
	ParameterValidator validator = getParameterValidator().getParameterValidatorForRequest(pRequest);
       
        for(Enumeration keys = pRequest.getQueryParameterNames(); keys.hasMoreElements();)
        {
            String strKey = keys.nextElement().toString();
            String values = pRequest.getParameter(strKey);
            if(values != null && validator.areParamValuesSuspicious(strKey, new String[] {values}))
            {
                vlogError("Suspicious query parameters detected: requestURI=\"{0.requestURI}\",, queryString=\"{0.queryString}\", referer=\"{1}\", remoteHost={2}, remoteAddress={3}", new Object[] {
                    pRequest, pRequest.getHeader("referer"), pRequest.getRemoteHost(), pRequest.getRemoteAddr()
                });
                return false;
            }
        }

        return true;
    }
    
	protected boolean allowPostParameters(DynamoHttpServletRequest pRequest)
    {
       
		Map<String,String[]> reqsmap= pRequest.getParameterMap();       
        if (reqsmap==null || reqsmap.isEmpty()) {
        	return true;
        }
        if (getParameterValidator() == null) {
			return true;
	}
		 
	ParameterValidator validator = getParameterValidator().getParameterValidatorForRequest(pRequest);
       
        Set<String> keyset = reqsmap.keySet();
        Iterator<String> it = keyset.iterator();
        
        while(it.hasNext())
        {
            String strKey = it.next().toString();
            String[] values = new String[reqsmap.size()];
            if (reqsmap.get(strKey)!=null) {
            	values=reqsmap.get(strKey);
            }
            if(!BBBUtility.isEmpty(values) && validator.areParamValuesSuspicious(strKey, values)){
                vlogError("Suspicious post parameters detected: requestURI=\"{0.requestURI}\",, queryString=\"{0.queryString}\", referer=\"{1}\", remoteHost={2}, remoteAddress={3}", new Object[] {
                    pRequest, pRequest.getHeader("referer"), pRequest.getRemoteHost(), pRequest.getRemoteAddr()
                });
                return false;
            }
        }

        return true;
    }

    public boolean allowRequest(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    {
    	
        boolean bAllowed = true;
        
        ParameterValidator validator;
        
        if(null != getParameterValidator()){
        	validator = getParameterValidator().getParameterValidatorForRequest(pRequest);
        	if(null != validator && !allowQueryParameters(pRequest))
	            bAllowed = false;
		    if(null != validator && !allowPostParameters(pRequest))
		        bAllowed = false;        
		    if (Boolean.valueOf(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
						BBBCoreConstants.XSS_VALIDATE_HEADER_FLAG))) {
				if (null != validator && !validateHeaders(pRequest, validator))
						bAllowed = false;
			}
        }
        if(isLoggingDebug()){
        	logDebug("ParameterValidator is null ");        	
        }
        return bAllowed;
    }


    /**
     * This method is used to prevent request headers against CSS attack.
     * @param DynamoHttpServletRequest request
     * @return boolean check whether header contain suspicious param or not
     */
	protected boolean validateHeaders(DynamoHttpServletRequest request, ParameterValidator validator) {
		boolean result = true;
		
		for (Enumeration keys = request.getHeaderNames(); keys
				.hasMoreElements();) {
			String strKey = keys.nextElement().toString();
			String values = request.getHeader(strKey);
			if (values != null && validator.areParamValuesSuspicious(strKey, new String[] { values })) {
				vlogError("Suspicious header parameters detected: requestURI=\"{0.requestURI}\","
								+ ", queryString=\"{0.queryString}\", referer=\"{1}\", remoteHost={2}, remoteAddress={3}",
						new Object[] { request, request.getHeader("referer"),
								request.getRemoteHost(),
								request.getRemoteAddr() });
				result = false;
				break;
			}
		}
	   	     
   
		return result;
	}
	
}