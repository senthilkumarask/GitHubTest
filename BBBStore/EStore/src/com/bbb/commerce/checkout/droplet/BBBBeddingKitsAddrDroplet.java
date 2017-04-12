package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.vo.BeddingShipAddrVO;
import com.bbb.utils.BBBUtility;
import com.bbb.framework.performance.BBBPerformanceMonitor;


/**
 * @author rsain4
 *
 */
public class BBBBeddingKitsAddrDroplet extends BBBDynamoServlet {


	private static final String IS_PACK_HOLD = "isPackHold";
	private static final String MM_DD_YYYY = "MM/dd/yyyy";
	private static final String ORDER_OBJECT = "order";
	private static String SCHOOL_COOKIE = "SchoolCookie";
	private String ATG_COMMERCE_SHOPPING_CART = "/atg/commerce/ShoppingCart";
	public final static String BEDDING_KIT = "beddingKit";
	public final static String NOT_BEDDING_KIT = "notBeddingKit";
	public final static String WEBLINK_ORDER = "weblinkOrder";
	public final static String BEDDING_SHIP_ADDR_VO = "beddingShipAddrVO";

	private BBBCatalogTools mCatalogTools;
	private MutableRepository siteRepository;
	private String PacknHoldDateMobile;

	public MutableRepository getSiteRepository() {
		return siteRepository;
	}

	public void setSiteRepository(MutableRepository siteRepository) {
		this.siteRepository = siteRepository;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	public void setPacknHoldDateMobile(String packnHoldDateMobile) {
		PacknHoldDateMobile = packnHoldDateMobile;
	}

	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("Entry BBBBeddingKitsDetailsDroplet.service");
		BBBPerformanceMonitor.start("BBBBdeddingKitsAddrDroplet", "service");
		final String siteId = getCurrentSiteId();
		SimpleDateFormat dateformat = new SimpleDateFormat(MM_DD_YYYY);
		BeddingShipAddrVO beddingShipAddrVO = null;
		Order order=null;		 
		boolean flagBeddingShip = false;
		boolean isWebLink = false;
		boolean isPackHold = false;
		Date date = new Date();
		String currentDate = dateformat.format(date);
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
			currentDate = BBBUtility.convertAppFormatDateToCAFormat(currentDate);
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date tommrrowDate = cal.getTime();
		String collegeIdValue = pRequest.getCookieParameter(SCHOOL_COOKIE);

		if(StringUtils.isBlank(collegeIdValue)){
			logDebug("BBBBeddingKitsDetailsDroplet :: College Id is null "+collegeIdValue);
			pRequest.serviceParameter(NOT_BEDDING_KIT, pRequest, pResponse);
		}

		if(pRequest.getObjectParameter(ORDER_OBJECT) !=null){
			order = (Order)pRequest.getObjectParameter(ORDER_OBJECT);
			logDebug("Order Details: "+order);

			if(collegeIdValue == null){
				collegeIdValue = ((BBBOrderImpl)order).getCollegeId();
			}
			
			if(pRequest.getObjectParameter(IS_PACK_HOLD) != null){
				isPackHold = true;
			}else{
				isPackHold = false;
			}
			
			if (order.getShippingGroups() != null && isPackHold && !StringUtils.isBlank(collegeIdValue)) {		 
				beddingShipAddrVO = getCatalogTools().validateBedingKitAtt(order.getShippingGroups(), collegeIdValue);
				try {
					if(beddingShipAddrVO != null)
					{
						if(!BBBUtility.isEmpty(beddingShipAddrVO.getShippingEndDate())){
							flagBeddingShip = getCatalogTools().validateBeddingAttDate(beddingShipAddrVO.getShippingEndDate(), currentDate);
			        		}	
					}else{
						beddingShipAddrVO = getCatalogTools().getBeddingShipAddrVO(collegeIdValue);
						if(beddingShipAddrVO != null)
						{
							if(!BBBUtility.isEmpty(beddingShipAddrVO.getShippingEndDate())){
								isWebLink = getCatalogTools().validateBeddingAttDate(beddingShipAddrVO.getShippingEndDate(), currentDate);
				        		}
						}
					}
				} catch (ParseException e) {
					if(beddingShipAddrVO != null){
					logError(LogMessageFormatter.formatMessage(
									pRequest, "Error while data parsing College Id : "
											+ collegeIdValue
											+ "beddingShipAddrVO.getShippingEndDate() : "
											+ beddingShipAddrVO.getShippingEndDate()
											+ "currentDate : " + currentDate), e);
					}
				}
			} 
			
			if(flagBeddingShip){
				pRequest.setParameter(BEDDING_SHIP_ADDR_VO, beddingShipAddrVO);				 
				pRequest.serviceParameter(BEDDING_KIT, pRequest, pResponse);
			}else if(isWebLink){
				pRequest.setParameter(BEDDING_SHIP_ADDR_VO, beddingShipAddrVO);				 
				pRequest.serviceParameter(WEBLINK_ORDER, pRequest, pResponse);
			}
			else{
				beddingShipAddrVO = new BeddingShipAddrVO();
				final String pAHStartDate = dateformat.format(tommrrowDate); 
				if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
					beddingShipAddrVO.setShippingStartDate(BBBUtility.convertAppFormatDateToCAFormat(pAHStartDate));
					}
				else{		
				beddingShipAddrVO.setShippingStartDate(pAHStartDate);
				}
				try {
					
					 final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
		                     BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
					 if (siteConfiguration.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) != null) {
						 final String pAHEndDateFromSite = dateformat.format((Date) siteConfiguration
                                 .getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME));
						 if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
								beddingShipAddrVO.setShippingEndDate(BBBUtility.convertAppFormatDateToCAFormat(pAHEndDateFromSite));
								}
							else{		
							beddingShipAddrVO.setShippingEndDate(pAHEndDateFromSite);
							}
		                }
					}
					 catch (final RepositoryException e) {
						 this.logError("Catalog API Method Name [getShippingEndDate]: RepositoryException "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
				        }
				pRequest.setParameter(BEDDING_SHIP_ADDR_VO, beddingShipAddrVO);
				pRequest.serviceParameter(NOT_BEDDING_KIT, pRequest, pResponse);
			}
		}
		else{
				logDebug("Order is null");
		}
			logDebug("Exit BBBBeddingKitsAddrDroplet.service");
			BBBPerformanceMonitor.end("BBBBeddingKitsAddrDroplet", "service");
			
	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
	/**
	 * Method to be called from mobiles
	 * @return
	 * @throws RepositoryException
	 */
	public String getPacknHoldDateMobile() throws RepositoryException {
		SimpleDateFormat currentDateformatter = new SimpleDateFormat("yyyy-MM-dd");
		final String siteId = getCurrentSiteId();
		
		logDebug("BBBBeddingKitsAddrDroplet.getPacknHoldDateMobile() method starts");	
	    final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
	    final OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_COMMERCE_SHOPPING_CART);
        BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
		if(order !=null){
			List<HardgoodShippingGroup> shipGrpList = order.getShippingGroups();
			if (shipGrpList != null ) {
				 final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
		                     BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);

					if (siteConfiguration.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) != null) {
						  PacknHoldDateMobile = currentDateformatter.format((Date) siteConfiguration
					             .getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME));

					    }
			}
		}
	 return PacknHoldDateMobile;
	}	

}