package com.bbb.commerce.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.repository.seo.ItemLinkException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.droplet.BrandDetailDroplet;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

public class TBSBrandDetailDroplet extends BrandDetailDroplet {

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
			if( pSiteId.startsWith("TBS_")) {
				pSiteId = pSiteId.substring(4);
			}
			
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

}
