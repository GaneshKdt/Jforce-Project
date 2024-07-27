
package com.nmims.webservice.fedex.ship;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies additional labels to be produced. All required labels for shipments will be produced without the need to request additional labels. These are only available as thermal labels.
 * 
 * <p>Java class for AdditionalLabelsDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdditionalLabelsDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://fedex.com/ws/ship/v17}AdditionalLabelsType"/>
 *         &lt;element name="Count" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdditionalLabelsDetail", propOrder = {
    "type",
    "count"
})
public class AdditionalLabelsDetail {

    @XmlElement(name = "Type", required = true)
    protected AdditionalLabelsType type;
    @XmlElement(name = "Count", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger count;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalLabelsType }
     *     
     */
    public AdditionalLabelsType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalLabelsType }
     *     
     */
    public void setType(AdditionalLabelsType value) {
        this.type = value;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCount(BigInteger value) {
        this.count = value;
    }

}
