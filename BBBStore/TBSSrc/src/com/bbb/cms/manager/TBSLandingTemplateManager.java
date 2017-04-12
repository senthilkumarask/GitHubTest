package com.bbb.cms.manager;

import java.util.ArrayList;
import java.util.List;

import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import com.bbb.cms.LandingTemplateVO;
import com.bbb.cms.PromoBoxLayoutVO;
import com.bbb.cms.PromoBoxVO;
import com.bbb.cms.PromoImageVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 * This Class interact with repository and get the content for bridal_landing.jsp,college_landing.jsp 
 * and category_landing.jsp
 * @author ajosh8
 *
 */

public class TBSLandingTemplateManager extends LandingTemplateManager {
  private static final String COLLEGE_PROMO_BOX2 = "collegePromoBox2";
  private static final String COLLEGE_PROMO_BOX1 = "collegePromoBox1";
  private static final String CATEGORY_PROMO_WIDGET = "catPromoWidget";
  private static final String CATEGORY_SEO_STATIC_TEXT = "seoStaticText";
  private static final String NOT_REQUIRED = "NotRequired";
  private static final String LAND_DESCRIPTOR = "landing";
  private static final String LAND_PAGE_NAME = "pageName";
  private static final String LAND_CAT_ID = "category";
  private static final String LAND_PAGE_TITLE = "pageTitle";
  private static final String LAND_MARKET_URL = "marketBannerUrl";
  private static final String LAND_PROMO_CONTENT = "promoSmallContent";
  private static final String LAND_HERO_IMAGE = "heroImage";
  private static final String LAND_BRIDAL_TOOL = "bridalTools";
  private static final String LAND_REG_CAT_IMAGE = "registryCategoryImage";
  private static final String LAND_PROMO_TIERL1 = "promoTierLayOut1";
  private static final String LAND_PROMO_TIERL2 = "promoTierLayOut2";
  private static final String LAND_KEEP_SAKE_SHOP = "keepSakeShop";
  private static final String LAND_BRANDS = "brands";
  private static final String LAND_BRAND_IMAGE = "brandImage";
  private static final String LAND_BRAND_NAME= "brandName";
  private static final String LAND_BRAND_DESC = "brandDescrip";
  private static final String PROD_TIER_TEMP_LAYOUT1 = "promoTierTempLayOut1";
  private static final String PROD_TIER_TEMP_LAYOUT2 = "promoTierTempLayOut2";
  private static final String PROD_TIER_TEMP_LAYOUT3 = "promoTierTempLayOut3";
  private static final String PROMOBOX_IMAGE_URL = "imageUrl";
  private static final String PROMOBOX_IMAGE_ALT_TEXT = "imageAltText";
  private static final String PROMOBOX_IMAGE_MAP_NAME = "imageMapName";
  private static final String PROMOBOX_IMAGE_MAP_CONTENT = "imageMapContent";
  private static final String PROMOBOX_CONTENT = "promoBoxContent";
  private static final String PROMOBOX_TITLE = "promoBoxTitle";  
  private static final String PROMO_IMAGE_LINK = "imageLink";
  private static final String PROMOIMAGE_LINK_LABEL = "linkLabel";
  private static final String PROMOIMAGE_LINK_URL = "linkUrl";
  private static final String SITE = "site";
  private BBBCatalogTools bbbCatalogTools = null;

  private Repository mLandingTemplate;
  
  private static final String JUST_FOR_YOU = "justForYouFlag";
  private static final String CLEARANCE_DEALS = "clearanceDealsFlag";
  private static final String TOP_REGISTRY_ITEMS = "topRegistryItemsFlag";
  private static final String ALSO_CHECK_OUT = "alsoCheckOutFlag";
  private static final String TOP_COLLEGE_ITEMS = "topCollegeItemsFlag";
  
  private static final String SUB_CATEGORIES = "subCategories";
  private static final String CATEGORY = "category";
  private static final String CAT_CONTIANER = "catContianer";
  private static final String MAX_L2_CATEGORY_COUNT = "maxL2CategoryCount";
  private static final String MAX_L3_CATEGORY_COUNT = "maxL3CategoryCount";

  /**
   * @return the landingTemplate
   */
  public Repository getLandingTemplate() {
    return mLandingTemplate;
  }

  /**
   * @param pLandingTemplate the landingTemplate to set
   */
  public void setLandingTemplate(Repository pLandingTemplate) {
    mLandingTemplate = pLandingTemplate;
  }

