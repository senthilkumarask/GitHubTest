/*
 * An XML document type.
 * Localname: QASearchOk
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QASearchOkDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QASearchOk(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QASearchOkDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QASearchOkDocument
{
    
    public QASearchOkDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QASEARCHOK$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QASearchOk");
    
    
    /**
     * Gets the "QASearchOk" element
     */
    public com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk getQASearchOk()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk target = null;
            target = (com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk)get_store().find_element_user(QASEARCHOK$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QASearchOk" element
     */
    public void setQASearchOk(com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk qaSearchOk)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk target = null;
            target = (com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk)get_store().find_element_user(QASEARCHOK$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk)get_store().add_element_user(QASEARCHOK$0);
            }
            target.set(qaSearchOk);
        }
    }
    
    /**
     * Appends and returns a new empty "QASearchOk" element
     */
    public com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk addNewQASearchOk()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk target = null;
            target = (com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk)get_store().add_element_user(QASEARCHOK$0);
            return target;
        }
    }
    /**
     * An XML QASearchOk(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QASearchOkImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk
    {
        
        public QASearchOkImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName ISOK$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "IsOk");
        private static final javax.xml.namespace.QName ERRORCODE$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ErrorCode");
        private static final javax.xml.namespace.QName ERRORMESSAGE$4 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ErrorMessage");
        private static final javax.xml.namespace.QName ERRORDETAIL$6 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ErrorDetail");
        
        
        /**
         * Gets the "IsOk" element
         */
        public boolean getIsOk()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISOK$0, 0);
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "IsOk" element
         */
        public org.apache.xmlbeans.XmlBoolean xgetIsOk()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISOK$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "IsOk" element
         */
        public void setIsOk(boolean isOk)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ISOK$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ISOK$0);
                }
                target.setBooleanValue(isOk);
            }
        }
        
        /**
         * Sets (as xml) the "IsOk" element
         */
        public void xsetIsOk(org.apache.xmlbeans.XmlBoolean isOk)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(ISOK$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(ISOK$0);
                }
                target.set(isOk);
            }
        }
        
        /**
         * Gets the "ErrorCode" element
         */
        public java.lang.String getErrorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORCODE$2, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORCODE$2, 0);
                return target;
            }
        }
        
        /**
         * True if has "ErrorCode" element
         */
        public boolean isSetErrorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ERRORCODE$2) != 0;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ERRORCODE$2);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORCODE$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ERRORCODE$2);
                }
                target.set(errorCode);
            }
        }
        
        /**
         * Unsets the "ErrorCode" element
         */
        public void unsetErrorCode()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ERRORCODE$2, 0);
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORMESSAGE$4, 0);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORMESSAGE$4, 0);
                return target;
            }
        }
        
        /**
         * True if has "ErrorMessage" element
         */
        public boolean isSetErrorMessage()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ERRORMESSAGE$4) != 0;
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORMESSAGE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ERRORMESSAGE$4);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORMESSAGE$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ERRORMESSAGE$4);
                }
                target.set(errorMessage);
            }
        }
        
        /**
         * Unsets the "ErrorMessage" element
         */
        public void unsetErrorMessage()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ERRORMESSAGE$4, 0);
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
                get_store().find_all_element_users(ERRORDETAIL$6, targetList);
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORDETAIL$6, i);
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
                get_store().find_all_element_users(ERRORDETAIL$6, targetList);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORDETAIL$6, i);
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
                return get_store().count_elements(ERRORDETAIL$6);
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
                arraySetterHelper(errorDetailArray, ERRORDETAIL$6);
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ERRORDETAIL$6, i);
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
                arraySetterHelper(errorDetailArray, ERRORDETAIL$6);
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
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ERRORDETAIL$6, i);
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
                    (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(ERRORDETAIL$6, i);
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
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ERRORDETAIL$6);
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
                target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(ERRORDETAIL$6, i);
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
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ERRORDETAIL$6);
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
                get_store().remove_element(ERRORDETAIL$6, i);
            }
        }
    }
}
