package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;


public class BBBProductList implements Serializable{

	private static final long serialVersionUID = 1L;
	private long bbbProductCount;
	private List<BBBProduct> bbbProducts;
	private Map<String, List<BBBProduct>> otherResults;
	private long recordOffset;
	private String   negativeMatchQuery;
	private long onlineProductInvCount;
	private long storeInvCount = 0L;

	public long getStoreInvCount() {
		return storeInvCount;
	}

	public void setStoreInvCount(long storeInvCount) {
		this.storeInvCount = storeInvCount;
	}

	public String getNegativeMatchQuery() {
		return negativeMatchQuery;
	}

	public void setNegativeMatchQuery(String negativeMatchQuery) {
		this.negativeMatchQuery = negativeMatchQuery;
	}


	public long getOnlineProductInvCount() {
		return onlineProductInvCount;
	}

	public void setOnlineProductInvCount(long onlineProductInvCount) {
		this.onlineProductInvCount = onlineProductInvCount;
	}


	public BBBProductList() {
		bbbProducts = new ArrayList<BBBProduct>();
	}

	public long getBBBProductCount() {
		return bbbProductCount;
	}

	public void setBBBProductCount(final long bbbProductCount) {
		this.bbbProductCount = bbbProductCount;
	}

	public List<BBBProduct> getBBBProducts() {
		return bbbProducts;
	}

	/**
	 * @return the otherResults
	 */
	public Map<String, List<BBBProduct>> getOtherResults() {
		return otherResults;
	}

	/**
	 * @param otherResults the otherResults to set
	 */
	public void setOtherResults(Map<String, List<BBBProduct>> otherResults) {
		this.otherResults = otherResults;
	}

	/**
	 * @return the recordOffset
	 */
	public long getRecordOffset() {
		return recordOffset;
	}

	/**
	 * @param recordOffset the recordOffset to set
	 */
	public void setRecordOffset(long recordOffset) {
		this.recordOffset = recordOffset;
	}
}

