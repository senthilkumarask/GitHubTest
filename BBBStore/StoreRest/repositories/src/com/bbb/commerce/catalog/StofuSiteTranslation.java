package com.bbb.commerce.catalog;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import atg.core.util.StringUtils;
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

/**
 * This derived property method will derive a property based on the current site and 
 * profile's current locale.
 * 
 * For example:<br>
 * <pre>
 *   &lt;property name="imagePath"&gt;
 *     &lt;derivation user-method="atg.projects.store.dp.StoreContextDerivation"&gt;
 *       &lt;expression&gt;image&lt;/expression&gt;
 *     &lt;/derivation&gt;
 *   &lt;/property&gt;
 * </pre>;
 * 
 * 
 **/
@SuppressWarnings("serial")
public class StofuSiteTranslation extends DerivationMethodImpl {

	protected static final String DERIVATION_NAME = "SiteDerivation";
	protected static final String DISPLAY_NAME = "Derive by the current site and locale";
	protected static final String SHORT_DESCRIPTION = "Get value mapped to by current site and locale";

	protected static final String KEY_SERVICE_PATH = "/atg/userprofiling/LocaleService";
	protected static final String SITE_TAG = "{site}";
	protected static final String LANGUAGE_TAG = "{language}";

	protected static final String DEFAULT_LANGUAGE_ATTR = "defaultLanguage";
	protected static final String DEFAULT_SITE_ATTR = "defaultSite";
	protected static final String DEFAULT_PROPERTY="defaultProperty";
	protected static final String DEFAULT_LOCALE="defaultLocale";
	protected static final String PROPERTY="property";
	protected static final String TRANSLATIONS="translations";
	protected static final String SITE_ID = "siteId";
	
	//Property Names of Catalog Repository
	protected static final String SITE_PROPERTY="site";
	protected static final String LOCALE_PROPERTY="locale";
	protected static final String ATTRIBUTE_PROPERTY="attributeName";
	protected static final String STRING_PROPERTY="attributeValueString";
	protected static final String NUMBER_PROPERTY="attributeValueNumber";
	protected static final String BOOLEAN_PROPERTY="attributeValueBoolean";
	protected static final String CLOB_PROPERTY="attributeValueClob";
	protected static final String DATE_PROPERTY="attributeValueDate";
	protected static final String GS_SITE_PREFIX="gsSitePrefix";
	
	private String mDefaultLocale = null;
	private String mDefaultProperty = null;
	private String mDefaultSiteAttribute = null;
	private String mDefaultLanguageAttribute = null;
	
	private String mProperty = null;
	private String mTranslations = null;
	private String mGSSitePrefix = null;


	//static initializer
	static {
		Derivation.registerDerivationMethod(DERIVATION_NAME,StofuSiteTranslation.class);
	}

	/**
	 * Set the name, display name and short description properties.
	 */
	public StofuSiteTranslation() {
		setName(DERIVATION_NAME);
		setDisplayName(DISPLAY_NAME);
		setShortDescription(SHORT_DESCRIPTION);
	}


	

	public String getDefaultLocale() {
		if(mDefaultLocale == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mDefaultLocale = (String) pd.getValue(DEFAULT_LOCALE);
		}
		return mDefaultLocale;
	}

	

	public String getDefaultProperty() {
		if(mDefaultProperty == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mDefaultProperty = (String) pd.getValue(DEFAULT_PROPERTY);
		}
		return mDefaultProperty;
	}

	public String getDefaultSiteAttribute() {
		if(mDefaultSiteAttribute == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mDefaultSiteAttribute = (String) pd.getValue(DEFAULT_SITE_ATTR);
		}
		return mDefaultSiteAttribute;
	}

	

	public String getDefaultLanguageAttribute() {
		if(mDefaultLanguageAttribute == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mDefaultLanguageAttribute = (String) pd.getValue(DEFAULT_LANGUAGE_ATTR);
		}
		return mDefaultLanguageAttribute;
	}
	
	public String getProperty() {
		if(mProperty == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mProperty = (String) pd.getValue(PROPERTY);
		}
		return mProperty;
	}
	
	public String getTranslations() {
		if(mTranslations == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mTranslations = (String) pd.getValue(TRANSLATIONS);
		}
		return mTranslations;
	}
	
	public String getGSSitePrefix() {
		if(mGSSitePrefix == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mGSSitePrefix = (String) pd.getValue(GS_SITE_PREFIX);
		}
		return mGSSitePrefix;
	}
	
	private static ApplicationLogging mLogger =
			ClassLoggingFactory.getFactory().getLoggerForClass(StofuSiteTranslation.class);

	/**
	 * @return ApplicationLogging object for logger.
	 */
	private ApplicationLogging getLogger()  {
		return mLogger;
	}

