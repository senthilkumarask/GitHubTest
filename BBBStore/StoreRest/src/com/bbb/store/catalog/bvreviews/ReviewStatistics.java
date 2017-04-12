package com.bbb.store.catalog.bvreviews;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewStatistics {
	
	@JsonProperty("AverageOverallRating")
	private double averageOverallRating;

	/**
	 * @return the averageOverallRating
	 */
	@JsonProperty("AverageOverallRating")
	public double getAverageOverallRating() {
		return averageOverallRating;
	}

	/**
	 * @param averageOverallRating the averageOverallRating to set
	 */
	@JsonProperty("AverageOverallRating")
	public void setAverageOverallRating(double averageOverallRating) {
		this.averageOverallRating = averageOverallRating;
	}

}
