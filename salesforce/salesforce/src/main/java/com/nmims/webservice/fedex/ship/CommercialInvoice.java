
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * CommercialInvoice element is required for electronic upload of CI data. It will serve to create/transmit an Electronic Commercial Invoice through the FedEx Systems. Customers are responsible for printing their own Commercial Invoice.If you would likeFedEx to generate a Commercial Invoice and transmit it to Customs. for clearance purposes, you need to specify that in the ShippingDocumentSpecification element. If you would like a copy of the Commercial Invoice that FedEx generated returned to you in reply it needs to be specified in the ETDDetail/RequestedDocumentCopies element. Commercial Invoice support consists of maximum of 99 commodity line items.
 * 
 * <p>Java class for CommercialInvoice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommercialInvoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Comments" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="99" minOccurs="0"/>
 *         &lt;element name="FreightCharge" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TaxesOrMiscellaneousCharge" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TaxesOrMiscellaneousChargeType" type="{http://fedex.com/ws/ship/v17}TaxesOrMiscellaneousChargeType" minOccurs="0"/>
 *         &lt;element name="PackingCosts" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="HandlingCosts" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="SpecialInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeclarationStatement" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PaymentTerms" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Purpose" type="{http://fedex.com/ws/ship/v17}PurposeOfShipmentType" minOccurs="0"/>
 *         &lt;element name="CustomerReferences" type="{http://fedex.com/ws/ship/v17}CustomerReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="OriginatorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TermsOfSale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommercialInvoice", propOrder = {
    "comments",
    "freightCharge",
    "taxesOrMiscellaneousCharge",
    "taxesOrMiscellaneousChargeType",
    "packingCosts",
    "handlingCosts",
    "specialInstructions",
    "declarationStatement",
    "paymentTerms",
    "purpose",
    "customerReferences",
    "originatorName",
    "termsOfSale"
})
public class CommercialInvoice {

    @XmlElement(name = "Comments")
    protected List<String> comments;
    @XmlElement(name = "FreightCharge")
    protected Money freightCharge;
    @XmlElement(name = "TaxesOrMiscellaneousCharge")
    protected Money taxesOrMiscellaneousCharge;
    @XmlElement(name = "TaxesOrMiscellaneousChargeType")
    protected TaxesOrMiscellaneousChargeType taxesOrMiscellaneousChargeType;
    @XmlElement(name = "PackingCosts")
    protected Money packingCosts;
    @XmlElement(name = "HandlingCosts")
    protected Money handlingCosts;
    @XmlElement(name = "SpecialInstructions")
    protected String specialInstructions;
    @XmlElement(name = "DeclarationStatement")
    protected String declarationStatement;
    @XmlElement(name = "PaymentTerms")
    protected String paymentTerms;
    @XmlElement(name = "Purpose")
    protected PurposeOfShipmentType purpose;
    @XmlElement(name = "CustomerReferences")
    protected List<CustomerReference> customerReferences;
    @XmlElement(name = "OriginatorName")
    protected String originatorName;
    @XmlElement(name = "TermsOfSale")
    protected String termsOfSale;

    /**
     * Gets the value of the comments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the comments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getComments() {
        if (comments == null) {
            comments = new ArrayList<String>();
        }
        return this.comments;
    }

    /**
     * Gets the value of the freightCharge property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getFreightCharge() {
        return freightCharge;
    }

    /**
     * Sets the value of the freightCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setFreightCharge(Money value) {
        this.freightCharge = value;
    }

    /**
     * Gets the value of the taxesOrMiscellaneousCharge property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTaxesOrMiscellaneousCharge() {
        return taxesOrMiscellaneousCharge;
    }

    /**
     * Sets the value of the taxesOrMiscellaneousCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTaxesOrMiscellaneousCharge(Money value) {
        this.taxesOrMiscellaneousCharge = value;
    }

    /**
     * Gets the value of the taxesOrMiscellaneousChargeType property.
     * 
     * @return
     *     possible object is
     *     {@link TaxesOrMiscellaneousChargeType }
     *     
     */
    public TaxesOrMiscellaneousChargeType getTaxesOrMiscellaneousChargeType() {
        return taxesOrMiscellaneousChargeType;
    }

    /**
     * Sets the value of the taxesOrMiscellaneousChargeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxesOrMiscellaneousChargeType }
     *     
     */
    public void setTaxesOrMiscellaneousChargeType(TaxesOrMiscellaneousChargeType value) {
        this.taxesOrMiscellaneousChargeType = value;
    }

    /**
     * Gets the value of the packingCosts property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getPackingCosts() {
        return packingCosts;
    }

    /**
     * Sets the value of the packingCosts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setPackingCosts(Money value) {
        this.packingCosts = value;
    }

    /**
     * Gets the value of the handlingCosts property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getHandlingCosts() {
        return handlingCosts;
    }

    /**
     * Sets the value of the handlingCosts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setHandlingCosts(Money value) {
        this.handlingCosts = value;
    }

    /**
     * Gets the value of the specialInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialInstructions() {
        return specialInstructions;
    }

    /**
     * Sets the value of the specialInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialInstructions(String value) {
        this.specialInstructions = value;
    }

    /**
     * Gets the value of the declarationStatement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeclarationStatement() {
        return declarationStatement;
    }

    /**
     * Sets the value of the declarationStatement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeclarationStatement(String value) {
        this.declarationStatement = value;
    }

    /**
     * Gets the value of the paymentTerms property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentTerms() {
        return paymentTerms;
    }

    /**
     * Sets the value of the paymentTerms property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentTerms(String value) {
        this.paymentTerms = value;
    }

    /**
     * Gets the value of the purpose property.
     * 
     * @return
     *     possible object is
     *     {@link PurposeOfShipmentType }
     *     
     */
    public PurposeOfShipmentType getPurpose() {
        return purpose;
    }

    /**
     * Sets the value of the purpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link PurposeOfShipmentType }
     *     
     */
    public void setPurpose(PurposeOfShipmentType value) {
        this.purpose = value;
    }

    /**
     * Gets the value of the customerReferences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customerReferences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomerReferences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomerReference }
     * 
     * 
     */
    public List<CustomerReference> getCustomerReferences() {
        if (customerReferences == null) {
            customerReferences = new ArrayList<CustomerReference>();
        }
        return this.customerReferences;
    }

    /**
     * Gets the value of the originatorName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginatorName() {
        return originatorName;
    }

    /**
     * Sets the value of the originatorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginatorName(String value) {
        this.originatorName = value;
    }

    /**
     * Gets the value of the termsOfSale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTermsOfSale() {
        return termsOfSale;
    }

    /**
     * Sets the value of the termsOfSale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTermsOfSale(String value) {
        this.termsOfSale = value;
    }

}
