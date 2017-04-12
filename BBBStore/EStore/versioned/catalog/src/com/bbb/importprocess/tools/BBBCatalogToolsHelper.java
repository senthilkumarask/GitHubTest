package com.bbb.importprocess.tools;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.adapter.version.CurrentVersionItem;
import atg.adapter.version.VersionRepository;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import com.bbb.common.BBBGenericService;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBStringUtils;

public class BBBCatalogToolsHelper extends BBBGenericService {

  private MutableRepository mConfigureRepository;
  private List<String> mActiveSites;
  private Repository mProductCatalog;
  private Repository mProdcutionProductCatalog;
  private Repository mProductionPriceListRepository;
  private Repository mSiteRepository;
  private Repository mSeoRepository;

  private int mBatchSize = 100;
  private BBBPIMFeedTools mBBBPIMFeedTools;

  private String mFolderId;
  private String mPath;
  private PriceListManager mPriceListManager;
  private Repository mShippingRepository;
  private Map<String, String> mSiteName;
  private Map<String, String> mListPriceListIdsMap;
  private Map<String, String> mSalePriceListIdsMap;
  private Map<String, String> mPimSiteToBBBSiteMap;
  private Map<String, String> siteFlagsMapInRep;
  private Map<String, String> mSiteIdRootCategoryIdMap;
  private boolean mbackWardCompatibilityFlag;
  private Map<String, String> mInCartPriceListIdsMap;

  
	
  /**
   * 
   * @return
   */
  public String getFolderId() {

    return mFolderId;
  }

  /**
   * 
   * @param pFolderId
   */
  public void setFolderId(final String pFolderId) {

    mFolderId = pFolderId;
  }

  /**
   * 
   * @return
   */
  public String getPath() {

    return mPath;
  }

  /**
   * @return the siteIdRootCategoryIdMap
   */
  public Map<String, String> getSiteIdRootCategoryIdMap() {
    return mSiteIdRootCategoryIdMap;
  }

  /**
   * @param pSiteIdRootCategoryIdMap
   *          the siteIdRootCategoryIdMap to set
   */
  public void setSiteIdRootCategoryIdMap(Map<String, String> pSiteIdRootCategoryIdMap) {
    mSiteIdRootCategoryIdMap = pSiteIdRootCategoryIdMap;
  }

  /**
   * @return the pimSiteToBBBSiteMap
   */
  public Map<String, String> getPimSiteToBBBSiteMap() {
    return mPimSiteToBBBSiteMap;
  }

  /**
   * @param pPimSiteToBBBSiteMap
   *          the pimSiteToBBBSiteMap to set
   */
  public void setPimSiteToBBBSiteMap(Map<String, String> pPimSiteToBBBSiteMap) {
    mPimSiteToBBBSiteMap = pPimSiteToBBBSiteMap;
  }

  public Date getDate() {

    java.util.Date date = new java.util.Date();
    long t = date.getTime();
    java.sql.Date sqlDate = new java.sql.Date(t);
    // java.sql.Time sqlTime = new java.sql.Time(t);
    // java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
    return sqlDate;
  }

  
   public java.util.Date getDate(java.sql.Date sqlDate) {

	    long t = sqlDate.getTime();
	    java.util.Date date = new java.util.Date(t);
		   
	    // java.sql.Time sqlTime = new java.sql.Time(t);
	    // java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
	    return date;
	  }
  
  
  public Timestamp getLastModifiedDate() {

    java.util.Date date = new java.util.Date();
    long t = date.getTime();
    java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
    return sqlTimestamp;
  }

  /**
   * Get Id
   * 
   * @param pSiteId
   * @param pCategoryId
   * @return
   */
  protected String getId(final String pId1, final String pId2) {
    return pId1 + BBBCatalogImportConstant.KEY_SEPERATOR + pId2;
  }
  
