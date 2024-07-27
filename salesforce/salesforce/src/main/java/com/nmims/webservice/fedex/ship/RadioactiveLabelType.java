
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RadioactiveLabelType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RadioactiveLabelType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="III_YELLOW"/>
 *     &lt;enumeration value="II_YELLOW"/>
 *     &lt;enumeration value="I_WHITE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RadioactiveLabelType")
@XmlEnum
public enum RadioactiveLabelType {

    III_YELLOW,
    II_YELLOW,
    I_WHITE;

    public String value() {
        return name();
    }

    public static RadioactiveLabelType fromValue(String v) {
        return valueOf(v);
    }

}
