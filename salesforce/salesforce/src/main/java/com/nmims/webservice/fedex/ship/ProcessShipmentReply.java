
package com.nmims.webservice.fedex.ship;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProcessShipmentReply complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessShipmentReply">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HighestSeverity" type="{http://fedex.com/ws/ship/v17}NotificationSeverityType"/>
 *         &lt;element name="Notifications" type="{http://fedex.com/ws/ship/v17}Notification" maxOccurs="unbounded"/>
 *         &lt;element name="TransactionDetail" type="{http://fedex.com/ws/ship/v17}TransactionDetail" minOccurs="0"/>
 *         &lt;element name="Version" type="{http://fedex.com/ws/ship/v17}VersionId"/>
 *         &lt;element name="JobId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CompletedShipmentDetail" type="{http://fedex.com/ws/ship/v17}CompletedShipmentDetail" minOccurs="0"/>
 *         &lt;element name="ErrorLabels" type="{http://fedex.com/ws/ship/v17}ShippingDocument" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessShipmentReply", propOrder = {
    "highestSeverity",
    "notifications",
    "transactionDetail",
    "version",
    "jobId",
    "completedShipmentDetail",
    "errorLabels"
})
@XmlRootElement(name = "ProcessShipmentReply")//Added to resolve JAXB issue
public class ProcessShipmentReply {

    @XmlElement(name = "HighestSeverity", required = true)
    protected NotificationSeverityType highestSeverity;
    @XmlElement(name = "Notifications", required = true)
    protected List<Notification> notifications;
    @XmlElement(name = "TransactionDetail")
    protected TransactionDetail transactionDetail;
    @XmlElement(name = "Version", required = true)
    protected VersionId version;
    @XmlElement(name = "JobId")
    protected String jobId;
    @XmlElement(name = "CompletedShipmentDetail")
    protected CompletedShipmentDetail completedShipmentDetail;
    @XmlElement(name = "ErrorLabels")
    protected List<ShippingDocument> errorLabels;

    /**
     * Gets the value of the highestSeverity property.
     * 
     * @return
     *     possible object is
     *     {@link NotificationSeverityType }
     *     
     */
    public NotificationSeverityType getHighestSeverity() {
        return highestSeverity;
    }

    /**
     * Sets the value of the highestSeverity property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationSeverityType }
     *     
     */
    public void setHighestSeverity(NotificationSeverityType value) {
        this.highestSeverity = value;
    }

    /**
     * Gets the value of the notifications property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notifications property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotifications().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Notification }
     * 
     * 
     */
    public List<Notification> getNotifications() {
        if (notifications == null) {
            notifications = new ArrayList<Notification>();
        }
        return this.notifications;
    }

    /**
     * Gets the value of the transactionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionDetail }
     *     
     */
    public TransactionDetail getTransactionDetail() {
        return transactionDetail;
    }

    /**
     * Sets the value of the transactionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionDetail }
     *     
     */
    public void setTransactionDetail(TransactionDetail value) {
        this.transactionDetail = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link VersionId }
     *     
     */
    public VersionId getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link VersionId }
     *     
     */
    public void setVersion(VersionId value) {
        this.version = value;
    }

    /**
     * Gets the value of the jobId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Sets the value of the jobId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobId(String value) {
        this.jobId = value;
    }

    /**
     * Gets the value of the completedShipmentDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CompletedShipmentDetail }
     *     
     */
    public CompletedShipmentDetail getCompletedShipmentDetail() {
        return completedShipmentDetail;
    }

    /**
     * Sets the value of the completedShipmentDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletedShipmentDetail }
     *     
     */
    public void setCompletedShipmentDetail(CompletedShipmentDetail value) {
        this.completedShipmentDetail = value;
    }

    /**
     * Gets the value of the errorLabels property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errorLabels property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrorLabels().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShippingDocument }
     * 
     * 
     */
    public List<ShippingDocument> getErrorLabels() {
        if (errorLabels == null) {
            errorLabels = new ArrayList<ShippingDocument>();
        }
        return this.errorLabels;
    }

}
