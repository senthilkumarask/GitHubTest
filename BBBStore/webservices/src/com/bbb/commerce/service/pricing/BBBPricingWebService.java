package com.bbb.commerce.service.pricing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;
import com.bedbathandbeyond.atg.MessageHeader;
import com.bedbathandbeyond.atg.PricingRequest;
import com.bedbathandbeyond.atg.PricingResponse;
import com.bedbathandbeyond.atg.Site;

/**
 * @author msiddi
 * 
 */
public class BBBPricingWebService extends BBBGenericService {

	private boolean mLoggingTransaction = false;
	/**
	 * Instance of PricingTools.
	 */
	private PricingTools mPricingTools;

	/**
	 * Instance of BBBPricingWSMapper.
	 */
	private BBBPricingWSMapper mPricingWSMapper;

	/**
	 * Instance of Profile Tools
	 */
	private BBBProfileTools mProfileTools;

	
	/**
	 * Instance of Site Repository
	 */
	private Repository siteRepository;

	/**
	 * Instance of PriceList Manager
	 */
	private PriceListManager mPriceListManager;

	
	private static final String NEW_USER = "user";
	
	private SiteContextManager siteContextManager;
	
	private static final String TBS_ORDER_IDENTIFIER = "TBS";
	private static final String CALLING_APP_CODE = "DS";
	

	/**
	 * This method will re-price the order - depending on if its partially
	 * shipped or not.
	 * 
	 * @param pricingRequest
	 * @return
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public PricingResponse priceOrder(final PricingRequest pricingRequest)
			throws BBBBusinessException, BBBSystemException {
		
		//Adding logic to change siteID inside MessageHeader for TBS orders.
		if(pricingRequest!=null && pricingRequest.getHeader() !=null) {
				setDSToATGSiteID(pricingRequest.getHeader());
		}
		
		final MessageHeader pricingHeader = pricingRequest.getHeader();
		PricingResponse pricingResponse = null;
		SiteContext siteContext = null;
		final Profile profile = new Profile(); 

		if (isLoggingTransaction()) {
			logDebug("Start priceOrder(),  Pricing Request :\n" + pricingRequest);
		}
		
		try {
			siteContext = pushSiteContext(pricingHeader);
		} catch (SiteContextException e) {
			logError("Site exception while performing pricing operation", e);
			throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1011,e.getMessage(), e);
		} catch (RepositoryException e) {
			logError("Repository exception while performing pricing operation", e);
			throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1012,e.getMessage(), e);
		}

		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			populateProfile(profile, siteContext.getSite(), siteContext);
			
			//Identify if this is a request for TBS order and set a parameter to identify same in PricingTools
			String identifier = pricingHeader.getOrderIdentifier();
			String callingApp = pricingHeader.getCallingAppCode();
			if (null != identifier && identifier.equalsIgnoreCase(TBS_ORDER_IDENTIFIER) && 
					null != callingApp && callingApp.equalsIgnoreCase(CALLING_APP_CODE)) {
				parameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, BBBCheckoutConstants.TRUE_STRING);
			}

			final BBBOrder bbbOrder = getPricingWSMapper().transformRequestToOrder(pricingRequest, profile, parameters);			
			
			Collection<RepositoryItem> pShippingPricingModels = new ArrayList<RepositoryItem>();
			Collection<RepositoryItem> pItemPricingModels = new ArrayList<RepositoryItem>();
			Collection<RepositoryItem> pOrderPricingModels = new ArrayList<RepositoryItem>();
			/*
			 * Here we extract promotion by promotion id or coupon id according
			 * to the type passed in request.
			 */
			final Map<String, Collection<RepositoryItem>> pricingModels = getPricingWSMapper().populatePromotions(
					bbbOrder, pricingRequest, profile);

			/*
			 * Here we merge global, profile promotions and the promotions
			 * present in the pricing service request and pass each type of
			 * promotion list to appropriate pricing engine through
			 * priceOrderSubtotalShipping method of PricingTools class.
			 */
			pShippingPricingModels.addAll(pricingModels.get(BBBCheckoutConstants.SHIPPING_PROMOTIONS));
			pItemPricingModels.addAll(pricingModels.get(BBBCheckoutConstants.ITEM_PROMOTIONS));
			pOrderPricingModels.addAll(pricingModels.get(BBBCheckoutConstants.ORDER_PROMOTIONS));
			
