package com.bbb.vo.wishlist;

import java.util.List;

public class LTLWishListDslVO {

	private List<String> savedDslOptions;
	private String skuId;

	public List<String> getSavedDslOptions() {
		return savedDslOptions;
	}

	public void setSavedDslOptions(List<String> savedDslOptions) {
		this.savedDslOptions = savedDslOptions;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
}
