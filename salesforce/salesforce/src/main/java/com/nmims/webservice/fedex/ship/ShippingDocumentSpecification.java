
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Contains all data required for additional (non-label) shipping documents to be produced in conjunction with a specific shipment.
 * 
 * <p>Java class for ShippingDocumentSpecification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShippingDocumentSpecification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ShippingDocumentTypes" type="{http://fedex.com/ws/ship/v17}RequestedShippingDocumentType" maxOccurs="unbounded"/>
 *         &lt;element name="CertificateOfOrigin" type="{http://fedex.com/ws/ship/v17}CertificateOfOriginDetail" minOccurs="0"/>
 *         &lt;element name="CommercialInvoiceDetail" type="{http://fedex.com/ws/ship/v17}CommercialInvoiceDetail" minOccurs="0"/>
 *         &lt;element name="CustomPackageDocumentDetail" type="{http://fedex.com/ws/ship/v17}CustomDocumentDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CustomShipmentDocumentDetail" type="{http://fedex.com/ws/ship/v17}CustomDocumentDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ExportDeclarationDetail" type="{http://fedex.com/ws/ship/v17}ExportDeclarationDetail" minOccurs="0"/>
 *         &lt;element name="GeneralAgencyAgreementDetail" type="{http://fedex.com/ws/ship/v17}GeneralAgencyAgreementDetail" minOccurs="0"/>
 *         &lt;element name="NaftaCertificateOfOriginDetail" type="{http://fedex.com/ws/ship/v17}NaftaCertificateOfOriginDetail" minOccurs="0"/>
 *         &lt;element name="Op900Detail" type="{http://fedex.com/ws/ship/v17}Op900Detail" minOccurs="0"/>
 *         &lt;element name="DangerousGoodsShippersDeclarationDetail" type="{http://fedex.com/ws/ship/v17}DangerousGoodsShippersDeclarationDetail" minOccurs="0"/>
 *         &lt;element name="FreightAddressLabelDetail" type="{http://fedex.com/ws/ship/v17}FreightAddressLabelDetail" minOccurs="0"/>
 *         &lt;element name="ReturnInstructionsDetail" type="{http://fedex.com/ws/ship/v17}ReturnInstructionsDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShippingDocumentSpecification", propOrder = {
    "shippingDocumentTypes",
    "certificateOfOrigin",
    "commercialInvoiceDetail",
    "customPackageDocumentDetail",
    "customShipmentDocumentDetail",
    "exportDeclarationDetail",
    "generalAgencyAgreementDetail",
    "naftaCertificateOfOriginDetail",
    "op900Detail",
    "dangerousGoodsShippersDeclarationDetail",
    "freightAddressLabelDetail",
    "returnInstructionsDetail"
})
public class ShippingDocumentSpecification {

    @XmlElement(name = "ShippingDocumentTypes", required = true)
    protected List<RequestedShippingDocumentType> shippingDocumentTypes;
    @XmlElement(name = "CertificateOfOrigin")
    protected CertificateOfOriginDetail certificateOfOrigin;
    @XmlElement(name = "CommercialInvoiceDetail")
    protected CommercialInvoiceDetail commercialInvoiceDetail;
    @XmlElement(name = "CustomPackageDocumentDetail")
    protected List<CustomDocumentDetail> customPackageDocumentDetail;
    @XmlElement(name = "CustomShipmentDocumentDetail")
    protected List<CustomDocumentDetail> customShipmentDocumentDetail;
    @XmlElement(name = "ExportDeclarationDetail")
    protected ExportDeclarationDetail exportDeclarationDetail;
    @XmlElement(name = "GeneralAgencyAgreementDetail")
    protected GeneralAgencyAgreementDetail generalAgencyAgreementDetail;
    @XmlElement(name = "NaftaCertificateOfOriginDetail")
    protected NaftaCertificateOfOriginDetail naftaCertificateOfOriginDetail;
    @XmlElement(name = "Op900Detail")
    protected Op900Detail op900Detail;
    @XmlElement(name = "DangerousGoodsShippersDeclarationDetail")
    protected DangerousGoodsShippersDeclarationDetail dangerousGoodsShippersDeclarationDetail;
    @XmlElement(name = "FreightAddressLabelDetail")
    protected FreightAddressLabelDetail freightAddressLabelDetail;
    @XmlElement(name = "ReturnInstructionsDetail")
    protected ReturnInstructionsDetail returnInstructionsDetail;

    /**
     * Gets the value of the shippingDocumentTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shippingDocumentTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShippingDocumentTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestedShippingDocumentType }
     * 
     * 
     */
    public List<RequestedShippingDocumentType> getShippingDocumentTypes() {
        if (shippingDocumentTypes == null) {
            shippingDocumentTypes = new ArrayList<RequestedShippingDocumentType>();
        }
        return this.shippingDocumentTypes;
    }

    /**
     * Gets the value of the certificateOfOrigin property.
     * 
     * @return
     *     possible object is
     *     {@link CertificateOfOriginDetail }
     *     
     */
    public CertificateOfOriginDetail getCertificateOfOrigin() {
        return certificateOfOrigin;
    }

    /**
     * Sets the value of the certificateOfOrigin property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificateOfOriginDetail }
     *     
     */
    public void setCertificateOfOrigin(CertificateOfOriginDetail value) {
        this.certificateOfOrigin = value;
    }

