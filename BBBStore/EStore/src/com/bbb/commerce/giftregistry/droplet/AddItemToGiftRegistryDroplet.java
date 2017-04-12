package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;

/**
 * The class is the extension of BBBPresentationDroplet which is again extension
 * og the ATG DynamoServlet. The class is responsible for rendering the content
 * for Gift Registry Flyouts in reg_flyout.jsp. The class presents content based
 * on whether the user is authenticated and number of registries the user owns
 * 
 * @author sku134
 * 
 */
public class AddItemToGiftRegistryDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;
	
	private static final String SITE_ID = "siteId";

	/**
	 * Fetch Registry Types for the dropdown to select a registry type and
	 * create a registry.
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
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
	    BBBPerformanceMonitor.start("AddItemToGiftRegistryDroplet", "service");
		logDebug(" AddItemToGiftRegistryDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		String siteId = pRequest.getParameter(SITE_ID);
		
		
		try {
				Profile profile = (Profile) pRequest
						.resolveName(BBBCoreConstants.ATG_PROFILE);

				if (!profile.isTransient()) {
					
					final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
							.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
					List<RegistrySkinnyVO> registrySkinnyVOList = null;


					if(sessionBean.getValues().get("registrySkinnyVOList")==null)
					{
						registrySkinnyVOList = getGiftRegistryManager()
								.getAcceptableGiftRegistries(profile, siteId);
						
						sessionBean.getValues().put("registrySkinnyVOList",
								registrySkinnyVOList);
					}else{
						registrySkinnyVOList = (List<RegistrySkinnyVO>)sessionBean.getValues().get("registrySkinnyVOList");
					}
					final List<RegistrySkinnyVO> regListWithEventDate = new ArrayList<RegistrySkinnyVO>();
					final List<RegistrySkinnyVO> regListWithoutEventDate = new ArrayList<RegistrySkinnyVO>();
					
					if(registrySkinnyVOList!=null){
						for(RegistrySkinnyVO  registrySkinnyVO:registrySkinnyVOList){
							
							if(null!=registrySkinnyVO){
								
								if(registrySkinnyVO.getEventDate()!=null){
									regListWithEventDate.add(registrySkinnyVO);
								}else{
									regListWithoutEventDate.add(registrySkinnyVO);
								}
								
							}
							
						}
					}
			
					// GFT-1108 : Social Recom: User can only recommend one product per invitation
					if (sessionBean.getRegistrySummaryVO() == null
							&& !sessionBean.isRecommRegistriesPopulated()) {
						List<RegistrySummaryVO> recommendRegistryList = getGiftRegistryManager()
								.recommendRegistryList(
										profile.getRepositoryId());
						if (null != recommendRegistryList) {
							sessionBean.setRegistrySummaryVO(recommendRegistryList);
						}
						sessionBean.setRecommRegistriesPopulated(true);
					}
					RegistryEventDateComparator eventDateComparator = new RegistryEventDateComparator();
					eventDateComparator.setSortOrder(2);
					Collections.sort(regListWithEventDate, eventDateComparator);
					if(registrySkinnyVOList!=null){
						registrySkinnyVOList.clear();
						registrySkinnyVOList.addAll(regListWithEventDate);
						registrySkinnyVOList.addAll(0, regListWithoutEventDate);					
					}
					if(sessionBean.getValues().get("registrySkinnyVOList")==null)
					{
						sessionBean.getValues().put("registrySkinnyVOList",
								registrySkinnyVOList);
					}
					pRequest.setParameter("registrySkinnyVOList",
							registrySkinnyVOList);
					if(registrySkinnyVOList!=null){
						pRequest.setParameter("size", registrySkinnyVOList.size());
					}

				}
				logDebug("set registrySkinnyVOList  output to the display page");
				pRequest.serviceLocalParameter("output", pRequest, pResponse);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException from service of AddItemToGiftRegistryDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_biz_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException from service of AddItemToGiftRegistryDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_sys_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Repository Exception from service of AddItemToGiftRegistryDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1001), e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_repo_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}
		logDebug(" AddItemToGiftRegistryDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends");
		BBBPerformanceMonitor.end("AddItemToGiftRegistryDroplet", "service");
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
	public void setGiftRegistryManager(GiftRegistryManager pGiftRegistryManager) {
		mGiftRegistryManager = pGiftRegistryManager;
	}

	/**
	 * Gets the catalog tools.
	 * 
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 * 
	 * @param pCatalogTools
	 *            the new catalog tools
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	/**
	 * This method is used to get all registry list for profile
	 * @throws IOException
	 * @throws ServletException
	 */
	public List<RegistrySkinnyVO> getProfileRegistryList() throws Exception {
		List<RegistrySkinnyVO> profileRegistryList =new ArrayList<RegistrySkinnyVO>(); 
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
	    BBBPerformanceMonitor.start("AddItemToGiftRegistryDroplet", "getProfileRegistryList");
		logDebug(" AddItemToGiftRegistryDroplet getProfileRegistryList() - start");

		pRequest.setParameter(SITE_ID,SiteContextManager.getCurrentSiteId());
		service(pRequest,pResponse);
		String errMsg=pRequest.getParameter(OUTPUT_ERROR_MSG);
		
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
       
		if (StringUtils.isEmpty(errMsg) && sessionBean.getValues().get("registrySkinnyVOList") != null)
		{
			profileRegistryList=(List<RegistrySkinnyVO>)sessionBean.getValues().get("registrySkinnyVOList");
		}
		else
		{
			if (errMsg.equalsIgnoreCase("err_regsearch_biz_exception"))
			{
				throw new BBBBusinessException(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004,"BBBBusinessException");
			}
			else if(errMsg.equalsIgnoreCase("err_regsearch_repo_exception"))
			{
				throw new RepositoryException(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006,"RepositoryException");
				
			}
			else 
			{
				throw new BBBSystemException(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005,"BBBSystemException");
			}
		}
		BBBPerformanceMonitor.end("AddItemToGiftRegistryDroplet", "getProfileRegistryList end");
		return profileRegistryList;
		
	}

	
}
