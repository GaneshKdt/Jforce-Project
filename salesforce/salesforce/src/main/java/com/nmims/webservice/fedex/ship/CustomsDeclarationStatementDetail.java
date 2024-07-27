
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This provides the information necessary to identify the different statements, declarations, acts, and/or certifications that apply to this shipment.
 * 
 * <p>Java class for CustomsDeclarationStatementDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomsDeclarationStatementDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Types" type="{http://fedex.com/ws/ship/v17}CustomsDeclarationStatementType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="NaftaLowValueStatementDetail" type="{http://fedex.com/ws/ship/v17}NaftaLowValueStatementDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomsDeclarationStatementDetail", propOrder = {
    "types",
    "naftaLowValueStatementDetail"
})
public class CustomsDeclarationStatementDetail {

    @XmlElement(name = "Types")
    protected List<CustomsDeclarationStatementType> types;
    @XmlElement(name = "NaftaLowValueStatementDetail")
    protected NaftaLowValueStatementDetail naftaLowValueStatementDetail;

    /**
     * Gets the value of the types property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the types property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomsDeclarationStatementType }
     * 
     * 
     */
    public List<CustomsDeclarationStatementType> getTypes() {
        if (types == null) {
            types = new ArrayList<CustomsDeclarationStatementType>();
        }
        return this.types;
    }

    /**
     * Gets the value of the naftaLowValueStatementDetail property.
     * 
     * @return
     *     possible object is
     *     {@link NaftaLowValueStatementDetail }
     *     
     */
    public NaftaLowValueStatementDetail getNaftaLowValueStatementDetail() {
        return naftaLowValueStatementDetail;
    }

    /**
     * Sets the value of the naftaLowValueStatementDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link NaftaLowValueStatementDetail }
     *     
     */
    public void setNaftaLowValueStatementDetail(NaftaLowValueStatementDetail value) {
        this.naftaLowValueStatementDetail = value;
    }

}
