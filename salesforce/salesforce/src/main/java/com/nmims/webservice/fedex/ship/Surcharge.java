
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Identifies each surcharge applied to the shipment.
 * 
 * <p>Java class for Surcharge complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Surcharge">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SurchargeType" type="{http://fedex.com/ws/ship/v17}SurchargeType" minOccurs="0"/>
 *         &lt;element name="Level" type="{http://fedex.com/ws/ship/v17}SurchargeLevelType" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Amount" type="{http://fedex.com/ws/ship/v17}Money"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Surcharge", propOrder = {
    "surchargeType",
    "level",
    "description",
    "amount"
})
public class Surcharge {

    @XmlElement(name = "SurchargeType")
    protected SurchargeType surchargeType;
    @XmlElement(name = "Level")
    protected SurchargeLevelType level;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Amount", required = true)
    protected Money amount;

    /**
     * Gets the value of the surchargeType property.
     * 
     * @return
     *     possible object is
     *     {@link SurchargeType }
     *     
     */
    public SurchargeType getSurchargeType() {
        return surchargeType;
    }

    /**
     * Sets the value of the surchargeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SurchargeType }
     *     
     */
    public void setSurchargeType(SurchargeType value) {
        this.surchargeType = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link SurchargeLevelType }
     *     
     */
    public SurchargeLevelType getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link SurchargeLevelType }
     *     
     */
    public void setLevel(SurchargeLevelType value) {
        this.level = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setAmount(Money value) {
        this.amount = value;
    }

}
