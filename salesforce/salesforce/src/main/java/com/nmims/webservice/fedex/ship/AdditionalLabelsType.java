
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AdditionalLabelsType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AdditionalLabelsType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BROKER"/>
 *     &lt;enumeration value="CONSIGNEE"/>
 *     &lt;enumeration value="CUSTOMS"/>
 *     &lt;enumeration value="DESTINATION"/>
 *     &lt;enumeration value="FREIGHT_REFERENCE"/>
 *     &lt;enumeration value="MANIFEST"/>
 *     &lt;enumeration value="ORIGIN"/>
 *     &lt;enumeration value="RECIPIENT"/>
 *     &lt;enumeration value="SHIPPER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AdditionalLabelsType")
@XmlEnum
public enum AdditionalLabelsType {

    BROKER,
    CONSIGNEE,
    CUSTOMS,
    DESTINATION,
    FREIGHT_REFERENCE,
    MANIFEST,
    ORIGIN,
    RECIPIENT,
    SHIPPER;

    public String value() {
        return name();
    }

    public static AdditionalLabelsType fromValue(String v) {
        return valueOf(v);
    }

}