  public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools){
    this.bbbCatalogTools = bbbCatalogTools;
  }

  public BBBCatalogTools getBbbCatalogTools() {
    return bbbCatalogTools;
  }

  /**
   * This method would interact with LandingTemplate repository and based on provided page name and
   * category id it will get the content. 
   * @param pPageName
   * @param pCategoryId
   * @return LandingTemplateVO
   */


  @SuppressWarnings("unchecked")
  public LandingTemplateVO getLandingTemplateData(String pPageName,String pCategoryId,String pSiteId){
    RepositoryView view = null;
    QueryBuilder queryBuilder;
    QueryExpression queryExpPageName;
    QueryExpression queryPageName;
    QueryExpression queryCategoryId;
    QueryExpression queryExpCategoryId;
    RepositoryItem[] items = null;
    final Query queryLanding;
    

    LandingTemplateVO landingTemplateVO = null;

    landingTemplateVO = new LandingTemplateVO();

    vlogDebug("LandingTemplateManager.getLandingTemplateData() Method Entering");

    try {
      view = getLandingTemplate().getView(LAND_DESCRIPTOR);
      queryBuilder = view.getQueryBuilder();
      queryExpPageName = queryBuilder.createPropertyQueryExpression(LAND_PAGE_NAME);
      queryPageName = queryBuilder.createConstantQueryExpression(pPageName);


      if(pCategoryId.equals(NOT_REQUIRED)){
        if (isLoggingDebug()) {
          logDebug("Request is coming from BridalLandingPage : No Category Required");
        }
        
        final Query[] queries = new Query[2];
        queries[0] = siteIdQuery(pSiteId, queryBuilder);
        queries[1] = queryBuilder.createComparisonQuery(queryExpPageName, queryPageName, QueryBuilder.EQUALS);
        queryLanding= queryBuilder.createAndQuery(queries);
       }
      else{
        queryExpCategoryId = queryBuilder.createPropertyQueryExpression(LAND_CAT_ID);

        queryCategoryId = queryBuilder.createConstantQueryExpression(pCategoryId);

        final Query[] queries = new Query[3];
        queries[0] = siteIdQuery(pSiteId, queryBuilder);
        queries[1] = queryBuilder.createComparisonQuery(queryExpPageName, queryPageName, QueryBuilder.EQUALS);
        queries[2] = queryBuilder.createComparisonQuery(queryExpCategoryId, queryCategoryId,QueryBuilder.EQUALS);
        queryLanding= queryBuilder.createAndQuery(queries);
        
      }

     
      if(isLoggingDebug()){
        logDebug("Landing Query to retrieve data : "+queryLanding);
      }
      items = view.executeQuery(queryLanding);

      if(items !=null){
        for (RepositoryItem item : items) {
          landingTemplateVO.setPageTitle((String)item.getPropertyValue(LAND_PAGE_TITLE));
          if(item.getPropertyValue(LAND_MARKET_URL) != null){
        	  String banerUrl = (String)(item.getPropertyValue(LAND_MARKET_URL));
        	  if(!StringUtils.isBlank(banerUrl) && banerUrl.contains("/store")){
        		  banerUrl = banerUrl.replaceAll("/store", "/tbs");
        	  }
        	  landingTemplateVO.setMarketingBannerUrl(banerUrl);
          }
          if(item.getPropertyValue(LAND_PROMO_CONTENT) != null){
        	  String promocontent = (String)item.getPropertyValue(LAND_PROMO_CONTENT);
              if(!StringUtils.isBlank(promocontent) && promocontent.contains("/store")){
            	  promocontent = promocontent.replaceAll("/store", "/tbs");
              }
              landingTemplateVO.setPromoSmallContent(promocontent);
          }
          
          if((RepositoryItem)item.getPropertyValue(COLLEGE_PROMO_BOX1) != null){
        	  RepositoryItem itemCollegePromoTierLayOut1=(RepositoryItem)item.getPropertyValue(COLLEGE_PROMO_BOX1);
              landingTemplateVO.setCollegePromoBox1(setPromoBoxVO(itemCollegePromoTierLayOut1));  
          }else{
        	  if(isLoggingDebug()) {
					logDebug("College Promo Box1 is not avilable");
					}
          }
          
          if((RepositoryItem)item.getPropertyValue(COLLEGE_PROMO_BOX2) != null){
        	  RepositoryItem itemCollegePromoTierLayOut2=(RepositoryItem)item.getPropertyValue(COLLEGE_PROMO_BOX2);
              landingTemplateVO.setCollegePromoBox2(setPromoBoxVO(itemCollegePromoTierLayOut2));
           }else{
         	  if(isLoggingDebug()) {
 					logDebug("College Promo Box2 is not avilable");
 					}
           }
          
          if(null !=  (RepositoryItem)item.getPropertyValue(CATEGORY_PROMO_WIDGET)){
        	  RepositoryItem itemCategoryPromoWidget = (RepositoryItem)item.getPropertyValue(CATEGORY_PROMO_WIDGET);
              landingTemplateVO.setCategoryPromoWidget(setPromoBoxVO(itemCategoryPromoWidget));
           }else{
 				vlogDebug("Category Widget Promo VO is not avilable");
           }
          
        //R2.1 implementation for SEO Static Text on Category Landing Page Start
          if(null !=  (String)item.getPropertyValue(CATEGORY_SEO_STATIC_TEXT)){
        	  String staticText = (String)(item.getPropertyValue(CATEGORY_SEO_STATIC_TEXT));
              if(!StringUtils.isBlank(staticText) && staticText.contains("/store")){
            	  staticText = staticText.replaceAll("/store", "/tbs");
              }
              landingTemplateVO.setSeoStaticText(staticText);
           }else{
 				vlogDebug("Category SEO Static Text is not available");
           }
        //R2.1 implementation for SEO Static Text on Category Landing Page End
          
          List<RepositoryItem> heroImages =(List<RepositoryItem>)item.getPropertyValue(LAND_HERO_IMAGE);

          //Calling method for getting content of PromoBox

          List<PromoBoxVO> heroImageList=(List<PromoBoxVO>) setPromoBox(heroImages);
          landingTemplateVO.setHeroImages(heroImageList);
          
          List<RepositoryItem> circularListings =(List<RepositoryItem>)item.getPropertyValue("circularListings");

          //Calling method for getting content of circular listings
          if(circularListings != null){
        	  List<PromoBoxVO> circularListingsList=(List<PromoBoxVO>) setPromoBox(circularListings);
        	  landingTemplateVO.setCircularListing(circularListingsList);
          }
          

          List<RepositoryItem> bridalTools=(List<RepositoryItem>) item.getPropertyValue(LAND_BRIDAL_TOOL);

          //Calling method for getting content of PromoImage

          List<PromoImageVO> bridalToolsList =(List<PromoImageVO>) setPromoImage(bridalTools);
          landingTemplateVO.setBridalTool(bridalToolsList);

          List<RepositoryItem> registryCategoryImage=(List<RepositoryItem>) item.getPropertyValue(LAND_REG_CAT_IMAGE);
          List<PromoImageVO> registryCategoryList=(List<PromoImageVO>) setPromoImage(registryCategoryImage);
          landingTemplateVO.setRegistryCategoryImage(registryCategoryList);


          List<RepositoryItem> promoTierLayOutFirst= (List<RepositoryItem>) item.getPropertyValue(LAND_PROMO_TIERL1);

          //Calling method for getting content of PromoTierLayOut
          List<PromoBoxLayoutVO> promoTierLayOutFirstList=(List<PromoBoxLayoutVO>)setPromoTierLayOut(promoTierLayOutFirst);

          landingTemplateVO.setPromoTierLayout1(promoTierLayOutFirstList);


          List<RepositoryItem> promoTierLayOutSecond= (List<RepositoryItem>) item.getPropertyValue(LAND_PROMO_TIERL2);
          List<PromoBoxLayoutVO> promoTierLayOutSecondList=(List<PromoBoxLayoutVO>) setPromoTierLayOut(promoTierLayOutSecond);
          landingTemplateVO.setPromoTierLayout2(promoTierLayOutSecondList);

          List<RepositoryItem> keepSakeShop= (List<RepositoryItem>) item.getPropertyValue(LAND_KEEP_SAKE_SHOP);
          List<ProductVO> keepSakeShopList=(List<ProductVO>)setProduct(pSiteId,keepSakeShop);
       
          landingTemplateVO.setKeepsakeShop(keepSakeShopList);

          List<BrandVO> brandList = getBrandsList(item);

          landingTemplateVO.setBrands(brandList);
          
          if (null != item.getPropertyValue(JUST_FOR_YOU)){
				final Boolean justForYouFlag = (Boolean) item.getPropertyValue(JUST_FOR_YOU);
				if(null != justForYouFlag) {
					if(isLoggingDebug()) {
						logDebug("justForYouFlag  is not Null");
						}
					landingTemplateVO.setJustForYouFlag(justForYouFlag);
				}
			}
          
          if (null != item.getPropertyValue(CLEARANCE_DEALS)){
				final Boolean clearanceDealsFlag = (Boolean) item.getPropertyValue(CLEARANCE_DEALS);
				if(null != clearanceDealsFlag) {
					if(isLoggingDebug()) {
						logDebug("clearanceDealsFlag  is not Null");
						}
					landingTemplateVO.setClearanceDealsFlag(clearanceDealsFlag);
				}
			}
          
          if (null != item.getPropertyValue(TOP_REGISTRY_ITEMS)){
				final Boolean topRegistryItemsFlag = (Boolean) item.getPropertyValue(TOP_REGISTRY_ITEMS);
				if(null != topRegistryItemsFlag) {
					if(isLoggingDebug()) {
						logDebug("topRegistryItemsFlag  is not Null");
						}
					landingTemplateVO.setTopRegistryItemsFlag(topRegistryItemsFlag);
				}
			}
          
          if (null != item.getPropertyValue(ALSO_CHECK_OUT)){
				final Boolean alsoCheckOutFlag = (Boolean) item.getPropertyValue(ALSO_CHECK_OUT);
				if(null != alsoCheckOutFlag) {
					if(isLoggingDebug()) {
						logDebug("alsoCheckOutFlag  is not Null");
						}
					landingTemplateVO.setAlsoCheckOutFlag(alsoCheckOutFlag);
				}
			}
          
          if (null != item.getPropertyValue(TOP_COLLEGE_ITEMS)){
				final Boolean topCollegeItemsFlag = (Boolean) item.getPropertyValue(TOP_COLLEGE_ITEMS);
				if(null != topCollegeItemsFlag) {
					if(isLoggingDebug()) {
						logDebug("topCollegeItemsFlag  is not Null");
						}
					landingTemplateVO.setTopCollegeItemsFlag(topCollegeItemsFlag);
				}
			}
          
          	//Setting SubCategories and L2 and L3 count
          	if (null != item.getPropertyValue(MAX_L2_CATEGORY_COUNT)){
				final int L2CategoryCount = Integer.valueOf((String) item.getPropertyValue(MAX_L2_CATEGORY_COUNT));
				if(isLoggingDebug()) {
					logDebug("maxL2CategoryCount  is " + L2CategoryCount);
					}
				landingTemplateVO.setL2CategoryCount(L2CategoryCount);
			}
          	if (null != item.getPropertyValue(MAX_L3_CATEGORY_COUNT)){
				final int L3CategoryCount = Integer.valueOf((String) item.getPropertyValue(MAX_L3_CATEGORY_COUNT));
				if(isLoggingDebug()) {
					logDebug("maxL2CategoryCount  is " + L3CategoryCount);
					}
				landingTemplateVO.setL3CategoryCount(L3CategoryCount);
			}

          	RepositoryItem categoryContainerItem =  (RepositoryItem)item.getPropertyValue(CAT_CONTIANER);
          	final List<RepositoryItem> subCategoriesItemList = new ArrayList<RepositoryItem>();
          	
			if(null != categoryContainerItem){
					String id = categoryContainerItem.getRepositoryId();
					if(null!=categoryContainerItem.getPropertyValue(CATEGORY)){
					 if(null!= categoryContainerItem.getPropertyValue(SUB_CATEGORIES)){
						 subCategoriesItemList.addAll((List<RepositoryItem>)categoryContainerItem.getPropertyValue(SUB_CATEGORIES));
					 }
				}
			}
			List<CategoryVO> subCategories = new ArrayList<CategoryVO>();
          	CategoryVO categoryVO = new CategoryVO();
          	CategoryVO subCategoryVO = null;
          	
			 for (RepositoryItem subCategoryItem : subCategoriesItemList) {
				 subCategoryVO = new CategoryVO();
				 subCategoryVO.setCategoryId(subCategoryItem.getRepositoryId());
				 subCategoryVO.setCategoryName((String)subCategoryItem.getPropertyValue("displayName"));
				 subCategories.add(subCategoryVO);
			 }
			 categoryVO.setSubCategories(subCategories);
			 landingTemplateVO.setCategory(categoryVO);
        }
      }
    } catch (RepositoryException e) {
    	if(isLoggingError()) {
      logError(LogMessageFormatter.formatMessage(null, "Repository Exception accured at LandingTemplateManager.getLandingTemplateData","catalog_1058"),e);
      
    	}
    }catch(Exception e){
      
      if(isLoggingError()) {
          logError(LogMessageFormatter.formatMessage(null, "Exception accured at LandingTemplateManager.getLandingTemplateData","catalog_1059"),e);
          
        	}
    }

    if (isLoggingDebug()) {
      logDebug("Existing method LandingTemplateManager.getLandingTemplateData()");

    }
    return landingTemplateVO;
  }

  /**
   * This method will return the BrandsVO based on the brand ID's specified in the template
   * @param item
   * @return List<BrandVO>
   */
  private List<BrandVO> getBrandsList(RepositoryItem item){	
    
    @SuppressWarnings("unchecked")
	List<RepositoryItem> brands= (List<RepositoryItem>) item.getPropertyValue(LAND_BRANDS);
    List<BrandVO> brandList=new ArrayList<BrandVO>();
    BrandVO brandVO=null;

    for (RepositoryItem itemp : brands) {
      brandVO = new BrandVO();
      brandVO.setBrandId((String)itemp.getRepositoryId());
      brandVO.setBrandImage((String)itemp.getPropertyValue(LAND_BRAND_IMAGE));
      brandVO.setBrandName((String)itemp.getPropertyValue(LAND_BRAND_NAME));
      brandVO.setBrandImageAltText((String)itemp.getPropertyValue(LAND_BRAND_DESC));

      brandList.add(brandVO);
    }
    return brandList;
  }

  /**
   * This method will get the property of product and set them in to list and later we can set this 
   * list into LandingTemplateVO
   * @param pLandingTemplateVO
   * @param pTopRegistry
   * @return List<ProductVO>
   */
  public List<ProductVO> setProduct(String siteId, List<RepositoryItem> pTopRegistry){
    ProductVO productVO=null;
    List<ProductVO> productList =new ArrayList<ProductVO>();
    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setProduct() Method Entering");
    }
    for (RepositoryItem itemp : pTopRegistry) {
      try{
        productVO = getBbbCatalogTools().getProductDetails(siteId, itemp.getRepositoryId(), true);
        productList.add(productVO);
      }catch(BBBBusinessException bbe){
        if(isLoggingError()){
          logError("catalog_1060: Received Business Exception. Ignore product "+ itemp.getRepositoryId()+" and continue loop :");
        }
      } catch(BBBSystemException bse){
        if(isLoggingError()){
          logError("catalog_1061: "+bse);
        }	
      } catch(Exception e){
        if(isLoggingError()){
          logError("catalog_1062: "+e);
        }
      } 
    }
    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setProduct() Method Ending");
    }
    return productList;

  }


  /**
   * This method would be calling the promo box method three times and get the list of content
   * @param pLandingTemplateVO
   * @param pPromoTierLayOut
   * @return List<PromoBoxLayoutVO>
   */
  public List<PromoBoxLayoutVO> setPromoTierLayOut(List<RepositoryItem> pPromoTierLayOut){

    List<PromoBoxLayoutVO> promoTierLayOutList=new ArrayList<PromoBoxLayoutVO>();

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoTierLayOut() Method Entering");
    }

    for (RepositoryItem item : pPromoTierLayOut) {

      PromoBoxLayoutVO promoBoxLayoutVO = new PromoBoxLayoutVO();
      RepositoryItem promoTierLayOut1 = (RepositoryItem) item.getPropertyValue(PROD_TIER_TEMP_LAYOUT1);

      PromoBoxVO promoBoxVO1=setPromoBoxVO(promoTierLayOut1);

      promoBoxLayoutVO.setPromoBoxFirstVOList(promoBoxVO1);

      RepositoryItem promoTierLayOut2 = (RepositoryItem) item.getPropertyValue(PROD_TIER_TEMP_LAYOUT2);
      PromoBoxVO promoBoxVO2=setPromoBoxVO(promoTierLayOut2);

      promoBoxLayoutVO.setPromoBoxSecondVOList(promoBoxVO2);

      RepositoryItem promoTierLayOut3 = (RepositoryItem) item.getPropertyValue(PROD_TIER_TEMP_LAYOUT3);
      PromoBoxVO promoBoxVO3=setPromoBoxVO(promoTierLayOut3);
      promoBoxLayoutVO.setPromoBoxThirdVOList(promoBoxVO3);

      promoTierLayOutList.add(promoBoxLayoutVO);
    }

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoTierLayOut() Method Ending");
    }

    return promoTierLayOutList;
  }

  /**
   * 
   * @param pPromoBox
   * @return PromoBoxVO
   */

  public PromoBoxVO setPromoBoxVO(RepositoryItem pPromoBox){

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoBoxVO() Method Entering");
    }
    PromoBoxVO promoBoxVO=new PromoBoxVO();
    String imgUrl = (String)pPromoBox.getPropertyValue(PROMOBOX_IMAGE_URL);
    if(!StringUtils.isBlank(imgUrl) && imgUrl.contains("/store")){
    	imgUrl = imgUrl.replaceAll("/store", "/tbs");
    }
    promoBoxVO.setImageURL(imgUrl);
    promoBoxVO.setImageAltText((String)pPromoBox.getPropertyValue(PROMOBOX_IMAGE_ALT_TEXT));
    String imgMapName = (String)pPromoBox.getPropertyValue(PROMOBOX_IMAGE_MAP_NAME);
    if(!StringUtils.isBlank(imgMapName) && imgMapName.contains("/store")){
    	imgMapName = imgMapName.replaceAll("/store", "/tbs");
    }
    promoBoxVO.setImageMapName(imgMapName);
    
    String imgcontent = (String)pPromoBox.getPropertyValue(PROMOBOX_IMAGE_MAP_CONTENT);
    if(!StringUtils.isBlank(imgcontent) && imgcontent.contains("/store")){
    	imgcontent = imgcontent.replaceAll("/store", "/tbs");
    }
    promoBoxVO.setImageMapContent(imgcontent);
    
    String imgBoxcontent = (String)pPromoBox.getPropertyValue(PROMOBOX_CONTENT);
    if(!StringUtils.isBlank(imgBoxcontent) && imgBoxcontent.contains("/store")){
    	imgBoxcontent = imgBoxcontent.replaceAll("/store", "/tbs");
    }
    promoBoxVO.setPromoBoxContent(imgBoxcontent);
    
    promoBoxVO.setPromoBoxTitle((String)pPromoBox.getPropertyValue(PROMOBOX_TITLE));
    
    String imgLink = (String)pPromoBox.getPropertyValue(PROMO_IMAGE_LINK);
    if(!StringUtils.isBlank(imgLink) && imgLink.contains("/store")){
    	imgLink = imgLink.replaceAll("/store", "/tbs");
    }
    promoBoxVO.setImageLink(imgLink);


    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoBoxVO() Method Ending");
    }

    return promoBoxVO;

  }


  /**
   * This method will get the promo image  properties and put them into PromoImageVO in to LandingVO  
   * @param pPromoImage
   * @return PromoImageVO
   */
  public PromoImageVO setPromoImage(RepositoryItem pPromoImage){

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoImage() Method Entering");
    }
    PromoImageVO promoImageVO =new PromoImageVO();

      promoImageVO.setImageAltText((String)pPromoImage.getPropertyValue(PROMOBOX_IMAGE_ALT_TEXT));
      promoImageVO.setImageUrl((String)pPromoImage.getPropertyValue(PROMOBOX_IMAGE_URL));
      promoImageVO.setLinkLabel((String)pPromoImage.getPropertyValue(PROMOIMAGE_LINK_LABEL));
      
      String imglink = (String)pPromoImage.getPropertyValue(PROMOIMAGE_LINK_URL);
      if(!StringUtils.isBlank(imglink) && imglink.contains("/store")){
    	  imglink = imglink.replaceAll("/store", "/tbs");
      }
      promoImageVO .setLinkUrl(imglink);

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoImage() Method Ending");
    }

    return promoImageVO;

  }

  /**
   * This method will get the promo image  properties and put them into list later we would be setting this list
   * in to LandingVO  
   * @param pLandingTemplateVO
   * @param pPromoImage
   * @return List<PromoImageVO>
   */

  private  List<PromoImageVO> setPromoImage(List<RepositoryItem> pPromoImage){

    PromoImageVO promoImageVO = null;
    List<PromoImageVO> promoImageList =new ArrayList<PromoImageVO>();


    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoImage() Method Entering");
    }

    for (RepositoryItem item : pPromoImage) {
      promoImageVO =new PromoImageVO();

      promoImageVO.setImageAltText((String)item.getPropertyValue(PROMOBOX_IMAGE_ALT_TEXT));
      promoImageVO.setImageUrl((String)item.getPropertyValue(PROMOBOX_IMAGE_URL));
      promoImageVO.setLinkLabel((String)item.getPropertyValue(PROMOIMAGE_LINK_LABEL));
      
      String imglink = (String)item.getPropertyValue(PROMOIMAGE_LINK_URL);
      if(!StringUtils.isBlank(imglink) && imglink.contains("/store")){
    	  imglink = imglink.replaceAll("/store", "/tbs");
      }
      promoImageVO .setLinkUrl(imglink);

      promoImageList.add(promoImageVO);

    }

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoImage() Method Ending");
    }

    return promoImageList;

  }


  /**
   * This method will get the promo box properties and put them into list later we would be setting this list
   * in to LandingVO
   * @param pLandingTemplateVO
   * @param pPromoBox
   * @return List<PromoBoxVO>
   */

  public  List<PromoBoxVO> setPromoBox(List<RepositoryItem> pPromoBox){


    PromoBoxVO promoBoxVO = null ;
    List<PromoBoxVO> promoBoxList =new ArrayList<PromoBoxVO>();

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoBox() Method Entering");
    }

    for (RepositoryItem item : pPromoBox) {
      promoBoxVO=new PromoBoxVO();
      promoBoxVO.setId(item.getRepositoryId());
      promoBoxVO.setImageURL((String)item.getPropertyValue(PROMOBOX_IMAGE_URL));
      promoBoxVO.setImageAltText((String)item.getPropertyValue(PROMOBOX_IMAGE_ALT_TEXT));
      
      String imgmapName = (String)item.getPropertyValue(PROMOBOX_IMAGE_MAP_NAME);
      if(!StringUtils.isBlank(imgmapName) && imgmapName.contains("/store")){
    	  imgmapName = imgmapName.replaceAll("/store", "/tbs");
      }
      promoBoxVO.setImageMapName(imgmapName);
      
      String imgmapContent = (String)item.getPropertyValue(PROMOBOX_IMAGE_MAP_CONTENT);
      if(!StringUtils.isBlank(imgmapContent) && imgmapContent.contains("/store")){
    	  imgmapContent = imgmapContent.replaceAll("/store", "/tbs");
      }
      promoBoxVO.setImageMapContent(imgmapContent);
      
      String imglink = (String)item.getPropertyValue(PROMO_IMAGE_LINK);
      if(!StringUtils.isBlank(imglink) && imglink.contains("/store")){
    	  imglink = imglink.replaceAll("/store", "/tbs");
      }
      promoBoxVO.setImageLink(imglink);
      StringBuffer sb = new StringBuffer();
      
      String promoContent = (String)item.getPropertyValue(PROMOBOX_CONTENT);
      if(!StringUtils.isBlank(promoContent) && promoContent.contains("/store")){
    	  promoContent = promoContent.replaceAll("/store", "/tbs");
      }
      sb.append(promoContent); 
      promoBoxVO.setPromoBoxContent(sb.toString());
      promoBoxVO.setPromoBoxTitle((String)item.getPropertyValue(PROMOBOX_TITLE));
      promoBoxList.add(promoBoxVO);
    }

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.setPromoBox() Method Ending");
    }

    return promoBoxList;



  }

  /**
   * This function is used to query for states based on Site Id.
   */
  public Query siteIdQuery(final String psiteId, final QueryBuilder pQueryBuilder) throws RepositoryException {

    String siteId = psiteId;

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.siteIdQuery() Method Entering");
    }

    if (StringUtils.isBlank(siteId)){
      return null;
    }
    final QueryExpression propertyExpression = pQueryBuilder.createPropertyQueryExpression(SITE);
    final QueryExpression rootItem = pQueryBuilder.createConstantQueryExpression(siteId);
    final Query includesQuery = pQueryBuilder.createIncludesQuery(propertyExpression, rootItem);

    if (isLoggingDebug()) {
      logDebug("LandingTemplateManager.siteIdQuery() Method Ending");
    }

    return includesQuery;
  }

}
