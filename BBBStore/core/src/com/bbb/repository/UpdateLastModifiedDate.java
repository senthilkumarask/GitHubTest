package com.bbb.repository;

import atg.adapter.gsa.GSAPropertyDescriptor;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

public class UpdateLastModifiedDate extends GSAPropertyDescriptor {
	    /**
	     * generated UID.updateParentLastModifiedPropertyName
	     */
	    private static final long serialVersionUID = -1342026296442111782L;

	    /**
	     * The encryptor component path property.lastModifiedDateShipGroup
	     */
	    private static final String UPDATE_PARENT_LAST_MODIF_PROPERTY = "updateParentLastModifiedProperty";

	    private static final String UPDATE_PARENT_LAST_MODIFIED = "updateParentLastModified";

	    /**
	     * The type name property.
	     */
	    private static final String TYPE_NAME = "parentLastModifiedDate";


	    private static final String URL_PATTERN = "dyn/admin";


	    static {
		RepositoryPropertyDescriptor.registerPropertyDescriptorClass(TYPE_NAME, UpdateLastModifiedDate.class);
	    }

	    private String updateParentLastModifiedProperty;

	    public String getUpdateParentLastModifiedProperty() {
			return updateParentLastModifiedProperty;
		}

		public void setUpdateParentLastModifiedProperty(
				String updateParentLastModifiedProperty) {
			this.updateParentLastModifiedProperty = updateParentLastModifiedProperty;
		}

		private boolean updateParentLastModified;

	    public boolean isUpdateParentLastModified() {
			return updateParentLastModified;
		}

		public void setUpdateParentLastModified(boolean updateParentLastModified) {
			this.updateParentLastModified = updateParentLastModified;
		}

		/**
	     * Constructs a UpdateLastModifiedDate.
	     */
	    public UpdateLastModifiedDate() {
		super();
	    }

	    /**
	     * Returns property Queryable.
	     *
	     * @return <code>true</code> as the property is always queryable
	     */
	    public boolean isQueryable() {
		return true;
	    }

	    /**
	     * Sets the property of this type for the item descriptor provided.
	     *
	     * @param pItem
	     *                the RepositoryItem to set the value for
	     * @param pValue
	     *                the value to set to the item.
	     */
	    public void setPropertyValue(final RepositoryItemImpl pItem, final Object pValue) {


	    	if (pValue == null) {
			    return;
			}
			if(updateParentLastModified){
		        pItem.setPropertyValue(UPDATE_PARENT_LAST_MODIF_PROPERTY, pValue);
		    }
		       super.setPropertyValue(pItem, pValue);
	    }

	    /**
	     * Returns the value of the underlying property.
	     *
	     * @param pItem
	     *                the RepositoryItem to retrieve the value from
	     * @param pValue
	     *                the value to retrieve
	     * @return The property value requested
	     */
	    public Object getPropertyValue(final RepositoryItemImpl pItem, final Object pValue) {

			return super.getPropertyValue(pItem, pValue);
	    }

	    /**
	     * Catch the attribute values that we care about and store them in member variables.
	     *
	     * @param pAttributeName
	     *                the Attribute to set
	     * @param pValue
	     *                the Value to set to the attribute
	     */
		public void setValue(final String pAttributeName, final Object pValue) {
			super.setValue(pAttributeName, pValue);
			if (pValue == null || pAttributeName == null) {
				return;
			}
			if (pAttributeName.equalsIgnoreCase(UPDATE_PARENT_LAST_MODIF_PROPERTY)){
				this.setUpdateParentLastModifiedProperty(pAttributeName);pValue.toString();
			} else if (pAttributeName.equalsIgnoreCase(UPDATE_PARENT_LAST_MODIFIED)){
				this.setUpdateParentLastModified(Boolean.parseBoolean(pValue.toString()));
			}
		}

}
