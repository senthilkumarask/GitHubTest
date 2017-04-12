/*
 * An XML document type.
 * Localname: QAGetDataHashCode
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetDataHashCodeDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetDataHashCode(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetDataHashCodeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetDataHashCodeDocument
{
    
    public QAGetDataHashCodeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETDATAHASHCODE$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetDataHashCode");
    
    
    /**
     * Gets the "QAGetDataHashCode" element
     */
    public com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode getQAGetDataHashCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode target = null;
            target = (com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode)get_store().find_element_user(QAGETDATAHASHCODE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetDataHashCode" element
     */
    public void setQAGetDataHashCode(com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode qaGetDataHashCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode target = null;
            target = (com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode)get_store().find_element_user(QAGETDATAHASHCODE$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode)get_store().add_element_user(QAGETDATAHASHCODE$0);
            }
            target.set(qaGetDataHashCode);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetDataHashCode" element
     */
    public com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode addNewQAGetDataHashCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode target = null;
            target = (com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode)get_store().add_element_user(QAGETDATAHASHCODE$0);
            return target;
        }
    }
    /**
     * An XML QAGetDataHashCode(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetDataHashCodeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetDataHashCodeDocument.QAGetDataHashCode
    {
        
        public QAGetDataHashCodeImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
