package com.bbb.internationalshipping.utils;

import java.util.List;
import java.util.Map;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.vo.BBBInternationalContextVO;


/**
 * This interface models around building of API to be used
 * in International Shipping.
 *
 */
public interface BBBInternationalShippingBuilder {
	
	public BBBInternationalContextVO buildContextFromIP (final String ipAddress) throws BBBSystemException, BBBBusinessException;
	public List<BBBInternationalContextVO> buildContextAll() throws BBBSystemException, BBBBusinessException;
	public BBBInternationalContextVO buildContextBasedOnCountryCode(final String countryCode) throws BBBSystemException, BBBBusinessException;
	public Map<String,String> buildCurrencyMap(final List<BBBInternationalContextVO> internationalContextVOs);
	public String getMerchantId() throws BBBSystemException, BBBBusinessException;
}
