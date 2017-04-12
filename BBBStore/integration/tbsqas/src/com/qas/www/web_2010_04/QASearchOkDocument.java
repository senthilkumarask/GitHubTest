/*
 * An XML document type.
 * Localname: QASearchOk
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QASearchOkDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QASearchOk(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QASearchOkDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QASearchOkDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qasearchokc4ccdoctype");
    
    /**
     * Gets the "QASearchOk" element
     */
    com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk getQASearchOk();
    
    /**
     * Sets the "QASearchOk" element
     */
    void setQASearchOk(com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk qaSearchOk);
    
    /**
     * Appends and returns a new empty "QASearchOk" element
     */
    com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk addNewQASearchOk();
    
    /**
     * An XML QASearchOk(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QASearchOk extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QASearchOk.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qasearchok0b3celemtype");
        
        /**
         * Gets the "IsOk" element
         */
        boolean getIsOk();
        
        /**
         * Gets (as xml) the "IsOk" element
         */
        org.apache.xmlbeans.XmlBoolean xgetIsOk();
        
        /**
         * Sets the "IsOk" element
         */
        void setIsOk(boolean isOk);
        
        /**
         * Sets (as xml) the "IsOk" element
         */
        void xsetIsOk(org.apache.xmlbeans.XmlBoolean isOk);
        
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
         * Gets array of all "ErrorDetail" elements
         */
        java.lang.String[] getErrorDetailArray();
        
        /**
         * Gets ith "ErrorDetail" element
         */
        java.lang.String getErrorDetailArray(int i);
        
        /**
         * Gets (as xml) array of all "ErrorDetail" elements
         */
        org.apache.xmlbeans.XmlString[] xgetErrorDetailArray();
        
        /**
         * Gets (as xml) ith "ErrorDetail" element
         */
        org.apache.xmlbeans.XmlString xgetErrorDetailArray(int i);
        
        /**
         * Returns number of "ErrorDetail" element
         */
        int sizeOfErrorDetailArray();
        
        /**
         * Sets array of all "ErrorDetail" element
         */
        void setErrorDetailArray(java.lang.String[] errorDetailArray);
        
        /**
         * Sets ith "ErrorDetail" element
         */
        void setErrorDetailArray(int i, java.lang.String errorDetail);
        
        /**
         * Sets (as xml) array of all "ErrorDetail" element
         */
        void xsetErrorDetailArray(org.apache.xmlbeans.XmlString[] errorDetailArray);
        
        /**
         * Sets (as xml) ith "ErrorDetail" element
         */
        void xsetErrorDetailArray(int i, org.apache.xmlbeans.XmlString errorDetail);
        
        /**
         * Inserts the value as the ith "ErrorDetail" element
         */
        void insertErrorDetail(int i, java.lang.String errorDetail);
        
        /**
         * Appends the value as the last "ErrorDetail" element
         */
        void addErrorDetail(java.lang.String errorDetail);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "ErrorDetail" element
         */
        org.apache.xmlbeans.XmlString insertNewErrorDetail(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "ErrorDetail" element
         */
        org.apache.xmlbeans.XmlString addNewErrorDetail();
        
        /**
         * Removes the ith "ErrorDetail" element
         */
        void removeErrorDetail(int i);
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk newInstance() {
              return (com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QASearchOkDocument.QASearchOk) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QASearchOkDocument newInstance() {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QASearchOkDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QASearchOkDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
