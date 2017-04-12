package com.bbb.store.catalog.bvreviews;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductReviewStats {
	
	 @JsonProperty("TotalReviewCount")
	private String totalReviewCount;
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

	/**
	 * @return the totalReviewCount
	 */
	@JsonProperty("TotalReviewCount")
	public String getTotalReviewCount() {
		return totalReviewCount;
	}

	/**
	 * @param totalReviewCount the totalReviewCount to set
	 */
	 @JsonProperty("TotalReviewCount")
	public void setTotalReviewCount(String totalReviewCount) {
		this.totalReviewCount = totalReviewCount;
	}

}
