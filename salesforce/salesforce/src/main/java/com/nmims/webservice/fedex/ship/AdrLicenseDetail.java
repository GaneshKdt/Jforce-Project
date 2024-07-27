
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies the details around the ADR license required for shipping.
 * 
 * <p>Java class for AdrLicenseDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdrLicenseDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LicenseOrPermitDetail" type="{http://fedex.com/ws/ship/v17}LicenseOrPermitDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdrLicenseDetail", propOrder = {
    "licenseOrPermitDetail"
})
public class AdrLicenseDetail {

    @XmlElement(name = "LicenseOrPermitDetail")
    protected LicenseOrPermitDetail licenseOrPermitDetail;

    /**
     * Gets the value of the licenseOrPermitDetail property.
     * 
     * @return
     *     possible object is
     *     {@link LicenseOrPermitDetail }
     *     
     */
    public LicenseOrPermitDetail getLicenseOrPermitDetail() {
        return licenseOrPermitDetail;
    }

    /**
     * Sets the value of the licenseOrPermitDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link LicenseOrPermitDetail }
     *     
     */
    public void setLicenseOrPermitDetail(LicenseOrPermitDetail value) {
        this.licenseOrPermitDetail = value;
    }

}
