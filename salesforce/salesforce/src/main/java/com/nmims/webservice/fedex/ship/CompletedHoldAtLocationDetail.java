
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompletedHoldAtLocationDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompletedHoldAtLocationDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HoldingLocation" type="{http://fedex.com/ws/ship/v17}ContactAndAddress" minOccurs="0"/>
 *         &lt;element name="HoldingLocationType" type="{http://fedex.com/ws/ship/v17}FedExLocationType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompletedHoldAtLocationDetail", propOrder = {
    "holdingLocation",
    "holdingLocationType"
})
public class CompletedHoldAtLocationDetail {

    @XmlElement(name = "HoldingLocation")
    protected ContactAndAddress holdingLocation;
    @XmlElement(name = "HoldingLocationType")
    protected FedExLocationType holdingLocationType;

    /**
     * Gets the value of the holdingLocation property.
     * 
     * @return
     *     possible object is
     *     {@link ContactAndAddress }
     *     
     */
    public ContactAndAddress getHoldingLocation() {
        return holdingLocation;
    }

    /**
     * Sets the value of the holdingLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactAndAddress }
     *     
     */
    public void setHoldingLocation(ContactAndAddress value) {
        this.holdingLocation = value;
    }

    /**
     * Gets the value of the holdingLocationType property.
     * 
     * @return
     *     possible object is
     *     {@link FedExLocationType }
     *     
     */
    public FedExLocationType getHoldingLocationType() {
        return holdingLocationType;
    }

    /**
     * Sets the value of the holdingLocationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link FedExLocationType }
     *     
     */
    public void setHoldingLocationType(FedExLocationType value) {
        this.holdingLocationType = value;
    }

}
