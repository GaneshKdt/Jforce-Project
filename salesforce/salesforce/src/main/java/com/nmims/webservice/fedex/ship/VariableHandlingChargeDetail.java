
package com.nmims.webservice.fedex.ship;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This definition of variable handling charge detail is intended for use in Jan 2011 corp load.
 * 
 * <p>Java class for VariableHandlingChargeDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VariableHandlingChargeDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FixedValue" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="PercentValue" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="RateElementBasis" type="{http://fedex.com/ws/ship/v17}RateElementBasisType" minOccurs="0"/>
 *         &lt;element name="RateTypeBasis" type="{http://fedex.com/ws/ship/v17}RateTypeBasisType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VariableHandlingChargeDetail", propOrder = {
    "fixedValue",
    "percentValue",
    "rateElementBasis",
    "rateTypeBasis"
})
public class VariableHandlingChargeDetail {

    @XmlElement(name = "FixedValue")
    protected Money fixedValue;
    @XmlElement(name = "PercentValue")
    protected BigDecimal percentValue;
    @XmlElement(name = "RateElementBasis")
    protected RateElementBasisType rateElementBasis;
    @XmlElement(name = "RateTypeBasis")
    protected RateTypeBasisType rateTypeBasis;

    /**
     * Gets the value of the fixedValue property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getFixedValue() {
        return fixedValue;
    }

    /**
     * Sets the value of the fixedValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setFixedValue(Money value) {
        this.fixedValue = value;
    }

    /**
     * Gets the value of the percentValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPercentValue() {
        return percentValue;
    }

    /**
     * Sets the value of the percentValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPercentValue(BigDecimal value) {
        this.percentValue = value;
    }

    /**
     * Gets the value of the rateElementBasis property.
     * 
     * @return
     *     possible object is
     *     {@link RateElementBasisType }
     *     
     */
    public RateElementBasisType getRateElementBasis() {
        return rateElementBasis;
    }

    /**
     * Sets the value of the rateElementBasis property.
     * 
     * @param value
     *     allowed object is
     *     {@link RateElementBasisType }
     *     
     */
    public void setRateElementBasis(RateElementBasisType value) {
        this.rateElementBasis = value;
    }

    /**
     * Gets the value of the rateTypeBasis property.
     * 
     * @return
     *     possible object is
     *     {@link RateTypeBasisType }
     *     
     */
    public RateTypeBasisType getRateTypeBasis() {
        return rateTypeBasis;
    }

    /**
     * Sets the value of the rateTypeBasis property.
     * 
     * @param value
     *     allowed object is
     *     {@link RateTypeBasisType }
     *     
     */
    public void setRateTypeBasis(RateTypeBasisType value) {
        this.rateTypeBasis = value;
    }

}
