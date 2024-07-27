
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InternationalTrafficInArmsRegulationsDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InternationalTrafficInArmsRegulationsDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LicenseOrExemptionNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InternationalTrafficInArmsRegulationsDetail", propOrder = {
    "licenseOrExemptionNumber"
})
public class InternationalTrafficInArmsRegulationsDetail {

    @XmlElement(name = "LicenseOrExemptionNumber")
    protected String licenseOrExemptionNumber;

    /**
     * Gets the value of the licenseOrExemptionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseOrExemptionNumber() {
        return licenseOrExemptionNumber;
    }

    /**
     * Sets the value of the licenseOrExemptionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseOrExemptionNumber(String value) {
        this.licenseOrExemptionNumber = value;
    }

}
