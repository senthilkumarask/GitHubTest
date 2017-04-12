/*
 * An XML document type.
 * Localname: QARefine
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.QARefineDocument
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * A document containing one QARefine(@http://www.qas.com/web-2010-04) element.
 *
 * This is a complex type.
 */
public class QARefineDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QARefineDocument
{
    
    public QARefineDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName QAREFINE$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QARefine");
    
    
    /**
     * Gets the "QARefine" element
     */
    public com.qas.www.web_2010_04.QARefineDocument.QARefine getQARefine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QARefineDocument.QARefine target = null;
            target = (com.qas.www.web_2010_04.QARefineDocument.QARefine)get_store().find_element_user(QAREFINE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "QARefine" element
     */
    public void setQARefine(com.qas.www.web_2010_04.QARefineDocument.QARefine qaRefine)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QARefineDocument.QARefine target = null;
            target = (com.qas.www.web_2010_04.QARefineDocument.QARefine)get_store().find_element_user(QAREFINE$0, 0);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.QARefineDocument.QARefine)get_store().add_element_user(QAREFINE$0);
            }
            target.set(qaRefine);
        }
    }
    
    /**
     * Appends and returns a new empty "QARefine" element
     */
    public com.qas.www.web_2010_04.QARefineDocument.QARefine addNewQARefine()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.QARefineDocument.QARefine target = null;
            target = (com.qas.www.web_2010_04.QARefineDocument.QARefine)get_store().add_element_user(QAREFINE$0);
            return target;
        }
    }
    /**
     * An XML QARefine(@http://www.qas.com/web-2010-04).
     *
     * This is a complex type.
     */
    public static class QARefineImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.QARefineDocument.QARefine
    {
        
        public QARefineImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName MONIKER$0 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Moniker");
        private static final javax.xml.namespace.QName REFINEMENT$2 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Refinement");
        private static final javax.xml.namespace.QName QACONFIG$4 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "QAConfig");
        private static final javax.xml.namespace.QName LAYOUT$6 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Layout");
        private static final javax.xml.namespace.QName FORMATTEDADDRESSINPICKLIST$8 = 
            new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "FormattedAddressInPicklist");
        private static final javax.xml.namespace.QName THRESHOLD$10 = 
            new javax.xml.namespace.QName("", "Threshold");
        private static final javax.xml.namespace.QName TIMEOUT$12 = 
            new javax.xml.namespace.QName("", "Timeout");
        
        
        /**
         * Gets the "Moniker" element
         */
        public java.lang.String getMoniker()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MONIKER$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "Moniker" element
         */
        public org.apache.xmlbeans.XmlString xgetMoniker()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MONIKER$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "Moniker" element
         */
        public void setMoniker(java.lang.String moniker)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MONIKER$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MONIKER$0);
                }
                target.setStringValue(moniker);
            }
        }
        
        /**
         * Sets (as xml) the "Moniker" element
         */
        public void xsetMoniker(org.apache.xmlbeans.XmlString moniker)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(MONIKER$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(MONIKER$0);
                }
                target.set(moniker);
            }
        }
        
        /**
         * Gets the "Refinement" element
         */
        public java.lang.String getRefinement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REFINEMENT$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "Refinement" element
         */
        public org.apache.xmlbeans.XmlString xgetRefinement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REFINEMENT$2, 0);
                return target;
            }
        }
        
        /**
         * True if has "Refinement" element
         */
        public boolean isSetRefinement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(REFINEMENT$2) != 0;
            }
        }
        
        /**
         * Sets the "Refinement" element
         */
        public void setRefinement(java.lang.String refinement)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REFINEMENT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REFINEMENT$2);
                }
                target.setStringValue(refinement);
            }
        }
        
        /**
         * Sets (as xml) the "Refinement" element
         */
        public void xsetRefinement(org.apache.xmlbeans.XmlString refinement)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(REFINEMENT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(REFINEMENT$2);
                }
                target.set(refinement);
            }
        }
        
        /**
         * Unsets the "Refinement" element
         */
        public void unsetRefinement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(REFINEMENT$2, 0);
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
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$4, 0);
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
                return get_store().count_elements(QACONFIG$4) != 0;
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
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().find_element_user(QACONFIG$4, 0);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$4);
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
                target = (com.qas.www.web_2010_04.QAConfigType)get_store().add_element_user(QACONFIG$4);
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
                get_store().remove_element(QACONFIG$4, 0);
            }
        }
        
        /**
         * Gets the "Layout" element
         */
        public java.lang.String getLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LAYOUT$6, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "Layout" element
         */
        public org.apache.xmlbeans.XmlString xgetLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LAYOUT$6, 0);
                return target;
            }
        }
        
        /**
         * True if has "Layout" element
         */
        public boolean isSetLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(LAYOUT$6) != 0;
            }
        }
        
        /**
         * Sets the "Layout" element
         */
        public void setLayout(java.lang.String layout)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LAYOUT$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LAYOUT$6);
                }
                target.setStringValue(layout);
            }
        }
        
        /**
         * Sets (as xml) the "Layout" element
         */
        public void xsetLayout(org.apache.xmlbeans.XmlString layout)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(LAYOUT$6, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(LAYOUT$6);
                }
                target.set(layout);
            }
        }
        
        /**
         * Unsets the "Layout" element
         */
        public void unsetLayout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(LAYOUT$6, 0);
            }
        }
        
        /**
         * Gets the "FormattedAddressInPicklist" element
         */
        public boolean getFormattedAddressInPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FORMATTEDADDRESSINPICKLIST$8, 0);
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "FormattedAddressInPicklist" element
         */
        public org.apache.xmlbeans.XmlBoolean xgetFormattedAddressInPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(FORMATTEDADDRESSINPICKLIST$8, 0);
                return target;
            }
        }
        
        /**
         * True if has "FormattedAddressInPicklist" element
         */
        public boolean isSetFormattedAddressInPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(FORMATTEDADDRESSINPICKLIST$8) != 0;
            }
        }
        
        /**
         * Sets the "FormattedAddressInPicklist" element
         */
        public void setFormattedAddressInPicklist(boolean formattedAddressInPicklist)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FORMATTEDADDRESSINPICKLIST$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FORMATTEDADDRESSINPICKLIST$8);
                }
                target.setBooleanValue(formattedAddressInPicklist);
            }
        }
        
        /**
         * Sets (as xml) the "FormattedAddressInPicklist" element
         */
        public void xsetFormattedAddressInPicklist(org.apache.xmlbeans.XmlBoolean formattedAddressInPicklist)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(FORMATTEDADDRESSINPICKLIST$8, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(FORMATTEDADDRESSINPICKLIST$8);
                }
                target.set(formattedAddressInPicklist);
            }
        }
        
        /**
         * Unsets the "FormattedAddressInPicklist" element
         */
        public void unsetFormattedAddressInPicklist()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(FORMATTEDADDRESSINPICKLIST$8, 0);
            }
        }
        
        /**
         * Gets the "Threshold" attribute
         */
        public int getThreshold()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLD$10);
                if (target == null)
                {
                    return 0;
                }
                return target.getIntValue();
            }
        }
        
        /**
         * Gets (as xml) the "Threshold" attribute
         */
        public com.qas.www.web_2010_04.ThresholdType xgetThreshold()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.ThresholdType target = null;
                target = (com.qas.www.web_2010_04.ThresholdType)get_store().find_attribute_user(THRESHOLD$10);
                return target;
            }
        }
        
        /**
         * True if has "Threshold" attribute
         */
        public boolean isSetThreshold()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().find_attribute_user(THRESHOLD$10) != null;
            }
        }
        
        /**
         * Sets the "Threshold" attribute
         */
        public void setThreshold(int threshold)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLD$10);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(THRESHOLD$10);
                }
                target.setIntValue(threshold);
            }
        }
        
        /**
         * Sets (as xml) the "Threshold" attribute
         */
        public void xsetThreshold(com.qas.www.web_2010_04.ThresholdType threshold)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.ThresholdType target = null;
                target = (com.qas.www.web_2010_04.ThresholdType)get_store().find_attribute_user(THRESHOLD$10);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.ThresholdType)get_store().add_attribute_user(THRESHOLD$10);
                }
                target.set(threshold);
            }
        }
        
        /**
         * Unsets the "Threshold" attribute
         */
        public void unsetThreshold()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_attribute(THRESHOLD$10);
            }
        }
        
        /**
         * Gets the "Timeout" attribute
         */
        public int getTimeout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$12);
                if (target == null)
                {
                    return 0;
                }
                return target.getIntValue();
            }
        }
        
        /**
         * Gets (as xml) the "Timeout" attribute
         */
        public com.qas.www.web_2010_04.TimeoutType xgetTimeout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.TimeoutType target = null;
                target = (com.qas.www.web_2010_04.TimeoutType)get_store().find_attribute_user(TIMEOUT$12);
                return target;
            }
        }
        
        /**
         * True if has "Timeout" attribute
         */
        public boolean isSetTimeout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().find_attribute_user(TIMEOUT$12) != null;
            }
        }
        
        /**
         * Sets the "Timeout" attribute
         */
        public void setTimeout(int timeout)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$12);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TIMEOUT$12);
                }
                target.setIntValue(timeout);
            }
        }
        
        /**
         * Sets (as xml) the "Timeout" attribute
         */
        public void xsetTimeout(com.qas.www.web_2010_04.TimeoutType timeout)
        {
            synchronized (monitor())
            {
                check_orphaned();
                com.qas.www.web_2010_04.TimeoutType target = null;
                target = (com.qas.www.web_2010_04.TimeoutType)get_store().find_attribute_user(TIMEOUT$12);
                if (target == null)
                {
                    target = (com.qas.www.web_2010_04.TimeoutType)get_store().add_attribute_user(TIMEOUT$12);
                }
                target.set(timeout);
            }
        }
        
        /**
         * Unsets the "Timeout" attribute
         */
        public void unsetTimeout()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_attribute(TIMEOUT$12);
            }
        }
    }
}
