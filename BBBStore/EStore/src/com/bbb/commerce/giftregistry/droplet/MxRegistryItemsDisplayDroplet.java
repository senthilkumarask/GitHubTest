package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.cms.PromoBoxVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.utility.PriceListComparator;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * This droplet Fetch Mx Registry Item List from registry Id and display registry
 * item list.
 * 
 * @author skalr2
 * 
 */
public class MxRegistryItemsDisplayDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	/** The Registry items list service name. */
	private String mRegistryItemsListServiceName;
	
	private PriceListManager priceListManager;
	
	private String profilePriceListPropertyName;
	
	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;

	private static final String SITE_ID = "siteId";

	/** The Constant ERR_REGINFO_FATAL_ERROR. */
	private static final String ERR_REGINFO_FATAL_ERROR = "err_gift_reg_fatal_error";

	/** The Constant ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR. */
	private static final String ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR = "err_gift_reg_siteflag_usertoken_error";

	/** The Constant ERR_REGINFO_INVALID_INPUT_FORMAT. */
	private static final String ERR_REGINFO_INVALID_INPUT_FORMAT = "err_gift_reg_invalid_input_format";

	private int mCertonaListMaxCount;
	
	private String topRegMaxCount;
	
	/**
	 * @return the mCertonaListMaxCount
	 */
	public final int getCertonaListMaxCount() {
		return mCertonaListMaxCount;
	}

	/**
	 * @param mCertonaListMaxCount the mCertonaListMaxCount to set
	 */
	public final void setCertonaListMaxCount(int pCertonaListMaxCount) {
		this.mCertonaListMaxCount = pCertonaListMaxCount;
	}
	/**
	 * Fetch Registry Item List from registry Id and display registry item list.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		logDebug(" MxRegistryItemsDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		BBBPerformanceMonitor.start("MxRegistryItemsDisplayDroplet", "GetRegistryItemList");
		final RegistrySearchVO registrySearchVO = new RegistrySearchVO();
		
		
		RegistryItemsListVO registryItemsListVO = null;
		String siteId = null;
		String registryId = null;
		int startIdx = 0;
		int blkSize = 0;
		Boolean isGiftGiver = false;
		Boolean isMxGiftGiver = false;
		Boolean isAvailForWebPurchaseFlag = false;
		String sortSeq = null;
		String view = null;
		String eventTypeCode = null;
		
		try {
			
			registryId = pRequest.getParameter( REGISTRY_ID );
			sortSeq = pRequest.getParameter(SORT_SEQ);
			view = pRequest.getParameter(VIEW);
			eventTypeCode = pRequest.getParameter(REG_EVENT_TYPE_CODE);
			startIdx = Integer.valueOf(pRequest.getParameter(START_INDEX));
			blkSize = Integer.valueOf(pRequest.getParameter(BULK_SIZE));
			isGiftGiver = Boolean.valueOf(pRequest.getParameter(IS_GIFT_GIVER));
			isMxGiftGiver = Boolean.valueOf(pRequest.getParameter("isMxGiftGiver"));
			isAvailForWebPurchaseFlag = Boolean.valueOf(pRequest.getParameter(IS_AVAIL_WEBPUR));
			siteId = pRequest.getParameter(SITE_ID_PARAM);
					
			final Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
			if(!isGiftGiver){
				mGiftRegistryManager.isRegistryOwnedByProfile(profile.getRepositoryId(),registryId,siteId);
			}
			
			if (sortSeq == null) {
				sortSeq = BBBGiftRegistryConstants.DEFAULT_CAT_SORT_SEQ;
			}
			if (view == null) {
				view = BBBGiftRegistryConstants.DEFAULT_ALL_VIEW_FILTER;
			}

			logDebug("pSiteId[" + siteId + "]");
			logDebug("pRegistryId[" + registryId + "]");
			
			if (isMxGiftGiver) //added for Mexico registry
			{
				registrySearchVO.setSiteId("5");
			}
			else if ((getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId))
					.size() != 0) {
				registrySearchVO.setSiteId(getCatalogTools()
						.getAllValuesForKey(
								BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
								siteId).get(0));
			}
			if ((getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)).size() != 0) {
				registrySearchVO.setUserToken(getCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
						.get(0));
			}
			registrySearchVO.setServiceName(getRegistryItemsListServiceName());

			registrySearchVO.setRegistryId(registryId);

			registrySearchVO.setView(Integer.parseInt(view));
			// registrySearchVO.setSortSeqItemList(sortSeq);
			registrySearchVO.setStartIdx(startIdx);
			registrySearchVO.setBlkSize(blkSize);
			registrySearchVO.setGiftGiver(isGiftGiver);
			registrySearchVO
					.setAvailForWebPurchaseFlag(isAvailForWebPurchaseFlag);

			// calling the GiftRegistryManager's fetchRegistryItems method
			registryItemsListVO = mGiftRegistryManager
					.fetchRegistryItems(registrySearchVO);

			String registryTypeId = null;

			final List<RegistryTypeVO> list = getCatalogTools()
					.getRegistryTypes(siteId);
			final Iterator<RegistryTypeVO> iter = list.iterator();
			registryTypeId = checkRegistryType(eventTypeCode, registryTypeId,
					iter);
			
			final String sortSequence = sortSeq;
			
			if (registryItemsListVO != null) {

				processItemList(pRequest, pResponse, registryTypeId,
						sortSequence, registryItemsListVO);

			}
		} catch (BBBBusinessException bbbbEx) {
			if(null != registryItemsListVO){
				logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException from service of RegistriesItemDisplayDroplet : Error Id is:"	+ registryItemsListVO.getServiceErrorVO().getErrorId() , BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1079),bbbbEx);	
			}
			pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);

		} catch (BBBSystemException bbbsEx) {
			logError("BBBSystemException in serviceMethod of registry : " + registryId + " Exception is : "+bbbsEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
		}
		logDebug(" MxRegistryItemsDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends");
		BBBPerformanceMonitor.end("MxRegistryItemsDisplayDroplet", "GetRegistryItemList");
	}

	private void processItemList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String registryTypeId,
			final String sortSequence , RegistryItemsListVO registryItemsListVO) throws ServletException, IOException,
			BBBBusinessException, BBBSystemException {
		if (registryItemsListVO.getServiceErrorVO().isErrorExists()) {

			if (!BBBUtility.isEmpty(registryItemsListVO
					.getServiceErrorVO().getErrorDisplayMessage())
					&& registryItemsListVO.getServiceErrorVO()
							.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR)// Technical
																										// Error
			{
				logError(LogMessageFormatter.formatMessage(pRequest, "Fatal error from service of RegistriesItemDisplayDroplet : Error Id is:"	+ registryItemsListVO.getServiceErrorVO().getErrorId(), BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1011));
				pRequest.setParameter(OUTPUT_ERROR_MSG,
						ERR_REGINFO_FATAL_ERROR);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
			if (!BBBUtility.isEmpty(registryItemsListVO
					.getServiceErrorVO().getErrorDisplayMessage())
					&& registryItemsListVO.getServiceErrorVO()
							.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN)// Technical
																												// Error
			{
				logError(LogMessageFormatter.formatMessage(pRequest, "Either user token or site flag invalid from service of MxRegistryItemsDisplayDroplet : Error Id is:"	+ registryItemsListVO.getServiceErrorVO()
						.getErrorId(), BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1002));
				pRequest.setParameter(OUTPUT_ERROR_MSG,
						ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
			if (!BBBUtility.isEmpty(registryItemsListVO
					.getServiceErrorVO().getErrorDisplayMessage())
					&& registryItemsListVO.getServiceErrorVO()
							.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT)// Technical
																												// Error
			{
				
				
				logError(LogMessageFormatter.formatMessage(pRequest, 
						"GiftRegistry input fields format error from processItemList() of " +
						"MxRegistryItemsDisplayDroplet | webservice error code=" + registryItemsListVO.getServiceErrorVO().getErrorId(), 
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));				
				
				pRequest.setParameter(OUTPUT_ERROR_MSG,
						ERR_REGINFO_INVALID_INPUT_FORMAT);
				
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
		}
		
		PromoBoxVO promoBoxVO = getCatalogTools().getPromoBoxForRegistry(registryTypeId);
		
		pRequest.setParameter(
				BBBGiftRegistryConstants.PROMOBOX,
				promoBoxVO);
		
		 List<RegistryItemVO> listRegistryItemVO = registryItemsListVO
				.getRegistryItemList();
		listRegistryItemVO=fliterNotAvliableItem(listRegistryItemVO);
		final Map<String, RegistryItemVO> mSkuRegItemVOMap = registryItemsListVO
				.getSkuRegItemVOMap();

		StringBuilder skuTempList = new StringBuilder("");

		final List<String> skuIds = new ArrayList<String>();
		final Map<String, Double> skuIdPriceMap = new HashMap<String, Double>();
		if (null != listRegistryItemVO 
				&& !StringUtils.isEmpty(registryTypeId)&& listRegistryItemVO.size() > 0) {
			
			pRequest.setParameter(
					BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT,
					registryItemsListVO.getTotEntries());
			
			getlistRegistryItemVO(pRequest, pResponse, registryTypeId,
					sortSequence, listRegistryItemVO, mSkuRegItemVOMap,
					skuTempList, skuIds, skuIdPriceMap);

		} else {
			pRequest.setParameter(
					BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 0);
			checkWithNull(pRequest, pResponse, registryTypeId,
					sortSequence);
			
		}
	}

	private void checkWithNull(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String registryTypeId,
			final String sortSequence) throws BBBSystemException,
			BBBBusinessException, ServletException, IOException {
		if (sortSequence.contentEquals(BBBGiftRegistryConstants.PRICE_SORT_SEQ))
		{
			List<String> priceRangeList = getCatalogTools()
					.getPriceRanges(registryTypeId,null);
			pRequest.setParameter(
					BBBGiftRegistryConstants.PRICE_RANGE_LIST,
					priceRangeList);
			Map<String, String> map = new HashMap<String, String>();
			for (String priString : priceRangeList) {
				map.put(priString, null) ;
			}
			pRequest.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,map);
		}
		
		else
		{
			Map<String, RegistryCategoryMapVO> getCategoryForRegistry = getCatalogTools()
					.getCategoryForRegistry(registryTypeId);
			//Map<String, String> map = new HashMap<String, String>();
			Map<String, List<RegistryItemVO>> categoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
			for (String catBucket : getCategoryForRegistry.keySet()) {
				//map.put(catBucket, null) ;
				categoryBuckets.put(catBucket, categoryBuckets.get(catBucket));
			}
			pRequest.setParameter(
					BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO,
					getCategoryForRegistry);
			pRequest.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,categoryBuckets);
		}
		
		pRequest.setParameter(BBBGiftRegistryConstants.EMPTY_LIST,"true");
		pRequest.setParameter(BBBGiftRegistryConstants.SORT_SEQUENCE,sortSequence);
		pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
				pResponse);
	}

	private void getlistRegistryItemVO(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String registryTypeId,
			final String sortSequence, List<RegistryItemVO> listRegistryItemVO,
			final Map<String, RegistryItemVO> mSkuRegItemVOMap,
			StringBuilder skuTempList, final List<String> skuIds,
			final Map<String, Double> skuIdPriceMap)
			throws BBBBusinessException, BBBSystemException, ServletException,
			IOException {
		String skuList,certonaSkuList;
		Iterator<RegistryItemVO> it = listRegistryItemVO.iterator();
		int certonaTopRegCount = 0;
		StringBuilder tempCertonaList = new StringBuilder("");
		
		String certonaTopRegMax = getCatalogTools().getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, getTopRegMaxCount()).get(0);
		if(!BBBUtility.isEmpty(certonaTopRegMax)){
			setCertonaListMaxCount(Integer.parseInt(certonaTopRegMax));
		}
		
		while (it.hasNext()) {
			certonaTopRegCount++;
			if(certonaTopRegCount==getCertonaListMaxCount())
				tempCertonaList.append(skuTempList);
			
			skuTempList = checkPrice(skuTempList, skuIds, skuIdPriceMap, it);
		}
		if(certonaTopRegCount>0 && certonaTopRegCount<getCertonaListMaxCount())
			tempCertonaList.append(skuTempList);

		if (skuTempList.length() > 0) {
			skuList = skuTempList.substring(0,
					skuTempList.length() - 1);
			certonaSkuList = tempCertonaList.substring(0, tempCertonaList.length() - 1);
			pRequest.setParameter("skuList", skuList);
			pRequest.setParameter("certonaSkuList", certonaSkuList);
		}
		
		Map<String, List<RegistryItemVO>> categoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> inStockCategoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> notInStockCategoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> categoryTempBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> inStockCategoryTempBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> notInStockCategoryTempBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		
		Map<String, RegistryCategoryMapVO> getCategoryForRegistry = getCatalogTools().getCategoryForRegistry(registryTypeId);
		
		if (sortSequence
				.contentEquals(BBBGiftRegistryConstants.DEFAULT_CAT_SORT_SEQ)) {
		withDefaultCategory(pRequest, registryTypeId, mSkuRegItemVOMap, skuIds,
				categoryBuckets, inStockCategoryBuckets,
				notInStockCategoryBuckets, getCategoryForRegistry);
		
			// ordering catering buckets 
				for (String catBucket : getCategoryForRegistry.keySet()) {
					categoryTempBuckets.put(catBucket, categoryBuckets.get(catBucket));
				}
				categoryBuckets = categoryTempBuckets;
				
				for (String catBucket : getCategoryForRegistry.keySet()) {
					inStockCategoryTempBuckets.put(catBucket, inStockCategoryBuckets.get(catBucket));
				}
				inStockCategoryBuckets = inStockCategoryTempBuckets;

				for (String catBucket : getCategoryForRegistry.keySet()) {
					notInStockCategoryTempBuckets.put(catBucket, notInStockCategoryBuckets.get(catBucket));
				}
				notInStockCategoryBuckets = notInStockCategoryTempBuckets;

				} else if (sortSequence
				.contentEquals(BBBGiftRegistryConstants.PRICE_SORT_SEQ)) {
				try {
					withDefaultPrice(pRequest, registryTypeId, mSkuRegItemVOMap,
							skuIdPriceMap, categoryBuckets, inStockCategoryBuckets,
							notInStockCategoryBuckets);
				} catch (RepositoryException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException from getlistRegistryItemVO() of MxRegistryItemsDisplayDroplet",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1080),e);

				}

		} else {
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		}
		pRequest.setParameter(
				BBBGiftRegistryConstants.CATEGORY_BUCKETS,
				categoryBuckets);
		pRequest.setParameter(
				BBBGiftRegistryConstants.INSTOCK_CATEGORY_BUCKETS,
				inStockCategoryBuckets);
		pRequest.setParameter(
				BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_BUCKETS,
				notInStockCategoryBuckets);
		pRequest.setParameter(BBBGiftRegistryConstants.COUNT,
				listRegistryItemVO.size());
		pRequest.setParameter(
				BBBGiftRegistryConstants.SORT_SEQUENCE,
				sortSequence);
		pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
				pResponse);
	}

	private void withDefaultPrice(final DynamoHttpServletRequest pRequest,
			String registryTypeId,
			final Map<String, RegistryItemVO> mSkuRegItemVOMap,
			final Map<String, Double> skuIdPriceMap,
			Map<String, List<RegistryItemVO>> categoryBuckets,
			Map<String, List<RegistryItemVO>> inStockCategoryBuckets,
			Map<String, List<RegistryItemVO>> notInStockCategoryBuckets)
			throws BBBSystemException, BBBBusinessException, RepositoryException {
		List<SKUDetailVO> pSVos;
		List<String> pSkuIds;
		RegistryItemVO reg;
		List<RegistryItemVO> pList;
		List<RegistryItemVO> pInStockRegVOList;
		List<RegistryItemVO> pNotInStockRegVOList;
		Map<String, List<SKUDetailVO>> map;
		// Get the list of Price Ranges from Catalog.
		List<String> priceRangeList = getCatalogTools()
				.getMxPriceRanges(registryTypeId);
		pRequest.setParameter(
				BBBGiftRegistryConstants.PRICE_RANGE_LIST,
				priceRangeList);

		final String siteId = pRequest.getParameter(SITE_ID_PARAM);
		final Profile profile = (Profile) pRequest.getObjectParameter(PROFILE);
		final String mxConversionValue = pRequest.getParameter("mxConversionValue");
		
		// Catalog API call to get
		// Map<PriceRange,List<SKUDetailVO>
		map = getCatalogTools().sortMxSkubyRegistry(null,
				skuIdPriceMap, registryTypeId, siteId, null, mxConversionValue);
		// System.out.println("Result Map: " + map);

		// Iterate through the keyset of map to look for each
		// category name
		for (String key : map.keySet()) {
			pSVos = map.get(key);

			pList = new ArrayList<RegistryItemVO>();
			pInStockRegVOList = new ArrayList<RegistryItemVO>();
			pNotInStockRegVOList = new ArrayList<RegistryItemVO>();

			for (SKUDetailVO sVo : pSVos) {
				pSkuIds = new ArrayList<String>();
				pSkuIds.add(sVo.getSkuId());
				reg = new RegistryItemVO();
				reg = mSkuRegItemVOMap.get(sVo.getSkuId());
				String productId = getCatalogTools().getParentProductForSku(sVo.getSkuId());
				RepositoryItem price = null;
				Object priceList;
				PriceListManager plManager = getPriceListManager();			
				if(sVo.getSkuId() != null){
					try {
						priceList = plManager.getPriceList(profile, getProfilePriceListPropertyName());
						if(sVo.getSkuId() instanceof String){
		                    price = plManager.getPrice((RepositoryItem)priceList, productId, sVo.getSkuId());
						}
					} catch (PriceListException e) {
						logError(LogMessageFormatter.formatMessage(pRequest, 
								"PriceListException from withDefaultPrice of MxRegistryItemsDisplayDroplet ", 
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1081),e);
					}
				}
				//String itemPrice = String.valueOf(price.getPropertyValue("listPrice"));
				//reg.setPrice(itemPrice);
				if(reg != null){
					reg.setPrice(String.valueOf(reg.getJdaRetailPrice()));
					reg.setsKUDetailVO(sVo);
	
					// API call for Inventory status
					boolean isBelowLine = this.getCatalogTools()
							.isSKUBelowLine(siteId, sVo.getSkuId());
					reg.setIsBelowLineItem(String
							.valueOf(isBelowLine));
					pInStockRegVOList.add(reg);
					pList.add(reg);

				}
		}
			
			Collections.sort(pList, new PriceListComparator());
			
				categoryBuckets.put(key, pList);
			if (pInStockRegVOList.isEmpty()) {
				inStockCategoryBuckets.put(key, null);
			} else {
				Collections.sort(pInStockRegVOList, new PriceListComparator());
				inStockCategoryBuckets.put(key,
						pInStockRegVOList);
			}
			if (pNotInStockRegVOList.isEmpty()) {
				notInStockCategoryBuckets.put(key, null);
			} else {
				Collections.sort(pNotInStockRegVOList, new PriceListComparator());
				notInStockCategoryBuckets.put(key,
						pNotInStockRegVOList);
			}
		}
		getBucket(categoryBuckets, inStockCategoryBuckets,
				notInStockCategoryBuckets, priceRangeList);
		String emptyFlag = "true";
		for (String bucket : notInStockCategoryBuckets.keySet()) {
			List<RegistryItemVO> pUnList = notInStockCategoryBuckets
					.get(bucket);
			if (pUnList != null) {
				emptyFlag = "false";
			}
		}
		pRequest.setParameter("emptyOutOfStockListFlag",
				emptyFlag);
		// System.out.println("Complete Buckets: " +
		// categoryBuckets);
	}

	private void withDefaultCategory(final DynamoHttpServletRequest pRequest,
			String registryTypeId,
			final Map<String, RegistryItemVO> mSkuRegItemVOMap,
			final List<String> skuIds,
			Map<String, List<RegistryItemVO>> categoryBuckets,
			Map<String, List<RegistryItemVO>> inStockCategoryBuckets,
			Map<String, List<RegistryItemVO>> notInStockCategoryBuckets,
			Map<String, RegistryCategoryMapVO> getCategoryForRegistry)
			throws BBBSystemException, BBBBusinessException {
		List<SKUDetailVO> pSVos;
		List<RegistryItemVO> pList;
		List<RegistryItemVO> pInStockRegVOList;
		List<RegistryItemVO> pNotInStockRegVOList;
		Map<String, List<SKUDetailVO>> map;
		pRequest.setParameter(
					BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO,
					getCategoryForRegistry);
		final String siteId = pRequest.getParameter(SITE_ID_PARAM);
		
			map = getCatalogTools().sortSkubyRegistry(skuIds, null,
					registryTypeId, siteId,
					BBBCatalogConstants.CATEGORY_SORT_TYPE,null);
				for (String key : map.keySet()) {
				pSVos = map.get(key);

				pList = new ArrayList<RegistryItemVO>();
				pInStockRegVOList = new ArrayList<RegistryItemVO>();
				pNotInStockRegVOList = new ArrayList<RegistryItemVO>();
			for (SKUDetailVO sVo : pSVos) {
					skuDetailsVO(mSkuRegItemVOMap, pList, pInStockRegVOList,
							pNotInStockRegVOList, sVo, siteId);
				}
				categoryBuckets.put(key, pList);
				if (pInStockRegVOList.isEmpty()) {
					inStockCategoryBuckets.put(key, null);
				} else {
					inStockCategoryBuckets.put(key,
							pInStockRegVOList);
				}
				if (pNotInStockRegVOList.isEmpty()) {
					notInStockCategoryBuckets.put(key, null);
				} else {
					notInStockCategoryBuckets.put(key,
							pNotInStockRegVOList);
				}
			}
			for (String catBucket : getCategoryForRegistry.keySet()) {
				if (!categoryBuckets.containsKey(catBucket)) {
					categoryBuckets.put(catBucket, null);
				}
				if (!inStockCategoryBuckets.containsKey(catBucket)) {
					inStockCategoryBuckets.put(catBucket, null);
				}
				if (!notInStockCategoryBuckets
						.containsKey(catBucket)) {
					notInStockCategoryBuckets.put(catBucket, null);
				}
			}
			String emptyFlag = "true";
			for (String bucket : notInStockCategoryBuckets.keySet()) {
				final List<RegistryItemVO> pUnList = notInStockCategoryBuckets
						.get(bucket);
				if (pUnList != null) {
					emptyFlag = "false";
				}
			}
			pRequest.setParameter("emptyOutOfStockListFlag",
					emptyFlag);
	}

	private StringBuilder checkPrice(StringBuilder skuTempList,
			final List<String> skuIds, final Map<String, Double> skuIdPriceMap,
			Iterator<RegistryItemVO> it) throws BBBBusinessException,
			BBBSystemException {
		String salePrice;
		String listPrice;
		String nullString = "null";
		RegistryItemVO registryItemVO = (RegistryItemVO) it
				.next();
	
		String productId = getCatalogTools().getParentProductForSku(
				String.valueOf(registryItemVO.getSku()), true);
		
		if(productId == null){
			logDebug("CLS=[MxRegistryItemsDisplayDroplet]/" +
					"Mthd=[CheckPrice]/MSG=[Parent productId null for SKU]=]"+registryItemVO.getSku());
			return skuTempList ;
		}
		
		if(registryItemVO.getJdaRetailPrice()>0)
		{
			salePrice = String
					.valueOf(registryItemVO.getJdaRetailPrice());
			listPrice = String
					.valueOf(registryItemVO.getJdaRetailPrice());
		}
		else
		{		
			salePrice = String
					.valueOf(getCatalogTools()
							.getSalePrice(
									productId,
									String.valueOf(registryItemVO
											.getSku())));
			listPrice = String
					.valueOf(getCatalogTools()
							.getListPrice(
									productId,
									String.valueOf(registryItemVO
											.getSku())));
		}
		
		boolean priceFound = false;
		
		if (!nullString.equalsIgnoreCase(salePrice) && !salePrice.equalsIgnoreCase("0.0")) {
			skuIdPriceMap.put(
					String.valueOf(registryItemVO.getSku()),
					Double.valueOf(salePrice));
			priceFound = true;
			
		} else if(!nullString.equalsIgnoreCase(salePrice)){
			skuIdPriceMap.put(
					String.valueOf(registryItemVO.getSku()),
					Double.valueOf(listPrice));
			priceFound = true;
		} 

		//if sku price is deteremined then only add sku, otherwise ignore this sku
		if( priceFound){
			skuIds.add(String.valueOf(registryItemVO.getSku()));
			skuTempList = skuTempList.append(registryItemVO
					.getSku() + ";");
		} else{		
			logDebug("CLS=[MxRegistryItemsDisplayDroplet]/" +
				"Mthd=[CheckPrice]/MSG=[PriceFound = false ] sku="+registryItemVO.getSku());
		}
		return skuTempList;
	}

	private void skuDetailsVO(
			final Map<String, RegistryItemVO> mSkuRegItemVOMap,
			List<RegistryItemVO> pList, List<RegistryItemVO> pInStockRegVOList,
			List<RegistryItemVO> pNotInStockRegVOList, SKUDetailVO sVo, final String siteId)
			throws BBBSystemException, BBBBusinessException {
		List<String> pSkuIds;
		RegistryItemVO reg;
		pSkuIds = new ArrayList<String>();
		pSkuIds.add(sVo.getSkuId());
		reg = new RegistryItemVO();
		// System.out.println("Category: " +key
		// +"Reg Item VO: " +
		// mSkuRegItemVOMap.get(sVo.getSkuId()));
		reg = mSkuRegItemVOMap.get(sVo.getSkuId());
		reg.setsKUDetailVO(sVo);

		// API call for Inventory status
		Boolean isBelowLine = this.getCatalogTools()
				.isSKUBelowLine(siteId, sVo.getSkuId());
		reg.setIsBelowLineItem(String
				.valueOf(isBelowLine));

		pInStockRegVOList.add(reg);

		pList.add(reg);
	}

	private void getBucket(
			final Map<String, List<RegistryItemVO>> categoryBuckets,
			final Map<String, List<RegistryItemVO>> inStockCategoryBuckets,
			final Map<String, List<RegistryItemVO>> notInStockCategoryBuckets,
			List<String> priceRangeList) {
		for (String pRange : priceRangeList) {
			if (!categoryBuckets.containsKey(pRange)) {
				categoryBuckets.put(pRange, null);
			}
			if (!inStockCategoryBuckets.containsKey(pRange)) {
				inStockCategoryBuckets.put(pRange, null);
			}
			if (!notInStockCategoryBuckets.containsKey(pRange)) {
				notInStockCategoryBuckets.put(pRange, null);
			}
		}
	}

	private String checkRegistryType(final String  eventTypeCode,
			String registryTypeId, final Iterator<RegistryTypeVO> iter) {
		while (iter.hasNext()) {
			RegistryTypeVO registryTypeVO = (RegistryTypeVO) iter
					.next();
			if (registryTypeVO.getRegistryCode().equalsIgnoreCase(
					eventTypeCode)) {
				registryTypeId = registryTypeVO.getRegistryTypeId();
			}
		}
		return registryTypeId;
	}

	/**
	 * Gets the gift registry manager.
	 * 
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(
			final GiftRegistryManager pGiftRegistryManager) {
		mGiftRegistryManager = pGiftRegistryManager;
	}

	/**
	 * Gets the registry items list service name.
	 * 
	 * @return the registryItemsListServiceName
	 */
	public String getRegistryItemsListServiceName() {
		return mRegistryItemsListServiceName;
	}

	/**
	 * Sets the registry items list service name.
	 * 
	 * @param registryItemsListServiceName
	 *            the registryItemsListServiceName to set
	 */
	public void setRegistryItemsListServiceName(
			final String registryItemsListServiceName) {
		this.mRegistryItemsListServiceName = registryItemsListServiceName;
	}

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
	
	/**
	 * if the item is not exist in the content remove from the list
	 * @param listRegistryItemVO
	 * @return
	 */
	public List<RegistryItemVO> fliterNotAvliableItem(List<RegistryItemVO> listRegistryItemVO )
	{
		if (null != listRegistryItemVO && listRegistryItemVO.size() > 0) {
			for (int index = listRegistryItemVO.size() - 1; index >= 0; --index) {

				RegistryItemVO registryItemVO = (RegistryItemVO) listRegistryItemVO
						.get(index);
				try{
				getCatalogTools().getParentProductForSku(
						String.valueOf(registryItemVO.getSku()), true);
				}
				catch (Exception exception)
				{
					listRegistryItemVO.remove(index);
				}
			}
			
		}
		return listRegistryItemVO;
	}
	

	/**
	 * @return the profilePriceListPropertyName
	 */
	public String getProfilePriceListPropertyName() {
		return profilePriceListPropertyName;
	}

	/**
	 * @param profilePriceListPropertyName the profilePriceListPropertyName to set
	 */
	public void setProfilePriceListPropertyName(String profilePriceListPropertyName) {
		this.profilePriceListPropertyName = profilePriceListPropertyName;
	}

	/**
	 * @return the priceListManager
	 */
	public PriceListManager getPriceListManager() {
		return priceListManager;
	}

	/**
	 * @param priceListManager the priceListManager to set
	 */
	public void setPriceListManager(PriceListManager priceListManager) {
		this.priceListManager = priceListManager;
	}
	
	/**
	 * @return the topRegMaxCount
	 */
	public String getTopRegMaxCount() {
		return topRegMaxCount;
	}

	/**
	 * @param topRegMaxCount the topRegMaxCount to set
	 */
	public void setTopRegMaxCount(String topRegMaxCount) {
		this.topRegMaxCount = topRegMaxCount;
	}
	
	/** The Constant REGISTRY_ID */
	public static final ParameterName REGISTRY_ID = ParameterName
			.getParameterName( BBBGiftRegistryConstants.REGISTRY_ID );

	/** The parameter for sort seq. */
	public static final ParameterName SORT_SEQ = ParameterName
			.getParameterName(BBBGiftRegistryConstants.SORT_SEQ);

	/** The Constant VIEW. */
	public static final ParameterName VIEW = ParameterName
			.getParameterName( BBBGiftRegistryConstants.VIEW );

	/** The Constant REG_EVENT_TYPE_CODE */
	public static final ParameterName REG_EVENT_TYPE_CODE = ParameterName
			.getParameterName(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE);

	/** The Constant START_INDEX */
	public static final ParameterName START_INDEX = ParameterName
			.getParameterName(BBBGiftRegistryConstants.START_INDEX);

	/** The Constant BULK_SIZE*/
	public static final ParameterName BULK_SIZE = ParameterName
			.getParameterName(BBBGiftRegistryConstants.BULK_SIZE);

	/** The Constant IS_GIFT_GIVER*/
	public static final ParameterName IS_GIFT_GIVER = ParameterName
			.getParameterName(BBBGiftRegistryConstants.IS_GIFT_GIVER);
	
	/** The Constant IS_AVAIL_WEBPUR*/
	public static final ParameterName IS_AVAIL_WEBPUR = ParameterName
			.getParameterName(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR);
	
	/** The Constant SITE_ID*/
	public static final ParameterName SITE_ID_PARAM = ParameterName
			.getParameterName(SITE_ID);
	
	/** The Constant profile*/
	public static final ParameterName PROFILE = ParameterName
			.getParameterName("profile");
	

}