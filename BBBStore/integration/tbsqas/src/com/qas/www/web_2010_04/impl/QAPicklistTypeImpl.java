/*
 * XML Type:  QAPicklistType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAPicklistType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML QAPicklistType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class QAPicklistTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAPicklistType
{
    
    public QAPicklistTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FULLPICKLISTMONIKER$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "FullPicklistMoniker");
    private static final javax.xml.namespace.QName PICKLISTENTRY$2 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "PicklistEntry");
    private static final javax.xml.namespace.QName PROMPT$4 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Prompt");
    private static final javax.xml.namespace.QName TOTAL$6 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Total");
    private static final javax.xml.namespace.QName AUTOFORMATSAFE$8 = 
        new javax.xml.namespace.QName("", "AutoFormatSafe");
    private static final javax.xml.namespace.QName AUTOFORMATPASTCLOSE$10 = 
        new javax.xml.namespace.QName("", "AutoFormatPastClose");
    private static final javax.xml.namespace.QName AUTOSTEPINSAFE$12 = 
        new javax.xml.namespace.QName("", "AutoStepinSafe");
    private static final javax.xml.namespace.QName AUTOSTEPINPASTCLOSE$14 = 
        new javax.xml.namespace.QName("", "AutoStepinPastClose");
    private static final javax.xml.namespace.QName LARGEPOTENTIAL$16 = 
        new javax.xml.namespace.QName("", "LargePotential");
    private static final javax.xml.namespace.QName MAXMATCHES$18 = 
        new javax.xml.namespace.QName("", "MaxMatches");
    private static final javax.xml.namespace.QName MOREOTHERMATCHES$20 = 
        new javax.xml.namespace.QName("", "MoreOtherMatches");
    private static final javax.xml.namespace.QName OVERTHRESHOLD$22 = 
        new javax.xml.namespace.QName("", "OverThreshold");
    private static final javax.xml.namespace.QName TIMEOUT$24 = 
        new javax.xml.namespace.QName("", "Timeout");
    
    
    /**
     * Gets the "FullPicklistMoniker" element
     */
    public java.lang.String getFullPicklistMoniker()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FULLPICKLISTMONIKER$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "FullPicklistMoniker" element
     */
    public org.apache.xmlbeans.XmlString xgetFullPicklistMoniker()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FULLPICKLISTMONIKER$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "FullPicklistMoniker" element
     */
    public void setFullPicklistMoniker(java.lang.String fullPicklistMoniker)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FULLPICKLISTMONIKER$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FULLPICKLISTMONIKER$0);
            }
            target.setStringValue(fullPicklistMoniker);
        }
    }
    
    /**
     * Sets (as xml) the "FullPicklistMoniker" element
     */
    public void xsetFullPicklistMoniker(org.apache.xmlbeans.XmlString fullPicklistMoniker)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(FULLPICKLISTMONIKER$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(FULLPICKLISTMONIKER$0);
            }
            target.set(fullPicklistMoniker);
        }
    }
    
    /**
     * Gets array of all "PicklistEntry" elements
     */
    public com.qas.www.web_2010_04.PicklistEntryType[] getPicklistEntryArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(PICKLISTENTRY$2, targetList);
            com.qas.www.web_2010_04.PicklistEntryType[] result = new com.qas.www.web_2010_04.PicklistEntryType[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "PicklistEntry" element
     */
    public com.qas.www.web_2010_04.PicklistEntryType getPicklistEntryArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.PicklistEntryType target = null;
            target = (com.qas.www.web_2010_04.PicklistEntryType)get_store().find_element_user(PICKLISTENTRY$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "PicklistEntry" element
     */
    public int sizeOfPicklistEntryArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PICKLISTENTRY$2);
        }
    }
    
    /**
     * Sets array of all "PicklistEntry" element
     */
    public void setPicklistEntryArray(com.qas.www.web_2010_04.PicklistEntryType[] picklistEntryArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(picklistEntryArray, PICKLISTENTRY$2);
        }
    }
    
    /**
     * Sets ith "PicklistEntry" element
     */
    public void setPicklistEntryArray(int i, com.qas.www.web_2010_04.PicklistEntryType picklistEntry)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.PicklistEntryType target = null;
            target = (com.qas.www.web_2010_04.PicklistEntryType)get_store().find_element_user(PICKLISTENTRY$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(picklistEntry);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "PicklistEntry" element
     */
    public com.qas.www.web_2010_04.PicklistEntryType insertNewPicklistEntry(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.PicklistEntryType target = null;
            target = (com.qas.www.web_2010_04.PicklistEntryType)get_store().insert_element_user(PICKLISTENTRY$2, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "PicklistEntry" element
     */
    public com.qas.www.web_2010_04.PicklistEntryType addNewPicklistEntry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.PicklistEntryType target = null;
            target = (com.qas.www.web_2010_04.PicklistEntryType)get_store().add_element_user(PICKLISTENTRY$2);
            return target;
        }
    }
    
    /**
     * Removes the ith "PicklistEntry" element
     */
    public void removePicklistEntry(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PICKLISTENTRY$2, i);
        }
    }
    
    /**
     * Gets the "Prompt" element
     */
    public java.lang.String getPrompt()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROMPT$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Prompt" element
     */
    public org.apache.xmlbeans.XmlString xgetPrompt()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROMPT$4, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Prompt" element
     */
    public void setPrompt(java.lang.String prompt)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROMPT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROMPT$4);
            }
            target.setStringValue(prompt);
        }
    }
    
    /**
     * Sets (as xml) the "Prompt" element
     */
    public void xsetPrompt(org.apache.xmlbeans.XmlString prompt)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROMPT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROMPT$4);
            }
            target.set(prompt);
        }
    }
    
    /**
     * Gets the "Total" element
     */
    public java.math.BigInteger getTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTAL$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getBigIntegerValue();
        }
    }
    
    /**
     * Gets (as xml) the "Total" element
     */
    public org.apache.xmlbeans.XmlNonNegativeInteger xgetTotal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(TOTAL$6, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Total" element
     */
    public void setTotal(java.math.BigInteger total)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TOTAL$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TOTAL$6);
            }
            target.setBigIntegerValue(total);
        }
    }
    
    /**
     * Sets (as xml) the "Total" element
     */
    public void xsetTotal(org.apache.xmlbeans.XmlNonNegativeInteger total)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(TOTAL$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().add_element_user(TOTAL$6);
            }
            target.set(total);
        }
    }
    
    /**
     * Gets the "AutoFormatSafe" attribute
     */
    public boolean getAutoFormatSafe()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(AUTOFORMATSAFE$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(AUTOFORMATSAFE$8);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "AutoFormatSafe" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetAutoFormatSafe()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(AUTOFORMATSAFE$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(AUTOFORMATSAFE$8);
            }
            return target;
        }
    }
    
    /**
     * True if has "AutoFormatSafe" attribute
     */
    public boolean isSetAutoFormatSafe()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(AUTOFORMATSAFE$8) != null;
        }
    }
    
    /**
     * Sets the "AutoFormatSafe" attribute
     */
    public void setAutoFormatSafe(boolean autoFormatSafe)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(AUTOFORMATSAFE$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(AUTOFORMATSAFE$8);
            }
            target.setBooleanValue(autoFormatSafe);
        }
    }
    
    /**
     * Sets (as xml) the "AutoFormatSafe" attribute
     */
    public void xsetAutoFormatSafe(org.apache.xmlbeans.XmlBoolean autoFormatSafe)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(AUTOFORMATSAFE$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(AUTOFORMATSAFE$8);
            }
            target.set(autoFormatSafe);
        }
    }
    
    /**
     * Unsets the "AutoFormatSafe" attribute
     */
    public void unsetAutoFormatSafe()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(AUTOFORMATSAFE$8);
        }
    }
    
    /**
     * Gets the "AutoFormatPastClose" attribute
     */
    public boolean getAutoFormatPastClose()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(AUTOFORMATPASTCLOSE$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(AUTOFORMATPASTCLOSE$10);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "AutoFormatPastClose" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetAutoFormatPastClose()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(AUTOFORMATPASTCLOSE$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(AUTOFORMATPASTCLOSE$10);
            }
            return target;
        }
    }
    
    /**
     * True if has "AutoFormatPastClose" attribute
     */
    public boolean isSetAutoFormatPastClose()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(AUTOFORMATPASTCLOSE$10) != null;
        }
    }
    
    /**
     * Sets the "AutoFormatPastClose" attribute
     */
    public void setAutoFormatPastClose(boolean autoFormatPastClose)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(AUTOFORMATPASTCLOSE$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(AUTOFORMATPASTCLOSE$10);
            }
            target.setBooleanValue(autoFormatPastClose);
        }
    }
    
    /**
     * Sets (as xml) the "AutoFormatPastClose" attribute
     */
    public void xsetAutoFormatPastClose(org.apache.xmlbeans.XmlBoolean autoFormatPastClose)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(AUTOFORMATPASTCLOSE$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(AUTOFORMATPASTCLOSE$10);
            }
            target.set(autoFormatPastClose);
        }
    }
    
    /**
     * Unsets the "AutoFormatPastClose" attribute
     */
    public void unsetAutoFormatPastClose()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(AUTOFORMATPASTCLOSE$10);
        }
    }
    
    /**
     * Gets the "AutoStepinSafe" attribute
     */
    public boolean getAutoStepinSafe()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(AUTOSTEPINSAFE$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(AUTOSTEPINSAFE$12);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "AutoStepinSafe" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetAutoStepinSafe()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(AUTOSTEPINSAFE$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(AUTOSTEPINSAFE$12);
            }
            return target;
        }
    }
    
    /**
     * True if has "AutoStepinSafe" attribute
     */
    public boolean isSetAutoStepinSafe()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(AUTOSTEPINSAFE$12) != null;
        }
    }
    
    /**
     * Sets the "AutoStepinSafe" attribute
     */
    public void setAutoStepinSafe(boolean autoStepinSafe)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(AUTOSTEPINSAFE$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(AUTOSTEPINSAFE$12);
            }
            target.setBooleanValue(autoStepinSafe);
        }
    }
    
    /**
     * Sets (as xml) the "AutoStepinSafe" attribute
     */
    public void xsetAutoStepinSafe(org.apache.xmlbeans.XmlBoolean autoStepinSafe)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(AUTOSTEPINSAFE$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(AUTOSTEPINSAFE$12);
            }
            target.set(autoStepinSafe);
        }
    }
    
    /**
     * Unsets the "AutoStepinSafe" attribute
     */
    public void unsetAutoStepinSafe()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(AUTOSTEPINSAFE$12);
        }
    }
    
    /**
     * Gets the "AutoStepinPastClose" attribute
     */
    public boolean getAutoStepinPastClose()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(AUTOSTEPINPASTCLOSE$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(AUTOSTEPINPASTCLOSE$14);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "AutoStepinPastClose" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetAutoStepinPastClose()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(AUTOSTEPINPASTCLOSE$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(AUTOSTEPINPASTCLOSE$14);
            }
            return target;
        }
    }
    
    /**
     * True if has "AutoStepinPastClose" attribute
     */
    public boolean isSetAutoStepinPastClose()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(AUTOSTEPINPASTCLOSE$14) != null;
        }
    }
    
    /**
     * Sets the "AutoStepinPastClose" attribute
     */
    public void setAutoStepinPastClose(boolean autoStepinPastClose)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(AUTOSTEPINPASTCLOSE$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(AUTOSTEPINPASTCLOSE$14);
            }
            target.setBooleanValue(autoStepinPastClose);
        }
    }
    
    /**
     * Sets (as xml) the "AutoStepinPastClose" attribute
     */
    public void xsetAutoStepinPastClose(org.apache.xmlbeans.XmlBoolean autoStepinPastClose)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(AUTOSTEPINPASTCLOSE$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(AUTOSTEPINPASTCLOSE$14);
            }
            target.set(autoStepinPastClose);
        }
    }
    
    /**
     * Unsets the "AutoStepinPastClose" attribute
     */
    public void unsetAutoStepinPastClose()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(AUTOSTEPINPASTCLOSE$14);
        }
    }
    
    /**
     * Gets the "LargePotential" attribute
     */
    public boolean getLargePotential()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(LARGEPOTENTIAL$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(LARGEPOTENTIAL$16);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "LargePotential" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetLargePotential()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(LARGEPOTENTIAL$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(LARGEPOTENTIAL$16);
            }
            return target;
        }
    }
    
    /**
     * True if has "LargePotential" attribute
     */
    public boolean isSetLargePotential()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(LARGEPOTENTIAL$16) != null;
        }
    }
    
    /**
     * Sets the "LargePotential" attribute
     */
    public void setLargePotential(boolean largePotential)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(LARGEPOTENTIAL$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(LARGEPOTENTIAL$16);
            }
            target.setBooleanValue(largePotential);
        }
    }
    
    /**
     * Sets (as xml) the "LargePotential" attribute
     */
    public void xsetLargePotential(org.apache.xmlbeans.XmlBoolean largePotential)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(LARGEPOTENTIAL$16);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(LARGEPOTENTIAL$16);
            }
            target.set(largePotential);
        }
    }
    
    /**
     * Unsets the "LargePotential" attribute
     */
    public void unsetLargePotential()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(LARGEPOTENTIAL$16);
        }
    }
    
    /**
     * Gets the "MaxMatches" attribute
     */
    public boolean getMaxMatches()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MAXMATCHES$18);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(MAXMATCHES$18);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "MaxMatches" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetMaxMatches()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(MAXMATCHES$18);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(MAXMATCHES$18);
            }
            return target;
        }
    }
    
    /**
     * True if has "MaxMatches" attribute
     */
    public boolean isSetMaxMatches()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(MAXMATCHES$18) != null;
        }
    }
    
    /**
     * Sets the "MaxMatches" attribute
     */
    public void setMaxMatches(boolean maxMatches)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MAXMATCHES$18);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(MAXMATCHES$18);
            }
            target.setBooleanValue(maxMatches);
        }
    }
    
    /**
     * Sets (as xml) the "MaxMatches" attribute
     */
    public void xsetMaxMatches(org.apache.xmlbeans.XmlBoolean maxMatches)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(MAXMATCHES$18);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(MAXMATCHES$18);
            }
            target.set(maxMatches);
        }
    }
    
    /**
     * Unsets the "MaxMatches" attribute
     */
    public void unsetMaxMatches()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(MAXMATCHES$18);
        }
    }
    
    /**
     * Gets the "MoreOtherMatches" attribute
     */
    public boolean getMoreOtherMatches()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MOREOTHERMATCHES$20);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(MOREOTHERMATCHES$20);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "MoreOtherMatches" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetMoreOtherMatches()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(MOREOTHERMATCHES$20);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(MOREOTHERMATCHES$20);
            }
            return target;
        }
    }
    
    /**
     * True if has "MoreOtherMatches" attribute
     */
    public boolean isSetMoreOtherMatches()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(MOREOTHERMATCHES$20) != null;
        }
    }
    
    /**
     * Sets the "MoreOtherMatches" attribute
     */
    public void setMoreOtherMatches(boolean moreOtherMatches)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MOREOTHERMATCHES$20);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(MOREOTHERMATCHES$20);
            }
            target.setBooleanValue(moreOtherMatches);
        }
    }
    
    /**
     * Sets (as xml) the "MoreOtherMatches" attribute
     */
    public void xsetMoreOtherMatches(org.apache.xmlbeans.XmlBoolean moreOtherMatches)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(MOREOTHERMATCHES$20);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(MOREOTHERMATCHES$20);
            }
            target.set(moreOtherMatches);
        }
    }
    
    /**
     * Unsets the "MoreOtherMatches" attribute
     */
    public void unsetMoreOtherMatches()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(MOREOTHERMATCHES$20);
        }
    }
    
    /**
     * Gets the "OverThreshold" attribute
     */
    public boolean getOverThreshold()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(OVERTHRESHOLD$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(OVERTHRESHOLD$22);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "OverThreshold" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetOverThreshold()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(OVERTHRESHOLD$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(OVERTHRESHOLD$22);
            }
            return target;
        }
    }
    
    /**
     * True if has "OverThreshold" attribute
     */
    public boolean isSetOverThreshold()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(OVERTHRESHOLD$22) != null;
        }
    }
    
    /**
     * Sets the "OverThreshold" attribute
     */
    public void setOverThreshold(boolean overThreshold)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(OVERTHRESHOLD$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(OVERTHRESHOLD$22);
            }
            target.setBooleanValue(overThreshold);
        }
    }
    
    /**
     * Sets (as xml) the "OverThreshold" attribute
     */
    public void xsetOverThreshold(org.apache.xmlbeans.XmlBoolean overThreshold)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(OVERTHRESHOLD$22);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(OVERTHRESHOLD$22);
            }
            target.set(overThreshold);
        }
    }
    
    /**
     * Unsets the "OverThreshold" attribute
     */
    public void unsetOverThreshold()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(OVERTHRESHOLD$22);
        }
    }
    
    /**
     * Gets the "Timeout" attribute
     */
    public boolean getTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$24);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(TIMEOUT$24);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "Timeout" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(TIMEOUT$24);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(TIMEOUT$24);
            }
            return target;
        }
    }
    
    /**
     * True if has "Timeout" attribute
     */
    public boolean isSetTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(TIMEOUT$24) != null;
        }
    }
    
    /**
     * Sets the "Timeout" attribute
     */
    public void setTimeout(boolean timeout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$24);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TIMEOUT$24);
            }
            target.setBooleanValue(timeout);
        }
    }
    
    /**
     * Sets (as xml) the "Timeout" attribute
     */
    public void xsetTimeout(org.apache.xmlbeans.XmlBoolean timeout)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(TIMEOUT$24);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(TIMEOUT$24);
            }
            target.set(timeout);
        }
    }
    
    /**
     * Unsets the "Timeout" attribute
     */
    public void unsetTimeout()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(TIMEOUT$24);
        }
    }
}
