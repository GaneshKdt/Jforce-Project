
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomsClearanceDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomsClearanceDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Brokers" type="{http://fedex.com/ws/ship/v17}BrokerDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ClearanceBrokerage" type="{http://fedex.com/ws/ship/v17}ClearanceBrokerageType" minOccurs="0"/>
 *         &lt;element name="CustomsOptions" type="{http://fedex.com/ws/ship/v17}CustomsOptionDetail" minOccurs="0"/>
 *         &lt;element name="ImporterOfRecord" type="{http://fedex.com/ws/ship/v17}Party" minOccurs="0"/>
 *         &lt;element name="RecipientCustomsId" type="{http://fedex.com/ws/ship/v17}RecipientCustomsId" minOccurs="0"/>
 *         &lt;element name="DutiesPayment" type="{http://fedex.com/ws/ship/v17}Payment" minOccurs="0"/>
 *         &lt;element name="DocumentContent" type="{http://fedex.com/ws/ship/v17}InternationalDocumentContentType" minOccurs="0"/>
 *         &lt;element name="CustomsValue" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="FreightOnValue" type="{http://fedex.com/ws/ship/v17}FreightOnValueType" minOccurs="0"/>
 *         &lt;element name="InsuranceCharges" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="PartiesToTransactionAreRelated" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CommercialInvoice" type="{http://fedex.com/ws/ship/v17}CommercialInvoice" minOccurs="0"/>
 *         &lt;element name="Commodities" type="{http://fedex.com/ws/ship/v17}Commodity" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ExportDetail" type="{http://fedex.com/ws/ship/v17}ExportDetail" minOccurs="0"/>
 *         &lt;element name="RegulatoryControls" type="{http://fedex.com/ws/ship/v17}RegulatoryControlType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DeclarationStatementDetail" type="{http://fedex.com/ws/ship/v17}CustomsDeclarationStatementDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomsClearanceDetail", propOrder = {
    "brokers",
    "clearanceBrokerage",
    "customsOptions",
    "importerOfRecord",
    "recipientCustomsId",
    "dutiesPayment",
    "documentContent",
    "customsValue",
    "freightOnValue",
    "insuranceCharges",
    "partiesToTransactionAreRelated",
    "commercialInvoice",
    "commodities",
    "exportDetail",
    "regulatoryControls",
    "declarationStatementDetail"
})
public class CustomsClearanceDetail {

    @XmlElement(name = "Brokers")
    protected List<BrokerDetail> brokers;
    @XmlElement(name = "ClearanceBrokerage")
    protected ClearanceBrokerageType clearanceBrokerage;
    @XmlElement(name = "CustomsOptions")
    protected CustomsOptionDetail customsOptions;
    @XmlElement(name = "ImporterOfRecord")
    protected Party importerOfRecord;
    @XmlElement(name = "RecipientCustomsId")
    protected RecipientCustomsId recipientCustomsId;
    @XmlElement(name = "DutiesPayment")
    protected Payment dutiesPayment;
    @XmlElement(name = "DocumentContent")
    protected InternationalDocumentContentType documentContent;
    @XmlElement(name = "CustomsValue")
    protected Money customsValue;
    @XmlElement(name = "FreightOnValue")
    protected FreightOnValueType freightOnValue;
    @XmlElement(name = "InsuranceCharges")
    protected Money insuranceCharges;
    @XmlElement(name = "PartiesToTransactionAreRelated")
    protected Boolean partiesToTransactionAreRelated;
    @XmlElement(name = "CommercialInvoice")
    protected CommercialInvoice commercialInvoice;
    @XmlElement(name = "Commodities")
    protected List<Commodity> commodities;
    @XmlElement(name = "ExportDetail")
    protected ExportDetail exportDetail;
    @XmlElement(name = "RegulatoryControls")
    protected List<RegulatoryControlType> regulatoryControls;
    @XmlElement(name = "DeclarationStatementDetail")
    protected CustomsDeclarationStatementDetail declarationStatementDetail;

    /**
     * Gets the value of the brokers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the brokers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBrokers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BrokerDetail }
     * 
     * 
     */
    public List<BrokerDetail> getBrokers() {
        if (brokers == null) {
            brokers = new ArrayList<BrokerDetail>();
        }
        return this.brokers;
    }

    /**
     * Gets the value of the clearanceBrokerage property.
     * 
     * @return
     *     possible object is
     *     {@link ClearanceBrokerageType }
     *     
     */
    public ClearanceBrokerageType getClearanceBrokerage() {
        return clearanceBrokerage;
    }

    /**
     * Sets the value of the clearanceBrokerage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClearanceBrokerageType }
     *     
     */
    public void setClearanceBrokerage(ClearanceBrokerageType value) {
        this.clearanceBrokerage = value;
    }

    /**
     * Gets the value of the customsOptions property.
     * 
     * @return
     *     possible object is
     *     {@link CustomsOptionDetail }
     *     
     */
    public CustomsOptionDetail getCustomsOptions() {
        return customsOptions;
    }

    /**
     * Sets the value of the customsOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomsOptionDetail }
     *     
     */
    public void setCustomsOptions(CustomsOptionDetail value) {
        this.customsOptions = value;
    }

    /**
     * Gets the value of the importerOfRecord property.
     * 
     * @return
     *     possible object is
     *     {@link Party }
     *     
     */
    public Party getImporterOfRecord() {
        return importerOfRecord;
    }

    /**
     * Sets the value of the importerOfRecord property.
     * 
     * @param value
     *     allowed object is
     *     {@link Party }
     *     
     */
    public void setImporterOfRecord(Party value) {
        this.importerOfRecord = value;
    }

    /**
     * Gets the value of the recipientCustomsId property.
     * 
     * @return
     *     possible object is
     *     {@link RecipientCustomsId }
     *     
     */
    public RecipientCustomsId getRecipientCustomsId() {
        return recipientCustomsId;
    }

    /**
     * Sets the value of the recipientCustomsId property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecipientCustomsId }
     *     
     */
    public void setRecipientCustomsId(RecipientCustomsId value) {
        this.recipientCustomsId = value;
    }

    /**
     * Gets the value of the dutiesPayment property.
     * 
     * @return
     *     possible object is
     *     {@link Payment }
     *     
     */
    public Payment getDutiesPayment() {
        return dutiesPayment;
    }

    /**
     * Sets the value of the dutiesPayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payment }
     *     
     */
    public void setDutiesPayment(Payment value) {
        this.dutiesPayment = value;
    }

    /**
     * Gets the value of the documentContent property.
     * 
     * @return
     *     possible object is
     *     {@link InternationalDocumentContentType }
     *     
     */
    public InternationalDocumentContentType getDocumentContent() {
        return documentContent;
    }

    /**
     * Sets the value of the documentContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link InternationalDocumentContentType }
     *     
     */
    public void setDocumentContent(InternationalDocumentContentType value) {
        this.documentContent = value;
    }

    /**
     * Gets the value of the customsValue property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getCustomsValue() {
        return customsValue;
    }

    /**
     * Sets the value of the customsValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setCustomsValue(Money value) {
        this.customsValue = value;
    }

    /**
     * Gets the value of the freightOnValue property.
     * 
     * @return
     *     possible object is
     *     {@link FreightOnValueType }
     *     
     */
    public FreightOnValueType getFreightOnValue() {
        return freightOnValue;
    }

    /**
     * Sets the value of the freightOnValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightOnValueType }
     *     
     */
    public void setFreightOnValue(FreightOnValueType value) {
        this.freightOnValue = value;
    }

    /**
     * Gets the value of the insuranceCharges property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getInsuranceCharges() {
        return insuranceCharges;
    }

    /**
     * Sets the value of the insuranceCharges property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setInsuranceCharges(Money value) {
        this.insuranceCharges = value;
    }

    /**
     * Gets the value of the partiesToTransactionAreRelated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPartiesToTransactionAreRelated() {
        return partiesToTransactionAreRelated;
    }

    /**
     * Sets the value of the partiesToTransactionAreRelated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPartiesToTransactionAreRelated(Boolean value) {
        this.partiesToTransactionAreRelated = value;
    }

    /**
     * Gets the value of the commercialInvoice property.
     * 
     * @return
     *     possible object is
     *     {@link CommercialInvoice }
     *     
     */
    public CommercialInvoice getCommercialInvoice() {
        return commercialInvoice;
    }

    /**
     * Sets the value of the commercialInvoice property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommercialInvoice }
     *     
     */
    public void setCommercialInvoice(CommercialInvoice value) {
        this.commercialInvoice = value;
    }

    /**
     * Gets the value of the commodities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the commodities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommodities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Commodity }
     * 
     * 
     */
    public List<Commodity> getCommodities() {
        if (commodities == null) {
            commodities = new ArrayList<Commodity>();
        }
        return this.commodities;
    }

    /**
     * Gets the value of the exportDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ExportDetail }
     *     
     */
    public ExportDetail getExportDetail() {
        return exportDetail;
    }

    /**
     * Sets the value of the exportDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDetail }
     *     
     */
    public void setExportDetail(ExportDetail value) {
        this.exportDetail = value;
    }

    /**
     * Gets the value of the regulatoryControls property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the regulatoryControls property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegulatoryControls().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegulatoryControlType }
     * 
     * 
     */
    public List<RegulatoryControlType> getRegulatoryControls() {
        if (regulatoryControls == null) {
            regulatoryControls = new ArrayList<RegulatoryControlType>();
        }
        return this.regulatoryControls;
    }

    /**
     * Gets the value of the declarationStatementDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CustomsDeclarationStatementDetail }
     *     
     */
    public CustomsDeclarationStatementDetail getDeclarationStatementDetail() {
        return declarationStatementDetail;
    }

    /**
     * Sets the value of the declarationStatementDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomsDeclarationStatementDetail }
     *     
     */
    public void setDeclarationStatementDetail(CustomsDeclarationStatementDetail value) {
        this.declarationStatementDetail = value;
    }

}
