
package com.nmims.webservice.fedex.ship;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * The descriptive data for the shipment being tendered to FedEx.
 * 
 * <p>Java class for RequestedShipment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestedShipment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ShipTimestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="DropoffType" type="{http://fedex.com/ws/ship/v17}DropoffType"/>
 *         &lt;element name="ServiceType" type="{http://fedex.com/ws/ship/v17}ServiceType"/>
 *         &lt;element name="PackagingType" type="{http://fedex.com/ws/ship/v17}PackagingType"/>
 *         &lt;element name="ManifestDetail" type="{http://fedex.com/ws/ship/v17}ShipmentManifestDetail" minOccurs="0"/>
 *         &lt;element name="TotalWeight" type="{http://fedex.com/ws/ship/v17}Weight" minOccurs="0"/>
 *         &lt;element name="TotalInsuredValue" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="PreferredCurrency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Shipper" type="{http://fedex.com/ws/ship/v17}Party"/>
 *         &lt;element name="Recipient" type="{http://fedex.com/ws/ship/v17}Party"/>
 *         &lt;element name="RecipientLocationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Origin" type="{http://fedex.com/ws/ship/v17}ContactAndAddress" minOccurs="0"/>
 *         &lt;element name="SoldTo" type="{http://fedex.com/ws/ship/v17}Party" minOccurs="0"/>
 *         &lt;element name="ShippingChargesPayment" type="{http://fedex.com/ws/ship/v17}Payment" minOccurs="0"/>
 *         &lt;element name="SpecialServicesRequested" type="{http://fedex.com/ws/ship/v17}ShipmentSpecialServicesRequested" minOccurs="0"/>
 *         &lt;element name="ExpressFreightDetail" type="{http://fedex.com/ws/ship/v17}ExpressFreightDetail" minOccurs="0"/>
 *         &lt;element name="FreightShipmentDetail" type="{http://fedex.com/ws/ship/v17}FreightShipmentDetail" minOccurs="0"/>
 *         &lt;element name="DeliveryInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VariableHandlingChargeDetail" type="{http://fedex.com/ws/ship/v17}VariableHandlingChargeDetail" minOccurs="0"/>
 *         &lt;element name="CustomsClearanceDetail" type="{http://fedex.com/ws/ship/v17}CustomsClearanceDetail" minOccurs="0"/>
 *         &lt;element name="PickupDetail" type="{http://fedex.com/ws/ship/v17}PickupDetail" minOccurs="0"/>
 *         &lt;element name="SmartPostDetail" type="{http://fedex.com/ws/ship/v17}SmartPostShipmentDetail" minOccurs="0"/>
 *         &lt;element name="BlockInsightVisibility" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="LabelSpecification" type="{http://fedex.com/ws/ship/v17}LabelSpecification"/>
 *         &lt;element name="ShippingDocumentSpecification" type="{http://fedex.com/ws/ship/v17}ShippingDocumentSpecification" minOccurs="0"/>
 *         &lt;element name="RateRequestTypes" type="{http://fedex.com/ws/ship/v17}RateRequestType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="EdtRequestType" type="{http://fedex.com/ws/ship/v17}EdtRequestType" minOccurs="0"/>
 *         &lt;element name="MasterTrackingId" type="{http://fedex.com/ws/ship/v17}TrackingId" minOccurs="0"/>
 *         &lt;element name="PackageCount" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="ConfigurationData" type="{http://fedex.com/ws/ship/v17}ShipmentConfigurationData" minOccurs="0"/>
 *         &lt;element name="RequestedPackageLineItems" type="{http://fedex.com/ws/ship/v17}RequestedPackageLineItem" maxOccurs="999" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestedShipment", propOrder = {
    "shipTimestamp",
    "dropoffType",
    "serviceType",
    "packagingType",
    "manifestDetail",
    "totalWeight",
    "totalInsuredValue",
    "preferredCurrency",
    "shipper",
    "recipient",
    "recipientLocationNumber",
    "origin",
    "soldTo",
    "shippingChargesPayment",
    "specialServicesRequested",
    "expressFreightDetail",
    "freightShipmentDetail",
    "deliveryInstructions",
    "variableHandlingChargeDetail",
    "customsClearanceDetail",
    "pickupDetail",
    "smartPostDetail",
    "blockInsightVisibility",
    "labelSpecification",
    "shippingDocumentSpecification",
    "rateRequestTypes",
    "edtRequestType",
    "masterTrackingId",
    "packageCount",
    "configurationData",
    "requestedPackageLineItems"
})
public class RequestedShipment {

