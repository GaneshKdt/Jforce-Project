
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Descriptive data required for a FedEx shipment that is to be held at the destination FedEx location for pickup by the recipient.
 * 
 * <p>Java class for HoldAtLocationDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HoldAtLocationDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LocationContactAndAddress" type="{http://fedex.com/ws/ship/v17}ContactAndAddress" minOccurs="0"/>
 *         &lt;element name="LocationType" type="{http://fedex.com/ws/ship/v17}FedExLocationType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HoldAtLocationDetail", propOrder = {
    "phoneNumber",
    "locationContactAndAddress",
    "locationType"
})
public class HoldAtLocationDetail {

    @XmlElement(name = "PhoneNumber", required = true)
    protected String phoneNumber;
    @XmlElement(name = "LocationContactAndAddress")
    protected ContactAndAddress locationContactAndAddress;
    @XmlElement(name = "LocationType")
    protected FedExLocationType locationType;

    /**
     * Gets the value of the phoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the locationContactAndAddress property.
     * 
     * @return
     *     possible object is
     *     {@link ContactAndAddress }
     *     
     */
    public ContactAndAddress getLocationContactAndAddress() {
        return locationContactAndAddress;
    }

    /**
     * Sets the value of the locationContactAndAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactAndAddress }
     *     
     */
    public void setLocationContactAndAddress(ContactAndAddress value) {
        this.locationContactAndAddress = value;
    }

    /**
     * Gets the value of the locationType property.
     * 
     * @return
     *     possible object is
     *     {@link FedExLocationType }
     *     
     */
    public FedExLocationType getLocationType() {
        return locationType;
    }

    /**
     * Sets the value of the locationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link FedExLocationType }
     *     
     */
    public void setLocationType(FedExLocationType value) {
        this.locationType = value;
    }

}
