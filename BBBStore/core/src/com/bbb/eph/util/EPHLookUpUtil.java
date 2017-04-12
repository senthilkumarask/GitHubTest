package com.bbb.eph.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.ServiceMap;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.eph.customizer.BaseCustomizer;
import com.bbb.eph.customizer.CategorySearchTermCustomizer;
import com.bbb.eph.customizer.ColorSearchTermCustomizer;
import com.bbb.eph.vo.BBBEphCategoryMapVo;
import com.bbb.eph.vo.EPHResultVO;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

public class EPHLookUpUtil extends BBBGenericService {
	
	private static final String CLS_NAME = "EPHLookUpUtil";
	
	private BBBConfigTools configTools;
	
	private Map<String,String> exclusionCustomizerMap;
	
	private String categoryCustomizer;
	
	private CoherenceCacheContainer cacheContainer;
	
	private Map<String,String> siteIdConceptMap;
	
	private String cacheName;
	
	/**
	 * Check if EPH Flag in configure keys.
	 * @return true if ephLookup enables else return false;
	 */
	
	/**
	 * Check if EPH Flag in configure keys.
	 * @return true if ephLookup enables else return false;
	 */
	public boolean isEpHLookUpEnable(){
		boolean isEnable=false;
		
		logDebug( CLS_NAME + " [isEpHLookUpEnable] method Start");	
		long startTime=System.currentTimeMillis();
		
		BBBPerformanceMonitor.start(CLS_NAME +BBBCoreConstants.UNDERSCORE+"isEpHLookUpEnable");
		String ephLookUpFlag= getConfigTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.EPH_LOOK_UP_FLAG, BBBCoreConstants.FALSE);
		if(StringUtils.isNotBlank(ephLookUpFlag)){
			isEnable=Boolean.valueOf(ephLookUpFlag);
		}
		
		
		long endTime=System.currentTimeMillis();
		BBBPerformanceMonitor.end(CLS_NAME +BBBCoreConstants.UNDERSCORE+"isEpHLookUpEnable");		
		logDebug( CLS_NAME + " [isEpHLookUpEnable] method End:isEnable:["+isEnable+"],tookTime:["+ (endTime - startTime) + "]");
		
