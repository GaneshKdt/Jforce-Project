
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
 * Data applicable to shipments using FEDEX_FREIGHT_ECONOMY and FEDEX_FREIGHT_PRIORITY services.
 * 
 * <p>Java class for FreightShipmentDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FreightShipmentDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FedExFreightAccountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FedExFreightBillingContactAndAddress" type="{http://fedex.com/ws/ship/v17}ContactAndAddress" minOccurs="0"/>
 *         &lt;element name="AlternateBilling" type="{http://fedex.com/ws/ship/v17}Party" minOccurs="0"/>
 *         &lt;element name="PrintedReferences" type="{http://fedex.com/ws/ship/v17}PrintedReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Role" type="{http://fedex.com/ws/ship/v17}FreightShipmentRoleType" minOccurs="0"/>
 *         &lt;element name="CollectTermsType" type="{http://fedex.com/ws/ship/v17}FreightCollectTermsType" minOccurs="0"/>
 *         &lt;element name="DeclaredValuePerUnit" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="DeclaredValueUnits" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LiabilityCoverageDetail" type="{http://fedex.com/ws/ship/v17}LiabilityCoverageDetail" minOccurs="0"/>
 *         &lt;element name="Coupons" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TotalHandlingUnits" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="ClientDiscountPercent" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="PalletWeight" type="{http://fedex.com/ws/ship/v17}Weight" minOccurs="0"/>
 *         &lt;element name="ShipmentDimensions" type="{http://fedex.com/ws/ship/v17}Dimensions" minOccurs="0"/>
 *         &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SpecialServicePayments" type="{http://fedex.com/ws/ship/v17}FreightSpecialServicePayment" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="HazardousMaterialsEmergencyContactNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HazardousMaterialsOfferor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LineItems" type="{http://fedex.com/ws/ship/v17}FreightShipmentLineItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FreightShipmentDetail", propOrder = {
    "fedExFreightAccountNumber",
    "fedExFreightBillingContactAndAddress",
    "alternateBilling",
    "printedReferences",
    "role",
    "collectTermsType",
    "declaredValuePerUnit",
    "declaredValueUnits",
    "liabilityCoverageDetail",
    "coupons",
    "totalHandlingUnits",
    "clientDiscountPercent",
    "palletWeight",
    "shipmentDimensions",
    "comment",
    "specialServicePayments",
    "hazardousMaterialsEmergencyContactNumber",
    "hazardousMaterialsOfferor",
    "lineItems"
})
public class FreightShipmentDetail {

    @XmlElement(name = "FedExFreightAccountNumber")
    protected String fedExFreightAccountNumber;
    @XmlElement(name = "FedExFreightBillingContactAndAddress")
    protected ContactAndAddress fedExFreightBillingContactAndAddress;
    @XmlElement(name = "AlternateBilling")
    protected Party alternateBilling;
    @XmlElement(name = "PrintedReferences")
    protected List<PrintedReference> printedReferences;
    @XmlElement(name = "Role")
    protected FreightShipmentRoleType role;
    @XmlElement(name = "CollectTermsType")
    protected FreightCollectTermsType collectTermsType;
    @XmlElement(name = "DeclaredValuePerUnit")
    protected Money declaredValuePerUnit;
    @XmlElement(name = "DeclaredValueUnits")
    protected String declaredValueUnits;
    @XmlElement(name = "LiabilityCoverageDetail")
    protected LiabilityCoverageDetail liabilityCoverageDetail;
    @XmlElement(name = "Coupons")
    protected List<String> coupons;
    @XmlElement(name = "TotalHandlingUnits")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger totalHandlingUnits;
    @XmlElement(name = "ClientDiscountPercent")
    protected BigDecimal clientDiscountPercent;
    @XmlElement(name = "PalletWeight")
    protected Weight palletWeight;
    @XmlElement(name = "ShipmentDimensions")
    protected Dimensions shipmentDimensions;
    @XmlElement(name = "Comment")
    protected String comment;
    @XmlElement(name = "SpecialServicePayments")
    protected List<FreightSpecialServicePayment> specialServicePayments;
    @XmlElement(name = "HazardousMaterialsEmergencyContactNumber")
    protected String hazardousMaterialsEmergencyContactNumber;
    @XmlElement(name = "HazardousMaterialsOfferor")
    protected String hazardousMaterialsOfferor;
    @XmlElement(name = "LineItems")
    protected List<FreightShipmentLineItem> lineItems;

    /**
     * Gets the value of the fedExFreightAccountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFedExFreightAccountNumber() {
        return fedExFreightAccountNumber;
    }

    /**
     * Sets the value of the fedExFreightAccountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFedExFreightAccountNumber(String value) {
        this.fedExFreightAccountNumber = value;
    }

    /**
     * Gets the value of the fedExFreightBillingContactAndAddress property.
     * 
     * @return
     *     possible object is
     *     {@link ContactAndAddress }
     *     
     */
    public ContactAndAddress getFedExFreightBillingContactAndAddress() {
        return fedExFreightBillingContactAndAddress;
    }

    /**
     * Sets the value of the fedExFreightBillingContactAndAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactAndAddress }
     *     
     */
    public void setFedExFreightBillingContactAndAddress(ContactAndAddress value) {
        this.fedExFreightBillingContactAndAddress = value;
    }

    /**
     * Gets the value of the alternateBilling property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getAlternateBilling() {
        return alternateBilling;
    }

    /**
     * Sets the value of the alternateBilling property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setAlternateBilling(Party value) {
        this.alternateBilling = value;
    }

    /**
     * Gets the value of the printedReferences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the printedReferences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrintedReferences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrintedReference }
     * 
     * 
     */
    public List<PrintedReference> getPrintedReferences() {
        if (printedReferences == null) {
            printedReferences = new ArrayList<PrintedReference>();
        }
        return this.printedReferences;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link FreightShipmentRoleType }
     *     
     */
    public FreightShipmentRoleType getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightShipmentRoleType }
     *     
     */
    public void setRole(FreightShipmentRoleType value) {
        this.role = value;
    }

    /**
     * Gets the value of the collectTermsType property.
     * 
     * @return
     *     possible object is
     *     {@link FreightCollectTermsType }
     *     
     */
    public FreightCollectTermsType getCollectTermsType() {
        return collectTermsType;
    }

    /**
     * Sets the value of the collectTermsType property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightCollectTermsType }
     *     
     */
    public void setCollectTermsType(FreightCollectTermsType value) {
        this.collectTermsType = value;
    }

    /**
     * Gets the value of the declaredValuePerUnit property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getDeclaredValuePerUnit() {
        return declaredValuePerUnit;
    }

    /**
     * Sets the value of the declaredValuePerUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setDeclaredValuePerUnit(Money value) {
        this.declaredValuePerUnit = value;
    }

    /**
     * Gets the value of the declaredValueUnits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeclaredValueUnits() {
        return declaredValueUnits;
    }

    /**
     * Sets the value of the declaredValueUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeclaredValueUnits(String value) {
        this.declaredValueUnits = value;
    }

    /**
     * Gets the value of the liabilityCoverageDetail property.
     * 
     * @return
     *     possible object is
     *     {@link LiabilityCoverageDetail }
     *     
     */
    public LiabilityCoverageDetail getLiabilityCoverageDetail() {
        return liabilityCoverageDetail;
    }

    /**
     * Sets the value of the liabilityCoverageDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link LiabilityCoverageDetail }
     *     
     */
    public void setLiabilityCoverageDetail(LiabilityCoverageDetail value) {
        this.liabilityCoverageDetail = value;
    }

    /**
     * Gets the value of the coupons property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coupons property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoupons().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCoupons() {
        if (coupons == null) {
            coupons = new ArrayList<String>();
        }
        return this.coupons;
    }

    /**
     * Gets the value of the totalHandlingUnits property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalHandlingUnits() {
        return totalHandlingUnits;
    }

    /**
     * Sets the value of the totalHandlingUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalHandlingUnits(BigInteger value) {
        this.totalHandlingUnits = value;
    }

    /**
     * Gets the value of the clientDiscountPercent property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getClientDiscountPercent() {
        return clientDiscountPercent;
    }

    /**
     * Sets the value of the clientDiscountPercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setClientDiscountPercent(BigDecimal value) {
        this.clientDiscountPercent = value;
    }

    /**
     * Gets the value of the palletWeight property.
     * 
     * @return
     *     possible object is
     *     {@link Weight }
     *     
     */
    public Weight getPalletWeight() {
        return palletWeight;
    }

    /**
     * Sets the value of the palletWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Weight }
     *     
     */
    public void setPalletWeight(Weight value) {
        this.palletWeight = value;
    }

    /**
     * Gets the value of the shipmentDimensions property.
     * 
     * @return
     *     possible object is
     *     {@link Dimensions }
     *     
     */
    public Dimensions getShipmentDimensions() {
        return shipmentDimensions;
    }

    /**
     * Sets the value of the shipmentDimensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dimensions }
     *     
     */
    public void setShipmentDimensions(Dimensions value) {
        this.shipmentDimensions = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the specialServicePayments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specialServicePayments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecialServicePayments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FreightSpecialServicePayment }
     * 
     * 
     */
    public List<FreightSpecialServicePayment> getSpecialServicePayments() {
        if (specialServicePayments == null) {
            specialServicePayments = new ArrayList<FreightSpecialServicePayment>();
        }
        return this.specialServicePayments;
    }

    /**
     * Gets the value of the hazardousMaterialsEmergencyContactNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHazardousMaterialsEmergencyContactNumber() {
        return hazardousMaterialsEmergencyContactNumber;
    }

    /**
     * Sets the value of the hazardousMaterialsEmergencyContactNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHazardousMaterialsEmergencyContactNumber(String value) {
        this.hazardousMaterialsEmergencyContactNumber = value;
    }

    /**
     * Gets the value of the hazardousMaterialsOfferor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHazardousMaterialsOfferor() {
        return hazardousMaterialsOfferor;
    }

    /**
     * Sets the value of the hazardousMaterialsOfferor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHazardousMaterialsOfferor(String value) {
        this.hazardousMaterialsOfferor = value;
    }

    /**
     * Gets the value of the lineItems property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lineItems property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLineItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FreightShipmentLineItem }
     * 
     * 
     */
    public List<FreightShipmentLineItem> getLineItems() {
        if (lineItems == null) {
            lineItems = new ArrayList<FreightShipmentLineItem>();
        }
        return this.lineItems;
    }

}
