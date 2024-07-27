
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Rate data specific to FedEx Freight or FedEx National Freight services.
 * 
 * <p>Java class for FreightRateDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FreightRateDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="QuoteNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="QuoteType" type="{http://fedex.com/ws/ship/v17}FreightRateQuoteType" minOccurs="0"/>
 *         &lt;element name="BaseChargeCalculation" type="{http://fedex.com/ws/ship/v17}FreightBaseChargeCalculationType" minOccurs="0"/>
 *         &lt;element name="BaseCharges" type="{http://fedex.com/ws/ship/v17}FreightBaseCharge" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Notations" type="{http://fedex.com/ws/ship/v17}FreightRateNotation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FreightRateDetail", propOrder = {
    "quoteNumber",
    "quoteType",
    "baseChargeCalculation",
    "baseCharges",
    "notations"
})
public class FreightRateDetail {

    @XmlElement(name = "QuoteNumber")
    protected String quoteNumber;
    @XmlElement(name = "QuoteType")
    protected FreightRateQuoteType quoteType;
    @XmlElement(name = "BaseChargeCalculation")
    protected FreightBaseChargeCalculationType baseChargeCalculation;
    @XmlElement(name = "BaseCharges")
    protected List<FreightBaseCharge> baseCharges;
    @XmlElement(name = "Notations")
    protected List<FreightRateNotation> notations;

    /**
     * Gets the value of the quoteNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuoteNumber() {
        return quoteNumber;
    }

    /**
     * Sets the value of the quoteNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuoteNumber(String value) {
        this.quoteNumber = value;
    }

    /**
     * Gets the value of the quoteType property.
     * 
     * @return
     *     possible object is
     *     {@link FreightRateQuoteType }
     *     
     */
    public FreightRateQuoteType getQuoteType() {
        return quoteType;
    }

    /**
     * Sets the value of the quoteType property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightRateQuoteType }
     *     
     */
    public void setQuoteType(FreightRateQuoteType value) {
        this.quoteType = value;
    }

    /**
     * Gets the value of the baseChargeCalculation property.
     * 
     * @return
     *     possible object is
     *     {@link FreightBaseChargeCalculationType }
     *     
     */
    public FreightBaseChargeCalculationType getBaseChargeCalculation() {
        return baseChargeCalculation;
    }

    /**
     * Sets the value of the baseChargeCalculation property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightBaseChargeCalculationType }
     *     
     */
    public void setBaseChargeCalculation(FreightBaseChargeCalculationType value) {
        this.baseChargeCalculation = value;
    }

    /**
     * Gets the value of the baseCharges property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the baseCharges property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBaseCharges().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FreightBaseCharge }
     * 
     * 
     */
    public List<FreightBaseCharge> getBaseCharges() {
        if (baseCharges == null) {
            baseCharges = new ArrayList<FreightBaseCharge>();
        }
        return this.baseCharges;
    }

    /**
     * Gets the value of the notations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FreightRateNotation }
     * 
     * 
     */
    public List<FreightRateNotation> getNotations() {
        if (notations == null) {
            notations = new ArrayList<FreightRateNotation>();
        }
        return this.notations;
    }

}