		return isEnable ;    
	}

	/**
	 * This method used to find EPHList/NodeIdList for a keyword from
	 * cahceContainer If the searchTerm term does not available in the cacheContainer
	 * then this method will exclude color and stop words from the searchTerm
	 * and try to find in cache container.In case after removing stop words and
	 * color from the search term then this method will create searchTerm with -1
	 * algorithm with different combination to lookup the EPHList/NodeList
	 * 
	 * @param searchTerm
	 * @return EPHResultVO
	 */
	public EPHResultVO getEPHCodeForSearchTerm(String searchTerm){
		
		 
		logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method Start:searchTerm:["+searchTerm+"]");	
		
		long startTime=System.currentTimeMillis();
		
		BBBPerformanceMonitor.start(CLS_NAME +BBBCoreConstants.UNDERSCORE+"getEPHCodeForSearchTerm");
		
		EPHResultVO ephResultVo= new EPHResultVO();
		
		ephResultVo =checkSearchTermInCacheContainer(searchTerm,ephResultVo);
		
		try{
			
				if( ephResultVo.isEphOrL1L2ListAvailable())
				{	
					logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method:searchTerm:["+searchTerm+"] found in CacheContainer without colorMap and otherMap exclusion");
					return ephResultVo;
				}
				else
				{
					for(Map.Entry<String, String> entry : getExclusionCustomizerMap().entrySet()){						
						Entry<String,String> customizerEntry=   entry;
						String CustomizerCompPath= customizerEntry.getValue();
						BaseCustomizer customizer=(BaseCustomizer) Nucleus.getGlobalNucleus().resolveName(CustomizerCompPath);
						 if(customizer instanceof ColorSearchTermCustomizer)
						 {
							 List<String> exclusionList= new ArrayList<String>();
							 searchTerm=customizer.removeAll(searchTerm,exclusionList);
							 ephResultVo.setColorMatchApplied(true); 
							 ephResultVo.setColorList(exclusionList);
						 }else
						 {
							 searchTerm=customizer.removeAll(searchTerm,null);	 
						 }
					}
					
					logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method:Exclusion customizer Applied :parsed searchTerm:["+searchTerm+"]");
					
					if(BBBUtility.isBlank(searchTerm))
					{
						logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method: Parsed SearchTerm is blank After excluding words by OtherMap and ColorMap.Skipping EPHLookUp:Search Will happen as usual ");
						return ephResultVo;
					}
					
					// check parsed search Term in cache,if cache contain search term then return the EPHResultVo with search term
					
					ephResultVo=checkSearchTermInCacheContainer(searchTerm,ephResultVo);
					
					if( ephResultVo.isEphOrL1L2ListAvailable())
					{	
						logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method: parsed searchTerm:["+searchTerm+"] found in CacheContainer after colorMap and otherMap exclusion");
						return ephResultVo;
					}
					else
					{
						
						String maxParsedStringLenforEPHString= getConfigTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.MAX_PARSED_STRING_LEN_FOR_EPH, BBBCoreConstants.STRING_THREE);
						
						int maxParsedStringLenforEPH=Integer.parseInt(maxParsedStringLenforEPHString);
						
					    StringTokenizer st= new StringTokenizer(searchTerm,BBBCoreConstants.SPACE);
					    
					    int numberOfWordInSearchTerm=st.countTokens();
					    
					    logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method :maxParsedStringLenforEPH:["+maxParsedStringLenforEPH+"],Number of Word in searchTerm:["+numberOfWordInSearchTerm+"],parsed SearchTerm:["+searchTerm+"]");
					  
					    //Check length of search Term before performing Length-1 Algorithm 
					    if(numberOfWordInSearchTerm > maxParsedStringLenforEPH || numberOfWordInSearchTerm == 1)
					    {
					    	
					    	logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method: Lenght of searchTerm [" + searchTerm+ "] is greater than defined max lenght ["+maxParsedStringLenforEPH+"] Or parsed SearchTerm have only one word which already looked in Coherance Cache, Skip EPH lookUp");
					    	return ephResultVo;
					    }
					    else
					    {   
					    	// Create list of search term by reducing the length 1					    	
					    	List<String> searchTermList = createSearchTermList(searchTerm);
					    	
					    	if( ! BBBUtility.isListEmpty(searchTermList)){
					    		
					    		for(String createdSearchTerm : searchTermList)
					    		{
					    			logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method :match createdSearchTerm:["+createdSearchTerm+"] with categoryListMap");
					    			
					    			boolean isMatchWithCategoryMap=getCategorySearchTermCustomizer().match(createdSearchTerm);
					    			
					    			if(isMatchWithCategoryMap){
					    				
					    				logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method :createdSearchTerm:["+createdSearchTerm+"] found in categoryListMap");
					    				
					    				ephResultVo=checkSearchTermInCacheContainer(createdSearchTerm,ephResultVo);
					    				
					    				if( ephResultVo.isEphOrL1L2ListAvailable())
										{	
												logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method :createdSearchTerm:["+createdSearchTerm+"] found in CacheContainer");
												return ephResultVo;
										}
					    				else
					    				{
					    					
					    						logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method :createdSearchTerm:["+createdSearchTerm+"] not found in CacheContainer,next searchTerm will not be consider from SearchTermList as ["+createdSearchTerm+"] already match with categoryListMap");
					    						return ephResultVo;
					    					
					    				}
					    			}
					    		}
					    	}
					    }
					} 
				}
			}
			catch(Exception exception)
			{
				logError( CLS_NAME + " [getEPHCodeForSearchTerm] method : Exception:Message["+exception.getMessage()+"],exception:["+exception+"]");
			}
			finally
			{
				long endTime=System.currentTimeMillis();
				
				BBBPerformanceMonitor.end(CLS_NAME +BBBCoreConstants.UNDERSCORE+"getEPHCodeForSearchTerm");
				
				if(isLoggingDebug()){
					logDebug( CLS_NAME + " [getEPHCodeForSearchTerm] method End:searchTerm:["+searchTerm+"],tookTime:["+ (endTime - startTime) + "] Millis,ephResultVo:["+ephResultVo+"]");
				}
			}
		
		return ephResultVo;
		
	}

	/**
	 * This method used to check the keyword in cacheContainer,If a keyword
	 * available in cache then it will set EPH_Code/CategoryList in EPHResultVO and enables
	 * EphOrL1L2ListAvailable in EPHresultVo
	 * 
	 * @param searchTerm
	 * @param ephResultVo
	 * @return EPHResultVO
	 */
	public EPHResultVO checkSearchTermInCacheContainer(String searchTerm,EPHResultVO ephResultVo){
		
		logDebug( CLS_NAME + " [checkSearchTermInCacheContainer] method Start");
		
		long startTime=System.currentTimeMillis();
		
		BBBPerformanceMonitor.start(CLS_NAME +BBBCoreConstants.UNDERSCORE+"checkSearchTermInCacheContainer");
		
		
		String siteId=SiteContextManager.getCurrentSiteId();
		String concept=getSiteIdConceptMap().get(siteId);
		
		String conceptAwareKeyword=BBBUtility.trimToNotNull(searchTerm).toLowerCase()+BBBCoreConstants.UNDERSCORE+concept;
		String cacheName=getConfigTools().getConfigKeyValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,BBBCoreConstants.EPH_CATEGORY_MAP_CACHE_LOAD_CACHE_NAME,this.getCacheName());
		
		if(isLoggingDebug()){
			logDebug( CLS_NAME + " [checkSearchTermInCacheContainer] method : finding searchTerm :["+conceptAwareKeyword+"] in CacheContainer for SiteId:["+siteId+"],cacheName:["+cacheName+"]");
		}
		
		BBBEphCategoryMapVo ephCategoryMapVo=(BBBEphCategoryMapVo) getCacheContainer().get(conceptAwareKeyword,cacheName);
		
		if(ephCategoryMapVo != null)
			{
				
				if(ephCategoryMapVo != null && StringUtils.isNotBlank(ephCategoryMapVo.getEPHList()))
				{
					ephResultVo.setEphList(ephCategoryMapVo.getEPHList());
					ephResultVo.setEphOrL1L2ListAvailable(true);
				}else if(ephCategoryMapVo != null && StringUtils.isNotBlank(ephCategoryMapVo.getCategoryList()))
				{
					
					ephResultVo.setCategoryList(ephCategoryMapVo.getCategoryList());
					ephResultVo.setEphOrL1L2ListAvailable(true);
				}
				else
				{
					ephResultVo.setEphOrL1L2ListAvailable(false);
				}
			}
		 
		long endTime=System.currentTimeMillis();
		
		BBBPerformanceMonitor.end(CLS_NAME +BBBCoreConstants.UNDERSCORE+"checkSearchTermInCacheContainer");
		
		if(isLoggingDebug()){
			logDebug( CLS_NAME + " [checkSearchTermInCacheContainer] method End:searchTerm:["+searchTerm+"],ephResultVo:["+ephResultVo+"]tookTime:["+ (endTime - startTime) + "] Millis");
			}
		
		return ephResultVo;
	}
	/**
	 * This method used to create search Term combination by reducing the length of supplied search Term
	 * eg: if search Term is "abc xyz test" then list will contain "abc xyz, xyz test,abc test,abc,xyz,test"
	 * @param searchTerm
	 * @return
	 */
	private List<String> createSearchTermList(String searchTerm)
	 {
		logDebug( CLS_NAME + " [createSearchTermList] method Start");	
		BBBPerformanceMonitor.start(CLS_NAME +BBBCoreConstants.UNDERSCORE+"createSearchTermList");
		
		long startTime=System.currentTimeMillis();
		
	       	List<String> searchTermList= new ArrayList<String>();
	    	StringTokenizer st= new StringTokenizer(searchTerm, BBBCoreConstants.SPACE);
	    	int wordCount=st.countTokens();
	    	String wordArray[] = new String[wordCount];
	    	int arrayIndex=0;
	    	while(st.hasMoreElements()){
	    		wordArray[arrayIndex]=(String) st.nextElement();
	    		arrayIndex++;
	    	}
	    	if(wordCount > 1)
	    	{
		    	for( int removeWord =1;removeWord < wordCount; removeWord++)
		    	{
		    		logDebug( CLS_NAME + " [createSearchTermList] method : Create Search Term by length:["+wordCount+"-"+removeWord+"]");
			    	int maxWordInCombination=wordArray.length-removeWord;
			    	int maxWordInNewSearchTerm = wordArray.length-1;
			    	// temp data array to store all combination one by one
			    	String data[]=new String[maxWordInCombination];
			    	// Print all combination using temp array 'data[]'
			    	createAllCombination(wordArray, data, 0, maxWordInNewSearchTerm, 0, maxWordInCombination,searchTermList);
		    	}
	    	}
	    	else
	    	{
	    		searchTermList.add(searchTerm.trim());
	    	}
	    	
	    	long endTime=System.currentTimeMillis();
	    	
	    	BBBPerformanceMonitor.end(CLS_NAME +BBBCoreConstants.UNDERSCORE+"createSearchTermList");
	    	
	    	if(isLoggingDebug()){
	    	
	    		StringBuffer createdSearchTerms= new StringBuffer();
	    		for(String createdSearchTerm: searchTermList){
	    		createdSearchTerms.append(BBBCoreConstants.LEFT_PARENTHESIS);
	    		createdSearchTerms.append(createdSearchTerm);
	    		createdSearchTerms.append(BBBCoreConstants.RIGHT_PARENTHESIS);
	    		
	    		}
			logDebug( CLS_NAME + " [createSearchTermList] method End:searchTerm:["+searchTerm+"],Total Time Took:["+ (endTime - startTime) + "] Millis,created searchTermList Size:["+searchTermList.size()+"],createdSearchTerms:["+createdSearchTerms.toString()+"]");
	    	}
			return searchTermList;
    }
	/**
	 * creates search Term combination
	 * @param wordArray
	 * @param data
	 * @param start
	 * @param end
	 * @param index
	 * @param maxWordInCombination
	 * @param searchTermList
	 */
	private void createAllCombination(String wordArray[], String data[], int start, int end, int index, int maxWordInCombination, List<String> searchTermList)
	{
		
	    if (index == maxWordInCombination)
	    {
	    	StringBuffer searchTerm= new StringBuffer();
	        for (int startIndexTempArray=0; startIndexTempArray<maxWordInCombination; startIndexTempArray++){
	            
	            searchTerm.append(data[startIndexTempArray]+BBBCoreConstants.SPACE);
	        }
	        
	        
	       
	        searchTermList.add(searchTerm.toString().trim());
	        return;
	    }
	    for (int startIndexWordArray=start; startIndexWordArray<=end && end-startIndexWordArray+1 >= maxWordInCombination-index; startIndexWordArray++)
	    {
	        data[index] = wordArray[startIndexWordArray];
	        createAllCombination(wordArray, data, startIndexWordArray+1, end, index+1, maxWordInCombination,searchTermList);
	    }
	}	
		

	

	/**
	 * @return the cacheContainer
	 */
	public CoherenceCacheContainer getCacheContainer() {
		return cacheContainer;
	}

	/**
	 * @param cacheContainer the cacheContainer to set
	 */
	public void setCacheContainer(CoherenceCacheContainer cacheContainer) {
		this.cacheContainer = cacheContainer;
	}




	/**
	 * @return the siteIdConceptMap
	 */
	public Map<String, String> getSiteIdConceptMap() {
		return siteIdConceptMap;
	}

	/**
	 * @param siteIdConceptMap the siteIdConceptMap to set
	 */
	public void setSiteIdConceptMap(Map<String, String> siteIdConceptMap) {
		this.siteIdConceptMap = siteIdConceptMap;
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

	/**
	 * @return the cacheName
	 */
	public String getCacheName() {
		return cacheName;
	}

	/**
	 * @param cacheName the cacheName to set
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @return the exclusionCustomizerMap
	 */
	public Map<String, String> getExclusionCustomizerMap() {
		return exclusionCustomizerMap;
	}

	/**
	 * @param exclusionCustomizerMap the exclusionCustomizerMap to set
	 */
	public void setExclusionCustomizerMap(Map<String, String> exclusionCustomizerMap) {
		this.exclusionCustomizerMap = exclusionCustomizerMap;
	}

	/**
	 * @return the categoryCustomizer path
	 */
	public String getCategoryCustomizer() {
		return categoryCustomizer;
	}
	
	/**
	 * @return the categoryCustomizer path
	 */
	public CategorySearchTermCustomizer getCategorySearchTermCustomizer() {
		return (CategorySearchTermCustomizer) Nucleus.getGlobalNucleus().resolveName(getCategoryCustomizer());
	}

	/**
	 * @param categoryCustomizer the categoryCustomizer to set
	 */
	public void setCategoryCustomizer(String categoryCustomizer) {
		this.categoryCustomizer = categoryCustomizer;
	}
	


}
