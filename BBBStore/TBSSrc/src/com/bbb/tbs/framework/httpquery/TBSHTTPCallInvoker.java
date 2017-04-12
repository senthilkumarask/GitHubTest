package com.bbb.tbs.framework.httpquery;

import atg.multisite.SiteContextManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by Keshav on 8/25/2014.
 */
public class TBSHTTPCallInvoker extends HTTPCallInvoker {

    private static final String CLS_NAME = "CLS=[TBSHTTPCallInvoker]/MSG::";

    private static volatile CloseableHttpClient httpClient;

    private static final String REFERER_URL = "referer_url";
    private static final String HTTP_CONNECTION_ATTRIBS = "HTTPConnectionAttributes";
    private static final String STALE_CHECK_ENABLED = "STALE_CHECK_ENABLED";
    private static final String CONNECTION_TIME_OUT = "CONNECTION_TIME_OUT";
    private static final String SOCKET_TIME_OUT = "SOCKET_TIME_OUT";
    private static final String MAX_TOTAL_CONNECTIONS = "MAX_TOTAL_CONNECTIONS";
    private static final String MAX_TOTAL_CONN_PER_HOST = "MAX_TOTAL_CONN_PER_HOST";

    public final String invokePostRequest(final String pURL, final String pRequestString)
            throws BBBBusinessException, BBBSystemException {

        final String methodName = "CLS=HTTPInvoker::MTHD=invoke"; //$NON-NLS-1$

        BBBPerformanceMonitor.start("HTTPInvoker", methodName); //$NON-NLS-1$
        final String responseObject = this.executePostQuery(pURL, pRequestString);
        BBBPerformanceMonitor.end("HTTPInvoker", methodName); //$NON-NLS-1$

        logDebug(CLS_NAME + "responseObject=" + responseObject); //$NON-NLS-1$
        return responseObject;
    }



    /**
     * Method that intialize the http client parameters.
     * @param pURL
     * @param pRequest
     * @return
     * @throws com.bbb.exception.BBBBusinessException
     * @throws com.bbb.exception.BBBSystemException
     */

    protected String executePostQuery(final String pURL, final String pRequest)
            throws BBBBusinessException, BBBSystemException {

        final long startTime = System.currentTimeMillis();

        if (httpClient == null) {
            this.initializeHttpClient();
        }

        final String response = callPostWebService(pURL, pRequest);
        final long endTime = System.currentTimeMillis();

        logDebug(CLS_NAME + " Total Time Taken by execute() Method " //$NON-NLS-1$
                + (endTime - startTime) + " response from webservice=" //$NON-NLS-1$
                + response);

        return response;
    }


    protected String callPostWebService(final String pURL, final String pRequest) throws BBBSystemException {
        BBBPerformanceMonitor.start("HTTPCallInvoker callPostWebService"); //$NON-NLS-1$

        logDebug(CLS_NAME + "Entering callPostWebService() method || Webservice call will be made"
        		+ "with parameters: pURL " + pURL + " pRequest " + pRequest); //$NON-NLS-1$

        HttpEntity entity = null;
        String response = null;
        final HttpPost httpURL = new HttpPost();

        try {
            httpURL.setURI(new URI(pURL));
            httpURL.setHeader(BBBCoreConstants.REFERRER, getReferrer());
            httpURL.setEntity(new StringEntity(pRequest, "UTF-8")); //$NON-NLS-1$
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

        } catch (Exception e) {
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
                logDebug("calling webservice with URL:" + pURL); //$NON-NLS-1$
            }
        }

        logDebug(CLS_NAME + " exitting callPostWebService() method with response: " + response); //$NON-NLS-1$
        BBBPerformanceMonitor.end("HTTPCallInvoker callWebService"); //$NON-NLS-1$
        return response;

    }

    protected String getReferrer() throws BBBSystemException {
        final String siteId = SiteContextManager.getCurrentSiteId();
        final StringBuffer referrerKey = new StringBuffer();
        referrerKey.append(REFERER_URL).append(BBBCoreConstants.UNDERSCORE).append(siteId);
        String referrer = null;
        try {
            referrer = getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, referrerKey.toString()).get(0);
            return referrer;
        } catch (BBBBusinessException e) {
            throw new BBBSystemException("Exception in getting Referer from config Keys", e);
        }
    }


    protected void initializeHttpClient()
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

        if (null == getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, CONNECTION_TIME_OUT).get(0)) {
            connectionTimeout = getDefaultConnectionTimeout();
        } else {
            connectionTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, CONNECTION_TIME_OUT).get(0));
        }

        if (null == getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, SOCKET_TIME_OUT).get(0)) {
            socketTimeout = getDefaultSocketTimeout();
        } else {
            socketTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, SOCKET_TIME_OUT).get(0));
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

}
