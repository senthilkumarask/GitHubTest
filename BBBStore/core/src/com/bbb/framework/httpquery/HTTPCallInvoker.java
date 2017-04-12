package com.bbb.framework.httpquery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.transaction.SystemException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import net.sf.json.JSONArray;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.inventory.vo.SBAuthTokenResponseVO;
import com.bbb.commerce.porch.service.vo.PorchBookAJobResponseVO;
import com.bbb.commerce.porch.service.vo.PorchSchedlueJobResponseVO;
import com.bbb.commerce.porch.service.vo.PorchValidateZipCodeResponseVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.sdd.vo.response.SddVOResponse;
import com.bbb.utils.BBBUtility;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;


/**
 * @author Sapient
 *
 */
public class HTTPCallInvoker extends BBBGenericService 
                    implements HTTPInvokerIF {

    private static final String CLS_NAME = "CLS=[HTTPCallInvoker]/MSG::";
    private static final String HTTP_CONNECTION_ATTRIBS = "HTTPConnectionAttributes";
	private String dummyQuery;
	private boolean defaultStaleCheckEnabled;
	private int defaultConnectionTimeout;
	private int defaultSocketTimeout;
	private int defaultMaxTotalConnections;
	private int defaultMaxHostConnectionsPerHost;
	private boolean jobsJVM;
	private List<String> serviceUnavailableStatus;
	
	/**
	 * @return defaultStaleCheckEnabled
	 */
	public boolean isDefaultStaleCheckEnabled() {
		return this.defaultStaleCheckEnabled;
	}
   

	/**
	 * @param defaultStaleCheckEnabled
	 */
	public void setDefaultStaleCheckEnabled(final boolean defaultStaleCheckEnabled) {
		this.defaultStaleCheckEnabled = defaultStaleCheckEnabled;
	}


	/**
	 * @return defaultConnectionTimeout
	 */
	public int getDefaultConnectionTimeout() {
		return this.defaultConnectionTimeout;
	}


	/**
	 * @param defaultConnectionTimeout
	 */
	public void setDefaultConnectionTimeout(final int defaultConnectionTimeout) {
		this.defaultConnectionTimeout = defaultConnectionTimeout;
	}


	/**
	 * @return defaultSocketTimeout
	 */
	public int getDefaultSocketTimeout() {
		return this.defaultSocketTimeout;
	}


	/**
	 * @param defaultSocketTimeout
	 */
	public void setDefaultSocketTimeout(final int defaultSocketTimeout) {
		this.defaultSocketTimeout = defaultSocketTimeout;
	}


	/**
	 * @return defaultMaxTotalConnections
	 */
	public int getDefaultMaxTotalConnections() {
		return this.defaultMaxTotalConnections;
	}


	/**
	 * @param defaultMaxTotalConnections
	 */
	public void setDefaultMaxTotalConnections(final int defaultMaxTotalConnections) {
		this.defaultMaxTotalConnections = defaultMaxTotalConnections;
	}


	/**
	 * @return defaultMaxHostConnectionsPerHost
	 */
	public int getDefaultMaxHostConnectionsPerHost() {
		return this.defaultMaxHostConnectionsPerHost;
	}


	/**
	 * @param defaultMaxHostConnectionsPerHost
	 */
	public void setDefaultMaxHostConnectionsPerHost( final int defaultMaxHostConnectionsPerHost) {
		this.defaultMaxHostConnectionsPerHost = defaultMaxHostConnectionsPerHost;
	}

		
	public boolean isJobsJVM() {
		return jobsJVM;
	}

   /**
    * 
    * @param jobsJVM
    */
	public void setJobsJVM(boolean jobsJVM) {
		this.jobsJVM = jobsJVM;
	}


	/**
	 * This method is used to call the webservices whether the call 
	 * is PUT, GET or POST.
	 * The method accepts Json and returns JAVA object 
	 * @param unusedobject
	 * @param headerParam
	 * @param url
	 * @param param
	 * @param httpType
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws BBBBusinessException 
	 * @throws Exception 
	 */
	
	public <T>T invokeToGetJson(Class<T> object, HashMap<String,String> headerParam , String url, HashMap<String,String> param , String httpType) throws BBBSystemException, BBBBusinessException {
		
		logDebug("Invoking the webservice");
		
		if(httpType.equalsIgnoreCase(BBBCoreConstants.GET)){
			return doHttpGetForJson(object, headerParam, url, false);
		}else if(httpType.equalsIgnoreCase(BBBCoreConstants.PUT)){
			return doHttpPutForJson(object, headerParam, url, param);
		}else if(httpType.equalsIgnoreCase(BBBCoreConstants.POST)){
			return doHttpPostForJson(object, headerParam, url, param);
		}else if(httpType.equalsIgnoreCase("porch")){
			return doHttpPostForPorchJson(object, headerParam, url, param);
		}
		
		else{
			
			throw new BBBSystemException("http type doesnot match GET, PUT or POST");
		}
	}
	
	
	/**
	 * @param object
	 * @param headerParam
	 * @param url
	 * @param param
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public <T> T  doHttpPostForPorchJson(Class<T> object, HashMap<String, String> headerParam, String url,
			HashMap<String, String> param) throws BBBSystemException, BBBBusinessException {

		
		logDebug("Calling HTTP POST ");
		logDebug("The contructed URL is " + url);
		HttpPost httpPost = new HttpPost(url);
		
		Iterator<Entry<String, String>> headerParamIterator = headerParam.entrySet().iterator();
		
		while(headerParamIterator.hasNext()){
			Map.Entry<String, String> pair = headerParamIterator.next();
			httpPost.addHeader(pair.getKey(),pair.getValue());
			
		}
		
		 
			try {
				StringEntity se = new StringEntity(param.get("json"));
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				httpPost.setEntity(se);
			 
			} catch (UnsupportedEncodingException e) {
				logError("Error while encoding the string",e);
			}
		 
		
	 
	        
		return invokePorchToGetJson(object, httpPost);
	
		
	}


	/**
	 * @param object
	 * @param httpUriRequest
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private  <T> T invokePorchToGetJson(Class<T> object, HttpUriRequest httpUriRequest) throws BBBSystemException, BBBBusinessException {
		HttpResponse httpResponse = null;
		
		if(httpClient == null){	
			
			this.initializeHttpClient();
					
		}	
		
		final long startTime = System.currentTimeMillis();
		 try {
				httpResponse = httpClient.execute(httpUriRequest);
				
				final long endTime = System.currentTimeMillis() - startTime;		
				
				logDebug("Total Time taken for calling the web service is " + endTime);
				
				return getJson(object, httpResponse , httpUriRequest,false);
			
		 } catch (ClientProtocolException e) {
				httpUriRequest.abort();
				logError("Error occurred while executing webservice", e); 
		        throw new BBBSystemException("Exception in calling web service using http client", e);
		        
			} catch (IOException e) {
				httpUriRequest.abort();
				logError("Error occurred while executing webservice", e); 
		        throw new BBBSystemException("Exception in calling web service using http client", e);
			} catch (IllegalStateException e) {
				httpUriRequest.abort();
				logError("Error occurred while executing webservice", e); 
		        throw new BBBSystemException("Exception in calling web service using http client", e);
			}
		
		
		
	}
	
	 

	/**
	 * This method calls http get with input parameter appended in the url.
	 * 
	 * @param object
	 * @param headerParam
	 * @param url
	 * @param params
	 * @param isSDDCommitOrderFlow
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public <T> T doHttpGetWithParam(Class<T> object, HashMap<String, String> headerParam, String url,
			List<NameValuePair> params, boolean isSDDCommitOrderFlow) throws BBBSystemException, BBBBusinessException {
		logDebug("HTPCallInvoker.doHttpGetWithParam: Starts.");
		StringBuilder httpUrl = new StringBuilder(url);
		if (null != params) {
			int count = 1;
			for (NameValuePair param : params) {
				if (count == 1) {
					httpUrl.append(BBBCoreConstants.QUESTION_MARK);
				}
				httpUrl.append(param.getName()).append(BBBCoreConstants.EQUAL).append(param.getValue());
				if (count < params.size()) {
					httpUrl.append(BBBCoreConstants.AMPERSAND);
				}
				count++;
			}
		}
		logDebug("HTPCallInvoker.doHttpGetWithParam: Ends, calling doHttpGetForJson() with url: " + httpUrl.toString());
		return doHttpGetForJson(object, headerParam, httpUrl.toString(), isSDDCommitOrderFlow);
	}

	/**
	 * This method is used to call web service with GET call
	 * 
	 * @param object
	 * @param headerParam
	 * @param url
	 * @param isSDDCommitOrderFlow
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private <T> T doHttpGetForJson(Class<T> object,
			HashMap<String, String> headerParam, String url, boolean isSDDCommitOrderFlow) throws BBBSystemException, BBBBusinessException {
		
		logDebug("Calling the GET webservice");
		
		HttpGet httpGet = new HttpGet(url);
		Iterator<Entry<String, String>> paramIterator = headerParam.entrySet().iterator();
		
		while(paramIterator.hasNext()){
			Map.Entry<String, String> pair = paramIterator.next();
			httpGet.addHeader(pair.getKey(),pair.getValue());
			
		}		
		return invokeToGetJson(object, httpGet, false);
	}
	
	/**
	 * This methods is used to invoke the webservice given the paramters
	 * 
	 * @param object
	 * @param httpUriRequest
	 * @param isSDDCommitOrderFlow
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public <T> T invokeToGetJson(Class<T> object, HttpUriRequest httpUriRequest, boolean isSDDCommitOrderFlow) throws BBBSystemException, BBBBusinessException {
		HttpResponse httpResponse = null;
		
		if(httpClient == null){	
			
			this.initializeHttpClient();
					
		}	
		
		final long startTime = System.currentTimeMillis();
		 try {
				httpResponse = httpClient.execute(httpUriRequest);
				
				final long endTime = System.currentTimeMillis() - startTime;		
				
				logDebug("Total Time taken for calling the web service is " + endTime);
				
				return getJson(object, httpResponse , httpUriRequest, isSDDCommitOrderFlow);
			
		 } catch(SocketTimeoutException e) {
	        	httpUriRequest.abort();
	            logError(CLS_NAME + "SocketTimeoutException in calling web service using http client", e);
	            if(isSDDCommitOrderFlow) {
	        		logInfo(CLS_NAME + " Unable to Place SDD Order due to SocketTimeout while calling EOM service.");
	        	}
	            throw new BBBSystemException(e.getMessage());
	        } catch (ClientProtocolException e) {
				httpUriRequest.abort();
				logError("Error occurred while executing webservice", e); 
		        throw new BBBSystemException("Exception in calling web service using http client", e);
		        
			} catch (IOException e) {
				httpUriRequest.abort();
				logError("Error occurred while executing webservice", e); 
		        throw new BBBSystemException("Exception in calling web service using http client", e);
			} catch (IllegalStateException e) {
				httpUriRequest.abort();
				logError("Error occurred while executing webservice", e); 
		        throw new BBBSystemException("Exception in calling web service using http client", e);
			}
		
		
		
	}

	/**
	 * This Method returns the java object using the json response
	 * 
	 * @param object
	 * @param httpResponse
	 * @param httpUriRequest
	 * @param isSDDCommitOrderFlow
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 * @throws BBBBusinessException 
	 */
	public <T> T getJson(Class<T> object, HttpResponse httpResponse, HttpUriRequest httpUriRequest, boolean isSDDCommitOrderFlow) throws BBBSystemException, IOException, BBBBusinessException {
		String json = null;
		Gson gson=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
		
		if(object == PorchBookAJobResponseVO.class || object == PorchSchedlueJobResponseVO.class || object ==PorchValidateZipCodeResponseVO.class){
			gson = new GsonBuilder().create();
		}
		if(object == PorchValidateZipCodeResponseVO.class && null==httpResponse.getEntity()){			
			return gson.fromJson(json,object);
		}
		try{
			json = EntityUtils.toString(httpResponse.getEntity());
			final int statusCode = httpResponse.getStatusLine().getStatusCode();
			if((statusCode == HttpStatus.SC_OK ||
					(( statusCode == HttpStatus.SC_CREATED  
						|| statusCode == HttpStatus.SC_NO_CONTENT ) && object == SddVOResponse.class )) && json !=null){			
				return  gson.fromJson(json,object);
			}else if(json!=null && statusCode != HttpStatus.SC_OK  && object == PorchBookAJobResponseVO.class){ 
				json = parsePorchJsonErrorResponse(json);
				return  gson.fromJson(json,object);
				
			} else if(isSDDCommitOrderFlow && null != getServiceUnavailableStatus() && getServiceUnavailableStatus().contains(String.valueOf(statusCode))){
				logInfo(CLS_NAME + " Unable to Place SDD Order because the EOM service is down or unresponsive");
				throw new BBBSystemException(" Unable to Place SDD Order because the EOM service is down or unresponsive");
			} else if((statusCode == HttpStatus.SC_BAD_REQUEST ||statusCode == HttpStatus.SC_UNAUTHORIZED) && object == SBAuthTokenResponseVO.class){
				throw new BBBBusinessException("Unable to proceed with order placement as we got status code: " + statusCode);
			} else {
				throw new BBBSystemException("Response code is:" + statusCode);
			}
			
		} catch(JsonSyntaxException e){
			httpUriRequest.abort();
			logError("Error occurred while executing webservice", e); 
        	throw new BBBSystemException("Exception in calling web service using http client", e);
        	
		} finally{
				try {
	                if (httpResponse.getEntity() != null) {
	                     EntityUtils.consume(httpResponse.getEntity());
	                }
	            } catch (IOException e) {
					logError("Error occurred while releasing the HTTP connection to the pool", e); 
	            } finally {
	                    logDebug("calling webservice with URL:" + httpUriRequest.getURI()); 
	                    logDebug("Response from webservice with URL:" + httpUriRequest.getURI() + "is :" + json);
	            }
		}
		
	}

	/**
	 * @param json
	 * @return
	 */
	private String parsePorchJsonErrorResponse(String json)   {
		net.sf.json.JSONObject porchResponse = new net.sf.json.JSONObject();
		JSONArray porchJobs= new JSONArray();
		JSONArray sampl = null;
		boolean singleStringValue=false;
		try{
		sampl= (JSONArray) net.sf.json.JSONSerializer.toJSON(json);
		}
		catch(net.sf.json.JSONException ex){
			singleStringValue=true;
		}
		
		porchResponse.put("porchUserId", null);
		if(singleStringValue){
			  
			net.sf.json.JSONObject jobs = new net.sf.json.JSONObject();
			
			jobs.put("success", "false");
			jobs.put("errorCode", "400");
			jobs.put("jobId", null);
			jobs.put("partnerSku", null);
			jobs.put("projectLifecycleId", null);
			
			jobs.put("errorDetails", json);
			porchJobs.add(jobs);
		} else{
		for (int i = 0; i < sampl.size(); i++) {
			net.sf.json.JSONObject ob = sampl.getJSONObject(i);
			String errorMessage = (String) ob.get("message");
			net.sf.json.JSONObject jobs = new net.sf.json.JSONObject();
			
			jobs.put("success", "false");
			jobs.put("errorCode", "400");
			jobs.put("jobId", null);
			jobs.put("partnerSku", null);
			jobs.put("projectLifecycleId", null);
			
			jobs.put("errorDetails", errorMessage);
			porchJobs.add(jobs);
			 
			 
			}
	}
		porchResponse.put("jobs", porchJobs);
		String jsonreponse = porchResponse.toString();
		
		
		return jsonreponse;
	}


	/**
	 * This method is used to call PUT
	 * @param unusedobject
	 * @param headerParam
	 * @param url
	 * @param param
	 * @return
	 * @throws BBBBusinessException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Exception 
	 */
	public <T> T doHttpPutForJson(Class<T> object,
			HashMap<String, String> headerParam, String url,
			HashMap<String, String> param) throws BBBSystemException, BBBBusinessException {
		
		logDebug("Calling HTTP PUT ");
		logDebug("The contructed URL is " + url);
		HttpPut httpPut = new HttpPut(url);
		
		Iterator<Entry<String, String>> headerParamIterator = headerParam.entrySet().iterator();
		
		while(headerParamIterator.hasNext()){
			Map.Entry<String, String> pair = headerParamIterator.next();
			httpPut.addHeader(pair.getKey(),pair.getValue());
			
		}		
		
		Iterator<Entry<String,String>> paramIterator = param.entrySet().iterator();
		JSONObject jsonObject = new JSONObject();
		while(paramIterator.hasNext()){
			
			Map.Entry<String, String> pair = paramIterator.next();
			try {
				jsonObject.put(pair.getKey(), pair.getValue());
			} catch (JSONException e) {
				logError("Error while adding value to json object",e);
			}
		}
		
		StringEntity entity = null;
		try {
			entity = new StringEntity(jsonObject.toString());
		} catch (UnsupportedEncodingException e1) {
			logError("Error while encoding the string",e1);
		}
		
		if(jsonObject!=null && entity!=null){
			httpPut.setEntity(entity);
		}
		
		return invokeToGetJson(object, httpPut, false);
	}
	
	/**
	 * This method is used to call POST
	 * @param unusedobject
	 * @param headerParam
	 * @param url
	 * @param param
	 * @return
	 * @throws BBBBusinessException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Exception 
	 */
	public <T> T doHttpPostForJson(Class<T> object,
			HashMap<String, String> headerParam, String url,
			HashMap<String, String> param) throws BBBSystemException, BBBBusinessException {
		
		logDebug("Calling HTTP POST ");
		logDebug("The contructed URL is " + url);
		HttpPost httpPost = new HttpPost(url);
		
		Iterator<Entry<String, String>> headerParamIterator = headerParam.entrySet().iterator();
		
		while(headerParamIterator.hasNext()){
			Map.Entry<String, String> pair = headerParamIterator.next();
			httpPost.addHeader(pair.getKey(),pair.getValue());
			
		}		
		
		Iterator<Entry<String,String>> paramIterator = param.entrySet().iterator();
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		while(paramIterator.hasNext()){
			Map.Entry<String, String> pair = paramIterator.next();
			urlParameters.add(new BasicNameValuePair(pair.getKey(), pair.getValue()));
		}
		
	
		if(urlParameters!=null){
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
			} catch (UnsupportedEncodingException e) {
				logError("Error while encoding the string",e);
			}
		}
		
		return invokeToGetJson(object, httpPost, false);
	}
	
	

    /**
     * This is public method called by HTTPQueryManager for executing http query on web
     * service.
     * 
     * @param targetURL
     * @param requiredParams
     * @param optionalParams
     * @param paramValues
     * @return string
     * @throws BBBBusinessException
     * @throws BBBSystemException
     */
    public String invoke( final String targetURL,final List<String> requiredParams,
            final List<String> optionalParams, final Map<String,String> paramValues) 
                    throws BBBBusinessException, BBBSystemException {
        
        final String completeURL = this.constructQuery(targetURL, requiredParams, optionalParams ,
                paramValues);
        DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        logDebug( CLS_NAME +"completeURL="+completeURL);
        final String methodName = "CLS=HTTPInvoker::MTHD=invoke";
        
        //BBBSL-6574 | Setting requestUrl and reponseXml of Certona
        if(null!= targetURL && targetURL.contains(BBBCoreConstants.CERTONA_TARGET_URL)){
        	 request.setParameter(BBBCoreConstants.CERTONA_REQUEST_URL, completeURL);
        }
        BBBPerformanceMonitor.start("HTTPInvoker", methodName);
        String responseObject = this.executeQuery(completeURL);
        BBBPerformanceMonitor.end("HTTPInvoker", methodName);
        
        //if responseObject is null or SKU is not in response
        if(BBBUtility.isEmpty(responseObject)){
            responseObject = getDummyQuery();
        }
        //BBBSL-6574 | Setting requestUrl and reponseXml of Certona
        if(null!= targetURL && targetURL.contains(BBBCoreConstants.CERTONA_TARGET_URL)){
	        request.setParameter(BBBCoreConstants.CERTONA_RESPONSE_XML, responseObject);
        }
  
        logDebug( CLS_NAME +"responseObject="+responseObject);

        return responseObject;
    }
    
	/**
     * This is public method called by HTTPQueryManager for executing http query on web
     * service.
	 * @param targetURL
     * @param requiredParams
     * @return
     * @throws BBBBusinessException
     * @throws BBBSystemException
     */
    public final String invoke(final String targetURL, final List<NameValuePair> requiredParams) 
                    throws BBBBusinessException, BBBSystemException {
        
        final String methodName = "CLS=HTTPInvoker::MTHD=invoke"; //$NON-NLS-1$
                
        BBBPerformanceMonitor.start("HTTPInvoker", methodName); //$NON-NLS-1$
        final String responseObject = this.executePostQuery(targetURL, requiredParams);
        BBBPerformanceMonitor.end("HTTPInvoker", methodName); //$NON-NLS-1$
        
        logDebug(CLS_NAME + "responseObject=" + responseObject); //$NON-NLS-1$
        return responseObject;
    }    

    /**
	 * This is public method called by HTTPQueryManager to execute the XML
	 * request with specified header values and POST request.
	 * 
	 * @param targetURL
	 * @param requestXml
	 * @param headerParams
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public final String invokeIntlCartSubmission(final String targetURL, final String requestXml,
			final Map<String, String> headerParams) throws BBBBusinessException,
			BBBSystemException {
		final String methodName = "CLS=HTTPInvoker::MTHD=invokeIntlCartSubmission"; //$NON-NLS-1$

		BBBPerformanceMonitor.start("HTTPInvoker", methodName); //$NON-NLS-1$
		final String responseObject = this.executeIntlCartSubmitQuery(targetURL,
				requestXml, headerParams);
		BBBPerformanceMonitor.end("HTTPInvoker", methodName); //$NON-NLS-1$

		logDebug(CLS_NAME + "responseObject=" + responseObject); //$NON-NLS-1$
		return responseObject;
	}

	/**
	 * Method that initialize the HttpClient parameters for specified headers
	 * and POST request.
	 * 
	 * @param targetURL
	 * @param requestXml
	 * @param headerParams
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private String executeIntlCartSubmitQuery(final String targetURL,
			final String requestXml, final Map<String, String> headerParams)
			throws BBBBusinessException, BBBSystemException {
		final long startTime = System.currentTimeMillis();

		if (httpClient == null) {
			this.initializeHttpClient();
		}

		final String response = callIntlCartSubmitService(targetURL, requestXml,
				headerParams);
		final long endTime = System.currentTimeMillis();

		logDebug(CLS_NAME + " Total Time Taken by execute() Method " //$NON-NLS-1$
				+ (endTime - startTime) + " response from webservice=" //$NON-NLS-1$
				+ response);

		return response;
	}

	/**
	 * Method that call International Cart Submit web service using HttpClient.
	 * 
	 * @param url
	 * @param requestXml
	 * @param headerParams
	 * @return
	 * @throws BBBSystemException
	 */
	private String callIntlCartSubmitService(final String targetURL,
			final String requestXml, final Map<String, String> headerParams)
			throws BBBSystemException {
		BBBPerformanceMonitor.start("HTTPCallInvoker callIntlCartSubmitService"); //$NON-NLS-1$

		logDebug(CLS_NAME
				+ "Entering callIntlCartSubmitService() method || Webservice call will be made"); //$NON-NLS-1$

		HttpEntity entity = null;
		String response = null;
		final HttpPost httpURL = new HttpPost();

		try {
			httpURL.setURI(new URI(targetURL));
			if (headerParams != null && headerParams.size() > 0) {
				for (Map.Entry<String, String> header : headerParams.entrySet()) {
					httpURL.setHeader(header.getKey(), header.getValue());
				}
			}
			httpURL.setEntity(new ByteArrayEntity(requestXml.getBytes("UTF-8")));
			final HttpResponse webResponse = httpClient.execute(httpURL);
			final int status = webResponse.getStatusLine().getStatusCode();
			entity = webResponse.getEntity();
			if (status == HttpStatus.SC_OK) {
				if (entity == null) {
					throw new BBBSystemException("Response code is:" + status //$NON-NLS-1$
							+ " |Empty entity received"); //$NON-NLS-1$
				}
				response = EntityUtils.toString(entity, BBBCoreConstants.UTF_8);
			} else {
				// Ensures that the entity content is fully consumed and the
				// content stream, if exists, is closed.
				EntityUtils.consume(entity);
				throw new BBBSystemException(
						"Response code is:" //$NON-NLS-1$
								+ status
								+ " |Invalid response received from the webservice host"); //$NON-NLS-1$
			}
		} catch (final URISyntaxException e) {
			httpURL.abort();
			logError(CLS_NAME + "ERROR: web service URL is incorrect- " + targetURL, e);
			throw new BBBSystemException("ERROR: web service URL is incorrect- " + targetURL, e);
		} catch (final UnsupportedEncodingException e) {
			httpURL.abort();
			logError(CLS_NAME + "ERROR: ByteArrayEntity XML encoding is not supported for requestXML.", e);
			throw new BBBSystemException("ERROR: ByteArrayEntity XML encoding is not supported for requestXML.", e);
		} catch (final ClientProtocolException e) {
			httpURL.abort();
			logError(CLS_NAME + "ERROR: HttpClient can not execute the POST request.", e);
			throw new BBBSystemException("ERROR: HttpClient can not execute the POST request.", e);
		} catch (final IOException e) {
			httpURL.abort();
			logError(
					CLS_NAME
							+ "ERROR: The followings could be the reason: \n1) Encode requestXml String into a sequence of bytes using UTF-8 failed. \n2) Query execution using HttpClient failed. \n3) Conversion of valid response or error response using HttpEntity failed.",
					e);
			throw new BBBSystemException(
					"ERROR: The followings could be the reason: \n1) Encode requestXml String into a sequence of bytes using UTF-8 failed. \n2) Query execution using HttpClient failed. \n3) Conversion of valid response or error response using HttpEntity failed.",
					e);
		} catch (final BBBSystemException e) {
			httpURL.abort();
			logError(CLS_NAME + "ERROR occurred while executing webservice", e);
			throw new BBBSystemException("ERROR: Exception in calling web service using http client", e);
		} finally {
			try {
				if (entity != null) {
					// Ensures that the entity content is fully consumed and the
					// content stream, if exists, is closed.
					EntityUtils.consume(entity);
				}
			} catch (IOException e) {
				logError(
						CLS_NAME
								+ " Error occurred while releasing the HTTP connection to the pool", e); //$NON-NLS-1$
			} finally {
				logDebug("calling webservice with URL:" + targetURL); //$NON-NLS-1$
			}
		}
		logDebug(CLS_NAME + " exitting callIntlCartSubmitService() method"); //$NON-NLS-1$
		BBBPerformanceMonitor.end("HTTPCallInvoker callIntlCartSubmitService end."); //$NON-NLS-1$
		return response;
	}

	/**
     * This method constructs complete query. The query will be in form of complete url with
     * query string as shown below
     * <targetURL>?param1=value1&param2=value2
     * 
     * @param targetURL
     * @param requiredParams
     * @param optionalParams
     * @param paramValues
     * @return string
     */
    @Override
	public String constructQuery(final String targetURL, final List<String> requiredParams, 
            final List<String> optionalParams, final Map<String,String> paramsValuesMap) throws BBBBusinessException {

        final StringBuffer completeQueryURL = new StringBuffer(targetURL);
        
        //flag to know if the parameter is first then do not prepend &
        boolean firstParameter = true;
        
        //if there is atleast one parameter then append ? for query string
        if( !requiredParams.isEmpty() || !optionalParams.isEmpty()){
            completeQueryURL.append('?');
        }
        
        //add optional parameters to query string
        final Iterator<String> reqParamsIter= requiredParams.iterator();
        while(reqParamsIter.hasNext()){
            
            final String paramName = reqParamsIter.next(); 
            final String paramValue =   paramsValuesMap.get(paramName);
            
            if( paramValue ==null ){
                throw new BBBBusinessException("ERR_HTTP_QUERY_MISSING_REQ_PARAM");
            }
            
            if(firstParameter){
                firstParameter = false;
            }else{
                completeQueryURL.append(BBBCoreConstants.AMPERSAND);
            }
            
            completeQueryURL.append(paramName).append(BBBCoreConstants.EQUAL).append(paramValue);
        }

        logDebug( CLS_NAME +"completeQueryURL with reqParams:: "+completeQueryURL);
        
        //add optional parameters to query string
        final Iterator<String> optParamsIter= optionalParams.iterator();
        while(optParamsIter.hasNext()){
            
            final String paramName = optParamsIter.next(); 
            final String paramValue =   paramsValuesMap.get(paramName);
            
            if( paramValue ==null ){
                continue;
            }

            if(firstParameter){
                firstParameter = false;
            }else{
                completeQueryURL.append(BBBCoreConstants.AMPERSAND);
            }
            completeQueryURL.append(paramName).append(BBBCoreConstants.EQUAL).append(paramValue);
        }
        
        logDebug( CLS_NAME +"completeQueryURL after including optionalPaams:: "+completeQueryURL);

        return completeQueryURL.toString();
    }

    private static volatile HttpClient httpClient;
    BBBCatalogTools mCatalogTools;  
    private static final String STALE_CHECK_ENABLED = "STALE_CHECK_ENABLED";
    private static final String CONNECTION_TIME_OUT = "CONNECTION_TIME_OUT";
    private static final String SOCKET_TIME_OUT = "SOCKET_TIME_OUT";
    private static final String MAX_TOTAL_CONNECTIONS = "MAX_TOTAL_CONNECTIONS";
    private static final String MAX_TOTAL_CONN_PER_HOST = "MAX_TOTAL_CONN_PER_HOST";
    private static final String REFERER_URL = "referer_url";
    // Connection,socket time out  config keys for Omniture.
    private static final String JOBS_JVM_CONNECTION_TIME_OUT = "JOBS_JVM_CONNECTION_TIME_OUT";
    private static final String JOBS_JVM_SOCKET_TIME_OUT = "JOBS_JVM_SOCKET_TIME_OUT";
    
    private void initializeHttpClient() 
            throws BBBBusinessException, BBBSystemException {
        
        logDebug( CLS_NAME +"Entering initializeHttpClient() method ");
        
        boolean staleCheckEnabled = true;
        int connectionTimeout = 0;
        int socketTimeout = 0;
        int maxTotalConnections = 0;
        int maxHostConnectionsPerHost = 0;
        
                
        
        if (null == getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, STALE_CHECK_ENABLED).get(0)) {
        	staleCheckEnabled = isDefaultStaleCheckEnabled();
        } else {
        	staleCheckEnabled = Boolean.parseBoolean(getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, STALE_CHECK_ENABLED).get(0));
        }
        
		/**
		 * BBBI-2682 jobsJVM flag is checked to determine when the connection &
		 * socket time out should be set to different values.
		 * 
		 */

		if (isJobsJVM()) {
			connectionTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS,JOBS_JVM_CONNECTION_TIME_OUT).get(0));

		} else if (null == getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, CONNECTION_TIME_OUT).get(0)) {
			connectionTimeout = getDefaultConnectionTimeout();
		} else {
			connectionTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS,CONNECTION_TIME_OUT).get(0));
		}

		if (isJobsJVM()) {
			socketTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS,JOBS_JVM_SOCKET_TIME_OUT).get(0));
		} else if (null == getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, SOCKET_TIME_OUT).get(0)) {
			socketTimeout = getDefaultSocketTimeout();
		} else {
			socketTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS,SOCKET_TIME_OUT).get(0));
		}

        if (null == getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, MAX_TOTAL_CONNECTIONS).get(0)) {
        	maxTotalConnections = getDefaultMaxTotalConnections();
        } else {
        	maxTotalConnections = Integer.parseInt(getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, MAX_TOTAL_CONNECTIONS).get(0));
        }
        
        if (null == getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, MAX_TOTAL_CONN_PER_HOST).get(0)) {
        	maxHostConnectionsPerHost = getDefaultMaxHostConnectionsPerHost();
        } else {
        	maxHostConnectionsPerHost = Integer.parseInt(getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, MAX_TOTAL_CONN_PER_HOST).get(0));
        }
        
        logDebug( CLS_NAME +"staleCheckEnabled="+staleCheckEnabled
                    +"connectionTimeout="+connectionTimeout
                    +"socketTimeout="+socketTimeout
                    +"maxTotalConnections="+maxTotalConnections
                    +"maxHostConnectionsPerHost="+maxHostConnectionsPerHost);
        
        
        if(httpClient == null){
        	final PoolingHttpClientConnectionManager clientConnectionManager  = new PoolingHttpClientConnectionManager();
            clientConnectionManager.setMaxTotal(maxTotalConnections);
            clientConnectionManager.setDefaultMaxPerRoute(maxHostConnectionsPerHost);
            
            final RequestConfig.Builder config =  RequestConfig.custom();
            config.setStaleConnectionCheckEnabled(staleCheckEnabled);
            config.setSocketTimeout(socketTimeout);
            config.setConnectTimeout(connectionTimeout);
            
        	httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultRequestConfig(config.build()).build();
        }
       
        logDebug( CLS_NAME +" exit from initializeHttpClient() method :httpClient="+httpClient);
    }
    
    private String requestURL;
    private String response;
    
    public void invoke(){
    	try {
    		setRequestURL("https://eomdev1.bedbath.com:30001/oauth/token?grant_type=password&username=manh&password=password&client_id=sp-module-ci&client_secret=B9DCC4D0-388F-4E7E-9758-CD3EFA665E02&scope=write");
    		System.setProperty("javax.net.ssl.keyStore","C:/Users/jethir/Desktop/Desktop/eomdev1bedbathcom.jks");
    		System.setProperty("javax.net.ssl.keyStorePassword","changeit");
    		System.setProperty("javax.net.ssl.trustStore","C:/Users/jethir/Desktop/Desktop/eomdev1bedbathcom.jks");
    		System.setProperty("javax.net.ssl.trustStorePassword","changeit");
			setResponse(callWebService(getRequestURL()));
		} catch (BBBSystemException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Method that call web service using http client
     * 
     * @param url
     * @return string
     * @throws BBBSystemException 
     * @throws SystemException 
     */
    public String callWebService(final String url) throws BBBSystemException {
        BBBPerformanceMonitor.start("HTTPCallInvoker callWebService");
        
        logDebug( CLS_NAME +"Entering callWebService() method || Webservice call will be made");
        
        HttpEntity entity = null;
        String response = null;
        final HttpGet httpURL=new HttpGet();
        try {
        	if(null == httpClient){
        		initializeHttpClient();
        	}
            httpURL.setURI(new URI(url));
            httpURL.setHeader(BBBCoreConstants.REFERRER, "http://bedbathandbeyond.com:7003");
            final HttpResponse webResponse = httpClient.execute(httpURL);
            final int status = webResponse.getStatusLine().getStatusCode();
            entity = webResponse.getEntity();
            /*
             * Fetching the Status Code.If Status Code is not OK, error will be
             * thrown.
             */
			if (status == HttpStatus.SC_OK) {
				if (entity == null) {
					if(isLoggingError()){
						logError("Certona request String : " + url + " HttpResponse : " + webResponse);
					}
					throw new Exception("Response code is:" + status
							+ " |Empty entity received");
				}
				response = EntityUtils.toString(entity, BBBCoreConstants.UTF_8);
			} else {
				if(isLoggingError()){
					logError("Certona request String : " + url);
				}
				// consume the error response if any
				EntityUtils.consume(entity);
				 BBBPerformanceMonitor.end("HTTPCallInvoker callWebService");
				throw new Exception(
						"Response code is:"
								+ status
								+ " |Invalid response received from the webservice host");
			}
		}
        //SAP-345 start
            
        catch(SocketTimeoutException e)
        {
        	 BBBPerformanceMonitor.end("HTTPCallInvoker callWebService");
        	httpURL.abort();
            logError(CLS_NAME + "Error occurred while executing webservice , SocketTimeoutException");
    	throw new BBBSystemException("Exception in calling web service using http client", e);
        }
        
        //SAP-345 end  
         catch (Exception e) {
        	 BBBPerformanceMonitor.end("HTTPCallInvoker callWebService");
        	httpURL.abort();
                logError(CLS_NAME + 
                        "Error occurred while executing webservice", e);
        	throw new BBBSystemException("Exception in calling web service using http client", e);
        } finally {
            try {
                if(entity != null){
                     EntityUtils.consume(entity);
                }
            } catch (IOException e) {
                    logError( CLS_NAME +
                        " Error occurred while releasing the HTTP connection to the pool", e);
            }finally{
                    logDebug("calling webservice with URL:"+ url);
            }
        }
        
        logDebug( CLS_NAME +" exitting callWebService() method");
        BBBPerformanceMonitor.end("HTTPCallInvoker callWebService");
        return response;
        
    }
    
    /**
     * This method execute query
     * 
     * @param completeQueryURL
     * @return String responseObject
     */
    @Override
	public String executeQuery(final String completeQueryURL) throws BBBBusinessException,
            BBBSystemException {
        
        final long startTime = System.currentTimeMillis();

        if (httpClient == null) {
            initializeHttpClient();
        }
       
        /* Calling the method to Fetch the Response from HTTP Request */
        final String response = callWebService(completeQueryURL);
        
        final long endTime = System.currentTimeMillis();
        
        logDebug(CLS_NAME + " Total Time Taken by execute() Method "
                    + (endTime - startTime)
                    +" response from webservice="+response);

        return response;
    }
	
	/**
	 * Method that intialize the http client parameters.
	 * @param completeQueryURL
	 * @param requiredParams
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	
	private String executePostQuery(final String completeQueryURL, final List<NameValuePair> requiredParams)
			throws BBBBusinessException, BBBSystemException {

		final long startTime = System.currentTimeMillis();

		if (httpClient == null) {
			this.initializeHttpClient();
		}

		final String response = callPostWebService(completeQueryURL, requiredParams);
		final long endTime = System.currentTimeMillis();

		logDebug(CLS_NAME + " Total Time Taken by execute() Method " //$NON-NLS-1$
				+ (endTime - startTime) + " response from webservice=" //$NON-NLS-1$
				+ response);

		return response;
	}
	
	 /**
	 * Method that call Post web service using http client.
	 * @param url
	 * @param params
	 * @return
	 * @throws BBBSystemException
	 */
	private String callPostWebService(final String url, final List<NameValuePair> params) throws BBBSystemException {
	        BBBPerformanceMonitor.start("HTTPCallInvoker callPostWebService"); //$NON-NLS-1$
	        
	        logDebug(CLS_NAME + "Entering callPostWebService() method || Webservice call will be made"); //$NON-NLS-1$
	        
	        HttpEntity entity = null;
	        String response = null;
	        final HttpPost httpURL = new HttpPost();
	        
	        try {
	            httpURL.setURI(new URI(url));
	            httpURL.setHeader(BBBCoreConstants.REFERRER, getReferer());
	            httpURL.setEntity(new UrlEncodedFormEntity(params, "UTF-8")); //$NON-NLS-1$
	            final HttpResponse webResponse = httpClient.execute(httpURL);
	            final int status = webResponse.getStatusLine().getStatusCode();
	            entity = webResponse.getEntity();
	           
				if (status == HttpStatus.SC_OK) {
					if (entity == null) {
						throw new Exception("Response code is:" + status //$NON-NLS-1$
								+ " |Empty entity received"); //$NON-NLS-1$
					}
					response = EntityUtils.toString(entity, BBBCoreConstants.UTF_8);
				} else {
					// consume the error response if any
					EntityUtils.consume(entity);
					throw new Exception(
							"Response code is:" //$NON-NLS-1$
									+ status
									+ " |Invalid response received from the webservice host"); //$NON-NLS-1$
				}
			}
	     //SAP-345 start
	            
        catch(SocketTimeoutException e)
        {
	       		httpURL.abort();
            logError(CLS_NAME + "Error occurred while executing webservice , SocketTimeoutException");
    	throw new BBBSystemException("Exception in calling web service using http client", e);
        }
        
        //SAP-345 end  
	         catch (Exception e) {
	       		httpURL.abort();
	                logError(CLS_NAME + "Error occurred while executing webservice", e); //$NON-NLS-1$
	        	throw new BBBSystemException("Exception in calling web service using http client", e); //$NON-NLS-1$
	        } finally {
	            try {
	                if (entity != null) {
	                     EntityUtils.consume(entity);
	                }
	            } catch (IOException e) {
					logError(
							CLS_NAME
								+ " Error occurred while releasing the HTTP connection to the pool", e); //$NON-NLS-1$
	            } finally {
	                    logDebug("calling webservice with URL:" + url); //$NON-NLS-1$
	            }
	        }
	        
	        logDebug(CLS_NAME + " exitting callPostWebService() method"); //$NON-NLS-1$
	        BBBPerformanceMonitor.end("HTTPCallInvoker callWebService"); //$NON-NLS-1$
	        return response;
	        
	    }

    /**
     * Get Referer from config Keys 
     * 
     * @return referer
     * @throws BBBSystemException
     */
	private String getReferer() throws BBBSystemException {
		final String siteId = SiteContextManager.getCurrentSiteId();
		final StringBuffer refererKey = new StringBuffer();
		refererKey.append(REFERER_URL).append(BBBCoreConstants.UNDERSCORE).append(siteId);
		String referer = null;
		try {
			referer = getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, refererKey.toString()).get(0);
			return referer;
		} catch (BBBBusinessException e) {
			throw new BBBSystemException("Exception in getting Referer from config Keys", e);
		}
	}

    /**
     * @return mCatalogTools
     */
    public BBBCatalogTools getCatalogTools() {
        return this.mCatalogTools;
    }


    /**
     * @param mCatalogTools
     */
    public void setCatalogTools(final BBBCatalogTools mCatalogTools) {
        this.mCatalogTools = mCatalogTools;
    }

    
    /**
     * @return dummyQuery
     */
    public String getDummyQuery() {
        return this.dummyQuery;
    }


    /**
     * @param dummyQuery
     */
    public void setDummyQuery(final String dummyQuery) {
        this.dummyQuery = dummyQuery;
    }
    
    /**
     * Method that call web service using http client
     * 
     * @param url
     * @param jsonString
     * @param header
     * @param isSDDCommitOrderFlow
     * @return
     * @throws BBBSystemException
     * @throws BBBBusinessException 
     */
    public String callPostRestService(final String url, String jsonString, Object header, boolean isSDDCommitOrderFlow) throws BBBSystemException, BBBBusinessException {
        BBBPerformanceMonitor.start("HTTPCallInvoker callPostRestService");
        
        logDebug( CLS_NAME + " Entering callPostRestService() method || Rest call to API will be made for URL :: " + url + " and Input JSON String :: " + jsonString);
        if(header!=null) {
        	logDebug("Header ::  " + header.toString());
        } else {
        	logDebug("Header is null.");
        }
        
        HttpEntity entity = null;
        String response = null;
        final HttpPost httpURL=new HttpPost();
        try {
        	if (httpClient == null) {
    			this.initializeHttpClient();
    		}
            httpURL.setURI(new URI(url));
            if (header instanceof String) {
				httpURL.addHeader("X-WSSE", header.toString());
			} else if (header instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Iterator<Entry<String, String>> paramIterator = ((HashMap<String, String>) header).entrySet().iterator();
				while (paramIterator.hasNext()) {
					Map.Entry<String, String> pair = paramIterator.next();
					httpURL.setHeader(pair.getKey(), pair.getValue()); 
				}
			}
			httpURL.setEntity(new StringEntity(jsonString));
            final HttpResponse webResponse = httpClient.execute(httpURL);
            final int status = webResponse.getStatusLine().getStatusCode();
            entity = webResponse.getEntity();
            /*
             * Fetching the Status Code.If Status Code is not OK, error will be
             * thrown.
             */
			if (status == HttpStatus.SC_OK) {
				if (entity == null) {
					throw new BBBSystemException("Response code is:" + status + " |Empty entity received");
				}
				response = EntityUtils.toString(entity, BBBCoreConstants.UTF_8);
			} else if (status == HttpStatus.SC_NOT_FOUND) {
				EntityUtils.consume(entity);
				BBBPerformanceMonitor.end("HTTPCallInvoker callPostRestService");
				if(isSDDCommitOrderFlow) {
					logInfo(CLS_NAME + " Unable to Place SDD Order because the EOM service is down or unresponsive");
				}
				throw new BBBSystemException("Requested URL Not found");
			} else if (isSDDCommitOrderFlow && null != getServiceUnavailableStatus() && getServiceUnavailableStatus().contains(String.valueOf(status))) {
				EntityUtils.consume(entity);
				BBBPerformanceMonitor.end("HTTPCallInvoker callPostRestService");
				logInfo(CLS_NAME + " Unable to Place SDD Order because the EOM service is down or unresponsive");
				throw new BBBSystemException(" Unable to Place SDD Order because the EOM service is down or unresponsive");
			} else if (status == HttpStatus.SC_UNAUTHORIZED) {
				EntityUtils.consume(entity);
				BBBPerformanceMonitor.end("HTTPCallInvoker callPostRestService");
				logInfo(CLS_NAME + "Unauthorized access, Invalid access token");
				throw new BBBBusinessException("Unable to proceed with Unauthorized access, Invalid access token");
			} else {
				response = EntityUtils.toString(entity, BBBCoreConstants.UTF_8);
				EntityUtils.consume(entity);
				BBBPerformanceMonitor.end("HTTPCallInvoker callPostRestService");
			}
		} catch(SocketTimeoutException e) {
        	BBBPerformanceMonitor.end("HTTPCallInvoker callPostRestService");
        	httpURL.abort();
            logError(CLS_NAME + "Error occurred while making Rest call to callPostRestService API, SocketTimeoutException");
            if(isSDDCommitOrderFlow) {
        		logInfo(CLS_NAME + " Unable to Place SDD Order due to SocketTimeout while calling EOM service.");
        	}
            throw new BBBSystemException(e.getMessage());
        } catch (IOException e) {
        	BBBPerformanceMonitor.end("HTTPCallInvoker callPostRestService");
        	httpURL.abort();
            logError(CLS_NAME + "Error occurred while consuming response in callPostRestService API", e);
        	throw new BBBSystemException(e.getMessage());
		} catch (URISyntaxException e) {
			BBBPerformanceMonitor.end("HTTPCallInvoker callPostRestService");
        	httpURL.abort();
            logError(CLS_NAME + "Error occurred while creating URI from request url in callPostRestService API", e);
        	throw new BBBSystemException(e.getMessage());
		}  finally {
            try {
                if(entity != null){
                     EntityUtils.consume(entity);
                }
            } catch (IOException e) {
                logError( CLS_NAME + " Error occurred while releasing the HTTP connection to the pool", e);
            }
        }
        
        logDebug( CLS_NAME +" exitting callPostRestService() method for URL : " + url);
        BBBPerformanceMonitor.end("HTTPCallInvoker callPostRestService");
        return response;
        
    }
    
    
    /**
     * This method is used to parse JSON response.
     * 
     * @param object
     * @param jsonString
     * @return
     * @throws BBBSystemException
     * @throws IOException
     */
    public <T> T parseJSONResponse(Class<T> object, String jsonString) throws BBBSystemException, IOException {
    	logDebug("Enter :: HTTPCallInvoker.parseJSONResponse(Class<T>, String). JSON "+jsonString);
        Gson gson = new GsonBuilder().create();
        T returnObj = null;
        try{
        	returnObj = gson.fromJson(jsonString,object);
        } catch(JsonSyntaxException e){
        	logError("JsonSyntaxException occurred while parsing JSON Request", e);
        	throw new BBBSystemException(e.getMessage());
        } catch(JsonParseException e){
        	logError("JsonParseException occurred while parsing JSON Request", e);
        	throw new BBBSystemException(e.getMessage());
        } catch(Exception e){
        	logError("Exception occurred while parsing JSON Reques", e);
        	throw new BBBSystemException(e.getMessage());
        }
        logDebug("Exit :: HTTPCallInvoker.parseJSONResponse(Class<T>, String) , Parsed object "+returnObj);
		return returnObj;
    }

	/**
	 * This method used to parse JSONArray into List of Java Object.
	 * 
	 * @param object
	 * @param jsonString
	 * @return
	 * @throws BBBSystemException
	 * @throws IOException
	 */
	public <T> List<T> parseJSONArrayResponse(Class<T> object, String jsonString) throws BBBSystemException, IOException {
		BBBPerformanceMonitor.start("HTTPCallInvoker - parseJSONArrayResponse starts!");
		logDebug("HTTPCallInvoker.parseJSONArrayResponse starts with input JSON:\n" + jsonString);
		ObjectMapper objectMapper = new ObjectMapper();
		List<T> outputList = null;
		try {
			outputList = objectMapper.readValue(jsonString, objectMapper.getTypeFactory().constructCollectionType(List.class, object));
		} catch (JsonSyntaxException e) {
			BBBPerformanceMonitor.end("HTTPCallInvoker - parseJSONArrayResponse ends.");
			logError("JsonSyntaxException occurred while parsing JSON Request", e);
			throw new BBBSystemException(e.getMessage());
		} catch (JsonParseException e) {
			BBBPerformanceMonitor.end("HTTPCallInvoker - parseJSONArrayResponse ends.");
			logError("JsonParseException occurred while parsing JSON Request", e);
			throw new BBBSystemException(e.getMessage());
		} catch (Exception e) {
			BBBPerformanceMonitor.end("HTTPCallInvoker - parseJSONArrayResponse ends.");
			logError("Exception occurred while parsing JSON Request", e);
			throw new BBBSystemException(e.getMessage());
		}
		logDebug("HTTPCallInvoker.parseJSONArrayResponse ends with output list of objects: " + outputList);
		BBBPerformanceMonitor.end("HTTPCallInvoker - parseJSONArrayResponse ends.");
		return outputList;
	}
    
 
    public String parseJSONRequest(Object object) throws BBBSystemException, IOException {
    	logDebug("Enter :: HTTPCallInvoker.parseJSONRequest(Object). Object for parse "+object);
        Gson gson = new GsonBuilder().create();
        String jsonString = BBBCoreConstants.BLANK;
        try{
              jsonString = gson.toJson(object);
        } catch(JsonSyntaxException e){
        	logError("JsonSyntaxException occurred while parsing JSON Request", e);
        	throw new BBBSystemException(e.getMessage());
        } catch(Exception e){
        	logError("Exception occurred while parsing JSON Request", e);
        	throw new BBBSystemException(e.getMessage());
        }
        logDebug("Exit :: HTTPCallInvoker.parseJSONRequest(Object). Parsed JSON "+jsonString);
        return jsonString;
    }
    
    /**
     * Convert vo to json.
     *
     * @param <T> the generic type
     * @param object the object
     * @param sddRequestVO the sdd request vo
     * @param postUrl the post url
     * @param headerParam the header param
     * @return the t
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public <T>T invokeWithJSONInput(Class<T> object, Object requestVO, String postUrl, Map<String, String> headerParam) throws BBBSystemException, BBBBusinessException, IOException
     {   logDebug("HTTPCallInvoker.convertVOToJson: Starts with params : URL :" + postUrl + " , headerParam values : " + headerParam.values());
 	    HttpPost httpPost = new HttpPost(postUrl);
 	    Iterator<Entry<String, String>> paramIterator = headerParam.entrySet().iterator();
 	      while(paramIterator.hasNext())
 	      {
 			 Map.Entry<String, String> pair = paramIterator.next();
 			 httpPost.addHeader(pair.getKey(),pair.getValue());
 			
 		  }
 	   
 	    StringEntity postingString = null;
 		postingString = new StringEntity(parseJSONRequest(requestVO));
 		logDebug("postingString is "+postingString);
 			
 		httpPost.setEntity(postingString);
 		logDebug("HTTPCallInvoker.convertVOToJson: Ends");
 		return invokeToGetJson(object, httpPost, false);
    }


	/**
	 * @return the serviceUnavailableStatus
	 */
	public List<String> getServiceUnavailableStatus() {
		return serviceUnavailableStatus;
	}


	/**
	 * @param serviceUnavailableStatus the serviceUnavailableStatus to set
	 */
	public void setServiceUnavailableStatus(List<String> serviceUnavailableStatus) {
		this.serviceUnavailableStatus = serviceUnavailableStatus;
	}


	public String getRequestURL() {
		return requestURL;
	}


	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}


	public String getResponse() {
		return response;
	}


	public void setResponse(String response) {
		this.response = response;
	}

}
