package com.bbb.commerce.catalog;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.UnsupportedFeatureException;
import atg.repository.dp.Derivation;
import atg.repository.dp.DerivationMethodImpl;
import atg.repository.dp.PropertyExpression;
import atg.repository.dp.RepositoryKeyService;
import atg.repository.query.PropertyQueryExpression;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.utils.BBBUtility;

/**
 * This derived property method will derive a property based on the current site
 * and profile's current locale.
 * 
 * For example:<br>
 * 
 * <pre>
 *   &lt;property name="imagePath"&gt;
 *     &lt;derivation user-method="atg.projects.store.dp.StoreContextDerivation"&gt;
 *       &lt;expression&gt;image&lt;/expression&gt;
 *     &lt;/derivation&gt;
 *   &lt;/property&gt;
 * </pre>
 * 
 * ;
 * 
 * 
 **/
@SuppressWarnings("serial")
public class SiteLocaleTranslation extends DerivationMethodImpl {

	private static final String CHANNEL_THEME_HEADER = "X-bbb-channel-theme";
	protected static final String DERIVATION_NAME = "SEODerivation";
	protected static final String DISPLAY_NAME = "Derive by the current site and locale";
	protected static final String SHORT_DESCRIPTION = "Get value mapped to by current site and locale";

	protected static final String KEY_SERVICE_PATH = "/atg/userprofiling/LocaleService";
	protected static final String SITE_TAG = "{site}";
	protected static final String LANGUAGE_TAG = "{language}";

	protected static final String DEFAULT_LANGUAGE_ATTR = "defaultLanguage";
	protected static final String DEFAULT_SITE_ATTR = "defaultSite";
	protected static final String DEFAULT_PROPERTY = "defaultProperty";
	protected static final String DEFAULT_LOCALE = "defaultLocale";
	protected static final String PROPERTY = "property";
	protected static final String TRANSLATIONS = "translations";
	protected static final String CHANNEL_HEADER = "X-bbb-channel";
	protected static final String SITE_PROPERTY = "site";
	protected static final String DESKTOPWEB = "DesktopWeb";
	protected static final String CHANNEL_PROPERTY = "channel";
	protected static final String CHANNEL_THEME_PROPERTY = "channelTheme";
	protected static final String LOCALE_PROPERTY = "locale";
	protected static final String CONFIGURABLE_TYPE = "configurableType";

	private String mDefaultLocale = null;
	private String mDefaultProperty = null;
	private String mDefaultSiteAttribute = null;
	private String mDefaultLanguageAttribute = null;

	private String mProperty = null;
	private String mTranslations = null;

	// static initializer
	static {
		Derivation.registerDerivationMethod(DERIVATION_NAME,
				SiteLocaleTranslation.class);
	}

	/**
	 * Set the name, display name and short description properties.
	 */
	public SiteLocaleTranslation() {
		setName(DERIVATION_NAME);
		setDisplayName(DISPLAY_NAME);
		setShortDescription(SHORT_DESCRIPTION);
	}

	public String getDefaultLocale() {
		if (mDefaultLocale == null) {
			RepositoryPropertyDescriptor pd = getDerivation()
					.getPropertyDescriptor();
			mDefaultLocale = (String) pd.getValue(DEFAULT_LOCALE);
		}
		return mDefaultLocale;
	}

	public String getDefaultProperty() {
		if (mDefaultProperty == null) {
			RepositoryPropertyDescriptor pd = getDerivation()
					.getPropertyDescriptor();
			mDefaultProperty = (String) pd.getValue(DEFAULT_PROPERTY);
		}
		return mDefaultProperty;
	}

	public String getDefaultSiteAttribute() {
		if (mDefaultSiteAttribute == null) {
			RepositoryPropertyDescriptor pd = getDerivation()
					.getPropertyDescriptor();
			mDefaultSiteAttribute = (String) pd.getValue(DEFAULT_SITE_ATTR);
		}
		return mDefaultSiteAttribute;
	}

	public String getDefaultLanguageAttribute() {
		if (mDefaultLanguageAttribute == null) {
			RepositoryPropertyDescriptor pd = getDerivation()
					.getPropertyDescriptor();
			mDefaultLanguageAttribute = (String) pd
					.getValue(DEFAULT_LANGUAGE_ATTR);
		}
		return mDefaultLanguageAttribute;
	}

	public String getProperty() {
		if (mProperty == null) {
			RepositoryPropertyDescriptor pd = getDerivation()
					.getPropertyDescriptor();
			mProperty = (String) pd.getValue(PROPERTY);
		}
		return mProperty;
	}

	public String getTranslations() {
		if (mTranslations == null) {
			RepositoryPropertyDescriptor pd = getDerivation()
					.getPropertyDescriptor();
			mTranslations = (String) pd.getValue(TRANSLATIONS);
		}
		return mTranslations;
	}

