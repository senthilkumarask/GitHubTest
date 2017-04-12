package com.bbb.search.endeca;

import static com.bbb.search.endeca.EndecaSearchUtil.isBrandPageRequest;
import static com.bbb.search.endeca.EndecaSearchUtil.isKeywordSearchRequest;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.BRAND;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_HEADER_CONTENT;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.DEPARTMENT;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.NAVIGATION;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.RECORD_TYPE;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.SITE_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.math.NumberUtils;

import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.Asset;
import com.bbb.search.bean.result.AutoSuggestVO;
import com.bbb.search.bean.result.BBBDynamicPriceVO;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.BBBProductList;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.PromoVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.bean.result.SkuVO;
import com.bbb.search.endeca.assembler.cartridge.config.CustomRefinementMenu;
import com.bbb.search.endeca.assembler.cartridge.config.FeaturedItemsContentItem;
import com.bbb.search.endeca.assembler.cartridge.config.FeaturedProducts;
import com.bbb.search.endeca.assembler.cartridge.handler.CategoryRefinementMenu;
import com.bbb.search.endeca.assembler.cartridge.handler.FlyoutFeaturedItemContent;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.util.EndecaConfigUtil;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.Breadcrumbs;
import com.endeca.infront.cartridge.NavigationContainer;
import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.cartridge.ResultsList;
import com.endeca.infront.cartridge.SearchAdjustments;
import com.endeca.infront.cartridge.model.AdjustedSearch;
import com.endeca.infront.cartridge.model.Ancestor;
import com.endeca.infront.cartridge.model.Attribute;
import com.endeca.infront.cartridge.model.Record;
import com.endeca.infront.cartridge.model.Refinement;
import com.endeca.infront.cartridge.model.RefinementBreadcrumb;
import com.endeca.infront.cartridge.model.SearchBreadcrumb;
import com.endeca.infront.cartridge.model.SuggestedSearch;
import com.endeca.soleng.urlformatter.UrlFormatException;


/**
 * This class is responsible for converting content item response into search results vo
 * 
 * @author sc0054
 *
 */
public class EndecaContentResponseParser extends BBBGenericService {
	
	private EndecaConfigUtil configUtil;
	
	private EndecaSearch endecaSearch;
	
	private EndecaSearchUtil endecaSearchUtil;
	
	private BBBConfigTools mConfigTools;
	
