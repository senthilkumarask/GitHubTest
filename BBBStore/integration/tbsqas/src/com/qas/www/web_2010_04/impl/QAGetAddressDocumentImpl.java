/*
 * An XML document type.
 * Localname: QAGetAddress
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetAddressDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetAddress(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetAddressDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetAddressDocument
{
    
    public QAGetAddressDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETADDRESS$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetAddress");
    
    
    /**
     * Gets the "QAGetAddress" element
     */
    public com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress getQAGetAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress target = null;
            target = (com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress)get_store().find_element_user(QAGETADDRESS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetAddress" element
     */
    public void setQAGetAddress(com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress qaGetAddress)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress target = null;
            target = (com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress)get_store().find_element_user(QAGETADDRESS$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress)get_store().add_element_user(QAGETADDRESS$0);
            }
            target.set(qaGetAddress);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetAddress" element
     */
    public com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress addNewQAGetAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress target = null;
            target = (com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress)get_store().add_element_user(QAGETADDRESS$0);
            return target;
        }
    }
    /**
     * An XML QAGetAddress(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetAddressImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetAddressDocument.QAGetAddress
    {
        
        public QAGetAddressImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName LAYOUT$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Layout");
        private static final javax.xml.namespace.QName MONIKER$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Moniker");
        private static final javax.xml.namespace.QName QACONFIG$4 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAConfig");
        
        
        /**
         * Gets the "Layout" element
         */
        public java.lang.String getLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LAYOUT$0, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LAYOUT$0, 0);
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LAYOUT$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LAYOUT$0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LAYOUT$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LAYOUT$0);
                }
                target.set(layout);
            }
        }
        
        /**
         * Gets the "Moniker" element
         */
        public java.lang.String getMoniker()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MONIKER$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "Moniker" element
         */
        public org.apache.xmlbeans.XmlString xgetMoniker()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MONIKER$2, 0);
                return target;
            }
        }
        
        /**
         * Sets the "Moniker" element
         */
        public void setMoniker(java.lang.String moniker)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MONIKER$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MONIKER$2);
                }
                target.setStringValue(moniker);
            }
        }
        
        /**
         * Sets (as xml) the "Moniker" element
         */
        public void xsetMoniker(org.apache.xmlbeans.XmlString moniker)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MONIKER$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MONIKER$2);
                }
                target.set(moniker);
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
