/*
 * An XML document type.
 * Localname: QALicenceInfo
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QALicenceInfoDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QALicenceInfo(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QALicenceInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QALicenceInfoDocument
{
    
    public QALicenceInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QALICENCEINFO$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QALicenceInfo");
    
    
    /**
     * Gets the "QALicenceInfo" element
     */
    public com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo getQALicenceInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo target = null;
            target = (com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo)get_store().find_element_user(QALICENCEINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QALicenceInfo" element
     */
    public void setQALicenceInfo(com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo qaLicenceInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo target = null;
            target = (com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo)get_store().find_element_user(QALICENCEINFO$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo)get_store().add_element_user(QALICENCEINFO$0);
            }
            target.set(qaLicenceInfo);
        }
    }
    
    /**
     * Appends and returns a new empty "QALicenceInfo" element
     */
    public com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo addNewQALicenceInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo target = null;
            target = (com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo)get_store().add_element_user(QALICENCEINFO$0);
            return target;
        }
    }
    /**
     * An XML QALicenceInfo(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QALicenceInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo
    {
        
        public QALicenceInfoImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName WARNINGLEVEL$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "WarningLevel");
        private static final javax.xml.namespace.QName LICENSEDSET$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "LicensedSet");
        
        
        /**
         * Gets the "WarningLevel" element
         */
        public com.qas.www.web_2010_04.LicenceWarningLevel.Enum getWarningLevel()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WARNINGLEVEL$0, 0);
                if (target == null)
                {
                    return null;
                }
                return (com.qas.www.web_2010_04.LicenceWarningLevel.Enum)target.getEnumValue();
            }
        }
        
        /**
         * Gets (as xml) the "WarningLevel" element
         */
        public com.qas.www.web_2010_04.LicenceWarningLevel xgetWarningLevel()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.LicenceWarningLevel target = null;
                target = (com.qas.www.web_2010_04.LicenceWarningLevel)get_store().find_element_user(WARNINGLEVEL$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "WarningLevel" element
         */
        public void setWarningLevel(com.qas.www.web_2010_04.LicenceWarningLevel.Enum warningLevel)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WARNINGLEVEL$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(WARNINGLEVEL$0);
                }
                target.setEnumValue(warningLevel);
            }
        }
        
        /**
         * Sets (as xml) the "WarningLevel" element
         */
        public void xsetWarningLevel(com.qas.www.web_2010_04.LicenceWarningLevel warningLevel)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.LicenceWarningLevel target = null;
                target = (com.qas.www.web_2010_04.LicenceWarningLevel)get_store().find_element_user(WARNINGLEVEL$0, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.LicenceWarningLevel)get_store().add_element_user(WARNINGLEVEL$0);
                }
                target.set(warningLevel);
            }
        }
        
        /**
         * Gets array of all "LicensedSet" elements
         */
        public com.qas.www.web_2010_04.QALicensedSet[] getLicensedSetArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(LICENSEDSET$2, targetList);
                com.qas.www.web_2010_04.QALicensedSet[] result = new com.qas.www.web_2010_04.QALicensedSet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets ith "LicensedSet" element
         */
        public com.qas.www.web_2010_04.QALicensedSet getLicensedSetArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QALicensedSet target = null;
                target = (com.qas.www.web_2010_04.QALicensedSet)get_store().find_element_user(LICENSEDSET$2, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        /**
         * Returns number of "LicensedSet" element
         */
        public int sizeOfLicensedSetArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(LICENSEDSET$2);
            }
        }
        
        /**
         * Sets array of all "LicensedSet" element
         */
        public void setLicensedSetArray(com.qas.www.web_2010_04.QALicensedSet[] licensedSetArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(licensedSetArray, LICENSEDSET$2);
            }
        }
        
        /**
         * Sets ith "LicensedSet" element
         */
        public void setLicensedSetArray(int i, com.qas.www.web_2010_04.QALicensedSet licensedSet)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QALicensedSet target = null;
                target = (com.qas.www.web_2010_04.QALicensedSet)get_store().find_element_user(LICENSEDSET$2, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(licensedSet);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "LicensedSet" element
         */
        public com.qas.www.web_2010_04.QALicensedSet insertNewLicensedSet(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QALicensedSet target = null;
                target = (com.qas.www.web_2010_04.QALicensedSet)get_store().insert_element_user(LICENSEDSET$2, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "LicensedSet" element
         */
        public com.qas.www.web_2010_04.QALicensedSet addNewLicensedSet()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QALicensedSet target = null;
                target = (com.qas.www.web_2010_04.QALicensedSet)get_store().add_element_user(LICENSEDSET$2);
                return target;
            }
        }
        
        /**
         * Removes the ith "LicensedSet" element
         */
        public void removeLicensedSet(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(LICENSEDSET$2, i);
            }
        }
    }
}