			/*
			 * Here we collect only shipping global promotion.
			 */
			pShippingPricingModels.addAll(getPricingTools().getShippingPricingEngine()
			.getPricingModels(profile));
			
			parameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, bbbOrder);
			vlogDebug(
					"BBBPricingWebService.priceOrder: We are going to apply Item promotions - {0} , Order promotions - {1} and Shipping promotions - {2} to order {3}",
					pItemPricingModels, pOrderPricingModels, pShippingPricingModels, bbbOrder);
			/*
			 * Calling pricing service and updates each types of adjustment to
			 * respective priceInfo object.
			 */
			getPricingTools().priceOrderSubtotalShipping(bbbOrder, pItemPricingModels, pShippingPricingModels,
					pOrderPricingModels, getPricingTools().getDefaultLocale(), profile, parameters);
			
			//Reset SiteID in the header	
			if(pricingRequest!=null && pricingRequest.getHeader() !=null) {
			    setATGToDSSiteID(pricingRequest.getHeader());
			}
			/* Generate Pricing response */
			pricingResponse = getPricingWSMapper().transformOrderToResponse(bbbOrder, pricingRequest, parameters);
			
			if (isLoggingTransaction()) {
				logDebug("End priceOrder(), Pricing Response :\n" + pricingResponse);
			}

			return pricingResponse;
		} catch (PricingException e) {
			logError("Pricing exception while performing pricing operation", e);
			throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1011,e.getMessage(), e);
		} catch (BBBBusinessException e) {
			logError("Business exception while performing pricing operation", e);
			throw e;
		} catch (BBBSystemException e) {
			logError("System exception while performing pricing operation", e);
			throw e;
		} catch (RepositoryException e) {
			logError("Repository exception while performing pricing operation", e);
			throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1012,e.getMessage(), e);
		}
		
		finally {
			getSiteContextManager().popSiteContext(siteContext);
		}
	}

	private SiteContext pushSiteContext(MessageHeader pricingHeader) throws SiteContextException, RepositoryException {
		
		logDebug("Start pushSiteContext(): MessageHeader - :\n" + pricingHeader);
		
		final String siteId = String.valueOf(pricingHeader.getSiteId());		
		
		SiteContext context = getSiteContextManager().getSiteContext(siteId);
		getSiteContextManager().pushSiteContext(context);
		return context;
	}

	/**
	 * Populates profile attributes.
	 * 
	 * @param pricingHeader
	 *            pricingHeader from PricingRequest
	 * @param profile
	 *            profile
	 * @param site 
	 * @throws RepositoryException 
	 */
	private void populateProfile(final Profile profile, RepositoryItem site, SiteContext siteContext) throws RepositoryException {
		
		getProfileTools().createNewUser(NEW_USER, profile);		
		String siteId = siteContext.getSite().getId();
			
		RepositoryItem priceList = getPriceListManager().determinePriceList(profile, site, getPriceListManager().getPriceListPropertyName());
		profile.setPropertyValue(getPriceListManager().getPriceListPropertyName(), priceList);		
		if(BBBUtility.isNotEmpty(siteId) && (siteId.equals(BBBCoreConstants.SITE_BAB_US) || siteId.equals(BBBCoreConstants.SITE_BBB))){
			profile.setPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE, BBBInternationalShippingConstants.DEFAULT_COUNTRY);
			profile.setPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE, BBBInternationalShippingConstants.CURRENCY_USD);
		}else if (BBBUtility.isNotEmpty(siteId) && siteId.equals(BBBCoreConstants.SITE_BAB_CA)){
			profile.setPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE, BBBInternationalShippingConstants.CURRENCY_CODE_CA);
			profile.setPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE, BBBInternationalShippingConstants.CURRENCY_CODE_CAD);
		}

		logDebug("End populateProfile(): Updated Profile value- :\n" + profile);		

	}
	
	/**
	 * This method is responsible for setting TBS siteId in MessageHeader when DS sends its custom siteID.
	 * For e.g. BedBathUS sent by DS for TBS orders is mapped to TBS_BedBathUS
	 * This is done so that TBS specific promotions are applied on order are again applied during Repricing of order. 
	 * @param messageHeader 
	 * 
	 */
	private void setDSToATGSiteID(MessageHeader messageHeader){
		if (null != messageHeader.getOrderIdentifier() && 
				messageHeader.getOrderIdentifier().equalsIgnoreCase(TBS_ORDER_IDENTIFIER)) {
			if(isLoggingDebug()){
				logDebug("BBBPricingWebService : setDSToATGSiteID() : siteID passed from DS for TBS Order = "+messageHeader.getSiteId());
			}
			if(messageHeader.getSiteId()!=null && messageHeader.getSiteId().equals(Site.BED_BATH_US)){
				messageHeader.setSiteId(Site.TBS_BED_BATH_US);
			}else if(messageHeader.getSiteId()!=null && messageHeader.getSiteId().equals(Site.BED_BATH_CANADA)){
				messageHeader.setSiteId(Site.TBS_BED_BATH_CANADA);
			}else if(messageHeader.getSiteId()!=null && messageHeader.getSiteId().equals(Site.BUY_BUY_BABY)){
				messageHeader.setSiteId(Site.TBS_BUY_BUY_BABY);
			}
		}
	}
	
	/**
	 * This method is responsible for setting DS siteId in MessageHeader before sending a response to DS
	 * For e.g. BedBathUS will be sent to DS in response for ATG Sites TBS_BedBathUS.
	 * @param messageHeader 
	 * 
	 */
	private void setATGToDSSiteID(MessageHeader messageHeader){
			if(messageHeader.getSiteId()!=null && 
					(messageHeader.getSiteId().equals(Site.TBS_BED_BATH_US))){
				messageHeader.setSiteId(Site.BED_BATH_US);
			}else if(messageHeader.getSiteId()!=null && 
					(messageHeader.getSiteId().equals(Site.TBS_BED_BATH_CANADA))){
				messageHeader.setSiteId(Site.BED_BATH_CANADA);
			}else if(messageHeader.getSiteId()!=null && 
					messageHeader.getSiteId().equals(Site.TBS_BUY_BUY_BABY)){
				messageHeader.setSiteId(Site.BUY_BUY_BABY);
			}
			
			if(isLoggingDebug()){
				logDebug("BBBPricingWebService : setATGToDSSiteID() : siteID passed to DS in response = "+messageHeader.getSiteId());
			}
	}
	
	/**
	 * @return the loggingTransaction
	 */
	public final boolean isLoggingTransaction() {
		return mLoggingTransaction;
	}

	/**
	 * @param pLoggingTransaction
	 *            the loggingTransaction to set
	 */
	public final void setLoggingTransaction(final boolean pLoggingTransaction) {
		mLoggingTransaction = pLoggingTransaction;
	}

	/**
	 * @return the profileTools
	 */
	public final BBBProfileTools getProfileTools() {
		return mProfileTools;
	}

	/**
	 * @param pProfileTools
	 *            the profileTools to set
	 */
	public final void setProfileTools(final BBBProfileTools pProfileTools) {
		mProfileTools = pProfileTools;
	}


	/**
	 * Getter for Pricing WSMapper.
	 * 
	 * @return mPricingWSMapper
	 */
	public BBBPricingWSMapper getPricingWSMapper() {
		return mPricingWSMapper;
	}

	/**
	 * Setter for Pricing WSMapper.
	 * 
	 * @param pPricingWSMapper
	 */
	public void setPricingWSMapper(final BBBPricingWSMapper pPricingWSMapper) {
		this.mPricingWSMapper = pPricingWSMapper;
	}

	/**
	 * Setter for Pricing Tool.
	 * 
	 * @param pPricingTools
	 */
	public void setPricingTools(final PricingTools pPricingTools) {
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
	 * @return the siteRepository
	 */
	public Repository getSiteRepository() {
		return siteRepository;
	}

	/**
	 * @param siteRepository
	 *            the siteRepository to set
	 */
	public void setSiteRepository(final Repository siteRepository) {
		this.siteRepository = siteRepository;
	}

	/**
	 * @return the mPriceListManager
	 */
	public PriceListManager getPriceListManager() {
		return mPriceListManager;
	}

	/**
	 * @param mPriceListManager
	 *            the mPriceListManager to set
	 */
	public void setPriceListManager(final PriceListManager priceListManager) {
		mPriceListManager = priceListManager;
	}

	/**
	 * @return the siteContextManager
	 */
	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}

	/**
	 * @param siteContextManager the siteContextManager to set
	 */
	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}
}