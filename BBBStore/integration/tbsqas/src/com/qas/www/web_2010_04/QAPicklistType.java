/*
 * XML Type:  QAPicklistType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAPicklistType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * An XML QAPicklistType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public interface QAPicklistType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(QAPicklistType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("qapicklisttypea847type");
    
    /**
     * Gets the "FullPicklistMoniker" element
     */
    java.lang.String getFullPicklistMoniker();
    
    /**
     * Gets (as xml) the "FullPicklistMoniker" element
     */
    org.apache.xmlbeans.XmlString xgetFullPicklistMoniker();
    
    /**
     * Sets the "FullPicklistMoniker" element
     */
    void setFullPicklistMoniker(java.lang.String fullPicklistMoniker);
    
    /**
     * Sets (as xml) the "FullPicklistMoniker" element
     */
    void xsetFullPicklistMoniker(org.apache.xmlbeans.XmlString fullPicklistMoniker);
    
    /**
     * Gets array of all "PicklistEntry" elements
     */
    com.qas.www.web_2010_04.PicklistEntryType[] getPicklistEntryArray();
    
    /**
     * Gets ith "PicklistEntry" element
     */
    com.qas.www.web_2010_04.PicklistEntryType getPicklistEntryArray(int i);
    
    /**
     * Returns number of "PicklistEntry" element
     */
    int sizeOfPicklistEntryArray();
    
    /**
     * Sets array of all "PicklistEntry" element
     */
    void setPicklistEntryArray(com.qas.www.web_2010_04.PicklistEntryType[] picklistEntryArray);
    
    /**
     * Sets ith "PicklistEntry" element
     */
    void setPicklistEntryArray(int i, com.qas.www.web_2010_04.PicklistEntryType picklistEntry);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "PicklistEntry" element
     */
    com.qas.www.web_2010_04.PicklistEntryType insertNewPicklistEntry(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "PicklistEntry" element
     */
    com.qas.www.web_2010_04.PicklistEntryType addNewPicklistEntry();
    
    /**
     * Removes the ith "PicklistEntry" element
     */
    void removePicklistEntry(int i);
    
    /**
     * Gets the "Prompt" element
     */
    java.lang.String getPrompt();
    
    /**
     * Gets (as xml) the "Prompt" element
     */
    org.apache.xmlbeans.XmlString xgetPrompt();
    
    /**
     * Sets the "Prompt" element
     */
    void setPrompt(java.lang.String prompt);
    
    /**
     * Sets (as xml) the "Prompt" element
     */
    void xsetPrompt(org.apache.xmlbeans.XmlString prompt);
    
    /**
     * Gets the "Total" element
     */
    java.math.BigInteger getTotal();
    
    /**
     * Gets (as xml) the "Total" element
     */
    org.apache.xmlbeans.XmlNonNegativeInteger xgetTotal();
    
    /**
     * Sets the "Total" element
     */
    void setTotal(java.math.BigInteger total);
    
    /**
     * Sets (as xml) the "Total" element
     */
    void xsetTotal(org.apache.xmlbeans.XmlNonNegativeInteger total);
    
    /**
     * Gets the "AutoFormatSafe" attribute
     */
    boolean getAutoFormatSafe();
    
    /**
     * Gets (as xml) the "AutoFormatSafe" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetAutoFormatSafe();
    
    /**
     * True if has "AutoFormatSafe" attribute
     */
    boolean isSetAutoFormatSafe();
    
    /**
     * Sets the "AutoFormatSafe" attribute
     */
    void setAutoFormatSafe(boolean autoFormatSafe);
    
    /**
     * Sets (as xml) the "AutoFormatSafe" attribute
     */
    void xsetAutoFormatSafe(org.apache.xmlbeans.XmlBoolean autoFormatSafe);
    
    /**
     * Unsets the "AutoFormatSafe" attribute
     */
    void unsetAutoFormatSafe();
    
    /**
     * Gets the "AutoFormatPastClose" attribute
     */
    boolean getAutoFormatPastClose();
    
    /**
     * Gets (as xml) the "AutoFormatPastClose" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetAutoFormatPastClose();
    
    /**
     * True if has "AutoFormatPastClose" attribute
     */
    boolean isSetAutoFormatPastClose();
    
    /**
     * Sets the "AutoFormatPastClose" attribute
     */
    void setAutoFormatPastClose(boolean autoFormatPastClose);
    
    /**
     * Sets (as xml) the "AutoFormatPastClose" attribute
     */
    void xsetAutoFormatPastClose(org.apache.xmlbeans.XmlBoolean autoFormatPastClose);
    
    /**
     * Unsets the "AutoFormatPastClose" attribute
     */
    void unsetAutoFormatPastClose();
    
    /**
     * Gets the "AutoStepinSafe" attribute
     */
    boolean getAutoStepinSafe();
    
    /**
     * Gets (as xml) the "AutoStepinSafe" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetAutoStepinSafe();
    
    /**
     * True if has "AutoStepinSafe" attribute
     */
    boolean isSetAutoStepinSafe();
    
    /**
     * Sets the "AutoStepinSafe" attribute
     */
    void setAutoStepinSafe(boolean autoStepinSafe);
    
    /**
     * Sets (as xml) the "AutoStepinSafe" attribute
     */
    void xsetAutoStepinSafe(org.apache.xmlbeans.XmlBoolean autoStepinSafe);
    
    /**
     * Unsets the "AutoStepinSafe" attribute
     */
    void unsetAutoStepinSafe();
    
    /**
     * Gets the "AutoStepinPastClose" attribute
     */
    boolean getAutoStepinPastClose();
    
    /**
     * Gets (as xml) the "AutoStepinPastClose" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetAutoStepinPastClose();
    
    /**
     * True if has "AutoStepinPastClose" attribute
     */
    boolean isSetAutoStepinPastClose();
    
    /**
     * Sets the "AutoStepinPastClose" attribute
     */
    void setAutoStepinPastClose(boolean autoStepinPastClose);
    
    /**
     * Sets (as xml) the "AutoStepinPastClose" attribute
     */
    void xsetAutoStepinPastClose(org.apache.xmlbeans.XmlBoolean autoStepinPastClose);
    
    /**
     * Unsets the "AutoStepinPastClose" attribute
     */
    void unsetAutoStepinPastClose();
    
    /**
     * Gets the "LargePotential" attribute
     */
    boolean getLargePotential();
    
    /**
     * Gets (as xml) the "LargePotential" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetLargePotential();
    
    /**
     * True if has "LargePotential" attribute
     */
    boolean isSetLargePotential();
    
    /**
     * Sets the "LargePotential" attribute
     */
    void setLargePotential(boolean largePotential);
    
    /**
     * Sets (as xml) the "LargePotential" attribute
     */
    void xsetLargePotential(org.apache.xmlbeans.XmlBoolean largePotential);
    
    /**
     * Unsets the "LargePotential" attribute
     */
    void unsetLargePotential();
    
    /**
     * Gets the "MaxMatches" attribute
     */
    boolean getMaxMatches();
    
    /**
     * Gets (as xml) the "MaxMatches" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetMaxMatches();
    
    /**
     * True if has "MaxMatches" attribute
     */
    boolean isSetMaxMatches();
    
    /**
     * Sets the "MaxMatches" attribute
     */
    void setMaxMatches(boolean maxMatches);
    
    /**
     * Sets (as xml) the "MaxMatches" attribute
     */
    void xsetMaxMatches(org.apache.xmlbeans.XmlBoolean maxMatches);
    
    /**
     * Unsets the "MaxMatches" attribute
     */
    void unsetMaxMatches();
    
    /**
     * Gets the "MoreOtherMatches" attribute
     */
    boolean getMoreOtherMatches();
    
    /**
     * Gets (as xml) the "MoreOtherMatches" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetMoreOtherMatches();
    
    /**
     * True if has "MoreOtherMatches" attribute
     */
    boolean isSetMoreOtherMatches();
    
    /**
     * Sets the "MoreOtherMatches" attribute
     */
    void setMoreOtherMatches(boolean moreOtherMatches);
    
    /**
     * Sets (as xml) the "MoreOtherMatches" attribute
     */
    void xsetMoreOtherMatches(org.apache.xmlbeans.XmlBoolean moreOtherMatches);
    
    /**
     * Unsets the "MoreOtherMatches" attribute
     */
    void unsetMoreOtherMatches();
    
    /**
     * Gets the "OverThreshold" attribute
     */
    boolean getOverThreshold();
    
    /**
     * Gets (as xml) the "OverThreshold" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetOverThreshold();
    
    /**
     * True if has "OverThreshold" attribute
     */
    boolean isSetOverThreshold();
    
    /**
     * Sets the "OverThreshold" attribute
     */
    void setOverThreshold(boolean overThreshold);
    
    /**
     * Sets (as xml) the "OverThreshold" attribute
     */
    void xsetOverThreshold(org.apache.xmlbeans.XmlBoolean overThreshold);
    
    /**
     * Unsets the "OverThreshold" attribute
     */
    void unsetOverThreshold();
    
    /**
     * Gets the "Timeout" attribute
     */
    boolean getTimeout();
    
    /**
     * Gets (as xml) the "Timeout" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetTimeout();
    
    /**
     * True if has "Timeout" attribute
     */
    boolean isSetTimeout();
    
    /**
     * Sets the "Timeout" attribute
     */
    void setTimeout(boolean timeout);
    
    /**
     * Sets (as xml) the "Timeout" attribute
     */
    void xsetTimeout(org.apache.xmlbeans.XmlBoolean timeout);
    
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
        public static com.qas.www.web_2010_04.QAPicklistType newInstance() {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.QAPicklistType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QAPicklistType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.QAPicklistType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.QAPicklistType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
