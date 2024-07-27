
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Description of shipping label to be returned in the reply
 * 
 * <p>Java class for LabelSpecification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LabelSpecification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Dispositions" type="{http://fedex.com/ws/ship/v17}ShippingDocumentDispositionDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="LabelFormatType" type="{http://fedex.com/ws/ship/v17}LabelFormatType"/>
 *         &lt;element name="ImageType" type="{http://fedex.com/ws/ship/v17}ShippingDocumentImageType" minOccurs="0"/>
 *         &lt;element name="LabelStockType" type="{http://fedex.com/ws/ship/v17}LabelStockType" minOccurs="0"/>
 *         &lt;element name="LabelPrintingOrientation" type="{http://fedex.com/ws/ship/v17}LabelPrintingOrientationType" minOccurs="0"/>
 *         &lt;element name="LabelOrder" type="{http://fedex.com/ws/ship/v17}LabelOrderType" minOccurs="0"/>
 *         &lt;element name="PrintedLabelOrigin" type="{http://fedex.com/ws/ship/v17}ContactAndAddress" minOccurs="0"/>
 *         &lt;element name="CustomerSpecifiedDetail" type="{http://fedex.com/ws/ship/v17}CustomerSpecifiedLabelDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LabelSpecification", propOrder = {
    "dispositions",
    "labelFormatType",
    "imageType",
    "labelStockType",
    "labelPrintingOrientation",
    "labelOrder",
    "printedLabelOrigin",
    "customerSpecifiedDetail"
})
public class LabelSpecification {

    @XmlElement(name = "Dispositions")
    protected List<ShippingDocumentDispositionDetail> dispositions;
    @XmlElement(name = "LabelFormatType", required = true)
    protected LabelFormatType labelFormatType;
    @XmlElement(name = "ImageType")
    protected ShippingDocumentImageType imageType;
    @XmlElement(name = "LabelStockType")
    protected LabelStockType labelStockType;
    @XmlElement(name = "LabelPrintingOrientation")
    protected LabelPrintingOrientationType labelPrintingOrientation;
    @XmlElement(name = "LabelOrder")
    protected LabelOrderType labelOrder;
    @XmlElement(name = "PrintedLabelOrigin")
    protected ContactAndAddress printedLabelOrigin;
    @XmlElement(name = "CustomerSpecifiedDetail")
    protected CustomerSpecifiedLabelDetail customerSpecifiedDetail;

    /**
     * Gets the value of the dispositions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dispositions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDispositions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShippingDocumentDispositionDetail }
     * 
     * 
     */
    public List<ShippingDocumentDispositionDetail> getDispositions() {
        if (dispositions == null) {
            dispositions = new ArrayList<ShippingDocumentDispositionDetail>();
        }
        return this.dispositions;
    }

    /**
     * Gets the value of the labelFormatType property.
     * 
     * @return
     *     possible object is
     *     {@link LabelFormatType }
     *     
     */
    public LabelFormatType getLabelFormatType() {
        return labelFormatType;
    }

    /**
     * Sets the value of the labelFormatType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelFormatType }
     *     
     */
    public void setLabelFormatType(LabelFormatType value) {
        this.labelFormatType = value;
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
     * Gets the value of the labelStockType property.
     * 
     * @return
     *     possible object is
     *     {@link LabelStockType }
     *     
     */
    public LabelStockType getLabelStockType() {
        return labelStockType;
    }

    /**
     * Sets the value of the labelStockType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelStockType }
     *     
     */
    public void setLabelStockType(LabelStockType value) {
        this.labelStockType = value;
    }

    /**
     * Gets the value of the labelPrintingOrientation property.
     * 
     * @return
     *     possible object is
     *     {@link LabelPrintingOrientationType }
     *     
     */
    public LabelPrintingOrientationType getLabelPrintingOrientation() {
        return labelPrintingOrientation;
    }

    /**
     * Sets the value of the labelPrintingOrientation property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelPrintingOrientationType }
     *     
     */
    public void setLabelPrintingOrientation(LabelPrintingOrientationType value) {
        this.labelPrintingOrientation = value;
    }

    /**
     * Gets the value of the labelOrder property.
     * 
     * @return
     *     possible object is
     *     {@link LabelOrderType }
     *     
     */
    public LabelOrderType getLabelOrder() {
        return labelOrder;
    }

    /**
     * Sets the value of the labelOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelOrderType }
     *     
     */
    public void setLabelOrder(LabelOrderType value) {
        this.labelOrder = value;
    }

    /**
     * Gets the value of the printedLabelOrigin property.
     * 
     * @return
     *     possible object is
     *     {@link ContactAndAddress }
     *     
     */
    public ContactAndAddress getPrintedLabelOrigin() {
        return printedLabelOrigin;
    }

    /**
     * Sets the value of the printedLabelOrigin property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactAndAddress }
     *     
     */
    public void setPrintedLabelOrigin(ContactAndAddress value) {
        this.printedLabelOrigin = value;
    }

    /**
     * Gets the value of the customerSpecifiedDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerSpecifiedLabelDetail }
     *     
     */
    public CustomerSpecifiedLabelDetail getCustomerSpecifiedDetail() {
        return customerSpecifiedDetail;
    }

    /**
     * Sets the value of the customerSpecifiedDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerSpecifiedLabelDetail }
     *     
     */
    public void setCustomerSpecifiedDetail(CustomerSpecifiedLabelDetail value) {
        this.customerSpecifiedDetail = value;
    }

}
