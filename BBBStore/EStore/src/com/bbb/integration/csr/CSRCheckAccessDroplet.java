
package com.bbb.integration.csr;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

/**
 * The class is the extension of the ATG DynamoServlet. The class is responsible
 * for rendering the content for location drop down in survey.jsp.
 * 
 */

public class CSRCheckAccessDroplet extends BBBDynamoServlet {

	/**
	 * This method is used to fetch Location value in the dropdown to select a location for user.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private BBBCatalogTools mCatalogTools;

	public static final String DELIMITER = "::";
	
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("CSRCheckAccessDroplet.service() method started");

	   boolean isIPAllowed = false;
		String clientIP = pRequest.getHeader("True-Client-IP");
		if(clientIP == null || clientIP.isEmpty())
			{
			clientIP = pRequest.getRemoteAddr();
			}
		logDebug("CSRCheckAccessDroplet :: clientIP=" + clientIP);
		List<String> listKeys = null;
		try 
		{
			listKeys = this.getCatalogTools().getAllValuesForKey(
					"ContentCatalogKeys", "csr_ip_pattern");
			if ((listKeys != null) && (listKeys.size() > 0)) 
			{
			
				logDebug("CSRCheckAccessDroplet :: listKeys=" + listKeys);
					for (String IPPattern : listKeys)
					{
						logDebug("CSRCheckAccessDroplet :: Pattern=" + IPPattern);
				        final Pattern pattern = Pattern.compile(IPPattern);
				        final Matcher match = pattern.matcher(clientIP);
			        
				        if(match.matches())
				        {
				        	isIPAllowed=true;
				        	logDebug("CSRCheckAccessDroplet :: Pattern Matched");
				        }
				}
			}
		} 
		catch (BBBSystemException e) 
		{
			this.logError("BBBSystemException - csr_ip_pattern key not found in config key" + e);
			isIPAllowed=false;
		} 
		catch (BBBBusinessException e) 
		{
				this.logError("BBBBusinessException - csr_ip_pattern key not found in config key"	+ e);
				isIPAllowed=false;
		}
		finally
		{
			pRequest.setParameter("isIPAllowed", isIPAllowed);
			pRequest.serviceLocalParameter("output", pRequest, pResponse);
		}

		logDebug("CSRCheckAccessDroplet.service() method ends");

	}

	
	
}
