package atg.droplet;

import java.util.Properties;

import atg.servlet.DynamoHttpServletRequest;

public class BBBDefaultCurrencyTagConverter extends CurrencyTagConverter {
	
	public String getName() {
		return "defaultCurrency";
	}
	
	public String convertObjectToString(DynamoHttpServletRequest pRequest,
			Object pValue, Properties pAttributes) throws TagConversionException {
		
		return(super.convertObjectToString(pRequest, pValue, pAttributes));
	}
}