package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.catalog.vo.SDDResponseVO;
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.selfservice.tools.StoreTools;
import com.bbb.utils.BBBUtility;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

/**
 * This class holds logic for favorite / Nearest Stores on PDP
 * 
 * @author
 * 
 */
public class BBBDisplaySDDEligibilityOnPDPDroplet extends BBBDynamoServlet {

	private SearchStoreManager searchStoreManager;
	private BBBCatalogTools catalogTools;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBInventoryManager inventoryManager;
	private BBBCatalogToolsImpl bbbCatalogTools;
	private StoreTools mStoreTools;
	private BBBSameDayDeliveryManager sameDayDeliveryManager;

	public static final String AVAIABLE_STATUS = "availableStatus";
	public static final String MESSAGE = "message";
	public static final String STR_ZIP_CODE = "strZipCode";
	public static final String STR_ORDER_BY = "strOrderBy";
	public static final String STR_GET_BY = "strGetBy";
	public static final String STR_LBL_SDD_AVAIL = "txt_sameday_delivery_available";
	public static final String STR_LBL_SDD_UNAVAIL = "txt_sameday_delivery_unavailable";
	public static final String STR_LBL_SDD_INELIGIBLE = "txt_sameday_delivery_ineligible";
	private static final String TXT_PRODUCT_SDD_INELIGIBLE = "txt_product_sdd_ineligible";
	private static final String TXT_SDD_MARKET_UNAVAILABLE = "txt_sdd_market_unavailable";
	private static final String TXT_PRODUCT_SDD_ELIGIBLE = "txt_product_sdd_eligible";
	private static final String TXT_PRODUCT_SDD_MARKET_INELIGIBLE = "txt_product_sdd_market_ineligible";
	public static final String ZIP_CODE = "zipCode";
	public static final String SDD_AJAX = "sddFromAjax";
	public static final String SDD_ELIGIBLE = "sddEligibility";
	public static final String SDD_ATTRIBS = "sddAttribs";
	public static final String SDD_INSTOCK = "inStock";
	public static final String SDD_SITE_ID = "siteId";
	public static final String SDD_SITE_CAT_REF = "catalogRefId";
	public static final String SDD_ZIP_CODE = "zipCode";
	public static final String SDD_SKU_ID = "skuId";
	public static final String SDD_IS_AJAX = "sddFromAjax";
	public static final String SDD_AVAILABLE_STATUS = "availableStatus";
	public static final String SDD_MESSAGE = "message";
	public static final String SDD_REGION_VO = "regionVO";

