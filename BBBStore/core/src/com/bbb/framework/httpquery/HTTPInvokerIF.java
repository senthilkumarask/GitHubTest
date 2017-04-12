package com.bbb.framework.httpquery;

import java.util.Map;
import java.util.List;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public interface HTTPInvokerIF {
	
	String constructQuery(String targetURL, List<String> requiredParams, 
			List<String> optionalParams, Map<String,String> paramsValuesMap) throws BBBBusinessException;
	String executeQuery(String completeQueryURL) throws BBBBusinessException, BBBSystemException ;
}
