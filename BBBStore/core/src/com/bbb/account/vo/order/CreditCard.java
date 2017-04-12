package com.bbb.account.vo.order;

import java.io.Serializable;

/**
 * CreditCard.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */

public class CreditCard  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double debitAmt;

    private String cardNum;

    private int expMo;

    private int expYr;

    private String cardType;

	/**
	 * @return the debitAmt
	 */
	public double getDebitAmt() {
		return debitAmt;
	}

	/**
	 * @param debitAmt the debitAmt to set
	 */
	public void setDebitAmt(double debitAmt) {
		this.debitAmt = debitAmt;
	}

	/**
	 * @return the cardNum
	 */
	public String getCardNum() {
		return cardNum;
	}

	/**
	 * @param cardNum the cardNum to set
	 */
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	/**
	 * @return the expMo
	 */
	public int getExpMo() {
		return expMo;
	}

	/**
	 * @param expMo the expMo to set
	 */
	public void setExpMo(int expMo) {
		this.expMo = expMo;
	}

	/**
	 * @return the expYr
	 */
	public int getExpYr() {
		return expYr;
	}

	/**
	 * @param expYr the expYr to set
	 */
	public void setExpYr(int expYr) {
		this.expYr = expYr;
	}

	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * @param cardType the cardType to set
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

}
