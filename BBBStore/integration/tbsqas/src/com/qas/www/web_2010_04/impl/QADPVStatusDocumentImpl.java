/*
 * An XML document type.
 * Localname: QADPVStatus
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QADPVStatusDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QADPVStatus(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QADPVStatusDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QADPVStatusDocument
{
    
    public QADPVStatusDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QADPVSTATUS$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QADPVStatus");
    
    
    /**
     * Gets the "QADPVStatus" element
     */
    public com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus getQADPVStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus target = null;
            target = (com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus)get_store().find_element_user(QADPVSTATUS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QADPVStatus" element
     */
    public void setQADPVStatus(com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus qadpvStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus target = null;
            target = (com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus)get_store().find_element_user(QADPVSTATUS$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus)get_store().add_element_user(QADPVSTATUS$0);
            }
            target.set(qadpvStatus);
        }
    }
    
    /**
     * Appends and returns a new empty "QADPVStatus" element
     */
    public com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus addNewQADPVStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus target = null;
            target = (com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus)get_store().add_element_user(QADPVSTATUS$0);
            return target;
        }
    }
    /**
     * An XML QADPVStatus(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QADPVStatusImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QADPVStatusDocument.QADPVStatus
    {
        
        public QADPVStatusImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName QADPVLOCKDETAILS$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QADPVLockDetails");
        private static final javax.xml.namespace.QName DPVRETURNEDSTATUS$2 = 
            new javax.xml.namespace.QName("", "DPVReturnedStatus");
        
        
        /**
         * Gets the "QADPVLockDetails" element
         */
        public com.qas.www.web_2010_04.QADPVLockDetailsType getQADPVLockDetails()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QADPVLockDetailsType target = null;
                target = (com.qas.www.web_2010_04.QADPVLockDetailsType)get_store().find_element_user(QADPVLOCKDETAILS$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "QADPVLockDetails" element
         */
        public boolean isSetQADPVLockDetails()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(QADPVLOCKDETAILS$0) != 0;
            }
        }
        
        /**
         * Sets the "QADPVLockDetails" element
         */
        public void setQADPVLockDetails(com.qas.www.web_2010_04.QADPVLockDetailsType qadpvLockDetails)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QADPVLockDetailsType target = null;
                target = (com.qas.www.web_2010_04.QADPVLockDetailsType)get_store().find_element_user(QADPVLOCKDETAILS$0, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QADPVLockDetailsType)get_store().add_element_user(QADPVLOCKDETAILS$0);
                }
                target.set(qadpvLockDetails);
            }
        }
        
        /**
         * Appends and returns a new empty "QADPVLockDetails" element
         */
        public com.qas.www.web_2010_04.QADPVLockDetailsType addNewQADPVLockDetails()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QADPVLockDetailsType target = null;
                target = (com.qas.www.web_2010_04.QADPVLockDetailsType)get_store().add_element_user(QADPVLOCKDETAILS$0);
                return target;
            }
        }
        
        /**
         * Unsets the "QADPVLockDetails" element
         */
        public void unsetQADPVLockDetails()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(QADPVLOCKDETAILS$0, 0);
            }
        }
        
        /**
         * Gets the "DPVReturnedStatus" attribute
         */
        public com.qas.www.web_2010_04.DPVStatusType.Enum getDPVReturnedStatus()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DPVRETURNEDSTATUS$2);
                if (target == null)
                {
                    return null;
                }
                return (com.qas.www.web_2010_04.DPVStatusType.Enum)target.getEnumValue();
            }
        }
        
        /**
         * Gets (as xml) the "DPVReturnedStatus" attribute
         */
        public com.qas.www.web_2010_04.DPVStatusType xgetDPVReturnedStatus()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.DPVStatusType target = null;
                target = (com.qas.www.web_2010_04.DPVStatusType)get_store().find_attribute_user(DPVRETURNEDSTATUS$2);
                return target;
            }
        }
        
        /**
         * True if has "DPVReturnedStatus" attribute
         */
        public boolean isSetDPVReturnedStatus()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().find_attribute_user(DPVRETURNEDSTATUS$2) != null;
            }
        }
        
        /**
         * Sets the "DPVReturnedStatus" attribute
         */
        public void setDPVReturnedStatus(com.qas.www.web_2010_04.DPVStatusType.Enum dpvReturnedStatus)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DPVRETURNEDSTATUS$2);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(DPVRETURNEDSTATUS$2);
                }
                target.setEnumValue(dpvReturnedStatus);
            }
        }
        
        /**
         * Sets (as xml) the "DPVReturnedStatus" attribute
         */
        public void xsetDPVReturnedStatus(com.qas.www.web_2010_04.DPVStatusType dpvReturnedStatus)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.DPVStatusType target = null;
                target = (com.qas.www.web_2010_04.DPVStatusType)get_store().find_attribute_user(DPVRETURNEDSTATUS$2);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.DPVStatusType)get_store().add_attribute_user(DPVRETURNEDSTATUS$2);
                }
                target.set(dpvReturnedStatus);
            }
        }
        
        /**
         * Unsets the "DPVReturnedStatus" attribute
         */
        public void unsetDPVReturnedStatus()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_attribute(DPVRETURNEDSTATUS$2);
            }
        }
    }
}
