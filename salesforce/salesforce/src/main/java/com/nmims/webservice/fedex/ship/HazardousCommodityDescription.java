
package com.nmims.webservice.fedex.ship;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Identifies and describes an individual hazardous commodity.
 * 
 * <p>Java class for HazardousCommodityDescription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HazardousCommodityDescription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SequenceNumber" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="PackingGroup" type="{http://fedex.com/ws/ship/v17}HazardousCommodityPackingGroupType" minOccurs="0"/>
 *         &lt;element name="PackingDetails" type="{http://fedex.com/ws/ship/v17}HazardousCommodityPackingDetail" minOccurs="0"/>
 *         &lt;element name="ReportableQuantity" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ProperShippingName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TechnicalName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Percentage" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="HazardClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubsidiaryClasses" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="LabelText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProcessingOptions" type="{http://fedex.com/ws/ship/v17}HazardousCommodityDescriptionProcessingOptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Authorization" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HazardousCommodityDescription", propOrder = {
    "id",
    "sequenceNumber",
    "packingGroup",
    "packingDetails",
    "reportableQuantity",
    "properShippingName",
    "technicalName",
    "percentage",
    "hazardClass",
    "subsidiaryClasses",
    "labelText",
    "processingOptions",
    "authorization"
})
public class HazardousCommodityDescription {

    @XmlElement(name = "Id")
    protected String id;
    @XmlElement(name = "SequenceNumber")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sequenceNumber;
    @XmlElement(name = "PackingGroup")
    protected HazardousCommodityPackingGroupType packingGroup;
    @XmlElement(name = "PackingDetails")
    protected HazardousCommodityPackingDetail packingDetails;
    @XmlElement(name = "ReportableQuantity")
    protected Boolean reportableQuantity;
    @XmlElement(name = "ProperShippingName")
    protected String properShippingName;
    @XmlElement(name = "TechnicalName")
    protected String technicalName;
    @XmlElement(name = "Percentage")
    protected BigDecimal percentage;
    @XmlElement(name = "HazardClass")
    protected String hazardClass;
    @XmlElement(name = "SubsidiaryClasses")
    protected List<String> subsidiaryClasses;
    @XmlElement(name = "LabelText")
    protected String labelText;
    @XmlElement(name = "ProcessingOptions")
    protected List<HazardousCommodityDescriptionProcessingOptionType> processingOptions;
    @XmlElement(name = "Authorization")
    protected String authorization;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSequenceNumber(BigInteger value) {
        this.sequenceNumber = value;
    }

    /**
     * Gets the value of the packingGroup property.
     * 
     * @return
     *     possible object is
     *     {@link HazardousCommodityPackingGroupType }
     *     
     */
    public HazardousCommodityPackingGroupType getPackingGroup() {
        return packingGroup;
    }

    /**
     * Sets the value of the packingGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link HazardousCommodityPackingGroupType }
     *     
     */
    public void setPackingGroup(HazardousCommodityPackingGroupType value) {
        this.packingGroup = value;
    }

    /**
     * Gets the value of the packingDetails property.
     * 
     * @return
     *     possible object is
     *     {@link HazardousCommodityPackingDetail }
     *     
     */
    public HazardousCommodityPackingDetail getPackingDetails() {
        return packingDetails;
    }

    /**
     * Sets the value of the packingDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link HazardousCommodityPackingDetail }
     *     
     */
    public void setPackingDetails(HazardousCommodityPackingDetail value) {
        this.packingDetails = value;
    }

    /**
     * Gets the value of the reportableQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReportableQuantity() {
        return reportableQuantity;
    }

    /**
     * Sets the value of the reportableQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReportableQuantity(Boolean value) {
        this.reportableQuantity = value;
    }

    /**
     * Gets the value of the properShippingName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProperShippingName() {
        return properShippingName;
    }

    /**
     * Sets the value of the properShippingName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProperShippingName(String value) {
        this.properShippingName = value;
    }

    /**
     * Gets the value of the technicalName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTechnicalName() {
        return technicalName;
    }

    /**
     * Sets the value of the technicalName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTechnicalName(String value) {
        this.technicalName = value;
    }

    /**
     * Gets the value of the percentage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPercentage() {
        return percentage;
    }

    /**
     * Sets the value of the percentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPercentage(BigDecimal value) {
        this.percentage = value;
    }

    /**
     * Gets the value of the hazardClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHazardClass() {
        return hazardClass;
    }

    /**
     * Sets the value of the hazardClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHazardClass(String value) {
        this.hazardClass = value;
    }

    /**
     * Gets the value of the subsidiaryClasses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subsidiaryClasses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubsidiaryClasses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSubsidiaryClasses() {
        if (subsidiaryClasses == null) {
            subsidiaryClasses = new ArrayList<String>();
        }
        return this.subsidiaryClasses;
    }

    /**
     * Gets the value of the labelText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabelText() {
        return labelText;
    }

    /**
     * Sets the value of the labelText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabelText(String value) {
        this.labelText = value;
    }

    /**
     * Gets the value of the processingOptions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processingOptions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessingOptions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HazardousCommodityDescriptionProcessingOptionType }
     * 
     * 
     */
    public List<HazardousCommodityDescriptionProcessingOptionType> getProcessingOptions() {
        if (processingOptions == null) {
            processingOptions = new ArrayList<HazardousCommodityDescriptionProcessingOptionType>();
        }
        return this.processingOptions;
    }

    /**
     * Gets the value of the authorization property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorization() {
        return authorization;
    }

    /**
     * Sets the value of the authorization property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorization(String value) {
        this.authorization = value;
    }

}
