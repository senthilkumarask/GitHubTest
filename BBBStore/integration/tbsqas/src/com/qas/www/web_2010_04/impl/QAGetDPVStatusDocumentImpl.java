/*
 * An XML document type.
 * Localname: QAGetDPVStatus
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetDPVStatusDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetDPVStatus(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetDPVStatusDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetDPVStatusDocument
{
    
    public QAGetDPVStatusDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETDPVSTATUS$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetDPVStatus");
    
    
    /**
     * Gets the "QAGetDPVStatus" element
     */
    public com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus getQAGetDPVStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus target = null;
            target = (com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus)get_store().find_element_user(QAGETDPVSTATUS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetDPVStatus" element
     */
    public void setQAGetDPVStatus(com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus qaGetDPVStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus target = null;
            target = (com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus)get_store().find_element_user(QAGETDPVSTATUS$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus)get_store().add_element_user(QAGETDPVSTATUS$0);
            }
            target.set(qaGetDPVStatus);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetDPVStatus" element
     */
    public com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus addNewQAGetDPVStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus target = null;
            target = (com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus)get_store().add_element_user(QAGETDPVSTATUS$0);
            return target;
        }
    }
    /**
     * An XML QAGetDPVStatus(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetDPVStatusImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetDPVStatusDocument.QAGetDPVStatus
    {
        
        public QAGetDPVStatusImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
