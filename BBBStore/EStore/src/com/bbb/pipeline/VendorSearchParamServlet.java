package com.bbb.pipeline;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.InsertableServletImpl;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

public class VendorSearchParamServlet extends InsertableServletImpl {
private BBBCatalogTools catalogTools;
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	@Override
	public void service(final DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
			throws ServletException, IOException {		
	    String methodName = "VendorSearchParamServlet.service";
	    BBBPerformanceMonitor.start(BBBCoreConstants.SET_VENDOR_SEARCH_PARAM_SERVLET, methodName);
		vlogDebug("Entering in VendorSearchParamServlet");
		
		String vendorFlag = getCatalogTools().getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG, BBBCoreConstants.FALSE);
		if(BBBUtility.isNotEmpty(vendorFlag) && Boolean.valueOf(vendorFlag)){
			this.setVendorIdInSession();			
		} else {
			vlogDebug("Vendor Flag is disabled.");
		}
		vlogDebug("Exiting VendorSearchParamServlet");
		BBBPerformanceMonitor.start(BBBCoreConstants.SET_VENDOR_SEARCH_PARAM_SERVLET, methodName);
		passRequest(pRequest, pResponse);
	}

	/**
	 * This function updates the vendorId in the session
	 * @return String
	 */
	private String setVendorIdInSession() {
		String methodName = "setVendorNameInSession";
		BBBPerformanceMonitor.start(BBBCoreConstants.SET_VENDOR_SEARCH_PARAM_SERVLET, methodName);
		BBBSessionBean bbbSessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		String vParam = getCatalogTools().getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_PARAM, "");
		String vendorId = null;
					
		//BBBI-4369
		if(BBBUtility.getOriginOfTraffic().equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)){
			vendorId = ServletUtil.getCurrentRequest().getHeader(BBBCoreConstants.SITE_SPECT_VENDOR_CODE);
		} else {
			if(BBBUtility.isNotEmpty(ServletUtil.getCurrentRequest().getParameter(vParam))) {
					
				// Retrieve Vendor Id from URL
				try {
					vendorId = URLDecoder.decode(ServletUtil.getCurrentRequest().getParameter(vParam), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					if(isLoggingError()){
						vlogError("Error in Decoding URL for Vendor Parameter :- ", e.getMessage());
					}	
				}
				
			} else if(BBBUtility.isNotEmpty(bbbSessionBean.getVendorParam())) {
				
				// Retrieve Vendor Name from SessionBean if not present in URL
				vendorId = bbbSessionBean.getVendorParam();
				
			} else {
				
				// Retrieve Vendor Name from SiteSpect if not present in Session
				vendorId = ServletUtil.getCurrentRequest().getHeader(BBBCoreConstants.SITE_SPECT_VENDOR_CODE);
			}
		}
			
			// BBBI-3934 | Adding check for invalid vendor id
			String activeVendors = getCatalogTools().getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.SEARCH_VENDOR_LIST_ACTIVE, "");
			// Set vendor Name to SessionBean

			setVendorNameToSessionBean(bbbSessionBean, vendorId, activeVendors);
		
		BBBPerformanceMonitor.end(BBBCoreConstants.SET_VENDOR_SEARCH_PARAM_SERVLET, methodName);
		
		return bbbSessionBean.getVendorParam();
	}

	/**
	 * @param bbbSessionBean
	 * @param vendorId
	 * @param activeVendors
	 */
	protected void setVendorNameToSessionBean(BBBSessionBean bbbSessionBean,
			String vendorId, String activeVendors) {
		if(BBBUtility.isNotEmpty(vendorId)) {
			if(BBBUtility.isNotEmpty(activeVendors) && activeVendors.contains(vendorId)){
				vlogDebug("Setting Vendor Name to SessionBean :- "+vendorId);
				bbbSessionBean.setVendorParam(vendorId);
				ServletUtil.getCurrentRequest().getSession().setAttribute(BBBCoreConstants.VENDOR_PARAM, vendorId);
			} else {
				vlogDebug("The vendor id:"+ vendorId +" fetched from URL/SiteSpect/Session is not found in active vendor list :"+ activeVendors);
			}
		}
	}
}

