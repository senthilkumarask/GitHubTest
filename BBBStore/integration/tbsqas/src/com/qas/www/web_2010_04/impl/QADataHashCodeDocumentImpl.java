/*
 * An XML document type.
 * Localname: QADataHashCode
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QADataHashCodeDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QADataHashCode(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QADataHashCodeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QADataHashCodeDocument
{
    
    public QADataHashCodeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QADATAHASHCODE$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QADataHashCode");
    
    
    /**
     * Gets the "QADataHashCode" element
     */
    public com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode getQADataHashCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode target = null;
            target = (com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode)get_store().find_element_user(QADATAHASHCODE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QADataHashCode" element
     */
    public void setQADataHashCode(com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode qaDataHashCode)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode target = null;
            target = (com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode)get_store().find_element_user(QADATAHASHCODE$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode)get_store().add_element_user(QADATAHASHCODE$0);
            }
            target.set(qaDataHashCode);
        }
    }
    
    /**
     * Appends and returns a new empty "QADataHashCode" element
     */
    public com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode addNewQADataHashCode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode target = null;
            target = (com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode)get_store().add_element_user(QADATAHASHCODE$0);
            return target;
        }
    }
    /**
     * An XML QADataHashCode(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QADataHashCodeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode
    {
        
        public QADataHashCodeImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName DATAHASHCODE$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DataHashCode");
        
        
        /**
         * Gets the "DataHashCode" element
         */
        public java.lang.String getDataHashCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATAHASHCODE$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "DataHashCode" element
         */
        public org.apache.xmlbeans.XmlString xgetDataHashCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATAHASHCODE$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "DataHashCode" element
         */
        public void setDataHashCode(java.lang.String dataHashCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATAHASHCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DATAHASHCODE$0);
                }
                target.setStringValue(dataHashCode);
            }
        }
        
        /**
         * Sets (as xml) the "DataHashCode" element
         */
        public void xsetDataHashCode(org.apache.xmlbeans.XmlString dataHashCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DATAHASHCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DATAHASHCODE$0);
                }
                target.set(dataHashCode);
            }
        }
    }
}
