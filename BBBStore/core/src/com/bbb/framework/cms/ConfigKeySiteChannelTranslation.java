package com.bbb.framework.cms;

import java.util.List;
import java.util.Set;

import atg.multisite.SiteContextManager;
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
import atg.repository.query.PropertyQueryExpression;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

@SuppressWarnings("serial")
public class ConfigKeySiteChannelTranslation extends DerivationMethodImpl{


	protected static final String DERIVATION_NAME = "SiteChannelDerivation";
	protected static final String DISPLAY_NAME = "Derive by the current site and channel";
	protected static final String SHORT_DESCRIPTION = "Get value mapped to by current site and channel";

	protected static final String SITE_TAG = "{site}";
	protected static final String LANGUAGE_TAG = "{language}";

	protected static final String DEFAULT_LANGUAGE_ATTR = "defaultLanguage";
	protected static final String DEFAULT_SITE_ATTR = "defaultSite";
	protected static final String DEFAULT_PROPERTY="defaultProperty";
	protected static final String DEFAULT_CHANNEL="defaultChannel";
	protected static final String PROPERTY="property";
	protected static final String TRANSLATIONS="translations";
	
	//Property Names of Catalog Repository
	protected static final String SITE_PROPERTY="site";
	protected static final String CHANNEL_PROPERTY="channel";
	protected static final String ATTRIBUTE_PROPERTY="attributeName";
	protected static final String STRING_PROPERTY="attributeValueString";
	protected static final String NUMBER_PROPERTY="attributeValueNumber";
	protected static final String BOOLEAN_PROPERTY="attributeValueBoolean";
	protected static final String CLOB_PROPERTY="attributeValueClob";	
	
	private String mDefaultChannel = null;
	private String mDefaultProperty = null;
	private String mDefaultSiteAttribute = null;
	private String mDefaultLanguageAttribute = null;
	
	private String mProperty = null;
	private String mTranslations = null;


	//static initializer
	static {
		Derivation.registerDerivationMethod(DERIVATION_NAME,ConfigKeySiteChannelTranslation.class);
	}

	/**
	 * Set the name, display name and short description properties.
	 */
	public ConfigKeySiteChannelTranslation() {
		setName(DERIVATION_NAME);
		setDisplayName(DISPLAY_NAME);
		setShortDescription(SHORT_DESCRIPTION);
	}

	public String getDefaultChannel() {
		if(mDefaultChannel == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mDefaultChannel = (String) pd.getValue(DEFAULT_CHANNEL);
		}
		return mDefaultChannel;
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
	
	private static ApplicationLogging mLogger =
			ClassLoggingFactory.getFactory().getLoggerForClass(ConfigKeySiteChannelTranslation.class);

	/**
	 * @return ApplicationLogging object for logger.
	 */
	private ApplicationLogging getLogger()  {
		return mLogger;
	}

	/**
	 * We do not support query for this derivation implementation
	 */
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		String channelId = BBBUtility.getChannel();
		
		String siteId ="";
		if(null != ServletUtil.getCurrentRequest()) {
			siteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
		}
		
		if(siteId==null){
			siteId = ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.SITE_ID);
		}
		
		if(BBBUtility.isEmpty(siteId)) {
			siteId = SiteContextManager.getCurrentSiteId();
		}
		
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("CurrentSiteId from SiteContextManager : "+siteId);
		}
		
		//Setting Default Site ID, if Current site id is null
		if(null == siteId || siteId.isEmpty()){
			siteId = getDefaultSiteAttribute();
			if(getLogger().isLoggingDebug()){
				getLogger().logDebug("Setting Default SiteId : "+siteId);
			}
		}
		
		//If Site Id and channelId are default, returning the Default Value
		if(siteId.equalsIgnoreCase(getDefaultSiteAttribute()) && channelId.equalsIgnoreCase(getDefaultChannel()) ){
			if(getLogger().isLoggingDebug()){
				getLogger().logDebug("site Id equals to default : "+siteId);
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
				RepositoryItem tempChannelItem = null;
		
				for (RepositoryItem translation : translations) {
					
					tempSiteItem = (RepositoryItem) translation.getPropertyValue(SITE_PROPERTY); 
					tempChannelItem = (RepositoryItem) translation.getPropertyValue(CHANNEL_PROPERTY);
					
					//Condition to check for siteId, ChannelId
					
					if (null != tempSiteItem && null != tempChannelItem &&  (tempSiteItem.getRepositoryId().equalsIgnoreCase(siteId)) && tempChannelItem.getRepositoryId().equalsIgnoreCase(channelId)){
                     if(getLogger().isLoggingDebug()){
                            getLogger().logDebug("Found Translation for site: " +siteId+" and channelId: " +channelId+" and attribute: "+ property);
                     }
                     value=this.setValues(translation, value);
                     
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
		
		String stringValue = (String)translation.getPropertyValue(getProperty());
		
		//Checking for each Data Type, if any Data type Value exists, then return same, else return the default value
        if(null != stringValue && !stringValue.isEmpty()){
                        value = stringValue;
                        return value;
        } else {
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
		//throw new UnsupportedFeatureException();
		return pBean.toString();
	}

	/**
	 * Determine whether the specified property can be used in a query.
	 *
	 **/
	public boolean isQueryable() {
		return false;
	}


}
