package com.bbb.importprocess.tools;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.importprocess.manager.BBBPIMProdCatalogImportManager;
import com.bbb.importprocess.vo.AttributeVO;
import com.bbb.importprocess.vo.BrandVO;
import com.bbb.importprocess.vo.CategoryVO;
import com.bbb.importprocess.vo.EcoSkuVO;
import com.bbb.importprocess.vo.ItemMediaVO;
import com.bbb.importprocess.vo.ItemVO;
import com.bbb.importprocess.vo.MediaVO;
import com.bbb.importprocess.vo.MiscVO;
import com.bbb.importprocess.vo.OperationVO;
import com.bbb.importprocess.vo.ProdSkuVO;
import com.bbb.importprocess.vo.ProductTabVO;
import com.bbb.importprocess.vo.ProductVO;
import com.bbb.importprocess.vo.RebateVO;
import com.bbb.importprocess.vo.SkuDisplayAttributesVO;
import com.bbb.importprocess.vo.SkuPricingVO;
import com.bbb.importprocess.vo.SkuVO;
import com.bbb.importprocess.vo.StofuImageParentVO;
import com.bbb.importprocess.vo.StofuImagesVO;
import com.bbb.importprocess.vo.ZipCodeVO;
import com.bbb.seo.SeoURLGenerator;
import com.bbb.utils.BBBStringUtils;
import com.bbb.utils.BBBUtility;

import atg.adapter.version.RepositoryVersionItem;
import atg.adapter.version.RepositoryWorkingVersionItem;
import atg.adapter.version.VersionRepository;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.QueryOptions;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;
import atg.repository.rql.RqlStatement;
import atg.versionmanager.Workspace;

public class BBBCatalogLoadTools extends BBBCatalogToolsHelper {





private static final String SALE = "sl";

  private static final String LIST = "lst";
  
  private static final String IN_CART = "ncrt";

  private static final String UPDATE_PROD_SKU_PROP = "updateProdSkuProperties";

  private static final String UPDATE_CLASS_PROP = "updateClassProperties";

  private static final String UPDATE_SUB_DEPT_PROP = "updateSubDeptProperties";

  private static final String UPDATE_DEPT = "updateDept";

  private static final String ASSOCIATE_PROD_WITH_SKU = "associateProductWithSku";

  private static final String UPDATE_PRICING_PROP = "updatePricingProperties";

  private static final String ASSOCIATE_SKU_ATT = "associateSkuAttributes";

  private static final String UPDATE_SKU_ATT_RELNS_PROP = "updateSkuAttributeRelnsProperties";

  private static final String UPDATE_PROD_PROD_RELATIONSHIP = "updateProdProdRelationship";

  private static final String ASSOCIATE_PRODUCTS_WITH_CATEGORY = "associateProductsWithCategory";

  private static final String ASSOCIATE_TABS_WITH_PROD = "associateTabsWithProduct";

  private static final String UPDATE_PRODUCT_TABS_PROP = "updateProductTabsProperties";

  private static final String UPDATE_OTHER_MEDIA_PROP = "updateOtherMediaProperties";

  private static final String UPDATE_REBATE_INFO = "updateRebateInfo";

  private static final String ASSOCIATE_REBATE_RELN_WITH_SKU = "associateRebateRelnWithSku";

  private static final String UPDATE_BRANDS_PROP = "updateBrandsProperties";

  private static final String ASSOCIATE_MEDIA_RELN_PROD = "associateMediaRelnWithProduct";

  private static final String ASSOCIATE_CAT_WITH_SUB_CAT = "associateCategoryWithSubCategory";

  private static final String CREATE_UPDATE_CATEGORY = "createUpdateCategory";
  
  private static final String UPDATE_ZIP_REGION_PROP = "updateZipCodeProperties";
  
  private static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION = "2003";
  
  private boolean mDirectToStaging = false;
  
  private static final String MEXICO_LIST_PRICE_LIST = "MX_ListPriceList";
  
  private static final String MEXICO_SALE_PRICE_LIST = "MX_SalePriceList";
  
  private static final String MEXICO_IN_CART_PRICE_LIST = "MexInCartPriceList";
  
  
  
  private static final String CAT_PRD_SEQ_CATEGORY_ID_PROPERTY  = "categoryId";
  
  private static final String CAT_PRD_SEQ_PRODUCT_ID_PROPERTY  = "childPrdId";
  
  private static final String CAT_PRD_SEQ_SEQUENCE_NUM  = "productSeqNum";

  private boolean populateOldCatProdSeqTable;
  
  private boolean useNewProductCategorySeq;
  
  private String newCatProdSeqRQL;
  
  private BBBPIMProdCatalogImportManager catalogImportManager;
  
  public BBBPIMProdCatalogImportManager getCatalogImportManager() {
	return catalogImportManager;
  }

  public void setCatalogImportManager(BBBPIMProdCatalogImportManager catalogImportManager) {
	this.catalogImportManager = catalogImportManager;
  }
 
  public String getNewCatProdSeqRQL() {
	return newCatProdSeqRQL;
  }

  public void setNewCatProdSeqRQL(String newCatProdSeqRQL) {
	this.newCatProdSeqRQL = newCatProdSeqRQL;
  }

  public boolean isUseNewProductCategorySeq() {
	return useNewProductCategorySeq;
  }

  public void setUseNewProductCategorySeq(boolean useNewProductCategorySeq) {
	this.useNewProductCategorySeq = useNewProductCategorySeq;
  }

  private ArrayList<MutableRepositoryItem> mProdChildSkuUpdateRequiredList = new ArrayList<MutableRepositoryItem>();


  private ArrayList<MutableRepositoryItem> mCategoryChildProdUpdateRequiredList = new ArrayList<MutableRepositoryItem>();

  public ArrayList<MutableRepositoryItem> getCategoryChildProdUpdateRequiredList() {
    return mCategoryChildProdUpdateRequiredList;
  }

  public void setCategoryChildProdUpdateRequiredList(ArrayList<MutableRepositoryItem> pCategoryChildProdUpdateRequiredList) {
	mCategoryChildProdUpdateRequiredList = pCategoryChildProdUpdateRequiredList;
  }

  public ArrayList<MutableRepositoryItem> getProdChildSkuUpdateRequiredList() {
	return mProdChildSkuUpdateRequiredList;
  }

  public void setProdChildSkuUpdateRequiredList(ArrayList<MutableRepositoryItem> pProdChildSkuUpdateRequiredList) {
    mProdChildSkuUpdateRequiredList = pProdChildSkuUpdateRequiredList;
  }

  
public boolean isPopulateOldCatProdSeqTable()
{
	return populateOldCatProdSeqTable;
}

public void setPopulateOldCatProdSeqTable(boolean populateOldCatProdSeqTable)
{
	this.populateOldCatProdSeqTable = populateOldCatProdSeqTable;
}

/**
 *   
 * @return mDirectToStaging
 */
public boolean isDirectToStaging() {
	return mDirectToStaging;
}

/**
 * 
 * @param pDirectToStaging
 */
public void setDirectToStaging(boolean pDirectToStaging) {
	mDirectToStaging = pDirectToStaging;
}


private boolean mUpdateLastModifiedDateOfChildItems=true;
  
/**
 * 
 * @return mUpdateLastModifiedDateOfChildItems
 */
public boolean isUpdateLastModifiedDateOfChildItems() {
	return mUpdateLastModifiedDateOfChildItems;
}

/**
 * 
 * @param pUpdateLastModifiedDateOfChildItems
 */
public void setUpdateLastModifiedDateOfChildItems(boolean pUpdateLastModifiedDateOfChildItems) {
	this.mUpdateLastModifiedDateOfChildItems = pUpdateLastModifiedDateOfChildItems;
}



private SeoURLGenerator seoLinkGenerator;
  
  /**
   * 
   */
    private BBBConfigTools configTools;
    
    /**
   * @return configTools
   */
  	public BBBConfigTools getConfigTools() {
  	  return this.configTools;
  	}

    /**
   * @param configTools
   */
  	public void setConfigTools(final BBBConfigTools configTools) {
  	  this.configTools = configTools;
  	}
  	
  
  /**
 * @return seoLinkGenerator
 */
public SeoURLGenerator getSeoLinkGenerator() {
    return this.seoLinkGenerator;
  }

  /**
 * @param seoLinkGenerator
 */
public void setSeoLinkGenerator(final SeoURLGenerator seoLinkGenerator) {
    this.seoLinkGenerator = seoLinkGenerator;
  }
  

/** Deployed Project Repository*/
	private MutableRepository mDeployedProjectRepository;

	/**
	 * Getter for pDeployedProjectRepository
	 * @return
	 */
	public MutableRepository getDeployedProjectRepository() {
		return mDeployedProjectRepository;
	}

	/**
	 * Setter for mDeployedProjectRepository
	 * @param pDeployedProjectRepository
	 */
	public void setDeployedProjectRepository(MutableRepository pDeployedProjectRepository) {
		this.mDeployedProjectRepository = pDeployedProjectRepository;
	}

  /**
   * 
   * @param pFolderId 
   * @return RepositoryItem
   * @throws RepositoryException
   * @throws TransactionDemarcationException
   * @throws Exception
   */
  public MutableRepositoryItem getParentFolder(final String pFolderId) throws RepositoryException {
    MutableRepositoryItem folderItem = null;

    final MutableRepository theRep = (MutableRepository)getProductCatalog();

    folderItem = (MutableRepositoryItem) theRep.getItem(pFolderId, BBBCatalogImportConstant.FOLDER);
    if (isLoggingDebug()) {
      logDebug("createFolder: Existing folder item: " + folderItem);
    }
    if (folderItem == null) {
      folderItem = theRep.createItem(pFolderId, BBBCatalogImportConstant.FOLDER);
      if (isLoggingDebug()) {
        logDebug("createFolder: folder item created: " + folderItem);
      }
      folderItem.setPropertyValue(BBBCatalogImportConstant.NAME, pFolderId);
      folderItem.setPropertyValue(BBBCatalogImportConstant.PATH, getPath());
      theRep.addItem(folderItem);
    } else {

      theRep.updateItem(folderItem);
    }

    return folderItem;
  }

  /**
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pCategoryId
   * @param pConnection
   * @param isProductionImport 
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  public void createUpdateCategory(final String pFeedId, final String pCategoryId,final Connection pConnection,final boolean isProductionImport)
      throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, CREATE_UPDATE_CATEGORY);

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [createUpdateCategory]");
      logDebug(" pFeedId[" + pFeedId + "] pCategoryId[" + pCategoryId + "] ");
    }
    MutableRepositoryItem categoryItem = null;

    final String categoryRepositoryId = pCategoryId;// getCategoryId(pSiteId, pCategoryId);
    
    if(StringUtils.isEmpty(pCategoryId)) {
      
      return;
    }  
    
    try {

     final CategoryVO categoryVO = getPimFeedTools().getPIMCategoryDetail(pCategoryId, pFeedId, pConnection);

      if (categoryVO == null) {

        if (isLoggingDebug()) {

          logDebug("CategoryVO Item is null");
        }
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODES", categoryRepositoryId, getDate(),
            "Category details not found in the staging", pConnection);
        return;
      }
      final String categoryName = categoryVO.getCategoryName();
      
      if(isLoggingDebug()){
         logDebug("   categoryName = " + categoryName);
      }
      
      
      if (StringUtils.isEmpty(categoryName)) {
        if (isLoggingDebug()) {

          logDebug("Category Name is null for CategoryId:" + pCategoryId);
        }
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODES", pCategoryId, getDate(),
            "Category Name is null for CategoryId:" + pCategoryId, pConnection);
        return;
      }

      categoryItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(categoryRepositoryId,
          BBBCatalogImportConstant.CATEGORY_DESC);

      if(isLoggingDebug()){
         logDebug("   categoryItem = "  + categoryItem);
      }
      
      final String operationFlag = categoryVO.getOperationFlag();
      if (!StringUtils.isEmpty(operationFlag) && operationFlag.trim().equals(BBBCatalogImportConstant.DELETE_CODE)) {
        if (categoryItem == null) {
          
          /**
           * This code is alternative solution of ATG OOTB Project Completed issue
           * Analysis - In case of Regular Staging Scheduler, when item has been deleted and project completed on staging. 
           * Deleted item will be a head version of publishing. And when regular production scheduler invoked, 
           * items has been reimported on production, item to be deleted will be a bad record and not part of project, because deleted 
           * item not found in publishing repository while importing on production.
           * 
           * Following code will verify for Production Schedulers and 
           * then it validate, is item present in Production data base. 
           * If yes, it will create dummy item and remove it in publishing and
           *     Removed item added in the project
           * If no, it will assume, PIM id as bad record and log it into the data base.    
           */
          if(isLoggingDebug()) {
            
            logDebug("OperationFlag is Delete, Category Item is not available");
          }

          final RepositoryItem productionCategoryItem = getProductionItem(pCategoryId, BBBCatalogImportConstant.CATEGORY_DESC);
          
          if(isProductionImport && productionCategoryItem != null) {
            
            categoryItem = ((MutableRepository)getProductCatalog()).createItem(pCategoryId, BBBCatalogImportConstant.CATEGORY_DESC);
            categoryItem.setPropertyValue(BBBCatalogImportConstant.DISPLAY_NAME, "&nbsp;");
            ((MutableRepository)getProductCatalog()).addItem(categoryItem);
            if(isLoggingDebug()) {
              
              logDebug("OperationFlag=Delete, Dummy Item Created successfully");
            }

            ((MutableRepository)getProductCatalog()).removeItem(pCategoryId, BBBCatalogImportConstant.CATEGORY_DESC);
            if(isLoggingDebug()) {
              
              logDebug("OperationFlag=Delete, Dummy Item Removed successfully");
            }
          } else {
            if(isLoggingDebug()) {
              
              logDebug("OperationFlag=Delete, Item not found, Log Bad Record");
            }
            final String catError = new StringBuffer().append("Category Item  " + categoryItem + " not found in Repository").toString();
            getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODES", catError, getDate(), "Category Item is Null",
              pConnection);
          }
        } else {
          
        	((MutableRepository)getProductCatalog()).removeItem(categoryRepositoryId, BBBCatalogImportConstant.CATEGORY_DESC);
          if(isLoggingDebug()) {
            
            logDebug("OperationFlag=Delete, Item Removed successfully");
          }

        }
        return;
      }

      if (categoryItem != null) {

    	  
    	  if(isLoggingDebug()){
    	     logDebug("   update existing category");
    	  }
    	  
        categoryItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(categoryRepositoryId,
            BBBCatalogImportConstant.CATEGORY_DESC);

        updateCategoryProperties(categoryItem, categoryVO);
		// START | BBBSL-2465
		if (!StringUtils.isEmpty(operationFlag) && operationFlag.trim().equals(BBBCatalogImportConstant.INSERT)) {
        	updateSeoTags(pFeedId, pCategoryId,null,null,categoryVO, false);
        }
		// END   | BBBSL-2465
         ((MutableRepository)getProductCatalog()).updateItem(categoryItem);

      } else {

        // Create a new category item in the versioned repository
    	  
    	  if(isLoggingDebug()){
    	     logDebug("   creating new category");
    	  }
    	  

        categoryItem = ((MutableRepository)getProductCatalog()).createItem(categoryRepositoryId, BBBCatalogImportConstant.CATEGORY_DESC);
        // Populate SEO Repository Properties for Category Creation
        
        if(isLoggingDebug()){
           logDebug("   categoryItem = "  + categoryItem);
        }
     

         /* updateSeoTags(pFeedId, pCategoryId, "cat_" + pCategoryId, "Category", "Category Creation", keyword, keyword,
              false, null, null);*/
        updateSeoTags(pFeedId, pCategoryId,null,null,categoryVO, false);

        // Populate Current Category Properties

        updateCategoryProperties(categoryItem, categoryVO);
        

        ((MutableRepository)getProductCatalog()).addItem(categoryItem);
      }
    } catch (RepositoryException rex) {
    	
    	
      if (isLoggingError()) {

        logError("Issue with categoryId= " + categoryRepositoryId);
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODES", categoryRepositoryId, getDate(), new StringBuilder().append("RepositoryException=").append(rex.getMessage()).toString(),
          pConnection);
      BBBPerformanceMonitor.cancel(CREATE_UPDATE_CATEGORY);
      isMonitorCanceled = true;
    } finally {
      if (!isMonitorCanceled)
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, CREATE_UPDATE_CATEGORY);
    }

  }

  /**
   * Populate Current Category Properties
   * 
   * @param pCategoryItem
   * @param categoryVO 
   * @param pResultSet
   * @param pSiteId
   * @throws RepositoryException 
   */
  public void updateCategoryProperties(final MutableRepositoryItem pCategoryItem, final CategoryVO categoryVO)
      throws RepositoryException {

    if (isLoggingDebug()) {
    logDebug("CatalogTools Method Name [updateCategoryProperties] : pCategoryItem[" + pCategoryItem + "] ");
    }
    // set categories here

    final String categoryName = categoryVO.getCategoryName();
    final String imageUrl = categoryVO.getImageUrl();
    final boolean isCollege = categoryVO.isIsCollege();
    final String shopGuideId = categoryVO.getShopGuideId();
    final boolean disable = categoryVO.isDisable();
    final String nodeType = categoryVO.getNodeType();

    final boolean phantomCategory = categoryVO.getPhantomCategory();
	String scene7URL = categoryVO.getScene7URL();
	int gsImageOrientation = categoryVO.getGSimageorientation();

    pCategoryItem.setPropertyValue(BBBCatalogImportConstant.DISPLAY_NAME, categoryName);
    pCategoryItem.setPropertyValue(BBBCatalogImportConstant.COLLEGE, Boolean.valueOf(isCollege));
    pCategoryItem.setPropertyValue(BBBCatalogImportConstant.SHOP_GUIDE, shopGuideId);
    pCategoryItem.setPropertyValue(BBBCatalogImportConstant.NODE_TYPE, nodeType);

    pCategoryItem.setPropertyValue(BBBCatalogImportConstant.CATDISABLE, Boolean.valueOf(!disable));
    pCategoryItem.setPropertyValue(BBBCatalogImportConstant.PHANTOM_CATEGORY, Boolean.valueOf(phantomCategory));

    // Added Property lastModifiedDate
    pCategoryItem.setPropertyValue(BBBCatalogImportConstant.LAST_MODIFIED_DATE, getLastModifiedDate());
    pCategoryItem.setPropertyValue(BBBCatalogImportConstant.SMALL_IMAGE, imageUrl);
    // added stofu specific properties
	//pCategoryItem.setPropertyValue(BBBCatalogImportConstant.SCENE7_URL,scene7URL);
    pCategoryItem.setPropertyValue(BBBCatalogImportConstant.GSIMAGE_ORIENTATION,gsImageOrientation);
  }

  /**
   * Associate Site with provided pRepositoryItem
   * 
   * @param pCategoryItem
   * @param pResultSet
   * @param pSiteId
   */

  @SuppressWarnings("unchecked")
  private void associateWithSiteItem(final MutableRepositoryItem pRepositoryItem, final String pProperty,
      final String pPIMSiteList) {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateWithSite] : pRepositoryItem[" + pRepositoryItem + "] pProperty[" + pProperty + "] pPIMSiteList[" + pPIMSiteList
          + "] ");
    }

    boolean isSiteRelExist;
    if (StringUtils.isEmpty(pPIMSiteList)) {

      pRepositoryItem.setPropertyValue(pProperty, null);
      return;
    }
    Set<MutableRepositoryItem> siteIds = null;
    final List<String> activeSites = getActiveSites();
    siteIds = (Set<MutableRepositoryItem>) pRepositoryItem.getPropertyValue(pProperty);
    for (String siteId : activeSites) {

      isSiteRelExist = validateRelationShip(siteId, pPIMSiteList);
      final String bbbsiteId = getPimSiteToBBBSiteMap().get(siteId);
      final MutableRepositoryItem siteItem = getSite(bbbsiteId);
      if (siteItem != null) {
        // If current siteId exist in the pPIMSiteList then add in the
        // repository site list else remove from the repository site list if
        // item
        // contains
        // in the list
        if (isSiteRelExist) {
          if (siteIds == null) {

            siteIds = new HashSet<MutableRepositoryItem>();
          }
          siteIds.add(siteItem);
        } else {
          if (siteIds != null && !siteIds.isEmpty() && siteIds.contains(siteItem)) {
            siteIds.remove(siteItem);
          }
        }
      }
    }
    if (siteIds != null && !siteIds.isEmpty()) {
      try {
        simplePropertyWrite(pProperty, siteIds, pRepositoryItem);

      } catch (RepositoryException e) {
        if (isLoggingError()) {

          logError(siteIds + " has following issue ");
          logError(BBBStringUtils.stack2string(e));
        }
       
      }
    }

  }

  private MutableRepositoryItem getSite(final String pSiteId) {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [getSite] : pSiteId[" + pSiteId + "]");
    }

    final MutableRepository siteRepostiory = (MutableRepository)getSiteRepository();
    MutableRepositoryItem siteItem = null;

    if(StringUtils.isEmpty(pSiteId)) {
      
      return null;
    }

    try {

      siteItem = (MutableRepositoryItem) siteRepostiory.getItem(pSiteId,
          BBBCatalogImportConstant.SITE_CONFIGURATION_DESC);
    } catch (RepositoryException e) {
      if (isLoggingError()) {

        logError(pSiteId + " has following issue ");
        logError(BBBStringUtils.stack2string(e));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(e));
      }

    }
    return siteItem;
  }

  /**
   * 
   * @param pSiteId
   * @param pResultSet
   * @return
   */
  private static boolean validateRelationShip(final String pSiteId, final String pSiteList) {

    if (pSiteList.contains(pSiteId)) {
      return true;
    }

    return false;
  }

 
  /**
   * Method associate category with subcategory
   * 
   * @param pSiteId
   * @param pCategoryId
   * @param pFeedId
   * @param pErrorList 
   * @param pConnection 
   * @param isProductionImport 
   * @param pBadCategorySubcategoryRelationShip
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  public void associateCategoryWithSubCategory(final String pFeedId, final String pCategoryId,final List<String> pErrorList,
      final Connection pConnection,final boolean isProductionImport) throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS,
        ASSOCIATE_CAT_WITH_SUB_CAT);
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateCategoryWithSubCategory] : pFeedId[" + pFeedId + "] " + " pCategoryId[" + pCategoryId + "] pErrorList[" + pErrorList + "] ");
    }

    final String categoryRepositoryId = pCategoryId;// getCategoryId(pSiteId,
    
    if(StringUtils.isEmpty(pCategoryId)) {
      
      return;
    }                                                // pCategoryId);
    final MutableRepository catalogRepository = (MutableRepository)getProductCatalog();

    final List<OperationVO> subCategoriesVOList = getPimFeedTools().getChildCategories(pFeedId, pCategoryId,
        pConnection);

    try {
      // getItem() - To find the category into the version repository
      MutableRepositoryItem categoryItem = (MutableRepositoryItem) catalogRepository.getItem(categoryRepositoryId,
          BBBCatalogImportConstant.CATEGORY_DESC);

      if (categoryItem == null) {

        if (isLoggingDebug()) {

          logDebug("Parent Category item not available into the version repository");
        }
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODE_PARENT", pCategoryId, getDate(),
            "Parent Category Item not available in the Repository ", pConnection);
        return;
      }

      @SuppressWarnings("unchecked")
      final List<RepositoryItem> childCategoryList = (List<RepositoryItem>) categoryItem
          .getPropertyValue(BBBCatalogImportConstant.FIXED_CHILD_CATEGORIES);

      for (OperationVO subCategoryVO : subCategoriesVOList) {
        
    	  final String subCategoryId = subCategoryVO.getId();
    	  final String subCategoryRepositoryId = subCategoryId;// getCategoryId(pSiteId,
        
        if (StringUtils.isEmpty(subCategoryId))  {
          
          continue; 
        }
        final String opcode = subCategoryVO.getOperationCode();
        
        final RepositoryItem subCategoryItem = ((MutableRepository)getProductCatalog()).getItem(subCategoryRepositoryId,
            BBBCatalogImportConstant.CATEGORY_DESC);

        if (subCategoryItem == null) {
          if (isLoggingDebug()) {

            logDebug("SubCategory not Available in the repository. Bad Relationship");
            logDebug("categoryRepositoryId=" + categoryRepositoryId + "-" + "SubCategoryRepositoryId="
                + subCategoryRepositoryId);
            logDebug("Operation Code=" + opcode);
          }
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODE_PARENT", pCategoryId + "-" + subCategoryId,
              getDate(),
              "Child Category Item not available in the Repository.Bad Relationship for Category SubCategory",
              pConnection);
          continue;
        }

        if (!StringUtils.isEmpty(opcode) && opcode.equals(BBBCatalogImportConstant.DELETE_CODE)) {
          
          // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
          if(isProductionImport && !childCategoryList.contains(subCategoryItem)) {
            
            continue;
          }  
          if (childCategoryList.contains(subCategoryItem)) {

            childCategoryList.remove(subCategoryItem);
          } else {
            
            if (isLoggingDebug()) {

              logDebug("SubCategory not associated with the Parent Category");
              logDebug("Unable to delete association");
            }
            pErrorList.add(subCategoryId);
            getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODE_PARENT", pCategoryId + "-" + subCategoryId,
                getDate(), "SubCategory not associated with the Parent Category. Unable to delete association",
                pConnection);
          }
          continue;
        } else {
          
          if (childCategoryList.contains(subCategoryItem)) {

            childCategoryList.remove(subCategoryItem);
          }
            
          childCategoryList.add(subCategoryItem);
          if (isLoggingDebug()) {

            logDebug("Successfully associate with Parent category");
          }
        }
      }
      // add child categories to current category
      // getItemForUpdate  get a write lock on the latest version.
      categoryItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(categoryRepositoryId,
          BBBCatalogImportConstant.CATEGORY_DESC);

      categoryItem.setPropertyValue(BBBCatalogImportConstant.FIXED_CHILD_CATEGORIES, childCategoryList);
      catalogRepository.updateItem(categoryItem);

    } catch (RepositoryException rex) {

      if (isLoggingError()) {
        
        logError(BBBStringUtils.stack2string(rex));
      }
   
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODE_PARENT", pCategoryId, getDate(), new StringBuilder().append("RepostioryException=").append(rex.getMessage()).toString(),
          pConnection);
      BBBPerformanceMonitor.cancel(ASSOCIATE_CAT_WITH_SUB_CAT);
      isMonitorCanceled = true;
    } finally {
      if (!isMonitorCanceled) {
        
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS,
            ASSOCIATE_CAT_WITH_SUB_CAT);
      }
    }

  }

  /**
   * Method Associate categories with Root Category
   * 
   * @param pSiteId
   * @param pCategoryId
   * @param pFeedId
   * @param pErrorList 
   * @param pConnection 
   * @param isProductionImport 
   * @param pBadCategorySubcategoryRelationShip
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  public void associateCategoriesWithRootCategory(final String pSiteId, final String pFeedId, final String pCategoryId,
      final List<String> pErrorList, final Connection pConnection, final boolean isProductionImport) throws BBBSystemException, BBBBusinessException {
    
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateCategoriesWithRootCategory]");
      logDebug("pFeedId[" + pFeedId + "]  pSiteId[" + pSiteId + "] pCategoryId[" + pCategoryId + "] pErrorList["
          + pErrorList + "] ");
    }

    final String categoryRepositoryId = pCategoryId;
    final List<OperationVO> subCategoriesVOList = getPimFeedTools().getUniqueRootCategories(pFeedId, pSiteId,
        pConnection);
    final String categoryError = new StringBuffer().append( "Category Item not found in the Repository").toString();
    final String subCatError = "SubRoot Category not available in the repostiory";

    if (subCategoriesVOList == null || subCategoriesVOList.isEmpty()) {

      if (isLoggingDebug()) {
        logDebug("No Root Categories found, return without processing");
      }
      return;
    }

    try {
      
      // getItem() - To find the category into the version repository
      if (StringUtils.isEmpty(categoryRepositoryId))  {
        
        return; 
      }

      MutableRepositoryItem categoryItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(categoryRepositoryId,
          BBBCatalogImportConstant.CATEGORY_DESC);

      if (categoryItem == null) {

        pErrorList.add(categoryError);
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODE", categoryRepositoryId, getDate(), categoryError,
            pConnection);
        return;
      }

      @SuppressWarnings("unchecked")
      final List<RepositoryItem> childCategoryList = (List<RepositoryItem>) categoryItem
          .getPropertyValue(BBBCatalogImportConstant.FIXED_CHILD_CATEGORIES);

      for (OperationVO subCategoryVO : subCategoriesVOList) {

    	  final String subRootCatId = subCategoryVO.getId();
    	  final String subCategoryRepositoryId = subRootCatId;// getCategoryId(pSiteId,
                                                            // subRootCatId);
    	  final String opcode = subCategoryVO.getOperationCode();
    	  final RepositoryItem subRootCategoryItem = ((MutableRepository)getProductCatalog()).getItem(
            subCategoryRepositoryId, BBBCatalogImportConstant.CATEGORY_DESC);

        if (isLoggingDebug()) {
          logDebug("processing for SubRoot Category=" + subCategoryRepositoryId);
          logDebug("Operation Code=" + opcode);
        }
        if (subRootCategoryItem == null) {

          if (isLoggingDebug()) {
            logDebug(subCatError);
          }
          pErrorList.add(subRootCatId);
          getPimFeedTools()
              .updateBadRecords(pFeedId, "", "ECP_NODE", subRootCatId, getDate(), subCatError, pConnection);
          continue;
        }

        if (!StringUtils.isEmpty(opcode) && opcode.equals(BBBCatalogImportConstant.DELETE_CODE)) {

          if (childCategoryList.contains(subRootCategoryItem)) {
            childCategoryList.remove(subRootCategoryItem);
            if (isLoggingDebug()) {

              logDebug("Successfully removed subRoot category.SubRoot Category already associate with the Root Category");
            }
          } else {
            // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
            if(isProductionImport) {
              
              continue;
            } 
            if (isLoggingDebug()) {

              logDebug("SubRoot Category not associated with the Root Category.Unable to remove subRoot category with Root Category");
            }
            pErrorList.add(subRootCatId);
            getPimFeedTools().updateBadRecords(
                pFeedId,
                "",
                "ECP_NODE",
                pCategoryId + BBBCatalogImportConstant.KEY_SEPERATOR + subRootCatId,
                getDate(),
                "Bad Relationship for Deletion.Category RootCategory not associated in the repository for siteId="
                    + pSiteId, pConnection);

          }
          continue;
        } else {
          if (!childCategoryList.contains(subRootCategoryItem)) {
            childCategoryList.add(subRootCategoryItem);
            if (isLoggingDebug()) {

              logDebug("SubRoot Category not associate with the Root Category");
              logDebug("Successfully added subRoot category");
            }
          } else {
            if (isLoggingDebug()) {

              logDebug("SubRoot Category already associate with the Root Category");
              }
          }

        }
      }
      // add child categories to current category
      // getItemForUpdate  get a write lock on the latest version.
      categoryItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(categoryRepositoryId,
          BBBCatalogImportConstant.CATEGORY_DESC);

      categoryItem.setPropertyValue(BBBCatalogImportConstant.FIXED_CHILD_CATEGORIES, childCategoryList);
      ((MutableRepository)getProductCatalog()).updateItem(categoryItem);
      if (isLoggingDebug()) {
        logDebug("End associateCategoriesWithRootCategory");
      }
    } catch (RepositoryException rex) {
      if (isLoggingError()) {
        logError(BBBStringUtils.stack2string(rex));
      }

      if (isLoggingDebug()) {
        logDebug(BBBStringUtils.stack2string(rex));
      }
      getPimFeedTools()
          .updateBadRecords(pFeedId, "", "ECP_NODE", pCategoryId, getDate(), new StringBuilder().append("ReposiotoryException=").append(rex.getMessage()).toString(), pConnection);

    }

  }

  /**
   * This method associate Media With Product
   * 
   * @param pSiteId
   * @param pFeedId
 * @param pProductId 
 * @param pErrorList 
 * @param pConnection 
 * @param isProductionImport 
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public void associateMediaRelnWithProduct(final String pFeedId, final String pProductId,
      final List<String> pErrorList, final Connection pConnection, final boolean isProductionImport) throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_MEDIA_RELN_PROD);
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateMediaRelnWithProduct]");
      logDebug("pFeedId[" + pFeedId + "]  pProductId[" + pProductId + "] pErrorList[" + pErrorList + "] ");
    }

    MutableRepositoryItem productItem = null;
    MutableRepositoryItem productMediaItem = null;
    if (isLoggingDebug()) {
      logDebug("Start associateMediaRelnWithProduct - Media association with productId =" + pProductId);
    }
    try {

      productItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pProductId,
          BBBCatalogImportConstant.PRODUCT_DESC);

      if (productItem == null) {
    	  final String productError = "Product not found in the repository";
        if (isLoggingDebug()) {

          logDebug(productError);
        }
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PROD_MEDIA", pProductId, getDate(), productError,
            pConnection);
        pErrorList.add(pProductId);
        return;
      }

      @SuppressWarnings("unchecked")
      List<MutableRepositoryItem> productRels = (List<MutableRepositoryItem>) productItem
          .getPropertyValue(BBBCatalogImportConstant.MEDIA_RELN);

      final List<OperationVO> mediaRelVOList = getPimFeedTools().getSkuMedia(pFeedId, pProductId, pConnection);

      if (mediaRelVOList == null || mediaRelVOList.isEmpty()) {
    	  final String error = new StringBuffer().append("Media relationship details not found in staging database").toString();
        if (isLoggingDebug()) {

          logDebug(error);
        }
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PROD_MEDIA", pProductId, getDate(), error, pConnection);
        pErrorList.add(pProductId);
        return;

      }
      if (productRels == null) {

        productRels = new ArrayList<MutableRepositoryItem>();
      }
      for (OperationVO mediaVO : mediaRelVOList) {

    	  final String mediaId = mediaVO.getId();
    	  final String opcode = mediaVO.getOperationCode();
    	  final String mediaRelationshipId = mediaId;

        if (isLoggingDebug()) {

          logDebug("MediaId=" + mediaId);
          logDebug("Operation Code=" + opcode);

        }

        if (!StringUtils.isEmpty(opcode) && opcode.equals(BBBCatalogImportConstant.DELETE_CODE)) {

          productMediaItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(mediaRelationshipId,
              BBBCatalogImportConstant.BBB_OTHER_MEDIA_DESC);
          
          // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
          if(isProductionImport && productMediaItem != null && !productRels.contains(productMediaItem)) {
            
            continue;
          }  
          if (productMediaItem != null && productRels.contains(productMediaItem)) {

            productRels.remove(productMediaItem);
            if (isLoggingDebug()) {

              logDebug("Removed relationship successfully");
            }
          } else {

        	  final String error = new StringBuffer().append("Bad Association Deletion- Media Id " + mediaId + " Association for product " + pProductId
                + "does not exist in the repository.Can't Delete").toString();
            if (isLoggingDebug()) {

              logDebug(error);
            }
            pErrorList.add(mediaId);
            getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PROD_MEDIA",
                pProductId + BBBCatalogImportConstant.KEY_SEPERATOR + mediaId, getDate(), error, pConnection);

          }
          continue;
        }

        productMediaItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(mediaRelationshipId,
            BBBCatalogImportConstant.BBB_OTHER_MEDIA_DESC);
        if (productMediaItem == null) {
        	final String error =new StringBuffer().append( "Bad Association- mediaId not found in the Repository, Association not done for the same").toString();
          if (isLoggingDebug()) {

            logDebug(error);
          }
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PROD_MEDIA",
              pProductId + BBBCatalogImportConstant.KEY_SEPERATOR + mediaId, getDate(), error, pConnection);
          pErrorList.add(mediaId);
          continue;
        }

        if (!productRels.contains(productMediaItem)) {
          
          productRels.add(productMediaItem);
          if (isLoggingDebug()) {

            logDebug("Association Success");

          }
        } else {

          if (isLoggingDebug()) {

            logDebug("Association already exist.");

          }
        }

      }
      if (productItem != null) {

        productItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(pProductId,
            BBBCatalogImportConstant.PRODUCT_DESC);
        simplePropertyWrite(BBBCatalogImportConstant.MEDIA_RELN, productRels, productItem);
        ((MutableRepository)getProductCatalog()).updateItem(productItem);
      }

    } catch (RepositoryException e) {
      if (isLoggingError()) {
        logError(BBBStringUtils.stack2string(e));
      }

      if (isLoggingDebug()) {
        logDebug(BBBStringUtils.stack2string(e));
      }
      final String error = "Repository Exception: Media association not done for productId " + pProductId;
      if (isLoggingDebug()) {

        logDebug(error);
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PROD_MEDIA", pProductId, getDate(), error, pConnection);
      pErrorList.add(pProductId);
      BBBPerformanceMonitor.cancel(ASSOCIATE_MEDIA_RELN_PROD);
      isMonitorCanceled = true;
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS,
            ASSOCIATE_MEDIA_RELN_PROD);
      }
    }

  }

  /**
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
 * @param pIsFrequent 
 * @param pIsRare 
 * @param pConnection 
   * @param isFrequent
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */

  public void updateItem(final String pFeedId, final String pItemId, final boolean pIsFrequent, final boolean pIsRare,
		  final Connection pConnection) throws BBBSystemException, BBBBusinessException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateItem]");
      logDebug("pFeedId[" + pFeedId + "]  pItemId[" + pItemId + "] pIsFrequent[" + pIsFrequent + "] pIsRare[" + pIsRare
          + "] ");
    }

    ItemVO itemVO = null;
    itemVO = getPimFeedTools().getPIMItemDetail(pFeedId, pItemId, pIsFrequent, pIsRare, pConnection);
    if (itemVO != null) {

			final String productTitle = itemVO.getProductTitle();
			final String caProductTitle = itemVO.getCAProductTitle();
			final String babProductTitle = itemVO.getBABProductTitle();
			//STOFU Change
			String gsProductTitle = itemVO.getGSProductTitle();
			String gsCaProductTitle = itemVO.getGSCAProductTitle();
			String gsBabProductTitle = itemVO.getGSBABProductTitle();
			// if titles for all sites are empty. Verify for WebOfferedFlag=true and disabledFlag=false
			if (StringUtils.isEmpty(productTitle)
					&& StringUtils.isEmpty(caProductTitle)
					&& StringUtils.isEmpty(babProductTitle)
					&& StringUtils.isEmpty(gsProductTitle)
					&& StringUtils.isEmpty(gsCaProductTitle)
					&& StringUtils.isEmpty(gsBabProductTitle)) {

				if ("Y".equalsIgnoreCase(itemVO.isWebOfferedFlag())
						&& "N".equalsIgnoreCase(itemVO.isDisableFlag())) {

					getPimFeedTools().updateBadRecords(pFeedId, "",
							"ECP_ITEM_FREQUENT", pItemId, getDate(),
							"Item Title is null for ItemId:" + pItemId,
							pConnection);
					return;

				}
			}

			if (StringUtils.isEmpty(productTitle)) {

				// if title not empty for all sites, means title is available
				// for any one of the site or item will not display on site
				// put &nbsp, as displayName is the mandatory field in the
				// repository
				itemVO.setProductTitle("&nbsp;");
			}

			if (isLoggingDebug()) {

        logDebug("Product Flag=" + itemVO.isProductFlag());
        logDebug("Collection Flag=" + itemVO.isCollectionFlag());
        logDebug("leadPrd Flag=" + itemVO.isLeadProduct());
      }
      // collection_flag = false && product_flag = false && Lead flag = false 
      // Item is sku

      if (itemVO.isProductFlag() || itemVO.isCollectionFlag() || itemVO.isLeadProduct()) {
    	  updateProductProperties(pFeedId, pItemId, pIsFrequent, pIsRare, itemVO, pConnection);
      } else {
    	  updateSkuProperties(pFeedId, pItemId, pIsFrequent, pIsRare, itemVO, pConnection);  
      }
    } else {
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEM", pItemId, getDate(),
          "itemVO is null, Could not get the Item Details", pConnection);

      if (isLoggingDebug()) {

        logDebug("ItemVO is null. Returning without processing");
      }
    }
  }

  /**
   * Updating Product Properties
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  private void updateProductProperties(final String pFeedId, final String pItemId, final boolean pIsFrequent,
      final boolean pIsRare, final ItemVO pItemVO, final Connection pConnection) throws BBBBusinessException,
      BBBSystemException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateProductProperties]");
      logDebug("pFeedId[" + pFeedId + "]  pItemId[" + pItemId + "] pIsFrequent[" + pIsFrequent + "] pIsRare[" + pIsRare
          + "] ");
    }
    MutableRepositoryItem productItem;
    if (isLoggingDebug()) {

      logDebug("In updateProductProperties(). Creating/Updating Product");
    }

    try {

      final boolean isCollection = pItemVO.isCollectionFlag();
      final boolean isLeadProduct = pItemVO.isLeadProduct();

      productItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pItemId, BBBCatalogImportConstant.PRODUCT_DESC);

      if (productItem != null) {
        if (isLoggingDebug()) {

          logDebug("Product exist in the repository.Updating properties");
        }
        productItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(pItemId,
            BBBCatalogImportConstant.PRODUCT_DESC);
        
        simplePropertyWrite(BBBCatalogImportConstant.COLLECTION, Boolean.valueOf(isCollection), productItem);
        simplePropertyWrite(BBBCatalogImportConstant.LEAD_PRD, Boolean.valueOf(isLeadProduct), productItem);
    
        if (pIsFrequent) {
        	if(isLoggingDebug()){
        	   logDebug("         Before call to updateFrequentProductProperties,  date=" + formatDate(getDate()));
        	}
        	updateFrequentProductProperties(pFeedId, productItem, pItemVO, pConnection);
        }
        if (pIsRare) {

          if(isLoggingDebug()){
        	logDebug("         Before call to updateRareProductProperties,  date=" + formatDate(getDate()));
           }
          updateRareProductProperties(pFeedId, productItem, pItemVO, pConnection);
        }
        
        /*Keep updating SEO Tags for products for DC backward compatibility*/ 
        if(isBackWardCompatibilityFlag()){
	        if(isLoggingDebug()){
	          logDebug("         Before call to updateSeoTags,  date=" + formatDate(getDate()));
	        }
	        updateSeoTags(pFeedId, pItemId,pItemVO,null,null, true);
        }
        // Added Property lastModifiedDate
        simplePropertyWrite(BBBCatalogImportConstant.LAST_MODIFIED_DATE, getLastModifiedDate(), productItem);
        simplePropertyWrite("seoUrl", getSeoUrl(pItemId,pItemVO.getProductTitle()), productItem);

        ((MutableRepository)getProductCatalog()).updateItem(productItem);

      } else {

        // Create a new Product item in the versioned repository

        productItem = ((MutableRepository)getProductCatalog()).createItem(pItemId, BBBCatalogImportConstant.PRODUCT_DESC);

        simplePropertyWrite(BBBCatalogImportConstant.COLLECTION, Boolean.valueOf(isCollection), productItem);
        simplePropertyWrite(BBBCatalogImportConstant.LEAD_PRD, Boolean.valueOf(isLeadProduct), productItem);
        // Display property is a mandatory property of the product,
        // here default value will be provided to the display name which will be
        // updated in updateFrequentProductProperties
        // This is done to avoid exception
        simplePropertyWrite(BBBCatalogImportConstant.DISPLAY_NAME_DEFAULT, pItemId, productItem);

        // set product properties here if (pIsFrequent) {
        if (pIsFrequent) {

          updateFrequentProductProperties(pFeedId, productItem, pItemVO, pConnection);
        }
        if (pIsRare) {
          updateRareProductProperties(pFeedId, productItem, pItemVO, pConnection);
        }
        // Added Property lastModifiedDate
        simplePropertyWrite(BBBCatalogImportConstant.LAST_MODIFIED_DATE, getLastModifiedDate(), productItem);
        // populate SEO Repository and its translation attributes
      

          /*updateSeoTags(pFeedId, pItemId, "prod_" + pItemId, "Product", "Product Creation", keywords, keywords, true,
              babTitle, caTitle);*/
        	
          //updateSeoTags(pFeedId, pItemId,pItemVO,null,true);
		  
		  // R2.2 92-A story. Update catalog table with product keywords from PIM table.
				updateProductKeywords(productItem, pItemVO);
     
        simplePropertyWrite("seoUrl", getSeoUrl(pItemId,pItemVO.getProductTitle()), productItem);
        ((MutableRepository)getProductCatalog()).addItem(productItem);
      }
      if(productItem!=null && !isCollection){
    	  
    	  if(isUpdateLastModifiedDateOfChildItems())
    	  {
    		  getProdChildSkuUpdateRequiredList().add(productItem);
    	  }
      }
    } catch (RepositoryException e) {
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEM", pItemId, getDate(),
          "Repository Exception: Item is null", pConnection);

    }

  }
	
	/*
	 * This method is used to update the product keywords in the catalog table
	 * from the PIM schema only if the the keywords are not empty or null in 
	 * the PIM tables. R2.2 92-A story.
	 */
	private void updateProductKeywords(MutableRepositoryItem productItem,
			ItemVO pItemVO) throws RepositoryException {

		if(pItemVO != null && pItemVO.getProductVO() != null){
			 if(!BBBUtility.isEmpty(pItemVO.getProductVO().getKeywords())){
				simplePropertyWrite(BBBCatalogImportConstant.PRD_KEYWORDS, pItemVO.getProductVO().getKeywords(), productItem);
			 }
		}
	}

	/**
	 * @param pFeedId  
	 * @param pBrandVO TODO
	 */
	private void updateSeoTags(final String pFeedId, final String pSeoTagId, final ItemVO pProductItemVO, BrandVO pBrandVO,final CategoryVO pCatVO, final boolean sites)
			throws BBBSystemException, BBBBusinessException {
		MutableRepositoryItem seoItem;

		try {
			seoItem = (MutableRepositoryItem) getSeoRepository().getItem(pSeoTagId, BBBCatalogImportConstant.DAS_SEO_TAG);
			if (seoItem == null) {
				
				seoItem = ((MutableRepository)getSeoRepository()).createItem(pSeoTagId, BBBCatalogImportConstant.DAS_SEO_TAG);
				if (pProductItemVO != null) {
					updateSeoProperties(pProductItemVO.getProductTitle(), pProductItemVO.getProductTitle(), pProductItemVO.getShortDescription(),
							pProductItemVO.getProductVO().getKeywords(), seoItem);
				}
				if (pCatVO != null) {
					updateSeoProperties(pCatVO.getCategoryName(), pCatVO.getCategoryName(), pCatVO.getCategoryName(), pCatVO.getKeywords(), seoItem);
				}
				if(pBrandVO != null){
					if (isLoggingDebug()) {
						logDebug("CatalogTools Method Name [updateSeoTags]");
						logDebug(" pSeoTagId[" + pSeoTagId + "] pDisplayName[" + pBrandVO.getName() + "] pTitle[" + pBrandVO.getName()
								+ "] pDescription[" + pBrandVO.getName() + "] pKeywords[" + pBrandVO.getName() + "]");
						}
					updateSeoProperties(pBrandVO.getName(), pBrandVO.getName(), pBrandVO.getName(), pBrandVO.getName(), seoItem);
					}
				if(pBrandVO != null){
					if (isLoggingDebug()) {
						logDebug("CatalogTools Method Name [updateSeoTags]");
						logDebug(" pSeoTagId[" + pSeoTagId + "] pDisplayName[" + pBrandVO.getName() + "] pTitle[" + pBrandVO.getName()
								+ "] pDescription[" + pBrandVO.getName() + "] pKeywords[" + pBrandVO.getName() + "]");
					}
					updateSeoProperties(pBrandVO.getName(), pBrandVO.getName(), pBrandVO.getName(), pBrandVO.getName(), seoItem);
				}
				if (sites && pProductItemVO.getBABProductTitle() != null && !StringUtils.isEmpty(pProductItemVO.getBABProductTitle())) {
					if (isLoggingDebug()) {
						logDebug("CatalogTools Method Name [updateSeoTags]");
						logDebug(" pSeoTagId[" + pSeoTagId + "] pDisplayName[" + pProductItemVO.getBABShortDescription() + "] pTitle[" + pProductItemVO.getBABProductTitle()
								+ "] pDescription[" + pProductItemVO.getBABWebDescrip() + "] pKeywords[" + pProductItemVO.getProductVO().getKeywords() + "]");
					}
					updateSeoSites(BBBCatalogImportConstant.BAB_SITE_ID, seoItem);
					updateSeoSiteTranslation(seoItem, BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItemVO.getBABProductTitle(),
							pProductItemVO.getBABShortDescription(), pProductItemVO.getProductVO().getKeywords());

				}
				if (sites && pProductItemVO.getCAProductTitle() != null && !StringUtils.isEmpty(pProductItemVO.getCAProductTitle())) {
					if (isLoggingDebug()) {
						logDebug("CatalogTools Method Name [updateSeoTags]");
						logDebug(" pSeoTagId[" + pSeoTagId + "] pDisplayName[" + pProductItemVO.getCAShortDescription() + "] pTitle[" + pProductItemVO.getCAProductTitle()
								+ "] pDescription[" + pProductItemVO.getCAWebDescrip() + "] pKeywords[" + pProductItemVO.getProductVO().getKeywords() + "]");
					}
					updateSeoSites(BBBCatalogImportConstant.CA_SITE_ID, seoItem);

					updateSeoSiteTranslation(seoItem, BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItemVO.getCAProductTitle(),
							pProductItemVO.getCAShortDescription(), pProductItemVO.getProductVO().getKeywords());
				}

				((MutableRepository)getSeoRepository()).addItem(seoItem);
			}
		} catch (RepositoryException re) {

			if (isLoggingError()) {

				logError(BBBStringUtils.stack2string(re));
				logError("Not able to update seo tags for pSeoTagId=" + pSeoTagId);
			}
			if (isLoggingDebug()) {

				logDebug(BBBStringUtils.stack2string(re));
			}

		}

	}

	private String getSeoUrl(final String prodId, String displayName) {
		//  String url = "/product/Amy-Butler-Bath-Red-Towels/prod60011";
     String url= null;
     String name = displayName;
     if (isLoggingDebug()) {
       logDebug("displyName :" + name );
     }
     name = BBBStringUtils.formattedDisplayName(name);
     if (isLoggingDebug()) {
         logDebug("Formatted displyName :" + name );
       }
     url= getSeoLinkGenerator().formatUrl(prodId, name);
     
     return url;
   
   }

  /**
 * @param pConnection  
 */
