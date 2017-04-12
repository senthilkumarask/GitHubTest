package com.bbb.integration.interactive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.checklist.vo.CategoryVO;
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.commerce.checklist.vo.ICUtilityVO;
import com.bbb.commerce.checklist.vo.NonRegistryGuideVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

import atg.droplet.DropletException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
/**
 * 
 * Utility class for checklist
 *
 */
public class InteractiveChecklistFormHandler extends BBBGenericFormHandler {

	private String currentSite;
	private Map<String, String> listOfSites = new HashMap<String, String>();
	private String skuIds;

	/**
	 * GiftRegistry Manager object
	 */
	private GiftRegistryManager giftRegistryManager;
	private CheckListTools checkListTools;
	private CheckListManager checkListManager;
	private List<ICUtilityVO> iCUtilityVO;
	Map<String, List<ICUtilityVO>> utilityMap;
	Set<String> invalidSkus;
	private Set<String> skuNotMapped;
	private String guideId;
	private String addOrHideGuideType;
	private BBBSessionBean sessionBean;
	private boolean activateGuideInRegistryRibbon;
	private String selectedGuideType;
	private List<NonRegistryGuideVO> nonRegistryGuideVOs;
	private boolean hideGuideFlow;
	private String hideGuideSuccessURL;
	private String nextActivatedGuideType;
	private String addGuideSuccessURL;
	private String addGuideRedirectURL;
	private boolean mobileRequest;
	private List<String> staticCheckListTypes;
	private String registryId;
	private String skuURL ;
	private String serverNameUS;
	private String serverNameBABY;
	private String serverNameCA;
	private BBBCatalogTools catalogTools;
	/**
	 * @return the mobileRequest
	 */
	public boolean isMobileRequest() {
		return this.mobileRequest;
	}

	/**
	 * @param mobileRequest
	 *            the mobileRequest to set
	 */
	public void setMobileRequest(boolean mobileRequest) {
		this.mobileRequest = mobileRequest;
	}

	
	public String getAddGuideSuccessURL() {
		return addGuideSuccessURL;
	}
	public void setAddGuideSuccessURL(String addGuideSuccessURL) {
		this.addGuideSuccessURL = addGuideSuccessURL;
	}
	
