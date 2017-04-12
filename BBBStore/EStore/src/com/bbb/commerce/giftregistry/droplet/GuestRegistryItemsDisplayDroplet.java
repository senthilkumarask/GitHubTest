package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.GuestRegistryItemsFirstCallVO;
import com.bbb.commerce.giftregistry.vo.GuestRegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.OmnitureGuestRegistryVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**This droplet is used in case of Guest View of Registry Pages to fetch items in 3 ajax calls.
 * 
 * The flags in which this view is used - 
 *  1.) When web service flag for registry is off
 *  2.) When checklist flag is enabled for this registry type
 *  3.) When GiftGiver_Items_Checklist_Flag flag is true
 *  4.) In case of category filter
 *  
 *  If the filter is based on price the RegistryItemsDisplayDroplet flow is enabled.
 *  
 *  The complete display of items is done on the following flow
 *  1.)On page load all the categories based on the registry type is fetched.
 *  2.) In the first ajax call the items are fetched and based on the inventory sorted in in stock and out of stock category. The categories that are
 *   not in stock is returned as list for removal
 *   The first call returns first non zero items C1, registry Item Count (for copy registry and sort and filter options),start browsing button flag and not in stock list.
 *   The map of in stock and out of stock categories are fetched and kept in sessiom
 *  3.) In second ajax call all in stock categories are returned and a list of not in stock category list.
 *  4.) In third ajax call not in stock category buckets are returned and omnniture data is returned.

 *
 */
