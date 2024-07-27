
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
 * All package-level shipping documents (other than labels and barcodes).
 * 
 * <p>Java class for ShippingDocument complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShippingDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://fedex.com/ws/ship/v17}ReturnedShippingDocumentType" minOccurs="0"/>
 *         &lt;element name="Localizations" type="{http://fedex.com/ws/ship/v17}Localization" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Grouping" type="{http://fedex.com/ws/ship/v17}ShippingDocumentGroupingType" minOccurs="0"/>
 *         &lt;element name="ShippingDocumentDisposition" type="{http://fedex.com/ws/ship/v17}ShippingDocumentDispositionType" minOccurs="0"/>
 *         &lt;element name="AccessReference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ImageType" type="{http://fedex.com/ws/ship/v17}ShippingDocumentImageType" minOccurs="0"/>
 *         &lt;element name="Resolution" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="CopiesToPrint" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="Parts" type="{http://fedex.com/ws/ship/v17}ShippingDocumentPart" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShippingDocument", propOrder = {
    "type",
    "localizations",
    "grouping",
    "shippingDocumentDisposition",
    "accessReference",
    "imageType",
    "resolution",
    "copiesToPrint",
    "parts"
})
public class ShippingDocument {

    @XmlElement(name = "Type")
    protected ReturnedShippingDocumentType type;
    @XmlElement(name = "Localizations")
    protected List<Localization> localizations;
    @XmlElement(name = "Grouping")
    protected ShippingDocumentGroupingType grouping;
    @XmlElement(name = "ShippingDocumentDisposition")
    protected ShippingDocumentDispositionType shippingDocumentDisposition;
    @XmlElement(name = "AccessReference")
    protected String accessReference;
    @XmlElement(name = "ImageType")
    protected ShippingDocumentImageType imageType;
    @XmlElement(name = "Resolution")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger resolution;
    @XmlElement(name = "CopiesToPrint")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger copiesToPrint;
    @XmlElement(name = "Parts")
    protected List<ShippingDocumentPart> parts;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnedShippingDocumentType }
     *     
     */
    public ReturnedShippingDocumentType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnedShippingDocumentType }
     *     
     */
    public void setType(ReturnedShippingDocumentType value) {
        this.type = value;
    }

    /**
     * Gets the value of the localizations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localizations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalizations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Localization }
     * 
     * 
     */
    public List<Localization> getLocalizations() {
        if (localizations == null) {
            localizations = new ArrayList<Localization>();
        }
        return this.localizations;
    }

    /**
     * Gets the value of the grouping property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocumentGroupingType }
     *     
     */
    public ShippingDocumentGroupingType getGrouping() {
        return grouping;
    }

    /**
     * Sets the value of the grouping property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocumentGroupingType }
     *     
     */
    public void setGrouping(ShippingDocumentGroupingType value) {
        this.grouping = value;
    }

    /**
     * Gets the value of the shippingDocumentDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocumentDispositionType }
     *     
     */
    public ShippingDocumentDispositionType getShippingDocumentDisposition() {
        return shippingDocumentDisposition;
    }

    /**
     * Sets the value of the shippingDocumentDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocumentDispositionType }
     *     
     */
    public void setShippingDocumentDisposition(ShippingDocumentDispositionType value) {
        this.shippingDocumentDisposition = value;
    }

    /**
     * Gets the value of the accessReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessReference() {
        return accessReference;
    }

    /**
     * Sets the value of the accessReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessReference(String value) {
        this.accessReference = value;
    }

    /**
     * Gets the value of the imageType property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocumentImageType }
     *     
     */
    public ShippingDocumentImageType getImageType() {
        return imageType;
    }

    /**
     * Sets the value of the imageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocumentImageType }
     *     
     */
    public void setImageType(ShippingDocumentImageType value) {
        this.imageType = value;
    }

    /**
     * Gets the value of the resolution property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getResolution() {
        return resolution;
    }

    /**
     * Sets the value of the resolution property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setResolution(BigInteger value) {
        this.resolution = value;
    }

    /**
     * Gets the value of the copiesToPrint property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCopiesToPrint() {
        return copiesToPrint;
    }

    /**
     * Sets the value of the copiesToPrint property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCopiesToPrint(BigInteger value) {
        this.copiesToPrint = value;
    }

    /**
     * Gets the value of the parts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShippingDocumentPart }
     * 
     * 
     */
    public List<ShippingDocumentPart> getParts() {
        if (parts == null) {
            parts = new ArrayList<ShippingDocumentPart>();
        }
        return this.parts;
    }

}
