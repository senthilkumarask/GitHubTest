package com.bbb.framework.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

public class HideUrlSessionFilter implements Filter
{
   // private static final long MAX_SESSION_VALIDITY = 15 * 60 * 1000l;

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        if (!(request instanceof HttpServletRequest))
        {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

      //  let’s invalidate any sessions that are backed by a URL-encoded session id. This prevents an attacker from generating a valid link.  
        HttpSession session = httpRequest.getSession();

        if (httpRequest.isRequestedSessionIdFromURL())
        {
            if (session != null)
            {
                session.invalidate();
            }
        }

     /*   try
        {
            session.setMaxInactiveInterval((int) MAX_SESSION_VALIDITY);
        } catch (IllegalStateException e)
        {

        }*/
        
        //To disable the default URL-encoding functionality, we need to wrap the existing HttpServletResponse object. 
        //Fortunately, the Servlet API provides just such a class ready-made in HttpServletResponseWrapper. 
        //We could subclass it to provide our own handling, but this is a trivial enough change that an anonymous inner class will do nicely:

        //overridden four methods.
        //encodeRedirectURL is used to encode redirected URLs, which can sometimes require different logic to determine if session identifiers are required. 
        //The other two methods are deprecated, but are included here for completeness.
        HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(
                httpResponse)
        {
            public String encodeRedirectUrl(String url)
            {
                return url;
            }

            public String encodeRedirectURL(String url)
            {
                return url;
            }

            public String encodeUrl(String url)
            {
                return url;
            }

            public String encodeURL(String url)
            {
                return url;
            }
        };
        

        //Finally, we need to pass the original request and our response wrapper to the next filter in the chain:

        chain.doFilter(request, wrappedResponse);
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


