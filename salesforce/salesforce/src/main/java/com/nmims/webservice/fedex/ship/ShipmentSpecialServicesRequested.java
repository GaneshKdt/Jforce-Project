
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * These special services are available at the shipment level for some or all service types. If the shipper is requesting a special service which requires additional data (such as the COD amount), the shipment special service type must be present in the specialServiceTypes collection, and the supporting detail must be provided in the appropriate sub-object below.
 * 
 * <p>Java class for ShipmentSpecialServicesRequested complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShipmentSpecialServicesRequested">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SpecialServiceTypes" type="{http://fedex.com/ws/ship/v17}ShipmentSpecialServiceType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CodDetail" type="{http://fedex.com/ws/ship/v17}CodDetail" minOccurs="0"/>
 *         &lt;element name="DeliveryOnInvoiceAcceptanceDetail" type="{http://fedex.com/ws/ship/v17}DeliveryOnInvoiceAcceptanceDetail" minOccurs="0"/>
 *         &lt;element name="HoldAtLocationDetail" type="{http://fedex.com/ws/ship/v17}HoldAtLocationDetail" minOccurs="0"/>
 *         &lt;element name="EMailNotificationDetail" type="{http://fedex.com/ws/ship/v17}EMailNotificationDetail" minOccurs="0"/>
 *         &lt;element name="ReturnShipmentDetail" type="{http://fedex.com/ws/ship/v17}ReturnShipmentDetail" minOccurs="0"/>
 *         &lt;element name="PendingShipmentDetail" type="{http://fedex.com/ws/ship/v17}PendingShipmentDetail" minOccurs="0"/>
 *         &lt;element name="InternationalControlledExportDetail" type="{http://fedex.com/ws/ship/v17}InternationalControlledExportDetail" minOccurs="0"/>
 *         &lt;element name="InternationalTrafficInArmsRegulationsDetail" type="{http://fedex.com/ws/ship/v17}InternationalTrafficInArmsRegulationsDetail" minOccurs="0"/>
 *         &lt;element name="ShipmentDryIceDetail" type="{http://fedex.com/ws/ship/v17}ShipmentDryIceDetail" minOccurs="0"/>
 *         &lt;element name="HomeDeliveryPremiumDetail" type="{http://fedex.com/ws/ship/v17}HomeDeliveryPremiumDetail" minOccurs="0"/>
 *         &lt;element name="FreightGuaranteeDetail" type="{http://fedex.com/ws/ship/v17}FreightGuaranteeDetail" minOccurs="0"/>
 *         &lt;element name="EtdDetail" type="{http://fedex.com/ws/ship/v17}EtdDetail" minOccurs="0"/>
 *         &lt;element name="CustomDeliveryWindowDetail" type="{http://fedex.com/ws/ship/v17}CustomDeliveryWindowDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipmentSpecialServicesRequested", propOrder = {
    "specialServiceTypes",
    "codDetail",
    "deliveryOnInvoiceAcceptanceDetail",
    "holdAtLocationDetail",
    "eMailNotificationDetail",
    "returnShipmentDetail",
    "pendingShipmentDetail",
    "internationalControlledExportDetail",
    "internationalTrafficInArmsRegulationsDetail",
    "shipmentDryIceDetail",
    "homeDeliveryPremiumDetail",
    "freightGuaranteeDetail",
    "etdDetail",
    "customDeliveryWindowDetail"
})
public class ShipmentSpecialServicesRequested {

