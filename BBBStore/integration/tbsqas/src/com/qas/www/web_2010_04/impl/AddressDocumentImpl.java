/*
 * An XML document type.
 * Localname: Address
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.AddressDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one Address(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class AddressDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.AddressDocument
{
    
    public AddressDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ADDRESS$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Address");
    
    
    /**
     * Gets the "Address" element
     */
    public com.qas.www.web_2010_04.AddressDocument.Address getAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.AddressDocument.Address target = null;
            target = (com.qas.www.web_2010_04.AddressDocument.Address)get_store().find_element_user(ADDRESS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "Address" element
     */
    public void setAddress(com.qas.www.web_2010_04.AddressDocument.Address address)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.AddressDocument.Address target = null;
            target = (com.qas.www.web_2010_04.AddressDocument.Address)get_store().find_element_user(ADDRESS$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.AddressDocument.Address)get_store().add_element_user(ADDRESS$0);
            }
            target.set(address);
        }
    }
    
    /**
     * Appends and returns a new empty "Address" element
     */
    public com.qas.www.web_2010_04.AddressDocument.Address addNewAddress()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.AddressDocument.Address target = null;
            target = (com.qas.www.web_2010_04.AddressDocument.Address)get_store().add_element_user(ADDRESS$0);
            return target;
        }
    }
    /**
     * An XML Address(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class AddressImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.AddressDocument.Address
    {
        
        public AddressImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName QAADDRESS$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAAddress");
        
        
        /**
         * Gets the "QAAddress" element
         */
        public com.qas.www.web_2010_04.QAAddressType getQAAddress()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAAddressType target = null;
                target = (com.qas.www.web_2010_04.QAAddressType)get_store().find_element_user(QAADDRESS$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "QAAddress" element
         */
        public void setQAAddress(com.qas.www.web_2010_04.QAAddressType qaAddress)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAAddressType target = null;
                target = (com.qas.www.web_2010_04.QAAddressType)get_store().find_element_user(QAADDRESS$0, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QAAddressType)get_store().add_element_user(QAADDRESS$0);
                }
                target.set(qaAddress);
            }
        }
        
        /**
         * Appends and returns a new empty "QAAddress" element
         */
        public com.qas.www.web_2010_04.QAAddressType addNewQAAddress()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAAddressType target = null;
                target = (com.qas.www.web_2010_04.QAAddressType)get_store().add_element_user(QAADDRESS$0);
                return target;
            }
        }
    }
}
