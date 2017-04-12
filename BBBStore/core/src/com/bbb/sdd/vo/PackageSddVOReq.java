package com.bbb.sdd.vo;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class PackageSddVOReq.
 *
 * @author 
 */
public class PackageSddVOReq implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The name. */
	private String name;
	
	/** The price. */
	private String price;
	
	/** The sku. */
	private String SKU;
	
	/** The weight. */
	private String weight;
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}
	
	/**
	 * Sets the price.
	 *
	 * @param price the new price
	 */
	public void setPrice(String price) {
		this.price = price;
	}
	
	/**
	 * Gets the sku.
	 *
	 * @return the sku
	 */
	public String getSKU() {
		return SKU;
	}
	
	/**
	 * Sets the sku.
	 *
	 * @param sKU the new sku
	 */
	public void setSKU(String sKU) {
		SKU = sKU;
	}
	
	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	public String getWeight() {
		return weight;
	}
	
	/**
	 * Sets the weight.
	 *
	 * @param weight the new weight
	 */
	public void setWeight(String weight) {
		this.weight = weight;
	}
	

public String toString(){
		
		StringBuffer packageSddVOReq = new StringBuffer("");
		packageSddVOReq.append("name: "+this.name + "|");
		packageSddVOReq.append("price: "+this.price + "|");
		packageSddVOReq.append("SKU: "+this.SKU + "|");
		packageSddVOReq.append("weight: "+this.weight + "|");
		return packageSddVOReq.toString();
		
	}
	
}