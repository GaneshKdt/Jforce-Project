
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EdtTaxType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EdtTaxType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADDITIONAL_TAXES"/>
 *     &lt;enumeration value="CONSULAR_INVOICE_FEE"/>
 *     &lt;enumeration value="CUSTOMS_SURCHARGES"/>
 *     &lt;enumeration value="DUTY"/>
 *     &lt;enumeration value="EXCISE_TAX"/>
 *     &lt;enumeration value="FOREIGN_EXCHANGE_TAX"/>
 *     &lt;enumeration value="GENERAL_SALES_TAX"/>
 *     &lt;enumeration value="IMPORT_LICENSE_FEE"/>
 *     &lt;enumeration value="INTERNAL_ADDITIONAL_TAXES"/>
 *     &lt;enumeration value="INTERNAL_SENSITIVE_PRODUCTS_TAX"/>
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="SENSITIVE_PRODUCTS_TAX"/>
 *     &lt;enumeration value="STAMP_TAX"/>
 *     &lt;enumeration value="STATISTICAL_TAX"/>
 *     &lt;enumeration value="TRANSPORT_FACILITIES_TAX"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EdtTaxType")
@XmlEnum
public enum EdtTaxType {

    ADDITIONAL_TAXES,
    CONSULAR_INVOICE_FEE,
    CUSTOMS_SURCHARGES,
    DUTY,
    EXCISE_TAX,
    FOREIGN_EXCHANGE_TAX,
    GENERAL_SALES_TAX,
    IMPORT_LICENSE_FEE,
    INTERNAL_ADDITIONAL_TAXES,
    INTERNAL_SENSITIVE_PRODUCTS_TAX,
    OTHER,
    SENSITIVE_PRODUCTS_TAX,
    STAMP_TAX,
    STATISTICAL_TAX,
    TRANSPORT_FACILITIES_TAX;

    public String value() {
        return name();
    }

    public static EdtTaxType fromValue(String v) {
        return valueOf(v);
    }

}
