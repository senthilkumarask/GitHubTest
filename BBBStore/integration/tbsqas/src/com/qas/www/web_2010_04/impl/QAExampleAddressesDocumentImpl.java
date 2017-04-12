/*
 * An XML document type.
 * Localname: QAExampleAddresses
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAExampleAddressesDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAExampleAddresses(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAExampleAddressesDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAExampleAddressesDocument
{
    
    public QAExampleAddressesDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAEXAMPLEADDRESSES$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAExampleAddresses");
    
    
    /**
     * Gets the "QAExampleAddresses" element
     */
    public com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses getQAExampleAddresses()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses target = null;
            target = (com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses)get_store().find_element_user(QAEXAMPLEADDRESSES$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAExampleAddresses" element
     */
    public void setQAExampleAddresses(com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses qaExampleAddresses)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses target = null;
            target = (com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses)get_store().find_element_user(QAEXAMPLEADDRESSES$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses)get_store().add_element_user(QAEXAMPLEADDRESSES$0);
            }
            target.set(qaExampleAddresses);
        }
    }
    
    /**
     * Appends and returns a new empty "QAExampleAddresses" element
     */
    public com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses addNewQAExampleAddresses()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses target = null;
            target = (com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses)get_store().add_element_user(QAEXAMPLEADDRESSES$0);
            return target;
        }
    }
    /**
     * An XML QAExampleAddresses(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAExampleAddressesImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAExampleAddressesDocument.QAExampleAddresses
    {
        
        public QAExampleAddressesImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName EXAMPLEADDRESS$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ExampleAddress");
        
        
        /**
         * Gets array of all "ExampleAddress" elements
         */
        public com.qas.www.web_2010_04.QAExampleAddress[] getExampleAddressArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(EXAMPLEADDRESS$0, targetList);
                com.qas.www.web_2010_04.QAExampleAddress[] result = new com.qas.www.web_2010_04.QAExampleAddress[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets ith "ExampleAddress" element
         */
        public com.qas.www.web_2010_04.QAExampleAddress getExampleAddressArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAExampleAddress target = null;
                target = (com.qas.www.web_2010_04.QAExampleAddress)get_store().find_element_user(EXAMPLEADDRESS$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        /**
         * Returns number of "ExampleAddress" element
         */
        public int sizeOfExampleAddressArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(EXAMPLEADDRESS$0);
            }
        }
        
        /**
         * Sets array of all "ExampleAddress" element
         */
        public void setExampleAddressArray(com.qas.www.web_2010_04.QAExampleAddress[] exampleAddressArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(exampleAddressArray, EXAMPLEADDRESS$0);
            }
        }
        
        /**
         * Sets ith "ExampleAddress" element
         */
        public void setExampleAddressArray(int i, com.qas.www.web_2010_04.QAExampleAddress exampleAddress)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAExampleAddress target = null;
                target = (com.qas.www.web_2010_04.QAExampleAddress)get_store().find_element_user(EXAMPLEADDRESS$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(exampleAddress);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "ExampleAddress" element
         */
        public com.qas.www.web_2010_04.QAExampleAddress insertNewExampleAddress(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAExampleAddress target = null;
                target = (com.qas.www.web_2010_04.QAExampleAddress)get_store().insert_element_user(EXAMPLEADDRESS$0, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "ExampleAddress" element
         */
        public com.qas.www.web_2010_04.QAExampleAddress addNewExampleAddress()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAExampleAddress target = null;
                target = (com.qas.www.web_2010_04.QAExampleAddress)get_store().add_element_user(EXAMPLEADDRESS$0);
                return target;
            }
        }
        
        /**
         * Removes the ith "ExampleAddress" element
         */
        public void removeExampleAddress(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(EXAMPLEADDRESS$0, i);
            }
        }
    }
}
