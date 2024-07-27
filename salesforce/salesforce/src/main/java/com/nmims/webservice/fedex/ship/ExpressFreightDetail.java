
package com.nmims.webservice.fedex.ship;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Details specific to an Express freight shipment.
 * 
 * <p>Java class for ExpressFreightDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExpressFreightDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PackingListEnclosed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShippersLoadAndCount" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="BookingConfirmationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExpressFreightDetail", propOrder = {
    "packingListEnclosed",
    "shippersLoadAndCount",
    "bookingConfirmationNumber"
})
public class ExpressFreightDetail {

    @XmlElement(name = "PackingListEnclosed")
    protected Boolean packingListEnclosed;
    @XmlElement(name = "ShippersLoadAndCount")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger shippersLoadAndCount;
    @XmlElement(name = "BookingConfirmationNumber")
    protected String bookingConfirmationNumber;

    /**
     * Gets the value of the packingListEnclosed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPackingListEnclosed() {
        return packingListEnclosed;
    }

    /**
     * Sets the value of the packingListEnclosed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPackingListEnclosed(Boolean value) {
        this.packingListEnclosed = value;
    }

    /**
     * Gets the value of the shippersLoadAndCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getShippersLoadAndCount() {
        return shippersLoadAndCount;
    }

    /**
     * Sets the value of the shippersLoadAndCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setShippersLoadAndCount(BigInteger value) {
        this.shippersLoadAndCount = value;
    }

    /**
     * Gets the value of the bookingConfirmationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingConfirmationNumber() {
        return bookingConfirmationNumber;
    }

    /**
     * Sets the value of the bookingConfirmationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingConfirmationNumber(String value) {
        this.bookingConfirmationNumber = value;
    }

}
