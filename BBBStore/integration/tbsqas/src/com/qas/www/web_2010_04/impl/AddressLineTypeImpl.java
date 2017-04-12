/*
 * XML Type:  AddressLineType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.AddressLineType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML AddressLineType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class AddressLineTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.AddressLineType
{
    
    public AddressLineTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName LABEL$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Label");
    private static final javax.xml.namespace.QName LINE$2 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Line");
    private static final javax.xml.namespace.QName DATAPLUSGROUP$4 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DataplusGroup");
    private static final javax.xml.namespace.QName LINECONTENT$6 = 
        new javax.xml.namespace.QName("", "LineContent");
    private static final javax.xml.namespace.QName OVERFLOW$8 = 
        new javax.xml.namespace.QName("", "Overflow");
    private static final javax.xml.namespace.QName TRUNCATED$10 = 
        new javax.xml.namespace.QName("", "Truncated");
    
    
    /**
     * Gets the "Label" element
     */
    public java.lang.String getLabel()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LABEL$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Label" element
     */
    public org.apache.xmlbeans.XmlString xgetLabel()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LABEL$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "Label" element
     */
    public boolean isSetLabel()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LABEL$0) != 0;
        }
    }
    
    /**
     * Sets the "Label" element
     */
    public void setLabel(java.lang.String label)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LABEL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LABEL$0);
            }
            target.setStringValue(label);
        }
    }
    
    /**
     * Sets (as xml) the "Label" element
     */
    public void xsetLabel(org.apache.xmlbeans.XmlString label)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LABEL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LABEL$0);
            }
            target.set(label);
        }
    }
    
    /**
     * Unsets the "Label" element
     */
    public void unsetLabel()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LABEL$0, 0);
        }
    }
    
    /**
     * Gets the "Line" element
     */
    public java.lang.String getLine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LINE$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Line" element
     */
    public org.apache.xmlbeans.XmlString xgetLine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LINE$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "Line" element
     */
    public boolean isSetLine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LINE$2) != 0;
        }
    }
    
    /**
     * Sets the "Line" element
     */
    public void setLine(java.lang.String line)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LINE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LINE$2);
            }
            target.setStringValue(line);
        }
    }
    
    /**
     * Sets (as xml) the "Line" element
     */
    public void xsetLine(org.apache.xmlbeans.XmlString line)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LINE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LINE$2);
            }
            target.set(line);
        }
    }
    
    /**
     * Unsets the "Line" element
     */
    public void unsetLine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LINE$2, 0);
        }
    }
    
    /**
     * Gets array of all "DataplusGroup" elements
     */
    public com.qas.www.web_2010_04.DataplusGroupType[] getDataplusGroupArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(DATAPLUSGROUP$4, targetList);
            com.qas.www.web_2010_04.DataplusGroupType[] result = new com.qas.www.web_2010_04.DataplusGroupType[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "DataplusGroup" element
     */
    public com.qas.www.web_2010_04.DataplusGroupType getDataplusGroupArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.DataplusGroupType target = null;
            target = (com.qas.www.web_2010_04.DataplusGroupType)get_store().find_element_user(DATAPLUSGROUP$4, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "DataplusGroup" element
     */
    public int sizeOfDataplusGroupArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DATAPLUSGROUP$4);
        }
    }
    
    /**
     * Sets array of all "DataplusGroup" element
     */
    public void setDataplusGroupArray(com.qas.www.web_2010_04.DataplusGroupType[] dataplusGroupArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(dataplusGroupArray, DATAPLUSGROUP$4);
        }
    }
    
    /**
     * Sets ith "DataplusGroup" element
     */
    public void setDataplusGroupArray(int i, com.qas.www.web_2010_04.DataplusGroupType dataplusGroup)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.DataplusGroupType target = null;
            target = (com.qas.www.web_2010_04.DataplusGroupType)get_store().find_element_user(DATAPLUSGROUP$4, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(dataplusGroup);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "DataplusGroup" element
     */
    public com.qas.www.web_2010_04.DataplusGroupType insertNewDataplusGroup(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.DataplusGroupType target = null;
            target = (com.qas.www.web_2010_04.DataplusGroupType)get_store().insert_element_user(DATAPLUSGROUP$4, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "DataplusGroup" element
     */
    public com.qas.www.web_2010_04.DataplusGroupType addNewDataplusGroup()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.DataplusGroupType target = null;
            target = (com.qas.www.web_2010_04.DataplusGroupType)get_store().add_element_user(DATAPLUSGROUP$4);
            return target;
        }
    }
    
    /**
     * Removes the ith "DataplusGroup" element
     */
    public void removeDataplusGroup(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DATAPLUSGROUP$4, i);
        }
    }
    
    /**
     * Gets the "LineContent" attribute
     */
    public com.qas.www.web_2010_04.LineContentType.Enum getLineContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(LINECONTENT$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(LINECONTENT$6);
            }
            if (target == null)
            {
                return null;
            }
            return (com.qas.www.web_2010_04.LineContentType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "LineContent" attribute
     */
    public com.qas.www.web_2010_04.LineContentType xgetLineContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.LineContentType target = null;
            target = (com.qas.www.web_2010_04.LineContentType)get_store().find_attribute_user(LINECONTENT$6);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.LineContentType)get_default_attribute_value(LINECONTENT$6);
            }
            return target;
        }
    }
    
    /**
     * True if has "LineContent" attribute
     */
    public boolean isSetLineContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(LINECONTENT$6) != null;
        }
    }
    
    /**
     * Sets the "LineContent" attribute
     */
    public void setLineContent(com.qas.www.web_2010_04.LineContentType.Enum lineContent)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(LINECONTENT$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(LINECONTENT$6);
            }
            target.setEnumValue(lineContent);
        }
    }
    
    /**
     * Sets (as xml) the "LineContent" attribute
     */
    public void xsetLineContent(com.qas.www.web_2010_04.LineContentType lineContent)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.LineContentType target = null;
            target = (com.qas.www.web_2010_04.LineContentType)get_store().find_attribute_user(LINECONTENT$6);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.LineContentType)get_store().add_attribute_user(LINECONTENT$6);
            }
            target.set(lineContent);
        }
    }
    
    /**
     * Unsets the "LineContent" attribute
     */
    public void unsetLineContent()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(LINECONTENT$6);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(OVERFLOW$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(OVERFLOW$8);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(OVERFLOW$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(OVERFLOW$8);
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
            return get_store().find_attribute_user(OVERFLOW$8) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(OVERFLOW$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(OVERFLOW$8);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(OVERFLOW$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(OVERFLOW$8);
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
            get_store().remove_attribute(OVERFLOW$8);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TRUNCATED$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(TRUNCATED$10);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(TRUNCATED$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(TRUNCATED$10);
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
            return get_store().find_attribute_user(TRUNCATED$10) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TRUNCATED$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TRUNCATED$10);
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
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(TRUNCATED$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(TRUNCATED$10);
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
            get_store().remove_attribute(TRUNCATED$10);
        }
    }
}
