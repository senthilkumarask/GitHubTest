/*
 * An XML document type.
 * Localname: QAFault
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAFaultDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAFault(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAFaultDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAFaultDocument
{
    
    public QAFaultDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAFAULT$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAFault");
    
    
    /**
     * Gets the "QAFault" element
     */
    public com.qas.www.web_2010_04.QAFaultDocument.QAFault getQAFault()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAFaultDocument.QAFault target = null;
            target = (com.qas.www.web_2010_04.QAFaultDocument.QAFault)get_store().find_element_user(QAFAULT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAFault" element
     */
    public void setQAFault(com.qas.www.web_2010_04.QAFaultDocument.QAFault qaFault)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAFaultDocument.QAFault target = null;
            target = (com.qas.www.web_2010_04.QAFaultDocument.QAFault)get_store().find_element_user(QAFAULT$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAFaultDocument.QAFault)get_store().add_element_user(QAFAULT$0);
            }
            target.set(qaFault);
        }
    }
    
    /**
     * Appends and returns a new empty "QAFault" element
     */
    public com.qas.www.web_2010_04.QAFaultDocument.QAFault addNewQAFault()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAFaultDocument.QAFault target = null;
            target = (com.qas.www.web_2010_04.QAFaultDocument.QAFault)get_store().add_element_user(QAFAULT$0);
            return target;
        }
    }
    /**
     * An XML QAFault(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAFaultImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAFaultDocument.QAFault
    {
        
        public QAFaultImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName ERRORCODE$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ErrorCode");
        private static final javax.xml.namespace.QName ERRORMESSAGE$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ErrorMessage");
        private static final javax.xml.namespace.QName ERRORDETAIL$4 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ErrorDetail");
        
        
        /**
         * Gets the "ErrorCode" element
         */
        public java.lang.String getErrorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORCODE$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "ErrorCode" element
         */
        public org.apache.xmlbeans.XmlString xgetErrorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORCODE$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "ErrorCode" element
         */
        public void setErrorCode(java.lang.String errorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ERRORCODE$0);
                }
                target.setStringValue(errorCode);
            }
        }
        
        /**
         * Sets (as xml) the "ErrorCode" element
         */
        public void xsetErrorCode(org.apache.xmlbeans.XmlString errorCode)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORCODE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ERRORCODE$0);
                }
                target.set(errorCode);
            }
        }
        
        /**
         * Gets the "ErrorMessage" element
         */
        public java.lang.String getErrorMessage()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORMESSAGE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "ErrorMessage" element
         */
        public org.apache.xmlbeans.XmlString xgetErrorMessage()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORMESSAGE$2, 0);
                return target;
            }
        }
        
        /**
         * Sets the "ErrorMessage" element
         */
        public void setErrorMessage(java.lang.String errorMessage)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORMESSAGE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ERRORMESSAGE$2);
                }
                target.setStringValue(errorMessage);
            }
        }
        
        /**
         * Sets (as xml) the "ErrorMessage" element
         */
        public void xsetErrorMessage(org.apache.xmlbeans.XmlString errorMessage)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORMESSAGE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ERRORMESSAGE$2);
                }
                target.set(errorMessage);
            }
        }
        
        /**
         * Gets array of all "ErrorDetail" elements
         */
        public java.lang.String[] getErrorDetailArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(ERRORDETAIL$4, targetList);
                java.lang.String[] result = new java.lang.String[targetList.size()];
                for (int i = 0, len = targetList.size() ; i < len ; i++)
                    result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getStringValue();
                return result;
            }
        }
        
        /**
         * Gets ith "ErrorDetail" element
         */
        public java.lang.String getErrorDetailArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORDETAIL$4, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) array of all "ErrorDetail" elements
         */
        public org.apache.xmlbeans.XmlString[] xgetErrorDetailArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(ERRORDETAIL$4, targetList);
                org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets (as xml) ith "ErrorDetail" element
         */
        public org.apache.xmlbeans.XmlString xgetErrorDetailArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORDETAIL$4, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return (org.apache.xmlbeans.XmlString)target;
            }
        }
        
        /**
         * Returns number of "ErrorDetail" element
         */
        public int sizeOfErrorDetailArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ERRORDETAIL$4);
            }
        }
        
        /**
         * Sets array of all "ErrorDetail" element
         */
        public void setErrorDetailArray(java.lang.String[] errorDetailArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(errorDetailArray, ERRORDETAIL$4);
            }
        }
        
        /**
         * Sets ith "ErrorDetail" element
         */
        public void setErrorDetailArray(int i, java.lang.String errorDetail)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORDETAIL$4, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.setStringValue(errorDetail);
            }
        }
        
        /**
         * Sets (as xml) array of all "ErrorDetail" element
         */
        public void xsetErrorDetailArray(org.apache.xmlbeans.XmlString[]errorDetailArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(errorDetailArray, ERRORDETAIL$4);
            }
        }
        
        /**
         * Sets (as xml) ith "ErrorDetail" element
         */
        public void xsetErrorDetailArray(int i, org.apache.xmlbeans.XmlString errorDetail)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORDETAIL$4, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(errorDetail);
            }
        }
        
        /**
         * Inserts the value as the ith "ErrorDetail" element
         */
        public void insertErrorDetail(int i, java.lang.String errorDetail)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = 
                    (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(ERRORDETAIL$4, i);
                target.setStringValue(errorDetail);
            }
        }
        
        /**
         * Appends the value as the last "ErrorDetail" element
         */
        public void addErrorDetail(java.lang.String errorDetail)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ERRORDETAIL$4);
                target.setStringValue(errorDetail);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "ErrorDetail" element
         */
        public org.apache.xmlbeans.XmlString insertNewErrorDetail(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(ERRORDETAIL$4, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "ErrorDetail" element
         */
        public org.apache.xmlbeans.XmlString addNewErrorDetail()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ERRORDETAIL$4);
                return target;
            }
        }
        
        /**
         * Removes the ith "ErrorDetail" element
         */
        public void removeErrorDetail(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ERRORDETAIL$4, i);
            }
        }
    }
}
