
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PickupRequestSourceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PickupRequestSourceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AUTOMATION"/>
 *     &lt;enumeration value="CUSTOMER_SERVICE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PickupRequestSourceType")
@XmlEnum
public enum PickupRequestSourceType {

    AUTOMATION,
    CUSTOMER_SERVICE;

    public String value() {
        return name();
    }

    public static PickupRequestSourceType fromValue(String v) {
        return valueOf(v);
    }

}
