package com.bbb.store.catalog.vo;

import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.rest.catalog.vo.CatalogItemAttributesVO;
import com.bbb.rest.catalog.vo.ProductRollupVO;


/**
 * 
 * Class containing product details extends MinimalVo of Style [specific to STOFU]
 * 
 * @author mgup39
 *
 */
public class FilteredProductDetailVO extends FilteredStyleProductDetailVO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//ProductGSVO Start
	private List<FilteredProductDetailVO> siblingProducts;
	private List<String> goodToKnow;
	private String productLowPrice;
	private String productHighPrice;
	private String seoUrl;
	private String productLowSalePrice;
	private String productHighSalePrice;
	private List<GSMediaVO> otherMedia;
	//ProductGSVO End
	
	//ProductRestVO Start
	private Map<String,String> skuRollupMap;
	private List<ProductRollupVO> productRollupVO ;
	private CatalogItemAttributesVO productAllAttributesVO;
	//ProductRestVO End  

	//ProductVO Start
	private Map<String, List<AttributeVO>> attributesList;
	private BrandVO productBrand;
	private boolean collection;
	private String salePriceRangeDescription;
	private Map<String,List<RollupTypeVO>> rollupAttributes;
	private Map<String,List<RollupTypeVO>> prdRelationRollup;
	private BazaarVoiceProductVO bvReviews;
	private boolean zoomAvailable;
	private String shopGuideId;
	private String upcCode;
	private Boolean mGiftCertProduct;
	private Boolean disabled;
	private String department;
	//ProductVO End
	
	
	/**
	 * @return the siblingProducts
	 */
	public List<FilteredProductDetailVO> getSiblingProducts() {
		return this.siblingProducts;
	}
	/**
	 * @param siblingProducts the siblingProducts to set
	 */
	public void setSiblingProducts(List<FilteredProductDetailVO> siblingProducts) {
		this.siblingProducts = siblingProducts;
	}
	/**
	 * @return the productLowPrice
	 */
	public String getProductLowPrice() {
		return this.productLowPrice;
	}
	/**
	 * @param productLowPrice the productLowPrice to set
	 */
	public void setProductLowPrice(String productLowPrice) {
		this.productLowPrice = productLowPrice;
	}
	/**
	 * @return the productHighPrice
	 */
	public String getProductHighPrice() {
		return this.productHighPrice;
	}
	/**
	 * @param productHighPrice the productHighPrice to set
	 */
	public void setProductHighPrice(String productHighPrice) {
		this.productHighPrice = productHighPrice;
	}
	/**
	 * @return the seoUrl
	 */
	public String getSeoUrl() {
		return this.seoUrl;
	}
	/**
	 * @param seoUrl the seoUrl to set
	 */
	public void setSeoUrl(String seoUrl) {
		this.seoUrl = seoUrl;
	}
	/**
	 * @return the productLowSalePrice
	 */
	public String getProductLowSalePrice() {
		return this.productLowSalePrice;
	}
	/**
	 * @param productLowSalePrice the productLowSalePrice to set
	 */
	public void setProductLowSalePrice(String productLowSalePrice) {
		this.productLowSalePrice = productLowSalePrice;
	}
	/**
	 * @return the productHighSalePrice
	 */
	public String getProductHighSalePrice() {
		return this.productHighSalePrice;
	}
	/**
	 * @param productHighSalePrice the productHighSalePrice to set
	 */
	public void setProductHighSalePrice(String productHighSalePrice) {
		this.productHighSalePrice = productHighSalePrice;
	}
	/**
	 * @return the otherMedia
	 */
	public List<GSMediaVO> getOtherMedia() {
		return this.otherMedia;
	}
	/**
	 * @param otherMedia the otherMedia to set
	 */
	public void setOtherMedia(List<GSMediaVO> otherMedia) {
		this.otherMedia = otherMedia;
	}
	/**
	 * @return the skuRollupMap
	 */
	public Map<String, String> getSkuRollupMap() {
		return this.skuRollupMap;
	}
	/**
	 * @param skuRollupMap the skuRollupMap to set
	 */
	public void setSkuRollupMap(Map<String, String> skuRollupMap) {
		this.skuRollupMap = skuRollupMap;
	}
	/**
	 * @return the productRollupVO
	 */
	public List<ProductRollupVO> getProductRollupVO() {
		return this.productRollupVO;
	}
	/**
	 * @param productRollupVO the productRollupVO to set
	 */
	public void setProductRollupVO(List<ProductRollupVO> productRollupVO) {
		this.productRollupVO = productRollupVO;
	}
	/**
	 * @return the productAllAttributesVO
	 */
	public CatalogItemAttributesVO getProductAllAttributesVO() {
		return this.productAllAttributesVO;
	}
	/**
	 * @param productAllAttributesVO the productAllAttributesVO to set
	 */
	public void setProductAllAttributesVO(
			CatalogItemAttributesVO productAllAttributesVO) {
		this.productAllAttributesVO = productAllAttributesVO;
	}
	/**
	 * @return the productBrand
	 */
	public BrandVO getProductBrand() {
		return this.productBrand;
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
		return this.collection;
	}
	/**
	 * @param collection the collection to set
	 */
	public void setCollection(boolean collection) {
		this.collection = collection;
	}
	/**
	 * @return the salePriceRangeDescription
	 */
	public String getSalePriceRangeDescription() {
		return this.salePriceRangeDescription;
	}
	/**
	 * @param salePriceRangeDescription the salePriceRangeDescription to set
	 */
	public void setSalePriceRangeDescription(String salePriceRangeDescription) {
		this.salePriceRangeDescription = salePriceRangeDescription;
	}
	/**
	 * @return the rollupAttributes
	 */
	public Map<String, List<RollupTypeVO>> getRollupAttributes() {
		return this.rollupAttributes;
	}
	/**
	 * @param rollupAttributes the rollupAttributes to set
	 */
	public void setRollupAttributes(Map<String, List<RollupTypeVO>> rollupAttributes) {
		this.rollupAttributes = rollupAttributes;
	}
	/**
	 * @return the prdRelationRollup
	 */
	public Map<String, List<RollupTypeVO>> getPrdRelationRollup() {
		return this.prdRelationRollup;
	}
	/**
	 * @param prdRelationRollup the prdRelationRollup to set
	 */
	public void setPrdRelationRollup(
			Map<String, List<RollupTypeVO>> prdRelationRollup) {
		this.prdRelationRollup = prdRelationRollup;
	}
	/**
	 * @return the bvReviews
	 */
	public BazaarVoiceProductVO getBvReviews() {
		return this.bvReviews;
	}
	/**
	 * @param bvReviews the bvReviews to set
	 */
	public void setBvReviews(BazaarVoiceProductVO bvReviews) {
		this.bvReviews = bvReviews;
	}
	/**
	 * @return the zoomAvailable
	 */
	public boolean isZoomAvailable() {
		return this.zoomAvailable;
	}
	/**
	 * @param zoomAvailable the zoomAvailable to set
	 */
	public void setZoomAvailable(boolean zoomAvailable) {
		this.zoomAvailable = zoomAvailable;
	}
	/**
	 * @return the shopGuideId
	 */
	public String getShopGuideId() {
		return this.shopGuideId;
	}
	/**
	 * @param shopGuideId the shopGuideId to set
	 */
	public void setShopGuideId(String shopGuideId) {
		this.shopGuideId = shopGuideId;
	}
	/**
	 * @return the upcCode
	 */
	public String getUpcCode() {
		return this.upcCode;
	}
	/**
	 * @param upcCode the upcCode to set
	 */
	public void setUpcCode(String upcCode) {
		this.upcCode = upcCode;
	}
	/**
	 * @return the mGiftCertProduct
	 */
	public Boolean getmGiftCertProduct() {
		return this.mGiftCertProduct;
	}
	/**
	 * @param mGiftCertProduct the mGiftCertProduct to set
	 */
	public void setmGiftCertProduct(Boolean mGiftCertProduct) {
		this.mGiftCertProduct = mGiftCertProduct;
	}
	/**
	 * @return the disabled
	 */
	public Boolean getDisabled() {
		return this.disabled;
	}
	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	/**
	 * @return the department
	 */
	public String getDepartment() {
		return this.department;
	}
	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}
	/**
	 * @return
	 */
	public Map<String, List<AttributeVO>> getAttributesList() {
		return this.attributesList;
	}
	/**
	 * @param attributesList
	 */
	public void setAttributesList(Map<String, List<AttributeVO>> attributesList) {
		this.attributesList = attributesList;
	}
	/**
	 * @return
	 */
	public List<String> getGoodToKnow() {
		return this.goodToKnow;
	}
	/**
	 * @param goodToKnow
	 */
	public void setGoodToKnow(List<String> goodToKnow) {
		this.goodToKnow = goodToKnow;
	}
	
}
