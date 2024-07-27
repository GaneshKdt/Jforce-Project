
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RateDimensionalDivisorType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RateDimensionalDivisorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="COUNTRY"/>
 *     &lt;enumeration value="CUSTOMER"/>
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="PRODUCT"/>
 *     &lt;enumeration value="WAIVED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RateDimensionalDivisorType")
@XmlEnum
public enum RateDimensionalDivisorType {

    COUNTRY,
    CUSTOMER,
    OTHER,
    PRODUCT,
    WAIVED;

    public String value() {
        return name();
    }

    public static RateDimensionalDivisorType fromValue(String v) {
        return valueOf(v);
    }

}