private void updateRareProductProperties(final String pFeedId, final MutableRepositoryItem pProductItem,
      final ItemVO pItemVO, final Connection pConnection) throws RepositoryException,BBBSystemException, BBBBusinessException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateRareProductProperties]");
      logDebug("pFeedId[" + pFeedId + "]  pProductItem[" + pProductItem + "]");
    }
    final ProductVO productVO = pItemVO.getProductVO();

    final String webOnly = pItemVO.isWebOnlyFlag();
    final String emailOutOfStock = pItemVO.isEmailOutOfStockFlag();
    final String collegeSku = pItemVO.isSkuCollegeId();
    final String isEmailOutOfStockFlag = pItemVO.isEmailOutOfStockFlag();
    final String isSwatch = productVO.isSwatchFlag();
    final Boolean giftCertProd = productVO.getGiftCertProduct();

    final Date enableDate = pItemVO.getEnableDate();   
    final Date babEnableDate = pItemVO.getBABEnableDate();
    final Date caEnableDate = pItemVO.getCAEnableDate();
    String vendorId = productVO.getVendorId();
    
    //LTL-34(PIM feed processing changes for Products)
    final String ltlFlag = productVO.getLtlFlag();
    if (!StringUtils.isEmpty(ltlFlag)) {
        simplePropertyWrite(BBBCatalogImportConstant.LTL_FLAG, Boolean.valueOf(getBoolean(ltlFlag)), pProductItem);
      }else{
  		simplePropertyWrite(BBBCatalogImportConstant.LTL_FLAG, Boolean.valueOf(BBBCoreConstants.RETURN_FALSE), pProductItem);
  	}
    
    if (enableDate !=null) {
    simplePropertyWrite(BBBCatalogImportConstant.ENABLE_DATE_DEFAULT, enableDate, pProductItem);    
    }   
    
	/** Added Site Specific Enable Date for R2.1 #53 Endeca & PIM FEED*/
    if (babEnableDate != null && enableDate!=null && !enableDate.equals(babEnableDate) ) {
    	addProdTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.ENABLE_DATE, null, 0.0, null, null,getDate(babEnableDate));      
    }
    
    if ( caEnableDate != null && enableDate!=null && !enableDate.equals(caEnableDate) ) {
    	addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.ENABLE_DATE, null, 0.0, null, null,getDate(caEnableDate));     
    }    
    
    if (!StringUtils.isEmpty(webOnly)) {
      simplePropertyWrite(BBBCatalogImportConstant.WEB_ONLY_DEFAULT, Boolean.valueOf(getBoolean(webOnly)), pProductItem);
    }

    if (!StringUtils.isEmpty(isEmailOutOfStockFlag)) {
      simplePropertyWrite(BBBCatalogImportConstant.EMAIL_OUT_OF_STOCK, Boolean.valueOf(getBoolean(emailOutOfStock)), pProductItem);
    }
    if (!StringUtils.isEmpty(collegeSku)) {
      simplePropertyWrite(BBBCatalogImportConstant.COLLEGE, collegeSku, pProductItem);
    }
    if (!StringUtils.isEmpty(isSwatch)) {

      simplePropertyWrite(BBBCatalogImportConstant.SWATCH, Boolean.valueOf(getBoolean(isSwatch)), pProductItem);
    }

		simplePropertyWrite(BBBCatalogImportConstant.GIFT_CERT_PRODUCT,
				giftCertProd, pProductItem);
		simplePropertyWrite(BBBCatalogImportConstant.ENABLE_DATE_DEFAULT, enableDate,
				pProductItem);
		/*simplePropertyWrite(BBBCatalogImportConstant.QUANTITY_TOOL_TIP,
				quantityToolTip, pProductItem);*/

    final String rollUpTypeId = productVO.getRollupTypeProd();
    final MutableRepositoryItem rollyTypeItem = getItem(rollUpTypeId, BBBCatalogImportConstant.BBBROLLUPTYPE_DESC);

    if (rollyTypeItem != null) {

      simplePropertyWrite(BBBCatalogImportConstant.PROD_ROLLUP_TYPE, rollyTypeItem, pProductItem);
    }

    final String brandId = productVO.getBrandId();
    final MutableRepositoryItem brandItem = getItem(brandId, BBBCatalogImportConstant.BBB_BRAND);


			simplePropertyWrite(BBBCatalogImportConstant.BBB_BRAND, brandItem,
					pProductItem);
		
		//Start ever living pdp properties
		updateEverLivingPDPProperties(pProductItem, pItemVO);
		//End ever living pdp properties
    // -----------Update media properties---------------//

      final String folderId = getFolderId();
    updateItemMediaProperties(pProductItem, pItemVO, folderId);
 /*   String collectionThumbnailUrl = pItemVO.getCollectionThumbnail();
    MutableRepositoryItem collectionThumbnailMediaItem = createUpdateMediaItem(pProductItem.getRepositoryId(),
        BBBCatalogImportConstant.COLLECTION, collectionThumbnailUrl, getFolderId());

    if (collectionThumbnailMediaItem != null) {

      simplePropertyWrite(BBBCatalogImportConstant.COLLECTION_THUMBNAIL, collectionThumbnailMediaItem, pProductItem);
    }*/
    // Newly added media properties

  /*  String imgHorzLoc = pItemVO.getImgHorzLoc();
   MutableRepositoryItem imgHorzLocItem = createUpdateMediaItem(pProductItem.getRepositoryId(),
        BBBCatalogImportConstant.IMG_HORZ_LOC, imgHorzLoc, getFolderId());

    if (imgHorzLoc != null) {

      simplePropertyWrite(BBBCatalogImportConstant.IMG_HORZ_LOC, imgHorzLoc, pProductItem);
    }
    String imgVertLoc = pItemVO.getImgVertLoc();
 final MutableRepositoryItem imgVertLocItem = createUpdateMediaItem(pProductItem.getRepositoryId(),
        BBBCatalogImportConstant.IMG_VERT_LOC, imgVertLoc, getFolderId());

   if (imgVertLoc != null) {

      simplePropertyWrite(BBBCatalogImportConstant.IMG_VERT_LOC, imgVertLoc, pProductItem);
    }*/

    // end newly added properties

    final String babWebOnly = pItemVO.getBabWebOnlyFlag();
    final String caWebOnly = pItemVO.getCAWebOnlyFlag();

     if (!StringUtils.isEmpty(babWebOnly)) {
    	addProdTranslationBooleanAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
    	          BBBCatalogImportConstant.WEB_ONLY, null, 0.0, null, babWebOnly,null);
   /*   addProdTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.WEB_ONLY, null, 0.0, null, babWebOnly);*/
    }

    if (!StringUtils.isEmpty(caWebOnly)) {
    	addProdTranslationBooleanAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
    	          BBBCatalogImportConstant.WEB_ONLY, null, 0.0, null, caWebOnly,null);
     /* addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.WEB_ONLY, null, 0.0, null, caWebOnly);*/
    }
    
    if(!StringUtils.isEmpty(vendorId)){    
    	simplePropertyWrite(BBBCatalogImportConstant.VENDOR_ID, vendorId, pProductItem);   	
    }
    if (isLoggingDebug()) {

      logDebug("End of updateRareProductProperties()");
    }
  }

  private void updateItemMediaProperties(final MutableRepositoryItem pItem, final ItemVO pItemVO, final String pFolderId)
      throws RepositoryException {

		if (isLoggingDebug()) {
			logDebug("CatalogTools Method Name [updateItemMediaProperties]");
			logDebug("pFolderId[" + pFolderId + "]  pItem[" + pItem + "]");
		}
		String mediaURLLoc = null;
		// add property in pItemVO for mediaURLLoc 
		if(pItemVO.getImgSmallLoc() != null) {
			if (pItemVO.getImgSmallLoc().indexOf("?") > 0) {
				mediaURLLoc = pItemVO.getImgSmallLoc().substring(0, pItemVO.getImgSmallLoc().indexOf("?"));
			} else {
				mediaURLLoc = pItemVO.getImgSmallLoc();
			}
		}else if(pItemVO.getImgLargeLoc() != null) {
			if (pItemVO.getImgLargeLoc().indexOf("?") > 0) {
				mediaURLLoc = pItemVO.getImgLargeLoc().substring(0, pItemVO.getImgLargeLoc().indexOf("?"));
			} else {
				mediaURLLoc = pItemVO.getImgLargeLoc();
			}
		}
		else if (pItemVO.getImgMedLoc() != null) {
			if (pItemVO.getImgMedLoc().indexOf("?") > 0) {
				mediaURLLoc = pItemVO.getImgMedLoc().substring(0, pItemVO.getImgMedLoc().indexOf("?"));
			} else {
				mediaURLLoc = pItemVO.getImgMedLoc();
			}
		}
		else if (pItemVO.getCollectionThumbnail() != null) {
			if (pItemVO.getCollectionThumbnail().indexOf("?") > 0) {
				mediaURLLoc = pItemVO.getCollectionThumbnail().substring(0, pItemVO.getCollectionThumbnail().indexOf("?"));
			} else {
				mediaURLLoc = pItemVO.getCollectionThumbnail();
			}
		}else if (pItemVO.getImgHorzLoc() != null) {
			if (pItemVO.getImgHorzLoc().indexOf("?") > 0) {
				mediaURLLoc = pItemVO.getImgHorzLoc().substring(0, pItemVO.getImgHorzLoc().indexOf("?"));
			} else {
				mediaURLLoc = pItemVO.getImgHorzLoc();
			}
		}
		else if (pItemVO.getImgVertLoc() != null) {
			if (pItemVO.getImgVertLoc().indexOf("?") > 0) {
				mediaURLLoc = pItemVO.getImgVertLoc().substring(0, pItemVO.getImgVertLoc().indexOf("?"));
			} else {
				mediaURLLoc = pItemVO.getImgVertLoc();
			}
		}
		if (mediaURLLoc != null) {
			simplePropertyWrite(BBBCatalogImportConstant.SCENE7_URL, mediaURLLoc, pItem);
		}
		final String swatchImageUrl = pItemVO.getImgSwatchLoc();
   /* MutableRepositoryItem swatchImageMediaItem = createUpdateMediaItem(pItem.getRepositoryId(),
        BBBCatalogImportConstant.SWATCH_IMAGE, swatchImageUrl, pFolderId);*/

    if (swatchImageUrl != null) {

      simplePropertyWrite(BBBCatalogImportConstant.SWATCH_IMAGE, swatchImageUrl, pItem);
    }

    final String zoomImageUrl = pItemVO.getImgZoomLoc();
 /*   MutableRepositoryItem zoomImageMediaItem = createUpdateMediaItem(pItem.getRepositoryId(),
        BBBCatalogImportConstant.ZOOM_IMAGE, zoomImageUrl, pFolderId);*/

    if (zoomImageUrl != null) {

      simplePropertyWrite(BBBCatalogImportConstant.ZOOM_IMAGE, zoomImageUrl, pItem);
    }

    if (!StringUtils.isEmpty(pItemVO.getImgZoomIndex())) {

      simplePropertyWrite(BBBCatalogImportConstant.ZOOM_INDEX, Integer.valueOf(getInt(pItemVO.getImgZoomIndex())), pItem);
    }
    if (!StringUtils.isEmpty(pItemVO.getImgZoomIndex())) {

      simplePropertyWrite(BBBCatalogImportConstant.ANYWHERE_ZOOM, Boolean.valueOf(getBoolean(pItemVO.getAnyWhereZoom())), pItem);
    }

  }

  private void updateFrequentProductProperties(final String pFeedId, final MutableRepositoryItem pProductItem,
      final ItemVO pItemVO, final Connection pConnection) throws RepositoryException, BBBSystemException, BBBBusinessException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateFrequentProductProperties]");
      logDebug("pFeedId[" + pFeedId + "]  pProductItem[" + pProductItem + "]");
    }
    final ProductVO productVO = pItemVO.getProductVO();
    final String displayName = pItemVO.getProductTitle();

    if (productVO == null) {

      if (isLoggingDebug()) {

        logDebug("Product Details not found for product id=" + pProductItem.getRepositoryId());
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEMS_FREQUENT", pProductItem.getRepositoryId(), getDate(),
          "Product Details not found,productVO is null", pConnection);
      return;
    }

		final String webOfferedFlag = pItemVO.isWebOfferedFlag();
		final String prodDisable = pItemVO.isDisableFlag();
		final String showImageCollection = productVO.getShowImagesCollection();
		final String intlRestricted = productVO.getIntlRestricted();
		
		//Calling Stofu property update method
        updateStofuFrequentProductProperties(pProductItem, pItemVO);
		
		
		
		// Frequent
		if (StringUtils.isEmpty(displayName)) {

      if (isLoggingDebug()) {

        logDebug("Display Name is Mandatory Field and it cannot be null");
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEMS_FREQUENT", pProductItem.getRepositoryId(), getDate(),
          "Product Display Name is Null", pConnection);
      return;
    }

    simplePropertyWrite(BBBCatalogImportConstant.DISPLAY_NAME_DEFAULT, displayName, pProductItem);
    simplePropertyWrite(BBBCatalogImportConstant.DESCRIPTION_DEFAULT, pItemVO.getShortDescription(), pProductItem);
    simplePropertyWrite(BBBCatalogImportConstant.LONG_DESCRIPTION_DEFAULT, pItemVO.getWebDescrip(), pProductItem);
    simplePropertyWrite(BBBCatalogImportConstant.DESIGNATED_PRODUCT_ID, pItemVO.getDesignatedProductId(), pProductItem);
    if (!StringUtils.isEmpty(webOfferedFlag)) {

      simplePropertyWrite(BBBCatalogImportConstant.WEB_OFFERED_DEFAULT, Boolean.valueOf(getBoolean(webOfferedFlag)), pProductItem);
    }
    simplePropertyWrite(BBBCatalogImportConstant.PRICE_RANGE_DESCRIP_DEFAULT, productVO.getPriceRangeDescrip(),
        pProductItem);
    simplePropertyWrite(BBBCatalogImportConstant.SKU_LOW_PRICE_DEFAULT, productVO.getSkuLowPrice(), pProductItem);

    simplePropertyWrite(BBBCatalogImportConstant.SKU_HIGH_PRICE_DEFAULT, productVO.getSkuHightPrice(), pProductItem);
    if (!StringUtils.isEmpty(prodDisable)) {

      simplePropertyWrite(BBBCatalogImportConstant.PROD_DISABLE_DEFAULT, Boolean.valueOf(getBoolean(prodDisable)), pProductItem);
    }
    // simplePropertyWrite("prodRollupType", productVO.getRollupTypeProd(),
    // pProductItem);
    simplePropertyWrite(BBBCatalogImportConstant.SHOP_GUIDE, productVO.getShopGuideId(), pProductItem);
    if (!StringUtils.isEmpty(showImageCollection)) {

      simplePropertyWrite(BBBCatalogImportConstant.SHOW_IMAGES_IN_COLLECTION, Boolean.valueOf(getBoolean(showImageCollection)),
          pProductItem);
    }
    
    if (!StringUtils.isEmpty(intlRestricted)) {
    	 simplePropertyWrite(BBBCatalogImportConstant.INTL_RESTRICTED, intlRestricted, pProductItem);
    	
    } 

    // simplePropertyWrite("likeUnlike", productVO.is.getShopGuideId(),
    // pProductItem);

    final String babWebOfferedFlag = pItemVO.isBABWebOfferedFlag();
    final String caWebOfferedFlag = pItemVO.isCAWebOfferedFlag();
    final String babProdDisabled = pItemVO.isBABDisableFlag();
    final String caProdDisabled = pItemVO.isCADisableFlag();
    final String babTitle = pItemVO.getBABProductTitle();
    final String caTitle = pItemVO.getCAProductTitle();
    final String babDescription = pItemVO.getBABShortDescription();
    final String caDescription = pItemVO.getCAShortDescription();
    final String babLongDescription = pItemVO.getBABWebDescrip();
    final String caLongDescription = pItemVO.getCAWebDescrip();
    final String caLowPrice = pItemVO.getProductVO().getCASkuLowPrice();
    final String caHighPrice = pItemVO.getProductVO().getCASkuHightPrice();
    final String caPriceRangeDescrip = pItemVO.getProductVO().getCAPriceRangeDescrip();
    if (!StringUtils.isEmpty(babWebOfferedFlag)) {
    	addProdTranslationBooleanAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.WEB_OFFERED, null, 0.0, null, babWebOfferedFlag,null);
      /*addProdTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.WEB_OFFERED, null, 0.0, null, babWebOfferedFlag);*/
    }

    if (!StringUtils.isEmpty(caWebOfferedFlag)) {
    	addProdTranslationBooleanAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.WEB_OFFERED, null, 0.0, null, caWebOfferedFlag,null);
     /* addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.WEB_OFFERED, null, 0.0, null, caWebOfferedFlag);*/
    }

    if (!StringUtils.isEmpty(babProdDisabled)) {
    	addProdTranslationBooleanAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.PROD_DISABLE, null, 0.0, null, babProdDisabled,null);
     /* addProdTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.PROD_DISABLE, null, 0.0, null, babProdDisabled);*/
    }
    if (!StringUtils.isEmpty(caProdDisabled)) {
    	addProdTranslationBooleanAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.PROD_DISABLE, null, 0.0, null, caProdDisabled,null);
     /* addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.PROD_DISABLE, null, 0.0, null, caProdDisabled);*/
    }
    if (!StringUtils.isEmpty(babTitle)) {

      addProdTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.DISPLAY_NAME, babTitle, 0.0, null, null,null);
    }
    if (!StringUtils.isEmpty(caTitle)) {

      addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.DISPLAY_NAME, caTitle, 0.0, null, null,null);
    }
    if (!StringUtils.isEmpty(babDescription)) {

      addProdTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.DESCRIPTION, babDescription, 0.0, null, null,null);
    }
    if (!StringUtils.isEmpty(caDescription)) {

      addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.DESCRIPTION, caDescription, 0.0, null, null,null);
    }
    if (!StringUtils.isEmpty(babLongDescription)) {

      addProdTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.LONG_DESCRIPTION, null, 0.0, babLongDescription, null,null);
    }
    if (!StringUtils.isEmpty(caLongDescription)) {

      addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.LONG_DESCRIPTION, null, 0.0, caLongDescription, null,null);
    }

    if (!StringUtils.isEmpty(caLowPrice)) {

      addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.SKU_LOW_PRICE, caLowPrice, 0.0, null, null,null);
    }else{
    	
    	deleteProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
    	          BBBCatalogImportConstant.SKU_LOW_PRICE, caLowPrice, 0.0, null, null);
    }

    if (!StringUtils.isEmpty(caHighPrice)) {

      addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.SKU_HIGH_PRICE, caHighPrice, 0.0, null, null,null);
    }else{
    	
    	deleteProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
    	          BBBCatalogImportConstant.SKU_HIGH_PRICE, caHighPrice, 0.0, null, null);
    }

    if (!StringUtils.isEmpty(caPriceRangeDescrip)) {

      addProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
          BBBCatalogImportConstant.PRICE_RANGE_DESCRIP, caPriceRangeDescrip, 0.0, null, null,null);
    }  else{
  	
    	deleteProdTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pProductItem,
    	          BBBCatalogImportConstant.PRICE_RANGE_DESCRIP, caPriceRangeDescrip, 0.0, null, null);
    }
    
    //STOFU Properties
  	String gsTitle = pItemVO.getGSProductTitle();
  	String gsBabTitle = pItemVO.getGSBABProductTitle();
  	String gsCaTitle = pItemVO.getGSCAProductTitle();
  	
  	if (!StringUtils.isEmpty(gsTitle)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_BBB_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.DISPLAY_NAME, gsTitle, 0.0, null,
  					null, null);
  	}
  	
  	if (!StringUtils.isEmpty(gsBabTitle)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_BAB_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.DISPLAY_NAME, gsBabTitle, 0.0, null,
  					null, null);
  	}
  		
  	if (!StringUtils.isEmpty(gsCaTitle)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_CA_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.DISPLAY_NAME, gsCaTitle, 0.0, null,
  					null, null);
  	}
  		
  	String gsDescription = pItemVO.getGSShortDescription();
  	String gsBabDescription = pItemVO.getGSBABShortDescription();
  	String gsCaDescription = pItemVO.getGSCAShortDescription();
  		
  	if (!StringUtils.isEmpty(gsDescription)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_BBB_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.DESCRIPTION, gsDescription, 0.0,
  					null, null, null);
  	}
  	if (!StringUtils.isEmpty(gsBabDescription)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_BAB_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.DESCRIPTION, gsBabDescription, 0.0,
  					null, null, null);
  	}
  	if (!StringUtils.isEmpty(gsCaDescription)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_CA_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.DESCRIPTION, gsCaDescription, 0.0,
  					null, null, null);
  	}
  		
  	String gsLongDescription = pItemVO.getGSWebDescrip();
  	String gsBabLongDescription = pItemVO.getGSBABWebDescrip();
  	String gsCaLongDescription = pItemVO.getGSCAWebDescrip();
  		
  	if (!StringUtils.isEmpty(gsLongDescription)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_BBB_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.LONG_DESCRIPTION, null, 0.0,
  					gsLongDescription, null, null);
  	}
  	if (!StringUtils.isEmpty(gsBabLongDescription)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_BAB_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.LONG_DESCRIPTION, null, 0.0,
  					gsBabLongDescription, null, null);
  	}
  	if (!StringUtils.isEmpty(gsCaLongDescription)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_CA_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.LONG_DESCRIPTION, null, 0.0,
  					gsCaLongDescription, null, null);
  	}
  	String gsLowPrice = pItemVO.getProductVO().getGSSkuLowPrice();
  	String gsHighPrice = pItemVO.getProductVO().getGSSkuHightPrice();
  	String gsPriceRangeDescrip = pItemVO.getProductVO().getGSPriceRangeDescrip();
  		
  	if (!StringUtils.isEmpty(gsLowPrice)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_BBB_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.SKU_LOW_PRICE, gsLowPrice, 0.0,
  					null, null, null);
  	}
  	if (!StringUtils.isEmpty(gsHighPrice)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_BBB_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.SKU_HIGH_PRICE, gsHighPrice, 0.0,
  					null, null, null);
  	}

  	if (!StringUtils.isEmpty(gsPriceRangeDescrip)) {

  			addProdTranslationAttributes(BBBCatalogImportConstant.GS_BBB_SITE_ID,
  					BBBCatalogImportConstant.EN_US, pProductItem,
  					BBBCatalogImportConstant.PRICE_RANGE_DESCRIP,
  					gsPriceRangeDescrip, 0.0, null, null, null);
  		}
  		
	String gsCaLowPrice = pItemVO.getProductVO().getGSCASKULowPrice();
	String gsCaHighPrice = pItemVO.getProductVO().getGSCASkuHightPrice();
	String gsCaPriceRangeDescrip = pItemVO.getProductVO().getGSCAPriceRangeDescrip();
  		
	if (!StringUtils.isEmpty(gsCaLowPrice)) {

		addProdTranslationAttributes(BBBCatalogImportConstant.GS_CA_SITE_ID,
				BBBCatalogImportConstant.EN_US, pProductItem,
				BBBCatalogImportConstant.SKU_LOW_PRICE, gsCaLowPrice, 0.0,
				null, null, null);
	}
	if (!StringUtils.isEmpty(gsCaHighPrice)) {

		addProdTranslationAttributes(BBBCatalogImportConstant.GS_CA_SITE_ID,
				BBBCatalogImportConstant.EN_US, pProductItem,
				BBBCatalogImportConstant.SKU_HIGH_PRICE, gsCaHighPrice, 0.0,
				null, null, null);
	}

	if (!StringUtils.isEmpty(gsCaPriceRangeDescrip)) {

		addProdTranslationAttributes(BBBCatalogImportConstant.GS_CA_SITE_ID,
				BBBCatalogImportConstant.EN_US, pProductItem,
				BBBCatalogImportConstant.PRICE_RANGE_DESCRIP,
				gsCaPriceRangeDescrip, 0.0, null, null, null);
	}
    // STOFU Changes END

    if (isLoggingDebug()) {

			logDebug("End updateFrequentProductProperties()");
		}
	}
	//GS(Stofu) related changes: Begin
	//updating product properies on the basis of various flags
	private void updateStofuFrequentProductProperties(
			final MutableRepositoryItem pProductItem, final ItemVO pItemVO)
			throws RepositoryException {
		    simplePropertyWrite(BBBCatalogImportConstant.GS_BBB_WEB_OFFERED_FLAG, pItemVO.isGSBBBWebOfferedFlag(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_BAB_WEB_OFFERED_FLAG, pItemVO.isGSBABWebOfferedFlag(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_CA_WEB_OFFERED_FLAG, pItemVO.isGSCAWebOfferedFlag(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_BBB_DISABLED_FLAG, pItemVO.isGSBBBDisabledFlag(), pProductItem);
	        simplePropertyWrite(BBBCatalogImportConstant.GS_BAB_DISABLED_FLAG, pItemVO.isGSBABDisabledFlag(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_CA_DISABLED_FLAG,  pItemVO.isGSCADisabledFlag(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_BBB_ENABLE_DATE,  pItemVO.getGSEnableDate(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_BAB_ENABLE_DATE,  pItemVO.getGSBABEnableDate(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_CA_ENABLE_DATE,  pItemVO.getGSCAEnableDate(), pProductItem);
		}
	//end
	
	//everlivingpdp
	private void updateEverLivingPDPProperties(
			final MutableRepositoryItem pProductItem, final ItemVO pItemVO)
			throws RepositoryException {
		    simplePropertyWrite(BBBCatalogImportConstant.DISABLE_FOREVER_PDP_FLAG, pItemVO.isDisableForeverPDPFlag(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.BAB_DISABLE_FOREVER_PDP_FLAG, pItemVO.isBabDisableForeverPDPFlag(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.CA_DISABLE_FOREVER_PDP_FLAG, pItemVO.isCaDisableForeverPDPFlag(), pProductItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_DISABLE_FOREVER_PDP_FLAG, pItemVO.isGsDisableForeverPDPFlag(), pProductItem);
		}
	//end

  /**
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  private void updateSkuProperties(final String pFeedId, final String pItemId, final boolean pIsFrequent,
      final boolean pIsRare, final ItemVO pItemVO, final Connection pConnection) throws BBBBusinessException,
      BBBSystemException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateSkuProperties]");
      logDebug("pFeedId[" + pFeedId + "]  pItemId[" + pItemId + "]  pIsFrequent[" + pIsFrequent + "]  pIsRare["
          + pIsRare + "]");
    }

    MutableRepositoryItem skuItem;

    try {
      skuItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pItemId, BBBCatalogImportConstant.SKU_DESC);

      if (skuItem != null) {
        if (isLoggingDebug()) {

          logDebug("Updating existing sku");
        }
        skuItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(pItemId,
            BBBCatalogImportConstant.SKU_DESC);
        if (pIsFrequent) {
          updateFrequentSkuProperties(pFeedId, skuItem, pItemVO, pConnection);
        }
        if (pIsRare) {
          updateRareSkuProperties(pFeedId, skuItem, pItemVO, pConnection);
        }
        // Added Property lastModifiedDate
        simplePropertyWrite(BBBCatalogImportConstant.LAST_MODIFIED_DATE, getLastModifiedDate(), skuItem);
        ((MutableRepository)getProductCatalog()).updateItem(skuItem);

      } else {

        // Create a new category item in the versioned repository
        if (isLoggingDebug()) {

          logDebug("Created new sku");
        }

        skuItem = ((MutableRepository)getProductCatalog()).createItem(pItemId, BBBCatalogImportConstant.SKU_DESC);

        // Display property is a mandatory property of the sku,
        // here default value will be provided to the display name which will be
        // updated in updateFrequentProductProperties
        // This is done to avoid exception
        simplePropertyWrite(BBBCatalogImportConstant.DISPLAY_NAME_DEFAULT, pItemId, skuItem);

        if (pIsFrequent) {
        	updateFrequentSkuProperties(pFeedId, skuItem, pItemVO, pConnection);
        }
        if (pIsRare) {
          updateRareSkuProperties(pFeedId, skuItem, pItemVO, pConnection);
        }
        // Added Property lastModifiedDate
        simplePropertyWrite(BBBCatalogImportConstant.LAST_MODIFIED_DATE, getLastModifiedDate(), skuItem);
        ((MutableRepository)getProductCatalog()).addItem(skuItem);
      }
    } catch (RepositoryException e) {
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEM", pItemId, getDate(), "Not able to update the sku ",
          pConnection);

      if (isLoggingDebug()) {

        logDebug("Not able to update the sku.");
      }

    }

  }

  /**
 * @param pConnection  
 */
private void updateRareSkuProperties(final String pFeedId,final MutableRepositoryItem pSkuItem,final ItemVO pItemVO,
		  final Connection pConnection) throws RepositoryException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateRareSkuProperties]");
      logDebug("pFeedId[" + pFeedId + "]  pSkuItem[" + pSkuItem + "]");
    }

    final SkuVO skuVO = pItemVO.getSkuVO();

    final String webOnlyFlag = pItemVO.isWebOnlyFlag();
    final String giftCert = skuVO.getGiftCertfFag();
    final String color = skuVO.getColor();
    final String colorGroup = skuVO.getColorGroup();
    final String size = skuVO.getSize();
    final String isGiftWrapEligible = skuVO.getGiftWrapEligible();
    final String vduSkuType = skuVO.getVDCSkuType();
    final String vduSkuMessage = skuVO.getVDCSkuMessage();
    final String upc = skuVO.getUpc();
    final String cost = skuVO.getCost();
    final String bopusExclusion = skuVO.getBopus();
    final String clerance = skuVO.getClearance();
    final String ecomFulfillment = skuVO.getCAEcomFulfilmentFlag();
    final String overWeight = skuVO.getOverWeightFlag();
    final String overSize = skuVO.getOverSizeFlag();
    final String forceBelowLine = skuVO.getForceBelowLine();
    final String taxStatus = skuVO.getTaxCd();
    final String prop65Lighting = skuVO.getProp65Lighting();
    final String prop65Crystal = skuVO.getProp65Crystal();
    final String prop65Dinnerware = skuVO.getProp65DinnerWare();
    final String prop65Other = skuVO.getProp65Other();
    final String jdaDept = skuVO.getJdaDeptId();
    final String jdaSubDept = skuVO.getJdaSubDeptid();
    final String jdaClass = skuVO.getJdaClass();
    final String vendorId = skuVO.getVendorId();   
    final Date enableDate = pItemVO.getEnableDate();
    /** Added Site Specific Enable Date for R2.1 #53 Endeca & PIM FEED*/ 
    //Date babEnableDate = pItemVO.getBABEnableDate();
   // Date caEnableDate = pItemVO.getCAEnableDate();
    
    //LTL-33(PIM feed processing changes for SKUs)
    final String assemblyTime = skuVO.getAssemblyTime();
    final String isAssemblyOffered = skuVO.getAssemblyOffered();
    final String ltlFlag = skuVO.getLtlFlag();
    final String orderToShipSla = skuVO.getOrderToShipSla();
    final String caseWeight = skuVO.getCaseWeight();
    
    //Katori Properties
    final String customizationOfferedFlag = skuVO.getCustomizationOfferedFlag();
    final String babCustomizationOfferedFlag = skuVO.getBabCustomizationOfferedFlag();
    final String caCustomizationOfferedFlag = skuVO.getCaCustomizationOfferedFlag();
    final String gsCustomizationOfferedFlag = skuVO.getGsCustomizationOfferedFlag();
    final String personalizationType = skuVO.getPersonalizationType();
    final String eligibleCustomizationCodes = skuVO.getEligibleCustomizationCodes();
    final String minShippingDays = skuVO.getMinShippingDays();
    final String maxShippingDays = skuVO.getMaxShippingDays();
    final String shippingCutoffOffset = skuVO.getShippingCutoffOffset();
    
    
    if (!StringUtils.isEmpty(assemblyTime)) {
    	
    	//IF ITS NOT A NUMBER,THEN SET IT AS ZERO IN PUB AND STG AND SWITCH TABLES OF BBB SCHEMAS
    	boolean assemblyTimeInNumberCheck= isNumeric(assemblyTime.trim());
    	if(assemblyTimeInNumberCheck){
    		simplePropertyWrite(BBBCatalogImportConstant.ASSEMBLY_TIME, assemblyTime, pSkuItem);
    	}else{
    		simplePropertyWrite(BBBCatalogImportConstant.ASSEMBLY_TIME, BBBCoreConstants.STRING_ZERO, pSkuItem);
    	}
    }
    else{
  		simplePropertyWrite(BBBCatalogImportConstant.ASSEMBLY_TIME, BBBCoreConstants.STRING_ZERO, pSkuItem);
  	}
    
    
    if (!StringUtils.isEmpty(isAssemblyOffered)) {
    		simplePropertyWrite(BBBCatalogImportConstant.IS_ASSEMBLY_OFFERED, Boolean.valueOf(getBoolean(isAssemblyOffered)), pSkuItem);
      }else{
  		simplePropertyWrite(BBBCatalogImportConstant.IS_ASSEMBLY_OFFERED, Boolean.valueOf(BBBCoreConstants.RETURN_FALSE), pSkuItem);
  	}
    
    if (!StringUtils.isEmpty(ltlFlag)) {
        simplePropertyWrite(BBBCatalogImportConstant.LTL_FLAG, Boolean.valueOf(getBoolean(ltlFlag)), pSkuItem);
      }else{
    		simplePropertyWrite(BBBCatalogImportConstant.LTL_FLAG, Boolean.valueOf(BBBCoreConstants.RETURN_FALSE), pSkuItem);
    	}
    
    if (!StringUtils.isEmpty(orderToShipSla)) {
    	
    	//IF ITS NOT A NUMBER,THEN SET IT AS ZERO IN PUB AND STG AND SWITCH TABLES OF BBB SCHEMAS
    	boolean orderToShipSlaInNumberCheck= isNumeric(orderToShipSla.trim());
    	if(orderToShipSlaInNumberCheck){
    		simplePropertyWrite(BBBCatalogImportConstant.ORDER_TO_SHIP_SLA, orderToShipSla, pSkuItem);
    	}else{
    		simplePropertyWrite(BBBCatalogImportConstant.ORDER_TO_SHIP_SLA, BBBCoreConstants.STRING_ZERO, pSkuItem);
    	}
    }
    else{
  		simplePropertyWrite(BBBCatalogImportConstant.ORDER_TO_SHIP_SLA, BBBCoreConstants.STRING_ZERO, pSkuItem);
  	}
    
    if (!StringUtils.isEmpty(caseWeight)) {
    	simplePropertyWrite(BBBCatalogImportConstant.CASE_WEIGHT, caseWeight, pSkuItem);
    } else{
  		simplePropertyWrite(BBBCatalogImportConstant.CASE_WEIGHT, BBBCoreConstants.STRING_ZERO, pSkuItem);
  	}
    
    
    
    
    if (enableDate !=null) {
    simplePropertyWrite(BBBCatalogImportConstant.ENABLE_DATE, enableDate, pSkuItem);    
    }   
    
    // BPSI-420 - Katori Properties
	logDebug("customizationOfferedFlag-----------------"
			+ customizationOfferedFlag);
	if (!StringUtils.isEmpty(customizationOfferedFlag)) {
		simplePropertyWrite(
				BBBCatalogImportConstant.CUSTOMIZATION_OFFERED_FLAG,
				Boolean.valueOf(getBoolean(customizationOfferedFlag)),
				pSkuItem);
	} else {
		simplePropertyWrite(
				BBBCatalogImportConstant.CUSTOMIZATION_OFFERED_FLAG,
				Boolean.valueOf(BBBCoreConstants.RETURN_FALSE), pSkuItem);
	}

	logDebug("babCustomizationOfferedFlag-----------------"
			+ babCustomizationOfferedFlag);
	if (!StringUtils.isEmpty(babCustomizationOfferedFlag)) {
		simplePropertyWrite(
				BBBCatalogImportConstant.BAB_CUSTOMIZATION_OFFERED_FLAG,
				Boolean.valueOf(getBoolean(babCustomizationOfferedFlag)),
				pSkuItem);
	} else {
		simplePropertyWrite(
				BBBCatalogImportConstant.BAB_CUSTOMIZATION_OFFERED_FLAG,
				Boolean.valueOf(BBBCoreConstants.RETURN_FALSE), pSkuItem);
	}

	logDebug("caCustomizationOfferedFlag-----------------"
			+ caCustomizationOfferedFlag);
	if (!StringUtils.isEmpty(caCustomizationOfferedFlag)) {
		simplePropertyWrite(
				BBBCatalogImportConstant.CA_CUSTOMIZATION_OFFERED_FLAG,
				Boolean.valueOf(getBoolean(caCustomizationOfferedFlag)),
				pSkuItem);
	} else {
		simplePropertyWrite(
				BBBCatalogImportConstant.CA_CUSTOMIZATION_OFFERED_FLAG,
				Boolean.valueOf(BBBCoreConstants.RETURN_FALSE), pSkuItem);
	}

	logDebug("gsCustomizationOfferedFlag-----------------"
			+ gsCustomizationOfferedFlag);
	if (!StringUtils.isEmpty(gsCustomizationOfferedFlag)) {
		simplePropertyWrite(
				BBBCatalogImportConstant.GS_CUSTOMIZATION_OFFERED_FLAG,
				Boolean.valueOf(getBoolean(gsCustomizationOfferedFlag)),
				pSkuItem);
	} else {
		simplePropertyWrite(
				BBBCatalogImportConstant.GS_CUSTOMIZATION_OFFERED_FLAG,
				Boolean.valueOf(BBBCoreConstants.RETURN_FALSE), pSkuItem);
	}

	logDebug("personalizationType-----------------"
			+ personalizationType);
	if (!StringUtils.isEmpty(personalizationType)) {
		simplePropertyWrite(
				BBBCatalogImportConstant.PERSONALIZATION_TYPE,
				personalizationType, pSkuItem);
	}

	logDebug("eligibleCustomizationCodes-----------------"
			+ eligibleCustomizationCodes);
	if (!StringUtils.isEmpty(eligibleCustomizationCodes)) {
		simplePropertyWrite(
				BBBCatalogImportConstant.ELIGIBLE_CUSTOMIZATION_CODES,
				eligibleCustomizationCodes, pSkuItem);
	}

	logDebug("minShippingDays-----------------" + minShippingDays);
	if (!StringUtils.isEmpty(minShippingDays)) {
		simplePropertyWrite(BBBCatalogImportConstant.MIN_SHIPPING_DAYS,
				Integer.valueOf(getInt(minShippingDays)), pSkuItem);
	} else {
		simplePropertyWrite(BBBCatalogImportConstant.MIN_SHIPPING_DAYS,
				BBBCoreConstants.ZERO, pSkuItem);
	}

	logDebug("maxShippingDays-----------------" + maxShippingDays);
	if (!StringUtils.isEmpty(maxShippingDays)) {
		simplePropertyWrite(BBBCatalogImportConstant.MAX_SHIPPING_DAYS,
				Integer.valueOf(getInt(maxShippingDays)), pSkuItem);
	} else {
		simplePropertyWrite(BBBCatalogImportConstant.MAX_SHIPPING_DAYS,
				BBBCoreConstants.ZERO, pSkuItem);
	}

	logDebug("shippingCutoffOffset-----------------" + shippingCutoffOffset);
	if (!StringUtils.isEmpty(shippingCutoffOffset)) {
		simplePropertyWrite(
				BBBCatalogImportConstant.SHIPPING_CUTOFF_OFFSET,
				Integer.valueOf(getInt(shippingCutoffOffset)), pSkuItem);
	} else {
		simplePropertyWrite(
				BBBCatalogImportConstant.SHIPPING_CUTOFF_OFFSET,
				BBBCoreConstants.ZERO, pSkuItem);
	}
    
	/** Added Site Specific Enable Date for R2.1 #53 Endeca & PIM FEED*/
    /*if (babEnableDate != null && !enableDate.equals(babEnableDate) ) {
    	addSkuTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.ENABLE_DATE_DEFAULT, null, 0.0, null, null,"U" ,getDate(babEnableDate));      
    }

    if(isLoggingDebug()){
		logDebug("Check for enable date after transalation CA "+caEnableDate);
	}

    
    if ( caEnableDate != null && !enableDate.equals(caEnableDate) ) {
    	addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.ENABLE_DATE_DEFAULT, null, 0.0, null, null,"U",getDate(caEnableDate));     
    }*/

    if (!StringUtils.isEmpty(webOnlyFlag)) {

      simplePropertyWrite(BBBCatalogImportConstant.WEB_ONLY_DEFAULT, Boolean.valueOf(getBoolean(webOnlyFlag)), pSkuItem);
    }
    if (!StringUtils.isEmpty(cost)) {

        simplePropertyWrite(BBBCatalogImportConstant.COST_DEFAULT, cost, pSkuItem);
      }

    if (!StringUtils.isEmpty(giftCert)) {

      simplePropertyWrite(BBBCatalogImportConstant.GIFT_CERT, Boolean.valueOf(getBoolean(giftCert)), pSkuItem);
    }

    simplePropertyWrite(BBBCatalogImportConstant.SKU_TYPE, skuVO.getSkuTypeCd(), pSkuItem);

    simplePropertyWrite(BBBCatalogImportConstant.COLOR2, color, pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.COLOR_GROUP, colorGroup, pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.SIZE2, size, pSkuItem);
    if (!StringUtils.isEmpty(giftCert)) {
      simplePropertyWrite(BBBCatalogImportConstant.GIFT_WRAP_ELIGIBLE, Boolean.valueOf(getBoolean(isGiftWrapEligible)), pSkuItem);
    }
    simplePropertyWrite(BBBCatalogImportConstant.VDC_SKU_TYPE, vduSkuType, pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.VDC_SKU_MESSAGE, vduSkuMessage, pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.UPC2, upc, pSkuItem);

    if (!StringUtils.isEmpty(bopusExclusion)) {
      simplePropertyWrite(BBBCatalogImportConstant.BOPUS_EXCLUSION, Boolean.valueOf(getBoolean(bopusExclusion)), pSkuItem);
    }
    if (!StringUtils.isEmpty(clerance)) {

      simplePropertyWrite(BBBCatalogImportConstant.CLERANCE2, Boolean.valueOf(getBoolean(clerance)), pSkuItem);
    }
    
    simplePropertyWrite(BBBCatalogImportConstant.BBB_DEPT, getjdaDeptId(jdaDept), pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.BBB_SUB_DEPT, getjdaSubDeptId(getId(jdaDept,jdaSubDept)), pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.JDA_CLASS, jdaClass, pSkuItem);

    simplePropertyWrite(BBBCatalogImportConstant.E_COM_FULFILLMENT, ecomFulfillment, pSkuItem);
    if (!StringUtils.isEmpty(overWeight)) {

      simplePropertyWrite(BBBCatalogImportConstant.OVER_WEIGHT, Boolean.valueOf(getBoolean(overWeight)), pSkuItem);
    }
    if (!StringUtils.isEmpty(overSize)) {

      simplePropertyWrite(BBBCatalogImportConstant.OVER_SIZE, Boolean.valueOf(getBoolean(overSize)), pSkuItem);
    }
    if (!StringUtils.isEmpty(forceBelowLine)) {

      simplePropertyWrite("forceBelowLineDefault", Boolean.valueOf(getBoolean(forceBelowLine)), pSkuItem);
    }

    simplePropertyWrite(BBBCatalogImportConstant.TAX_STATUS, taxStatus, pSkuItem);

    simplePropertyWrite(BBBCatalogImportConstant.PROP65_LIGHTING, Boolean.valueOf(getBoolean(prop65Lighting)), pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.PROP65_CRYSTAL, Boolean.valueOf(getBoolean(prop65Crystal)), pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.PROP65_DINNERWARE, Boolean.valueOf(getBoolean(prop65Dinnerware)), pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.PROP65_OTHER, Boolean.valueOf(getBoolean(prop65Other)), pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.VENDOR_ID, vendorId, pSkuItem);
  // simplePropertyWrite(BBBCatalogImportConstant.ENABLE_DATE, enableDate, pSkuItem);
    
    ////  PIM Feed Import BBBP-6233 Changes start
    if (isLoggingDebug()) {
    	logDebug("Start setting value for BBBP-6233 changes");
    }
    if (skuVO.getChainSkuStatusCd() != null) {
    	simplePropertyWrite(BBBCatalogImportConstant.CHAIN_SKU_STATUS_CD, String.valueOf(skuVO.getChainSkuStatusCd()), pSkuItem);
    } else {
    	simplePropertyWrite(BBBCatalogImportConstant.CHAIN_SKU_STATUS_CD, null, pSkuItem);
    }
    if (skuVO.getWebSkuStatusCd() != null) {
    	simplePropertyWrite(BBBCatalogImportConstant.WEB_SKU_STATUS_CD, String.valueOf(skuVO.getWebSkuStatusCd()), pSkuItem);
    } else {
    	simplePropertyWrite(BBBCatalogImportConstant.WEB_SKU_STATUS_CD, null, pSkuItem);
    }
    if (skuVO.getCaChainSkuStatusCd() != null) {
    	simplePropertyWrite(BBBCatalogImportConstant.CA_CHAIN_SKU_STATUS_CD, String.valueOf(skuVO.getCaChainSkuStatusCd()), pSkuItem);
    } else {
    	simplePropertyWrite(BBBCatalogImportConstant.CA_CHAIN_SKU_STATUS_CD, null, pSkuItem);
    }
    if (skuVO.getCaWebSkuStatusCd() != null) {
    	simplePropertyWrite(BBBCatalogImportConstant.CA_WEB_SKU_STATUS_CD, String.valueOf(skuVO.getCaWebSkuStatusCd()), pSkuItem);
    } else {
    	simplePropertyWrite(BBBCatalogImportConstant.CA_WEB_SKU_STATUS_CD, null, pSkuItem);
    }
    if (isLoggingDebug()) {
    	logDebug("end setting value for BBBP-6233 changes");
    }
    ////  PIM Feed Import BBBP-6233 Changes end
   
    final String babWebOnly = pItemVO.getBabWebOnlyFlag();
    final String caWebOnly = pItemVO.getCAWebOnlyFlag();
    final String babForceBelowLine = skuVO.getBabForceBelowLine();
    final String caForceBelowLine = skuVO.getCaForceBelowLine();
    final String caCost = skuVO.getCaCost();
    if(isLoggingDebug()){
		logDebug("check for cost in catalog tools " + skuVO.getCaCost()+ skuVO.getCost());
	}
    if(isLoggingDebug()){
		logDebug("check for cost in catalog tools setting parameters " +caCost+ cost);
	}

    if (!StringUtils.isEmpty(caCost)) {
    	 addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
    	          BBBCatalogImportConstant.COST_DEFAULT, caCost, 0.0, null,null,"U",null);
        
        }
    if(isLoggingDebug()){
		logDebug("check for cost after translations " +caCost+ cost);
	}

    if (!StringUtils.isEmpty(babWebOnly)) {
    addSkuTranslationBooleanAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.WEB_ONLY, null, 0.0, null, babWebOnly,null);
     /* addSkuTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.WEB_ONLY, null, 0.0, null, babWebOnly);*/
    }

    if (!StringUtils.isEmpty(caWebOnly)) {
      addSkuTranslationBooleanAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.WEB_ONLY, null, 0.0, null, caWebOnly,null);
   /*   addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.WEB_ONLY, null, 0.0, null, caWebOnly);*/
    }

    if (!StringUtils.isEmpty(babForceBelowLine)) {
    	addSkuTranslationBooleanAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
    	          BBBCatalogImportConstant.FORCE_BELOW_LINE, null, 0.0, null, babForceBelowLine,null);
     /* addSkuTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.FORCE_BELOW_LINE, null, 0.0, null, babForceBelowLine);*/
    }

    if (!StringUtils.isEmpty(caForceBelowLine)) {
    	addSkuTranslationBooleanAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.FORCE_BELOW_LINE, null, 0.0, null, caForceBelowLine,null);
 /*     addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.FORCE_BELOW_LINE, null, 0.0, null, caForceBelowLine);*/
    }

  }

  private void updateFrequentSkuProperties(final String pFeedId,final MutableRepositoryItem pSkuItem,final ItemVO pItemVO,
		  final Connection pConnection) throws RepositoryException, BBBBusinessException, BBBSystemException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateFrequentSkuProperties]");
      logDebug("pFeedId[" + pFeedId + "]  pSkuItem[" + pSkuItem + "]");
    }
    String displayName = pItemVO.getProductTitle();
    final String description = pItemVO.getShortDescription();

    final SkuVO skuVO = pItemVO.getSkuVO();
    final String webOfferedFlag = pItemVO.isWebOfferedFlag();
    final String skuDisable = pItemVO.isDisableFlag();
    final String collegeSku = pItemVO.isSkuCollegeId();
    final String isEmailOutOfStockFlag = pItemVO.isEmailOutOfStockFlag();
    final Boolean bopusExclusion = Boolean.valueOf(pItemVO.isBopusExclusion());
	//String isStoreSku = "N";
	final String pimShippingSurcharge = skuVO.getShippingSurcharge();
	final String skuIntlRestricted = skuVO.getIntlRestricted();
    // Frequent
    //Calling Stofu update method
	updateStofuFrequentSkuProperties(pSkuItem, pItemVO);
  	if (StringUtils.isEmpty(displayName)) {

      displayName = pSkuItem.getRepositoryId();
    }
    simplePropertyWrite(BBBCatalogImportConstant.DISPLAY_NAME_DEFAULT, displayName, pSkuItem);
    simplePropertyWrite(BBBCatalogImportConstant.DESCRIPTION_DEFAULT, description, pSkuItem);

    if (!StringUtils.isEmpty(webOfferedFlag)) {

      simplePropertyWrite(BBBCatalogImportConstant.WEB_OFFERED_DEFAULT, Boolean.valueOf(getBoolean(webOfferedFlag)), pSkuItem);
    }
    
    if (!StringUtils.isEmpty(skuIntlRestricted)) {
    	 simplePropertyWrite(BBBCatalogImportConstant.INTL_RESTRICTED, skuIntlRestricted, pSkuItem);
    	
    } 

    if (!StringUtils.isEmpty(skuDisable)) {
      simplePropertyWrite(BBBCatalogImportConstant.DISABLE_DEFAULT, Boolean.valueOf(getBoolean(skuDisable)), pSkuItem);
    }

    if (!StringUtils.isEmpty(isEmailOutOfStockFlag)) {

      simplePropertyWrite(BBBCatalogImportConstant.EMAIL_OUT_OF_STOCK, Boolean.valueOf(getBoolean(isEmailOutOfStockFlag)), pSkuItem);
    }

    simplePropertyWrite(BBBCatalogImportConstant.COLLEGE, collegeSku, pSkuItem);

    simplePropertyWrite(BBBCatalogImportConstant.BOPUS_EXCLUSION, bopusExclusion, pSkuItem);
    
    final double shippingSurcharge = getDouble(pimShippingSurcharge);
    if (shippingSurcharge > 0.0) {

      simplePropertyWrite(BBBCatalogImportConstant.SHIPPING_SURCHARGE_DEFAULT, Double.valueOf(shippingSurcharge), pSkuItem);
    } else {
      
      simplePropertyWrite(BBBCatalogImportConstant.SHIPPING_SURCHARGE_DEFAULT, null, pSkuItem);
    }
    final String folderId = getFolderId();

    updateItemMediaProperties(pSkuItem, pItemVO, folderId);

    final String babWebOfferedFlag = pItemVO.isBABWebOfferedFlag();
    final String caWebOfferedFlag = pItemVO.isCAWebOfferedFlag();
    final String babProdDisabled = pItemVO.isBABDisableFlag();
    final String caProdDisabled = pItemVO.isCADisableFlag();
    final String babTitle = pItemVO.getBABProductTitle();
    final String caTitle = pItemVO.getCAProductTitle();
    final String babDescription = pItemVO.getBABShortDescription();
    final String caDescription = pItemVO.getCAShortDescription();
    final String caShippingSurcharge = skuVO.getCAShippingSurcharge();
    final String babShippingSurcharge = skuVO.getBABShippingSurcharge();

    if (!StringUtils.isEmpty(babWebOfferedFlag)) {
     addSkuTranslationBooleanAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.WEB_OFFERED, null, 0.0, null, babWebOfferedFlag,null);
    /*  addSkuTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.WEB_OFFERED, null, 0.0, null, babWebOfferedFlag);*/
    }

    if (!StringUtils.isEmpty(caWebOfferedFlag)) {
addSkuTranslationBooleanAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.WEB_OFFERED, null, 0.0, null, caWebOfferedFlag,null);
    /*  addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.WEB_OFFERED, null, 0.0, null, caWebOfferedFlag);*/
    }

    if (!StringUtils.isEmpty(babProdDisabled)) {
  addSkuTranslationBooleanAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.DISABLE, null, 0.0, null, babProdDisabled,null);
     /* addSkuTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.DISABLE, null, 0.0, null, babProdDisabled);*/
    }
    if (!StringUtils.isEmpty(caProdDisabled)) {
   addSkuTranslationBooleanAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.DISABLE, null, 0.0, null, caProdDisabled,null);
      /*addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.DISABLE, null, 0.0, null, caProdDisabled);*/
    }
    if (!StringUtils.isEmpty(babTitle)) {

      addSkuTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.DISPLAY_NAME, babTitle, 0.0, null,null,"U",null);
    }
    if (!StringUtils.isEmpty(caTitle)) {

      addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.DISPLAY_NAME, caTitle, 0.0, null,null,"U",null);
    }
    if (!StringUtils.isEmpty(babDescription)) {

      addSkuTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.DESCRIPTION, babDescription, 0.0, null,null,"U",null);
    }
    if (!StringUtils.isEmpty(caDescription)) {

      addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.DESCRIPTION, caDescription, 0.0, null,null,"U",null);
    }
  //STOFU Properties
	String gsTitle = pItemVO.getGSProductTitle();
	String gsBabTitle = pItemVO.getGSBABProductTitle();
	String gsCaTitle = pItemVO.getGSCAProductTitle();
	
	if (!StringUtils.isEmpty(gsTitle)) {

		addSkuTranslationAttributes(BBBCatalogImportConstant.GS_BBB_SITE_ID,
				BBBCatalogImportConstant.EN_US, pSkuItem,
				BBBCatalogImportConstant.DISPLAY_NAME, gsTitle, 0.0, null,
				null, "U",null);
	}
	if (!StringUtils.isEmpty(gsBabTitle)) {

		addSkuTranslationAttributes(BBBCatalogImportConstant.GS_BAB_SITE_ID,
				BBBCatalogImportConstant.EN_US, pSkuItem,
				BBBCatalogImportConstant.DISPLAY_NAME, gsBabTitle, 0.0, null,
				null, "U",null);
	}
	if (!StringUtils.isEmpty(gsCaTitle)) {

		addSkuTranslationAttributes(BBBCatalogImportConstant.GS_CA_SITE_ID,
				BBBCatalogImportConstant.EN_US, pSkuItem,
				BBBCatalogImportConstant.DISPLAY_NAME, gsCaTitle, 0.0, null,
				null, "U",null);
	}
	
	String gsDescription = pItemVO.getGSShortDescription();
	String gsBabDescription = pItemVO.getGSBABShortDescription();
	String gsCaDescription = pItemVO.getGSCAShortDescription();
	
	if (!StringUtils.isEmpty(gsDescription)) {

		addSkuTranslationAttributes(BBBCatalogImportConstant.GS_BBB_SITE_ID,
				BBBCatalogImportConstant.EN_US, pSkuItem,
				BBBCatalogImportConstant.DESCRIPTION, gsDescription, 0.0,
				null, null, "U",null);
	}
	if (!StringUtils.isEmpty(gsBabDescription)) {

		addSkuTranslationAttributes(BBBCatalogImportConstant.GS_BAB_SITE_ID,
				BBBCatalogImportConstant.EN_US, pSkuItem,
				BBBCatalogImportConstant.DESCRIPTION, gsBabDescription, 0.0,
				null, null, "U",null);
	}
	if (!StringUtils.isEmpty(gsCaDescription)) {

		addSkuTranslationAttributes(BBBCatalogImportConstant.GS_CA_SITE_ID,
				BBBCatalogImportConstant.EN_US, pSkuItem,
				BBBCatalogImportConstant.DESCRIPTION, gsCaDescription, 0.0,
				null, null, "U",null);
	}
  	//End of STOFU properties
    if (!StringUtils.isEmpty(caShippingSurcharge)) {

      addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.SHIPPING_SURCHARGE, null, getDouble(caShippingSurcharge), null,null,"U",null);
    } else {
      
      addSkuTranslationAttributes(BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
          BBBCatalogImportConstant.SHIPPING_SURCHARGE, null, getDouble(caShippingSurcharge), null,null,"D",null);
      
    }
    if (!StringUtils.isEmpty(babShippingSurcharge)) {
        addSkuTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
            BBBCatalogImportConstant.SHIPPING_SURCHARGE, null, getDouble(babShippingSurcharge), null,null,"U",null);
      } else {
        
        addSkuTranslationAttributes(BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US, pSkuItem,
            BBBCatalogImportConstant.SHIPPING_SURCHARGE, null, getDouble(babShippingSurcharge), null,null,"D",null);
        
      }
	// Commented the below If Condition for PS-21021 
	//  if( (!StringUtils.isEmpty(webOfferedFlag) && webOfferedFlag.equals("N")) && (!StringUtils.isEmpty(babWebOfferedFlag) && babWebOfferedFlag.equals("N"))  && (!StringUtils.isEmpty(caWebOfferedFlag) && caWebOfferedFlag.equals("N"))){
    //	isStoreSku = "Y";
    	simplePropertyWrite(BBBCatalogImportConstant.STORE_SKU, Boolean.valueOf(getBoolean("N")), pSkuItem);
    //   }
	  
	  
    try {
    	final String pimEligibleShippMethods = skuVO.getEligibleShipMethods();
      updateEligibleShipMethods(pFeedId, pSkuItem, pimEligibleShippMethods, pConnection);

      final String pimNonShippableStates = skuVO.getNonShippableStates();
      updateNonShippableStates(pFeedId, pSkuItem, pimNonShippableStates, pConnection);
    } catch (RepositoryException rex) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEMS_FREQUENT", pSkuItem.getRepositoryId(), getDate(),
          "not able to update updateEligibleShipMethods or updateEligibleShipMethods or updateNonShippableStates",
          pConnection);
      if (isLoggingDebug()) {

				logDebug("End of updateFrequentSkuProperties");
			}
		}
	}
   //updating stofu related properties
	private void updateStofuFrequentSkuProperties(
			MutableRepositoryItem pSkuItem, ItemVO pItemVO) throws RepositoryException {
		    simplePropertyWrite(BBBCatalogImportConstant.GS_BBB_WEB_OFFERED_FLAG, pItemVO.isGSBBBWebOfferedFlag(), pSkuItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_BAB_WEB_OFFERED_FLAG, pItemVO.isGSBABWebOfferedFlag(), pSkuItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_CA_WEB_OFFERED_FLAG, pItemVO.isGSCAWebOfferedFlag(), pSkuItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_BBB_DISABLED_FLAG, pItemVO.isGSBBBDisabledFlag(), pSkuItem);
	        simplePropertyWrite(BBBCatalogImportConstant.GS_BAB_DISABLED_FLAG, pItemVO.isGSBABDisabledFlag(), pSkuItem);
		    simplePropertyWrite(BBBCatalogImportConstant.GS_CA_DISABLED_FLAG,  pItemVO.isGSCADisabledFlag(), pSkuItem);
		
	}
	
	//end

  /**
   * Method update the non shipping states into SKU
   * 
   * @param pSkuId
   * @param pSkuItem
   * @param pPIMNonShippableStates
   * @param pErrorList
   */
  private void updateNonShippableStates(final String pFeedId,final MutableRepositoryItem pSkuItem,
      final String pPIMNonShippableStates, final Connection pConnection) throws RepositoryException,BBBBusinessException, BBBSystemException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateNonShippableStates]");
      logDebug("pSkuItem[" + pSkuItem + "]  pPIMNonShippableStates[" + pPIMNonShippableStates + "]");
    }
    MutableRepositoryItem stateItem = null;
    // Temp list is created to save data lost from unwanted exceptions, below
    // logic clear non shippable states and added it again
    // if any exception comes, current data may lost, so below logic first add
    // it into the temp list and move it to the current list at end.
    final Set<MutableRepositoryItem> tempNonShippStateList = new HashSet<MutableRepositoryItem>();
    @SuppressWarnings("unchecked")
    final Set<RepositoryItem> nonShippableStatesItemSet = (Set<RepositoryItem>) pSkuItem
        .getPropertyValue(BBBCatalogImportConstant.NON_SHIPPABLE_STATES);
    
    if (StringUtils.isEmpty(pPIMNonShippableStates)) {
      if (isLoggingDebug()) {
        logDebug("Set Non Shipping States is null");

      }
      if(nonShippableStatesItemSet != null) {
        
        nonShippableStatesItemSet.clear();
        pSkuItem.setPropertyValue(BBBCatalogImportConstant.NON_SHIPPABLE_STATES, nonShippableStatesItemSet);
      }
      return;
    }

    final String[] pimNonShippableStatesArray = StringUtils.splitStringAtString(pPIMNonShippableStates, ",");

    if (pimNonShippableStatesArray == null || pimNonShippableStatesArray.length <= 0) {
      if (isLoggingDebug()) {
        logDebug("Issue with converting Non Shippable States to Array");
        logDebug("pimNonShippableStatesArray=" + pimNonShippableStatesArray.toString());
        logDebug("return without processing");
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEMS_RARE", pSkuItem.getRepositoryId(), getDate(),
          "Non Shippable States Details not found, pimNonShippableStatesArray is null", pConnection);

      return;
    }
    if (isLoggingDebug()) {
      logDebug("updating Non Shipping States");
      logDebug("Non Shipping States[]=" + pimNonShippableStatesArray.toString());
    }

    for (String pimNonShippableState : pimNonShippableStatesArray) {

      if (StringUtils.isEmpty(pimNonShippableState)) {
        continue;
      }

      stateItem = getShippingItem(pimNonShippableState, BBBCatalogImportConstant.STATE);
      if (stateItem == null) {

        if (isLoggingDebug()) {
          logDebug("Item not available in the shippingRepository");
          logDebug("Bad Relationship pimNonShippableState=" + pimNonShippableState);
        }
        getPimFeedTools().updateBadRecords(
            pFeedId,
            "",
            "ECP_ITEMS_RARE",
            pSkuItem.getRepositoryId(),
            getDate(),
            "Item not available in the shippingRepository,Bad Relationship pimNonShippableState="
                + pimNonShippableState + " stateItem is null", pConnection);

        continue;
      }
      tempNonShippStateList.add(stateItem);
    }

    if (!tempNonShippStateList.isEmpty()) {

      // clear all Set with assumption- each feed will get all the
      // NonShippableStates ids.
      nonShippableStatesItemSet.clear();
      nonShippableStatesItemSet.addAll(tempNonShippStateList);
      pSkuItem.setPropertyValue(BBBCatalogImportConstant.NON_SHIPPABLE_STATES, nonShippableStatesItemSet);
    }
  }

  /**
   * Method update the EligibleShippable Methods into SKU
   * 
   * @param pSkuId
   * @param pSkuItem
   * @param pPIMEligibleShippMethods
   * @param pErrorList
   * @throws BBBBusinessException
   * @throws BBBSystemException
 * @throws RepositoryException 
   */
  private void updateEligibleShipMethods(final String pFeedId,final MutableRepositoryItem pSkuItem,
      final String pPIMEligibleShippMethods, final Connection pConnection) throws BBBSystemException, BBBBusinessException, RepositoryException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateEligibleShipMethods]");
      logDebug("pSkuItem[" + pSkuItem + "]  pPIMEligibleShippMethods[" + pPIMEligibleShippMethods + "]");
    }
    MutableRepositoryItem eligibleShippMethodItem = null;
    // Temp list is created to save data lost from unwanted exceptions, below
    // logic clear eligible shippable methods and added it again
    // if any exception comes, current data may lost, so below logic first add
    // it into the temp list and move it to the current list at end.
    final Set<MutableRepositoryItem> tempEligibleShippMethods = new HashSet<MutableRepositoryItem>();
    @SuppressWarnings("unchecked")
    final Set<RepositoryItem> eligibleShippMethodsItemSet = (Set<RepositoryItem>) pSkuItem
        .getPropertyValue(BBBCatalogImportConstant.ELIGIBLE_SHIP_METHODS);

    if (StringUtils.isEmpty(pPIMEligibleShippMethods)) {
      if(eligibleShippMethodsItemSet != null) {
        
        eligibleShippMethodsItemSet.clear();
        pSkuItem.setPropertyValue(BBBCatalogImportConstant.ELIGIBLE_SHIP_METHODS, eligibleShippMethodsItemSet);
      }
      return;
    }

    final String[] pimEligibleShippMethodsArray = StringUtils.splitStringAtString(pPIMEligibleShippMethods, ",");

    if (pimEligibleShippMethodsArray == null || pimEligibleShippMethodsArray.length <= 0) {
      if (isLoggingDebug()) {
        logDebug("Issue with converting Eligible ShippMethods to Array");
        logDebug("pimEligibleShippMethodsArray=" + pimEligibleShippMethodsArray.toString());
        logDebug("return without processing");
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEMS_RARE", pSkuItem.getRepositoryId(), getDate(),
          "Issue with converting Eligible ShippMethods to Array, pimEligibleShippMethodsArray is null", pConnection);

      return;
    }
    if (isLoggingDebug()) {
      logDebug("updating EligibleShippMethods");
      logDebug("pimEligibleShippMethods[]=" + pimEligibleShippMethodsArray.toString());
    }

    for (String pimEligibleShippMethod : pimEligibleShippMethodsArray) {

      if (StringUtils.isEmpty(pimEligibleShippMethod)) {
        continue;
      }

      eligibleShippMethodItem = getShippingItem(pimEligibleShippMethod, BBBCatalogImportConstant.SHIPPING_METHODS_DESC);
      if (eligibleShippMethodItem == null) {

        if (isLoggingDebug()) {
          logDebug("Item not available in the shippingRepository");
          logDebug("Bad Relationship pimEligibleShippMethod=" + pimEligibleShippMethod);
        }
        getPimFeedTools().updateBadRecords(
            pFeedId,
            "",
            "ECP_ITEMS_RARE",
            pSkuItem.getRepositoryId(),
            getDate(),
            "Item not available in the shippingRepository,Bad Relationship pimEligibleShippMethod="
                + eligibleShippMethodItem + "eligibleShippMethodItem is null", pConnection);
        continue;
      }
      tempEligibleShippMethods.add(eligibleShippMethodItem);
    }

    if (!tempEligibleShippMethods.isEmpty()) {

      // clear all Set with assumption- each feed will get all the
      // EligibleShippMethod ids.
      eligibleShippMethodsItemSet.clear();
      eligibleShippMethodsItemSet.addAll(tempEligibleShippMethods);
      pSkuItem.setPropertyValue(BBBCatalogImportConstant.ELIGIBLE_SHIP_METHODS, eligibleShippMethodsItemSet);
    }
  }




  /**
   * This method update the brand properties
   * 
   * @param pSiteId
   * @param pFeedId
 * @param pBrandId 
 * @param pErrorList 
 * @param pConnection 
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
 * @return RepositoryItem
 * @throws BBBBusinessException 
 * @throws BBBSystemException 
   */
  public MutableRepositoryItem updateBrandsProperties(final String pFeedId, final String pBrandId,
		  final List<String> pErrorList, final Connection pConnection) throws BBBBusinessException, BBBSystemException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_BRANDS_PROP);
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateBrandsProperties]");
      logDebug("pFeedId[" + pFeedId + "] pBrandId[" + pBrandId + "]");
    }
    final String brandId = pBrandId;

    final BrandVO brandVO = getPimFeedTools().getBrandDetails(pFeedId, brandId, pConnection);

    if (brandVO == null) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_BRANDS", brandId, getDate(),
          "brand is not found in the repository, brandVO is null", pConnection);
      if (isLoggingDebug()) {

        logDebug("brandVO is null");
      }
      return null;
    }

    final String brandName = brandVO.getName();
    final String brandDescription = brandVO.getDescrip();
    final String brandImage = brandVO.getBrandImage();
    final boolean display = brandVO.isDisplayFlag();
    final String siteIds = brandVO.getSiteId();

    MutableRepositoryItem brandItem = null;
    try {
      brandItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(brandId, BBBCatalogImportConstant.BBB_BRAND);
      if (brandItem == null) {
        if (isLoggingDebug()) {

          logDebug("brandItem is null so creating it");
        }

        createItem(brandId, BBBCatalogImportConstant.BBB_BRAND);
      }
      brandItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(brandId,
          BBBCatalogImportConstant.BBB_BRAND);
      if (isLoggingDebug()) {

        logDebug("brandItem=" + brandItem);
      }
      updateSeoTags(pFeedId, pBrandId, null, brandVO, null, false);
      simplePropertyWrite(BBBCatalogImportConstant.BRAND_NAME, brandName, brandItem);
      simplePropertyWrite(BBBCatalogImportConstant.BRAND_DESCRIP, brandDescription, brandItem);
      simplePropertyWrite(BBBCatalogImportConstant.BRAND_IMAGE, brandImage, brandItem);
      simplePropertyWrite(BBBCatalogImportConstant.DISPLAYS, Boolean.valueOf(display), brandItem);
      associateWithSiteItem(brandItem, BBBCatalogImportConstant.SITES, siteIds);
      ((MutableRepository)getProductCatalog()).updateItem(brandItem);

    } catch (RepositoryException rex) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_BRANDS", brandId, getDate(),
          brandId + " has following issue, not able to create or update brand item", pConnection);
      if (isLoggingDebug()) {

        logDebug("Not able to create or update brand item");
      }
      BBBPerformanceMonitor.cancel(UPDATE_BRANDS_PROP);
      isMonitorCanceled = true;
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_BRANDS_PROP);

      }
    }
    return brandItem;
  }

  /**
   * Method associate rebate items with SKU
 * @param pFeedId 
   * 
   * @param pSkuId
 * @param pConnection 
 * @param isProductionImport 
   * @param pSkuItem
   * @param pPIMNonShippableStates
   * @param pErrorList
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  public void associateRebateRelnWithSku(final String pFeedId, final String pSkuId, final Connection pConnection, final boolean isProductionImport)
      throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_REBATE_RELN_WITH_SKU);

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateRebateRelnWithSku]");
      logDebug("pFeedId[" + pFeedId + "] pSkuId[" + pSkuId + "]");
    }
    try {
      // getItem() - To find the category into the version repository
      MutableRepositoryItem skuItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pSkuId,
          BBBCatalogImportConstant.SKU_DESC);
      if (skuItem == null) {
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_REBATES", pSkuId, getDate(),
            "Sku Details not found in the repository,skuItem is null", pConnection);

        if (isLoggingDebug()) {

          logDebug(pSkuId + " Sku not available in the repository");
          logDebug("Sku - Rebate relationship could not be done ");
        }

        return;
      }

      final List<OperationVO> rebateIdsVOList = getPimFeedTools().getRebateList(pFeedId, pSkuId, pConnection);

      @SuppressWarnings("unchecked")
      Set<MutableRepositoryItem> rebateItemSet = (Set<MutableRepositoryItem>) skuItem
          .getPropertyValue(BBBCatalogImportConstant.REBATES);

      for (OperationVO rebateIdVO : rebateIdsVOList) {

    	  final String rebateId = rebateIdVO.getId();
    	  final String opcode = rebateIdVO.getOperationCode();
    	  final MutableRepositoryItem rebateItem = getRebateItem(rebateId);

        if (rebateItem == null) {

          getPimFeedTools().updateBadRecords(
              pFeedId,
              "",
              "ECP_SKU_REBATES",
              pSkuId + BBBCatalogImportConstant.KEY_SEPERATOR + rebateId,
              getDate(),
              "Bad relationship for SkuId-" + pSkuId + " RebateId-" + rebateId
                  + "Sku - Rebate relationship could not be done", pConnection);

          if (isLoggingDebug()) {

            logDebug("Bad relationship for SkuId-" + pSkuId + " RebateId-" + rebateId);
            logDebug("Sku - Rebate relationship could not be done ");
          }

          continue;
        }

        if (rebateItemSet == null) {
          rebateItemSet = new HashSet<MutableRepositoryItem>();
        }
        if (!StringUtils.isEmpty(opcode) && opcode.equals(BBBCatalogImportConstant.DELETE_CODE)) {
          
          // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
          if(isProductionImport && !rebateItemSet.contains(rebateItem)) {
            
            continue;
          }  
          if (rebateItemSet.contains(rebateItem)) {

            rebateItemSet.remove(rebateItem);
          } else {

            // pErrorList.add(subCategoryId);
            getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_REBATES", rebateId + "-" + pSkuId, getDate(),
                "Sku not associated with Rebate. Unable to delete association", pConnection);

            if (isLoggingDebug()) {

              logDebug(rebateId + " item not exist with " + pSkuId + " Could not delete");

            }
          }
          continue;
        } else {
          if (!rebateItemSet.contains(rebateItem)) {
            rebateItemSet.add(rebateItem);
          }

        }
      }
      // add child categories to current category
      // getItemForUpdate  get a write lock on the latest version.
      skuItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(pSkuId, BBBCatalogImportConstant.SKU_DESC);

      skuItem.setPropertyValue(BBBCatalogImportConstant.REBATES, rebateItemSet);
      ((MutableRepository)getProductCatalog()).updateItem(skuItem);

    } catch (RepositoryException rex) {

      if (isLoggingError()) {

        logError("Not able to create or update rebates for sku=" + pSkuId);
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_REBATES", pSkuId, getDate(), "Association not done",
          pConnection);
      BBBPerformanceMonitor.cancel(ASSOCIATE_REBATE_RELN_WITH_SKU);
      isMonitorCanceled = true;
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_REBATE_RELN_WITH_SKU);
      }
    }

  }

  /**
   * Get the rebate item
   * 
   * @param pRebateId
   * @return
   */
  private MutableRepositoryItem getRebateItem(final String pRebateId) {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [getRebateItem]");
      logDebug("pRebateId[" + pRebateId + "]");
    }
    MutableRepositoryItem rebateItem = null;
    try {
      rebateItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pRebateId, BBBCatalogImportConstant.REBATE_INFO);

    } catch (RepositoryException e) {
      if (isLoggingError()) {

        logError(pRebateId + " has following issue ");
        logError(BBBStringUtils.stack2string(e));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(e));
      }
    }
    return rebateItem;
  }

  /**
   * This method update the rebateInfo properties
   * 
   * @param pSiteId
   * @param pFeedId
 * @param pRebateId 
 * @param pConnection 
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
 * @return RepositoryItem
 * @throws BBBBusinessException 
 * @throws BBBSystemException 
   */
  public MutableRepositoryItem updateRebateInfo(final String pFeedId, final String pRebateId,
      final Connection pConnection) throws BBBBusinessException, BBBSystemException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_REBATE_INFO);
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateRebateInfo]");
      logDebug("pFeedId[" + pFeedId + "] pRebateId[" + pRebateId + "]");
    }

    final String rebateId = pRebateId;

    final RebateVO rebateVO = getPimFeedTools().getRebateDetails(pFeedId, rebateId, pConnection);

    if (rebateVO == null) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_REBATES", rebateId, getDate(),
          "Rebate details not found in the repository,rebateVO is null ", pConnection);
      return null;

    }

    final String description = rebateVO.getDescrip();
    final String rebateURL = rebateVO.getRebateUrl();
    final Timestamp startDate = rebateVO.getStartDate();
    final Timestamp endDate = rebateVO.getEndDate();
    final String siteIds = rebateVO.getSiteId();

    MutableRepositoryItem rebateItem = null;
    try {
			if (siteIds != null) {
				rebateItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(rebateId, BBBCatalogImportConstant.REBATE_INFO);
				if (rebateItem == null) {

					createItem(rebateId, BBBCatalogImportConstant.REBATE_INFO);
				}
				rebateItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(rebateId, BBBCatalogImportConstant.REBATE_INFO);
				simplePropertyWrite(BBBCatalogImportConstant.DESCRIP, description, rebateItem);
				simplePropertyWrite(BBBCatalogImportConstant.REBATE_URL, rebateURL, rebateItem);
				simplePropertyWrite(BBBCatalogImportConstant.START_DATE, startDate, rebateItem);
				simplePropertyWrite(BBBCatalogImportConstant.END_DATE, endDate, rebateItem);

				associateWithSiteItem(rebateItem, BBBCatalogImportConstant.SITES, siteIds);
				((MutableRepository)getProductCatalog()).updateItem(rebateItem);
			} else {
				if(isLoggingDebug()){
					logDebug("Updating the bad record for the rebate:::::"+rebateId);
					logDebug("Updating the bad record, as the siteId is null for the follwing rebate:::::::"+rebateVO.getDescrip());
				}
				getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_REBATES", rebateId, getDate(), rebateId + " Site Id cannot be null for updating rebate",
						pConnection);
			}
    } catch (RepositoryException rex) {
    	final String error = new StringBuffer().append(rebateId).append(" has issue, not able to create or update rebate item").toString();
      if (isLoggingError()) {
   
        logError(error);
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_REBATES", rebateId, getDate(),
          new StringBuilder().append("RepostioryException=").append(rex.getMessage()).toString(), pConnection);
      BBBPerformanceMonitor.cancel(UPDATE_REBATE_INFO);
      isMonitorCanceled = true;
      throw new BBBBusinessException(BBBCoreErrorConstants.VERS_TOOLS_REPOSITORY_EXC,error, rex);
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_REBATE_INFO);
      }
    }
    return rebateItem;
  }
  
  public void updateStofuImages(final String pFeedId, final String pSkuId,
			final Connection pConnection) throws BBBBusinessException,
			BBBSystemException {
		MutableRepositoryItem productItem = null;
		MutableRepositoryItem skuItem = null;
		if (isLoggingDebug()) {
			logDebug("CatalogTools Method Name [updateStofuImages]");
			logDebug("pFeedId[" + pFeedId + "] pSkuId[" + pSkuId + "]");
		}

		StofuImageParentVO stofuImageParentVO = getPimFeedTools()
				.getStofuImages(pFeedId, pSkuId, pConnection);

		if (stofuImageParentVO == null) {

			getPimFeedTools()
					.updateBadRecords(
							pFeedId,
							"",
							"ECP_STOFU_IMAGES",
							pSkuId,
							getDate(),
							"Ambiguous Record : multiple product flags corresponding to single item ",
							pConnection);
		} else {

			boolean productFlag = stofuImageParentVO.isProductFlag();
			List<StofuImagesVO> stofuImagesVOList = stofuImageParentVO
					.getStofuImagesVOList();

			if (productFlag) {
				try {

					productItem = (MutableRepositoryItem) getProductCatalog()
							.getItem(pSkuId,
									BBBCatalogImportConstant.PRODUCT_DESC);
					if (productItem == null) {
						return;
					}

					saveUpdatedItem(productItem, stofuImagesVOList,
							"gsProductImages");

				} catch (RepositoryException e) {
					if (isLoggingError()) {

						logError(pSkuId
								+ " has following issue, not able to create or update Stofu Image properties");
						logError(BBBStringUtils.stack2string(e));
					}
					if (isLoggingDebug()) {

						logDebug(BBBStringUtils.stack2string(e));
					}
					getPimFeedTools()
							.updateBadRecords(
									pFeedId,
									"",
									"ECP_STOFU_IMAGES",
									pSkuId
											+ BBBCatalogImportConstant.KEY_SEPERATOR
											+ pSkuId,
									getDate(),
									"Association not done,not able to create or update stofu image  properties",
									pConnection);

				}
			} else {

				try {

					skuItem = (MutableRepositoryItem) getProductCatalog()
							.getItem(pSkuId, BBBCatalogImportConstant.SKU_DESC);
					if (skuItem == null) {
						return;
					}

					saveUpdatedItem(skuItem, stofuImagesVOList, "gsSkuImages");

				} catch (RepositoryException e) {
					if (isLoggingError()) {

						logError(pSkuId
								+ " has following issue, not able to create or update Stofu Image properties");
						logError(BBBStringUtils.stack2string(e));
					}
					if (isLoggingDebug()) {

						logDebug(BBBStringUtils.stack2string(e));
					}
					getPimFeedTools()
							.updateBadRecords(
									pFeedId,
									"",
									"ECP_STOFU_IMAGES",
									pSkuId
											+ BBBCatalogImportConstant.KEY_SEPERATOR
											+ pSkuId,
									getDate(),
									"Association not done,not able to create or update stofu image  properties",
									pConnection);
				}
			}

		}
	}

	// Stofu Related Method
	private void saveUpdatedItem(MutableRepositoryItem item,
			List<StofuImagesVO> stofuImagesVOList, String propertyName)
			throws RepositoryException {

		Map<String, String> updatedImageMap = new HashMap<String, String>();
		try {
			updatedImageMap = (Map<String, String>) item
					.getPropertyValue(propertyName);
			for (StofuImagesVO stofuImagesVO : stofuImagesVOList) {
				String operationFlag = stofuImagesVO.getOperationFlag();
				String shotType = stofuImagesVO.getShotType();
				String scene7URL = stofuImagesVO.getScene7URL();
				if (operationFlag!=null && !operationFlag.isEmpty()) {
					if (operationFlag.equalsIgnoreCase(BBBCatalogImportConstant.DELETE)) {
						if (updatedImageMap.containsKey(shotType)) {

							updatedImageMap.remove(shotType);

						}

					} else if (operationFlag
							.equalsIgnoreCase(BBBCatalogImportConstant.INSERT)
							|| operationFlag
							.equalsIgnoreCase(BBBCatalogImportConstant.UPDATE)) {

						updatedImageMap.put(shotType, scene7URL);

					} else {
						if (isLoggingDebug()) {
							logDebug("Operation Flag is not valid :  skiping this particular VO ");
							continue;

						}
					}
				}
			}

			item.setPropertyValue(propertyName, updatedImageMap);

			((MutableRepository)getProductCatalog()).updateItem(item);
		} catch (RepositoryException e) {
			throw new RepositoryException();
		}

	}

	/**
	 * This method create or update the product media relationship and its
	 * properties
	 * 
	 * @param pSiteId
	 * @param pFeedId
	 * @param pItemId
	 * @param pIsFrequent
	 * @param pItemVO
	 * @throws BBBBusinessException
	 */
	public MutableRepositoryItem updatePrdMediaRelnProperties(
			final String pFeedId, final String pProductId,
			final String pMediaId, final Connection pConnection)
			throws BBBSystemException, BBBBusinessException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updatePrdMediaRelnProperties]");
      logDebug("pFeedId[" + pFeedId + "] pProductId[" + pProductId + "] pMediaId[" + pMediaId + "]");
    }
    ItemMediaVO itemMediaVO;
    if (isLoggingDebug()) {

      logDebug("Start updatePrdMediaRelnProperties. Update Product Media relationship properties");
      logDebug("ProductId=" + pProductId + " MediaId=" + pMediaId);

    }

    itemMediaVO = getPimFeedTools().getPIMItemMediaDetail(pFeedId, pProductId, pMediaId, pConnection);

    if (itemMediaVO == null) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PROD_MEDIA",
          pProductId + BBBCatalogImportConstant.KEY_SEPERATOR + pMediaId, getDate(),
          "Item Media Details not found,itemMediaVO is null", pConnection);

      if (isLoggingDebug()) {

        logDebug("Item Media Details not found in the Staging database");

      }
      return null;

      // throw new BBBBusinessException(pProductId +
      // " Item Media Details not found in the Staging database");
    }
    final String mediaId = pMediaId;
    final MutableRepositoryItem mediaItem = getOtherMediaItem(mediaId);
    final String mediaRepositoryId = getId(pProductId, mediaId);
    final Timestamp startDate = itemMediaVO.getStartDate();
    final Timestamp endDate = itemMediaVO.getEndDate();
    final String comments = itemMediaVO.getComments();
    final String widget = itemMediaVO.getWidget();

    MutableRepositoryItem productMediaItem = null;

    if (mediaItem == null) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_MEDIA", pMediaId, getDate(),
          "Media Details not found in the repository,mediaItem is null", pConnection);
      return null;
    }
    try {

      productMediaItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(mediaRepositoryId,
          BBBCatalogImportConstant.BBB_PRD_MEDIA_RELN);
      if (productMediaItem == null) {
        if (isLoggingDebug()) {

          logDebug("Item Media Details not found so creating the item");

        }

        createItem(mediaRepositoryId, BBBCatalogImportConstant.BBB_PRD_MEDIA_RELN);
      }
      productMediaItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(mediaRepositoryId,
          BBBCatalogImportConstant.BBB_PRD_MEDIA_RELN);
      simplePropertyWrite(BBBCatalogImportConstant.MEDIA, mediaItem, productMediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.START_DATE, startDate, productMediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.END_DATE, endDate, productMediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.COMMENT, comments, productMediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.WIDGETS, widget, productMediaItem);

      ((MutableRepository)getProductCatalog()).updateItem(productMediaItem);
      if (isLoggingDebug()) {
        logDebug("End updatePrdMediaRelnProperties");
      }
    } catch (RepositoryException rex) {
      if (isLoggingError()) {

        logError(pProductId + " has following issue, not able to create or update Media Relation properties");
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PROD_MEDIA",
          pProductId + BBBCatalogImportConstant.KEY_SEPERATOR + pMediaId, getDate(),
          "Association not done,not able to create or update Media Relation properties", pConnection);

    }
    return productMediaItem;
  }

  /**
   * This method create or update the media properties
   * 
   * @param pSiteId
   * @param pFeedId
 * @param pMediaId 
 * @param pConnection 
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public void updateOtherMediaProperties(final String pFeedId, final String pMediaId, final Connection pConnection)
      throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_OTHER_MEDIA_PROP);

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateOtherMediaProperties]");
      logDebug("pFeedId[" + pFeedId + "] pMediaId[" + pMediaId + "]");
    }
    final String mediaId = pMediaId;

    MediaVO mediaVO;
    try {
      mediaVO = getPimFeedTools().getPIMOtherMediaDetail(pFeedId, mediaId, pConnection);

      if (mediaVO == null) {

        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_MEDIA", mediaId, getDate(),
            "Details not found for the mediaId in the repository,mediaVO is null ", pConnection);

        return;
        // throw new BBBBusinessException(mediaId +
        // " Details not found in the Staging database");
      }

      final String mediaType = mediaVO.getMediaType();
      final String provider = mediaVO.getProvider();
      final String mediaSource = mediaVO.getMediaSource();
      final String mediaDescription = mediaVO.getMediaDescription();
      final String comments = mediaVO.getComments();
      final String mediaTranscript = mediaVO.getMediaTranscript();
      final Timestamp startDate = mediaVO.getStartDate();
      final Timestamp endDate = mediaVO.getEndDate();
      final String widgetId = mediaVO.getWidgetId();
      final String siteIds = mediaVO.getSiteIds();
      MutableRepositoryItem mediaItem = null;

      mediaItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(mediaId,
          BBBCatalogImportConstant.BBB_OTHER_MEDIA_DESC);
      if (mediaItem == null) {
        if (isLoggingDebug()) {

          logDebug("Item Media Details not found so creating the item");

        }

        createItem(mediaId, BBBCatalogImportConstant.BBB_OTHER_MEDIA_DESC);
      }
      mediaItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(mediaId,
          BBBCatalogImportConstant.BBB_OTHER_MEDIA_DESC);
      simplePropertyWrite(BBBCatalogImportConstant.MEDIA_TYPE, mediaType, mediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.PROVIDER2, provider, mediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.MEDIA_SOURCE, mediaSource, mediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.MEDIA_DESCRIPTION, mediaDescription, mediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.COMMENT, comments, mediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.MEDIA_TRANSCRIPT, mediaTranscript, mediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.START_DATE, startDate, mediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.END_DATE, endDate, mediaItem);
      simplePropertyWrite(BBBCatalogImportConstant.WIDGETS, widgetId, mediaItem);
      associateWithSiteItem(mediaItem, BBBCatalogImportConstant.SITES, siteIds);
      
      ((MutableRepository)getProductCatalog()).updateItem(mediaItem);

    } catch (RepositoryException rex) {
      if (isLoggingError()) {

        logError(mediaId + " has following issue, not able to create or update media item");
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_MEDIA", mediaId, getDate(),
          mediaId + " has following issue, not able to create or update media item", pConnection);
      BBBPerformanceMonitor.cancel(UPDATE_OTHER_MEDIA_PROP);
      isMonitorCanceled = true;
    } finally {

      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_OTHER_MEDIA_PROP);
      }
    }

  }

  /**
   * This method get brand item
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
   * @throws BBBBusinessException
   */
  private MutableRepositoryItem getOtherMediaItem(final String pMediaId) {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [getOtherMediaItem]");
      logDebug(" pMediaId[" + pMediaId + "]");
    }

    MutableRepositoryItem otherMediaItem = null;
    try {
      otherMediaItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pMediaId,
          BBBCatalogImportConstant.BBB_OTHER_MEDIA_DESC);

    } catch (RepositoryException rex) {
      
    	final String error = new StringBuilder().append("Media Item").append(pMediaId).append(" has issue ").append(rex.getMessage()).toString();
      if (isLoggingError()) {
        
        logError(error);
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }

    }
    return otherMediaItem;
  }

  /**
   * This method create and update item for multi property productTabs for
   * provided skuId
   * 
   * @param pSiteId
   * @param pFeedId
 * @param pProductId 
 * @param pErrorList 
 * @param pConnection 
 * @param isProductionImport 
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  public void createUpdateProductTabs(final String pFeedId, final String pProductId,final List<String> pErrorList,
      final Connection pConnection, final boolean isProductionImport) throws BBBBusinessException, BBBSystemException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [createUpdateProductTabs]");
      logDebug(" pFeedId[" + pFeedId + "] pProductId[" + pProductId + "]");
    }

    final List<OperationVO> tabIdsVO = getPimFeedTools().getProductTabs(pFeedId, pProductId, pConnection);
    for (OperationVO tabVO : tabIdsVO) {

    	final String tabId = tabVO.getId();
    	updateProductTabsProperties(pFeedId, pProductId, tabId, pConnection);

    }
    associateTabsWithProduct(pFeedId, pProductId, pErrorList, pConnection, isProductionImport);
  }

  /**
   * This method update the tabs properties
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  private void updateProductTabsProperties(final String pFeedId, final String pProductId, final String pTabId,
      final Connection pConnection) throws BBBBusinessException, BBBSystemException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_PRODUCT_TABS_PROP);

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateProductTabsProperties]");
      logDebug(" pFeedId[" + pFeedId + "] pProductId[" + pProductId + "] pTabId[" + pTabId + "]");
    }

    final ProductTabVO productTabVO = getPimFeedTools().getProductTabsDetails(pFeedId, pProductId, pTabId, pConnection);
    if (productTabVO == null) {
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PRODUCT_TABS",
          pProductId + BBBCatalogImportConstant.KEY_SEPERATOR + pTabId, getDate(),
          "Details not found in the repository", pConnection);
      return;
    }
    final String tabName = productTabVO.getTabName();
    final String tabContent = productTabVO.getTabContent();

    final String pimSiteList = productTabVO.getSiteId();

    MutableRepositoryItem productTabItem = null;
    final String tabRepositoryId = getId(pProductId, pTabId);
    try {

      productTabItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(tabRepositoryId,
          BBBCatalogImportConstant.PRODUCT_TAB_DESC);
      if (productTabItem == null) {
        if (isLoggingDebug()) {

          logDebug("Tab Details not found so creating the item");

        }

        createItem(tabRepositoryId, BBBCatalogImportConstant.PRODUCT_TAB_DESC);
      }
      productTabItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(tabRepositoryId,
          BBBCatalogImportConstant.PRODUCT_TAB_DESC);
      simplePropertyWrite(BBBCatalogImportConstant.TAB_NAME, tabName, productTabItem);
      simplePropertyWrite(BBBCatalogImportConstant.TAB_CONTENT, tabContent, productTabItem);

      associateWithSiteItem(productTabItem, BBBCatalogImportConstant.SITES, pimSiteList);
      ((MutableRepository)getProductCatalog()).updateItem(productTabItem);

    } catch (RepositoryException e) {
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PRODUCT_TABS", tabRepositoryId, getDate(),
          "Not able to update product tab properties", pConnection);
      BBBPerformanceMonitor.cancel(UPDATE_PRODUCT_TABS_PROP);
      isMonitorCanceled = true;
      // throw new BBBBusinessException(e.getMessage(), e);
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_PRODUCT_TABS_PROP);
      }
    }
  }

  /**
   * This method associateTabsWithProduct the tabs properties
   * 
   * @param pSiteId
   * @param pFeedId
 * @param pProductId 
 * @param pErrorList 
 * @param pConnection 
 * @param isProductionImport 
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public void associateTabsWithProduct(final String pFeedId, final String pProductId, final List<String> pErrorList,
      final Connection pConnection, final boolean isProductionImport) throws BBBSystemException, BBBBusinessException {
    
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_TABS_WITH_PROD);

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateTabsWithProduct]");
      logDebug(" pFeedId[" + pFeedId + "] pProductId[" + pProductId + "] ");
    }
    MutableRepositoryItem productItem = null;
    MutableRepositoryItem productTabItem = null;
    if (isLoggingDebug()) {
      logDebug("In associateTabsWithProduct method : productId= " + pProductId);
    }
    try {

      productItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pProductId,
          BBBCatalogImportConstant.PRODUCT_DESC);

      if (productItem == null) {
        if (isLoggingDebug()) {

          logDebug("Product does not exist in the Repository");
        }
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEM", pProductId, getDate(),
            "Product does not exist in the Repository ", pConnection);
        return;
        // throw new BBBBusinessException(pProductId + " Item not found");
      }

      @SuppressWarnings("unchecked")
      List<MutableRepositoryItem> productTabs = (List<MutableRepositoryItem>) productItem
          .getPropertyValue(BBBCatalogImportConstant.PRODUCT_TABS);

      final List<OperationVO> tabIdsVOList = getPimFeedTools().getProductTabs(pFeedId, pProductId, pConnection);
      if (tabIdsVOList != null && !tabIdsVOList.isEmpty() && productTabs == null) {

        productTabs = new ArrayList<MutableRepositoryItem>();
      }
      productTabItem = addTabsInList(pProductId, pErrorList, productTabItem, productTabs, tabIdsVOList, isProductionImport);
      if (productItem != null) {

        productItem =  ((MutableRepository)getProductCatalog()).getItemForUpdate(pProductId,
            BBBCatalogImportConstant.PRODUCT_DESC);
        productItem.setPropertyValue(BBBCatalogImportConstant.PRODUCT_TABS, productTabs);
        ((MutableRepository)getProductCatalog()).updateItem(productItem);
      }
    } catch (RepositoryException e) {

      pErrorList.add(pProductId);
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_PRODUCT_TABS", pProductId, getDate(),
          "Tab association not done for productId" + pProductId, pConnection);
      BBBPerformanceMonitor.cancel(ASSOCIATE_TABS_WITH_PROD);
      isMonitorCanceled = true;
    } finally {
      
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_TABS_WITH_PROD);
      }
    }

  }

  private MutableRepositoryItem addTabsInList(final String pProductId, final List<String> pErrorList,
      MutableRepositoryItem productTabItem,final  List<MutableRepositoryItem> productTabs,
      final List<OperationVO> tabIdsVOList, final boolean isProductionImport) {
    
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [addTabsInList]");
      logDebug(" pProductId[" + pProductId + "] ");
    }
    for (OperationVO tabVO : tabIdsVOList) {

    	final String tabId = tabVO.getId();
      try {

    	  final String tabRepositoryId = getId(pProductId, tabId);
        final String opcode = tabVO.getOperationCode();

        productTabItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(tabRepositoryId,
            BBBCatalogImportConstant.PRODUCT_TAB_DESC);
        if (productTabItem == null) {

          pErrorList.add(tabRepositoryId + " tab not found in the Repository, Association not done for the same");

          continue;
        }
        if (!StringUtils.isEmpty(opcode) && opcode.equals(BBBCatalogImportConstant.DELETE_CODE)) {
          
          // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
          if(isProductionImport && !productTabs.contains(productTabItem)) {
            
            continue;
          }  
          if (productTabs.contains(productTabItem)) {

            productTabs.remove(productTabItem);
          }
          continue;
        } else if (!productTabs.contains(productTabItem)) {

          productTabs.add(productTabItem);
        }
      } catch (RepositoryException rex) {

        pErrorList.add(rex.getMessage());
        if (isLoggingError()) {

          logError(tabId + " has following issue, return null");
          logError(BBBStringUtils.stack2string(rex));
        }
        if (isLoggingDebug()) {

          logDebug(BBBStringUtils.stack2string(rex));
        }

        BBBPerformanceMonitor.cancel("addTabsInList");
        // isMonitorCanceled = true;
      }
      // finally{

      // if (!isMonitorCanceled)
      // BBBPerformanceMonitor.end(importprocessCatalogLoadTools,
      // "addTabsInList");
      // }
    }
    return productTabItem;
  }

  /**
   * Adding Product Translations
   * 
   * @param pSiteId
   * @param pLocale
   * @param productItem
   * @param pAttributeName
   * @param pAttributeValueString
   * @param pAttributeValueNumber
   * @param pAttributeValueClob
   * @param pAttributeValueBoolean
   */
  private void addProdTranslationAttributes(final String pSiteId, final String pLocale,
      final MutableRepositoryItem productItem, final String pAttributeName, final String pAttributeValueString,
      final double pAttributeValueNumber, final String pAttributeValueClob, final String pAttributeValueBoolean, final java.util.Date pAttributeValueDate) {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [addProdTranslationAttributes]");
      logDebug(" pSiteId[" + pSiteId + "] pLocale[" + pLocale + "] pAttributeName[" + pAttributeName
          + "] pAttributeValueString[" + pAttributeValueString + "] pAttributeValueNumber[" + pAttributeValueNumber
          + "] pAttributeValueClob[" + pAttributeValueClob + "] pAttributeValueBoolean[" + pAttributeValueBoolean + "] pAttributeValueDate[" + pAttributeValueDate + "]");
      logDebug("Adding Product Translation Attributes");

    }
    MutableRepositoryItem prodTranslationItem = null;
    final String productId = productItem.getRepositoryId();
    final String translationId = getTranslationKey(pSiteId, pLocale, pAttributeName, productId);
    try {

      @SuppressWarnings("unchecked")
      Set<MutableRepositoryItem> productTranslations = (Set<MutableRepositoryItem>) productItem
          .getPropertyValue(BBBCatalogImportConstant.PRD_TRANSLATIONS);
      if (productTranslations == null) {

        productTranslations = new HashSet<MutableRepositoryItem>();
      }
      if (isLoggingDebug()) {
        logDebug("Adding for translationId=" + translationId);
        logDebug("productTranslations size=" + productTranslations.size());
      }

      prodTranslationItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(translationId,
          BBBCatalogImportConstant.BBB_PRD_TRANSLATION_DESC);

      if (prodTranslationItem != null) {

        if (isLoggingDebug()) {

          logDebug("Updating existing ProductTranslation Item");
        }
        prodTranslationItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(translationId,
            BBBCatalogImportConstant.BBB_PRD_TRANSLATION_DESC);

        // Populate Current Properties
        updateProdTranslationAttributes(pSiteId, pLocale, pAttributeName, pAttributeValueString, pAttributeValueNumber,
            pAttributeValueClob, pAttributeValueBoolean,pAttributeValueDate, prodTranslationItem);

        ((MutableRepository)getProductCatalog()).updateItem(prodTranslationItem);

      } else {

        // Create a new category item in the versioned repository
        if (isLoggingDebug()) {

          logDebug("Creating new ProductTranslation Item");
        }
        prodTranslationItem = ((MutableRepository)getProductCatalog()).createItem(translationId,
            BBBCatalogImportConstant.BBB_PRD_TRANSLATION_DESC);

        // Populate Current Properties
        updateProdTranslationAttributes(pSiteId, pLocale, pAttributeName, pAttributeValueString, pAttributeValueNumber,
            pAttributeValueClob, pAttributeValueBoolean, pAttributeValueDate,prodTranslationItem);

        ((MutableRepository)getProductCatalog()).addItem(prodTranslationItem);
      }

      if (!productTranslations.contains(prodTranslationItem)) {

        productTranslations.add(prodTranslationItem);
      }

      productItem.setPropertyValue(BBBCatalogImportConstant.PRD_TRANSLATIONS, productTranslations);

      if (isLoggingDebug()) {

        logDebug("Translation added succussfully ");
      }
    } catch (RepositoryException rex) {
      
      if (isLoggingError()) {
        
        logError(translationId + " has following issue, not able to create or update translations");
        logError(BBBStringUtils.stack2string(rex));
      }
    } catch (BBBBusinessException bbbex) {
      if (isLoggingError()) {

        logError(translationId + " has following issue, not able to create or update translations");
        logError(BBBStringUtils.stack2string(bbbex));
      }
    }

  }
  
  /**
   * Delete Product Translations
   * 
   * @param pSiteId
   * @param pLocale
   * @param productItem
   * @param pAttributeName
   * @param pAttributeValueString
   * @param pAttributeValueNumber
   * @param pAttributeValueClob
   * @param pAttributeValueBoolean
   */
  private void deleteProdTranslationAttributes(final String pSiteId, final String pLocale,
      final MutableRepositoryItem productItem, final String pAttributeName, final String pAttributeValueString,
			final double pAttributeValueNumber,
			final String pAttributeValueClob,
			final String pAttributeValueBoolean) {
	  
		if (isLoggingDebug()) {
			logDebug("CatalogTools Method Name [deleteProdTranslationAttributes]");
			logDebug(" pSiteId[" + pSiteId + "] pLocale[" + pLocale
					+ "] pAttributeName[" + pAttributeName
					+ "] pAttributeValueString[" + pAttributeValueString
					+ "] pAttributeValueNumber[" + pAttributeValueNumber
					+ "] pAttributeValueClob[" + pAttributeValueClob
					+ "] pAttributeValueBoolean[" + pAttributeValueBoolean
					+ "]");
			logDebug("Deleting Product Translation Attributes");

		}
		MutableRepositoryItem prodTranslationItem = null;
		final String productId = productItem.getRepositoryId();
		final String translationId = getTranslationKey(pSiteId, pLocale,
				pAttributeName, productId);
		try {

	      	Set<MutableRepositoryItem> productTranslations = (Set<MutableRepositoryItem>) productItem
	                .getPropertyValue(BBBCatalogImportConstant.PRD_TRANSLATIONS);
	      	if (productTranslations == null) {
	            productTranslations = new HashSet<MutableRepositoryItem>();
	        }
	      	
	      	prodTranslationItem = (MutableRepositoryItem) getProductCatalog()
					.getItem(translationId,
							BBBCatalogImportConstant.BBB_PRD_TRANSLATION_DESC);
			if (prodTranslationItem != null) {

				((MutableRepository)getProductCatalog()).removeItem(translationId,
						BBBCatalogImportConstant.BBB_PRD_TRANSLATION_DESC);
				
				if (productTranslations.contains(prodTranslationItem)) {
					productTranslations.remove(prodTranslationItem);
				
					productItem.setPropertyValue(
							BBBCatalogImportConstant.PRD_TRANSLATIONS,
							productTranslations);
				}

				if (isLoggingDebug()) {
					logDebug("Removed for translationId=" + translationId);
					logDebug("Translation item deleted succussfully ");
				}
			}
		} catch (RepositoryException rex) {

			if (isLoggingError()) {

				logError(translationId
						+ " has following issue, not able to delete translation item");
				logError(BBBStringUtils.stack2string(rex));
			}
		}
 }
  
  private void addProdTranslationBooleanAttributes(final String pSiteId, final String pLocale,
	      final MutableRepositoryItem productItem, final String pAttributeName, final String pAttributeValueString,
	      final double pAttributeValueNumber, final String pAttributeValueClob, final String pAttributeValueBoolean, final java.util.Date pAttributeValueDate) {

	    if (isLoggingDebug()) {
	      logDebug("CatalogTools Method Name [addProdTranslationAttributes]");
	      logDebug(" pSiteId[" + pSiteId + "] pLocale[" + pLocale + "] pAttributeName[" + pAttributeName
	          + "] pAttributeValueString[" + pAttributeValueString + "] pAttributeValueNumber[" + pAttributeValueNumber
	          + "] pAttributeValueClob[" + pAttributeValueClob + "] pAttributeValueBoolean[" + pAttributeValueBoolean + "] pAttributeValueDate[" +pAttributeValueDate + "]");
	      logDebug("Adding Product Translation Attributes");

	    }
	    String translationId=null;
	    MutableRepositoryItem prodTranslationItem = null;
	   	 translationId= getBooleanTranslationKey(pSiteId, pLocale, pAttributeName, pAttributeValueBoolean);
	   	final String deleteTranslationId = getBooleanTranslationKeyToRemove(pSiteId, pLocale, pAttributeName, pAttributeValueBoolean);
	  	  try {

	      @SuppressWarnings("unchecked")
	      Set<MutableRepositoryItem> productTranslations = (Set<MutableRepositoryItem>) productItem
	          .getPropertyValue(BBBCatalogImportConstant.PRD_TRANSLATIONS);
	      if (productTranslations == null) {

	        productTranslations = new HashSet<MutableRepositoryItem>();
	      }
	      if (isLoggingDebug()) {
	        logDebug("Adding for translationId=" + translationId);
	        logDebug("productTranslations size=" + productTranslations.size());
	      }
	      prodTranslationItem =(MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(deleteTranslationId,
		          BBBCatalogImportConstant.BBB_PRD_TRANSLATION_DESC);	   
	      if(prodTranslationItem!=null && productTranslations.contains(prodTranslationItem)){
	    	  productTranslations.remove(prodTranslationItem);
	    	  prodTranslationItem=null;
	      }
	      prodTranslationItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(translationId,
	          BBBCatalogImportConstant.BBB_PRD_TRANSLATION_DESC);	      

	      if (prodTranslationItem == null) {
	    	    // Create a new category item in the versioned repository
		        if (isLoggingDebug()) {

		          logDebug("Creating new ProductTranslation Item");
		        }
		        prodTranslationItem = ((MutableRepository)getProductCatalog()).createItem(translationId,
		            BBBCatalogImportConstant.BBB_PRD_TRANSLATION_DESC);

		        // Populate Current Properties
		        updateProdTranslationAttributes(pSiteId, pLocale, pAttributeName, pAttributeValueString, pAttributeValueNumber,
		            pAttributeValueClob, pAttributeValueBoolean, pAttributeValueDate,  prodTranslationItem);

		        ((MutableRepository)getProductCatalog()).addItem(prodTranslationItem);
	      } 

	      if (!productTranslations.contains(prodTranslationItem)) {
	        productTranslations.add(prodTranslationItem);
	      }

	      productItem.setPropertyValue(BBBCatalogImportConstant.PRD_TRANSLATIONS, productTranslations);

	      if (isLoggingDebug()) {

	        logDebug("Translation added succussfully ");
	      }
	    } catch (RepositoryException rex) {
	      
      if (isLoggingError()) {

        logError(translationId + " has following issue, not able to create or update translations");
        logError(BBBStringUtils.stack2string(rex));
      }
    } catch (BBBBusinessException bbbex) {
	      
	      if (isLoggingError()) {

	        logError(translationId + " has following issue, not able to create or update translations");
	        logError(BBBStringUtils.stack2string(bbbex));
	      }
	    }

	  }
  /**
   * Update Product Translation Attributes
   * 
   * @param pSiteId
   * @param pLocale
   * @param pAttributeName
   * @param pAttributeValueStr
   * @param pAttributeValueNumber
   * @param pAttributeValueClob
   * @param pAttributeValueBoolean
   * @param prodTranslationItem
   * @throws BBBBusinessException
   */
  private void updateProdTranslationAttributes(final String pSiteId, final String pLocale, final String pAttributeName,
      final String pAttributeValueStr, final double pAttributeValueNumber, final String pAttributeValueClob,
      final String pAttributeValueBoolean,final java.util.Date pAttributeValueDate,final MutableRepositoryItem prodTranslationItem) throws RepositoryException,
      BBBBusinessException {
    MutableRepositoryItem siteItem = null;

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateProdTranslationAttributes]");
      logDebug(" pSiteId[" + pSiteId + "] pLocale[" + pLocale + "] pAttributeName[" + pAttributeName
          + "] pAttributeValueStr[" + pAttributeValueStr + "] pAttributeValueNumber[" + pAttributeValueNumber
          + "] pAttributeValueClob[" + pAttributeValueClob + "] pAttributeValueBoolean[" + pAttributeValueBoolean + "] pAttributeValueDate[" + pAttributeValueDate + "]");
    }

    final String bbbSiteId = getPimSiteToBBBSiteMap().get(pSiteId);
    if (!StringUtils.isEmpty(bbbSiteId)) {

      siteItem = getSite(bbbSiteId);
    }
    if (siteItem == null) {

    final String pError = pSiteId + "Site Id is not exist in repository";
      if (isLoggingDebug()) {
        logDebug(pError);
        logDebug("Unable to create translation for Attribute" + pAttributeName);
        throw new BBBBusinessException(BBBCoreErrorConstants.VERS_TOOLS_SITE_ID_ERROR,pError);
      }
    }
   simplePropertyWrite(BBBCatalogImportConstant.SITE, siteItem, prodTranslationItem);

    simplePropertyWrite(BBBCatalogImportConstant.LOCALE, pLocale, prodTranslationItem);
    simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_NAME, pAttributeName, prodTranslationItem);

    if (!StringUtils.isEmpty(pAttributeValueStr)) {

      simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_STRING, pAttributeValueStr, prodTranslationItem);
    }
    if (pAttributeValueNumber > 0) {
      simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_NUMBER, Double.valueOf(pAttributeValueNumber), prodTranslationItem);
    }
    if (!StringUtils.isEmpty(pAttributeValueClob)) {
      simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_CLOB, pAttributeValueClob, prodTranslationItem);
    }
    if (!StringUtils.isEmpty(pAttributeValueBoolean)) {
      simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_BOOLEAN, Boolean.valueOf(getBoolean(pAttributeValueBoolean)),
          prodTranslationItem);
    }
    if (pAttributeValueDate !=null) {
        simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_DATE, pAttributeValueDate,
            prodTranslationItem);
      }
    if (isLoggingDebug()) {
      logDebug("updated successfully Product Translation Attributes");
    }
  }

  /**
   * Adding SKU Translations
   * 
   * @param pSiteId
   * @param pLocale
   * @param pSkuItem
   * @param pAttributeName
   * @param pAttributeValueString
   * @param pAttributeValueNumber
   * @param pAttributeValueClob
   * @param pAttributeValueBoolean
   * @param pAttributeValueDate
   */
  private void addSkuTranslationAttributes(final String pSiteId, final String pLocale,
      final MutableRepositoryItem pSkuItem, final String pAttributeName, final String pAttributeValueString,
      final double pAttributeValueNumber, final String pAttributeValueClob, final String pAttributeValueBoolean, final String pOperationFlag, final java.util.Date pAttributeValueDate) {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [addSkuTranslationAttributes]");
      logDebug(" pSiteId[" + pSiteId + "] pLocale[" + pLocale + "] pAttributeName[" + pAttributeName
          + "] pAttributeValueString[" + pAttributeValueString + "] pAttributeValueNumber[" + pAttributeValueNumber
          + "] pAttributeValueClob[" + pAttributeValueClob + "] pAttributeValueBoolean[" + pAttributeValueBoolean + "]"+"pOperationFlag"+pOperationFlag);
    }
    MutableRepositoryItem skuTranslationItem = null;
    final String skuId = pSkuItem.getRepositoryId();
    final String translationId = getTranslationKey(pSiteId, pLocale, pAttributeName, skuId);
    @SuppressWarnings("unchecked")
    Set<MutableRepositoryItem> skuTranslations = (Set<MutableRepositoryItem>) pSkuItem
        .getPropertyValue(BBBCatalogImportConstant.SKU_TRANSLATIONS);
    if (skuTranslations == null) {

      skuTranslations = new HashSet<MutableRepositoryItem>();
    }
    if (isLoggingDebug()) {
      logDebug("Adding for translationId=" + translationId);
      logDebug("skuTranslations size=" + skuTranslations.size());
    }
    try {

      skuTranslationItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(translationId,
          BBBCatalogImportConstant.BBB_SKU_TRANSLATION_DESC);

      if(pOperationFlag.equalsIgnoreCase(BBBCatalogImportConstant.DELETE_CODE)) {
        
        if(skuTranslations !=null && skuTranslations.contains(skuTranslationItem)) {
         
          skuTranslations.remove(skuTranslationItem);
          pSkuItem.setPropertyValue(BBBCatalogImportConstant.SKU_TRANSLATIONS, skuTranslations);
          
        }
        return;
      }
        
      if (skuTranslationItem != null) {

        if (isLoggingDebug()) {

          logDebug("Updating existing skuTranslation Item");
        }
       
        skuTranslationItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(translationId,
            BBBCatalogImportConstant.BBB_SKU_TRANSLATION_DESC);

        // Populate Current Properties
		/** Updates Site Specific Enable Date for R2.1 #53 Endeca & PIM FEED, passing new param pAttributeValueDate for web enabled date*/
        updateSkuTranslationAttributes(pSiteId, pLocale, pAttributeName, pAttributeValueString, pAttributeValueNumber,
            pAttributeValueClob, pAttributeValueBoolean, pAttributeValueDate,skuTranslationItem);

        ((MutableRepository)getProductCatalog()).updateItem(skuTranslationItem);

      } else {

        // Create a new category item in the versioned repository
        if (isLoggingDebug()) {

          logDebug("Creating new skuTranslation Item");
        }
        skuTranslationItem = ((MutableRepository)getProductCatalog()).createItem(translationId,
            BBBCatalogImportConstant.BBB_SKU_TRANSLATION_DESC);

        // Populate Current Properties
		/** Updates Site Specific Enable Date for R2.1 #53 Endeca & PIM FEED, passing new param pAttributeValueDate for web enabled date*/
        updateSkuTranslationAttributes(pSiteId, pLocale, pAttributeName, pAttributeValueString, pAttributeValueNumber,
            pAttributeValueClob, pAttributeValueBoolean,pAttributeValueDate, skuTranslationItem);

        ((MutableRepository)getProductCatalog()).addItem(skuTranslationItem);
      }

      
      if (!skuTranslations.contains(skuTranslationItem)) {

        skuTranslations.add(skuTranslationItem);
      }

      pSkuItem.setPropertyValue(BBBCatalogImportConstant.SKU_TRANSLATIONS, skuTranslations);

      if (isLoggingDebug()) {

        logDebug("Translation added succussfully ");
      }
    } catch (RepositoryException rex) {
      if (isLoggingError()) {

        logError(translationId + " has following issue, not able to create or update translations");
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }
    } catch (BBBBusinessException bbbex) {
      if (isLoggingError()) {

        logError(translationId + " has following issue, not able to create or update translations");
        logError(BBBStringUtils.stack2string(bbbex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(bbbex));
      }
    }

  }  
    
  /**
   * Adding SKU Translations Boolean Attributes
   * 
   * @param pSiteId
   * @param pLocale
   * @param pSkuItem
   * @param pAttributeName
   * @param pAttributeValueString
   * @param pAttributeValueNumber
   * @param pAttributeValueClob
   * @param pAttributeValueBoolean
   * @param pAttributeValueDate
   */
  
  private void addSkuTranslationBooleanAttributes(final String pSiteId, final String pLocale,
	      final MutableRepositoryItem pSkuItem, final String pAttributeName, final String pAttributeValueString,
	      final double pAttributeValueNumber, final String pAttributeValueClob, final String pAttributeValueBoolean, final java.util.Date pAttributeValueDate) {

	    if (isLoggingDebug()) {
	      logDebug("CatalogTools Method Name [addSkuTranslationAttributes]");
	      logDebug(" pSiteId[" + pSiteId + "] pLocale[" + pLocale + "] pAttributeName[" + pAttributeName
	          + "] pAttributeValueString[" + pAttributeValueString + "] pAttributeValueNumber[" + pAttributeValueNumber
	          + "] pAttributeValueClob[" + pAttributeValueClob + "] pAttributeValueBoolean[" + pAttributeValueBoolean + "] pAttributeValueDate[" + pAttributeValueDate + "]");
	    }
	    MutableRepositoryItem skuTranslationItem = null;
	    final String translationId = getBooleanTranslationKey(pSiteId, pLocale, pAttributeName,pAttributeValueBoolean);
	    final String deleteTranslationId = getBooleanTranslationKeyToRemove(pSiteId, pLocale, pAttributeName,pAttributeValueBoolean);
	 
	   	 /* String translationId = getTranslationKey(pSiteId, pLocale, pAttributeName, skuId);*/
	    @SuppressWarnings("unchecked")
	    Set<MutableRepositoryItem> skuTranslations = (Set<MutableRepositoryItem>) pSkuItem
	        .getPropertyValue(BBBCatalogImportConstant.SKU_TRANSLATIONS);
	    
	    if (skuTranslations == null) {

	      skuTranslations = new HashSet<MutableRepositoryItem>();
	    }
	    if (isLoggingDebug()) {
	      logDebug("Adding for translationId=" + translationId);
	      logDebug("skuTranslations size=" + skuTranslations.size());
	    }
	    try {
	       skuTranslationItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(deleteTranslationId,
	    	          BBBCatalogImportConstant.BBB_SKU_TRANSLATION_DESC);
	       if (skuTranslationItem!=null && skuTranslations.contains(skuTranslationItem)) {
		        skuTranslations.remove(skuTranslationItem);
		        skuTranslationItem=null;
		      }
	      skuTranslationItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(translationId,
	   	          BBBCatalogImportConstant.BBB_SKU_TRANSLATION_DESC);
	    
	      if (skuTranslationItem == null) {
	    	  
	   	        // Create a new category item in the versioned repository
	        if (isLoggingDebug()) {

	          logDebug("Creating new skuTranslation Item");
	        }
	        skuTranslationItem = ((MutableRepository)getProductCatalog()).createItem(translationId,
	            BBBCatalogImportConstant.BBB_SKU_TRANSLATION_DESC);

	        // Populate Current Properties
			/** Updates Site Specific Enable Date for R2.1 #53 Endeca & PIM FEED, passing new param pAttributeValueDate for web enabled date*/
	        updateSkuTranslationAttributes(pSiteId, pLocale, pAttributeName, pAttributeValueString, pAttributeValueNumber,
	            pAttributeValueClob, pAttributeValueBoolean, pAttributeValueDate, skuTranslationItem);

	        ((MutableRepository)getProductCatalog()).addItem(skuTranslationItem);
	      }

	      if (!skuTranslations.contains(skuTranslationItem)) {

	        skuTranslations.add(skuTranslationItem);
	      }

	      pSkuItem.setPropertyValue(BBBCatalogImportConstant.SKU_TRANSLATIONS, skuTranslations);

	      if (isLoggingDebug()) {

	        logDebug("Translation added succussfully ");
	      }
	    } catch (RepositoryException rex) {
	      if (isLoggingError()) {

	        logError(translationId + " has following issue, not able to create or update translations");
	        logError(BBBStringUtils.stack2string(rex));
	      }
	      if (isLoggingDebug()) {

	        logDebug(BBBStringUtils.stack2string(rex));
	      }
	    } catch (BBBBusinessException bbbex) {
	      if (isLoggingError()) {

	        logError(translationId + " has following issue, not able to create or update translations");
	        logError(BBBStringUtils.stack2string(bbbex));
	      }
	      if (isLoggingDebug()) {

	        logDebug(BBBStringUtils.stack2string(bbbex));
	      }
	    }

	  }
  /**
   * Update Product Translation Attributes
   * 
   * @param pSiteId
   * @param pLocale
   * @param pAttributeName
   * @param pAttributeValueStr
   * @param pAttributeValueNumber
   * @param pAttributeValueClob
   * @param pAttributeValueBoolean
   * @param pAttributeValueDate
   * @param skuTranslationItem
   * @throws BBBBusinessException
   */
  private void updateSkuTranslationAttributes(final String pSiteId, final String pLocale, final String pAttributeName,
      final String pAttributeValueStr, final double pAttributeValueNumber, final String pAttributeValueClob,
      final String pAttributeValueBoolean, final java.util.Date pAttributeValueDate,final MutableRepositoryItem skuTranslationItem) throws RepositoryException,
      BBBBusinessException {

    MutableRepositoryItem siteItem = null;
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateSkuTranslationAttributes]");
      logDebug(" pSiteId[" + pSiteId + "] pLocale[" + pLocale + "] pAttributeName[" + pAttributeName
          + "] pAttributeValueStr[" + pAttributeValueStr + "] pAttributeValueNumber[" + pAttributeValueNumber
          + "] pAttributeValueClob[" + pAttributeValueClob + "] pAttributeValueBoolean[" + pAttributeValueBoolean + "] pAttributeValueDate[" + pAttributeValueDate + "]");
    }
    final String bbbSiteId = getPimSiteToBBBSiteMap().get(pSiteId);
    if (!StringUtils.isEmpty(bbbSiteId)) {
      siteItem = getSite(bbbSiteId);
    }

    if (siteItem == null) {
      final String pError = pSiteId + "Site Id is not exist in repository";
      if (isLoggingDebug()) {
        logDebug(pError);
        logDebug("Unable to create translation for Attribute" + pAttributeName);
        throw new BBBBusinessException(BBBCoreErrorConstants.VERS_TOOLS_SITE_ID_ERROR,pError);
      }
    }
    simplePropertyWrite(BBBCatalogImportConstant.SITE, siteItem, skuTranslationItem);

    simplePropertyWrite(BBBCatalogImportConstant.LOCALE, pLocale, skuTranslationItem);
    simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_NAME, pAttributeName, skuTranslationItem);

    if (!StringUtils.isEmpty(pAttributeValueStr)) {

      simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_STRING, pAttributeValueStr, skuTranslationItem);
    }
    if (pAttributeValueNumber > 0) {

      simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_NUMBER, Double.valueOf(pAttributeValueNumber), skuTranslationItem);
    }
    if (!StringUtils.isEmpty(pAttributeValueClob)) {

      simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_CLOB, pAttributeValueClob, skuTranslationItem);
    }
    if (!StringUtils.isEmpty(pAttributeValueBoolean)) {

      simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_BOOLEAN, Boolean.valueOf(getBoolean(pAttributeValueBoolean)),
          skuTranslationItem);
    }
	/**Checking for Date Type Attribute Value R2.1 #53 Endeca & PIM FEED */
    if ( pAttributeValueDate != null) {

        simplePropertyWrite(BBBCatalogImportConstant.ATTRIBUTE_VALUE_DATE, pAttributeValueDate,
            skuTranslationItem);
      }
    if (isLoggingDebug()) {
      logDebug("updated successfully Product Translation Attributes");
    }
  }

  /**
   * This method associates products with categories for provided pCategoryId,
   * pFeedId and pSiteId
   * 
   * @param pSiteId
   * @param pCategoryId
   * @param pFeedId
 * @param pErrorList 
 * @param pConnection 
 * @param isProductionImport 
   * @param pBadCategorySubcategoryRelationShip
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  public void associateProductsWithCategory(final String pSiteId, final String pCategoryId, final String pFeedId,
		  final List<String> pErrorList, final Connection pConnection, final boolean isProductionImport) throws BBBSystemException, BBBBusinessException {
	    boolean isMonitorCanceled = false;
	    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_PRODUCTS_WITH_CATEGORY);
	    if (isLoggingDebug()) {
	      logDebug("CatalogTools Method Name [associateProductsWithCategory]");
	      logDebug(" pSiteId[" + pSiteId + "] pCategoryId[" + pCategoryId + "] pFeedId[" + pFeedId + "]");
	    }
	    final String categoryRepositoryId = pCategoryId;// getCategoryId(pSiteId,
	                                                    // pCategoryId);
	    final MutableRepository catalogRepository = (MutableRepository)getProductCatalog();
	    if(isLoggingDebug()){
	       logDebug("      catalogRepository = " + catalogRepository);
	    }
	    final List<OperationVO> childProductsVOList = getPimFeedTools().getChildProducts(pFeedId, pSiteId, pCategoryId,
	        pConnection);
	    if (isLoggingDebug()) {
	      logDebug("for CategoryId=" + categoryRepositoryId + "childProductsVOList size=" + childProductsVOList.size());
	      logDebug("for CategoryId=" + categoryRepositoryId + "childProductsVOList contents=" + childProductsVOList);
	    }
	    try {

	      // getItem() - To find the category into the version repository
	      MutableRepositoryItem categoryItem = (MutableRepositoryItem) catalogRepository.getItem(categoryRepositoryId,
	          BBBCatalogImportConstant.CATEGORY_DESC);
	      

	      if (categoryItem == null) {
	        if (isLoggingDebug()) {

	          logDebug("associateProductsWithCategory Category item not available into the version repository");
	        }
	        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODES_SKU", pCategoryId, getDate(),
	            "Category Item not available in the Rpository for siteId=" + pSiteId, pConnection);
	        return;
	      }
	      
	      // START --- Primary Node ID changes
	      final Set<String> siteIds = (Set<String>) categoryItem.getPropertyValue(BBBCatalogImportConstant.SITE_IDS);
	      
	      // In our case there will be only one SiteId associated with a Category so this break statement will not have any impact so adding NOPMD
	      String siteId = null;
	      for(String site : siteIds){
	    	  siteId = site;
	    	  break; //NOPMD
	      }
	      // END   --- Primary Node ID changes
	      
	      
	      @SuppressWarnings("unchecked")	      
	      final List<RepositoryItem> childProductList = (List<RepositoryItem>) categoryItem
	          .getPropertyValue(BBBCatalogImportConstant.FIXED_CHILD_PRODUCTS);

	      if (isLoggingDebug()) {

	        logDebug("childProductList :" + childProductList);
	      }
	      
	      //Retrieve product category sequence mapping /////////LOGIC CHANGED || BPSI-4027
	      Map<String, String> productCatSeqNumMap = new HashMap<String, String>();
	      
	      if(isPopulateOldCatProdSeqTable()){
	    	       productCatSeqNumMap =  (Map<String,String>)categoryItem.getPropertyValue(BBBCatalogImportConstant.CATEGORY_PROD_SEQ_MAP);
	    	       if(productCatSeqNumMap ==null){
	    	    	   productCatSeqNumMap = new HashMap<String, String>();
	    	       }
	      }
	      
	      MutableRepositoryItem catItem = null;

	      for (OperationVO productVO : childProductsVOList) {

	    	  final String productId = productVO.getId();
	    	  final String opcode = productVO.getOperationCode();
	    	  final String primaryNodeId = productVO.getPrimaryNodeId();
	    	  final String productSeqNumber = productVO.getProductSeqNumber();

	        if (isLoggingDebug()) {

	          logDebug(" From childProductsVOList using for loop, this  productId is :" + productId);
	        }
	        
	        if(isLoggingDebug()){
	           logDebug("      C product - ((MutableRepository)getProductCatalog()).getItem() Start,  date=" + formatDate(getDate()) + ", feedId = " + pFeedId +  ", siteId = " + pSiteId + ", productId =" + productId + ", categoryId="  + pCategoryId);
	        }
	        MutableRepositoryItem productItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(productId,
	            BBBCatalogImportConstant.PRODUCT_DESC);

	        if (productItem == null) {

	          if (isLoggingDebug()) {

	            logDebug("associateProductsWithCategory Product not Available in the repository. Bad Relationship");
	            logDebug("associateProductsWithCategory Operation Code=" + opcode);
	          }
	          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODES_SKU", pCategoryId + "-" + productId, getDate(),
	              "Product Item not available in the Repository.Bad Relationship for siteId=" + pSiteId, pConnection);
	          continue;
	        }
	        
	        if (!StringUtils.isEmpty(opcode) && opcode.equals(BBBCatalogImportConstant.DELETE_CODE)) {
	        	if(isLoggingDebug()){
	        		logDebug("associateProductsWithCategory - Processing DELETE operation flag");
	        		logDebug("associateProductsWithCategory Calling updateLastModifiedDate(): Method ");
	        	}
	        	getCategoryChildProdUpdateRequiredList().add(productItem);
	          if (childProductList.contains(productItem) ) {

	            if (isLoggingDebug()) {

	              logDebug("associateProductsWithCategory Product is removed ");
	            }

	            childProductList.remove(productItem);
	            
	            //remove product and category sequence mapping | logic changed BPSI-4027
	            // old logic for multi type cat_product_seq table
	            if(isPopulateOldCatProdSeqTable()){
	            productCatSeqNumMap.remove(productId);
	            }
	            if(isUseNewProductCategorySeq()){
		            // new remove logic for new table | BPSI-4027
		            //catalogRepository.removeItem(categoryRepositoryId+":"+productVO.getId(), BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC);
	            	String repoItemId = categoryRepositoryId + ":" + productId;
					RepositoryItem item = catalogRepository.getItem(repoItemId, BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC);
					if(item!=null){
						catalogRepository.removeItem(item.getRepositoryId(), item.getItemDescriptor().getItemDescriptorName());
					}else{
						logError("Delete OPCode was encountered for item when there is no item :- productId" + productId + " categoryId:-" + categoryRepositoryId);
					}
	            }else if(!isPopulateOldCatProdSeqTable()){
	            	logError("Data is not getting populdated in Old Category and New Category sequences.");
	            }
	            
	            
	            // START --- Primary Node ID changes RM -18135
	            productItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(productId,
	                  BBBCatalogImportConstant.PRODUCT_DESC);
	           
	            final Set<RepositoryItem> pList = (Set<RepositoryItem>) productItem.getPropertyValue(BBBCatalogImportConstant.PRD_TRANSLATIONS);
	            final Set<RepositoryItem> pTempList = (Set<RepositoryItem>) productItem.getPropertyValue(BBBCatalogImportConstant.PRD_TRANSLATIONS);
	           
	          if(null != pList){
	          	if (isLoggingDebug()) {
	          		logDebug("associateProductsWithCategory Product have some translations.");
		            }
	          	for(RepositoryItem pItem: pList){
	          		final String attributeName = (String) pItem.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME_SITE_TRANS_PROPERTY_NAME);
	          		final String attributeStringValue = (String) pItem.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING_SITE_TRANS_PROPERTY_NAME);
	                    if(!BBBUtility.isEmpty(attributeName) 
	                                 && attributeName.trim().equalsIgnoreCase(BBBCatalogConstants.PRIMARY_PARENT_CATEGORY) 
	                                 && !BBBUtility.isEmpty(attributeStringValue)){
	                  	  
	                  	  	if (isLoggingDebug()) {
	                  	  		logDebug("associateProductsWithCategory Product have category Id: "+ attributeStringValue.trim() + "defined as Primary category Id and current category is : " + categoryRepositoryId);
	          	            }
	                          if(categoryRepositoryId.equalsIgnoreCase(attributeStringValue.trim())){
	                          	pTempList.remove(pItem);
	                              if (isLoggingDebug()) {
	                        	  		logDebug("associateProductsWithCategory Product have this category Id defined as Primary category Id.Hence need to be removed.");
	                	            }
	                          }
	                    }
	          	}
	          }
	          
	          if(null != pTempList && pTempList.size() < pList.size()){
	          	productItem.setPropertyValue(BBBCatalogImportConstant.PRD_TRANSLATIONS, pTempList);
	          	catalogRepository.updateItem(productItem);
	                if (isLoggingDebug()) {
	          	  		logDebug("associateProductsWithCategory Product have this category Id defined as Primary category Id. Removed such Product translation.");
	  	            }
	          }
	          // END --- Primary Node ID changes RM -18135

	          } else {
	            // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
	            if(isProductionImport) {
	              
	              continue;
	            }  

	            if (isLoggingDebug()) {

	              logDebug("associateProductsWithCategory Product not associated with the Category");
	              logDebug("associateProductsWithCategory  Unable to delete association");
	            }
	            pErrorList.add(productId);
	            getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODE_SKU", pCategoryId + "-" + productId,
	                getDate(), "Product not associated with the Category. Unable to delete association", pConnection);

	          }
	          continue;
	        } else {
	        	logDebug("associateProductsWithCategory Operation Code is neither empty nor delete too =" + opcode);
	        	String existingProductSeqNumber = null;
	        	if(isUseNewProductCategorySeq()){
	    	        //BPSI-4027 | in case opcode is add or insert will, modify the sequence number with current value 
		           //catItem = (MutableRepositoryItem) catalogRepository.getItem(categoryRepositoryId+":"+productVO.getId(), BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC);
		           String repoItemId = categoryRepositoryId + ":" + productId;
		           if(catalogRepository instanceof VersionRepository){
		        	 //PUB Instance
			           VersionRepository versionedRepository = (VersionRepository) catalogRepository;
			           Workspace workspace = this.getCatalogImportManager().getWorkspace();
			           if(workspace!=null){
				           if(versionedRepository.getVersionItem(repoItemId, BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC, workspace)!=null){
				        	   RepositoryVersionItem currentItem = versionedRepository.getVersionItem(repoItemId, BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC, workspace);
				        	   //currentItem is a replica of the latest asset with asset_version incremented. 
				        	   if(!currentItem.isDeleted()){
				        		   //If currentItem is not deleted, it is accessible through MutableRepository API.
				        		   //So, invoke getItemForUpdate() to retrieve the latest asset.
				        		   MutableRepositoryItem repositoryItem = catalogRepository.getItemForUpdate(repoItemId, "bbbCatProductSeq");
				        		   repositoryItem.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_CATEGORY_ID_PROPERTY, categoryRepositoryId);
				        		   repositoryItem.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_PRODUCT_ID_PROPERTY, productVO.getId());
				        		   repositoryItem.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_SEQUENCE_NUM, productVO.getProductSeqNumber());
				        		   catalogRepository.updateItem(repositoryItem);
				        	   }else{
				        		   //If currentItem is deleted, either the latest asset is in deleted state or the the association was removed in this project.
				        		   //Retrieve the latest item in the workspace, update with the latest values and set the version_deleted to be false.
				        		   RepositoryWorkingVersionItem workingItem = versionedRepository.checkOut(repoItemId, BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC, workspace);
				        		   workingItem.setDeleted(false);
				        		   workingItem.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_CATEGORY_ID_PROPERTY, categoryRepositoryId);
						           workingItem.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_PRODUCT_ID_PROPERTY, productVO.getId());
						           workingItem.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_SEQUENCE_NUM, productVO.getProductSeqNumber());
						           versionedRepository.updateItem(workingItem);
				        	   }
				           }else{
				        	   //If there are no assets for the association, create an asset with version 1.
				        	   MutableRepositoryItem repositoryItem = catalogRepository.createItem(repoItemId,BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC);
			        		   repositoryItem.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_CATEGORY_ID_PROPERTY, categoryRepositoryId);
			        		   repositoryItem.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_PRODUCT_ID_PROPERTY, productVO.getId());
			        		   repositoryItem.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_SEQUENCE_NUM, productVO.getProductSeqNumber());
			        		   catalogRepository.addItem(repositoryItem);
				           }
			           }
		           }else{
		        	 //Staging Instance
			           MutableRepositoryItem item = null;
		           RepositoryItem repItem = this.getProductCatalog().getItem(repoItemId, BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC);
		           if(repItem == null){
			        	   item = catalogRepository.createItem(repoItemId, BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC);
		        	   item.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_CATEGORY_ID_PROPERTY, categoryRepositoryId);
		        	   item.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_PRODUCT_ID_PROPERTY, productVO.getId());
		        	   item.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_SEQUENCE_NUM, productVO.getProductSeqNumber());
		        	   catalogRepository.addItem(item);
		           }else{
		        	   item = catalogRepository.getItemForUpdate(repoItemId, BBBCatalogImportConstant.CAT_PRODUCT_SEQ_DESC);
		        	   existingProductSeqNumber = (String) item.getPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_SEQUENCE_NUM);
		        	   if(!productSeqNumber.equalsIgnoreCase(existingProductSeqNumber)){
		        		   item.setPropertyValue(BBBCatalogLoadTools.CAT_PRD_SEQ_SEQUENCE_NUM, productSeqNumber);
		        		   catalogRepository.updateItem(item);
		        	   }
		           }
		           }
	        	}else if(!isPopulateOldCatProdSeqTable()){
	        		logError("Data is not getting populdated in Old Category and New Category sequences.");
	        	}
		           
	           if(isPopulateOldCatProdSeqTable())
	           {
	        	   existingProductSeqNumber =productCatSeqNumMap.get(productId);
		           if(isLoggingDebug()){    
		        	   logDebug("   existingProductSeqNumber = " + existingProductSeqNumber);
		           }
	        	  //in case opcode is add or insert will, modify the sequence number with current value 
	        	productCatSeqNumMap.put(productId,productSeqNumber);
		        }


	        	  if (!childProductList.contains(productItem)) {
	        		  if(isLoggingDebug()){
	        		  logDebug("associateProductsWithCategory Product not already associated with the Category");
	        		  }
	        		  childProductList.add(productItem);
	        	  } else {
	        		    if (isLoggingDebug()) {			            	
			              logDebug("associateProductsWithCategory Product already associated with the Category, doing nothing, pFeedId= " + pFeedId +  ", siteId = " + pSiteId + ", productId =" + productId + ", categoryId="  + pCategoryId);
			            }
	        	  }
	        	  // START --- Primary Node ID changes
	        	  if(BBBUtility.isNotEmpty(primaryNodeId)){
	        		  	if (siteId == null) {
	        		        if (isLoggingDebug()) {
	        		        	logDebug("associateProductsWithCategory  Category item is not associated with any site into the latest version repository under DCS_CATEGORY_SITES table, pFeedId= " + pFeedId +  ", siteId = " + pSiteId + ", productId =" + productId + ", categoryId="  + pCategoryId);
	        		        }
	        		        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODES_SKU", pCategoryId, getDate(),
	        		            "Category item is not associated with this site into the latest version repository under DCS_CATEGORY_SITES table: pFeedId=" + pFeedId +  ", siteId = " + pSiteId + ", productId =" + productId + ", categoryId="  + pCategoryId, pConnection);
	        		        if(isLoggingDebug()){
	        		        	logDebug("RETURNING for categoryId=" + pCategoryId + ", feedId=" + pFeedId);
	        		        }
	        		        return;
	        		    }
	        		  	    logDebug("associateProductsWithCategory  Primary Category id: "+primaryNodeId +" -- Site id:"+siteId);
							String siteCode = productVO.getSiteIds();
							
							productItem =  ((MutableRepository)getProductCatalog()).getItemForUpdate(productId,
									BBBCatalogImportConstant.PRODUCT_DESC);
							if (isLoggingDebug()) {
							   logDebug("      Start addProdTranslationAttributes()");
							}
							if(siteCode != null && siteCode.equalsIgnoreCase(BBBCatalogImportConstant.BBB_SITE_ID)) {
								if (isLoggingDebug()) {
								logDebug("      Adding primaryCategoryIdDefault :: "
										+ primaryNodeId
										+ " for productItem :: "
										+ productItem.getRepositoryId()
										+ "  for BedBathUS site");
									}
								simplePropertyWrite(BBBCatalogImportConstant.PRIMARY_CATEGORY_ID_DEFAULT, primaryNodeId, productItem);
							} else {
							addProdTranslationAttributes(siteCode, BBBCatalogImportConstant.EN_US, productItem,
								  BBBCatalogImportConstant.PRIMARY_CATEGORY_ID, primaryNodeId, 0.0, null, null,null);
							}
							if (isLoggingDebug()) {
							   logDebug("      End addProdTranslationAttributes()");
							}
						
	        	  	}
	        	  catalogRepository.updateItem(productItem);
	        	  	// END   --- Primary Node ID changes
	        	}
	      	}
	      // add child products to current category
	      // getItemForUpdate  get a write lock on the latest version.
	      categoryItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(categoryRepositoryId,
	          BBBCatalogImportConstant.CATEGORY_DESC);

	      categoryItem.setPropertyValue(BBBCatalogImportConstant.FIXED_CHILD_PRODUCTS, childProductList);
	      
	      if(isPopulateOldCatProdSeqTable()){
	      // for sequence
	      categoryItem.setPropertyValue(BBBCatalogImportConstant.CATEGORY_PROD_SEQ_MAP,productCatSeqNumMap);
	      }
	      
	      catalogRepository.updateItem(categoryItem);

	    } catch (RepositoryException rex) {

	      if (isLoggingError()) {
	        logError(BBBStringUtils.stack2string(rex));
	      }
	      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_NODES_SKU", categoryRepositoryId, getDate(),
	          rex.getMessage(), pConnection);
	      BBBPerformanceMonitor.cancel(ASSOCIATE_PRODUCTS_WITH_CATEGORY);
	      isMonitorCanceled = true;
	    } finally {
	      if (!isMonitorCanceled) {
	        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS,
	            ASSOCIATE_PRODUCTS_WITH_CATEGORY);
	      }
	    }
	  }
  
  @SuppressWarnings("unchecked")
  public void updateLastModifiedDateOfChildProducts(Set<String> products) {
	  try {
		  for(String productId: products){
			  RepositoryItem productItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(productId,BBBCatalogImportConstant.PRODUCT_DESC);
		  }
	  } catch (RepositoryException e) {
		  logError("Error occurred while updating lastModifiedDate of child products.", e);
	  }
  }


@SuppressWarnings("unchecked")
	public void updateLastModifiedDate(RepositoryItem productItem) {

		try {
			List<RepositoryItem> childSKUS = null;
			productItem = ((MutableRepository)getProductCatalog())
					.getItemForUpdate(productItem.getRepositoryId(),
							BBBCatalogImportConstant.PRODUCT_DESC);
			
            if(!isUpdateLastModifiedDateOfChildItems())
			{
				return;
			}
			boolean isCollection = false;
			if(productItem
					.getPropertyValue(BBBCatalogImportConstant.COLLECTION)!=null ){
			isCollection =Boolean.parseBoolean(productItem
					.getPropertyValue(BBBCatalogImportConstant.COLLECTION).toString());
			}
			if (isCollection) {
				List<RepositoryItem> childProducts = null;
				childProducts = (List<RepositoryItem>) productItem
						.getPropertyValue(BBBCatalogImportConstant.PRODUCT_CHILD_PRODUCTS);
				if (childProducts != null && !childProducts.isEmpty()) {
					for (RepositoryItem pChildProductItem : childProducts) {
						String producId=null;
						if((RepositoryItem)pChildProductItem.getPropertyValue("productId")!=null){
							producId=((RepositoryItem)pChildProductItem.getPropertyValue("productId")).getRepositoryId();
						}
						if(producId!=null){
						pChildProductItem = ((MutableRepository)getProductCatalog())
								.getItemForUpdate(producId,
										BBBCatalogImportConstant.PRODUCT_DESC);
						
						childSKUS = (List<RepositoryItem>) pChildProductItem
								.getPropertyValue(BBBCatalogImportConstant.CHILD_SKUS);

							if (childSKUS != null && !childSKUS.isEmpty()) {
								for (RepositoryItem skuItem : childSKUS) {
									skuItem = ((MutableRepository)getProductCatalog())
											.getItemForUpdate(
													skuItem.getRepositoryId(),
													BBBCatalogImportConstant.SKU_DESC);
									// BBBSL-5187 | Commented out to avoid the update to price objects during regular PIM import leading to assets conflicts during Pricing Feed import. 
									/*Object[] params = new Object[1];
									params[0] = skuItem.getRepositoryId();
									RepositoryItem[] priceItems = null;
									priceItems = this
											.executeRQLQuery(
													BBBCatalogImportConstant.QUERY_SKU_ID,
													params,
													BBBCatalogImportConstant.PRICE2,
													getPriceListManager()
															.getPriceListRepository());
									if (priceItems != null
											&& priceItems.length > 0) {
										for (RepositoryItem priceItem : priceItems) {
											priceItem = getPriceListManager()
													.getPriceListRepository()
													.getItemForUpdate(
															priceItem
																	.getRepositoryId(),
															BBBCatalogImportConstant.PRICE2);
										}
									}*/
									}
								}
							}
						}
					}

				childSKUS = new ArrayList<RepositoryItem>();
				/*if (productItem
						.getPropertyValue(BBBCatalogImportConstant.SKU_HIGH_PRICE) != null) {
					childSKUS
							.add((RepositoryItem) productItem
									.getPropertyValue(BBBCatalogImportConstant.SKU_HIGH_PRICE));
				}
				if (productItem
						.getPropertyValue(BBBCatalogImportConstant.SKU_LOW_PRICE) != null) {
					childSKUS
							.add((RepositoryItem) productItem
									.getPropertyValue(BBBCatalogImportConstant.SKU_LOW_PRICE));
				}*/
			} else {

				childSKUS = (List<RepositoryItem>) productItem
						.getPropertyValue(BBBCatalogImportConstant.CHILD_SKUS);
			}
			if (childSKUS != null && !childSKUS.isEmpty()) {
				for (RepositoryItem skuItem : childSKUS) {
					skuItem = ((MutableRepository)getProductCatalog())
							.getItemForUpdate(skuItem.getRepositoryId(),
									BBBCatalogImportConstant.SKU_DESC);
					// BBBSL-5187 | Commented out to avoid the update to price objects during regular PIM import leading to assets conflicts during Pricing Feed import. 
					/*Object[] params = new Object[1];
					params[0] = skuItem.getRepositoryId();
					RepositoryItem[] priceItems = null;
					priceItems = this.executeRQLQuery(
							BBBCatalogImportConstant.QUERY_SKU_ID, params,
							BBBCatalogImportConstant.PRICE2,
							getPriceListManager().getPriceListRepository());
					if (priceItems != null && priceItems.length > 0) {
						for (RepositoryItem priceItem : priceItems) {
							priceItem = getPriceListManager()
									.getPriceListRepository().getItemForUpdate(
											priceItem.getRepositoryId(),
											BBBCatalogImportConstant.PRICE2);
						}
					}*/
					}
				}

		} catch (RepositoryException rex) {

			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(rex));
			}
		} 
		/*catch (BBBSystemException bbbse) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(bbbse));
			}
		} catch (BBBBusinessException bbbbe) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(bbbbe));
			}
		}*/
}

	/**
	 * This method will update the lastModifiedDate of the SKU and its
	 * translations of Product. Purpose of this method to fix the partial feed
	 * issue for lead products and simple products swatch images
	 * 
	 * @param productItem
	 */
	public void updateLastModifiedDateForChildSkus(final RepositoryItem productItem) {

  
		try {
			// Updating last modified data child SKUS
			if (isLoggingDebug()) {
				logDebug("updateLastModifiedDateForChildSkus() : Method | Updating last modified date for child sku and translations of Product ID:"
						+ productItem.getRepositoryId());
			}
				if (productItem
						.getPropertyValue(BBBCatalogImportConstant.CHILD_SKUS) != null) {
					@SuppressWarnings("unchecked")
					final List<RepositoryItem> pChildSkus = (List<RepositoryItem>) productItem
							.getPropertyValue(BBBCatalogImportConstant.CHILD_SKUS);
					if (pChildSkus != null && !pChildSkus.isEmpty()) {
						for (RepositoryItem skuItem : pChildSkus) {
							if (isLoggingDebug()) {
								logDebug("updateLastModifiedDateForChildSkus() : Method | Updating last modified date for child sku and translations of SKU ID:"
										+ skuItem.getRepositoryId());
							}
							skuItem = ((MutableRepository)getProductCatalog())
									.getItemForUpdate(
											skuItem.getRepositoryId(),
											BBBCatalogImportConstant.SKU_DESC);
							if (skuItem != null
									&& skuItem
											.getPropertyValue(BBBCatalogImportConstant.SKU_TRANSLATIONS) != null) {
								@SuppressWarnings({ "unchecked" })
								final Set<RepositoryItem> pSkuTranslations = (Set<RepositoryItem>) skuItem
										.getPropertyValue(BBBCatalogImportConstant.SKU_TRANSLATIONS);
								for (RepositoryItem pSkuTranslationItem : pSkuTranslations) {
									if (isLoggingDebug()) {
										logDebug("updateLastModifiedDateForChildSkus() : Method | Updating last modified date for translations of Translation ID:"
												+ pSkuTranslationItem.getRepositoryId());
									}
									pSkuTranslationItem = ((MutableRepository)getProductCatalog())
											.getItemForUpdate(
													pSkuTranslationItem
															.getRepositoryId(),
													BBBCatalogImportConstant.BBB_SKU_TRANSLATION_DESC);
								}
							}
						}
					}
				}
			
		} catch (RepositoryException rex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(rex));
			}
		}catch (Exception rex) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(rex));
			}
		}
	}

	/**
	 * @throws RepositoryException 
	 * @throws BBBBusinessException  
	 */
	private RepositoryItem[] executeRQLQuery(final String rqlQuery,final Object[] params,final String viewName,
			final MutableRepository repository) throws BBBSystemException, RepositoryException , BBBBusinessException {
	RqlStatement statement = null;
	RepositoryItem[] queryResult = null;
	if (rqlQuery != null) {
		if (repository != null) {
			try {
				statement = RqlStatement.parseRqlStatement(rqlQuery);
				final RepositoryView view = repository.getView(viewName);
				if (view == null && isLoggingError()) {
					logError("View " + viewName + " is null");
				}
	
				queryResult = statement.executeQuery(view, params);
				if (isLoggingDebug() && queryResult == null) {
	
					logDebug("No results returned for query [" + rqlQuery + "]");
	
				}
	
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError("Unable to retrieve data");
				}
	
				throw new BBBSystemException(UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION, e);
			}
		} else {
			if (isLoggingError()) {
				logError("Repository has no data");
			}
		}
	} else {
		if (isLoggingError()) {
			logError("Query String is null");
		}
	}
	
	return queryResult;
	}
