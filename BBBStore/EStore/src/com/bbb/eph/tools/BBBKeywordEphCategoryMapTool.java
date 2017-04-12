package com.bbb.eph.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.adapter.gsa.GSARepository;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.eph.vo.BBBEphCategoryMapVo;
import com.bbb.eph.vo.BBBProductEphCategoryMapVo;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

public class BBBKeywordEphCategoryMapTool extends BBBGenericService{
	
	final String CLS_NAME = "BBBKeywordEphCategoryMapTool";
	
	private static int MAPP_UP_TO_DATE=0;
	private static int MAPP_DOES_NOT_EXIST=1;
	private static int MAPP_ITEM_OUT_DATED=2;
	
	private BBBCatalogToolsImpl bbbCatalogTools;	
	private String productAndEphWithKeywordByConcept;
	private String allProductAndEphWithKeywordByConcept;
	private Repository ephCatRepository;
	
	private Repository omnitureReportRepository;
	
	private String keywordtoEphCatMappSeedName;
	
	private String insertKeywordToEphCatMapQuery;
	
	private String updateKeywordToEphCatMapQuery;
	
	private String latestModifiedDateForConcept;

	private String removeOldMappingsQuery;
	
	private IdGenerator idGenerator;
	
	/**
	 * Method used to build mapping between keyword with EPH/category with helper method.
	 * @param concept
	 * @throws BBBSystemException
	 */
	
