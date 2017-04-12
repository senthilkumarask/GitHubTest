package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

/**
 * This class holds logic for favorite / Nearest Stores on PDP
 * 
 * @author
 * 
 */
public class BBBFindStoreOnPDPDroplet extends BBBDynamoServlet {

	private SearchStoreManager searchStoreManager;
	private BBBCatalogTools catalogTools;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBStoreInventoryContainer storeInventoryContainer;
	private BBBInventoryManager inventoryManager;
	private Profile atgProfile;
	private BBBSessionBean sessionBean;
	public final static String FAV_STORE_STOCK_STATUS = "favStoreStockStatus";

	public SearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public BBBStoreInventoryContainer getStoreInventoryContainer() {
		return storeInventoryContainer;
	}

	public void setStoreInventoryContainer(
			BBBStoreInventoryContainer storeInventoryContainer) {
		this.storeInventoryContainer = storeInventoryContainer;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(
			LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	public Profile getAtgProfile() {
		return atgProfile;
	}

	public void setAtgProfile(Profile atgProfile) {
		this.atgProfile = atgProfile;
	}

	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		final String logMessage = getClass().getName() + "service || ";
		logDebug(logMessage + " Inside Service Method...");

		// getting request parameters
		String searchString = req.getParameter(BBBCoreConstants.SEARCHSTRING);
		String latitude = req.getParameter(SelfServiceConstants.LATITUDE);
		String longitude = req.getParameter(SelfServiceConstants.LONGITUDE);
		String radius = req.getParameter(BBBCoreConstants.RADIUS);
		String siteId = req.getParameter(BBBCoreConstants.SITE_ID);
		String storeId = req.getParameter(BBBCoreConstants.STORE_ID);
		String isReqForFavStore = req
				.getParameter(BBBCoreConstants.FAVOURITE_STORE);
		String favStoreId = req.getParameter("favStoreId");
		String plpCall = req.getParameter("localStoreCallFromPLP");
		String latLngFromPLP = req.getParameter("latLngFromPLP");
		boolean callFromPLP = Boolean.parseBoolean(plpCall);
		// Check if lat/lng is there in cookie for the address/zip code entered previously by user OR if lat/lng is there in cookie for the StoreId passed from PLP
		String latLngCookie = BBBUtility.getCookie(req,
						SelfServiceConstants.LAT_LNG_COOKIE);
		if(callFromPLP || (!callFromPLP && BBBUtility.isEmpty(latLngCookie))){
			String headerValue = (String) req.getHeader("X-Akamai-Edgescape");
			if (headerValue != null) {
				
				//Calling utility method which will fetch the values from header and populate them in a map.
				Map<String, String> map = BBBUtility.getAkamaiHeaderValueMap(headerValue);
				
				 latitude = map.get(SelfServiceConstants.LOCATIONLAT);
				 longitude = map.get(SelfServiceConstants.LONG);

		}
		}
	
		if(BBBCoreConstants.FALSE.equals(isReqForFavStore))
		{
			//setting radius cookie to make radius search consistent through out the site
			Cookie cookie = new Cookie(SelfServiceConstants.RADIUS_STORE , radius);
			cookie.setMaxAge(getCookieTimeOut());
			cookie.setDomain(req.getServerName());
			cookie.setPath(BBBCoreConstants.SLASH);
			BBBUtility.addCookie(res, cookie, true);
		}
				
		// setting radius in session so that the same can be used in Store Tools
		// as per existing logic
		req.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius);

		// get store details based on lat/lng derived by StoreID passed from PLP
		if (!BBBUtility.isEmpty(latLngFromPLP) && latLngFromPLP != latLngCookie) {
		logInfo("Search is based on cookie");
		String[] arrLatLng = latLngFromPLP.split(BBBCoreConstants.COMMA);
		if (BBBCoreConstants.TRUE.equals(isReqForFavStore)) {
			logInfo("Getting Fav Stores based on Cookie");
			try {
				getSearchStoreManager().getFavStoreByCoordinates(siteId,
						req, res, arrLatLng[1], arrLatLng[0], callFromPLP);
			} catch (Exception e) {

				String errorMsg = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,
						BBBCoreConstants.DEFAULT_LOCALE, null);
				if (!BBBUtility.isEmpty(errorMsg)) {
					errorMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_FAV_STORE;
				}
				req.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES,
						errorMsg);

				req.serviceParameter(
						BBBCoreConstants.ERROR_VIEW_FAV_STORES, req, res);
				logError(
						"Exception occurred while getting fav store by coordinates ",
						e);
			}
		} else {
			logInfo("Getting Nearest Stores based on Cookie");
			try {
				getSearchStoreManager().getNearestStoresByCoordinates(
						arrLatLng[1], arrLatLng[0], req, res, callFromPLP);
			} catch (Exception e) {

				String errMsg = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,
						BBBCoreConstants.DEFAULT_LOCALE, null);
				if (BBBUtility.isEmpty(errMsg)) {
					errMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_VIEW_STORES;
				}
				req.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES,
						errMsg);

