package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContext;
import atg.repository.RepositoryException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegInfoVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.BBBPropertyManager;

/**
 * This droplet Fetch Registry Info from registry Id and display registry
 * summary info.
 *
 * @author skalr2
 *
 */
public class RegistryInfoNewDisplayDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	/** The Registry info service name. */
	private String mRegistryInfoServiceName;

	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;

	private BBBPropertyManager pmgr;

	/** The Constant REGISTRYSUMMARYVO. */
	//private static final String REGISTRYSUMMARYVO = "registrySummaryVO";

	//private static final String REGISTRYVO = "registryVO";
	//private static final String PROFILE_FAVOURITE_STORE = "profileFavouriteStore";

	/** The Constant EVENTDATE. */
	//private static final String EVENTDATE = "eventDate";

	/** The Constant EVENTDATE. */
	//private static final String FUTURE_SHIPPING_DATE = "futureShippingDate";

	/** The Constant VALIDCHECK. */
	private static final String VALIDCHECK = "validCheck";

	/** The Constant REGISTRY_URL. */
	//private static final String REGISTRY_URL = "registryURL";

	/** The Constant CO_REG_FLAG. */
	//private static final String CO_REG_FLAG = "coRegFlag";

	/** The Constant ERR_NO_REG_INFO. */
	private static final String ERR_NO_REG_INFO = "err_no_reg_info";

	/** The Constant ERR_INVALID_REG_INFO_REQ. */
	//private static final String ERR_INVALID_REG_INFO_REQ = "err_invalid_reg_info_req";

