
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ShipmentManifestDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShipmentManifestDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ManifestReferenceType" type="{http://fedex.com/ws/ship/v17}CustomerReferenceType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipmentManifestDetail", propOrder = {
    "manifestReferenceType"
})
public class ShipmentManifestDetail {

    @XmlElement(name = "ManifestReferenceType")
    protected CustomerReferenceType manifestReferenceType;

    /**
     * Gets the value of the manifestReferenceType property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerReferenceType }
     *     
     */
    public CustomerReferenceType getManifestReferenceType() {
        return manifestReferenceType;
    }

    /**
     * Sets the value of the manifestReferenceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerReferenceType }
     *     
     */
    public void setManifestReferenceType(CustomerReferenceType value) {
        this.manifestReferenceType = value;
    }

}
