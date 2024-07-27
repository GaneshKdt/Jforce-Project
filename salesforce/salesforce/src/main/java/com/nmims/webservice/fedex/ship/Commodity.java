
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
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * 
 *             For international multiple piece shipments, commodity information must be passed in the Master and on each child transaction.
 *             If this shipment cotains more than four commodities line items, the four highest valued should be included in the first 4 occurances for this request.
 *           
 * 
 * <p>Java class for Commodity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Commodity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NumberOfPieces" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CountryOfManufacture" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HarmonizedCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Weight" type="{http://fedex.com/ws/ship/v17}Weight"/>
 *         &lt;element name="Quantity" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="QuantityUnits" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalMeasures" type="{http://fedex.com/ws/ship/v17}Measure" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="UnitPrice" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="CustomsValue" type="{http://fedex.com/ws/ship/v17}Money" minOccurs="0"/>
 *         &lt;element name="ExciseConditions" type="{http://fedex.com/ws/ship/v17}EdtExciseCondition" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ExportLicenseNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExportLicenseExpirationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="CIMarksAndNumbers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NaftaDetail" type="{http://fedex.com/ws/ship/v17}NaftaCommodityDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Commodity", propOrder = {
    "name",
    "numberOfPieces",
    "description",
    "countryOfManufacture",
    "harmonizedCode",
    "weight",
    "quantity",
    "quantityUnits",
    "additionalMeasures",
    "unitPrice",
    "customsValue",
    "exciseConditions",
    "exportLicenseNumber",
    "exportLicenseExpirationDate",
    "ciMarksAndNumbers",
    "partNumber",
    "naftaDetail"
})
public class Commodity {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "NumberOfPieces", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numberOfPieces;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "CountryOfManufacture", required = true)
    protected String countryOfManufacture;
    @XmlElement(name = "HarmonizedCode")
    protected String harmonizedCode;
    @XmlElement(name = "Weight", required = true)
    protected Weight weight;
    @XmlElement(name = "Quantity")
    protected BigDecimal quantity;
    @XmlElement(name = "QuantityUnits")
    protected String quantityUnits;
    @XmlElement(name = "AdditionalMeasures")
    protected List<Measure> additionalMeasures;
    @XmlElement(name = "UnitPrice")
    protected Money unitPrice;
    @XmlElement(name = "CustomsValue")
    protected Money customsValue;
    @XmlElement(name = "ExciseConditions")
    protected List<EdtExciseCondition> exciseConditions;
    @XmlElement(name = "ExportLicenseNumber")
    protected String exportLicenseNumber;
    @XmlElement(name = "ExportLicenseExpirationDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar exportLicenseExpirationDate;
    @XmlElement(name = "CIMarksAndNumbers")
    protected String ciMarksAndNumbers;
    @XmlElement(name = "PartNumber")
    protected String partNumber;
    @XmlElement(name = "NaftaDetail")
    protected NaftaCommodityDetail naftaDetail;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the numberOfPieces property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfPieces() {
        return numberOfPieces;
    }

    /**
     * Sets the value of the numberOfPieces property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfPieces(BigInteger value) {
        this.numberOfPieces = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the countryOfManufacture property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryOfManufacture() {
        return countryOfManufacture;
    }

    /**
     * Sets the value of the countryOfManufacture property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryOfManufacture(String value) {
        this.countryOfManufacture = value;
    }

    /**
     * Gets the value of the harmonizedCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHarmonizedCode() {
        return harmonizedCode;
    }

    /**
     * Sets the value of the harmonizedCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHarmonizedCode(String value) {
        this.harmonizedCode = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link Weight }
     *     
     */
    public Weight getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Weight }
     *     
     */
    public void setWeight(Weight value) {
        this.weight = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setQuantity(BigDecimal value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the quantityUnits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantityUnits() {
        return quantityUnits;
    }

    /**
     * Sets the value of the quantityUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantityUnits(String value) {
        this.quantityUnits = value;
    }

    /**
     * Gets the value of the additionalMeasures property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalMeasures property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalMeasures().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Measure }
     * 
     * 
     */
    public List<Measure> getAdditionalMeasures() {
        if (additionalMeasures == null) {
            additionalMeasures = new ArrayList<Measure>();
        }
        return this.additionalMeasures;
    }

    /**
     * Gets the value of the unitPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the value of the unitPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setUnitPrice(Money value) {
        this.unitPrice = value;
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
     * Gets the value of the exciseConditions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the exciseConditions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExciseConditions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EdtExciseCondition }
     * 
     * 
     */
    public List<EdtExciseCondition> getExciseConditions() {
        if (exciseConditions == null) {
            exciseConditions = new ArrayList<EdtExciseCondition>();
        }
        return this.exciseConditions;
    }

    /**
     * Gets the value of the exportLicenseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportLicenseNumber() {
        return exportLicenseNumber;
    }

    /**
     * Sets the value of the exportLicenseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportLicenseNumber(String value) {
        this.exportLicenseNumber = value;
    }

    /**
     * Gets the value of the exportLicenseExpirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExportLicenseExpirationDate() {
        return exportLicenseExpirationDate;
    }

    /**
     * Sets the value of the exportLicenseExpirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExportLicenseExpirationDate(XMLGregorianCalendar value) {
        this.exportLicenseExpirationDate = value;
    }

    /**
     * Gets the value of the ciMarksAndNumbers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCIMarksAndNumbers() {
        return ciMarksAndNumbers;
    }

    /**
     * Sets the value of the ciMarksAndNumbers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCIMarksAndNumbers(String value) {
        this.ciMarksAndNumbers = value;
    }

    /**
     * Gets the value of the partNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartNumber() {
        return partNumber;
    }

    /**
     * Sets the value of the partNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartNumber(String value) {
        this.partNumber = value;
    }

    /**
     * Gets the value of the naftaDetail property.
     * 
     * @return
     *     possible object is
     *     {@link NaftaCommodityDetail }
     *     
     */
    public NaftaCommodityDetail getNaftaDetail() {
        return naftaDetail;
    }

    /**
     * Sets the value of the naftaDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link NaftaCommodityDetail }
     *     
     */
    public void setNaftaDetail(NaftaCommodityDetail value) {
        this.naftaDetail = value;
    }

}
