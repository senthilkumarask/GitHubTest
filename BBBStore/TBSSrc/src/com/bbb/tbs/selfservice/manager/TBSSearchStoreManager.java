package com.bbb.tbs.selfservice.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.TBSCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.TBSBopusInventoryService;
import com.bbb.commerce.inventory.TBSInventoryManagerImpl;
import com.bbb.commerce.vo.InventoryStatusVO;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.tbs.selfservice.tools.TBSStoreTools;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.query.Builder;
import atg.commerce.inventory.InventoryException;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

public class TBSSearchStoreManager extends SearchStoreManager{
	
	
	/**
     * mStoreRepository property to hold StoreRepository
     */
    private GSARepository mStoreRepository;
    /**
     * mSqlQuery property to hold SqlQuery for getting the near by store
     */
    private String mSqlQuery;
    /**
     * mDefaultStoreRadius property to hold DefaultStoreRadius
     */
    private int mDefaultStoreRadius;
    /**
     * mWarehouseStoreIds property to hold WarehouseStoreIds
     */
    private List<String> mWarehouseStoreIds = new ArrayList<String>();
    /**
     * mBedBathBeyondStoreIds property to hold BedBathBeyondFulfillmentStoreIds
     */
    private List<String> mBedBathBeyondStoreIds = new ArrayList<String>();
    /**
     * mBuyBuyBabyStoreIds property to hold BuyBuyBabyFulfillmentStoreIds
     */
    private List<String> mBuyBuyBabyStoreIds = new ArrayList<String>();
    /**
     * mCanadaStoreIds property to hold CanadaFulfillmentStoreIds
     */
    private List<String> mCanadaStoreIds = new ArrayList<String>();
    /**
     * mInventoryManager property to hold BBBInventoryManager
     */
    private TBSInventoryManagerImpl mInventoryManager;
    
    private Map<String, String> mShipmentTimeMap = new HashMap<String, String>();
    /**
     * mDefaultSuperStoreNum
     */
    private String mDefaultSuperStoreNum;
    
	private TBSBopusInventoryService mBopusService;
	
	List<String> mDepartmentalStores = new ArrayList<String>();
	
	private TBSInventoryManagerImpl mTbsInventoryManager;
	
	/**
	 * This attribute holds site wise RF inventory attribute
	 */
	private Map<String, String> rfInventoryAttrMap;
	/**
	 * This attribute holds site wise ORF inventory attribute
	 */
	private Map<String, String> orfInventoryAttrMap;
	
	/**
	 * @return the tbsInventoryManager
	 */
	public TBSInventoryManagerImpl getTbsInventoryManager() {
		return mTbsInventoryManager;
	}
	/**
	 * @param pTbsInventoryManager the tbsInventoryManager to set
	 */
	public void setTbsInventoryManager(TBSInventoryManagerImpl pTbsInventoryManager) {
		mTbsInventoryManager = pTbsInventoryManager;
	}
	
	/**
	 * @return the departmentalStores
	 */
	public List<String> getDepartmentalStores() {
		return mDepartmentalStores;
	}
	/**
	 * @param pDepartmentalStores the departmentalStores to set
	 */
	public void setDepartmentalStores(List<String> pDepartmentalStores) {
		mDepartmentalStores = pDepartmentalStores;
	}
	/**
	 * @return the bopusService
	 */
	public TBSBopusInventoryService getBopusService() {
		return mBopusService;
	}
	/**
	 * @param pBopusService the bopusService to set
	 */
	public void setBopusService(TBSBopusInventoryService pBopusService) {
		mBopusService = pBopusService;
	}
    
    /**
	 * @return the storeRepository
	 */
	public GSARepository getStoreRepository() {
		return mStoreRepository;
	}

	/**
	 * @return the sqlQuery
	 */
	public String getSqlQuery() {
		return mSqlQuery;
	}

	/**
	 * @param pStoreRepository the storeRepository to set
	 */
	public void setStoreRepository(GSARepository pStoreRepository) {
		mStoreRepository = pStoreRepository;
	}

	/**
	 * @param pSqlQuery the sqlQuery to set
	 */
	public void setSqlQuery(String pSqlQuery) {
		mSqlQuery = pSqlQuery;
	}

	/**
	 * @return the defaultStoreRadius
	 */
	public int getDefaultStoreRadius() {
		return mDefaultStoreRadius;
	}

	/**
	 * @param pDefaultStoreRadius the defaultStoreRadius to set
	 */
	public void setDefaultStoreRadius(int pDefaultStoreRadius) {
		mDefaultStoreRadius = pDefaultStoreRadius;
	}

	/**
	 * @return the warehouseStoreIds
	 */
	public List<String> getWarehouseStoreIds() {
		return mWarehouseStoreIds;
	}

	/**
	 * @return the bedBathBeyondStoreIds
	 */
	public List<String> getBedBathBeyondStoreIds() {
		return mBedBathBeyondStoreIds;
	}

	/**
	 * @return the buyBuyBabyStoreIds
	 */
	public List<String> getBuyBuyBabyStoreIds() {
		return mBuyBuyBabyStoreIds;
	}

	/**
	 * @return the canadaFulfillmentStoreIds
	 */
	public List<String> getCanadaStoreIds() {
		return mCanadaStoreIds;
	}

	/**
	 * @param pWarehouseStoreIds the warehouseStoreIds to set
	 */
	public void setWarehouseStoreIds(List<String> pWarehouseStoreIds) {
		mWarehouseStoreIds = pWarehouseStoreIds;
	}

	/**
	 * @param pBedBathBeyondStoreIds the bedBathBeyondFulfillmentStoreIds to set
	 */
	public void setBedBathBeyondStoreIds(
			List<String> pBedBathBeyondStoreIds) {
		mBedBathBeyondStoreIds = pBedBathBeyondStoreIds;
	}

	/**
	 * @param pBuyBuyBabyStoreIds the buyBuyBabyStoreIds to set
	 */
	public void setBuyBuyBabyStoreIds(
			List<String> pBuyBuyBabyStoreIds) {
		mBuyBuyBabyStoreIds = pBuyBuyBabyStoreIds;
	}

	/**
	 * @param pCanadaStoreIds the canadaFulfillmentStoreIds to set
	 */
	public void setCanadaStoreIds(List<String> pCanadaStoreIds) {
		mCanadaStoreIds = pCanadaStoreIds;
	}

	/**
	 * @return the inventoryManager
	 */
	public TBSInventoryManagerImpl getInventoryManager() {
		return mInventoryManager;
	}

	/**
	 * @param pInventoryManager the inventoryManager to set
	 */
	public void setInventoryManager(TBSInventoryManagerImpl pInventoryManager) {
		mInventoryManager = pInventoryManager;
	}

	/**
	 * @return the shipmentTimeMap
	 */
	public Map<String, String> getShipmentTimeMap() {
		return mShipmentTimeMap;
	}

	/**
	 * @param pShipmentTimeMap the shipmentTimeMap to set
	 */
	public void setShipmentTimeMap(Map<String, String> pShipmentTimeMap) {
		mShipmentTimeMap = pShipmentTimeMap;
	}
	
	/**
	 * @return the defaultSuperStoreNum
	 */
	public String getDefaultSuperStoreNum() {
		return mDefaultSuperStoreNum;
	}

	/**
	 * @param pDefaultSuperStoreNum the defaultSuperStoreNum to set
	 */
	public void setDefaultSuperStoreNum(String pDefaultSuperStoreNum) {
		mDefaultSuperStoreNum = pDefaultSuperStoreNum;
	}

	/**
	 * Method to perform a store lookup 
	 * @param pStoreId
     * @return StoreDetails
     * @throws RepositoryException
     * @throws SQLException
	 */
	public StoreDetails findStoreById(String pStoreId) throws RepositoryException, SQLException {
		RepositoryItem storeItem = getStoreRepository().getItem(pStoreId, TBSConstants.STORE);		
		return convertStoreItemToStore(storeItem);
	}
	