    /**
     * Gets the value of the commercialInvoiceDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CommercialInvoiceDetail }
     *     
     */
    public CommercialInvoiceDetail getCommercialInvoiceDetail() {
        return commercialInvoiceDetail;
    }

    /**
     * Sets the value of the commercialInvoiceDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommercialInvoiceDetail }
     *     
     */
    public void setCommercialInvoiceDetail(CommercialInvoiceDetail value) {
        this.commercialInvoiceDetail = value;
    }

    /**
     * Gets the value of the customPackageDocumentDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customPackageDocumentDetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomPackageDocumentDetail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomDocumentDetail }
     * 
     * 
     */
    public List<CustomDocumentDetail> getCustomPackageDocumentDetail() {
        if (customPackageDocumentDetail == null) {
            customPackageDocumentDetail = new ArrayList<CustomDocumentDetail>();
        }
        return this.customPackageDocumentDetail;
    }

    /**
     * Gets the value of the customShipmentDocumentDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customShipmentDocumentDetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomShipmentDocumentDetail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomDocumentDetail }
     * 
     * 
     */
    public List<CustomDocumentDetail> getCustomShipmentDocumentDetail() {
        if (customShipmentDocumentDetail == null) {
            customShipmentDocumentDetail = new ArrayList<CustomDocumentDetail>();
        }
        return this.customShipmentDocumentDetail;
    }

    /**
     * Gets the value of the exportDeclarationDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ExportDeclarationDetail }
     *     
     */
    public ExportDeclarationDetail getExportDeclarationDetail() {
        return exportDeclarationDetail;
    }

    /**
     * Sets the value of the exportDeclarationDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDeclarationDetail }
     *     
     */
    public void setExportDeclarationDetail(ExportDeclarationDetail value) {
        this.exportDeclarationDetail = value;
    }

    /**
     * Gets the value of the generalAgencyAgreementDetail property.
     * 
     * @return
     *     possible object is
     *     {@link GeneralAgencyAgreementDetail }
     *     
     */
    public GeneralAgencyAgreementDetail getGeneralAgencyAgreementDetail() {
        return generalAgencyAgreementDetail;
    }

    /**
     * Sets the value of the generalAgencyAgreementDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneralAgencyAgreementDetail }
     *     
     */
    public void setGeneralAgencyAgreementDetail(GeneralAgencyAgreementDetail value) {
        this.generalAgencyAgreementDetail = value;
    }

    /**
     * Gets the value of the naftaCertificateOfOriginDetail property.
     * 
     * @return
     *     possible object is
     *     {@link NaftaCertificateOfOriginDetail }
     *     
     */
    public NaftaCertificateOfOriginDetail getNaftaCertificateOfOriginDetail() {
        return naftaCertificateOfOriginDetail;
    }

    /**
     * Sets the value of the naftaCertificateOfOriginDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link NaftaCertificateOfOriginDetail }
     *     
     */
    public void setNaftaCertificateOfOriginDetail(NaftaCertificateOfOriginDetail value) {
        this.naftaCertificateOfOriginDetail = value;
    }

    /**
     * Gets the value of the op900Detail property.
     * 
     * @return
     *     possible object is
     *     {@link Op900Detail }
     *     
     */
    public Op900Detail getOp900Detail() {
        return op900Detail;
    }

    /**
     * Sets the value of the op900Detail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Op900Detail }
     *     
     */
    public void setOp900Detail(Op900Detail value) {
        this.op900Detail = value;
    }

    /**
     * Gets the value of the dangerousGoodsShippersDeclarationDetail property.
     * 
     * @return
     *     possible object is
     *     {@link DangerousGoodsShippersDeclarationDetail }
     *     
     */
    public DangerousGoodsShippersDeclarationDetail getDangerousGoodsShippersDeclarationDetail() {
        return dangerousGoodsShippersDeclarationDetail;
    }

    /**
     * Sets the value of the dangerousGoodsShippersDeclarationDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link DangerousGoodsShippersDeclarationDetail }
     *     
     */
    public void setDangerousGoodsShippersDeclarationDetail(DangerousGoodsShippersDeclarationDetail value) {
        this.dangerousGoodsShippersDeclarationDetail = value;
    }

    /**
     * Gets the value of the freightAddressLabelDetail property.
     * 
     * @return
     *     possible object is
     *     {@link FreightAddressLabelDetail }
     *     
     */
    public FreightAddressLabelDetail getFreightAddressLabelDetail() {
        return freightAddressLabelDetail;
    }

    /**
     * Sets the value of the freightAddressLabelDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightAddressLabelDetail }
     *     
     */
    public void setFreightAddressLabelDetail(FreightAddressLabelDetail value) {
        this.freightAddressLabelDetail = value;
    }

    /**
     * Gets the value of the returnInstructionsDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnInstructionsDetail }
     *     
     */
    public ReturnInstructionsDetail getReturnInstructionsDetail() {
        return returnInstructionsDetail;
    }

    /**
     * Sets the value of the returnInstructionsDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnInstructionsDetail }
     *     
     */
    public void setReturnInstructionsDetail(ReturnInstructionsDetail value) {
        this.returnInstructionsDetail = value;
    }

}
