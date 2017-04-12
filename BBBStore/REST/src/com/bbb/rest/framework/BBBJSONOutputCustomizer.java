package com.bbb.rest.framework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.dozer.MappingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.repository.tojava.runtime.RepositoryItemWrapper;
import atg.rest.RestException;
import atg.rest.output.JSONOutputCustomizer;
import atg.rest.processor.BeanProcessor.FormHandlerExceptions;
import atg.rest.processor.BeanProcessor.FormHandlerPropertiesAndExceptions;
import atg.rest.util.BeanURI;
import atg.rest.util.ParameterContext;
import atg.rest.util.ParsedURI;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.rest.output.BBBRestDozerBeanProvider;
import com.bbb.utils.BBBUtility;


public class BBBJSONOutputCustomizer extends JSONOutputCustomizer{
	private BBBRestDozerBeanProvider dozerBean;
	private Map<String, String> serviceClassMap = new HashMap<String, String>();
	private List<String> excludeObjectKeys 		= null;
	private List<String> excludeObjectValues 	= null;	
    private String errorCodeSeparator;
	public String getErrorCodeSeparator() {
		return errorCodeSeparator;
	}
	public void setErrorCodeSeparator(String errorCodeSeparator) {
		this.errorCodeSeparator = errorCodeSeparator;
	}
	public Map<String, String> getServiceClassMap() {
		return serviceClassMap;
	}
	public void setServiceClassMap(Map<String, String> serviceClassMap) {
		this.serviceClassMap = serviceClassMap;
	}
	public BBBRestDozerBeanProvider getDozerBean() {
		return dozerBean;
	}
	public void setDozerBean(BBBRestDozerBeanProvider dozerBean) {
		this.dozerBean = dozerBean;
	}
	
