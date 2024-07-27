
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompletedShipmentDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompletedShipmentDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UsDomestic" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CarrierCode" type="{http://fedex.com/ws/ship/v17}CarrierCodeType" minOccurs="0"/>
 *         &lt;element name="MasterTrackingId" type="{http://fedex.com/ws/ship/v17}TrackingId" minOccurs="0"/>
 *         &lt;element name="ServiceTypeDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PackagingDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OperationalDetail" type="{http://fedex.com/ws/ship/v17}ShipmentOperationalDetail" minOccurs="0"/>
 *         &lt;element name="AccessDetail" type="{http://fedex.com/ws/ship/v17}PendingShipmentAccessDetail" minOccurs="0"/>
 *         &lt;element name="TagDetail" type="{http://fedex.com/ws/ship/v17}CompletedTagDetail" minOccurs="0"/>
 *         &lt;element name="SmartPostDetail" type="{http://fedex.com/ws/ship/v17}CompletedSmartPostDetail" minOccurs="0"/>
 *         &lt;element name="HazardousShipmentDetail" type="{http://fedex.com/ws/ship/v17}CompletedHazardousShipmentDetail" minOccurs="0"/>
 *         &lt;element name="ShipmentRating" type="{http://fedex.com/ws/ship/v17}ShipmentRating" minOccurs="0"/>
 *         &lt;element name="CompletedHoldAtLocationDetail" type="{http://fedex.com/ws/ship/v17}CompletedHoldAtLocationDetail" minOccurs="0"/>
 *         &lt;element name="ExportComplianceStatement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CompletedEtdDetail" type="{http://fedex.com/ws/ship/v17}CompletedEtdDetail" minOccurs="0"/>
 *         &lt;element name="ShipmentDocuments" type="{http://fedex.com/ws/ship/v17}ShippingDocument" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="AssociatedShipments" type="{http://fedex.com/ws/ship/v17}AssociatedShipmentDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CompletedCodDetail" type="{http://fedex.com/ws/ship/v17}CompletedCodDetail" minOccurs="0"/>
 *         &lt;element name="CompletedPackageDetails" type="{http://fedex.com/ws/ship/v17}CompletedPackageDetail" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompletedShipmentDetail", propOrder = {
    "usDomestic",
    "carrierCode",
    "masterTrackingId",
    "serviceTypeDescription",
    "packagingDescription",
    "operationalDetail",
    "accessDetail",
    "tagDetail",
    "smartPostDetail",
    "hazardousShipmentDetail",
    "shipmentRating",
    "completedHoldAtLocationDetail",
    "exportComplianceStatement",
    "completedEtdDetail",
    "shipmentDocuments",
    "associatedShipments",
    "completedCodDetail",
    "completedPackageDetails"
})
public class CompletedShipmentDetail {

    @XmlElement(name = "UsDomestic")
    protected Boolean usDomestic;
    @XmlElement(name = "CarrierCode")
    protected CarrierCodeType carrierCode;
    @XmlElement(name = "MasterTrackingId")
    protected TrackingId masterTrackingId;
    @XmlElement(name = "ServiceTypeDescription")
    protected String serviceTypeDescription;
    @XmlElement(name = "PackagingDescription")
    protected String packagingDescription;
    @XmlElement(name = "OperationalDetail")
    protected ShipmentOperationalDetail operationalDetail;
    @XmlElement(name = "AccessDetail")
    protected PendingShipmentAccessDetail accessDetail;
    @XmlElement(name = "TagDetail")
    protected CompletedTagDetail tagDetail;
    @XmlElement(name = "SmartPostDetail")
    protected CompletedSmartPostDetail smartPostDetail;
    @XmlElement(name = "HazardousShipmentDetail")
    protected CompletedHazardousShipmentDetail hazardousShipmentDetail;
    @XmlElement(name = "ShipmentRating")
    protected ShipmentRating shipmentRating;
    @XmlElement(name = "CompletedHoldAtLocationDetail")
    protected CompletedHoldAtLocationDetail completedHoldAtLocationDetail;
    @XmlElement(name = "ExportComplianceStatement")
    protected String exportComplianceStatement;
    @XmlElement(name = "CompletedEtdDetail")
    protected CompletedEtdDetail completedEtdDetail;
    @XmlElement(name = "ShipmentDocuments")
    protected List<ShippingDocument> shipmentDocuments;
    @XmlElement(name = "AssociatedShipments")
    protected List<AssociatedShipmentDetail> associatedShipments;
    @XmlElement(name = "CompletedCodDetail")
    protected CompletedCodDetail completedCodDetail;
    @XmlElement(name = "CompletedPackageDetails")
    protected List<CompletedPackageDetail> completedPackageDetails;

    /**
     * Gets the value of the usDomestic property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUsDomestic() {
        return usDomestic;
    }

    /**
     * Sets the value of the usDomestic property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUsDomestic(Boolean value) {
        this.usDomestic = value;
    }

    /**
     * Gets the value of the carrierCode property.
     * 
     * @return
     *     possible object is
     *     {@link CarrierCodeType }
     *     
     */
    public CarrierCodeType getCarrierCode() {
        return carrierCode;
    }

    /**
     * Sets the value of the carrierCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CarrierCodeType }
     *     
     */
    public void setCarrierCode(CarrierCodeType value) {
        this.carrierCode = value;
    }

    /**
     * Gets the value of the masterTrackingId property.
     * 
     * @return
     *     possible object is
     *     {@link TrackingId }
     *     
     */
    public TrackingId getMasterTrackingId() {
        return masterTrackingId;
    }

    /**
     * Sets the value of the masterTrackingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrackingId }
     *     
     */
    public void setMasterTrackingId(TrackingId value) {
        this.masterTrackingId = value;
    }

