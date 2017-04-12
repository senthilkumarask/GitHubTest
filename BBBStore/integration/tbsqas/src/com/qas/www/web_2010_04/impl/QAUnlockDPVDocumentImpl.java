/*
 * An XML document type.
 * Localname: QAUnlockDPV
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAUnlockDPVDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAUnlockDPV(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAUnlockDPVDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAUnlockDPVDocument
{
    
    public QAUnlockDPVDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAUNLOCKDPV$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAUnlockDPV");
    
    
    /**
     * Gets the "QAUnlockDPV" element
     */
    public com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV getQAUnlockDPV()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV target = null;
            target = (com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV)get_store().find_element_user(QAUNLOCKDPV$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAUnlockDPV" element
     */
    public void setQAUnlockDPV(com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV qaUnlockDPV)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV target = null;
            target = (com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV)get_store().find_element_user(QAUNLOCKDPV$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV)get_store().add_element_user(QAUNLOCKDPV$0);
            }
            target.set(qaUnlockDPV);
        }
    }
    
    /**
     * Appends and returns a new empty "QAUnlockDPV" element
     */
    public com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV addNewQAUnlockDPV()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV target = null;
            target = (com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV)get_store().add_element_user(QAUNLOCKDPV$0);
            return target;
        }
    }
    /**
     * An XML QAUnlockDPV(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAUnlockDPVImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAUnlockDPVDocument.QAUnlockDPV
    {
        
        public QAUnlockDPVImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName UNLOCKCODE$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "UnlockCode");
        
        
        /**
         * Gets the "UnlockCode" element
         */
        public java.lang.String getUnlockCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNLOCKCODE$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "UnlockCode" element
         */
        public org.apache.xmlbeans.XmlString xgetUnlockCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNLOCKCODE$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "UnlockCode" element
         */
        public void setUnlockCode(java.lang.String unlockCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNLOCKCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(UNLOCKCODE$0);
                }
                target.setStringValue(unlockCode);
            }
        }
        
        /**
         * Sets (as xml) the "UnlockCode" element
         */
        public void xsetUnlockCode(org.apache.xmlbeans.XmlString unlockCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(UNLOCKCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(UNLOCKCODE$0);
                }
                target.set(unlockCode);
            }
        }
    }
}
