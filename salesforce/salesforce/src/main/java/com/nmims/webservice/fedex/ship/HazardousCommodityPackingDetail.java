
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies documentation and limits for validation of an individual packing group/category.
 * 
 * <p>Java class for HazardousCommodityPackingDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HazardousCommodityPackingDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CargoAircraftOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="PackingInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HazardousCommodityPackingDetail", propOrder = {
    "cargoAircraftOnly",
    "packingInstructions"
})
public class HazardousCommodityPackingDetail {

    @XmlElement(name = "CargoAircraftOnly")
    protected Boolean cargoAircraftOnly;
    @XmlElement(name = "PackingInstructions")
    protected String packingInstructions;

    /**
     * Gets the value of the cargoAircraftOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCargoAircraftOnly() {
        return cargoAircraftOnly;
    }

    /**
     * Sets the value of the cargoAircraftOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCargoAircraftOnly(Boolean value) {
        this.cargoAircraftOnly = value;
    }

    /**
     * Gets the value of the packingInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackingInstructions() {
        return packingInstructions;
    }

    /**
     * Sets the value of the packingInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackingInstructions(String value) {
        this.packingInstructions = value;
    }

}
