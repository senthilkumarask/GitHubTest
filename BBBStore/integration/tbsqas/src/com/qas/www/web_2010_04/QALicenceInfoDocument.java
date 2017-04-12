/*
 * An XML document type.
 * Localname: QALicenceInfo
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QALicenceInfoDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QALicenceInfo(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QALicenceInfoDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QALicenceInfoDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qalicenceinfo9a23doctype");
    
    /**
     * Gets the "QALicenceInfo" element
     */
    com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo getQALicenceInfo();
    
    /**
     * Sets the "QALicenceInfo" element
     */
    void setQALicenceInfo(com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo qaLicenceInfo);
    
    /**
     * Appends and returns a new empty "QALicenceInfo" element
     */
    com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo addNewQALicenceInfo();
    
    /**
     * An XML QALicenceInfo(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QALicenceInfo extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QALicenceInfo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qalicenceinfo67e8elemtype");
        
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
         * Gets array of all "LicensedSet" elements
         */
        com.qas.www.web_2010_04.QALicensedSet[] getLicensedSetArray();
        
        /**
         * Gets ith "LicensedSet" element
         */
        com.qas.www.web_2010_04.QALicensedSet getLicensedSetArray(int i);
        
        /**
         * Returns number of "LicensedSet" element
         */
        int sizeOfLicensedSetArray();
        
        /**
         * Sets array of all "LicensedSet" element
         */
        void setLicensedSetArray(com.qas.www.web_2010_04.QALicensedSet[] licensedSetArray);
        
        /**
         * Sets ith "LicensedSet" element
         */
        void setLicensedSetArray(int i, com.qas.www.web_2010_04.QALicensedSet licensedSet);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "LicensedSet" element
         */
        com.qas.www.web_2010_04.QALicensedSet insertNewLicensedSet(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "LicensedSet" element
         */
        com.qas.www.web_2010_04.QALicensedSet addNewLicensedSet();
        
        /**
         * Removes the ith "LicensedSet" element
         */
        void removeLicensedSet(int i);
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo newInstance() {
              return (com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QALicenceInfoDocument.QALicenceInfo) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QALicenceInfoDocument newInstance() {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QALicenceInfoDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QALicenceInfoDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
