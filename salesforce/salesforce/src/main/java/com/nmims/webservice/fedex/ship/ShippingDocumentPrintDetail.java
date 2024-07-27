
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies printing options for a shipping document.
 * 
 * <p>Java class for ShippingDocumentPrintDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShippingDocumentPrintDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrinterId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShippingDocumentPrintDetail", propOrder = {
    "printerId"
})
public class ShippingDocumentPrintDetail {

    @XmlElement(name = "PrinterId")
    protected String printerId;

    /**
     * Gets the value of the printerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrinterId() {
        return printerId;
    }

    /**
     * Sets the value of the printerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrinterId(String value) {
        this.printerId = value;
    }

}
