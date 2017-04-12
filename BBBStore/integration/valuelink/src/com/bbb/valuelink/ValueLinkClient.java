package com.bbb.valuelink;

import java.util.Map;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * @author vagra4
 *
 */
public interface ValueLinkClient {

	/**
	 * This method invokes the ValueLink API for Gift card related calls.
	 * 
	 * @param pGiftCardNo
	 * @param pPinNo
	 * @param pVLInputs
	 * @param pSessionBean
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String invoke(Map<String, String> mVLInputs) throws BBBSystemException, BBBBusinessException;
}
