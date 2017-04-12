/*
 * An XML document type.
 * Localname: QAPromptSet
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAPromptSetDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAPromptSet(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAPromptSetDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAPromptSetDocument
{
    
    public QAPromptSetDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAPROMPTSET$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAPromptSet");
    
    
    /**
     * Gets the "QAPromptSet" element
     */
    public com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet getQAPromptSet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet target = null;
            target = (com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet)get_store().find_element_user(QAPROMPTSET$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAPromptSet" element
     */
    public void setQAPromptSet(com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet qaPromptSet)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet target = null;
            target = (com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet)get_store().find_element_user(QAPROMPTSET$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet)get_store().add_element_user(QAPROMPTSET$0);
            }
            target.set(qaPromptSet);
        }
    }
    
    /**
     * Appends and returns a new empty "QAPromptSet" element
     */
    public com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet addNewQAPromptSet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet target = null;
            target = (com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet)get_store().add_element_user(QAPROMPTSET$0);
            return target;
        }
    }
    /**
     * An XML QAPromptSet(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAPromptSetImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAPromptSetDocument.QAPromptSet
    {
        
        public QAPromptSetImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName LINE$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Line");
        private static final javax.xml.namespace.QName DYNAMIC$2 = 
            new javax.xml.namespace.QName("", "Dynamic");
        
        
        /**
         * Gets array of all "Line" elements
         */
        public com.qas.www.web_2010_04.PromptLine[] getLineArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(LINE$0, targetList);
                com.qas.www.web_2010_04.PromptLine[] result = new com.qas.www.web_2010_04.PromptLine[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets ith "Line" element
         */
        public com.qas.www.web_2010_04.PromptLine getLineArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.PromptLine target = null;
                target = (com.qas.www.web_2010_04.PromptLine)get_store().find_element_user(LINE$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        /**
         * Returns number of "Line" element
         */
        public int sizeOfLineArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(LINE$0);
            }
        }
        
        /**
         * Sets array of all "Line" element
         */
        public void setLineArray(com.qas.www.web_2010_04.PromptLine[] lineArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(lineArray, LINE$0);
            }
        }
        
        /**
         * Sets ith "Line" element
         */
        public void setLineArray(int i, com.qas.www.web_2010_04.PromptLine line)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.PromptLine target = null;
                target = (com.qas.www.web_2010_04.PromptLine)get_store().find_element_user(LINE$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(line);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "Line" element
         */
        public com.qas.www.web_2010_04.PromptLine insertNewLine(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.PromptLine target = null;
                target = (com.qas.www.web_2010_04.PromptLine)get_store().insert_element_user(LINE$0, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "Line" element
         */
        public com.qas.www.web_2010_04.PromptLine addNewLine()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.PromptLine target = null;
                target = (com.qas.www.web_2010_04.PromptLine)get_store().add_element_user(LINE$0);
                return target;
            }
        }
        
        /**
         * Removes the ith "Line" element
         */
        public void removeLine(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(LINE$0, i);
            }
        }
        
        /**
         * Gets the "Dynamic" attribute
         */
        public boolean getDynamic()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DYNAMIC$2);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(DYNAMIC$2);
                }
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "Dynamic" attribute
         */
        public org.apache.xmlbeans.XmlBoolean xgetDynamic()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(DYNAMIC$2);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_default_attribute_value(DYNAMIC$2);
                }
                return target;
            }
        }
        
        /**
         * True if has "Dynamic" attribute
         */
        public boolean isSetDynamic()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().find_attribute_user(DYNAMIC$2) != null;
            }
        }
        
        /**
         * Sets the "Dynamic" attribute
         */
        public void setDynamic(boolean dynamic)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DYNAMIC$2);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(DYNAMIC$2);
                }
                target.setBooleanValue(dynamic);
            }
        }
        
        /**
         * Sets (as xml) the "Dynamic" attribute
         */
        public void xsetDynamic(org.apache.xmlbeans.XmlBoolean dynamic)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(DYNAMIC$2);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(DYNAMIC$2);
                }
                target.set(dynamic);
            }
        }
        
        /**
         * Unsets the "Dynamic" attribute
         */
        public void unsetDynamic()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_attribute(DYNAMIC$2);
            }
        }
    }
}
