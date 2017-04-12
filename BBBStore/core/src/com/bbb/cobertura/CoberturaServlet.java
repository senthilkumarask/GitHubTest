package com.bbb.cobertura;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class CoberturaServlet extends DynamoServlet  {
	
	/**
	 * 
	 */


	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		try {
		    String className = "net.sourceforge.cobertura.coveragedata.ProjectData";
		    String methodName = "saveGlobalProjectData";
		    Class saveClass = Class.forName(className);
		    java.lang.reflect.Method saveMethod = saveClass.getDeclaredMethod(methodName, new Class[0]);
		    saveMethod.invoke(null,new Object[0]);
		    pRequest.setParameter("result", "True");
		    pRequest.serviceParameter("output", pRequest, pResponse);
		} catch (Exception e) {
			logError(e);
		}
		
	}
} 


