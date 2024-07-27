
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RotationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RotationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LEFT"/>
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="RIGHT"/>
 *     &lt;enumeration value="UPSIDE_DOWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RotationType")
@XmlEnum
public enum RotationType {

    LEFT,
    NONE,
    RIGHT,
    UPSIDE_DOWN;

    public String value() {
        return name();
    }

    public static RotationType fromValue(String v) {
        return valueOf(v);
    }

}
