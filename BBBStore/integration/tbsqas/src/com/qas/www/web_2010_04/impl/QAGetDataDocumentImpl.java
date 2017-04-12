/*
 * An XML document type.
 * Localname: QAGetData
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetDataDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetData(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetDataDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetDataDocument
{
    
    public QAGetDataDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETDATA$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetData");
    
    
    /**
     * Gets the "QAGetData" element
     */
    public com.qas.www.web_2010_04.QAGetDataDocument.QAGetData getQAGetData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDataDocument.QAGetData target = null;
            target = (com.qas.www.web_2010_04.QAGetDataDocument.QAGetData)get_store().find_element_user(QAGETDATA$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetData" element
     */
    public void setQAGetData(com.qas.www.web_2010_04.QAGetDataDocument.QAGetData qaGetData)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDataDocument.QAGetData target = null;
            target = (com.qas.www.web_2010_04.QAGetDataDocument.QAGetData)get_store().find_element_user(QAGETDATA$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetDataDocument.QAGetData)get_store().add_element_user(QAGETDATA$0);
            }
            target.set(qaGetData);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetData" element
     */
    public com.qas.www.web_2010_04.QAGetDataDocument.QAGetData addNewQAGetData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDataDocument.QAGetData target = null;
            target = (com.qas.www.web_2010_04.QAGetDataDocument.QAGetData)get_store().add_element_user(QAGETDATA$0);
            return target;
        }
    }
    /**
     * An XML QAGetData(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetDataImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetDataDocument.QAGetData
    {
        
        public QAGetDataImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
