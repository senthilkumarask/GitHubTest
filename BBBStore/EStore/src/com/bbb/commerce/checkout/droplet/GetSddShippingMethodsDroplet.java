package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.pricing.PricingTools;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;


/**
 * To get shipping methods for Same Day Delivery on ajax call
 *
 * @author sghosh
 */

public class GetSddShippingMethodsDroplet extends BBBDynamoServlet {

	private static final String CHOOSE_SDD_OPTION = "chooseSddOption";

	private static final String REGISTRY_ZIP = "registryZip";

	private static final String CURRENT_ZIP = "currentZip";

	/**
	 * Instance of Shipping manager.
	 */
	private BBBShippingGroupManager mShippingGroupManager;

	/**
	 * Instance of PricingTools.
	 */
	private PricingTools mPricingTools;
	
	
	/** The same day delivery manager. */
	private BBBSameDayDeliveryManager sameDayDeliveryManager;

	/**
	 * Gets the same day delivery manager.
	 *
	 * @return the same day delivery manager
	 */
	public BBBSameDayDeliveryManager getSameDayDeliveryManager() {
		return sameDayDeliveryManager;
	}

	/**
	 * Sets the same day delivery manager.
	 *
	 * @param sameDayDeliveryManager the new same day delivery manager
	 */
	public void setSameDayDeliveryManager(
			BBBSameDayDeliveryManager sameDayDeliveryManager) {
		this.sameDayDeliveryManager = sameDayDeliveryManager;
	}
	
	/**
	 * Setter for Pricing Tool.
	 *
	 * @param pPricingTools the new pricing tools
	 */
	public void setPricingTools(PricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}

	/**
	 * Getter for Pricing Tool.
	 * 
	 * @return mPricingTools
	 */
	public PricingTools getPricingTools() {
		return mPricingTools;
	}

	/**
	 * Getter for BBBShippingGroupManager.
	 * 
	 * @return mShippingGroupManager.
	 */
	public BBBShippingGroupManager getShippingGroupManager() {
		return mShippingGroupManager;
	}

	/**
	 * Setter for BBBShippingGroupManager.
	 * 
	 * @param pShipingGrpMgr
	 *            BBB Shipping Group Manager
	 */
	public void setShippingGroupManager(
			final BBBShippingGroupManager pShipingGrpMgr) {
		this.mShippingGroupManager = pShipingGrpMgr;
	}

