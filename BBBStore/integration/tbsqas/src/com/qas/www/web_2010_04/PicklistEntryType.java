/*
 * XML Type:  PicklistEntryType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.PicklistEntryType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * An XML PicklistEntryType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public interface PicklistEntryType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(PicklistEntryType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("picklistentrytypefd15type");
    
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
     * Gets the "PartialAddress" element
     */
    java.lang.String getPartialAddress();
    
    /**
     * Gets (as xml) the "PartialAddress" element
     */
    org.apache.xmlbeans.XmlString xgetPartialAddress();
    
    /**
     * Sets the "PartialAddress" element
     */
    void setPartialAddress(java.lang.String partialAddress);
    
    /**
     * Sets (as xml) the "PartialAddress" element
     */
    void xsetPartialAddress(org.apache.xmlbeans.XmlString partialAddress);
    
    /**
     * Gets the "Picklist" element
     */
    java.lang.String getPicklist();
    
    /**
     * Gets (as xml) the "Picklist" element
     */
    org.apache.xmlbeans.XmlString xgetPicklist();
    
    /**
     * Sets the "Picklist" element
     */
    void setPicklist(java.lang.String picklist);
    
    /**
     * Sets (as xml) the "Picklist" element
     */
    void xsetPicklist(org.apache.xmlbeans.XmlString picklist);
    
    /**
     * Gets the "Postcode" element
     */
    java.lang.String getPostcode();
    
    /**
     * Gets (as xml) the "Postcode" element
     */
    org.apache.xmlbeans.XmlString xgetPostcode();
    
    /**
     * Sets the "Postcode" element
     */
    void setPostcode(java.lang.String postcode);
    
    /**
     * Sets (as xml) the "Postcode" element
     */
    void xsetPostcode(org.apache.xmlbeans.XmlString postcode);
    
    /**
     * Gets the "Score" element
     */
    java.math.BigInteger getScore();
    
    /**
     * Gets (as xml) the "Score" element
     */
    org.apache.xmlbeans.XmlNonNegativeInteger xgetScore();
    
    /**
     * Sets the "Score" element
     */
    void setScore(java.math.BigInteger score);
    
    /**
     * Sets (as xml) the "Score" element
     */
    void xsetScore(org.apache.xmlbeans.XmlNonNegativeInteger score);
    
    /**
     * Gets the "QAAddress" element
     */
    com.qas.www.web_2010_04.QAAddressType getQAAddress();
    
    /**
     * True if has "QAAddress" element
     */
    boolean isSetQAAddress();
    
    /**
     * Sets the "QAAddress" element
     */
    void setQAAddress(com.qas.www.web_2010_04.QAAddressType qaAddress);
    
    /**
     * Appends and returns a new empty "QAAddress" element
     */
    com.qas.www.web_2010_04.QAAddressType addNewQAAddress();
    
    /**
     * Unsets the "QAAddress" element
     */
    void unsetQAAddress();
    
    /**
     * Gets the "FullAddress" attribute
     */
    boolean getFullAddress();
    
    /**
     * Gets (as xml) the "FullAddress" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetFullAddress();
    
    /**
     * True if has "FullAddress" attribute
     */
    boolean isSetFullAddress();
    
    /**
     * Sets the "FullAddress" attribute
     */
    void setFullAddress(boolean fullAddress);
    
    /**
     * Sets (as xml) the "FullAddress" attribute
     */
    void xsetFullAddress(org.apache.xmlbeans.XmlBoolean fullAddress);
    
    /**
     * Unsets the "FullAddress" attribute
     */
    void unsetFullAddress();
    
    /**
     * Gets the "Multiples" attribute
     */
    boolean getMultiples();
    
    /**
     * Gets (as xml) the "Multiples" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetMultiples();
    
    /**
     * True if has "Multiples" attribute
     */
    boolean isSetMultiples();
    
    /**
     * Sets the "Multiples" attribute
     */
    void setMultiples(boolean multiples);
    
    /**
     * Sets (as xml) the "Multiples" attribute
     */
    void xsetMultiples(org.apache.xmlbeans.XmlBoolean multiples);
    
    /**
     * Unsets the "Multiples" attribute
     */
    void unsetMultiples();
    
    /**
     * Gets the "CanStep" attribute
     */
    boolean getCanStep();
    
    /**
     * Gets (as xml) the "CanStep" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetCanStep();
    
    /**
     * True if has "CanStep" attribute
     */
    boolean isSetCanStep();
    
    /**
     * Sets the "CanStep" attribute
     */
    void setCanStep(boolean canStep);
    
    /**
     * Sets (as xml) the "CanStep" attribute
     */
    void xsetCanStep(org.apache.xmlbeans.XmlBoolean canStep);
    
    /**
     * Unsets the "CanStep" attribute
     */
    void unsetCanStep();
    
    /**
     * Gets the "AliasMatch" attribute
     */
    boolean getAliasMatch();
    
    /**
     * Gets (as xml) the "AliasMatch" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetAliasMatch();
    
    /**
     * True if has "AliasMatch" attribute
     */
    boolean isSetAliasMatch();
    
    /**
     * Sets the "AliasMatch" attribute
     */
    void setAliasMatch(boolean aliasMatch);
    
    /**
     * Sets (as xml) the "AliasMatch" attribute
     */
    void xsetAliasMatch(org.apache.xmlbeans.XmlBoolean aliasMatch);
    
    /**
     * Unsets the "AliasMatch" attribute
     */
    void unsetAliasMatch();
    
    /**
     * Gets the "PostcodeRecoded" attribute
     */
    boolean getPostcodeRecoded();
    
    /**
     * Gets (as xml) the "PostcodeRecoded" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetPostcodeRecoded();
    
    /**
     * True if has "PostcodeRecoded" attribute
     */
    boolean isSetPostcodeRecoded();
    
    /**
     * Sets the "PostcodeRecoded" attribute
     */
    void setPostcodeRecoded(boolean postcodeRecoded);
    
    /**
     * Sets (as xml) the "PostcodeRecoded" attribute
     */
    void xsetPostcodeRecoded(org.apache.xmlbeans.XmlBoolean postcodeRecoded);
    
    /**
     * Unsets the "PostcodeRecoded" attribute
     */
    void unsetPostcodeRecoded();
    
    /**
     * Gets the "CrossBorderMatch" attribute
     */
    boolean getCrossBorderMatch();
    
    /**
     * Gets (as xml) the "CrossBorderMatch" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetCrossBorderMatch();
    
    /**
     * True if has "CrossBorderMatch" attribute
     */
    boolean isSetCrossBorderMatch();
    
    /**
     * Sets the "CrossBorderMatch" attribute
     */
    void setCrossBorderMatch(boolean crossBorderMatch);
    
    /**
     * Sets (as xml) the "CrossBorderMatch" attribute
     */
    void xsetCrossBorderMatch(org.apache.xmlbeans.XmlBoolean crossBorderMatch);
    
    /**
     * Unsets the "CrossBorderMatch" attribute
     */
    void unsetCrossBorderMatch();
    
    /**
     * Gets the "DummyPOBox" attribute
     */
    boolean getDummyPOBox();
    
    /**
     * Gets (as xml) the "DummyPOBox" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetDummyPOBox();
    
    /**
     * True if has "DummyPOBox" attribute
     */
    boolean isSetDummyPOBox();
    
    /**
     * Sets the "DummyPOBox" attribute
     */
    void setDummyPOBox(boolean dummyPOBox);
    
    /**
     * Sets (as xml) the "DummyPOBox" attribute
     */
    void xsetDummyPOBox(org.apache.xmlbeans.XmlBoolean dummyPOBox);
    
    /**
     * Unsets the "DummyPOBox" attribute
     */
    void unsetDummyPOBox();
    
    /**
     * Gets the "Name" attribute
     */
    boolean getName();
    
    /**
     * Gets (as xml) the "Name" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetName();
    
    /**
     * True if has "Name" attribute
     */
    boolean isSetName();
    
    /**
     * Sets the "Name" attribute
     */
    void setName(boolean name);
    
    /**
     * Sets (as xml) the "Name" attribute
     */
    void xsetName(org.apache.xmlbeans.XmlBoolean name);
    
    /**
     * Unsets the "Name" attribute
     */
    void unsetName();
    
    /**
     * Gets the "Information" attribute
     */
    boolean getInformation();
    
    /**
     * Gets (as xml) the "Information" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetInformation();
    
    /**
     * True if has "Information" attribute
     */
    boolean isSetInformation();
    
    /**
     * Sets the "Information" attribute
     */
    void setInformation(boolean information);
    
    /**
     * Sets (as xml) the "Information" attribute
     */
    void xsetInformation(org.apache.xmlbeans.XmlBoolean information);
    
    /**
     * Unsets the "Information" attribute
     */
    void unsetInformation();
    
    /**
     * Gets the "WarnInformation" attribute
     */
    boolean getWarnInformation();
    
    /**
     * Gets (as xml) the "WarnInformation" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetWarnInformation();
    
    /**
     * True if has "WarnInformation" attribute
     */
    boolean isSetWarnInformation();
    
    /**
     * Sets the "WarnInformation" attribute
     */
    void setWarnInformation(boolean warnInformation);
    
    /**
     * Sets (as xml) the "WarnInformation" attribute
     */
    void xsetWarnInformation(org.apache.xmlbeans.XmlBoolean warnInformation);
    
    /**
     * Unsets the "WarnInformation" attribute
     */
    void unsetWarnInformation();
    
    /**
     * Gets the "IncompleteAddr" attribute
     */
    boolean getIncompleteAddr();
    
    /**
     * Gets (as xml) the "IncompleteAddr" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetIncompleteAddr();
    
    /**
     * True if has "IncompleteAddr" attribute
     */
    boolean isSetIncompleteAddr();
    
    /**
     * Sets the "IncompleteAddr" attribute
     */
    void setIncompleteAddr(boolean incompleteAddr);
    
    /**
     * Sets (as xml) the "IncompleteAddr" attribute
     */
    void xsetIncompleteAddr(org.apache.xmlbeans.XmlBoolean incompleteAddr);
    
    /**
     * Unsets the "IncompleteAddr" attribute
     */
    void unsetIncompleteAddr();
    
    /**
     * Gets the "UnresolvableRange" attribute
     */
    boolean getUnresolvableRange();
    
    /**
     * Gets (as xml) the "UnresolvableRange" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetUnresolvableRange();
    
    /**
     * True if has "UnresolvableRange" attribute
     */
    boolean isSetUnresolvableRange();
    
    /**
     * Sets the "UnresolvableRange" attribute
     */
    void setUnresolvableRange(boolean unresolvableRange);
    
    /**
     * Sets (as xml) the "UnresolvableRange" attribute
     */
    void xsetUnresolvableRange(org.apache.xmlbeans.XmlBoolean unresolvableRange);
    
    /**
     * Unsets the "UnresolvableRange" attribute
     */
    void unsetUnresolvableRange();
    
    /**
     * Gets the "PhantomPrimaryPoint" attribute
     */
    boolean getPhantomPrimaryPoint();
    
    /**
     * Gets (as xml) the "PhantomPrimaryPoint" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetPhantomPrimaryPoint();
    
    /**
     * True if has "PhantomPrimaryPoint" attribute
     */
    boolean isSetPhantomPrimaryPoint();
    
    /**
     * Sets the "PhantomPrimaryPoint" attribute
     */
    void setPhantomPrimaryPoint(boolean phantomPrimaryPoint);
    
    /**
     * Sets (as xml) the "PhantomPrimaryPoint" attribute
     */
    void xsetPhantomPrimaryPoint(org.apache.xmlbeans.XmlBoolean phantomPrimaryPoint);
    
    /**
     * Unsets the "PhantomPrimaryPoint" attribute
     */
    void unsetPhantomPrimaryPoint();
    
    /**
     * Gets the "SubsidiaryData" attribute
     */
    boolean getSubsidiaryData();
    
    /**
     * Gets (as xml) the "SubsidiaryData" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetSubsidiaryData();
    
    /**
     * True if has "SubsidiaryData" attribute
     */
    boolean isSetSubsidiaryData();
    
    /**
     * Sets the "SubsidiaryData" attribute
     */
    void setSubsidiaryData(boolean subsidiaryData);
    
    /**
     * Sets (as xml) the "SubsidiaryData" attribute
     */
    void xsetSubsidiaryData(org.apache.xmlbeans.XmlBoolean subsidiaryData);
    
    /**
     * Unsets the "SubsidiaryData" attribute
     */
    void unsetSubsidiaryData();
    
    /**
     * Gets the "ExtendedData" attribute
     */
    boolean getExtendedData();
    
    /**
     * Gets (as xml) the "ExtendedData" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetExtendedData();
    
    /**
     * True if has "ExtendedData" attribute
     */
    boolean isSetExtendedData();
    
    /**
     * Sets the "ExtendedData" attribute
     */
    void setExtendedData(boolean extendedData);
    
    /**
     * Sets (as xml) the "ExtendedData" attribute
     */
    void xsetExtendedData(org.apache.xmlbeans.XmlBoolean extendedData);
    
    /**
     * Unsets the "ExtendedData" attribute
     */
    void unsetExtendedData();
    
    /**
     * Gets the "EnhancedData" attribute
     */
    boolean getEnhancedData();
    
    /**
     * Gets (as xml) the "EnhancedData" attribute
     */
    org.apache.xmlbeans.XmlBoolean xgetEnhancedData();
    
    /**
     * True if has "EnhancedData" attribute
     */
    boolean isSetEnhancedData();
    
    /**
     * Sets the "EnhancedData" attribute
     */
    void setEnhancedData(boolean enhancedData);
    
    /**
     * Sets (as xml) the "EnhancedData" attribute
     */
    void xsetEnhancedData(org.apache.xmlbeans.XmlBoolean enhancedData);
    
    /**
     * Unsets the "EnhancedData" attribute
     */
    void unsetEnhancedData();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.PicklistEntryType newInstance() {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.PicklistEntryType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.PicklistEntryType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.PicklistEntryType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.PicklistEntryType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
