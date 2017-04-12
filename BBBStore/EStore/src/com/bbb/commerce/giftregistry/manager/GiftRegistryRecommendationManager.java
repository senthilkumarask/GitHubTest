package com.bbb.commerce.giftregistry.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ComponentName;
import atg.nucleus.naming.ParameterName;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.account.BBBProfileManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.tool.RecommendationRegistryProductVO;
import com.bbb.commerce.giftregistry.tool.RegistryBulkNotificationVO;
import com.bbb.commerce.giftregistry.tool.SortRecommRegistryVO;
import com.bbb.commerce.giftregistry.utility.PriceListComparator;
import com.bbb.commerce.giftregistry.vo.RestRegistryInfoDetailVO;
import com.bbb.commerce.giftregistry.vo.ManageRegItemsResVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.email.BBBEmailHelper;
import com.bbb.email.BBBTemplateEmailSender;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * This class is the business layer object for the management of gift registry Recommendations
 *  object manipulation. It provides the high
 * level access to gift registry Recommendatons. It makes calls to lower level utilities in
 * GiftlistTools which makes the Repository Calls for creation of recommendations and fetching of recommendations..
 *
 * @author vchan5
 */
public class GiftRegistryRecommendationManager extends BBBGenericService {


	private static final String SKU_LIST_PRICE = "listPrice";
	private static final String CATEGORY = "category";
	private static final String SKU_SALE_PRICE = "salePrice";
	private static final String RECOMMENDER_TAB_SORT_BY_ALPHABET = "";
	private GiftRegistryTools mGiftRegistryTools;
	private BBBCatalogTools mCatalogTools;
	private static final String DATE = "date";
	private MutableRepository mGiftRepository;
	private BBBSessionBean mSessionBean;
	private String registrantScheduledBulkEmail;
	private String templateUrl = null;
	private BBBTemplateEmailSender emailSender;
	private String registryURL;
	private String groupByFlag = null;

	private PriceListManager priceListManager;
	private String profilePriceListPropertyName;

	/** The Constant profile */
	public static final ParameterName PROFILE = ParameterName
			.getParameterName("profile");

	public String getRegistryURL() {
		return registryURL;
	}

	public void setRegistryURL(String registryURL) {
		this.registryURL = registryURL;
	}

	private MutableRepository registryInfo;

	/**
	 * @return the mGiftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return mGiftRegistryTools;
	}
	/**
	 * @param mGiftRegistryTools
	 *            the mGiftRegistryTools to set
	 */
	public void setGiftRegistryTools(GiftRegistryTools mGiftRegistryTools) {
		this.mGiftRegistryTools = mGiftRegistryTools;
	}

	public MutableRepository getGiftRepository() {
		return mGiftRepository;
	}
	public void setGiftRepository(MutableRepository mGiftRepository) {
		this.mGiftRepository = mGiftRepository;
	}


