package atg.droplet;

import java.util.Properties;

import atg.servlet.DynamoHttpServletRequest;

public class BBBUnformattedCurrencyTagConvertor extends BBBCurrencyTagConvertor{
	
	private static final long serialVersionUID = 1L;

	public String getName() {
		return "unformattedCurrency";
	}
	
	public String convertObjectToString(DynamoHttpServletRequest pRequest,
			Object pValue, Properties pAttributes) throws TagConversionException {
		
		if(pValue instanceof String) {
			 pValue = Double.parseDouble(pValue.toString());
		}
		pAttributes.put("formatPrice", "false");
		return(super.convertObjectToString(pRequest, pValue, pAttributes));
	}
}