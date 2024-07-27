
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Each instance of this data type represents the set of barcodes (of all types) which are associated with a specific package.
 * 
 * <p>Java class for PackageBarcodes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageBarcodes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BinaryBarcodes" type="{http://fedex.com/ws/ship/v17}BinaryBarcode" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="StringBarcodes" type="{http://fedex.com/ws/ship/v17}StringBarcode" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageBarcodes", propOrder = {
    "binaryBarcodes",
    "stringBarcodes"
})
public class PackageBarcodes {

    @XmlElement(name = "BinaryBarcodes")
    protected List<BinaryBarcode> binaryBarcodes;
    @XmlElement(name = "StringBarcodes")
    protected List<StringBarcode> stringBarcodes;

    /**
     * Gets the value of the binaryBarcodes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the binaryBarcodes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBinaryBarcodes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BinaryBarcode }
     * 
     * 
     */
    public List<BinaryBarcode> getBinaryBarcodes() {
        if (binaryBarcodes == null) {
            binaryBarcodes = new ArrayList<BinaryBarcode>();
        }
        return this.binaryBarcodes;
    }

    /**
     * Gets the value of the stringBarcodes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stringBarcodes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStringBarcodes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringBarcode }
     * 
     * 
     */
    public List<StringBarcode> getStringBarcodes() {
        if (stringBarcodes == null) {
            stringBarcodes = new ArrayList<StringBarcode>();
        }
        return this.stringBarcodes;
    }

}
