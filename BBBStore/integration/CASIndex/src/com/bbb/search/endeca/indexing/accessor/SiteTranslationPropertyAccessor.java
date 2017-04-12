package com.bbb.search.endeca.indexing.accessor;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Set;

import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class SiteTranslationPropertyAccessor extends PropertyAccessorImpl {
	
	/** The Constant SITE_PROPERTY. */
	private static final String SITE_PROPERTY = "site";

	/** The Constant LOCALE_PROPERTY. */
	private static final String LOCALE_PROPERTY = "locale";

	/** The Constant ATTRIBUTE_PROPERTY. */
	private static final String ATTRIBUTE_PROPERTY = "attributeName";

	/** The Constant STRING_PROPERTY. */
	private static final String STRING_PROPERTY = "attributeValueString";

	/** The Constant NUMBER_PROPERTY. */
	private static final String NUMBER_PROPERTY = "attributeValueNumber";

	/** The Constant BOOLEAN_PROPERTY. */
	private static final String BOOLEAN_PROPERTY = "attributeValueBoolean";

	/** The Constant CLOB_PROPERTY. */
	private static final String CLOB_PROPERTY = "attributeValueClob";
	
	private static final String DATE_PROPERTY="attributeValueDate";

	/** The Constant Default Value */
	private static final String PROPERTY_DEFAULT_SUFFIX = "Default";

	/** Translation Name from which to fetch the property value */
	private String mTranslationName = null;

	/**
	 * @return the mTranslationName
	 */
	public String getTranslationName() {
		return mTranslationName;
	}

	/**
	 * @param mTranslationName
	 *            the mTranslationName to set
	 */
	public void setTranslationName(String mTranslationName) {
		this.mTranslationName = mTranslationName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.repository.search.indexing.PropertyAccessorImpl#
	 * getTextOrMetaPropertyValue(atg.repository.search.indexing.Context,
	 * atg.repository.RepositoryItem, java.lang.String,
	 * atg.repository.search.indexing.specifier.PropertyTypeEnum)
	 */
	protected Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {

		String currentSiteVariant = null;

		Object returnVal = pItem.getPropertyValue(new StringBuffer(
								pPropertyName).append(PROPERTY_DEFAULT_SUFFIX)
								.toString());
		String repositoryItemId = pItem.getRepositoryId();

		currentSiteVariant = getCurrentVariantSiteId(pContext, pItem);

		Set<RepositoryItem> translations = (Set<RepositoryItem>) getRepositoryItemProperyValue(
				pItem, getTranslationName());

		if (null != translations && !translations.isEmpty()) {

			if (isLoggingDebug()) {
				logDebug(new StringBuilder("FIND TRANSLATION FOR ")
						.append("ITEM ID: ").append(repositoryItemId)
						.append("SITE[").append(currentSiteVariant)
						.append("]:ITEM[").append(pPropertyName).append("]")
						.toString());
			}

			for (RepositoryItem translation : translations) {

				if (isCurrentSiteVariantTranslation(currentSiteVariant,
								translation) 
								&& translation.getPropertyValue(ATTRIBUTE_PROPERTY).equals(pPropertyName)) {

					if (isLoggingDebug()) {
						logDebug(new StringBuilder("TRANSLATION FOUND FOR ")
								.append("ITEM ID: ").append(repositoryItemId)
								.append("SITE[").append(currentSiteVariant)
								.append("]:ITEM[")
								.append(pPropertyName).append("]").toString());
					}
					
					returnVal = this.setValues(translation, returnVal);

					break;

				} 
			}
		} 

		return returnVal;
	}

	private String getCurrentVariantSiteId(Context pContext,
			RepositoryItem pItem) {

		String currentSiteVariant = null;

		Object siteIds = getSiteContextPropertyValue(pContext, pItem, "$siteId");

		if (siteIds != null) {

			if (siteIds instanceof java.util.Set) {
				currentSiteVariant = (String) ((Set<Object>) siteIds)
						.iterator().next();
			} else {
				currentSiteVariant = (String) siteIds;
			}

		} else {
			if (isLoggingDebug()) {
				logDebug("SiteIDs is NULL");
			}
		}

		return currentSiteVariant;
	}

	private String getCurrentLocaleVariant(Locale currentLocaleVariant) {
		return new StringBuffer(currentLocaleVariant.getLanguage()).append("_")
				.append(currentLocaleVariant.getCountry()).toString();
	}

	private Object getRepositoryItemProperyValue(RepositoryItem pItem,
			String pPropertyName) {

		return ((RepositoryItemImpl) pItem).getPropertyValue(pPropertyName);
	}

	private boolean isCurrentLocaleVariantTranslation(
			String currentLocaleVariant, RepositoryItem translation) {

		boolean isCurrentLocaleVariantTranslation = false;

		String translationLocale = (String) translation
				.getPropertyValue(LOCALE_PROPERTY);

		if (null != translationLocale
				&& currentLocaleVariant.equalsIgnoreCase(translationLocale)) {
			isCurrentLocaleVariantTranslation = true;
		}
		
		return isCurrentLocaleVariantTranslation;
	}

	private boolean isCurrentSiteVariantTranslation(String currentSiteVariant,
			RepositoryItem translation) {

		boolean isCurrentSiteVariantTranslation = false;

		String translationSite = (String) ((RepositoryItem) translation
				.getPropertyValue(SITE_PROPERTY)).getRepositoryId();

		if (null != translationSite
				&& currentSiteVariant.equalsIgnoreCase(translationSite)) {
			isCurrentSiteVariantTranslation = true;
		}
		
		return isCurrentSiteVariantTranslation;
	}

	private boolean isCurrentRepositoryItemTranslation(
			String currentRepositoryItem, RepositoryItem translation) {

		boolean isCurrentRepositoryItemTranslation = false;

		String translationAttributeName = (String) translation
				.getPropertyValue(ATTRIBUTE_PROPERTY);

		if (null != translationAttributeName
				&& currentRepositoryItem
						.equalsIgnoreCase(translationAttributeName)) {
			isCurrentRepositoryItemTranslation = true;
		}
		
		return isCurrentRepositoryItemTranslation;
	}

	/**
	 * Sets the values.
	 * 
	 * @param translation
	 *            the translation
	 * @param defaultValue
	 *            the default value
	 * @return the object
	 */
	private Object setValues(RepositoryItem translation, Object defaultValue){
		
		Object value = null;
		
		String stringValue = (String)translation.getPropertyValue(STRING_PROPERTY);
		Double intValue = (Double)translation.getPropertyValue(NUMBER_PROPERTY);
		Boolean boolValue = (Boolean)translation.getPropertyValue(BOOLEAN_PROPERTY);
		String clobValue = (String)translation.getPropertyValue(CLOB_PROPERTY);
		/** Added Site Specific Enable Date for R2.1 #53 PIM FEED*/
		Timestamp attributeValueDate = (Timestamp)translation.getPropertyValue(DATE_PROPERTY);
		
		//Checking for each Data Type, if any Data type Value exists, then return same, else return the default value
		if(null != stringValue && !stringValue.isEmpty()){
			value = stringValue;
			return value;
		}else if(null != intValue){
			value = intValue;
			return value;
		} else if(null != boolValue){
			value = boolValue;
			return value;
		} else if(null != clobValue){
			value = clobValue;
			return value;
		} else if(null != attributeValueDate){
			value = attributeValueDate;
			return value;
		} else {
			if(isLoggingDebug()){
				logDebug("No Value found in Tranlsations");
			}
			return defaultValue;
		}
		
	}

	
	

}