  /**
   * Get Id
   * 
   * @param pSiteId
   * @param pCategoryId
   * @return
   */
  protected String getId(final String pId1, final String pId2, final String pId3) {
    return pId1 + BBBCatalogImportConstant.KEY_SEPERATOR + pId2 + BBBCatalogImportConstant.KEY_SEPERATOR + pId3;
  }

  /**
   * Get Translation key
   * 
   * @param pSiteId
   * @param pLocale
   * @param pAttributeName
   * @param pItemId
   * @return
   */
  
  protected String getTranslationKey(final String pSiteId, final String pLocale, final String pAttributeName,
      String pItemId) {
    StringBuffer stringBuff = new StringBuffer();
    return stringBuff.append(pSiteId).append("_").append(pLocale).append("_").append(pItemId).append("_")
        .append(pAttributeName).toString();
  }
  /**
   * Get boolean Translation key
   * 
   * @param pSiteId
   * @param pLocale
   * @param pAttributeName
   * @param pAttributevalue
   * @return
   */
  protected String getBooleanTranslationKey(final String pSiteId, final String pLocale, final String pAttributeName, String pAttributeValue) {
		StringBuffer stringBuff = new StringBuffer();
		return stringBuff.append(pSiteId).append("_").append(pLocale).append("_").append(pAttributeName).append("_").append(pAttributeValue).toString();
	}
  /**
   * Get boolean Translation key to remove
   * 
   * @param pSiteId
   * @param pLocale
   * @param pAttributeName
   * @param pAttributevalue
   * @return
   */
  protected String getBooleanTranslationKeyToRemove(final String pSiteId, final String pLocale, final String pAttributeName, String pAttributeValue) {
	  
		StringBuffer stringBuff = new StringBuffer();
		if(pAttributeValue.endsWith("Y")){
		return stringBuff.append(pSiteId).append("_").append(pLocale).append("_").append(pAttributeName).append("_").append("N").toString();
		}else{
			return stringBuff.append(pSiteId).append("_").append(pLocale).append("_").append(pAttributeName).append("_").append("Y").toString();
		}
	}

  /**
   * Get Eco Fee Id
   * 
   * @param pSkuId
   * @param pEcoSkuId
   * @param pStateId
   * @param pSkuType
   * @return
   */
  protected String getEcoFeeKeyId(final String pSkuId, final String pEcoSkuId, final String pStateId, String pSkuType) {
    StringBuffer stringBuff = new StringBuffer();
    return stringBuff.append(pSkuId).append(BBBCatalogImportConstant.KEY_SEPERATOR).append(pEcoSkuId)
        .append(BBBCatalogImportConstant.KEY_SEPERATOR).append(pStateId).append(BBBCatalogImportConstant.KEY_SEPERATOR)
        .append(pSkuType).toString();
  }



  /**
   * Get the Site based Sale Price Id
   * 
   * @param pSiteId
   * @return
   */
  protected String getSalePriceListId(String pSiteId) {

    return getSalePriceListIdsMap().get(pSiteId);
  }

  /**
   * Get the Site based List Price Id
   * 
   * @param pSiteId
   * @return
   */
  protected String getListPriceListId(String pSiteId) {

    return getListPriceListIdsMap().get(pSiteId);
  }
  
  
  /**
   * Get the Site based In Cart Price Id
   * 
   * @param pSiteId
   * @return
   */
  protected String getInCartPriceListId(String pSiteId) {

    return getInCartPriceListIdsMap().get(pSiteId);
  }

  /**
   * 
   * @param pdeptId
   * @param psubDeptId
   * @param pclassId
   * @return
   */
  protected String getClassId(final String pId1, final String pId2, final String pId3) {
    return pId1 + ":" + pId2 + ":" + pId3;
  }

