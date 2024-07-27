
package com.nmims.webservice.fedex.ship;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Identifies and describes an individual hazardous commodity. For 201001 load, this is based on data from the FedEx Ground Hazardous Materials Shipping Guide.
 * 
 * <p>Java class for ValidatedHazardousCommodityDescription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValidatedHazardousCommodityDescription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SequenceNumber" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="PackingGroup" type="{http://fedex.com/ws/ship/v17}HazardousCommodityPackingGroupType" minOccurs="0"/>
 *         &lt;element name="PackingInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProperShippingName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProperShippingNameAndDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TechnicalName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HazardClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubsidiaryClasses" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Symbols" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TunnelRestrictionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SpecialProvisions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Attributes" type="{http://fedex.com/ws/ship/v17}HazardousCommodityAttributeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Authorization" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LabelText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidatedHazardousCommodityDescription", propOrder = {
    "id",
    "sequenceNumber",
    "packingGroup",
    "packingInstructions",
    "properShippingName",
    "properShippingNameAndDescription",
    "technicalName",
    "hazardClass",
    "subsidiaryClasses",
    "symbols",
    "tunnelRestrictionCode",
    "specialProvisions",
    "attributes",
    "authorization",
    "labelText"
})
public class ValidatedHazardousCommodityDescription {

    @XmlElement(name = "Id")
    protected String id;
    @XmlElement(name = "SequenceNumber")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sequenceNumber;
    @XmlElement(name = "PackingGroup")
    protected HazardousCommodityPackingGroupType packingGroup;
    @XmlElement(name = "PackingInstructions")
    protected String packingInstructions;
    @XmlElement(name = "ProperShippingName")
    protected String properShippingName;
    @XmlElement(name = "ProperShippingNameAndDescription")
    protected String properShippingNameAndDescription;
    @XmlElement(name = "TechnicalName")
    protected String technicalName;
    @XmlElement(name = "HazardClass")
    protected String hazardClass;
    @XmlElement(name = "SubsidiaryClasses")
    protected List<String> subsidiaryClasses;
    @XmlElement(name = "Symbols")
    protected String symbols;
    @XmlElement(name = "TunnelRestrictionCode")
    protected String tunnelRestrictionCode;
    @XmlElement(name = "SpecialProvisions")
    protected String specialProvisions;
    @XmlElement(name = "Attributes")
    protected List<HazardousCommodityAttributeType> attributes;
    @XmlElement(name = "Authorization")
    protected String authorization;
    @XmlElement(name = "LabelText")
    protected String labelText;

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
     * Gets the value of the properShippingNameAndDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProperShippingNameAndDescription() {
        return properShippingNameAndDescription;
    }

    /**
     * Sets the value of the properShippingNameAndDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProperShippingNameAndDescription(String value) {
        this.properShippingNameAndDescription = value;
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
     * Gets the value of the symbols property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSymbols() {
        return symbols;
    }

    /**
     * Sets the value of the symbols property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSymbols(String value) {
        this.symbols = value;
    }

    /**
     * Gets the value of the tunnelRestrictionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTunnelRestrictionCode() {
        return tunnelRestrictionCode;
    }

    /**
     * Sets the value of the tunnelRestrictionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTunnelRestrictionCode(String value) {
        this.tunnelRestrictionCode = value;
    }

    /**
     * Gets the value of the specialProvisions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialProvisions() {
        return specialProvisions;
    }

    /**
     * Sets the value of the specialProvisions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialProvisions(String value) {
        this.specialProvisions = value;
    }

    /**
     * Gets the value of the attributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HazardousCommodityAttributeType }
     * 
     * 
     */
    public List<HazardousCommodityAttributeType> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<HazardousCommodityAttributeType>();
        }
        return this.attributes;
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

}
