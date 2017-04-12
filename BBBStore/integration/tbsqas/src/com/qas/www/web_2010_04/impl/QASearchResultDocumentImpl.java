/*
 * An XML document type.
 * Localname: QASearchResult
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QASearchResultDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QASearchResult(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QASearchResultDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QASearchResultDocument
{
    
    public QASearchResultDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QASEARCHRESULT$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QASearchResult");
    
    
    /**
     * Gets the "QASearchResult" element
     */
    public com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult getQASearchResult()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult target = null;
            target = (com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult)get_store().find_element_user(QASEARCHRESULT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QASearchResult" element
     */
    public void setQASearchResult(com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult qaSearchResult)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult target = null;
            target = (com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult)get_store().find_element_user(QASEARCHRESULT$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult)get_store().add_element_user(QASEARCHRESULT$0);
            }
            target.set(qaSearchResult);
        }
    }
    
    /**
     * Appends and returns a new empty "QASearchResult" element
     */
    public com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult addNewQASearchResult()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult target = null;
            target = (com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult)get_store().add_element_user(QASEARCHRESULT$0);
            return target;
        }
    }
    /**
     * An XML QASearchResult(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QASearchResultImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult
    {
        
        public QASearchResultImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName QAPICKLIST$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAPicklist");
        private static final javax.xml.namespace.QName QAADDRESS$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAAddress");
        private static final javax.xml.namespace.QName VERIFICATIONFLAGS$4 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "VerificationFlags");
        private static final javax.xml.namespace.QName VERIFYLEVEL$6 = 
            new javax.xml.namespace.QName("", "VerifyLevel");
        
        
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
         * True if has "QAPicklist" element
         */
        public boolean isSetQAPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(QAPICKLIST$0) != 0;
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
        
        /**
         * Unsets the "QAPicklist" element
         */
        public void unsetQAPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(QAPICKLIST$0, 0);
            }
        }
        
        /**
         * Gets the "QAAddress" element
         */
        public com.qas.www.web_2010_04.QAAddressType getQAAddress()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAAddressType target = null;
                target = (com.qas.www.web_2010_04.QAAddressType)get_store().find_element_user(QAADDRESS$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "QAAddress" element
         */
        public boolean isSetQAAddress()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(QAADDRESS$2) != 0;
            }
        }
        
        /**
         * Sets the "QAAddress" element
         */
        public void setQAAddress(com.qas.www.web_2010_04.QAAddressType qaAddress)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAAddressType target = null;
                target = (com.qas.www.web_2010_04.QAAddressType)get_store().find_element_user(QAADDRESS$2, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QAAddressType)get_store().add_element_user(QAADDRESS$2);
                }
                target.set(qaAddress);
            }
        }
        
        /**
         * Appends and returns a new empty "QAAddress" element
         */
        public com.qas.www.web_2010_04.QAAddressType addNewQAAddress()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAAddressType target = null;
                target = (com.qas.www.web_2010_04.QAAddressType)get_store().add_element_user(QAADDRESS$2);
                return target;
            }
        }
        
        /**
         * Unsets the "QAAddress" element
         */
        public void unsetQAAddress()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(QAADDRESS$2, 0);
            }
        }
        
        /**
         * Gets the "VerificationFlags" element
         */
        public com.qas.www.web_2010_04.VerificationFlagsType getVerificationFlags()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.VerificationFlagsType target = null;
                target = (com.qas.www.web_2010_04.VerificationFlagsType)get_store().find_element_user(VERIFICATIONFLAGS$4, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "VerificationFlags" element
         */
        public boolean isSetVerificationFlags()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(VERIFICATIONFLAGS$4) != 0;
            }
        }
        
        /**
         * Sets the "VerificationFlags" element
         */
        public void setVerificationFlags(com.qas.www.web_2010_04.VerificationFlagsType verificationFlags)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.VerificationFlagsType target = null;
                target = (com.qas.www.web_2010_04.VerificationFlagsType)get_store().find_element_user(VERIFICATIONFLAGS$4, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.VerificationFlagsType)get_store().add_element_user(VERIFICATIONFLAGS$4);
                }
                target.set(verificationFlags);
            }
        }
        
        /**
         * Appends and returns a new empty "VerificationFlags" element
         */
        public com.qas.www.web_2010_04.VerificationFlagsType addNewVerificationFlags()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.VerificationFlagsType target = null;
                target = (com.qas.www.web_2010_04.VerificationFlagsType)get_store().add_element_user(VERIFICATIONFLAGS$4);
                return target;
            }
        }
        
        /**
         * Unsets the "VerificationFlags" element
         */
        public void unsetVerificationFlags()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(VERIFICATIONFLAGS$4, 0);
            }
        }
        
        /**
         * Gets the "VerifyLevel" attribute
         */
        public com.qas.www.web_2010_04.VerifyLevelType.Enum getVerifyLevel()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(VERIFYLEVEL$6);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(VERIFYLEVEL$6);
                }
                if (target == null)
                {
                    return null;
                }
                return (com.qas.www.web_2010_04.VerifyLevelType.Enum)target.getEnumValue();
            }
        }
        
        /**
         * Gets (as xml) the "VerifyLevel" attribute
         */
        public com.qas.www.web_2010_04.VerifyLevelType xgetVerifyLevel()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.VerifyLevelType target = null;
                target = (com.qas.www.web_2010_04.VerifyLevelType)get_store().find_attribute_user(VERIFYLEVEL$6);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.VerifyLevelType)get_default_attribute_value(VERIFYLEVEL$6);
                }
                return target;
            }
        }
        
        /**
         * True if has "VerifyLevel" attribute
         */
        public boolean isSetVerifyLevel()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().find_attribute_user(VERIFYLEVEL$6) != null;
            }
        }
        
        /**
         * Sets the "VerifyLevel" attribute
         */
        public void setVerifyLevel(com.qas.www.web_2010_04.VerifyLevelType.Enum verifyLevel)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(VERIFYLEVEL$6);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(VERIFYLEVEL$6);
                }
                target.setEnumValue(verifyLevel);
            }
        }
        
        /**
         * Sets (as xml) the "VerifyLevel" attribute
         */
        public void xsetVerifyLevel(com.qas.www.web_2010_04.VerifyLevelType verifyLevel)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.VerifyLevelType target = null;
                target = (com.qas.www.web_2010_04.VerifyLevelType)get_store().find_attribute_user(VERIFYLEVEL$6);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.VerifyLevelType)get_store().add_attribute_user(VERIFYLEVEL$6);
                }
                target.set(verifyLevel);
            }
        }
        
        /**
         * Unsets the "VerifyLevel" attribute
         */
        public void unsetVerifyLevel()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_attribute(VERIFYLEVEL$6);
            }
        }
    }
}
