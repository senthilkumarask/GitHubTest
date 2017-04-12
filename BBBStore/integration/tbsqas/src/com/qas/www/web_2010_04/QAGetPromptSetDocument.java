/*
 * An XML document type.
 * Localname: QAGetPromptSet
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetPromptSetDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QAGetPromptSet(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QAGetPromptSetDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QAGetPromptSetDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qagetpromptset24c8doctype");
    
    /**
     * Gets the "QAGetPromptSet" element
     */
    com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet getQAGetPromptSet();
    
    /**
     * Sets the "QAGetPromptSet" element
     */
    void setQAGetPromptSet(com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet qaGetPromptSet);
    
    /**
     * Appends and returns a new empty "QAGetPromptSet" element
     */
    com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet addNewQAGetPromptSet();
    
    /**
     * An XML QAGetPromptSet(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QAGetPromptSet extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QAGetPromptSet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qagetpromptset033celemtype");
        
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
         * True if has "Engine" element
         */
        boolean isSetEngine();
        
        /**
         * Sets the "Engine" element
         */
        void setEngine(com.qas.www.web_2010_04.EngineType engine);
        
        /**
         * Appends and returns a new empty "Engine" element
         */
        com.qas.www.web_2010_04.EngineType addNewEngine();
        
        /**
         * Unsets the "Engine" element
         */
        void unsetEngine();
        
        /**
         * Gets the "PromptSet" element
         */
        com.qas.www.web_2010_04.PromptSetType.Enum getPromptSet();
        
        /**
         * Gets (as xml) the "PromptSet" element
         */
        com.qas.www.web_2010_04.PromptSetType xgetPromptSet();
        
        /**
         * Sets the "PromptSet" element
         */
        void setPromptSet(com.qas.www.web_2010_04.PromptSetType.Enum promptSet);
        
        /**
         * Sets (as xml) the "PromptSet" element
         */
        void xsetPromptSet(com.qas.www.web_2010_04.PromptSetType promptSet);
        
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
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet newInstance() {
              return (com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument newInstance() {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QAGetPromptSetDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QAGetPromptSetDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
