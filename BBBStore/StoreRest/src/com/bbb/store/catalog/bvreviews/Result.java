
package com.bbb.store.catalog.bvreviews;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Result {

	/*@JsonProperty("TagDimensions")
    private TagDimensions tagDimensions;
    @JsonProperty("TagDimensionsOrder")
    private List<Object> tagDimensionsOrder = new ArrayList<Object>();
    @JsonProperty("AdditionalFieldsOrder")
    private List<Object> additionalFieldsOrder = new ArrayList<Object>();*/
    /*@JsonProperty("Cons")
    private Object cons;*/
    @JsonProperty("IsRecommended")
    private Boolean isRecommended;
    @JsonProperty("IsRatingsOnly")
    private Boolean isRatingsOnly;
    @JsonProperty("UserNickname")
    private String userNickname;
    /*@JsonProperty("Pros")
    private Object pros;*/
    /*@JsonProperty("Photos")
    private List<Object> photos = new ArrayList<Object>();
    @JsonProperty("ContextDataValues")
    private ContextDataValues contextDataValues;
    @JsonProperty("Videos")
    private List<Object> videos = new ArrayList<Object>();
    @JsonProperty("ContextDataValuesOrder")
    private List<Object> contextDataValuesOrder = new ArrayList<Object>();*/
    @JsonProperty("LastModificationTime")
    private String lastModificationTime;
   /* @JsonProperty("SubmissionId")
    private String submissionId;*/
  /*  @JsonProperty("TotalFeedbackCount")
    private Long totalFeedbackCount;*/
    /*@JsonProperty("TotalPositiveFeedbackCount")
    private Long totalPositiveFeedbackCount;
    @JsonProperty("BadgesOrder")
    private List<Object> badgesOrder = new ArrayList<Object>();*/
    @JsonProperty("UserLocation")
    private String userLocation;
    /*@JsonProperty("Badges")
    private Badges badges;*/
    /*@JsonProperty("AuthorId")
    private String authorId;*/
    /*@JsonProperty("SecondaryRatingsOrder")
    private List<Object> secondaryRatingsOrder = new ArrayList<Object>();
    @JsonProperty("IsFeatured")
    private Boolean isFeatured;
    @JsonProperty("IsSyndicated")
    private Boolean isSyndicated;
    @JsonProperty("ProductRecommendationIds")
    private List<Object> productRecommendationIds = new ArrayList<Object>();*/
    @JsonProperty("Title")
    private String title;
    @JsonProperty("ProductId")
    private String productId;
    /*@JsonProperty("AdditionalFields")
    private AdditionalFields additionalFields;
    @JsonProperty("Helpfulness")
    private Double helpfulness;
    @JsonProperty("CampaignId")
    private String campaignId;
    @JsonProperty("TotalNegativeFeedbackCount")
    private Long totalNegativeFeedbackCount;*/
    @JsonProperty("SubmissionTime")
    private String submissionTime;
    @JsonProperty("Rating")
    private Long rating;
    /*@JsonProperty("ContentLocale")
    private String contentLocale;*/
   /* @JsonProperty("RatingRange")
    private Long ratingRange;
    @JsonProperty("TotalCommentCount")
    private Long totalCommentCount;*/
    @JsonProperty("ReviewText")
    private String reviewText;
    /*@JsonProperty("ModerationStatus")
    private String moderationStatus;
    @JsonProperty("ClientResponses")
    private List<Object> clientResponses = new ArrayList<Object>();
    @JsonProperty("Id")
    private String id;
    @JsonProperty("SecondaryRatings")
    private SecondaryRatings secondaryRatings;
    @JsonProperty("CommentIds")
    private List<Object> commentIds = new ArrayList<Object>();
    @JsonProperty("LastModeratedTime")
    private String lastModeratedTime;*/
	/**
	 * @return the tagDimensions
	 *//*
    @JsonProperty("TagDimensions")
	public TagDimensions getTagDimensions() {
		return tagDimensions;
	}
	*//**
	 * @param tagDimensions the tagDimensions to set
	 *//*
    @JsonProperty("TagDimensions")
	public void setTagDimensions(TagDimensions tagDimensions) {
		this.tagDimensions = tagDimensions;
	}
	*//**
	 * @return the tagDimensionsOrder
	 *//*
    @JsonProperty("TagDimensionsOrder")
	public List<Object> getTagDimensionsOrder() {
		return tagDimensionsOrder;
	}
	*//**
	 * @param tagDimensionsOrder the tagDimensionsOrder to set
	 *//*
    @JsonProperty("TagDimensionsOrder")
	public void setTagDimensionsOrder(List<Object> tagDimensionsOrder) {
		this.tagDimensionsOrder = tagDimensionsOrder;
	}
	*//**
	 * @return the additionalFieldsOrder
	 *//*
    @JsonProperty("AdditionalFieldsOrder")
	public List<Object> getAdditionalFieldsOrder() {
		return additionalFieldsOrder;
	}
	*//**
	 * @param additionalFieldsOrder the additionalFieldsOrder to set
	 *//*
    @JsonProperty("AdditionalFieldsOrder")
	public void setAdditionalFieldsOrder(List<Object> additionalFieldsOrder) {
		this.additionalFieldsOrder = additionalFieldsOrder;
	}*/
	/**
	 * @return the cons
	 */
    /*@JsonProperty("Cons")
	public Object getCons() {
		return cons;
	}
	*//**
	 * @param cons the cons to set
	 *//*
    @JsonProperty("Cons")
	public void setCons(Object cons) {
		this.cons = cons;
	}*/
	/**
	 * @return the isRecommended
	 */
    @JsonProperty("IsRecommended")
	public Boolean getIsRecommended() {
		return isRecommended;
	}
	/**
	 * @param isRecommended the isRecommended to set
	 */
    @JsonProperty("IsRecommended")
	public void setIsRecommended(Boolean isRecommended) {
		this.isRecommended = isRecommended;
	}
	/**
	 * @return the isRatingsOnly
	 */
    @JsonProperty("IsRatingsOnly")
	public Boolean getIsRatingsOnly() {
		return isRatingsOnly;
	}
	/**
	 * @param isRatingsOnly the isRatingsOnly to set
	 */
    @JsonProperty("IsRatingsOnly")
	public void setIsRatingsOnly(Boolean isRatingsOnly) {
		this.isRatingsOnly = isRatingsOnly;
	}
	/**
	 * @return the userNickname
	 */
    @JsonProperty("UserNickname")
	public String getUserNickname() {
		return userNickname;
	}
	/**
	 * @param userNickname the userNickname to set
	 */
    @JsonProperty("UserNickname")
	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}
	/**
	 * @return the pros
	 *//*
    @JsonProperty("Pros")
	public Object getPros() {
		return pros;
	}
	*//**
	 * @param pros the pros to set
	 *//*
    @JsonProperty("Pros")
	public void setPros(Object pros) {
		this.pros = pros;
	}*/
	/**
	 * @return the photos
	 */
    /*@JsonProperty("Photos")
	public List<Object> getPhotos() {
		return photos;
	}
	*//**
	 * @param photos the photos to set
	 *//*
    @JsonProperty("Photos")
	public void setPhotos(List<Object> photos) {
		this.photos = photos;
	}
	*//**
	 * @return the contextDataValues
	 *//*
    @JsonProperty("ContextDataValues")
	public ContextDataValues getContextDataValues() {
		return contextDataValues;
	}
	*//**
	 * @param contextDataValues the contextDataValues to set
	 *//*
    @JsonProperty("ContextDataValues")
	public void setContextDataValues(ContextDataValues contextDataValues) {
		this.contextDataValues = contextDataValues;
	}
	*//**
	 * @return the videos
	 *//*
    @JsonProperty("Videos")
	public List<Object> getVideos() {
		return videos;
	}
	*//**
	 * @param videos the videos to set
	 *//*
    @JsonProperty("Videos")
	public void setVideos(List<Object> videos) {
		this.videos = videos;
	}
	*//**
	 * @return the contextDataValuesOrder
	 *//*
    @JsonProperty("ContextDataValuesOrder")
	public List<Object> getContextDataValuesOrder() {
		return contextDataValuesOrder;
	}
	*//**
	 * @param contextDataValuesOrder the contextDataValuesOrder to set
	 *//*
    @JsonProperty("ContextDataValuesOrder")
	public void setContextDataValuesOrder(List<Object> contextDataValuesOrder) {
		this.contextDataValuesOrder = contextDataValuesOrder;
	}*/
	/**
	 * @return the lastModificationTime
	 */
    @JsonProperty("LastModificationTime")
	public String getLastModificationTime() {
		return lastModificationTime;
	}
	/**
	 * @param lastModificationTime the lastModificationTime to set
	 */
    @JsonProperty("LastModificationTime")
	public void setLastModificationTime(String lastModificationTime) {
		this.lastModificationTime = lastModificationTime;
	}
	/**
	 * @return the submissionId
	 */
    /*@JsonProperty("SubmissionId")
	public String getSubmissionId() {
		return submissionId;
	}
	*//**
	 * @param submissionId the submissionId to set
	 *//*
    @JsonProperty("SubmissionId")
	public void setSubmissionId(String submissionId) {
		this.submissionId = submissionId;
	}
	*//**
	 * @return the totalFeedbackCount
	 *//*
    @JsonProperty("TotalFeedbackCount")
	public Long getTotalFeedbackCount() {
		return totalFeedbackCount;
	}
	*//**
	 * @param totalFeedbackCount the totalFeedbackCount to set
	 *//*
    @JsonProperty("TotalFeedbackCount")
	public void setTotalFeedbackCount(Long totalFeedbackCount) {
		this.totalFeedbackCount = totalFeedbackCount;
	}*/
	/**
	 * @return the totalPositiveFeedbackCount
	 */
    /*@JsonProperty("TotalPositiveFeedbackCount")
	public Long getTotalPositiveFeedbackCount() {
		return totalPositiveFeedbackCount;
	}
	*//**
	 * @param totalPositiveFeedbackCount the totalPositiveFeedbackCount to set
	 *//*
    @JsonProperty("TotalPositiveFeedbackCount")
	public void setTotalPositiveFeedbackCount(Long totalPositiveFeedbackCount) {
		this.totalPositiveFeedbackCount = totalPositiveFeedbackCount;
	}
	*//**
	 * @return the badgesOrder
	 *//*
    @JsonProperty("BadgesOrder")
	public List<Object> getBadgesOrder() {
		return badgesOrder;
	}
	*//**
	 * @param badgesOrder the badgesOrder to set
	 *//*
    @JsonProperty("BadgesOrder")
	public void setBadgesOrder(List<Object> badgesOrder) {
		this.badgesOrder = badgesOrder;
	}*/
	/**
	 * @return the userLocation
	 */
    @JsonProperty("UserLocation")
	public String getUserLocation() {
		return userLocation;
	}
	/**
	 * @param userLocation the userLocation to set
	 */
    @JsonProperty("UserLocation")
	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}
	/**
	 * @return the badges
	 */
    /*@JsonProperty("Badges")
	public Badges getBadges() {
		return badges;
	}
	*//**
	 * @param badges the badges to set
	 *//*
    @JsonProperty("Badges")
	public void setBadges(Badges badges) {
		this.badges = badges;
	}*/
	/**
	 * @return the authorId
	 */
    /*@JsonProperty("AuthorId")
	public String getAuthorId() {
		return authorId;
	}
	*//**
	 * @param authorId the authorId to set
	 *//*
    @JsonProperty("AuthorId")
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}*/
	/**
	 * @return the secondaryRatingsOrder
	 */
    /*@JsonProperty("SecondaryRatingsOrder")
	public List<Object> getSecondaryRatingsOrder() {
		return secondaryRatingsOrder;
	}
	*//**
	 * @param secondaryRatingsOrder the secondaryRatingsOrder to set
	 *//*
    @JsonProperty("SecondaryRatingsOrder")
	public void setSecondaryRatingsOrder(List<Object> secondaryRatingsOrder) {
		this.secondaryRatingsOrder = secondaryRatingsOrder;
	}
	*//**
	 * @return the isFeatured
	 *//*
    @JsonProperty("IsFeatured")
	public Boolean getIsFeatured() {
		return isFeatured;
	}
	*//**
	 * @param isFeatured the isFeatured to set
	 *//*
    @JsonProperty("IsFeatured")
	public void setIsFeatured(Boolean isFeatured) {
		this.isFeatured = isFeatured;
	}
	*//**
	 * @return the isSyndicated
	 *//*
    @JsonProperty("IsSyndicated")
	public Boolean getIsSyndicated() {
		return isSyndicated;
	}
	*//**
	 * @param isSyndicated the isSyndicated to set
	 *//*
    @JsonProperty("IsSyndicated")
	public void setIsSyndicated(Boolean isSyndicated) {
		this.isSyndicated = isSyndicated;
	}
	*//**
	 * @return the productRecommendationIds
	 *//*
    @JsonProperty("ProductRecommendationIds")
	public List<Object> getProductRecommendationIds() {
		return productRecommendationIds;
	}
	*//**
	 * @param productRecommendationIds the productRecommendationIds to set
	 *//*
    @JsonProperty("ProductRecommendationIds")
	public void setProductRecommendationIds(List<Object> productRecommendationIds) {
		this.productRecommendationIds = productRecommendationIds;
	}*/
	/**
	 * @return the title
	 */
    @JsonProperty("Title")
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
    @JsonProperty("Title")
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the productId
	 */
    @JsonProperty("ProductId")
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
    @JsonProperty("ProductId")
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the additionalFields
	 */
    /*@JsonProperty("AdditionalFields")
	public AdditionalFields getAdditionalFields() {
		return additionalFields;
	}
	*//**
	 * @param additionalFields the additionalFields to set
	 *//*
    @JsonProperty("AdditionalFields")
	public void setAdditionalFields(AdditionalFields additionalFields) {
		this.additionalFields = additionalFields;
	}
	*//**
	 * @return the helpfulness
	 *//*
    @JsonProperty("Helpfulness")
	public Double getHelpfulness() {
		return helpfulness;
	}
	*//**
	 * @param helpfulness the helpfulness to set
	 *//*
    @JsonProperty("Helpfulness")
	public void setHelpfulness(Double helpfulness) {
		this.helpfulness = helpfulness;
	}
	*//**
	 * @return the campaignId
	 *//*
    @JsonProperty("CampaignId")
	public String getCampaignId() {
		return campaignId;
	}
	*//**
	 * @param campaignId the campaignId to set
	 *//*
    @JsonProperty("CampaignId")
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	*//**
	 * @return the totalNegativeFeedbackCount
	 *//*
    @JsonProperty("TotalNegativeFeedbackCount")
	public Long getTotalNegativeFeedbackCount() {
		return totalNegativeFeedbackCount;
	}
	*//**
	 * @param totalNegativeFeedbackCount the totalNegativeFeedbackCount to set
	 *//*
    @JsonProperty("TotalNegativeFeedbackCount")
	public void setTotalNegativeFeedbackCount(Long totalNegativeFeedbackCount) {
		this.totalNegativeFeedbackCount = totalNegativeFeedbackCount;
	}*/
	/**
	 * @return the submissionTime
	 */
    @JsonProperty("SubmissionTime")
	public String getSubmissionTime() {
		return submissionTime;
	}
	/**
	 * @param submissionTime the submissionTime to set
	 */
    @JsonProperty("SubmissionTime")
	public void setSubmissionTime(String submissionTime) {
		this.submissionTime = submissionTime;
	}
	/**
	 * @return the rating
	 */
    @JsonProperty("Rating")
	public Long getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
    @JsonProperty("Rating") 
	public void setRating(Long rating) {
		this.rating = rating;
	}
	/**
	 * @return the contentLocale
	 */
    /*@JsonProperty("ContentLocale")
	public String getContentLocale() {
		return contentLocale;
	}
	*//**
	 * @param contentLocale the contentLocale to set
	 *//*
    @JsonProperty("ContentLocale")
	public void setContentLocale(String contentLocale) {
		this.contentLocale = contentLocale;
	}*/
	/**
	 * @return the ratingRange
	 */
    /*@JsonProperty("RatingRange")
	public Long getRatingRange() {
		return ratingRange;
	}
	*//**
	 * @param ratingRange the ratingRange to set
	 *//*
    @JsonProperty("RatingRange")
	public void setRatingRange(Long ratingRange) {
		this.ratingRange = ratingRange;
	}
	*//**
	 * @return the totalCommentCount
	 *//*
    @JsonProperty("TotalCommentCount")
	public Long getTotalCommentCount() {
		return totalCommentCount;
	}
	*//**
	 * @param totalCommentCount the totalCommentCount to set
	 *//*
    @JsonProperty("TotalCommentCount")
	public void setTotalCommentCount(Long totalCommentCount) {
		this.totalCommentCount = totalCommentCount;
	}*/
	/**
	 * @return the reviewText
	 */
    @JsonProperty("ReviewText")
	public String getReviewText() {
		return reviewText;
	}
	/**
	 * @param reviewText the reviewText to set
	 */
    @JsonProperty("ReviewText")
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	/**
	 * @return the moderationStatus
	 */
   /* @JsonProperty("ModerationStatus")
	public String getModerationStatus() {
		return moderationStatus;
	}
	*//**
	 * @param moderationStatus the moderationStatus to set
	 *//*
    @JsonProperty("ModerationStatus")
	public void setModerationStatus(String moderationStatus) {
		this.moderationStatus = moderationStatus;
	}
	*//**
	 * @return the clientResponses
	 *//*
    @JsonProperty("ClientResponses")
	public List<Object> getClientResponses() {
		return clientResponses;
	}
	*//**
	 * @param clientResponses the clientResponses to set
	 *//*
    @JsonProperty("ClientResponses")
	public void setClientResponses(List<Object> clientResponses) {
		this.clientResponses = clientResponses;
	}
	*//**
	 * @return the id
	 *//*
    @JsonProperty("Id")
	public String getId() {
		return id;
	}
	*//**
	 * @param id the id to set
	 *//*
    @JsonProperty("Id")
	public void setId(String id) {
		this.id = id;
	}
	*//**
	 * @return the secondaryRatings
	 *//*
    @JsonProperty("SecondaryRatings")
	public SecondaryRatings getSecondaryRatings() {
		return secondaryRatings;
	}
	*//**
	 * @param secondaryRatings the secondaryRatings to set
	 *//*
    @JsonProperty("SecondaryRatings")
	public void setSecondaryRatings(SecondaryRatings secondaryRatings) {
		this.secondaryRatings = secondaryRatings;
	}
	*//**
	 * @return the commentIds
	 *//*
    @JsonProperty("CommentIds")
	public List<Object> getCommentIds() {
		return commentIds;
	}
	*//**
	 * @param commentIds the commentIds to set
	 *//*
    @JsonProperty("CommentIds")
	public void setCommentIds(List<Object> commentIds) {
		this.commentIds = commentIds;
	}
	*//**
	 * @return the lastModeratedTime
	 *//*
    @JsonProperty("LastModeratedTime")
	public String getLastModeratedTime() {
		return lastModeratedTime;
	}
	*//**
	 * @param lastModeratedTime the lastModeratedTime to set
	 *//*
    @JsonProperty("LastModeratedTime")
	public void setLastModeratedTime(String lastModeratedTime) {
		this.lastModeratedTime = lastModeratedTime;
	}*/ 
}