/**
   * This method create or update the product product relationship and its
   * properties
   * 
   * @param pFeedId
   * @param pProductId
   * @param pChildProductId
 * @param pConnection 
 * @return RepositoryItem
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public MutableRepositoryItem updateProdProdRelationship(final String pFeedId, final String pProductId,
      final String pChildProductId, final Connection pConnection) throws BBBSystemException, BBBBusinessException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateProdProdRelationship]");
      logDebug(" pProductId[" + pProductId + "] pChildProductId[" + pChildProductId + "] pFeedId[" + pFeedId + "]");
    }
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_PROD_PROD_RELATIONSHIP);

    ProdSkuVO itemSkuVO;

    itemSkuVO = getPimFeedTools().getPIMProdSkuDetail(pFeedId, pProductId, pChildProductId, pConnection);

    if (itemSkuVO == null) {
    	final String errorMsg = "Details for productId and childSkuId not found in the CatalogTools, itemSkuVO is null";
      if (isLoggingDebug()) {

        logDebug(errorMsg);

      }

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS",
          "Prod=" + pProductId + "ChildProd=" + pChildProductId, getDate(), errorMsg, pConnection);
      return null;
    }
    final String rollupType = itemSkuVO.getRollupType();
    final Boolean likeUnlike = itemSkuVO.getLikeUnlikeFlag();

    final MutableRepositoryItem childProductItem = getItem(pChildProductId, BBBCatalogImportConstant.PRODUCT_DESC);
    if (childProductItem == null) {

    final String errorMsg =new StringBuffer().append( "Child Product Id not found in the repository").toString();
      if (isLoggingDebug()) {

        logDebug(errorMsg);

      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS",
          "Prod=" + pProductId + "ChildProd=" + pChildProductId, getDate(), errorMsg, pConnection);
      return null;

    }
    if (isLoggingDebug()) {
      logDebug("Value for childSkuId =" + pChildProductId);
    }

    final String prodRepositoryId = getId(pProductId, pChildProductId);

    MutableRepositoryItem productItem = null;
    try {

      productItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(prodRepositoryId,
          BBBCatalogImportConstant.BBB_PRD_RELN);
      if (productItem == null) {

        createItem(prodRepositoryId, BBBCatalogImportConstant.BBB_PRD_RELN);
      }

      if (isLoggingDebug()) {
        logDebug("Value for productItem =" + productItem);
      }

      productItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(prodRepositoryId,
          BBBCatalogImportConstant.BBB_PRD_RELN);

      simplePropertyWrite(BBBCatalogImportConstant.PRODUCT_ID, childProductItem, productItem);
      simplePropertyWrite(BBBCatalogImportConstant.LIKE_UNLIKE, likeUnlike, productItem);
      simplePropertyWrite(BBBCatalogImportConstant.COLLECTION_ROLLUP_TYPE,
          getItem(rollupType, BBBCatalogImportConstant.BBBROLLUPTYPE_DESC), productItem);

      ((MutableRepository)getProductCatalog()).updateItem(productItem);

    } catch (RepositoryException rex) {

      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", prodRepositoryId, getDate(),
          rex.getMessage(), pConnection);
      BBBPerformanceMonitor.cancel(UPDATE_PROD_PROD_RELATIONSHIP);
      isMonitorCanceled = true;

    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_PROD_PROD_RELATIONSHIP);
      }
    }
    return productItem;
  }

  /**
   * This method create or update the Sku Attributes relationship and its
   * properties
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
 * @param pSkuDisplayAttributesVO 
 * @param pConnection 
   * @param pIsFrequent
   * @param pItemVO
 * @return RepositoryItem
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public MutableRepositoryItem updateSkuAttributeRelnsProperties(final String pFeedId, final String pItemId, final SkuDisplayAttributesVO pSkuDisplayAttributesVO, final Connection pConnection) throws BBBSystemException,
      BBBBusinessException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS,
        UPDATE_SKU_ATT_RELNS_PROP);
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateSkuAttributeRelnsProperties]");
      logDebug(" pSkuId[" + pItemId + "] pFeedId[" + pFeedId + "]");
    }
    
    SkuDisplayAttributesVO skuDisplayAttribute;
    final String attributeId = pSkuDisplayAttributesVO.getItemAttributeId();
    final String valueId = pSkuDisplayAttributesVO.getItemValueId();
    skuDisplayAttribute = pSkuDisplayAttributesVO;

    if (StringUtils.isEmpty(pItemId) || StringUtils.isEmpty(attributeId) || StringUtils.isEmpty(valueId)) {

      if (isLoggingDebug()) {

        logDebug("skuDisplayAttribute is null");
      }
      return null;
    }

    final String attributeRepositoryId = getId(pItemId, attributeId, valueId);
    final Timestamp startDate = skuDisplayAttribute.getStartDT();
    final Timestamp endDate = skuDisplayAttribute.getEndDT();
    final String miscInfo = skuDisplayAttribute.getMiscInfo();
	final String siteId = skuDisplayAttribute.getSiteFlag();
   // String bbbSiteId = getPimSiteToBBBSiteMap().get(siteId);
    final MutableRepositoryItem attributeItem = getItem(getId(attributeId, valueId), "attributeInfo");

    if (attributeItem == null) {
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_ATTRIBUTES", getId(attributeId, valueId), getDate(),
          "attributeInfo not found for " + getId(attributeId, valueId), pConnection);
      return null;
    }

    MutableRepositoryItem skuAttributeItem = null;
    try {

      skuAttributeItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(attributeRepositoryId,
          BBBCatalogImportConstant.BBB_ATTR_RELN_DESC);
      if (skuAttributeItem == null) {

        createItem(attributeRepositoryId, BBBCatalogImportConstant.BBB_ATTR_RELN_DESC);
      }
      skuAttributeItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(attributeRepositoryId,
          BBBCatalogImportConstant.BBB_ATTR_RELN_DESC);
      simplePropertyWrite(BBBCatalogImportConstant.SKU_ATTRIBUTE, attributeItem, skuAttributeItem);
      simplePropertyWrite(BBBCatalogImportConstant.START_DATE, startDate, skuAttributeItem);
      simplePropertyWrite(BBBCatalogImportConstant.END_DATE, endDate, skuAttributeItem);
      simplePropertyWrite(BBBCatalogImportConstant.MISC_INFO, miscInfo, skuAttributeItem);
      if(isBackWardCompatibilityFlag()) {
      associateSKUAttrbuteWithSite(skuAttributeItem, siteId);
      associateWithSite(skuAttributeItem, "siteIds", siteId);
      }else {
          associateSKUAttrbuteWithSite(skuAttributeItem, siteId);
      }
      ((MutableRepository)getProductCatalog()).updateItem(skuAttributeItem);

    } catch (RepositoryException rex) {


      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_ATTRIBUTES", attributeRepositoryId, getDate(),
          rex.getMessage(), pConnection);

      BBBPerformanceMonitor.cancel(UPDATE_SKU_ATT_RELNS_PROP);
      isMonitorCanceled = true;

    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS,
            UPDATE_SKU_ATT_RELNS_PROP);
      }
    }
    return skuAttributeItem;
  }

  /**
   * This method associate Media With Product
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
 * @param pErrorList 
 * @param pConnection 
 * @param isProductionImport 
   * @param pIsFrequent
   * @param pItemVO
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public void associateSkuAttributes(final String pFeedId, final String pItemId, final List<String> pErrorList,
      final Connection pConnection, final boolean isProductionImport) throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;
    boolean isProduct = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_SKU_ATT);
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateSkuAttributes]");
      logDebug(" pSkuId[" + pItemId + "] pFeedId[" + pFeedId + "]");
    }
    MutableRepositoryItem item = null;
    MutableRepositoryItem itemAttributeItem = null;
    String propertyName = null;
    String attributeDescriptorName = null;
    String itemDescriptorName = null;

    try {

      item = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pItemId, BBBCatalogImportConstant.PRODUCT_DESC);
      if (item == null) {

        item = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pItemId, BBBCatalogImportConstant.SKU_DESC);
      } else {

        isProduct = true;
      }

      if (item == null) {

    	final String error = "Item not found in the repository";
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_ATTRIBUTES", pItemId, getDate(), error, pConnection);
        pErrorList.add(pItemId);
        return;
      }

      if (isProduct) {

        propertyName = BBBCatalogImportConstant.PRODUCT_ATTRIBUTE_RELNS;
        attributeDescriptorName = BBBCatalogImportConstant.BBB_ATTR_RELN_DESC;
        itemDescriptorName = BBBCatalogImportConstant.PRODUCT_DESC;

        if (isLoggingDebug()) {
          logDebug("Item is a Product");
          logDebug("propertyName=" + propertyName + " attributeDescriptorName=" + attributeDescriptorName
              + " itemDescriptorName=" + itemDescriptorName);
        }
      } else {
        propertyName = BBBCatalogImportConstant.SKU_ATTRIBUTE_RELNS;
        attributeDescriptorName = BBBCatalogImportConstant.BBB_ATTR_RELN_DESC;
        itemDescriptorName = BBBCatalogImportConstant.SKU_DESC;

        if (isLoggingDebug()) {
          logDebug("Item is a Sku");
          logDebug("propertyName=" + propertyName + " attributeDescriptorName=" + attributeDescriptorName
              + " itemDescriptorName=" + itemDescriptorName);
        }
      }

      @SuppressWarnings("unchecked")
      Set<MutableRepositoryItem> itemAttributeRelns = (Set<MutableRepositoryItem>) item.getPropertyValue(propertyName);

      final List<SkuDisplayAttributesVO> skuAttributeOperVOList = getPimFeedTools().getSkuAttributeList(pFeedId, pItemId,
          pConnection);

      if (skuAttributeOperVOList == null || skuAttributeOperVOList.isEmpty()) {

    	  final String error = new StringBuffer().append("Attributes relationship details not found in staging database").toString();
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_ATTRIBUTES", pItemId, getDate(), error, pConnection);
        pErrorList.add(pItemId);
        return;

      }
      if (itemAttributeRelns == null) {

        itemAttributeRelns = new HashSet<MutableRepositoryItem>();
      }
      for (SkuDisplayAttributesVO skuAttributeVO : skuAttributeOperVOList) {

    	final String attributeId = skuAttributeVO.getItemAttributeId();
        final String opcode = skuAttributeVO.getOperationFlag();
        final String itemValueId = skuAttributeVO.getItemValueId();

        final String attributeRelationshipId = getId(pItemId, attributeId, itemValueId);
        if (isLoggingDebug()) {

          logDebug("attributeId=" + attributeId + " attributeRelationshipId=" + attributeRelationshipId);
          logDebug("Operation Code=" + opcode);
        }
        final MutableRepositoryItem attributeItem = getAttributeItem(getId(attributeId, itemValueId));
        if (attributeItem == null) {
        	final String error =new StringBuffer().append( "Bad Association - attribute item" + attributeId + "does not exist in the repository.").toString();
          if (isLoggingDebug()) {

            logDebug(error);
          }
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_ATTRIBUTES",
              "No Details found for attributeId:" + attributeId + "in the repository", getDate(), error, pConnection);
          pErrorList.add(attributeId);
          continue;
        }
        if (!StringUtils.isEmpty(opcode) && opcode.equals(BBBCatalogImportConstant.DELETE_CODE)) {
          
          itemAttributeItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(attributeRelationshipId,
              attributeDescriptorName);
 
   
          if (itemAttributeItem!= null && itemAttributeRelns.contains(itemAttributeItem)) {

        	  itemAttributeRelns.remove(itemAttributeItem);
            if (isLoggingDebug()) {

              logDebug("Removed relationship successfully");
            }
          } else {
            // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
            if(isProductionImport && itemAttributeItem!=null) {
              ((MutableRepository)getProductCatalog()).removeItem(itemAttributeItem.getRepositoryId(),	BBBCatalogImportConstant.BBB_ATTR_RELN_DESC);
              continue;
            } 
            final String error = new StringBuilder().append("Bad Association Deletion- attribute Id =").append(skuAttributeVO.getItemAttributeId()).append("Value_Id=").append(skuAttributeVO.getItemValueId()).append(" association for sku does not exist in the repository.Can't Delete").toString();
            if (isLoggingDebug()) {

              logDebug(error);
            }
            getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_ATTRIBUTES",
                pItemId + BBBCatalogImportConstant.KEY_SEPERATOR + attributeId, getDate(), error, pConnection);
            pErrorList.add(attributeId);
          }
          continue;
        }
        itemAttributeItem = updateSkuAttributeRelnsProperties(pFeedId, pItemId, skuAttributeVO, pConnection);
        if (itemAttributeItem == null) {

        	final String error = new StringBuilder().append("Bad Association- AttributeId=").append(skuAttributeVO.getItemAttributeId()).append("Value_Id=").append(skuAttributeVO.getItemValueId()).append("not found in the Repository, Association not done for the same").toString();
          if (isLoggingDebug()) {

            logDebug(error);
          }
          
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_ATTRIBUTES",pItemId , getDate(), error,
              pConnection);
          pErrorList.add(attributeId);
          continue;
        }

        if (!itemAttributeRelns.contains(itemAttributeItem)) {
          itemAttributeRelns.add(itemAttributeItem);
          if (isLoggingDebug()) {

            logDebug("Association Success");

          }
        } else {

          if (isLoggingDebug()) {

            logDebug("Association already exist.");

          }
        }

      }
      if (item != null) {

    	  item = ((MutableRepository)getProductCatalog()).getItemForUpdate(pItemId, itemDescriptorName);
        simplePropertyWrite(propertyName, itemAttributeRelns, item);
        ((MutableRepository)getProductCatalog()).updateItem(item);
      }

    } catch (RepositoryException e) {
      if (isLoggingError()) {
        logError(BBBStringUtils.stack2string(e));
      }

      if (isLoggingDebug()) {
        logDebug(BBBStringUtils.stack2string(e));
      }
      final String error = new StringBuffer().append("Sku Attributes association not done for skuId " + pItemId).toString();
      if (isLoggingDebug()) {

        logDebug(error);
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_ATTRIBUTES", pItemId, getDate(), error, pConnection);
      pErrorList.add(pItemId);
      BBBPerformanceMonitor.cancel(ASSOCIATE_SKU_ATT);
      isMonitorCanceled = true;

    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_SKU_ATT);
      }
    }
  }

  /**
   * This method create or update the attributes properties
   * 
   * @param pSiteId
   * @param pFeedId
 * @param pItemAttributeId 
 * @param pConnection 
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public void updateItemAttributesProperties(final String pFeedId, final String pItemAttributeId,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor
        .start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, "updateItemAttributesProperties");

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateItemAttributesProperties]");
      logDebug(" pItemAttributeId[" + pItemAttributeId + "] pFeedId[" + pFeedId + "]");
    }

    final List<OperationVO> operationList = getPimFeedTools().getSkuAttibutes(pFeedId, pItemAttributeId, pConnection);

    for (OperationVO operationVO : operationList) {

    	final String itemValueId = operationVO.getId();

      if (StringUtils.isBlank(itemValueId)) {
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEM_ATTRIBUTES", pItemAttributeId, getDate(),
            "itemValueId" + itemValueId + " not found for attributeId : " + pItemAttributeId, pConnection);
        if (isLoggingDebug()) {
          logDebug("itemValueId : " + itemValueId);
        }
        continue;
      }

      final String itemAttributeId = pItemAttributeId;

      AttributeVO attributeDetailsVO;
      try {
    	  attributeDetailsVO = getPimFeedTools().getItemAttributesDetailValue(pFeedId, itemAttributeId, itemValueId,
            pConnection);
    	  

        if (attributeDetailsVO == null) {
          getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEM_ATTRIBUTES", "Details not found", getDate(),
              "Details not found", pConnection);
          if (isLoggingDebug()) {

            logDebug("attributeDetailsVO is null");
          }

          // throw new BBBBusinessException(itemAttributeId +
          // " Details not found in the Staging database");
        }

        final String id = itemAttributeId + BBBCatalogImportConstant.KEY_SEPERATOR + itemValueId;
        final String displayDescrip = attributeDetailsVO.getDescription();
        final String imageURL = attributeDetailsVO.getImageURL();
        final String imageActionURL = attributeDetailsVO.getActionURL();
        final String placeHolder = attributeDetailsVO.getPlaceHolder();
        final Timestamp startDate = attributeDetailsVO.getStartDT();
        final Timestamp endDate = attributeDetailsVO.getEndDT();
        final String priority = attributeDetailsVO.getPriority();
        final String pimSiteList = attributeDetailsVO.getSiteFlag();
        final String attrIntlFlag = attributeDetailsVO.getIntlFlag();

        MutableRepositoryItem attributeItem = null;

        attributeItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(id,
            BBBCatalogImportConstant.ATTRIBUTE_INFO_DESC);
        if (attributeItem == null) {

        	createItem(id, BBBCatalogImportConstant.ATTRIBUTE_INFO_DESC);
        }
        attributeItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(id,
            BBBCatalogImportConstant.ATTRIBUTE_INFO_DESC);
        simplePropertyWrite(BBBCatalogImportConstant.ID, id, attributeItem);
        simplePropertyWrite(BBBCatalogImportConstant.DISPLAY_DESCRIP, displayDescrip, attributeItem);
        simplePropertyWrite(BBBCatalogImportConstant.IMAGE_URL, imageURL, attributeItem);
        simplePropertyWrite(BBBCatalogImportConstant.ACTION_URL, imageActionURL, attributeItem);
        simplePropertyWrite(BBBCatalogImportConstant.PLACE_HOLDER, placeHolder, attributeItem);
        simplePropertyWrite(BBBCatalogImportConstant.START_DATE, startDate, attributeItem);
        simplePropertyWrite(BBBCatalogImportConstant.END_DATE, endDate, attributeItem);
        simplePropertyWrite(BBBCatalogImportConstant.INTL_FLAG, attrIntlFlag, attributeItem);
        if (!StringUtils.isEmpty(priority)) {
          simplePropertyWrite(BBBCatalogImportConstant.PRIORITY2, Integer.valueOf(getInt(priority)), attributeItem);
        }
        
        associateWithSiteItem(attributeItem, BBBCatalogImportConstant.SITES, pimSiteList);

        ((MutableRepository)getProductCatalog()).updateItem(attributeItem);

      } catch (RepositoryException rex) {
        if (isLoggingError()) {

          logError(itemAttributeId + " has following issue, not able to create or update media item");
          logError(BBBStringUtils.stack2string(rex));
        }
        if (isLoggingDebug()) {

          logDebug(BBBStringUtils.stack2string(rex));
        }
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ITEM_ATTRIBUTES", itemAttributeId, getDate(),
            rex.getMessage(), pConnection);
        BBBPerformanceMonitor.cancel("updateItemAttributesProperties");
        isMonitorCanceled = true;
        // throw new BBBBusinessException(rex.getMessage(), rex);
      } finally {
        if (!isMonitorCanceled) {
          BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS,
              "updateItemAttributesProperties");
        }
      }
    }
  }

  /**
   * This method used to update pricing for provided skuId
   * 
   * @param pFeedId
   * @param pSkuId
   * @param pConnection
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  public void updatePricing(final String pFeedId, final String pSkuId, final Connection pConnection)
      throws BBBSystemException, BBBBusinessException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updatePricing]");
      logDebug(" pSkuId[" + pSkuId + "] pFeedId[" + pFeedId + "]");
    }

    final SkuPricingVO skuPricingVO = getPimFeedTools().getSkuPricingDetails(pFeedId, pSkuId, pConnection);
    final String wasPrice = skuPricingVO.getWasPrice();
    final String listPrice = skuPricingVO.getJDARetailPrice();
    final String caWasPrice = skuPricingVO.getCAWasPrice();
    final String caListPrice = skuPricingVO.getCAJDARetailPrice();
    final String mxWasPrice = skuPricingVO.getMXWasPrice();
    final String mxListPrice = skuPricingVO.getMXJDARetailPrice();
    
    final String usInCartPrice = skuPricingVO.getInCartPrice();
    final String caInCartPrice = skuPricingVO.getInCartPriceCA();
    final String mxInCartPrice = skuPricingVO.getInCartPriceMX();
    if (isLoggingDebug()) {

      logDebug("updating price for wasPrice=" + wasPrice + " listPrice=" + listPrice + " caWasPrice=" + caWasPrice
          + " caListPrice=" + caListPrice + " mxWasPrice=" + mxWasPrice
          + " mxListPrice=" + mxListPrice + "usInCartPrice" + usInCartPrice + "caInCartPrice" + caInCartPrice + "mxInCartPrice" + mxInCartPrice );
    }
    
    if((StringUtils.isEmpty(listPrice) && StringUtils.isEmpty(caListPrice) && StringUtils.isEmpty(mxListPrice)) || (!isValidPrice(listPrice) 
    		&& !isValidPrice(caListPrice) && !isValidPrice(mxListPrice))) {
      
      final String error = new StringBuffer().append("List price for Sku Id "+pSkuId +" cann't have 0 or null pricing").toString();
      if (isLoggingDebug()) {
        logDebug(error);
      }
      getPimFeedTools().updatePricingBadRecords(pFeedId, "", "ECP_SKU_PRICING_NEW", pSkuId, getDate(),error, pConnection);
      
      return;
    }
    
 
    if (!StringUtils.isEmpty(listPrice)) {

      addPricing(pFeedId, BBBCatalogImportConstant.BBB_SITE_ID, pSkuId, wasPrice, listPrice, usInCartPrice,pConnection, false);
    }
    if (!StringUtils.isEmpty(caListPrice)) {

      addPricing(pFeedId, BBBCatalogImportConstant.CA_SITE_ID, pSkuId, caWasPrice, caListPrice,caInCartPrice, pConnection, false);
    }
    if (!StringUtils.isEmpty(mxListPrice)) {

      addPricing(pFeedId, BBBCatalogImportConstant.BBB_SITE_ID, pSkuId, mxWasPrice, mxListPrice,mxInCartPrice, pConnection,true);
      }
   
    
  }

  /**
   * Method add pricing for specific site, it updates pricing for sale price and
   * list price if was price is not empty else it update only list price
   * 
   * @param pFeedId
   * @param pSiteId
   * @param pSkuId
   * @param pWasPrice
   * @param pListPrice
 * @param pConnection 
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  public void addPricing(final String pFeedId, final String pSiteId, final String pSkuId, final String pWasPrice,
      final String pListPrice,final String pInCartPrice, final Connection pConnection, boolean isIntlPricing) throws BBBSystemException, BBBBusinessException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [addPricing]");
      logDebug(" pSkuId[" + pSkuId + "] pFeedId[" + pFeedId + "] pSiteId[" + pSiteId + "] pWasPrice[" + pWasPrice
          + "] pListPrice[" + pListPrice + "]");
    }
    if (isValidPrice(pListPrice)) {
					if(isIntlPricing) {
		    		    logDebug("International Pricing Executing");
			    		final List<String> mXlistPriceList = getConfigTools().getAllValuesForKey(BBBCatalogImportConstant.CONTENT_CATALOG_KEYS, MEXICO_LIST_PRICE_LIST);
			        	final List<String> mXsalePriceList = getConfigTools().getAllValuesForKey(BBBCatalogImportConstant.CONTENT_CATALOG_KEYS, MEXICO_SALE_PRICE_LIST);
			        	final List<String> mXInCartPriceLists = getConfigTools().getAllValuesForKey(BBBCatalogImportConstant.CONTENT_CATALOG_KEYS, MEXICO_IN_CART_PRICE_LIST);
			        	final String mXListPrice = mXlistPriceList.get(0);
			        	final String mXSalePrice = mXsalePriceList.get(0);
			        	final String mXInCartPriceList = mXInCartPriceLists.get(0);
			        	if (!StringUtils.isEmpty(mXListPrice)) {
			                 if (isValidPrice(pWasPrice) && isValidPrice(pListPrice)) {
			                	 updatePricingProperties(pSkuId, mXListPrice, pWasPrice,pSiteId,LIST, isIntlPricing);
			                     updatePricingProperties(pSkuId,mXSalePrice, pListPrice,pSiteId,SALE, isIntlPricing);
			                } else {
			                	 updatePricingProperties(pSkuId, mXListPrice, pListPrice, pSiteId,LIST, isIntlPricing);
			                     updatePricingProperties(pSkuId, mXSalePrice, null, pSiteId,SALE, isIntlPricing);
			                }
			                 if (!StringUtils.isEmpty(mXInCartPriceList)) {
					        		if (isValidPrice(pInCartPrice)) {
					        			updatePricingProperties(pSkuId,mXInCartPriceList, pInCartPrice,pSiteId,IN_CART, isIntlPricing);
					                } else {
					                	updatePricingProperties(pSkuId,mXInCartPriceList, null,pSiteId,IN_CART, isIntlPricing);
					                }
				    			 }
			            }
			        	
			    	}else {
			    		 logDebug("Not International Pricing Executing");
				        final String listPriceListId = getListPriceListId(pSiteId);
				    	final String salePriceListId = getSalePriceListId(pSiteId);
				    	final String inCartPriceListId = getInCartPriceListId(pSiteId);
				
				    	final List<String> listPriceList = getConfigTools().getAllValuesForKey(BBBCatalogImportConstant.CONTENT_CATALOG_KEYS, listPriceListId);
				    	final List<String> salePriceList = getConfigTools().getAllValuesForKey(BBBCatalogImportConstant.CONTENT_CATALOG_KEYS, salePriceListId);
				    	final List<String> inCartPriceList = getConfigTools().getAllValuesForKey(BBBCatalogImportConstant.CONTENT_CATALOG_KEYS, inCartPriceListId);
				    	final String listListPrice = listPriceList.get(0);
				    	final String listSalePrice = salePriceList.get(0);
				    	final String listInCartPrice = inCartPriceList.get(0);
					      if (!StringUtils.isEmpty(listListPrice)) {
					
						        if (isValidPrice(pWasPrice) && isValidPrice(pListPrice)) {
						           updatePricingProperties(pSkuId, listListPrice, pWasPrice,pSiteId,LIST, isIntlPricing);
						           updatePricingProperties(pSkuId, listSalePrice, pListPrice,pSiteId,SALE, isIntlPricing);
					           } else {
					        	   updatePricingProperties(pSkuId, listListPrice, pListPrice, pSiteId,LIST, isIntlPricing);
					        	   updatePricingProperties(pSkuId, listSalePrice, null, pSiteId,SALE, isIntlPricing);
					        	}
						        if (!StringUtils.isEmpty(listInCartPrice)) {
						        if (isValidPrice(pInCartPrice)) {
						        	 updatePricingProperties(pSkuId, listInCartPrice, pInCartPrice,pSiteId,IN_CART, isIntlPricing);
						           } else {
						        	   updatePricingProperties(pSkuId, listInCartPrice, null,pSiteId,IN_CART, isIntlPricing);
						        	}
						        }
					        }
				      }
		    	}
  }
  
  private boolean isValidPrice(final String pPrice) {

    if (!StringUtils.isEmpty(pPrice)) {
    	final double price = getDouble(pPrice);
      if (price > 0) {

        return true;
      }
    }

    return false;
  }

   private void updatePricingProperties(final String pSkuId, final String pPriceListId, final String pPrice, final String pSiteId, final String pType, boolean isIntlPricing) {
    
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_PRICING_PROP);
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updatePricingProperties]");
      logDebug(" pSkuId[" + pSkuId + "] pPriceListId[" + pPriceListId + "] pPrice[" + pPrice + "] ");
    }
       long priceLong = Math.round(Double.valueOf(getDouble(pPrice)));
       
       double priceMex = (double) priceLong;
   
    
    MutableRepositoryItem mutablePrice=null;
    final PriceListManager priceListManager = getPriceListManager();
    final MutableRepository priceListRepository = priceListManager.getPriceListRepository();
    RepositoryItem productionRepositoryItem = null;
    RepositoryItem priceItem = null;
    String priceReposiotryId = null;
    if(isIntlPricing){
    	priceReposiotryId = new StringBuffer().append(pSiteId).append("_").append("mx_").append(pType).append("_").append(pSkuId).toString();
    } else {
    	priceReposiotryId = new StringBuffer().append(pSiteId).append("_").append(pType).append("_").append(pSkuId).toString();
    }
  
    try {

      priceItem = priceListManager.getPrice(pPriceListId, null, pSkuId);
      final MutableRepositoryItem priceListItem = (MutableRepositoryItem) priceListManager.getPriceList(pPriceListId);
   
      if (priceItem == null && pPrice != null) {
       
        mutablePrice = priceListRepository.createItem(priceReposiotryId,BBBCatalogImportConstant.PRICE2);
        mutablePrice.setPropertyValue(BBBCatalogImportConstant.SKU_ID, pSkuId);
        if(isIntlPricing){ 
        	mutablePrice.setPropertyValue(BBBCatalogImportConstant.LIST_PRICE, priceMex);
        }
        else{
        	mutablePrice.setPropertyValue(BBBCatalogImportConstant.LIST_PRICE, Double.valueOf(getDouble(pPrice)));
        }
        mutablePrice.setPropertyValue(BBBCatalogImportConstant.PRICE_LIST, priceListItem);
        mutablePrice.setPropertyValue(BBBCatalogImportConstant.PRICING_SCHEME, BBBCatalogImportConstant.LIST_PRICE);
        priceListRepository.addItem(mutablePrice);
      } else if(pPrice == null) {

        
          if(priceItem != null) {
             if (isLoggingDebug()) {

               logDebug("CatalogTools Method Name [updatePricingProperties] Price is null. Removing Item from the list");
             }  
            priceListRepository.removeItem(priceItem.getRepositoryId(), BBBCatalogImportConstant.PRICE2);
          } else {
            if(isLoggingDebug()) {
              
              logDebug("***************getProductionPriceListRepository()="+ getProductionPriceListRepository());
            }
            
            if(getProductionPriceListRepository() != null) {
              
               try {
                 
                productionRepositoryItem =  getProductionPriceListRepository().getItem(priceReposiotryId, BBBCatalogImportConstant.PRICE2);
              } catch (RepositoryException e) {
                if(isLoggingError()) {
                  logError(BBBStringUtils.stack2string(e));
                }
              }
               if(isLoggingDebug()) {
                 
                 logDebug("CatalogTools Method Name [updatePricingProperties] productionRepositoryItem="+ productionRepositoryItem);
               }
            }
            if (productionRepositoryItem != null) {
            
              mutablePrice = priceListRepository.createItem(priceReposiotryId,BBBCatalogImportConstant.PRICE2);
              mutablePrice.setPropertyValue(BBBCatalogImportConstant.SKU_ID, pSkuId);
              if(isIntlPricing){ 
              	mutablePrice.setPropertyValue(BBBCatalogImportConstant.LIST_PRICE, priceMex);
              }
              else{
              mutablePrice.setPropertyValue(BBBCatalogImportConstant.LIST_PRICE, Double.valueOf(getDouble(pPrice)));
              }
              mutablePrice.setPropertyValue(BBBCatalogImportConstant.PRICE_LIST, priceListItem);
              mutablePrice.setPropertyValue(BBBCatalogImportConstant.PRICING_SCHEME, BBBCatalogImportConstant.LIST_PRICE);
              priceListRepository.addItem(mutablePrice);
              if(isLoggingDebug()) {
                
                logDebug("CatalogTools Method Name [updatePricingProperties] Created Dummy Item="+ productionRepositoryItem);
              }
              priceListRepository.removeItem(mutablePrice.getRepositoryId(), BBBCatalogImportConstant.PRICE2);
              if(isLoggingDebug()) {
                
                logDebug("CatalogTools Method Name [updatePricingProperties] Removed Dummy Item="+ productionRepositoryItem);
              }
            }
          }
      } else {
    	  if(getProductionPriceListRepository() != null) {
			mutablePrice = (MutableRepositoryItem)getProductionPriceListRepository().getItem(
					priceItem.getRepositoryId(),
					BBBCatalogImportConstant.PRICE2);
    	  }
			if(mutablePrice!=null){
				
					final Double oldListPrice = (Double) mutablePrice
							.getPropertyValue(BBBCatalogImportConstant.LIST_PRICE);
					// if old price is not null and is as same as new price
					//then skip the update , otherwise update the price || fix for BBBSL-2250
					if (oldListPrice == null || oldListPrice != Double.valueOf(getDouble(pPrice)))  {
						mutablePrice = priceListRepository.getItemForUpdate(
								priceItem.getRepositoryId(),
								BBBCatalogImportConstant.PRICE2);
						 if(isIntlPricing){ 
					        	mutablePrice.setPropertyValue(BBBCatalogImportConstant.LIST_PRICE, priceMex);
					        }
						 else{
						mutablePrice.setPropertyValue(
								BBBCatalogImportConstant.LIST_PRICE,
								Double.valueOf(getDouble(pPrice)));
						 }
						priceListRepository.updateItem(mutablePrice);
					}	
					
			}else{
          mutablePrice = priceListRepository.getItemForUpdate(priceItem.getRepositoryId(), BBBCatalogImportConstant.PRICE2);
          if(isIntlPricing){ 
          	mutablePrice.setPropertyValue(BBBCatalogImportConstant.LIST_PRICE, priceMex);
          }
          else{
          mutablePrice.setPropertyValue(BBBCatalogImportConstant.LIST_PRICE, Double.valueOf(getDouble(pPrice)));
          }
          priceListRepository.updateItem(mutablePrice);
        }
      }

    } catch (PriceListException ple) {

      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(ple));
        logError("Not able to update pricing for sku=" + pSkuId);
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(ple));
      }
      BBBPerformanceMonitor.cancel(UPDATE_PRICING_PROP);
      isMonitorCanceled = true;

    } catch (RepositoryException re) {

      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(re));
        logError("Not able to update pricing for sku=" + pSkuId);
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(re));
      }

      BBBPerformanceMonitor.cancel(UPDATE_PRICING_PROP);
      isMonitorCanceled = true;

    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_PRICING_PROP);
      }
    }
  }

  /**
   * This method associate Sku With Product
   * 
   * @param pFeedId
   * @param pProductId
   * @param pErrorList
 * @param pConnection 
 * @param isProductionImport 
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  @SuppressWarnings("unchecked")
  public void associateProductWithSku(final String pFeedId, final String pProductId, final List<String> pErrorList,
      final Connection pConnection, final boolean isProductionImport) throws BBBSystemException, BBBBusinessException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateProductWithSku]");
      logDebug(" pFeedId[" + pFeedId + "] pProductId[" + pProductId + "] ");
    }
    boolean isMonitorCanceled = false;

    List<MutableRepositoryItem> nullValueHolder = new ArrayList<MutableRepositoryItem> ();
    nullValueHolder.add(null);

    MutableRepositoryItem productItem = null;
    List<MutableRepositoryItem> productChildProdRels = null;
    List<MutableRepositoryItem> productChildSkuRels = null;
    List<MutableRepositoryItem> relatedProductRels = null;
    try {
      BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_PROD_WITH_SKU);

      productItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pProductId,
          BBBCatalogImportConstant.PRODUCT_DESC);

      final String pError = new StringBuffer().append("product id  " + pProductId + " not found in Repository").toString();
      if (productItem == null) {

        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", pError, getDate(), "Product Item is Null",
            pConnection);
        return;
      }

      final List<OperationVO> itemRelVOList = getPimFeedTools().getSkuItem(pFeedId, pProductId, pConnection);

      if (itemRelVOList == null || itemRelVOList.isEmpty()) {
        return;

      }
      
      
      if (isLoggingDebug()) {
        logDebug("Relationship Size =" + itemRelVOList.size());
      }

      //Get child products here.  Product - Child relationship will be created every time
      productChildProdRels = (List<MutableRepositoryItem>) productItem
          .getPropertyValue(BBBCatalogImportConstant.PRODUCT_CHILD_PRODUCTS);
      
      Set<String> productChildProdSet = new HashSet<>();
      Set<String> lastModDateUpdateRequiredSet = new HashSet<String>();
      if (productChildProdRels == null) {

        productChildProdRels = new ArrayList<MutableRepositoryItem>();
      } else {
    	  for(RepositoryItem item:productChildProdRels){
    		  productChildProdSet.add(((RepositoryItem)item.getPropertyValue(BBBCatalogConstants.PRODUCT_ID_PRODUCT_PROPERTY_NAME)).getRepositoryId());
    	  }
    	  if(isProductionImport){
    		  for (MutableRepositoryItem childPrdItem:productChildProdRels){
     			/*Checking all the relations which are present in production. 
      			* If found, we will delete those relations else if this is a new relation,
      			* it will not be deleted as its pred_version is null and it leads to IllegalArgumentException 
      			* */
      			  	
      			RepositoryItem relnItem = getProductionProductCatalog().getItem(childPrdItem.getRepositoryId(), BBBCatalogImportConstant.BBB_PRD_RELN);
  	            if(childPrdItem!=null && null!=relnItem)
  	            {
  					((MutableRepository)getProductCatalog()).removeItem(childPrdItem.getRepositoryId(), BBBCatalogImportConstant.BBB_PRD_RELN);
  	            }
    		  }
    	  }
      }
      //To maintain the order (sequence) of the list, removing skuItem every time from the list
      productChildProdRels.clear();
      // Product - CHILD PRODUCT Relationship End
      
      //Get child skus here.  Product - Child relationship will be created every time
      productChildSkuRels = (List<MutableRepositoryItem>) productItem
          .getPropertyValue(BBBCatalogImportConstant.CHILD_SKUS);

      if (productChildSkuRels == null) {

        productChildSkuRels = new ArrayList<MutableRepositoryItem>();
      }
      //To maintain the order (sequence) of the list, removing skuItem every time from the list
      productChildSkuRels.clear();
      // Product - CHILD SKU Relationship End
      
      
      
      relatedProductRels = (List<MutableRepositoryItem>) productItem
          .getPropertyValue(BBBCatalogImportConstant.FIXED_RELATED_PRODUCTS);

      if (relatedProductRels == null) {

        relatedProductRels = new ArrayList<MutableRepositoryItem>();
      }
    //To maintain the order (sequence) of the list, removing skuItem every time from the list
      
      relatedProductRels.clear();
      // Related Product Relationship End

      
      for (OperationVO itemRelVO : itemRelVOList) {

    	  final String pRelatedType = itemRelVO.getAttribute();
       
        if (isLoggingDebug()) {
          logDebug("Value for Related Tpe CD =" + pRelatedType);
        }

        if (pRelatedType.contains(BBBCatalogImportConstant.RELATIONSHIP_AC)
            || pRelatedType.contains(BBBCatalogImportConstant.RELATIONSHIP_CO)) {

            if(productChildProdRels!=null && productChildProdRels.contains(null)) {
                productChildProdRels.removeAll(nullValueHolder);
            }
          getProdProdAssociation(pFeedId, pProductId, productChildProdRels, itemRelVO, pConnection, isProductionImport);
        } else if (pRelatedType.contains(BBBCatalogImportConstant.RELATIONSHIP_PR)) {

            if(productChildSkuRels != null && productChildSkuRels.contains(null)) {
               productChildSkuRels.removeAll(nullValueHolder);
            }
          getProdSkuAssociation(pFeedId, pProductId, productChildSkuRels, itemRelVO, pConnection, isProductionImport);
        }

        else if (pRelatedType.contains(BBBCatalogImportConstant.RELATIONSHIP_SW)) {

            if(relatedProductRels != null && relatedProductRels.contains(null)) {
               relatedProductRels.removeAll(nullValueHolder);
            }
          getRelatedProdAssociation(pFeedId, pProductId, relatedProductRels, itemRelVO, pConnection, isProductionImport);
        }

      }
      if (productItem != null) {
        
        productItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(pProductId,
            BBBCatalogImportConstant.PRODUCT_DESC);
        if (productChildProdRels != null && !productChildProdRels.isEmpty()) {
            if(productChildProdRels.contains(null)) {
               productChildProdRels.removeAll(nullValueHolder);
            }
          if (isLoggingDebug()) {

            logDebug("Updating Product - Child Product Relationship");
            logDebug("Product Child Product List size=" + productChildProdRels.size());
          }
          productItem.setPropertyValue(BBBCatalogImportConstant.PRODUCT_CHILD_PRODUCTS, productChildProdRels);
          Set<String> childProdNewRelationsSet = new HashSet<String>();
          if(productChildProdRels!=null){
	          for(RepositoryItem item:productChildProdRels){
	        	  childProdNewRelationsSet.add(((RepositoryItem)item.getPropertyValue(BBBCatalogConstants.PRODUCT_ID_PRODUCT_PROPERTY_NAME)).getRepositoryId());
	          }
          }
          determineLastModDateUpdateRequiredSet(productChildProdSet, childProdNewRelationsSet, lastModDateUpdateRequiredSet);
          updateLastModifiedDateOfChildProducts(lastModDateUpdateRequiredSet);
        }

        if (productChildSkuRels != null && !productChildSkuRels.isEmpty()) {
            if(productChildSkuRels.contains(null)) {
               productChildSkuRels.removeAll(nullValueHolder);
            }
          if (isLoggingDebug()) {

            logDebug("Updating Product - Child Sku Relationship");
            logDebug("Product Child Sku List size=" + productChildSkuRels.size());
          }

          simplePropertyWrite(BBBCatalogImportConstant.CHILD_SKUS, productChildSkuRels, productItem);
        }
        if (relatedProductRels != null && !relatedProductRels.isEmpty()) {
            if(relatedProductRels.contains(null)) {
               relatedProductRels.removeAll(nullValueHolder);
            }

          if (isLoggingDebug()) {

            logDebug("Updating Product - Related Products Relationship");
            logDebug("Product Child Sku List size=" + relatedProductRels.size());
          }
          simplePropertyWrite(BBBCatalogImportConstant.FIXED_RELATED_PRODUCTS, relatedProductRels, productItem);
        }
    
        ((MutableRepository)getProductCatalog()).updateItem(productItem);
      }

    } catch (RepositoryException e) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", pProductId, getDate(), e.getMessage(),
          pConnection);
      pErrorList.add(pProductId);
      BBBPerformanceMonitor.cancel(ASSOCIATE_PROD_WITH_SKU);
      isMonitorCanceled = true;
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, ASSOCIATE_PROD_WITH_SKU);
      }
    }
  }
  
  private Set<String> determineLastModDateUpdateRequiredSet(Set<String> previousList, Set<String> newRelations, Set<String> updateRequired){
	  Set<String> tempOldList = new HashSet<String>();
	  tempOldList.addAll(previousList);
	  previousList.removeAll(newRelations);//Gives deleted items by removing items present in new relations.
	  newRelations.removeAll(tempOldList);//Gives added items by removing items present in old relations.
	  updateRequired.addAll(previousList);
	  updateRequired.addAll(newRelations);
	  return updateRequired;
  }

  /**
   * This method associates product with product
   * 
   * @param pFeedId
   * @param pProductId
 * @param pProductRels 
 * @param pChildProdVO 
 * @param pConnection 
 * @param isProductionImport 
   * @param pErrorList
 * @throws BBBSystemException 
   * @throws BBBBusinessException
 * @throws RepositoryException 
   */
  public void getProdProdAssociation(final String pFeedId, final String pProductId,
      final List<MutableRepositoryItem> pProductRels, final OperationVO pChildProdVO, final Connection pConnection, final boolean isProductionImport)
      throws BBBSystemException, BBBBusinessException, RepositoryException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [getProdProdAssociation]");
      logDebug(" pFeedId[" + pFeedId + "] pProductId[" + pProductId + "] ");
    }
    MutableRepositoryItem productItem = null;
    MutableRepositoryItem childProdItem = null;

    productItem = (MutableRepositoryItem) getProductCatalog()
        .getItem(pProductId, BBBCatalogImportConstant.PRODUCT_DESC);
    if (productItem == null) {

      return;
    }

    final String opcode = pChildProdVO.getOperationCode();
    final String childProduct = pChildProdVO.getId();
    final String productRelationshipId = getId(pProductId, childProduct);
    // String productRelationshipId = childProduct;

    childProdItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(productRelationshipId,
        BBBCatalogImportConstant.BBB_PRD_RELN);
    if (childProdItem == null) {
      createItem(productRelationshipId, BBBCatalogImportConstant.BBB_PRD_RELN);
    }

    if (!StringUtils.isEmpty(opcode) && opcode.contains(BBBCatalogImportConstant.DELETE_CODE)) {
      // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
      if(isProductionImport) {
  		  ((MutableRepository)getProductCatalog()).removeItem(productRelationshipId, BBBCatalogImportConstant.BBB_PRD_RELN);
           return;
      }  
      if (childProdItem == null) {
    	final String skuError = new StringBuffer().append("Child Product Item  " + childProdItem + " not found in Repository").toString();
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", skuError, getDate(),
            "ChildProduct Item is Null", pConnection);
        ((MutableRepository)getProductCatalog()).removeItem(productRelationshipId, BBBCatalogImportConstant.BBB_PRD_RELN);
        return;
      }
      if (pProductRels.contains(childProdItem)) {
       
        pProductRels.remove(childProdItem);
      } else {

    	  final String skuError =new StringBuffer().append( "childProdItem " + childProdItem + " not found in the relationship").toString();
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", skuError, getDate(),
            "childProdItem not found", pConnection);
      }
      return;
    }

    childProdItem = updateProdProdRelationship(pFeedId, pProductId, childProduct, pConnection);

    if (isLoggingDebug()) {
      logDebug("Value for skuItem =" + childProdItem);
    }

    if (childProdItem != null && !pProductRels.contains(childProdItem)) {
      pProductRels.add(childProdItem);
    }

  }

  /**
   * This method associates products & sku
   * 
   * @param pFeedId
   * @param pProductId
 * @param pProductSkuRels 
 * @param pChildSkuVO 
 * @param pConnection 
 * @param isProductionImport 
   * @param pErrorList
 * @throws BBBSystemException 
   * @throws BBBBusinessException
 * @throws RepositoryException 
   */

  public void getProdSkuAssociation(final String pFeedId, final String pProductId,
      final List<MutableRepositoryItem> pProductSkuRels, final OperationVO pChildSkuVO, final Connection pConnection, final boolean isProductionImport)
      throws BBBSystemException, BBBBusinessException, RepositoryException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [getProdSkuAssociation]");
      logDebug(" pFeedId[" + pFeedId + "] pProductId[" + pProductId + "] ");
    }

    MutableRepositoryItem productItem = null;
    MutableRepositoryItem skuItem = null;
    productItem = (MutableRepositoryItem) getProductCatalog()
        .getItem(pProductId, BBBCatalogImportConstant.PRODUCT_DESC);
    // Add bad Record
    final String prodError =new StringBuffer().append( "product id  " + pProductId + " not found in Repository").toString();
    if (productItem == null) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", prodError, getDate(),
          "Product Item is Null", pConnection);
      return;
    }
    if (isLoggingDebug()) {
      logDebug("Value for productItem =" + productItem);
    }

    final String opcode = pChildSkuVO.getOperationCode();
    final String skuId = pChildSkuVO.getId();
   /* int sequence = getInt(pChildSkuVO.getSequence());
    int seq= getSequenceNo(pChildSkuVO,sequence);*/
   

    skuItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(skuId, BBBCatalogImportConstant.SKU_DESC);

    if (skuItem == null) {
    	final String skuError = new StringBuffer().append("sku id  " + skuId + " not found in Repository for Prod Sku association").toString();
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS",
          "productId=" + pProductId + " skuId=" + skuId, getDate(), skuError, pConnection);
      return;
    }
    if (!StringUtils.isEmpty(opcode) && opcode.contains(BBBCatalogImportConstant.DELETE_CODE)) {
      // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
      if(isProductionImport && !pProductSkuRels.contains(skuItem)) {
        
        return;
      }  
      if (pProductSkuRels.contains(skuItem)) {
        
        if(!isProductionImport) {
          
          pProductSkuRels.remove(skuItem);
        }

      } else {

        getPimFeedTools().updateBadRecords(
            pFeedId,
            "",
            "ECP_RELATED_ITEMS",
            pProductId + BBBCatalogImportConstant.KEY_SEPERATOR + skuId,
            getDate(),
            "Can't delete. Association does not exist for " + pProductId + BBBCatalogImportConstant.KEY_SEPERATOR
                + skuId, pConnection);
      }
      return;
    }
   
    
    //To maintain the order (sequence) of the list, removing skuItem list and adding in the list 
    pProductSkuRels.add(skuItem);
    updateProdSkuProperties(pFeedId, pProductId, skuId, pConnection);
  }

  /**
   * This method is used to implement the logic to convert PIM sequence to ATG List sequence.If input sequence is 0, 
   * it returns -1 means item will be added in the last of the list 
   * @param pChildSkuVO
   * @param pSequence
   * @return
   */
 /* private int getSequenceNo(OperationVO pChildSkuVO, int pSequence) {
    int seq;
    if (pSequence==0)
    {
      seq= -1;
    }
    else{
      seq = (pSequence/10)-1;
    }
    return seq;
  }
*/
  /**
   * This method associates related products
   * 
   * @param pFeedId
   * @param pProductId
 * @param pProductSkuRels 
 * @param pRelatedProductVO 
 * @param pConnection 
 * @param isProductionImport 
   * @param pErrorList
 * @throws BBBSystemException 
   * @throws BBBBusinessException
 * @throws RepositoryException 
   */

  public void getRelatedProdAssociation(final String pFeedId, final String pProductId,
      final List<MutableRepositoryItem> pProductSkuRels, final OperationVO pRelatedProductVO,
      final Connection pConnection, final boolean isProductionImport) throws BBBSystemException, BBBBusinessException, RepositoryException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [getRelatedProdAssociation]");
      logDebug(" pFeedId[" + pFeedId + "] pProductId[" + pProductId + "] ");
    }

    MutableRepositoryItem productItem = null;
    MutableRepositoryItem relatedProductItem = null;
    productItem = (MutableRepositoryItem) getProductCatalog()
        .getItem(pProductId, BBBCatalogImportConstant.PRODUCT_DESC);
    // Add bad Record
    final String pError = new StringBuffer().append("product id  " + pProductId + " not found in Repository for Related Prod Association").toString();
    if (productItem == null) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", pError, getDate(),
          "productItem is Null for ProductId" + pProductId, pConnection);
      return;
    }
    if (isLoggingDebug()) {
      logDebug("Value for productItem =" + productItem);
    }

    final String opcode = pRelatedProductVO.getOperationCode();
    final String relatedProductId = pRelatedProductVO.getId();

    relatedProductItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(relatedProductId,
        BBBCatalogImportConstant.PRODUCT_DESC);

    if (relatedProductItem == null) {
    	final String skuError =new StringBuffer().append( "related product id  " + relatedProductId + " not found in Repository").toString();
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", skuError, getDate(),
          "relatedProductItem is Null for relatedProductId: " + relatedProductId, pConnection);
      return;
    }
    if (!StringUtils.isEmpty(opcode) && opcode.contains(BBBCatalogImportConstant.DELETE_CODE)) {

   // if import is doing with RegularProduction Schedular, skip delete item. This is done because item is already deleted with RegularStaging. 
      if(isProductionImport && !pProductSkuRels.contains(relatedProductItem)) {
        
        return;
      }  
 

      if (pProductSkuRels.contains(relatedProductItem)) {

        pProductSkuRels.remove(relatedProductItem);

      } else {

    	  final String skuError =new StringBuffer().append( "Related ProductIem " + relatedProductItem + " not found for relatedProductId: "
            + relatedProductId).toString();
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", skuError, getDate(),
            "Related ProductItem not found for relatedProductId" + relatedProductId, pConnection);
      }
      return;
    }

    if (!pProductSkuRels.contains(relatedProductItem)) {
      pProductSkuRels.add(relatedProductItem);
    }

  }

  /**
   * 
   * @param pItemId
   * @param pItemDiscriptorName
   * @throws RepositoryException
   */
  private void createItem(final String pItemId, final String pItemDiscriptorName) throws RepositoryException {

	  final MutableRepositoryItem item = ((MutableRepository)getProductCatalog()).createItem(pItemId, pItemDiscriptorName);
    ((MutableRepository)getProductCatalog()).addItem(item);
  }

  /**
   * parse string as double and return 0.0 if fail to parse
   * 
   * @param pString
   * @return
   */
  private double getDouble(final String pString) {

    double data = 0.0;
    if (pString == null) {
      return 0.0;
    }
    try {
      data = Double.parseDouble(pString);
    } catch (NumberFormatException nfe) {

      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(nfe));
      }
    }

    return data;
  }

  /**
   * parse string as double and return 0.0 if fail to parse
   * 
   * @param pString
   * @return
   */
  private int getInt(final String pString) {

    int data = 0;
    try {
      data = Integer.parseInt(pString);
    } catch (NumberFormatException nfe) {

      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(nfe));
      }
    }

    return data;
  }

  private MutableRepositoryItem getShippingItem(final String pId, final String pItemDescriptor) {

    try {

      return (MutableRepositoryItem) getShippingRepository().getItem(pId, pItemDescriptor);
    } catch (RepositoryException e) {
      if (isLoggingError()) {

        logError("Not found id" + pId + " in Shipping Repository Item Descriptor " + pItemDescriptor
            + " because of following reason");
        logError(BBBStringUtils.stack2string(e));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(e));
      }
    }
    return null;
  }

  /**
   * This method get brand item
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
   * @throws BBBBusinessException
   */
  private MutableRepositoryItem getAttributeItem(final String pAttributeId) {

    MutableRepositoryItem attributeItem = null;

    if (isLoggingDebug()) {

      logDebug("Get Attribute Item from repository for AttributeId=" + pAttributeId);
    }
    try {
      attributeItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pAttributeId,
          BBBCatalogImportConstant.ATTRIBUTE_INFO_DESC);

    } catch (RepositoryException rex) {
      if (isLoggingError()) {

        logError(pAttributeId + " has following issue, return null");
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }

    }

    return attributeItem;
  }

  /**
   * This method create or update the media properties
   * 
   * @param pSiteId
   * @param pFeedId
   * @param pItemId
   * @param pIsFrequent
   *          /** This method create or update the dept properties
   * 
   * @param pDeptId
 * @param pErrorList 
 * @param pConnection 
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public void updateDept(final String pFeedId, final String pDeptId,final List<String> pErrorList,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateDept]");
      logDebug(" pDeptId[" + pDeptId + "] pFeedId[" + pFeedId + "] ");
    }

    final String deptId = pDeptId;
    boolean isMonitorCanceled = false;
    MiscVO deptVO;
    try {
      BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_DEPT);

      if (isLoggingDebug()) {

        logDebug("deptId is" + deptId);
      }
      deptVO = getPimFeedTools().getPIMDeptDetail(pFeedId, deptId, pConnection);

      if (deptVO == null) {

        if (isLoggingDebug()) {

          logDebug("deptVO is null");
        }
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_DEPT", "Dept Id :" + deptId, getDate(),
            "Details not found deptVO is null", pConnection);
        return;

        // throw new BBBBusinessException(deptId +
        // " Details not found in the Staging database");
      }

      final String descrip = deptVO.getDescrip();

      MutableRepositoryItem deptItem = null;

      deptItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(deptId, BBBCatalogImportConstant.BBB_DEPT);
      if (deptItem == null) {

        createItem(deptId, BBBCatalogImportConstant.BBB_DEPT);
      }
      deptItem = ((MutableRepository)getProductCatalog())
          .getItemForUpdate(deptId, BBBCatalogImportConstant.BBB_DEPT);

      if (isLoggingDebug()) {

        logDebug("deptItem=" + deptItem);
      }
      simplePropertyWrite(BBBCatalogImportConstant.DESCRIP, descrip, deptItem);

      ((MutableRepository)getProductCatalog()).updateItem(deptItem);

    } catch (RepositoryException rex) {
      if (isLoggingError()) {

        logError(deptId + " has following issue, not able to update the dept item");
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_DEPT", "Repository Exception for deptId:" + deptId,
          getDate(), rex.getMessage(), pConnection);
      pErrorList.add(deptId);
      // throw new BBBBusinessException(rex.getMessage(), rex);
      BBBPerformanceMonitor.cancel(UPDATE_DEPT);
      isMonitorCanceled = true;

    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, "updateDeptProperties");
      }
    }
  }

  /**
   * This method create or update the sub dept properties
 * @param pFeedId 
   * 
   * @param pDeptId
 * @param pErrorList 
 * @param pConnection 
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public void updateSubDeptProperties(final String pFeedId, final String pDeptId,final List<String> pErrorList,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateSubDeptProperties]");
      logDebug(" pSubDeptId[" + pDeptId + "] pFeedId[" + pFeedId + "] ");
    }
    String deptId = pDeptId;
    String descrip = null;
    String subDeptId = null;

    try {
      BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_SUB_DEPT_PROP);

      if (isLoggingDebug()) {

        logDebug("subDeptId is" + deptId);
      }
      final List<MiscVO> subDeptVOList = getPimFeedTools().getPIMSubDeptDetail(pFeedId, deptId, pConnection);

      for (MiscVO subDeptVO : subDeptVOList) {
        if (subDeptVO == null) {

          if (isLoggingDebug()) {

            logDebug("subdeptVO is null");
          }

          getPimFeedTools().updateBadRecords(pFeedId, "", "SUB_DEPT", "SubDept Id: " + deptId + "not found", getDate(),
              "Details not found for" + deptId + "subdeptVO is null", pConnection);

          continue;
          // throw new BBBBusinessException(subDeptId +
          // " Details not found in the Staging database");
        }

        deptId = subDeptVO.getDeptid();
        subDeptId = subDeptVO.getSubDeptid();
        final MutableRepositoryItem deptItem = getItem(deptId, BBBCatalogImportConstant.BBB_DEPT);

       /* if (deptItem == null) {

          if (isLoggingDebug()) {

            logDebug("deptItem is not available in the repository");
          }

          getPimFeedTools().updateBadRecords(pFeedId, "", "SUB_DEPT", deptId, getDate(),
              "Details not found in the repository for deptId=" + deptId, pConnection);

          return;
        }
      */       
        descrip = subDeptVO.getDescrip();
        final String subDeptRepostioryId = getId(deptId,subDeptId);
        MutableRepositoryItem subDeptItem = null;

        subDeptItem = getItem(subDeptRepostioryId, BBBCatalogImportConstant.BBB_SUB_DEPT);
        if (subDeptItem == null) {

          createItem(subDeptRepostioryId, BBBCatalogImportConstant.BBB_SUB_DEPT);
        }
        subDeptItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(subDeptRepostioryId,
            BBBCatalogImportConstant.BBB_SUB_DEPT);
        if (isLoggingDebug()) {

          logDebug("subDeptItem=" + subDeptItem);
        }
        simplePropertyWrite(BBBCatalogImportConstant.JDA_DEPT_ID, deptItem, subDeptItem);
        simplePropertyWrite(BBBCatalogImportConstant.DESCRIP, descrip, subDeptItem);

        ((MutableRepository)getProductCatalog()).updateItem(subDeptItem);
      }
    } catch (RepositoryException rex) {
      if (isLoggingError()) {

        logError(deptId + " has following issue, not able to create or update subdept item");
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }

      getPimFeedTools().updateBadRecords(pFeedId, "", "SUB_DEPT", "Repository Exception for subDeptId:" + deptId,
          getDate(), rex.getMessage(), pConnection);
      pErrorList.add(deptId);

      BBBPerformanceMonitor.cancel(UPDATE_SUB_DEPT_PROP);
      isMonitorCanceled = true;

    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_SUB_DEPT_PROP);
      }
    }
  }

  /**
   * This method create or update the class properties
 * @param pFeedId 
   * 
   * @param pClassId
 * @param pErrorList 
 * @param pConnection 
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public void updateClassProperties(final String pFeedId, final String pClassId,final List<String> pErrorList,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {
    boolean isMonitorCanceled = false;
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateClassProperties]");
      logDebug(" pClassId[" + pClassId + "] pFeedId[" + pFeedId + "] ");
    }
    final String classId = pClassId;

    List<MiscVO> classVOList = null;

    try {

      BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_CLASS_PROP);
      classVOList = getPimFeedTools().getPIMClassListVO(pFeedId, classId, pConnection);
      for (MiscVO classVO : classVOList) {

        if (classVO == null) {

          if (isLoggingDebug()) {

            logDebug("classVO is null");
          }

          getPimFeedTools().updateBadRecords(pFeedId, "", "CLASS", "ClassId:" + classId + "not found", getDate(),
              "Details not found for classId" + classId + "classVO is null", pConnection);
          continue;

          // throw new BBBBusinessException(classId +
          // " Details not found in the Staging database");
        }

        final String deptId = classVO.getDeptid();
        final String subDeptId = classVO.getSubDeptid();
        final String descrip = classVO.getDescrip();

        if (StringUtils.isEmpty(deptId) || StringUtils.isEmpty(subDeptId) || StringUtils.isEmpty(classId)) {

        	final String errorMsg = new StringBuffer().append("Missing Details. Can't Proceed for Update Class,  DeptId=" + deptId + "SubDeptId="
              + subDeptId + " ClassId=" + classId).toString();
          if (isLoggingDebug()) {

            logDebug(errorMsg);

          }

          getPimFeedTools().updateBadRecords(pFeedId, "", "CLASS", pClassId, getDate(), errorMsg, pConnection);
          continue;
        }

        final String classID = getClassId(deptId, subDeptId, classId);
        if (isLoggingDebug()) {

          logDebug("classID is" + classID + " depId is" + deptId + " subDepId is" + subDeptId);
        }

        MutableRepositoryItem classItem = null;

        classItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(classID, BBBCatalogImportConstant.CLASS_DESC);
        if (classItem == null) {
          classItem = ((MutableRepository)getProductCatalog()).createItem(classID, BBBCatalogImportConstant.CLASS_DESC);

          if (isLoggingDebug()) {

            logDebug("classItem is" + classItem);
          }

          simplePropertyWrite(BBBCatalogImportConstant.JDA_DEPT_ID, deptId, classItem);
          simplePropertyWrite(BBBCatalogImportConstant.JDA_SUB_DEPT_ID, subDeptId, classItem);
          simplePropertyWrite(BBBCatalogImportConstant.JDA_CLASS, classId, classItem);
          simplePropertyWrite(BBBCatalogImportConstant.DESCRIP, descrip, classItem);
          ((MutableRepository)getProductCatalog()).addItem(classItem);

        } else {
          classItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(classID,
              BBBCatalogImportConstant.CLASS_DESC);

          if (isLoggingDebug()) {

            logDebug("classItem is" + classItem);
          }

          simplePropertyWrite(BBBCatalogImportConstant.JDA_DEPT_ID, deptId, classItem);
          simplePropertyWrite(BBBCatalogImportConstant.JDA_SUB_DEPT_ID, subDeptId, classItem);
          simplePropertyWrite(BBBCatalogImportConstant.JDA_CLASS, classId, classItem);

          simplePropertyWrite(BBBCatalogImportConstant.DESCRIP, descrip, classItem);

          ((MutableRepository)getProductCatalog()).updateItem(classItem);

        }

      }
    } catch (RepositoryException rex) {
      if (isLoggingError()) {

        logError(classId + " has following issue, not able to create or update class item");
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }

      getPimFeedTools().updateBadRecords(pFeedId, "", "CLASS", "Repository Exception for classId: " + classId,
          getDate(), rex.getMessage(), pConnection);
      pErrorList.add(classId);
      BBBPerformanceMonitor.cancel(UPDATE_CLASS_PROP);
      isMonitorCanceled = true;
      // throw new BBBBusinessException(rex.getMessage(), rex);
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_CLASS_PROP);
      }
    }
  }

  /**
   * This method update the sku threshold properties
   * 
   * @param pFeedId
 * @param pJdaDeptId 
 * @param pErrorList 
 * @param pConnection 
   * @param threshId
   * @throws BBBBusinessException
   * @throws BBBSystemException
   */
  public void updateSkuThreshold(final String pFeedId, final String pJdaDeptId,final List<String> pErrorList,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateskuThresholdProperties]");
      logDebug(" pJdaDeptId[" + pJdaDeptId + "] pFeedId[" + pFeedId + "] ");
    }
    boolean isMonitorCanceled = false;
    final String jdaDeptId = pJdaDeptId;

    List<MiscVO> threshHoldVOList =null;
    try {

      BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_CLASS_PROP);
      threshHoldVOList = getPimFeedTools().getSkuThreshDetails(pFeedId, pJdaDeptId, pConnection);
    for (MiscVO threshVO : threshHoldVOList) {

      if (threshVO == null) {
        if (isLoggingDebug()) {

          logDebug("threshVO is null");
        }

        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_THRESHOLD", "threshId:" + jdaDeptId + "not found",
            getDate(), "Details not found for" + jdaDeptId + ",threshVO is null", pConnection);
        continue;

        // throw new BBBBusinessException(threshId +
        // " Details not found in the Staging database not found");
      }

      final String skuThresholdId = threshVO.getSkuThresholdid();
      final String deptId = threshVO.getDeptid();
      final String subDeptId = threshVO.getSubDeptid();
      final String skuId = threshVO.getSkuid();
      final String classId = threshVO.getClassid();
      final String siteFlag = threshVO.getSiteFlag();
      // String type = threshVO.getType();
      final long thresholdLimited = threshVO.getThresholdLimited();
      final long thresholdAvailable = threshVO.getThresholdAvailable();
      
      
     /* if (StringUtils.isEmpty(deptId) || StringUtils.isEmpty(subDeptId)
          || StringUtils.isEmpty(skuId) || StringUtils.isEmpty(classId)) {

        String error =new StringBuffer().append( "DeptId:" + deptId + " subDeptId=" + subDeptId + " skuId="
            + skuId + " ClassId=" + classId).toString();
        if (isLoggingDebug()) {

          logDebug(error);
        }

        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_THRESHOLD", error, getDate(),
            "Details not found for" + jdaDeptId + ",threshVO is null", pConnection);
        continue;

        // throw new BBBBusinessException(threshId +
        // " Details not found in the Staging database not found");
      }*/
     
     // String threshHoldRepostioryId = getThreshHoldId(deptId, subDeptId, classId, skuId);

     //   MutableRepositoryItem threshItem = updateThreshHoldProperties(threshVO);

       // ((MutableRepository)getProductCatalog()).updateItem(threshItem);
      
      if (isLoggingDebug()) {

        logDebug( " depId is" + deptId + " subDepId is" + subDeptId+ "skuId is" +skuId +"classId" +classId);
      }
      
      MutableRepositoryItem threhHoldItem = null; 
      threhHoldItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(skuThresholdId, BBBCatalogImportConstant.SKU_THRESHOLDS_DESC);
      if (threhHoldItem == null) {
        threhHoldItem = ((MutableRepository)getProductCatalog()).createItem(skuThresholdId, BBBCatalogImportConstant.SKU_THRESHOLDS_DESC);

        if (isLoggingDebug()) {

          logDebug("classItem is" + threhHoldItem);
        }

        simplePropertyWrite(BBBCatalogImportConstant.JDA_DEPT_ID, deptId, threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.JDA_SUB_DEPT_ID, subDeptId, threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.SKU_ID, skuId, threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.JDA_CLASS, classId, threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.SITE_ID, getSite(siteFlag), threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.THRESHOLD_LIMITED, Long.valueOf(thresholdLimited), threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.THRESHOLD_AVAILABLE, Long.valueOf(thresholdAvailable), threhHoldItem);
       
        ((MutableRepository)getProductCatalog()).addItem(threhHoldItem);

      } else {
        threhHoldItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(skuThresholdId,
            BBBCatalogImportConstant.SKU_THRESHOLDS_DESC);

        if (isLoggingDebug()) {

          logDebug("threhHoldItem is" + threhHoldItem);
        }

        simplePropertyWrite(BBBCatalogImportConstant.JDA_DEPT_ID, deptId, threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.JDA_SUB_DEPT_ID, subDeptId, threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.SKU_ID, skuId, threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.JDA_CLASS, classId, threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.SITE_ID, getSite(siteFlag), threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.THRESHOLD_LIMITED, Long.valueOf(thresholdLimited), threhHoldItem);
        simplePropertyWrite(BBBCatalogImportConstant.THRESHOLD_AVAILABLE, Long.valueOf(thresholdAvailable), threhHoldItem);
       
        ((MutableRepository)getProductCatalog()).updateItem(threhHoldItem);

      }

    }
    }
    catch (RepositoryException rex) {
      
      if (isLoggingError()) {

        logError(jdaDeptId + " has following issue, not able to create or update dept item");
        logError(BBBStringUtils.stack2string(rex));
      }
      
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_THRESHOLD", jdaDeptId,
          getDate(), new StringBuffer().append("Repository Exception for jdaDeptId: ").append(rex.getMessage()).toString(), pConnection);
      pErrorList.add(jdaDeptId);
      BBBPerformanceMonitor.cancel("updateSkuThreshold");
      isMonitorCanceled = true;
      // throw new BBBBusinessException(rex.getMessage(), rex);
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, "updateSkuThreshold");
      }
    }

  }


  
  /**
   * This method gets the rollup item
   * 
   * @param rollupType
   * @throws BBBBusinessException
   */
  private MutableRepositoryItem getjdaDeptId(final String jdaDept) {
    
    if(StringUtils.isEmpty(jdaDept)) { 
      
      return null;
    }
    MutableRepositoryItem deptId = null;
    try {
      deptId = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(jdaDept, BBBCatalogImportConstant.BBB_DEPT);

    } catch (RepositoryException rex) {
      if (isLoggingError()) {

        logError(jdaDept + " has following issue, return null");
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }

    }

    return deptId;
  }

  private MutableRepositoryItem getjdaSubDeptId(final String jdaSubDept) {
    
    if(StringUtils.isEmpty(jdaSubDept)) { 
      
      return null;
    }
    MutableRepositoryItem subdeptId = null;
    try {
      subdeptId = (MutableRepositoryItem) getProductCatalog()
          .getItem(jdaSubDept, BBBCatalogImportConstant.BBB_SUB_DEPT);

    } catch (RepositoryException rex) {
      if (isLoggingError()) {

        logError(jdaSubDept + " has following issue, return null");
        logError(BBBStringUtils.stack2string(rex));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }

    }

    return subdeptId;
  }

  /**
 * @param pFeedId  
 * @param pSeoTagId 
 * @param pDisplayName 
 * @param pTitle 
 * @param pDescription 
 * @param pKeywords 
 * @param pContentKey 
 * @param isTranslation 
 * @param babTitle 
 * @param caTitle 
 * @throws BBBSystemException 
 * @throws BBBBusinessException 
 */
