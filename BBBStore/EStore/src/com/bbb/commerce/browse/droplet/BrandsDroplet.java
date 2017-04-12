package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.repository.seo.IndirectUrlTemplate;
import com.bbb.commerce.browse.vo.BrandsListingVO;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.search.integration.SearchManager;
import com.bbb.utils.BBBUtility;

/**
 * Droplet to access all data related to brands view. This data is mainly
 * displayed as part of brand listing page Copyright 2011 Bath & Beyond, Inc.
 * All Rights Reserved. Reproduction or use of this file without explicit
 * written consent is prohibited. Created by: njai13 Created on: November-2011
 * 
 * @author njai13
 * 
 */
public class BrandsDroplet extends BBBDynamoServlet {
	public final static String FEATURED_TYPE = "featured";
	public final static String ALL_TYPE = "all";
	public final static String PARAMETER_ALPHABET_BRANDLIST_MAP = "alphabetBrandListMap";
	public final static String PARAMETER_FEATURED_BRANDS = "featuredBrands";
	public final static String OPARAM_OUTPUT = "output";
	public final static String KEYWORD_NAME = "keywordName";
	public final static String SEO_OPARAM_OUTPUT = "seooutput";
	public final static String SEO_URL = "seoUrl";
	public final static String SORT_OPTION_VO = "sortOptionVO";
	public final static String PARAMETER_TYPE = "type";
	public final static String ALPHABET_BRANDLIST_MAP="alphabetBrandListMobileMap";
	public final static String NUMERIC_BRANDLIST_MAP="numericBrandListMobileMap";
	private SearchManager searchManager;
	private BBBCatalogToolsImpl mCatalogTools;
	private IndirectUrlTemplate brandTemplate;
	
	/**
	 * @return the brandTemplate
	 */
	public IndirectUrlTemplate getBrandTemplate() {
		return this.brandTemplate;
	}

	/**
	 * @param brandTemplate the brandTemplate to set
	 */
	public void setBrandTemplate(IndirectUrlTemplate brandTemplate) {
		this.brandTemplate = brandTemplate;
	}


	/**
	 * @return the searchManager
	 */
	public SearchManager getSearchManager() {
		return this.searchManager;
	}

	/**
	 * @param searchManager
	 *            the searchManager to set
	 */
	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogToolsImpl getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogToolsImpl pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	
	
