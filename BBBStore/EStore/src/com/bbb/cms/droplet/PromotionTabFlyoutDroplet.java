package com.bbb.cms.droplet;

import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.PromoBoxContentVO;
import com.bbb.cms.manager.LandingTemplateManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.logging.LogMessageFormatter;

public class PromotionTabFlyoutDroplet extends BBBDynamoServlet{
	
	private LandingTemplateManager mLandingTemplateManager = null;
	
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
	 * This method gets the value of site id from the page and fetches the states
	 * greater than the current date and site id.
	 */
	@Override
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {
		
		final String PROMOTION_TAB_ID = "promoTabId";
	    final String PROMO_BOX_CONTENT_VO = "promoBoxContentVO";
	    
	    String promoTabId= null;
	    
	    if(request.getParameter(PROMOTION_TAB_ID) !=null){
			promoTabId = (String)request.getParameter(PROMOTION_TAB_ID);
		}
	    
		if(!StringUtils.isEmpty(promoTabId)) {
			String promotionTabId = promoTabId.split(BBBCoreConstants.UNDERSCORE)[0];
			PromoBoxContentVO promoBoxContentVO = null;
			try {
				promoBoxContentVO = this.getLandingTemplateManager().getPromotionTab(promotionTabId);
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				this.logError(LogMessageFormatter.formatMessage(request, "LandingTemplateDroplet|service()|RepositoryException"),e);

				request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
			}
			request.setParameter(PROMO_BOX_CONTENT_VO, promoBoxContentVO);
			request.serviceParameter("output", request, response);
		}
	}
	

}
