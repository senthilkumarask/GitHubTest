/*
 * XML Type:  DataplusGroupType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.DataplusGroupType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML DataplusGroupType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class DataplusGroupTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.DataplusGroupType
{
    
    public DataplusGroupTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DATAPLUSGROUPITEM$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DataplusGroupItem");
    private static final javax.xml.namespace.QName GROUPNAME$2 = 
        new javax.xml.namespace.QName("", "GroupName");
    
    
    /**
     * Gets array of all "DataplusGroupItem" elements
     */
    public java.lang.String[] getDataplusGroupItemArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(DATAPLUSGROUPITEM$0, targetList);
            java.lang.String[] result = new java.lang.String[targetList.size()];
            for (int i = 0, len = targetList.size() ; i < len ; i++)
                result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getStringValue();
            return result;
        }
    }
    
    /**
     * Gets ith "DataplusGroupItem" element
     */
    public java.lang.String getDataplusGroupItemArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATAPLUSGROUPITEM$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) array of all "DataplusGroupItem" elements
     */
    public org.apache.xmlbeans.XmlString[] xgetDataplusGroupItemArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(DATAPLUSGROUPITEM$0, targetList);
            org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets (as xml) ith "DataplusGroupItem" element
     */
    public org.apache.xmlbeans.XmlString xgetDataplusGroupItemArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATAPLUSGROUPITEM$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return (org.apache.xmlbeans.XmlString)target;
        }
    }
    
    /**
     * Returns number of "DataplusGroupItem" element
     */
    public int sizeOfDataplusGroupItemArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(DATAPLUSGROUPITEM$0);
        }
    }
    
    /**
     * Sets array of all "DataplusGroupItem" element
     */
    public void setDataplusGroupItemArray(java.lang.String[] dataplusGroupItemArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(dataplusGroupItemArray, DATAPLUSGROUPITEM$0);
        }
    }
    
    /**
     * Sets ith "DataplusGroupItem" element
     */
    public void setDataplusGroupItemArray(int i, java.lang.String dataplusGroupItem)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATAPLUSGROUPITEM$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(dataplusGroupItem);
        }
    }
    
    /**
     * Sets (as xml) array of all "DataplusGroupItem" element
     */
    public void xsetDataplusGroupItemArray(org.apache.xmlbeans.XmlString[]dataplusGroupItemArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(dataplusGroupItemArray, DATAPLUSGROUPITEM$0);
        }
    }
    
    /**
     * Sets (as xml) ith "DataplusGroupItem" element
     */
    public void xsetDataplusGroupItemArray(int i, org.apache.xmlbeans.XmlString dataplusGroupItem)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATAPLUSGROUPITEM$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(dataplusGroupItem);
        }
    }
    
    /**
     * Inserts the value as the ith "DataplusGroupItem" element
     */
    public void insertDataplusGroupItem(int i, java.lang.String dataplusGroupItem)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = 
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(DATAPLUSGROUPITEM$0, i);
            target.setStringValue(dataplusGroupItem);
        }
    }
    
    /**
     * Appends the value as the last "DataplusGroupItem" element
     */
    public void addDataplusGroupItem(java.lang.String dataplusGroupItem)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DATAPLUSGROUPITEM$0);
            target.setStringValue(dataplusGroupItem);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "DataplusGroupItem" element
     */
    public org.apache.xmlbeans.XmlString insertNewDataplusGroupItem(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(DATAPLUSGROUPITEM$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "DataplusGroupItem" element
     */
    public org.apache.xmlbeans.XmlString addNewDataplusGroupItem()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DATAPLUSGROUPITEM$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "DataplusGroupItem" element
     */
    public void removeDataplusGroupItem(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(DATAPLUSGROUPITEM$0, i);
        }
    }
    
    /**
     * Gets the "GroupName" attribute
     */
    public java.lang.String getGroupName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(GROUPNAME$2);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "GroupName" attribute
     */
    public org.apache.xmlbeans.XmlString xgetGroupName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(GROUPNAME$2);
            return target;
        }
    }
    
    /**
     * True if has "GroupName" attribute
     */
    public boolean isSetGroupName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(GROUPNAME$2) != null;
        }
    }
    
    /**
     * Sets the "GroupName" attribute
     */
    public void setGroupName(java.lang.String groupName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(GROUPNAME$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(GROUPNAME$2);
            }
            target.setStringValue(groupName);
        }
    }
    
    /**
     * Sets (as xml) the "GroupName" attribute
     */
    public void xsetGroupName(org.apache.xmlbeans.XmlString groupName)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(GROUPNAME$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(GROUPNAME$2);
            }
            target.set(groupName);
        }
    }
    
    /**
     * Unsets the "GroupName" attribute
     */
    public void unsetGroupName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(GROUPNAME$2);
        }
    }
}
