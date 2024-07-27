
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Data required to complete the Destionation Control Statement for US exports.
 * 
 * <p>Java class for DestinationControlDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DestinationControlDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StatementTypes" type="{http://fedex.com/ws/ship/v17}DestinationControlStatementType" maxOccurs="unbounded"/>
 *         &lt;element name="DestinationCountries" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EndUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DestinationControlDetail", propOrder = {
    "statementTypes",
    "destinationCountries",
    "endUser"
})
public class DestinationControlDetail {

    @XmlElement(name = "StatementTypes", required = true)
    protected List<DestinationControlStatementType> statementTypes;
    @XmlElement(name = "DestinationCountries")
    protected String destinationCountries;
    @XmlElement(name = "EndUser")
    protected String endUser;

    /**
     * Gets the value of the statementTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the statementTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatementTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DestinationControlStatementType }
     * 
     * 
     */
    public List<DestinationControlStatementType> getStatementTypes() {
        if (statementTypes == null) {
            statementTypes = new ArrayList<DestinationControlStatementType>();
        }
        return this.statementTypes;
    }

    /**
     * Gets the value of the destinationCountries property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationCountries() {
        return destinationCountries;
    }

    /**
     * Sets the value of the destinationCountries property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationCountries(String value) {
        this.destinationCountries = value;
    }

    /**
     * Gets the value of the endUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndUser() {
        return endUser;
    }

    /**
     * Sets the value of the endUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndUser(String value) {
        this.endUser = value;
    }

}
