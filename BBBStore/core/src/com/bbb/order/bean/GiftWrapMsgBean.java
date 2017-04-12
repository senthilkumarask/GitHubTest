package com.bbb.order.bean;

import java.io.Serializable;



/**
 * GiftWrapMsgBean.
 * @author ATG
 * @version $Revision: #2 $
 */
public class GiftWrapMsgBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String giftWrapMessage;

	public String getGiftWrapMessage() {
		if(giftWrapMessage != null){
			return insertPeriodically(giftWrapMessage, "</br>",
					40);
		} else {
			return null;
		}
	}
	public void setGiftWrapMessage(String giftWrapMessage) {
		this.giftWrapMessage = giftWrapMessage;
	}
	
	private static String insertPeriodically(String text, String insert,
			int period) {
		StringBuilder builder = new StringBuilder(text.length()
				+ insert.length() * (text.length() / period) + 1);

		int index = 0;
		String prefix = "";
		while (index < text.length()) {
			// Don't put the insert in the very first iteration.
			// This is easier than appending it *after* each substring
			builder.append(prefix);
			prefix = insert;
			builder.append(text.substring(index,
					Math.min(index + period, text.length())));
			index += period;
		}
		return builder.toString();
	}
}