
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomDeliveryWindowType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CustomDeliveryWindowType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AFTER"/>
 *     &lt;enumeration value="BEFORE"/>
 *     &lt;enumeration value="BETWEEN"/>
 *     &lt;enumeration value="ON"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CustomDeliveryWindowType")
@XmlEnum
public enum CustomDeliveryWindowType {

    AFTER,
    BEFORE,
    BETWEEN,
    ON;

    public String value() {
        return name();
    }

    public static CustomDeliveryWindowType fromValue(String v) {
        return valueOf(v);
    }

}
