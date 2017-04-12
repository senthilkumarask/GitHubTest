package com.bbb.common.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.multisite.SiteContextManager;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.servlet.ServletUtil;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.common.vo.AppShipMethodPriceVO;
import com.bbb.common.manager.ShippingMethodManager;

import com.bbb.common.vo.ShippingInfoKey;
import com.bbb.common.vo.StatesShippingMethodPriceVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * @author vagrawal5
 * 
 *         This Droplet is used to render shipping method details.
 */

public class ShippingMethodDroplet extends BBBDynamoServlet {

	private ShippingMethodManager mShippingMethodManager;
	private List<String> sortSequence;

	/**
	 * @return the mShippingMethodManager
	 */
	public ShippingMethodManager getShippingMethodManager() {
		return mShippingMethodManager;
	}

	/**
	 * @param pShippingMethodManager
	 *            the mShippingMethodManager to set
	 */
	public void setShippingMethodManager(
			ShippingMethodManager pShippingMethodManager) {
		mShippingMethodManager = pShippingMethodManager;
	}

	/**
	 * Description: ShippingMethodDroplet class Overrides the OOTB method
	 * service method. It delegates the processing to the ShippingMethodManager
	 * for all the business related logic related to Shipping method.
	 * 
	 * @param pRequest
	 *            request parameter
	 * @param pResponse
	 *            response parameter
	 * @throws ServletException
	 *             an instance of ServletException
	 * @throws IOException
	 *             an instance of IOException
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("starting method ShippingMethodManagerDroplet.service");

		String requestType = (String) pRequest
				.getLocalParameter(BBBCmsConstants.REQUEST_TYPE);
		if (requestType.equals(BBBCmsConstants.SHIPPING_METHOD_DETAILS)) {
			getShippingMethodDetailsService(pRequest, pResponse);

		}

		if (requestType.equals(BBBCmsConstants.SHIPPING_PRICE_TABLE_DETAIL)) {
			getShippingPriceTableDetailService(pRequest, pResponse);
		
		}

		if (requestType.equals(BBBCmsConstants.SORT_SHIPPING_PRICE)) {
			getShippingPriceSortService(pRequest, pResponse);
		
		}

		logDebug("Existing method ShippingMethodManagerDroplet.service");


	}
	private void getShippingPriceSortService(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {	
		@SuppressWarnings("unchecked")
		final List<AppShipMethodPriceVO> mAppShipMethodPriceVO=(List<AppShipMethodPriceVO>)pRequest
				.getLocalParameter(BBBCmsConstants.APP_SHIP_METHOD_PRICE_V_OS);
		List<AppShipMethodPriceVO> sortedAppShipMethodPriceVO =new ArrayList<AppShipMethodPriceVO>();
			for(String shippingMenthod:getSortSequence()){
				for(AppShipMethodPriceVO appShipMethodPriceVO:mAppShipMethodPriceVO){
					if(appShipMethodPriceVO.getmAppShipMethodName().equalsIgnoreCase(shippingMenthod)){				
					sortedAppShipMethodPriceVO.add(appShipMethodPriceVO);
					}
				}	
			}	
			pRequest.setParameter(BBBCmsConstants.SORTED_APP_SHIP_METHOD_PRICE_VO, sortedAppShipMethodPriceVO);
			pRequest.serviceParameter(BBBCmsConstants.SORT, pRequest,	pResponse);
	}

	/**
	 * Description: This method performs all the ShippingMethod related
	 * processing.
	 * 
	 * @param pRequest
	 *            request parameter
	 * @param pResponse
	 *            response parameter
	 * @throws ServletException
	 *             an instance of ServletException
	 * @throws IOException
	 *             an instance of IOException
	 */
	private void getShippingPriceTableDetailService(
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	
		logDebug("Starting method ShippingMethodDroplet.getShippingPriceTableDetailService");
		
		Map<String, Map<ShippingInfoKey, StatesShippingMethodPriceVO>> shippingPriceTableDetail1 = null;
	
		try {

			String siteId = null;

			if (pRequest.getLocalParameter(BBBCmsConstants.SITE_ID) != null) {
				siteId = (String) pRequest
						.getLocalParameter(BBBCmsConstants.SITE_ID);
			}
			if(null!=siteId){
				if("TBS_BedBathUS".equals(siteId) ) {
					siteId = "BedBathUS";
				}
				else if("TBS_BuyBuyBaby".equals(siteId) ) {
					siteId = "BuyBuyBaby";			
				}
				else if("TBS_BedBathCanada".equals(siteId) ) {
					siteId = "BedBathCanada";			
				}
			}
			shippingPriceTableDetail1 = getShippingMethodManager().getShippingPriceTableDetail(siteId);
			Set<String> shippingMethods = getShippingMethodManager().getShippingMethods(siteId);
			Set<String> sortedShippingMethods=new LinkedHashSet<String>();
			for(String shippingMenthod:getSortSequence()){
				for(String pShippingMethod:shippingMethods){
					if(pShippingMethod.equalsIgnoreCase(shippingMenthod)){				
						sortedShippingMethods.add(pShippingMethod);
					}
				}	
			}	
			if (shippingPriceTableDetail1 != null) {
				pRequest.setParameter(BBBCmsConstants.SHIPPING_PRICE_TABLE_DETAIL, shippingPriceTableDetail1);
				pRequest.setParameter(BBBCmsConstants.SHIPPING_METHODS_TABLE, sortedShippingMethods);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,	pResponse);
			} else {
				pRequest.serviceParameter(BBBCmsConstants.EMPTY, pRequest, pResponse);
			}


		} catch (RepositoryException repException) {
		logError("Repository Exception occurred while fetching Shipping details", repException);
			
		}

