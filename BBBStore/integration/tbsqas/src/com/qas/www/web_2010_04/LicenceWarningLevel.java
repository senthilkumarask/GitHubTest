/*
 * XML Type:  LicenceWarningLevel
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.LicenceWarningLevel
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04;


/**
 * An XML LicenceWarningLevel(@http://www.qas.com/web-2010-04).
 *
 * This is an atomic type that is a restriction of com.qas.www.web_2010_04.LicenceWarningLevel.
 */
public interface LicenceWarningLevel extends org.apache.xmlbeans.XmlString
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(LicenceWarningLevel.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sEE3B9D7CF6F5B2D0EB3FF60201002EF8").resolveHandle("licencewarninglevele069type");
    
    org.apache.xmlbeans.StringEnumAbstractBase enumValue();
    void set(org.apache.xmlbeans.StringEnumAbstractBase e);
    
    static final Enum NONE = Enum.forString("None");
    static final Enum DATA_EXPIRING = Enum.forString("DataExpiring");
    static final Enum LICENCE_EXPIRING = Enum.forString("LicenceExpiring");
    static final Enum CLICKS_LOW = Enum.forString("ClicksLow");
    static final Enum EVALUATION = Enum.forString("Evaluation");
    static final Enum NO_CLICKS = Enum.forString("NoClicks");
    static final Enum DATA_EXPIRED = Enum.forString("DataExpired");
    static final Enum EVAL_LICENCE_EXPIRED = Enum.forString("EvalLicenceExpired");
    static final Enum FULL_LICENCE_EXPIRED = Enum.forString("FullLicenceExpired");
    static final Enum LICENCE_NOT_FOUND = Enum.forString("LicenceNotFound");
    static final Enum DATA_UNREADABLE = Enum.forString("DataUnreadable");
    
    static final int INT_NONE = Enum.INT_NONE;
    static final int INT_DATA_EXPIRING = Enum.INT_DATA_EXPIRING;
    static final int INT_LICENCE_EXPIRING = Enum.INT_LICENCE_EXPIRING;
    static final int INT_CLICKS_LOW = Enum.INT_CLICKS_LOW;
    static final int INT_EVALUATION = Enum.INT_EVALUATION;
    static final int INT_NO_CLICKS = Enum.INT_NO_CLICKS;
    static final int INT_DATA_EXPIRED = Enum.INT_DATA_EXPIRED;
    static final int INT_EVAL_LICENCE_EXPIRED = Enum.INT_EVAL_LICENCE_EXPIRED;
    static final int INT_FULL_LICENCE_EXPIRED = Enum.INT_FULL_LICENCE_EXPIRED;
    static final int INT_LICENCE_NOT_FOUND = Enum.INT_LICENCE_NOT_FOUND;
    static final int INT_DATA_UNREADABLE = Enum.INT_DATA_UNREADABLE;
    
    /**
     * Enumeration value class for com.qas.www.web_2010_04.LicenceWarningLevel.
     * These enum values can be used as follows:
     * <pre>
     * enum.toString(); // returns the string value of the enum
     * enum.intValue(); // returns an int value, useful for switches
     * // e.g., case Enum.INT_NONE
     * Enum.forString(s); // returns the enum value for a string
     * Enum.forInt(i); // returns the enum value for an int
     * </pre>
     * Enumeration objects are immutable singleton objects that
     * can be compared using == object equality. They have no
     * public constructor. See the constants defined within this
     * class for all the valid values.
     */
    static final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase
    {
        /**
         * Returns the enum value for a string, or null if none.
         */
        public static Enum forString(java.lang.String s)
            { return (Enum)table.forString(s); }
        /**
         * Returns the enum value corresponding to an int, or null if none.
         */
        public static Enum forInt(int i)
            { return (Enum)table.forInt(i); }
        
        private Enum(java.lang.String s, int i)
            { super(s, i); }
        
        static final int INT_NONE = 1;
        static final int INT_DATA_EXPIRING = 2;
        static final int INT_LICENCE_EXPIRING = 3;
        static final int INT_CLICKS_LOW = 4;
        static final int INT_EVALUATION = 5;
        static final int INT_NO_CLICKS = 6;
        static final int INT_DATA_EXPIRED = 7;
        static final int INT_EVAL_LICENCE_EXPIRED = 8;
        static final int INT_FULL_LICENCE_EXPIRED = 9;
        static final int INT_LICENCE_NOT_FOUND = 10;
        static final int INT_DATA_UNREADABLE = 11;
        
        public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
            new org.apache.xmlbeans.StringEnumAbstractBase.Table
        (
            new Enum[]
            {
                new Enum("None", INT_NONE),
                new Enum("DataExpiring", INT_DATA_EXPIRING),
                new Enum("LicenceExpiring", INT_LICENCE_EXPIRING),
                new Enum("ClicksLow", INT_CLICKS_LOW),
                new Enum("Evaluation", INT_EVALUATION),
                new Enum("NoClicks", INT_NO_CLICKS),
                new Enum("DataExpired", INT_DATA_EXPIRED),
                new Enum("EvalLicenceExpired", INT_EVAL_LICENCE_EXPIRED),
                new Enum("FullLicenceExpired", INT_FULL_LICENCE_EXPIRED),
                new Enum("LicenceNotFound", INT_LICENCE_NOT_FOUND),
                new Enum("DataUnreadable", INT_DATA_UNREADABLE),
            }
        );
        private static final long serialVersionUID = 1L;
        private java.lang.Object readResolve() { return forInt(intValue()); } 
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static com.qas.www.web_2010_04.LicenceWarningLevel newValue(java.lang.Object obj) {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) type.newValue( obj ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel newInstance() {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static com.qas.www.web_2010_04.LicenceWarningLevel parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (com.qas.www.web_2010_04.LicenceWarningLevel) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
