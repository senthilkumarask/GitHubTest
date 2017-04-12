/**
 * 
 */
package com.bbb.integration.cybersource.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.core.util.StringUtils;
import atg.integrations.cybersourcesoap.CyberSourceStatus;
import atg.integrations.cybersourcesoap.PreCalculatedTaxUtils;
import atg.nucleus.PropertiesFileLoader;
import atg.payment.tax.TaxRequestInfo;

/**
 * @author alakra
 * 
 */
public class BBBPreCalculatedTaxUtils extends PreCalculatedTaxUtils {

	/**
	 * Encoding used for taxes persisting/loading
	 */
	private static final String ENCODING_8859_1 = "8859_1";

	/**
	 * Methods that loads Properties object from repository
	 * 
	 * @param pOrder
	 * @return
	 */
	public Properties loadProperties(Order pOrder) {
		OrderImpl orderImpl = (OrderImpl) pOrder;
		Properties persistedProperties = new Properties();
		String persistedPropertiesString = (String) orderImpl
				.getPropertyValue(getBaseServiceFields()
						.getPreCalculatedTaxPropertyName());
		if (!StringUtils.isBlank(persistedPropertiesString)) {
			InputStream inputStream = null;
	
			try {
				inputStream = new ByteArrayInputStream(persistedPropertiesString.getBytes(ENCODING_8859_1));
				PropertiesFileLoader propertiesFileLoader = new PropertiesFileLoader();
				propertiesFileLoader.load(persistedProperties, inputStream);
			} catch (UnsupportedEncodingException e) {
				if (isLoggingError()) {
					logError("Unsupported Encoding Exception occurred while preparing inputStream", e);
				}
			} catch (IOException e) {
				if (isLoggingError()) {
					logError("IO Exception occurred while preparing properties FileLoader", e);
				}
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						if (isLoggingError()) {
							logError("IO Exception occurred while closing the input stream ", e);
						}
					}
				}
			}
		}
		
		return persistedProperties;
	}

	/**
	 * Method for precalculated tax loading
	 * 
	 * @param pTaxRequestInfo
	 * @return null if precalcualted tax not found or filled CyberSourceStatus
	 *         object otherwise
	 */
	public CyberSourceStatus getPrecalculatedTaxes(TaxRequestInfo pTaxRequestInfo) {
		Properties persistedProperties = loadProperties(pTaxRequestInfo.getOrder());
		if (persistedProperties != null && persistedProperties.size() > 0) {
			return super.getPrecalculatedTaxes(pTaxRequestInfo);
		} else {
			return null;
		}
	}
}
