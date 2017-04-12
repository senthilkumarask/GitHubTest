package com.bbb.search.vendor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.vendor.constants.VendorSearchConstants;
import com.bbb.search.vendor.vo.VendorRequestVO;
import com.bbb.search.vendor.vo.VendorResponseVO;
import com.bbb.utils.BBBUtility;

public class VendorSearchClient extends BBBGenericService {
	private static final String CLASS_NAME = "Vendor_Search_Client";
	/** The http call invoker. */

	private HTTPCallInvoker httpCallInvoker;
	private String mockedVendorEndpoint;
	private boolean fetchResponseFromMockedService;
	private BBBConfigTools bbbConfigTools;
	

	/**
	 * @return the bbbConfigTools
	 */
	public BBBConfigTools getBbbConfigTools() {
		return bbbConfigTools;
	}

	/**
	 * @param bbbConfigTools the bbbConfigTools to set
	 */
	public void setBbbConfigTools(BBBConfigTools bbbConfigTools) {
		this.bbbConfigTools = bbbConfigTools;
	}

	/**
	 * @return the mockedVendorEndpoint
	 */
	public String getMockedVendorEndpoint() {
		return mockedVendorEndpoint;
	}

	/**
	 * @param mockedVendorEndpoint the mockedVendorEndpoint to set
	 */
	public void setMockedVendorEndpoint(String mockedVendorEndpoint) {
		this.mockedVendorEndpoint = mockedVendorEndpoint;
	}

	/**
	 * @return the fetchResponseFromMockedService
	 */
	public boolean isFetchResponseFromMockedService() {
		return fetchResponseFromMockedService;
	}

	/**
	 * @param fetchResponseFromMockedService the fetchResponseFromMockedService to set
	 */
	public void setFetchResponseFromMockedService(
			boolean fetchResponseFromMockedService) {
		this.fetchResponseFromMockedService = fetchResponseFromMockedService;
	}

	/**
	 * This invokes the rest api of 3rd party search engine to fetch the required response
	 * @param vReqVo
	 * @return
	 */
	public VendorResponseVO fetchVendorResponse(VendorRequestVO vReqVo){
		final String FETCH_VENDOR_RESPONSE = "fetchVendorResponse";
		BBBPerformanceMonitor.start(CLASS_NAME,
				FETCH_VENDOR_RESPONSE);
		logDebug("Start:VendorResponseVO.fetchVendorResponse. Input: VendorRequestVO:"+vReqVo.toString());
		VendorResponseVO vResponseVo = null;
		final String vendorId = (String) ServletUtil.getCurrentRequest().getSession().getAttribute(VendorSearchConstants.VENDOR_PARAM);
		String vRequestJson;
		String vResponseJson;
		String vendorEndPoint;
		
		final String vendorSiteSpectCode = vendorId.concat(VendorSearchConstants.SEARCH_VENDOR_SITESPECT);
		final String vendorName = this.getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, vendorSiteSpectCode , BBBCoreConstants.BLANK);
		final String vendorEndPointKeyName = vendorName.concat(VendorSearchConstants.ENDPOINT);
		if(this.isFetchResponseFromMockedService()){
			vendorEndPoint = this.getMockedVendorEndpoint();
		} else {
			vendorEndPoint = this.getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, vendorEndPointKeyName , BBBCoreConstants.BLANK);
		}
		logDebug("Invoking webservice for vendor:"+vendorName);
		try {
							
			vRequestJson = getHttpCallInvoker().parseJSONRequest(vReqVo);
			if(!BBBUtility.isEmpty(vRequestJson) && BBBUtility.isNotEmpty(vendorEndPoint)){
				//invoking post web service	.
				vResponseJson = getHttpCallInvoker().callPostRestService(vendorEndPoint, vRequestJson, this.requestHeader(vendorName), false);
				if(!BBBUtility.isEmpty(vResponseJson)){
					vResponseVo = getHttpCallInvoker().parseJSONResponse(VendorResponseVO.class, vResponseJson);
				}
			}
		} catch (BBBSystemException e) {
			BBBPerformanceMonitor.cancel(CLASS_NAME,
					FETCH_VENDOR_RESPONSE);
			logError("Error fetching response from Search Engine"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			BBBPerformanceMonitor.cancel(CLASS_NAME,
					FETCH_VENDOR_RESPONSE);
			logError("Error fetching response from Search Engine"+e.getMessage());
			e.printStackTrace();
		} catch (BBBBusinessException e) {
			BBBPerformanceMonitor.cancel(CLASS_NAME,
					FETCH_VENDOR_RESPONSE);
			logError("Error fetching response from Search Engine"+e.getMessage());
			e.printStackTrace();
		} 
		
		BBBPerformanceMonitor.end(CLASS_NAME,
				FETCH_VENDOR_RESPONSE);
		if(null != vResponseVo){
			logDebug("End:VendorResponseVO.fetchVendorResponse.Output:"+vResponseVo.toString());
		}
		else{
			logDebug("End:VendorResponseVO.fetchVendorResponse.Output: Response is null");
		}
		
		return vResponseVo;
	}

	/**
	 * @return the httpCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}

	/**
	 * @param httpCallInvoker the httpCallInvoker to set
	 */
	public void setHttpCallInvoker(HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}

	private Map<String, String> requestHeader(String vendorName){
		Map<String, String> headerParam = new HashMap<String, String>();
		if(VendorSearchConstants.UNBXD.equalsIgnoreCase(vendorName)){
			String apiKey = this.getBbbConfigTools().getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, VendorSearchConstants.UNBXD_API_KEY , BBBCoreConstants.BLANK);
			headerParam.put("APIKey", apiKey);
		}
		return headerParam;
	}
	
}
