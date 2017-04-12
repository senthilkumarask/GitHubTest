/*
 * XML Type:  QAConfigType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAConfigType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML QAConfigType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class QAConfigTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAConfigType
{
    
    public QAConfigTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName INIFILE$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "IniFile");
    private static final javax.xml.namespace.QName INISECTION$2 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "IniSection");
    
    
    /**
     * Gets the "IniFile" element
     */
    public java.lang.String getIniFile()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INIFILE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "IniFile" element
     */
    public org.apache.xmlbeans.XmlString xgetIniFile()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INIFILE$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "IniFile" element
     */
    public boolean isSetIniFile()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(INIFILE$0) != 0;
        }
    }
    
    /**
     * Sets the "IniFile" element
     */
    public void setIniFile(java.lang.String iniFile)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INIFILE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INIFILE$0);
            }
            target.setStringValue(iniFile);
        }
    }
    
    /**
     * Sets (as xml) the "IniFile" element
     */
    public void xsetIniFile(org.apache.xmlbeans.XmlString iniFile)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INIFILE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INIFILE$0);
            }
            target.set(iniFile);
        }
    }
    
    /**
     * Unsets the "IniFile" element
     */
    public void unsetIniFile()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(INIFILE$0, 0);
        }
    }
    
    /**
     * Gets the "IniSection" element
     */
    public java.lang.String getIniSection()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INISECTION$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "IniSection" element
     */
    public org.apache.xmlbeans.XmlString xgetIniSection()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INISECTION$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "IniSection" element
     */
    public boolean isSetIniSection()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(INISECTION$2) != 0;
        }
    }
    
    /**
     * Sets the "IniSection" element
     */
    public void setIniSection(java.lang.String iniSection)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(INISECTION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(INISECTION$2);
            }
            target.setStringValue(iniSection);
        }
    }
    
    /**
     * Sets (as xml) the "IniSection" element
     */
    public void xsetIniSection(org.apache.xmlbeans.XmlString iniSection)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(INISECTION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(INISECTION$2);
            }
            target.set(iniSection);
        }
    }
    
    /**
     * Unsets the "IniSection" element
     */
    public void unsetIniSection()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(INISECTION$2, 0);
        }
    }
}
