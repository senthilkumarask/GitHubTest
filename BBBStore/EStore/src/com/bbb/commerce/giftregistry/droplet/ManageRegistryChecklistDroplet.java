package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import com.bbb.cms.BannerVO;
import com.bbb.cms.ManageRegistryChecklistVO;
import com.bbb.cms.manager.ContentTemplateManager;
import com.bbb.cms.vo.CMSResponseVO;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.checklist.vo.NonRegistryGuideVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

public class ManageRegistryChecklistDroplet extends BBBDynamoServlet {

	private ContentTemplateManager contentTemplateManager;
	private GiftRegistryManager giftRegistryManager;
	private BBBSessionBean sessionBean;
	private CheckListManager checkListManager;
	private List<NonRegistryGuideVO> nonRegistryGuideVOs;
	private boolean activateGuideInRegistryRibbon;
	private String selectedGuideType;
	/*
	 * This droplet fetches the list of links to be shown on the manage dropdown
	 * of a particular type of registry based on the registry type. Input Param
	 * : registry id
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) throws ServletException, IOException {
		final String registryId = req .getParameter(BBBCoreConstants.REGISTRY_ID);
		final String guideType = req .getParameter(BBBCoreConstants.GUIDE_TYPE);
		RegistrySummaryVO regSummaryVO = null;
		boolean fromAjax = Boolean.parseBoolean(req.getParameter("fromAjax"));
		BBBPerformanceMonitor.start(BBBPerformanceConstants.MANAGE_REGISTRY_CHECKLIST, BBBCoreConstants.DROPLET_MANAGE_REGSITRY_CHECKLIST);
		if (BBBUtility.isEmpty(guideType) && !BBBUtility.isEmpty(registryId)) {
			try {
				regSummaryVO = getGiftRegistryManager().getRegistryInfo(registryId, SiteContextManager.getCurrentSiteId());
			} catch (BBBSystemException e) {
				logError("BBBSystem Exception ManageRegistryChecklistDroplet" + e.getMessage());
				logDebug("Error Stack Trace:"+e);
			} catch (BBBBusinessException e) {
				logError("BBBBusiness Exception ManageRegistryChecklistDroplet" + e.getMessage());
				logDebug("Error Stack Trace:"+e);
			}
		}
		
		String subTypeCode = null;
		if (regSummaryVO != null) {
			subTypeCode = regSummaryVO.getRegistryType().getRegistryTypeName();
			logDebug("Entering ManageRegistryChecklistDroplet : service with registryType : "
					+ subTypeCode);
		} else if (!BBBUtility.isEmpty(guideType)) {
			subTypeCode = guideType;
			logDebug("Entering ManageRegistryChecklistDroplet : service with registryType : "
					+ subTypeCode);
			if (fromAjax) {
				Cookie cookie = this.getCheckListManager().createorUpdateRegistryGuideCookie(guideType, req, false);
				BBBUtility.addCookie(res, cookie, false);
				getSessionBean().getValues().put(BBBCoreConstants.SELECTED_GUIDE_TYPE, guideType);
				//fetching data from session as we just require selected vo from list of guide vos , so no need to fetch vos again as there are already present in session.
					/*NonRegistryGuideVO nonRegistryVO = getCheckListManager().getNonRegistryGuide (subTypeCode, BBBCoreConstants.GUIDE);*/
					List<NonRegistryGuideVO> nonRegistryGuideVOs = (List<NonRegistryGuideVO>)getSessionBean().getValues().get(BBBGiftRegistryConstants.GUIDE_VO_LIST);
					/*if (BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
						nonRegistryGuideVOs = new ArrayList<NonRegistryGuideVO>();
					}
					nonRegistryGuideVOs.add(0, nonRegistryVO);
					nonRegistryGuideVOs = getCheckListManager().removeDuplicateNonRegVO(nonRegistryGuideVOs);
					getSessionBean().getValues().put(BBBGiftRegistryConstants.GUIDE_VO_LIST, nonRegistryGuideVOs);*/
					for (int iterate = 0; iterate < nonRegistryGuideVOs.size(); iterate++) {
						if (nonRegistryGuideVOs.get(iterate).getGuideTypeCode().equalsIgnoreCase(guideType)) {
							req.setParameter(BBBCoreConstants.SELECTED_GUIDE_VO, nonRegistryGuideVOs.get(iterate));
							break;
						}
						
					}
					getSessionBean().setActivateGuideInRegistryRibbon(true);
					this.setNonRegistryGuideVOs(nonRegistryGuideVOs);
					this.setActivateGuideInRegistryRibbon(true);
					this.setSelectedGuideType(guideType);
					//req.setParameter(BBBCoreConstants.SELECTED_GUIDE_VO, nonRegistryVO);
			}
		}
		String channel = BBBUtility.getOriginOfTraffic();
		if (!BBBUtility.isEmpty(subTypeCode)) {
			
			if (regSummaryVO != null) {
				getSessionBean().getValues().put(
						BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
						regSummaryVO);
				if (fromAjax && getSessionBean().isActivateGuideInRegistryRibbon()) {
//					getCheckListManager().resetGuideContext(getSessionBean());
					getSessionBean().setActivateGuideInRegistryRibbon(false);
				}
			}
				req.setParameter(
						BBBCoreConstants.CHANNEL, channel);
			
			// Adding the query param in the json string
			String jsonString = new StringBuilder(
					BBBCoreConstants.ARRAY_SEPARETORSTART)
					.append(BBBCoreConstants.ARRAY_SEPARETOR_COLON)
					.append(subTypeCode)
					.append(BBBCoreConstants.ARRAY_SEPARETOR_COLON)
					.append(BBBCoreConstants.ARRAY_SEPARETOR).toString();

			CMSResponseVO responseVO = null;
			// Response based on channel, site and registry type
			try {
				responseVO = getContentTemplateManager().getContent(
						BBBCoreConstants.MANAGE_CHECKLIST_LINK, jsonString);

			} catch (RepositoryException repException) {
				logError("Repository Exception ManageRegistryChecklistDroplet" +
						repException.getMessage());
				logDebug("Repository Exception ManageRegistryChecklistDroplet Stack Trace:"+repException);

			} catch (BBBSystemException e) {
				logError("BBBSystem Exception ManageRegistryChecklistDroplet" +e.getMessage());
				logDebug("BBBSystem Exception ManageRegistryChecklistDroplet Stack Trace:"+e);
			} catch (BBBBusinessException e) {
				logError(
						"BBBBusiness Exception ManageRegistryChecklistDroplet" +e.getMessage());
				logDebug("BBBBusiness Exception ManageRegistryChecklistDroplet Stack Trace:"+e);
			}
			ManageRegistryChecklistVO manageRegistryChecklistVO = new ManageRegistryChecklistVO();
			manageRegistryChecklistVO.setRegistryType(subTypeCode);
			List<BannerVO> banners = new ArrayList<BannerVO>();
			if (null != responseVO && null != responseVO.getResponseItems()) {
				for (RepositoryItem item : responseVO.getResponseItems()) {
					List<RepositoryItem> links = (List<RepositoryItem>) item
							.getPropertyValue(BBBCoreConstants.LINKS);
					for (RepositoryItem link : links) {
						BannerVO banner = new BannerVO();
						if(null !=link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_LINK)){
						banner.setBannerLink(((String) link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_LINK)).trim());
						}
						if(null !=link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_TEXT)){
						banner.setBannerText(((String) link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_TEXT)).trim());
						}
						banners.add(banner);
					}
				}
			}
			manageRegistryChecklistVO.setLinks(banners);
			req.setParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK,
					manageRegistryChecklistVO);

			req.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO,
					regSummaryVO);
			
			req.serviceParameter(BBBCmsConstants.OUTPUT, req, res);

		} else {
			req.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, req, res);
		}
		BBBPerformanceMonitor.end(
				BBBPerformanceConstants.MANAGE_REGISTRY_CHECKLIST,
				BBBCoreConstants.DROPLET_MANAGE_REGSITRY_CHECKLIST);
	}

	/**
	 * Used in mobile to get the manage registry ribbon dropdown
	 * 
	 * @return ManageRegistryChecklistVO
	 */
	public ManageRegistryChecklistVO getManageRegistryRibbon(String registryId,String guideType,String fromAjax) {
		logDebug("ManageRegistryChecklistDroplet : getManageRegistryRibbon() method with input parameters registryId:"+ registryId + " guideType:" + guideType + " fromAjax:" + fromAjax);
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		request.setParameter(BBBCoreConstants.REGISTRY_ID, registryId);
		request.setParameter(BBBCoreConstants.GUIDE_TYPE, guideType);
		request.setParameter(BBBCoreConstants.FROM_AJAX, fromAjax);
		ManageRegistryChecklistVO manageRegistryChecklistVO = null;
		try {
			logDebug("Calling the service method of ManageRegistryChecklistDroplet");
			service(ServletUtil.getCurrentRequest(),
					ServletUtil.getCurrentResponse());
			manageRegistryChecklistVO = (ManageRegistryChecklistVO) request
					.getObjectParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK);
			manageRegistryChecklistVO.setActivateGuideInRegistryRibbon(this.isActivateGuideInRegistryRibbon());
			manageRegistryChecklistVO.setNonRegistryGuideVOs(this.getNonRegistryGuideVOs());
			manageRegistryChecklistVO.setSelectedGuideType(this.getSelectedGuideType());
			logDebug("Completed setting the ManageRegistryChecklistVO for mobile");
		} catch (ServletException e) {
			logError("ManageRegistryChecklistDroplet:ServletException: " +e.getMessage());
			logDebug("ManageRegistryChecklistDroplet:ServletException Stack Trace:"+e);
		} catch (IOException e) {
			logError("ManageRegistryChecklistDroplet:IOException: " + e.getMessage());
			logDebug("ManageRegistryChecklistDroplet:IOException Stack Trace:"+e);
		}
		return manageRegistryChecklistVO;
	}
	
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public GiftRegistryManager getGiftRegistryManager() {
		return giftRegistryManager;
	}

	public void setGiftRegistryManager(GiftRegistryManager giftRegistryManager) {
		this.giftRegistryManager = giftRegistryManager;
	}

	public ContentTemplateManager getContentTemplateManager() {
		return contentTemplateManager;
	}

	public void setContentTemplateManager(
			ContentTemplateManager contentTemplateManager) {
		this.contentTemplateManager = contentTemplateManager;
	}

	/**
	 * @return the checkListManager
	 */
	public CheckListManager getCheckListManager() {
		return checkListManager;
	}

	/**
	 * @param checkListManager the checkListManager to set
	 */
	public void setCheckListManager(CheckListManager checkListManager) {
		this.checkListManager = checkListManager;
	}

	public List<NonRegistryGuideVO> getNonRegistryGuideVOs() {
		return nonRegistryGuideVOs;
	}

	public void setNonRegistryGuideVOs(List<NonRegistryGuideVO> nonRegistryGuideVOs) {
		this.nonRegistryGuideVOs = nonRegistryGuideVOs;
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
	
	
}