	private static final String TRUCK_DELIVERY = "Truck Delivery";
	private static final String FREE_STANDARD_SHIPPING = "Free Standard Shipping";
	/**
	 * Util method for converting ContentItem response into EndecaContentResonseVO object
	 * @param pContentItem
	 * @return EndecaContentResponseVO
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public SearchResults extractContentResponse(ContentItem pContentItem, SearchQuery pSearchQuery) 
			throws BBBSystemException, BBBBusinessException {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "extractContentResponse");	
		
		logDebug("Entering EndecaContentResponseParser.extractContentResponse method");
		
		SearchResults searchResults = new SearchResults();
		
		if(pContentItem != null) {
			
			//get contents from contentItem if not null
			Object contents = pContentItem.get(BBBEndecaConstants.CI_CONTENTS_NAME);
			if(contents != null) {
				logDebug("contents not null");
				if(contents instanceof List<?>) {
					logDebug("contents instanceof List");
					List<ContentItem> contentItemList = (List<ContentItem>)contents;
					processContentList(contentItemList,searchResults, pSearchQuery);//for contentItemList
				} else if(contents instanceof ContentItem){
					//contents is of type ContentItem
					logDebug("contents instanceof ContentItem");
					ContentItem contentItem = (ContentItem)contents;
					processContentItem(contentItem,searchResults,contentItem.getType(), pSearchQuery);
				} //if else contents instanceof
			} 
			
		} 
		
		logDebug("finished processing pContentItem");
		
		//CENTER key is referenced in subcategory.jsp while displaying category name
		//this will not be set in above code when only footer text is set
		createPlaceholderPromoMapWhenMissing(searchResults);
		
		if(null!= searchResults && null!= searchResults.getFacets()) {
			searchResults.setAssetMap(getSearchTabs(searchResults.getFacets()));
		}
		
		//set category names for Category PLP
		if(null!= searchResults && null!= searchResults.getDescriptors()) {
			searchResults.setCurrentCatName(getCurrentCatName(searchResults.getDescriptors()));
			searchResults.setParentCatName(getParentCatName(searchResults.getDescriptors()));
		}
		if(getConfigUtil().getCatalogTools().getValueForConfigKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_COLOR_RELEVANCY, false)){
			//update image links to point to current selected color's images
			getEndecaSearchUtil().updateProductImageWithSwatchImageUrls(searchResults, pSearchQuery);
		}
		else{
			updateSwatchImageUrls(searchResults);
		}
		
		
		if(pSearchQuery != null){
			searchResults.setPagingLinks(getEndecaSearch().getPagination(searchResults.getBbbProducts(),pSearchQuery));
			
			//setting these values to null for all queries except search
			if(!isKeywordSearchRequest(pSearchQuery)) {
				searchResults.setAssetMap(null);
				searchResults.setAutoSuggest(null);
			} 
			
			searchResults.setSearchQuery(getEndecaSearch().getCurrentSearchQuery(pSearchQuery));
		}
		logDebug("Exit EndecaContentResponseParser.extractContentResponse method");		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "extractContentResponse");		
		return searchResults;
	}
	
	
	/**
	 * This method itearates over the complete ThreeColumnPage template and parses each ContentItem in turn 
	 * @param pContentItemList
	 * @param searchResults
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	private void processContentList(List<ContentItem> pContentItemList,SearchResults searchResults, SearchQuery pSearchQuery) 
			throws BBBSystemException, BBBBusinessException {		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "processContentList");
		logDebug("Entering EndecaContentResponseParser.processContentList method");
		for(int i=0;i<pContentItemList.size();i++) {
			
			ContentItem rootContentItem = pContentItemList.get(i);
			if(getConfigUtil().getValidRootTemplateIDs().contains(rootContentItem.getType())) {
			/*if(rootContentItem.getType().equals("ThreeColumnNavigationPage") 
					|| rootContentItem.getType().equals("ContentSlotMain") 
					|| rootContentItem.getType().equals("ContentSlotHeader")
					|| rootContentItem.getType().equals("ContentSlotSecondary")) {*/ 
				Set<String> rootContentKeys = rootContentItem.keySet();
				for(String rootContentKey : rootContentKeys) {
					//rootContentItem.get(rootContentKey);
					
					Object currentContentItemObject = rootContentItem.get(rootContentKey);
					
					if(currentContentItemObject instanceof List<?>) {
						
						List<ContentItem> individualContentItemList = (List<ContentItem>)currentContentItemObject;
						for(int k=0;individualContentItemList!=null && k<individualContentItemList.size();k++) {
							ContentItem individualContentItem = (ContentItem)individualContentItemList.get(k);
							
							//dynamic slots would have contents here instead of actual content item
							//call processcontentlist again to iterate over
							if(null != individualContentItem.get(BBBEndecaConstants.CI_CONTENTS_NAME)) {
								Object contentsFromDynamicSlot = individualContentItem.get(BBBEndecaConstants.CI_CONTENTS_NAME);
								if(contentsFromDynamicSlot instanceof List<?>) {
									//call processContentList on this inner contents 
									processContentList((List<ContentItem>)contentsFromDynamicSlot,searchResults, pSearchQuery);
								}
							} else {
								processContentItem(individualContentItem,searchResults,rootContentKey, pSearchQuery);
							}
						}
					}//if object is instanceof List<ContentItem>
				}//rootContentItem contents
			} else {
				//indicates that root content item is not of type ThreeColumnPage
				//template at this location only has partial content XML
				//calling processContentItem on this ContentItem
				processContentItem(rootContentItem,searchResults,rootContentItem.getType(), pSearchQuery);
			}//if else root is ThreeColumnPage or one of ContentSlot
		}
		logDebug("Exit EndecaContentResponseParser.processContentList method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "processContentList");
	}


	/**
	 * To check for type of each ContentItem and process accordingly
	 * @param pContentItem
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	private void processContentItem(ContentItem pContentItem, SearchResults searchResults,String contentGroupName, SearchQuery pSearchQuery) 
			throws BBBSystemException, BBBBusinessException {	
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "processContentItem");
		logDebug("Entering EndecaContentResponseParser.processContentItem method");
		logDebug("Content Item Object - "+pContentItem.toString());
		if(pContentItem instanceof NavigationContainer) {
			logDebug("processing NavigationContainer - "+pContentItem);
			NavigationContainer navigationContainer = (NavigationContainer) pContentItem;
			//setting facets to contentresponsevo object
			searchResults.setFacets(convertToFacetListVO(navigationContainer, pSearchQuery));
			
			setCategoryRefinement(navigationContainer, searchResults);		
		} else if(pContentItem instanceof ResultsList) {
			logDebug("processing ResultsList - "+pContentItem);
			searchResults.setBbbProducts(convertToBBBProductListVO((ResultsList)pContentItem));
		} else if(pContentItem instanceof FeaturedItemsContentItem) {
			logDebug("processing FeaturedItemsContentItem - "+pContentItem);
			//use contentGroupName for putting into map 
			Map<String, List<PromoVO>> promoMap = searchResults.getPromoMap();
			promoMap = extractPromoList(contentGroupName, (FeaturedItemsContentItem)pContentItem, promoMap);
			searchResults.setPromoMap(promoMap);
		} else if(pContentItem instanceof FeaturedProducts) {
			logDebug("processing FeaturedProducts - "+pContentItem);
			//featured products will always be in CenterColumn
			Map<String, List<PromoVO>> promoMap = searchResults.getPromoMap();
			promoMap = extractFeaturedProducts((FeaturedProducts)pContentItem, promoMap);
			searchResults.setPromoMap(promoMap);
		} else if(pContentItem instanceof Breadcrumbs) {
			logDebug("processing Breadcrumbs for descritpors and autosuggest - "+pContentItem);
			searchResults.setDescriptors(convertToDescriptorListVO((Breadcrumbs)pContentItem, pSearchQuery));
			searchResults.setAutoSuggest(convertToAutoSuggestVO((Breadcrumbs)pContentItem,searchResults.getAutoSuggest()));
		} else if(pContentItem instanceof SearchAdjustments) {
			logDebug("processing SearchAdjustments for autosuggest - "+pContentItem);
			searchResults.setAutoSuggest(convertToAutoSuggestVO((SearchAdjustments)pContentItem,searchResults.getAutoSuggest()));
		}else if (pContentItem instanceof FlyoutFeaturedItemContent){
			logDebug("processing FlyoutFeaturedItemContent for Flyout Promos - "+pContentItem);
			//Used for L1 top nav promo
			String currCatId = ((FlyoutFeaturedItemContent)pContentItem).getCategoryId();
			
			//prepare Map<String, List<PromoVO>> in searchResults VO.
			Map<String, List<PromoVO>> promoMap = searchResults.getPromoMap();
			if(promoMap == null){
				promoMap = new HashMap<String,List<PromoVO>>();
				searchResults.setPromoMap(promoMap);
			}
			List<PromoVO> promoVOList = promoMap.get(currCatId);
			if(promoVOList == null){
				promoVOList = new ArrayList<PromoVO>();
			}
			//map flyouts to L1 category.
			addFlyoutsToCategory((FlyoutFeaturedItemContent) pContentItem, promoVOList);
			if(!promoVOList.isEmpty()){
				promoMap.put(currCatId, promoVOList);
			}
		}
		//if else if
		logDebug("Exit EndecaContentResponseParser.processContentItem method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "processContentItem");
	}
	
	private void addFlyoutsToCategory(FlyoutFeaturedItemContent pContentItem, List<PromoVO> pPromoVOs){
		logDebug("Entering EndecaContentResponseParser.addFlyoutsToCategory method");
		List<FeaturedItemsContentItem> imgItems = pContentItem.getTypedProperty(CI_PROP_HEADER_CONTENT);
		
		if(imgItems != null){
			for (FeaturedItemsContentItem featuredItemsContentItem : imgItems) {
				if(featuredItemsContentItem != null){
					PromoVO promoVO = new PromoVO();
					promoVO.setImageSrc(featuredItemsContentItem.getImageSrc());
					promoVO.setImageAlt(featuredItemsContentItem.getImageAlt());
					promoVO.setImageHref(featuredItemsContentItem.getImageHref());
					
					pPromoVOs.add(promoVO);
				}
			}
		}
		logDebug("Exit EndecaContentResponseParser.addFlyoutsToCategory method");
	}

	/**
	 * Helper method used for converting FeaturedItems ContentItem into promo list
	 * @param contentGroupName
	 * @param featuredItems
	 * @param promoMap
	 * @return
	 */
	protected Map<String, List<PromoVO>> extractPromoList(
			String contentGroupName, FeaturedItemsContentItem featuredItems,
			Map<String, List<PromoVO>> promoMap) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "extractPromoList");
		logDebug("Entering EndecaContentResponseParser.extractPromoList method");
		/*if(getConfigUtil().getCatridgeNameMap().values() != null 
				 && getConfigUtil().getCatridgeNameMap().containsValue(contentGroupName)) {*/
		if(getConfigUtil().getCatridgeNameMap().keySet() != null  && getConfigUtil().getCatridgeNameMap().containsKey(contentGroupName)) {
			String keyName = "";
			for(Map.Entry<String, String> entry : getConfigUtil().getCatridgeNameMap().entrySet()) {
				if(contentGroupName.equalsIgnoreCase(entry.getKey())) {
					keyName = entry.getValue().toUpperCase(Locale.getDefault());
					break;
				}
			}
			PromoVO promoVO = new PromoVO();
			promoVO.setImageAlt(featuredItems.getImageAlt());
			promoVO.setImageHref(featuredItems.getImageHref());
			promoVO.setImageSrc(featuredItems.getImageSrc());
			
			promoVO.setBlurbPlp(featuredItems.getFooterText());
			promoVO.setCaption(featuredItems.getCaption());
			promoVO.setLinkTarget(featuredItems.getLinkReference());
			
			String relatedSearchString = featuredItems.getRelatedSearches();
			//HYD-32 start
			StringBuilder relatedSearchStringKeys = new StringBuilder(); 
			logDebug("relatedSearchString"+relatedSearchString);
			if (null != relatedSearchString) {
  				String relatedString[] = relatedSearchString.split(BBBCatalogConstants.DELIMITERCOMMA);
  				LinkedHashMap<String, String> keywordMap = new LinkedHashMap<String, String>();
  				int sizeRelatedString = relatedString.length;
  				for (String commaSeperated : relatedString) {
  					// BBBSL-3455 - Start - storing sanitized search term as key and display text as value.
  					String key= commaSeperated.trim().toLowerCase().replaceAll("[\'\"]", "").replaceAll("[^a-z0-9]", " ").replaceAll("[ ]+", "-");
                    keywordMap.put(key, commaSeperated);                    
                    relatedSearchStringKeys.append(key);                    
                    if(sizeRelatedString>1){
                    	relatedSearchStringKeys.append(',');
	  					sizeRelatedString--;
                    }
                    
                    // BBBSL-3455 - End
                    //resultString = inputString.replace(/[^a-z0-9\'\"]/gi,' ').replace(/[\'\"]/g,'').replace(/[ ]+/g,' ').toLowerCase().trim().replace(/[ ]/g,'-');

  				}
  				promoVO.setRelatedSearchStringKeys(relatedSearchStringKeys.toString());
  				promoVO.setRelatedSeperated(keywordMap);
  			}
			//HYD-32 end
			promoVO.setRelatedSearchString(relatedSearchString);
	    	promoVO.setMobileImageHref(featuredItems.getMobileImageHref());
	    	promoVO.setMobileImageSrc(featuredItems.getMobileImageSrc());
	    	promoVO.setMobileImageAlt(featuredItems.getMobileImageAlt());
	    	
			logDebug("promoVO - "+promoVO);
			if(promoVO != null && promoVO.getImageSrc() != null){
			logDebug("In extractPromoList method of EndecaContentResponseParser For site: "+ SiteContextManager.getCurrentSiteId() +":: In PromoVO, Image HREF: - "+promoVO.getImageHref()+ " :: Image Alt text: - "+promoVO.getImageAlt()+ " :: Image source: - "+promoVO.getImageSrc());
			}
			List<PromoVO> currentPromoList;
			
			if(promoMap != null) {
				currentPromoList = promoMap.get(keyName);
				if(currentPromoList!= null && currentPromoList.size() > 0) {
					currentPromoList.add(promoVO);
					promoMap.put(keyName,currentPromoList);
				} else {
					currentPromoList = new ArrayList<PromoVO>();
					currentPromoList.add(promoVO);
					promoMap.put(keyName,currentPromoList);
				}
			} else {
				promoMap = new HashMap<String,List<PromoVO>>();
				currentPromoList = new ArrayList<PromoVO>();
				currentPromoList.add(promoVO);
				promoMap.put(keyName,currentPromoList);
			}
			
		}
		logDebug("Exit EndecaContentResponseParser.extractPromoList method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "extractPromoList");
		return promoMap;		
	}
	
	
	/**
	 * Helper method used for converting FeaturedItems ContentItem into promo list
	 * @param featuredItems
	 * @param promoMap
	 * @return
	 */
	protected Map<String, List<PromoVO>> extractFeaturedProducts(FeaturedProducts featuredProducts,
			Map<String, List<PromoVO>> promoMap) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "extractFeaturedProducts");
		logDebug("Entering EndecaContentResponseParser.extractFeaturedProducts method");
		Map<String, String> featuredProductMap = new TreeMap<String, String>();
		
		PromoVO promoVO = null;
		
		if(promoMap == null) {
			promoMap = new HashMap<String,List<PromoVO>>();
		} 
		
		//retrieve centercolumn promolist
		List<PromoVO> centerPromoList = promoMap.get(BBBEndecaConstants.FEATURED_PRODUCT_PROMO_LOCATION);
		if(centerPromoList!= null && !centerPromoList.isEmpty()) {
			ListIterator<PromoVO> iter = centerPromoList.listIterator();
			boolean featuredProductsFound = false;
			while(iter.hasNext()) {
				promoVO = iter.next();
				if(promoVO != null && promoVO.getFeaturedProducts() != null) {
					featuredProductMap = promoVO.getFeaturedProducts();	
					//found featuredProductMap for one of the promo
					//featuredproducts will be updated to that same promovo 
					featuredProductsFound = true;
					break;
				}
				
			}
			
			if(null != featuredProducts.getTopRatedRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getTopRatedRecords(),BBBEndecaConstants.TOP_RATED_FEATURED_PRODUCT);	
			}
			if(null != featuredProducts.getNewestRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getNewestRecords(),BBBEndecaConstants.NEWEST_FEATURED_PRODUCT);
			}
			if(null != featuredProducts.getPopularRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getPopularRecords(),BBBEndecaConstants.POPULAR_FEATURED_PRODUCT);
			}
			if(null != featuredProducts.getTrendingRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getTrendingRecords(),BBBEndecaConstants.TRENDING_FEATURED_PRODUCT);
			}
			if(null != featuredProducts.getSponsoredRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getSponsoredRecords(),BBBEndecaConstants.SPONSORED_FEATURE_PRODUCT);
			}
			
			if(featuredProductsFound && promoVO != null) {
				//featuredProductMap is found in one of promoVO of center column
				//promoVO will not be null as featured products are retrieved from this promoVO
				promoVO.setFeaturedProducts(featuredProductMap);
			} else {
				promoVO = new PromoVO();
				promoVO.setFeaturedProducts(featuredProductMap);
				centerPromoList.add(promoVO);
				promoMap.put(BBBEndecaConstants.FEATURED_PRODUCT_PROMO_LOCATION,centerPromoList);
			}
		} else {
			centerPromoList = new ArrayList<PromoVO>();
			
			//creating new map to hold featured products
			if(null != featuredProducts.getTopRatedRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getTopRatedRecords(),BBBEndecaConstants.TOP_RATED_FEATURED_PRODUCT);	
			}
			if(null != featuredProducts.getNewestRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getNewestRecords(),BBBEndecaConstants.NEWEST_FEATURED_PRODUCT);
			}
			if(null != featuredProducts.getPopularRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getPopularRecords(),BBBEndecaConstants.POPULAR_FEATURED_PRODUCT);
			}
			if(null != featuredProducts.getTrendingRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getTrendingRecords(),BBBEndecaConstants.TRENDING_FEATURED_PRODUCT);
			}
			if(null != featuredProducts.getSponsoredRecords()) {
				featuredProductMap = getFeaturedProducts(featuredProductMap,featuredProducts.getSponsoredRecords(),BBBEndecaConstants.SPONSORED_FEATURE_PRODUCT);
			}
			
			//new promoVO object
			promoVO = new PromoVO();
			promoVO.setFeaturedProducts(featuredProductMap);
			centerPromoList.add(promoVO);
			promoMap.put(BBBEndecaConstants.FEATURED_PRODUCT_PROMO_LOCATION,centerPromoList);
		}
		logDebug("Exit EndecaContentResponseParser.extractFeaturedProducts method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "extractFeaturedProducts");
		return promoMap;
	}
	
	/**
	 * parses contentitem and extracts featured product map based on priority
	 * @param featureProductMap
	 * @param featureProperty
	 * @param featureType
	 * @return
	 */
	protected Map<String, String> getFeaturedProducts(Map<String, String> featureProductMap, List<Record> records , String featureType){
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "getFeaturedProducts");
		logDebug("Start Fetching details for Featured Property " + featureType);
		
		if(records != null){
			final ListIterator<Record> iterRecords = records.listIterator();
			
			Record record = null;
			String match = null;
			while (iterRecords.hasNext() && featureProductMap.size() <= 5) {
				record = (Record) iterRecords.next();
				Map<String, Attribute> attributesMap = record.getAttributes();
				match = getFirstAttributeValue(attributesMap,BBBEndecaConstants.FEATURED_PRODUCT_ID); 
				
				//if no match found return
				if (match == null)
					return featureProductMap;
				
				
				for(int j=0; j <= featureProductMap.size(); j++){
					if(featureProductMap.containsKey(match)){
						
						logDebug("Product Id : " + match + ", already exists in Featured Product Map.");
						
						String existingPriority = getConfigUtil().getPriorityFeaturesMap().get(featureProductMap.get(match));
						String newPriority = getConfigUtil().getPriorityFeaturesMap().get(featureType);
						if(Integer.parseInt(existingPriority) > Integer.parseInt(newPriority)){
							
							logDebug("Updating Featured Product Map, Removing Existing Feature : " + featureProductMap.get(match));
							logDebug("Adding New Feature : " + featureType);
							
							featureProductMap.remove(match);
							featureProductMap.put(match, featureType);
						}
					}else{
						featureProductMap.put(match, featureType);
						
						logDebug("Adding New Featured Product Property for Product ID : " + match + " and feature property : " + featureType);
						
					}
				}
				
			}
		}else{
			
			logDebug("No result found for Featured Property : " + featureType);
			
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "getFeaturedProducts");
		return featureProductMap;
	}

	private void setCategoryRefinement(NavigationContainer pNavigationContainer, SearchResults pSearchResults){
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "setCategoryRefinement");
		logDebug("Entering EndecaContentResponseParser.setCategoryRefinement method");
		final String siteId = SiteContextManager.getCurrentSiteId();
		String catId=null;
		List<RefinementMenu> refinements = pNavigationContainer.getNavigation();
		ListIterator<RefinementMenu> refinementListIter = null;
		RefinementMenu refinementMenu = null;
		CategoryVO catVO =null;
		
		if(null != refinements) {
			refinementListIter = refinements.listIterator();
			while(refinementListIter.hasNext()) {
				refinementMenu = (RefinementMenu) refinementListIter.next();
				
				if (refinementMenu instanceof CategoryRefinementMenu){
					if(((CategoryRefinementMenu) refinementMenu).getCatList() != null){
						try {
							//PS-61408 |Checking for phantom category and removing respective L3s
							catId=((CategoryRefinementMenu) refinementMenu).getCatList().getQuery();
							if(null!=catId){
								catVO = getConfigUtil().getCatalogTools().getCategoryDetail(siteId, catId, false);
							}
							if(null !=catVO && !catVO.getPhantomCategory()){
								getConfigUtil().getCatalogTools().removePhantomCategoryCLP(((CategoryRefinementMenu) refinementMenu).getCatList().getCategoryRefinement(), siteId);
								pSearchResults.setCategoryHeader(((CategoryRefinementMenu) refinementMenu).getCatList());
							}else if(getConfigUtil().getCatalogTools().isFirstLevelCategory(catId, siteId)) {
								pSearchResults.setCategoryHeader(((CategoryRefinementMenu) refinementMenu).getCatList());
							} 
							break;
						} catch (BBBSystemException | BBBBusinessException e) {
							logError("Exception in setCategoryRefinement ::" , e);
						}
						
					}else if (((CategoryRefinementMenu) refinementMenu).getFacetParentVOs() != null){
						List<FacetParentVO> list = pSearchResults.getFacets();
						
						if(list != null){
							list.addAll(((CategoryRefinementMenu) refinementMenu).getFacetParentVOs());
						}else{
							pSearchResults.setFacets(((CategoryRefinementMenu) refinementMenu).getFacetParentVOs());
						}

						break;
					}
				}
			}
		}
		logDebug("Exit EndecaContentResponseParser.setCategoryRefinement method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "setCategoryRefinement");
	}

	/**
	 * This method will iterate over all refinements and create FacetParentVO list object with available refinements
	 * @param xmlContentItem
	 * @return
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	protected List<FacetParentVO> convertToFacetListVO(NavigationContainer navigationContainer, SearchQuery pSearchQuery) throws BBBSystemException, BBBBusinessException {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToFacetListVO");
		logDebug("Entering EndecaContentResponseParser.convertToFacetListVO method");
		List<FacetParentVO> facetParentList = new ArrayList<FacetParentVO>();
		
		final Map<String, String> dimensionMap = getConfigUtil().getDimensionMap();
		
		//SiteContextManager.getCurrentSiteId() would return TBS_ when used in TBS EAR 
		final String siteId = pSearchQuery != null ? pSearchQuery.getSiteId() : SiteContextManager.getCurrentSiteId();
		final List<String> catIDstobeSupressed = getConfigUtil().getCatIdsToBeSuppressed(siteId);
		final Collection<String> facets = getConfigUtil().getFacets(siteId);
		String attributes = getConfigUtil().getCatalogTools().getConfigValueByconfigType(getConfigUtil().getDimDisplayMapConfig()).get("Attributes");
		List<String> pList = getConfigUtil().sanitizedAttributeValues(siteId);
		
		Map<String,String> attributeMap=getConfigUtil().getAttributeInfo();
		
		List<RefinementMenu> refinements = navigationContainer.getNavigation();
		ListIterator<RefinementMenu> refinementListIter = null;
		FacetParentVO facetParent = null;
		List<FacetRefinementVO> facetRefinements = null;
		RefinementMenu refinementMenu = null;
		
		if(null != refinements) {
			logDebug("refinements is not null and retrieved from NavigationContainer");
			refinementListIter = refinements.listIterator();
			while(refinementListIter.hasNext()) {
				boolean typeAheadDimension = false;
				refinementMenu = (RefinementMenu) refinementListIter.next();
				
				if (refinementMenu instanceof CategoryRefinementMenu){
					continue;//handled separately.
				} 

				facetParent = new FacetParentVO();
				facetRefinements = new ArrayList<FacetRefinementVO>();
				
				//set dimension name same as refinementMenu's name if not found in dimensionMap
				//this refinement would get ignored while processing in getFacets()
				if(dimensionMap.get(refinementMenu.getDimensionName()) == null || dimensionMap.get(refinementMenu.getDimensionName()).equals("")) {
					//RECORD_TYPE dimension would also fall in this category and need to be added so that it is parsed for search assets
					if(RECORD_TYPE.equalsIgnoreCase(refinementMenu.getDimensionName())) {
						logDebug("refinementMenu.getDimensionName() is equal to RECORD_TYPE"+refinementMenu.getDimensionName());
						facetParent.setName(refinementMenu.getDimensionName());
					} else {
						//avoid showing this dimension in refinements for all other cases
						logDebug("refinementMenu.getDimensionName() is not found in dimensionMap - "+refinementMenu.getDimensionName());
						continue;
					}
				} else {
					facetParent.setName(dimensionMap.get(refinementMenu.getDimensionName()));
				}
				
				//ignore current facet if this is not in facets list defined for site
				if(!facets.contains(facetParent.getName()) && !RECORD_TYPE.equalsIgnoreCase(facetParent.getName())) {
					logDebug("facet is ignored as this facet is not defined in facets list defined for site - "+facetParent.getName());
					continue;
				}
				
				//ignore Brand facet for Brand page
				if(null != pSearchQuery && isBrandPageRequest(pSearchQuery) && BRAND.equalsIgnoreCase(facetParent.getName())) {
					logDebug("Brand facet is ignored for brand page - "+facetParent.getName());
					continue;
				}
				
				logDebug("processing current facet - "+facetParent.getName()+" refinement's name - "+refinementMenu.getDimensionName());
				
				facetParent.setMultiSelect(refinementMenu.isMultiSelect());
				//facetParent.setMultiSelect(true);
				
				if(refinementMenu.getExpandAction() != null) 
					facetParent.setQuery(refinementMenu.getExpandAction().getNavigationState());
				
				
				CustomRefinementMenu customRefinementMenu = null;
				boolean hasCustomBoost = false;
				boolean hasCustomBury = false;
				if(refinementMenu instanceof CustomRefinementMenu) {
					customRefinementMenu = (CustomRefinementMenu)refinementMenu;
					hasCustomBoost = (null != customRefinementMenu.getCustomBoostedDvals() 
							&& customRefinementMenu.getCustomBoostedDvals().size() > 0) ? true : false;
					hasCustomBury = (null != customRefinementMenu.getCustomBuriedDvals() 
							&& customRefinementMenu.getCustomBuriedDvals().size() > 0) ? true : false;
	
					
				}
				FacetRefinementVO facetRefinement = null;
				if(refinementMenu.getRefinements() != null){
						
					for(Refinement refinement : refinementMenu.getRefinements()) {
						facetRefinement = new FacetRefinementVO();
						facetRefinement.setName(refinement.getLabel());
						//dimensionname required for intl attributes & pricing
						facetRefinement.setDimensionName(refinementMenu.getDimensionName());
						if(null != refinement.getCount()) {
						facetRefinement.setSize(Integer.toString(refinement.getCount()));
						}
						
						//Adding sort string to refinement so that search PLP would get sorted after refining
						if(null != pSearchQuery && !BBBUtility.isEmpty(pSearchQuery.getSortString())) {
							facetRefinement.setQuery(BBBEndecaConstants.SORT_FIELD+"="+pSearchQuery.getSortString());
						}
						
						//facetRefinement.setQuery(refinement.getNavigationState());
						/*logDebug("refinement getNavigationState - "+refinement.getNavigationState());
						logDebug("count - "+refinement.getCount()+" label - "+
										refinement.getLabel()+" siterootpath - "+refinement.getSiteRootPath()
										+"properties map - "+refinement.getProperties()+" sitestate - "+refinement.getSiteState()
										+"contentpath - "+refinement.getContentPath());*/
						
						boolean addRefinementValue;
						try {
							addRefinementValue = EndecaSearchUtil.assignFacetRefFilterAndCatalogId(facetRefinement,refinement.getNavigationState(), pSearchQuery);
						} catch(UrlFormatException urlFormatExe) {
							addRefinementValue = false;
						}
						
						//continue incase facet ref filter and catalog id cannot be retrieved
						if(!addRefinementValue) {
							continue;
						}
						
						//add to facetRefinements only as per custom dimension logic
						if(null != customRefinementMenu) {
							
							if(hasCustomBoost) {
								//disable showing if this is not defined in boost list
								if(!customRefinementMenu.getCustomBoostedDvals().contains(facetRefinement.getCatalogId())) {
									addRefinementValue = false;
								}
							} else if(hasCustomBury) {
								//disable showing if this is part of bury list and when boost list is empty
								if(customRefinementMenu.getCustomBuriedDvals().contains(facetRefinement.getCatalogId())) {
									addRefinementValue = false;
								}
							}
						} else {
							addRefinementValue = true;
						}
						
						if(!catIDstobeSupressed.contains(facetRefinement.getCatalogId())) {
							if(facetParent.getName().equals(attributes) && null != pList && !pList.isEmpty()) {
								if(pList.contains(facetRefinement.getName().toLowerCase())){
									facetRefinement.setIntlFlag(BBBCoreConstants.YES_CHAR);
									for (Map.Entry<String,String> entry : attributeMap.entrySet()) {
										if(entry.getKey().contains(facetRefinement.getName())){
											facetRefinement.setIntlFlag(entry.getValue());
									    }
									}
									if(addRefinementValue) {
										logDebug("processing current facetRefinement - "+facetRefinement.getName());
										facetRefinements.add(facetRefinement);
									}
								} 
							} else {
								 if(addRefinementValue) {
									 logDebug("facet is not Attributes. processing current facetRefinement - "+facetRefinement.getName());
									 facetRefinements.add(facetRefinement);
								 }
							}
						}
						
					}
					facetParent.setFacetRefinement(facetRefinements);
				}

				if(null != facetParent && null!= facetParent.getFacetRefinement() && !facetParent.getFacetRefinement().isEmpty()) {
					facetParentList.add(facetParent);	
				}
			}
		}
		logDebug("Exit EndecaContentResponseParser.convertToFacetListVO method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToFacetListVO");
		return facetParentList;
	}

	
	/**
	 * This method will iterate over breadcrumbs and converts to descriptor vo
	 * @param xmlContentItem
	 * @return
	 * @throws BBBSystemException 
	 */
	protected List<CurrentDescriptorVO> convertToDescriptorListVO(Breadcrumbs breadcrumbs, SearchQuery pSearchQuery) throws BBBSystemException {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToDescriptorListVO");
		logDebug("Entering EndecaContentResponseParser.convertToDescriptorListVO method");
		final Map<String, String> dimensionMap = getConfigUtil().getDimensionMap();
		final String siteId = SiteContextManager.getCurrentSiteId();
		final Collection<String> facets = getConfigUtil().getFacets(siteId);
		
		List<CurrentDescriptorVO> descriptorList = new ArrayList<CurrentDescriptorVO>();
		CurrentDescriptorVO currentDescriptor = null;
		
		for(RefinementBreadcrumb refinementBreadcrumb : breadcrumbs.getRefinementCrumbs()) {
			
			//ignore showing site id flag in descriptor list
			if(refinementBreadcrumb.getDimensionName().equalsIgnoreCase(SITE_ID)) {
				logDebug("Ignoring Site_ID dimension");
				continue;
			}
			
			//ignore Brand descriptor for Brand page
			if(null != pSearchQuery && isBrandPageRequest(pSearchQuery) && BRAND.equalsIgnoreCase(dimensionMap.get(refinementBreadcrumb.getDimensionName())) ) {
				logDebug("Ignoring Brand dimension for brand plp");
				continue;
			}
			
			//continue populating descriptors for record type or if current refinement exists in facets configured per site
			if(facets.contains(dimensionMap.get(refinementBreadcrumb.getDimensionName()))
					|| refinementBreadcrumb.getDimensionName().equalsIgnoreCase(RECORD_TYPE)) {
				currentDescriptor = new CurrentDescriptorVO();
				currentDescriptor.setRootName(dimensionMap.get(refinementBreadcrumb.getDimensionName()));
				currentDescriptor.setName(refinementBreadcrumb.getLabel());
				logDebug("Processing currentDescriptor - "+currentDescriptor.getName());
				if(dimensionMap.get(refinementBreadcrumb.getDimensionName()).equalsIgnoreCase(DEPARTMENT)) {
					List<Ancestor> ancestors = refinementBreadcrumb.getAncestors();
					if(null != ancestors) {
						for(Ancestor ancestor : ancestors) {
							if(!BBBUtility.isEmpty(ancestor.getLabel())) {
								currentDescriptor.setAncestorName(ancestor.getLabel());
								//continue iterating over ancestors so that last level is assigned as ancestor
							}
						}
					}
				}
				
				String removalNavStateAsString = refinementBreadcrumb.getRemoveAction().getNavigationState().toString().substring(1);
				
				try {
					String nValue = EndecaSearchUtil.getParameter(removalNavStateAsString,NAVIGATION);
					if(!BBBUtility.isEmpty(nValue)) {
						currentDescriptor.setDescriptorFilter(nValue.replaceAll(" ","-"));
						String catalogId = EndecaSearchUtil.getCatalogIdUsingRequestQuery(nValue,true, pSearchQuery);
						if(!BBBUtility.isEmpty(catalogId))
							currentDescriptor.setCategoryId(catalogId);
					}
				} catch(UrlFormatException urlFormatExe) {
					logDebug("urlFormatExe while trying to get N value from nav state - "+urlFormatExe.getMessage());
				}
				
				//Adding sort string to refinement so that PLP would still be sorted after clicking on X in blue pills
				if(null != pSearchQuery && !BBBUtility.isEmpty(pSearchQuery.getSortString())) {
					currentDescriptor.setRemovalQuery(BBBEndecaConstants.SORT_FIELD+"="+pSearchQuery.getSortString());
				} else {
					currentDescriptor.setRemovalQuery("");
				}
				
				//currentDescriptor.setRemovalQuery(refinementBreadcrumb.getRemoveAction().getNavigationState().toString().substring(1));
				//currentDescriptor.setCategoryId(refinementBreadcrumb.get);
				//nParam = ENEQueryToolkit.removeDescriptor(navigation, dimValDescriptor).toString();
				//currentDescriptor.setRemovalQuery(getEndecaClient().createRemoveDescriptorQuery(nParam,queryParamForFacets,pSearchQuery));
				// Added for R2.2 SEO friendly Story : Start
				//currentDescriptor.setDescriptorFilter(refinementBreadcrumb.replaceAll(" ", "-"));
				descriptorList.add(currentDescriptor);
			}
		}
		logDebug("Exit EndecaContentResponseParser.convertToDescriptorListVO method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToDescriptorListVO");
		return descriptorList.isEmpty() ? null : descriptorList;
	}


	/**
	 * Method to convert search keywords from breadcrumb into autosuggestvo 
	 * and extract search key, terms, matchmode & corrected terms 
	 * @param pContentItem
	 * @return
	 */
	protected List<AutoSuggestVO> convertToAutoSuggestVO(Breadcrumbs breadcrumbs,List<AutoSuggestVO> pList) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToAutoSuggestVO");
		logDebug("Entering EndecaContentResponseParser.convertToAutoSuggestVO(breadcrumbs,...) method");
		if(pList == null) {
			pList = new ArrayList<AutoSuggestVO>();
		}
		AutoSuggestVO autoSuggestVO = null;
		
		List<SearchBreadcrumb> searchBreadcrumbs = breadcrumbs.getSearchCrumbs();
		for(SearchBreadcrumb searchBreadcrumb : searchBreadcrumbs) {
			logDebug("processing searchBreadcumb.getTerms() - "+searchBreadcrumb.getTerms());
			boolean autoSuggestFound = false;
			for(AutoSuggestVO autoSuggest : pList) {
				if(autoSuggest.getSearchTerms().equals(searchBreadcrumb.getTerms())) {
					autoSuggest.setSearchTerms(searchBreadcrumb.getTerms());
					autoSuggest.setSearchMode(searchBreadcrumb.getMatchMode().name());
					autoSuggest.setSpellCorrection(searchBreadcrumb.getCorrectedTerms());
					autoSuggestFound = true;
				}
			}
			if(!autoSuggestFound) {
				autoSuggestVO = new AutoSuggestVO();
				autoSuggestVO.setSearchKey(searchBreadcrumb.getKey());
				autoSuggestVO.setSearchTerms(searchBreadcrumb.getTerms());
				autoSuggestVO.setSearchMode(searchBreadcrumb.getMatchMode().name());
				autoSuggestVO.setSpellCorrection(searchBreadcrumb.getCorrectedTerms());
				//autoSuggestVO.setDymSuggestion(searchBreadcrumb.getCorrectedTerms());
				//autoSuggestVO.isAutoPhrase()
				pList.add(autoSuggestVO);
			}
		}
		logDebug("Exit EndecaContentResponseParser.convertToAutoSuggestVO(breadcrumbs,...) method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToAutoSuggestVO");
		return pList;
		
	}
	
	
	/**
	 * Method to convert search keywords from breadcrumb into autosuggestvo 
	 * and extract search key, terms, matchmode & corrected terms 
	 * @param pContentItem
	 * @return
	 */
	protected List<AutoSuggestVO> convertToAutoSuggestVO(SearchAdjustments searchAdjustments,List<AutoSuggestVO> pList) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToAutoSuggestVO");
		logDebug("Entering EndecaContentResponseParser.convertToAutoSuggestVO(searchAdjustments,...) method");
		if(pList == null) {
			pList = new ArrayList<AutoSuggestVO>();
		}
		AutoSuggestVO autoSuggestVO = null;
		
		List<String> originalTerms = searchAdjustments.getOriginalTerms();
		if(null != originalTerms && !originalTerms.isEmpty()) {
			for(String key : originalTerms) {
				logDebug("processing key - "+key);
				boolean searchKeyFound = false;
				autoSuggestVO = new AutoSuggestVO();
				for(AutoSuggestVO autoSuggest : pList) {
					if(autoSuggest.getSearchTerms() !=null && autoSuggest.getSearchTerms().equals(key)) {
						searchKeyFound = true;
						autoSuggest.setSpellCorrection(getAdjustedTerms(searchAdjustments,key));
						autoSuggest.setDymSuggestion(getDYMTerms(searchAdjustments,key));
						autoSuggest.setAutoPhrase(isAutoPhrased(searchAdjustments,key));
					}
				}
				if(!searchKeyFound) {
					autoSuggestVO.setSearchKey(key);
					autoSuggestVO.setSearchTerms(key);
					autoSuggestVO.setSpellCorrection(getAdjustedTerms(searchAdjustments,key));
					autoSuggestVO.setDymSuggestion(getDYMTerms(searchAdjustments,key));
					autoSuggestVO.setAutoPhrase(isAutoPhrased(searchAdjustments,key));
					
					pList.add(autoSuggestVO);
				}
				
			}
		}
		logDebug("Exit EndecaContentResponseParser.convertToAutoSuggestVO(searchAdjustments,...) method");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToAutoSuggestVO");
		return pList;
	}


	/**
	 * Method used to check if terms are adjusted
	 * @param searchAdjustments
	 * @param key
	 * @return
	 */
	private String getAdjustedTerms(SearchAdjustments searchAdjustments,
			String key) {
		Map<String, List<AdjustedSearch>> adjustedSearches = searchAdjustments.getAdjustedSearches();
		
		if(null != adjustedSearches && adjustedSearches.containsKey(key) && null != adjustedSearches.get(key)) {
			for(AdjustedSearch adjustedSearch : adjustedSearches.get(key)) {
				return adjustedSearch.getAdjustedTerms();
			}
		}
		
		return null;
	}


	/**
	 * Method used to get DYM variation for current key
	 * @param searchAdjustments
	 * @param key
	 * @return
	 */
	private String getDYMTerms(SearchAdjustments searchAdjustments, String key) {
		Map<String, List<SuggestedSearch>> suggestedSearches = searchAdjustments.getSuggestedSearches();
		if(null != suggestedSearches && suggestedSearches.containsKey(key) && null != suggestedSearches.get(key)) {
			for(SuggestedSearch suggestedSearch : suggestedSearches.get(key)) {
				try {
					return EndecaSearchUtil.getParameter(suggestedSearch.getNavigationState(), BBBEndecaConstants.NAV_KEYWORD);
				} catch (UrlFormatException urlFormatExe) {
					logDebug("urlFormatExe while trying to retrieve DYM suggestions from nav state - "+urlFormatExe.getMessage());
					return null;
				}
			}
		}
		return null;
	}


	/**
	 * Method used to check if current key is autophrased;
	 * @param searchAdjustments
	 * @param key
	 * @return
	 */
	private boolean isAutoPhrased(SearchAdjustments searchAdjustments,
			String key) {
		Map<String, List<AdjustedSearch>> adjustedSearches = searchAdjustments.getAdjustedSearches();
		
		if(null != adjustedSearches && adjustedSearches.containsKey(key) && null != adjustedSearches.get(key)) {
			for(AdjustedSearch adjustedSearch : adjustedSearches.get(key)) {
				if(adjustedSearch.isAutoPhrased()) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * This method will convert ResultsList object from content item to BBBProductList VO object
	 * 
	 * @param resultsList
	 * @return
	 */
	protected BBBProductList convertToBBBProductListVO(ResultsList resultsList) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToBBBProductListVO");
		logDebug("Entering EndecaContentResponseParser.convertToBBBProductListVO method");
		final BBBProductList bbbProducts = new BBBProductList();
		BBBProduct bbbOthResult = null;
		BBBProduct bbbProduct = null;
		
		long productCount = 0L;
		final Map<String, List<BBBProduct>> otherResults = new HashMap<String, List<BBBProduct>>();
		String scene7Path = getConfigUtil().getScene7Path();
		
		//scene7Path is overwritten from catalogtools if exists
		try {
			scene7Path = getConfigUtil().getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL,BBBCoreConstants.SCENE_SEVEN_DOMAIN_PATH).get(0);
		} catch (BBBSystemException se) {
			logError("Exception thrown in find the config key for Scene7domainpath:"+se.getMessage());
		} catch (BBBBusinessException be) {
			logError("Exception thrown in find the config key for Scene7domainpath:"+be.getMessage());
		}
		
		String higherShippingThreshhold = ((BBBConfigToolsImpl)getEndecaSearch().getCatalogTools()).getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, null, null,SiteContextManager.getCurrentSiteId());
		List<String> ltlAttributesList = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST);
		List<String> shippingAttributesList = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.SHIPPING_ATTRIBUTES_LIST);
		
		
		boolean dynamicPricingEnabled=false;
		String dynamicPricingEnabledStr = null;
		dynamicPricingEnabledStr = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
				 BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY);
		if(dynamicPricingEnabledStr!=null ){
			dynamicPricingEnabled = Boolean.parseBoolean(dynamicPricingEnabledStr);
		}

			
			
		/*try {
		 * 
			scene7Path = getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL,BBBCoreConstants.SCENE_SEVEN_DOMAIN_PATH).get(0);
		} catch (BBBSystemException se) {
			logError("Exception thrown in find the config key for Scene7domainpath:"+se.getMessage());
		} catch (BBBBusinessException be) {
			logError("Exception thrown in find the config key for Scene7domainpath:"+be.getMessage());
		}
		*/
		
		//adding store count for plp
		if(resultsList.containsKey(BBBEndecaConstants.NEGATIVEMATCHQUERY)){
			Object  negativeMatchQuery =  resultsList.get(BBBEndecaConstants.NEGATIVEMATCHQUERY);
			 String negativeMatchQueryString = String.valueOf(negativeMatchQuery);
			bbbProducts.setNegativeMatchQuery(negativeMatchQueryString);
		}
		
		if(resultsList.containsKey(BBBEndecaConstants.ONLINE_PRODUCT_COUNT)){
			Object  onlineProdCount =  resultsList.get(BBBEndecaConstants.ONLINE_PRODUCT_COUNT);
			 Long onlineProdInveCount = Long.parseLong(String.valueOf(onlineProdCount));
			bbbProducts.setOnlineProductInvCount(onlineProdInveCount);
		}
		
		// Obtain total result count
		productCount = resultsList.getTotalNumRecs();
		bbbProducts.setBBBProductCount(productCount);
		bbbProducts.setRecordOffset(resultsList.getFirstRecNum());
		
		Map<String, String> propertyMap = getConfigUtil().getPropertyMap();
		Map<String, String> attributePropMap = getConfigUtil().getAttributePropmap();
		Map<String, String> swatchInfoMap = getConfigUtil().getSwatchInfoMap();
		
		List<Record> recordList = resultsList.getRecords();
		Iterator<Record> iterRecords = recordList.iterator();
		SortedMap<Integer,String> attr = null;
		Map<String,SkuVO> pSkuSet = null;
		List<SkuVO> skuVOList= null;
		List<String> categoryList = null;
		String productID = null;
		/*boolean isColor=false;
		boolean isColorSize=false;*/
		while (iterRecords.hasNext()) {
			Record record = (Record)iterRecords.next();
			Map<String, Attribute> attributesMap = record.getAttributes();
			/*Set<String> keys = attributesMap.keySet();
			Iterator<String> keyIter = keys.iterator();
			while(keyIter.hasNext()) {
				String key = keyIter.next();
				Attribute attributeValueList = attributesMap.get(key);
				
			}*/
			//Attribute.toString() returns first value as a String
			productID = getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_ID"));
			bbbProduct = new BBBProduct();
			categoryList = new ArrayList<String>();
			// Map the Endeca properties to corresponding properties of Results VO.
			bbbProduct.setProductID(productID);
			bbbProduct.setProductName(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_TITLE")));
			//bbbProduct.setCategory((String) properties.get(getPropertyMap().get("PRODUCT_CATEGORY_ID")));

			/*if("Y".equalsIgnoreCase((String)properties.get(getPropertyMap().get("PRODUCT_SWATCH_FLAG")))){ bbbProduct.setSwatchFlag("1");}
			else{ bbbProduct.setSwatchFlag("0");}*/
			bbbProduct.setSwatchFlag(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_SWATCH_FLAG")));
			//System.out.println("Swatch flag: " + bbbProduct.getSwatchFlag());

			
			bbbProduct.setHighPriceMX((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_HIGH_PRICE_MX")));
			bbbProduct.setLowPriceMX((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_LOW_PRICE_MX")));
			bbbProduct.setWasLowPriceMX((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_WAS_LOW_PRICE_MX")));
			bbbProduct.setWasHighPriceMX((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_WAS_HIGH_PRICE_MX")));
			
			bbbProduct.setPriceRangeMX((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_PRICE_RANGE_MX")));
			bbbProduct.setWasPriceRangeMX((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_WAS_PRICE_RANGE_MX")));

			bbbProduct.setHighPrice((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_HIGH_PRICE")));
			bbbProduct.setLowPrice((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_LOW_PRICE")));
			bbbProduct.setWasLowPrice((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_WAS_LOW_PRICE")));
			bbbProduct.setWasHighPrice((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_WAS_HIGH_PRICE")));
			bbbProduct.setPriceRange((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_PRICE_RANGE")));
			bbbProduct.setWasPriceRange((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_WAS_PRICE_RANGE")));
			bbbProduct.setPriceRangeToken((String) getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_PRICE_RANGE_TOKEN")));
			if(getFirstAttributeValue(attributesMap,propertyMap.get("International_Restricted"))!=null){
				bbbProduct.setIntlRestricted(BBBCoreConstants.YES_CHAR.equalsIgnoreCase((String)getFirstAttributeValue(attributesMap,propertyMap.get("International_Restricted"))));
			}

			//System.out.println("get Product List () with Product Id: " + productID + " & pColor = " + pColor +" & Swatch Flag = " + bbbProduct.getSwatchFlag() );
			bbbProduct.setImageURL(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_IMAGE")));
			getEndecaSearch().getSearchUtil().setProductImageURLForDifferentView(bbbProduct);
			// Set the vertical Image URL for the product
			bbbProduct.setVerticalImageURL(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_VERTICAL_IMAGE")));
			//System.out.println("Image URL:" + bbbProduct.getImageURL());

			bbbProduct.setReviews(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_REVIEWS")));
			bbbProduct.setRatings(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_RATINGS")));
			if (NumberUtils.isNumber(bbbProduct.getRatings())) {
				Integer rating = (int) (Double.valueOf(bbbProduct.getRatings()) * BBBCoreConstants.TEN);
				bbbProduct.setRatingForCSS(rating);
			}
			bbbProduct.setDescription(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_DESC")));
			bbbProduct.setVideoId(getFirstAttributeValue(attributesMap,propertyMap.get("VIDEO_ID")));
			bbbProduct.setGuideId(getFirstAttributeValue(attributesMap,propertyMap.get("GUIDE_ID")));
			bbbProduct.setGuideTitle(getFirstAttributeValue(attributesMap,propertyMap.get("GUIDE_TITLE")));
			bbbProduct.setGuideImage(getFirstAttributeValue(attributesMap,propertyMap.get("GUIDE_IMAGE")));
			bbbProduct.setGuideAltText(getFirstAttributeValue(attributesMap,propertyMap.get("GUIDE_ALT_TEXT")));
			bbbProduct.setGuideShortDesc(getFirstAttributeValue(attributesMap,propertyMap.get("GUIDE_SHORT_DESC")));
			bbbProduct.setSeoUrl(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_SEO_URL")));

			//DynamicPricingVO
			if(dynamicPricingEnabled){
				logDebug("Dynamic pricing is enabled setting dynamicPriceVO");
				BBBDynamicPriceVO dynamicPriceVO = new BBBDynamicPriceVO();
				getConfigUtil().getCatalogTools().getDynamicPriceDetails(dynamicPriceVO, productID);
				bbbProduct.setDynamicPriceVO(dynamicPriceVO);
				logDebug("bbbProduct.setDynamicPriceVO done");
			}
			
			int scene7Value;
			try {
				logDebug("bbbProduct.getProductID() - "+bbbProduct.getProductID());
				
				scene7Value = Integer.parseInt(bbbProduct.getProductID()) % 10;
				
				logDebug("scene7Path - "+scene7Path);
				
				if (null != scene7Path) {
					if (scene7Value % 2 == 0) {
						bbbProduct.setSceneSevenURL(scene7Path.replaceFirst(BBBCoreConstants.STRING_X, BBBCoreConstants.STRING_ONE));
					} else {
						bbbProduct.setSceneSevenURL(scene7Path.replaceFirst(BBBCoreConstants.STRING_X, BBBCoreConstants.STRING_TWO));
					}
				}
			
			} catch (NumberFormatException nfe) {
				logError("Exception thrown in scene7Value "+nfe.getMessage());
			}
			
			if(BBBUtility.isNotEmpty(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_ON_SALE")))){
				bbbProduct.setOnSale(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_ON_SALE")));
			}

			logDebug("SEO Url for "+bbbProduct.getProductID()+" is "+bbbProduct.getSeoUrl());

			bbbProduct.setCollectionFlag(getFirstAttributeValue(attributesMap,propertyMap.get("COLLECTION_PRODUCT")));

			// added for R2-item 141
			if( ("04").equalsIgnoreCase(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_ROLLUP_TYPE_CODE")))
            	|| ("05").equalsIgnoreCase(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_ROLLUP_TYPE_CODE"))) ){
            	bbbProduct.setRollupFlag(true);
            }
			
			if( !BBBUtility.isEmpty(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_ROLLUP_TYPE_CODE")))
	            	&& !("00").equalsIgnoreCase(getFirstAttributeValue(attributesMap,propertyMap.get("PRODUCT_ROLLUP_TYPE_CODE"))) ){
	            	bbbProduct.setMswpFlag(true);
	            }
			
			if( ("1").equalsIgnoreCase(getFirstAttributeValue(attributesMap,propertyMap.get("VDC_Flag"))) ){
	            	bbbProduct.setVdcFlag(true);
	            }

			String otherCat = getFirstAttributeValue(attributesMap,propertyMap.get("OTHER_RESULT_CATEGORY"));
			String otherTitle = getFirstAttributeValue(attributesMap,propertyMap.get("OTHER_RESULT_TITLE"));
			String otherLink = getFirstAttributeValue(attributesMap,propertyMap.get("OTHER_RESULT_LINK"));
			String otherPageType = getFirstAttributeValue(attributesMap,propertyMap.get("OTHER_RESULT_PAGE_TYPE"));

			if(BBBUtility.isNotEmpty(otherCat)){
				if(otherResults.containsKey(otherCat)){
					bbbOthResult = new BBBProduct();
					bbbOthResult.setProductID(productID);
					bbbOthResult.setOthResLink(otherLink);
					bbbOthResult.setOthResTitle(otherTitle);
					bbbOthResult.setOtherPageType(otherPageType);
					otherResults.get(otherCat).add(bbbOthResult);
				}
				else if(!otherResults.containsKey(otherCat)){
					bbbOthResult = new BBBProduct();
					List<BBBProduct> othResList = new ArrayList<BBBProduct>();
					bbbOthResult.setProductID(productID);
					bbbOthResult.setOthResLink(otherLink);
					bbbOthResult.setOthResTitle(otherTitle);
					bbbOthResult.setOtherPageType(otherPageType);
					othResList.add(bbbOthResult);
					otherResults.put(otherCat, othResList);
				}
			}
			// Iterate through Multi valued property for single Result Item.
			Set<String> keys = attributesMap.keySet();
			Iterator<String> keyIter = keys.iterator();
			
			attr =new TreeMap<Integer,String>();
			pSkuSet = new HashMap<String,SkuVO>();
			SkuVO pSkuVO = null;
			skuVOList = new ArrayList<SkuVO>();
			// BBBSL-2187 : To Display Swatch in alphabetical order
			Map<String,SkuVO> colorSkuSet = new TreeMap<String, SkuVO>();
			boolean freeShippingBadge = true;
			List<AttributeVO> attributeListVO = new ArrayList<AttributeVO>();
			while(keyIter.hasNext()) {
				String key = keyIter.next();
				if(propertyMap.get("PRODUCT_ATTR").equalsIgnoreCase(key)) {
					Attribute<String> attributeValueList = attributesMap.get(key);
					for(String eachAttributeVaue : attributeValueList) {
						try {
							JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(eachAttributeVaue.toString());
			            	final DynaBean jSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
			        		DynaClass dynaClass = jSONResultbean.getDynaClass();
			        		DynaProperty dynaProp[] = dynaClass.getDynaProperties();
			        		List<String> propertyNames = new ArrayList<String>();
			        		for (int i = 0; i < dynaProp.length; i++) {
			        			String name = dynaProp[i].getName();
			        			propertyNames.add(name);
			        		}
			        		if(propertyNames.contains(attributePropMap.get("PRIORITY")) && propertyNames.contains(attributePropMap.get("PLACEHOLDER")) && propertyNames.contains(attributePropMap.get("DISPDESC")) && propertyNames.contains(attributePropMap.get("SKU_ATTRIBUTE_ID"))){
			        			String placeholder = (String) jSONResultbean.get(attributePropMap.get("PLACEHOLDER"));
			        			if(!("".equals(jSONResultbean.get(attributePropMap.get("PRIORITY")))) && placeholder.indexOf(getConfigUtil().getPlaceHolder())>=0 ){
			        				AttributeVO attrVo =  new AttributeVO();
			        				String dispDesc = (String)jSONResultbean.get(attributePropMap.get("DISPDESC"));
			        				String skuAttribute = (String)jSONResultbean.get(attributePropMap.get("SKU_ATTRIBUTE_ID"));
			        				 if ((shippingAttributesList != null) && !shippingAttributesList.isEmpty()) {
			                             final String shippingAttributes[] = shippingAttributesList.get(0).split(BBBCoreConstants.COMMA);
			                             for (final String shippingAttributeKey : shippingAttributes) {
			                                 if (skuAttribute.contains(shippingAttributeKey)) {
			                                	 freeShippingBadge = false;
			                                     break;
			                                 }
			                             }
			                         }
			        				 if(ltlAttributesList.contains(skuAttribute)){
				        					freeShippingBadge = false;
			        				 }
			        				if(dispDesc.contains(BBBCoreConstants.PERSONALIZATION_ATTR)) {
			        					bbbProduct.setPersonalized(true);
			        				}
			        				
			        				//String intlProdAttr = "true";
			        				int priority = Integer.parseInt((String)jSONResultbean.get(attributePropMap.get("PRIORITY")));
			        				attr.put(priority, (String)jSONResultbean.get(attributePropMap.get("DISPDESC")));
			        				attrVo.setPriority(priority);
			        				// this code will be uncommented once we will start getting ISINTL flag from endeca
			        				if(propertyNames.contains(attributePropMap.get("International_Restricted")) && jSONResultbean.get(attributePropMap.get("International_Restricted"))!=null){
			        					String intlProdAttr = (String)jSONResultbean.get(attributePropMap.get("International_Restricted"));
			        					if(intlProdAttr.equalsIgnoreCase("Y")) {
				        					intlProdAttr="true";
				        					attrVo.setIntlProdAttr(intlProdAttr);

				        				} else {
				        					intlProdAttr="false";
				        					attrVo.setIntlProdAttr(intlProdAttr);
				        				}
			        					logDebug("Endeca Content Response Parser intlProdAttr |||||||||||||||| value is :"+dispDesc+"-----"+intlProdAttr);
			        				}
			        				
			        				attrVo.setSkuAttributeId(skuAttribute);
			        				attrVo.setAttributeDescrip(dispDesc);
			        				attributeListVO.add(attrVo);
			        			}
			        		}
						} catch(JSONException je) {
							//ignore exception and current property for this product
							logDebug("JSONException while trying to retrieve PRODUCT_ATTR from Endeca Response" + je.getMessage());
						}
					}
					
				}
				
	            bbbProduct.setAttribute(attr);
				
				// Product Swatch Image Info property.
	            if( propertyMap.get("PRODUCT_CHILD_PRODUCT").equalsIgnoreCase(key)){
	            	Attribute<String> attributeValueList = attributesMap.get(key);
	            	for(String eachAttributeVaue : attributeValueList) {
	            		try {
	            			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(eachAttributeVaue);
			        		final DynaBean jSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
			        		DynaClass dynaClass = jSONResultbean.getDynaClass();
			        		DynaProperty dynaProp[] = dynaClass.getDynaProperties();
			        		List<String> propertyNames = new ArrayList<String>();
			        		for (int i = 0; i < dynaProp.length; i++) {
			        			String name = dynaProp[i].getName();
			        			propertyNames.add(name);
			        		}
			        		if(propertyNames.contains(swatchInfoMap.get("SKU_ID"))){
			        			pSkuVO = new SkuVO();
			        			pSkuVO.setSkuID((String) jSONResultbean.get(swatchInfoMap.get("SKU_ID")));
			        			if(propertyNames.contains(swatchInfoMap.get("SWATCH_IMAGE"))){
			        				pSkuVO.setSkuSwatchImageURL((String) jSONResultbean.get(swatchInfoMap.get("SWATCH_IMAGE")));
			        			}
			        			if(propertyNames.contains(swatchInfoMap.get("PRODUCT_IMAGE"))){
			        				pSkuVO.setSkuMedImageURL((String) jSONResultbean.get(swatchInfoMap.get("PRODUCT_IMAGE")));
			        				getEndecaSearch().getSearchUtil().setSkuImageURLForDifferentView(pSkuVO);
			        			}
			        			if(propertyNames.contains(swatchInfoMap.get("COLOR"))){
			        				pSkuVO.setColor((String) jSONResultbean.get(swatchInfoMap.get("COLOR")));
			        				/*// added for R2-item 141
			        				isColor=true;*/
			        			}
			        			if(propertyNames.contains(swatchInfoMap.get("GROUP"))){
			        				pSkuVO.setColorGroup((String) jSONResultbean.get(swatchInfoMap.get("GROUP")));
			        			}
			        			if(propertyNames.contains(swatchInfoMap.get("PRODUCT_VERTICAL_IMAGE"))){
			        				pSkuVO.setSkuVerticalImageURL((String) jSONResultbean.get(swatchInfoMap.get("PRODUCT_VERTICAL_IMAGE")));
			        			}

			        			/*// added for R2-item 141
			        			if(isColor){
			        				if(propertyNames.contains(getSwatchInfoMap().get("SKU_SIZE"))){
				        				 isColorSize=true;
				        			}
			        			}*/
			        			skuVOList.add(pSkuVO);
			        			pSkuSet.put(pSkuVO.getSkuID(), pSkuVO);
			        		}
	            		} catch(JSONException je) {
	            			//ignore exception and current property for this product
	            			logDebug("JSONException while trying to retrieve PRODUCT_CHILD_PRODUCT from Endeca Response" + je.getMessage());
	            		}
	            	}
	            	
	            	
	            	
				}
				
	            bbbProduct.setSkuSet(pSkuSet);
			
	            /*if(isColorSize){
	            	bbbProduct.setRollupFlag(true);
	            }*/
        			
	            for(SkuVO skuVO : skuVOList){
	            	if(null != skuVO.getColor() && !colorSkuSet.containsKey(skuVO.getColor())){
	            			colorSkuSet.put(skuVO.getColor(), skuVO);
	            	}
	            }
	            bbbProduct.setColorSet(colorSkuSet);
	            /* GS-192 Setting List with Categories tagged to a Product. */
	            if( (propertyMap.get("PRODUCT_CATEGORY_ID")).equalsIgnoreCase(key)) {
	            	categoryList.add(getFirstAttributeValue(attributesMap, propertyMap.get("PRODUCT_CATEGORY_ID")));
	            }
	        }
			
			// BBBH-220 - ship message display changes
			if(freeShippingBadge){
				getEndecaSearch().updateShippingMessageFlag(bbbProduct, higherShippingThreshhold);
        	}
			bbbProduct.setAttributeVO(attributeListVO);

			//System.out.println("Final iamge URl set to: "+ bbbProduct.getImageURL());
	        // Adding the Populated Product Item into Product List VO.
			bbbProduct.setCategoryList(categoryList);
	        bbbProducts.getBBBProducts().add(bbbProduct);
	    }
		bbbProducts.setOtherResults(otherResults);
	
		logDebug("Exit EndecaContentResponseParser.convertToBBBProductListVO method. bbbProducts count =" + bbbProducts.getBBBProductCount());
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "convertToBBBProductListVO");
		return bbbProducts;
	}


	public String getFirstAttributeValue(Map<String, Attribute> attributesMap,
			String string) {
		Attribute attributeValues = attributesMap.get(string);
		if(attributeValues != null) {
			return attributeValues.toString();
		}
		return null;
	}
	
	/**
	 * 
	 * @param contentResponseVO
	 */
	private void createPlaceholderPromoMapWhenMissing(
			SearchResults searchResults) {
		Map<String, List<PromoVO>> promoMap = searchResults.getPromoMap();
		
		if(promoMap == null) {
			promoMap = new HashMap<String,List<PromoVO>>();
			searchResults.setPromoMap(promoMap);
		}
		
		for(String key : getConfigUtil().getCatridgeNameMap().keySet()) {
			if(null == promoMap.get(key.toUpperCase(Locale.getDefault()))) {
				promoMap.put(key.toUpperCase(Locale.getDefault()), new ArrayList<PromoVO>());
			}
		}
	}

	/**
	 * @return the configUtil
	 */
	public EndecaConfigUtil getConfigUtil() {
		return configUtil;
	}


	/**
	 * @param configUtil the configUtil to set
	 */
	public void setConfigUtil(EndecaConfigUtil configUtil) {
		this.configUtil = configUtil;
	}
	
	
	/**
	 * This method is to get search tabs to be shown in search plp
	 * and will retrieve the values from content response
	 * @param pContentResponseVO
	 * @param queryString
	 * @param navigationRefine
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Map<String, Asset> getSearchTabs(final List<FacetParentVO> facetParentList) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "getSearchTabs");
		logDebug("Entering EndecaContentResponseParser.getSearchTabs method.");
		
		ListIterator facetParentIter = null;
		FacetParentVO facetParent = null;
		final Map<String, Asset> recTypeParent = new HashMap<String, Asset>();
		List<FacetRefinementVO> facetRefinements = null;
		ListIterator facetRefinementIter = null;
		FacetRefinementVO facetRefinement = null;
		Asset asset = null;
		
		FacetParentVO facetToRemove = null;
		
		facetParentIter = facetParentList.listIterator();
		
		while(facetParentIter.hasNext()) {
			facetParent = (FacetParentVO) facetParentIter.next();
			if(null != facetParent && (BBBEndecaConstants.RECORD_TYPE).equalsIgnoreCase(facetParent.getName())){
				facetToRemove = facetParent;
				facetRefinements = facetParent.getFacetRefinement();
				facetRefinementIter = facetRefinements.listIterator();
				while(facetRefinementIter.hasNext()) {
					facetRefinement = (FacetRefinementVO) facetRefinementIter.next();
					asset = new Asset();
					asset.setQuery(facetRefinement.getQuery());
					// Added for R2.2 SEO friendly Story : Start
					asset.setAssetFilter(facetRefinement.getCatalogId());
					// Added for R2.2 SEO friendly Story : End
					try{
						asset.setCount(Integer.valueOf(facetRefinement.getSize()));	
						String assetName = getConfigUtil().getRecordTypeNames().get(facetRefinement.getName());
						if(null != assetName) {
						recTypeParent.put(assetName,asset);
						}
					} catch(NumberFormatException nfr) {
						logError("unable to fetch count of records tagged to this record type. ignoring this value - "+facetRefinement.getName());
					}
				}
				break;
			}
			
		}
		
		//remove RECORD_TYPE from facet list as this is no longer required
		if(null != facetToRemove) {
			facetParentList.remove(facetToRemove);
			//remove null item from facetParentList
			facetParentList.removeAll(Collections.singleton(null));
		}

		logDebug("Exit EndecaContentResponseParser.getSearchTabs method.");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "getSearchTabs");
		return recTypeParent;
	}


	/**
	 * Method to iterate over descriptors to find current category name
	 * @param descriptors
	 * @return
	 */
	private String getCurrentCatName(List<CurrentDescriptorVO> descriptors) {
		if(null == descriptors) {
			return "";
		}
		
		for(CurrentDescriptorVO currentDescriptor : descriptors) {
			if(currentDescriptor.getRootName().equalsIgnoreCase(BBBEndecaConstants.DEPARTMENT)) {
				return currentDescriptor.getName();
			}
		}
		
		return null;
	}


	/**
	 * Method to iterate over descriptors to find parent category name
	 * @param descriptors
	 * @return
	 */
	private String getParentCatName(List<CurrentDescriptorVO> descriptors) {
		if(null == descriptors) {
			return "";
		}
		
		for(CurrentDescriptorVO currentDescriptor : descriptors) {
			if(currentDescriptor.getRootName().equalsIgnoreCase(BBBEndecaConstants.DEPARTMENT)) {
				return currentDescriptor.getAncestorName();
			}
		}
		
		return null;
	}
	
	
	/**
	 * will parse redirect and splits using redirectDelimiter
	 * @param redirectsFromEndeca
	 * @return
	 * @throws BBBSystemException 
	 */
	public String getRedirectUrl(String redirectFromEndeca, SearchQuery pSearchQuery) throws BBBSystemException {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "getRedirectUrl");
		logDebug("Entering EndecaContentResponseParser.getRedirectUrl method.");
		if(BBBUtility.isEmpty(redirectFromEndeca)) {
			return null;
		}
		
		if(pSearchQuery == null){
			throw new BBBSystemException("Required Parameters null. dynamo request and/or searchQueryVO.");
		}
		
		//set redirect URL only when it is keyword search
		if(isKeywordSearchRequest(pSearchQuery)) {
			return EndecaSearch.getRedirectUrl(Arrays.asList(redirectFromEndeca.split(getConfigUtil().getRedirectDelimiter())), pSearchQuery.getHostname());
		}
		logDebug("Exit EndecaContentResponseParser.getRedirectUrl method.");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "getRedirectUrl");
		return null;
	}

	/**
	 * Method to update swatch images of products based on color selected in descriptor
	 * 
	 * @param bbbProducts
	 * @param color
	 * @return updated bbbProducts list
	 */
	private void updateSwatchImageUrls(final SearchResults searchResults) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "updateSwatchImageUrls");
		logDebug("Entering EndecaContentResponseParser.updateSwatchImageUrls method.");
		if(null!= searchResults && null!= searchResults.getDescriptors()) {
			searchResults.setCurrentCatName(getCurrentCatName(searchResults.getDescriptors()));
			searchResults.setParentCatName(getParentCatName(searchResults.getDescriptors()));
			
			if(null != searchResults.getBbbProducts()){
				int count = 0;
				String color = null;
				String colorGroup = getConfigTools().getConfigKeyValue(BBBCoreConstants.DIM_DISPLAY_CONFIGTYPE, BBBCoreConstants.COLORGROUP_KEY, BBBCoreConstants.FACET_DESCRIPTOR_COLORGROUP);
				for(CurrentDescriptorVO pVo : searchResults.getDescriptors()){
					if((null != pVo.getRootName()) && BBBUtility.isNotEmpty(colorGroup) && (colorGroup.equalsIgnoreCase(pVo.getRootName()))){
						count = count + 1 ;
						color = pVo.getName();
					}
				}
				if(count == 1){
					//when there is only one color in descriptors
					//fix sku image shown while selecting color
					if(null != color){
						for(BBBProduct product : searchResults.getBbbProducts().getBBBProducts()) {
							Map<String, SkuVO> skuMap = product.getSkuSet();
							for(SkuVO skuVO : skuMap.values()) {
								if(null != skuVO.getColorGroup() && skuVO.getColorGroup().equalsIgnoreCase(color)) {
									product.setImageURL(skuVO.getSkuMedImageURL());
									product.setVerticalImageURL(skuVO.getSkuVerticalImageURL());
									product.setProductImageUrlForGrid3x3(skuVO.getSkuMedImageUrlForGrid3x3());
									product.setProductImageUrlForGrid4(skuVO.getSkuMedImageUrlForGrid4());
									break;
								}
							}
			    		}
						
					}
				}
			}
			
		}
		logDebug("Exit EndecaContentResponseParser.updateSwatchImageUrls method.");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_CONTENT_RESPONSE_PARSER, "updateSwatchImageUrls");
	}

	/**
	 * @return the endecaSearch
	 */
	public EndecaSearch getEndecaSearch() {
		return endecaSearch;
	}


	/**
	 * @param endecaSearch the endecaSearch to set
	 */
	public void setEndecaSearch(EndecaSearch endecaSearch) {
		this.endecaSearch = endecaSearch;
	}
	


	/**
	 * @return the endecaSearchUtil
	 */
	public EndecaSearchUtil getEndecaSearchUtil() {
		return endecaSearchUtil;
	}


	/**
	 * @param endecaSearchUtil the endecaSearchUtil to set
	 */
	public void setEndecaSearchUtil(EndecaSearchUtil endecaSearchUtil) {
		this.endecaSearchUtil = endecaSearchUtil;
	}


	/**
	 * @return the mConfigTools
	 */
	public BBBConfigTools getConfigTools() {
		return mConfigTools;
	}


	/**
	 * @param mConfigTools the mConfigTools to set
	 */
	public void setConfigTools(BBBConfigTools mConfigTools) {
		this.mConfigTools = mConfigTools;
	}

}
