package atg.droplet;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Properties;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.output.BBBCustomTagComponent;
import com.bbb.utils.BBBUtility;


/**
 * This class is having methods used by tag converter to parse and format
 * currency amounts.
 * 
 * @author ssha53
 */
@SuppressWarnings("serial")
public class BBBCurrencyTagConvertor extends CurrencyTagConverter {
	
	/** The bbbCustomTagComponent component instance */
	private BBBCustomTagComponent bbbCustomTagComponent = null;
	
	/**
	 * This method is used to convert currency value to string format. 
	 * 
	 * @param pRequest DynamoHttpServletRequest request           
	 * @param pValue in <code>Object</code> format
	 * @param pAttributes in <code>Properties</code> format
	 * 
	 * @return currency value in <code>String</code> format
	 * @throws TagConversionException
	 */
	@Override
	public String convertObjectToString(DynamoHttpServletRequest pRequest, Object pValue, Properties pAttributes)
			throws TagConversionException {

		String returnValue = BBBCoreConstants.BLANK;

		if (pValue == null) {
			returnValue = BBBCoreConstants.BLANK;
		} else if (pValue instanceof String) {
			returnValue = pValue.toString();
		} else {

			if (getBbbCustomTagComponent() == null) {
				setBbbCustomTagComponent((BBBCustomTagComponent) Nucleus.getGlobalNucleus().resolveName("/com/bbb/rest/output/BBBCustomTagComponent"));
			}

			BBBSessionBean sessionBeanFromReq = (BBBSessionBean) pRequest.getAttribute("sessionBean");
			if (sessionBeanFromReq == null) {
				sessionBeanFromReq = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
				pRequest.setAttribute("sessionBean", sessionBeanFromReq);
				
			}

			String country = (String) sessionBeanFromReq.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
			String currency = (String) sessionBeanFromReq.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY);
			boolean formattedPriceRequired = true;
			if (null != pAttributes && !BBBUtility.isEmpty((String) pAttributes.getProperty("formatPrice"))) {
				formattedPriceRequired = Boolean.getBoolean((String) pAttributes.getProperty("formatPrice"));
			}

			if (!BBBUtility.isEmpty(country) && !BBBUtility.isEmpty(currency)) {
				// Case: US or International for site id BedBathUS or BuyBuyBaby
				if (!country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY)) {
					if (pValue instanceof Double) {
						String formattedValue = getBbbCustomTagComponent().formatValue(Double.valueOf(pValue.toString()),currency, country,formattedPriceRequired, pAttributes);
						returnValue = formattedValue;
					}
				} else {
					if (formattedPriceRequired) {
						String convertedValue = getBbbCustomTagComponent().formatValueForUS((Double) pValue);
						if (null != pAttributes && !BBBUtility.isEmpty(pAttributes.getProperty(BBBInternationalShippingConstants.ROUND))) {
							convertedValue = getBbbCustomTagComponent().getConvertedValueForPriceRange(new BigDecimal((Double) pValue), BBBCoreConstants.DOLLAR);
						}
						returnValue = convertedValue;
					} else {
						NumberFormat format = getBbbCustomTagComponent().getCurrencyFormatter(2, (Double) pValue);
						String formattedValue = format.format((Double) pValue);
						formattedValue = formattedValue.replaceAll(BBBCoreConstants.COMMA, BBBCoreConstants.BLANK);
						returnValue = formattedValue;
					}
					boolean isFormattedWithoutCurrency = false;
					if(pAttributes!=null) {
						String withoutDoubleCurrency = (String)pAttributes.get("isFormattedWithoutCurrency");
						if(!BBBUtility.isEmpty(withoutDoubleCurrency) && withoutDoubleCurrency.equalsIgnoreCase(BBBCoreConstants.TRUE)){
							isFormattedWithoutCurrency = true;
						}
					}
					if(isFormattedWithoutCurrency){
						returnValue = getBbbCustomTagComponent().formatValueForUSWithoutCurrency((Double)pValue);
					}
				}
			} else {
				// Case: Site id BedBathCanada
				if (formattedPriceRequired) {
					String convertedValue = getBbbCustomTagComponent().formatValueForUS((Double) pValue);
					returnValue = convertedValue;
				} else {
					NumberFormat format = getBbbCustomTagComponent().getCurrencyFormatter(2, (Double) pValue);
					String formattedValue = format.format((Double) pValue);
					formattedValue = formattedValue.replaceAll(BBBCoreConstants.COMMA, BBBCoreConstants.BLANK);
					returnValue = formattedValue;
				}
				boolean isFormattedWithoutCurrency = false;
				if(pAttributes!=null && !pAttributes.isEmpty()) {
					String withoutDoubleCurrency = (String)pAttributes.get("isFormattedWithoutCurrency");
					if(!BBBUtility.isEmpty(withoutDoubleCurrency) && withoutDoubleCurrency.equalsIgnoreCase(BBBCoreConstants.TRUE)){
						isFormattedWithoutCurrency = true;
					}
				}
				if(isFormattedWithoutCurrency){
					returnValue = getBbbCustomTagComponent().formatValueForUSWithoutCurrency((Double)pValue);
				}
			}
		}
		return returnValue;
	}
	
	
	/**
	 * @return the bbbCustomTagComponent
	 */
	public BBBCustomTagComponent getBbbCustomTagComponent() {
		return bbbCustomTagComponent;
	}
	
	
	/**
	 * @param bbbCustomTagComponent the bbbCustomTagComponent to set
	 */
	public void setBbbCustomTagComponent(BBBCustomTagComponent bbbCustomTagComponent) {
		this.bbbCustomTagComponent = bbbCustomTagComponent;
	}

}
