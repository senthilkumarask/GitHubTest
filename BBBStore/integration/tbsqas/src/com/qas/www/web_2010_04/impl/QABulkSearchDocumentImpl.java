/*
 * An XML document type.
 * Localname: QABulkSearch
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QABulkSearchDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QABulkSearch(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QABulkSearchDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QABulkSearchDocument
{
    
    public QABulkSearchDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QABULKSEARCH$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QABulkSearch");
    
    
    /**
     * Gets the "QABulkSearch" element
     */
    public com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch getQABulkSearch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch target = null;
            target = (com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch)get_store().find_element_user(QABULKSEARCH$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QABulkSearch" element
     */
    public void setQABulkSearch(com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch qaBulkSearch)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch target = null;
            target = (com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch)get_store().find_element_user(QABULKSEARCH$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch)get_store().add_element_user(QABULKSEARCH$0);
            }
            target.set(qaBulkSearch);
        }
    }
    
    /**
     * Appends and returns a new empty "QABulkSearch" element
     */
    public com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch addNewQABulkSearch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch target = null;
            target = (com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch)get_store().add_element_user(QABULKSEARCH$0);
            return target;
        }
    }
    /**
     * An XML QABulkSearch(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QABulkSearchImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QABulkSearchDocument.QABulkSearch
    {
        
        public QABulkSearchImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName COUNTRY$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Country");
        private static final javax.xml.namespace.QName ENGINE$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Engine");
        private static final javax.xml.namespace.QName LAYOUT$4 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Layout");
        private static final javax.xml.namespace.QName QACONFIG$6 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAConfig");
        private static final javax.xml.namespace.QName BULKSEARCHTERM$8 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "BulkSearchTerm");
        
        
        /**
         * Gets the "Country" element
         */
        public java.lang.String getCountry()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COUNTRY$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "Country" element
         */
        public com.qas.www.web_2010_04.DataIDType xgetCountry()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.DataIDType target = null;
                target = (com.qas.www.web_2010_04.DataIDType)get_store().find_element_user(COUNTRY$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "Country" element
         */
        public void setCountry(java.lang.String country)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COUNTRY$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COUNTRY$0);
                }
                target.setStringValue(country);
            }
        }
        
        /**
         * Sets (as xml) the "Country" element
         */
        public void xsetCountry(com.qas.www.web_2010_04.DataIDType country)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.DataIDType target = null;
                target = (com.qas.www.web_2010_04.DataIDType)get_store().find_element_user(COUNTRY$0, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.DataIDType)get_store().add_element_user(COUNTRY$0);
                }
                target.set(country);
            }
        }
        
        /**
         * Gets the "Engine" element
         */
        public com.qas.www.web_2010_04.EngineType getEngine()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.EngineType target = null;
                target = (com.qas.www.web_2010_04.EngineType)get_store().find_element_user(ENGINE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "Engine" element
         */
        public void setEngine(com.qas.www.web_2010_04.EngineType engine)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.EngineType target = null;
                target = (com.qas.www.web_2010_04.EngineType)get_store().find_element_user(ENGINE$2, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.EngineType)get_store().add_element_user(ENGINE$2);
                }
                target.set(engine);
            }
        }
        
        /**
         * Appends and returns a new empty "Engine" element
         */
        public com.qas.www.web_2010_04.EngineType addNewEngine()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.EngineType target = null;
                target = (com.qas.www.web_2010_04.EngineType)get_store().add_element_user(ENGINE$2);
                return target;
            }
        }
        
        /**
         * Gets the "Layout" element
         */
        public java.lang.String getLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LAYOUT$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "Layout" element
         */
        public org.apache.xmlbeans.XmlString xgetLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LAYOUT$4, 0);
                return target;
            }
        }
        
        /**
         * True if has "Layout" element
         */
        public boolean isSetLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(LAYOUT$4) != 0;
            }
        }
        
        /**
         * Sets the "Layout" element
         */
        public void setLayout(java.lang.String layout)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LAYOUT$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LAYOUT$4);
                }
                target.setStringValue(layout);
            }
        }
        
        /**
         * Sets (as xml) the "Layout" element
         */
        public void xsetLayout(org.apache.xmlbeans.XmlString layout)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LAYOUT$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LAYOUT$4);
                }
                target.set(layout);
            }
        }
        
        /**
         * Unsets the "Layout" element
         */
        public void unsetLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(LAYOUT$4, 0);
            }
        }
        
        /**
         * Gets the "QAConfig" element
         */
        public com.qas.www.web_2010_04.QAConfigType getQAConfig()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAConfigType target = null;
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$6, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "QAConfig" element
         */
        public boolean isSetQAConfig()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(QACONFIG$6) != 0;
            }
        }
        
        /**
         * Sets the "QAConfig" element
         */
        public void setQAConfig(com.qas.www.web_2010_04.QAConfigType qaConfig)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAConfigType target = null;
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$6, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$6);
                }
                target.set(qaConfig);
            }
        }
        
        /**
         * Appends and returns a new empty "QAConfig" element
         */
        public com.qas.www.web_2010_04.QAConfigType addNewQAConfig()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAConfigType target = null;
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$6);
                return target;
            }
        }
        
        /**
         * Unsets the "QAConfig" element
         */
        public void unsetQAConfig()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(QACONFIG$6, 0);
            }
        }
        
        /**
         * Gets the "BulkSearchTerm" element
         */
        public com.qas.www.web_2010_04.QASearchType getBulkSearchTerm()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QASearchType target = null;
                target = (com.qas.www.web_2010_04.QASearchType)get_store().find_element_user(BULKSEARCHTERM$8, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "BulkSearchTerm" element
         */
        public boolean isSetBulkSearchTerm()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(BULKSEARCHTERM$8) != 0;
            }
        }
        
        /**
         * Sets the "BulkSearchTerm" element
         */
        public void setBulkSearchTerm(com.qas.www.web_2010_04.QASearchType bulkSearchTerm)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QASearchType target = null;
                target = (com.qas.www.web_2010_04.QASearchType)get_store().find_element_user(BULKSEARCHTERM$8, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QASearchType)get_store().add_element_user(BULKSEARCHTERM$8);
                }
                target.set(bulkSearchTerm);
            }
        }
        
        /**
         * Appends and returns a new empty "BulkSearchTerm" element
         */
        public com.qas.www.web_2010_04.QASearchType addNewBulkSearchTerm()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QASearchType target = null;
                target = (com.qas.www.web_2010_04.QASearchType)get_store().add_element_user(BULKSEARCHTERM$8);
                return target;
            }
        }
        
        /**
         * Unsets the "BulkSearchTerm" element
         */
        public void unsetBulkSearchTerm()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(BULKSEARCHTERM$8, 0);
            }
        }
    }
}