	public void service(final DynamoHttpServletRequest req, DynamoHttpServletResponse res)
			throws ServletException, IOException {
		logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| service() | Starts");
		final String siteId = SiteContextManager.getCurrentSiteId();
		String inStock = BBBCoreConstants.FALSE;
		final String channel = BBBUtility.getChannel();
		if (req.getParameter(SDD_INSTOCK) != null) {
			inStock = req.getParameter(SDD_INSTOCK);
		}
		String skuId = req.getParameter(BBBCoreConstants.SKU_ID);
		if (!StringUtils.isEmpty(req.getParameter(SDD_SKU_ID))) {
			skuId = req.getParameter("skuId");
		}
		final String inputZip = req.getParameter(ZIP_CODE);
		final String fromAjax = req.getParameter(SDD_AJAX);
		String sddAttribs = req.getParameter(SDD_ATTRIBS);
		logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| params passed" + skuId + inputZip + fromAjax + sddAttribs);
		String currentZipCode = BBBCoreConstants.BLANK;
		String orderBy = BBBCoreConstants.BLANK;
		String getBy = BBBCoreConstants.BLANK;
		String availableStoreId = null;
		BBBSessionBean sessionBean = (BBBSessionBean) (req.resolveName(BBBCoreConstants.SESSION_BEAN));
		String zipCode = getZipCodeFromSession(sessionBean);
		if (null != zipCode) {
			currentZipCode = zipCode;
		}
		RegionVO regionVo = null;
		// The below scenario is for the AJAX flow
		if (null != sessionBean && BBBCoreConstants.TRUE.equalsIgnoreCase(fromAjax) && !StringUtils.isBlank(inputZip)) {
			sessionBean.getCurrentZipcodeVO().setStoreId(BBBCoreConstants.BLANK);
			// remove hyphen from zipcode
			String inputZipNoHyphen = BBBUtility.hyphenExcludedZip(inputZip);
			regionVo = getSameDayDeliveryManager().populateDataInVO(sessionBean, req, inputZipNoHyphen,
					BBBCoreConstants.RETURN_FALSE, BBBCoreConstants.RETURN_TRUE, BBBCoreConstants.RETURN_FALSE);
			// if channel is mobile/mobileApp set region VO to request
			if (null != regionVo && channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)
					|| channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)) {
				logDebug("BBBDisplaySDDEligibilityOnPDPDroplet |" + channel + " | setting regionVO");
				req.setAttribute("regionVO", regionVo);
			}
			currentZipCode = inputZipNoHyphen;
		}
		boolean requireSDDCheck = true;
		if (!BBBUtility.isEmpty(inputZip)) {
			if (null == regionVo) {
				regionVo = getSameDayDeliveryManager().populateDataInVO(sessionBean, req,
						BBBUtility.hyphenExcludedZip(inputZip), BBBCoreConstants.RETURN_FALSE,
						BBBCoreConstants.RETURN_TRUE, BBBCoreConstants.RETURN_FALSE);
			}
		} else {
			regionVo = getSameDayDeliveryManager().populateDataInVO(sessionBean, req, currentZipCode,
					BBBCoreConstants.RETURN_FALSE, BBBCoreConstants.RETURN_TRUE, BBBCoreConstants.RETURN_FALSE);
		}
		if (null == regionVo) {
			logDebug("BBBDisplaySDDEligibilityOnPDPDroplet.service: current zipcode doesn't is not sdd eligible");
			setSddAvailabilityStatus(req, BBBCoreConstants.MARKET_INELIGIBLE, currentZipCode, orderBy, getBy);
			requireSDDCheck = false;
		}

		/*
		 * the below code check for the sddEligibility and Stock level and
		 * returns un-available either of it is false.
		 */
		if (null != sessionBean && null != sessionBean.getCurrentZipcodeVO()) {
			logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| sddEligibility of zipcode and inStock is | "
					+ sessionBean.getCurrentZipcodeVO().isSddEligibility() + "|" + inStock);
		}
		/*
		 * BBBH-2388 - Return un-available message if sddEligible is false and
		 * outofstock
		 */
		if (requireSDDCheck && (null == sessionBean || null == sessionBean.getCurrentZipcodeVO()
				|| !sessionBean.getCurrentZipcodeVO().isSddEligibility())) {
			logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| when current zip is not sdd eligible");
			setSddAvailabilityStatus(req, BBBCoreConstants.SDD_ITEM_INELIGIBLE, currentZipCode, orderBy, getBy);
			requireSDDCheck = false;
		}
		try {
			if (StringUtils.isBlank(skuId)) {
				/* This piece of code is for MSWP landing page. */
				if (!requireSDDCheck) {
					/* MSWP landing page in non-pilot zip. */
					setSddAvailabilityStatus(req, BBBCoreConstants.PRODUCT_SDD_MARKET_INELIGIBLE, currentZipCode,
							orderBy, getBy);
				} else {
					String promotAttId = sessionBean.getCurrentZipcodeVO().getPromoAttId();
					String productId = req.getParameter(BBBCoreConstants.PRODUCTID);
					final RepositoryItem productRepositoryItem = getBbbCatalogTools().getCatalogRepository()
							.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
					boolean isSddProduct = getCatalogTools().hasSDDAttribute(SiteContextManager.getCurrentSiteId(),
							productRepositoryItem, promotAttId, true);
					if (isSddProduct) {
						setSddAvailabilityStatus(req, BBBCoreConstants.PRODUCT_SDD_ELIGIBLE, currentZipCode, orderBy,
								getBy);
					} else {
						setSddAvailabilityStatus(req, BBBCoreConstants.PRODUCT_SDD_INELIGIBLE, currentZipCode, orderBy,
								getBy);
					}
					requireSDDCheck = false;
				}
			}
			if (requireSDDCheck && StringUtils.isNotEmpty(skuId)
					&& (null != fromAjax && fromAjax.equalsIgnoreCase(BBBCoreConstants.TRUE))) {
				String promotAttId = sessionBean.getCurrentZipcodeVO().getPromoAttId();
				sddAttribs = BBBCoreConstants.FALSE;
				logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| promo Attribute in session is " + promotAttId);
				final RepositoryItem skuRepositoryItem = getBbbCatalogTools().getCatalogRepository().getItem(skuId,
						BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				boolean skuSddFlag = getCatalogTools().hasSDDAttribute(SiteContextManager.getCurrentSiteId(),
						skuRepositoryItem, promotAttId, true);
				if (skuSddFlag) {
					sddAttribs = BBBCoreConstants.TRUE;
				}
				logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| sdd Attribute value for sku  " + skuId + " is "
						+ sddAttribs);
			}
		} catch (BBBSystemException se) {
			vlogError(se,
					"BBBDisplaySDDEligibilityOnPDPDroplet| Exception occurred while fetching isHasSddAttribute from skuDetailVO ");
		} catch (BBBBusinessException be) {
			vlogError(be,
					"BBBDisplaySDDEligibilityOnPDPDroplet| Exception occurred while fetching isHasSddAttribute from skuDetailVO ");
		} catch (RepositoryException e) {
			vlogError(e,
					"BBBDisplaySDDEligibilityOnPDPDroplet| RepositoryException occurred while fetching SKU item from SKU id.");
		}
		/*
		 * BBBH-2388, if the Promo Attribute is true proceed further for the
		 * inventory check else return un-available message
		 */
		if (requireSDDCheck && (null == sddAttribs || !sddAttribs.equalsIgnoreCase(BBBCoreConstants.TRUE))) {
			logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| item is not eligible");
			setSddAvailabilityStatus(req, BBBCoreConstants.SDD_ITEM_INELIGIBLE, currentZipCode, orderBy, getBy);
			requireSDDCheck = false;
		}
		if (requireSDDCheck && !inStock.equalsIgnoreCase(BBBCoreConstants.TRUE)) {
			logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| item is not available in online inventory");
			setSddAvailabilityStatus(req, BBBCoreConstants.SDD_ITEM_UNAVAILABLE, currentZipCode, orderBy, getBy);
			requireSDDCheck = false;
		}
		if (!requireSDDCheck) {
			if (!(channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)
					|| channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
				req.serviceParameter(AVAIABLE_STATUS, req, res);
				req.serviceParameter(MESSAGE, req, res);
			}
			logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| service() | Ends");
			return;
		}
		if (!StringUtils.isEmpty(sessionBean.getSddStoreId())
				&& (null == fromAjax || (null != fromAjax && fromAjax.equalsIgnoreCase(BBBCoreConstants.FALSE)))) {
			logDebug(
					"BBBDisplaySDDEligibilityOnPDPDroplet| checking inventory for product with sdd store id in session "
							+ sessionBean.getSddStoreId());
			String sddStoreId = sessionBean.getSddStoreId();
			try {
				List<String> alStoreIds = new ArrayList<>();
				alStoreIds.add(sddStoreId);

				availableStoreId = getSearchStoreManager().getStoreWithInventoryByStoreId(alStoreIds, req, res, siteId);
				logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| availableStoreId is | " + availableStoreId);
			} catch (BBBBusinessException | BBBSystemException e) {
				logError(
						"BBBDisplaySDDEligibilityOnPDPDroplet| BBBBusinessException or BBBSystemException occurred whilegetStoreWithInventoryByStoreId "
								+ e.getMessage(),
						e);

			} catch (RepositoryException e) {
				logError(
						"BBBDisplaySDDEligibilityOnPDPDroplet| Exception occurred while fetching inventory from Repository "
								+ e.getMessage(),
						e);
			}
			orderBy = sessionBean.getCurrentZipcodeVO().getDisplayCutoffTime();
			getBy = sessionBean.getCurrentZipcodeVO().getDisplayGetByTime();
			if (StringUtils.isEmpty(availableStoreId)) {
				setSddAvailabilityStatus(req, BBBCoreConstants.SDD_ITEM_UNAVAILABLE, currentZipCode, orderBy, getBy);
			} else {
				setSddAvailabilityStatus(req, BBBCoreConstants.SDD_ITEM_AVAILABLE, currentZipCode, orderBy, getBy);

			}
		}
		/*
		 * The below scenario is while either Non Ajax flow(page load) and
		 * storeId is empty [OR] Ajax flow(confirm zipcode) with Eligibility
		 * True
		 */
		else {
			logDebug(
					"BBBDisplaySDDEligibilityOnPDPDroplet| checking inventory for product by iterating over all stores in region ");
			List<String> alStoreIds = new ArrayList<>();
			RegionVO regionVO = null;
			String regionId = sessionBean.getCurrentZipcodeVO().getRegionId();
			try {
				regionVO = getBbbCatalogTools().getStoreIdsFromRegion(regionId);
			} catch (BBBBusinessException | BBBSystemException e) {
				logError(
						"BBBDisplaySDDEligibilityOnPDPDroplet| Exception occurred while fetching regionVO from BBBCatalogTools ",
						e);
			}
			if (null != regionVO) {
				Set<String> sddStoreIdList = regionVO.getStoreIds();
				logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| sddStoreIdList is | " + sddStoreIdList.toString());
				if (null != sddStoreIdList) {
					Iterator<String> iter = sddStoreIdList.iterator();
					while (iter.hasNext()) {
						alStoreIds.add(iter.next());
					}
				}
				try {
					/*
					 * This method is called to fetch the storeId's if available
					 * and inventory is positive number
					 */
					availableStoreId = getSearchStoreManager().getStoreWithInventoryByStoreId(alStoreIds, req, res,
							siteId);
				} catch (BBBBusinessException | BBBSystemException e) {
					logError(
							"BBBDisplaySDDEligibilityOnPDPDroplet| BBBBusinessException or BBBSystemException occurred whilegetStoreWithInventoryByStoreId ",
							e);
				} catch (RepositoryException e) {
					logError(
							"BBBDisplaySDDEligibilityOnPDPDroplet| Exception occurred while fetching inventory from Repository ",
							e);
				}
				logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| availableStoreId is | " + availableStoreId);
				orderBy = sessionBean.getCurrentZipcodeVO().getDisplayCutoffTime();
				getBy = sessionBean.getCurrentZipcodeVO().getDisplayGetByTime();
				if (StringUtils.isEmpty(availableStoreId)) {
					sessionBean.getCurrentZipcodeVO().setStoreId(availableStoreId);
					setSddAvailabilityStatus(req, BBBCoreConstants.SDD_ITEM_UNAVAILABLE, currentZipCode, orderBy,
							getBy);
				} else {
					sessionBean.getCurrentZipcodeVO().setStoreId(availableStoreId);
					setSddAvailabilityStatus(req, BBBCoreConstants.SDD_ITEM_AVAILABLE, currentZipCode, orderBy, getBy);
				}

			} else {
				logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| market is not obtained");
				setSddAvailabilityStatus(req, BBBCoreConstants.SDD_ITEM_INELIGIBLE, currentZipCode, orderBy, getBy);
			}
		}
		if (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)
				|| channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)) {
			logDebug(
					"BBBDisplaySDDEligibilityOnPDPDroplet | " + channel + " | so not setting serviceParams in request");
		} else {
			req.serviceParameter(AVAIABLE_STATUS, req, res);
			req.serviceParameter(MESSAGE, req, res);
		}
		logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| service() | Ends");
	}

	private String getZipCodeFromSession(final BBBSessionBean sessionBean) {
		if (null != sessionBean && null != sessionBean.getCurrentZipcodeVO()) {
			return sessionBean.getCurrentZipcodeVO().getZipcode();
		}
		return null;
	}

	/*
	 * This method sets the availability messages based on the status, replaces
	 * the placeholders for the message to be rendered in the PDP
	 */

	public void setSddAvailabilityStatus(final DynamoHttpServletRequest req, final String status,
			final String currentZip, final String orderBy, final String getBy) {
		logDebug(
				"BBBDisplaySDDEligibilityOnPDP| setSddAvailabilityStatus() | Starts with inputParams status, currentZip, orderBy, getBy"
						+ status + "," + currentZip + "," + orderBy + "," + getBy);
		Map<String, String> placeholderMap = new HashMap<>();
		placeholderMap.put(STR_ZIP_CODE, currentZip);

		if (status.equalsIgnoreCase(BBBCoreConstants.SDD_ITEM_AVAILABLE)) {
			placeholderMap.put(STR_ORDER_BY, orderBy);
			placeholderMap.put(STR_GET_BY, getBy);
			final String availableMsg = getLblTxtTemplateManager().getPageTextArea(STR_LBL_SDD_AVAIL, placeholderMap);
			req.setParameter(AVAIABLE_STATUS, status);
			req.setParameter(MESSAGE, availableMsg);
		} else if (status.equalsIgnoreCase(BBBCoreConstants.SDD_ITEM_INELIGIBLE)) {
			final String unAvailableMsg = getLblTxtTemplateManager().getPageTextArea(STR_LBL_SDD_INELIGIBLE,
					placeholderMap);
			req.setParameter(AVAIABLE_STATUS, status);
			req.setParameter(MESSAGE, unAvailableMsg);
		} else if (status.equalsIgnoreCase(BBBCoreConstants.MARKET_INELIGIBLE)) {
			final String unAvailableMsg = getLblTxtTemplateManager().getPageTextArea(TXT_SDD_MARKET_UNAVAILABLE,
					placeholderMap);
			req.setParameter(AVAIABLE_STATUS, status);
			req.setParameter(MESSAGE, unAvailableMsg);
		} else if (status.equalsIgnoreCase(BBBCoreConstants.PRODUCT_SDD_ELIGIBLE)) {
			final String unAvailableMsg = getLblTxtTemplateManager().getPageTextArea(TXT_PRODUCT_SDD_ELIGIBLE,
					placeholderMap);
			req.setParameter(AVAIABLE_STATUS, status);
			req.setParameter(MESSAGE, unAvailableMsg);
		} else if (status.equalsIgnoreCase(BBBCoreConstants.PRODUCT_SDD_INELIGIBLE)) {
			final String unAvailableMsg = getLblTxtTemplateManager().getPageTextArea(TXT_PRODUCT_SDD_INELIGIBLE,
					placeholderMap);
			req.setParameter(AVAIABLE_STATUS, status);
			req.setParameter(MESSAGE, unAvailableMsg);
		} else if (status.equalsIgnoreCase(BBBCoreConstants.PRODUCT_SDD_MARKET_INELIGIBLE)) {
			final String unAvailableMsg = getLblTxtTemplateManager().getPageTextArea(TXT_PRODUCT_SDD_MARKET_INELIGIBLE,
					placeholderMap);
			req.setParameter(AVAIABLE_STATUS, status);
			req.setParameter(MESSAGE, unAvailableMsg);
		} else {
			final String unAvailableMsg = getLblTxtTemplateManager().getPageTextArea(STR_LBL_SDD_UNAVAIL,
					placeholderMap);
			req.setParameter(AVAIABLE_STATUS, status);
			req.setParameter(MESSAGE, unAvailableMsg);
		}
		logDebug("BBBDisplaySDDEligibilityOnPDP| setSddAvailabilityStatus() | Ends");

	}

	/**
	 * Gets the bbb catalog tools.
	 * 
	 * @return the bbb catalog tools
	 */
	public BBBCatalogToolsImpl getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	/**
	 * Sets the bbb catalog tools.
	 * 
	 * @param bbbCatalogTools
	 *            the new bbb catalog tools
	 */
	public void setBbbCatalogTools(BBBCatalogToolsImpl bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	/**
	 * @return the storeTools
	 */
	public StoreTools getStoreTools() {
		return mStoreTools;
	}

	/**
	 * @param pStoreTools
	 *            the storeTools to set
	 */
	public void setStoreTools(StoreTools pStoreTools) {
		mStoreTools = pStoreTools;
	}

	/**
	 * @return the sameDayDeliveryManager
	 */
	public BBBSameDayDeliveryManager getSameDayDeliveryManager() {
		return sameDayDeliveryManager;
	}

	/**
	 * Sets the sameDayDeliveryManager
	 * 
	 * @param sameDayDeliveryManager
	 */
	public void setSameDayDeliveryManager(BBBSameDayDeliveryManager sameDayDeliveryManager) {
		this.sameDayDeliveryManager = sameDayDeliveryManager;
	}

	/**
	 * @return the searchStoreManager
	 */
	public SearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	/**
	 * Sets the searchStoreManager
	 * 
	 * @param searchStoreManager
	 */
	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * Sets the catalogTools
	 * 
	 * @param catalogTools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the inventoryManager
	 */
	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	/**
	 * Sets the inventoryManager
	 * 
	 * @param inventoryManager
	 */
	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	/**
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	/**
	 * Sets the lblTxtTemplateManager
	 * 
	 * @param lblTxtTemplateManager
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/*
	 * This method is called from the mobile rest call[getSDDEligibilityStatus]
	 * for finding the Same Day Availability for the Mobile flow
	 */
	public SDDResponseVO getSDDEligibilityForREST(String customerZip, String skuId, String productId, String isAjax,
			String sddAttribs, String inStock) {
		logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| getSDDEligibilityForREST params " + customerZip + "|skuId-"
				+ skuId + "-|isAjax" + isAjax + "-|sddAttribs" + sddAttribs + "-|inStock" + inStock);
		SDDResponseVO sddResponseVO = new SDDResponseVO();
		DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
		try {
			req.setParameter(SDD_SITE_CAT_REF, skuId);
			req.setParameter(SDD_ZIP_CODE, customerZip);
			req.setParameter(SDD_SKU_ID, skuId);
			req.setParameter(BBBCoreConstants.PRODUCTID, productId);
			req.setParameter(SDD_IS_AJAX, isAjax);
			req.setParameter(SDD_ATTRIBS, sddAttribs);
			req.setParameter(SDD_INSTOCK, inStock);
			this.service(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse());
		} catch (ServletException | IOException e) {
			logError("BBBDisplaySDDEligibilityOnPDPDroplet| getSDDEligibilityForREST() | Exception:" + e.getMessage());
			return null;
		}
		sddResponseVO.setAvailableStatus(req.getParameter(SDD_AVAILABLE_STATUS));
		sddResponseVO.setDisplayMessage(req.getParameter(SDD_MESSAGE));

		sddResponseVO.setRegionVO((RegionVO) req.getAttribute(SDD_REGION_VO));
		logDebug("BBBDisplaySDDEligibilityOnPDPDroplet| getSDDEligibilityForREST ends");
		return sddResponseVO;
	}
}
