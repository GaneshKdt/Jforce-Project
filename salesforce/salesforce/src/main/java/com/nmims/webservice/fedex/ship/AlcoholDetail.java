
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies details for a package containing alcohol
 * 
 * <p>Java class for AlcoholDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AlcoholDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RecipientType" type="{http://fedex.com/ws/ship/v17}AlcoholRecipientType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AlcoholDetail", propOrder = {
    "recipientType"
})
public class AlcoholDetail {

    @XmlElement(name = "RecipientType")
    protected AlcoholRecipientType recipientType;

    /**
     * Gets the value of the recipientType property.
     * 
     * @return
     *     possible object is
     *     {@link AlcoholRecipientType }
     *     
     */
    public AlcoholRecipientType getRecipientType() {
        return recipientType;
    }

    /**
     * Sets the value of the recipientType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlcoholRecipientType }
     *     
     */
    public void setRecipientType(AlcoholRecipientType value) {
        this.recipientType = value;
    }

}
