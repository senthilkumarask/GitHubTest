package atg.droplet;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

public class BBBFormatedPriceTagConvertor extends BBBCurrencyTagConvertor {

	private static final long serialVersionUID = 1L;
	private static final String spanPreCurrency= "<span aria-hidden='true'>";
	private static final String spanClosing ="</span>";
	
	
	
	public String getName() {
		return "formattedPrice";
	}
	/*
	 * this convertor is used to add markups for currency & price for PDP page.
	 * @see atg.droplet.BBBCurrencyTagConvertor#convertObjectToString(atg.servlet.DynamoHttpServletRequest, java.lang.Object, java.util.Properties)
	 */
	public String convertObjectToString(DynamoHttpServletRequest pRequest,
			Object pValue, Properties pAttributes) throws TagConversionException {
		

		StringBuilder returnValue = new StringBuilder();
		String format = BBBCoreConstants.BLANK;
		String attributeFormateedPrice ="formattedPrice";
		String spanPrice = "<span itemprop='price' aria-hidden='true' content='";
		String spanHighPrice = "<span itemprop='highPrice' aria-hidden='true' content='";
		String spanLowPrice = "<span itemprop='lowPrice' aria-hidden='true' content='";
		String isFromPDP = "false";
		String isKatoriPrice="false";
		String spanHyphen = "<span aria-hidden='true'> - </span>";
		String spanCurrency = "<span itemprop='priceCurrency' aria-hidden='true'>";
		
		
		if (!BBBUtility.isEmpty((String) pAttributes.getProperty(attributeFormateedPrice))) {
			format = (String) pAttributes.getProperty(attributeFormateedPrice);
		}
		
		//Code Change to check for PDP flag and the katori check.
		if (!BBBUtility.isEmpty((String) pAttributes.getProperty("symbol"))) {
			String pdpAndKatoriFlag=(String) pAttributes.getProperty("symbol");
			 isFromPDP=pdpAndKatoriFlag.split(BBBCoreConstants.COLON)[0];
			 if(pdpAndKatoriFlag.contains(BBBCoreConstants.COLON)){
			 isKatoriPrice=pdpAndKatoriFlag.split(BBBCoreConstants.COLON)[1];
			 }
				
		}
		
		//to check whether call is from PDP or otherpage.
		if(isFromPDP.equalsIgnoreCase("false")){
			
			return (String) pValue;
		}
		BBBSessionBean sessionBeanFromReq = (BBBSessionBean) pRequest.getAttribute("sessionBean");
		if (sessionBeanFromReq == null) {
			sessionBeanFromReq = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
			pRequest.setAttribute("sessionBean", sessionBeanFromReq);
			
		}
		String currency = (String) sessionBeanFromReq.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY);
		String country = (String) sessionBeanFromReq.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
		
		format = format.replace("$", BBBCoreConstants.BLANK).trim();
		String formattedPrice = (String)pValue;
		String currencySymbol = BBBCoreConstants.BLANK;
		//for default country case

		if(SiteContextManager.getCurrentSiteId().equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || (BBBUtility.isEmpty(currency) && BBBUtility.isEmpty(country))){
			currency=BBBCoreConstants.USD;
			currencySymbol = "\\" + BBBCoreConstants.DOLLAR;
			formattedPrice = formattedPrice.replace(BBBCoreConstants.DOLLAR, BBBCoreConstants.USD);
		}else if(BBBUtility.isNotEmpty(currency) && currency.equals(BBBCoreConstants.USD) && BBBUtility.isNotEmpty(country) && country.equalsIgnoreCase(BBBCoreConstants.US)){
			currencySymbol = "\\" + BBBCoreConstants.DOLLAR;
			formattedPrice = formattedPrice.replace(BBBCoreConstants.DOLLAR, BBBCoreConstants.USD);
		}else if(!SiteContextManager.getCurrentSiteId().equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
			currencySymbol = currency;
		}
		
		int count = StringUtils.countMatches(formattedPrice, currency);
		if(count == 0){
			if(isKatoriPrice.equalsIgnoreCase("true")){
				formattedPrice=currency.concat(formattedPrice);
				count++;
				
			}
		}

