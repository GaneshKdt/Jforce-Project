
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies the information associated with a package that has COD special service in a ground shipment.
 * 
 * <p>Java class for CodReturnPackageDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CodReturnPackageDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CollectionAmount" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="AdjustmentType" type="{http://fedex.com/ws/ship/v17}CodAdjustmentType" minOccurs="0"/>
 *         &lt;element name="Electronic" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Barcodes" type="{http://fedex.com/ws/ship/v17}PackageBarcodes" minOccurs="0"/>
 *         &lt;element name="Label" type="{http://fedex.com/ws/ship/v17}ShippingDocument" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodReturnPackageDetail", propOrder = {
    "collectionAmount",
    "adjustmentType",
    "electronic",
    "barcodes",
    "label"
})
public class CodReturnPackageDetail {

    @XmlElement(name = "CollectionAmount")
    protected Money collectionAmount;
    @XmlElement(name = "AdjustmentType")
    protected CodAdjustmentType adjustmentType;
    @XmlElement(name = "Electronic")
    protected Boolean electronic;
    @XmlElement(name = "Barcodes")
    protected PackageBarcodes barcodes;
    @XmlElement(name = "Label")
    protected ShippingDocument label;

    /**
     * Gets the value of the collectionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getCollectionAmount() {
        return collectionAmount;
    }

    /**
     * Sets the value of the collectionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setCollectionAmount(Money value) {
        this.collectionAmount = value;
    }

    /**
     * Gets the value of the adjustmentType property.
     * 
     * @return
     *     possible object is
     *     {@link CodAdjustmentType }
     *     
     */
    public CodAdjustmentType getAdjustmentType() {
        return adjustmentType;
    }

    /**
     * Sets the value of the adjustmentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodAdjustmentType }
     *     
     */
    public void setAdjustmentType(CodAdjustmentType value) {
        this.adjustmentType = value;
    }

    /**
     * Gets the value of the electronic property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isElectronic() {
        return electronic;
    }

    /**
     * Sets the value of the electronic property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setElectronic(Boolean value) {
        this.electronic = value;
    }

    /**
     * Gets the value of the barcodes property.
     * 
     * @return
     *     possible object is
     *     {@link PackageBarcodes }
     *     
     */
    public PackageBarcodes getBarcodes() {
        return barcodes;
    }

    /**
     * Sets the value of the barcodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageBarcodes }
     *     
     */
    public void setBarcodes(PackageBarcodes value) {
        this.barcodes = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocument }
     *     
     */
    public ShippingDocument getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocument }
     *     
     */
    public void setLabel(ShippingDocument value) {
        this.label = value;
    }

}
