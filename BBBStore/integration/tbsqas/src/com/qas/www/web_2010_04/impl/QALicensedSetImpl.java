/*
 * XML Type:  QALicensedSet
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QALicensedSet
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML QALicensedSet(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class QALicensedSetImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QALicensedSet
{
    
    public QALicensedSetImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ID$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "ID");
    private static final javax.xml.namespace.QName DESCRIPTION$2 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Description");
    private static final javax.xml.namespace.QName COPYRIGHT$4 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Copyright");
    private static final javax.xml.namespace.QName VERSION$6 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Version");
    private static final javax.xml.namespace.QName BASECOUNTRY$8 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "BaseCountry");
    private static final javax.xml.namespace.QName STATUS$10 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Status");
    private static final javax.xml.namespace.QName SERVER$12 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Server");
    private static final javax.xml.namespace.QName WARNINGLEVEL$14 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "WarningLevel");
    private static final javax.xml.namespace.QName DAYSLEFT$16 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DaysLeft");
    private static final javax.xml.namespace.QName DATADAYSLEFT$18 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "DataDaysLeft");
    private static final javax.xml.namespace.QName LICENCEDAYSLEFT$20 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "LicenceDaysLeft");
    
    
    /**
     * Gets the "ID" element
     */
    public java.lang.String getID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ID$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "ID" element
     */
    public org.apache.xmlbeans.XmlString xgetID()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ID$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "ID" element
     */
    public void setID(java.lang.String id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ID$0);
            }
            target.setStringValue(id);
        }
    }
    
    /**
     * Sets (as xml) the "ID" element
     */
    public void xsetID(org.apache.xmlbeans.XmlString id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(ID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(ID$0);
            }
            target.set(id);
        }
    }
    
    /**
     * Gets the "Description" element
     */
    public java.lang.String getDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESCRIPTION$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Description" element
     */
    public org.apache.xmlbeans.XmlString xgetDescription()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESCRIPTION$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Description" element
     */
    public void setDescription(java.lang.String description)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESCRIPTION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DESCRIPTION$2);
            }
            target.setStringValue(description);
        }
    }
    
    /**
     * Sets (as xml) the "Description" element
     */
    public void xsetDescription(org.apache.xmlbeans.XmlString description)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESCRIPTION$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DESCRIPTION$2);
            }
            target.set(description);
        }
    }
    
    /**
     * Gets the "Copyright" element
     */
    public java.lang.String getCopyright()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COPYRIGHT$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Copyright" element
     */
    public org.apache.xmlbeans.XmlString xgetCopyright()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COPYRIGHT$4, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Copyright" element
     */
    public void setCopyright(java.lang.String copyright)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COPYRIGHT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COPYRIGHT$4);
            }
            target.setStringValue(copyright);
        }
    }
    
    /**
     * Sets (as xml) the "Copyright" element
     */
    public void xsetCopyright(org.apache.xmlbeans.XmlString copyright)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(COPYRIGHT$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(COPYRIGHT$4);
            }
            target.set(copyright);
        }
    }
    
    /**
     * Gets the "Version" element
     */
    public java.lang.String getVersion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VERSION$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Version" element
     */
    public org.apache.xmlbeans.XmlString xgetVersion()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VERSION$6, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Version" element
     */
    public void setVersion(java.lang.String version)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(VERSION$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(VERSION$6);
            }
            target.setStringValue(version);
        }
    }
    
    /**
     * Sets (as xml) the "Version" element
     */
    public void xsetVersion(org.apache.xmlbeans.XmlString version)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(VERSION$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(VERSION$6);
            }
            target.set(version);
        }
    }
    
    /**
     * Gets the "BaseCountry" element
     */
    public java.lang.String getBaseCountry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BASECOUNTRY$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "BaseCountry" element
     */
    public org.apache.xmlbeans.XmlString xgetBaseCountry()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BASECOUNTRY$8, 0);
            return target;
        }
    }
    
    /**
     * Sets the "BaseCountry" element
     */
    public void setBaseCountry(java.lang.String baseCountry)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BASECOUNTRY$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BASECOUNTRY$8);
            }
            target.setStringValue(baseCountry);
        }
    }
    
    /**
     * Sets (as xml) the "BaseCountry" element
     */
    public void xsetBaseCountry(org.apache.xmlbeans.XmlString baseCountry)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BASECOUNTRY$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BASECOUNTRY$8);
            }
            target.set(baseCountry);
        }
    }
    
    /**
     * Gets the "Status" element
     */
    public java.lang.String getStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STATUS$10, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Status" element
     */
    public org.apache.xmlbeans.XmlString xgetStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STATUS$10, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Status" element
     */
    public void setStatus(java.lang.String status)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STATUS$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STATUS$10);
            }
            target.setStringValue(status);
        }
    }
    
    /**
     * Sets (as xml) the "Status" element
     */
    public void xsetStatus(org.apache.xmlbeans.XmlString status)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(STATUS$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(STATUS$10);
            }
            target.set(status);
        }
    }
    
    /**
     * Gets the "Server" element
     */
    public java.lang.String getServer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SERVER$12, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Server" element
     */
    public org.apache.xmlbeans.XmlString xgetServer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SERVER$12, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Server" element
     */
    public void setServer(java.lang.String server)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SERVER$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SERVER$12);
            }
            target.setStringValue(server);
        }
    }
    
    /**
     * Sets (as xml) the "Server" element
     */
    public void xsetServer(org.apache.xmlbeans.XmlString server)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SERVER$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SERVER$12);
            }
            target.set(server);
        }
    }
    
    /**
     * Gets the "WarningLevel" element
     */
    public com.qas.www.web_2010_04.LicenceWarningLevel.Enum getWarningLevel()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WARNINGLEVEL$14, 0);
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
            target = (com.qas.www.web_2010_04.LicenceWarningLevel)get_store().find_element_user(WARNINGLEVEL$14, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(WARNINGLEVEL$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(WARNINGLEVEL$14);
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
            target = (com.qas.www.web_2010_04.LicenceWarningLevel)get_store().find_element_user(WARNINGLEVEL$14, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.LicenceWarningLevel)get_store().add_element_user(WARNINGLEVEL$14);
            }
            target.set(warningLevel);
        }
    }
    
    /**
     * Gets the "DaysLeft" element
     */
    public java.math.BigInteger getDaysLeft()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DAYSLEFT$16, 0);
            if (target == null)
            {
                return null;
            }
            return target.getBigIntegerValue();
        }
    }
    
    /**
     * Gets (as xml) the "DaysLeft" element
     */
    public org.apache.xmlbeans.XmlNonNegativeInteger xgetDaysLeft()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(DAYSLEFT$16, 0);
            return target;
        }
    }
    
    /**
     * Sets the "DaysLeft" element
     */
    public void setDaysLeft(java.math.BigInteger daysLeft)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DAYSLEFT$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DAYSLEFT$16);
            }
            target.setBigIntegerValue(daysLeft);
        }
    }
    
    /**
     * Sets (as xml) the "DaysLeft" element
     */
    public void xsetDaysLeft(org.apache.xmlbeans.XmlNonNegativeInteger daysLeft)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(DAYSLEFT$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().add_element_user(DAYSLEFT$16);
            }
            target.set(daysLeft);
        }
    }
    
    /**
     * Gets the "DataDaysLeft" element
     */
    public java.math.BigInteger getDataDaysLeft()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATADAYSLEFT$18, 0);
            if (target == null)
            {
                return null;
            }
            return target.getBigIntegerValue();
        }
    }
    
    /**
     * Gets (as xml) the "DataDaysLeft" element
     */
    public org.apache.xmlbeans.XmlNonNegativeInteger xgetDataDaysLeft()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(DATADAYSLEFT$18, 0);
            return target;
        }
    }
    
    /**
     * Sets the "DataDaysLeft" element
     */
    public void setDataDaysLeft(java.math.BigInteger dataDaysLeft)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATADAYSLEFT$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DATADAYSLEFT$18);
            }
            target.setBigIntegerValue(dataDaysLeft);
        }
    }
    
    /**
     * Sets (as xml) the "DataDaysLeft" element
     */
    public void xsetDataDaysLeft(org.apache.xmlbeans.XmlNonNegativeInteger dataDaysLeft)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(DATADAYSLEFT$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().add_element_user(DATADAYSLEFT$18);
            }
            target.set(dataDaysLeft);
        }
    }
    
    /**
     * Gets the "LicenceDaysLeft" element
     */
    public java.math.BigInteger getLicenceDaysLeft()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LICENCEDAYSLEFT$20, 0);
            if (target == null)
            {
                return null;
            }
            return target.getBigIntegerValue();
        }
    }
    
    /**
     * Gets (as xml) the "LicenceDaysLeft" element
     */
    public org.apache.xmlbeans.XmlNonNegativeInteger xgetLicenceDaysLeft()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(LICENCEDAYSLEFT$20, 0);
            return target;
        }
    }
    
    /**
     * Sets the "LicenceDaysLeft" element
     */
    public void setLicenceDaysLeft(java.math.BigInteger licenceDaysLeft)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LICENCEDAYSLEFT$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LICENCEDAYSLEFT$20);
            }
            target.setBigIntegerValue(licenceDaysLeft);
        }
    }
    
    /**
     * Sets (as xml) the "LicenceDaysLeft" element
     */
    public void xsetLicenceDaysLeft(org.apache.xmlbeans.XmlNonNegativeInteger licenceDaysLeft)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(LICENCEDAYSLEFT$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().add_element_user(LICENCEDAYSLEFT$20);
            }
            target.set(licenceDaysLeft);
        }
    }
}
