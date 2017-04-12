package com.bbb.store.catalog;

import java.util.ArrayList;
import java.util.List;

import com.bbb.store.catalog.vo.FilteredProductDetailVO;
import com.bbb.store.catalog.vo.FilteredSKUDetailVO;
import com.bbb.store.catalog.vo.FilteredStyleProductDetailVO;
import com.bbb.store.catalog.vo.FilteredStyleSkuDetailVO;
import com.bbb.store.catalog.vo.ProductGSVO;
import com.bbb.store.catalog.vo.SkuGSVO;

public class BBBGSFilteredResponse {

	/**
	 * @param source
	 * @param destination
	 */
	public static FilteredProductDetailVO getFilteredProductDetails(ProductGSVO source){
		FilteredProductDetailVO destination = new FilteredProductDetailVO();
		if(source != null && destination != null){
			destination.setGoodToKnow(source.getGoodToKnow());
			destination.setSeoUrl(source.getSeoUrl());
			destination.setOtherMedia(source.getOtherMedia());
			destination.setSkuRollupMap(source.getProductRestVO().getSkuRollupMap());
			destination.setProductRollupVO(source.getProductRestVO().getProductRollupVO());
			destination.setProductAllAttributesVO(source.getProductRestVO().getProductAllAttributesVO());
			destination.setProductBrand(source.getProductRestVO().getProductVO().getProductBrand());
			destination.setCollection(source.getProductRestVO().getProductVO().isCollection());
			destination.setSalePriceRangeDescription(source.getProductRestVO().getProductVO().getSalePriceRangeDescription());
			destination.setRollupAttributes(source.getProductRestVO().getProductVO().getRollupAttributes());
			destination.setPrdRelationRollup(source.getProductRestVO().getProductVO().getPrdRelationRollup());
			destination.setBvReviews(source.getProductRestVO().getProductVO().getBvReviews());
			destination.setZoomAvailable(source.getProductRestVO().getProductVO().isZoomAvailable());
			destination.setShopGuideId(source.getProductRestVO().getProductVO().getShopGuideId());
			destination.setUpcCode(source.getProductRestVO().getProductVO().getUpcCode());
			destination.setmGiftCertProduct(source.getProductRestVO().getProductVO().getGiftCertProduct());
			destination.setDisabled(source.getProductRestVO().getProductVO().getDisabled());
			destination.setDepartment(source.getProductRestVO().getProductVO().getDepartment());
			destination.setAttributesList(source.getProductRestVO().getProductVO().getAttributesList());
			destination.setProductId(source.getProductRestVO().getProductVO().getProductId());
			destination.setChildSKUs(source.getProductRestVO().getProductVO().getChildSKUs());
			destination.setPriceRangeDescription(source.getProductRestVO().getProductVO().getPriceRangeDescription());
			destination.setName(source.getProductRestVO().getProductVO().getName());
			destination.setShortDescription(source.getProductRestVO().getProductVO().getShortDescription());
			destination.setLongDescription(source.getProductRestVO().getProductVO().getLongDescription());

			destination.setProductImageMap(source.getProductImageMap());
			destination.setPrimaryCategory(source.getPrimaryCategory());
			destination.setProductLowPrice(source.getProductLowPrice());
			destination.setProductHighPrice(source.getProductHighPrice());
			destination.setProductLowSalePrice(source.getProductLowSalePrice());
			destination.setProductHighSalePrice(source.getProductHighSalePrice());
			destination.setSiblingProducts(getSiblingProductDetails(source));
			
		}
		return destination;
	}
	
	
	/**
	 * @param source
	 * @param destination
	 */
	public static FilteredStyleProductDetailVO getFilteredStyleProductDetails(ProductGSVO source){
		FilteredStyleProductDetailVO destination = new FilteredStyleProductDetailVO();
		if(source != null && destination != null){
			destination.setProductId(source.getProductRestVO().getProductVO().getProductId());
			destination.setChildSKUs(source.getProductRestVO().getProductVO().getChildSKUs());
			destination.setPriceRangeDescription(source.getProductRestVO().getProductVO().getPriceRangeDescription());
			destination.setName(source.getProductRestVO().getProductVO().getName());
			destination.setShortDescription(source.getProductRestVO().getProductVO().getShortDescription());
			destination.setLongDescription(source.getProductRestVO().getProductVO().getLongDescription());

			destination.setProductImageMap(source.getProductImageMap());
			destination.setPrimaryCategory(source.getPrimaryCategory());
		}
		return destination;
	}
	
	
	/**
	 * @param productVO
	 * @return
	 */
	public static List<FilteredProductDetailVO> getSiblingProductDetails(ProductGSVO productVO){
		List<FilteredProductDetailVO> filteredSbilings = null;
		if(productVO != null && productVO.getSiblingProducts() != null && productVO.getSiblingProducts().size() > 0){
			filteredSbilings = new ArrayList<FilteredProductDetailVO>();
			List<ProductGSVO> siblings = productVO.getSiblingProducts();
			for(ProductGSVO product: siblings){
				FilteredProductDetailVO filterProduct = new FilteredProductDetailVO();	
				filterProduct = getFilteredProductDetails(product);
				filteredSbilings.add(filterProduct);
			}
		}
		return filteredSbilings;
	}
	
	
	/**
	 * @param source
	 * @param destination
	 */
	public static void getFilteredSkuDetails(SkuGSVO source, FilteredSKUDetailVO destination){
		
		destination.setDisplayName(source.getSkuRestVO().getSkuVO().getDisplayName());
		destination.setActiveFlag(source.getSkuRestVO().getSkuVO().isActiveFlag());
		destination.setColor(source.getSkuRestVO().getSkuVO().getColor());
		destination.setUpc(source.getSkuRestVO().getSkuVO().getUpc());
		destination.setSkuInStock(source.getSkuRestVO().getSkuVO().isSkuInStock());
		destination.setSkuId(source.getSkuRestVO().getSkuVO().getSkuId());
		destination.setSkuAttributes(source.getSkuRestVO().getSkuVO().getSkuAttributes());
		destination.setSize(source.getSkuRestVO().getSkuVO().getSize());
		destination.setDescription(source.getSkuRestVO().getSkuVO().getDescription());
		destination.setLongDescription(source.getSkuRestVO().getSkuVO().getLongDescription());
		destination.setInventoryStatus(source.getSkuRestVO().getInventoryStatus());
		destination.setListPrice(source.getSkuRestVO().getListPrice());
		destination.setSalePrice(source.getSkuRestVO().getSalePrice());
		destination.setSkuAllAttributeVO(source.getSkuRestVO().getSkuAllAttributeVO());
		destination.setSkuImageMap(source.getSkuImageMap());
		destination.setSkuInventory(source.getSkuInventory());
		
	}
	
