package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.multisite.SiteContext;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileManager;
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
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * The class is the extension of BBBPresentationDroplet which is again extension
 * og the ATG DynamoServlet. The class is responsible for rendering the content
 * for Gift Registry Flyouts in reg_flyout.jsp. The class presents content based
 * on whether the user is authenticated and number of registries the user owns
 * 
 * @author sku134
 * 
 */
public class PrimeGiftRegistryInfoDroplet extends BBBPresentationDroplet
{

	/** The Gift registry manager. */
	private GiftRegistryManager	mGiftRegistryManager;

	private static final String	SITE_ID					= "siteId";
	private static final String	SITE_ID_HEADER			= "X-bbb-site-id";
	private static final String	REG_SUMMARY_KEY_CONST	= "_REG_SUMMARY";

	/** The Registry items list service name. */
	private String				mRegistryItemsListServiceName;

	private PriceListManager	priceListManager;

	private String				profilePriceListPropertyName;

	/** The Catalog tools. */
	private BBBCatalogTools		mCatalogTools;

	/** The Constant ERR_REGINFO_FATAL_ERROR. */
	private static final String	PLUS					= "+";
	private static final String	DOLLAR					= "$";
	private static final String	HYPHEN					= "-";

	private int					mCertonaListMaxCount;

	private String				topRegMaxCount;

	private SiteContext			siteContext;

	/**
	 * @return the mCertonaListMaxCount
	 */
	public final int getCertonaListMaxCount()
	{
		return mCertonaListMaxCount;
	}

	/**
	 * @param mCertonaListMaxCount
	 *            the mCertonaListMaxCount to set
	 */
	public final void setCertonaListMaxCount(int pCertonaListMaxCount)
	{
		this.mCertonaListMaxCount = pCertonaListMaxCount;
	}

