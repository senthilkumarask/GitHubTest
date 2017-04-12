/*
 * An XML document type.
 * Localname: QADataHashCode
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QADataHashCodeDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QADataHashCode(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QADataHashCodeDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QADataHashCodeDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qadatahashcode58ebdoctype");
    
    /**
     * Gets the "QADataHashCode" element
     */
    com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode getQADataHashCode();
    
    /**
     * Sets the "QADataHashCode" element
     */
    void setQADataHashCode(com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode qaDataHashCode);
    
    /**
     * Appends and returns a new empty "QADataHashCode" element
     */
    com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode addNewQADataHashCode();
    
    /**
     * An XML QADataHashCode(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QADataHashCode extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QADataHashCode.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qadatahashcode6d9celemtype");
        
        /**
         * Gets the "DataHashCode" element
         */
        java.lang.String getDataHashCode();
        
        /**
         * Gets (as xml) the "DataHashCode" element
         */
        org.apache.xmlbeans.XmlString xgetDataHashCode();
        
        /**
         * Sets the "DataHashCode" element
         */
        void setDataHashCode(java.lang.String dataHashCode);
        
        /**
         * Sets (as xml) the "DataHashCode" element
         */
        void xsetDataHashCode(org.apache.xmlbeans.XmlString dataHashCode);
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode newInstance() {
              return (com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QADataHashCodeDocument.QADataHashCode) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QADataHashCodeDocument newInstance() {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QADataHashCodeDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QADataHashCodeDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
