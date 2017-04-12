package com.bbb.account.vo.order;

import java.io.Serializable;
import java.util.List;

/**
 * Payments.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */

public class Payments implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<GiftCard> giftCards;

    private CreditCard creditCard;

	/**
	 * @return the creditCard
	 */
	public CreditCard getCreditCard() {
		return creditCard;
	}

	/**
	 * @param creditCard the creditCard to set
	 */
	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	/**
	 * @return the giftCards
	 */
	public List<GiftCard> getGiftCards() {
		return giftCards;
	}

	/**
	 * @param giftCards the giftCards to set
	 */
	public void setGiftCards(List<GiftCard> giftCards) {
		this.giftCards = giftCards;
	}

}