public void updateSeoTags(final String pFeedId, final String pSeoTagId, final String pDisplayName,
      final String pTitle, final String pDescription, final String pKeywords, final String pContentKey,
      final boolean isTranslation, final String babTitle, final String caTitle) throws BBBSystemException,
      BBBBusinessException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateSeoTags]");
      logDebug(" pSeoTagId[" + pSeoTagId + "] pDisplayName[" + pDisplayName + "] pTitle[" + pTitle + "] pDescription["
          + pDescription + "] pKeywords[" + pKeywords + "]");
    }
    MutableRepositoryItem seoItem;
    if(StringUtils.isEmpty(pSeoTagId)) { 
      
      return;
    }

    try {
      seoItem = (MutableRepositoryItem) getSeoRepository().getItem(pSeoTagId, BBBCatalogImportConstant.DAS_SEO_TAG);
      if (seoItem != null) {

        seoItem = ((MutableRepository)getSeoRepository()).getItemForUpdate(pSeoTagId,
            BBBCatalogImportConstant.DAS_SEO_TAG);
        updateSeoProperties(pDisplayName, pTitle, pDescription, pKeywords, seoItem);
        if (isTranslation && !StringUtils.isEmpty(babTitle)) {

          updateSeoSites(BBBCatalogImportConstant.BAB_SITE_ID, seoItem);
          updateSeoSiteTranslation(seoItem, BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US,
              "Product", "Product Creation", pKeywords);

        }
        if (isTranslation && !StringUtils.isEmpty(caTitle)) {
          updateSeoSites(BBBCatalogImportConstant.CA_SITE_ID, seoItem);

          updateSeoSiteTranslation(seoItem, BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US,
              "Product", "Product Creation", pKeywords);
        }
        ((MutableRepository)getSeoRepository()).updateItem(seoItem);

      }

      else {

        seoItem = ((MutableRepository)getSeoRepository()).createItem(pSeoTagId, BBBCatalogImportConstant.DAS_SEO_TAG);
        updateSeoProperties(pDisplayName, pTitle, pDescription, pKeywords, seoItem);
        if (isTranslation && !StringUtils.isEmpty(babTitle)) {

          updateSeoSites(BBBCatalogImportConstant.BAB_SITE_ID, seoItem);
          updateSeoSiteTranslation(seoItem, BBBCatalogImportConstant.BAB_SITE_ID, BBBCatalogImportConstant.EN_US,
              "Product", "Product Creation", pKeywords);

        }
        if (isTranslation && !StringUtils.isEmpty(caTitle)) {

          updateSeoSites(BBBCatalogImportConstant.CA_SITE_ID, seoItem);

          updateSeoSiteTranslation(seoItem, BBBCatalogImportConstant.CA_SITE_ID, BBBCatalogImportConstant.EN_US,
              "Product", "Product Creation", pKeywords);
        }

        ((MutableRepository)getSeoRepository()).addItem(seoItem);
      }
    } catch (RepositoryException re) {

      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(re));
        logError("Not able to update seo tags for pSeoTagId=" + pSeoTagId);
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(re));
      }

    }

  }

  /**
   * 
   * @param pDisplayName
   * @param pTitle
   * @param pDescription
   * @param pKeywords
   * @param seoItem
   * @throws RepositoryException
   */
  private void updateSeoProperties(final String pDisplayName, final String pTitle, final String pDescription,
      final String pKeywords,final MutableRepositoryItem seoItem) throws RepositoryException {

    simplePropertyWrite(BBBCatalogImportConstant.TITLE_DEFAULT, pTitle, seoItem);
    simplePropertyWrite(BBBCatalogImportConstant.DESCRIPTION_DEFAULT, pDescription, seoItem);
    simplePropertyWrite(BBBCatalogImportConstant.KEYWORDS_DEFAULT, pKeywords, seoItem);
    simplePropertyWrite(BBBCatalogImportConstant.DISPLAY_NAME_DEFAULT, pDisplayName, seoItem);
    simplePropertyWrite(BBBCatalogImportConstant.CONTENT_KEY, seoItem.getRepositoryId(), seoItem);
  }

  /**
   * 
   * @param pSiteId
   * @param seoItem
   */
  private void updateSeoSites(final String pSiteId,final MutableRepositoryItem seoItem) throws RepositoryException {
	  final String siteId = getPimSiteToBBBSiteMap().get(pSiteId);
    if (!StringUtils.isEmpty(pSiteId)) {
      @SuppressWarnings("unchecked")
      Set<String> sites = (Set<String>) seoItem.getPropertyValue(BBBCatalogImportConstant.SITES);
      if (sites == null) {

        sites = new HashSet<String>();
      }
      sites.add(siteId);
      simplePropertyWrite(BBBCatalogImportConstant.SITES, sites, seoItem);
    }
  }

  /**
 * @param pSeoTranslationId
 * @param pSiteId
 * @param pLocale
 * @param pTitle
 * @param pDescription
 * @param pKeywords
 * @return RepositoryItem
 * @throws RepositoryException
 */
