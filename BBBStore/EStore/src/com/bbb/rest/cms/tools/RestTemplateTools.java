package com.bbb.rest.cms.tools;

import java.util.ArrayList;
import java.util.List;

import atg.repository.RepositoryItem;

import com.bbb.cms.BannerVO;
import com.bbb.cms.CarouselVO;
import com.bbb.cms.CategoryContainerVO;
import com.bbb.cms.ContentBoxVO;
import com.bbb.cms.LinkVO;
import com.bbb.cms.ProductCarouselVO;
import com.bbb.cms.PromoBoxVO;
import com.bbb.cms.PromoContainerVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class RestTemplateTools extends BBBGenericService {

	private BBBCatalogTools catalogTools;
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	
	/**
	 * This method gets PromoBoxVO
	 * 
	 * @param pPromoItem
	 * @return PromoBoxVO
	 */
	public PromoBoxVO getPromoBoxVO(RepositoryItem pPromoItem){
		if(isLoggingDebug()){
			logDebug("Entering  RestTemplateTools method [getPromoBoxVO ] ");
		}
		PromoBoxVO pVO = new PromoBoxVO();
		
		//sets PromoBoxVO details
		pVO.setId((String) pPromoItem.getRepositoryId());
		pVO.setImageAltText((String) pPromoItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_ALT_TEXT));
		pVO.setImageLink((String) pPromoItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_LINK));
		pVO.setImageMapName((String) pPromoItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_MAP_NAME));
		pVO.setImageURL((String) pPromoItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_IMAGE_URL));
		pVO.setHeight((String) pPromoItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_HEIGHT));
		pVO.setWidth((String) pPromoItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROMOBOX_WIDTH));
		if(isLoggingDebug()){
			logDebug("Exit RestTemplateTools method [getPromoBoxVO ]");
		}
		return pVO;
	}
	
	/**
	 *  This method gets ProductCarouselVO
	 *  
	 * @param productCarousel
	 * @param pSiteId
	 * @return ProductCarouselVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	public ProductCarouselVO getProductCarouselVO(RepositoryItem productCarousel, String pSiteId)  {
		if(isLoggingDebug()){
			logDebug("Entering  RestTemplateTools method [getProductCarouselVO ] ");
		}
		ProductCarouselVO productCarouselVO = new ProductCarouselVO();
		
		//sets ProductCarouselVO details
		productCarouselVO.setCarouselId(productCarousel.getRepositoryId());
		productCarouselVO.setTitle((String) productCarousel.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_CAROUSEL_TITLE));
		productCarouselVO.setFlipTime((Integer) productCarousel.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_CAROUSEL_FLIP_TIME));
		productCarouselVO.setNoOfProductsInProductCarousel((String) productCarousel.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_NO_OF_PRODUCTS_IN_CAROUSAL));
		
		// Get Product Listing
		List<RepositoryItem> productItems = (List<RepositoryItem>) productCarousel.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_CAROUSEL_PRODUCTS);
		if(null != productItems && !productItems.isEmpty()){
			List<ProductVO> pList = new ArrayList<ProductVO>();
			for(RepositoryItem pRepItem: productItems){
				try {
					pList.add((this.getCatalogTools().getProductDetails(pSiteId, pRepItem.getRepositoryId(), true)));
				} catch (BBBSystemException e) {
					logDebug("System Exception while fetching Product Details for Product ID: " +  pRepItem.getRepositoryId(), e);
				} catch (BBBBusinessException e) {
					logDebug("Business Exception while fetching Product Details for Product ID: " +  pRepItem.getRepositoryId(), e);
				}
			}
			productCarouselVO.setProductList(pList);
		}
		if(isLoggingDebug()){
			logDebug("Exit RestTemplateTools method [getProductCarouselVO ]");
		}
		return productCarouselVO;
	}
	
	/**
	 * This method gets BannerVO
	 * 
	 * @param banner
	 * @return BannerVO
	 */
	public BannerVO getBannerVO(RepositoryItem banner) {
		if(isLoggingDebug()){
			logDebug("Entering  RestTemplateTools method [getBannerVO ] ");
		}
		BannerVO bVO = new BannerVO();
		
		//sets BannerVO details
		bVO.setBannerID(banner.getRepositoryId());
		bVO.setBannerText((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_TEXT));
		bVO.setBannerLink((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_LINK));
		bVO.setBannerBackground((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_BACKGROUND));
		bVO.setBannerForeground((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_FOREGROUND));
		bVO.setBannerFontSize((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_FONTSIZE));
		bVO.setBannerFontWeight((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_FONTWEIGHT));
		if(isLoggingDebug()){
			logDebug("Exit RestTemplateTools method [getBannerVO ]");
		}
		return bVO;
	}
	
	/**
	 * This method gets CarouselVO
	 * 
	 * @param pItem
	 * @return CarouselVO
	 */
	@SuppressWarnings("unchecked")
	public CarouselVO getCarouselVO(RepositoryItem pItem){
		
		if(isLoggingDebug()){
			logDebug("Entering  RestTemplateTools method [getCarouselVO ] ");
		}
		CarouselVO pVO = new CarouselVO();
		
		//sets CarouselVO details
		pVO.setCarouselId((String) pItem.getRepositoryId());
		pVO.setFlipTime((Integer) pItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_CAROUSEL_FLIP_TIME));
		pVO.setTitle((String) pItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_CAROUSEL_TITLE));
		pVO.setCarouselImageCount((String) pItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_IMAGE_COUNT));
		
		List<RepositoryItem> promoItems = (List<RepositoryItem>) pItem.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_CAROUSEL_IMAGES);
		
		if(null != promoItems && !promoItems.isEmpty()){
			List<PromoBoxVO> pList = new ArrayList<PromoBoxVO>();
			for(RepositoryItem pRepItem: promoItems){
				pList.add(getPromoBoxVO(pRepItem));
			}
			pVO.setImages(pList);
		}
		if(isLoggingDebug()){
			logDebug("Exit RestTemplateTools method [getCarouselVO ]");
		}
		return pVO;
	}
	
	/**
	 * This method gets CarouselVO
	 * 
	 * @param pItem
	 * @return CarouselVO
	 */
	@SuppressWarnings("unchecked")
	public ContentBoxVO getContentBoxVO(RepositoryItem pItem){
		ContentBoxVO cVO = new ContentBoxVO();
		
		//sets CarouselVO details
		cVO.setContentBoxId(pItem.getRepositoryId());
		cVO.setImagePosition((String) pItem.getPropertyValue(BBBCmsConstants.REST_CONTENTBOX_IMAGE_POSITION));
		cVO.setContent((String) pItem.getPropertyValue(BBBCmsConstants.REST_CONTENTBOX_CONTENT));
				
		// Get Promo Item
		RepositoryItem promoItem = (RepositoryItem) pItem.getPropertyValue(BBBCmsConstants.REST_STATIC_COLLECTION_TEMPLATE_PROPERTY_CAROUSEL);
		if(null != promoItem){
			cVO.setImageBox(getPromoBoxVO(promoItem));
		}
		
		return cVO;
	}
	
	/**
	 * This method gets the details for Home Promo Tier layout
	 * 
	 * @param homePromoTierLayOut
	 * @return PromoContainerVO
	 */
	public PromoContainerVO getHomePromoTierLayOut(
			RepositoryItem homePromoTierLayOut) {
		if(isLoggingDebug()){
			logDebug("Entering  RestTemplateTools method [getHomePromoTierLayOut ] ");
		}
		PromoContainerVO promoVO = new PromoContainerVO();
		
		//sets PromoContainerVO details
		promoVO.setPromoId(homePromoTierLayOut.getRepositoryId());
		
		// Get Image Box 1 Item
		RepositoryItem imageBox1 = (RepositoryItem) homePromoTierLayOut.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROPERTY_IMAGE_BOX1);
		if(null != imageBox1){
			promoVO.setImageBox1(getPromoBoxVO(imageBox1));
		}
		
		// Get Image Box 1 Item
		RepositoryItem imageBox2 = (RepositoryItem) homePromoTierLayOut.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_PROPERTY_IMAGE_BOX2);
		if(null != imageBox2){
			promoVO.setImageBox2(getPromoBoxVO(imageBox2));
		}
		if(isLoggingDebug()){
			logDebug("Exit RestTemplateTools method [getHomePromoTierLayOut ] :: ");
		}
		
		return promoVO;
	}

	/**
	 * This method gets CategoryContainerVO
	 * 
	 * @param categoryContainer
	 * @param pSiteId
	 * @return CategoryContainerVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	public CategoryContainerVO getCategoryContainerVO(RepositoryItem categoryContainer, String pSiteId) {
		if(isLoggingDebug()){
			logDebug("Entering  RestTemplateTools method [getCategoryContainerVO ] ");
		}
		CategoryContainerVO categoryContainerVO = new CategoryContainerVO();
		
		//sets CategoryContainerVO details
		categoryContainerVO.setCategoryId(categoryContainer.getRepositoryId());
		categoryContainerVO.setFlipTime((Integer) categoryContainer.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_CAROUSEL_FLIP_TIME));
		categoryContainerVO.setTitle((String) categoryContainer.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_CAROUSEL_TITLE));
		
		// Get Category Listing
		List<RepositoryItem> categoryItems = (List<RepositoryItem>) categoryContainer.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_CATEGORY_IN_CONTAINER);
		if(null != categoryItems && !categoryItems.isEmpty()){
			List<CategoryVO> cList = new ArrayList<CategoryVO>();
			for(RepositoryItem pRepItem: categoryItems){
				CategoryVO categoryVO = null;
				try {
					categoryVO = (this.getCatalogTools().getCategoryDetail(pSiteId, pRepItem.getRepositoryId(),false));
				} catch (BBBSystemException e) {
					logDebug("System Exception while fetching Category Details for Category ID: " +  pRepItem.getRepositoryId(), e);
				} catch (BBBBusinessException e) {
					logDebug("Business Exception while fetching Category Details for Category ID: " +  pRepItem.getRepositoryId(), e);
				}
				if(categoryVO != null)
				{
					//child products not required
					categoryVO.setChildProducts(null);
					
					//Not required child products for subcategories
					List<CategoryVO> subCategories = categoryVO.getSubCategories();
					if(subCategories != null)
					{
						for(CategoryVO subCat: subCategories){
						if(subCat!=null){
							subCat.setChildProducts(null);
						}
						}
					}
					cList.add(categoryVO);
				}
			}
			categoryContainerVO.setCategoryInCatContainer(cList);
		}	
			
		categoryContainerVO.setNoOfCatInCatContainer((String) categoryContainer.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_NO_OF_CATEGORY_IN_CATAGORY_CONTAINER));
		if(isLoggingDebug()){
			logDebug("Exit RestTemplateTools method [getCategoryContainerVO ] :: " + categoryContainerVO.toString());
		}
		
		return categoryContainerVO;
	}

	
	/**
	 * This method gets LinkVO
	 * 
	 * @param banner
	 * @return
	 */
	public LinkVO getLinkVO(RepositoryItem banner) {
		LinkVO linkVO = new LinkVO();
		
		//sets LinkVO details
		linkVO.setBannerID(banner.getRepositoryId());
		linkVO.setBannerText((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_TEXT));
		linkVO.setBannerLink((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_LINK));
		linkVO.setBannerBackground((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_BACKGROUND));
		linkVO.setBannerForeground((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_FOREGROUND));
		linkVO.setBannerFontSize((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_FONTSIZE));
		linkVO.setBannerFontWeight((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_FONTWEIGHT));
		linkVO.setHiddenFlag((boolean) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_HIDDENFLAG));
		linkVO.setLinkCode((String) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_LINKCODE));
		
		/* below condition checks for sub-links associated with a link and calls this method again to get entire details of sub-links :: story :: BBBH-165 */
		List<RepositoryItem> subBannerList = (List<RepositoryItem>) banner.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_MULTILINKS);
		if(!BBBUtility.isListEmpty(subBannerList)){
			List<LinkVO> pList = new ArrayList<LinkVO>();
			for(RepositoryItem pRepItem: subBannerList){
				if(pRepItem != null){
					pList.add(getLinkVO(pRepItem));
				}
			}
			linkVO.setMultiLinksList(pList);
		}		
		
		return linkVO;
	}
	
	
}