    /**
     * Gets the value of the serviceTypeDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceTypeDescription() {
        return serviceTypeDescription;
    }

    /**
     * Sets the value of the serviceTypeDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceTypeDescription(String value) {
        this.serviceTypeDescription = value;
    }

    /**
     * Gets the value of the packagingDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackagingDescription() {
        return packagingDescription;
    }

    /**
     * Sets the value of the packagingDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackagingDescription(String value) {
        this.packagingDescription = value;
    }

    /**
     * Gets the value of the operationalDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentOperationalDetail }
     *     
     */
    public ShipmentOperationalDetail getOperationalDetail() {
        return operationalDetail;
    }

    /**
     * Sets the value of the operationalDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentOperationalDetail }
     *     
     */
    public void setOperationalDetail(ShipmentOperationalDetail value) {
        this.operationalDetail = value;
    }

    /**
     * Gets the value of the accessDetail property.
     * 
     * @return
     *     possible object is
     *     {@link PendingShipmentAccessDetail }
     *     
     */
    public PendingShipmentAccessDetail getAccessDetail() {
        return accessDetail;
    }

    /**
     * Sets the value of the accessDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link PendingShipmentAccessDetail }
     *     
     */
    public void setAccessDetail(PendingShipmentAccessDetail value) {
        this.accessDetail = value;
    }

    /**
     * Gets the value of the tagDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CompletedTagDetail }
     *     
     */
    public CompletedTagDetail getTagDetail() {
        return tagDetail;
    }

    /**
     * Sets the value of the tagDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletedTagDetail }
     *     
     */
    public void setTagDetail(CompletedTagDetail value) {
        this.tagDetail = value;
    }

    /**
     * Gets the value of the smartPostDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CompletedSmartPostDetail }
     *     
     */
    public CompletedSmartPostDetail getSmartPostDetail() {
        return smartPostDetail;
    }

    /**
     * Sets the value of the smartPostDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletedSmartPostDetail }
     *     
     */
    public void setSmartPostDetail(CompletedSmartPostDetail value) {
        this.smartPostDetail = value;
    }

    /**
     * Gets the value of the hazardousShipmentDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CompletedHazardousShipmentDetail }
     *     
     */
    public CompletedHazardousShipmentDetail getHazardousShipmentDetail() {
        return hazardousShipmentDetail;
    }

    /**
     * Sets the value of the hazardousShipmentDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletedHazardousShipmentDetail }
     *     
     */
    public void setHazardousShipmentDetail(CompletedHazardousShipmentDetail value) {
        this.hazardousShipmentDetail = value;
    }

    /**
     * Gets the value of the shipmentRating property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentRating }
     *     
     */
    public ShipmentRating getShipmentRating() {
        return shipmentRating;
    }

    /**
     * Sets the value of the shipmentRating property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentRating }
     *     
     */
    public void setShipmentRating(ShipmentRating value) {
        this.shipmentRating = value;
    }

    /**
     * Gets the value of the completedHoldAtLocationDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CompletedHoldAtLocationDetail }
     *     
     */
    public CompletedHoldAtLocationDetail getCompletedHoldAtLocationDetail() {
        return completedHoldAtLocationDetail;
    }

    /**
     * Sets the value of the completedHoldAtLocationDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletedHoldAtLocationDetail }
     *     
     */
    public void setCompletedHoldAtLocationDetail(CompletedHoldAtLocationDetail value) {
        this.completedHoldAtLocationDetail = value;
    }

    /**
     * Gets the value of the exportComplianceStatement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportComplianceStatement() {
        return exportComplianceStatement;
    }

    /**
     * Sets the value of the exportComplianceStatement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportComplianceStatement(String value) {
        this.exportComplianceStatement = value;
    }

    /**
     * Gets the value of the completedEtdDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CompletedEtdDetail }
     *     
     */
    public CompletedEtdDetail getCompletedEtdDetail() {
        return completedEtdDetail;
    }

    /**
     * Sets the value of the completedEtdDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletedEtdDetail }
     *     
     */
    public void setCompletedEtdDetail(CompletedEtdDetail value) {
        this.completedEtdDetail = value;
    }

    /**
     * Gets the value of the shipmentDocuments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shipmentDocuments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShipmentDocuments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShippingDocument }
     * 
     * 
     */
    public List<ShippingDocument> getShipmentDocuments() {
        if (shipmentDocuments == null) {
            shipmentDocuments = new ArrayList<ShippingDocument>();
        }
        return this.shipmentDocuments;
    }

    /**
     * Gets the value of the associatedShipments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the associatedShipments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssociatedShipments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AssociatedShipmentDetail }
     * 
     * 
     */
    public List<AssociatedShipmentDetail> getAssociatedShipments() {
        if (associatedShipments == null) {
            associatedShipments = new ArrayList<AssociatedShipmentDetail>();
        }
        return this.associatedShipments;
    }

    /**
     * Gets the value of the completedCodDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CompletedCodDetail }
     *     
     */
    public CompletedCodDetail getCompletedCodDetail() {
        return completedCodDetail;
    }

    /**
     * Sets the value of the completedCodDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletedCodDetail }
     *     
     */
    public void setCompletedCodDetail(CompletedCodDetail value) {
        this.completedCodDetail = value;
    }

    /**
     * Gets the value of the completedPackageDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the completedPackageDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompletedPackageDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompletedPackageDetail }
     * 
     * 
     */
    public List<CompletedPackageDetail> getCompletedPackageDetails() {
        if (completedPackageDetails == null) {
            completedPackageDetails = new ArrayList<CompletedPackageDetail>();
        }
        return this.completedPackageDetails;
    }

}
