package com.bbb.kickstarters;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.ImageVO;

public class TopSkuProductVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productId;
	private String name;
	private Map<String, List<AttributeVO>> attributesList;
	private ImageVO productImages;
	private boolean collection;
	private BazaarVoiceProductVO bvProductVO;
	private String seoUrl;
	
	public String getSeoUrl() {
		return seoUrl;
	}
	public void setSeoUrl(String seoUrl) {
		this.seoUrl = seoUrl;
	}
	public BazaarVoiceProductVO getBvProductVO() {
		return bvProductVO;
	}
	public void setBvProductVO(BazaarVoiceProductVO bvProductVO) {
		this.bvProductVO = bvProductVO;
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
	
}
