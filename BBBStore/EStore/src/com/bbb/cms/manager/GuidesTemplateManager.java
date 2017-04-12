package com.bbb.cms.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import com.bbb.cms.GuidesContentVO;
import com.bbb.cms.GuidesTemplateVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 *This class will interact with repository to get the data.
 *guidesCategory and content would be coming from droplet and based on them we would be getting data. 
 * @author ajosh8
 *
 */

public class GuidesTemplateManager extends BBBGenericService {
  private static final String GUIDES_DESCRIPTOR = "guides";
  private static final String GUIDES_CATEGORY = "guidesCategory";
  private static final String GUIDES_CONTENT_TYPE = "contentType";
  private static final String GUIDES_ID = "id";
  private static final String GUIDES_TITLE = "title";
  private static final String GUIDES_IMAGE_URL = "imageUrl";
  private static final String GUIDES_IMAGE_ALT_TEXT = "imageAltText";
  private static final String GUIDES_SHORT_DESC = "shortDescription";
  private static final String GUIDES_LONG_DESC = "longDescription";
  private static final String GUIDES_OMNIDATA ="OmnitureData";


  private Repository mGuidesTemplate;



  /**
   * @return the guidesTemplate
   */
  public Repository getGuidesTemplate() {
    return mGuidesTemplate;
  }



  /**
   * @param pGuidesTemplate the guidesTemplate to set
   */
  public void setGuidesTemplate(Repository pGuidesTemplate) {
    mGuidesTemplate = pGuidesTemplate;
  }


  /**
   * method to fetch all guides related details based on content type and guild category provided
   * @param pContentType content type whose related guides need to be fetched 
   * @param pGuidesCategory guide category
   * @return list of guild details 
   * @throws BBBBusinessException exception in case of any business error occurred
 */
public List<GuidesTemplateVO> getGuidesTemplateData(String pContentType,String pGuidesCategory) throws BBBBusinessException{
    
	logDebug("GuidesTemplateManager.getGuidesTemplateData : START");
	List<GuidesTemplateVO> guidesContentList  = new ArrayList<GuidesTemplateVO>();
	
	if(BBBUtility.isEmpty(pContentType) || BBBUtility.isEmpty(pGuidesCategory)){
		logError(" input parameter  is null or empty throwing BBBBusinessException");
		throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM, "Input param can not be null");	
	}
	
	guidesContentList = getGuidesTemplateData(pContentType,pGuidesCategory,null);
	
	if(guidesContentList == null || guidesContentList.isEmpty()){
		logError(" no result found for pContentType:"+pContentType +" and pGuidesCategory:"+ pGuidesCategory + " throwing BBBBusinessException");
		throw new BBBBusinessException(BBBCmsConstants.ERROR_VALUE_NOT_FOUND, "no result found");
	}
	