  /**
   * 
   * @param pdeptId
   * @param psubDeptId
   * @param pclassId
   * @return
   */
  protected String getThreshHoldId(final String pId1, final String pId2, final String pId3, final String pId4) {
    
	
	  String  id = BBBCoreConstants.BLANK;
	  

		if (null != pId1) {
			
			id = "d_"+pId1;
		}
		if (null != pId2) {
			
			if (id.equals(BBBCoreConstants.BLANK)) {
				id = "s_"+pId2;
			} else {
				id = id + BBBCoreConstants.UNDERSCORE + "s_"+pId2;
			}

		}
		if (null != pId3) {

			if (id.equals(BBBCoreConstants.BLANK)) {
				id = "c_"+pId3;
			} else {
				id = id + BBBCoreConstants.UNDERSCORE + "c_" + pId3;
			}

		}
		if (null != pId4) {
			if (id.equals(BBBCoreConstants.BLANK)) {
				id = "sku_" +pId4;
			} else {
				id = id + BBBCoreConstants.UNDERSCORE + "sku_" +pId4;
			}

		}
	  return id;
	  
  }

  
  /**
   * @return the priceListManager
   */
  public PriceListManager getPriceListManager() {
    return mPriceListManager;
  }

  /**
   * @param pPriceListManager
   *          the priceListManager to set
   */
  public void setPriceListManager(PriceListManager pPriceListManager) {
    mPriceListManager = pPriceListManager;
  }

  /**
   * @return the listPriceListIdsMap
   */
  public Map<String, String> getListPriceListIdsMap() {
    return mListPriceListIdsMap;
  }

  /**
   * @param pListPriceListIdsMap
   *          the listPriceListIdsMap to set
   */
  public void setListPriceListIdsMap(Map<String, String> pListPriceListIdsMap) {
    mListPriceListIdsMap = pListPriceListIdsMap;
  }

  /**
   * @return the salePriceListIdsMap
   */
  public Map<String, String> getSalePriceListIdsMap() {
    return mSalePriceListIdsMap;
  }

  /**
   * @param pSalePriceListIdsMap
   *          the salePriceListIdsMap to set
   */
  public void setSalePriceListIdsMap(Map<String, String> pSalePriceListIdsMap) {
    mSalePriceListIdsMap = pSalePriceListIdsMap;
  }

  /**
   * 
   * @return
   */
  public Repository getProductCatalog() {

    return mProductCatalog;
  }

  /**
   * 
   * @param pProductCatalog
   */
  public void setProductCatalog(final Repository pProductCatalog) {

    this.mProductCatalog = pProductCatalog;
  }
  
  /**
   * 
   * @return
   */
  public Repository getProductionPriceListRepository() {

    return mProductionPriceListRepository;
  }

   /**
   * 
   * @param pProductionPriceListRepository
   */
  public void setProductionPriceListRepository(final Repository pProductionPriceListRepository) {
    
    this.mProductionPriceListRepository = pProductionPriceListRepository;
  }

  /**
   * 
   * @return
   */
  public Repository getSeoRepository() {

    return mSeoRepository;
  }

  /**
   * 
   * @param pProductCatalog
   */
  public void setSeoRepository(final Repository pSeoRepository) {

    this.mSeoRepository = pSeoRepository;
  }

  /**
   * @return the shippingRepository
   */
  public Repository getShippingRepository() {
    return mShippingRepository;
  }

  /**
   * @param pShippingRepository
   *          the shippingRepository to set
   */
  public void setShippingRepository(Repository pShippingRepository) {
    mShippingRepository = pShippingRepository;
  }

  /**
   * 
   * @return
   */
  public List<String> getActiveSites() {

    return mActiveSites;
  }

  /**
   * 
   * @param pActiveSites
   */
  public void setActiveSites(final List<String> pActiveSites) {

    this.mActiveSites = pActiveSites;
  }

  /**
   * 
   * @return
   */
  public BBBPIMFeedTools getPimFeedTools() {

    return mBBBPIMFeedTools;
  }

  /**
   * 
   * @param pBBBPIMFeedTools
   */
  public void setPimFeedTools(final BBBPIMFeedTools pBBBPIMFeedTools) {

    mBBBPIMFeedTools = pBBBPIMFeedTools;
  }

  /**
   * @return the siteRepository
   */
  public Repository getSiteRepository() {
    return mSiteRepository;
  }

