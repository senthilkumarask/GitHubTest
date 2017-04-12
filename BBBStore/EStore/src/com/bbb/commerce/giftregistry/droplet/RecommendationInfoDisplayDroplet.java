package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.RecommendationRegistryProductVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class RecommendationInfoDisplayDroplet extends BBBPresentationDroplet {


	private static final String DEFAULT_SORT_OPTION = "recommender";
	GiftRegistryRecommendationManager giftRegistryRecommendationManager;
	private static String EVENT_TYPE_CODE = "eventTypeCode";

	public GiftRegistryRecommendationManager getGiftRegistryRecommendationManager() {
		return giftRegistryRecommendationManager;
	}

	public void setGiftRegistryRecommendationManager(
			GiftRegistryRecommendationManager giftRegistryRecommendationManager) {
		this.giftRegistryRecommendationManager = giftRegistryRecommendationManager;
	}

	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {

		logDebug("Inside the RecommendationInfoDisplayDroplet");

		String registryId = req.getParameter(BBBGiftRegistryConstants.REGISTRY_ID);
		String tabId = req.getParameter(BBBGiftRegistryConstants.TAB_ID);
		String sortOption = req.getParameter(BBBGiftRegistryConstants.SORT_OPTION);
		String pPageSize = req.getParameter(BBBCoreConstants.PAGESIZE);
		String pPageNum = req.getParameter(BBBCoreConstants.PAGENUMBER);
		String eventTypeCode = req.getParameter(EVENT_TYPE_CODE);
		String fromViewRegistryOwner = req.getParameter("fromViewRegistryOwner");
		String fromRecommenderTab = req.getParameter("fromRecommenderTab");
		if(fromViewRegistryOwner == null){
			fromViewRegistryOwner = BBBCoreConstants.FALSE;
		}
		if(fromRecommenderTab == null){
			fromRecommenderTab = BBBCoreConstants.FALSE;
		}
		logDebug("RegistryId= " +registryId +"tabId= " + tabId+ "sortOption= " + sortOption+
				"PageSize= " + pPageSize+ "pageNum" +pPageNum+"eventTypeCode:"+eventTypeCode +
				"fromViewRegistryOwner" + fromViewRegistryOwner);
		if(fromViewRegistryOwner.equalsIgnoreCase(BBBCoreConstants.TRUE)){
			int[] badgeCount = getBadgeCount(registryId);
			req.setParameter("recommendationCount", badgeCount[0]);
			req.setParameter("recommendationProductSize", badgeCount[1]);
			req.serviceLocalParameter(OPARAM_OUTPUT, req, res);
		} else {
			try {
				List<RecommendationRegistryProductVO> recommendationProduct =
						getRecommendationTabDetails(registryId, tabId, sortOption,eventTypeCode,req);
				req.setParameter("recommendationProduct", recommendationProduct);
				

				if(fromRecommenderTab.equalsIgnoreCase(BBBCoreConstants.TRUE)){
					int emailOptInValue = getGiftRegistryRecommendationManager().getEmailOptInValue(registryId);
					req.setParameter("emailOptInValue", emailOptInValue);
				}
				if (recommendationProduct.size() > 0) {
					req.serviceLocalParameter(OPARAM_OUTPUT, req, res);
				} else {
					req.serviceLocalParameter(OPARAM_ERROR, req, res);
				}
			} catch (BBBBusinessException e) {
				//Cannot do anything with error - just logging it
				logError("Error fetching recommendation for recommender tab",e);
			} catch (BBBSystemException e) {
				//Cannot do anything with error - just logging it
				logError("Error fetching recommendation for recommender tab",e);
			}
		}
	}

	private List<RecommendationRegistryProductVO> getRecommendationTabDetails(String registryId, String tabId,
			String sortOption,String eventTypeCode, DynamoHttpServletRequest req)
					throws BBBBusinessException, BBBSystemException {
		if(BBBUtility.isEmpty(sortOption) && !tabId.equalsIgnoreCase(BBBGiftRegistryConstants.RECOMMENDER_TAB)){
			sortOption = DEFAULT_SORT_OPTION;
		}else if(tabId.equalsIgnoreCase(BBBGiftRegistryConstants.RECOMMENDER_TAB)){
			sortOption = "";
		}
		List<RecommendationRegistryProductVO> recommendationProduct = getGiftRegistryRecommendationManager().
				getRegistryRecommendationItemsForTab(registryId, tabId, sortOption,eventTypeCode,req);
		return recommendationProduct;
	}

	public int[] getBadgeCount(String registryId) {
		int[] recommendationCount = new int[2];
		recommendationCount = getGiftRegistryRecommendationManager().getGiftRegistryTools().getRecommendationCount(registryId);
		return recommendationCount;
	}
	
	

}
