/*
 * An XML document type.
 * Localname: QAGetSystemInfo
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetSystemInfoDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetSystemInfo(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetSystemInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetSystemInfoDocument
{
    
    public QAGetSystemInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETSYSTEMINFO$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetSystemInfo");
    
    
    /**
     * Gets the "QAGetSystemInfo" element
     */
    public com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo getQAGetSystemInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo target = null;
            target = (com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo)get_store().find_element_user(QAGETSYSTEMINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetSystemInfo" element
     */
    public void setQAGetSystemInfo(com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo qaGetSystemInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo target = null;
            target = (com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo)get_store().find_element_user(QAGETSYSTEMINFO$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo)get_store().add_element_user(QAGETSYSTEMINFO$0);
            }
            target.set(qaGetSystemInfo);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetSystemInfo" element
     */
    public com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo addNewQAGetSystemInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo target = null;
            target = (com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo)get_store().add_element_user(QAGETSYSTEMINFO$0);
            return target;
        }
    }
    /**
     * An XML QAGetSystemInfo(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetSystemInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetSystemInfoDocument.QAGetSystemInfo
    {
        
        public QAGetSystemInfoImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
