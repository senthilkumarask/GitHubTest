/**
 * 
 */
package com.bbb.certona.vo;

import java.util.Map;

import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;

/**
 * @author ikhan2
 *
 */
public class CertonaResponseVO implements HTTPServiceResponseIF{

	private String error;

	private String pageId;
	private String trackingId;
	
	private String errorCode;
	private boolean errorExist;
	
	private Map<String, CertonaResonanceItemVO> resonanceMap;
	private String resxLinks;
	
	private ProductVO oosProductVO;
	//(BPS-2413) OOS recommendation: New certona slot & tagging - MSWP
	private Map <String,String> resxLinksMap;
	
	//BBBSL-6574: Printing Request and response of Certona on view source 
	private String requestURL;
	
	private String responseXML;
	
	private String exceptionDetails;
	
	/**
	 * @return the responseXML
	 */
	public String getResponseXML() {
		return responseXML;
	}

	/**
	 * @param responseXML the responseXML to set
	 */
	public void setResponseXML(String responseXML) {
		this.responseXML = responseXML;
	}

	/**
	 * @return Getter method for the requestURL
	 */
	public String getRequestURL() {
		return requestURL;
	}

	/**
	 * @param Setter method for the requestURL to set
	 */
	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	@Override
	public void setError(final String errorCode) {
		this.error = errorCode;
	}
	
	public String getError() {
		return this.error;
	}

	@Override
	public String toString() {
		return "CertonaResponseVO [error=" + error + ", pageId=" + pageId
				+ ", trackingId=" + trackingId + ", errorCode=" + errorCode
				+ ", errorExist=" + errorExist + ", resonanceMap="
				+ resonanceMap + ", resxLinks=" + resxLinks + ", oosProductVO="
				+ oosProductVO + ", resxLinksMap=" + resxLinksMap + ",requestURL=" 
				+ requestURL + ",	responseXML=" + responseXML + "]";
	}

	public String getPageId() {
		return this.pageId;
	}

	public void setPageId(final String pageId) {
		this.pageId = pageId;
	}

	public String getTrackingId() {
		return this.trackingId;
	}

	public void setTrackingId(final String trackingId) {
		this.trackingId = trackingId;
	}

	public Map<String, CertonaResonanceItemVO> getResonanceMap() {
		return this.resonanceMap;
	}

	public void setResonanceMap(Map<String, CertonaResonanceItemVO> resonanceMap) {
		this.resonanceMap = resonanceMap;
	}

	public String getResxLinks() {
		return this.resxLinks;
	}

	public void setResxLinks(String resxLinks) {
		this.resxLinks = resxLinks;
	}

	/**
	 * @return the errorExist
	 */
	public boolean isErrorExist() {
		return errorExist;
	}

	/**
	 * @param errorExist the errorExist to set
	 */
	public void setErrorExist(boolean errorExist) {
		this.errorExist = errorExist;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public ProductVO getOosProductVO() {
		return oosProductVO;
	}

	public void setOosProductVO(ProductVO oosProductVO) {
		this.oosProductVO = oosProductVO;
	}

	/**
	 * Getter for ResxLinksMap.
	 */
	public Map<String, String> getResxLinksMap() {
		return resxLinksMap;
	}

	/**
	 * Setter for ResxLinksMap.
	 */
	public void setResxLinksMap(Map<String, String> resxLinksMap) {
		this.resxLinksMap = resxLinksMap;
	}

	public String getExceptionDetails() {
		return exceptionDetails;
	}

	public void setExceptionDetails(String exceptionDetails) {
		this.exceptionDetails = exceptionDetails;
	}

}
