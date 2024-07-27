
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FreightGuaranteeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FreightGuaranteeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GUARANTEED_DATE"/>
 *     &lt;enumeration value="GUARANTEED_MORNING"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FreightGuaranteeType")
@XmlEnum
public enum FreightGuaranteeType {

    GUARANTEED_DATE,
    GUARANTEED_MORNING;

    public String value() {
        return name();
    }

    public static FreightGuaranteeType fromValue(String v) {
        return valueOf(v);
    }

}
