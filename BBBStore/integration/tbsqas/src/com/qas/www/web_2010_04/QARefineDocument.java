/*
 * An XML document type.
 * Localname: QARefine
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QARefineDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * A document containing one QARefine(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public interface QARefineDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QARefineDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qarefined0a3doctype");
    
    /**
     * Gets the "QARefine" element
     */
    com.qas.www.web_2010_04.QARefineDocument.QARefine getQARefine();
    
    /**
     * Sets the "QARefine" element
     */
    void setQARefine(com.qas.www.web_2010_04.QARefineDocument.QARefine qaRefine);
    
    /**
     * Appends and returns a new empty "QARefine" element
     */
    com.qas.www.web_2010_04.QARefineDocument.QARefine addNewQARefine();
    
    /**
     * An XML QARefine(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public interface QARefine extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QARefine.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qarefine069celemtype");
        
        /**
         * Gets the "Moniker" element
         */
        java.lang.String getMoniker();
        
        /**
         * Gets (as xml) the "Moniker" element
         */
        org.apache.xmlbeans.XmlString xgetMoniker();
        
        /**
         * Sets the "Moniker" element
         */
        void setMoniker(java.lang.String moniker);
        
        /**
         * Sets (as xml) the "Moniker" element
         */
        void xsetMoniker(org.apache.xmlbeans.XmlString moniker);
        
        /**
         * Gets the "Refinement" element
         */
        java.lang.String getRefinement();
        
        /**
         * Gets (as xml) the "Refinement" element
         */
        org.apache.xmlbeans.XmlString xgetRefinement();
        
        /**
         * True if has "Refinement" element
         */
        boolean isSetRefinement();
        
        /**
         * Sets the "Refinement" element
         */
        void setRefinement(java.lang.String refinement);
        
        /**
         * Sets (as xml) the "Refinement" element
         */
        void xsetRefinement(org.apache.xmlbeans.XmlString refinement);
        
        /**
         * Unsets the "Refinement" element
         */
        void unsetRefinement();
        
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
         * Gets the "Threshold" attribute
         */
        int getThreshold();
        
        /**
         * Gets (as xml) the "Threshold" attribute
         */
        com.qas.www.web_2010_04.ThresholdType xgetThreshold();
        
        /**
         * True if has "Threshold" attribute
         */
        boolean isSetThreshold();
        
        /**
         * Sets the "Threshold" attribute
         */
        void setThreshold(int threshold);
        
        /**
         * Sets (as xml) the "Threshold" attribute
         */
        void xsetThreshold(com.qas.www.web_2010_04.ThresholdType threshold);
        
        /**
         * Unsets the "Threshold" attribute
         */
        void unsetThreshold();
        
        /**
         * Gets the "Timeout" attribute
         */
        int getTimeout();
        
        /**
         * Gets (as xml) the "Timeout" attribute
         */
        com.qas.www.web_2010_04.TimeoutType xgetTimeout();
        
        /**
         * True if has "Timeout" attribute
         */
        boolean isSetTimeout();
        
        /**
         * Sets the "Timeout" attribute
         */
        void setTimeout(int timeout);
        
        /**
         * Sets (as xml) the "Timeout" attribute
         */
        void xsetTimeout(com.qas.www.web_2010_04.TimeoutType timeout);
        
        /**
         * Unsets the "Timeout" attribute
         */
        void unsetTimeout();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static com.qas.www.web_2010_04.QARefineDocument.QARefine newInstance() {
              return (com.qas.www.web_2010_04.QARefineDocument.QARefine) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static com.qas.www.web_2010_04.QARefineDocument.QARefine newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (com.qas.www.web_2010_04.QARefineDocument.QARefine) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QARefineDocument newInstance() {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QARefineDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QARefineDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QARefineDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QARefineDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
