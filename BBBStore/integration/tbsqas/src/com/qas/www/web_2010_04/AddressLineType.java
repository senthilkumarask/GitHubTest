/*
 * XML Type:  AddressLineType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.AddressLineType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * An XML AddressLineType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public interface AddressLineType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(AddressLineType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("addresslinetype24a0type");
    
    /**
     * Gets the "Label" element
     */
    java.lang.String getLabel();
    
    /**
     * Gets (as xml) the "Label" element
     */
    org.apache.xmlbeans.XmlString xgetLabel();
    
    /**
     * True if has "Label" element
     */
    boolean isSetLabel();
    
    /**
     * Sets the "Label" element
     */
    void setLabel(java.lang.String label);
    
    /**
     * Sets (as xml) the "Label" element
     */
    void xsetLabel(org.apache.xmlbeans.XmlString label);
    
    /**
     * Unsets the "Label" element
     */
    void unsetLabel();
    
    /**
     * Gets the "Line" element
     */
    java.lang.String getLine();
    
    /**
     * Gets (as xml) the "Line" element
     */
    org.apache.xmlbeans.XmlString xgetLine();
    
    /**
     * True if has "Line" element
     */
    boolean isSetLine();
    
    /**
     * Sets the "Line" element
     */
    void setLine(java.lang.String line);
    
    /**
     * Sets (as xml) the "Line" element
     */
    void xsetLine(org.apache.xmlbeans.XmlString line);
    
    /**
     * Unsets the "Line" element
     */
    void unsetLine();
    
    /**
     * Gets array of all "DataplusGroup" elements
     */
    com.qas.www.web_2010_04.DataplusGroupType[] getDataplusGroupArray();
    
    /**
     * Gets ith "DataplusGroup" element
     */
    com.qas.www.web_2010_04.DataplusGroupType getDataplusGroupArray(int i);
    
    /**
     * Returns number of "DataplusGroup" element
     */
    int sizeOfDataplusGroupArray();
    
    /**
     * Sets array of all "DataplusGroup" element
     */
    void setDataplusGroupArray(com.qas.www.web_2010_04.DataplusGroupType[] dataplusGroupArray);
    
    /**
     * Sets ith "DataplusGroup" element
     */
    void setDataplusGroupArray(int i, com.qas.www.web_2010_04.DataplusGroupType dataplusGroup);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "DataplusGroup" element
     */
    com.qas.www.web_2010_04.DataplusGroupType insertNewDataplusGroup(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "DataplusGroup" element
     */
    com.qas.www.web_2010_04.DataplusGroupType addNewDataplusGroup();
    
    /**
     * Removes the ith "DataplusGroup" element
     */
    void removeDataplusGroup(int i);
    
    /**
     * Gets the "LineContent" attribute
     */
    com.qas.www.web_2010_04.LineContentType.Enum getLineContent();
    
    /**
     * Gets (as xml) the "LineContent" attribute
     */
    com.qas.www.web_2010_04.LineContentType xgetLineContent();
    
    /**
     * True if has "LineContent" attribute
     */
    boolean isSetLineContent();
    
    /**
     * Sets the "LineContent" attribute
     */
    void setLineContent(com.qas.www.web_2010_04.LineContentType.Enum lineContent);
    
    /**
     * Sets (as xml) the "LineContent" attribute
     */
    void xsetLineContent(com.qas.www.web_2010_04.LineContentType lineContent);
    
    /**
     * Unsets the "LineContent" attribute
     */
    void unsetLineContent();
    
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
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.AddressLineType newInstance() {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.AddressLineType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.AddressLineType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.AddressLineType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.AddressLineType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.AddressLineType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.AddressLineType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.AddressLineType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
