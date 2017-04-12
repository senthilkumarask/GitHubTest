package com.bbb.selfservice.tools;

//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 03-November-2011
//--------------------------------------------------------------------------------

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.bbb.common.BBBGenericService;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * 
 * @author jsidhu
 *This class interact with MapQuest, Sends a HTTP request and recieve the HTTP response back.
 */

public class MapQuestService extends BBBGenericService {
    /*
     * This method is used to create URI and send request to MapQuest  
     */
    public String communicateMapQuest(String searchString)
            throws ClientProtocolException, IOException {
        String logMessage = getClass().getName() + "communicateMapQuest";
            logDebug(logMessage + " || " + " Starts here.");
            logDebug(logMessage + " || " + " Search Criteria String --> "
                    + searchString);
        BufferedReader reader = null;
        String sWsResponse = null;
        InputStreamReader inpStreamReader = null;
        try {
            final HttpClient httpClient = new DefaultHttpClient();
            // HttpConnectionParams.setConnectionTimeout(3000);
            HttpResponse webResponse;
            HttpGet getMethod;
            if (searchString.contains("|")) {
                String pipe = URLEncoder.encode("|", "UTF-8");
                searchString = searchString.replace("|", pipe);
            }
            if (searchString.contains(" ")) {
                String space = URLEncoder.encode(" ", "UTF-8");
                searchString = searchString.replaceAll(" ", space);
            }
            getMethod = new HttpGet(searchString);
            BBBPerformanceMonitor.start("HTTPMapQuestInvoke", searchString);
            webResponse = executeHttpClient(httpClient, getMethod);
            BBBPerformanceMonitor.end("HTTPMapQuestInvoke", searchString);
            int WSresponse = (webResponse.getStatusLine().getStatusCode());
            if (WSresponse == 200) {
            	inpStreamReader = new InputStreamReader(webResponse.getEntity().getContent(), "UTF-8");
                reader = new BufferedReader(inpStreamReader);
                // for multiple line resp..
                sWsResponse = inputToString(reader);
            }

        } finally {
            if (reader != null) {
                reader.close();
            } 
            if (inpStreamReader != null) {
            	inpStreamReader.close();
            } 
        }

        return sWsResponse;
    }

	/**
	 * @param httpClient
	 * @param getMethod
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	protected HttpResponse executeHttpClient(final HttpClient httpClient, HttpGet getMethod)
			throws IOException, ClientProtocolException {
		return httpClient.execute(getMethod);
	}

    protected String inputToString(Reader reader) throws IOException {
        StringBuffer buff = new StringBuffer();
        int i=((BufferedReader) reader).read();
        while (i  != -1) {
            char c = (char) i;
            buff.append(c);
            i=((BufferedReader) reader).read();
        }
        return buff.toString();
    }
}
