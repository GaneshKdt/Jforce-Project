
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * These special services are available at the package level for some or all service types. If the shipper is requesting a special service which requires additional data, the package special service type must be present in the specialServiceTypes collection, and the supporting detail must be provided in the appropriate sub-object below.
 * 
 * <p>Java class for PackageSpecialServicesRequested complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageSpecialServicesRequested">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SpecialServiceTypes" type="{http://fedex.com/ws/ship/v17}PackageSpecialServiceType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CodDetail" type="{http://fedex.com/ws/ship/v17}CodDetail" minOccurs="0"/>
 *         &lt;element name="DangerousGoodsDetail" type="{http://fedex.com/ws/ship/v17}DangerousGoodsDetail" minOccurs="0"/>
 *         &lt;element name="DryIceWeight" type="{http://fedex.com/ws/ship/v17}Weight" minOccurs="0"/>
 *         &lt;element name="SignatureOptionDetail" type="{http://fedex.com/ws/ship/v17}SignatureOptionDetail" minOccurs="0"/>
 *         &lt;element name="PriorityAlertDetail" type="{http://fedex.com/ws/ship/v17}PriorityAlertDetail" minOccurs="0"/>
 *         &lt;element name="AlcoholDetail" type="{http://fedex.com/ws/ship/v17}AlcoholDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageSpecialServicesRequested", propOrder = {
    "specialServiceTypes",
    "codDetail",
    "dangerousGoodsDetail",
    "dryIceWeight",
    "signatureOptionDetail",
    "priorityAlertDetail",
    "alcoholDetail"
})
public class PackageSpecialServicesRequested {

    @XmlElement(name = "SpecialServiceTypes")
    protected List<PackageSpecialServiceType> specialServiceTypes;
    @XmlElement(name = "CodDetail")
    protected CodDetail codDetail;
    @XmlElement(name = "DangerousGoodsDetail")
    protected DangerousGoodsDetail dangerousGoodsDetail;
    @XmlElement(name = "DryIceWeight")
    protected Weight dryIceWeight;
    @XmlElement(name = "SignatureOptionDetail")
    protected SignatureOptionDetail signatureOptionDetail;
    @XmlElement(name = "PriorityAlertDetail")
    protected PriorityAlertDetail priorityAlertDetail;
    @XmlElement(name = "AlcoholDetail")
    protected AlcoholDetail alcoholDetail;

    /**
     * Gets the value of the specialServiceTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specialServiceTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecialServiceTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PackageSpecialServiceType }
     * 
     * 
     */
    public List<PackageSpecialServiceType> getSpecialServiceTypes() {
        if (specialServiceTypes == null) {
            specialServiceTypes = new ArrayList<PackageSpecialServiceType>();
        }
        return this.specialServiceTypes;
    }

    /**
     * Gets the value of the codDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CodDetail }
     *     
     */
    public CodDetail getCodDetail() {
        return codDetail;
    }

    /**
     * Sets the value of the codDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodDetail }
     *     
     */
    public void setCodDetail(CodDetail value) {
        this.codDetail = value;
    }

    /**
     * Gets the value of the dangerousGoodsDetail property.
     * 
     * @return
     *     possible object is
     *     {@link DangerousGoodsDetail }
     *     
     */
    public DangerousGoodsDetail getDangerousGoodsDetail() {
        return dangerousGoodsDetail;
    }

    /**
     * Sets the value of the dangerousGoodsDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link DangerousGoodsDetail }
     *     
     */
    public void setDangerousGoodsDetail(DangerousGoodsDetail value) {
        this.dangerousGoodsDetail = value;
    }

    /**
     * Gets the value of the dryIceWeight property.
     * 
     * @return
     *     possible object is
     *     {@link Weight }
     *     
     */
    public Weight getDryIceWeight() {
        return dryIceWeight;
    }

    /**
     * Sets the value of the dryIceWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Weight }
     *     
     */
    public void setDryIceWeight(Weight value) {
        this.dryIceWeight = value;
    }

    /**
     * Gets the value of the signatureOptionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureOptionDetail }
     *     
     */
    public SignatureOptionDetail getSignatureOptionDetail() {
        return signatureOptionDetail;
    }

    /**
     * Sets the value of the signatureOptionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureOptionDetail }
     *     
     */
    public void setSignatureOptionDetail(SignatureOptionDetail value) {
        this.signatureOptionDetail = value;
    }

    /**
     * Gets the value of the priorityAlertDetail property.
     * 
     * @return
     *     possible object is
     *     {@link PriorityAlertDetail }
     *     
     */
    public PriorityAlertDetail getPriorityAlertDetail() {
        return priorityAlertDetail;
    }

    /**
     * Sets the value of the priorityAlertDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link PriorityAlertDetail }
     *     
     */
    public void setPriorityAlertDetail(PriorityAlertDetail value) {
        this.priorityAlertDetail = value;
    }

    /**
     * Gets the value of the alcoholDetail property.
     * 
     * @return
     *     possible object is
     *     {@link AlcoholDetail }
     *     
     */
    public AlcoholDetail getAlcoholDetail() {
        return alcoholDetail;
    }

    /**
     * Sets the value of the alcoholDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlcoholDetail }
     *     
     */
    public void setAlcoholDetail(AlcoholDetail value) {
        this.alcoholDetail = value;
    }

}
