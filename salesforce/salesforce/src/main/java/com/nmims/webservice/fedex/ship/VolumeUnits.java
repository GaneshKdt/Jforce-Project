
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VolumeUnits.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VolumeUnits">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CUBIC_FT"/>
 *     &lt;enumeration value="CUBIC_M"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "VolumeUnits")
@XmlEnum
public enum VolumeUnits {

    CUBIC_FT,
    CUBIC_M;

    public String value() {
        return name();
    }

    public static VolumeUnits fromValue(String v) {
        return valueOf(v);
    }

}
