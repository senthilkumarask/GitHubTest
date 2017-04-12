/*
 * XML Type:  PromptLine
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.PromptLine
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML PromptLine(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class PromptLineImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.PromptLine
{
    
    public PromptLineImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PROMPT$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Prompt");
    private static final javax.xml.namespace.QName SUGGESTEDINPUTLENGTH$2 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "SuggestedInputLength");
    private static final javax.xml.namespace.QName EXAMPLE$4 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "Example");
    
    
    /**
     * Gets the "Prompt" element
     */
    public java.lang.String getPrompt()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROMPT$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Prompt" element
     */
    public org.apache.xmlbeans.XmlString xgetPrompt()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROMPT$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Prompt" element
     */
    public void setPrompt(java.lang.String prompt)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROMPT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROMPT$0);
            }
            target.setStringValue(prompt);
        }
    }
    
    /**
     * Sets (as xml) the "Prompt" element
     */
    public void xsetPrompt(org.apache.xmlbeans.XmlString prompt)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROMPT$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROMPT$0);
            }
            target.set(prompt);
        }
    }
    
    /**
     * Gets the "SuggestedInputLength" element
     */
    public java.math.BigInteger getSuggestedInputLength()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUGGESTEDINPUTLENGTH$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getBigIntegerValue();
        }
    }
    
    /**
     * Gets (as xml) the "SuggestedInputLength" element
     */
    public org.apache.xmlbeans.XmlNonNegativeInteger xgetSuggestedInputLength()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(SUGGESTEDINPUTLENGTH$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "SuggestedInputLength" element
     */
    public void setSuggestedInputLength(java.math.BigInteger suggestedInputLength)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUGGESTEDINPUTLENGTH$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SUGGESTEDINPUTLENGTH$2);
            }
            target.setBigIntegerValue(suggestedInputLength);
        }
    }
    
    /**
     * Sets (as xml) the "SuggestedInputLength" element
     */
    public void xsetSuggestedInputLength(org.apache.xmlbeans.XmlNonNegativeInteger suggestedInputLength)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlNonNegativeInteger target = null;
            target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().find_element_user(SUGGESTEDINPUTLENGTH$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlNonNegativeInteger)get_store().add_element_user(SUGGESTEDINPUTLENGTH$2);
            }
            target.set(suggestedInputLength);
        }
    }
    
    /**
     * Gets the "Example" element
     */
    public java.lang.String getExample()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXAMPLE$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "Example" element
     */
    public org.apache.xmlbeans.XmlString xgetExample()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXAMPLE$4, 0);
            return target;
        }
    }
    
    /**
     * Sets the "Example" element
     */
    public void setExample(java.lang.String example)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(EXAMPLE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(EXAMPLE$4);
            }
            target.setStringValue(example);
        }
    }
    
    /**
     * Sets (as xml) the "Example" element
     */
    public void xsetExample(org.apache.xmlbeans.XmlString example)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(EXAMPLE$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(EXAMPLE$4);
            }
            target.set(example);
        }
    }
}
