/*
 * XML Type:  PicklistEntryType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.PicklistEntryType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML PicklistEntryType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class PicklistEntryTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.PicklistEntryType
{
    
    public PicklistEntryTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MONIKER$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Moniker");
    private static final javax.xml.namespace.QName PARTIALADDRESS$2 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "PartialAddress");
    private static final javax.xml.namespace.QName PICKLIST$4 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Picklist");
    private static final javax.xml.namespace.QName POSTCODE$6 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Postcode");
    private static final javax.xml.namespace.QName SCORE$8 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Score");
    private static final javax.xml.namespace.QName QAADDRESS$10 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAAddress");
    private static final javax.xml.namespace.QName FULLADDRESS$12 = 
        new javax.xml.namespace.QName("", "FullAddress");
    private static final javax.xml.namespace.QName MULTIPLES$14 = 
        new javax.xml.namespace.QName("", "Multiples");
    private static final javax.xml.namespace.QName CANSTEP$16 = 
        new javax.xml.namespace.QName("", "CanStep");
    private static final javax.xml.namespace.QName ALIASMATCH$18 = 
        new javax.xml.namespace.QName("", "AliasMatch");
    private static final javax.xml.namespace.QName POSTCODERECODED$20 = 
        new javax.xml.namespace.QName("", "PostcodeRecoded");
    private static final javax.xml.namespace.QName CROSSBORDERMATCH$22 = 
        new javax.xml.namespace.QName("", "CrossBorderMatch");
    private static final javax.xml.namespace.QName DUMMYPOBOX$24 = 
        new javax.xml.namespace.QName("", "DummyPOBox");
    private static final javax.xml.namespace.QName NAME$26 = 
        new javax.xml.namespace.QName("", "Name");
    private static final javax.xml.namespace.QName INFORMATION$28 = 
        new javax.xml.namespace.QName("", "Information");
    private static final javax.xml.namespace.QName WARNINFORMATION$30 = 
        new javax.xml.namespace.QName("", "WarnInformation");
    private static final javax.xml.namespace.QName INCOMPLETEADDR$32 = 
        new javax.xml.namespace.QName("", "IncompleteAddr");
    private static final javax.xml.namespace.QName UNRESOLVABLERANGE$34 = 
        new javax.xml.namespace.QName("", "UnresolvableRange");
    private static final javax.xml.namespace.QName PHANTOMPRIMARYPOINT$36 = 
        new javax.xml.namespace.QName("", "PhantomPrimaryPoint");
    private static final javax.xml.namespace.QName SUBSIDIARYDATA$38 = 
        new javax.xml.namespace.QName("", "SubsidiaryData");
    private static final javax.xml.namespace.QName EXTENDEDDATA$40 = 
        new javax.xml.namespace.QName("", "ExtendedData");
    private static final javax.xml.namespace.QName ENHANCEDDATA$42 = 
        new javax.xml.namespace.QName("", "EnhancedData");
    
    
    /**
     * Gets the "Moniker" element
     */
    public java.lang.String getMoniker()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MONIKER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Moniker" element
     */
    public org.apache.xmlbeans.XmlString xgetMoniker()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MONIKER$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Moniker" element
     */
    public void setMoniker(java.lang.String moniker)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MONIKER$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MONIKER$0);
            }
            target.setStringValue(moniker);
        }
    }
    
    /**
     * Sets (as xml) the "Moniker" element
     */
    public void xsetMoniker(org.apache.xmlbeans.XmlString moniker)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MONIKER$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MONIKER$0);
            }
            target.set(moniker);
        }
    }
    
    /**
     * Gets the "PartialAddress" element
     */
    public java.lang.String getPartialAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARTIALADDRESS$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "PartialAddress" element
     */
    public org.apache.xmlbeans.XmlString xgetPartialAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARTIALADDRESS$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "PartialAddress" element
     */
    public void setPartialAddress(java.lang.String partialAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PARTIALADDRESS$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PARTIALADDRESS$2);
            }
            target.setStringValue(partialAddress);
        }
    }
    
    /**
     * Sets (as xml) the "PartialAddress" element
     */
    public void xsetPartialAddress(org.apache.xmlbeans.XmlString partialAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PARTIALADDRESS$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PARTIALADDRESS$2);
            }
            target.set(partialAddress);
        }
    }
    
    /**
     * Gets the "Picklist" element
     */
    public java.lang.String getPicklist()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PICKLIST$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Picklist" element
     */
    public org.apache.xmlbeans.XmlString xgetPicklist()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PICKLIST$4, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Picklist" element
     */
    public void setPicklist(java.lang.String picklist)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PICKLIST$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PICKLIST$4);
            }
            target.setStringValue(picklist);
        }
    }
    
    /**
     * Sets (as xml) the "Picklist" element
     */
    public void xsetPicklist(org.apache.xmlbeans.XmlString picklist)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PICKLIST$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PICKLIST$4);
            }
            target.set(picklist);
        }
    }
    
    /**
     * Gets the "Postcode" element
     */
    public java.lang.String getPostcode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POSTCODE$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Postcode" element
     */
    public org.apache.xmlbeans.XmlString xgetPostcode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(POSTCODE$6, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Postcode" element
     */
    public void setPostcode(java.lang.String postcode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POSTCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(POSTCODE$6);
            }
            target.setStringValue(postcode);
        }
    }
    
    /**
     * Sets (as xml) the "Postcode" element
     */
    public void xsetPostcode(org.apache.xmlbeans.XmlString postcode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(POSTCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(POSTCODE$6);
            }
            target.set(postcode);
        }
    }
    
    /**
     * Gets the "Score" element
     */
    public java.math.BigInteger getScore()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SCORE$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getBigIntegerValue();
        }
    }
    
    /**
     * Gets (as xml) the "Score" element
     */
    public org.apache.xmlbeans.XmlNonNegativeInteger xgetScore()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(SCORE$8, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Score" element
     */
    public void setScore(java.math.BigInteger score)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SCORE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SCORE$8);
            }
            target.setBigIntegerValue(score);
        }
    }
    
    /**
     * Sets (as xml) the "Score" element
     */
    public void xsetScore(org.apache.xmlbeans.XmlNonNegativeInteger score)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(SCORE$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().add_element_user(SCORE$8);
            }
            target.set(score);
        }
    }
    
    /**
     * Gets the "QAAddress" element
     */
    public com.qas.www.web_2010_04.QAAddressType getQAAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAAddressType target = null;
            target = (com.qas.www.web_2010_04.QAAddressType)get_store().find_element_user(QAADDRESS$10, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * True if has "QAAddress" element
     */
    public boolean isSetQAAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(QAADDRESS$10) != 0;
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
            target = (com.qas.www.web_2010_04.QAAddressType)get_store().find_element_user(QAADDRESS$10, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAAddressType)get_store().add_element_user(QAADDRESS$10);
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
            target = (com.qas.www.web_2010_04.QAAddressType)get_store().add_element_user(QAADDRESS$10);
            return target;
        }
    }
    
    /**
     * Unsets the "QAAddress" element
     */
    public void unsetQAAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(QAADDRESS$10, 0);
        }
    }
    
    /**
     * Gets the "FullAddress" attribute
     */
    public boolean getFullAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(FULLADDRESS$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(FULLADDRESS$12);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "FullAddress" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetFullAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(FULLADDRESS$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(FULLADDRESS$12);
            }
            return target;
        }
    }
    
    /**
     * True if has "FullAddress" attribute
     */
    public boolean isSetFullAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(FULLADDRESS$12) != null;
        }
    }
    
    /**
     * Sets the "FullAddress" attribute
     */
    public void setFullAddress(boolean fullAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(FULLADDRESS$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(FULLADDRESS$12);
            }
            target.setBooleanValue(fullAddress);
        }
    }
    
    /**
     * Sets (as xml) the "FullAddress" attribute
     */
    public void xsetFullAddress(org.apache.xmlbeans.XmlBoolean fullAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(FULLADDRESS$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(FULLADDRESS$12);
            }
            target.set(fullAddress);
        }
    }
    
    /**
     * Unsets the "FullAddress" attribute
     */
    public void unsetFullAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(FULLADDRESS$12);
        }
    }
    
    /**
     * Gets the "Multiples" attribute
     */
    public boolean getMultiples()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MULTIPLES$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(MULTIPLES$14);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "Multiples" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetMultiples()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(MULTIPLES$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(MULTIPLES$14);
            }
            return target;
        }
    }
    
    /**
     * True if has "Multiples" attribute
     */
    public boolean isSetMultiples()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(MULTIPLES$14) != null;
        }
    }
    
    /**
     * Sets the "Multiples" attribute
     */
    public void setMultiples(boolean multiples)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MULTIPLES$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(MULTIPLES$14);
            }
            target.setBooleanValue(multiples);
        }
    }
    
    /**
     * Sets (as xml) the "Multiples" attribute
     */
    public void xsetMultiples(org.apache.xmlbeans.XmlBoolean multiples)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(MULTIPLES$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(MULTIPLES$14);
            }
            target.set(multiples);
        }
    }
    
    /**
     * Unsets the "Multiples" attribute
     */
    public void unsetMultiples()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(MULTIPLES$14);
        }
    }
    
    /**
     * Gets the "CanStep" attribute
     */
    public boolean getCanStep()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(CANSTEP$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(CANSTEP$16);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "CanStep" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetCanStep()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(CANSTEP$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(CANSTEP$16);
            }
            return target;
        }
    }
    
    /**
     * True if has "CanStep" attribute
     */
    public boolean isSetCanStep()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(CANSTEP$16) != null;
        }
    }
    
    /**
     * Sets the "CanStep" attribute
     */
    public void setCanStep(boolean canStep)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(CANSTEP$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(CANSTEP$16);
            }
            target.setBooleanValue(canStep);
        }
    }
    
    /**
     * Sets (as xml) the "CanStep" attribute
     */
    public void xsetCanStep(org.apache.xmlbeans.XmlBoolean canStep)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(CANSTEP$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(CANSTEP$16);
            }
            target.set(canStep);
        }
    }
    
    /**
     * Unsets the "CanStep" attribute
     */
    public void unsetCanStep()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(CANSTEP$16);
        }
    }
    
    /**
     * Gets the "AliasMatch" attribute
     */
    public boolean getAliasMatch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ALIASMATCH$18);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(ALIASMATCH$18);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "AliasMatch" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetAliasMatch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(ALIASMATCH$18);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(ALIASMATCH$18);
            }
            return target;
        }
    }
    
    /**
     * True if has "AliasMatch" attribute
     */
    public boolean isSetAliasMatch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(ALIASMATCH$18) != null;
        }
    }
    
    /**
     * Sets the "AliasMatch" attribute
     */
    public void setAliasMatch(boolean aliasMatch)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ALIASMATCH$18);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ALIASMATCH$18);
            }
            target.setBooleanValue(aliasMatch);
        }
    }
    
    /**
     * Sets (as xml) the "AliasMatch" attribute
     */
    public void xsetAliasMatch(org.apache.xmlbeans.XmlBoolean aliasMatch)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(ALIASMATCH$18);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(ALIASMATCH$18);
            }
            target.set(aliasMatch);
        }
    }
    
    /**
     * Unsets the "AliasMatch" attribute
     */
    public void unsetAliasMatch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(ALIASMATCH$18);
        }
    }
    
    /**
     * Gets the "PostcodeRecoded" attribute
     */
    public boolean getPostcodeRecoded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(POSTCODERECODED$20);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(POSTCODERECODED$20);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "PostcodeRecoded" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetPostcodeRecoded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(POSTCODERECODED$20);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(POSTCODERECODED$20);
            }
            return target;
        }
    }
    
    /**
     * True if has "PostcodeRecoded" attribute
     */
    public boolean isSetPostcodeRecoded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(POSTCODERECODED$20) != null;
        }
    }
    
    /**
     * Sets the "PostcodeRecoded" attribute
     */
    public void setPostcodeRecoded(boolean postcodeRecoded)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(POSTCODERECODED$20);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(POSTCODERECODED$20);
            }
            target.setBooleanValue(postcodeRecoded);
        }
    }
    
    /**
     * Sets (as xml) the "PostcodeRecoded" attribute
     */
    public void xsetPostcodeRecoded(org.apache.xmlbeans.XmlBoolean postcodeRecoded)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(POSTCODERECODED$20);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(POSTCODERECODED$20);
            }
            target.set(postcodeRecoded);
        }
    }
    
    /**
     * Unsets the "PostcodeRecoded" attribute
     */
    public void unsetPostcodeRecoded()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(POSTCODERECODED$20);
        }
    }
    
    /**
     * Gets the "CrossBorderMatch" attribute
     */
    public boolean getCrossBorderMatch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(CROSSBORDERMATCH$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(CROSSBORDERMATCH$22);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "CrossBorderMatch" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetCrossBorderMatch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(CROSSBORDERMATCH$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(CROSSBORDERMATCH$22);
            }
            return target;
        }
    }
    
    /**
     * True if has "CrossBorderMatch" attribute
     */
    public boolean isSetCrossBorderMatch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(CROSSBORDERMATCH$22) != null;
        }
    }
    
    /**
     * Sets the "CrossBorderMatch" attribute
     */
    public void setCrossBorderMatch(boolean crossBorderMatch)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(CROSSBORDERMATCH$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(CROSSBORDERMATCH$22);
            }
            target.setBooleanValue(crossBorderMatch);
        }
    }
    
    /**
     * Sets (as xml) the "CrossBorderMatch" attribute
     */
    public void xsetCrossBorderMatch(org.apache.xmlbeans.XmlBoolean crossBorderMatch)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(CROSSBORDERMATCH$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(CROSSBORDERMATCH$22);
            }
            target.set(crossBorderMatch);
        }
    }
    
    /**
     * Unsets the "CrossBorderMatch" attribute
     */
    public void unsetCrossBorderMatch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(CROSSBORDERMATCH$22);
        }
    }
    
    /**
     * Gets the "DummyPOBox" attribute
     */
    public boolean getDummyPOBox()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DUMMYPOBOX$24);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(DUMMYPOBOX$24);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "DummyPOBox" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetDummyPOBox()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(DUMMYPOBOX$24);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(DUMMYPOBOX$24);
            }
            return target;
        }
    }
    
    /**
     * True if has "DummyPOBox" attribute
     */
    public boolean isSetDummyPOBox()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(DUMMYPOBOX$24) != null;
        }
    }
    
    /**
     * Sets the "DummyPOBox" attribute
     */
    public void setDummyPOBox(boolean dummyPOBox)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DUMMYPOBOX$24);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(DUMMYPOBOX$24);
            }
            target.setBooleanValue(dummyPOBox);
        }
    }
    
    /**
     * Sets (as xml) the "DummyPOBox" attribute
     */
    public void xsetDummyPOBox(org.apache.xmlbeans.XmlBoolean dummyPOBox)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(DUMMYPOBOX$24);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(DUMMYPOBOX$24);
            }
            target.set(dummyPOBox);
        }
    }
    
    /**
     * Unsets the "DummyPOBox" attribute
     */
    public void unsetDummyPOBox()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(DUMMYPOBOX$24);
        }
    }
    
    /**
     * Gets the "Name" attribute
     */
    public boolean getName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NAME$26);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(NAME$26);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "Name" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(NAME$26);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(NAME$26);
            }
            return target;
        }
    }
    
    /**
     * True if has "Name" attribute
     */
    public boolean isSetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(NAME$26) != null;
        }
    }
    
    /**
     * Sets the "Name" attribute
     */
    public void setName(boolean name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NAME$26);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(NAME$26);
            }
            target.setBooleanValue(name);
        }
    }
    
    /**
     * Sets (as xml) the "Name" attribute
     */
    public void xsetName(org.apache.xmlbeans.XmlBoolean name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(NAME$26);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(NAME$26);
            }
            target.set(name);
        }
    }
    
    /**
     * Unsets the "Name" attribute
     */
    public void unsetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(NAME$26);
        }
    }
    
    /**
     * Gets the "Information" attribute
     */
    public boolean getInformation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INFORMATION$28);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(INFORMATION$28);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "Information" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetInformation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(INFORMATION$28);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(INFORMATION$28);
            }
            return target;
        }
    }
    
    /**
     * True if has "Information" attribute
     */
    public boolean isSetInformation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(INFORMATION$28) != null;
        }
    }
    
    /**
     * Sets the "Information" attribute
     */
    public void setInformation(boolean information)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INFORMATION$28);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(INFORMATION$28);
            }
            target.setBooleanValue(information);
        }
    }
    
    /**
     * Sets (as xml) the "Information" attribute
     */
    public void xsetInformation(org.apache.xmlbeans.XmlBoolean information)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(INFORMATION$28);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(INFORMATION$28);
            }
            target.set(information);
        }
    }
    
    /**
     * Unsets the "Information" attribute
     */
    public void unsetInformation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(INFORMATION$28);
        }
    }
    
    /**
     * Gets the "WarnInformation" attribute
     */
    public boolean getWarnInformation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(WARNINFORMATION$30);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(WARNINFORMATION$30);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "WarnInformation" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetWarnInformation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(WARNINFORMATION$30);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(WARNINFORMATION$30);
            }
            return target;
        }
    }
    
    /**
     * True if has "WarnInformation" attribute
     */
    public boolean isSetWarnInformation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(WARNINFORMATION$30) != null;
        }
    }
    
    /**
     * Sets the "WarnInformation" attribute
     */
    public void setWarnInformation(boolean warnInformation)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(WARNINFORMATION$30);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(WARNINFORMATION$30);
            }
            target.setBooleanValue(warnInformation);
        }
    }
    
    /**
     * Sets (as xml) the "WarnInformation" attribute
     */
    public void xsetWarnInformation(org.apache.xmlbeans.XmlBoolean warnInformation)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(WARNINFORMATION$30);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(WARNINFORMATION$30);
            }
            target.set(warnInformation);
        }
    }
    
    /**
     * Unsets the "WarnInformation" attribute
     */
    public void unsetWarnInformation()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(WARNINFORMATION$30);
        }
    }
    
    /**
     * Gets the "IncompleteAddr" attribute
     */
    public boolean getIncompleteAddr()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INCOMPLETEADDR$32);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(INCOMPLETEADDR$32);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IncompleteAddr" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetIncompleteAddr()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(INCOMPLETEADDR$32);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(INCOMPLETEADDR$32);
            }
            return target;
        }
    }
    
    /**
     * True if has "IncompleteAddr" attribute
     */
    public boolean isSetIncompleteAddr()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(INCOMPLETEADDR$32) != null;
        }
    }
    
    /**
     * Sets the "IncompleteAddr" attribute
     */
    public void setIncompleteAddr(boolean incompleteAddr)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INCOMPLETEADDR$32);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(INCOMPLETEADDR$32);
            }
            target.setBooleanValue(incompleteAddr);
        }
    }
    
    /**
     * Sets (as xml) the "IncompleteAddr" attribute
     */
    public void xsetIncompleteAddr(org.apache.xmlbeans.XmlBoolean incompleteAddr)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(INCOMPLETEADDR$32);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(INCOMPLETEADDR$32);
            }
            target.set(incompleteAddr);
        }
    }
    
    /**
     * Unsets the "IncompleteAddr" attribute
     */
    public void unsetIncompleteAddr()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(INCOMPLETEADDR$32);
        }
    }
    
    /**
     * Gets the "UnresolvableRange" attribute
     */
    public boolean getUnresolvableRange()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(UNRESOLVABLERANGE$34);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(UNRESOLVABLERANGE$34);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "UnresolvableRange" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetUnresolvableRange()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(UNRESOLVABLERANGE$34);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(UNRESOLVABLERANGE$34);
            }
            return target;
        }
    }
    
    /**
     * True if has "UnresolvableRange" attribute
     */
    public boolean isSetUnresolvableRange()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(UNRESOLVABLERANGE$34) != null;
        }
    }
    
    /**
     * Sets the "UnresolvableRange" attribute
     */
    public void setUnresolvableRange(boolean unresolvableRange)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(UNRESOLVABLERANGE$34);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(UNRESOLVABLERANGE$34);
            }
            target.setBooleanValue(unresolvableRange);
        }
    }
    
    /**
     * Sets (as xml) the "UnresolvableRange" attribute
     */
    public void xsetUnresolvableRange(org.apache.xmlbeans.XmlBoolean unresolvableRange)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(UNRESOLVABLERANGE$34);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(UNRESOLVABLERANGE$34);
            }
            target.set(unresolvableRange);
        }
    }
    
    /**
     * Unsets the "UnresolvableRange" attribute
     */
    public void unsetUnresolvableRange()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(UNRESOLVABLERANGE$34);
        }
    }
    
    /**
     * Gets the "PhantomPrimaryPoint" attribute
     */
    public boolean getPhantomPrimaryPoint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PHANTOMPRIMARYPOINT$36);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PHANTOMPRIMARYPOINT$36);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "PhantomPrimaryPoint" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetPhantomPrimaryPoint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(PHANTOMPRIMARYPOINT$36);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(PHANTOMPRIMARYPOINT$36);
            }
            return target;
        }
    }
    
    /**
     * True if has "PhantomPrimaryPoint" attribute
     */
    public boolean isSetPhantomPrimaryPoint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(PHANTOMPRIMARYPOINT$36) != null;
        }
    }
    
    /**
     * Sets the "PhantomPrimaryPoint" attribute
     */
    public void setPhantomPrimaryPoint(boolean phantomPrimaryPoint)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PHANTOMPRIMARYPOINT$36);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PHANTOMPRIMARYPOINT$36);
            }
            target.setBooleanValue(phantomPrimaryPoint);
        }
    }
    
    /**
     * Sets (as xml) the "PhantomPrimaryPoint" attribute
     */
    public void xsetPhantomPrimaryPoint(org.apache.xmlbeans.XmlBoolean phantomPrimaryPoint)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(PHANTOMPRIMARYPOINT$36);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(PHANTOMPRIMARYPOINT$36);
            }
            target.set(phantomPrimaryPoint);
        }
    }
    
    /**
     * Unsets the "PhantomPrimaryPoint" attribute
     */
    public void unsetPhantomPrimaryPoint()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(PHANTOMPRIMARYPOINT$36);
        }
    }
    
    /**
     * Gets the "SubsidiaryData" attribute
     */
    public boolean getSubsidiaryData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(SUBSIDIARYDATA$38);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(SUBSIDIARYDATA$38);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "SubsidiaryData" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetSubsidiaryData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(SUBSIDIARYDATA$38);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(SUBSIDIARYDATA$38);
            }
            return target;
        }
    }
    
    /**
     * True if has "SubsidiaryData" attribute
     */
    public boolean isSetSubsidiaryData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(SUBSIDIARYDATA$38) != null;
        }
    }
    
    /**
     * Sets the "SubsidiaryData" attribute
     */
    public void setSubsidiaryData(boolean subsidiaryData)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(SUBSIDIARYDATA$38);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(SUBSIDIARYDATA$38);
            }
            target.setBooleanValue(subsidiaryData);
        }
    }
    
    /**
     * Sets (as xml) the "SubsidiaryData" attribute
     */
    public void xsetSubsidiaryData(org.apache.xmlbeans.XmlBoolean subsidiaryData)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(SUBSIDIARYDATA$38);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(SUBSIDIARYDATA$38);
            }
            target.set(subsidiaryData);
        }
    }
    
    /**
     * Unsets the "SubsidiaryData" attribute
     */
    public void unsetSubsidiaryData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(SUBSIDIARYDATA$38);
        }
    }
    
    /**
     * Gets the "ExtendedData" attribute
     */
    public boolean getExtendedData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(EXTENDEDDATA$40);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(EXTENDEDDATA$40);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "ExtendedData" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetExtendedData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(EXTENDEDDATA$40);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(EXTENDEDDATA$40);
            }
            return target;
        }
    }
    
    /**
     * True if has "ExtendedData" attribute
     */
    public boolean isSetExtendedData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(EXTENDEDDATA$40) != null;
        }
    }
    
    /**
     * Sets the "ExtendedData" attribute
     */
    public void setExtendedData(boolean extendedData)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(EXTENDEDDATA$40);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(EXTENDEDDATA$40);
            }
            target.setBooleanValue(extendedData);
        }
    }
    
    /**
     * Sets (as xml) the "ExtendedData" attribute
     */
    public void xsetExtendedData(org.apache.xmlbeans.XmlBoolean extendedData)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(EXTENDEDDATA$40);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(EXTENDEDDATA$40);
            }
            target.set(extendedData);
        }
    }
    
    /**
     * Unsets the "ExtendedData" attribute
     */
    public void unsetExtendedData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(EXTENDEDDATA$40);
        }
    }
    
    /**
     * Gets the "EnhancedData" attribute
     */
    public boolean getEnhancedData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ENHANCEDDATA$42);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(ENHANCEDDATA$42);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "EnhancedData" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetEnhancedData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(ENHANCEDDATA$42);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(ENHANCEDDATA$42);
            }
            return target;
        }
    }
    
    /**
     * True if has "EnhancedData" attribute
     */
    public boolean isSetEnhancedData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(ENHANCEDDATA$42) != null;
        }
    }
    
    /**
     * Sets the "EnhancedData" attribute
     */
    public void setEnhancedData(boolean enhancedData)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ENHANCEDDATA$42);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ENHANCEDDATA$42);
            }
            target.setBooleanValue(enhancedData);
        }
    }
    
    /**
     * Sets (as xml) the "EnhancedData" attribute
     */
    public void xsetEnhancedData(org.apache.xmlbeans.XmlBoolean enhancedData)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(ENHANCEDDATA$42);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(ENHANCEDDATA$42);
            }
            target.set(enhancedData);
        }
    }
    
    /**
     * Unsets the "EnhancedData" attribute
     */
    public void unsetEnhancedData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(ENHANCEDDATA$42);
        }
    }
}
