/*
 * An XML document type.
 * Localname: QASearch
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QASearchDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QASearch(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QASearchDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QASearchDocument
{
    
    public QASearchDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QASEARCH$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QASearch");
    
    
    /**
     * Gets the "QASearch" element
     */
    public com.qas.www.web_2010_04.QASearchDocument.QASearch getQASearch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASearchDocument.QASearch target = null;
            target = (com.qas.www.web_2010_04.QASearchDocument.QASearch)get_store().find_element_user(QASEARCH$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QASearch" element
     */
    public void setQASearch(com.qas.www.web_2010_04.QASearchDocument.QASearch qaSearch)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASearchDocument.QASearch target = null;
            target = (com.qas.www.web_2010_04.QASearchDocument.QASearch)get_store().find_element_user(QASEARCH$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QASearchDocument.QASearch)get_store().add_element_user(QASEARCH$0);
            }
            target.set(qaSearch);
        }
    }
    
    /**
     * Appends and returns a new empty "QASearch" element
     */
    public com.qas.www.web_2010_04.QASearchDocument.QASearch addNewQASearch()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASearchDocument.QASearch target = null;
            target = (com.qas.www.web_2010_04.QASearchDocument.QASearch)get_store().add_element_user(QASEARCH$0);
            return target;
        }
    }
    /**
     * An XML QASearch(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QASearchImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QASearchDocument.QASearch
    {
        
        public QASearchImpl(org.apache.xmlbeans.SchemaType sType)
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
        private static final javax.xml.namespace.QName SEARCH$8 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Search");
        private static final javax.xml.namespace.QName FORMATTEDADDRESSINPICKLIST$10 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "FormattedAddressInPicklist");
        
        
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
         * Gets the "Search" element
         */
        public java.lang.String getSearch()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SEARCH$8, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "Search" element
         */
        public org.apache.xmlbeans.XmlString xgetSearch()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SEARCH$8, 0);
                return target;
            }
        }
        
        /**
         * Sets the "Search" element
         */
        public void setSearch(java.lang.String search)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SEARCH$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SEARCH$8);
                }
                target.setStringValue(search);
            }
        }
        
        /**
         * Sets (as xml) the "Search" element
         */
        public void xsetSearch(org.apache.xmlbeans.XmlString search)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SEARCH$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SEARCH$8);
                }
                target.set(search);
            }
        }
        
        /**
         * Gets the "FormattedAddressInPicklist" element
         */
        public boolean getFormattedAddressInPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FORMATTEDADDRESSINPICKLIST$10, 0);
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "FormattedAddressInPicklist" element
         */
        public org.apache.xmlbeans.XmlBoolean xgetFormattedAddressInPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(FORMATTEDADDRESSINPICKLIST$10, 0);
                return target;
            }
        }
        
        /**
         * True if has "FormattedAddressInPicklist" element
         */
        public boolean isSetFormattedAddressInPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(FORMATTEDADDRESSINPICKLIST$10) != 0;
            }
        }
        
        /**
         * Sets the "FormattedAddressInPicklist" element
         */
        public void setFormattedAddressInPicklist(boolean formattedAddressInPicklist)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FORMATTEDADDRESSINPICKLIST$10, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FORMATTEDADDRESSINPICKLIST$10);
                }
                target.setBooleanValue(formattedAddressInPicklist);
            }
        }
        
        /**
         * Sets (as xml) the "FormattedAddressInPicklist" element
         */
        public void xsetFormattedAddressInPicklist(org.apache.xmlbeans.XmlBoolean formattedAddressInPicklist)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(FORMATTEDADDRESSINPICKLIST$10, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(FORMATTEDADDRESSINPICKLIST$10);
                }
                target.set(formattedAddressInPicklist);
            }
        }
        
        /**
         * Unsets the "FormattedAddressInPicklist" element
         */
        public void unsetFormattedAddressInPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(FORMATTEDADDRESSINPICKLIST$10, 0);
            }
        }
    }
}
