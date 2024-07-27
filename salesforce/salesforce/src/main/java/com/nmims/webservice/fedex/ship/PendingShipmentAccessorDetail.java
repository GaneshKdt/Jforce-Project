
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies the details to be used by the user of the pending shipment
 * 
 * <p>Java class for PendingShipmentAccessorDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PendingShipmentAccessorDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Role" type="{http://fedex.com/ws/ship/v17}AccessorRoleType" minOccurs="0"/>
 *         &lt;element name="UserId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EmailLabelUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PendingShipmentAccessorDetail", propOrder = {
    "role",
    "userId",
    "password",
    "emailLabelUrl"
})
public class PendingShipmentAccessorDetail {

    @XmlElement(name = "Role")
    protected AccessorRoleType role;
    @XmlElement(name = "UserId")
    protected String userId;
    @XmlElement(name = "Password")
    protected String password;
    @XmlElement(name = "EmailLabelUrl")
    protected String emailLabelUrl;

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link AccessorRoleType }
     *     
     */
    public AccessorRoleType getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessorRoleType }
     *     
     */
    public void setRole(AccessorRoleType value) {
        this.role = value;
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserId(String value) {
        this.userId = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the emailLabelUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailLabelUrl() {
        return emailLabelUrl;
    }

    /**
     * Sets the value of the emailLabelUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailLabelUrl(String value) {
        this.emailLabelUrl = value;
    }

}