	/**
	 * This droplet's service method is used to prime data for an owner's
	 * registry to enhance performance for registry landing page. This code is
	 * called as a post login process and data related to user who logged in is
	 * primed for subsequent view s by user.
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
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException
	{
		BBBPerformanceMonitor.start("PrimeGiftRegistryInfoDroplet", "service");
		logDebug(" PrimeGiftRegistryInfoDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		try
		{
			this.logDebug("Priming of giftregistry data will be done as RegItemWSCall key value is FALSE ");
			//PS-63264 | Adding transient profile check to avoid priming of transient users
				if (!sessionBean.isPrimeRegistryCompleted() && !profile.isTransient())
				{
					String siteId = ((SiteContext) pRequest.resolveName("/atg/multisite/SiteContext")).getSite().getId();
					RegistryResVO registryResVO = null;
					RegistryItemsListVO registryItemsListVO = null;
					List<RegistrySkinnyVO> registrySkinnyVOList = getGiftRegistryManager().getAcceptableGiftRegistries(profile, siteId);
					final List<RegistryTypeVO> list = getGiftRegistryManager().fetchRegistryTypes(siteId);
					String registryTypeId = null;
					Map<String, Map<String, RegistryCategoryMapVO>> getCategoryForAllRegistryMap = new HashMap<String, Map<String, RegistryCategoryMapVO>>();
					Map<String, RegistryCategoryMapVO> getCategoryForRegistry = null;
					String[] sortSeq = { "1", "2" };
					logDebug("All registry items are fetched as registryskinnyVOList for which data is to be primed " + registrySkinnyVOList);
					for (RegistrySkinnyVO vo : registrySkinnyVOList)
					{
						logDebug("Priming started for registry id : " + vo.getRegistryId());
						registryItemsListVO = getGiftRegistryManager().fetchRegistryItemsFromEcomAdmin(vo.getRegistryId(), false, BBBCoreConstants.VIEW_ALL);
						registryTypeId = checkRegistryType(vo.getEventCode(), registryTypeId, list.iterator());
						getCategoryForRegistry = getCatalogTools().getCategoryForRegistry(registryTypeId);
						registryResVO = getGiftRegistryManager().getRegistryInfoFromEcomAdmin(vo.getRegistryId(), siteId);
						sessionBean.getValues().put(vo.getRegistryId() + REG_SUMMARY_KEY_CONST, registryResVO);
						getCategoryForAllRegistryMap.put(registryTypeId, getCategoryForRegistry);
						if (registryItemsListVO != null)
						{
							for (String sortby : sortSeq)
							{
								if (BBBGiftRegistryConstants.DEFAULT_CAT_SORT_SEQ.equals(sortby))
								{
									processItemList(pRequest, pResponse, registryTypeId, sortby, registryItemsListVO);
								}
								else if (BBBGiftRegistryConstants.PRICE_SORT_SEQ.equals(sortby))
								{
									processItemList(pRequest, pResponse, registryTypeId, sortby, registryItemsListVO);
								}
							}

							logDebug("Priming completed for registry id : " + vo.getRegistryId());
						}
					}

					logDebug("set registrySkinnyVOList  output to the display page");
					pRequest.serviceLocalParameter("output", pRequest, pResponse);
					logDebug("Priming completed for registry successfully for all items");
				}
			
		}
		catch (Exception e)
		{
			logError(LogMessageFormatter.formatMessage(pRequest, "Exception from service of PrimeGiftRegistryInfoDroplet",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
			pRequest.setParameter(OUTPUT_ERROR_MSG, "err_regsearch_biz_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}
		sessionBean.setPrimeRegistryCompleted(true);
		logDebug(" PrimeGiftRegistryInfoDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends");
		BBBPerformanceMonitor.end("PrimeGiftRegistryInfoDroplet", "service");
	}

	/**
	 * generate the j docs
	 */
	public boolean primeGiftRegistryInfo()
	{
		boolean isPrimingDone = false;
		BBBPerformanceMonitor.start("PrimeGiftRegistryInfoDroplet", "service");
		logDebug(" PrimeGiftRegistryInfoDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		
		
			this.logDebug("Priming of giftregistry data will be done as RegItemWSCall key value is FALSE ");
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
			String siteId = this.getSiteContext().getSite().getId();
			if (null == siteId)
			{
				siteId = pRequest.getHeader(SITE_ID_HEADER);
			}
			// String registryId = pRequest.getParameter(REG_ID);
			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			try
			{
				if (pRequest != null)
				{
					Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
					// final String siteID =
					// getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
					// siteId).get(0);
					// if (registryId == null)
					// {
					RegistryItemsListVO registryItemsListVO = null;
					RegistryResVO registryResVO = null;
					List<RegistrySkinnyVO> registrySkinnyVOList = getGiftRegistryManager().getAcceptableGiftRegistries(profile, siteId);
					final List<RegistryTypeVO> list = getGiftRegistryManager().fetchRegistryTypes(siteId);
					String registryTypeId = null;
					Map<String, Map<String, RegistryCategoryMapVO>> getCategoryForAllRegistryMap = new HashMap<String, Map<String, RegistryCategoryMapVO>>();
					Map<String, RegistryCategoryMapVO> getCategoryForRegistry = null;
					String[] sortSeq = { "1", "2" };
					logDebug("All registry items are fetched as registryskinnyVOList for which data is to be primed " + registrySkinnyVOList);
					synchronized(registrySkinnyVOList) {
						for (RegistrySkinnyVO vo : registrySkinnyVOList)
						{
							logDebug("Priming started for registry id : " + vo.getRegistryId());
							registryItemsListVO = getGiftRegistryManager().fetchRegistryItemsFromEcomAdmin(vo.getRegistryId(), false, BBBCoreConstants.VIEW_ALL);
							registryTypeId = checkRegistryType(vo.getEventCode(), registryTypeId, list.iterator());
							getCategoryForRegistry = getCatalogTools().getCategoryForRegistry(registryTypeId);
							registryResVO = getGiftRegistryManager().getRegistryInfoFromEcomAdmin(vo.getRegistryId(), siteId);
							sessionBean.getValues().put(vo.getRegistryId() + REG_SUMMARY_KEY_CONST, registryResVO);
							getCategoryForAllRegistryMap.put(registryTypeId, getCategoryForRegistry);
							if (registryItemsListVO != null)
							{
								for (String sortby : sortSeq)
								{
									if (BBBGiftRegistryConstants.DEFAULT_CAT_SORT_SEQ.equals(sortby))
									{
										processItemList(pRequest, pResponse, registryTypeId, sortby, registryItemsListVO);
									}
									else if (BBBGiftRegistryConstants.PRICE_SORT_SEQ.equals(sortby))
									{
										processItemList(pRequest, pResponse, registryTypeId, sortby, registryItemsListVO);
									}
								}
	
								logDebug("Priming completed for registry id : " + vo.getRegistryId());
							}
						}
					}
					isPrimingDone = true;
					// }
					logDebug("set registrySkinnyVOList  output to the display page");
				}

			}
			catch (Exception e)
			{
				logError("BBBBusinessException from service of PrimeGiftRegistryInfoDroplet", e);
				isPrimingDone = false;
			}
		sessionBean.setPrimeRegistryCompleted(true);
		logDebug(" PrimeGiftRegistryInfoDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends");
		BBBPerformanceMonitor.end("PrimeGiftRegistryInfoDroplet", "service");
		return isPrimingDone;
	}

	private Map<String, Map<String, List<RegistryItemVO>>> processItemList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String registryTypeId, final String sortSequence, RegistryItemsListVO registryItemsListVO)
			throws ServletException, IOException, BBBBusinessException, BBBSystemException
	{

		Map<String, Map<String, List<RegistryItemVO>>> allCatBuckets = null;
		PromoBoxVO promoBoxVO = getCatalogTools().getPromoBoxForRegistry(registryTypeId);

		pRequest.setParameter(BBBGiftRegistryConstants.PROMOBOX, promoBoxVO);

		List<RegistryItemVO> listRegistryItemVO = registryItemsListVO.getRegistryItemList();
		listRegistryItemVO = fliterNotAvliableItem(listRegistryItemVO);
		final Map<String, RegistryItemVO> mSkuRegItemVOMap = registryItemsListVO.getSkuRegItemVOMap();

		StringBuilder skuTempList = new StringBuilder("");

		final List<String> skuIds = new ArrayList<String>();
		Map<String, Double> skuIdPriceMap = new HashMap<String, Double>();
		if (!BBBUtility.isListEmpty(listRegistryItemVO) && !StringUtils.isEmpty(registryTypeId))
		{

			pRequest.setParameter(BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, registryItemsListVO.getTotEntries());
			allCatBuckets = getlistRegistryItemVO(pRequest, pResponse, registryTypeId, sortSequence, listRegistryItemVO, mSkuRegItemVOMap,
					skuTempList, skuIds, skuIdPriceMap);

		}
		else
		{
			pRequest.setParameter(BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 0);
			checkWithNull(pRequest, pResponse, registryTypeId, sortSequence);

		}

		return allCatBuckets;
	}

