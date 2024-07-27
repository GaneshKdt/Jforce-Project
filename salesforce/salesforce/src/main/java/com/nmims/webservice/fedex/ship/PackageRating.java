
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This class groups together for a single package all package-level rate data (across all rate types) as part of the response to a shipping request, which groups shipment-level data together and groups package-level data by package.
 * 
 * <p>Java class for PackageRating complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageRating">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ActualRateType" type="{http://fedex.com/ws/ship/v17}ReturnedRateType" minOccurs="0"/>
 *         &lt;element name="EffectiveNetDiscount" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="PackageRateDetails" type="{http://fedex.com/ws/ship/v17}PackageRateDetail" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageRating", propOrder = {
    "actualRateType",
    "effectiveNetDiscount",
    "packageRateDetails"
})
public class PackageRating {

    @XmlElement(name = "ActualRateType")
    protected ReturnedRateType actualRateType;
    @XmlElement(name = "EffectiveNetDiscount")
    protected Money effectiveNetDiscount;
    @XmlElement(name = "PackageRateDetails")
    protected List<PackageRateDetail> packageRateDetails;

    /**
     * Gets the value of the actualRateType property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnedRateType }
     *     
     */
    public ReturnedRateType getActualRateType() {
        return actualRateType;
    }

    /**
     * Sets the value of the actualRateType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnedRateType }
     *     
     */
    public void setActualRateType(ReturnedRateType value) {
        this.actualRateType = value;
    }

    /**
     * Gets the value of the effectiveNetDiscount property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getEffectiveNetDiscount() {
        return effectiveNetDiscount;
    }

    /**
     * Sets the value of the effectiveNetDiscount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setEffectiveNetDiscount(Money value) {
        this.effectiveNetDiscount = value;
    }

    /**
     * Gets the value of the packageRateDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the packageRateDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPackageRateDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PackageRateDetail }
     * 
     * 
     */
    public List<PackageRateDetail> getPackageRateDetails() {
        if (packageRateDetails == null) {
            packageRateDetails = new ArrayList<PackageRateDetail>();
        }
        return this.packageRateDetails;
    }

}
