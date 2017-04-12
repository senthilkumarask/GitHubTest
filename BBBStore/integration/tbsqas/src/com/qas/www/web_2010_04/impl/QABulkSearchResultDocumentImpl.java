/*
 * An XML document type.
 * Localname: QABulkSearchResult
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QABulkSearchResultDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QABulkSearchResult(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QABulkSearchResultDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QABulkSearchResultDocument
{
    
    public QABulkSearchResultDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QABULKSEARCHRESULT$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QABulkSearchResult");
    
    
    /**
     * Gets the "QABulkSearchResult" element
     */
    public com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult getQABulkSearchResult()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult target = null;
            target = (com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult)get_store().find_element_user(QABULKSEARCHRESULT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QABulkSearchResult" element
     */
    public void setQABulkSearchResult(com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult qaBulkSearchResult)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult target = null;
            target = (com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult)get_store().find_element_user(QABULKSEARCHRESULT$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult)get_store().add_element_user(QABULKSEARCHRESULT$0);
            }
            target.set(qaBulkSearchResult);
        }
    }
    
    /**
     * Appends and returns a new empty "QABulkSearchResult" element
     */
    public com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult addNewQABulkSearchResult()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult target = null;
            target = (com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult)get_store().add_element_user(QABULKSEARCHRESULT$0);
            return target;
        }
    }
    /**
     * An XML QABulkSearchResult(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QABulkSearchResultImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult
    {
        
        public QABulkSearchResultImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName BULKADDRESS$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "BulkAddress");
        private static final javax.xml.namespace.QName BULKERROR$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "BulkError");
        private static final javax.xml.namespace.QName ERRORCODE$4 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ErrorCode");
        private static final javax.xml.namespace.QName COUNT$6 = 
            new javax.xml.namespace.QName("", "Count");
        private static final javax.xml.namespace.QName SEARCHCOUNT$8 = 
            new javax.xml.namespace.QName("", "SearchCount");
        
        
        /**
         * Gets array of all "BulkAddress" elements
         */
        public com.qas.www.web_2010_04.QABulkSearchItemType[] getBulkAddressArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(BULKADDRESS$0, targetList);
                com.qas.www.web_2010_04.QABulkSearchItemType[] result = new com.qas.www.web_2010_04.QABulkSearchItemType[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets ith "BulkAddress" element
         */
        public com.qas.www.web_2010_04.QABulkSearchItemType getBulkAddressArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QABulkSearchItemType target = null;
                target = (com.qas.www.web_2010_04.QABulkSearchItemType)get_store().find_element_user(BULKADDRESS$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        /**
         * Returns number of "BulkAddress" element
         */
        public int sizeOfBulkAddressArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(BULKADDRESS$0);
            }
        }
        
        /**
         * Sets array of all "BulkAddress" element
         */
        public void setBulkAddressArray(com.qas.www.web_2010_04.QABulkSearchItemType[] bulkAddressArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(bulkAddressArray, BULKADDRESS$0);
            }
        }
        
        /**
         * Sets ith "BulkAddress" element
         */
        public void setBulkAddressArray(int i, com.qas.www.web_2010_04.QABulkSearchItemType bulkAddress)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QABulkSearchItemType target = null;
                target = (com.qas.www.web_2010_04.QABulkSearchItemType)get_store().find_element_user(BULKADDRESS$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(bulkAddress);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "BulkAddress" element
         */
        public com.qas.www.web_2010_04.QABulkSearchItemType insertNewBulkAddress(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QABulkSearchItemType target = null;
                target = (com.qas.www.web_2010_04.QABulkSearchItemType)get_store().insert_element_user(BULKADDRESS$0, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "BulkAddress" element
         */
        public com.qas.www.web_2010_04.QABulkSearchItemType addNewBulkAddress()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QABulkSearchItemType target = null;
                target = (com.qas.www.web_2010_04.QABulkSearchItemType)get_store().add_element_user(BULKADDRESS$0);
                return target;
            }
        }
        
        /**
         * Removes the ith "BulkAddress" element
         */
        public void removeBulkAddress(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(BULKADDRESS$0, i);
            }
        }
        
        /**
         * Gets the "BulkError" element
         */
        public java.lang.String getBulkError()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BULKERROR$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "BulkError" element
         */
        public org.apache.xmlbeans.XmlString xgetBulkError()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BULKERROR$2, 0);
                return target;
            }
        }
        
        /**
         * True if has "BulkError" element
         */
        public boolean isSetBulkError()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(BULKERROR$2) != 0;
            }
        }
        
        /**
         * Sets the "BulkError" element
         */
        public void setBulkError(java.lang.String bulkError)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BULKERROR$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BULKERROR$2);
                }
                target.setStringValue(bulkError);
            }
        }
        
        /**
         * Sets (as xml) the "BulkError" element
         */
        public void xsetBulkError(org.apache.xmlbeans.XmlString bulkError)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BULKERROR$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BULKERROR$2);
                }
                target.set(bulkError);
            }
        }
        
        /**
         * Unsets the "BulkError" element
         */
        public void unsetBulkError()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(BULKERROR$2, 0);
            }
        }
        
        /**
         * Gets the "ErrorCode" element
         */
        public java.lang.String getErrorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORCODE$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "ErrorCode" element
         */
        public org.apache.xmlbeans.XmlString xgetErrorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORCODE$4, 0);
                return target;
            }
        }
        
        /**
         * True if has "ErrorCode" element
         */
        public boolean isSetErrorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ERRORCODE$4) != 0;
            }
        }
        
        /**
         * Sets the "ErrorCode" element
         */
        public void setErrorCode(java.lang.String errorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORCODE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ERRORCODE$4);
                }
                target.setStringValue(errorCode);
            }
        }
        
        /**
         * Sets (as xml) the "ErrorCode" element
         */
        public void xsetErrorCode(org.apache.xmlbeans.XmlString errorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORCODE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ERRORCODE$4);
                }
                target.set(errorCode);
            }
        }
        
        /**
         * Unsets the "ErrorCode" element
         */
        public void unsetErrorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ERRORCODE$4, 0);
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(COUNT$6);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(COUNT$6);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(COUNT$6);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_default_attribute_value(COUNT$6);
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
                return get_store().find_attribute_user(COUNT$6) != null;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(COUNT$6);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(COUNT$6);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(COUNT$6);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(COUNT$6);
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
                get_store().remove_attribute(COUNT$6);
            }
        }
        
        /**
         * Gets the "SearchCount" attribute
         */
        public java.lang.String getSearchCount()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(SEARCHCOUNT$8);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(SEARCHCOUNT$8);
                }
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "SearchCount" attribute
         */
        public org.apache.xmlbeans.XmlString xgetSearchCount()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(SEARCHCOUNT$8);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_default_attribute_value(SEARCHCOUNT$8);
                }
                return target;
            }
        }
        
        /**
         * True if has "SearchCount" attribute
         */
        public boolean isSetSearchCount()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().find_attribute_user(SEARCHCOUNT$8) != null;
            }
        }
        
        /**
         * Sets the "SearchCount" attribute
         */
        public void setSearchCount(java.lang.String searchCount)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(SEARCHCOUNT$8);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(SEARCHCOUNT$8);
                }
                target.setStringValue(searchCount);
            }
        }
        
        /**
         * Sets (as xml) the "SearchCount" attribute
         */
        public void xsetSearchCount(org.apache.xmlbeans.XmlString searchCount)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(SEARCHCOUNT$8);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(SEARCHCOUNT$8);
                }
                target.set(searchCount);
            }
        }
        
        /**
         * Unsets the "SearchCount" attribute
         */
        public void unsetSearchCount()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_attribute(SEARCHCOUNT$8);
            }
        }
    }
}
