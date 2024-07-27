
package com.nmims.webservice.fedex.ship;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines additional data to print in the Configurable portion of the label, this allows you to print the same type information on the label that can also be printed on the doc tab.
 * 
 * <p>Java class for ConfigurableLabelReferenceEntry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConfigurableLabelReferenceEntry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ZoneNumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="Header" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DataField" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LiteralValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfigurableLabelReferenceEntry", propOrder = {
    "zoneNumber",
    "header",
    "dataField",
    "literalValue"
})
public class ConfigurableLabelReferenceEntry {

    @XmlElement(name = "ZoneNumber", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger zoneNumber;
    @XmlElement(name = "Header")
    protected String header;
    @XmlElement(name = "DataField")
    protected String dataField;
    @XmlElement(name = "LiteralValue")
    protected String literalValue;

    /**
     * Gets the value of the zoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getZoneNumber() {
        return zoneNumber;
    }

    /**
     * Sets the value of the zoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setZoneNumber(BigInteger value) {
        this.zoneNumber = value;
    }

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeader(String value) {
        this.header = value;
    }

    /**
     * Gets the value of the dataField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataField() {
        return dataField;
    }

    /**
     * Sets the value of the dataField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataField(String value) {
        this.dataField = value;
    }

    /**
     * Gets the value of the literalValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLiteralValue() {
        return literalValue;
    }

    /**
     * Sets the value of the literalValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLiteralValue(String value) {
        this.literalValue = value;
    }

}
