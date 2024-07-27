
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Descriptive data required for a FedEx COD (Collect-On-Delivery) shipment.
 * 
 * <p>Java class for CodDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CodDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodCollectionAmount" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="AddTransportationChargesDetail" type="{http://fedex.com/ws/ship/v17}CodAddTransportationChargesDetail" minOccurs="0"/>
 *         &lt;element name="CollectionType" type="{http://fedex.com/ws/ship/v17}CodCollectionType"/>
 *         &lt;element name="CodRecipient" type="{http://fedex.com/ws/ship/v17}Party" minOccurs="0"/>
 *         &lt;element name="FinancialInstitutionContactAndAddress" type="{http://fedex.com/ws/ship/v17}ContactAndAddress" minOccurs="0"/>
 *         &lt;element name="RemitToName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReferenceIndicator" type="{http://fedex.com/ws/ship/v17}CodReturnReferenceIndicatorType" minOccurs="0"/>
 *         &lt;element name="ReturnTrackingId" type="{http://fedex.com/ws/ship/v17}TrackingId" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodDetail", propOrder = {
    "codCollectionAmount",
    "addTransportationChargesDetail",
    "collectionType",
    "codRecipient",
    "financialInstitutionContactAndAddress",
    "remitToName",
    "referenceIndicator",
    "returnTrackingId"
})
public class CodDetail {

    @XmlElement(name = "CodCollectionAmount")
    protected Money codCollectionAmount;
    @XmlElement(name = "AddTransportationChargesDetail")
    protected CodAddTransportationChargesDetail addTransportationChargesDetail;
    @XmlElement(name = "CollectionType", required = true)
    protected CodCollectionType collectionType;
    @XmlElement(name = "CodRecipient")
    protected Party codRecipient;
    @XmlElement(name = "FinancialInstitutionContactAndAddress")
    protected ContactAndAddress financialInstitutionContactAndAddress;
    @XmlElement(name = "RemitToName")
    protected String remitToName;
    @XmlElement(name = "ReferenceIndicator")
    protected CodReturnReferenceIndicatorType referenceIndicator;
    @XmlElement(name = "ReturnTrackingId")
    protected TrackingId returnTrackingId;

    /**
     * Gets the value of the codCollectionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getCodCollectionAmount() {
        return codCollectionAmount;
    }

    /**
     * Sets the value of the codCollectionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setCodCollectionAmount(Money value) {
        this.codCollectionAmount = value;
    }

    /**
     * Gets the value of the addTransportationChargesDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CodAddTransportationChargesDetail }
     *     
     */
    public CodAddTransportationChargesDetail getAddTransportationChargesDetail() {
        return addTransportationChargesDetail;
    }

    /**
     * Sets the value of the addTransportationChargesDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodAddTransportationChargesDetail }
     *     
     */
    public void setAddTransportationChargesDetail(CodAddTransportationChargesDetail value) {
        this.addTransportationChargesDetail = value;
    }

    /**
     * Gets the value of the collectionType property.
     * 
     * @return
     *     possible object is
     *     {@link CodCollectionType }
     *     
     */
    public CodCollectionType getCollectionType() {
        return collectionType;
    }

    /**
     * Sets the value of the collectionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodCollectionType }
     *     
     */
    public void setCollectionType(CodCollectionType value) {
        this.collectionType = value;
    }

    /**
     * Gets the value of the codRecipient property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getCodRecipient() {
        return codRecipient;
    }

    /**
     * Sets the value of the codRecipient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setCodRecipient(Party value) {
        this.codRecipient = value;
    }

    /**
     * Gets the value of the financialInstitutionContactAndAddress property.
     * 
     * @return
     *     possible object is
     *     {@link ContactAndAddress }
     *     
     */
    public ContactAndAddress getFinancialInstitutionContactAndAddress() {
        return financialInstitutionContactAndAddress;
    }

    /**
     * Sets the value of the financialInstitutionContactAndAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactAndAddress }
     *     
     */
    public void setFinancialInstitutionContactAndAddress(ContactAndAddress value) {
        this.financialInstitutionContactAndAddress = value;
    }

    /**
     * Gets the value of the remitToName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemitToName() {
        return remitToName;
    }

    /**
     * Sets the value of the remitToName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemitToName(String value) {
        this.remitToName = value;
    }

    /**
     * Gets the value of the referenceIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link CodReturnReferenceIndicatorType }
     *     
     */
    public CodReturnReferenceIndicatorType getReferenceIndicator() {
        return referenceIndicator;
    }

    /**
     * Sets the value of the referenceIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodReturnReferenceIndicatorType }
     *     
     */
    public void setReferenceIndicator(CodReturnReferenceIndicatorType value) {
        this.referenceIndicator = value;
    }

    /**
     * Gets the value of the returnTrackingId property.
     * 
     * @return
     *     possible object is
     *     {@link TrackingId }
     *     
     */
    public TrackingId getReturnTrackingId() {
        return returnTrackingId;
    }

    /**
     * Sets the value of the returnTrackingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrackingId }
     *     
     */
    public void setReturnTrackingId(TrackingId value) {
        this.returnTrackingId = value;
    }

}