public MutableRepositoryItem updateSeoTranslation(final String pSeoTranslationId, final String pSiteId,
      final String pLocale, final String pTitle, final String pDescription, final String pKeywords)
      throws RepositoryException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateSeoTranslation]");
      logDebug(" pSeoTranslationId[" + pSeoTranslationId + "] pSiteId[" + pSiteId + "] pLocale[" + pLocale
          + "] pTitle[" + pTitle + "] pDescription[" + pDescription + "] pKeywords[" + pKeywords + "]");
    }
    MutableRepositoryItem seoItem = null;
    
    if(StringUtils.isEmpty(pSeoTranslationId)) { 
      
      return null;
    }
    seoItem = (MutableRepositoryItem) getSeoRepository().getItem(pSeoTranslationId,
        BBBCatalogImportConstant.BBB_SEO_TRANSLATION);
    final String siteId = getPimSiteToBBBSiteMap().get(pSiteId);
    if (seoItem == null) {
      seoItem = ((MutableRepository)getSeoRepository()).createItem(pSeoTranslationId, BBBCatalogImportConstant.BBB_SEO_TRANSLATION);
      updateSeoTransProperties(pSeoTranslationId, pLocale, pTitle, pDescription, pKeywords, seoItem, siteId);

      ((MutableRepository)getSeoRepository()).addItem(seoItem);
    }

    else {
      seoItem = ((MutableRepository)getSeoRepository()).getItemForUpdate(pSeoTranslationId,
          BBBCatalogImportConstant.BBB_SEO_TRANSLATION);
      updateSeoTransProperties(pSeoTranslationId, pLocale, pTitle, pDescription, pKeywords, seoItem, siteId);

      ((MutableRepository)getSeoRepository()).updateItem(seoItem);
    }

    return seoItem;
  }

  /**
   * 
   * @param pSeoTranslationId
   * @param pLocale
   * @param pTitle
   * @param pDescription
   * @param pKeywords
   * @param seoItem
   * @param siteId
   */
  private void updateSeoTransProperties(final String pSeoTranslationId, final String pLocale, final String pTitle,
      final String pDescription, final String pKeywords,final MutableRepositoryItem seoItem,final String siteId)
      throws RepositoryException {

    simplePropertyWrite(BBBCatalogImportConstant.ID, pSeoTranslationId, seoItem);
    simplePropertyWrite(BBBCatalogImportConstant.SITE, getSite(siteId), seoItem);
    simplePropertyWrite(BBBCatalogImportConstant.LOCALE, pLocale, seoItem);
    simplePropertyWrite(BBBCatalogImportConstant.TITLE, pTitle, seoItem);
    simplePropertyWrite(BBBCatalogImportConstant.DESCRIPTION, pDescription, seoItem);
    simplePropertyWrite(BBBCatalogImportConstant.KEYWORDS, pKeywords, seoItem);
  }

  /**
 * @param pSeoItem
 * @param pSiteId
 * @param pLocale
 * @param pTitle
 * @param pDescription
 * @param pKeywords
 * @throws BBBBusinessException
 */