	public String getAddGuideRedirectURL() {
		return addGuideRedirectURL;
	}
	public void setAddGuideRedirectURL(String addGuideRedirectURL) {
		this.addGuideRedirectURL = addGuideRedirectURL;
	}
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}
	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}
	public String getGuideId() {
		return guideId;
	}
	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}
	public CheckListManager getCheckListManager() {
		return checkListManager;
	}
	public void setCheckListManager(CheckListManager checkListManager) {
		this.checkListManager = checkListManager;
	}
	/**
	 * 
	 * @return
	 */
	public Set<String> getInvalidSkus() {
		return invalidSkus;
	}
    /**
     * 
     * @param invalidSkus
     */
	public void setInvalidSkus(Set<String> invalidSkus) {
		this.invalidSkus = invalidSkus;
	}
	/**
	 * 
	 * @return
	 */
	public String getInteractiveSuccessURL() {
		return interactiveSuccessURL;
	}
	/**
	 * 
	 * @param interactiveSuccessURL
	 */
	public void setInteractiveSuccessURL(String interactiveSuccessURL) {
		this.interactiveSuccessURL = interactiveSuccessURL;
	}
	/**
	 * 
	 * @return
	 */
	public String getInteractiveErrorURL() {
		return interactiveErrorURL;
	}
	/**
	 * 
	 * @param interactiveErrorURL
	 */
	public void setInteractiveErrorURL(String interactiveErrorURL) {
		this.interactiveErrorURL = interactiveErrorURL;
	}
	
	private String interactiveSuccessURL;
	private String interactiveErrorURL;
	/**
	 * 
	 * @return
	 */
	public Map<String, List<ICUtilityVO>> getUtilityMap() {
		return utilityMap;
	}
	/**
	 * 
	 * @param utilityMap
	 */
	public void setUtilityMap(Map<String, List<ICUtilityVO>> utilityMap) {
		this.utilityMap = utilityMap;
	}
	/**
	 * 
	 * @return
	 */
	public List<ICUtilityVO> getiCUtilityVO() {
		return iCUtilityVO;
	}
	/**
	 * 
	 * @param iCUtilityVO
	 */
	public void setiCUtilityVO(List<ICUtilityVO> iCUtilityVO) {
		this.iCUtilityVO = iCUtilityVO;
	}

	/**
	 * @return the mGiftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return giftRegistryManager;
	}

	/**
	 * @param pGiftRegistryManager
	 *            the pGiftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager giftRegistryManager) {
		this.giftRegistryManager = giftRegistryManager;
	}
	/**
	 * 
	 * @return
	 */
	public String getCurrentSite() {
		return currentSite;
	}
	/**
	 * 
	 * @param currentSite
	 */
	public void setCurrentSite(String currentSite) {
		this.currentSite = currentSite;
	}
	/**
	 * 
	 * @return
	 */
	public Map<String, String> getListOfSites() {
		return listOfSites;
	}
	/**
	 * 
	 * @param listOfSites
	 */
	public void setListOfSites(Map<String, String> listOfSites) {
		this.listOfSites = listOfSites;
	}

	/**
	 * @return the checkListTools
	 */
	public CheckListTools getCheckListTools() {
		return checkListTools;
	}

	/**
	 * @param checkListTools
	 *            the checkListTools to set
	 */
	public void setCheckListTools(CheckListTools checkListTools) {
		this.checkListTools = checkListTools;
	}

	/**
	 * Gets the sku ids.
	 * 
	 * @return the skuIds
	 */
	public String getSkuIds() {
		return this.skuIds;
	}

	/**
	 * Sets the sku ids.
	 * 
	 * @param pSkuIds
	 *            the skuIds to set
	 */
	public void setSkuIds(final String pSkuIds) {
		this.skuIds = pSkuIds;
	}

	public boolean isHideGuideFlow() {
		return hideGuideFlow;
	}
	public void setHideGuideFlow(boolean hideGuideFlow) {
		this.hideGuideFlow = hideGuideFlow;
	}
	public String getHideGuideSuccessURL() {
		return hideGuideSuccessURL;
	}
	public void setHideGuideSuccessURL(String hideGuideSuccessURL) {
		this.hideGuideSuccessURL = hideGuideSuccessURL;
	}
	public String getNextActivatedGuideType() {
		return nextActivatedGuideType;
	}
	public void setNextActivatedGuideType(String nextActivatedGuideType) {
		this.nextActivatedGuideType = nextActivatedGuideType;
	}
	
	public String getAddOrHideGuideType() {
		return addOrHideGuideType;
	}
	public void setAddOrHideGuideType(String addOrHideGuideType) {
		this.addOrHideGuideType = addOrHideGuideType;
	}
	
	public String getSkuURL() {
		return skuURL;
	}

	public void setSkuURL(String skuURL) {
		this.skuURL = skuURL;
	}

	public String getServerNameUS() {
		return serverNameUS;
	}

	public void setServerNameUS(String serverNameUS) {
		this.serverNameUS = serverNameUS;
	}

	public String getServerNameBABY() {
		return serverNameBABY;
	}

	public void setServerNameBABY(String serverNameBABY) {
		this.serverNameBABY = serverNameBABY;
	}

	public String getServerNameCA() {
		return serverNameCA;
	}

	public void setServerNameCA(String serverNameCA) {
		this.serverNameCA = serverNameCA;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**Fetches the sku details from checklist repository
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleFetchRegCatInfo(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("handleValidateUser method start. ");
		List<RegistryTypeVO> registryTypeList;
		List<ICUtilityVO> listRegCategories = new ArrayList<ICUtilityVO>();
		ICUtilityVO icvo = null;
		Map<String, List<ICUtilityVO>> utilityMap = new HashMap<String, List<ICUtilityVO>>();
		Set<String> invalidSkus = new HashSet<String>();
		Set<String> skuNotMapped = new HashSet<String>();
		
		try {
			//Get all registry types for the site
			registryTypeList = getGiftRegistryManager().fetchRegistryTypes(
					getCurrentSite());

			RepositoryItem skuItem = null;
			if(registryTypeList != null){
				//comma separated skus
				if (BBBUtility.isNotEmpty(getSkuIds()) && getSkuIds().contains(BBBCoreConstants.COMMA)) {
					final String[] skuIdArray = getSkuIds().split(
							BBBCoreConstants.COMMA);
					//iterate over each sku
					if (skuIdArray != null && skuIdArray.length > 0) {
						for (final String skuId : skuIdArray) {
							if (!BBBUtility.isEmpty(skuId.trim())) {
								skuItem = this.getCheckListTools()
										.getSkuComputedAttributeItem(
												skuId.trim());
							}
							if(skuItem != null){
								listRegCategories = new ArrayList<ICUtilityVO>();
								Iterator<RegistryTypeVO> it = registryTypeList.iterator();
								//Iterate over each registry type
								while (it.hasNext()) {
									icvo = new ICUtilityVO();
									//Get the checklist VO
									CheckListVO checkListVO = getCheckListVO(icvo, it);
									if(checkListVO != null){
										//Check if the sku is present in the checklist category
											boolean validsku = compareRegistryCatWithSkuCat(checkListVO,
													skuItem, icvo);
											if(validsku){
												listRegCategories.add(icvo);
												utilityMap.put(skuId, listRegCategories);
												this.setUtilityMap(utilityMap);
											}else{
												skuNotMapped.add(skuId.trim());
												this.setSkuNotMapped(skuNotMapped);
											}
										}
									}
								}else{
										invalidSkus.add(skuId.trim());
										this.setInvalidSkus(invalidSkus);
									}
								
							}
						}
	
					}
				 else {
					//single sku entered
					if (!BBBUtility.isEmpty(getSkuIds().trim())) {
						skuItem = this.getCheckListTools()
								.getSkuComputedAttributeItem(
										getSkuIds().trim());
					}
					if(skuItem != null){
						Iterator<RegistryTypeVO> it = registryTypeList.iterator();
						//Iterate over each registry type
						while (it.hasNext()) {
							icvo = new ICUtilityVO();
							CheckListVO checkListVO = getCheckListVO(icvo, it);
							if(checkListVO != null){
								//Check if the sku is present in the checklist category
								boolean validsku = compareRegistryCatWithSkuCat(checkListVO, skuItem, icvo);
								if(validsku){
									listRegCategories.add(icvo);
									utilityMap.put(getSkuIds(), listRegCategories);
									this.setUtilityMap(utilityMap);
								}else{
									skuNotMapped.add(getSkuIds().trim());
									this.setSkuNotMapped(skuNotMapped);
								}
								}
							}
						}else{
								invalidSkus.add(getSkuIds().trim());
								this.setInvalidSkus(invalidSkus);
						}
					}
				this.setSkuURL(getCurrentSite());
				this.setServerNameUS(getCatalogTools().getConfigKeyValue(BBBCatalogConstants.CONTENT_CATALOG_KEYS, "site_server_name_US", "bedbathbeyond-87.sapient.com"));
				this.setServerNameBABY(getCatalogTools().getConfigKeyValue(BBBCatalogConstants.CONTENT_CATALOG_KEYS, "site_server_name_BABY", "buybuybaby-87.sapient.com"));
				this.setServerNameCA(getCatalogTools().getConfigKeyValue(BBBCatalogConstants.CONTENT_CATALOG_KEYS, "site_server_name_CA", "bedbathbeyondca-87.sapient.com")); 
			}
		}
		 catch (RepositoryException e) {
			logError("RepositoryException for Utility Checklist"+e);
		} catch (BBBSystemException e) {
			logError("BBBSystemException for Utility Checklist"+e);
			addFormException(new DropletException(e.getMessage()));
			
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException for Utility Checklist"+e);
			addFormException(new DropletException(e.getMessage()));
		}
		this.setInteractiveErrorURL(this.getInteractiveErrorURL());
		final String successURL = this.getInteractiveSuccessURL();
		this.setInteractiveSuccessURL(successURL);
		return this.checkFormRedirect(this.getInteractiveErrorURL(),
				this.getInteractiveErrorURL(), pRequest, pResponse);
	}
/**
 * 
 * @param icvo
 * @param it
 * @return
 * @throws BBBSystemException
 * @throws BBBBusinessException
 */
	private CheckListVO getCheckListVO(ICUtilityVO icvo,
			Iterator<RegistryTypeVO> it) throws BBBSystemException,
			BBBBusinessException {
		String registryCode = null;
		String registryDescription = null;
		RegistryTypeVO registryTypeVO  = it.next();
		registryCode = registryTypeVO.getRegistryCode();
		registryDescription = registryTypeVO.getRegistryDescription();
		icvo.setRegistryTypeDescription(registryDescription);
		boolean fromUtility = true;
		CheckListVO checkListVO =  this.getCheckListTools().populateStaticCheckListVO(registryCode, fromUtility);
		return checkListVO;
	}
/**
 * 
 * @param checkListVO
 * @param skuItem
 * @param icvo
 * @throws BBBSystemException
 * @throws BBBBusinessException
 */
	private boolean compareRegistryCatWithSkuCat(CheckListVO checkListVO,
			RepositoryItem skuItem, ICUtilityVO icvo)
			throws BBBSystemException, BBBBusinessException {
		//Get the categories applicable for the sku in the 
		List<String> catListForSku = this.getCheckListTools()
				.getCategoryListForSku(skuItem);

		CategoryVO c2CategoryVO = null;
		CategoryVO c3CategoryVO = null;
		if (!BBBUtility.isListEmpty(catListForSku)) {
			List<CategoryVO> catListForRegType = checkListVO
					.getCategoryListVO();
			if(catListForRegType != null){
				//Iterate over each C1
				for (CategoryVO c1Cat : catListForRegType) {
					List<CategoryVO> c2Categories = c1Cat.getChildCategoryVO();
					List<CategoryVO> childCats = null;//new HashMap<String, CategoryVO>();
					if(c2Categories!= null){
						//Iterate over each C2
						ListIterator<CategoryVO> c2CategoriesIter=c2Categories.listIterator();
						while (c2CategoriesIter.hasNext()) {
								c2CategoryVO = c2CategoriesIter.next();
								childCats = c2CategoryVO.getChildCategoryVO();
								//If c2 has child categories
								if (childCats != null && !childCats.isEmpty()) {
									ListIterator<CategoryVO> childCatsIter = childCats.listIterator();
									//Iterate over each C3
									while (childCatsIter.hasNext()) {
										c3CategoryVO = childCatsIter.next();
										//Check if the category is present in the catageory list for the sku
										if (catListForSku.contains(c3CategoryVO.getCategoryId())) {
											icvo.setC1Category(c1Cat.getDisplayName());
											icvo.setC2Category(c2CategoryVO
													.getDisplayName());
											icvo.setC3Category(c3CategoryVO
													.getDisplayName());
											if(!c1Cat.isShowCheckList()){
												icvo.setC1ShowOnChecklist(BBBCoreConstants.FALSE);
											}
											if(!c2CategoryVO.isShowCheckList()){
												icvo.setC2ShowOnChecklist(BBBCoreConstants.FALSE);
											}
											if(!c3CategoryVO.isShowCheckList()){
												icvo.setC3ShowOnChecklist(BBBCoreConstants.FALSE);
											}
											if(c1Cat.isDisabled()){
												icvo.setC1Disabled(BBBCoreConstants.RETURN_TRUE);
											}
											if(c2CategoryVO.isDisabled()){
												icvo.setC2Disabled(BBBCoreConstants.RETURN_TRUE);
											}
											if(c3CategoryVO.isDisabled()){
												icvo.setC3Disabled(BBBCoreConstants.RETURN_TRUE);
											}
											if(c1Cat.isDeleted()){
												icvo.setC1Deleted(BBBCoreConstants.RETURN_TRUE);
											}
											if(c2CategoryVO.isDeleted()){
												icvo.setC2Deleted(BBBCoreConstants.RETURN_TRUE);
											}
											if(c3CategoryVO.isDeleted()){
												icvo.setC3Deleted(BBBCoreConstants.RETURN_TRUE);
											}
										}
									}
								} else {
									/*
									 * Matching the registry items with C2s when C2s are
									 * leaf nodes.
									 */
									if (catListForSku.contains(c2CategoryVO.getCategoryId())) {
										icvo.setC1Category(c1Cat.getDisplayName());
										icvo.setC2Category(c2CategoryVO.getDisplayName());
										if(!c1Cat.isShowCheckList()){
											icvo.setC1ShowOnChecklist(BBBCoreConstants.FALSE);
										}
										if(!c2CategoryVO.isShowCheckList()){
											icvo.setC2ShowOnChecklist(BBBCoreConstants.FALSE);
										}
										if(c1Cat.isDisabled()){
											icvo.setC1Disabled(BBBCoreConstants.RETURN_TRUE);
										}
										if(c2CategoryVO.isDisabled()){
											icvo.setC2Disabled(BBBCoreConstants.RETURN_TRUE);
										}
										if(c1Cat.isDeleted()){
											icvo.setC1Deleted(BBBCoreConstants.RETURN_TRUE);
										}
										if(c2CategoryVO.isDeleted()){
											icvo.setC2Deleted(BBBCoreConstants.RETURN_TRUE);
										}
									}
								}
					}
				 }
			 }
		 }
		return true;
		}
		return false;
	}

	public Set<String> getSkuNotMapped() {
		return skuNotMapped;
	}
	public void setSkuNotMapped(Set<String> skuNotMapped) {
		this.skuNotMapped = skuNotMapped;
	}
	
	
	/** Methods add the Shopping Guide and Creates Cookie
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean handleAddOrHideShoppingGuide(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		
//		String guideId = this.getGuideId();
		String guideType = this.getAddOrHideGuideType();
		String isOverriddenUrl = null;
		HashMap<String, String> leafCategoryUrlMap = new HashMap<String, String>();
		logInfo("InteractiveChecklistFormHandler.handleAddRegistryGuide method started for Guide Type = " + guideType);
		String successUrl = null;
		try {
			//changes to check if guide is disabled then on click of "add this guide" button redirect to home directly without doing any processing
			NonRegistryGuideVO nonRegistryGuideVOUpdated = getCheckListManager()
					.getNonRegistryGuide(guideType, BBBCoreConstants.GUIDE);
			if (!nonRegistryGuideVOUpdated.isGuideDisabled() || isHideGuideFlow()) {
		if (!BBBUtility.isEmpty(guideType)) {
			final HashMap sessionMap = getSessionBean().getValues();
			Cookie cookie = this.getCheckListManager().createorUpdateRegistryGuideCookie(guideType, pRequest, isHideGuideFlow());
			BBBUtility.addCookie(pResponse, cookie, false);
			
				List<NonRegistryGuideVO> nonRegistryGuideVOs = (List<NonRegistryGuideVO>)sessionMap.get(BBBGiftRegistryConstants.GUIDE_VO_LIST);
				NonRegistryGuideVO nonRegistryGuideVO = null;
				if (isHideGuideFlow()) {
					String nextGuideOrReg = null;
//					successUrl = getHideGuideSuccessURL();
					String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
					if(channel!=null && channel.isEmpty()){
						channel = pRequest.getParameter(BBBCoreConstants.CHANNEL);
					}
					if(BBBUtility.isNotEmpty(channel) && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB))){
						//this.setAddGuideRedirectURL(getCheckListManager().getFirstLeafCategoryURL(guideType));
						successUrl = BBBCoreConstants.ATG_REST_IGNORE_REDIRECT;
					}
					// BBBI-5229 : Dsk | Getting system error is user hides a guide if there are two guides added
					if (!BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
					Iterator<NonRegistryGuideVO> iter=nonRegistryGuideVOs.iterator();
					int count=0;
					while (iter.hasNext()) {
						nonRegistryGuideVO=iter.next();
						if(nonRegistryGuideVO !=null && guideType.equalsIgnoreCase(nonRegistryGuideVO.getGuideTypeCode()))
						{break;
						}
						count++;
					}
					nonRegistryGuideVOs.remove(count);
					}
					
					if (!BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
						nonRegistryGuideVO = nonRegistryGuideVOs.get(BBBCoreConstants.ZERO);
						nextGuideOrReg = nonRegistryGuideVO.getGuideTypeCode();
						sessionMap.put(BBBCoreConstants.SELECTED_GUIDE_TYPE, nextGuideOrReg);
						this.setSelectedGuideType(nextGuideOrReg);
						this.setActivateGuideInRegistryRibbon(true);
					} else {
						getSessionBean().setActivateGuideInRegistryRibbon(false);
						this.setActivateGuideInRegistryRibbon(false);
						RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) getSessionBean().getValues().
								get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
						if (registrySummaryVO != null) {
							nextGuideOrReg = registrySummaryVO.getRegistryId();
						}
						sessionMap.put(BBBCoreConstants.SELECTED_GUIDE_TYPE, null);
					}
					this.setNextActivatedGuideType(nextGuideOrReg);
				} else {
					leafCategoryUrlMap = getCheckListManager().getFirstLeafCategoryURL(guideType);
					if(null != leafCategoryUrlMap){
						if(BBBCoreConstants.TRUE.equalsIgnoreCase(leafCategoryUrlMap.get(BBBCoreConstants.IS_OVERRIDDEN_URL))){
							this.setAddGuideRedirectURL(leafCategoryUrlMap.get(BBBCoreConstants.LEAF_CATEGORY_URL));
						}
						else {
							this.setAddGuideRedirectURL(BBBCoreConstants.CONTEXT_STORE + leafCategoryUrlMap.get(BBBCoreConstants.LEAF_CATEGORY_URL));
						}
					}
					//this.setAddGuideRedirectURL(BBBCoreConstants.CONTEXT_STORE + getCheckListManager().getFirstLeafCategoryURL(guideType));
					String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
					successUrl = getAddGuideSuccessURL();
					if(channel!=null && channel.isEmpty()){
									channel = pRequest.getParameter(BBBCoreConstants.CHANNEL);
								}
					if(BBBUtility.isNotEmpty(channel) && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) ||
												channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
						if(null != leafCategoryUrlMap){
						this.setAddGuideRedirectURL(leafCategoryUrlMap.get(BBBCoreConstants.LEAF_CATEGORY_URL));
						}
						successUrl = BBBCoreConstants.ATG_REST_IGNORE_REDIRECT;
					}
					if (BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
						nonRegistryGuideVOs = new ArrayList<NonRegistryGuideVO>();
					}
					//nonRegistryGuideVO = getCheckListManager().getNonRegistryGuide (guideType, BBBCoreConstants.GUIDE);
					//if (!nonRegistryGuideVO.isGuideDisabled()) {
					nonRegistryGuideVOs.add(0, nonRegistryGuideVOUpdated);
					nonRegistryGuideVOs = getCheckListManager().removeDuplicateNonRegVO(nonRegistryGuideVOs);
					this.setNonRegistryGuideVOs(nonRegistryGuideVOs);
					getSessionBean().setActivateGuideInRegistryRibbon(true);
					this.setActivateGuideInRegistryRibbon(true);
					sessionMap.put(BBBCoreConstants.SELECTED_GUIDE_TYPE, guideType);
					this.setSelectedGuideType(guideType);
					//}
				}
				sessionMap.put(BBBGiftRegistryConstants.GUIDE_VO_LIST, nonRegistryGuideVOs);
			
		}
			} else {
				final HashMap sessionMap = getSessionBean().getValues();
				this.setNonRegistryGuideVOs((List<NonRegistryGuideVO>)sessionMap.get(BBBGiftRegistryConstants.GUIDE_VO_LIST));
				if (null != (List<NonRegistryGuideVO>)sessionMap.get(BBBGiftRegistryConstants.GUIDE_VO_LIST)) {
					this.setActivateGuideInRegistryRibbon(true);
					this.setSelectedGuideType((String)sessionMap.get(BBBCoreConstants.SELECTED_GUIDE_TYPE));
				} else {
					this.setActivateGuideInRegistryRibbon(false);
				}
				this.setAddGuideRedirectURL("/store");
				successUrl = getAddGuideSuccessURL();
				String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
				if ((BBBUtility.isNotEmpty(channel) && (channel
						.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel
						.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)))) {
					this.setAddGuideRedirectURL("");
					successUrl = BBBCoreConstants.ATG_REST_IGNORE_REDIRECT;
				}
			}
		} catch (BBBSystemException exception) {
			logError("BBBSystemException occurred while adding Non Registry Guide for Guide Type = " + guideType, exception);
		} catch (BBBBusinessException exception) {
			logError("BBBBusinessException occurred while adding Non Registry Guide for Guide Type = " + guideType, exception);
		}
			
		return this.checkFormRedirect(successUrl, successUrl, pRequest, pResponse);
	}
	
	public boolean handleRegistrySummaryVO(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException{
		RegistrySummaryVO regSummaryVO = null;
	if(!BBBUtility.isEmpty(this.getRegistryId())){
		try {
			regSummaryVO = getGiftRegistryManager().getRegistryInfo(this.getRegistryId(), SiteContextManager.getCurrentSiteId());
		} catch (BBBSystemException e) {
			logError("BBBSystem Exception ManageRegistryChecklistDroplet" + e.getMessage());
			logDebug("Error Stack Trace:"+e);
		} catch (BBBBusinessException e) {
			logError("BBBBusiness Exception ManageRegistryChecklistDroplet" + e.getMessage());
			logDebug("Error Stack Trace:"+e);
		}
	}
			if (regSummaryVO != null) {
			getSessionBean().getValues().put(
					BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
					regSummaryVO);
	}
			return true;
	}
	/** Methods to fetch static checklist codes
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleFetchStaticCheckListTypes(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		logInfo("InteractiveChecklistFormHandler.handleFetchStaticCheckListTypes method started");
		 try {
			this.setStaticCheckListTypes(this.getCheckListManager().getStaticCheckLists());
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("BBBSystemException | BBBBusinessException occurred in method handleFetchStaticCheckListTypes() " + e);
		}
		return true;
	}
	
	public boolean isActivateGuideInRegistryRibbon() {
		return activateGuideInRegistryRibbon;
	}
	public void setActivateGuideInRegistryRibbon(
			boolean activateGuideInRegistryRibbon) {
		this.activateGuideInRegistryRibbon = activateGuideInRegistryRibbon;
	}
	public String getSelectedGuideType() {
		return selectedGuideType;
	}
	public void setSelectedGuideType(String selectedGuideType) {
		this.selectedGuideType = selectedGuideType;
	}
	public List<NonRegistryGuideVO> getNonRegistryGuideVOs() {
		return nonRegistryGuideVOs;
	}
	public void setNonRegistryGuideVOs(List<NonRegistryGuideVO> nonRegistryGuideVOs) {
		this.nonRegistryGuideVOs = nonRegistryGuideVOs;
	}

	public List<String> getStaticCheckListTypes() {
		return staticCheckListTypes;
	}

	public void setStaticCheckListTypes(List<String> staticCheckListTypes) {
		this.staticCheckListTypes = staticCheckListTypes;
	}

	public String getRegistryId() {
		return registryId;
	}

	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}

}