    @XmlElement(name = "ShipTimestamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar shipTimestamp;
    @XmlElement(name = "DropoffType", required = true)
    protected DropoffType dropoffType;
    @XmlElement(name = "ServiceType", required = true)
    protected ServiceType serviceType;
    @XmlElement(name = "PackagingType", required = true)
    protected PackagingType packagingType;
    @XmlElement(name = "ManifestDetail")
    protected ShipmentManifestDetail manifestDetail;
    @XmlElement(name = "TotalWeight")
    protected Weight totalWeight;
    @XmlElement(name = "TotalInsuredValue")
    protected Money totalInsuredValue;
    @XmlElement(name = "PreferredCurrency")
    protected String preferredCurrency;
    @XmlElement(name = "Shipper", required = true)
    protected Party shipper;
    @XmlElement(name = "Recipient", required = true)
    protected Party recipient;
    @XmlElement(name = "RecipientLocationNumber")
    protected String recipientLocationNumber;
    @XmlElement(name = "Origin")
    protected ContactAndAddress origin;
    @XmlElement(name = "SoldTo")
    protected Party soldTo;
    @XmlElement(name = "ShippingChargesPayment")
    protected Payment shippingChargesPayment;
    @XmlElement(name = "SpecialServicesRequested")
    protected ShipmentSpecialServicesRequested specialServicesRequested;
    @XmlElement(name = "ExpressFreightDetail")
    protected ExpressFreightDetail expressFreightDetail;
    @XmlElement(name = "FreightShipmentDetail")
    protected FreightShipmentDetail freightShipmentDetail;
    @XmlElement(name = "DeliveryInstructions")
    protected String deliveryInstructions;
    @XmlElement(name = "VariableHandlingChargeDetail")
    protected VariableHandlingChargeDetail variableHandlingChargeDetail;
    @XmlElement(name = "CustomsClearanceDetail")
    protected CustomsClearanceDetail customsClearanceDetail;
    @XmlElement(name = "PickupDetail")
    protected PickupDetail pickupDetail;
    @XmlElement(name = "SmartPostDetail")
    protected SmartPostShipmentDetail smartPostDetail;
    @XmlElement(name = "BlockInsightVisibility")
    protected Boolean blockInsightVisibility;
    @XmlElement(name = "LabelSpecification", required = true)
    protected LabelSpecification labelSpecification;
    @XmlElement(name = "ShippingDocumentSpecification")
    protected ShippingDocumentSpecification shippingDocumentSpecification;
    @XmlElement(name = "RateRequestTypes")
    protected List<RateRequestType> rateRequestTypes;
    @XmlElement(name = "EdtRequestType")
    protected EdtRequestType edtRequestType;
    @XmlElement(name = "MasterTrackingId")
    protected TrackingId masterTrackingId;
    @XmlElement(name = "PackageCount", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger packageCount;
    @XmlElement(name = "ConfigurationData")
    protected ShipmentConfigurationData configurationData;
    @XmlElement(name = "RequestedPackageLineItems")
    protected List<RequestedPackageLineItem> requestedPackageLineItems;

    /**
     * Gets the value of the shipTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getShipTimestamp() {
        return shipTimestamp;
    }

    /**
     * Sets the value of the shipTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setShipTimestamp(XMLGregorianCalendar value) {
        this.shipTimestamp = value;
    }

    /**
     * Gets the value of the dropoffType property.
     * 
     * @return
     *     possible object is
     *     {@link DropoffType }
     *     
     */
    public DropoffType getDropoffType() {
        return dropoffType;
    }

    /**
     * Sets the value of the dropoffType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DropoffType }
     *     
     */
    public void setDropoffType(DropoffType value) {
        this.dropoffType = value;
    }

    /**
     * Gets the value of the serviceType property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceType }
     *     
     */
    public ServiceType getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceType }
     *     
     */
    public void setServiceType(ServiceType value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the packagingType property.
     * 
     * @return
     *     possible object is
     *     {@link PackagingType }
     *     
     */
    public PackagingType getPackagingType() {
        return packagingType;
    }

    /**
     * Sets the value of the packagingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackagingType }
     *     
     */
    public void setPackagingType(PackagingType value) {
        this.packagingType = value;
    }

    /**
     * Gets the value of the manifestDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentManifestDetail }
     *     
     */
    public ShipmentManifestDetail getManifestDetail() {
        return manifestDetail;
    }

    /**
     * Sets the value of the manifestDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentManifestDetail }
     *     
     */
    public void setManifestDetail(ShipmentManifestDetail value) {
        this.manifestDetail = value;
    }

    /**
     * Gets the value of the totalWeight property.
     * 
     * @return
     *     possible object is
     *     {@link Weight }
     *     
     */
    public Weight getTotalWeight() {
        return totalWeight;
    }

    /**
     * Sets the value of the totalWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Weight }
     *     
     */
    public void setTotalWeight(Weight value) {
        this.totalWeight = value;
    }

    /**
     * Gets the value of the totalInsuredValue property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalInsuredValue() {
        return totalInsuredValue;
    }

    /**
     * Sets the value of the totalInsuredValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalInsuredValue(Money value) {
        this.totalInsuredValue = value;
    }

    /**
     * Gets the value of the preferredCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    /**
     * Sets the value of the preferredCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreferredCurrency(String value) {
        this.preferredCurrency = value;
    }

    /**
     * Gets the value of the shipper property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getShipper() {
        return shipper;
    }

    /**
     * Sets the value of the shipper property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setShipper(Party value) {
        this.shipper = value;
    }

    /**
     * Gets the value of the recipient property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getRecipient() {
        return recipient;
    }

    /**
     * Sets the value of the recipient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setRecipient(Party value) {
        this.recipient = value;
    }

    /**
     * Gets the value of the recipientLocationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecipientLocationNumber() {
        return recipientLocationNumber;
    }

    /**
     * Sets the value of the recipientLocationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecipientLocationNumber(String value) {
        this.recipientLocationNumber = value;
    }

    /**
     * Gets the value of the origin property.
     * 
     * @return
     *     possible object is
     *     {@link ContactAndAddress }
     *     
     */
    public ContactAndAddress getOrigin() {
        return origin;
    }

    /**
     * Sets the value of the origin property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactAndAddress }
     *     
     */
    public void setOrigin(ContactAndAddress value) {
        this.origin = value;
    }

    /**
     * Gets the value of the soldTo property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getSoldTo() {
        return soldTo;
    }

    /**
     * Sets the value of the soldTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setSoldTo(Party value) {
        this.soldTo = value;
    }

    /**
     * Gets the value of the shippingChargesPayment property.
     * 
     * @return
     *     possible object is
     *     {@link Payment }
     *     
     */
    public Payment getShippingChargesPayment() {
        return shippingChargesPayment;
    }

    /**
     * Sets the value of the shippingChargesPayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payment }
     *     
     */
    public void setShippingChargesPayment(Payment value) {
        this.shippingChargesPayment = value;
    }

    /**
     * Gets the value of the specialServicesRequested property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentSpecialServicesRequested }
     *     
     */
    public ShipmentSpecialServicesRequested getSpecialServicesRequested() {
        return specialServicesRequested;
    }

    /**
     * Sets the value of the specialServicesRequested property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentSpecialServicesRequested }
     *     
     */
    public void setSpecialServicesRequested(ShipmentSpecialServicesRequested value) {
        this.specialServicesRequested = value;
    }

    /**
     * Gets the value of the expressFreightDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ExpressFreightDetail }
     *     
     */
    public ExpressFreightDetail getExpressFreightDetail() {
        return expressFreightDetail;
    }

    /**
     * Sets the value of the expressFreightDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpressFreightDetail }
     *     
     */
    public void setExpressFreightDetail(ExpressFreightDetail value) {
        this.expressFreightDetail = value;
    }

    /**
     * Gets the value of the freightShipmentDetail property.
     * 
     * @return
     *     possible object is
     *     {@link FreightShipmentDetail }
     *     
     */
    public FreightShipmentDetail getFreightShipmentDetail() {
        return freightShipmentDetail;
    }

    /**
     * Sets the value of the freightShipmentDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightShipmentDetail }
     *     
     */
    public void setFreightShipmentDetail(FreightShipmentDetail value) {
        this.freightShipmentDetail = value;
    }

    /**
     * Gets the value of the deliveryInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    /**
     * Sets the value of the deliveryInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryInstructions(String value) {
        this.deliveryInstructions = value;
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
     * Gets the value of the customsClearanceDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CustomsClearanceDetail }
     *     
     */
    public CustomsClearanceDetail getCustomsClearanceDetail() {
        return customsClearanceDetail;
    }

    /**
     * Sets the value of the customsClearanceDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomsClearanceDetail }
     *     
     */
    public void setCustomsClearanceDetail(CustomsClearanceDetail value) {
        this.customsClearanceDetail = value;
    }

    /**
     * Gets the value of the pickupDetail property.
     * 
     * @return
     *     possible object is
     *     {@link PickupDetail }
     *     
     */
    public PickupDetail getPickupDetail() {
        return pickupDetail;
    }

    /**
     * Sets the value of the pickupDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link PickupDetail }
     *     
     */
    public void setPickupDetail(PickupDetail value) {
        this.pickupDetail = value;
    }

    /**
     * Gets the value of the smartPostDetail property.
     * 
     * @return
     *     possible object is
     *     {@link SmartPostShipmentDetail }
     *     
     */
    public SmartPostShipmentDetail getSmartPostDetail() {
        return smartPostDetail;
    }

    /**
     * Sets the value of the smartPostDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link SmartPostShipmentDetail }
     *     
     */
    public void setSmartPostDetail(SmartPostShipmentDetail value) {
        this.smartPostDetail = value;
    }

    /**
     * Gets the value of the blockInsightVisibility property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBlockInsightVisibility() {
        return blockInsightVisibility;
    }

    /**
     * Sets the value of the blockInsightVisibility property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBlockInsightVisibility(Boolean value) {
        this.blockInsightVisibility = value;
    }

    /**
     * Gets the value of the labelSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link LabelSpecification }
     *     
     */
    public LabelSpecification getLabelSpecification() {
        return labelSpecification;
    }

    /**
     * Sets the value of the labelSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelSpecification }
     *     
     */
    public void setLabelSpecification(LabelSpecification value) {
        this.labelSpecification = value;
    }

    /**
     * Gets the value of the shippingDocumentSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocumentSpecification }
     *     
     */
    public ShippingDocumentSpecification getShippingDocumentSpecification() {
        return shippingDocumentSpecification;
    }

    /**
     * Sets the value of the shippingDocumentSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocumentSpecification }
     *     
     */
    public void setShippingDocumentSpecification(ShippingDocumentSpecification value) {
        this.shippingDocumentSpecification = value;
    }

    /**
     * Gets the value of the rateRequestTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rateRequestTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRateRequestTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RateRequestType }
     * 
     * 
     */
    public List<RateRequestType> getRateRequestTypes() {
        if (rateRequestTypes == null) {
            rateRequestTypes = new ArrayList<RateRequestType>();
        }
        return this.rateRequestTypes;
    }

    /**
     * Gets the value of the edtRequestType property.
     * 
     * @return
     *     possible object is
     *     {@link EdtRequestType }
     *     
     */
    public EdtRequestType getEdtRequestType() {
        return edtRequestType;
    }

    /**
     * Sets the value of the edtRequestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EdtRequestType }
     *     
     */
    public void setEdtRequestType(EdtRequestType value) {
        this.edtRequestType = value;
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
     * Gets the value of the packageCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPackageCount() {
        return packageCount;
    }

    /**
     * Sets the value of the packageCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPackageCount(BigInteger value) {
        this.packageCount = value;
    }

    /**
     * Gets the value of the configurationData property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentConfigurationData }
     *     
     */
    public ShipmentConfigurationData getConfigurationData() {
        return configurationData;
    }

    /**
     * Sets the value of the configurationData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentConfigurationData }
     *     
     */
    public void setConfigurationData(ShipmentConfigurationData value) {
        this.configurationData = value;
    }

    /**
     * Gets the value of the requestedPackageLineItems property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requestedPackageLineItems property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequestedPackageLineItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestedPackageLineItem }
     * 
     * 
     */
    public List<RequestedPackageLineItem> getRequestedPackageLineItems() {
        if (requestedPackageLineItems == null) {
            requestedPackageLineItems = new ArrayList<RequestedPackageLineItem>();
        }
        return this.requestedPackageLineItems;
    }

}
