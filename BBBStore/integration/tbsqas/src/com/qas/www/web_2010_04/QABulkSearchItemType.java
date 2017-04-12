/*
 * XML Type:  QABulkSearchItemType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QABulkSearchItemType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * An XML QABulkSearchItemType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public interface QABulkSearchItemType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QABulkSearchItemType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qabulksearchitemtype8cf9type");
    
    /**
     * Gets the "QAAddress" element
     */
    com.qas.www.web_2010_04.QAAddressType getQAAddress();
    
    /**
     * Sets the "QAAddress" element
     */
    void setQAAddress(com.qas.www.web_2010_04.QAAddressType qaAddress);
    
    /**
     * Appends and returns a new empty "QAAddress" element
     */
    com.qas.www.web_2010_04.QAAddressType addNewQAAddress();
    
    /**
     * Gets the "InputAddress" element
     */
    java.lang.String getInputAddress();
    
    /**
     * Gets (as xml) the "InputAddress" element
     */
    org.apache.xmlbeans.XmlString xgetInputAddress();
    
    /**
     * Sets the "InputAddress" element
     */
    void setInputAddress(java.lang.String inputAddress);
    
    /**
     * Sets (as xml) the "InputAddress" element
     */
    void xsetInputAddress(org.apache.xmlbeans.XmlString inputAddress);
    
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
        public static com.qas.www.web_2010_04.QABulkSearchItemType newInstance() {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QABulkSearchItemType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QABulkSearchItemType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
