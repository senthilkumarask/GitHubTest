package com.bbb.order.bean;

import java.util.LinkedList;
import java.util.List;

import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.repository.RepositoryItem;

/**
 * This class is used by the DetailedItemPriceInfo.
 * 
 * @author Sapient Consulting Pvt Ltd.
 * 
 */
public class BBBDetailedItemPriceInfo extends DetailedItemPriceInfo {

	public BBBDetailedItemPriceInfo() {
		//defaulf constructor
	}

	/**
	 * @param pDetailedItemPriceInfo
	 */
	public BBBDetailedItemPriceInfo(BBBDetailedItemPriceInfo pDetailedItemPriceInfo) {
		super.copyDetailProperties(pDetailedItemPriceInfo);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8106037571243318500L;
	private List<RepositoryItem> dsLineItemTaxInfos;

	/**
	 * @return the dsLineItemTaxInfos
	 */
	public List<RepositoryItem> getDsLineItemTaxInfos() {
		if (null == dsLineItemTaxInfos) {
			dsLineItemTaxInfos = new LinkedList<RepositoryItem>();
		}
		return dsLineItemTaxInfos;
	}

	/**
	 * @param dsLineItemTaxInfos
	 *            the dsLineItemTaxInfos to set
	 */
	public void setDsLineItemTaxInfos(List<RepositoryItem> dsLineItemTaxInfos) {
		this.dsLineItemTaxInfos = dsLineItemTaxInfos;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(System.getProperty("line.separator"));
		sb.append("  dsLineItemTaxInfos: ");
		sb.append(getDsLineItemTaxInfos());
		return sb.toString();
	}
}
