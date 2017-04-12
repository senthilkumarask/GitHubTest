package com.bbb.store.catalog.bvreviews;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Products {
	
	@JsonProperty("TotalReviewCount")
	private int totalReviewCount;

	/**
	 * @return the totalReviewCount
	 */
	@JsonProperty("TotalReviewCount")
	public int getTotalReviewCount() {
		return totalReviewCount;
	}

	/**
	 * @param totalReviewCount the totalReviewCount to set
	 */
	@JsonProperty("TotalReviewCount")
	public void setTotalReviewCount(int totalReviewCount) {
		this.totalReviewCount = totalReviewCount;
	}
	
	@JsonProperty("ReviewStatistics")
	private ReviewStatistics reviewStatistics;

	/**
	 * @return the reviewStatistics
	 */
	@JsonProperty("ReviewStatistics")
	public ReviewStatistics getReviewStatistics() {
		return reviewStatistics;
	}

	/**
	 * @param reviewStatistics the reviewStatistics to set
	 */
	@JsonProperty("ReviewStatistics")
	public void setReviewStatistics(ReviewStatistics reviewStatistics) {
		this.reviewStatistics = reviewStatistics;
	}

	
}