	/**
	 * We do not support query for this derivation implementation
	 */
	protected Query createQuery(int pQueryType, boolean pDerivedPropertyOnLeft,
			boolean pCountDerivedProperty, QueryExpression pOther, int pOperator,
			boolean pIgnoreCase, QueryExpression pMinScore,
			QueryExpression pSearchStringFormat, Query pItemQuery,
			QueryBuilder pBuilder, PropertyQueryExpression pParentProperty,
			List pChildPropertyList) throws RepositoryException {

		throw new UnsupportedFeatureException();
	}

	/**
	 * Determine the derived property value.
	 *
	 * @param pItem the item whose property value is desired
	 * @return the derived value
	 * @exception RepositoryException if there is a problem deriving the value
	 **/
	@SuppressWarnings("unchecked")
	public Object derivePropertyValue(RepositoryItemImpl pItem)
			throws RepositoryException {
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("In derive property value ");
		}

		List exps = getDerivation().getExpressionList();

		PropertyExpression pe;
		Object value = null;
		String propName = null;

		//Retrieving the default value using the Expression
		try {
			if (null == exps || exps.size() == 0) {
				return null;
			}
			else {
				pe = (PropertyExpression)exps.get(0);
				if(null != pe){
					propName = pe.getPropertyDescriptor().getName();
					if(getLogger().isLoggingDebug()){
						getLogger().logDebug("PropName : "+propName);
					}
					if(getDefaultProperty()!=null) {
					value = pe.evaluate(pItem);
					}
				}
			}
		} catch (IndexOutOfBoundsException ioobe) {
			return null;
		} catch(Exception e){
			return null;
		}
		
