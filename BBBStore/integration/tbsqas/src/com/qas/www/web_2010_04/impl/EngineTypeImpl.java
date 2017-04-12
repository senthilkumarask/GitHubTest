/*
 * XML Type:  EngineType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.EngineType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML EngineType(@http://www.qas.com/web-2010-04).
 *
 * This is an atomic type that is a restriction of com.qas.www.web_2010_04.EngineType.
 */
public class EngineTypeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements com.qas.www.web_2010_04.EngineType
{
    
    public EngineTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType, true);
    }
    
    protected EngineTypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b)
    {
        super(sType, b);
    }
    
    private static final javax.xml.namespace.QName FLATTEN$0 = 
        new javax.xml.namespace.QName("", "Flatten");
    private static final javax.xml.namespace.QName INTENSITY$2 = 
        new javax.xml.namespace.QName("", "Intensity");
    private static final javax.xml.namespace.QName PROMPTSET$4 = 
        new javax.xml.namespace.QName("", "PromptSet");
    private static final javax.xml.namespace.QName THRESHOLD$6 = 
        new javax.xml.namespace.QName("", "Threshold");
    private static final javax.xml.namespace.QName TIMEOUT$8 = 
        new javax.xml.namespace.QName("", "Timeout");
    
    
    /**
     * Gets the "Flatten" attribute
     */
    public boolean getFlatten()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(FLATTEN$0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "Flatten" attribute
     */
    public org.apache.xmlbeans.XmlBoolean xgetFlatten()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(FLATTEN$0);
            return target;
        }
    }
    
    /**
     * True if has "Flatten" attribute
     */
    public boolean isSetFlatten()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(FLATTEN$0) != null;
        }
    }
    
    /**
     * Sets the "Flatten" attribute
     */
    public void setFlatten(boolean flatten)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(FLATTEN$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(FLATTEN$0);
            }
            target.setBooleanValue(flatten);
        }
    }
    
    /**
     * Sets (as xml) the "Flatten" attribute
     */
    public void xsetFlatten(org.apache.xmlbeans.XmlBoolean flatten)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_attribute_user(FLATTEN$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_attribute_user(FLATTEN$0);
            }
            target.set(flatten);
        }
    }
    
    /**
     * Unsets the "Flatten" attribute
     */
    public void unsetFlatten()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(FLATTEN$0);
        }
    }
    
    /**
     * Gets the "Intensity" attribute
     */
    public com.qas.www.web_2010_04.EngineIntensityType.Enum getIntensity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INTENSITY$2);
            if (target == null)
            {
                return null;
            }
            return (com.qas.www.web_2010_04.EngineIntensityType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "Intensity" attribute
     */
    public com.qas.www.web_2010_04.EngineIntensityType xgetIntensity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.EngineIntensityType target = null;
            target = (com.qas.www.web_2010_04.EngineIntensityType)get_store().find_attribute_user(INTENSITY$2);
            return target;
        }
    }
    
    /**
     * True if has "Intensity" attribute
     */
    public boolean isSetIntensity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(INTENSITY$2) != null;
        }
    }
    
    /**
     * Sets the "Intensity" attribute
     */
    public void setIntensity(com.qas.www.web_2010_04.EngineIntensityType.Enum intensity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(INTENSITY$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(INTENSITY$2);
            }
            target.setEnumValue(intensity);
        }
    }
    
    /**
     * Sets (as xml) the "Intensity" attribute
     */
    public void xsetIntensity(com.qas.www.web_2010_04.EngineIntensityType intensity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.EngineIntensityType target = null;
            target = (com.qas.www.web_2010_04.EngineIntensityType)get_store().find_attribute_user(INTENSITY$2);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.EngineIntensityType)get_store().add_attribute_user(INTENSITY$2);
            }
            target.set(intensity);
        }
    }
    
    /**
     * Unsets the "Intensity" attribute
     */
    public void unsetIntensity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(INTENSITY$2);
        }
    }
    
    /**
     * Gets the "PromptSet" attribute
     */
    public com.qas.www.web_2010_04.PromptSetType.Enum getPromptSet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROMPTSET$4);
            if (target == null)
            {
                return null;
            }
            return (com.qas.www.web_2010_04.PromptSetType.Enum)target.getEnumValue();
        }
    }
    
    /**
     * Gets (as xml) the "PromptSet" attribute
     */
    public com.qas.www.web_2010_04.PromptSetType xgetPromptSet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.PromptSetType target = null;
            target = (com.qas.www.web_2010_04.PromptSetType)get_store().find_attribute_user(PROMPTSET$4);
            return target;
        }
    }
    
    /**
     * True if has "PromptSet" attribute
     */
    public boolean isSetPromptSet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(PROMPTSET$4) != null;
        }
    }
    
    /**
     * Sets the "PromptSet" attribute
     */
    public void setPromptSet(com.qas.www.web_2010_04.PromptSetType.Enum promptSet)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROMPTSET$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROMPTSET$4);
            }
            target.setEnumValue(promptSet);
        }
    }
    
    /**
     * Sets (as xml) the "PromptSet" attribute
     */
    public void xsetPromptSet(com.qas.www.web_2010_04.PromptSetType promptSet)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.qas.www.web_2010_04.PromptSetType target = null;
            target = (com.qas.www.web_2010_04.PromptSetType)get_store().find_attribute_user(PROMPTSET$4);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.PromptSetType)get_store().add_attribute_user(PROMPTSET$4);
            }
            target.set(promptSet);
        }
    }
    
    /**
     * Unsets the "PromptSet" attribute
     */
    public void unsetPromptSet()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(PROMPTSET$4);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLD$6);
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
            target = (com.qas.www.web_2010_04.ThresholdType)get_store().find_attribute_user(THRESHOLD$6);
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
            return get_store().find_attribute_user(THRESHOLD$6) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(THRESHOLD$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(THRESHOLD$6);
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
            target = (com.qas.www.web_2010_04.ThresholdType)get_store().find_attribute_user(THRESHOLD$6);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.ThresholdType)get_store().add_attribute_user(THRESHOLD$6);
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
            get_store().remove_attribute(THRESHOLD$6);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$8);
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
            target = (com.qas.www.web_2010_04.TimeoutType)get_store().find_attribute_user(TIMEOUT$8);
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
            return get_store().find_attribute_user(TIMEOUT$8) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TIMEOUT$8);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TIMEOUT$8);
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
            target = (com.qas.www.web_2010_04.TimeoutType)get_store().find_attribute_user(TIMEOUT$8);
            if (target == null)
            {
                target = (com.qas.www.web_2010_04.TimeoutType)get_store().add_attribute_user(TIMEOUT$8);
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
            get_store().remove_attribute(TIMEOUT$8);
        }
    }
}
