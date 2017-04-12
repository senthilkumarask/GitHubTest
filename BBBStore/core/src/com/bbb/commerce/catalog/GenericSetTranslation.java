package com.bbb.commerce.catalog;

import java.util.List;
import java.util.Set;

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
//import atg.repository.dp.PropertyExpression;
import atg.repository.query.PropertyQueryExpression;

import com.bbb.utils.BBBUtility;

/**
 * @author agu117
 *
 */
@SuppressWarnings("serial")
public class GenericSetTranslation extends DerivationMethodImpl {

	protected static final String DERIVATION_NAME = "GenericSetTranslation";
	protected static final String DISPLAY_NAME = "Derive Display Name of Items in the Set";
	protected static final String SHORT_DESCRIPTION = "Derive Display Name of Items in the Set. "
			+ "This provides Id for Item Descriptors by concatenating the Value from a property, "
			+ "passed as a parameter, that belongs to the item descriptor of the items in the Set.";
	protected static final String PROPERTY = "property";
	protected static final String ITEM_DESC_PROPERTY = "itemDescProperty";
	protected static final String SEPERATOR = ", ";

	private String mProperty;
	private String mItemDescProperty;

	//static initializer
	static {
		Derivation.registerDerivationMethod(DERIVATION_NAME, GenericSetTranslation.class);
	}

	/**
	 * Set the name, display name and short description properties.
	 */
	public GenericSetTranslation() {
		setName(DERIVATION_NAME);
		setDisplayName(DISPLAY_NAME);
		setShortDescription(SHORT_DESCRIPTION);
	}

	/**
	 * @return
	 */
	public String getProperty() {
		if (this.mProperty == null) {
			final RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			this.mProperty = (String) pd.getValue(PROPERTY);
		}
		return this.mProperty;
	}
	
	/**
	 * @param mProperty
	 */
	public void setProperty(String mProperty) {
		this.mProperty = mProperty;
	}
	
	/**
	 * @return
	 */
	public String getItemDescProperty() {
		if (this.mItemDescProperty == null) {
			final RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			this.mItemDescProperty = (String) pd.getValue(ITEM_DESC_PROPERTY);
		}
		return this.mItemDescProperty;
	}

	/**
	 * @param mItemDescProperty
	 */
	public void setItemDescProperty(String mItemDescProperty) {
		this.mItemDescProperty = mItemDescProperty;
	}

	private static ApplicationLogging mLogger =
			ClassLoggingFactory.getFactory().getLoggerForClass(GenericSetTranslation.class);

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object derivePropertyValue(RepositoryItemImpl pItem)
			throws RepositoryException {
		if (this.getLogger().isLoggingDebug()) {
			this.getLogger().logDebug("In derive property value ");
		}
		
		//final List exps = getDerivation().getExpressionList();
		
		Object value = null;
		final String propName = this.getProperty();
		
		try {
			if (null != pItem) {
				if (null == propName) {
					value = pItem.getPropertyValue("ID");
					if (this.getLogger().isLoggingDebug()) {
						this.getLogger().logDebug("Default Value : " + value);
					}
				} else {
					final Set<RepositoryItem> itemSet = (Set<RepositoryItem>) 
							getProperyValue(pItem, propName);

					if ((itemSet == null || itemSet.isEmpty()) || BBBUtility.isEmpty(this.getItemDescProperty())) {
						if (this.getLogger().isLoggingDebug()) {
							this.getLogger().logDebug("Item Set is null. So default value");
						}
					} else {
						String tempPropValue = null;
						String tempValue = "";
						for (RepositoryItem item : itemSet) {
							tempPropValue = (String) item.getPropertyValue(this.getItemDescProperty());
							
							if (BBBUtility.isNotEmpty(tempPropValue)) {
								if (this.getLogger().isLoggingDebug()) {
									this.getLogger().logDebug("Found Property Value: " + tempPropValue);
								}
								tempValue = tempValue + tempPropValue + SEPERATOR;
							}
						}
						value = tempValue.substring(0, tempValue.length() - 2);
					}	
				}
			} 
		} catch (Exception e) {
			this.getLogger().logError(e);
		}
		if (this.getLogger().isLoggingDebug()) {
			this.getLogger().logDebug("Value returned from derivation: " + value);
		}
		return value;
	}
	
	private Object getProperyValue(final Object pItem, final String pPropertyName) {
		if (pItem instanceof RepositoryItemImpl) {
			return ((RepositoryItemImpl) pItem).getPropertyValue(pPropertyName);
		} else {
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