public void updateSeoSiteTranslation(final MutableRepositoryItem pSeoItem, final String pSiteId,
      final String pLocale, final String pTitle, final String pDescription, final String pKeywords)
      throws BBBBusinessException {

    MutableRepositoryItem seoItem;
    seoItem = pSeoItem;
    final String seoTranslationId = getId(pSiteId, pSeoItem.getRepositoryId());
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateSeoSiteTranslation]");
      logDebug(" seoTranslationId[" + seoTranslationId + "]");
    }
    try {

    	final MutableRepositoryItem transItem = updateSeoTranslation(seoTranslationId, pSiteId, pLocale, pTitle, pDescription,
          pKeywords);
      if (transItem == null) {
        if (isLoggingDebug()) {

          logDebug("transItem=" + transItem + "is null");

        }

      }
      @SuppressWarnings("unchecked")
      final Set<MutableRepositoryItem> translationSet = (Set<MutableRepositoryItem>) seoItem
          .getPropertyValue(BBBCatalogImportConstant.SEO_TRANSLATIONS);
      if (!translationSet.contains(transItem)) {

        translationSet.add(transItem);
      }
      simplePropertyWrite(BBBCatalogImportConstant.SEO_TRANSLATIONS, translationSet, seoItem);
      ((MutableRepository)getSeoRepository()).updateItem(seoItem);
    } catch (RepositoryException re) {

      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(re));
        logError("Not able to update Seo Site Translations for seoTranslationId=" + seoTranslationId);
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(re));
      }

      throw new BBBBusinessException(BBBCoreErrorConstants.VERS_TOOLS_REPOSITORY_EXC,new StringBuffer().append("Ropository Exception for SEO item").append(seoItem.getRepositoryId()).append(re.getMessage()).toString(), re);
    }

  }

  /**
   * 
   * @param pFeedId
   * @param pProductId
   * @param pSkuId
   * @param pConnection
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  public void updateProdSkuProperties(final String pFeedId, final String pProductId, final String pSkuId,
		  final Connection pConnection) throws BBBSystemException, BBBBusinessException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateProdSkuProperties]");
      logDebug(" pFeedId[" + pFeedId + "] pProductId[" + pProductId + "] pSkuId[" + pSkuId + "] ");
    }
    boolean isMonitorCanceled = false;

    MutableRepositoryItem prodItem;

    try {
      BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_PROD_SKU_PROP);
      ProdSkuVO itemSkuVO;
      itemSkuVO = getPimFeedTools().getPIMProdSkuDetail(pFeedId, pProductId, pSkuId, pConnection);
      final String rollupType = itemSkuVO.getRollupType();
      final Boolean likeUnlike = itemSkuVO.getLikeUnlikeFlag();
      prodItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pProductId, BBBCatalogImportConstant.PRODUCT_DESC);
      if (prodItem == null) {
        prodItem = ((MutableRepository)getProductCatalog()).createItem(pProductId, BBBCatalogImportConstant.PRODUCT_DESC);
        if (!StringUtils.isEmpty(rollupType)) {
          simplePropertyWrite(BBBCatalogImportConstant.PROD_ROLLUP_TYPE,
              getItem(rollupType, BBBCatalogImportConstant.BBBROLLUPTYPE_DESC), prodItem);
        }

        simplePropertyWrite(BBBCatalogImportConstant.LIKE_UNLIKE, likeUnlike, prodItem);

        ((MutableRepository)getProductCatalog()).addItem(prodItem);
      }

      else {
        prodItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(pProductId,
            BBBCatalogImportConstant.PRODUCT_DESC);
        if (!StringUtils.isEmpty(rollupType)) {
          simplePropertyWrite(BBBCatalogImportConstant.PROD_ROLLUP_TYPE,
              getItem(rollupType, BBBCatalogImportConstant.BBBROLLUPTYPE_DESC), prodItem);
        }
        simplePropertyWrite(BBBCatalogImportConstant.LIKE_UNLIKE, likeUnlike, prodItem);
        ((MutableRepository)getProductCatalog()).updateItem(prodItem);
      }
    } catch (RepositoryException re) {

      if (isLoggingError()) {

        logError(BBBStringUtils.stack2string(re));
        logError("Not able to update Properties for Prod Sku Associaton for pProductId=" + pProductId);
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(re));
      }
      BBBPerformanceMonitor.cancel(UPDATE_PROD_SKU_PROP);
      isMonitorCanceled = true;
    } finally {
      if (!isMonitorCanceled) {
        BBBPerformanceMonitor.end(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_PROD_SKU_PROP);
      }
    }
  }

  /**
   * This method associate Sku With EcoSku
   * 
   * @param pFeedId
   * @param pSkuId
   * @param pErrorList
 * @param pConnection 
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  @SuppressWarnings("unchecked")
  public void associateEcoSkuWithSku(final String pFeedId, final String pSkuId, final List<String> pErrorList,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateEcoSkuWithSku]");
      logDebug(" pFeedId[" + pFeedId + "] pSkuId[" + pSkuId + "] ");
    }
    MutableRepositoryItem skuItem = null;
    MutableRepositoryItem ecoSkuItem = null;

    Set<MutableRepositoryItem> skuEcoSkuRelsList = null;
    try {

      skuItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pSkuId, BBBCatalogImportConstant.SKU_DESC);

      if (skuItem == null) {
    	final String pError =new StringBuffer().append( "Parent sku id  " + pSkuId + " not found in Repository").toString();
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ECO_ITEMS", pSkuId, getDate(), pError, pConnection);
        return;
      }

      final List<EcoSkuVO> ecoSkuVOList = getPimFeedTools().getEcoSkuDetailList(pFeedId, pSkuId, pConnection);

      if (ecoSkuVOList == null || ecoSkuVOList.isEmpty()) {
        return;

      }

      if (isLoggingDebug()) {
        logDebug("Relationship Size =" + ecoSkuVOList.size());
      }

      skuEcoSkuRelsList = (Set<MutableRepositoryItem>) skuItem.getPropertyValue(BBBCatalogImportConstant.ECO_FEE_SKUS);
      if (skuEcoSkuRelsList == null) {

        skuEcoSkuRelsList = new HashSet<MutableRepositoryItem>();
      }

      for (EcoSkuVO ecoSkuVO : ecoSkuVOList) {

        ecoSkuItem = updateEcoFeeSkuRelationship(pFeedId, pSkuId, ecoSkuVO, pConnection);

        if (isLoggingDebug()) {
          logDebug("Value for skuItem =" + ecoSkuItem);
        }

        if (ecoSkuItem != null && !skuEcoSkuRelsList.contains(ecoSkuItem)) {
          skuEcoSkuRelsList.add(ecoSkuItem);
        } else {

          pErrorList.add(ecoSkuVO.getEcoFeeSkuId());
          continue;
        }

      }
      if (skuItem != null && skuEcoSkuRelsList != null) {

        skuItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(pSkuId,
            BBBCatalogImportConstant.SKU_DESC);

        if (isLoggingDebug()) {

          logDebug("Updating Product - Child Product Relationship");
          logDebug("Product Child Product List size=" + skuEcoSkuRelsList.size());
        }
        skuItem.setPropertyValue(BBBCatalogImportConstant.ECO_FEE_SKUS, skuEcoSkuRelsList);

        ((MutableRepository)getProductCatalog()).updateItem(skuItem);
      }

    } catch (RepositoryException e) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_ECO_ITEMS", pSkuId, getDate(), new StringBuffer().append("Repository Exception for ").append(skuItem.getRepositoryId()).append(e.getMessage()).toString(),
          pConnection);
      pErrorList.add(pSkuId);
    }

  }

   
  /**
   * This method update the brand properties
   * 
   * @param pSiteId
   * @param pFeedId
 * @param pRegionId 
 * @param pErrorList 
 * @param pConnection 
   * @param pItemId
   * @param pIsFrequent
   * @param pItemVO
 * @return RepositoryItem
 * @throws BBBBusinessException 
 * @throws BBBSystemException 
   */
  @SuppressWarnings("unchecked")
  public MutableRepositoryItem updateZIPCodeRegion(final String pFeedId, final String pRegionId,
		  final List<String> pErrorList, final Connection pConnection) throws BBBBusinessException, BBBSystemException {
    boolean isMonitorCanceled = false;
    BBBPerformanceMonitor.start(BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS, UPDATE_ZIP_REGION_PROP);
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateZIPCodeRegions]");
      logDebug("pFeedId[" + pFeedId + "] pRegionId[" + pRegionId + "]");
    }
    final String regionId = pRegionId;
    
    if (isLoggingDebug()) {
    	logDebug("regionId : " + regionId);
    }
    
    final ZipCodeVO regionVO = getPimFeedTools().getZipCodeRegionDetails(pFeedId, regionId, pConnection);
   

    if (regionVO == null) {

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_REGION_ZIPCODES", regionId, getDate(),
          "regionVO is not found in the repository, regionVO is null", pConnection);
      if (isLoggingDebug()) {
        logDebug("regionVO is null");
      }
      return null;
    }

    final String regionName = regionVO.getRegionName();
    final String regionZipCodes = regionVO.getZipCode();
    final String operationFlag = regionVO.getOperationFlag();
    
    if (isLoggingDebug()) {
    	logDebug("regionName :" + regionName + "regionZipCodes :" + regionZipCodes + "operationFlag :" + operationFlag);
    }
    
    Set<RepositoryItem> setZipRestricted = new HashSet<RepositoryItem>();
    RepositoryItem zipCodeSkuItem = null;
    MutableRepositoryItem regionItem = null;
		try {
			if (isLoggingDebug()) {
			logDebug("operationFlag was " + operationFlag + " for region="
					+ regionId);
			}
			if (null != operationFlag
					&& operationFlag.trim().equalsIgnoreCase("I")) {
				regionItem = (MutableRepositoryItem) ((MutableRepository) getProductCatalog())
						.getItem(regionId,BBBCatalogImportConstant.BBB_SITE_SKU_REGION);
				if (regionItem == null) {
					regionItem = ((MutableRepository)getProductCatalog()).createItem(regionId, BBBCatalogImportConstant.BBB_SITE_SKU_REGION);
					regionItem.setPropertyValue("regionName", regionName);
					regionItem.setPropertyValue("restrictedZipCodes", regionZipCodes);
				    ((MutableRepository)getProductCatalog()).addItem(regionItem);
				} else {
					getPimFeedTools()
							.updateBadRecords(
									pFeedId,
									"",
									"ECP_REGION_ZIPCODES",
									regionId,
									getDate(),
									regionId
											+ " has following issue, Item with Region Id :"
											+ regionId + " already exist",
									pConnection);
				}

			} else if (null != operationFlag
					&& operationFlag.trim().equalsIgnoreCase("U")) {
				regionItem = ((MutableRepository) getProductCatalog())
						.getItemForUpdate(regionId,BBBCatalogImportConstant.BBB_SITE_SKU_REGION);
				if (isLoggingDebug()) {
					logDebug("regionItem=" + regionItem);
				}
				if (regionItem == null) {
					getPimFeedTools()
							.updateBadRecords(
									pFeedId,
									"",
									"ECP_REGION_ZIPCODES",
									regionId,
									getDate(),
									regionId
											+ " has following issue, Item with Region Id :"
											+ regionId + " not exist",
									pConnection);
				} else {

					simplePropertyWrite("regionName", regionName, regionItem);
					simplePropertyWrite("restrictedZipCodes", regionZipCodes,
							regionItem);

					((MutableRepository) getProductCatalog())
							.updateItem(regionItem);
				}

			} else if (null != operationFlag
					&& operationFlag.trim().equalsIgnoreCase("D")) {
				zipCodeSkuItem = ((MutableRepository) getProductCatalog())
						.getItem(regionId,BBBCatalogImportConstant.BBB_SITE_SKU_REGION);
				final RqlStatement statement = RqlStatement
						.parseRqlStatement("(siteSkuRegions=?0 )");
				final RepositoryView viewSku = this.getProductCatalog()
						.getView(BBBCatalogImportConstant.SKU_DESC);
				final Object[] paramRegion = new Object[1];
				paramRegion[0] = regionId;
				final RepositoryItem[] skuItem = statement.executeQuery(
						viewSku, paramRegion);
				if(skuItem !=null){
				for (final RepositoryItem item : skuItem) {
					regionItem = ((MutableRepository) getProductCatalog())
							.getItemForUpdate(item.getRepositoryId(), "sku");
					setZipRestricted = (Set<RepositoryItem>) regionItem
							.getPropertyValue("siteSkuRegions");
					for (RepositoryItem zipRestricted : setZipRestricted) {
						if (zipRestricted.equals(zipCodeSkuItem)) {
							setZipRestricted.remove(zipCodeSkuItem);
						}
					}
					regionItem.setPropertyValue("siteSkuRegions",
							setZipRestricted);
					((MutableRepository) this.getProductCatalog())
							.updateItem(regionItem);
				}
				}
				((MutableRepository) getProductCatalog()).removeItem(regionId,
						BBBCatalogImportConstant.BBB_SITE_SKU_REGION);
			} else {
				
				if (isLoggingDebug()) {
				logDebug("valid operation flag not found for region Id "
						+ regionId + " operation flag :" + operationFlag);
				}
				getPimFeedTools()
						.updateBadRecords(
								pFeedId,
								"",
								"ECP_REGION_ZIPCODES",
								regionId,
								getDate(),
								regionId
										+ " has following issue,valid operation flag not found",
								pConnection);
			}

		} catch (RepositoryException rex) {

			getPimFeedTools()
					.updateBadRecords(
							pFeedId,
							"",
							"ECP_REGION_ZIPCODES",
							regionId,
							getDate(),
							regionId
									+ " has following issue, not able to create or Update ZIP Code Region item",
							pConnection);
			if (isLoggingDebug()) {

				logDebug("Not able to create or update ZIP Code Region item");
			}
			BBBPerformanceMonitor.cancel(UPDATE_ZIP_REGION_PROP);
			isMonitorCanceled = true;
		} finally {
			if (!isMonitorCanceled) {
				BBBPerformanceMonitor.end(
						BBBCatalogImportConstant.IMPORT_PROCESS_CATALOG_TOOLS,
						UPDATE_ZIP_REGION_PROP);

			}
		}
    return regionItem;
  }

  /**
   * This method associate Sku With Region
   * 
   * @param pFeedId
   * @param pSkuId
   * @param pErrorList
 * @param pConnection 
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  
  @SuppressWarnings("unchecked")
public void associateZipCodeWithSku(final String pFeedId, final String pSkuId, final List<String> pErrorList,
      final Connection pConnection) throws BBBSystemException, BBBBusinessException {
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateZipCodeWithSku]");
      logDebug(" pFeedId[" + pFeedId + "] pSkuId[" + pSkuId + "] ");
    }
    MutableRepositoryItem skuItem = null;
    RepositoryItem zipCodeSkuItem = null;   
    Set<RepositoryItem> setZipRestricted = new HashSet<RepositoryItem>();   
    Boolean isExist = false;
    try {
      skuItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(pSkuId, BBBCatalogImportConstant.SKU_DESC);
      if (skuItem == null) {
        final String pError =new StringBuffer().append( "Sku id  " + pSkuId + " not found in Repository").toString();
        getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_REGION_RESTRICTIONS ", pSkuId, getDate(), pError, pConnection);
        return;
      }      
      final List<ZipCodeVO> zipCodeVOList = getPimFeedTools().getSkuRegionList(pFeedId, pSkuId, pConnection);
      if (zipCodeVOList == null) {
        return;
      }   
     if(zipCodeVOList!=null) {
    	for(ZipCodeVO zipCodeVO:zipCodeVOList) {
    	zipCodeSkuItem = getZipCodeSkuRelationship(pFeedId, pSkuId, zipCodeVO, pConnection);
        if (isLoggingDebug()) {
          logDebug("Value for skuItem =" + zipCodeSkuItem);
        }
        if (skuItem != null && skuItem.getPropertyValue("siteSkuRegions")!=null) {
						setZipRestricted = (Set<RepositoryItem>) skuItem
								.getPropertyValue("siteSkuRegions");
						if (zipCodeSkuItem != null) {
							final String operationFlag = zipCodeVO
									.getOperationFlag();
							if (null != operationFlag
									&& operationFlag.trim().equalsIgnoreCase(
											"I")) {
								setZipRestricted.add(zipCodeSkuItem);
							} else if (null != operationFlag
									&& operationFlag.trim().equalsIgnoreCase(
											"U")) {
								for (RepositoryItem zipRestricted : setZipRestricted) {
									if (zipRestricted.equals(zipCodeSkuItem)) {
										isExist = true;
									}
								}
								if (isExist == false) {
									setZipRestricted.add(zipCodeSkuItem);
								}
							} else if (null != operationFlag
									&& operationFlag.trim().equalsIgnoreCase(
											"D")) {
								for (RepositoryItem zipRestricted : setZipRestricted) {
									if (zipRestricted.equals(zipCodeSkuItem)) {
										setZipRestricted.remove(zipCodeSkuItem);
									}
								}
							} else {
								
								if (isLoggingDebug()) {
								logDebug("valid operation flag not found for skuId "
										+ pSkuId
										+ " operation flag :"
										+ operationFlag);
								}
								getPimFeedTools()
										.updateBadRecords(
												pFeedId,
												"",
												"ECP_SKU_REGION_RESTRICTIONS",
												pSkuId,
												getDate(),
												pSkuId
														+ " has following issue, valid operation flag not found",
												pConnection);
							}
        	
        	}
        } else {
          pErrorList.add(skuItem.getRepositoryId());
        }
      }
      if (skuItem != null && setZipRestricted != null && !setZipRestricted.isEmpty()) {
        skuItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(pSkuId,
            BBBCatalogImportConstant.SKU_DESC);
        if (isLoggingDebug()) {
        	logDebug("Updating Sku - Zip Code Relationship");
        }
        skuItem.setPropertyValue("siteSkuRegions",setZipRestricted);
        ((MutableRepository)getProductCatalog()).updateItem(skuItem);
      }
     }
    } catch (RepositoryException e) {
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_REGION_RESTRICTIONS", pSkuId, getDate(), new StringBuffer().append("Repository Exception for ").append(skuItem.getRepositoryId()).append(e.getMessage()).toString(),
          pConnection);
      pErrorList.add(pSkuId);
    }

  }

  /**
   * This method would update sku region relation ship.
 * @param pFeedId 
 * @param pSkuId 
 * @param pZipCodeVO 
 * @param pConnection 
   * @return RepositoryItem
 * @throws BBBSystemException 
 * @throws BBBBusinessException 
   */
  public RepositoryItem getZipCodeSkuRelationship(final String pFeedId,final String  pSkuId,final ZipCodeVO pZipCodeVO,final Connection pConnection) throws BBBSystemException, BBBBusinessException{
	  if (isLoggingDebug()) {
		  logDebug("CatalogTools Method Name [updateZipCodeSkuRelationship]");
		  logDebug(" pSkuId[" + pSkuId +"] pFeedId[" + pFeedId + "]");
	  }
	  ZipCodeVO itemZipCodeVO;
	  RepositoryItem regionItem  = null;
	  itemZipCodeVO = pZipCodeVO;
	  final String regionId = itemZipCodeVO.getRegionId();
	  if (StringUtils.isEmpty(regionId)) {
		  final String errorMsg =new StringBuffer().append( "Missing Details. Can't Proceed for ZipSku : SkuId=" + pSkuId
				  + " regionId=" + regionId ).toString();
		  if (isLoggingDebug()) {
			  logDebug(errorMsg);
		  }
		  getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_REGION_RESTRICTIONS ", "SkuId=" + pSkuId, getDate(), errorMsg,
				  pConnection);
		  return null;
	  }
	  try {	    	
		  regionItem = ((MutableRepository)getProductCatalog()).getItem(regionId,"siteSkuRegion");        
		  if (regionItem == null) {
			  final String  errorMsg = new StringBuffer().append("Region Id not found in the repository").toString();
			  if (isLoggingDebug()) {
				  logDebug(errorMsg);
			  }	       			
		  }	  
	  } catch (RepositoryException rex) {
		  if (isLoggingDebug()) {
			  logDebug(BBBStringUtils.stack2string(rex));
		  }
		  getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_SKU_REGION_RESTRICTIONS ", "SkuId=" + pSkuId + " regionId=" + regionId,
				  getDate(), new StringBuilder().append("Repository Exception=").append(rex.getMessage()).toString(), pConnection);
	  }
	  if (isLoggingDebug()) {
		  logDebug("END Method Name [updateZipCodeSkuRelationship]");
	  }
	  return regionItem; 	  
  }
  
  
  /**
   * This method create or update the EcoFeeSku relationship and its properties
   * 
   * @param pFeedId
   * @param pSkuId
 * @param pEcoSkuVO 
 * @param pConnection 
   * @param pEcoSkuId
 * @return RepositoryItem
 * @throws BBBSystemException 
   * @throws BBBBusinessException
   */
  public MutableRepositoryItem updateEcoFeeSkuRelationship(final String pFeedId, final String pSkuId,
      final EcoSkuVO pEcoSkuVO, final Connection pConnection) throws BBBSystemException, BBBBusinessException {

    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [updateEcoFeeSkuRelationship]");
      logDebug(" pSkuId[" + pSkuId + "] pEcoSkuId[" + pEcoSkuVO.getEcoFeeSkuId() + "] pFeedId[" + pFeedId + "]");
    }
    EcoSkuVO itemSkuEcoVO;

    itemSkuEcoVO = pEcoSkuVO;
    final String ecoSkuId = itemSkuEcoVO.getEcoFeeSkuId();
    final String stateId = itemSkuEcoVO.getStateId();
    final String skuType = itemSkuEcoVO.getEcoFeeSkuType();

    if (StringUtils.isEmpty(ecoSkuId) || StringUtils.isEmpty(stateId) || StringUtils.isEmpty(skuType)) {

    final String errorMsg =new StringBuffer().append( "Missing Details. Can't Proceed for EcoSkuItem SkuId=" + pSkuId + "StateId=" + stateId
          + " ecoSkuId=" + ecoSkuId + " skuType=" + skuType).toString();
      if (isLoggingDebug()) {

        logDebug(errorMsg);

      }

      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", "SkuId=" + pSkuId, getDate(), errorMsg,
          pConnection);
      return null;
    }

   /* MutableRepositoryItem ecoSkuItem = getItem(ecoSkuId, BBBCatalogImportConstant.SKU_DESC);
    if (ecoSkuItem == null) {

      String  errorMsg = new StringBuffer().append("Eco Sku Id not found in the repository").toString();
      if (isLoggingDebug()) {

        logDebug(errorMsg);

      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", "SkuId=" + pSkuId + " ecoSkuId=" + ecoSkuId,
          getDate(), errorMsg, pConnection);
      return null;

    }*/

    final MutableRepositoryItem stateItem = getShippingItem(itemSkuEcoVO.getStateId(), BBBCatalogImportConstant.STATE);
    if (stateItem == null) {

    	final String errorMsg =new StringBuffer().append( "stateId " + stateId + " not found in the repository").toString();
      if (isLoggingDebug()) {

        logDebug(errorMsg);

      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", "SkuId=" + pSkuId + " ecoSkuId=" + ecoSkuId,
          getDate(), errorMsg, pConnection);
      return null;

    }
    if (isLoggingDebug()) {
      logDebug("Value for EcoSkuId =" + ecoSkuId);
    }

    final String skuEcoRepositoryId = getEcoFeeKeyId(pSkuId, ecoSkuId, stateId, skuType);

    MutableRepositoryItem skuEcoFeeRelItem = null;
    try {

      skuEcoFeeRelItem = (MutableRepositoryItem) ((MutableRepository)getProductCatalog()).getItem(skuEcoRepositoryId,
          BBBCatalogImportConstant.BBB_ECO_FEE_SKU);

      if (skuEcoFeeRelItem == null) {

        createItem(skuEcoRepositoryId, BBBCatalogImportConstant.BBB_ECO_FEE_SKU);
      }

      if (isLoggingDebug()) {
        logDebug("Value for skuEcoFeeRelItem =" + skuEcoFeeRelItem);
      }

      skuEcoFeeRelItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(skuEcoRepositoryId,
          BBBCatalogImportConstant.BBB_ECO_FEE_SKU);

      simplePropertyWrite(BBBCatalogImportConstant.STATE, stateItem, skuEcoFeeRelItem);
      
      if(isDirectToStaging()) {
         simplePropertyWrite(BBBCatalogImportConstant.ECO_FEE_SKU_STRING, ecoSkuId, skuEcoFeeRelItem);
      } else {
          simplePropertyWrite(BBBCatalogImportConstant.ECO_FEE_SKU, ecoSkuId, skuEcoFeeRelItem);  
      }
      
      simplePropertyWrite(BBBCatalogImportConstant.SKU_TYPE, skuType, skuEcoFeeRelItem);

      ((MutableRepository)getProductCatalog()).updateItem(skuEcoFeeRelItem);

    } catch (RepositoryException rex) {

      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(rex));
      }
      getPimFeedTools().updateBadRecords(pFeedId, "", "ECP_RELATED_ITEMS", "SkuId=" + pSkuId + " ecoSkuId=" + ecoSkuId,
          getDate(), new StringBuilder().append("Repository Exception=").append(rex.getMessage()).toString(), pConnection);
    }
    if (isLoggingDebug()) {

      logDebug("END Method Name [updateEcoFeeSkuRelationship]");
    }
    return skuEcoFeeRelItem;
  }

  /*****************************************
   * Associate Site with provided pRepositoryItem
   * 
   * @param pCategoryItem
   * @param pResultSet
   * @param pSiteId
   */

  @SuppressWarnings("unchecked")
  private void associateWithSite(final MutableRepositoryItem pRepositoryItem, final String pProperty,
      final String pPIMSiteList) {
    
    if (isLoggingDebug()) {
      logDebug("CatalogTools Method Name [associateWithSite]");
      logDebug("pRepositoryItem[" + pRepositoryItem + "] pProperty[" + pProperty + "] pPIMSiteList[" + pPIMSiteList
          + "] ");
    }

    boolean isSiteRelExist;
    if (StringUtils.isEmpty(pPIMSiteList)) {
	
      if (isLoggingDebug()) {
	  
        logDebug("pPIMSiteList is empty");
        logDebug("return setting null in sites");
      }
      pRepositoryItem.setPropertyValue(pProperty, null);
      return;
    }
    Set<String> siteIds = null;
    final List<String> activeSites = getActiveSites();
    siteIds = (Set<String>) pRepositoryItem.getPropertyValue(pProperty);
	    if (isLoggingDebug()) {
	  
      logDebug("Existing Sites Relationship=" +siteIds);
    }

    for (String siteId : activeSites ) {
      isSiteRelExist = validateRelationShip(siteId, pPIMSiteList);
      final String bbbsiteId = getPimSiteToBBBSiteMap().get(siteId);  
      if (isLoggingDebug()) {
	  
        logDebug("isSiteRelExist=" +isSiteRelExist + "bbbsiteId=" +bbbsiteId);
      }
      // If current siteId exist in the pPIMSiteList then add in the
      // site list else remove from the site list if
      // item
      // contains
      // in the list
      if (siteIds == null) {
        siteIds = new HashSet<String>();
      }

      if(isSiteRelExist) {
        siteIds.add(bbbsiteId);
      } else {
        
        
        if(siteIds.contains(bbbsiteId)) {
          
          siteIds.remove(bbbsiteId); 
        }
           
        
      }  
      
    }
      if (siteIds != null && !siteIds.isEmpty()) {
        
        try {
            
          simplePropertyWrite(pProperty, siteIds, pRepositoryItem);
        } catch (RepositoryException e) {
        if (isLoggingError()) {

          logError(siteIds + " has following issue ");
          logError(BBBStringUtils.stack2string(e));
        }
        if (isLoggingDebug()) {

          logDebug(BBBStringUtils.stack2string(e));
        }

        }
      }
  }

  private void associateSKUAttrbuteWithSite(final MutableRepositoryItem pRepositoryItem,final String pPIMSiteList) {
	    boolean siteFlag = false;
	    if (isLoggingDebug()) {
	      logDebug("CatalogTools Method Name [associateSKUAttrbuteWithSite]");
	      logDebug("pRepositoryItem[" + pRepositoryItem + "] pPIMSiteList[" + pPIMSiteList
	          + "] ");
	    }

	    boolean isSiteRelExist;
	    if (StringUtils.isEmpty(pPIMSiteList)) {
		
	      if (isLoggingDebug()) {
		  
	        logDebug("pPIMSiteList is empty");
	        logDebug("return setting null in site flags");
	      }
	     return;
	    }
	  
	    final List<String> activeSites = getActiveSites();
	   
		   

	    for (String siteId : activeSites ) {
	      siteFlag = false;
	      isSiteRelExist = validateRelationShip(siteId, pPIMSiteList);
	      final String bbbsiteId = getPimSiteToBBBSiteMap().get(siteId);  
	      if (isLoggingDebug()) {
		  
	        logDebug("isSiteRelExist=" +isSiteRelExist + "bbbsiteId=" +bbbsiteId);
	      }
	      // If current siteId exist in the pPIMSiteList then add in the
	      // site list else remove from the site list if
	      // item
	      // contains
	      // in the list
	      if(isSiteRelExist) {
	    	  siteFlag=true;
	      }   
	      try {	            
	          simplePropertyWrite(getSiteFlagsMapInRep().get(siteId), siteFlag, pRepositoryItem);
	        } catch (RepositoryException e) {
	        if (isLoggingError()) {

	          logError(getSiteFlagsMapInRep().get(siteId) + " has following issue ");
	          logError(BBBStringUtils.stack2string(e));
	        }
	        if (isLoggingDebug()) {

	          logDebug(BBBStringUtils.stack2string(e));
	        }

	        }
	    }
	      
	  }
   

 
  @SuppressWarnings("unused")
