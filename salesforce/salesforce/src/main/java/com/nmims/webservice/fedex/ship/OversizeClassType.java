
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OversizeClassType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OversizeClassType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OVERSIZE_1"/>
 *     &lt;enumeration value="OVERSIZE_2"/>
 *     &lt;enumeration value="OVERSIZE_3"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "OversizeClassType")
@XmlEnum
public enum OversizeClassType {

    OVERSIZE_1,
    OVERSIZE_2,
    OVERSIZE_3;

    public String value() {
        return name();
    }

    public static OversizeClassType fromValue(String v) {
        return valueOf(v);
    }

}
