
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocTabContentZone001 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocTabContentZone001">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DocTabZoneSpecifications" type="{http://fedex.com/ws/ship/v17}DocTabZoneSpecification" maxOccurs="12"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocTabContentZone001", propOrder = {
    "docTabZoneSpecifications"
})
public class DocTabContentZone001 {

    @XmlElement(name = "DocTabZoneSpecifications", required = true)
    protected List<DocTabZoneSpecification> docTabZoneSpecifications;

    /**
     * Gets the value of the docTabZoneSpecifications property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the docTabZoneSpecifications property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocTabZoneSpecifications().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocTabZoneSpecification }
     * 
     * 
     */
    public List<DocTabZoneSpecification> getDocTabZoneSpecifications() {
        if (docTabZoneSpecifications == null) {
            docTabZoneSpecifications = new ArrayList<DocTabZoneSpecification>();
        }
        return this.docTabZoneSpecifications;
    }

}