	private void checkWithNull(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse, String registryTypeId,
			final String sortSequence) throws BBBSystemException, BBBBusinessException, ServletException, IOException
	{
		if (sortSequence.contentEquals(BBBGiftRegistryConstants.PRICE_SORT_SEQ))
		{
			List<String> priceRangeList = getCatalogTools().getPriceRanges(registryTypeId, null);
			//BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
			BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(pRequest);
			String country = (String) sessionBean.getValues()
					.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
			if (!BBBUtility.isEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)
					&& !country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY) && priceRangeList != null)
			{
				List<String> changedPriceRangeList = null;
				changedPriceRangeList = new ArrayList<String>();
				for (String item : priceRangeList)
				{
					String newKey = getNewKeyRange(item);
					changedPriceRangeList.add(newKey);
				}
				priceRangeList = changedPriceRangeList;
			}
			else if (!BBBUtility.isEmpty(country) && country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY))
			{
				priceRangeList = getCatalogTools().getPriceRanges(registryTypeId, country);
			}
			pRequest.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST, priceRangeList);
			Map<String, String> map = new HashMap<String, String>();
			for (String priString : priceRangeList)
			{
				map.put(priString, null);
			}
			pRequest.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS, map);
		}

		else
		{
			Map<String, RegistryCategoryMapVO> getCategoryForRegistry = getCatalogTools().getCategoryForRegistry(registryTypeId);
			// Map<String, String> map = new HashMap<String, String>();
			Map<String, List<RegistryItemVO>> categoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
			for (String catBucket : getCategoryForRegistry.keySet())
			{
				// map.put(catBucket, null) ;
				categoryBuckets.put(catBucket, categoryBuckets.get(catBucket));
			}
			pRequest.setParameter(BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO, getCategoryForRegistry);
			pRequest.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS, categoryBuckets);
		}

		pRequest.setParameter(BBBGiftRegistryConstants.EMPTY_LIST, "true");
		pRequest.setParameter(BBBGiftRegistryConstants.SORT_SEQUENCE, sortSequence);
		pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
	}

	private Map<String, Map<String, List<RegistryItemVO>>> getlistRegistryItemVO(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String registryTypeId, final String sortSequence, List<RegistryItemVO> listRegistryItemVO,
			final Map<String, RegistryItemVO> mSkuRegItemVOMap, StringBuilder skuTempList, final List<String> skuIds,
			Map<String, Double> skuIdPriceMap) throws BBBBusinessException, BBBSystemException, ServletException, IOException
	{

		String skuList, certonaSkuList;
		Iterator<RegistryItemVO> it = listRegistryItemVO.iterator();
		int certonaTopRegCount = 0;
		StringBuilder tempCertonaList = new StringBuilder("");

		String certonaTopRegMax = getCatalogTools().getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, getTopRegMaxCount()).get(0);

		if (!BBBUtility.isEmpty(certonaTopRegMax))
		{
			setCertonaListMaxCount(Integer.parseInt(certonaTopRegMax));
		}

		while (it.hasNext())
		{
			certonaTopRegCount++;
			if (certonaTopRegCount == getCertonaListMaxCount())
				tempCertonaList.append(skuTempList);

			skuTempList = checkPrice(skuTempList, skuIds, skuIdPriceMap, it);
		}
		if (certonaTopRegCount > 0 && certonaTopRegCount < getCertonaListMaxCount())
			tempCertonaList.append(skuTempList);

		if (skuTempList.length() > 0)
		{
			skuList = skuTempList.substring(0, skuTempList.length() - 1);
			certonaSkuList = tempCertonaList.substring(0, tempCertonaList.length() - 1);
			pRequest.setParameter("skuList", skuList);
			pRequest.setParameter("certonaSkuList", certonaSkuList);
		}

		Map<String, Map<String, List<RegistryItemVO>>> allCatBuckets = new HashMap<String, Map<String, List<RegistryItemVO>>>();
		Map<String, List<RegistryItemVO>> categoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> inStockCategoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> notInStockCategoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> categoryTempBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> inStockCategoryTempBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> notInStockCategoryTempBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();

		Map<String, RegistryCategoryMapVO> getCategoryForRegistry = getCatalogTools().getCategoryForRegistry(registryTypeId);

		if (sortSequence.contentEquals(BBBGiftRegistryConstants.DEFAULT_CAT_SORT_SEQ))
		{
			withDefaultCategory(pRequest, registryTypeId, mSkuRegItemVOMap, skuIds, categoryBuckets, inStockCategoryBuckets,
					notInStockCategoryBuckets, getCategoryForRegistry);

			// ordering catering buckets
			for (String catBucket : getCategoryForRegistry.keySet())
			{
				categoryTempBuckets.put(catBucket, categoryBuckets.get(catBucket));
			}
			categoryBuckets = categoryTempBuckets;

			for (String catBucket : getCategoryForRegistry.keySet())
			{
				inStockCategoryTempBuckets.put(catBucket, inStockCategoryBuckets.get(catBucket));
			}
			inStockCategoryBuckets = inStockCategoryTempBuckets;

			for (String catBucket : getCategoryForRegistry.keySet())
			{
				notInStockCategoryTempBuckets.put(catBucket, notInStockCategoryBuckets.get(catBucket));
			}
			notInStockCategoryBuckets = notInStockCategoryTempBuckets;

		}
		else if (sortSequence.contentEquals(BBBGiftRegistryConstants.PRICE_SORT_SEQ))
		{
			try
			{
				withDefaultPrice(pRequest, registryTypeId, mSkuRegItemVOMap, skuIdPriceMap, categoryBuckets, inStockCategoryBuckets,
						notInStockCategoryBuckets);
			}
			catch (RepositoryException e)
			{
				logError(LogMessageFormatter.formatMessage(pRequest,
						"RepositoryException from getlistRegistryItemVO() of RegistryItemsDisplayDroplet",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1080), e);

			}

		}
		else
		{
			pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
		}

		allCatBuckets.put(BBBGiftRegistryConstants.CATEGORY_BUCKETS, categoryBuckets);
		allCatBuckets.put(BBBGiftRegistryConstants.INSTOCK_CATEGORY_BUCKETS, inStockCategoryBuckets);
		allCatBuckets.put(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_BUCKETS, notInStockCategoryBuckets);
		// pRequest.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,
		// categoryBuckets);
		// pRequest.setParameter(BBBGiftRegistryConstants.INSTOCK_CATEGORY_BUCKETS,
		// inStockCategoryBuckets);
		// pRequest.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_BUCKETS,
		// notInStockCategoryBuckets);
		// pRequest.setParameter(BBBGiftRegistryConstants.COUNT,
		// listRegistryItemVO.size());
		// pRequest.setParameter(BBBGiftRegistryConstants.SORT_SEQUENCE,
		// sortSequence);
		return allCatBuckets;
	}

	private void withDefaultPrice(final DynamoHttpServletRequest pRequest, String registryTypeId, Map<String, RegistryItemVO> mSkuRegItemVOMap,
			final Map<String, Double> skuIdPriceMap, Map<String, List<RegistryItemVO>> categoryBuckets,
			Map<String, List<RegistryItemVO>> inStockCategoryBuckets, Map<String, List<RegistryItemVO>> notInStockCategoryBuckets)
			throws BBBSystemException, BBBBusinessException, RepositoryException
	{

		List<SKUDetailVO> pSVos;
		List<String> pSkuIds;
		RegistryItemVO reg;
		List<RegistryItemVO> pList;
		List<RegistryItemVO> pInStockRegVOList;
		List<RegistryItemVO> pNotInStockRegVOList;
		Map<String, List<SKUDetailVO>> map;

		//BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(pRequest);
		String country = (String) sessionBean.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
		// Get the list of Price Ranges from Catalog.
		List<String> priceRangeList = getCatalogTools().getPriceRanges(registryTypeId, country);
		if (!BBBUtility.isEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY)
				&& !country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY) && priceRangeList != null)
		{
			List<String> changedPriceRangeList = null;
			changedPriceRangeList = new ArrayList<String>();
			for (String item : priceRangeList)
			{
				String newKey = getNewKeyRange(item);
				changedPriceRangeList.add(newKey);
			}
			priceRangeList = changedPriceRangeList;
		}
		pRequest.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST, priceRangeList);

		String siteId = pRequest.getParameter(SITE_ID_PARAM);
		if (null == siteId)
		{
			siteId = pRequest.getHeader(SITE_ID_HEADER);
		}
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);

		// Catalog API call to get
		// Map<PriceRange,List<SKUDetailVO>
		map = getCatalogTools().sortSkubyRegistry(null, skuIdPriceMap, registryTypeId, siteId, null, country);
		// System.out.println("Result Map: " + map);
		// Iterate through the keyset of map to look for each
		// category name

		// BPSI-1049 Start: enableLTLRegForSite false means LTL item will be
		// below the line and will not be
		// available for Add to Cart while true means it can be added to cart
		String enableLTLRegForSiteValue = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
				BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE).get(0);
		boolean enableLTLRegForSite = false;
		if (BBBUtility.isNotEmpty(enableLTLRegForSiteValue))
		{
			enableLTLRegForSite = Boolean.parseBoolean(enableLTLRegForSiteValue);
		}

		for (String key : map.keySet())
		{
			pSVos = map.get(key);
			String range = key;
			int rangeFirstDollarIndex = range.indexOf(DOLLAR);
			int rangeLastDollarIndex = range.lastIndexOf(DOLLAR);
			int rangeIndexOfHyphen = range.indexOf(HYPHEN);
			int indexOfPlus = range.indexOf(PLUS);
			String lastRange = null;
			String rangeMin = null;
			String rangeMax = null;
			if (indexOfPlus > -1)
			{
				lastRange = range.substring(rangeFirstDollarIndex + 1, indexOfPlus - 1);
			}
			else if (rangeFirstDollarIndex > -1 && rangeLastDollarIndex > -1 && rangeIndexOfHyphen > -1)
			{
				rangeMin = range.substring(rangeFirstDollarIndex + 1, rangeIndexOfHyphen - 1);
				rangeMax = range.substring(rangeLastDollarIndex + 1);
			}

			pList = new ArrayList<RegistryItemVO>();
			pInStockRegVOList = new ArrayList<RegistryItemVO>();
			pNotInStockRegVOList = new ArrayList<RegistryItemVO>();

			for (SKUDetailVO sVo : pSVos)
			{
				pSkuIds = new ArrayList<String>();
				pSkuIds.add(sVo.getSkuId());
				reg = new RegistryItemVO();
				Iterator<String> mapIterator = mSkuRegItemVOMap.keySet().iterator();
				String key_delimeter = "_";
				while (mapIterator.hasNext())
				{
					String skuMapkey = mapIterator.next();
					Double personalizedPrice = mSkuRegItemVOMap.get(skuMapkey).getPersonlisedPrice();
					int indexOfDelimeter = skuMapkey.indexOf(key_delimeter);
					String skuIDFromKey = skuMapkey;
					if (indexOfDelimeter > -1)
					{
						skuIDFromKey = skuMapkey.substring(0, indexOfDelimeter);
						if (sVo.getSkuId().equalsIgnoreCase(skuIDFromKey))
						{
							if (null != lastRange && !StringUtils.isEmpty(lastRange))
							{
								if (personalizedPrice >= Double.parseDouble(lastRange))
								{
									reg = mSkuRegItemVOMap.get(skuMapkey);
									mSkuRegItemVOMap.remove(skuMapkey);
									break;
								}
							}
							else if (personalizedPrice >= Double.parseDouble(rangeMin) && personalizedPrice <= Double.parseDouble(rangeMax))
							{
								reg = mSkuRegItemVOMap.get(skuMapkey);
								mSkuRegItemVOMap.remove(skuMapkey);
								break;
							}
						}
					}
					else if (sVo.getSkuId().equalsIgnoreCase(skuIDFromKey))
					{

						reg = mSkuRegItemVOMap.get(skuIDFromKey);
					}

				}
				String productId = getCatalogTools().getParentProductForSku(sVo.getSkuId());
				RepositoryItem price = null;
				Object priceList;
				PriceListManager plManager = getPriceListManager();
				if (sVo.getSkuId() != null)
				{
					try
					{
						priceList = plManager.getPriceList(profile, getProfilePriceListPropertyName());
						if (sVo.getSkuId() instanceof String)
						{
							price = plManager.getPrice((RepositoryItem) priceList, productId, sVo.getSkuId());
						}
					}
					catch (PriceListException e)
					{
						logError(LogMessageFormatter.formatMessage(pRequest,
								"PriceListException from withDefaultPrice of RegistryItemsDisplayDroplet ",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1081), e);
					}
				}
				if(null != price){
					String itemPrice = String.valueOf(price.getPropertyValue("listPrice"));
					reg.setPrice(itemPrice);
				}
				

				reg.setsKUDetailVO(sVo);

				// API call for Inventory status
				boolean isBelowLine = this.getCatalogTools().isSKUBelowLine(siteId, sVo.getSkuId());
				reg.setIsBelowLineItem(String.valueOf(isBelowLine));

				if (!isBelowLine)
				{
					pInStockRegVOList.add(reg);
				}
				else
				{
					pNotInStockRegVOList.add(reg);
				}

				pList.add(reg);
			}

			Collections.sort(pList, new PriceListComparator());
			if (!BBBUtility.isEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY)
					&& !country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY) && priceRangeList != null)
			{
				key = getNewKeyRange(key);
			}
			categoryBuckets.put(key, pList);
			if (pInStockRegVOList.isEmpty())
			{
				inStockCategoryBuckets.put(key, null);
			}
			else
			{
				Collections.sort(pInStockRegVOList, new PriceListComparator());
				inStockCategoryBuckets.put(key, pInStockRegVOList);
			}
			if (pNotInStockRegVOList.isEmpty())
			{
				notInStockCategoryBuckets.put(key, null);
			}
			else
			{
				Collections.sort(pNotInStockRegVOList, new PriceListComparator());
				notInStockCategoryBuckets.put(key, pNotInStockRegVOList);
			}
		}
		getBucket(categoryBuckets, inStockCategoryBuckets, notInStockCategoryBuckets, priceRangeList);
		String emptyFlag = "true";
		for (String bucket : notInStockCategoryBuckets.keySet())
		{
			List<RegistryItemVO> pUnList = notInStockCategoryBuckets.get(bucket);
			if (pUnList != null)
			{
				emptyFlag = "false";
			}
		}

		pRequest.setParameter("emptyOutOfStockListFlag", emptyFlag);
		// System.out.println("Complete Buckets: " +
		// categoryBuckets);
	}

	public String getNewKeyRange(String value)
	{
		Properties pAttributes = new Properties();
		String newKey = null;
		Pattern p = Pattern.compile(BBBCoreConstants.PATTERN_FORMAT);
		Matcher m = null;
		m = p.matcher(value);
		int i = 0;
		String minPrice = BBBCoreConstants.BLANK, maxPrice = BBBCoreConstants.BLANK;
		while (m.find())
		{
			if (i == 0)
			{
				minPrice = m.group();
				minPrice = minPrice.substring(1);
				i++;
			}
			else if (i == 1)
			{
				maxPrice = m.group();
				maxPrice = maxPrice.substring(1);
			}
		}
		pAttributes.setProperty(BBBInternationalShippingConstants.ROUND, BBBInternationalShippingConstants.DOWN);
		newKey = BBBUtility.convertToInternationalPrice(value, minPrice, maxPrice, pAttributes);

		return newKey;
	}

	private void withDefaultCategory(final DynamoHttpServletRequest pRequest, String registryTypeId, Map<String, RegistryItemVO> mSkuRegItemVOMap,
			final List<String> skuIds, Map<String, List<RegistryItemVO>> categoryBuckets, Map<String, List<RegistryItemVO>> inStockCategoryBuckets,
			Map<String, List<RegistryItemVO>> notInStockCategoryBuckets, Map<String, RegistryCategoryMapVO> getCategoryForRegistry)
			throws BBBSystemException, BBBBusinessException
	{

		List<SKUDetailVO> pSVos;
		List<RegistryItemVO> pList;
		List<RegistryItemVO> pInStockRegVOList;
		List<RegistryItemVO> pNotInStockRegVOList;
		Map<String, List<SKUDetailVO>> map;
		pRequest.setParameter(BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO, getCategoryForRegistry);

		String siteId = pRequest.getParameter(SITE_ID_PARAM);
		if (null == siteId)
		{
			siteId = pRequest.getHeader(SITE_ID_HEADER);
		}
		// BPSI-1049 Start: enableLTLRegForSite false means LTL item will be
		// below the line and will not be
		// available for Add to Cart while true means it can be added to cart
		String enableLTLRegForSiteValue = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
				BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE).get(0);
		boolean enableLTLRegForSite = false;
		if (BBBUtility.isNotEmpty(enableLTLRegForSiteValue))
		{
			enableLTLRegForSite = Boolean.parseBoolean(enableLTLRegForSiteValue);
		}

		map = getCatalogTools().sortSkubyRegistry(skuIds, null, registryTypeId, siteId, BBBCatalogConstants.CATEGORY_SORT_TYPE, null);

		// sessionBean.getValues().put(map, registryId+"_SORTED_SKU_MAP");
		for (String key : map.keySet())
		{
			pSVos = map.get(key);
			pList = new ArrayList<RegistryItemVO>();
			pInStockRegVOList = new ArrayList<RegistryItemVO>();
			pNotInStockRegVOList = new ArrayList<RegistryItemVO>();
			for (SKUDetailVO sVo : pSVos)
			{
				mSkuRegItemVOMap = skuDetailsVO(mSkuRegItemVOMap, pList, pInStockRegVOList, pNotInStockRegVOList, sVo, siteId, enableLTLRegForSite);
			}
			categoryBuckets.put(key, pList);
			if (pInStockRegVOList.isEmpty())
			{
				inStockCategoryBuckets.put(key, null);
			}
			else
			{
				inStockCategoryBuckets.put(key, pInStockRegVOList);
			}
			if (pNotInStockRegVOList.isEmpty())
			{
				notInStockCategoryBuckets.put(key, null);
			}
			else
			{
				notInStockCategoryBuckets.put(key, pNotInStockRegVOList);
			}
		}
		for (String catBucket : getCategoryForRegistry.keySet())
		{
			if (!categoryBuckets.containsKey(catBucket))
			{
				categoryBuckets.put(catBucket, null);
			}
			if (!inStockCategoryBuckets.containsKey(catBucket))
			{
				inStockCategoryBuckets.put(catBucket, null);
			}
			if (!notInStockCategoryBuckets.containsKey(catBucket))
			{
				notInStockCategoryBuckets.put(catBucket, null);
			}
		}
		String emptyFlag = "true";
		for (String bucket : notInStockCategoryBuckets.keySet())
		{
			final List<RegistryItemVO> pUnList = notInStockCategoryBuckets.get(bucket);
			if (pUnList != null)
			{
				emptyFlag = "false";
			}
		}
		pRequest.setParameter("emptyOutOfStockListFlag", emptyFlag);

	}

	private StringBuilder checkPrice(StringBuilder skuTempList, final List<String> skuIds, Map<String, Double> skuIdPriceMap,
			Iterator<RegistryItemVO> it) throws BBBBusinessException, BBBSystemException
	{
		String salePrice;
		String listPrice;
		RegistryItemVO registryItemVO = (RegistryItemVO) it.next();

		String productId = getCatalogTools().getParentProductForSku(String.valueOf(registryItemVO.getSku()), true);

		if (productId == null)
		{
			logDebug("CLS=[RegistryItemsDisplayDroplet]/" + "Mthd=[CheckPrice]/MSG=[Parent productId null for SKU]=]" + registryItemVO.getSku());
			return skuTempList;
		}

		if (registryItemVO.getJdaRetailPrice() > 0)
		{
			salePrice = String.valueOf(registryItemVO.getJdaRetailPrice());
			listPrice = String.valueOf(registryItemVO.getJdaRetailPrice());
		}
		else
		{
			salePrice = String.valueOf(getCatalogTools().getSalePrice(productId, String.valueOf(registryItemVO.getSku())));
			listPrice = String.valueOf(getCatalogTools().getListPrice(productId, String.valueOf(registryItemVO.getSku())));
		}

		boolean priceFound = false;
		if (!StringUtils.isEmpty(registryItemVO.getPersonalisedCode()))
		{
			StringBuilder str = new StringBuilder(String.valueOf(registryItemVO.getSku()));
			str.append("_");
			String key = str.append(String.valueOf(String.valueOf(registryItemVO.getRefNum()))).toString();
			skuIdPriceMap.put(key, registryItemVO.getPersonlisedPrice());
			priceFound = true;
		}
		else
		{
			if (salePrice != null && !salePrice.equalsIgnoreCase("0.0"))
			{
				skuIdPriceMap.put(String.valueOf(registryItemVO.getSku()), Double.valueOf(salePrice));
				priceFound = true;

			}
			else if (listPrice != null)
			{
				skuIdPriceMap.put(String.valueOf(registryItemVO.getSku()), Double.valueOf(listPrice));
				priceFound = true;
			}
		}

		// if sku price is deteremined then only add sku, otherwise ignore this
		// sku
		if (priceFound)
		{
			skuIds.add(String.valueOf(registryItemVO.getSku()));
			skuTempList = skuTempList.append(registryItemVO.getSku() + ";");
		}
		else
		{
			logDebug("CLS=[RegistryItemsDisplayDroplet]/" + "Mthd=[CheckPrice]/MSG=[PriceFound = false ] sku=" + registryItemVO.getSku());
		}

		return skuTempList;
	}

	private Map<String, RegistryItemVO> skuDetailsVO(Map<String, RegistryItemVO> mSkuRegItemVOMap, List<RegistryItemVO> pList,
			List<RegistryItemVO> pInStockRegVOList, List<RegistryItemVO> pNotInStockRegVOList, SKUDetailVO sVo, final String siteId,
			final boolean enableLTLRegForSite) throws BBBSystemException, BBBBusinessException
	{

		List<String> pSkuIds;
		RegistryItemVO reg;
		pSkuIds = new ArrayList<String>();
		pSkuIds.add(sVo.getSkuId());
		reg = new RegistryItemVO();
		// System.out.println("Category: " +key
		// +"Reg Item VO: " +
		// mSkuRegItemVOMap.get(sVo.getSkuId()));
		Iterator<String> mapIterator = mSkuRegItemVOMap.keySet().iterator();
		String key_delimeter = "_";
		while (mapIterator.hasNext())
		{
			String key = mapIterator.next();
			int indexOfDelimeter = key.indexOf(key_delimeter);
			String skuIDFromKey = key;
			if (indexOfDelimeter > -1)
			{
				skuIDFromKey = key.substring(0, indexOfDelimeter);
				if (sVo.getSkuId().equalsIgnoreCase(skuIDFromKey))
				{
					reg = mSkuRegItemVOMap.get(key);
					mSkuRegItemVOMap.remove(key);
					break;
				}
			}
			else if (sVo.getSkuId().equalsIgnoreCase(skuIDFromKey))
			{
				reg = mSkuRegItemVOMap.get(skuIDFromKey);
			}

		}
		reg.setsKUDetailVO(sVo);
		// API call for Inventory status

		Boolean isBelowLine = this.getCatalogTools().isSKUBelowLine(siteId, sVo.getSkuId());
		// BPSI-1049 Start: enableLTLRegForSite false means LTL item will be
		// below the line and will not be
		// available for Add to Cart while true means it can be added to cart
	
		reg.setIsBelowLineItem(String.valueOf(isBelowLine));
		if (!isBelowLine)
		{
			pInStockRegVOList.add(reg);
		}
		else
		{
			pNotInStockRegVOList.add(reg);
		}

		pList.add(reg);
		return mSkuRegItemVOMap;
	}

	private void getBucket(final Map<String, List<RegistryItemVO>> categoryBuckets, final Map<String, List<RegistryItemVO>> inStockCategoryBuckets,
			final Map<String, List<RegistryItemVO>> notInStockCategoryBuckets, List<String> priceRangeList)
	{
		for (String pRange : priceRangeList)
		{
			if (!categoryBuckets.containsKey(pRange))
			{
				categoryBuckets.put(pRange, null);
			}
			if (!inStockCategoryBuckets.containsKey(pRange))
			{
				inStockCategoryBuckets.put(pRange, null);
			}
			if (!notInStockCategoryBuckets.containsKey(pRange))
			{
				notInStockCategoryBuckets.put(pRange, null);
			}
		}
	}

	private String checkRegistryType(final String eventTypeCode, String registryTypeId, final Iterator<RegistryTypeVO> iter)
	{
		while (iter.hasNext())
		{
			RegistryTypeVO registryTypeVO = (RegistryTypeVO) iter.next();
			if (registryTypeVO.getRegistryCode().equalsIgnoreCase(eventTypeCode))
			{
				registryTypeId = registryTypeVO.getRegistryTypeId();
			}
		}
		return registryTypeId;
	}

	/**
	 * Gets the registry items list service name.
	 * 
	 * @return the registryItemsListServiceName
	 */
	public String getRegistryItemsListServiceName()
	{
		return mRegistryItemsListServiceName;
	}

	/**
	 * Sets the registry items list service name.
	 * 
	 * @param registryItemsListServiceName
	 *            the registryItemsListServiceName to set
	 */
	public void setRegistryItemsListServiceName(final String registryItemsListServiceName)
	{
		this.mRegistryItemsListServiceName = registryItemsListServiceName;
	}

	/**
	 * Gets the catalog tools.
	 * 
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools()
	{
		return mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 * 
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools)
	{
		this.mCatalogTools = catalogTools;
	}

	/**
	 * if the item is not exist in the content remove from the list
	 * 
	 * @param listRegistryItemVO
	 * @return
	 */
	public List<RegistryItemVO> fliterNotAvliableItem(List<RegistryItemVO> listRegistryItemVO)
	{
		if (null != listRegistryItemVO && listRegistryItemVO.size() > 0)
		{
			for (int index = listRegistryItemVO.size() - 1; index >= 0; --index)
			{

				RegistryItemVO registryItemVO = (RegistryItemVO) listRegistryItemVO.get(index);
				try
				{
					getCatalogTools().getParentProductForSku(String.valueOf(registryItemVO.getSku()), true);
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
	public String getProfilePriceListPropertyName()
	{
		return profilePriceListPropertyName;
	}

	/**
	 * @param profilePriceListPropertyName
	 *            the profilePriceListPropertyName to set
	 */
	public void setProfilePriceListPropertyName(String profilePriceListPropertyName)
	{
		this.profilePriceListPropertyName = profilePriceListPropertyName;
	}

	/**
	 * @return the priceListManager
	 */
	public PriceListManager getPriceListManager()
	{
		return priceListManager;
	}

	/**
	 * @param priceListManager
	 *            the priceListManager to set
	 */
	public void setPriceListManager(PriceListManager priceListManager)
	{
		this.priceListManager = priceListManager;
	}

	/**
	 * @return the topRegMaxCount
	 */
	public String getTopRegMaxCount()
	{
		return topRegMaxCount;
	}

	/**
	 * @param topRegMaxCount
	 *            the topRegMaxCount to set
	 */
	public void setTopRegMaxCount(String topRegMaxCount)
	{
		this.topRegMaxCount = topRegMaxCount;
	}

	/** The Constant REGISTRY_ID */
	public static final ParameterName	REGISTRY_ID	= ParameterName.getParameterName(BBBGiftRegistryConstants.REGISTRY_ID);

	/** The parameter for sort seq. */
	public static final ParameterName	SORT_SEQ= ParameterName.getParameterName(BBBGiftRegistryConstants.SORT_SEQ);

	/** The Constant VIEW. */
	public static final ParameterName	VIEW= ParameterName.getParameterName(BBBGiftRegistryConstants.VIEW);

	/** The Constant REG_EVENT_TYPE_CODE */
	public static final ParameterName	REG_EVENT_TYPE_CODE	= ParameterName.getParameterName(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE);

	/** The Constant START_INDEX */
	public static final ParameterName	START_INDEX	= ParameterName.getParameterName(BBBGiftRegistryConstants.START_INDEX);

	/** The Constant BULK_SIZE */
	public static final ParameterName	BULK_SIZE= ParameterName.getParameterName(BBBGiftRegistryConstants.BULK_SIZE);

	/** The Constant IS_GIFT_GIVER */
	public static final ParameterName	IS_GIFT_GIVER= ParameterName.getParameterName(BBBGiftRegistryConstants.IS_GIFT_GIVER);

	/** The Constant IS_AVAIL_WEBPUR */
	public static final ParameterName	IS_AVAIL_WEBPUR	= ParameterName.getParameterName(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR);

	/** The Constant SITE_ID */
	public static final ParameterName	SITE_ID_PARAM= ParameterName.getParameterName(SITE_ID);

	/** The Constant profile */
	public static final ParameterName	PROFILE	= ParameterName.getParameterName("profile");

	/**
	 * Gets the gift registry manager.
	 * 
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager()
	{
		return mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager pGiftRegistryManager)
	{
		mGiftRegistryManager = pGiftRegistryManager;
	}

	public SiteContext getSiteContext()
	{
		return siteContext;
	}

	public void setSiteContext(SiteContext mSiteContext)
	{
		this.siteContext = mSiteContext;
	}

}