	/**
     * This method is used to get the list of the storeItems based on the storeid, latitude, longitude and radius.
     * @param pStoreId
     * @param pSiteId
     * @return
     * @throws RepositoryException
     * @throws SQLException
     */
    @SuppressWarnings("rawtypes")
	public RepositoryItem[] searchNearByStoresFromLatLong(Map latLong,Object changeRadius) throws RepositoryException, SQLException {
    	
    	vlogDebug("TBSSearchStoreManager :: searchNearByStoresFromLatLong() :: START");
    	String finalRadius = (String)changeRadius;
    	RepositoryItem[] lItems = null;
    	Object lLatitude = null;
    	Object lLongitude = null;
    	Object countryCode = BBBCoreConstants.BLANK,storeType = 0;
    	int radius = 0;
		
		if(finalRadius!=null){
			radius = Integer.parseInt(finalRadius);
			//changedRadius = changeRadius.intValue();
		}
        if(latLong != null && radius != 0){
        	
        	Iterator keySetIterator = latLong.keySet().iterator();
        	
        	lLatitude = latLong.get("lat");
        	lLongitude = latLong.get("lng");  
        	
	      	 if(isLoggingDebug())
	      	 {
	      	  logDebug("lLatitude: " + latLong.get("lat") + " lLongitude: " + latLong.get("lng"));
	      	 }
	      	String currentSite = SiteContextManager.getCurrentSiteId();
        	if(currentSite.equals(TBSConstants.SITE_TBS_BAB_US)){
        		countryCode="USA";
        		storeType=10;
        	}else if(currentSite.equals(TBSConstants.SITE_TBS_BBB)){
        		countryCode="USA";
        		storeType=40;
        	}else if(currentSite.equals(TBSConstants.SITE_TBS_BAB_CA)){
        		countryCode="CAN";
        		storeType=50;
        	}
        	
        	Object params[] = new Object[5];
        	params[0] = storeType;
        	params[1] = countryCode;
        	params[2] = lLatitude;
        	params[3] = lLongitude;
        	params[4] = radius;
         	
        	RepositoryView lView = getStoreRepository().getView(TBSConstants.STORE);
        	Builder lBuilder = (Builder) lView.getQueryBuilder();
        	Query lQuery = lBuilder.createSqlPassthroughQuery(getSqlQuery(), params);
        	lItems = lView.executeQuery(lQuery);
        	
        }
        if (lItems != null) {
			vlogDebug("Store Items :: " + lItems);
		}
		vlogDebug("TBSSearchStoreManager :: searchNearByStoresFromLatLong() :: END");
        return  lItems;
    }

	/**
     * This method is used to get the list of the storeItems based on the storeid, latitude, longitude and radius.
     * @param pStoreId
     * @param pSiteId
     * @return
     * @throws RepositoryException
     * @throws SQLException
     */
    public RepositoryItem[] searchNearByStores(String pStoreId) throws RepositoryException, SQLException {
    	
    	vlogDebug("TBSSearchStoreManager :: searchNearByStores() :: START");
    	RepositoryItem[] lItems = null;
        RepositoryItem lStoreItem = getStoreRepository().getItem(pStoreId, TBSConstants.STORE);
        if(lStoreItem != null){
        	Object lLatitude = lStoreItem.getPropertyValue(TBSConstants.LATITUDE);
        	Object lLongitude =  lStoreItem.getPropertyValue(TBSConstants.LONGITUDE);
        	String currentSite = SiteContextManager.getCurrentSiteId();
        	Object countryCode = BBBCoreConstants.BLANK,storeType = 0;
        	if(currentSite.equals(TBSConstants.SITE_TBS_BAB_US)){
        		countryCode="USA";
        		storeType=10;
        	}else if(currentSite.equals(TBSConstants.SITE_TBS_BAB_CA)){
        		countryCode="CAN";
        		storeType=50;
        	}else if(currentSite.equals(TBSConstants.SITE_TBS_BBB)){
        		countryCode="USA";
        		storeType=40;
        	}
        		
        	Object params[] = new Object[5];
        	params[0] = storeType;
        	params[1] = countryCode;
        	params[2] = lLatitude;
        	params[3] = lLongitude;
        	params[4] = getDefaultStoreRadius();
        	
        	RepositoryView lView = getStoreRepository().getView(TBSConstants.STORE);
        	Builder lBuilder = (Builder) lView.getQueryBuilder();
        	Query lQuery = lBuilder.createSqlPassthroughQuery(getSqlQuery(), params);
        	lItems = lView.executeQuery(lQuery);
        }
        if (lItems != null) {
			vlogDebug("Sore Items :: " + lItems);
		}
		vlogDebug("TBSSearchStoreManager :: searchNearByStores() :: END");
        return  lItems;
    }
    /**
     * This method is used to convert the store item into StoreDetails object
     * @param pRepositoryItem
     * @param pCurrentLatLongMap 
     * @param pObject 
     * @return
     */
	@SuppressWarnings("unused")
	public StoreDetails convertStoreItemToStore(RepositoryItem pRepositoryItem) {
		vlogDebug("TBSSearchStoreManager :: convertStoreItemToStore() :: START");
		StoreDetails storeDetails = null;
		if(pRepositoryItem != null){
			String storeId = null;
			String storeName = null;
			String storeDescription = null;
			String address = null;
			String city = null;
			String state = null;
			String conutry = null;
			String postalCode = null;
			String satStoreTimings = null;
			String sunStoreTimings = null;
			String weekdaysStoreTimings = null;
			String otherTimings1 = null;
			String otherTimings2 = null;
			String storePhone = null;
			String imageCode = null;
			String distance = null;
			String distanceUnit = null;
			String recordNumber = null;
			String longitude = null;
			String latitude = null;
			String specialtyShopsCd = null;
			String storeType = null;
			
			if(pRepositoryItem.getPropertyValue("id") != null){
				storeId = (String) pRepositoryItem.getPropertyValue("id");
			}
			if(pRepositoryItem.getPropertyValue("storeName") != null){
				storeName = (String) pRepositoryItem.getPropertyValue("storeName");
				storeDescription = (String) pRepositoryItem.getPropertyValue("storeName");
			}
			if(pRepositoryItem.getPropertyValue("address") != null){
				address = (String) pRepositoryItem.getPropertyValue("address");
			}
			if(pRepositoryItem.getPropertyValue("city") != null){
				city = (String) pRepositoryItem.getPropertyValue("city");
			}
			if(pRepositoryItem.getPropertyValue("state") != null){
				state = (String) pRepositoryItem.getPropertyValue("state");
			}
			if(pRepositoryItem.getPropertyValue("countryCode") != null){
				conutry = (String) pRepositoryItem.getPropertyValue("countryCode");
			}
			if(pRepositoryItem.getPropertyValue("postalCode") != null){
				postalCode = (String) pRepositoryItem.getPropertyValue("postalCode");
			}
			if(pRepositoryItem.getPropertyValue("satOpen") != null){
				satStoreTimings = ((Integer) pRepositoryItem.getPropertyValue("satOpen")).toString();
			}
			if(pRepositoryItem.getPropertyValue("satClose") != null){
				satStoreTimings += TBSConstants.HIFEN + ((Integer) pRepositoryItem.getPropertyValue("satClose")).toString();
			}
			if(pRepositoryItem.getPropertyValue("sunOpen") != null){
				sunStoreTimings = ((Integer) pRepositoryItem.getPropertyValue("sunOpen")).toString();
			}
			if(pRepositoryItem.getPropertyValue("sunClose") != null){
				sunStoreTimings += TBSConstants.HIFEN + ((Integer) pRepositoryItem.getPropertyValue("sunClose")).toString();
			}
			if(pRepositoryItem.getPropertyValue("phone") != null){
				storePhone = (String) pRepositoryItem.getPropertyValue("phone");
			}
			if(pRepositoryItem.getPropertyValue("hours") != null){
				otherTimings1 = (String) pRepositoryItem.getPropertyValue("hours");
			}
			if(pRepositoryItem.getPropertyValue("longitude") != null){
				longitude = (String) pRepositoryItem.getPropertyValue("longitude");
			}
			if(pRepositoryItem.getPropertyValue("latitude") != null){
				latitude = (String) pRepositoryItem.getPropertyValue("latitude");
			}
			if(pRepositoryItem.getPropertyValue("storeType") != null){
				storeType = (String) pRepositoryItem.getPropertyValue("storeType");
			}
	
			storeDetails = new StoreDetails(storeId, storeName, storeDescription, address, city, state, conutry, postalCode,
					satStoreTimings, sunStoreTimings, weekdaysStoreTimings, otherTimings1, otherTimings2, storePhone, imageCode, distance,
					distanceUnit, recordNumber, longitude, latitude, specialtyShopsCd, storeType);
		}
		vlogDebug("TBSSearchStoreManager :: convertStoreItemToStore() :: END");
		return storeDetails;
		
	}
    /**
     * This method is used to convert the store item into StoreDetails object, and to caluclate 
     * distance between two points based on latitude and longitude of the two points.
     * @param pRepositoryItem
     * @param currentStoreItem 
     * @param pCurrentLatLong 
     * @return
     */
	public StoreDetails convertStoreItemToStore(RepositoryItem pRepositoryItem, RepositoryItem currentStoreItem, Map pCurrentLatLong) {
		vlogDebug("TBSSearchStoreManager :: convertStoreItemToStore() :: START");
		StoreDetails storeDetails = null;
		if(pRepositoryItem != null){
			String storeId = null;
			String storeName = null;
			String storeDescription = null;
			String address = null;
			String city = null;
			String state = null;
			String conutry = null;
			String postalCode = null;
			String satStoreTimings = null;
			String sunStoreTimings = null;
			String weekdaysStoreTimings = null;
			String otherTimings1 = null;
			String otherTimings2 = null;
			String storePhone = null;
			String imageCode = null;
			String distance = null;
			String distanceUnit = null;
			String recordNumber = null;
			String longitude = null;
			String latitude = null;
			String specialtyShopsCd = null;
			String storeType = null;
			String currenStoreLatitude = null;
			String currenStoreLongitude = null;
			Object currentZipLatitude = null;
	    	Object currentZipLongitude = null;
			
			if(pRepositoryItem.getPropertyValue("id") != null){
				storeId = (String) pRepositoryItem.getPropertyValue("id");
			}
			if(pRepositoryItem.getPropertyValue("storeName") != null){
				storeName = (String) pRepositoryItem.getPropertyValue("storeName");
				storeDescription = (String) pRepositoryItem.getPropertyValue("storeName");
			}
			if(pRepositoryItem.getPropertyValue("address") != null){
				address = (String) pRepositoryItem.getPropertyValue("address");
			}
			if(pRepositoryItem.getPropertyValue("city") != null){
				city = (String) pRepositoryItem.getPropertyValue("city");
			}
			if(pRepositoryItem.getPropertyValue("state") != null){
				state = (String) pRepositoryItem.getPropertyValue("state");
			}
			if(pRepositoryItem.getPropertyValue("province") != null){
				state = (String) pRepositoryItem.getPropertyValue("province");
			}
			if(pRepositoryItem.getPropertyValue("countryCode") != null){
				conutry = (String) pRepositoryItem.getPropertyValue("countryCode");
			}
			if(pRepositoryItem.getPropertyValue("postalCode") != null){
				postalCode = (String) pRepositoryItem.getPropertyValue("postalCode");
			}
			if(pRepositoryItem.getPropertyValue("satOpen") != null){
				satStoreTimings = ((Integer) pRepositoryItem.getPropertyValue("satOpen")).toString();
			}
			if(pRepositoryItem.getPropertyValue("satClose") != null){
				satStoreTimings += TBSConstants.HIFEN + ((Integer) pRepositoryItem.getPropertyValue("satClose")).toString();
			}
			if(pRepositoryItem.getPropertyValue("sunOpen") != null){
				sunStoreTimings = ((Integer) pRepositoryItem.getPropertyValue("sunOpen")).toString();
			}
			if(pRepositoryItem.getPropertyValue("sunClose") != null){
				sunStoreTimings += TBSConstants.HIFEN + ((Integer) pRepositoryItem.getPropertyValue("sunClose")).toString();
			}
			if(pRepositoryItem.getPropertyValue("phone") != null){
				storePhone = (String) pRepositoryItem.getPropertyValue("phone");
			}
			if(pRepositoryItem.getPropertyValue("hours") != null){
				otherTimings1 = (String) pRepositoryItem.getPropertyValue("hours");
			}
			if(pRepositoryItem.getPropertyValue("longitude") != null){
				longitude = (String) pRepositoryItem.getPropertyValue("longitude");
			}
			if(pRepositoryItem.getPropertyValue("latitude") != null){
				latitude = (String) pRepositoryItem.getPropertyValue("latitude");
			}
			if(pRepositoryItem.getPropertyValue("storeType") != null){
				storeType = (String) pRepositoryItem.getPropertyValue("storeType");
			}
			if(pCurrentLatLong!=null){
				currentZipLatitude = pCurrentLatLong.get("lat");
				currentZipLongitude = pCurrentLatLong.get("lng"); 
				if(currentZipLongitude != null && currentZipLatitude !=null && (SiteContextManager.getCurrentSiteId().equals(TBSConstants.SITE_TBS_BAB_US) || SiteContextManager.getCurrentSiteId().equals(TBSConstants.SITE_TBS_BBB))){
					distance = caluclateDistanceInMiles(currentZipLongitude,currentZipLatitude,latitude,longitude);
					distanceUnit="Miles";
				} else if(currentZipLongitude != null && currentZipLatitude !=null && SiteContextManager.getCurrentSiteId().equals(TBSConstants.SITE_TBS_BAB_CA)){
					distance = caluclateDistanceInKm(currentZipLongitude,currentZipLatitude,latitude,longitude);
					distanceUnit="km";
				}
			}else if(currentStoreItem != null){
				currenStoreLongitude = (String) currentStoreItem.getPropertyValue("longitude");
				currenStoreLatitude = (String) currentStoreItem.getPropertyValue("latitude");
				if(currenStoreLongitude != null && currenStoreLatitude !=null && (SiteContextManager.getCurrentSiteId().equals(TBSConstants.SITE_TBS_BAB_US) || SiteContextManager.getCurrentSiteId().equals(TBSConstants.SITE_TBS_BBB))){
					distance = caluclateDistanceInMiles(currenStoreLongitude,currenStoreLatitude,latitude,longitude);
					distanceUnit="Miles";
				}else if(currenStoreLongitude != null && currenStoreLatitude !=null && SiteContextManager.getCurrentSiteId().equals(TBSConstants.SITE_TBS_BAB_CA)){
					distance = caluclateDistanceInKm(currenStoreLongitude,currenStoreLatitude,latitude,longitude);
					distanceUnit="km";
				}
			}
			
			storeDetails = new StoreDetails(storeId, storeName, storeDescription, address, city, state, conutry, postalCode,
					satStoreTimings, sunStoreTimings, weekdaysStoreTimings, otherTimings1, otherTimings2, storePhone, imageCode, distance,
					distanceUnit, recordNumber, longitude, latitude, specialtyShopsCd, storeType);
		}
		vlogDebug("TBSSearchStoreManager :: convertStoreItemToStore() :: END");
		return storeDetails;
		
	}
	
