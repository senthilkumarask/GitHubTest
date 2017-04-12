/*
 *
 * File  : EncryptionPropertyDescriptor.java
 * Project:     BBB
 */
package com.bbb.framework.crypto;

import atg.adapter.gsa.GSAPropertyDescriptor;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryImpl;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.servlet.ServletUtil;

/**
 * A property descriptor to encrypt and decrypt string values.
 * 
 * Adapted from atg.repository.PasswordPropertyDescriptor
 * 
 *
 */
public class EncryptionPropertyDescriptor extends GSAPropertyDescriptor {
    /**
     * generated UID.
     */
    private static final long serialVersionUID = -1342026296442111782L;

    /**
     * The type name property.
     */
    private static final String TYPE_NAME = "encrypted";

    /**
     * The encryptor component path property.
     */
    private static final String ENCRYPTOR_COMPONENT = "encryptorComponent";
    
    private static final String URL_PATTERN = "dyn/admin";

    /**
     * The encryptor key property name.
     */
    //private static final String KEY = "key";

    static {
	RepositoryPropertyDescriptor.registerPropertyDescriptorClass(TYPE_NAME, EncryptionPropertyDescriptor.class);
    }

    /**
     * The Encryptor Component to use.
     */
    private AbstractEncryptorComponent mEncryptorComponent;

    /**
     * The encryption key.
     */
    //private String mKey;

    /**
     * Constructs a EncryptionPropertyDescriptor.
     */
    public EncryptionPropertyDescriptor() {
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
	if (this.mEncryptorComponent == null) {
	    logError("Property Item Descriptor: " + getItemDescriptor().getItemDescriptorName() + "." + getName()
		    + " not property configured.");
	    throw new IllegalArgumentException("Encryptor Component not configured "
		    + getItemDescriptor().getItemDescriptorName() + "."
		    + getPropertyItemDescriptor().getItemDescriptorName());
	}
	try {
	    extractSuperCall(pItem, pValue);
	} catch (final EncryptorException ee) {
	    logError("Failed to encrypt property: ", ee);
	    throw new IllegalArgumentException("Failed to encrypt property "
		    + getItemDescriptor().getItemDescriptorName(), ee);
	}
    }

	/**
	 * @param pItem
	 * @param pValue
	 * @throws EncryptorException
	 */
	protected void extractSuperCall(final RepositoryItemImpl pItem, final Object pValue) throws EncryptorException {
		super.setPropertyValue(pItem, this.mEncryptorComponent.encryptString(pValue.toString()));
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
	// If the object value is null or "the null object", then simply return null.
	if ((pValue == null) || pValue == RepositoryItemImpl.NULL_OBJECT) {
	    return null;
	}
	String requestUri = null;
	if (ServletUtil.getCurrentRequest() != null) {
		requestUri = ServletUtil.getCurrentRequest().getRequestURI();
	}
	if (requestUri == null || (requestUri != null && !requestUri.contains(URL_PATTERN))) {
		if (this.mEncryptorComponent == null) {
		    logError("Property Item Descriptor: " + getItemDescriptor().getItemDescriptorName() + "." + getName()
			    + " not property configured.");
		    throw new IllegalArgumentException("Encryptor Component not configured "
			    + getItemDescriptor().getItemDescriptorName() + "." + this.getName());
		}
		try {
		    return extractSuperGetCall(pItem, pValue);
		} catch (final EncryptorException ee) {
		    logError("Failed to decrypt property: [" + pValue.toString() + "]", ee);
		}
	}	
	return extractSuperGet(pItem, pValue);
    }

	/**
	 * @param pItem
	 * @param pValue
	 * @return
	 */
	protected Object extractSuperGet(final RepositoryItemImpl pItem, final Object pValue) {
		return super.getPropertyValue(pItem, pValue);
	}

