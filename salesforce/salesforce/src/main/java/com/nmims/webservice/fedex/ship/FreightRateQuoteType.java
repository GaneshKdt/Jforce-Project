
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FreightRateQuoteType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FreightRateQuoteType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AUTOMATED"/>
 *     &lt;enumeration value="MANUAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FreightRateQuoteType")
@XmlEnum
public enum FreightRateQuoteType {

    AUTOMATED,
    MANUAL;

    public String value() {
        return name();
    }

    public static FreightRateQuoteType fromValue(String v) {
        return valueOf(v);
    }

}
