/*
 * An XML document type.
 * Localname: QALayouts
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QALayoutsDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QALayouts(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QALayoutsDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QALayoutsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qalayouts7979doctype");
    
    /**
     * Gets the "QALayouts" element
     */
    com.qas.www.web_2010_04.QALayoutsDocument.QALayouts getQALayouts();
    
    /**
     * Sets the "QALayouts" element
     */
    void setQALayouts(com.qas.www.web_2010_04.QALayoutsDocument.QALayouts qaLayouts);
    
    /**
     * Appends and returns a new empty "QALayouts" element
     */
    com.qas.www.web_2010_04.QALayoutsDocument.QALayouts addNewQALayouts();
    
    /**
     * An XML QALayouts(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QALayouts extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QALayouts.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qalayoutse794elemtype");
        
        /**
         * Gets array of all "Layout" elements
         */
        com.qas.www.web_2010_04.QALayout[] getLayoutArray();
        
        /**
         * Gets ith "Layout" element
         */
        com.qas.www.web_2010_04.QALayout getLayoutArray(int i);
        
        /**
         * Returns number of "Layout" element
         */
        int sizeOfLayoutArray();
        
        /**
         * Sets array of all "Layout" element
         */
        void setLayoutArray(com.qas.www.web_2010_04.QALayout[] layoutArray);
        
        /**
         * Sets ith "Layout" element
         */
        void setLayoutArray(int i, com.qas.www.web_2010_04.QALayout layout);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "Layout" element
         */
        com.qas.www.web_2010_04.QALayout insertNewLayout(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "Layout" element
         */
        com.qas.www.web_2010_04.QALayout addNewLayout();
        
        /**
         * Removes the ith "Layout" element
         */
        void removeLayout(int i);
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QALayoutsDocument.QALayouts newInstance() {
              return (com.qas.www.web_2010_04.QALayoutsDocument.QALayouts) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QALayoutsDocument.QALayouts newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QALayoutsDocument.QALayouts) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QALayoutsDocument newInstance() {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QALayoutsDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QALayoutsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
