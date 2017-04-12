package com.bbb.commerce.order;


/**
 * Interface to hold Pay at RegisterProcessor  payment information.
 *
 */
public interface PayAtRegisterProcessor {
	
	/**
	 * abstract method
	 * @param pPaymentInfo
	 * @return
	 */
	PayAtRegisterStatus authorize(PayAtRegisterBeanInfo pPaymentInfo);

}
