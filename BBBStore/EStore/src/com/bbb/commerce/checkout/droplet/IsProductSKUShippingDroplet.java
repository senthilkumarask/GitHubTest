/**
 * 
 */
package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import com.bbb.exception.BBBBusinessException;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;

/**
 * Droplet to display Shipping restriction message on the Product detail page,
 * Cart page and gift giver registry page
 * 
 *
 * 
 * 
 */
public class IsProductSKUShippingDroplet extends BBBDynamoServlet {
	private static final String RESTRICTED_ATTRIBUTES = "restrictedAttributes";

	public final static ParameterName OPARAM_TRUE = ParameterName.getParameterName("true");
	public final static ParameterName OPARAM_FALSE = ParameterName.getParameterName("false");
	public final static ParameterName PARAMETER_SITE_ID = ParameterName.getParameterName("siteId");
	public final static ParameterName PARAMETER_SKU_ID = ParameterName.getParameterName("skuId");
	public final static ParameterName PARAMETER_PROD_ID = ParameterName.getParameterName("prodId");
	public final static String OPARAM_VDC_MSG ="vdcMsg";

	private BBBCatalogTools mCatalogTools;
	private IsProductSKUShippingDropletHelper mHelper;
	
	/**
	 * @return the helper
	 */
	public IsProductSKUShippingDropletHelper getHelper() {
		return mHelper;
	}

	/**
	 * @param pHelper the helper to set
	 */
	public void setHelper(IsProductSKUShippingDropletHelper pHelper) {
		mHelper = pHelper;
	}

	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		String pSiteId = pRequest.getParameter(PARAMETER_SITE_ID);
		final String pSkuId = pRequest.getParameter(PARAMETER_SKU_ID);
		final String pProductId = pRequest.getParameter(PARAMETER_PROD_ID);
		Map<String,AttributeVO> restrictionZipCodeAttributes= new HashMap<String,AttributeVO>();
		restrictionZipCodeAttributes = getHelper().getAttribute(pSiteId, pSkuId, pProductId);
			if (restrictionZipCodeAttributes !=null && !restrictionZipCodeAttributes.isEmpty() ) {
				pRequest.setParameter(RESTRICTED_ATTRIBUTES,restrictionZipCodeAttributes.values());
				//Rest Specific - Set Parameter to get restricted attributed list for sku
				pRequest.setParameter(BBBCoreConstants.RESTRICTED_ATTRIBUTES_REST,restrictionZipCodeAttributes.keySet());
				
				pRequest.serviceLocalParameter(OPARAM_TRUE, pRequest, pResponse);
			} else {
				pRequest.serviceLocalParameter(OPARAM_FALSE,pRequest,pResponse);
				//Rest Specific - Set Parameter to null in case of no restricted attributes
				pRequest.setParameter(BBBCoreConstants.RESTRICTED_ATTRIBUTES_REST,null);
			}
			try {// BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message
				String offsetDateVDC = getCatalogTools().getActualOffsetDate(pSiteId, pSkuId);
				if (!StringUtils.isEmpty(offsetDateVDC)){					
					pRequest.setParameter(BBBCoreConstants.OFFSET_DATE_VDC,offsetDateVDC);
					pRequest.serviceLocalParameter(OPARAM_VDC_MSG,pRequest,pResponse);
		}
				} catch (final BBBSystemException e) {
	                this.logError("System Exception Occourred while getting getActualOffsetDate ", e);
	            }catch (final BBBBusinessException e) {
	                this.logError("Business Exception Occourred while getting getActualOffsetDate ", e);
	}
		}
	}
	