	/** BBBH-2379 - Shipping page changes (MPC)
	 * This methods adds list of shipping methods in request for order param
	 * being passed in the request.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @return void
	 * @throws ServletException , IOException
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start("GetSddShippingMethodsDroplet", "service");
		logDebug("starting - service method GetSddShippingMethodsDroplet");
		final BBBOrder order = (BBBOrder) pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);
		String shippingZip = BBBCoreConstants.BLANK;
		String eligibilityStatus = BBBCoreConstants.BLANK;
		final String chooseSddOption = (String) pRequest
				.getParameter(CHOOSE_SDD_OPTION);
		BBBSessionBean sessionBean = ((BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN));
		try {
			List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>();

			// We will add Sdd Shipping method to be displayed after zip change or sdd option selection
			getSameDayDeliveryManager().getBbbCatalogTools().updateListWithSddShipMethod(shipMethodVOList);

			List<HardgoodShippingGroup> hardgoodShippingGroupList = getShippingGroupManager()
					.getHardgoodShippingGroups(order);

			//This flow is when user selects SDD shipping method and we check for only inventory
			if(!StringUtils.isBlank(chooseSddOption) && Boolean.valueOf(chooseSddOption)){
				shippingZip = BBBUtility.hyphenExcludedZip((String) pRequest.getParameter(CURRENT_ZIP));
				logDebug("GetSddShippingMethodsDroplet : Starting the Same Day Delivery option selection flow for checking the SBC inventory with zip : " + shippingZip);
				if (StringUtils.isBlank(shippingZip) ){
					if(sessionBean.getCurrentZipcodeVO() != null){
						shippingZip = sessionBean.getCurrentZipcodeVO().getZipcode();
					}
				}
				if (!StringUtils.isBlank(shippingZip) ){
					if(sessionBean.getShippingZipcodeVO() != null){
						RegionVO regionVO = getSameDayDeliveryManager().getBbbCatalogTools().getStoreIdsFromRegion(sessionBean.getShippingZipcodeVO().getRegionId());
						if(regionVO != null){
							eligibilityStatus = getSameDayDeliveryManager().checkSBCInventoryForSdd(regionVO.getStoreIds(), order, false);
						}
					}

				}
			} 
			//This flow is when user changes zipcode by editing/adding shipping address. Check for market eligibilty and item eligibility
			else{
				shippingZip = BBBUtility.hyphenExcludedZip((String) pRequest.getParameter(CURRENT_ZIP));
				final String DISPLAY_SDD_ALWAYS="displaySDDAlways";
				logDebug("GetSddShippingMethodsDroplet : Starting the Same Day Delivery shipping zipcode change flow. Shipping Zip from request parameter is : " + shippingZip);
				if (StringUtils.isBlank(shippingZip) ){
					if(sessionBean.getCurrentZipcodeVO() != null){
						shippingZip = sessionBean.getCurrentZipcodeVO().getZipcode();
						logDebug("Obtaining zip from session: " + shippingZip);
						
						// We got the new zipcode for which we will populate the session and fetch the new market
						RegionVO regionVO = getSameDayDeliveryManager()
								.populateDataInVO(sessionBean, pRequest, shippingZip,
										BBBCoreConstants.RETURN_FALSE,
										BBBCoreConstants.RETURN_FALSE,
										BBBCoreConstants.RETURN_TRUE);
						eligibilityStatus = getSameDayDeliveryManager().checkForSDDEligibility(pRequest, order, regionVO, shipMethodVOList, shippingZip);
						if(regionVO != null){
							pRequest.setParameter("regionVO", regionVO);
						}
						
						if( isDisplaySSD(regionVO, sessionBean, true)){
							logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" );
							pRequest.getSession().setAttribute(DISPLAY_SDD_ALWAYS, BBBCoreConstants.TRUE);
						}
					}
				} else if(shippingZip.equals(REGISTRY_ZIP)){
					logDebug("Registry Zip flow : ");
					eligibilityStatus = BBBCoreConstants.ADDRESS_INELIGIBLE;
					
					if( null != sessionBean && isDisplaySSD(null, sessionBean, false)){
						logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" );
						pRequest.getSession().setAttribute(DISPLAY_SDD_ALWAYS, BBBCoreConstants.TRUE);
					}
					
					
				} else{
					logDebug("Obtaining zip from request param flow ");
					RegionVO regionVO = getSameDayDeliveryManager()
							.populateDataInVO(sessionBean, pRequest,
									shippingZip, BBBCoreConstants.RETURN_FALSE,
									BBBCoreConstants.RETURN_FALSE,
									BBBCoreConstants.RETURN_TRUE);
					eligibilityStatus = getSameDayDeliveryManager().checkForSDDEligibility(pRequest, order, regionVO, shipMethodVOList, shippingZip);
					if(regionVO != null){
						pRequest.setParameter("regionVO", regionVO);
					}
					
					if( isDisplaySSD(regionVO, sessionBean, true)){
						logDebug("GetSddShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" );
						pRequest.getSession().setAttribute(DISPLAY_SDD_ALWAYS, BBBCoreConstants.TRUE);
					}
				}
			}
			
			logDebug("GetSddShippingMethodsDroplet returns sdd eligibilityStatus as" + eligibilityStatus);
			if(!StringUtils.isBlank(eligibilityStatus) && (eligibilityStatus.equals(BBBCoreConstants.ITEM_ELIGIBLE) ||
					eligibilityStatus.equals(BBBCoreConstants.SDD_ELIGIBLE))){
				pRequest.setParameter(BBBCoreConstants.SDD_OPTION_ENABLED, true);
				logDebug("GetSddShippingMethodsDroplet option as enabled since sdd eligibility conditions are met");
			}
			
			pRequest.setParameter(BBBCoreConstants.SDD_ELIGIBLITY_STATUS, eligibilityStatus);
			
			getShippingGroupManager().calculateShippingCost(shipMethodVOList, order,
					hardgoodShippingGroupList, shippingZip);

			pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,
					shipMethodVOList);
			pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest,
					pResponse);


		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"SystemException"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BusinessException"), e);

		} catch (RepositoryException re) {
			logError(LogMessageFormatter.formatMessage(null,
					"RepositoryException"), re);
		}
		logDebug("Exiting - service method GetSddShippingMethodsDroplet");

		BBBPerformanceMonitor.end("GetSddShippingMethodsDroplet", "service");
	}
	
	/**
	 * this method is use to check DISPLAY_SDD_ALWAYS flag
	 * @param regionVO - region VO
	 * @param sessionBean - Session Bean
	 * @param includeRegision - flag for region VO
	 * @return
	 */
	protected boolean isDisplaySSD (RegionVO regionVO, BBBSessionBean sessionBean,boolean includeRegision){
		boolean result = false;
		
		
		if(includeRegision){
			result = regionVO != null
					|| (null != sessionBean
							.getCurrentZipcodeVO() && sessionBean
							.getCurrentZipcodeVO()
							.isSddEligibility())
					|| (null != sessionBean
									.getLandingZipcodeVO() && sessionBean
							.getLandingZipcodeVO()
							.isSddEligibility());
		} else{
			result =  (null != sessionBean
					.getCurrentZipcodeVO() && sessionBean
					.getCurrentZipcodeVO()
					.isSddEligibility())
			|| (null != sessionBean
							.getLandingZipcodeVO() && sessionBean
					.getLandingZipcodeVO()
					.isSddEligibility());
		}
	
		return result;			
			
	}

	
}
