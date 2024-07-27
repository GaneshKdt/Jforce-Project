
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies an individual recipient of e-mailed shipping document(s).
 * 
 * <p>Java class for ShippingDocumentEMailRecipient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShippingDocumentEMailRecipient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RecipientType" type="{http://fedex.com/ws/ship/v17}EMailNotificationRecipientType" minOccurs="0"/>
 *         &lt;element name="Address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShippingDocumentEMailRecipient", propOrder = {
    "recipientType",
    "address"
})
public class ShippingDocumentEMailRecipient {

    @XmlElement(name = "RecipientType")
    protected EMailNotificationRecipientType recipientType;
    @XmlElement(name = "Address")
    protected String address;

    /**
     * Gets the value of the recipientType property.
     * 
     * @return
     *     possible object is
     *     {@link EMailNotificationRecipientType }
     *     
     */
    public EMailNotificationRecipientType getRecipientType() {
        return recipientType;
    }

    /**
     * Sets the value of the recipientType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EMailNotificationRecipientType }
     *     
     */
    public void setRecipientType(EMailNotificationRecipientType value) {
        this.recipientType = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

}
