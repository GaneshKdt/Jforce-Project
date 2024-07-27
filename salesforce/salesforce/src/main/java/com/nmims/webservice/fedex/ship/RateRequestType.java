
package com.nmims.webservice.fedex.ship;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RateRequestType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RateRequestType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LIST"/>
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="PREFERRED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RateRequestType")
@XmlEnum
public enum RateRequestType {

    LIST,
    NONE,
    PREFERRED;

    public String value() {
        return name();
    }

    public static RateRequestType fromValue(String v) {
        return valueOf(v);
    }

}
