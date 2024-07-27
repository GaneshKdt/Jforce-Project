
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PickupRequestType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PickupRequestType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FUTURE_DAY"/>
 *     &lt;enumeration value="SAME_DAY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PickupRequestType")
@XmlEnum
public enum PickupRequestType {

    FUTURE_DAY,
    SAME_DAY;

    public String value() {
        return name();
    }

    public static PickupRequestType fromValue(String v) {
        return valueOf(v);
    }

}
