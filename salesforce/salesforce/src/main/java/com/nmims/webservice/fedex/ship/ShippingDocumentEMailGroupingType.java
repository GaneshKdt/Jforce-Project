
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ShippingDocumentEMailGroupingType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ShippingDocumentEMailGroupingType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BY_RECIPIENT"/>
 *     &lt;enumeration value="NONE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ShippingDocumentEMailGroupingType")
@XmlEnum
public enum ShippingDocumentEMailGroupingType {

    BY_RECIPIENT,
    NONE;

    public String value() {
        return name();
    }

    public static ShippingDocumentEMailGroupingType fromValue(String v) {
        return valueOf(v);
    }

}
