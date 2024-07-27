
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Data required to produce a Certificate of Origin document. Remaining content (business data) to be defined once requirements have been completed.
 * 
 * <p>Java class for NaftaCertificateOfOriginDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NaftaCertificateOfOriginDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Format" type="{http://fedex.com/ws/ship/v17}ShippingDocumentFormat" minOccurs="0"/>
 *         &lt;element name="BlanketPeriod" type="{http://fedex.com/ws/ship/v17}DateRange" minOccurs="0"/>
 *         &lt;element name="ImporterSpecification" type="{http://fedex.com/ws/ship/v17}NaftaImporterSpecificationType" minOccurs="0"/>
 *         &lt;element name="SignatureContact" type="{http://fedex.com/ws/ship/v17}Contact" minOccurs="0"/>
 *         &lt;element name="ProducerSpecification" type="{http://fedex.com/ws/ship/v17}NaftaProducerSpecificationType" minOccurs="0"/>
 *         &lt;element name="Producers" type="{http://fedex.com/ws/ship/v17}NaftaProducer" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CustomerImageUsages" type="{http://fedex.com/ws/ship/v17}CustomerImageUsage" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NaftaCertificateOfOriginDetail", propOrder = {
    "format",
    "blanketPeriod",
    "importerSpecification",
    "signatureContact",
    "producerSpecification",
    "producers",
    "customerImageUsages"
})
public class NaftaCertificateOfOriginDetail {

    @XmlElement(name = "Format")
    protected ShippingDocumentFormat format;
    @XmlElement(name = "BlanketPeriod")
    protected DateRange blanketPeriod;
    @XmlElement(name = "ImporterSpecification")
    protected NaftaImporterSpecificationType importerSpecification;
    @XmlElement(name = "SignatureContact")
    protected Contact signatureContact;
    @XmlElement(name = "ProducerSpecification")
    protected NaftaProducerSpecificationType producerSpecification;
    @XmlElement(name = "Producers")
    protected List<NaftaProducer> producers;
    @XmlElement(name = "CustomerImageUsages")
    protected List<CustomerImageUsage> customerImageUsages;

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingDocumentFormat }
     *     
     */
    public ShippingDocumentFormat getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingDocumentFormat }
     *     
     */
    public void setFormat(ShippingDocumentFormat value) {
        this.format = value;
    }

    /**
     * Gets the value of the blanketPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link DateRange }
     *     
     */
    public DateRange getBlanketPeriod() {
        return blanketPeriod;
    }

    /**
     * Sets the value of the blanketPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateRange }
     *     
     */
    public void setBlanketPeriod(DateRange value) {
        this.blanketPeriod = value;
    }

    /**
     * Gets the value of the importerSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link NaftaImporterSpecificationType }
     *     
     */
    public NaftaImporterSpecificationType getImporterSpecification() {
        return importerSpecification;
    }

    /**
     * Sets the value of the importerSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link NaftaImporterSpecificationType }
     *     
     */
    public void setImporterSpecification(NaftaImporterSpecificationType value) {
        this.importerSpecification = value;
    }

    /**
     * Gets the value of the signatureContact property.
     * 
     * @return
     *     possible object is
     *     {@link Contact }
     *     
     */
    public Contact getSignatureContact() {
        return signatureContact;
    }

    /**
     * Sets the value of the signatureContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link Contact }
     *     
     */
    public void setSignatureContact(Contact value) {
        this.signatureContact = value;
    }

    /**
     * Gets the value of the producerSpecification property.
     * 
     * @return
     *     possible object is
     *     {@link NaftaProducerSpecificationType }
     *     
     */
    public NaftaProducerSpecificationType getProducerSpecification() {
        return producerSpecification;
    }

    /**
     * Sets the value of the producerSpecification property.
     * 
     * @param value
     *     allowed object is
     *     {@link NaftaProducerSpecificationType }
     *     
     */
    public void setProducerSpecification(NaftaProducerSpecificationType value) {
        this.producerSpecification = value;
    }

    /**
     * Gets the value of the producers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the producers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProducers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NaftaProducer }
     * 
     * 
     */
    public List<NaftaProducer> getProducers() {
        if (producers == null) {
            producers = new ArrayList<NaftaProducer>();
        }
        return this.producers;
    }

    /**
     * Gets the value of the customerImageUsages property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customerImageUsages property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomerImageUsages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomerImageUsage }
     * 
     * 
     */
    public List<CustomerImageUsage> getCustomerImageUsages() {
        if (customerImageUsages == null) {
            customerImageUsages = new ArrayList<CustomerImageUsage>();
        }
        return this.customerImageUsages;
    }

}
