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
public class BBBMexicoCurrencyTagConvertor extends CurrencyTagConverter {
	
	public String getName() {
		return "mxCurrency";
	}
	
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

			
			String country = BBBInternationalShippingConstants.COUNTRY_MEXICO;
			String currency = BBBInternationalShippingConstants.CURRENCY_MEXICO;
			boolean formattedPriceRequired = true;
			if (!BBBUtility.isEmpty((String) pAttributes.getProperty("formatPrice"))) {
				formattedPriceRequired = Boolean.getBoolean((String) pAttributes.getProperty("formatPrice"));
			}

			if (pValue instanceof Double) {
				String formattedValue = getBbbCustomTagComponent().formatValue(Double.valueOf(pValue.toString()),currency, country,formattedPriceRequired, pAttributes);
				returnValue = formattedValue;
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