		logDebug("Existing method ShippingMethodDroplet.getShippingPriceTableDetailService");

	
	}

	/**
	 * Description: This method performs all the Shipping price table related
	 * processing.
	 * 
	 * @param pRequest
	 *            request parameter
	 * @param pResponse
	 *            response parameter
	 * @throws ServletException
	 *             an instance of ServletException
	 * @throws IOException
	 *             an instance of IOException
	 */
	private void getShippingMethodDetailsService(
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

	
		logDebug("Starting method ShippingMethodDroplet.getShippingMethodDetailsService");


		try {
			String siteId = null;

			if (pRequest.getLocalParameter(BBBCmsConstants.SITE_ID) != null) {
				siteId = (String) pRequest
						.getLocalParameter(BBBCmsConstants.SITE_ID);
			}
			Map<String, List<String>> shippingMethodDetails = getShippingMethodManager()
					.getShippingMethodDetails(siteId);

			pRequest.setParameter(BBBCmsConstants.SHIPPING_METHOD_DETAILS,
					shippingMethodDetails);
			pRequest.serviceLocalParameter(BBBCmsConstants.OUTPUT, pRequest,
					pResponse);

		}catch(BBBBusinessException be){

		logError("Business Exception occurred while fetching ShippingMethodDetails", be);		
		pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		} catch(BBBSystemException bs){
		logError("System Exception occurred while fetching ShippingMethodDetails", bs);
		pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		}
		catch (RepositoryException repException) {
			logError("Repository Exception occurred while fetching ShippingMethodDetails", repException);
			pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		}
		logDebug("Existing method ShippingMethodDroplet.getShippingMethodDetailsService");

	}
	
	/** 
	 * calls service method with request_type ShippingMethodDetails
	 * @param inputParam
	 * @throws BBBSystemException 
	 */
	public Map<String, List<ShippingDaysVO>> getShippingMethodDetails() throws BBBSystemException,BBBBusinessException {
		logDebug("starting method ShippingMethodManagerDroplet.getShippingMethodDetails");

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		String siteId = SiteContextManager.getCurrentSiteId();
		pRequest.setParameter(BBBCmsConstants.SITE_ID, siteId);
		if(StringUtils.isEmpty(siteId)){
			logError("ShippingMethodManagerDroplet.getShippingMethodDetails recived null is siteId Parameter");
			throw new BBBBusinessException("err_business_exception_empty_siteid", "Business exception in getShippingPriceTableDetail(), Recieved empty sitID ");
		}

		try {
			getShippingMethodDetailsService(pRequest, pResponse);
			Map<String, List<String>> shippingMethodDetails = (Map<String, List<String>>) pRequest.getLocalParameter(BBBCmsConstants.SHIPPING_METHOD_DETAILS);
			if (shippingMethodDetails == null || shippingMethodDetails.isEmpty()) {
				throw new BBBSystemException("err_system_exception_empty_response", "System exception in getShippingMethodDetails(), Recieved no details from DataBase");
			} else {
				Map<String, List<ShippingDaysVO>> shippingMethodDetailsCopy = new HashMap<String, List<ShippingDaysVO>>(); 
				Set<Entry<String,List<String>>> keySet = shippingMethodDetails.entrySet();
				for(Entry<String,List<String>> list:keySet){
					List<ShippingDaysVO> shippingDaysVOList = new ArrayList<ShippingDaysVO>();
					String key = list.getKey();
					List<String> daysList = list.getValue();
					Iterator<String> itrDays = daysList.iterator();
					while(itrDays.hasNext()){
						ShippingDaysVO vo = new ShippingDaysVO();
						String day1 = itrDays.next();
						vo.setMinDay(day1);
						String day2 = itrDays.next();
						vo.setMaxDay(day2);	
						shippingDaysVOList.add(vo);
					}
					shippingMethodDetailsCopy.put(key, shippingDaysVOList);				
				}
				return shippingMethodDetailsCopy;
			}

		} catch (ServletException e) {
			 throw new BBBSystemException("err_ServletException_exception_empty_response", "ServletException in ShippingMethodManagerDroplet");
		} catch (IOException e) {
			 throw new BBBSystemException("err_IOException_exception_empty_response", "IOException in ShippingMethodManagerDroplet");
		} finally {
			logDebug("Exiting method ShippingMethodManagerDroplet.getShippingMethodDetails()");
		}
	}

	/**
	 * calls service method with request_type shippingMethodsTable
	 * @param inputParam
	 * @throws BBBSystemException
	 * @throws BBBBusinessException 
	 */
	public ShippingMethodDropletVO getShippingPriceTableDetail() throws BBBSystemException, BBBBusinessException {
		logDebug("starting method ShippingMethodManagerDroplet.getShippingPriceTableDetail");

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		String siteId = SiteContextManager.getCurrentSiteId();;
		pRequest.setParameter(BBBCmsConstants.SITE_ID,siteId);
		if(StringUtils.isEmpty(siteId)){
			logError("ShippingMethodManagerDroplet.getShippingMethodDetails recived null is siteId Parameter");
			throw new BBBBusinessException("err_business_exception_empty_siteid", "Business exception in getShippingPriceTableDetail(), Recieved empty sitID ");
		}
		
		try {
			getShippingPriceTableDetailService(pRequest, pResponse);
			Map<String, Map<ShippingInfoKey, StatesShippingMethodPriceVO>> shippingPriceTableDetail = (Map<String, Map<ShippingInfoKey, StatesShippingMethodPriceVO>>) pRequest.getLocalParameter(BBBCmsConstants.SHIPPING_PRICE_TABLE_DETAIL);
			Set<String> shippingMethods = (Set<String>) pRequest.getLocalParameter(BBBCmsConstants.SHIPPING_METHODS_TABLE);
			if (shippingPriceTableDetail == null || shippingPriceTableDetail.isEmpty() || shippingMethods == null || shippingMethods.isEmpty()) {
				throw new BBBSystemException("err_system_exception_empty_response", "System exception in getShippingPriceTableDetail(), Recieved no details from DataBase");
			} else {
				ShippingMethodDropletVO vo = new ShippingMethodDropletVO();
				vo.setShippingPriceTableDetail(shippingPriceTableDetail);
				vo.setShippingMethods(shippingMethods);
				return vo;
			}

		} catch (ServletException e) {
			 throw new BBBSystemException("err_ServletException_exception_empty_response", "ServletException in ShippingMethodManagerDroplet");
		} catch (IOException e) {
			 throw new BBBSystemException("err_IOException_exception_empty_response", "IOException in ShippingMethodManagerDroplet");
		} finally {
			logDebug("Exiting method ShippingMethodManagerDroplet.getShippingPriceTableDetail");

		}
	}
	public List<String> getSortSequence() {
		return sortSequence;
		}
		public void setSortSequence(List<String> sortSequence) {
		this.sortSequence = sortSequence;
		}
}
