package com.bbb.rest.output;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import atg.droplet.BBBCurrencyTagConvertor;
import atg.droplet.BBBDefaultCurrencyTagConverter;
import atg.droplet.BBBFormatedPriceTagConvertor;
import atg.droplet.BBBMexicoCurrencyTagConvertor;
import atg.droplet.BBBUnformattedCurrencyTagConvertor;
import atg.droplet.TagConverterManager;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.internationalshipping.vo.BBBInternationalCurrencyDetailsVO;
import com.bbb.utils.BBBUtility;

public class BBBCustomTagComponent extends BBBGenericService {

	private String mexicoCurrencySymbol;
	private NumberFormat wholeNumberFormat;
	private NumberFormat decimalNumberFormat;
	
	/**
	 * @return the wholeNumberFormat
	 */
	public NumberFormat getWholeNumberFormat() {
		return wholeNumberFormat;
	}

	/**
	 * @param wholeNumberFormat the wholeNumberFormat to set
	 */
	public void setWholeNumberFormat(NumberFormat wholeNumberFormat) {
		this.wholeNumberFormat = wholeNumberFormat;
	}

	/**
	 * @return the decimalNumberFormat
	 */
	public NumberFormat getDecimalNumberFormat() {
		return decimalNumberFormat;
	}

	/**
	 * @param decimalNumberFormat the decimalNumberFormat to set
	 */
	public void setDecimalNumberFormat(NumberFormat decimalNumberFormat) {
		this.decimalNumberFormat = decimalNumberFormat;
	}

	/**
	 * @return the mexicoCurrencySymbol
	 */
	public String getMexicoCurrencySymbol() {
		return mexicoCurrencySymbol;
	}

	/**
	 * @param mexicoCurrencySymbol the mexicoCurrencySymbol to set
	 */
	public void setMexicoCurrencySymbol(String mexicoCurrencySymbol) {
		this.mexicoCurrencySymbol = mexicoCurrencySymbol;
	}


	/** International repository. */
	private MutableRepository internationalRepository;
	private BBBCatalogTools catalogTools;
	private BBBLocalCacheContainer localcache;
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * Gets the international repository.
	 *
	 * @return the internationalRepository
	 */
	public final MutableRepository getInternationalRepository() {
		return internationalRepository;
	}

	/**
	 * Sets the international repository.
	 *
	 * @param internationalRepository the internationalRepository to set
	 */
	public final void setInternationalRepository(final MutableRepository internationalRepository) {
		this.internationalRepository = internationalRepository;
	}

	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		
		logDebug("BBBCustomTagComponent doStartService");			
		