	public void createEPHCategoryMapping(String concept, boolean isFullMappingRefresh) throws BBBSystemException{
		logDebug(CLS_NAME+":createEPHCategoryMapping Start:concept:["+concept+"]");
		BBBPerformanceMonitor.start(CLS_NAME + "createEPHCategoryMapping");
		
		int topProductCount=0; 	
		int totalKeyword=0;
		int processedKeyword=0;		
		int skippedKeyword=0;
		long startTime=System.currentTimeMillis();
		
	try {
		
		
		topProductCount= Integer.parseInt(getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.OMNITURE_TOP_PRODUCT_COUNT_EPH_MAPPING, BBBCoreConstants.STRING_THREE));
		logDebug(CLS_NAME+":getTopProductCount End:topProductCount:["+topProductCount+"]");
		
		Map<String,List<BBBProductEphCategoryMapVo>> keywordObpListMap = getKeywordsProductAndEphCodeByConcept(concept,topProductCount,isFullMappingRefresh);
				
		List<BBBEphCategoryMapVo> insertKeywordToEphVOList= new ArrayList<BBBEphCategoryMapVo>();
		List<BBBEphCategoryMapVo> updateKeywordToEphVOList= new ArrayList<BBBEphCategoryMapVo>();
			for( Map.Entry<String,List<BBBProductEphCategoryMapVo>> keywordEphVoEntry : keywordObpListMap.entrySet())
					{
							String keyword=keywordEphVoEntry.getKey();
							List<BBBProductEphCategoryMapVo> keywordEphVoList=keywordEphVoEntry.getValue();
							
						try {
								
								BBBEphCategoryMapVo ephCategoryMapVo=getEphsOrCategoryIdsString(keywordEphVoList,keyword,concept);
								if(StringUtils.isNotBlank(ephCategoryMapVo.getEPHList()) || StringUtils.isNotBlank(ephCategoryMapVo.getCategoryList()))
								{
									int status=checkMappingInRepository(ephCategoryMapVo);
									if(MAPP_DOES_NOT_EXIST == status )
									{
										insertKeywordToEphVOList.add(ephCategoryMapVo);
									}
									else if(MAPP_ITEM_OUT_DATED == status)
									{
										updateKeywordToEphVOList.add(ephCategoryMapVo);
									}
									
								}
							}
							catch (RepositoryException  exception)
							{
								logError(CLS_NAME
										+ ":createEPHCategoryMapping In Eexcepion for concept:["
										+ concept + "],keyword,[" + keyword
										+ "],Exception msg:" + exception.getMessage()
										+ ",exception:" + exception);
							}
					}
			
		int updatedCount=performUpdateInBatch(concept,updateKeywordToEphVOList);
		int insertCount=performInsertInBatch(concept,insertKeywordToEphVOList);
		totalKeyword=keywordObpListMap.size();
		processedKeyword=updatedCount+insertCount;
		skippedKeyword=totalKeyword-processedKeyword;		
		}
		catch (BBBSystemException sysExcep)
		{
			logError(CLS_NAME+":createEPHCategoryMapping Start:concept:["+concept+"],Exception"+sysExcep);
			throw sysExcep;
		}
		finally
		{
			BBBPerformanceMonitor.end(CLS_NAME + "createEPHCategoryMapping:");	
		}
		
		
		logDebug(CLS_NAME + ":createEPHCategoryMapping End:concept:[" + concept
				+ "],totalKeyword:[" + totalKeyword + "],"
				+ "processedKeyword count:[" + processedKeyword
				+ "],skippedKeyword count:[" + skippedKeyword
				+ "],TotalTime consumed:["
				+ (System.currentTimeMillis() -startTime ) + "]");

	}
	
	/**
	 * This method will delete keyword-EPH/category mappings
	 * which are older than mappingRetentionDays. 
	 * mappingRetentionDays is configured from BCC. 
	 * 
	 */
	public void removeOldMappings() throws BBBSystemException{
		logDebug(CLS_NAME+":removeOldMappings Start");
		BBBPerformanceMonitor.start(CLS_NAME + "removeOldMappings");
		boolean success = true;
		
		// fetch retention days config key
		int mappingRetentionDays = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.KEYWORD_EPH_CAT_MAP_RETENTION_DAYS, BBBCoreConstants.FIFTY);		

		Connection connection=null;
		PreparedStatement preparedStatement = null;			
		try {
			 long startTime = System.currentTimeMillis();	
			 
			 connection = ((GSARepository)getEphCatRepository()).getDataSource().getConnection();
			 preparedStatement=connection.prepareStatement(getRemoveOldMappingsQuery());			 
			 preparedStatement.setInt(1, mappingRetentionDays);
			 
			 int deletedItems = preparedStatement.executeUpdate();			 			 
		 	 this.logDebug("There are " + deletedItems + "old mappings, which have been deleted.");
		 	 
		 	 long endTime = System.currentTimeMillis();
		 	 logDebug("removeOldMappings start time : " + startTime
					+ ", end time : " + endTime +
					", Total time taken : "	+ (endTime - startTime));
			
			} catch (SQLException exception) {
				success = false;
				logError("Exception in removeOldMappings due to : "+exception);
				BBBPerformanceMonitor.cancel(CLS_NAME + "removeOldMappings");
				throw new BBBSystemException(BBBCoreConstants.SQLEXCEPTION);
			}
			finally
			{ 
				 try{						
						if(preparedStatement != null)
						{
							preparedStatement.close();
						}
						if(connection != null && !connection.isClosed())
						{
							if (success)
							{
								connection.commit();
							}
							else {
								connection.rollback();
							}
										
							connection.close();
								
						}
					 }
				 	catch (SQLException e) {
				 		logError(CLS_NAME + " Error in deleting old keyword-EPH/category mappings:"+e);
				 	}
				 
				BBBPerformanceMonitor.end(CLS_NAME + "removeOldMappings");
				
			}
		 
		logDebug(CLS_NAME+":removeOldMappings");
				
	}
	
	/**
	 * This method will check the keyword to eph/categoryList mapping in the
	 * repository.
	 * 
	 * @param ephCategoryMapVo
	 * @return status(	 0 : mapping exist in repository and no update required;
	 * 					 1: mapping does not exist in repository,
	 * 					 2: mapping exist in repository but need to be updated with current mapping,in the case  method set id of Item in the VO which used to update the item with SQL ) 
	 * @throws RepositoryException
	 */

	private int checkMappingInRepository(BBBEphCategoryMapVo ephCategoryMapVo) throws RepositoryException {
		
		
		String keyword=ephCategoryMapVo.getKeyword();
		String concept=	ephCategoryMapVo.getConcept();	
		String ephList=ephCategoryMapVo.getEPHList();
		String categoryList=ephCategoryMapVo.getCategoryList();
		logDebug(CLS_NAME + ":checkMappingInRepository Start|  Keyword:["+ keyword + "],concept:["+ concept + "],ephList:["+ ephList + ",catList:["+ categoryList + "]");
		
		BBBPerformanceMonitor.start(CLS_NAME + "checkMappingInRepository"); 
		
		int success = MAPP_UP_TO_DATE;
		 
	try {	
		
			RepositoryItemDescriptor ephMapItemDesc =getEphCatRepository().getItemDescriptor(BBBCoreConstants.EPH_CATEGORY_MAPPING);
			RepositoryView view=ephMapItemDesc.getRepositoryView();
			QueryBuilder userBuilder = view.getQueryBuilder();
			
			QueryExpression conceptQueyEx  = userBuilder.createPropertyQueryExpression(BBBCoreConstants.CONCEPT);
	        QueryExpression conceptValueEx = userBuilder.createConstantQueryExpression(concept);
	        Query conceptEqualQuery        = userBuilder.createComparisonQuery(conceptQueyEx, conceptValueEx, QueryBuilder.EQUALS);
	        
	        QueryExpression keywordQueyEx  = userBuilder.createPropertyQueryExpression(BBBCoreConstants.SEARCH_KEYWORD);
	        QueryExpression keywordValueEx = userBuilder.createConstantQueryExpression(keyword);
	        Query keywordEqualQuery        = userBuilder.createComparisonQuery(keywordQueyEx, keywordValueEx, QueryBuilder.EQUALS);
			
	        Query[] getkeywordForConcept 		= { conceptEqualQuery, keywordEqualQuery};
	        Query getkeywordForConceptQuery 	= userBuilder.createAndQuery(getkeywordForConcept);
			MutableRepositoryItem[] ephItems 	= (MutableRepositoryItem[])view.executeQuery(getkeywordForConceptQuery);
			if(ephItems != null)
			{   
				MutableRepositoryItem ephItem=ephItems[0];
				String ephInRep=(String) ephItem.getPropertyValue(BBBCoreConstants.EPH_IDS);
				String categoryInRep=(String) ephItem.getPropertyValue(BBBCoreConstants.CATEGORYIDS);
				
				logDebug("checkMappingInRepository :Item Value: eph:["+ephInRep+"]"+",category:["+categoryInRep+"]");
				logDebug("checkMappingInRepository :new  Value: eph:["+ephList+"]"+",category:["+categoryList+"]");
				 
				if ((StringUtils.isNotBlank(ephList) && !ephList
						.equalsIgnoreCase(ephInRep))
						
						|| (StringUtils.isNotBlank(categoryList) && !categoryList
								.equalsIgnoreCase(categoryInRep))
								
						|| (StringUtils.isNotBlank(ephInRep) && !ephInRep
								.equalsIgnoreCase(ephList))
								
						|| (StringUtils.isNotBlank(categoryInRep) && !categoryInRep
								.equalsIgnoreCase(categoryList)))  
					{

						ephCategoryMapVo.setId(ephItem.getRepositoryId());
						logDebug("checkMappingInRepository: mapping need to be updated for repositoryId:["+ephItem.getRepositoryId()+"],for keyword:["+keyword+"],concept:["+concept+"]");
						success=MAPP_ITEM_OUT_DATED;
					}
				else{
						logDebug("checkMappingInRepository :item alreadyUpToDate repositoryId:["+ephItem.getRepositoryId()+"],for keyword:["+keyword+"],concept:["+concept+"]");
						
				    }
			}
			else
			{
				success= MAPP_DOES_NOT_EXIST;
				logDebug("checkMappingInRepository :No Mapping found for keyword:["+keyword+"],concept:["+concept+"]");
			}
	
	  }
	catch (RepositoryException repExcep)
	{
		logError(CLS_NAME+":checkMappingInRepository Exception:"+repExcep);
		throw repExcep;
	}
	finally
	{
		BBBPerformanceMonitor.end(CLS_NAME + "insertOrUpdateMapping");
	}
	
	logDebug(CLS_NAME+":checkMappingInRepository End | Keyword:["+ keyword + "],concept:["+ concept + "],ephList:["+ ephList + ",catList:["+ categoryList + "]");
	return success;
	}

	
	/**
	 * This method used to build ephList if any one OBP have EPH
	 * code,else  method append primaryNode/firstParentCategory of all OBP
	 * and sets as categoryList in VO
	 * 
	 * @param productEphVoList
	 * @param keyword
	 * @param concept
	 * @return
	 */

	private BBBEphCategoryMapVo getEphsOrCategoryIdsString(List<BBBProductEphCategoryMapVo> productEphVoList,String keyword,String concept) {
		
		 
		logDebug(CLS_NAME+":getEphsOrCategoryIdsString Start:keyword:["+keyword+"],concept:["+concept+"]");
		BBBPerformanceMonitor.start(CLS_NAME + "getEphsOrCategoryIdsString");
		boolean isEphAvailForKeyword=false;
		int count=0 ;
		StringBuffer ePHList=new StringBuffer();
		
		long startTime = System.currentTimeMillis();
		
		BBBEphCategoryMapVo ephCategoryMapVo= new BBBEphCategoryMapVo();
		ephCategoryMapVo.setKeyword(keyword);
		ephCategoryMapVo.setConcept(concept);
		Set<String> alreadyAddedEPH= new HashSet<String>();
		
		for(BBBProductEphCategoryMapVo prodEphCatMapVo :productEphVoList)
		{
			String prodEph =prodEphCatMapVo.getEph();
			String productId=prodEphCatMapVo.getProductId();
			logDebug("Build EPHList: productId["+productId+"],prodEph:["+prodEph+"]");
			if(StringUtils.isNotBlank(prodEph))
			{	
				
				// do not add same eph if multiple product have same EPH code.
				if( !alreadyAddedEPH.contains(prodEph) )
				{	
					if(count != 0)
					{
						ePHList.append(",")	;
					}
					ePHList.append(prodEph);
					alreadyAddedEPH.add(prodEph);
					
					count++;
					isEphAvailForKeyword=true;
				}
			}
		}
		if(isEphAvailForKeyword)
		{	
			String ephString =ePHList.toString();
			logDebug("getEphsOrCategoryIdsString: isEphAvailForKeyword[true],ephString["+ephString+"],keyword:["+keyword+"],concept:["+concept+"]");
			ephCategoryMapVo.setEPHList(ephString);	
		}
		else
		{   
			
			ephCategoryMapVo.setCategoryList(getCategoryListForKeyword(productEphVoList,keyword, concept));
		}
				
		long endTime = System.currentTimeMillis();
		logDebug("removeOldMappings start time : " + startTime
				+ ", end time : " + endTime +
				", Total time by method : "	+ (endTime - startTime));
		BBBPerformanceMonitor.end(CLS_NAME + "getEphsOrCategoryIdsString");
		
		logDebug(CLS_NAME + ":getEphsOrCategoryIdsString End:keyword:["
				+ keyword + "],concept:[" + concept + "]:ephList:["
				+ ephCategoryMapVo.getEPHList() + "],catList:["
				+ ephCategoryMapVo.getCategoryList() + "]");
		
		return ephCategoryMapVo;
	}
	/**
	 * get category List for a keyword.
	 * @param productEphVoList
	 * @param keyword
	 * @param concept
	 * @return
	 */
	
	private String getCategoryListForKeyword(List<BBBProductEphCategoryMapVo> productEphVoList,String keyword,String concept) {
		
		 
		logDebug(CLS_NAME+":getCategoryListForKeyword Start: isEphAvailForKeyword[false],Find PrimaryOrCateNode for keyword:["+keyword+"],concept:["+concept+"]");
		BBBPerformanceMonitor.start(CLS_NAME + "getCategoryListForKeyword");
		StringBuffer categoryIdList=new StringBuffer();
		String categoryIdListStr=null;
		Set<String> alreadyAddedCatId= new HashSet<String>();
		 
		int count=0 ;
		for(BBBProductEphCategoryMapVo prodEphCatMapVo :productEphVoList)
		{		
			String productId=prodEphCatMapVo.getProductId();
			
		try {
				
				String primaryCategoryId =	getBbbCatalogTools().getPrimaryCategory(productId);
					
				if(StringUtils.isBlank(primaryCategoryId))
					{
						String siteId=SiteContextManager.getCurrentSiteId();
						String parentCategoryIdForProduct=	getBbbCatalogTools().getParentCategoryIdForProduct(productId,siteId);
						logDebug("getCategoryListForKeyword:getParentCategoryIdForProduct: productId:["+productId+"],parentCategoryIdForProduct:["+parentCategoryIdForProduct+"],siteId:["+siteId+"]");
						if(StringUtils.isNotBlank(parentCategoryIdForProduct))
						 {  
							
							if( !alreadyAddedCatId.contains(parentCategoryIdForProduct))
							{
								if(count != 0)
								{  
									categoryIdList.append(",")	;
								}
								categoryIdList.append(parentCategoryIdForProduct);
								alreadyAddedCatId.add(parentCategoryIdForProduct);
								count++;
							}
						 }
					}
					else
					{  	
						logDebug("getCategoryListForKeyword:getPrimaryCategory: productId:["+productId+"],primaryCategoryId:["+primaryCategoryId+"]");
						
						if( !alreadyAddedCatId.contains(primaryCategoryId))
						{	
							if(count != 0)
							{
								categoryIdList.append(",")	;
							}
							categoryIdList.append(primaryCategoryId);
							alreadyAddedCatId.add(primaryCategoryId); 
							count++;
						}
					}
				
					categoryIdListStr=categoryIdList.toString();
			    } 
				catch (BBBSystemException |BBBBusinessException exception) {
				logError("Exception in getCategoryListForKeyword:productId;["+productId+"],Exception Msg ["+exception.getMessage()+"],Exception:"+exception);
			   }
		}
		 
		BBBPerformanceMonitor.end(CLS_NAME + "getCategoryListForKeyword");
		logDebug(CLS_NAME+":getCategoryListForKeyword End:categoryIdList:["+categoryIdListStr+"]");
		
	return categoryIdListStr; 
	}
	
	/**
	 * This method used to fetch all keyword (only fetch those keyword from
	 * Omniture_report_data which have LastModifiedDate> Max(concept.lastModifiedDate) of
	 * BBB_KEYWORD_EPH_CAT )with top 'n' number of OBP and EPH code from
	 * Omniture_report_data .
	 * 
	 * @param concept
	 * @param topProductCount
	 * @return
	 * @throws BBBSystemException
	 */
	
	private Map<String,List<BBBProductEphCategoryMapVo>> getKeywordsProductAndEphCodeByConcept(String concept,int topProductCount, boolean isFullMappingRefresh) throws BBBSystemException{
		
		logDebug(CLS_NAME+":getKeywordsProductAndEphCodeByConcept Start:concept:["+concept+"],topProductCount:["+topProductCount+"],isMappingRefreshReq flag : "+isFullMappingRefresh);
		BBBPerformanceMonitor.start(CLS_NAME + "getKeywordsProductAndEphCodeByConcept");
		Connection connection=null;
		PreparedStatement preparedStatement = null;
		ResultSet rs=null;
		Map<String,List<BBBProductEphCategoryMapVo>> keywordObpListMap= new HashMap<String,List<BBBProductEphCategoryMapVo>>();
		
		
		try {
			 long startTime = System.currentTimeMillis();
			 
			 connection = ((GSARepository)getOmnitureReportRepository()).getDataSource().getConnection();
			 if(isFullMappingRefresh){
				 this.logDebug("executing query for full mapping refresh");
				 preparedStatement=connection.prepareStatement(getAllProductAndEphWithKeywordByConcept());			 
				 preparedStatement.setString(1, concept);
				 preparedStatement.setInt(2, topProductCount); 
			 } else {
				 this.logDebug("executing query for partial mapping refresh");
				 preparedStatement=connection.prepareStatement(getProductAndEphWithKeywordByConcept());			 
				 preparedStatement.setTimestamp(1, getLatestModifiedDateByConcept(concept));
				 preparedStatement.setString(2, concept);
				 preparedStatement.setInt(3, topProductCount);
			}			 
			 
			 rs= preparedStatement.executeQuery();
			 
			 while(rs.next()){
				 BBBProductEphCategoryMapVo productEphVo= new BBBProductEphCategoryMapVo();
				 String keyword =rs.getString(BBBCoreConstants.OPB_KEYWORD);
				 productEphVo.setKeyword(keyword);
				 productEphVo.setProductId(rs.getString(BBBCoreConstants.PRODUCT_ID_PARAM_NAME));
				 productEphVo.setEph(rs.getString(BBBCoreConstants.EPH_PROD_NODE_ID));
				 productEphVo.setConcept(concept);
				 
					 if(keywordObpListMap.containsKey(keyword))
					 {
						 keywordObpListMap.get(keyword).add(productEphVo);
					 }
					 else
					 {
						 List<BBBProductEphCategoryMapVo> productEphVoList= new ArrayList<BBBProductEphCategoryMapVo>();
						 productEphVoList.add(productEphVo);
						 keywordObpListMap.put(keyword,productEphVoList);
					 }
				 
			 }
			 
			 long endTime = System.currentTimeMillis();
			 logDebug("getKeywordsProductAndEphCodeByConcept start time : " + startTime
					+ ", end time : " + endTime +
					", Total time taken: "	+ (endTime - startTime));
			 
			
			} catch (SQLException exception) {
				logError("Exception in getKeywordsProductAndEphCodeByConcept due to : "+exception);
				throw new BBBSystemException(BBBCoreConstants.SQLEXCEPTION);
			}
			finally
			{ 
				 try{
						if(rs != null)
						{
							rs.close();
						}
						if(preparedStatement != null)
						{
							preparedStatement.close();
						}
						if(connection != null)
						{
							connection.close();
						}
					 }
				 	catch (SQLException e) {
				 		logError(CLS_NAME + " Error in getKeywordsProductAndEphCodeByConcept:"+e);
				 	}
				 
				BBBPerformanceMonitor.end(CLS_NAME + "getKeywordsProductAndEphCodeByConcept");
				
			}
		 
		logDebug(CLS_NAME+":getKeywordsProductAndEphCodeByConcept End:concept:["+concept+"],keyword Count:["+keywordObpListMap.size()+"]");
		
		return keywordObpListMap;
	}
	
	
	
	/**
	 * Method used to update  mapping with new value of EPH or Category for a keyword in batch
	 * @param concept
	 * @param updateKeywordToEphVOList
	 * @return
	 */
	private int performUpdateInBatch(String concept,List<BBBEphCategoryMapVo> updateKeywordToEphVOList){
		
		logDebug(CLS_NAME+":performUpdateInBatch Start:concept:["+concept+"]");
		
		BBBPerformanceMonitor.start(CLS_NAME + "performUpdateInBatch");
		
		Connection connection=null;
		PreparedStatement preparedStatement = null;
		int count = BBBCoreConstants.ZERO;
		final int updateBatchSize = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING,BBBCoreConstants.EPH_MAPPING_INSERT_BATCH_SIZE, 100);
		
		if(! BBBUtility.isListEmpty(updateKeywordToEphVOList))
		{   
			
			try {
				  long startTime = System.currentTimeMillis();
				  
				  connection = ((GSARepository)getEphCatRepository()).getDataSource().getConnection();
				  preparedStatement = connection.prepareStatement(getUpdateKeywordToEphCatMapQuery());
						
				  	for( BBBEphCategoryMapVo ephCategoryMapVo: updateKeywordToEphVOList)
						{
							count = count + BBBCoreConstants.ONE;
							preparedStatement.setString(1, ephCategoryMapVo.getEPHList());
							preparedStatement.setString(2, ephCategoryMapVo.getCategoryList());
							preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
							preparedStatement.setString(4, ephCategoryMapVo.getId());
							logDebug(CLS_NAME + ":performUpdateInBatch:concept:["
									+ concept + "],updating mapping with Id["
									+ ephCategoryMapVo.getId() + "],ephList:["
									+ ephCategoryMapVo.getEPHList() + "],catList:["
									+ ephCategoryMapVo.getCategoryList() + "]");
							
							preparedStatement.addBatch();
							
							
							if(count % updateBatchSize == BBBCoreConstants.ZERO)	{
								logDebug(CLS_NAME+":performInsertInBatch:concept:["+concept+"] execute Batch update: count[ "+count+"] updateBatchSize ["+updateBatchSize+"]");
								count = executeBatchCommitOrRollback(connection,   count, preparedStatement, concept);
							}
						}
				count = executeBatchCommitOrRollback(connection,   count, preparedStatement, concept);
				
				long endTime = System.currentTimeMillis();
				 logDebug("performUpdateInBatch start time : " + startTime
						+ ", end time : " + endTime +
						", Total time by method : "	+ (endTime - startTime));
			} catch (SQLException SqlExcep)
			{
				logError(CLS_NAME+":performUpdateInBatch SQLException:["+concept+"]"+",SQLException"+SqlExcep);
			}
			 finally {
			
							try {
								if (preparedStatement != null) {
									preparedStatement.close();
								}
								if (connection != null && !connection.isClosed()) {
									connection.close();
								}
			
							} catch (SQLException SqlExc) {
								logError(CLS_NAME
										+ ":performUpdateInBatch SQLException in releasing resources:["
										+ concept + "]" + ",SQLException" + SqlExc);
							}
							BBBPerformanceMonitor.end(CLS_NAME + "performUpdateInBatch");	
						}
		}
		
	 logDebug(CLS_NAME+":performUpdateInBatch End:concept:["+concept+"],total Number of recored updated:["+count+"]");
	 return count;
	}
	
	/**
	 * Used to create mapping between keyword and eph/categoryList in batch.
	 * @param concept
	 * @param insertKeywordToEphVOList
	 * @return
	 */
	
	private int performInsertInBatch(String concept,List<BBBEphCategoryMapVo> insertKeywordToEphVOList){
		
		logDebug(CLS_NAME+":performInsertInBatch Start:concept:["+concept+"]");
		
		BBBPerformanceMonitor.start(CLS_NAME + "performInsertInBatch");
		
		Connection connection=null;
		PreparedStatement preparedStatement = null;
		int count = BBBCoreConstants.ZERO;
		final int insertBatchSize = getBbbCatalogTools().getValueForConfigKey(BBBCoreConstants.OMNITURE_BOOSTING,BBBCoreConstants.EPH_MAPPING_INSERT_BATCH_SIZE, 100);
		
		if(! BBBUtility.isListEmpty(insertKeywordToEphVOList))
		{   
			
			try {
				  long startTime = System.currentTimeMillis();
				  
				  connection = ((GSARepository)getEphCatRepository()).getDataSource().getConnection();
				  preparedStatement = connection.prepareStatement(getInsertKeywordToEphCatMapQuery());
						
				  	for( BBBEphCategoryMapVo ephCategoryMapVo: insertKeywordToEphVOList)
						{
							count = count + BBBCoreConstants.ONE;
							String id = getIdGenerator().generateStringId(getKeywordtoEphCatMappSeedName());
							preparedStatement.setString(1, id);
							preparedStatement.setString(2, ephCategoryMapVo.getKeyword());
							preparedStatement.setString(3, ephCategoryMapVo.getConcept());
							preparedStatement.setString(4, ephCategoryMapVo.getEPHList());
							preparedStatement.setString(5, ephCategoryMapVo.getCategoryList());
							preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
							
							logDebug(CLS_NAME + ":performInsertInBatch:concept:["
									+ concept + "],creating mapping with Id[" + id
									+ "],ephList:[" + ephCategoryMapVo.getEPHList()
									+ "],catList:["
									+ ephCategoryMapVo.getCategoryList() + "]");
							
							preparedStatement.addBatch();
							
							if(count % insertBatchSize == BBBCoreConstants.ZERO)	{
								logDebug(CLS_NAME+":performInsertInBatch:concept:["+concept+"] execute Batch Insert count[ "+count+"] insertBatchSize  Size ["+insertBatchSize+"]");
								count = executeBatchCommitOrRollback(connection,   count, preparedStatement, concept);
							}
						}
				
				count = executeBatchCommitOrRollback(connection,   count, preparedStatement, concept);
				
				long endTime = System.currentTimeMillis();
				 logDebug("performUpdateInBatch start time : " + startTime
						+ ", end time : " + endTime +
						", Total time by method : "	+ (endTime - startTime));
			} catch (SQLException | IdGeneratorException SqlExcep)
			{
				logError(CLS_NAME+":performInsertInBatch SQLException:["+concept+"]"+",SQLException"+SqlExcep);
			}
			 finally {
			
							try {
								if (preparedStatement != null) {
									preparedStatement.close();
								}
								if (connection != null && !connection.isClosed()) {
									connection.close();
								}
			
							} catch (SQLException SqlExc) {
								logError(CLS_NAME
										+ ":performInsertInBatch SQLException in releasing resources:["
										+ concept + "]" + ",SQLException" + SqlExc);
							}
							BBBPerformanceMonitor.end(CLS_NAME + "performInsertInBatch");	
						}
		}
		
	 logDebug(CLS_NAME+":performInsertInBatch End:concept:["+concept+"],total Number of recored inserted:["+count+"]");
	 return count;
	}

	/**
	 * Util method to execute batch
	 * @param connection
	 * @param count
	 * @param preparedStatement
	 * @param concept
	 * @return
	 */
	private int executeBatchCommitOrRollback(Connection connection,  int count,PreparedStatement preparedStatement,String concept) {
	
	boolean success = false;
	
	logDebug(CLS_NAME+":executeBatchCommitOrRollback Start:concept:["+concept+"],count:["+count+"]");
	
	BBBPerformanceMonitor.start(CLS_NAME + "executeBatchCommitOrRollback");
	
	try{
			int[] updateCount=preparedStatement.executeBatch();
			logDebug(CLS_NAME+":executeBatchCommitOrRollback: Number of "+updateCount.length+" item Updated/inserted successfully: for concept:["+concept+"],count:["+count+"]");
			success = true;
		}
		catch(SQLException sqlException)
		{
			logError(CLS_NAME+":executeBatchCommitOrRollback Exception occured:concept:["+concept+"],sqlException"+ sqlException);
		}
		finally
		{
			try {
					if (connection != null)
					{
							if (success)
							{
								logInfo(CLS_NAME+":Total No Of Records Entered/updated :: [" + count + "] For Concept :: [" + concept+"]");
								connection.commit();
							}
						else {
								logError(CLS_NAME+":Total No Of Records Entered :: [" + count + "] For Concept :: [" + concept+"]");
								connection.rollback();
							}
					}
			} catch (SQLException e) {
				count = 0;
				logError(CLS_NAME+":SQL Exception ocurred for while committing/closing the preparedStatement/connection ", e);
			}									
		}
	BBBPerformanceMonitor.end(CLS_NAME + "executeBatchCommitOrRollback");
	
	logDebug(CLS_NAME+":executeBatchCommitOrRollback End:concept:["+concept+"]");
	
	return count;
	}
	
	/**
	 * Method get max(lastModifiedDate) of a concept from BBB_KEYWORD_EPH_CAT
	 * table.If there is no entry in the table for concept the this method will
	 * return SYSDATE-1
	 * 
	 * @param concept
	 * @return
	 * @throws BBBSystemException
	 */
	
	private Timestamp getLatestModifiedDateByConcept(String concept) throws BBBSystemException{
		
		logDebug(CLS_NAME+":getLatestModifiedDateByConcept Start:concept:["+concept+"]");
		
		BBBPerformanceMonitor.start(CLS_NAME + "getLatestModifiedDateByConcept");
		
		Connection connection=null;
		PreparedStatement preparedStatement = null;
		ResultSet rs=null;
		Timestamp latestDate=null;
		
				 
				  try {
						
						 connection = ((GSARepository)getEphCatRepository()).getDataSource().getConnection();
						 preparedStatement=connection.prepareStatement(getLatestModifiedDateForConcept());
						 preparedStatement.setString(1, concept);
						 rs= preparedStatement.executeQuery();
						 while(rs.next())
						    {							 
							 latestDate= rs.getTimestamp(1) ;
							}
						
						} catch (SQLException exception) {
							logError("Exception in getLatestModifiedDateByConcept due to : "+exception);
							throw new BBBSystemException(BBBCoreConstants.SQLEXCEPTION);
						}
						finally
						{ 
							 try{
									if(rs != null)
									{
										rs.close();
									}
									if(preparedStatement != null)
									{
										preparedStatement.close();
									}
									if(connection != null)
									{
										connection.close();
									}
								 }
							 	catch (SQLException e) {
							 		logError(CLS_NAME + " Error in getLatestModifiedDateByConcept:"+e);
							 	}
						}
	 logDebug(CLS_NAME+":getLatestModifiedDateByConcept End:concept:["+concept+"],latestDate Last ModifiedDate:["+latestDate+"]");
	 
	 return latestDate;
	}
	
	/**
	 * @return the ephCatRepository
	 */
	public Repository getEphCatRepository() {
		return ephCatRepository;
	}


	/**
	 * @param ephCatRepository the ephCatRepository to set
	 */
	public void setEphCatRepository(Repository ephCatRepository) {
		this.ephCatRepository = ephCatRepository;
	}


	


	/**
	 * @return the bbbCatalogTools
	 */
	public BBBCatalogToolsImpl getBbbCatalogTools() {
		return bbbCatalogTools;
	}


	/**
	 * @param bbbCatalogTools the bbbCatalogTools to set
	 */
	public void setBbbCatalogTools(BBBCatalogToolsImpl bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}


	
	/**
	 * @return the omnitureReportRepository
	 */
	public Repository getOmnitureReportRepository() {
		return omnitureReportRepository;
	}
	/**
	 * @param omnitureReportRepository the omnitureReportRepository to set
	 */
	public void setOmnitureReportRepository(Repository omnitureReportRepository) {
		this.omnitureReportRepository = omnitureReportRepository;
	}

	/**
	 * @return the keywordtoEphCatMappSeedName
	 */
	public String getKeywordtoEphCatMappSeedName() {
		return keywordtoEphCatMappSeedName;
	}

	/**
	 * @param keywordtoEphCatMappSeedName the keywordtoEphCatMappSeedName to set
	 */
	public void setKeywordtoEphCatMappSeedName(String keywordtoEphCatMappSeedName) {
		this.keywordtoEphCatMappSeedName = keywordtoEphCatMappSeedName;
	}

	/**
	 * @return the insertKeywordToEphCatMapQuery
	 */
	public String getInsertKeywordToEphCatMapQuery() {
		return insertKeywordToEphCatMapQuery;
	}

	/**
	 * @param insertKeywordToEphCatMapQuery the insertKeywordToEphCatMapQuery to set
	 */
	public void setInsertKeywordToEphCatMapQuery(
			String insertKeywordToEphCatMapQuery) {
		this.insertKeywordToEphCatMapQuery = insertKeywordToEphCatMapQuery;
	}

	/**
	 * @return the updateKeywordToEphCatMapQuery
	 */
	public String getUpdateKeywordToEphCatMapQuery() {
		return updateKeywordToEphCatMapQuery;
	}

	/**
	 * @param updateKeywordToEphCatMapQuery the updateKeywordToEphCatMapQuery to set
	 */
	public void setUpdateKeywordToEphCatMapQuery(
			String updateKeywordToEphCatMapQuery) {
		this.updateKeywordToEphCatMapQuery = updateKeywordToEphCatMapQuery;
	}

	/**
	 * @return the idGenerator
	 */
	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	/**
	 * @param idGenerator the idGenerator to set
	 */
	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	

	/**
	 * @return the latestModifiedDateForConcept
	 */
	public String getLatestModifiedDateForConcept() {
		return latestModifiedDateForConcept;
	}

	/**
	 * @param latestModifiedDateForConcept the latestModifiedDateForConcept to set
	 */
	public void setLatestModifiedDateForConcept(String latestModifiedDateForConcept) {
		this.latestModifiedDateForConcept = latestModifiedDateForConcept;
	}

	/**
	 * @return the productAndEphWithKeywordByConcept
	 */
	public String getProductAndEphWithKeywordByConcept() {
		return productAndEphWithKeywordByConcept;
	}

	/**
	 * @param productAndEphWithKeywordByConcept the productAndEphWithKeywordByConcept to set
	 */
	public void setProductAndEphWithKeywordByConcept(
			String productAndEphWithKeywordByConcept) {
		this.productAndEphWithKeywordByConcept = productAndEphWithKeywordByConcept;
	}

	/**
	 * @return the allProductAndEphWithKeywordByConcept
	 */
	public String getAllProductAndEphWithKeywordByConcept() {
		return allProductAndEphWithKeywordByConcept;
	}

	/**
	 * @param allProductAndEphWithKeywordByConcept the allProductAndEphWithKeywordByConcept to set
	 */
	public void setAllProductAndEphWithKeywordByConcept(
			String allProductAndEphWithKeywordByConcept) {
		this.allProductAndEphWithKeywordByConcept = allProductAndEphWithKeywordByConcept;
	}

	/**
	 * @return the removeOldMappingsQuery
	 */
	public String getRemoveOldMappingsQuery() {
		return removeOldMappingsQuery;
	}

	/**
	 * @param removeOldMappingsQuery the removeOldMappingsQuery to set
	 */
	public void setRemoveOldMappingsQuery(String removeOldMappingsQuery) {
		this.removeOldMappingsQuery = removeOldMappingsQuery;
	}
	
	


}
