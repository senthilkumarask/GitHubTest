package com.bbb.framework.httpquery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.bbb.certona.vo.CertonaResponseVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.parser.ResultParserIF;
import com.bbb.framework.httpquery.vo.HTTPServiceRequestIF;
import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;
import com.bbb.utils.BBBUtility;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;

public class HTTPQueryManager extends BBBGenericService {

	BBBCatalogTools mCatalogTools;
	HTTPCallInvoker mHttpCallInvoker;
	
	private static final String CLS_NAME = "CLS=[HTTPQueryManager]/MSG::";
	private static final String THIRD_PARTY_URLS = "ThirdPartyURLs";
	private static final String HTTP_CLIENT_OPT_PARAMS = "HTTPClientOptParams";
	private static final String HTTP_CLIENT_REQ_PARAMS = "HTTPClientReqParams";
	private static final String WS_RESPONSE_PROCESSOR ="WSResponseProcessor";
	private static final String EMPTY_STRING = "";
	private static final String SEMICOL_DELIMITER = ";";
	
	/**
	 * This is public method called by Droplet for executing http query on web service and
	 * parsing/formatting the remote response in required form.
	 * @param pRequestVO
	 */
	public HTTPServiceResponseIF invoke(final HTTPServiceRequestIF pRequestVO) 
			throws BBBBusinessException, BBBSystemException{
	
        
        logDebug( CLS_NAME +"entering invoke()");
        
        
        
		final Map<String,String> paramsValuesMap = pRequestVO.getParamsValuesMap();
		final String serviceName = pRequestVO.getServiceName();
		final String serviceType = pRequestVO.getServiceType();
		final String siteId = pRequestVO.getSiteId();
		
		final String remoteResponse = invokeTarget(paramsValuesMap, serviceName, serviceType, siteId);
		
		HTTPServiceResponseIF responseVO = null;
		
		if( remoteResponse != null && !remoteResponse.trim().equals(EMPTY_STRING)){
			responseVO = processResult(remoteResponse, serviceType);
		}
		
        
        logDebug( CLS_NAME +"exitting invoke()");
        
		
		return responseVO;
	}
	
	/**
	 * This method finds out set of parameter names, web service host URL (host URL) which
	 * are mandatorly required for web service query invocation
	 * @param paramValues
	 * @param serviceName
	 * @return
	 */
	private String invokeTarget(final Map<String,String> paramsValues, final String serviceNames, 
			final String serviceType, final String pSiteId) 
			throws BBBBusinessException, BBBSystemException{
		
		String hostTargetURL = null;
		
		String configType = HTTP_CLIENT_REQ_PARAMS +"_"+ pSiteId;
		
		List<String> requiredParamsList = null; 
		List<String> optionalParamsList = null; 
		
		
		
		//requiredParamsList = (List<String>) getCatalogTools().getAllValuesForKey(configType, key);
		
		requiredParamsList = findUniqueParamsList(serviceNames, configType);
		
		configType = HTTP_CLIENT_OPT_PARAMS + "_"+pSiteId;
		
		//optionalParamsList = (List<String>) getCatalogTools().getAllValuesForKey(configType, key);
		optionalParamsList = findUniqueParamsList(serviceNames, configType);
				
		
		logDebug( CLS_NAME +" thirdPartyURLs 2= " 
					+ getCatalogTools().getAllValuesForKey(THIRD_PARTY_URLS, serviceType).get(0));
		
		
		//get hostTargetUrl from the config repository
		hostTargetURL = getCatalogTools().getConfigValueByconfigType(THIRD_PARTY_URLS).get(serviceType);

				
        logDebug( CLS_NAME +" serviceName="+serviceNames
            		+ " requiredParams=" +requiredParamsList
            		+ " optionalParams="+optionalParamsList
            		+ " endPointName="+serviceType
            		+ " hostTargetURL="+hostTargetURL);
        		
		if(hostTargetURL == null){
						
			logError(CLS_NAME +" BBBBusinessException HOST URL NULL");			
			
			throw new BBBBusinessException("ERR_HTTP_QUERY_HOSTURL_NULL","ERR_HTTP_QUERY_HOSTURL_NULL");
		}
		
		String remoteResponse = null;
		try {
			remoteResponse = getHttpCallInvoker().invoke(hostTargetURL,
					requiredParamsList, optionalParamsList, paramsValues);
		} catch (BBBSystemException e) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_CERTONA_1001, e.getMessage());
		}
		
		return remoteResponse;
	}
	
	
	/**
	 * Create uniq params list 
	 * 
	 * @param serviceNames
	 * @param configType
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private List<String> findUniqueParamsList(String serviceNames, String configType) 
			throws BBBBusinessException, BBBSystemException {
		
		List<String> paramsList = new ArrayList<String>();
		
		//serviceNames may be ; delimited names of services.e.g. in certona we have serviceNames=pdp_fbw;pdp_cav
		if(serviceNames != null && serviceNames.indexOf(SEMICOL_DELIMITER) > 0){
			
			StringTokenizer sTokenizer = new StringTokenizer(serviceNames,SEMICOL_DELIMITER);
			
			while(sTokenizer.hasMoreTokens()){
				final String key  = sTokenizer.nextToken();
				if(BBBUtility.isNotEmpty(key)){//if(key !=null && key.trim().length()>0){
					
					final List<String> reqList = (List<String>) getCatalogTools().getAllValuesForKey(configType, key);
				
					paramsList.addAll(reqList);
				}
			}
		}
		
	   
        logDebug( CLS_NAME +" findUniqueParamsList before duplidate Removal for ="+serviceNames
            		+ " configType=" +configType +" paramsList="+paramsList );
        
	   
		removeDuplicate(paramsList);
		
	   
       logDebug( CLS_NAME +" findUniqueParamsList nafter duplicate removal for ="+serviceNames
            		+ " configType=" +configType +" paramsList="+paramsList );
       	   
		return paramsList;
	}
	

	/**
	 * Remove Duplicates from List : Order does not maintain
	 * 
	 * @param arlList
	 */
	public static void removeDuplicate(List<String> arlList)
	{
		HashSet<String> h = new HashSet<String>(arlList);
		arlList.clear();
		arlList.addAll(h);
		
	}

	
	
	/**
	 * This method invokes the parser for processing on remoteResponse object returned from
	 * web service
	 * 
	 * @param remoteResponse
	 * @param serviceName
	 * @return
	 * @throws BBBSystemException 
	 */
	private HTTPServiceResponseIF processResult(final String remoteResponse, 
			final String serviceType) throws BBBBusinessException, BBBSystemException{

		ResultParserIF resultParser = null;
		
		final String parserClass = getCatalogTools().getConfigValueByconfigType(WS_RESPONSE_PROCESSOR).get(serviceType);
		
		if( parserClass == null || parserClass.trim().equals(EMPTY_STRING)){
			throw new BBBBusinessException("ERR_HTTP_QUERY_PARSER_NULL","ERR_HTTP_QUERY_PARSER_NULL");
		}
			
		try {
			
			resultParser = getResultParserIF(parserClass);
		

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

	protected ResultParserIF getResultParserIF(final String parserClass)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (ResultParserIF) Class.forName(parserClass).newInstance();
	}
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	public HTTPCallInvoker getHttpCallInvoker() {
		
		if(mHttpCallInvoker == null){
			mHttpCallInvoker = new HTTPCallInvoker();
		}
		return mHttpCallInvoker;
	}

	public void setHttpCallInvoker(final HTTPCallInvoker httpCallInvoker) {
		this.mHttpCallInvoker = httpCallInvoker;
	}
}
