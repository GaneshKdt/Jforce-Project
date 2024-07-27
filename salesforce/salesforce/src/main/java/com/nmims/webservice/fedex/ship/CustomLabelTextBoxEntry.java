
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
 * Constructed string, based on format and zero or more data fields, printed in specified printer font (for thermal labels) or generic font/size (for plain paper labels).
 * 
 * <p>Java class for CustomLabelTextBoxEntry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomLabelTextBoxEntry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TopLeftCorner" type="{http://fedex.com/ws/ship/v17}CustomLabelPosition" minOccurs="0"/>
 *         &lt;element name="BottomRightCorner" type="{http://fedex.com/ws/ship/v17}CustomLabelPosition" minOccurs="0"/>
 *         &lt;element name="Position" type="{http://fedex.com/ws/ship/v17}CustomLabelPosition" minOccurs="0"/>
 *         &lt;element name="Format" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DataFields" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ThermalFontId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FontName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FontSize" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="Rotation" type="{http://fedex.com/ws/ship/v17}RotationType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomLabelTextBoxEntry", propOrder = {
    "topLeftCorner",
    "bottomRightCorner",
    "position",
    "format",
    "dataFields",
    "thermalFontId",
    "fontName",
    "fontSize",
    "rotation"
})
public class CustomLabelTextBoxEntry {

    @XmlElement(name = "TopLeftCorner")
    protected CustomLabelPosition topLeftCorner;
    @XmlElement(name = "BottomRightCorner")
    protected CustomLabelPosition bottomRightCorner;
    @XmlElement(name = "Position")
    protected CustomLabelPosition position;
    @XmlElement(name = "Format")
    protected String format;
    @XmlElement(name = "DataFields")
    protected List<String> dataFields;
    @XmlElement(name = "ThermalFontId")
    protected String thermalFontId;
    @XmlElement(name = "FontName")
    protected String fontName;
    @XmlElement(name = "FontSize")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger fontSize;
    @XmlElement(name = "Rotation")
    protected RotationType rotation;

    /**
     * Gets the value of the topLeftCorner property.
     * 
     * @return
     *     possible object is
     *     {@link CustomLabelPosition }
     *     
     */
    public CustomLabelPosition getTopLeftCorner() {
        return topLeftCorner;
    }

    /**
     * Sets the value of the topLeftCorner property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomLabelPosition }
     *     
     */
    public void setTopLeftCorner(CustomLabelPosition value) {
        this.topLeftCorner = value;
    }

    /**
     * Gets the value of the bottomRightCorner property.
     * 
     * @return
     *     possible object is
     *     {@link CustomLabelPosition }
     *     
     */
    public CustomLabelPosition getBottomRightCorner() {
        return bottomRightCorner;
    }

    /**
     * Sets the value of the bottomRightCorner property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomLabelPosition }
     *     
     */
    public void setBottomRightCorner(CustomLabelPosition value) {
        this.bottomRightCorner = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link CustomLabelPosition }
     *     
     */
    public CustomLabelPosition getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomLabelPosition }
     *     
     */
    public void setPosition(CustomLabelPosition value) {
        this.position = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the dataFields property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataFields property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataFields().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDataFields() {
        if (dataFields == null) {
            dataFields = new ArrayList<String>();
        }
        return this.dataFields;
    }

    /**
     * Gets the value of the thermalFontId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThermalFontId() {
        return thermalFontId;
    }

    /**
     * Sets the value of the thermalFontId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThermalFontId(String value) {
        this.thermalFontId = value;
    }

    /**
     * Gets the value of the fontName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFontName() {
        return fontName;
    }

    /**
     * Sets the value of the fontName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFontName(String value) {
        this.fontName = value;
    }

    /**
     * Gets the value of the fontSize property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFontSize() {
        return fontSize;
    }

    /**
     * Sets the value of the fontSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFontSize(BigInteger value) {
        this.fontSize = value;
    }

    /**
     * Gets the value of the rotation property.
     * 
     * @return
     *     possible object is
     *     {@link RotationType }
     *     
     */
    public RotationType getRotation() {
        return rotation;
    }

    /**
     * Sets the value of the rotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link RotationType }
     *     
     */
    public void setRotation(RotationType value) {
        this.rotation = value;
    }

}
