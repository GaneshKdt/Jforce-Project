
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomLabelDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomLabelDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CoordinateUnits" type="{http://fedex.com/ws/ship/v17}CustomLabelCoordinateUnits" minOccurs="0"/>
 *         &lt;element name="TextEntries" type="{http://fedex.com/ws/ship/v17}CustomLabelTextEntry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="GraphicEntries" type="{http://fedex.com/ws/ship/v17}CustomLabelGraphicEntry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="BoxEntries" type="{http://fedex.com/ws/ship/v17}CustomLabelBoxEntry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TextBoxEntries" type="{http://fedex.com/ws/ship/v17}CustomLabelTextBoxEntry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="BarcodeEntries" type="{http://fedex.com/ws/ship/v17}CustomLabelBarcodeEntry" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomLabelDetail", propOrder = {
    "coordinateUnits",
    "textEntries",
    "graphicEntries",
    "boxEntries",
    "textBoxEntries",
    "barcodeEntries"
})
public class CustomLabelDetail {

    @XmlElement(name = "CoordinateUnits")
    protected CustomLabelCoordinateUnits coordinateUnits;
    @XmlElement(name = "TextEntries")
    protected List<CustomLabelTextEntry> textEntries;
    @XmlElement(name = "GraphicEntries")
    protected List<CustomLabelGraphicEntry> graphicEntries;
    @XmlElement(name = "BoxEntries")
    protected List<CustomLabelBoxEntry> boxEntries;
    @XmlElement(name = "TextBoxEntries")
    protected List<CustomLabelTextBoxEntry> textBoxEntries;
    @XmlElement(name = "BarcodeEntries")
    protected List<CustomLabelBarcodeEntry> barcodeEntries;

    /**
     * Gets the value of the coordinateUnits property.
     * 
     * @return
     *     possible object is
     *     {@link CustomLabelCoordinateUnits }
     *     
     */
    public CustomLabelCoordinateUnits getCoordinateUnits() {
        return coordinateUnits;
    }

    /**
     * Sets the value of the coordinateUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomLabelCoordinateUnits }
     *     
     */
    public void setCoordinateUnits(CustomLabelCoordinateUnits value) {
        this.coordinateUnits = value;
    }

    /**
     * Gets the value of the textEntries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the textEntries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTextEntries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomLabelTextEntry }
     * 
     * 
     */
    public List<CustomLabelTextEntry> getTextEntries() {
        if (textEntries == null) {
            textEntries = new ArrayList<CustomLabelTextEntry>();
        }
        return this.textEntries;
    }

    /**
     * Gets the value of the graphicEntries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the graphicEntries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGraphicEntries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomLabelGraphicEntry }
     * 
     * 
     */
    public List<CustomLabelGraphicEntry> getGraphicEntries() {
        if (graphicEntries == null) {
            graphicEntries = new ArrayList<CustomLabelGraphicEntry>();
        }
        return this.graphicEntries;
    }

    /**
     * Gets the value of the boxEntries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boxEntries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoxEntries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomLabelBoxEntry }
     * 
     * 
     */
    public List<CustomLabelBoxEntry> getBoxEntries() {
        if (boxEntries == null) {
            boxEntries = new ArrayList<CustomLabelBoxEntry>();
        }
        return this.boxEntries;
    }

    /**
     * Gets the value of the textBoxEntries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the textBoxEntries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTextBoxEntries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomLabelTextBoxEntry }
     * 
     * 
     */
    public List<CustomLabelTextBoxEntry> getTextBoxEntries() {
        if (textBoxEntries == null) {
            textBoxEntries = new ArrayList<CustomLabelTextBoxEntry>();
        }
        return this.textBoxEntries;
    }

    /**
     * Gets the value of the barcodeEntries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the barcodeEntries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBarcodeEntries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomLabelBarcodeEntry }
     * 
     * 
     */
    public List<CustomLabelBarcodeEntry> getBarcodeEntries() {
        if (barcodeEntries == null) {
            barcodeEntries = new ArrayList<CustomLabelBarcodeEntry>();
        }
        return this.barcodeEntries;
    }

}
