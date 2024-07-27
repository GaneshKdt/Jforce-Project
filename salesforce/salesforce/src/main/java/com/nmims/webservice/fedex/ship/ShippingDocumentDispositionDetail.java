
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Each occurrence of this class specifies a particular way in which a kind of shipping document is to be produced and provided.
 * 
 * <p>Java class for ShippingDocumentDispositionDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShippingDocumentDispositionDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DispositionType" type="{http://fedex.com/ws/ship/v17}ShippingDocumentDispositionType" minOccurs="0"/>
 *         &lt;element name="Grouping" type="{http://fedex.com/ws/ship/v17}ShippingDocumentGroupingType" minOccurs="0"/>
 *         &lt;element name="EMailDetail" type="{http://fedex.com/ws/ship/v17}ShippingDocumentEMailDetail" minOccurs="0"/>
 *         &lt;element name="PrintDetail" type="{http://fedex.com/ws/ship/v17}ShippingDocumentPrintDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShippingDocumentDispositionDetail", propOrder = {
    "dispositionType",
    "grouping",
    "eMailDetail",
    "printDetail"
})
public class ShippingDocumentDispositionDetail {

    @XmlElement(name = "DispositionType")
    protected ShippingDocumentDispositionType dispositionType;
    @XmlElement(name = "Grouping")
    protected ShippingDocumentGroupingType grouping;
    @XmlElement(name = "EMailDetail")
    protected ShippingDocumentEMailDetail eMailDetail;
    @XmlElement(name = "PrintDetail")
    protected ShippingDocumentPrintDetail printDetail;

    /**
     * Gets the value of the dispositionType property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocumentDispositionType }
     *     
     */
    public ShippingDocumentDispositionType getDispositionType() {
        return dispositionType;
    }

    /**
     * Sets the value of the dispositionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocumentDispositionType }
     *     
     */
    public void setDispositionType(ShippingDocumentDispositionType value) {
        this.dispositionType = value;
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
     * Gets the value of the eMailDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocumentEMailDetail }
     *     
     */
    public ShippingDocumentEMailDetail getEMailDetail() {
        return eMailDetail;
    }

    /**
     * Sets the value of the eMailDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocumentEMailDetail }
     *     
     */
    public void setEMailDetail(ShippingDocumentEMailDetail value) {
        this.eMailDetail = value;
    }

    /**
     * Gets the value of the printDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocumentPrintDetail }
     *     
     */
    public ShippingDocumentPrintDetail getPrintDetail() {
        return printDetail;
    }

    /**
     * Sets the value of the printDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocumentPrintDetail }
     *     
     */
    public void setPrintDetail(ShippingDocumentPrintDetail value) {
        this.printDetail = value;
    }

}
