
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RateElementBasisType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RateElementBasisType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BASE_CHARGE"/>
 *     &lt;enumeration value="NET_CHARGE"/>
 *     &lt;enumeration value="NET_CHARGE_EXCLUDING_TAXES"/>
 *     &lt;enumeration value="NET_FREIGHT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RateElementBasisType")
@XmlEnum
public enum RateElementBasisType {

    BASE_CHARGE,
    NET_CHARGE,
    NET_CHARGE_EXCLUDING_TAXES,
    NET_FREIGHT;

    public String value() {
        return name();
    }

    public static RateElementBasisType fromValue(String v) {
        return valueOf(v);
    }

}
