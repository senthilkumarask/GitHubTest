package com.bbb.cms;

import java.io.Serializable;

public class PromoBoxContentVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String promoBoxContent;
	private String promoBoxCssFilePath;
	private String promoBoxJsFilePath;
	
	/**
	 * @return the promoBoxContent
	 */
	public String getPromoBoxContent() {
		return promoBoxContent;
	}
	/**
	 * @param promoBoxContent to set
	 */
	public void setPromoBoxContent(String promoBoxContent) {
		this.promoBoxContent = promoBoxContent;
	}
	/**
	 * @return the promoBoxCssFilePath
	 */
	public String getPromoBoxCssFilePath() {
		return promoBoxCssFilePath;
	}
	/**
	 * @param promoBoxCssFilePath to set
	 */
	public void setPromoBoxCssFilePath(String promoBoxCssFilePath) {
		this.promoBoxCssFilePath = promoBoxCssFilePath;
	}
	
	/**
	 * @return the promoBoxJsFilePath
	 */
	public String getPromoBoxJsFilePath() {
		return promoBoxJsFilePath;
	}
	/**
	 * @param promoBoxJsFilePath to set
	 */
	public void setPromoBoxJsFilePath(String promoBoxJsFilePath) {
		this.promoBoxJsFilePath = promoBoxJsFilePath;
	}
	
	
	
	
}