	private String caluclateDistanceInMiles(Object currenStoreLongitude, Object currenStoreLatitude, String pLatitude, String pLongitude) {
		  
		  double currStoreLongitude = Double.parseDouble(currenStoreLongitude.toString());
		  double currStoreLatitude = Double.parseDouble(currenStoreLatitude.toString());
		  double storeLatitude = Double.parseDouble(pLatitude);
		  double storeLongitude = Double.parseDouble(pLongitude);
		  double theta = currStoreLongitude - storeLongitude;
		  double dist = Math.sin(deg2rad(currStoreLatitude)) * Math.sin(deg2rad(storeLatitude)) + Math.cos(deg2rad(currStoreLatitude)) * Math.cos(deg2rad(storeLatitude)) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515;
		  String distance = Double.toString(dist);
		  
		  return distance;
	}
	
	private String caluclateDistanceInKm(Object currenStoreLongitude, Object currenStoreLatitude, String pLatitude, String pLongitude) {
		  
		  double currStoreLongitude = Double.parseDouble(currenStoreLongitude.toString());
		  double currStoreLatitude = Double.parseDouble(currenStoreLatitude.toString());
		  double storeLatitude = Double.parseDouble(pLatitude);
		  double storeLongitude = Double.parseDouble(pLongitude);
		  double theta = currStoreLongitude - storeLongitude;
		  double dist = Math.sin(deg2rad(currStoreLatitude)) * Math.sin(deg2rad(storeLatitude)) + Math.cos(deg2rad(currStoreLatitude)) * Math.cos(deg2rad(storeLatitude)) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 1.609344;
		  String distance = Double.toString(dist);
		  
		  return distance;
	}
	
	

