
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Customer-provided specifications for handling individual commodities.
 * 
 * <p>Java class for HazardousCommodityOptionDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HazardousCommodityOptionDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LabelTextOption" type="{http://fedex.com/ws/ship/v17}HazardousCommodityLabelTextOptionType" minOccurs="0"/>
 *         &lt;element name="CustomerSuppliedLabelText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HazardousCommodityOptionDetail", propOrder = {
    "labelTextOption",
    "customerSuppliedLabelText"
})
public class HazardousCommodityOptionDetail {

    @XmlElement(name = "LabelTextOption")
    protected HazardousCommodityLabelTextOptionType labelTextOption;
    @XmlElement(name = "CustomerSuppliedLabelText")
    protected String customerSuppliedLabelText;

    /**
     * Gets the value of the labelTextOption property.
     * 
     * @return
     *     possible object is
     *     {@link HazardousCommodityLabelTextOptionType }
     *     
     */
    public HazardousCommodityLabelTextOptionType getLabelTextOption() {
        return labelTextOption;
    }

    /**
     * Sets the value of the labelTextOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link HazardousCommodityLabelTextOptionType }
     *     
     */
    public void setLabelTextOption(HazardousCommodityLabelTextOptionType value) {
        this.labelTextOption = value;
    }

    /**
     * Gets the value of the customerSuppliedLabelText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerSuppliedLabelText() {
        return customerSuppliedLabelText;
    }

    /**
     * Sets the value of the customerSuppliedLabelText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerSuppliedLabelText(String value) {
        this.customerSuppliedLabelText = value;
    }

}
