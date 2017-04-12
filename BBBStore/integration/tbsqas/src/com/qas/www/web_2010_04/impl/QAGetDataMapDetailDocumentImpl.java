/*
 * An XML document type.
 * Localname: QAGetDataMapDetail
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetDataMapDetailDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetDataMapDetail(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetDataMapDetailDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetDataMapDetailDocument
{
    
    public QAGetDataMapDetailDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETDATAMAPDETAIL$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetDataMapDetail");
    
    
    /**
     * Gets the "QAGetDataMapDetail" element
     */
    public com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail getQAGetDataMapDetail()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail target = null;
            target = (com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail)get_store().find_element_user(QAGETDATAMAPDETAIL$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetDataMapDetail" element
     */
    public void setQAGetDataMapDetail(com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail qaGetDataMapDetail)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail target = null;
            target = (com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail)get_store().find_element_user(QAGETDATAMAPDETAIL$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail)get_store().add_element_user(QAGETDATAMAPDETAIL$0);
            }
            target.set(qaGetDataMapDetail);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetDataMapDetail" element
     */
    public com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail addNewQAGetDataMapDetail()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail target = null;
            target = (com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail)get_store().add_element_user(QAGETDATAMAPDETAIL$0);
            return target;
        }
    }
    /**
     * An XML QAGetDataMapDetail(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetDataMapDetailImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetDataMapDetailDocument.QAGetDataMapDetail
    {
        
        public QAGetDataMapDetailImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName DATAMAP$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DataMap");
        
        
        /**
         * Gets the "DataMap" element
         */
        public java.lang.String getDataMap()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATAMAP$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "DataMap" element
         */
        public com.qas.www.web_2010_04.DataIDType xgetDataMap()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.DataIDType target = null;
                target = (com.qas.www.web_2010_04.DataIDType)get_store().find_element_user(DATAMAP$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "DataMap" element
         */
        public void setDataMap(java.lang.String dataMap)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATAMAP$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DATAMAP$0);
                }
                target.setStringValue(dataMap);
            }
        }
        
        /**
         * Sets (as xml) the "DataMap" element
         */
        public void xsetDataMap(com.qas.www.web_2010_04.DataIDType dataMap)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.DataIDType target = null;
                target = (com.qas.www.web_2010_04.DataIDType)get_store().find_element_user(DATAMAP$0, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.DataIDType)get_store().add_element_user(DATAMAP$0);
                }
                target.set(dataMap);
            }
        }
    }
}
