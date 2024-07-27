
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RebateType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RebateType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BONUS"/>
 *     &lt;enumeration value="EARNED"/>
 *     &lt;enumeration value="OTHER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RebateType")
@XmlEnum
public enum RebateType {

    BONUS,
    EARNED,
    OTHER;

    public String value() {
        return name();
    }

    public static RebateType fromValue(String v) {
        return valueOf(v);
    }

}