//	/** The Constant ERR_REGINFO_BIZ. */
//	private static final String ERR_REGINFO_BIZ = "err_reginfo_biz_error";

	/** The Constant ERR_REGINFO_SYS. */
	//private static final String ERR_REGINFO_SYS = "err_reginfo_sys_error";

	/** The Constant ERR_REGINFO_FATAL_ERROR. */
	//private static final String ERR_REGINFO_FATAL_ERROR = "err_gift_reg_fatal_error";

	/** The Constant ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR. */
	//private static final String ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR = "err_gift_reg_siteflag_usertoken_error";

	/** The Constant ERR_REGINFO_INVALID_INPUT_FORMAT. */
	//private static final String ERR_REGINFO_INVALID_INPUT_FORMAT = "err_gift_reg_invalid_input_format";

	//private static final String SESSION_BEAN = "/com/bbb/profile/session/SessionBean";

	/** Profile reference variable. */
	private Profile profile;

	/** url for registryGuest view jsp. */
	private String regGuestViewURL;

	/** The Site context. */
	private SiteContext mSiteContext;

	/** The gift reg session bean. */
	private GiftRegSessionBean mGiftRegSessionBean;

	private static LblTxtTemplateManager contentManager;
	/**
	 * Gets the site context.
	 *
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return this.mSiteContext;
	}

	/**
	 * Sets the site context.
	 *
	 * @param pSiteContext
	 *            the siteContext to set
	 */
	public void setSiteContext(final SiteContext pSiteContext) {
		this.mSiteContext = pSiteContext;
	}

	/**
	 * Fetch Registry Info from registry Id and display registry summary info.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug(" RegistryInfoDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		BBBPerformanceMonitor.start("RegistryInfoDisplayDroplet", "GetRegistryInfo");
		//RegistryResVO registryResVO = null;
		//RegistrySummaryVO registrySummaryVO = null;
		//RegistryVO registryVO = null;
		//final RegistryReqVO regReqVO = new RegistryReqVO();

		String registryId = null;
		String siteId = null;
		//String evDate = null;
		//String futureShippingDate = null;
		//boolean coRegFlag = false;
		
		//List<RepositoryItem> regAddresses=new ArrayList<RepositoryItem>();

		/* variable to find if request is from owner or guest */
		String displayView = null;
		//final DateFormat sdf = new SimpleDateFormat(
				//BBBGiftRegistryConstants.DATE_FORMAT);
		//final SimpleDateFormat monthParse = new SimpleDateFormat("MM", pRequest.getLocale());
		//final SimpleDateFormat monthDisplay = new SimpleDateFormat("MMMM", pRequest.getLocale());

		registryId = pRequest
				.getParameter(BBBGiftRegistryConstants.REGISTRY_ID);
		displayView = pRequest
				.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW);

		siteId = this.getSiteContext().getSite().getId();
		this.logDebug(BBBCoreConstants.SITE_ID + "[" + siteId + "]");
		this.logDebug(BBBGiftRegistryConstants.REGISTRY_ID + "[" + registryId + "]");
		

			// if valid registry
			if (this.validateRegistry(registryId, displayView, pRequest)) {

				//RegAddressesVO regAddressesVO=new RegAddressesVO();
				//RegSkuListVO regSkuListVO=new RegSkuListVO();
				RegInfoVO regInfoVO=new RegInfoVO();

				// calling the GiftRegistryManager's getRegistryInfo method
					try {
						//this.getGiftRegistryManager().getRegistrySummaryVO(registryId, siteId);
						regInfoVO = this.getGiftRegistryManager().getRegistryInfoFromDB(registryId, siteId);
						//regAddressesVO = this.getGiftRegistryManager().getRegistryAddressesFromDB(registryId);
						//regSkuListVO = this.getGiftRegistryManager().getRegistrySkuDetailsFromDB(registryId);

					} catch (BBBSystemException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logError(e.getMessage(),e);
					} catch (BBBBusinessException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logError(e.getMessage(),e);
					} catch (RepositoryException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logError(e.getMessage(),e);
					}

					if (regInfoVO != null) {

							int registryNum =regInfoVO.getRegistryNum();
							String eventType =regInfoVO.getEventType();
							int registryDT = regInfoVO.getEventDate();
//							for(RegAddressesVO item:regAddressesVO){
//							int nameAddrNum =(Integer) item.getPropertyValue("nameAddrNum");
//							pRequest.setParameter("nameAddrNum",nameAddrNum);
//							}
//							if(regSkuItems!=null){
//								for(RepositoryItem item:regSkuItems){
//									int skuId =(Integer) item.getPropertyValue("skuId");
//									pRequest.setParameter("skuId",skuId);
//								}
//							}
							pRequest.setParameter("registryNum",registryNum);
							pRequest.setParameter("eventType",eventType);
							pRequest.setParameter("registryDT",registryDT);
							
							
							
						
							pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
									pResponse);
						}else {
						pRequest.setParameter(OUTPUT_ERROR_MSG, ERR_NO_REG_INFO);
						pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
					}
			

			}
	}

	/**
	 * This method validates if the registryId belongs to this profile
	 *
	 * Return false if pRegistryId is null Return true if request is not from
	 * owner view Return true if the pRegistryId is in users's registries set
	 * otherwise return false.
	 *
	 * @param pRegistryId
	 *            the registry id
	 * @param displayView
	 *            the display view
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 */
	private boolean validateRegistry(final String pRegistryId, final String displayView,
			final DynamoHttpServletRequest pRequest) {

		boolean success = false;
		boolean validCheck = false;

		if (pRegistryId == null) {

			return success;

		} else if ((displayView == null)
				|| !displayView
						.equalsIgnoreCase(BBBGiftRegistryConstants.OWNER_VIEW)) {

			validCheck = this.getGiftRegistryManager().isUserOwnRegistry(this.getProfile(), pRegistryId, pRequest);
			pRequest.setParameter(VALIDCHECK, validCheck);
			success = true;

		} else {

			// if user is transient
			if (this.getProfile().isTransient()) {
				success = false;

			} else {
				// //check user
				success = this.getGiftRegistryManager().isUserOwnRegistry(this.getProfile(), pRegistryId, pRequest);
				pRequest.setParameter(VALIDCHECK, success);
			}
		}

		return success;

	}

	/**
	 * Gets the gift registry manager.
	 *
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return this.mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 *
	 * @param giftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(
			final GiftRegistryManager giftRegistryManager) {
		this.mGiftRegistryManager = giftRegistryManager;
	}

	/**
	 * Gets the registry info service name.
	 *
	 * @return the registryInfoServiceName
	 */
	public String getRegistryInfoServiceName() {
		return this.mRegistryInfoServiceName;
	}

	/**
	 * Sets the registry info service name.
	 *
	 * @param registryInfoServiceName
	 *            the registryInfoServiceName to set
	 */
	public void setRegistryInfoServiceName(final String registryInfoServiceName) {
		this.mRegistryInfoServiceName = registryInfoServiceName;
	}

	/**
	 * Gets the catalog tools.
	 *
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param pCatalogTools
	 *            the new catalog tools
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	 * Gets the reg guest view url.
	 *
	 * @return the reg guest view url
	 */
	public String getRegGuestViewURL() {
		return this.regGuestViewURL;
	}

	/**
	 * Sets the reg guest view url.
	 *
	 * @param regGuestViewURL
	 *            the new reg guest view url
	 */
	public void setRegGuestViewURL(final String regGuestViewURL) {
		this.regGuestViewURL = regGuestViewURL;
	}

	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	public Profile getProfile() {
		final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final Profile profile = (Profile) request.resolveName(BBBCoreConstants.ATG_PROFILE);
		return profile;
	}

	/**
	 * Sets the profile.
	 *
	 * @param profile
	 *            the profile to set
	 */
	public void setProfile(final Profile profile) {
		this.profile = profile;
	}

	/**
	 * @return the pmgr
	 */
	public BBBPropertyManager getPmgr() {
		return this.pmgr;
	}

	/**
	 * @param mPmgr the mPmgr to set
	 */
	public void setPmgr(final BBBPropertyManager pmgr) {
		this.pmgr = pmgr;
	}

	/**
	 * @return the giftRegSessionBean
	 */
	public GiftRegSessionBean getGiftRegSessionBean() {
		return this.mGiftRegSessionBean;
	}

	/**
	 * @param giftRegSessionBean the giftRegSessionBean to set
	 */
	public void setGiftRegSessionBean(final GiftRegSessionBean giftRegSessionBean) {
		this.mGiftRegSessionBean = giftRegSessionBean;
	}
}