		if(count == 1){
			if((currency.equals(BBBCoreConstants.USD) && country!= null && country.equals("US"))||(SiteContextManager.getCurrentSiteId().equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA))){
				currencySymbol = BBBCoreConstants.DOLLAR;
			}
			 formattedPrice=this.formatCurrencySpan(formattedPrice, currency,spanPrice,currencySymbol,spanCurrency);
			
		}else if(count == 2){
			if((currency.equals(BBBCoreConstants.USD) && country!= null && country.equals("US"))||(SiteContextManager.getCurrentSiteId().equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA))){
				currencySymbol = BBBCoreConstants.DOLLAR;
			}
			String lowPrice=formattedPrice.split(BBBCoreConstants.HYPHEN)[0];
			String highPrice=formattedPrice.split(BBBCoreConstants.HYPHEN)[1];
			String lowPriceSpan=this.formatCurrencySpan(lowPrice, currency,spanLowPrice,currencySymbol,spanCurrency);	
			String highPriceSpan=this.formatCurrencySpan(highPrice,currency,spanHighPrice,currencySymbol,spanCurrency);	
			
			formattedPrice=lowPriceSpan.concat(BBBCoreConstants.SPACE+spanHyphen+BBBCoreConstants.SPACE).concat(highPriceSpan);
		}
	
		returnValue.append(formattedPrice);
		
		return returnValue.toString();
	}
	
	
	
	
	/**
	 * Creating the Price Span Tag for Both High and Low Price
	 * @param priceString
	 * @param currency
	 * @param priceSpan
	 * @param currencySymbol
	 * @param spanCurrency
	 * @return
	 */
	private String formatCurrencySpan(String priceString, String currency, String priceSpan,String currencySymbol, String spanCurrency) {
		
		String formattedSpan=null;
		priceString=priceString.trim();
		String contentEnd = "'>";
		if(priceString.indexOf(currency)!=0){
			String prePriceString=priceString.split(currency)[0];
			String postPriceString=priceString.split(currency)[1];
			String currencyReplacement = spanCurrency.concat(currencySymbol).concat(spanClosing).concat(priceSpan);
			formattedSpan=spanPreCurrency.concat(prePriceString).concat(spanClosing).concat(currencyReplacement).concat(removeCommas(postPriceString)).concat(contentEnd).concat(postPriceString).concat(spanClosing);
		}
		else{
			/* Code change to avoid ArrayIndexOutOfBoundException Internal JIRA Ticket #BBB-677 starts */
			String[] priceStrArr=priceString.split(currency);
			String postPriceString=(priceStrArr != null && priceStrArr.length>1) ? priceStrArr[1] : "";
			/* Code change to avoid ArrayIndexOutOfBoundException Internal JIRA Ticket #BBB-677 ends */
			postPriceString=postPriceString.trim();
			if(postPriceString.contains(BBBCoreConstants.SPACE)){
				String price=postPriceString.split(BBBCoreConstants.SPACE)[0];
				String rangeType=postPriceString.split(BBBCoreConstants.SPACE)[1];
				String postPricespan=spanPreCurrency.concat(BBBCoreConstants.SPACE).concat(rangeType).concat(spanClosing);
				String currencyReplacement = spanCurrency.concat(currencySymbol).concat(spanClosing).concat(priceSpan);
				if(currencySymbol.equals(BBBCoreConstants.DOLLAR))
				{
					formattedSpan=currencyReplacement.concat(removeCommas(price)).concat(contentEnd).concat(price).concat(spanClosing).concat(BBBCoreConstants.SPACE).concat(postPricespan);
				} else{
					formattedSpan=currencyReplacement.concat(removeCommas(price)).concat(contentEnd).concat(BBBCoreConstants.SPACE+price).concat(spanClosing).concat(BBBCoreConstants.SPACE).concat(postPricespan);
				}
			}
			else{
			String currencyReplacement = spanCurrency.concat(currencySymbol).concat(spanClosing).concat(priceSpan);
			if(currencySymbol.equals(BBBCoreConstants.DOLLAR))
				{
					formattedSpan=currencyReplacement.concat(removeCommas(postPriceString)).concat(contentEnd).concat(postPriceString).concat(spanClosing);
				}
			else {
					formattedSpan=currencyReplacement.concat(removeCommas(postPriceString)).concat(contentEnd).concat(BBBCoreConstants.SPACE+postPriceString).concat(spanClosing);
				}
			}
		}
		return formattedSpan;
		
	}
	
	public String removeCommas(String price)
    {	
		return price.replaceAll(BBBCoreConstants.COMMA, BBBCoreConstants.BLANK);
    }
}
