package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.utils.BBBUtility;

import atg.repository.RepositoryItem;

import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.search.bean.result.BBBDynamicPriceVO;
import com.bbb.search.bean.result.BBBProduct;
/**
 * 
 * @author njai13
 *
 */
public class ProductVO implements Serializable {
	/**
	 * 
	 */
    private BBBDynamicPriceVO dynamicPriceVO;
	private static final long serialVersionUID = 1L;
	private String productId;
	private String name;
	private String shortDescription;
	//Product keywords used for SEO
	private String prdKeywords;
	private String longDescription;
	private ImageVO productImages;
	private BrandVO productBrand;
	private boolean collection;
	private String priceRangeDescription;
	private String priceRangeDescriptionRepository;
	private String defaultPriceRangeDescription;
	private String salePriceRangeDescription;
	private String salePriceRangeDescriptionRepository;
	private boolean easy2MediaAvailableFlag;
	private List<String> childSKUs;
	private String productType;
	private List<TabVO> productTabs;
	private Map<String,List<RollupTypeVO>> rollupAttributes;
	private Map<String,List<RollupTypeVO>> prdRelationRollup;
	private List<MediaVO> productMedia;
	private Map<String, List<AttributeVO>> attributesList;
	private RepositoryItem productRepositoryItem;
	private BazaarVoiceProductVO bvReviews;
	private String skuLowPrice;
	private String skuHighPrice;
	private boolean zoomAvailable;
	private String seoUrl;
	private String shopGuideId;
	private String harmonLongDescription;
	private boolean ltlProduct;
	private boolean colorMatched;
	private boolean intlRestricted;
	private String priceRangeToken;
	private KickStarterPriceVO kickStarterPrice;
	private boolean customizableRequired;
	private String customizableCodes;
	private String vendorId;
	private boolean warrantyPriceCheck;
	private boolean shipMsgFlag;
	private String displayShipMsg;
	private boolean displaySizeAsSwatch;

	private Double lowPrice;
	private Double highPrice;

	private String priceString;
	private VendorInfoVO vendorInfoVO;
	
	private boolean bvRemoved = false;
	private Set<String> collectionParentProductIds;
	
	
	private String priceLabelCode;
	private boolean inCartFlag;
	private boolean dynamicPricingProduct;
	private BBBProduct bbbProduct;
	private SKUDetailVO defaultSkuDetailVO;
	
	private boolean isAccessoryLead;
	private boolean parentcollection;
	
	//if SKSW product is personalizable
	private boolean personalizedSku;
	
	//private List<String> activeChildProductIds;
	private List<String> porchServiceFamilyCodes;
	private String porchServiceFamilyType;
	 
	
	public boolean isPersonalizedSku() {
		return personalizedSku;
	}
	
	public void setPersonalizedSku(boolean personalizedSku) {
		this.personalizedSku = personalizedSku;
	}
	
