package com.bbb.commerce.order.vo;

import java.io.Serializable;

public class OrderOmnitureVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String events;
	private String products;
	private String purchaseID;
	private String state;
	private String zip;
	private String evar12;
	private String evar14;
	private String evar16;
	private String evar17;
	private String evar19;
	private String evar20;	
	private String evar21;
	private String evar31;
	private String prop17;
	private String eVar54;
	public String getEvents() {
		return events;
	}
	public void setEvents(String events) {
		this.events = events;
	}
	public String getProducts() {
		return products;
	}
	public void setProducts(String products) {
		this.products = products;
	}
	public String getPurchaseID() {
		return purchaseID;
	}
	public void setPurchaseID(String purchaseID) {
		this.purchaseID = purchaseID;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getEvar12() {
		return evar12;
	}
	public void setEvar12(String evar12) {
		this.evar12 = evar12;
	}
	public String getEvar14() {
		return evar14;
	}
	public void setEvar14(String evar14) {
		this.evar14 = evar14;
	}
	public String getEvar16() {
		return evar16;
	}
	public void setEvar16(String evar16) {
		this.evar16 = evar16;
	}
	public String getEvar17() {
		return evar17;
	}
	public void setEvar17(String evar17) {
		this.evar17 = evar17;
	}
	public String getEvar19() {
		return evar19;
	}
	public void setEvar19(String evar19) {
		this.evar19 = evar19;
	}
	public String getEvar20() {
		return evar20;
	}
	public void setEvar20(String evar20) {
		this.evar20 = evar20;
	}
	public String getEvar21() {
		return evar21;
	}
	public void setEvar21(String evar21) {
		this.evar21 = evar21;
	}
	public String getEvar31() {
		return evar31;
	}
	public void setEvar31(String evar31) {
		this.evar31 = evar31;
	}
	public String getProp17() {
		return prop17;
	}
	public void setProp17(String prop17) {
		this.prop17 = prop17;
	}
	
	/**
	 * @return the eVar54
	 */
	public String geteVar54() {
		return eVar54;
	}
	/**
	 * @param eVar54 the eVar54 to set
	 */
	public void seteVar54(String eVar54) {
		this.eVar54 = eVar54;
	}
	@Override
	public String toString() {
		
		final StringBuffer strBuf=  new StringBuffer("OrderOmnitureVO ::");
		
		strBuf.append(" events:").append(events);
		strBuf.append("\n products :").append(products);
		strBuf.append("\n purchaseID :").append(purchaseID);
		strBuf.append("\n state :").append(state);
		strBuf.append("\n zip :").append(zip);
		strBuf.append("\n evar12 :").append(evar12);
		strBuf.append("\n evar14 :").append(evar14);
		strBuf.append("\n evar16 :").append(evar16);
		strBuf.append("\n evar17 :").append(evar17);
		strBuf.append("\n evar19 :").append(evar19);
		strBuf.append("\n evar20 :").append(evar20);
		strBuf.append("\n evar21 :").append(evar21);
		strBuf.append("\n prop17 :").append(prop17);
		strBuf.append("\n eVar54 :").append(eVar54);
		
		
		return strBuf.toString();
	}
	
}
