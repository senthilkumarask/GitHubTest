/*
 * XML Type:  QAAddressType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAAddressType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * An XML QAAddressType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public interface QAAddressType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QAAddressType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qaaddresstype2384type");
    
    /**
     * Gets array of all "AddressLine" elements
     */
    com.qas.www.web_2010_04.AddressLineType[] getAddressLineArray();
    
    /**
     * Gets ith "AddressLine" element
     */
    com.qas.www.web_2010_04.AddressLineType getAddressLineArray(int i);
    
    /**
     * Returns number of "AddressLine" element
     */
    int sizeOfAddressLineArray();
    
    /**
     * Sets array of all "AddressLine" element
     */
    void setAddressLineArray(com.qas.www.web_2010_04.AddressLineType[] addressLineArray);
    
    /**
     * Sets ith "AddressLine" element
     */
    void setAddressLineArray(int i, com.qas.www.web_2010_04.AddressLineType addressLine);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "AddressLine" element
     */
    com.qas.www.web_2010_04.AddressLineType insertNewAddressLine(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "AddressLine" element
     */
    com.qas.www.web_2010_04.AddressLineType addNewAddressLine();
    
    /**
     * Removes the ith "AddressLine" element
     */
    void removeAddressLine(int i);
    
    /**
     * Gets the "Overflow" attribute
     */
    boolean getOverflow();
    
    /**
     * Gets (as xml) the "Overflow" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetOverflow();
    
    /**
     * True if has "Overflow" attribute
     */
    boolean isSetOverflow();
    
    /**
     * Sets the "Overflow" attribute
     */
    void setOverflow(boolean overflow);
    
    /**
     * Sets (as xml) the "Overflow" attribute
     */
    void xsetOverflow(org.apache.xmlbeans.XmlBoolean overflow);
    
    /**
     * Unsets the "Overflow" attribute
     */
    void unsetOverflow();
    
    /**
     * Gets the "Truncated" attribute
     */
    boolean getTruncated();
    
    /**
     * Gets (as xml) the "Truncated" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetTruncated();
    
    /**
     * True if has "Truncated" attribute
     */
    boolean isSetTruncated();
    
    /**
     * Sets the "Truncated" attribute
     */
    void setTruncated(boolean truncated);
    
    /**
     * Sets (as xml) the "Truncated" attribute
     */
    void xsetTruncated(org.apache.xmlbeans.XmlBoolean truncated);
    
    /**
     * Unsets the "Truncated" attribute
     */
    void unsetTruncated();
    
    /**
     * Gets the "DPVStatus" attribute
     */
    com.qas.www.web_2010_04.DPVStatusType.Enum getDPVStatus();
    
    /**
     * Gets (as xml) the "DPVStatus" attribute
     */
    com.qas.www.web_2010_04.DPVStatusType xgetDPVStatus();
    
    /**
     * True if has "DPVStatus" attribute
     */
    boolean isSetDPVStatus();
    
    /**
     * Sets the "DPVStatus" attribute
     */
    void setDPVStatus(com.qas.www.web_2010_04.DPVStatusType.Enum dpvStatus);
    
    /**
     * Sets (as xml) the "DPVStatus" attribute
     */
    void xsetDPVStatus(com.qas.www.web_2010_04.DPVStatusType dpvStatus);
    
    /**
     * Unsets the "DPVStatus" attribute
     */
    void unsetDPVStatus();
    
    /**
     * Gets the "MissingSubPremise" attribute
     */
    boolean getMissingSubPremise();
    
    /**
     * Gets (as xml) the "MissingSubPremise" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetMissingSubPremise();
    
    /**
     * True if has "MissingSubPremise" attribute
     */
    boolean isSetMissingSubPremise();
    
    /**
     * Sets the "MissingSubPremise" attribute
     */
    void setMissingSubPremise(boolean missingSubPremise);
    
    /**
     * Sets (as xml) the "MissingSubPremise" attribute
     */
    void xsetMissingSubPremise(org.apache.xmlbeans.XmlBoolean missingSubPremise);
    
    /**
     * Unsets the "MissingSubPremise" attribute
     */
    void unsetMissingSubPremise();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.QAAddressType newInstance() {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QAAddressType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QAAddressType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QAAddressType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QAAddressType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QAAddressType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QAAddressType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QAAddressType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
