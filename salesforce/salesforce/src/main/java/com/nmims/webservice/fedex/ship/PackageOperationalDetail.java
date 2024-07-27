
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Package-level data required for labeling and/or movement.
 * 
 * <p>Java class for PackageOperationalDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageOperationalDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AstraHandlingText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OperationalInstructions" type="{http://fedex.com/ws/ship/v17}OperationalInstruction" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Barcodes" type="{http://fedex.com/ws/ship/v17}PackageBarcodes" minOccurs="0"/>
 *         &lt;element name="GroundServiceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageOperationalDetail", propOrder = {
    "astraHandlingText",
    "operationalInstructions",
    "barcodes",
    "groundServiceCode"
})
public class PackageOperationalDetail {

    @XmlElement(name = "AstraHandlingText")
    protected String astraHandlingText;
    @XmlElement(name = "OperationalInstructions")
    protected List<OperationalInstruction> operationalInstructions;
    @XmlElement(name = "Barcodes")
    protected PackageBarcodes barcodes;
    @XmlElement(name = "GroundServiceCode")
    protected String groundServiceCode;

    /**
     * Gets the value of the astraHandlingText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAstraHandlingText() {
        return astraHandlingText;
    }

    /**
     * Sets the value of the astraHandlingText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAstraHandlingText(String value) {
        this.astraHandlingText = value;
    }

    /**
     * Gets the value of the operationalInstructions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operationalInstructions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperationalInstructions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OperationalInstruction }
     * 
     * 
     */
    public List<OperationalInstruction> getOperationalInstructions() {
        if (operationalInstructions == null) {
            operationalInstructions = new ArrayList<OperationalInstruction>();
        }
        return this.operationalInstructions;
    }

    /**
     * Gets the value of the barcodes property.
     * 
     * @return
     *     possible object is
     *     {@link PackageBarcodes }
     *     
     */
    public PackageBarcodes getBarcodes() {
        return barcodes;
    }

    /**
     * Sets the value of the barcodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageBarcodes }
     *     
     */
    public void setBarcodes(PackageBarcodes value) {
        this.barcodes = value;
    }

    /**
     * Gets the value of the groundServiceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroundServiceCode() {
        return groundServiceCode;
    }

    /**
     * Sets the value of the groundServiceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroundServiceCode(String value) {
        this.groundServiceCode = value;
    }

}
