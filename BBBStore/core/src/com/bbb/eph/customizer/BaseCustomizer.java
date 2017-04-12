package com.bbb.eph.customizer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atg.core.util.StringUtils;
import atg.nucleus.ServiceException;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

public abstract class BaseCustomizer extends BBBGenericService {
	
	protected static final String KEYWORD_TYPE_UNDERSCORE = "keywordType_";
	
	private BBBLocalCacheContainer bbbSemanticDataCacheContainer;
	
	private Pattern regexPattern;
	
	private List<String> regexWordList;
	
	private String regexPreFixKey;
	
	private String regexPostFixKey;
	
	private BBBConfigTools configTools;
	
	
	/**
	 * Abstract method which implements by sub classes to initialize the regex pattern.
	 */
	public abstract void initializeRegexPattern();
	 
	
	
	/**
	 * Used to match search Term with CategoryMap Pattern
	 * @param searchTerm
	 * @return true if there is any match else return false;
	 * 
	 */
	
	public boolean match(String searchTerm){
		logDebug( getName() + " [match] method Start::searchTerm:["+searchTerm+"]");
		BBBPerformanceMonitor.start(getName() +BBBCoreConstants.UNDERSCORE+"match");
		boolean isMatch=false;
		
		long startTime=System.currentTimeMillis();
		if(getRegexPattern() == null)
		{
			logDebug(  getName() + " [match] method :Regex Pattern in null:["+searchTerm+"]");
			return false;
		}
		Matcher matcher = getRegexPattern().matcher(searchTerm);
		isMatch= matcher.matches();
		
		long endTime=System.currentTimeMillis();
		BBBPerformanceMonitor.end(getName() +BBBCoreConstants.UNDERSCORE+"match");
		logDebug( getName() + " [match] method End:: searchTerm:[" + searchTerm + "],isMatch:["+ isMatch + "],tookTime:["+ (endTime - startTime) + "] Millis");
		return isMatch;
	}
	/**
	 * Method used to remove all word from the input string which match with regex pattern and add all matching words in the matchingWordList(those words which removed from the input string)
	 * @param searchTerm
	 * @param matchingWordList
	 * @return searchTerm 
	 */
	public String removeAll(String searchTerm,List<String> matchingWordList){
		logDebug(  getName() + " [removeAll] method Start::searchTerm:["+searchTerm+"]");
		BBBPerformanceMonitor.start(getName() +BBBCoreConstants.UNDERSCORE+"removeAll");
		long startTime=System.currentTimeMillis(); 
		//add space both side to avoid partial match:Eg:-  if keyword is woodland and color map have wood as a color,it should not remove wood from woodland
		searchTerm=BBBCoreConstants.SPACE+BBBUtility.trimToNotNull(searchTerm)+BBBCoreConstants.SPACE;
		String parsedSearchTerm=BBBUtility.trimToNotNull(doMatch(searchTerm,matchingWordList));
		logDebug(  getName() + " [removeAll] method ::refinedSearchTerm:["+parsedSearchTerm+"]"); 
		long endTime=System.currentTimeMillis();
		BBBPerformanceMonitor.end(getName() +BBBCoreConstants.UNDERSCORE+"removeAll");
		logDebug(  getName() + " [removeAll] method End::parsed searchTerm:["+parsedSearchTerm+"],matchingWordList:["+matchingWordList+"],tookTime:["+(endTime-startTime)+"] Millis");
		return parsedSearchTerm;
	}
	
	/**
	 * Method used to Compile  pattern from the sub classes after reading the keyword from Semantic Data Repository
	 */
	public void compilePattern()
	{
		logDebug(  getName() + " [compilePattern] method Start::");
		BBBPerformanceMonitor.start(getName() +BBBCoreConstants.UNDERSCORE+"compilePattern");
		long startTime = System.currentTimeMillis();
		Pattern pattern = null;
		Set<String> uniqueKeyWordSet= new LinkedHashSet<>();
		
		
		uniqueKeyWordSet.addAll(getRegexWordList());
		
		if (! BBBUtility.isListEmpty(getRegexWordList()))
		  {
			StringBuffer regexString =null;
			
			String regPreFix = getConfigTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, getRegexPreFixKey(), BBBCoreConstants.BLANK);
			String regPostFix = getConfigTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, getRegexPostFixKey(), BBBCoreConstants.BLANK);
			
			logDebug(  getName() + " [compilePattern] method regExPreFix::["+regPreFix+"],regPostFix:["+regPostFix+"]");
			
			
			regexString= new StringBuffer(regPreFix);
			
			
			for(String wordStr :uniqueKeyWordSet)
			 {
				  	regexString.append(BBBUtility.trimToNotNull(wordStr));  
				  	regexString.append(BBBCoreConstants.PIPE_SYMBOL.toString()); 
			}		 
			int lastIndex=regexString.lastIndexOf(BBBCoreConstants.PIPE_SYMBOL.toString()) ;
			
				regexString.delete(lastIndex, regexString.length());
				
				
				regexString.append(regPostFix); 
				
				
				pattern = Pattern.compile(regexString.toString(),Pattern.CASE_INSENSITIVE);
				
