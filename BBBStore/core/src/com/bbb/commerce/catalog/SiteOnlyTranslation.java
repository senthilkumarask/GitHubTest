package com.bbb.commerce.catalog;

import java.util.List;
import java.util.Map;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;
import com.bbb.utils.BBBJobContextManager;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.MutableRepository;
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
public class SiteOnlyTranslation extends DerivationMethodImpl {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected static final String DERIVATION_NAME = "inventorySiteDerivation";
	protected static final String DISPLAY_NAME = "Derive by the current site and locale";
	protected static final String SHORT_DESCRIPTION = "Get value mapped to by current site and locale";

	protected static final String SITE_TAG = "{site}";
	protected static final String SITE_ID = "siteId";

	protected static final String DEFAULT_SITE_ATTR = "defaultSite";
	protected static final String DEFAULT_PROPERTY="defaultProperty";
	protected static final String DEFAULT_LOCALE="defaultLocale";
	protected static final String ITEM_TYPE="itemType";
	private static String inventoryRepositoryPath = "/atg/commerce/inventory/InventoryRepository";
	@SuppressWarnings("unused")
	private static MutableRepository inventoryRepository;
	private String mDefaultProperty = null;
	private String mItemType = null;


	//static initializer
	static {
		Derivation.registerDerivationMethod(DERIVATION_NAME,SiteOnlyTranslation.class);
		inventoryRepository = (MutableRepository)Nucleus.getGlobalNucleus().resolveName(inventoryRepositoryPath);
	}

	/**
	 * Set the name, display name and short description properties.
	 */
	public SiteOnlyTranslation() {
		setName(DERIVATION_NAME);
		setDisplayName(DISPLAY_NAME);
		setShortDescription(SHORT_DESCRIPTION);
	}


	private String mDefaultSiteAttribute = null;

	public String getDefaultProperty() {
		if(mDefaultProperty == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mDefaultProperty = (String) pd.getValue(DEFAULT_PROPERTY);
		}
		return mDefaultProperty;
	}

	public void setDefaultProperty(String mDefaultProperty) {
		this.mDefaultProperty = mDefaultProperty;
	}

	public String getItemType() {
		if(mItemType == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mItemType = (String) pd.getValue(ITEM_TYPE);
		}
		return mItemType;
	}

	public void setItemType(String mItemType) {
		this.mItemType = mItemType;
	}

	public String getDefaultSiteAttribute() {
		if(mDefaultSiteAttribute == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mDefaultSiteAttribute = (String) pd.getValue(DEFAULT_SITE_ATTR);
		}
		return mDefaultSiteAttribute;
	}

	private static ApplicationLogging mLogger =
		ClassLoggingFactory.getFactory().getLoggerForClass(SiteOnlyTranslation.class);

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

		try {
			if (BBBUtility.isListEmpty(exps)) {//if (exps == null || exps.size() == 0) 
				return null;
			}
			else {
				pe = (PropertyExpression)exps.get(0);
				propName = pe.getPropertyDescriptor().getName();
				if(getLogger().isLoggingDebug()){
					getLogger().logDebug("PropName : "+propName);
				}
				value = String.valueOf(pe.evaluate(pItem));
			}
		}
		catch (IndexOutOfBoundsException ioobe) {
			return null;
		}
		if(propName != null){
			value = getProperyValue(pItem, propName);
			if(getLogger().isLoggingDebug()){
				getLogger().logDebug("Default Value retrieved from propName : "+value+" for inventory ID : "+pItem.getRepositoryId());
			}
		}else{
			value = getProperyValue(pItem, getDefaultProperty());
		}

		String siteId ="";
		if(null != ServletUtil.getCurrentRequest()) {
			siteId = (String) ServletUtil.getCurrentRequest().getAttribute(SITE_ID);
		}
		if (StringUtils.isEmpty(siteId)){
			siteId = getCurrentSiteId();
		}
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("CurrentSiteId from SiteContextManager : "+siteId);
		}
//		If code flow is coming from scheduled job
		Long threadId = Thread.currentThread().getId();
		if (BBBUtility.isEmpty(siteId)) {
			String jobId = BBBJobContextManager.getInstance().getJobInfo()
					.get(threadId.toString());
			if (BBBUtility.isNotEmpty(jobId)
					&& null != BBBJobContextManager.getInstance()
							.getJobContext().get(jobId)) {
				siteId = BBBJobContextManager.getInstance().getJobContext()
						.get(jobId).get(BBBCoreConstants.SITE_ID);
			}
		}
		if(BBBUtility.isEmpty(siteId)){//if(siteId == null || siteId.isEmpty())
			//siteId = getDefaultSiteAttribute(); this is for testing purposes. If SiteId is null return blank in case of Inventory
			//Inventory cannot use cross site values
			if(getLogger().isLoggingDebug()){
				getLogger().logDebug("SiteId is blank so returning defaultValue : "+value);
			}
			return value;

		} else {
			if(siteId.equals("TBS_BedBathUS") ) {
				siteId = "BedBathUS";
			}
			else if(siteId.equals("TBS_BuyBuyBaby") ) {
				siteId = "BuyBuyBaby";			
			}
			else if(siteId.equals("TBS_BedBathCanada") ) {
				siteId = "BedBathCanada";			
			}
			if(siteId.equalsIgnoreCase(getDefaultSiteAttribute())){
				if(getLogger().isLoggingDebug()){
					getLogger().logDebug("SiteId passed is same as defaultSiteAttribute. So returning default value : "+value);
				}
				return value;
			}
		}
		try{
			String itemType = getItemType();
			String translationName = null;
			@SuppressWarnings("unused")
			String itemDescripName = null;
			if(BBBUtility.isEmpty(itemType)){//if(itemType == null || itemType.isEmpty()){
				return value;
			} else {
				if(itemType.equalsIgnoreCase("inventory")){
					translationName = "translations";
					itemDescripName = "inventoryTranslation";
			}
			}
			Map<String,RepositoryItem> translations = (Map<String, RepositoryItem>)getProperyValue(pItem, translationName);

			if(BBBUtility.isMapNullOrEmpty(translations)){//if(translations == null || translations.isEmpty())
				if(getLogger().isLoggingDebug()){
					getLogger().logDebug("Translations is null. So returning default value");
				}
				return value;
			} else{
				RepositoryItem translation = null;
				translation = (RepositoryItem)translations.get(siteId);

				if(translation != null){
					if(getLogger().isLoggingDebug()){
						getLogger().logDebug("Translation is not null. So determining the return value");
					}
					Long translationDetailRetValue = (Long)translation.getPropertyValue(getDerivation().getPropertyDescriptor().getName());

					if(translationDetailRetValue != null){
						if(getLogger().isLoggingDebug()){
							getLogger().logDebug("Return value : "+translationDetailRetValue);
						}
						return translationDetailRetValue;
					} else {
						if(getLogger().isLoggingDebug()){
							getLogger().logDebug("Return value is zero as no translation found for site : "+0);
						}
						return Long.valueOf(0);//Return zero for Inventory
					}
				} else {
					if(getLogger().isLoggingDebug()){
						getLogger().logDebug("Translation is null. So return default value : "+value);
					}
					return value;
				}
			}
		} catch(Exception e){
			getLogger().logError(e);
		}
		return value;
	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
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

}
