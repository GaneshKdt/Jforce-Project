
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FreightChargeBasisType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FreightChargeBasisType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CWT"/>
 *     &lt;enumeration value="FLAT"/>
 *     &lt;enumeration value="MINIMUM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FreightChargeBasisType")
@XmlEnum
public enum FreightChargeBasisType {

    CWT,
    FLAT,
    MINIMUM;

    public String value() {
        return name();
    }

    public static FreightChargeBasisType fromValue(String v) {
        return valueOf(v);
    }

}
