/*
 * An XML document type.
 * Localname: QAUnlockDPVOk
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAUnlockDPVOkDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QAUnlockDPVOk(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QAUnlockDPVOkDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QAUnlockDPVOkDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qaunlockdpvok0460doctype");
    
    /**
     * Gets the "QAUnlockDPVOk" element
     */
    com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk getQAUnlockDPVOk();
    
    /**
     * Sets the "QAUnlockDPVOk" element
     */
    void setQAUnlockDPVOk(com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk qaUnlockDPVOk);
    
    /**
     * Appends and returns a new empty "QAUnlockDPVOk" element
     */
    com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk addNewQAUnlockDPVOk();
    
    /**
     * An XML QAUnlockDPVOk(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QAUnlockDPVOk extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QAUnlockDPVOk.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qaunlockdpvok2262elemtype");
        
        /**
         * Gets the "UnlockCodeOk" element
         */
        boolean getUnlockCodeOk();
        
        /**
         * Gets (as xml) the "UnlockCodeOk" element
         */
        org.apache.xmlbeans.XmlBoolean xgetUnlockCodeOk();
        
        /**
         * Sets the "UnlockCodeOk" element
         */
        void setUnlockCodeOk(boolean unlockCodeOk);
        
        /**
         * Sets (as xml) the "UnlockCodeOk" element
         */
        void xsetUnlockCodeOk(org.apache.xmlbeans.XmlBoolean unlockCodeOk);
        
        /**
         * Gets the "ErrorCode" element
         */
        java.lang.String getErrorCode();
        
        /**
         * Gets (as xml) the "ErrorCode" element
         */
        org.apache.xmlbeans.XmlString xgetErrorCode();
        
        /**
         * True if has "ErrorCode" element
         */
        boolean isSetErrorCode();
        
        /**
         * Sets the "ErrorCode" element
         */
        void setErrorCode(java.lang.String errorCode);
        
        /**
         * Sets (as xml) the "ErrorCode" element
         */
        void xsetErrorCode(org.apache.xmlbeans.XmlString errorCode);
        
        /**
         * Unsets the "ErrorCode" element
         */
        void unsetErrorCode();
        
        /**
         * Gets the "ErrorMessage" element
         */
        java.lang.String getErrorMessage();
        
        /**
         * Gets (as xml) the "ErrorMessage" element
         */
        org.apache.xmlbeans.XmlString xgetErrorMessage();
        
        /**
         * True if has "ErrorMessage" element
         */
        boolean isSetErrorMessage();
        
        /**
         * Sets the "ErrorMessage" element
         */
        void setErrorMessage(java.lang.String errorMessage);
        
        /**
         * Sets (as xml) the "ErrorMessage" element
         */
        void xsetErrorMessage(org.apache.xmlbeans.XmlString errorMessage);
        
        /**
         * Unsets the "ErrorMessage" element
         */
        void unsetErrorMessage();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk newInstance() {
              return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument.QAUnlockDPVOk) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument newInstance() {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QAUnlockDPVOkDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QAUnlockDPVOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