		TagConverterManager.registerTagConverter(new BBBCurrencyTagConvertor());
		TagConverterManager.registerTagConverter(new BBBUnformattedCurrencyTagConvertor());	
		TagConverterManager.registerTagConverter(new BBBDefaultCurrencyTagConverter());
		TagConverterManager.registerTagConverter(new BBBFormatedPriceTagConvertor());
		TagConverterManager.registerTagConverter(new BBBMexicoCurrencyTagConvertor());
	}
	
	public String format(String value, String currency,String country, boolean formattedPriceRequired,Properties pAttributes) {
		String formatValue = BBBCoreConstants.BLANK;
		if(value.startsWith(BBBCoreConstants.DOLLOR)  ) {
			formatValue = formatValue(Double.valueOf(value.substring(BBBCoreConstants.ONE)), currency, country, formattedPriceRequired, pAttributes);
		}else { 
			formatValue =  formatValue(Double.valueOf(value),currency,country, formattedPriceRequired, pAttributes);
		}
		return formatValue;
	}
	
	/**
	 * This method converts the Price into localized price based on the country and currency
	 * If formattedPriceRequired is true then 
	 * For Mexico returns the formatted price with country symbol appended
	 * Else return localized price with Currency Symbol appended
	 * If formattedPriceRequired is false then
	 * For Mexico returns the formatted price only
	 * Else return localized price only
	 * @param value
	 * @param currency
	 * @param country
	 * @param formattedPriceRequired
	 * @param pAttributes
	 * @return String
	 */
	
	
	public String formatValue(Double value, String currency,String country, boolean formattedPriceRequired,Properties pAttributes)  {
		Object round=null;
		String roundMethod=null;
		Integer scale = 0;
		String currencySymbol = "";
		String returnCurrencyVal = "";
		BigDecimal convertedValue = null;
		NumberFormat currencyformatter = null;
		boolean isFormattedWithoutCurrency = false;
		if(pAttributes!=null) {
			round=pAttributes.getProperty("round"); //for rounding the the range prices
			String withoutDoubleCurrency = (String)pAttributes.get("isFormattedWithoutCurrency");
			if(!BBBUtility.isEmpty(withoutDoubleCurrency) && withoutDoubleCurrency.equalsIgnoreCase(BBBCoreConstants.TRUE)){
				isFormattedWithoutCurrency = true;
			}
		}
		
		if(round instanceof String){
			roundMethod = (String) round;
		}
		
		if (!BBBUtility.isEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.COUNTRY_MEXICO)) {
			
			// Case : Country - US / International Except Mexico
			double lcpFactor = 1.0; //initializing LCP factor with 1  ... for default LCP factor
			double fxRate = 0.0;
			BBBInternationalCurrencyDetailsVO currencyDetailsVO = getCurrencyDetailsVO(currency, country);
			
			if (currencyDetailsVO != null) {
				lcpFactor = currencyDetailsVO.getLcpFactor();
				fxRate = currencyDetailsVO.getFxRate();
				scale = currencyDetailsVO.getScale();
				currencySymbol = currencyDetailsVO.getCurrencySymbol();
			}
			
			convertedValue = BigDecimal.valueOf(value * fxRate * lcpFactor);
			convertedValue = convertedValue.divide(BigDecimal.ONE, scale, BigDecimal.ROUND_HALF_UP);
			if(!BBBUtility.isEmpty(roundMethod)) {
				returnCurrencyVal = getConvertedValueForPriceRange(convertedValue, currencySymbol);
				return returnCurrencyVal;
			}
			currencyformatter = getCurrencyFormatter(scale, convertedValue.doubleValue());
			if(!formattedPriceRequired){
				returnCurrencyVal = currencyformatter.format(convertedValue);
				returnCurrencyVal = returnCurrencyVal.replaceAll(BBBCoreConstants.COMMA ,BBBCoreConstants.BLANK);
			} else {
				returnCurrencyVal = currencyformatter.format(convertedValue);
				returnCurrencyVal = currencySymbol + BBBCoreConstants.SPACE + returnCurrencyVal;
			}
			if(isFormattedWithoutCurrency){
				returnCurrencyVal = currencyformatter.format(convertedValue);
			}
		} else {
			// Case : Country - Mexico
			currencySymbol = this.getMexicoCurrencySymbol();
			convertedValue=new BigDecimal(value);
				
			if(!formattedPriceRequired){
				returnCurrencyVal = value.toString();
			} else {
				currencyformatter = getCurrencyFormatter(scale, value);
				returnCurrencyVal = currencyformatter.format(value);
				returnCurrencyVal = currencySymbol + BBBCoreConstants.SPACE + returnCurrencyVal;
			}
			if(isFormattedWithoutCurrency && null!=currencyformatter){
				returnCurrencyVal = currencyformatter.format(convertedValue);
			}
		}
		return returnCurrencyVal;
	}
	
	/**
	 * This method returns the currency details based on the country and currency
	 * @param currency
	 * @param country
	 * @return BBBInternationalCurrencyDetailsVO
	 */
	public BBBInternationalCurrencyDetailsVO getCurrencyDetailsVO(String currency, String country) {
		
		double lcpFactor = 1.0; //initializing LCP factor with 1  ... for default LCP factor
		double fxRate = 0.0;
		Integer scale = 0;
		String currencySymbol = null;
		String merchantId=null;
		BBBInternationalCurrencyDetailsVO currencyDetailsVO = null;
		try {
			if(getLocalcache().get(currency) != null) {
				currencyDetailsVO = (BBBInternationalCurrencyDetailsVO) getLocalcache().get(currency);
			} else {
				currencyDetailsVO = new BBBInternationalCurrencyDetailsVO();
				RepositoryItem	exchangeRates = this.getInternationalRepository().getItem(currency, BBBCatalogConstants.EXCHANGE_RATES);
				fxRate = (Double)exchangeRates.getPropertyValue("value");
				scale = (Integer)exchangeRates.getPropertyValue("roundMethod");
				currencySymbol = (String) exchangeRates.getPropertyValue("shopperCurrency");
				currencyDetailsVO.setFxRate(fxRate);
				currencyDetailsVO.setScale(scale);
				currencyDetailsVO.setCurrencySymbol(currencySymbol);
				if(!BBBUtility.isEmpty(currencySymbol)){
					getLocalcache().put(currency, currencyDetailsVO);
				}
				
			}
			final List<String> merchantIdList = this.getCatalogTools().getAllValuesForKey(
					BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.MERCHANT_ID);
			if(!BBBUtility.isListEmpty(merchantIdList)) {
				merchantId= merchantIdList.get(BBBCoreConstants.ZERO);
			}
			if(getLocalcache().get(merchantId + country) != null) {
				lcpFactor = (Double) getLocalcache().get(merchantId + country);
			} else {
				final RepositoryView countryListView = getInternationalRepository().getView(BBBInternationalShippingConstants.COUNTRY_LIST_ITEMDESCRIPTOR);
				final RqlStatement statement = RqlStatement.parseRqlStatement("merchantId = ?0 and cod = ?1", true);
				final Object[] params = { merchantId, country };
				final RepositoryItem[] countryListItems = statement.executeQuery(countryListView, params);
				if (!BBBUtility.isArrayEmpty(countryListItems) && countryListItems[0].getPropertyValue("frontLoadCoefficient") != null) {
					lcpFactor = (Double) countryListItems[0].getPropertyValue("frontLoadCoefficient");
				}
				getLocalcache().put(merchantId + country, lcpFactor);
		   }
		currencyDetailsVO.setLcpFactor(lcpFactor);
		} catch (RepositoryException e) {
			logError("BBBCustomTagComponent::Repository Exception occured for country :" + country + " :: currency: "+ currency + e);
		} catch (BBBSystemException e) {
			logError("BBBCustomTagComponent::BBBSystemException occured for country :" + country + " :: currency: "+ currency + e);
		} catch (BBBBusinessException e) {
			logError("BBBCustomTagComponent::BBBBusinessException occured for country :" + country + " :: currency: "+ currency + e);
		}
		return currencyDetailsVO;
	}

	public String getConvertedValueForPriceRange(BigDecimal value, String currencySymbol) {
		
		BigDecimal convertedValue = value.divide(BigDecimal.ONE, 0, BigDecimal.ROUND_FLOOR);
		return currencySymbol + BBBCoreConstants.SPACE + convertedValue;
	}

	public BBBLocalCacheContainer getLocalcache() {
		return localcache;
	}

	public void setLocalcache(BBBLocalCacheContainer localcache) {
		this.localcache = localcache;
	}
	
	/**
	 * This method returns currency formatter based on the currencyValue
	 * if currencyValue is whole number it returns formatter with zero decimal digit.
	 * if currencyValue is not whole number it returns formatter with two decimal digits.
	 * @param scale
	 * @param currencyValue
	 * @return NumberFormat
	 */
	
	public NumberFormat getCurrencyFormatter(int scale, Double currencyValue) {
		NumberFormat format = null;
		if (Double.compare(currencyValue % 1,BBBCoreConstants.ZERO) == 0) {
			if (this.getWholeNumberFormat() != null) {
				format = this.getWholeNumberFormat();
			} else {
				format =NumberFormat.getCurrencyInstance(Locale.US);
				DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) format).getDecimalFormatSymbols();
				decimalFormatSymbols.setCurrencySymbol(BBBCoreConstants.BLANK);
				((DecimalFormat) format).setDecimalFormatSymbols(decimalFormatSymbols);
				((DecimalFormat) format).setMaximumFractionDigits(BBBCoreConstants.ZERO);
				((DecimalFormat) format).setMinimumFractionDigits(BBBCoreConstants.ZERO);
				this.setWholeNumberFormat(format);
			}
		} else{
			format = getDefaultCurrencyFormatter();
		}
		return format;
	}
	
	
	public NumberFormat getDefaultCurrencyFormatter() {
		NumberFormat format = null;
			if (this.getDecimalNumberFormat() != null) {
				format = this.getDecimalNumberFormat();
			} else {
				format =NumberFormat.getCurrencyInstance(Locale.US);
				DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) format).getDecimalFormatSymbols();
				decimalFormatSymbols.setCurrencySymbol(BBBCoreConstants.BLANK);
				((DecimalFormat) format).setDecimalFormatSymbols(decimalFormatSymbols);
				((DecimalFormat) format).setMaximumFractionDigits(BBBCoreConstants.TWO);
				((DecimalFormat) format).setMinimumFractionDigits(BBBCoreConstants.TWO);
				this.setDecimalNumberFormat(format);
			}
		return format;
	}
	
	/**
	 * This method returns formatted currency appended with Dollor symbool 
	 * if pValue is whole number it returns currency with zero decimal digit.
	 * if pValue is not whole number it returns currency with two decimal digits.
	 * @param pValue
	 * @return String
	 */

	public String formatValueForUS(Double pValue) {
		String formattedValue = null;
		NumberFormat currencyFormatter = getCurrencyFormatter(2, pValue);
		if (currencyFormatter != null) {
			formattedValue = BBBCoreConstants.DOLLAR + currencyFormatter.format(new BigDecimal(pValue));
		}
		return formattedValue;
	}
	
	public String formatValueForUSWithoutCurrency(Double pValue){
		String formattedValue = null;
		NumberFormat currencyFormatter = getCurrencyFormatter(2, pValue);
		if (currencyFormatter != null) {
			formattedValue = currencyFormatter.format(new BigDecimal(pValue));
		}
		return formattedValue;
	}
}