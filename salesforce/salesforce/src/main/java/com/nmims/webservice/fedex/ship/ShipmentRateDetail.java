
package com.nmims.webservice.fedex.ship;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Data for a shipment's total/summary rates, as calculated per a specific rate type. The "total..." fields may differ from the sum of corresponding package data for Multiweight or Express MPS.
 * 
 * <p>Java class for ShipmentRateDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShipmentRateDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RateType" type="{http://fedex.com/ws/ship/v17}ReturnedRateType" minOccurs="0"/>
 *         &lt;element name="RateScale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RateZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PricingCode" type="{http://fedex.com/ws/ship/v17}PricingCodeType" minOccurs="0"/>
 *         &lt;element name="RatedWeightMethod" type="{http://fedex.com/ws/ship/v17}RatedWeightMethod" minOccurs="0"/>
 *         &lt;element name="MinimumChargeType" type="{http://fedex.com/ws/ship/v17}MinimumChargeType" minOccurs="0"/>
 *         &lt;element name="CurrencyExchangeRate" type="{http://fedex.com/ws/ship/v17}CurrencyExchangeRate" minOccurs="0"/>
 *         &lt;element name="SpecialRatingApplied" type="{http://fedex.com/ws/ship/v17}SpecialRatingAppliedType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DimDivisor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="DimDivisorType" type="{http://fedex.com/ws/ship/v17}RateDimensionalDivisorType" minOccurs="0"/>
 *         &lt;element name="FuelSurchargePercent" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="TotalBillingWeight" type="{http://fedex.com/ws/ship/v17}Weight" minOccurs="0"/>
 *         &lt;element name="TotalDimWeight" type="{http://fedex.com/ws/ship/v17}Weight" minOccurs="0"/>
 *         &lt;element name="TotalBaseCharge" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TotalFreightDiscounts" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TotalNetFreight" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TotalSurcharges" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TotalNetFedExCharge" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TotalTaxes" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TotalNetCharge" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TotalRebates" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TotalDutiesAndTaxes" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="TotalNetChargeWithDutiesAndTaxes" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="ShipmentLegRateDetails" type="{http://fedex.com/ws/ship/v17}ShipmentLegRateDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="FreightRateDetail" type="{http://fedex.com/ws/ship/v17}FreightRateDetail" minOccurs="0"/>
 *         &lt;element name="FreightDiscounts" type="{http://fedex.com/ws/ship/v17}RateDiscount" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Rebates" type="{http://fedex.com/ws/ship/v17}Rebate" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Surcharges" type="{http://fedex.com/ws/ship/v17}Surcharge" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Taxes" type="{http://fedex.com/ws/ship/v17}Tax" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DutiesAndTaxes" type="{http://fedex.com/ws/ship/v17}EdtCommodityTax" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="VariableHandlingCharges" type="{http://fedex.com/ws/ship/v17}VariableHandlingCharges" minOccurs="0"/>
 *         &lt;element name="TotalVariableHandlingCharges" type="{http://fedex.com/ws/ship/v17}VariableHandlingCharges" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipmentRateDetail", propOrder = {
    "rateType",
    "rateScale",
    "rateZone",
    "pricingCode",
    "ratedWeightMethod",
    "minimumChargeType",
    "currencyExchangeRate",
    "specialRatingApplied",
    "dimDivisor",
    "dimDivisorType",
    "fuelSurchargePercent",
    "totalBillingWeight",
    "totalDimWeight",
    "totalBaseCharge",
    "totalFreightDiscounts",
    "totalNetFreight",
    "totalSurcharges",
    "totalNetFedExCharge",
    "totalTaxes",
    "totalNetCharge",
    "totalRebates",
    "totalDutiesAndTaxes",
    "totalNetChargeWithDutiesAndTaxes",
    "shipmentLegRateDetails",
    "freightRateDetail",
    "freightDiscounts",
    "rebates",
    "surcharges",
    "taxes",
    "dutiesAndTaxes",
    "variableHandlingCharges",
    "totalVariableHandlingCharges"
})
public class ShipmentRateDetail {

    @XmlElement(name = "RateType")
    protected ReturnedRateType rateType;
    @XmlElement(name = "RateScale")
    protected String rateScale;
    @XmlElement(name = "RateZone")
    protected String rateZone;
    @XmlElement(name = "PricingCode")
    protected PricingCodeType pricingCode;
    @XmlElement(name = "RatedWeightMethod")
    protected RatedWeightMethod ratedWeightMethod;
    @XmlElement(name = "MinimumChargeType")
    protected MinimumChargeType minimumChargeType;
    @XmlElement(name = "CurrencyExchangeRate")
    protected CurrencyExchangeRate currencyExchangeRate;
    @XmlElement(name = "SpecialRatingApplied")
    protected List<SpecialRatingAppliedType> specialRatingApplied;
    @XmlElement(name = "DimDivisor")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger dimDivisor;
    @XmlElement(name = "DimDivisorType")
    protected RateDimensionalDivisorType dimDivisorType;
    @XmlElement(name = "FuelSurchargePercent")
    protected BigDecimal fuelSurchargePercent;
    @XmlElement(name = "TotalBillingWeight")
    protected Weight totalBillingWeight;
    @XmlElement(name = "TotalDimWeight")
    protected Weight totalDimWeight;
    @XmlElement(name = "TotalBaseCharge")
    protected Money totalBaseCharge;
    @XmlElement(name = "TotalFreightDiscounts")
    protected Money totalFreightDiscounts;
    @XmlElement(name = "TotalNetFreight")
    protected Money totalNetFreight;
    @XmlElement(name = "TotalSurcharges")
    protected Money totalSurcharges;
    @XmlElement(name = "TotalNetFedExCharge")
    protected Money totalNetFedExCharge;
    @XmlElement(name = "TotalTaxes")
    protected Money totalTaxes;
    @XmlElement(name = "TotalNetCharge")
    protected Money totalNetCharge;
    @XmlElement(name = "TotalRebates")
    protected Money totalRebates;
    @XmlElement(name = "TotalDutiesAndTaxes")
    protected Money totalDutiesAndTaxes;
    @XmlElement(name = "TotalNetChargeWithDutiesAndTaxes")
    protected Money totalNetChargeWithDutiesAndTaxes;
    @XmlElement(name = "ShipmentLegRateDetails")
    protected List<ShipmentLegRateDetail> shipmentLegRateDetails;
    @XmlElement(name = "FreightRateDetail")
    protected FreightRateDetail freightRateDetail;
    @XmlElement(name = "FreightDiscounts")
    protected List<RateDiscount> freightDiscounts;
    @XmlElement(name = "Rebates")
    protected List<Rebate> rebates;
    @XmlElement(name = "Surcharges")
    protected List<Surcharge> surcharges;
    @XmlElement(name = "Taxes")
    protected List<Tax> taxes;
    @XmlElement(name = "DutiesAndTaxes")
    protected List<EdtCommodityTax> dutiesAndTaxes;
    @XmlElement(name = "VariableHandlingCharges")
    protected VariableHandlingCharges variableHandlingCharges;
    @XmlElement(name = "TotalVariableHandlingCharges")
    protected VariableHandlingCharges totalVariableHandlingCharges;

    /**
     * Gets the value of the rateType property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnedRateType }
     *     
     */
    public ReturnedRateType getRateType() {
        return rateType;
    }

    /**
     * Sets the value of the rateType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnedRateType }
     *     
     */
    public void setRateType(ReturnedRateType value) {
        this.rateType = value;
    }

    /**
     * Gets the value of the rateScale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateScale() {
        return rateScale;
    }

    /**
     * Sets the value of the rateScale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateScale(String value) {
        this.rateScale = value;
    }

    /**
     * Gets the value of the rateZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateZone() {
        return rateZone;
    }

    /**
     * Sets the value of the rateZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateZone(String value) {
        this.rateZone = value;
    }

    /**
     * Gets the value of the pricingCode property.
     * 
     * @return
     *     possible object is
     *     {@link PricingCodeType }
     *     
     */
    public PricingCodeType getPricingCode() {
        return pricingCode;
    }

    /**
     * Sets the value of the pricingCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PricingCodeType }
     *     
     */
    public void setPricingCode(PricingCodeType value) {
        this.pricingCode = value;
    }

    /**
     * Gets the value of the ratedWeightMethod property.
     * 
     * @return
     *     possible object is
     *     {@link RatedWeightMethod }
     *     
     */
    public RatedWeightMethod getRatedWeightMethod() {
        return ratedWeightMethod;
    }

    /**
     * Sets the value of the ratedWeightMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link RatedWeightMethod }
     *     
     */
    public void setRatedWeightMethod(RatedWeightMethod value) {
        this.ratedWeightMethod = value;
    }

    /**
     * Gets the value of the minimumChargeType property.
     * 
     * @return
     *     possible object is
     *     {@link MinimumChargeType }
     *     
     */
    public MinimumChargeType getMinimumChargeType() {
        return minimumChargeType;
    }

    /**
     * Sets the value of the minimumChargeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MinimumChargeType }
     *     
     */
    public void setMinimumChargeType(MinimumChargeType value) {
        this.minimumChargeType = value;
    }

    /**
     * Gets the value of the currencyExchangeRate property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyExchangeRate }
     *     
     */
    public CurrencyExchangeRate getCurrencyExchangeRate() {
        return currencyExchangeRate;
    }

    /**
     * Sets the value of the currencyExchangeRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyExchangeRate }
     *     
     */
    public void setCurrencyExchangeRate(CurrencyExchangeRate value) {
        this.currencyExchangeRate = value;
    }

    /**
     * Gets the value of the specialRatingApplied property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specialRatingApplied property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecialRatingApplied().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecialRatingAppliedType }
     * 
     * 
     */
    public List<SpecialRatingAppliedType> getSpecialRatingApplied() {
        if (specialRatingApplied == null) {
            specialRatingApplied = new ArrayList<SpecialRatingAppliedType>();
        }
        return this.specialRatingApplied;
    }

    /**
     * Gets the value of the dimDivisor property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDimDivisor() {
        return dimDivisor;
    }

    /**
     * Sets the value of the dimDivisor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDimDivisor(BigInteger value) {
        this.dimDivisor = value;
    }

    /**
     * Gets the value of the dimDivisorType property.
     * 
     * @return
     *     possible object is
     *     {@link RateDimensionalDivisorType }
     *     
     */
    public RateDimensionalDivisorType getDimDivisorType() {
        return dimDivisorType;
    }

    /**
     * Sets the value of the dimDivisorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link RateDimensionalDivisorType }
     *     
     */
    public void setDimDivisorType(RateDimensionalDivisorType value) {
        this.dimDivisorType = value;
    }

    /**
     * Gets the value of the fuelSurchargePercent property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFuelSurchargePercent() {
        return fuelSurchargePercent;
    }

    /**
     * Sets the value of the fuelSurchargePercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFuelSurchargePercent(BigDecimal value) {
        this.fuelSurchargePercent = value;
    }

    /**
     * Gets the value of the totalBillingWeight property.
     * 
     * @return
     *     possible object is
     *     {@link Weight }
     *     
     */
    public Weight getTotalBillingWeight() {
        return totalBillingWeight;
    }

    /**
     * Sets the value of the totalBillingWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Weight }
     *     
     */
    public void setTotalBillingWeight(Weight value) {
        this.totalBillingWeight = value;
    }

    /**
     * Gets the value of the totalDimWeight property.
     * 
     * @return
     *     possible object is
     *     {@link Weight }
     *     
     */
    public Weight getTotalDimWeight() {
        return totalDimWeight;
    }

    /**
     * Sets the value of the totalDimWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Weight }
     *     
     */
    public void setTotalDimWeight(Weight value) {
        this.totalDimWeight = value;
    }

    /**
     * Gets the value of the totalBaseCharge property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalBaseCharge() {
        return totalBaseCharge;
    }

    /**
     * Sets the value of the totalBaseCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalBaseCharge(Money value) {
        this.totalBaseCharge = value;
    }

    /**
     * Gets the value of the totalFreightDiscounts property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalFreightDiscounts() {
        return totalFreightDiscounts;
    }

    /**
     * Sets the value of the totalFreightDiscounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalFreightDiscounts(Money value) {
        this.totalFreightDiscounts = value;
    }

    /**
     * Gets the value of the totalNetFreight property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalNetFreight() {
        return totalNetFreight;
    }

    /**
     * Sets the value of the totalNetFreight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalNetFreight(Money value) {
        this.totalNetFreight = value;
    }

    /**
     * Gets the value of the totalSurcharges property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalSurcharges() {
        return totalSurcharges;
    }

    /**
     * Sets the value of the totalSurcharges property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalSurcharges(Money value) {
        this.totalSurcharges = value;
    }

    /**
     * Gets the value of the totalNetFedExCharge property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalNetFedExCharge() {
        return totalNetFedExCharge;
    }

    /**
     * Sets the value of the totalNetFedExCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalNetFedExCharge(Money value) {
        this.totalNetFedExCharge = value;
    }

    /**
     * Gets the value of the totalTaxes property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalTaxes() {
        return totalTaxes;
    }

    /**
     * Sets the value of the totalTaxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalTaxes(Money value) {
        this.totalTaxes = value;
    }

    /**
     * Gets the value of the totalNetCharge property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalNetCharge() {
        return totalNetCharge;
    }

    /**
     * Sets the value of the totalNetCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalNetCharge(Money value) {
        this.totalNetCharge = value;
    }

    /**
     * Gets the value of the totalRebates property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalRebates() {
        return totalRebates;
    }

    /**
     * Sets the value of the totalRebates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalRebates(Money value) {
        this.totalRebates = value;
    }

    /**
     * Gets the value of the totalDutiesAndTaxes property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalDutiesAndTaxes() {
        return totalDutiesAndTaxes;
    }

    /**
     * Sets the value of the totalDutiesAndTaxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalDutiesAndTaxes(Money value) {
        this.totalDutiesAndTaxes = value;
    }

    /**
     * Gets the value of the totalNetChargeWithDutiesAndTaxes property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getTotalNetChargeWithDutiesAndTaxes() {
        return totalNetChargeWithDutiesAndTaxes;
    }

    /**
     * Sets the value of the totalNetChargeWithDutiesAndTaxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setTotalNetChargeWithDutiesAndTaxes(Money value) {
        this.totalNetChargeWithDutiesAndTaxes = value;
    }

    /**
     * Gets the value of the shipmentLegRateDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shipmentLegRateDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShipmentLegRateDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShipmentLegRateDetail }
     * 
     * 
     */
    public List<ShipmentLegRateDetail> getShipmentLegRateDetails() {
        if (shipmentLegRateDetails == null) {
            shipmentLegRateDetails = new ArrayList<ShipmentLegRateDetail>();
        }
        return this.shipmentLegRateDetails;
    }

    /**
     * Gets the value of the freightRateDetail property.
     * 
     * @return
     *     possible object is
     *     {@link FreightRateDetail }
     *     
     */
    public FreightRateDetail getFreightRateDetail() {
        return freightRateDetail;
    }

    /**
     * Sets the value of the freightRateDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreightRateDetail }
     *     
     */
    public void setFreightRateDetail(FreightRateDetail value) {
        this.freightRateDetail = value;
    }

    /**
     * Gets the value of the freightDiscounts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the freightDiscounts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFreightDiscounts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RateDiscount }
     * 
     * 
     */
    public List<RateDiscount> getFreightDiscounts() {
        if (freightDiscounts == null) {
            freightDiscounts = new ArrayList<RateDiscount>();
        }
        return this.freightDiscounts;
    }

    /**
     * Gets the value of the rebates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rebates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRebates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Rebate }
     * 
     * 
     */
    public List<Rebate> getRebates() {
        if (rebates == null) {
            rebates = new ArrayList<Rebate>();
        }
        return this.rebates;
    }

    /**
     * Gets the value of the surcharges property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the surcharges property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSurcharges().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Surcharge }
     * 
     * 
     */
    public List<Surcharge> getSurcharges() {
        if (surcharges == null) {
            surcharges = new ArrayList<Surcharge>();
        }
        return this.surcharges;
    }

    /**
     * Gets the value of the taxes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taxes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaxes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tax }
     * 
     * 
     */
    public List<Tax> getTaxes() {
        if (taxes == null) {
            taxes = new ArrayList<Tax>();
        }
        return this.taxes;
    }

    /**
     * Gets the value of the dutiesAndTaxes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dutiesAndTaxes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDutiesAndTaxes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EdtCommodityTax }
     * 
     * 
     */
    public List<EdtCommodityTax> getDutiesAndTaxes() {
        if (dutiesAndTaxes == null) {
            dutiesAndTaxes = new ArrayList<EdtCommodityTax>();
        }
        return this.dutiesAndTaxes;
    }

    /**
     * Gets the value of the variableHandlingCharges property.
     * 
     * @return
     *     possible object is
     *     {@link VariableHandlingCharges }
     *     
     */
    public VariableHandlingCharges getVariableHandlingCharges() {
        return variableHandlingCharges;
    }

    /**
     * Sets the value of the variableHandlingCharges property.
     * 
     * @param value
     *     allowed object is
     *     {@link VariableHandlingCharges }
     *     
     */
    public void setVariableHandlingCharges(VariableHandlingCharges value) {
        this.variableHandlingCharges = value;
    }

    /**
     * Gets the value of the totalVariableHandlingCharges property.
     * 
     * @return
     *     possible object is
     *     {@link VariableHandlingCharges }
     *     
     */
    public VariableHandlingCharges getTotalVariableHandlingCharges() {
        return totalVariableHandlingCharges;
    }

    /**
     * Sets the value of the totalVariableHandlingCharges property.
     * 
     * @param value
     *     allowed object is
     *     {@link VariableHandlingCharges }
     *     
     */
    public void setTotalVariableHandlingCharges(VariableHandlingCharges value) {
        this.totalVariableHandlingCharges = value;
    }

}
