package com.bbb.cms;

import java.io.Serializable;

public class PromoContainerVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String promoId;
	private PromoBoxVO imageBox1;
	private PromoBoxVO imageBox2;
	
	/**
	 * @return the promoId
	 */
	public String getPromoId() {
		return promoId;
	}
	/**
	 * @param promoId the promoId to set
	 */
	public void setPromoId(String promoId) {
		this.promoId = promoId;
	}
	/**
	 * @return the imageBox1
	 */
	public PromoBoxVO getImageBox1() {
		return imageBox1;
	}
	/**
	 * @param imageBox1 the imageBox1 to set
	 */
	public void setImageBox1(PromoBoxVO imageBox1) {
		this.imageBox1 = imageBox1;
	}
	/**
	 * @return the imageBox2
	 */
	public PromoBoxVO getImageBox2() {
		return imageBox2;
	}
	/**
	 * @param imageBox2 the imageBox2 to set
	 */
	public void setImageBox2(PromoBoxVO imageBox2) {
		this.imageBox2 = imageBox2;
	}
}
