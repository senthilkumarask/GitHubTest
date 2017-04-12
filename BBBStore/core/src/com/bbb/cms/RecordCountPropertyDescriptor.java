package com.bbb.cms;

import java.util.Collection;
import java.util.List;

import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
/**
 * This class computes the no of items in a property of item descriptor of type list
 * @author njai13
 *
 */
public class RecordCountPropertyDescriptor extends RepositoryPropertyDescriptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;




	//-------------------------------------

	protected static final String TYPE_NAME = "recordCount";

	/** Values of these attributes for this particular property */
	protected String propertyToCount = null; 


	static {
		RepositoryPropertyDescriptor.registerPropertyDescriptorClass(TYPE_NAME, 
				RecordCountPropertyDescriptor.class);
	}

	//-------------------------------------
	/**
	 * Constructs a new RecordCountPropertyDescriptor
	 **/
	public RecordCountPropertyDescriptor () {
		super();
	}

	//-------------------------------------
	/**
	 * Constructs a new RecordCountPropertyDescriptor for a particular property.
	 **/
	public RecordCountPropertyDescriptor(String pPropertyName)
	{
		super(pPropertyName);
	}

	//-------------------------------------
	/**
	 * Constructs a new RepositoryPropertyDescriptor with the given 
	 * property name, property type, and short description.
	 **/
	@SuppressWarnings("rawtypes")
	public RecordCountPropertyDescriptor(String pPropertyName, 
			Class pPropertyType,
			String pShortDescription)
	{
		super(pPropertyName, pPropertyType, pShortDescription);
	}

	//-------------------------------------
	/**
	 * Returns property Queryable
	 **/
	public boolean isQueryable() {
		return false;
	}

	//-------------------------------------
	/**
	 * Returns property Writable.
	 */
	public boolean isWritable() {
		return false;
	}

	private static ApplicationLogging mLogger =
			ClassLoggingFactory.getFactory().getLoggerForClass(RecordCountPropertyDescriptor.class);

	/**
	 * @return ApplicationLogging object for logger.
	 */
	private ApplicationLogging getLogger()  {
		return mLogger;
	}

	//-------------------------------------
	/**
	 * This method is called to retrieve a read-only value for this property.
	 *
	 * Once a repository has computed the value it would like to return for
	 * this property, this property descriptor gets a chance to modify it
	 * based on how the property is defined.  For example, if null is to
	 * be returned, we return the default value.
	 */
	@SuppressWarnings("unchecked")
	public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
		List<RepositoryItemImpl> propertyList=null;
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("In method getPropertyValue for item "+pItem +" and property for which count is required "+propertyToCount);
		}
		String defaultSize="0";

		if(pItem!=null && propertyToCount != null){

			if(pItem.getPropertyValue(propertyToCount) instanceof Collection){
				Collection<RepositoryItemImpl> property=(Collection<RepositoryItemImpl>) pItem.getPropertyValue(propertyToCount);
				if (property != null) {
					if(getLogger().isLoggingDebug()){
						getLogger().logDebug("No of items in property "+propertyToCount +" are "+String.valueOf(property.size()));
					}
					return String.valueOf(property.size());
				}
			}
		}
		if(getLogger().isLoggingDebug()){
			getLogger().logDebug("No items in property "+propertyToCount +" returning default count of 0");
		}
		return defaultSize;
	}

	/**
	 * Catch the attribute values that we care about and store them in 
	 * member variables.
	 */
	public void setValue(String pAttributeName, Object pValue) {
		super.setValue(pAttributeName, pValue);

		if (pValue == null || pAttributeName == null) return;
		if (pAttributeName.equalsIgnoreCase("propertyToCount")){
			propertyToCount = pValue.toString();
		}

	}

	/**
	 * Returns the name this type uses in the XML file.
	 */
	public String getTypeName() {
		return TYPE_NAME;
	}



}

