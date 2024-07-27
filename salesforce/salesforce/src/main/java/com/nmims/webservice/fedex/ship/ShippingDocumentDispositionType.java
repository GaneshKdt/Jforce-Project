
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ShippingDocumentDispositionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ShippingDocumentDispositionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CONFIRMED"/>
 *     &lt;enumeration value="DEFERRED_RETURNED"/>
 *     &lt;enumeration value="DEFERRED_STORED"/>
 *     &lt;enumeration value="EMAILED"/>
 *     &lt;enumeration value="QUEUED"/>
 *     &lt;enumeration value="RETURNED"/>
 *     &lt;enumeration value="STORED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ShippingDocumentDispositionType")
@XmlEnum
public enum ShippingDocumentDispositionType {

    CONFIRMED,
    DEFERRED_RETURNED,
    DEFERRED_STORED,
    EMAILED,
    QUEUED,
    RETURNED,
    STORED;

    public String value() {
        return name();
    }

    public static ShippingDocumentDispositionType fromValue(String v) {
        return valueOf(v);
    }

}