	public double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}
	
	private double rad2deg(double rad) {
	  return (rad * 180.0 / Math.PI);
	}
	/**
	 * This method is used to get the list of store details based on the configured WarehouseStoreIds.
	 * @return
	 */
	public List<StoreDetails> includeWarehouseStoreDetails() {
		  
		  vlogDebug("TBSSearchStoreManager :: includeWarehouseStoreDetails() :: START");
		  
		  if(getWarehouseStoreIds().size() <= TBSConstants.ZERO){
		   vlogDebug("No WarehouseStoreIds are configured .");
		   return null;
		  }
		  List<StoreDetails> wareHouseStoreDetails = new ArrayList<StoreDetails>();
		  for (String warehouseStoreId : getWarehouseStoreIds()) {
		   
		    
		    StoreDetails storeDetails = new StoreDetails(warehouseStoreId, null, null, null, null, null, null, null,
		      null, null, null, null, null, null, null, null,null, null, null, null, null, null);
		    
		    wareHouseStoreDetails.add(storeDetails);
		  }
		  vlogDebug("TBSSearchStoreManager :: includeWarehouseStoreDetails() :: END");
		  return wareHouseStoreDetails;
		 }
		
	
	/**
	 * This method is used to get the list of bedbath fulfilment store details based on the configured BedBathBeyond Fulfillment StoreIds.
	 * @return
	 */
	public List<StoreDetails> includeBedBathUSStoreDetails() {
		vlogDebug("TBSSearchStoreManager :: includeBedBathUSStoreDetails() :: START");
		
		if(getBedBathBeyondStoreIds().size() <= TBSConstants.ZERO){
			vlogDebug("No BedBathBeyond Fulfillment StoreIds are configured .");
			return null;
		}
		List<StoreDetails> bedBathStoreDetails = new ArrayList<StoreDetails>();
		StoreDetails storeDetails = null;
		for (String bedBathStoreId : getBedBathBeyondStoreIds()) {
			try {
				RepositoryItem storeItem = getStoreRepository().getItem(bedBathStoreId, TBSConstants.STORE);
				if(storeItem != null){
					storeDetails = convertStoreItemToStore(storeItem);
					bedBathStoreDetails.add(storeDetails);
				}
			} catch (RepositoryException e) {
				logError("Store not found with id :: "+bedBathStoreId);
			}
		}
		vlogDebug("TBSSearchStoreManager :: includeBedBathUSStoreDetails() :: END");
		return bedBathStoreDetails;
	}
	
	/**
	 * This method is used to get the list of Canada fulfilment store details based on the configured Canada Fulfillment StoreIds.
	 * @return
	 */
	public List<StoreDetails> includeCanadaStoreDetails() {
		vlogDebug("TBSSearchStoreManager :: includeCanadaStoreDetails() :: START");
		
		if(getCanadaStoreIds().size() <= TBSConstants.ZERO){
			vlogDebug("No Canada Fulfillment StoreIds are configured .");
			return null;
		}
		List<StoreDetails> canadaStoreDetails = new ArrayList<StoreDetails>();
		StoreDetails storeDetails = null;
		for (String canadaStoreId : getCanadaStoreIds()) {
			try {
				RepositoryItem storeItem = getStoreRepository().getItem(canadaStoreId, TBSConstants.STORE);
				if(storeItem != null){
					storeDetails = convertStoreItemToStore(storeItem);
					canadaStoreDetails.add(storeDetails);
				}
			} catch (RepositoryException e) {
				logError("Store not found with id :: "+canadaStoreId);
			}
		}
		vlogDebug("TBSSearchStoreManager :: includeCanadaStoreDetails() :: END");
		return canadaStoreDetails;
	}
	
	/**
	 * This method is used to get the list of BuyBuyBaby fulfilment store details based on the configured BuyBuyBaby Fulfillment StoreIds.
	 * @return
	 */
	public List<StoreDetails> includeBuyBuyBabyStoreDetails() {
		vlogDebug("TBSSearchStoreManager :: includeBuyBuyBabyStoreDetails() :: START");
		
		if(getBuyBuyBabyStoreIds().size() <= TBSConstants.ZERO){
			vlogDebug("No BuyBuyBaby Fulfillment StoreIds are configured .");
			return null;
		}
		List<StoreDetails> buyBuyBabyStoreDetails = new ArrayList<StoreDetails>();
		StoreDetails storeDetails = null;
		for (String buyBuybabyStoreId : getBuyBuyBabyStoreIds()) {
			try {
				RepositoryItem storeItem = getStoreRepository().getItem(buyBuybabyStoreId, TBSConstants.STORE);
				if(storeItem != null){
					storeDetails = convertStoreItemToStore(storeItem);
					buyBuyBabyStoreDetails.add(storeDetails);
				}
			} catch (RepositoryException e) {
				logError("Store not found with id :: "+buyBuybabyStoreId);
			}
		}
		vlogDebug("TBSSearchStoreManager :: includeBuyBuyBabyStoreDetails() :: END");
		return buyBuyBabyStoreDetails;
	}
	
	
	/**
	 * Product availability count and bopus exclusion check for searched Store
	 * 
	 * @param pStoreDetails
	 * @param pSiteId
	 * @param pSkuId
	 * @param pReqQty
	 * @param operation
	 * @return
	 * @throws InventoryException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Map<String, Integer> checkProductAvailability(List<StoreDetails> pStoreDetails, String pSiteId, String pSkuId,
			String pRegistryId, boolean pChangeStore, long pReqQty, String operation, BBBStoreInventoryContainer pStoreInventoryContainer, DynamoHttpServletRequest pRequest)
					throws InventoryException, BBBSystemException, BBBBusinessException {
		
		vlogDebug("TBSSearchStoreManager :: checkProductAvailability() method :: START");
		Map<String, Integer> productAvailStatus = new HashMap<String, Integer>();
		List<String> bopusEligibleStates = null;
		List<String> bopusInEligibleStore = new ArrayList<String>();
		bopusEligibleStates = getCatalogTools().getBopusEligibleStates(pSiteId);
		
		String pStoreId  = getStoreType(pSiteId);
		bopusInEligibleStore = getCatalogTools().getBopusInEligibleStores(pStoreId, pSiteId );
		
		if (bopusInEligibleStore != null && bopusEligibleStates != null) {
			vlogDebug("Inside  SearchInStoreDroplet.checkProductAvailability---bopusInEligibleStore List " + bopusInEligibleStore);
			vlogDebug("Inside  SearchInStoreDroplet.checkProductAvailability---BopusEligibleState List " + bopusEligibleStates);
		}
		List<String> storeIds = new ArrayList<String>();
		for (StoreDetails storeDetails : pStoreDetails) {
			// Check for BOPUS excluscion at State Level
			if (bopusEligibleStates == null) {
				// Store pick not available for state
				productAvailStatus.put(storeDetails.getStoreId(), SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			} else if (!bopusEligibleStates.contains(storeDetails.getState())) {
				// Store pick not available for state
				productAvailStatus.put(storeDetails.getStoreId(), SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			}else if(bopusInEligibleStore !=null && bopusInEligibleStore.contains(storeDetails.getStoreId())){
				// Store  is not Bopus Eligible 
				productAvailStatus.put(storeDetails.getStoreId(), SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			}
			storeIds.add(storeDetails.getStoreId());
		}
		InventoryStatusVO statusVO = null;
		Map<String, Integer> storeInventoryMap;
		Map<String, Integer> storeInventoryStatusMap;

		statusVO = getTbsInventoryManager().getTbsBOPUSProductAvailability(pSiteId, pSkuId, storeIds, pReqQty,
				BBBInventoryManager.STORE_STORE, pStoreInventoryContainer, true, pRegistryId, pChangeStore);
		
		storeInventoryMap = statusVO.getInventoryMap();
		storeInventoryStatusMap = statusVO.getInventoryStatusMap();
		
		for (String storeId : storeInventoryStatusMap.keySet()) {
			if(storeInventoryStatusMap.get(storeId)==BBBInventoryManager.LIMITED_STOCK)	{
				storeInventoryStatusMap.put(storeId, BBBInventoryManager.AVAILABLE);
			}
			Integer boupusStatus = productAvailStatus.get(storeId);
			Integer inventoryStatus = storeInventoryStatusMap.get(storeId);
			if(boupusStatus!=null){
				//merge the No Bopus flag with Inventory
				String mergedData = boupusStatus.toString(); 
				if(inventoryStatus !=null){
					mergedData += inventoryStatus.toString();	
				}
				productAvailStatus.put(storeId, Integer.parseInt(mergedData));
			}else{
				productAvailStatus.put(storeId,storeInventoryStatusMap.get(storeId));
			}
		}
		pRequest.setParameter("productAvailStatus", productAvailStatus);
		vlogDebug("TBSSearchStoreManager :: checkProductAvailability() method : storeInventoryMap is: {0}" , storeInventoryMap);
		vlogDebug("TBSSearchStoreManager :: checkProductAvailability() method :: END");
		return storeInventoryMap;
	}
	
	/**
	 * Product availability count and bopus exclusion check for searched Store
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @throws InventoryException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Map<String, Integer> checkProductAvailabilityByORF(String pSiteId, String pSkuId)
					throws InventoryException, BBBSystemException, BBBBusinessException {
		
		
			return null;
	}
	
	
	
	public String getShipTime(String sku, long pQuantity, String siteId, String storeId) {
		Boolean enableEOMFlag = Boolean.FALSE;
		try {
			enableEOMFlag = getCatalogTools().getValueForConfigKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					BBBCoreConstants.EOM_ENABLE_FLAG, false);
		} catch (BBBSystemException e1) {
			vlogError(e1, "TBSSearchStoreManager.getShipTime: BBBSystemException while trying to get EnableEOM flag.");
		} catch (BBBBusinessException e1) {
			vlogError(e1, "TBSSearchStoreManager.getShipTime: BBBBusinessException while trying to get EnableEOM flag.");
		}
		if (enableEOMFlag) {
			return getShipTimeByEOM(sku, pQuantity, siteId, storeId).get(TBSConstants.TIME_FRAME);
		}
		String timeFrame = "0004";
		boolean nearbyStoreLink = false;
		boolean warehouseFlag = false;
		boolean regionaStoreslFlag = false;
		boolean otherStoresFlag = false;
		boolean departmentFlag = false;
		Integer currentStoreQuantity = 0;
		String currentSiteId = SiteContextManager.getCurrentSiteId();
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		if (BBBUtility.isEmpty(storeId)) {
			//getting the storeNumber from the session.
			storeId = (String) (request).getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		}
		
		SKUDetailVO skuVO = null;
		try{
			skuVO = getCatalogTools().getSKUDetails(siteId, sku);
		}catch (BBBSystemException e) {
			vlogError("BBBSystemException :: "+e);
		} catch (BBBBusinessException e) {
			vlogError("BBBBusinessException :: "+e);
		}
		
		if (!BBBUtility.isEmpty(storeId)) {
			StoreDetails objStoreDetails = null;
			List<StoreDetails> wareHouseStoreDetails = null;
			List<StoreDetails> bedBathStoreDetails = null;
			List<StoreDetails> canadaStoreDetails = null;
			List<StoreDetails> buyBuyBabyStoreDetails = null;
			List<StoreDetails> storeDetails = new ArrayList<StoreDetails>();
			List<StoreDetails> nearbyStores = new ArrayList<StoreDetails>();

			RepositoryItem[] storeList = null;
			
			if (!skuVO.isBopusAllowed()) {
				try {
					storeList = searchNearByStores(storeId);
					if (storeList != null && storeList.length > 0) {
						for (RepositoryItem repositoryItem : storeList) {
							// converting the storeItem into StoreDetails object
							objStoreDetails = convertStoreItemToStore(repositoryItem);
							storeDetails.add(objStoreDetails);
						}
						nearbyStores.addAll(storeDetails);
					}
				} catch (RepositoryException e) {
					vlogError("RepositoryException occurred while searching for near by stores");
					request.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
				} catch (SQLException e) {
					vlogError("SQLException occurred while searching for near by stores");
					request.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
				}
			}
		
			wareHouseStoreDetails = includeWarehouseStoreDetails();
			if (wareHouseStoreDetails != null && wareHouseStoreDetails.size() > TBSConstants.ZERO) {
				storeDetails.addAll(wareHouseStoreDetails);
			}
			bedBathStoreDetails = includeBedBathUSStoreDetails();
			canadaStoreDetails = includeCanadaStoreDetails();
			buyBuyBabyStoreDetails = includeBuyBuyBabyStoreDetails();

			if (!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase(TBSConstants.BED_BATH_US)) {
				storeDetails.addAll(bedBathStoreDetails);
			}
			if (!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase(TBSConstants.BED_BATH_CA)) {
				storeDetails.addAll(canadaStoreDetails);
			}
			if (!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase(TBSConstants.BUY_BUY_BABY)) {
				storeDetails.addAll(buyBuyBabyStoreDetails);
			}
			if (!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase("TBS_BedBathUS")) {
				storeDetails.addAll(bedBathStoreDetails);
			}
			if (!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase("TBS_BuyBuyBaby")) {
				storeDetails.addAll(buyBuyBabyStoreDetails);
			}
			if (!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase("TBS_BedBathCanada")) {
				storeDetails.addAll(canadaStoreDetails);
			}

				if (storeDetails.size() > TBSConstants.ZERO) {
					Map<String, Integer> storeInventoryMap = null;
					try {
						BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer) ServletUtil.getCurrentRequest()
								.resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");
						storeInventoryMap = checkProductAvailability(storeDetails, siteId, sku, null, false, pQuantity, BBBInventoryManager.STORE_STORE,
								storeInventoryContainer, request);
	
						logDebug("storeInventoryMap :: " + storeInventoryMap);
					} catch (InventoryException e) {
						logDebug("InventoryException in SearchInStoreDroplet while check Product Availability");
						request.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
					} catch (BBBSystemException e) {
						logError("BBBSystemException in SearchInStoreDroplet while check Product Availability",e);
						request.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
					} catch (BBBBusinessException e) {
						logError("BBBBusinessException in SearchInStoreDroplet while check Product Availability",e);
						request.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
					}
	
					if (storeInventoryMap != null && storeInventoryMap.size() > 0) {
						vlogDebug("storeInventoryMap :: " + storeInventoryMap);
						logDebug("current store :: " + storeId + " inventory data :: " + storeInventoryMap.get(storeId));
						
						if(storeInventoryMap.get(storeId) != null && storeInventoryMap.get(storeId) >= pQuantity){
							currentStoreQuantity = storeInventoryMap.get(storeId);
						}
						
						if (wareHouseStoreDetails != null && !wareHouseStoreDetails.isEmpty()) {
							for (StoreDetails warehouseStore : wareHouseStoreDetails) {
								if (storeInventoryMap.get(warehouseStore.getStoreId()) != null && storeInventoryMap.get(warehouseStore.getStoreId()) >= pQuantity) {
									logDebug("warehouseStore :: " + storeId + " inventory data :: " + storeInventoryMap.get(warehouseStore.getStoreId()));
									warehouseFlag = true;
									break;
								}
							}
						}
						if (warehouseFlag) {
							vlogDebug("Inventory available at warehouse stores");
							timeFrame = "0001";
							nearbyStoreLink = true;
						} else {
							// verifying regional fulfillments
							storeDetails.removeAll(nearbyStores);
							storeDetails.removeAll(wareHouseStoreDetails);
							if (storeDetails != null && !storeDetails.isEmpty()) {
								for (StoreDetails store : storeDetails) {
									if (storeInventoryMap.get(store.getStoreId()) != null && storeInventoryMap.get(store.getStoreId()) >= pQuantity) {
										logDebug("regional fulfillment Store :: " + storeId + " inventory data :: " + storeInventoryMap.get(store.getStoreId()));
										regionaStoreslFlag = true;
										break;
									}
								}
							}
						}
						if (!warehouseFlag && regionaStoreslFlag) {
							logDebug("Inventory available at regional stores");
							timeFrame = "0002";
							nearbyStoreLink = true;
						}
	
						// verifying the Other stores
						Map<String, Integer> nearbyStoreInventoryMap = new HashMap<String, Integer>();
						if (nearbyStores != null && !nearbyStores.isEmpty()) {
							for (StoreDetails storeDetail : nearbyStores) {
								if (storeInventoryMap.get(storeDetail.getStoreId()) != null && storeInventoryMap.get(storeDetail.getStoreId()) >= pQuantity) {
									logDebug("other Store :: " + storeId + " inventory data :: " + storeInventoryMap.get(storeDetail.getStoreId()));
									if (!warehouseFlag && !regionaStoreslFlag) {
										otherStoresFlag = true;
									}
								}
								//setting the nearby store inventory data
								nearbyStoreInventoryMap.put(storeDetail.getStoreId(), storeInventoryMap.get(storeDetail.getStoreId()));
							}
							request.setParameter(TBSConstants.NEAR_BY_STORES, nearbyStores);
							request.setParameter(TBSConstants.NEAR_BY_STORES_INVENTORY, nearbyStoreInventoryMap);
						}
						if (otherStoresFlag) {
							vlogDebug("Inventory available at other stores");
							timeFrame = "0003";
							nearbyStoreLink =true;
						} 
						
						// If Inventory not found in other Stores, check for the availability in Special Departments
						//SKUDetailVO skuVO = getCatalogTools().getSKUDetails(siteId, sku);
						
						if(!warehouseFlag && !regionaStoreslFlag && !otherStoresFlag){
							Map<String, Integer> specialDeptInventoryMap = new HashMap<String, Integer>();
							ThresholdVO skuThresholdVO = null;
							List<String> deptIds = null;
							List<String> specialDeptIds = null;
							try {
								deptIds = ((TBSCatalogToolsImpl)getCatalogTools()).getSkuDepartMents(sku);
								specialDeptIds = getCatalogTools().getAllValuesForKey(
										BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.SPECIAL_DEPARTMENTS);
							} catch (BBBSystemException e) {
								vlogError("BBBSystemException :: " + e);
								request.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
							} catch (BBBBusinessException e) {
								vlogError("BBBBusinessException :: "+ e);
								request.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
							}
							
							if(deptIds != null && !deptIds.isEmpty() && specialDeptIds != null && !specialDeptIds.isEmpty()){
								for (String deptId : deptIds) {
									if(specialDeptIds.contains(deptId)){
										try {
											skuThresholdVO = ((TBSCatalogToolsImpl)getCatalogTools()).getSkuThresholdForSplDept(deptId, pQuantity);
										} catch (RepositoryException e) {
											logError("Exception in cheking Threshhold of sku", e);
											departmentFlag = true;
										} 
										if(skuThresholdVO != null && skuThresholdVO.getThresholdAvailable() > 0){
											logDebug("Inventory available in the department"+specialDeptInventoryMap.get(deptId));
											departmentFlag=true;
											break;
										}
									}
								}
							}
							
							if(departmentFlag){
								timeFrame = "0005";
							}
						}
					} 
				} 
			}
			

				
				
			
			// this call is made when there is no inventory found in warehouse and regional store, 
			//Inventory check by network
			Map<String, Integer> networkInventoryMap = null;
			
			if(!warehouseFlag && !regionaStoreslFlag && !departmentFlag){
				String mNetworkInvFlag=null;
		        try {
					mNetworkInvFlag = getNetworkInventoryFlag(currentSiteId);
		        }  catch (BBBSystemException bbbSystemException) {
					logError("Exception occured while query networkInventory Flag"+bbbSystemException.getMessage());
				} catch (BBBBusinessException bbbBusinessException) {
					logError("Exception occured while query networkInventory Flag"+bbbBusinessException.getMessage());
				}
		        if (BBBCoreConstants.TRUE.equalsIgnoreCase(mNetworkInvFlag)) {
		        	try {
		        		networkInventoryMap = ((TBSStoreTools)getStoreTools()).fetchORFInventory(currentSiteId, sku);
						if(networkInventoryMap != null && networkInventoryMap.get(sku)!=null 
								&& (networkInventoryMap.get(sku) - pQuantity) >= getOrfThreshold(currentSiteId)){
							vlogDebug("Inventory available in the network");
							request.setParameter(TBSConstants.EMPTY_STORE_INVENTORY, false);
							timeFrame = "0003";
						}
					} catch (BBBBusinessException e) {
						vlogError("Exception in querying Inventory by network", e.getMessage());
					}
		        }
			}
		request.setParameter(TBSConstants.TIME_FRAME, timeFrame);
		request.setParameter(TBSConstants.NEAR_BY_STORE_LINK, nearbyStoreLink);
		request.setParameter(TBSConstants.CURRENT_STORE_QTY, currentStoreQuantity);
		return timeFrame;
	}

	/**
	 * This method checks local ORF inventory.
	 * 
	 * @param sku
	 * @param pQuantity
	 * @param timeFrame
	 * @param warehouseFlag
	 * @param regionaStoreslFlag
	 * @param otherStoresFlag
	 * @param nonTbsCurrentSiteId
	 * @param request
	 * @return
	 */
	private String getLocalORFInventory(String sku, long pQuantity, String timeFrame, boolean warehouseFlag,
			boolean regionaStoreslFlag, boolean otherStoresFlag, String nonTbsCurrentSiteId,
			DynamoHttpServletRequest request) {
		Map<String, Integer> networkInventoryMap;
		try {
			String mNetworkInvFlag = getNetworkInventoryFlag(SiteContextManager.getCurrentSiteId());
			if (BBBCoreConstants.TRUE.equalsIgnoreCase(mNetworkInvFlag)) {
				networkInventoryMap = ((TBSStoreTools) getStoreTools()).fetchORFInventory(nonTbsCurrentSiteId, sku);
				if (networkInventoryMap != null && networkInventoryMap.get(sku) != null
						&& (networkInventoryMap.get(sku) - pQuantity) >= getOrfThreshold(SiteContextManager.getCurrentSiteId())) {
					otherStoresFlag = true;
					vlogDebug("Inventory available in the network");
					request.setParameter(TBSConstants.NEAR_BY_STORE_LINK, false);
					request.setParameter(TBSConstants.EMPTY_STORE_INVENTORY, false);
					timeFrame = TBSConstants.OTHER_STORE_INVENTORY_AVAILABLE_CODE;
				} else if (!warehouseFlag && !regionaStoreslFlag && !otherStoresFlag) {
					vlogDebug("Inventory is not available at any store");
					request.setParameter(TBSConstants.NEAR_BY_STORE_LINK, false);
					timeFrame = TBSConstants.INVENTORY_UNAVAILABLE_CODE;
				}
			}
		} catch (BBBSystemException bbbSystemException) {
			vlogError(bbbSystemException, "Exception occured while querying networkInventory Flag");
		} catch (BBBBusinessException bbbBusinessException) {
			vlogError(bbbBusinessException, "Exception occured while querying networkInventory Flag or fetching ORF inventory.");
		}
		return timeFrame;
	}

	/**
	 * This method is used to get ship time and nearby inventory and instore
	 * inventory from EOM service.
	 * 
	 * @param skuId
	 * @param pQuantity
	 * @param siteId
	 * @param storeId
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public Map<String, String> getShipTimeByEOM(String skuId, long pQuantity, String siteId, String storeId) {
		vlogDebug(
				"TBSSeachStoreManager.getShipTimeByEOM: Starts, with skuId: {0} pQuantity: {1} siteId: {2} storeId: {3}",
				skuId, pQuantity, siteId, storeId);
		String timeFrame = BBBCoreConstants.BLANK;
		boolean regionaStoreslFlag = false;
		boolean otherStoresFlag = false;
		boolean departmentFlag = false;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		if (BBBUtility.isEmpty(storeId)) {
			// getting the storeNumber from the session.
			storeId = (String) (pRequest).getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		}
		SKUDetailVO skuVO = null;
		Map<String, String> networkInventoryOutput = new ConcurrentHashMap<>();
		try {
			skuVO = getCatalogTools().getSKUDetails(siteId, skuId);
		} catch (BBBSystemException e) {
			vlogError(e, "TBSSeachStoreManager.getShipTimeByEOM: BBBSystemException while getting skuVO from SKU id");
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
		} catch (BBBBusinessException e) {
			vlogError(e, "TBSSeachStoreManager.getShipTimeByEOM: BBBBusinessException while getting skuVO from SKU id");
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
		}
		Map<String, Integer> inventoryByNetwork = new LinkedHashMap<>();
		boolean warehouseFlag = false;
		if (!BBBUtility.isEmpty(storeId)) {
			List<StoreDetails> nearbyStores = populateNearbyStores(storeId, pRequest, skuVO, networkInventoryOutput);
			Map<String, Integer> nearbyStoreInventoryMap = null;
			if (!nearbyStores.isEmpty()) {
				nearbyStoreInventoryMap = populateNearbyStoreInventory(skuId, pQuantity, siteId, storeId, pRequest,
						networkInventoryOutput, nearbyStores);
			} else {
				vlogDebug("TBSSearchStoreManager.getShipTimeByEOM: There is no inventory in current stores.");
				pRequest.setParameter(TBSConstants.CURRENT_STORE_QTY, "0");
			}
			try {
				List<String> skuList = new ArrayList<>();
				skuList.add(skuId);
				inventoryByNetwork = getBopusService().getInventoryForBopusItemByViewType(skuList);
			} catch (BBBBusinessException e) {
				vlogError(e,
						"TBSSearchStoreManager.getShipTimeByEOM: BBBBusinessException while trying to get inventory from network call.");
				networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
			} catch (BBBSystemException e) {
				vlogError(e,
						"TBSSearchStoreManager.getShipTimeByEOM: BBBSystemException while trying to get inventory from network call.");
				networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
			} catch (IOException e) {
				vlogError(e,
						"TBSSearchStoreManager.getShipTimeByEOM: IOException while trying to get inventory from network call.");
				networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
			}
			if (!BBBUtility.isMapNullOrEmpty(inventoryByNetwork)) {
				if (getInventoryKeyBySkuAndRefNum(skuId, inventoryByNetwork,
						TBSConstants.DC_GROUP_QUANTITY) >= pQuantity) {
					warehouseFlag = true;
				}
				if (warehouseFlag) {
					vlogDebug("TBSSeachStoreManager.getShipTimeByEOM: Inventory available at warehouse stores");
					pRequest.setParameter(TBSConstants.NEAR_BY_STORE_LINK, true);
					timeFrame = TBSConstants.WAREHOUSE_INVENTORY_AVAILABLE_CODE;
				} else if (!StringUtils.isBlank(siteId)) {
					regionaStoreslFlag = checkRegionalStoreForInventory(skuId, pQuantity, siteId, inventoryByNetwork);
				}
				if (!warehouseFlag && regionaStoreslFlag) {
					vlogDebug("TBSSeachStoreManager.getShipTimeByEOM: Inventory available at regional stores");
					pRequest.setParameter(TBSConstants.NEAR_BY_STORE_LINK, true);
					timeFrame = TBSConstants.REGIONAL_STORES_INVENTORY_AVAILABLE_CODE;
				}
				/*
				 * verifying the Other stores if not available at whereHouse and
				 * regional stores
				 */
				if (!warehouseFlag && !regionaStoreslFlag && !BBBUtility.isMapNullOrEmpty(nearbyStoreInventoryMap)) {
					otherStoresFlag = checkNearbyStoresForInventory(pQuantity, nearbyStoreInventoryMap);
				}
				if (otherStoresFlag) {
					vlogDebug("TBSSeachStoreManager.getShipTimeByEOM: Inventory available at other stores");
					timeFrame = TBSConstants.OTHER_STORE_INVENTORY_AVAILABLE_CODE;
					pRequest.setParameter(TBSConstants.NEAR_BY_STORE_LINK, true);
				}
			}

			/*
			 * If Inventory not found in other Stores, check for the
			 * availability in Special Departments
			 */
			if (!warehouseFlag && !regionaStoreslFlag && !otherStoresFlag) {
				timeFrame = checkDepartmentForInventory(skuId, pQuantity, timeFrame, departmentFlag, pRequest,
						networkInventoryOutput);
			}

			/*
			 * This call is made when there is no inventory found in warehouse
			 * and regional store, Inventory check by network
			 */
			if (!warehouseFlag && !regionaStoreslFlag && !otherStoresFlag) {
				timeFrame = checkORFInventory(skuId, pQuantity, siteId, timeFrame, regionaStoreslFlag, otherStoresFlag,
						pRequest, inventoryByNetwork, warehouseFlag);
			}
			pRequest.setParameter(TBSConstants.TIME_FRAME, timeFrame);
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.OUTPUT);
		} else {
			vlogDebug("TBSSeachStoreManager.getShipTimeByEOM: Inventory Data not found.");
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.EMPTY);
		}
		networkInventoryOutput.put(TBSConstants.TIME_FRAME, timeFrame);
		vlogDebug("TBSSeachStoreManager.getShipTimeByEOM: Ends, with timeFrame: {0} ", timeFrame);
		return networkInventoryOutput;
	}

	/**
	 * This method checks ORF Inventory.
	 * 
	 * @param skuId
	 * @param pQuantity
	 * @param siteId
	 * @param timeFrame
	 * @param regionaStoreslFlag
	 * @param otherStoresFlag
	 * @param pRequest
	 * @param inventoryByNetwork
	 * @param warehouseFlag
	 * @return
	 */
	private String checkORFInventory(String skuId, long pQuantity, String siteId, String timeFrame,
			boolean regionaStoreslFlag, boolean otherStoresFlag, DynamoHttpServletRequest pRequest,
			Map<String, Integer> inventoryByNetwork, boolean warehouseFlag) {
		if (!StringUtils.isBlank(siteId) && !BBBUtility.isMapNullOrEmpty(inventoryByNetwork)) {
			otherStoresFlag = checkORFInventoryFromEOM(skuId, pQuantity, siteId, inventoryByNetwork);
		}
		if (otherStoresFlag) {
			vlogDebug("TBSSeachStoreManager.getShipTimeByEOM: Inventory available in the network");
			pRequest.setParameter(TBSConstants.NEAR_BY_STORE_LINK, false);
			pRequest.setParameter(TBSConstants.EMPTY_STORE_INVENTORY, false);
			timeFrame = TBSConstants.OTHER_STORE_INVENTORY_AVAILABLE_CODE;
		} else if (!warehouseFlag && !regionaStoreslFlag && !otherStoresFlag) {
			vlogDebug("TBSSeachStoreManager.getShipTimeByEOM: Checking inventory from local repository.");
			timeFrame = getLocalORFInventory(skuId, pQuantity, timeFrame, warehouseFlag, regionaStoreslFlag,
					otherStoresFlag, siteId, pRequest);
		}
		return timeFrame;
	}

	/**
	 * This method populates nearby store for a give store.
	 * 
	 * @param storeId
	 * @param pRequest
	 * @param skuVO
	 * @param networkInventoryOutput
	 * @return
	 */
	private List<StoreDetails> populateNearbyStores(String storeId, DynamoHttpServletRequest pRequest,
			SKUDetailVO skuVO, Map<String, String> networkInventoryOutput) {
		vlogDebug("TBSSeachStoreManager.populateNearbyStores: Starts");
		List<StoreDetails> nearbyStores = new ArrayList<StoreDetails>();
		if (skuVO.isBopusAllowed()) {
			vlogDebug("TBSSeachStoreManager.populateNearbyStores: Ends");
			return nearbyStores;
		}
		try {
			RepositoryItem[] storeList = searchNearByStores(storeId);
			if (storeList != null && storeList.length > 0) {
				for (RepositoryItem repositoryItem : storeList) {
					// converting the storeItem into StoreDetails object
					StoreDetails objStoreDetails = convertStoreItemToStore(repositoryItem);
					nearbyStores.add(objStoreDetails);
				}
			}
		} catch (RepositoryException e) {
			vlogError(e,
					"TBSSeachStoreManager.populateNearbyStores: RepositoryException occurred while searching for near by stores");
			pRequest.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
		} catch (SQLException e) {
			vlogError(e,
					"TBSSeachStoreManager.populateNearbyStores: SQLException occurred while searching for near by stores");
			pRequest.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
		}
	
		vlogDebug("TBSSeachStoreManager.populateNearbyStores: Ends");
		return nearbyStores;
	}

	/**
	 * This method is used to populate nearby store inventory from EOM.
	 * 
	 * @param skuId
	 * @param pQuantity
	 * @param siteId
	 * @param storeId
	 * @param pRequest
	 * @param networkInventoryOutput
	 * @param nearbyStores
	 * @return
	 */
	private Map<String, Integer> populateNearbyStoreInventory(String skuId, long pQuantity, String siteId,
			String storeId, DynamoHttpServletRequest pRequest, Map<String, String> networkInventoryOutput,
			List<StoreDetails> nearbyStores) {
		vlogDebug("TBSSeachStoreManager.populateNearbyStoreInventory: Starts");
		Map<String, Integer> nearbyStoreInventoryMap = new LinkedHashMap<>();
		try {
			BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer) ServletUtil
					.getCurrentRequest().resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");
			nearbyStoreInventoryMap = checkProductAvailability(nearbyStores, siteId, skuId, null, false, pQuantity,
					BBBInventoryManager.STORE_STORE, storeInventoryContainer, pRequest);

			vlogDebug("TBSSeachStoreManager.populateNearbyStoreInventory: storeInventoryMap :: {0}",
					nearbyStoreInventoryMap);
		} catch (InventoryException e) {
			vlogError(e, "InventoryException in SearchInStoreDroplet while check Product Availability");
			pRequest.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
		} catch (BBBSystemException e) {
			vlogError(e,
					"TBSSeachStoreManager.populateNearbyStoreInventory: BBBSystemException in SearchInStoreDroplet while check Product Availability.");
			pRequest.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
		} catch (BBBBusinessException e) {
			vlogError(e,
					"TBSSeachStoreManager.populateNearbyStoreInventory: BBBBusinessException in SearchInStoreDroplet while check Product Availability.");
			pRequest.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
		}

		if (!BBBUtility.isMapNullOrEmpty(nearbyStoreInventoryMap)) {
			vlogDebug(
					"TBSSeachStoreManager.populateNearbyStoreInventory: nearbyStoreInventoryMap: {0},  current store: {1}, Inventory in this store: {2}",
					nearbyStoreInventoryMap, storeId, nearbyStoreInventoryMap.get(storeId));
			if (nearbyStoreInventoryMap.get(storeId) != null && nearbyStoreInventoryMap.get(storeId) > pQuantity) {
				pRequest.setParameter(TBSConstants.CURRENT_STORE_QTY, nearbyStoreInventoryMap.get(storeId));
			} else {
				pRequest.setParameter(TBSConstants.CURRENT_STORE_QTY, "0");
			}
			pRequest.setParameter(TBSConstants.NEAR_BY_STORES, nearbyStores);
			pRequest.setParameter(TBSConstants.NEAR_BY_STORES_INVENTORY, nearbyStoreInventoryMap);
		} else {
			vlogDebug("TBSSeachStoreManager.populateNearbyStoreInventory: Inventory Data not found......");
			pRequest.setParameter(TBSConstants.EMPTY_STORE_INVENTORY, true);
			pRequest.setParameter(TBSConstants.CURRENT_STORE_QTY, "0");
		}
		vlogDebug("TBSSeachStoreManager.populateNearbyStoreInventory: Ends");
		return nearbyStoreInventoryMap;
	}

	/**
	 * This method checks ORF inventory from EOM.
	 * 
	 * @param skuId
	 * @param pQuantity
	 * @param siteId
	 * @param otherStoresFlag
	 * @param inventoryByNetwork
	 * @return
	 */
	private boolean checkORFInventoryFromEOM(String skuId, long pQuantity, String siteId,
			Map<String, Integer> inventoryByNetwork) {
		vlogDebug("TBSSeachStoreManager.checkORFInventoryFromEOM: Starts");
		boolean otherStoresFlag = false;
		if (!BBBUtility.isMapNullOrEmpty(getOrfInventoryAttrMap()) && getInventoryKeyBySkuAndRefNum(skuId, inventoryByNetwork,
				getOrfInventoryAttrMap().get(siteId)) >= pQuantity) {
			otherStoresFlag = true;
		}
		vlogDebug("TBSSeachStoreManager.checkORFInventoryFromEOM: Ends");
		return otherStoresFlag;
	}

	/**
	 * This method checks Department inventory from EOM.
	 * 
	 * @param skuId
	 * @param pQuantity
	 * @param timeFrame
	 * @param departmentFlag
	 * @param pRequest
	 * @param networkInventoryOutput
	 * @return
	 */
	private String checkDepartmentForInventory(String skuId, long pQuantity, String timeFrame, boolean departmentFlag,
			DynamoHttpServletRequest pRequest, Map<String, String> networkInventoryOutput) {
		vlogDebug("TBSSeachStoreManager.checkDepartmentForInventory: Starts");
		Map<String, Integer> specialDeptInventoryMap = new HashMap<>();
		ThresholdVO skuThresholdVO = null;
		List<String> deptIds = null;
		List<String> specialDeptIds = null;
		try {
			deptIds = ((TBSCatalogToolsImpl) getCatalogTools()).getSkuDepartMents(skuId);
			specialDeptIds = getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,
					BBBCatalogConstants.SPECIAL_DEPARTMENTS);
		} catch (BBBSystemException e) {
			vlogError("TBSSeachStoreManager.checkDepartmentForInventory: BBBSystemException :: {0}", e);
			pRequest.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
		} catch (BBBBusinessException e) {
			vlogError("TBSSeachStoreManager.checkDepartmentForInventory: BBBBusinessException :: {0}", e);
			pRequest.setParameter(TBSConstants.INVENTORY_ERROR_MESSAGE, e.getMessage());
			networkInventoryOutput.put(TBSConstants.SERVICE_PARAMETER, TBSConstants.ERROR);
		}

		if (deptIds != null && !deptIds.isEmpty() && specialDeptIds != null && !specialDeptIds.isEmpty()) {
			for (String deptId : deptIds) {
				if (specialDeptIds.contains(deptId)) {
					try {
						skuThresholdVO = ((TBSCatalogToolsImpl) getCatalogTools()).getSkuThresholdForSplDept(deptId,
								pQuantity);
					} catch (RepositoryException e) {
						logError(
								"TBSSeachStoreManager.checkDepartmentForInventory: Exception in cheking Threshhold of sku",
								e);
						timeFrame = TBSConstants.DEPARTMENT_INVENTORY_AVAILABLE_CODE;
					}
					if (skuThresholdVO != null && skuThresholdVO.getThresholdAvailable() >= pQuantity) {
						departmentFlag = true;
						vlogDebug(
								"TBSSeachStoreManager.checkDepartmentForInventory: Inventory available in the department: {0}",
								specialDeptInventoryMap.get(deptId));
						timeFrame = TBSConstants.DEPARTMENT_INVENTORY_AVAILABLE_CODE;
						pRequest.setParameter(TBSConstants.NEAR_BY_STORE_LINK, true);
						break;
					}
				}
			}
		}
		if (!departmentFlag) {
			vlogDebug(
					"TBSSeachStoreManager.checkDepartmentForInventory: Inventory is also not available in the department");
			timeFrame = TBSConstants.INVENTORY_UNAVAILABLE_CODE;
			pRequest.setParameter(TBSConstants.NEAR_BY_STORE_LINK, false);
		}
		vlogDebug("TBSSeachStoreManager.checkDepartmentForInventory: Ends");
		return timeFrame;
	}

	/**
	 * This method checks Nearby Store inventory from EOM.
	 * 
	 * @param pQuantity
	 * @param otherStoresFlag
	 * @param nearbyStoreInventoryMap
	 * @return
	 */
	private boolean checkNearbyStoresForInventory(long pQuantity,
			Map<String, Integer> nearbyStoreInventoryMap) {
		vlogDebug("TBSSeachStoreManager.checkNearbyStoresForInventory: Starts");
		boolean otherStoresFlag = false;
		for (Integer inventory : nearbyStoreInventoryMap.values()) {
			if (inventory >= pQuantity) {
				otherStoresFlag = true;
				break;
			}
		}
		vlogDebug("TBSSeachStoreManager.checkNearbyStoresForInventory: Ends");
		return otherStoresFlag;
	}

	/**
	 * This method checks RF inventory from EOM.
	 * 
	 * @param skuId
	 * @param pQuantity
	 * @param siteId
	 * @param regionaStoreslFlag
	 * @param inventoryByNetwork
	 * @return
	 */
	private boolean checkRegionalStoreForInventory(String skuId, long pQuantity, String siteId,
			Map<String, Integer> inventoryByNetwork) {
		vlogDebug("TBSSeachStoreManager.checkRegionalStoreForInventory: Starts");
		boolean regionaStoreslFlag = false;
		if (!BBBUtility.isMapNullOrEmpty(getRfInventoryAttrMap()) && getInventoryKeyBySkuAndRefNum(skuId, inventoryByNetwork,
				getRfInventoryAttrMap().get(siteId)) >= pQuantity) {
			regionaStoreslFlag = true;
		}
		vlogDebug("TBSSeachStoreManager.checkRegionalStoreForInventory: Ends");
		return regionaStoreslFlag;
	}

	/**
	 * This method creates inventory unique key for a SKU by given reference
	 * number.
	 * 
	 * @param skuId
	 * @param inventoryByNetwork
	 * @return
	 */
	private Integer getInventoryKeyBySkuAndRefNum(String skuId, Map<String, Integer> inventoryByNetwork,
			String refNum) {
		return inventoryByNetwork.get(skuId + BBBCoreConstants.PIPE_SYMBOL + refNum);
	}
	
	/**
	 * 
	 * @param pSystemIpRange
	 * @return
	 */
	public RepositoryItem getStoreItem(String pSystemIpRange) {
		vlogDebug("SearchStoreManager :: getStoreItem() :: START " );
		RepositoryItem storeItem = null;
		RepositoryItem[] store = null;
		if(!StringUtils.isBlank(pSystemIpRange)){
			try {
				RepositoryItemDescriptor storeIPRangeDescriptor = getStoreRepository().getItemDescriptor("ipRangeData");
				RepositoryView storeIpRangeView = storeIPRangeDescriptor.getRepositoryView();
				QueryBuilder storeQueryBuilder = storeIpRangeView.getQueryBuilder();
				
				QueryExpression ipRangeProperty = storeQueryBuilder.createPropertyQueryExpression("ipRange");
				QueryExpression ipRangeValue = storeQueryBuilder.createConstantQueryExpression(pSystemIpRange);
				Query orderQuery = storeQueryBuilder.createComparisonQuery(ipRangeProperty, ipRangeValue, QueryBuilder.EQUALS);
				
				store = storeIpRangeView.executeQuery(orderQuery);
			} catch (RepositoryException e) {
				logError("RepositoryException occurred while getting storeItem using IPRange ::",e);
			}
			if(store != null && store.length > TBSConstants.ZERO){
				storeItem = store[0];
				vlogDebug("returned storeitems "+store.length );
			}
		}
		vlogDebug("SearchStoreManager :: getStoreItem() :: END " );
		return storeItem;
	}
	
	/**
	 * 
	 * @param pStoreId
	 * @return
	 */
	public boolean isValidStore(String pStoreId){
		
		vlogDebug("SearchStoreManager :: isValidStore() :: START " );
		RepositoryItem lStoreItem = null;
		
		if(!StringUtils.isBlank(pStoreId)){
			// validate storeNumberFromParam against the available stores in store repository
			vlogDebug("Store Number to be validated", pStoreId);
			try {
				lStoreItem = getStoreRepository().getItem(pStoreId, "store");
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				vlogError("RepositoryException occurred while getting storeItem using storeId ::",e.getMessage());
			}
			if(lStoreItem!=null){
				return true;
			}
		}
		vlogDebug("SearchStoreManager :: isValidStore() :: END " );
		return false;
	}
	
	/**
	 * Gets the siteid meant for store 
	 * @param pSiteId
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getDefaultStoreType(String pSiteId) throws BBBSystemException, BBBBusinessException {
		if (pSiteId != null) {
			List<String> siteIds = getCatalogTools().getAllValuesForKey(TBSConstants.DEFAULTSTORETYPE, pSiteId);
			if (siteIds == null || siteIds.get(BBBCoreConstants.ZERO).isEmpty() ){
				logDebug(LogMessageFormatter.formatMessage(null, "No Value found for Key "+pSiteId+" in Config Type "+TBSConstants.DEFAULTSTORETYPE+" passed to getAllValuesForKey() method"));
				throw new BBBSystemException (BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE,BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE);
			}
			return siteIds.get(BBBCoreConstants.ZERO);
		} 
		logDebug("Site ID is null : TBSSearchStoreManager.getDefaultStoreType()");
		throw new BBBSystemException (BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE,BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE);
	}
	
	/**
	 * Gets the siteid meant for store 
	 * @param pSiteId
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getNetworkInventoryFlag(String pSiteId) throws BBBSystemException, BBBBusinessException {
		String networkInvFlag = "false";
		if (pSiteId != null) {
			List<String> siteIds = getCatalogTools().getAllValuesForKey(TBSConstants.NETWORKINVENTORYFLAG, pSiteId);
			if (siteIds != null && !siteIds.get(BBBCoreConstants.ZERO).isEmpty() ){
				return siteIds.get(BBBCoreConstants.ZERO);
			} else {
				logError(LogMessageFormatter.formatMessage(null, "No Value found for Key "+pSiteId+" in Config Type "+TBSConstants.NETWORKINVENTORYFLAG+" passed to getAllValuesForKey() method"));
				throw new BBBSystemException (BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE,BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE);
			}
		}
		return networkInvFlag;
	}
	
	public int getOrfThreshold(String siteId) {
		int orfThreshold = 0;
		if (siteId != null) {
			List<String> orfThresholdList = null;
			try {
				orfThresholdList = getCatalogTools().getAllValuesForKey(BBBCheckoutConstants.BOPUSCONFIG, TBSConstants.ORF_THRESHOLD);
			} catch (Exception e) {
				logError("No Value found for Key " + siteId + " in Config Type " + BBBCheckoutConstants.BOPUSCONFIG + " passed to getAllValuesForKey() method");
			}
			if (!BBBUtility.isListEmpty(orfThresholdList)){
				orfThreshold =  Integer.parseInt(orfThresholdList.get(BBBCoreConstants.ZERO));
			}
		}
		return orfThreshold;
	}

	/**
	 * @return the rfInventoryAttrMap
	 */
	public Map<String, String> getRfInventoryAttrMap() {
		return rfInventoryAttrMap;
	}
	
	/**
	 * @param rfInventoryAttrMap the rfInventoryAttrMap to set
	 */
	public void setRfInventoryAttrMap(Map<String, String> rfInventoryAttrMap) {
		this.rfInventoryAttrMap = rfInventoryAttrMap;
	}
	
	/**
	 * @return the orfInventoryAttrMap
	 */
	public Map<String, String> getOrfInventoryAttrMap() {
		return orfInventoryAttrMap;
	}
	
	/**
	 * @param orfInventoryAttrMap the orfInventoryAttrMap to set
	 */
	public void setOrfInventoryAttrMap(Map<String, String> orfInventoryAttrMap) {
		this.orfInventoryAttrMap = orfInventoryAttrMap;
	}
	
	
}
