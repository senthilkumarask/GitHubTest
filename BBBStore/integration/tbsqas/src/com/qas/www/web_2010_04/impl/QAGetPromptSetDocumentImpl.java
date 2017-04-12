/*
 * An XML document type.
 * Localname: QAGetPromptSet
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QAGetPromptSetDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QAGetPromptSet(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QAGetPromptSetDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetPromptSetDocument
{
    
    public QAGetPromptSetDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAGETPROMPTSET$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAGetPromptSet");
    
    
    /**
     * Gets the "QAGetPromptSet" element
     */
    public com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet getQAGetPromptSet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet target = null;
            target = (com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet)get_store().find_element_user(QAGETPROMPTSET$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QAGetPromptSet" element
     */
    public void setQAGetPromptSet(com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet qaGetPromptSet)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet target = null;
            target = (com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet)get_store().find_element_user(QAGETPROMPTSET$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet)get_store().add_element_user(QAGETPROMPTSET$0);
            }
            target.set(qaGetPromptSet);
        }
    }
    
    /**
     * Appends and returns a new empty "QAGetPromptSet" element
     */
    public com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet addNewQAGetPromptSet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet target = null;
            target = (com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet)get_store().add_element_user(QAGETPROMPTSET$0);
            return target;
        }
    }
    /**
     * An XML QAGetPromptSet(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QAGetPromptSetImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QAGetPromptSetDocument.QAGetPromptSet
    {
        
        public QAGetPromptSetImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName COUNTRY$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Country");
        private static final javax.xml.namespace.QName ENGINE$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Engine");
        private static final javax.xml.namespace.QName PROMPTSET$4 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "PromptSet");
        private static final javax.xml.namespace.QName QACONFIG$6 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAConfig");
        
        
        /**
         * Gets the "Country" element
         */
        public java.lang.String getCountry()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COUNTRY$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "Country" element
         */
        public com.qas.www.web_2010_04.DataIDType xgetCountry()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.DataIDType target = null;
                target = (com.qas.www.web_2010_04.DataIDType)get_store().find_element_user(COUNTRY$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "Country" element
         */
        public void setCountry(java.lang.String country)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COUNTRY$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COUNTRY$0);
                }
                target.setStringValue(country);
            }
        }
        
        /**
         * Sets (as xml) the "Country" element
         */
        public void xsetCountry(com.qas.www.web_2010_04.DataIDType country)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.DataIDType target = null;
                target = (com.qas.www.web_2010_04.DataIDType)get_store().find_element_user(COUNTRY$0, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.DataIDType)get_store().add_element_user(COUNTRY$0);
                }
                target.set(country);
            }
        }
        
        /**
         * Gets the "Engine" element
         */
        public com.qas.www.web_2010_04.EngineType getEngine()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.EngineType target = null;
                target = (com.qas.www.web_2010_04.EngineType)get_store().find_element_user(ENGINE$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "Engine" element
         */
        public boolean isSetEngine()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(ENGINE$2) != 0;
            }
        }
        
        /**
         * Sets the "Engine" element
         */
        public void setEngine(com.qas.www.web_2010_04.EngineType engine)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.EngineType target = null;
                target = (com.qas.www.web_2010_04.EngineType)get_store().find_element_user(ENGINE$2, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.EngineType)get_store().add_element_user(ENGINE$2);
                }
                target.set(engine);
            }
        }
        
        /**
         * Appends and returns a new empty "Engine" element
         */
        public com.qas.www.web_2010_04.EngineType addNewEngine()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.EngineType target = null;
                target = (com.qas.www.web_2010_04.EngineType)get_store().add_element_user(ENGINE$2);
                return target;
            }
        }
        
        /**
         * Unsets the "Engine" element
         */
        public void unsetEngine()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(ENGINE$2, 0);
            }
        }
        
        /**
         * Gets the "PromptSet" element
         */
        public com.qas.www.web_2010_04.PromptSetType.Enum getPromptSet()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROMPTSET$4, 0);
                if (target == null)
                {
                    return null;
                }
                return (com.qas.www.web_2010_04.PromptSetType.Enum)target.getEnumValue();
            }
        }
        
        /**
         * Gets (as xml) the "PromptSet" element
         */
        public com.qas.www.web_2010_04.PromptSetType xgetPromptSet()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.PromptSetType target = null;
                target = (com.qas.www.web_2010_04.PromptSetType)get_store().find_element_user(PROMPTSET$4, 0);
                return target;
            }
        }
        
        /**
         * Sets the "PromptSet" element
         */
        public void setPromptSet(com.qas.www.web_2010_04.PromptSetType.Enum promptSet)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROMPTSET$4, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROMPTSET$4);
                }
                target.setEnumValue(promptSet);
            }
        }
        
        /**
         * Sets (as xml) the "PromptSet" element
         */
        public void xsetPromptSet(com.qas.www.web_2010_04.PromptSetType promptSet)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.PromptSetType target = null;
                target = (com.qas.www.web_2010_04.PromptSetType)get_store().find_element_user(PROMPTSET$4, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.PromptSetType)get_store().add_element_user(PROMPTSET$4);
                }
                target.set(promptSet);
            }
        }
        
        /**
         * Gets the "QAConfig" element
         */
        public com.qas.www.web_2010_04.QAConfigType getQAConfig()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAConfigType target = null;
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$6, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "QAConfig" element
         */
        public boolean isSetQAConfig()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(QACONFIG$6) != 0;
            }
        }
        
        /**
         * Sets the "QAConfig" element
         */
        public void setQAConfig(com.qas.www.web_2010_04.QAConfigType qaConfig)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAConfigType target = null;
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$6, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$6);
                }
                target.set(qaConfig);
            }
        }
        
        /**
         * Appends and returns a new empty "QAConfig" element
         */
        public com.qas.www.web_2010_04.QAConfigType addNewQAConfig()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.QAConfigType target = null;
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$6);
                return target;
            }
        }
        
        /**
         * Unsets the "QAConfig" element
         */
        public void unsetQAConfig()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(QACONFIG$6, 0);
            }
        }
    }
}