	logDebug("GuidesTemplateManager.getGuidesTemplateData : END");
	return guidesContentList;
    
  }
  

  
  /**
   * This method will interact with guidesTemplate repository and get the data.
   * @param pContentType
   * @param pGuidesCategory
   * @return GuidesTemplateVO
   */
  public List<GuidesTemplateVO> getGuidesTemplateData(String pContentType,String pGuidesCategory,String pSiteId){

    RepositoryView view;
    QueryBuilder queryBuilder;
    QueryExpression propertyGuidesCategory;
    QueryExpression constantGuidesCategory;
    QueryExpression propertyContentType;
    QueryExpression constantContentType;
    final Query queryLanding;
    RepositoryItem[] items = null;
    String siteId;
    GuidesTemplateVO guidesTemplateVO=null;
    List<GuidesTemplateVO> lstGuidesTemplate=new ArrayList<GuidesTemplateVO>();

    
      logDebug("GuidesTemplateManager.getGuidesTemplateData() Method Entering");
    

    try {
      view=getGuidesTemplate().getView(GUIDES_DESCRIPTOR);
      queryBuilder=view.getQueryBuilder();

      propertyGuidesCategory = queryBuilder.createPropertyQueryExpression(GUIDES_CATEGORY);
      constantGuidesCategory=queryBuilder.createConstantQueryExpression(pGuidesCategory);

      propertyContentType = queryBuilder.createPropertyQueryExpression(GUIDES_CONTENT_TYPE);
      constantContentType=queryBuilder.createConstantQueryExpression(pContentType);

      if (StringUtils.isEmpty(pSiteId)) {

        siteId = SiteContextManager.getCurrentSiteId();
        }
      else{
        siteId=pSiteId;
      }
      
      //siteId="storeSiteUS";

      final Query[] queries = new Query[3];
      queries[0] = siteIdQuery(siteId, queryBuilder);
      queries[1] = queryBuilder.createComparisonQuery(propertyGuidesCategory, constantGuidesCategory, QueryBuilder.EQUALS);
      queries[2] = queryBuilder.createComparisonQuery(propertyContentType, constantContentType,QueryBuilder.EQUALS);
      queryLanding= queryBuilder.createAndQuery(queries);
      items = view.executeQuery(queryLanding);
      for (RepositoryItem item : items) {
        guidesTemplateVO =new GuidesTemplateVO();
        
        guidesTemplateVO.setGuideTemplateId((String)item.getPropertyValue(GUIDES_ID));
        guidesTemplateVO.setTitle((String)item.getPropertyValue(GUIDES_TITLE));
        guidesTemplateVO.setImageAltText((String)item.getPropertyValue(GUIDES_IMAGE_ALT_TEXT)); 
        guidesTemplateVO.setImageUrl((String) item.getPropertyValue(GUIDES_IMAGE_URL));
        guidesTemplateVO.setLongDescription((String) item.getPropertyValue(GUIDES_LONG_DESC));
        guidesTemplateVO.setShortDescription((String) item.getPropertyValue(GUIDES_SHORT_DESC));
        guidesTemplateVO.setOmnitureData((Map<String, String>) item.getPropertyValue(GUIDES_OMNIDATA));
        lstGuidesTemplate.add(guidesTemplateVO);

      }

    } catch (RepositoryException e) {

      LogMessageFormatter.formatMessage(null, "Repository Exception accured at GuidesManager");
    }catch(Exception e){
      LogMessageFormatter.formatMessage(null, "Exception accured at GuidesManager");
    }


   
      logDebug("GuidesTemplateManager.getGuidesTemplateData() Method Ending");
    

    return lstGuidesTemplate;
  }

  
  
  
  
  /**
   * This method will interact with guidesTemplate repository and get the long description.
   * @param pGuideId
   */
  public  GuidesTemplateVO getGuidesLongDescription(String pGuideId){

    RepositoryView view;
    QueryBuilder queryBuilder;
    QueryExpression propertyGuidesCategory;
    QueryExpression constantGuidesCategory;
    RepositoryItem[] items = null;
    GuidesTemplateVO guidesTemplateVO=null;
    Query query;
    

    
     logDebug("GuidesTemplateManager.getGuidesLongDescription() Method Entering");
    

    try {
      view=getGuidesTemplate().getView(GUIDES_DESCRIPTOR);
      queryBuilder=view.getQueryBuilder();

      propertyGuidesCategory = queryBuilder.createPropertyQueryExpression(GUIDES_ID);
      constantGuidesCategory=queryBuilder.createConstantQueryExpression(pGuideId);
      query = queryBuilder.createPatternMatchQuery(propertyGuidesCategory, constantGuidesCategory, QueryBuilder.EQUALS,true);
       
      items = view.executeQuery(query);
      if(items!=null){
      for (RepositoryItem item : items) {
         guidesTemplateVO =new GuidesTemplateVO();       
         guidesTemplateVO.setTitle((String)item.getPropertyValue(GUIDES_TITLE));
         guidesTemplateVO.setLongDescription((String)item.getPropertyValue(GUIDES_LONG_DESC));
         guidesTemplateVO.setOmnitureData((Map<String, String>) item.getPropertyValue(GUIDES_OMNIDATA));
         guidesTemplateVO.setImageUrl((String)item.getPropertyValue(GUIDES_IMAGE_URL));  
         guidesTemplateVO.setShortDescription((String) item.getPropertyValue(GUIDES_SHORT_DESC));    
       }
      }
    } catch (RepositoryException e) {

      LogMessageFormatter.formatMessage(null, "Repository Exception accured at GuidesManager");
    }catch(Exception e){
      LogMessageFormatter.formatMessage(null, "Exception accured at GuidesManager");
    }


    
     logDebug("GuidesTemplateManager.getGuidesLongDescription() Method Ending");
    

    return guidesTemplateVO;
  }
  
  private Query siteIdQuery(final String psiteId, final QueryBuilder pQueryBuilder) throws RepositoryException {
    final String siteId = psiteId;
    if (StringUtils.isBlank(siteId)){
      return null;
    }
    final QueryExpression propertyExpression = pQueryBuilder.createPropertyQueryExpression("site");
    final QueryExpression rootItem = pQueryBuilder.createConstantQueryExpression(siteId);
    final Query includesQuery = pQueryBuilder.createIncludesQuery(propertyExpression, rootItem);

    return includesQuery;
  }
  /**
   * The method provides details for all guides as per input contentType and guidesCategory
   * @param contentType
   * @param guidesCategory
   * @return
   * @throws BBBBusinessException
   */
  	public GuidesContentVO getAllGuidesContent(String contentType,String guidesCategory) throws BBBBusinessException{
		final GuidesContentVO guidesContentVO =new GuidesContentVO();
		
		final String siteId = SiteContextManager.getCurrentSiteId();
		logDebug("GuidesTemplateManager.getAllGuidesContent input param contentType "+contentType +" guidesCategory "+guidesCategory);
		if(StringUtils.isEmpty(contentType) || StringUtils.isEmpty(guidesCategory) ){
			logError(" input parameter  is null or empty throwing BBBBusinessException");
			throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM, "Input can not be null");
		}

		else{
			final List<GuidesTemplateVO> guidesVOList = this.getGuidesTemplateData(contentType,guidesCategory,siteId);

			if(guidesVOList==null || guidesVOList.isEmpty()){
				logError(" no result found for contentType"+contentType +" and siteId "+siteId+" throwing BBBBusinessException");
				throw new BBBBusinessException(BBBCmsConstants.ERROR_VALUE_NOT_FOUND,BBBCmsConstants.ERROR_VALUE_NOT_FOUND);

			}
			else{
				guidesContentVO.setGuidesContentList(guidesVOList);
			}
		}

		return guidesContentVO;

  	}
  
	/**
	 * method to fetch guide details for the guild id provided 
	 * @param guideId guide Id whose details needs to be fetched 
	 * @return guild related details
	 * @throws BBBBusinessException exception in case error occurred while fetching guild details 
	 */
	public GuidesTemplateVO getGuidesContent(String guideId) throws BBBBusinessException{
		logDebug("GuidesTemplateManager.getGuidesContent : START");
		GuidesTemplateVO guidesTemplateVO =new GuidesTemplateVO();
		logDebug("GuidesTemplateManager.getGuidesContent input param guideId "+guideId );
		if(StringUtils.isEmpty(guideId)  ){
			logError(" input parameter  is null or empty throwing BBBBusinessException");
			throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM, "Input guide id can not be null");
		}
	
		else{
			guidesTemplateVO  = this.getGuidesLongDescription(guideId);
			if(guidesTemplateVO==null ){
				logError(" no result found for guideId"+guideId + "throwing BBBBusinessException");
				throw new BBBBusinessException(BBBCmsConstants.ERROR_VALUE_NOT_FOUND, "no result found for guideId");
	
			}
			
		}
		
		logDebug("GuidesTemplateManager.getGuidesContent : END");
		return guidesTemplateVO;
	
	}

}