/*
 * An XML document type.
 * Localname: QAGetLayouts
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetLayoutsDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetLayouts(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetLayoutsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetLayoutsDocument
{
    
    public QAGetLayoutsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETLAYOUTS$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetLayouts");
    
    
    /**
     * Gets the "QAGetLayouts" element
     */
    public com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts getQAGetLayouts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts target = null;
            target = (com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts)get_store().find_element_user(QAGETLAYOUTS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetLayouts" element
     */
    public void setQAGetLayouts(com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts qaGetLayouts)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts target = null;
            target = (com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts)get_store().find_element_user(QAGETLAYOUTS$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts)get_store().add_element_user(QAGETLAYOUTS$0);
            }
            target.set(qaGetLayouts);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetLayouts" element
     */
    public com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts addNewQAGetLayouts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts target = null;
            target = (com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts)get_store().add_element_user(QAGETLAYOUTS$0);
            return target;
        }
    }
    /**
     * An XML QAGetLayouts(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetLayoutsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetLayoutsDocument.QAGetLayouts
    {
        
        public QAGetLayoutsImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName COUNTRY$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Country");
        private static final javax.xml.namespace.QName QACONFIG$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAConfig");
        
        
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
         * Gets the "QAConfig" element
         */
        public com.qas.www.web_2010_04.QAConfigType getQAConfig()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAConfigType target = null;
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$2, 0);
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
                return get_store().count_elements(QACONFIG$2) != 0;
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
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$2, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$2);
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
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$2);
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
                get_store().remove_element(QACONFIG$2, 0);
            }
        }
    }
}
