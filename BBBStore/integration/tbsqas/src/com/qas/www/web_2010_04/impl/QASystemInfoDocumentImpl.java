/*
 * An XML document type.
 * Localname: QASystemInfo
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QASystemInfoDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QASystemInfo(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QASystemInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QASystemInfoDocument
{
    
    public QASystemInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QASYSTEMINFO$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QASystemInfo");
    
    
    /**
     * Gets the "QASystemInfo" element
     */
    public com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo getQASystemInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo target = null;
            target = (com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo)get_store().find_element_user(QASYSTEMINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QASystemInfo" element
     */
    public void setQASystemInfo(com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo qaSystemInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo target = null;
            target = (com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo)get_store().find_element_user(QASYSTEMINFO$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo)get_store().add_element_user(QASYSTEMINFO$0);
            }
            target.set(qaSystemInfo);
        }
    }
    
    /**
     * Appends and returns a new empty "QASystemInfo" element
     */
    public com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo addNewQASystemInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo target = null;
            target = (com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo)get_store().add_element_user(QASYSTEMINFO$0);
            return target;
        }
    }
    /**
     * An XML QASystemInfo(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QASystemInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QASystemInfoDocument.QASystemInfo
    {
        
        public QASystemInfoImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SYSTEMINFO$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "SystemInfo");
        
        
        /**
         * Gets array of all "SystemInfo" elements
         */
        public java.lang.String[] getSystemInfoArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(SYSTEMINFO$0, targetList);
                java.lang.String[] result = new java.lang.String[targetList.size()];
                for (int i = 0, len = targetList.size() ; i < len ; i++)
                    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getStringValue();
                return result;
            }
        }
        
        /**
         * Gets ith "SystemInfo" element
         */
        public java.lang.String getSystemInfoArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SYSTEMINFO$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) array of all "SystemInfo" elements
         */
        public org.apache.xmlbeans.XmlString[] xgetSystemInfoArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(SYSTEMINFO$0, targetList);
                org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets (as xml) ith "SystemInfo" element
         */
        public org.apache.xmlbeans.XmlString xgetSystemInfoArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SYSTEMINFO$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return (org.apache.xmlbeans.XmlString)target;
            }
        }
        
        /**
         * Returns number of "SystemInfo" element
         */
        public int sizeOfSystemInfoArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SYSTEMINFO$0);
            }
        }
        
        /**
         * Sets array of all "SystemInfo" element
         */
        public void setSystemInfoArray(java.lang.String[] systemInfoArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(systemInfoArray, SYSTEMINFO$0);
            }
        }
        
        /**
         * Sets ith "SystemInfo" element
         */
        public void setSystemInfoArray(int i, java.lang.String systemInfo)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SYSTEMINFO$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.setStringValue(systemInfo);
            }
        }
        
        /**
         * Sets (as xml) array of all "SystemInfo" element
         */
        public void xsetSystemInfoArray(org.apache.xmlbeans.XmlString[]systemInfoArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(systemInfoArray, SYSTEMINFO$0);
            }
        }
        
        /**
         * Sets (as xml) ith "SystemInfo" element
         */
        public void xsetSystemInfoArray(int i, org.apache.xmlbeans.XmlString systemInfo)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SYSTEMINFO$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(systemInfo);
            }
        }
        
        /**
         * Inserts the value as the ith "SystemInfo" element
         */
        public void insertSystemInfo(int i, java.lang.String systemInfo)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = 
                    (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(SYSTEMINFO$0, i);
                target.setStringValue(systemInfo);
            }
        }
        
        /**
         * Appends the value as the last "SystemInfo" element
         */
        public void addSystemInfo(java.lang.String systemInfo)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SYSTEMINFO$0);
                target.setStringValue(systemInfo);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "SystemInfo" element
         */
        public org.apache.xmlbeans.XmlString insertNewSystemInfo(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(SYSTEMINFO$0, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "SystemInfo" element
         */
        public org.apache.xmlbeans.XmlString addNewSystemInfo()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SYSTEMINFO$0);
                return target;
            }
        }
        
        /**
         * Removes the ith "SystemInfo" element
         */
        public void removeSystemInfo(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SYSTEMINFO$0, i);
            }
        }
    }
}
