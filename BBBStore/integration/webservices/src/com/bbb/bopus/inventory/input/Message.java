//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.16 at 04:28:41 PM IST 
//


package com.bbb.bopus.inventory.input;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}SupplyBalanceInput" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "supplyBalanceInput"
})
@XmlRootElement(name = "Message")
public class Message {

    @XmlElement(name = "SupplyBalanceInput", required = true)
    protected List<SupplyBalanceInput> supplyBalanceInput;

   

	/**
     * Gets the value of the supplyBalanceInput property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supplyBalanceInput property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupplyBalanceInput().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupplyBalanceInput }
     * 
     * 
     */
    public List<SupplyBalanceInput> getSupplyBalanceInput() {
        if (supplyBalanceInput == null) {
            supplyBalanceInput = new ArrayList<SupplyBalanceInput>();
        }
        return this.supplyBalanceInput;
    }

    public void setSupplyBalanceInput(List<SupplyBalanceInput> supplyBalanceInput) {
		this.supplyBalanceInput = supplyBalanceInput;
	}
   

}
