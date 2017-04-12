package com.bbb.cms.droplet;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.LandingTemplateVO;
import com.bbb.cms.manager.LandingTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
/**
 * LandingTemplateDroplet retrieves the data from LandingRepository
 * @author rakara
 *
 */
public class LandingTemplateDroplet extends BBBDynamoServlet {

	private static final String COLLEGE_LANDING_PAGE = "CollegeLandingPage";

	private LandingTemplateManager mLandingTemplateManager = null;

	private BBBCatalogTools catalogTools = null;
	private static final String NOT_REQUIRED = "NotRequired";
	private static final String REGISTRY_LANDING_PAGE = "RegistryLandingPage";
	private static final String REGISTRY_BABYCA_LANDING_PAGE = "RegistryBabyCALandingPage";
	

	/**
	 * @return the landingTemplateManager
	 */
	public LandingTemplateManager getLandingTemplateManager() {
		return this.mLandingTemplateManager;
	}

	/**
	 * @param pLandingTemplateManager the landingTemplateManager to set
	 */
	public void setLandingTemplateManager(final LandingTemplateManager pLandingTemplateManager) {
		this.mLandingTemplateManager = pLandingTemplateManager;
	}



	/**
	 * Return CatalogTools
	 * @return
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * Setting CatalogTools
	 * @param catalogTools
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * This method gets the value of site id from the page and fetches the states
	 * greater than the current date and site id.
	 */
	@Override
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		
		this.logDebug("starting method LandingTemplateDroplet");
		

		final String LAND_PAGENAME = "pageName";
		final String SITE_ID = "siteId";
		final String EMPTY = "empty";
		final String LANDING_OUTPUT = "output";
		final String LAND_TEMP_VO = "LandingTemplateVO";
		final String COLLEGE_CATEGORIES="collegeCategories";

		String pageName =null;
		String categoryId =null;
		String siteId = null;

		if(request.getLocalParameter(LAND_PAGENAME) !=null){
			pageName = (String)request.getLocalParameter(LAND_PAGENAME);
		}


		
		this.logDebug("Input Paramters : "+pageName);
		


		if(request.getLocalParameter(SITE_ID) !=null){
			siteId = (String)request.getLocalParameter(SITE_ID);
		}else{
			siteId = SiteContextManager.getCurrentSiteId();
		}

		//Request College sub-categories
		try{
			String rootCollegeId = null;
			if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId) || BBBCoreConstants.SITE_BAB_US.equalsIgnoreCase(siteId)
					|| TBSConstants.SITE_TBS_BAB_CA.equalsIgnoreCase(siteId) || TBSConstants.SITE_TBS_BAB_US.equalsIgnoreCase(siteId)) {
				rootCollegeId = this.getCatalogTools().getRootCollegeIdFrmConfig(siteId);
			}

			
			this.logDebug("Calling getCategoryDetail for Category : "+rootCollegeId+" and site:"+siteId);
			

			if( REGISTRY_LANDING_PAGE.equals(pageName) || COLLEGE_LANDING_PAGE.equals(pageName) || REGISTRY_BABYCA_LANDING_PAGE.equals(pageName)) {
				
				this.logDebug("Request is coming from RegistryLandingPage or CollegeLandingPage : No Category Required");
				
				categoryId = NOT_REQUIRED;
			}
				if(!StringUtils.isEmpty(rootCollegeId)){
				CategoryVO collegeCategory = this.getCatalogTools().getCategoryDetail(siteId, rootCollegeId,false);
				if (pageName.equals(COLLEGE_LANDING_PAGE) && (collegeCategory != null)) {
					collegeCategory = this.getCatalogTools().getSortedCollegeCategory(collegeCategory);
				}
				request.setParameter(COLLEGE_CATEGORIES, collegeCategory);

				}else{

					
					this.logDebug("CollegeCategory is not defined ");
					
					request.serviceParameter(EMPTY, request, response);

				}

		}catch(final BBBBusinessException be){
			
			this.logError(LogMessageFormatter.formatMessage(request, "LandingTemplateDroplet|service()|BBBBusinessException","catalog_1044"),be);

			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		} catch(final BBBSystemException bs){
			
			this.logError(LogMessageFormatter.formatMessage(request, "LandingTemplateDroplet|service()|BBBSystemException","catalog_1045"),bs);

			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		}

		if(StringUtils.isEmpty(pageName)) {
			request.serviceParameter(EMPTY, request, response);
		} else{
			
			this.logDebug("Calling LandingTemplateManager : ");
			
			final LandingTemplateVO landingTemplateVO=this.getLandingTemplateManager().getLandingTemplateData(pageName, categoryId,siteId);
			if(landingTemplateVO != null){
				
				this.logDebug("Received LandingTemplateVO : "+landingTemplateVO);
				
				request.setParameter(LAND_TEMP_VO, landingTemplateVO);
				request.serviceParameter(LANDING_OUTPUT, request, response);
			} else{
				
				this.logDebug("Received LandingTemplateVO as Null: ");
				
				request.serviceParameter(EMPTY, request, response);
			}
		}

		
		this.logDebug("Existing method LandingTemplateDroplet");
	

	}
}