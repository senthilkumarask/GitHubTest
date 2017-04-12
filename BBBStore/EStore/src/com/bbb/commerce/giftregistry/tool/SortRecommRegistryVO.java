package com.bbb.commerce.giftregistry.tool;

import java.util.List;
import java.util.Map;


/**
 *
 * VO used for two purposes.
 * It is used to populate products information when the tabs are Pending, Declined or Accepted.
 * VO is used to populate Recommenders information when the tab is Recommenders.
 *
 * @author schint
 *
 */
public class SortRecommRegistryVO{
	/**
	 *
	 */
	
	private Map<String, List<RecommendationRegistryProductVO>> categoryBucketsForRecommendation;
	
	public Map<String, List<RecommendationRegistryProductVO>> getCategoryBucketsForRecommendation() {
		return categoryBucketsForRecommendation;
	}
	public void setCategoryBucketsForRecommendation(
			Map<String, List<RecommendationRegistryProductVO>> categoryBucketsForRecommendation) {
		this.categoryBucketsForRecommendation = categoryBucketsForRecommendation;
	}
	String groupByFlag ;
	
	public String getGroupByFlag() {
		return groupByFlag;
	}
	public void setGroupByFlag(String groupByFlag) {
		this.groupByFlag = groupByFlag;
	}
	
}
