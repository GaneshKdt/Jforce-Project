
package com.nmims.webservice.fedex.ship;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Description of an individual commodity or class of content in a shipment.
 * 
 * <p>Java class for FreightShipmentLineItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FreightShipmentLineItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FreightClass" type="{http://fedex.com/ws/ship/v17}FreightClassType" minOccurs="0"/>
 *         &lt;element name="ClassProvidedByCustomer" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="HandlingUnits" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="Packaging" type="{http://fedex.com/ws/ship/v17}PhysicalPackagingType" minOccurs="0"/>
 *         &lt;element name="Pieces" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="NmfcCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HazardousMaterials" type="{http://fedex.com/ws/ship/v17}HazardousCommodityOptionType" minOccurs="0"/>
 *         &lt;element name="PurchaseOrderNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Weight" type="{http://fedex.com/ws/ship/v17}Weight" minOccurs="0"/>
 *         &lt;element name="Dimensions" type="{http://fedex.com/ws/ship/v17}Dimensions" minOccurs="0"/>
 *         &lt;element name="Volume" type="{http://fedex.com/ws/ship/v17}Volume" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FreightShipmentLineItem", propOrder = {
    "freightClass",
    "classProvidedByCustomer",
    "handlingUnits",
    "packaging",
    "pieces",
    "nmfcCode",
    "hazardousMaterials",
    "purchaseOrderNumber",
    "description",
    "weight",
    "dimensions",
    "volume"
})
public class FreightShipmentLineItem {

    @XmlElement(name = "FreightClass")
    protected FreightClassType freightClass;
    @XmlElement(name = "ClassProvidedByCustomer")
    protected Boolean classProvidedByCustomer;
    @XmlElement(name = "HandlingUnits")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger handlingUnits;
    @XmlElement(name = "Packaging")
    protected PhysicalPackagingType packaging;
    @XmlElement(name = "Pieces")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger pieces;
    @XmlElement(name = "NmfcCode")
    protected String nmfcCode;
    @XmlElement(name = "HazardousMaterials")
    protected HazardousCommodityOptionType hazardousMaterials;
    @XmlElement(name = "PurchaseOrderNumber")
    protected String purchaseOrderNumber;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Weight")
    protected Weight weight;
    @XmlElement(name = "Dimensions")
    protected Dimensions dimensions;
    @XmlElement(name = "Volume")
    protected Volume volume;

    /**
     * Gets the value of the freightClass property.
     * 
     * @return
     *     possible object is
     *     {@link FreightClassType }
     *     
     */
    public FreightClassType getFreightClass() {
        return freightClass;
    }

    /**
     * Sets the value of the freightClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightClassType }
     *     
     */
    public void setFreightClass(FreightClassType value) {
        this.freightClass = value;
    }

    /**
     * Gets the value of the classProvidedByCustomer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isClassProvidedByCustomer() {
        return classProvidedByCustomer;
    }

    /**
     * Sets the value of the classProvidedByCustomer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setClassProvidedByCustomer(Boolean value) {
        this.classProvidedByCustomer = value;
    }

    /**
     * Gets the value of the handlingUnits property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHandlingUnits() {
        return handlingUnits;
    }

    /**
     * Sets the value of the handlingUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHandlingUnits(BigInteger value) {
        this.handlingUnits = value;
    }

    /**
     * Gets the value of the packaging property.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalPackagingType }
     *     
     */
    public PhysicalPackagingType getPackaging() {
        return packaging;
    }

    /**
     * Sets the value of the packaging property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalPackagingType }
     *     
     */
    public void setPackaging(PhysicalPackagingType value) {
        this.packaging = value;
    }

    /**
     * Gets the value of the pieces property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPieces() {
        return pieces;
    }

    /**
     * Sets the value of the pieces property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPieces(BigInteger value) {
        this.pieces = value;
    }

    /**
     * Gets the value of the nmfcCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNmfcCode() {
        return nmfcCode;
    }

    /**
     * Sets the value of the nmfcCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNmfcCode(String value) {
        this.nmfcCode = value;
    }

    /**
     * Gets the value of the hazardousMaterials property.
     * 
     * @return
     *     possible object is
     *     {@link HazardousCommodityOptionType }
     *     
     */
    public HazardousCommodityOptionType getHazardousMaterials() {
        return hazardousMaterials;
    }

    /**
     * Sets the value of the hazardousMaterials property.
     * 
     * @param value
     *     allowed object is
     *     {@link HazardousCommodityOptionType }
     *     
     */
    public void setHazardousMaterials(HazardousCommodityOptionType value) {
        this.hazardousMaterials = value;
    }

    /**
     * Gets the value of the purchaseOrderNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    /**
     * Sets the value of the purchaseOrderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaseOrderNumber(String value) {
        this.purchaseOrderNumber = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
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
     * Gets the value of the volume property.
     * 
     * @return
     *     possible object is
     *     {@link Volume }
     *     
     */
    public Volume getVolume() {
        return volume;
    }

    /**
     * Sets the value of the volume property.
     * 
     * @param value
     *     allowed object is
     *     {@link Volume }
     *     
     */
    public void setVolume(Volume value) {
        this.volume = value;
    }

}