    @XmlElement(name = "SpecialServiceTypes")
    protected List<ShipmentSpecialServiceType> specialServiceTypes;
    @XmlElement(name = "CodDetail")
    protected CodDetail codDetail;
    @XmlElement(name = "DeliveryOnInvoiceAcceptanceDetail")
    protected DeliveryOnInvoiceAcceptanceDetail deliveryOnInvoiceAcceptanceDetail;
    @XmlElement(name = "HoldAtLocationDetail")
    protected HoldAtLocationDetail holdAtLocationDetail;
    @XmlElement(name = "EMailNotificationDetail")
    protected EMailNotificationDetail eMailNotificationDetail;
    @XmlElement(name = "ReturnShipmentDetail")
    protected ReturnShipmentDetail returnShipmentDetail;
    @XmlElement(name = "PendingShipmentDetail")
    protected PendingShipmentDetail pendingShipmentDetail;
    @XmlElement(name = "InternationalControlledExportDetail")
    protected InternationalControlledExportDetail internationalControlledExportDetail;
    @XmlElement(name = "InternationalTrafficInArmsRegulationsDetail")
    protected InternationalTrafficInArmsRegulationsDetail internationalTrafficInArmsRegulationsDetail;
    @XmlElement(name = "ShipmentDryIceDetail")
    protected ShipmentDryIceDetail shipmentDryIceDetail;
    @XmlElement(name = "HomeDeliveryPremiumDetail")
    protected HomeDeliveryPremiumDetail homeDeliveryPremiumDetail;
    @XmlElement(name = "FreightGuaranteeDetail")
    protected FreightGuaranteeDetail freightGuaranteeDetail;
    @XmlElement(name = "EtdDetail")
    protected EtdDetail etdDetail;
    @XmlElement(name = "CustomDeliveryWindowDetail")
    protected CustomDeliveryWindowDetail customDeliveryWindowDetail;

    /**
     * Gets the value of the specialServiceTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specialServiceTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecialServiceTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShipmentSpecialServiceType }
     * 
     * 
     */
    public List<ShipmentSpecialServiceType> getSpecialServiceTypes() {
        if (specialServiceTypes == null) {
            specialServiceTypes = new ArrayList<ShipmentSpecialServiceType>();
        }
        return this.specialServiceTypes;
    }

    /**
     * Gets the value of the codDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CodDetail }
     *     
     */
    public CodDetail getCodDetail() {
        return codDetail;
    }

    /**
     * Sets the value of the codDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodDetail }
     *     
     */
    public void setCodDetail(CodDetail value) {
        this.codDetail = value;
    }

    /**
     * Gets the value of the deliveryOnInvoiceAcceptanceDetail property.
     * 
     * @return
     *     possible object is
     *     {@link DeliveryOnInvoiceAcceptanceDetail }
     *     
     */
    public DeliveryOnInvoiceAcceptanceDetail getDeliveryOnInvoiceAcceptanceDetail() {
        return deliveryOnInvoiceAcceptanceDetail;
    }

    /**
     * Sets the value of the deliveryOnInvoiceAcceptanceDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryOnInvoiceAcceptanceDetail }
     *     
     */
    public void setDeliveryOnInvoiceAcceptanceDetail(DeliveryOnInvoiceAcceptanceDetail value) {
        this.deliveryOnInvoiceAcceptanceDetail = value;
    }

    /**
     * Gets the value of the holdAtLocationDetail property.
     * 
     * @return
     *     possible object is
     *     {@link HoldAtLocationDetail }
     *     
     */
    public HoldAtLocationDetail getHoldAtLocationDetail() {
        return holdAtLocationDetail;
    }

    /**
     * Sets the value of the holdAtLocationDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link HoldAtLocationDetail }
     *     
     */
    public void setHoldAtLocationDetail(HoldAtLocationDetail value) {
        this.holdAtLocationDetail = value;
    }

    /**
     * Gets the value of the eMailNotificationDetail property.
     * 
     * @return
     *     possible object is
     *     {@link EMailNotificationDetail }
     *     
     */
    public EMailNotificationDetail getEMailNotificationDetail() {
        return eMailNotificationDetail;
    }

    /**
     * Sets the value of the eMailNotificationDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link EMailNotificationDetail }
     *     
     */
    public void setEMailNotificationDetail(EMailNotificationDetail value) {
        this.eMailNotificationDetail = value;
    }

    /**
     * Gets the value of the returnShipmentDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnShipmentDetail }
     *     
     */
    public ReturnShipmentDetail getReturnShipmentDetail() {
        return returnShipmentDetail;
    }

    /**
     * Sets the value of the returnShipmentDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnShipmentDetail }
     *     
     */
    public void setReturnShipmentDetail(ReturnShipmentDetail value) {
        this.returnShipmentDetail = value;
    }

    /**
     * Gets the value of the pendingShipmentDetail property.
     * 
     * @return
     *     possible object is
     *     {@link PendingShipmentDetail }
     *     
     */
    public PendingShipmentDetail getPendingShipmentDetail() {
        return pendingShipmentDetail;
    }

