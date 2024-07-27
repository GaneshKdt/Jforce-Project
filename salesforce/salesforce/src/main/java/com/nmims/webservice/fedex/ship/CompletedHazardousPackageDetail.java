
package com.nmims.webservice.fedex.ship;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Completed package-level hazardous commodity information for a single package.
 * 
 * <p>Java class for CompletedHazardousPackageDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompletedHazardousPackageDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReferenceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Accessibility" type="{http://fedex.com/ws/ship/v17}DangerousGoodsAccessibilityType" minOccurs="0"/>
 *         &lt;element name="CargoAircraftOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Regulation" type="{http://fedex.com/ws/ship/v17}HazardousCommodityRegulationType" minOccurs="0"/>
 *         &lt;element name="RadioactiveTransportIndex" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="LabelType" type="{http://fedex.com/ws/ship/v17}RadioactiveLabelType" minOccurs="0"/>
 *         &lt;element name="Containers" type="{http://fedex.com/ws/ship/v17}ValidatedHazardousContainer" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompletedHazardousPackageDetail", propOrder = {
    "referenceId",
    "accessibility",
    "cargoAircraftOnly",
    "regulation",
    "radioactiveTransportIndex",
    "labelType",
    "containers"
})
public class CompletedHazardousPackageDetail {

    @XmlElement(name = "ReferenceId")
    protected String referenceId;
    @XmlElement(name = "Accessibility")
    protected DangerousGoodsAccessibilityType accessibility;
    @XmlElement(name = "CargoAircraftOnly")
    protected Boolean cargoAircraftOnly;
    @XmlElement(name = "Regulation")
    protected HazardousCommodityRegulationType regulation;
    @XmlElement(name = "RadioactiveTransportIndex")
    protected BigDecimal radioactiveTransportIndex;
    @XmlElement(name = "LabelType")
    protected RadioactiveLabelType labelType;
    @XmlElement(name = "Containers")
    protected List<ValidatedHazardousContainer> containers;

    /**
     * Gets the value of the referenceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * Sets the value of the referenceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceId(String value) {
        this.referenceId = value;
    }

    /**
     * Gets the value of the accessibility property.
     * 
     * @return
     *     possible object is
     *     {@link DangerousGoodsAccessibilityType }
     *     
     */
    public DangerousGoodsAccessibilityType getAccessibility() {
        return accessibility;
    }

    /**
     * Sets the value of the accessibility property.
     * 
     * @param value
     *     allowed object is
     *     {@link DangerousGoodsAccessibilityType }
     *     
     */
    public void setAccessibility(DangerousGoodsAccessibilityType value) {
        this.accessibility = value;
    }

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
     * Gets the value of the regulation property.
     * 
     * @return
     *     possible object is
     *     {@link HazardousCommodityRegulationType }
     *     
     */
    public HazardousCommodityRegulationType getRegulation() {
        return regulation;
    }

    /**
     * Sets the value of the regulation property.
     * 
     * @param value
     *     allowed object is
     *     {@link HazardousCommodityRegulationType }
     *     
     */
    public void setRegulation(HazardousCommodityRegulationType value) {
        this.regulation = value;
    }

    /**
     * Gets the value of the radioactiveTransportIndex property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRadioactiveTransportIndex() {
        return radioactiveTransportIndex;
    }

    /**
     * Sets the value of the radioactiveTransportIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRadioactiveTransportIndex(BigDecimal value) {
        this.radioactiveTransportIndex = value;
    }

    /**
     * Gets the value of the labelType property.
     * 
     * @return
     *     possible object is
     *     {@link RadioactiveLabelType }
     *     
     */
    public RadioactiveLabelType getLabelType() {
        return labelType;
    }

    /**
     * Sets the value of the labelType property.
     * 
     * @param value
     *     allowed object is
     *     {@link RadioactiveLabelType }
     *     
     */
    public void setLabelType(RadioactiveLabelType value) {
        this.labelType = value;
    }

    /**
     * Gets the value of the containers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the containers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContainers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValidatedHazardousContainer }
     * 
     * 
     */
    public List<ValidatedHazardousContainer> getContainers() {
        if (containers == null) {
            containers = new ArrayList<ValidatedHazardousContainer>();
        }
        return this.containers;
    }

}
