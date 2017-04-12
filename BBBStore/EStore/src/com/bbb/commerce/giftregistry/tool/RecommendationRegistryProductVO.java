package com.bbb.commerce.giftregistry.tool;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;

/**
 *
 * VO used for two purposes.
 * It is used to populate products information when the tabs are Pending, Declined or Accepted.
 * VO is used to populate Recommenders information when the tab is Recommenders.
 *
 * @author schint
 *
 */
public class RecommendationRegistryProductVO
		implements
			Comparable<RecommendationRegistryProductVO>{
	/**
	 *
	 */
	String registryId;
	String recommenderProfileId;
	String skuId;
	String isFromFB;
	String productId;
	String firstName;
	String lastName;
	long recommendedQuantity;
	long acceptedQuantity;
	boolean declined;
	double skuPrice;
	double skuListPrice;
	double skuSalePrice;
	Date recommendedDate;
	Date recenltyModifiedDate;
	String comment;
	String skuDisplayName;
	String skuColor;
	String skuSize;
	String upc;
	ImageVO imageVO;
	BazaarVoiceProductVO bvProductVO;
	private String fname_profileId;
	private String jdaCategory;
	private int purchasedQuantity;
	/** The jda retail price. */
	// BPS-795
	private double jdaRetailPrice;

	//Part of Recommenders Story.
	private long declinedQuantity;
	private boolean profileActive;
	private String fullName;
	private boolean ltl;
	private SKUDetailVO sKUDetailVO;
	private String price;
	private String groupByFlag;
	private String displayShipMsg;
	//BBBH-4958
	private double skuIncartPrice;
	
	/**
	 * Gets the sku incartPrice.
	 *
	 * @return skuIncartPrice
	 */
	public double getSkuIncartPrice() {
		return skuIncartPrice;
	}

	/**
	 * Sets the skuIncartPrice.
	 *
	 * @param skuIncartPrice
	 */
	public void setSkuIncartPrice(double skuIncartPrice) {
		this.skuIncartPrice = skuIncartPrice;
	}

	/** The sku attributes. */
	// added for story BBBP-805
	private Map<String, List<AttributeVO>> skuAttributes;
	
	/** The ltl ship method. */
	private String ltlShipMethod;
	
	private String ltlAssemblySelected;
	
	/** The ltl ship method desc. */
	private String ltlShipMethodDesc;
	
	/** The assembly fees. */
	private double assemblyFees;
	
	/** The delivery surcharge. */
	private double deliverySurcharge;
	
	/** The ship method unsupported. */
	private boolean shipMethodUnsupported;
	
	/**
	 * Gets the sku attributes.
	 *
	 * @return the sku attributes
	 */
	public Map<String, List<AttributeVO>> getSkuAttributes() {
		return skuAttributes;
	}
	
	/**
	 * Sets the sku attributes.
	 *
	 * @param skuAttributes the sku attributes
	 */
	public void setSkuAttributes(Map<String, List<AttributeVO>> skuAttributes) {
		this.skuAttributes = skuAttributes;
	}
	
	/**
	 * Gets the ltl ship method.
	 *
	 * @return the ltl ship method
	 */
	public String getLtlShipMethod() {
		return ltlShipMethod;
	}
	
	/**
	 * Sets the ltl ship method.
	 *
	 * @param ltlShipMethod the new ltl ship method
	 */
	public void setLtlShipMethod(String ltlShipMethod) {
		this.ltlShipMethod = ltlShipMethod;
	}
	

	public String getLtlAssemblySelected() {
		return ltlAssemblySelected;
	}

	public void setLtlAssemblySelected(String ltlAssemblySelected) {
		this.ltlAssemblySelected = ltlAssemblySelected;
	}
	
	/**
	 * Gets the ltl ship method desc.
	 *
	 * @return the ltl ship method desc
	 */
	public String getLtlShipMethodDesc() {
		return ltlShipMethodDesc;
	}
	
	/**
	 * Sets the ltl ship method desc.
	 *
	 * @param ltlShipMethodDesc the new ltl ship method desc
	 */
	public void setLtlShipMethodDesc(String ltlShipMethodDesc) {
		this.ltlShipMethodDesc = ltlShipMethodDesc;
	}
	
	/**
	 * Gets the assembly fees.
	 *
	 * @return the assembly fees
	 */
	public double getAssemblyFees() {
		return assemblyFees;
	}
	
	/**
	 * Sets the assembly fees.
	 *
	 * @param assemblyFees the new assembly fees
	 */
	public void setAssemblyFees(double assemblyFees) {
		this.assemblyFees = assemblyFees;
	}
	
	/**
	 * Gets the delivery surcharge.
	 *
	 * @return the delivery surcharge
	 */
	public double getDeliverySurcharge() {
		return deliverySurcharge;
	}
	
	/**
	 * Sets the delivery surcharge.
	 *
	 * @param deliverySurcharge the new delivery surcharge
	 */
	public void setDeliverySurcharge(double deliverySurcharge) {
		this.deliverySurcharge = deliverySurcharge;
	}
	
	/**
	 * Checks if is ship method unsupported.
	 *
	 * @return true, if is ship method unsupported
	 */
	public boolean isShipMethodUnsupported() {
		return shipMethodUnsupported;
	}
	
	/**
	 * Sets the ship method unsupported.
	 *
	 * @param shipMethodUnsupported the new ship method unsupported
	 */
	public void setShipMethodUnsupported(boolean shipMethodUnsupported) {
		this.shipMethodUnsupported = shipMethodUnsupported;
	}
	
	public boolean isLtl() {
		return ltl;
	}
	public void setLtl(boolean ltl) {
		this.ltl = ltl;
	}
	public String getIsFromFB() {
		return isFromFB;
	}
	public void setIsFromFB(String isFromFB) {
		this.isFromFB = isFromFB;
	}
	public String getFullName() {
		return this.fullName;
	}
	public void setFullName(){
		this.fullName = this.getFirstName() + " " + this.getLastName();
	}

	public boolean isProfileActive() {
		return profileActive;
	}
	public void setProfileActive(boolean profileActive) {
		this.profileActive = profileActive;
	}
	public String getJdaCategory() {
		return jdaCategory;
	}
	public void setJdaCategory(String jdaCategory) {
		this.jdaCategory = jdaCategory;
	}
	String repositoryId;
	public String getRepositoryId() {
		return repositoryId;
	}
	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	public String getFname_profileId() {
		return firstName+"_"+recommenderProfileId;
	}
	public long getDeclinedQuantity() {
		return declinedQuantity;
	}
	public void setDeclinedQuantity(long declinedQuantity) {
		this.declinedQuantity = declinedQuantity;
	}
	public void setFname_profileId(String fname_profileId) {
		this.fname_profileId = fname_profileId;
	}
	public void setFname_profileId(String fname, String profileId) {
		this.fname_profileId = fname+"_"+profileId;
	}
	
	public RecommendationRegistryProductVO(String registryId, String skuId,
			long recommendedQuantity, String comment,String recommenderProfileId ) {
		super();
		this.registryId = registryId;
		this.skuId = skuId;
		this.recommendedQuantity = recommendedQuantity;
		this.comment = comment;
		this.recommenderProfileId=recommenderProfileId;
	}
	// BPS-795
		public RecommendationRegistryProductVO() {
			//constructor
		}

	public boolean isDeclined() {
		return declined;
	}
	public void setDeclined(boolean isdeclined) {
		this.declined = isdeclined;
	}
	public ImageVO getImageVO() {
		return imageVO;
	}
	public void setImageVO(ImageVO imageVO) {
		this.imageVO = imageVO;
	}
	public static Comparator<RecommendationRegistryProductVO> getSalePrice() {
		return salePriceComparator;
	}
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public static void setSalePrice(
			Comparator<RecommendationRegistryProductVO> salePrice) {
		RecommendationRegistryProductVO.salePriceComparator = salePrice;
	}
	public static Comparator<RecommendationRegistryProductVO> getRecommendedDateComparator() {
		return recommendedDateComparator;
	}
	public static void setRecommendedDateComparator(
			Comparator<RecommendationRegistryProductVO> recommendedDateComparator) {
		RecommendationRegistryProductVO.recommendedDateComparator = recommendedDateComparator;
	}
	public static Comparator<RecommendationRegistryProductVO> getJdaCategoryComparator() {
		return jdaCategoryComparator;
	}
	public static void setJdaCategoryComparator(
			Comparator<RecommendationRegistryProductVO> jdaCategoryComparator) {
		RecommendationRegistryProductVO.jdaCategoryComparator = jdaCategoryComparator;
	}
	public static Comparator<RecommendationRegistryProductVO> getRecommender() {
		return recommenderComparator;
	}
	public static void setRecommender(
			Comparator<RecommendationRegistryProductVO> recommender) {
		RecommendationRegistryProductVO.recommenderComparator = recommender;
	}

	public double getSkuListPrice() {
		return skuListPrice;
	}
	public void setSkuListPrice(double skuListPrice) {
		this.skuListPrice = skuListPrice;
	}
	public double getSkuSalePrice() {
		return skuSalePrice;
	}
	public void setSkuSalePrice(double skuSalePrice) {
		this.skuSalePrice = skuSalePrice;
	}
	public String getSkuDisplayName() {
		return skuDisplayName;
	}
	public void setSkuDisplayName(String skuDisplayName) {
		this.skuDisplayName = skuDisplayName;
	}
	public String getSkuColor() {
		return skuColor;
	}
	public void setSkuColor(String skuColor) {
		this.skuColor = skuColor;
	}
	public String getSkuSize() {
		return skuSize;
	}
	public void setSkuSize(String skuSize) {
		this.skuSize = skuSize;
	}
	public BazaarVoiceProductVO getBvProductVO() {
		return bvProductVO;
	}
	public void setBvProductVO(BazaarVoiceProductVO bvProductVO) {
		this.bvProductVO = bvProductVO;
	}
	public String getRegistryId() {
		return registryId;
	}
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public double getSkuPrice() {
		return skuPrice;
	}
	public void setSkuPrice(double skuPrice) {
		this.skuPrice = skuPrice;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public long getRecommendedQuantity() {
		return recommendedQuantity;
	}
	public void setRecommendedQuantity(long recommendedQuantity) {
		this.recommendedQuantity = recommendedQuantity;
	}
	public long getAcceptedQuantity() {
		return acceptedQuantity;
	}
	public void setAcceptedQuantity(long requestedQuantity) {
		this.acceptedQuantity = requestedQuantity;
	}
	public Date getRecommendedDate() {
		return recommendedDate;
	}
	public void setRecommendedDate(Date recommendedDate) {
		this.recommendedDate = recommendedDate;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getRecenltyModifiedDate() {
		return recenltyModifiedDate;
	}
	public void setRecenltyModifiedDate(Date recenltyModifiedDate) {
		this.recenltyModifiedDate = recenltyModifiedDate;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getRecommenderProfileId() {
		return recommenderProfileId;
	}
	public void setRecommenderProfileId(String recommenderProfileId) {
		this.recommenderProfileId = recommenderProfileId;
	}
	@Override
	public int compareTo(RecommendationRegistryProductVO recommendationVO) {
		return recommendationVO.getFirstName().compareTo(this.firstName);
	}

	/**
	 * Comparator to sort recommendations list or array in order of Sale Price
	 */
	public static Comparator<RecommendationRegistryProductVO> salePriceComparator = new Comparator<RecommendationRegistryProductVO>() {

		@Override
		public int compare(RecommendationRegistryProductVO r1,
				RecommendationRegistryProductVO r2) {
			return (int) (r2.getSkuSalePrice() - r1.getSkuSalePrice());
		}
	};
	/**
	 * Comparator to sort recommendations list or array in order of List Price
	 */
	public static Comparator<RecommendationRegistryProductVO> listPriceComparator = new Comparator<RecommendationRegistryProductVO>() {

		@Override
		public int compare(RecommendationRegistryProductVO r1,
				RecommendationRegistryProductVO r2) {
			int compareVal = (int) (r1.getSkuListPrice() - r2.getSkuListPrice());
			//BPS SURGE CHANGE - IN CASE List Price IS SAME DO SECOND SORT BY DATE
			if(compareVal == 0) {
				compareVal = (int) (r2.getRecommendedDate().compareTo(r1.getRecommendedDate()));
			}
			return compareVal;

		}
	};
	/**
	 * Comparator to sort recommendations list or array in order of recommendedDate
	 */
	public static Comparator<RecommendationRegistryProductVO> recommendedDateComparator = new Comparator<RecommendationRegistryProductVO>() {

		@Override
		public int compare(RecommendationRegistryProductVO r1,
				RecommendationRegistryProductVO r2) {
			return (int) (r2.getRecommendedDate().compareTo(r1
					.getRecommendedDate()));
		}
	};
	
	public static Comparator<RecommendationRegistryProductVO> lastModifiedDateComparator = new Comparator<RecommendationRegistryProductVO>() {

		@Override
		public int compare(RecommendationRegistryProductVO r1,
				RecommendationRegistryProductVO r2) {
			return (int) (r2.getRecenltyModifiedDate().compareTo(r1
					.getRecenltyModifiedDate()));
		}
	};

	/**
	 * Comparator to sort recommendations list or array in order of SalePrice
	 */
	public static Comparator<RecommendationRegistryProductVO> jdaCategoryComparator = new Comparator<RecommendationRegistryProductVO>() {

		@Override
		public int compare(RecommendationRegistryProductVO r1,
				RecommendationRegistryProductVO r2) {
			int compareVal = (r1.getJdaCategory().compareTo(r2.getJdaCategory()));
			//BPS SURGE CHANGE - IN CASE Category IS SAME DO SECOND SORT BY DATE
			if(compareVal == 0) {
				compareVal = (int) (r2.getRecommendedDate().compareTo(r1.getRecommendedDate()));
			}
			return compareVal;
		}
	};

	/**
	 * Comparator to sort recommendations list or array in order of recommender first name
	 */
	public static Comparator<RecommendationRegistryProductVO> recommenderComparator = new Comparator<RecommendationRegistryProductVO>() {

		@Override
		public int compare(RecommendationRegistryProductVO r1,
				RecommendationRegistryProductVO r2) {
			int compareVal = r1.getFname_profileId().toLowerCase().compareTo(r2.getFname_profileId().toLowerCase());
			//BPS SURGE CHANGE - IN CASE RECOMMENDER IS SAME DO SECOND SORT BY DATE
			if(compareVal == 0) {
				compareVal = (int) (r2.getRecommendedDate().compareTo(r1.getRecommendedDate()));
			}
			return compareVal;
		}
	};
	
	/**
	 * Comparator to sort recommendations list or array in order of price
	 */
	public static Comparator<RecommendationRegistryProductVO> priceCompare = new Comparator<RecommendationRegistryProductVO>() {

		@Override
	public  int compare(final RecommendationRegistryProductVO arg0, final RecommendationRegistryProductVO arg1) {
		
		final Double price1 = Double.parseDouble((String)arg0.getPrice());
		final Double price2 = Double.parseDouble((String)arg1.getPrice());
		return price1.compareTo(price2);
	}
	};
		
	/**
	 * Comparator to sort recommendations list or array in order of price
	 */
	public static Comparator<String> bucketSort = new Comparator<String>() {


		@Override
		public int compare(String arg0, String arg1) {
			
			return arg0.compareTo(arg1);
		}
	};
		
	
	

	@Override
	public String toString() {
		return "RecommendationRegistryProductVO [registryId=" + registryId
				+ ", recommenderProfileId=" + recommenderProfileId + ", skuId="
				+ skuId + ", isFromFB=" + isFromFB + ", productId=" + productId
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", recommendedQuantity=" + recommendedQuantity
				+ ", acceptedQuantity=" + acceptedQuantity + ", declined="
				+ declined + ", skuPrice=" + skuPrice + ", skuListPrice="
				+ skuListPrice + ", skuSalePrice=" + skuSalePrice
				+ ", recommendedDate=" + recommendedDate
				+ ", recenltyModifiedDate=" + recenltyModifiedDate
				+ ", comment=" + comment + ", skuDisplayName=" + skuDisplayName
				+ ", skuColor=" + skuColor + ", skuSize=" + skuSize + ", upc="
				+ upc + ", imageVO=" + imageVO + ", bvProductVO=" + bvProductVO
				+ ", fname_profileId=" + fname_profileId + ", jdaCategory="
				+ jdaCategory + ", purchasedQuantity=" + purchasedQuantity
				+ ", declinedQuantity=" + declinedQuantity + ", profileActive="
				+ profileActive + ", fullName=" + fullName + ", ltl="
				+ ltl + ", repositoryId=" + repositoryId +",  ltlShipMethod=" + ltlShipMethod +"]";
	}

	// BPS-1380 Accepted Tab Surge Start
	/**
	 * @return the purchasedQuantity
	 */
	public int getPurchasedQuantity() {
		return purchasedQuantity;
	}

	/**
	 * @param purchasedQuantity the purchasedQuantity to set
	 */
	public void setPurchasedQuantity(int purchasedQuantity) {
		this.purchasedQuantity = purchasedQuantity;
	}
	// BPS-1380 Accepted Tab Surge END
	public SKUDetailVO getsKUDetailVO() {
		return sKUDetailVO;
	}
	public void setsKUDetailVO(SKUDetailVO sKUDetailVO) {
		this.sKUDetailVO = sKUDetailVO;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public double getJdaRetailPrice() {
		return jdaRetailPrice;
	}
	public void setJdaRetailPrice(double jdaRetailPrice) {
		this.jdaRetailPrice = jdaRetailPrice;
	}
	public String getGroupByFlag() {
		return groupByFlag;
	}
	public void setGroupByFlag(String groupByFlag) {
		this.groupByFlag = groupByFlag;
	}
	public String getDisplayShipMsg() {
		return displayShipMsg;
	}
	public void setDisplayShipMsg(String displayShipMsg) {
		this.displayShipMsg = displayShipMsg;
	}
}
