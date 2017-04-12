/*
 * XML Type:  VerificationFlagsType
 * Namespace: http://www.qas.com/web-2010-04
 * Java type: com.qas.www.web_2010_04.VerificationFlagsType
 *
 * Automatically generated - do not modify.
 */
package com.qas.www.web_2010_04.impl;
/**
 * An XML VerificationFlagsType(@http://www.qas.com/web-2010-04).
 *
 * This is a complex type.
 */
public class VerificationFlagsTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements com.qas.www.web_2010_04.VerificationFlagsType
{
    
    public VerificationFlagsTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName BLDGFIRMNAMECHANGED$0 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "BldgFirmNameChanged");
    private static final javax.xml.namespace.QName PRIMARYNUMBERCHANGED$2 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "PrimaryNumberChanged");
    private static final javax.xml.namespace.QName STREETCORRECTED$4 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "StreetCorrected");
    private static final javax.xml.namespace.QName RURALRTEHIGHWAYCONTRACTMATCHED$6 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "RuralRteHighwayContractMatched");
    private static final javax.xml.namespace.QName CITYNAMECHANGED$8 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "CityNameChanged");
    private static final javax.xml.namespace.QName CITYALIASMATCHED$10 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "CityAliasMatched");
    private static final javax.xml.namespace.QName STATEPROVINCECHANGED$12 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "StateProvinceChanged");
    private static final javax.xml.namespace.QName POSTCODECORRECTED$14 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "PostCodeCorrected");
    private static final javax.xml.namespace.QName SECONDARYNUMRETAINED$16 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "SecondaryNumRetained");
    private static final javax.xml.namespace.QName IDENPRESTINFORETAINED$18 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "IdenPreStInfoRetained");
    private static final javax.xml.namespace.QName GENPRESTINFORETAINED$20 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "GenPreStInfoRetained");
    private static final javax.xml.namespace.QName POSTSTINFORETAINED$22 = 
        new javax.xml.namespace.QName("http://www.qas.com/web-2010-04", "PostStInfoRetained");
    
    
    /**
     * Gets the "BldgFirmNameChanged" element
     */
    public boolean getBldgFirmNameChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BLDGFIRMNAMECHANGED$0, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "BldgFirmNameChanged" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetBldgFirmNameChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(BLDGFIRMNAMECHANGED$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "BldgFirmNameChanged" element
     */
    public boolean isSetBldgFirmNameChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(BLDGFIRMNAMECHANGED$0) != 0;
        }
    }
    
    /**
     * Sets the "BldgFirmNameChanged" element
     */
    public void setBldgFirmNameChanged(boolean bldgFirmNameChanged)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BLDGFIRMNAMECHANGED$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BLDGFIRMNAMECHANGED$0);
            }
            target.setBooleanValue(bldgFirmNameChanged);
        }
    }
    
    /**
     * Sets (as xml) the "BldgFirmNameChanged" element
     */
    public void xsetBldgFirmNameChanged(org.apache.xmlbeans.XmlBoolean bldgFirmNameChanged)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(BLDGFIRMNAMECHANGED$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(BLDGFIRMNAMECHANGED$0);
            }
            target.set(bldgFirmNameChanged);
        }
    }
    
    /**
     * Unsets the "BldgFirmNameChanged" element
     */
    public void unsetBldgFirmNameChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(BLDGFIRMNAMECHANGED$0, 0);
        }
    }
    
    /**
     * Gets the "PrimaryNumberChanged" element
     */
    public boolean getPrimaryNumberChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PRIMARYNUMBERCHANGED$2, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "PrimaryNumberChanged" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetPrimaryNumberChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(PRIMARYNUMBERCHANGED$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "PrimaryNumberChanged" element
     */
    public boolean isSetPrimaryNumberChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PRIMARYNUMBERCHANGED$2) != 0;
        }
    }
    
    /**
     * Sets the "PrimaryNumberChanged" element
     */
    public void setPrimaryNumberChanged(boolean primaryNumberChanged)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PRIMARYNUMBERCHANGED$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PRIMARYNUMBERCHANGED$2);
            }
            target.setBooleanValue(primaryNumberChanged);
        }
    }
    
    /**
     * Sets (as xml) the "PrimaryNumberChanged" element
     */
    public void xsetPrimaryNumberChanged(org.apache.xmlbeans.XmlBoolean primaryNumberChanged)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(PRIMARYNUMBERCHANGED$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(PRIMARYNUMBERCHANGED$2);
            }
            target.set(primaryNumberChanged);
        }
    }
    
    /**
     * Unsets the "PrimaryNumberChanged" element
     */
    public void unsetPrimaryNumberChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PRIMARYNUMBERCHANGED$2, 0);
        }
    }
    
    /**
     * Gets the "StreetCorrected" element
     */
    public boolean getStreetCorrected()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREETCORRECTED$4, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "StreetCorrected" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetStreetCorrected()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(STREETCORRECTED$4, 0);
            return target;
        }
    }
    
    /**
     * True if has "StreetCorrected" element
     */
    public boolean isSetStreetCorrected()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(STREETCORRECTED$4) != 0;
        }
    }
    
    /**
     * Sets the "StreetCorrected" element
     */
    public void setStreetCorrected(boolean streetCorrected)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STREETCORRECTED$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STREETCORRECTED$4);
            }
            target.setBooleanValue(streetCorrected);
        }
    }
    
    /**
     * Sets (as xml) the "StreetCorrected" element
     */
    public void xsetStreetCorrected(org.apache.xmlbeans.XmlBoolean streetCorrected)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(STREETCORRECTED$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(STREETCORRECTED$4);
            }
            target.set(streetCorrected);
        }
    }
    
    /**
     * Unsets the "StreetCorrected" element
     */
    public void unsetStreetCorrected()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(STREETCORRECTED$4, 0);
        }
    }
    
    /**
     * Gets the "RuralRteHighwayContractMatched" element
     */
    public boolean getRuralRteHighwayContractMatched()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RURALRTEHIGHWAYCONTRACTMATCHED$6, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "RuralRteHighwayContractMatched" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetRuralRteHighwayContractMatched()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(RURALRTEHIGHWAYCONTRACTMATCHED$6, 0);
            return target;
        }
    }
    
    /**
     * True if has "RuralRteHighwayContractMatched" element
     */
    public boolean isSetRuralRteHighwayContractMatched()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RURALRTEHIGHWAYCONTRACTMATCHED$6) != 0;
        }
    }
    
    /**
     * Sets the "RuralRteHighwayContractMatched" element
     */
    public void setRuralRteHighwayContractMatched(boolean ruralRteHighwayContractMatched)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RURALRTEHIGHWAYCONTRACTMATCHED$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RURALRTEHIGHWAYCONTRACTMATCHED$6);
            }
            target.setBooleanValue(ruralRteHighwayContractMatched);
        }
    }
    
    /**
     * Sets (as xml) the "RuralRteHighwayContractMatched" element
     */
    public void xsetRuralRteHighwayContractMatched(org.apache.xmlbeans.XmlBoolean ruralRteHighwayContractMatched)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(RURALRTEHIGHWAYCONTRACTMATCHED$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(RURALRTEHIGHWAYCONTRACTMATCHED$6);
            }
            target.set(ruralRteHighwayContractMatched);
        }
    }
    
    /**
     * Unsets the "RuralRteHighwayContractMatched" element
     */
    public void unsetRuralRteHighwayContractMatched()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RURALRTEHIGHWAYCONTRACTMATCHED$6, 0);
        }
    }
    
    /**
     * Gets the "CityNameChanged" element
     */
    public boolean getCityNameChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CITYNAMECHANGED$8, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "CityNameChanged" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetCityNameChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(CITYNAMECHANGED$8, 0);
            return target;
        }
    }
    
    /**
     * True if has "CityNameChanged" element
     */
    public boolean isSetCityNameChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CITYNAMECHANGED$8) != 0;
        }
    }
    
    /**
     * Sets the "CityNameChanged" element
     */
    public void setCityNameChanged(boolean cityNameChanged)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CITYNAMECHANGED$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CITYNAMECHANGED$8);
            }
            target.setBooleanValue(cityNameChanged);
        }
    }
    
    /**
     * Sets (as xml) the "CityNameChanged" element
     */
    public void xsetCityNameChanged(org.apache.xmlbeans.XmlBoolean cityNameChanged)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(CITYNAMECHANGED$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(CITYNAMECHANGED$8);
            }
            target.set(cityNameChanged);
        }
    }
    
    /**
     * Unsets the "CityNameChanged" element
     */
    public void unsetCityNameChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CITYNAMECHANGED$8, 0);
        }
    }
    
    /**
     * Gets the "CityAliasMatched" element
     */
    public boolean getCityAliasMatched()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CITYALIASMATCHED$10, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "CityAliasMatched" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetCityAliasMatched()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(CITYALIASMATCHED$10, 0);
            return target;
        }
    }
    
    /**
     * True if has "CityAliasMatched" element
     */
    public boolean isSetCityAliasMatched()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(CITYALIASMATCHED$10) != 0;
        }
    }
    
    /**
     * Sets the "CityAliasMatched" element
     */
    public void setCityAliasMatched(boolean cityAliasMatched)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CITYALIASMATCHED$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CITYALIASMATCHED$10);
            }
            target.setBooleanValue(cityAliasMatched);
        }
    }
    
    /**
     * Sets (as xml) the "CityAliasMatched" element
     */
    public void xsetCityAliasMatched(org.apache.xmlbeans.XmlBoolean cityAliasMatched)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(CITYALIASMATCHED$10, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(CITYALIASMATCHED$10);
            }
            target.set(cityAliasMatched);
        }
    }
    
    /**
     * Unsets the "CityAliasMatched" element
     */
    public void unsetCityAliasMatched()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(CITYALIASMATCHED$10, 0);
        }
    }
    
    /**
     * Gets the "StateProvinceChanged" element
     */
    public boolean getStateProvinceChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STATEPROVINCECHANGED$12, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "StateProvinceChanged" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetStateProvinceChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(STATEPROVINCECHANGED$12, 0);
            return target;
        }
    }
    
    /**
     * True if has "StateProvinceChanged" element
     */
    public boolean isSetStateProvinceChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(STATEPROVINCECHANGED$12) != 0;
        }
    }
    
    /**
     * Sets the "StateProvinceChanged" element
     */
    public void setStateProvinceChanged(boolean stateProvinceChanged)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STATEPROVINCECHANGED$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STATEPROVINCECHANGED$12);
            }
            target.setBooleanValue(stateProvinceChanged);
        }
    }
    
    /**
     * Sets (as xml) the "StateProvinceChanged" element
     */
    public void xsetStateProvinceChanged(org.apache.xmlbeans.XmlBoolean stateProvinceChanged)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(STATEPROVINCECHANGED$12, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(STATEPROVINCECHANGED$12);
            }
            target.set(stateProvinceChanged);
        }
    }
    
    /**
     * Unsets the "StateProvinceChanged" element
     */
    public void unsetStateProvinceChanged()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(STATEPROVINCECHANGED$12, 0);
        }
    }
    
    /**
     * Gets the "PostCodeCorrected" element
     */
    public boolean getPostCodeCorrected()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POSTCODECORRECTED$14, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "PostCodeCorrected" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetPostCodeCorrected()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(POSTCODECORRECTED$14, 0);
            return target;
        }
    }
    
    /**
     * True if has "PostCodeCorrected" element
     */
    public boolean isSetPostCodeCorrected()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(POSTCODECORRECTED$14) != 0;
        }
    }
    
    /**
     * Sets the "PostCodeCorrected" element
     */
    public void setPostCodeCorrected(boolean postCodeCorrected)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POSTCODECORRECTED$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(POSTCODECORRECTED$14);
            }
            target.setBooleanValue(postCodeCorrected);
        }
    }
    
    /**
     * Sets (as xml) the "PostCodeCorrected" element
     */
    public void xsetPostCodeCorrected(org.apache.xmlbeans.XmlBoolean postCodeCorrected)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(POSTCODECORRECTED$14, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(POSTCODECORRECTED$14);
            }
            target.set(postCodeCorrected);
        }
    }
    
    /**
     * Unsets the "PostCodeCorrected" element
     */
    public void unsetPostCodeCorrected()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(POSTCODECORRECTED$14, 0);
        }
    }
    
    /**
     * Gets the "SecondaryNumRetained" element
     */
    public boolean getSecondaryNumRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SECONDARYNUMRETAINED$16, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "SecondaryNumRetained" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetSecondaryNumRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(SECONDARYNUMRETAINED$16, 0);
            return target;
        }
    }
    
    /**
     * True if has "SecondaryNumRetained" element
     */
    public boolean isSetSecondaryNumRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SECONDARYNUMRETAINED$16) != 0;
        }
    }
    
    /**
     * Sets the "SecondaryNumRetained" element
     */
    public void setSecondaryNumRetained(boolean secondaryNumRetained)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SECONDARYNUMRETAINED$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SECONDARYNUMRETAINED$16);
            }
            target.setBooleanValue(secondaryNumRetained);
        }
    }
    
    /**
     * Sets (as xml) the "SecondaryNumRetained" element
     */
    public void xsetSecondaryNumRetained(org.apache.xmlbeans.XmlBoolean secondaryNumRetained)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(SECONDARYNUMRETAINED$16, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(SECONDARYNUMRETAINED$16);
            }
            target.set(secondaryNumRetained);
        }
    }
    
    /**
     * Unsets the "SecondaryNumRetained" element
     */
    public void unsetSecondaryNumRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SECONDARYNUMRETAINED$16, 0);
        }
    }
    
    /**
     * Gets the "IdenPreStInfoRetained" element
     */
    public boolean getIdenPreStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IDENPRESTINFORETAINED$18, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "IdenPreStInfoRetained" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetIdenPreStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(IDENPRESTINFORETAINED$18, 0);
            return target;
        }
    }
    
    /**
     * True if has "IdenPreStInfoRetained" element
     */
    public boolean isSetIdenPreStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(IDENPRESTINFORETAINED$18) != 0;
        }
    }
    
    /**
     * Sets the "IdenPreStInfoRetained" element
     */
    public void setIdenPreStInfoRetained(boolean idenPreStInfoRetained)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IDENPRESTINFORETAINED$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(IDENPRESTINFORETAINED$18);
            }
            target.setBooleanValue(idenPreStInfoRetained);
        }
    }
    
    /**
     * Sets (as xml) the "IdenPreStInfoRetained" element
     */
    public void xsetIdenPreStInfoRetained(org.apache.xmlbeans.XmlBoolean idenPreStInfoRetained)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(IDENPRESTINFORETAINED$18, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(IDENPRESTINFORETAINED$18);
            }
            target.set(idenPreStInfoRetained);
        }
    }
    
    /**
     * Unsets the "IdenPreStInfoRetained" element
     */
    public void unsetIdenPreStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(IDENPRESTINFORETAINED$18, 0);
        }
    }
    
    /**
     * Gets the "GenPreStInfoRetained" element
     */
    public boolean getGenPreStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GENPRESTINFORETAINED$20, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "GenPreStInfoRetained" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetGenPreStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(GENPRESTINFORETAINED$20, 0);
            return target;
        }
    }
    
    /**
     * True if has "GenPreStInfoRetained" element
     */
    public boolean isSetGenPreStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(GENPRESTINFORETAINED$20) != 0;
        }
    }
    
    /**
     * Sets the "GenPreStInfoRetained" element
     */
    public void setGenPreStInfoRetained(boolean genPreStInfoRetained)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(GENPRESTINFORETAINED$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(GENPRESTINFORETAINED$20);
            }
            target.setBooleanValue(genPreStInfoRetained);
        }
    }
    
    /**
     * Sets (as xml) the "GenPreStInfoRetained" element
     */
    public void xsetGenPreStInfoRetained(org.apache.xmlbeans.XmlBoolean genPreStInfoRetained)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(GENPRESTINFORETAINED$20, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(GENPRESTINFORETAINED$20);
            }
            target.set(genPreStInfoRetained);
        }
    }
    
    /**
     * Unsets the "GenPreStInfoRetained" element
     */
    public void unsetGenPreStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(GENPRESTINFORETAINED$20, 0);
        }
    }
    
    /**
     * Gets the "PostStInfoRetained" element
     */
    public boolean getPostStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POSTSTINFORETAINED$22, 0);
            if (target == null)
            {
                return false;
            }
            return target.getBooleanValue();
        }
    }
    
    /**
     * Gets (as xml) the "PostStInfoRetained" element
     */
    public org.apache.xmlbeans.XmlBoolean xgetPostStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(POSTSTINFORETAINED$22, 0);
            return target;
        }
    }
    
    /**
     * True if has "PostStInfoRetained" element
     */
    public boolean isSetPostStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(POSTSTINFORETAINED$22) != 0;
        }
    }
    
    /**
     * Sets the "PostStInfoRetained" element
     */
    public void setPostStInfoRetained(boolean postStInfoRetained)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(POSTSTINFORETAINED$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(POSTSTINFORETAINED$22);
            }
            target.setBooleanValue(postStInfoRetained);
        }
    }
    
    /**
     * Sets (as xml) the "PostStInfoRetained" element
     */
    public void xsetPostStInfoRetained(org.apache.xmlbeans.XmlBoolean postStInfoRetained)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlBoolean target = null;
            target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(POSTSTINFORETAINED$22, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(POSTSTINFORETAINED$22);
            }
            target.set(postStInfoRetained);
        }
    }
    
    /**
     * Unsets the "PostStInfoRetained" element
     */
    public void unsetPostStInfoRetained()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(POSTSTINFORETAINED$22, 0);
        }
    }
}
