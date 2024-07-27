
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The descriptive data required for FedEx delivery signature services.
 * 
 * <p>Java class for SignatureOptionDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignatureOptionDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OptionType" type="{http://fedex.com/ws/ship/v17}SignatureOptionType"/>
 *         &lt;element name="SignatureReleaseNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureOptionDetail", propOrder = {
    "optionType",
    "signatureReleaseNumber"
})
public class SignatureOptionDetail {

    @XmlElement(name = "OptionType", required = true)
    protected SignatureOptionType optionType;
    @XmlElement(name = "SignatureReleaseNumber")
    protected String signatureReleaseNumber;

    /**
     * Gets the value of the optionType property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureOptionType }
     *     
     */
    public SignatureOptionType getOptionType() {
        return optionType;
    }

    /**
     * Sets the value of the optionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureOptionType }
     *     
     */
    public void setOptionType(SignatureOptionType value) {
        this.optionType = value;
    }

    /**
     * Gets the value of the signatureReleaseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignatureReleaseNumber() {
        return signatureReleaseNumber;
    }

    /**
     * Sets the value of the signatureReleaseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignatureReleaseNumber(String value) {
        this.signatureReleaseNumber = value;
    }

}
