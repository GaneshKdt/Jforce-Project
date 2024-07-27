
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ShippingDocumentGroupingType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ShippingDocumentGroupingType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CONSOLIDATED_BY_DOCUMENT_TYPE"/>
 *     &lt;enumeration value="INDIVIDUAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ShippingDocumentGroupingType")
@XmlEnum
public enum ShippingDocumentGroupingType {

    CONSOLIDATED_BY_DOCUMENT_TYPE,
    INDIVIDUAL;

    public String value() {
        return name();
    }

    public static ShippingDocumentGroupingType fromValue(String v) {
        return valueOf(v);
    }

}
