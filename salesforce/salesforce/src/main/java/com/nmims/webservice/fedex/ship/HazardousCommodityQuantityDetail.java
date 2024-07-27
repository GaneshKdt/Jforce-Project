
package com.nmims.webservice.fedex.ship;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Identifies amount and units for quantity of hazardous commodities.
 * 
 * <p>Java class for HazardousCommodityQuantityDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HazardousCommodityQuantityDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="Units" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="QuantityType" type="{http://fedex.com/ws/ship/v17}HazardousCommodityQuantityType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HazardousCommodityQuantityDetail", propOrder = {
    "amount",
    "units",
    "quantityType"
})
public class HazardousCommodityQuantityDetail {

    @XmlElement(name = "Amount")
    protected BigDecimal amount;
    @XmlElement(name = "Units")
    protected String units;
    @XmlElement(name = "QuantityType")
    protected HazardousCommodityQuantityType quantityType;

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmount(BigDecimal value) {
        this.amount = value;
    }

    /**
     * Gets the value of the units property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the value of the units property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnits(String value) {
        this.units = value;
    }

    /**
     * Gets the value of the quantityType property.
     * 
     * @return
     *     possible object is
     *     {@link HazardousCommodityQuantityType }
     *     
     */
    public HazardousCommodityQuantityType getQuantityType() {
        return quantityType;
    }

    /**
     * Sets the value of the quantityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link HazardousCommodityQuantityType }
     *     
     */
    public void setQuantityType(HazardousCommodityQuantityType value) {
        this.quantityType = value;
    }

}