	/*
	 * public String getConfigurableType() { if(mConfigurableType == null) {
	 * RepositoryPropertyDescriptor pd =
	 * getDerivation().getPropertyDescriptor(); mConfigurableType = (String)
	 * pd.getValue(CONFIGURABLE_TYPE); } return mConfigurableType; }
	 */

	private static ApplicationLogging mLogger = ClassLoggingFactory
			.getFactory().getLoggerForClass(SiteLocaleTranslation.class);


	/**
	 * @return ApplicationLogging object for logger.
	 */
	public ApplicationLogging getLogger() {
		return mLogger;
	}

	/**
	 * We do not support query for this derivation implementation
	 */
	@SuppressWarnings("rawtypes")
	protected Query createQuery(int pQueryType, boolean pDerivedPropertyOnLeft,
			boolean pCountDerivedProperty, QueryExpression pOther,
			int pOperator, boolean pIgnoreCase, QueryExpression pMinScore,
			QueryExpression pSearchStringFormat, Query pItemQuery,
			QueryBuilder pBuilder, PropertyQueryExpression pParentProperty,
			List pChildPropertyList) throws RepositoryException {

		throw new UnsupportedFeatureException();
	}

	/**
	 * Determine the derived property value.
	 * 
	 * @param pItem
	 *            the item whose property value is desired
	 * @return the derived value
	 * @exception RepositoryException
	 *                if there is a problem deriving the value
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object derivePropertyValue(RepositoryItemImpl pItem)
			throws RepositoryException {
		if (getLogger().isLoggingDebug()) {
			getLogger().logDebug("In derive property value ");
		}

		List exps = getDerivation().getExpressionList();

		PropertyExpression pe;
		Object value = null;
		String propName = null;

		try {
			if (BBBUtility.isListEmpty(exps)) {
				return null;
			} else {
				pe = (PropertyExpression) exps.get(0);
				if (pe != null) {
					propName = pe.getPropertyDescriptor().getName();
					if (getLogger().isLoggingDebug()) {
						getLogger().logDebug("PropName : " + propName);
					}
					value = pe.evaluate(pItem);
				}
			}
		} catch (IndexOutOfBoundsException ioobe) {
			return null;
		} catch (Exception e) {
			return null;
		}
		if (propName == null) {
			value = getProperyValue(pItem, getDefaultProperty());
		}
		if (getLogger().isLoggingDebug()) {
			getLogger().logDebug("Default Value : " + value);
		}

		String siteId = extractSiteID();
		// testing Purpose
		// String siteId ="BuyBuyBaby";
		if (getLogger().isLoggingDebug()) {
			getLogger().logDebug(
					"CurrentSiteId from SiteContextManager : " + siteId);
		}

		if (ServletUtil.getCurrentRequest().getParameter(
				BBBCoreConstants.IS_FROM_SCHEDULER) != null) {
			siteId = ServletUtil.getCurrentRequest().getParameter(
					BBBCoreConstants.SITE_ID);
		}

		// Checking For siteId
		if (siteId == null || siteId.isEmpty()) {
			siteId = getDefaultSiteAttribute();
			if (getLogger().isLoggingDebug()) {
				getLogger().logDebug("Setting Default SiteId : " + siteId);
			}
		}
		String channelID = null;
		String channelThemeID = null;
		if (null != ServletUtil.getCurrentRequest()) {
			channelID = ServletUtil.getCurrentRequest().getHeader(
					CHANNEL_HEADER);
		}
		if (null == channelID || StringUtils.isEmpty(channelID)) {
			if (null != ServletUtil.getCurrentRequest()
					&& null != ServletUtil.getCurrentRequest().getParameter(
							BBBCoreConstants.CHANNEL)) {
				channelID = ServletUtil.getCurrentRequest().getParameter(
						BBBCoreConstants.CHANNEL);
			}
		}
		if (null == channelID || StringUtils.isEmpty(channelID)) {
			channelID = DESKTOPWEB;
		}
		if (channelID.equalsIgnoreCase(BBBCoreConstants.FF1)
				|| channelID.equalsIgnoreCase(BBBCoreConstants.FF2)) {
			channelThemeID = ServletUtil.getCurrentRequest().getHeader(
					CHANNEL_THEME_HEADER);
		}
		String reqLocale = null;
		RepositoryKeyService keySvice = getKeyService();
		if (null != keySvice) {
			reqLocale = keySvice.getRepositoryKey().toString();
		} else {
			reqLocale = getDefaultLocale();
		}

		if (getLogger().isLoggingDebug()) {
			getLogger().logDebug("LocaleStr : " + reqLocale);
		}

		// start of TBS changes
		if(!StringUtils.isBlank(siteId)){
			if(siteId.equals("TBS_BedBathUS")){
				siteId = BBBCoreConstants.SITE_BAB_US;
			}
			if(siteId.equals("TBS_BuyBuyBaby")){
				siteId = BBBCoreConstants.SITE_BBB;
			}
			if(siteId.equals("TBS_BedBathCanada")){
				siteId = BBBCoreConstants.SITE_BAB_CA;
			}
		}
		// end of TBS changes
		String reqConfigurableType = null;
		reqConfigurableType = ServletUtil.getCurrentRequest().getParameter(
				BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE);
		String reqEmailType = null;
		reqEmailType = ServletUtil.getCurrentRequest().getParameter(
				BBBGiftRegistryConstants.EMAIL_TYPE);

		if (reqEmailType == null || reqEmailType.isEmpty()) {
			if (siteId.equalsIgnoreCase(getDefaultSiteAttribute())
					&& reqLocale.equalsIgnoreCase(getDefaultLocale())) {
				if (getLogger().isLoggingDebug()) {
					getLogger().logDebug(
							"site Id equals to default : " + siteId);
					getLogger().logDebug(
							"Locale equals to default : " + reqLocale);
				}
				return value;
			}
		}
		try {
			String repositoryId = pItem.getRepositoryId();
			if (getLogger().isLoggingDebug()) {
				getLogger().logDebug("Repository ID : " + repositoryId);
			}

			String translationName = getTranslations();
			String property = getProperty();
			// String configurableTypeName = getConfigurableType();

			Set<RepositoryItem> translations = (Set) getProperyValue(pItem,
					translationName);

			if ((translations == null || translations.isEmpty())
					|| (property == null || property.isEmpty())) {
				if (getLogger().isLoggingDebug()) {
					getLogger().logDebug(
							"Translations is null. So returning default value");
				}
				return value;
			} else {
				RepositoryItem tempSiteItem = null;
				RepositoryItem tempChannel = null;
				RepositoryItem tempChannelTheme = null;
				String tempLocale = null;

				for (RepositoryItem translation : translations) {

					tempSiteItem = (RepositoryItem) translation
							.getPropertyValue(SITE_PROPERTY);
					tempLocale = (String) translation
							.getPropertyValue(LOCALE_PROPERTY);
					// BPSI 5748
					if(translation.getItemDescriptor().hasProperty(CHANNEL_PROPERTY)){
					tempChannel = (RepositoryItem) translation
							.getPropertyValue(CHANNEL_PROPERTY);
					}
					if(translation.getItemDescriptor().hasProperty(CHANNEL_THEME_PROPERTY)){
					tempChannelTheme = (RepositoryItem) translation
							.getPropertyValue(CHANNEL_THEME_PROPERTY);
					}
					if (tempSiteItem != null
							&& tempSiteItem.getRepositoryId().equalsIgnoreCase(
									siteId)) {
						if (tempLocale != null
								&& tempLocale
										.equalsIgnoreCase(getDefaultLocale())) {
							String tempConfigurableType = null;
							if(translation.getItemDescriptor().hasProperty(CONFIGURABLE_TYPE)){
							tempConfigurableType = (String) translation
									.getPropertyValue(CONFIGURABLE_TYPE);
							}
							if (tempChannel != null
									&& tempChannel.getRepositoryId()
											.equalsIgnoreCase(channelID)
									&& tempChannelTheme != null
									&& tempChannelTheme.getRepositoryId()
											.equalsIgnoreCase(channelThemeID)) {
								if (getLogger().isLoggingDebug()) {
									getLogger().logDebug(
											"Translation present for channel "
													+ channelID + " and theme "
													+ channelThemeID);
								}
								return getValue(property, translation);
							} else if (tempChannel != null
									&& tempChannel.getRepositoryId()
											.equalsIgnoreCase(channelID)
									&& tempChannelTheme != null
									&& !tempChannelTheme.getRepositoryId()
											.equalsIgnoreCase(channelThemeID)) {
								if (getLogger().isLoggingDebug()) {
									getLogger()
											.logDebug(
													"Translation theme id ["
															+ tempChannelTheme
															+ "] , channel "
															+ channelID
															+ " where as required theme ["
															+ channelThemeID
															+ "] , continue to next translation");
								}
								continue;
							} else if (tempChannel != null
									&& tempChannel.getRepositoryId()
											.equalsIgnoreCase(channelID)
									&& tempChannelTheme == null
									&& (BBBUtility.isEmpty(reqConfigurableType))) {
								if (getLogger().isLoggingDebug()) {
									getLogger().logDebug(
											"Translation present for channel "
													+ channelID);
								}
								return getValue(property, translation);
							} else if (tempChannel != null
									&& !tempChannel.getRepositoryId()
											.equalsIgnoreCase(channelID)) {
								if (getLogger().isLoggingDebug()) {
									getLogger()
											.logDebug(
													"Translation Channel ID is ["
															+ tempChannel
															+ "] , Requested Channel id is ["
															+ channelID
															+ "] , continue to next translation");
								}
								continue;
							} else if (tempChannel != null
									&& tempChannel.getRepositoryId()
											.equalsIgnoreCase(channelID)
									&& tempChannelTheme == null
									&& BBBUtility
											.isNotEmpty(tempConfigurableType)
									&& tempConfigurableType
											.equalsIgnoreCase(reqConfigurableType)) {
								if (getLogger().isLoggingDebug()) {
									getLogger()
											.logDebug(
													"Translation present for channel "
															+ channelID
															+ " and Configuration Type "
															+ tempConfigurableType);
								}
								return getValue(property, translation);
							} else if (tempChannel != null
									&& tempChannel.getRepositoryId()
											.equalsIgnoreCase(channelID)
									&& tempChannelTheme == null
									&& BBBUtility
											.isNotEmpty(tempConfigurableType)
									&& !tempConfigurableType
											.equalsIgnoreCase(reqConfigurableType)) {
								if (getLogger().isLoggingDebug()) {
									getLogger()
									 	.logDebug(
											"Translation Configurable Type is ["
													+ tempConfigurableType
													+ "] , Requested Configurable Type is ["
													+ reqConfigurableType
													+ "] , continue to next translation");
                                 }
								continue;
							} else {
								if (getLogger().isLoggingDebug()) {
									getLogger().logDebug(
											"Translation present for site "
													+ siteId + " and locale "
													+ getDefaultLocale());
								}
								return getValue(property, translation);
							}
						} else {
							if (getLogger().isLoggingDebug()) {
								getLogger()
										.logDebug(
												"Translation locale is ["
														+ tempLocale
														+ "] , Required locale is ["
														+ getDefaultLocale()
														+ "] , continue to next translation");
							}
							continue;
						}
					} else {
						if (getLogger().isLoggingDebug()) {
							getLogger()
									.logDebug(
											"Translation site is ["
													+ tempSiteItem
													+ "] , Requested site is ["
													+ siteId
													+ "] , continue to next translation");
						}
						continue;
					}
				}
			}
		} catch (Exception e) {
			getLogger().logError(e);
		}
		if (getLogger().isLoggingDebug()) {
			getLogger().logDebug("Final Returned Value" + value);
		}
		return value;
	}

	/**
	 * @return
	 */
	protected String extractSiteID() {
		return SiteContextManager.getCurrentSiteId();
	}

