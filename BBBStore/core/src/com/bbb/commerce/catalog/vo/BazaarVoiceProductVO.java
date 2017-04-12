package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.bbb.constants.BBBCoreConstants;


/**
 * @author sjatas
 *
 */
public class BazaarVoiceProductVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String NOT_YET_RATED = "Not yet rated";
	private static final String MAX_LIMIT = " out of 5 stars";
	private String source;
	private String externalId;
	private String name;
	private String id;
	private String ratingsTitle;
	private boolean ratingAvailable;
	private float averageOverallRating;
	private int totalReviewCount;
	private String overallRatingRange;
	private String ratingsOnlyReviewCount;
	private String siteId;
	
	

	
	/**
	 * @return Site ID
	 */
	public final String getSiteId() {
		return this.siteId;
	}

	/**
	 * @param siteId Site Identifier
	 */
	public final void setSiteId(final String siteId) {
		this.siteId = siteId;
	}

	/**
	 * 
	 */
	public BazaarVoiceProductVO() {
		super();
	}

	/**
	 * @param pSource Source
	 * @param pExternalId External ID
	 * @param pName Name
	 * @param pId ID
	 * @param pAverageOverallRating Average Overall Rating
	 * @param pTotalReviewCount Total Review count
	 * @param pOverallRatingRange Overall Rating Change
	 * @param pRatingsOnlyReviewCount Rating Only Review Count
	 */
	public BazaarVoiceProductVO(final String pSource, final String pExternalId, final String pName, final String pId,
			final float pAverageOverallRating, final int pTotalReviewCount, final String pOverallRatingRange, 
			final String pRatingsOnlyReviewCount) {
		super();
		this.source = pSource;
		this.externalId = pExternalId;
		this.name = pName;
		this.id = pId;
		this.averageOverallRating = pAverageOverallRating;
		this.totalReviewCount = pTotalReviewCount;
		this.overallRatingRange = pOverallRatingRange;
		this.ratingsOnlyReviewCount = pRatingsOnlyReviewCount;
	}




	/**
	 * @return the source
	 */
	public final String getSource() {
		return this.source;
	}




	/**
	 * @param pSource the source to set
	 */
	public final void setSource(final String pSource) {
		this.source = pSource;
	}




	/**
	 * @return the externalId
	 */
	public final String getExternalId() {
		return this.externalId;
	}




	/**
	 * @param pExternalId the externalId to set
	 */
	public final void setExternalId(final String pExternalId) {
		this.externalId = pExternalId;
	}




	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}




	/**
	 * @param pName the name to set
	 */
	public final void setName(final String pName) {
		this.name = pName;
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return this.id;
	}

	/**
	 * @param pId the id to set
	 */
	public final void setId(final String pId) {
		this.id = pId;
	}

    /**
     * @return Average Overall Rating
     */
    public final float getAverageOverallRating() {
	
		final BigDecimal ratingAverage = new BigDecimal(this.averageOverallRating).setScale(1, RoundingMode.HALF_EVEN);
		this.averageOverallRating = ratingAverage.floatValue();
		return this.averageOverallRating;
		
	}

	/**
	 * @param pAverageOverallRating the averageOverallRating to set
	 */
	public final void setAverageOverallRating(final float pAverageOverallRating) {
		this.averageOverallRating = pAverageOverallRating;
	}

	/**
	 * @return the totalReviewCount
	 */
	public final int getTotalReviewCount() {
		return this.totalReviewCount;
	}

	/**
	 * @param pTotalReviewCount the totalReviewCount to set
	 */
	public final void setTotalReviewCount(final int pTotalReviewCount) {
		this.totalReviewCount = pTotalReviewCount;
	}

	/**
	 * @return the overallRatingRange
	 */
	public final String getOverallRatingRange() {
		return this.overallRatingRange;
	}

	/**
	 * @param pOverallRatingRange the overallRatingRange to set
	 */
	public final void setOverallRatingRange(final String pOverallRatingRange) {
		this.overallRatingRange = pOverallRatingRange;
	}

	/**
	 * @return the ratingsOnlyReviewCount
	 */
	public final String getRatingsOnlyReviewCount() {
		return this.ratingsOnlyReviewCount;
	}

	/**
	 * @param pRatingsOnlyReviewCount the ratingsOnlyReviewCount to set
	 */
	public final void setRatingsOnlyReviewCount(final String pRatingsOnlyReviewCount) {
		this.ratingsOnlyReviewCount = pRatingsOnlyReviewCount;
	}

	/**
	 * @return Rating Availability
	 */
	public final boolean isRatingAvailable() {
		return this.ratingAvailable;
	}
	
	/**
	 * @param ratingAvailable Rating Availability
	 */
	public final void setRatingAvailable(final boolean ratingAvailable) {
		this.ratingAvailable = ratingAvailable;
	}
	
	/**
	 * @param ratingsTitle
	 */
	public final void setRatingsTitle(final String ratingsTitle) {
		this.ratingsTitle = ratingsTitle;
	}
	
		
	/**
	 * @return the ratingsTitle
	 */
	public final String getRatingsTitle() {
		final float avg_rating = getAverageOverallRating();
		if (avg_rating <= 0) {
			ratingsTitle = NOT_YET_RATED;
		} else {
			int remainder = ((int) (avg_rating * BBBCoreConstants.TEN)) % BBBCoreConstants.TEN;
			int rating;
			if (remainder == 0) {
				rating = (((int) (avg_rating * BBBCoreConstants.TEN)) / BBBCoreConstants.TEN);
				ratingsTitle = ("" + rating) + MAX_LIMIT;
			} else {
				ratingsTitle = (avg_rating) + MAX_LIMIT;
			}
		}
		return this.ratingsTitle;
	}

}


