
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies which party will be responsible for payment of any surcharges for Freight special services for which split billing is allowed.
 * 
 * <p>Java class for FreightSpecialServicePayment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FreightSpecialServicePayment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SpecialService" type="{http://fedex.com/ws/ship/v17}ShipmentSpecialServiceType" minOccurs="0"/>
 *         &lt;element name="PaymentType" type="{http://fedex.com/ws/ship/v17}FreightShipmentRoleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FreightSpecialServicePayment", propOrder = {
    "specialService",
    "paymentType"
})
public class FreightSpecialServicePayment {

    @XmlElement(name = "SpecialService")
    protected ShipmentSpecialServiceType specialService;
    @XmlElement(name = "PaymentType")
    protected FreightShipmentRoleType paymentType;

    /**
     * Gets the value of the specialService property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentSpecialServiceType }
     *     
     */
    public ShipmentSpecialServiceType getSpecialService() {
        return specialService;
    }

    /**
     * Sets the value of the specialService property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentSpecialServiceType }
     *     
     */
    public void setSpecialService(ShipmentSpecialServiceType value) {
        this.specialService = value;
    }

    /**
     * Gets the value of the paymentType property.
     * 
     * @return
     *     possible object is
     *     {@link FreightShipmentRoleType }
     *     
     */
    public FreightShipmentRoleType getPaymentType() {
        return paymentType;
    }

    /**
     * Sets the value of the paymentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightShipmentRoleType }
     *     
     */
    public void setPaymentType(FreightShipmentRoleType value) {
        this.paymentType = value;
    }

}
