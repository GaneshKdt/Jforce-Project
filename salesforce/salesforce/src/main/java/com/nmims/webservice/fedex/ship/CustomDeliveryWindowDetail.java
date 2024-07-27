
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CustomDeliveryWindowDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomDeliveryWindowDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://fedex.com/ws/ship/v17}CustomDeliveryWindowType" minOccurs="0"/>
 *         &lt;element name="RequestTime" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/>
 *         &lt;element name="RequestRange" type="{http://fedex.com/ws/ship/v17}DateRange" minOccurs="0"/>
 *         &lt;element name="RequestDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomDeliveryWindowDetail", propOrder = {
    "type",
    "requestTime",
    "requestRange",
    "requestDate"
})
public class CustomDeliveryWindowDetail {

    @XmlElement(name = "Type")
    protected CustomDeliveryWindowType type;
    @XmlElement(name = "RequestTime")
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar requestTime;
    @XmlElement(name = "RequestRange")
    protected DateRange requestRange;
    @XmlElement(name = "RequestDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar requestDate;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link CustomDeliveryWindowType }
     *     
     */
    public CustomDeliveryWindowType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomDeliveryWindowType }
     *     
     */
    public void setType(CustomDeliveryWindowType value) {
        this.type = value;
    }

    /**
     * Gets the value of the requestTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestTime() {
        return requestTime;
    }

    /**
     * Sets the value of the requestTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestTime(XMLGregorianCalendar value) {
        this.requestTime = value;
    }

    /**
     * Gets the value of the requestRange property.
     * 
     * @return
     *     possible object is
     *     {@link DateRange }
     *     
     */
    public DateRange getRequestRange() {
        return requestRange;
    }

    /**
     * Sets the value of the requestRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateRange }
     *     
     */
    public void setRequestRange(DateRange value) {
        this.requestRange = value;
    }

    /**
     * Gets the value of the requestDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestDate() {
        return requestDate;
    }

    /**
     * Sets the value of the requestDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestDate(XMLGregorianCalendar value) {
        this.requestDate = value;
    }

}
