
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LiabilityCoverageDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LiabilityCoverageDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CoverageType" type="{http://fedex.com/ws/ship/v17}LiabilityCoverageType" minOccurs="0"/>
 *         &lt;element name="CoverageAmount" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LiabilityCoverageDetail", propOrder = {
    "coverageType",
    "coverageAmount"
})
public class LiabilityCoverageDetail {

    @XmlElement(name = "CoverageType")
    protected LiabilityCoverageType coverageType;
    @XmlElement(name = "CoverageAmount")
    protected Money coverageAmount;

    /**
     * Gets the value of the coverageType property.
     * 
     * @return
     *     possible object is
     *     {@link LiabilityCoverageType }
     *     
     */
    public LiabilityCoverageType getCoverageType() {
        return coverageType;
    }

    /**
     * Sets the value of the coverageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LiabilityCoverageType }
     *     
     */
    public void setCoverageType(LiabilityCoverageType value) {
        this.coverageType = value;
    }

    /**
     * Gets the value of the coverageAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getCoverageAmount() {
        return coverageAmount;
    }

    /**
     * Sets the value of the coverageAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setCoverageAmount(Money value) {
        this.coverageAmount = value;
    }

}
