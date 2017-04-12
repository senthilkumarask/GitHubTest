/*
 * XML Type:  QAAddressType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAAddressType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML QAAddressType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class QAAddressTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAAddressType
{
    
    public QAAddressTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ADDRESSLINE$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "AddressLine");
    private static final javax.xml.namespace.QName OVERFLOW$2 = 
        new javax.xml.namespace.QName("", "Overflow");
    private static final javax.xml.namespace.QName TRUNCATED$4 = 
        new javax.xml.namespace.QName("", "Truncated");
    private static final javax.xml.namespace.QName DPVSTATUS$6 = 
        new javax.xml.namespace.QName("", "DPVStatus");
    private static final javax.xml.namespace.QName MISSINGSUBPREMISE$8 = 
        new javax.xml.namespace.QName("", "MissingSubPremise");
    
    
    /**
     * Gets array of all "AddressLine" elements
     */
    public com.qas.www.web_2010_04.AddressLineType[] getAddressLineArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(ADDRESSLINE$0, targetList);
            com.qas.www.web_2010_04.AddressLineType[] result = new com.qas.www.web_2010_04.AddressLineType[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "AddressLine" element
     */
    public com.qas.www.web_2010_04.AddressLineType getAddressLineArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.AddressLineType target = null;
            target = (com.qas.www.web_2010_04.AddressLineType)get_store().find_element_user(ADDRESSLINE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "AddressLine" element
     */
    public int sizeOfAddressLineArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ADDRESSLINE$0);
        }
    }
    
    /**
     * Sets array of all "AddressLine" element
     */
    public void setAddressLineArray(com.qas.www.web_2010_04.AddressLineType[] addressLineArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(addressLineArray, ADDRESSLINE$0);
        }
    }
    
    /**
     * Sets ith "AddressLine" element
     */
    public void setAddressLineArray(int i, com.qas.www.web_2010_04.AddressLineType addressLine)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.AddressLineType target = null;
            target = (com.qas.www.web_2010_04.AddressLineType)get_store().find_element_user(ADDRESSLINE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(addressLine);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "AddressLine" element
     */
    public com.qas.www.web_2010_04.AddressLineType insertNewAddressLine(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.AddressLineType target = null;
            target = (com.qas.www.web_2010_04.AddressLineType)get_store().insert_element_user(ADDRESSLINE$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "AddressLine" element
     */
    public com.qas.www.web_2010_04.AddressLineType addNewAddressLine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.AddressLineType target = null;
            target = (com.qas.www.web_2010_04.AddressLineType)get_store().add_element_user(ADDRESSLINE$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "AddressLine" element
     */
    public void removeAddressLine(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ADDRESSLINE$0, i);
        }
    }
    
    /**
     * Gets the "Overflow" attribute
     */
    public boolean getOverflow()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(OVERFLOW$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(OVERFLOW$2);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "Overflow" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetOverflow()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(OVERFLOW$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(OVERFLOW$2);
            }
            return target;
        }
    }
    
    /**
     * True if has "Overflow" attribute
     */
    public boolean isSetOverflow()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(OVERFLOW$2) != null;
        }
    }
    
    /**
     * Sets the "Overflow" attribute
     */
    public void setOverflow(boolean overflow)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(OVERFLOW$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(OVERFLOW$2);
            }
            target.setBooleanValue(overflow);
        }
    }
    
    /**
     * Sets (as xml) the "Overflow" attribute
     */
    public void xsetOverflow(org.apache.xmlbeans.XmlBoolean overflow)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(OVERFLOW$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(OVERFLOW$2);
            }
            target.set(overflow);
        }
    }
    
    /**
     * Unsets the "Overflow" attribute
     */
    public void unsetOverflow()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(OVERFLOW$2);
        }
    }
    
    /**
     * Gets the "Truncated" attribute
     */
    public boolean getTruncated()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TRUNCATED$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(TRUNCATED$4);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "Truncated" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetTruncated()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(TRUNCATED$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(TRUNCATED$4);
            }
            return target;
        }
    }
    
    /**
     * True if has "Truncated" attribute
     */
    public boolean isSetTruncated()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(TRUNCATED$4) != null;
        }
    }
    
    /**
     * Sets the "Truncated" attribute
     */
    public void setTruncated(boolean truncated)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TRUNCATED$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TRUNCATED$4);
            }
            target.setBooleanValue(truncated);
        }
    }
    
    /**
     * Sets (as xml) the "Truncated" attribute
     */
    public void xsetTruncated(org.apache.xmlbeans.XmlBoolean truncated)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(TRUNCATED$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(TRUNCATED$4);
            }
            target.set(truncated);
        }
    }
    
    /**
     * Unsets the "Truncated" attribute
     */
    public void unsetTruncated()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(TRUNCATED$4);
        }
    }
    
    /**
     * Gets the "DPVStatus" attribute
     */
    public com.qas.www.web_2010_04.DPVStatusType.Enum getDPVStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DPVSTATUS$6);
            if (target == null)
            {
                return null;
            }
            return (com.qas.www.web_2010_04.DPVStatusType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "DPVStatus" attribute
     */
    public com.qas.www.web_2010_04.DPVStatusType xgetDPVStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.DPVStatusType target = null;
            target = (com.qas.www.web_2010_04.DPVStatusType)get_store().find_attribute_user(DPVSTATUS$6);
            return target;
        }
    }
    
    /**
     * True if has "DPVStatus" attribute
     */
    public boolean isSetDPVStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(DPVSTATUS$6) != null;
        }
    }
    
    /**
     * Sets the "DPVStatus" attribute
     */
    public void setDPVStatus(com.qas.www.web_2010_04.DPVStatusType.Enum dpvStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DPVSTATUS$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(DPVSTATUS$6);
            }
            target.setEnumValue(dpvStatus);
        }
    }
    
    /**
     * Sets (as xml) the "DPVStatus" attribute
     */
    public void xsetDPVStatus(com.qas.www.web_2010_04.DPVStatusType dpvStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.DPVStatusType target = null;
            target = (com.qas.www.web_2010_04.DPVStatusType)get_store().find_attribute_user(DPVSTATUS$6);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.DPVStatusType)get_store().add_attribute_user(DPVSTATUS$6);
            }
            target.set(dpvStatus);
        }
    }
    
    /**
     * Unsets the "DPVStatus" attribute
     */
    public void unsetDPVStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(DPVSTATUS$6);
        }
    }
    
    /**
     * Gets the "MissingSubPremise" attribute
     */
    public boolean getMissingSubPremise()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MISSINGSUBPREMISE$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(MISSINGSUBPREMISE$8);
            }
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "MissingSubPremise" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetMissingSubPremise()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(MISSINGSUBPREMISE$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(MISSINGSUBPREMISE$8);
            }
            return target;
        }
    }
    
    /**
     * True if has "MissingSubPremise" attribute
     */
    public boolean isSetMissingSubPremise()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(MISSINGSUBPREMISE$8) != null;
        }
    }
    
    /**
     * Sets the "MissingSubPremise" attribute
     */
    public void setMissingSubPremise(boolean missingSubPremise)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(MISSINGSUBPREMISE$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(MISSINGSUBPREMISE$8);
            }
            target.setBooleanValue(missingSubPremise);
        }
    }
    
    /**
     * Sets (as xml) the "MissingSubPremise" attribute
     */
    public void xsetMissingSubPremise(org.apache.xmlbeans.XmlBoolean missingSubPremise)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(MISSINGSUBPREMISE$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(MISSINGSUBPREMISE$8);
            }
            target.set(missingSubPremise);
        }
    }
    
    /**
     * Unsets the "MissingSubPremise" attribute
     */
    public void unsetMissingSubPremise()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(MISSINGSUBPREMISE$8);
        }
    }
}
