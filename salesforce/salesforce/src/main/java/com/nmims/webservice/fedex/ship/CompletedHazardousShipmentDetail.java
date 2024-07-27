
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Computed shipment level hazardous commodity information.
 * 
 * <p>Java class for CompletedHazardousShipmentDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompletedHazardousShipmentDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HazardousSummaryDetail" type="{http://fedex.com/ws/ship/v17}CompletedHazardousSummaryDetail" minOccurs="0"/>
 *         &lt;element name="DryIceDetail" type="{http://fedex.com/ws/ship/v17}ShipmentDryIceDetail" minOccurs="0"/>
 *         &lt;element name="AdrLicense" type="{http://fedex.com/ws/ship/v17}AdrLicenseDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompletedHazardousShipmentDetail", propOrder = {
    "hazardousSummaryDetail",
    "dryIceDetail",
    "adrLicense"
})
public class CompletedHazardousShipmentDetail {

    @XmlElement(name = "HazardousSummaryDetail")
    protected CompletedHazardousSummaryDetail hazardousSummaryDetail;
    @XmlElement(name = "DryIceDetail")
    protected ShipmentDryIceDetail dryIceDetail;
    @XmlElement(name = "AdrLicense")
    protected AdrLicenseDetail adrLicense;

    /**
     * Gets the value of the hazardousSummaryDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CompletedHazardousSummaryDetail }
     *     
     */
    public CompletedHazardousSummaryDetail getHazardousSummaryDetail() {
        return hazardousSummaryDetail;
    }

    /**
     * Sets the value of the hazardousSummaryDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletedHazardousSummaryDetail }
     *     
     */
    public void setHazardousSummaryDetail(CompletedHazardousSummaryDetail value) {
        this.hazardousSummaryDetail = value;
    }

    /**
     * Gets the value of the dryIceDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentDryIceDetail }
     *     
     */
    public ShipmentDryIceDetail getDryIceDetail() {
        return dryIceDetail;
    }

    /**
     * Sets the value of the dryIceDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentDryIceDetail }
     *     
     */
    public void setDryIceDetail(ShipmentDryIceDetail value) {
        this.dryIceDetail = value;
    }

    /**
     * Gets the value of the adrLicense property.
     * 
     * @return
     *     possible object is
     *     {@link AdrLicenseDetail }
     *     
     */
    public AdrLicenseDetail getAdrLicense() {
        return adrLicense;
    }

    /**
     * Sets the value of the adrLicense property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdrLicenseDetail }
     *     
     */
    public void setAdrLicense(AdrLicenseDetail value) {
        this.adrLicense = value;
    }

}
