/*
 * An XML document type.
 * Localname: QAUnlockDPVOk
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAUnlockDPVOkDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAUnlockDPVOk(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAUnlockDPVOkDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAUnlockDPVOkDocument
{
    
    public QAUnlockDPVOkDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAUNLOCKDPVOK$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAUnlockDPVOk");
    
    
    /**
     * Gets the "QAUnlockDPVOk" element
     */
    public com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk getQAUnlockDPVOk()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk target = null;
            target = (com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk)get_store().find_element_user(QAUNLOCKDPVOK$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAUnlockDPVOk" element
     */
    public void setQAUnlockDPVOk(com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk qaUnlockDPVOk)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk target = null;
            target = (com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk)get_store().find_element_user(QAUNLOCKDPVOK$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk)get_store().add_element_user(QAUNLOCKDPVOK$0);
            }
            target.set(qaUnlockDPVOk);
        }
    }
    
    /**
     * Appends and returns a new empty "QAUnlockDPVOk" element
     */
    public com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk addNewQAUnlockDPVOk()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk target = null;
            target = (com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk)get_store().add_element_user(QAUNLOCKDPVOK$0);
            return target;
        }
    }
    /**
     * An XML QAUnlockDPVOk(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAUnlockDPVOkImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk
    {
        
        public QAUnlockDPVOkImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName UNLOCKCODEOK$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "UnlockCodeOk");
        private static final javax.xml.namespace.QName ERRORCODE$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ErrorCode");
        private static final javax.xml.namespace.QName ERRORMESSAGE$4 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ErrorMessage");
        
        
        /**
         * Gets the "UnlockCodeOk" element
         */
        public boolean getUnlockCodeOk()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNLOCKCODEOK$0, 0);
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "UnlockCodeOk" element
         */
        public org.apache.xmlbeans.XmlBoolean xgetUnlockCodeOk()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(UNLOCKCODEOK$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "UnlockCodeOk" element
         */
        public void setUnlockCodeOk(boolean unlockCodeOk)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(UNLOCKCODEOK$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(UNLOCKCODEOK$0);
                }
                target.setBooleanValue(unlockCodeOk);
            }
        }
        
        /**
         * Sets (as xml) the "UnlockCodeOk" element
         */
        public void xsetUnlockCodeOk(org.apache.xmlbeans.XmlBoolean unlockCodeOk)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(UNLOCKCODEOK$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(UNLOCKCODEOK$0);
                }
                target.set(unlockCodeOk);
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
    }
}
