
package com.nmims.webservice.fedex.ship;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies the concept of a container used to package dangerous goods commodities.
 * 
 * <p>Java class for ValidatedHazardousContainer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValidatedHazardousContainer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="QValue" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="HazardousCommodities" type="{http://fedex.com/ws/ship/v17}ValidatedHazardousCommodityContent" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidatedHazardousContainer", propOrder = {
    "qValue",
    "hazardousCommodities"
})
public class ValidatedHazardousContainer {

    @XmlElement(name = "QValue")
    protected BigDecimal qValue;
    @XmlElement(name = "HazardousCommodities")
    protected List<ValidatedHazardousCommodityContent> hazardousCommodities;

    /**
     * Gets the value of the qValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getQValue() {
        return qValue;
    }

    /**
     * Sets the value of the qValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setQValue(BigDecimal value) {
        this.qValue = value;
    }

    /**
     * Gets the value of the hazardousCommodities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hazardousCommodities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHazardousCommodities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValidatedHazardousCommodityContent }
     * 
     * 
     */
    public List<ValidatedHazardousCommodityContent> getHazardousCommodities() {
        if (hazardousCommodities == null) {
            hazardousCommodities = new ArrayList<ValidatedHazardousCommodityContent>();
        }
        return this.hazardousCommodities;
    }

}
