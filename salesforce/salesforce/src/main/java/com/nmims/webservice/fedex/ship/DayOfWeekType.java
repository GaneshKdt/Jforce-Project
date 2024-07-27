
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DayOfWeekType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DayOfWeekType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FRI"/>
 *     &lt;enumeration value="MON"/>
 *     &lt;enumeration value="SAT"/>
 *     &lt;enumeration value="SUN"/>
 *     &lt;enumeration value="THU"/>
 *     &lt;enumeration value="TUE"/>
 *     &lt;enumeration value="WED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DayOfWeekType")
@XmlEnum
public enum DayOfWeekType {

    FRI,
    MON,
    SAT,
    SUN,
    THU,
    TUE,
    WED;

    public String value() {
        return name();
    }

    public static DayOfWeekType fromValue(String v) {
        return valueOf(v);
    }

}
