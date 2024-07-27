
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrackingId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrackingId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TrackingIdType" type="{http://fedex.com/ws/ship/v17}TrackingIdType" minOccurs="0"/>
 *         &lt;element name="FormId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UspsApplicationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TrackingNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrackingId", propOrder = {
    "trackingIdType",
    "formId",
    "uspsApplicationId",
    "trackingNumber"
})
public class TrackingId {

    @XmlElement(name = "TrackingIdType")
    protected TrackingIdType trackingIdType;
    @XmlElement(name = "FormId")
    protected String formId;
    @XmlElement(name = "UspsApplicationId")
    protected String uspsApplicationId;
    @XmlElement(name = "TrackingNumber")
    protected String trackingNumber;

    /**
     * Gets the value of the trackingIdType property.
     * 
     * @return
     *     possible object is
     *     {@link TrackingIdType }
     *     
     */
    public TrackingIdType getTrackingIdType() {
        return trackingIdType;
    }

    /**
     * Sets the value of the trackingIdType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrackingIdType }
     *     
     */
    public void setTrackingIdType(TrackingIdType value) {
        this.trackingIdType = value;
    }

    /**
     * Gets the value of the formId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormId() {
        return formId;
    }

    /**
     * Sets the value of the formId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormId(String value) {
        this.formId = value;
    }

    /**
     * Gets the value of the uspsApplicationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUspsApplicationId() {
        return uspsApplicationId;
    }

    /**
     * Sets the value of the uspsApplicationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUspsApplicationId(String value) {
        this.uspsApplicationId = value;
    }

    /**
     * Gets the value of the trackingNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /**
     * Sets the value of the trackingNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrackingNumber(String value) {
        this.trackingNumber = value;
    }

}
