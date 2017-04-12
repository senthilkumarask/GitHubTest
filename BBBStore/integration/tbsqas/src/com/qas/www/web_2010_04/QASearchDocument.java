/*
 * An XML document type.
 * Localname: QASearch
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QASearchDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QASearch(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QASearchDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QASearchDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qasearch7608doctype");
    
    /**
     * Gets the "QASearch" element
     */
    com.qas.www.web_2010_04.QASearchDocument.QASearch getQASearch();
    
    /**
     * Sets the "QASearch" element
     */
    void setQASearch(com.qas.www.web_2010_04.QASearchDocument.QASearch qaSearch);
    
    /**
     * Appends and returns a new empty "QASearch" element
     */
    com.qas.www.web_2010_04.QASearchDocument.QASearch addNewQASearch();
    
    /**
     * An XML QASearch(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QASearch extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QASearch.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qasearch257celemtype");
        
        /**
         * Gets the "Country" element
         */
        java.lang.String getCountry();
        
        /**
         * Gets (as xml) the "Country" element
         */
        com.qas.www.web_2010_04.DataIDType xgetCountry();
        
        /**
         * Sets the "Country" element
         */
        void setCountry(java.lang.String country);
        
        /**
         * Sets (as xml) the "Country" element
         */
        void xsetCountry(com.qas.www.web_2010_04.DataIDType country);
        
        /**
         * Gets the "Engine" element
         */
        com.qas.www.web_2010_04.EngineType getEngine();
        
        /**
         * Sets the "Engine" element
         */
        void setEngine(com.qas.www.web_2010_04.EngineType engine);
        
        /**
         * Appends and returns a new empty "Engine" element
         */
        com.qas.www.web_2010_04.EngineType addNewEngine();
        
        /**
         * Gets the "Layout" element
         */
        java.lang.String getLayout();
        
        /**
         * Gets (as xml) the "Layout" element
         */
        org.apache.xmlbeans.XmlString xgetLayout();
        
        /**
         * True if has "Layout" element
         */
        boolean isSetLayout();
        
        /**
         * Sets the "Layout" element
         */
        void setLayout(java.lang.String layout);
        
        /**
         * Sets (as xml) the "Layout" element
         */
        void xsetLayout(org.apache.xmlbeans.XmlString layout);
        
        /**
         * Unsets the "Layout" element
         */
        void unsetLayout();
        
        /**
         * Gets the "QAConfig" element
         */
        com.qas.www.web_2010_04.QAConfigType getQAConfig();
        
        /**
         * True if has "QAConfig" element
         */
        boolean isSetQAConfig();
        
        /**
         * Sets the "QAConfig" element
         */
        void setQAConfig(com.qas.www.web_2010_04.QAConfigType qaConfig);
        
        /**
         * Appends and returns a new empty "QAConfig" element
         */
        com.qas.www.web_2010_04.QAConfigType addNewQAConfig();
        
        /**
         * Unsets the "QAConfig" element
         */
        void unsetQAConfig();
        
        /**
         * Gets the "Search" element
         */
        java.lang.String getSearch();
        
        /**
         * Gets (as xml) the "Search" element
         */
        org.apache.xmlbeans.XmlString xgetSearch();
        
        /**
         * Sets the "Search" element
         */
        void setSearch(java.lang.String search);
        
        /**
         * Sets (as xml) the "Search" element
         */
        void xsetSearch(org.apache.xmlbeans.XmlString search);
        
        /**
         * Gets the "FormattedAddressInPicklist" element
         */
        boolean getFormattedAddressInPicklist();
        
        /**
         * Gets (as xml) the "FormattedAddressInPicklist" element
         */
        org.apache.xmlbeans.XmlBoolean xgetFormattedAddressInPicklist();
        
        /**
         * True if has "FormattedAddressInPicklist" element
         */
        boolean isSetFormattedAddressInPicklist();
        
        /**
         * Sets the "FormattedAddressInPicklist" element
         */
        void setFormattedAddressInPicklist(boolean formattedAddressInPicklist);
        
        /**
         * Sets (as xml) the "FormattedAddressInPicklist" element
         */
        void xsetFormattedAddressInPicklist(org.apache.xmlbeans.XmlBoolean formattedAddressInPicklist);
        
        /**
         * Unsets the "FormattedAddressInPicklist" element
         */
        void unsetFormattedAddressInPicklist();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QASearchDocument.QASearch newInstance() {
              return (com.qas.www.web_2010_04.QASearchDocument.QASearch) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QASearchDocument.QASearch newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QASearchDocument.QASearch) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QASearchDocument newInstance() {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QASearchDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QASearchDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QASearchDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