		//Using the DefaultProperty to set default value, if expression is null
		if(null == propName && getDefaultProperty()!=null) {
			value = getProperyValue(pItem, getDefaultProperty());
		} 
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("Default Value : "+value);
		}

		String siteId ="";
		if(null != ServletUtil.getCurrentRequest()) {
			siteId = (String) ServletUtil.getCurrentRequest().getAttribute(SITE_ID);
		}
		if (StringUtils.isEmpty(siteId)){
			siteId = SiteContextManager.getCurrentSiteId();
		}
		String webDefaultSiteId = null;
		//testing Purpose, comment above line and uncomment below line.
		//String siteId ="BuyBuyBaby";
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("CurrentSiteId from SiteContextManager : "+siteId);
		}
		
		//Setting Default Site ID, if Current site id is null
		if(null == siteId || siteId.isEmpty()){
			siteId = getDefaultSiteAttribute();
			if(getLogger().isLoggingDebug()){
				getLogger().logDebug("Setting Default SiteId : "+siteId);
			}
		}//STOFU CHANGE: To get the web site id without GS_
		else {
			if(siteId.contains(getGSSitePrefix())){
				webDefaultSiteId = siteId.replace(getGSSitePrefix(), "");
			}
		}
		
		String reqLocale = null;
		RepositoryKeyService keySvice = getKeyService();
		
		//Setting Default Locale, if Current locale is null
		if(null != keySvice ){
				reqLocale = keySvice.getRepositoryKey().toString();
		} else {
				reqLocale = getDefaultLocale();
		}
		
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("LocaleStr : "+reqLocale);
		}
		
		if(siteId.equals("TBS_BedBathUS") ) {
			siteId = "BedBathUS";
		}
		else if(siteId.equals("TBS_BuyBuyBaby") ) {
			siteId = "BuyBuyBaby";			
		}
		else if(siteId.equals("TBS_BedBathCanada") ) {
			siteId = "BedBathCanada";			
		}
		
		//If Site Id and Locale are default, returning the Default Value
		if(siteId.equalsIgnoreCase(getDefaultSiteAttribute()) && reqLocale.equalsIgnoreCase(getDefaultLocale()) ){
			if(getLogger().isLoggingDebug()){
				getLogger().logDebug("site Id equals to default : "+siteId);
				getLogger().logDebug("Locale equals to default : "+reqLocale);
			}
			if (null==value) {
				if(pe!=null){
					value=pe.evaluate(pItem);	
				}
			}
			return value;
		}
		
		try{
			String repositoryId = pItem.getRepositoryId();
			if(getLogger().isLoggingDebug()){
				getLogger().logDebug("Repository ID : "+repositoryId);
			}
			
			String translationName = getTranslations();
			String property = getProperty();
						
			Set<RepositoryItem> translations = (Set)getProperyValue(pItem, translationName);
			
			//If translations are empty AND Property is not defined, returning the Default Value
			if( (null == translations || translations.isEmpty() ) || (null == property || property.isEmpty())){
				if(getLogger().isLoggingDebug()){
					getLogger().logDebug("Translations is null. So returning default value");
				}
				return value;
			} else {				
				RepositoryItem tempSiteItem = null;
				String tempLocale = null;
				String tempAttributeName = null;
				for (RepositoryItem translation : translations) {
					
					tempSiteItem = (RepositoryItem) translation.getPropertyValue(SITE_PROPERTY); 
					tempLocale = (String) translation.getPropertyValue(LOCALE_PROPERTY);
					tempAttributeName = (String) translation.getPropertyValue(ATTRIBUTE_PROPERTY);
					
					//Condition to check for siteId, reqLocale and attribute
					if ((null != tempSiteItem && (tempSiteItem.getRepositoryId().equalsIgnoreCase(siteId)))
							&& (null != tempAttributeName && (tempAttributeName.equalsIgnoreCase(property))) ){
						if(getLogger().isLoggingDebug()){
							getLogger().logDebug("Found Translation for site: " +siteId+" and attribute: "+ property);
						}
						
						if(null != tempLocale && (tempLocale.equalsIgnoreCase(getDefaultLocale()))){
							if(getLogger().isLoggingDebug()){
								getLogger().logDebug("Found Translation for  default Locale:"+getDefaultLocale());
							}
							value=this.setValues(translation, value);
							
							
						}else if (null != tempLocale && (tempLocale.equalsIgnoreCase(reqLocale))){
							if(getLogger().isLoggingDebug()){
								getLogger().logDebug("Found Translation for Locale:"+reqLocale);
							}
							value=this.setValues(translation, value);
							if(getLogger().isLoggingDebug()){
								getLogger().logDebug("Final Returned Value" +value);
							}
							return value;
						}		
					} //STOFU CHANGE: To check whether the translation value is against the web site id w/o GS_
					else if ((null != tempSiteItem && (tempSiteItem.getRepositoryId().equalsIgnoreCase(webDefaultSiteId)))
							&& (null != tempAttributeName && (tempAttributeName.equalsIgnoreCase(property))) ){
						if(getLogger().isLoggingDebug()){
							getLogger().logDebug("Found Translation for webDefaultSiteId: " +webDefaultSiteId+" and attribute: "+ property);
						}
						
						if(null != tempLocale && (tempLocale.equalsIgnoreCase(getDefaultLocale()))){
							if(getLogger().isLoggingDebug()){
								getLogger().logDebug("Found Translation for  default Locale:"+getDefaultLocale());
							}
							value=this.setValues(translation, value);
							
							
						}else if (null != tempLocale && (tempLocale.equalsIgnoreCase(reqLocale))){
							if(getLogger().isLoggingDebug()){
								getLogger().logDebug("Found Translation for Locale:"+reqLocale);
							}
							value=this.setValues(translation, value);
							if(getLogger().isLoggingDebug()){
								getLogger().logDebug("Final Returned Value" +value);
							}
						}		
					}
					
				}
			}
		} catch(Exception e){
			getLogger().logError(e);
		}
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("Final Returned Value" +value);
		}
		return value;
	}
	
	private Object setValues(RepositoryItem translation, Object defaultValue){
		
		Object value = null;
		
		String stringValue = (String)translation.getPropertyValue(STRING_PROPERTY);
		Double intValue = (Double)translation.getPropertyValue(NUMBER_PROPERTY);
		Boolean boolValue = (Boolean)translation.getPropertyValue(BOOLEAN_PROPERTY);
		String clobValue = (String)translation.getPropertyValue(CLOB_PROPERTY);
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
		}else {
			if(getLogger().isLoggingDebug()){
				getLogger().logDebug("No Value found in Tranlsations");
			}
			return defaultValue;
		}
		
	}

	private Object getProperyValue(Object pItem, String pPropertyName){
		if(pItem instanceof RepositoryItemImpl){
			return ((RepositoryItemImpl)pItem).getPropertyValue(pPropertyName);
		} else{
			return null;
		}
	}

	/**
	 * Determine the derived property value using the specified bean.
	 *
	 * @param pBean the bean representing a repository item whose property
	 * value is desired
	 * @return the derived value
	 * @exception RepositoryException if there is a problem deriving the value
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
	 * to use proper language.
	 */
	public RepositoryKeyService getKeyService() {

		RepositoryKeyService repositoryKeyService = null;
		Nucleus nucleus = Nucleus.getGlobalNucleus();

		if (nucleus != null) {
			Object keyService = nucleus.resolveName(KEY_SERVICE_PATH);

			if (!(keyService instanceof RepositoryKeyService)) {
				if (getLogger().isLoggingDebug()){
					getLogger().logDebug("The RepositoryKeyService (" + KEY_SERVICE_PATH +
							") does not implement atg.repository.dp.RepositoryKeyService");
				}
			} else {
				repositoryKeyService = (RepositoryKeyService)keyService;
			}
		}
		return repositoryKeyService;
	}

}
