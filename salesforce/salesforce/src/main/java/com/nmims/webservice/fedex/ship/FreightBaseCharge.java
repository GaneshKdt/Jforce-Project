
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Individual charge which contributes to the total base charge for the shipment.
 * 
 * <p>Java class for FreightBaseCharge complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FreightBaseCharge">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FreightClass" type="{http://fedex.com/ws/ship/v17}FreightClassType" minOccurs="0"/>
 *         &lt;element name="RatedAsClass" type="{http://fedex.com/ws/ship/v17}FreightClassType" minOccurs="0"/>
 *         &lt;element name="NmfcCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Weight" type="{http://fedex.com/ws/ship/v17}Weight" minOccurs="0"/>
 *         &lt;element name="ChargeRate" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="ChargeBasis" type="{http://fedex.com/ws/ship/v17}FreightChargeBasisType" minOccurs="0"/>
 *         &lt;element name="ExtendedAmount" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FreightBaseCharge", propOrder = {
    "freightClass",
    "ratedAsClass",
    "nmfcCode",
    "description",
    "weight",
    "chargeRate",
    "chargeBasis",
    "extendedAmount"
})
public class FreightBaseCharge {

    @XmlElement(name = "FreightClass")
    protected FreightClassType freightClass;
    @XmlElement(name = "RatedAsClass")
    protected FreightClassType ratedAsClass;
    @XmlElement(name = "NmfcCode")
    protected String nmfcCode;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Weight")
    protected Weight weight;
    @XmlElement(name = "ChargeRate")
    protected Money chargeRate;
    @XmlElement(name = "ChargeBasis")
    protected FreightChargeBasisType chargeBasis;
    @XmlElement(name = "ExtendedAmount")
    protected Money extendedAmount;

    /**
     * Gets the value of the freightClass property.
     * 
     * @return
     *     possible object is
     *     {@link FreightClassType }
     *     
     */
    public FreightClassType getFreightClass() {
        return freightClass;
    }

    /**
     * Sets the value of the freightClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightClassType }
     *     
     */
    public void setFreightClass(FreightClassType value) {
        this.freightClass = value;
    }

    /**
     * Gets the value of the ratedAsClass property.
     * 
     * @return
     *     possible object is
     *     {@link FreightClassType }
     *     
     */
    public FreightClassType getRatedAsClass() {
        return ratedAsClass;
    }

    /**
     * Sets the value of the ratedAsClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightClassType }
     *     
     */
    public void setRatedAsClass(FreightClassType value) {
        this.ratedAsClass = value;
    }

    /**
     * Gets the value of the nmfcCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNmfcCode() {
        return nmfcCode;
    }

    /**
     * Sets the value of the nmfcCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNmfcCode(String value) {
        this.nmfcCode = value;
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
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link Weight }
     *     
     */
    public Weight getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Weight }
     *     
     */
    public void setWeight(Weight value) {
        this.weight = value;
    }

    /**
     * Gets the value of the chargeRate property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getChargeRate() {
        return chargeRate;
    }

    /**
     * Sets the value of the chargeRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setChargeRate(Money value) {
        this.chargeRate = value;
    }

    /**
     * Gets the value of the chargeBasis property.
     * 
     * @return
     *     possible object is
     *     {@link FreightChargeBasisType }
     *     
     */
    public FreightChargeBasisType getChargeBasis() {
        return chargeBasis;
    }

    /**
     * Sets the value of the chargeBasis property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightChargeBasisType }
     *     
     */
    public void setChargeBasis(FreightChargeBasisType value) {
        this.chargeBasis = value;
    }

    /**
     * Gets the value of the extendedAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getExtendedAmount() {
        return extendedAmount;
    }

    /**
     * Sets the value of the extendedAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setExtendedAmount(Money value) {
        this.extendedAmount = value;
    }

}