				this.setRegexPattern(pattern);
				
				logDebug(  getName() + " [compilePattern] Pattern compiled Successfully:pattern["+pattern+"]");
		}
		 
		if(pattern == null){
			logDebug( getName() + " [compilePattern] method : pattern is null:  Check getRegexWordList():["+getRegexWordList()+"]");
			this.setRegexPattern(null);
		}
		
		long endTime=System.currentTimeMillis();
		BBBPerformanceMonitor.end(getName() +BBBCoreConstants.UNDERSCORE+"compilePattern");
		
		logDebug(  getName() + " [compilePattern] method End::,tookTime:["+ (endTime - startTime) + "] Millis");
	}
	
	private String doMatch(String searchTerm,List<String> matchingWordList){
		logDebug(  getName() + " [doMatch] method Start::searchTerm:["+searchTerm+"]");
		Matcher matcher =null;
		if (null==getRegexPattern())
		{
			logDebug(  getName() + " [doMatch] method :Regex Pattern in null:["+searchTerm+"]");
			return searchTerm;
		} 
		
		matcher = getRegexPattern().matcher(searchTerm);
		boolean sizeMatched = matcher.matches();
		if (sizeMatched) {
			
			// group(2) is word in searchterm which match with the Regex pattern
			 String removedWord=matcher.group(2);
			 
			// group(1) is the String before the matching word in the Search Term
			 String group1=matcher.group(1)  ;
			// group(3) is the String after the matching word in the Search Term
			 String group3=matcher.group(3);
			 
			 String parsedSearchTerm=null;
			 
			 if(StringUtils.isNotBlank(group1)  && StringUtils.isNotBlank(group3) )
			 {
				 parsedSearchTerm=group1.trim() + BBBCoreConstants.SPACE+ group3.trim();
			 }
			 else if(StringUtils.isNotBlank(group1))
			 {
				 parsedSearchTerm=group1.trim();
			 }
			 else if(StringUtils.isNotBlank(group3))
			 {
				 parsedSearchTerm=group3.trim();
			 }
			 
			 if(removedWord != null)
			 {
				 removedWord=removedWord.trim();
			 }
			 logDebug(  getName() + " [doMatch] method Start::removedWord:["+removedWord+"],parsedSearchTerm:["+parsedSearchTerm+"]");
			 if(matchingWordList != null && ! matchingWordList.contains(removedWord)){
					matchingWordList.add(removedWord);
			 	}
			 return doMatch(BBBCoreConstants.SPACE+BBBUtility.trimToNotNull(parsedSearchTerm)+BBBCoreConstants.SPACE ,matchingWordList);
		}else{
			
			return searchTerm;
		}
	}
	
	@Override
	public void doStartService() throws ServiceException {
		
		
		try{
			
			
		    initializeRegexPattern();
		    
		    logInfo(  getName() + " startup complete");
		     
			}
		catch(Exception exception)
		{
			logError(  getName() + " [doStartService] initializeRegexPattern : exception due to:"+exception.getMessage()+",exception:"+exception);	
		}
		 
	}
	
	/**
	 * @return the regexPattern
	 */
	public Pattern getRegexPattern() {
		return regexPattern;
	}
	/**
	 * @param regexPattern the regexPattern to set
	 */
	public void setRegexPattern(Pattern regexPattern) {
		this.regexPattern = regexPattern;
	}



	/**
	 * @return the bbbSemanticDataCacheContainer
	 */
	public BBBLocalCacheContainer getBbbSemanticDataCacheContainer() {
		return bbbSemanticDataCacheContainer;
	}



	/**
	 * @param bbbSemanticDataCacheContainer the bbbSemanticDataCacheContainer to set
	 */
	public void setBbbSemanticDataCacheContainer(
			BBBLocalCacheContainer bbbSemanticDataCacheContainer) {
		this.bbbSemanticDataCacheContainer = bbbSemanticDataCacheContainer;
	}



	/**
	 * @return the regexWordList
	 */
	public List<String> getRegexWordList() {
		return regexWordList;
	}



	/**
	 * @param regexWordList the regexWordList to set
	 */
	public void setRegexWordList(List<String> regexWordList) {
		this.regexWordList = regexWordList;
	}



	/**
	 * @return the regexPreFixKey
	 */
	public String getRegexPreFixKey() {
		return regexPreFixKey;
	}



	/**
	 * @param regexPreFixKey the regexPreFixKey to set
	 */
	public void setRegexPreFixKey(String regexPreFixKey) {
		this.regexPreFixKey = regexPreFixKey;
	}



	/**
	 * @return the regexPostFixKey
	 */
	public String getRegexPostFixKey() {
		return regexPostFixKey;
	}



	/**
	 * @param regexPostFixKey the regexPostFixKey to set
	 */
	public void setRegexPostFixKey(String regexPostFixKey) {
		this.regexPostFixKey = regexPostFixKey;
	}



	/**
	 * @return the configTools
	 */
	public BBBConfigTools getConfigTools() {
		return configTools;
	}



	/**
	 * @param configTools the configTools to set
	 */
	public void setConfigTools(BBBConfigTools configTools) {
		this.configTools = configTools;
	}
	
	

}
