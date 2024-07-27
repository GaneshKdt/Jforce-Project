
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Return Email Details
 * 
 * <p>Java class for ReturnEMailDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReturnEMailDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MerchantPhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AllowedSpecialServices" type="{http://fedex.com/ws/ship/v17}ReturnEMailAllowedSpecialServiceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnEMailDetail", propOrder = {
    "merchantPhoneNumber",
    "allowedSpecialServices"
})
public class ReturnEMailDetail {

    @XmlElement(name = "MerchantPhoneNumber")
    protected String merchantPhoneNumber;
    @XmlElement(name = "AllowedSpecialServices")
    protected List<ReturnEMailAllowedSpecialServiceType> allowedSpecialServices;

    /**
     * Gets the value of the merchantPhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantPhoneNumber() {
        return merchantPhoneNumber;
    }

    /**
     * Sets the value of the merchantPhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantPhoneNumber(String value) {
        this.merchantPhoneNumber = value;
    }

    /**
     * Gets the value of the allowedSpecialServices property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedSpecialServices property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedSpecialServices().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReturnEMailAllowedSpecialServiceType }
     * 
     * 
     */
    public List<ReturnEMailAllowedSpecialServiceType> getAllowedSpecialServices() {
        if (allowedSpecialServices == null) {
            allowedSpecialServices = new ArrayList<ReturnEMailAllowedSpecialServiceType>();
        }
        return this.allowedSpecialServices;
    }

}
