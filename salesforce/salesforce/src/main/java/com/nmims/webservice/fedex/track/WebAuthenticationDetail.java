
package com.nmims.webservice.fedex.track;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Used in authentication of the sender's identity.
 * 
 * <p>Java class for WebAuthenticationDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WebAuthenticationDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ParentCredential" type="{http://fedex.com/ws/track/v10}WebAuthenticationCredential" minOccurs="0"/>
 *         &lt;element name="UserCredential" type="{http://fedex.com/ws/track/v10}WebAuthenticationCredential"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebAuthenticationDetail", propOrder = {
    "parentCredential",
    "userCredential"
})
public class WebAuthenticationDetail {

    @XmlElement(name = "ParentCredential")
    protected WebAuthenticationCredential parentCredential;
    @XmlElement(name = "UserCredential", required = true)
    protected WebAuthenticationCredential userCredential;

    /**
     * Gets the value of the parentCredential property.
     * 
     * @return
     *     possible object is
     *     {@link WebAuthenticationCredential }
     *     
     */
    public WebAuthenticationCredential getParentCredential() {
        return parentCredential;
    }

    /**
     * Sets the value of the parentCredential property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebAuthenticationCredential }
     *     
     */
    public void setParentCredential(WebAuthenticationCredential value) {
        this.parentCredential = value;
    }

    /**
     * Gets the value of the userCredential property.
     * 
     * @return
     *     possible object is
     *     {@link WebAuthenticationCredential }
     *     
     */
    public WebAuthenticationCredential getUserCredential() {
        return userCredential;
    }

    /**
     * Sets the value of the userCredential property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebAuthenticationCredential }
     *     
     */
    public void setUserCredential(WebAuthenticationCredential value) {
        this.userCredential = value;
    }

}
