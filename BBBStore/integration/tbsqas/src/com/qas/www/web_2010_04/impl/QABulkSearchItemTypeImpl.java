/*
 * XML Type:  QABulkSearchItemType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QABulkSearchItemType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML QABulkSearchItemType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class QABulkSearchItemTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QABulkSearchItemType
{
    
    public QABulkSearchItemTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAADDRESS$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAAddress");
    private static final javax.xml.namespace.QName INPUTADDRESS$2 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "InputAddress");
    private static final javax.xml.namespace.QName VERIFICATIONFLAGS$4 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "VerificationFlags");
    private static final javax.xml.namespace.QName VERIFYLEVEL$6 = 
        new javax.xml.namespace.QName("", "VerifyLevel");
    
    
    /**
     * Gets the "QAAddress" element
     */
    public com.qas.www.web_2010_04.QAAddressType getQAAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAAddressType target = null;
            target = (com.qas.www.web_2010_04.QAAddressType)get_store().find_element_user(QAADDRESS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAAddress" element
     */
    public void setQAAddress(com.qas.www.web_2010_04.QAAddressType qaAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAAddressType target = null;
            target = (com.qas.www.web_2010_04.QAAddressType)get_store().find_element_user(QAADDRESS$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAAddressType)get_store().add_element_user(QAADDRESS$0);
            }
            target.set(qaAddress);
        }
    }
    
    /**
     * Appends and returns a new empty "QAAddress" element
     */
    public com.qas.www.web_2010_04.QAAddressType addNewQAAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAAddressType target = null;
            target = (com.qas.www.web_2010_04.QAAddressType)get_store().add_element_user(QAADDRESS$0);
            return target;
        }
    }
    
    /**
     * Gets the "InputAddress" element
     */
    public java.lang.String getInputAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INPUTADDRESS$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "InputAddress" element
     */
    public org.apache.xmlbeans.XmlString xgetInputAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INPUTADDRESS$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "InputAddress" element
     */
    public void setInputAddress(java.lang.String inputAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INPUTADDRESS$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INPUTADDRESS$2);
            }
            target.setStringValue(inputAddress);
        }
    }
    
    /**
     * Sets (as xml) the "InputAddress" element
     */
    public void xsetInputAddress(org.apache.xmlbeans.XmlString inputAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INPUTADDRESS$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INPUTADDRESS$2);
            }
            target.set(inputAddress);
        }
    }
    
    /**
     * Gets the "VerificationFlags" element
     */
    public com.qas.www.web_2010_04.VerificationFlagsType getVerificationFlags()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.VerificationFlagsType target = null;
            target = (com.qas.www.web_2010_04.VerificationFlagsType)get_store().find_element_user(VERIFICATIONFLAGS$4, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "VerificationFlags" element
     */
    public boolean isSetVerificationFlags()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(VERIFICATIONFLAGS$4) != 0;
        }
    }
    
    /**
     * Sets the "VerificationFlags" element
     */
    public void setVerificationFlags(com.qas.www.web_2010_04.VerificationFlagsType verificationFlags)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.VerificationFlagsType target = null;
            target = (com.qas.www.web_2010_04.VerificationFlagsType)get_store().find_element_user(VERIFICATIONFLAGS$4, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.VerificationFlagsType)get_store().add_element_user(VERIFICATIONFLAGS$4);
            }
            target.set(verificationFlags);
        }
    }
    
    /**
     * Appends and returns a new empty "VerificationFlags" element
     */
    public com.qas.www.web_2010_04.VerificationFlagsType addNewVerificationFlags()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.VerificationFlagsType target = null;
            target = (com.qas.www.web_2010_04.VerificationFlagsType)get_store().add_element_user(VERIFICATIONFLAGS$4);
            return target;
        }
    }
    
    /**
     * Unsets the "VerificationFlags" element
     */
    public void unsetVerificationFlags()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(VERIFICATIONFLAGS$4, 0);
        }
    }
    
    /**
     * Gets the "VerifyLevel" attribute
     */
    public com.qas.www.web_2010_04.VerifyLevelType.Enum getVerifyLevel()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(VERIFYLEVEL$6);
            if (target == null)
            {
                return null;
            }
            return (com.qas.www.web_2010_04.VerifyLevelType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "VerifyLevel" attribute
     */
    public com.qas.www.web_2010_04.VerifyLevelType xgetVerifyLevel()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.VerifyLevelType target = null;
            target = (com.qas.www.web_2010_04.VerifyLevelType)get_store().find_attribute_user(VERIFYLEVEL$6);
            return target;
        }
    }
    
    /**
     * True if has "VerifyLevel" attribute
     */
    public boolean isSetVerifyLevel()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(VERIFYLEVEL$6) != null;
        }
    }
    
    /**
     * Sets the "VerifyLevel" attribute
     */
    public void setVerifyLevel(com.qas.www.web_2010_04.VerifyLevelType.Enum verifyLevel)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(VERIFYLEVEL$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(VERIFYLEVEL$6);
            }
            target.setEnumValue(verifyLevel);
        }
    }
    
    /**
     * Sets (as xml) the "VerifyLevel" attribute
     */
    public void xsetVerifyLevel(com.qas.www.web_2010_04.VerifyLevelType verifyLevel)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.VerifyLevelType target = null;
            target = (com.qas.www.web_2010_04.VerifyLevelType)get_store().find_attribute_user(VERIFYLEVEL$6);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.VerifyLevelType)get_store().add_attribute_user(VERIFYLEVEL$6);
            }
            target.set(verifyLevel);
        }
    }
    
    /**
     * Unsets the "VerifyLevel" attribute
     */
    public void unsetVerifyLevel()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(VERIFYLEVEL$6);
        }
    }
}
