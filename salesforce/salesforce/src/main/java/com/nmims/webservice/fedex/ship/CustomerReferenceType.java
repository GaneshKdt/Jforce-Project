
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerReferenceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CustomerReferenceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BILL_OF_LADING"/>
 *     &lt;enumeration value="CUSTOMER_REFERENCE"/>
 *     &lt;enumeration value="DEPARTMENT_NUMBER"/>
 *     &lt;enumeration value="ELECTRONIC_PRODUCT_CODE"/>
 *     &lt;enumeration value="INTRACOUNTRY_REGULATORY_REFERENCE"/>
 *     &lt;enumeration value="INVOICE_NUMBER"/>
 *     &lt;enumeration value="P_O_NUMBER"/>
 *     &lt;enumeration value="RMA_ASSOCIATION"/>
 *     &lt;enumeration value="SHIPMENT_INTEGRITY"/>
 *     &lt;enumeration value="STORE_NUMBER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CustomerReferenceType")
@XmlEnum
public enum CustomerReferenceType {

    BILL_OF_LADING,
    CUSTOMER_REFERENCE,
    DEPARTMENT_NUMBER,
    ELECTRONIC_PRODUCT_CODE,
    INTRACOUNTRY_REGULATORY_REFERENCE,
    INVOICE_NUMBER,
    P_O_NUMBER,
    RMA_ASSOCIATION,
    SHIPMENT_INTEGRITY,
    STORE_NUMBER;
 

    public String value() {
        return name();
    }

    public static CustomerReferenceType fromValue(String v) {
        return valueOf(v);
    }

}