	/**
	 * @param pItem
	 * @param pValue
	 * @return
	 * @throws EncryptorException
	 */
	protected Object extractSuperGetCall(final RepositoryItemImpl pItem, final Object pValue)
			throws EncryptorException {
		return super.getPropertyValue(pItem, this.mEncryptorComponent.decryptString(pValue.toString()));
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
		extractSuperSetCall(pAttributeName, pValue);
		if (pValue == null || pAttributeName == null) {
			return;
		}
		if (pAttributeName.equalsIgnoreCase(ENCRYPTOR_COMPONENT)){
//			Nucleus nucleus = Nucleus.getGlobalNucleus();
			mEncryptorComponent = (AbstractEncryptorComponent) Nucleus
					.getGlobalNucleus().resolveName(pValue.toString());
		}
	}

	/**
	 * @param pAttributeName
	 * @param pValue
	 */
	protected void extractSuperSetCall(final String pAttributeName, final Object pValue) {
		super.setValue(pAttributeName, pValue);
	}

    /**
     * Logs an error for the repository we are part of.
     * 
     * @param pError
     *                The error string to log
     */
    public void logError(final String pError) {
	logError(pError, null);
    }

    /**
     * Log an error with an exception for the repository we are part of.
     * 
     * @param pError
     *                The error string to log
     * @param pThrowable
     *                The exception to log
     */
    protected void logError(final String pError, final Throwable pThrowable) {
	if (getItemDescriptor() != null) {
	    final RepositoryImpl repositoryImpl = (RepositoryImpl) getItemDescriptor().getRepository();
	    if (repositoryImpl.isLoggingError()) {
		repositoryImpl.logError("Error with repository property: " + getName() + " item-descriptor "
			+ getItemDescriptor().getItemDescriptorName() + ": " + pError, pThrowable);
	    }
	}
    }

    /**
     * Logs a debug statement for the repository we are part of.
     * 
     * @param pMessage
     *                the Message to log
     */
    public void logDebug(final String pMessage) {
	if (getItemDescriptor() != null) {
	    final RepositoryImpl repositoryImpl = (RepositoryImpl) getItemDescriptor().getRepository();
	    if (repositoryImpl.isLoggingDebug()) {
		repositoryImpl.logDebug("Repository property: " + getName() + " item-descriptor "
			+ getItemDescriptor().getItemDescriptorName() + ": " + pMessage);
	    }
	}
    }

    /**
     * Returns the name this type uses in the XML file.
     * 
     * @return String The type for this property descriptor
     */
    public String getTypeName() {
	return TYPE_NAME;
    }

    /**
     * @return java.lang.String
     */
    @SuppressWarnings("rawtypes")
	public Class getPropertyType() {
	return java.lang.String.class;
    }

    /**
     * Perform type checking.
     * 
     * @param pClass
     *                The class of this property (data type)
     */
    @SuppressWarnings("rawtypes")
	public void setPropertyType(final Class pClass) {
	if (pClass != java.lang.String.class) {
	    throw new IllegalArgumentException("encrypted properties must be java.lang.String");
	}
	super.setPropertyType(pClass);
    }

    /**
     * @param pClass
     *                the Component property type
     */
    @SuppressWarnings("rawtypes")
	public void setComponentPropertyType(final Class pClass) {
	if (pClass != null) {
	    throw new IllegalArgumentException("encrypted properties must be scalars");
	}
    }

    /**
     * @param pDesc
     *                the Property Item Descriptor
     */
    public void setPropertyItemDescriptor(final RepositoryItemDescriptor pDesc) {
	if (pDesc != null) {
	    throw new IllegalArgumentException("encrypted properties must be java.lang.String");
	}
    }

    /**
     * @param pDesc
     *                the Component Item Descriptor
     */
    public void setComponentItemDescriptor(final RepositoryItemDescriptor pDesc) {
	if (pDesc != null) {
	    throw new IllegalArgumentException("encrypted properties must be scalars");
	}
    }
}