  /**
   * @param pSiteRepository
   *          the siteRepository to set
   */
  public void setSiteRepository(Repository pSiteRepository) {
    mSiteRepository = pSiteRepository;
  }

  /**
   * @return the siteName
   */
  public Map<String, String> getSiteName() {

    return mSiteName;
  }

  /**
   * @param mSiteName
   *          the siteName to set
   */
  public void setSiteName(Map<String, String> pSiteName) {

    mSiteName = pSiteName;
  }

  /**
   * 
   * @return
   */
  public int getBatchSize() {

    return mBatchSize;
  }

  /**
   * 
   * @param pBatchSize
   */
  public void setBatchSize(final int pBatchSize) {

    mBatchSize = pBatchSize;
  }

  /**
   * 
   * @return
   */
  public MutableRepository getConfigureRepository() {

    return mConfigureRepository;
  }

  /**
   * @param configRepository
   *          the configRepository to set
   */
  public void setConfigureRepository(MutableRepository pConfigRepository) {

    mConfigureRepository = pConfigRepository;
  }

  /**
   * 
   * @return
   */
  public Repository getProductionProductCatalog() {

    return mProdcutionProductCatalog;
  }

  /**
   * 
   * @param pProductCatalog
   */
  public void setProductionProductCatalog(final Repository pProductionProductCatalog) {

    this.mProdcutionProductCatalog = pProductionProductCatalog;
  }
  
  
  /**
   * 
   * @param isBatchFeed
   * @param pCategoryIds
   * @return
   */
  public int getMaxBatchCount(final boolean isBatchFeed, final List<String> pCategoryIds) {

    int maxBatchCount = pCategoryIds.size();
    double size;
    if (getBatchSize() > 0) {
      size = pCategoryIds.size();
      maxBatchCount = (int) Math.ceil(size / getBatchSize());
    }
    return maxBatchCount;
  }

  /**
   * 
   * @param pCategoryIds
   * @return
   */
  public List<List<String>> getBatchList(final List<String> pCategoryIds) {

    int startIndex;
    int endIndex;
    int totalRecords = pCategoryIds.size();

    final List<List<String>> subList = new ArrayList<List<String>>();

    /*
     * if(isOneTimeFeed()) { setBatchSize(1); }
     */
    if (getBatchSize() <= 0 || totalRecords <= getBatchSize()) {

      subList.add(pCategoryIds);
      return subList;
    }

    final double total = Math.ceil((double) totalRecords / getBatchSize());
    for (int batchCount = 0; batchCount < total; batchCount++) {

      List<String> batchList;
      totalRecords = pCategoryIds.size();
      startIndex = getBatchSize() * batchCount;
      endIndex = getBatchSize() * batchCount + getBatchSize();

      if (startIndex >= totalRecords) {
        startIndex = totalRecords - 1;
        endIndex = totalRecords;
      } else if (endIndex >= totalRecords) {

        endIndex = totalRecords;
      }
      if (isLoggingDebug()) {
        logDebug("startIndex=" + startIndex + " " + "endIndex" + endIndex);
      }
      batchList = pCategoryIds.subList(startIndex, endIndex);
      subList.add(batchList);
    }
    return subList;

  }

