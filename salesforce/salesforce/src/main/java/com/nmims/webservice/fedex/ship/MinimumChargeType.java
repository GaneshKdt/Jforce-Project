
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MinimumChargeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MinimumChargeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CUSTOMER"/>
 *     &lt;enumeration value="CUSTOMER_FREIGHT_WEIGHT"/>
 *     &lt;enumeration value="EARNED_DISCOUNT"/>
 *     &lt;enumeration value="MIXED"/>
 *     &lt;enumeration value="RATE_SCALE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MinimumChargeType")
@XmlEnum
public enum MinimumChargeType {

    CUSTOMER,
    CUSTOMER_FREIGHT_WEIGHT,
    EARNED_DISCOUNT,
    MIXED,
    RATE_SCALE;

    public String value() {
        return name();
    }

    public static MinimumChargeType fromValue(String v) {
        return valueOf(v);
    }

}
