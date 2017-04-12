/*
 * An XML document type.
 * Localname: QAGetLicenseInfo
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetLicenseInfoDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetLicenseInfo(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetLicenseInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetLicenseInfoDocument
{
    
    public QAGetLicenseInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETLICENSEINFO$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetLicenseInfo");
    
    
    /**
     * Gets the "QAGetLicenseInfo" element
     */
    public com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo getQAGetLicenseInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo target = null;
            target = (com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo)get_store().find_element_user(QAGETLICENSEINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetLicenseInfo" element
     */
    public void setQAGetLicenseInfo(com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo qaGetLicenseInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo target = null;
            target = (com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo)get_store().find_element_user(QAGETLICENSEINFO$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo)get_store().add_element_user(QAGETLICENSEINFO$0);
            }
            target.set(qaGetLicenseInfo);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetLicenseInfo" element
     */
    public com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo addNewQAGetLicenseInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo target = null;
            target = (com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo)get_store().add_element_user(QAGETLICENSEINFO$0);
            return target;
        }
    }
    /**
     * An XML QAGetLicenseInfo(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetLicenseInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetLicenseInfoDocument.QAGetLicenseInfo
    {
        
        public QAGetLicenseInfoImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