	private Object getValue(String property, RepositoryItem translation) {
		Object value = null;
		if (null != translation.getPropertyValue(property)) {
			value = translation.getPropertyValue(property);
		}
		if (getLogger().isLoggingDebug()) {
			getLogger().logDebug(
					"Property : " + property + " and Value : " + value
							+ " returned");
		}
		return value;
	}

	private Object getProperyValue(Object pItem, String pPropertyName) {
		if (pItem instanceof RepositoryItemImpl) {
			return ((RepositoryItemImpl) pItem).getPropertyValue(pPropertyName);
		} else {
			return null;
		}
	}

	/**
	 * Determine the derived property value using the specified bean.
	 * 
	 * @param pBean
	 *            the bean representing a repository item whose property value
	 *            is desired
	 * @return the derived value
	 * @exception RepositoryException
	 *                if there is a problem deriving the value
	 **/
	public Object derivePropertyValue(Object pBean) throws RepositoryException {
		throw new UnsupportedFeatureException();
	}

	/**
	 * Determine whether the specified property can be used in a query.
	 * 
	 **/
	public boolean isQueryable() {
		return false;
	}

	/**
	 * Returns LocaleKeyService service.
	 * 
	 * @return RepositoryKeyService The service for which we will get the locale
	 *         to use proper language.
	 */
	public RepositoryKeyService getKeyService() {

		RepositoryKeyService repositoryKeyService = null;
		Nucleus nucleus = Nucleus.getGlobalNucleus();

		if (nucleus != null) {
			Object keyService = nucleus.resolveName(KEY_SERVICE_PATH);

			if (!(keyService instanceof RepositoryKeyService)) {
				if (getLogger().isLoggingDebug()) {
					getLogger()
							.logDebug(
									"The RepositoryKeyService ("
											+ KEY_SERVICE_PATH
											+ ") does not implement atg.repository.dp.RepositoryKeyService");
				}
			} else {
				repositoryKeyService = (RepositoryKeyService) keyService;
			}
		}
		return repositoryKeyService;
	}

}
