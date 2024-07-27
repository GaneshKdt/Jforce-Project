
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BrokerDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BrokerDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://fedex.com/ws/ship/v17}BrokerType" minOccurs="0"/>
 *         &lt;element name="Broker" type="{http://fedex.com/ws/ship/v17}Party" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BrokerDetail", propOrder = {
    "type",
    "broker"
})
public class BrokerDetail {

    @XmlElement(name = "Type")
    protected BrokerType type;
    @XmlElement(name = "Broker")
    protected Party broker;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link BrokerType }
     *     
     */
    public BrokerType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link BrokerType }
     *     
     */
    public void setType(BrokerType value) {
        this.type = value;
    }

    /**
     * Gets the value of the broker property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getBroker() {
        return broker;
    }

    /**
     * Sets the value of the broker property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setBroker(Party value) {
        this.broker = value;
    }

}
