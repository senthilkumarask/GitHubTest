package com.bbb.commerce.catalog;

import java.util.List;

import com.bbb.utils.BBBUtility;

import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.UnsupportedFeatureException;
import atg.repository.dp.Derivation;
import atg.repository.dp.DerivationMethodImpl;
import atg.repository.dp.PropertyExpression;
import atg.repository.query.PropertyQueryExpression;

/**
 * This derived property method will derive a property based on the Scene7URL for Product and skus
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
public class MediaTranslation extends DerivationMethodImpl {

	protected static final String DERIVATION_NAME = "MediaDerivation";
	protected static final String DISPLAY_NAME = "Derive Other Images depending on scene7 URL";
	protected static final String SHORT_DESCRIPTION = "Derive Other Images depending on scene7 URL for Products and Skus";
	protected static final String SCENE7_URL = "scene7URL";
	protected static final String APPEND_VALUE = "appendValue";
	private String mScene7URL = null;
	private String mAppendValue = null;


	//static initializer
	static {
		Derivation.registerDerivationMethod(DERIVATION_NAME,MediaTranslation.class);
	}

	/**
	 * Set the name, display name and short description properties.
	 */
	public MediaTranslation() {
		setName(DERIVATION_NAME);
		setDisplayName(DISPLAY_NAME);
		setShortDescription(SHORT_DESCRIPTION);
	}

	public String getScene7URL() {
		if(mScene7URL == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mScene7URL = (String) pd.getValue(SCENE7_URL);
		}
		return mScene7URL;
	}

	public void setScene7URL(String mScene7URL) {
		this.mScene7URL = mScene7URL;
	}

	public String getAppendValue() {
		if(mAppendValue == null) {
			RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
			mAppendValue = (String) pd.getValue(APPEND_VALUE);
		}
		return mAppendValue;
	}

	public void setAppendValue(String mAppendValue) {
		this.mAppendValue = mAppendValue;
	}
	
	
	private static ApplicationLogging mLogger =
			ClassLoggingFactory.getFactory().getLoggerForClass(MediaTranslation.class);

	/**
	 * @return ApplicationLogging object for logger.
	 */
	public ApplicationLogging getLogger()  {
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
	@SuppressWarnings("rawtypes")
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
			if (BBBUtility.isListEmpty(exps)) {
				return value;
			}
			else {
				pe = (PropertyExpression)exps.get(0);
				if(pe != null){
					propName = pe.getPropertyDescriptor().getName();
					if(getLogger().isLoggingDebug()){
						getLogger().logDebug("PropName : "+propName);
					}
					value = String.valueOf(pe.evaluate(pItem));
				}
			}
		} catch(Exception e){
			return null;
		}
		if(null == propName) {
			value = getProperyValue(pItem, getScene7URL());
		} 
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("Default Value : "+value);
		}
		
		try{
			if(null != pItem){
				
				String appendValue = getAppendValue();
				String scene7URL = getScene7URL();
				
					if (null != pItem.getPropertyValue(scene7URL) && null != scene7URL) {
						
						String scene7URLValue = (String) pItem.getPropertyValue(SCENE7_URL);
						value = scene7URLValue;
						if (null != appendValue){
						
							
							if(getLogger().isLoggingDebug()){
								getLogger().logDebug("scene7URL: "+scene7URLValue+ " ++ AND appendValue: "+appendValue);
							}
							 value = scene7URLValue + appendValue;
						 
						}else{
							return value;
						}
					} else {
						return value;
					}
				
				
			}else {
				return value;
			}
		} catch(Exception e){
			getLogger().logError(e);
		}
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("value returned from derivation: " + value);
		}
		return value;
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
