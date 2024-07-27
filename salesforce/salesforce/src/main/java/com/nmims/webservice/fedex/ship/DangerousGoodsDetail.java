
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The descriptive data required for a FedEx shipment containing dangerous goods (hazardous materials).
 * 
 * <p>Java class for DangerousGoodsDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DangerousGoodsDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Regulation" type="{http://fedex.com/ws/ship/v17}HazardousCommodityRegulationType" minOccurs="0"/>
 *         &lt;element name="Accessibility" type="{http://fedex.com/ws/ship/v17}DangerousGoodsAccessibilityType" minOccurs="0"/>
 *         &lt;element name="CargoAircraftOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Options" type="{http://fedex.com/ws/ship/v17}HazardousCommodityOptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="PackingOption" type="{http://fedex.com/ws/ship/v17}DangerousGoodsPackingOptionType" minOccurs="0"/>
 *         &lt;element name="ReferenceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Containers" type="{http://fedex.com/ws/ship/v17}DangerousGoodsContainer" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Packaging" type="{http://fedex.com/ws/ship/v17}HazardousCommodityPackagingDetail" minOccurs="0"/>
 *         &lt;element name="Signatory" type="{http://fedex.com/ws/ship/v17}DangerousGoodsSignatory" minOccurs="0"/>
 *         &lt;element name="EmergencyContactNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Offeror" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InfectiousSubstanceResponsibleContact" type="{http://fedex.com/ws/ship/v17}Contact" minOccurs="0"/>
 *         &lt;element name="AdditionalHandling" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RadioactivityDetail" type="{http://fedex.com/ws/ship/v17}RadioactivityDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DangerousGoodsDetail", propOrder = {
    "regulation",
    "accessibility",
    "cargoAircraftOnly",
    "options",
    "packingOption",
    "referenceId",
    "containers",
    "packaging",
    "signatory",
    "emergencyContactNumber",
    "offeror",
    "infectiousSubstanceResponsibleContact",
    "additionalHandling",
    "radioactivityDetail"
})
public class DangerousGoodsDetail {

    @XmlElement(name = "Regulation")
    protected HazardousCommodityRegulationType regulation;
    @XmlElement(name = "Accessibility")
    protected DangerousGoodsAccessibilityType accessibility;
    @XmlElement(name = "CargoAircraftOnly")
    protected Boolean cargoAircraftOnly;
    @XmlElement(name = "Options")
    protected List<HazardousCommodityOptionType> options;
    @XmlElement(name = "PackingOption")
    protected DangerousGoodsPackingOptionType packingOption;
    @XmlElement(name = "ReferenceId")
    protected String referenceId;
    @XmlElement(name = "Containers")
    protected List<DangerousGoodsContainer> containers;
    @XmlElement(name = "Packaging")
    protected HazardousCommodityPackagingDetail packaging;
    @XmlElement(name = "Signatory")
    protected DangerousGoodsSignatory signatory;
    @XmlElement(name = "EmergencyContactNumber")
    protected String emergencyContactNumber;
    @XmlElement(name = "Offeror")
    protected String offeror;
    @XmlElement(name = "InfectiousSubstanceResponsibleContact")
    protected Contact infectiousSubstanceResponsibleContact;
    @XmlElement(name = "AdditionalHandling")
    protected String additionalHandling;
    @XmlElement(name = "RadioactivityDetail")
    protected RadioactivityDetail radioactivityDetail;

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
     * Gets the value of the options property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the options property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOptions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HazardousCommodityOptionType }
     * 
     * 
     */
    public List<HazardousCommodityOptionType> getOptions() {
        if (options == null) {
            options = new ArrayList<HazardousCommodityOptionType>();
        }
        return this.options;
    }

    /**
     * Gets the value of the packingOption property.
     * 
     * @return
     *     possible object is
     *     {@link DangerousGoodsPackingOptionType }
     *     
     */
    public DangerousGoodsPackingOptionType getPackingOption() {
        return packingOption;
    }

    /**
     * Sets the value of the packingOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link DangerousGoodsPackingOptionType }
     *     
     */
    public void setPackingOption(DangerousGoodsPackingOptionType value) {
        this.packingOption = value;
    }

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
     * {@link DangerousGoodsContainer }
     * 
     * 
     */
    public List<DangerousGoodsContainer> getContainers() {
        if (containers == null) {
            containers = new ArrayList<DangerousGoodsContainer>();
        }
        return this.containers;
    }

    /**
     * Gets the value of the packaging property.
     * 
     * @return
     *     possible object is
     *     {@link HazardousCommodityPackagingDetail }
     *     
     */
    public HazardousCommodityPackagingDetail getPackaging() {
        return packaging;
    }

    /**
     * Sets the value of the packaging property.
     * 
     * @param value
     *     allowed object is
     *     {@link HazardousCommodityPackagingDetail }
     *     
     */
    public void setPackaging(HazardousCommodityPackagingDetail value) {
        this.packaging = value;
    }

    /**
     * Gets the value of the signatory property.
     * 
     * @return
     *     possible object is
     *     {@link DangerousGoodsSignatory }
     *     
     */
    public DangerousGoodsSignatory getSignatory() {
        return signatory;
    }

    /**
     * Sets the value of the signatory property.
     * 
     * @param value
     *     allowed object is
     *     {@link DangerousGoodsSignatory }
     *     
     */
    public void setSignatory(DangerousGoodsSignatory value) {
        this.signatory = value;
    }

    /**
     * Gets the value of the emergencyContactNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    /**
     * Sets the value of the emergencyContactNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmergencyContactNumber(String value) {
        this.emergencyContactNumber = value;
    }

    /**
     * Gets the value of the offeror property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfferor() {
        return offeror;
    }

    /**
     * Sets the value of the offeror property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfferor(String value) {
        this.offeror = value;
    }

    /**
     * Gets the value of the infectiousSubstanceResponsibleContact property.
     * 
     * @return
     *     possible object is
     *     {@link Contact }
     *     
     */
    public Contact getInfectiousSubstanceResponsibleContact() {
        return infectiousSubstanceResponsibleContact;
    }

    /**
     * Sets the value of the infectiousSubstanceResponsibleContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link Contact }
     *     
     */
    public void setInfectiousSubstanceResponsibleContact(Contact value) {
        this.infectiousSubstanceResponsibleContact = value;
    }

    /**
     * Gets the value of the additionalHandling property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalHandling() {
        return additionalHandling;
    }

    /**
     * Sets the value of the additionalHandling property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalHandling(String value) {
        this.additionalHandling = value;
    }

    /**
     * Gets the value of the radioactivityDetail property.
     * 
     * @return
     *     possible object is
     *     {@link RadioactivityDetail }
     *     
     */
    public RadioactivityDetail getRadioactivityDetail() {
        return radioactivityDetail;
    }

    /**
     * Sets the value of the radioactivityDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link RadioactivityDetail }
     *     
     */
    public void setRadioactivityDetail(RadioactivityDetail value) {
        this.radioactivityDetail = value;
    }

}
