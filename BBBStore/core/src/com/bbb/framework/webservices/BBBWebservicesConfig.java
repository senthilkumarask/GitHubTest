/*
 *
 * File  : BBBWebservicesConfig.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.webservices;

import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import com.bbb.common.BBBGenericService;

public class BBBWebservicesConfig extends BBBGenericService {

	private BBBCatalogTools mCatalogTools;
	private Map serviceToWsdlMap;
	private Map serviceToErrorCodeMap;

	public Map getServiceToWsdlMap() {
		return serviceToWsdlMap;
	}

	public void setServiceToWsdlMap(Map serviceToWsdlMap) {
		this.serviceToWsdlMap = serviceToWsdlMap;
	}

	public Map getServiceToErrorCodeMap() {
		return serviceToErrorCodeMap;
	}

	public void setServiceToErrorCodeMap(Map serviceToErrorCodeMap) {
		this.serviceToErrorCodeMap = serviceToErrorCodeMap;
	}
	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools
	 *            the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	public String locateServiceURL(final String pOperation)
			throws BBBSystemException, BBBBusinessException {
		// return this.getEndPoint().get(pOperation);
		String mStrEndPoint = BBBWebServiceConstants.EMPTY;
		if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT, pOperation)!=null) {
			if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT, pOperation).size()>BBBWebServiceConstants.ZERO) {
				mStrEndPoint = getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT, pOperation).get(BBBWebServiceConstants.ZERO);
			} else {
				logError("BBBWebservicesConfig | locateServiceURL |"+BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT+"  - value not found");
			}
		} else {
			BBBSystemException sysExc = new BBBSystemException("BBBWebservicesConfig | locateServiceURL | value not found for "+BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT);
			throw sysExc;
		}
		return mStrEndPoint;
	}

	/**
	 * getServiceTimeout: return timeout value for WS call.
	 * 
	 * @param pOperation
	 *            the operation
	 * 
	 * @return timeout
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws NumberFormatException
	 */
	public int getServiceTimeout(final String pOperation)
			throws NumberFormatException, BBBSystemException,
			BBBBusinessException {
		int mTimeOut = BBBWebServiceConstants.ZERO;
		if((getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT, pOperation))!=null) {
			if(getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT, pOperation).size()>BBBWebServiceConstants.ZERO) {
				mTimeOut = Integer.parseInt(getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT, pOperation)
				.get(BBBWebServiceConstants.ZERO));
			} else {
				logError("BBBWebservicesConfig | locateServiceURL |"+BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT+"  - value not found");
			}
		}	else	{
			
			BBBSystemException sysExc = new BBBSystemException("BBBWebservicesConfig | getServiceTimeout | value not found for "+BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT);
			throw sysExc;
		}
		return mTimeOut;
		// Integer.parseInt(this.getTimeOut().get(pOperation));
	}

}
