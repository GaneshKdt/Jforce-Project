
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReturnedShippingDocumentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReturnedShippingDocumentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AUXILIARY_LABEL"/>
 *     &lt;enumeration value="CERTIFICATE_OF_ORIGIN"/>
 *     &lt;enumeration value="COD_AND_DELIVERY_ON_INVOICE_ACCEPTANCE_RETURN_2_D_BARCODE"/>
 *     &lt;enumeration value="COD_AND_DELIVERY_ON_INVOICE_ACCEPTANCE_RETURN_LABEL"/>
 *     &lt;enumeration value="COD_RETURN_2_D_BARCODE"/>
 *     &lt;enumeration value="COD_RETURN_LABEL"/>
 *     &lt;enumeration value="COMMERCIAL_INVOICE"/>
 *     &lt;enumeration value="CUSTOM_PACKAGE_DOCUMENT"/>
 *     &lt;enumeration value="CUSTOM_SHIPMENT_DOCUMENT"/>
 *     &lt;enumeration value="DANGEROUS_GOODS_SHIPPERS_DECLARATION"/>
 *     &lt;enumeration value="DELIVERY_ON_INVOICE_ACCEPTANCE_RETURN_2_D_BARCODE"/>
 *     &lt;enumeration value="DELIVERY_ON_INVOICE_ACCEPTANCE_RETURN_LABEL"/>
 *     &lt;enumeration value="ETD_LABEL"/>
 *     &lt;enumeration value="EXPORT_DECLARATION"/>
 *     &lt;enumeration value="FREIGHT_ADDRESS_LABEL"/>
 *     &lt;enumeration value="GENERAL_AGENCY_AGREEMENT"/>
 *     &lt;enumeration value="GROUND_BARCODE"/>
 *     &lt;enumeration value="NAFTA_CERTIFICATE_OF_ORIGIN"/>
 *     &lt;enumeration value="OP_900"/>
 *     &lt;enumeration value="OUTBOUND_2_D_BARCODE"/>
 *     &lt;enumeration value="OUTBOUND_LABEL"/>
 *     &lt;enumeration value="PRO_FORMA_INVOICE"/>
 *     &lt;enumeration value="RECIPIENT_ADDRESS_BARCODE"/>
 *     &lt;enumeration value="RECIPIENT_POSTAL_BARCODE"/>
 *     &lt;enumeration value="RETURN_INSTRUCTIONS"/>
 *     &lt;enumeration value="TERMS_AND_CONDITIONS"/>
 *     &lt;enumeration value="USPS_BARCODE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReturnedShippingDocumentType")
@XmlEnum
public enum ReturnedShippingDocumentType {

    AUXILIARY_LABEL,
    CERTIFICATE_OF_ORIGIN,
    COD_AND_DELIVERY_ON_INVOICE_ACCEPTANCE_RETURN_2_D_BARCODE,
    COD_AND_DELIVERY_ON_INVOICE_ACCEPTANCE_RETURN_LABEL,
    COD_RETURN_2_D_BARCODE,
    COD_RETURN_LABEL,
    COMMERCIAL_INVOICE,
    CUSTOM_PACKAGE_DOCUMENT,
    CUSTOM_SHIPMENT_DOCUMENT,
    DANGEROUS_GOODS_SHIPPERS_DECLARATION,
    DELIVERY_ON_INVOICE_ACCEPTANCE_RETURN_2_D_BARCODE,
    DELIVERY_ON_INVOICE_ACCEPTANCE_RETURN_LABEL,
    ETD_LABEL,
    EXPORT_DECLARATION,
    FREIGHT_ADDRESS_LABEL,
    GENERAL_AGENCY_AGREEMENT,
    GROUND_BARCODE,
    NAFTA_CERTIFICATE_OF_ORIGIN,
    OP_900,
    OUTBOUND_2_D_BARCODE,
    OUTBOUND_LABEL,
    PRO_FORMA_INVOICE,
    RECIPIENT_ADDRESS_BARCODE,
    RECIPIENT_POSTAL_BARCODE,
    RETURN_INSTRUCTIONS,
    TERMS_AND_CONDITIONS,
    USPS_BARCODE;

    public String value() {
        return name();
    }

    public static ReturnedShippingDocumentType fromValue(String v) {
        return valueOf(v);
    }

}
