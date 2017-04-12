package com.bbb.framework.security;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import atg.security.DigestPasswordHasher;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.InsertableServletImpl;

import com.bbb.utils.BBBUtility;


public class SessionSecurityServlet extends InsertableServletImpl 
{

	private static String SECURE_COOKIE = "COOKIE-SECURE-SID";
	private static String SESSION_TOKEN = "SESSION-SECURE-ID";
	private static String ERROR_PAGE ="/store/global/secureTokenError.jsp";        
	private static Integer AUTH_ERROR_HTTP_STAUS = 409;
	private static String CONTEXT = "/store";
	private static String SECURE_TOKEN_FLAG = "secureTokenFlag";
		
	private boolean enableServlet=false;
	
	
	
	public boolean getEnableServlet() {
		return enableServlet;
	}

	public void setEnableServlet(boolean enableServlet) {
		this.enableServlet = enableServlet;
	}

	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		   
		logDebug("Pipeline Servlet invoked for URL="+pRequest.getRequestURL());

    	logDebug("Value of enableServlet parameter="+getEnableServlet());
        
    	if (!getEnableServlet())
    	{
    		logDebug("Servlet is disabled so skipping the process for secure cookie check");
    		passRequest(pRequest, pResponse);
    		return;
    	}
    	
    	if (!CONTEXT.equals(pRequest.getContextPath()))
    	{
    		logDebug("Context is not /store so skipping the process for secure cookie check");
    		passRequest(pRequest, pResponse);
    		return;

    	}
    	
        String secureCookieValue = null;
        String sessionToken = null; 
        
        logDebug("httpRequest.isSecure() : "+pRequest.isSecure());

        if (pRequest.isSecure())
        {	
        	secureCookieValue = pRequest.getCookieParameter(SECURE_COOKIE);
            if (pRequest.getSession().getAttribute(SESSION_TOKEN)!=null)
            {
            	sessionToken = (String)pRequest.getSession().getAttribute(SESSION_TOKEN);
            }
            logDebug("Https session accessed."+ "Secure cookie value=" + secureCookieValue + ". Session token value="+sessionToken);
            
            if (sessionToken==null)
    		{
    			createSecureCookie(pRequest,pResponse,sessionToken);
    		}
    		else
    		{ 
				if (secureCookieValue!=null)
				{
					logDebug("sessionToken.equals(secureCookieValue) : "+sessionToken.equals(secureCookieValue));

					if (sessionToken.equals(secureCookieValue))
					{        				
						passRequest(pRequest, pResponse);
						return;
					}
					else
					{
						sessionValidation(pRequest,pResponse);
					}
				}
				else
				{
					sessionValidation(pRequest,pResponse); 		  
				}		
    		}

        }
        else
        {
        	passRequest(pRequest, pResponse);;
        	return;
        }
        
    }

	public void init(FilterConfig config) throws ServletException
    {
		//init method, do nothing
    }
	@Override
    public void destroy()
    {
		//destroy method, do nothing
    }
    
    public void createSecureCookie(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse , String sessionToken) throws ServletException, IOException {
    	
    	sessionToken = UUID.randomUUID().toString();
		pRequest.getSession().setAttribute(SESSION_TOKEN,sessionToken );
		//generate secure cookie id
		DigestPasswordHasher passwordHasher = new DigestPasswordHasher();
		String securedCookie = passwordHasher.encryptPassword(sessionToken);
		sessionToken = securedCookie;
		pRequest.getSession().setAttribute(SESSION_TOKEN,sessionToken );
		Cookie cookie = new Cookie(SECURE_COOKIE, securedCookie);
		cookie.setDomain(pRequest.getServerName());
		cookie.setPath("/");
		cookie.setSecure(true);
		BBBUtility.addCookie(pResponse, cookie, true);
		logDebug("Added cookie and session token with value="+sessionToken);
		passRequest(pRequest, pResponse);
		return;
    }
    
    public void sessionValidation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	
	 	// if token is active
		if ("true".equalsIgnoreCase((String)pRequest.getSession().getAttribute(SECURE_TOKEN_FLAG))) {
			
			pResponse.setStatus(AUTH_ERROR_HTTP_STAUS);
			pResponse.sendRedirect(ERROR_PAGE);
   		 
			HttpSession session = pRequest.getSession(false);
			
 		  if (session != null) {
 			  ServletUtil.invalidateSessionNameContext(pRequest, session);
 			  ServletUtil.invalidateSession(pRequest, session);

 			  if ( ServletUtil.isWebLogic() )
 				  pRequest.setAttribute(DynamoHttpServletRequest.SESSION_INVALIDATED, Boolean.TRUE);
 			  
 		  }
 		  logError("Secure token mismatch between HttpSession and browser cookie");
			
		} else { 
			// else pass request.
			passRequest(pRequest, pResponse);
			return;
		}
    }
    
    @Override
    public void logDebug(String pMessage) {
    	// TODO Auto-generated method stub
    	if (isLoggingDebug())
    	{
    		super.logDebug(pMessage);
    	}
    	
    }
}


