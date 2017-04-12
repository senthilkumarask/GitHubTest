/*
 * An XML document type.
 * Localname: QABulkSearchResult
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QABulkSearchResultDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QABulkSearchResult(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QABulkSearchResultDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QABulkSearchResultDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qabulksearchresultdef9doctype");
    
    /**
     * Gets the "QABulkSearchResult" element
     */
    com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult getQABulkSearchResult();
    
    /**
     * Sets the "QABulkSearchResult" element
     */
    void setQABulkSearchResult(com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult qaBulkSearchResult);
    
    /**
     * Appends and returns a new empty "QABulkSearchResult" element
     */
    com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult addNewQABulkSearchResult();
    
    /**
     * An XML QABulkSearchResult(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QABulkSearchResult extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QABulkSearchResult.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qabulksearchresult5a5celemtype");
        
        /**
         * Gets array of all "BulkAddress" elements
         */
        com.qas.www.web_2010_04.QABulkSearchItemType[] getBulkAddressArray();
        
        /**
         * Gets ith "BulkAddress" element
         */
        com.qas.www.web_2010_04.QABulkSearchItemType getBulkAddressArray(int i);
        
        /**
         * Returns number of "BulkAddress" element
         */
        int sizeOfBulkAddressArray();
        
        /**
         * Sets array of all "BulkAddress" element
         */
        void setBulkAddressArray(com.qas.www.web_2010_04.QABulkSearchItemType[] bulkAddressArray);
        
        /**
         * Sets ith "BulkAddress" element
         */
        void setBulkAddressArray(int i, com.qas.www.web_2010_04.QABulkSearchItemType bulkAddress);
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "BulkAddress" element
         */
        com.qas.www.web_2010_04.QABulkSearchItemType insertNewBulkAddress(int i);
        
        /**
         * Appends and returns a new empty value (as xml) as the last "BulkAddress" element
         */
        com.qas.www.web_2010_04.QABulkSearchItemType addNewBulkAddress();
        
        /**
         * Removes the ith "BulkAddress" element
         */
        void removeBulkAddress(int i);
        
        /**
         * Gets the "BulkError" element
         */
        java.lang.String getBulkError();
        
        /**
         * Gets (as xml) the "BulkError" element
         */
        org.apache.xmlbeans.XmlString xgetBulkError();
        
        /**
         * True if has "BulkError" element
         */
        boolean isSetBulkError();
        
        /**
         * Sets the "BulkError" element
         */
        void setBulkError(java.lang.String bulkError);
        
        /**
         * Sets (as xml) the "BulkError" element
         */
        void xsetBulkError(org.apache.xmlbeans.XmlString bulkError);
        
        /**
         * Unsets the "BulkError" element
         */
        void unsetBulkError();
        
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
         * Gets the "Count" attribute
         */
        java.lang.String getCount();
        
        /**
         * Gets (as xml) the "Count" attribute
         */
        org.apache.xmlbeans.XmlString xgetCount();
        
        /**
         * True if has "Count" attribute
         */
        boolean isSetCount();
        
        /**
         * Sets the "Count" attribute
         */
        void setCount(java.lang.String count);
        
        /**
         * Sets (as xml) the "Count" attribute
         */
        void xsetCount(org.apache.xmlbeans.XmlString count);
        
        /**
         * Unsets the "Count" attribute
         */
        void unsetCount();
        
        /**
         * Gets the "SearchCount" attribute
         */
        java.lang.String getSearchCount();
        
        /**
         * Gets (as xml) the "SearchCount" attribute
         */
        org.apache.xmlbeans.XmlString xgetSearchCount();
        
        /**
         * True if has "SearchCount" attribute
         */
        boolean isSetSearchCount();
        
        /**
         * Sets the "SearchCount" attribute
         */
        void setSearchCount(java.lang.String searchCount);
        
        /**
         * Sets (as xml) the "SearchCount" attribute
         */
        void xsetSearchCount(org.apache.xmlbeans.XmlString searchCount);
        
        /**
         * Unsets the "SearchCount" attribute
         */
        void unsetSearchCount();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult newInstance() {
              return (com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QABulkSearchResultDocument.QABulkSearchResult) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument newInstance() {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QABulkSearchResultDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QABulkSearchResultDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
