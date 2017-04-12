package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bazaarvoice.seo.sdk.BVManagedUIContent;
import com.bazaarvoice.seo.sdk.BVUIContent;
import com.bazaarvoice.seo.sdk.config.BVClientConfig;
import com.bazaarvoice.seo.sdk.config.BVConfiguration;
import com.bazaarvoice.seo.sdk.config.BVSdkConfiguration;
import com.bazaarvoice.seo.sdk.model.BVParameters;
import com.bazaarvoice.seo.sdk.model.ContentType;
import com.bazaarvoice.seo.sdk.model.SubjectType;
import com.bazaarvoice.seo.sdk.util.BVUtility;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.utils.BBBUtility;

/**
 * Droplet to access all data related to brands view. This data is mainly
 * displayed as part of brand listing page Copyright 2011 Bath & Beyond, Inc.
 * All Rights Reserved. Reproduction or use of this file without explicit
 * written consent is prohibited. Created by: njai13 Created on: November-2011
 * 
 * @author njai13
 * 
 */
public class BVContentDroplet extends BBBDynamoServlet {

	private String staging;
	private String subjectType;
	private String contentType;
	private String sdkEnabled;
	private String executionTimeOut;
	private String botExecutionTimeOut;
	private String cloudKey;
	private String bvRootFolder;
	private String includeDisplayIntCode;
	private String loadSEOFilesLocally;
	private String sslEnabled;
	public final static String UTF = "UTF-8";
	public final static String BVRRP = "bvrrp";
	public final static String BVSYP = "bvsyp";
	public final static String BVQAP = "bvqap";
	public final static String BVPAGE = "bvpage";
	public final static String USERAGENT = "User-Agent";
	public final static String PRODUCTID = "productId";
	public final static String PAGEURL = "pageURL";
	public final static String BVCONTENT = "bvContent";
	public final static String OUTPUT = "output";
	public final static String CONTENT = "content";
	public final static String EXC = "Error: Unable to download";
	public final static String JSONLY = "JavaScript-only Display";
	public final static String LOCAL_SEO_FILES_ROOT = "/";
	
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
	    return this.contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
	    this.contentType = contentType;
	}

	/**
	 * @return the subjectType
	 */
	public String getSubjectType() {
		return this.subjectType;
	}

	/**
	 * @param subjectType the subjectType to set
	 */
	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	/**
	 * @return the staging
	 */
	public String getStaging() {
		return this.staging;
	}

	/**
	 * @param staging the staging to set
	 */
	public void setStaging(String staging) {
		this.staging = staging;
	}
	
	

	/**
	 * This method prepares the query string.
	 * 
	 * @param pRequest instance of DynamoHttpServletRequest
	 * @param pResponse instance of DynamoHttpServletResponse
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		final String userAgent = pRequest.getHeader(USERAGENT);
		final String productId = pRequest.getParameter(PRODUCTID);
		final String pageURL = pRequest.getParameter(PAGEURL);		
		String fullURL = null;
		
		String hostURL = getHost(pRequest);
		String queryString = getQueryString(pRequest);
		if (queryString != null) {
			fullURL = hostURL.concat(pageURL).concat(queryString);
		} else {
			fullURL = hostURL.concat(pageURL);
		}
		
		BVConfiguration bvSdkConfiguration = new BVSdkConfiguration();
		bvSdkConfiguration.addProperty(BVClientConfig.SEO_SDK_ENABLED, getSdkEnabled());
		bvSdkConfiguration.addProperty(BVClientConfig.EXECUTION_TIMEOUT, getExecutionTimeOut());
		bvSdkConfiguration.addProperty(BVClientConfig.EXECUTION_TIMEOUT_BOT, getBotExecutionTimeOut());
		bvSdkConfiguration.addProperty(BVClientConfig.CLOUD_KEY, getCloudKey());
		bvSdkConfiguration.addProperty(BVClientConfig.BV_ROOT_FOLDER, getBvRootFolder());
		bvSdkConfiguration.addProperty(BVClientConfig.INCLUDE_DISPLAY_INTEGRATION_CODE, getIncludeDisplayIntCode());
		bvSdkConfiguration.addProperty(BVClientConfig.STAGING, getStaging());
		bvSdkConfiguration.addProperty(BVClientConfig.LOAD_SEO_FILES_LOCALLY, getLoadSEOFilesLocally());
		bvSdkConfiguration.addProperty(BVClientConfig.SSL_ENABLED, getSslEnabled());
		bvSdkConfiguration.addProperty(BVClientConfig.LOCAL_SEO_FILE_ROOT, LOCAL_SEO_FILES_ROOT);
		bvSdkConfiguration.addProperty(BVClientConfig.CHARSET, UTF);
		String contentTypeInput = pRequest.getParameter(CONTENT);
		
		if(BBBUtility.isEmpty(contentTypeInput)){
		    contentTypeInput = getContentType();
		}
		if(StringUtils.isEmpty(contentTypeInput)){
			logError("BVContentDroplet.Service recieved no content parameter");
		}
		
			logDebug("service method -  contentType - " + contentTypeInput);
		BVParameters bvParameters = new BVParameters();
		bvParameters.setUserAgent(userAgent);
		bvParameters.setPageURI(fullURL);
		bvParameters.setContentType(ContentType.ctFromKeyWord(getContentType()));
		bvParameters.setSubjectType(SubjectType.subjectType(getSubjectType()));
		bvParameters.setBaseURI(hostURL.concat(pageURL));
		bvParameters.setSubjectId(productId);
		logDebug("service method - Configutation - " + bvSdkConfiguration);
		logDebug("service method -  Full Url Is : " + fullURL);
		logDebug("service method -  BVUtilty.getQueryString(fullURL)- " +  BVUtility.getQueryString(fullURL));		
		BVUIContent bvContentProvider= new BVManagedUIContent(bvSdkConfiguration);
		String bvContent = bvContentProvider.getContent(bvParameters);
		if (!bvContent.isEmpty()) {
			pRequest.setParameter(BVCONTENT, bvContent);			
		}else if (!bvContent.contains(JSONLY)){ 
			logError("BVContentDroplet.Service received no SEO Content");
		}
		logDebug("Content received from bazaarvoice seo call: " + bvContent);
		pRequest.serviceLocalParameter(OUTPUT , pRequest, pResponse);
	}
	
	/**
	 * This method prepares the query string.
	 * 
	 * @param pRequest instance of DynamoHttpServletRequest
	 */
	private String getQueryString (DynamoHttpServletRequest pRequest) {
		StringBuilder queryParameters = new StringBuilder();
		String queryParam = null;
		String queryString = pRequest.getQueryString();
		String[] queryParams = queryString.split("&");
		int length = queryParams.length;
		/*if (length == 2) {
			return null;
		}*/
		queryParameters.append("?");
		for (int i = 0; i < length; i++) {
			queryParam = queryParams[i];
			/*if (queryParam.contains("productId") || queryParam.contains("contentKey")) {
				continue;
			}*/
			queryParameters.append(queryParam);
			if (i < length - 1) {
				queryParameters.append("&");
			}			
		}
		return queryParameters.toString();
	}

	/**
	 * This method prepares the host string.
	 * 
	 * @param pRequest instance of DynamoHttpServletRequest
	 */
	private String getHost(DynamoHttpServletRequest pRequest) {

		
		logDebug("[Start]: getHost()");
		
		String hostStr = null;

		if (pRequest != null) {
			String url = pRequest.getRequestURL().toString();
			String contextPath = pRequest.getContextPath();

			hostStr = url.split(contextPath)[0].concat(contextPath);

			
			logDebug("url: " + url);
			logDebug("contextPath: " + contextPath);
			logDebug("hostpath after context path split: " + hostStr);
			
		} else {
			
				logWarning("Request object is null");
			
		}
		
			logDebug("[End]: getHost()");
		
		return hostStr;
	}

	public String getSdkEnabled() {
		return sdkEnabled;
	}

	public void setSdkEnabled(String sdkEnabled) {
		this.sdkEnabled = sdkEnabled;
	}

	public String getExecutionTimeOut() {
		return executionTimeOut;
	}

	public void setExecutionTimeOut(String executionTimeOut) {
		this.executionTimeOut = executionTimeOut;
	}

	public String getBotExecutionTimeOut() {
		return botExecutionTimeOut;
	}

	public void setBotExecutionTimeOut(String botExecutionTimeOut) {
		this.botExecutionTimeOut = botExecutionTimeOut;
	}

	public String getCloudKey() {
		return cloudKey;
	}

	public void setCloudKey(String cloudKey) {
		this.cloudKey = cloudKey;
	}

	public String getBvRootFolder() {
		return bvRootFolder;
	}

	public void setBvRootFolder(String bvRootFolder) {
		this.bvRootFolder = bvRootFolder;
	}

	public String getIncludeDisplayIntCode() {
		return includeDisplayIntCode;
	}

	public void setIncludeDisplayIntCode(String includeDisplayIntCode) {
		this.includeDisplayIntCode = includeDisplayIntCode;
	}

	public String getLoadSEOFilesLocally() {
		return loadSEOFilesLocally;
	}

	public void setLoadSEOFilesLocally(String loadSEOFilesLocally) {
		this.loadSEOFilesLocally = loadSEOFilesLocally;
	}

	public String getSslEnabled() {
		return sslEnabled;
	}

	public void setSslEnabled(String sslEnabled) {
		this.sslEnabled = sslEnabled;
	}
	
	
}