private void updateTranslationForProd (final String margin,final String siteNumForPIM,final MutableRepositoryItem productItem ) throws RepositoryException{

	  if(siteNumForPIM.equals("1")){
		  if (isLoggingDebug()) {
			  logDebug("updating margin property **************************************");
		  }
		  simplePropertyWrite(BBBCatalogImportConstant.MARGIN_DEFAULT, margin, productItem);
	  }
	  if (isLoggingDebug()) {
		  logDebug("creating translatiion **************************************");
	  }
	  addProdTranslationAttributes(siteNumForPIM, BBBCatalogImportConstant.EN_US, productItem,
			  BBBCatalogImportConstant.MARGIN,margin, 0.0, null,null,null);
  }
 
   
 
  /**
	 * This method is used to validate if product is active in the catalog or not
	 * For the product to be active  weboffered should be true and disable should be false
	 * Also if start and end date are not null then current date should be after start date
	 * and before end date.Also the sites to which the sku is associated should have the current site too
	 * @param productRepositoryItem
 * @param siteId 
	 * @return boolean
	 */
  @SuppressWarnings("unchecked")
	public boolean isProductActive(final RepositoryItem productRepositoryItem,final String siteId){
	  final Set<String> assocSites=(Set<String>) productRepositoryItem.getPropertyValue("siteIds");

		if(assocSites!=null && !assocSites.isEmpty()&& assocSites.contains(siteId)){
			// Edited as part of Instant preview story
			final java.util.Date previewDate = new java.util.Date();
			final Date startDate = (Date) productRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
			final Date endDate = (Date) productRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
			final Boolean webOffered=(Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME);
			final Boolean disable= (Boolean)productRepositoryItem.getPropertyValue	(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME);
			logDebug(productRepositoryItem.getRepositoryId()+" Product id details ::Product startDate["+startDate+
					"]Product endDate["+endDate+"]Product disable["+disable+"]Product webOffered["+webOffered+"]");
			if(((endDate!=null && previewDate.after(endDate))|| (startDate!=null&& previewDate.before(startDate))) ||(disable!=null && disable) ||(webOffered!=null && !webOffered)){
				return false;
			}
			return true;
		}
		return false;

	}

	/**
  	 * This method is used to update the product keywords in the catalog table
	 * from the PIM schema only if the the keywords are not empty or null in 
	 * the PIM tables and product exists in the product catalog. 
	 * R2.2 92-A story.
	 * 
  	 * @param feedId
  	 * @param productId
  	 * @param pConnection
  	 * @throws BBBSystemException
  	 */
  	public void updateProdKeywordItem(String feedId, String productId,
  			Connection pConnection) throws BBBSystemException {
  		if (isLoggingDebug()) {
  			logDebug("BBBCatalogLoadTools.updateProdKeywordItem() method starts");
  			logDebug("pFeedId[" + feedId + "]  itemId[" + productId + "] ");
  		}

  		ItemVO itemVO = new ItemVO();
  		MutableRepositoryItem productItem;

  		itemVO = getPimFeedTools().getPIMProductKeywordsDetail(feedId, productId, itemVO, pConnection);
  		if (isLoggingDebug()) {
  			logDebug("ItemVO after populating products keywords = " + itemVO);
  		}
 		/**
 		 * Commented out to fulfill BBBSL-3806 - update keyword even if null
 		 * if(!BBBUtility.isEmpty(itemVO.getProductVO().getKeywords())){
 		 */
  			try {
  				productItem = (MutableRepositoryItem) getProductCatalog().getItem(productId, BBBCatalogImportConstant.PRODUCT_DESC);

  				//Populate product keywords only if product exists in catalog schema.
  				if(productItem != null){
  					if(isLoggingDebug()){
  						logDebug("Updating product keywords for item id[" +productId + "]");
  					}
  					productItem = ((MutableRepository)getProductCatalog()).getItemForUpdate(productId, BBBCatalogImportConstant.PRODUCT_DESC);
  					simplePropertyWrite(BBBCatalogImportConstant.PRD_KEYWORDS, itemVO.getProductVO().getKeywords(), productItem);
  					((MutableRepository)getProductCatalog()).updateItem(productItem);
  				} else {
  					if(isLoggingDebug()){
  						logDebug("The product with item id[" +productId + "]doesn't exist in product catalog schema so not updating its keywords");
  					}
  					getPimFeedTools().updateBadRecords(feedId, "", "ECP_PRODUCTS_KEYWORDS",
  							productId, getDate(), "Item not exist in product catalog hence not updating its keywords",
  							pConnection);
  				}
  			} catch (RepositoryException excep) {
  				logError("Repository Exception while updating the product keywords for item ["+productId+"]", excep);
  				getPimFeedTools().updateBadRecords(feedId, "", "ECP_PRODUCTS_KEYWORDS",
  						productId, getDate(), "Repository Exception while updating keywords",
  						pConnection);
  			}
 		/**
 		 * }
 		 */
  		if (isLoggingDebug()) {
  			logDebug("BBBCatalogLoadTools.updateProdKeywordItem() method ends");
  		}
  	}
  	
  	


    /**
  	 * @throws BBBBusinessException 
  	 * @throws RepositoryException 
  	 * @throws BBBSystemException 
       * 
       */
  	public RepositoryItem[] getStagingDeployedProjects(String feedType) throws BBBSystemException, RepositoryException, BBBBusinessException{
  		
  		if (isLoggingDebug()) {
  			logDebug("MTHD=[getStagingDeployedProjects] MSG=[Enter]");
  		}
  		
  		final RepositoryView view = this.getDeployedProjectRepository().getView(BBBCatalogImportConstant.ITEM_DESCRIPTOR_PROJECT_INFO);
  		
  		final QueryBuilder builder = view.getQueryBuilder();
  		
  		Query query = null;
  		
  		QueryExpression feedTypeExpression = builder.createPropertyQueryExpression("projectType");			
		QueryExpression feedTypeValue = builder.createConstantQueryExpression(feedType);	
		query = builder.createComparisonQuery(feedTypeExpression, feedTypeValue, QueryBuilder.EQUALS);
  		
  		final SortDirectives sortDirectives = new SortDirectives();
  		sortDirectives.addDirective(new SortDirective(BBBCatalogImportConstant.PROPERTY_DEPLOYEMENT_TIME, SortDirective.DIR_DESCENDING));
  		final RepositoryItem[] deployedProjects = view.executeQuery(query, new QueryOptions(0, -1, sortDirectives,null));
  	
  			logDebug("MTHD=[getStagingDeployedProjects] MSG=[Return items="+deployedProjects);
  		
  		return deployedProjects;
  		
  	}

public String formatDate(java.sql.Date sqlDate){
	  
	  String dateAsString = null;
	  Format formatter = null;
	  formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	  dateAsString = formatter.format(sqlDate);
	  
	  return dateAsString;
  }


}