	public Double getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}
	public Double getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}

	public String getDisplayShipMsg() {
		return displayShipMsg;
	}
	public void setDisplayShipMsg(String displayShipMsg) {
		this.displayShipMsg = displayShipMsg;
	}
	
	public boolean isShipMsgFlag() {
		return shipMsgFlag;
	}
	public void setShipMsgFlag(boolean shipMsgFlag) {
		this.shipMsgFlag = shipMsgFlag;
	}

	public VendorInfoVO getVendorInfoVO() {
		return vendorInfoVO;
	}
	public void setVendorInfoVO(VendorInfoVO vendorInfoVO) {
		this.vendorInfoVO = vendorInfoVO;
	}

	private List<AttributeVO> productAllAttributesVO;
	public boolean isWarrantyPriceCheck() {
		return warrantyPriceCheck;
	}
	public void setWarrantyPriceCheck(boolean warrantyPriceCheck) {
		this.warrantyPriceCheck = warrantyPriceCheck;
	}
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	public String getCustomizableCodes() {
		return customizableCodes;
	}
	public void setCustomizableCodes(String customizableCodes) {
		this.customizableCodes = customizableCodes;
	}
	public boolean isCustomizableRequired() {
		return customizableRequired;
	}
	public void setCustomizableRequired(boolean customizableRequired) {
		this.customizableRequired = customizableRequired;
	}
	public String getPriceRangeToken() {
		return priceRangeToken;
	}
	public void setPriceRangeToken(String priceRangeToken) {
		this.priceRangeToken = priceRangeToken;
	}
	/**
	 * @return the intlRestricted
	 */
	public boolean isIntlRestricted() {
		return intlRestricted;
	}
	/**
	 * @param intlRestricted the intlRestricted to set
	 */
	public void setIntlRestricted(boolean intlRestricted) {
		this.intlRestricted = intlRestricted;
	}
	
	public KickStarterPriceVO getKickStarterPrice() {
		return kickStarterPrice;
	}
	public void setKickStarterPrice(KickStarterPriceVO kickStarterPrice) {
		this.kickStarterPrice = kickStarterPrice;
	}
	
	/**
	 * 
	 * @return prdKeywords
	 */
	public String getPrdKeywords() {
		return prdKeywords;
	}
	
	/**
	 * 
	 * @param prdKeywords
	 */
	public void setPrdKeywords(String prdKeywords) {
		this.prdKeywords = prdKeywords;
	}

	
	public String getHarmonLongDescription() {
		return harmonLongDescription;
	}
	public void setHarmonLongDescription(String harmonLongDescription) {
		this.harmonLongDescription = harmonLongDescription;
	}
	
	public boolean isEasy2MediaAvailableFlag() {
		return easy2MediaAvailableFlag;
	}
	public void setEasy2MediaAvailableFlag(boolean easy2MediaAvailableFlag) {
		this.easy2MediaAvailableFlag = easy2MediaAvailableFlag;
	}
	
	/**
	 * @return the ltlProduct
	 */
	public boolean isLtlProduct() {
		return ltlProduct;
	}
	/**
	 * @param ltlProduct
	 */
	public void setLtlProduct(boolean ltlProduct) {
		this.ltlProduct = ltlProduct;
	}
	
	// Added to fix BBBSL-1888
	public Boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	/**
	 * @return the shopGuideId
	 */
	public String getShopGuideId() {
		return shopGuideId;
	}
	/**
	 * @param shopGuideId the shopGuideId to set
	 */
	public void setShopGuideId(String shopGuideId) {
		this.shopGuideId = shopGuideId;
	}

	/**
	 * @return the seoUrl
	 */
	public String getSeoUrl() {
		return seoUrl;
	}
	/**
	 * @param seoUrl the seoUrl to set
	 */
	public void setSeoUrl(String seoUrl) {
		this.seoUrl = seoUrl;
	}

	/*
	 * Added for BV Product feed
	 */
	private List<String> parentCategoryList;
	private String productPageUrl;
	private String imageUrl;
	private String ean;
	private String mfPartNumber;
	private String upcCode;
	private String isbn;
	private Map<String, String> regProductNames;
	private Map<String, String> regDescriptions;
	private Map<String, String> regProdPageUrl;
	private Map<String, String> regImageUrl;
	private String modelNumber;
	private String brandId;
	private Boolean mGiftCertProduct;
	private Boolean disabled;
	private String department;
	
	//Ever living pdp flag
	private Boolean isEverLiving;
	public Boolean getIsEverLiving() {
		return isEverLiving;
	}
	public void setIsEverLiving(Boolean isEverLiving) {
		this.isEverLiving = isEverLiving;
	}
	//END ever living pdp flag

	public ProductVO() {
		// TODO Auto-generated constructor stub
	}
	public ProductVO(ProductVO productVO) {
		this.setAttributesList(productVO.getAttributesList());
		this.setChildSKUs(productVO.getChildSKUs());
		this.setLongDescription(productVO.getLongDescription());
		this.setName(productVO.getName());
		this.setPriceRangeDescription(productVO.getPriceRangeDescription());
		this.setDefaultPriceRangeDescription(productVO.getDefaultPriceRangeDescription());
		this.setBvReviews(productVO.getBvReviews());
		this.setProductBrand(productVO.getProductBrand());
		this.setProductId(productVO.getProductId());
		this.setProductImages(productVO.getProductImages());
		this.setProductMedia(productVO.getProductMedia());
		this.setProductRepositoryItem(productVO.getProductRepositoryItem());
		this.setProductTabs(productVO.getProductTabs());
		this.setProductType(productVO.getProductType());
		this.setRollupAttributes(productVO.getRollupAttributes());
		this.setSalePriceRangeDescription(productVO.getSalePriceRangeDescription());
		this.setShortDescription(productVO.getShortDescription());
		this.setSkuHighPrice(productVO.getSkuHighPrice());
		this.setSkuLowPrice(productVO.getSkuLowPrice());
		this.setSeoUrl(productVO.getSeoUrl());
		this.setShopGuideId(productVO.getShopGuideId());
		this.setIsEverLiving(productVO.getIsEverLiving());
	}
	/**
	 * @return the refinedName
	 */ 
	public String getRefinedName() {
		return BBBUtility.refineFacetString(this.name);
	}
	
	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	/**
	 * @return the productImages
	 */
	public ImageVO getProductImages() {
		return productImages;
	}

	/**
	 * @param productImages the productImages to set
	 */
	public void setProductImages(ImageVO productImages) {
		this.productImages = productImages;
	}

	/**
	 * @return the productBrand
	 */
	public BrandVO getProductBrand() {
		return productBrand;
	}

	/**
	 * @param productBrand the productBrand to set
	 */
	public void setProductBrand(BrandVO productBrand) {
		this.productBrand = productBrand;
	}

	/**
	 * @return the collection
	 */
	public boolean isCollection() {
		return collection;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	/**
	 * @return the priceRangeDescription
	 */
	public String getPriceRangeDescription() {
		String country = getDynamicPriceVO().getCountry();
		//if country is US or MEXICO
		if( country!=null && (country.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO) || 
				country.equals(BBBInternationalShippingConstants.DEFAULT_COUNTRY) )){
			return getDynamicPriceVO().getPriceRangeDescription() !=null?getDynamicPriceVO().getPriceRangeDescription():priceRangeDescription;
		} else{
			return priceRangeDescription;
		}
	}

	/**
	 * @param priceRangeDescription the priceRangeDescription to set
	 */
	public void setPriceRangeDescription(String priceRangeDescription) {
		this.priceRangeDescription = priceRangeDescription;
	}

	/**
	 * @return the salePriceRangeDescription
	 */
	public String getSalePriceRangeDescription() {

		String country = getDynamicPriceVO().getCountry();
		//if country is US or MEXICO then only fetch dynamic price 
		if( country!=null && (country.equals(BBBInternationalShippingConstants.COUNTRY_MEXICO) || 
				country.equals(BBBInternationalShippingConstants.DEFAULT_COUNTRY) )){
			return getDynamicPriceVO().getSalePriceRangeDescription() !=null?getDynamicPriceVO().getSalePriceRangeDescription():salePriceRangeDescription;
		} else{
			return salePriceRangeDescription;
		}
	}

	/**
	 * @param salePriceRangeDescription the salePriceRangeDescription to set
	 */
	public void setSalePriceRangeDescription(String salePriceRangeDescription) {
		this.salePriceRangeDescription = salePriceRangeDescription;
	}


	/**
	 * @return the childSKUs
	 */
	public List<String> getChildSKUs() {
		return childSKUs;
	}

	/**
	 * @param childSKUs the childSKUs to set
	 */
	public void setChildSKUs(List<String> childSKUs) {
		this.childSKUs = childSKUs;
	}

	/**
	 * @return the productType
	 */
	public String getProductType() {
		return productType;
	}

	/**
	 * @param productType the productType to set
	 */
	public void setProductType(String productType) {
		this.productType = productType;
	}

	/**
	 * @return the productTabs
	 */
	public List<TabVO> getProductTabs() {
		return productTabs;
	}

	/**
	 * @param productTabs the productTabs to set
	 */
	public void setProductTabs(List<TabVO> productTabs) {
		this.productTabs = productTabs;
	}

	/**
	 * @return the rollupAttributes
	 */
	public Map<String, List<RollupTypeVO>> getRollupAttributes() {
		return rollupAttributes;
	}

	/**
	 * @param rollupAttributes the rollupAttributes to set
	 */
	public void setRollupAttributes(Map<String, List<RollupTypeVO>> rollupAttributes) {
		this.rollupAttributes = rollupAttributes;
	}

	/**
	 * @return the productMedia
	 */
	public List<MediaVO> getProductMedia() {
		return productMedia;
	}

	/**
	 * @param productMedia the productMedia to set
	 */
	public void setProductMedia(List<MediaVO> productMedia) {
		this.productMedia = productMedia;
	}

	/**
	 * @return the attributesList
	 */
	public Map<String, List<AttributeVO>> getAttributesList() {
		return attributesList;
	}

	/**
	 * @param attributesList the attributesList to set
	 */
	public void setAttributesList(Map<String, List<AttributeVO>> attributesList) {
		this.attributesList = attributesList;
	}

	/**
	 * @return the productRepositoryItem
	 */
	public RepositoryItem getProductRepositoryItem() {
		return productRepositoryItem;
	}

	/**
	 * @param productRepositoryItem the productRepositoryItem to set
	 */
	public void setProductRepositoryItem(RepositoryItem productRepositoryItem) {
		this.productRepositoryItem = productRepositoryItem;
	}

	/**
	 * @return the bvReviews
	 */
	public BazaarVoiceProductVO getBvReviews() {
		return bvReviews;
	}

	/**
	 * @param bvReviews the bvReviews to set
	 */
	public void setBvReviews(BazaarVoiceProductVO bvReviews) {
		this.bvReviews = bvReviews;
	}

	/**
	 * @return the skuLowPrice
	 */
	public String getSkuLowPrice() {
		return skuLowPrice;
	}

	/**
	 * @param skuLowPrice the skuLowPrice to set
	 */
	public void setSkuLowPrice(String skuLowPrice) {
		this.skuLowPrice = skuLowPrice;
	}

	/**
	 * @return the skuHighPrice
	 */
	public String getSkuHighPrice() {
		return skuHighPrice;
	}

	/**
	 * @param skuHighPrice the skuHighPrice to set
	 */
	public void setSkuHighPrice(String skuHighPrice) {
		this.skuHighPrice = skuHighPrice;
	}


	public List<String> getParentCategoryList() {
		return parentCategoryList;
	}

	public void setParentCategoryList(List<String> parentCategoryList) {
		this.parentCategoryList = parentCategoryList;
	}

	public String getProductPageUrl() {
		return productPageUrl;
	}

	public void setProductPageUrl(String productPageUrl) {
		this.productPageUrl = productPageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getMfPartNumber() {
		return mfPartNumber;
	}

	public void setMfPartNumber(String mfPartNumber) {
		this.mfPartNumber = mfPartNumber;
	}

	public String getUpcCode() {
		return upcCode;
	}

	public void setUpcCode(String upcCode) {
		this.upcCode = upcCode;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Map<String, String> getRegProductNames() {
		return regProductNames;
	}

	public void setRegProductNames(Map<String, String> regProductNames) {
		this.regProductNames = regProductNames;
	}

	public Map<String, String> getRegDescriptions() {
		return regDescriptions;
	}

	public void setRegDescriptions(Map<String, String> regDescriptions) {
		this.regDescriptions = regDescriptions;
	}

	public Map<String, String> getRegProdPageUrl() {
		return regProdPageUrl;
	}

	public void setRegProdPageUrl(Map<String, String> regProdPageUrl) {
		this.regProdPageUrl = regProdPageUrl;
	}

	public Map<String, String> getRegImageUrl() {
		return regImageUrl;
	}

	public void setRegImageUrl(Map<String, String> regImageUrl) {
		this.regImageUrl = regImageUrl;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public Map<String, List<RollupTypeVO>> getPrdRelationRollup() {
		return prdRelationRollup;
	}

	public void setPrdRelationRollup(
			Map<String, List<RollupTypeVO>> prdRelationRollup) {
		this.prdRelationRollup = prdRelationRollup;
	}

	public boolean isZoomAvailable() {
		return zoomAvailable;
	}

	public void setZoomAvailable(boolean zoomAvailable) {
		this.zoomAvailable = zoomAvailable;
	}

	public Boolean getGiftCertProduct() {
		return mGiftCertProduct;
	}

	public void setGiftCertProduct(Boolean pGiftCertProduct) {
		this.mGiftCertProduct = pGiftCertProduct;
	}
	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}
	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}
	public boolean isColorMatched() {
		return colorMatched;
	}
	public void setColorMatched(boolean colorMatched) {
		this.colorMatched = colorMatched;
	}
	public String toString(){
		return "ProductVO.toString() product Id is:"+this.productId;
		/**
		 * Manoj: commenting the below code as this was getting printed even in 
		 * JSP rendering.
		 * 
		 * / 
		/*
		if(this.productId!=null){
			StringBuffer toString=new StringBuffer("Details for product Id "+this.productId+" \n");
			toString.append("*************Basic details *********** \n").append("Product Name:: ").append(this.name).append("\n Product Short Description :: ").append(this.shortDescription)
			.append("\n Product Long Description ").append(this.longDescription).append("\n Product is a collection?? ").append(this.collection)
			.append("\n Product Low price ").append(this.skuLowPrice!=null ?this.skuLowPrice:" Null").append("\n Product High price ").append(this.skuHighPrice!=null ?this.skuHighPrice:" Null")
			.append("\n Product price Range " ).append(this.priceRangeDescription!=null ?this.priceRangeDescription:" Null")
			.append("\n Product sale Price Range " ).append(this.salePriceRangeDescription!=null ?this.salePriceRangeDescription:" Null")
			.append("\n******************************BRAND details ************************* \n")
			.append("\n Brand Name ").append((this.getProductBrand()!=null && this.getProductBrand().getBrandName()!=null)?this.getProductBrand().getBrandName():" NULL")
			.append("\n Brand ID ").append((this.getProductBrand()!=null && this.getProductBrand().getBrandId()!=null)?this.getProductBrand().getBrandId():" NULL")
			.append("\n Brand Image ").append((this.getProductBrand()!=null && this.getProductBrand().getBrandImage()!=null)?this.getProductBrand().getBrandImage():" NULL")
			.append("\n Brand Display Flag ").append((this.getProductBrand()!=null )?this.getProductBrand().isDisplayFlag():" NULL")
			.append("\n***********************************Product Attributes************************************\n");
			if(this.attributesList!=null && !attributesList.isEmpty()){
				Set<String >keySet=attributesList.keySet();
				for(String key:keySet){
					List<AttributeVO> attrList=attributesList.get(key);
					if(attrList!=null && !attrList.isEmpty()){
						toString.append("\n Product Attributes For key ").append(key).append(" Total no "+attrList.size()+" \n");
						int i=0;
						for(AttributeVO attrVo:attrList){
							toString.append(++i+") Attribute Description::  "+attrVo.getAttributeDescrip())
							.append(" ::attribute Name  "+attrVo.getAttributeName()).append(" ::attribute PlaceHolder::  "+attrVo.getPlaceHolder()).append("\n");
						}
					}
					else{
						toString.append("\n No Product attributes present for key ").append(key).append(" \n");
					}
				}
			}
			else{
				toString.append("No Attributes defined for product ").append(this.productId).append(" \n");
			}
			if(productImages!=null){
				toString.append("Product Images for Product ID ").append(productId).append("\n").append(productImages.toString());
			}
			else{
				toString.append("No Images defined for product ").append(this.productId);
			}
			if(this.rollupAttributes!=null && !rollupAttributes.isEmpty()){
				Set<String >keySet=rollupAttributes.keySet();
				for(String key:keySet){
					List<RollupTypeVO> rollUpList=rollupAttributes.get(key);
					if(rollUpList!=null && !rollUpList.isEmpty()){
						toString.append("\n**************Product Roll Up Attributes of type  ").append(key).append(" :Total no "+rollUpList.size()+" \n");
						for(RollupTypeVO rollupTypeVO:rollUpList){
							toString.append(" Roll Up attribute::  "+rollupTypeVO.getRollupAttribute())
							.append(" ::Roll Up attribute Code::  "+rollupTypeVO.getRollupTypeCode()).append(" ::Swatch Image Path::  "+rollupTypeVO.getSwatchImagePath()).append("\n");
						}
					}
					else{
						toString.append("\n Roll Up attributes present for type ").append(key).append(" \n");
					}
				}
			}
			else{
				toString.append("No RollUp Attributes defined for product ").append(this.productId).append("\n");
			}
			if(bvReviews!=null){
				toString.append(" bazaar Voice average Rating:: ").append(bvReviews.getAverageOverallRating())
				.append(" bazaar Voice Total review Count:: ").append(bvReviews.getTotalReviewCount()).append("\n");;   
			}
			else{
				toString.append("Bazaar Voice Rating Not available for product ").append(this.productId).append("\n");
			}
			if(this.childSKUs!=null  && !childSKUs.isEmpty()){
				toString.append("List of Child Sku available for productId ").append(this.productId).append("\n");
				for(String skuId:childSKUs){
					toString.append(skuId).append(" :: ");
				}
			}
			else{
				toString.append("No Child Sku available for ").append(this.productId).append("\n");
			}
			return toString.toString();
		}
		else{
			return null;
		}*/
	}
	public String getDefaultPriceRangeDescription() {
		return defaultPriceRangeDescription;
	}
	public void setDefaultPriceRangeDescription(
			String defaultPriceRangeDescription) {
		this.defaultPriceRangeDescription = defaultPriceRangeDescription;
	}
	public List<AttributeVO> getProductAllAttributesVO() {
		return productAllAttributesVO;
	}
	public void setProductAllAttributesVO(List<AttributeVO> productAllAttributesVO) {
		this.productAllAttributesVO = productAllAttributesVO;
	}
	public Set<String> getCollectionParentProductIds() {
		if(this.collectionParentProductIds == null)
			this.collectionParentProductIds = new HashSet<String>();
		return collectionParentProductIds;
	}
	public void setCollectionParentProductIds(Set<String> collectionParentProductIds) {
		this.collectionParentProductIds = collectionParentProductIds;
	}
	public String getPriceString() {
		return priceString;
	}
	public void setPriceString(String priceString) {
		this.priceString = priceString;
	}
	public boolean isBvRemoved() {
		return bvRemoved;
	}
	public void setBvRemoved(boolean bvRemoved) {
		this.bvRemoved = bvRemoved;
	}
	
	public String getPriceLabelCode() {
		//return priceLabelCode;
		return getDynamicPriceVO().getPriceLabelCode()!=null?getDynamicPriceVO().getPriceLabelCode():priceLabelCode;
	}
	
	public void setPriceLabelCode(String priceLabelCode) {
		this.priceLabelCode = priceLabelCode;
	}
	
	/**
	 * @return the dynamicPricingProduct
	 */
	public final boolean isDynamicPricingProduct() {
		//return dynamicPricingProduct;
		return getDynamicPriceVO().isDynamicPricingProduct();
	}
	/**
	 * @param dynamicPricingProduct the dynamicPricingProduct to set
	 */
	public final void setDynamicPricingProduct(boolean dynamicPricingProduct) {
		this.dynamicPricingProduct = dynamicPricingProduct;
	}
	/**
	 * @return the inCartFlag
	 */
	public final boolean isInCartFlag() {
		//return inCartFlag;
		return getDynamicPriceVO().isInCartFlag();
	}
	/**
	 * @param inCartFlag the inCartFlag to set
	 */
	public final void setInCartFlag(boolean inCartFlag) {
		this.inCartFlag = inCartFlag;
	}
	public BBBProduct getBbbProduct() {
		return bbbProduct;
	}
	public void setBbbProduct(BBBProduct bbbProduct) {
		this.bbbProduct = bbbProduct;
	}

	public void setDynamicPriceVO(BBBDynamicPriceVO dynamicPriceVO) {
		this.dynamicPriceVO = dynamicPriceVO;
	}
	
	public BBBDynamicPriceVO getDynamicPriceVO() {
		
		//if dynamice sku price info is null and info is not fetched once
		if(dynamicPriceVO==null && ! isDynPriceInfoAlreadyFetched()){
			
			dynamicPriceVO = BBBUtility.populateDynamicProductPricingVO(getProductId()); 
	
			//set it that information was fetched
			this.setDynPriceInfoAlreadyFetched(true);
		}
		
		return dynamicPriceVO;
	}
	
	private boolean dynPriceInfoAlreadyFetched;

	public boolean isDynPriceInfoAlreadyFetched() {
		return dynPriceInfoAlreadyFetched;
	}
	public void setDynPriceInfoAlreadyFetched(boolean dynPriceInfoAlreadyFetched) {
		this.dynPriceInfoAlreadyFetched = dynPriceInfoAlreadyFetched;
	}

	public String getPriceRangeDescriptionRepository() {
		return priceRangeDescriptionRepository;
	}
	public void setPriceRangeDescriptionRepository(
			String priceRangeDescriptionRepository) {
		this.priceRangeDescriptionRepository = priceRangeDescriptionRepository;
	}
	public String getSalePriceRangeDescriptionRepository() {
		return salePriceRangeDescriptionRepository;
	}
	public void setSalePriceRangeDescriptionRepository(
			String salePriceRangeDescriptionRepository) {
		this.salePriceRangeDescriptionRepository = salePriceRangeDescriptionRepository;
	}


/**
	 * @return the porchServiceFamilyCodes
	 */
	public List<String> getPorchServiceFamilyCodes() {
		return porchServiceFamilyCodes;
	}
	/**
	 * @param porchServiceFamilyCodes the porchServiceFamilyCodes to set
	 */
	public void setPorchServiceFamilyCodes(List<String> porchServiceFamilyCodes) {
		this.porchServiceFamilyCodes = porchServiceFamilyCodes;
	}
	/**
	 * @return the porchServiceFamilyType
	 */
	public String getPorchServiceFamilyType() {
		return porchServiceFamilyType;
	}
	/**
	 * @param porchServiceFamilyType the porchServiceFamilyType to set
	 */
	public void setPorchServiceFamilyType(String porchServiceFamilyType) {
		this.porchServiceFamilyType = porchServiceFamilyType;
	}
	
	
	/*public List<String> getActiveChildProductIds() {
		return activeChildProductIds;
	}
	public void setActiveChildProductIds(List<String> activeChildProductIds) {
		this.activeChildProductIds = activeChildProductIds;
	}*/

    public boolean isDisplaySizeAsSwatch() {
		return displaySizeAsSwatch;
	}
	public void setDisplaySizeAsSwatch(boolean displaySizeAsSwatch) {
		this.displaySizeAsSwatch = displaySizeAsSwatch;
	}
	public SKUDetailVO getDefaultSkuDetailVO() {
		return defaultSkuDetailVO;
}

	public void setDefaultSkuDetailVO(SKUDetailVO defaultSkuDetailVO) {
		this.defaultSkuDetailVO = defaultSkuDetailVO;
	}

	public boolean isAccessoryLead() {
		return isAccessoryLead;
	}
	

	public void setAccessoryLead(boolean isAccessoryLead) {
		this.isAccessoryLead = isAccessoryLead;
	}

	public boolean isParentcollection() {
		return parentcollection;
	}
	

	public void setParentcollection(boolean parentcollection) {
		this.parentcollection = parentcollection;
	}
	
	
	
}

