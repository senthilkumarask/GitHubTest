package com.bbb.eph.customizer;

import java.util.List;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * This component will be used to remove stop word from the search Term by matching the search term with OtherMap
 * @author Sapient
 *
 */
public class OtherSearchTermCustomizer extends BaseCustomizer {
	
	private static final String CLS_NAME = "OtherSearchTermCustomizer";
	private static final String OTHER_TYPE = "Other";
	
	/**
	 * Method will be used to initialize regex pattern for the customizer. This
	 * method will be called at component startup and from BBBSemanticDeployment
	 * Listener to refresh the regex pattern if there is any deployment happen
	 * in the Semantic Repository
	 */
	public  void initializeRegexPattern(){
		
		BBBPerformanceMonitor.start(CLS_NAME+BBBCoreConstants.UNDERSCORE+"initializeRegexPattern");
	  
		logDebug( CLS_NAME + " [initializeRegexPattern] method Start");		
		long startTime=System.currentTimeMillis();	
		
		List<String> regexWordList= (List) getBbbSemanticDataCacheContainer().get(KEYWORD_TYPE_UNDERSCORE+OTHER_TYPE);
		
		logDebug( CLS_NAME + " [initializeRegexPattern] compile PatternFrom List:");
		
		setRegexWordList(regexWordList);
		
		compilePattern();	
		
		logDebug( CLS_NAME + " [initializeRegexPattern] compile PatternFrom List End:");
		
		long endTime=System.currentTimeMillis();

		BBBPerformanceMonitor.end(CLS_NAME+BBBCoreConstants.UNDERSCORE+"initializeRegexPattern");
		
		logDebug(CLS_NAME + " [initializeRegexPattern] method End::tookTime:["+ (endTime - startTime) + "]");
		
		
	}

}
