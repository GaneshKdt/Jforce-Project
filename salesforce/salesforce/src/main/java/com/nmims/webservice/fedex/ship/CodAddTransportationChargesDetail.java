
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CodAddTransportationChargesDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CodAddTransportationChargesDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RateTypeBasis" type="{http://fedex.com/ws/ship/v17}RateTypeBasisType" minOccurs="0"/>
 *         &lt;element name="ChargeBasis" type="{http://fedex.com/ws/ship/v17}CodAddTransportationChargeBasisType" minOccurs="0"/>
 *         &lt;element name="ChargeBasisLevel" type="{http://fedex.com/ws/ship/v17}ChargeBasisLevelType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodAddTransportationChargesDetail", propOrder = {
    "rateTypeBasis",
    "chargeBasis",
    "chargeBasisLevel"
})
public class CodAddTransportationChargesDetail {

    @XmlElement(name = "RateTypeBasis")
    protected RateTypeBasisType rateTypeBasis;
    @XmlElement(name = "ChargeBasis")
    protected CodAddTransportationChargeBasisType chargeBasis;
    @XmlElement(name = "ChargeBasisLevel")
    protected ChargeBasisLevelType chargeBasisLevel;

    /**
     * Gets the value of the rateTypeBasis property.
     * 
     * @return
     *     possible object is
     *     {@link RateTypeBasisType }
     *     
     */
    public RateTypeBasisType getRateTypeBasis() {
        return rateTypeBasis;
    }

    /**
     * Sets the value of the rateTypeBasis property.
     * 
     * @param value
     *     allowed object is
     *     {@link RateTypeBasisType }
     *     
     */
    public void setRateTypeBasis(RateTypeBasisType value) {
        this.rateTypeBasis = value;
    }

    /**
     * Gets the value of the chargeBasis property.
     * 
     * @return
     *     possible object is
     *     {@link CodAddTransportationChargeBasisType }
     *     
     */
    public CodAddTransportationChargeBasisType getChargeBasis() {
        return chargeBasis;
    }

    /**
     * Sets the value of the chargeBasis property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodAddTransportationChargeBasisType }
     *     
     */
    public void setChargeBasis(CodAddTransportationChargeBasisType value) {
        this.chargeBasis = value;
    }

    /**
     * Gets the value of the chargeBasisLevel property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeBasisLevelType }
     *     
     */
    public ChargeBasisLevelType getChargeBasisLevel() {
        return chargeBasisLevel;
    }

    /**
     * Sets the value of the chargeBasisLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeBasisLevelType }
     *     
     */
    public void setChargeBasisLevel(ChargeBasisLevelType value) {
        this.chargeBasisLevel = value;
    }

}
