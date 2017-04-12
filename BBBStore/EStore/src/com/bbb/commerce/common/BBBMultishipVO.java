package com.bbb.commerce.common;

import java.io.Serializable;

public class BBBMultishipVO implements Serializable {

	/*
	 * ===================================================== 
	 * * MEMBER VARIABLES
	 * =====================================================
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isDefaultMultiship;
	private boolean showSingleShipLink;
	/**
	 * @return the isDefaultMultiship
	 */
	public boolean isDefaultMultiship() {
		return isDefaultMultiship;
	}
	/**
	 * @param isDefaultMultiship the isDefaultMultiship to set
	 */
	public void setDefaultMultiship(boolean isDefaultMultiship) {
		this.isDefaultMultiship = isDefaultMultiship;
	}
	/**
	 * @return the showSingleShipLink
	 */
	public boolean isShowSingleShipLink() {
		return showSingleShipLink;
	}
	/**
	 * @param showSingleShipLink the showSingleShipLink to set
	 */
	public void setShowSingleShipLink(boolean showSingleShipLink) {
		this.showSingleShipLink = showSingleShipLink;
	}

}
