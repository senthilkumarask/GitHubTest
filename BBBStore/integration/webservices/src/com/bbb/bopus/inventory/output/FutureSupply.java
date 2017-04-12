//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.16 at 04:29:01 PM IST 
//


package com.bbb.bopus.inventory.output;

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
 *         &lt;element name="Item" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Facility" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Manufacturing_Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Lot" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Supply_Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Supply_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Supply_Detail_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ETA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Total_Inventory" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Total_Supply_Balance" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Total_Allocated_Qty_Future" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Total_Unallocatable_Qty" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "item",
    "facility",
    "manufacturingDate",
    "lot",
    "supplyType",
    "supplyID",
    "supplyDetailID",
    "status",
    "eta",
    "totalInventory",
    "totalSupplyBalance",
    "totalAllocatedQtyFuture",
    "totalUnallocatableQty"
})
@XmlRootElement(name = "Future_Supply")
public class FutureSupply {

    @XmlElement(name = "Item")
    protected String item;
    @XmlElement(name = "Facility")
    protected String facility;
    @XmlElement(name = "Manufacturing_Date")
    protected String manufacturingDate;
    @XmlElement(name = "Lot")
    protected String lot;
    @XmlElement(name = "Supply_Type")
    protected String supplyType;
    @XmlElement(name = "Supply_ID")
    protected String supplyID;
    @XmlElement(name = "Supply_Detail_ID")
    protected String supplyDetailID;
    @XmlElement(name = "Status")
    protected String status;
    @XmlElement(name = "ETA")
    protected String eta;
    @XmlElement(name = "Total_Inventory", required = true)
    protected String totalInventory;
    @XmlElement(name = "Total_Supply_Balance", required = true)
    protected String totalSupplyBalance;
    @XmlElement(name = "Total_Allocated_Qty_Future", required = true)
    protected String totalAllocatedQtyFuture;
    @XmlElement(name = "Total_Unallocatable_Qty", required = true)
    protected String totalUnallocatableQty;

    /**
     * Gets the value of the item property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItem() {
        return item;
    }

    /**
     * Sets the value of the item property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItem(String value) {
        this.item = value;
    }

    /**
     * Gets the value of the facility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFacility() {
        return facility;
    }

    /**
     * Sets the value of the facility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFacility(String value) {
        this.facility = value;
    }

    /**
     * Gets the value of the manufacturingDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturingDate() {
        return manufacturingDate;
    }

    /**
     * Sets the value of the manufacturingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturingDate(String value) {
        this.manufacturingDate = value;
    }

    /**
     * Gets the value of the lot property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLot() {
        return lot;
    }

    /**
     * Sets the value of the lot property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLot(String value) {
        this.lot = value;
    }

    /**
     * Gets the value of the supplyType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplyType() {
        return supplyType;
    }

    /**
     * Sets the value of the supplyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplyType(String value) {
        this.supplyType = value;
    }

    /**
     * Gets the value of the supplyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplyID() {
        return supplyID;
    }

    /**
     * Sets the value of the supplyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplyID(String value) {
        this.supplyID = value;
    }

    /**
     * Gets the value of the supplyDetailID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplyDetailID() {
        return supplyDetailID;
    }

    /**
     * Sets the value of the supplyDetailID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplyDetailID(String value) {
        this.supplyDetailID = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the eta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getETA() {
        return eta;
    }

    /**
     * Sets the value of the eta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setETA(String value) {
        this.eta = value;
    }

    /**
     * Gets the value of the totalInventory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalInventory() {
        return totalInventory;
    }

    /**
     * Sets the value of the totalInventory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalInventory(String value) {
        this.totalInventory = value;
    }

    /**
     * Gets the value of the totalSupplyBalance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalSupplyBalance() {
        return totalSupplyBalance;
    }

    /**
     * Sets the value of the totalSupplyBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalSupplyBalance(String value) {
        this.totalSupplyBalance = value;
    }

    /**
     * Gets the value of the totalAllocatedQtyFuture property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalAllocatedQtyFuture() {
        return totalAllocatedQtyFuture;
    }

    /**
     * Sets the value of the totalAllocatedQtyFuture property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalAllocatedQtyFuture(String value) {
        this.totalAllocatedQtyFuture = value;
    }

    /**
     * Gets the value of the totalUnallocatableQty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalUnallocatableQty() {
        return totalUnallocatableQty;
    }

    /**
     * Sets the value of the totalUnallocatableQty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalUnallocatableQty(String value) {
        this.totalUnallocatableQty = value;
    }

}
