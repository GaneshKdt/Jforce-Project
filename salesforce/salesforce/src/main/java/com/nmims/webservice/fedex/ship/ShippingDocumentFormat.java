
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies characteristics of a shipping document to be produced.
 * 
 * <p>Java class for ShippingDocumentFormat complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShippingDocumentFormat">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Dispositions" type="{http://fedex.com/ws/ship/v17}ShippingDocumentDispositionDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TopOfPageOffset" type="{http://fedex.com/ws/ship/v17}LinearMeasure" minOccurs="0"/>
 *         &lt;element name="ImageType" type="{http://fedex.com/ws/ship/v17}ShippingDocumentImageType" minOccurs="0"/>
 *         &lt;element name="StockType" type="{http://fedex.com/ws/ship/v17}ShippingDocumentStockType" minOccurs="0"/>
 *         &lt;element name="ProvideInstructions" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="OptionsRequested" type="{http://fedex.com/ws/ship/v17}DocumentFormatOptionsRequested" minOccurs="0"/>
 *         &lt;element name="Localization" type="{http://fedex.com/ws/ship/v17}Localization" minOccurs="0"/>
 *         &lt;element name="CustomDocumentIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShippingDocumentFormat", propOrder = {
    "dispositions",
    "topOfPageOffset",
    "imageType",
    "stockType",
    "provideInstructions",
    "optionsRequested",
    "localization",
    "customDocumentIdentifier"
})
public class ShippingDocumentFormat {

    @XmlElement(name = "Dispositions")
    protected List<ShippingDocumentDispositionDetail> dispositions;
    @XmlElement(name = "TopOfPageOffset")
    protected LinearMeasure topOfPageOffset;
    @XmlElement(name = "ImageType")
    protected ShippingDocumentImageType imageType;
    @XmlElement(name = "StockType")
    protected ShippingDocumentStockType stockType;
    @XmlElement(name = "ProvideInstructions")
    protected Boolean provideInstructions;
    @XmlElement(name = "OptionsRequested")
    protected DocumentFormatOptionsRequested optionsRequested;
    @XmlElement(name = "Localization")
    protected Localization localization;
    @XmlElement(name = "CustomDocumentIdentifier")
    protected String customDocumentIdentifier;

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
     * Gets the value of the topOfPageOffset property.
     * 
     * @return
     *     possible object is
     *     {@link LinearMeasure }
     *     
     */
    public LinearMeasure getTopOfPageOffset() {
        return topOfPageOffset;
    }

    /**
     * Sets the value of the topOfPageOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinearMeasure }
     *     
     */
    public void setTopOfPageOffset(LinearMeasure value) {
        this.topOfPageOffset = value;
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
     * Gets the value of the stockType property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocumentStockType }
     *     
     */
    public ShippingDocumentStockType getStockType() {
        return stockType;
    }

    /**
     * Sets the value of the stockType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocumentStockType }
     *     
     */
    public void setStockType(ShippingDocumentStockType value) {
        this.stockType = value;
    }

    /**
     * Gets the value of the provideInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isProvideInstructions() {
        return provideInstructions;
    }

    /**
     * Sets the value of the provideInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProvideInstructions(Boolean value) {
        this.provideInstructions = value;
    }

    /**
     * Gets the value of the optionsRequested property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentFormatOptionsRequested }
     *     
     */
    public DocumentFormatOptionsRequested getOptionsRequested() {
        return optionsRequested;
    }

    /**
     * Sets the value of the optionsRequested property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentFormatOptionsRequested }
     *     
     */
    public void setOptionsRequested(DocumentFormatOptionsRequested value) {
        this.optionsRequested = value;
    }

    /**
     * Gets the value of the localization property.
     * 
     * @return
     *     possible object is
     *     {@link Localization }
     *     
     */
    public Localization getLocalization() {
        return localization;
    }

    /**
     * Sets the value of the localization property.
     * 
     * @param value
     *     allowed object is
     *     {@link Localization }
     *     
     */
    public void setLocalization(Localization value) {
        this.localization = value;
    }

    /**
     * Gets the value of the customDocumentIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomDocumentIdentifier() {
        return customDocumentIdentifier;
    }

    /**
     * Sets the value of the customDocumentIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomDocumentIdentifier(String value) {
        this.customDocumentIdentifier = value;
    }

}
