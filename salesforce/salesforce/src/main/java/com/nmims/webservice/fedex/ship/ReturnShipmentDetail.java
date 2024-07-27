
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Information relating to a return shipment.
 * 
 * <p>Java class for ReturnShipmentDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReturnShipmentDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReturnType" type="{http://fedex.com/ws/ship/v17}ReturnType"/>
 *         &lt;element name="Rma" type="{http://fedex.com/ws/ship/v17}Rma" minOccurs="0"/>
 *         &lt;element name="ReturnEMailDetail" type="{http://fedex.com/ws/ship/v17}ReturnEMailDetail" minOccurs="0"/>
 *         &lt;element name="ReturnAssociation" type="{http://fedex.com/ws/ship/v17}ReturnAssociationDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnShipmentDetail", propOrder = {
    "returnType",
    "rma",
    "returnEMailDetail",
    "returnAssociation"
})
public class ReturnShipmentDetail {

    @XmlElement(name = "ReturnType", required = true)
    protected ReturnType returnType;
    @XmlElement(name = "Rma")
    protected Rma rma;
    @XmlElement(name = "ReturnEMailDetail")
    protected ReturnEMailDetail returnEMailDetail;
    @XmlElement(name = "ReturnAssociation")
    protected ReturnAssociationDetail returnAssociation;

    /**
     * Gets the value of the returnType property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnType }
     *     
     */
    public ReturnType getReturnType() {
        return returnType;
    }

    /**
     * Sets the value of the returnType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnType }
     *     
     */
    public void setReturnType(ReturnType value) {
        this.returnType = value;
    }

    /**
     * Gets the value of the rma property.
     * 
     * @return
     *     possible object is
     *     {@link Rma }
     *     
     */
    public Rma getRma() {
        return rma;
    }

    /**
     * Sets the value of the rma property.
     * 
     * @param value
     *     allowed object is
     *     {@link Rma }
     *     
     */
    public void setRma(Rma value) {
        this.rma = value;
    }

    /**
     * Gets the value of the returnEMailDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnEMailDetail }
     *     
     */
    public ReturnEMailDetail getReturnEMailDetail() {
        return returnEMailDetail;
    }

    /**
     * Sets the value of the returnEMailDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnEMailDetail }
     *     
     */
    public void setReturnEMailDetail(ReturnEMailDetail value) {
        this.returnEMailDetail = value;
    }

    /**
     * Gets the value of the returnAssociation property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnAssociationDetail }
     *     
     */
    public ReturnAssociationDetail getReturnAssociation() {
        return returnAssociation;
    }

    /**
     * Sets the value of the returnAssociation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnAssociationDetail }
     *     
     */
    public void setReturnAssociation(ReturnAssociationDetail value) {
        this.returnAssociation = value;
    }

}
