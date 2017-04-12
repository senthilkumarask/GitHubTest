/*
 * 
 */
package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import com.bbb.account.BBBProfileManager;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.cms.PromoBoxVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.checklist.vo.CategoryVO;
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.commerce.checklist.vo.MyItemCategoryVO;
import com.bbb.commerce.checklist.vo.MyItemVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.utility.PriceListComparator;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

/**
 * This droplet Fetch Registry Item List from registry Id and display registry
 * item list.
 * 
 * @author skalr2
 * 
 */
public class RegistryItemsDisplayDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	/** The Registry items list service name. */
	private String mRegistryItemsListServiceName;
	
	private PriceListManager priceListManager;
	
	private String profilePriceListPropertyName;
	
	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;

	private static final String SITE_ID = "siteId";
	private static final String EVENT_DATE = "eventDate";
	/** The Constant ERR_REGINFO_FATAL_ERROR. */
	private static final String ERR_REGINFO_FATAL_ERROR = "err_gift_reg_fatal_error";
	private static final String BUYOFF_SHOW_START_BROWSING = "showStartBrowsing";
	private static final String BROWSE_ITEMPURCHASED_LIMIT = "buyoffItemPurchasedLimit";
	private static final String BROWSE_UNFULLFILLED_SIZE_LIMIT = "buyoffItemPurchasedPercentageLimit";
	private static final String PLUS = "+";
	private static final String DOLLAR = "$";
	private static final String HYPHEN = "-";
	/** The Constant ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR. */
	private static final String ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR = "err_gift_reg_siteflag_usertoken_error";

	/** The Constant ERR_REGINFO_INVALID_INPUT_FORMAT. */
	private static final String ERR_REGINFO_INVALID_INPUT_FORMAT = "err_gift_reg_invalid_input_format";
	
	private int mCertonaListMaxCount;
	
	private String topRegMaxCount;
	
	private BBBEximManager eximManager;
	
	private CheckListManager checkListManager;
	
	/**
	 * @return the mCertonaListMaxCount
	 */
	public final int getCertonaListMaxCount() {
		return mCertonaListMaxCount;
	}

	/**
	 * @param mCertonaListMaxCount the mCertonaListMaxCount to set
	 */
	public final void setCertonaListMaxCount(int pCertonaListMaxCount) {
		this.mCertonaListMaxCount = pCertonaListMaxCount;
	}
	
	/**
	 * Fetch Registry Item List from registry Id and display registry item list.
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
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		logDebug(" RegistryItemsDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		BBBPerformanceMonitor.start("RegistryItemsDisplayDroplet", "GetRegistryItemList");
		
		String isSecondCall = pRequest.getParameter(IS_SECOND_CALL);
		
		/*Second ajax call for splitted calls for showing redesigned My Items Tab*/ 
		if(BBBUtility.isNotEmpty(isSecondCall) && isSecondCall.equalsIgnoreCase(BBBCoreConstants.TRUE)){
			/*Second ajax call to populate registry items in rest of the categories*/
			setRegItemsSecondCall(pRequest, pResponse);
		}
		else{
		
		RegistryItemsListVO registryItemsListVO = null;
		String siteId = null;
		String registryId = null;
		String c2id = null;
		String c3id = null;
		String c1id = null;
		int startIdx = 0;
		int blkSize = 0;
		Boolean isGiftGiver = false;
		Boolean isMxGiftGiver = false;
		Boolean isAvailForWebPurchaseFlag = false;
		String sortSeq = null;
		String view = null;
		String eventTypeCode = null;
		double listPrice = 0.0;
		double salePrice = 0.0;
		String regEventDate = null; 
		boolean enableBuyOffRegistry = false;
		Boolean isMyItemsCheckList = false;
			registryId = pRequest.getParameter( REGISTRY_ID );
			c2id = pRequest.getParameter(C2_ID);
			c3id = pRequest.getParameter(C3_ID);
			c1id = pRequest.getParameter(C1_ID);
			sortSeq = pRequest.getParameter(SORT_SEQ);
			view = pRequest.getParameter(VIEW);
			eventTypeCode = pRequest.getParameter(REG_EVENT_TYPE_CODE);
			startIdx = Integer.valueOf(pRequest.getParameter(START_INDEX));
			blkSize = Integer.valueOf(pRequest.getParameter(BULK_SIZE));
			isGiftGiver = Boolean.valueOf(pRequest.getParameter(IS_GIFT_GIVER));
			isMxGiftGiver = Boolean.valueOf(pRequest.getParameter("isMxGiftGiver"));
			isAvailForWebPurchaseFlag = Boolean.valueOf(pRequest.getParameter(IS_AVAIL_WEBPUR));
			siteId = pRequest.getParameter(SITE_ID_PARAM);
			regEventDate = pRequest.getParameter(EVENT_DATE_PARAM);
			isMyItemsCheckList = Boolean.valueOf(pRequest.getParameter(IS_MY_ITEMS_CHECKLIST));
			
			final Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
			
			if(!isGiftGiver){
				try {
					mGiftRegistryManager.isRegistryOwnedByProfile(profile.getRepositoryId(),registryId,siteId);
				} catch (BBBBusinessException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logError(e.getMessage(),e);
				} catch (BBBSystemException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					logError(e.getMessage(),e);
				}
			}
			
			if (sortSeq == null) {
				sortSeq = BBBGiftRegistryConstants.DEFAULT_CAT_SORT_SEQ;
			}
			if (view == null) {
				view = BBBGiftRegistryConstants.DEFAULT_ALL_VIEW_FILTER;
			}

			logDebug("pSiteId[" + siteId + "]");
			logDebug("pRegistryId[" + registryId + "]");
			
			
			
			try {
				boolean regItemsWSCall = false;
				String regItemsWSCallFlag = BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
						BBBGiftRegistryConstants.REGISTRY_WS_CALL);
				if (regItemsWSCallFlag != null && !regItemsWSCallFlag.isEmpty()) {
					regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag);
				}
			if(regItemsWSCall){
				final RegistrySearchVO registrySearchVO = new RegistrySearchVO();
				if (isMxGiftGiver) //added for Mexico registry
				{
					registrySearchVO.setSiteId("5");
				} else
					if (BBBConfigRepoUtils.getStringValue(
							BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId)
							!= null) {
						registrySearchVO.setSiteId(BBBConfigRepoUtils.getStringValue(
								BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId));
					}
					if (BBBConfigRepoUtils.getStringValue(
							BBBWebServiceConstants.TXT_WSDLKEY,
							BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) != null) {
						registrySearchVO.setUserToken(BBBConfigRepoUtils.getStringValue(
								BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN));
					}
				registrySearchVO.setServiceName(getRegistryItemsListServiceName());

				registrySearchVO.setRegistryId(registryId);

				registrySearchVO.setView(Integer.parseInt(view));
				// registrySearchVO.setSortSeqItemList(sortSeq);
				registrySearchVO.setStartIdx(startIdx);
				registrySearchVO.setBlkSize(blkSize);
				registrySearchVO.setGiftGiver(isGiftGiver);
				registrySearchVO
						.setAvailForWebPurchaseFlag(isAvailForWebPurchaseFlag);
				// calling the GiftRegistryManager's fetchRegistryItems method
				registryItemsListVO = mGiftRegistryManager.fetchRegistryItems(registrySearchVO);
			}else{
				//Start new repo changes
					registryItemsListVO = mGiftRegistryManager
							.fetchRegistryItemsFromEcomAdmin(registryId,
									isGiftGiver, isMyItemsCheckList, view);
				//end new repo changes
			}
			
			/*Setting registry items in the session bean for the redesigned My Items Tab*/
			if(isMyItemsCheckList) {
				BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
				sessionBean.getValues().put(BBBGiftRegistryConstants.REGISTRY_ITEMS,registryItemsListVO);
			}
			
			String registryTypeId = null;
			
			final List<RegistryTypeVO> list = getCatalogTools()
					.getRegistryTypes(siteId);
			final Iterator<RegistryTypeVO> iter = list.iterator();
			registryTypeId = checkRegistryType(eventTypeCode, registryTypeId,
					iter);
			
			final String sortSequence = sortSeq;
			
			boolean isNotifyRegistrant = false;
				String notifyRegFlag = BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
						BBBGiftRegistryConstants.NOTIFY_REGISTRANT_FLAG);
			if (notifyRegFlag != null && !notifyRegFlag.isEmpty()){
				isNotifyRegistrant = Boolean.parseBoolean(notifyRegFlag);
			}
			
			if(!isMyItemsCheckList){
				Map<String,RegistryItemVO>  registryVo =  registryItemsListVO.getSkuRegItemVOMap();	
				List<RegistryItemVO> registryItemsList = registryItemsListVO.getRegistryItemList();
				Set<Entry<String, RegistryItemVO>> s=registryVo.entrySet();
				Iterator i = s.iterator();
				while(i.hasNext()){
					Entry  e=(Entry) i.next(); 
					RegistryItemVO reVo=(RegistryItemVO) e.getValue();
					setLTLAttributesInRegItem(siteId, regEventDate,
							isNotifyRegistrant, registryItemsList, reVo);
					
				}
			}
			final StringBuffer omniProductList =new StringBuffer();
			if (registryItemsListVO != null) {
			
				processItemList(pRequest, pResponse, registryTypeId,registryId,eventTypeCode,
						sortSequence, registryItemsListVO, c2id, c3id, c1id);
					
				if(isGiftGiver) {
				// added for omnature tagging for copy registry
				List<RegistryItemVO>  listRegistryVo =  registryItemsListVO.getRegistryItemList();				
				for(RegistryItemVO reVo : listRegistryVo){							
					double price= 0.0;
					
					Long skuId = reVo.getSku();
					int quatity = reVo.getQtyRequested();
					if(reVo.getsKUDetailVO()!=null){
							 
						RepositoryItem skuRepoItem = reVo.getsKUDetailVO().getSkuRepositoryItem();					
						String parentProductId = null;
						
			                final Set<RepositoryItem> parentProduct = (Set<RepositoryItem>) skuRepoItem
			                                .getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME);
			                if ((parentProduct != null) && !parentProduct.isEmpty()) {
			                    for (final RepositoryItem productRepositoryItem : parentProduct) {			                      
			                            parentProductId = productRepositoryItem.getRepositoryId();
			                            this.logDebug("parent Product Id[" + parentProductId + "]");
			                            break;
			                    }
			                }	
			    			listPrice = getCatalogTools().getListPrice(parentProductId,String.valueOf(reVo.getSku()));
			    			salePrice = getCatalogTools().getSalePrice(parentProductId,String.valueOf(reVo.getSku()));
			    			if ((salePrice > 0)) {
			    				price = salePrice * reVo.getQtyRequested();
							} else {
								price = listPrice * reVo.getQtyRequested();
							}
			    					
			                String omniProdvalue = ";"+ parentProductId + ";;;event22="+ quatity + "|event23=" + price + ";eVar30=" + skuId;
			                omniProductList.append(omniProdvalue);
					}
				}
				enableBuyOffRegistry = this.enableBuyOffStartBrowsing(listRegistryVo);
				pRequest.setParameter(BUYOFF_SHOW_START_BROWSING,enableBuyOffRegistry);				
                pRequest.setParameter("omniProductList",omniProductList.toString());
				}
			}
		}catch (BBBBusinessException bbbbEx) {
			if (null != registryItemsListVO) {
					logError(
							LogMessageFormatter
									.formatMessage(
											pRequest,
											"BBBBusinessException from service of RegistriesItemDisplayDroplet : Error Id is:"
													+ registryItemsListVO
															.getServiceErrorVO()
															.getErrorId(),
											BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1079),
							bbbbEx);
			} else {
				logError("BBBBusinessException in serviceMethod of registry : " + registryId + " Exception is : "+ bbbbEx);
			}
			pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);

		}catch (BBBSystemException bbbsEx) {
			logError("BBBSystemException in serviceMethod of registry : " + registryId + " Exception is : "+bbbsEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
		}
		}
		logDebug(" RegistryItemsDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends");
		BBBPerformanceMonitor.end("RegistryItemsDisplayDroplet", "GetRegistryItemList");
	}

	/**
	 * Sets the ltl attributes in reg item.
	 *
	 * @param siteId the site id
	 * @param regEventDate the reg event date
	 * @param isNotifyRegistrant the is notify registrant
	 * @param registryItemsList the registry items list
	 * @param reVo the re vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public void setLTLAttributesInRegItem(String siteId, String regEventDate,
			boolean isNotifyRegistrant, List<RegistryItemVO> registryItemsList,
			RegistryItemVO reVo) throws BBBSystemException,
			BBBBusinessException {
		
		logDebug("Setting LTL attribute in registry item for sku " + reVo.getSku());
		Long skuId = reVo.getSku();
		String sku="";
		if(skuId>0){
			sku=Long.toString(skuId);
		}				
		if(!BBBUtility.isEmpty(reVo.getPersonalisedCode())){
			reVo.setPersonalizationOptionsDisplay(getEximManager().getPersonalizedOptionsDisplayCode(reVo.getPersonalisedCode()));
		}
		/* changes starts for story : BBBP-4572 : Notify Registrant (Show Message) while adding N & D status items */
		if (isNotifyRegistrant && ! siteId.contains("TBS")){				
			String displayMessageType = mGiftRegistryManager.getNotifyRegistrantMsgType(sku, regEventDate);
			reVo.setDisplayNotifyRegistrantMsg(displayMessageType);
		}	
		/* changes ends for story : BBBP-4572 : Notify Registrant (Show Message) while adding N & D status items */
		
		if(BBBUtility.isNotEmpty(reVo.getRefNum()) && BBBCoreConstants.MINUS_ONE.equalsIgnoreCase(reVo.getRefNum().trim())){
			reVo.setRefNum(BBBCoreConstants.BLANK);
		}
		if(BBBUtility.isNotEmpty(reVo.getLtlDeliveryServices())){
			reVo.setDeliverySurcharge(getCatalogTools().getDeliveryCharge(
					SiteContextManager.getCurrentSiteId(), sku,
					reVo.getLtlDeliveryServices()));
			if((BBBUtility.isNotEmpty(reVo.getAssemblySelected()) && reVo.getAssemblySelected().equalsIgnoreCase(BBBCoreConstants.YES_CHAR)) 
					|| reVo.getLtlDeliveryServices().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
				//set in transient property of gift list
				boolean isShippingMethodExistsForSku = getCatalogTools()
						.isShippingMethodExistsForSku(siteId, sku,
								BBBCoreConstants.LW, true);
				reVo.setShipMethodUnsupported(!isShippingMethodExistsForSku);
				reVo.setAssemblyFees(getCatalogTools().getAssemblyCharge(SiteContextManager.getCurrentSiteId(), sku));
				 RepositoryItem shippingMethod = getCatalogTools().getShippingMethod(reVo.getLtlDeliveryServices().trim());
				 String shippingMethodDesc = ((String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION)).trim();
				 reVo.setLtlShipMethodDesc(shippingMethodDesc+BBBGiftRegistryConstants.WITH_ASSEMBLY);
				 reVo.setLtlDeliveryServices(shippingMethod.getRepositoryId().trim()+BBBCoreConstants.A);
				}
			else{
				boolean isShippingMethodExistsForSku = getCatalogTools()
						.isShippingMethodExistsForSku(siteId, sku,
								reVo.getLtlDeliveryServices(), false);
				reVo.setShipMethodUnsupported(!isShippingMethodExistsForSku);
				 RepositoryItem shippingMethod = getCatalogTools().getShippingMethod(reVo.getLtlDeliveryServices().trim());
				 String shippingMethodDesc = ((String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION)).trim();
				 reVo.setLtlShipMethodDesc(shippingMethodDesc);
				 reVo.setLtlDeliveryServices(reVo.getLtlDeliveryServices().trim());
			}
		}
		
		if(null != reVo.getItemType() && BBBCoreConstants.LTL.equalsIgnoreCase(reVo.getItemType()))
		{
			int noOfSameSku = Collections.frequency(getLTLskuList(registryItemsList), sku);
			if(noOfSameSku == 1 && reVo.getQtyPurchased() == 0)
			{
				if (BBBUtility.isEmpty(reVo.getLtlDeliveryServices())
						|| BBBUtility.isNotEmpty(reVo.getLtlDeliveryServices())
						&& reVo.isShipMethodUnsupported()) {
					reVo.setDSLUpdateable(true);
				}
			}
		}
	}

	/**
	 * Sets the reg items second call.
	 *
	 * @param pRequest the request
	 * @param pResponse the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public void setRegItemsSecondCall(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String categoryId = null;
		String siteId = null;
		MyItemCategoryVO categoryVO;
		String regEventDate = null;
		regEventDate = pRequest.getParameter(EVENT_DATE_PARAM);
		categoryId = pRequest.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID);
		siteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);
		String enableLTLRegForSiteValue;
		String c1id =  pRequest.getParameter(C1_ID);
		logDebug("setRegItemsSecondCall :: Second ajax call to populate rest of the registry items");
		try {
			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
			List<RegistryItemVO> registryItemsAll = (List<RegistryItemVO>) sessionBean
					.getValues().get(
							BBBGiftRegistryConstants.REGISTRY_ITEMS_ALL);
			if(BBBUtility.isEmpty(c1id)){
				boolean enableLTLRegForSite = false;
					enableLTLRegForSiteValue = BBBConfigRepoUtils.getStringValue(
							BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
							BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE);
				if(BBBUtility.isNotEmpty(enableLTLRegForSiteValue)) {
					enableLTLRegForSite = Boolean.parseBoolean(enableLTLRegForSiteValue);	
				}
				
				
				
				/*Getting List of category buckets populated during the first call*/
					List<MyItemCategoryVO> ephCategoryBuckets = (List<MyItemCategoryVO>) sessionBean
							.getValues().get(
									BBBGiftRegistryConstants.EPH_CATEGORY_BUCKETS);
				
				/*For saving the list of category buckets coming after first non empty C1*/
				List<MyItemCategoryVO> copyEphCategoryBuckets = new ArrayList<MyItemCategoryVO>();
				
				sessionBean.getValues().remove(BBBGiftRegistryConstants.EPH_CATEGORY_BUCKETS);
				
				if(ephCategoryBuckets!=null && !ephCategoryBuckets.isEmpty()) {
					
					logDebug("setRegItemsSecondCall : Found EPH C1 categories from session set in the first call");
					ListIterator<MyItemCategoryVO> it = ephCategoryBuckets.listIterator();
					
					RegistryItemsListVO registryItems = (RegistryItemsListVO) sessionBean.getValues().get(BBBGiftRegistryConstants.REGISTRY_ITEMS);
					sessionBean.getValues().remove(BBBGiftRegistryConstants.REGISTRY_ITEMS);
					
					
					sessionBean.getValues().remove(BBBGiftRegistryConstants.REGISTRY_ITEMS_ALL);
					
					List<RegistryItemVO> registryItemsList = registryItems.getRegistryItemList();
					
					if(registryItemsList == null) {
						registryItemsList = new ArrayList<RegistryItemVO>();
					}
					
					boolean matchFound = false;
					
					/*Iterating over EPH C1s to match with the categoryId passed from request*/
					while(it.hasNext()) {
						categoryVO = it.next();
						if(matchFound){
							/*Setting details for the C1 coming after the first non empty C1 populated in the first call*/
							setRegistryItemDetailsInC1(categoryVO, siteId, enableLTLRegForSite, regEventDate, registryItemsList, registryItemsAll);
							copyEphCategoryBuckets.add(categoryVO);
						}
						if(!matchFound && categoryVO.getCategoryId().equals(categoryId)) {
							matchFound = true;
						}
					}
					
					
					
					
					logDebug("Setting the rest of the category buckets in request in second call");
					pRequest.setParameter(BBBGiftRegistryConstants.EPH_CATEGORY_BUCKETS, copyEphCategoryBuckets);
				}
				
			}
			final List<String> skuIds = new ArrayList<String>();
			if(registryItemsAll != null && registryItemsAll.size()>0){
			setCertonaItemList(pRequest,registryItemsAll);
			ListIterator<RegistryItemVO> regItemsItr = registryItemsAll.listIterator();
				
				/*Setting certona sku list for showing Top Registry Items section for certona items*/
				setCertonaSkuList(pRequest, null, skuIds, null, regItemsItr, 0, null);
			}
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
						pResponse);
			
			logDebug("Exiting setRegItemsSecondCall method");
		} catch (BBBSystemException e) {
			logError("BBBSystemException in setRegItemsSecondCall method of RegistryItemsDisplayDroplet ",e);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException in setRegItemsSecondCall method of RegistryItemsDisplayDroplet ",e);
		}
	}
	
	/**
	 * Method to enable showing start browsing button on RLP based on some conditions
	 * @param listRegistryVo
	 * @return boolean
	 * @throws BBBSystemException
	 */
	private boolean enableBuyOffStartBrowsing(List<RegistryItemVO> listRegistryVo) throws BBBSystemException{
		String buyOffstartBrowsingKey=null;
		Boolean isbuyOffstartBrowsingt=false;
		int unfulfilledCount = 0;
		logDebug("RegistryItemsDisplayDroplet:enableBuyOffStartBrowsing - started");
		logDebug("Input RegistryVO list" + listRegistryVo);
			buyOffstartBrowsingKey = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					BBBCmsConstants.BUYOFF_START_BROWSING_KEY);
			if(null!=buyOffstartBrowsingKey && !buyOffstartBrowsingKey.isEmpty()){
				isbuyOffstartBrowsingt=Boolean.valueOf(buyOffstartBrowsingKey);
				logDebug(" Is buyoff start browsing key on ? "+isbuyOffstartBrowsingt);			
			}

			
			if(null != isbuyOffstartBrowsingt && (isbuyOffstartBrowsingt == true)){
				for(RegistryItemVO reVo : listRegistryVo){
					if(reVo.getQtyPurchased()< reVo.getQtyRequested()){
						unfulfilledCount+=1;
	    			}
				}
				
				final String itemSizeLimit = this.getGiftRegistryManager().getGiftRegistryConfigurationByKey(BROWSE_ITEMPURCHASED_LIMIT);
			    final String totQtyPurchasedCountLimit = this.getGiftRegistryManager().getGiftRegistryConfigurationByKey(BROWSE_UNFULLFILLED_SIZE_LIMIT);
				logDebug("Number of sku's ? "+listRegistryVo.size());	
				logDebug("Number of sku's limit ? "+itemSizeLimit);
				logDebug("Number of unfulfilled sku's ? "+ unfulfilledCount);	
				logDebug("Percentage unfulfilled sku's limit ? "+totQtyPurchasedCountLimit);
				logDebug("Percentage of unfulfilled sku's ? "+ (Integer.parseInt(totQtyPurchasedCountLimit)* listRegistryVo.size())*1.0/100.0);
				if(!BBBUtility.isEmpty(itemSizeLimit) && !BBBUtility.isEmpty(totQtyPurchasedCountLimit) && (unfulfilledCount <= Integer.parseInt(itemSizeLimit)) || (unfulfilledCount*1.0 <= ((Integer.parseInt(totQtyPurchasedCountLimit)* listRegistryVo.size())*1.0/100.0))){
					return true;
				}
				else
				{
					return false;
				}				
			}			
		return false;		
	}

	private void processItemList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String registryTypeId,String registryId,String eventTypeCode,
			final String sortSequence , RegistryItemsListVO registryItemsListVO, String c2id,String c3id,String c1id) throws ServletException, IOException,
			BBBBusinessException, BBBSystemException {
		Boolean isMyItemsCheckList = false;
		isMyItemsCheckList = Boolean.valueOf(pRequest.getParameter(IS_MY_ITEMS_CHECKLIST));
		if (registryItemsListVO.getServiceErrorVO().isErrorExists()) {

			if (!BBBUtility.isEmpty(registryItemsListVO
					.getServiceErrorVO().getErrorDisplayMessage())
					&& registryItemsListVO.getServiceErrorVO()
							.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR)// Technical
																										// Error
			{
				logError(LogMessageFormatter.formatMessage(pRequest, "Fatal error from service of RegistriesItemDisplayDroplet : Error Id is:"	+ registryItemsListVO.getServiceErrorVO().getErrorId(), BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1011));
				pRequest.setParameter(OUTPUT_ERROR_MSG,
						ERR_REGINFO_FATAL_ERROR);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
			if (!BBBUtility.isEmpty(registryItemsListVO
					.getServiceErrorVO().getErrorDisplayMessage())
					&& registryItemsListVO.getServiceErrorVO()
							.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN)// Technical
																												// Error
			{
				logError(LogMessageFormatter.formatMessage(pRequest, "Either user token or site flag invalid from service of RegistryItemsDisplayDroplet : Error Id is:"	+ registryItemsListVO.getServiceErrorVO()
						.getErrorId(), BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1002));
				pRequest.setParameter(OUTPUT_ERROR_MSG,
						ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
			if (!BBBUtility.isEmpty(registryItemsListVO
					.getServiceErrorVO().getErrorDisplayMessage())
					&& registryItemsListVO.getServiceErrorVO()
							.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT)// Technical
																												// Error
			{
				
				
				logError(LogMessageFormatter.formatMessage(pRequest, 
						"GiftRegistry input fields format error from processItemList() of " +
						"RegistryItemsDisplayDroplet | webservice error code=" + registryItemsListVO.getServiceErrorVO().getErrorId(), 
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));				
				
				pRequest.setParameter(OUTPUT_ERROR_MSG,
						ERR_REGINFO_INVALID_INPUT_FORMAT);
				
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
			
		}
		
		PromoBoxVO promoBoxVO = getCatalogTools().getPromoBoxForRegistry(registryTypeId);
		
		pRequest.setParameter(
				BBBGiftRegistryConstants.PROMOBOX,
				promoBoxVO);
		
		 List<RegistryItemVO> listRegistryItemVO = registryItemsListVO
				.getRegistryItemList();
		 
		if(!isMyItemsCheckList){
			listRegistryItemVO=fliterNotAvliableItem(listRegistryItemVO);
		}
		final Map<String, RegistryItemVO> mSkuRegItemVOMap = registryItemsListVO
				.getSkuRegItemVOMap();

		StringBuilder skuTempList = new StringBuilder("");

		final List<String> skuIds = new ArrayList<String>();
		 Map<String, Double> skuIdPriceMap = new HashMap<String, Double>();
		if (!BBBUtility.isListEmpty(listRegistryItemVO)	&& !StringUtils.isEmpty(registryTypeId)) {
			
			pRequest.setParameter(
					BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT,
					registryItemsListVO.getTotEntries());
			
			getlistRegistryItemVO(pRequest, pResponse, registryTypeId,
					sortSequence, listRegistryItemVO, mSkuRegItemVOMap,
					skuTempList, skuIds, skuIdPriceMap, c2id, c3id, c1id);

		} else {
				if(!getCheckListManager().showC1CategoryOnRlp(c1id, registryId, eventTypeCode).equalsIgnoreCase(BBBCoreConstants.SHOW)){
					pRequest.setParameter("other", true);
				}
				calculateQty(pRequest, c2id, c3id, c1id);
			pRequest.setParameter(
					BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 0);
			checkWithNull(pRequest, pResponse, registryTypeId,
					sortSequence);
			// ILD-449 | changes to set ephCategoryBuckets only if there are no items in registry and call is coming from checklist.
				String fromChecklist = pRequest.getParameter(FROM_CHECKLIST);
				if(!BBBUtility.isEmpty(fromChecklist) && BBBCoreConstants.TRUE.equalsIgnoreCase(fromChecklist)){
				/*Getting the checklist VO for the given registry type and id.*/
				MyItemVO myItemVO = getCheckListManager().getMyItemVO(registryId, eventTypeCode);
				
				if(myItemVO !=null){
				/*Getting the C1 EPH Categories for the given registry type.*/
				List<MyItemCategoryVO> c1Categories = myItemVO.getCategoryListVO();
				if(!BBBUtility.isListEmpty(c1Categories)) {
					
					List<MyItemCategoryVO> c1CategoriesOnRLP = new ArrayList<MyItemCategoryVO>();
					Boolean isGiftGiver = Boolean.valueOf(pRequest.getParameter(IS_GIFT_GIVER));
					/*Iterating over the C1 categories to get those C1s which are having show flag true for RLV.*/
					if(!isGiftGiver && BBBUtility.isEmpty(c1id)){
					for(MyItemCategoryVO c1Cat : c1Categories) {
						if(getCheckListManager().showC1CategoryOnRlp(c1Cat.getCategoryId(), registryId, eventTypeCode).equalsIgnoreCase(BBBCoreConstants.SHOW)){
							c1CategoriesOnRLP.add(c1Cat);
						}
					}
					}
				
			
				if(StringUtils.isEmpty(c3id) && BBBUtility.isEmpty(c2id)){
					
					/*Setting the list of buckets in the request for populating the page in the first call*/
					pRequest.setParameter(BBBGiftRegistryConstants.EPH_CATEGORY_BUCKETS, c1CategoriesOnRLP);
				
				}
				}
				}
				}
				
			
			
		}
		
	}

	private void calculateQty(final DynamoHttpServletRequest pRequest,
 String c2id, String c3id, String c1id) {
		String size = null;
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
		CheckListVO checklistvo = sessionBean.getChecklistVO();
		if (checklistvo != null) {
			if (checklistvo.getCategoryListVO() != null) {
				Iterator<CategoryVO> it = checklistvo.getCategoryListVO().iterator();
				while (it.hasNext()) {
					CategoryVO cat1VO = (CategoryVO) it.next();
					if (cat1VO.getCategoryId().equalsIgnoreCase(c1id) && cat1VO.getChildCategoryVO() != null) {
						ListIterator<CategoryVO> c2Iter = cat1VO.getChildCategoryVO().listIterator();
						while (c2Iter.hasNext()) {
							CategoryVO cat2VO = (CategoryVO) c2Iter.next();
							if (cat2VO.getCategoryId().equalsIgnoreCase(c2id) && BBBUtility.isEmpty(c3id)) {
								size = cat2VO.getAddedQuantity() + " of " + cat2VO.getSuggestedQuantity();
								pRequest.setParameter(BBBGiftRegistryConstants.ADDED_COUNT, cat2VO.getAddedQuantity());
								break;
							} else {
								ListIterator<CategoryVO> c3Iter = cat2VO.getChildCategoryVO().listIterator();
								while (c3Iter.hasNext()) {
									CategoryVO cat3VO = (CategoryVO) c3Iter.next();
									if (cat3VO.getCategoryId().equalsIgnoreCase(c3id)) {
										size = cat3VO.getAddedQuantity() + " of " + cat3VO.getSuggestedQuantity();
										pRequest.setParameter(BBBGiftRegistryConstants.ADDED_COUNT,
												cat3VO.getAddedQuantity());
										break;
									}
								}
							}
						}
					}
				}
			}

		}
		pRequest.setParameter(BBBGiftRegistryConstants.QTY_OF, size);
	}
	/**
	 * 
	 * @param reVo
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private void updateRegforLTL(RegistryItemVO reVo) throws BBBBusinessException, BBBSystemException{
		String sku=BBBCoreConstants.BLANK+reVo.getSku();
		String siteId=SiteContextManager.getCurrentSiteId();
		if(BBBUtility.isNotEmpty(reVo.getRefNum()) && BBBCoreConstants.MINUS_ONE.equalsIgnoreCase(reVo.getRefNum().trim())){
			reVo.setRefNum(BBBCoreConstants.BLANK);
		}
		reVo.setDeliverySurcharge(getCatalogTools().getDeliveryCharge(SiteContextManager.getCurrentSiteId(),sku, reVo.getLtlDeliveryServices()));
		if((BBBUtility.isNotEmpty(reVo.getAssemblySelected()) && reVo.getAssemblySelected().equalsIgnoreCase(BBBCoreConstants.YES_CHAR)) || reVo.getLtlDeliveryServices().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
			//set in transient property of gift list
			boolean isShippingMethodExistsForSku = getCatalogTools().isShippingMethodExistsForSku(siteId, BBBCoreConstants.BLANK+reVo.getSku(),BBBCoreConstants.LW , true);
			reVo.setShipMethodUnsupported(!isShippingMethodExistsForSku);
			reVo.setAssemblyFees(getCatalogTools().getAssemblyCharge(SiteContextManager.getCurrentSiteId(), sku));
			RepositoryItem shippingMethod = getCatalogTools().getShippingMethod(reVo.getLtlDeliveryServices().trim());
			String shippingMethodDesc = ((String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION)).trim();
			 reVo.setLtlShipMethodDesc(shippingMethodDesc+BBBGiftRegistryConstants.WITH_ASSEMBLY);
			reVo.setLtlDeliveryServices(shippingMethod.getRepositoryId().trim()+BBBCoreConstants.A);
			}
		else{
			boolean isShippingMethodExistsForSku = getCatalogTools().isShippingMethodExistsForSku(siteId, sku, reVo.getLtlDeliveryServices(), false);
			reVo.setShipMethodUnsupported(!isShippingMethodExistsForSku);
			RepositoryItem shippingMethod = getCatalogTools().getShippingMethod(reVo.getLtlDeliveryServices().trim());
			String shippingMethodDesc = ((String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION)).trim();
			reVo.setLtlShipMethodDesc(shippingMethodDesc);
		}
	}

	private void checkWithNull(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String registryTypeId,
			final String sortSequence) throws BBBSystemException,
			BBBBusinessException, ServletException, IOException {
		if (sortSequence.contentEquals(BBBGiftRegistryConstants.PRICE_SORT_SEQ))
		{
			List<String> priceRangeList = getCatalogTools()
					.getPriceRanges(registryTypeId,null);
//			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
			BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(pRequest);
			String country = (String) sessionBean
					.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
			if(!BBBUtility.isEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY) && priceRangeList!=null){
				 List<String> changedPriceRangeList=null;
				 changedPriceRangeList=new ArrayList<String>();
				 for(String item:priceRangeList)
	             {
					 String newKey=getNewKeyRange(item);
					 changedPriceRangeList.add(newKey);
	             }
				 priceRangeList=changedPriceRangeList;
			}else if(!BBBUtility.isEmpty(country) && country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY)){
				priceRangeList=getCatalogTools().getPriceRanges(registryTypeId,country);
			}
			pRequest.setParameter(
					BBBGiftRegistryConstants.PRICE_RANGE_LIST,
					priceRangeList);
			Map<String, String> map = new HashMap<String, String>();
			if(priceRangeList != null){
				for (String priString : priceRangeList) {
					map.put(priString, null) ;
				}
			}
			pRequest.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,map);
		}
		
		else
		{
			Map<String, RegistryCategoryMapVO> getCategoryForRegistry = getCatalogTools()
					.getCategoryForRegistry(registryTypeId);
			//Map<String, String> map = new HashMap<String, String>();
			Map<String, List<RegistryItemVO>> categoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
			for (String catBucket : getCategoryForRegistry.keySet()) {
				//map.put(catBucket, null) ;
				categoryBuckets.put(catBucket, categoryBuckets.get(catBucket));
			}
			pRequest.setParameter(
					BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO,
					getCategoryForRegistry);
			pRequest.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,categoryBuckets);
		}
		
		pRequest.setParameter(BBBGiftRegistryConstants.EMPTY_LIST,"true");
		pRequest.setParameter(BBBGiftRegistryConstants.SORT_SEQUENCE,sortSequence);
		pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
				pResponse);
	}
	
	private void getlistRegistryItemVO(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String registryTypeId,
			final String sortSequence, List<RegistryItemVO> listRegistryItemVO,
			final Map<String, RegistryItemVO> mSkuRegItemVOMap,
			StringBuilder skuTempList, final List<String> skuIds,
			 Map<String, Double> skuIdPriceMap,String c2id,String c3id,String c1id)
			throws BBBBusinessException, BBBSystemException, ServletException,
			IOException {
		
		Iterator<RegistryItemVO> it = listRegistryItemVO.iterator();
		int certonaTopRegCount = 0;
		StringBuilder tempCertonaList = new StringBuilder("");
		
		String certonaTopRegMax = BBBConfigRepoUtils.getStringValue(BBBCertonaConstants.FEED_CONFIG_TYPE, getTopRegMaxCount());
		if(!BBBUtility.isEmpty(certonaTopRegMax)){
			setCertonaListMaxCount(Integer.parseInt(certonaTopRegMax));
		}
		String myItemsCheckList = pRequest.getParameter(IS_MY_ITEMS_CHECKLIST);
		Boolean isMyItemsCheckList = Boolean.parseBoolean(myItemsCheckList);
		
		if(!isMyItemsCheckList) {
			setCertonaSkuList(pRequest, skuTempList, skuIds, skuIdPriceMap, it, certonaTopRegCount, tempCertonaList);
		}
		Map<String, List<RegistryItemVO>> categoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> inStockCategoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> notInStockCategoryBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> categoryTempBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> inStockCategoryTempBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		Map<String, List<RegistryItemVO>> notInStockCategoryTempBuckets = new LinkedHashMap<String, List<RegistryItemVO>>();
		
		
		if (sortSequence
				.contentEquals(BBBGiftRegistryConstants.DEFAULT_CAT_SORT_SEQ)) {
				/*For EPH Category Grouping for Redesigned My Items Tab, if My_Items_CheckList_Flag is true.*/
				if(isMyItemsCheckList) {
					setMyItemsForEPHNodes(pRequest, listRegistryItemVO, c2id, c3id, c1id , mSkuRegItemVOMap);
				} else {
					Map<String, RegistryCategoryMapVO> getCategoryForRegistry = getCatalogTools().getCategoryForRegistry(registryTypeId);
					withDefaultCategory(pRequest, registryTypeId, mSkuRegItemVOMap, skuIds,
							categoryBuckets, inStockCategoryBuckets,
							notInStockCategoryBuckets, getCategoryForRegistry);
					
						// ordering catering buckets 
							for (String catBucket : getCategoryForRegistry.keySet()) {
								categoryTempBuckets.put(catBucket, categoryBuckets.get(catBucket));
							}
							categoryBuckets = categoryTempBuckets;
							
							for (String catBucket : getCategoryForRegistry.keySet()) {
								inStockCategoryTempBuckets.put(catBucket, inStockCategoryBuckets.get(catBucket));
							}
							inStockCategoryBuckets = inStockCategoryTempBuckets;

							for (String catBucket : getCategoryForRegistry.keySet()) {
								notInStockCategoryTempBuckets.put(catBucket, notInStockCategoryBuckets.get(catBucket));
							}
							notInStockCategoryBuckets = notInStockCategoryTempBuckets;
							
							
							
				}
		
				} else if (sortSequence
				.contentEquals(BBBGiftRegistryConstants.PRICE_SORT_SEQ)) {
				try {
					withDefaultPrice(pRequest, registryTypeId, mSkuRegItemVOMap,
							skuIdPriceMap, categoryBuckets, inStockCategoryBuckets,
							notInStockCategoryBuckets);
				} catch (RepositoryException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "RepositoryException from getlistRegistryItemVO() of RegistryItemsDisplayDroplet",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1080),e);

				}
				for(Entry<String, List<RegistryItemVO>> entry:categoryBuckets.entrySet()){
					List<RegistryItemVO> listreVo=(List<RegistryItemVO>) entry.getValue();
					if(listreVo!=null && listreVo.size()>=1){
						for(RegistryItemVO reVo:listreVo){
							if(BBBUtility.isNotEmpty(reVo.getLtlDeliveryServices())){
								updateRegforLTL(reVo);
							}
						}
					}
				}
				for(Entry<String, List<RegistryItemVO>> entry:inStockCategoryBuckets.entrySet()){
					List<RegistryItemVO> listreVo=(List<RegistryItemVO>) entry.getValue();
						if(listreVo!=null && listreVo.size()>=1){
							for(RegistryItemVO reVo:listreVo){
								if(BBBUtility.isNotEmpty(reVo.getLtlDeliveryServices())){
									updateRegforLTL(reVo);
								}
							}
						}
				}
				
				for(Entry<String, List<RegistryItemVO>> entry:notInStockCategoryBuckets.entrySet()){
					List<RegistryItemVO> listreVo=(List<RegistryItemVO>) entry.getValue();
					if(listreVo!=null && listreVo.size()>=1){
						for(RegistryItemVO reVo:listreVo){
							if(BBBUtility.isNotEmpty(reVo.getLtlDeliveryServices())){
								updateRegforLTL(reVo);
							}
					}
					}
				}
		} else {
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		}
		pRequest.setParameter(
				BBBGiftRegistryConstants.CATEGORY_BUCKETS,
				categoryBuckets);
		pRequest.setParameter(
				BBBGiftRegistryConstants.INSTOCK_CATEGORY_BUCKETS,
				inStockCategoryBuckets);
		pRequest.setParameter(
				BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_BUCKETS,
				notInStockCategoryBuckets);
		pRequest.setParameter(BBBGiftRegistryConstants.COUNT,
				listRegistryItemVO.size());
		pRequest.setParameter(
				BBBGiftRegistryConstants.SORT_SEQUENCE,
				sortSequence);
		pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
				pResponse);
	}

	/**
	 * Arranges the My Items according to
	 * the EPH nodes, when My_Items_Checklist_Flag is on. 
	 *
	 * @param pRequest the request
	 * @param listRegistryItemVO the list registry item vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	@SuppressWarnings("unchecked")
	public void setMyItemsForEPHNodes(final DynamoHttpServletRequest pRequest,
			List<RegistryItemVO> listRegistryItemVO,String c2id,String c3id,String c1id,Map<String, RegistryItemVO> mSkuRegItemVOMap) throws BBBSystemException,
			BBBBusinessException {
		logDebug("Entering setMyItemsForEPHNodes method");
		
		String registryType = pRequest.getParameter(EVENT_TYPE);
		String registryId = pRequest.getParameter(REGISTRY_ID);
		String siteId = pRequest.getParameter(SITE_ID_PARAM);
		Boolean isGiftGiver = Boolean.valueOf(pRequest.getParameter(IS_GIFT_GIVER));
		String regEventDate = pRequest.getParameter(EVENT_DATE_PARAM);
		String enableLTLRegForSiteValue = BBBConfigRepoUtils.getStringValue
				(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
						BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE);
		boolean enableLTLRegForSite = false;
		if(BBBUtility.isNotEmpty(enableLTLRegForSiteValue)) {
			enableLTLRegForSite = Boolean.parseBoolean(enableLTLRegForSiteValue);	
		}
		
		
		/*Getting the checklist VO for the given registry type and id.*/
		MyItemVO myItemVO = getCheckListManager().getMyItemVO(registryId, registryType);
		
		if(myItemVO !=null){
		/*Getting the C1 EPH Categories for the given registry type.*/
		List<MyItemCategoryVO> c1Categories = myItemVO.getCategoryListVO();
		
		if(!BBBUtility.isListEmpty(c1Categories)) {
			
			List<MyItemCategoryVO> c1CategoriesOnRLP = new ArrayList<MyItemCategoryVO>();
			
			/*Iterating over the C1 categories to get those C1s which are having show flag true for RLV.*/
			if(!isGiftGiver && BBBUtility.isEmpty(c1id)){
			for(MyItemCategoryVO c1Cat : c1Categories) {
				if(getCheckListManager().showC1CategoryOnRlp(c1Cat.getCategoryId(), registryId, registryType).equalsIgnoreCase(BBBCoreConstants.SHOW)){
					c1CategoriesOnRLP.add(c1Cat);
				}
			}
			}
			List<RegistryItemVO> regItems = new ArrayList<RegistryItemVO>();
			regItems.addAll(listRegistryItemVO);
			
			if(isGiftGiver) {
				MyItemVO myItemVOInStock = getCheckListManager().getMyItemVO(registryId, registryType);
				MyItemVO myItemVONotInStock = getCheckListManager().getMyItemVO(registryId, registryType);
				for(MyItemCategoryVO c1Cat : c1Categories) {
					if(!getCheckListManager().showC1CategoryOnRlp(c1Cat.getCategoryId(), registryId, registryType).equalsIgnoreCase(BBBCoreConstants.SHOW)){
						myItemVOInStock.getCategoryListVO().remove(c1Cat);
						myItemVONotInStock.getCategoryListVO().remove(c1Cat);
					}
				}
				List<RegistryItemVO> inStockRegItems = new ArrayList<RegistryItemVO>();
				List<RegistryItemVO> notInStockRegItems = new ArrayList<RegistryItemVO>();
				List<MyItemCategoryVO> inStockEPHBuckets = new ArrayList<MyItemCategoryVO>();
				List<MyItemCategoryVO> notInStockEPHBuckets = new ArrayList<MyItemCategoryVO>();
				
				inStockEPHBuckets.addAll(myItemVOInStock.getCategoryListVO());
				notInStockEPHBuckets.addAll(myItemVONotInStock.getCategoryListVO());
				
				populateRegItemAttributes(siteId, enableLTLRegForSite, regEventDate, regItems, regItems);
				filterBelowLineItems(regItems, inStockRegItems, notInStockRegItems);
				
				getCheckListManager().getCheckListTools().setGiftGiverEPHBuckets(inStockRegItems, inStockEPHBuckets);
				getCheckListManager().getCheckListTools().setGiftGiverEPHBuckets(notInStockRegItems, notInStockEPHBuckets);
				
				pRequest.setParameter(BBBGiftRegistryConstants.IN_STOCK_EPH_BUCKETS, inStockEPHBuckets);
				pRequest.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_EPH_BUCKETS, notInStockEPHBuckets);
			} else {
			
			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
			
			RegistryItemsListVO registryItemsListVO = (RegistryItemsListVO) sessionBean.getValues().get(BBBGiftRegistryConstants.REGISTRY_ITEMS);
			
			List<RegistryItemVO> registryItemsList = null;
			if (registryItemsListVO != null) {
				registryItemsList = registryItemsListVO
						.getRegistryItemList();
			}
			//Added for C3 filter from checklist
			if (BBBUtility.isNotEmpty(c1id)) {
				List<RegistryItemVO> regItemsList = null;
				logDebug("C3 filter setMyItemsForEPHNodes method c3 id is "+c3id);
				if (registryItemsList != null) {
					Iterator<RegistryItemVO> registryItemsIterator = registryItemsList
							.iterator();
					//Get the list of SKU  nad the categories applicable for that sku
					Map<String, List<String>> regItemvolist = this
							.getCheckListManager().getCheckListTools()
							.getSkuCategoriesListMap(registryItemsList);
					regItemsList = new ArrayList<RegistryItemVO>();
					//Iterate over each sku to know if it belongs to the category selected
					while (registryItemsIterator.hasNext()) {
						RegistryItemVO registryItemVo = registryItemsIterator
								.next();
						String skuId = String.valueOf(registryItemVo
								.getSku());
						//If items are associated with C3 
						if(BBBUtility.isNotEmpty(c3id)){
							if (this.getCheckListManager()
									.getCheckListTools()
									.isSkuInCategory(regItemvolist, skuId, c3id)) {
								regItemsList.add(registryItemVo);
							}
						}
						//If items are associated with C2
						else{
							if (this.getCheckListManager()
									.getCheckListTools()
									.isSkuInCategory(regItemvolist, skuId, c2id)) {
								regItemsList.add(registryItemVo);
							}
						}
					}
					//If C1 is disabled on RLP
					if(regItemsList.size() > 0 && !getCheckListManager().showC1CategoryOnRlp(c1id, registryId, registryType).equalsIgnoreCase(BBBCoreConstants.SHOW)){
						pRequest.setParameter(BBBGiftRegistryConstants.OTHER_CATEGORY, true);
					}
				}
				calculateQty(pRequest, c2id, c3id, c1id);
					populateRegItemAttributes(siteId, enableLTLRegForSite,
							regEventDate, regItemsList, regItems);
					pRequest.setParameter(
							BBBGiftRegistryConstants.REGISTRY_ITEMS_ALL,
							regItemsList);
					sessionBean.getValues().put(BBBGiftRegistryConstants.REGISTRY_ITEMS_ALL, regItemsList);
			}
			if(StringUtils.isEmpty(c3id) && BBBUtility.isEmpty(c2id)){
				/*For saving the processed registry items to be used in certona call in second ajax call*/
				List<RegistryItemVO> registryItemsAll = new ArrayList<RegistryItemVO>();
				getCheckListManager().getCheckListTools().setEPHCategoryBuckets(regItems, c1CategoriesOnRLP);
				/*Setting the registry items in the first non empty C1 in the first ajax call*/
				setFirstEPHBucket(pRequest, siteId, enableLTLRegForSite,
						c1CategoriesOnRLP, registryItemsList, registryItemsAll);
				/*Setting the list of buckets in session to be used in second call*/
				sessionBean.getValues().put(BBBGiftRegistryConstants.EPH_CATEGORY_BUCKETS, c1CategoriesOnRLP);
				
				/*Setting the list of buckets in the request for populating the page in the first call*/
				pRequest.setParameter(BBBGiftRegistryConstants.EPH_CATEGORY_BUCKETS, c1CategoriesOnRLP);
				/*Setting the processed registry items to be used in certona call in second ajax call*/
				sessionBean.getValues().put(BBBGiftRegistryConstants.REGISTRY_ITEMS_ALL, registryItemsAll);
			}
			
			
			
			}
			
		}
		}
		logDebug("Exiting setMyItemsForEPHNodes method");
	}

	/**
	 * Filter below line items.
	 *
	 * @param regItems the reg items
	 * @param inStockRegItems the in stock reg items
	 * @param notInStockRegItems the not in stock reg items
	 */
	private void filterBelowLineItems(List<RegistryItemVO> regItems,
			List<RegistryItemVO> inStockRegItems,
			List<RegistryItemVO> notInStockRegItems) {
		
		logDebug("filterBelowLineItems : filtering registry items into stock or in stock list based on below line attribute.");
		for(RegistryItemVO regItem : regItems) {
			if(Boolean.valueOf(regItem.getIsBelowLineItem())) {
				notInStockRegItems.add(regItem);
			} else {
				inStockRegItems.add(regItem);
			}
		}
		logDebug("Exiting filterBelowLineItems method");
	}

	/**
	 * Sets the first eph bucket.
	 *
	 * @param pRequest the request
	 * @param siteId the site id
	 * @param enableLTLRegForSite the enable ltl reg for site
	 * @param c1CategoriesOnRLP the c1 categories on rlp
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public void setFirstEPHBucket(final DynamoHttpServletRequest pRequest, String siteId,
			boolean enableLTLRegForSite, List<MyItemCategoryVO> c1CategoriesOnRLP, List<RegistryItemVO> registryItemsList, List<RegistryItemVO> registryItemsAll)
			throws BBBSystemException, BBBBusinessException {
		
		logDebug("Entering setFirstEPHBucket method");
		String regEventDate = pRequest.getParameter(EVENT_DATE_PARAM);
		
		for(MyItemCategoryVO categoryVO : c1CategoriesOnRLP) {
			/*Checking for the first occurring non empty C1 category*/
			if(categoryVO.getRegistryItemsCount()>0) {
				
				logDebug("Getting registry items for first non empty C1 - " + categoryVO.getCategoryId());
				/*Populating registry items*/
				setRegistryItemDetailsInC1(categoryVO, siteId, enableLTLRegForSite, regEventDate, registryItemsList, registryItemsAll);
				pRequest.setParameter(BBBGiftRegistryConstants.EXPANDED_CATEGORY, categoryVO.getCategoryId());
				break;
			}
		}
		logDebug("Exiting setFirstEPHBucket method");
	}
	
	/**
	 * Sets the registry item details for the items
	 * falling under given c1.
	 *
	 * @param categoryVO the category vo
	 * @param siteId the site id
	 * @param enableLTLRegForSite the enable ltl reg for site
	 * @param regItems the reg items
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public void setRegistryItemDetailsInC1(MyItemCategoryVO categoryVO,
			String siteId, boolean enableLTLRegForSite, String regEventDate,
			List<RegistryItemVO> registryItemsList,
			List<RegistryItemVO> registryItemsAll) throws BBBSystemException,
			BBBBusinessException {	
		List<MyItemCategoryVO> c2CategoriesList;
		List<MyItemCategoryVO> c3CategoriesList;
		MyItemCategoryVO c2CategoryVO;
		MyItemCategoryVO c3CategoryVO;
		List<RegistryItemVO> c1RegItems;
		List<RegistryItemVO> c2RegItems;
		List<RegistryItemVO> c3RegItems;
		Integer c1registryItemsCount;
		Integer c2RegistryItemsCount;
		c2CategoriesList = categoryVO.getChildCategoryVO();
		
		logDebug("Setting registry items for C1 category : " + categoryVO.getCategoryId());
		
		/*Getting items directly linked to C1 in case of 'Other' Category*/
		if(categoryVO.getCategoryId().equalsIgnoreCase(BBBGiftRegistryConstants.OTHER_CATEGORY)) {
			c1RegItems = categoryVO.getRegistryItems();
			populateRegItemAttributes(siteId, enableLTLRegForSite, regEventDate, c1RegItems, registryItemsList);
			
			/*Adding items in registryItemsAll to be used in upcoming certona Sku List Call*/
			if(registryItemsAll!=null){
				registryItemsAll.addAll(c1RegItems);
			}
			categoryVO.setRegistryItemsCount(c1RegItems.size());
		}
		
		/*Iterating over the C2 child categories*/
		if(c2CategoriesList!=null && !c2CategoriesList.isEmpty()){
			c1registryItemsCount = 0;
			ListIterator<MyItemCategoryVO> c2CategoryIter = c2CategoriesList.listIterator();
			while(c2CategoryIter.hasNext()){
				c2RegistryItemsCount = 0;
				c2CategoryVO = c2CategoryIter.next();
				c2RegItems = c2CategoryVO.getRegistryItems();
				
				/*Populating LTL, Price, Below line and SKUDetailVO in regItem*/
				populateRegItemAttributes(siteId, enableLTLRegForSite, regEventDate,
						c2RegItems, registryItemsList);
				
				/*Adding items in registryItemsAll to be used in upcoming certona Sku List Call*/
				if(registryItemsAll!=null && !c2CategoryVO.getCategoryId().equalsIgnoreCase(BBBGiftRegistryConstants.ALL_ITEMS_C2)){
					registryItemsAll.addAll(c2RegItems);
				}
				c2RegistryItemsCount += c2RegItems.size();
				c3CategoriesList = c2CategoryVO.getChildCategoryVO();
				
				/*Iterating over the C3 child categories*/
				if(c3CategoriesList!=null && !c3CategoriesList.isEmpty()) {
					ListIterator<MyItemCategoryVO> c3CategoriesListIter = c3CategoriesList.listIterator();
					while(c3CategoriesListIter.hasNext()) {
						c3CategoryVO = c3CategoriesListIter.next();
						c3RegItems = c3CategoryVO.getRegistryItems();
						
						/*Populating LTL, Price, Below line and SKUDetailVO in regItem*/
						populateRegItemAttributes(siteId, enableLTLRegForSite, regEventDate,
								c3RegItems, registryItemsList);
						
						/*Adding items in registryItemsAll to be used in upcoming certona Sku List Call*/
						if(registryItemsAll!=null){
							registryItemsAll.addAll(c3RegItems);
						}
						c2RegistryItemsCount += c3RegItems.size();
					}
				}
				c2CategoryVO.setRegistryItemsCount(c2RegistryItemsCount);
				if(!c2CategoryVO.getCategoryId().equalsIgnoreCase(BBBGiftRegistryConstants.ALL_ITEMS_C2)) {
					c1registryItemsCount += c2RegistryItemsCount;
				}
			}
			categoryVO.setRegistryItemsCount(c1registryItemsCount);
		}
		logDebug("Exiting setRegistryItemDetailsInC1 method");
	
	}

	
	/**
	 * Populate reg item attributes.
	 *
	 * @param siteId the site id
	 * @param enableLTLRegForSite the enable ltl reg for site
	 * @param regItems the reg items
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public void populateRegItemAttributes(String siteId,
			boolean enableLTLRegForSite, String regEventDate, List<RegistryItemVO> regItems, List<RegistryItemVO> registryItemsList)
			throws BBBSystemException, BBBBusinessException {
		
		SKUDetailVO skuDetailVO;
		String skuId;
		
		logDebug("Populating Registry Item Attributes for Items : " + regItems);
		
		/*Filtering items for which the product is not available for the given SKU*/
		regItems = fliterNotAvliableItem(regItems);
		
		/*Getting config key for Notify Registrant Flag to be used in setting up LTL attributes*/
		boolean isNotifyRegistrant = false;
		String notifyRegFlag = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.NOTIFY_REGISTRANT_FLAG);
		if (notifyRegFlag != null && !notifyRegFlag.isEmpty()){
			isNotifyRegistrant = Boolean.parseBoolean(notifyRegFlag);
		}
		
		/*Iterating over the registry items for populating LTL, price, below line and SkuDetailVO attributes*/
		if(regItems != null){
			for(RegistryItemVO regItem : regItems) {
				
				/*Getting LTL attributes*/
				setLTLAttributesInRegItem(siteId, regEventDate, isNotifyRegistrant, registryItemsList, regItem);
				/*Getting Price*/
				getGiftRegistryManager().getGiftRegistryTools().setPriceInRegItem(regItem);
				/*Populating SKUDetailVO*/
				if(!BBBUtility.isEmpty(String.valueOf(regItem.getSku()))){
					skuId = String.valueOf(regItem.getSku());
					skuDetailVO = getGiftRegistryManager().getGiftRegistryTools().getSKUDetailsWithProductId(siteId, skuId, regItem);
					regItem.setsKUDetailVO(skuDetailVO);
				}
				
				/*Setting below line item attribute*/
				setBelowLineAttribute(regItem, enableLTLRegForSite, siteId);
			}
		}
		logDebug("Exiting populateRegItemAttributes method");
	}
	
	/**
	 * Sets the below line attribute.
	 *
	 * @param regItem the reg item
	 * @param enableLTLRegForSite the enable ltl reg for site
	 * @param siteId the site id
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public void setBelowLineAttribute(RegistryItemVO regItem,
			final boolean enableLTLRegForSite, final String siteId)
			throws BBBSystemException, BBBBusinessException {
		
		logDebug("Setting up below line attirbute for SKU " + regItem.getSku());
		SKUDetailVO skuDetailVO = regItem.getsKUDetailVO();
		Boolean isBelowLine = this.getCatalogTools()
				.isSKUBelowLine(siteId, skuDetailVO.getSkuId());
		regItem.setIsBelowLineItem(String.valueOf(isBelowLine));
		logDebug("Exiting setBelowLineAttribute method");
	}
	

	private void withDefaultPrice(final DynamoHttpServletRequest pRequest,
			String registryTypeId,
			 Map<String, RegistryItemVO> mSkuRegItemVOMap,
			final Map<String, Double> skuIdPriceMap,
			Map<String, List<RegistryItemVO>> categoryBuckets,
			Map<String, List<RegistryItemVO>> inStockCategoryBuckets,
			Map<String, List<RegistryItemVO>> notInStockCategoryBuckets)
			throws BBBSystemException, BBBBusinessException, RepositoryException {
		List<SKUDetailVO> pSVos;
		List<String> pSkuIds;
		RegistryItemVO reg;
		List<RegistryItemVO> pList;
		List<RegistryItemVO> pInStockRegVOList;
		List<RegistryItemVO> pNotInStockRegVOList;
		Map<String, List<SKUDetailVO>> map;
		
		//BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(pRequest);
		String country = (String) sessionBean
				.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
		// Get the list of Price Ranges from Catalog.
		List<String> priceRangeList = getCatalogTools()
				.getPriceRanges(registryTypeId,country);
		if(!BBBUtility.isEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY) && priceRangeList!=null){
			 List<String> changedPriceRangeList=null;
			 changedPriceRangeList=new ArrayList<String>();
			 for(String item:priceRangeList)
             {
				 String newKey=getNewKeyRange(item);
				 changedPriceRangeList.add(newKey);
             }
			 priceRangeList=changedPriceRangeList;
		}
		pRequest.setParameter(
				BBBGiftRegistryConstants.PRICE_RANGE_LIST,
				priceRangeList);

		final String siteId = pRequest.getParameter(SITE_ID_PARAM);
		final Profile profile = (Profile) pRequest.getObjectParameter(PROFILE);
		
		// Catalog API call to get
		// Map<PriceRange,List<SKUDetailVO>
		map = getCatalogTools().sortSkubyRegistry(null,
				skuIdPriceMap, registryTypeId, siteId, null,country);
		// System.out.println("Result Map: " + map);
		// Iterate through the keyset of map to look for each
		// category name
		
		// BPSI-1049 Start: enableLTLRegForSite false means LTL item will be below the line and will not be 
		// available for Add to Cart while true means it can be added to cart
		String enableLTLRegForSiteValue = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
						BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE);
		boolean enableLTLRegForSite = false;
		if(BBBUtility.isNotEmpty(enableLTLRegForSiteValue)) {
			enableLTLRegForSite = Boolean.parseBoolean(enableLTLRegForSiteValue);
		}
		
		for (String key : map.keySet()) {
			pSVos = map.get(key);
			String range = key;
			if( BBBInternationalShippingConstants.MEXICO_COUNTRY.equals(country) && ! StringUtils.isEmpty(range) 
				&& range.contains(BBBInternationalShippingConstants.CURRENCY_MEXICO))
			 {
				String tmpRange=range.replace(BBBInternationalShippingConstants.CURRENCY_MEXICO, DOLLAR);
				tmpRange=tmpRange.replace(BBBCoreConstants.COMMA,BBBCoreConstants.BLANK);
				range=tmpRange;
			}
			int rangeFirstDollarIndex = range.indexOf(DOLLAR);
			int rangeLastDollarIndex = range.lastIndexOf(DOLLAR);
			int rangeIndexOfHyphen = range.indexOf(HYPHEN);
			int indexOfPlus = range.indexOf(PLUS);
			String lastRange = null;
			String rangeMin = null;
			String rangeMax = null;
			if(indexOfPlus > -1){
				lastRange = range.substring(rangeFirstDollarIndex+1,indexOfPlus-1);
			}
			else if(rangeFirstDollarIndex > -1 && rangeLastDollarIndex > -1 && rangeIndexOfHyphen > -1){
			 rangeMin = range.substring(rangeFirstDollarIndex+1,rangeIndexOfHyphen-1);
			 rangeMax =  range.substring(rangeLastDollarIndex+1);
			}

			pList = new ArrayList<RegistryItemVO>();
			pInStockRegVOList = new ArrayList<RegistryItemVO>();
			pNotInStockRegVOList = new ArrayList<RegistryItemVO>();

			for (SKUDetailVO sVo : pSVos) {
				pSkuIds = new ArrayList<String>();
				pSkuIds.add(sVo.getSkuId());
				reg = new RegistryItemVO();
				Iterator<String> mapIterator = mSkuRegItemVOMap.keySet().iterator();
				 String key_delimeter = "_";
					while (mapIterator.hasNext()) {
						String skuMapkey = mapIterator.next();
						Double personalizedPrice = mSkuRegItemVOMap.get(skuMapkey).getPersonlisedPrice();
						int indexOfDelimeter = skuMapkey.indexOf(key_delimeter);
						String skuIDFromKey = skuMapkey;
						if (indexOfDelimeter > -1) {
							skuIDFromKey = skuMapkey.substring(0, indexOfDelimeter);
							if(sVo.getSkuId().equalsIgnoreCase(skuIDFromKey)){
								if(mSkuRegItemVOMap.get(skuMapkey).getLtlDeliveryServices()!=null){
									reg = mSkuRegItemVOMap.get(skuMapkey);
									mSkuRegItemVOMap.remove(skuMapkey);
									break;
								}
								else if(null != lastRange && !StringUtils.isEmpty(lastRange)){
									if(personalizedPrice >= Double.parseDouble(lastRange)){
										reg = mSkuRegItemVOMap.get(skuMapkey);
										mSkuRegItemVOMap.remove(skuMapkey);
										break;
									}
								}
								else if(!BBBUtility.isEmpty(rangeMin) && !BBBUtility.isEmpty(rangeMax)){
									if(personalizedPrice >= Double.parseDouble(rangeMin) && personalizedPrice <=Double.parseDouble(rangeMax)){
										reg = mSkuRegItemVOMap.get(skuMapkey);
										mSkuRegItemVOMap.remove(skuMapkey);
										break;
									}
								}
							}
						}
						else if(sVo.getSkuId().equalsIgnoreCase(skuIDFromKey)){
							
							reg = mSkuRegItemVOMap.get(skuIDFromKey);
						}
						
					}
				String productId = getCatalogTools().getParentProductForSku(sVo.getSkuId());
				RepositoryItem price = null;
				Object priceList;
				PriceListManager plManager = getPriceListManager();			
				if(sVo.getSkuId() != null){
					try {
						priceList = plManager.getPriceList(profile, getProfilePriceListPropertyName());
						if(sVo.getSkuId() instanceof String){
		                    price = plManager.getPrice((RepositoryItem)priceList, productId, sVo.getSkuId());
						}
					} catch (PriceListException e) {
						logError(LogMessageFormatter.formatMessage(pRequest, 
								"PriceListException from withDefaultPrice of RegistryItemsDisplayDroplet ", 
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1081),e);
					}
				}
				if(price != null){
					String itemPrice = String.valueOf(price.getPropertyValue("listPrice"));
					reg.setPrice(itemPrice);
				}
			
				reg.setsKUDetailVO(sVo);

				// API call for Inventory status
				boolean isBelowLine = this.getCatalogTools()
						.isSKUBelowLine(siteId, sVo.getSkuId());
			
				reg.setIsBelowLineItem(String
						.valueOf(isBelowLine));

				if (!isBelowLine) {
					pInStockRegVOList.add(reg);
				} else {
					pNotInStockRegVOList.add(reg);
				}

				pList.add(reg);
			}
			
			Collections.sort(pList, new PriceListComparator());
			if(!BBBUtility.isEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY) && priceRangeList!=null){
				key=getNewKeyRange(key);
			}
				categoryBuckets.put(key, pList);
			if (pInStockRegVOList.isEmpty()) {
				inStockCategoryBuckets.put(key, null);
			} else {
				Collections.sort(pInStockRegVOList, new PriceListComparator());
				inStockCategoryBuckets.put(key,
						pInStockRegVOList);
			}
			if (pNotInStockRegVOList.isEmpty()) {
				notInStockCategoryBuckets.put(key, null);
			} else {
				Collections.sort(pNotInStockRegVOList, new PriceListComparator());
				notInStockCategoryBuckets.put(key,
						pNotInStockRegVOList);
			}
		}
		getBucket(categoryBuckets, inStockCategoryBuckets,
				notInStockCategoryBuckets, priceRangeList);
		String emptyFlag = "true";
		for (String bucket : notInStockCategoryBuckets.keySet()) {
			List<RegistryItemVO> pUnList = notInStockCategoryBuckets
					.get(bucket);
			if (pUnList != null) {
				emptyFlag = "false";
			}
		}
		
		pRequest.setParameter("emptyOutOfStockListFlag",
				emptyFlag);
		// System.out.println("Complete Buckets: " +
		// categoryBuckets);
	}

	
	public String getNewKeyRange(String value)
	{
		Properties pAttributes = new Properties();
		String newKey=null;
		Pattern p = Pattern.compile(BBBCoreConstants.PATTERN_FORMAT);
		Matcher m = null;
		m = p.matcher(value);
		int i = 0;
		String minPrice = BBBCoreConstants.BLANK, maxPrice = BBBCoreConstants.BLANK;
		while (m.find()) {
			if (i == 0) {
				minPrice = m.group();
				minPrice=minPrice.substring(1);
				i++;
			} else if (i == 1){
				maxPrice = m.group();
				maxPrice=maxPrice.substring(1);
			}
		}
           pAttributes.setProperty(BBBInternationalShippingConstants.ROUND,BBBInternationalShippingConstants.DOWN);
           newKey=BBBUtility.convertToInternationalPrice(value, minPrice, maxPrice, pAttributes);
           	
		return newKey;
	}
	
	private void withDefaultCategory(final DynamoHttpServletRequest pRequest,
			String registryTypeId,
			 Map<String, RegistryItemVO> mSkuRegItemVOMap,
			final List<String> skuIds,
			Map<String, List<RegistryItemVO>> categoryBuckets,
			Map<String, List<RegistryItemVO>> inStockCategoryBuckets,
			Map<String, List<RegistryItemVO>> notInStockCategoryBuckets,
			Map<String, RegistryCategoryMapVO> getCategoryForRegistry)
			throws BBBSystemException, BBBBusinessException {
		List<SKUDetailVO> pSVos;
		List<RegistryItemVO> pList;
		List<RegistryItemVO> pInStockRegVOList;
		List<RegistryItemVO> pNotInStockRegVOList;
		Map<String, List<SKUDetailVO>> map;
		pRequest.setParameter(
					BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO,
					getCategoryForRegistry);
		final String siteId = pRequest.getParameter(SITE_ID_PARAM);
		
		// BPSI-1049 Start: enableLTLRegForSite false means LTL item will be below the line and will not be 
		// available for Add to Cart while true means it can be added to cart
		String enableLTLRegForSiteValue = BBBConfigRepoUtils.getStringValue
				(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
						BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE);
		boolean enableLTLRegForSite = false;
		if(BBBUtility.isNotEmpty(enableLTLRegForSiteValue)) {
			enableLTLRegForSite = Boolean.parseBoolean(enableLTLRegForSiteValue);	
		}
		
		map = getCatalogTools().sortSkubyRegistry(skuIds, null,registryTypeId, siteId,
					BBBCatalogConstants.CATEGORY_SORT_TYPE,null);
				for (String key : map.keySet()) {
				pSVos = map.get(key);
				pList = new ArrayList<RegistryItemVO>();
				pInStockRegVOList = new ArrayList<RegistryItemVO>();
				pNotInStockRegVOList = new ArrayList<RegistryItemVO>();
			for (SKUDetailVO sVo : pSVos) {
				mSkuRegItemVOMap = skuDetailsVO(mSkuRegItemVOMap, pList, pInStockRegVOList,
							pNotInStockRegVOList, sVo, siteId, enableLTLRegForSite);
				}
				categoryBuckets.put(key, pList);
				if (pInStockRegVOList.isEmpty()) {
					inStockCategoryBuckets.put(key, null);
				} else {
					inStockCategoryBuckets.put(key,
							pInStockRegVOList);
				}
				if (pNotInStockRegVOList.isEmpty()) {
					notInStockCategoryBuckets.put(key, null);
				} else {
					notInStockCategoryBuckets.put(key,
							pNotInStockRegVOList);
				}
			}
			for (String catBucket : getCategoryForRegistry.keySet()) {
				if (!categoryBuckets.containsKey(catBucket)) {
					categoryBuckets.put(catBucket, null);
				}
				if (!inStockCategoryBuckets.containsKey(catBucket)) {
					inStockCategoryBuckets.put(catBucket, null);
				}
				if (!notInStockCategoryBuckets
						.containsKey(catBucket)) {
					notInStockCategoryBuckets.put(catBucket, null);
				}
			}
			String emptyFlag = "true";
			for (String bucket : notInStockCategoryBuckets.keySet()) {
				final List<RegistryItemVO> pUnList = notInStockCategoryBuckets
						.get(bucket);
				if (pUnList != null) {
					emptyFlag = "false";
				}
			}
			pRequest.setParameter("emptyOutOfStockListFlag",
					emptyFlag);
	}

	private StringBuilder checkPrice(StringBuilder skuTempList,
			final List<String> skuIds,  Map<String, Double> skuIdPriceMap,
			Iterator<RegistryItemVO> it,boolean isGiftGiver) throws BBBBusinessException,
			BBBSystemException {
		String salePrice;
		String listPrice;
		Double inCartPrice = 0.0;
		RegistryItemVO registryItemVO = (RegistryItemVO) it
				.next();
		String productId = getCatalogTools().getParentProductForSku(
				String.valueOf(registryItemVO.getSku()), true);
		
		if(productId == null){
			logDebug("CLS=[RegistryItemsDisplayDroplet]/" +
					"Mthd=[CheckPrice]/MSG=[Parent productId null for SKU]=]"+registryItemVO.getSku());
			return skuTempList ;
		}
		
		if(registryItemVO.getJdaRetailPrice()>0)
		{
			salePrice = String
					.valueOf(registryItemVO.getJdaRetailPrice());
			listPrice = String
					.valueOf(registryItemVO.getJdaRetailPrice());			
		}
		else
		{		
			salePrice = String
					.valueOf(getCatalogTools()
							.getSalePrice(
									productId,
									String.valueOf(registryItemVO
											.getSku())));
			listPrice = String
					.valueOf(getCatalogTools()
							.getListPrice(
									productId,
									String.valueOf(registryItemVO
											.getSku())));
			
		}
		
		boolean priceFound = false;
		final boolean isInCartSKU=this.getCatalogTools().getSkuIncartFlag(String.valueOf(registryItemVO.getSku()));
		if(isInCartSKU){
		inCartPrice=this.getCatalogTools().getIncartPrice(productId, String.valueOf(registryItemVO.getSku()));
		}
		if((registryItemVO.getPersonalisedCode()!= null && !StringUtils.isEmpty(registryItemVO.getPersonalisedCode())) ){
			StringBuilder str = new StringBuilder(String.valueOf(registryItemVO.getSku()));
			str.append("_");
			String key= str.append(String.valueOf(String.valueOf(registryItemVO.getRefNum()))).toString();
			skuIdPriceMap.put(key,registryItemVO.getPersonlisedPrice());
			priceFound = true;
		}
		else if(BBBUtility.isNotEmpty(registryItemVO.getLtlDeliveryServices())){
			StringBuilder str = new StringBuilder(String.valueOf(registryItemVO.getSku()));
			str.append("_");
			String key= str.append(String.valueOf(registryItemVO.getLtlDeliveryServices())).toString();
			if(!isGiftGiver && isInCartSKU){	
	        	skuIdPriceMap.put(
	        			key,
						inCartPrice);
	        	priceFound = true;
			}else if (salePrice !=null && !salePrice.equalsIgnoreCase("0.0")) {
				skuIdPriceMap.put(
						key,
						Double.valueOf(salePrice));
				priceFound = true;
				
			} else if( listPrice !=null){
				skuIdPriceMap.put(
						key,
						Double.valueOf(listPrice));
				priceFound = true;
			} 
		}
		else{
		     if(!isGiftGiver && isInCartSKU){	
        	
        	skuIdPriceMap.put(
					String.valueOf(registryItemVO.getSku()),
					inCartPrice);
        	priceFound = true;
		}else if (salePrice !=null && !salePrice.equalsIgnoreCase("0.0")) {
			skuIdPriceMap.put(
					String.valueOf(registryItemVO.getSku()),
					Double.valueOf(salePrice));
			priceFound = true;
			
		} else if( listPrice !=null){
			skuIdPriceMap.put(
					String.valueOf(registryItemVO.getSku()),
					Double.valueOf(listPrice));
			priceFound = true;
		} 
		
		}

		//if sku price is deteremined then only add sku, otherwise ignore this sku
		if( priceFound){
			skuIds.add(String.valueOf(registryItemVO.getSku()));
			skuTempList = skuTempList.append(registryItemVO
					.getSku() + ";");
		} else{		
			logDebug("CLS=[RegistryItemsDisplayDroplet]/" +
				"Mthd=[CheckPrice]/MSG=[PriceFound = false ] sku="+registryItemVO.getSku());
		}
		return skuTempList;
	}
	

	private Map<String, RegistryItemVO> skuDetailsVO(
			 Map<String, RegistryItemVO> mSkuRegItemVOMap,
			List<RegistryItemVO> pList, List<RegistryItemVO> pInStockRegVOList,
			List<RegistryItemVO> pNotInStockRegVOList, SKUDetailVO sVo, final String siteId,final boolean enableLTLRegForSite)
			throws BBBSystemException, BBBBusinessException {
		List<String> pSkuIds;
		RegistryItemVO reg;
		pSkuIds = new ArrayList<String>();
		pSkuIds.add(sVo.getSkuId());
		reg = new RegistryItemVO();
		// System.out.println("Category: " +key
		// +"Reg Item VO: " +
		// mSkuRegItemVOMap.get(sVo.getSkuId()));
		Iterator<String> mapIterator = mSkuRegItemVOMap.keySet().iterator();
		 String key_delimeter = "_";
			while (mapIterator.hasNext()) {
				String key = mapIterator.next();
				int indexOfDelimeter = key.indexOf(key_delimeter);
				String skuIDFromKey = key;
				if (indexOfDelimeter > -1) {
					skuIDFromKey = key.substring(0, indexOfDelimeter);
					if(sVo.getSkuId().equalsIgnoreCase(skuIDFromKey)){
					reg = mSkuRegItemVOMap.get(key);
					mSkuRegItemVOMap.remove(key);
					break;
					}
				}
				else if(sVo.getSkuId().equalsIgnoreCase(skuIDFromKey)){
					reg = mSkuRegItemVOMap.get(skuIDFromKey);
				}
				
			}
		reg.setsKUDetailVO(sVo);
		// API call for Inventory status

		Boolean isBelowLine = this.getCatalogTools()
				.isSKUBelowLine(siteId, sVo.getSkuId());
		
		reg.setIsBelowLineItem(String.valueOf(isBelowLine));
	if (!isBelowLine) {
			pInStockRegVOList.add(reg);
		} else {
			pNotInStockRegVOList.add(reg);
		}

		pList.add(reg);
		return  mSkuRegItemVOMap;
	}
	
	/**
	 * Sets the certona sku list.
	 *
	 * @param pRequest the request
	 * @param skuTempList the sku temp list
	 * @param skuIds the sku ids
	 * @param skuIdPriceMap the sku id price map
	 * @param it the it
	 * @param certonaTopRegCount the certona top reg count
	 * @param tempCertonaList the temp certona list
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public void setCertonaSkuList(final DynamoHttpServletRequest pRequest,
			StringBuilder skuTempList, final List<String> skuIds,
			Map<String, Double> skuIdPriceMap,
			Iterator<RegistryItemVO> it, int certonaTopRegCount,
			StringBuilder tempCertonaList) throws BBBBusinessException,
			BBBSystemException {
		
		logDebug("Setting certona Sku List for showing Top Registry items");
		if(skuTempList==null){
			skuTempList = new StringBuilder("");
		}
		if(skuIdPriceMap==null){
			skuIdPriceMap = new HashMap<String, Double>();
		}
		if(tempCertonaList==null){
			tempCertonaList = new StringBuilder("");
		}
		String skuList;
		String certonaSkuList;
		while (it.hasNext()) {
			certonaTopRegCount++;
			if(certonaTopRegCount==getCertonaListMaxCount())
				tempCertonaList.append(skuTempList);
			boolean giftGiver=Boolean.valueOf(pRequest.getParameter(IS_GIFT_GIVER));
			skuTempList = checkPrice(skuTempList, skuIds, skuIdPriceMap, it,giftGiver);
		}
		if(certonaTopRegCount>0 && certonaTopRegCount<getCertonaListMaxCount())
			tempCertonaList.append(skuTempList);
		
		if (skuTempList.length() > 0) {
			skuList = skuTempList.substring(0,
					skuTempList.length() - 1);
			certonaSkuList = tempCertonaList.substring(0, tempCertonaList.length() - 1);
			pRequest.setParameter(BBBGiftRegistryConstants.SKU_LIST, skuList);
			pRequest.setParameter(BBBGiftRegistryConstants.CERTONA_SKU_LIST, certonaSkuList);
		}
		logDebug("Exiting setCertonaSkuList method");
	}

	private void getBucket(
			final Map<String, List<RegistryItemVO>> categoryBuckets,
			final Map<String, List<RegistryItemVO>> inStockCategoryBuckets,
			final Map<String, List<RegistryItemVO>> notInStockCategoryBuckets,
			List<String> priceRangeList) {
		for (String pRange : priceRangeList) {
			if (!categoryBuckets.containsKey(pRange)) {
				categoryBuckets.put(pRange, null);
			}
			if (!inStockCategoryBuckets.containsKey(pRange)) {
				inStockCategoryBuckets.put(pRange, null);
			}
			if (!notInStockCategoryBuckets.containsKey(pRange)) {
				notInStockCategoryBuckets.put(pRange, null);
			}
		}
	}

	private String checkRegistryType(final String  eventTypeCode,
			String registryTypeId, final Iterator<RegistryTypeVO> iter) {
		while (iter.hasNext()) {
			RegistryTypeVO registryTypeVO = (RegistryTypeVO) iter
					.next();
			if (registryTypeVO.getRegistryCode().equalsIgnoreCase(
					eventTypeCode)) {
				registryTypeId = registryTypeVO.getRegistryTypeId();
			}
		}
		return registryTypeId;
	}

	private List<String> getLTLskuList(List<RegistryItemVO> registryItemList)
	{
		ArrayList<String> arrList = new ArrayList<String>();
		for(RegistryItemVO reVo : registryItemList)
		{
			if(BBBCoreConstants.LTL.equalsIgnoreCase(reVo.getItemType()))
			{
				arrList.add(String.valueOf(reVo.getSku()));
			}
		}
		return arrList;
	}
	/**
	 * Gets the gift registry manager.
	 * 
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(
			final GiftRegistryManager pGiftRegistryManager) {
		mGiftRegistryManager = pGiftRegistryManager;
	}

	/**
	 * Gets the registry items list service name.
	 * 
	 * @return the registryItemsListServiceName
	 */
	public String getRegistryItemsListServiceName() {
		return mRegistryItemsListServiceName;
	}

	/**
	 * Sets the registry items list service name.
	 * 
	 * @param registryItemsListServiceName
	 *            the registryItemsListServiceName to set
	 */
	public void setRegistryItemsListServiceName(
			final String registryItemsListServiceName) {
		this.mRegistryItemsListServiceName = registryItemsListServiceName;
	}

	/**
	 * Gets the catalog tools.
	 * 
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 * 
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}
	
	/**
	 * if the item is not exist in the content remove from the list
	 * @param listRegistryItemVO
	 * @return
	 */
	public List<RegistryItemVO> fliterNotAvliableItem(List<RegistryItemVO> listRegistryItemVO )
	{
		if (null != listRegistryItemVO && listRegistryItemVO.size() > 0) {
			for (int index = listRegistryItemVO.size() - 1; index >= 0; --index) {

				RegistryItemVO registryItemVO = (RegistryItemVO) listRegistryItemVO
						.get(index);
				try{
					
					getCatalogTools().getParentProductForSku(
							String.valueOf(registryItemVO.getSku()), true);
					}
					catch (Exception exception)
					{
						listRegistryItemVO.remove(index);
					}
			}
			
		}
		return listRegistryItemVO;
	}
	
	private void setCertonaItemList(DynamoHttpServletRequest pRequest,
			List<RegistryItemVO> registryItemsAll) {
		logDebug("RegistryItemsDisplayDroplet :setCertonaItemList Start");
		StringBuffer productLists= new StringBuffer();
		String itemList="";
		boolean isProductAdded=false;
		for(RegistryItemVO registryItemVO :registryItemsAll){
			if(registryItemVO.getsKUDetailVO() != null && registryItemVO.getsKUDetailVO().getParentProdId() != null){
				
				String prodId=registryItemVO.getsKUDetailVO().getParentProdId();
				productLists.append(prodId+";");
				isProductAdded=true;
			}
		}
		if(isProductAdded){
			itemList=productLists.substring(0, productLists.length()-1);
		}
		pRequest.setParameter(BBBGiftRegistryConstants.ITEM_LIST, itemList);
		logDebug("RegistryItemsDisplayDroplet :setCertonaItemList End");
		
	}

	/**
	 * @return the profilePriceListPropertyName
	 */
	public String getProfilePriceListPropertyName() {
		return profilePriceListPropertyName;
	}

	/**
	 * @param profilePriceListPropertyName the profilePriceListPropertyName to set
	 */
	public void setProfilePriceListPropertyName(String profilePriceListPropertyName) {
		this.profilePriceListPropertyName = profilePriceListPropertyName;
	}

	/**
	 * @return the priceListManager
	 */
	public PriceListManager getPriceListManager() {
		return priceListManager;
	}

	/**
	 * @param priceListManager the priceListManager to set
	 */
	public void setPriceListManager(PriceListManager priceListManager) {
		this.priceListManager = priceListManager;
	}
	
	/**
	 * @return the topRegMaxCount
	 */
	public String getTopRegMaxCount() {
		return topRegMaxCount;
	}

	/**
	 * @param topRegMaxCount the topRegMaxCount to set
	 */
	public void setTopRegMaxCount(String topRegMaxCount) {
		this.topRegMaxCount = topRegMaxCount;
	}
	
	/** The Constant REGISTRY_ID */
	public static final ParameterName REGISTRY_ID = ParameterName
			.getParameterName( BBBGiftRegistryConstants.REGISTRY_ID );
	
	
	/** The Constant C2_ID */
	public static final ParameterName C2_ID = ParameterName
			.getParameterName(BBBGiftRegistryConstants.C2_ID);
	/** The Constant C1_ID */
	public static final ParameterName C1_ID = ParameterName
			.getParameterName(BBBGiftRegistryConstants.C1_ID);
	/** The Constant C3_ID */
	public static final ParameterName C3_ID = ParameterName
			.getParameterName(BBBGiftRegistryConstants.C3_ID);

	/** The parameter for sort seq. */
	public static final ParameterName SORT_SEQ = ParameterName
			.getParameterName(BBBGiftRegistryConstants.SORT_SEQ);

	/** The Constant VIEW. */
	public static final ParameterName VIEW = ParameterName
			.getParameterName( BBBGiftRegistryConstants.VIEW );

	/** The Constant REG_EVENT_TYPE_CODE */
	public static final ParameterName REG_EVENT_TYPE_CODE = ParameterName
			.getParameterName(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE);
	
	/** The Constant START_INDEX */
	public static final ParameterName EVENT_TYPE = ParameterName
			.getParameterName(BBBGiftRegistryConstants.EVENT_TYPE);

	/** The Constant START_INDEX */
	public static final ParameterName START_INDEX = ParameterName
			.getParameterName(BBBGiftRegistryConstants.START_INDEX);

	/** The Constant BULK_SIZE*/
	public static final ParameterName BULK_SIZE = ParameterName
			.getParameterName(BBBGiftRegistryConstants.BULK_SIZE);

	/** The Constant IS_GIFT_GIVER*/
	public static final ParameterName IS_GIFT_GIVER = ParameterName
			.getParameterName(BBBGiftRegistryConstants.IS_GIFT_GIVER);
	
	/** The Constant IS_AVAIL_WEBPUR*/
	public static final ParameterName IS_AVAIL_WEBPUR = ParameterName
			.getParameterName(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR);
	
	/** The Constant SITE_ID*/
	public static final ParameterName SITE_ID_PARAM = ParameterName
			.getParameterName(SITE_ID);
	
	/** The Constant EVENT_DATE*/
	public static final ParameterName EVENT_DATE_PARAM = ParameterName
			.getParameterName(EVENT_DATE);
	
	/** The Constant profile*/
	public static final ParameterName PROFILE = ParameterName
			.getParameterName("profile");
	
	/** The Constant IS_MY_ITEMS_CHECKLIST. */
	public static final ParameterName IS_MY_ITEMS_CHECKLIST = ParameterName
			.getParameterName(BBBGiftRegistryConstants.IS_MY_ITEMS_CHECKLIST);
	
	public static final ParameterName IS_SECOND_CALL = ParameterName
			.getParameterName(BBBGiftRegistryConstants.IS_SECOND_CALL);
	
	/** The Constant FROM_CHECKLIST */
	public static final ParameterName FROM_CHECKLIST = ParameterName
			.getParameterName( BBBGiftRegistryConstants.FROM_CHECKLIST );
	
	public BBBEximManager getEximManager() {
		return eximManager;
	}
	
	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}

	public CheckListManager getCheckListManager() {
		return checkListManager;
	}

	public void setCheckListManager(CheckListManager checkListManager) {
		this.checkListManager = checkListManager;
	}


}