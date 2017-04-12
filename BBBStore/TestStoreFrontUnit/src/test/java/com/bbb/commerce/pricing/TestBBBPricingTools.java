package com.bbb.commerce.pricing;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import atg.commerce.CommerceException;
import atg.repository.RepositoryException;
import atg.service.idgen.IdGeneratorException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BaseTestCase;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.internationalshipping.vo.BBBInternationalCurrencyDetailsVO;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.output.BBBCustomTagComponent;


public class TestBBBPricingTools extends BaseTestCase{
	@Spy BBBPricingTools pricingTools = new BBBPricingTools();
	@Mock BBBCustomTagComponent bbbcustomcomponent;
	@Spy BBBLocalCacheContainer localcache=new BBBLocalCacheContainer();
	@Spy BBBSessionBean mockSession=new BBBSessionBean();
	@Spy BBBInternationalCurrencyDetailsVO currencyDetailsVO=new BBBInternationalCurrencyDetailsVO();
	
	@Override
	public void setUp() {
		super.setUp();
		
		
	}
	@Test
	public void testCheckMexicoShippingPriceCalculation() throws IOException, BBBSystemException, BBBBusinessException, CommerceException, IdGeneratorException, RepositoryException, ServletException, RunProcessException {
		DynamoHttpServletRequest pRequest = getDynHttpRequest();
		prepareServletUtil();
		doReturn(mockSession).when(pRequest).resolveName(BBBCoreConstants.SESSION_BEAN);
		
		mockSession.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT,"MX");
		mockSession.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY,"MXN");
		bbbcustomcomponent.setLocalcache(localcache);
		pricingTools.setBbbcustomcomponent(bbbcustomcomponent);
		currencyDetailsVO.setFxRate(16.33);
		currencyDetailsVO.setScale(2);
		currencyDetailsVO.setCurrencySymbol("MXN");
		when(bbbcustomcomponent.getCurrencyDetailsVO(anyString(),anyString())).thenReturn(currencyDetailsVO);
		double mexicoOrderSubtotal=950;
		BigDecimal val=new BigDecimal(mexicoOrderSubtotal/16.33);
		val = val.divide(BigDecimal.ONE, 2,
				BigDecimal.ROUND_HALF_UP);
		double expectedamount = val.doubleValue();
		double actualAmount=pricingTools.mexicoOrderShippingCalculation(mexicoOrderSubtotal);
		assertEquals(expectedamount, actualAmount);
		mockSession.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT,"IN");
		mockSession.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY,"INR");
		currencyDetailsVO = new BBBInternationalCurrencyDetailsVO();
		currencyDetailsVO.setFxRate(46.33);
		currencyDetailsVO.setScale(0);
		currencyDetailsVO.setCurrencySymbol("INR");
		when(bbbcustomcomponent.getCurrencyDetailsVO(anyString(),anyString())).thenReturn(currencyDetailsVO);
		double inrOrderSubtotal=50.23;
		expectedamount = inrOrderSubtotal;
		actualAmount=pricingTools.mexicoOrderShippingCalculation(inrOrderSubtotal);
		assertEquals(expectedamount, actualAmount);
	
}
}
