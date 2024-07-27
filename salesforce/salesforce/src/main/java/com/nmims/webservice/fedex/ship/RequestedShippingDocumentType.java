
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RequestedShippingDocumentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RequestedShippingDocumentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CERTIFICATE_OF_ORIGIN"/>
 *     &lt;enumeration value="COMMERCIAL_INVOICE"/>
 *     &lt;enumeration value="CUSTOMER_SPECIFIED_LABELS"/>
 *     &lt;enumeration value="CUSTOM_PACKAGE_DOCUMENT"/>
 *     &lt;enumeration value="CUSTOM_SHIPMENT_DOCUMENT"/>
 *     &lt;enumeration value="DANGEROUS_GOODS_SHIPPERS_DECLARATION"/>
 *     &lt;enumeration value="EXPORT_DECLARATION"/>
 *     &lt;enumeration value="FREIGHT_ADDRESS_LABEL"/>
 *     &lt;enumeration value="GENERAL_AGENCY_AGREEMENT"/>
 *     &lt;enumeration value="LABEL"/>
 *     &lt;enumeration value="NAFTA_CERTIFICATE_OF_ORIGIN"/>
 *     &lt;enumeration value="OP_900"/>
 *     &lt;enumeration value="PRO_FORMA_INVOICE"/>
 *     &lt;enumeration value="RETURN_INSTRUCTIONS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RequestedShippingDocumentType")
@XmlEnum
public enum RequestedShippingDocumentType {

    CERTIFICATE_OF_ORIGIN,
    COMMERCIAL_INVOICE,
    CUSTOMER_SPECIFIED_LABELS,
    CUSTOM_PACKAGE_DOCUMENT,
    CUSTOM_SHIPMENT_DOCUMENT,
    DANGEROUS_GOODS_SHIPPERS_DECLARATION,
    EXPORT_DECLARATION,
    FREIGHT_ADDRESS_LABEL,
    GENERAL_AGENCY_AGREEMENT,
    LABEL,
    NAFTA_CERTIFICATE_OF_ORIGIN,
    OP_900,
    PRO_FORMA_INVOICE,
    RETURN_INSTRUCTIONS;

    public String value() {
        return name();
    }

    public static RequestedShippingDocumentType fromValue(String v) {
        return valueOf(v);
    }

}
