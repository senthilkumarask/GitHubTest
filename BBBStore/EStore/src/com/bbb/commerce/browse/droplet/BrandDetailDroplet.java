package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.search.bean.result.SortOptionVO;
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
public class BrandDetailDroplet extends BBBDynamoServlet {
	
	public static final String ORIG_BRAND_NAME = "origBrandName";
	public final static String OPARAM_OUTPUT = "output";
	public final static String KEYWORD_NAME = "keywordName";
	public final static String SEO_OPARAM_OUTPUT = "seooutput";
	public final static String SEO_URL = "seoUrl";
	public final static String SORT_OPTION_VO = "sortOptionVO";
	public final static String JS_FILE_PATH = "jsFilePath";
	public final static String CSS_FILE_PATH = "cssFilePath";
	public final static String PROMO_CONTENT = "promoContent";
	public final static String PROMO_CONTENT_AVAILABLE = "isPromoContentAvailable";
	public final static String SCRIPT_START = "<script>";
	public final static String SCRIPT_END = "</script>";
	public static final String BRAND_NAME = "brandName";
	public final static String DEFAULT_SORT_OPTION = "defaultSortOpt";
	public static final String DEFAULT_SORT_ORDER = "defaultSortOrder";

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
		
		final String methodName = "BrandDetailDroplet:service";
		try {
			//R2.2 Seo Friendly Url - 504 A Changes
			logDebug(methodName + "Start");
			
			String brandName = request.getParameter(ORIG_BRAND_NAME);
			String brandId = request.getParameter(KEYWORD_NAME);
			String pSiteId = SiteContextManager.getCurrentSiteId();
			
			if(BBBUtility.isNotEmpty(brandName)){
				request.setParameter(SEO_URL, generateSeoUrl(brandName));
				request.serviceLocalParameter(SEO_OPARAM_OUTPUT, request, response);
			}
			
			if(BBBUtility.isNotEmpty(brandId)) {
				boolean promoContentAvailable = false;
				logDebug("Retriving Brand Details from Id: " + brandId);
				BrandVO brand = getCatalogTools().getBrandDetails(brandId, pSiteId);
				
				if (brand != null && BBBUtility.isNotEmpty(brand.getBrandName())) {
					brand.setSeoURL(generateSeoUrl(brand.getBrandName()));
					BrandVO bccBrandVO = getCatalogTools().getBccManagedBrand(brand.getBrandName(),"");
				 	//R2.2 BRAND Promo container from BCC START
				 	if(!BBBUtility.isEmpty( bccBrandVO.getBrandContent())){
						if(!bccBrandVO.getBrandContent().toLowerCase().contains(SCRIPT_START) && !bccBrandVO.getBrandContent().toLowerCase().contains(SCRIPT_END) ){
							if(!BBBUtility.isEmpty( bccBrandVO.getJsFilePath())){
								request.setParameter(JS_FILE_PATH, bccBrandVO.getJsFilePath());
							}
							if(!BBBUtility.isEmpty( bccBrandVO.getCssFilePath())){
							request.setParameter(CSS_FILE_PATH, bccBrandVO.getCssFilePath());
							}
							request.setParameter(PROMO_CONTENT, bccBrandVO.getBrandContent());
							promoContentAvailable=true;
						}
					}
					request.setParameter(PROMO_CONTENT_AVAILABLE, promoContentAvailable);
					//R2.2 BRAND Promo container from BCC END
					request.setParameter(SORT_OPTION_VO, bccBrandVO.getSortOptionVO());
					request.setParameter(SEO_URL, brand.getSeoURL());
					request.setParameter(BRAND_NAME, brand.getBrandName());
					request.serviceLocalParameter(SEO_OPARAM_OUTPUT, request, response);
				}
			}
			
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(request, "Business Exception from service of BrandDetailDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1020),e);
			request.setParameter("error", "err_brand_system_error");
			request.serviceLocalParameter("error", request, response);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(request, "System Exception from service of BrandDetailDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1021),e);
			request.setParameter("error", "err_brand_system_error");
			request.serviceLocalParameter("error", request, response);
		} catch (ItemLinkException e) {
			logError(LogMessageFormatter.formatMessage(request, "Item Link Exception from service of BrandsDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1021),e);
			request.setParameter("error", "err_brand_system_error");
			request.serviceLocalParameter("error", request, response);
		}	
			logDebug(methodName + "End");
	}


	public Map<String, Object> getBrandDetails(String brandId) throws BBBSystemException {
	
		logDebug("BrandsDroplet.getBrandDetails() method starts");
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();
		pRequest.setParameter(KEYWORD_NAME, brandId);
		try {
			service(pRequest, pResponse);
			SortOptionVO sorOptions = (SortOptionVO) pRequest.getObjectParameter(SORT_OPTION_VO);
			String seoUrl =  (String) pRequest.getObjectParameter(SEO_URL);
			Map<String, Object> map	 = new HashMap<String, Object>();
			map.put(BRAND_NAME, pRequest.getObjectParameter(BRAND_NAME));
			map.put(SEO_URL, seoUrl);
			map.put(SORT_OPTION_VO, sorOptions);

			if (sorOptions != null && sorOptions.getDefaultSortingOption() != null && 
					!BBBUtility.isEmpty(sorOptions.getDefaultSortingOption().getSortUrlParam()) && sorOptions.getDefaultSortingOption().getAscending() != null) {
				if (isLoggingDebug()) {
					logDebug("Returning Default Sort Option :: " + sorOptions.getDefaultSortingOption().getSortUrlParam() + "for Brand :: "
							+ pRequest.getObjectParameter(BRAND_NAME) + " and Default Sort Order :: " + sorOptions.getDefaultSortingOption().getAscending());
				}
				map.put(DEFAULT_SORT_OPTION, sorOptions.getDefaultSortingOption().getSortUrlParam());
				map.put(DEFAULT_SORT_ORDER, sorOptions.getDefaultSortingOption().getAscending());
			}
			
			return map;
		} catch (ServletException e) {
			 throw new BBBSystemException("err_servlet_exception_state_details", "ServletException in BrandsDroplet Droplet");
		} catch (IOException e) {
			 throw new BBBSystemException("err_io_exception_state_details", "IO Exception in in BrandsDroplet Droplet");
		} finally {
			
			logDebug(" BrandsDroplet.getBrandDetails method ends");
			
		}
	}
	
	public String generateSeoUrl (String pBrandName) throws ItemLinkException {
		
		logDebug("Creating SEO Url for Brand Name: " + pBrandName);
		WebApp pDefaultWebApp = null;
		UrlParameter[] pUrlParams = getBrandTemplate().cloneUrlParameters();
		pUrlParams[0].setValue(pBrandName);
		return getBrandTemplate().formatUrl(pUrlParams, pDefaultWebApp); 
	}
}