	/**
	 * @param source
	 * @param destination
	 */
	public static FilteredStyleSkuDetailVO getFilteredStyleSkuDetails(SkuGSVO source){
		FilteredStyleSkuDetailVO destination = new FilteredStyleSkuDetailVO();
		if(source != null && destination != null){
			
			destination.setSkuId(source.getSkuRestVO().getSkuVO().getSkuId());
			destination.setDisplayName(source.getSkuRestVO().getSkuVO().getDisplayName());
			destination.setDescription(source.getSkuRestVO().getSkuVO().getDescription());
			destination.setLongDescription(source.getSkuRestVO().getSkuVO().getLongDescription());
			destination.setColor(source.getSkuRestVO().getSkuVO().getColor());
			destination.setSize(source.getSkuRestVO().getSkuVO().getSize());
			destination.setUpc(source.getSkuRestVO().getSkuVO().getUpc());
			destination.setListPrice(source.getSkuRestVO().getListPrice());
			destination.setSalePrice(source.getSkuRestVO().getSalePrice());
			destination.setSkuImageMap(source.getSkuImageMap());
		}
		return destination;
	}
	
	/**
	 * Method to get Filtered Sku Details
	 * @param source
	 * @return
	 */
	public static FilteredSKUDetailVO filteredSkuDetails(SkuGSVO source){
		FilteredSKUDetailVO destination = new FilteredSKUDetailVO();
		getFilteredSkuDetails(source, destination);
		return destination;
	}
	
}