  /**
   * 
   * @param pPath
   */
  public void setPath(final String pPath) {

    mPath = pPath;
  }

  
  /**
   * This method get MutableRepositoryItem for specified pId and pItemDescriptor
   * 
   * @param pId
   * @param pItemDescriptor
   * @return
   */
  public MutableRepositoryItem getItem(final String pId, final String pItemDescriptor) {

    MutableRepositoryItem item = null;

    if (StringUtils.isEmpty(pId)) {

      return null;
    }
    if (isLoggingDebug()) {

      logDebug("Get item for id=" + pId + "in Item Descriptor" + pItemDescriptor);
    }
    try {
      item = (MutableRepositoryItem) getProductCatalog().getItem(pId, pItemDescriptor);
      if (isLoggingDebug()) {
        logDebug("got item=" + item);
      }
    } catch (RepositoryException e) {
      if (isLoggingError()) {

        logError(pId + " has following issue ");
        logError(BBBStringUtils.stack2string(e));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(e));
      }
    }
    return item;
  }

  
  /**
   * This method get MutableRepositoryItem for specified pId and pItemDescriptor
   * 
   * @param pId
   * @param pItemDescriptor
   * @return
   */
  public RepositoryItem getProductionItem(final String pId, final String pItemDescriptor) {

    RepositoryItem item = null;

    if (StringUtils.isEmpty(pId)) {

      return null;
    }
    if (isLoggingDebug()) {

      logDebug("Get item for id=" + pId + "in Item Descriptor" + pItemDescriptor);
    }
    try {
      item = (RepositoryItem) getProductionProductCatalog().getItem(pId, pItemDescriptor);
      if (isLoggingDebug()) {
        logDebug("got item=" + item);
      }
    } catch (RepositoryException e) {
      if (isLoggingError()) {

        logError(pId + " has following issue ");
        logError(BBBStringUtils.stack2string(e));
      }
      if (isLoggingDebug()) {

        logDebug(BBBStringUtils.stack2string(e));
      }
    }
    return item;
  }


  /**
   * This method writes the new value to the item
   */
  protected void simplePropertyWrite(final String pPropertyName, final Object pValue, final MutableRepositoryItem item) throws RepositoryException {

    if (isLoggingDebug()) {

      logDebug("propertyName=" + pPropertyName + " value=" + pValue);
    }
    /*
     * if (pValue == null) { return; }
     */

    item.setPropertyValue(pPropertyName, pValue);

  }

  /**
   * If ResultSet parameter value is Y return boolean true else return boolean
   * false
   * 
   * @param pString
   * @return
   */
  protected boolean getBoolean(String pString) {

    boolean flag;

    String data = pString;
    if (data != null && data.trim().equals("Y")) {
      flag = true;
    } else {
      flag = false;
    }

    return flag;
  }

  public static boolean isNumeric(String stringToTest){
	    boolean isNumeric;
	    try{
	        Integer.parseInt(stringToTest);
	        isNumeric = true;
	    } catch(NumberFormatException e) {
	        isNumeric = false;
	    }
	    return isNumeric;
	}

  public String makeList(List<String> pArrayList, String pSeparator) {

    StringBuffer result = new StringBuffer();
    if (pArrayList != null) {
      Iterator<String> element = pArrayList.iterator();
      do {
        if (!element.hasNext())
          break;
        Object next = element.next();
        result.append(next.toString());
        if (pSeparator != null && element.hasNext())
          result.append(pSeparator);
      } while (true);
    }
    return result.toString();
  }

/**
 * @return the siteFlagsMapInRep
 */
public Map<String, String> getSiteFlagsMapInRep() {
	return siteFlagsMapInRep;
}

/**
 * @param siteFlagsMapInRep the siteFlagsMapInRep to set
 */
public void setSiteFlagsMapInRep(Map<String, String> siteFlagsMapInRep) {
	this.siteFlagsMapInRep = siteFlagsMapInRep;
}

/**
 * @return the mbackWardCompatibilityFlag
 */
public boolean isBackWardCompatibilityFlag() {
	return mbackWardCompatibilityFlag;
}

/**
 * @param mbackWardCompatibilityFlag the mbackWardCompatibilityFlag to set
 */
public void setBackWardCompatibilityFlag(boolean mbackWardCompatibilityFlag) {
	this.mbackWardCompatibilityFlag = mbackWardCompatibilityFlag;
}

/**
 * 
 * @return mInCartPriceListIdsMap
 */
public Map<String, String> getInCartPriceListIdsMap() {
	return mInCartPriceListIdsMap;
}

/**
 * 
 * @param mInCartPriceListIdsMap to set
 */
public void setInCartPriceListIdsMap(Map<String, String> mInCartPriceListIdsMap) {
	this.mInCartPriceListIdsMap = mInCartPriceListIdsMap;
}

}
