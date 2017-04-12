/*
 * An XML document type.
 * Localname: QAGetExampleAddresses
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetExampleAddressesDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetExampleAddresses(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetExampleAddressesDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetExampleAddressesDocument
{
    
    public QAGetExampleAddressesDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETEXAMPLEADDRESSES$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetExampleAddresses");
    
    
    /**
     * Gets the "QAGetExampleAddresses" element
     */
    public com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses getQAGetExampleAddresses()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses target = null;
            target = (com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses)get_store().find_element_user(QAGETEXAMPLEADDRESSES$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetExampleAddresses" element
     */
    public void setQAGetExampleAddresses(com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses qaGetExampleAddresses)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses target = null;
            target = (com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses)get_store().find_element_user(QAGETEXAMPLEADDRESSES$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses)get_store().add_element_user(QAGETEXAMPLEADDRESSES$0);
            }
            target.set(qaGetExampleAddresses);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetExampleAddresses" element
     */
    public com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses addNewQAGetExampleAddresses()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses target = null;
            target = (com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses)get_store().add_element_user(QAGETEXAMPLEADDRESSES$0);
            return target;
        }
    }
    /**
     * An XML QAGetExampleAddresses(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetExampleAddressesImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetExampleAddressesDocument.QAGetExampleAddresses
    {
        
        public QAGetExampleAddressesImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName COUNTRY$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Country");
        private static final javax.xml.namespace.QName LAYOUT$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Layout");
        private static final javax.xml.namespace.QName QACONFIG$4 = 
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
         * Gets the "Layout" element
         */
        public java.lang.String getLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LAYOUT$2, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LAYOUT$2, 0);
                return target;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LAYOUT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LAYOUT$2);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LAYOUT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LAYOUT$2);
                }
                target.set(layout);
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
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$4, 0);
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
                return get_store().count_elements(QACONFIG$4) != 0;
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
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$4, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$4);
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
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$4);
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
                get_store().remove_element(QACONFIG$4, 0);
            }
        }
    }
}