	public BBBSessionBean getSessionBean() {
		return mSessionBean;
	}
	public void setSessionBean(BBBSessionBean mSessionBean) {
		this.mSessionBean = mSessionBean;
	}
	/**
	 * This method will give list of recommendations for given registry id.
	 * Below method will also return the results in sorted order based on the sort property
	 * and results for the pagination. refer the below property usages
	 *
	 * @param registryId
	 *            - Cutomer registry ID
	 * @param tabId
	 *            -- 0- pending tab, 1 - Accepting Tab, 2- Declined Tab, 3-
	 *            Recommenders tab
	 * @param sortOption
	 *            - Sort option for the page for sorting of the recommendations,
	 *            preffered values are salePrice, Recommender, Product
	 * @param pageSize
	 *            -- No of recommendations for page, if you need all product for single instance pass 0 or negitive integer
	 * @param pageNum
	 *            - Previous Page Number(1-10) is current page recommendation
	 *            count then 10 will for next pagination
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public List<RecommendationRegistryProductVO> getRegistryRecommendationItemsForTab(
			String registryId, String tabId, String sortOption,
			String eventTypeCode, DynamoHttpServletRequest req)
			throws BBBBusinessException, BBBSystemException {

		logDebug("Registry Recommendations API call getRegistryRecommendationItemsForTab(); Params : registryId="
				+ registryId + ",tabId=" + tabId + ",sortOption=" + sortOption);
		List<RecommendationRegistryProductVO> recommendationsVO = null;
		Map<String, List<RecommendationRegistryProductVO>> categoryBucketsForRecommendation = new LinkedHashMap<String, List<RecommendationRegistryProductVO>>();

		final long startTime = System.currentTimeMillis();
		recommendationsVO = getGiftRegistryTools()
			.getRegistryRecommendationItemsForTab(registryId, tabId);
		
			req = ServletUtil.getCurrentRequest();
			if (req != null) {
			req.setParameter("tabId", tabId);
			categoryBucketsForRecommendation = sortRecommendations(sortOption,
					eventTypeCode, recommendationsVO, req,
					categoryBucketsForRecommendation, tabId);
		}

		final long endTime =  System.currentTimeMillis()-startTime;
		logDebug("Total time taken for processing data for the recommendations is: "
				+ endTime
				+ " for registry id: "
				+ registryId
				+ " and for the tab: " + tabId);
		logDebug("RecommendationRegistryProductVO ::::"
				+ recommendationsVO.toString()
				+ " size of RecommendationRegistryProductVO :::"
				+ recommendationsVO.size());
		for (RecommendationRegistryProductVO recommVO : recommendationsVO) {
			recommVO.setBvProductVO(mCatalogTools
					.getBazaarVoiceDetails(recommVO.getProductId()));
		}
		return recommendationsVO;
	}
	/**
	 * This method will sort the recommendation tab based on the four options.
	 * 
	 * @param sortOption
	 * @param eventTypeCode
	 * @param recommendationsVO
	 * @param categoryBucketsForRecommendation
	 * @param req
	 * 
	 * @return categoryBucketsForRecommendation
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public Map<String, List<RecommendationRegistryProductVO>> sortRecommendations(
			String sortOption,
			String eventTypeCode,
			List<RecommendationRegistryProductVO> recommendationsVO,
			DynamoHttpServletRequest req,
			Map<String, List<RecommendationRegistryProductVO>> categoryBucketsForRecommendation, String tabId)
			throws BBBSystemException, BBBBusinessException {

		logDebug("Registry Recommendations API call sortRecommendations(); Params : sortOption="
				+ sortOption + "eventTypeCode" + eventTypeCode);

		if (!BBBUtility.isListEmpty(recommendationsVO)) {

			List<String> priceRangeList = null;

			final Map<String, Double> skuIdPriceMap = new HashMap<String, Double>();
			String siteId = extractSiteId();
			

			Profile profile = (Profile) ServletUtil.getCurrentRequest()
					.resolveName(BBBCoreConstants.ATG_PROFILE);
			if (req == null) {
				req = ServletUtil.getCurrentRequest();
			}

			
			if (null != sortOption) {
				String registryTypeId = checkRegistryType(eventTypeCode, siteId);
				
				if (sortOption.equalsIgnoreCase(SKU_SALE_PRICE)) {
					Collections
							.sort(recommendationsVO,
									RecommendationRegistryProductVO.salePriceComparator);
					Map<String, RegistryCategoryMapVO> getCategoryForRegistry = getCatalogTools()
							.getCategoryForRegistry(registryTypeId);

					categoryBucketsForRecommendation = priceSorting(
							eventTypeCode, recommendationsVO, skuIdPriceMap,
							categoryBucketsForRecommendation, SKU_SALE_PRICE,
							siteId, registryTypeId, priceRangeList, profile);
					groupByFlag = "price";

				} else if (sortOption.equalsIgnoreCase(DATE)) {
					if(tabId.equalsIgnoreCase(BBBGiftRegistryConstants.ACCEPTED_TAB) || tabId.equalsIgnoreCase(BBBGiftRegistryConstants.DECLINED_TAB)){
						Collections
							.sort(recommendationsVO,
								RecommendationRegistryProductVO.lastModifiedDateComparator);
					}else{
						Collections
							.sort(recommendationsVO,
									RecommendationRegistryProductVO.recommendedDateComparator);
					}
					groupByFlag = "date";
				} else if (sortOption.equalsIgnoreCase(CATEGORY)) {
					// BPS-1379 Sort By Category

					Map<String, RegistryCategoryMapVO> getCategoryForRegistry = getCatalogTools()
							.getCategoryForRegistry(registryTypeId);

					categoryBucketsForRecommendation = jdaCategorySorting(
							eventTypeCode, recommendationsVO,
							categoryBucketsForRecommendation, siteId,
							registryTypeId, getCategoryForRegistry);
					groupByFlag = "category";
				} else if (sortOption.equalsIgnoreCase(SKU_LIST_PRICE)) {
					Collections
							.sort(recommendationsVO,
									RecommendationRegistryProductVO.listPriceComparator);
					Map<String, RegistryCategoryMapVO> getCategoryForRegistry = getCatalogTools()
							.getCategoryForRegistry(registryTypeId);

					categoryBucketsForRecommendation = priceSorting(
							eventTypeCode, recommendationsVO, skuIdPriceMap,
							categoryBucketsForRecommendation, SKU_LIST_PRICE,
							siteId, registryTypeId, priceRangeList, profile);
					groupByFlag = "price";
				} else {
					Collections
							.sort(recommendationsVO,
									RecommendationRegistryProductVO.recommenderComparator);
					groupByFlag = "recommender";
					categoryBucketsForRecommendation = getRecommender(recommendationsVO,categoryBucketsForRecommendation);
				}
			} else {
				Collections.sort(recommendationsVO,
						RecommendationRegistryProductVO.recommendedDateComparator);
				groupByFlag = "date";
			}

			req.setParameter("categoryBucketsForRecommendation",
					categoryBucketsForRecommendation);
			req.setParameter("groupByFlag", groupByFlag);
			req.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,
					priceRangeList);

		}

		return categoryBucketsForRecommendation;
	}

	/**
	 * @return siteID
	 */
	protected String extractSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
	
	
	private Map<String, List<RecommendationRegistryProductVO>> getRecommender(
			List<RecommendationRegistryProductVO> recommendationsVO,
			Map<String, List<RecommendationRegistryProductVO>> categoryBucketsForRecommendation) {
		List<RecommendationRegistryProductVO> list = null;

		for (RecommendationRegistryProductVO recommendVo : recommendationsVO) {
			List<RecommendationRegistryProductVO> pList = new ArrayList<RecommendationRegistryProductVO>();
			String profileId = recommendVo.getRecommenderProfileId();
			String firstName = recommendVo.getFirstName();
			String lastName = recommendVo.getLastName();
			String key = profileId + (":" + firstName + " " + lastName);
			if (categoryBucketsForRecommendation.get(key) == null) {
				pList.add(recommendVo);
				categoryBucketsForRecommendation.put(key, pList);
			} else {
				list = categoryBucketsForRecommendation.get(key);
				list.add(recommendVo);
				categoryBucketsForRecommendation.put(key, list);
			}

		}

		return categoryBucketsForRecommendation;
	}
	/**
	 * This method will sort the recommendation tab based on price.
	 * 
	 * @param sortOption
	 * @param eventTypeCode
	 * @param recommendationsVO
	 * @param categoryBucketsForRecommendation
	 * @param req
	 * 
	 * @return categoryBucketsForRecommendation
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private Map<String, List<RecommendationRegistryProductVO>> priceSorting(
			String eventTypeCode,
			List<RecommendationRegistryProductVO> recommendationsVO,
			Map<String, Double> skuIdPriceMap,
			Map<String, List<RecommendationRegistryProductVO>> categoryBucketsForRecommendation,
			String pPrice, String siteId, String registryTypeId,
			List<String> priceRangeList, Profile profile)
			throws BBBBusinessException, BBBSystemException {

		logDebug("Registry Recommendations API call priceSorting()");
		List<String> skuIdList = getSkuFromRecommendations(recommendationsVO);
		StringBuilder skuTempList = new StringBuilder("");

		Iterator<RecommendationRegistryProductVO> it = recommendationsVO
				.iterator();

		while (it.hasNext()) {
			skuTempList = checkPrice(skuTempList, skuIdList, skuIdPriceMap, it);

		}

		BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(null);
		/*BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil
				.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);*/
		String country = (String) sessionBean
				.getValues()
				.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
		// Get the list of Price Ranges from Catalog.

		
		priceRangeList = getCatalogTools().getPriceRanges(registryTypeId,
				country);

