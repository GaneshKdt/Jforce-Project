
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RateDiscountType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RateDiscountType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BONUS"/>
 *     &lt;enumeration value="COUPON"/>
 *     &lt;enumeration value="EARNED"/>
 *     &lt;enumeration value="INCENTIVE"/>
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="VOLUME"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RateDiscountType")
@XmlEnum
public enum RateDiscountType {

    BONUS,
    COUPON,
    EARNED,
    INCENTIVE,
    OTHER,
    VOLUME;

    public String value() {
        return name();
    }

    public static RateDiscountType fromValue(String v) {
        return valueOf(v);
    }

}
