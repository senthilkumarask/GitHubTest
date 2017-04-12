package com.bbb.account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.ServletException;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;


public class WalletClickThroughDroplet extends BBBDynamoServlet{
	

    private static final String STR_D = "d";
	private static final String STR_A = "a";
	private BBBCatalogTools catalogTools;
	private static final String JSON_STATUS = "siteId";
	private static final String JSON_WALLET_CREATED = "walletCreated";
	private static final String JSON_WALLETID = "walletId";
	private static final String JSON_OFFERID = "offerId";
	private static final String JSON_ACTION = "action";
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	
	/**
	 * This method is used to get the json response from the service provided.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {	
		
		 
		    logDebug("WalletClickThroughDroplet.service() method Started");	
		
		    String clickData = pRequest.getParameter(STR_D);
			String actionType = pRequest.getParameter(STR_A);

			
        	logDebug("clickData and actionType" + clickData + actionType);	
										   
	        String svcJson = getServiceResponse(clickData, actionType);
	        logDebug("svcJson obtained is" + svcJson);

			JSONObject json; 
	        	        
			try {				
				 json = new JSONObject(svcJson);
				
				 logDebug("json obtained is" + json);
		        
		         String relativeUrl = json.getString("redirectUrl");

				 logDebug("relativeUrl" + relativeUrl);
		       		        		           
				 pRequest.setParameter("relativeUrl", relativeUrl);
		        
			} catch (Exception e) {				
				logError(LogMessageFormatter.formatMessage(pRequest, "JSONException exception has occured"),e);
				pRequest.serviceParameter("error",pRequest, pResponse);
			}

			pRequest.serviceLocalParameter("output", pRequest, pResponse);
			logDebug("WalletClickThroughDroplet.service() method ended");			        
    }
	
	/**
	 * This method is used to get the json response from the service.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private String getServiceResponse(String clickData, String actionType) {
		
		logDebug("WalletClickThroughDroplet.getServiceResponse() method Started");	
		String responseBody = null;
		String couponsmanager_scheme = null;
		String couponsmanager_host = null;
		String couponsmanager_port = null;
		String couponsmanager_path = null;
		CloseableHttpResponse response = null;
		
		try {
			
			
			couponsmanager_scheme = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.COUPONS_MANAGER_SCHEME).get(0);
		
			couponsmanager_host = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.COUPONS_MANAGER_HOST_NAME).get(0);
			couponsmanager_port = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.COUPONS_MANAGER_PORT).get(0);
			couponsmanager_path = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.COUPONS_MANAGER_PATH).get(0);
			
			logDebug("couponsmanager_scheme is:" + couponsmanager_scheme);
			logDebug("couponsmanager_host is:" + couponsmanager_host);
			logDebug("couponsmanager_path is:" + couponsmanager_path);
			logDebug("couponsmanager_port is:" + couponsmanager_port);
			
			URI uri = new URIBuilder()
	        .setScheme(couponsmanager_scheme)
	        .setHost(couponsmanager_host)
	        .setPort(Integer.parseInt(couponsmanager_port))
	        .setPath(couponsmanager_path)
	        .setParameter(BBBCoreConstants.CLICK_THROUGH_PARAMETER_CLICKDATA, clickData)
	        .setParameter(BBBCoreConstants.CLICK_THROUGH_PARAMETER_CLICKACTION, actionType)
	        .build();
			
			
			logDebug("Get URL:" + uri.toString());
			HttpGet httpget = new HttpGet(uri); 
			
			CloseableHttpClient client = HttpClientBuilder.create().build();
			response = client.execute(httpget);

			StatusLine statusLine = response.getStatusLine();
	        if (statusLine.getStatusCode() >= 300) {	        	
	        	logDebug("Status:" + statusLine.getStatusCode() + " - " + statusLine.getReasonPhrase());
	        }
										
			    HttpEntity entity = response.getEntity();
			    if (entity != null) {
			    	logDebug("Got Entity, read content");
			        InputStream is = entity.getContent();
			        try {
			        	BufferedReader br = new BufferedReader(new InputStreamReader(is));			        	

			        	StringBuilder sb = new StringBuilder();
			        	String line = br.readLine();
			        	while (line != null) {
			        	    sb.append(line);
			        	    line = br.readLine();
			        	}

			        	responseBody = sb.toString();			        	
			        	logDebug("Response: " + responseBody);
			        } finally {
			            is.close();
			        }
				    }
		}  catch (BBBSystemException e1) {
			logError("BBBSystemException exception has occured",e1);			
		} catch (BBBBusinessException e1) {
			logError("BBBBusinessException exception has occured",e1);			
		}catch (NumberFormatException e) {
			logError("NumberFormatException exception has occured",e);			
		} catch (URISyntaxException e) {
			logError("URISyntaxException exception has occured",e);			
		} catch (IllegalStateException e) {
			logError("IllegalStateException exception has occured",e);			
		} catch (IOException e) {
			logError("IOException exception has occured",e);
		}finally {
			try{
				if(null!=response){
					response.close();
				}
			}catch(Exception e){
				logError("Exception exception has occured",e);
			}
	    }				
		return responseBody;
	}
}
