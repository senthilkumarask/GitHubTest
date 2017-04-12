package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

//import atg.dynamometer.Banner;

//import com.bbb.commerce.catalog.vo.ProductVO;

public class HomePageContentVO  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String templateId;
	private String siteId;
	private CarouselVO carouselVO;
	private BannerVO bannerVO;
	private ProductCarouselVO productCarouselVO;
	private CategoryContainerVO categoryContainerVO;
	private List<PromoContainerVO> homePromoTierLayOut1;
	private List<PromoContainerVO> homePromoTierLayOut2;
	
	/**
	 * @return the homePromoTierLayOut1
	 */
	public List<PromoContainerVO> getHomePromoTierLayOut1() {
		return homePromoTierLayOut1;
	}
	/**
	 * @param homePromoTierLayOut1 the homePromoTierLayOut1 to set
	 */
	public void setHomePromoTierLayOut1(List<PromoContainerVO> homePromoTierLayOut1) {
		this.homePromoTierLayOut1 = homePromoTierLayOut1;
	}
	/**
	 * @return the homePromoTierLayOut2
	 */
	public List<PromoContainerVO> getHomePromoTierLayOut2() {
		return homePromoTierLayOut2;
	}
	/**
	 * @param homePromoTierLayOut2 the homePromoTierLayOut2 to set
	 */
	public void setHomePromoTierLayOut2(List<PromoContainerVO> homePromoTierLayOut2) {
		this.homePromoTierLayOut2 = homePromoTierLayOut2;
	}
	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}
	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return the carouselVO
	 */
	public CarouselVO getCarouselVO() {
		return carouselVO;
	}
	/**
	 * @param carouselVO the carouselVO to set
	 */
	public void setCarouselVO(CarouselVO carouselVO) {
		this.carouselVO = carouselVO;
	}
	/**
	 * @return the bannerVO
	 */
	public BannerVO getBannerVO() {
		return bannerVO;
	}
	/**
	 * @param bannerVO the bannerVO to set
	 */
	public void setBannerVO(BannerVO bannerVO) {
		this.bannerVO = bannerVO;
	}
	/**
	 * @return the productCarouselVO
	 */
	public ProductCarouselVO getProductCarouselVO() {
		return productCarouselVO;
	}
	/**
	 * @param productCarouselVO the productCarouselVO to set
	 */
	public void setProductCarouselVO(ProductCarouselVO productCarouselVO) {
		this.productCarouselVO = productCarouselVO;
	}
	/**
	 * @return the categoryContainerVO
	 */
	public CategoryContainerVO getCategoryContainerVO() {
		return categoryContainerVO;
	}
	/**
	 * @param categoryContainerVO the categoryContainerVO to set
	 */
	public void setCategoryContainerVO(CategoryContainerVO categoryContainerVO) {
		this.categoryContainerVO = categoryContainerVO;
	}
	
	@Override
	public String toString() {
		return "HomePageContentVO [templateId=" + templateId + ", siteId="
				+ siteId + ", carouselVO=" + carouselVO + ", bannerVO="
				+ bannerVO + ", productCarouselVO=" + productCarouselVO
				+ ", categoryContainerVO=" + categoryContainerVO
				+ ", homePromoTierLayOut1=" + homePromoTierLayOut1
				+ ", homePromoTierLayOut2=" + homePromoTierLayOut2 + "]";
	}
	
}
