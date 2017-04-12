package com.bbb.framework.security;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
//import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



//import atg.nucleus.logging.ApplicationLogging;
//import atg.nucleus.logging.ClassLoggingFactory;
import atg.security.DigestPasswordHasher;

public class SessionSecurityFilter implements Filter
{

	//private static final ApplicationLogging MLOGGING =
	  //  ClassLoggingFactory.getFactory().getLoggerForClass(SessionSecurityFilter.class);
	private static String SECURE_COOKIE = "COOKIE-SECURE-SID";
	private static String SESSION_TOKEN = "SESSION-SECURE-ID";
	//private static String ERROR_PAGE ="/WEB-INF/global/error.jsp";        
	
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
    	//MLOGGING.logInfo("Entering doFilter()");
        //System.out.println("Entering doFilter()");
    	if (!(request instanceof HttpServletRequest))
        {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //httpRequest.getRequestURL()
        
        String secureCookieValue = null;
        String sessionToken = null; 
        
        //DynamoHttpServletRequest pReq = ServletUtil.getDynamoRequest(httpRequest);
        //MLOGGING.logDebug("pReq.isSecure() : "+pReq.isSecure());
        //MLOGGING.logInfo("httpRequest.isSecure() : "+httpRequest.isSecure());
        //System.out.println("httpRequest.isSecure() : "+httpRequest.isSecure());
        if (httpRequest.isSecure())
        {	
        	
        	secureCookieValue = getCookie(httpRequest);
            if (httpRequest.getSession().getAttribute(SESSION_TOKEN)!=null)
            {
            	
            	
            	sessionToken = (String)httpRequest.getSession().getAttribute(SESSION_TOKEN);
            }
            //MLOGGING.logInfo("Https session accessed."+ "Secure cookie value=" + secureCookieValue + ". Session token value="+sessionToken);
           //System.out.println("Https session accessed."+ "Secure cookie value=" + secureCookieValue + ". Session token value="+sessionToken);
        }
        else
        {
        	chain.doFilter(request, response);
        	return;
        }
        
        	
        if (secureCookieValue!=null)
        {
        		if (sessionToken==null)
        		{
            		sessionToken = UUID.randomUUID().toString();
            		httpRequest.getSession().setAttribute(SESSION_TOKEN,sessionToken );
            		//generate secure cookie id
            		DigestPasswordHasher passwordHasher = new DigestPasswordHasher();
            		String securedCookie = passwordHasher.encryptPassword(sessionToken);
            		sessionToken = securedCookie;
            		httpRequest.getSession().setAttribute(SESSION_TOKEN,sessionToken );
            		
            		Cookie cookie = new Cookie(SECURE_COOKIE, securedCookie);
            		cookie.setDomain(httpRequest.getServerName());
            		cookie.setPath("/");
            		cookie.setSecure(true);

            		httpResponse.addCookie(cookie);
            		chain.doFilter(request, response);
            		return;
        		}
        		else
        		{ 
        			//MLOGGING.logInfo("sessionToken.equals(secureCookieValue) : "+sessionToken.equals(secureCookieValue));
        			//System.out.println("sessionToken.equals(secureCookieValue) : "+sessionToken.equals(secureCookieValue));
        			if (sessionToken.equals(secureCookieValue))
        			{        				
        				chain.doFilter(request, response);
        				return;
        			}
        			else
        			{
        				response.getWriter().print("Secure token mismatch between HttpSession and browser cookie");
        				//RequestDispatcher rd = request.getRequestDispatcher(ERROR_PAGE);
        				//rd.forward(request, response);
        				throw new ServletException("Secure token mismatch between HttpSession and browser cookie");
        			}
        	}
        }
        else
        { 
        	if (sessionToken==null)
        	{
        		sessionToken = UUID.randomUUID().toString();
        		httpRequest.getSession().setAttribute(SESSION_TOKEN,sessionToken );
        		//generate secure cookie id
        		DigestPasswordHasher passwordHasher = new DigestPasswordHasher();
        		String securedCookie = passwordHasher.encryptPassword(sessionToken);
        		sessionToken = securedCookie;
        		httpRequest.getSession().setAttribute(SESSION_TOKEN,sessionToken );
        		Cookie cookie = new Cookie(SECURE_COOKIE, securedCookie);
        		cookie.setDomain(httpRequest.getServerName());
        		cookie.setPath("/");
        		cookie.setSecure(true);

        		httpResponse.addCookie(cookie);
        		chain.doFilter(request, response);
        		return;
        	}
        	else
        	{
        		response.getWriter().print("Secure token mismatch between HttpSession and browser cookie");
        		//RequestDispatcher rd = request.getRequestDispatcher(ERROR_PAGE);
				//rd.forward(request, response);
				throw new ServletException("Secure token mismatch between HttpSession and browser cookie");
        	}
        }
        

    }
    
	private String getCookie(HttpServletRequest request) {
		Cookie cookies[] = request.getCookies();
		String cookieValue = null;
		if (cookies != null)
			for (int i = 0; i < cookies.length; i++) {
				String name = cookies[i].getName();
				if (SECURE_COOKIE.equals(name)) {
					cookieValue = cookies[i].getValue();
					break;
				}
			}
		return cookieValue;
	}

	@Override
    public void init(FilterConfig config) throws ServletException
    {
		//init method, do nothing
    }
	@Override
    public void destroy()
    {
		//destroy method, do nothing
    }
}


