/*
 * XML Type:  QASearchType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QASearchType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML QASearchType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class QASearchTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QASearchType
{
    
    public QASearchTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SEARCH$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Search");
    private static final javax.xml.namespace.QName COUNT$2 = 
        new javax.xml.namespace.QName("", "Count");
    
    
    /**
     * Gets array of all "Search" elements
     */
    public java.lang.String[] getSearchArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(SEARCH$0, targetList);
            java.lang.String[] result = new java.lang.String[targetList.size()];
            for (int i = 0, len = targetList.size() ; i < len ; i++)
                result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getStringValue();
            return result;
        }
    }
    
    /**
     * Gets ith "Search" element
     */
    public java.lang.String getSearchArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SEARCH$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) array of all "Search" elements
     */
    public org.apache.xmlbeans.XmlString[] xgetSearchArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(SEARCH$0, targetList);
            org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets (as xml) ith "Search" element
     */
    public org.apache.xmlbeans.XmlString xgetSearchArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SEARCH$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return (org.apache.xmlbeans.XmlString)target;
        }
    }
    
    /**
     * Returns number of "Search" element
     */
    public int sizeOfSearchArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SEARCH$0);
        }
    }
    
    /**
     * Sets array of all "Search" element
     */
    public void setSearchArray(java.lang.String[] searchArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(searchArray, SEARCH$0);
        }
    }
    
    /**
     * Sets ith "Search" element
     */
    public void setSearchArray(int i, java.lang.String search)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SEARCH$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(search);
        }
    }
    
    /**
     * Sets (as xml) array of all "Search" element
     */
    public void xsetSearchArray(org.apache.xmlbeans.XmlString[]searchArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(searchArray, SEARCH$0);
        }
    }
    
    /**
     * Sets (as xml) ith "Search" element
     */
    public void xsetSearchArray(int i, org.apache.xmlbeans.XmlString search)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SEARCH$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(search);
        }
    }
    
    /**
     * Inserts the value as the ith "Search" element
     */
    public void insertSearch(int i, java.lang.String search)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = 
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(SEARCH$0, i);
            target.setStringValue(search);
        }
    }
    
    /**
     * Appends the value as the last "Search" element
     */
    public void addSearch(java.lang.String search)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SEARCH$0);
            target.setStringValue(search);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "Search" element
     */
    public org.apache.xmlbeans.XmlString insertNewSearch(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(SEARCH$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "Search" element
     */
    public org.apache.xmlbeans.XmlString addNewSearch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SEARCH$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "Search" element
     */
    public void removeSearch(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SEARCH$0, i);
        }
    }
    
    /**
     * Gets the "Count" attribute
     */
    public java.lang.String getCount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(COUNT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(COUNT$2);
            }
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Count" attribute
     */
    public org.apache.xmlbeans.XmlString xgetCount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(COUNT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_default_attribute_value(COUNT$2);
            }
            return target;
        }
    }
    
    /**
     * True if has "Count" attribute
     */
    public boolean isSetCount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(COUNT$2) != null;
        }
    }
    
    /**
     * Sets the "Count" attribute
     */
    public void setCount(java.lang.String count)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(COUNT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(COUNT$2);
            }
            target.setStringValue(count);
        }
    }
    
    /**
     * Sets (as xml) the "Count" attribute
     */
    public void xsetCount(org.apache.xmlbeans.XmlString count)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(COUNT$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(COUNT$2);
            }
            target.set(count);
        }
    }
    
    /**
     * Unsets the "Count" attribute
     */
    public void unsetCount()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(COUNT$2);
        }
    }
}
