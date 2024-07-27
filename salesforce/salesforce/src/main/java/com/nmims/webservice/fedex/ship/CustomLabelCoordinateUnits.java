
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomLabelCoordinateUnits.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CustomLabelCoordinateUnits">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MILS"/>
 *     &lt;enumeration value="PIXELS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CustomLabelCoordinateUnits")
@XmlEnum
public enum CustomLabelCoordinateUnits {

    MILS,
    PIXELS;

    public String value() {
        return name();
    }

    public static CustomLabelCoordinateUnits fromValue(String v) {
        return valueOf(v);
    }

}
