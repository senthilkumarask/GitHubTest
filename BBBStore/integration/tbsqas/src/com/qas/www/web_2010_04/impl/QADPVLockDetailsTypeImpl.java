/*
 * XML Type:  QADPVLockDetailsType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QADPVLockDetailsType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML QADPVLockDetailsType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class QADPVLockDetailsTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QADPVLockDetailsType
{
    
    public QADPVLockDetailsTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DPVLOCKDATE$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DPVLockDate");
    private static final javax.xml.namespace.QName DPVLOCKTIME$2 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DPVLockTime");
    private static final javax.xml.namespace.QName DPVSEEDADDRESS$4 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DPVSeedAddress");
    private static final javax.xml.namespace.QName DPVLOCKCODE$6 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DPVLockCode");
    
    
    /**
     * Gets the "DPVLockDate" element
     */
    public java.lang.String getDPVLockDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DPVLOCKDATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DPVLockDate" element
     */
    public org.apache.xmlbeans.XmlString xgetDPVLockDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DPVLOCKDATE$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "DPVLockDate" element
     */
    public boolean isSetDPVLockDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DPVLOCKDATE$0) != 0;
        }
    }
    
    /**
     * Sets the "DPVLockDate" element
     */
    public void setDPVLockDate(java.lang.String dpvLockDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DPVLOCKDATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DPVLOCKDATE$0);
            }
            target.setStringValue(dpvLockDate);
        }
    }
    
    /**
     * Sets (as xml) the "DPVLockDate" element
     */
    public void xsetDPVLockDate(org.apache.xmlbeans.XmlString dpvLockDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DPVLOCKDATE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DPVLOCKDATE$0);
            }
            target.set(dpvLockDate);
        }
    }
    
    /**
     * Unsets the "DPVLockDate" element
     */
    public void unsetDPVLockDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DPVLOCKDATE$0, 0);
        }
    }
    
    /**
     * Gets the "DPVLockTime" element
     */
    public java.lang.String getDPVLockTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DPVLOCKTIME$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DPVLockTime" element
     */
    public org.apache.xmlbeans.XmlString xgetDPVLockTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DPVLOCKTIME$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "DPVLockTime" element
     */
    public boolean isSetDPVLockTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DPVLOCKTIME$2) != 0;
        }
    }
    
    /**
     * Sets the "DPVLockTime" element
     */
    public void setDPVLockTime(java.lang.String dpvLockTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DPVLOCKTIME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DPVLOCKTIME$2);
            }
            target.setStringValue(dpvLockTime);
        }
    }
    
    /**
     * Sets (as xml) the "DPVLockTime" element
     */
    public void xsetDPVLockTime(org.apache.xmlbeans.XmlString dpvLockTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DPVLOCKTIME$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DPVLOCKTIME$2);
            }
            target.set(dpvLockTime);
        }
    }
    
    /**
     * Unsets the "DPVLockTime" element
     */
    public void unsetDPVLockTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DPVLOCKTIME$2, 0);
        }
    }
    
    /**
     * Gets the "DPVSeedAddress" element
     */
    public java.lang.String getDPVSeedAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DPVSEEDADDRESS$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DPVSeedAddress" element
     */
    public org.apache.xmlbeans.XmlString xgetDPVSeedAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DPVSEEDADDRESS$4, 0);
            return target;
        }
    }
    
    /**
     * True if has "DPVSeedAddress" element
     */
    public boolean isSetDPVSeedAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DPVSEEDADDRESS$4) != 0;
        }
    }
    
    /**
     * Sets the "DPVSeedAddress" element
     */
    public void setDPVSeedAddress(java.lang.String dpvSeedAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DPVSEEDADDRESS$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DPVSEEDADDRESS$4);
            }
            target.setStringValue(dpvSeedAddress);
        }
    }
    
    /**
     * Sets (as xml) the "DPVSeedAddress" element
     */
    public void xsetDPVSeedAddress(org.apache.xmlbeans.XmlString dpvSeedAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DPVSEEDADDRESS$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DPVSEEDADDRESS$4);
            }
            target.set(dpvSeedAddress);
        }
    }
    
    /**
     * Unsets the "DPVSeedAddress" element
     */
    public void unsetDPVSeedAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DPVSEEDADDRESS$4, 0);
        }
    }
    
    /**
     * Gets the "DPVLockCode" element
     */
    public java.lang.String getDPVLockCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DPVLOCKCODE$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "DPVLockCode" element
     */
    public org.apache.xmlbeans.XmlString xgetDPVLockCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DPVLOCKCODE$6, 0);
            return target;
        }
    }
    
    /**
     * True if has "DPVLockCode" element
     */
    public boolean isSetDPVLockCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DPVLOCKCODE$6) != 0;
        }
    }
    
    /**
     * Sets the "DPVLockCode" element
     */
    public void setDPVLockCode(java.lang.String dpvLockCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DPVLOCKCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DPVLOCKCODE$6);
            }
            target.setStringValue(dpvLockCode);
        }
    }
    
    /**
     * Sets (as xml) the "DPVLockCode" element
     */
    public void xsetDPVLockCode(org.apache.xmlbeans.XmlString dpvLockCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DPVLOCKCODE$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DPVLOCKCODE$6);
            }
            target.set(dpvLockCode);
        }
    }
    
    /**
     * Unsets the "DPVLockCode" element
     */
    public void unsetDPVLockCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DPVLOCKCODE$6, 0);
        }
    }
}
