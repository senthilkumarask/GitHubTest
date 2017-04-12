package com.bbb.framework.pagefilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import weblogic.servlet.SessionCreationException;
import atg.filter.dspjsp.PageFilter;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;


/**
 * @author mamish
 * 
 * This class handles the weblogic session limit
 * 
 */

public class BBBPageFilter extends PageFilter {
	
	private static final String  module = BBBPageFilter.class.getName();
	private static final ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClassName(module);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try{
			super.doFilter(request, response, chain);	
		}catch(SessionCreationException sce){
			logger.logError("**** Error PageFilter: " + sce.getMessage(), sce);
			((HttpServletResponse)response).sendError(503);
		}
	}
}
