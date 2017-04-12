/*
 * XML Type:  QALicensedSet
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QALicensedSet
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * An XML QALicensedSet(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public interface QALicensedSet extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QALicensedSet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qalicensedsetb993type");
    
    /**
     * Gets the "ID" element
     */
    java.lang.String getID();
    
    /**
     * Gets (as xml) the "ID" element
     */
    org.apache.xmlbeans.XmlString xgetID();
    
    /**
     * Sets the "ID" element
     */
    void setID(java.lang.String id);
    
    /**
     * Sets (as xml) the "ID" element
     */
    void xsetID(org.apache.xmlbeans.XmlString id);
    
    /**
     * Gets the "Description" element
     */
    java.lang.String getDescription();
    
    /**
     * Gets (as xml) the "Description" element
     */
    org.apache.xmlbeans.XmlString xgetDescription();
    
    /**
     * Sets the "Description" element
     */
    void setDescription(java.lang.String description);
    
    /**
     * Sets (as xml) the "Description" element
     */
    void xsetDescription(org.apache.xmlbeans.XmlString description);
    
    /**
     * Gets the "Copyright" element
     */
    java.lang.String getCopyright();
    
    /**
     * Gets (as xml) the "Copyright" element
     */
    org.apache.xmlbeans.XmlString xgetCopyright();
    
    /**
     * Sets the "Copyright" element
     */
    void setCopyright(java.lang.String copyright);
    
    /**
     * Sets (as xml) the "Copyright" element
     */
    void xsetCopyright(org.apache.xmlbeans.XmlString copyright);
    
    /**
     * Gets the "Version" element
     */
    java.lang.String getVersion();
    
    /**
     * Gets (as xml) the "Version" element
     */
    org.apache.xmlbeans.XmlString xgetVersion();
    
    /**
     * Sets the "Version" element
     */
    void setVersion(java.lang.String version);
    
    /**
     * Sets (as xml) the "Version" element
     */
    void xsetVersion(org.apache.xmlbeans.XmlString version);
    
    /**
     * Gets the "BaseCountry" element
     */
    java.lang.String getBaseCountry();
    
    /**
     * Gets (as xml) the "BaseCountry" element
     */
    org.apache.xmlbeans.XmlString xgetBaseCountry();
    
    /**
     * Sets the "BaseCountry" element
     */
    void setBaseCountry(java.lang.String baseCountry);
    
    /**
     * Sets (as xml) the "BaseCountry" element
     */
    void xsetBaseCountry(org.apache.xmlbeans.XmlString baseCountry);
    
    /**
     * Gets the "Status" element
     */
    java.lang.String getStatus();
    
    /**
     * Gets (as xml) the "Status" element
     */
    org.apache.xmlbeans.XmlString xgetStatus();
    
    /**
     * Sets the "Status" element
     */
    void setStatus(java.lang.String status);
    
    /**
     * Sets (as xml) the "Status" element
     */
    void xsetStatus(org.apache.xmlbeans.XmlString status);
    
    /**
     * Gets the "Server" element
     */
    java.lang.String getServer();
    
    /**
     * Gets (as xml) the "Server" element
     */
    org.apache.xmlbeans.XmlString xgetServer();
    
    /**
     * Sets the "Server" element
     */
    void setServer(java.lang.String server);
    
    /**
     * Sets (as xml) the "Server" element
     */
    void xsetServer(org.apache.xmlbeans.XmlString server);
    
    /**
     * Gets the "WarningLevel" element
     */
    com.qas.www.web_2010_04.LicenceWarningLevel.Enum getWarningLevel();
    
    /**
     * Gets (as xml) the "WarningLevel" element
     */
    com.qas.www.web_2010_04.LicenceWarningLevel xgetWarningLevel();
    
    /**
     * Sets the "WarningLevel" element
     */
    void setWarningLevel(com.qas.www.web_2010_04.LicenceWarningLevel.Enum warningLevel);
    
    /**
     * Sets (as xml) the "WarningLevel" element
     */
    void xsetWarningLevel(com.qas.www.web_2010_04.LicenceWarningLevel warningLevel);
    
    /**
     * Gets the "DaysLeft" element
     */
    java.math.BigInteger getDaysLeft();
    
    /**
     * Gets (as xml) the "DaysLeft" element
     */
    org.apache.xmlbeans.XmlNonNegativeInteger xgetDaysLeft();
    
    /**
     * Sets the "DaysLeft" element
     */
    void setDaysLeft(java.math.BigInteger daysLeft);
    
    /**
     * Sets (as xml) the "DaysLeft" element
     */
    void xsetDaysLeft(org.apache.xmlbeans.XmlNonNegativeInteger daysLeft);
    
    /**
     * Gets the "DataDaysLeft" element
     */
    java.math.BigInteger getDataDaysLeft();
    
    /**
     * Gets (as xml) the "DataDaysLeft" element
     */
    org.apache.xmlbeans.XmlNonNegativeInteger xgetDataDaysLeft();
    
    /**
     * Sets the "DataDaysLeft" element
     */
    void setDataDaysLeft(java.math.BigInteger dataDaysLeft);
    
    /**
     * Sets (as xml) the "DataDaysLeft" element
     */
    void xsetDataDaysLeft(org.apache.xmlbeans.XmlNonNegativeInteger dataDaysLeft);
    
    /**
     * Gets the "LicenceDaysLeft" element
     */
    java.math.BigInteger getLicenceDaysLeft();
    
    /**
     * Gets (as xml) the "LicenceDaysLeft" element
     */
    org.apache.xmlbeans.XmlNonNegativeInteger xgetLicenceDaysLeft();
    
    /**
     * Sets the "LicenceDaysLeft" element
     */
    void setLicenceDaysLeft(java.math.BigInteger licenceDaysLeft);
    
    /**
     * Sets (as xml) the "LicenceDaysLeft" element
     */
    void xsetLicenceDaysLeft(org.apache.xmlbeans.XmlNonNegativeInteger licenceDaysLeft);
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QALicensedSet newInstance() {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicensedSet parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QALicensedSet parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QALicensedSet parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QALicensedSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
