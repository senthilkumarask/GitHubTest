/*
 * An XML document type.
 * Localname: QAData
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QADataDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAData(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QADataDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QADataDocument
{
    
    public QADataDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QADATA$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAData");
    
    
    /**
     * Gets the "QAData" element
     */
    public com.qas.www.web_2010_04.QADataDocument.QAData getQAData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QADataDocument.QAData target = null;
            target = (com.qas.www.web_2010_04.QADataDocument.QAData)get_store().find_element_user(QADATA$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAData" element
     */
    public void setQAData(com.qas.www.web_2010_04.QADataDocument.QAData qaData)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QADataDocument.QAData target = null;
            target = (com.qas.www.web_2010_04.QADataDocument.QAData)get_store().find_element_user(QADATA$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QADataDocument.QAData)get_store().add_element_user(QADATA$0);
            }
            target.set(qaData);
        }
    }
    
    /**
     * Appends and returns a new empty "QAData" element
     */
    public com.qas.www.web_2010_04.QADataDocument.QAData addNewQAData()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QADataDocument.QAData target = null;
            target = (com.qas.www.web_2010_04.QADataDocument.QAData)get_store().add_element_user(QADATA$0);
            return target;
        }
    }
    /**
     * An XML QAData(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QADataImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QADataDocument.QAData
    {
        
        public QADataImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName DATASET$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DataSet");
        
        
        /**
         * Gets array of all "DataSet" elements
         */
        public com.qas.www.web_2010_04.QADataSet[] getDataSetArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(DATASET$0, targetList);
                com.qas.www.web_2010_04.QADataSet[] result = new com.qas.www.web_2010_04.QADataSet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets ith "DataSet" element
         */
        public com.qas.www.web_2010_04.QADataSet getDataSetArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QADataSet target = null;
                target = (com.qas.www.web_2010_04.QADataSet)get_store().find_element_user(DATASET$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        /**
         * Returns number of "DataSet" element
         */
        public int sizeOfDataSetArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DATASET$0);
            }
        }
        
        /**
         * Sets array of all "DataSet" element
         */
        public void setDataSetArray(com.qas.www.web_2010_04.QADataSet[] dataSetArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(dataSetArray, DATASET$0);
            }
        }
        
        /**
         * Sets ith "DataSet" element
         */
        public void setDataSetArray(int i, com.qas.www.web_2010_04.QADataSet dataSet)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QADataSet target = null;
                target = (com.qas.www.web_2010_04.QADataSet)get_store().find_element_user(DATASET$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(dataSet);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "DataSet" element
         */
        public com.qas.www.web_2010_04.QADataSet insertNewDataSet(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QADataSet target = null;
                target = (com.qas.www.web_2010_04.QADataSet)get_store().insert_element_user(DATASET$0, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "DataSet" element
         */
        public com.qas.www.web_2010_04.QADataSet addNewDataSet()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QADataSet target = null;
                target = (com.qas.www.web_2010_04.QADataSet)get_store().add_element_user(DATASET$0);
                return target;
            }
        }
        
        /**
         * Removes the ith "DataSet" element
         */
        public void removeDataSet(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DATASET$0, i);
            }
        }
    }
}
