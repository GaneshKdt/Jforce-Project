
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
 * This class rationalizes RequestedPackage and RequestedPackageSummary from previous interfaces.
 * 
 * <p>Java class for RequestedPackageLineItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestedPackageLineItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SequenceNumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="GroupNumber" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="GroupPackageCount" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="VariableHandlingChargeDetail" type="{http://fedex.com/ws/ship/v17}VariableHandlingChargeDetail" minOccurs="0"/>
 *         &lt;element name="InsuredValue" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="Weight" type="{http://fedex.com/ws/ship/v17}Weight" minOccurs="0"/>
 *         &lt;element name="Dimensions" type="{http://fedex.com/ws/ship/v17}Dimensions" minOccurs="0"/>
 *         &lt;element name="PhysicalPackaging" type="{http://fedex.com/ws/ship/v17}PhysicalPackagingType" minOccurs="0"/>
 *         &lt;element name="ItemDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ItemDescriptionForClearance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CustomerReferences" type="{http://fedex.com/ws/ship/v17}CustomerReference" maxOccurs="10" minOccurs="0"/>
 *         &lt;element name="SpecialServicesRequested" type="{http://fedex.com/ws/ship/v17}PackageSpecialServicesRequested" minOccurs="0"/>
 *         &lt;element name="ContentRecords" type="{http://fedex.com/ws/ship/v17}ContentRecord" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestedPackageLineItem", propOrder = {
    "sequenceNumber",
    "groupNumber",
    "groupPackageCount",
    "variableHandlingChargeDetail",
    "insuredValue",
    "weight",
    "dimensions",
    "physicalPackaging",
    "itemDescription",
    "itemDescriptionForClearance",
    "customerReferences",
    "specialServicesRequested",
    "contentRecords"
})
public class RequestedPackageLineItem {

    @XmlElement(name = "SequenceNumber")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger sequenceNumber;
    @XmlElement(name = "GroupNumber")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger groupNumber;
    @XmlElement(name = "GroupPackageCount")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger groupPackageCount;
    @XmlElement(name = "VariableHandlingChargeDetail")
    protected VariableHandlingChargeDetail variableHandlingChargeDetail;
    @XmlElement(name = "InsuredValue")
    protected Money insuredValue;
    @XmlElement(name = "Weight")
    protected Weight weight;
    @XmlElement(name = "Dimensions")
    protected Dimensions dimensions;
    @XmlElement(name = "PhysicalPackaging")
    protected PhysicalPackagingType physicalPackaging;
    @XmlElement(name = "ItemDescription")
    protected String itemDescription;
    @XmlElement(name = "ItemDescriptionForClearance")
    protected String itemDescriptionForClearance;
    @XmlElement(name = "CustomerReferences")
    protected List<CustomerReference> customerReferences;
    @XmlElement(name = "SpecialServicesRequested")
    protected PackageSpecialServicesRequested specialServicesRequested;
    @XmlElement(name = "ContentRecords")
    protected List<ContentRecord> contentRecords;

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
     * Gets the value of the groupNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getGroupNumber() {
        return groupNumber;
    }

    /**
     * Sets the value of the groupNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setGroupNumber(BigInteger value) {
        this.groupNumber = value;
    }

    /**
     * Gets the value of the groupPackageCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getGroupPackageCount() {
        return groupPackageCount;
    }

    /**
     * Sets the value of the groupPackageCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setGroupPackageCount(BigInteger value) {
        this.groupPackageCount = value;
    }

    /**
     * Gets the value of the variableHandlingChargeDetail property.
     * 
     * @return
     *     possible object is
     *     {@link VariableHandlingChargeDetail }
     *     
     */
    public VariableHandlingChargeDetail getVariableHandlingChargeDetail() {
        return variableHandlingChargeDetail;
    }

    /**
     * Sets the value of the variableHandlingChargeDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link VariableHandlingChargeDetail }
     *     
     */
    public void setVariableHandlingChargeDetail(VariableHandlingChargeDetail value) {
        this.variableHandlingChargeDetail = value;
    }

    /**
     * Gets the value of the insuredValue property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getInsuredValue() {
        return insuredValue;
    }

    /**
     * Sets the value of the insuredValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setInsuredValue(Money value) {
        this.insuredValue = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link Weight }
     *     
     */
    public Weight getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Weight }
     *     
     */
    public void setWeight(Weight value) {
        this.weight = value;
    }

    /**
     * Gets the value of the dimensions property.
     * 
     * @return
     *     possible object is
     *     {@link Dimensions }
     *     
     */
    public Dimensions getDimensions() {
        return dimensions;
    }

    /**
     * Sets the value of the dimensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dimensions }
     *     
     */
    public void setDimensions(Dimensions value) {
        this.dimensions = value;
    }

    /**
     * Gets the value of the physicalPackaging property.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalPackagingType }
     *     
     */
    public PhysicalPackagingType getPhysicalPackaging() {
        return physicalPackaging;
    }

    /**
     * Sets the value of the physicalPackaging property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalPackagingType }
     *     
     */
    public void setPhysicalPackaging(PhysicalPackagingType value) {
        this.physicalPackaging = value;
    }

    /**
     * Gets the value of the itemDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemDescription() {
        return itemDescription;
    }

    /**
     * Sets the value of the itemDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemDescription(String value) {
        this.itemDescription = value;
    }

    /**
     * Gets the value of the itemDescriptionForClearance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemDescriptionForClearance() {
        return itemDescriptionForClearance;
    }

    /**
     * Sets the value of the itemDescriptionForClearance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemDescriptionForClearance(String value) {
        this.itemDescriptionForClearance = value;
    }

    /**
     * Gets the value of the customerReferences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customerReferences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomerReferences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomerReference }
     * 
     * 
     */
    public List<CustomerReference> getCustomerReferences() {
        if (customerReferences == null) {
            customerReferences = new ArrayList<CustomerReference>();
        }
        return this.customerReferences;
    }

    /**
     * Gets the value of the specialServicesRequested property.
     * 
     * @return
     *     possible object is
     *     {@link PackageSpecialServicesRequested }
     *     
     */
    public PackageSpecialServicesRequested getSpecialServicesRequested() {
        return specialServicesRequested;
    }

    /**
     * Sets the value of the specialServicesRequested property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageSpecialServicesRequested }
     *     
     */
    public void setSpecialServicesRequested(PackageSpecialServicesRequested value) {
        this.specialServicesRequested = value;
    }

    /**
     * Gets the value of the contentRecords property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contentRecords property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContentRecords().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContentRecord }
     * 
     * 
     */
    public List<ContentRecord> getContentRecords() {
        if (contentRecords == null) {
            contentRecords = new ArrayList<ContentRecord>();
        }
        return this.contentRecords;
    }

}
