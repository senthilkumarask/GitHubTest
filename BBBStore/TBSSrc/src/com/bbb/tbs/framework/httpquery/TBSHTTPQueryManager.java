package com.bbb.tbs.framework.httpquery;

import atg.core.util.StringUtils;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.httpquery.HTTPQueryManager;
import com.bbb.framework.httpquery.parser.ResultParserIF;
import com.bbb.framework.httpquery.vo.HTTPServiceRequestIF;
import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;
import com.bbb.utils.BBBUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by acer on 8/27/2014.
 */
public class TBSHTTPQueryManager extends HTTPQueryManager{

    private static final String CLS_NAME = "CLS=[TBSHTTPQueryManager]/MSG::";
    private static final String THIRD_PARTY_URLS = "ThirdPartyURLs";
    private static final String HTTP_CLIENT_OPT_PARAMS = "HTTPClientOptParams";
    private static final String HTTP_CLIENT_REQ_PARAMS = "HTTPClientReqParams";
    private static final String WS_RESPONSE_PROCESSOR ="WSResponseProcessor";
    private static final String EMPTY_STRING = "";
    private static final String SEMICOL_DELIMITER = ";";

    public HTTPCallInvoker getHttpCallInvoker() {
        return mHttpCallInvoker;
    }

    public void setHttpCallInvoker(final HTTPCallInvoker httpCallInvoker) {
        this.mHttpCallInvoker = httpCallInvoker;
    }

    private HTTPCallInvoker mHttpCallInvoker;

