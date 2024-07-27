
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocTabContentBarcoded complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocTabContentBarcoded">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Symbology" type="{http://fedex.com/ws/ship/v17}BarcodeSymbologyType" minOccurs="0"/>
 *         &lt;element name="Specification" type="{http://fedex.com/ws/ship/v17}DocTabZoneSpecification"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocTabContentBarcoded", propOrder = {
    "symbology",
    "specification"
})
public class DocTabContentBarcoded {

    @XmlElement(name = "Symbology")
    protected BarcodeSymbologyType symbology;
    @XmlElement(name = "Specification", required = true)
    protected DocTabZoneSpecification specification;

    /**
     * Gets the value of the symbology property.
     * 
     * @return
     *     possible object is
     *     {@link BarcodeSymbologyType }
     *     
     */
    public BarcodeSymbologyType getSymbology() {
        return symbology;
    }

    /**
     * Sets the value of the symbology property.
     * 
     * @param value
     *     allowed object is
     *     {@link BarcodeSymbologyType }
     *     
     */
    public void setSymbology(BarcodeSymbologyType value) {
        this.symbology = value;
    }

    /**
     * Gets the value of the specification property.
     * 
     * @return
     *     possible object is
     *     {@link DocTabZoneSpecification }
     *     
     */
    public DocTabZoneSpecification getSpecification() {
        return specification;
    }

    /**
     * Sets the value of the specification property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocTabZoneSpecification }
     *     
     */
    public void setSpecification(DocTabZoneSpecification value) {
        this.specification = value;
    }

}
