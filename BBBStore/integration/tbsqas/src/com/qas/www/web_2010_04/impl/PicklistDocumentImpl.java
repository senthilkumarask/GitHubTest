/*
 * An XML document type.
 * Localname: Picklist
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.PicklistDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one Picklist(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class PicklistDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.PicklistDocument
{
    
    public PicklistDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PICKLIST$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Picklist");
    
    
    /**
     * Gets the "Picklist" element
     */
    public com.qas.www.web_2010_04.PicklistDocument.Picklist getPicklist()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.PicklistDocument.Picklist target = null;
            target = (com.qas.www.web_2010_04.PicklistDocument.Picklist)get_store().find_element_user(PICKLIST$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "Picklist" element
     */
    public void setPicklist(com.qas.www.web_2010_04.PicklistDocument.Picklist picklist)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.PicklistDocument.Picklist target = null;
            target = (com.qas.www.web_2010_04.PicklistDocument.Picklist)get_store().find_element_user(PICKLIST$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.PicklistDocument.Picklist)get_store().add_element_user(PICKLIST$0);
            }
            target.set(picklist);
        }
    }
    
    /**
     * Appends and returns a new empty "Picklist" element
     */
    public com.qas.www.web_2010_04.PicklistDocument.Picklist addNewPicklist()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.PicklistDocument.Picklist target = null;
            target = (com.qas.www.web_2010_04.PicklistDocument.Picklist)get_store().add_element_user(PICKLIST$0);
            return target;
        }
    }
    /**
     * An XML Picklist(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class PicklistImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.PicklistDocument.Picklist
    {
        
        public PicklistImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName QAPICKLIST$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAPicklist");
        
        
        /**
         * Gets the "QAPicklist" element
         */
        public com.qas.www.web_2010_04.QAPicklistType getQAPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAPicklistType target = null;
                target = (com.qas.www.web_2010_04.QAPicklistType)get_store().find_element_user(QAPICKLIST$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "QAPicklist" element
         */
        public void setQAPicklist(com.qas.www.web_2010_04.QAPicklistType qaPicklist)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAPicklistType target = null;
                target = (com.qas.www.web_2010_04.QAPicklistType)get_store().find_element_user(QAPICKLIST$0, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QAPicklistType)get_store().add_element_user(QAPICKLIST$0);
                }
                target.set(qaPicklist);
            }
        }
        
        /**
         * Appends and returns a new empty "QAPicklist" element
         */
        public com.qas.www.web_2010_04.QAPicklistType addNewQAPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAPicklistType target = null;
                target = (com.qas.www.web_2010_04.QAPicklistType)get_store().add_element_user(QAPICKLIST$0);
                return target;
            }
        }
    }
}
