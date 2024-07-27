
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies the results of processing for the COD special service.
 * 
 * <p>Java class for CompletedCodDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompletedCodDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CollectionAmount" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="AdjustmentType" type="{http://fedex.com/ws/ship/v17}CodAdjustmentType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompletedCodDetail", propOrder = {
    "collectionAmount",
    "adjustmentType"
})
public class CompletedCodDetail {

    @XmlElement(name = "CollectionAmount")
    protected Money collectionAmount;
    @XmlElement(name = "AdjustmentType")
    protected CodAdjustmentType adjustmentType;

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

}