		if (!BBBUtility.isEmpty(country)
				&& !country
						.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)
				&& priceRangeList != null) {
			List<String> changedPriceRangeList = null;
			changedPriceRangeList = new ArrayList<String>();
			for (String item : priceRangeList) {
				String newKey = getNewKeyRange(item);
				changedPriceRangeList.add(newKey);
			}
			priceRangeList = changedPriceRangeList;
		}

		Map<String, List<SKUDetailVO>> categoryMap = getCatalogTools()
				.sortSkubyRegistry(null, skuIdPriceMap, registryTypeId, siteId,
						null, country);

		Set<String> priceSet = categoryMap.keySet();
		for (String price : priceSet) {
			List<RecommendationRegistryProductVO> pList = new ArrayList<RecommendationRegistryProductVO>();

			List<SKUDetailVO> skuVoList = categoryMap.get(price);
			for (SKUDetailVO skuDetailVo : skuVoList) {
				for (RecommendationRegistryProductVO recommendVo : recommendationsVO) {
					if (recommendVo.getSkuId().equalsIgnoreCase(
							skuDetailVo.getSkuId())) {
						   // Removed the logic of fetching from price list and fetching price from the map already set.
						   // Part of BBBH-3983
							Double itemPrice=skuIdPriceMap.get(skuDetailVo.getSkuId());
							recommendVo.setPrice(String.valueOf(itemPrice));
							pList.add(recommendVo);
							}

				}
			}

			Collections.sort(pList,
					RecommendationRegistryProductVO.priceCompare);

			if (!BBBUtility.isEmpty(country)){
			price = getNewKeyRange(price);
			}
			categoryBucketsForRecommendation.put(price, pList);
			getBucket(categoryBucketsForRecommendation, priceRangeList);

		}
		if (categoryBucketsForRecommendation != null) {
			categoryBucketsForRecommendation = getSortedPriceRange(categoryBucketsForRecommendation);
		}
		return categoryBucketsForRecommendation;

	}
	private Map<String, List<RecommendationRegistryProductVO>> getSortedPriceRange(
			final Map<String, List<RecommendationRegistryProductVO>> map) {
		HashMap<Integer, String> tempMap = new HashMap<Integer, String>();
		
		/*BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil
				.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		*/
		
		BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(null);
		String country = (String) sessionBean
				.getValues()
				.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
		int newkey = 0;
		for (String key : map.keySet()) {
			String testkey = key;
			testkey = testkey.replace(",","");
			String regex = "[$+-.A-Z]";
			String[] sub = testkey.split(regex);
		if(!BBBUtility.isEmpty(country) && country.equalsIgnoreCase("US")){
			 newkey = Integer.parseInt(sub[1].trim());
		}
		else{
			if(sub.length < 4){
				newkey = Integer.parseInt(sub[1].trim());
			}
			else{
				 newkey = Integer.parseInt(sub[3].trim());
			}
			
		}
			tempMap.put(newkey, key);

		}
		TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>(tempMap);

		LinkedHashMap<String, List<RecommendationRegistryProductVO>> mapfinal = new LinkedHashMap<String, List<RecommendationRegistryProductVO>>();

		for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {

			mapfinal.put(entry.getValue(), map.get(entry.getValue()));

		}
		return mapfinal;
	}

	private void getBucket(
			final Map<String, List<RecommendationRegistryProductVO>> categoryBuckets,
			List<String> priceRangeList) {
		if(priceRangeList != null){
		for (String pRange : priceRangeList) {
			if (!categoryBuckets.containsKey(pRange)) {
				categoryBuckets.put(pRange, null);
			}

		}
	  }
	}

	private StringBuilder checkPrice(StringBuilder skuTempList,
			final List<String> skuIds, final Map<String, Double> skuIdPriceMap,
			Iterator<RecommendationRegistryProductVO> it)
			throws BBBBusinessException, BBBSystemException {
		String salePrice;
		String listPrice;
		Double inCartPrice=0.0;
		RecommendationRegistryProductVO recRegItemVO = (RecommendationRegistryProductVO) it
				.next();

		String productId = getCatalogTools().getParentProductForSku(
				String.valueOf(recRegItemVO.getSkuId()), true);

		if (productId == null) {
			logDebug("CLS=[RecommendationManager]/"
					+ "Mthd=[CheckPrice]/MSG=[Parent productId null for SKU]=]"
					+ recRegItemVO.getSkuId());
			return skuTempList;
		}
		if (recRegItemVO.getJdaRetailPrice() > 0) {
			salePrice = String.valueOf(recRegItemVO.getJdaRetailPrice());
			listPrice = String.valueOf(recRegItemVO.getJdaRetailPrice());
		} else {
			salePrice = String.valueOf(getCatalogTools().getSalePrice(
					productId, String.valueOf(recRegItemVO.getSkuId())));
			listPrice = String.valueOf(getCatalogTools().getListPrice(
					productId, String.valueOf(recRegItemVO.getSkuId())));
			inCartPrice=recRegItemVO.getSkuIncartPrice();
		}

		boolean priceFound = false;

		if (recRegItemVO.getsKUDetailVO().isInCartFlag()) {
			skuIdPriceMap.put(String.valueOf(recRegItemVO.getSkuId()),
					inCartPrice);
			priceFound = true;

		} else if (!("null").equalsIgnoreCase(salePrice) && !("0.0").equalsIgnoreCase(salePrice)) {
			skuIdPriceMap.put(String.valueOf(recRegItemVO.getSkuId()),
					Double.valueOf(salePrice));
			priceFound = true;

		} else if (!("null").equalsIgnoreCase(listPrice)) {
			skuIdPriceMap.put(String.valueOf(recRegItemVO.getSkuId()),
					Double.valueOf(listPrice));
			priceFound = true;
		}
		if (priceFound) {
			skuIds.add(String.valueOf(recRegItemVO.getSkuId()));
			skuTempList = skuTempList.append(recRegItemVO.getSkuId() + ";");
		} else {
			logDebug("CLS=[RecommendationManager]/"
					+ "Mthd=[CheckPrice]/MSG=[PriceFound = false ] sku="
					+ recRegItemVO.getSkuId());
		}
		return skuTempList;
	}
	public String getNewKeyRange(String value) {
		Properties pAttributes = new Properties();
		String newKey = null;
		Pattern p = Pattern.compile(BBBCoreConstants.PATTERN_FORMAT);
		Matcher m = null;
		m = p.matcher(value);
		int i = 0;
		String minPrice = BBBCoreConstants.BLANK, maxPrice = BBBCoreConstants.BLANK;
		while (m.find()) {
			if (i == 0) {
				minPrice = m.group();
				minPrice = minPrice.substring(1);
				i++;
			} else if (i == 1){
				maxPrice = m.group();
				maxPrice = maxPrice.substring(1);
			}	
				

		}
		pAttributes.setProperty(BBBInternationalShippingConstants.ROUND,
				BBBInternationalShippingConstants.DOWN);
		newKey = BBBUtility.convertToInternationalPrice(value, minPrice,
				maxPrice, pAttributes);

		return newKey;
	}
	/**
	 * This method will give list of recommendations for recommendation tab of
	 * mobile. Below method will also return the results in sorted order based
	 * on the alphabet.
	 * 
	 * @param registryId
	 *            - Customer registry ID
	 * 
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */

	public List<RecommendationRegistryProductVO> getRegistryRecommItemsForMobileRecommenderTab(
			String registryId) throws BBBBusinessException, BBBSystemException {

		logDebug("Registry Recommendations API call getRegistryRecommItemsForMobileRecommenderTab(); Params : registryId="
				+ registryId);
		List<RecommendationRegistryProductVO> recommendationsVO = null;

		final long startTime = System.currentTimeMillis();
		recommendationsVO = getGiftRegistryTools()
				.getRegistryRecommendationItemsForTab(registryId,
						BBBGiftRegistryConstants.RECOMMENDER_TAB_ID);
		DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
		
		Map<String, List<RecommendationRegistryProductVO>> categoryBucketsForRecommendation = new LinkedHashMap<String, List<RecommendationRegistryProductVO>>();
		sortRecommendations(RECOMMENDER_TAB_SORT_BY_ALPHABET, "",
				recommendationsVO, req, categoryBucketsForRecommendation, BBBGiftRegistryConstants.RECOMMENDER_TAB_ID);

		final long endTime = System.currentTimeMillis() - startTime;
		logDebug("Total time taken for processing data for the recommendations is: "
				+ endTime + " for registry id: " + registryId);
		logDebug("RecommendationRegistryProductVO ::::"
				+ recommendationsVO.toString()
				+ " size of RecommendationRegistryProductVO :::"
				+ recommendationsVO.size());
		return recommendationsVO;
	}

	/**
	 * This method will be called for recommendation tab for mobile.
	 * 
	 * @param registryId
	 * @param sortOption
	 * @param eventTypeCode
	 * @param tabId
	 * 
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */

	public SortRecommRegistryVO getRegistryRecommItemsForMobile(
			String registryId, String sortOption, String eventTypeCode,
			String tabId) throws BBBBusinessException, BBBSystemException {

		logDebug("Registry Recommendations API call getRegistryRecommItemsForMobile(); Params : registryId="
				+ registryId
				+ "sortOption = "
				+ sortOption
				+ "eventTypeCode = " + eventTypeCode);
		List<RecommendationRegistryProductVO> recommendationsVO = null;

		final long startTime = System.currentTimeMillis();
		recommendationsVO = getGiftRegistryTools()
				.getRegistryRecommendationItemsForTab(registryId, tabId);
		DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
		Map<String, List<RecommendationRegistryProductVO>> categoryBucketsForRecommendation = new LinkedHashMap<String, List<RecommendationRegistryProductVO>>();

		categoryBucketsForRecommendation = sortRecommendations(sortOption,
				eventTypeCode, recommendationsVO, req,
				categoryBucketsForRecommendation, tabId);

		final long endTime = System.currentTimeMillis() - startTime;
		logDebug("Total time taken for processing data for the recommendations is: "
				+ endTime + " for registry id: " + registryId);
		logDebug("RecommendationRegistryProductVO ::::"
				+ recommendationsVO.toString()
				+ " size of RecommendationRegistryProductVO :::"
				+ recommendationsVO.size());

		final SortRecommRegistryVO sortRecVO = new SortRecommRegistryVO();
		sortRecVO
				.setCategoryBucketsForRecommendation(categoryBucketsForRecommendation);
		sortRecVO.setGroupByFlag(groupByFlag);

		return sortRecVO;
	}
	/**
	 * This method will sort the recommendation tab based on category.
	 * 
	 * @param eventTypeCode
	 * @param recommendationsVO
	 * @param categoryBucketsForRecommendation
	 * @param siteId
	 * @param registryTypeId
	 * @param getCategoryForRegistry
	 * 
	 * @return categoryBucketsForRecommendation
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
private Map<String, List<RecommendationRegistryProductVO>> jdaCategorySorting(
			String eventTypeCode,
			List<RecommendationRegistryProductVO> recommendationsVO,
			Map<String, List<RecommendationRegistryProductVO>> categoryBucketsForRecommendation,
			String siteId, String registryTypeId,
			Map<String, RegistryCategoryMapVO> getCategoryForRegistry)
			throws BBBSystemException, BBBBusinessException {

		logDebug("Registry Recommendations API call jdaCategorySorting()");

		List<String> skuIdList = getSkuFromRecommendations(recommendationsVO);

		logDebug("Sort by Category for sku list" + skuIdList);

		logDebug("EventTypeCode:" + eventTypeCode + " registryTypeId"
				+ registryTypeId + " Site ID:" + siteId);

		Map<String, List<SKUDetailVO>> categoryMap = getCatalogTools()
				.sortSkubyRegistry(skuIdList, null, registryTypeId, siteId,
						BBBCatalogConstants.CATEGORY_SORT_TYPE, null);

		Set<String> jdaCatSet = categoryMap.keySet();
		for (String jdaCat : jdaCatSet) {
			List<RecommendationRegistryProductVO> pList = new ArrayList<RecommendationRegistryProductVO>();
			logDebug("JDA Category on basis of EventTypeCode" + jdaCat);

			List<SKUDetailVO> skuVoList = categoryMap.get(jdaCat);
			for (SKUDetailVO skuDetailVo : skuVoList) {
				for (RecommendationRegistryProductVO recommendVo : recommendationsVO) {
					if (recommendVo.getSkuId().equalsIgnoreCase(
							skuDetailVo.getSkuId())) {
						if (BBBUtility.isEmpty(recommendVo.getJdaCategory())) {
							recommendVo.setJdaCategory(jdaCat);

							pList.add(recommendVo);
							break;

						}
					}

				}
			}

			categoryBucketsForRecommendation.put(jdaCat, pList);
			for (String catBucket : getCategoryForRegistry.keySet()) {
				RegistryCategoryMapVO registryCategoryMapVO = getCategoryForRegistry.get(catBucket);
				if (!categoryBucketsForRecommendation.containsKey(catBucket) && registryCategoryMapVO.isRecommendedCatFlag()) {
					categoryBucketsForRecommendation.put(catBucket, null);
				}
			}

		}
		return categoryBucketsForRecommendation;

	}

	/*private List<RecommendationRegistryProductVO> doPagination(
			List<RecommendationRegistryProductVO> recommendationsVO,
			int pageSize, int pageNum) {
		if(recommendationsVO!=null && recommendationsVO.size() > 0 
				&& pageNum >= 0 && pageSize > 0) {
			int endIndex=0;
			int startIndex = 0;
			startIndex = pageSize*pageNum;
			if(startIndex > recommendationsVO.size()){
				startIndex = recommendationsVO.size();
			}
			endIndex= startIndex + pageSize;
			if(endIndex > recommendationsVO.size()){
				endIndex = recommendationsVO.size();
			}

			recommendationsVO=recommendationsVO.subList(startIndex, endIndex);
		}

		return recommendationsVO;


	}*/
	/**
	 * The method is used create a recommended product for passed friend registry from the PDP page.
	 * will take registry Id which for product recomandation has to created, skuid is products child sku,
	 * comment submitted from pop window and quantity which recommender has recommended for the prodcut
	 * @param registryId
	 * @param skuId
	 * @param comment
	 * @param quantity
	 * @throws BBBSystemException
	 */
	public void createRegistryRecommendationProduct(String registryId,
			String skuId, String comment, Long quantity,
			String recommenderProfileId) throws BBBSystemException {

		logDebug("Registry Recommendations Creation API call createRegistryRecommendationProduct(); Params : registryId="
				+ registryId
				+ ",skuId="
				+ skuId
				+ ",comment="
				+ comment
				+ ",quantity=" + quantity);
		if (!StringUtils.isEmpty(registryId) && !StringUtils.isEmpty(skuId)
				&& quantity > 0) {
			getGiftRegistryTools().createRegistryRecommendationsItem(
					new RecommendationRegistryProductVO(registryId, skuId,
							quantity, comment, recommenderProfileId));
		}
		logDebug("Registry Recommendations Created for Params : registryId="
				+ registryId + ",skuId=" + skuId + ",comment=" + comment
				+ ",quantity=" + quantity);
	}

	/**
	 * The method is used as wrapper method for
	 * createRegistryRecommendationProduct method.
	 * 
	 * @param registryId
	 * @param skuId
	 * @param comment
	 * @param quantity
	 * @throws BBBSystemException
	 */
	public void createRegistryRecommendationProductService(String registryId,
			String skuId, String comment, Long quantity)
			throws BBBSystemException {

		logDebug("Registry Recommendations Creation API call createRegistryRecommendationProductService(); Params : registryId="
				+ registryId
				+ ",skuId="
				+ skuId
				+ ",comment="
				+ comment
				+ ",quantity=" + quantity);
		final DynamoHttpServletRequest pRequest = ServletUtil
				.getCurrentRequest();
		final Profile profile = (Profile) pRequest.resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
		String recommenderProfileId = profile.getRepositoryId();
		createRegistryRecommendationProduct(registryId, skuId, comment,
				quantity, recommenderProfileId);
		logDebug("Exiting createRegistryRecommendationProductService ");
	}

	// BPSI-147- Scheduler to send bulk monthly email
	/**
	 * This method sends email to registrant for newly recommended items.
	 * 
	 * @param registryBulkNotificationVO
	 * 
	 * @return
	 * @throws BBBSystemException
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean sendRegistrantScheduledBulkEmail(
			final RegistryBulkNotificationVO registryBulkNotificationVO)
			throws BBBSystemException {

		this.logDebug("GiftRegistryRecommendationManager.sendRegistrantScheduledBulkEmail() method "
				+ "starts with parameters sendInviterBulkEmailVO: "
				+ registryBulkNotificationVO);

		final String profileId = registryBulkNotificationVO.getProfileId();
		final String siteId = registryBulkNotificationVO.getSiteId();
		final String registrantEmail = registryBulkNotificationVO
				.getRegistrantEmail();

		boolean emailSuccess = true;
		final Map emailParams = new HashMap<String, String>();
		final Map<String, String> placeHolderValues = new HashMap<String, String>();
		final Calendar currentDate = Calendar.getInstance();
		final long uniqueKeyDate = currentDate.getTimeInMillis();
		final String emailPersistId = profileId + uniqueKeyDate;
		final TemplateEmailInfoImpl emailInfo = (TemplateEmailInfoImpl) ServletUtil
				.getCurrentRequest()
				.resolveName(
						"/com/bbb/commerce/giftregistry/email/GiftRegistryEmailInfoImpl");

		placeHolderValues.put(BBBGiftRegistryConstants.FRMDATA_SITEID, siteId);
		placeHolderValues
				.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId);
		placeHolderValues.put(BBBGiftRegistryConstants.EMAIL_TYPE,
				this.getRegistrantScheduledBulkEmail());

		emailParams.put(BBBCoreConstants.TEMPLATE_URL_PARAM_NAME,
				this.getTemplateUrl());
		emailParams.put("recommendersDetail", registryBulkNotificationVO);
		emailParams.put(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES,
				placeHolderValues);
		emailParams.put(BBBCoreConstants.SITE_ID, siteId);
		emailParams.put(BBBCoreConstants.RECIPIENT_EMAIL, registrantEmail);
		placeHolderValues.put(BBBCoreConstants.IS_FROM_SCHEDULER,
				BBBCoreConstants.TRUE);
		placeHolderValues.put(BBBCoreConstants.REGISTRY_URL,
				getRegistryURL() + BBBCoreConstants.QUESTION_MARK
						+ BBBCoreConstants.REGISTRY_ID + BBBCoreConstants.EQUAL
						+ registryBulkNotificationVO.getRegistryId()
						+ BBBCoreConstants.AMPERSAND
						+ BBBCoreConstants.EVENT_TYPE + BBBCoreConstants.EQUAL
						+ registryBulkNotificationVO.getEventType()
						+ "#t=recommendations");
		placeHolderValues.put(
				BBBGiftRegistryConstants.NEW_RECOMMENDATION_COUNT,
				((Integer) registryBulkNotificationVO.getRecomCount())
						.toString());
		placeHolderValues.put(
				BBBGiftRegistryConstants.RECOMMENDER_FULL_NAME,
				registryBulkNotificationVO.getRegistFirstName()
						+ BBBCoreConstants.SPACE
						+ registryBulkNotificationVO.getRegistLastName());
		emailInfo.setMessageTo(registrantEmail);

		try {
			BBBEmailHelper.sendEmail(null, emailParams, this.getEmailSender(),
					emailInfo);

		} catch (final TemplateEmailException e) {
			emailSuccess = false;
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10123
							+ " BBBBusinessException of "
							+ "sendRegistrantScheduledBulkEmail from GiftRegistryRecommendationManager",
					e);
		} catch (RepositoryException e) {
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10123
							+ " BBBBusinessException of "
							+ "sendRegistrantScheduledBulkEmail from GiftRegistryRecommendationManager",
					e);
		}

		this.logDebug("GiftRegistryRecommendationManager.sendRegistrantScheduledBulkEmail() method "
				+ "ends with return parameter emailSuccess: " + emailSuccess);

		return emailSuccess;
	}
		/**
	 * Returns list of Sku id from List of recommendationsVO
	 * 
	 * @param recommendationsVO
	 * @return
	 */
	public List<String> getSkuFromRecommendations(
			List<RecommendationRegistryProductVO> recommendationsVO) {
		logDebug("Method getSkuFromRecommendations || Start");
		List<String> skuIdList = new ArrayList<String>();
		for(RecommendationRegistryProductVO recommendationProdVO : recommendationsVO) {
			skuIdList.add(recommendationProdVO.getSkuId());
		}
		logDebug("Method getSkuFromRecommendations || End");
		return skuIdList;
	}

	// BPS-
	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	//BPS-1379 Sort By Category
	/**
	 * Returns registryTypeId for eventTypeCode Eg. eventTypeCode = BRD
	 * (Wedding) registryTypeId = DC100001
	 * @param eventTypeCode
	 * @param iter
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String checkRegistryType(final String  eventTypeCode, String siteId) 
			throws BBBSystemException, BBBBusinessException {
		logDebug("Start checkRegistryType || For eventTypeCode"+eventTypeCode + " ");
		final List<RegistryTypeVO> list = getCatalogTools().getRegistryTypes(
				siteId);
		final Iterator<RegistryTypeVO> iter = list.iterator();

		String registryTypeId = "";
		while (iter.hasNext()) {
			RegistryTypeVO registryTypeVO = (RegistryTypeVO) iter.next();
			if (registryTypeVO.getRegistryCode()
					.equalsIgnoreCase(eventTypeCode)) {
				registryTypeId = registryTypeVO.getRegistryTypeId();
				break;
			}
		}
		logDebug("End registryTypeId || For eventTypeCode"+eventTypeCode+" ");
		return registryTypeId;
	}
	// BPS-1379 Sort By Category


	/**
	 * This method adds item to gift registry
	 *  from pending & declined recommendations tab.
	 *
	 * @param jsonObject
	 * @param fromDeclinedTab
	 * @throws RepositoryException
	 */
	public boolean acceptRecommendation(
			GiftRegistryViewBean giftRegistryViewBean, Boolean fromDeclinedTab) {

		logDebug("In pending Items Handling method :");

		int qty = giftRegistryViewBean.getTotQuantity();
		String quantity = String.valueOf(qty);
		String repositoryId = giftRegistryViewBean.getRepositoryId();

		Long quantityAccepted = Long.valueOf(quantity);
		String ltlShipMethod=null;
		String assemblySelected=null;
		if(giftRegistryViewBean.getAdditem()!=null && giftRegistryViewBean.getAdditem().get(0)!=null){
			ltlShipMethod=giftRegistryViewBean.getAdditem().get(0).getLtlDeliveryServices();
			if(BBBCoreConstants.LWA.equalsIgnoreCase(ltlShipMethod)){
				ltlShipMethod=BBBCoreConstants.LW;
				assemblySelected=BBBCoreConstants.TRUE;
				
			}
		}
		logDebug("the quanity accepted is : " + quantity);

		MutableRepositoryItem pendingItems = null;

		ServletUtil.getCurrentRequest().setParameter(
				BBBGiftRegistryConstants.IS_DECLINED,
				giftRegistryViewBean.getIsDeclined());
		try {
			pendingItems = getGiftRepository().getItemForUpdate(repositoryId,
					BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS);
			if (null != pendingItems) {
				pendingItems.setPropertyValue(
						BBBGiftRegistryConstants.ACCEPTED_QUANTITY,
						quantityAccepted);
				if(BBBUtility.isNotEmpty(ltlShipMethod)){
					pendingItems.setPropertyValue(
							BBBGiftRegistryConstants.LTL_SHIP_METHOD,
							ltlShipMethod);
				}
				if(BBBUtility.isNotEmpty(assemblySelected)){
					pendingItems.setPropertyValue(
							BBBGiftRegistryConstants.ASEEMBLY_SELECTED,
							assemblySelected);
				}
				if (fromDeclinedTab) {
					ServletUtil.getCurrentRequest().setParameter(
							BBBGiftRegistryConstants.IS_FROM_DECLINED_TAB,
							giftRegistryViewBean.getIsFromDeclinedTab());
					pendingItems.setPropertyValue(
							BBBGiftRegistryConstants.DECLINED2, 0);
				} else {
					ServletUtil.getCurrentRequest().setParameter(
							BBBGiftRegistryConstants.IS_FROM_PENDING_TAB,
							giftRegistryViewBean.getIsFromPendingTab());
				}

				getGiftRepository().updateItem(pendingItems);
			} else {
				return false;
			}

		} catch (RepositoryException e) {
			logError("Error while updating quantity", e);
		}
		return true;
	}

	public boolean declinePendingItems(GiftRegistryViewBean giftRegistryViewBean) {

		logDebug("In decline Pending Items :");

		ServletUtil.getCurrentRequest().setParameter(
				BBBGiftRegistryConstants.IS_FROM_PENDING_TAB,
				giftRegistryViewBean.getIsFromPendingTab());
		ServletUtil.getCurrentRequest().setParameter(
				BBBGiftRegistryConstants.IS_DECLINED,
				giftRegistryViewBean.getIsDeclined());
		int quantityDeclined = 1;
		String repositoryId = giftRegistryViewBean.getRepositoryId();
		MutableRepositoryItem pendingItems = null;

		try {
			pendingItems = getGiftRepository().getItemForUpdate(repositoryId,
					BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS);
			if (null != pendingItems) {
				pendingItems.setPropertyValue(
						BBBGiftRegistryConstants.DECLINED2, quantityDeclined);
				getGiftRepository().updateItem(pendingItems);
			} else {
				return false;
			}
		} catch (RepositoryException e) {
			logError("error while updating declined quantity", e);
		}

		return true;
	}
	
	/**
	 * 
	 * Validates the given parameters as valid query parameters.
	 * Checks if registryId, recommenderProfileId are valid strings.
	 * The requestedFlag can only be "block"/"unblock".
	 * Calls the GiftRegistryTools.persistChangeInBlockStatus(registryId, recommenderProfileId, reqFlag) to persist the
	 * change in flag once all the parameters are validated.
	 * 
	 * @param registryId
	 * @param recommenderProfileId
	 * @param requestedFlag
	 * @return
	 * @throws BBBSystemException
	 */
	public boolean persistToggleRecommenderStatus(String registryId, 
					String recommenderProfileId, String requestedFlag) 
						throws BBBSystemException{
		if(BBBUtility.isEmpty(registryId) 
				|| BBBUtility.isEmpty(recommenderProfileId) 
				|| BBBUtility.isEmpty(requestedFlag)){
			throw new BBBSystemException(
					"Invalid Parameter Values have been passed. They are registryId:- "
							+ registryId + " , recommenderProfileId:-"
							+ recommenderProfileId + "requestedFlag:- "
							+ requestedFlag);
		}else{
			int reqFlag = 1;
			if (requestedFlag
					.equalsIgnoreCase(BBBGiftRegistryConstants.BLOCK_USER)) {
				reqFlag = 0;
			} else if (requestedFlag
					.equalsIgnoreCase(BBBGiftRegistryConstants.UNBLOCK_USER)) {
				reqFlag = 1;
			}else{
				logError("Illegal flag value received for the requestedFlag. Value received is "
						+ requestedFlag);
				return false;
			}
			return this.getGiftRegistryTools().persistChangeInBlockStatus(
					registryId, recommenderProfileId, reqFlag);
		}
	}
	
	/**
	 * This method is used to change option to receive scheduled email by
	 * registrant from recommender tab.
	 * 
	 * @param registryId
	 * @param emailOptionValue
	 *            -1 for never, 0 for daily, 1 for weekly, 2 for monthly
	 * @return the true if success
	 * @throws BBBSystemException
	 */
	public boolean setEmailOptInChange(final String registryId,
			final String emailOptionValue) throws BBBSystemException {
		this.logDebug("GiftRegistryRecommendationManager.setEmailOptInChange() method starts with parameters "
				+ "registryId: "
				+ registryId
				+ " & emailOptionValue: "
				+ emailOptionValue);
		MutableRepositoryItem registryRecommendationItem = null;
		int emailOption = Integer.parseInt(emailOptionValue);
		if (emailOption >= -1 && emailOption <= 2) {
			try {
				registryRecommendationItem = getGiftRepository()
						.getItemForUpdate(
								registryId,
								BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS);
				registryRecommendationItem.setPropertyValue(
						BBBGiftRegistryConstants.EMAIL_OPTION, emailOption);
				getGiftRepository().updateItem(registryRecommendationItem);
			} catch (RepositoryException e) {
				logError("Error while setting value of emailOption", e);
				this.logDebug("GiftRegistryRecommendationManager.setEmailOptInChange() method ends with return value: " + false);
				return false;
			}
		} else {
			return false;
		}

		this.logDebug("GiftRegistryRecommendationManager.setEmailOptInChange() method ends with return value: " + true);
		return true;
	}

	public int getEmailOptInValue(final String registryId)
			throws BBBSystemException {
		this.logDebug("GiftRegistryRecommendationManager.getEmailOptInValue() method starts with parameters "
				+ "registryId: " + registryId);
		MutableRepositoryItem registryRecommendationItem = null;
		int emailOption = -1;
		try {
			registryRecommendationItem = (MutableRepositoryItem) getGiftRepository()
					.getItem(registryId,
							BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS);
			if (null != registryRecommendationItem &&
					null != registryRecommendationItem
					.getPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION)) {
				emailOption = (Integer) registryRecommendationItem
						.getPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION);
			}
		} catch (RepositoryException e) {
			logError("Error while fetching value of emailOption", e);
		}
		this.logDebug("GiftRegistryRecommendationManager.getEmailOptInValue() method ends with return "
				+ "parameter emailOption: " + emailOption);
		return emailOption;
	}

	public String getRegistrantScheduledBulkEmail() {
		return registrantScheduledBulkEmail;
	}

	public void setRegistrantScheduledBulkEmail(
			final String registrantScheduledBulkEmail) {
		this.registrantScheduledBulkEmail = registrantScheduledBulkEmail;
	}

	public String getTemplateUrl() {
		return templateUrl;
	}

	public void setTemplateUrl(final String templateUrl) {
		this.templateUrl = templateUrl;
	}

	public BBBTemplateEmailSender getEmailSender() {
		return emailSender;
	}

	public void setEmailSender(final BBBTemplateEmailSender emailSender) {
		this.emailSender = emailSender;
	}

	public PriceListManager getPriceListManager() {
		return priceListManager;
	}

	public void setPriceListManager(PriceListManager priceListManager) {
		this.priceListManager = priceListManager;
	}

	public String getProfilePriceListPropertyName() {
		return profilePriceListPropertyName;
	}

	public void setProfilePriceListPropertyName(
			String profilePriceListPropertyName) {
		this.profilePriceListPropertyName = profilePriceListPropertyName;
	}
	
	/**
	 * 
	 * undoFrom is the action from which it is to be performed.
	 * 
	 * @param giftRegistryViewBean
	 * @param undoFrom
	 * @return
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 * @throws RepositoryException 
	 */
	public boolean performUndo(GiftRegistryViewBean giftRegistryViewBean, String undoFrom) throws BBBBusinessException, BBBSystemException {
		// TODO Auto-generated method stub
		boolean result = false;
		
		try {
			if(undoFrom.equalsIgnoreCase("moveToPending")){
				result = revertToPending(giftRegistryViewBean);
			}
			else if(undoFrom.equalsIgnoreCase("moveToDeclined")){
				result = revertToDeclined(giftRegistryViewBean);
			}
			if(!result){
				ManageRegItemsResVO resultVO = this.getGiftRegistryTools().removeUpdateGiftRegistryItem(giftRegistryViewBean);
				if(!resultVO.getServiceErrorVO().isErrorExists()){
					return true;
				}else{
					return false;
				}
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String getRowIdAfterAddition(AddItemsBean addedItemsBean) throws RepositoryException{
		String rowId = null;
		
		RepositoryView view = getGiftRepository().getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL);
		Object[] params = new Object[2];
		params[0] = addedItemsBean.getSku();
		params[1] = addedItemsBean.getRegistryId();
		
		RqlStatement rqlStatement = RqlStatement.parseRqlStatement("skuId=?0 and registryNum=?1");
		RepositoryItem[] addedItems = extractDbCall(view, params, rqlStatement);
		if(addedItems!=null && addedItems.length!=1){
			RepositoryItem addedItem = addedItems[0];
			rowId = (String) addedItem.getPropertyValue("ROWID");
		}else{
			logError("Unable to detect added product");
		}
			
		return rowId;
	}

	/**
	 * @param view
	 * @param params
	 * @param rqlStatement
	 * @return
	 * @throws RepositoryException
	 */
	protected RepositoryItem[] extractDbCall(RepositoryView view, Object[] params, RqlStatement rqlStatement)
			throws RepositoryException {
		return rqlStatement.executeQuery(view, params);
	}
	
	public boolean revertToPending(GiftRegistryViewBean giftRegistryViewBean) throws RepositoryException{
		boolean result = false;
		
		MutableRepositoryItem pendingItems = getRecommendation(giftRegistryViewBean);
		if(null != pendingItems){
			pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0);
            pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
			getGiftRepository().updateItem(pendingItems);
			result = true;
		}
		
		return result;
		
	}
	
	public MutableRepositoryItem getRecommendation(GiftRegistryViewBean giftRegistryViewBean) throws RepositoryException{
		MutableRepositoryItem pendingItems = null;
		String repositoryId = (String)giftRegistryViewBean.getRegistryId();
		pendingItems = getGiftRepository().getItemForUpdate(repositoryId,BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS);
		return pendingItems;
	}
	
	public boolean revertToDeclined(GiftRegistryViewBean giftRegistryViewBean) throws RepositoryException{
		boolean result = false;
		MutableRepositoryItem pendingItems = getRecommendation(giftRegistryViewBean);
		
		if(null != pendingItems){
			pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0);
            pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
			getGiftRepository().updateItem(pendingItems);
			result = true;
		}
		
		return result;
		
	}

}

