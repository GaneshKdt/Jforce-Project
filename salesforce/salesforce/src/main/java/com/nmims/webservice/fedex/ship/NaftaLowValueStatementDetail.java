
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies the information necessary for printing the NAFTA Low Value statement on customs documentation.
 * 
 * <p>Java class for NaftaLowValueStatementDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NaftaLowValueStatementDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Role" type="{http://fedex.com/ws/ship/v17}CustomsRoleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NaftaLowValueStatementDetail", propOrder = {
    "role"
})
public class NaftaLowValueStatementDetail {

    @XmlElement(name = "Role")
    protected CustomsRoleType role;

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link CustomsRoleType }
     *     
     */
    public CustomsRoleType getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomsRoleType }
     *     
     */
    public void setRole(CustomsRoleType value) {
        this.role = value;
    }

}