				req.serviceParameter(
						BBBCoreConstants.ERROR_VIEW_ALL_STORES, req, res);
				logError(
						"Exception occurred while getting nearest store to fav store by coordinates ",
						e);
			}
		}
		}
		
		
		// get store details based on lat/lng saved in cookie
		else if (!BBBUtility.isEmpty(latLngCookie)
				&& BBBUtility.isEmpty(searchString)) {
			logDebug("Search is based on cookie");
			String[] arrLatLng = latLngCookie.split(BBBCoreConstants.COMMA);
			if (BBBCoreConstants.TRUE.equals(isReqForFavStore)) {
				logDebug("Getting Fav Stores based on Cookie");
				try {
					getSearchStoreManager().getFavStoreByCoordinates(siteId,
							req, res, arrLatLng[1], arrLatLng[0], callFromPLP);
				} catch (Exception e) {

					String errorMsg = getLblTxtTemplateManager().getErrMsg(
							BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,
							BBBCoreConstants.DEFAULT_LOCALE, null);
					if (!BBBUtility.isEmpty(errorMsg)) {
						errorMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_FAV_STORE;
					}
					req.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES,
							errorMsg);

					req.serviceParameter(
							BBBCoreConstants.ERROR_VIEW_FAV_STORES, req, res);
					logError(
							"Exception occurred while getting fav store by coordinates ",
							e);
				}
			} else {
				logDebug("Getting Nearest Stores based on Cookie");
				try {
					getSearchStoreManager().getNearestStoresByCoordinates(
							arrLatLng[1], arrLatLng[0], req, res, callFromPLP);
				} catch (Exception e) {

					String errMsg = getLblTxtTemplateManager().getErrMsg(
							BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,
							BBBCoreConstants.DEFAULT_LOCALE, null);
					if (BBBUtility.isEmpty(errMsg)) {
						errMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_VIEW_STORES;
					}
					req.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES,
							errMsg);

					req.serviceParameter(
							BBBCoreConstants.ERROR_VIEW_ALL_STORES, req, res);
					logError(
							"Exception occurred while getting nearest store to fav store by coordinates ",
							e);
				}
			}
		}
		// get store details by zip code / Address
		else if (!BBBUtility.isEmpty(searchString)) {
			logDebug("Search is based on search String");
			try {
				getSearchStoreManager().getStoresBySearchString(searchString,
						req, res, callFromPLP);
			} catch (Exception e) {
				// setting error message in request parameter
				String errMsg = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,
						BBBCoreConstants.DEFAULT_LOCALE, null);
				if (BBBUtility.isEmpty(errMsg)) {
					errMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_VIEW_STORES;
				}
				req.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, errMsg);

				req.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES,
						req, res);
				logError(
						"Exception occurred while searching stores by string ",
						e);
			}
		}

		// get list of stores near to fav store within specified radius(BCC
		// configured)
		else if (!BBBUtility.isEmpty(storeId)
				&& BBBCoreConstants.FALSE.equals(isReqForFavStore)) {
			logDebug("Getting Nearest Stores based on Fav Store Id");
			try {
				getSearchStoreManager().getNearestStoresByStoreId(storeId, req,
						res, favStoreId, callFromPLP);
			} catch (Exception e) {
				String errMsg = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,
						BBBCoreConstants.DEFAULT_LOCALE, null);
				if (BBBUtility.isEmpty(errMsg)) {
					errMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_VIEW_STORES;
				}
				req.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, errMsg);

				req.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES,
						req, res);
				logError(
						"Exception occurred while getting nearest store to fav store ",
						e);
			}
		}

		// get nearest store by coordinates
		else if (!BBBUtility.isEmpty(latitude)
				&& !BBBUtility.isEmpty(longitude)
				&& BBBCoreConstants.FALSE.equals(isReqForFavStore)) {
			logDebug("Getting Nearest Stores based on Coordinates");
			try {
				getSearchStoreManager().getNearestStoresByCoordinates(latitude,
						longitude, req, res, callFromPLP);
			} catch (Exception e) {

				String errMsg = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,
						BBBCoreConstants.DEFAULT_LOCALE, null);
				if (BBBUtility.isEmpty(errMsg)) {
					errMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_VIEW_STORES;
				}
				req.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, errMsg);

				req.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES,
						req, res);
				logError(
						"Exception occurred while getting nearest store to fav store by coordinates ",
						e);
			}
		}
		// get favorite store by store id
		else if (BBBCoreConstants.TRUE.equals(isReqForFavStore)
				&& !BBBUtility.isEmpty(favStoreId)) {
			logDebug("Getting Favorite Store based on Fav Store Id");
			try {
				if(!callFromPLP){
				getSearchStoreManager().getFavStoreByStoreId(siteId, req, res,
						favStoreId);
				}else{
					getSearchStoreManager().getFavStoreByStoreIdForPLP(siteId, req, res,
							favStoreId);
				}
			} catch (Exception e) {
				String errorMsg = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,
						BBBCoreConstants.DEFAULT_LOCALE, null);
				if (!BBBUtility.isEmpty(errorMsg)) {
					errorMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_FAV_STORE;
				}
				req.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES,
						errorMsg);

				req.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES,
						req, res);
				logError("Exception occurred while getting fav store ", e);
			}
		}

		// getting store details by Akamai Lat/lng if favorite store is not
		// there for the user
		// This flow will be valid for non logged in users and for the
		// logged in users having none favorite store
		else if (BBBCoreConstants.TRUE.equals(isReqForFavStore)
				&& !BBBUtility.isEmpty(latitude)
				&& !BBBUtility.isEmpty(longitude)) {
			logDebug("Getting Favorite Store based on Coordinates");
			try {
				getSearchStoreManager().getFavStoreByCoordinates(siteId, req,
						res, latitude, longitude, callFromPLP);
			} catch (Exception e) {

				String errorMsg = getLblTxtTemplateManager().getErrMsg(
						BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,
						BBBCoreConstants.DEFAULT_LOCALE, null);
				if (!BBBUtility.isEmpty(errorMsg)) {
					errorMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_FAV_STORE;
				}
				req.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES,
						errorMsg);

				req.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES,
						req, res);
				logError(
						"Exception occurred while getting fav store by coordinates ",
						e);
			}
		}
		else
		{
			String lblFindStore = getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_FIND_IN_YOUR_STORE, BBBCoreConstants.DEFAULT_LOCALE, null);
			req.setParameter(BBBCoreConstants.EMPTY_INPUT, lblFindStore);
			req.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, req, res);
		}
		logDebug(" service() method Ends here  .");
		
	}
	
    /**
    * This method takes max age of cookie from config keys
    * 
     * @return int
    * @throws BBBSystemException
    * @throws BBBBusinessException
    */
	public int getCookieTimeOut() {
		int configValue = 0;
		try
		{
			List<String> keysList = getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG);
			if (!BBBUtility.isListEmpty(keysList))
			{
				configValue = Integer.parseInt(keysList.get(0));
			}
		}
		catch (BBBSystemException e1)
		{
			logError("BBBSystemException | Error occurred while getting cookie time out value for radius");
		} catch (BBBBusinessException e1)
		{
			logError("BBBBusinessException | Error occurred while getting cookie time out value for radius");
		}
		return configValue;
	}

}