    public HTTPServiceResponseIF invoke(final HTTPServiceRequestIF pRequestVO)
            throws BBBBusinessException, BBBSystemException {
        logDebug( CLS_NAME +"entering invoke()");
        HTTPServiceResponseIF responseVO = null;
        final Map<String,String> paramsValuesMap = pRequestVO.getParamsValuesMap();
        String isPostXMLRequest = paramsValuesMap.get("isPostXMLRequest");
        String paramsPartOfURL = paramsValuesMap.get("paramsPartOfURL");
        String serviceName = pRequestVO.getServiceName();
        String serviceType = pRequestVO.getServiceType();
        String siteId = pRequestVO.getSiteId();
        
        if(!StringUtils.isBlank(isPostXMLRequest) && isPostXMLRequest.equalsIgnoreCase("true")){
           
            String xmlRequest = paramsValuesMap.get(serviceName);
            String remoteResponse = invokeTargetPostRequest(serviceName, xmlRequest, siteId);

            if (remoteResponse != null && !remoteResponse.trim().equals(EMPTY_STRING) && remoteResponse.contains("configurationModel")) {
                responseVO = processResult(remoteResponse, serviceType);
            }
            logDebug(CLS_NAME + "exiting invoke()");
            
            
        } else if(!StringUtils.isBlank(paramsPartOfURL) && paramsPartOfURL.equalsIgnoreCase("true")) {
        	String remoteResponse = invokeTargetWithParamsPartOfURL(paramsValuesMap, serviceName, serviceType);
        	
        	if( remoteResponse != null && !remoteResponse.trim().equals(EMPTY_STRING) && remoteResponse.contains("configurationModel")){
    			responseVO = processResult(remoteResponse, serviceType);
    		}
        	
        } else {
            responseVO = super.invoke(pRequestVO);
        }
        return responseVO;
    }
    
    
    /**
	 * This method finds out set of parameter names, web service host URL (host URL) which
	 * are mandatorly required for web service query invocation
	 * @param paramValues
	 * @param serviceName
	 * @return
	 */
	protected String invokeTargetWithParamsPartOfURL(final Map<String,String> paramsValues, final String serviceNames, 
			final String serviceType) 
			throws BBBBusinessException, BBBSystemException{
		
		String hostTargetURL = null;
		
		
		logDebug( CLS_NAME +" thirdPartyURLs 2= " 
					+ getCatalogTools().getAllValuesForKey(THIRD_PARTY_URLS, serviceType).get(0));
		
		
		//get hostTargetUrl from the config repository
		hostTargetURL = getCatalogTools().getConfigValueByconfigType(THIRD_PARTY_URLS).get(serviceType);
		//TODO: get the required param names from the DB
		String userId = paramsValues.get("userId");
		

		StringBuffer lStringBuffer = new StringBuffer().append(hostTargetURL).append("/").append(userId);
		
        logDebug( CLS_NAME +" serviceName="+serviceNames
            		+ " endPointName="+serviceType
            		+ " hostTargetURL="+hostTargetURL);
        		
		if(hostTargetURL == null){
			if (isLoggingError()) {				
				logError(CLS_NAME +" BBBBusinessException HOST URL NULL");			
			}
			throw new BBBBusinessException("ERR_HTTP_QUERY_HOSTURL_NULL","ERR_HTTP_QUERY_HOSTURL_NULL");
		}
		
		String remoteResponse = null;
		try {
			remoteResponse = getHttpCallInvoker().invoke(lStringBuffer.toString(),
					new ArrayList<String>(), new ArrayList<String>(), paramsValues);
		} catch (BBBSystemException e) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_CERTONA_1001, "Exception in calling certona webservice");
		}
		
		return remoteResponse;
	}
    
    
    
    

    protected String invokeTargetPostRequest(String pServiceName, String pXmlRequest, String pSiteId) throws BBBBusinessException, BBBSystemException {

        String hostTargetURL = null;

        String configType = HTTP_CLIENT_REQ_PARAMS +"_"+ pSiteId;

        configType = HTTP_CLIENT_OPT_PARAMS + "_"+pSiteId;

        logDebug( CLS_NAME +" thirdPartyURLs 2= "
                + getCatalogTools().getAllValuesForKey(THIRD_PARTY_URLS, pServiceName).get(0));

        //get hostTargetUrl from the config repository
        hostTargetURL = getCatalogTools().getConfigValueByconfigType(THIRD_PARTY_URLS).get(pServiceName);

        logDebug( CLS_NAME +" serviceName="+pServiceName
                + " endPointName="+pServiceName
                + " hostTargetURL="+hostTargetURL);

        if(hostTargetURL == null){
            if (isLoggingError()) {
                logError(CLS_NAME +" BBBBusinessException HOST URL NULL");
            }
            throw new BBBBusinessException("ERR_HTTP_QUERY_HOSTURL_NULL","ERR_HTTP_QUERY_HOSTURL_NULL");
        }
        String remoteResponse = null;
        try {
            remoteResponse = ((TBSHTTPCallInvoker) getHttpCallInvoker()).invokePostRequest(hostTargetURL,pXmlRequest);
        } catch (BBBSystemException e) {
            BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_CERTONA_1001, "Exception in calling webservice");
            //TODO: update the error
        }
        return remoteResponse;
    }


    /**
     * This method invokes the parser for processing on remoteResponse object returned from
     * web service
     *
     * @param remoteResponse
     * @param serviceType
     * @return
     * @throws BBBSystemException
     */
    protected HTTPServiceResponseIF processResult(final String remoteResponse,
                                                final String serviceType) throws BBBBusinessException, BBBSystemException{

        ResultParserIF resultParser = null;

        final String parserClass = getCatalogTools().getConfigValueByconfigType(WS_RESPONSE_PROCESSOR).get(serviceType);

        if( parserClass == null || parserClass.trim().equals(EMPTY_STRING)){
            throw new BBBBusinessException("ERR_HTTP_QUERY_PARSER_NULL","ERR_HTTP_QUERY_PARSER_NULL");
        }

        try {

            resultParser = (ResultParserIF) Class.forName(parserClass).newInstance();


            logDebug( CLS_NAME +" resultParser="+resultParser
                    + " parserClass=" +parserClass );


        } catch (InstantiationException inExcep) {
            logError(CLS_NAME +" InstantiationException = "+ inExcep);
            throw new BBBBusinessException("ERR_HTTP_QUERY_BIZ_EXCEPTION","ERR_HTTP_QUERY_BIZ_EXCEPTION",inExcep);

        } catch (IllegalAccessException illegalEx) {

            logError(CLS_NAME+" IllegalAccessException = "+ illegalEx);

            throw new BBBBusinessException("ERR_HTTP_QUERY_BIZ_EXCEPTION","ERR_HTTP_QUERY_BIZ_EXCEPTION",illegalEx);

        } catch (ClassNotFoundException cnfEx) {

            logError(CLS_NAME+" ClassNotFoundException = "+ cnfEx);

            throw new BBBBusinessException("ERR_HTTP_QUERY_BIZ_EXCEPTION","ERR_HTTP_QUERY_BIZ_EXCEPTION",cnfEx);
        }

        final HTTPServiceResponseIF responseVO = resultParser.parse(remoteResponse);

        if(responseVO !=null ){
            logDebug( CLS_NAME +" after parsing responseVO="+responseVO
                    + " parserClass=" + resultParser );
        }else{
            logDebug( CLS_NAME +" after parsing responseVO is null" );
        }

        return responseVO;

    }
}
