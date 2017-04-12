package com.bbb.vo.wishlist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * detail of wishlist items
 */
public class WishListVO{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * wishlist item VO 
	 */
	@SuppressWarnings("rawtypes")
	private List wishListItems;
	
	/**
	 * map of wish list product detail per product id 
	 */
	private Map<String,WishListProductVO> prodPropMap=new HashMap<String,WishListProductVO>();
	
	/**
	 * map of wish list sku detail per sku id
	 */
	private Map<String,WishListSkuDetailVO> skuPropMap=new HashMap<String,WishListSkuDetailVO>();
	
	/**
	 *LTL Wish List DSL info
	 */
	private LTLWishListDslVO savedDsl;
	
	/**
	 * @return the wishListItems
	 */
	@SuppressWarnings("rawtypes")
	public List getWishListItems() {
		return wishListItems;
	}
	/**
	 * @param wishListItems the wishListItems to set
	 */
	@SuppressWarnings("rawtypes")
	public void setWishListItems(List wishListItems) {
		this.wishListItems = wishListItems;
	}
	/**
	 * @return the prodPropMap
	 */
	public Map<String, WishListProductVO> getProdPropMap() {
		return prodPropMap;
	}
	/**
	 * @param prodPropMap the prodPropMap to set
	 */
	public void setProdPropMap(Map<String, WishListProductVO> prodPropMap) {
		this.prodPropMap = prodPropMap;
	}
	/**
	 * @return the skuPropMap
	 */
	public Map<String, WishListSkuDetailVO> getSkuPropMap() {
		return skuPropMap;
	}
	/**
	 * @param skuPropMap the skuPropMap to set
	 */
	public void setSkuPropMap(Map<String, WishListSkuDetailVO> skuPropMap) {
		this.skuPropMap = skuPropMap;
	}
	public LTLWishListDslVO getSavedDsl() {
		return savedDsl;
	}
	public void setSavedDsl(LTLWishListDslVO savedDsl) {
		this.savedDsl = savedDsl;
	}




}
