/*
 * XML Type:  DataplusGroupType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.DataplusGroupType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * An XML DataplusGroupType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public interface DataplusGroupType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DataplusGroupType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("dataplusgrouptype62edtype");
    
    /**
     * Gets array of all "DataplusGroupItem" elements
     */
    java.lang.String[] getDataplusGroupItemArray();
    
    /**
     * Gets ith "DataplusGroupItem" element
     */
    java.lang.String getDataplusGroupItemArray(int i);
    
    /**
     * Gets (as xml) array of all "DataplusGroupItem" elements
     */
    org.apache.xmlbeans.XmlString[] xgetDataplusGroupItemArray();
    
    /**
     * Gets (as xml) ith "DataplusGroupItem" element
     */
    org.apache.xmlbeans.XmlString xgetDataplusGroupItemArray(int i);
    
    /**
     * Returns number of "DataplusGroupItem" element
     */
    int sizeOfDataplusGroupItemArray();
    
    /**
     * Sets array of all "DataplusGroupItem" element
     */
    void setDataplusGroupItemArray(java.lang.String[] dataplusGroupItemArray);
    
    /**
     * Sets ith "DataplusGroupItem" element
     */
    void setDataplusGroupItemArray(int i, java.lang.String dataplusGroupItem);
    
    /**
     * Sets (as xml) array of all "DataplusGroupItem" element
     */
    void xsetDataplusGroupItemArray(org.apache.xmlbeans.XmlString[] dataplusGroupItemArray);
    
    /**
     * Sets (as xml) ith "DataplusGroupItem" element
     */
    void xsetDataplusGroupItemArray(int i, org.apache.xmlbeans.XmlString dataplusGroupItem);
    
    /**
     * Inserts the value as the ith "DataplusGroupItem" element
     */
    void insertDataplusGroupItem(int i, java.lang.String dataplusGroupItem);
    
    /**
     * Appends the value as the last "DataplusGroupItem" element
     */
    void addDataplusGroupItem(java.lang.String dataplusGroupItem);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "DataplusGroupItem" element
     */
    org.apache.xmlbeans.XmlString insertNewDataplusGroupItem(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "DataplusGroupItem" element
     */
    org.apache.xmlbeans.XmlString addNewDataplusGroupItem();
    
    /**
     * Removes the ith "DataplusGroupItem" element
     */
    void removeDataplusGroupItem(int i);
    
    /**
     * Gets the "GroupName" attribute
     */
    java.lang.String getGroupName();
    
    /**
     * Gets (as xml) the "GroupName" attribute
     */
    org.apache.xmlbeans.XmlString xgetGroupName();
    
    /**
     * True if has "GroupName" attribute
     */
    boolean isSetGroupName();
    
    /**
     * Sets the "GroupName" attribute
     */
    void setGroupName(java.lang.String groupName);
    
    /**
     * Sets (as xml) the "GroupName" attribute
     */
    void xsetGroupName(org.apache.xmlbeans.XmlString groupName);
    
    /**
     * Unsets the "GroupName" attribute
     */
    void unsetGroupName();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.DataplusGroupType newInstance() {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.DataplusGroupType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.DataplusGroupType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.DataplusGroupType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.DataplusGroupType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
