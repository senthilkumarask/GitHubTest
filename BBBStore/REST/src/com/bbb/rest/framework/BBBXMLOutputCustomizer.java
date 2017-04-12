package com.bbb.rest.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dozer.MappingException;

import atg.droplet.DropletException;
import atg.rest.RestException;
import atg.rest.output.XMLOutputCustomizer;
import atg.rest.processor.BeanProcessor.FormHandlerExceptions;
import atg.rest.processor.BeanProcessor.FormHandlerPropertiesAndExceptions;
import atg.rest.util.ParsedURI;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.rest.output.BBBRestDozerBeanProvider;



public class BBBXMLOutputCustomizer extends XMLOutputCustomizer {
    private String errorCodeSeparator;
    
    private BBBRestDozerBeanProvider dozerBean;
	private Map<String, String> serviceClassMap = new HashMap<String, String>();
    
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
	
	public String getErrorCodeSeparator() {
		return errorCodeSeparator;
	}
	public void setErrorCodeSeparator(String errorCodeSeparator) {
		this.errorCodeSeparator = errorCodeSeparator;
	}
    
	/**
     * Extend OOB method to send error code in case specific exceptions are thrown.
     */
	protected void addObjectToElement(ParsedURI pParsedURI, Object pValue, Element pElement, int pNestingDepth, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws RestException,IOException
	{
    	if(pValue instanceof DropletException)    		
	    {
   			pElement.addText(((DropletException)pValue).getErrorCode()+
   					getErrorCodeSeparator()+
   					((DropletException)pValue).getMessage());
	    }
        else super.addObjectToElement(pParsedURI, pValue, pElement, pNestingDepth, pRequest, pResponse);
	}

	@Override
	public void outputBean(ParsedURI pParsedURI, Object pResContainer,
			int pNestingDepth, DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws RestException,
			IOException {
		/*This code sets the result value to false in case there are exceptions in the formHandler response*/
		if (pResContainer instanceof FormHandlerPropertiesAndExceptions) {
			FormHandlerPropertiesAndExceptions formHandler = (FormHandlerPropertiesAndExceptions) pResContainer;
			if (formHandler.getExceptions() != null
					&& formHandler.getExceptions().size() > 0) {
				formHandler.setResult(false);
			}else if(!formHandler.isResult() && (formHandler.getExceptions()==null || formHandler.getExceptions().size()==0)){
				formHandler.setResult(true);
			}
		} else if (pResContainer instanceof FormHandlerExceptions) {
			FormHandlerExceptions formHandler = (FormHandlerExceptions) pResContainer;
			if (formHandler.getExceptions() != null
					&& formHandler.getExceptions().size() > 0) {
				formHandler.setResult(false);
			}else if(!formHandler.isResult() && (formHandler.getExceptions()==null || formHandler.getExceptions().size()==0)){
				formHandler.setResult(true);
			}
		}
		/*Code End*/
		super.outputBean(pParsedURI, pResContainer, pNestingDepth, pRequest,
				pResponse);
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
    
}