	@Override
	public void service(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) throws ServletException,
			IOException {
		
		final String methodName = "BrandsDroplet:service";
		try {
			Map<String, ArrayList<BrandVO>> alphabetBrandListMap = new TreeMap<String, ArrayList<BrandVO>>();
			BrandsListingVO brandListingVO = getSearchManager().getAllBrands();
			if(brandListingVO != null){
				List<BrandVO> activeBrandsList = brandListingVO.getListBrands();
				if(activeBrandsList != null) {
					alphabetBrandListMap=processBrandResults(activeBrandsList);
					logDebug(methodName + "[OutParameter=output]");
					logDebug("Key Set in all brands map " + alphabetBrandListMap.keySet());
					
					List<BrandVO> featuredBrands = brandListingVO.getListFeaturedBrands();
					
					logDebug(methodName + "[OutParameter=output]");
					logDebug("Featured Map list size " + featuredBrands.size());
					
					request.setParameter(PARAMETER_FEATURED_BRANDS, featuredBrands);
					request.setParameter(PARAMETER_ALPHABET_BRANDLIST_MAP, alphabetBrandListMap);
					request.serviceParameter(OPARAM_OUTPUT, request, response);
				}
			}
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(request, "Business Exception from service of BrandsDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1020),e);
			request.setParameter("error", "err_brand_system_error");
			request.serviceLocalParameter("error", request, response);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(request, "System Exception from service of BrandsDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1021),e);
			request.setParameter("error", "err_brand_system_error");
			request.serviceLocalParameter("error", request, response);
		}

	}

	/**
	 * @return TreeMap <String,ArrayList<BrandVO>> It is assumed that the
	 *         results from Search Engine will come in sorted order alphabetically. The
	 *         method separates each brand name in the buckets of their first
	 *         alphabet. the method checks if key exists for the 1st alphabet of
	 *         the brandName if yes it adds the brand name in that bucket else
	 *         it creates new bucket with prefix as the new key
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Map<String, ArrayList<BrandVO>> processBrandResults(List<BrandVO> listAllBrands)
			throws BBBBusinessException, BBBSystemException {
		String methodName = "BrandsDroplet:processBrandResults";
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final Map<String, ArrayList<BrandVO>> brandListMap = new LinkedHashMap<String, ArrayList<BrandVO>>();
		final Map<String, ArrayList<BrandVO>> alphabetBrandListMap = new TreeMap<String, ArrayList<BrandVO>>();
		final Map<String, ArrayList<BrandVO>> numericBrandListMap = new TreeMap<String, ArrayList<BrandVO>>();
		//if (listAllBrands != null) { // - Null check already done before invoking this method
			final int allBrandSize = listAllBrands.size();
			for (int i = 0; i < allBrandSize; i++) {
				final BrandVO brandVO = listAllBrands.get(i);
				final String prefix = ((Character) (brandVO.getBrandName().charAt(0)))
						.toString().toUpperCase();
				if(!prefix.matches("[A-Za-z]") && BBBUtility.isInteger(prefix)){
					if (numericBrandListMap.containsKey(prefix)) {
						logDebug(methodName + "[new prefix added " + prefix + "]");
						logDebug(methodName + "[new brand added in new bucket" + brandVO.getBrandName() + "]");
						numericBrandListMap.get(prefix).add(brandVO);
					} else {
						final ArrayList<BrandVO> newBrandList = new ArrayList<BrandVO>();
						logDebug(methodName + "[new brand added " + brandVO.getBrandName() + " in bucket with key " + prefix + " ]");
						newBrandList.add(brandVO);
						numericBrandListMap.put(prefix, newBrandList);
					}
					
				}else{
					if (alphabetBrandListMap.containsKey(prefix)) {
						logDebug(methodName + "[new prefix added " + prefix + "]");
						logDebug(methodName + "[new brand added in new bucket" + brandVO.getBrandName() + "]");
						alphabetBrandListMap.get(prefix).add(brandVO);
					} else {
						final ArrayList<BrandVO> newBrandList = new ArrayList<BrandVO>();
						logDebug(methodName + "[new brand added " + brandVO.getBrandName() + " in bucket with key " + prefix + " ]");
						newBrandList.add(brandVO);
						alphabetBrandListMap.put(prefix, newBrandList);
					}
				}
			}
			brandListMap.putAll(alphabetBrandListMap);
			brandListMap.putAll(numericBrandListMap);
			request.setParameter(ALPHABET_BRANDLIST_MAP,alphabetBrandListMap);
			request.setParameter(NUMERIC_BRANDLIST_MAP,numericBrandListMap);
		//}

		return brandListMap;
	}
	
	
	public BrandsListingVO getAllBrands() throws BBBSystemException {
		
		logDebug("BrandsDroplet.getAllBrands() method starts");
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();
		
		try {
			service(pRequest, pResponse);
			List<BrandVO>  featuredBrands = (List<BrandVO>) pRequest.getObjectParameter(PARAMETER_FEATURED_BRANDS);
			Map<String, ArrayList<BrandVO>>  alphabetBrandListMap = (TreeMap<String, ArrayList<BrandVO>>) pRequest.getObjectParameter(ALPHABET_BRANDLIST_MAP);
			Map<String, ArrayList<BrandVO>>  numericBrandListMap = (TreeMap<String, ArrayList<BrandVO>>) pRequest.getObjectParameter(NUMERIC_BRANDLIST_MAP);
			BrandsListingVO brandsListingVO = new BrandsListingVO();
			brandsListingVO.setListFeaturedBrands(featuredBrands);
			brandsListingVO.setAlphabetBrandListMap(brandMapSort(alphabetBrandListMap));
			brandsListingVO.setNumericBrandListMap(brandMapSort(numericBrandListMap));
			return brandsListingVO;
		} catch (ServletException e) {
			 throw new BBBSystemException("err_servlet_exception_state_details", "ServletException in BrandsDroplet Droplet");
		} catch (IOException e) {
			 throw new BBBSystemException("err_io_exception_state_details", "IO Exception in in BrandsDroplet Droplet");
		} finally {
	
			logDebug(" BrandsDroplet.getAllBrands method ends");
			
		}
	}
	
	private Map<String, ArrayList<BrandVO>> brandMapSort (Map<String, ArrayList<BrandVO>> brandMap) {
		if (brandMap != null) {
			for (Entry<String, ArrayList<BrandVO>> brandMapItem : brandMap.entrySet()) {
			    Collections.sort(brandMapItem.getValue(), BrandVO.BrandNameComparator);			
		    }
		}
		return brandMap;
	}
}
