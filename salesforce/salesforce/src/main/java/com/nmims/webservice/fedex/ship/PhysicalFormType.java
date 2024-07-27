
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PhysicalFormType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PhysicalFormType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GAS"/>
 *     &lt;enumeration value="LIQUID"/>
 *     &lt;enumeration value="SOLID"/>
 *     &lt;enumeration value="SPECIAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PhysicalFormType")
@XmlEnum
public enum PhysicalFormType {

    GAS,
    LIQUID,
    SOLID,
    SPECIAL;

    public String value() {
        return name();
    }

    public static PhysicalFormType fromValue(String v) {
        return valueOf(v);
    }

}