    /**
     * Sets the value of the pendingShipmentDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link PendingShipmentDetail }
     *     
     */
    public void setPendingShipmentDetail(PendingShipmentDetail value) {
        this.pendingShipmentDetail = value;
    }

    /**
     * Gets the value of the internationalControlledExportDetail property.
     * 
     * @return
     *     possible object is
     *     {@link InternationalControlledExportDetail }
     *     
     */
    public InternationalControlledExportDetail getInternationalControlledExportDetail() {
        return internationalControlledExportDetail;
    }

    /**
     * Sets the value of the internationalControlledExportDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link InternationalControlledExportDetail }
     *     
     */
    public void setInternationalControlledExportDetail(InternationalControlledExportDetail value) {
        this.internationalControlledExportDetail = value;
    }

    /**
     * Gets the value of the internationalTrafficInArmsRegulationsDetail property.
     * 
     * @return
     *     possible object is
     *     {@link InternationalTrafficInArmsRegulationsDetail }
     *     
     */
    public InternationalTrafficInArmsRegulationsDetail getInternationalTrafficInArmsRegulationsDetail() {
        return internationalTrafficInArmsRegulationsDetail;
    }

    /**
     * Sets the value of the internationalTrafficInArmsRegulationsDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link InternationalTrafficInArmsRegulationsDetail }
     *     
     */
    public void setInternationalTrafficInArmsRegulationsDetail(InternationalTrafficInArmsRegulationsDetail value) {
        this.internationalTrafficInArmsRegulationsDetail = value;
    }

    /**
     * Gets the value of the shipmentDryIceDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentDryIceDetail }
     *     
     */
    public ShipmentDryIceDetail getShipmentDryIceDetail() {
        return shipmentDryIceDetail;
    }

    /**
     * Sets the value of the shipmentDryIceDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentDryIceDetail }
     *     
     */
    public void setShipmentDryIceDetail(ShipmentDryIceDetail value) {
        this.shipmentDryIceDetail = value;
    }

    /**
     * Gets the value of the homeDeliveryPremiumDetail property.
     * 
     * @return
     *     possible object is
     *     {@link HomeDeliveryPremiumDetail }
     *     
     */
    public HomeDeliveryPremiumDetail getHomeDeliveryPremiumDetail() {
        return homeDeliveryPremiumDetail;
    }

    /**
     * Sets the value of the homeDeliveryPremiumDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link HomeDeliveryPremiumDetail }
     *     
     */
    public void setHomeDeliveryPremiumDetail(HomeDeliveryPremiumDetail value) {
        this.homeDeliveryPremiumDetail = value;
    }

    /**
     * Gets the value of the freightGuaranteeDetail property.
     * 
     * @return
     *     possible object is
     *     {@link FreightGuaranteeDetail }
     *     
     */
    public FreightGuaranteeDetail getFreightGuaranteeDetail() {
        return freightGuaranteeDetail;
    }

    /**
     * Sets the value of the freightGuaranteeDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightGuaranteeDetail }
     *     
     */
    public void setFreightGuaranteeDetail(FreightGuaranteeDetail value) {
        this.freightGuaranteeDetail = value;
    }

    /**
     * Gets the value of the etdDetail property.
     * 
     * @return
     *     possible object is
     *     {@link EtdDetail }
     *     
     */
    public EtdDetail getEtdDetail() {
        return etdDetail;
    }

    /**
     * Sets the value of the etdDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link EtdDetail }
     *     
     */
    public void setEtdDetail(EtdDetail value) {
        this.etdDetail = value;
    }

    /**
     * Gets the value of the customDeliveryWindowDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CustomDeliveryWindowDetail }
     *     
     */
    public CustomDeliveryWindowDetail getCustomDeliveryWindowDetail() {
        return customDeliveryWindowDetail;
    }

    /**
     * Sets the value of the customDeliveryWindowDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomDeliveryWindowDetail }
     *     
     */
    public void setCustomDeliveryWindowDetail(CustomDeliveryWindowDetail value) {
        this.customDeliveryWindowDetail = value;
    }

}
