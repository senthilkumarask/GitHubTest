package com.bbb.eph.customizer;

import java.util.List;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * This component will be used to match created search term with -1 length with CategoryMap
 * @author Sapient
 *
 */
public class CategorySearchTermCustomizer extends BaseCustomizer {
	
	private static final String CATEGORY_TYPE = "CategoryType";
	private static final String CLS_NAME = "CategorySearchTermCustomizer";
	
	/**
	 * Method will be used to initialize regex pattern for the customizer. This
	 * method will be called at component startup and from BBBSemanticDeployment
	 * Listener to refresh the regex pattern if there is any deployment happen
	 * in the Semantic Repository
	 */
	
	public  void initializeRegexPattern(){
		
		logDebug( CLS_NAME + " [initializeRegexPattern] method Start");		
		
		BBBPerformanceMonitor.start(CLS_NAME +BBBCoreConstants.UNDERSCORE+"initializeRegexPattern");
		
		long startTime=System.currentTimeMillis();	
		List<String> regexWordList= (List) getBbbSemanticDataCacheContainer().get(KEYWORD_TYPE_UNDERSCORE+CATEGORY_TYPE);
		setRegexWordList(regexWordList);
		compilePattern();	
		long endTime=System.currentTimeMillis();
		
		
		BBBPerformanceMonitor.end(CLS_NAME +BBBCoreConstants.UNDERSCORE+"initializeRegexPattern");
		
		logDebug(CLS_NAME + " [initializeRegexPattern] method End::tookTime:["+ (endTime - startTime) + "]");
		
		
	}
}
