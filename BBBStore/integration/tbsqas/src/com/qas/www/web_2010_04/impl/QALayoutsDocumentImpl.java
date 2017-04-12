/*
 * An XML document type.
 * Localname: QALayouts
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QALayoutsDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QALayouts(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QALayoutsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QALayoutsDocument
{
    
    public QALayoutsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QALAYOUTS$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QALayouts");
    
    
    /**
     * Gets the "QALayouts" element
     */
    public com.qas.www.web_2010_04.QALayoutsDocument.QALayouts getQALayouts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QALayoutsDocument.QALayouts target = null;
            target = (com.qas.www.web_2010_04.QALayoutsDocument.QALayouts)get_store().find_element_user(QALAYOUTS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QALayouts" element
     */
    public void setQALayouts(com.qas.www.web_2010_04.QALayoutsDocument.QALayouts qaLayouts)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QALayoutsDocument.QALayouts target = null;
            target = (com.qas.www.web_2010_04.QALayoutsDocument.QALayouts)get_store().find_element_user(QALAYOUTS$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QALayoutsDocument.QALayouts)get_store().add_element_user(QALAYOUTS$0);
            }
            target.set(qaLayouts);
        }
    }
    
    /**
     * Appends and returns a new empty "QALayouts" element
     */
    public com.qas.www.web_2010_04.QALayoutsDocument.QALayouts addNewQALayouts()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QALayoutsDocument.QALayouts target = null;
            target = (com.qas.www.web_2010_04.QALayoutsDocument.QALayouts)get_store().add_element_user(QALAYOUTS$0);
            return target;
        }
    }
    /**
     * An XML QALayouts(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QALayoutsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QALayoutsDocument.QALayouts
    {
        
        public QALayoutsImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName LAYOUT$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Layout");
        
        
        /**
         * Gets array of all "Layout" elements
         */
        public com.qas.www.web_2010_04.QALayout[] getLayoutArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(LAYOUT$0, targetList);
                com.qas.www.web_2010_04.QALayout[] result = new com.qas.www.web_2010_04.QALayout[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets ith "Layout" element
         */
        public com.qas.www.web_2010_04.QALayout getLayoutArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QALayout target = null;
                target = (com.qas.www.web_2010_04.QALayout)get_store().find_element_user(LAYOUT$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        /**
         * Returns number of "Layout" element
         */
        public int sizeOfLayoutArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(LAYOUT$0);
            }
        }
        
        /**
         * Sets array of all "Layout" element
         */
        public void setLayoutArray(com.qas.www.web_2010_04.QALayout[] layoutArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(layoutArray, LAYOUT$0);
            }
        }
        
        /**
         * Sets ith "Layout" element
         */
        public void setLayoutArray(int i, com.qas.www.web_2010_04.QALayout layout)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QALayout target = null;
                target = (com.qas.www.web_2010_04.QALayout)get_store().find_element_user(LAYOUT$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(layout);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "Layout" element
         */
        public com.qas.www.web_2010_04.QALayout insertNewLayout(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QALayout target = null;
                target = (com.qas.www.web_2010_04.QALayout)get_store().insert_element_user(LAYOUT$0, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "Layout" element
         */
        public com.qas.www.web_2010_04.QALayout addNewLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QALayout target = null;
                target = (com.qas.www.web_2010_04.QALayout)get_store().add_element_user(LAYOUT$0);
                return target;
            }
        }
        
        /**
         * Removes the ith "Layout" element
         */
        public void removeLayout(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(LAYOUT$0, i);
            }
        }
    }
}
