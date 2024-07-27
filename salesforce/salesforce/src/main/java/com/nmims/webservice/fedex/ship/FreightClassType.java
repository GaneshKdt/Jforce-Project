
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FreightClassType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FreightClassType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CLASS_050"/>
 *     &lt;enumeration value="CLASS_055"/>
 *     &lt;enumeration value="CLASS_060"/>
 *     &lt;enumeration value="CLASS_065"/>
 *     &lt;enumeration value="CLASS_070"/>
 *     &lt;enumeration value="CLASS_077_5"/>
 *     &lt;enumeration value="CLASS_085"/>
 *     &lt;enumeration value="CLASS_092_5"/>
 *     &lt;enumeration value="CLASS_100"/>
 *     &lt;enumeration value="CLASS_110"/>
 *     &lt;enumeration value="CLASS_125"/>
 *     &lt;enumeration value="CLASS_150"/>
 *     &lt;enumeration value="CLASS_175"/>
 *     &lt;enumeration value="CLASS_200"/>
 *     &lt;enumeration value="CLASS_250"/>
 *     &lt;enumeration value="CLASS_300"/>
 *     &lt;enumeration value="CLASS_400"/>
 *     &lt;enumeration value="CLASS_500"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FreightClassType")
@XmlEnum
public enum FreightClassType {

    CLASS_050,
    CLASS_055,
    CLASS_060,
    CLASS_065,
    CLASS_070,
    CLASS_077_5,
    CLASS_085,
    CLASS_092_5,
    CLASS_100,
    CLASS_110,
    CLASS_125,
    CLASS_150,
    CLASS_175,
    CLASS_200,
    CLASS_250,
    CLASS_300,
    CLASS_400,
    CLASS_500;

    public String value() {
        return name();
    }

    public static FreightClassType fromValue(String v) {
        return valueOf(v);
    }

}
