
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocTabContent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocTabContent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DocTabContentType" type="{http://fedex.com/ws/ship/v17}DocTabContentType"/>
 *         &lt;element name="Zone001" type="{http://fedex.com/ws/ship/v17}DocTabContentZone001" minOccurs="0"/>
 *         &lt;element name="Barcoded" type="{http://fedex.com/ws/ship/v17}DocTabContentBarcoded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocTabContent", propOrder = {
    "docTabContentType",
    "zone001",
    "barcoded"
})
public class DocTabContent {

    @XmlElement(name = "DocTabContentType", required = true)
    protected DocTabContentType docTabContentType;
    @XmlElement(name = "Zone001")
    protected DocTabContentZone001 zone001;
    @XmlElement(name = "Barcoded")
    protected DocTabContentBarcoded barcoded;

    /**
     * Gets the value of the docTabContentType property.
     * 
     * @return
     *     possible object is
     *     {@link DocTabContentType }
     *     
     */
    public DocTabContentType getDocTabContentType() {
        return docTabContentType;
    }

    /**
     * Sets the value of the docTabContentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocTabContentType }
     *     
     */
    public void setDocTabContentType(DocTabContentType value) {
        this.docTabContentType = value;
    }

    /**
     * Gets the value of the zone001 property.
     * 
     * @return
     *     possible object is
     *     {@link DocTabContentZone001 }
     *     
     */
    public DocTabContentZone001 getZone001() {
        return zone001;
    }

    /**
     * Sets the value of the zone001 property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocTabContentZone001 }
     *     
     */
    public void setZone001(DocTabContentZone001 value) {
        this.zone001 = value;
    }

    /**
     * Gets the value of the barcoded property.
     * 
     * @return
     *     possible object is
     *     {@link DocTabContentBarcoded }
     *     
     */
    public DocTabContentBarcoded getBarcoded() {
        return barcoded;
    }

    /**
     * Sets the value of the barcoded property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocTabContentBarcoded }
     *     
     */
    public void setBarcoded(DocTabContentBarcoded value) {
        this.barcoded = value;
    }

}