public class GuestRegistryItemsDisplayDroplet extends BBBPresentationDroplet {
	private CheckListManager checkListManager;
	private BBBCatalogTools catalogTools;
	private GiftRegistryManager giftRegistryManager;
	private static final String BUYOFF_SHOW_START_BROWSING = "showStartBrowsing";
	private static final String REG_ITEM_COUNT = "regItemCount";
	private static final String EMPTY_OUT_OF_STOCK_LIST_FLAG = "emptyOutOfStockListFlag";
	private static final String OMNITURE_LIST = "omnitureList";
	private BBBSessionBean sessionBean;
	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		BBBPerformanceMonitor.start("GuestRegistryItemsDisplayDroplet", "GuestRegistryItemsDisplayDroplet");
		String eventTypeCode = req
				.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE);

		logDebug("Entering GuestRegistryItemsDisplayDroplet with Registry Type : "+eventTypeCode);
		// Based on the ajax call order: parameters : isFirstAjaxCall,isSecondAjaxCall and isThirdAjaxCall are set.
		boolean isFirstAjaxCall = Boolean.parseBoolean(req.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL));
		boolean isSecondAjaxCall = Boolean.parseBoolean(req.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL));
		boolean isThirdAjaxCall = Boolean.parseBoolean(req.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL));
		
		// This list would have not in stock category id for removal.
		List<String> notInStockCategoryList=new ArrayList<String>();
		if(isFirstAjaxCall){
			logDebug("First call GuestRegistryItemsDisplayDroplet start : "+System.currentTimeMillis());
			try {
				GuestRegistryItemsListVO firstCategoryList=fetchFirstCategoryItems(req,res,notInStockCategoryList);
				req.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,
						firstCategoryList);
				req.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST,
						notInStockCategoryList);
			} catch (BBBSystemException | BBBBusinessException e) {
				logError(e.getMessage(),e);
			}
			logDebug("First call GuestRegistryItemsDisplayDroplet End : "+System.currentTimeMillis());	
		}else if(isSecondAjaxCall){
			logDebug("Second call GuestRegistryItemsDisplayDroplet start : "+System.currentTimeMillis());
			Map<String, GuestRegistryItemsListVO> giftGiverInStockRegItemsMap = fetchAllCategoryItems(req,res,notInStockCategoryList);
			req.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,
					giftGiverInStockRegItemsMap);
			req.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST,
					notInStockCategoryList);
			logDebug("Second call GuestRegistryItemsDisplayDroplet End : "+System.currentTimeMillis());	
		}else if(isThirdAjaxCall){
			logDebug("Third call GuestRegistryItemsDisplayDroplet start : "+System.currentTimeMillis());
			Map<String, GuestRegistryItemsListVO> giftGiverOutOfStockRegItemsMap = fetchBelowLineCategoryItems(req,res);
			req.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,
					giftGiverOutOfStockRegItemsMap);
			if(giftGiverOutOfStockRegItemsMap ==null || giftGiverOutOfStockRegItemsMap.isEmpty()){
				req.setParameter(EMPTY_OUT_OF_STOCK_LIST_FLAG,true);
			}
			StringBuffer omniProductList =new StringBuffer();
			fetchOmnitureDetailsForCopyRegistry(req,omniProductList);
			req.setParameter(OMNITURE_LIST,omniProductList);
			logDebug("Third call GuestRegistryItemsDisplayDroplet end : "+System.currentTimeMillis());
		}else{
		// On page load fetch just the category Buckets
		LinkedHashMap<String, String> categoryBuckets = fetchCategoryList(eventTypeCode);
		req.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,
				categoryBuckets);
		}
		req.serviceLocalParameter(BBBCoreConstants.OPARAM, req, res);
		BBBPerformanceMonitor.end("GuestRegistryItemsDisplayDroplet", "GuestRegistryItemsDisplayDroplet");
	}
	
	@SuppressWarnings("unchecked")
	private void fetchOmnitureDetailsForCopyRegistry(
			DynamoHttpServletRequest req,StringBuffer omniProductList) {
		logDebug("FetchOmnitureDetailsForCopyREgistry start : "+System.currentTimeMillis());
		 List<OmnitureGuestRegistryVO> omnitureList =(List<OmnitureGuestRegistryVO>) getSessionBean().getValues().get(OMNITURE_LIST);
		 StringBuffer certonaProductList =new StringBuffer();
		 if(omnitureList==null || omnitureList.size() <1){
			 getSessionBean().getValues().remove(OMNITURE_LIST);
				return;
			}
		Iterator<OmnitureGuestRegistryVO> iter=omnitureList.iterator();
		while(iter.hasNext()){
			OmnitureGuestRegistryVO omniGuestVO=iter.next();
			String omniProdvalue = ";"+ omniGuestVO.getParentProductId() + ";;;event22="+ omniGuestVO.getQuantity() + "|event23=" + omniGuestVO.getPrice() + ";eVar30=" + omniGuestVO.getSkuId();
			omniProductList.append(omniProdvalue);
			certonaProductList.append(omniGuestVO.getParentProductId()+";");
		}
		String itemList="";
		if(certonaProductList.length()> 1 ){
		itemList=certonaProductList.substring(0,certonaProductList.length()-1);
		}
		req.setParameter(BBBGiftRegistryConstants.ITEM_LIST, itemList);
		getSessionBean().getValues().remove(OMNITURE_LIST);
	}

	/** This method is used to populate not in stock list and remove items from session
	 * @param req
	 * @param res
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, GuestRegistryItemsListVO> fetchBelowLineCategoryItems(DynamoHttpServletRequest req, DynamoHttpServletResponse res) {
		String registryId = req.getParameter(BBBGiftRegistryConstants.REGISTRY_ID);
		String giftGiverOutOfStockRegItemsKey=BBBGiftRegistryConstants.GIFT_GIVER_OUT_OF_STOCK_REG_ITEMS+registryId;
		BBBSessionBean sessionBean = (BBBSessionBean) req.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME);
		if(sessionBean == null){
			sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		}
		this.setSessionBean(sessionBean);
		if(getSessionBean().getValues().get(giftGiverOutOfStockRegItemsKey)==null){
			// no in stock category left
			return null ;
		}
		String eventDate=req.getParameter(BBBGiftRegistryConstants.EVENT_DATE);
		// Fetching out of stock items from session.
		Map<String, GuestRegistryItemsListVO> giftGiverOutOfStockRegItemsMap = (Map<String, GuestRegistryItemsListVO>) getSessionBean().getValues().get(giftGiverOutOfStockRegItemsKey);
		if(giftGiverOutOfStockRegItemsMap.isEmpty()){
			return null;
		}
		List<OmnitureGuestRegistryVO> omnitureList =(List<OmnitureGuestRegistryVO>) getSessionBean().getValues().get(OMNITURE_LIST);
		
		Iterator iter = giftGiverOutOfStockRegItemsMap.entrySet().iterator();
		    while (iter.hasNext()) {
		        Map.Entry registryItemList = (Map.Entry)iter.next();
		        GuestRegistryItemsListVO registryItemsListVO = (GuestRegistryItemsListVO) registryItemList.getValue();
		        getGiftRegistryManager().fliterNotAvliableItem(registryItemsListVO);
		        // Skipping inventory check for out of sku
		        getGiftRegistryManager().populateSKUDetailsInRegItem(registryItemsListVO,false);
		        getGiftRegistryManager().setLTLAttributesInRegItem((GuestRegistryItemsListVO) registryItemList.getValue(), eventDate);
		        getGiftRegistryManager().personlizeImageUrl(registryItemsListVO.getRegistryItemList());
		        getGiftRegistryManager().populatePriceInfoInRegItem((GuestRegistryItemsListVO) registryItemList.getValue());
		        // Added for omniture event fire on copy registry.
		        if(registryItemsListVO !=null && registryItemsListVO.getRegistryItemList() !=null)
				 for(RegistryItemVO regItem:registryItemsListVO.getRegistryItemList()){
					 double price= 0.0;
					 OmnitureGuestRegistryVO omnitureGuestRegVO=new OmnitureGuestRegistryVO();
					 omnitureGuestRegVO.setSkuId(regItem.getSku());
					 omnitureGuestRegVO.setQuantity(regItem.getQtyRequested());
					 omnitureGuestRegVO.setParentProductId(regItem.getsKUDetailVO().getParentProdId());
					 price=Double.parseDouble(regItem.getPrice());
					 price =price*regItem.getQtyRequested();
					 omnitureGuestRegVO.setPrice(price);
					 omnitureList.add(omnitureGuestRegVO);
				 }
		    }
			  List<String> tempCatList=new ArrayList<String>();
			  Iterator it = giftGiverOutOfStockRegItemsMap.entrySet().iterator();
			  // Remove empty out of stock category maps. 
		   while(it.hasNext()){
			 Map.Entry pair = (Map.Entry)it.next();
	         GuestRegistryItemsListVO regItemListVO=(GuestRegistryItemsListVO) pair.getValue();
			  if(regItemListVO.getRegistryItemList() ==null || regItemListVO.getRegistryItemList().size()<1){
				  tempCatList.add(regItemListVO.getCategoryId());
			  }
		  }
		 giftGiverOutOfStockRegItemsMap.keySet().removeAll(tempCatList);
		 // Removing the session keys.
		 getSessionBean().getValues().remove(giftGiverOutOfStockRegItemsKey);
		return giftGiverOutOfStockRegItemsMap;
	}


	/**This method would iterate over all the in stock category map as put in session .Update the out of stock list if item is out of stock. Return the in stock list and remove the in 
	 * stock items from session
	 * @param req
	 * @param res
	 * @param notInStockCategoryList
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, GuestRegistryItemsListVO> fetchAllCategoryItems(DynamoHttpServletRequest req, DynamoHttpServletResponse res, List<String> notInStockCategoryList) {
		String registryId = req.getParameter(BBBGiftRegistryConstants.REGISTRY_ID);
		
		String eventDate=req.getParameter(BBBGiftRegistryConstants.EVENT_DATE);
		logDebug("Entering RegistryCategoryDisplayDroplet with registry id :"+registryId);
		
		String giftGiverInStockRegItemsKey=BBBGiftRegistryConstants.GIFT_GIVER_IN_STOCK_REG_ITEMS+registryId;
		
		BBBSessionBean sessionBean = (BBBSessionBean) req.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME);
		if(sessionBean == null){
			sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		}
		this.setSessionBean(sessionBean);
		
		if(getSessionBean().getValues().get(giftGiverInStockRegItemsKey)==null){
			// no in stock category left
			return null ;
		}
		
		// If inventoryCallEnabled is null or true : include the inventory checks in flow
		String isInventoryCallEnabled = req.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED);
		boolean isPerformInventoryCheck=false;
		
		if(BBBUtility.isEmpty(isInventoryCallEnabled) || BBBCoreConstants.TRUE.equalsIgnoreCase(isInventoryCallEnabled)){
			isPerformInventoryCheck=true;
		}
		String giftGiverOutOfStockRegItemsKey=BBBGiftRegistryConstants.GIFT_GIVER_OUT_OF_STOCK_REG_ITEMS+registryId;
		
		//Fetch in stock and out of stock category maps.
		Map<String, GuestRegistryItemsListVO> giftGiverOutOfStockRegItemsMap = (Map<String, GuestRegistryItemsListVO>) getSessionBean().getValues().get(giftGiverOutOfStockRegItemsKey);
		Map<String, GuestRegistryItemsListVO> giftGiverInStockRegItemsMap = (Map<String, GuestRegistryItemsListVO>) getSessionBean().getValues().get(giftGiverInStockRegItemsKey);
		List<OmnitureGuestRegistryVO> omnitureList =(List<OmnitureGuestRegistryVO>) getSessionBean().getValues().get(OMNITURE_LIST);
		 
		//Iterate over in stock list and populate sku details. If inventory is available add them into not in stock list.
		Iterator iter = giftGiverInStockRegItemsMap.entrySet().iterator();
		    while (iter.hasNext()) {
		        Map.Entry registryItemListVO = (Map.Entry)iter.next();
		        GuestRegistryItemsListVO registryItemsListVO = (GuestRegistryItemsListVO) registryItemListVO.getValue();
		        getGiftRegistryManager().fliterNotAvliableItem(registryItemsListVO);
		        getGiftRegistryManager().removeOutOfStockItems(giftGiverOutOfStockRegItemsMap,registryItemsListVO,isPerformInventoryCheck);
		        getGiftRegistryManager().populateSKUDetailsInRegItem(registryItemsListVO,isPerformInventoryCheck);
				getGiftRegistryManager().setLTLAttributesInRegItem((GuestRegistryItemsListVO) registryItemListVO.getValue(), eventDate);
		        getGiftRegistryManager().personlizeImageUrl(registryItemsListVO.getRegistryItemList());
		        getGiftRegistryManager().populatePriceInfoInRegItem((GuestRegistryItemsListVO) registryItemListVO.getValue());
				 // Added for omniture event fire on copy registry.
		        if(registryItemsListVO !=null && registryItemsListVO.getRegistryItemList() !=null)
				 for(RegistryItemVO regItem:registryItemsListVO.getRegistryItemList()){
					 double price= 0.0;
					 OmnitureGuestRegistryVO omnitureGuestRegVO=new OmnitureGuestRegistryVO();
					 omnitureGuestRegVO.setSkuId(regItem.getSku());
					 omnitureGuestRegVO.setQuantity(regItem.getQtyRequested());
					 omnitureGuestRegVO.setParentProductId(regItem.getsKUDetailVO().getParentProdId());
					 price=Double.parseDouble(regItem.getPrice());
					 price =price*regItem.getQtyRequested();
					 omnitureGuestRegVO.setPrice(price);
					 omnitureList.add(omnitureGuestRegVO);
				 }
		    }
		    getSessionBean().getValues().put(OMNITURE_LIST, omnitureList);
		   
		    // Remove all those in stock keys from regItemsInStockListByCategory map which donot have any registry Items.
			  List<String> tempCatList=new ArrayList<String>();
			  Iterator it = giftGiverInStockRegItemsMap.entrySet().iterator();
			 while(it.hasNext()){
				 Map.Entry pair = (Map.Entry)it.next();
		         GuestRegistryItemsListVO regItemListVO=(GuestRegistryItemsListVO) pair.getValue();
				  if(regItemListVO.getRegistryItemList() ==null || regItemListVO.getRegistryItemList().size()<1){
					  tempCatList.add(regItemListVO.getCategoryId());
				  }
			  }
		
		// Remove empty category buckets	 
		giftGiverInStockRegItemsMap.keySet().removeAll(tempCatList);
		// Update out of stock list
		for(String catId:tempCatList){
			notInStockCategoryList.add(catId);
		}
		// Update out of stock list in session
		getSessionBean().getValues().put(giftGiverOutOfStockRegItemsKey, giftGiverOutOfStockRegItemsMap);
		// Remove in stock list from session.
		getSessionBean().getValues().remove(giftGiverInStockRegItemsKey);
		return giftGiverInStockRegItemsMap;
		
	}


	/**This method based on the registry id and event code as the input param fetches the category list on the basis of reg item vo and updates the session 
	 * with in stock and not in  stock category data. Then fetches the first non zero in stock category and fetches the data of sku ,inventory and price and updates
	 * the session by removing that category and updates the not in stock category list. 
	 * @param req
	 * @param res
	 * @return 
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private GuestRegistryItemsListVO fetchFirstCategoryItems(DynamoHttpServletRequest req, DynamoHttpServletResponse res,List<String> notInStockCategoryList) throws BBBSystemException, BBBBusinessException {
		String eventTypeCode = req
				.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE);
		String registryId = req.getParameter(BBBGiftRegistryConstants.REGISTRY_ID);
		String view = req.getParameter(BBBGiftRegistryConstants.VIEW );
		// If inventoryCallEnabled is null or true : include the inventory checks in flow
		String isInventoryCallEnabled = req.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED);
		boolean isPerformInventoryCheck=false;
		
		if(BBBUtility.isEmpty(isInventoryCallEnabled) || BBBCoreConstants.TRUE.equalsIgnoreCase(isInventoryCallEnabled)){
			isPerformInventoryCheck=true;
		}
		
		if (BBBUtility.isEmpty(view)) {
			view = BBBGiftRegistryConstants.DEFAULT_ALL_VIEW_FILTER;
		}
        if(eventTypeCode ==null || registryId==null){
        	return null;
        }
		logDebug("Entering fetchFirstCategoryItems| GuestRegistryItemsDisplayDroplet with Registry Type : "
				+ eventTypeCode +" registry id :"+registryId);

		String giftGiverInStockRegItemsKey=BBBGiftRegistryConstants.GIFT_GIVER_IN_STOCK_REG_ITEMS+registryId;
		String giftGiverOutOfStockRegItemsKey=BBBGiftRegistryConstants.GIFT_GIVER_OUT_OF_STOCK_REG_ITEMS+registryId;
		
		// Puts the value of in stock and non in stock data in two maps.
		Map<String, GuestRegistryItemsListVO> regItemsInStockListByCategory=new LinkedHashMap<String, GuestRegistryItemsListVO>();
		Map<String, GuestRegistryItemsListVO> regItemsOutOfStockListByCategory=new LinkedHashMap<String, GuestRegistryItemsListVO>();
		
		List<RegistryItemVO> regItemsList=null;
		
		// This list stores the data of registry items returned from stored procedure.
		try {
			regItemsList = getGiftRegistryManager().fetchRegItemsListByCategory(eventTypeCode,registryId);
		} catch (BBBBusinessException e) {
			logError(e.getMessage(),e);
		}
		
		if(regItemsList ==null){
			logDebug("regItemsList is null for registry id :"+registryId);
			return null;
		}
		 List<OmnitureGuestRegistryVO> omniParams = new ArrayList<OmnitureGuestRegistryVO>();
		LinkedHashMap<String, String> categoryBuckets = fetchCategoryList(eventTypeCode);
		// Adding all the categories in not in stock list and later this would be removed
		for(String catId:categoryBuckets.keySet()){
	        	notInStockCategoryList.add(catId);
	        }
		 
		
        boolean enableBuyOffRegistry = getGiftRegistryManager().enableBuyOffStartBrowsing(regItemsList);
        
        req.setParameter(BUYOFF_SHOW_START_BROWSING,enableBuyOffRegistry);				
        req.setParameter(REG_ITEM_COUNT,regItemsList!=null?regItemsList.size():0);	
        
        // Based on the first remove the registry items are do not lie in the view selected
        if(!BBBGiftRegistryConstants.DEFAULT_ALL_VIEW_FILTER.equalsIgnoreCase(view)){
        	List<RegistryItemVO> updatedList = getGiftRegistryManager().removeRegItemsBasedOnFilter(regItemsList,view);
        	regItemsList =new ArrayList<RegistryItemVO>();
        	for(RegistryItemVO registryItemVO:updatedList){
        		regItemsList.add(registryItemVO);
        	}
        }
       	
        // Populate in stock/out of stock map and not in stock category list.
        getGiftRegistryManager().populateCategoryMap(categoryBuckets,regItemsList,regItemsInStockListByCategory,regItemsOutOfStockListByCategory,notInStockCategoryList);
		
        String eventDate=req.getParameter(BBBGiftRegistryConstants.EVENT_DATE);    
        BBBSessionBean sessionBean = (BBBSessionBean) req.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME);
		if(sessionBean == null){
			sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		}
		this.setSessionBean(sessionBean);
	
		 GuestRegistryItemsListVO firstCategory=null;
		 if(regItemsInStockListByCategory !=null && !regItemsInStockListByCategory.isEmpty()){
			  // Fetch the first non zero category items list.
			  firstCategory = getGiftRegistryManager().fetchFirstC1Data(regItemsInStockListByCategory,regItemsOutOfStockListByCategory,isPerformInventoryCheck);
			  Iterator it = regItemsInStockListByCategory.entrySet().iterator();
			 
			  // Remove all those in stock keys from regItemsInStockListByCategory map which donot have any registry Items.
			  List<String> tempCatList=new ArrayList<String>();
			 while(it.hasNext()){
				 Map.Entry pair = (Map.Entry)it.next();
		         GuestRegistryItemsListVO regItemListVO=(GuestRegistryItemsListVO) pair.getValue();
				  if(regItemListVO.getRegistryItemList() ==null || regItemListVO.getRegistryItemList().size()<1){
					  tempCatList.add(regItemListVO.getCategoryId());
				  }
			  }
			 regItemsInStockListByCategory.keySet().removeAll(tempCatList);
			 
			// If the first category is not null then populate the sku details and price info.
		   if(firstCategory!=null && firstCategory.getRegistryItemList() !=null && firstCategory.getRegistryItemList().size()>0){
			 getGiftRegistryManager().setLTLAttributesInRegItem(firstCategory, eventDate);
			 getGiftRegistryManager().populatePriceInfoInRegItem(firstCategory);
			 getGiftRegistryManager().personlizeImageUrl(firstCategory.getRegistryItemList());
			 // Added for omniture event fire on copy registry.
			 for(RegistryItemVO regItem:firstCategory.getRegistryItemList()){
				 double price= 0.0;
				 OmnitureGuestRegistryVO omnitureGuestRegVO=new OmnitureGuestRegistryVO();
				 omnitureGuestRegVO.setSkuId(regItem.getSku());
				 omnitureGuestRegVO.setQuantity(regItem.getQtyRequested());
				 omnitureGuestRegVO.setParentProductId(regItem.getsKUDetailVO().getParentProdId());
				 price=Double.parseDouble(regItem.getPrice());
				 price =price*regItem.getQtyRequested();
				 omnitureGuestRegVO.setPrice(price);
				 omniParams.add(omnitureGuestRegVO);
			 }
			 }
		 }
		 
		 // Remove those category ids which are out of stock list and not in in stock list
		 Iterator iterOut = regItemsOutOfStockListByCategory.entrySet().iterator();
		    while (iterOut.hasNext()) {
		        Map.Entry registryItemListVO = (Map.Entry)iterOut.next();
		        String outCatId=(String) registryItemListVO.getKey();
		        if(!regItemsInStockListByCategory.containsKey(outCatId)){
		        	notInStockCategoryList.add(outCatId);
   	        }
		        
		 }
		 // Remove first category list.
		 if(regItemsInStockListByCategory !=null && firstCategory !=null){   
		 regItemsInStockListByCategory.remove(firstCategory.getCategoryId());
		 }
		 // Updates the session with these two maps to avoid any further calls to stored procedure.
		 getSessionBean().getValues().put(giftGiverOutOfStockRegItemsKey, regItemsOutOfStockListByCategory);
		 getSessionBean().getValues().put(giftGiverInStockRegItemsKey, regItemsInStockListByCategory);
		 getSessionBean().getValues().put(OMNITURE_LIST, omniParams);
		 return firstCategory;
	}


	/**
	 * Fetch category list.
	 *
	 * @param eventTypeCode the event type code
	 * @return the linked hash map
	 */
	private LinkedHashMap<String,String> fetchCategoryList(String eventTypeCode) {
		logDebug("Inside checkRegistryType For eventTypeCode "+eventTypeCode);
		LinkedHashMap<String,String> categoryMap=null;
		if(BBBUtility.isEmpty(eventTypeCode)){
			return null;
		}
			try {
				categoryMap=getCheckListManager().getEPHCategoryBasedOnRegistryType(eventTypeCode);
				if(categoryMap !=null && !categoryMap.containsKey(BBBGiftRegistryConstants.OTHER)){
					categoryMap.put(BBBGiftRegistryConstants.OTHER, BBBGiftRegistryConstants.OTHER)	;
				}
				return categoryMap;
			} catch (BBBSystemException | BBBBusinessException e) {
				logError("Error fetching ephCategoriesList",e);
			}
		
		return categoryMap;
		
	}
	
	/**
	 * Used in mobile to get the details of all C1's for gifter view.
	 * 
	 * @return Map<String,String>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<GuestRegistryItemsListVO> getDetailsForOOS(String isThirdAjaxCall,String view,String registryId,String invCheckEnabled,String eventDate){
		logDebug("GuestRegistryItemsDisplayDroplet:getDetailsForOOS() method with input parameters:isThirdAjaxCall: " + isThirdAjaxCall + ", view: " + view +",registryId: " + registryId + ",invCheckEnabled: " + invCheckEnabled + ",eventDate: " + eventDate);
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		request.setParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL, isThirdAjaxCall);
		request.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId);
		request.setParameter(BBBGiftRegistryConstants.VIEW, view);
		request.setParameter(BBBGiftRegistryConstants.EVENT_DATE, eventDate);
		request.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled);
		Map<String, GuestRegistryItemsListVO>  categoryBuckets = null;	
		List<GuestRegistryItemsListVO> catList = new ArrayList<GuestRegistryItemsListVO>();
		logDebug("Calling the service method of GuestRegistryItemsDisplayDroplet");
			try {
				service(request,
						ServletUtil.getCurrentResponse());
				categoryBuckets =  (Map<String, GuestRegistryItemsListVO>) request.getObjectParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS);
				Iterator iterator = categoryBuckets.entrySet().iterator();
			    while (iterator.hasNext()) {
			        Map.Entry pairs = (Map.Entry)iterator.next();
			       
			        if(((GuestRegistryItemsListVO)pairs.getValue()).getRegistryItemList().size() > 0){
			        	catList.add((GuestRegistryItemsListVO) pairs.getValue());
			        }
			    }
			} catch (ServletException e) {
				logError("GuestRegistryItemsDisplayDroplet:getDetailsForOOS() ServletException: ",e);
			}
			catch (IOException e) {
				logError("GuestRegistryItemsDisplayDroplet:getDetailsForOOS() IOException: ",e);
			}			
			return catList;	
	}
	
	/**
	 * Used in mobile to get the details of all C1's for gifter view.
	 * 
	 * @return Map<String,String>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public GuestRegistryItemsFirstCallVO getDetailsForAllCat(String isSecondAjaxCall,String registryId,String view,String invCheckEnabled,String eventDate){
		logDebug("GuestRegistryItemsDisplayDroplet:getDetailsForAllCat() method with input parameters:isSecondAjaxCall: " + isSecondAjaxCall + ", view: " + view +",registryId: " + registryId + ",invCheckEnabled: " + invCheckEnabled + ",eventDate: " + eventDate);
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
	    request.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId);
		request.setParameter(BBBGiftRegistryConstants.VIEW, view);
		request.setParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL, isSecondAjaxCall);
		request.setParameter(BBBGiftRegistryConstants.EVENT_DATE,eventDate);
		request.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled);
		GuestRegistryItemsFirstCallVO guestCallVo = new GuestRegistryItemsFirstCallVO();
		Map<String, GuestRegistryItemsListVO> categoryBuckets = null;	
		List<GuestRegistryItemsListVO> catList = new ArrayList<GuestRegistryItemsListVO>();
		List<String> notInStockCategoryList = null;
		logDebug("Calling the service method of GuestRegistryItemsDisplayDroplet");
			try {
				service(request,
						ServletUtil.getCurrentResponse());
				categoryBuckets =  (Map<String, GuestRegistryItemsListVO>) request.getObjectParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS);
				notInStockCategoryList = (List<String>)request.getObjectParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST);
				Iterator iterator = categoryBuckets.entrySet().iterator();
			    while (iterator.hasNext()) {
			        Map.Entry pairs = (Map.Entry)iterator.next();
			        if(((GuestRegistryItemsListVO)pairs.getValue()).getRegistryItemList().size() > 0){
			        catList.add((GuestRegistryItemsListVO) pairs.getValue());
			        }
			    }
			guestCallVo.setRemainingCategoryBuckets(catList);
			guestCallVo.setNotInStockCategoryList(notInStockCategoryList);
			} catch (ServletException e) {
				logError("GuestRegistryItemsDisplayDroplet:getDetailsForAllCat() ServletException: ",e);
			}
			catch (IOException e) {
				logError("GuestRegistryItemsDisplayDroplet:getDetailsForAllCat() IOException: ",e);
			}			
			return guestCallVo;	
	}

	/**
	 * Used in mobile to get the details of first C1 for gifter view. It also fetches a flag to show or hide start browsing button.
	 * 
	 * @return Map<String,String>
	 */
	@SuppressWarnings("unchecked")
	public GuestRegistryItemsFirstCallVO getDetailsForFirstC1(String eventTypeCode,String isFirstAjaxCall,String registryId,String view,String invCheckEnabled,String eventDate){
		logDebug("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() method with input parameters:isFirstAjaxCall: " + isFirstAjaxCall + ", view: " + view +",registryId: " + registryId + ",invCheckEnabled: " + invCheckEnabled + ",eventDate: " + eventDate + ",eventTypeCode: " + eventTypeCode);
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		request.setParameter(BBBGiftRegistryConstants.VIEW, view);
		request.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, eventTypeCode);
		request.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId);
		request.setParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL, isFirstAjaxCall);
		request.setParameter(BBBGiftRegistryConstants.EVENT_DATE, eventDate);
		request.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled);
		GuestRegistryItemsListVO categoryBuckets = null;	
		List<String> notInStockCategoryList = null;
		GuestRegistryItemsFirstCallVO guestRegItemsFirstCall = new GuestRegistryItemsFirstCallVO();
		logDebug("Calling the service method of GuestRegistryItemsDisplayDroplet");
			try {
				service(request,
						ServletUtil.getCurrentResponse());
				
				categoryBuckets =  (GuestRegistryItemsListVO) request.getObjectParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS);
				guestRegItemsFirstCall.setCategoryBuckets(categoryBuckets);
				notInStockCategoryList = (List<String>)request.getObjectParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST);
				guestRegItemsFirstCall.setNotInStockCategoryList(notInStockCategoryList);
				guestRegItemsFirstCall.setShowStartBrowsing(Boolean.parseBoolean(request.getParameter(BUYOFF_SHOW_START_BROWSING)));
				guestRegItemsFirstCall.setRegItemCount(request.getParameter(REG_ITEM_COUNT));
			} catch (ServletException e) {
				logError("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException: ",e);
			}
			catch (IOException e) {
				logError("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() IOException: ",e);
			}			
			return guestRegItemsFirstCall;	
	}
	

	/**
	 * Used in mobile to get the details of all C1's for gifter view.
	 * 
	 * @return Map<String,String>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LinkedHashMap<String,String> getAllC1ForGifterView(String eventTypeCode){
		logDebug("GuestRegistryItemsDisplayDroplet:getAllC1ForGifterView() method with input parameters: eventTypeCode: " + eventTypeCode);
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
	request.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, eventTypeCode);
	LinkedHashMap<String,String> categoryBuckets = null;		
	LinkedHashMap<String,String> sortedCategoryBuckets = new LinkedHashMap<>();		
		logDebug("Calling the service method of GuestRegistryItemsDisplayDroplet");
			try {
				service(request,
						ServletUtil.getCurrentResponse());
				categoryBuckets =  (LinkedHashMap<String, String>) request.getObjectParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS);
				// Sorting the map for mobile 
				Iterator iterCat = categoryBuckets.entrySet().iterator();
				int count=100;
			    while (iterCat.hasNext()) {
			        Map.Entry catId = (Map.Entry)iterCat.next();
			        String outCatId=(String) catId.getKey();
			        sortedCategoryBuckets.put(String.valueOf(count++).concat(BBBCatalogConstants.DELIMITER).concat(outCatId), (String) catId.getValue());
			    }
			} catch (ServletException e) {
				logError("GuestRegistryItemsDisplayDroplet:getAllC1ForGifterView() ServletException: ",e);
			}
			catch (IOException e) {
				logError("GuestRegistryItemsDisplayDroplet:getAllC1ForGifterView() IOException: ",e);
			}
			return sortedCategoryBuckets;	
	}
	
	public CheckListManager getCheckListManager() {
		return checkListManager;
	}

	public void setCheckListManager(CheckListManager checkListManager) {
		this.checkListManager = checkListManager;
	}
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public GiftRegistryManager getGiftRegistryManager() {
		return giftRegistryManager;
	}

	public void setGiftRegistryManager(GiftRegistryManager giftRegistryManager) {
		this.giftRegistryManager = giftRegistryManager;
	}
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}
}