/*
 * An XML document type.
 * Localname: QASearchResult
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QASearchResultDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QASearchResult(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QASearchResultDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QASearchResultDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qasearchresult12ebdoctype");
    
    /**
     * Gets the "QASearchResult" element
     */
    com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult getQASearchResult();
    
    /**
     * Sets the "QASearchResult" element
     */
    void setQASearchResult(com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult qaSearchResult);
    
    /**
     * Appends and returns a new empty "QASearchResult" element
     */
    com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult addNewQASearchResult();
    
    /**
     * An XML QASearchResult(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QASearchResult extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QASearchResult.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qasearchresultad9celemtype");
        
        /**
         * Gets the "QAPicklist" element
         */
        com.qas.www.web_2010_04.QAPicklistType getQAPicklist();
        
        /**
         * True if has "QAPicklist" element
         */
        boolean isSetQAPicklist();
        
        /**
         * Sets the "QAPicklist" element
         */
        void setQAPicklist(com.qas.www.web_2010_04.QAPicklistType qaPicklist);
        
        /**
         * Appends and returns a new empty "QAPicklist" element
         */
        com.qas.www.web_2010_04.QAPicklistType addNewQAPicklist();
        
        /**
         * Unsets the "QAPicklist" element
         */
        void unsetQAPicklist();
        
        /**
         * Gets the "QAAddress" element
         */
        com.qas.www.web_2010_04.QAAddressType getQAAddress();
        
        /**
         * True if has "QAAddress" element
         */
        boolean isSetQAAddress();
        
        /**
         * Sets the "QAAddress" element
         */
        void setQAAddress(com.qas.www.web_2010_04.QAAddressType qaAddress);
        
        /**
         * Appends and returns a new empty "QAAddress" element
         */
        com.qas.www.web_2010_04.QAAddressType addNewQAAddress();
        
        /**
         * Unsets the "QAAddress" element
         */
        void unsetQAAddress();
        
        /**
         * Gets the "VerificationFlags" element
         */
        com.qas.www.web_2010_04.VerificationFlagsType getVerificationFlags();
        
        /**
         * True if has "VerificationFlags" element
         */
        boolean isSetVerificationFlags();
        
        /**
         * Sets the "VerificationFlags" element
         */
        void setVerificationFlags(com.qas.www.web_2010_04.VerificationFlagsType verificationFlags);
        
        /**
         * Appends and returns a new empty "VerificationFlags" element
         */
        com.qas.www.web_2010_04.VerificationFlagsType addNewVerificationFlags();
        
        /**
         * Unsets the "VerificationFlags" element
         */
        void unsetVerificationFlags();
        
        /**
         * Gets the "VerifyLevel" attribute
         */
        com.qas.www.web_2010_04.VerifyLevelType.Enum getVerifyLevel();
        
        /**
         * Gets (as xml) the "VerifyLevel" attribute
         */
        com.qas.www.web_2010_04.VerifyLevelType xgetVerifyLevel();
        
        /**
         * True if has "VerifyLevel" attribute
         */
        boolean isSetVerifyLevel();
        
        /**
         * Sets the "VerifyLevel" attribute
         */
        void setVerifyLevel(com.qas.www.web_2010_04.VerifyLevelType.Enum verifyLevel);
        
        /**
         * Sets (as xml) the "VerifyLevel" attribute
         */
        void xsetVerifyLevel(com.qas.www.web_2010_04.VerifyLevelType verifyLevel);
        
        /**
         * Unsets the "VerifyLevel" attribute
         */
        void unsetVerifyLevel();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult newInstance() {
              return (com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QASearchResultDocument.QASearchResult) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QASearchResultDocument newInstance() {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QASearchResultDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QASearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