	public List<String> getExcludeObjectKeys() {
		return excludeObjectKeys;
	}
	public void setExcludeObjectKeys(List<String> excludeObjectKeys) {
		this.excludeObjectKeys = excludeObjectKeys;
	}
	public List<String> getExcludeObjectValues() {
		return excludeObjectValues;
	}
	public void setExcludeObjectValues(List<String> excludeObjectValues) {
		this.excludeObjectValues = excludeObjectValues;
	}
/**
 * Overriden OOB method to filter the response
 */
	public void outputBean(ParsedURI pParsedURI, Object pValue, int pNestingDepth, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws RestException, IOException{   
		String serviceClassName = getServiceClassMap().get(pParsedURI.getURI());     
		
		/*This code sets the result value to false in case there are exceptions in the formHandler response*/
		if(pValue instanceof FormHandlerPropertiesAndExceptions){
			FormHandlerPropertiesAndExceptions formHandler = (FormHandlerPropertiesAndExceptions)pValue;
			if(formHandler.getExceptions()!=null && formHandler.getExceptions().size()>0){
				formHandler.setResult(false);
			}else if(!formHandler.isResult() && (formHandler.getExceptions()==null || formHandler.getExceptions().size()==0)){
				formHandler.setResult(true);
			}
		}else if(pValue instanceof FormHandlerExceptions){
			FormHandlerExceptions formHandler = (FormHandlerExceptions)pValue;
			if(formHandler.getExceptions()!=null && formHandler.getExceptions().size()>0){
				formHandler.setResult(false);
			}else if(!formHandler.isResult() && (formHandler.getExceptions()==null || formHandler.getExceptions().size()==0)){
				formHandler.setResult(true);
			}
		}
		/*Code End*/
		
		if(serviceClassName != null ){
			try {
				Object destProductVO = getDozerBean().map(serviceClassName, pValue);
				super.outputBean(pParsedURI, destProductVO, pNestingDepth, pRequest, pResponse);
			} catch (MappingException e) {
				//In case of exception calling OOB method 
				super.outputBean(pParsedURI, pValue, pNestingDepth, pRequest, pResponse);
			} catch (ClassNotFoundException e) {
				//In case of exception calling OOB method
				super.outputBean(pParsedURI, pValue, pNestingDepth, pRequest, pResponse);
			} catch (InstantiationException e) {
				//In case of exception calling OOB method
				super.outputBean(pParsedURI, pValue, pNestingDepth, pRequest, pResponse);
			} catch (IllegalAccessException e) {
				//In case of exception calling OOB method
				super.outputBean(pParsedURI, pValue, pNestingDepth, pRequest, pResponse);
			}
		}else{
			super.outputBean(pParsedURI, pValue, pNestingDepth, pRequest, pResponse);
		}
	}
	
	/* Overrided OOB method as OOB method is not serializing the map object that contains list/array/set/map as value for key. We
	 * have raised ATG ticket to get this fixed and will remove the code once ticket is resolved.
	 * 
	 * (non-Javadoc)
	 * @see atg.rest.output.JSONOutputCustomizer#addMapToJSONObject(java.lang.String, java.util.Map, java.lang.Object, java.lang.Object, int, atg.rest.util.ParsedURI, atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
    protected Map addMapToJSONObject(String pResourceName, Map pValue, Object pJSON, Object pContainer, int pNestingDepth, ParsedURI pParsedURI, DynamoHttpServletRequest pRequest, 
            DynamoHttpServletResponse pResponse)
        throws RestException, IOException, JSONException
    {
        Object value = null;
        Map map = new HashMap(pValue.size());
        int maxNestingDepth = ParameterContext.getParameterContext().getMaxNestingDepth();
        Iterator iter;
        if(pJSON instanceof JSONObject)
        {
        	//JSONObject jobj = new JSONObject(map);
            if(StringUtils.isBlank(pResourceName))
                ((JSONObject)pJSON).put(getRootElementString(), map);
            else
                ((JSONObject)pJSON).put(pResourceName, map);
        } 
        else {
        	
        	if(pJSON instanceof JSONArray)
            ((JSONArray)pJSON).put(map);
        }
        for(Iterator i$ = pValue.keySet().iterator(); i$.hasNext();)
        {
            Object key = i$.next();
            value = pValue.get(key);
            
            if(value==null){
           	 map.put(key, JSONObject.NULL);
            } else 
            if((value instanceof RepositoryItem) && pNestingDepth >= maxNestingDepth)
            {
                Object objValue = null;
                if(enableShowRESTPaths())
                    objValue = getRepositoryItemReferenceString((RepositoryItem)value, pRequest, pResponse);
                else
                    objValue = getRepositoryItemReferenceObject((RepositoryItem)value, pRequest, pResponse);
                map.put(key, objValue);
            } else
            if((value instanceof RepositoryItemWrapper) && pNestingDepth >= maxNestingDepth)
            {
                Object objValue = null;
                if(enableShowRESTPaths())
                    objValue = getRepositoryItemReferenceString(((RepositoryItemWrapper)value)._getRepositoryItem(), pRequest, pResponse);
                else
                    objValue = getRepositoryItemReferenceObject(((RepositoryItemWrapper)value)._getRepositoryItem(), pRequest, pResponse);
                map.put(key, objValue);
            } else
            if((value instanceof GenericService) && pNestingDepth >= maxNestingDepth)
            {
                Object objValue = null;
                if(enableShowRESTPaths())
                    objValue = getNucleusComponentReferenceString((GenericService)value, pRequest, pResponse);
                else
                    objValue = getNucleusComponentReferenceObject((GenericService)value, pRequest, pResponse);
                map.put(key, objValue);
            } else
            if((value instanceof String) || (value instanceof Number) || (value instanceof Boolean) || (value instanceof Class) || (value instanceof Byte) || (value instanceof Character) || (value instanceof Enum) || pNestingDepth >= maxNestingDepth)
            {
                if(value == null)
                    map.put(key, JSONObject.NULL);
                else
                    map.put(key, value);
            } else
            { 
            	/**
            	 * TEMP CODE TO HANDLE OOB ISSUE -- START
            	 */
            	JSONObject child=new JSONObject();
				if(value instanceof List){
					JSONArray resultArray = addListToJSONObject(pResourceName, (List)value, child, pContainer, pNestingDepth, pParsedURI, pRequest, pResponse);
					map.put(key, resultArray);
				}
				else if(value instanceof Map){									 
					Map resultArray = addMapToJSONObject(pResourceName, (Map)value, child, pContainer, pNestingDepth, pParsedURI, pRequest, pResponse);
					map.put(key, resultArray);					 
				}
				else if(pValue instanceof Set){
					JSONArray resultArray = addSetToJSONObject(pResourceName, (Set)value, child, pContainer, pNestingDepth, pParsedURI, pRequest, pResponse);
					map.put(key, resultArray);
				}
				else if(pValue.getClass().isArray()){
					JSONArray resultArray = addArrayToJSONObject(pResourceName, value, child, pContainer, pNestingDepth, pParsedURI, pRequest, pResponse);
					map.put(key, resultArray);
				}
				else if(pValue instanceof Object){
					addObjectToJSONObject(pResourceName, value, child, pContainer, pNestingDepth, pParsedURI, pRequest, pResponse);
					
				}
				String tempKey;
				//if(pResourceName == null){
					//pResourceName = getRootElementString();
				//}
				if(!BBBUtility.isEmpty(pResourceName)&&!child.isNull(pResourceName)){
					map.put(key, child.get(pResourceName));
				}else{
					map.put(key, child);
				}
				
            }
        	/**
        	 * TEMP CODE TO HANDLE OOB ISSUE --END
        	 */

        }
        return map;
    }
         
    
    /**
     * Extend OOB method to send error code in case specific exceptions are thrown.
     */
    protected void addObjectToJSONObject(String pResourceName, Object pValue, Object pJSON, Object pContainer, int pNestingDepth, ParsedURI pParsedURI, DynamoHttpServletRequest pRequest, 
            DynamoHttpServletResponse pResponse) throws RestException, IOException{
    	
    	if(getExcludeObjectKeys().contains(pResourceName)
    			|| getExcludeObjectValues().contains(pValue)) {
    		return;
    	}
    	if(pValue instanceof DropletException)    		
	    {
    		try{
	    		if(pJSON instanceof JSONObject)
	                ((JSONObject)pJSON).put(pResourceName,((DropletException)pValue).getErrorCode()
	                		+getErrorCodeSeparator()
	                		+((DropletException)pValue).getMessage());
	            else
	            if(pJSON instanceof JSONArray)
	                ((JSONArray)pJSON).put(((DropletException)pValue).getErrorCode()
	                		+getErrorCodeSeparator()
	                		+((Throwable)pValue).getMessage());
		        
	    	}catch(JSONException jsone){
	        		throw new RestException(jsone, pResponse, 500);
	    	}
	    }
	    
    	else super.addObjectToJSONObject(pResourceName, pValue, pJSON, pContainer, pNestingDepth, pParsedURI, pRequest, pResponse);
	
    }
    
    
    
    
    
    @Override
	protected boolean loadNucleusComponentIntoMap(String pResourceName,
			Object pValue, Object pContainer, BeanURI pURI) {

		if ((pResourceName == null) || (pValue == null) || (pContainer == null)
				|| (pURI == null)) {
			return false;
		}

		if (pURI.getContainerMap() == null) {
			pURI.setContainerMap(new HashMap());
		}
		
		int valueHash = pValue.hashCode();
		if(pURI.getContainerMap().isEmpty()){
			return false;
		}
		Properties props = (Properties) pURI.getContainerMap().get(
				Integer.valueOf(valueHash));

		if (props != null) {
			return true;
		}
		int containerHash = pContainer.hashCode();
		props = (Properties) pURI.getContainerMap().get(
				Integer.valueOf(containerHash));

		if (props == null) {
			return false;
		}
		String val = props.getProperty(pResourceName);

		if (val == null) {
			return false;
		}
		boolean success = false;

		if (val.startsWith("/")) {
			success = loadNucleusComponentIntoMap(val, pValue, pURI);
		}
		return success;
	
	}
	 
	/* Overridden method to apply dozer mapping
     * @see atg.rest.output.RestOutputCustomizerImpl#outputBeanValue(atg.rest.util.ParsedURI, java.lang.String, java.lang.Object, int, atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    public void outputBeanValue(ParsedURI pParsedURI, String pResourceName, Object pValue, int pNestingDepth, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws RestException, IOException
            {
            	
			boolean flag = true;
			if(pValue instanceof List){
				
				if(pValue != null){
					
					List input = ((List)pValue);
					List<Object> output = new ArrayList<Object>();
					
					String serviceClassName = getServiceClassMap().get(pParsedURI.getURI());
					
					if(serviceClassName != null){
						for(int i = 0; i < input.size(); i++){
    				    	if(input.get(i) != null){
								try {
									
									Object destProductVO = getDozerBean().map(serviceClassName, input.get(i));
									output.add(destProductVO);
								
								} catch (MappingException e) {
									logError("MappingException Exception occured while applying Dozer mapping", e);
									super.outputBeanValue(pParsedURI, pResourceName, pValue, pNestingDepth, pRequest, pResponse);
								} catch (ClassNotFoundException e) {
									logError("ClassNotFoundException Exception occured while applying Dozer mapping", e);
									super.outputBeanValue(pParsedURI, pResourceName, pValue, pNestingDepth, pRequest, pResponse);
								} catch (InstantiationException e) {
									logError("InstantiationException Exception occured while applying Dozer mapping", e);
									super.outputBeanValue(pParsedURI, pResourceName, pValue, pNestingDepth, pRequest, pResponse);
								} catch (IllegalAccessException e) {
									logError("IllegalAccessException Exception occured while applying Dozer mapping", e);
									super.outputBeanValue(pParsedURI, pResourceName, pValue, pNestingDepth, pRequest, pResponse);
								}
    							
    				    	}	
    				    	
    	    			}
						flag = false;
						super.outputBeanValue(pParsedURI, pResourceName, output, pNestingDepth, pRequest, pResponse);
						
					}
					
				}
				
			}
		
			if(flag){
				super.outputBeanValue(pParsedURI, pResourceName, pValue, pNestingDepth, pRequest, pResponse);
			}
    		
        }
    
    /* overridden method to avoid exception while doing indentation of JSON Output 
     * @see atg.rest.output.JSONOutputCustomizer#sendToOutputStream(java.lang.Object, atg.servlet.DynamoHttpServletResponse)
     */
    public void sendToOutputStream(Object pJSON, DynamoHttpServletResponse pResponse)
            throws RestException, IOException
        {
    		try{
	    		if(pJSON instanceof JSONObject){
	    			JSONObject json = (JSONObject)pJSON;
	    			/***
	    			 * BBBH-4283 - Mobile GZIP compression
	    			 */
	    			if (BBBUtility.isGzipCompressionEnabled()) {
	    				if (this.isLoggingDebug()) {
							logDebug("BBBJSONOutputCustomizer.sendToOutputStream - GZIP Compression enabled.");
						}
						DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		    			final String originOfTraffic = request.getHeader(BBBCoreConstants.ORIGIN_OF_TRAFFIC);
		    			final String acceptEncoding = request.getHeader("accept-encoding");
		    		    if ((acceptEncoding != null && acceptEncoding.indexOf("gzip") != -1) || (originOfTraffic != null && originOfTraffic.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB))) {
		    		    	if (this.isLoggingDebug()) {
								logDebug("BBBJSONOutputCustomizer.sendToOutputStream - Accept-Encoding - gzip OR MobileWeb request invocation.");
							}
							String userInput = getStringControlParam("atg-rest-user-input");
		    				if (userInput != null) {
		    					JSONObject envelope = new JSONObject();
		    					envelope.put("atg-rest-user-input", userInput);
		    					envelope.put("atg-rest-response", pJSON);
		    					json = envelope;
		    				}
		
			    			ByteArrayOutputStream compressed = new ByteArrayOutputStream();
		    				GZIPOutputStream gzout = new GZIPOutputStream(compressed);
		    				gzout.write(json.toString().getBytes("UTF-8"));
		    				gzout.close();
		
		    				byte[] compressedBytes = compressed.toByteArray();
		    				pResponse.setHeader("Content-Encoding", "gzip");
		    				pResponse.setContentLength(compressedBytes.length);
		    				pResponse.setCharacterEncoding(getEncoding());
		    				pResponse.getOutputStream().write(compressedBytes);
						} else {
							super.sendToOutputStream(new JSONObject(json.toString()), pResponse);
						}
	    			} else {
	    				super.sendToOutputStream(new JSONObject(json.toString()), pResponse);
	    			}
	    		}
    		} catch(JSONException jsone){
        		throw new RestException(jsone, pResponse, 500);
    		}
    	}

}

